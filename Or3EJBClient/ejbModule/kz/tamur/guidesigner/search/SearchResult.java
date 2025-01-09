package kz.tamur.guidesigner.search;

import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import kz.tamur.Or3Frame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;

public class SearchResult extends JPanel implements ActionListener {
	
	private JPanel path = new JPanel(new BorderLayout());
    private SearchNode treeNode;
    private java.util.List<String[]> list;
    private SearchTableModel model;
    private JTable table;
    private JScrollPane tablPane;
    private static final ImageIcon SORT_UP = getImageIcon("SortUpLight");
    private static final ImageIcon SORT_DOWN = getImageIcon("SortDownLight");
    private CellRenderer renderer = new CellRenderer(false, null);
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem jumpItem = createMenuItem("Перейти");
    private JMenuItem jumpToObjectItem = createMenuItem("Перейти на Объект");
    private JMenuItem showPrevItem = createMenuItem("Предварительный просмотр");
    private JMenuItem copyUIDItem = createMenuItem("Копировать UID");
    private JLabel infoLabel  = Utils.createLabel("* * *");
    private MainFrame.DescLabel counterLabel = kz.tamur.comps.Utils.createDescLabel("");

    public SearchResult() {
    	setLayout(new BorderLayout());
    	
    	JPanel infoPanel = new JPanel(new GridBagLayout());
    	JPanel counterPanel = new JPanel(new GridBagLayout());
    	infoLabel.setForeground(Color.RED);
    	infoPanel.add(infoLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,  5, 5, 5), 0, 0));
    	counterPanel.add(counterLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,  5, 5, 5), 0, 0));
    	path.add(infoPanel, BorderLayout.WEST);
    	path.add(counterPanel, BorderLayout.EAST);
    	
    	add(path, BorderLayout.NORTH);
    	popup.add(jumpItem);
    	jumpItem.addActionListener(this);
        popup.add(jumpToObjectItem);
        jumpToObjectItem.addActionListener(this);
        popup.add(showPrevItem);
        showPrevItem.addActionListener(this);
        popup.addSeparator();
        popup.add(copyUIDItem);
        copyUIDItem.addActionListener(this);
        
        list = new ArrayList<>();
		model = new SearchTableModel(list);

    	table = new JTable(model);
    	table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	if (e.isPopupTrigger())  showPopup(e);                
            }
            
            private void showPopup(MouseEvent e) {
            	Point p = e.getPoint();
    			int rowNumber = table.rowAtPoint(p);
    			ListSelectionModel selectionModel = table.getSelectionModel();
    			selectionModel.setSelectionInterval(rowNumber, rowNumber);
    			String attrID = (model.getRealValueAt(rowNumber, 1));
            	if ("Method".equals(attrID) || "Trigger".equals(attrID) || "Change".equals(attrID)) {
            		jumpToObjectItem.setVisible(false);
            	} else {
            		jumpToObjectItem.setVisible(true);
            	}
            	if ("Change".equals(attrID)) {
            		jumpItem.setVisible(false);
            		copyUIDItem.setText("Копировать ID");
           		} else {
            		jumpItem.setVisible(true);
            		copyUIDItem.setText("Копировать UID");
            	}
            	if ("Method".equals(attrID)) {
	            	int row = table.getSelectedRow();
	        		String objUID = model.getRealValueAt(row, 0);
	        		try {
	    				KrnMethod method = Kernel.instance().getMethodById(objUID);
    					KrnClass cls = Kernel.instance().getClassById(method.classId);
    					if (cls != null)
    						jumpItem.setEnabled(true);
    					else
    						jumpItem.setEnabled(false);
	    				
	    			} catch (KrnException ex) {
	    	             ex.printStackTrace();
	    	        }
            	}

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    	table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
		        setCounterText();
			}
		});
    	JTableHeader header = table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            tc.setHeaderRenderer(r);
            if (i == model.getSortColumn())
                r.setIcon(model.getColumnIcon(i));
        }
    	
    }
    	
    public void init(SearchNode node) {
    	treeNode = node;
    	if ( node == null || !treeNode.isLeaf()) {
    		infoLabel.setText("* * *");
    		counterLabel.setText("");
   			remove(tablPane);
    	}
    	else {
    		if (tablPane != null) {
    			remove(tablPane);
    		}    		
    		list = treeNode.getSearchRes();
    		infoLabel.setText("Результаты по поиску фразы: '" + treeNode.getTitle() + "'");
    		model.setData(list);
    		model.sortData();
            table.setModel(model);
            tablPane = new JScrollPane(table);            
            table.setFillsViewportHeight(true);
            table.setRowSelectionAllowed(true);
            table.setDefaultRenderer(String.class, renderer);
            table.getColumnModel().getColumn(2).setMinWidth(100);
            table.getColumnModel().getColumn(2).setMaxWidth(160);
            table.setRowHeight(20);
            add(tablPane);
            setCounterText();
    	}
        validate();
        repaint();
    }
    
    public JTable getTable() {
    	return table;
    }
    
    private void setCounterText() {
        int rowCount = table.getModel().getRowCount();
        int selectedRow = table.getSelectedRow() + 1;
        counterLabel.setText((selectedRow == 0 ? "-" : selectedRow) + " / " + rowCount);
    }
    
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		Kernel krn = Kernel.instance();
		int row = table.getSelectedRow();
		String objUID = model.getRealValueAt(row, 0);
		String attrID = (model.getRealValueAt(row, 1));
		String pattern = treeNode.getTitle().split(" - ")[0];
		if (src == jumpToObjectItem) {
			try {
				Or3Frame.instance().jumpClass(Utils.getObjectByUid(objUID, 0), attrID);
			} catch (KrnException e1) {
				e1.printStackTrace();
			}
		} else if (src == showPrevItem) {
			String blobString = null;
			try {
				int[] selectedRows = table.getSelectedRows();
				String selectedNodeUID = (String) model.getValueAt(selectedRows[0], 0);
				if ("Method".equals(attrID)) {					
					blobString = Kernel.instance().getMethodExpression(selectedNodeUID);
				}else if ("Change".equals(attrID)) {					
    				List<KrnVcsChange> changes = Kernel.instance().getVcsDifChanges(true,new long[]{Long.valueOf(model.getRealValueAt(row, 0))});
    				if(changes.size()>0){
						blobString = Kernel.instance().getVcsHistoryDataIncrement(changes.get(0));
    				}else {
    					
    				}
				} else if ("Trigger".equals(attrID)) {
					String[] params = objUID.split("_");
					if ("0".equals(params[1])) {
						KrnClass cls = krn.getClassByUid(params[0]);
						byte[] bytes;
						if ("0".equals(params[2])) {
							bytes = cls.beforeCreateObjExpr;
						} else if ("1".equals(params[2])) {
							bytes = cls.afterCreateObjExpr;
						} else if ("2".equals(params[2])) {
							bytes = cls.beforeDeleteObjExpr;
						} else {
							bytes = cls.afterDeleteObjExpr;
						}
						try {
							blobString = new String(bytes, "UTF-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
					} else {
						KrnAttribute attr = krn.getAttributeByUid(params[0]);
						byte[] bytes;
						if ("0".equals(params[2])) {
							bytes = attr.beforeEventExpr;
						} else if ("1".equals(params[2])) {
							bytes = attr.afterEventExpr;
						} else if ("2".equals(params[2])) {
							bytes = attr.beforeDelEventExpr;
						} else {
							bytes = attr.afterDelEventExpr;
						}
						try {
							blobString = new String(bytes, "UTF-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					Long selectedNodeAttrID = Long.parseLong(((SearchTableModel) model).getRealValueAt(selectedRows[0], 1));
					KrnObject object = krn.getObjectByUid(selectedNodeUID, 0);
					KrnAttribute attribute = krn.getAttributeById(selectedNodeAttrID);
					KrnClass reportCls = krn.getClassByName("ReportPrinter");
					boolean isReport=krn.getClass(object.getClassId()).equals(reportCls);
					if (object != null && attribute != null) {
						if(isReport && attribute.name.contains("template")){
							String fileName ="";
							String configValue=new String(krn.getBlob(object, "config", 0,0, 0));
							if(configValue.contains("<editorType>1")){
								fileName = "xxx.xlsx";
							}else if(configValue.contains("<editorType>0")){
								fileName = "xxx.docx";
							}else {
								fileName = "xxx.docx";
							}
							byte[] value=krn.getBlob(object, attribute.name, 0, 0, 0);
							try {
								blobString = extract(fileName,value);
							} catch (Exception e1) {
								try {
									blobString = extract(fileName.substring(0,fileName.length()-1),value);
								} catch (Exception e2) {
									e2.printStackTrace();
								}
							}
						}else
							blobString = new String(krn.getBlob(object, attribute.name, 0, 0, 0));
					}
				}
				if (blobString == null) {
					MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Не удается выполнить предварительный просмотр!");
					return;
				}
				new SearchOperationsWindow("Предварительный просмотр", 700, 700, 2, Or3Frame.instance().getSearchPanel().getSearchingInfo(), blobString);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (KrnException e1) {
				e1.printStackTrace();
			}
		} else if (src == jumpItem) {
			try {
				String className = "<Unaccessible>";				
				Object obj = null;
				if ("Method".equals(attrID)) {
					obj = Kernel.instance().getMethodById(objUID);
					className = "<Methods>";
				} else if ("Trigger".equals(attrID)) {
					String[] params = objUID.split("_");
					Or3Frame.instance().jumpTrigger(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
					return;
				} else {
					obj = Utils.getObjectByUid(objUID,0);
					if (obj != null)
						className = Kernel.instance().getClass(((KrnObject) obj).classId).name;
				}
				if (obj == null) {
					MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Переход не возможен!");
					return;
				}
				if (className.equals("<Methods>")) {
					Or3Frame.instance().jumpMethod((KrnMethod) obj);
				} else if (className.equals("UI")) {
               		Or3Frame.instance().jumpInterface((KrnObject) obj, pattern);
				} else if (className.equals("ProcessDef")) { 
               		Or3Frame.instance().jumpService((KrnObject) obj, pattern);
				} else if (className.equals("User")) { 
               		Or3Frame.instance().jumpUser((KrnObject) obj);
				} else if (className.equals("Filter")) { 
               		Or3Frame.instance().jumpFilter((KrnObject) obj);
				} else if (className.equals("ReportPrinter")) {
               		if (Kernel.instance().getLongs((KrnObject) obj, "parent", 0).length > 0)
               			Or3Frame.instance().jumpReport((KrnObject) obj);
               	} else {
               		Or3Frame.instance().jumpClass((KrnObject) obj, null);
               	}
			} catch (KrnException ex) {
	             ex.printStackTrace();
	        }
		} else if (src == copyUIDItem) {
			StringSelection data = new StringSelection("Trigger".equals(attrID) ? objUID.substring(0, objUID.length() - 4) : objUID);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(data, data);
		}
	}
		
	public static Color getBackground(int row, boolean isSelected, boolean isPaintSelected, List<Integer> editedRows) {
		Color backgroundColor;
		if (isPaintSelected && editedRows != null && editedRows.contains(row))
			backgroundColor = Color.PINK;
		else {
			if (!isSelected)
				backgroundColor = (row % 2 == 0 ? Utils.getLightSysColor() : Color.WHITE);
			else
				backgroundColor = Utils.getMidSysColor();
    	}
		return backgroundColor;
	}
	
	public static Color getForeground(boolean isSelected) {
		Color fontColor;
		if (isSelected)
			fontColor = Color.WHITE;
    	else
    		fontColor = Color.BLACK;
		return fontColor;
	}
	
	public static String extract(String fileName, byte[] data) throws IOException {
		int dotPos = fileName.lastIndexOf('.');
		if (dotPos != -1) {
			String ext = fileName.substring(dotPos + 1);
			if ("doc".equals(ext)) {
				WordExtractor extractor = new WordExtractor(new ByteArrayInputStream(data));
				return extractor.getText();
			
			} else if ("docx".equals(ext)) {
				XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(data));
				XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
				return extractor.getText();

			} else if ("xls".equals(ext)) {
				POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(data));
				ExcelExtractor extractor = new ExcelExtractor(fs);
				return extractor.getText();

			} else if ("xlsx".equals(ext)) {
				XSSFWorkbook book = new XSSFWorkbook(new ByteArrayInputStream(data));
				XSSFExcelExtractor extractor = new XSSFExcelExtractor(book);
				return extractor.getText();
			}
		}
		return null;
	}
	public class SearchTableModel extends AbstractTableModel {
		private final String[] heads = {"Описание Объекта", "Описание свойства", "Релевантность"};
	    private List<String[]> data;
	    private boolean isSortAsc = false;
	    private int sortColumn = 0;
	    
	    public SearchTableModel(java.util.List<String[]> list)	{
	    	try	{
	    		this.data = list;
	    	} catch (Exception e) { e.printStackTrace(); }
	    	sortData();
	    }
		
	    public List<String[]> getData() {
	    	return data;
	    }

	    public void setData(List<String[]> data) {
	    	this.data = data;
	    }

	    public int getColumnCount() { return heads.length; }

	    public int getRowCount() { return data.size(); }

	    public String getColumnName(int col) { return heads[col]; }

	    public Object getValueAt(int row, int col) {
	    	if (col == 2)
	    		return data.get(row)[3];
	    	return data.get(row)[col];
	    }
	    
	    public String getRealValueAt(int row, int col) {
	    	return data.get(row)[col];
	    }

	    public Class getColumnClass(int c) { 
	    	return getValueAt(0, c).getClass();
	    }
	    
	    public boolean isSortAsc() {
            return isSortAsc;
        }

        public void setSortAsc(boolean sortAsc) {
            isSortAsc = sortAsc;
        }

        public int getSortColumn() {
            return sortColumn;
        }

        public void setSortColumn(int sortColumn) {
            this.sortColumn = sortColumn;
        }

        public Icon getColumnIcon(int column) {
            if (column == sortColumn) {
                return isSortAsc ? SORT_UP : SORT_DOWN;
            }
            return null;
        }

        public void sortData() {
            Collections.sort(data, new DataComparator(sortColumn, isSortAsc));
        }
	    
	    @Override
		public void fireTableDataChanged() {
			super.fireTableDataChanged();
            setCounterText();
		}
	}
	
	class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = table.getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
            if (modelIndex < 0) {
                return;
            }
            if (model.getSortColumn() == modelIndex) {
                model.setSortAsc(!model.isSortAsc());
            } else {
                model.setSortColumn(modelIndex);
            }
            for (int i = 0; i < model.getColumnCount(); i++) {
                TableColumn column = colModel.getColumn(i);
                int index = column.getModelIndex();
                JLabel renderer = (JLabel) column.getHeaderRenderer();
                renderer.setIcon(model.getColumnIcon(index));
            }
            table.getTableHeader().repaint();
            model.sortData();
            table.tableChanged(new TableModelEvent(model));
            repaint();
        }
    }
	
	class DataComparator implements Comparator<String[]> {

        protected int sortColumn;
        protected boolean isSortAsc;

        public DataComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(String[] u1, String[] u2) {
            int res = 0;
            if (u1 == null)
                res = -1;
            else if (u2 == null)
                res = 1;
            else {
                switch (sortColumn) {
                case 0:
                	res = u1[5].compareTo(u2[5]);
                    break;
                case 1:
                	res = u1[6].compareTo(u2[6]);
                	break;
                case 2:
                	res = u1[3].compareTo(u2[3]);
                    break;
                }
            }
            if (!isSortAsc) {
                res = -res;
            }
            return res;
        }
    }
	
    class CellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
    	
    	private JLabel component = Utils.createLabel();
    	private boolean isPaintSelected = false;
    	private List<Integer> editedRows;
    	
    	public CellRenderer(boolean isPaintSelected, List<Integer> editedRows) {
    		this.isPaintSelected = isPaintSelected;
    		this.editedRows = editedRows;
    	}
    	
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	component.setOpaque(true);
        	component.setBackground(SearchResult.getBackground(row, isSelected, isPaintSelected, editedRows));
	    	component.setForeground(SearchResult.getForeground(isSelected));	    	
	    	component.setText((String)value);
	    	component.setIcon(null);
	    	if (column == 2)
        		component.setHorizontalAlignment(RIGHT);
        	else
        		component.setHorizontalAlignment(LEFT);
	    	if (column == 0) { 
					component.setText(model.getRealValueAt(row, 5));
					component.setIcon(kz.tamur.rt.Utils.getImageIconForClass(model.getRealValueAt(row,7)));
	    	} else if (column == 1) 
	    		component.setText(model.getRealValueAt(row, 6));
			return component;
        }
    }
}
