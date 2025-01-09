package kz.tamur.guidesigner.scheduler;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;

import javax.swing.tree.TreeNode;
import java.util.Vector;
import java.util.Map;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 14.06.2005
 * Time: 11:05:52
 * To change this template use File | Settings | File Templates.
 */
public class TimerNode implements TreeNode {
    TimerObject     timer;
    TreeNode parent;
    Vector<TreeNode> children;
    Map timers;
    static public final Enumeration EMPTY_ENUMERATION
	= new Enumeration() {
	    public boolean hasMoreElements() { return false; }
	    public Object nextElement() {
		throw new NoSuchElementException("No more elements");
	    }
    };

    public TimerNode(TimerObject timer,Map timers) {
	this.timer = timer;
    this.timers = timers;
    this.parent=null;
    }

    // Used to sort the file names.
 //   static private MergeSort  fileMS = new MergeSort() {
//	public int compareElementsAt(int a, int b) {
//	    return ((String)toSort[a]).compareTo((String)toSort[b]);
//	}
 //   };

    /**
     * Returns the the string to be used to display this leaf in the JTree.
     */
    public String toString() {
	return timer.toString();
    }

    public TimerObject getTimer() {
	return timer;
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() {
        if (children != null) {
            return children.toArray();
        }
        try {
            KrnObject[] objs = Kernel.instance().getObjects(timer.obj,"children",0);
            children = new Vector<TreeNode>();
            if(objs.length>0) {
                for (KrnObject obj : objs) {
                    TimerObject child = (TimerObject) timers.get(obj.id);
                    TimerNode node = new TimerNode(child, timers);
                    node.setParent(this);
                    children.add(node);
                }
            }
        } catch (KrnException e) {
                e.printStackTrace();
        }
        return children.toArray();
    }

    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration children() {
        if (children == null) {
            return EMPTY_ENUMERATION;
        } else {
            return children.elements();
        }
    }

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode getChildAt(int childIndex) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.elementAt(childIndex);
    }

    public int getIndex(TreeNode aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isNodeChild(aChild)) {
            return -1;
        }
        return children.indexOf(aChild);	// linear search
    }

    public void setParent(TreeNode newParent) {
	parent = newParent;
    }

    public boolean isNodeChild(TreeNode aNode) {
	boolean retval;

	if (aNode == null) {
	    retval = false;
	} else {
	    if (getChildCount() == 0) {
		retval = false;
	    } else {
		retval = (aNode.getParent() == this);
	    }
	}

	return retval;
    }
    public void remove(TimerNode aChild) {
	if (aChild == null) {
	    throw new IllegalArgumentException("argument is null");
	}

	if (!isNodeChild(aChild)) {
	    throw new IllegalArgumentException("argument is not a child");
	}
	remove(getIndex(aChild));	// linear search
    }
    public void remove(int childIndex) {
	TimerNode child = (TimerNode)getChildAt(childIndex);
	children.removeElementAt(childIndex);
	child.setParent(null);
    }

    public void insert(TimerNode newChild, int childIndex) {
	    TimerNode oldParent = (TimerNode)newChild.getParent();

	    if (oldParent != null) {
		oldParent.remove(newChild);
	    }
	    newChild.setParent(this);
	    if (children == null) {
		children = new Vector<TreeNode>();
	    }
	    children.insertElementAt(newChild, childIndex);
    }
    public TimerObject getObject(){
        return timer;
    }
}