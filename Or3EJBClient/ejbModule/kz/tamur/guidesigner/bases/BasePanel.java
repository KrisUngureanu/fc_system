package kz.tamur.guidesigner.bases;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.MainFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.users.Or3RightsNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 02.11.2004
 * Time: 11:47:06
 * To change this template use File | Settings | File Templates.
 */

public class BasePanel extends JPanel implements ActionListener, TreeSelectionListener,
        PropertyListener, PropertyChangeListener {

    private List saveList = new ArrayList();
    private List unsavedItems = new ArrayList();
    //private EmptyComponent emptyComp = new EmptyComponent();
    private JToolBar toolBar = Utils.createDesignerToolBar();
    private JButton createBtn = ButtonsFactory.createToolButton("Create", "Создать");
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");

    private JSplitPane splitPane = new JSplitPane();
    private BaseTree tree;
    private PropertyInspector inspector = new PropertyInspector(null);

    private DesignerStatusBar statusBar = new DesignerStatusBar();

    private JPopupMenu pMenu = new JPopupMenu();
    private JMenuItem createItem = createMenuItem("Создать");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem deleteItem = createMenuItem("Удалить");
    private BaseNode inode;
    private boolean canEdit = false;
    private boolean canDelete = false;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public BasePanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.BASES_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.BASES_DELETE_RIGHT);
        initToolBar();
        initPopup();
        add(splitPane, BorderLayout.CENTER);
        //add(statusBar, BorderLayout.SOUTH);
        final Kernel krn = Kernel.instance();
        if (tree == null) {
            KrnClass cls = null;
            try {
                cls = krn.getClassByName("Корень структуры баз");
                KrnObject baseRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = {baseRoot.id};
                String title = krn.getStringValues(ids, cls.id, "наименование", 0,
                        false, 0)[0].value;
                long flags = krn.getLongValues(ids, cls.id,  "flags", 0)[0].value;
                long level = krn.getLongValues(ids, cls.id,  "уровень", 0)[0].value;
                KrnObject base = krn.getObjectValues(ids, cls.id, "значение", 0)[0].value;
                boolean isPhysical = krn.getLongValues(ids, cls.id, "физически раздельная?", 0)[0].value == 1 ? true : false;
                inode = new BaseNode(baseRoot, title, flags, level, base, 0, isPhysical);
                tree = new BaseTree(inode);
                tree.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.SINGLE_TREE_SELECTION);
                JScrollPane sp = new JScrollPane(tree);
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                splitPane.setLeftComponent(sp);
                splitPane.setRightComponent(inspector);
                //inspector.getModel().addPropertyListener(this);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
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
                pMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
//        if (canEdit) {
//            inspector.getModel().addPropertyChangeListener(tree);
//            inspector.getModel().addPropertyChangeListener(this);
//        }

//        inspector.getModel().setCanEdit(canEdit);

        tree.setSelectionRow(0);
        
        setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
        tree.setOpaque(isOpaque);
       
    }

    private void initPopup() {
        User user = Kernel.instance().getUser();
        pMenu.add(createItem);
        createItem.addActionListener(this);
        createItem.setEnabled(user.hasRight(Or3RightsNode.BASES_CREATE_RIGHT));
        pMenu.add(saveItem);
        saveItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(deleteItem);
        deleteItem.addActionListener(this);
    }

    private void initToolBar() {
        User user = Kernel.instance().getUser();
        toolBar.add(createBtn);
        createBtn.addActionListener(this);
        createBtn.setEnabled(user.hasRight(Or3RightsNode.BASES_CREATE_RIGHT));
        toolBar.add(saveBtn);
        saveBtn.addActionListener(this);
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

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == createBtn || src == createItem) {
            create();
        } else if (src == saveItem) {
            save((BaseNode)tree.getSelectedNode());
        } else if (src == deleteBtn || src == deleteItem) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
        }
    }

    private void create() {
        CreateBasePanel rp = new CreateBasePanel();
        DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                "Создание узла структуры баз", rp);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            String baseName = rp.getText();
            if (baseName == null) {
                JOptionPane.showMessageDialog(this, "Неверное имя структуры!",
                        "Сообщение", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    BaseTree.BaseTreeModel model =
                            (BaseTree.BaseTreeModel)tree.getModel();
                    model.createChildNode(baseName);
                    validate();
                    repaint();
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteSelected() {
        BaseNode node = (BaseNode)tree.getSelectedNode();
        String mess = "Вы действительно хотите удалить ";
        if (node.isLeaf()) {
            mess = mess + "Структуру баз '" + node.toString() + "'?";
        } else {
            mess = mess + "Структуру баз '" + node.toString() + "' и всё её содержимое?";
        }
        int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            tree.deleteNode(node);
        }
    }

    private int getUnsavedCount() {
        unsavedItems.clear();
        checkSavedItems(inode);
        return unsavedItems.size();
    }

    private BaseNode checkSavedItems(BaseNode root) {
        if (root.isLeaf() && root.isModified()) {
            unsavedItems.add(root);
            return root;
        } else if (!root.isLeaf()) {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                	checkSavedItems((BaseNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private BaseNode prepareSaveList(BaseNode root) {
    	if (root.isModified())
    		saveList.add(root);
        if (root.isLeaf()) {
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((BaseNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        prepareSaveList(inode);
        for (int i = 0; i < saveList.size(); i++) {
            BaseNode node = (BaseNode)saveList.get(i);
            save(node);
        }
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Структуры баз: \n";
            for (int i = 0; i < saveList.size(); i++) {
                BaseNode rn = (BaseNode)saveList.get(i);
                mess = mess + rn.toString() + "\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                saveAll();
                return res;
            } else {
                return res;
            }
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    public void valueChanged(TreeSelectionEvent e) {
        BaseNode node = (BaseNode)e.getPath().getLastPathComponent();
        if (node != inode) {
            deleteBtn.setEnabled(canDelete);
            deleteItem.setEnabled(canDelete);
            if (node.isModified()) {
                saveItem.setEnabled(canEdit);
                saveBtn.setEnabled(canEdit);
            } else {
                saveItem.setEnabled(false);
            }
        } else {
            deleteBtn.setEnabled(false);
            deleteItem.setEnabled(false);
        }
        inspector.setObject(new BaseNodeItem(node,this));
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.5);
    }

    public void propertyModified(OrGuiComponent c) {
        //tree.renameProcess(c);
        saveBtn.setEnabled(canEdit);
        saveItem.setEnabled(canEdit);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {

    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    private void save(BaseNode node) {
        final Kernel krn = Kernel.instance();
        if (node != inode) {
            try {
                KrnObject o = node.getKrnObj();
                krn.setString(o.id, o.classId, "наименование", 0, 0, node.toString(), 0);
                krn.setLong(o.id, o.classId, "flags", 0, node.getFlags(), 0);
                krn.setLong(o.id, o.classId, "уровень", 0, node.getLevel(), 0);
                krn.setLong(o.id, o.classId, "физически раздельная?", 0, node.isPhysical()?1:0, 0);
                KrnObject baseObj = node.getBaseObj();
                if (baseObj != null) {
                    krn.setObject(o.id, o.classId, "значение", 0,
                            baseObj.id, 0, false);
                }
                node.setModified(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tree.repaint();
            saveItem.setEnabled(false);
            if (getUnsavedCount() > 0) {
                saveBtn.setEnabled(canEdit);
            } else {
                saveBtn.setEnabled(false);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        saveBtn.setEnabled(canEdit);
    }

    public void setModified(BaseNode node){
        node.setModified(true);
        ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
        saveItem.setEnabled(canEdit);
        saveBtn.setEnabled(canEdit);
    }

}
