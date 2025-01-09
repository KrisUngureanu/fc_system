package kz.tamur.rt.adapters;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CLEAR;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrHyperPopup;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.guidesigner.DialogEventHandler;
import kz.tamur.guidesigner.FrameEventHandler;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.InterfaceFrame;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.OrCellRenderer;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.SortedFrame;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.util.expr.Editor;

public class HyperPopupAdapter extends ComponentAdapter implements ActionListener, DialogEventHandler, FrameEventHandler {

    private DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private DecimalFormat dformat = null;
    private OrHyperPopup hpopup;
    private boolean selfChange = false;
    private KrnObject _ifc, dynIfc;
    private OrRef dynIfcRef;
    private String _ifcTitle, contentPath;
    private OrRef contentRef;
    private ASTStart contentExpr;
    private OrRef selectedRef;
    private OrRef autoCreateRef;
    private int refreshMode;
    private int cash;
    private boolean fork;
    private ASTStart beforeOpenAction, afterTemplate, beforTemplate, beforeModificationTemplate, dynamicIfcExprTemplate;
    private OrPopupCellRenderer renderer;
    private String selectedRefPath;
    private OrRef titleRef;
    private int[] selRows;
    private int actionFlag;
    private boolean copyFlag;
    private boolean ifcLock = false;
    private HiperPopupCellEditor cellEditor;
    private ImageIcon hpcImage = kz.tamur.rt.Utils.getImageIcon("HyperPopCol");
    private boolean showIcon = true;
    private ActionLoader sw;

    private UIFrame frm;
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    public HyperPopupAdapter(UIFrame frame, OrHyperPopup hpopup, boolean isEditor) throws KrnException {
        super(frame, hpopup, isEditor);
        Kernel krn = Kernel.instance();
        PropertyNode proot = hpopup.getProperties();
        PropertyValue pv = hpopup.getPropertyValue(proot.getChild("ref").getChild("refreshMode"));
        if (!pv.isNull()) {
            refreshMode = pv.intValue();
        }
        PropertyNode pn = proot.getChild("pov");
        pv = hpopup.getPropertyValue(pn.getChild("cashFlag"));
        if (!pv.isNull()) {
            cash = pv.intValue();
        }
        pv = hpopup.getPropertyValue(pn.getChild("fork"));
        if (!pv.isNull()) {
            fork = pv.booleanValue();
        }
        PropertyNode prop = proot.getChild("ref").getChild("titlePath");
        pv = hpopup.getPropertyValue(prop);
        String titlePath = null;
        if (!pv.isNull()) {
            titlePath = pv.stringValue();
            if (isEditor()) {
                titleRef = OrRef
                        .createRef(titlePath, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            } else {
                titleRef = OrRef.createRef(titlePath, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                        frame);
            }
            titleRef.addOrRefListener(this);
        }

        renderer = new OrPopupCellRenderer();
        PropertyNode rprop = proot.getChild("ref").getChild("content");
        pv = hpopup.getPropertyValue(rprop);
        if (!pv.isNull()) {
            contentPath = pv.stringValue();
            if (!contentPath.equals("")) {
                long contentFilterId = 0;
                pv = hpopup.getPropertyValue(proot.getChild("ref").getChild("contentFilter"));
                if (!pv.isNull()) {
                    contentFilterId = pv.filterValue().getObjId();
                }

                if (refreshMode == Constants.RM_DIRECTLY) {
                    contentRef = OrRef.createRef(contentPath, false, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame);
                } else {
                    contentRef = OrRef.createContentRef(contentPath, contentFilterId, refreshMode, Mode.RUNTIME,
                            frame.getTransactionIsolation(), true, frame);
                }

                if (contentFilterId > 0)
                    contentRef.setDefaultFilter(contentFilterId);

                contentRef.addOrRefListener(this);
            }
        }
        rprop = proot.getChild("ref").getChild("selectedRef");
        pv = hpopup.getPropertyValue(rprop);
        if (!pv.isNull()) {
            selectedRefPath = pv.stringValue();
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("callDialog"));
        if (!pv.isNull()) {
            KrnObject oo = new KrnObject(Long.parseLong(pv.getKrnObjectId()), "", krn.getClassByName(pv.getKrnClassName()).id);
            _ifc = oo;
            _ifcTitle = pv.getTitle();
        }
        this.hpopup = hpopup;
        this.hpopup.addActionListener(this);
        if (!isEditor) {
            kz.tamur.rt.Utils.setComponentTabFocusCircle(this.hpopup);
        }
        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobAfter"));
        String expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue();
        }
        if (expr != null && expr.length() > 0) {
            afterTemplate = OrLang.createStaticTemplate(expr);
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobBefore"));
        expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue();
        }
        if (expr != null && expr.length() > 0) {
            beforeModificationTemplate = OrLang.createStaticTemplate(expr);
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("dynamicIfcExpr"));
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
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobBeforClear"));
        String beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue();
        }
        if (beforExpr != null && beforExpr.length() > 0) {
            beforTemplate = OrLang.createStaticTemplate(beforExpr);
            try {
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("charModification"));
        if (!pv.isNull()) {
            actionFlag = pv.intValue();
        }

        pn = proot.getChild("pov").getChild("ifcLock");
        pv = hpopup.getPropertyValue(pn);
        if (!pv.isNull()) {
            ifcLock = pv.booleanValue();
        }
        // Действие перед открытием интерфейса
        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("beforeOpen"));
        String beforOpenExpr = null;
        if (!pv.isNull()) {
            beforOpenExpr = pv.stringValue();
        }
        if (beforOpenExpr != null && beforOpenExpr.length() > 0) {
            beforeOpenAction = OrLang.createStaticTemplate(beforOpenExpr);
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("dynamicIfc"));
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

        pn = proot.getChild("constraints").getChild("formatPattern");
        pv = hpopup.getPropertyValue(pn);
        String pattern = "";
        if (!pv.isNull()) {
            pattern = pv.stringValue();
        } else {
            pattern = pn.getDefaultValue().toString();
        }
        if (pattern != null && pattern.length() > 0) {
            dformat = new DecimalFormat(pattern);
            dformat.setGroupingUsed(true);
            dformat.setGroupingSize(3);
            DecimalFormatSymbols dfs = dformat.getDecimalFormatSymbols();
            dfs.setGroupingSeparator(' ');
            dfs.setDecimalSeparator(',');
            dformat.setDecimalFormatSymbols(dfs);
        }

        if (this.hpopup.isClearBtnExists()) {
            this.hpopup.addDeleteMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (beforTemplate != null) {
                        ClientOrLang orlang = new ClientOrLang(HyperPopupAdapter.this.frame);
                        Map vc = new HashMap();
                        try {
                            boolean calcOwner = OrCalcRef.setCalculations();
                            orlang.evaluate(beforTemplate, vc, HyperPopupAdapter.this, new Stack<String>());
                            if (calcOwner)
                                OrCalcRef.makeCalculations();
                        } catch (Exception ex) {
                            Util.showErrorMessage((OrHyperPopup) e.getSource(), ex.getMessage(), "Действие перед вставкой");
                        }
                    }
                    boolean calcOwner = OrCalcRef.setCalculations();
                    if (dataRef != null) {
                        OrRef.Item item = dataRef.getItem(langId);
                        if (item != null) {
                            dataRef.deleteItem(HyperPopupAdapter.this, HyperPopupAdapter.this);
                        }
                    }
                    updateParamFilters(null);
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                }
            });
        }
        if (isEditor) {
            cellEditor = new HiperPopupCellEditor();
        }
        
        //* Действие при открытии HyperPopup *\\
        PropertyNode pPopupExpr = proot.getChild("ref").getChild("contentCalc");
        pv = hpopup.getPropertyValue(pPopupExpr);
        String contentPopupExpr;
        if (!pv.isNull()) {
            contentPopupExpr = pv.stringValue();          
            
            if (contentPopupExpr != null && contentPopupExpr.length() > 0) {
                propertyName = "Свойство: Содержимое формула";
                contentExpr = OrLang.createStaticTemplate(contentPopupExpr);                
                try {
                    Editor e = new Editor(contentPopupExpr);
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
        }
        
        this.hpopup.setXml(null);
    }

    public void clear() {}

    private void doBeforeOpen() throws Exception {
        if (beforeOpenAction != null) {
            ClientOrLang lng = new ClientOrLang(frame);
            Map<String, Object> vars = new HashMap<String, Object>();
            Stack<String> callStack = new Stack<String>();
            lng.evaluate(beforeOpenAction, vars, this, callStack);
        }
    }

    public void actionPerformed(ActionEvent e) {
        CursorToolkit.startWaitCursor(hpopup);
        if (sw == null) {
            try {
                doBeforeOpen();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            sw = new ActionLoader(e, (RootPaneContainer) hpopup.getTopLevelAncestor());
            sw.start();
        }
    }

    public void actionPerformed2(ActionEvent e) {
        if (hpopup.isHelpClick()) {
            hpopup.setHelpClick(false);
        } else {
            int index = 0;
            try {
                kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
                // KrnObject[] objs = null;
                // ArrayList objs = null;
                List<KrnObject> objs = null;
                boolean hasFilters = false;
                if (contentRef != null) {
                    if (refreshMode != Constants.RM_DIRECTLY) {
                        contentRef.getRoot().evaluate(hpopup);
                    }
                    hasFilters = contentRef.hasFilters();
//                    if (refreshMode == Constants.RM_DIRECTLY && (dataRef == null || dataRef.getRoot() != contentRef.getRoot())) {
//                    	contentRef.getRoot().evaluate(hpopup);
//                    }
                    
                    // objs = new ArrayList();
                    objs = new ArrayList<KrnObject>();
                    if (contentRef.isArray() && !contentRef.isInOrTable() && !contentRef.isColumn()) {
                        List<Item> items = contentRef.getItems(langId);
                        index = contentRef.getIndex();
                        for (int i = 0; i < items.size(); ++i) {
                            KrnObject obj = (KrnObject) ((OrRef.Item) items.get(i)).getCurrent();
                            if (obj != null) {
                                objs.add(obj);
                            }
                        }
                    } else {
                        OrRef.Item item = contentRef.getItem(langId);
                        if (item != null) {
                            if (item.getCurrent() != null) {
                                objs.add((KrnObject)item.getCurrent());
                            }
                        }
                    }
                }
                
                if (contentExpr != null) {              
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        orlang.evaluate(contentExpr, vc, this, new Stack<String>());
                        objs = (List<KrnObject>)vc.get("RETURN");                        
                    } catch (Exception ex) {
                        Util.showErrorMessage(hpopup, ex.getMessage(), "Данные.Содержимое формула");
                    } finally {
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    }                    
                }
                
                if (mgr != null) {
                    long tr_id = mgr.getCash().getTransactionId();
                    KrnObject[] os = null;
                    if (objs != null) {
                        os = (KrnObject[]) objs.toArray(new KrnObject[objs.size()]);
                    }
                    frm = null;
                    if (_ifc != null) {
                        if (fork) {
                            showFrame(_ifc, os, mgr);
                        }
                        frm = mgr.getInterfacePanel(_ifc, os, tr_id, frame.getEvaluationMode(), (cash & 0x01) > 0, fork);
                    } else if (dynIfcRef != null) {
                        OrRef.Item item = dynIfcRef.getItem(langId);
                        dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                        if (dynIfc != null) {
                            frm = mgr.getInterfacePanel(dynIfc, os, tr_id, frame.getEvaluationMode(), (cash & 0x01) > 0, fork);
                        } else if (isEditor()) {
                            cellEditor.stopCellEditing();
                        }
                    } else if (dynamicIfcExprTemplate != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map vc = new HashMap();
                        try {
                            orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                            Object res = vc.get("RETURN");
                            if (res != null && res instanceof KrnObject) {
                                dynIfc = (KrnObject) res;
                                frm = mgr
                                        .getInterfacePanel(dynIfc, os, tr_id, frame.getEvaluationMode(), (cash & 0x01) > 0, fork);
                            }
                        } catch (Exception ex) {
                            Util.showErrorMessage(hpopup, ex.getMessage(), "Динамический интерфейс (Выражение)");
                        }
                    }
                    if (frm == null) {
                        MessagesFactory.showMessageDialog(hpopup.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Не задан интерфейс обработки!");
                        return;
                    }
                    if (selectedRefPath != null && selectedRefPath.length() > 0) {
                        if (selectedRef == null) {
                            selectedRef = OrRef.createRef(selectedRefPath, false, Mode.RUNTIME, frm.getRefs(), OrRef.TR_CLEAR,
                                    frm);
                        }
                    }
                    frm.getRef().setHasFilters(hasFilters);
                    // frm.getRef().fireValueChangedEvent(-1, this, 0);
                    // if (index > 0) {
                    // frm.getRef().absolute(index, this);
                    // }
                    int mode = frm.getEvaluationMode();
                    PanelAdapter pa = frm.getPanelAdapter();
                    OrPanel p = (OrPanel) frm.getPanel();
                    String title = p.getTitle();
                    // Определить то, как должен отображаться интерфейс
                    DesignerDialog dsgDialog = null;
                    DesignerModalFrame dsgFrame = null;
                    if (hpopup.getTypeView() == Constants.DIALOG) {
                        dsgDialog = Utils.getDesignerDialog(hpopup.getTopLevelAncestor(), title, p, false);
                        dsgDialog.setLanguage(frm.getInterfaceLang().id);
                        dsgDialog.setInitiator(hpopup);
                    } else {
                        dsgFrame = Utils.getDesignerModalFrame(hpopup.getTopLevelAncestor(), title, p, false);
                        dsgFrame.setLanguage(frm.getInterfaceLang().id);
                        dsgFrame.setInitiator(hpopup);
                    }

                    // frm.getRef().absolute(index, hpopup);
                    boolean ifcEnabled = !ifcLock;
                    if (ifcEnabled) {
                        ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.ARCH_RO_MODE);
                    }
                    if (ifcEnabled) {
                        ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.READONLY_MODE);
                    }
                    pa.setEnabled(ifcEnabled);
                    Dimension pSize = p.getPrefSize();
                    if (pSize == null) {
                        pSize = new Dimension(Utils.getMaxWindowSizeActDisplay());
                    }

                    if (dsgDialog == null) {
                        dsgFrame.setFirstRow(frm);
                        dsgFrame.setSize(pSize);
                        dsgFrame.setLocation(Utils.getCenterLocationPoint(dsgFrame.getSize()));
                        dsgFrame.setDialogEventHandler(this);
                        dsgFrame.setVisible(true);
                    } else {
                        dsgDialog.setFirstRow(frm);
                        dsgDialog.setSize(pSize);
                        dsgDialog.setLocation(Utils.getCenterLocationPoint(dsgDialog.getSize()));
                        dsgDialog.setDialogEventHandler(this);
                        dsgDialog.show();
                    }

                    OrGuiComponent content = frame.getPanel();
                    if (content instanceof OrPanel) {
                        OrButton button = ((OrPanel) content).getDefaultButton();
                        if (button instanceof JButton) {
                        	Container cont = hpopup.getTopLevelAncestor();
                        	if (cont instanceof JDialog)
                            	((JDialog)cont).getRootPane().setDefaultButton((JButton) button);
                        	else
                            	((JFrame)cont).getRootPane().setDefaultButton((JButton) button);
                        	
                        	((OrPanel)content).requestFocus();
                        }
                    }
                    
                    int result = dsgDialog == null ? dsgFrame.getResult() : dsgDialog.getResult();
                    if (result != BUTTON_NOACTION && result == BUTTON_OK) {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        // Получаем список выбранных объектов
                        List<OrRef.Item> selectedItems = frm.getRef().getSelectedItems();
                        if (selectedRef != null) {
                            if (selectedRef.getSelItems() != null) {
                                selectedItems = new ArrayList<Item>(selectedRef.getSelItems());
                            }else if (selectedRef.getSelectedItems() != null) {
                                    selectedItems = new ArrayList<Item>(selectedRef.getSelectedItems());
                            } else {
                                List<OrRef.Item> subItems = new ArrayList<OrRef.Item>();
                                for (OrRef.Item item : selectedItems) {
                                    frm.getRef().absolute((KrnObject) item.getCurrent(), this);
                                    OrRef.Item it = selectedRef.getItem(langId);
                                    subItems.add(it);
                                }
                                selectedItems = subItems;
                            }
                        }

                        if ((cash & 0x01) > 0)
                            frm.getRef().fireValueChangedEvent(-1, this, 0);
                        mgr.releaseInterface(true);

                        List<Object> selectedObjects = makeSelObjList(selectedItems);
                        // Выполняем действия до модификации
                        if (beforeModificationTemplate != null) {
                            ClientOrLang orlang = new ClientOrLang(frame);
                            Map<String, Object> vc = new HashMap<String, Object>();
                            vc.put("SELOBJS", new ArrayList<Object>(selectedObjects));
                            try {
                                orlang.evaluate(beforeModificationTemplate, vc, this, new Stack<String>());
                                Object res = vc.get("RETURN");
                                if (res != null) {
                                    selectedObjects = (List<Object>) res;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед модификацией");
                            }
                        }

                        Object v = (selectedObjects.size() > 0) ? selectedObjects : null;
                        updateParamFilters(v);

                        if (dataRef != null && frm.getRef().isInOrTable()) {
                            if (selectedItems.size() == 0)
                                dataRef.fireValueChangedEvent(0, this, 0);
                            else
                                addItems(selectedObjects);
                        } else if (dataRef != null) {
                            addItems(selectedObjects);
                        }
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } else if (result == BUTTON_CLEAR) {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        mgr.releaseInterface(false);
                        if (beforTemplate != null) {
                            ClientOrLang orlang = new ClientOrLang(frame);
                            Map vc = new HashMap();
                            try {
                                orlang.evaluate(beforTemplate, vc, this, new Stack<String>());
                            } catch (Exception ex) {
                                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед вставкой");
                            }
                        }
                        updateParamFilters(null);
                        if (dataRef != null) {
                            OrRef.Item item = dataRef.getItem(langId);
                            if (item != null) {
                                dataRef.deleteItem(this, this);
                            }
                        }
                        if (calcOwner) {
                            OrCalcRef.makeCalculations();
                        }
                    } else if (result == BUTTON_CANCEL) {
                        mgr.releaseInterface(false);
                        if (isEditor()) {
                            cellEditor.cancelCellEditing();
                        }
                    }
                    if (isEditor()) {
                        cellEditor.stopCellEditing();
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void showFrame(KrnObject uiObj, KrnObject[] objs, InterfaceManager parent) throws KrnException {
        InterfaceFrame ifrm = new InterfaceFrame(uiObj, objs, parent);
        ifrm.setVisible(true);
    }

    private List<Object> makeSelObjList(List<OrRef.Item> selectedItems) {
        List<Object> res = new ArrayList<Object>();
        for (OrRef.Item item : selectedItems) {
            if (item != null) {
                res.add(item.getCurrent());
            }
        }
        return res;
    }

    private boolean contains(List<Object> values, Object value) {
        for (Object o : values) {
            if (o instanceof KrnObject) {
                if (((KrnObject) value).id == ((KrnObject) o).id) {
                    return true;
                }
            } else {
                if (value.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addItems(List<Object> values) {
        if (values.size() == 0)
            return;
        int copyTrId = dataRef.getDirtyTransactions() == OrRef.TR_CLEAR ? 0 : -1;
        kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        try {
            if (!dataRef.isColumn()) {
                List<Item> currentItems = dataRef.getItems(langId);
                for (int i = 0; i < currentItems.size(); ++i) {
                    Object obj = currentItems.get(i).getCurrent();
                    if (obj instanceof KrnObject) {
                        Funcs.remove(values, (KrnObject) obj);
                    } else {
                        values.remove(obj);
                    }
                }
            }

            if (autoCreateRef != null) {
                if (!autoCreateRef.isArray()) {
                    OrRef.Item item = autoCreateRef.getItem(langId);
                    if (item == null) {
                        autoCreateRef.insertItemHack(0, 0, null, this, this, true);
                    }
                    Object obj = values.get(0);
                    if (copyFlag && obj instanceof KrnObject) {
                        KrnObject[] krn_obj = Kernel.instance().cloneObject2(new KrnObject[] { (KrnObject) obj }, copyTrId,
                                mgr.getCash().getTransactionId());
                        obj = krn_obj[0];
                    }
                    if (dataRef.getItem(langId) != null)
                        dataRef.changeItemHack(0, obj, this, this);
                    else
                        dataRef.insertItemHack(0, 0, obj, this, this, false);
                } else {
                    for (Object value : values) {
                        if (copyFlag && value instanceof KrnObject) {
                            KrnObject[] krn_obj = Kernel.instance().cloneObject2(new KrnObject[] { (KrnObject) value }, copyTrId,
                                    mgr.getCash().getTransactionId());
                            value = krn_obj[0];
                        }
                        autoCreateRef.insertItemHack(-1, -1, null, this, this, true);
                        dataRef.insertItemHack(0, 0, value, this, this, false);
                    }
                }
                autoCreateRef.fireValueChangedEvent(-1, this, 0);
            } else if (dataRef.isInOrTable()) {
                for (Object value : values) {
                    if (copyFlag && value instanceof KrnObject) {
                        KrnObject[] krn_obj = Kernel.instance().cloneObject2(new KrnObject[] { (KrnObject) value }, copyTrId,
                                mgr.getCash().getTransactionId());
                        value = krn_obj[0];
                    }
                    dataRef.insertItemHack(-1, -1, value, this, this, false);
                }
                dataRef.fireItemsChanged(this);
            } else if (values.size() > 0) {
                if (selectedRefPath == null || actionFlag == Constants.CHANGE_ACTION) {
                    Object obj_ = values.get(0);
                    if (copyFlag && obj_ instanceof KrnObject) {
                        KrnObject[] krn_obj = Kernel.instance().cloneObject2(new KrnObject[] { (KrnObject) obj_ }, copyTrId,
                                mgr.getCash().getTransactionId());
                        obj_ = krn_obj[0];
                    }
                    if (dataRef.getItem(langId) == null) {
                        dataRef.insertItem(0, obj_, this, this, false);
                    } else {
                    	if (!obj_.equals(dataRef.getItem(langId).getCurrent()))
                    		dataRef.changeItem(obj_, this, this);
                    }
                } else if (actionFlag == Constants.ADD_ACTION) {
                    String o = "";
                    for (int i = 0; i < values.size(); i++) {
                        if (i == values.size() - 1) {
                            o = o + values.get(i).toString();
                        } else {
                            o = o + values.get(i).toString() + ", ";
                        }
                    }
                    if (dataRef.getItem(langId) == null) {
                        dataRef.insertItem(0, o, this, this, false);
                    } else {
                        Object o1 = dataRef.getItem(langId).getCurrent();
                        dataRef.changeItem(o1.toString() + ", " + o.toString(), this, this);
                    }
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (afterTemplate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", values);
            try {
                orlang.evaluate(afterTemplate, vc, this, new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие после вставки");
            }
        }
    }

    private class OrPopupCellRenderer extends OrCellRenderer {
       
        private int alignment = SwingConstants.LEFT;
        private boolean isFirstLoading = true;

        public OrPopupCellRenderer() {
            if (titleRef != null && titleRef.getAttribute() != null && titleRef.getAttribute().typeClassId == Kernel.IC_FLOAT) {
                alignment = SwingConstants.RIGHT;
            }
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	String iconName = MainFrame.iconsSettings.get("iconPopupColumn");
			final ImageIcon imageIcon = kz.tamur.rt.Utils.getImageIconFull(iconName);
            final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            OrTableModel model = (OrTableModel) table.getModel();
            if (model instanceof TreeTableAdapter.RtTreeTableModel) {
                row = ((TreeTableAdapter.RtTreeTableModel) model).getActualRow(row);
            }
            if (hpopup.isIconVisible()) {
				if (imageIcon == null) {
					comp.setIcon(hpcImage);
				} else {
					comp.setIcon(imageIcon);
				}
			} else {
				comp.setIcon(null);
			}
            if (titleRef != null) {
                long lid = langId;
                if (lid <= 0) {
                    KrnObject lang = frame.getInterfaceLang();
                    lid = lang != null ? lang.id : 0;
                }

                List items = titleRef.getItems(lid);
                if (items.size() > 0 && items.size() > row) {
                    Object o = ((OrRef.Item) items.get(row)).getCurrent();
                    if (o instanceof java.sql.Date || o instanceof java.util.Date) {
                        comp.setText(formatter.format(o));
                    } else if (o instanceof Double && dformat != null) {
                        comp.setText(dformat.format(o));
                    } else {
                        if (o != null) {
                            comp.setText(o.toString());
                        } else {
                            comp.setText("");
                        }
                    }
                }
            } else {
                comp.setText("");
            }
            comp.setHorizontalAlignment(alignment);
            if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
                // Непрозрачность для текста и иконки
                JLabel newComp = new JLabel() {
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        g.drawString(comp.getText(), 12, 10);
                        if (comp.getIcon() != null) {
                        	if (imageIcon == null) {
                        		g.drawImage(hpcImage.getImage(), 0, 2, null);
                        	} else {
                        		g.drawImage(imageIcon.getImage(), 0, 2, null);
                        	}
                        }
                    }
                };
                newComp.setHorizontalAlignment(alignment);
                newComp.setIcon(comp.getIcon());
                newComp.setFont(comp.getFont());
                newComp.setBackground(comp.getBackground());
                newComp.setForeground(comp.getForeground());
                newComp.setOpaque(true);
                newComp.setToolTipText(comp.getToolTipText());
                return newComp;
            }
            if (hpopup.isClearBtnExists()) {
            	return getLabel(comp, row, isSelected, isFirstLoading);
            } else {
              return comp;
            }
        }

        private JLabel getLabel(JLabel addLabel, int row, boolean isSelected, boolean isFirstLoading) {
            ImageIcon deleteIcon = kz.tamur.rt.Utils.getImageIconExt("DeleteValue", ".png");
        	Color selectColor = new Color(178, 186, 202);
        	Color whiteColor = new Color(255, 255, 255);
        	Color greyColor = new Color( 223, 220, 220);
        	
        	JLabel mainLabel = new JLabel();
        	mainLabel.setLayout(new BorderLayout());
        	JLabel deleteLabel = new JLabel();
            deleteLabel.setIcon(deleteIcon);
            if (addLabel.getText().length() > 0) {
            	deleteLabel.setEnabled(true);
            } else {
            	deleteLabel.setEnabled(false);
            }
        	JPanel deletePanel = new JPanel(new GridBagLayout());

        	if (isSelected || isFirstLoading) {
        		if (isFirstLoading) {
        			setFirstLoading(false);
        		}
        		mainLabel.setBackground(selectColor);
        		deleteLabel.setBackground(selectColor);
        		deletePanel.setBackground(selectColor);
        	} else {
    	    	if (row % 2 == 0) {
    	    		mainLabel.setBackground(whiteColor);
    	    		deleteLabel.setBackground(whiteColor);
    	    		deletePanel.setBackground(whiteColor);
    	    	} else {
    	    		mainLabel.setBackground(greyColor);
    	    		deleteLabel.setBackground(greyColor);
    	    		deletePanel.setBackground(greyColor);
    	    	}
        	}
        	
        	deletePanel.add(deleteLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));
        	mainLabel.add(addLabel, BorderLayout.CENTER);
        	mainLabel.add(deletePanel, BorderLayout.EAST);
        	return mainLabel;
        }
        
        private void setFirstLoading(boolean isFirstLoading) {
        	this.isFirstLoading = isFirstLoading;
        }
    }

    public TableCellRenderer getCellRenderer() {
        return renderer;
    }

    public void setEnabled(boolean isEnable) {
        int mode = frame.getEvaluationMode();
        if (editable && mode != kz.tamur.rt.InterfaceManager.READONLY_MODE && mode != kz.tamur.rt.InterfaceManager.ARCH_RO_MODE) {
            super.setEnabled(isEnable);
            hpopup.setEnabled(isEnable);
            isEnabled = isEnable;
        } else {
            hpopup.setEnabled(editable && isEnable);
        }
    }

    public OrCellEditor getCellEditor() {
        if (cellEditor == null) {
            cellEditor = new HiperPopupCellEditor();
            hpopup.addActionListener(cellEditor);
        }
        return cellEditor;
    }

    public class HiperPopupCellEditor extends OrCellEditor {
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valueChanged(new OrRefEvent(dataRef, 0, -1, null));
            if (titleRef != null) {
                valueChanged(new OrRefEvent(titleRef, 0, -1, null));
                if (hpopup.getIcon() != null) {
                    hpopup.setIcon(null);
                }
            } else {
                hpopup.setText("");
            }

            return hpopup;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Object getValueFor(Object obj) {
            return ((OrRef.Item) obj).getCurrent();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (!selfChange) {
            OrRef ref = e.getRef();
            if (ref == titleRef) {
                selfChange = true;
                long lid = langId;
                if (lid <= 0) {
                    KrnObject lang = frame.getInterfaceLang();
                    lid = lang != null ? lang.id : 0;
                }
                Object value = ref.getValue(lid);
                if (value != null) {
                    if (value instanceof java.sql.Date || value instanceof java.util.Date) {
                        hpopup.setText(formatter.format(value));
                    } else if (value instanceof Double && dformat != null) {
                        hpopup.setText(dformat.format(value));
                    } else {
                        hpopup.setText("" + value);
                    }
                } else {
                    hpopup.setText("");
                }
                selfChange = false;
            }
        }
    }

    public OrRef getTitleRef() {
        return titleRef;
    }

    public OrRef getContentRef() {
        return contentRef;
    }

    public boolean checkConstraints(DesignerDialog dlg) {
        ReqMsgsList msg = frm.getRef().canCommit();
        if (msg.getListSize() > 0) {
            SortedFrame errDlg = new SortedFrame(dlg, res.getString("errors"));
            msg.setParent(errDlg);
            errDlg.setOption(new String[] { res.getString("continue"), res.getString("save") });
            errDlg.setContent(msg);
            errDlg.setLocation(Utils.getCenterLocationPoint(errDlg.getSize()));
            errDlg.show();
            return errDlg.getResult() != BUTTON_OK;
        }
        return true;
    }

    public boolean checkConstraints(DesignerModalFrame frame) {
        ReqMsgsList msg = frm.getRef().canCommit();
        if (msg.getListSize() > 0) {
            SortedFrame errDlg = new SortedFrame(frame, res.getString("errors"));
            msg.setParent(errDlg);
            errDlg.setOption(new String[] { res.getString("continue"), res.getString("save") });
            errDlg.setContent(msg);
            errDlg.setLocation(Utils.getCenterLocationPoint(errDlg.getSize()));
            errDlg.show();
            return errDlg.getResult() != BUTTON_OK;
        }
        return true;
    }

    class ActionLoader extends SwingWorker {

        private ActionEvent event;
        private RootPaneContainer root;

        public ActionLoader(ActionEvent e, RootPaneContainer root) {
            super();
            this.event = e;
            this.root = root;
        }

        public Object construct() {
            actionPerformed2(event);
            CursorToolkit.stopWaitCursor(root);
            return null;
        }

        public void finished() {
            super.finished();
            sw = null;
        }
    }
}
