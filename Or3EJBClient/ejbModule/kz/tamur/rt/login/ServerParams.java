package kz.tamur.rt.login;

import static kz.tamur.comps.Utils.createDesignerToolBar;
import static kz.tamur.comps.Utils.getCenterLocationPoint;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconJpg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.util.Funcs;

import org.jdom.Element;

public class ServerParams extends JDialog implements ActionListener {
    
    ImageIcon LoginImg_ = getImageIconJpg("LoginBox");	
    JToolBar toolBar = createDesignerToolBar();
    JButton newBtn = ButtonsFactory.createToolButton("Create", "Создать новый");
    JButton delBtn = ButtonsFactory.createToolButton("Delete", "Удалить");
    SelTableModel table_m = new SelTableModel(new String[]{"Свойство","Значение"});
    JTable paramTable;
    ServerTree srvTree;
    private Element xml;
    JComboBox<String> typeSrv=new JComboBox<>();
    JButton okBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK);
    JButton cancelBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
    TablePropertyCellEditor tce;
    GradientPanel content =  new GradientPanel();   
    ResourceBundle res;
    private String lang;
    private String srv;
    private boolean result=false;

    public ServerParams(Window owner, String lang, ResourceBundle resource,Element xml) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.lang = lang;
        res = resource;
        this.xml= xml;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();
    }

    private void jbInit(){
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setTitle(res.getString("srvChange"));
        this.getRootPane().setDefaultButton(okBtn);
        content.setGradient(Constants.GLOBAL_DEF_GRADIENT);
        newBtn.setBackground(Color.white);
        newBtn.setIcon(getImageIcon("new"));


        okBtn.setBackground(Color.white);
        okBtn.setIcon(getImageIcon("checkOk"));
        okBtn.addActionListener(this);


        cancelBtn.setIcon(getImageIcon("Delete"));
        cancelBtn.addActionListener(this);
        content.setPreferredSize(new Dimension(650,550));
        toolBar.add(newBtn);
        newBtn.addActionListener(this);
        toolBar.add(delBtn);
        delBtn.addActionListener(this);
        content.setLayout(new BorderLayout());
        content.add(toolBar,BorderLayout.NORTH);
        ServerNode node=new ServerNode(xml);
        srvTree=new ServerTree(node,lang);
        srvTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                ServerNode node = (ServerNode) e.getPath().getLastPathComponent();
                setServerParam(node);
            }
        });
        JSplitPane sp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.setDividerLocation(200);
        sp.setDividerSize(3);
        tce=new TablePropertyCellEditor(new OrPropTextField());
        
        typeSrv.addActionListener(this);
        typeSrv.addItem("");
        typeSrv.addItem("JBossServer");
        typeSrv.addItem("JBossAS7");
        typeSrv.addItem("JBossEAP");
        typeSrv.addItem("Wildfly");
        typeSrv.addItem("Wildfly Cluster");
        typeSrv.addItem("Wildfly 14+");
        typeSrv.addItem("Weblogic");
        
        table_m = new SelTableModel(new String[]{"Свойство","Значение"}){
            public void setValueAt(Object value, int row, int col) {
                if (srvTree.getCellEditor() != null)
                    srvTree.getCellEditor().cancelCellEditing();
                super.setValueAt(value, row, col);
                ServerNode node = (ServerNode) srvTree.getSelectedNode();
                Element xml = node.getXml();
                if (row == 0)
                    xml.setAttribute("serverType", (String) value);
                else if (row == 1)
                    xml.setAttribute("host", (String) value);
                else if (row == 2)
                    xml.setAttribute("port", (String) value);
                else if (row == 3)
                    xml.setAttribute("baseName", (String) value);
                else if (row == 4)
                    xml.setAttribute("webUrl", (String) value);
                else if (row == 5)
                    xml.setAttribute("ear", (String) value);
            }

        };
        paramTable = new JTable(table_m);
        paramTable.getColumnModel().getColumn(1).setCellEditor(tce);
        JScrollPane ls;
        JScrollPane rs;
        sp.setLeftComponent(ls = new JScrollPane(srvTree));
        sp.setRightComponent(rs = new JScrollPane(paramTable));
        JPanel btnp=new JPanel();
        btnp.setBorder(BorderFactory.createEtchedBorder());
        content.add(sp,BorderLayout.CENTER);
        btnp.add(okBtn);
        btnp.add(cancelBtn);
        content.add(btnp,BorderLayout.SOUTH);
        getContentPane().add(content);
        pack();
        setLocation(getCenterLocationPoint(getSize()));
        FocusListener fl_= new FocusListener() {
            public void focusGained(FocusEvent e) {
                getRootPane().setDefaultButton((JButton)e.getSource());
            }
            public void focusLost(FocusEvent e) {
                getRootPane().setDefaultButton(okBtn);
            }
        };
        cancelBtn.addFocusListener(fl_);
        okBtn.addFocusListener(fl_);
        toolBar.setOpaque(false);
        sp.setOpaque(false);
        btnp.setOpaque(false);
        srvTree.setOpaque(false);
        paramTable.setOpaque(false);
        ls.setOpaque(false);
        ls.getViewport().setOpaque(false);
        rs.setOpaque(false);
        rs.getViewport().setOpaque(false);
        newBtn.setOpaque(false);
        delBtn.setOpaque(false);
    }

    public void setVisible(boolean visible,String srv) {
        if (visible) {
            getRootPane().setDefaultButton(okBtn);
        }
        srvTree.setSelectedNode(this.srv = srv);
        super.setVisible(visible);
    }

    public void setServerParam(ServerNode node){
        Object[][] data=null;
        if(node.isLeaf()){
            data=new Object[][]{{"Тип сервера",""},{"Адрес",""},{"Порт",""},{"Имя БД",""},{"Адрес Web",""},{"Название EAR",""}};
            Element xml =node.getXml();
            String serverType = xml.getAttributeValue("serverType");
            String host = xml.getAttributeValue("host");
            String port = xml.getAttributeValue("port");
            String baseName = xml.getAttributeValue("baseName");
            String webUrl = xml.getAttributeValue("webUrl");
            String earName = xml.getAttributeValue("ear") != null ? xml.getAttributeValue("ear") : "Or3EAR";
            data[0][1] = serverType;
            data[1][1] = host;
            data[2][1] = port;
            data[3][1] = baseName;
            data[4][1] = webUrl;
            data[5][1] = earName;
        }
        if(paramTable.getCellEditor()!=null)
            paramTable.getCellEditor().cancelCellEditing();
        table_m.reload(data);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();
            if (src == okBtn) {
                ServerNode node=(ServerNode)srvTree.getSelectedNode();
                if(node!=null && node.isLeaf()) srv=node.toString();
                result=true;
                dispose();
            } else if (src == newBtn) {
                srvTree.miCreateElement.doClick();
            } else if (src == delBtn) {
                srvTree.miDelete.doClick();
            } else if (src == cancelBtn) {
                dispose();
            }else if(src==typeSrv){
            	String value = (String)typeSrv.getSelectedItem();
            	if(!tce.getCellEditorValue().equals(value)){
            		tce.setCellEditorValue(value);
            		tce.stopCellEditing();
            	}else 
            		tce.cancelCellEditing();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    public boolean getResult(){
        return result;
    }
    
    public String getServer(){
        return srv;
    }
    
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            String mess = res.getString("srvNoChanged");
            MessagesFactory.showMessageDialog(this, MessagesFactory.INFORMATION_MESSAGE, mess, lang);
            dispose();
        }
    }

    class SelTableModel extends AbstractTableModel {
        Object[][] data;
        String[] col_names;
        public SelTableModel(String[] col_names){
              this.col_names =col_names;
        }
        public int getColumnCount() {
            return col_names.length;
        }

        public int getRowCount() {
            if (data != null) return data.length;
            return 0;
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0 && col_names.length>1)
                return false;
            else
                return true;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {

            }
            return data[rowIndex][columnIndex];
        }


        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        public String getColumnName(int column) {
            return col_names[column];
        }

        public void reload(Object[][] data_) {
            data = data_;
            fireTableDataChanged();
        }
    }
    
    private class TablePropertyCellEditor extends DefaultCellEditor {
        private JTextField tf;
        public TablePropertyCellEditor(JTextField tf) {
			super(tf);
			this.tf=tf;
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
                             boolean isSelected,
                             int row, int column) {
            if (row==0){
            	typeSrv.setSelectedItem((String)value);
            	return typeSrv;
            }else {
            	tf.setText((String)value);
            	return tf;
            }
        }

        public String getCellEditorValue() {
            return Funcs.normalizeInput(tf.getText());
        }

        public void setCellEditorValue(String value) {
            tf.setText(value != null ? value : "");
        }
    }
    
    public ServerTree getServerTree(){
        return srvTree;
    }
}