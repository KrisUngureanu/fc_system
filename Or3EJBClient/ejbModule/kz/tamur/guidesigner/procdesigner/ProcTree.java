package kz.tamur.guidesigner.procdesigner;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnException;

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
public class ProcTree extends DesignerTree {

    private String searchString = "";
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private String type;
    private ExpressionEditor exprEditor;
    public Set saveList = new HashSet();
    private static String CREAT_TEXT=" IS\nBEGIN\nEND;\n";

    public ProcTree(ExpressionEditor exprEditor,ProcNode root,String type) {
        super(root);
        this.exprEditor=exprEditor;
        this.root = root;
        this.type = type;
        model = new ProcTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());

        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.PROCS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.PROCS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.PROCS_CREATE_RIGHT);
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


    public class ProcTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public ProcTreeModel(TreeNode root) {
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
            	ProcNode node = (ProcNode) getSelectedNode();
            	ProcNode fnode=getProcNodeByObject(cp.getElementName());
            	if(node!=null && fnode!=null && !node.equals(fnode)){
                    MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Узел с таким именем существует");
            	}else if(node!=null){
            		String expr = node.getExpressionText();
            		expr=expr.replace(node.toString(), cp.getElementName());
            		node.setExpressionText(expr);
            		exprEditor.setExpression(expr);
                    node.rename(cp.getElementName());
                    TreeNode[] tp = getPathToRoot(node);
                    fireTreeNodesChanged(this, tp, null, null);
                    saveList.add(node);
            	}
            }
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            AbstractDesignerTreeNode selNode = getSelectedNode();
            int idx = selNode.getChildCount();
            ProcNode node = new ProcNode(type,title,null, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            setSelectedNode(node);
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            AbstractDesignerTreeNode selNode = (ProcNode)root;
            AbstractDesignerTreeNode inode = getSelectedNode();
            ProcNode node = getProcNodeByObject(title);
            if (node != null) {
                TreePath path = new TreePath(((DefaultMutableTreeNode) node).getPath());
                if (path != null) {
                    setSelectionPath(path);
                    scrollPathToVisible(path);
                }
            } else {
	            if (inode != null && !inode.isLeaf()) {
	                selNode = inode;
	            }
	            int idx = selNode.getChildCount();
	            node = new ProcNode(type,title.toUpperCase(Constants.OK),null, idx);
	            insertNodeInto(node, selNode, selNode.getChildCount());
	            String expr=type+" "+ title.toUpperCase(Constants.OK) +CREAT_TEXT;
	            node.setExpressionText(expr);
	            setSelectedNode(node);
	            node.setModify(true);
	            saveList.add(node);
            }
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            removeNodeFromParent(node);
            krn.deleteProcedure(node.toString(), type);
            saveList.remove(node);
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new ProcNode(type,node.toString(),null,
                        parent.getChildCount());
            }
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
            ProcNode node = (ProcNode)value;
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                l.setIcon(kz.tamur.rt.Utils.getImageIcon(node.isValid()?"ProcNodeGreen":"ProcNodeRed"));
            	
            }
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
        }else if (src == miCreateFolder) {
                createFolder();
        } else if (src == miCreateElement) {
            createElement();
        } else if (src == miCopy) {
            copyElement();
        } else if (src == miPaste) {
            pasteElement();
        } else if (src == miCut) {
            cutElement();
        } else if (src == miRename) {
            model.renameNode();
        } else if (src == miDelete) {
            AbstractDesignerTreeNode node = getSelectedNode();
            if (node != null && !node.equals(model.getRoot())) {
                try {
                    String mes = "";
                    if (node.isLeaf()) {
                        mes = "Удаление элемента '" + node.toString() + "'!\nПродолжить?";
                    } else {
                        mes = "Удалить папку '" + node.toString() +
                                "' и всё её содержимое?";
                    }
                    int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mes);
                    if (res == ButtonsFactory.BUTTON_YES) {
                        model.deleteNode(node, false);
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (src == miFind) {
            find();
        } else if (src == miFindNext) {
            keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED,
                    KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_F3,
                    KeyEvent.CHAR_UNDEFINED));
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
                            ProcNode n = new ProcNode(type,ifcName,null, parent.getChildCount());
                            n.setExpressionText(((ProcNode)copyNode).getExpressionText());
                            model.addNode(n, parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id,
                                    copyNode.getKrnObj().classId, "name", 0, 0, ifcName, 0);
                            model.addNode(new ProcNode(type,ifcName,null,parent.getChildCount()), parent, false);
                            model.deleteNode(copyNode, true);
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
        ArrayList<ProcNode> list = new ArrayList<ProcNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ProcNode node = (ProcNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        ProcNode[] res = new ProcNode[list.size()];
        list.toArray(res);
        return res;
    }

    public void setSelectedNode(String title) {
        TreeModel m = getModel();
        ProcNode node = (ProcNode)finder.findFirst(
                (ProcNode)m.getRoot(), new StringPattern(title,0));
        if (node != null) {
            TreePath tpath = new TreePath(node.getPath());
            setSelectionPath(tpath);
            scrollPathToVisible(tpath);
        }
    }

    public ProcNode getProcNodeByObject(String title) {
        TreeModel m = getModel();
        ProcNode node = (ProcNode)finder.findFirst(
                (ProcNode)m.getRoot(), new StringPattern(title,0));
        return node;
    }

    protected void initPopup() {
        pm.add(miCreateFolder);
        miCreateFolder.addActionListener(this);
        pm.add(miCreateElement);
        miCreateElement.addActionListener(this);
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
