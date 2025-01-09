package kz.tamur.guidesigner;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;

import javax.swing.tree.TreeNode;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Utils;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.FrameTemplate;

import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 19.10.2004
 * Time: 9:32:29
 */
public class NodeFinder implements PropertyChangeListener {
    private Stack<Position> positions = new Stack<Position>();
    private FindPattern pattern;
    private TreeNode lastMatch = null;
    private TreeNode root = null;
    private boolean search = true;
    private FrameTemplate win = null;

    public NodeFinder() {
        this(true);
    }

    public NodeFinder(boolean init) {
        super();
        if (init) {
            win = Utils.isDesignerRun() ? Or3Frame.instance() : (MainFrame) InterfaceManagerFactory.instance().getManager();
            win.addPropertyChangeListener(this);
        }
    }

    public TreeNode findFirst(TreeNode node, FindPattern pattern) {
        this.pattern = pattern;
        root = node;
        lastMatch = null;
        positions.clear();
        positions.add(new Position(node, -1));
        return findNext();
    }

    public TreeNode findNext() {
        if (win != null) {
            win.setState(FrameTemplate.STATE_FIND);
            CursorToolkit.startWaitCursor(win);
        }
        if (positions.isEmpty() && root != null)
            positions.add(new Position(root, -1));

        while (!positions.isEmpty() && search) {
            Position pos = (Position) positions.peek();
            if (pos.index == -1) {
                if (pattern.isMatches(pos.n) && pos.n != lastMatch) {
                    lastMatch = pos.n;
                    CursorToolkit.stopWaitCursor(win);
                    if (win != null) win.setState(FrameTemplate.STATE_DEF);
                    return pos.n;
                }
                pos.index = 0;
            }
            if (pos.n.getChildCount() > pos.index) {
                TreeNode child = pos.n.getChildAt(pos.index);
                positions.add(new Position(child, -1));
            } else {
                positions.pop();
                if (!positions.isEmpty()) {
                    pos = (Position) positions.peek();
                    pos.index++;
                }
            }
        }
        search = true;
        if (win != null) {
            CursorToolkit.stopWaitCursor(win);
            win.setState(FrameTemplate.STATE_DEF);
        }
        return null;
    }
    
    /**
     * @param search
     * для выполнения поиска классов с первого раза
     */
    public void setSearchMode(boolean search) {
    	this.search = search;
    }
    
    public TreeNode findPrev() {
    	if (win != null) win.setState(FrameTemplate.STATE_FIND);
        CursorToolkit.startWaitCursor(win);
        if (positions.isEmpty() && root != null)
            positions.add(new Position(root, root.getChildCount() - 1));

        while (!positions.isEmpty() && search) {
            Position pos = (Position) positions.peek();
            if (pos.index == -1) {
                if (pattern.isMatches(pos.n) && pos.n != lastMatch) {
                    lastMatch = pos.n;
                    CursorToolkit.stopWaitCursor(win);
                    if (win != null) win.setState(FrameTemplate.STATE_DEF);
                    return pos.n;
                }
            }
            if (pos.index >= 0 && pos.n.getChildCount() > 0) {
                TreeNode child = pos.n.getChildAt(pos.index);
                positions.add(new Position(child, child.getChildCount() - 1));
            } else {
                positions.pop();
                if (!positions.isEmpty()) {
                    pos = (Position) positions.peek();
                    pos.index--;
                }
            }
        }
        search = true;
        CursorToolkit.stopWaitCursor(win);
        if (win != null) win.setState(FrameTemplate.STATE_DEF);
        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("stopSearch")) {
            search = false;
        }
    }

    private static final class Position {
        public TreeNode n;
        public int index;

        public Position(TreeNode n, int index) {
            this.n = n;
            this.index = index;
        }
    }
}
