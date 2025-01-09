package kz.tamur.guidesigner.boxes;

import static kz.tamur.or3ee.common.TransportIds.DIRECTORY;
import static kz.tamur.or3ee.common.TransportIds.JBOSS_JMS;
import static kz.tamur.or3ee.common.TransportIds.LOTUS_DIIOP;
import static kz.tamur.or3ee.common.TransportIds.MAIL;
import static kz.tamur.or3ee.common.TransportIds.MQ_JMS;
import static kz.tamur.or3ee.common.TransportIds.MQ_TRANSPORT;
import static kz.tamur.or3ee.common.TransportIds.SGDS_TRANSPORT;
import static kz.tamur.or3ee.common.TransportIds.WEB_SERVICE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.Utils;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 11.11.2005
 * Time: 16:45:53
 */
public class TransportPane extends JPanel implements ActionListener {
    private final String[] COL_NAMES = {"Свойство", "Значение"};
    private SelTableModel table_m = new SelTableModel();
    private final Kernel krn = Kernel.instance();
    private JToolBar tollBar=Utils.createDesignerToolBar();
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    public Tabbed tabbed;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public TransportPane() {
        super();
        tabbed = new Tabbed();
        init();
    }
    private void init(){
        JTable localTransport = new SelTableTable(table_m);
        JTable mailTransport = new SelTableTable(table_m);
        JTable mqTransport = new SelTableTable(table_m);
        JTable mqJmsTransport = new SelTableTable(table_m);
        JTable jbossJmsTransport = new SelTableTable(table_m);
        JTable sgdsTransport = new SelTableTable(table_m);
        JTable diiopTransport = new SelTableTable(table_m);
        
        setFont(kz.tamur.rt.Utils.getDefaultFont());
        tollBar.add(saveBtn);
        saveBtn.addActionListener(this);
        setLayout(new BorderLayout());
        add(tollBar,BorderLayout.NORTH);
        this.add(tabbed,BorderLayout.CENTER);
        tabbed.addTab("Локальные папки", new SelTableScrollPane(localTransport));
        tabbed.addTab("Электронная почта", new SelTableScrollPane(mailTransport));
        tabbed.addTab("MQ Client", new SelTableScrollPane(mqTransport));
        tabbed.addTab("MQ JMS Client", new SelTableScrollPane(mqJmsTransport));
        tabbed.addTab("JBOSS JMS Client",new SelTableScrollPane(jbossJmsTransport));
        tabbed.addTab("SGDS Client",new SelTableScrollPane(sgdsTransport));
        tabbed.addTab("DIIOP Client",new SelTableScrollPane(diiopTransport));
        saveBtn.setEnabled(false);
        setOpaque(isOpaque);
        tabbed.setOpaque(isOpaque);
        
        reload();
    }

    public void actionPerformed(ActionEvent e) {
        Object src=e.getSource();
        if(src==saveBtn){
            save();
        }
    }

    class SelTableTable extends JTable {
        DefaultCellEditor tce = new DefaultCellEditor(new OrPropTextField());

        public SelTableTable(TableModel dm) {
            super(dm);
            getColumnModel().getColumn(1).setCellEditor(tce);
        }

    }
    class SelTableScrollPane extends JScrollPane {

        public SelTableScrollPane(Component view) {
            super(view);
            setOpaque(isOpaque);
            getViewport().setOpaque(isOpaque);
        }
        
    }
        class SelTableModel extends AbstractTableModel {
        Object[][] data;

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            if (data != null) return data.length;
            return 0;
        }

        public boolean isCellEditable(int row, int col) {
            return !(col == 0 || row==2);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {

            }
            return data[rowIndex][columnIndex];
        }


        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
            setModify();
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public void reload(Object[][] data_) {
            data = data_;
            fireTableDataChanged();
        }
    }
    public void reload() {
        Object[][]data_ =null;
        byte[] data=null;
        int index= tabbed.getSelectedIndex();
        String title=tabbed.getTitleAt(index);
        saveBtn.setEnabled(false);
        try{
            if(title.equals("Локальные папки")){
                data=krn.getTransportParam(DIRECTORY);
            } else if(title.equals("Электронная почта")){
                data=krn.getTransportParam(MAIL);
            }else if(title.equals("MQ Client")){
                data=krn.getTransportParam(MQ_TRANSPORT);
            }
            else if(title.equals("MQ JMS Client")){
                data=krn.getTransportParam(MQ_JMS);
            }
            else if(title.equals("JBOSS JMS Client")){
                data=krn.getTransportParam(JBOSS_JMS);
            }
            else if(title.equals("Wwb Service Client")){
                data=krn.getTransportParam(WEB_SERVICE);
            }
            else if(title.equals("SGDS Client")){
                data=krn.getTransportParam(SGDS_TRANSPORT);
            }
            else if(title.equals("DIIOP Client")){
                    data=krn.getTransportParam(LOTUS_DIIOP);
            }
        } catch(KrnException e){
            e.printStackTrace();
        }
        if(data!=null && data.length>0){
            if(data!=null && data.length>0){
                try{
                      SAXBuilder builder = new SAXBuilder();
                      Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                      String delay="",ready="",maxCutMsg="",connect="";
                      Element root = doc.getRootElement();
                      Element param=root.getChild("delay");
                      if(param!=null) delay=param.getText();
                      param=root.getChild("ready");
                      ready=param.getText();
                      if(param!=null) param=root.getChild("connect");
                      connect=param.getText();
                      param=root.getChild("maxCutMsg");
                      if(param!=null) maxCutMsg=param.getText();
                      data_ = new Object[][]{{"Период",delay},{"Готовность",ready},{"Соединение",connect},{"КолОбрСообщений",maxCutMsg}};
                } catch(IOException e){
                    e.printStackTrace();
                } catch(JDOMException e){
                    e.printStackTrace();
                }
            }
        }
        table_m.reload(data_);
    }
    public void save(){
        int index=tabbed.getSelectedIndex();
        String title=tabbed.getTitleAt(index);
        try{
            Element root = new Element("params");
            Element e = new Element("delay");
            e.setText(""+table_m.data[0][1]);
            root.addContent(e);
            e = new Element("ready");
            e.setText(""+table_m.data[1][1]);
            root.addContent(e);
            e = new Element("maxCutMsg");
            e.setText(""+table_m.data[3][1]);
            root.addContent(e);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(root, os);
            os.close();
            byte[] data = os.toByteArray();
            if(title.equals("Локальные папки")){
                krn.setTransportParam(data,DIRECTORY);
            } else if(title.equals("Электронная почта")){
                krn.setTransportParam(data,MAIL);
            }else if(title.equals("MQ Client")){
                krn.setTransportParam(data,MQ_TRANSPORT);
            }
            else if(title.equals("MQ JMS Client")){
                krn.setTransportParam(data,MQ_JMS);
            }
            else if(title.equals("JBOSS JMS Client")){
                krn.setTransportParam(data,JBOSS_JMS);
            }
            else if(title.equals("Web Service Client")){
                krn.setTransportParam(data,JBOSS_JMS);
            }
            else if(title.equals("SGDS Client")){
                krn.setTransportParam(data,SGDS_TRANSPORT);
            }
            else if(title.equals("DIIOP Client")){
                krn.setTransportParam(data,LOTUS_DIIOP);
            }
            saveBtn.setEnabled(false);
        } catch(KrnException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    private void setModify(){
            saveBtn.setEnabled(true);
    }

    public void fireChangeTabbed() {
        tabbed.fireChange();
    }

    class Tabbed extends OrBasicTabbedPane {
        public void fireChange() {
            fireStateChanged();
        }

    }

}
