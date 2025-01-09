package kz.tamur.comps;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.RadioBoxPropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.List;

public class OrRadioBox extends JPanel implements OrGuiComponent, MouseTarget {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new RadioBoxPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrFrame frame;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    
    private String toolTipContent = null;

    private boolean isCopy;

    private Border borderType;
    private String borderTitleUID;

    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;

    public ButtonGroup btnGroup = new ButtonGroup();
    private int columncount;

    private MouseDelegator delegator = new MouseDelegator(this);
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private Map borderProps = new TreeMap();
    private int currentItemCount = 0;
    private String descriptionUID;
    private Color backgroundColor;
    private Color fontColor;
    private Font font;

	private String varName;

    OrRadioBox(Element xml, int mode, OrFrame frame) {
        super();
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        init();
        enableEvents(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        //description = PropertyHelper.getDescription(this);
        
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
               addMouseListener(new MouseAdapter() {
                   public void mouseEntered(MouseEvent e) {
                       updateToolTip();
                   }
               });
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
            // setEditable(false);
            setEnabled(false);
        }
        updateProperties();
    }

    private void init() {
        setOpaque(false);
        if (mode != Mode.RUNTIME) {
       //     addRadioItems("");
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            JComponent c = (JComponent) comps[i];
            c.setEnabled(enabled);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public PropertyNode getProperties() {
        return PROPS;
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

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        Utils.processBorderProperties(this, frame);
        updateProperties();
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("columncount".equals(name)) {
            columncount = value.intValue();
            setBoxLayout(currentItemCount);
        } else if ("content".equals(name)) {
              //  addRadioItems(value.stringValue());
                PropertyValue pv = getPropertyValue(getProperties().getChild("view").getChild("sort"));
                if (!pv.isNull()) {
                    if (pv.booleanValue()) {
                        updateSort(true);
                    }
                }
        } else if ("sort".equals(name)) {
                updateSort(value.booleanValue());
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
        }
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
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
                description = (byte[])p.second;
            }
        }
        Utils.processBorderProperties(this, frame);
        //updateProperties();
    }

    private void updateProperties() {
        font = null;
        fontColor = null;
        backgroundColor = null;
        
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
            font = pv.fontValue();
        } else {
            font = (Font) pn.getChild("font").getChild("fontG").getDefaultValue();
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
            fontColor = pv.colorValue();
        } else {
            fontColor = (Color) pn.getChild("font").getChild("fontColor").getDefaultValue();
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            backgroundColor = pv.colorValue();
        } else {
            backgroundColor = (Color) pn.getChild("background").getChild("backgroundColor").getDefaultValue();
        }
        setBackground(backgroundColor);
        pv = getPropertyValue(getProperties().getChild("ref").getChild("content"));
        if (!pv.isNull() && mode != Mode.RUNTIME) {
            addRadioItems(pv.stringValue());
            setBoxLayout(currentItemCount);
        }
        pv = getPropertyValue(pn.getChild("columncount"));
        if (!pv.isNull()) {
            columncount = pv.intValue();
            setBoxLayout(currentItemCount);
        }
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof JRadioButton) {
                c.setBackground(backgroundColor);
                c.setFont(font);
                c.setForeground(fontColor);
            }
        }

        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }

        pv = getPropertyValue(getProperties().getChild("view").getChild("sort"));
        if (!pv.isNull()) {
            if (pv.booleanValue()) {
                updateSort(true);
            }
        }
        pn = getProperties().getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            if (!pv.isNull()) {
                borderType = pv.borderValue();
            } else {
                borderType = (Border)pn.getChild("borderType").getDefaultValue();
            }
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String)pv.resourceStringValue().first;
            }
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String)p.first;
            description = (byte[])p.second;
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        Utils.processBorderProperties(this, frame);
    }

    public int getMode() {
        return mode;
    }

    public void setBoxLayout(int choices) {
        if (columncount == 1) {
            setLayout(new GridLayout(choices, 1));
        } else if (columncount > 1) {
            float rowcount_ = (choices - 1) / columncount + 1;
            setLayout(new GridLayout((int) rowcount_, columncount));
        }
        revalidate();
    }


    private void addRadioItems(String path) {
        if (!path.isEmpty()) {
            removeAllButtons();
            try {
                Kernel krn = Kernel.instance();
                PathElement2[] ps = com.cifs.or2.client.Utils.parsePath2(path);
                if (ps.length > 1) {
                	PathElement2 cp = ps[ps.length - 2];
                	PathElement2 ap = ps[ps.length - 1];
                	
                	AttrRequestBuilder arb = new AttrRequestBuilder(cp.type, krn);
                	long langId = ap.attr.isMultilingual ? com.cifs.or2.client.Utils.getInterfaceLangId() : 0;
                	arb.add(ap.attr, langId);
                	List<Object[]> recs = krn.getClassObjects(cp.type, arb.build(), new long[0], new int[] {0}, 0);
                	
                    for (Object[] rec : recs) {
                        JRadioButton rdb = new JRadioButton((String)rec[2]);
                        rdb.setOpaque(false);
                        add(rdb);
                        btnGroup.add(rdb);
                        if (mode != Mode.RUNTIME) {
                            rdb.addMouseListener(delegator);
                            rdb.addMouseMotionListener(delegator);
                        }
                    }
                    JRadioButton clear = new JRadioButton("clear");
                    btnGroup.add(clear);
                    currentItemCount = recs.size();
                    setBoxLayout(currentItemCount);
                    validate();
                }
            } catch (Exception e) {
            //    e.printStackTrace();
                JRadioButton rdb = new JRadioButton("ERROR");
                rdb.setOpaque(false);
                add(rdb);
                btnGroup.add(rdb);
                if (mode != Mode.RUNTIME) {
                    rdb.addMouseListener(delegator);
                    rdb.addMouseMotionListener(delegator);
                }
            }
        } else {
            removeAllButtons();
            for (int i = 0; i < 3; i++) {
                JRadioButton rdb = new JRadioButton("Выбор " + i);
                rdb.setMargin(Constants.INSETS_0);
                btnGroup.add(rdb);
                rdb.setOpaque(true);
                add(rdb);
                if (mode != Mode.RUNTIME) {
                    rdb.addMouseListener(delegator);
                    rdb.addMouseMotionListener(delegator);
                }
            }
            currentItemCount = 3;
        }
    }

    public void removeAllButtons() {
        do {
            for (Enumeration e = btnGroup.getElements(); e.hasMoreElements();) {
                JRadioButton item_ = (JRadioButton) e.nextElement();
                remove(item_);
                btnGroup.remove(item_);
            }
        } while (btnGroup.getElements().hasMoreElements());
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


    //
    public int getTabIndex() {
        return tabIndex;
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
            Utils.processBorderProperties(this, frame);
        }
    }

    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    public void updateSort(boolean isSort) {
        if (isSort) {
            Component[] comps = getComponents();
            if (comps.length > 0) {
                List compsList = new ArrayList();
                for (int i = 0; i < comps.length; i++) {
                    Component comp = comps[i];
                    if (comp instanceof JRadioButton) {
                        compsList.add(comp);
                        remove(comp);
                    }
                }
                Collections.sort(compsList, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if (o1 != null && o2 != null) {
                            String t1 = ((JRadioButton)o1).getText();
                            String t2 = ((JRadioButton)o2).getText();
                            return t1.compareTo(t2);
                        }
                        return 0;
                    }
                });
                for (int i = 0; i < compsList.size(); i++) {
                    Component component = (Component) compsList.get(i);
                    add(component);
                }
            }
        } else {
            removeAllButtons();
            PropertyValue pv = getPropertyValue(
                    getProperties().getChild("ref").getChild("content"));
            if (!pv.isNull()) {
         //       addRadioItems(pv.stringValue());
            }
        }
    }

    public Border getBorderType() {
        return borderType;
    }

    public String getBorderTitleUID() {
        return borderTitleUID;
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public Font getDesFont() {
        return font;
    }

    public Color getDesBackground() {
        return backgroundColor;
    }

    public Color getDesForeground() {
        return fontColor;
    }

    
    public ComponentAdapter getAdapter() {
        return null;
    }

    public String getVarName() {
        return varName;
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

    @Override
    public String getUUID() {
        return UUID;
    }
    
    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
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

    public int getColumncount() {
        return columncount;
    }
  
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    /*public String getToolTipText() {
    	return toolTipExprText;
    }*/
    public String getToolTip() {
    	return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
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
