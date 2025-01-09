package kz.tamur.guidesigner.filters;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.VERTICAL;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.MSTRING;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STRING;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.PropertyReestr;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.SearchResultTable;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.CursorToolkit;

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
    private EventListenerList listenerList = new EventListenerList();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private String replaceTo;
    private String find;
    
    private FiltersPanel fitersPanel;
    private OrFilterTree filterTree;
    private OrFilterNode filterNode;

    public ReplacePanel(FiltersPanel fitersPanel, OrFilterTree filterTree) {
        super();
        this.fitersPanel = fitersPanel;
        this.filterTree = filterTree;
        init();
    }
    
    public OrFilterNode getFilterNode() {
    	return filterNode;
    }

    private void init() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(Or3Frame.instance().getSize().width - 200, 620));
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
        List<PropertyNode> attrs = PropertyReestr.getRegisterFilterProperties();
        PropertyNode prop;
        int type;
        StringBuilder exclude = new StringBuilder();
        for (int i = 0; i < attrs.size(); i++) {
            prop = attrs.get(i);
            prop.setPlainMode(true);
            type = prop.getType();
            if (type == REF || type == MSTRING || type == EXPR || type == RSTRING || type == STRING) {
                attrCombo.addItem(prop);
            } else {
                exclude.append(exclude.length() > 0 ? ", " : "").append(prop.toString());
            }
        }
        if (exclude.length() > 0) {
            exclude.insert(0, " (исключая ").append(")");
        }
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
        TitledBorder tBorder = BorderFactory.createTitledBorder(b, "Результат поиска", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Utils.getDefaultFont(), Utils.getDarkShadowSysColor());
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
                        	filterNode = ((SearchTableModel) model).getSelectedFilterNode(resultTable.getSelectedRow());
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

    private void searchRecur(PropertyNode pn, OrFilterNode filterNode, String find, SearchTableModel model, String filterName) {
    	PropertyValue pv = filterNode.getPropertyValue(pn);
        if (pv.equals(find, Constants.CONTAINS)) {
            model.addComponent(filterName, filterNode, pn);
        }
    	for (int i = 0; i < filterNode.getChildCount(); i++) {
    		searchRecur(pn, (OrFilterNode) filterNode.getChildAt(i), find, model, filterName);
    	}
    }

    private void search(SearchTableModel model, String filterName) {
        Object selectedProperty = attrCombo.getSelectedItem();
        OrFilterNode filterNode = filterTree.getRoot();
        if (selectedProperty instanceof String) {
            for (int i = 0; i < attrCombo.getItemCount() - 2; i++) {
                PropertyNode pn = (PropertyNode) attrCombo.getItemAt(i);
                searchRecur(pn, filterNode, this.find, model, filterName);
            }
        } else {
            searchRecur(((PropertyNode) selectedProperty), filterNode, this.find, model, filterName);
        }
    }

    private void search() {
        SearchTableModel model = new SearchTableModel();
        search(model, DesignerFrame.tabbedContent.getTitleAt(DesignerFrame.tabbedContent.getSelectedIndex()));
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
        find = Funcs.normalizeInput(findText.getText());
        if (src == findBtn) {
            search();
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
                    OrFilterNode filterNode = model.getSelectedFilterNode(row);
                    PropertyNode pn = model.getSelectedProperty(row);
                    replace(filterNode, pn);
                    model.fireTableDataChanged();
                    firePropertyModified(filterNode);
                }
            }
        } else if (src == doReplaceAllBtn) {
            if (!find.isEmpty() && resultTable.getRowCount() > 0) {
                CursorToolkit.startWaitCursor(this);
                SearchTableModel model = (SearchTableModel) resultTable.getModel();
                OrFilterNode filterNode;
                PropertyNode pn;
                for (int i = 0; i < resultTable.getRowCount(); i++) {
                    filterNode = model.getSelectedFilterNode(i);
                    pn = model.getSelectedProperty(i);
                    replace(filterNode, pn);
                    firePropertyModified(filterNode);
                }
                model.fireTableDataChanged();
                CursorToolkit.stopWaitCursor(this);
                StringBuilder mes = new StringBuilder().append("Произведено ").append(resultTable.getRowCount()).append(" замен значений.");
                MessagesFactory.showMessageDialog((Dialog) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, mes.toString());
            }
        }
    }

    class SearchTableModel extends AbstractTableModel {

        private final String[] COL_NAMES = new String[] { "Узел", "Свойство", "Значение" };
        private List<PropertySearchResult> data = new ArrayList<PropertySearchResult>();

        public SearchTableModel() {}

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public void addComponent(String filterName, OrFilterNode filterNode, PropertyNode pn) {
            PropertySearchResult psr = new PropertySearchResult(filterName, filterNode, pn);
            if (!data.contains(psr)) {
                data.add(psr);
                fireTableDataChanged();
            }
        }

        public void sort() {
            Collections.sort(data, new Comparator<PropertySearchResult>() {
                public int compare(PropertySearchResult arg0, PropertySearchResult arg1) {
                	if (arg0.filterName.compareTo(arg1.filterName) == 0) {
                    	if (arg0.filterNode.getTitle().compareTo(arg1.filterNode.getTitle()) == 0) {
                        	return arg0.pn.getFullName().compareTo(arg1.pn.getFullName());
                    	}
                    	arg0.filterNode.getTitle().compareTo(arg1.filterNode.getTitle());
                	}
                	return arg0.filterName.compareTo(arg1.filterName);
                }

            });
        }

        public OrFilterNode getSelectedFilterNode(int row) {
            return data.get(row).filterNode;
        }
        
        public PropertyNode getSelectedProperty(int row) {
            return data.get(row).pn;
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return data.get(rowIndex).filterNode.getTitle();
            } else if (columnIndex == 1) {
                PropertyNode pn = data.get(rowIndex).pn;
                return pn.toString();
            } else if (columnIndex == 2) {
                PropertyNode pn = data.get(rowIndex).pn;
                PropertyValue pv = data.get(rowIndex).filterNode.getPropertyValue(pn);
                return pv;
            }

            return null;
        }
    }

    private void replace(OrGuiComponent c, PropertyNode pn) {
        PropertyValue pv = c.getPropertyValue(pn);
        String currentValue = "";
        if (pv != null && !pv.isNull()) {
            int type = pn.getType();
            if (type == REF || type == MSTRING || type == EXPR || type == STRING) {
                currentValue = pv.stringValue();
                String newVal = currentValue.replace(find, replaceTo);
                PropertyValue newValue = new PropertyValue(newVal, fitersPanel.getInterfaceLanguage().id, pn);
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
            }
        }
    }
    
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
        String filterName;
        OrFilterNode filterNode;
        PropertyNode pn;

        public PropertySearchResult(String filterName, OrFilterNode filterNode, PropertyNode pn) {
            this.filterName = filterName;
            this.filterNode = filterNode;
            this.pn = pn;
        }
    }
}