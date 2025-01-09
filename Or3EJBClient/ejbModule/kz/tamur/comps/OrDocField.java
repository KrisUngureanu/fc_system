package kz.tamur.comps;

import static kz.tamur.rt.Utils.processCreateImage;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.models.DocFieldPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.DocFieldAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OrDocField extends OrTransparentButton implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new DocFieldPropertyRoot();
    private static final PropertyNode FULL_PROPS = new DocFieldPropertyRoot();
    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrFrame frame;
    private OrGuiContainer parent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private boolean isHelpClick = false;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String title;
    private String titleUID;
    public String titleBeforeAttaching;
    public String titleAfterAttaching;
    private DocFieldAdapter adapter;
    private byte[] description;
    private String descriptionUID;
    private String base64Icon;
    private String varName;
    private int action = 0;
    private Color fontColor;
    private int posIcon;
    private boolean showOnTopPan;
    private int positionOnTopPan;
    private boolean multipleFile = false;
    
    protected boolean showUploaded = false;
    
	private String extensions;
    
    OrDocField(Element xml, int mode, OrFrame frame, boolean isEditor) {
        super("OrDocDield");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        
        if (mode == Mode.RUNTIME) {
            // Всплывающая подсказка
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
			setEnabled(false);
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
        String name = value.getProperty().getName();
        if ("title".equals(name)) {
            setText((String) value.resourceStringValue().second);
        } else if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color fontColor = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(fontColor);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setBackground(val);
        } else if ("enabled".equals(name)) {
            setEnabled(value.booleanValue());
        } else if ("image".equals(name)) {
            byte[] b = value.getImageValue();
            setIcon(value == null || b == null ? kz.tamur.rt.Utils.getImageIcon("DocField") : processCreateImage(b));
            if (b != null && b.length > 0) {
                base64Icon = new String(Base64.encode(b));
            }
        } else if ("alignmentText".equals(name)) {
            setHorizontalAlignment(value.intValue());
        } else if ("opaque".equals(name)) {
            setTransparent(!value.booleanValue());
            setOpaque(value.booleanValue());
            repaint();
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
        } else if ("action".equals(name)) {
            PropertyValue pv = getPropertyValue(getProperties().getChild("pov").getChild("action"));
            if (!pv.isNull()) {
                action = pv.intValue();
            }
        } else if ("anchorImage".equals(name)) {
            posIcon = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage")).intValue();
            setIcon(getIcon());
        } else if ("positionOnTopPan".equals(name)) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(name)) {
            showOnTopPan = value.booleanValue();
        } else if ("showUploaded".equals(name)) {
            showUploaded = value.booleanValue();
        } else if ("extensions".equals(name)) {
        	extensions = value.stringValue();
        } else if ("multipleFile".equals(name)) {
        	multipleFile = value.booleanValue();
        }
    }
    
    public String getBase64Icon() {
        return base64Icon;
    }
    
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            title = frame.getString(titleUID);
            setText(title);
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
            if (adapter != null) adapter.setLangId(langId);
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
            if (!pv.isNull()) {
                setText(pv.stringValue());
            }
            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[])p.second;
            }
        }
    }

    private void updateProperties() {
        updateDynProp();
        PropertyValue pv = null;
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String)p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        
    	pv = getPropertyValue(getProperties().getChild("titleBeforeAttaching"));
    	if (pv.isNull()) {
    		titleBeforeAttaching = "Прикрепить файл";
    	} else {
    		titleBeforeAttaching = (String) pv.resourceStringValue().second;;
    	}
    	
    	pv = getPropertyValue(getProperties().getChild("titleAfterAttaching"));
    	if (pv.isNull()) {
    		titleAfterAttaching = "Просмотреть файл";
    	} else {
    		titleAfterAttaching = (String) pv.resourceStringValue().second;;
    	}
        
        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            posIcon = pv.intValue();
        } else {
            posIcon = GridBagConstraints.WEST;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        }
        
        pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        
        pv = getPropertyValue(pn.getChild("image"));
        byte[] b = pv.getImageValue();
        setIcon(pv.isNull() ? kz.tamur.rt.Utils.getImageIcon("DocField") : processCreateImage(b));
        if (b != null && b.length > 0) {
            base64Icon = new String(Base64.encode(b));
        }
        
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForeground(fontColor);
        }
        
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        
        pv = getPropertyValue(pn.getChild("alignmentText"));
        setHorizontalAlignment(pv.isNull() ? ((Integer) pn.getChild("alignmentText").getDefaultValue()).intValue() : pv.intValue());
        
        
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setTransparent(!pv.booleanValue());
            setOpaque(pv.booleanValue());
            repaint();
            if (!pv.booleanValue()) {
                addMouseListener(new MouseAdapter() {
                    private void mouse(boolean isEnter) {
                        if (!isHelpClick) {
                            if (isEnter) {
                                setForeground(Color.blue);
                                setCursor(Constants.HAND_CURSOR);
                            } else {
                                setForeground(fontColor == null ? Color.black : fontColor);
                                setCursor(Constants.DEFAULT_CURSOR);
                            }
                        }
                    }
                    
                    public void mouseExited(MouseEvent e) {
                        mouse(false);
                    }

                    public void mouseEntered(MouseEvent e) {
                        mouse(true);
                    }
                });
            }
            
        } else {
            setTransparent(false);
            setOpaque(true);
            repaint();
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }
        
        
        pv = getPropertyValue(getProperties().getChild( "pov").getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
        
        pn = getProperties().getChild("pov");

        pv = getPropertyValue(pn.getChild("action"));
        if (!pv.isNull()) {
            action = pv.intValue();
        }

        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
            
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
        
        pv = getPropertyValue(PROPS.getChild("view").getChild("showUploaded"));
        showUploaded = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("showUploaded").getDefaultValue() : pv.booleanValue();
        
        pn = getProperties().getChild("constraints");
        pv = getPropertyValue(pn.getChild("extensions"));
        if (!pv.isNull()){
        	extensions = pv.stringValue();
        }
        
        pv = getPropertyValue(getProperties().getChild("multipleFile"));
        if (!pv.isNull())
        	multipleFile = pv.booleanValue();
    }

    public int getMode() {
        return mode;
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
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public String getBorderTitleUID() {
        return null;
    }

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

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setAdapter(DocFieldAdapter adapter) {
        this.adapter = adapter;
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
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    public int getDocAction() {
        return action;
    }

    public String getToolTip() {
        return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
    }

    @Override
    public void setIcon(Icon icon) {
        switch (posIcon) {
        case GridBagConstraints.EAST: // иконка справа
            setVerticalTextPosition(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.LEFT);
            break;
        case GridBagConstraints.WEST: // иконка Слева
            setVerticalTextPosition(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            break;
        case GridBagConstraints.NORTH: // иконка Сверху
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            break;
        case GridBagConstraints.SOUTH: // иконка Снизу
            setVerticalTextPosition(SwingConstants.TOP);
            setHorizontalTextPosition(SwingConstants.CENTER);
            break;
        }
        super.setIcon(icon);
    }
    
    /**
     * Возвращает позицию иконки
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
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

	public boolean isShowUploaded() {
		return showUploaded;
	}
	
	public String getExtensions(){
		return extensions;
	}

	public boolean isMultipleFile() {
		return multipleFile;
	}
}