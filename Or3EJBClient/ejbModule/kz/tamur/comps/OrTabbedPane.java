package kz.tamur.comps;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TabbedPanePropertyRoot;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.*;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.CopyTabPanel;
import kz.tamur.util.Pair;
import kz.tamur.util.MapMap;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static kz.tamur.comps.Mode.*;
import kz.tamur.rt.Utils;
import static kz.tamur.rt.Utils.createMenuItem;
public class OrTabbedPane extends OrBasicTabbedPane implements OrGuiContainer, PropertyListener, ActionListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new TabbedPanePropertyRoot();
    private int mode;
    private Element xml;
    private boolean isSelected;

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miBack = createMenuItem("Переместить назад");
    private JMenuItem miForvard = createMenuItem("Переместить вперёд");
    private JMenuItem miCopy = createMenuItem("Создать копию закладки");

    private EventListenerList listeners = new EventListenerList();
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private byte[] description;
    private Map<Component, Pair<Component, Boolean>> tabs = new IdentityHashMap<Component, Pair<Component, Boolean>>();
    private String descriptionUID;
    /** The var name. */
    private String varName;

    /** Начальный цвет градиента. */
    private Color startColor;

    /** конечный цвет градиента. */
    private Color endColor;

    /** Ориентация градиента. */
    private int orientation = 0;

    /** Цикличность градиента. */
    private boolean isCycle = true;

    /** позиция отсчёта градиента для начального цвета. */
    private int positionStartColor = 0;

    /** позиция отсчёта градиента для конечного цвета. */
    private int positionEndColor = 50;
    private boolean isEnableGradient = true;
    private boolean isFoundGradient = true;

    OrTabbedPane(Element xml, int mode, Factory cf, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);

        PropertyNode prop = PROPS.getChild("children");
        PropertyValue pv = getPropertyValue(prop);
        java.util.List children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                Element e = (Element) children.get(i);
                OrGuiComponent comp = cf.create(e, mode, frame);
                comp.setGuiParent(this);
                String title = "Закладка";
                ImageIcon icon = null;
                if (mode == DESIGN) {
                    pv = comp.getPropertyValue(comp.getProperties().getChild("title"));
                    if (!pv.isNull()) {
                        title = (String) pv.resourceStringValue().second;
                    }
                } else {
                    if (comp instanceof OrPanel) {
                        title = ((OrPanel) comp).getTitle();
                    }
                }
                if (comp instanceof OrPanel) {
                    if (mode == DESIGN) {
                        pv = comp.getPropertyValue(comp.getProperties().getChild("view").getChild("icon"));
                        if (!pv.isNull()) {
                            icon = Utils.processCreateImage(pv.getImageValue());
                        }
                    } else {
                        icon = ((OrPanel) comp).getIcon();
                    }
                }
                if (mode == RUNTIME) {
                    int cnt = getTabCount();
                    Component prevTab = (cnt > 0) ? getComponentAt(cnt - 1) : null;
                    tabs.put((Component) comp, new Pair<Component, Boolean>(prevTab, true));
                }

                addTab(title, icon, (Component) comp);
                ((OrGuiContainer) comp).addPropertyListener(this);
                fireStateChanged();

            }
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        // description = PropertyHelper.getDescription(this);
        updateProperties();
        miBack.addActionListener(this);
        miForvard.addActionListener(this);
        miCopy.addActionListener(this);
        pm.add(miBack);
        pm.add(miForvard);
        pm.add(miCopy);
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	super.mouseReleased(e);
                showPop(e);
            }

            private void showPop(MouseEvent e) {
                if (OrTabbedPane.this.mode == DESIGN && e.isPopupTrigger()) {
                    pm.show(OrTabbedPane.this.getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());
                }
            }
        });

        if (getTabCount() != 0) {
            miCopy.setEnabled(true);
            if (getSelectedIndex() == 0) {
                miBack.setEnabled(false);
                miForvard.setEnabled(true);
            } else if (getSelectedIndex() == getTabCount() - 1) {
                miBack.setEnabled(true);
                miForvard.setEnabled(false);
            } else {
                miBack.setEnabled(true);
                miForvard.setEnabled(true);
            }
        } else {
            miBack.setEnabled(false);
            miForvard.setEnabled(false);
            miCopy.setEnabled(false);
        }

        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // TODO упростить
                if (getSelectedIndex() == 0 && getTabCount() > 1) {
                    miBack.setEnabled(false);
                    miForvard.setEnabled(true);
                } else if (getSelectedIndex() == getTabCount() - 1 && getTabCount() > 1) {
                    miBack.setEnabled(true);
                    miForvard.setEnabled(false);
                } else if (getTabCount() == 1) {
                    miBack.setEnabled(false);
                    miForvard.setEnabled(false);
                } else {
                    miBack.setEnabled(true);
                    miForvard.setEnabled(true);
                }
                    miCopy.setEnabled(getTabCount() > 0);
            }
        });
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
        if (mode == DESIGN) {
            addPropertyListener(TabbedContent.instance());
        }
        prop = PROPS.getChild("title");
        pv = getPropertyValue(prop);
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
        }
        /*
         * if (getTabCount() > 0) {
         * setSelectedIndex(getTabCount() - 1);
         * setSelectedIndex(0);
         * }
         */
        if (mode == RUNTIME) {
            ActionMap am = getActionMap();
            am.put("doLeft", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int idx = getSelectedIndex();
                    idx = (idx > 0) ? idx - 1 : getTabCount() - 1;
                    setSelectedIndex(idx);
                }
            });
            am.put("doRight", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int idx = getSelectedIndex();
                    idx = (idx < getTabCount() - 1) ? idx + 1 : 0;
                    setSelectedIndex(idx);
                }
            });

            InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), "doLeft");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), "doRight");
        }

        fireStateChanged();
        prop = null;
    }

    public boolean canAddComponent(int x, int y) {
        return true;
    }

    public void addComponent(OrGuiComponent c, int x, int y) {
        if (c instanceof OrGuiContainer) {
         // Выделить слушателей на удаление
            java.util.List<OrGuiComponent> copyList = new ArrayList<OrGuiComponent>(c.getListListeners());
            // Добавить слушателей родителя в добавляемый компонент
            c.setListListeners(listListeners, copyList);
            PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("title"));
            String title = "Закладка";
            if (!pv.isNull() && !"".equals(((Pair) pv.resourceStringValue()).second)) {
                title = (String) ((Pair) pv.resourceStringValue()).second;
            }
            ImageIcon icon = null;
            if (c instanceof OrPanel) {
                if (mode == DESIGN) {
                    pv = c.getPropertyValue(c.getProperties().getChild("view").getChild("icon"));
                    if (!pv.isNull()) {
                        icon = Utils.processCreateImage(pv.getImageValue());
                    }
                } else {
                    icon = ((OrPanel) c).getIcon();
                }
            }
            addTab(title, icon, (Component) c);
            PropertyHelper.addProperty(new PropertyValue(c.getXml(), PROPS.getChild("children")), xml);
            ((OrGuiContainer) c).addPropertyListener(this);
            firePropertyModified();
            setSelectedIndex(getTabCount() - 1);
            fireStateChanged();
        }
    }

    public void removeComponent(OrGuiComponent c) {
        Component comp = (Component) c;
        remove(comp);
        PropertyHelper.removeProperty(new PropertyValue(c.getXml(), PROPS.getChild("children")), xml);
        revalidate();
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == DESIGN && isSelected) {
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
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
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

    public Element getXml() {
        return xml;
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUID);
        if (mode == RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
        } else {
            PropertyNode pn = PROPS.getChild("description");
            if (pn != null) {
                PropertyValue pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    Pair p = pv.resourceStringValue();
                    description = (byte[]) p.second;
                }
            }
        }
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof OrGuiComponent) {
                OrGuiComponent comp = (OrGuiComponent) c;
                comp.setLangId(langId);
                if (mode == DESIGN) {
                    PropertyValue pv = PropertyHelper.getPropertyValue(comp.getProperties().getChild("title"), comp.getXml(),
                            frame);
                    if (!pv.isNull()) {
                        setTitleAt(i, (String) pv.resourceStringValue().second);
                    }
                } else {
                    if (comp instanceof OrGuiContainer) {
                        setTitleAt(i, ((OrGuiContainer) comp).getTitle());
                    }
                }
            }
        }
    }

    public int getMode() {
        return mode;
    }

    private void updateProperties() {
        // Utils.processBorder(this, frame, borderProps);
        // Utils.processBorderProperties(this, frame, borderProps);
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("tabPolicy"));
        if (!pv.isNull()) {
            switch (pv.intValue()) {
            default:
            case Constants.TAB_WRAP_LINE:
                setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                break;
            case Constants.TAB_SCROLL:
                setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                break;
            }
        } else {
            setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        }

        pv = getPropertyValue(pn.getChild("tabOrientation"));
        if (!pv.isNull() && pv.intValue() > 0) {
            setTabPlacement(pv.intValue());
        } else {
            setTabPlacement(JTabbedPane.TOP);
        }

        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pn = PROPS.getChild("description");
        if (pn != null) {
            pv = getPropertyValue(pn);
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

        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("gradient"));
        isFoundGradient = !pv.isNull();
        if (isFoundGradient) {
            // градиентная заливка компонента
            setGradient((GradientColor) pv.objectValue());
        } 
        pv = getPropertyValue(pn.getChild("transparent"));
        if (!pv.isNull()) {
            // прозрачность компонента(да/нет)
            setOpaque(!pv.booleanValue());
            putClientProperty("TabbedPane.contentOpaque", !pv.booleanValue());
            putClientProperty("TabbedPane.tabsOpaque", !pv.booleanValue());

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
            ((PropertyListener) list[i]).propertyModified(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public void propertyModified(OrGuiComponent c) {
        PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("title"));
        String s = "Закладка";
        if (!pv.isNull() && !"".equals(((Pair) pv.resourceStringValue()).second)) {
            s = (String) ((Pair) pv.resourceStringValue()).second;
        }
        setTitleAt(getSelectedIndex(), s);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == miCopy) {
            CopyTabPanel ctp = new CopyTabPanel(getTitleAt(getSelectedIndex()));
            DesignerDialog dlg = new DesignerDialog((Frame) OrTabbedPane.this.getTopLevelAncestor(), "Копирование закладки", ctp);
            dlg.show();
            if (dlg.isOK()) {
                String newTitle = ctp.getText();
                OrGuiComponent comp = (OrGuiComponent) getSelectedComponent();
                Element xml = comp.getXml();
                Element copyXml = (Element) xml.clone();
                OrGuiComponent copyComp = copyTab(copyXml);
                if (copyComp != null) {
                    copyComp.setPropertyValue(new PropertyValue(new Pair(null, newTitle), copyComp.getProperties().getChild(
                            "title")));
                    addComponent(copyComp, 0, 0);
                    // DesignerFrame.instance().setInterfaceLanguage();
                    setSelectedComponent((Component) copyComp);
                }
            }
            revalidate();
            return;
        }
        int currIdx = getSelectedIndex();
        Element currXml = null;
        Component comp = getSelectedComponent();
        String title = getTitleAt(currIdx);

        remove(comp);
        PropertyValue pv = getPropertyValue(getProperties().getChild("children"));
        List children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
            currXml = (Element) children.remove(currIdx);
        }
        if (src == miBack) {
            children.add(currIdx - 1, currXml);
            ImageIcon icon = null;
            if (comp instanceof OrPanel) {
                if (mode == DESIGN) {
                    pv = ((OrPanel)comp).getPropertyValue(((OrPanel)comp).getProperties().getChild("view").getChild("icon"));
                    if (!pv.isNull()) {
                        icon = Utils.processCreateImage(pv.getImageValue());
                    }
                } else {
                    icon = ((OrPanel)comp).getIcon();
                }
            }
            insertTab(title, icon, comp, "", currIdx - 1);
            setSelectedIndex(currIdx - 1);
        } else if (src == miForvard) {
            children.add(currIdx + 1, currXml);
            ImageIcon icon = null;
            if (comp instanceof OrPanel) {
                if (mode == DESIGN) {
                    pv = ((OrPanel)comp).getPropertyValue(((OrPanel)comp).getProperties().getChild("view").getChild("icon"));
                    if (!pv.isNull()) {
                        icon = Utils.processCreateImage(pv.getImageValue());
                    }
                } else {
                    icon = ((OrPanel)comp).getIcon();
                }
            }
            insertTab(title, icon, comp, "", currIdx + 1);
            setSelectedIndex(currIdx + 1);
        }
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
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
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

    public ArrayList getPanels() {
        ArrayList panels = new ArrayList();
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof OrPanel)
                panels.add(comps[i]);
        }
        return panels;
    }

    private OrGuiComponent copyTab(Element xml) {
        try {
        	MapMap<Long, String, Object> strings = new MapMap<Long, String, Object>();
            loadStringMap(xml, strings, (InterfaceFrame) frame);
            convertUids(xml, (InterfaceFrame) frame, strings);
            OrGuiComponent newComp = Factories.instance().create(xml, DESIGN, frame);
            newComp.setGuiParent(this);
            return newComp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadStringMap(Element xml, MapMap<Long, String, Object> map, InterfaceFrame frame) {
        if ("Component".equals(xml.getName())) {
            String cls = xml.getAttributeValue("class");
            if ("Panel".equals(cls)
                    || "Label".equals(cls)
                    || "HyperLabel".equals(cls)
                    || "Button".equals(cls)
                    || "PopUpPanel".equals(cls)
                    || "CollapsiblePanel".equals(cls)
                    || "Accordion".equals(cls)
                    || "CheckBox".equals(cls)
                    || "HyperPopup".equals(cls)
                    || "TabbedPane".equals(cls)
                    || "Table".equals(cls)
                    || "SplitPane".equals(cls)
                    || "ScrollPane".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Note".equals(cls)
                    || "DocField".equals(cls)
                    || "ChartPanel".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Map".equals(cls)
                    || "MemoField".equals(cls)
                    || "RichTextEditor".equals(cls)
                    || "TreeTable".equals(cls)
                    || "TreeTable2".equals(cls)
                    ) {
                Element e = xml.getChild("title");
                if (e != null) {
                    putToMap(e, map, frame);
                }
                e = xml.getChild("treeTitle");
                if (e != null) {
                    putToMap(e, map, frame);
                }
        	} else if (cls.endsWith("Column")) {
                Element e = xml.getChild("header").getChild("text");
                if (e != null) {
                    putToMap(e, map, frame);
                }
	            e = xml.getChild("header").getChild("editor");
	            if (e != null) {
                    putToMap(e, map, frame);
	            }
	        }
	        Element e = xml.getChild("description");
	        if (e != null) {
                putToMap(e, map, frame);
	        }
	        e = xml.getChild("toolTip");
	        if (e != null) {
                putToMap(e, map, frame);
	        }
            Element e1 = xml.getChild("obligation");
            if (e1 != null) {
                Element e2 = e1.getChild("message");
                if (e2 != null) {
                    putToMap(e2, map, frame);
                }
            }
            e1 = xml.getChild("view");
            if (e1 != null) {
                Element e2 = e1.getChild("border");
                if (e2 != null) {
                    putToMap(e2.getChild("borderTitle"), map, frame);
                }
            }
            e1 = xml.getChild("pov");
            if (e1 != null) {
                Element e2 = e1.getChild("copy");
                if (e2 != null) {
                    putToMap(e2.getChild("copyTitle"), map, frame);
                }
                e2 = e1.getChild("maxObjectCountMessage");
                if (e2 != null) {
                    putToMap(e2, map, frame);
                }
            }
            e1 = xml.getChild("constraints");
            if (e1 != null) {
                Element e2 = e1.getChild("formula");
                if (e2 != null) {
                    putToMap(e2.getChild("message"), map, frame);
                }
            }
            e1 = xml.getChild("children");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }

            e1 = xml.getChild("panel");
            if (("CollapsiblePanel".equals(cls)||"PopUpPanel".equals(cls)) && e1 != null) {
                loadStringMap((Element) e1.getChildren().get(0), map, frame);
            }
            
            e1 = xml.getChild("panels");
            if ("Accordion".equals(cls) && e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            
            e1 = xml.getChild("columns");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("viewComp");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("left");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("right");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
        }
    }

    private void putToMap(Element tag, MapMap<Long, String, Object> map, InterfaceFrame frame) {
        Long[] langs = frame.frameLangs();
        for (int i = 0; i < langs.length; i++) {
            Long lang = langs[i];
            map.put(lang, tag.getText(), frame.getString(lang, tag.getText()));
        }
    }

    public void convertUids(Element xml, InterfaceFrame frame, MapMap<Long, String, Object> strings) throws Exception {
        if ("Component".equals(xml.getName())) {
            Element uidElement = xml.getChild("UUID");
            if (uidElement != null) {
            	uidElement.setText(java.util.UUID.randomUUID().toString());
            }
            String cls = xml.getAttributeValue("class");
            if ("Panel".equals(cls)
                  || "Label".equals(cls)
                  || "HyperLabel".equals(cls)
                  || "Button".equals(cls)
                  || "PopUpPanel".equals(cls)
                  || "CollapsiblePanel".equals(cls)
                  || "Accordion".equals(cls)
                  || "CheckBox".equals(cls)
                  || "HyperPopup".equals(cls)
                  || "TabbedPane".equals(cls)
                  || "Table".equals(cls)
                  || "SplitPane".equals(cls)
                  || "ScrollPane".equals(cls)
                  || "LayoutPane".equals(cls)
                  || "Note".equals(cls)
                  || "DocField".equals(cls)
                  || "ChartPanel".equals(cls)
                  || "LayoutPane".equals(cls)
                  || "Map".equals(cls)
                  || "MemoField".equals(cls)
                  || "RichTextEditor".equals(cls)
                  || "TreeTable".equals(cls)
                  || "TreeTable2".equals(cls)
                  ) {
                Element e = xml.getChild("title");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
                e = xml.getChild("treeTitle");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
            } else if (cls.endsWith("Column")) {
                Element e = xml.getChild("header").getChild("text");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
                e = xml.getChild("header").getChild("editor");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
            }
            Element e = xml.getChild("description");
            if (e != null) {
                putCopyToString(e, frame, strings);
            }
            e = xml.getChild("toolTip");
            if (e != null) {
                putCopyToString(e, frame, strings);
            }
            Element e1 = xml.getChild("obligation");
            if (e1 != null) {
                Element e2 = e1.getChild("message");
                if (e2 != null) {
                    putCopyToString(e2, frame, strings);
                }
            }
            e1 = xml.getChild("view");
            if (e1 != null) {
                Element e2 = e1.getChild("border");
                if (e2 != null) {
                    Element e3 = e2.getChild("borderTitle");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
            }
            e1 = xml.getChild("constraints");
            if (e1 != null) {
                Element e2 = e1.getChild("formula");
                if (e2 != null) {
                    Element e3 = e2.getChild("message");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
            }
            e1 = xml.getChild("pov");
            if (e1 != null) {
                Element e2 = e1.getChild("copy");
                if (e2 != null) {
                    Element e3 = e2.getChild("copyTitle");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
                e2 = e1.getChild("maxObjectCountMessage");
                if (e2 != null) {
                    putCopyToString(e2, frame, strings);
                }
            }
            e1 = xml.getChild("children");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element element = (Element) children.get(i);
                    convertUids(element, frame, strings);
                }
            }
                   
            e1 = xml.getChild("panel");
            if (("CollapsiblePanel".equals(cls)||"PopUpPanel".equals(cls)) && e1 != null) {
                convertUids((Element) e1.getChildren().get(0), frame, strings);
            }
            
            e1 = xml.getChild("panels");
            if ("Accordion".equals(cls) && e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            
            e1 = xml.getChild("columns");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            e1 = xml.getChild("viewComp");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            e1 = xml.getChild("left");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element element = (Element) children.get(i);
                    convertUids(element, frame, strings);
                }
            }
            e1 = xml.getChild("right");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
        }
    }

    private void putCopyToString(Element e, InterfaceFrame frame, MapMap<Long, String, Object> strings) {
        String oldUid = e.getText();
        String uid = frame.getNextUid();
        e.setText(uid);
        Set<Long> keySet = strings.keySet();
        for (Iterator<Long> it = keySet.iterator(); it.hasNext();) {
            Long lang = it.next();
            Object text = strings.get(lang, oldUid);
            try {
                frame.setCopyString(lang, uid, text);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public void setTabVisible(Component tab, boolean isVisible) {
        if (!isVisible) {
            Pair<Component, Boolean> ts = tabs.get(tab);
            if (ts.second) {
                tabs.put(tab, new Pair<Component, Boolean>(ts.first, false));
                remove(tab);
            }
        } else {
            Pair<Component, Boolean> ts = tabs.get(tab);
            if (!ts.second) {
                tabs.put(tab, new Pair<Component, Boolean>(ts.first, true));
                Component prevTab = ts.first;
                while (prevTab != null) {
                    ts = tabs.get(prevTab);
                    if (ts.second) {
                        break;
                    }
                    prevTab = ts.first;
                }
                int index = (prevTab != null) ? indexOfComponent(prevTab) + 1 : 0;
                String title = ((OrGuiContainer) tab).getTitle();
                ImageIcon icon = null;
                if (tab instanceof OrPanel) {
                    if (mode == DESIGN) {
                        PropertyValue pv = ((OrPanel)tab).getPropertyValue(((OrPanel)tab).getProperties().getChild("view").getChild("icon"));
                        if (!pv.isNull()) {
                            icon = Utils.processCreateImage(pv.getImageValue());
                        }
                    } else {
                        icon = ((OrPanel)tab).getIcon();
                    }
                }
                insertTab(title, icon, tab, "", index);
            }
        }
        fireStateChanged();
    }

    
    public ComponentAdapter getAdapter() {
        return null;
    }

    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName()))
            return this;
        int count = getComponentCount();

        for (int i = 0; i < count; i++) {
            Component c = getComponent(i);
            if (c instanceof OrGuiContainer) {
                OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
                if (cc != null)
                    return cc;
            } else if (c instanceof OrGuiComponent) {
                OrGuiComponent gc = (OrGuiComponent) c;
                if (title.equals(gc.getVarName()))
                    return gc;
            }
        }
        return null;
    }

    public String getVarName() {
        return varName;
    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // если для компонента НЕ установлена градиентная заливка
        if (!isEnableGradient || !isFoundGradient) {
            return;
        }

        // защита он неверных параметров
        if (startColor == null && endColor == null) {
            startColor = Color.WHITE;
            endColor = Utils.getLightGraySysColor();
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
        switch (orientation) {
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
     * @param gradient
     *            the new gradient
     */
    public void setGradient(GradientColor gradient) {
        startColor = gradient.getStartColor();
        endColor = gradient.getEndColor();
        orientation = gradient.getOrientation();
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

    /**
     * Обновление иконок на заголовках вкладок
     */
    public void updateIcon() {
        int count = getTabCount();
        if (count == 0) {
            return;
        }
        Component comp;
        ImageIcon icon;
        for (int i = 0; i < count; ++i) {
            comp = getComponentAt(i);
            if (comp instanceof OrPanel) {
                icon = ((OrPanel) comp).getIcon();
                setIconAt(i, icon);
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
    
    
    @Override
    public void setSelectedIndex(int index) {
        Component panel = getComponent(index);
        if (mode == RUNTIME && panel instanceof OrPanel) {
            ASTStart template = ((OrPanel) panel).getBeforeOpenTemplate();
            if (template != null) {
                ClientOrLang orlang = new ClientOrLang(frame, true);
                Map<String, Object> vc = new HashMap<String, Object>();
                try {
                    boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(template, vc, ((UIFrame) frame).getPanelAdapter(), new Stack<String>());
                    if (calcOwner) {
                        OrCalcRef.makeCalculations();
                    }
                } catch (Exception ex) {
                    kz.tamur.rt.adapters.Util.showErrorMessage(((OrPanel) panel), ex.getMessage(), "Действие перед открытием вкладки");
                }
            }
        }
        super.setSelectedIndex(index);
        if (mode == RUNTIME && panel instanceof OrPanel) {
            ASTStart template = ((OrPanel) panel).getAfterOpenTemplate();
            if (template != null) {
                ClientOrLang orlang = new ClientOrLang(frame, true);
                Map<String, Object> vc = new HashMap<String, Object>();
                try {
                    boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(template, vc, ((UIFrame) frame).getPanelAdapter(), new Stack<String>());
                    if (calcOwner) {
                        OrCalcRef.makeCalculations();
                    }
                } catch (Exception ex) {
                    kz.tamur.rt.adapters.Util.showErrorMessage(((OrPanel) panel), ex.getMessage(), "Действие после открытия вкладки");
                }
            }
        }
    }
}

