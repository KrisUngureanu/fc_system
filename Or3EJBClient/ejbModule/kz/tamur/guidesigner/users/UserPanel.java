package kz.tamur.guidesigner.users;

import static com.cifs.or2.client.Kernel.SC_USER;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_ECP_EXPIRY_NOTIF;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_LIABILITY_SIGN;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_TEMP_REG_NOTIF;
import static kz.tamur.comps.Constants.ATTR_BAN_FAMILIES;
import static kz.tamur.comps.Constants.ATTR_BAN_KEYBOARD;
import static kz.tamur.comps.Constants.ATTR_BAN_LOGIN_IN_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_BAN_NAMES;
import static kz.tamur.comps.Constants.ATTR_BAN_PHONE;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_WORD;
import static kz.tamur.comps.Constants.ATTR_CHANGE_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_CHECK_CLIENT_IP;
import static kz.tamur.comps.Constants.ATTR_ECP_EXPIRY_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_LIABILITY_SIGN_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_LOGIN;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_MAX_VALID_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MIN_LOGIN_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH_ADMIN;
import static kz.tamur.comps.Constants.ATTR_MIN_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_NUMBER_FAILED_LOGIN;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN;
import static kz.tamur.comps.Constants.ATTR_TEMP_REG_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_TIME_LOCK;
import static kz.tamur.comps.Constants.ATTR_USE_ECP;
import static kz.tamur.comps.Constants.ATTR_USE_NOTALLNUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_NUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_REGISTER_SYMBOLS;
import static kz.tamur.comps.Constants.ATTR_USE_SPECIAL_SYMBOL;
import static kz.tamur.comps.Constants.ATTR_USE_SYMBOLS;
import static kz.tamur.comps.Constants.ATTR_BAN_USE_OWN_IDENTIFICATION_DATA;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.common.PasswordPolicy;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA. User: Администратор Date: 02.11.2004 Time: 11:47:06
 * To change this template use File | Settings | File Templates.
 */

public class UserPanel extends JPanel implements ActionListener, TreeSelectionListener, PropertyListener, PropertyChangeListener {

    private List<UserNode> saveList = new ArrayList<UserNode>();
    private List<UserNode> saveItems = new ArrayList<UserNode>();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton createBtn = ButtonsFactory.createToolButton("Create", "Создать");
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Поиск");
    

    private JSplitPane splitPane = new JSplitPane();
    private UserTree tree;
    private PropertyInspector inspector = new PropertyInspector(null);

    private DesignerStatusBar statusBar = new DesignerStatusBar();

    private JPopupMenu pMenu = new JPopupMenu();
    private JMenuItem createItem = createMenuItem("Создать");
    private JMenuItem addItem = createMenuItem("Выбрать из существующих...", "userExist");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem deleteItem = createMenuItem("Удалить");
    private UserNode inode;
    private PolicyNode policyNode;
    private UsersSelectedPanel usersSelectedPanel;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JScrollPane sp;

    public UserPanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.USERS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.USERS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.USERS_CREATE_RIGHT);

        initToolBar();
        add(splitPane, BorderLayout.CENTER);
        final Kernel krn = Kernel.instance();
        if (tree == null) {
            UserTree userTree = kz.tamur.comps.Utils.getUserTree();
            inode = (UserNode) userTree.getRoot();

            UserNode root = new UserNode();
            tree = new UserTree(root);
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();

            m.insertNodeInto(inode, root, 0);
            if (krn.getUser().isAdmin()) {
                policyNode = new PolicyNode();

                if (policyNode.getKrnObj() != null)
                    m.insertNodeInto(policyNode, root, 1);

            } else {
                // nodes - список групп пользователя
                List<Long> nodes = new ArrayList<Long>();
                long baseId = krn.getUser().getBase().id;

                inode.getVIPUsers(krn.getUser().getName(), nodes);
                inode.checkAccess(baseId, nodes);
            }

            tree.setRootVisible(false);
            tree.expandPath(new TreePath(inode.getPath()));
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            splitPane.setLeftComponent(sp = new JScrollPane(tree));
            splitPane.setRightComponent(inspector);
        }
        initPopup();
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                addItem.setVisible(!tree.getSelectedNode().isLeaf());
                pMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        tree.setSelectionRow(0);
        setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
    }

    private boolean isNodeContein(UserNode parent, List nodes, long baseId) {
        boolean par = false;
        for (int i = 0; i < parent.getChildCount(); ++i) {
            boolean par_ = false;
            UserNode node_ = (UserNode) parent.getChildAt(i);
            if (!node_.isLeaf() && !nodes.contains(new Long(node_.getKrnObj().id))) {
                par_ = isNodeContein(node_, nodes, baseId);
                if (!par_) {
                    parent.remove(i--);
                }
            } else if (node_.isLeaf()) {
                if (node_.getBaseStructureObj() != null && node_.getBaseStructureObj().id != baseId) {
                    parent.remove(i--);
                }
            } else {
                for (int j = 0; j < node_.getChildCount(); ++j) {
                    nodes.add(new Long(((UserNode) node_.getChildAt(j)).getKrnObj().id));
                }
                isNodeContein(node_, nodes, baseId);
                par_ = true;
            }
            if (par_)
                par = par_;
        }
        return par;
    }

    private void initPopup() {
        pMenu.add(tree.getMiFind());
        pMenu.add(createItem);
        createItem.addActionListener(this);
        pMenu.add(addItem);
        addItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(saveItem);
        saveItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(deleteItem);
        deleteItem.addActionListener(this);
    }

    private void initToolBar() {
        toolBar.add(createBtn);
        createBtn.addActionListener(this);
        toolBar.add(saveBtn);
        saveBtn.addActionListener(this);
        toolBar.add(searchBtn);
        searchBtn.addActionListener(this);
        toolBar.addSeparator();
        toolBar.add(deleteBtn);
        deleteBtn.addActionListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.WEST);
        add(panel, BorderLayout.NORTH);
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    private UsersSelectedPanel getUsersSelectedPanel() {
        UserNode selectedNode = (UserNode) tree.getSelectedNode();
        usersSelectedPanel = new UsersSelectedPanel(selectedNode);
        return usersSelectedPanel;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == createBtn || src == createItem) {
            create();
        } else if (src == saveItem) {
            try {
                save((UserNode) tree.getSelectedNode(), null, null);
            } catch (KrnException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (src == deleteBtn || src == deleteItem) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
        } else if (src == searchBtn) {
        	tree.find();
        } else if (src == addItem) {
            UserNode selectedNode = (UserNode) tree.getSelectedNode();
            usersSelectedPanel = getUsersSelectedPanel();
            usersSelectedPanel.setSelectedNode(selectedNode);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор пользователя", usersSelectedPanel);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                UserNode[] vals = usersSelectedPanel.getSelectedValues();
                for (int i = 0; i < vals.length; i++) {
                    UserNode val = vals[i];
                    if (selectedNode.isEditor() && !val.isEditor()) {
                        val.setEditor(true);
                        val.setModified(true);
                    }
                    tree.addNode(val, selectedNode);
                    try {
                        Kernel.instance().userRightsChanged(val.getName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void create() {
        CreateUserPanel rp = new CreateUserPanel();
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Создание пользователей и групп", rp, tree);
        dlg.setDialogEventHandler(rp);
        dlg.pack();
        dlg.show();
    }

    private void deleteSelected() {
        UserNode node = (UserNode) tree.getSelectedNode();
        String mess = "Вы действительно хотите удалить ";
        mess += node.isLeaf() ? "пользователя '" + node.toString() + "'?" : "папку '" + node.toString()
                + "' и всё её содержимое?";
        int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            tree.deleteNode(node);
        }
    }

    private int getUnsavedCount() {
        saveItems.clear();
        checkSavedItems(inode);
        return saveItems.size();
    }

    private UserNode checkSavedItems(UserNode root) {
        if (root.isModified()) {
            saveItems.add(root);
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((UserNode) root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private UserNode prepareSaveList(UserNode root) {
        if (root.isModified()) {
            if (saveList.indexOf(root) == -1)
                saveList.add(root);
            return root;
        } else {
            int childCount = root.getLoadedChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((UserNode) root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        if (policyNode != null && policyNode.getKrnObj() != null && policyNode.isModified()) {
            saveList.add(policyNode);
        }
        prepareSaveList(inode);
        Map<Integer, List<Long>> isMonitorValues = new HashMap<>();
        Map<Integer, List<Long>> isToolbarValues = new HashMap<>();
        for (int i = 0; i < saveList.size(); i++) {
            UserNode node = saveList.get(i);
            try {
                save(node, isMonitorValues, isToolbarValues);
            } catch (KrnException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("####" + isMonitorValues);
        System.out.println("####" + isToolbarValues);

        try {
	        Kernel krn = Kernel.instance();
	        KrnClass localConfigCls = krn.getClassByName("ConfigLocal");
	        KrnAttribute isMonitorAttr = krn.getAttributeByName(localConfigCls, "isMonitor");
	        KrnAttribute isToolBarAttr = krn.getAttributeByName(localConfigCls, "isToolBar");
	        // Массовая установка атрибута isMonitor
			for (Entry<Integer, List<Long>> entry: isMonitorValues.entrySet()) {
	        	int value = entry.getKey();
	        	List<Long> ids = entry.getValue();
	    		krn.setLong(ids, isMonitorAttr.id, (long) value, 0);
	        }
	        
	        // Массовая установка атрибута isToolbar
			for (Entry<Integer, List<Long>> entry: isToolbarValues.entrySet()) {
	        	int value = entry.getKey();
	        	List<Long> ids = entry.getValue();
	    		krn.setLong(ids, isToolBarAttr.id, (long) value, 0);
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Пользователи или группы: \n";
            for (int i = 0; i < saveList.size(); i++) {
                UserNode rn = saveList.get(i);
                mess = mess + "\"" + rn.toString() + "\"\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                saveAll();
            }
            return res;
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    public void valueChanged(TreeSelectionEvent e) {
        UserNode node = (UserNode) e.getPath().getLastPathComponent();
        if (node instanceof PolicyNode) {
            deleteBtn.setEnabled(false);
            deleteItem.setEnabled(false);
            createBtn.setEnabled(false);
            createItem.setEnabled(false);
            addItem.setEnabled(false);
        } else if (node.getKrnObj().classId == SC_USER.id) {
            deleteBtn.setEnabled(canDelete);
            deleteItem.setEnabled(canDelete);
            createBtn.setEnabled(false);
            createItem.setEnabled(false);
            addItem.setEnabled(false);
        } else if (node != inode) {
            deleteBtn.setEnabled(canDelete);
            deleteItem.setEnabled(canDelete);
            createBtn.setEnabled(canCreate);
            createItem.setEnabled(canCreate);
            addItem.setEnabled(canEdit);
        } else {
            deleteBtn.setEnabled(false);
            deleteItem.setEnabled(false);
            createBtn.setEnabled(canCreate);
            createItem.setEnabled(canCreate);
            addItem.setEnabled(canEdit);
        }
        if (node.isModified()) {
            saveItem.setEnabled(canEdit);
            saveBtn.setEnabled(canEdit);
        } else {
            saveItem.setEnabled(false);
        }
        inspector.setObject(new UserNodeItem(node, this));
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.5);
    }

    public void propertyModified(OrGuiComponent c) {
        // tree.renameProcess(c);
        saveBtn.setEnabled(canEdit);
        saveItem.setEnabled(canEdit);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {}

    public void propertyModified(OrGuiComponent c, int propertyEvent) {}

    private void save(UserNode node, Map<Integer, List<Long>> isMonitorValues, Map<Integer, List<Long>> isToolbarValues) throws KrnException, IOException {
        final Kernel krn = Kernel.instance();
        if (node instanceof PolicyNode) {
            PolicyNode pnode = (PolicyNode) node;
            KrnObject o = pnode.getKrnObj();
            PasswordPolicy policy = pnode.getPolicyWrapper();
            		
            krn.setLong(o.id, o.classId, ATTR_MAX_VALID_PERIOD, 0, policy.getMaxValidPeriod(), 0);
            krn.setLong(o.id, o.classId, ATTR_MIN_LOGIN_LENGTH, 0, policy.getMinLoginLength(), 0);
            krn.setLong(o.id, o.classId, ATTR_MIN_PASSWORD_LENGTH, 0, policy.getMinPasswordLength(), 0);
            krn.setLong(o.id, o.classId, ATTR_MIN_PASSWORD_LENGTH_ADMIN, 0, policy.getMinPasswordLengthAdmin(), 0);
            krn.setLong(o.id, o.classId, ATTR_NUMBER_PASSWORD_DUBLICATE, 0, policy.getNumPassDubl(), 0);
            krn.setLong(o.id, o.classId, ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN, 0, policy.getNumPassDublAdmin(), 0);
            krn.setLong(o.id, o.classId, ATTR_USE_NUMBERS, 0, Utils.toLong(policy.getUseNumbers()), 0);
            krn.setLong(o.id, o.classId, ATTR_USE_SYMBOLS, 0, Utils.toLong(policy.getUseSymbols()), 0);
            krn.setLong(o.id, o.classId, ATTR_USE_REGISTER_SYMBOLS, 0, Utils.toLong(policy.getUseRegisterSymbols()), 0);
            krn.setLong(o.id, o.classId, ATTR_USE_SPECIAL_SYMBOL, 0, Utils.toLong(policy.getUseSpecialSymbol()), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_NAMES, 0, Utils.toLong(policy.getBanNames()), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_FAMILIES, 0, Utils.toLong(policy.getBanFamilies()), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_PHONE, 0, Utils.toLong(policy.getBanPhone()), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_WORD, 0, Utils.toLong(policy.getBanWord()), 0);
            krn.setLong(o.id, o.classId, ATTR_MAX_PERIOD_PASSWORD, 0, policy.getMaxPeriodPassword(), 0);
            krn.setLong(o.id, o.classId, ATTR_MIN_PERIOD_PASSWORD, 0, policy.getMinPeriodPassword(), 0);
            krn.setLong(o.id, o.classId, ATTR_NUMBER_FAILED_LOGIN, 0, policy.getNumberFailedLogin(), 0);
            krn.setLong(o.id, o.classId, ATTR_TIME_LOCK, 0, policy.getTimeLock(), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_LOGIN_IN_PASSWORD, 0, Utils.toLong(policy.getBanLoginInPassword()), 0);
            krn.setLong(o.id, o.classId, ATTR_MAX_LENGTH_PASS, 0, policy.getMaxLengthPass(), 0);
            krn.setLong(o.id, o.classId, ATTR_MAX_LENGTH_LOGIN, 0, policy.getMaxLengthLogin(), 0);
            krn.setLong(o.id, o.classId, ATTR_CHANGE_FIRST_PASS, 0, Utils.toLong(policy.isChangeFirstPass()), 0);
            krn.setLong(o.id, o.classId, ATTR_MAX_PERIOD_FIRST_PASS, 0, policy.getMaxPeriodFirstPass(), 0);
            krn.setLong(o.id, o.classId, ATTR_BAN_REPEAT_CHAR, 0, Utils.toLong(policy.isBanRepeatChar()), 0);
        	krn.setLong(o.id, o.classId, ATTR_USE_NOTALLNUMBERS, 0, Utils.toLong(policy.getUseNotAllNumbers()), 0);
        	krn.setLong(o.id, o.classId, ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR, 0, Utils.toLong(policy.isBanRepAnyWhereMoreTwoChar()), 0);
        	krn.setLong(o.id, o.classId, ATTR_BAN_KEYBOARD, 0, Utils.toLong(policy.isBanKeyboard()), 0);
        	krn.setLong(o.id, o.classId, ATTR_ACTIVATE_LIABILITY_SIGN, 0, Utils.toLong(policy.isActivateLiabilitySign()), 0);
        	krn.setLong(o.id, o.classId, ATTR_LIABILITY_SIGN_PERIOD, 0, policy.getLiabilitySignPeriod(), 0);
        	krn.setLong(o.id, o.classId, ATTR_ACTIVATE_ECP_EXPIRY_NOTIF, 0, Utils.toLong(policy.isActivateECPExpiryNotif()), 0);
        	krn.setLong(o.id, o.classId, ATTR_ECP_EXPIRY_NOTIF_PERIOD, 0, policy.getECPExpiryNotifPeriod(), 0);
        	krn.setLong(o.id, o.classId, ATTR_ACTIVATE_TEMP_REG_NOTIF, 0, Utils.toLong(policy.isActivateTempRegNotif()), 0);
        	krn.setLong(o.id, o.classId, ATTR_TEMP_REG_NOTIF_PERIOD, 0, policy.getTempRegNotifPeriod(), 0);
        	krn.setLong(o.id, o.classId, ATTR_CHECK_CLIENT_IP, 0, Utils.toLong(policy.isCheckClientIp()), 0);
        	if (krn.isRNDB() || krn.hasUseECP()) {
        		krn.setLong(o.id, o.classId, ATTR_USE_ECP, 0, Utils.toLong(policy.isUseECP()), 0);
        	}
        	if (!krn.isULDB() && !krn.isRNDB()) {
        		krn.setLong(o.id, o.classId, ATTR_BAN_USE_OWN_IDENTIFICATION_DATA, 0, Utils.toLong(policy.isBanUseOwnIdentificationData()), 0);
        	}
        	List<String> chs = pnode.logChanges();
        	for (String ch : chs) {
                krn.writeLogRecord(SystemEvent.EVENT_CHANGE_PASSWORD_POLICY, ch);
        	}
        } else if (node.isLeaf()) {
            if (node.getName().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "Имя элемента не должно быть пустым!", "Сообщение", JOptionPane.ERROR_MESSAGE);
                node.setName(node.getOldName());
                ((UserTree.UserTreeModel) tree.getModel()).rename(node, node.getOldName());
                inspector.setObject(new UserNodeItem(node, this));
            } else {
                node.saveUser(isMonitorValues, isToolbarValues);
                node.updateVersion();
                KrnObject[] objs = {node.getKrnObj()};
                krn.updateUsers(objs);
            }
        } else {
        	List<String> changedProps = node.getChangedProps();
        	System.out.println("#############################################" + node.getName() + "|||ModifiedProperties: " + changedProps);
        	if (node.isNewNode() || changedProps.size() > 0) {
	            KrnObject o = node.getKrnObj();
	            KrnObject[] hyperObjs = node.getHypers();
	            KrnObject[] helps = node.getHelp();
	            KrnObject process = node.getProcess();
	            
	            KrnObject[] objs;
	            
	            if (node.isNewNode() || changedProps.contains("hyperMenu")) {
		            objs = krn.getObjects(o, "hyperMenu", 0);
		            List<Object> values = new ArrayList<Object>(objs.length);
		            for (int i = 0; i < objs.length; i++) {
		                values.add(objs[i]);
		            }
		            krn.deleteValue(o.id, o.classId, "hyperMenu", values, 0);
		            if (hyperObjs != null && hyperObjs.length > 0) {
		                for (int i = 0; i < hyperObjs.length; i++) {
		                    KrnObject hyperObj = hyperObjs[i];
		                    krn.setObject(o.id, o.classId, "hyperMenu", i, hyperObj.id, 0, false);
		                }
		            }
	            }

	            if (node.isNewNode() || changedProps.contains("process")) {
		            if (process != null) {
		                krn.setObject(o.id, o.classId, "process", 0, process.id, 0, false);
		            } else {
		                krn.deleteValue(o.id, o.classId, "process", new int[] { 0 }, 0);
		            }
	            }
	            
	            if (node.isNewNode() || changedProps.contains("helps")) {
		            objs = krn.getObjects(o, "helps", 0);
		            if (objs != null && objs.length > 0) {
		                int[] indexes = new int[objs.length];
		                for (int i = 0; i < objs.length; i++) {
		                    indexes[i] = i;
		                }
		                krn.deleteValue(o.id, o.classId, "helps", indexes, 0);
		            }
		            if (helps != null) {
		                for (int i = 0; i < helps.length; i++) {
		                    KrnObject help = helps[i];
		                    krn.setObject(o.id, o.classId, "helps", i, help.id, 0, false);
		                }
		            }
	            }
	            
	            if (node.isNewNode() || changedProps.contains("or3Rights")) {
		            Element e = node.getOr3Rights();
		            byte[] bytes = new byte[0];
		            if (e != null) {
		                XMLOutputter out = new XMLOutputter();
		                out.getFormat().setEncoding("UTF-8");
		                ByteArrayOutputStream os = new ByteArrayOutputStream();
		                e.detach();
		                out.output(new Document(e), os);
		                os.close();
		                bytes = os.toByteArray();
		            }
		            krn.setBlob(o.id, o.classId, "or3rights", 0, bytes, 0, 0);
	            }
	            
	            if (node.isNewNode() || changedProps.contains("editor")) {
	            	krn.setLong(o.id, o.classId, "editor", 0, node.isEditor() ? 1 : 0, 0);
	            }
	            
	            if (node.isNewNode() || changedProps.contains("name")) {
	            	krn.setString(o.id, o.classId, "name", 0, 0, node.getName(), 0);
	            }
	            
	            if (node.isNewNode() || changedProps.contains("isMonitor") || changedProps.contains("isToolBar")) {
		            boolean isCreated = true;
		            if (node.getConfigObj() == null) {
		            	isCreated = node.createConfigObj();
		            }
		            if (isCreated) {
			            if (node.isNewNode() || changedProps.contains("isMonitor")) {
			            	if (isMonitorValues == null) {
			            		krn.setLong(node.getConfigObj().id, node.getConfigObj().classId, "isMonitor", 0, (long) node.getMonitor(), 0);
			            	} else {
			            		List<Long> idsList = isMonitorValues.get(node.getMonitor());
			            		if (idsList == null) {
			            			idsList = new ArrayList<Long>();
			            			isMonitorValues.put(node.getMonitor(), idsList);
			            		}
			            		idsList.add(node.getConfigObj().id);
			            	}
			            }
			            if (node.isNewNode() || changedProps.contains("isToolBar")) {
			            	if (isToolbarValues == null) {
			            		krn.setLong(node.getConfigObj().id, node.getConfigObj().classId, "isToolBar", 0, (long) node.getToolBar(), 0);
			            	} else {
			            		List<Long> idsList = isToolbarValues.get(node.getToolBar());
			            		if (idsList == null) {
			            			idsList = new ArrayList<Long>();
			            			isToolbarValues.put(node.getToolBar(), idsList);
			            		}
			            		idsList.add(node.getConfigObj().id);
			            	}
			            }
		            }
	            }
	            
                node.updateVersion();
	            
	            krn.updateUser(o, node.getName());
        	}
        }
        node.setModified(false);
        tree.repaint();
        saveItem.setEnabled(false);
        saveBtn.setEnabled(getUnsavedCount() > 0);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        saveBtn.setEnabled(true);
    }

    public void setModified(UserNode node) {
        node.setModified(true);
        ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
        saveItem.setEnabled(canEdit);
        saveBtn.setEnabled(canEdit);
    }

    public void load(KrnObject user) {
        UserNode userNode = (UserNode) tree.find(user);
        tree.setSelectedNode(userNode);
    }
}