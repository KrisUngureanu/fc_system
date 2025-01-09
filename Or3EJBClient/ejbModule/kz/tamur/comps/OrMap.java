package kz.tamur.comps;

import kz.tamur.comps.models.MapPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.*;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.util.Pair;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 27.03.2009
 * Time: 9:52:37
 * To change this template use File | Settings | File Templates.
 */
public class OrMap extends JPanel implements OrGuiComponent, MouseListener, MouseMotionListener {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new MapPropertyRoot();
    private static Color[] COLORS = {
        new Color(0xFF9E3E),
        new Color(0xFFF188),
        new Color(0x55BF55),
        new Color(0xffb2b2),
        new Color(0xFFF188),
        new Color(0xffb2b2),
        new Color(0x55BF55),
        new Color(0xFFF188),
        new Color(0xFF9E3E),
        new Color(0x55BF55),
        new Color(0xFFF188),
        new Color(0xFF9E3E),
        new Color(0xffb2b2),
        new Color(0x55BF55),
        new Color(0xFF0000),
        new Color(0xFF0000),
    };

    private static String[] TITLES = {
        "Западно-Казахстанская",
        "Актюбинская",
        "Костанайская",
        "Северо-Казахстанская",
        "Акмолинская",
        "Карагандинская",
        "Павлодарская",
        "Восточно-Казахстанская",
        "Алматинская",
        "Жамбылская",
        "Южно-Казахстанская",
        "Кызылординская",
        "Мангистауская",
        "Атырауская",
        "г.Астана",
        "г.Алматы",
    };

    private static int[][] TPOSS = {
        {45, 63},
        {108,80},
        {149,36},
        {190,23},
        {192,54},
        {200,102},
        {238,44},
        {286,80},
        {271,130},
        {219,146},
        {184,168},
        {148,134},
        {58,128},
        {51,94},
        {206,54},
        {263,146},
    };

    private static int[][] CIRCLE_DATA = {
            {203,54,6},
            {260,146,6},
    };

    private static int[][] DATA = {
        // Западно-Казахстанская
        {14,14,10,16,13,25,30,34,33,39,52,57,63,72,76,78,76,70,69,62,57,52,49,46,41,36,33,26},
        {73,71,68,58,52,46,55,55,47,45,37,42,40,47,52,63,76,67,70,71,77,72,72,75,75,77,77,73},
        // Актюбинская
        {69,70,76,78,76,79,85,98,108,116,129,130,138,140,136,136,148,149,138,130,125,122,117,106,104,101,85,84,83,76,75,77,77,72,72},
        {70,67,76,63,52,51,58,53,54,61,59,52,59,71,76,81,91,97,104,95,95,102,102,114,114,119,123,112,106,101,96,88,79,77,73},
        // Костанайская
        {130,121,128,126,136,129,132,155,166,168,165,165,159,161,179,161,154,148,136,136,140,138,130},
        {52,45,40,36,33,25,21,16,14,26,33,42,50,57,63,85,84,91,81,76,71,59,52},
        // Северо-Казахстанская
        {166,180,187,198,201,209,220,216,221,196,190,183,184,173,165,165,168},
        {14,13,5,6,21,18,22,26,36,30,26,28,36,42,42,33,26},
        // Акмолинская
        {165,173,184,183,190,196,221,221,199,185,179,161,159},
        {42,42,36,28,26,30,36,54,68,61,63,57,50},
        // Карагандинская
        {148,154,161,179,185,199,221,224,224,233,232,247,252,253,250,250,257,257,231,227,190,175,173,160,138,149},
        {91,84,85,63,61,68,54,56,60,63,66,57,59,63,66,101,99,104,126,117,118,119,117,118,104,97},
        // Павлодарская
        {216,241,240,252,262,256,260,259,253,252,247,232,233,224,224,221,221},
        {26,14,20,28,42,50,56,63,63,59,57,66,63,60,56,54,36},
        // Восточно-Казахстанская
        {262,269,275,281,293,300,304,309,312,320,323,329,325,326,319,322,321,316,302,300,291,281,279,275,263,257,250,250,253,259,260,256},
        {42,53,46,51,46,47,53,54,59,58,55,60,63,67,72,85,88,91,90,111,99,99,97,99,96,99,101,66,63,63,56,50},
        // Алматинская
        {257,263,275,279,281,291,300,303,302,294,281,286,291,288,284,248,242,236,232,231,257},
        {99,96,99,97,99,99,111,112,115,114,122,124,139,148,151,152,137,134,127,126,104},
        // Жамбылькая
        {190,227,231,232,236,242,248,242,235,230,229,210,205,194,196},
        {118,117,126,127,134,137,152,152,148,150,158,156,161,149,141},
        // Южно-Казахстанская
        {175,190,196,194,205,187,186,179,169,167,163,162,178,177,180,181,177,177,174},
        {119,118,141,149,161,178,182,176,177,166,165,163,153,149,146,142,138,131,127},
        // Кызылординская
        {101,104,106,117,122,125,130,138,138,160,173,175,174,177,177,181,180,177,178,163,162,161,153,128,113},
        {119,114,114,102,102,95,95,104,104,118,117,119,127,131,138,142,146,149,153,162,163,156,146,147,130},
        // Мангистауская
        {84,85,78,72,67,58,42,29,67,73,75},
        {112,123,124,167,166,153,156,110,107,108,106},
        // Атырауская
        {14,26,33,36,41,46,49,52,57,62,69,72,72,77,77,75,76,83,84,75,73,67,29,25,21,26,20,14,14},
        {73,73,77,77,75,75,72,72,77,71,70,73,77,79,88,96,101,106,112,106,108,107,110,97,91,92,78,77,73},
    };

    private int maxX = 0;
    private int maxY = 0;

    private double r;

    protected int mode;
    protected Element xml;
    protected boolean isSelected;

    private OrGuiContainer guiParent;
    private int tabIndex;

    private boolean isHelpClick = false;

    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private JButton deleteBtn = ButtonsFactory.createToolButton(
            "Delete", "Удалить значение", true);
    private Color fontColor;
    private byte[] description;
    private String descriptionUID;
    private ImageIcon icon;
    private boolean iconVisible = true;

    private MapAdapter adapter;
    private Polygon[] polygons = new Polygon[14];
    private Circle[] circles = new Circle[2];
    private int selectedIndex = -1;
	private String varName;

    public OrMap(Element xml, int mode, OrFrame frame) {
        super();
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK);

        setFocusable(true);

        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);

        updateProperties();
        if (mode == Mode.RUNTIME) {
            try {
                adapter = new MapAdapter(frame, this, false);
            } catch (KrnException e) {
                e.printStackTrace();
            }
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        } else {
            setEnabled(false);
        }

        for (int i = 0; i < DATA.length - 1; i += 2) {
            for (int j = 0; j < DATA[i].length; j++) {
                if (DATA[i][j] > maxX) {
                    maxX = DATA[i][j];
                }
                if (DATA[i + 1][j] > maxY) {
                    maxY = DATA[i + 1][j];
                }
            }

            polygons[i/2] = new Polygon(DATA[i], DATA[i+1], DATA[i].length);
        }
        for (int i = 0; i < circles.length; i++) {
            int xs = (int)Math.floor(CIRCLE_DATA[i][0]);
            int ys = (int)Math.floor(CIRCLE_DATA[i][1]);
            int rs = (int)Math.floor(CIRCLE_DATA[i][2]);

            circles[i] = new Circle(xs, ys, rs);
        }

        addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                Dimension sz = getSize();
                double rx = 1.0 * sz.width / maxX;
                double ry = 1.0 * sz.height / maxY;
                r = Math.min(rx, ry);
                for (int i = 0; i < polygons.length; i++) {
                    int[] xs = new int[DATA[i*2].length];
                    int[] ys = new int[DATA[i*2].length];
                    for (int j = 0; j < DATA[i*2].length; j++) {
                        xs[j] = (int)Math.floor(DATA[i*2][j] * r);
                        ys[j] = (int)Math.floor(DATA[i*2 + 1][j] * r);
                    }
                    polygons[i] = new Polygon(xs, ys, xs.length);
                }
                for (int i = 0; i < circles.length; i++) {
                    int xs = (int)Math.floor(CIRCLE_DATA[i][0] * r);
                    int ys = (int)Math.floor(CIRCLE_DATA[i][1] * r);
                    int rs = (int)Math.floor(CIRCLE_DATA[i][2] * r);

                    circles[i] = new Circle(xs, ys, rs);
                }
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);

        Font f = new Font("Tahoma", Font.PLAIN, 12);
        Font fb = new Font("Tahoma", Font.BOLD, 16);
        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics fmb = g.getFontMetrics(fb);

        //g2.setStroke(new BasicStroke(2));
        if (mode == Mode.RUNTIME) {
            OrRef indexRef = adapter.getIndexRef();
            OrRef colorRef = adapter.getColorRef();
            OrRef titleRef = adapter.getTitleRef();
            OrRef valueRef = adapter.getValueRef();
            int selectedRefIndex = -1;

            int tableRefIndex = adapter.getRef().getIndex();
            int tableIndex = -1;
            if (tableRefIndex > -1) {
                Object obj = indexRef.getValue(0, tableRefIndex);
                tableIndex = (obj instanceof Number) ? ((Number)obj).intValue() : -1;
            }

            for (int i=0; i<indexRef.getItems(0).size(); i++) {
                Object obj = indexRef.getValue(0, i);
                int index = (obj instanceof Number) ? ((Number)obj).intValue() : -1;
                if (index > -1 && index == selectedIndex) {
                    selectedRefIndex = i;
                }
                if (index > -1 && index != selectedIndex && index != tableIndex && index < polygons.length) {
                    obj = (colorRef != null) ? colorRef.getValue(0, i) : null;
                    Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                    g.setColor(color);
                    g.fillPolygon(polygons[index]);
                    g.setColor(Color.darkGray);

                    g.drawPolygon(polygons[index]);
                }
            }
            if (selectedRefIndex != -1 && selectedIndex < polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, selectedRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                g.fillPolygon(polygons[selectedIndex]);
                g.setColor(Color.red);
                g.drawPolygon(polygons[selectedIndex]);
            }
            if (tableIndex != -1 && tableIndex < polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, tableRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                g.fillPolygon(polygons[tableIndex]);
                g.setColor(Color.red);
                g.drawPolygon(polygons[tableIndex]);
            }
            for (int i=0; i<indexRef.getItems(0).size(); i++) {
                Object obj = indexRef.getValue(0, i);
                int index = (obj instanceof Number) ? ((Number)obj).intValue() : -1;

                if (index >= polygons.length && index != selectedIndex && index != tableIndex) {
                    obj = (colorRef != null) ? colorRef.getValue(0, i) : null;
                    Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                    g.setColor(color);
                    Circle c = circles[index-polygons.length];
                    g.fillOval(c.x, c.y, c.r, c.r);
                    g.setColor(Color.darkGray);
                    g.drawOval(c.x, c.y, c.r, c.r);
                }
            }
            if (selectedRefIndex != -1 && selectedIndex >= polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, selectedRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                Circle c = circles[selectedIndex-polygons.length];
                g.fillOval(c.x, c.y, c.r, c.r);
                g.setColor(Color.red);
                g.drawOval(c.x, c.y, c.r, c.r);
            }
            if (tableIndex != -1 && tableIndex >= polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, tableRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number)obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                Circle c = circles[tableIndex-polygons.length];
                g.fillOval(c.x, c.y, c.r, c.r);
                g.setColor(Color.red);
                g.drawOval(c.x, c.y, c.r, c.r);
            }
            if (r >= 2) {
                g.setColor(Color.darkGray);
                int delta = 5;
                for (int i=0; i<indexRef.getItems(0).size(); i++) {
                    Object obj = indexRef.getValue(0, i);
                    int index = (obj instanceof Number) ? ((Number)obj).intValue() : -1;
                    if (index > -1 && index != selectedIndex) {
                        obj = (titleRef != null) ? titleRef.getValue(titleRef.getLangId(), i) : null;
                        String title = (obj instanceof String) ? obj.toString() : "----";

                        obj = (valueRef != null) ? valueRef.getValue(titleRef.getLangId(), i) : null;
                        String value = (obj instanceof String) ? obj.toString() : "----";

                        if (i == tableRefIndex) {
                            Rectangle b = fmb.getStringBounds(title, g).getBounds();
                            Rectangle b2 = fmb.getStringBounds(value, g).getBounds();
                            int tx, ty, tx2, ty2;
                            if (index < polygons.length) {
                                tx = (int)Math.round(TPOSS[index][0] * r) - b.width / 2;
                                ty = (int)Math.round(TPOSS[index][1] * r) - (b.height + b2.height + delta) / 2;

                                tx2 = (int)Math.round(TPOSS[index][0] * r) - b2.width / 2;
                                ty2 = (int)Math.round(TPOSS[index][1] * r) - (b2.height - b.height - delta) / 2;
                            } else {
                                tx = (int)Math.round(TPOSS[index][0] * r);
                                ty = (int)Math.round(TPOSS[index][1] * r);

                                tx2 = (int)Math.round(TPOSS[index][0] * r) + b.width / 2 - b2.width / 2;
                                ty2 = (int)Math.round(TPOSS[index][1] * r) + b.height + delta;
                            }

                            g.setFont(fb);
                            g.drawString(title, tx, ty);
                            //g.setFont(fb);
                            g.drawString(value, tx2, ty2);
                        } else {
                            Rectangle b = fm.getStringBounds(title, g).getBounds();
                            Rectangle b2 = fmb.getStringBounds(value, g).getBounds();
                            int tx, ty, tx2, ty2;
                            if (index < polygons.length) {
                                tx = (int)Math.round(TPOSS[index][0] * r) - b.width / 2;
                                ty = (int)Math.round(TPOSS[index][1] * r) - (b.height + b2.height + delta) / 2;

                                tx2 = (int)Math.round(TPOSS[index][0] * r) - b2.width / 2;
                                ty2 = (int)Math.round(TPOSS[index][1] * r) - (b2.height - b.height - delta) / 2;
                            } else {
                                tx = (int)Math.round(TPOSS[index][0] * r);
                                ty = (int)Math.round(TPOSS[index][1] * r);

                                tx2 = (int)Math.round(TPOSS[index][0] * r) + b.width / 2 - b2.width / 2;
                                ty2 = (int)Math.round(TPOSS[index][1] * r) + b.height + delta;
                            }

                            g.setFont(f);
                            g.drawString(title, tx, ty);
                            g.setFont(fb);
                            g.drawString(value, tx2, ty2);
                        }
                    }
                }
                if (selectedRefIndex != -1) {
                    Object obj = (titleRef != null) ? titleRef.getValue(titleRef.getLangId(), selectedRefIndex) : null;
                    String title = (obj instanceof String) ? obj.toString() : "----";

                    obj = (valueRef != null) ? valueRef.getValue(titleRef.getLangId(), selectedRefIndex) : null;
                    String value = (obj instanceof String) ? obj.toString() : "----";

                    g.setColor(Color.red);
                    if (selectedRefIndex == tableRefIndex ) {
                        Rectangle b = fmb.getStringBounds(title, g).getBounds();
                        Rectangle b2 = fmb.getStringBounds(value, g).getBounds();

                        int tx, ty, tx2, ty2;
                        if (selectedIndex < polygons.length) {
                            tx = (int)Math.round(TPOSS[selectedIndex][0] * r) - b.width / 2;
                            ty = (int)Math.round(TPOSS[selectedIndex][1] * r) - (b.height + b2.height + delta) / 2;

                            tx2 = (int)Math.round(TPOSS[selectedIndex][0] * r) - b2.width / 2;
                            ty2 = (int)Math.round(TPOSS[selectedIndex][1] * r) - (b2.height - b.height - delta) / 2;
                        } else {
                            tx = (int)Math.round(TPOSS[selectedIndex][0] * r);
                            ty = (int)Math.round(TPOSS[selectedIndex][1] * r);

                            tx2 = (int)Math.round(TPOSS[selectedIndex][0] * r) + b.width / 2 - b2.width / 2;
                            ty2 = (int)Math.round(TPOSS[selectedIndex][1] * r) + b.height + delta;
                        }

                        g.setFont(fb);
                        g.drawString(title, tx, ty);
                        g.drawString(value, tx2, ty2);
                    } else {
                        Rectangle b = fm.getStringBounds(title, g).getBounds();
                        Rectangle b2 = fmb.getStringBounds(value, g).getBounds();

                        int tx, ty, tx2, ty2;
                        if (selectedIndex < polygons.length) {
                            tx = (int)Math.round(TPOSS[selectedIndex][0] * r) - b.width / 2;
                            ty = (int)Math.round(TPOSS[selectedIndex][1] * r) - (b.height + b2.height + delta) / 2;

                            tx2 = (int)Math.round(TPOSS[selectedIndex][0] * r) - b2.width / 2;
                            ty2 = (int)Math.round(TPOSS[selectedIndex][1] * r) - (b2.height - b.height - delta) / 2;
                        } else {
                            tx = (int)Math.round(TPOSS[selectedIndex][0] * r);
                            ty = (int)Math.round(TPOSS[selectedIndex][1] * r);

                            tx2 = (int)Math.round(TPOSS[selectedIndex][0] * r) + b.width / 2 - b2.width / 2;
                            ty2 = (int)Math.round(TPOSS[selectedIndex][1] * r) + b.height + delta;
                        }

                        g.setFont(f);
                        g.drawString(title, tx, ty);
                        g.setFont(fb);
                        g.drawString(value, tx2, ty2);
                    }
                }
            }
        } else {

            int cnt = DATA.length;
            for (int i = 0; i < cnt / 2; i++) {
                g.setColor(COLORS[i]);
                g.fillPolygon(polygons[i]);
                g.setColor(Color.darkGray);
                g.drawPolygon(polygons[i]);

            }
            for (int i = 0; i < CIRCLE_DATA.length; i++) {
                g.setColor(COLORS[polygons.length + i]);
                Circle c = circles[i];
                g.fillOval(c.x, c.y, c.r, c.r);
                g.setColor(Color.darkGray);
                g.drawOval(c.x, c.y, c.r, c.r);
            }

            if (r >= 2) {
                for (int i = 0; i < TITLES.length; i ++) {
                    Rectangle b = fm.getStringBounds(TITLES[i], g).getBounds();
                    int tx = (int)Math.round(TPOSS[i][0] * r) - b.width / 2;
                    int ty = (int)Math.round(TPOSS[i][1] * r) - b.height / 2;
                    g.drawString(TITLES[i], tx, ty);
                }
            }
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
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        //Utils.processBorderProperties(this, frame);
        PropertyNode prop = value.getProperty();
        if ("title".equals(prop.getName())) {
            Pair p = value.resourceStringValue();
            //setText((String)p.second);
        } else if ("fontG".equals(prop.getName())) {
            setFont(value.fontValue());
        } else if ("fontColor".equals(prop.getName())) {
            fontColor = value.colorValue();
            setForeground(fontColor);
        } else if ("opaque".equals(prop.getName())) {
            setOpaque(value.booleanValue());
        } else if ("backgroundColor".equals(prop.getName())) {
            setBackground(value.colorValue());
        }
    }

    public Element getXml() {
        return xml;
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public void setLangId(long langId) {
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
        PropertyValue pv = null;
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            title = (String)p.second;
            titleUID = (String)p.first;
            //setText(title);
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

        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForeground(fontColor);
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setOpaque(pv.booleanValue());
        } else {
            setOpaque(true);
            setPropertyValue(new PropertyValue(true,
                    pn.getChild("opaque")));
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            //setContentAreaFilled(false);
            //setOpaque(true);
            setBackground(pv.colorValue());
            //setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        }
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }
    }

    public int getMode() {
        return mode;
    }

    public boolean isCopy() {
        return false;
    }

    public void setCopy(boolean copy) {
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.guiParent = parent;
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

    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public MapAdapter getAdapter() {
        return adapter;
    }

    public void mouseClicked(MouseEvent e) {
        if (selectedIndex != -1) {
            OrRef indexRef = adapter.getIndexRef();
            int selectedRefIndex = -1;

            for (int i=0; i<indexRef.getItems(0).size(); i++) {
                Object obj = indexRef.getValue(0, i);
                int index = (obj instanceof Number) ? ((Number)obj).intValue() : -1;
                if (index > -1 && index == selectedIndex) {
                    selectedRefIndex = i;
                    break;
                }
            }

            if (e.getClickCount() > 1)
                adapter.actionPerformed(selectedRefIndex);
            else {
            	adapter.getRef().absolute(selectedRefIndex, this);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseDragged(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        for (int i = 0; i < circles.length; i++) {
            if (circles[i].contains(x, y)) {
                if (selectedIndex != polygons.length + i) {
                    selectedIndex = polygons.length + i;

                    setCursor(Constants.HAND_CURSOR);
                    repaint();
                }
                return;
            }
        }
        for (int i = 0; i < polygons.length; i++) {
            if (polygons[i].contains(x, y)) {
                if (selectedIndex != i) {
                    selectedIndex = i;

                    setCursor(Constants.HAND_CURSOR);
                    repaint();
                }
                return;
            }
        }
        if (selectedIndex != -1) {
            selectedIndex = -1;
            setCursor(Constants.DEFAULT_CURSOR);
            repaint();
        }
    }

    public class Circle {
        public int x;
        public int y;
        public int r;
        public int radius;
        private int cx;
        private int cy;
        private int sqrad;

        public Circle(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.radius = r/2;
            this.cx = this.x + this.radius;
            this.cy = this.y + this.radius;
            this.sqrad = this.radius*this.radius;
        }

        public boolean contains(int x, int y) {
            int rx = x - this.cx;
            int ry = y - this.cy;
            return (rx * rx + ry * ry <= sqrad);
        }
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
