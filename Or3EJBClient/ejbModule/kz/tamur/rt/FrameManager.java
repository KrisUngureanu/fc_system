package kz.tamur.rt;

import com.cifs.or2.kernel.KrnObject;
import kz.tamur.rt.adapters.UIFrame;
import javax.swing.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 21.04.2004
 * Time: 18:44:51
 * To change this template use File | Settings | File Templates.
 */
public class FrameManager {

    private Map<Long, UIFrame> byId = new HashMap<Long, UIFrame>();

    private Stack<UIFrame> frames = new Stack<UIFrame>();
    private InterfaceManager mgr;

    public FrameManager(InterfaceManager mgr) {
    	this.mgr = mgr;
    }

    public UIFrame createFrame(KrnObject obj, UIFrame oldFrm, JProgressBar progress, JLabel label,KrnObject lang) {
        UIFrame res = byId.get(obj.id);
        if (res == null) {
            res = new UIFrame(mgr, obj, oldFrm, progress, label,lang);
            if (res.isLoaded()) byId.put(obj.id, res);
            else return null;
        } else {
        	res.getObj().id = obj.id;
        }
        return res;
    }

    public UIFrame getCurrentFrame() {
        return frames.isEmpty() ? null : frames.peek();
    }
    
    public UIFrame absolute(KrnObject uiObj, UIFrame oldFrm, JProgressBar progress, JLabel label,KrnObject lang) {
        UIFrame frm = createFrame(uiObj, oldFrm, progress, label,lang);
        if (frm != null) {
        	frames.push(frm);
        	
        }
        return frm;
    }

    public boolean hasPrev() {
        return frames.size() > 1;
    }

    public UIFrame prev() {
        UIFrame frm = frames.pop();
        UIFrame curFrame = getCurrentFrame();
        if (curFrame != null) {
        	if (curFrame.isSharedCache())
        		curFrame.getCash().setLogIfcId(curFrame.getObj().id);
        	else
        		curFrame.getCash().setLogIfcId(-1);
        }
        return frm;
    }

    public int getIndex() {
        return frames.size() - 1;
    }

    public int getIndex(UIFrame frame) {
        return frames.indexOf(frame);
    }
    
    /**
     * @return the frames
     */
    public Stack<UIFrame> getFrames() {
        return frames;
    }
}
