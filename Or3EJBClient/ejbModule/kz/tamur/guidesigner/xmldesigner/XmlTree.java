package kz.tamur.guidesigner.xmldesigner;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import kz.tamur.util.*;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.users.Or3RightsNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class XmlTree extends DesignerTree {

    private String searchString = "";
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;

    public XmlTree(XmlNode root) {
        super(root);
        this.root = root;
        model = new XmlTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());

        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FUNCS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.FUNCS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.FUNCS_CREATE_RIGHT);
    }


    protected void defaultDeleteOperations() throws KrnException {
        AbstractDesignerTreeNode node = getSelectedNode();
        ControlTabbedContent tc =  ControlTabbedContent.instance();
        if (tc.isExistIfr(node.getKrnObj().id)) {
            tc.closeCurrent();
        }
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Поиск функции", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();
        }
    }


    public class XmlTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public XmlTreeModel(TreeNode root) {
            super(root);
        }

        public void renameNode() {
            CreateElementPanel cp = new CreateElementPanel(
                    CreateElementPanel.RENAME_TYPE, getSelectedNode().toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Переименование узла", cp);
            dlg.show();
            int res = dlg.getResult();
            if (res == ButtonsFactory.BUTTON_OK) {
                Kernel krn = Kernel.instance();
                try {
                    KrnClass cls = krn.getClassByName("Func");
                    KrnObject obj = getSelectedNode().getKrnObj();
                    KrnAttribute attr = krn.getAttributeByName(cls, "name");
                    krn.setString(obj.id, attr.id, 0, 0,
                            cp.getElementName(), 0);
                    XmlNode source =
                            (XmlNode)
                            XmlTree.this.root.find(obj).getLastPathComponent();
                    source.rename(cp.getElementName());
                    TreeNode[] tp = getPathToRoot(source);
                    fireTreeNodesChanged(this, tp, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("FuncFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            AbstractDesignerTreeNode selNode = getSelectedNode();
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "name", 0, 0, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children", idx, obj.id, 0, false);
            XmlNode node = new XmlNode(obj, title, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("Func");
            final KrnObject obj = krn.createObject(cls, 0);
            AbstractDesignerTreeNode selNode = (XmlNode)root;
            AbstractDesignerTreeNode inode = getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            }
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "name", 0, 0, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children",
                    idx, obj.id, 0, false);
            XmlNode node = new XmlNode(obj, title, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        private void deleteFilterNode(AbstractDesignerTreeNode node) throws KrnException {
            FiltersTree ftree = kz.tamur.comps.Utils.getFiltersTree();
            FilterNode filter = ftree.findByName(node.toString());
            if (filter != null) {
                FiltersTree.FilterTreeModel fModel =
                        (FiltersTree.FilterTreeModel)ftree.getModel();
                fModel.deleteNode(filter, false);
            }
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            XmlNode parent = (XmlNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                deleteFilterNode(node);
                krn.deleteObject(node.getKrnObj(), 0);
            }
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new XmlNode(node.getKrnObj(), node.toString(),
                        parent.getChildCount());
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId,
                    "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                l.setIcon(kz.tamur.rt.Utils.getImageIcon("XmlTreeNode"));
            }
            XmlNode node = (XmlNode)value;
            if (node.isLeaf() && node.isModify()) {
                l.setForeground(Color.red);
            } else {
                if (!selected) {
                    l.setForeground(Color.black);
                } else {
                    l.setForeground(Color.white);
                }
            }
            if (selected) {
                l.setBackground(Utils.getDarkShadowSysColor());
            } else {
                l.setBackground(Utils.getLightSysColor());
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }

    }


    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == miRename) {
            renameNode();
        } else {
            super.actionPerformed(e);
        }
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Вставка копии узла", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String ifcName = cp.getElementName();
                if (ifcName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя узла!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    try {
                        if (!copyNode.isCutProcess()) {
                            byte[] data =
                                    ((XmlNode)copyNode).getExpressionText().getBytes("UTF-8");
                            KrnClass cls = Kernel.instance().getClassByName("Func");
                            KrnObject func = krn.createObject(cls, 0);
                            krn.setString(func.id, func.classId, "name", 0, 0, ifcName, 0);
                            krn.setBlob(func.id, func.classId, "text", 0, data, 0, 0);
                            XmlNode n = new XmlNode(func, ifcName, parent.getChildCount());
//                            n.setExpressionText(((XmlNode)copyNode).getExpressionText());
                            model.addNode(n, parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id,
                                    copyNode.getKrnObj().classId, "name", 0, 0, ifcName, 0);
                            model.addNode(new XmlNode(copyNode.getKrnObj(),
                                    ifcName, parent.getChildCount()), parent, false);
                            model.deleteNode(copyNode, true);
                            defaultDeleteOperations();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }
    }

    private void renameNode() {
        model.renameNode();
    }

/*
    public int getLangId() {
        return langId;
    }
*/

/*
    public void setLangId(int langId) {
        this.langId = langId;
        KrnClass cls = null;
        Kernel krn = Kernel.instance();
        try {
            cls = krn.getClassByName("UIRoot");
            KrnObject uiRoot = krn.getClassObjects(cls, 0)[0];
            int[] ids = {uiRoot.id};
            StringValue[] svs = krn.getStringValues(ids, cls.id, "title", langId,
                    false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }
            root = new InterfaceNode(uiRoot, title, langId, 0);
            model = new XmlTreeModel(root);
            setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();
    }
*/

    public DesignerTreeNode[] getSelectedNodes() {
        ArrayList<XmlNode> list = new ArrayList<XmlNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            XmlNode node = (XmlNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        XmlNode[] res = new XmlNode[list.size()];
        list.toArray(res);
        return res;
    }

    public void setSelectedNode(KrnObject obj) {
        TreeModel m = getModel();
        XmlNode node = (XmlNode)finder.findFirst(
                (XmlNode)m.getRoot(), new KrnObjectPattern(obj));
        if (node != null) {
            TreePath tpath = new TreePath(node.getPath());
            setSelectionPath(tpath);
            scrollPathToVisible(tpath);
        }
    }

    public XmlNode getXmlNodeByObject(KrnObject object) {
        TreeModel m = getModel();
        XmlNode node = (XmlNode)finder.findFirst(
                (XmlNode)m.getRoot(), new KrnObjectPattern(object));
        return node;
    }

    protected void initPopup() {
        pm.add(miCreateFolder);
        miCreateFolder.addActionListener(this);
        pm.add(miCreateWS);
        miCreateWS.addActionListener(this);
        pm.addSeparator();
        pm.add(miGenerate);
        miGenerate.addActionListener(this);
        pm.addSeparator();
        pm.add(miCopy);
        miCopy.addActionListener(this);
        pm.add(miCut);
        miCut.addActionListener(this);
        pm.add(miPaste);
        miPaste.addActionListener(this);
        pm.addSeparator();
        pm.add(miDelete);
        pm.addSeparator();
        pm.add(miFind);
      //  pm.add(miExport);
        miExport.addActionListener(this);
        miFind.addActionListener(this);
        miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        pm.add(miFindNext);
        miFindNext.addActionListener(this);
        miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        pm.add(miRename);
        miRename.addActionListener(this);
        miDelete.addActionListener(this);
        miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    }

    protected void showPopup(MouseEvent e) {
             if (getSelectedNode().isLeaf()) {
               miCreateFolder.setEnabled(false);
               miCreateElement.setEnabled(false);
               miCopy.setEnabled(canCreate);
               miCut.setEnabled(canCreate);
               miPaste.setEnabled(false);
            } else {
               miCreateFolder.setEnabled(canCreate);
               miCreateElement.setEnabled(canCreate);
               miCopy.setEnabled(false);
               miCut.setEnabled(false);
               if (copyNode != null) {
                   miPaste.setEnabled(canCreate);
               } else {
                   miPaste.setEnabled(false);
               }
            }
             miDelete.setEnabled(!(getSelectedNode() == root) && canDelete);
             miRename.setEnabled(canEdit);
             pm.show(e.getComponent(), e.getX(), e.getY());
         }

}
