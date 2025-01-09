package kz.tamur.comps;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TextFieldPropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.*;
import kz.tamur.util.AttributeTypeChecker;
import kz.tamur.util.CopyButton;
import kz.tamur.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class OrPasswordField extends JPasswordField implements OrTextComponent, FocusListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private static final Log log = LogFactory.getLog(OrPasswordField.class);

    public static PropertyNode PROPS = new TextFieldPropertyRoot();
    private int mode;
    private Element xml;
    private boolean isSelected;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String toolTipContent = null;
    //protected OrTextDocument doc;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;

    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private OrPasswordCellEditor editor;

    private PasswordFieldAdapter adapter;
    private String descriptionUID;
    private boolean deleteOnType = false;

	private String varName;

    OrPasswordField(Element xml, int mode, OrFrame frame, boolean isEditor) throws KrnException {
        this.mode = mode;
        this.frame = frame;
        this.xml = xml;
        UUID = PropertyHelper.getUUID(this, isEditor);
        if (mode == Mode.RUNTIME) {
            constraints = PropertyHelper.getConstraints(PROPS, xml);
            prefSize = PropertyHelper.getPreferredSize(this);
            maxSize = PropertyHelper.getMaximumSize(this);
            minSize = PropertyHelper.getMinimumSize(this);
            //description = PropertyHelper.getDescription(this);
        }
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
            //setEditable(false);
            setEnabled(false);
        } else if (this.mode == Mode.RUNTIME) {
        	// Если компонент в режиме выполнения
        	// Создаем адаптер
        	adapter = new PasswordFieldAdapter(frame, this, isEditor);
            // Если не колонка, то слушаем фокус
            if(!isEditor) {
                addFocusListener(this);
                kz.tamur.rt.Utils.setComponentFocusCircle(this);
            }
        }
        setDisabledTextColor(Color.BLACK);
        updateProperties();

        if (mode == Mode.RUNTIME && !isEditor) {
        	// Слушаем фокус для выделения компонента
            addFocusListener(new DefaultFocusAdapter(adapter));
        	// Не храним XML в режиме выполнения компонента
        	this.xml = null;
        }
    }


    public void setEnabled(boolean enabled) {
        if (mode == Mode.DESIGN) {
            super.setEnabled(enabled);
        } else {
            super.setEditable(enabled);
        }
    }

/*
    public boolean isEnabled() {
        return super.isEditable();
    }
*/

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
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
            AttributeTypeChecker.instance().check(value, new long[] { AttributeTypeChecker.STRING_TYPE });
        }
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            setForeground(value.colorValue());
        } else if ("backgroundColor".equals(name)) {
            setBackground(value.colorValue());
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
        Utils.processBorderProperties(this, frame);
        if (copyBtn != null) {
            copyBtn.setCopyTitle(frame.getString(copyTitleUID));
        }
        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[])p.second;
            }
        }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
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
        if (mode == Mode.RUNTIME) {
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
            pv = getPropertyValue(
                    PROPS.getChild("pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
            pv = getPropertyValue(
                    PROPS.getChild("pov").getChild("deleteOnType"));
            deleteOnType = !pv.isNull() && pv.booleanValue();

            pv = getPropertyValue(
                    getProperties().getChild("pov").getChild("tabIndex"));

            if (!pv.isNull()) {
                tabIndex = pv.intValue();
            } else {
                tabIndex = pv.intValue();
            }
            if (adapter.getCopyRef() != null) {
                pn = getProperties().getChild("pov").getChild("copy");
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
                    if(copyBtn == null) {
                        setLayout(new BorderLayout());
                        copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        copyBtn.setCopyAdapter(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                            	adapter.doCopy();
                            }
                        });
                        add(copyBtn, BorderLayout.EAST);
                    }
                }
            }
        }
        Utils.processBorderProperties(this, frame);
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

    public int getTabIndex() {
        return tabIndex;
    }

    public String getCopyTitleUID() {
        return copyTitleUID;
    }

    // TODO объявить OrGuiComponent
    public ComponentAdapter getAdapter() {
    	return adapter;
    }


    // FocusListener
    public void focusGained(FocusEvent e) {
    	if (deleteOnType) {
            setSelectionStart(0);
            setSelectionEnd(getPassword().length);
            getCaret().setSelectionVisible(true);
        }
	}

	public void focusLost(FocusEvent e) {
    	try {
    		String text = new String(getPassword());
			adapter.changeValue(text.length() > 0 ? text : null);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
            Util.showErrorMessage(this, ex.getMessage(), "");
		}
	}

	public void setValue(Object value) {
		setText(value != null ? value.toString() : "");
	}

    public OrCellEditor getCellEditor() {
        if (editor == null) {
            editor = new OrPasswordCellEditor();
            addActionListener(editor);
            addFocusListener(editor);
            setBorder(BorderFactory.createEmptyBorder());
        }
        return editor;
    }


    private class OrPasswordCellEditor extends OrCellEditor implements FocusListener {

        public Object getCellEditorValue() {
            return new String(getPassword());
        }



        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            adapter.valueChanged(new OrRefEvent(adapter.getDataRef(), 0, -1, null));
            OrTableModel model = (OrTableModel) table.getModel();
            if (model instanceof TreeTableAdapter.RtTreeTableModel) {
                row = ((TreeTableAdapter.RtTreeTableModel) model).getActualRow(row);
            }
            adapter.setState(new Integer(row), new Integer(0));

            if (deleteOnType) {
                setSelectionStart(0);
                setSelectionEnd(getPassword().length);
                getCaret().setSelectionVisible(true);
            } else {
                getCaret().setVisible(true);
            }
            return OrPasswordField.this;
        }

        public Object getValueFor(Object obj) {
            return ((OrRef.Item) obj).getCurrent();
        }

        public OrRef getDataRef() {
            return adapter.getDataRef();
        }

        public boolean stopCellEditing() {
        	// TODO Убрать в OrTextField
            if (getParent() != null) {
                boolean res = checkUnique();
                if (res) {
                    if (OrPasswordField.this.isEditable())
                        try {
                            adapter.changeValue(new String(getPassword()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                } else {
                    res = showDuplicate();
                    return res;
                }
            }
            return super.stopCellEditing();
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            stopCellEditing();
        }
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

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