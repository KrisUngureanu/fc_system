package kz.tamur.comps;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SplitPanePropertyRoot;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;
import kz.tamur.util.DescriptionSupport;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class OrSplitPane extends JSplitPane implements OrGuiContainer, MouseTarget, DescriptionSupport {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private static PropertyNode PROPS = new SplitPanePropertyRoot();
    private static PropertyNode LEFT = PROPS.getChild("left");
    private static PropertyNode RIGHT = PROPS.getChild("right");

    private boolean isShown = false;

    private int mode;
    private boolean isSelected;
    private Element xml;
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

    private EventListenerList listeners = new EventListenerList();
    private OrFrame frame;
    private int orientation;
    private double divLocation;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;

    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private String descriptionUID;
	private String varName;
	    /** Начальный цвет градиента. */
	    private Color startColor;

	    /** конечный цвет градиента. */
	    private Color endColor;

	    /** Ориентация градиента. */
	    private int orientationGr = 0;

	    /** Цикличность градиента. */
	    private boolean isCycle = true;

	    /** позиция отсчёта градиента для начального цвета. */
	    private int positionStartColor = 0;

	    /** позиция отсчёта градиента для конечного цвета. */
	    private int positionEndColor = 50;
	    private boolean isEnableGradient = true;
	    private boolean isFoundGradient = true;
	    
    OrSplitPane(Element xml, int mode, Factory cf, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        setFocusable(true);
        loadChildren(cf);
        MouseListener mouseDelegator = new MouseDelegator(this);
        Component[] cs = getComponents();
        for (int i = 0; i < cs.length; i++) {
            Component c = cs[i];
            if (!(c instanceof OrGuiComponent)) {
                c.addMouseListener(mouseDelegator);
            }
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        //description = PropertyHelper.getDescription(this);
        PropertyNode prop = PROPS.getChild("title");
        PropertyValue pv = getPropertyValue(prop);
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String)p.first;
            title = frame.getString(titleUID);
        }
        
        if (mode == Mode.RUNTIME) {
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
        /*
         * добавление слушателя, который будет перерисовывать родителя компонента если компонент прозрачен
         * необходимо для удаления артефактов прорисовки при изменении размеров прозрачных компонентов
         */

        addComponentListener(new ComponentListener() {

            public void componentShown(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                if (isOpaque() && getTopLevelAncestor() != null) {
                    getTopLevelAncestor().repaint();
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
        updateProperties();
    }

    public void paint(Graphics g) {
        if (!isShown) {
            setOrientation(orientation);
            setDividerLocation(divLocation);
            setResizeWeight(divLocation);
            isShown = true;
        }
        super.paint(g);
    }

    private void loadChildren(Factory cf) throws KrnException {
        PropertyValue pv = getPropertyValue(LEFT);
        List children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            OrGuiComponent c = cf.create(e, mode, frame);
            c.setGuiParent(this);
            setLeftComponent((Component)c);
        } else {
            setLeftComponent(new EmptyPlace());
        }
        pv = getPropertyValue(RIGHT);
        children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            OrGuiComponent c = cf.create(e, mode, frame);
            c.setGuiParent(this);
            setRightComponent((Component)c);
        } else {
            setRightComponent(new EmptyPlace());
        }
    }

    public boolean canAddComponent(int x, int y) {
        Component c = getComponentAt(x, y);
        if (c instanceof EmptyPlace) {
            return true;
        } else {
            return false;
        }
    }

    public void addComponent(OrGuiComponent c, int x, int y) {
        // Выделить слушателей на удаление
        java.util.List<OrGuiComponent> copyList = new ArrayList<OrGuiComponent>(c.getListListeners());
        // Добавить слушателей родителя в добавляемый компонент
        c.setListListeners(listListeners, copyList);
        Component place = getComponentAt(x, y);
        if (getLeftComponent() == place) {
            setLeftComponent((Component)c);
            setPropertyValue(new PropertyValue(c.getXml(), LEFT));
        } else if (getRightComponent() == place) {
            setRightComponent((Component)c);
            setPropertyValue(new PropertyValue(c.getXml(), RIGHT));
        }
        validate();
        repaint();
    }

    public void removeComponent(OrGuiComponent c) {
        remove((Component) c);
        if (getLeftComponent() == c && c.getXml() != null) {
            PropertyHelper.removeProperty(new PropertyValue(c.getXml(), LEFT), xml);
        } else if (getRightComponent() == c && c.getXml() != null) {
            PropertyHelper.removeProperty(new PropertyValue(c.getXml(), RIGHT), xml);
        }
        revalidate();
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.translatePoint(c.getX(), c.getY());
        e.setSource(this);
        processMouseEvent(e);
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
        updateProperties();
        final String name = value.getProperty().getName();
        PropertyNode pn;
        PropertyValue pv;
        if ("title".equals(name)) {
            firePropertyModified();
        } else if ("gradient".equals(name)) {
            // если градиентная заливка отключена, необходимо перерисовать компоеннт основным его цветом
            pv = getPropertyValue(getProperties().getChild("extended").getChild("gradient"));
            if (pv.isNull()) {
                pn = getProperties().getChild("view").getChild("background");
                pv = getPropertyValue(pn.getChild("backgroundColor"));
                setBackground(pv.isNull() ? (Color) pn.getChild("backgroundColor").getDefaultValue() : pv.colorValue());
                repaintAll();
            }
        } else if ("transparent".equals(name)) {
            // перерисовка
            repaintAll();
        }
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUID);
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
        Component c = getLeftComponent();
        if (c instanceof OrGuiComponent) {
            ((OrGuiComponent)c).setLangId(langId);
        }
        c = getRightComponent();
        if (c instanceof OrGuiComponent) {
            ((OrGuiComponent)c).setLangId(langId);
        }
    }

    public int getMode() {
        return mode;
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyNode orient = pn.getChild("orientation");
        PropertyValue pv = getPropertyValue(orient);
        if (!pv.isNull()) {
            switch(pv.intValue()) {
                case Constants.HORIZONTAL:
                    setOrientation(HORIZONTAL_SPLIT);
                    orientation = HORIZONTAL_SPLIT;
                    break;
                case Constants.VERTICAL:
                    setOrientation(VERTICAL_SPLIT);
                    orientation = VERTICAL_SPLIT;
                    break;
                default:
                    setOrientation(HORIZONTAL_SPLIT);
                    orientation = HORIZONTAL_SPLIT;
            }
        } else {
            setPropertyValue(new PropertyValue(Constants.HORIZONTAL, orient));
            setOrientation(HORIZONTAL_SPLIT);
            orientation = HORIZONTAL_SPLIT;
        }
        PropertyNode dividerLocation = pn.getChild("dividerLocation");
        pv = getPropertyValue(dividerLocation);
        if (!pv.isNull()) {
            double val = pv.doubleValue();
            if (val > 1 || val < 0) {
                String mess = "Недопустимое значение " + val + "!\n" +
                        "(Диапозон от 0 до 1)";
                MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                        MessagesFactory.ERROR_MESSAGE, mess);
                setPropertyValue(new PropertyValue(
                        ((Double)dividerLocation.getDefaultValue()).doubleValue(),
                        dividerLocation));
                setDividerLocation(
                        ((Double)dividerLocation.getDefaultValue()).doubleValue());
                divLocation = ((Double)dividerLocation.getDefaultValue()).doubleValue();
            } else {
                setDividerLocation(pv.doubleValue());
                divLocation = pv.doubleValue();
                repaint();
            }
        } else {
            setDividerLocation(((Double)dividerLocation.getDefaultValue()).doubleValue());
            divLocation = ((Double)dividerLocation.getDefaultValue()).doubleValue();
        }
        setResizeWeight(divLocation);
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
        
        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("gradient"));
        if (!pv.isNull()) {
            isFoundGradient = true;
            // градиентная заливка компонента
            setGradient((GradientColor)pv.objectValue());
        }else {
            isFoundGradient = false;
        }
        pv = getPropertyValue(pn.getChild("transparent"));
        if (!pv.isNull()) {
            // прозрачность компонента(да/нет)
            setOpaque(!pv.booleanValue());
        }
        repaintAll();
    }

    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener)list[i]).propertyModified(this);
        }
    }

    public String getTitle() {
        return title;
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

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component comp = getLeftComponent();
        if (comp != null && comp instanceof OrGuiComponent) {
            comp.setEnabled(enabled);
        }
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}

    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
        
        Component c = getLeftComponent();
		if (c instanceof OrGuiContainer) {
			OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
			if (cc != null) return cc; 
		} else if (c instanceof OrGuiComponent) {
			OrGuiComponent gc = (OrGuiComponent)c;
			if (title.equals(gc.getVarName())) return gc;
		}
		
        c = getRightComponent();
		if (c instanceof OrGuiContainer) {
			OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
			if (cc != null) return cc; 
		} else if (c instanceof OrGuiComponent) {
			OrGuiComponent gc = (OrGuiComponent)c;
			if (title.equals(gc.getVarName())) return gc;
		}
    	return null;
    }

	public String getVarName() {
		return varName;
	}
	
	       
        
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // если для компонента НЕ установлена градиентная заливка
            if (!isEnableGradient ||!isFoundGradient) {
                return;
            }
            
            // защита он неверных параметров
            if (startColor == null && endColor == null) {
                startColor = Color.WHITE;
                endColor = kz.tamur.rt.Utils.getLightGraySysColor();
            } else if (startColor == null) {
                setBackground(endColor);
                return;
            } else if (endColor == null) {
                setBackground(startColor);
                return;
            }

            // расчёт переменных для градиента
            final int height = getHeight();
            final int wigth = getWidth();
            // позиция по горизонтали начального цвета
            final int startH = (int) (wigth / 100f * positionStartColor);
            // позиция по горизонтали конечного цвета
            final int endH = (int) (wigth / 100f * positionEndColor);
            // позиция по вертикали начального цвета
            final int startV = (int) (height / 100f * positionStartColor);
            // позиция по вертикали конечного цвета        
            final int endV = (int) (height / 100f * positionEndColor);
            // градиент
            GradientPaint gp;
            // задание градиентной заливки, в зависимости от его ориентации
            switch (orientationGr) {
            case Constants.HORIZONTAL:
                gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
                break;
            case Constants.VERTICAL:
                gp = new GradientPaint(0, startV, startColor, 0, endV, endColor, isCycle);
                break;
            case Constants.DIAGONAL:
                gp = new GradientPaint(startH, height - startV, startColor, endH, height - endV, endColor, isCycle);
                break;
            case Constants.DIAGONAL2:
                gp = new GradientPaint(startH, startV, startColor, endH, endV, endColor, isCycle);
                break;
            default:
                gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        /**
         * Установить gradient.
         *
         * @param gradient the new gradient
         */
        public void setGradient(GradientColor gradient) {
            startColor = gradient.getStartColor();
            endColor = gradient.getEndColor();
            orientationGr = gradient.getOrientation();
            isCycle = gradient.isCycle();
            positionStartColor = gradient.getPositionStartColor();
            positionEndColor = gradient.getPositionEndColor();
            isEnableGradient = gradient.isEnabled();
            repaint();
        }

    /**
     * Перерисовать компонент и всех его потомков
     */
    private void repaintAll() {
        if (isFoundGradient) {
            repaint();
            Component[] comps = getComponents();
            for (Component comp : comps) {
                comp.repaint();
            }
        }
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
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setListListeners(listListeners, listForDel);
            }
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
