package kz.tamur.comps;

import static kz.tamur.comps.PropertyHelper.getPropertyValue;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;

import java.awt.*;

import kz.tamur.comps.models.BackgroundProperty;
import kz.tamur.comps.models.CompPosition;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.FontProperty;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.PropertyRoot;
import kz.tamur.comps.models.Types;
import kz.tamur.guidesigner.EmptyComponent;
import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 15.05.2004
 * Time: 14:00:13
 */
public class ComponentsTreeRenderer extends DefaultTreeCellRenderer {

    private OrFrame frame;

    public ComponentsTreeRenderer(OrFrame frame) {
        this.frame = frame;
    }

    public void setFrame(OrFrame frame) {
        this.frame = frame;
    }

    public void setLangId(long langId) {
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setFont(Utils.getDefaultFont());
        if (value instanceof OrGuiComponent) {
            if (value instanceof EmptyComponent) {
                setText("Значение не определено...");
            } else {
                OrGuiComponent e = (OrGuiComponent) value;
                PropertyValue p = null;
                String cls = e.getXml().getAttributeValue("class");
                if (e instanceof OrTableColumn) {
                    p = getPropertyValue(OrTextColumn.PROPS.getChild("header").getChild("text"), e.getXml(), frame);
                } else {
                    if ("Panel".equals(cls) || "Label".equals(cls) || "HyperLabel".equals(cls) || "Button".equals(cls)
                            || "CheckBox".equals(cls) || "HyperPopup".equals(cls) || "TabbedPane".equals(cls)
                            || "Table".equals(cls) || "SplitPane".equals(cls) || "Note".equals(cls)
                            || "LayoutPane".equals(cls) || "ChartPanel".equals(cls) || "DocField".equals(cls) || "ChartPanel".equals(cls)
                            || "CollapsiblePanel".equals(cls) || "Accordion".equals(cls)) {
                        p = getPropertyValue(OrLabel.PROPS.getChild("title"), e.getXml(), frame);
                    } else {
                        p = getPropertyValue(OrTextField.PROPS.getChild("title"), e.getXml(), frame);
                    }
                }

                StringBuilder text = new StringBuilder();
                text.append(p.stringValue()).append(" [").append(e.getClass().getName().substring(Constants.COMPS_PACKAGE_L))
                        .append("]");
                setText(text.toString());
                if (e instanceof OrAccordion || e instanceof OrCollapsiblePanel) {
                    setIcon(getImageIconFull(cls + ".png"));
                } else if (e instanceof Spacer) {
                    if (e instanceof Spacer) {
                        if (((Spacer) e).getType() == Spacer.HORIZONTAL) {
                            setIcon(getImageIcon("HSpacer"));
                        } else {
                            setIcon(getImageIcon("VSpacer"));
                        }
                    }
                } else {
                    setIcon(getImageIcon(cls));
                }
            }
        }
        return this;
    }
}
