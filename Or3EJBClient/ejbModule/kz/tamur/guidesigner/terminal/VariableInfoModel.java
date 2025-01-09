package kz.tamur.guidesigner.terminal;

import other.treetable.AbstractTreeTableModel;
import other.treetable.TreeTableModel;

import javax.swing.tree.TreeNode;

import com.cifs.or2.kernel.KrnObject;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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

/**
 * TreeTableModel для Информационной панели, Более полные данные переменной
 */
public class VariableInfoModel extends AbstractTreeTableModel implements TreeTableModel {
	static ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
	static protected String[] cNames = {res.getString("key"), res.getString("type"), res.getString("value")};
	static protected Class[] cTypes = {TreeTableModel.class, String.class, String.class};
	
	public VariableInfoModel(VariableObject root, Map variables) {
		super(new VariableInfoNode(root, variables));
	}
	
	protected VariableObject getVariable(Object node) {
		VariableInfoNode variableInfoNode = ((VariableInfoNode)node);
		return variableInfoNode.getVariable();
	}
	
	protected Object[] getChildren(Object node) {
		VariableInfoNode variableInfoNode = ((VariableInfoNode)node);
		return variableInfoNode.getChildren();
	}
	
	public int getChildCount(Object node) {
		Object[] children = getChildren(node);
		return (children == null) ? 0 : children.length;
	}
	
	public Object getChild(Object node, int i) {
		return getChildren(node)[i];
	}
	
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
		VariableObject variable = getVariable(node);
		try {
			switch(column) {
			case 0:
				return variable.name;
			case 1:
				return variable.type;
			case 2:
				return variable.var;
			default:
				return variable;
			}
		} catch(SecurityException se) {
			se.printStackTrace();
		}
		
		return null;
	}
	
	private String getObjArrayToStr(Object[] obj) {
		String res = "";
		if(obj == null || obj.length ==0) return res;
		res = obj[0].toString().trim();
		for(int i = 1; i < obj.length; ++i) {
			res += "," + obj[i].toString().trim();
		}
		return res;
	}
	
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		return getPathToRoot(aNode, 0);
	}
	
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[] retNodes;
		
		if(aNode == null) {
			if(depth == 0)
				return null;
			else retNodes = new TreeNode[depth];
		} else {
			depth++;
			if(aNode == root)
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}
	
	public void insertNodeInto(VariableInfoNode newChild, VariableInfoNode parent, int index){
		parent.insert(newChild, index);
		int[] newIndexs = new int[1];
		newIndexs[0] = index;
		nodesWereInserted(parent, newIndexs);
	}
	
	public void removeNodeFromParent(VariableInfoNode node) {
		VariableInfoNode parent = (VariableInfoNode)node.getParent();
		if(parent == null)
			throw new IllegalArgumentException("node does not have a parent.");
		int[] childIndex = new int[1];
		Object[] removedArray = new Object[1];
		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
		nodesWereRemoved(parent, childIndex, removedArray);
	}
	
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		if(listenerList != null && node != null && childIndices != null && childIndices.length >0) {
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];
			
			for(int counter = 0; counter < cCount; counter++)
				newChildren[counter] = node.getChildAt(childIndices[counter]);
			fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
		}
	}
	
	public void nodesWerInserted(TreeNode node, int[] childIndices) {
		if(listenerList != null && node != null && childIndices != null && childIndices.length > 0) {
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];
			for(int counter = 0; counter < cCount; counter++)
				newChildren[counter] = node.getChildAt(childIndices[counter]);
			fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
		}
	}
	
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
		if(node != null && childIndices != null) {
			fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
		}
	}
	
	public void nodesChanged(TreeNode node, int[] childIndices) {
		if(node != null) {
			if(childIndices != null) {
				int cCount = childIndices.length;
				if(cCount > 0) {
					Object[] cChildren = new Object[cCount];
					for(int counter = 0; counter < cCount; counter++)
						cChildren[counter] = node.getChildAt(childIndices[counter]);
					fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
				}
			} else if(node == getRoot()){
				fireTreeNodesChanged(this, getPathToRoot(node), null, null);
			}
		}
	}
	
	public void nodeChanged(TreeNode node) {
		if(listenerList != null && node != null) {
			TreeNode parent = node.getParent();
			if(parent != null) {
				int anIndex = parent.getIndex(node);
				if(anIndex != -1) {
					int[] cIndexs = new int[1];
					
					cIndexs[0] = anIndex;
					nodesChanged(parent, cIndexs);
				}
			} else if(node == getRoot()) {
				nodesChanged(node, null);
			}
		}
	}
	
	//in cellComponent, for Btn action must be isCellEditable true
    public boolean isCellEditable(Object node, int column) { 
    	if(column==1 && ((VariableInfoNode)node).variable.var instanceof KrnObject){
    		return true;
    	}
        return getColumnClass(column) == TreeTableModel.class; 
   }
}

/* A FileNode is a derivative of the File class - though we delegate to
 * the File object rather than subclassing it. It is used to maintain a
 * cache of a directory's children and therefore avoid repeated access
 * to the underlying file system during rendering.
 */

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
/*
public class VariableInfoModel extends AbstractTreeTableModel implements TreeTableModel{
	//Names of columns
	static protected String[] cNames = {"Keys", "Types", "Values"};
	//Types of columns
	static protected Class[] cTypes = {TreeTableModel.class, String.class, String.class};
	
	public VariableInfoModel(VariableObject root, Map variables){
		super(new VariableInfoNode(root, variables));
	}
	
	protected VariableObject getVariable(Object node) {
		VariableInfoNode variableNode = ((VariableInfoNode)node);
		return variableNode.getVariable();
	}
	
	protected Object[] getChildren(Object node) {
		VariableInfoNode variableNode = ((VariableInfoNode)node);
		return variableNode.getChildren();
	}
	
	public int getChildCount(Object node) {
		Object[] children = getChildren(node);
		return (children == null) ? 0 : children.length;
	}
	
	public Object getChild(Object node, int i) {
		return getChildren(node)[i];
	}
	
	public boolean isLeaf(Object node) {
		//return getVariable(node).obj.classId == 
		//MustBeDeleted
		return false;
	}
	
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
		VariableObject variable = getVariable(node);
		try {
			switch(column) {
			case 0:
				return variable;
			case 1:
				//return variable.
			case 2:
			case 3:
			default:
				return variable;
			}
		} catch ( SecurityException se) {
			se.printStackTrace();
		}
		return null;
	}
	
	private String getObjArrayToStr(Object[] obj) {
		String res = "";
		if(obj == null || obj.length == 0) return res;
		res = obj[0].toString().trim();
		for(int i = 1; i < obj.length; ++i) {
			res += "," + obj[i].toString().trim();
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
     *//*
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth){
		TreeNode[] retNodes;
		
		if(aNode == null) {
			if(depth == 0)
				return null;
			else 
				retNodes = new TreeNode[depth];
		}else {
			depth++;
			if(aNode == root) 
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}
	
	public void insertNodeInto(VariableInfoNode newChild,
									VariableInfoNode parent,
									int index){
		parent.insert(newChild, index);
		int[] newIndexs = new int[1];
		
		newIndexs[0] = index;
		nodesWereInserted(parent, newIndexs);
	}
	
	public void removeNodeFromParent(VariableInfoNode node) {
		VariableInfoNode parent = (VariableInfoNode)node.getParent();
		
		if(parent == null) 
			throw new IllegalArgumentException("node does not have a parent.");
		
		int[] childIndex = new int[1];
		Object[] removedArray = new Object[1];
		
		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
		nodesWereRemoved(parent, childIndex, removedArray);
	}
	
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		if(listenerList != null && node != null && childIndices != null && childIndices.length > 0){
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];
			
			for(int counter = 0; counter < cCount; counter++)
				newChildren[counter] = node.getChildAt(childIndices[counter]);
			fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
		}
	}
	
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren){
		if(node != null && childIndices != null) {
			fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
		}
	}
	
	public void nodesChanged(TreeNode node,int[] childIndices) {
		if(node != null) {
			if(childIndices != null) {
				int cCount = childIndices.length;
				if(cCount > 0) {
					Object[] cChildren = new Object[cCount];
					
					for(int counter = 0; counter < cCount; counter++)
						cChildren[counter] = node.getChildAt(childIndices[counter]);
					fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
				}
			} else if(node == getRoot()) {
				fireTreeNodesChanged(this, getPathToRoot(node), null, null);
			}
		}
	}
	
	public void nodeChanged(TreeNode node) {
		if(listenerList != null && node!= null) {
			TreeNode parent = node.getParent();
			
			if(parent != null) {
				int anIndex = parent.getIndex(node);
				if(anIndex != -1) {
					int [] cIndexs = new int[1];
					cIndexs[0] = anIndex;
					nodesChanged(parent, cIndexs);
				}
			} else if(node == getRoot()) {
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
