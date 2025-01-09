package kz.tamur.guidesigner.languages;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;

/*
 * User: vital
 * Date: 09.12.2005
 * Time: 9:45:10
 */

public class LangRegPanel extends JPanel {

    private JTable table = new JTable();
    private List items = new ArrayList();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public LangRegPanel() {
        setLayout(new BorderLayout());
        loadLanguages();
        table.setModel(new LanguageTableModel());
        CellRenderer cr = new CellRenderer();
        TableColumn tc = table.getColumnModel().getColumn(0);
        tc.setMaxWidth(30);
        tc.setCellRenderer(cr);
        table.getColumnModel().getColumn(1).setCellRenderer(cr);
        tc = table.getColumnModel().getColumn(2);
        tc.setCellRenderer(cr);
        tc.setCellEditor(new CellEditor(new JTextField()));
        tc.setMaxWidth(50);
        table.setOpaque(isOpaque);
        JScrollPane scroller = new JScrollPane(table);
        scroller.setOpaque(isOpaque);
        scroller.getViewport().setOpaque(isOpaque);
        add(scroller, BorderLayout.CENTER);
    }

    private void loadLanguages() {
        items.clear();
        try {
            final Kernel krn = Kernel.instance();
            long[] langIds = Funcs.makeObjectIdArray(Kernel.LANGUAGES);
            StringValue[] svs = krn.getStringValues(
                    langIds, Kernel.SC_LANGUAGE.id, "name", 0, false, 0);
            StringValue[] codes = krn.getStringValues(
                    langIds, Kernel.SC_LANGUAGE.id, "code", 0, false, 0);
            LongValue[] selectedLangs = krn.getLongValues(langIds,
                    Kernel.SC_LANGUAGE.id, "lang?", 0);
            for (int i = 0; i < svs.length; i++) {
                StringValue sv = svs[i];
                String code = "N/A";
                boolean isRegistered = false;
                for (int j = 0; j < codes.length; j++) {
                    StringValue codeValue = codes[j];
                    if (codeValue.objectId == sv.objectId && codeValue.value != null) {
                        code = codeValue.value;
                        break;
                    }
                }
                for (int j = 0; j < selectedLangs.length; j++) {
                    LongValue selValue = selectedLangs[j];
                    if (selValue.objectId == sv.objectId) {
                        isRegistered = selValue.value == 1;
                        break;
                    }
                }
                Language lang = new Language(code, sv.value, isRegistered, false);
                items.add(lang);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Collections.sort(items);
    }

    class LanguageTableModel extends AbstractTableModel {

        private String[] COL_NAMES = new String[] {"Код", "Наименование", " "};

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return items.size();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Language lang = (Language)items.get(rowIndex);
/*
            switch(columnIndex) {
                case 0:
                    return lang.code;
                case 1:
                    return lang.name;
                case 2:
                    return new Boolean(lang.isRegistered);
            }
*/
            return lang;
        }
    }

    class Language implements Comparable {
        String code;
        String name;
        boolean isRegistered;
        boolean isModified;

        public Language(String code, String name, boolean registered, boolean modified) {
            this.code = code;
            this.name = name;
            isRegistered = registered;
            isModified = modified;
        }

        public int compareTo(Object o) {
            return ((Language)o).name.compareTo(name)*-1;
        }
    }

    class CellRenderer extends DefaultTableCellRenderer {
        private JCheckBox checkBox = Utils.createCheckBox("", false);

        public CellRenderer() {
            checkBox.setBackground(Color.white);
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel)super.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);
            Language l = (Language)value;
            if (l.isModified) {
                label.setForeground(Color.red);
            } else {
                label.setForeground(Color.black);
            }
            if (l.isRegistered) {
                label.setOpaque(true);
                label.setBackground(Utils.getLightYellowColor());
                checkBox.setBackground(Utils.getLightYellowColor());
            } else {
                label.setOpaque(false);
                label.setBackground(Color.white);
                checkBox.setBackground(Color.white);
            }
            switch(column) {
                case 0:
                    label.setText(l.code);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                case 1:
                    label.setText(l.name);
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    return label;
                case 2:
/*
                    if (isSelected) {
                        checkBox.setBackground(Utils.getMidSysColor());
                    } else {
                        checkBox.setBackground(Color.white);
                    }
*/
                    checkBox.setSelected(l.isRegistered);
                    return checkBox;
            }
            return null;
        }
    }

    class CellEditor extends DefaultCellEditor {

        private JCheckBox checkBox = Utils.createCheckBox("", false);
        private Language lang;
        public CellEditor(final JTextField textField) {
            super(textField);
            checkBox.setBackground(Color.white);
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (lang != null) {
                        lang.isModified = true;
                        lang.isRegistered = checkBox.isSelected();
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected,
                                                     int row, int column) {
            Language l = (Language)value;
            lang = l;
            if (column == 2) {
                checkBox.setSelected(l.isRegistered);
                return checkBox;
            }
            return null;
        }
    }

    private List prepareSave() {
        List res = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            Language language = (Language) items.get(i);
            if (language.isModified) {
                res.add(language);
            }
        }
        return res;
    }

    public void save() {
        List l = prepareSave();
        try {
            final Kernel krn = Kernel.instance();
            KrnClass cls = krn.getClassByName("Language");
            long[] langIds = Funcs.makeObjectIdArray(Kernel.LANGUAGES);
            StringValue[] svs = krn.getStringValues(
                    langIds, Kernel.SC_LANGUAGE.id, "name", 0, false, 0);
            for (int i = 0; i < svs.length; i++) {
                StringValue sv = svs[i];
                for (int j = 0; j < l.size(); j++) {
                    Language o =  (Language)l.get(j);
                    if (o.name.equals(sv.value)) {
                        krn.setLong(sv.objectId, cls.id, "lang?", 0,
                                (o.isRegistered) ? 1 : 0, 0);
                        break;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



}
