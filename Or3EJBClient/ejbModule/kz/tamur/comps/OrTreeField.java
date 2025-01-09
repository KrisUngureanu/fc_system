package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeFieldPropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class OrTreeField extends JButton implements OrGuiComponent {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new TreeFieldPropertyRoot();
    private static final PropertyNode FULL_PROPS = new TreeFieldPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    private JTree tree = new JTree() { { setRootVisible(false); } };
    private JScrollPane scroll = new JScrollPane(tree);

    private OrTree orTree;
    private boolean titleMode;
    private boolean isHelpClick = false;
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private Color fontColor;
    private byte[] description;
    private String descriptionUID;
    private String varName;
    private boolean showOnTopPan;
    private int positionOnTopPan;
    private boolean isClearBtnExists;
    private JLabel deleteLabel = new JLabel();
	private boolean isPrefWidth;
    private boolean isFolderAsLeaf = false;

    OrTreeField(Element xml, int mode, OrFrame frame, boolean isEditor) {
        super("OrTreeField");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        PropertyValue pv = getPropertyValue(getProperties().getChild("view").getChild("folderAsLeaf"));
        if (pv.isNull()) {
        	isFolderAsLeaf = ((Boolean) getProperties().getChild("view").getChild("folderAsLeaf").getDefaultValue()).booleanValue();
        } else {
        	isFolderAsLeaf = pv.booleanValue();
        }
        tree.setCellRenderer(new OrTreeCellRenderer(false, true, isFolderAsLeaf));
        setFocusable(true);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        deleteLabel.setIcon(kz.tamur.rt.Utils.getImageIconExt("DeleteValue", ".png"));
        deleteLabel.setToolTipText("Удалить значение");
        setLayout(new BorderLayout());
        add(deleteLabel, BorderLayout.EAST);
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
        } else if (this.mode == Mode.DESIGN) {
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
                    if (toolTip != null && toolTip.length > 0) {
                        setToolTipText(new String(toolTip));
                        SAXBuilder builder = new SAXBuilder();
                        InputStream is = new ByteArrayInputStream(toolTip);
                        try {
                            Element var_doc = builder.build(is).getRootElement();
                            if (var_doc.getName().equals("html")) {
                                XMLOutputter outp = new XMLOutputter();

                                outp.setFormat(Format.getCompactFormat());
                                StringWriter sw = new StringWriter();
                                outp.output(var_doc.getChild("body").getContent(), sw);
                                StringBuffer sb = sw.getBuffer();
                                toolTipContent = sb.toString();
                                toolTipExprText = var_doc.getChild("body").getValue();
                            }
                        } catch (JDOMException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            setEnabled(false);
        }

        updateProperties();
        if (mode != Mode.DESIGN) {
            addMouseListener(new HyperMouseAdapter());
        } else {
            setEnabled(false);
        }
        orTree = new OrTree(xml, mode, frame, true);
        orTree.setRootVisible(false);
        orTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }

    private void updateProperties() {
        updateDynProp();
        Utils.processBorderProperties(this, frame);
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        PropertyValue pv = null;
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            setText(pv.stringValue());
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
        
        PropertyNode pn = getProperties().getChild("view");

        pv = getPropertyValue(pn.getChild("clearBtnShow"));
        isClearBtnExists = pv.booleanValue();
        deleteLabel.setVisible(isClearBtnExists);

        pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            setIcon(kz.tamur.rt.Utils.processCreateImage(pv.getImageValue()));
        } else {
            ImageIcon icon = kz.tamur.rt.Utils.getImageIcon("TreeRt");
            setIcon(icon);
            setHorizontalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.LEFT);
        }
        PropertyNode pnode = pn.getChild("alignmentText");
        pv = getPropertyValue(pnode);
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        } else {
            setHorizontalAlignment(((Integer) pnode.getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFonts(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForegroundColors(fontColor);
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackgroundColors(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setOpaque(pv.booleanValue());
        } else {
            setOpaque(true);
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }
        pv = getPropertyValue(pn.getChild("fullPath"));
        if (!pv.isNull()) {
            titleMode = pv.booleanValue();
            if (pv.booleanValue()) {
                add(scroll, BorderLayout.CENTER);
                repaint();
            } else {
                remove(scroll);
                repaint();
            }
        }
        pv = getPropertyValue(PROPS.getChild("pos").getChild("pref").getChild("width"));
        isPrefWidth = pv.isNull() ? false : true;
    }

	private void setBackgroundColors(Color bg) {
        setBackground(bg);
        scroll.setBackground(bg);
        tree.setBackground(bg);
        OrTreeCellRenderer rend = (OrTreeCellRenderer) tree.getCellRenderer();
        rend.setBackground(bg);
        tree.setCellRenderer(rend);
    }

    private void setForegroundColors(Color bg) {
        setForeground(bg);
        scroll.setForeground(bg);
        tree.setForeground(bg);
        OrTreeCellRenderer rend = (OrTreeCellRenderer) tree.getCellRenderer();
        rend.setForeground(bg);
        tree.setCellRenderer(rend);
    }

    private void setFonts(Font f) {
        setFont(f);
        tree.setFont(f);
        OrTreeCellRenderer rend = (OrTreeCellRenderer) tree.getCellRenderer();
        rend.setFont(f);
        tree.setCellRenderer(rend);
    }

    private void updateToolTip() {
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

    public OrFrame getFrame() {
        return frame;
    }

    public OrTree getOrTree() {
        return orTree;
    }

    public int getTabIndex() {
        return -1;
    }

    public boolean isTitleMode() {
        return titleMode;
    }

    public JTree getTree() {
        return tree;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public boolean isClearBtnExists() {
        return isClearBtnExists;
    }

	public boolean isPrefWidth() {
		return isPrefWidth;
	}
    
    public void addDeleteMouseListener(MouseListener listener) {
        deleteLabel.addMouseListener(listener);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    @Override
    public Element getXml() {
        return xml;
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
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

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("fontColor".equals(name)) {
            Color fontColor = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(fontColor);
        } else if ("toolTip".equals(name)) {
            if (value.isNull()) {
                toolTipUid = null;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null && toolTip.length > 0) {
                    setToolTipText(new String(toolTip));
                    SAXBuilder builder = new SAXBuilder();
                    InputStream is = new ByteArrayInputStream(toolTip);
                    try {
                        Element var_doc = builder.build(is).getRootElement();
                        if (var_doc.getName().equals("html")) {
                            XMLOutputter outp = new XMLOutputter();

                            outp.setFormat(Format.getPrettyFormat());
                            StringWriter sw = new StringWriter();
                            outp.output(var_doc.getChild("body").getContent(), sw);
                            StringBuffer sb = sw.getBuffer();
                            toolTipContent = sb.toString();
                            toolTipExprText = var_doc.getChild("body").getValue();
                        }
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ("positionOnTopPan".equals(name)) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(name)) {
            showOnTopPan = value.booleanValue();
        } else if ("clearBtnShow".equals(name)) {
            isClearBtnExists = value.booleanValue();
            deleteLabel.setVisible(isClearBtnExists);
        }
        updateProperties();
    }

    @Override
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    @Override
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
        TreeNode root = (TreeNode) orTree.getModel().getRoot();
        if (root.getChildCount() > 0) {
            TreeAdapter.Node n = (TreeAdapter.Node) root.getChildAt(0);
            n.reset();
        }
        Utils.processBorderProperties(this, frame);
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    @Override
    public void setXml(Element xml) {
        this.xml = xml;
    }

    @Override
    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    @Override
    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    @Override
    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    @Override
    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    @Override
    public String getVarName() {
        return varName;
    }

    @Override
    public String getUUID() {
        return UUID;
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

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    @Override
    public String getToolTip() {
        return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
    }

    @Override
    public void updateDynProp() {
        if (mode != Mode.RUNTIME) {
            showOnTopPan = getPropertyValue(PROPS.getChild("web").getChild("showOnTopPan")).booleanValue();
            PropertyNode dependentNode = FULL_PROPS.getChild("web").getChild("positionOnTopPan");
            if (showOnTopPan) {
                PropertyNode node = PROPS.getChild("web").getChild("positionOnTopPan");
                if (node == null) {
                    PROPS.getChild("web").addChild(dependentNode);
                }
                positionOnTopPan = getPropertyValue(dependentNode).intValue();
            } else {
                PROPS.getChild("web").removeChild("positionOnTopPan");
            }
        }
    }

    /**
     * @return the showOnTopPan
     */
    @Override
    public boolean isShowOnTopPan() {
        return showOnTopPan;
    }

    /**
     * @return the positionOnTopPan
     */
    @Override
    public int getPositionOnTopPan() {
        return positionOnTopPan;
    }

    @Override
    public void setAttention(boolean attention) {
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        deleteLabel.setEnabled(getText().length() > 0 && isEnabled);
    }

    private class HyperMouseAdapter extends MouseAdapter {

        private void mouse(boolean isEnter) {
            if (!isHelpClick) {
                PropertyNode pn = getProperties().getChild("view").getChild("font");
                PropertyValue pv = null;
                if (isEnter) {
                    pv = getPropertyValue(pn.getChild("lightFontColor"));
                    if (!pv.isNull()) {
                        setForeground(pv.colorValue());
                    } else {
                        setForeground(Color.blue);
                    }
                    setCursor(Constants.HAND_CURSOR);
                } else {
                    if (fontColor != null) {
                        setForeground(fontColor);
                    } else {
                        setForeground(Color.black);
                    }
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            mouse(false);
            isHelpClick = false;
            super.mouseExited(e);
        }

        public void mouseEntered(MouseEvent e) {
            mouse(true);
            super.mouseEntered(e);
        }
    }
}
