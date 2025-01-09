package kz.tamur.guidesigner.terminal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import kz.tamur.comps.Constants;

/**
 * Панель переменных в Консоли
 * @author g009c1233
 * @since 2001/06/07
 * @version 0.1
 */
public class PropertyPanel extends JPanel{
	ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
	private JLabel label = new JLabel(res.getString("properties"));
	private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	public JComboBox select = new JComboBox(new String[] {res.getString("server"), res.getString("client")});
	private JTable table = new JTable();
	private JScrollPane scroll = new JScrollPane();
	private Object[] tableTitle;
	private Object[][] tableData;
	private DefaultTableModel model = new DefaultTableModel();
	
	/**
	 * Инициализация панели переменных 
	 * наверное надо через init() сделать
	 */
	public PropertyPanel() {
		setLayout(new GridBagLayout());
		tableTitle = new Object[] {res.getString("key"), res.getString("value")};
		model = new DefaultTableModel(tableData, tableTitle);
		table = new JTable(model);
		scroll = new JScrollPane(table);
		//add(Utils.createLabel("Компоненты"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
          //      GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            //    new Insets(0, 0, 0, 3), 0, 0));
		add(label,new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(3, 0, 0, 3), 0, 0));
		add(select, new GridBagConstraints(2, 0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(1, 0, 0, 3), 0, 0));
		add(scroll, new GridBagConstraints(0, 1, 3, 3, 3, 3,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(3, 0, 0, 0), 0, 0));
	}
	
	/**
	 * Обновляет переменные в PropertyPanel по карте
	 * @param vars карта переменных
	 */
	public void updateProps(HashMap vars) {
		Set set = vars.entrySet();
		Iterator itr = set.iterator();
		tableData = new Object[vars.size()][2];
		int i = 0;
		while(itr.hasNext()){
			Map.Entry m = (Map.Entry)itr.next();
			tableData[i][0] = m.getKey();
			tableData[i][1] = m.getValue();
			i++;
		}
		model.getDataVector().removeAllElements();
		model.setDataVector(tableData, tableTitle);
		
	}
	
	public JTable getTable() {
		return table;
	}
}
