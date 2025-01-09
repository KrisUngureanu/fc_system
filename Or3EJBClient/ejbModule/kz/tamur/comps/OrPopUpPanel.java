package kz.tamur.comps;

import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.getExpReturn;
import static kz.tamur.comps.Utils.processBorderProperties;
import static kz.tamur.comps.Utils.processStdCompProperties;
import static kz.tamur.rt.adapters.Util.showErrorMessage;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import kz.tamur.comps.models.PopUpPanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.comps.ui.toolbar.OrToolBar;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.PopUpPanelAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;

/**
 * The Class OrPopUpPanel.
 * 
 * @author Lebedev Sergey
 */
public class OrPopUpPanel extends OrTransparentButton implements OrGuiComponent {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    /** uuid. */
    protected String UUID;

    /** Свойства компонента. */
    private PropertyNode PROPS = new PopUpPanelPropertyRoot();
    private static final PropertyNode FULL_PROPS = new PopUpPanelPropertyRoot();

    /** Адаптер . */
    private PopUpPanelAdapter adapter;
    /** Режим выполнения. */
    private int mode;

    /** The xml. */
    private Element xml;

    /** The frame. */
    private OrFrame frame;

    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;

    /** pref size. */
    private Dimension prefSize;

    /** max size. */
    private Dimension maxSize;

    /** min size. */
    private Dimension minSize;

    /** constraints. */
    private GridBagConstraints constraints;

    /** parent. */
    private OrGuiContainer parent;

    /** title. */
    private String title;

    /** title uid. */
    private String titleUID;

    /** description uid. */
    private String descriptionUID;

    /** description. */
    private byte[] description;

    /** is default button. */
    private boolean isDefaultButton;

    /** tab index. */
    private int tabIndex;

    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate;

    /** var name. */
    private String varName;

    /** is selected. */
    protected boolean isSelected;

    /** is copy. */
    private boolean isCopy;

    /** standart border. */
    private Border standartBorder;

    /** copy border. */
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());

    /** is help click. */
    private boolean isHelpClick = false;
    /** Цвет фрифта для заголовка кнопки. */
    private Color fontColor;
    /** Доп панель. */
    private JPanel popUpPane = new JPanel();

    /** Панель, на которой реализована всплывающая панель в исполнении. */
    private JPopupMenu popUp = new JPopupMenu() {
        public void setVisible(boolean b) {
            if (b) {
                evaluate(beforeOpenTemplate, 0);
            } else {
                evaluate(beforeCloseTemplate, 2);
            }
            if (hide || b || hideAfterClick) {
                hide = false;
                super.setVisible(b);
            }
            if (b) {
                evaluate(afterOpenTemplate, 1);
            } else {
                evaluate(afterCloseTemplate, 3);
            }
        }
    };

    /** Панель на которой реализован редактор панели в конструкторе. */
    private OrToolBar editor = new OrToolBar(JToolBar.VERTICAL, true);

    /** Кнопка для вызова редактора всплывающей панели в конструкторе. */
    private JButton editBtn = ButtonsFactory.createToolButton("interfaceNode.png", "Редактор панели", true);

    /** Панель для размещения всплывающих компонентов. */
    private OrPanel mainPanel;
    private OrTransparentButton close = new OrTransparentButton();
    // private JButton close = new JButton();

    /** Слушатель скрытия всплывающей панели. */
    private ActionListener actL;
    private MouseAdapter actL2;
    
    private String webNameIcon = null;
    private boolean hideAfterClick = true;
    private boolean hide;
    private boolean showOnTopPan;
    private int positionOnTopPan;
    private boolean isShowAsMenu=false;
    private String toolTipContent = null;
    
    /**
     * Конструктор класса.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param fm
     *            the fm
     * @param frame
     *            the frame
     * @throws KrnException
     *             the krn exception
     */
    OrPopUpPanel(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        init(fm);
    }

    /**
     * Инициализация панели.
     * 
     * @param fm
     *            the fm
     * @param mode
     *            the mode
     * @throws KrnException
     *             the krn exception
     */
    private void init(Factory fm) throws KrnException {
        actL2 = new MouseAdapter() {
            private void mouse(boolean isEnter) {
                if (isEnter) {
                    setForeground(Color.blue);
                    setCursor(Constants.HAND_CURSOR);
                } else {
                    setForeground(fontColor == null ? Color.black : fontColor);
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }

            public void mouseExited(MouseEvent e) {
                mouse(false);
            }

            public void mouseEntered(MouseEvent e) {
                mouse(true);
            }
        };
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        if (mode == RUNTIME) {
            close.setIcon(kz.tamur.rt.Utils.getImageIconFull("DeleteIcon.png"));
            actL = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hidePopUp();
                }
            };
            Utils.setAllSize(close, new Dimension(12, 12));
            close.addActionListener(actL);

            adapter = new PopUpPanelAdapter(frame, this, false);
            // всплывающая подсказка
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = getExpReturn(toolTipExpr, frame, getAdapter());
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
        
        if (mode == DESIGN) {
            setEnabled(false);
            PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
            	if (pv.objectValue() instanceof Expression) {
            		try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = getExpReturn(toolTipExpr, frame, getAdapter());
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
	                            setToolTipText(toolTipExprText.trim());
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

        Element panel;

        PropertyValue pv = getPropertyValue(PROPS.getChild("panel"));
        if (!pv.isNull()) {
            List children = pv.elementValue().getChildren();
            panel = (Element) children.get(0);
        } else {
            panel = new Element("Component");
            panel.setAttribute("class", "Panel");
        }

        updateProperties();
        mainPanel = (OrPanel) fm.create(panel, mode, frame);
        mainPanel.setGuiParent(this.getGuiParent());

        mainPanel.setDelete(false);
        mainPanel.setGuiParent(getGuiParent());
        if (pv.isNull()) {
            PropertyHelper.addProperty(new PropertyValue(mainPanel.getXml(), PROPS.getChild("panel")), xml);
        }
        if (hideAfterClick) {
            popUpPane.setLayout(new CardLayout());
            popUpPane.add(mainPanel, "popUpPane");
        } else {
            popUpPane.setLayout(new GridBagLayout());
            popUpPane.add(close, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(1, 3, 2, 0), 0, 0));
            popUpPane.add(mainPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        addListenersChildren(mainPanel);
        if (mode == Mode.DESIGN) {
            setEnabled(false);
            setLayout(new BorderLayout());
            editor.setVisible(false);
            editor.setOpaque(true);
            popUpPane.setOpaque(true);
            editor.add(popUpPane);
            add(editor, BorderLayout.WEST);
            add(editBtn, BorderLayout.EAST);

            editBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showEditor(false);
                }
            });
        } else {
            popUp.add(popUpPane);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    @Override
    public Element getXml() {
        return xml;
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
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

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        processStdCompProperties(this, value);
        processBorderProperties(this, frame);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("title".equals(name)) {
            Pair p = value.resourceStringValue();
            setText((String) p.second);
        } else if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            fontColor = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(fontColor);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setBackground(val);
        } else if ("enabled".equals(name)) {
            setEnabled(value.booleanValue());
        } else if ("image".equals(name)) {
            if (value == null || value.isNull()) {
                setIcon(null);
                webNameIcon = null;
            } else {
                byte[] v = value.getImageValue();
                setIcon(Utils.processCreateImage(v));
                if (v != null && v.length > 0) {
                    StringBuilder name_ = new StringBuilder();
                    name_.append("ico");
                    kz.tamur.rt.Utils.getHash(v, name_);
                    name_.append(".").append(kz.tamur.rt.Utils.getSignature(v));
                    webNameIcon = name_.toString();
                }
            }
        } else if ("alignmentText".equals(name)) {
            setHorizontalAlignment(value.intValue());
            PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                setText(frame.getString((String) p.first));
            }
        } else if ("positionOnTopPan".equals(name)) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(name)) {
            showOnTopPan = value.booleanValue();
        } else if ("opaque".equals(name)) {
            setTransparent(!value.booleanValue());
            setOpaque(value.booleanValue());
            repaint();
            if (!value.booleanValue()) {
                addMouseListener(actL2);
            }else {
                removeMouseListener(actL2);
            }
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

    @Override
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    @Override
    public void setLangId(long langId) {
        if (mode == RUNTIME) {
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
            mainPanel.setLangId(langId);
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
            if (!pv.isNull()) {
                setText(pv.stringValue());
            }
            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        processBorderProperties(this, frame);
    }

    /**
     * Обновление свойство компонента
     */
    private void updateProperties() {
        updateDynProp();
        processBorderProperties(this, frame);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        PropertyValue pv = null;
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        } else {
            setHorizontalAlignment(((Integer) pn.getChild("alignmentText").getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            setIcon(Utils.processCreateImage(pv.getImageValue()));
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("ico");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                webNameIcon = name.toString();
            }
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
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setTransparent(!pv.booleanValue());
            setOpaque(pv.booleanValue());
            repaint();
            if (!pv.booleanValue()) {
                addMouseListener(actL2);
            }else {
                removeMouseListener(actL2);
            }
        } else {
            setTransparent(false);
            setOpaque(true);
            repaint();
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
        if (mode == RUNTIME) {
            pv = getPropertyValue(pn.getChild("beforeOpen"));
            if (pv != null) {
                String expr = null;
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    beforeOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            pv = getPropertyValue(pn.getChild("afterOpen"));
            if (pv != null) {
                String expr = null;
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            pv = getPropertyValue(pn.getChild("beforeClose"));
            if (pv != null) {
                String expr = null;
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    beforeCloseTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            pv = getPropertyValue(pn.getChild("afterClose"));
            if (pv != null) {
                String expr = null;
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterCloseTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        }

        pv = getPropertyValue(pn.getChild("defaultButton"));
        if (!pv.isNull()) {
            isDefaultButton = pv.booleanValue();
        }
        pv = getPropertyValue(pn.getChild("hideAfterClick"));
        if (!pv.isNull()) {
            hideAfterClick = pv.booleanValue();
        }
        pv = getPropertyValue(pn.getChild("isShowAsMenu"));
        if (!pv.isNull()) {
        	isShowAsMenu = pv.booleanValue();
        }
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return parent;
    }

    @Override
    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    @Override
    public void setXml(Element xml) {
        this.xml = xml;
    }

    @Override
    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    @Override
    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    /**
     * Получить tab index.
     * 
     * @return tab index
     */
    public int getTabIndex() {
        return tabIndex;
    }

    @Override
    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    /**
     * Проверяет, является ли help click.
     * 
     * @return <code>true</code>, если help click
     */
    public boolean isHelpClick() {
        return isHelpClick;
    }

    /**
     * Установить help click.
     * 
     * @param helpClick
     *            новое значение help click
     */
    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    @Override
    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    public void setAdapter(ComponentAdapter adapter) {
    }

    @Override
    public boolean isDefaultButton() {
        return isDefaultButton;
    }

    @Override
    public String getVarName() {
        return varName;
    }

    /**
     * Обновление всплывающей подсказки.
     */
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = getExpReturn(toolTipExpr, frame, getAdapter());
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
    public void setText(String text) {
        super.setText(Utils.castToHTML(text, this));
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    /**
     * Получить заголовок.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Установить заголовок.
     * 
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Получить pop up.
     * 
     * @return the popUp
     */
    public JPanel getPopUp() {
        return popUpPane;
    }

    /**
     * Показать всплвающую панель.
     * Для режима исполнения.
     */
    public void showPopUp() {
        Point location = MouseInfo.getPointerInfo().getLocation();
        // преобразование координат
        SwingUtilities.convertPointFromScreen(location, this);
        Point loc = new Point(location);
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        popUp.show(this, x, y);
    }

    public void showEditor(boolean loc) {
        if (!editor.getUI().isFloating() && !editor.isVisible() && isShowing()) {
            popUpPane.setPreferredSize(mainPanel.getPreferredSize());
            popUpPane.setMinimumSize(mainPanel.getMinimumSize());
            popUpPane.setMaximumSize(mainPanel.getMaximumSize());
            editor.setSize(mainPanel.getPreferredSize());
            editor.setVisible(true);
            Point location;
            location = loc ? getLocationOnScreen() : MouseInfo.getPointerInfo().getLocation();
            editor.getUI().setFloatingLocation((int) location.getX(), (int) location.getY());
            editor.getUI().setFloating(true, location);
            ((JDialog) editor.getUI().getFloatingToolBar()).addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent w) {
                    editor.setVisible(false);
                }
            });
        }
    }

    /**
     * Скрыть всплывающую панель.
     * Для режима исполнения.
     */
    public void hidePopUp() {
        hide = true;
        popUp.setVisible(false);
    }

    /**
     * Получить main panel.
     * 
     * @return the mainPanel
     */
    public OrPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Adds the listeners children.
     * 
     * @param root
     *            the root
     */
    private void addListenersChildren(Component root) {
        if (root instanceof OrGuiComponent) {
            OrGuiComponent comp = (OrGuiComponent) root;
            comp.setComponentChange(this);
        }
        Component[] comps = ((JComponent) root).getComponents();
        if (comps != null) {
            for (Component comp : comps) {
                if (comp != null && comp instanceof JComponent) {
                    addListenersChildren(comp);
                }
            }
        }
        if (hideAfterClick && mode == RUNTIME) {
            if (root instanceof OrButton) {
                ((OrButton) root).addActionListener(actL);
            } else if (root instanceof OrPopUpPanel) {
                ((OrPopUpPanel) root).addActionListener(actL);
            } else if (root instanceof OrDocField) {
                ((OrDocField) root).addActionListener(actL);
            } else if (root instanceof OrHyperLabel) {
                ((OrHyperLabel) root).addActionListener(actL);
            } else if (root instanceof OrHyperPopup) {
                ((OrHyperPopup) root).addActionListener(actL);
            }
        }
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }

    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners, java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
        mainPanel.setListListeners(listListeners, listForDel);
    }

    public String getWebNameIcon() {
        return webNameIcon;
    }

    /**
     * @param webNameIcon
     *            the webNameIcon to set
     */
    public void setWebNameIcon(String webNameIcon) {
        this.webNameIcon = webNameIcon;
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    /**
     * @return the hideAfterClick
     */
    public boolean isHideAfterClick() {
        return hideAfterClick;
    }

    public ASTStart getBeforeOpenTemplate() {
        return beforeOpenTemplate;
    }

    public ASTStart getAfterOpenTemplate() {
        return afterOpenTemplate;
    }

    public ASTStart getBeforeCloseTemplate() {
        return beforeCloseTemplate;
    }

    public ASTStart getAfterCloseTemplate() {
        return afterCloseTemplate;
    }

    private void evaluate(ASTStart template, int type) {
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang((UIFrame) frame);
            Map vc = new HashMap();
            try {
                orlang.evaluate(template, vc, ((UIFrame) frame).getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                switch (type) {
                case 0:
                    showErrorMessage(this, ex.getMessage(), "Действие перед открытием");
                case 1:
                    showErrorMessage(this, ex.getMessage(), "Действие после открытия");
                case 2:
                    showErrorMessage(this, ex.getMessage(), "Действие перед закрытием");
                case 3:
                    showErrorMessage(this, ex.getMessage(), "Действие после закрытия");
                }
            }
        }
    }
    
    @Override
    public String getToolTip() {
    	return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
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
    
    /**
     * Проверяет, является ли панель подпанелью аналогичной панели
     * @return
     */
    public boolean isSubPanel() {
        for (OrGuiComponent listener : listListeners) {
            if (listener instanceof OrPopUpPanel) {
                return true;
            }
        }
        return false;
    }
}
