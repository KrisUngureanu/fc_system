package kz.tamur.admin;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.*;

/*
 * User: vital
 * Date: 24.12.2005
 * Time: 10:41:44
 * Used only in kz.tamur.admin.clsBrowser -> ObjectBrowser!!! May be need move it in admin.clsbrow?! & rename it, because list changed to Tree!
 */

public class AttributeListPanel extends JPanel {

    private KrnClass cls;
    private JTree attrTree = new JTree();
    private DefaultListModel lm = new DefaultListModel();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public AttributeListPanel(KrnClass cls) {
        this.cls = cls;
        init();
    }

    private void init() {
    	attrTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        if (cls != null) {
            List<KrnAttribute> list = Kernel.instance().getAttributes(cls);
            list = getListByNames(list);
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(cls.name);
            for (final KrnAttribute KrnAttr : list) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(KrnAttr) {
                    @Override
                    public String toString() {
                        return KrnAttr.name;
                    }
                };

                if (KrnAttr.isIndexed)
                    node.add(new DefaultMutableTreeNode("fistashka"));
                if (KrnAttr.typeClassId > 99
                        || (KrnAttr.typeClassId != Kernel.IC_TIME && KrnAttr.typeClassId != Kernel.IC_BLOB && KrnAttr.collectionType == 0))
                    rootNode.add(node);
            }

            attrTree = new JTree(rootNode);
            attrTree.setCellRenderer(new CellRenderer());
            attrTree.addTreeWillExpandListener(new TreeWillExpandListener() {

                public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
                }

                public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
                    willExpandLoader(e);
                }

            });
        }
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(attrTree);
        sp.setPreferredSize(new Dimension(300, 400));
        add(sp, BorderLayout.CENTER);
        setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        attrTree.setOpaque(isOpaque);

    }
    
    private List<KrnAttribute> getListByNames(List<KrnAttribute> list) {
        List<String> names = new ArrayList<String>(), onames = new ArrayList<String>();
        List<KrnAttribute> out = new ArrayList<KrnAttribute>();
        for (KrnAttribute attr : list) {
            names.add(attr.name);
            onames.add(attr.name);
        }
        Collections.sort(names);
        for (String name : names) {
            out.add(list.get(onames.indexOf(name)));
        }
        return out;
    }

    public void willExpandLoader(TreeExpansionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        KrnAttribute[] kAttrs = null;
        try {
            kAttrs = Kernel.instance().getAttributesByClassId(
                    Kernel.instance().getClass(((KrnAttribute) node.getUserObject()).typeClassId));
        } catch (KrnException e1) {
            e1.printStackTrace();
        }
        node.removeAllChildren();
        if (kAttrs != null) {
            kAttrs = (KrnAttribute[]) ((List) getListByNames(Arrays.asList(kAttrs))).toArray(new KrnAttribute[0]);
            for (final KrnAttribute kAttr : kAttrs) {
                DefaultMutableTreeNode inNode = new DefaultMutableTreeNode(kAttr) {
                    @Override
                    public String toString() {
                        return kAttr.name;
                    }
                };
                if (kAttr.isIndexed)
                    inNode.add(new DefaultMutableTreeNode("fistashka"));
                if (kAttr.typeClassId > 99
                        || (kAttr.typeClassId != Kernel.IC_TIME && kAttr.typeClassId != Kernel.IC_BLOB && kAttr.collectionType == 0))
                    node.add(inNode);

            }
        } else {

        }
    }

    public KrnAttribute getSelectedAttribute() {
        if (attrTree != null) {
            TreePath path = attrTree.getSelectionPath();
            if (path != null) {
                if (attrTree.getSelectionPath().getParentPath() == null)
                    return null;
                return (KrnAttribute) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            }
        }
        return null;
    }

    public TreePath getSelectionPath() {
        if (attrTree != null) {
            return attrTree.getSelectionPath();
        }
        return null;
    }
    
    public KrnAttribute[] getSelectedPathsAttributes() {
        if (attrTree != null) {
            TreePath path = attrTree.getSelectionPath();
            if (path != null && path.getParentPath() != null) {
                KrnAttribute[] attrs = new KrnAttribute[path.getPathCount() - 1];
                for (int i = attrs.length - 1; i > -1; i--) {
                    attrs[i] = (KrnAttribute) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                    path = path.getParentPath();
                }
                return attrs;
            }
        }
        return null;
    }

    private class CellRenderer extends JLabel implements TreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            setOpaque(false);
            if (hasFocus && selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(Color.white);
            } else {
                setBackground(Utils.getLightGraySysColor());
                setForeground(Color.black);
            }
            setOpaque(isOpaque || selected);
            setFont(Utils.getDefaultFont());
            setText(value.toString());
            return this;
        }
    }
}
