package kz.tamur.admin;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.InterfaceTree.InterfaceTreeModel;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FiltersTree.FilterTreeModel;
import kz.tamur.guidesigner.reports.ReportTree.ReportTreeModel;
import kz.tamur.guidesigner.service.ServicesTree.ServiceTreeModel;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class RecyclePanel extends JPanel implements ActionListener {
	
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem restoreItem = createMenuItem("Восстановить объект", "RestoreIcon.png");
    private JMenuItem deleteItem = createMenuItem("Удалить объект", "deleteObjs");
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    private JTable recycleObjectsTable;
    private RecycleTableModel model;
    private JComboBox<String> comboBox;
    private Kernel kernel = Kernel.instance();

	
	public RecyclePanel() {
        super(new BorderLayout());
        Utils.setAllSize(this, new Dimension(400, 400));
        setOpaque(isOpaque);
        
        comboBox = new JComboBox<String>();
        comboBox.setOpaque(isOpaque);
        comboBox.addItem("Процессы");
        comboBox.addItem("Интерфейсы");
        comboBox.addItem("Фильтры");
        comboBox.addItem("Отчеты");
        comboBox.setSelectedIndex(0);
        comboBox.setFont(Utils.getDefaultFont());
        
        comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.setRecycleObjects(getObjects());
				model.fireTableDataChanged();
			}
		});
        
        add(comboBox, BorderLayout.NORTH);
        
        model = new RecycleTableModel(getObjects());
        recycleObjectsTable = new JTable(model);
        recycleObjectsTable.setFont(Utils.getDefaultFont());
    	recycleObjectsTable.getTableHeader().setFont(Utils.getDefaultFont());
        
        JScrollPane scrollPane = new JScrollPane(recycleObjectsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        
        popup.add(restoreItem);
        restoreItem.addActionListener(this);
        popup.add(deleteItem);
        deleteItem.addActionListener(this);
        
        recycleObjectsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (recycleObjectsTable.getSelectedRow() > -1) {
						popup.show(RecyclePanel.this, e.getX(), e.getY());
					}
				}
			}
		}); 
	}
	
	private List<List<String>> getObjects() {
		List<List<String>> res = new ArrayList<List<String>>();
		try {
			int selectedItem = comboBox.getSelectedIndex();
			KrnClass cls;
			if (selectedItem == 0) {
				cls = kernel.getClassByName("ProcessDefRecycle");
			} else if (selectedItem == 1) {
				cls = kernel.getClassByName("UIRecycle");
			} else if (selectedItem == 2) {
				cls = kernel.getClassByName("FilterRecycle");
			} else {
				cls = kernel.getClassByName("ReportPrinterRecycle");
			}
			
			KrnAttribute titleAttr = kernel.getAttributeByName(cls, "title");
			KrnAttribute eventInitiatorAttr = kernel.getAttributeByName(cls, "eventInitiator");
			KrnAttribute eventDateAttr = kernel.getAttributeByName(cls, "eventDate");
			
			KrnObject[] objs = kernel.getClassObjects(cls, 0);
			for(int i = 0; i < objs.length; i++) {
				String title = kernel.getStringsSingular(objs[i].id, titleAttr.id, 0, false, false);
				String eventInitiator = kernel.getStringsSingular(objs[i].id, eventInitiatorAttr.id, 0, false, false);
				String eventDate = kernel.getStringsSingular(objs[i].id, eventDateAttr.id, 0, false, false);
				String objId = String.valueOf(objs[i].id);
				res.add(Arrays.asList(title, eventInitiator, eventDate, objId));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == restoreItem) {
	    	restoreItem();	    	
		} else if (src == deleteItem) {
			deleteItem();
		}
	}
	
	private void restoreItem() {
		int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите восстановить выбранный элемент?");
        if (res == ButtonsFactory.BUTTON_YES) {
        	int index = recycleObjectsTable.getSelectedRow();
			long objRecycleId = Long.valueOf(model.getNode(index).get(3));
			try {
				KrnObject objRecycle = kernel.getObjectById(objRecycleId, 0);
				// Перезапись объекта
				int selectedItem = comboBox.getSelectedIndex();
				if (selectedItem == 0) {
					restoreProcess(objRecycle);
				} else if (selectedItem == 1) {
					restoreUI(objRecycle);
				} else if (selectedItem == 2) {
					restoreFilter(objRecycle);
				} else {
					restoreReportPrinter(objRecycle);
				}
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			model.getRecycleObjects().remove(index);
			model.fireTableDataChanged();
        }
	}
	
	private void restoreProcess(KrnObject objRecycle) {
		try {
	   	 	KrnClass cls = kernel.getClassByName("ProcessDef");
	   	 	KrnClass clsRecycle = kernel.getClassByName("ProcessDefRecycle");
	   	 	KrnAttribute uidAttr = kernel.getAttributeByName(clsRecycle, "uid");
	   	 	String uid = kernel.getStringsSingular(objRecycle.id, uidAttr.id, 0, false, true);
	   	 	KrnObject obj = kernel.createObject(cls, uid, 0);
	
	    	 byte[] config = kernel.getBlob(objRecycle, "config", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "config", 0, config, 0, 0);
	    	 
	    	 byte[] diagram = kernel.getBlob(objRecycle, "diagram", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "diagram", 0, diagram, 0, 0);
	    	 
	    	 KrnObject[] filters = kernel.getObjects(objRecycle, "filters", 0);
	    	 if (filters != null && filters.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "filters", 0, filters[0].id, 0, true);
	    	 }
	    	 
	    	 String[] hotkey = kernel.getStrings(objRecycle, "hotKey", 0, 0);
	    	 if (hotkey != null && hotkey.length > 0) {
	    		 kernel.setString(obj.id, cls.id, "hotKey", 0, 0, hotkey[0], 0);
	    	 }
	    	 
	    	 byte[] icon = kernel.getBlob(objRecycle, "icon", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "icon", 0, icon, 0, 0);
	    	 
	    	 long[] isBtnToolBar = kernel.getLongs(objRecycle, "isBtnToolBar", 0);
	    	 if (isBtnToolBar != null && isBtnToolBar.length > 0) {
	    		 kernel.setLong(obj.id, cls.id, "isBtnToolBar", 0, isBtnToolBar[0], 0);
	    	 }
	    	 
	    	 byte[] message = kernel.getBlob(objRecycle, "message", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "message", 0, message, 0, 0);
	    	 
	    	 KrnObject[] parent = kernel.getObjects(objRecycle, "parent", 0);
	    	 if (parent != null && parent.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "parent", 0, parent[0].id, 0, true);
	    	 }
	
	    	 long[] runtimeIndex = kernel.getLongs(objRecycle, "runtimeIndex", 0);
	    	 if (runtimeIndex != null && runtimeIndex.length > 0) {
	    		 kernel.setLong(obj.id, cls.id, "runtimeIndex", 0, runtimeIndex[0], 0);
	    	 }
	    	 
	    	 byte[] strings = kernel.getBlob(objRecycle, "strings", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "strings", 0, strings, 0, 0);
	    	 
	    	 byte[] test = kernel.getBlob(objRecycle, "test", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "test", 0, test, 0, 0);
	    	 
	    	 String[] title = kernel.getStrings(objRecycle, "title", 0, 0);
	    	 if (title != null && title.length > 0) {
	    		 kernel.setString(obj.id, cls.id, "title", 0, 0, title[0], 0);
	    	 }
	    	 
	    	 KrnObject[] balance = kernel.getObjects(objRecycle, "баланс_ед", 0);
	    	 if (balance != null && balance.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "баланс_ед", 0, balance[0].id, 0, true);
	    	 }
	    	 
	    	 kernel.deleteObject(objRecycle, 0);
	    	 
	    	 if (parent != null && parent.length > 0) {
		    	 ((ServiceTreeModel) kz.tamur.comps.Utils.getServicesTree().getModel()).createChildNode(obj, parent[0]);
	    	 }
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}

	private void restoreUI(KrnObject objRecycle) {
		try {
	   	 	KrnClass cls = kernel.getClassByName("UI");
	   	 	KrnClass clsRecycle = kernel.getClassByName("UIRecycle");
	   	 	KrnAttribute uidAttr = kernel.getAttributeByName(clsRecycle, "uid");
	   	 	String uid = kernel.getStringsSingular(objRecycle.id, uidAttr.id, 0, false, true);
	   	 	KrnObject obj = kernel.createObject(cls, uid, 0);
	
	    	 byte[] config = kernel.getBlob(objRecycle, "config", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "config", 0, config, 0, 0);
	    	 
	    	 KrnObject[] filtersFolder = kernel.getObjects(objRecycle, "filtersFolder", 0);
	    	 if (filtersFolder != null && filtersFolder.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "filtersFolder", 0, filtersFolder[0].id, 0, true);
	    	 }
	    	 
	    	 KrnObject[] parent = kernel.getObjects(objRecycle, "parent", 0);
	    	 if (parent != null && parent.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "parent", 0, parent[0].id, 0, true);
	    	 }
	
	    	 byte[] strings = kernel.getBlob(objRecycle, "strings", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "strings", 0, strings, 0, 0);
	    	 
	    	 String[] title = kernel.getStrings(objRecycle, "title", 0, 0);
	    	 if (title != null && title.length > 0) {
	    		 kernel.setString(obj.id, cls.id, "title", 0, 0, title[0], 0);
	    	 }
	    	 
	    	 byte[] webConfig = kernel.getBlob(objRecycle, "webConfig", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "webConfig", 0, webConfig, 0, 0);
	    	 
	    	 long[] webConfigChanged = kernel.getLongs(objRecycle, "webConfigChanged", 0);
	    	 if (webConfigChanged != null && webConfigChanged.length > 0) {
	    		 kernel.setLong(obj.id, cls.id, "webConfigChanged", 0, webConfigChanged[0], 0);
	    	 }
	    	 
	    	 kernel.deleteObject(objRecycle, 0);
	    	 
	    	 if (parent != null && parent.length > 0) {
		    	 ((InterfaceTreeModel) kz.tamur.comps.Utils.getInterfaceTree().getModel()).createChildNode(obj, parent[0]);
	    	 }
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}
	
	private void restoreFilter(KrnObject objRecycle) {
		try {
	   	 	KrnClass cls = kernel.getClassByName("Filter");
	   	 	KrnClass clsRecycle = kernel.getClassByName("FilterRecycle");
	   	 	KrnAttribute uidAttr = kernel.getAttributeByName(clsRecycle, "uid");
	   	 	String uid = kernel.getStringsSingular(objRecycle.id, uidAttr.id, 0, false, true);
	   	 	KrnObject obj = kernel.createObject(cls, uid, 0);
	
		   	 String[] className = kernel.getStrings(objRecycle, "className", 0, 0);
	    	 if (className != null && className.length > 0) {
	    		 kernel.setString(obj.id, cls.id, "className", 0, 0, className[0], 0);
	    	 }
	   	 	
	    	 byte[] config = kernel.getBlob(objRecycle, "config", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "config", 0, config, 0, 0);
	    	 
	    	 long[] dateSelect = kernel.getLongs(objRecycle, "dateSelect", 0);
	    	 if (dateSelect != null && dateSelect.length > 0) {
	    		 kernel.setLong(obj.id, cls.id, "dateSelect", 0, dateSelect[0], 0);
	    	 }

	    	 byte[] exprSql = kernel.getBlob(objRecycle, "exprSql", 0, 0, 0);
	    	 kernel.setBlob(obj.id, cls.id, "exprSql", 0, exprSql, 0, 0);

	    	 KrnObject[] parent = kernel.getObjects(objRecycle, "parent", 0);
	    	 if (parent != null && parent.length > 0) {
	    		 kernel.setObject(obj.id, cls.id, "parent", 0, parent[0].id, 0, true);
	    	 }

	    	 String[] title = kernel.getStrings(objRecycle, "title", 0, 0);
	    	 if (title != null && title.length > 0) {
	    		 kernel.setString(obj.id, cls.id, "title", 0, 0, title[0], 0);
	    	 }
	    	 
	    	 kernel.deleteObject(objRecycle, 0);
	    	 
	    	 if (parent != null && parent.length > 0) {
		    	 ((FilterTreeModel) kz.tamur.comps.Utils.getFiltersTree().getModel()).createChildNode(obj, parent[0]);
	    	 }
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}
	
	private void restoreReportPrinter(KrnObject objRecycle) {
		try {
			KrnClass cls = kernel.getClassByName("ReportPrinter");
			KrnClass clsRecycle = kernel.getClassByName("ReportPrinterRecycle");
			KrnAttribute uidAttr = kernel.getAttributeByName(clsRecycle, "uid");
			String uid = kernel.getStringsSingular(objRecycle.id, uidAttr.id, 0, false, true);
			KrnObject obj = kernel.createObject(cls, uid, 0);

			String[] constraints = kernel.getMemos(objRecycle, "constraints", 0, 0);
			if (constraints != null) {
				for (int i = 0; i < constraints.length; i++) {
					kernel.setMemo((int) obj.id, (int) cls.id, "constraints", i, 0, constraints[i], 0);
				}
			}

			String[] descInfo = kernel.getMemos(objRecycle, "descInfo", 0, 0);
			if (descInfo != null) {
				for (int i = 0; i < descInfo.length; i++) {
					kernel.setMemo((int) obj.id, (int) cls.id, "descInfo", i, 0, descInfo[i], 0);
				}
			}

			long[] flags = kernel.getLongs(objRecycle, "flags", 0);
			if (flags != null) {
				for (int i = 0; i < flags.length; i++) {
					kernel.setLong(obj.id, cls.id, "flags", i, flags[i], 0);
				}
			}

			String[] ref = kernel.getMemos(objRecycle, "ref", 0, 0);
			if (ref != null) {
				for (int i = 0; i < ref.length; i++) {
					kernel.setMemo((int) obj.id, (int) cls.id, "ref", i, 0, ref[i], 0);
				}
			}

			String[] title = kernel.getStrings(objRecycle, "title", 0, 0);
			if (title != null && title.length > 0) {
				kernel.setString(obj.id, cls.id, "title", 0, 0, title[0], 0);
			}

//			KrnObject[] bases = kernel.getObjects(objRecycle, "bases", new long[] { 0 }, 0);
//			if (bases != null) {
//				for (int i = 0; i < bases.length; i++) {
//					kernel.setObject(obj.id, cls.id, "bases", i, bases[i].id, 0, true);
//				}
//			}

			byte[] config = kernel.getBlob(objRecycle, "config", 0, 0, 0);
			kernel.setBlob(obj.id, cls.id, "config", 0, config, 0, 0);

			byte[] data = kernel.getBlob(objRecycle, "data", 0, 0, 0);
			kernel.setBlob(obj.id, cls.id, "data", 0, data, 0, 0);

			byte[] data2 = kernel.getBlob(objRecycle, "data2", 0, 0, 0);
			kernel.setBlob(obj.id, cls.id, "data2", 0, data2, 0, 0);

			KrnObject[] parent = kernel.getObjects(objRecycle, "parent", 0);
			if (parent != null && parent.length > 0) {
				kernel.setObject(obj.id, cls.id, "parent", 0, parent[0].id, 0, true);
			}

			byte[] template = kernel.getBlob(objRecycle, "template", 0, 0, 0);
			kernel.setBlob(obj.id, cls.id, "template", 0, template, 0, 0);

			byte[] template2 = kernel.getBlob(objRecycle, "template2", 0, 0, 0);
			kernel.setBlob(obj.id, cls.id, "template2", 0, template2, 0, 0);

			KrnObject[] rootReport = kernel.getObjects(objRecycle, "базовый отчет", 0);
			if (rootReport != null && rootReport.length > 0) {
				kernel.setObject(obj.id, cls.id, "базовый отчет", 0, rootReport[0].id, 0, true);
			}

			kernel.deleteObject(objRecycle, 0);

			if (parent != null && parent.length > 0) {
				((ReportTreeModel) kz.tamur.comps.Utils.getReportTree(null).getModel()).createChildNode(obj, parent[0]);
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteItem() {
		int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите безвозвратно удалить выбранный элемент?");
        if (res == ButtonsFactory.BUTTON_YES) {
			int index = recycleObjectsTable.getSelectedRow();
			long objId = Long.valueOf(model.getNode(index).get(3));
			try {
				KrnObject obj = kernel.getObjectById(objId, 0);
				kernel.deleteObject(obj, 0);
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			model.getRecycleObjects().remove(index);
			model.fireTableDataChanged();
        }
	}
	
	class RecycleTableModel extends AbstractTableModel {

		private final String[] COL_NAMES = { "Объект", "Пользователь", "Дата" };
		private List<List<String>> recycleObjects;

		public RecycleTableModel(List<List<String>> recycleObjects) {
			this.recycleObjects = recycleObjects;
		}

		public List<List<String>> getRecycleObjects() {
			return recycleObjects;
		}
		
		public void setRecycleObjects(List<List<String>> recycleObjects) {
			this.recycleObjects = recycleObjects;
		}

		public int getRowCount() {
			return recycleObjects.size();
		}

		public int getColumnCount() {
			return COL_NAMES.length;
		}

		public String getColumnName(int columnIndex) {
			return COL_NAMES[columnIndex];
		}

		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				case 3:
					return Long.class;
			}
			return null;
		}

		public String getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return recycleObjects.get(rowIndex).get(0);
				case 1:
					return recycleObjects.get(rowIndex).get(1);
				case 2:
					return recycleObjects.get(rowIndex).get(2);
				case 3:
					return recycleObjects.get(rowIndex).get(3);
			}
			return null;
		}

		public List<String> getNode(int row) {
			return recycleObjects.get(row);
		}

		public void fireTableDataChanged() {
			super.fireTableDataChanged();
		}
	}
}
