package kz.tamur.guidesigner.terminal;

import javax.swing.tree.TreeNode;

import java.util.Vector;
import java.util.Map;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class VariableInfoNode implements TreeNode {
	VariableObject variable;
	TreeNode parent;
	Vector<TreeNode> children;
	Map variables;
	static public final Enumeration EMPTY_ENUMERATION = new Enumeration() {
		public boolean hasMoreElements() { return false;}
		public Object nextElement() {
			throw new NoSuchElementException("No more elements");
		}
	};
	
	public VariableInfoNode(VariableObject variable, Map variables) {
		this.variable = variable;
		this.variables = variables;
		this.parent = null;
	}
	
	public String toString() {
		return variable.toString();
	}
	
	public VariableObject getVariable() {
		return variable;
	}
	
	private VariableObject[] getObjectsFromMap() {
		//VariableObject[] objs = (VariableObject[])variable.vrs.toArray()
		if(variable.vrs == null) return null;
		int length = variable.vrs.size();		
		VariableObject[] objs = new VariableObject[length];

		for(int i = 0; i < length; i++){
			objs[i] = variable.vrs.elementAt(i);
		}
		
		return objs;
	}
	
	//Oldone!!!
	/*
	//TODO, REWRITE!!!
	private VariableObject[] getObjectsFromMap() {
		Vector<VariableObject> vector = new Vector<VariableObject>();
		//now work only for array!!! Must rewrite!!!
		if(!variable.var.getClass().isArray()) return null;
		Field[] oj = variable.var.getClass().getDeclaredFields();
		Object[] o = (Object[])variable.var;
		for(Object f : o) {
			VariableObject vo = new VariableObject(f);
			vo.name = "Arrayy";
			vo.type = vo.getClass().getName();
			vector.add(vo);
		}
		
		VariableObject[] objs = (VariableObject[]) vector.toArray();
		return objs;
	}*/
	
	//TODO!!! rewrite!!! Error!
	protected Object[] getChildren() {
		if(children != null) {
			return children.toArray();
		}
		try {
			VariableObject[] objs = getObjectsFromMap();
			children = new Vector<TreeNode>();
			if(objs!=null && objs.length > 0) {
				for(VariableObject obj : objs) {
					//VariableObject child = obj;
					//VariableInfoNode node = new VariableInfoNode(child, variables);
					VariableInfoNode node = new VariableInfoNode(obj, variables);
					node.setParent(this);
					children.add(node);
				}
			}
		} catch (Exception e) {
			System.out.println("Error in JTreeTableInfoTable getChildren()");
			e.printStackTrace();
		}
		return children.toArray();
	}
	
	public int getChildCount() {
		if(children == null) {
			return 0;
		} else {
			return children.size();
		}
	}
	
	public boolean getAllowsChildren() {
		return true;
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public Enumeration children() {
		if(children == null) {
			return EMPTY_ENUMERATION;
		} else {
			return children.elements();
		}
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	public TreeNode getChildAt(int childIndex){
		if(children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		return children.elementAt(childIndex);
	}
	
	public int getIndex(TreeNode aChild) {
		if(aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}
		if(!isNodeChild(aChild)){
			return -1;
		}
		return children.indexOf(aChild);
	}
	
	public void setParent(TreeNode newParent) {
		parent = newParent;
	}
	
	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;
		
		if(aNode == null) {
			retval = false;
		} else {
			if(getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}
		return retval;
	}
	
	public void remove(VariableInfoNode aChild) {
		if(aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}
		
		if(!isNodeChild(aChild)) {
			throw new IllegalArgumentException("argument is not a child");
		}
		
		remove(getIndex(aChild));
	}
	
	public void remove(int childIndex) {
		VariableInfoNode child = (VariableInfoNode)getChildAt(childIndex);
		children.removeElementAt(childIndex);
		child.setParent(null);
	}
	
	public void insert(VariableInfoNode newChild, int childIndex) {
		VariableInfoNode oldParent = (VariableInfoNode) newChild.getParent();
		
		if(oldParent != null) {
			oldParent.remove(newChild);
		}
		newChild.setParent(this);
		if(children == null){
			children = new Vector<TreeNode>();
		}
		children.insertElementAt(newChild, childIndex);
	}
	
	public VariableObject getObject() {
		return variable;
	}
}


/*
public class VariableInfoNode implements TreeNode{
	VariableObject variable;
	TreeNode parent;
	Vector<TreeNode> children;
	Map variables;
	static public final Enumeration EMPTY_ENUMERATION =
		new Enumeration() {
			public boolean hasMoreElements() { return false;}
			public Object nextElement() {
				throw new NoSuchElementException("No more elements");
			}
	};
	//public VariableInfoNode(VariableInfoNode variable, Map variables){
	public VariableInfoNode(VariableObject variable, Map variables){
		this.variable = variable;
		this.variables = variables;
		this.parent = null;
	}
	
	public String toString() {
		return variable.toString();
	}

	public VariableObject getVariable() {
		return variable;
	}
	
	protected Object[] getChildren() {
		if(children != null) {
			return children.toArray();
		}
		try {
			children = new Vector<TreeNode>();
			//get objects, then 
			VariableObject[] objs = null;
			//ended
			if(objs.length > 0) {
				for(VariableObject obj : objs) {
					VariableObject child = (VariableObject) variables.get(obj.name);
					VariableInfoNode node = new VariableInfoNode(child, variables);
					node.setParent(this);
					children.add(node);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return children.toArray();
	}
	
	public int getChildCount() {
		if(children == null) {
			return 0;
		} else {
			return children.size();
		}
	}
	
	public boolean getAllowsChildren() {
		return true;
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public Enumeration children() {
		if(children == null) {
			return EMPTY_ENUMERATION;
		} else {
			return children.elements();
		}
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	public TreeNode getChildAt(int childIndex) {
		if(children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		return children.elementAt(childIndex);
	}
	
	public int getIndex(TreeNode aChild) {
		if( aChild == null) {
			throw new IllegalArgumentException("argument is nul");
		}
		
		if(!isNodeChild(aChild)) {
			return -1;
		}
		return children.indexOf(aChild);
	}
	
	public void setParent(TreeNode newParent){
		parent = newParent;
	}
	
	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;
		
		if(aNode == null){
			retval = false;
		} else {
			if(getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}
		
		return retval;
	}
	
	public void remove(VariableInfoNode aChild) {
		if(aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}
		if(!isNodeChild(aChild)) {
			throw new IllegalArgumentException("argument is not a child");
		}
		remove(getIndex(aChild));
	}
	
	public void remove(int childIndex) {
		VariableInfoNode child = (VariableInfoNode) getChildAt(childIndex);
		children.removeElementAt(childIndex);
		child.setParent(null);
	}
	
	public void insert(VariableInfoNode newChild, int childIndex) {
		VariableInfoNode oldParent = (VariableInfoNode) newChild.getParent();
		
		if(oldParent != null) {
			oldParent.remove(newChild);
		}
		newChild.setParent(this);
		if(children == null) {
			children = new Vector<TreeNode>();
		}
		children.insertElementAt(newChild, childIndex);
	}
	
	public VariableObject getObject() {
		return variable;
	}
}
*/