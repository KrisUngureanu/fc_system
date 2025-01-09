package kz.tamur.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.rt.Utils;
import kz.tamur.util.ExpressionEditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.UserSessionValue;

public class TriggersPanel extends GradientPanel {
    private JTable table;
    private String[] columns = new String[] { "Тип тригера", "Id объекта", "Название объекта", "Название тригера", "Формула" };
    private TriggersTableModel model = new TriggersTableModel();
    private Kernel kernel = Kernel.instance();
    private ClassBrowser classBrowser;

	public TriggersPanel(ClassBrowser classBrowser) {
		this.classBrowser = classBrowser;
        setLayout(new BorderLayout());
		table = new JTable(null, columns);
		table.setFont(Utils.getDefaultFont());
		JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);
        try {
			model.add(Kernel.instance().getOrlangTriggersInfo());
		} catch (KrnException e) {
			e.printStackTrace();
		}
        table.setModel(model);
        table.setRowSorter(new TriggersTableRowSorter(model));
        table.getColumnModel().getColumn(4).setCellRenderer(new TriggersTableCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new TriggersTableCellEditor(new JCheckBox()));
	}
	
	public ClassBrowser getClassBrowser() {
		return classBrowser;
	}
	
	class TriggersTableRowSorter extends TableRowSorter {
    	
		private int columnCount;
    	
        public TriggersTableRowSorter(TriggersTableModel model) {
            super(model);
            columnCount = model.getColumnCount();
        }

        @Override
        public Comparator<?> getComparator(final int column) {
        	switch(column) {
	        	case 0: case 2: case 3:
	        		return super.getComparator(column);
	        	case 1:
	        		return new Comparator<String>() {
	                    @Override
	                    public int compare(String s1, String s2) {
	                        return (int) (Long.parseLong(s1) - Long.parseLong(s2));
	                    }
	                };
            	default:
	            	return null;
        	}
        }
    }
	
	class TriggersTableCellRenderer extends JButton implements TableCellRenderer {
		
		public TriggersTableCellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			setText("Просмотреть код");
			setFont(Utils.getDefaultFont());
			setFocusable(false);
			return this;
		}
	}
	
	public class TriggersTableCellEditor extends DefaultCellEditor {
		private JButton button;
		private boolean clicked;
		private int row, col;
		private JTable table;

		public TriggersTableCellEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setFont(Utils.getDefaultFont());
			button.setFocusable(false);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.table = table;
			this.row = row;
			this.col = column;
			button.setText("Просмотреть код");
			clicked = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (clicked) {
				try {
					OrlangTriggerInfo tr = model.get(table.convertRowIndexToModel(row));
	                final int ownerType = tr.getOwnerType();
	                String name = tr.getName();
	                final int triggerType = Utils.getTriggerTypeByName(name);
					
	                long ownerId = tr.getOwnerId();
	                final Object owner;
	                if (ownerType == 0) {
	                	owner = kernel.getClass(ownerId);
	                } else {
	                	owner = kernel.getAttributeById(ownerId);
	                }
	                
					boolean readOnly = false;
                 	UserSessionValue us = kernel.vcsLockModel(ownerType == 0 ? ((KrnClass) owner).uid : ((KrnAttribute) owner).uid, Utils.getModelChangeTypeByTriggerType(triggerType, ownerType));
                     if (us != null) {
                         if (triggerVcsLock(owner, 0, triggerType, us) == ButtonsFactory.BUTTON_YES) {
                             readOnly = true;
                         } else {
                 			clicked = false;
                			return "Просмотреть код";
                         }
                     }
					
					byte[] bs = tr.getExpression();
					String expr = (bs != null && bs.length > 0) ? new String(bs, "UTF-8") : "";
				    JTextArea exprArea = new JTextArea();
				    exprArea.setText(expr);

	                final ExpressionEditor ex = new ExpressionEditor(exprArea.getText(), tr.getTransaction(), TriggersPanel.this, owner, triggerType, readOnly);
	                ex.setVisibleTransactionCheck(true);
	                ActionListener btnaction = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							try {
				                if (ownerType == 0) {
				                	kernel.setClsTriggerEventExpression(ex.getExpression(), ((KrnClass) owner).id, triggerType, ex.getStatusTansactionCheck());
				                } else {
				                	kernel.setAttrTriggerEventExpression(ex.getExpression(), ((KrnAttribute) owner).id, triggerType, ex.getStatusTansactionCheck());
		                			classBrowser.updateAttrTree(kernel.getClassNode(((KrnAttribute) owner).classId));
				                }
				                TriggersPanel.this.refreshTable();
							} catch (KrnException e) {
								e.printStackTrace();
							}
						}
					};
	                if (ownerType == 0) {
						EditorWindow.addTab(((KrnClass) owner).uid, "Класс '" + ((KrnClass) owner).name + "', событие '" + name + "'", ex, btnaction, "TriggersBrowser");
	                } else {
						EditorWindow.addTab(((KrnAttribute) owner).uid, "Атрибут '" + ((KrnAttribute) owner).name + "', событие '" + name + "'", ex, btnaction, "TriggersBrowser");
	                }
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			clicked = false;
			return "Просмотреть код";
		}

		private int triggerVcsLock(Object triggerOwner, int ownerType, int triggerType, UserSessionValue us) {
			StringBuilder mess = new StringBuilder();
			mess.append("Триггер '" + Utils.getTriggerNameByTriggerType(triggerType, 1) + (ownerType == 0 ? "' класса '" : "' атрибута '"))
					.append(ownerType == 0 ? ((KrnClass) triggerOwner).name : ((KrnAttribute) triggerOwner).name)
					.append("' редактируется!\nПользователь: ").append(us.name).append("\nОткрыть триггер в режиме просмотра?");
			return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess.toString(), 235, 130);
		}
			
		public boolean stopCellEditing() {
			clicked = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
	
	class TriggersTableModel extends AbstractTableModel {

        private List<OrlangTriggerInfo> objs;

        public TriggersTableModel() {
            objs = new ArrayList<OrlangTriggerInfo>();
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
        	return column == 4;
        }

        public void clear() {
            if (objs.size() > 0) {
                int lastRow = objs.size() - 1;
                objs.clear();
                fireTableRowsDeleted(0, lastRow);
            }
        }

        public int add(OrlangTriggerInfo obj) {
            int firstRow = this.objs.size();
            objs.add(obj);
            fireTableRowsInserted(firstRow, firstRow);
            return objs.size() - 1;
        }

        public void add(List<OrlangTriggerInfo> objs) {
            if (objs.size() > 0) {
                int firstRow = this.objs.size();
                this.objs.addAll(objs);
                fireTableRowsInserted(firstRow, this.objs.size() - 1);
            }
        }

        public void add(OrlangTriggerInfo[] objs) {
            int firstRow = this.objs.size();
            for (OrlangTriggerInfo obj : objs)
                this.objs.add(obj);
            fireTableRowsInserted(firstRow, this.objs.size() - 1);
        }

        public void remove(int index) {
            objs.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public void remove(OrlangTriggerInfo obj) {
            int index = objs.indexOf(obj);
            if (index >= 0) {
                objs.remove(obj);
                fireTableRowsDeleted(index, index);
            }
        }

        public OrlangTriggerInfo get(int index) {
            return objs.get(index);
        }

        @Override
        public int getRowCount() {
            return objs.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
        	OrlangTriggerInfo obj = objs.get(rowIndex);
            if (obj != null) {
                switch (columnIndex) {
                case 0:
                    return obj.getOwnerType() == 0 ? "Триггер класса" : "Триггер атрибута";
                case 1:
                    return obj.getOwnerId();
                case 2:
                	int mode = obj.getOwnerType();
                	if (mode == 0) {
                		KrnClass cls = null;
						try {
							cls = kernel.getClass(obj.getOwnerId());
						} catch (KrnException e) {
							e.printStackTrace();
						}
                		return cls == null ? "-" : cls.name;
                	} else {
                		KrnAttribute attr = null;
                		KrnClass cls = null;
                		try {
							attr = kernel.getAttributeById(obj.getOwnerId());
							if (attr != null) {
								cls = kernel.getClass(attr.classId);
							}
						} catch (KrnException e) {
							e.printStackTrace();
						}
                		String clsName = cls == null ? "-" : cls.name;
                		return clsName + "." + (attr == null ? "-" : attr.name);
                	}
                case 3:
                	return obj.getName();
                case 4:
                	return "Просмотреть код";
                }
            }
            return null;
        }
    }
	
	public void refreshTable() {
		try {
			model.clear();
			model.add(Kernel.instance().getOrlangTriggersInfo());
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}
}