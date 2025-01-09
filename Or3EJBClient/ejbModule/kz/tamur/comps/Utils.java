package kz.tamur.comps;

import static com.cifs.or2.client.Kernel.SC_CONFIG_LOCAL;
import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF_FOLDER;
import static com.cifs.or2.client.Kernel.SC_USER_FOLDER;
import static kz.tamur.rt.Utils.setAllSize;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ToolBarUI;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.Or3Frame;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.OrGradientToolBar;
import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.guidesigner.EmptyFrame;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.bases.BaseNode;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.guidesigner.boxes.BoxNode;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.hypers.HyperNode;
import kz.tamur.guidesigner.hypers.HyperTree;
import kz.tamur.guidesigner.noteeditor.NoteNode;
import kz.tamur.guidesigner.noteeditor.NoteTree;
import kz.tamur.guidesigner.reports.ReportNode;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.guidesigner.serviceControl.StructureViewNode;
import kz.tamur.guidesigner.serviceControl.StructureViewTree;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.Or3RightsTree;
import kz.tamur.guidesigner.users.PolicyNode;
import kz.tamur.guidesigner.users.UserNode;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Value;
import kz.tamur.rt.Application;
import kz.tamur.rt.AreaDevice;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;
import kz.tamur.util.OrFileFilter;
import kz.tamur.util.OrToolBarUI;
import kz.tamur.util.Pair;
import kz.tamur.util.ServiceControlNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.04.2004
 * Time: 17:23:00
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    private static InterfaceTree interfaceTree;
    private static BaseTree baseTree;
    private static ReportTree reportTree;
    private static ServicesTree servicesTree;
    private static ServicesControlTree servicesControlTree;
    
    private static FiltersTree filtersTree;
    private static BoxTree boxTree;
    /** Дерево пользователей. Считывается один раз */
    private static UserTree userTree;
    private static NoteTree noteTree;
    private static PolicyNode policyNode;

    public static void updateConstraints(OrGuiComponent c) {
        OrGuiContainer cnt = getContainer(c);
        if (cnt != null) {
            cnt.updateConstraints(c);
        }
    }

    public static Map getStrings(int[] objectIds, KrnAttribute[] path, int tr, Kernel krn) throws KrnException {
        Map res = new HashMap();
        for (int i = 0; i < objectIds.length; i++) {
            res.put(new Integer(objectIds[i]), new Integer(objectIds[i]));
        }
        for (int i = 0; i < path.length; i++) {
            KrnAttribute attr = path[i];
            long[] ids = Funcs.makeLongArray(res.values());
            MapMap mmap = null;
            boolean last = (i == path.length - 1);
            if (last) {
                long langId = com.cifs.or2.client.Utils.getDataLangId();
                StringValue[] svs = krn.getStringValues(ids, path[i], langId, false, tr);
                mmap = Funcs.convertStringValues(svs, attr.collectionType == 1);
            } else {
                ObjectValue[] ovs = krn.getObjectValues(ids, path[i], tr);
                mmap = Funcs.convertObjectValues(ovs, attr.collectionType == 1);
            }
            for (Iterator it = res.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                Integer value = (Integer) res.get(key);
                SortedMap map = (SortedMap) mmap.get(value);
                if (map != null && map.size() > 0) {
                    Pair p = (Pair) map.get(map.lastKey());
                    if (last) {
                        res.put(key, p.first);
                    } else {
                        res.put(key, new Long(((KrnObject) p.first).id));
                    }
                } else {
                    it.remove();
                }
            }
        }
        return res;
    }

    public static OrGuiContainer getContainer(OrGuiComponent c) {
        Container parent = ((Component) c).getParent();
        while (parent != null && !(parent instanceof OrGuiContainer)) {
            parent = parent.getParent();
        }
        return (OrGuiContainer) parent;
    }

    public static void processStdCompProperties(OrGuiComponent c, PropertyValue pv) {
        if (!pv.isNull()) {
            PropertyNode prop = pv.getProperty();
            final String name = getParentName(prop);
            final String fullName = prop.getFullPath();
            if ("pos".equals(name) || "pref".equals(name) || "min".equals(name) || "max".equals(name) || "insets".equals(name)
                    || "view.showDateChooser".equals(fullName) || "pov.copy.copyTitle".equals(fullName)
                    || "pov.copy.copyPath".equals(fullName)) {
                updateConstraints(c);
            }
        }
    }

    private static String getParentName(PropertyNode prop) {
        String[] path = prop.getPath();
        if (path.length > 1) {
            return path[path.length - 2];
        }
        return null;
    }

    public static JToolBar createDesignerToolBar() {
        return new DesignerToolBar();
    }

    public static OrGradientToolBar createGradientToolBar() {
        return new OrGradientToolBar();
    }

    public static Dimension getMaxWindowSize() {
        int screen = isDesignerRun() ? Kernel.instance().getUser() == null ? Or3Frame.screen_ : Or3Frame.instance().screen
                : Application.instance().screen;
        // получить все дисплеи
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // если параметры не корректны, то задать основной дисплей
        if (screen < 0 || screen > screenDevices.length - 1) {
            screen = 0;
        }
        final Rectangle bounds = screenDevices[screen].getDefaultConfiguration().getBounds();
        int x = bounds.x;
        int y = bounds.y;
        int height = bounds.height;
        int width = bounds.width;
        return new Dimension(width, height);
    }

    public static Dimension getMaxWindowSizeActDisplay() {
        int screen = isDesignerRun() ? Kernel.instance().getUser() == null ? Or3Frame.screen_ : Or3Frame.instance().screen
                : Application.instance().screen;
        // получить все дисплеи
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        // если параметры не корректны, то задать основной дисплей
        if (screen < 0 || screen > screenDevices.length - 1) {
            screen = 0;
        }
        GraphicsConfiguration conf = screenDevices[screen].getDefaultConfiguration();
        Rectangle bounds = conf.getBounds();
        // получить область без панели задач
        java.awt.Insets ins = Toolkit.getDefaultToolkit().getScreenInsets(conf);
        // получить допустимый размер окна с учётом панели задач
        int width = bounds.width - ins.left - ins.right;
        int height = bounds.height - ins.top - ins.bottom;
        return new Dimension(width, height);
    }

    public static Point getSouthEastLocationPoint(Dimension size) {
        return getLocationPoint(size, GridBagConstraints.SOUTHEAST);
    }

    public static Point getCenterLocationPoint(Dimension size) {
        return getLocationPoint(size, GridBagConstraints.CENTER);
    }

    /**
     * Получить location point.
     * 
     * @param size
     *            размер окна
     * @param orientation
     *            позиция окна, используется GridBagConstraints
     *            NORTHWEST NORTH NORTHEAST
     *            WEST CENTER EAST
     *            SOUTHWEST SOUTH SOUTHEAST
     * 
     * @return the location point
     */
    public static Point getLocationPoint(Dimension size, int orientation) {
        int screen = isDesignerRun() ? Kernel.instance().getUser() == null ? Or3Frame.screen_ : Or3Frame.instance().screen
                : Application.instance().screen;
        // получить все дисплеи
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // если параметры не корректны, то задать основной дисплей
        if (screen < 0 || screen > screenDevices.length - 1) {
            screen = 0;
        }
        GraphicsConfiguration conf = screenDevices[screen].getDefaultConfiguration();
        Rectangle bounds = conf.getBounds();
        // получить область без панели задач
        java.awt.Insets ins = Toolkit.getDefaultToolkit().getScreenInsets(conf);
        int x = bounds.x;
        int y = bounds.y;
        int height = bounds.height;
        int width = bounds.width;
        // корректировка точки вывода окна, в зависимости от расположения панели задач
        switch (orientation) {
        case GridBagConstraints.NORTHWEST:
            break;
        case GridBagConstraints.NORTH:
            break;
        case GridBagConstraints.NORTHEAST:
            break;
        case GridBagConstraints.WEST:
            break;
        case GridBagConstraints.CENTER:
            x = Funcs.add(new int[] {x, width / 2, - size.width / 2, ins.left / 2, - ins.right / 2});
            y = Funcs.add(new int[] {y, height / 2, - size.height / 2, ins.top / 2, - ins.bottom / 2});
            break;
        case GridBagConstraints.EAST:
            break;
        case GridBagConstraints.SOUTHWEST:
            break;
        case GridBagConstraints.SOUTH:
            break;
        case GridBagConstraints.SOUTHEAST:
            x = Funcs.add(new int[] {x, width, -size.width, ins.left, -ins.right});
            y = Funcs.add(new int[] {y, height, -size.height, ins.top, -ins.bottom});
            break;
        default:
            break;
        }

        return new Point(x, y);
    }

    public static Point getCenterLocationPoint(int width, int height) {
        return getCenterLocationPoint(new Dimension(width, height));
    }

    public static class DesignerToolBar extends JToolBar {
        private ToolBarUI ui;

        public DesignerToolBar() {
            super();
            ui = getUI();
            setFloatable(false);
            setRollover(true);
        }

        public void setRollover(boolean isRollover) {
            super.setRollover(isRollover);
            if (!isRollover) {
                setUI(new OrToolBarUI());
            } else {
                setUI(ui);
            }
            setUI(new OrToolBarUI());
            setUI(ui);
        }

        public void addSeparator() {
            ToolSeparator sep = new ToolSeparator();
            this.add(sep);
        }

        protected class ToolSeparator extends JSeparator {

            public ToolSeparator() {
                super();
                setOpaque(true);

                if (DesignerToolBar.this.getOrientation() == JToolBar.HORIZONTAL) {
                    int height = DesignerToolBar.this.getHeight();
                    setPreferredSize(new Dimension(10, height != 0 ? height : 22));
                    // setMaximumSize(new Dimension(10,
                    // DesignerToolBar.this.getHeight()));
                    // setMinimumSize(new Dimension(10,
                    // DesignerToolBar.this.getHeight()));
                } else {
                    setPreferredSize(new Dimension(DesignerToolBar.this.getWidth(), 2));
                    setMaximumSize(new Dimension(DesignerToolBar.this.getWidth(), 2));
                    setMinimumSize(new Dimension(DesignerToolBar.this.getWidth(), 2));
                }
            }

            public void paintComponent(Graphics g) {
                if (DesignerToolBar.this.getOrientation() == JToolBar.HORIZONTAL) {
                    int height = DesignerToolBar.this.getHeight();
                    int s = (height != 0) ? height : 22;
                    Color oldColor = g.getColor();
                    g.setColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
                    g.drawLine(5, 0, 5, s);
                    g.setColor(kz.tamur.rt.Utils.getLightSysColor());
                    g.drawLine(6, 0, 6, s);
                    g.setColor(oldColor);
                } else {
                    int w = DesignerToolBar.this.getWidth();
                    g.setColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
                    g.drawLine(0, 5, w, 5);
                }
            }
        }
    }

    public static MainFrame.DescLabel createDescLabel(String text) {
        MainFrame.DescLabel lab = new MainFrame.DescLabel();
        if (text != null) {
            lab.setText(text);
        }
        lab.setFont(kz.tamur.rt.Utils.getDefaultFont());
        lab.setForeground(kz.tamur.rt.Utils.getDarkShadowSysColor());
        return lab;
    }

    public static void processBorderProperties(OrGuiComponent c, OrFrame frame) {
        if (c instanceof OrPanel) {
            OrPanel comp = (OrPanel) c;
            Border b = comp.getBorderType();
            if (b instanceof TitledBorder) {
                ((TitledBorder) b).setTitle(frame.getString(comp.getBorderTitleUID(), ""));
            }
            comp.setBorder(b);
            return;
        }
        if (c instanceof OrRadioBox) {
            OrRadioBox comp = (OrRadioBox) c;
            Border b = comp.getBorderType();
            if (b instanceof TitledBorder) {
                ((TitledBorder) b).setTitle(frame.getString(comp.getBorderTitleUID()));
            }
            comp.setBorder(b);
            return;
        }
        PropertyNode pn = c.getProperties().getChild("view").getChild("border");
        if (pn != null) {
            PropertyValue pv = c.getPropertyValue(pn.getChild("borderType"));
            if (!pv.isNull()) {
                Border b = pv.borderValue();
                if (b instanceof TitledBorder) {
                    pv = c.getPropertyValue(pn.getChild("borderTitle"));
                    if (!pv.isNull()) {
                        String title = frame.getString((String) pv.resourceStringValue().first);
                        ((TitledBorder) b).setTitle(title);
                    }
                }
                ((JComponent) c).setBorder(b);
            } else {
                Border def = (Border) pn.getChild("borderType").getDefaultValue();
                if (def != null) {
                    ((JComponent) c).setBorder(def);
                } else {
                    ((JComponent) c).setBorder(null);
                }
            }
        }
    }

    public static void processBorder(OrGuiComponent c, OrFrame frame, Map props) {

        String text = "";
        int pos = TitledBorder.DEFAULT_POSITION;
        int align = TitledBorder.DEFAULT_JUSTIFICATION;
        Font borderFont = kz.tamur.rt.Utils.getDefaultFont();
        Color borderTitleColor = kz.tamur.rt.Utils.getDarkShadowSysColor();
        int thick = 1;
        Color borderColor = kz.tamur.rt.Utils.getDarkShadowSysColor();
        Border b = null;

        PropertyNode borderNode = null;
        borderNode = c.getProperties().getChild("border");
        PropertyValue pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderColor"), c.getXml(), frame);
        if (!pv.isNull()) {
            borderColor = pv.colorValue();
        }
        props.put(Constants.BORDER_COLOR, borderColor);

        pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderThick"), c.getXml(), frame);
        if (!pv.isNull()) {
            thick = pv.intValue();
        }
        props.put(Constants.BORDER_THICK, new Integer(thick));

        pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderType"), c.getXml(), frame);
        if (!pv.isNull()) {
            b = pv.borderValue();
        }
        props.put(Constants.BORDER_STYLE, b);
        PropertyNode titleNode = borderNode.getChild("borderTitle");
        PropertyValue pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("text"), c.getXml(), frame);
        if (!pv1.isNull()) {
            Pair p = pv1.resourceStringValue();
            String tUID = (String) p.first;
            props.put(Constants.BORDER_TEXT_UID, tUID);
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("borderTitlePos"), c.getXml(), frame);
            if (!pv1.isNull()) {
                switch (pv1.intValue()) {
                case Constants.CENTER_POSITION:
                    pos = TitledBorder.CENTER;
                    break;
                case Constants.CENTER1_POSITION:
                    pos = TitledBorder.ABOVE_TOP;
                    break;
                case Constants.CENTER2_POSITION:
                    pos = TitledBorder.BELOW_TOP;
                    break;
                }
                props.put(Constants.BORDER_POS, new Integer(pos));
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("borderTitleAlign"), c.getXml(), frame);
            if (!pv1.isNull()) {
                switch (pv1.intValue()) {
                case Constants.CENTER_ALIGNMENT:
                    align = TitledBorder.CENTER;
                    break;
                case Constants.LEFT_ALIGNMENT:
                    align = TitledBorder.LEFT;
                    break;
                case Constants.RIGHT_ALIGNMENT:
                    align = TitledBorder.RIGHT;
                    break;
                }
                props.put(Constants.BORDER_JUST, new Integer(align));
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("font"), c.getXml(), frame);
            if (!pv1.isNull()) {
                borderFont = pv1.fontValue();
                props.put(Constants.BORDER_FONT, borderFont);
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("fontColor"), c.getXml(), frame);
            if (!pv1.isNull()) {
                borderTitleColor = pv1.colorValue();
                props.put(Constants.BORDER_FONT_COLOR, borderTitleColor);
            }
        }
    }

    public static JFileChooser createOpenChooser(int fileType) {
        return createOpenChooser(fileType, 0);
    }

    public static JFileChooser createOpenChooser(int fileType, long langId) {
    	return createOpenChooser(fileType, langId, false);
    }
    
    public static JFileChooser createOpenChooser(int fileType, long langId, boolean multiSelectionEnabled) {
        LangItem li = LangItem.getById(langId);
        ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

        if (li != null) {
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
            }
        }
        JFileChooser fChooser = new JFileChooser(kz.tamur.rt.Utils.loadPropertis().getProperty("lastSelectDir"));
        fChooser.updateUI();
        fChooser.setApproveButtonText(res.getString("open"));// "Открыть");
        fChooser.setApproveButtonToolTipText(res.getString("openFile"));// "Открыть файл");
        fChooser.setDialogTitle(res.getString("openFileTitle"));// "Открытие файла");
        fChooser.setMultiSelectionEnabled(multiSelectionEnabled);
        
        if (fileType > -1) {
	        OrFileFilter filter = new OrFileFilter();
	        if (fileType == Constants.IMAGE_FILTER) {
	            filter.addExtension("gif");
	            filter.addExtension("jpg");
	            filter.addExtension("jpeg");
	            filter.addExtension("png");
	            filter.addExtension("ico");
	            filter.setDescription(res.getString("pictures"));// "Рисунки GIF, JPG");
	        } else if (fileType == Constants.XML_FILTER) {
	            filter.addExtension("xml");
	            filter.setDescription("Интерфейсы в виде XML");
	        } else if (fileType == Constants.STYLEDTEXT_FILTER) {
	            filter.addExtension("stt");
	            filter.setDescription("Стильные тексты");
	        } else if (fileType == Constants.MSDOC_FILTER) {
	            filter.addExtension("doc");
	            filter.addExtension("docx");
	            filter.addExtension("xls");
	            filter.addExtension("xlsx");
	            filter.setDescription(res.getString("docs"));// "Стильные тексты");
	        } else if (fileType == Constants.HTML_FILTER) {
	            filter.addExtension("htm");
	            filter.addExtension("html");
	            filter.setDescription("Документы HTML");
	        } else if (fileType == Constants.JASPER_FILTER) {
	            filter.addExtension("jasper");
	            filter.setDescription("Отчеты JASPER");
	        }
	
	        fChooser.setFileFilter(filter);
        }
        return fChooser;
    }

    public static JFileChooser createSaveChooser(int fileType) {
        JFileChooser fChooser = new JFileChooser();
        fChooser.updateUI();
        fChooser.setApproveButtonText("Сохранить");
        fChooser.setApproveButtonToolTipText("Сохранить файл");
        fChooser.setDialogTitle("Сохранение файла");
        OrFileFilter filter = new OrFileFilter();
        if (fileType == Constants.IMAGE_FILTER) {
            filter.addExtension("gif");
            filter.addExtension("jpg");
            filter.setDescription("Рисунки GIF, JPG");
        } else if (fileType == Constants.XML_FILTER) {
            filter.addExtension("xml");
            filter.setDescription("Интерфейсы в виде XML");
        } else if (fileType == Constants.STYLEDTEXT_FILTER) {
            filter.addExtension("stt");
            filter.setDescription("Стильные тексты");
        }
        fChooser.setFileFilter(filter);
        return fChooser;
    }

    public static InterfaceTree getIfcTree(Object value, KrnObject lastSelected) {
        Kernel krn = Kernel.instance();
        InterfaceTree tree = null;
        try {
            KrnClass cls = krn.getClassByName("UIRoot");
            KrnObject ifcRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = { ifcRoot.id };
            String title = krn.getStringValues(ids, cls.id, "title", com.cifs.or2.client.Utils.getInterfaceLangId(krn), false, 0)[0].value;
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
            InterfaceNode inode = new InterfaceNode(ifcRoot, title, langId);
            tree = new InterfaceTree(inode, langId);
            if (value != null) {
                KrnObject obj = (KrnObject) value;
                KrnObject checkObject = kz.tamur.rt.Utils.getObjectById(obj.id, 0);
                if (checkObject != null) {
                    tree.setSelectedNode(checkObject);
                    return tree;
                }
            }
            if (lastSelected != null) {
                tree.setSelectedNode(lastSelected);
            }
            return tree;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BaseTree getBaseTree(Object value, KrnObject lastSelected) {
        Kernel krn = Kernel.instance();
        BaseTree tree = null;
        try {
            KrnClass cls = krn.getClassByName("Корень структуры баз");
            KrnObject baseRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = { baseRoot.id };
            StringValue[] titles = krn.getStringValues(ids, cls.id, "наименование", 0, false, 0);
            if (titles == null || titles.length == 0) {
                krn.setString(baseRoot.id, baseRoot.classId, "наименование", 0, 0, "Системная база", 0);
                titles = new StringValue[] { new StringValue(baseRoot.id, 0, "Системная база") };
            }
            String title = titles[0].value;
            long flags = krn.getLongValues(ids, cls.id, "flags", 0)[0].value;
            long level = krn.getLongValues(ids, cls.id, "уровень", 0)[0].value;
            KrnObject base = krn.getObjectValues(ids, cls.id, "значение", 0)[0].value;
            boolean isPhysical = krn.getLongValues(ids, cls.id, "физически раздельная?", 0)[0].value == 1 ? true : false;
            BaseNode inode = new BaseNode(baseRoot, title, flags, level, base, 0, isPhysical);
            tree = new BaseTree(inode);
            if (value != null) {
                KrnObject obj = (KrnObject) value;

                KrnObject checkObject = kz.tamur.rt.Utils.getObjectById(obj.id, 0);

                if (checkObject != null) {
                    tree.setSelectedNode(checkObject);
                    return tree;
                }
            } else {
                if (lastSelected != null) {
                    tree.setSelectedNode(lastSelected);
                }
            }
            return tree;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Or3RightsTree getOr3RightsTree(Element rights) {
        Or3RightsNode root = new Or3RightsNode(rights, "or3rights");
        Or3RightsTree tree = new Or3RightsTree(root);
        return tree;
    }

    public static HyperTree getHyperTree() {
        KrnClass cls = null;
        Kernel krn = Kernel.instance();
        HyperNode inode = null;
        HyperTree tree = null;
        try {
            cls = krn.getClassByName("MainTree");
            KrnObject hyperRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = { hyperRoot.id };
            String title = krn.getStringValues(ids, cls.id, "title", krn.getLangIdByCode("RU"), false, 0)[0].value;
            com.cifs.or2.kernel.StringValue[] val = krn.getStringValues(ids, cls.id, "title", krn.getLangIdByCode("KZ"), false, 0);
            String titleKz = val == null || val.length == 0 ? null : val[0].value;
            inode = new HyperNode(hyperRoot, title, titleKz, null, null, 0, null, null, com.cifs.or2.client.Utils.getInterfaceLangId(krn), false, null);
            tree = new HyperTree(inode, true);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return tree;
    }

    public static UserTree getUserTree(String UserRootName) {
        final Kernel krn = Kernel.instance();
        KrnClass cls = null;
        UserTree tree = null;
        UserNode inode = null;
        try {
            cls = krn.getClassByName("UserFolder");
            KrnAttribute attr=krn.getAttributeByName(cls, "name");
            KrnObject [] objs=krn.getObjectsByAttribute(cls.id, attr.id, 0, ComparisonOperations.CO_EQUALS, UserRootName, 0);
            if(objs.length>0){
                    long[] ids = {objs[0].id};
                    String title = krn.getStringValues(ids, cls.id, "name", 0,
                            false, 0)[0].value;
                    inode = new UserNode(objs[0], title, "", "","", null, null,
                            null, null, "", "","", false, false, false, false, false, "", false,null, 0, 0, 
                            null, null,null,null, 0, false);
                    tree = new UserTree(inode);
                    tree.getSelectionModel().setSelectionMode(
                            TreeSelectionModel.SINGLE_TREE_SELECTION);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return tree;
    }
    
    public static synchronized UserTree getUserTree() {
    	return getUserTree(false);
    }

   /**
     * Возвращает дерево пользователей. При первом обращении запрашивает с сервера
     * объект - корень дерева пользователей (UserRoot), создает корневой узел дерева
     * и создает новое дерево с указанным корнем. Устанавливает для дерева режим
     * выделения только одного узла
     * 
     * @return Дерево пользователей системы
     */
    public static synchronized UserTree getUserTree(boolean isReload) {
    	if (isReload || userTree == null) {
	        final Kernel krn = Kernel.instance();
	        KrnClass cls = null;
	        UserNode inode = null;
	        
	        try {
	            cls = krn.getClassByName("UserRoot");
	            KrnAttribute configAttr = krn.getAttributeByName(cls, "config");
	            KrnObject userRoot = krn.getClassObjects(cls, 0)[0];
	            long[] ids = { userRoot.id };
	            
                AttrRequestBuilder arb = new AttrRequestBuilder(SC_USER_FOLDER, krn).add("name")
                		.add("process").add("or3rights").add("hyperMenu").add("helps");
                
                if (configAttr != null)
                	arb.add("config");
                
                String title = null;
                KrnObject processObj = null;
    	        Element or3Rights = null;
                KrnObject[] hypers = null;
                KrnObject[] helps = null;
                KrnObject config_ = null;
	            int monitorTask = 0, toolBar = 0;

                List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
                if (rows.size() > 0) {
                    long lang_ru = krn.getLangIdByCode("RU");

                    Object[] row = rows.get(0);
                	
                	if (row[2] != null)
                		title = (String)row[2];
                	
                	if (row[3] != null) {
                		processObj = (KrnObject)row[3];
                		UserNode.setItemsTitle(new long[] {processObj.id}, processObj.classId, "title", lang_ru, krn);
                	}

                	if (row[4] != null) {
                		byte[] b = (byte[])row[4];
                        try {
                            if (b != null && b.length > 0) {
                                SAXBuilder builder = new SAXBuilder();
                                Document doc = builder.build(new ByteArrayInputStream(b), "UTF-8");
                                or3Rights = doc.getRootElement();
                            }
                        } catch (JDOMException e) {
                            System.out.println("Не могу прочитать права or3rights!");
                        } catch (Exception e) {
                            System.out.println("Свойство or3rights[blob] не найдено в классе UserFolder!");
                        }
                	}
                	
                	if (row[5] != null) {
                		List<Value> tmps = (List)row[5];
                		
                		if (tmps.size() > 0) {
	                		long[] oids = new long[tmps.size()];
	                		hypers = new KrnObject[tmps.size()];
	                		for(int i=0; i<tmps.size(); i++) {
	                			hypers[i] = (KrnObject)tmps.get(i).value;
	                			oids[i] = hypers[i].id;
	                		}
	                		UserNode.setItemsTitle(oids, hypers[0].classId, "title", lang_ru, krn);
                		}
                	}
                    
                	if (row[6] != null) {
                		List<Value> tmps = (List)row[6];
                		
                		if (tmps.size() > 0) {
	                		long[] oids = new long[tmps.size()];
	                		helps = new KrnObject[tmps.size()];
	                		for(int i=0; i<tmps.size(); i++) {
	                			helps[i] = (KrnObject)tmps.get(i).value;
	                			oids[i] = helps[i].id;
	                		}
	                		UserNode.setItemsTitle(oids, helps[0].classId, "title", lang_ru, krn);
                		}
                	}

                	if (configAttr != null) {
                    	if (row[7] != null) {
                    		config_ = (KrnObject)row[7];
                            AttrRequestBuilder arb2 = new AttrRequestBuilder(SC_CONFIG_LOCAL, krn).add("isMonitor").add("isToolBar");

                            List<Object[]> rows2 = krn.getObjects(new long[] {config_.id}, arb2.build(), 0);
                            
                            if (rows2.size() > 0) {
                            	Object[] row2 = rows2.get(0);
                            	
                            	if (row2[2] != null)
                            		monitorTask = ((Number)row2[2]).intValue();
                            	if (row2.length > 3 && row2[3] != null)
                            		toolBar = ((Number)row2[3]).intValue();
                            }
                    	}
                	}
                	
                }

	            inode = new UserNode(userRoot, title, "", "", "", null, null, null, null, "", "", "", false, false, false, false,
	                    false, "", false, config_, monitorTask, toolBar, hypers, processObj, or3Rights, helps, 0, false);
	            userTree = new UserTree(inode);
	            userTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	        } catch (KrnException e) {
	            e.printStackTrace();
	        }
    	}
    	return userTree;
    }

    public static PolicyNode getPolicyNode() {
        if (policyNode == null) {
            policyNode = new PolicyNode();
            if (policyNode.getKrnObj() == null)
                policyNode = null;
        } else {
            policyNode.reload();
        }
        return policyNode;
    }
    
    public static ServicesTree getServicesTree() {
    	return getServicesTree(false);
    }

    public static ServicesTree getServicesTree(boolean isReload) {
        if (isReload || servicesTree == null) {
            final Kernel krn = Kernel.instance();
            KrnClass cls = null;
            ServiceNode inode = null;
            try {
                cls = krn.getClassByName("ProcessDefRoot");
                KrnObject[] objs = krn.getClassObjects(cls, 0);
                long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
                KrnObject serviceRoot = null;
                if (objs == null || objs.length == 0) {
                    serviceRoot = krn.createObject(cls, 0);
                    krn.setString(serviceRoot.id, serviceRoot.classId, "title", 0, langId, "Процессы", 0);
                } else {
                	serviceRoot = objs[0];
                }
                long[] ids = { serviceRoot.id };
                
    	        long ruId = krn.getLangIdByCode("RU");
    	        long kzId = krn.getLangIdByCode("KZ");
    	        
    	        AttrRequestBuilder arb = new AttrRequestBuilder(SC_PROCESS_DEF_FOLDER, krn)
    	        		.add("title", ruId).add("title", kzId).add("runtimeIndex")
    	        		.add("isTab").add("tabName", ruId).add("tabName", kzId);
    	
    	        String title = null;
    	        String titleKz = null;
    	        long runtimeIndex = 0;
    	        boolean isTab = false;
    	        String tabRu = null;
    	        String tabKz = null;
    	        
    	        List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
    	        if (rows.size() > 0) {
    	            Object[] row = rows.get(0);
    	        	
    	            title = (row[2] != null) ? (String)row[2] : "Не определён";
    	            titleKz = (row[3] != null) ? (String)row[3] : "";
    	            runtimeIndex = (row[4] != null) ? (Long)row[4] : 0;
    	            isTab = (row[5] != null) ? (Boolean)row[5] : false;
    	
    	            tabRu = (row[6] != null) ? (String)row[6] : "";
    	            tabKz = (row[7] != null) ? (String)row[7] : "";
    	        	
    	        }
    	        
    	        inode = new ServiceNode(serviceRoot, langId == ruId ? title : titleKz, langId, 0, title, titleKz, runtimeIndex, isTab, tabRu, tabKz, "", false, null);
                servicesTree = new ServicesTree(inode, null, false);
                servicesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return servicesTree;
    }

    public static ServicesControlTree getServicesControlTree() {
        if (servicesControlTree == null) {
            final Kernel krn = Kernel.instance();
            KrnClass cls = null;
            ServiceControlNode inode = null;
            try {
                cls = krn.getClassByName(Constants.NAME_CLASS_CONTROL_FOLDER_ROOT);
                KrnObject[] objs = krn.getClassObjects(cls, 0);
                long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
                
                KrnObject controlRoot = null;
                if (objs == null || objs.length == 0) {
                	controlRoot = krn.createObject(cls, 0);
                    krn.setString(controlRoot.id, controlRoot.classId, "title", 0, langId, "Управление процессами", 0);
                } else {
                	controlRoot = objs[0];
                }
                
                long[] ids = { controlRoot.id };
                StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
                String title = strs.length > 0 ? strs[0].value : "Не определён";

                inode = new ServiceControlNode(controlRoot, null, title, 0, langId, 0);
            } catch (KrnException e) {
                e.printStackTrace();
                return null;
            }
            servicesControlTree = new ServicesControlTree(inode, null);
            servicesControlTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        return servicesControlTree;
    }

    
    public static ServicesControlTree getServicesControlTree(ServiceControlNode root) {
        ServicesControlTree servicesControlTree;
        servicesControlTree = new ServicesControlTree(root, null);
        servicesControlTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return servicesControlTree;
    }
    
    /**
     * Получить дерево структуры объекта.
     *
     * @param root узел, структуру объекта которого необходимо отобразить
     * @return structure дерево структуры
     */
    public static StructureViewTree getStructureViewTree(StructureViewNode root) {
        StructureViewTree servicesControlTree;
        servicesControlTree = new StructureViewTree(root);
        return servicesControlTree;
    }
    
    public static BoxTree getBoxTree() {
        if (boxTree == null) {
            final Kernel krn = Kernel.instance();
            KrnClass cls = null;
            BoxNode inode = null;
            try {
                cls = krn.getClassByName("BoxRoot");
                KrnObject boxRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = { boxRoot.id };
                StringValue[] strs = krn.getStringValues(ids, cls.id, "name", 0, false, 0);
                KrnObject[] bases = krn.getObjects(boxRoot, "base", 0);
                KrnObject base = krn.getUser().getBase();
                if (bases.length > 0)
                    base = bases[0];
                String title = "Не определён";
                if (strs.length > 0) {
                    title = strs[0].value;
                }
                inode = new BoxNode(boxRoot, title, base, "", "", "", "", "", "", "", new byte[0], "", 0, 0, 0);// , null);
                boxTree = new BoxTree(inode);
                boxTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return boxTree;
    }

    public static FiltersTree getFiltersTree() {
        return getFiltersTreeContent(null);
    }
    
    public static FiltersTree getFiltersTreeContent(ControlTabbedContent tcontent) {
    	return getFiltersTreeContent(tcontent, false);
    }

    public static FiltersTree getFiltersTreeContent(ControlTabbedContent tcontent, boolean isReload) {
    	if (filtersTree == null || isReload) {
    		final Kernel krn = Kernel.instance();
    		KrnClass cls = null;
    		FilterNode inode = null;
    		try {
    			cls = krn.getClassByName("FilterRoot");
    			KrnObject[] objs = krn.getClassObjects(cls, 0);
    			long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
    			KrnObject filterRoot = null;
    			if (objs == null || objs.length == 0) {
    				filterRoot = krn.createObject(cls, 0);
    				krn.setString(filterRoot.id, filterRoot.classId, "title", 0, langId, "Фильтры", 0);
    			} else {
    				filterRoot = objs[0];
    			}
    			long[] ids = { filterRoot.id };
    			StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
    			String title = "Не определён";
    			if (strs.length > 0) {
    				title = strs[0].value;
    			}
    			inode = new FilterNode(filterRoot, title, langId, 0);
    			filtersTree = new FiltersTree(inode, langId, tcontent);
    			filtersTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    		} catch (KrnException e) {
    			e.printStackTrace();
    		}
    	}
        return filtersTree;
    }

    public static FiltersTree getFiltersTree(KrnObject flrFolder) {
        if (flrFolder == null)
            return getFiltersTree();
        final Kernel krn = Kernel.instance();
        FiltersTree filtersTree_ = null;
        FilterNode inode = null;
        try {
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId();
            String[] str = krn.getStrings(flrFolder, "title", langId, 0);
            String title = "Не определён";
            if (str.length > 0) {
                title = str[0];
            }
            inode = new FilterNode(flrFolder, title, langId, 0);
            filtersTree_ = new FiltersTree(inode, langId);
            filtersTree_.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return filtersTree_;
    }

    public static InterfaceTree getInterfaceTree() {
    	return getInterfaceTree(false);
    }

    public static InterfaceTree getInterfaceTree(boolean isReload) {
        if (isReload || interfaceTree == null) {
            Kernel krn = Kernel.instance();
            KrnClass cls = null;
            KrnObject lang = krn.getInterfaceLanguage();
            long langId = (lang != null) ? lang.id : 0;
            try {
                cls = krn.getClassByName("UIRoot");
                KrnObject[] objs = krn.getClassObjects(cls, 0);
                if (objs == null || objs.length == 0) {
                    KrnObject obj = krn.createObject(cls, 0);
                    krn.setString(obj.id, obj.classId, "title", 0, langId, "Интерфейсы", 0);
                }
                KrnObject uiRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = { uiRoot.id };
                StringValue[] svs = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
                String title = "Не назначен";
                if (svs.length > 0 && svs[0] != null) {
                    title = svs[0].value;
                }
                InterfaceNode inode = new InterfaceNode(uiRoot, title, langId);
                interfaceTree = new InterfaceTree(inode, langId);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return interfaceTree;
    }

    public static BaseTree getBaseTree() {
        if (baseTree == null) {
            Kernel krn = Kernel.instance();
            KrnClass cls = null;
            try {
                cls = krn.getClassByName("Корень структуры баз");
                KrnObject baseRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = { baseRoot.id };
                String title = krn.getStringValues(ids, cls.id, "наименование", 0, false, 0)[0].value;
                long flags = krn.getLongValues(ids, cls.id, "flags", 0)[0].value;
                long level = krn.getLongValues(ids, cls.id, "уровень", 0)[0].value;
                KrnObject base = krn.getObjectValues(ids, cls.id, "значение", 0)[0].value;
                boolean isPhysical = krn.getLongValues(ids, cls.id, "физически раздельная?", 0)[0].value == 1 ? true : false;
                BaseNode inode = new BaseNode(baseRoot, title, flags, level, base, 0, isPhysical);
                baseTree = new BaseTree(inode);
                baseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baseTree;
    }

    public static ReportTree getReportTree(KrnObject lang) {
    	if(reportTree == null) {
    		Kernel krn = Kernel.instance();
    		KrnObject lang_ = krn.getInterfaceLanguage();
    		lang = (lang != null) ? lang : lang_;
    		KrnClass cls = null;
    		KrnObject reportRoot = null;
    		String title = "";
    		try {
    			cls = krn.getClassByName("ReportRoot");
    			reportRoot = krn.getClassObjects(cls, 0)[0];
    			long[] ids = { reportRoot.id };
    			StringValue[] vals = krn.getStringValues(ids, cls.id, "title", lang.id, false, 0);
    			if (vals != null && vals.length > 0) {
    				title = vals[0].value;
    			}
    		} catch (KrnException e) {
    			e.printStackTrace();
    		}
    		OrFrame frame = new EmptyFrame();
    		frame.setInterfaceLang(lang);
    		ReportNode inode = new ReportNode(reportRoot, title, null, 0, frame);
    		reportTree = new ReportTree(inode, frame);
    	}
        return reportTree;
    }

    public static DesignerDialog getDesignerDialog(Container comp, String title, JPanel content, boolean hasClearBtn) {
        if (comp instanceof Dialog)
            return new DesignerDialog((Dialog) comp, title, content, hasClearBtn);
        else if (comp instanceof Frame)
            return new DesignerDialog((Frame) comp, title, content, hasClearBtn);
        else
            return new DesignerDialog((Dialog) null, title, content, hasClearBtn);
    }

    public static DesignerModalFrame getDesignerModalFrame(Container comp, String title, JPanel content, boolean hasClearBtn) {
        if (comp instanceof Dialog)
            return new DesignerModalFrame((Dialog) comp, title, content, hasClearBtn);
        else if (comp instanceof Frame)
            return new DesignerModalFrame((Frame) comp, title, content, hasClearBtn);
        return null;
    }

    public static DesignerDialog getFilterMenu(Container comp, String title, JScrollPane content, long ifcLangId) {
        DesignerDialog dlg = null;
        if (comp instanceof Dialog)
            dlg = new DesignerDialog((Dialog) comp, title, content, false, false, false, false, true);
        else if (comp instanceof Frame)
            dlg = new DesignerDialog((Frame) comp, title, content, false, false, false, false, true);
        dlg.setLanguage(ifcLangId);
        return dlg;
    }

    public static List parentComponentSetting(Component comp, List<Component> list) {
        Component res = null;
        if (comp != null) {
            res = comp.getParent();
            if (res instanceof OrPanel || res instanceof OrScrollPane || res instanceof OrSplitPane
                    || res instanceof OrTabbedPane || res instanceof OrLayoutPane || res instanceof JViewport) {
                Component c = res.getParent();
                if (c != null) {
                    list.add(res);
                    return parentComponentSetting(res, list);
                } else {
                    list.add(res);
                }
            } else if (res instanceof JTable) {
                Component c = res.getParent().getParent().getParent();
                if (c != null && c instanceof OrTable) {
                    list.add(c);
                    return parentComponentSetting(c, list);
                }
            }
        }
        return list;
    }

    public static String getFilterNameById(long id) {
        Kernel krn = Kernel.instance();
        String name = "";
        try {
            KrnClass cls = krn.getClassByName("Filter");
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
            StringValue[] strs = krn.getStringValues(new long[] { id }, cls.id, "title", langId, false, 0);
            name = (strs.length > 0) ? strs[0].value : "";
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static NoteTree getNotesTree() {
        final Kernel krn = Kernel.instance();
        KrnClass cls = null;
        NoteNode inode = null;
        try {
            cls = krn.getClassByName("NoteRoot");
            KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
            long[] ids = { filterRoot.id };
            StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
            String title = "Не определён";
            if (strs.length > 0) {
                title = strs[0].value;
            }
            inode = new NoteNode(filterRoot, title, langId, 0);
            noteTree = new NoteTree(inode, langId);
            noteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return noteTree;
    }

    public static String parseObjectsToTitle(KrnObject[] objs) {
        String title = "";
        if (objs != null && objs.length > 0) {
            Kernel krn = Kernel.instance();
            try {
                KrnClass cls = krn.getClassByName("HiperTree");
                KrnAttribute attr = krn.getAttributeByName(cls, "title");
                /*
                 * java.util.List objIdList = new ArrayList();
                 * for (int i = 0; i < objs.length; i++) {
                 * KrnObject obj = objs[i];
                 * objIdList.add(obj);
                 * }
                 */
                long[] ids = Funcs.makeObjectIdArray(objs);
                StringValue[] strVals = krn.getStringValues(ids, attr, com.cifs.or2.client.Utils.getInterfaceLangId(), false, 0);
                for (int i = 0; i < strVals.length; i++) {
                    StringValue strVal = strVals[i];
                    if (strVal.value.length() > 0) {
                        title = ("".equals(title)) ? strVal.value : title + "," + strVal.value;
                    }
                }
                /*
                 * for (int i = 0; i < objs.length; i++) {
                 * KrnObject obj = objs[i];
                 * String[] strs = krn.getStrings(obj, "title",
                 * com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                 * if (strs.length > 0) {
                 * title = ("".equals(title)) ? strs[0] : title + "," +strs[0];
                 * }
                 * }
                 */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    public static Component getPanel(Component comp) {
        Component res;
        if (comp != null) {
            res = comp.getParent();
        } else {
            return null;
        }
        if (res instanceof OrPanel) {
            return res;
        } else if (!(res instanceof OrPanel)) {
            return getPanel(res);
        } else {
            return null;
        }
    }

    public static Component findTabbedPane(Component comp) {
        Component res;
        if (comp != null) {
            res = comp.getParent();
        } else {
            return null;
        }
        if (res instanceof kz.tamur.comps.OrTabbedPane) {
            return res;
        } else if (!(res instanceof kz.tamur.comps.OrTabbedPane)) {
            return findTabbedPane(res);
        } else {
            return null;
        }
    }

    /**
     * Получает текстовую строку выполняя переданную формулу
     * 
     * @param expr
     *            Формула
     * @param frame
     *            Фрейм компонента
     * @param ctx
     *            адаптер компонента
     * @return Возвращает объект соответствующего класса
     */
    public static Object evalExp(String expr, OrFrame frame, CheckContext ctx) {
        expr = expr.trim();
        ASTStart dataEvaluate = null;
        if (expr.length() > 0) {
            dataEvaluate = OrLang.createStaticTemplate(expr);
        }
        if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            try {
                Map<String, Object> vc = new HashMap<String, Object>();
                boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(dataEvaluate, vc, ctx, new Stack<String>());
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
                Object rez = vc.get("RETURN");
                return rez;

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }
    
    public static String getMethodOwner(KrnMethod method) {
    	if(method == null) return null;
    	Kernel krn = Kernel.instance();
    	String res = null;
    	
    	try {
    		if (krn.getBindingModuleToUserMode()) {
    			if (method.ownerId > 0) {
    				long currentUserId = krn.getUserSession().userObj.id;
    				if (method.ownerId != currentUserId) {
    					KrnObject userObj = krn.getObjectById(method.ownerId, 0);
    					if (userObj != null) {	// Владелец метода существует
    						KrnClass userCls = krn.getClassByName("User");
    						KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
    						res = krn.getStringsSingular(method.ownerId, userNameAttr.id, 0, false, false);
    						
    					}
    				}
    			}
    		}
    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
        
        return res;
    }
    
    public static String getObjOwner(KrnObject obj) {
    	String res = null;
    	Kernel krn = Kernel.instance();
    	try {
	        if (krn.getBindingModuleToUserMode()) {
				KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
	        	if (developerObjs.length > 0) {
	        		long ownerId = developerObjs[0].id;
	        		long currentUserId = krn.getUserSession().userObj.id;
//	        		if (ownerId != currentUserId) {
	        			KrnObject userObj = krn.getObjectById(ownerId, 0);
	        			if (userObj != null) {	// Владелец фильтра существует
	            			KrnClass userCls = krn.getClassByName("User");
	            			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
	            			res = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
	        			}
//	        		}
	        	}
	        }
        } catch(KrnException e) {
        	e.printStackTrace();
        }
    	return res;
    }
    /**
     * Получает текстовую строку выполняя переданную формулу
     * 
     * @param expr
     *            Формула
     * @param frame
     *            Фрейм компонента
     * @param ctx
     *            адаптер компонента
     * @return Строка которая получается путём выполнения формулы и взятия конечной переменной <code>RETURN</code>>
     */
    public static String getExpReturn(String expr, OrFrame frame, CheckContext ctx) {
        expr = expr.trim();
        ASTStart dataEvaluate = null;
        if (expr.length() > 0) {
            dataEvaluate = OrLang.createStaticTemplate(expr);
        }
        if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            try {
                Map<String, Object> vc = new HashMap<String, Object>();
                boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(dataEvaluate, vc, ctx, new Stack<String>());
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
                Object rez = vc.get("RETURN");
                return rez.toString();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

    /**
     * Перегрузка <code>isChangeScreen(int x, int y, Dimension size)</code> Checks if is change screen.
     * 
     * @param e
     *            событие отправленное слушателю
     */
    public static void isChangeScreen(ComponentEvent e) {
        Component sourse = (Component) e.getSource();
        Point p = null;
        try {
            p = sourse.getLocationOnScreen();
        } catch (IllegalComponentStateException e2) {
        }
        if (p != null && sourse != null) {
            Utils.isChangeScreen(p.x, p.y, sourse.getSize());
        }
    }

    /**
     * Проверяет, было ли перенесено окно на другой монитор
     * если да, то в конфигурационный файл пишется номер монитора
     * для запущенного приложения
     * 
     * @param x
     *            положение окна по X
     * @param y
     *            положение окна по Y
     * @param size
     *            размер окна
     */
    public static void isChangeScreen(int x, int y, Dimension size) {

        GraphicsDevice[] screenDevices = kz.tamur.rt.Utils.screenDevices;
        AreaDevice[] areaDevices = kz.tamur.rt.Utils.areaDevices;
        // если это первый вызов метода
        if (screenDevices == null) {
            // выявить все дисплеи
            screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            // создать массив с разрешениями дисплеев
            areaDevices = new AreaDevice[screenDevices.length];
            // заполнить разрешения
            Rectangle bouns;
            for (int i = 0; i < screenDevices.length; ++i) {
                bouns = screenDevices[i].getDefaultConfiguration().getBounds();
                areaDevices[i] = new AreaDevice(bouns.width, bouns.height, bouns.x, bouns.y);
            }
        }
        // если у пользователя всего один дисплей, то уйти из метода
        if (screenDevices.length == 1) {
            return;
        }

        // переход на другой дисплей определяется по центральной точке окна
        // определить центральную точку
        int cX = x + size.width / 2;
        int cY = y + size.height / 2;
        int newScreen = -1;
        int oldScreen = -1;
        // проверка центральной точки на попадание в диапазон дисплея
        for (int i = 0; i < areaDevices.length; ++i) {
            if (cX >= areaDevices[i].x && cX <= areaDevices[i].x + areaDevices[i].width && cY >= areaDevices[i].y
                    && cY <= areaDevices[i].y + areaDevices[i].height) {
                newScreen = i;
            }
        }

        String nameProp = "";
        if (isDesignerRun()) {
            nameProp = "screenDesigner";
            oldScreen = Kernel.instance().getUser() == null ? Or3Frame.screen_ : Or3Frame.instance().screen;
        } else {
            nameProp = "screenApplication";
            oldScreen = Application.instance().screen;
        }
        // если дисплей был изменён
        if (oldScreen != newScreen) {
            kz.tamur.rt.Utils.setProperty(nameProp, newScreen);
            if (isDesignerRun()) {
                if (Kernel.instance().getUser() == null) {
                    Or3Frame.screen_ = newScreen;
                } else {
                    Or3Frame.instance().screen = newScreen;
                }
            } else {
                Application.instance().screen = newScreen;
            }
        }
    }

    public static Cursor getHelpCursor() {
        if (kz.tamur.rt.Utils.helpCursor == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = getImageIcon("HelpCursor").getImage();
            kz.tamur.rt.Utils.helpCursor = toolkit.createCustomCursor(image, new Point(0, 0), "helpcursor");
        }
        return kz.tamur.rt.Utils.helpCursor;
    }

    public static void lookAndFeelMenuItem(JMenuItem mi, String text, String iconName) {
        kz.tamur.rt.Utils.lookAndFeelMenuItem(mi, text);
        if (mi != null) {
            ImageIcon ic = null;
            try {
                ic = getImageIcon(iconName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mi.setIcon(ic);
        }
    }

    private static class DesignerMenu extends JMenu {
        private static final long serialVersionUID = 1L;

        public DesignerMenu(String title) {
            super(title);
            init();
        }

        private void init() {
            setFont(kz.tamur.rt.Utils.getDefaultFont());
            setForeground(kz.tamur.rt.Utils.getDefaultFontColor());
            String title = getText();
            if (title != null) {
                if (title.equals("Копировать")) {
                    setIcon(getImageIcon("edit"));
                }
            }
        }
    }

    private static class DesinerCheckMenuItem extends JCheckBoxMenuItem {

        private int type;
        private String title;
        private ImageIcon icon;

        public DesinerCheckMenuItem(String title, int type) {
            super(title);
            this.type = type;
            this.title = title;
            init();
        }

        void init() {
            setFont(kz.tamur.rt.Utils.getDefaultFont());
            switch (type) {
            case ButtonsFactory.FN_TREE:
                icon = getImageIcon("CompsTree");
                break;
            case ButtonsFactory.FN_INSPECTOR:
                icon = getImageIcon("inspector");
                break;
            case ButtonsFactory.FN_DEBUG:
                icon = getImageIcon("FnDebug");
                break;
            case ButtonsFactory.FN_CLASSES:
                icon = getImageIconFull("FnClasses2.png");
                break;
            case ButtonsFactory.FN_AREA:
                icon = getImageIcon("TabbedPane");
                setAllSize(this, new Dimension(24, 24));
                break;
            case ButtonsFactory.FN_SERVICES:
                icon = getImageIconFull("FnServices2.png");
                break;
            case ButtonsFactory.FN_INTERFACES:
                icon = getImageIconFull("FnIfc2.png");
                break;
            case ButtonsFactory.FN_FILTERS:
                icon = getImageIconFull("FnFilters2.png");
                break;
            case ButtonsFactory.FN_USERS:
                icon = getImageIconFull("FnUsers2.png");
                break;
            case ButtonsFactory.FN_REPORTS:
                icon = getImageIconFull("FnReports2.png");
                break;
            case ButtonsFactory.FN_HYPERS:
                icon = getImageIconFull("FnComps2.png");
                break;
            case ButtonsFactory.FN_BASE:
                icon = getImageIconFull("FnBase2.png");
                break;
            case ButtonsFactory.FN_SCHEDULER:
                icon = getImageIconFull("FnSched2.png");
                break;
            case ButtonsFactory.FN_BOXES:
                icon = getImageIconFull("FnBox2.png");
                break;
            case ButtonsFactory.FN_FUNC:
                icon = getImageIcon("Functions");
                break;
            case ButtonsFactory.FN_ACTIVE_USERS:
                icon = getImageIconFull("FnActiveUsers2.png");
                break;
            case ButtonsFactory.FN_REPL:
                icon = getImageIconFull("FnRepl2.png");
                break;
            case ButtonsFactory.FN_SEARCH:
                icon = getImageIconFull("FnSearch2.png");
                break;
            case ButtonsFactory.FN_TERMINAL:
                icon = getImageIconFull("FnTerminal2.png");
                break;
            case ButtonsFactory.FN_CONFIG:
                icon = getImageIconFull("FnConfig2.png");
                break;
            case ButtonsFactory.FN_CONFIGS:
                icon = getImageIconFull("FnConfigs2.png");
                break;
            case ButtonsFactory.FN_SERVICES_CONTROL:
                icon = getImageIconFull("FnServicesControl2.png");
                break;
            case ButtonsFactory.FN_CHAT:
                icon = getImageIconFull("FnServicesControl2.png");
                break;
            case ButtonsFactory.FN_RECYCLE:
                icon = getImageIconFull("FnRecycle2.png");
                break;
            case ButtonsFactory.FN_PROC:
                icon = getImageIconFull("FnProcedure2.png");
                break;
            case ButtonsFactory.FN_VCS_CHANGE:
                icon = getImageIconFull("FnVcsChange2.png");
                break;
            case ButtonsFactory.FN_RIGHTS:
                icon = getImageIconFull("FnRights2.png");
                break;
            }
            setIcon(icon);
        }
    }

    public static DesinerCheckMenuItem createCheckMenuItem(String title) {
        return new DesinerCheckMenuItem(title, -1);
    }

    public static DesinerCheckMenuItem createCheckMenuItem(String title, int type) {
        return new DesinerCheckMenuItem(title, type);
    }

    /**
     * Проверяет то что сейчас запущенно
     * 
     * @return true если запущен дизайнер
     */
    public static boolean isDesignerRun() {
        return Application.instance() == null;
    }

    public static JButton createBtnEditorIfc(ActionListener parent) {
        return createBtn(parent, "editorIfc");
    }

    public static JButton createBtnEditor(ActionListener parent) {
        return createBtn(parent, "editor");
    }

    public static JButton createBtnOpenIfc(ActionListener parent) {
        return createBtn(parent, "CodeEditor");
    }

    public static JButton createBtn(ActionListener parent, String icon) {
        JButton button = new JButton();
        kz.tamur.rt.Utils.setAllSize(button, Constants.BTN_EDITOR_SIZE);
        if (parent != null) {
            button.addActionListener(parent);
        }
        button.setIcon(getImageIcon(icon));
        button.setMargin(Constants.INSETS_0);
        return button;
    }

    public static OrPropTextField createEditor(Font font) {
        return createEditor(null, font);
    }

    public static OrPropTextField createEditor(ActionListener parent, Font font) {
        OrPropTextField label = new OrPropTextField();
        label.setLayout(new BorderLayout());
        label.setBorder(BorderFactory.createEmptyBorder());
        if (parent != null) {
            label.addActionListener(parent);
        }
        label.setFont(font);
        return label;
    }

    public static String getFullPathComponent(Component comp) {
        StringBuilder b = new StringBuilder();
        getFullPathComponent(comp, b);
        return b.toString();
    }

    public static void getFullPathComponent(Component comp, StringBuilder b) {
        if (b.length() != 0) {
            b.append(".");
        }
        if (comp != null && comp instanceof OrGuiComponent) {
            String title = ((OrGuiComponent) comp).getVarName();
            if (title == null) {
                title = ((OrGuiComponent) comp).getClass().toString().replaceAll(".*\\.", "") + "("
                        + ((OrGuiComponent) comp).getUUID() + ")";
            }
            b.append(title);
            getFullPathComponent(comp.getParent(), b);
        }

    }

    public static String getFullPathComponent(OrPanel.Spacer sp) {
        StringBuilder b = new StringBuilder();
        b.append(sp.getName());
        if (sp.getParent() instanceof OrGuiComponent) {
            getFullPathComponent(sp.getParent(), b);
        }
        return b.toString();
    }
}