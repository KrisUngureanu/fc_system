package kz.tamur.guidesigner;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyReestr;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.util.expr.Editor;


/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 16.09.2004
 * Time: 10:12:03
 * To change this template use File | Settings | File Templates.
 */

public class DebugPanel extends JPanel implements ActionListener {

    private JTable itemsTable = new JTable(new DebugPropertyModel());
    private int areaType = Constants.CURRENT_INTERFACE;
    private SearchTableModel resTableModel = new SearchTableModel();
    private JTable resultTable = new JTable(resTableModel);
    private JToggleButton areaBtn = ButtonsFactory.createFunctionButton(ButtonsFactory.FN_AREA);
    private JButton attrsBtn = ButtonsFactory.createToolButton("DebugAttrs", "Атрибуты");
    private JButton runBtn = ButtonsFactory.createToolButton("runDebug", "Запустить");
    private JButton clearBtn = ButtonsFactory.createToolButton("Cancel", "Очистить");
    private JButton closeBtn = new JButton(kz.tamur.rt.Utils.getImageIcon("HideMode"));
    private DesignerFrame df;
    private OrGuiComponent comp = null;
    private Component cont = null;
    private PropertyNode[] selectedProperties = null;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public DebugPanel() {
        super();
        setLayout(new BorderLayout());
        init();
    }

    public void setDf(DesignerFrame df) {
        this.df = df;
    }



    private void init() {
        closeBtn.setContentAreaFilled(false);
        areaBtn.addActionListener(this);
        areaBtn.setToolTipText("Область проверки");
        itemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        itemsTable.setFont(Utils.getDefaultFont());

        resultTable.setFont(Utils.getDefaultFont());
        resultTable.setGridColor(Utils.getLightSysColor());
        resultTable.setBackground(Utils.getLightSysColor());

        JScrollPane sp = new JScrollPane(resultTable);
        sp.setPreferredSize(new Dimension(500, 200));
        add(sp, BorderLayout.CENTER);

        attrsBtn.addActionListener(this);
        runBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        JToolBar tb = kz.tamur.comps.Utils.createDesignerToolBar();
        tb.setOrientation(JToolBar.VERTICAL);
        tb.add(areaBtn);
        tb.add(attrsBtn);
        tb.add(runBtn);
        tb.add(clearBtn);

        add(tb, BorderLayout.WEST);
        TableColumn tc = resultTable.getColumnModel().getColumn(resultTable.getColumnCount() - 1);
        tc.setCellRenderer(new ErrorCellRenderer());
        resultTable.setBackground(Utils.getLightSysColor());
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (resultTable.getRowCount() > 0 &&
                            resultTable.getSelectedRow() > -1) {
                        TableModel model = resultTable.getModel();
                        if (model instanceof SearchTableModel) {
                            comp = ((SearchTableModel)model).getSelectedGuiComponent(
                                    resultTable.getSelectedRow());
                            cont = ((SearchTableModel)model).getSelectedContainer(
                                    resultTable.getSelectedRow());
                            DesignerFrame.tabbedContent.setSelectedComponent(cont);
                            df.getController().addSelection(comp, false);
                        }
                    }
                }
                super.mouseClicked(e);
            }
        });
        selectedProperties =
                ((DebugPropertyModel)itemsTable.getModel()).getSelectedProperties();
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(Utils.getMidSysColor());
        titlePanel.setPreferredSize(new Dimension(100, 20));
        JLabel lab = Utils.createLabel("Режим отладки");
        lab.setForeground(Color.white);
        lab.setFont(new Font("Tahoma", Font.BOLD, 11));
        lab.setIcon(kz.tamur.rt.Utils.getImageIcon("debug"));
        lab.setIconTextGap(3);
        titlePanel.add(lab, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        titlePanel.add(new JLabel(""), new GridBagConstraints(1, 0, 4, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setPreferredSize(new Dimension(18, 18));
        closeBtn.setMinimumSize(new Dimension(18, 18));
        closeBtn.setMaximumSize(new Dimension(18, 18));
        closeBtn.setOpaque(false);
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                df.closeDebug();
            }
        });
        titlePanel.add(closeBtn, new GridBagConstraints(5, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        add(titlePanel, BorderLayout.NORTH);
    }


    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JButton) {
            if (src == runBtn) {
                resTableModel = new SearchTableModel();
                resultTable.setModel(resTableModel);
                TableColumn tc = resultTable.getColumnModel().getColumn(resultTable.getColumnCount() - 1);
                tc.setCellRenderer(new ErrorCellRenderer());
                if (areaType == Constants.CURRENT_INTERFACE) {
                    run((OrGuiContainer)DesignerFrame.tabbedContent.getOrGuiComponent(),
                            DesignerFrame.tabbedContent.getOrGuiComponent(),
                            DesignerFrame.tabbedContent.getSelectedIndex());
                } else if (areaType == Constants.ALL_OPEN_INTERFACE) {
                    for (int i = 0; i < DesignerFrame.tabbedContent.getTabCount(); i++) {
                        OrGuiComponent c = DesignerFrame.tabbedContent.getOrGuiComponent(i);
                        run(c, c, i);
                    }
                }
            } else if (src == clearBtn) {
                resTableModel = new SearchTableModel();
                resultTable.setModel(resTableModel);
                TableColumn tc = resultTable.getColumnModel().getColumn(resultTable.getColumnCount() - 1);
                tc.setCellRenderer(new ErrorCellRenderer());
            } else if (src == attrsBtn) {
                
                itemsTable.setOpaque(isOpaque);
                
                JScrollPane sp = new JScrollPane(itemsTable);
                
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                
                sp.setPreferredSize(new Dimension(470, 200));
                DesignerDialog dlg = new DesignerDialog(
                        (JFrame)df.getTopLevelAncestor() , "Атрибуты проверки", sp);
                dlg.show();
                if (dlg.isOK()) {
                    DebugPropertyModel model =
                            (DebugPropertyModel)itemsTable.getModel();
                    selectedProperties = model.getSelectedProperties();
                }
            }
        }
        if (src instanceof JToggleButton) {
            JToggleButton tb = (JToggleButton)src;
            if (tb == areaBtn) {
                if (!tb.isSelected()) {
                    areaType = Constants.CURRENT_INTERFACE;
                } else {
                    areaType = Constants.ALL_OPEN_INTERFACE;
                }
                resTableModel = new SearchTableModel();
                resultTable.setModel(resTableModel);
                TableColumn tc = resultTable.getColumnModel().getColumn(resultTable.getColumnCount() - 1);
                tc.setCellRenderer(new ErrorCellRenderer());
            }
        }
    }

    class ErrorItem extends Object {

        private OrGuiComponent container;
        private OrGuiComponent component;
        private PropertyNode pn;
        private PropertyValue value;
        private String errorMessage;
        private int ifcIndex;

        public ErrorItem(OrGuiComponent container, OrGuiComponent component,
                         PropertyNode pn, PropertyValue value, String errorMessage, int ifcIdx) {
            this.container = container;
            this.component = component;
            this.pn = pn;
            this.value = value;
            this.errorMessage = errorMessage;
            this.ifcIndex = ifcIdx;
        }

        public int getIfcIndex() {
            return ifcIndex;
        }

        public OrGuiComponent getContainer() {
            return container;
        }

        public OrGuiComponent getComponent() {
            return component;
        }

        public PropertyNode getPn() {
            return pn;
        }

        public PropertyValue getValue() {
            return value;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    class SearchTableModel extends AbstractTableModel {

        private String[] COL_NAMES;
        private List data = new ArrayList();


        public SearchTableModel() {
            switch(areaType) {
                case 0:
                    COL_NAMES = new String[] {"Компонент", "Свойство",
                                              "Значение", "Ошибка"};
                    break;
                case 1:
                    COL_NAMES = new String[] {"Интерфейс", "Компонент", "Свойство",
                                              "Значение", "Ошибка"};
                    break;
            }
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public void addComponent(ErrorItem item) {
            data.add(item);
            fireTableDataChanged();
        }

        public OrGuiComponent getSelectedGuiComponent(int row) {
            ErrorItem item = (ErrorItem)data.get(row);
            return item.getComponent();
        }

        public Component getSelectedContainer(int row) {
            ErrorItem item = (ErrorItem)data.get(row);
            return (Component)item.getContainer();
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            ErrorItem item = (ErrorItem)data.get(rowIndex);
            String title = "";
            OrGuiComponent c = item.getComponent();
            String className = c.getClass().getName().substring(
                    Constants.COMPS_PACKAGE.length());
            PropertyValue pv = null;
            if (!(c instanceof OrTableColumn)) {
                pv = c.getPropertyValue(c.getProperties().getChild("title"));
            } else {
                pv = c.getPropertyValue(
                        c.getProperties().getChild("header").getChild("text"));
            }
            if (!pv.isNull()) {
                title = pv.stringValue() + " [" + className +"]";
            } else {
                title = title + " [" + className +"]";
            }
            if (areaType == Constants.CURRENT_INTERFACE) {
                switch(columnIndex) {
                    case 0:
                        return title;
                    case 1:
                        return item.getPn();
                    case 2:
                        return item.getValue();
                    case 3:
                        return item.getErrorMessage();
                }
            } else {
                switch(columnIndex) {
                    case 0:
                        return DesignerFrame.tabbedContent.getTitleAt(item.getIfcIndex());
                    case 1:
                        return title;
                    case 2:
                        return item.getPn();
                    case 3:
                        return item.getValue();
                    case 4:
                        return item.getErrorMessage();
                }
            }
            return null;
        }
    }

    private void run(OrGuiComponent container, OrGuiComponent parentIfc, int ifcIdx) {
        if (container instanceof OrGuiContainer && !(container instanceof OrTable)) {
            Container p = (Container)container;
            for (int i = 0; i < p.getComponentCount(); i++) {
                Component c = p.getComponent(i);
                if (c instanceof OrGuiContainer) {
                    run((OrGuiComponent)c, parentIfc, ifcIdx);
                } else if (c instanceof OrGuiComponent &&
                        !(c instanceof OrGuiContainer)) {
                    checkErrors((OrGuiComponent)c, parentIfc, ifcIdx);
                }
            }
        } else if (container instanceof OrTable) {
            OrTable tab = (OrTable)container;
            for (int i = 0; i < tab.getTableComponentCount(); i++) {
                checkErrors(tab.getTableComponent(i), parentIfc, ifcIdx);
            }
        }
    }

    private void checkErrors(OrGuiComponent c, OrGuiComponent container, int ifcIdx) {
        PropertyNode[] pns = selectedProperties;
        PropertyNode lastPn = null;
        PropertyValue lastPv = null;
        if (pns.length > 0) {
            for (int i = 0; i < pns.length; i++) {
                try {
                    PropertyNode pn = pns[i];
                    lastPn = pn;
                    if (pn.getType()==Types.REF) {
                        PropertyValue pv = c.getPropertyValue(pn);
                        if (pv != null && !pv.isNull()) {
                            lastPv = pv;
                            String value = pv.stringValue();
                            KrnAttribute[] attrs = Utils.getAttributesForPath(value);
                            if (attrs == null) {
                                ErrorItem item = new ErrorItem(
                                        container, c, pn, pv, "Ошибка данных", ifcIdx);
                                resTableModel.addComponent(item);
                            } else {
                                for (int j = 0; j < attrs.length; j++) {
                                    if (attrs[j] == null) {
                                        ErrorItem item = new ErrorItem(
                                                container, c, pn, pv, "Ошибка данных", ifcIdx);
                                        resTableModel.addComponent(item);
                                    }
                                }
                            }
                        }
                    } else if (pn.getType()==Types.EXPR) {
                        PropertyValue pv = c.getPropertyValue(pn);
                        if (pv != null && !pv.isNull()) {
                            String value = pv.stringValue();
                            Editor e = new Editor(value);
                            ArrayList<String> refPaths = e.getRefPaths();
                            for (int j = 0; j < refPaths.size(); ++j) {
                                String path = refPaths.get(j);
                                KrnAttribute[] attrs = Utils.getAttributesForPath(path);
                                if (attrs == null) {
                                    ErrorItem item = new ErrorItem(container, c, pn, pv, "Ошибка формулы", ifcIdx);
                                    resTableModel.addComponent(item);
                                } else {
                                    for (int k = 0; k < attrs.length; k++) {
                                        if (attrs[k] == null) {
                                            ErrorItem item = new ErrorItem(container, c, pn, pv, "Ошибка формулы", ifcIdx);
                                            resTableModel.addComponent(item);
                                        }
                                    }
                                }

                            }
                        }
                    }
                } catch(Exception e) {
                    ErrorItem item = new ErrorItem(
                            container, c, lastPn, lastPv, "Ошибка данных", ifcIdx);
                    resTableModel.addComponent(item);
                }
            }
        }

    }

    public OrGuiComponent getResultComponent() {
        return comp;
    }

    public Component getResultContainer() {
        return cont;
    }

    class DebugPropertyModel extends AbstractTableModel {

        private List data = new ArrayList();


        public DebugPropertyModel() {
            List l = PropertyReestr.getDebugProperties();
            for (int i = 0; i < l.size(); i++) {
                data.add(new PropertyItem(true, (PropertyNode)l.get(i)));
            }
        }

        public String getColumnName(int column) {
            if (column == 0) {
                return "";
            } else {
                return "Свойство";
            }
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return data.size();
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            PropertyItem item = (PropertyItem)data.get(rowIndex);
            switch (columnIndex) {
                    case 0:
                            return new Boolean(item.isSelected());
                    case 1:
                            return item.toString();
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                PropertyItem item = (PropertyItem)data.get(rowIndex);
                item.setSelected(((Boolean) aValue).booleanValue());
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0)
                return true;
            return false;
        }

        public PropertyNode[] getSelectedProperties() {
            List l = new ArrayList();
            for (int i = 0; i < data.size(); i++) {
                PropertyItem pi = (PropertyItem)data.get(i);
                if (pi.isSelected()) {
                    l.add(pi.getPropertyNode());
                }
            }
            PropertyNode[] res = new PropertyNode[l.size()];
            for (int i = 0; i < l.size(); i++) {
                res[i] = (PropertyNode)l.get(i);
            }
            return res;
        }
    }

    class PropertyItem extends Object {

        private boolean isSelected;
        private PropertyNode propertyNode;

        public PropertyItem(boolean selected, PropertyNode pn) {
            isSelected = selected;
            this.propertyNode = pn;
        }

        public PropertyNode getPropertyNode() {
            return propertyNode;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String toString() {
            return propertyNode.toString();
        }
    }

    class ErrorCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            if ("Ошибка данных".equals(value)) {
                setIcon(kz.tamur.rt.Utils.getImageIcon("DataError"));
            } else if ("Ошибка формулы".equals(value)) {
                setIcon(kz.tamur.rt.Utils.getImageIcon("ExprError"));
            }
            setText(value.toString());
            setFont(Utils.getDefaultFont());
            return this;
        }
    }
}
