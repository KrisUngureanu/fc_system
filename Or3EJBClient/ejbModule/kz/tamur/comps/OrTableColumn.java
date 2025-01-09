package kz.tamur.comps;

import kz.tamur.comps.models.ColumnPropertyRoot;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.AttributeTypeChecker;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static kz.tamur.util.AttributeTypeChecker.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 19.03.2004
 * Time: 11:04:45
 */
public abstract class OrTableColumn implements OrColumnComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    protected OrTable table;
    protected PropertyChangeSupport ps = new PropertyChangeSupport(this);
    protected OrGuiComponent editor;
    protected int preferredWidth;
    protected int maxWidth;
    protected int minWidth;
    private OrGuiContainer parent;
    private boolean isCopy;
    private int modelIndex;
    private boolean isSelected;
    private int uniqueIndex;
    protected OrFrame frame;
    private String title;
    private String titleUid;
    public static PropertyNode PROPS = new ColumnPropertyRoot();
    private String toolTipUid;
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private String toolTipExpr = null;
    private int mode;
    private Element xml;
    private Font headerFont;
    private Color headerBackground;
    private Color headerForeground;
    private String columnBackColorExpr;
    private String columnFontColorExpr;
    private byte[] description;

    protected boolean isHelpClick = false;
    private String descriptionUID;
    private boolean canSort = true;
    private String varName;
    private int rotation;
    private String titleExp = null;
    private int titleSource = Constants.SOURSE_SIMPLE;
    private boolean sort = false;
    private int direction;
    private int sortingIndex;

    protected OrTableColumn(Element xml, int mode, OrFrame frame) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
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
        }
        if (mode == Mode.DESIGN) {
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
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
    }

    public void setLangId(long langId) {
        switch (titleSource) {
        default:
        case Constants.SOURSE_SIMPLE:
            title = frame.getString(titleUid);
            break;
        case Constants.SOURSE_EXPRESSION:
            if (mode == Mode.RUNTIME) {
                String tlt = null;
                try {
                    tlt = Utils.getExpReturn(titleExp, frame, getAdapter());
                    if (tlt != null && !tlt.isEmpty()) {
                        title = tlt;
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка в формуле\r\n" + tlt + "\r\n" + e);
                }
            } else {
                setTitleHeader();
            }
            break;
        case Constants.SOURSE_HTML:
            byte[] tltB = frame.getBytes(titleUid);
            if (tltB != null) {
                title = new String(tltB);
            }
            break;
        }

        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null) {
                description = frame.getBytes(descriptionUID);
            }
        } else if (!(this instanceof OrImageColumn)) {
            PropertyValue pv = getPropertyValue(getProperties().getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        if (this instanceof OrDateColumn) {
            ((OrDateColumn) this).editor.setLangId(langId);
        }
    }

    private void init() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("width").getChild("pref"));
        preferredWidth = pv.isNull() ? Constants.DEFAULT_PREF_WIDTH : pv.intValue();

        if (mode == Mode.DESIGN) {
            setPropertyValue(new PropertyValue(preferredWidth, getProperties().getChild("width").getChild("pref")));
        }

        pv = getPropertyValue(getProperties().getChild("width").getChild("max"));
        if (!pv.isNull() && pv.intValue() != 0) {
            maxWidth = pv.intValue();
        } else if (preferredWidth > 0) {
            maxWidth = Constants.DEFAULT_MAX_WIDTH;
        } else {
            maxWidth = 0;
        }
        pv = getPropertyValue(getProperties().getChild("width").getChild("min"));
        if (!pv.isNull() && pv.intValue() != 0) {
            minWidth = pv.intValue();
        } else if (preferredWidth > 0) {
            minWidth = Constants.DEFAULT_MIN_WIDTH;
        } else {
            minWidth = 0;
        }
        PropertyNode pn = getProperties().getChild("header");
        // поворот заголовка столбца
        pv = getPropertyValue(pn.getChild("rotation"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(new EnumValue(Constants.DONT_ROTATE, "Без поворота"), pn.getChild("rotation")));
        } else {
            rotation = pv.intValue();
        }

        pv = getPropertyValue(pn.getChild("editor"));
        if (!pv.isNull()) {
            if (pv.objectValue() instanceof Expression) {
                setTitleHeader();
            } else {
                titleSource = Constants.SOURSE_HTML;
                titleUid = (String) pv.resourceStringValue().first;
                byte[] tlt = frame.getBytes(titleUid);
                if (tlt != null) {
                    title = new String(tlt);
                }
            }
        } else {
            setTitleHeader();
        }

        pv = getPropertyValue(pn.getChild("canSort"));
        canSort = pv.isNull() ? true : pv.booleanValue();

        pv = getPropertyValue(pn.getChild("sorted"));
        sort = pv.isNull() ? false : pv.booleanValue();
        
        pv = getPropertyValue(pn.getChild("sortingDirection"));
        direction = pv.isNull() ? Constants.SORT_ASCENDING : pv.intValue();

        pv = getPropertyValue(pn.getChild("sortingIndex"));
        sortingIndex = pv.isNull() ? 0 : pv.intValue();

        pv = getPropertyValue(pn.getChild("font"));
        headerFont = pv.isNull() ? UIManager.getFont("TableHeader.font") : pv.fontValue();

        pv = getPropertyValue(pn.getChild("backgroundColorCol"));
        headerBackground = pv.isNull() ? UIManager.getColor("TableHeader.background") : pv.colorValue();

        pv = getPropertyValue(pn.getChild("fontColorCol"));
        headerForeground = pv.isNull() ? UIManager.getColor("TableHeader.foreground") : pv.colorValue();
        if (!(this instanceof OrImageColumn)) {
            pv = getPropertyValue(getProperties().getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = (byte[]) p.second;
            }}
        pv = getPropertyValue(getProperties().getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        if (mode == Mode.RUNTIME) {
            xml = null;
        }
    }

    private void setTitleHeader() {
        titleSource = Constants.SOURSE_SIMPLE;
        PropertyValue pv = getPropertyValue(getProperties().getChild("header").getChild("text"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUid = (String) p.first;
            title = (String) p.second;
        } else {
            if (OrTableColumn.this instanceof OrTextColumn) {
                title = "TextColumn";
            } else if (OrTableColumn.this instanceof OrIntColumn) {
                title = "IntColumn";
            } else if (OrTableColumn.this instanceof OrFloatColumn) {
                title = "FloatColumn";
            } else if (OrTableColumn.this instanceof OrDateColumn) {
                title = "DateColumn";
            } else if (OrTableColumn.this instanceof OrMemoColumn) {
                title = "MemoColumn";
            } else if (OrTableColumn.this instanceof OrCheckColumn) {
                title = "CheckColumn";
            } else if (OrTableColumn.this instanceof OrHyperColumn) {
                title = "HyperColumn";
            } else if (OrTableColumn.this instanceof OrPopupColumn) {
                title = "PopupColumn";
            } else if (OrTableColumn.this instanceof OrTreeColumn) {
                title = "TreeColumn";
            } else if (OrTableColumn.this instanceof OrComboColumn) {
                title = "ComboColumn";
            } else if (OrTableColumn.this instanceof OrDocFieldColumn) {
                title = "OrDocFieldColumn";
            } else if (OrTableColumn.this instanceof OrImageColumn) {
                title = "OrImageColumn";
            } else {
                title = "Column";
            }
        }
    }

    public OrTable getOrTable() {
        return table;
    }

    public void setOrTable(OrTable table) {
        this.table = table;
        if (table.getClientProperty("doNotCancelPopup") == null) {
            table.putClientProperty("doNotCancelPopup", new StringBuffer("HidePopupKey"));
        }
        init();
    }

    public OrGuiComponent getEditor() {
        return editor;
    }

    public void setPrefWidth(int width) {
        preferredWidth = width;
        TableColumn column = null;
        JTable jtable = table.getJTable();
        TableColumnModel model = jtable.getColumnModel();
        if (model.getColumnCount() > 0) {
            column = model.getColumn(modelIndex);
            column.setPreferredWidth(preferredWidth);
        }
    }

    public void setMaxWidth(int width) {
        maxWidth = width;
        JTable jtable = table.getJTable();
        TableColumn col = jtable.getColumnModel().getColumn(modelIndex);
        col.setMaxWidth(maxWidth);
    }

    public void setMinWidth(int width) {
        minWidth = width;
        JTable jtable = table.getJTable();
        TableColumn col = jtable.getColumnModel().getColumn(modelIndex);
        col.setMinWidth(minWidth);
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getPreferredWidthOfProp() {
        final PropertyValue pv = getPropertyValue(getProperties().getChild("width").getChild("pref"));
        return pv.isNull() ? Constants.DEFAULT_PREF_WIDTH : pv.intValue();
    }

    public int getMaxWidthOfProp() {
        final PropertyValue pv = getPropertyValue(getProperties().getChild("width").getChild("max"));
        return pv.isNull() ? Constants.DEFAULT_MAX_WIDTH : pv.intValue();
    }

    public int getMinWidthOfProp() {
        final PropertyValue pv = getPropertyValue(getProperties().getChild("width").getChild("min"));
        return pv.isNull() ? Constants.DEFAULT_MIN_WIDTH : pv.intValue();
    }

    public String getTitle() {
        return title;
    }

    public int getSummaryType() {
        int type = Constants.SUMMARY_NO;
        if (OrTableColumn.this instanceof OrIntColumn || OrTableColumn.this instanceof OrFloatColumn) {
            PropertyValue pv = getPropertyValue(getProperties().getChild("view").getChild("summary"));
            if (!pv.isNull()) {
                type = pv.intValue();
            }
        }
        return type;
    }

    private Font getHeaderFont() {
        return headerFont;
    }

    private Color getHeaderBackground() {
        return headerBackground;
    }

    private Color getHeaderForeground() {
        return headerForeground;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        ps.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        ps.removePropertyChangeListener(l);
    }

    public TableCellRenderer getDefaultRenderer() {
        return createDefaultRenderer();
    }

    public GridBagConstraints getConstraints() {
        return null;
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN) {
            ps.firePropertyChange("selected", this.isSelected, isSelected);
        }
        this.isSelected = isSelected;
        DefaultTableColumnModel cmodel = (DefaultTableColumnModel) table.getJTable().getColumnModel();
        cmodel.propertyChange(new PropertyChangeEvent(this, "width", null, null));

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
    }

    protected TableCellRenderer createDefaultRenderer() {
        // Рендер с изменяемым положением текста
    	OrHeaderTableCellRenderer label = new OrHeaderTableCellRenderer(rotation, toolTipContent) {
            public void paint(Graphics g) {
                super.paint(g);
                if (getMode() == Mode.DESIGN && isSelected) {
                    kz.tamur.rt.Utils.drawRects(this, g);
                }
            }

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(getHeaderForeground());
                        // Цвет шапки таблиц
                        setBackground(getHeaderBackground());
                        setFont(getHeaderFont());
                    }
                    if (getMode() == Mode.DESIGN && OrTableColumn.this.isSelected) {
                        // Цвет шапки таблиц
                        setBackground(Color.white);
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                String start = "<html><p align=\"center\">&nbsp;";
                String end = "&nbsp;</p></html>";
                String breakLine = "<br>";
                value = getTitle();
                String val = (value == null) ? "" : value.toString();
                String nVal = val.replaceAll("@", breakLine + "&nbsp;");
                setText(start + nVal + end);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };
        return label;
    }
    
    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return null;
    }

    public Dimension getMaxSize() {
        return null;
    }

    public Dimension getMinSize() {
        return null;
    }

    public String getBorderTitleUID() {
        return null;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    public void setModelIndex(int modelIndex) {
        this.modelIndex = modelIndex;
    }

    public void setUniqueIndex(int unique) {
        uniqueIndex = unique;
    }

    public int getUniqueIndex() {
        return uniqueIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyNode prop = value.getProperty();
        final String name = prop.getName();
        if ("data".equals(name) || "treeDataRef".equals(name)) {
            if (this instanceof OrDateColumn) {
                AttributeTypeChecker.instance().check(value, new long[] { DATE_TYPE });
            } else if (this instanceof OrMemoColumn) {
                AttributeTypeChecker.instance().check(value, new long[] { BLOB_TYPE, MEMO_TYPE, STRING_TYPE });
            } else if (this instanceof OrTextColumn) {
                AttributeTypeChecker.instance().check(value, new long[] { STRING_TYPE });
            } else if (this instanceof OrIntColumn) {
                AttributeTypeChecker.instance().check(value, new long[] { INTEGER_TYPE });
            } else if (this instanceof OrFloatColumn) {
                AttributeTypeChecker.instance().check(value, new long[] { FLOAT_TYPE });
            }
        }
        PropertyHelper.setPropertyValue(value, xml, frame);

        // поворот заголовка столбца
        if ("rotation".equals(name)) {
            rotation = value.enumValue();
            TableColumn column = null;
            JTable jtable = table.getJTable();
            TableColumnModel model = jtable.getColumnModel();
            if (model.getColumnCount() > 0) {
                column = model.getColumn(modelIndex);
                column.setHeaderRenderer(createDefaultRenderer());
            }
            ps.firePropertyChange("text", null, null);
            table.repaint();
        }

        if ("editor".equals(name)) {
            if (value.isNull()) {
                setTitleHeader();
                ps.firePropertyChange("text", null, null);
            } else {
                if (value.objectValue() instanceof Expression) {
                    titleSource = Constants.SOURSE_EXPRESSION;
                    String tlt = null;
                    try {
                        tlt = ((Expression) value.objectValue()).text;
                        tlt = Utils.getExpReturn(tlt, frame, getAdapter());
                        if (tlt != null && !tlt.isEmpty()) {
                            if (mode == Mode.RUNTIME) {
                                title = tlt;
                                ps.firePropertyChange(name, null, null);
                            } else {
                                setTitleHeader();
                                ps.firePropertyChange("text", null, null);
                            }

                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + tlt + "\r\n" + e);
                    }
                } else {
                    titleSource = Constants.SOURSE_HTML;
                    titleUid = (String) value.resourceStringValue().first;
                    byte[] tlt = frame.getBytes(titleUid);
                    if (tlt != null) {
                        title = new String(tlt);
                        ps.firePropertyChange(name, null, null);
                    }
                }
            }
        }

        if ("text".equals(name)) {
            setTitleHeader();
            ps.firePropertyChange(name, null, null);
        }
        if ("font".equals(name)) {
            headerFont = value.fontValue();
            ps.firePropertyChange(name, null, null);
        }
        if ("fontColorCol".equals(name)) {
            headerForeground = value.colorValue();
            ps.firePropertyChange(name, null, null);
        }
        if ("backgroundColorCol".equals(name)) {
            headerBackground = value.colorValue();
            ps.firePropertyChange(name, null, null);
        }
        if ("pref".equals(name)) {
            table.isSelfChange = true;
            setPrefWidth(value.isNull() ? ((Integer) prop.getDefaultValue()).intValue() : value.intValue());
            table.isSelfChange = false;

        }
        if ("max".equals(name)) {
            setMaxWidth(value.isNull() ? ((Integer) prop.getDefaultValue()).intValue() : value.intValue());
            ps.firePropertyChange(name, null, null);
        }
        if ("min".equals(name)) {
            setMinWidth(value.isNull() ? ((Integer) prop.getDefaultValue()).intValue() : value.intValue());
            ps.firePropertyChange(name, null, null);
        }

        if ("font".equals(name) && "header".equals(prop.getParent().getName())) {
                headerFont = value.fontValue();
        }

        if ("sorted".equals(name)) {
            sort = value.isNull() ? false : value.booleanValue();
        }
        if ("sortingDirection".equals(name)) {
            direction = value.isNull() ? Constants.SORT_ASCENDING : value.intValue();
        }

        if ("sortingIndex".equals(name)) {
            sortingIndex = value.isNull() ? 0 : value.intValue();
        }

        if ("toolTip".equals(name)) {
            if (value.isNull()) {
                toolTipUid = null;
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null && toolTip.length > 0) {
                    // setToolTipText(new String(toolTip));
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
        }
    }

    public int getMode() {
        return mode;
    }

    public Element getXml() {
        return xml;
    }

    public void setEnabled(boolean isEnabled) {
    }

    public OrGuiComponent getEditor(int row) {
        return null;
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public String getColumnBackColorExpr() {
        return columnBackColorExpr;
    }

    public String getColumnFontColorExpr() {
        return columnFontColorExpr;
    }

    public ComponentAdapter getAdapter() {
        return null;
    }

    public boolean isCanSort() {
        return canSort;
    }

    public String getVarName() {
        return varName;
    }
    
    /**
     * @param headerBackground the headerBackground to set
     */
    public void setHeaderBackground(Color headerBackground) {
        this.headerBackground = headerBackground;
    }
    
    public void setHeaderTitle(String title) {
    	this.title = title;
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
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
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
    public void setToolTipText(String toolTip) {
    	this.toolTipContent = toolTip;
    }

    /**
     * @return the rotation
     */
    public int getRotation() {
        return rotation;
    }

    public boolean isSort() {
        return sort;
    }

    public int getDirection() {
        return direction;
    }

    public int getSortingIndex() {
        return sortingIndex;
    }

    @Override
    public String getToolTip() {
        return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
    }

    public String getToolTipText() {
        return toolTipExprText;
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
}
