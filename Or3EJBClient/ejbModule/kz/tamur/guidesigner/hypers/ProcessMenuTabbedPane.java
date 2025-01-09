package kz.tamur.guidesigner.hypers;

import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF_FOLDER;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.rt.Utils;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * User: vital
 * Date: 15.03.2005
 * Time: 11:03:30
 */
public class ProcessMenuTabbedPane extends JPanel implements TreeSelectionListener {

    private ServicesTree tree;
    // private ProcessPropertyTableModel model = new ProcessPropertyTableModel();
    // private JTable table = new JTable(model);
    private PropertyInspector inspector = new PropertyInspector(null);
    private Tabbed tabbedPane;
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить");
    private long langId = com.cifs.or2.client.Utils.getInterfaceLangId();
    private long kzId, ruId;

    private List<ServiceNode> saveList = new ArrayList<ServiceNode>();
    private boolean canEdit = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ProcessMenuTabbedPane() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.MENU_EDIT_RIGHT);

        setLayout(new BorderLayout());
        Kernel krn = Kernel.instance();
        kzId = krn.getLangIdByCode("KZ");
        tabbedPane = new Tabbed(this);
        tabbedPane.setFont(Utils.getDefaultFont());
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ProcessTab tab = (ProcessTab) tabbedPane.getSelectedComponent();
                if (tab != null) {
                    ServiceNode n = (ServiceNode) ((ServicesTree) tab.getTree()).getSelectedNode();
                    if (n == null)
                        n = (ServiceNode) tab.getTreeModel().getRoot();
                    inspector.setObject(n.getParent() != null ? new ServiceNodeItem(n, tabbedPane.getOwner()) : null);
                }
            }
        });
        tabbedPane.fireChange();
        tree = createTree();
        tree.setShowPopupEnabled(false);
        tabbedPane.addTab("Процессы", new ProcessTab(tree, null, 0, "Процессы"));
        tree.addTreeSelectionListener(this);
        // table.getColumnModel().getColumn(0).setCellRenderer(new PropCellRenderer());
        // table.getColumnModel().getColumn(1).setCellRenderer(new PropCellRenderer());
        // table.getColumnModel().getColumn(1).setCellEditor(new PropCellEditor(new JTextField()));
        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAll();
                for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
                    ProcessTab pt = (ProcessTab) tabbedPane.getComponentAt(i);
                    if (pt.isModified())
                        pt.setModified(false);
                }
                if(saveList.size() == 0)
                	saveBtn.setEnabled(false);
            }
        });
        toolBar.add(saveBtn);
        add(toolBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        init();
    }

    private void init() {
        if (tree != null) {
            tabbedPane.setSelectedIndex(0);
            ProcessTab parent = (ProcessTab) tabbedPane.getSelectedComponent();
            loadTabs((ServiceNode) tree.getRoot(), parent);
        }
        updateTabIndeces();
        setOpaque(isOpaque);
        tabbedPane.setOpaque(isOpaque);
    }

    public void saveAll() {
    	List<ServiceNode> newSaveList = new ArrayList<ServiceNode>();
        for (int i = 0; i < saveList.size(); i++) {
            ServiceNode root = saveList.get(i);
            boolean hasException = save(root);
            if(hasException) {
            	if(!newSaveList.contains(saveList.get(i)))
            		newSaveList.add(saveList.get(i));
            }
        }
        saveList.clear();
        if(newSaveList.size() > 0) {
        	saveList.addAll(newSaveList);
        }
    }

    private boolean save(ServiceNode node) {
    	boolean hasException = false;
        Kernel krn = Kernel.instance();
        KrnObject obj = node.getKrnObj();
        try {
            krn.setLong(obj.id, obj.classId, "runtimeIndex", 0, node.getRuntimeIndex(), 0);
            String title = node.getTitle();
            if (title == null || title.length() == 0) {
                title = "";
            }
            krn.setString(obj.id, obj.classId, "title", 0, langId, title, 0);
            String titleKz = node.getTitleKz();
            if (titleKz == null || titleKz.length() == 0) {
                titleKz = "";
            }
            krn.setString(obj.id, obj.classId, "title", 0, kzId, titleKz, 0);
            if (!node.isLeaf()) {
                krn.setLong(obj.id, obj.classId, "isTab", 0, (node.isTab()) ? 1 : 0, 0);
                if (node.isTab()) {
                    String tabTitle = node.getTabName();
                    if (tabTitle == null || tabTitle.length() == 0) {
                        tabTitle = node.toString();
                    }
                    krn.setString(obj.id, obj.classId, "tabName", 0, langId, tabTitle, 0);
                    String tabTitleKz = node.getTabNameKz();
                    if (tabTitleKz == null || tabTitleKz.length() == 0) {
                        tabTitleKz = "";
                    }
                    krn.setString(obj.id, obj.classId, "tabName", 0, kzId, tabTitleKz, 0);
                }
                
            }
            if (node.isLeaf()) {
                long id = obj.classId;
                if (krn.getAttributeByName(krn.getClass(id), "isBtnToolBar") != null) {
                    krn.setLong(obj.id, id, "isBtnToolBar", 0, node.isBtnToolBar() ? 1 : 0, 0);
                    if (krn.getAttributeByName(krn.getClass(id), "hotKey") != null && !node.getHotKey().isEmpty()) {
                        krn.setString(obj.id, id, "hotKey", 0, langId, node.getHotKey(), 0);
                    }
                }
                byte[] icon = node.getIcon();
                if (icon != null && icon.length > 0) {
                    krn.setBlob(obj.id, id, "icon", 0, icon, langId, 0);
                }
            }
        } catch (KrnException e) {
        	Container comp = this.getTopLevelAncestor();
        	hasException = true;
        	MessagesFactory.showMessageDialog(comp, MessagesFactory.ERROR_MESSAGE, node + " \n " + e.getMessage());
            //e.printStackTrace();
        }
        return hasException;
    }

    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getPath().getLastPathComponent();
        if (o != null && o instanceof ServiceNode) {
            inspector.setObject(((ServiceNode) o).getParent() != null ? new ServiceNodeItem(o, this) : null);
        }
    }

    public boolean isTabsModified() {
        for (int i = 1; i < tabbedPane.getTabCount(); i++) {
            ProcessTab pt = (ProcessTab) tabbedPane.getComponentAt(i);
            if (pt.isModified) {
                return true;
            }
        }
        return false;
    }

    public ServicesTree createTree() {
        final Kernel krn = Kernel.instance();
        KrnClass cls = null;
        ServiceNode inode = null;
        ServicesTree sTree = null;
        try {
            cls = krn.getClassByName("ProcessDefRoot");
            KrnObject serviceRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = { serviceRoot.id };
            
            ruId = krn.getLangIdByCode("RU");
            kzId = krn.getLangIdByCode("KZ");
            
            AttrRequestBuilder arb = new AttrRequestBuilder(SC_PROCESS_DEF_FOLDER, krn)
            		.add("title", ruId).add("title", kzId).add("runtimeIndex")
            		.add("isTab").add("tabName", langId).add("tabName", kzId);

            String title = null;
            String titleKz = null;
            long runtimeIndex = 0;
            boolean isTab = false;
            String tabRu = null;
            String tabKz = null;
            
            List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
            if (rows.size() > 0) {
                Object[] row = rows.get(0);
            	
                title = (row[2] != null) ? (String)row[2] : "Не определён";
                titleKz = (row[3] != null) ? (String)row[3] : "";
                runtimeIndex = (row[4] != null) ? (Long)row[4] : 0;
                isTab = (row[5] != null) ? (Boolean)row[5] : false;

                tabRu = (row[6] != null) ? (String)row[6] : "";
                tabKz = (row[7] != null) ? (String)row[7] : "";
            	
            }
            inode = new ServiceNode(serviceRoot, langId == ruId ? title : titleKz, langId, 0, title, titleKz, runtimeIndex, isTab, tabRu, tabKz, "", false, null);
            inode.setRuntimeDesign(true);
            sTree = new ServicesTree(inode, null, true);
            sTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return sTree;
    }

    private void loadTabs(ServiceNode node, ProcessTab parent) {
        if (!node.isLeaf()) {
            if (node.isTab()) {
                parent = createTab(node, parent);
            }
            updateNodeIndeces(node, parent);
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                ServiceNode child = (ServiceNode) children.nextElement();
                loadTabs(child, parent);
            }
        }
    }

    public PropertyInspector getPropertyInspector() {
        return inspector;
    }

    public void updateTabIndeces() {
        List<ProcessTab> tabs = new ArrayList<ProcessTab>();
        int cnt = tabbedPane.getTabCount();
        for (int i = cnt - 1; i > 0; i--) {
            tabs.add((ProcessTab)tabbedPane.getComponentAt(i));
            tabbedPane.remove(i);
        }
        Collections.sort(tabs, new Comparator<ProcessTab>() {
            public int compare(ProcessTab o1, ProcessTab o2) {
                if (o1 != null && o2 != null) {
                    Long i1 = new Long(o1.getIndex());
                    Long i2 = new Long(o2.getIndex());
                    return i1.compareTo(i2);
                }
                return 0;
            }
        });
        for (int i = 0; i < tabs.size(); i++) {
            ProcessTab component = tabs.get(i);
            tabbedPane.addTab(component.getTabName(), component);
        }
    }

    private void updateNodeIndeces(ServiceNode parent, ProcessTab tab) {
        if (parent != null) {
            Enumeration<ServiceNode> children = parent.children();
            List<ServiceNode> childrenList = new ArrayList<ServiceNode>();
            while (children.hasMoreElements()) {
                childrenList.add(children.nextElement());
            }
            parent.removeAllChildren();
            Collections.sort(childrenList, new Comparator<ServiceNode>() {
                public int compare(ServiceNode o1, ServiceNode o2) {
                    if (o1 != null && o2 != null) {
                        Long i1 = new Long(o1.getRuntimeIndex());
                        Long i2 = new Long(o2.getRuntimeIndex());
                        return i1.compareTo(i2);
                    }
                    return 0;
                }
            });
            for (int i = 0; i < childrenList.size(); i++) {
                ServiceNode o = (ServiceNode) childrenList.get(i);
                parent.add(o);
            }
            ServicesTree.ServiceTreeModel m = tab.getTreeModel();
            TreeNode[] tp = m.getPathToRoot(parent);
            m.fireTreeStructureChanged(parent, tp, null, null);
        }
    }

    public void updateTabName(String tabName) {
        if (tabName != null && tabName.length() > 0) {
            ProcessTab pt = (ProcessTab) tabbedPane.getSelectedComponent();
            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), tabName);
            pt.setTabName(tabName);
        }
    }

    public ProcessTab createTab(ServiceNode root, ProcessTab parent) {
        ServicesTree stree = new ServicesTree(root, null, true);
        stree.addTreeSelectionListener(this);
        String tabName = root.getTabName();
        if (tabName == null || tabName.length() == 0) {
            tabName = root.toString();
        }
        ProcessTab newTab = new ProcessTab(stree, parent, root.getRuntimeIndex(), tabName);
        tabbedPane.addTab(tabName, newTab);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        TreeNode[] tp = parent.getTreeModel().getPathToRoot(root.getParent());
        if (tp != null) {
        	parent.getTreeModel().fireTreeStructureChanged(tree, tp, null, null);
        }
        parent.addChild(newTab);
        inspector.setObject(new ServiceNodeItem(root, this));
        return newTab;
    }

    public void removeTab(ServiceNode node) {
        ProcessTab tab = (ProcessTab) tabbedPane.getSelectedComponent();
        if (tab != null) {
            tab.removeThis();
            if (tab.getParentTab() != null && tab.getParentTab().getTreeModel() != null) {
                ServicesTree.ServiceTreeModel parentModel = tab.getParentTab().getTreeModel();
                TreeNode[] tp = parentModel.getPathToRoot(node.getParent());
                parentModel.fireTreeStructureChanged(tree, tp, null, null);
                tabbedPane.remove(tab);
            }
            inspector.setObject(new ServiceNodeItem(node, this));
        }
    }

    class ProcessTab extends JScrollPane {

        private ServicesTree tree;
        private ProcessTab parent;
        private long index = 0;
        private String tabName;
        private boolean isModified = false;
        private List<ProcessTab> children = new ArrayList<ProcessTab>();

        public ProcessTab(ServicesTree tree, ProcessTab parent, long index, String tabName) {
            super(tree);
            this.tree = tree;
            this.parent = parent;
            this.index = index;
            this.tabName = tabName;
            setOpaque(isOpaque);
            getViewport().setOpaque(isOpaque);
        }

        public ServicesTree.ServiceTreeModel getTreeModel() {
            return (ServicesTree.ServiceTreeModel) tree.getModel();
        }

        public long getIndex() {
            return index;
        }

        public String getTabName() {
            return tabName;
        }

        public void setTabName(String tabName) {
            this.tabName = tabName;
        }

        public void setIndex(long index) {
            this.index = index;
        }

        public boolean isModified() {
            return isModified;
        }

        public void setModified(boolean modified) {
            isModified = modified;
            if (isModified) {
                saveBtn.setEnabled(canEdit);
            }
        }

        public ProcessTab getParentTab() {
            return parent;
        }

        public void setParentTab(ProcessTab parent) {
            this.parent = parent;
        }

        public void addChild(ProcessTab t) {
            t.setParentTab(this);
            children.add(t);
        }

        public void removeThis() {
            if (parent != null) {
                for (int i = 0; i < children.size(); i++) {
                    ProcessTab processTab = children.get(i);
                    parent.addChild(processTab);
                }
            }
        }

        public ServicesTree getTree() {
            return tree;
        }

    }

    public void setModified(boolean isIndex, boolean isTabChanged, ServiceNode node) {
        ProcessTab pt = (ProcessTab) tabbedPane.getSelectedComponent();
        if (pt != null) {
        	if(!saveList.contains(node))
        		saveList.add(node);
            pt.setModified(true);
            if (isTabChanged && node.isTab()) {
                ProcessTab pt_ = createTab(node, pt);
                pt_.setModified(true);
                tabbedPane.setSelectedComponent(pt_);
            } else if (isTabChanged && pt.parent != null) {
                removeTab(node);
                tabbedPane.setSelectedComponent(pt.getParentTab());
            } else if (!node.equals(pt.getTreeModel().getRoot())) {
                    pt.getTreeModel().nodesChanged(node, null);
            }
            if (isIndex) {
                if (node.isTab()) {
                    pt.setIndex(node.getRuntimeIndex());
                    updateTabIndeces();
                    tabbedPane.setSelectedComponent(pt);
                } else {
                    ServiceNode parent_ = (ServiceNode) node.getParent();
                    if (parent_ != null) {
                        TreePath path = pt.getTree().getSelectionPath();
                        parent_.resort();
                        pt.getTreeModel().nodeStructureChanged(parent_);
                        pt.getTree().setSelectionPath(path);
                    }
                }
            }
        }
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        if (this.langId != langId) {
            this.langId = langId;
            int count = tabbedPane.getTabCount();
            for (int i = 1; i < count; ++i) {
                ProcessTab proc = (ProcessTab) tabbedPane.getComponentAt(i);
                proc.getTree().setLangId(langId);
                proc.setTabName(proc.getTree().getTabName());
                tabbedPane.setTitleAt(i, proc.getTree().getTabName());
            }
        }
    }

    class Tabbed extends OrBasicTabbedPane {
        private ProcessMenuTabbedPane owner;

        public Tabbed(ProcessMenuTabbedPane owner) {
            super();
            this.owner = owner;
        }

        public void fireChange() {
            fireStateChanged();
        }

        public ProcessMenuTabbedPane getOwner() {
            return owner;
        }
    }
}
