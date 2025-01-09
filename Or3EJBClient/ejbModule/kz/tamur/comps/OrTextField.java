package kz.tamur.comps;

import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Mode.RUNTIME;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.border.Border;

import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TextFieldPropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.rt.adapters.TextFieldAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.rt.adapters.Util;
import kz.tamur.util.AttributeTypeChecker;
import kz.tamur.util.CopyButton;
import kz.tamur.util.OrTextDocument;
import kz.tamur.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.gui.OrMultiLineToolTip;
import com.cifs.or2.kernel.KrnException;

import static kz.tamur.rt.Utils.createMenuItem;
public class OrTextField extends JTextField implements OrTextComponent, FocusListener, ActionListener, ClipboardOwner, KeyListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private static final Log log = LogFactory.getLog(OrTextField.class);

    public static PropertyNode PROPS = new TextFieldPropertyRoot();
    private int mode;
    private Element xml;
    private boolean isSelected;
    protected OrTextDocument doc;
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
    private int tabIndex;
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;

    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private OrTextCellEditor editor;

    private TextFieldAdapter adapter;
    private String descriptionUID;
    private boolean deleteOnType = false;

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miPaste = createMenuItem("Вставить");
    private JMenuItem miCut = createMenuItem("Вырезать");
    private JMenuItem miCopy = createMenuItem("Копировать");
    private String varName;

    private boolean isToolTip = false;

	private boolean alwaysFocused = false;
	private boolean requestFocus = false;
	private boolean emailType = false;
	

    OrTextField(Element xml, int mode, final OrFrame frame, boolean isEditor) throws KrnException {
        this.mode = mode;
        this.frame = frame;
        this.xml = xml;
        UUID = PropertyHelper.getUUID(this, isEditor);
        if (mode == RUNTIME) {
            constraints = PropertyHelper.getConstraints(PROPS, xml);         
            prefSize = PropertyHelper.getPreferredSize(this);
            maxSize = PropertyHelper.getMaximumSize(this);
            minSize = PropertyHelper.getMinimumSize(this);
            // description = PropertyHelper.getDescription(this);
            miCut.addActionListener(this);
            miPaste.addActionListener(this);
            miCopy.addActionListener(this);
            pm.add(miCopy);
            pm.add(miPaste);
            pm.add(miCut);
            addMouseListener(new MouseAdapter() {
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
                        pm.show(OrTextField.this.getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());
                    }
                }
            });


        }
        if (this.mode == DESIGN) {
        	PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            isToolTip = true;
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = (String) pv.resourceStringValue().first;
                    isToolTip = toolTipUid != null;
                    byte[] toolTip = frame.getBytes(toolTipUid);
                    if (toolTip != null && toolTip.length > 0) {
                        setToolTipText(new String(toolTip));
	                    SAXBuilder builder = new SAXBuilder();
	                    InputStream is = new ByteArrayInputStream(toolTip);
	                    try {
							Element var_doc = builder.build(is).getRootElement();
							if(var_doc.getName().equals("html")) {
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
            // setEditable(false);
            setEnabled(false);
        } else if (this.mode == RUNTIME) {
            // Если компонент в режиме выполнения
            // Создание адаптера
            adapter = new TextFieldAdapter(frame, this, isEditor);
            // Инициализация всплывающей подсказки
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            isToolTip = true;
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = (String) pv.resourceStringValue().first;
                    isToolTip = toolTipUid != null;
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

            // Создание документа
            doc = new OrTextDocument(this, this.mode);
            setDocument(doc);
        }
        setDisabledTextColor(Color.BLACK);
        
        updateProperties();

        if (mode == RUNTIME && !isEditor) {
            // Слушаем фокус для выделения компонента
           addFocusListener(this);
           addFocusListener(new DefaultFocusAdapter(adapter));
            
            if (alwaysFocused) {
            	frame.setAllwaysFocused(this);
            	this.addKeyListener(this);
            	requestFocusInWindow();
            	removeEnterKs();
            } else {
                kz.tamur.rt.Utils.setComponentFocusCircle(this);
            }
            // Не храним XML в режиме выполнения компонента
            this.xml = null;
        }
    }

    public void setEnabled(boolean enabled) {
        if (mode == DESIGN) {
            super.setEnabled(enabled);
        } else {
            super.setEditable(enabled);
            miPaste.setEnabled(enabled);
            miCut.setEnabled(enabled);
        }
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == DESIGN && isSelected) {
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
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
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
        if ("data".equals(prop.getName())) {
            AttributeTypeChecker.instance().check(value, new long[] { AttributeTypeChecker.STRING_TYPE });
        }
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        if ("fontG".equals(prop.getName())) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(prop.getName())) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("backgroundColor".equals(prop.getName())) {
            Color val = value.isNull() ? Color.WHITE : value.colorValue();
            setBackground(val);
        } else if ("alignmentText".equals(prop.getName())) {
            setHorizontalAlignment(value.intValue());
        } else if ("toolTip".equals(prop.getName())) {
            if (value.isNull()) {
                toolTipUid = null;
                isToolTip = false;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                isToolTip = toolTipUid != null;
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
        Utils.processBorderProperties(this, frame);
        if (copyBtn != null) {
            copyBtn.setCopyTitle(frame.getString(copyTitleUID));
        }
        if (mode == RUNTIME) {
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
    }

    private void updateProperties() {
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("typeEmail"));
        if (!pv.isNull()) {
            emailType = pv.booleanValue();
        }
        if (mode == RUNTIME) {
            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = (byte[]) p.second;
            }
            pv = getPropertyValue(PROPS.getChild("pov").getChild("activity").getChild("editable"));
            setEnabled(pv.isNull() ? true : !pv.booleanValue());
            
            pv = getPropertyValue(PROPS.getChild("pov").getChild("deleteOnType"));
            deleteOnType = pv.isNull() ? false : pv.booleanValue();
                
            pn = getProperties().getChild("constraints");
            pv = getPropertyValue(pn.getChild("charsNumber"));
            if (!pv.isNull()) {
                int count = pv.intValue();
                doc.setCharsLimit(count);
            }
            pv = getPropertyValue(pn.getChild("exclude"));
            if (!pv.isNull()) {
                doc.setExcludeChars(pv.stringValue());
            } else {
                doc.setExcludeChars("");
            }
            pv = getPropertyValue(pn.getChild("include"));
            doc.setIncludeChars(pv.isNull()?"":pv.stringValue());

            pv = getPropertyValue(getProperties().getChild("pov").getChild("tabIndex"));
            tabIndex = pv.intValue();
            
            if (tabIndex == -1) {
        		alwaysFocused = true;
            }

            if (adapter.getCopyRef() != null) {
                pn = getProperties().getChild("pov").getChild("copy");
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String) pv.resourceStringValue().first;
                    if (copyBtn == null) {
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

    public boolean isEmailType() {
		return emailType;
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
            setSelectionEnd(getText().length());
            getCaret().setSelectionVisible(true);
        }
    }

    public void focusLost(FocusEvent e) {
        saveValue();
        if (requestFocus) {
        	requestFocus = false;
        	System.out.println(e.getSource());
        	requestFocusInWindow();
        }
    }
    
    /**
     * Removes the enter ks.
     */
    private void removeEnterKs() {
        AWTKeyStroke enterKs = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0, false);
        Set<AWTKeyStroke> keys = new HashSet<AWTKeyStroke>(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        keys.remove(enterKs);
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);

        enterKs = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_DOWN_MASK, false);
        keys = new HashSet<AWTKeyStroke>(getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        keys.remove(enterKs);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
    }

    @Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			requestFocus = true;
			transferFocus();
		}
	}

	public void saveValue() {
        try {
            String text = getText();
            adapter.changeValue(text.length() > 0 ? text : null);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            Util.showErrorMessage(this, ex.getMessage(), "");
        }
    }

    public void setValue(Object value) {
        setText(value != null ? value.toString() : "");
        // перевод каретки необходим в случаях когда размер текста превышает размер поля и нужно отобразить начало текста
        setCaretPosition(0);
    }

    public OrCellEditor getCellEditor() {
        if (editor == null) {
            editor = new OrTextCellEditor();
            addActionListener(editor);
            addFocusListener(editor);
           setBorder(BorderFactory.createEmptyBorder());
        }
        return editor;
    }

    private class OrTextCellEditor extends OrCellEditor implements FocusListener {

        public Object getCellEditorValue() {
            return getText();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            adapter.valueChanged(new OrRefEvent(adapter.getDataRef(), 0, -1, null));
            OrTableModel model = (OrTableModel) table.getModel();
            if (model instanceof TreeTableAdapter.RtTreeTableModel) {
                row = ((TreeTableAdapter.RtTreeTableModel) model).getActualRow(row);
            }
            adapter.setState(new Integer(row), new Integer(0));

            if (deleteOnType) {
                setSelectionStart(0);
                setSelectionEnd(getText().length());
                getCaret().setSelectionVisible(true);
            } else {
                getCaret().setVisible(true);
            }
            return OrTextField.this;
        }

        public Object getValueFor(Object obj) {
            return ((OrRef.Item) obj).getCurrent();
        }

        public OrRef getDataRef() {
            return adapter.getDataRef();
        }

        public boolean stopCellEditing() {
            if (getParent() != null) {
                boolean res = checkUnique();
                if (res) {
                    if (OrTextField.this.isEditable())
                        try {
                            adapter.changeValue(getText());
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
            // перевод каретки необходим в случаях когда размер текста превышает размер поля и нужно отобразить начало текста            
            setCaretPosition(0);
        }
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
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
        // odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                // highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public JToolTip createToolTip() {
        return isToolTip ? super.createToolTip() : new OrMultiLineToolTip();
    }

    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                isToolTip = !toolTipExprText_.isEmpty();
                if (!isToolTip) {
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
    
    public OrTextDocument getOrTextDocument(){
    	return doc;
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
