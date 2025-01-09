package kz.tamur.guidesigner.users;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import kz.tamur.guidesigner.*;
import kz.tamur.rt.Utils;
import kz.tamur.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class UserTree extends DesignerTree implements PropertyChangeListener {

    private String searchString = "";
    private int typeComboIndex = 0, conditionComboIndex = 0;    
    FindPattern pattern; 

    public UserTree(final UserNode root) {
        super(root);
        this.root = root;
        model = new UserTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());
    }

    public void setSelectedNode(UserNode selectedNode) {
        TreePath tpath = new TreePath(selectedNode.getPath());
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }

    protected void defaultDeleteOperations() {

    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel(1);
        sip.setSearchText(searchString);
        sip.setTypeIndex(typeComboIndex);
        sip.setConditionIndex(conditionComboIndex);
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Поиск элемента", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            typeComboIndex = sip.getType();
            conditionComboIndex = sip.getCondition();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = null;
                    if(sip.getType() == 0){
                		pattern = new StringPattern(searchString, sip.getSearchMethod());
                	} else if (sip.getType() == 1){
                		pattern = new IDPattern(Long.parseLong(searchString));
                	} else if (sip.getType() == 2){
                		pattern = new UIDPattern(searchString);
                	}
                    fnode = finder.findFirst(node,pattern);
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

    public DesignerTreeNode[] getSelectedNodes() {
        java.util.List<UserNode> list = new ArrayList<UserNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            UserNode node = (UserNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        UserNode[] res = new UserNode[list.size()];
        list.toArray(res);
        return res;
    }

    public DesignerTreeNode[] getOnlySelectedNodes() {
        java.util.List<UserNode> list = new ArrayList<UserNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            UserNode node = (UserNode) path.getLastPathComponent();
            list.add(node);
        }
        UserNode[] res = new UserNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {

    }

    public void deleteNode(UserNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public class UserTreeModel extends DefaultTreeModel implements
            DesignerTreeModel {

        private UserNode rootNode;

        public UserTreeModel(TreeNode root) {
            super(root);
            rootNode = (UserNode) root;
        }

        public AbstractDesignerTreeNode createFolderNode(String title)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("UserFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            UserNode selNode = (UserNode) getSelectedNode();
            if (selNode == null) {
                selNode = rootNode;
            } else if (selNode.isLeaf()) {
                selNode = (UserNode) selNode.getParent();
            }
            TreePath parent = new TreePath(selNode);
            setSelectionPath(parent);
            KrnObject userObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "name", 0, 0, title, 0);
            krn.setLong(obj.id, cls.id, "isFolder", 0, 1, 0);
            int idx = selNode.getChildCount();
            krn.setObject(userObj.id, userObj.classId, "children", idx, obj.id,
                    0, false);
            UserNode node = new UserNode(obj, title, "", "", "", null, null,
                    null, null, "", "", "", false, false, false, false, false,
                    "", false, null, 0, 0, null, null, null, null, idx, true);
            insertNodeInto(node, selNode, selNode.getChildCount());
            krn.updateUser(userObj, selNode.getName());
            if (node.getParent() != null) {
                int monitor = ((UserNode) node.getParent()).getMonitor();
                if (monitor == 2) {
                    monitor = 1;
                }
                node.setMonitor(monitor);
            }
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String name)
                throws KrnException {
            return createChildNode(name, null, 0);
        }

        public AbstractDesignerTreeNode createChildNode(String name,
                char[] pass, long validPeriod) throws KrnException {
            return createChildNode(name, null, false);
        }

        /**
         * @param name
         * @param pd
         * @param isAdmin
         * @return
         * @throws KrnException
         */
        public AbstractDesignerTreeNode createChildNode(String name,
                char[] pd, boolean isAdmin) throws KrnException {
            TreeNode fnode = finder.findFirst(root, new StringPatternStrong(
                    name));
            if (fnode != null) {
                MessagesFactory.showMessageDialog(
                        (Frame) getTopLevelAncestor(),
                        MessagesFactory.ERROR_MESSAGE, "Пользователь \"" + name
                                + "\" уже существует в системе!");
                return null;
            }
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("User");
            final KrnObject obj = krn.createObject(cls, 0);
            UserNode selNode = (UserNode) getSelectedNode();
            KrnObject userObj = selNode.getKrnObj();
            KrnObject base = krn.getUser().getBase();

            // запись атрибута "логин пользователя"
            krn.setString(obj.id, cls.id, "name", 0, 0, name, 0);
            // запись атрибута "администратор"
            krn.setLong(obj.id, cls.id, "admin", 0, Utils.toLong(isAdmin), 0);
            // запись атрибута база данных
            krn.setObject(obj.id, obj.classId, "base", 0, base.id, 0, false);

            String pw = (pd != null) ? new String(pd) : "";

            PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode();
            long pdDublicate = 0;
            if (pnode != null) {
                pdDublicate = (isAdmin) ? pnode.getPolicyWrapper().getNumPassDublAdmin()
                        : pnode.getPolicyWrapper().getNumPassDubl();
            }
            if (pd != null) {
                pw = PasswordService.getInstance().encrypt(pw);
                krn.setString(obj.id, cls.id, "password", 0, 0, pw, 0);

                String at = "";
                try {
                    ClassNode cnode = krn.getClassNode(obj.classId);
                    if (pdDublicate > 0) {
                        at = "previous passwords";
                        KrnAttribute attr2 = cnode.getAttribute(at);
                        // записать новый пул паролей
                        krn.setMemo((int) obj.id, (int) attr2.id, 0, 0, pw, 0);
                    }
                    at = "дата изменения пароля";
                    KrnAttribute attr4 = cnode.getAttribute(at);

                    // записать дату изменения пароля
                    Calendar c = new GregorianCalendar(
                            TimeZone.getTimeZone("Asia/Dhaka"));
                    krn.setTime(obj.id, attr4.id, 0, c.getTime(), 0);
                } catch (Exception ex) {
                    System.out.println("Не найден атрибут \"" + at + "\"");
                }
            }
            int idx = selNode.getChildCount();
            krn.setObject(userObj.id, userObj.classId, "children", idx, obj.id,
                    0, false);

            KrnObject rusLang = LangItem.getByCode("RU").obj;
            krn.setObject(obj.id, obj.classId, "data language", 0, rusLang.id,
                    0, false);
            krn.setObject(obj.id, obj.classId, "interface language", 0,
                    rusLang.id, 0, false);
            if (pdDublicate > 0) {
                // необходимо сохранить пароль в ПУЛе паролей
                try {
                    krn.setMemo((int) obj.id, (int) cls.id,
                            "previous passwords", 0, 0, pw, 0);
                } catch (Exception ex) {
                    System.out
                            .println("Не найден атрибут \"User.previous passwords\"");
                }
            }

            UserNode node = new UserNode(obj, name, pw, "", "", base, rusLang,
                    rusLang, null, "", "", "", false, isAdmin, false, false,
                    false, "", false, null, 0, 0, null, null, null, null, idx, true);
            //node.setModified(true); 
            //node.setPasswordChanged(true); 
            insertNodeInto(node, selNode, selNode.getChildCount());
            setSelectedNode(node);

            krn.updateUser(userObj, selNode.getName());
            krn.userCreated(name);
            if (node.getParent() != null) {
                int monitor = ((UserNode) node.getParent()).getMonitor();
                if (monitor == 2) {
                    monitor = 1;
                }
                node.setMonitor(monitor);
            }
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            UserNode parent = (UserNode) node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values = Collections.singletonList((Object) node
                    .getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children",
                    values, 0);
            final KrnClass cls = krn.getClassByName("User");

            if (!isMove) {
                TreeNode tn = finder.findFirst(root,
                        new KrnObjectPattern(node.getKrnObj()));
                if (tn == null) {
                    krn.deleteObject(node.getKrnObj(), 0);
                    if (node.getKrnObj().classId == cls.id) {
                        krn.userDeleted(((UserNode) node).getName());
                    }
                } else {
                    if (node.getKrnObj().classId == cls.id) {
                        krn.userRightsChanged(((UserNode) node).getName());
                    }
                }
            } else {
                if (node.getKrnObj().classId == cls.id) {
                    krn.userRightsChanged(((UserNode) node).getName());
                }
            }
            krn.updateUser(parentObj, parent.getName());
        }

        public void addNode(AbstractDesignerTreeNode node,
                AbstractDesignerTreeNode parent, boolean isMove)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                UserNode unode = (UserNode) node;
                node = new UserNode(node.getKrnObj(), node.toString(),
                        unode.getPassword(), unode.getSign(),
                        unode.getSignKz(), unode.getBaseStructureObj(),
                        unode.getDataLangObj(), unode.getIfcLangObj(),
                        unode.getIfcObject(), unode.getDoljnost(),
                        unode.getEmail(), unode.getIpAddress(),
                        unode.isEditor(), unode.isAdmin(), unode.isDeveloper(),
                        unode.isBlocked(), unode.isMulti(), unode.getIIN(),
                        unode.isOnlyECP(),unode.getConfigObj(), unode.getMonitor(), unode.getToolBar(), 
                        unode.getHypers(), unode.getProcess(), unode.getOr3Rights(), unode.getHelp(), parent.getChildCount(), false);
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId, "children",
                    parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
            krn.updateUser(parentObj, ((UserNode) parent).getName());
        }

        public void rename(UserNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

        public void renameNode() {

        }

    }

    public void addNode(UserNode node, UserNode parent) {
        try {
            model.addNode(node, parent, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            setOpaque(true);
            UserNode node = (UserNode) value;
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(node.isModified() ? Color.yellow : Color.white);
            } else {
                if (isOpaque) {
                    setBackground(Utils.getLightSysColor());
                }
                setForeground(node.isModified() ? Color.red : Color.black);
            }
            setFont(Utils.getDefaultFont());
            if (!leaf) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon(expanded?"Open":"CloseFolder"));
            } else {
                setIcon(kz.tamur.rt.Utils.getImageIcon("userNode"));
                //setIcon(kz.tamur.rt.Utils.getImageIcon(selected ? "userNodeSel" : "userNode"));
            }
            if (isOpaque && row == dragRow) {
                setBackground(Utils.getSysColor());
            }
            setText(value.toString());
            setOpaque(selected || isOpaque);
            return this;
        }

    }

    public void propertyChange(PropertyChangeEvent evt) {
        UserNode node = (UserNode) evt.getOldValue();
        if (node != null) {
            if ("name".equals(evt.getPropertyName())) {
                TreeNode[] tp = ((UserTreeModel) model).getPathToRoot(node);
                ((UserTreeModel) model).fireTreeNodesChanged(this, tp, null,
                        null);
            }
            node.setModified(true);
            repaint();
        }
    }
}
