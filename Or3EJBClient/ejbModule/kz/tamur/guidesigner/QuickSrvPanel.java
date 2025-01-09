package kz.tamur.guidesigner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jdom.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.QuickSrvList;

/**
 * HotKeys
 * @author g009c1233
 * @since 2011/05/05
 * @version 0.2
 */
public class QuickSrvPanel extends JPanel{
	ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private JTable table;
    private String[][] data;
    private String[] columnNames;
    private DefaultTableModel tmodel;
    private String xmlName = "HotKeysList";
    private QuickSrvList qlist;
    private String name;
    private String path;
    private String id;
    private boolean added = false;
    private Element xml;
    private String elName = "key-";
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    /**
     * Constructor, вытаскивает свою XML
     */
    public QuickSrvPanel() {
        qlist = new QuickSrvList();
        xml = qlist.getXml(xmlName);
    }
    
    /**
     * Constructor, создает панельку для отображения
     * @param b выбор Listenerов 
     */
    public QuickSrvPanel(boolean b){
		super(new GridBagLayout());
		
		init(false);
    }
    
    /**
     * Constructor, создает панельку для добавления HotKeya
     * @param namee Имя процесса
     * @param pathh Путь процесса
     * @param idd номер процесса
     */
    public QuickSrvPanel (String namee, String pathh, long idd){
		super(new GridBagLayout());
		name = namee.toString(); path = pathh; id = String.valueOf(idd);
		
		init(true);
    }
    
    /**
     * Инициализация панельки
     * @param b выбор Listenera
     */
    private void init(boolean b){
        setPreferredSize(new Dimension(400, 250));
        setMinimumSize(new Dimension(400, 250));
        this.setBackground(new Color(255,255,255));
        
        qlist = new QuickSrvList();
        xml = qlist.getXml(xmlName);
        
        if(xml==null) createXml();
        if(xml.getAttributeValue("items") == null) createXml();
        for(int i = 0; i < 10; i++) if(xml.getChild(elName + i) == null) createXml();
        
		columnNames = new String[] {"HotKeys", "Description"};
		data = new String[11][3];
		
		data[0][0] = "HotKeys";
		data[0][1] = "<html><font size=\"5\">"+ res.getString("description") +"</font></html>";
		for(int i = 1; i <= 10; i++) {
			data[i][0] = "Ctrl + Key '" + (i - 1) + "'";
			if(xml.getChild(elName + (i-1)).getAttribute("nick") == null || xml.getChild(elName +(i-1)).getAttributeValue("nick").equals("")){
				if(!(xml.getChild(elName+(i-1)).getAttributeValue("name").equals("")) && (xml.getChild(elName+(i-1)).getAttributeValue("name"))!= null)
					//data[i][1] = "<html><font size=\"5\">" + xml.getChild(elName+(i-1)).getAttributeValue("name") + " bla-bla-bla...</font></html>";
					data[i][1] = xml.getChild(elName+(i-1)).getAttributeValue("name");
				else 
					data[i][1] = "<html><font size=\"5\">" + res.getString("clickToSet") + "</font></html>";
			} else {
				data[i][1] = "<html><font size=\"5\">" + xml.getChild(elName + (i-1)).getAttributeValue("nick") + "</font></html>";
			}
		}
		tmodel = new DefaultTableModel(data,columnNames);
		table = new JTable(tmodel){
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.setRowHeight(25);
		TableColumn column;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(77);
		column.setMaxWidth(77);
		column = table.getColumnModel().getColumn(1);
		//column.setPreferredWidth(325);
		
		table.setPreferredSize(new Dimension(360, 200));
		
		//true - when need to Add New Key
		//false - when just show & set nicks
		if(b){
		table.addMouseListener(new MouseAdapter(){
		     public void mouseClicked(MouseEvent e){
		         if (e.getClickCount() == 2){
		    		if(table.getSelectedRow() < 1 || added) return;
		    		String msg = "set " + name + " to Key \"" + (table.getSelectedRow()-1)+ "\"?";
		    		String title = "Really???";
		    		if(JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
		    			xml.getChild(elName+(table.getSelectedRow()-1)).setAttribute("name", name);
		    			xml.getChild(elName+(table.getSelectedRow()-1)).setAttribute("path", path);
		    			xml.getChild(elName+(table.getSelectedRow()-1)).setAttribute("id", id);
		    			table.setValueAt("<html><font size=\"5\">Edited," + xml.getChild(elName+(table.getSelectedRow()-1)).getAttributeValue("name") + "</font></html>", table.getSelectedRow(), 1);

		    			added = true;
		    			//May be make MSGDialog, to confirm???
		    			
		    			qlist.setXml(xml);
		    		}
		         }
		     }
		} );
    	} else {
    		table.addMouseListener(new MouseAdapter(){
   		     public void mouseClicked(MouseEvent e){
   		         if (e.getModifiers() == 4 && table.getSelectedRow() == table.rowAtPoint(new Point(e.getX(), e.getY())) && !xml.getChild(elName+(table.getSelectedRow()-1)).getAttribute("id").equals("")){
   		        	 String result = JOptionPane.showInputDialog("Введите название:");
   		        	 if(result != null){
   		        		 xml.getChild(elName+(table.getSelectedRow()-1)).setAttribute("nick", result);
   		        	 } else {
   		        		 xml.getChild(elName+(table.getSelectedRow()-1)).removeAttribute("nick");
   		        	 }
   		        	 qlist.setXml(xml);
   		         }
   		     }
   		} );
    	}
		
		add(table, new GridBagConstraints( 0, 0, 2, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
	
		setOpaque(isOpaque);
		table.setOpaque(isOpaque);
    }
    
    /**
     * Создает чистую XMLку
     */
    private void createXml(){
    	xml = new Element(xmlName);
		xml.setAttribute("items", "10");
		xml.setText(" AaA ");
		for(int i = 0; i < 10; i++){
			Element el = new Element(elName + i);
			el.setAttribute("name", "");
			el.setAttribute("path", "");
			el.setAttribute("id", "");
			xml.addContent(el);
		}
    }
    
    /**
     * Возвращает id по номеру
     * @param key номер горячей клавишы
     * @return id процесса
     */
    public long quickKeyIdGet(int key){
		long toReturn = -1;
		
		if((xml.getChild(elName+key)).getAttributeValue("id") != "" && (xml.getChild(elName+key)).getAttributeValue("id") != null){
			return Long.valueOf((xml.getChild(elName+key)).getAttributeValue("id"));
		}
		
		return toReturn;
	}
	
    /**
     * Возвращает имя процесса по номеру
     * @param key номер горячей клавишы
     * @return Имя процесса
     */
	public String quickKeyNameGet(int key) {
		if((xml.getChild(elName+key)).getAttributeValue("name") != "" && (xml.getChild(elName+key)).getAttributeValue("name")!=null){
			return (xml.getChild(elName+key)).getAttributeValue("name");
		}
		return null;
	}
	
	/**
	 * Возвращает путь процесса по номеру
	 * @param key номер горячей клавишы
	 * @return Путь процесса
	 */
	public String quickKeyPathGet(int key){
		if((xml.getChild(elName+key)).getAttributeValue("path") != "" && (xml.getChild(elName+key)).getAttributeValue("path")!=null){
			return (xml.getChild(elName+key)).getAttributeValue("path");
		}
		return null;
	}
	
	public void deleteKey(int key){
		xml.getChild(elName + key).setAttribute("name", "");
		xml.getChild(elName + key).setAttribute("path", "");
		xml.getChild(elName + key).setAttribute("id", "");
		qlist.setXml(xml);
	}
}