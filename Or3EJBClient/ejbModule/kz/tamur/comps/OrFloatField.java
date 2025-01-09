package kz.tamur.comps;

import kz.tamur.comps.models.FloatFieldPropertyRoot;
import static kz.tamur.rt.Utils.createMenuItem;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.AttributeTypeChecker;
import kz.tamur.util.CopyButton;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatterFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class OrFloatField extends JFormattedTextField implements OrGuiComponent, ActionListener, ClipboardOwner {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new FloatFieldPropertyRoot();
    private int mode;
    private Element xml;
    private boolean isSelected;
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
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private String copyRefPath;
    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private String descriptionUID;
    private boolean deleteOnType = false;
    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miPaste = createMenuItem("Вставить");
    private JMenuItem miCut = createMenuItem("Вырезать");
    private JMenuItem miCopy = createMenuItem("Копировать");
	private String varName;

    OrFloatField(Element xml, int mode, OrFrame frame, boolean isEditor) {
        //super();
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        //description = PropertyHelper.getDescription(this);
        
               
        updateProperties();
        if (this.mode == Mode.DESIGN) {
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
            setEnabled(false);
        } else {//if (this.mode == Mode.RUNTIME) {
            miCut.addActionListener(this);
            miPaste.addActionListener(this);
            miCopy.addActionListener(this);
            pm.add(miCopy);
            pm.add(miPaste);
            pm.add(miCut);
            addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e) {
                    showPop(e);
                }

                public void mousePressed(MouseEvent e) {
                    showPop(e);
                }

                public void mouseReleased(MouseEvent e) {
                    showPop(e);
                }

                private void showPop(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        OrFloatField.this.miPaste.setEnabled(OrFloatField.this.isEditable());
                        OrFloatField.this.miCut.setEnabled(OrFloatField.this.isEditable());
                        pm.show(OrFloatField.this.getComponentAt(e.getX(), e.getY()),
                                e.getX(), e.getY());
                    }
                }
            });

            PropertyNode pn = PROPS.getChild("constraints").getChild("formatPattern");
            PropertyValue pv = getPropertyValue(pn);
            String pattern = "";
            if (!pv.isNull()) {
                pattern = pv.stringValue();
            } else {
                pattern = pn.getDefaultValue().toString();
                setPropertyValue(new PropertyValue(pattern, pn));
            }
            DecimalFormat pat = new DecimalFormat(pattern);
            pat.setGroupingUsed(true);
            pat.setGroupingSize(3);
            DecimalFormatSymbols dfs = pat.getDecimalFormatSymbols();
            dfs.setGroupingSeparator(' ');
            dfs.setDecimalSeparator(',');
            pat.setDecimalFormatSymbols(dfs);
            FloatFormatter fmt = new FloatFormatter(pat);
            setFormatterFactory(new DefaultFormatterFactory(fmt, fmt, fmt));
        }
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
            PropertyNode pn = getProperties().getChild("pov").getChild("copy");
            pv = getPropertyValue(pn.getChild("copyPath"));
            if (!pv.isNull()) {
                copyRefPath = pv.stringValue();
            }
            if (copyRefPath != null) {
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
                    if(copyBtn == null) {
                        setLayout(new BorderLayout());
                        copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        add(copyBtn, BorderLayout.WEST);
                    }
                }
            }
        }
        setHorizontalAlignment(SwingConstants.RIGHT);
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

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("data".equals(name)) {
            AttributeTypeChecker.instance().check(value, new long[] { AttributeTypeChecker.FLOAT_TYPE });
        }
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Color.WHITE : value.colorValue();
            setBackground(val);
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
        updateProperties();
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
    }

    private void updateProperties() {
        PropertyValue pv = null;
        pv = getPropertyValue(
                PROPS.getChild("pov").getChild("deleteOnType"));
        if (!pv.isNull()) {
            deleteOnType = pv.booleanValue();
        } else {
            deleteOnType = false;
        }
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        Utils.processBorderProperties(this, frame);
/*
        pn = getProperties().getChild("border").getChild("borderType");
        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setBorder((Border)pn.getDefaultValue());
        }
*/
        pn = getProperties().getChild("pov");
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEditable(!pv.booleanValue());
            } else {
                setEditable(true);
            }
        }
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
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

    public String getBorderTitleUID() {
        return null;
    }

    //
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

    public class FloatFormatter extends JFormattedTextField.AbstractFormatter {

        private DecimalFormat fmt;
        private DecimalFormat summFmt;

        public FloatFormatter(DecimalFormat fmt) {
            this.fmt = fmt;
            this.summFmt = (DecimalFormat)fmt.clone();
        }

        public Object stringToValue(String text) throws ParseException {
            Number res = null;
            if (text != null && text.length() > 0) {
                text = text.replaceAll(" ", "");
                res = fmt.parse(text);
                if (!(res instanceof Double)) {
                    res = new Double(res.doubleValue());
                }
            }
            return res;
        }

        public String valueToString(Object value) throws ParseException {
            if (value instanceof Number) {
                return fmt.format(value);
            }
            return "";
        }

        public DecimalFormat getFmt() {
            return fmt;
        }

        public DecimalFormat getSummFmt() {
            return summFmt;
        }
    }

    public void setEnabled(boolean enabled) {
        if (mode == Mode.DESIGN) {
            super.setEnabled(enabled);
        } else {
            super.setEditable(enabled);
            miPaste.setEnabled(enabled);
            miCut.setEnabled(enabled);
        }
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public String getCopyRefPath() {
        return copyRefPath;
    }

    public CopyButton getCopyBtn() {
        return copyBtn;
    }

    public boolean isDeleteOnType() {
        return deleteOnType;
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
		if (src == miCopy) {
			String txt = getSelectedText();
			if (txt != null && txt.length() > 0)
				setClipboardContents(txt);
		} else if (src == miPaste) {
			String txt = getClipboardContents(); 
			if (txt != null)
				replaceSelection(txt);
		} else if (src == miCut) {
			String txt = getSelectedText();
			if (txt != null && txt.length() > 0) {
				setClipboardContents(txt);
				replaceSelection("");
			}
		}
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//	odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText =
			(contents != null) &&
							contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if ( hasTransferableText ) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException ex){
				//highly unlikely since we are using a standard DataFlavor
				System.out.println(ex);
				ex.printStackTrace();
			}
			catch (IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
	    }
	    return result;
	}

	public void setClipboardContents( String aString ){
		StringSelection stringSelection = new StringSelection(aString );
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this);
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
