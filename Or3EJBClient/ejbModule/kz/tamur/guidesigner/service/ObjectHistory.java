package kz.tamur.guidesigner.service;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import kz.tamur.guidesigner.InterfaceActionsConteiner;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.TimeValue;
import com.sun.awt.AWTUtilities;

public class ObjectHistory extends JFrame {
	
	private JScrollPane mainPanel;
	private int DEFAULT_WIDTH = 600;
	private int DEFAULT_HEIGHT = 130;  
	private Timer showTimer;
	private float windowOpacity = 0;
	private JFrame thisFrame;
    private JTable historyNodesTable;
    private HistoryNodesTableModel historyNodesTableModel;
    private ArrayList<HistoryNode> historyNodes;
    private boolean canClose = true;
    private boolean mode;
    private String objectType;

	
	public ObjectHistory(long objectID, String objectType) {
		this.objectType = objectType;
		if (objectType.equals("Интерфейс")) {
			mode = InterfaceActionsConteiner.getInterfacesMode();
		} else if (objectType.equals("Процесс")) {
			mode = ServiceActionsConteiner.getServicesMode();
		}		
		if (mode) {
			super.setUndecorated(true);
			super.setAlwaysOnTop(true);
			super.setResizable(false);
			super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			super.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
			thisFrame = this;
			
			try {
				Kernel krn = Kernel.instance();  	
				KrnClass class_ = krn.getClassByName("Action");
	            KrnAttribute attribute_ = krn.getAttributeByName(class_, "editingDate");
				KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
				long[] massivObjectsId = new long[massivObjects.length];
				for (int i = 0; i < massivObjects.length; i++)
					massivObjectsId[i] = massivObjects[i].id;
				StringValue[] massivUser = krn.getStringValues(massivObjectsId, class_.id, "user", 0, false, 0);
				StringValue[] massivAction = krn.getStringValues(massivObjectsId, class_.id, "action", 0, false, 0);
	            TimeValue[] massivDateTime = krn.getTimeValues(massivObjectsId, attribute_, 0);
	            LongValue[] massivID = krn.getLongValues(massivObjectsId, class_.id, "id", 0);
	            historyNodes = new ArrayList<HistoryNode>();;
	            for (int i = 0; i < massivObjects.length; i++) {
	            	if (objectID == massivID[i].value) {
	            		String details = "-";
	            		if (massivAction[i].value.equals("Edit") || massivAction[i].value.equals("Rename")) {
	            			details = "Отобразить...";
	            		}
	            		historyNodes.add(new HistoryNode(massivUser[i].value, new Date(massivDateTime[i].value.year - 1900, massivDateTime[i].value.month, massivDateTime[i].value.day, massivDateTime[i].value.hour, massivDateTime[i].value.min, massivDateTime[i].value.sec), massivAction[i].value, massivObjectsId[i], details));
	            	}
	            }
			} catch (KrnException e) {
				e.printStackTrace();
			}		
			
			historyNodesTableModel = new HistoryNodesTableModel(historyNodes);
			historyNodesTable = new JTable(historyNodesTableModel) {
	            public void valueChanged(ListSelectionEvent e) {
	                super.valueChanged(e);
	            }
	        };		
			historyNodesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			historyNodesTable.addFocusListener(new FocusListener() {			
				public void focusGained(FocusEvent e) {}
				
				public void focusLost(FocusEvent e) {
					if (canClose) {
						thisFrame.dispose();
					}
				}			
			});
			
			historyNodesTable.addMouseListener(new MouseListener() {			
				public void mouseReleased(MouseEvent e) {}			
				public void mousePressed(MouseEvent e) {}		
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2 ){
						JTable sourceTable = (JTable)e.getSource();
						if (sourceTable.getSelectedColumn() == 4 && sourceTable.getModel().getValueAt(sourceTable.getSelectedRow(), sourceTable.getSelectedColumn()).equals("Отобразить...")) {
							showDetails(e);
						}
					}
				}
			});
			
			setTableAlignment(JLabel.CENTER, historyNodesTable);    
			historyNodesTable.setRowSelectionAllowed(true);
			historyNodesTable.setRequestFocusEnabled(true);
	       
			mainPanel = new JScrollPane(historyNodesTable);
			mainPanel.setBackground(Color.WHITE);
			mainPanel.getVerticalScrollBar().setBackground(Color.WHITE);
			mainPanel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
			mainPanel.setMaximumSize(new Dimension(WIDTH, HEIGHT));
			mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));		
			mainPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			mainPanel.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor(), 1));
			add(mainPanel);		
			
			
			AWTUtilities.setWindowOpacity(thisFrame, windowOpacity);
			super.setVisible(true);
			showTimer = new Timer(100, new ActionListener() {
			     public void actionPerformed(ActionEvent e) {
			    	 windowOpacity += 0.05;
			    	 AWTUtilities.setWindowOpacity(thisFrame, windowOpacity);
			    	 if (windowOpacity > 0.95) {
			    		 showTimer.stop();
			    	 }
			     }
			});	
			showTimer.start();
		}
	}
	
	private void showDetails(MouseEvent e) {
		canClose = false;
		JTextArea detailsText = new JTextArea();
		detailsText.setEditable(false);
		detailsText.addFocusListener(new FocusListener() {			
			public void focusGained(FocusEvent e) {}
			
			public void focusLost(FocusEvent e) {
				canClose = true;
			}			
		});
		if (loadHistoryChanges(e).equals(null)) {
			if (objectType.equals("Интерфейс")) {
				detailsText.setText("В настоящий момент данные о истории изменения интерфейса не доступны!");
			} else if (objectType.equals("Процесс")) {
				detailsText.setText("В настоящий момент данные о истории изменения процесса не доступны!");
			}
		} else {
			String[] text = loadHistoryChanges(e);
			for (int i = 0; i < text.length; i++) {
				detailsText.append(text[i]);
			}
		}
		
		JScrollPane detailsScroll = new JScrollPane(detailsText);	
		detailsScroll.setBackground(Color.WHITE);
		detailsScroll.getVerticalScrollBar().setBackground(Color.WHITE);
		detailsScroll.getHorizontalScrollBar().setBackground(Color.WHITE);
		detailsScroll.setMinimumSize(new Dimension(300, 500));
		detailsScroll.setMaximumSize(new Dimension(300, 500));
		detailsScroll.setPreferredSize(new Dimension(300, 500));		
		detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPopupMenu detailsPopup = new JPopupMenu();
		detailsPopup.setBorderPainted(false);
		detailsPopup.add(detailsScroll);
		detailsPopup.show(thisFrame, DEFAULT_WIDTH, -3);		
		detailsText.requestFocus();
	}
	
	private String[] loadHistoryChanges(MouseEvent event) {
		try {		
			Kernel krn = Kernel.instance();  	
			KrnClass class_ = krn.getClassByName("Action");
			KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
			long[] massivObjectsId = new long[massivObjects.length];
			for (int i = 0; i < massivObjects.length; i++)
				massivObjectsId[i] = massivObjects[i].id;
            for (int i = massivObjects.length - 1; i >= 0; i--) {
               	if (massivObjectsId[i] == (historyNodesTableModel.getNode(((JTable) event.getSource()).getSelectedRow())).getID()) {
         			String[] massivLog = krn.getMemos(massivObjects[i], "log", 0, 0);
        			return massivLog;
            	}	
			}
            return null;
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Выравнивает заголовки таблицы и значения в ячейках по центру
    private void setTableAlignment(int anyAlignment, JTable anyTable) {
        TableColumn Column;
    	JTableHeader myHeader = anyTable.getTableHeader();
        new TableRowColor(anyTable);
    	myHeader.setUpdateTableInRealTime(true);
    	myHeader.addMouseListener(new ColumnListener());
    	myHeader.setReorderingAllowed(false);    
        for (int i = 0; i < anyTable.getColumnCount(); i++) {        	
        	Column = anyTable.getColumnModel().getColumn(i);
        	DefaultTableCellRenderer newRenderer = new DefaultTableCellRenderer();
        	newRenderer.setHorizontalAlignment(anyAlignment);
        	Column.setHeaderRenderer(newRenderer);
        	if (i == ((HistoryNodesTableModel) anyTable.getModel()).getSortColumn())
        		newRenderer.setIcon(((HistoryNodesTableModel) anyTable.getModel()).getColumnIcon(i));        	
        }        
        anyTable.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        anyTable.updateUI();
    }        
	
    public class HistoryNodesTableModel extends AbstractTableModel {
    	
        private final String[] COL_NAMES = {"Пользователь", "Время", "Действие", "ID", "Детали"};
        private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        private List<HistoryNode> historyNodes;
        private boolean isSortAsc = false;
        private int sortColumn = 1;
        private final ImageIcon SORT_UP = kz.tamur.rt.Utils.getImageIcon("SortUpLight");
        private final ImageIcon SORT_DOWN = kz.tamur.rt.Utils.getImageIcon("SortDownLight");

        public HistoryNodesTableModel(ArrayList<HistoryNode> historyNodes) {
        	this.historyNodes = historyNodes;
            sortData();
        }
        
        public int getRowCount() {
			return historyNodes.size();
		}
        
        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public String getColumnName(int columnIndex) {
            return COL_NAMES[columnIndex];
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 1:
                    return Date.class;
                case 3:
                    return Long.class;
                default:
                    return String.class;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            	case 0:
            		return historyNodes.get(rowIndex).userName;
                case 1:
                    return dateFormat.format(historyNodes.get(rowIndex).editingDate);
                case 2:
                    return historyNodes.get(rowIndex).action;
                case 3:
                    return historyNodes.get(rowIndex).id; 
                case 4:
                    return historyNodes.get(rowIndex).details; 
            }
            return null;
        }	
        
        public Object getObjectValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {  
            	case 0:
            		return historyNodes.get(rowIndex).userName;
                case 1:
                    return historyNodes.get(rowIndex).editingDate;
                case 2:
                    return historyNodes.get(rowIndex).action;
                case 3:
                    return historyNodes.get(rowIndex).id; 
                case 4:
                    return historyNodes.get(rowIndex).details; 
            }
            return null;
        }

        public HistoryNode getNode(int row) {
            return historyNodes.get(row);
        }

        public void setNodes(ArrayList<HistoryNode> historyNodes) {        	
            this.historyNodes = historyNodes;
            sortData();
        }

        public boolean hasNode(HistoryNode node) {
        	 for (HistoryNode historyNode : historyNodes) {
        		 if (historyNode.equals(node)) return true;
           }
        	 return false;
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
            if (column == sortColumn)
                return isSortAsc ? SORT_UP : SORT_DOWN;
            return null;
        }
        
        public void sortData() {        	
        	Collections.sort(historyNodes, new NodesComparator(sortColumn, isSortAsc));
        }

		public void fireTableDataChanged() {
			super.fireTableDataChanged();
		}
    }
    
    public class NodesComparator implements Comparator<HistoryNode> {

        protected int sortColumn;
        protected boolean isSortAsc;

        public NodesComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(HistoryNode node_1, HistoryNode node_2) {
            int res = 0;
            if (node_1 == null)
            	res = -1;
            else if (node_2 == null)
            	res = 1;
            else {
            	switch (sortColumn) {   
	            	case 0:
	            		res = node_1.userName.compareTo(node_2.userName);
	            		break;
                    case 1:
                		res = node_1.editingDate.compareTo(node_2.editingDate);
                		break;
                    case 2:
                		res = node_1.action.compareTo(node_2.action);
                		break; 
                    case 3:
                		res = node_1.id.compareTo(node_2.id);
                		break;  
                    case 4:
                		res = node_1.details.compareTo(node_2.details);
                		break;   
            	}
            }
            if (!isSortAsc) {
                res = -res;
            }
            return res;
        }
    } 
    
    public class HistoryNode {
    	
    	private String userName;
    	private Date editingDate;
    	private String action;
    	private String details;
    	private Long id;
    	
    	public HistoryNode(String userName, Date editingDate, String action, Long id, String details) {
    		this.userName = userName;
    		this.editingDate = editingDate;
    		this.action = action;
    		this.id = id;
    		this.details = details; 
    	}
    	
    	public String getUserName() {
    		return userName;
    	}
    	
    	public Date getEditingDate() {
    		return editingDate;
    	}
    	
    	public String getAction() {
    		return action;
    	}
    	
    	public Long getID() {
    		return id;
    	}
    	
    	public String getDetails() {
    		return details;
    	}
    }
    
    public class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = historyNodesTable.getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex =
                    colModel.getColumn(columnModelIndex).getModelIndex();
            if (modelIndex < 0) {
                return;
            }
            if (((HistoryNodesTableModel) historyNodesTable.getModel()).getSortColumn() == modelIndex) {
            	((HistoryNodesTableModel) historyNodesTable.getModel()).setSortAsc(!((HistoryNodesTableModel) historyNodesTable.getModel()).isSortAsc());
            } else {
            	((HistoryNodesTableModel) historyNodesTable.getModel()).setSortColumn(modelIndex);
            }
            for (int i = 0; i < ((HistoryNodesTableModel) historyNodesTable.getModel()).getColumnCount(); i++) {
                TableColumn column = colModel.getColumn(i);
                int index = column.getModelIndex();
                JLabel renderer = (JLabel) column.getHeaderRenderer();
                renderer.setIcon(((HistoryNodesTableModel) historyNodesTable.getModel()).getColumnIcon(index));
                renderer.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor(), 1));
            }
            historyNodesTable.getTableHeader().repaint();
            ((HistoryNodesTableModel) historyNodesTable.getModel()).sortData();
            historyNodesTable.tableChanged(new TableModelEvent(((HistoryNodesTableModel) historyNodesTable.getModel())));
            repaint();
        }
    }
    
    class ColorRenderer extends JLabel implements TableCellRenderer
    {		
		public ColorRenderer() {			
			setOpaque(true);
		}
     
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (value != null)
				setText(value.toString());
			if (isSelected) {
				setBackground(Color.PINK);
				setForeground(Color.BLUE);
			} else {
				setBackground(Utils.getLightGraySysColor());
				setForeground(Color.BLACK);				
			}
			setHorizontalAlignment(JLabel.CENTER);
			setFont(new Font("Arial", Font.ITALIC, 12));
			return this;
		}
    }
    
	class TableRowColor {		
		public TableRowColor(JTable anyTable) {			
			ColorRenderer anyColorRenderer = new ColorRenderer();
			for (int i = 0; i < anyTable.getColumnCount(); i++)
				anyTable.getColumn(anyTable.getColumnName(i)).setCellRenderer(anyColorRenderer);			
		}
	}
	
}


