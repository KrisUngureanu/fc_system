package kz.tamur.rt.adapters;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrHyperLabel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.OrCellRenderer;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;


public class HyperLabelAdapter extends ComponentAdapter
        implements ActionListener {

    private OrHyperLabel hlabel;
    private KrnObject _ifc, dynIfc;
    private OrRef dynIfcRef;
    private boolean editIfc = false;
    
    private ASTStart beforeOpenAction;
    private ASTStart dynamicIfcExprTemplate;

    private OrHiperCellRenderer renderer;
    private HiperLabelCellEditor cellEditor = new HiperLabelCellEditor();
    public HyperLabelAdapter(UIFrame frame, OrHyperLabel hlabel, boolean isEditor)
            throws KrnException {
        super(frame, hlabel, isEditor);
        Kernel krn = Kernel.instance();
        PropertyNode proot = hlabel.getProperties();
        PropertyValue pv = hlabel.getPropertyValue(proot.getChild("pov").getChild("interface"));
        if (!pv.isNull()) {
            KrnObject oo = new KrnObject(Long.parseLong(pv.getKrnObjectId()), "", krn.getClassByName(pv.getKrnClassName()).id);
            _ifc = oo;
        }

        // Поведение
        PropertyNode behavNode = proot.getChild("pov");
        // Действие перед открытием интерфейса
    	pv = hlabel.getPropertyValue(behavNode.getChild("beforeOpen"));
    	String expr = pv.isNull() ? "" : pv.stringValue();
    	if (expr.length() > 0) {
            beforeOpenAction = OrLang.createStaticTemplate(expr);
    	}
        // Динамический интерфейс (Ref)
        pv = hlabel.getPropertyValue(behavNode.getChild("dynamicIfc"));
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Динамический интерфейс";
                dynIfcRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
                dynIfcRef.addOrRefListener(this);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        pv = hlabel.getPropertyValue(behavNode.getChild("dynamicIfc_expr"));
        String dynIfcExpr = null;
        if (!pv.isNull()) {
            dynIfcExpr = pv.stringValue();
        }
        if (dynIfcExpr != null && dynIfcExpr.length() > 0) {
            dynamicIfcExprTemplate = OrLang.createStaticTemplate(dynIfcExpr);
            try {
                Editor e = new Editor(dynIfcExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pv = hlabel.getPropertyValue(proot.getChild("pov").getChild("editIfc"));
        if (!pv.isNull()) {
            editIfc = pv.booleanValue();
        } else {
            editIfc = ((Boolean)proot.getChild("pov").getChild("editIfc").getDefaultValue()).booleanValue();
        }

        this.hlabel = hlabel;
        this.hlabel.addActionListener(this);
        kz.tamur.rt.Utils.setComponentTabFocusCircle(this.hlabel);
        this.hlabel.setXml(null);
    }

    public void clear() {}

    //ActionListener
    public void actionPerformed(ActionEvent e) {
        if (hlabel.isHelpClick()) {
            hlabel.setHelpClick(false);
        } else {
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            KrnObject[] objs = null;
            if (dataRef != null) {
                if (dataRef.isArray() && !dataRef.isInOrTable()) {
                    java.util.List items = dataRef.getItems(langId);
                    objs = new KrnObject[items.size()];
                    for (int i = 0; i < items.size(); i++) {
                        OrRef.Item item = (OrRef.Item) items.get(i);
                        objs[i] = (KrnObject) item.getCurrent();
                    }
                } else {
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item != null && item.getCurrent() != null)
                        objs = new KrnObject[]{(KrnObject) item.getCurrent()};
                }
            }
            if (mgr != null) {
                try {
                    String path = (dataRef != null) ? dataRef.toString() : "";
                    if (dynIfcRef != null) {
                        OrRef.Item item  = dynIfcRef.getItem(langId);
                        dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                    } else if (dynamicIfcExprTemplate != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        try {
                            orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                            Object res = vc.get("RETURN");
                            if (res != null && res instanceof KrnObject) {
                                dynIfc = (KrnObject)res;
                            }
                        } catch (Exception ex) {
                            Util.showErrorMessage(hlabel, ex.getMessage(),
                                    "Динамический интерфейс (Выражение)");
                        }
                    }
                    int mode = editIfc ? InterfaceManager.SERVICE_MODE : InterfaceManager.READONLY_MODE;
                    UIFrame frameUI = mgr.getCurrentInterface();
                    long tid = frameUI.getCash().getTransactionId();
                    if (_ifc != null) {
                    	doBeforeOpen();
                        if (frameUI.getRef() == null) {
                            mode = frameUI.getEvaluationMode();
                        }
                        mgr.absolute(_ifc, objs, path, mode, false, tid, 0, hlabel.isBlockErrors(),"");
                    } else if (dynIfc != null) {
                    	doBeforeOpen();
                        if (frameUI.getRef() == null) {
                            mode = frameUI.getEvaluationMode();
                        }
                        mgr.absolute(dynIfc, objs, path, mode, false, tid, 0, hlabel.isBlockErrors(),"");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void setDynIfcRef(OrRef dynIfcRef) {
        this.dynIfcRef = dynIfcRef;
        //this.dynIfcRef.addOrRefListener(this);
    }
    
    private void doBeforeOpen() throws Exception {
    	if (beforeOpenAction != null) {
	    	ClientOrLang lng = new ClientOrLang(frame);
	    	Map<String, Object> vars = new HashMap<String, Object>();
	    	Stack<String> callStack = new Stack<String>();
			lng.evaluate(beforeOpenAction, vars, this, callStack);
    	}
    }

    public OrRef getDynamicInterfaceRef() {
        return dynIfcRef;
    }

    static class OrHiperCellRenderer extends OrCellRenderer {
		private static final JLabel comp = new JLabel();
		static {
			comp.setOpaque(true);
			String iconName = MainFrame.iconsSettings.get("iconHyperColumn");
        	ImageIcon icon = kz.tamur.rt.Utils.getImageIconFull(iconName);
			if (icon == null) {
				comp.setIcon(kz.tamur.rt.Utils.getImageIcon("VSlider"));
			} else {
				comp.setIcon(icon);
			}
            comp.setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			return comp;
		}
	}

    public TableCellRenderer getCellRenderer() {
    	if (renderer == null) {
    		renderer = new OrHiperCellRenderer();
    	}
		return renderer;
	}

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        hlabel.setEnabled(isEnabled);
    }

    public OrCellEditor getCellEditor() {
        if (cellEditor == null) {
            cellEditor = new HiperLabelCellEditor();
            hlabel.addActionListener(cellEditor);
        }
        return cellEditor;
    }

    class HiperLabelCellEditor extends OrCellEditor {
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valueChanged(new OrRefEvent(dataRef, 0, -1, null));
            hlabel.setText("");
            return hlabel;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Object getValueFor(Object obj) {
            return null;
        }
    }
}
