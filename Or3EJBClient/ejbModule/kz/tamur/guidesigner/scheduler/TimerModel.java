package kz.tamur.guidesigner.scheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 08.06.2005
 * Time: 17:50:44
 * To change this template use File | Settings | File Templates.
 */
import java.util.Map;

import javax.swing.tree.TreeNode;

import other.treetable.AbstractTreeTableModel;
import other.treetable.TreeTableModel;

import com.cifs.or2.client.Kernel;

import kz.tamur.comps.Constants;

/**
 * FileSystemModel is a TreeTableModel representing a hierarchical file
 * system. Nodes in the FileSystemModel are FileNodes which, when they
 * are directory nodes, cache their children to avoid repeatedly querying
 * the real file system.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class TimerModel extends AbstractTreeTableModel
                             implements TreeTableModel {

    // Names of the columns.
	static protected String[] cNames = { "Наименование задания", "Статус выполнения", "Пользователь", "Процессы", "Месяцы", "Дни месяца", "Дни недели", "Час", "Минута", "Время старта", "Время завершения", "Ошибка" };
    // Types of the columns.
    static protected Class[]  cTypes = {TreeTableModel.class,String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};

    // The the returned file length for directories.
//    public static final Integer ZERO = new Integer(0);

    public TimerModel(TimerObject root,Map timers) {
	super(new TimerNode(root,timers));
    }

    //
    // Some convenience methods.
    //

    protected TimerObject getTimer(Object node) {
	TimerNode timerNode = ((TimerNode)node);
	return timerNode.getTimer();
    }

    protected Object[] getChildren(Object node) {
	TimerNode timerNode = ((TimerNode)node);
	return timerNode.getChildren();
    }

    //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
	Object[] children = getChildren(node);
	return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
	return getChildren(node)[i];
    }

    // The superclass's implementation would work, but this is more efficient.
    public boolean isLeaf(Object node) {
        return getTimer(node).obj.classId==Kernel.SC_TIMER.id;
    }

    //
    //  The TreeTableNode interface.
    //

    public int getColumnCount() {
	return cNames.length;
    }

    public String getColumnName(int column) {
	return cNames[column];
    }

    public Class getColumnClass(int column) {
	return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
		TimerObject timer = getTimer(node);
		try {
		    switch(column) {
		        case 0:
			        return timer;
		        case 1:
			        return timer.obj.classId==Kernel.SC_TIMER.id?(timer.redy>Constants.TIMER_NOT_ACTIVE?"Выполнять":"Не выполнять"):"";
		        case 2:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.user!=null?timer.user.title:""):"";
		        case 3:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.srvs!=null?getObjArrayToStr(timer.srvs.toArray()):""):"";
	            case 4:
	                if(timer.obj.classId==Kernel.SC_TIMER.id){
	                    if (timer.monthCol!=null){
	                        return replaceMonthName(timer.monthCol);
	                    } else return "Все";
	                }
	            case 5:
	                 return timer.obj.classId==Kernel.SC_TIMER.id?(timer.monthsDaysCol!=null?timer.monthsDaysCol:"Все"):"";
	            case 6:
	                if(timer.obj.classId==Kernel.SC_TIMER.id){
	                    if (timer.weekDaysCol!=null){
	                        return replaceWeekDaysName(timer.weekDaysCol);
	                    } else return "Все";
	                }
	            case 7:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.hourCol!=null?timer.hourCol:"Все"):"";
	            case 8:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.minuteCol!=null?timer.minuteCol:"Все"):"";
	            case 9:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.timeStart!=null?timer.timeStart:""):"";
	            case 10:
	                return timer.obj.classId==Kernel.SC_TIMER.id?(timer.timeFinish!=null?timer.timeFinish:""):"";
		        case 11:
			        return timer.obj.classId==Kernel.SC_TIMER.id?(timer.err?"Ошибка":""):"";
		    }
		}
		catch  (SecurityException se) {
	        se.printStackTrace();
	    }
		return null;
    }
    
    public String replaceMonthName (String a){
        String nameMonth = new String(a);
        String str = new String();
        for (String res : nameMonth.split(",")){
            switch (res){
                case "0": str = str.concat(res.replace("0","Январь "));continue;
                case "1": str = str.concat(res.replace("1","Февраль "));continue;
                case "2": str = str.concat(res.replace("2","Март "));continue;
                case "3": str = str.concat(res.replace("3","Апрель "));continue;
                case "4": str = str.concat(res.replace("4","Май "));continue;
                case "5": str = str.concat(res.replace("5","Июнь "));continue;
                case "6": str = str.concat(res.replace("6","Июль "));continue;
                case "7": str = str.concat(res.replace("7","Август "));continue;
                case "8": str = str.concat(res.replace("8","Сентябрь "));continue;
                case "9": str = str.concat(res.replace("9","Октябрь "));continue;
                case "10": str = str.concat(res.replace("10","Ноябрь "));continue;
                case "11": str = str.concat(res.replace("11","Декабрь"));
            }
        }
        return str;
    }

    public String replaceWeekDaysName (String a){
        String weekDays = new String(a);
        String days = new String();
        for (String res : weekDays.split(",")){
            switch (res){
                case "1": days = days.concat(res.replace("1","Воскресенье "));continue;
                case "2": days = days.concat(res.replace("2","Понедельник "));continue;
                case "3": days = days.concat(res.replace("3","Вторник "));continue;
                case "4": days = days.concat(res.replace("4","Среда "));continue;
                case "5": days = days.concat(res.replace("5","Четверг "));continue;
                case "6": days = days.concat(res.replace("6","Пятница "));continue;
                case "7": days = days.concat(res.replace("7","Суббота "));
            }
        }
        return days;
    }

    private String getObjArrayToStr(Object[] obj) {
        String res="";
        if(obj==null ||obj.length==0)return res;
        res=obj[0].toString().trim();
        for(int i=1;i<obj.length;++i){
            res+=","+obj[i].toString().trim();
        }
        return res;
    }

    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node
     */
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[]              retNodes;
	// This method recurses, traversing towards the root in order
	// size the array. On the way back, it fills in the nodes,
	// starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        }
        else {
            depth++;
            if(aNode == root)
                retNodes = new TreeNode[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    public void insertNodeInto(TimerNode newChild,
                               TimerNode parent, int index){
        parent.insert(newChild, index);

        int[]           newIndexs = new int[1];

        newIndexs[0] = index;
        nodesWereInserted(parent, newIndexs);
    }

    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     */
    public void removeNodeFromParent(TimerNode node) {
        TimerNode         parent = (TimerNode)node.getParent();

        if(parent == null)
            throw new IllegalArgumentException("node does not have a parent.");

        int[]            childIndex = new int[1];
        Object[]         removedArray = new Object[1];

        childIndex[0] = parent.getIndex(node);
        parent.remove(childIndex[0]);
        removedArray[0] = node;
        nodesWereRemoved(parent, childIndex, removedArray);
    }
    public void nodesWereInserted(TreeNode node, int[] childIndices) {
        if(listenerList != null && node != null && childIndices != null
           && childIndices.length > 0) {
            int               cCount = childIndices.length;
            Object[]          newChildren = new Object[cCount];

            for(int counter = 0; counter < cCount; counter++)
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices,
                                  newChildren);
        }
    }

    /**
      * Invoke this method after you've removed some TreeNodes from
      * node.  childIndices should be the index of the removed elements and
      * must be sorted in ascending order. And removedChildren should be
      * the array of the children objects that were removed.
      */
    public void nodesWereRemoved(TreeNode node, int[] childIndices,
                                 Object[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
                                 removedChildren);
        }
    }
    public void nodesChanged(TreeNode node, int[] childIndices) {
        if(node != null) {
	    if (childIndices != null) {
		int            cCount = childIndices.length;

		if(cCount > 0) {
		    Object[]       cChildren = new Object[cCount];

		    for(int counter = 0; counter < cCount; counter++)
			cChildren[counter] = node.getChildAt
			    (childIndices[counter]);
		    fireTreeNodesChanged(this, getPathToRoot(node),
					 childIndices, cChildren);
		}
	    }
	    else if (node == getRoot()) {
		fireTreeNodesChanged(this, getPathToRoot(node), null, null);
	    }
        }
    }
    public void nodeChanged(TreeNode node) {
        if(listenerList != null && node != null) {
            TreeNode         parent = node.getParent();

            if(parent != null) {
                int        anIndex = parent.getIndex(node);
                if(anIndex != -1) {
                    int[]        cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
	    else if (node == getRoot()) {
		nodesChanged(node, null);
	    }
        }
    }
}

/* A FileNode is a derivative of the File class - though we delegate to
 * the File object rather than subclassing it. It is used to maintain a
 * cache of a directory's children and therefore avoid repeated access
 * to the underlying file system during rendering.
 */
