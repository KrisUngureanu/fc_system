package kz.tamur.rt;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.FrameTemplate;
import kz.tamur.util.Funcs;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.SortedFrame;

public class InterfaceFrame extends FrameTemplate implements InterfaceManager, ActionListener {

	private FrameManager frameManager = new FrameManager(this);
	private InterfaceManager parent;
	private JLabel label = new JLabel();
	private JProgressBar progress = new JProgressBar();
	
    private static ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private ToolButton applyBtn = new ToolButton("Apply32", res.getString("applyChanges"), label);
    private ToolButton rollbackBtn = new ToolButton("Cancel32", res.getString("cancelChanges"), label);
    private ToolButton prevBtn = new ToolButton("BackPage32", res.getString("backPage"), label);
    private ToolButton printBtn = new ToolButton("Printer32", res.getString("print"), label);

	public InterfaceFrame(KrnObject uiObj, KrnObject[] objs, InterfaceManager parent) throws KrnException {
		this.parent = parent;
		UIFrame frm = frameManager.absolute(uiObj, null, progress, label,parent.getInterfaceLang());
        if (frm != null) {
            frm.setTransactionId(0);
            frm.setInterfaceLang(parent.getInterfaceLang(), false);
            frm.setDataLang(parent.getDataLang(), false);
            //frm.setEvaluationMode(mode);

            objs = doBeforeOpen(frm, objs);

            evaluateAllRefs(frm, objs);
            
            doAfterOpen(frm);
            
            setContentPane(frm.getPanel());
        }
	}

	private KrnObject[] doBeforeOpen(UIFrame frm, KrnObject[] objs) {
		OrPanel p = frm.getPanel();
		ASTStart template = p.getBeforeOpenTemplate();
		if (template != null) {
			ClientOrLang orlang = new ClientOrLang(frm);
			Map<String, Object> vc = new HashMap<String, Object>();
			if (objs != null)
				vc.put("OBJS", Funcs.makeList(objs));
			try {
				orlang.evaluate(template, vc, frm.getPanelAdapter(),
						new Stack<String>());
			} catch (Exception ex) {
				Util.showErrorMessage(p, ex.getMessage(),
						"Действие перед открытием");
			}
			List<KrnObject> objList = (List<KrnObject>) vc.get("OBJS");
			if (objList != null) {// && objList.size()>0) {
				objs = objList.toArray(new KrnObject[objList.size()]);
			}
		}
		return objs;
	}

	private void doAfterOpen(UIFrame frm) {
        OrPanel p = frm.getPanel();
        if (p != null) {
	        ASTStart template = p.getAfterOpenTemplate();
	        if (template != null) {
	            ClientOrLang orlang = new ClientOrLang(frm);
	            Map vc = new HashMap();
	            try {
	                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
	            } catch (Exception ex) {
	                Util.showErrorMessage(p, ex.getMessage(), "Действие после открытия");
	            }
	        }
        }
	}

    private void evaluateAllRefs(UIFrame frm, KrnObject[] objs) throws KrnException {
        Map contents = frm.getContentRef();
        OrRef ref = frm.getRef();
        Map refs = frm.getRefs();

        contents = frm.getContentRef();
        for (Iterator langIt = contents.values().iterator(); langIt.hasNext();) {
            ref = (OrRef) langIt.next();
            if (ref.getParent() == null && !ref.isHyperPopup())
                try {
                    ref.evaluate((KrnObject[]) null, this);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
        }
        ref = frm.getRef();
        refs = frm.getRefs();
        for (Iterator langIt = refs.values().iterator(); langIt.hasNext();) {
            OrRef chRef = (OrRef) langIt.next();
            if (chRef.getParent() == null && (ref == null || !chRef.toString().equals(ref.toString())))
                try {
                    chRef.evaluate((KrnObject[]) null, this);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
        }
        if (ref != null)
            ref.evaluate(objs, null);
        if (ref != null)
            ref.stateChanged(new OrRefEvent(ref, -1, -1, null));

    }

	public boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath,
			int mode, boolean isHiperTree, long tid, long flowId,
			boolean isBlockErrors, String uiType) throws KrnException {
		return false;
	}

	public Cache getCash() {
        if (frameManager != null) {
            UIFrame frame = frameManager.getCurrentFrame();
            if (frame != null) {
                return frame.getCash();
            }
        }
        return null;
	}

	public UIFrame getCurrentInterface() {
        return frameManager.getCurrentFrame();
	}

	public KrnObject getDataLang() {
        return frameManager.getCurrentFrame().getDataLang();
	}

	public int getEvaluationMode() {
        return (frameManager.getCurrentFrame() != null) ? frameManager.getCurrentFrame().getEvaluationMode() : 0;
	}

	public UIFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid,
			int mode, long flowId, boolean shareCash, boolean fork)
			throws KrnException {

    	FrameManager fm = frameManager;
        UIFrame oldFrm = fm.getCurrentFrame();
        UIFrame frm = fm.absolute(uiObj, shareCash ? oldFrm : null, progress, label,oldFrm.getIfcLang());
        
        if (frm != null) {
        	if (shareCash && oldFrm != null) {
        		frm.setCache(oldFrm.getCash());
                oldFrm.getCash().setLogIfcId(frm.getObj().id);
        	} else {
        		frm.getCash().reset(flowId);
        	}
            frm.setTransactionId(tid);
            frm.setInterfaceLang(parent.getInterfaceLang(), false);
            frm.setDataLang(parent.getDataLang(), false);
            frm.setFlowId(flowId);
            frm.setEvaluationMode(mode);

            objs = doBeforeOpen(frm, objs);

            evaluateAllRefs(frm, objs);
            
            doAfterOpen(frm);
        }
        return frm;
	}

	public KrnObject getInterfaceLang() {
        return frameManager.getCurrentFrame().getIfcLang();
	}

	public UIFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs,
			long tid, int mode, boolean shareCash, boolean fork)
			throws KrnException {
        UIFrame oldFrm = frameManager.getCurrentFrame();
        UIFrame frm = getInterface(uiObj, objs, tid, mode, oldFrm.getFlowId(), shareCash, fork);
        return frm;
	}

	public Kernel getKernel() {
		return Kernel.instance();
	}

	public void releaseInterface(boolean commit) {
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
        // Переход на предыдущий интерфейс
        if (src == prevBtn) {
            //previous();
            setCursor(Constants.DEFAULT_CURSOR);
        }
        // Сохранение изменений
        else if (src == applyBtn) {
//            if (oldCursor.getName().equals("helpcursor")) {
            //commitCurrent(new String[]{res.getString("continue"),
            //                           res.getString("save")});
        }
        // Отмена изменений
        else if (src == rollbackBtn) {
        	/*
//            if (oldCursor.getName().equals("helpcursor")) {
            int res1 = MessagesFactory.showMessageDialog(this,
                    MessagesFactory.QUESTION_MESSAGE,
                    res.getString("cancelChanges") + "?", parent.getIn);
            if (res1 == ButtonsFactory.BUTTON_YES) {
                rollbackCurrent();
            }
            */
        }
        // печать документов
        else if (src == printBtn) {
        	/*
            UIFrame frame = frameManager.getCurrentFrame();
            if (frame != null) {
                if (docMenu == null) {
                    docMenu = new JPopupMenu();
                    ReportRecord root = frame.getRootReport();
                    loadReports(root, docMenu);
                }
                docMenu.show(printBtn, 0, printBtn.getHeight());
            }
            */
        }
	}
	
    private int commitCurrent(String[] options) throws KrnException {
        int result = 1;
        UIFrame frm = frameManager.getCurrentFrame();
        if (frm != null && frm.getRef() != null && frm.getRef().getType() != null &&
                (frm.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            try {
                ReqMsgsList msg = frm.getRef().canCommit();
                if (msg.getListSize() > 0) {
                    SortedFrame dlg = new SortedFrame(this, res.getString("errors"));
                    msg.setParent(dlg);
                    dlg.setOption(options);
                    dlg.setContent(msg);
                    dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
                    dlg.show();
                    result = dlg.getResult();
                }
                if (result != BUTTON_NOACTION && result == BUTTON_OK) {
                	frm.getCash().commit(frm.getFlowId());
                    frm.getRef().commitChanges(this);
                    if (frm.getFlowId() > 0) {
                        if (msg.hasFatalErrors()) {
                            Kernel.instance().setPermitPerform(frm.getFlowId(), false);
                            TaskTable.instance(false).setPermitPerform(false);
                        } else {
                            Kernel.instance().setPermitPerform(frm.getFlowId(), true);
                            TaskTable.instance(false).setPermitPerform(true);
                        }
                        List<OrRef.Item> a_sel = frm.getRef().getSelectedItems();
                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
                        for (int i = 0; i < a_sel.size(); i++) {
                            OrRef.Item item = a_sel.get(i);
                            selObjs[i] = (KrnObject) item.getCurrent();
                        }
                        Activity act_=TaskTable.instance(false).getSelectedActivity();
                        if(act_!=null){
                            Kernel.instance().setSelectedObjects(act_.flowId,act_.nodesId[0][act_.nodesId[0].length-1], selObjs);
                            TaskTable.instance(false).taskReload(act_.flowId,!"-1".equals(act_.infUi) && !"-1".equals(act_.ui) ?2:!"-1".equals(act_.infUi)?1:!"-1".equals(act_.ui)?0:-1);
                        }
                    }
                }
            } catch (KrnException ex) {
                Container container = (Frame)InterfaceManagerFactory.instance().getManager();
                MessagesFactory.showMessageDialogBig((Frame)container, MessagesFactory.ERROR_MESSAGE,
                            "Ошибка при сохранении интерфейса!\r\n" + ex.getMessage());

                ex.printStackTrace();
                MessagesFactory.showMessageDialog( this, MessagesFactory.ERROR_MESSAGE,Constants.ERROR_MESSAGE_1 + ex.getMessage());
            }
        }
        return result;
    }


    private void rollbackCurrent() {
        UIFrame frame = frameManager.getCurrentFrame();
        if (frame != null) {
            try {
                if (frame.getRef() != null) {
                    Map contents = frame.getContentRef();
                    for (Iterator langIt = contents.values().iterator(); langIt.hasNext();) {
                        OrRef chRef = (OrRef) langIt.next();
                        if (chRef.getParent() == null)
                            try {
                                chRef.rollback(this, frame.getFlowId());
                            } catch (KrnException e) {
                                e.printStackTrace();
                            }
                    }

                    OrRef ref = frame.getRef();
                    Map refs = frame.getRefs();
                    for (Iterator langIt = refs.values().iterator(); langIt.hasNext();) {
                        OrRef chRef = (OrRef) langIt.next();
                        if (chRef.getParent() == null && !chRef.toString().equals(ref.toString()))
                            try {
                                chRef.rollback(this, frame.getFlowId());
                            } catch (KrnException e) {
                                e.printStackTrace();
                            }
                    }

                    frame.getRef().rollback(this, frame.getFlowId());
                }
                frame.getPanelAdapter().clearFilterParam();
                
                doAfterOpen(frame);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException {
        return CommitResult.CONTINUE_EDIT;
    }

    @Override
    public CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException {
        return beforePrevious(check, canIgnore, null, null);
    }

    @Override
    public CommitResult beforePrevious() throws KrnException {
        return beforePrevious(true, true);
    }

    @Override
    public void afterPrevious(boolean isShow, boolean check, CommitResult cr) throws KrnException {}
}
