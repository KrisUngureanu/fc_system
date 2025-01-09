package kz.tamur.guidesigner.config;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuItem;
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

import kz.tamur.Or3Frame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.config.ConfigsTree.ConfigTreeModel;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.MainFrame;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ProjectConfiguration;

import static com.cifs.or2.client.Kernel.SC_USER;

/**
 * Created by IntelliJ IDEA. User: Администратор Date: 02.11.2004 Time: 11:47:06
 * To change this template use File | Settings | File Templates.
 */

public class ConfigurationsPanel extends JPanel implements ActionListener, TreeSelectionListener, PropertyListener, PropertyChangeListener {

    private List<ConfigNode> saveList = new ArrayList<ConfigNode>();
    private List<ConfigNode> saveItems = new ArrayList<ConfigNode>();
    
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    
    private JButton createBtn = ButtonsFactory.createToolButton("Create", "Создать");
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");

    private JSplitPane splitPane = new JSplitPane();
    
    private ConfigsTree tree;
    private PropertyInspector inspector = new PropertyInspector(null);

    private DesignerStatusBar statusBar = new DesignerStatusBar();

    private JPopupMenu pMenu = new JPopupMenu();
    private JMenuItem connectItem = createMenuItem("Подключиться");
    private JMenuItem createItem = createMenuItem("Создать");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem deleteItem = createMenuItem("Удалить");

    private JMenuItem switchOnItem = createMenuItem("Отключить");
    private JMenuItem switchOffItem = createMenuItem("Включить");

    private ConfigNode inode;
    
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JScrollPane sp;

    public ConfigurationsPanel() {
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
        	ProjectConfiguration root = krn.getChildConfigurations(null).get(0);
        	inode = new ConfigNode(root);
        	tree = new ConfigsTree(inode);
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
                pMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        tree.setSelectionRow(0);
        setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
    }

    private boolean isNodeContein(ConfigNode parent, List nodes, long baseId) {
        boolean par = false;
        for (int i = 0; i < parent.getChildCount(); ++i) {
            boolean par_ = false;
            ConfigNode node_ = (ConfigNode) parent.getChildAt(i);
            if (!node_.isLeaf() && !nodes.contains(new Long(node_.getKrnObj().id))) {
                par_ = isNodeContein(node_, nodes, baseId);
                if (!par_) {
                    parent.remove(i--);
                }
            } else {
                for (int j = 0; j < node_.getChildCount(); ++j) {
                    nodes.add(new Long(((ConfigNode) node_.getChildAt(j)).getKrnObj().id));
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
        pMenu.add(connectItem);
        connectItem.addActionListener(this);
        pMenu.add(createItem);
        createItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(saveItem);
        saveItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(deleteItem);
        deleteItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(switchOnItem);
        switchOnItem.setEnabled(false);
        pMenu.add(switchOffItem);
    }

    private void initToolBar() {
        toolBar.add(createBtn);
        createBtn.addActionListener(this);
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
        } else if (src == connectItem) {
        	ConfigNode node = (ConfigNode) tree.getSelectedNode();
        	if (node != null && node.getDsName() != null) {
        		Or3Frame.instance().connect(node.getDsName());
        		connectItem.setEnabled(false);
        	}
        } else if (src == saveItem) {
            try {
                save((ConfigNode) tree.getSelectedNode());
                Kernel.instance().saveAllConfigurations();
            } catch (KrnException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (src == deleteBtn || src == deleteItem) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
            Kernel.instance().saveAllConfigurations();
        }
    }

    private void create() {
        CreateConfigurationPanel p = new CreateConfigurationPanel();
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Создание конфигурации", p);
        dlg.setDialogEventHandler(p);
        dlg.pack();
        dlg.show();
        
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
        	ConfigTreeModel model = (ConfigTreeModel) tree.getModel();
        	try {
        		ConfigNode newNode = (ConfigNode) model.createChildNode(p.getName(), p.getDsName(), p.getSchemeName());
        		if (newNode != null) {
        			tree.expandPath(new TreePath(newNode.getPath()));
        			tree.setSelectedNode(newNode);
        		}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }

    private void deleteSelected() {
        ConfigNode node = (ConfigNode) tree.getSelectedNode();
        String mess = "Вы действительно хотите удалить конфигурацию '" + node.toString() + "'?";
        
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

    private ConfigNode checkSavedItems(ConfigNode root) {
        if (root.isModified())
            saveItems.add(root);
        
        int childCount = root.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
            	checkSavedItems((ConfigNode) root.getChildAt(i));
            }
        }
        return root;
    }

    private ConfigNode prepareSaveList(ConfigNode root) {
        if (root.isModified() && saveList.indexOf(root) == -1)
        	saveList.add(root);

        int childCount = root.getLoadedChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                prepareSaveList((ConfigNode) root.getChildAt(i));
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        prepareSaveList(inode);
        for (int i = 0; i < saveList.size(); i++) {
        	ConfigNode node = saveList.get(i);
            try {
                save(node);
            } catch (KrnException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Следующие конфигурации: \n";
            for (int i = 0; i < saveList.size(); i++) {
            	ConfigNode rn = saveList.get(i);
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


    private void save(ConfigNode node) throws KrnException, IOException {
        final Kernel krn = Kernel.instance();
        
        ProjectConfiguration pc = node.getConfig();
        krn.changeConfiguration(pc.getOldDsName(), pc);
        pc.setOldDsName(pc.getDsName());

        node.setModified(false);
        tree.repaint();
        saveItem.setEnabled(false);
        saveBtn.setEnabled(getUnsavedCount() > 0);
    }

    public void valueChanged(TreeSelectionEvent e) {
        ConfigNode node = (ConfigNode) e.getPath().getLastPathComponent();
        if (node != inode) {
            deleteBtn.setEnabled(canDelete);
            deleteItem.setEnabled(canDelete);
            createBtn.setEnabled(canCreate);
            createItem.setEnabled(canCreate);
            connectItem.setEnabled(!node.getDsName().equals(Kernel.instance().getBaseName()));
        } else {
            deleteBtn.setEnabled(false);
            deleteItem.setEnabled(false);
            createBtn.setEnabled(canCreate);
            createItem.setEnabled(canCreate);
            connectItem.setEnabled(false);
        }
        if (node.isModified()) {
            saveItem.setEnabled(canEdit);
        } else {
            saveItem.setEnabled(false);
        }
        saveBtn.setEnabled(getUnsavedCount() > 0);
        inspector.setObject(new ConfigNodeItem(node, this));
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.5);
    }

    public void propertyModified(OrGuiComponent c) {
        // tree.renameProcess(c);
        saveBtn.setEnabled(canEdit);
        saveItem.setEnabled(canEdit);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {

    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    public void propertyChange(PropertyChangeEvent evt) {
        saveBtn.setEnabled(true);
    }

    public void setModified(ConfigNode node) {
        node.setModified(true);
        ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
        saveItem.setEnabled(canEdit);
        saveBtn.setEnabled(canEdit);
    }

    public void load(KrnObject user) {
    	ConfigNode userNode = (ConfigNode) tree.find(user);
        tree.setSelectedNode(userNode);
    }
}
