package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.VERTICAL;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.NONE;
import static kz.tamur.comps.models.Types.*;
import kz.tamur.Or3Frame;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyReestr;
import kz.tamur.comps.models.Types;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 16.09.2004
 * Time: 10:12:03
 */
public class ReplacePanel extends JPanel implements ActionListener {

    private final static Insets ins5500 = new Insets(5, 5, 0, 0);
    private final static Insets ins5505 = new Insets(5, 5, 0, 5);
    private final static Insets ins0050 = new Insets(0, 0, 5, 0);
    private final static JLabel imageLab = new JLabel(kz.tamur.rt.Utils.getImageIconJpg("ReplaceImage2"));
    private final static JLabel attrLab = Utils.createLabel("Свойство", JLabel.RIGHT);
    private final static JLabel findLab = Utils.createLabel("Текст для поиска", JLabel.RIGHT);
    private final static JLabel replaceLab = Utils.createLabel("Заменить на", JLabel.RIGHT);
    private JComboBox attrCombo = Utils.createCombo();
    private JTextField findText = Utils.createDesignerTextField();
    private JButton browseBtn = ButtonsFactory.createToolButton("editor", "Выбрать", true);
    private JTextField replaceText = Utils.createDesignerTextField();
    private JButton browseReplBtn = ButtonsFactory.createToolButton("editor", "Выбрать", true);
    private JTable resultTable = new SearchResultTable();
    private JPanel actionPanel = new JPanel(new GridBagLayout());
    private JButton findBtn = ButtonsFactory.createToolButton("Поиск");
    private JButton clearBtn = ButtonsFactory.createToolButton("Очистить");
    private JButton doReplaceBtn = ButtonsFactory.createToolButton("Заменить");
    private JButton doReplaceAllBtn = ButtonsFactory.createToolButton("Заменить всё");
    private DesignerFrame df;
    private OrGuiComponent comp = null;
    private Component cont = null;
    private EventListenerList listenerList = new EventListenerList();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private String replaceTo;
    private String find;

    public ReplacePanel() {
        super();
        init();
    }

    public void setDf(DesignerFrame df) {
        this.df = df;
    }

    private void init() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(Or3Frame.instance().getSize().width - 200, 620));
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
        List<PropertyNode> attrs = PropertyReestr.getRegisterProperties();
        PropertyNode prop;
        int type;
        StringBuilder exclude = new StringBuilder();
        for (int i = 0; i < attrs.size(); i++) {
            prop = attrs.get(i);
            prop.setPlainMode(true);
            type = prop.getType();
            if (EXPR == type || REF == type || STRING == type || MSTRING == type || RSTRING == type || VIEW_STRING == type || FILTER == type || REPORT == type || KRNOBJECT == type) {
                attrCombo.addItem(prop);
            } else {
                exclude.append(i == 0 ? "(Исключая: " : ',').append(prop.toString());
            }
        }
        exclude.append(')');
        attrCombo.addItem("Все свойства" + exclude.toString());
        attrCombo.setSelectedIndex(attrCombo.getItemCount() - 1);
        attrCombo.addActionListener(this);

        Dimension size = new Dimension(100, 20);

        browseBtn.addActionListener(this);
        browseReplBtn.addActionListener(this);
        Utils.setAllSize(findText, size);
        Utils.setAllSize(replaceText, size);

        findBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        doReplaceBtn.setEnabled(false);
        doReplaceAllBtn.setEnabled(false);

        JScrollPane sp = new JScrollPane(resultTable);
        sp.setPreferredSize(new Dimension(700, 250));
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        TitledBorder tBorder = BorderFactory.createTitledBorder(b, "Результат поиска", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, Utils.getDefaultFont(), Utils.getDarkShadowSysColor());
        sp.setBorder(tBorder);

        actionPanel.add(findBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, ins0050, 0, 0));
        actionPanel.add(clearBtn, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, NONE, ins0050, 0, 0));
        actionPanel.add(doReplaceBtn, new GridBagConstraints(0, 2, 1, 1, 0, 0, CENTER, NONE, ins0050, 0, 0));
        actionPanel.add(doReplaceAllBtn, new GridBagConstraints(0, 3, 1, 1, 0, 0, CENTER, NONE, ins0050, 0, 0));
        actionPanel.add(imageLab, new GridBagConstraints(0, 4, 1, 1, 0, 1, CENTER, VERTICAL, Constants.INSETS_0, 0, 0));
        add(attrLab, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        add(attrCombo, new GridBagConstraints(2, 0, 2, 1, 1, 0, CENTER, HORIZONTAL, ins5505, 0, 0));
        add(findLab, new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        add(findText, new GridBagConstraints(2, 1, 1, 1, 1, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        add(browseBtn, new GridBagConstraints(3, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5505, 0, 0));
        add(replaceLab, new GridBagConstraints(1, 2, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        add(replaceText, new GridBagConstraints(2, 2, 1, 1, 1, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        add(browseReplBtn, new GridBagConstraints(3, 2, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5505, 0, 0));
        add(actionPanel, new GridBagConstraints(4, 0, 1, 5, 0, 1, NORTH, VERTICAL, ins5505, 0, 0));
        add(sp, new GridBagConstraints(1, 4, 3, 1, 1, 2, CENTER, BOTH, ins5500, 0, 0));

        Dimension sizeBtn = new Dimension(100, 30);
        Utils.setAllSize(findBtn, sizeBtn);
        Utils.setAllSize(clearBtn, sizeBtn);
        Utils.setAllSize(doReplaceBtn, sizeBtn);
        Utils.setAllSize(doReplaceAllBtn, sizeBtn);
        doReplaceBtn.addActionListener(this);
        doReplaceAllBtn.addActionListener(this);
        resultTable.setFont(Utils.getDefaultFont());
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (resultTable.getRowCount() > 0 && resultTable.getSelectedRow() > -1) {
                        TableModel model = resultTable.getModel();
                        if (model instanceof SearchTableModel) {
                            comp = ((SearchTableModel) model).getSelectedGuiComponent(resultTable.getSelectedRow());
                            cont = ((SearchTableModel) model).getSelectedContainer(resultTable.getSelectedRow());
                            ((DesignerDialog) getTopLevelAncestor()).dispose();
                        }
                    }
                }
                super.mouseClicked(e);
            }
        });
        resultTable.setOpaque(isOpaque);
        actionPanel.setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
    }

    private void searchRecur(PropertyNode pn, OrGuiComponent c, String find, SearchTableModel model, String ifcName,
            OrGuiComponent container) {
    	if (c instanceof EmptyPlace) return;
        PropertyValue pv = c.getPropertyValue(pn);
        if (pv.equals(find, Constants.CONTAINS)) {
            model.addComponent(c, ifcName, container, pn);
        }
        if (c instanceof OrGuiContainer && !(c instanceof OrScrollPane) && !(c instanceof OrTable)) {
            Container cont = (Container) c;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                Component comp = cont.getComponent(i);
                if (comp instanceof OrGuiComponent) {
                    searchRecur(pn, (OrGuiComponent) comp, find, model, ifcName, container);
                }
            }
        } else if (c instanceof OrGuiContainer && c instanceof OrScrollPane) {
            OrScrollPane cont = (OrScrollPane) c;
            for (int i = 0; i < cont.getOrComponentCount(); i++) {
                Component comp = cont.getOrComponent(i);
                if (comp instanceof OrGuiComponent) {
                    searchRecur(pn, (OrGuiComponent) comp, find, model, ifcName, container);
                }
            }
        } else if (c instanceof OrTable) {
            JTable table = ((OrTable) c).getJTable();
            OrTableModel tabModel = (OrTableModel) table.getModel();
            for (int i = 0; i < table.getColumnCount(); i++) {
                OrTableColumn tc = tabModel.getColumn(i);
                if (tc != null) {
                    searchRecur(pn, tc, find, model, ifcName, container);
                }
            }
            OrPanel pan = ((OrTable) c).getAddPan();
            if (pan != null) {
                searchRecur(pn, pan, find, model, ifcName, container);
            }
        } else if (c instanceof OrPopUpPanel) {
            OrPanel panel = ((OrPopUpPanel) c).getMainPanel();
            if (panel != null) {
                Component[] comps = panel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof OrGuiComponent) {
                        searchRecur(pn, (OrGuiComponent) comp, find, model, ifcName, container);
                    }
                }
            }
        } else if (c instanceof OrCollapsiblePanel) {
            OrPanel panel = ((OrCollapsiblePanel) c).getContent();
            if (panel != null) {
                Component[] comps = panel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof OrGuiComponent) {
                        searchRecur(pn, (OrGuiComponent) comp, find, model, ifcName, container);
                    }
                }
            }
        } else if (c instanceof OrAccordion) {
            List<OrPanel> panels = ((OrAccordion) c).getContent();
            if (panels != null) {
                for (OrPanel panel : panels) {
                    Component[] comps = panel.getComponents();
                    for (Component comp : comps) {
                        if (comp instanceof OrGuiComponent) {
                            searchRecur(pn, (OrGuiComponent) comp, find, model, ifcName, container);
                        }
                    }
                }
            }
        }
    }

    private void search(OrGuiComponent p, SearchTableModel model, String ifcName) {
        long langId = df.getInterfaceLang().id;
        Object selItem = attrCombo.getSelectedItem();
        if (selItem instanceof String) {
            for (int i = 0; i < attrCombo.getItemCount() - 2; i++) {
                PropertyNode pn1 = (PropertyNode) attrCombo.getItemAt(i);
                searchRecur(pn1, p, this.find, model, ifcName, p);
            }
        } else {
            searchRecur(((PropertyNode) selItem), p, this.find, model, ifcName, p);
        }
    }

    private void search(int areaType) {
        SearchTableModel model = new SearchTableModel();
        OrGuiComponent cont = DesignerFrame.tabbedContent.getOrGuiComponent();
        if (cont != null && cont instanceof OrPanel) {
            OrPanel p = (OrPanel) cont;
            search(p, model, DesignerFrame.tabbedContent.getTitleAt(DesignerFrame.tabbedContent.getSelectedIndex()));
        }

        model.sort();
        resultTable.setModel(model);

        ResultTableCellRenderer tcr = new ResultTableCellRenderer();
        TableColumn tc = resultTable.getColumnModel().getColumn(0);
        tc.setPreferredWidth(90);
        tc = resultTable.getColumnModel().getColumn(1);
        tc.setCellRenderer(tcr);
        tc.setPreferredWidth(50);
        tc = resultTable.getColumnModel().getColumn(2);
        tc.setPreferredWidth(800);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        replaceTo = replaceText.getText();
        find = findText.getText();
        if (src == findBtn) {
            search(0);
            doReplaceAllBtn.setEnabled(true);
            doReplaceBtn.setEnabled(true);
        } else if (src == clearBtn) {
            resultTable.setModel(new SearchTableModel());
        } else if (src == browseBtn || src == browseReplBtn) {
            try {
                ClassNode cls = Kernel.instance().getClassNodeByName("Объект");
                ClassBrowser cb = new ClassBrowser(cls, true);
                DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите класс", cb);
                dlg.show();
                if (dlg.isOK()) {
                    if (src == browseBtn) {
                        findText.setText(cb.getSelectedPath());
                    } else {
                        replaceText.setText(cb.getSelectedPath());
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        } else if (src == doReplaceBtn) {
            if (!find.isEmpty()) {
                int row = resultTable.getSelectedRow();
                if (resultTable.getRowCount() > 0 && row != -1) {
                    SearchTableModel model = (SearchTableModel) resultTable.getModel();
                    OrGuiComponent c = model.getSelectedGuiComponent(row);
                    PropertyNode pn = model.getSelectedProperty(row);
                    replace(c, pn);
                    model.fireTableDataChanged();
                    firePropertyModified(c);
                }
            }
        } else if (src == doReplaceAllBtn) {
            if (!find.isEmpty() && resultTable.getRowCount() > 0) {
                CursorToolkit.startWaitCursor(this);
                SearchTableModel model = (SearchTableModel) resultTable.getModel();
                OrGuiComponent c;
                PropertyNode pn;
                for (int i = 0; i < resultTable.getRowCount(); i++) {
                    c = model.getSelectedGuiComponent(i);
                    pn = model.getSelectedProperty(i);
                    replace(c, pn);
                    firePropertyModified(c);
                }
                model.fireTableDataChanged();
                CursorToolkit.stopWaitCursor(this);
                StringBuilder mes = new StringBuilder().append("Произведено ").append(resultTable.getRowCount())
                        .append(" замен значений.");
                MessagesFactory.showMessageDialog((Dialog) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                        mes.toString());
            }
        }
    }

    class SearchTableModel extends AbstractTableModel {

        private final String[] COL_NAMES = new String[] { "Компонент", "Свойство", "Значение" };
        private List<PropertySearchResult> data = new ArrayList<PropertySearchResult>();

        public SearchTableModel() {
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public void addComponent(OrGuiComponent c, String ifcName, OrGuiComponent container, PropertyNode pn) {
            PropertySearchResult psr = new PropertySearchResult(ifcName, container, c, pn);
            if (!data.contains(psr)) {
                data.add(psr);
                fireTableDataChanged();
            }
        }

        public void sort() {
            Collections.sort(data, new Comparator<PropertySearchResult>() {

                public int compare(PropertySearchResult arg0, PropertySearchResult arg1) {
                    OrGuiComponent c0 = arg0.comp;
                    String className0 = c0.getClass().getName();
                    String title0 = "";
                    PropertyValue pv = null;
                    if (!(c0 instanceof OrTableColumn)) {
                        pv = c0.getPropertyValue(c0.getProperties().getChild("title"));
                    } else {
                        pv = c0.getPropertyValue(c0.getProperties().getChild("header").getChild("text"));
                    }
                    if (!pv.isNull()) {
                        title0 = pv.stringValue();
                    }
                    title0 = title0 + " [" + className0 + "]";

                    OrGuiComponent c1 = arg1.comp;
                    String className1 = c1.getClass().getName();
                    String title1 = "";
                    if (!(c1 instanceof OrTableColumn)) {
                        pv = c1.getPropertyValue(c1.getProperties().getChild("title"));
                    } else {
                        pv = c1.getPropertyValue(c1.getProperties().getChild("header").getChild("text"));
                    }
                    if (!pv.isNull()) {
                        title1 = pv.stringValue();
                    }
                    title1 = title1 + " [" + className1 + "]";

                    return title0.compareTo(title1);
                }

            });
        }

        public OrGuiComponent getSelectedGuiComponent(int row) {
            return data.get(row).comp;
        }

        public PropertyNode getSelectedProperty(int row) {
            return data.get(row).pn;
        }

        public Component getSelectedContainer(int row) {
            return (Component) data.get(row).container;
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            OrGuiComponent c = data.get(rowIndex).comp;
            String className = c.getClass().getName().substring(Constants.COMPS_PACKAGE.length());

            if (columnIndex == 0) {
                String title = "";
                PropertyValue pv = null;
                if (!(c instanceof OrTableColumn)) {
                    pv = c.getPropertyValue(c.getProperties().getChild("title"));
                } else {
                    pv = c.getPropertyValue(c.getProperties().getChild("header").getChild("text"));
                }
                if (!pv.isNull()) {
                    title = pv.stringValue();
                }
                title = title + " [" + className + "]";
                return title;
            } else if (columnIndex == 1) {
                PropertyNode pn = data.get(rowIndex).pn;
                return pn.toString();
            } else if (columnIndex == 2) {
                PropertyNode pn = data.get(rowIndex).pn;
                PropertyValue pv = c.getPropertyValue(pn);
                return pv;
            }

            return null;
        }
    }

    public OrGuiComponent getResultComponent() {
        return comp;
    }

    public Component getResultContainer() {
        return cont;
    }

    public void setResultComponent(OrGuiComponent component) {
        comp = component;
    }

    public void setResultContainer(Component container) {
        cont = container;
    }

    private void replace(OrGuiComponent c, PropertyNode pn) {
        PropertyValue pv = c.getPropertyValue(pn);
        String currentValue = "";
        if (pv != null && !pv.isNull()) {
            int type = pn.getType();
            if (type == REF || type == MSTRING || type == EXPR) {
                currentValue = pv.stringValue();
                String newVal = currentValue.replace(find, replaceTo);
                PropertyValue newValue = new PropertyValue(newVal, df.getInterfaceLang().id, pn);
                try {
                    c.setPropertyValue(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == RSTRING) {
                Pair p = pv.resourceStringValue();
                String uid = (String) p.first;
                currentValue = (String) p.second;
                
                String replaceTo2 = Funcs.validate(Funcs.normalizeInput(replaceTo));
                String find2 = Funcs.validate(Funcs.normalizeInput(find));
                String newVal = currentValue.replace(find2, replaceTo2);
                
                PropertyValue newValue = new PropertyValue(new Pair(uid, newVal), pn);
                try {
                    c.setPropertyValue(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == REPORT) {
            	ReportRecord r = pv.reportValue();
            	replace(r, find, replaceTo);
                try {
                    c.setPropertyValue(new PropertyValue(r, pn));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (df != null) {
            	GuiComponentItem componentItem = new GuiComponentItem(c, df);
            	df.getInspector().updateObject(componentItem);
            }
        }
    }
    
    private boolean replace(ReportRecord r, String find, String replaceTo) {
    	boolean res = false;
    	if (r != null) {
    		String t = r.getPath();
    		if (t != null) {
    			String tt = t.replace(find, replaceTo);
    			r.setPath(tt);
    		}
    		t = r.getFunc();
    		if (t != null) {
    			String tt = t.replace(find, replaceTo);
    			r.setFunc(tt);
    		}
    		t = r.getVisibilityFunc();
    		if (t != null) {
    			String tt = t.replace(find, replaceTo);
    			r.setVisibilityFunc(tt);
    		}
    		if (r.getChildren() != null) {
	    		for (ReportRecord ch : r.getChildren()) {
	    			replace(ch, find, replaceTo);
	    		}
    		}
    	}
    	return res;
    }


    /*
     * private String getTitle(OrGuiComponent c, PropertyValue pv) {
     * String className = c.getClass().getName().substring(Constants.COMPS_PACKAGE.length());
     * String title = "";
     * if (c instanceof OrTableColumn) {
     * pv = c.getPropertyValue(c.getProperties().getChild("header").getChild("text"));
     * } else {
     * pv = c.getPropertyValue(c.getProperties().getChild("title"));
     * }
     * if (!pv.isNull()) {
     * title = pv.stringValue();
     * }
     * title = title + " [" + className + "]";
     * return title;
     * }
     */

    public void addPropertyListener(PropertyListener l) {
        listenerList.add(PropertyListener.class, l);
    }

    public void firePropertyModified(OrGuiComponent c) {
        Object[] list = listenerList.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener) list[i]).propertyModified(c);
        }
    }

    class ResultTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                lab.setToolTipText(value.toString());
            }
            return lab;
        }
    }

    class PropertySearchResult {
        String ifcName;
        OrGuiComponent container;
        OrGuiComponent comp;
        PropertyNode pn;

        public PropertySearchResult(String ifcName, OrGuiComponent container, OrGuiComponent c, PropertyNode pn) {
            this.ifcName = ifcName;
            this.container = container;
            this.comp = c;
            this.pn = pn;
        }
    }
}
