package kz.tamur.web.common.webgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.WebUtils;
import kz.tamur.web.controller.WebController;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

/**
 * User: Erik
 * Date: 15.07.2006
 * Time: 11:24:42
 */
public class WebPanel extends WebComponent implements JSONComponent {
    private WebLayout layout;
    // protected List<WebComponent> children;
    private static final WebComponent[] EMPTY_ARRAY = new WebComponent[0];
    protected List<WebComponent> children = new java.util.ArrayList<WebComponent>();
    private int cols = 0;
    private int rows = 0;
    private double totalWeightX;
    private double totalWeightY;
    protected String borderTitle;
    private int[] cellWidths;
    private int[] cellHeights;
    protected int totalSizeX;
    private int totalSizeY;
    private int[] cellWidths2;
    private int[] cellHeights2;
    protected Border borderType;
    private static double heightCoeff = 1;// 0.78;
    private static double maxHeightCoeff = 1;// 0.92;

    protected int positionPict = GridBagConstraints.CENTER;
    protected boolean autoResizePict = true;
    protected String bgImageName = null;
    protected String iconName = null;
    protected boolean isFoundGradient = false;
    protected StringBuilder border = null;

    public WebPanel(Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        children = new java.util.ArrayList<WebComponent>();
    }

    public void setLayout(WebLayout layout) {
        this.layout = layout;
    }

    public void add(WebComponent component, GridBagConstraints cs) {
        children.add(component);
        cols = Math.max(cols, cs.gridx + cs.gridwidth);
        rows = Math.max(rows, cs.gridy + cs.gridheight);
        component.setParent(this);
        component.setConstraints(cs);
    }

    public void add(WebComponent component) {
        children.add(component);
        component.setParent(this);
    }

    public void remove(WebComponent comp) {
        if (comp.parent == this) {
            int count = children.size();
            for (int i = count; --i >= 0;) {
                if (children.get(i) == comp) {
                    children.remove(i);
                }
            }
        }
    }

    public void calculateSize() {
        super.calculateSize();
        if (layout instanceof WebGridBagLayout) {
            calculateTotalWeight();
            calculateCellWidths();
            calculateTotalSizeX();
            calculateCellWidthSizes();
            calculateTotalSizeY();
            calculateCellHeightSizes();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    WebComponent comp = getComponent(i, j);
                    if (comp != null && comp.isVisible()) {
                        comp.calculateSize();
                    }
                }
            }
        }
    }

    private void calculateCellWidths() {
        cellWidths = new int[cols];
        cellHeights = new int[rows];
        for (int j = 0; j < cols; j++) {
            double max = 0;
            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible()) {
                    if (comp.constraints != null && comp.constraints.weightx / comp.constraints.gridwidth > max)
                        max = comp.constraints.weightx / comp.constraints.gridwidth;
                }
            }
            cellWidths[j] = (int) (max * 100 / totalWeightX);
        }

        for (int i = 0; i < rows; i++) {
            double max = 0;
            for (int j = 0; j < cols; j++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible()) {
                    if (comp.constraints != null && comp.constraints.weighty / comp.constraints.gridheight > max)
                        max = comp.constraints.weighty / comp.constraints.gridheight;
                }
            }
            cellHeights[i] = (int) (max * 100 / totalWeightY);
        }
    }

    public int totalCols() {
        return cols;
    }

    public int totalRows() {
        return rows;
    }

    public int totalCompsInRow(int row) {
        int total = 0;
        int j = 0;
        while (j < cols) {
            WebComponent comp = getComponentIn(row, j);
            if (comp != null) {
                j += comp.constraints.gridwidth;
                total++;
            } else {
                total++;
                j++;
            }
        }
        return total;
    }

    public int totalCompsInCol(int col) {
        int total = 0;
        int j = 0;
        while (j < rows) {
            WebComponent comp = getComponentIn(j, col);
            if (comp != null) {
                j += comp.constraints.gridheight;
                total++;
            } else {
                j++;
                total++;
            }
        }
        return total;
    }

    public boolean isLastInRow(int row, WebComponent c) {
        WebComponent comp = getComponentIn(row, cols - c.constraints.gridwidth);
        if (!c.equals(comp))
            return false;
        for (int j = 0; j < cols; j++) {
            comp = getComponentIn(row, j);
            if (comp != null && comp.constraints.weightx > 0)
                return false;
        }
        return true;
    }

    public boolean hasHorizontalWeight() {
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.constraints.weightx > 0)
                    return true;
            }
        }
        return false;
    }

    public boolean hasVerticalWeight() {
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.constraints.weighty > 0)
                    return true;
            }
        }
        return false;
    }

    private void calculateTotalWeight() {
        totalWeightX = 1;
        totalWeightY = 1;
        double total = 0;
        double total2 = 0;

        int approximateScreenWidth = getApproximateWidth();
        int count = 0;
        for (int j = 0; j < cols; j++) {
            double max = 0;
            double max2 = 0;

            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    double t = comp.constraints.weightx / comp.constraints.gridwidth;
                    double t2 = 0;
                    if (t == 0.0) {
                        t2 = 1.0d * comp.getEstimatedWidth() / (approximateScreenWidth * comp.constraints.gridwidth);
                    } else {
                        count++;
                    }
                    if (t > max) {
                        max = t;
                    }
                    if (t2 > max2) {
                        max2 = t2;
                    }
                }
            }
            total += max;
            total2 += max2;
        }
        // Если только один компонент резиновый, то не надо пересчитывать и так нормально отображает
        if (count == 1)
            total2 = 0;

        if (total2 > 0.8)
            total2 = 0.8;

        total = total / (1 - total2);
        if (total > totalWeightX) {
            totalWeightX = total;
        }

        total = 0;
        total2 = 0;
        int approximateScreenHeight = getApproximateHeight();
        count = 0;
        for (int i = 0; i < rows; i++) {
            double max = 0;
            double max2 = 0;
            for (int j = 0; j < cols; j++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    double t = comp.constraints.weighty / comp.constraints.gridheight;

                    double t2 = 0;

                    if (t == 0.0) {
                        t2 = 1.0d * comp.getEstimatedHeight() / (approximateScreenHeight * comp.constraints.gridheight);
                    } else
                        count++;

                    if (t > max)
                        max = t;
                    if (t2 > max2)
                        max2 = t2;
                }
            }
            total += max;
            total2 += max2;
        }
        // Если только один компонент резиновый, то не надо пересчитывать и так нормально отображает
        if (count == 1)
            total2 = 0;
        if (total2 > 0.8)
            total2 = 0.8;
        total = total / (1 - total2);
        if (total > totalWeightY) {
            totalWeightY = total;
        }
    }

    private void calculateCellWidthSizes() {
        cellWidths2 = new int[cols];
        for (int j = 0; j < cols; j++) {
            int max = 0;
            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    int t = comp.getWidth() / comp.constraints.gridwidth;
                    if (t > max)
                        max = t;
                }
            }
            cellWidths2[j] = max;
        }
    }

    private void calculateCellHeightSizes() {
        cellHeights2 = new int[rows];
        for (int i = 0; i < rows; i++) {
            int max = 0;
            for (int j = 0; j < cols; j++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    int t = comp.getHeight() / comp.constraints.gridheight;
                    if (t > max)
                        max = t;
                }
            }
            cellHeights2[i] = max;
        }
    }

    private void calculateTotalSizeX() {
        totalSizeX = 1;
        int total = 0;
        for (int j = 0; j < cols; j++) {
            int max = 0;
            for (int i = 0; i < rows; i++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    int t = comp.getWidth() / comp.constraints.gridwidth;
                    if (t > max)
                        max = t;
                }
            }
            total += max;
        }
        if (total > totalSizeX) {
            totalSizeX = total;
        }
    }

    private void calculateTotalSizeY() {
        totalSizeY = 1;
        int total = 0;
        for (int i = 0; i < rows; i++) {
            int max = 0;
            for (int j = 0; j < cols; j++) {
                WebComponent comp = getComponentIn(i, j);
                if (comp != null && comp.isVisible() && comp.constraints != null) {
                    int t = comp.getHeight() / comp.constraints.gridheight;
                    if (t > max)
                        max = t;
                }
            }
            total += max;
        }
        if (total > totalSizeY) {
            totalSizeY = total;
        }
    }

    public WebComponent getWebComponent(String id) {
        if (id.equals(this.id)) {
            return this;
        }
        for (WebComponent comp : children) {
            if (comp.getWebComponent(id) != null) {
                return comp.getWebComponent(id);
            }
        }
        return null;
    }
    
    public WebComponent getWebComponentUID(String id) {
    	if (id.equals(this.id)) {
            return this;
        }
        for (WebComponent comp : children) {
            if (comp.getWebComponentUID(id) != null) {
                return comp.getWebComponentUID(id);
            }
        }
        return null;
    }

    public WebComponent[] getComponents() {
        return children.toArray(EMPTY_ARRAY);
    }

    public WebComponent getComponent(int index) {
        if (index < children.size() && index > -1) {
            return children.get(index);
        }
        return null;
    }

    public int getComponentIndexByText(String text) {
    	int i = 0;
        for (WebComponent comp : children) {
            if (comp instanceof WebButton) {
            	if (text.equals(((WebButton)comp).getText())) return i;
            }
            i++;
        }
        return -1;
    }

    public int getComponentCount() {
        return children.size();
    }

    private WebComponent getComponent(int y, int x) {
        for (WebComponent comp : children) {
            if (comp.constraints.gridx == x && comp.constraints.gridy == y) {
                return comp;
            }
        }
        return null;
    }

    private WebComponent getComponentIn(int y, int x) {
        for (WebComponent comp : children) {
            if (comp.constraints.gridx <= x && comp.constraints.gridx + comp.constraints.gridwidth > x
                    && comp.constraints.gridy <= y && comp.constraints.gridy + comp.constraints.gridheight > y)
                return comp;
        }
        return null;
    }

    public double getTotalWeightX() {
        return totalWeightX;
    }

    public double getTotalWeightY() {
        return totalWeightY;
    }

    public void setSizeOnLoad(StringBuilder out) {
        out.append("setDialogSize(").append(getMaxWidth()).append(", ").append(getMaxHeight()).append(");");
    }

    protected void appendBorder(StringBuilder temp, Border brd) {
        Color c = new Color(128, 144, 166);
        border = new StringBuilder();
        int thickness = 1;
        String type = "";
        if (brd instanceof EtchedBorder) {
            type = "groove";
        } else if (brd instanceof LineBorder) {
            thickness = ((LineBorder) brd).getThickness();
            c = ((LineBorder) brd).getLineColor();
            type = "solid";
        } else if (brd instanceof BevelBorder) {
            int tp = ((BevelBorder) brd).getBevelType();
            if (tp == BevelBorder.RAISED) {
                type = "outset";
            } else if (tp == BevelBorder.LOWERED) {
                type = "inset";
            }
        }

        border.append("border: ");
        border.append(thickness);
        border.append("px ");
        if (c != null) {
            border.append(" #");
            String code = Integer.toHexString(c.getRed());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
            code = Integer.toHexString(c.getGreen());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
            code = Integer.toHexString(c.getBlue());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
        }
        border.append(" ").append(type).append("; ");
        temp.append(border);
    }

    protected void appendBorder(JsonObject temp, Border brd) {
        Color c = new Color(128, 144, 166);
        border = new StringBuilder();
        int thickness = 1;
        String type = "";
        if (brd instanceof EtchedBorder) {
            type = "groove";
        } else if (brd instanceof LineBorder) {
            thickness = ((LineBorder) brd).getThickness();
            c = ((LineBorder) brd).getLineColor();
            type = "solid";
        } else if (brd instanceof BevelBorder) {
            int tp = ((BevelBorder) brd).getBevelType();
            if (tp == BevelBorder.RAISED) {
                type = "outset";
            } else if (tp == BevelBorder.LOWERED) {
                type = "inset";
            }
        }

        border.append("border: ");
        border.append(thickness);
        border.append("px ");
        if (c != null) {
            border.append(" #");
            String code = Integer.toHexString(c.getRed());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
            code = Integer.toHexString(c.getGreen());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
            code = Integer.toHexString(c.getBlue());
            while (code.length() < 2) {
                code = "0" + code;
            }
            border.append(code);
        }
        border.append(" ").append(type).append("; ");
        temp.add("border", border);
    }

    public int getHeight() {
        if (prefSize != null && prefSize.height > 0) {
            return (int) (heightCoeff * prefSize.height);
        } else if (minSize != null && minSize.height > 0) {
            return (int) (heightCoeff * minSize.height);
        } else if (maxSize != null && maxSize.height > 0) {
            return (int) (heightCoeff * maxSize.height);
        }
        return 0;
    }

    public int getMaxHeight() {
        if (maxSize != null && maxSize.height > 0) {
            return (int) (maxHeightCoeff * maxSize.height);
        } else if (prefSize != null && prefSize.height > 0) {
            return (int) (maxHeightCoeff * prefSize.height);
        } else if (minSize != null && minSize.height > 0) {
            return (int) (maxHeightCoeff * minSize.height);
        }
        return 0;
    }

    public int getTotalSizeX() {
        return totalSizeX;
    }

    protected void appendFontStyle(StringBuilder temp) {
        appendFont(temp);
        appendFontColor(temp);
        appendGradient(temp);
        if (gradientColor == null)
            appendBgFontColor(temp);
    }

    public WebComponent[] getChildren() {
        return children.toArray(EMPTY_ARRAY);
    }

    public List<WebComponent> getListChildren() {
        return children;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public String getJavaScript() {
        StringBuilder out = new StringBuilder(256);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                WebComponent comp = getComponent(i, j);
                if (comp != null) {
                    out.append(comp.getJavaScript());
                }
            }
        }
        return out.toString();
    }

    public StringBuilder getBorderHTML(StringBuilder b) {

        b.append("<FIELDSET");
        StringBuilder temp = new StringBuilder(100);
        addConstraints(temp);
        if (borderType instanceof TitledBorder) {
            appendBorder(temp, ((TitledBorder) borderType).getBorder());
        }

        if (temp.length() > 0) {
            b.append(" style=\"").append(temp).append("\"");
        }

        temp = new StringBuilder(200);

        temp.append("margin: auto; border-style: none; white-space:nowrap;");
        StringBuilder title = new StringBuilder(200);
        if (borderType != null) {
            Font fn = ((TitledBorder) borderType).getTitleFont();
            int psT = ((TitledBorder) borderType).getTitleJustification();
            Color colT = ((TitledBorder) borderType).getTitleColor();
            kz.tamur.rt.Utils.getCSS(fn, temp);
            kz.tamur.rt.Utils.getCSS(colT, temp);
            
            temp.append(" padding-right:2; padding-left:2;");
            String style1;
            String style2;
            StringBuilder hr = new StringBuilder();
            hr.append("<hr size='1' style=\"width: 100%; ");
            // сбросить стили бутстрапа
            hr.append("border-color: ''; border-style: ''; border-width: ''; margin: 0; ");
            // добавить свои стили
            hr.append(border);
            hr.append("border-bottom: 0 none; left: 0; top: -2;\"/>");
            switch (psT) {
            case TitledBorder.CENTER:
                style1 = "style='width: 50%;'";
                style2 = "style='width: 50%;'";
                break;
            case TitledBorder.LEFT:
            default:
                style1 = "style='width: 2px;'";
                style2 = "style='width: 100%;'";
                break;
            case TitledBorder.RIGHT:
                style1 = "style='width: 100%;'";
                style2 = "style='width: 2px;'";
                break;
            }

            title.append("<table style='width: 100%;'>");
            title.append("<tr><td ");
            title.append(style1);
            title.append(">");
            title.append(hr.toString());
            title.append("</td><td style='");
            title.append(temp);
            title.append("'>");
            title.append(borderTitle);
            title.append("</td><td ");
            title.append(style2);
            title.append(">");
            title.append(hr.toString());
            title.append("</td></tr></table>");
        }
        b.append("><LEGEND ");
        b.append("style='").append(temp).append("' ");
        b.append("class='leg'>").append(title).append("</LEGEND>");
        return b;
    }

    public void getBorderHTML(JsonObject json) {
        JsonObject border = new JsonObject();
        JsonObject style = new JsonObject();
        if (borderType instanceof TitledBorder) {
            appendBorder(style, ((TitledBorder) borderType).getBorder());
        }
        if (borderType != null) {
            Font fn = ((TitledBorder) borderType).getTitleFont();
            int psT = ((TitledBorder) borderType).getTitleJustification();
            Color colT = ((TitledBorder) borderType).getTitleColor();
            WebUtils.appendFontStyle(fn, style);
            appendFontStyle(style);

            if (colT != null) {
                style.set("color", WebUtils.colorToString(colT));
            }
        }

        if (style.size() > 0) {
            border.add("st", style);
        }
        border.add("title", borderTitle);
        json.set("border", border);
    }

   @Override
    public JsonObject putJSON(boolean isSend) {
        return putJSON(false, isSend);
    }
    
    public JsonObject putJSON(boolean isChange,boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        if (parent instanceof WebTabbedPane && children.size() == 1) {
            children.get(0).putJSON();
            return obj;
        }
        if (layout instanceof WebGridBagLayout) {
            String sHeight = this.sHeight;
            //style.add("width", "100%");
            //style.add("height", "100%");
            if (bgImageName != null) {
                JsonObject img = new JsonObject();
                StringBuilder tmp = new StringBuilder(50);
                tmp.append("url('").append(WebController.PATH_IMG).append(bgImageName).append("') no-repeat scroll ")
                        .append(positionPict == GridBagConstraints.CENTER ? "center" : "0 0").append(" transparent");
                img.add("background", tmp);
            }

            if (borderTitle != null && borderTitle.length() > 0 && !isChange) {
                getBorderHTML(property);
            } else if (children.size() == 0) {
                property.add("children-size", 0);
            } else {
                if (sWidth!= null && sWidth.length() > 0) {
                    property.add("width", sWidth);
                } 
                if (sHeight!= null && sHeight.length() > 0) {
                    property.add("height", sHeight);
                } 
            }
            
            if (!isVisible()) {
                sendChangeProperty("v", 0);
            }
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    WebComponent comp = getComponent(i, j);
                    if (comp != null) {
                        if (comp.isVisible()) {
                            comp.putJSON();
                        } else {
                            comp.setNeedToPutJSON();
                        }
                    }
                }

            }
        } else if (layout != null) {
            obj.add("layout", layout.getJSON(this, isChange));
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}
