package kz.tamur.admin;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PackageNode;
import com.cifs.or2.kernel.KrnClass;

public class PackageTree extends JTree {

	private static final long serialVersionUID = 1L;
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private AttributeTree attrTree;

	public PackageTree() {
		super(Kernel.instance().getPackageHierarchy());
        jbInit();
	}
	
    private void jbInit() {
    	PackageTreeCellRenderer cellRenderer = new PackageTreeCellRenderer();
    	cellRenderer.setBackgroundNonSelectionColor(isOpaque ? Color.lightGray : new Color(0, 0, 0, 0));
    	cellRenderer.setClosedIcon(null);
    	cellRenderer.setOpenIcon(null);
    	cellRenderer.setLeafIcon(null);
    	cellRenderer.setBackgroundSelectionColor(Utils.getDarkShadowSysColor());
    	cellRenderer.setBorderSelectionColor(Utils.getDarkShadowSysColor());
        putClientProperty("JTree.lineStyle", "Angled");
        setBackground(Color.lightGray);
        setCellRenderer(cellRenderer);
        setOpaque(isOpaque);
    }
    
    public void setAttrTree(AttributeTree attrTree) {
    	this.attrTree = attrTree;
    }
    
    public KrnClass getSelectedClass() {
        TreePath path = getSelectionPath();
        PackageNode node = (path == null) ? null : (PackageNode) path.getLastPathComponent();
        return (node == null) ? null : node.getKrnClass();
    }
    
    public com.cifs.or2.client.Package getSelectedPackage() {
        TreePath path = getSelectionPath();
        PackageNode node = (path == null) ? null : (PackageNode) path.getLastPathComponent();
        return (node == null) ? null : node.getPackage();
    }
    
	public List<KrnClass> getSelectedClasses() {
		TreePath path = getSelectionPath();
		if (path != null) {
			Object[] objs = path.getPath();
			List<KrnClass> classes = new ArrayList<>();
			for (int i = 0; i < objs.length; i++) {
				KrnClass cls = ((PackageNode) objs[i]).getKrnClass();
				if (cls != null) {
					classes.add(cls);
				}
			}
			return classes;
		}
		return null;
	}

	public List<com.cifs.or2.client.Package> getSelectedPackages() {
		TreePath path = getSelectionPath();
		if (path != null) {
			Object[] objs = path.getPath();
			List<com.cifs.or2.client.Package> packages = new ArrayList<>();
			for (int i = 0; i < objs.length; i++) {
				com.cifs.or2.client.Package pckg = ((PackageNode) objs[i]).getPackage();
				if (pckg != null) {
					packages.add(pckg);
				}
			}
			return packages;
		}
		return null;
	}
    
	public PackageNode getSelectedNode() {
        TreePath path = getSelectionPath();
        PackageNode node = (path == null) ? null : (PackageNode) path.getLastPathComponent();
        return (node == null) ? null : node;
    }
	
    public String getNodeName() {
    	PackageNode node= getSelectedNode();
    	if (node != null) {
    		return node.getKrnClass().name;
    	}
    	return "";
    }
}

class PackageTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        PackageNode node = (PackageNode) value;
        if (node.getPackage() == null) {
        	KrnClass cls = node.getKrnClass();
            l.setIcon(ClassTreeIconLoader.getIcon(cls.isRepl, cls.isVirtual()));
        } else {
        	if (node.isRoot()) {
            	l.setIcon(kz.tamur.rt.Utils.getImageIcon("packageFolder"));
        	} else {
        		if (node.isEmpty()) {
                	l.setIcon(kz.tamur.rt.Utils.getImageIcon("packageEmpty"));
        		} else {
                	l.setIcon(kz.tamur.rt.Utils.getImageIcon("package"));
        		}
        	}
        }
        l.setForeground(sel ? Color.white : Color.black);
        l.setBackground(Utils.getDarkShadowSysColor());
        l.setOpaque(selected);
        return l;
    }
}