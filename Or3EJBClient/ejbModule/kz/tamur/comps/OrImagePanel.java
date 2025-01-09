package kz.tamur.comps;

import kz.tamur.comps.models.ImagePanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.ImagePanelAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrImagePanel extends JPanel implements OrGuiComponent, ActionListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new ImagePanelPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    protected OrFrame frame;
    private int tabIndex;

    private boolean  showPopup = true;

    private OrGuiContainer guiParent;
    
    /** The title. */
    private String title;

    /** The title uid. */
    private String titleUID;
    
    private String titleAlign = "center";

    /** The border type. */
    private Border borderType;

    /** The border title uid. */
    private String borderTitleUID;

    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private ImagePanelAdapter adapter;
    private String descriptionUID;
	private String varName;
	private String imageUID = null;
	private int orientation = 1; // горизонтальная по умолчанию

    OrImagePanel(Element xml, int mode, OrFrame frame) {
        super();
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
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
        
        PropertyNode pn = getProperties().getChild("view").getChild("imageUID");
        if (pn != null) {
        	PropertyValue pv = getPropertyValue(pn);
            this.imageUID  = pv.isNull() ? null : pv.stringValue();
        }
        
        updateProperties();
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
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml);
        }
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
        updateProperties();
        final String name = value.getProperty().getName();
        if ("title".equals(name)) {
            setTitle(value.toString());
        } else if ("imageUID".equals(name)) {
            this.imageUID  = value.isNull() ? null : value.stringValue();
        } else if ("orientation".equals(name)) {
            setOrientation(value.enumValue());
        }
    }
    
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public void setLangId(long langId) {
        kz.tamur.comps.Utils.processBorderProperties(this, frame);
        title = frame.getString(titleUID);
        if (title == null || title.length() == 0) {
			PropertyValue titleExprVal = getPropertyValue(PROPS.getChild("title1").getChild("expr"));
			if (!titleExprVal.isNull()) {
                String titleExpr = (String) titleExprVal.objectValue();
                title = kz.tamur.comps.Utils.getExpReturn(titleExpr, frame, getAdapter());
			}
		}
        if (title == null) title = "";

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
        if (adapter != null) adapter.setLangId(langId);
        updateProperties();
    }

    private void updateProperties() {
        PropertyValue pv = null;
        if (mode == Mode.DESIGN) {
        } else {
            pv = getPropertyValue(
                    PROPS.getChild("pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
        }
        PropertyNode pn = getProperties().getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            borderType = pv.isNull() ? (Border) pn.getChild("borderType").getDefaultValue() : pv.borderValue();
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String) pv.resourceStringValue().first;
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

        pn = PROPS.getChild("titleAlign");
        if (pn != null) {
            pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                switch (pv.enumValue()) {
                case GridBagConstraints.CENTER:
                    titleAlign  = "center";
                    break;
                case GridBagConstraints.WEST:
                    titleAlign  = "left";
                    break;
                case GridBagConstraints.EAST:
                    titleAlign  = "right";
                    break;
                }
            }
        }

        pn = PROPS.getChild("view").getChild("orientation");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setOrientation(pv.enumValue());
        } else {
        	setOrientation((Integer)pn.getDefaultValue());
        }
    }

    public int getMode() {
        return mode;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fChooser = new JFileChooser();
        if (fChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sf = fChooser.getSelectedFile();
            if (sf != null) {
                byte[] val = null;
                try {
                	val = Funcs.read(sf);
                    //setIcon(Utils.processCreateImage(val));
                    PropertyHelper.setPropertyValue(new
                            PropertyValue(val, getProperties().getChild(
                            "view").getChild("image")), xml, frame);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleAlign() {
        return titleAlign;
    }

    /**
     * Получить border type.
     * 
     * @return the border type
     */
    public Border getBorderType() {
        return borderType;
    }

    /**
     * Получить border title uid.
     * 
     * @return the border title uid
     */
    public String getBorderTitleUID() {
        return borderTitleUID;
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
        if (mode == Mode.RUNTIME) {
            return prefSize;
        } else {
            return PropertyHelper.getPreferredSize(this);
        }
    }

    public Dimension getMaxSize() {
        if (mode == Mode.RUNTIME) {
            return maxSize;
        } else {
            return PropertyHelper.getMaximumSize(this);
        }
    }

    public Dimension getMinSize() {
        if (mode == Mode.RUNTIME) {
            return minSize;
        } else {
            return PropertyHelper.getMinimumSize(this);
        }
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
        }
    }

    public void setEnabled(boolean enabled) {
        showPopup = enabled;
    }

    public boolean isShowPopup() {
        return showPopup;
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public void setAdapter(ImagePanelAdapter imageAdapter) {
        adapter = imageAdapter;
    }

    @Override
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
    
    public String getImageUID() {
    	return this.imageUID;
    }
    
    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
