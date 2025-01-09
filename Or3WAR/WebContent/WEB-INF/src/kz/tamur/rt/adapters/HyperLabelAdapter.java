package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.WebFrame;
import kz.tamur.or3.client.comps.interfaces.OrHyperLabelComponent;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

public class HyperLabelAdapter extends ComponentAdapter {

    private OrHyperLabelComponent hlabel;
    private KrnObject _ifc, dynIfc;
    private OrRef dynIfcRef;
    private boolean editIfc = false;
    
    private ASTStart beforeOpenAction;
    private ASTStart dynamicIfcExprTemplate;
    
    private int cashFlag;

    public HyperLabelAdapter(OrFrame frame, OrHyperLabelComponent hlabel, boolean isEditor)
            throws KrnException {
        super(frame, hlabel, isEditor);
        Kernel krn = frame.getKernel();
        PropertyNode proot = hlabel.getProperties();
        PropertyValue pv = hlabel.getPropertyValue(proot.getChild("pov").getChild("interface"));
        if (!pv.isNull()) {
        	long ifcId = Long.parseLong(pv.getKrnObjectId());
        	String uid = frame.getKernel().getUId(ifcId);
            _ifc = new KrnObject(ifcId, uid, krn.getClassByName(pv.getKrnClassName()).id);
        }

        // Поведение
        PropertyNode behavNode = proot.getChild("pov");
        // Действие перед открытием интерфейса
    	pv = hlabel.getPropertyValue(behavNode.getChild("beforeOpen"));
    	String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
    	if (expr.length() > 0) {
        	long ifcId = ((WebFrame)frame).getObj().id;
        	String key = ((WebComponent)hlabel).getId() + "_" + OrLang.BEFORE_OPEN_TYPE;
        	beforeOpenAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
    	}
        // Динамический интерфейс (Ref)
        pv = hlabel.getPropertyValue(behavNode.getChild("dynamicIfc"));
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Динамический интерфейс";
                dynIfcRef = OrRef.createRef(pv.stringValue(frame.getKernel()), false, Mode.RUNTIME, frame.getRefs(),
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
            dynIfcExpr = pv.stringValue(frame.getKernel());
        }
        if (dynIfcExpr != null && dynIfcExpr.length() > 0) {
        	long ifcId = ((WebFrame)frame).getObj().id;
        	String key = ((WebComponent)hlabel).getId() + "_" + OrLang.DYNAMIC_IFC_TYPE;
            dynamicIfcExprTemplate = ClientOrLang.getStaticTemplate(ifcId, key, dynIfcExpr, getLog());
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
            editIfc = (Boolean) proot.getChild("pov").getChild("editIfc").getDefaultValue();
        }
        
        pv = hlabel.getPropertyValue(proot.getChild("pov").getChild("cashFlag"));
        if (!pv.isNull()) {
        	cashFlag = pv.intValue();
        }

        this.hlabel = hlabel;
        this.hlabel.setXml(null);
    }

    public void clear() {
    }

    //ActionListener
    public void forward(boolean evalBeforeOpen) {
        InterfaceManager mgr = frame.getInterfaceManager();
        KrnObject[] objs = null;
        if (dataRef != null) {
            if (dataRef.isArray() && !dataRef.isInOrTable()) {
                List<OrRef.Item> items = dataRef.getItems(langId);
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
                long tid = mgr.getCash().getTransactionId();
                if (dynIfcRef != null) {
                    OrRef.Item item  = dynIfcRef.getItem(langId);
                    dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                } else if (dynamicIfcExprTemplate != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                        Object res = vc.get("RETURN");
                        if (res != null && res instanceof KrnObject) {
                            dynIfc = (KrnObject)res;
                        }
                    } catch (Exception ex) {
                        Util.showErrorMessage(hlabel, ex.getMessage(),
                                "Динамический интерфейс (Выражение)");
                    	log.error("Ошибка при выполнении формулы 'Динамический интерфейс (Выражение)' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                        log.error(ex, ex);
        	        } finally {
        				if (calcOwner)
        					OrCalcRef.makeCalculations();
                    }
                }
                int mode = editIfc ? InterfaceManager.SERVICE_MODE : InterfaceManager.READONLY_MODE;
                OrFrame frameUI = mgr.getCurrentFrame();
                if (_ifc != null) {
                    boolean res = (!evalBeforeOpen) || doBeforeOpen();
                    if (res) {
	                    if (frameUI.getRef() == null) {
	                        mode = frameUI.getEvaluationMode();
	                    }
	                    mgr.absolute(_ifc, objs, path, mode, false, (cashFlag & 2) > 0 ? 0 : tid, (cashFlag & 1) > 0, frame.getFlowId(), hlabel.isBlockErrors(),"");
                    }
                } else if (dynIfc != null) {
                    boolean res = (!evalBeforeOpen) || doBeforeOpen();
                    if (res) {
	                    if (frameUI.getRef() == null) {
	                        mode = frameUI.getEvaluationMode();
	                    }
	                    mgr.absolute(dynIfc, objs, path, mode, false, (cashFlag & 2) > 0 ? 0 : tid, (cashFlag & 1) > 0, frame.getFlowId(), hlabel.isBlockErrors(),"");
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void setDynIfcRef(OrRef dynIfcRef) {
        this.dynIfcRef = dynIfcRef;
        //this.dynIfcRef.addOrRefListener(this);
    }
    
    private boolean doBeforeOpen() throws Exception {
    	boolean res = true;
    	if (beforeOpenAction != null) {
	    	ClientOrLang lng = new ClientOrLang(frame);
	    	Map<String, Object> vars = new HashMap<String, Object>();
	    	Stack<String> callStack = new Stack<String>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                lng.evaluate(beforeOpenAction, vars, this, callStack);
            } catch (Exception e) {
                Util.showErrorMessage(hlabel, e.getMessage(), "Действие перед открытием");
            	log.error("Ошибка при выполнении формулы 'Действие перед открытием' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
        		log.error(e, e);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
            Object ret = vars.get("RETURN");
            res = !(ret instanceof Number && ((Number) ret).intValue() == 0);
        }
    	return res;
    }

    public OrRef getDynamicInterfaceRef() {
        return dynIfcRef;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        hlabel.setEnabled(isEnabled);
    }
}
