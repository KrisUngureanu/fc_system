package kz.tamur.comps;

import static kz.tamur.comps.Constants.CONTAINER_COMP;
import static kz.tamur.comps.Constants.HYPER_COMP;
import static kz.tamur.comps.Constants.STANDART_COMP;
import static kz.tamur.comps.Constants.TABLE_COMP;
import static kz.tamur.comps.Constants.TREES_COMP;
import static kz.tamur.comps.Constants.USER_COMP;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.filters.OrFilterNode;
import kz.tamur.util.Pair;

import org.jdom.Element;

import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.04.2004
 * Time: 19:29:07
 * To change this template use File | Settings | File Templates.
 */
public class Factories implements Factory {
    private static Factories inst;
    private EventListenerList listeners = new EventListenerList();
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);

    public static synchronized Factories instance() {
        if (inst == null) {
            inst = new Factories();
        }
        return inst;
    }

    public Factories() {
    }

    public OrGuiComponent create(Element xml, int mode, OrFrame frame) throws KrnException {
        String name = xml.getAttributeValue("class");
        fireComponentCreating(name);
        OrGuiComponent c = createImpl(xml, mode, frame);
        if (mode == Mode.DESIGN) {
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId();
            c.setLangId(langId);
            setComponentTitle(c, name, frame, false);
        }
        fireComponentCreated(c);
        return c;
    }

    private void setComponentTitle(OrGuiComponent c, String name, OrFrame frame, boolean isNew) {
        long langId = Kernel.instance().getInterfaceLanguage().id;
        int counter = 0;
        if (frame instanceof InterfaceFrame) {
            counter = ((InterfaceFrame)frame).getInterfaceCounter();
        }
        PropertyNode pnv = c.getProperties().getChild("varName");
        if (isNew && pnv != null) {
            PropertyValue pv = c.getPropertyValue(pnv);
            if (pv.isNull()) {
                c.setPropertyValue(new PropertyValue(name + ++counter, langId, pnv));
                ps.firePropertyChange("counter", 0, counter);
            }
        }

        if (!(c instanceof Spacer) && !(c instanceof OrTableColumn) &&
                !(c instanceof OrCheckBox)) {
        	PropertyNode pn = c.getProperties().getChild("title");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                if (pv.isNull()) {
                    String label = new StringBuilder().append(name).append(" ").append(counter).toString();
                    if (pn.getType() == Types.RSTRING) {
                        c.setPropertyValue(new PropertyValue(new Pair(frame.getNextUid(), label), pn));
                    } else {
                        c.setPropertyValue(new PropertyValue(label, langId, pn));
                    }
                }
            }
        } else if (c instanceof Spacer) {
            PropertyNode pn = c.getProperties().getChild("title");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                if (!pv.isNull()) {
                    c.setPropertyValue(new PropertyValue("", langId, pn));
                }
            }
        } else if (c instanceof OrTableColumn) {
            PropertyNode pn = c.getProperties().getChild("header").getChild("text");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                if (pv.isNull()) {
                    c.setPropertyValue(new PropertyValue(new Pair(frame.getNextUid(),
                            name + " " + counter), pn));
                    //ps.firePropertyChange("counter", 0, counter);
                }
            }
        }
    }

    public OrGuiComponent create(String name, OrFrame frame) throws KrnException {
        Element e = new Element("Component");
        e.setAttribute("class", name);
        fireComponentCreating(name);
        OrGuiComponent c = createImpl(e, Mode.DESIGN, frame);
        if(c==null) return null;
        setComponentTitle(c, name, frame, true);
        if (!(c instanceof OrTableColumn ||c instanceof Spacer ||c instanceof OrFilterNode ||c instanceof OrReportPrinter||c instanceof OrCollapsiblePanel ||c instanceof OrAccordion) ) {
            PropertyNode pn = c.getProperties().getChild("pos").getChild("fill");
            c.setPropertyValue(new PropertyValue(
                    ((Integer)pn.getDefaultValue()).intValue(), pn));
            pn = c.getProperties().getChild("pos").getChild("anchor");
            c.setPropertyValue(new PropertyValue(
                    ((Integer)pn.getDefaultValue()).intValue(), pn));
        }
        if ("Panel".equals(name)
        		|| "GISPanel".equals(name) 
        		|| "AnalyticPanel".equals(name)
        		|| "ScrollPane".equals(name)
                || "SplitPane".equals(name)
        		|| "LayoutPane".equals(name)
                || "Tree".equals(name)
                || "Tree2".equals(name)
                || "HyperTree".equals(name) 
                || "TabbedPane".equals(name)) {
            PropertyNode ps = c.getProperties().getChild("pos");
            c.setPropertyValue(new PropertyValue(1, ps.getChild("weightx")));
            c.setPropertyValue(new PropertyValue(1, ps.getChild("weighty")));
            c.setPropertyValue(new PropertyValue(
                    GridBagConstraints.BOTH, ps.getChild("fill")));
            if ("TabbedPane".equals(name)) {
                c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("view").getChild("font").getChild("fontG")));
            }
        } else if ("Table".equals(name)
        		|| "TreeTable".equals(name)
        		|| "TreeTable2".equals(name)) {
            PropertyNode ps = c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(200, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(200, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(250, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(250, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(100, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(100, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("fill");
            c.setPropertyValue(new PropertyValue(GridBagConstraints.BOTH, ps));
        } else if ("MemoField".equals(name) || "TextField".equals(name) || "PasswordField".equals(name) || "IntField".equals(name) ||
                "FloatField".equals(name) || "ComboBox".equals(name) || "RichTextEditor".equals(name)||
                "SequenceField".equals(name)) {
            //Предпочтительный
            PropertyNode ps =
                    c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(180, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            //Maximum
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(180, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            //Minimum
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(130, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("anchor");
            c.setPropertyValue(new PropertyValue(GridBagConstraints.WEST, ps));
            c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("view").getChild("font").getChild("fontG")));
        } else if ("DateField".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(70, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(70, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(70, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("anchor");
            c.setPropertyValue(new PropertyValue(GridBagConstraints.WEST, ps));
            c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("view").getChild("font").getChild("fontG")));
        } else if ("CoolDateField".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(110, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(110, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(110, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("anchor");
            c.setPropertyValue(new PropertyValue(GridBagConstraints.WEST, ps));
            c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("font")));
        } else if ("Label".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("view").getChild("alignmentText");
            c.setPropertyValue(new PropertyValue(JLabel.RIGHT, ps));
            c.setPropertyValue(new PropertyValue(GridBagConstraints.EAST,
                    c.getProperties().getChild("pos").getChild("anchor")));
            c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("view").getChild("font").getChild("fontG")));
            
        } else if ("Image".equals(name) 
        		|| "ImagePanel".equals(name)
                || "RadioBox".equals(name)
                || "Barcode".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(150, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(150, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(150, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(150, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(100, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(150, ps.getChild("height")));
            if (!"Image".equals(name) 
            		&& !"ImagePanel".equals(name)
            		&& !"Barcode".equals(name)) {
                c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                    c.getProperties().getChild("view").getChild("font").getChild("fontG")));
            }
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(150, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(100, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(150, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(150, ps.getChild("height")));
        } else if ("Button".equals(name)
        		|| "HyperPopup".equals(name)
        		|| "TreeField".equals(name)
        		|| "TreeField2".equals(name)
        		|| "DocField".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("view").getChild("alignmentText");
            c.setPropertyValue(new PropertyValue(
                    ((Integer)ps.getDefaultValue()).intValue(), ps));
            ps = c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(80, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("max");
            c.setPropertyValue(new PropertyValue(80, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            ps = c.getProperties().getChild("pos").getChild("min");
            c.setPropertyValue(new PropertyValue(50, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
            c.setPropertyValue(new PropertyValue(Utils.getDefaultComponentFont(),
                c.getProperties().getChild("view").getChild("font").getChild("fontG")));
        } else if ("SpinnerField".equals(name)) {
            PropertyNode ps =
                    c.getProperties().getChild("pos").getChild("pref");
            c.setPropertyValue(new PropertyValue(50, ps.getChild("width")));
            c.setPropertyValue(new PropertyValue(20, ps.getChild("height")));
        } 
        if ("TextField".equals(name) || "PasswordField".equals(name) || "IntField".equals(name) || "FloatField".equals(name)
                || "DateField".equals(name) || "MemoField".equals(name) || "ComboBox".equals(name) || "Button".equals(name)
                || "CheckBox".equals(name) || "RadioBox".equals(name) || "SequenceField".equals(name)
                || "HyperLabel".equals(name) || "HyperPopup".equals(name) || "TreeField".equals(name) || "DocField".equals(name)
                || "SliderField".equals(name) || "CoolDateField".equals(name) || "RichTextEditor".equals(name)) {
            c.setPropertyValue(new PropertyValue(5, c.getProperties().getChild("pos").getChild("insets").getChild("rightInsets")));
        }
        
        fireComponentCreated(c);
        return c;
    }

    private OrGuiComponent createImpl(Element xml, int mode, OrFrame frame) throws KrnException {
        String name = xml.getAttributeValue("class");
        if ("Panel".equals(name)) {
            return new OrPanel(xml, mode, this, frame);
        } else if ("GISPanel".equals(name)) {
            return new OrGISPanel(xml, mode, this, frame);
        } else if ("AnalyticPanel".equals(name)) {
            return new OrAnalyticPanel(xml, mode, this, frame);
        } else if ("ScrollPane".equals(name)) {
            return new OrScrollPane(xml, mode, this, frame);
        } else if ("LayoutPane".equals(name)) {
            return new OrLayoutPane(xml, mode, this, frame);
        } else if ("SplitPane".equals(name)) {
            return new OrSplitPane(xml, mode, this, frame);
        } else if ("TabbedPane".equals(name)) {
            return new OrTabbedPane(xml, mode, this, frame);
        } else if ("Tree".equals(name)) {
            return new OrTreeCtrl(xml, mode, frame);
        } else if ("Tree2".equals(name)) {
            return new OrTreeControl2(xml, mode, frame);
        } else if ("Table".equals(name)) {
            return new OrTable(xml, mode, this, frame);
        } else if ("TreeTable".equals(name)) {
            return new OrTreeTable(xml, mode, this, frame);
        } else if ("TreeTable2".equals(name)) {
            return new OrTreeTable2(xml, mode, this, frame);
        } else if ("Label".equals(name)) {
            return new OrLabel(xml, mode, frame, false);
        } else if ("Image".equals(name)) {
            return new OrImage(xml, mode, frame);
        } else if ("ImagePanel".equals(name)) {
            return new OrImagePanel(xml, mode, frame);
        } else if ("HSpacer".equals(name)) {
            return new Spacer(xml, Spacer.HORIZONTAL, mode, frame);
        } else if ("VSpacer".equals(name)) {
            return new Spacer(xml, Spacer.VERTICAL, mode, frame);
        } else if ("TextField".equals(name)) {
            return new OrTextField(xml, mode, frame, false);
        } else if ("PasswordField".equals(name)) {
            return new OrPasswordField(xml, mode, frame, false);
        } else if ("CheckBox".equals(name)) {
            return new OrCheckBox(xml, mode, frame, false);
        } else if ("IntField".equals(name)) {
            return new OrIntField(xml, mode, frame, false);
        } else if ("CoolDateField".equals(name)) {
            return new OrCoolDateField(xml, mode, frame);
        } else if ("FloatField".equals(name)) {
            return new OrFloatField(xml, mode, frame, false);
        } else if ("DateField".equals(name)) {
            return new OrDateField(xml, mode, frame, false);
        } else if ("MemoField".equals(name)) {
            return new OrMemoField(xml, mode, frame, false);
        } else if ("RichTextEditor".equals(name)) {
            return new OrRichTextEditor(xml, mode, frame, false);    
        } else if ("ComboBox".equals(name)) {
            return new OrComboBox(xml, mode, frame, false);
        }else if ("ComboColumn".equals(name)) {
            return new OrComboColumn(xml, mode, frame);
        } else if ("TextColumn".equals(name)) {
            return new OrTextColumn(xml, mode, frame);
        } else if ("IntColumn".equals(name)) {
            return new OrIntColumn(xml, mode, frame);
        } else if ("FloatColumn".equals(name)) {
            return new OrFloatColumn(xml, mode, frame);
        } else if ("DateColumn".equals(name)) {
            return new OrDateColumn(xml, mode, frame);
        } else if ("MemoColumn".equals(name)) {
            return new OrMemoColumn(xml, mode, frame);
        } else if ("CheckColumn".equals(name)) {
            return new OrCheckColumn(xml, mode, frame);
        } else if ("HyperColumn".equals(name)) {
            return new OrHyperColumn(xml, mode, frame);
        } else if ("PopupColumn".equals(name)) {
            return new OrPopupColumn(xml, mode, frame);
        }  else if ("TreeColumn".equals(name)) {
            return new OrTreeColumn(xml, mode, frame);
        }  else if ("ImageColumn".equals(name)) {
            return new OrImageColumn(xml, mode, frame);
        } else if ("Button".equals(name)) {
            return new OrButton(xml, mode, frame);
        } else if ("RadioBox".equals(name)) {
            return new OrRadioBox(xml, mode, frame);
        } else if ("SequenceField".equals(name)) {
            return new OrSequenceField(xml, mode, frame);
        } else if ("HyperLabel".equals(name)) {
            return new OrHyperLabel(xml, mode, frame, false);
        } else if ("HyperPopup".equals(name)) {
            return new OrHyperPopup(xml, mode, frame, false);
        } else if ("TreeField".equals(name)) {
            return new OrTreeField(xml, mode, frame, false);
        } else if ("ReportPrinter".equals(name)) {
            return new OrReportPrinter(xml, mode, frame);
        } else if ("Note".equals(name)) {
            return new OrNote(xml, mode, frame);
        } else if ("FilterNode".equals(name)) {
            return new OrFilterNode(xml, mode, this, frame);
        } else if ("SliderField".equals(name)) {
            return new OrSliderField(xml, mode, frame);
        } else if ("SpinnerField".equals(name)) {
            return new OrSpinnerField(xml, mode, frame);
        /*} else if ("OrService".equals(name)) {
            return new OrService(xml, mode, this);
        } else if ("ServiceAction".equals(name)) {
            return new ServiceAction(xml, mode);*/
        } else if ("HyperTree".equals(name)) {
            return new OrHyperTree(xml, mode, frame);
        } else if ("DocField".equals(name)) {
            return new OrDocField(xml, mode, frame, false);
        } else if ("DocFieldColumn".equals(name)) {
            return new OrDocFieldColumn(xml, mode, frame);
        } else if ("Map".equals(name)) {
            return new OrMap(xml, mode, frame);
        } else if ("ChartPanel".equals(name)) {
            return new OrChartPanel(xml, mode, this, frame);
        } else if ("PopUpPanel".equals(name)) {
            return new OrPopUpPanel(xml, mode, this, frame);
        } else if ("CollapsiblePanel".equals(name)) {
            return new OrCollapsiblePanel(xml, mode, this, frame);
        } else if ("Accordion".equals(name)) {
            return new OrAccordion(xml, mode, this, frame);
        } else if ("Barcode".equals(name)) {
        	return new OrBarcode(xml, mode, frame);
        }
        return null;
    }

    public Place createEmptyPlace() {
        EmptyPlace place = new EmptyPlace();
        fireComponentCreated(place);
        return place;
    }

    public void addFactoryListener(FactoryListener l) {
        listeners.add(FactoryListener.class, l);
    }

    public void removeFactoryListener(FactoryListener l) {
        listeners.remove(FactoryListener.class, l);
    }

    private void fireComponentCreated(OrGuiComponent c) {
        EventListener[] ls = listeners.getListeners(FactoryListener.class);
        for (int i = 0; i < ls.length; i++) {
            ((FactoryListener)ls[i]).componentCreated(c);
        }
    }

    private void fireComponentCreating(String className) {
        EventListener[] ls = listeners.getListeners(FactoryListener.class);
        for (int i = 0; i < ls.length; i++) {
            ((FactoryListener)ls[i]).componentCreating(className);
        }
    }

    public class ComponentButton {
        public String name;
        public String icon;
        public int status;

        public ComponentButton(String name, int status) {
            this.name = name;
            this.icon = name + ".gif";
            this.status = status;
        }
        public ComponentButton(String name, String icon, int status) {
            this.name = name;
            this.icon = icon;
            this.status = status;
        }
    }

    public ComponentButton[] getNames() {
        return new ComponentButton[] { new ComponentButton("Panel", CONTAINER_COMP),
                new ComponentButton("ScrollPane", CONTAINER_COMP), 
                new ComponentButton("SplitPane", CONTAINER_COMP),
                new ComponentButton("LayoutPane", CONTAINER_COMP), 
                new ComponentButton("TabbedPane", CONTAINER_COMP), 
                new ComponentButton("HSpacer", CONTAINER_COMP),
                new ComponentButton("VSpacer", CONTAINER_COMP), 
                new ComponentButton("PopUpPanel", CONTAINER_COMP),
                new ComponentButton("CollapsiblePanel", "collapsiblePanel.png", CONTAINER_COMP),
                new ComponentButton("Accordion", "accordion.png", CONTAINER_COMP), 
                new ComponentButton("Tree", TREES_COMP),
                new ComponentButton("Tree2", TREES_COMP), 
                new ComponentButton("Table", TABLE_COMP),
                new ComponentButton("TreeTable", TREES_COMP), 
                new ComponentButton("TreeTable2", TREES_COMP),
                new ComponentButton("Label", STANDART_COMP), 
                new ComponentButton("TextField", STANDART_COMP),
                new ComponentButton("PasswordField", STANDART_COMP), 
                new ComponentButton("IntField", STANDART_COMP),
                new ComponentButton("FloatField", STANDART_COMP), 
                new ComponentButton("DateField", STANDART_COMP),
                new ComponentButton("MemoField", STANDART_COMP), 
                new ComponentButton("RichTextEditor", STANDART_COMP), 
                new ComponentButton("ComboBox", STANDART_COMP),
                new ComponentButton("Button", STANDART_COMP), 
                new ComponentButton("CheckBox", STANDART_COMP),
                new ComponentButton("RadioBox", STANDART_COMP), 
                new ComponentButton("Image", STANDART_COMP),
                new ComponentButton("ImagePanel", STANDART_COMP), 
                new ComponentButton("Map", STANDART_COMP),
                new ComponentButton("Barcode", "barcode.png", STANDART_COMP),
                new ComponentButton("GISPanel", "GIS.png", STANDART_COMP),
                new ComponentButton("AnalyticPanel", "ChartPanel.gif", STANDART_COMP),
                new ComponentButton("TextColumn", TABLE_COMP), 
                new ComponentButton("IntColumn", TABLE_COMP),
                new ComponentButton("FloatColumn", TABLE_COMP), 
                new ComponentButton("DateColumn", TABLE_COMP),
                new ComponentButton("MemoColumn", TABLE_COMP), 
                new ComponentButton("ComboColumn", TABLE_COMP),
                new ComponentButton("CheckColumn", TABLE_COMP), 
                new ComponentButton("HyperColumn", TABLE_COMP),
                new ComponentButton("PopupColumn", TABLE_COMP), 
                new ComponentButton("TreeColumn", TABLE_COMP),
                new ComponentButton("DocFieldColumn", TABLE_COMP), 
                new ComponentButton("ImageColumn", TABLE_COMP),
                new ComponentButton("SequenceField", USER_COMP), 
                new ComponentButton("Note", USER_COMP),
                new ComponentButton("HyperLabel", HYPER_COMP), 
                new ComponentButton("HyperPopup", HYPER_COMP),
                new ComponentButton("TreeField", TREES_COMP), 
                new ComponentButton("DocField", USER_COMP),
                new ComponentButton("SliderField", USER_COMP), 
                new ComponentButton("SpinnerField", USER_COMP),
                new ComponentButton("CoolDateField", USER_COMP), 
                new ComponentButton("ChartPanel", USER_COMP) };
    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        ps.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        ps.removePropertyChangeListener(l);
    }

}
