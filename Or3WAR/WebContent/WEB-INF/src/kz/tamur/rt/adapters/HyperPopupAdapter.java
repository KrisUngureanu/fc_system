package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrHyperPopupComponent;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebHyperPopup;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.WebFrame;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonObject;

public class HyperPopupAdapter extends ComponentAdapter {

    private OrHyperPopupComponent hpopup;
    private boolean selfChange = false;
    private KrnObject _ifc, dynIfc;
    private OrRef dynIfcRef;
    private String _ifcTitle;
    private OrRef contentRef;
    private ASTStart contentExpr;
    private OrRef selectedRef;
    private OrRef autoCreateRef;
    private int refreshMode;
    private int cash;
    private boolean fork;
    private ASTStart beforeOpenAction,afterTemplate, beforTemplate, beforeModificationTemplate, dynamicIfcExprTemplate;
    private String selectedRefPath;
    private OrRef titleRef;
    private int[] selRows;
    private int actionFlag;
    private boolean copyFlag;
    private boolean ifcLock = false;
    private OrCalcRef titleRefExpr, attentionRef;

    public HyperPopupAdapter(OrFrame frame, OrHyperPopupComponent hpopup, boolean isEditor) throws KrnException {
        super(frame, hpopup, isEditor);
        Kernel krn = frame.getKernel();
        PropertyNode proot = hpopup.getProperties();
        PropertyValue pv = hpopup.getPropertyValue(
                proot.getChild("ref").getChild("refreshMode"));
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
        //TitlePath
        PropertyNode prop = proot.getChild("ref").getChild("titlePath");
        pv = hpopup.getPropertyValue(prop);
        String titlePath;
        if (!pv.isNull()) {
            titlePath = pv.stringValue(frame.getKernel());
            if (isEditor()) {
                titleRef = OrRef.createRef(titlePath, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            } else {
                titleRef = OrRef.createRef(titlePath, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            }
            titleRef.addOrRefListener(this);
        }
        
        PropertyNode propExpr = proot.getChild("ref").getChild("titlePathExpr");
        pv = hpopup.getPropertyValue(propExpr);
        String titlePathExpr;
        if (!pv.isNull()) {
            titlePathExpr = pv.stringValue();
            if (titlePathExpr.trim().length() > 0) {
                try {
                    propertyName = "Свойство: Данные.Титулы.Формула";
                    if (isEditor()) {
                    	titleRefExpr = new OrCalcRef(titlePathExpr, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),frame, hpopup, propertyName, this);
                    } else { 
                    	titleRefExpr = new OrCalcRef(titlePathExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),frame, hpopup, propertyName, this);
                    }
                    titleRefExpr.addOrRefListener(this);
                } catch (Exception e) {
                    showErrorNessage(e.getMessage() + titlePathExpr);
                    log.error(e, e);
                }
            }
        }
        
    	String attentionExpr = ((OrWebHyperPopup) hpopup).getAttentionExpr();
    	if (attentionExpr != null && attentionExpr.length() > 0) {
    		try {
                propertyName = "Свойство: Поведение.Активность.Внимание";
	            attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, hpopup, propertyName, this);
	            attentionRef.addOrRefListener(this);
	    	} catch (Exception e) {
		        showErrorNessage(e.getMessage() + attentionExpr);
		        log.error(e, e);
	    	}
    	}
        
        //содержимое
        PropertyNode rprop = proot.getChild("ref").getChild("content");
        pv = hpopup.getPropertyValue(rprop);
        if (!pv.isNull()) {
            final String contentPath = pv.stringValue(frame.getKernel());
            try {
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
            } catch(Exception ex) {
                Util.showErrorMessage(hpopup, ex.getMessage(), "Содержимое");
            }
        }
        // выбираемый атрибут
        rprop = proot.getChild("ref").getChild("selectedRef");
        pv = hpopup.getPropertyValue(rprop);
        if (!pv.isNull()) {
            selectedRefPath = pv.stringValue(frame.getKernel());
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("callDialog"));
        if (!pv.isNull()) {
        	//Необходимо иметь все атрибуты KrnObject
        	_ifc = krn.getObjectById(Long.parseLong(pv.getKrnObjectId()), 0);
            //_ifc = new KrnObject(Long.parseLong(pv.getKrnObjectId()), "", krn.getClassByName(pv.getKrnClassName()).id);
            _ifcTitle = pv.getTitle();
        }
        this.hpopup = hpopup;

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobAfter"));
        String expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue(frame.getKernel());
        }
        if (expr != null && expr.length() > 0) {
        	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.AFTER_JOB_TYPE;
            	afterTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
        	} else {
        		afterTemplate = OrLang.createStaticTemplate(expr, log);
        	}
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error(ex, ex);
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobBefore"));
        expr = null;
        if (!pv.isNull()) {
            expr = pv.stringValue(frame.getKernel());
        }
        if (expr != null && expr.length() > 0) {
        	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.BEFORE_JOB_TYPE;
            	beforeModificationTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
        	} else {
        		beforeModificationTemplate = OrLang.createStaticTemplate(expr, log);
        	}
            try {
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error(ex, ex);
            }
        }
        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("dynamicIfcExpr"));
        String dynIfcExpr = null;
        if (!pv.isNull()) {
            dynIfcExpr = pv.stringValue(frame.getKernel());
        }
        if (dynIfcExpr != null && dynIfcExpr.length() > 0) {
        	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.DYNAMIC_IFC_TYPE;
            	dynamicIfcExprTemplate = ClientOrLang.getStaticTemplate(ifcId, key, dynIfcExpr, getLog());
        	} else {
        		dynamicIfcExprTemplate = OrLang.createStaticTemplate(dynIfcExpr, log);
        	}
            try {
                Editor e = new Editor(dynIfcExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error(ex, ex);
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("actionJobBeforClear"));
        String beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue(frame.getKernel());
        }
        if (beforExpr != null && beforExpr.length() > 0) {
        	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.BEFORE_CLEAR_JOB_TYPE;
            	beforTemplate = ClientOrLang.getStaticTemplate(ifcId, key, beforExpr, getLog());
        	} else {
        		beforTemplate = OrLang.createStaticTemplate(beforExpr, log);
        	}
            try {
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error(ex, ex);
            }
        }

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("charModification"));
        if (!pv.isNull()) {
            actionFlag = pv.intValue();
        }

        pn = hpopup.getProperties().getChild("pov").getChild("ifcLock");
        pv = hpopup.getPropertyValue(pn);
        if (!pv.isNull()) {
            ifcLock = pv.booleanValue();
        }
        // Действие перед открытием интерфейса
    	pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("act").getChild("beforeOpen"));
        String beforOpenExpr = null;
        if (!pv.isNull()) {
            beforOpenExpr = pv.stringValue(frame.getKernel());
        }
        if (beforOpenExpr != null && beforOpenExpr.length() > 0) {
        	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.BEFORE_OPEN_TYPE;
            	beforeOpenAction = ClientOrLang.getStaticTemplate(ifcId, key, beforOpenExpr, getLog());
        	} else {
        		beforeOpenAction = OrLang.createStaticTemplate(beforOpenExpr, log);
        	}
            try {
                Editor e = new Editor(beforOpenExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error(ex, ex);
            }
    	}

        pv = hpopup.getPropertyValue(proot.getChild("pov").getChild("dynamicIfc"));
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
            	log.error(e, e);
            }
        }    
        
        //* Действие при открытии HyperPopup *\\
        PropertyNode pPopupExpr = proot.getChild("ref").getChild("contentCalc");
        pv = hpopup.getPropertyValue(pPopupExpr);
        String contentPopupExpr;
        if (!pv.isNull()) {
        	contentPopupExpr = pv.stringValue(frame.getKernel());
            
		    if (contentPopupExpr != null && contentPopupExpr.length() > 0) {
		    	propertyName = "Свойство: Содержимое формула";
		    	if (hpopup instanceof WebComponent && frame instanceof WebFrame) {
		    		long ifcId = ((WebFrame)frame).getObj().id;
		        	String key = ((WebComponent)hpopup).getId() + "_" + OrLang.HYPER_POPUP_EXPR_TYPE;
		        	contentExpr = ClientOrLang.getStaticTemplate(ifcId, key, contentPopupExpr, getLog());
		    	} else {
		    		contentExpr = OrLang.createStaticTemplate(contentPopupExpr, log);
		    	}
		        try {
		            Editor e = new Editor(contentPopupExpr);
		            ArrayList<String> paths = e.getRefPaths();
		            for (int j = 0; j < paths.size(); ++j) {
		                String path = paths.get(j);
		                OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
		                        OrRef.TR_CLEAR, frame);
		            }
		        } catch (Exception ex) {
		        	log.error(ex, ex);
		        }
			}
        }      
        
        this.hpopup.setXml(null);
        //this.hpopup.setBackground(Color.red);
    }

    public void clear() {
    }

    public boolean doBeforeOpen() {
    	boolean res = true;
    	if (beforeOpenAction != null) {
	    	ClientOrLang lng = new ClientOrLang(frame);
	    	Map<String, Object> vars = new HashMap<String, Object>();
	    	Stack<String> callStack = new Stack<String>();
            boolean calcOwner = OrCalcRef.setCalculations();
	        try {
	        	lng.evaluate(beforeOpenAction, vars, this, callStack);
	        } catch (Exception ex) {
	            Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед открытием");
            	log.error("Ошибка при выполнении формулы 'Действие перед открытием' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
	        }
            Object ret = vars.get("RETURN");
            res = !(ret instanceof Number && ((Number) ret).intValue() == 0);
    	}
    	return res;
    }

    public OrFrame getPopupFrame() {
        try {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
            //KrnObject[] objs = null;
            List<KrnObject> objs = null;
            boolean hasFilters = false;
            if (contentRef != null) {
                if (refreshMode != Constants.RM_DIRECTLY) {
                    contentRef.getRoot().evaluate(hpopup);
                }
                hasFilters = contentRef.hasFilters();
/*
                    if (refreshMode == Constants.RM_DIRECTLY
                            && (dataRef == null || dataRef.getRoot() != contentRef.getRoot())) {
                        contentRef.getRoot().evaluate(hpopup);
                    }
*/
                objs = new ArrayList<KrnObject>();
                if (contentRef.isArray() && !contentRef.isInOrTable() && !contentRef.isColumn()) {
                    List<Item> items = contentRef.getItems(langId);
                    for (int i = 0; i < items.size(); ++i) {
                        KrnObject obj = (KrnObject) items.get(i).getCurrent();
                        //if (filteredIds == null || filteredIds.contains(new Integer(obj.id))) {
                        if (obj != null) {
                            objs.add(obj);
                        }
                        //}
                    }
                } else {
                    OrRef.Item item = contentRef.getItem(langId);
                    if (item != null) {
                        if (item.getCurrent() instanceof KrnObject) {
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
                } catch (Exception e) {
                	log.error("Ошибка при выполнении формулы 'Данные.Содержимое формула' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(e, e);
                } finally {
            		if (calcOwner)
            			OrCalcRef.makeCalculations();
                }
                objs = (List<KrnObject>)vc.get("RETURN");
            }
            
            if (mgr != null) {
                long tr_id = mgr.getCash().getTransactionId();
                KrnObject[] os = null;
                if (objs != null) {
                    os = (KrnObject[]) objs.toArray(new KrnObject[objs.size()]);
                    log.info("************************");
                    log.info("objects size = " + os.length);
                    log.info("************************");
                }
                OrFrame frm = null;
                if (_ifc != null) {
                    frm = mgr.getInterfacePanel(_ifc, os, tr_id,
                            frame.getEvaluationMode(), (cash & 0x1) > 0, fork, true);
                } else if (dynIfcRef != null) {
                    OrRef.Item item  = dynIfcRef.getItem(langId);
                    dynIfc = (KrnObject) ((item != null) ? item.getCurrent() : null);
                    if (dynIfc != null) {
                        frm = mgr.getInterfacePanel(dynIfc, os, tr_id,
                                frame.getEvaluationMode(), (cash & 0x01) > 0, fork, true);
                    }
                } else if (dynamicIfcExprTemplate != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    Map vc = new HashMap();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(dynamicIfcExprTemplate, vc, this, new Stack<String>());
                        Object res = vc.get("RETURN");
                        if (res != null && res instanceof KrnObject) {
                            dynIfc = (KrnObject)res;
                            frm = mgr.getInterfacePanel(dynIfc, os, tr_id,
                                    frame.getEvaluationMode(), (cash & 0x01) > 0, fork, true);
                        }
                    } catch (Exception ex) {
                        Util.showErrorMessage(hpopup, ex.getMessage(),
                                "Динамический интерфейс (Выражение)");
                    	log.error("Ошибка при выполнении формулы 'Динамический интерфейс (Выражение)' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                        log.error(ex, ex);
        	        } finally {
        				if (calcOwner)
        					OrCalcRef.makeCalculations();
                    }
                }
                if (frm == null) {
                    return null;
                }
                if (selectedRefPath != null && selectedRefPath.length() > 0) {
                    if (selectedRef == null) {
                        selectedRef = OrRef.createRef(selectedRefPath, false,
                                Mode.RUNTIME, mgr.getRefs(), OrRef.TR_CLEAR,
                                frm);
                    }
                }
                OrRef frmRef = frm.getRef();
                if (frmRef != null) {
                	frmRef.setHasFilters(hasFilters);
				}
                //Привязка к БД может отсутствовать, когда не нужно изначально подавать объекты на интерфейс
            	return frm;
                /*else {
                	Util.showErrorMessage(hpopup, "В интерфейсе отсутствует привязка к БД","");
                	return null;
				}
                */
                //frm.getRef().fireValueChangedEvent(-1, this, 0);
                //if (index > 0) {
                //    frm.getRef().absolute(index, this);
                //}
                
            }
        } catch (KrnException e1) {
        	log.error(e1, e1);
        }
        return null;
    }

    public JsonObject checkConstraints(WebFrame frm) {
        ReqMsgsList msg = frm.getRef().canCommit();
        if (msg.getListSize() > 0) {
			boolean isDataIntegrityControl = ((OrWebPanel) frm.getPanel()).isDataIntegrityControl();
        	JsonObject res = new JsonObject().add("result", "error").add("fatal", msg.hasFatalErrors() ? 1 : 0)
        			.add("errors", frm.toJSON(msg)).add("isDataIntegrityControl", isDataIntegrityControl ? 1 : 0);
        	
        	Pair<String, String> docFile = frm.toFileMsg(msg);
        	if (docFile != null)
        		res.add("path", docFile.first).add("name", docFile.second);
        	return res;
        } else {
            return new JsonObject().add("result", "success");
        }
    }

    public void okPressed(OrFrame frm) {
        kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
        boolean calcOwner = OrCalcRef.setCalculations();
        try {
	        // Получаем спсок выбранных объектов
	        List<OrRef.Item> selectedItems = frm.getRef().getSelectedItems();
	        if (selectedRef != null) {
	            if (selectedRef.getSelItems() != null) {
	                selectedItems = new ArrayList<Item>(selectedRef.getSelItems());
	            } else if (selectedRef.getSelectedItems() != null) {
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
	
	        mgr.releaseInterface(true);

	        if ((cash & 0x01) > 0)
	            frm.getRef().fireValueChangedEvent(-1, this, 0);
	
	        List<Object> selectedObjects = makeSelObjList(selectedItems);
	
	        if (((WebFrame) frame).getSession().getConfirmMessage() != null) {
	        	if (calcOwner) {
	        		calcOwner = false;
	                OrCalcRef.removeCalculations();
	        	}
	        	return;
	        }
	
	        // Выполняем действия до модификации
	        if (beforeModificationTemplate != null) {
	            ClientOrLang orlang = new ClientOrLang(frame);
	            Map<String, Object> vc = new HashMap<String, Object>();
	            vc.put("SELOBJS", selectedObjects);
	            try {
	                orlang.evaluate(beforeModificationTemplate, vc, this, new Stack<String>());
	                Object res = vc.get("RETURN");
	                if (res != null) {
	                    selectedObjects = (List<Object>)res;
	                }
	            } catch (Exception ex) {
	                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед модификацией");
                	log.error("Ошибка при выполнении формулы 'Действие перед модификацией' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(ex, ex);
	            }
	        }
	    
	        Object v = selectedObjects.size() > 0 ? selectedObjects : null;
	        updateParamFilters(v);

	        if (dataRef != null && frm.getRef().isInOrTable()) {
	            if (selectedItems.size() == 0)
	                dataRef.fireValueChangedEvent(0, this, 0);
	            else
	                addItems(selectedObjects);
	        } else if (dataRef != null) {
	            addItems(selectedObjects);
	        }
    	} catch (Exception e) {
    		log.error(e, e);
    	} finally {
			if (calcOwner)
				OrCalcRef.makeCalculations();
    	}
    }

    /**
     * Действие при нажатии на кнопку "Очистить значение"
     * 
     * @param isRelease
     *            удалить интерфес из стека? Если кнопка нажата на открывшемся диалоге, то нужно удалять, если на кнопке, то нет.
     */
    public void clearPressed(boolean isRelease) {
        boolean calcOwner = OrCalcRef.setCalculations();
        try {
	        if (isRelease) {
	            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
	            mgr.releaseInterface(false);
	        }
	    	boolean isSaveValue = false;
	        if (beforTemplate != null) {
	            ClientOrLang orlang = new ClientOrLang(frame);
	            Map<String, Object> vc = new HashMap<String, Object>();
	            try {
	                orlang.evaluate(beforTemplate, vc, this, new Stack<String>());
	                Object saveValue = vc.get("SAVEVALUE");
	                isSaveValue = (saveValue instanceof Number && ((Number) saveValue).intValue() == 1);
	            } catch (Exception ex) {
	                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед вставкой");
	            	log.error("Ошибка при выполнении формулы 'Действие перед вставкой' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
	                log.error(ex, ex);
	            }
	        }
	        if (!isSaveValue) {
	        	updateParamFilters(null);
	        	if (dataRef != null) {
		            OrRef.Item item = dataRef.getItem(langId);
		            if (item != null) {
		                dataRef.deleteItem(this, this);
		            }
	        	}
	        }
    	} catch (Exception e) {
    		log.error(e, e);
    	} finally {
			if (calcOwner)
				OrCalcRef.makeCalculations();
    	}
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
        for(Object o : values) {
            if (o instanceof KrnObject) {
                if (((KrnObject)value).id == ((KrnObject)o).id) {
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
        kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
        try {
        	if (!dataRef.isColumn()) {
	            List<OrRef.Item> currentItems = dataRef.getItems(langId);
	            for (int i = 0; i < currentItems.size(); ++i) {
	            	Object obj = currentItems.get(i).getCurrent();
	            	if (obj instanceof KrnObject) {
	            		Funcs.remove(values, (KrnObject)obj);
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
                        KrnObject[] krn_obj = frame.getKernel().cloneObject2(new KrnObject[]{(KrnObject) obj},
                                copyTrId,
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
                            KrnObject[] krn_obj = frame.getKernel().cloneObject2(new KrnObject[]{(KrnObject) value},
                                    copyTrId,
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
                        KrnObject[] krn_obj = frame.getKernel().cloneObject2(new KrnObject[]{(KrnObject) value},
                                copyTrId,
                                mgr.getCash().getTransactionId());
                        value = krn_obj[0];
                    }
                    dataRef.insertItemHack(-1, -1, value, this, this, false);
                }
                if (dataRef.getIndex() < 0)
    				dataRef.setIndex(0);

                dataRef.fireValueChangedEvent(-1, this, 0);
            } else if (values.size() > 0) {
                if (selectedRefPath == null || actionFlag == Constants.CHANGE_ACTION) {
                    Object obj_ = values.get(0);
                    if (copyFlag && obj_ instanceof KrnObject) {
                        KrnObject[] krn_obj = frame.getKernel().cloneObject2(new KrnObject[]{(KrnObject) obj_},
                                copyTrId,
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
                        dataRef.changeItem(o1.toString() + ", " + o.toString(),
                                this, this);
                    }
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
        if (afterTemplate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", values);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterTemplate, vc, this, new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие после вставки");
            	log.error("Ошибка при выполнении формулы 'Действие после вставки' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
        }
    }

    public void setEnabled(boolean isEnable) {
        int mode = frame.getEvaluationMode();
        if (editable && mode != kz.tamur.rt.InterfaceManager.READONLY_MODE &&
                mode != kz.tamur.rt.InterfaceManager.ARCH_RO_MODE) {
            super.setEnabled(isEnable);
            hpopup.setEnabled(isEnable);
            isEnabled = isEnable;
        } else {
            hpopup.setEnabled(editable && isEnable);
        }
    }

    public void deleteButtonPressed() {
        if (beforTemplate != null) {
            ClientOrLang orlang = new ClientOrLang(HyperPopupAdapter.this.frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforTemplate, vc, HyperPopupAdapter.this, new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(hpopup, ex.getMessage(), "Действие перед вставкой");
            	log.error("Ошибка при выполнении формулы 'Действие перед вставкой' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
        	} finally {
	            if (calcOwner)
	            	OrCalcRef.makeCalculations();
            }
        }
        boolean calcOwner = OrCalcRef.setCalculations();
        try {
	        updateParamFilters(null);
	        if (dataRef != null) {
	            OrRef.Item item = dataRef.getItem(langId);
	            if (item != null) {
	                dataRef.deleteItem(HyperPopupAdapter.this, HyperPopupAdapter.this);
	            }
	        }
    	} catch (Exception e) {
    		log.error(e, e);
    	} finally {
			if (calcOwner)
				OrCalcRef.makeCalculations();
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
                hpopup.setValue(value);
                if (isEditor()) {
                	((OrWebHyperPopup) hpopup).getColumnAdapter().valueChanged(e);
                }
                selfChange = false;
            } else if (ref == titleRefExpr) {
                selfChange = true;
                Object value = ref.getValue(langId);
                hpopup.setValue(value);
                if (isEditor()) {
                	((OrWebHyperPopup) hpopup).getColumnAdapter().valueChanged(e);
                }
                selfChange = false;
            } else if (ref == attentionRef) {
    			((OrWebHyperPopup) hpopup).sendChangeProperty("hyperPopupAttention", attentionRef.getValue(langId).toString());
            }
        }
    }

    public OrRef getTitleRef() {
        return titleRef;
    }
    
    public OrCalcRef getTitleRefExpr() {
        return titleRefExpr;
    }

    public OrRef getContentRef() {
        return contentRef;
    }
}
