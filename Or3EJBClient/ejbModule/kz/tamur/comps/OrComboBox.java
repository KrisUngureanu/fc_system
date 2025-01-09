package kz.tamur.comps;

import static kz.tamur.comps.Constants.INSETS_0;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.ComboBoxPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComboBoxAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter.OrComboItem;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 18:24:06
 */
public class OrComboBox extends JPanel implements OrGuiComponent, MouseTarget {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new ComboBoxPropertyRoot();
    private int mode;
    private boolean isSelected;
    private Element xml;
    private OrFrame frame;
    private OrGuiContainer parent;
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
    private ComboBoxAdapter adapter;
    private boolean isHelpClick = false;
    private String descriptionUID;

	private String varName;

	private JComponent comp;
	
    OrComboBox(Element xml, int mode, OrFrame frame, boolean isEditor) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        this.setLayout(new GridBagLayout());
        UUID = PropertyHelper.getUUID(this, isEditor);
        if (mode == Mode.DESIGN) {
            Component[] cs = getComponents();
            for (int i = 0; i < cs.length; i++) {
                Component c = cs[i];
                c.addMouseListener(new InternalMouseListener());
                c.addMouseMotionListener(new InternalMouseListener());
            }
            enableEvents(AWTEvent.MOUSE_EVENT_MASK);
            setEnabled(false);
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        
        updateProperties();
        
        if (mode == Mode.RUNTIME) {
        	adapter = new ComboBoxAdapter(frame, this, isEditor);
        	adapter.calculateContent();
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
        	// Слушаем фокус для выделения компонента
            if (!isEditor) {
            	if (comp instanceof JComboBox) {
            		((JComboBox)comp).getEditor().getEditorComponent().addFocusListener(new DefaultFocusAdapter(adapter));
            	}
            	else if (comp instanceof JList)
            		((JList)comp).addFocusListener(new DefaultFocusAdapter(adapter));
            	 
        	    kz.tamur.rt.Utils.setComponentTabFocusCircle(comp);
            }
        	// Не храним XML в режиме выполнения
        	this.xml = null;
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
	                if (toolTip != null) {
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
    }

    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.translatePoint(c.getX(), c.getY());
        e.setSource(this);
        this.processMouseEvent(e);
    }

    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.translatePoint(c.getX(), c.getY());
        e.setSource(this);
        processMouseMotionEvent(e);
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(OrComboBox.this, g);
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
        processProperties(value);
        PropertyNode prop = value.getProperty();
        if ("toolTip".equals(prop.getName())) {
            if (value.isNull()) {
                toolTipUid = null;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null) {
                    setToolTipText(new String(toolTip));
                }
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
        } else if ("appearance".equals(prop.getName())) {
            int appearance = Constants.VIEW_SIMPLE_COMBO;
            if (!value.isNull()) {
            	appearance = value.enumValue();
            }
            if (appearance == Constants.VIEW_SIMPLE_COMBO || appearance == Constants.VIEW_SOLID_LIST) {
            	comp = new Or3ComboBox();
            }
            else if (appearance == Constants.VIEW_LIST || appearance == Constants.VIEW_CHECKBOX_LIST) {
            	comp = new JList();
            }
        	this.removeAll();
        	this.add(comp, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, INSETS_0, 0, 0));
            comp.addMouseListener(new InternalMouseListener());
            comp.addMouseMotionListener(new InternalMouseListener());
            Utils.updateConstraints(this);
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

    class InternalMouseListener extends MouseInputAdapter {
        public void mousePressed(MouseEvent e) {
            e.setSource(OrComboBox.this);
            OrComboBox.this.dispatchEvent(e);
        }

        public void mouseMoved(MouseEvent e) {
            Component c = (Component) e.getSource();
            e.translatePoint(c.getX(), c.getY());
            e.setSource(OrComboBox.this);
            OrComboBox.this.dispatchEvent(e);
        }

        public void mouseDragged(MouseEvent e) {
            Component c = (Component) e.getSource();
            e.translatePoint(c.getX(), c.getY());
            e.setSource(OrComboBox.this);
            OrComboBox.this.processMouseMotionEvent(e);
        }

        // implements java.awt.event.MouseListener
        public void mouseReleased(MouseEvent e) {
            e.setSource(OrComboBox.this);
            OrComboBox.this.dispatchEvent(e);
        }

    }

    public void fireEvent() {
    	if (comp instanceof Or3ComboBox) {
    		((Or3ComboBox)comp).fireEvent();
    	}
    }

    private void processProperties(PropertyValue pv) {
        PropertyNode pn = pv.getProperty();
        if ("backgroundColor".equals(pn.getName())) {
            setBackground(pv.colorValue());
        } else if ("fontColor".equals(pn.getName())) {
            setForeground(pv.colorValue());
        } else if ("fontG".equals(pn.getName())) {
            setFont(pv.fontValue());
        } else if ("borderType".equals(pn.getName())) {
            setBorder(pv.borderValue());
        }else if ("alignmentText".equals(pn.getName())) {
            // TODO реализация приостановлена - рендер отказывается работать
            /*DefaultListCellRenderer dlcr = new DefaultListCellRenderer(); 
            dlcr.setHorizontalAlignment(pv.intValue()); 
            setRenderer(dlcr); */
         }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        } else {
            setForeground((Color)pn.getChild("font").getChild("fontColor").getDefaultValue());
        }
        pv = getPropertyValue(pn.getChild("appearance"));
        int appearance = Constants.VIEW_SIMPLE_COMBO;
        if (!pv.isNull()) {
        	appearance = pv.enumValue();
        }
        if (appearance == Constants.VIEW_SIMPLE_COMBO || appearance == Constants.VIEW_SOLID_LIST) {
        	comp = new Or3ComboBox();
        }
        else if (appearance == Constants.VIEW_LIST || appearance == Constants.VIEW_CHECKBOX_LIST) {
        	comp = new JList();
        }
    	this.removeAll();
    	this.add(comp, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, INSETS_0, 0, 0));
        comp.addMouseListener(new InternalMouseListener());
        comp.addMouseMotionListener(new InternalMouseListener());
        Utils.updateConstraints(this);

     // TODO реализация приостановлена - рендер отказывается работать
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {/*
            DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
            dlcr.setHorizontalAlignment(pv.intValue());
            setRenderer(dlcr);

            ListCellRenderer renderer = new DefaultListCellRenderer();
            ((JLabel) renderer).setHorizontalAlignment(SwingConstants.RIGHT);
            setRenderer(renderer);

        */}
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        } else {
            setBackground((Color)pn.getChild("background").getChild("backgroundColor").getDefaultValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        } else {
            setFont((Font)pn.getChild("font").getChild("fontG").getDefaultValue());
        }

        pn = getProperties().getChild("pov");
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
    
    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mode != Mode.DESIGN) {
        	if (comp instanceof JComboBox) {
        		((JComboBox)comp).getEditor().getEditorComponent().setEnabled(enabled);
        		((JTextField)((JComboBox)comp).getEditor().getEditorComponent()).setEditable(enabled);
        		((JComboBox)comp).setEnabled(enabled);
        	} else if (comp instanceof JList) {
        		((JList)comp).setEnabled(enabled);
        	}
        }
    }
    
    public void setEditable(boolean b) {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).setEditable(b);
    	}
    }

    public void setValue(Object value) {
    	if (value instanceof KrnObject) {
    		KrnObject o = (KrnObject)value;
	        int count = getItemCount();
	        if (count > 0) {
                for (int i = 0; i < count; ++i) {
                    OrComboItem item = (OrComboItem)getItemAt(i);
                    KrnObject curr = item.getObject();
                    if (o != null && curr != null && o.id == curr.id) {
                        setSelectedIndex(i);
                        break;
                    }
                }
	        }
    	} else if (value instanceof String) {
    		String o = (String)value;
	        int count = getItemCount();
	        if (count > 0) {
                for (int i = 0; i < count; ++i) {
                    OrComboItem item = (OrComboItem)getItemAt(i);
                    String curr = item.toString();
                    if (o != null && curr != null && o.equals(curr)) {
                        setSelectedIndex(i);
                        break;
                    }
                }
	        }
    	} else {
    		setSelectedIndex(-1);
    	}
    }
    
    public void setSelectedIndex(int i) {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).setSelectedIndex(i);
    	} else if (comp instanceof JList) {
    		((JList)comp).setSelectedIndex(i);
    	}
    }

    public int getItemCount() {
    	if (comp instanceof JComboBox) {
    		return ((JComboBox)comp).getItemCount();
    	} else if (comp instanceof JList) {
    		return ((JList)comp).getModel().getSize();
    	}
    	return 0;
    }

    public Object getItemAt(int i) {
    	if (comp instanceof JComboBox) {
    		return ((JComboBox)comp).getItemAt(i);
    	} else if (comp instanceof JList) {
    		return ((JList)comp).getModel().getElementAt(i);
    	}
    	return null;
    }

    public Object getSelectedItem() {
    	if (comp instanceof JComboBox) {
    		return ((JComboBox)comp).getSelectedItem();
    	} else if (comp instanceof JList) {
    		return ((JList)comp).getSelectedValue();
    	}
    	return null;
    }

    public void setSelectedItem(Object o) {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).setSelectedItem(o);
    	} else if (comp instanceof JList) {
    		((JList)comp).setSelectedValue(o, true);
    	}
    }

    public int getSelectedIndex() {
    	if (comp instanceof JComboBox) {
    		return ((JComboBox)comp).getSelectedIndex();
    	} else if (comp instanceof JList) {
    		return ((JList)comp).getSelectedIndex();
    	}
    	return -1;
    }

    public void removeAllItems() {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).removeAllItems();
    	} else if (comp instanceof JList) {
    		((JList)comp).setModel(new DefaultComboBoxModel());
    	}
    }

    public void setModel(ComboBoxModel m) {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).setModel(m);
    	} else if (comp instanceof JList) {
    		((JList)comp).setModel(m);
    	}
    }

    public void setRenderer(ListCellRenderer r) {
    	if (comp instanceof JComboBox) {
    		((JComboBox)comp).setRenderer(r);
    	} else if (comp instanceof JList) {
    		((JList)comp).setCellRenderer(r);
    	}
    }

    public JComponent getEditorComponent() {
    	if (comp instanceof JComboBox) {
    		return (JComponent) ((JComboBox)comp).getEditor().getEditorComponent();
    	} else if (comp instanceof JList) {
    		return comp;
    	}
    	return comp;
    }

    public ComboBoxEditor getEditor() {
    	if (comp instanceof JComboBox)
    		return ((JComboBox)comp).getEditor();
    	else
    		return null;
    }

    public ComponentAdapter getAdapter() {
    	return adapter;
    }

	
	public void setAdapter(ComponentAdapter adapter) {
		this.adapter = (ComboBoxAdapter)adapter;
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
    
    public JComboBox getComboBox() {
    	if (comp instanceof JComboBox)
    		return (JComboBox)comp;
    	return null;
    }
    
    public JList getList() {
    	if (comp instanceof JList)
    		return (JList)comp;
    	return null;
    }
    
    public class Or3ComboBox extends JComboBox {
    	public void fireEvent() {
    		this.fireActionEvent();
    	}
    }

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		// TODO Auto-generated method stub
		comp.setPreferredSize(preferredSize);
		super.setPreferredSize(preferredSize);
	}

	@Override
	public void setMaximumSize(Dimension maximumSize) {
		// TODO Auto-generated method stub
		comp.setPreferredSize(maximumSize);
		super.setMaximumSize(maximumSize);
	}

	@Override
	public void setMinimumSize(Dimension minimumSize) {
		// TODO Auto-generated method stub
		comp.setPreferredSize(minimumSize);
		super.setMinimumSize(minimumSize);
	}
}
