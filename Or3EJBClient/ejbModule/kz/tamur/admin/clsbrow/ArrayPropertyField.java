package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

class ArrayPropertyField extends JPanel implements ActionListener {
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton addBtn_ = ButtonsFactory.createToolButton("plus", "Добавить запись");
    private JButton delBtn_ = ButtonsFactory.createToolButton("minus", "Удалить запись");
    private KrnAttribute attr_;
    private Object[][] list;
    private DefaultTableModel model;
    private Vector<PropertyField> delObjects=new Vector<PropertyField>();
    private JTable table=new JTable();
    private KrnObject obj_;
    private DesignerDialog owner;
    private KrnObject linkValue;
    private int li;
    private JScrollPane sp;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public ArrayPropertyField(KrnObject obj_,KrnAttribute attr, Vector<PropertyField> vals)
            throws KrnException {
        setLayout(new BorderLayout());

        this.owner =(DesignerDialog)getTopLevelAncestor();

        this.obj_=obj_;
        attr_ = attr;
        for(PropertyField pf:vals){
            if((pf instanceof ObjectPropertyField && ((ObjectPropertyField)pf).getValue()==null)
                    ||(pf instanceof TextPropertyField && ((TextPropertyField)pf).isDelete)){
                vals.remove(pf);
                delObjects.add(pf);
            }
        }
        list=new Object[vals.size()][1];
        for (int i = 0; i < vals.size(); ++i) {
            list[i][0]= vals.get(i);
        }
        init();
    }
    public ArrayPropertyField(KrnObject obj_,KrnAttribute attr, KrnObject[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.owner =(DesignerDialog)getTopLevelAncestor();
        this.obj_=obj_;
        attr_ = attr;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0]= new ObjectPropertyField(obj_, attr, i,table,this, vals[i]);
        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, String[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        if(attr_.typeClassId == Kernel.IC_MEMO){
            for (int i = 0; i < vals.length; ++i) {
                list[i][0]=new MemoPropertyField(obj_, a, i,table, vals[i]);
            }
        }else{
            for (int i = 0; i < vals.length; ++i) {
                list[i][0]=new TextPropertyField(obj_, a, i,table, vals[i]);
            }

        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, long[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0]=new IntPropertyField(obj_, a, i,table, vals[i]);
        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, double[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0]= new FloatPropertyField(obj_, a, i,table, vals[i]);
        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, DateValue[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0] = new DatePropertyField(obj_, a, i,table, vals[i]);
        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, TimeValue[] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0]=new TimePropertyField(obj_, a, i,table, vals[i]);
        }
        init();
    }

    public ArrayPropertyField(KrnObject obj_,KrnAttribute a, byte[][] vals)
            throws KrnException {
        setLayout(new BorderLayout());
        this.obj_=obj_;
        attr_ = a;
        list=new Object[vals.length][1];
        for (int i = 0; i < vals.length; ++i) {
            list[i][0]=new BlobPropertyField(obj_, a,i, table, vals[i],"UTF-8");
        }
        init();
    }
    public void setOwner(DesignerDialog owner){
        this.owner=owner;
    }
    public KrnObject getLinkValue(){
        return linkValue;
    }
    public int getLinkIndex(){
        return li;
    }
    public Vector getList(){
        return  model.getDataVector();
    }
    public Vector getDelList(){
        return  delObjects;
    }
    // ActionListener
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == addBtn_) {
                int idx = table.getSelectedRow();
                MoveTableView(model.getRowCount()-1);
                if(idx>-1)
                    table.getCellEditor(idx,0).stopCellEditing();
                PropertyField pf;
                if (attr_.typeClassId == Kernel.IC_STRING
                ) {
                    pf = new TextPropertyField(obj_, attr_, model.getRowCount(),table, "");
                } else if(attr_.typeClassId == Kernel.IC_MEMO) {
                    pf = new MemoPropertyField(obj_, attr_, model.getRowCount(),table, "");
                } else if(attr_.typeClassId == Kernel.IC_BLOB) {
                    pf = new BlobPropertyField(obj_, attr_, model.getRowCount(),table, null,"UTF-8");
                } else if(attr_.typeClassId == Kernel.IC_INTEGER
                        || attr_.typeClassId == Kernel.IC_BOOL) {
                    pf = new IntPropertyField(obj_, attr_, model.getRowCount(),table, 0);
                } else if(attr_.typeClassId == Kernel.IC_FLOAT) {
                    pf = new FloatPropertyField(obj_, attr_, model.getRowCount(),table, 0);
                } else {
                    pf = new ObjectPropertyField(obj_, attr_, model.getRowCount(),table,this, null);
                }
                model.addRow(new Object[]{pf});
                table.setRowSelectionInterval(model.getRowCount()-1,model.getRowCount()-1);
                MoveTableView(model.getRowCount()-1);
                pf.doClickSelBtn();
            } else if (src == delBtn_) {
                int idx = table.getSelectedRow();
                if (idx != -1) {
                    PropertyField pf = (PropertyField) model.getValueAt(idx, 0);
                    int res;
                    res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                            "Удалить объект \"" + pf.toString() + "\"?");
                    if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_YES) {
                        table.getCellEditor(idx, 0).stopCellEditing();
                        model.removeRow(idx);
                        if (table.getRowCount() > 0)
                            table.setRowSelectionInterval(idx == 0 ? 0 : idx - 1, idx == 0 ? 0 : idx - 1);
                        pf.deleteValue();
                        delObjects.add(pf);
                    }
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
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
        if (attr_.typeClassId > 99) {
            table.getColumnModel().getColumn(0).setCellRenderer(new ArrayPropertyCellRenderer());
        }
//        if (attr_.typeClassId == Kernel.IC_BLOB || attr_.typeClassId == Kernel.IC_MEMO) {
//            ce.setClickCountToStart(2);
//        }
        table.getTableHeader().setFont(Utils.getDefaultFont());
        table.getTableHeader().setForeground(Utils.getDarkShadowSysColor());
        table.setFont(Utils.getDefaultFont());
        add(sp=new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(new Dimension(400,500));
        setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
    }

    public void setLinkValue(KrnObject linkValue,int i) {
        this.linkValue = linkValue;
        this.li = i;
    }

    public DesignerDialog getOwner() {
        return owner;
    }

    protected void MoveTableView(int i) {
        JViewport vp = sp.getViewport();
        Point p = vp.getViewPosition();
        int lastRow = vp.getHeight() / table.getRowHeight()-1;
        p.y = Math.max(0, (i - lastRow)*table.getRowHeight());
        vp.setViewPosition(p);
    }

    private class ArrayPropertyCellRenderer extends DefaultTableCellRenderer {
     public Component getTableCellRendererComponent(JTable table, Object value,
                                                  boolean isSelected, boolean hasFocus, int row,
                                                  int column) {
         if (value instanceof ObjectPropertyField) {
             if(isSelected)
                 ((ObjectPropertyField)value).setBackground(Utils.getSysColor());
             else
                 ((ObjectPropertyField)value).setBackground(Color.white);
             return (ObjectPropertyField)value;
         } else {
             return this;
         }
     }
 }
 private class ArrayPropertyCellEditor extends DefaultCellEditor {

     public ArrayPropertyCellEditor() {
         super(new JTextField());
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
         if (value instanceof ObjectPropertyField)
             ((ObjectPropertyField)value).setBackground(Utils.getSysColor());
         return editorComponent;
     }

 }
}