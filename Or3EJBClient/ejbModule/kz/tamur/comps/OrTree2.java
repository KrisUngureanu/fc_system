package kz.tamur.comps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.NodeFinder;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.StringPattern;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import static kz.tamur.rt.Utils.createMenuItem;

import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrTree2 extends JTree implements OrGuiComponent, ActionListener {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private static final long serialVersionUID = 1L;
    private String searchString;
    protected NodeFinder finder = new NodeFinder();
    protected boolean useCheck = false;
    public static PropertyNode PROPS = new TreePropertyRoot();

    private int mode;
    protected Element xml;
    protected boolean isSelected;
    protected OrFrame frame;
    protected Cache cash;
    protected static final Kernel krn_ = Kernel.instance();
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private JButton treeFieldButton = null;

    private TreeAdapter2 adapter;
    private String descriptionUID;

    private JPopupMenu nodeOperations_ = new JPopupMenu();
    private JMenuItem nodeRenameItem_;
    private JMenuItem nodeCreateItem_;
    private JMenuItem nodeCreateBeforeItem_;
    private JMenuItem nodeCreateAfterItem_;
    private JMenuItem nodeCreateWithHistoryItem_;
    private JMenuItem nodeDeleteItem_;
    private JMenuItem nodeSelectChildren_;
    private JMenuItem expandItem;
    private JMenuItem collapsItem;

    protected ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    private boolean isFolderSelect = false;
    private boolean isFolderAsLeaf = false;
    private String varName;
    boolean firstOpen = true;
    private boolean transparent = false;
    private Font font = null;
    private int viewType = Constants.FILES;
    private int sortType=0;
    private boolean showSearchLine = false;

    public OrTree2() {
        PropertyValue pv;

        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(getProperties().getChild("extended").getChild("transparent"));
            transparent = pv.booleanValue();
            pv = getPropertyValue(getProperties().getChild("view").getChild("font").getChild("fontG"));
            if (pv != null) {
                font = pv.fontValue();
            }
            pv = getPropertyValue(getProperties().getChild("view").getChild("folderAsLeaf"));
            if (pv.isNull()) {
            	isFolderAsLeaf = ((Boolean) getProperties().getChild("view").getChild("folderAsLeaf").getDefaultValue()).booleanValue();
            } else {
            	isFolderAsLeaf = pv.booleanValue();
            }
        }
        if (font == null) {
            setCellRenderer(new OrTreeCellRenderer(false, transparent, isFolderAsLeaf));
        } else {
            setCellRenderer(new OrTreeCellRenderer(false, transparent, isFolderAsLeaf, font));
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        updateProperties();
    }

    protected OrTree2(Element xml, int mode, OrFrame frame) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        PropertyNode view = PROPS.getChild("view");
        PropertyValue pv;
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(PROPS.getChild("extended").getChild("transparent"));
            transparent = pv.booleanValue();
            pv = getPropertyValue(view.getChild("font").getChild("fontG"));
            if (pv != null) {
                font = pv.fontValue();
            }
            pv = getPropertyValue(view.getChild("folderAsLeaf"));
            if (pv.isNull()) {
            	isFolderAsLeaf = ((Boolean) view.getChild("folderAsLeaf").getDefaultValue()).booleanValue();
            } else {
            	isFolderAsLeaf = pv.booleanValue();
            }
        }
        if (font == null) {
            setCellRenderer(new OrTreeCellRenderer(false, transparent, isFolderAsLeaf));
        } else {
            setCellRenderer(new OrTreeCellRenderer(false, transparent, isFolderAsLeaf, font));
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }

        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = (String) pv.resourceStringValue().first;
                    byte[] toolTip = frame.getBytes(toolTipUid);
                    if (toolTip != null) {
                        setToolTipText(new String(toolTip));
                    }
                }
            }
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    updateToolTip();
                }
            });

            pv = getPropertyValue(view.getChild("popup").getChild("show"));
            if (!pv.isNull() && pv.booleanValue()) {
                addMouseListener(new TreeMouseListener());
                PropertyNode items = view.getChild("popup").getChild("items");
                // if (!isReadOnly) {
                pv = getPropertyValue(items.getChild("renameNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("renameNode");
                    nodeRenameItem_ = createMenuItem(menuName, "Rename");
                    nodeRenameItem_.addActionListener(this);
                    nodeOperations_.add(nodeRenameItem_);
                }

                pv = getPropertyValue(items.getChild("createNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("createNode");
                    nodeCreateItem_ = createMenuItem(menuName, "Create");
                    nodeCreateItem_.addActionListener(this);
                    nodeOperations_.addSeparator();
                    nodeOperations_.add(nodeCreateItem_);
                    nodeOperations_.addSeparator();
                }
                pv = getPropertyValue(items.getChild("createAndBindNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("createNodeAndBind");
                    nodeCreateWithHistoryItem_ = createMenuItem(menuName, "Create");
                    nodeCreateWithHistoryItem_.addActionListener(this);
                    nodeOperations_.add(nodeCreateWithHistoryItem_);
                }
                pv = getPropertyValue(items.getChild("deleteNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("deleteNode");
                    nodeDeleteItem_ = createMenuItem(menuName, "Delete");
                    nodeDeleteItem_.addActionListener(this);
                    nodeOperations_.add(nodeDeleteItem_);
                }

                pv = getPropertyValue(items.getChild("expandNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("expandNode");
                    expandItem = createMenuItem(menuName, "ExpandTree");
                    expandItem.addActionListener(this);
                    nodeOperations_.add(expandItem);
                }
                pv = getPropertyValue(items.getChild("collapseNode"));
                if (pv.isNull() || pv.booleanValue()) {
                    String menuName = res.getString("collapseNode");
                    collapsItem = createMenuItem(menuName, "CollapseTree");
                    collapsItem.addActionListener(this);
                    nodeOperations_.add(collapsItem);
                }
            }

            pv = getPropertyValue(view.getChild("folderSelect"));
            if (pv.isNull()) {
                isFolderSelect = ((Boolean) view.getChild("folderSelect").getDefaultValue()).booleanValue();
            } else {
                isFolderSelect = pv.booleanValue();
            }

            pv = getPropertyValue(view.getChild("viewType"));
            if (pv.isNull()) {
                viewType = ((EnumValue) view.getChild("viewType").getDefaultValue()).code;
            } else {
                viewType = pv.intValue();
            }
            pv = getPropertyValue(PROPS.getChild("ref").getChild("sortPath"));
            if (!pv.isNull()) {
                sortType = 1;
            }
            addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    JTree t = (JTree) e.getSource();

                    TreePath[] paths = t.getSelectionPaths();
                    if (paths == null || paths.length == 0) {
                        adapter.setSelectedNodes(new Node[0]);
                    } else {
                        Node[] nodes = new Node[paths.length];
                        for (int i = 0; i < paths.length; i++) {
                            nodes[i] = (Node) paths[i].getLastPathComponent();
                        }
                        adapter.setSelectedNodes(nodes);
                    }

                    Container cnt = t.getTopLevelAncestor();
                    if (cnt instanceof DesignerDialog) {
                        TreePath path = t.getSelectionPath();
                        TreeNode node = (path != null) ? (TreeNode) path.getLastPathComponent() : null;
                        DesignerDialog dlg = (DesignerDialog) cnt;
                        if (node != null && (node.isLeaf() || isFolderSelect)) {
                            dlg.setOkEnabled(true);
                        } else {
                            dlg.setOkEnabled(false);
                        }
                    }
                }
            });

            KeyAdapter ka = new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    final Window cnt = (Window) getTopLevelAncestor();
                    if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                        find(cnt);
                    } else if (e.getKeyCode() == KeyEvent.VK_F3 && e.isShiftDown()) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                TreeNode fnode = finder.findPrev();
                                if (fnode != null) {
                                    TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                    if (path != null) {
                                        setSelectionPath(path);
                                        scrollPathToVisible(path);
                                    }
                                } else {
                                    MessagesFactory.showMessageSearchFinished(cnt);
                                }
                            }
                        });
                        t.start();
                    } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                TreeNode fnode = finder.findNext();
                                if (fnode != null) {
                                    TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                    if (path != null) {
                                        setSelectionPath(path);
                                        scrollPathToVisible(path);
                                    }
                                } else {
                                    MessagesFactory.showMessageSearchFinished(cnt);
                                }
                            }
                        });
                        t.start();
                    }
                }
            };
            addKeyListener(ka);
        }
        updateProperties();
    }

    @Override
    public void expandPath(TreePath path) {
        super.expandPath(path);
        // при первом открытии дерева выделяет первый(корневой) элемент
        if (firstOpen) {
            setSelectionPath(getPathForRow(0));
            firstOpen = false;
        }
    }

    public void find(final Window parent) {
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        sip.setSearchMethod(ComparisonOperations.CO_CONTAINS);
        if (searchString != null) {
            sip.setSearchText(searchString);
        }
        final DesignerDialog dlg = new DesignerDialog(parent, "Поиск элемента", sip);
        dlg.show();

        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    CursorToolkit.startWaitCursor(dlg);
                    searchString = sip.getSearchText();
                    Node node = (Node) adapter.getModel().getRoot();
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                    CursorToolkit.stopWaitCursor(dlg);
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(parent);
                    }
                }

            });
            t.start();
        }
    }

    public void setTreeTableRenderer() {
        setCellRenderer(new OrTreeCellRenderer(true, transparent, isFolderAsLeaf, font));
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        updateProperties();
    }

    protected void updateProperties() {
        PropertyNode pn = getProperties().getChild("extended");
        // прозрачность компонента(да/нет)
        PropertyValue pv = getPropertyValue(pn.getChild("transparent"));
        setTransparent(!pv.booleanValue());
        
        pv = getPropertyValue(getProperties().getChild("view").getChild("showSearchLine"));
        if (pv.isNull()) {
        	showSearchLine = ((Boolean) getProperties().getChild("view").getChild("showSearchLine").getDefaultValue()).booleanValue();
        } else {
        	showSearchLine = pv.booleanValue();
        }
        pv = getPropertyValue(getProperties().getChild("view").getChild("folderSelect"));
        if (!pv.isNull()) {
            isFolderSelect = pv.booleanValue();
        } else {
            isFolderSelect = ((Boolean)getProperties().getChild("view").getChild("folderSelect").getDefaultValue()).booleanValue();
        }

        pn = getProperties().getChild("ref").getChild("sortPath");
        // доступность пунктов меню перемещения узлов
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            sortType = 1;
        }
        
        Utils.processBorderProperties(this, frame);
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            changeTitles(res);
        }
        if (adapter != null) {
            try {
                adapter.setLangId(langId);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public int getTabIndex() {
        return -1;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    public JButton getTreeFieldButton() {
        return treeFieldButton;
    }

    public void setTreeFieldButton(JButton treeFieldButton) {
        this.treeFieldButton = treeFieldButton;
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public TreeAdapter2 getAdapter() {
        return adapter;
    }

    protected void setAdapter(TreeAdapter2 adapter) {
        this.adapter = adapter;
        setModel(adapter.getModel());
        setRootVisible(true);

        // Если обеспечивается порядок элементов и разрешено создание узлов
        if (adapter.isOrderSupported() && nodeCreateItem_ != null) {
            int index = nodeOperations_.getComponentIndex(nodeCreateItem_);

            String menuName = res.getString("createNodeBefore");
            nodeCreateBeforeItem_ = createMenuItem(menuName, "Create");
            nodeCreateBeforeItem_.addActionListener(this);
            nodeOperations_.insert(nodeCreateBeforeItem_, ++index);

            menuName = res.getString("createNodeAfter");
            nodeCreateAfterItem_ = createMenuItem(menuName, "Create");
            nodeCreateAfterItem_.addActionListener(this);
            nodeOperations_.insert(nodeCreateAfterItem_, ++index);
        }
    }

    private void changeTitles(ResourceBundle res) {
        if (nodeRenameItem_ != null) {
            nodeRenameItem_.setText(res.getString("renameNode"));
        }
        if (nodeCreateItem_ != null) {
            nodeCreateItem_.setText(res.getString("createNode"));
        }
        if (nodeCreateWithHistoryItem_ != null) {
            nodeCreateWithHistoryItem_.setText(res.getString("createNodeAndBind"));
        }
        if (nodeDeleteItem_ != null) {
            nodeDeleteItem_.setText(res.getString("deleteNode"));
        }
        if (expandItem != null) {
            expandItem.setText(res.getString("expandNode"));
        }
        if (collapsItem != null) {
            collapsItem.setText(res.getString("collapseNode"));
        }
    }

    public void setEnabled(boolean isEnabled) {
        if (nodeRenameItem_ != null) {
            nodeRenameItem_.setEnabled(isEnabled);
        }
        if (nodeCreateItem_ != null) {
            nodeCreateItem_.setEnabled(isEnabled);
        }
        if (nodeCreateWithHistoryItem_ != null) {
            nodeCreateWithHistoryItem_.setEnabled(isEnabled);
        }
        if (nodeDeleteItem_ != null) {
            nodeDeleteItem_.setEnabled(isEnabled);
        }
    }

    public void expandAll(Node node) {
        if (node != null && !node.isLeaf()) {
            expandPath(new TreePath(node.getPath()));
            Enumeration childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                Node child = (Node) childNodes.nextElement();
                expandAll(child);
            }
        }
    }

    public void collapseAll(Node node) {
        if (node != null) {
            Enumeration childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                Node child = (Node) childNodes.nextElement();
                collapseAll(child);
            }
            if (!node.isLeaf()) {
                collapsePath(new TreePath(node.getPath()));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == nodeCreateItem_) {
                // Создаем узел внутри выбранного узла
                createNewNode(TreeAdapter2.INSERT_CHILD);
            } else if (src == nodeCreateBeforeItem_) {
                // Создаем узел перед выбранным узлом
                createNewNode(TreeAdapter2.INSERT_BEFORE);
            } else if (src == nodeCreateAfterItem_) {
                // Создаем узел после выбранного узла
                createNewNode(TreeAdapter2.INSERT_AFTER);
            } else if (src == nodeDeleteItem_) {
                ((TreeAdapter2) adapter).deleteNodes(this);
            } else if (src == nodeRenameItem_) {
                renameNode();
            } else if (src == nodeSelectChildren_) {
                TreePath path = getSelectionPath();
                if (path != null) {
                    List<TreePath> paths = new LinkedList<TreePath>();
                    getChildPaths(path, paths);
                    setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
                }
            } else if (src == expandItem) {
                expandAll(adapter.getSelectedNode());
            } else if (src == collapsItem) {
                collapseAll(adapter.getSelectedNode());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createNewNode(int position) throws KrnException {
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.CREATE_ELEMENT_TYPE, "");
        DesignerDialog dlg = null;
        Container cnt = getTopLevelAncestor();
        if (cnt instanceof Dialog) {
            dlg = new DesignerDialog((Window) cnt, res.getString("createNodeTitle"), cp);
        } else {
            dlg = new DesignerDialog((Frame) cnt, res.getString("createNodeTitle"), cp);
        }
        dlg.setLanguage(frame.getInterfaceLang().id);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            adapter.createNode(cp.getElementName(), position, this);
        }
    }

    private void renameNode() throws KrnException {
        Node n = adapter.getSelectedNode();
        if (n != null) {
            Container cnt = getTopLevelAncestor();
            DesignerDialog dlg = null;
            String name = n.toString(adapter.getRowForNode(n));

            CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, name, frame.getInterfaceLang().id);
            if (cnt instanceof Dialog) {
                dlg = new DesignerDialog((Dialog) cnt, res.getString("renameNodeTitle"), cp);
            } else {
                dlg = new DesignerDialog((Frame) cnt, res.getString("renameNodeTitle"), cp);
            }
            dlg.setLanguage(frame.getInterfaceLang().id);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String title = cp.getElementName();
                adapter.renameNode(title, this);
            }
        }
    }

    private void getChildPaths(TreePath path, List<TreePath> paths) {
        paths.add(path);
        Node node = (Node) path.getLastPathComponent();
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            Node child = (Node) node.getChildAt(i);
            TreePath childPath = path.pathByAddingChild(child);
            getChildPaths(childPath, paths);
        }
    }

    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }

    private class TreeMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        private void showNodeOperations(MouseEvent e) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null && !isPathSelected(path)) {
                setSelectionPath(path);
            }
            if (e.getComponent() instanceof OrTreeTable2.TreeTableCellRenderer) {
                OrTreeTable2.TreeTableCellRenderer rend = (OrTreeTable2.TreeTableCellRenderer) e.getComponent();
                Component comp = rend.getTable();
                nodeOperations_.show(comp, e.getX(), e.getY());
            } else {
                nodeOperations_.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public String getVarName() {
        return varName;
    }

    void setTransparent(boolean transparent) {
        setOpaque(transparent);
        repaint();
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    public void setUseCheck(boolean useCheck) {
        this.useCheck = useCheck;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }

    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners, java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
    }

    /**
     * @return the viewType
     */
    public int getViewType() {
        return viewType;
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }

    public boolean isShowSearchLine() {
		return showSearchLine;
	}

    public int getSortType(){
    	return sortType;
    }

	public boolean isFolderSelect() {
		return isFolderSelect;
	}
}
