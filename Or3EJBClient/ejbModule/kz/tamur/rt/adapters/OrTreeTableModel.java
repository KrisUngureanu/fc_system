package kz.tamur.rt.adapters;

import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.cifs.or2.kernel.KrnObject;

import other.treetable.TreeTableModel;

public class OrTreeTableModel implements TreeTableModel {
	
	private TreeTableAdapterEx adapter;

	protected EventListenerList listenerList = new EventListenerList();

	public Class getColumnClass(int column) {
		if (column == 0) {
			return TreeTableModel.class;
		}
		return Object.class;
	}

	public int getColumnCount() {
		return adapter.getColumnCount();
	}

	public String getColumnName(int column) {
		return adapter.getColumnAt(column).getColumn().getTitle();
	}

	public Object getValueAt(Object node, int column) {
		Node n = (Node)node;
		return adapter.getValueAt(n.index, column);
	}

	public boolean isCellEditable(Object node, int column) {
		return true;
	}

	public void setValueAt(Object value, Object node, int column) {
	}

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	public Object getChild(Object parent, int index) {
		Node p = (Node)parent;
		return null;
	}

	public int getChildCount(Object parent) {
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	public Object getRoot() {
		return null;
	}

	public boolean isLeaf(Object node) {
		return false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}
	
	private static class Node {
		public KrnObject obj;
		public int index;
		
		private List<Node> children;
	}

}
