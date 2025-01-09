package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NONE;
import static kz.tamur.comps.models.Types.*;
import static kz.tamur.guidesigner.DesignerFrame.tabbedContent;
import kz.tamur.Or3Frame;
import kz.tamur.comps.*;
import kz.tamur.rt.Utils;
import kz.tamur.comps.models.PropertyReestr;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.Border;

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
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 16.09.2004
 * Time: 10:12:03
 */
public class SearchPanel extends JPanel implements ActionListener {

    private final static Insets ins5500 = new Insets(5, 5, 0, 0);
    private final static Insets ins5505 = new Insets(5, 5, 0, 5);
    private final static JLabel imageLab = new JLabel(kz.tamur.rt.Utils.getImageIconJpg("FindImage2"));
    private final static JLabel areaLab = Utils.createLabel("Интерфейсы", JLabel.RIGHT);
    private final static JLabel attrLab = Utils.createLabel("Свойства", JLabel.RIGHT);
    private final static JLabel findLab = Utils.createLabel("Строка", JLabel.RIGHT);
    private JComboBox<String> areaCombo = Utils.createCombo();
    private JComboBox attrCombo = Utils.createCombo();
    private JTextField findText = Utils.createDesignerTextField();
    private JButton browseBtn = ButtonsFactory.createToolButton("editor", "Выбрать", true);
    private JTable resultTable = new SearchResultTable();
    private JCheckBox allCheck = Utils.createCheckBox("Полностью", false);
    private JCheckBox startWithCheck = Utils.createCheckBox("С начала", false);
    private JCheckBox containsCheck = Utils.createCheckBox("Содержит", true);
    private ButtonGroup bg = new ButtonGroup();
    private DesignerFrame df;
    private int compareType = Constants.CONTAINS;
    private int areaType = Constants.CURRENT_INTERFACE;
    private OrGuiComponent comp = null;
    private Component cont = null;
    private SearchDialog parentDialog;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        init();
    }

    public void setDf(DesignerFrame df) {
        this.df = df;
    }

    private void init() {
        setPreferredSize(new Dimension(Or3Frame.instance().getSize().width - 200, 500));
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));

        areaCombo.addItem("Текущий интерфейс");
        areaCombo.addItem("Открытые интерфейсы");
        areaCombo.setSelectedIndex(0);
        areaCombo.addActionListener(this);
        areaType = Constants.CURRENT_INTERFACE;
        List<PropertyNode> attrs = PropertyReestr.getRegisterProperties();
        PropertyNode prop;
        int type;
        StringBuilder exclude = new StringBuilder();
        Collections.sort(attrs, new Comparator<PropertyNode>(){
            @Override
            public int compare(PropertyNode p1, PropertyNode p2) {
                          return p1.getFullName().compareTo(p2.getFullName());
            }
        });
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
        if (exclude.length() > 0) {
            exclude.append(')');
        }
        attrCombo.addItem("Все свойства" + exclude.toString());
        attrCombo.setSelectedIndex(attrCombo.getItemCount() - 1);
        attrCombo.addActionListener(this);
        findText.setPreferredSize(new Dimension(100, 20));
        browseBtn.addActionListener(this);

        bg.add(allCheck);
        bg.add(startWithCheck);
        bg.add(containsCheck);
        allCheck.addActionListener(this);
        startWithCheck.addActionListener(this);
        containsCheck.addActionListener(this);

        JPanel searchArea = new JPanel(new GridBagLayout());
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        Border tBorder = Utils.createTitledBorder(b, "Область поиска");
        searchArea.setBorder(tBorder);
        searchArea.add(areaLab, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        searchArea.add(areaCombo, new GridBagConstraints(1, 0, 2, 1, 1, 0, CENTER, HORIZONTAL, ins5505, 0, 0));

        searchArea.add(attrLab, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        searchArea.add(attrCombo, new GridBagConstraints(1, 1, 2, 1, 1, 0, CENTER, HORIZONTAL, ins5505, 0, 0));
        add(searchArea, new GridBagConstraints(1, 0, 3, 1, 1, 0, CENTER, HORIZONTAL, ins5500, 0, 0));

        JPanel searchRow = new JPanel(new GridBagLayout());
        searchRow.add(findLab, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        searchRow.add(findText, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        searchRow.add(browseBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, ins5505, 0, 0));

        JPanel searchCriteria = new JPanel(new GridBagLayout());
        tBorder = Utils.createTitledBorder(b, "Критерии поиска");
        searchCriteria.add(searchRow, new GridBagConstraints(0, 0, 4, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
        searchCriteria.add(allCheck, new GridBagConstraints(1, 1, 1, 1, 0, 0, WEST, NONE, Constants.INSETS_0, 0, 0));
        searchCriteria.add(startWithCheck, new GridBagConstraints(2, 1, 1, 1, 0, 0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
        searchCriteria.add(containsCheck, new GridBagConstraints(3, 1, 1, 1, 0, 0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
        searchCriteria.add(new JLabel(), new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, HORIZONTAL, Constants.INSETS_0, 0, 0));
        searchCriteria.setBorder(tBorder);
        add(searchCriteria, new GridBagConstraints(1, 1, 3, 1, 1, 0, CENTER, HORIZONTAL, ins5500, 0, 0));
        JScrollPane sp = new JScrollPane(resultTable);
        sp.setPreferredSize(new Dimension(500, 250));
        b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        tBorder = Utils.createTitledBorder(b, "Результат поиска");
        sp.setBorder(tBorder);
        add(sp, new GridBagConstraints(1, 2, 3, 3, 1, 1, CENTER, BOTH, ins5500, 0, 0));
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (resultTable.getRowCount() > 0 && resultTable.getSelectedRow() > -1) {
                        TableModel model = resultTable.getModel();
                        if (model instanceof SearchTableModel) {
                            comp = ((SearchTableModel) model).getSelectedGuiComponent(resultTable.getSelectedRow());
                            cont = ((SearchTableModel) model).getSelectedContainer(resultTable.getSelectedRow());
                            ((SearchDialog) getTopLevelAncestor()).dispose();
                        }
                    }
                }
                super.mouseClicked(e);
            }
        });

        allCheck.setOpaque(isOpaque);
        startWithCheck.setOpaque(isOpaque);
        containsCheck.setOpaque(isOpaque);
        resultTable.setOpaque(isOpaque);
        searchCriteria.setOpaque(isOpaque);
        searchRow.setOpaque(isOpaque);
        searchArea.setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
    }

    private void searchRecur(PropertyNode pn, OrGuiComponent c, String find, SearchTableModel model, String ifcName,
            OrGuiComponent container) {
    	if (c instanceof EmptyPlace) return;
        PropertyValue pv = c.getPropertyValue(pn);
        if (pv.equals(find, compareType)) {
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
        } else if (c instanceof OrScrollPane) {
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
                searchRecur(pn, panel, find, model, ifcName, container);
            }
        } else if (c instanceof OrCollapsiblePanel) {
            OrPanel panel = ((OrCollapsiblePanel) c).getContent();
            if (panel != null) {
                searchRecur(pn, panel, find, model, ifcName, container);
            }
        } else if (c instanceof OrAccordion) {
            List<OrPanel> panels = ((OrAccordion) c).getContent();
            if (panels != null) {
                for (OrPanel panel : panels) {
                    searchRecur(pn, panel, find, model, ifcName, container);
                }
            }
        }
    }

    private void searchAll(OrPanel p, SearchTableModel model, String ifcName, PropertyNode pn) {
        long langId = df.getInterfaceLang().id;
        String find = null;
        if (pn != null) {
            find = Funcs.normalizeInput(findText.getText());
            searchRecur(pn, p, find, model, ifcName, p);
        }
    }

    private void search(OrPanel p, SearchTableModel model, String ifcName) {
        searchAll(p, model, ifcName, (PropertyNode) attrCombo.getSelectedItem());
    }

    private void search(int areaType) {
        SearchTableModel model = new SearchTableModel(areaType);
        if (areaType == Constants.CURRENT_INTERFACE) {
            OrGuiComponent cont = tabbedContent.getOrGuiComponent();
            if (cont != null && cont instanceof OrPanel) {
                OrPanel p = (OrPanel) cont;
                String title = tabbedContent.getTitleAt(tabbedContent.getSelectedIndex());
                if (attrCombo.getSelectedItem() instanceof String) {
                    for (int i = 0; i < attrCombo.getItemCount() - 1; i++) {
                        searchAll(p, model, title, (PropertyNode) attrCombo.getItemAt(i));
                    }
                } else {
                    search(p, model, title);
                }
            }
        } else if (areaType == Constants.ALL_OPEN_INTERFACE) {
            String title;
            for (int i = 0; i < tabbedContent.getTabCount(); i++) {
                OrGuiComponent cont = tabbedContent.getOrGuiComponent(i);
                if (cont != null && cont instanceof OrPanel) {
                    OrPanel p = (OrPanel) cont;
                    title = tabbedContent.getTitleAt(i);
                    if (attrCombo.getSelectedItem() instanceof String) {
                        for (int j = 0; j < attrCombo.getItemCount() - 1; j++) {
                            searchAll(p, model, title, (PropertyNode) attrCombo.getItemAt(j));
                        }
                    } else {
                        search(p, model, title);
                    }
                }
            }
        }
        model.sort();
        resultTable.setModel(model);
        ResultTableCellRenderer tcr = new ResultTableCellRenderer();
        if (areaType == Constants.CURRENT_INTERFACE) {
            TableColumn tc = resultTable.getColumnModel().getColumn(0);
            tc.setPreferredWidth(90);
            tc = resultTable.getColumnModel().getColumn(1);
            tc.setCellRenderer(tcr);

            tc = resultTable.getColumnModel().getColumn(1);
            tc.setPreferredWidth(50);
            tc = resultTable.getColumnModel().getColumn(2);
            tc.setPreferredWidth(800);

        } else if (areaType == Constants.ALL_OPEN_INTERFACE) {
            TableColumn tc = resultTable.getColumnModel().getColumn(0);
            tc.setPreferredWidth(90);
            tc = resultTable.getColumnModel().getColumn(1);
            tc.setPreferredWidth(80);
            tc = resultTable.getColumnModel().getColumn(2);
            tc.setPreferredWidth(40);
            tc.setCellRenderer(tcr);
            tc = resultTable.getColumnModel().getColumn(3);
            tc.setPreferredWidth(500);
        } else {
            resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumn tc = resultTable.getColumnModel().getColumn(0);
            tc.setMaxWidth(500);
            tc.setPreferredWidth(250);
            tc.setMinWidth(100);
            for (int i = 1; i < resultTable.getColumnModel().getColumnCount(); i++) {
                tc = resultTable.getColumnModel().getColumn(i);
                tc.setMaxWidth(500);
                tc.setPreferredWidth(200);
                tc.setMinWidth(200);
                tc.setCellRenderer(tcr);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JButton) {
            if (parentDialog != null && src == parentDialog.getFindBtn()) {
                search(areaType);
            } else if (parentDialog != null && src == parentDialog.getClearBtn()) {
                resultTable.setModel(new SearchTableModel(Constants.CURRENT_INTERFACE));
            } else if (src == browseBtn) {
                try {
                    ClassNode cls = Kernel.instance().getClassNodeByName("Объект");
                    ClassBrowser cb = new ClassBrowser(cls, true);
                    DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите класс", cb);
                    dlg.show();
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                        findText.setText(cb.getSelectedPath());
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (src instanceof JCheckBox) {
            if (allCheck.isSelected()) {
                compareType = Constants.ALL;
            } else if (startWithCheck.isSelected()) {
                compareType = Constants.START_WHIH;
            } else if (containsCheck.isSelected()) {
                compareType = Constants.CONTAINS;
            }
        } else if (src == areaCombo) {
            areaType = "Текущий интерфейс".equals(((JComboBox) src).getSelectedItem()) ? Constants.CURRENT_INTERFACE
                    : Constants.ALL_OPEN_INTERFACE;

        }
    }

    class SearchTableModel extends AbstractTableModel {

        private String[] COL_NAMES = new String[] { "Интерфейс", "Компонент", "Свойство", "Значение" };
        private List<PropertySearchResult> data = new ArrayList<PropertySearchResult>();

        private int searchAreaType = Constants.CURRENT_INTERFACE;

        public SearchTableModel(int searchAreaType) {
            this.searchAreaType = searchAreaType;
        }

        public String getColumnName(int column) {
            return searchAreaType == Constants.CURRENT_INTERFACE ? COL_NAMES[column + 1] : COL_NAMES[column];
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

        public Component getSelectedContainer(int row) {
            return (Component) data.get(row).container;
        }

        public int getColumnCount() {
            if (searchAreaType == Constants.CURRENT_INTERFACE)
                return COL_NAMES.length - 1;
            else
                return COL_NAMES.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (searchAreaType == Constants.CURRENT_INTERFACE) {
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
            } else {
                OrGuiComponent c = data.get(rowIndex).comp;
                String className = c.getClass().getName().substring(Constants.COMPS_PACKAGE.length());

                if (columnIndex == 0) {
                    return data.get(rowIndex).ifcName;
                } else if (columnIndex == 1) {
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
                } else if (columnIndex == 2) {
                    PropertyNode pn = data.get(rowIndex).pn;
                    return pn.toString();
                } else if (columnIndex == 3) {
                    PropertyNode pn = data.get(rowIndex).pn;
                    PropertyValue pv = c.getPropertyValue(pn);
                    return pv;
                }
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

    public void setParentDialog(SearchDialog parent) {
        parentDialog = parent;
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

    public JLabel getImageLab() {
        return imageLab;
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
