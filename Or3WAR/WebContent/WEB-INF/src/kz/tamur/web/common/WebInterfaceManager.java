package kz.tamur.web.common;

import static kz.tamur.comps.Constants.ACT_AUTO_STRING;
import static kz.tamur.comps.Constants.ACT_DIALOG_STRING;
import static kz.tamur.rt.InterfaceManager.ARCH_RO_MODE;
import static kz.tamur.rt.InterfaceManager.ARCH_RW_MODE;
import static kz.tamur.rt.InterfaceManager.SERVICE_MODE;
import static kz.tamur.rt.InterfaceManager.SPR_RO_MODE;
import static kz.tamur.rt.InterfaceManager.SPR_RW_MODE;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManager.CommitResult;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.rt.adapters.PanelAdapter;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.component.WebFrameManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 15.07.2006
 * Time: 18:00:54
 */
public class WebInterfaceManager {
    private Log log;

    private WebSession session;
    private KrnObject selectedIfcLang;
    private KrnObject selectedDataLang;
    private WebFrame mainUI;

    public WebInterfaceManager(WebSession session) {
        this.session = session;
        this.log = LogFactory.getLog(session.getKernel().getUserSession().dsName + "."
        				+ session.getKernel().getUserSession().logName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
        selectedIfcLang = session.getKernel().getInterfaceLanguage();
        selectedDataLang = session.getKernel().getDataLanguage();
    }

    public void openActivityInterface(Activity act) {
        if (ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType)) {
              openAutoActivityInterface(act);
        } else {
            try {
                session.getKernel().openInterface(act.ui.id,act.flowId,act.trId,act.processDefId.length>0?act.processDefId[0]:-1);
                absolute(null, act.ui, (act.objs.length > 0 || act.uiFlag == 1) ? act.objs : null, "", SERVICE_MODE, false, act.trId, false, act.flowId, false, act.uiType);
                sendNodeType(act);
            } catch (KrnException e) {
                log.error(e, e);
            }
        }
    }
    
    private void sendNodeType(Activity act) throws KrnException {
    	String nodeType = session.getKernel().getNextProcessNode(act.flowId);
    	String buttonTitle = session.getResource().getString("send");
    	if ("end-state".equals(nodeType)) {
    		buttonTitle = session.getResource().getString("webSend");
    	} else if (!"".equals(nodeType)) {
    		buttonTitle = session.getResource().getString("buttonRun");
    	}
    	session.sendCommand("nodeType", buttonTitle);
    }

    public void openControlInterface(Activity act) {
        try {
            session.getKernel().openInterface(act.ui.id,act.flowId,act.trId,act.processDefId.length>0?act.processDefId[0]:-1);
            absolute(null, act.infUi, (act.infObjs.length > 0) ? act.infObjs : null, "", SERVICE_MODE, false, act.trId, false, act.flowId, false, act.uiType);
            sendNodeType(act);
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void openAutoActivityInterface(Activity act) {
        try {
        	session.getKernel().openInterface(act.ui.id,act.flowId,act.trId,act.processDefId.length>0?act.processDefId[0]:-1);
            getInterface(act.ui, (act.objs.length > 0 || act.uiFlag == 1) ? act.objs : null, act.trId, SERVICE_MODE, act.flowId, false, false, false);
            session.getTaskHelper().setAutoActOpen(act);
            sendNodeType(act);
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void openArchiveInterface(ArchiveHelper.HyperNode node) {
        try {
            int mode = node.isChangeable() ? ARCH_RW_MODE : ARCH_RO_MODE;
            absolute(null, node.getIfcObject(), null, "", mode, true, 0, false, 0, true, "");
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void openDictInterface(ArchiveHelper.HyperNode node) {
        try {
            int mode = session.getArchiveHelper().isReadOnly(node.getKrnObj().id, session.getId()) ? SPR_RO_MODE : SPR_RW_MODE;
            absolute(null, node.getIfcObject(), null, "", mode, true, 0, false, 0, true, "");
        } catch (KrnException e) {
            log.error(e, e);
        }
    }
    
    public void openLDIfc(String objUid, WebSession s) {
    	Kernel krn = s.getKernel();
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
    		KrnObject obj = krn.getClassOwnObjects(cls, 0)[0];
    		AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn).add("ifc_uid");
    		long[] objIds = {obj.id};    		
    		Object[] row = krn.getObjects(objIds, arb.build(), 0).get(0);    		
    		String ifcUid = arb.getStringValue("ifc_uid", row);
    		KrnObject uiObj = krn.getObjectByUid(ifcUid, 0);
    		KrnObject parObj = krn.getObjectByUid(objUid, 0);
    		absolute(null, uiObj, new KrnObject[] {parObj}, "", InterfaceManager.SERVICE_MODE, true, 0, false, 0, true, "");    		
    	} catch(KrnException e) {
    		log.error(e, e);
    	}
    }
    
    
    
    public void openTaskIntf(String objUid, String ifcUid, WebSession s) {
    	Kernel krn = s.getKernel();
    	try {
    		KrnObject uiObj = krn.getObjectByUid(ifcUid, 0);
    		KrnObject parObj = krn.getObjectByUid(objUid, 0);
   			if((uiObj!=null) && (parObj!=null)){
    			log.info("Открытие интерфейса по нажатию на уведомление. UID Интерфейса="+uiObj+", UID объекта="+parObj);
    			absolute(null, uiObj, new KrnObject[] {parObj}, "", InterfaceManager.SERVICE_MODE, true, 0, false, 0, true, ""); 
    		}
    		   		
    	} catch(KrnException e) {
    		log.error(e, e);
    	}
    }
    
    

    public void openDictInterface(KrnObject ifcObj) {
        try {
            absolute(null, ifcObj, null, "", SPR_RO_MODE, true, 0, false, 0, true, "");
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void openAdminInterface(ArchiveHelper.HyperNode node) {
        try {
        	int mode = session.getArchiveHelper().isReadOnly(node.getKrnObj().id, session.getId()) ? SPR_RO_MODE : SPR_RW_MODE;
            absolute(null, node.getIfcObject(), null, "", mode, true, 0, false, 0, true, "");
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void openOrderInterface(String iuid,String ouid) {
        try {
        	KrnObject iobj=session.getKernel().getCachedObjectByUid(iuid);
        	KrnObject oobj=session.getKernel().getCachedObjectByUid(ouid);
            absolute(null, iobj, new KrnObject[]{oobj}, "", InterfaceManager.SERVICE_MODE, true, 0, false, 0, true, "");
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public boolean absolute(WebFrame frm, KrnObject uiObj, KrnObject[] objs, String s, int mode, boolean b, long tid, boolean shareCash, long flowId,
    		boolean isBlockErrors, String uiType) throws KrnException {
        long start = System.currentTimeMillis();

        boolean result = false;
        if (uiObj != null) {
        	WebFrame oldFrm = null;
        	if (frm == null) {
        		frm = session.getFrameManager().getCurrentFrame();
	            if (frm != null) {
	                frm.commit(0);
	            }
	            oldFrm = frm;
	            frm = session.getFrameManager().absolute(uiObj, shareCash ? oldFrm : null);
        	}
            if (frm != null) {
                if (shareCash && oldFrm != null) {
                    frm.setCache(oldFrm.getCash());
                    oldFrm.getCash().setLogIfcId(frm.getInterfaceId());
            	} else {
            		frm.getCash().reset(flowId);
                }
                frm.setTransactionId(tid);
                frm.setInterfaceLang(selectedIfcLang, false);
                frm.setDataLang(selectedDataLang, false);
                frm.setFlowId(flowId);
                frm.setInitialObjs(objs);
                
                Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
                objs = doBeforeOpen(frm, objs, objsMap);
                
                PanelAdapter pa = frm.getPanelAdapter();
                if (pa.isEnabled()) {
                	if (mode == SPR_RO_MODE) {
                        pa.setEnabled(pa.getRef() == null);
                        frm.getSession().sendCommand("hideSend", "0");
                        frm.getSession().sendCommand("hideSave", "0");
                        frm.getSession().sendCommand("hideCancel", pa.getDataRef() == null ? "1" : "0");
                    } else if (mode == ARCH_RW_MODE || mode == ARCH_RO_MODE) {
                        pa.setEnabled(true);
                        frm.getSession().sendCommand("hideSave", frm.getPanel().isPanelEnabled() ? "1" : "0");
                        frm.getSession().sendCommand("hideSend", "0");
                        frm.getSession().sendCommand("hideCancel", frm.getPanel().isPanelEnabled() ? "2" : "0");
                    } else if (mode == SPR_RW_MODE){
                    	pa.setEnabled(true);
                    	frm.getSession().sendCommand("hideSend", "0");
                    	frm.getSession().sendCommand("hideSave", "1");
                    	frm.getSession().sendCommand("hideCancel", "1");
                    } else if (mode == InterfaceManager.SERVICE_MODE){
                        pa.setEnabled(true);
                    	frm.getSession().sendCommand("hideSend", "1");
                        frm.getSession().sendCommand("hideSave", "1");
                        frm.getSession().sendCommand("hideCancel", "1");
                    } else {
                    	pa.setEnabled(false);
                    }
                } else {
                	if (mode == ARCH_RW_MODE || mode == ARCH_RO_MODE) {
                    	frm.getSession().sendCommand("hideSend", "0");
                		frm.getSession().sendCommand("hideCancel", frm.getPanel().isPanelEnabled() ? "1" : "0");
                		frm.getSession().sendCommand("hideSave", frm.getPanel().isPanelEnabled() ? "1" : "0");
                	}
                    pa.setEnabled(false);
                }
                if (pa.getRef() != null && pa.getRef().toString().equals("уд::осн::Поручение"))
                	frm.getSession().sendCommand("hideSend", "0");

                evaluateAllRefs(frm, objs, objsMap);
                if (/*!frm.getKernel().isADVANCED_UI() ||*/ mainUI == null || session.getFrameManager().getIndex() > 0) {
                    showCurrent(mode, uiType);
                }
                result = true;
                
                OrWebPanel pan = (OrWebPanel) frm.getPanel();
        		if (pan.isHideBreadCrumps()) {
                    session.sendCommand("hideFullPath", true);
        		}
                session.sendCommand("stack", session.getFrameManager().getStackFrames());
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        log.info("|USER: " + session.getUserName() + "| Время генерации интерфейса : " + elapsedTimeMillis / 1000F + " сек.");
        return result;
    }

    public boolean reopen(WebFrame frm) throws KrnException {
        long start = System.currentTimeMillis();
        int mode = frm.getEvaluationMode();
        KrnObject[] objs = frm.getInitialObjs();
        boolean result = false;

        frm.clear();
        frm.getCash().reset(frm.getFlowId());
        Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
        objs = doBeforeOpen(frm, objs, objsMap);
                
        PanelAdapter pa = frm.getPanelAdapter();
        if (pa.isEnabled()) {
            if (mode == SPR_RO_MODE) {
                pa.setEnabled(pa.getRef() == null);
                frm.getSession().sendCommand("hideSend", "0");
                frm.getSession().sendCommand("hideSave", "0");
                frm.getSession().sendCommand("hideCancel", pa.getDataRef() == null ? "1" : "0");
            } else if (mode == ARCH_RW_MODE || mode == ARCH_RO_MODE) {
                pa.setEnabled(true);
                frm.getSession().sendCommand("hideSave", frm.getPanel().isPanelEnabled() ? "1" : "0");
                frm.getSession().sendCommand("hideSend", "0");
                frm.getSession().sendCommand("hideCancel", frm.getPanel().isPanelEnabled() ? "2" : "0");
            } else if (mode == SPR_RW_MODE){
            	pa.setEnabled(true);
            	frm.getSession().sendCommand("hideSend", "0");
            	frm.getSession().sendCommand("hideSave", "1");
            	frm.getSession().sendCommand("hideCancel", "1");
            } else if (mode == InterfaceManager.SERVICE_MODE){
                pa.setEnabled(true);
            	frm.getSession().sendCommand("hideSend", "1");
                frm.getSession().sendCommand("hideSave", "1");
                frm.getSession().sendCommand("hideCancel", "1");
            } else {
            	pa.setEnabled(false);
            }
        } else {
            pa.setEnabled(false);
        }
        if (pa.getRef() != null && pa.getRef().toString().equals("уд::осн::Поручение"))
        	frm.getSession().sendCommand("hideSend", "0");

        evaluateAllRefs(frm, objs, objsMap);
        if (/*!frm.getKernel().isADVANCED_UI() ||*/ mainUI == null || session.getFrameManager().getIndex() > 0) {
            showCurrent(mode, null);
        }
        result = true;
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        log.info("|USER: " + session.getUserName() + "| Время генерации интерфейса : " + elapsedTimeMillis / 1000F + " сек.");
        return result;
    }

    public boolean absolute(WebFrame frm, WebFrame parentFrm, KrnObject[] objs) throws KrnException {
        long start = System.currentTimeMillis();

        boolean result = false;
        if (frm != null && parentFrm != null) {
        	int mode = parentFrm.getEvaluationMode();
            frm.setCache(parentFrm.getCash());
            frm.setInterfaceLang(parentFrm.getIfcLang(), false);
            frm.setDataLang(parentFrm.getDataLang(), false);
            frm.setFlowId(parentFrm.getFlowId());
            frm.setEvaluationMode(mode);
            
            Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
            objs = doBeforeOpen(frm, objs, objsMap);

            PanelAdapter pa = frm.getPanelAdapter();
            if (pa.isEnabled()) {
            	if (mode == SPR_RO_MODE) {
                    pa.setEnabled(pa.getRef() == null);
                    frm.getSession().sendCommand("hideSend", "0");
                    frm.getSession().sendCommand("hideSave", "0");
                    frm.getSession().sendCommand("hideCancel", pa.getDataRef() == null ? "1" : "0");
                } else if (mode == ARCH_RW_MODE || mode == ARCH_RO_MODE) {
                    pa.setEnabled(true);
                    frm.getSession().sendCommand("hideSave", frm.getPanel().isPanelEnabled() ? "1" : "0");
                    frm.getSession().sendCommand("hideSend", "0");
                    frm.getSession().sendCommand("hideCancel", frm.getPanel().isPanelEnabled() ? "2" : "0");
                } else if (mode == SPR_RW_MODE){
                	pa.setEnabled(true);
                	frm.getSession().sendCommand("hideSend", "0");
                	frm.getSession().sendCommand("hideSave", "1");
                	frm.getSession().sendCommand("hideCancel", "1");
                } else if (mode == InterfaceManager.SERVICE_MODE){
                    pa.setEnabled(true);
                	frm.getSession().sendCommand("hideSend", "1");
                    frm.getSession().sendCommand("hideSave", "1");
                    frm.getSession().sendCommand("hideCancel", "1");
                } else {
                	pa.setEnabled(false);
                }
            } else {
                pa.setEnabled(false);
            }
            if (pa.getRef() != null && pa.getRef().toString().equals("уд::осн::Поручение"))
            	frm.getSession().sendCommand("hideSend", "0");

            evaluateAllRefs(frm, objs, objsMap);
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        log.info("|USER: " + session.getUserName() + "| Время генерации интерфейса : " + elapsedTimeMillis / 1000F + " сек.");
        return result;
    }

    private void showCurrent(int mode, String uiType){
        WebFrame frm = session.getFrameManager().getCurrentFrame();
        frm.setInterfaceLang(selectedIfcLang, false);
        frm.setDataLang(selectedDataLang, false);
        frm.setEvaluationMode(mode);
        doAfterOpen(frm);
    }

    private void evaluateAllRefs(WebFrame frm, KrnObject[] objs, Map<String, KrnObject[]> objsMap) throws KrnException {
    	String threadName = Thread.currentThread().getName();
    	try {
    		Thread.currentThread().setName(threadName + "(UI: " + frm.getObj().uid + ")");
	        Map<String, OrRef> contents = frm.getContentRef();
	        for (Iterator<OrRef> langIt = contents.values().iterator(); langIt.hasNext();) {
	            OrRef ref = langIt.next();
	            if (ref.getParent() == null && !ref.isHyperPopup())
	                try {
	                    ref.evaluate((KrnObject[]) null, this);
	                } catch (KrnException e) {
	                    log.error(e, e);
	                }
	        }
	        OrRef ref = frm.getRef();
	        Map<String, OrRef> refs = frm.getRefs();
	        for (Iterator<OrRef> langIt = refs.values().iterator(); langIt.hasNext();) {
	            OrRef chRef = langIt.next();
	            if (chRef.getParent() == null && (ref == null || !chRef.toString().equals(ref.toString())))
	                try {
	                    chRef.evaluate(objsMap.get(chRef.toString()), this);
	                } catch (KrnException e) {
	                    log.error(e, e);
	                }
	        }
	        if (ref != null)
	            ref.evaluate(objs, null);
	        if (ref != null)
	            ref.stateChanged(new OrRefEvent(ref, -1, -1, null));
	        frm.loadReports();//Переносим загрузку отчетов на момент когда данные подгружены и формулы будут выполняться
    	} finally {
    		Thread.currentThread().setName(threadName);
    	}
    }
    public CommitResult previous(boolean isShow, boolean canIgnore) throws KrnException {
        return previous(isShow, true, canIgnore);
    }
    public CommitResult previous(boolean isShow, boolean check, boolean canIgnore) throws KrnException {
        return previous(isShow, check, canIgnore, null, null);
    }
        
    public CommitResult previous(boolean isShow, boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException {
        CommitResult cr = CommitResult.CONTINUE_EDIT;

        WebFrameManager frameManager = session.getFrameManager();

        if (frameManager.hasPrev()) {
            int commitAction = 0;
            WebFrame frm = frameManager.getCurrentFrame();
            OrPanelComponent p = frm.getPanel();
            ASTStart template = p.getBeforeCloseTemplate();
            if (template != null) {
                ClientOrLang orlang = new ClientOrLang(frm);
                Map<String, Object> vc = new HashMap<String, Object>();
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                } catch (Exception ex) {
                    Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
                	log.error("Ошибка при выполнении формулы 'Действие перед закрытием' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                    log.error(ex, ex);
                } finally {
                    if (calcOwner)
                    	OrCalcRef.makeCalculations();
                    if (vc.get("RETURN") instanceof Number)
                    	commitAction = ((Number)vc.get("RETURN")).intValue();
                }
            }

            if (commitAction == 0) {
            	cr = frm.commitCurrent(new String[] {titleContinueEdit == null ? session.getResource().getString("continue") : titleContinueEdit, titleiIgnoreError == null ? session.getResource().getString("ignore") : titleiIgnoreError}, null, check, canIgnore);
            	if (cr == CommitResult.SESSION_REALESED) return cr;
            } else {
            	cr = CommitResult.WITHOUT_ERRORS;
            }
            if (!check || cr != CommitResult.CONTINUE_EDIT) {
                frm.clear();
                ASTStart templateCl = p.getAfterCloseTemplate();
                if (templateCl != null) {
                    ClientOrLang orlang = new ClientOrLang(frm);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
                    } catch (Exception ex) {
                        Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
                    	log.error("Ошибка при выполнении формулы 'Действие после закрытия' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                        log.error(ex, ex);
                    } finally {
                        if (calcOwner)
                        	OrCalcRef.removeCalculations();
                    }
                }
                frm = frameManager.prev();
                if (isShow) {
                    showCurrent(frameManager.getEvaluationMode(), "");
                }
                if (/*frm.getKernel().isADVANCED_UI() &&*/ mainUI != null && frameManager.getIndex() == 0) {
                    rollbackCurrent();
                }
            }
        }
        return cr;
    }

    public CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException {
        CommitResult cr = CommitResult.CONTINUE_EDIT;

        WebFrameManager frameManager = session.getFrameManager();
        int commitAction = 0;

        if (frameManager.hasPrev()) {
            WebFrame frm = frameManager.getCurrentFrame();
            OrPanelComponent p = frm.getPanel();
            ASTStart template = p.getBeforeCloseTemplate();
            if (template != null) {
                ClientOrLang orlang = new ClientOrLang(frm);
                Map<String, Object> vc = new HashMap<String, Object>();
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                } catch (Exception ex) {
                    Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
                	log.error("Ошибка при выполнении формулы 'Действие перед закрытием' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                    log.error(ex, ex);
                } finally {
                    if (calcOwner)
                    	OrCalcRef.makeCalculations();
                    
                    if (vc.get("RETURN") instanceof Number)
                    	commitAction = ((Number)vc.get("RETURN")).intValue();
                }
            }

            if (commitAction == 0) {
	            cr = frm.commitCurrent(new String[] {titleContinueEdit == null ? session.getResource().getString("continue") : titleContinueEdit, titleiIgnoreError == null ? session.getResource().getString("ignore") : titleiIgnoreError}, null, check, canIgnore);
	            if (!check || cr != CommitResult.CONTINUE_EDIT) {
	                ASTStart templateCl = p.getAfterCloseTemplate();
	                if (templateCl != null) {
	                    ClientOrLang orlang = new ClientOrLang(frm);
	                    Map<String, Object> vc = new HashMap<String, Object>();
	                    boolean calcOwner = OrCalcRef.setCalculations();
	                    try {
	                        orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
	                    } catch (Exception ex) {
	                        Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
	                    	log.error("Ошибка при выполнении формулы 'Действие после закрытия' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
	                        log.error(ex, ex);
	                    } finally {
	                        if (calcOwner)
	                        	OrCalcRef.makeCalculations();
	                    }
	                }
	            }
            } else {
                clearInterface(frm, false);
                return CommitResult.WITHOUT_ERRORS;
            }
        }
        return cr;
    }

    public boolean afterPrevious(boolean isShow, boolean check, CommitResult cr) throws KrnException {
    	boolean frameChanged = false;
        WebFrameManager frameManager = session.getFrameManager();
        if (frameManager.hasPrev() && (!check || cr != CommitResult.CONTINUE_EDIT)) {
            if (frameManager.getCurrentFrame() != null) {
                frameManager.getCurrentFrame().clear();
            }
            WebFrame oldFrm = frameManager.prev();
            frameChanged = true;
            if (isShow) {
            	if (oldFrm.equals(frameManager.getCurrentFrame()))
            		reopen(frameManager.getCurrentFrame());
            	else
            		showCurrent(frameManager.getEvaluationMode(), "");
            }
            if (/* frm.getKernel().isADVANCED_UI() && */mainUI != null && frameManager.getIndex() == 0) {
                rollbackCurrent();
            }
        }
        return frameChanged;
    }

    void rollbackCurrent() {
        WebFrameManager frameManager = session.getFrameManager();
        WebFrame frame = frameManager.getCurrentFrame();
        if (frame != null) {
            try {
                if (frame.getRef() != null) {
                	frame.getCash().rollback(frame.getFlowId());
                }
                frame.getPanelAdapter().clearFilterParam();
                
                Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
                KrnObject[] objs = doBeforeOpen(frame, null, objsMap);

                evaluateAllRefs(frame, objs, objsMap);

                doAfterOpen(frame);
            } catch (KrnException ex) {
                log.error(ex, ex);
            }
        }
    }

    public void performBeforeClose(String xml) {
        WebFrameManager frameManager = session.getFrameManager();
        WebFrame frm = frameManager.getCurrentFrame();
        OrPanelComponent p = frm.getPanel();
        ASTStart template = p.getBeforeCloseTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SIGNED_XML", xml);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Перед закрытием");
            	log.error("Ошибка при выполнении формулы 'Перед закрытием' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
        }
    }

    public String getStringToSign() {
        String ret = "";
        WebFrameManager frameManager = session.getFrameManager();
        WebFrame frm = frameManager.getCurrentFrame();
        OrPanelComponent p = frm.getPanel();
        ASTStart template = p.getCreateXmlTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Формирование XML");
            	log.error("Ошибка при выполнении формулы 'Формирование XML' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
            Object res = vc.get("RETURN");
            if (res instanceof Element) {
                try {
                    Format ft = Format.getRawFormat();
                    ft.setEncoding("UTF-8");
                    XMLOutputter f = new XMLOutputter(ft);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    f.output(new Document((Element) res), os);
                    os.close();
                    ret = new String(os.toByteArray(), "UTF-8");
                } catch (Exception e) {
                    log.error(e, e);
                }
            } else if (res instanceof String) {
                ret = (String) res;
            }
        }
        return ret;
    }

    public OrFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs, long tid, int mode, boolean shareCash, boolean fork, boolean isPopup)
            throws KrnException {
        WebFrameManager frameManager = session.getFrameManager();
        WebFrame oldFrm = frameManager.getCurrentFrame();
        OrFrame frm = getInterface(uiObj, objs, tid, mode, oldFrm.getFlowId(), shareCash, fork, isPopup);
        if (frm != null)
            frm.setInterfaceLang(selectedIfcLang, session.getResource());
        return frm;
    }

    public OrFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid, int mode, long flowId, boolean shareCash,
            boolean fork, boolean isPopup) throws KrnException {
        long start = System.currentTimeMillis();

        WebFrameManager frameManager = session.getFrameManager();
        WebFrame oldFrm = frameManager.getCurrentFrame();
        WebFrame frm = frameManager.absolute(uiObj, shareCash ? oldFrm : null);
        if (frm != null) {
            if (shareCash && oldFrm != null) {
                frm.setCache(oldFrm.getCash());
                oldFrm.getCash().setLogIfcId(frm.getInterfaceId());
        	} else {
        		frm.getCash().reset(flowId);
            }
            frm.setTransactionId(tid);
            frm.setInterfaceLang(selectedIfcLang, false);
            frm.setDataLang(selectedDataLang, false);
            frm.setFlowId(flowId);
            frm.setEvaluationMode(mode);
            
            if (!isPopup) {
	            if (mode == SPR_RO_MODE) {
	                frm.getSession().sendCommand("hideSend", "0");
	                frm.getSession().sendCommand("hideSave", "0");
	                frm.getSession().sendCommand("hideCancel", frm.getPanelAdapter().getDataRef() == null ? "1" : "0");
	            } else if (mode == SPR_RW_MODE) {
	                frm.getSession().sendCommand("hideSend", "0");
	                frm.getSession().sendCommand("hideSave", "1");
	                frm.getSession().sendCommand("hideCancel", "1");
	            } else if (mode == InterfaceManager.SERVICE_MODE){
	                frm.getSession().sendCommand("hideSend", "1");
	                frm.getSession().sendCommand("hideSave", "1");
	                frm.getSession().sendCommand("hideCancel", "1");
	            }
            }

            Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
            objs = doBeforeOpen(frm, objs, objsMap);

            if (!isPopup && frm.getPanelAdapter() != null && frm.getPanelAdapter().getRef() != null && frm.getPanelAdapter().getRef().toString().equals("уд::осн::Поручение"))
            	frm.getSession().sendCommand("hideSend", "0");
            
            evaluateAllRefs(frm, objs, objsMap);

            doAfterOpen(frm);
        }
        
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        log.info("Время генерации интерфейса : " + elapsedTimeMillis / 1000F + " сек.");
        return frm;
    }

    public void releaseInterface(boolean commit) {
        WebFrameManager frameManager = session.getFrameManager();
        WebFrame frm = frameManager.getCurrentFrame();
        OrPanelComponent p = frm.getPanel();
        ASTStart template = p.getBeforeCloseTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("COMMIT", commit);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
            	log.error("Ошибка при выполнении формулы 'Действие перед закрытием' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
        }
        clearInterface(frm, commit);
        ASTStart templateCl = p.getAfterCloseTemplate();
        if (templateCl != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
            	log.error("Ошибка при выполнении формулы 'Действие после закрытия' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
        }
        frameManager.prev();
    }

    public void clearInterface(WebFrame frm, boolean commit) {
        if (!frm.isSharedCache() && (frm.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            if (commit) {
                try {
                    frm.getCash().commit(frm.getFlowId());
                    frm.getRef().commitChanges(this);
                } catch (KrnException ex) {
                    log.error(ex, ex);
                    ((WebPanel) frm.getPanel()).setErrorMessage(Constants.ERROR_MESSAGE_1 + ex.getMessage(), false);
                }
            } else {
                try {
                    frm.getCash().rollback(frm.getFlowId());
                } catch (KrnException ex) {
                    log.error(ex, ex);
                    ((WebPanel) frm.getPanel()).setErrorMessage(Constants.ERROR_MESSAGE_1 + ex.getMessage(), false);
                }
            }
            frm.clear();
        } else {
            if (frm.isSharedCache()) {
            	if (commit)
            		frm.getCash().clearCacheChange(frm.getInterfaceId(), this);
            	else
            		frm.getCash().undoCacheChange(frm.getInterfaceId(), this);
                
            	frm.setCache(null);
            } else
                frm.clear();
        }

    }
    
    public void setLangId(long langId) {
        selectedIfcLang = LangHelper.getLangById(langId, session.getConfigNumber()).obj;
    }

    public void setDataLangId(long langId) {
        selectedDataLang = LangHelper.getLangById(langId, session.getConfigNumber()).obj;
    }

    private KrnObject[] doBeforeOpen(WebFrame frm, KrnObject[] objs, Map<String, KrnObject[]> objsMap) {
        OrPanelComponent p = frm.getPanel();
        ASTStart template = (p != null) ? p.getBeforeOpenTemplate() : null;
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm, true);
            Map<String, Object> vc = new HashMap<String, Object>();
            if (objs != null)
                vc.put("OBJS", Funcs.makeList(objs));
        	boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие перед открытием");
            	log.error("Ошибка при выполнении формулы 'Действие перед открытием' компонента '" + p.getClass().getName() + "', uuid: " + p.getUUID());
                log.error(ex, ex);
                // Если формула падает, то не грузим объекты (нужно добиться правильной работы формулы)
                return new KrnObject[0];
            } finally {
    			if (calcOwner)
    				OrCalcRef.removeCalculations();
            }

            KrnObject[] newObjs = null;
            Map<String, KrnObject[]> cobjsMap = orlang.getObjsMap();
            if (cobjsMap != null && frm.getRef() != null)
            	newObjs = cobjsMap.get(frm.getRef().toString());
            
            if (newObjs == null) {
            	List<KrnObject> objList = (List<KrnObject>) vc.get("OBJS");
	            if (objList != null) {// && objList.size()>0) {
	            	newObjs = objList.toArray(new KrnObject[objList.size()]);
	            }
            }
            
            if (newObjs != null)
            	objs = newObjs;
            
            if (cobjsMap != null)
                objsMap.putAll(cobjsMap);
        }
        return objs;
    }

    public void doAfterOpen(WebFrame frm) {
        OrPanelComponent p = frm.getPanel();
        ASTStart template = (p != null) ? p.getAfterOpenTemplate() : null;
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations(); 
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после открытия");
            	log.error("Ошибка при выполнении формулы 'Действие после открытия' компонента '" + p.getClass().getName() + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
            	if (calcOwner)
            		OrCalcRef.removeCalculations();
            }
        }
    }

    public WebFrame getMainUI() {
        return mainUI;
    }

    public void setMainUI(WebFrame mainUI) {
        this.mainUI = mainUI;
    }

    // Состояние кнопки "Отменить"
    private boolean rollbackEnabled = false;

    public void enableRollback(boolean b) {
        rollbackEnabled = b;
    }

    public boolean isRollbackEnabled() {
        return rollbackEnabled;
    }
}
