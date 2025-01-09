/*
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: Dec 13, 2002
 * Time: 8:36:16 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cifs.or2.client.replicator;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.TableSorter;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ReplicatorLogs extends javax.swing.JFrame{

	/* префиксы файлов репликаций */
  private static final String CONFIRM_FILE_PREFIX_RUNLOW = "CL_";
  private static final String CONFIRM_FILE_PREFIX_RISE = "CR_";
  private static final String REPLICATION_FILE_PREFIX_RUNLOW = "RL_";
  private static final String REPLICATION_FILE_PREFIX_RISE = "RR_";

	/* статусы состояния процесса репликации */
	private static final int STATUS_NONE = 999;
	private static final int STATUS_STARTED = 0;
	private static final int STATUS_ENDED = 1;
	private static final int STATUS_NOT_RECIEVED_CLIENT_SGDS = -2;
	private static final int STATUS_RECIEVED_CLIENT_SGDS = 2;
	private static final int STATUS_NOT_RECIEVED_SERVER_SGDS = -3;
	private static final int STATUS_RECIEVED_SERVER_SGDS = 3;
	private static final int STATUS_NO_CONFIRM = -4;
	private static final int STATUS_YES_CONFIRM = 4;
	private static final int STATUS_NO_DATA = 5;
  private static final int STATUS_ERROR = 6;
  private static final int STATUS_WARNING = 7;

	/* типы журанлов репликаций */
	private static final int LOG_IMPORT = 0;
	private static final int LOG_EXPORT = 1;
	private static final int LOG_CONFIRM = 2;
	private static final int LOG_ROOT = 3;
	private static final int LOG_COLL = 4;
	private static final int LOG_COLL_ITEM = 5;
	private static final int LOG_COLL_ITEM_EMPTY = 6;
	private static final int LOG_SUCCESS_REPLICATIONS = 7;
  private static final int LOG_NEGATIVE_EVENTS = 8;
  private static final int LOG_CHECKDB = 9;

	private Kernel krn;
	private JPanel contentPane;
	private JPanel importsPane = new JPanel();
  private JPanel exportsPane = new JPanel();
  private JPanel treePane = new JPanel();
	private JTree tree;
	private GeneralTable general_table;
	private ImportsTable imports_table;
	private ExportsTable exports_table;

	protected SimpleDateFormat formatter_date;

	class MainTable extends JTable {
		protected String[] columnNames;
		protected DefaultTableModel model;
		public MainTable () {
			setAutoResizeMode(AUTO_RESIZE_OFF);
		}
		protected void initColumnSizes() {
			TableColumn column = null;
			Component comp = null;
			int headerWidth = 0;
			int cellWidth = 0;
			TableModel model = getModel();
			for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
				column = getColumnModel().getColumn(i);

				comp = getDefaultRenderer(model.getColumnClass(i)).
												 getTableCellRendererComponent(
														 this, column.getHeaderValue(),
														 false, false, 0, i);
				headerWidth = comp.getPreferredSize().width;
				if (model.getRowCount() > 0) {
					comp = getDefaultRenderer(model.getColumnClass(i)).
													 getTableCellRendererComponent(
															 this, getValueAt(0,i),
															 false, false, 0, i);
					cellWidth = comp.getPreferredSize().width;
				}

				//XXX: Before Swing 1.1 Beta 2, use setMinWidth instead.
				column.setPreferredWidth(Math.max(headerWidth, cellWidth)+10);
			}
		}
	}
	public class LogFilenameFilter implements FilenameFilter
  {
    Pattern re;
    public LogFilenameFilter(String mask) throws KrnException
    {
      try {
        re = Pattern.compile(mask);
      } catch (Exception e)  {
        System.out.println("error from REException");
          throw new KrnException (0, e.getMessage());
      }
    }
    public boolean accept(File dir, String name)
    {
			return re.matcher(name).find();
    }
  }
	class LogStatus {
		private int log_type;
		private long status;
		public String toString() {
			String res = "";
			switch (log_type) {
				case LOG_IMPORT:
					switch ((int)status) {
						case STATUS_STARTED: res = "процесс импорта запущен."; break;
						case STATUS_ENDED: res = "импорт завершен успешно."; break;
						case STATUS_NOT_RECIEVED_CLIENT_SGDS:
							res = "импорт завершен, но файл подтверждения непринят "+
								"системой СГДС (клиентом)."; break;
						case STATUS_NOT_RECIEVED_SERVER_SGDS:
							res = "файл подтверждения принят системой СГДС (клиентом),"+
								" но непринят системой СГДС (сервером)."; break;
						case STATUS_RECIEVED_CLIENT_SGDS:
							res = "файл подтверждения принят успешно "+
								"системой СГДС (клиентом)."; break;
						case STATUS_RECIEVED_SERVER_SGDS:
							res = "файл подтверждения принят успешно системой СГДС (сервером)."; break;
            case STATUS_ERROR: res = "ошибка при импорте."; break;
					}; break;
				case LOG_EXPORT:
					switch ((int)status) {
						case STATUS_STARTED: res = "процесс экспорта запущен."; break;
						case STATUS_ENDED: res = "экспорт завершен успешно."; break;
						case STATUS_NOT_RECIEVED_CLIENT_SGDS:
							res = "экспорт завершен, но файл экспорта непринят "+
								"системой СГДС (клиентом)."; break;
						case STATUS_NOT_RECIEVED_SERVER_SGDS:
							res = "файл экспорта принят системой СГДС (клиентом),"+
								" но непринят системой СГДС (сервером)."; break;
						case STATUS_RECIEVED_CLIENT_SGDS:
							res = "файл экспорта принят успешно "+
								"системой СГДС (клиентом)."; break;
						case STATUS_RECIEVED_SERVER_SGDS:
							res = "файл экспорта принят успешно системой СГДС (сервером)."; break;
						case STATUS_NO_CONFIRM: res = "нет файла подтверждения."; break;
            case STATUS_YES_CONFIRM: res = "файл подтверждения получен."; break;
            case STATUS_NO_DATA: res = "нет данных для экспорта."; break;
            case STATUS_ERROR: res = "ошибка при экспорте."; break;
					}; break;
        case LOG_CHECKDB:
          switch ((int)status) {
            case STATUS_ERROR: res = "ОШИБКА! Надена ошибка при проверке базы."; break;
            case STATUS_WARNING: res = "ПРЕДУПРЕЖДЕНИЕ! Надена ошибка при проверке базы."; break;
          }
          break;
			}
			return res;
		}

		public LogStatus(int log_type, long status) {
			this.log_type = log_type;
			this.status = status;
		}
	}
	class GeneralTable extends MainTable {
    private void fillTable(
      KrnClass cls, KrnObject replRecord, int log_type, LogStatus status
    )
    throws KrnException
    {
      KrnObject entity = krn.getObjectsSingular(
        replRecord.id,
        krn.getAttributeByName(cls,"entity").id, false
      );
      String fileName = "";
      if (!(log_type == LOG_EXPORT && status.status == STATUS_STARTED)) {
        fileName = krn.getStringsSingular (
          replRecord.id,
          krn.getAttributeByName(cls,"fileName").id, 0, false, false
        );
      }
      String error_name = krn.getStringsSingular (
        replRecord.id,
        krn.getAttributeByName(cls,"error message").id, 0, false, false
      );
      long d; Database fromDb = null;
      d = krn.getLongsSingular(replRecord,
        krn.getAttributeByName(cls,"date"),false) * 1000L;
      KrnObject o = krn.getObjectsSingular(replRecord.id,
        krn.getAttributeByName(cls,"database").id,false);
      if (o != null)
        fromDb = new Database(o);
      KrnObject replObject = null;
      try {
        replObject = krn.getObjectsSingular(replRecord.id,
          krn.getAttributeByName(cls,"replObject").id,false);
      } catch (Exception e) { e.printStackTrace(); }

      long replicationID = 0;
      if (replObject != null)
        replicationID = krn.getLongsSingular (
          replObject,
          krn.getAttributeByName(
            krn.getClassByName("ReplCollection"),"replicationID"), false
        );

      String[] row = new String[columnNames.length];
      String db_name = "";
      if (fromDb != null)
        db_name = fromDb.toString();
      row[0] = db_name;
      long entity_id = -1;
      if (entity != null)
        entity_id = entity.id;
      row[1] = status.toString();
      row[2] = formatter_date.format(new java.util.Date(d));
      row[3] = fileName;
      row[4] = String.valueOf(entity_id);
      row[5] = String.valueOf(replRecord.id);
      row[6] = error_name;
      if (replObject != null)
        row[7] = String.valueOf(replicationID);
      model.addRow(row);
    }
		protected void load(int log_type, long replicationID)
		{
			try {
				for (int i = model.getRowCount()-1; i >= 0; i--)
					model.removeRow(i);
				int requist_server_log_type = 0;
				KrnClass clsReplCollection = krn.getClassByName("ReplCollection");
				KrnClass cls_repl_row = krn.getClassByName("Зап табл репликации");
				KrnClass cls = null;
				KrnClass clsReplCurrentStatus = krn.getClassByName("ReplCurrentStatus");

				// вытаскиваем записи репликации
				KrnObject[] repl_item = null;
				if (replicationID > -1) { // для конкретной репликации
					repl_item = krn.getReplRecords(log_type, replicationID);
					cls = cls_repl_row;
          for (int i = 0; i < repl_item.length; i++) {
            LogStatus status = new LogStatus(log_type,
              krn.getLongsSingular (
                repl_item[i],
                krn.getAttributeByName(cls,"status"), false)
            );
            fillTable(cls, repl_item[i], log_type, status);
          }
				}
				else { // для записей последних проблемных состояний
				  repl_item = krn.getObjectsLiveOfClass(clsReplCurrentStatus);
					cls = clsReplCurrentStatus;
          boolean for_confirm = false;
          if (log_type == LOG_CONFIRM) {
            log_type = LOG_EXPORT;
            for_confirm = true;
          }
          for (int i = 0; i < repl_item.length; i++) {
            long server_log_type = krn.getLongsSingular (
              repl_item[i],
              krn.getAttributeByName(cls,"logType"), false
            );
            if (log_type != server_log_type)
              continue;
            LogStatus status = new LogStatus(log_type,
              krn.getLongsSingular (
                repl_item[i],
                krn.getAttributeByName(cls,"status"), false)
            );
            if (for_confirm) {
              if (status.status != STATUS_NO_CONFIRM)
                continue;
            }
            else {
              if (status.status == STATUS_NO_CONFIRM)
                continue; // замудрёно!
            }
            fillTable(cls, repl_item[i], log_type, status);
          }
				}
				TableSorter ts = (TableSorter) getModel();
				ts.sortByColumn(2);
        initColumnSizes();
			} catch(Exception e) {
				e.printStackTrace();
				new KrnException(0, e.getMessage());
			}
		}
		public GeneralTable() {
			columnNames = new String [] {
				"база", "статус", "дата/время", "файл", "идент. имп/эксп",
        "id зап табл", "сообщение об ошибке", "у/н репл"
			};
			model = new DefaultTableModel(columnNames, 0);
			TableSorter ts = new TableSorter(model);
			ts.addMouseListenerToHeaderInTable(this);
			setModel(ts);
		}
	}
	class ImportsTable extends MainTable {
		protected void load(long replicationID) {
			try {
				for (int i = model.getRowCount()-1; i >= 0; i--)
					model.removeRow(i);
				KrnClass clsReplCollection = krn.getClassByName("ReplCollection");
				KrnClass clsReplRow = krn.getClassByName("Зап табл репликации");
				KrnObject curDb = krn.getCurrentDb();
				KrnObject[] imports = krn.getObjects(curDb,"imports",0);
				for (int i = 0; i < imports.length; i++) {
					long id = imports[i].id;
					KrnObject repl_row = krn.getObjectsSingular(
						imports[i].id,
						krn.getAttributeByName(clsImport,"зап табл репликации").id, false
					);
					if (repl_row == null) continue;
					KrnObject replObj = krn.getObjectsSingular(
						repl_row.id,
						krn.getAttributeByName(clsReplRow,"replObject").id, false
					);
          if (replObj == null) continue;
          long r_id = krn.getLongsSingular(
						replObj,
						krn.getAttributeByName(clsReplCollection,"replicationID"),
						false
					);
					if (replicationID > -1 && replicationID != r_id) continue;
					long d = krn.getLongsSingular(imports[i],
						krn.getAttributeByName(clsImport,"дата репликации"),false) * 1000L;
					Database fromDb = new Database(
						krn.getObjectsSingular(imports[i].id,
							krn.getAttributeByName(clsImport,"from_database").id,false)
					);
          long exp_id = krn.getLongsSingular(
						imports[i],
						krn.getAttributeByName(clsImport,"exp_id"),
						false
					);
          long exp_date = 0;
          try {
            exp_date = krn.getLongsSingular(
              imports[i],
              krn.getAttributeByName(clsImport,"exp_date"),
              false
            );
          } catch (Exception e) {e.printStackTrace();}
					String file_name = krn.getStringsSingular(
						imports[i].id,
						krn.getAttributeByName(clsImport,"file_name").id, 0, false, false
					);
					String[] row = new String[columnNames.length];
					row[0] = String.valueOf(id);
					row[1] = String.valueOf(r_id);
					row[2] = formatter_date.format(new java.util.Date(d));
					row[3] = fromDb.toString();
          row[4] = String.valueOf(exp_id);
          row[5] = formatter_date.format(new java.util.Date(exp_date));
					row[6] = file_name;
					model.addRow(row);
				}
				TableSorter ts = (TableSorter) getModel();
				ts.sortByColumn(2);
        initColumnSizes();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		public ImportsTable() {
			columnNames = new String [] {
				"у/н имп", "у/н репл", "дата/время", "от базы",
			  "идент эксп от базы-источника", "дата эксп", "файл"
			};
			model = new DefaultTableModel(columnNames, 0);
			TableSorter ts = new TableSorter(model);
			ts.addMouseListenerToHeaderInTable(this);
			setModel(ts);
		}
	}
	class ExportsTable extends MainTable {
		public ExportsTable () {
			columnNames = new String[] {
				"у/н эксп", "у/н репл", "дата/время", "для базы", "файл"
			};
			model = new DefaultTableModel(columnNames, 0);
			TableSorter ts = new TableSorter(model);
			ts.addMouseListenerToHeaderInTable(this);
			setModel(ts);
		}
		protected void load(long replicationID) {
			try {
				for (int i = model.getRowCount()-1; i >= 0; i--)
					model.removeRow(i);
				KrnClass clsReplCollection = krn.getClassByName("ReplCollection");
				KrnClass clsReplRow = krn.getClassByName("Зап табл репликации");
				KrnObject curDb = krn.getCurrentDb();
				KrnObject[] exports = krn.getObjects(curDb,"exports",0);
				for (int i = 0; i < exports.length; i++) {
					long id = exports[i].id;
					System.out.println("exports[i].id = " + id);
					KrnObject repl_row = krn.getObjectsSingular(
						exports[i].id,
						krn.getAttributeByName(clsExport,"зап табл репликации").id, false
					);
					if (repl_row == null) continue;
					System.out.println("repl_row = " + repl_row.id);
					KrnObject replObj = krn.getObjectsSingular(
						repl_row.id,
						krn.getAttributeByName(clsReplRow,"replObject").id, false
					);
					if (replObj == null) continue;
					System.out.println("replObj = " + replObj.id);
          long r_id = krn.getLongsSingular(
						replObj,
						krn.getAttributeByName(clsReplCollection,"replicationID"),
						false
					);
					if (replicationID > -1 && replicationID != r_id) continue;
					long d = krn.getLongsSingular(exports[i],
						krn.getAttributeByName(clsExport,"дата репликации"),false) * 1000L;
					Database fromDb = new Database(
						krn.getObjectsSingular(exports[i].id,
							krn.getAttributeByName(clsExport,"to_database").id,false)
					);
					long exp_id = krn.getLongsSingular(
						exports[i],
						krn.getAttributeByName(clsExport,"exp_id"),
						false
					);
					String file_name = krn.getStringsSingular(
						exports[i].id,
						krn.getAttributeByName(clsImport,"file_name").id, 0, false, false
					);
					String[] row = new String[columnNames.length];
					row[0] = String.valueOf(id);
					row[1] = String.valueOf(r_id);
					row[2] = formatter_date.format(new java.util.Date(d));
					row[3] = fromDb.toString();
					row[4] = file_name;
					model.addRow(row);
				}
				TableSorter ts = (TableSorter) getModel();
				ts.sortByColumn(2);
        initColumnSizes();
			} catch(Exception e) {
				e.printStackTrace();
				new KrnException(0, e.getMessage());
			}
		}
	}
	public ReplicatorLogs() {
		try  {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showLogs(LogNode node) {
    if (node == null) return;
		if (node.type == LOG_SUCCESS_REPLICATIONS) {
			mainSpl.setRightComponent(imp_exp_Pane);
			imports_table.load(node.replicationID);
			exports_table.load(node.replicationID);
		}
		else {
			mainSpl.setRightComponent(new JScrollPane(others_Pane));
			general_table.load(node.type, node.replicationID);
		}
	}
	private class LogNode extends DefaultMutableTreeNode{
		private boolean is_cur_repl = false;
		private boolean loaded = false;
		public int type;
    private long replicationID = -1;
    private long date;
    private long runMode;
    private long replType;
		public LogNode (int type) {
			this.type = type;
		}
		public LogNode (int type, long replicationID) {
			this.type = type;
			this.replicationID = replicationID;
			this.is_cur_repl = true;
		}
		public LogNode (int type, KrnObject replObj) throws KrnException{
			this.type = type;
      replicationID = krn.getLongsSingular(replObj,
				krn.getAttributeByName(clsReplCollection,"replicationID"),false);
      date = krn.getLongsSingular(replObj,
				krn.getAttributeByName(clsReplCollection,"date"),false) * 1000L;
      try {
        KrnObject firstRecord = krn.getObjectsSingular(replObj.id,
          krn.getAttributeByName(clsReplCollection,"зап табл репликации").id,false);
        if (firstRecord != null) {
          KrnClass recCls = krn.getClassByName("Зап табл репликации");
          KrnAttribute recAttrStatus = krn.getAttributeByName(recCls, "status");
          withErrorOrWarningStatus = krn.getLongsSingular(firstRecord,recAttrStatus,false);
        }
      } catch (Exception e) {e.printStackTrace();}
      try {
        runMode = krn.getLongsSingular(replObj,
          krn.getAttributeByName(clsReplCollection,"runMode"),false);
      } catch (Exception e) {e.printStackTrace();}
      try {
        replType = krn.getLongsSingular(replObj,
          krn.getAttributeByName(clsReplCollection,"type"),false);
      } catch (Exception e) {e.printStackTrace();}
			add(new LogNode(LOG_COLL_ITEM_EMPTY));
		}
		private void setText (int type) {
			this.type = type;
		}
		private void loadItem() {
			add(new LogNode(LOG_IMPORT,replicationID));
			add(new LogNode(LOG_EXPORT,replicationID));
			add(new LogNode(LOG_SUCCESS_REPLICATIONS,replicationID));
			loaded = true;
		}
    private long withErrorOrWarningStatus = STATUS_NONE;
		public String toString() {
      String text = "";
			switch (type) {
				case LOG_ROOT:
					text = "Журнал репликаций"; break;
				case LOG_COLL:
					text = "Полный хронологический журнал репликаций"; break;
				case LOG_COLL_ITEM:
          String srunMode = ""; String sreplType = "";
          if (replType == Kernel.et_SECOND_EXPORT)
            sreplType = "повторный";
          if (runMode == Kernel.rm_AUTO)
            srunMode = "автозапуск";
          else
          if (runMode == Kernel.rm_MANUAL)
            srunMode = "ручной запуск";
          else {
            KrnObject obj = null;
            try {
              obj = new KrnObject(runMode,"",krn.getClassByName("Структура баз").id);
            } catch (Exception e) {e.printStackTrace();}
            Database db = new Database(obj);
            srunMode = "запуск для базы "+db;
          }
					text = "["+String.valueOf(replicationID)+"] "+
						formatter_date.format(new java.util.Date(date))+" "+srunMode+" "+sreplType;
          break;
				case LOG_SUCCESS_REPLICATIONS:
					text = "Журнал успешно завершенных репликаций";
					if (is_cur_repl)
						text = "Успешно завершенные репликации";
					break;
				case LOG_NEGATIVE_EVENTS:
					text = "Сведения о"; break;
				case LOG_IMPORT:
					text = "Незагруженных файлов импорта";
					if (is_cur_repl)
						text = "Процессы импорта";
					break;
				case LOG_EXPORT:
					text = "Безуспешно завершенных процессах экспорта";
					if (is_cur_repl)
						text = "Процессы экспорта";
					break;
        case LOG_CONFIRM:
					text = "Неполученных подтверждений";
//					if (is_cur_repl)
//						text = "Неполученные подтверждения";
					break;
        case LOG_CHECKDB:
          if (is_cur_repl)
  					text = "Проблемы в базе";
          else
            text = "Результат последней проверки базы";
					break;
			}
			return text;
		}
	}
	JSplitPane mainSpl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	JSplitPane imp_exp_Pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	JPanel others_Pane = new JPanel();
	KrnClass clsReplCollection;
	KrnClass clsImport;
	KrnClass clsExport;
  LogNode repl_collection = new LogNode(LOG_COLL);

	private void init() throws KrnException {
		formatter_date = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
		this.setTitle("Журнал репликаций");
		this.setSize(600, 500);
    this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width -
			getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height -
			getSize().height) / 2);
		contentPane = new JPanel(new BorderLayout());
		getContentPane().add(contentPane);
		tree = new JTree();
		DefaultMutableTreeNode n = new LogNode(LOG_ROOT);
		LogNode negative_events = new LogNode(LOG_NEGATIVE_EVENTS);
		n.add(negative_events);
		negative_events.add(new LogNode(LOG_IMPORT));
		negative_events.add(new LogNode(LOG_EXPORT));
    negative_events.add(new LogNode(LOG_CONFIRM));
    negative_events.add(new LogNode(LOG_CHECKDB));
		n.add(new LogNode(LOG_SUCCESS_REPLICATIONS));
		n.add(repl_collection);
		TreeModel model = new DefaultTreeModel(n);
		tree.setModel(model);

		tree.addTreeSelectionListener (new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent e)
      {
        showLogs ((LogNode) tree.getLastSelectedPathComponent());
      }
    });
		tree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			public void treeWillCollapse(TreeExpansionEvent event)
				throws ExpandVetoException {
			}
			public void treeWillExpand(TreeExpansionEvent event)
				throws ExpandVetoException {
				TreePath n = event.getPath();
				LogNode node = (LogNode) n.getPathComponent(n.getPathCount()-1);
				if (node.type == LOG_COLL_ITEM)
					if (!node.loaded) {
						node.removeAllChildren();
            if (node.withErrorOrWarningStatus == STATUS_ERROR)
              node.add(new LogNode(LOG_CHECKDB, node.replicationID));
            else {
              if (node.withErrorOrWarningStatus == STATUS_WARNING)
                node.add(new LogNode(LOG_CHECKDB, node.replicationID));
              node.loadItem();
            }
					}
			}
		});

    JPanel bottomPane = new JPanel();
    bottomPane.setPreferredSize(new Dimension(100,40));
    JButton btnClear = new JButton("Очистить журнал 'Сведения о'");
    JButton btnRefresh = new JButton("Обновить");
    btnClear.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try
        {
          KrnObject[] logs = krn.getClassObjects(krn.getClassByName("ReplCurrentStatus"), 0);
          for (int i = 0; i < logs.length; i++) {
            krn.deleteObject(logs[i], 0);
          }
          refreshTotalInfo();
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    btnRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          getReplCollection();
        } catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    JPanel bPane = new JPanel();
    bPane.setLayout(new GridLayout(1, 4));
    bPane.add(btnRefresh);
    bPane.add(btnClear);
    bPane.setSize(200, 40);
    bottomPane.add(bPane, BorderLayout.WEST);
    contentPane.add(bottomPane, BorderLayout.SOUTH);
    contentPane.add(mainSpl, BorderLayout.CENTER);
    imp_exp_Pane.setPreferredSize(new Dimension(200, 200));
    tree.setAutoscrolls(true);
//    tree.setPreferredSize(new Dimension(300,300));
    JScrollPane scr = new JScrollPane();

/*    treePane.setLayout(new BorderLayout());
		treePane.setPreferredSize(new Dimension(300,300));
    treePane.add(tree, BorderLayout.CENTER);     */

    JScrollPane s = new JScrollPane(tree);
    s.setPreferredSize(new Dimension(300,300));
		mainSpl.setLeftComponent(s);
		mainSpl.setRightComponent(imp_exp_Pane);

		krn = Kernel.instance();

		clsReplCollection = krn.getClassByName("ReplCollection");
		clsImport = krn.getClassByName("Import");
		clsExport = krn.getClassByName("Export");

		// Colletion of replications
    getReplCollection();

		// imports
		imports_table = new ImportsTable();
		exports_table = new ExportsTable();

		JPanel import_caption = new JPanel();
		import_caption.setPreferredSize(new Dimension(100,20));
		import_caption.setLayout(new BorderLayout());
		import_caption.add(new Label("Импорты"),BorderLayout.WEST);

		importsPane.setLayout(new BorderLayout());
		importsPane.setSize(new Dimension(200,200));
		importsPane.add(import_caption,BorderLayout.NORTH);
		importsPane.add(new JScrollPane(imports_table),BorderLayout.CENTER);

		// exports
		JPanel export_caption = new JPanel();
    export_caption.setPreferredSize(new Dimension(100,20));
		export_caption.setLayout(new BorderLayout());
		export_caption.add(new Label("Экспорты"),BorderLayout.CENTER);
		exportsPane.setLayout(new BorderLayout());
		exportsPane.add(export_caption,BorderLayout.NORTH);
		exportsPane.add(new JScrollPane(exports_table),BorderLayout.CENTER);

    importsPane.setPreferredSize(new Dimension(100, 200));
    exportsPane.setPreferredSize(new Dimension(100, 200));
		imp_exp_Pane.setTopComponent(new JScrollPane(importsPane));
		imp_exp_Pane.setBottomComponent(new JScrollPane(exportsPane));

		general_table = new GeneralTable();
		others_Pane.setLayout(new BorderLayout());
		others_Pane.add(new JScrollPane(general_table),BorderLayout.CENTER);
	}
  private void refreshTotalInfo() {
    TreePath p = tree.getSelectionPath();
    if (p != null) {
      LogNode n = (LogNode) p.getPathComponent(p.getPathCount()-1);
      if ((n.type == LOG_IMPORT || n.type == LOG_EXPORT || n.type == LOG_CONFIRM)
        && n.replicationID == -1)
        general_table.load(n.type, n.replicationID);
    }
  }
  private void getReplCollection() throws KrnException{
    KrnClass clsReplCollection = krn.getClassByName("ReplCollection");
		KrnObject[] replColletion = krn.getClassObjects(clsReplCollection, 0);
    repl_collection.removeAllChildren();
		for (int i = 0; i < replColletion.length; i++) {
			repl_collection.add(new LogNode(LOG_COLL_ITEM,replColletion[i]));
		}
    refreshTotalInfo();
  }
	private class Database
  {
    KrnObject obj;
    private String title_;
		private String getUId()throws KrnException{
      return obj.uid;
		}
    public Database (KrnObject o)
    {
      obj = o;
    }
    public String toString()
    {
      String res = "";
      try
      {
				KrnAttribute attr = krn.getAttributeByName(
					krn.getClassByName("Структура баз"),"значение");
				System.out.println("attr = " + attr);
				System.out.println("obj = " + obj);
				KrnObject o = krn.getObjectsSingular(obj.id, attr.id, false);
				attr = krn.getAttributeByName(krn.getClassByName("База"),"наименование");
				String s = krn.getStringsSingular(o.id, attr.id, 0, false, false);
        res = "["+String.valueOf(obj.id)+"] "+s;
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return res;
    }
  }
}