package kz.tamur.admin;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

public class IndexTree extends JTree{
	public IndexTree(){
		jbInit();
	}
	
	public IndexTree(TreeNode root){
		super(root,false);
		jbInit();
	}
	
	private void jbInit(){
		IndexTreeCellRenderer dtcr_ = new IndexTreeCellRenderer();
        dtcr_.setBackgroundNonSelectionColor(Color.lightGray);
        dtcr_.setClosedIcon(null);
        dtcr_.setOpenIcon(null);
        dtcr_.setLeafIcon(null);
        dtcr_.setBackgroundSelectionColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
        dtcr_.setBorderSelectionColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
        putClientProperty("JTree.lineStyle", "Angled");
        setBackground(Color.lightGray);
        setCellRenderer(dtcr_);
	}
	
	public void expandAll(){
		int row = 0;
		while(row < this.getRowCount()){
			this.expandRow(row++);			
		}
	}
	

	
	class IndexTreeCellRenderer extends DefaultTreeCellRenderer {		
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        	JLabel ret = new JLabel("Элемент дерева не определен");        	
        	if(node.getUserObject() instanceof JLabel){
        		ret = (JLabel)node.getUserObject();        		
        	}
        	ret.setOpaque(true);        	
        	if(sel){
        		ret.setBackground(kz.tamur.rt.Utils.getDarkShadowSysColor());
        		ret.setForeground(Color.WHITE);
        	}else{
        		ret.setBackground(Color.LIGHT_GRAY);
        		ret.setForeground(new Color(63,76,107));
        	}
        	return ret;
        }
    }
}
