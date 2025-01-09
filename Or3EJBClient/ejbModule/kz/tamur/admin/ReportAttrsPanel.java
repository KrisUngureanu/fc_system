package kz.tamur.admin;

import com.cifs.or2.client.Kernel;

import com.cifs.or2.client.QRComparator;
import com.cifs.or2.client.SearchAndReplaceDialog;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.ods.ComparisonOperations;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import static kz.tamur.rt.Utils.createMenuItem;
public class ReportAttrsPanel extends JPanel
        implements ActionListener {
    private final static String[] columnNames_ =
            {"Наименование", "Атрибут", "Тип", "Корень", "Титулы", "Глубина"};

    // Data elements
    private long classId_;
    KrnClass clsQRAttrs_;
    KrnClass cls_;
    private ArrayList reportattrs_ = new ArrayList();

    // Gui elements
    private ReportAttrsTableModel model_ = new ReportAttrsTableModel();
    private JTable names_ = new JTable(model_);

    private JButton addBtn_ = ButtonsFactory.createToolButton("createAll","", true);
    private JButton removeBtn_ = ButtonsFactory.createToolButton("deleteAll","", true);
    //private JButton applyBtn_ = new JButton("Применить");

    JPopupMenu findOperations = new JPopupMenu();
    JMenuItem findItem = createMenuItem("Найти...");
    JMenuItem replaceItem = createMenuItem("Заменить...");

    int col = 0;

    public ReportAttrsPanel(final long classId) throws KrnException {
        classId_ = classId;
        clsQRAttrs_ = Kernel.instance().getClassByName("QRAttrs");
        // Data initalization
        final Kernel krn = Kernel.instance();

        cls_ = krn.getClassNode(classId).getKrnClass();
        KrnAttribute atrClassName = krn.getAttributeByName(clsQRAttrs_, "className");

        KrnObject[] ks = krn.getObjectsByAttribute(clsQRAttrs_.id, atrClassName.id, 0, ComparisonOperations.CO_EQUALS, cls_.name, 0);
        for (int i = 0; i < ks.length; i++) {
            QRAttr qr = new QRAttr(ks[i]);
            reportattrs_.add(qr);
        }

        // GUI intialization
        setLayout(new BorderLayout());

        Dimension btnSize = new Dimension(100, 30);
        //applyBtn_.setPreferredSize(btnSize);
        //applyBtn_.addActionListener(this);
        addBtn_.setPreferredSize(btnSize);
        addBtn_.addActionListener(this);
        removeBtn_.setPreferredSize(btnSize);
        removeBtn_.addActionListener(this);

        JPanel btnPanel = new JPanel();
        //btnPanel.setLayout(new GridLayout(3, 1));
        //btnPanel.add(applyBtn_);
        btnPanel.add(addBtn_);
        btnPanel.add(removeBtn_);

        JScrollPane sp = new JScrollPane(names_);
        //sp.setPreferredSize(new Dimension(400, 200));

        add(sp, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.NORTH);

        model_.fireTableDataChanged();
        names_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
        int width = ssz.width;

        names_.getColumnModel().getColumn(0).setPreferredWidth(width/5-1);
        names_.getColumnModel().getColumn(1).setPreferredWidth(width*7/10-1);
        TableColumn valueCol = names_.getColumnModel().getColumn(1);
        valueCol.setCellEditor(new PropCellEditor());
        names_.getColumnModel().getColumn(2).setPreferredWidth(width/40-1);
        names_.getColumnModel().getColumn(3).setPreferredWidth(width/40-1);
        names_.getColumnModel().getColumn(4).setPreferredWidth(width/40-1);
        names_.getColumnModel().getColumn(5).setPreferredWidth(width/40-1);
        valueCol = names_.getColumnModel().getColumn(3);
        valueCol.setCellEditor(new PropCellEditor());
        valueCol = names_.getColumnModel().getColumn(4);
        valueCol.setCellEditor(new PropCellEditor());

        QRMouseAdapter qrma = new QRMouseAdapter();
        names_.getTableHeader().addMouseListener(qrma);

        findItem.addActionListener(this);
        findOperations.add(findItem);
        replaceItem.addActionListener(this);
        findOperations.add(replaceItem);
        addBtn_.setText("Добавить");
        addBtn_.setPreferredSize(new Dimension(100, 25));
        removeBtn_.setText("Удалить");
        removeBtn_.setPreferredSize(new Dimension(100, 25));
    }

    public void actionPerformed(ActionEvent e) {
        final Kernel krn = Kernel.instance();
        final Object src = e.getSource();

        try {
            if (src == addBtn_) {
                Container cont = getTopLevelAncestor();
                ClassBrowser cb = new ClassBrowser(krn.getClassNode(classId_), true);
                DesignerDialog dlg = null;
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog)cont, "", cb);
                } else {
                    dlg = new DesignerDialog((Frame)cont, "", cb);
                }
                Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
                dlg.setBounds(0, 0, ssz.width, ssz.height-30);
                dlg.show();
                if (dlg.isOK()) {
                    int count = cb.getSelectedAttributesCount();
                    for (int i=0; i<count; i++) {
                        KrnAttribute[] attrs = cb.getSelectedAttributes(i);

                        String path = com.cifs.or2.client.Utils.getPathForAttributes(attrs);

                        if (path != null && path.length() > 0) {
                            KrnObject c = krn.createObject(clsQRAttrs_, 0);
                            QRAttr qr = new QRAttr(c);
                            reportattrs_.add(qr);
                            try {
                                qr.setPath(path, true);
                                qr.setName(path, true);
                                qr.setStringValue("className", 0, cls_.name);
                                qr.setType(0, true);
                            } catch (KrnException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    model_.fireTableDataChanged();
                }

            } else if (src == removeBtn_) {
                int[] rows = names_.getSelectedRows();
                ArrayList qrs = new ArrayList();
                for (int i = 0; i < rows.length; i++) {
                    QRAttr qr = (QRAttr) reportattrs_.get(rows[i]);
                    qrs.add(qr);
                    krn.deleteObject(qr.obj, 0);
                }
                reportattrs_.removeAll(qrs);
                model_.fireTableDataChanged();
            } else if (src == findItem) {

            } else if (src == replaceItem) {
                SearchAndReplaceDialog rDlg =
                        new SearchAndReplaceDialog (null, names_, reportattrs_, col);
                rDlg.setVisible(true);
                if (rDlg.result == SearchAndReplaceDialog.REPLACE_RESULT) {
                    String find = rDlg.find;
                    String replace = rDlg.replace;
                    replace(find, replace, 0);
                } else if (rDlg.result == SearchAndReplaceDialog.REPLACE_ALL_RESULT) {
                    String find = rDlg.find;
                    String replace = rDlg.replace;
                    replace(find, replace, 1);
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void replace(String find, String replace, int mode) {
        model_.replace(find, replace, mode, names_.getSelectedRow(), col);
    }

    private void replaceString(String find, String replace, int rowIndex, int colIndex) {
        String original = (String) model_.getValueAt(rowIndex, colIndex);
        int start = original.indexOf(find);
        if (start > -1) {
            String newValue = original.substring(0, start) + replace +
                    original.substring(start+find.length(), original.length());
            if (!original.equals(newValue))
                model_.setValueAt(newValue, rowIndex, colIndex);
        }
    }

    private class ReportAttrsTableModel extends AbstractTableModel {
        public int getColumnCount() {
            return columnNames_.length;
        }

        public int getRowCount() {
            return reportattrs_.size();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (((Integer) getValueAt(rowIndex, 2)).intValue() == 1) return true;
            if (columnIndex < 3) return true;
            return false;
        }

        public String getColumnName(int column) {
            return columnNames_[column];
        }

        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                case 1:
                case 3:
                case 4:
                    return String.class;
            }
            return Integer.class;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            QRAttr qr = (QRAttr) reportattrs_.get(rowIndex);
            try {
                switch (columnIndex) {
                    case 0:
                        qr.setName((String) aValue, true);
                        break;
                    case 1:
                        qr.setPath((String) aValue, true);
                        break;
                    case 2:
                        qr.setType(((Integer) aValue).intValue(), true);
                        break;
                    case 3:
                        qr.setRoot((String) aValue, true);
                        break;
                    case 4:
                        qr.setTitles((String) aValue, true);
                        break;
                    case 5:
                        qr.setDepth(((Integer) aValue).intValue(), true);
                        break;
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            QRAttr qr = (QRAttr) reportattrs_.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return qr.getName();
                case 1:
                    return qr.getPath();
                case 2:
                    return new Long(qr.getType());
                case 3:
                    return qr.getRoot();
                case 4:
                    return qr.getTitles();
                case 5:
                    return new Long(qr.getDepth());
            }
            return null;
        }

        public void replace(String find, String replace, int mode, int rowIndex,
                            int colIndex) {
            if (mode == 0) {
                if (rowIndex == -1) {
                    return;
                }
                replaceString(find, replace, rowIndex, colIndex);
                fireTableRowsUpdated(rowIndex, rowIndex);
            } else {
                for (int i = 0; i < reportattrs_.size(); i++) {
                    replaceString(find, replace, i, colIndex);
                }
                fireTableRowsUpdated(0, reportattrs_.size());
            }
        }
    }

    class PropCellEditor extends AbstractCellEditor implements TableCellEditor,
            ActionListener {
        private Object value_;
        private JButton btn_ = new JButton();
        private JTextField tf_ = new JTextField();
        private boolean textMode = false;

        public PropCellEditor() {
            btn_.setBorder(null);
            btn_.addActionListener(this);
        }

        public Object getCellEditorValue() {
            if (textMode)
                return tf_.getText();
            else return value_;
        }

        public boolean isCellEditable(EventObject event) {
            if (event instanceof MouseEvent) {
	            textMode = false;
                if (((MouseEvent) event).getClickCount() < 2) {
                    return false;
                }
            } else
                textMode = true;
        return true;
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            String str = (String) value;

            if (textMode) {
                tf_.setText(str);
                return tf_;
            } else
                btn_.setText(str);
            return btn_;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == tf_) {
                stopCellEditing();
            } else {
                ClassBrowser cb = new ClassBrowser(null, true);
                Container cont = getTopLevelAncestor();
                DesignerDialog dlg = null;
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog)cont, "Классы",cb);
                } else {
                    dlg = new DesignerDialog((Frame)cont, "Классы",cb);
                }
                dlg.show();
                if (dlg.isOK()) {
                    KrnAttribute[] attrs = cb.getSelectedAttributes();
                    value_ = com.cifs.or2.client.Utils.getPathForAttributes(attrs);
                    stopCellEditing();
                } else
                    cancelCellEditing();
            }
        }
    }

    class QRMouseAdapter extends MouseAdapter {
        QRComparator qrcName;
        QRComparator qrcPath;

        public QRMouseAdapter() {
            qrcName = new QRComparator(-1, 0);
            qrcPath = new QRComparator(-1, 1);
        }

        public void mouseReleased(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            col = h.getColumnModel().getColumnIndexAtX(e.getX());
            col = h.getColumnModel().getColumn(col).getModelIndex();

            if (e.isPopupTrigger()) {
                findOperations.show(e.getComponent(), e.getX(), e.getY());
            } else {
                if (h.getTable().equals(names_)) {
                    if (col == 0) {
                        Object[] arr = reportattrs_.toArray();
                        qrcName.changeDirection();
                        Arrays.sort(arr, qrcName);
                        reportattrs_.clear();
                        for (int i = 0; i<arr.length; i++) {
                            reportattrs_.add(arr[i]);
                        }
                        model_.fireTableDataChanged();
                    } else if (col == 1) {
                        Object[] arr = reportattrs_.toArray();
                        qrcPath.changeDirection();
                        Arrays.sort(arr, qrcPath);
                        reportattrs_.clear();
                        for (int i = 0; i<arr.length; i++) {
                            reportattrs_.add(arr[i]);
                        }
                        model_.fireTableDataChanged();
                    }
                }
            }
        }

        public void mouseClicked(MouseEvent e) {

        }
    }
}
