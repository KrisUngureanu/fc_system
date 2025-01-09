package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SequenceFieldPropertyRoot;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class OrSequenceField extends JPanel implements OrGuiComponent, MouseTarget {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new SequenceFieldPropertyRoot();
    private MouseDelegator delegator = new MouseDelegator(this);
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

    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    private JTextField textField = kz.tamur.rt.Utils.createDesignerTextField();
    public JButton nextBtn = ButtonsFactory.createToolButton("NextSeq", "Следующий", true);
    public JButton skippedBtn = ButtonsFactory.createToolButton("SkipSeq", "Выбрать из пропущенных", true);
    public JButton clearBtn = ButtonsFactory.createToolButton("ClearSeq", "Очистить", true);

    private int tabIndex;

    //Sequence properties field
    private boolean strikes;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private String descriptionUID;
	private String varName;


    OrSequenceField(Element xml, int mode, OrFrame frame) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        init();
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
        
        updateProperties();
        //if (this.mode == Mode.DESIGN) {
            textField.setEditable(false);
        //} else {
         //   textField.setEnabled(false);
        //}
    }
    
    private void init() {
        PropertyValue pv = getPropertyValue(
                PROPS.getChild("pov").getChild("sequences").getChild("strikes"));
        strikes = (!pv.isNull()) ? pv.booleanValue() : false;
        setLayout(new GridBagLayout());
        MouseListener btnMouseAdapter = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                JButton src = (JButton)e.getSource();
                src.setCursor(Constants.HAND_CURSOR);
            }
        };

        textField.setMaximumSize(new Dimension(120,18));
        textField.setMinimumSize(new Dimension(120,18));
        textField.setPreferredSize(new Dimension(120,18));

        nextBtn.addMouseListener(btnMouseAdapter);
        skippedBtn.addMouseListener(btnMouseAdapter);
        clearBtn.addMouseListener(btnMouseAdapter);

        nextBtn.setMaximumSize(new Dimension(18,18));
        nextBtn.setMinimumSize(new Dimension(18,18));
        nextBtn.setPreferredSize(new Dimension(18,18));

        skippedBtn.setMaximumSize(new Dimension(18,18));
        skippedBtn.setMinimumSize(new Dimension(18,18));
        skippedBtn.setPreferredSize(new Dimension(18,18));

        clearBtn.setMaximumSize(new Dimension(18,18));
        clearBtn.setMinimumSize(new Dimension(18,18));
        clearBtn.setPreferredSize(new Dimension(18,18));


        add(textField, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(textField, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(nextBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 0), 0, 0));
        add(skippedBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 0), 0, 0));
        add(clearBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 0), 0, 0));
        if (mode == Mode.DESIGN) {
            textField.addMouseListener(delegator);
            textField.addMouseMotionListener(delegator);
            nextBtn.addMouseListener(delegator);
            nextBtn.addMouseMotionListener(delegator);
            nextBtn.setEnabled(false);
            skippedBtn.addMouseListener(delegator);
            skippedBtn.addMouseMotionListener(delegator);
            skippedBtn.setEnabled(false);
            clearBtn.addMouseListener(delegator);
            clearBtn.addMouseMotionListener(delegator);
            clearBtn.setEnabled(false);
        }
    }

    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }


    public void paint(Graphics g) {
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
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        String name = prop.getName();
        if ("fontG".equals(name)) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(name)) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("backgroundColor".equals(name)) {
            Color val = value.isNull() ? Color.WHITE : value.colorValue();
            setBackground(val);
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
    }

    private void updateProperties() {
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            textField.setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            textField.setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            textField.setBackground(pv.colorValue());
        }
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }
        pn = PROPS.getChild("pos").getChild("pref");
        pv = getPropertyValue(pn.getChild("width"));
        int width = -1;
        if (!pv.isNull()) {
            width = pv.intValue();
        }
        pv = getPropertyValue(pn.getChild("height"));
        int height = -1;
        if (!pv.isNull()) {
            height = pv.intValue();
        }
        if (width != -1 && height != -1) {
            setPreferredSize(new Dimension(width, height));
            validate();
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
        //Utils.processBorderProperties(this, langId);
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

    public void setValue(String value) {
        textField.setText(value);
    }

    public String getValue() {
        return Funcs.normalizeInput(textField.getText());
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        String val = Funcs.normalizeInput(textField.getText());
        if (strikes) {
            nextBtn.setEnabled(isEnabled);
        } else if (isEnabled && (
                val == null || val.equals("") || val.length() == 0)) {
            nextBtn.setEnabled(isEnabled);
        }
        skippedBtn.setEnabled(isEnabled);
        clearBtn.setEnabled(isEnabled);
    }

    public boolean isStrikes() {
        return strikes;
    }

    public void addActionListener(ActionListener al) {
        nextBtn.addActionListener(al);
        skippedBtn.addActionListener(al);
        clearBtn.addActionListener(al);
    }

    public void addDocumentListener(DocumentListener dl) {
        textField.getDocument().addDocumentListener(dl);
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
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
}
