package kz.tamur.comps;

import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.processBorderProperties;
import static kz.tamur.comps.Utils.processStdCompProperties;
import kz.tamur.comps.models.NotePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrButtonUI;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.guidesigner.noteeditor.NoteBrowser;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;

/**
 * The Class OrNote.
 * 
 */
public class OrNote extends OrTransparentButton implements OrGuiComponent, ActionListener {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new NotePropertyRoot();
    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrFrame frame;
    private String title;
    private String titleUID;
    private NoteBrowser popup;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String descriptionUID;
    private byte[] description;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private KrnObject krnObj;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private int posIcon;
    private boolean showOnTopPan;
    private int positionOnTopPan;
    private String base64Icon;
    private Color fontColor;
    private String varName;
    private MouseAdapter actL;

    /**
     * Конструктор класса OrNote.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param frame
     *            the frame
     */
    OrNote(Element xml, int mode, OrFrame frame) {
        super("OrNote");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(true);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);

        actL = new MouseAdapter() {
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

        if (mode == Mode.RUNTIME) {
            addActionListener(this);
            setFocusable(true);
            kz.tamur.rt.Utils.setComponentTabFocusCircle(this);
            addFocusListener(new DefaultFocusAdapter(this));
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

        updateProperties();
    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
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
            Color val = value.isNull() ? Color.WHITE : value.colorValue();
            setBackground(val);
        } else if ("enabled".equals(name)) {
            setEnabled(value.booleanValue());
        } else if ("image".equals(name)) {
            if (value.isNull()) {
                ImageIcon icon = kz.tamur.rt.Utils.getImageIconFull("noteNew.png");
                setIcon(icon);
                setWebNameIcon("noteNew.png");
                base64Icon = null;
            } else {
                byte[] b = value.getImageValue();
                ImageIcon icon = kz.tamur.rt.Utils.processCreateImage(b);
                setIcon(icon);
                if (b != null && b.length > 0) {
                    base64Icon = new String(Base64.encode(b));
                }
            }
        } else if ("alignmentText".equals(name)) {
            setHorizontalAlignment(value.intValue());
            PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                setText(frame.getString((String) p.first));
            }
        } else if ("opaque".equals(name)) {
            setTransparent(!value.booleanValue());
            setOpaque(value.booleanValue());
            repaint();
            if (!value.booleanValue()) {
                addMouseListener(actL);
            } else {
                removeMouseListener(actL);
            }
        } else if ("anchorImage".equals(prop.getName())) {
            posIcon = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage")).intValue();
            setIcon(getIcon());
        } else if ("positionOnTopPan".equals(prop.getName())) {
            positionOnTopPan = value.intValue();
        } else if ("showOnTopPan".equals(prop.getName())) {
            showOnTopPan = value.booleanValue();
        }
    }

    
    @Override
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    
    @Override
    public void setLangId(long langId) {
        popup = null;
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
        }
        processBorderProperties(this, frame);
    }

    /**
     * Обновление свойств компонента.
     */
    private void updateProperties() {
        processBorderProperties(this, frame);
        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
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
        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            posIcon = pv.intValue();
        } else {
            posIcon = GridBagConstraints.WEST;
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        }
        pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (pv.isNull()) {
            setHorizontalAlignment(((Integer) pn.getChild("alignmentText").getDefaultValue()).intValue());
        } else {
            setHorizontalAlignment(pv.intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            setIcon(kz.tamur.rt.Utils.processCreateImage(pv.getImageValue()));
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                StringBuilder name = new StringBuilder();
                name.append("foto");
                kz.tamur.rt.Utils.getHash(b, name);
                name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                base64Icon = new String(Base64.encode(b)); // FIXME убрать!
            }
        } else {
            ImageIcon icon = kz.tamur.rt.Utils.getImageIconFull("noteNew.png");
            setIcon(icon);
            setWebNameIcon("noteNew.png");
            base64Icon = null;
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
                addMouseListener(actL);
            } else {
                removeMouseListener(actL);
            }
        } else {
            setTransparent(false);
            setOpaque(true);
            repaint();
            setPropertyValue(new PropertyValue(true, pn.getChild("opaque")));
        }

        pv = getPropertyValue(getProperties().getChild("pov").getChild("spravInterface"));
        if (!pv.isNull()) {
            try {
                Kernel krn = Kernel.instance();
                String objId = pv.getKrnObjectId();
                if (!"".equals(objId)) {
                    krnObj = new KrnObject(Long.parseLong(objId), "", krn.getClassByName("Note").id);
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получить base64 icon.
     * 
     * @return base64 icon
     */
    public String getBase64Icon() {
        return base64Icon;
    }

    
    @Override
    public int getMode() {
        return mode;
    }

    /**
     * Action performed.
     * 
     * @param e
     *            the e
     */
    public void actionPerformed(ActionEvent e) {
        long langId = frame.getInterfaceLang().id;
        if (krnObj != null) {
            if (popup == null) {
                popup = new NoteBrowser(krnObj, false, langId);
                popup.init();
            }
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(popup.getNavigator(), BorderLayout.NORTH);
            panel.add(popup, BorderLayout.CENTER);
            popup.setDividerLocation(200);
            Component comp = getTopLevelAncestor();
            Container dlg = null;
            if (comp instanceof Frame) {
                dlg = new JFrame(popup.getTitle());
                ((JFrame) dlg).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ((JFrame) dlg).addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        OrNote.this.setEnabled(true);
                        super.windowClosing(e);
                    }
                });
                ((JFrame) dlg).setIconImage(kz.tamur.rt.Utils.getImageIcon("icon").getImage());
                ((JFrame) dlg).setExtendedState(((JFrame) dlg).getExtendedState() | JFrame.MAXIMIZED_BOTH);
                ((JFrame) dlg).getContentPane().add(panel);
                dlg.setSize(kz.tamur.rt.Utils.getScreenSize((JFrame) dlg));
            } else if (comp instanceof Dialog) {
                dlg = new JDialog((Dialog) comp, popup.getTitle(), false);
                ((JDialog) dlg).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ((JDialog) dlg).addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        OrNote.this.setEnabled(true);
                        super.windowClosing(e);
                    }
                });
                ((JDialog) dlg).getContentPane().add(panel);
                dlg.setSize(kz.tamur.rt.Utils.getScreenSize((JDialog) dlg));
            }
            dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
            this.setEnabled(false);
            dlg.setVisible(true);
        }
    }

    
    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    
    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
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

    
    @Override
    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    
    @Override
    public ComponentAdapter getAdapter() {
        // не используется
        return null;
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
     * Возвращает позицию иконки.
     * 
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
    }

    
    public String getVarName() {
        return varName;
    }

    /**
     * Update tool tip.
     */
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
    public void setListListeners(java.util.List<OrGuiComponent> listForAdd, java.util.List<OrGuiComponent> listForDel) {
        for (int i = 0; i < listForDel.size(); i++) {
            listListeners.remove(listForDel.get(i));
        }
        for (int i = 0; i < listForAdd.size(); i++) {
            listListeners.add(i, listForAdd.get(i));
        }
    }

    
    @Override
    public OrButtonUI getUI() {
        return (OrButtonUI) super.getUI();
    }

    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    
    @Override
    public String getToolTip() {
        // не используется
        return null;
    }

    
    @Override
    public void updateDynProp() {
        // не используется
    }

    
    @Override
    public int getPositionOnTopPan() {
        return positionOnTopPan;
    }

    
    @Override
    public boolean isShowOnTopPan() {
        return showOnTopPan;
    }

    
    @Override
    public void setAttention(boolean attention) {
        // не используется
    }
}
