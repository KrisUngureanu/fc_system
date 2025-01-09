package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.*;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class NoteTree extends DesignerTree {


    private String searchString = "";
    private NodeFinder finder = new NodeFinder();


    private long langId;

    public NoteTree(NoteNode root, long langId) {
        super(root);
        this.root = root;
        this.langId = langId;
        model = new NoteTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
        miRename.setEnabled(true);
    }

    protected void defaultDeleteOperations() {

    }

    public NoteNode findByName(String name) {
        setSelectionPath(new TreePath(root));
        AbstractDesignerTreeNode node = getSelectedNode();
        if (node == null) {
            node = root;
        }
        TreeNode fnode = finder.findFirst(node, new StringPattern(name));
        if (fnode != null) {
            setSelectedNode(((NoteNode) fnode).getKrnObj());
            return (NoteNode) fnode;
        } else {
            return null;
        }
    }

    public AbstractDesignerTreeNode find(KrnObject obj) {
        TreeNode n = finder.findFirst(root, new KrnObjectPattern(obj));
        return (AbstractDesignerTreeNode)n;
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((JDialog) getTopLevelAncestor(), "Поиск справки", sip);
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
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();
        }
    }


    public class NoteTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public NoteTreeModel(TreeNode root) {
            super(root);
        }

        public void renameFilter(NoteNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }


        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("NoteFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            //final int langId = Utils.getFilterLangId();
            NoteNode selNode = (NoteNode) getSelectedNode();
            KrnObject noteObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(noteObj.id, noteObj.classId, "children",
                    idx, obj.id, 0, false);
            NoteNode node = new NoteNode(obj, title, langId, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("Note");
            final KrnObject obj = krn.createObject(cls, 0);
            NoteNode selNode = (NoteNode) root;
            NoteNode inode = (NoteNode) getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            }
            KrnObject noteObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(noteObj.id, noteObj.classId, "children",
                    idx, obj.id, 0, false);
            NoteNode node = new NoteNode(obj, title, langId, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            NoteNode parent = (NoteNode) node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
            }
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new NoteNode(node.getKrnObj(), node.toString(),
                        node.getLangId(), parent.getChildCount());
            }
            if(parent.isLeaf()){
            	AbstractDesignerTreeNode parent_=(AbstractDesignerTreeNode)parent.getParent();
            	int index=parent_.getIndex(parent);
            	KrnObject parentObj = parent_.getKrnObj();
	            krn.setObject(parentObj.id, parentObj.classId,
	                    "children", index, node.getKrnObj().id, 0, false);
	            insertNodeInto(node, parent_, index);
            }else{
	            KrnObject parentObj = parent.getKrnObj();
	            krn.setObject(parentObj.id, parentObj.classId,
	                    "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
	            insertNodeInto(node, parent, parent.getChildCount());
            }
        }

        public void renameNode() {
            NoteNode node = (NoteNode) getSelectedNode();
            CreateElementPanel op = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, node.toString());
            DesignerDialog dlg = null;
            if (getTopLevelAncestor() instanceof Dialog) {
                dlg = new DesignerDialog((Dialog) getTopLevelAncestor(),
                        "Переименование папки", op);
            } else {
                dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                        "Переименование папки", op);
            }
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                Kernel krn = Kernel.instance();
                try {
                    KrnClass cls = krn.getClassByName("Note");
                    KrnObject obj = getSelectedNode().getKrnObj();
                    KrnAttribute attr = krn.getAttributeByName(cls, "title");
                    krn.setString(obj.id, attr.id, 0, langId,
                            op.getElementName(), 0);
                    NoteNode source =
                            (NoteNode)
                            NoteTree.this.root.find(obj).getLastPathComponent();
                    source.rename(op.getElementName());
                    TreeNode[] tp = getPathToRoot(source);
                    fireTreeNodesChanged(this, tp, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                if (Constants.SE_UI||Utils.isDesignerRun()) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIconFull("noteNew.png"));
                }else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("NoteNode"));
                }
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }

    }



    /*protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Создание копии фильтра", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String filterName = cp.getElementName();
                if (filterName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя фильтра!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    try {
                        if (!copyNode.isCutProcess()) {
                            byte[] data = krn.getBlob(copyNode.getKrnObj(), "config", 0, 0, 0);
                            byte[] dataSql = krn.getBlob(copyNode.getKrnObj(), "exprSql", 0, 0, 0);
                            KrnObject noteObj = krn.createObject(Kernel.SC_FILTER, 0);
                            krn.setString(noteObj.id, noteObj.classId, "title", 0,
                                    langId, filterName, 0);
                            krn.setBlob(noteObj.id, noteObj.classId, "config", 0, data, 0, 0);
                            krn.setBlob(noteObj.id, noteObj.classId, "exprSql", 0, dataSql, 0, 0);
                            krn.commitTransaction();
                            model.addNode(new NoteNode(noteObj, filterName,
                                    langId, parent.getChildCount()), parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id,
                                    copyNode.getKrnObj().classId, "title", 0,
                                    langId, filterName, 0);
                            model.addNode(new NoteNode(copyNode.getKrnObj(),
                                    filterName, langId, parent.getChildCount()), parent, false);
                            model.deleteNode(copyNode, true);
                            defaultDeleteOperations();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }*/

    public void renameFilter(NoteNode node, String title) {
        ((NoteTreeModel) model).renameFilter(node, title);
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
        KrnClass cls = null;
        Kernel krn = Kernel.instance();
        try {
            cls = krn.getClassByName("NoteRoot");
            KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = {filterRoot.id};
            StringValue[] svs = krn.getStringValues(ids, cls.id, "title", langId,
                    false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }
            root = new NoteNode(filterRoot, title, langId, 0);
            model = new NoteTreeModel(root);
            setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();
    }

    public DesignerTreeNode[] getSelectedNodes() {
        List<NoteNode> list = new ArrayList<NoteNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            NoteNode node = (NoteNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        NoteNode[] res = new NoteNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null ) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Создание копии справки", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String newName = cp.getElementName();
                if (newName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя справки!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    try {
                        String oldName = copyNode.toString();
                        if (!copyNode.isCutProcess()) {
                            final KrnClass cls = krn.getClassByName("Note");
                            final KrnObject noteObj = krn.createObject(cls, 0);
                            long ru=krn.getLangIdByCode("RU");
                            long kz=krn.getLangIdByCode("KZ");
                            byte[] data_ru = krn.getBlob(copyNode.getKrnObj(), "content", 0, ru, 0);
                            byte[] data_kz = krn.getBlob(copyNode.getKrnObj(), "content", 0, kz, 0);
                            krn.setString(noteObj.id, noteObj.classId, "title", 0,
                                    langId, newName, 0);
                            krn.setBlob(noteObj.id, noteObj.classId, "content", 0, data_ru, ru, 0);
                            krn.setBlob(noteObj.id, noteObj.classId, "content", 0, data_kz, kz, 0);
                            krn.writeLogRecord(SystemEvent.EVENT_COPY_FILTER, "'" + oldName + "' в '" + newName + "'");
                            model.addNode(new NoteNode(noteObj, newName,
                                    langId, parent.getChildCount()), parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id,
                                    copyNode.getKrnObj().classId, "title", 0,
                                    langId, newName, 0);
                            //NoteNode newNode=new NoteNode(copyNode.getKrnObj(),newName, langId, parent.getChildCount());
                           	model.deleteNode(copyNode, true);
                            model.addNode(copyNode, parent, false);
                            krn.writeLogRecord(SystemEvent.EVENT_MOVE_FILTER, "'" + oldName + "' в '" + newName + "' в папку '" + parent.toString() + "'");
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
       //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setSelectedNode(KrnObject obj) {
        TreeModel m = getModel();
        NoteNode node = (NoteNode) finder.findFirst((NoteNode) m.getRoot(), new KrnObjectPattern(obj));
        TreePath tpath = new TreePath(node.getPath());
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }

    public void setSelectedNode(int objId) {
        KrnObject obj = null;
        try {
            obj = kz.tamur.rt.Utils.getObjectById(objId, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (obj != null) {
            TreeModel m = getModel();
            NoteNode node = (NoteNode) finder.findFirst((NoteNode) m.getRoot(), new KrnObjectPattern(obj));
            TreePath tpath = new TreePath(node.getPath());
            setSelectionPath(tpath);
            scrollPathToVisible(tpath);
        }
    }

    public NoteNode getNoteNodeByObject(KrnObject object) {
        TreeModel m = getModel();
        NoteNode node = (NoteNode) finder.findFirst((NoteNode) m.getRoot(), new KrnObjectPattern(object));
        return node;
    }


}
