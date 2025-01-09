package kz.tamur.comps;

import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.DateFieldPropertyRoot;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.DateFieldAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.util.AttributeTypeChecker;
import kz.tamur.util.CalendarAdapter;
import kz.tamur.util.CalendarButton;
import kz.tamur.util.DateField;
import kz.tamur.util.CopyButton;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.rt.Utils.createMenuItem;

public class OrDateField extends DateField implements OrTextComponent, ActionListener, ClipboardOwner {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new DateFieldPropertyRoot();

    private int mode;

    private OrGuiContainer parent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    
    private String toolTipContent = null;

    private Border standartBorder;

    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    private Element xml;

    private boolean isSelected;

    private OrFrame frame;

    private int tabIndex;

    private boolean isCopy = false;

    private GridBagConstraints constraints;

    private Dimension prefSize;

    private Dimension maxSize;

    private Dimension minSize;

    private String copyRefPath;

    private String copyTitleUID;

    /** Кнопка копирования */
    private CopyButton copyBtn = null;

    /** Кнопка календаря */
    private CalendarButton calBtn = null;

    private byte[] description;

    private String descriptionUID;

    private boolean deleteOnType = true;
    private boolean showCalendar = true;
    private boolean showCopy = false;

    private DateFieldAdapter adapter;
    private OrDateCellEditor editor;

    private ThreadLocalDateFormat FORMAT_;
    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miPaste = createMenuItem("Вставить");
    private JMenuItem miCut = createMenuItem("Вырезать");
    private JMenuItem miCopy = createMenuItem("Копировать");

    private String varName;
    CopyAdapter copyAdapter = null;
    CalendarAdapter calAdapter = null;

    /** Панель на которой расположены кнопки календаря и копирования */
    private JPanel panelBtn = null;
    /** Коофициент изменения ширины поля при добавлении, удалени кнопок */
    private int addedWidth = 0;

    OrDateField(Element xml, int mode, OrFrame frame, boolean isEditor) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this, isEditor);
        dateFormat = Constants.DD_MM_YYYY;
        constraints = PropertyHelper.getConstraints(PROPS, xml);

        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);

        PropertyValue pv = getPropertyValue(PROPS.getChild("view").getChild("showDateChooser"));
        showCalendar = !pv.isNull() && pv.booleanValue();
        if (showCalendar && this.mode == Mode.RUNTIME) {
            calAdapter = new CalendarAdapter(this);
        }

        PropertyNode pn = getProperties().getChild("pov").getChild("copy");
        if (getPropertyValue(pn.getChild("copyPath")) != null) {
            pv = getPropertyValue(pn.getChild("copyTitle"));
            if (!pv.isNull()) {
                showCopy = copyBtn == null && !frame.getString((String) pv.resourceStringValue().first).isEmpty();
                if (showCopy && this.mode == Mode.RUNTIME) {
                    copyAdapter = new CopyAdapter();
                }
            }
        }
/*        if (prefSize != null) {
            prefSize = new Dimension((int) prefSize.getWidth() + addedWidth, (int) prefSize.getHeight());
        }
        if (maxSize != null) {
            maxSize = new Dimension((int) maxSize.getWidth() + addedWidth, (int) maxSize.getHeight());
        }
        if (minSize != null) {
            minSize = new Dimension((int) minSize.getWidth() + addedWidth, (int) minSize.getHeight());
        }
*/
        if (this.mode == Mode.RUNTIME) {
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
        }
        updateProperties();
        if (this.mode == Mode.DESIGN) {
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
        } else if (this.mode == Mode.RUNTIME) {
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
                        pm.show(OrDateField.this.getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());
                    }
                }
            });
            // Создаем адаптер
            adapter = new DateFieldAdapter(frame, this, isEditor);

            if (!isEditor) {
                addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                    }
                });
                addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                            valueChanged();
                    }
                });

                addFocusListener(new DefaultFocusAdapter(this));

                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (deleteOnType) {
                            setSelectionStart(0);
                            setSelectionEnd(getText().length());
                            getCaret().setSelectionVisible(true);
                        }
                    }
                });
            }

            if (copyBtn != null) {
                if (copyAdapter == null) {
                    copyAdapter = new CopyAdapter();
                }
                copyBtn.setCopyAdapter(copyAdapter);
            }

            FORMAT_ = Funcs.getDateFormat(getDateFormat());
        }

        if (showCalendar && this.mode == Mode.RUNTIME) {
            if (calAdapter == null) {
                calAdapter = new CalendarAdapter(this);
            }
            calBtn.setCopyAdapter(calAdapter);
        }
    }

    public void setEnabled(boolean enabled) {
        if (mode == Mode.DESIGN) {
            super.setEnabled(enabled);
        } else {
            super.setEditable(enabled);
            miPaste.setEnabled(enabled);
            miCut.setEnabled(enabled);
            if (copyBtn != null) {
                copyBtn.setEnabled(enabled);
            }
            if (calBtn != null) {
                calBtn.setEnabled(enabled);
            }
        }
    }

    // For copy process
    public boolean isCopy() {
        return isCopy;
    }

    public boolean isShowCalendar() {
        return showCalendar;
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
        return mode == RUNTIME ? prefSize : new Dimension(PropertyHelper.getPreferredSize(this).width + addedWidth, PropertyHelper.getPreferredSize(this).height);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return tabIndex;
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
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("data".equals(name)) {
            AttributeTypeChecker.instance().check(value, new long[] { AttributeTypeChecker.DATE_TYPE });
        }
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        if ("font".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            setForeground(value.colorValue());
        } else if ("backgroundColor".equals(name)) {
            setBackground(value.colorValue());
        } else if ("boredrType".equals(name)) {
            setBorder(value.borderValue());
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
        super.setLangId(langId);
        Utils.processBorderProperties(this, frame);
        if (copyBtn != null) {
            copyBtn.setCopyTitle(frame.getString(copyTitleUID));
        }
        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
            if (calAdapter != null) {
                calAdapter.updateToolTipCalendar();
            }
            if (copyBtn == null && copyTitleUID != null && !frame.getString(copyTitleUID).isEmpty()) {
                copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                if (this.mode == Mode.RUNTIME) {
                    if (copyAdapter == null) {
                        copyAdapter = new CopyAdapter();
                    }
                    copyBtn.setCopyAdapter(copyAdapter);
                }
                panelBtn.add(copyBtn);
                showCopy = true;
                if (prefSize != null) {
                    prefSize = new Dimension((int) prefSize.getWidth() + 20, (int) prefSize.getHeight());
                }
                if (maxSize != null) {
                    maxSize = new Dimension((int) maxSize.getWidth() + 20, (int) maxSize.getHeight());
                }
                if (minSize != null) {
                    minSize = new Dimension((int) minSize.getWidth() + 20, (int) minSize.getHeight());
                }

                updatePanelBtn();
                Utils.updateConstraints(this);
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
        pv = getPropertyValue(PROPS.getChild("pov").getChild("deleteOnType"));
        deleteOnType = pv.isNull() ? true : pv.booleanValue();

        
        PropertyNode pn = PROPS.getChild("view");
        pv = getPropertyValue(pn.getChild("format"));
        
        setText("");
        if (!pv.isNull()) {
            dateFormat = pv.intValue();
        } else {
            dateFormat = ((EnumValue) pn.getChild("format").getDefaultValue()).code;
            setPropertyValue(new PropertyValue(dateFormat, pn.getChild("format")));
        }

        switch (dateFormat) {
        case Constants.DD_MM_YYYY:
            setText(MASK_);
            break;
        case Constants.DD_MM_YYYY_HH_MM:
            setText(MASK_1);
            break;
        case Constants.DD_MM_YYYY_HH_MM_SS:
            setText(MASK_2);
            break;
        case Constants.DD_MM_YYYY_HH_MM_SS_SSS:
            setText(MASK_3);
            break;
        case Constants.HH_MM_SS:
            setText(MASK_4);
            break;
        case Constants.HH_MM:
            setText(MASK_5);
            break;
        case Constants.DD_MM:
            setText(MASK_6);
            break;
        }
        
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

        if (panelBtn == null) {
            setLayout(new BorderLayout());
            panelBtn = new JPanel();
            panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
            panelBtn.setOpaque(false);
            add(panelBtn, BorderLayout.EAST);
        }

        addedWidth = 0;

        pv = getPropertyValue(PROPS.getChild("view").getChild("showDateChooser"));
        if (!pv.isNull() && pv.booleanValue()) {
            if (calBtn == null) {
                calBtn = new CalendarButton(this, "Выбор даты из календаря");
                if (this.mode == Mode.RUNTIME) {
                    if (calAdapter == null) {
                        calAdapter = new CalendarAdapter(this);
                    }
                    calBtn.setCopyAdapter(calAdapter);
                }
                panelBtn.add(calBtn);
                addedWidth += 20;
            }
        } else {
            if (calBtn != null) {
                panelBtn.remove(calBtn);
                addedWidth -= 20;
                calBtn = null;
            }
        }
        showCalendar = calBtn != null;

        pn = PROPS.getChild("pov").getChild("copy");
        pv = getPropertyValue(pn.getChild("copyPath"));
        copyRefPath = pv.isNull() ? null : pv.stringValue();
        if (copyRefPath != null) {
            pv = getPropertyValue(pn.getChild("copyTitle"));
            if (!pv.isNull()) {
                copyTitleUID = (String) pv.resourceStringValue().first;
                if (copyBtn == null && !frame.getString(copyTitleUID).isEmpty()) {
                    copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                    if (this.mode == Mode.RUNTIME) {
                        if (copyAdapter == null) {
                            copyAdapter = new CopyAdapter();
                        }
                        copyBtn.setCopyAdapter(copyAdapter);
                    }
                    panelBtn.add(copyBtn);
                    addedWidth += 20;

                }

            }
        }

        if (copyBtn != null && (frame.getString(copyTitleUID).isEmpty() || copyRefPath == null)) {
            panelBtn.remove(copyBtn);
            addedWidth -= 20;
            copyBtn = null;
        }

        showCopy = copyBtn != null;

        if (prefSize != null) {
            prefSize = new Dimension((int) prefSize.getWidth() + addedWidth, (int) prefSize.getHeight());
        }
        if (maxSize != null) {
            maxSize = new Dimension((int) maxSize.getWidth() + addedWidth, (int) maxSize.getHeight());
        }
        if (minSize != null) {
            minSize = new Dimension((int) minSize.getWidth() + addedWidth, (int) minSize.getHeight());
        }

        updatePanelBtn();

        if (mode == Mode.RUNTIME) {
            pn = PROPS.getChild("pov");
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            setEditable(pv.isNull() ? true : !pv.booleanValue());

            pv = getPropertyValue(pn.getChild("tabIndex"));
            tabIndex = pv.intValue();

            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = (byte[]) p.second;
            }

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

    public String getCopyRefPath() {
        return copyRefPath;
    }

    public String getCopyTitleUID() {
        return copyTitleUID;
    }

    public CopyButton getCopyBtn() {
        return copyBtn;
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public boolean isDeleteOnType() {
        return deleteOnType;
    }

    
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    private void valueChanged() {
        try {
            Object value = getValue();
            adapter.changeValue(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public OrCellEditor getCellEditor() {
        if (editor == null) {
            editor = new OrDateCellEditor();
            addActionListener(editor);
            addFocusListener(editor);
            addKeyListener(editor);
            setBorder(BorderFactory.createEmptyBorder());
        }
        return editor;
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

    /**
     * Обновление размеров панели с кнопками
     * Размер устанавливается в соответствии с тем, какие кнопки нужно отображать на ней
     */
    public void updatePanelBtn() {
        int width = 0;
        if (showCalendar) {
            width += 20;
        }
        if (showCopy) {
            width += 20;
        }
        panelBtn.setPreferredSize(new Dimension(width, 20));
        panelBtn.setVisible(showCalendar || showCopy);
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
    
    class OrDateCellEditor extends OrCellEditor implements FocusListener, KeyListener {

        public Object getCellEditorValue() {
            return (getValue() != null) ? FORMAT_.format(getValue()) : null;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            adapter.valueChanged(new OrRefEvent(adapter.getDataRef(), -1, -1, null));
            if (isDeleteOnType()) {
                setSelectionStart(0);
                setSelectionEnd(getText().length());
                getCaret().setSelectionVisible(true);
            } else {
                getCaret().setVisible(true);
            }
            return OrDateField.this;
        }

        public Object getValueFor(Object obj) {
            Object date = ((OrRef.Item) obj).getCurrent();

            return toText(date);
        }

        public boolean stopCellEditing() {
            if (getParent() != null) {
                try {
                    adapter.changeValue(getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return super.stopCellEditing();
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            if (!(e.getOppositeComponent() instanceof CalendarButton))
                stopCellEditing();
        }

        public void keyTyped(KeyEvent e) {
            if (getParent() != null && e.getKeyCode() == KeyEvent.VK_TAB) {
                getParent().dispatchEvent(e);
            }
        }

        public void keyPressed(KeyEvent e) {
            keyTyped(e);
        }

        public void keyReleased(KeyEvent e) {
            keyTyped(e);
        }
    }

    private class CopyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            OrRef copyRef = adapter.getCopyRef();
            if (copyRef != null) {
                try {
                    OrRef.Item item = copyRef.getItem(adapter.getLangId());
                    Object value = (item != null) ? item.getCurrent() : null;
                    adapter.changeValue(value);
                    setValue(value);
                    if (adapter.isEditor()) {
                        editor.stopCellEditing();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
