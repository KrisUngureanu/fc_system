package kz.tamur.guidesigner.changemon;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.SOUTH;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.getImageIcon;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.StringValue;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 31.01.2009
 * Time: 13:37:56
 */
public class ChangeMonHistoryPanel extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable changeHistoryTable;
    private MainFrame.DescLabel counterLabel = kz.tamur.comps.Utils.createDescLabel("");
    private JTextPane comText = new JTextPane();
    private int selRowIdx;
    private int rowCount;
    private static final ImageIcon SORT_UP = getImageIcon("SortUpLight");
    private static final ImageIcon SORT_DOWN = getImageIcon("SortDownLight");
    private ChangeHistoryTableModel model;
    private Map<Long,String> users=new HashMap<Long,String>();
    private Map<Long,String> attrs=new HashMap<Long,String>();
    private Map<Long,String> langs=new HashMap<Long,String>();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
	private static JLabel infoLabel = new JLabel("История изменений:");
	private JButton showHistoryBtn = createToolButton("OpenFrom", "Показать изменения");

    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ChangeMonHistoryPanel() {
        super();
        /*
        Kernel krn = Kernel.instance();
        try {
	        changes = krn.getVcsChanges(true);

        } catch (KrnException e) {
            e.printStackTrace();
        }
        */
        setLayout(new GridBagLayout());


        model = new ChangeHistoryTableModel(new ArrayList<KrnVcsChange>());
        changeHistoryTable = new JTable(model) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);
                setCounterText();
        		if( !e.getValueIsAdjusting() && e.getSource() == changeHistoryTable.getSelectionModel()){
        			showHistoryBtn.setEnabled(changeHistoryTable.getSelectedRow()>=0);
        		}		
            }
        };

        JTableHeader header = changeHistoryTable.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn tc = changeHistoryTable.getColumnModel().getColumn(i);
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            tc.setHeaderRenderer(r);
            if (i == model.getSortColumn())
                r.setIcon(model.getColumnIcon(i));
        }

        changeHistoryTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel tablePain = new JPanel();
        tablePain.setLayout(new BorderLayout(3, 3));
        JPanel counter = new JPanel();
        counter.setLayout(new BorderLayout(3, 3));
        counter.add(counterLabel, BorderLayout.EAST);
        JScrollPane sp = new JScrollPane(changeHistoryTable);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        sp.setMinimumSize(new Dimension(600, 200));
        tablePain.add(sp);
        
        JPanel curComment = new JPanel();
        curComment.setLayout(new GridBagLayout());
        JScrollPane commentSP = new JScrollPane();
        commentSP.setMinimumSize(new Dimension(400, 40));
        commentSP.setPreferredSize(new Dimension(1200,40));
        GridBagConstraints c = new GridBagConstraints(0,0,1,0,0.9,1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 1, 0);
        
        comText.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
            	comText.setEditable(true);

            }

            @Override
            public void focusGained(FocusEvent e) {
            	comText.setEditable(false);

            }
        });
        
        commentSP.getViewport().add(comText, null);
        curComment.add(commentSP, c);

        showHistoryBtn.setEnabled(false);
        toolBar.add(showHistoryBtn);
        toolBar.setBorder(null);
		toolBar.setMinimumSize(new Dimension(400, 30));
        showHistoryBtn.addActionListener(this);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());
        listPanel.add(infoLabel, new CnrBuilder().x(0).y(0).anchor(CENTER).ins(5, 5, 0, 5).build());
        listPanel.add(tablePain, new CnrBuilder().x(0).y(1).fill(BOTH).wtx(1).wty(1).build());
        add(toolBar, new CnrBuilder().x(0).y(0).anchor(WEST).build());
        add(counter, new CnrBuilder().x(1).y(0).anchor(EAST).build());
        add(curComment, new CnrBuilder().x(0).y(2).anchor(SOUTH).fill(HORIZONTAL).wtx(1).build());
        add(listPanel, new CnrBuilder().x(0).y(1).w(2).fill(BOTH).anchor(WEST).wtx(1).wty(1).ins(5, 5, 0, 5).build());

        setCounterText();
        setOpaque(isOpaque);
        // Установка прозрачности, зависящей от глобальных настроек системы
        listPanel.setOpaque(isOpaque);
        tablePain.setOpaque(isOpaque);
        counter.setOpaque(isOpaque);
		toolBar.setOpaque(isOpaque);
		changeHistoryTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int row = changeHistoryTable.rowAtPoint(e.getPoint());
					if (row != -1) {
						KrnVcsChange change = model.changes.get(row);
						if(change!=null){
							try{
								String increment=Kernel.instance().getVcsHistoryDataIncrement(change);
								File file=new File("temp.txt");
		                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
		                        os.write(increment!=null && increment.length()>0?increment.getBytes():new byte[0]);
		                        os.close();
								open(file);
							}catch(KrnException ex){
								ex.printStackTrace();
							} catch (java.io.IOException e1) {
								e1.printStackTrace();
							}
						}
					}
					getTopLevelAncestor().setCursor(Cursor.getDefaultCursor());
				}
			}
		});
 }

    public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src.equals(showHistoryBtn)){
				int row=changeHistoryTable.getSelectedRow();
				KrnVcsChange change = model.changes.get(row);
				if(change!=null){
					try{
						String increment=Kernel.instance().getVcsHistoryDataIncrement(change);
						File file=new File("temp.txt");
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        os.write(increment!=null && increment.length()>0?increment.getBytes():new byte[0]);
                        os.close();
						open(file);
					}catch(KrnException ex){
						ex.printStackTrace();
					} catch (java.io.IOException e1) {
						e1.printStackTrace();
					}
				}
			}
    }

    private void open(File f) {
        try {
        	if (f != null) {
                String str = f.getAbsolutePath();
                System.out.println(str);
                String[] cmd = new String[] {"explorer.exe", str};        
                Map<String, String> newEnv = new HashMap<String, String>();
                newEnv.putAll(System.getenv());
                String[] i18n = new String[cmd.length + 2];
                i18n[0] = "cmd";
                i18n[1] = "/C";
                i18n[2] = cmd[0];
                for (int counter = 1; counter < cmd.length; counter++)
                {
                    String envName = "JENV_" + counter;
                    i18n[counter + 2] = "%" + envName + "%";
                    newEnv.put(envName, cmd[counter]);
                }
                cmd = i18n;

                ProcessBuilder pb = new ProcessBuilder(cmd);
                Map<String, String> env = pb.environment();
                env.putAll(newEnv);
                pb.start();
        	}
        } catch (java.io.IOException e) {
			e.printStackTrace();
		}
    }

	public void refreshTable(KrnVcsChange chObj, boolean isReload) {
		List<KrnVcsChange> changes = new ArrayList<KrnVcsChange>();
		infoLabel.setText("История изменений:");
		if (chObj != null) {
			try {
				Kernel krn = Kernel.instance();
				if (chObj.cvsChangeMethod != null) {
					// запрашиваем данные о всех изменениях выбранного метода
					changes = krn.getVcsHistoryChanges(true, chObj.cvsChangeMethod.uid, 2,false);
				} else if (chObj.cvsChangeObj != null) {
					// запрашиваем данные о всех изменениях выбранного объекта
					changes = krn.getVcsHistoryChanges(false, String.valueOf(chObj.cvsChangeObj.id), -1,false);
				} else if (chObj.cvsChangeClass != null) {
					// запрашиваем данные о всех изменениях выбранного триггера класса
					changes = krn.getVcsHistoryChanges(true, chObj.cvsChangeClass.uid, chObj.typeId,false);
				} else if (chObj.cvsChangeAttr != null) {
					// запрашиваем данные о всех изменениях выбранного триггера атрибута
					changes = krn.getVcsHistoryChanges(true, chObj.cvsChangeAttr.uid, chObj.typeId,false);
				}
				fillTitles(changes, chObj);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		ChangeHistoryTableModel tm = (ChangeHistoryTableModel) changeHistoryTable
				.getModel();
		tm.setChanges(changes);
		tm.fireTableDataChanged();
	}
	
	public void refreshTableFromSearch(List<KrnVcsChange> changes, String text) {
		if (text.matches(".+"))
			infoLabel.setText(Funcs.sanitizeHtml(text));
		ChangeHistoryTableModel tm = (ChangeHistoryTableModel) changeHistoryTable
				.getModel();
		tm.setChanges(changes);
		tm.fireTableDataChanged();
	}
	
    private void fillTitles(List<KrnVcsChange> changes,KrnVcsChange chObj){
        Kernel krn = Kernel.instance();
        try {
            if(changes!=null){
            	List<Long> usersList=new ArrayList<Long>();
	            for(KrnVcsChange change:changes){
	            	if(change.user!=null && !users.containsKey(change.user.id)) 
	            		usersList.add(change.user.id);
	            	change.title=chObj.title;
	            	if(change.attrId>0 && !attrs.containsKey(change.attrId)){
	            		KrnAttribute ch_attr=krn.getAttributeById(change.attrId);
	            		// Делаем проверку, так как атрибут может быть удаленным
	            		attrs.put(change.attrId, ch_attr != null ? ch_attr.name : "--атрибут не найден--");
	            	}
	            	if(change.langId>0 && !langs.containsKey(change.langId)){
	            		KrnAttribute ch_attr_lang=krn.getAttributeByName(Kernel.SC_LANGUAGE,"code");
	            		String ch_lang=krn.getStringsSingular(change.langId, ch_attr_lang.id,0, false, false);
	            		langs.put(change.langId, ch_lang);
	            	}
	            }
	            KrnAttribute attr=krn.getAttributeByName(Kernel.SC_USER, "name");
	            long[] objIds=Funcs.makeLongArray(usersList);
	            if(objIds!=null && objIds.length>0){
	            	StringValue[] titles=krn.getStringValues(objIds, attr, 0, false, 0);
	            	for(StringValue title:titles){
	            		if(title.index==0)
	            			users.put(title.objectId, title.value);
	            	}
	            }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    	
    }
    private void setCounterText() {
        rowCount = changeHistoryTable.getModel().getRowCount();
        selRowIdx = changeHistoryTable.getSelectedRow() + 1;
        counterLabel.setText(selRowIdx + " / " + rowCount + " ");
        if(changeHistoryTable.getModel() != null && changeHistoryTable.getModel().getRowCount() > 0)
        comText.setText(((ChangeHistoryTableModel)changeHistoryTable.getModel()).changes.get(selRowIdx > 0?selRowIdx-1:selRowIdx).comment);
    }

    private class ChangeHistoryTableModel extends AbstractTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String[] COL_NAMES = { "Наименование","Дата изменения","Дата подтверждения", "Ответственный", "Комментарий","Атрибут","Язык"};
        private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        private boolean isSortAsc = false;
        private int sortColumn = 1;

        java.util.List<KrnVcsChange> changes;

        public ChangeHistoryTableModel(java.util.List<KrnVcsChange> changes) {
        	this.changes=changes;
        	sortData();
        }

        public int getRowCount() {
            return changes.size();
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
                return Long.class;
            default:
                return String.class;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                return changes.get(rowIndex).title;
            case 1:
                return changes.get(rowIndex).dateChange!=null?df.format(changes.get(rowIndex).dateChange):null;
            case 2:
                return changes.get(rowIndex).dateConfirm!=null?df.format(changes.get(rowIndex).dateConfirm):null;
            case 3:
                return users.get(changes.get(rowIndex).user.id);
            case 4:
                return changes.get(rowIndex).comment;
            case 5:
                return attrs.get(changes.get(rowIndex).attrId);
            case 6:
                return langs.get(changes.get(rowIndex).langId);
            }
            return null;
        }

        public void setChanges(java.util.List<KrnVcsChange> changes) {
            this.changes = changes;
            sortData();
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
            Collections.sort(changes, new ChangesComparator(sortColumn, isSortAsc));
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
            setCounterText();
        }
    }

    class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = changeHistoryTable.getColumnModel();
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
            changeHistoryTable.getTableHeader().repaint();
            model.sortData();
            changeHistoryTable.tableChanged(new TableModelEvent(model));
            repaint();
        }
    }

    class ChangesComparator implements Comparator<KrnVcsChange> {

        protected int sortColumn;
        protected boolean isSortAsc;

        public ChangesComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(KrnVcsChange ch1, KrnVcsChange ch2) {
            int res = 0;
            if (ch1 == null)
                res = -1;
            else if (ch2 == null)
                res = 1;
            else {
                switch (sortColumn) {
                case 0:
                    res = ch1.title.compareTo(ch2.title);
                    break;
                case 1:
                	res = ch1.dateChange==null ? ch2.dateChange==null  ? 0 : -1 : ch2.dateChange==null ? 1 : ch1.dateChange.compareTo(ch2.dateChange);
                    break;
                case 2:
                	res = ch1.dateConfirm==null ? ch2.dateConfirm==null  ? 0 : -1 : ch2.dateConfirm==null ? 1 : ch1.dateConfirm.compareTo(ch2.dateConfirm);
                    break;
                case 3:
                    res = ch1.user.id>ch2.user.id?1:-1;
                    break;
                case 4:
                    res = ch1.comment==null?-1:ch2.comment==null?1:ch1.comment.compareTo(ch2.comment);
                    break;
                case 5:
                    res = ch1.attrId>ch2.attrId?1:-1;
                    break;
                case 6:
                    res = ch1.langId>ch2.langId?1:-1;
                    break;
                }
            }
            if (!isSortAsc) {
                res = -res;
            }
            return res;
        }
    }
    public int processExit() {
        return ButtonsFactory.BUTTON_NOACTION;
    }
}
