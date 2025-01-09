package kz.tamur.comps;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.border.Border;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.models.BarcodePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.BarcodeAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.kernel.KrnException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Класс реализующий создание QR кода
 */
public class OrBarcode extends JLabel implements OrGuiComponent, ActionListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    /** Шаблон свойств компонента */
    public static PropertyNode PROPS = new BarcodePropertyRoot();

    /** Режим выполнения компонента */
    protected int mode;

    /** The xml. */
    protected Element xml;

    /** Компонент выбран? */
    protected boolean isSelected;

    /** The frame. */
    protected OrFrame frame;

    /** The tab index. */
    private int tabIndex;

    /** The show popup. */
    private boolean showPopup = true;

    /** The gui parent. */
    private OrGuiContainer guiParent;

    /** идентификатор строки с подсказкой. */
    private String toolTipUid;

    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;

    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    
    private String toolTipContent = null;

    /** The is copy. */
    private boolean isCopy;

    /** The standart border. */
    private Border standartBorder;

    /** The copy border. */
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    /** The constraints. */
    private GridBagConstraints constraints;

    /** The pref size. */
    private Dimension prefSize;

    /** The max size. */
    private Dimension maxSize;

    /** The min size. */
    private Dimension minSize;

    /** The description. */
    private byte[] description;

    /** The adapter. */
    private BarcodeAdapter adapter;

    /** The description uid. */
    private String descriptionUID;

    /** The var name. */
    private String varName;

    /** автоподгонка размера рисунка под размер компонента. */
    private boolean autoResize = true;
    /** Максимальный размер загружаемого изображения(в байтах), если 0, то нет ограничения. */
    private long maxDataSize = 0;
    private String webNameIcon = null;
    private String base64Icon;
    /**
     * Создание нового or image.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param frame
     *            the frame
     */
    OrBarcode(Element xml, int mode, OrFrame frame) {
        super(" ");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        setIconTextGap(0);
        // авторазмер рисунка
        PropertyValue pv = getPropertyValue(PROPS.getChild("view").getChild("autoresize"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(autoResize, PROPS.getChild("view").getChild("autoresize")));
        } else {
            autoResize = pv.booleanValue();
        }
        PropertyNode pn = PROPS.getChild("ref").getChild("maxSize");
         pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        } else {
            maxDataSize = pv.intValue()*1024;
        }
        
        try {
			generateQR(50, 50, "http://tamur.kz/");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        updateProperties();
        if (mode == Mode.RUNTIME) {
        	try {
				adapter = new BarcodeAdapter(frame, this, false);
			} catch (KrnException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
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
        if(mode == Mode.DESIGN) {
        	pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
            	if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
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
							if(var_doc.getName().equals("html")) {
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

        getImage();
        kz.tamur.comps.Utils.processBorderProperties(this, frame);
    }
    /**
     * Генерирует QR код.
     * 
     * @param width - ширина изображения
     * @param height - длина изображения 
     * @param content - строка для шифрования 
     * */
    private void generateQR(int width, int height, String content) throws Exception { 
    	
    	//Проверяем размеры
    	if(width < 0 && height < 0) return;
    	
    	//Проверяем строку для шифрования
    	else if(content == "" || content == null) return;
    	
    	//Файл для сохранения QR кода
    	ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
    	String imageFormat = "png";
    	String charset = "UTF-8";
    	
    	//Создаем мэп с подсказкой по кодировке
    	Hashtable hints = new Hashtable();
    	hints.put(EncodeHintType.CHARACTER_SET, charset);
	    
    	BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 150, 150, hints);
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, tmpStream);
		
	    byte[] b = tmpStream.toByteArray();
	    tmpStream.close();
		
    	//Устанавливаем иконку посредством байт массива
		setIcon(kz.tamur.rt.Utils.processCreateImage(b));
        PropertyHelper.setPropertyValue(new PropertyValue(b, getProperties().getChild("view").getChild("barCodeImg")),
                xml, frame);
	}

    
    
	public void paint(Graphics g) {
        super.paint(g);
        // выделить рамкой объект при клике на нём в дизайнере
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
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("title".equals(name)) {
            setText(value.stringValue());
        } else if ("borderType".equals(name) || "borderTitle".equals(name)) {
            setBorder(value.borderValue());
        } else if ("autoresize".equals(name) || "width".equals(name) || "height".equals(name) || "image".equals(name)) {
            prefSize = PropertyHelper.getPreferredSize(this);
            maxSize = PropertyHelper.getMaximumSize(this);
            minSize = PropertyHelper.getMinimumSize(this);
            if ("autoresize".equals(name)) {
                autoResize = value.booleanValue();
            }
            getImage();
        } else if ("toolTip".equals(name)) {
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
                description = (byte[]) p.second;
            }
        }
        if (adapter != null)
            adapter.setLangId(langId);
        updateProperties();
    }

    /**
     * Обновление свойство компонента
     */
    private void updateProperties() {
        PropertyValue pv = null;
        if (mode == Mode.DESIGN) {
            pv = getPropertyValue(getProperties().getChild("title"));
            if (!pv.isNull()) {
                setText(pv.stringValue());
            }
        } else {
            pv = getPropertyValue(PROPS.getChild("pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
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
        pv = getPropertyValue(pn.getChild("barCodeImg"));
       
        if (!pv.isNull()) {
           // setIcon(Utils.processCreateImage(pv.getImageValue()));
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("foto");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                webNameIcon = name.toString();
            }
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
                    setIcon(kz.tamur.rt.Utils.processCreateImage(val));
                    PropertyHelper.setPropertyValue(new PropertyValue(val, getProperties().getChild("view").getChild("barCodeImg")),
                            xml, frame);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Устанавливает картинку в компонент.
     * 
     */
    private void getImage() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("view").getChild("barCodeImg"));
        ImageIcon img = null;
        if (!pv.isNull()) {
            // картинка которая буедт отображена если свойство не переопределено
        	byte[] b = pv.getImageValue();
        	base64Icon = new String(Base64.encode(b));
            img = kz.tamur.rt.Utils.processCreateImage(b);
            
        }
        if (mode != Mode.RUNTIME && img == null) {
            // для красоты в дизайнере будет отображаться наша картинка
            img = kz.tamur.rt.Utils.getImageIconFull("no-cover.png");
        }

        if (autoResize) {
            if (prefSize != null && prefSize.width != 0 && prefSize.height != 0) {
                img = kz.tamur.rt.Utils.setSize(img, prefSize.width, prefSize.height);
            }

        }
        setIcon(img);
        repaint();
    }
    
    public String getBase64Icon() {
    	return base64Icon;
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

    /**
     * Получить border title uid.
     * 
     * @return the border title uid
     */
    public String getBorderTitleUID() {
        return null;
    }

    //
    /**
     * Получить tab index.
     * 
     * @return the tab index
     */
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

    /**
     * Проверяет, является ли show popup.
     * 
     * @return true, если show popup
     */
    public boolean isShowPopup() {
        return showPopup;
    }

    
    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    /**
     * Установить adapter.
     * 
     * @param imageAdapter
     *            the new adapter
     */
    public void setAdapter(BarcodeAdapter bAdapter) {
        adapter = bAdapter;
    }

    
    public ComponentAdapter getAdapter() {
        return null;
    }

    
    public String getVarName() {
        return varName;
    }

    /**
     * Update tool tip.
     */
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }

    /**
     * Проверяет, является ли auto resize.
     * 
     * @return true, если auto resize
     */
    public boolean isAutoResize() {
        return autoResize;
    }
    
    @Override
    public String getUUID() {
        return UUID;
    }

    /**
     * @return the maxDataSize
     */
    public long getMaxDataSize() {
        return maxDataSize;
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
    
    public String getWebNameIcon() {
        return webNameIcon;
    }

    /**
     * @param webNameIcon the webNameIcon to set
     */
    public void setWebNameIcon(String webNameIcon) {
        this.webNameIcon = webNameIcon;
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


	public void setValue(Object value) {
		if(value instanceof String) {
			try {
				generateQR(150, 150, (String)value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(value instanceof Long) {
			long val = (Long)value;
			try {
				generateQR(150, 150, String.valueOf(val));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
}
