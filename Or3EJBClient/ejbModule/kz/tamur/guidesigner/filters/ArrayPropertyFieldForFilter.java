package kz.tamur.guidesigner.filters;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.cifs.or2.kernel.KrnClass;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.DateField;
import kz.tamur.util.Funcs;

public class ArrayPropertyFieldForFilter extends JPanel implements ActionListener {
    private DesignerDialog owner;
    private Object[][] list;
    private JTable table=new JTable();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JScrollPane sp;
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton addBtn_ = ButtonsFactory.createToolButton("plus", "Добавить запись");
    private JButton delBtn_ = ButtonsFactory.createToolButton("minus", "Удалить запись");
    private DefaultTableModel model;
    private KrnClass cls;
    public ArrayPropertyFieldForFilter(KrnClass cls, Object vals[],int dateFormat)  {
        this.cls = cls;
        setLayout(new BorderLayout());
        owner =(DesignerDialog)getTopLevelAncestor();
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            if(cls.getName().equals("date")||cls.getName().equals("time")) {
            DateField dateField = new DateField();
            java.util.Date res=null;
            try {
                 dateField.setDateFormat(dateFormat);
                 if(!vals[i].equals("")) {
                res = Funcs.getDateFormat(dateFormat).parse((String)vals[i]);}
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateField.setValue(res);
            
            list[i][0]= dateField;}
            else 
            {JTextField textField = new JTextField();
            textField.setText((String)vals[i]);
            list[i][0] = textField;}
        }
        init();
    }
   
    private void init() {
        setOpaque(isOpaque);
        table.setOpaque(isOpaque);
        if (sp != null) {
            sp.setOpaque(isOpaque);
            sp.getViewport().setOpaque(isOpaque);
        }
        toolBar.add(addBtn_);
        toolBar.add(delBtn_);
        addBtn_.addActionListener(this);
        delBtn_.addActionListener(this);
        add(toolBar, BorderLayout.NORTH);
        model = new DefaultTableModel(list,new String[]{"Объект"});
        table = new JTable(model);
        ArrayPropertyCellEditor ce=new ArrayPropertyCellEditor();
        table.getColumnModel().getColumn(0).setCellEditor(ce);
        table.getColumnModel().getColumn(0).setCellRenderer(new ArrayPropertyCellRenderer());
        table.getTableHeader().setFont(Utils.getDefaultFont());
        table.getTableHeader().setForeground(Utils.getDarkShadowSysColor());
        table.setFont(Utils.getDefaultFont());
        add(sp=new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(new Dimension(400,500));
        setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
    }
    private class ArrayPropertyCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus, int row,
                                                     int column) {
            if (value instanceof DateField) {
                if(isSelected)
                    ((DateField)value).setBackground(Utils.getSysColor());
                else
                    ((DateField)value).setBackground(Color.white);
                return (DateField)value;
            }
            else if(value instanceof JTextField)
            {
                if(isSelected)
                    ((JTextField)value).setBackground(Utils.getSysColor());
                else
                    ((JTextField)value).setBackground(Color.white);
                return (JTextField)value;
            }
            else {
                return this;
            }
        }
    }
    private class ArrayPropertyCellEditor extends DefaultCellEditor {

        public ArrayPropertyCellEditor() {
            super(new DateField());
            
            delegate = new EditorDelegate() {
                public void setValue(Object value) {
                    this.value=value;
                    editorComponent=(JComponent)value;
                }

                public Object getCellEditorValue() {
                return value;
                }
                public boolean isCellEditable(EventObject anEvent) {
                    return true;
                }
            };
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                             boolean isSelected,
                             int row, int column) {
            delegate.setValue(value);
            if (value instanceof DateField)
                ((DateField)value).setBackground(Utils.getSysColor());
            return editorComponent;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src==addBtn_)
        {
            JTextField field;
            if(cls.getName().equals("date"))
            {
                field = new DateField();
                ((DateField)field).setValue(null);
            }
            else if(cls.getName().equals("time"))
                    {
                field = new DateField();
                
                ((DateField)field).setDateFormat(Constants.DD_MM_YYYY_HH_MM_SS_SSS);
                ((DateField)field).setValue(null);
            }
            else {
               
                field = new JTextField();
            }
 
            model.addRow(new Object[]{field});
        }
        else if (src == delBtn_) {
            int idx = table.getSelectedRow();
            if (idx != -1) {
                JTextField pf = (JTextField) model.getValueAt(idx, 0);
                int res;
                res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                        "Удалить объект \"" + pf.getText() + "\"?");
                if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_YES) {
                    table.getCellEditor(idx, 0).stopCellEditing();
                    model.removeRow(idx);
                    if (table.getRowCount() > 0)
                        table.setRowSelectionInterval(idx == 0 ? 0 : idx - 1, idx == 0 ? 0 : idx - 1);
                    pf.setText("");
                }
            }
        }
    }
    public Vector getList(){
        return  model.getDataVector();
    }
}
