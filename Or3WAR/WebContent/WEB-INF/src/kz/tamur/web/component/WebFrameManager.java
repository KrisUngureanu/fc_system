package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.web.common.CommonHelper;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.common.WebInterfaceManager;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;
import kz.tamur.comps.OrFrame;
import kz.tamur.or3ee.common.UserSession;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 12.07.2006
 * Time: 11:07:26
 * To change this template use File | Settings | File Templates.
 */
public class WebFrameManager implements InterfaceManager {

    private Stack<WebFrame> frames = new Stack<WebFrame>();
    private Map<Long, WebFrame> byId = new HashMap<Long, WebFrame>();
    private Map<String, WebFrame> byPanel = new HashMap<String, WebFrame>();
    private WebSession session;
    private Log log;
	private WebFrame waitingFrame;
	

    public WebFrameManager(WebSession session) {
        this.session = session;
        this.log = WebSessionManager.getLog(session.getKernel().getUserSession().dsName, session.getKernel().getUserSession().logName);
    }

    @Override
    public WebFrame getCurrentFrame() {
        return frames.isEmpty() ? null : frames.peek();
    }

    public WebFrame getPreviousFrame() {
        if (frames.size() < 2) {
            log.info("getPreviousFrame: frames size(): " + frames.size());
            return null;
        }
        synchronized (frames) {
            WebFrame curFrame = frames.pop();
            WebFrame res = frames.peek();
            frames.push(curFrame);
            log.info("getPreviousFrame: frames size(): " + frames.size());
            return res;
        }
    }

    public WebFrame absolute(KrnObject uiObj, WebFrame oldFrm) {
        WebFrame frm = createFrame(uiObj, oldFrm);
        if (frm != null && !(frames.size() == 2 && frames.peek().equals(frm))) {
            frames.push(frm);
        }
        log.info("absolute: frames size(): " + frames.size());
        return frm;
    }

    /**
     * Метод реализует аналогичен {@link WebFrameManager#absolute} но не записывает в стек вызванный интерфейс
     * 
     * @param uiObj
     * @param oldFrm
     * @return
     */
    public WebFrame absolute2(KrnObject uiObj, WebFrame oldFrm) {
        return createFrame(uiObj, oldFrm);
    }

    public boolean hasPrev() {
        log.info("hasPrev: frames size(): " + frames.size());
        if (session.isForPublicUser())
            return frames.size() > 2;
        else
            return frames.size() > 1;
    }

    public WebFrame prev() {
        WebFrame frm = frames.pop();
        if (frm != null) {
            ((OrWebPanel) frm.getPanel()).removeChangeProperties();
        }

        WebFrame curFrame = getCurrentFrame();
        if (curFrame != null) {
            if (curFrame.isSharedCache())
                curFrame.getCash().setLogIfcId(curFrame.getInterfaceId());
            else
                curFrame.getCash().setLogIfcId(-1);
        }

        log.info("prev: frames size(): " + frames.size());

        return frm;
    }

    public int getIndex() {
        return frames.size() - 1;
    }

    public int getIndex(WebFrame frame) {
        return frames.indexOf(frame);
    }

    public WebFrame getFrame(int index) {
        WebFrame frm = getCurrentFrame();
        if (frm != null) {
            while (frames.size() > index) {
                if (frm != null) {
                    frm.commit(0);
                }
                prev();
                frm = getCurrentFrame();
            }
        }
        return frm;
    }

    public WebFrame createFrame(KrnObject obj, WebFrame oldFrm) {
        log.info("|USER: " + session.getUserName() + "| before open ifc: uid = " + obj.uid + ", id = " + obj.id);
        WebFrame res = byId.get(new Long(obj.id));
        if (res == null) {
            res = new WebFrame(obj, oldFrm, session);
            if (obj.id != 0) {
                boolean loaded = res.load();
                if (loaded) {
                    byId.put(new Long(obj.id), res);
                } else {
                    res = null;
                }
                CommonHelper.takeMemorySnapshot(session);
            } else {
                byId.put(new Long(obj.id), res);
            }
        } else {
            res.getObj().id = obj.id;
            res.getObj().uid = obj.uid;
        }
        return res;
    }

    public WebFrame createChildFrame(KrnObject obj, WebFrame parentFrm) {
        log.info("|USER: " + session.getUserName() + "| before load ifc: uid = " + obj.uid + ", id = " + obj.id);
        WebFrame res = byId.get(new Long(obj.id));
        if (res == null) {
            res = new WebFrame(obj, parentFrm, session);
            if (obj.id != 0) {
            	res.setRefs(parentFrm.getRefs());
            	res.setContentRef(parentFrm.getContentRef());
            	res.setRefGroups(parentFrm.getRefGroups());

            	boolean loaded = res.load(parentFrm);
                if (loaded) {
                    byId.put(new Long(obj.id), res);
                } else {
                    res = null;
                }
                CommonHelper.takeMemorySnapshot(session);
            } else {
                byId.put(new Long(obj.id), res);
            }
        } else {
            res.getObj().id = obj.id;
            res.getObj().uid = obj.uid;
        }
        return res;
    }

    public CommitResult beforePrevious() throws KrnException {
        return beforePrevious(true, true, null, null);
    }

    public CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException {
        return beforePrevious(check, canIgnore, null, null);
    }

    public CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit,
            String titleiIgnoreError) throws KrnException {
        session.getTaskHelper().setAutoAct(true);
        WebInterfaceManager ifcMgr = session.getInterfaceManager();
        CommitResult res = ifcMgr.beforePrevious(check, canIgnore, titleContinueEdit, titleiIgnoreError);
        return res;
    }

    public boolean afterPrevious(boolean isShow, boolean check, boolean isSend, CommitResult cr) throws KrnException {
        WebInterfaceManager ifcMgr = session.getInterfaceManager();
        boolean frameChanged = ifcMgr.afterPrevious(isShow, check, cr);
        if (cr != CommitResult.CONTINUE_EDIT) {
            session.setOpenPrev(true);
            if (isSend) {
                session.sendCommand("prevUI", session.getFrameManager().hasPrev() ? 1 : 0);
            }
            session.getTaskHelper().setOpenUI(null);
        }
        return frameChanged;
    }

    public boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath, int mode, boolean isHiperTree, long tid,
           boolean shareCash, long flowId, boolean isBlockErrors, String uiType) throws KrnException {
        WebInterfaceManager ifcMgr = session.getInterfaceManager();
        return ifcMgr.absolute(null, uiObj, objs, refPath, mode, isHiperTree, tid, shareCash, flowId, isBlockErrors, uiType);
    }

    public Map<String, OrRef> getRefs() {
        return getCurrentFrame().getRefs();
    }

    public Map<String, OrRef> getContentRefs() {
        return Collections.emptyMap();
    }

    public Cache getCash() {
        WebFrame frame = getCurrentFrame();
        if (frame != null) {
            return frame.getCash();
        }
        return null;
    }

    public int getEvaluationMode() {
        return (getCurrentFrame() != null) ? getCurrentFrame().getEvaluationMode() : 0;
    }

    public boolean isCreatingAttr(String path) {
        return false;
    }

    public OrFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid, int mode, long flowId, boolean shareCash,
            boolean fork) throws KrnException {
        return null;
    }

    public OrFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs, long tid, int mode, boolean shareCash, boolean fork, boolean isPopup)
            throws KrnException {
        WebInterfaceManager ifcMgr = session.getInterfaceManager();
        return ifcMgr.getInterfacePanel(uiObj, objs, tid, mode, shareCash, fork, isPopup);
    }

    public void releaseInterface(boolean commit) {
        WebInterfaceManager ifcMgr = session.getInterfaceManager();
        ifcMgr.releaseInterface(commit);
    }

    public KrnObject getInterfaceLang() {
        return getCurrentFrame().getIfcLang();
    }

    public KrnObject getDataLang() {
        return getCurrentFrame().getDataLang();
    }

    public void clearFrame() {
        while (frames.size() > 1) {
            WebInterfaceManager ifcMgr = session.getInterfaceManager();
            ifcMgr.releaseInterface(false);
        }
        log.info("clearFrame: frames size(): " + frames.size());
    }

    public void clearFrame2() {
        while (frames.size() > 0) {
            frames.pop();
        }
        log.info("clearFrame2: frames size(): " + frames.size());
    }

    public Kernel getKernel() {
        return session.getKernel();
    }

    public WebFrame getFrameById(String id) {
        return byId.get(new Long(id));
    }

    public WebFrame getFrameByPanelUid(String uid) {
        return byPanel.get(uid);
    }

    public void putFrameByPanelUid(String uid, WebFrame frm) {
        byPanel.put(uid, frm);
    }

    /**
     * @return the frames
     */
    public Stack<WebFrame> getFrames() {
        return frames;
    }

    public void release() {
        for (WebFrame f : byId.values()) {
            f.release();
        }
        for (WebFrame f : byPanel.values()) {
            f.release();
        }
        byId.clear();
        byPanel.clear();
        frames.clear();
        frames = null;
        byId = null;
        byPanel = null;
    }

    /**
     * Получить весь текущий стек открытых интерфейсов.
     * Метод возвращает JSON-массив JSON-объектов.
     * В каждом объекте: ID интерфейса и его заголовок.
     * 
     * @return массив идентификаторов открытых интерфейсов..
     */
    public JsonArray getStackFrames() {
        JsonArray stack = new JsonArray();
        Iterator<WebFrame> iter = frames.iterator();
        if (iter.hasNext()) iter.next(); // Не надо показывать в хлебных крошках главное окно
        WebFrame frame;
        JsonObject obj;
        while (iter.hasNext()) {
            frame = iter.next();
            if (frame != null) {
                obj = new JsonObject();
                obj.add("id", frame.getInterfaceId());
                obj.add("title", getTitleFrame(frame));
                stack.add(obj);
            }
        }
        return stack;
    }

    /**
     * Получить заголовок интерфейса.
     * 
     * @param frame
     *            интерфейс.
     * @return заголовок интерфейса.
     */
    private String getTitleFrame(WebFrame frame) {
        return frame.getPanel() == null ? "" : frame.getPanel().getTitle();
    }
    
    public void setInterfaceLang(KrnObject lang, boolean withReloading) {
    	for (long id : byId.keySet()) {
    		WebFrame frame = byId.get(id);
    		if (frame != null)
    			frame.setInterfaceLang(lang, withReloading);
    	}
    }

	public void setWaitingFrame(WebFrame frame) {
		this.waitingFrame = frame;
	}

	public WebFrame getWaitingFrame() {
		return waitingFrame;
	}
}
