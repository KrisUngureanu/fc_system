package kz.tamur.web.component;

import kz.tamur.comps.models.MapPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.rt.adapters.*;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.UpdateContent;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.controller.WebController;
import kz.tamur.or3.client.comps.interfaces.OrMapComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.imageio.ImageIO;

public class OrWebMap extends WebComponent implements JSONComponent, JSONCellComponent, OrMapComponent {
    public static PropertyNode PROPS = new MapPropertyRoot();
    private Polygon[] polygons = new Polygon[14];
    private Circle[] circles = new Circle[2];
    // private Polygon selected = null;
    private static int[][] TPOSS = { { 45, 63 }, { 108, 80 }, { 149, 36 }, { 190, 23 }, { 192, 54 }, { 200, 102 }, { 238, 44 },
            { 286, 80 }, { 271, 130 }, { 219, 146 }, { 184, 168 }, { 148, 134 }, { 58, 128 }, { 51, 94 }, { 206, 54 },
            { 263, 146 }, };

    private static int[][] CIRCLE_DATA = { { 203, 54, 6 }, { 260, 146, 6 }, };

    private static int[][] DATA = {
        // �������-�������������
        {14,14,10,16,13,25,30,34,33,39,52,57,63,72,76,78,76,70,69,62,57,52,49,46,41,36,33,26},
        {73,71,68,58,52,46,55,55,47,45,37,42,40,47,52,63,76,67,70,71,77,72,72,75,75,77,77,73},
        // �����������
        {69,70,76,78,76,79,85,98,108,116,129,130,138,140,136,136,148,149,138,130,125,122,117,106,104,101,85,84,83,76,75,77,77,72,72},
        {70,67,76,63,52,51,58,53,54,61,59,52,59,71,76,81,91,97,104,95,95,102,102,114,114,119,123,112,106,101,96,88,79,77,73},
        // ������������
        {130,121,128,126,136,129,132,155,166,168,165,165,159,161,179,161,154,148,136,136,140,138,130},
        {52,45,40,36,33,25,21,16,14,26,33,42,50,57,63,85,84,91,81,76,71,59,52},
        // ������-�������������
        {166,180,187,198,201,209,220,216,221,196,190,183,184,173,165,165,168},
        {14,13,5,6,21,18,22,26,36,30,26,28,36,42,42,33,26},
        // �����������
        {165,173,184,183,190,196,221,221,199,185,179,161,159},
        {42,42,36,28,26,30,36,54,68,61,63,57,50},
        // ��������������
        {148,154,161,179,185,199,221,224,224,233,232,247,252,253,250,250,257,257,231,227,190,175,173,160,138,149},
        {91,84,85,63,61,68,54,56,60,63,66,57,59,63,66,101,99,104,126,117,118,119,117,118,104,97},
        // ������������
        {216,241,240,252,262,256,260,259,253,252,247,232,233,224,224,221,221},
        {26,14,20,28,42,50,56,63,63,59,57,66,63,60,56,54,36},
        // ��������-�������������
        {262,269,275,281,293,300,304,309,312,320,323,329,325,326,319,322,321,316,302,300,291,281,279,275,263,257,250,250,253,259,260,256},
        {42,53,46,51,46,47,53,54,59,58,55,60,63,67,72,85,88,91,90,111,99,99,97,99,96,99,101,66,63,63,56,50},
        // �����������
        {257,263,275,279,281,291,300,303,302,294,281,286,291,288,284,248,242,236,232,231,257},
        {99,96,99,97,99,99,111,112,115,114,122,124,139,148,151,152,137,134,127,126,104},
        // ����������
        {190,227,231,232,236,242,248,242,235,230,229,210,205,194,196},
        {118,117,126,127,134,137,152,152,148,150,158,156,161,149,141},
        // ����-�������������
        {175,190,196,194,205,187,186,179,169,167,163,162,178,177,180,181,177,177,174},
        {119,118,141,149,161,178,182,176,177,166,165,163,153,149,146,142,138,131,127},
        // ��������������
        {101,104,106,117,122,125,130,138,138,160,173,175,174,177,177,181,180,177,178,163,162,161,153,128,113},
        {119,114,114,102,102,95,95,104,104,118,117,119,127,131,138,142,146,149,153,162,163,156,146,147,130},
        // �������������
        {84,85,78,72,67,58,42,29,67,73,75},
        {112,123,124,167,166,153,156,110,107,108,106},
        // ����������
        {14,26,33,36,41,46,49,52,57,62,69,72,72,77,77,75,76,83,84,75,73,67,29,25,21,26,20,14,14},
        {73,73,77,77,75,75,72,72,77,71,70,73,77,79,88,96,101,106,112,106,108,107,110,97,91,92,78,77,73},
    };

    private int maxX = 0;
    private int maxY = 0;

    private double r;

    private OrGuiContainer guiParent;
    private boolean isCopy;
    private int tabIndex;

    private boolean isClearBtnExists;
    private boolean isHelpClick = false;

    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private MapAdapter adapter;
    private String hpcImage = "HyperPopCol";
    private DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private boolean ifcLock = false;
    private WebFrame frm;
    private boolean valueChanged = false;

    private File dstMap;// , dstSel;
    String oldName;// , oldName2;

    OrWebMap(Element xml, int mode, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        // description = PropertyHelper.getDescription(this);
        updateProperties();
        if (mode == Mode.RUNTIME) {
            for (int i = 0; i < DATA.length - 1; i += 2) {
                for (int j = 0; j < DATA[i].length; j++) {
                    if (DATA[i][j] > maxX) {
                        maxX = DATA[i][j];
                    }
                    if (DATA[i + 1][j] > maxY) {
                        maxY = DATA[i + 1][j];
                    }
                }
            }
            // repaint();
            PropertyNode pn = getProperties().getChild("pov").getChild("ifcLock");
            PropertyValue pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                ifcLock = pv.booleanValue();
            }
            adapter = new MapAdapter(frame, this, false);
        }
        this.xml = null;
    }

    public String actionPerformed(long fid, int index) {
        select(index);
        frm = (WebFrame) adapter.getPopupFrame();
        int configNumber = frm.getSession().getConfigNumber();
        if (frm == null) {
            // @todo MessageFactory ������� ����� � web
            String mess = "�� ����� ��������� ���������!";
            log.debug(mess);
            StringBuffer out = new StringBuffer();
            out.append("<html>");
            out.append("<head>");
            out.append("<title>");
            out.append(mess);
            out.append("</title>");
            out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
            out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash=" + UpdateContent.tocHash[configNumber]
                    + "\" type=\"text/css\" media=\"screen\" />");
            out.append("</head>");
            out.append("<body>");
            out.append(mess);
            out.append("</body></html>");

            return out.toString();
        }
        return frm.setSize(id, fid);
    }

    public void setValue(String value) {
        if ("YES".equals(value)) {
            adapter.okPressed(frm);
        } else if ("CLEAR".equals(value)) {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
            mgr.releaseInterface(false);
        } else {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
            mgr.releaseInterface(false);
        }
    }

    public void valueChanged() {
        String name = dstMap.getName();
        if (!name.equals(oldName)) {
            oldName = name;
            sendChangeProperty("imgSrc", name);
        }
    }

    public void setVerticalAlignment(int center) {
    }

    public GridBagConstraints getConstraints() {
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml, id, frame);
        }
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            // setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        updateProperties(PROPS);
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            // setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            // fontColor = pv.colorValue();
            // setForeground(fontColor);
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            // setOpaque(pv.booleanValue());
        } else {
            // setOpaque(true);
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            // setContentAreaFilled(false);
            // setOpaque(true);
            setBackground(pv.colorValue());
            // setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        }
        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.guiParent = parent;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public MapAdapter getAdapter() {
        return adapter;
    }

    public void refresh() {
        repaint();
    }

    private void repaint() {
        valueChanged = true;
        File dir = WebController.WEB_IMAGES_DIRECTORY;

        int w = getWidth() > 0 ? getWidth() : 1000;
        int h = getHeight() > 0 ? getHeight() : 800;

        int selectedIndex = adapter.getRef().getIndex();
        // String fileName = "mapw"+w+"h"+h+"l"+frame.getDataLang().id+"i"+selectedIndex+".png";
        // new File(dir, fileName);
        // String fileName2 = "mapselw"+w+"h"+h+"i"+selectedIndex+".png";
        // dstSel = new File(dir, fileName2);
        if (polygons[0] == null) {
            double rx = 1.0 * w / maxX;
            double ry = 1.0 * h / maxY;
            r = Math.min(rx, ry);
            for (int i = 0; i < polygons.length; i++) {
                int[] xs = new int[DATA[i * 2].length];
                int[] ys = new int[DATA[i * 2].length];
                for (int j = 0; j < DATA[i * 2].length; j++) {
                    xs[j] = (int) Math.floor(DATA[i * 2][j] * r);
                    ys[j] = (int) Math.floor(DATA[i * 2 + 1][j] * r);
                }
                polygons[i] = new Polygon(xs, ys, xs.length);
            }
        }
        if (circles[0] == null) {
            for (int i = 0; i < circles.length; i++) {
                int xs = (int) Math.floor((CIRCLE_DATA[i][0] - CIRCLE_DATA[i][2] / 2) * r);
                int ys = (int) Math.floor((CIRCLE_DATA[i][1] - CIRCLE_DATA[i][2] / 2) * r);
                int rs = (int) Math.floor(CIRCLE_DATA[i][2] * r);

                circles[i] = new Circle(xs, ys, rs);
            }
        }
        // if (!dstMap.exists()) {
        try {
            OrRef indexRef = adapter.getIndexRef();
            OrRef colorRef = adapter.getColorRef();
            OrRef titleRef = adapter.getTitleRef();
            OrRef valueRef = adapter.getValueRef();

            int count = titleRef.getItems(titleRef.getLangId()).size();

            if (valueRef != null && valueRef.getItems(valueRef.getLangId()).size() == count && indexRef != null
                    && indexRef.getItems(0).size() == count && colorRef != null && colorRef.getItems(0).size() == count) {

                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics g = img.getGraphics();
                paint(g);

                g.dispose();

                try {
                    dstMap = Funcs.createTempFile("map", ".png", dir);
                    ((WebFrame) frame).getSession().deleteOnExit(dstMap);
                } catch (Exception e) {
                	log.error(e, e);
                }
                FileOutputStream fos = new FileOutputStream(dstMap);

                ImageIO.write(img, "png", fos);

                fos.close();
            }
        } catch (Exception e) {
        	log.error(e, e);
        }
        // }
        /*
         * if (!dstSel.exists()) {
         * try {
         * BufferedImage img = new BufferedImage(w, h,
         * BufferedImage.TYPE_INT_ARGB);
         * Graphics g = img.getGraphics();
         * paintSelection(g);
         * 
         * g.dispose();
         * 
         * FileOutputStream fos = new FileOutputStream(dstSel);
         * 
         * ImageIO.write(img, "png", fos);
         * 
         * fos.close();
         * } catch (Exception e) {
         * e.printStackTrace();
         * }
         * }
         */
    }

    public void paint(Graphics g) {
        Font f = new Font("Tahoma", Font.BOLD, 12);
        Font fb = new Font("Tahoma", Font.BOLD, 16);
        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics fmb = g.getFontMetrics(fb);

        // g2.setStroke(new BasicStroke(2));
        if (mode == Mode.RUNTIME) {
            OrRef indexRef = adapter.getIndexRef();
            OrRef colorRef = adapter.getColorRef();
            OrRef titleRef = adapter.getTitleRef();
            OrRef valueRef = adapter.getValueRef();

            int selectedRefIndex = adapter.getRef().getIndex();
            int selectedIndex = -1;
            if (selectedRefIndex > -1) {
                Object obj = indexRef.getValue(0, selectedRefIndex);
                selectedIndex = (obj instanceof Number) ? ((Number) obj).intValue() : -1;
            }

            for (int i = 0; i < indexRef.getItems(0).size(); i++) {
                Object obj = indexRef.getValue(0, i);
                int index = (obj instanceof Number) ? ((Number) obj).intValue() : -1;

                if (index > -1 && index < polygons.length && index != selectedIndex) {
                    obj = (colorRef != null) ? colorRef.getValue(0, i) : null;
                    Color color = (obj instanceof Number) ? new Color(((Number) obj).intValue()) : Color.ORANGE;

                    g.setColor(color);
                    g.fillPolygon(polygons[index]);
                    g.setColor(Color.darkGray);
                    g.drawPolygon(polygons[index]);
                }
            }
            if (selectedRefIndex != -1 && selectedIndex < polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, selectedRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number) obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                g.fillPolygon(polygons[selectedIndex]);
                g.setColor(Color.red);
                g.drawPolygon(polygons[selectedIndex]);
                // g.drawPolygon(selected.xpoints, selected.ypoints, selected.npoints);
            }
            for (int i = 0; i < indexRef.getItems(0).size(); i++) {
                Object obj = indexRef.getValue(0, i);
                int index = (obj instanceof Number) ? ((Number) obj).intValue() : -1;

                if (index >= polygons.length && index != selectedIndex) {
                    obj = (colorRef != null) ? colorRef.getValue(0, i) : null;
                    Color color = (obj instanceof Number) ? new Color(((Number) obj).intValue()) : Color.ORANGE;

                    g.setColor(color);
                    Circle c = circles[index - polygons.length];
                    g.fillOval(c.x, c.y, c.r, c.r);
                    g.setColor(Color.darkGray);
                    g.drawOval(c.x, c.y, c.r, c.r);
                }
            }
            if (selectedRefIndex != -1 && selectedIndex >= polygons.length) {
                Object obj = (colorRef != null) ? colorRef.getValue(0, selectedRefIndex) : null;
                Color color = (obj instanceof Number) ? new Color(((Number) obj).intValue()) : Color.ORANGE;

                g.setColor(color);
                Circle c = circles[selectedIndex - polygons.length];
                g.fillOval(c.x, c.y, c.r, c.r);
                g.setColor(Color.red);
                g.drawOval(c.x, c.y, c.r, c.r);
                // g.drawPolygon(selected.xpoints, selected.ypoints, selected.npoints);
            }
            if (r >= 2) {
                g.setColor(Color.darkGray);
                int delta = 5;
                for (int i = 0; i < indexRef.getItems(0).size(); i++) {
                    Object obj = indexRef.getValue(0, i);
                    int index = (obj instanceof Number) ? ((Number) obj).intValue() : -1;
                    if (index > -1 && index != selectedIndex) {
                        obj = (titleRef != null) ? titleRef.getValue(titleRef.getLangId(), i) : null;
                        String title = (obj instanceof String) ? obj.toString() : "----";

                        obj = (valueRef != null) ? valueRef.getValue(titleRef.getLangId(), i) : null;
                        String value = (obj instanceof String) ? obj.toString() : "----";

                        Rectangle b = fm.getStringBounds(title, g).getBounds();
                        Rectangle b2 = fmb.getStringBounds(value, g).getBounds();
                        int tx, ty, tx2, ty2;
                        if (index < polygons.length) {
                            tx = (int) Math.round(TPOSS[index][0] * r) - b.width / 2;
                            ty = (int) Math.round(TPOSS[index][1] * r) - (b.height + b2.height + delta) / 2;

                            tx2 = (int) Math.round(TPOSS[index][0] * r) - b2.width / 2;
                            ty2 = (int) Math.round(TPOSS[index][1] * r) - (b2.height - b.height - delta) / 2;
                        } else {
                            tx = (int) Math.round(TPOSS[index][0] * r);
                            ty = (int) Math.round(TPOSS[index][1] * r);

                            tx2 = (int) Math.round(TPOSS[index][0] * r) + b.width / 2 - b2.width / 2;
                            ty2 = (int) Math.round(TPOSS[index][1] * r) + b.height + delta;
                        }
                        g.setFont(f);
                        g.drawString(title, tx, ty);
                        g.setFont(fb);
                        g.drawString(value, tx2, ty2);
                    }
                }
                if (selectedRefIndex != -1) {
                    Object obj = (titleRef != null) ? titleRef.getValue(titleRef.getLangId(), selectedRefIndex) : null;
                    String title = (obj instanceof String) ? obj.toString() : "----";

                    obj = (valueRef != null) ? valueRef.getValue(titleRef.getLangId(), selectedRefIndex) : null;
                    String value = (obj instanceof String) ? obj.toString() : "----";

                    // g.setColor(Color.red);
                    Rectangle b = fmb.getStringBounds(title, g).getBounds();
                    Rectangle b2 = fmb.getStringBounds(value, g).getBounds();

                    int tx, ty, tx2, ty2;
                    if (selectedIndex < polygons.length) {
                        tx = (int) Math.round(TPOSS[selectedIndex][0] * r) - b.width / 2;
                        ty = (int) Math.round(TPOSS[selectedIndex][1] * r) - (b.height + b2.height + delta) / 2;

                        tx2 = (int) Math.round(TPOSS[selectedIndex][0] * r) - b2.width / 2;
                        ty2 = (int) Math.round(TPOSS[selectedIndex][1] * r) - (b2.height - b.height - delta) / 2;
                    } else {
                        tx = (int) Math.round(TPOSS[selectedIndex][0] * r);
                        ty = (int) Math.round(TPOSS[selectedIndex][1] * r);

                        tx2 = (int) Math.round(TPOSS[selectedIndex][0] * r) + b.width / 2 - b2.width / 2;
                        ty2 = (int) Math.round(TPOSS[selectedIndex][1] * r) + b.height + delta;
                    }

                    g.setFont(fb);
                    g.drawString(title, tx, ty);
                    g.drawString(value, tx2, ty2);
                }
            }
        }
    }

    /*
     * public void paintSelection(Graphics g) {
     * int selectedIndex = adapter.getRef().getIndex();
     * 
     * if (selectedIndex != -1) {
     * g.setColor(Color.darkGray);
     * g.drawPolygon(polygons[selectedIndex]);
     * g.drawPolygon(selected);
     * }
     * }
     */

    public void select(int ind) {
        OrRef indexRef = adapter.getIndexRef();
        int selectedRefIndex = -1;

        for (int i = 0; i < indexRef.getItems(0).size(); i++) {
            Object obj = indexRef.getValue(0, i);
            int index = (obj instanceof Number) ? ((Number) obj).intValue() : -1;
            if (index > -1 && index == ind) {
                selectedRefIndex = i;
                break;
            }
        }

        adapter.getRef().absolute(selectedRefIndex, this);
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if (dstMap == null) {
            repaint();
        }


        if (dstMap != null) {
            String name = dstMap.getName();
            JsonObject img = new JsonObject();
            JsonObject style_ = new JsonObject();
            style_.add("position", "absolute");
            img.add("style", style_);
            img.add("usemap", "#Navigation" + id);
            img.add("src", WebController.PATH_IMG + name);
            property.add("img", img);

            JsonObject map = new JsonObject();
            map.add("name", "Navigation" + id);
            JsonArray areas = new JsonArray();
            for (int i = 0; i < circles.length; i++) {
                JsonObject e = new JsonObject();
                Circle c = circles[i];
                e.add("shape", "circle");
                e.add("onClick", "return mapSelected(this, " + (i + polygons.length) + ");");
                e.add("onDblDlick", "return mapPressed(this, " + (i + polygons.length) + ");");
                e.add("coords", c.x + "," + c.y + "," + c.r);
                areas.add(e);
            }
            JsonArray polygones = new JsonArray();
            for (int i = 0; i < polygons.length; i++) {
                JsonObject e = new JsonObject();
                e.add("shape", "poly");
                e.add("onClick", "return mapSelected(this, " + i + ");");
                e.add("onDblDlick", "return mapPressed(this, " + i + ");");
                StringBuilder b = new StringBuilder();
                for (int j = 0; j < polygons[i].npoints; j++) {
                    if (j == 0)
                        b.append(polygons[i].xpoints[j]).append(",").append(polygons[i].ypoints[j]);
                    else
                        b.append(",").append(polygons[i].xpoints[j]).append(",").append(polygons[i].ypoints[j]);
                }
                e.add("coords", b);
                polygones.add(e);
            }
            map.add("areasCircle", areas);
            map.add("areasPoly", polygones);
            property.add("map", map);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        return obj;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state ) {
        JsonObject obj = addJSON(tid);
        JsonObject property = new JsonObject();
        obj.add("pr", property);
        property.add("row", row);
        property.add("column", column);
        property.add("cellEditable", cellEditable);
        property.add("isSelected", isSelected);
        property.add("state", state);
        return obj;

    }

    public class Circle {
        public int x;
        public int y;
        public int r;

        public Circle(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
}
