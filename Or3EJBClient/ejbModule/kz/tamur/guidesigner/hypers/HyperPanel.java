package kz.tamur.guidesigner.hypers;

import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.Or3Frame;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.EmptyComponent;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.LangItem;
import kz.tamur.util.LanguageCombo;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 02.11.2004
 * Time: 11:47:06
 * To change this template use File | Settings | File Templates.
 */

public class HyperPanel extends JPanel implements ActionListener, TreeSelectionListener{

    private List saveList = new ArrayList();
    private List saveItems = new ArrayList();
    private EmptyComponent emptyComp = new EmptyComponent();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Поиск");
    
    private JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JSplitPane procSplitPane = new JSplitPane();
    private JSplitPane splitPane = new JSplitPane();
    private HyperTree tree;
    private PropertyInspector inspector;

    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private LanguageCombo langSelector = new LanguageCombo();

    private ProcessMenuTabbedPane tabbedContent;

    private HyperNode inode;

    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");

    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private List<HyperNode> systemNodeList = new ArrayList();
    
    public HyperPanel() {
        super(new BorderLayout());
        tabbedContent = new ProcessMenuTabbedPane();
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.MENU_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.MENU_DELETE_RIGHT);
        inspector = new PropertyInspector(null);

        initToolBar();
        mainSplitPane.setLeftComponent(splitPane);
        procSplitPane.setLeftComponent(tabbedContent);
        procSplitPane.setRightComponent(tabbedContent.getPropertyInspector());
        mainSplitPane.setRightComponent(procSplitPane);
        add(mainSplitPane, BorderLayout.CENTER);
        updateStatusBar();

        final Kernel krn = Kernel.instance();
        if (tree == null) {
            KrnClass cls = null;
            KrnObject[] objs = null;
            try {
                cls = krn.getClassByName("MainTree");
                KrnObject hyperRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = { hyperRoot.id };
                String title = krn.getStringValues(ids, cls.id, "title", krn.getLangIdByCode("RU"), false, 0)[0].value;

                com.cifs.or2.kernel.StringValue[] val = krn.getStringValues(ids, cls.id, "title", krn.getLangIdByCode("KZ"), false, 0);
                String titleKz = val == null || val.length == 0 ? null : val[0].value;
                
                inode = new HyperNode(hyperRoot, title, titleKz, null, null, 0, null, null,
                        com.cifs.or2.client.Utils.getInterfaceLangId(), false, null);
                
                KrnClass hiperCls = krn.getClassByName("HiperTree");
                KrnAttribute isSystem = krn.getAttributeByName(hiperCls, "isSystem");
                if (isSystem != null) 
                	objs = krn.getObjectsByAttribute(hiperCls.id, isSystem.id, 0, ComparisonOperations.CO_EQUALS, 1, 0);
                
                if (objs != null && objs.length > 0) {
                	for(KrnObject obj : objs) {
		                long[] idsHiper = { obj.id };
		                String titleHiper = krn.getStringValues(idsHiper, hiperCls.id, "title", krn.getLangIdByCode("RU"), false, 0)[0].value;
		                com.cifs.or2.kernel.StringValue[] valHiper = krn.getStringValues(idsHiper, hiperCls.id, "title", 
		                		krn.getLangIdByCode("KZ"), false, 0);
		                String titleKzHiper = valHiper == null || valHiper.length == 0 ? null : valHiper[0].value;
		                HyperNode node = new HyperNode(obj, titleHiper, titleKzHiper, null, null, 0, null, null,
		                        com.cifs.or2.client.Utils.getInterfaceLangId(), false, null);
		                systemNodeList.add(node);
                	}
                }
                
                tree = new HyperTree(inode, true);
                tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                JScrollPane sp = new JScrollPane(tree);
                splitPane.setLeftComponent(sp);
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                splitPane.setRightComponent(inspector);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        tree.addTreeSelectionListener(this);
//        if (canEdit) {
//            inspector.getModel().addPropertyChangeListener(tree);
//            inspector.getModel().addPropertyChangeListener(this);
//        }
        statusBar.addEmptySpace();
        statusBar.addSeparator();
        statusBar.addAnyComponent(currentDbName);
        statusBar.addSeparator();
        currentUserLable.setIcon(getImageIcon("User"));
        statusBar.addAnyComponent(currentUserLable);
        statusBar.addSeparator();
        dsLabel.setIcon(getImageIcon("HostConn"));
        dsLabel.setIconTextGap(10);
        statusBar.addAnyComponent(dsLabel);
        statusBar.addSeparator();
        serverLabel.setIcon(getImageIcon("PortConn"));
        serverLabel.setIconTextGap(10);
        statusBar.addAnyComponent(serverLabel);
        statusBar.addSeparator();
        statusBar.addLabel(" Язык меню: ");
        statusBar.addAnyComponent(langSelector);
        statusBar.addCorner();
        add(statusBar, BorderLayout.SOUTH);
        langSelector.addActionListener(this);

        tree.setSelectionRow(0);
        
        setOpaque(isOpaque);
        mainSplitPane.setOpaque(isOpaque);
        procSplitPane.setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
    }


    private void initToolBar() {
        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
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
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        LangItem li = (LangItem)langSelector.getSelectedItem();
        if (src == deleteBtn) {// || src == deleteItem) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
        } else if(src == searchBtn) {
        	tree.find();
        }else if (src == langSelector) {
            tree.setLang(li.obj.id);
            tabbedContent.setLangId(li.obj.id);
        }
    }

    private void deleteSelected() {
        HyperNode node = (HyperNode)tree.getSelectedNode();
        boolean reloadOrRightsTree = false;
        String mess = "Вы действительно хотите удалить ";
        if (node.isLeaf()) {
            mess = mess + "элемент '" + node.toString() + "'?";
        } else {
            mess = mess + "папку '" + node.toString() + "' и всё её содержимое?";
        }
        int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            TreeNode parent = node.getParent();
            if(parent instanceof HyperNode) {
            	HyperNode par = (HyperNode) parent;
            	if(par.getKrnObj().uid.equals("9.30198536")) {
            		reloadOrRightsTree = true;
            	}
            }
            tree.deleteNode(node);
            if(reloadOrRightsTree)
            	Or3RightsNode.addDynamicNodes();
        }
    }

    private int getUnsavedCount() {
        saveItems.clear();
        checkSavedItems(inode);
        return saveItems.size();
    }

    private HyperNode checkSavedItems(HyperNode root) {
        if (root.isLeaf() && root.isModified()) {
            saveItems.add(root);
            return root;
        } else {
            if (root.isModified()) {
                saveItems.add(root);
            }
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((HyperNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private HyperNode prepareSaveList(HyperNode root) {
        if (root.isLeaf() && root.isModified()) {
            saveList.add(root);
            return root;
        } else {
            if (root.isModified()) {
                saveList.add(root);
            }
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((HyperNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        prepareSaveList(inode);
        boolean reloadOrRightsTree = false;
        for (int i = 0; i < saveList.size(); i++) {
            HyperNode node = (HyperNode)saveList.get(i);
            save(node);
            TreeNode parent = node.getParent();
            if(parent instanceof HyperNode) {
            	HyperNode par = (HyperNode) parent;
            	if(par.getKrnObj().uid.equals("9.30198536")) {
            		reloadOrRightsTree = true;
            	}
            }
        }
        saveBtn.setEnabled(false);
        if(reloadOrRightsTree) {
        	Or3RightsNode.addDynamicNodes();
        }
    }

    public int processExit() {
        int res = ButtonsFactory.BUTTON_NOACTION;
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Элементы: \n";
            for (int i = 0; i < saveList.size(); i++) {
                HyperNode rn = (HyperNode)saveList.get(i);
                mess = mess + rn.toString() + "\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                saveAll();
            }
        }
        if (tabbedContent.isTabsModified()) {
            res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, "Модифицировано меню процессов!\n" +
                    "Сохранить изменения?");
            if (res == ButtonsFactory.BUTTON_YES) {
                tabbedContent.saveAll();
                return res;
            } else {
                return res;
            }
        } else {
            return res;
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        if (e.getPath() != null) {
            HyperNode node = (HyperNode)e.getPath().getLastPathComponent();
            if (node.isModified()) {
                saveBtn.setEnabled(canEdit);
            }
            if (node.isLeaf()) {
                deleteBtn.setEnabled(canDelete);
            } else {
                if (node == inode || systemNodeList.contains(node)) {
                    deleteBtn.setEnabled(false);
                } else {
                    deleteBtn.setEnabled(canDelete);
                }
            }
            inspector.setObject(new HyperNodeItem(node, this));
        }
    }

    public void placeDivider() {
        mainSplitPane.setDividerLocation(0.4);
        splitPane.setDividerLocation(0.7);
        procSplitPane.setDividerLocation(0.7);
    }
    private void save(HyperNode node) {
        final Kernel krn = Kernel.instance();
        try {
            KrnObject o = node.getKrnObj();
            krn.setString(o.id, o.classId, "title", 0,((LangItem)langSelector.getSelectedItem()).obj.id,node.toString(), 0);
            krn.setString(o.id, o.classId, "title", 0,langSelector.getKazLang().id,node.getTitleKz(), 0);
            krn.setLong(o.id, o.classId, "runtimeIndex", 0, node.getRuntimeIndex(), 0);
            KrnObject ifcObj = node.getIfcObject();
            if (node.isLeaf()) {
                if (ifcObj != null) {
                    krn.setObject(o.id, o.classId, "hiperObj", 0,
                            node.getIfcObject().id, 0, false);
                } else {
                    krn.deleteValue(o.id, o.classId, "hiperObj", new int[] {0}, 0);
                }
                krn.setLong(o.id, o.classId, "isDialog", 0, (node.isDialog()) ? 1 : 0, 0);
                krn.setLong(o.id, o.classId, "isChangeable", 0, (node.isChangeable()) ? 1 : 0, 0);
                if(node.getIcon() != null)
                	krn.setBlob(o.id, o.classId, "uiIcon", 0, node.getIcon(), 0, 0);
            }
            node.setModified(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tree.repaint();
        //saveItem.setEnabled(false);
        if (getUnsavedCount() > 0) {
            saveBtn.setEnabled(canEdit);
        } else {
            saveBtn.setEnabled(false);
        }
    }

    public void setModified(boolean isIndex,HyperNode node){
        saveBtn.setEnabled(canEdit);
        if(canEdit) node.setModified(true);
        HyperTree.HyperTreeModel model= (HyperTree.HyperTreeModel)tree.getModel();
        model.nodeChanged(node);
        if(isIndex){
            HyperNode parent_=((HyperNode)node.getParent());
            if (parent_ != null) {
                TreePath path=tree.getSelectionPath();
                parent_.resort();
                model.nodeStructureChanged(parent_);
                tree.setSelectionPath(path);
            }
        }
    }

    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText(Or3Frame.getCurrentDbName());
        currentUserLable.setText(Or3Frame.getCurrentUserName());
    }
}
