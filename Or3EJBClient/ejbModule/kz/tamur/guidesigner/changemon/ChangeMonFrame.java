package kz.tamur.guidesigner.changemon;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Constants.INSETS_2;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.StringValue;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.Funcs;
import kz.tamur.util.OpenElementPanel;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 31.01.2009
 * Time: 13:37:56
 */
public class ChangeMonFrame extends JPanel implements ActionListener, DocumentListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private static JTable changeTable;
	private ChangeMonTableModel model;
	private TableRowSorter<ChangeMonTableModel> rowSorter;
	private static JButton commitdBtn = createToolButton("SaveIcon", "Подтвердить изменения");
	private static JButton unDoBtn = createToolButton("unDo", "Отменить изменения");
	private JButton refreshBtn = createToolButton("BoxNode", "Обновить");
	private JButton srchBtn = ButtonsFactory.createToolButton("SearchIcon", ".png", "Поиск");
	private JButton backBtn = ButtonsFactory.createToolButton("DeleteValue", ".png", "Скрыть результат поиска");
	protected JPopupMenu pm = new JPopupMenu();
	protected JMenuItem resetUser = createMenuItem("Переназначить пользователя", "userNodeSel");
	protected JMenuItem unDoRec = createMenuItem("Отменить изменения", "unDo");
	protected JMenuItem commitRec = createMenuItem("Подтвердить изменения", "checkOk");
	protected JMenuItem checkUnCheck = createMenuItem("Пометить/снять пометку", "CheckBox");
	private UserTree userTree= null;
	private OpenElementPanel userOp=null;
	private static String pattern = "dd.MM.yyyy HH:mm:ss";

	private JPanel dlgPanel = new JPanel();
	private JTextArea messageArea = new JTextArea();
	private JTextArea noteLabel = new JTextArea();
	private String noteFix = "Изменения, сделанные в объектах проектирования \n будут переданы в репликационный файл";
	private String noteUnDo = "Изменения, сделанные в объектах проектирования,\n будут отменены";
	private JCheckBox isFixdCheckBox = new JCheckBox("Подтвержденные?");
	private JCheckBox isImportCheckBox=new JCheckBox("Импортированные?");
	private JCheckBox isExportCheckBox=new JCheckBox("Экспортированные?");
	private JCheckBox isUserCheckBox = new JCheckBox("Тек.пользователь?");
	private JCheckBox checkAllBox = new JCheckBox("Подтвердить все?");
	private JLabel replSel = new JLabel("Репликация");
	private DefaultComboBoxModel<FilterObject> importBoxModel=new DefaultComboBoxModel<FilterObject>(new Vector<FilterObject>());
	private DefaultComboBoxModel<FilterObject> exportBoxModel=new DefaultComboBoxModel<FilterObject>(new Vector<FilterObject>());
	private DefaultComboBoxModel<FilterObject> nullBoxModel=new DefaultComboBoxModel<FilterObject>(new Vector<FilterObject>());
	private JComboBox<FilterObject> replBox = new JComboBox<FilterObject>();

	private JLabel userSel = new JLabel("Пользователь");
	private Vector<FilterObject> userList = new Vector<FilterObject>();
	//Поиск в истории изменений по ключевому слову
	private Map<String, List<Object>> searchingInfo = new HashMap<String, List<Object>>();
	private Map<String, List<Integer>> editedRows = new HashMap<String, List<Integer>>();
	private String iconTreeNode;
	//
	private DefaultComboBoxModel<FilterObject> userBoxModel = new DefaultComboBoxModel<FilterObject>(userList){
		@Override public void addElement(FilterObject obj){
			int count = getSize();
			FilterObject toAdd = (FilterObject) obj;

			List<FilterObject> items = new ArrayList<FilterObject>();
			for(int i = 0; i < count; i++){
				items.add((FilterObject)getElementAt(i));
			}

			if(items.size() == 0){
				super.addElement(toAdd);
				return;
			}else{
				if(toAdd.title.compareTo(items.get(0).title) <= 0){
					insertElementAt(toAdd, 0);
				}else{
					int lastIndexOfHigherNum = 0;
					for(int i = 0; i < count; i++){
						if(toAdd.title.compareTo(items.get(i).title) > 0){
							lastIndexOfHigherNum = i;
						}
					}
					insertElementAt(toAdd, lastIndexOfHigherNum+1);
				}
			}
		}
	};
	private JComboBox<FilterObject> userBox = new JComboBox<FilterObject>(userBoxModel);

	private JList changesList = new JList();
	private DefaultListModel listModel = new DefaultListModel();

	private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
	private boolean canCommitModification;
	private static MainFrame.DescLabel counterLabel = kz.tamur.comps.Utils.createDescLabel("");
	private static int rowCount = 0;
	private static int selRowIdx = 0;
	private static int selRowCount = 0;
	private Map<Long, String> filters = new HashMap<Long, String>();
	private Map<Long, String> reports = new HashMap<Long, String>();
	private Map<Long, String> uis = new HashMap<Long, String>();
	private Map<Long, String> process = new HashMap<Long, String>();
	private Map<Long, String> users = new HashMap<Long, String>();
	private Map<Long, String> repls = new HashMap<Long, String>();
	private Map<Long,Long> impExpIds=new HashMap<Long,Long>();

	private static List<KrnVcsChange> workeChanges;
	private static List<KrnVcsChange> changes=null;
	private static List<KrnVcsChange> tmpChanges=new ArrayList<KrnVcsChange>();

	private static User user;
	private DesignerDialog dlg;
	private ChangeMonHistoryPanel changeHistory = new ChangeMonHistoryPanel();
	private long selUserId = -1;
	private long selReplId = -1;
	private static Kernel krn = Kernel.instance();

	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

	private KrnClass clsUI;
	private KrnClass clsProcessDef;
	private KrnClass clsFilter;
	private KrnClass clsReportPrinter;

	private JToolBar ttoolBar = kz.tamur.comps.Utils.createDesignerToolBar();
	private JToggleButton methodBtn = new JToggleButton("Методы");
	private JToggleButton serviceBtn = new JToggleButton("Процессы");
	private JToggleButton ifcBtn = new JToggleButton("Интерфейсы");
	private JToggleButton filterBtn = new JToggleButton("Фильтры");
	private JToggleButton reportBtn = new JToggleButton("Отчеты");
	private JToggleButton boxBtn = new JToggleButton("Пункты обмена");
	private JToggleButton hmBtn = new JToggleButton("Гиперменю");
	private JToggleButton triggerBtn = new JToggleButton("Триггеры");
	private JToggleButton classBtn = new JToggleButton("Классы");
	private JToggleButton attrBtn = new JToggleButton("Атрибуты");

	private JPanel datePanel = new JPanel();
	private DateField begDate = new DateField();
	private DateField endDate = new DateField();
	private CalendarButton calBtn1,calBtn2;
	private static boolean isSysDb = Or3Frame.isSysDb();
	private boolean isReplBoxSet=false;
	private JPanel commentPanel = new JPanel();	
	private JLabel commentLabel = new JLabel("По комментарию подтверждения: ");
	private JTextField comment = kz.tamur.rt.Utils.createDesignerTextField();
	private JButton commentBtn = new JButton("Найти");


	public ChangeMonFrame() {
		super();

		commentPanel.setLayout(new GridBagLayout());
		commentPanel.add(commentLabel, new GridBagConstraints(0, 0, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		commentPanel.add(comment, new GridBagConstraints(0, 1, 1, 1, 1, 0, CENTER, NONE, INSETS_2, 0, 0));
		commentPanel.add(commentBtn, new GridBagConstraints(1, 1, 1, 1, 1, 0, CENTER, NONE, INSETS_2, 0, 0));
		commentBtn.addActionListener(this);
		comment.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					doSearch();
				}
			}
		});
		begDate.setMinimumSize(new Dimension(70, 20));
		endDate.setMinimumSize(new Dimension(70, 20));
		datePanel.setLayout(new GridBagLayout());
		datePanel.add(new JLabel("Период изменений"), new GridBagConstraints(0, 0, 6, 1, 1, 0, CENTER, NONE, INSETS_2, 0, 0));
		datePanel.add(new JLabel("c:"), new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(begDate, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn1=new CalendarButton("c:");
		calBtn1.setDataField(begDate);
		datePanel.add(calBtn1, new GridBagConstraints(2, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(new JLabel("по:"), new GridBagConstraints(3, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(endDate, new GridBagConstraints(4, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn2=new CalendarButton("по:");
		calBtn2.setDataField(endDate);
		datePanel.add(calBtn2, new GridBagConstraints(5, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn1.addActionListener(this);
		begDate.addActionListener(this);
		endDate.addActionListener(this);

		isUserCheckBox.setSelected(true);
		checkAllBox.setSelected(false);
		commitdBtn.setEnabled(false);
		unDoBtn.setEnabled(false);
		checkAllBox.setEnabled(isSysDb);
		isFixdCheckBox.setEnabled(isSysDb);
		isImportCheckBox.setEnabled(true);
		isExportCheckBox.setEnabled(isSysDb);
		if(isSysDb) toolBar.add(commitdBtn);
		toolBar.add(refreshBtn);
		if(isSysDb) toolBar.add(unDoBtn);
		toolBar.add(srchBtn);
		toolBar.add(backBtn);
		if(isSysDb) {
			toolBar.add(checkAllBox);
			toolBar.addSeparator();
		}
		if(isSysDb) {
			toolBar.add(isFixdCheckBox);
			toolBar.addSeparator();
		}
		if(isSysDb) toolBar.add(isExportCheckBox);
		if (!isSysDb) toolBar.add(isImportCheckBox);

		toolBar.addSeparator();
		toolBar.add(replSel);
		toolBar.add(replBox);
		toolBar.addSeparator();
		toolBar.add(isUserCheckBox);
		toolBar.addSeparator();
		toolBar.add(userSel);
		toolBar.add(userBox);
		toolBar.addSeparator();

		toolBar.setBorder(null);
		toolBar.setMinimumSize(new Dimension(1400, 30));
		commitdBtn.addActionListener(this);
		refreshBtn.addActionListener(this);
		unDoBtn.addActionListener(this);
		srchBtn.addActionListener(this);
		backBtn.addActionListener(this);
		//
		ttoolBar.add(methodBtn);
		ttoolBar.add(serviceBtn);
		ttoolBar.add(ifcBtn);
		ttoolBar.add(filterBtn);
		ttoolBar.add(reportBtn);
		ttoolBar.add(boxBtn);
		ttoolBar.add(hmBtn);
		ttoolBar.add(triggerBtn);
		ttoolBar.add(attrBtn);
		ttoolBar.add(classBtn);
		ttoolBar.addSeparator();
		ttoolBar.add(datePanel);
		ttoolBar.addSeparator();
		ttoolBar.add(commentPanel);
		ttoolBar.addSeparator();
		ttoolBar.setBorder(null);
		ttoolBar.setMinimumSize(new Dimension(1350,30));
		//
		messageArea.getDocument().addDocumentListener(this);
		isFixdCheckBox.addActionListener(this);
		isImportCheckBox.addActionListener(this);
		isExportCheckBox.addActionListener(this);
		isUserCheckBox.addActionListener(this);
		checkAllBox.addActionListener(this);
		userBox.addActionListener(this);
		replBox.addActionListener(this);

		methodBtn.addActionListener(this);
		serviceBtn.addActionListener(this);
		ifcBtn.addActionListener(this);
		filterBtn.addActionListener(this);
		reportBtn.addActionListener(this);
		boxBtn.addActionListener(this);
		hmBtn.addActionListener(this);
		triggerBtn.addActionListener(this);
		attrBtn.addActionListener(this);
		classBtn.addActionListener(this);

		user = krn.getUser();
		canCommitModification = user.hasRight(Or3RightsNode.VCS_CHANGE_VIEW_RIGHT);
		setLayout(new GridBagLayout());

		JLabel l = new JLabel("Контроль версионности изменений:");
		userBoxModel.addElement(new FilterObject("", -1));
		importBoxModel.addElement(new FilterObject("",-1));
		exportBoxModel.addElement(new FilterObject("",-1));

		loadChanges();
		userBox.setSelectedIndex(0);
		//replBox.setSelectedIndex(0);
		userBox.setPreferredSize(new Dimension(100, 20));
		replBox.setPreferredSize(new Dimension(200, 20));
		model = new ChangeMonTableModel(changes);
		changeTable = new JTable(model){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChanged(ListSelectionEvent e) {
				super.valueChanged(e);
				setCounterText();
			}

		};
		changeTable.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_C) {
					int row = changeTable.getSelectedRow();
					if (row != -1) {
						StringBuilder copyText = new StringBuilder();
						for (int i = 0; i < model.getColumnCount(); i++) {
							copyText.append(model.getValueAt(row, i));
							if (i < model.getColumnCount() - 1) {
								copyText.append("\t");
							}
						}
						StringSelection stringSelection = new StringSelection(copyText.toString());
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
					}
				}
			}
		});
		try {
			clsUI = krn.getClassByName("UI");
			clsProcessDef = krn.getClassByName("ProcessDef");
			clsFilter = krn.getClassByName("Filter");
			clsReportPrinter = krn.getClassByName("ReportPrinter");
		} catch (KrnException e) {
			e.printStackTrace();
		}

		rowSorter = new TableRowSorter<ChangeMonTableModel>(model);

		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(isSysDb ? 2 : 1, SortOrder.DESCENDING));
		rowSorter.setSortKeys(sortKeys); 
		changeTable.setRowSorter(rowSorter);

		changeTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int row = changeTable.rowAtPoint(e.getPoint());
					if (row != -1) {
						KrnVcsChange change = model.changes.get(rowSorter.convertRowIndexToModel(row));
						KrnObject cvsChangeObj = change.cvsChangeObj;
						if (change.cvsChangeObj != null) {
							if (clsUI != null && cvsChangeObj.classId == clsUI.id) {
								Or3Frame.instance().jumpInterface(cvsChangeObj);
							} else if (clsProcessDef != null && cvsChangeObj.classId == clsProcessDef.id) {
								Or3Frame.instance().jumpService(cvsChangeObj);
							} else if (clsFilter != null && cvsChangeObj.classId == clsFilter.id) {
								Or3Frame.instance().jumpFilter(cvsChangeObj);
							} else if (clsReportPrinter != null && cvsChangeObj.classId == clsReportPrinter.id) {
								Or3Frame.instance().jumpReport(cvsChangeObj);
							}
						} else if (change.cvsChangeMethod != null) {
							KrnMethod cvsChangeMethod = change.cvsChangeMethod;
							if (cvsChangeMethod != null && cvsChangeMethod.classId > 0) {
								Or3Frame.instance().jumpMethod(cvsChangeMethod);
							}	
							else {
								MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Метод удален");
							}

						} else if (change.cvsChangeClass != null) {
							KrnClass cvsChangeClass = change.cvsChangeClass;
							if(change.typeId==0) {
								Or3Frame.instance().jumpClassProperty(cvsChangeClass);
							}else {
								int triggerType;
								if (change.typeId == 4) {
									triggerType = 0;
								} else if (change.typeId == 5) {
									triggerType = 1;
								} else if (change.typeId == 6) {
									triggerType = 2;
								} else {
									triggerType = 3;
								}
								if (cvsChangeClass != null) {
									Or3Frame.instance().jumpTrigger(cvsChangeClass.uid, 0, triggerType);
								}
							}
						} else if (change.cvsChangeAttr != null) {
							KrnAttribute cvsChangeAttr = change.cvsChangeAttr;
							if(change.typeId==1) {
								Or3Frame.instance().jumpAttrProperty(cvsChangeAttr);
							}else {
								int triggerType;
								if (change.typeId == 8) {
									triggerType = 0;
								} else if (change.typeId == 9) {
									triggerType = 1;
								} else if (change.typeId == 10) {
									triggerType = 2;
								} else {
									triggerType = 3;
								}
								if (cvsChangeAttr != null) {
									Or3Frame.instance().jumpTrigger(cvsChangeAttr.uid, 1, triggerType);
								}
							}
						}
					}
					getTopLevelAncestor().setCursor(Cursor.getDefaultCursor());
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() && !(isFixdCheckBox.isSelected()
						||isImportCheckBox.isSelected()
						||isExportCheckBox.isSelected()
						)) {
					int row = changeTable.rowAtPoint(e.getPoint());
					if(row>=0){
						changeTable.setRowSelectionInterval(row, row);
						pm.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		changeTable.setPreferredScrollableViewportSize(changeTable.getPreferredSize());

		final SimpleDateFormat fmt = new SimpleDateFormat(pattern);
		DefaultTableCellRenderer dataCellRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (value instanceof Date) {
					value = fmt.format(value);
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		changeTable.setDefaultRenderer( Object.class,  new FormalDataCellRenderer(changeTable.getDefaultRenderer(Object.class)));
		changeTable.setDefaultEditor(Object.class, new FormalDataCellEditor(new JCheckBox()));
		changeTable.getColumnModel().getColumn(2).setCellRenderer(dataCellRenderer);
		changeTable.getColumnModel().getColumn(0).setMinWidth(60);
		changeTable.getColumnModel().getColumn(0).setMaxWidth(60);
		changeTable.getColumnModel().getColumn(2).setMinWidth(120);
		changeTable.getColumnModel().getColumn(2).setMaxWidth(120);
		changeTable.getColumnModel().getColumn(3).setMinWidth(90);
		changeTable.getColumnModel().getColumn(3).setMaxWidth(100);
		changeTable.getColumnModel().getColumn(4).setMinWidth(100);
		changeTable.getColumnModel().getColumn(4).setMaxWidth(100);
		changeTable.getColumnModel().getColumn(5).setMinWidth(100);
		changeTable.getColumnModel().getColumn(5).setMaxWidth(100);
		ListSelectionModel selectionModel = changeTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(this);
		JTableHeader header = changeTable.getTableHeader();
		header.setUpdateTableInRealTime(true);
		header.setReorderingAllowed(false);
		header.setResizingAllowed(true);
		header.setDefaultRenderer(new DefaultTableHeaderCellRenderer());
		JPanel tablePain = new JPanel();
		tablePain.setLayout(new BorderLayout(3, 3));
		JScrollPane sp = new JScrollPane(changeTable);
		sp.setOpaque(isOpaque);
		sp.getViewport().setOpaque(isOpaque);
		sp.setMinimumSize(new Dimension(600, 200));
		tablePain.add(sp);
		noteLabel.setPreferredSize(new Dimension(400, 40));
		noteLabel.setEditable(false);
		noteLabel.setBackground(Utils.getMainColor());
		noteLabel.setText(noteFix);
		messageArea.setPreferredSize(new Dimension(400, 100));
		messageArea.setLineWrap(true);
		messageArea.setBorder(new LineBorder(Color.black, 1, true));
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new GridBagLayout());
		listPanel.add(ttoolBar, new CnrBuilder().x(0).y(0).anchor(WEST).ins(5, 5, 0, 5).build());
		listPanel.add(l, new CnrBuilder().x(0).y(1).anchor(CENTER).ins(5, 5, 0, 5).build());
		listPanel.add(counterLabel, new CnrBuilder().x(2).y(1).anchor(EAST).ins(5, 5, 0, 5).build());
		listPanel.add(tablePain, new CnrBuilder().x(0).y(2).fill(BOTH).wtx(1).wty(1).build());
		listPanel.add(messageArea, new CnrBuilder().x(0).y(3).fill(BOTH).ins(5, 5, 0, 5).build());

		JSplitPane splPanel=new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel, changeHistory);
		splPanel.setDividerLocation(1.0);
		add(toolBar, new CnrBuilder().x(0).y(0).anchor(WEST).build());
		add(splPanel, new CnrBuilder().x(0).y(1).w(1).fill(BOTH).anchor(WEST).wtx(1).wty(1).ins(5, 5, 0, 5).build());

		dlgPanel.setMinimumSize(new Dimension(400, 200));
		dlgPanel.setLayout(new GridBagLayout());
		dlgPanel.add(messageArea, new CnrBuilder().x(0).y(0).fill(BOTH).ins(5, 5, 0, 5).build());
		dlgPanel.add(noteLabel, new CnrBuilder().x(0).y(1).fill(BOTH).ins(5, 5, 0, 5).build());

		JScrollPane chlp = new JScrollPane(changesList);
		dlgPanel.add(chlp, new CnrBuilder().x(0).y(2).anchor(CENTER).ins(5, 5, 0, 5).build());
		changesList.setCellRenderer(new ImageListCellRenderer());
		changesList.setModel(listModel);
		commitdBtn.setVisible(canCommitModification);
		messageArea.setVisible(canCommitModification);
		setOpaque(isOpaque);
		// Установка прозрачности, зависящей от глобальных настроек системы
		listPanel.setOpaque(isOpaque);
		tablePain.setOpaque(isOpaque);
		toolBar.setOpaque(isOpaque);
		initPopup();
		setCounterText();
		backBtn.setEnabled(false);
		if(!isSysDb) changeTable.removeColumn(changeTable.getColumnModel().getColumn(0));

		if(isFixdCheckBox.isSelected()) {
			comment.setEnabled(true);
			commentBtn.setEnabled(true);        	
		} else {
			comment.setEnabled(false);
			commentBtn.setEnabled(false);
		}
	}

	private void initPopup() {
		pm.add(resetUser);
		resetUser.addActionListener(this);
		pm.addSeparator();
		pm.add(unDoRec);
		unDoRec.addActionListener(this);
		pm.add(commitRec);
		commitRec.addActionListener(this);
		pm.add(checkUnCheck);
		checkUnCheck.addActionListener(this);
	}
	private void loadChanges(){
		try {
			KrnObject langObj=krn.getInterfaceLanguage();
			workeChanges = krn.getVcsChanges(isImportCheckBox.isSelected()
					?Constants.VCS_IMPORT:isExportCheckBox.isSelected()
							?Constants.VCS_EXPORT:isFixdCheckBox.isSelected()
									?Constants.VCS_FIXD:isSysDb?Constants.VCS_NOT_FIXD:-1,-1,isUserCheckBox.isSelected()
											?krn.getUser().object.id:selUserId,isImportCheckBox.isSelected() || isExportCheckBox.isSelected()?selReplId:-1);
			/*workeChanges = krn.getVcsChanges(isFixdCheckBox.isSelected()?Constants.VCS_FIXD:isSysDb?Constants.VCS_NOT_FIXD:-1
            			,isImportCheckBox.isSelected()?Constants.VCS_IMPORT:isExportCheckBox.isSelected()?Constants.VCS_EXPORT:-1
                        ,isUserCheckBox.isSelected()?krn.getUser().object.id:selUserId,isImportCheckBox.isSelected() || isExportCheckBox.isSelected()?selReplId:-1);*/
			List<Long> users_=krn.getVcsGroupObjects(Kernel.SC_USER.id);
			List<Long> imps_=krn.getVcsGroupObjects(Kernel.SC_IMPORT.id);
			List<Long> exps_=krn.getVcsGroupObjects(Kernel.SC_EXPORT.id);
			changes=workeChanges;
			if(changes!=null){
				for(KrnVcsChange change:changes){
					if(change.cvsChangeObj!=null){
						if(Kernel.SC_PROCESS_DEF.id==change.cvsChangeObj.classId){
							process.put(change.cvsChangeObj.id, "");
						}else if(Kernel.SC_FILTER.id==change.cvsChangeObj.classId){
							filters.put(change.cvsChangeObj.id, "");
						}else if(Kernel.SC_REPORT_PRINTER.id==change.cvsChangeObj.classId){
							reports.put(change.cvsChangeObj.id, "");
						}else if(Kernel.SC_UI.id==change.cvsChangeObj.classId){
							uis.put(change.cvsChangeObj.id, "");
						}
					}
				}
				KrnAttribute attr=krn.getAttributeByName(Kernel.SC_PROCESS_DEF, "title");
				long[] objIds=Funcs.makeLongArray(process.keySet());
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, langObj.id, false, 0);
					for(StringValue title:titles){
						if(title.index==0)
							process.put(title.objectId, title.value);
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_UI, "title");
				objIds=Funcs.makeLongArray(uis.keySet());
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, langObj.id, false, 0);
					for(StringValue title:titles){
						if(title.index==0)
							uis.put(title.objectId, title.value);
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_FILTER, "title");
				objIds=Funcs.makeLongArray(filters.keySet());
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, langObj.id, false, 0);
					for(StringValue title:titles){
						if(title.index==0)
							filters.put(title.objectId, title.value);
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_REPORT_PRINTER, "title");
				objIds=Funcs.makeLongArray(reports.keySet());
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, langObj.id, false, 0);
					for(StringValue title:titles){
						if(title.index==0)
							reports.put(title.objectId, title.value);
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_USER, "name");
				objIds=Funcs.makeLongArray(users_);
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, 0, false, 0);
					for(StringValue title:titles){
						if(title.index==0)
							users.put(title.objectId, title.value);
						FilterObject fobj=new FilterObject(title.value, title.objectId);
						if(userBoxModel.getIndexOf(fobj)<0)
							userBoxModel.addElement(fobj);
						else
							fobj=null;
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_IMPORT, "file_name");
				objIds=Funcs.makeLongArray(imps_);
				if(objIds!=null && objIds.length>0){
					KrnAttribute attr_exp=krn.getAttributeByName(Kernel.SC_IMPORT, "exp_id");
					LongValue[] expIds=krn.getLongValues(objIds, attr_exp, 0);
					if(expIds.length>0){
						for(LongValue exp_id_:expIds){
							if(exp_id_.index==0){
								impExpIds.put(exp_id_.objectId, exp_id_.value);
							}
						}
					}
					StringValue[] titles=krn.getStringValues(objIds, attr, 0, false, 0);
					if(titles.length>0){
						Arrays.sort(titles);
						for(StringValue title:titles){
							if(title.index==0){
								repls.put(title.objectId, title.value);
								FilterObject fobj=new FilterObject(title.value, title.objectId);
								if(importBoxModel.getIndexOf(fobj)<0) 
									importBoxModel.addElement(fobj);
								else
									fobj=null;
							}
						}
					}else{
						Arrays.sort(objIds);
						for(long objId:objIds){
							repls.put(objId, ""+objId);
							FilterObject fobj=new FilterObject(""+objId, objId);
							if(importBoxModel.getIndexOf(fobj)<0) 
								importBoxModel.addElement(fobj);
							else
								fobj=null;
						}
					}
				}
				attr=krn.getAttributeByName(Kernel.SC_EXPORT, "file_name");
				objIds=Funcs.makeLongArray(exps_);
				if(objIds!=null && objIds.length>0){
					StringValue[] titles=krn.getStringValues(objIds, attr, 0, false, 0);
					if(titles.length>0){
						Arrays.sort(titles);
						for(StringValue title:titles){
							if(title.index==0){
								repls.put(title.objectId, title.value);
								FilterObject fobj=new FilterObject(title.value, title.objectId);
								if(exportBoxModel.getIndexOf(fobj)<0) 
									exportBoxModel.addElement(fobj);
								else
									fobj=null;
							}
						}
					}else{
						Arrays.sort(objIds);
						for(long objId:objIds){
							repls.put(objId, ""+objId);
							FilterObject fobj=new FilterObject(""+objId, objId);
							if(exportBoxModel.getIndexOf(fobj)<0) 
								exportBoxModel.addElement(fobj);
							else
								fobj=null;
						}
					}
				}
				if(isImportCheckBox.isSelected()){
					replBox.setModel(importBoxModel);
					replBox.repaint();

				}else if(isExportCheckBox.isSelected()){
					replBox.setModel(exportBoxModel);
					if(isReplBoxSet && replBox.getItemCount()>0) {
						isReplBoxSet=false;
						replBox.setSelectedIndex(replBox.getItemCount()-1);
					}
					replBox.repaint();
				}else{
					replBox.setModel(nullBoxModel);
					replBox.repaint();
				}
				userBox.repaint();
				setChangeTitle();
			}
			if(methodBtn.isSelected() 
					|| serviceBtn.isSelected()      || ifcBtn.isSelected() 
					|| filterBtn.isSelected() || reportBtn.isSelected()
					|| boxBtn.isSelected()  || hmBtn.isSelected()  || triggerBtn.isSelected()
					|| classBtn.isSelected()  || attrBtn.isSelected()
					|| begDate.getValue()!=null || endDate.getValue()!=null) {
				loadTmpChanges();
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}

	private void doSearch() {
		if(methodBtn.isSelected() || serviceBtn.isSelected() 
				|| ifcBtn.isSelected() || filterBtn.isSelected() 
				|| boxBtn.isSelected() || hmBtn.isSelected() 
				|| triggerBtn.isSelected() || attrBtn.isSelected() || classBtn.isSelected() 
				|| reportBtn.isSelected()|| begDate.getValue()!=null || endDate.getValue()!=null){
			loadTmpChanges();
		}else {
			changes = new ArrayList<KrnVcsChange>();
			changes.addAll(workeChanges);
			if(comment.getText().length() > 0) {
				String cmt = comment.getText().toUpperCase();
				for(KrnVcsChange change: workeChanges) {
					if(change.comment == null) {
						if(changes != null && changes.contains(change))
							changes.remove(change);   
					}else if(!change.comment.toUpperCase().contains(cmt)) {
						if(changes != null && changes.contains(change))
							changes.remove(change);    		
					}
				}
			}
		}
		setChanges(changes);
		model.fireTableDataChanged();
		if(selRowCount==0) {
			commitdBtn.setEnabled(false);
			unDoBtn.setEnabled(false);
		}
		messageArea.setText("");
	}


	private void loadTmpChanges(){
		tmpChanges.clear();
		boolean parDate=false;
		boolean parFilter=false;
		KrnDate endDate_=null;
		if(begDate.getValue()!=null || endDate.getValue()!=null){
			parDate=true;
			if(endDate.getValue()!=null){
				endDate_= Funcs.convertDate(Funcs.convertDate(endDate.getValue()));
				endDate_.addDays(1);
			}
		}

		String cmt = comment.getText().toUpperCase();

		if(methodBtn.isSelected() 
				|| serviceBtn.isSelected()      || ifcBtn.isSelected() 
				|| filterBtn.isSelected() || reportBtn.isSelected()
				|| boxBtn.isSelected()  || hmBtn.isSelected() || triggerBtn.isSelected()
				|| classBtn.isSelected() || attrBtn.isSelected() || cmt.length() > 0) {
			parFilter=true;
		}   

		for(KrnVcsChange change:workeChanges){
			if(parDate){
				if((endDate_==null ||change.dateChange.before(endDate_)) 
						&& (begDate.getValue()==null ||change.dateChange.after(begDate.getValue()))){
					if(!parFilter){
						tmpChanges.add(change);
					}
				}else{
					continue;
				}

			}
			if(!parFilter) continue;
			if(methodBtn.isSelected() && change.cvsChangeMethod!=null)
				tmpChanges.add(change);
			if(serviceBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_PROCESS_DEF.id)
				tmpChanges.add(change);
			if(ifcBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_UI.id)
				tmpChanges.add(change);
			if(filterBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_FILTER.id)
				tmpChanges.add(change);
			if(reportBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_REPORT_PRINTER.id)
				tmpChanges.add(change);
			if(boxBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_BOX_EXCHANGE.id)
				tmpChanges.add(change);
			if(hmBtn.isSelected() && change.cvsChangeObj!=null && change.cvsChangeObj.classId==Kernel.SC_HIPERTREE.id)
				tmpChanges.add(change);
			if(triggerBtn.isSelected() && change.isTrigger)
				tmpChanges.add(change);
			if(attrBtn.isSelected() && change.cvsChangeAttr!=null && !change.isTrigger)
				tmpChanges.add(change);
			if(classBtn.isSelected() && change.cvsChangeClass!=null && !change.isTrigger)
				tmpChanges.add(change);
			if(cmt.length() > 0) {
				if(change.comment == null) {
					if(tmpChanges != null && tmpChanges.contains(change))
						tmpChanges.remove(change);   
				}
				else {
					String str = change.comment;
					if(!change.comment.toUpperCase().contains(cmt)) {
						if(tmpChanges != null && tmpChanges.contains(change))
							tmpChanges.remove(change);    		
					}
				}
			}
		}    	
		changes=tmpChanges;

	}

	private void filter() {
		List<KrnVcsChange> tmpChanges = new ArrayList<KrnVcsChange>();
		boolean parFilter = false;
		Date endDate_ = null;

		if (begDate.getValue() != null || endDate.getValue() != null) {
			if (endDate.getValue() != null) {
				endDate_ = endDate.getValue();
				endDate_.setTime(endDate_.getTime() + 24*60*60000);
			}
		}

		if (methodBtn.isSelected() || serviceBtn.isSelected() || ifcBtn.isSelected() || filterBtn.isSelected()
				|| reportBtn.isSelected() || boxBtn.isSelected() || hmBtn.isSelected() || triggerBtn.isSelected()
				|| attrBtn.isSelected() || classBtn.isSelected()) {
			parFilter = true;
		}

		for (KrnVcsChange change : workeChanges) {
			Date date = change.dateChange;
			if ((endDate_ == null || date.before(endDate_))
					&& (begDate.getValue() == null || date.after(begDate.getValue()))) {
				if (parFilter) {
					KrnObject obj = change.cvsChangeObj;

					if (methodBtn.isSelected() && change.cvsChangeMethod != null)
						tmpChanges.add(change);
					else if (serviceBtn.isSelected() && obj != null	&& obj.classId == Kernel.SC_PROCESS_DEF.id)
						tmpChanges.add(change);
					else if (ifcBtn.isSelected() && obj != null && obj.classId == Kernel.SC_UI.id)
						tmpChanges.add(change);
					else if (filterBtn.isSelected() && obj != null && obj.classId == Kernel.SC_FILTER.id)
						tmpChanges.add(change);
					else if (reportBtn.isSelected() && obj != null && obj.classId == Kernel.SC_REPORT_PRINTER.id)
						tmpChanges.add(change);
					else if (boxBtn.isSelected() && obj != null && obj.classId == Kernel.SC_BOX_EXCHANGE.id)
						tmpChanges.add(change);
					else if (hmBtn.isSelected() && obj != null && obj.classId == Kernel.SC_HIPERTREE.id)
						tmpChanges.add(change);
					else if (triggerBtn.isSelected() && change.isTrigger)
						tmpChanges.add(change);
					else if (attrBtn.isSelected() && change.cvsChangeAttr != null)
						tmpChanges.add(change);
					else if (classBtn.isSelected() && change.cvsChangeClass != null)
						tmpChanges.add(change);
				} else
					tmpChanges.add(change);
			}
		}
		changes = tmpChanges;
	}

	private Icon getIcon(int row) {
		KrnVcsChange change = model.changes.get(row);
		Icon res = null;
		if (change.cvsChangeObj != null) {
			if (Kernel.SC_PROCESS_DEF.id == change.cvsChangeObj.classId) {
				res = kz.tamur.rt.Utils.getImageIconForClass("ProcessDef");
			} else if (Kernel.SC_FILTER.id == change.cvsChangeObj.classId) {
				res = kz.tamur.rt.Utils.getImageIconForClass("Filter");
			} else if (Kernel.SC_REPORT_PRINTER.id == change.cvsChangeObj.classId) {
				res = kz.tamur.rt.Utils.getImageIconForClass("ReportPrinter");
			} else if (Kernel.SC_UI.id == change.cvsChangeObj.classId) {
				res = kz.tamur.rt.Utils.getImageIconForClass("UI");
			} else if (Kernel.SC_BOX_EXCHANGE.id==change.cvsChangeObj.classId){
				res=kz.tamur.rt.Utils.getImageIconForClass("BoxTree");
			} else if (Kernel.SC_HIPERTREE.id==change.cvsChangeObj.classId){
				res=kz.tamur.rt.Utils.getImageIconForClass("HyperTree");
			}
		} else if (change.cvsChangeMethod != null) {
			res = kz.tamur.rt.Utils.getImageIconForClass("<Methods>");
		} else if (change.cvsChangeClass != null && !change.isTrigger) {
			res = kz.tamur.rt.Utils.getImageIconForClass("<Class>");
		} else if (change.cvsChangeAttr != null && !change.isTrigger) {
			res = kz.tamur.rt.Utils.getImageIconForClass("<Attribut>");
		} else if (change.isTrigger) {
			res = kz.tamur.rt.Utils.getImageIconForClass("<Triggers>");
		}
		return res;
	}

	private Boolean getJumpBackground(int row) {
		KrnVcsChange change = model.changes.get(rowSorter.convertRowIndexToModel(row));
		Boolean res = true;
		if (change.cvsChangeMethod != null) {
			if (change.cvsChangeMethod.classId < 0) {
				res = false;
			}
		}
		return res;
	}

	private void setChangeTitle() {
		for (KrnVcsChange change : changes) {
			if (change.title != null && !"".equals(change.title))
				continue;
			String title = "";
			if (change.cvsChangeObj != null) {
				if (Kernel.SC_PROCESS_DEF.id == change.cvsChangeObj.classId) {
					title = process.get(change.cvsChangeObj.id);
				} else if (Kernel.SC_FILTER.id == change.cvsChangeObj.classId) {
					title = filters.get(change.cvsChangeObj.id);
				} else if (Kernel.SC_REPORT_PRINTER.id == change.cvsChangeObj.classId) {
					title = reports.get(change.cvsChangeObj.id);
				} else if (Kernel.SC_UI.id == change.cvsChangeObj.classId) {
					title = uis.get(change.cvsChangeObj.id);
				}
			} else if (change.cvsChangeMethod != null) {
				title = change.cvsChangeMethod.name;
			} else if (change.cvsChangeClass != null) {
				title = "Триггер класса " + change.cvsChangeClass.name + " (" + change.cvsChangeClass.id + "), событие '" + Utils.getTriggerNameByModelChangeType(change.typeId) + "'";
			} else if (change.cvsChangeAttr != null) {
				title = "Триггер атрибута " + change.cvsChangeAttr.name + " (" + change.cvsChangeAttr.id + "), событие '" + Utils.getTriggerNameByModelChangeType(change.typeId) + "'";
			}
			change.title = title;
		}
	}

	private boolean isPermitEdit(int row) {
		KrnVcsChange change = model.changes.get(row);
		if ((change.user.id == user.getObject().id || user.hasRight(Or3RightsNode.VCS_CHANGE_COMMIT_RIGHT)) && change.importId == -1)
			return true;
		else
			return false;
	}

	private static void setCounterText() {
		rowCount = changeTable.getModel().getRowCount();
		selRowIdx = changeTable.getSelectedRow() + 1;
		counterLabel.setText(selRowIdx + " / " + rowCount + " ");
	}

	private class ChangeMonTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final String[] COL_NAMES = {"Выбрать?", "Наименование измененного объекта", "Дата изменения", "Ответственный", "Импорт","Экспорт","Коментарий"};

		java.util.List<KrnVcsChange> changes;

		public ChangeMonTableModel(java.util.List<KrnVcsChange> changes) {
			this.changes=changes;
		}

		public boolean isCellEditable(int row, int column) {
			if(column==0 && isSysDb && isPermitEdit(row)
					&& !isFixdCheckBox.isSelected() && !isImportCheckBox.isSelected() && !isExportCheckBox.isSelected())
				return true;
			else
				return false;
		}

		public int getRowCount() {
			return changes.size();
		}

		public int getColumnCount() {
			return COL_NAMES.length;
		}

		public Class getColumnClass(int col) {
			if (col == 2)
				return KrnDate.class;
			else
				return super.getColumnClass(col);
		}

		public String getColumnName(int columnIndex) {
			return COL_NAMES[columnIndex];
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return changes.get(rowIndex).isChecked;
			case 1:
				return changes.get(rowIndex).toString();
			case 2:
				return changes.get(rowIndex).dateChange;
			case 3:
				return users.get(changes.get(rowIndex).user.id);
			case 4:
				return changes.get(rowIndex).importId>0?changes.get(rowIndex).importId:"";
			case 5:
				return changes.get(rowIndex).importId>0?impExpIds.get(changes.get(rowIndex).importId)
						:changes.get(rowIndex).exportId>0?changes.get(rowIndex).exportId:"";
			case 6:
				return changes.get(rowIndex).comment;
			}
			return null;
		}

		@Override
		public void fireTableDataChanged() {
			super.fireTableDataChanged();
			setCounterText();
		}
	}

	public void setChanges(java.util.List<KrnVcsChange> changes) {
		model.changes = changes;
		model.fireTableDataChanged();
	}

	public class FormalDataCellRenderer implements TableCellRenderer {
		private JLabel component = Utils.createLabel();
		private TableCellRenderer normal;

		public FormalDataCellRenderer(TableCellRenderer cellRenderer) {
			super();
			this.normal = cellRenderer;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSysDb && column==0) {
				if (value instanceof Boolean) {
					JCheckBox cb = new JCheckBox("", value.equals(new Boolean(true)));
					cb.setFocusPainted(isSelected);
					return cb;
				}
			} else if ((isSysDb && column==1)||(!isSysDb && column==0)) {
				component.setOpaque(true);
				component.setBackground(getBackground(1, isSelected, getJumpBackground(row)));
				component.setText((String) value);
				component.setIcon(getIcon(rowSorter.convertRowIndexToModel(row)));
				return component;
			}
			if (isSelected)
				setCounterText();
			return normal.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	class FormalDataCellEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;

		public FormalDataCellEditor(JCheckBox editor) {
			super(editor); //? What shall I do?
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,Object value, boolean isSelected, int row, int column) { 
			if (value.equals(true)) {
				selRowCount--;
				value=false;
			} else {
				selRowCount++;
				value=true;
			}
			if (selRowCount > 0 && isSysDb) { 
				unDoBtn.setEnabled(true);
				commitdBtn.setEnabled(true);
			} else {
				commitdBtn.setEnabled(false);
				unDoBtn.setEnabled(false);
			}

			changes.get(rowSorter.convertRowIndexToModel(row)).isChecked = value.equals(true);
			return super.getTableCellEditorComponent(table, value, isSelected,row, column);
		}
	}

	public static Color getBackground(int row, boolean isSelected, boolean isJump) {
		Color backgroundColor;
		if(!isJump){
			backgroundColor = Utils.getLightGraySysColor();
		}
		else if(!isSelected){
			backgroundColor = (row % 2 == 0 ? Utils.getLightSysColor() : Color.WHITE);
		}
		else
			backgroundColor = Utils.getMidSysColor();
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

	class ImageListCellRenderer implements ListCellRenderer {

		private JLabel component = Utils.createLabel();

		public Component getListCellRendererComponent(JList jlist, Object value, int cellIndex, boolean isSelected, boolean cellHasFocus) {
			component.setOpaque(true);
			component.setBackground(Utils.getMainColor());
			if (value instanceof JLabel) {
				String text = ((JLabel) value).getText();
				component.setText(StringUtils.rightPad(text, 100));
				component.setIcon(((JLabel) value).getIcon());
			} else {
				component.setText("?????????");
			}
			return component;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		boolean isRefresh=false;
		if (src.equals(commitdBtn) || src.equals(unDoBtn)) {
			messageArea.setText("");
			messageArea.setVisible(src.equals(commitdBtn));
			listModel.clear();
			ArrayList<KrnVcsChange> selChanges=new ArrayList<KrnVcsChange>();
			Vector<Integer> rows=new Vector<Integer>();
			for(int i=0;i<model.getRowCount();i++) {
				if (model.getValueAt(i, 0).equals(true)){
					rows.add(i);
					selChanges.add((KrnVcsChange) changes.get(i));
					JLabel litem = Utils.createLabel();
					litem.setOpaque(true);
					litem.setBackground(getBackground(1, true, getJumpBackground(i)));
					litem.setText((String)changes.get(i).title);
					litem.setIcon(getIcon(i));
					listModel.addElement(litem);
				}
			}
			if(selChanges.size()>0){
				noteLabel.setText(src.equals(unDoBtn)?noteUnDo:noteFix);
				dlg = new DesignerDialog((Frame)getTopLevelAncestor(), src.equals(commitdBtn)?"Введите коментарий для подтверждения":"Подтвердите операцию", dlgPanel);
				dlg.setOkEnabled(src.equals(unDoBtn));
				dlg.setVisible(true); 
				dlg.setMinimumSize(new Dimension(450,300));
				dlg.setResizable(false);
				if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
					try {
						if(src.equals(unDoBtn)) {
							krn.rollbackVcsObjects(selChanges);
							for(KrnVcsChange ch:selChanges) {
								if(ch.cvsChangeObj!=null && ch.cvsChangeObj.classId==Kernel.SC_FILTER.id) {
									krn.saveFilter(ch.cvsChangeObj.id);
									Or3Frame.instance().refreashFilter(ch.cvsChangeObj);
									Or3Frame.instance().getFiltersFrame().reloadTree();
									if(krn.getObjectByUid(ch.cvsChangeObj.uid, 0) == null) {
										krn.getUser().config.removeFltInHistory(ch.cvsChangeObj);
										Or3Frame.instance().getFiltersFrame().getTabbedContent().removeFltTabByObj(ch.cvsChangeObj);
									}
								}else if(!ch.isTrigger && ch.cvsChangeAttr!=null) {
									//Or3Frame.instance().reloadAttribute(ch.cvsChangeAttr);
								}else if(!ch.isTrigger && ch.cvsChangeClass!=null) {
									//Or3Frame.instance().reloadClass(ch.cvsChangeClass);
								}else if(ch.cvsChangeObj!=null && (ch.cvsChangeObj.classId == Kernel.SC_REPORT_FOLDER.id || ch.cvsChangeObj.classId == Kernel.SC_REPORT_PRINTER.id)) {
									Or3Frame.instance().getReportFrame().reloadTree();
									if(krn.getObjectByUid(ch.cvsChangeObj.uid, 0) == null) {
										krn.getUser().config.removeRptInHistory(ch.cvsChangeObj);
										Or3Frame.instance().getReportFrame().unselect();
									}
								}else if(ch.cvsChangeObj!=null && (ch.cvsChangeObj.classId == Kernel.SC_PROCESS_DEF.id || ch.cvsChangeObj.classId == Kernel.SC_PROCESS_DEF_FOLDER.id)) {
									Or3Frame.instance().getServiceFrame().reloadTree();
									if(krn.getObjectByUid(ch.cvsChangeObj.uid, 0) == null) {
										krn.getUser().config.removeSrvInHistory(ch.cvsChangeObj);
										Or3Frame.instance().getServiceFrame().getTabbedContent().removeSrvTabByObj(ch.cvsChangeObj);
									}
								}else if(ch.cvsChangeObj!=null && (ch.cvsChangeObj.classId == Kernel.SC_UI.id || ch.cvsChangeObj.classId == Kernel.SC_UI_FOLDER.id)) {
									Or3Frame.instance().getDesignerFrame().reloadTree();
									if(krn.getObjectByUid(ch.cvsChangeObj.uid, 0) == null) {
										krn.getUser().config.removeIfcInHistory(ch.cvsChangeObj);
										DesignerFrame.tabbedContent.removeIfcTabByObj(ch.cvsChangeObj);
									}
								}else if(ch.cvsChangeObj!=null && ch.cvsChangeObj.classId == Kernel.SC_FILTER_FOLDER.id) {
									Or3Frame.instance().getFiltersFrame().reloadTree();
									if(krn.getObjectByUid(ch.cvsChangeObj.uid, 0) == null) {
										krn.getUser().config.removeFltInHistory(ch.cvsChangeObj);
										Or3Frame.instance().getFiltersFrame().getTabbedContent().removeFltTabByObj(ch.cvsChangeObj);
									}
								}								
								
							}
						}else
							krn.commitVcsObjects(selChanges, messageArea.getText());
						changes.removeAll(selChanges);
						if(workeChanges!=null && workeChanges.size()>0)
							workeChanges.removeAll(selChanges);
						//fillTableModel();
						setChanges(changes);
						model.fireTableDataChanged();

						if(selChanges.size()<=selRowCount)
							selRowCount-=selChanges.size();
						else
							selRowCount=0;
						if(selRowCount==0) {
							commitdBtn.setEnabled(false);
							unDoBtn.setEnabled(false);
						}
						messageArea.setText("");
						// Обновляем методы в модели
						for(KrnVcsChange ch:selChanges){
							if(ch.cvsChangeMethod!=null && ch.cvsChangeMethod.classId>0)
								krn.getClassNode(ch.cvsChangeMethod.classId).rollbackMethodByUid(ch.cvsChangeMethod.uid);
						}
						//если открыт ClassBrouser обновляем его содержимое
						Or3Frame frm = (Or3Frame) getTopLevelAncestor();
						if(frm.getClassFrame()!=null && frm.getClassFrame().getClassBrouser()!=null)
							frm.getClassFrame().getClassBrouser().updateAttrTree();
					} catch (KrnException e1) {
						e1.printStackTrace();
					}
				}
			}
		}else if(src.equals(refreshBtn)){
			isRefresh=true;
			if(checkAllBox.isSelected())
				checkAllBox.setSelected(false);
		} else if (src.equals(srchBtn)) {
			SearchPanel sp = new SearchPanel();
			Container cont = getTopLevelAncestor();
			DesignerDialog dlg = new DesignerDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, "Поиск", sp);
			dlg.show();
			if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
				if(sp.getParamComboSelectedIndex()==0)
					searchObject(sp.getSearchText(), sp.isFromDB());
				else if(sp.getParamComboSelectedIndex()==1)
					searchObjectByDiffHistory(sp.getSearchText());
			}
		} else if (src.equals(backBtn)) {
			backBtn.setEnabled(false);
			isRefresh=true;
		}else if(src.equals(isFixdCheckBox) || src.equals(isImportCheckBox)|| src.equals(isExportCheckBox)  || src.equals(isUserCheckBox)){

			if(src.equals(isFixdCheckBox)) {                
				if(isFixdCheckBox.isSelected()) {
					comment.setEnabled(true);
					commentBtn.setEnabled(true);
				} else {
					comment.setEnabled(false);
					commentBtn.setEnabled(false);
				}
			}

			if(checkAllBox.isSelected())
				checkAllBox.setSelected(false);
			if(src.equals(refreshBtn)){
				if(isFixdCheckBox.isSelected())
					isFixdCheckBox.setSelected(false);
				if(isImportCheckBox.isSelected())
					isImportCheckBox.setSelected(false);
				if(isExportCheckBox.isSelected())
					isExportCheckBox.setSelected(false);
			}else if(src.equals(isFixdCheckBox) && (isImportCheckBox.isSelected() || isExportCheckBox.isSelected())){
				if(isImportCheckBox.isSelected())
					isImportCheckBox.setSelected(false);
				else
					isExportCheckBox.setSelected(false);
			}else if(src.equals(isImportCheckBox) && (isFixdCheckBox.isSelected() || isExportCheckBox.isSelected())){
				if(isFixdCheckBox.isSelected())
					isFixdCheckBox.setSelected(false);
				else
					isExportCheckBox.setSelected(false);
			}else if(src.equals(isExportCheckBox)) {
				if((isFixdCheckBox.isSelected() || isImportCheckBox.isSelected())){
					if(isFixdCheckBox.isSelected())
						isFixdCheckBox.setSelected(false);
					else
						isImportCheckBox.setSelected(false);
				}
				isReplBoxSet=isExportCheckBox.isSelected();
			}
			if(isFixdCheckBox.isSelected() || isImportCheckBox.isSelected() || isExportCheckBox.isSelected())
				checkAllBox.setEnabled(false);
			else if(!checkAllBox.isEnabled())
				checkAllBox.setEnabled(true && isSysDb);
			isRefresh=true;
		}else if(src.equals(checkAllBox)){
			for(KrnVcsChange change:changes){
				if((change.user.id == user.getObject().id || user.hasRight(Or3RightsNode.VCS_CHANGE_COMMIT_RIGHT)) && change.importId==-1){
					change.isChecked=checkAllBox.isSelected();
					if(checkAllBox.isSelected())
						selRowCount++;
					else
						selRowCount--;
				}
			}
			if(selRowCount>0){ 
				unDoBtn.setEnabled(true);
				commitdBtn.setEnabled(true);
			}else{
				commitdBtn.setEnabled(false);
				unDoBtn.setEnabled(false);
			}
			model.fireTableDataChanged();
		}else if(src.equals(userBox)){
			selUserId=((FilterObject)userBox.getSelectedItem()).id;
			isRefresh=true;
		}else if(src.equals(replBox)){
			selReplId=((FilterObject)replBox.getSelectedItem()).id; 
			isRefresh=true;
		}else if(src.equals(methodBtn) || src.equals(serviceBtn) 
				|| src.equals(ifcBtn) || src.equals(filterBtn) 
				|| src.equals(boxBtn) || src.equals(hmBtn) 
				|| src.equals(triggerBtn) || src.equals(attrBtn) || src.equals(classBtn)
				|| src.equals(reportBtn) || src.equals(begDate) || src.equals(endDate) || src.equals(commentBtn)){
			//обновление
			doSearch();
			//
		}else if(src.equals(resetUser)){
			if(userTree==null){
				userTree= kz.tamur.comps.Utils.getUserTree("Разработчик");
				if(userTree==null)
					userTree= kz.tamur.comps.Utils.getUserTree("Разработчики");
				userOp=new OpenElementPanel(userTree);
			}
			DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите пользователя", userOp,false);
			dlg.show();
			if(dlg.getResult()== ButtonsFactory.BUTTON_OK){
				AbstractDesignerTreeNode node = userOp.getTree().getSelectedNode();
				KrnObject userObj=node.getKrnObj();
				try {
					if(userObj!=null){
						int row = changeTable.getSelectedRow();
						if(row>=0){
							KrnVcsChange change=changes.get(rowSorter.convertRowIndexToModel(row));
							if(change.user==null || change.user.id!=userObj.id){
								boolean res = krn.setVcsUserForObject(change, userObj.id);
								if(res){
									change.oldUserId=change.user.id;
									change.user=userObj;
									model.fireTableDataChanged();
								}
							}
						}
					}
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
			}
		}
		if(isRefresh && model!=null){
			//обновление
			loadChanges();
			setChanges(changes);
			//    model.fireTableDataChanged();
			selRowCount=0;
			commitdBtn.setEnabled(false);
			unDoBtn.setEnabled(false);
			messageArea.setText("");
			//

		}
	}
	private void searchObject(String uid, boolean isFromDB) {
		List<KrnVcsChange> foundChanges = new ArrayList<KrnVcsChange>();
		if (uid.length() > 0) {
			if (isFromDB) {
				try {
					int isFixd;
					if (isFixdCheckBox.isSelected()) {
						isFixd = Constants.VCS_FIXD;
					} else if (isImportCheckBox.isSelected()) {
						isFixd = Constants.VCS_IMPORT;
					} else if (isExportCheckBox.isSelected()) {
						isFixd = Constants.VCS_EXPORT;
					} else {
						isFixd = Constants.VCS_NOT_FIXD;
					}
					foundChanges = krn.getVcsChangesByUID(isFixd, -1, isUserCheckBox.isSelected() ? krn.getUser().object.id : selUserId, isImportCheckBox.isSelected() || isExportCheckBox.isSelected() ? selReplId : -1, uid);
				} catch (KrnException e) {
					e.printStackTrace();
				}
			} else {
				for (KrnVcsChange change : changes) {
					if (change.cvsChangeObj != null) {
						if (change.cvsChangeObj.uid.trim().equals(uid)) {
							foundChanges.add(change);
						}
					} else if (change.cvsChangeMethod != null) {
						if (change.cvsChangeMethod.uid.trim().equals(uid)) {
							foundChanges.add(change);
						}
					} else if (change.cvsChangeClass != null) {
						if (change.cvsChangeClass.uid.trim().equals(uid)) {
							foundChanges.add(change);
						}
					} else if (change.cvsChangeAttr != null) {
						if (change.cvsChangeAttr.uid.trim().equals(uid)) {
							foundChanges.add(change);
						}
					}
				}
			}
		}
		if (foundChanges.size() > 0) {
			checkAllBox.setSelected(false);
			selRowCount = 0;
			setChanges(foundChanges);
			model.fireTableDataChanged();
			backBtn.setEnabled(true);
		} else {
			Container cont = getTopLevelAncestor();
			MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, (isFromDB ? "В БД измнений " : "Среди загруженных изменений ") + "по заданому UID объекты не найдены...");
		}
	}
	private void searchObjectByDiffHistory(String text){
		srchBtn.setEnabled(false);
		try {
			List<Object> results = Kernel.instance().search(text, 1000, new int[]{1,1}, new boolean[]{false,false,false,false,false,false,false,true});
			List<String[]> objects = (List<String[]>) results.get(0);
			if(objects!=null && objects.size()>0){
				long[] ids=new long[objects.size()];
				for(int i=0;i<objects.size();i++){
					ids[i]=Long.valueOf(objects.get(i)[0]);
				}
				int ind=changeTable.getSelectedRow();
				if(ind>=0)
					changeTable.removeRowSelectionInterval(ind, ind);
				List<KrnVcsChange> changes=Kernel.instance().getVcsDifChanges(true, ids);
				changeHistory.refreshTableFromSearch(changes,"Результаты конекстного поиска - '"+text+"':");
			}
		} catch (KrnException e) {
			e.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("Cancel by user!!!!");
			ex.printStackTrace();
		}
		srchBtn.setEnabled(true);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (dlg != null)
			dlg.setOkEnabled(!messageArea.getText().equals(""));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (dlg != null)
			dlg.setOkEnabled(!messageArea.getText().equals(""));
	}

	@Override
	public void changedUpdate(DocumentEvent e) {}

	private class FilterObject {

		private String title;
		private long id;

		FilterObject(String title, long id) {
			this.title = title;
			this.id = id;
		}

		public String toString() {
			return title;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof FilterObject)
				return id == ((FilterObject) obj).id;
			return false;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && e.getSource() == changeTable.getSelectionModel() && e.getFirstIndex() >= 0) {
			int row = changeTable.getSelectedRow();
			KrnVcsChange change = row >= 0 ? model.changes.get(rowSorter.convertRowIndexToModel(row)) : null;
			changeHistory.refreshTable(change, false);
		}
	}

	class SearchPanel extends JPanel implements ActionListener{

		private JLabel label = Utils.createLabel("Введите UID для поиска:");
		private JTextField textField = Utils.createDesignerTextField();
		private Dimension size = new Dimension(300, 70);
		private JCheckBox isFromDB = Utils.createCheckBox("Искать в БД", false);
		private JComboBox paramCombo = Utils.createCombo();

		public SearchPanel() {
			super(new GridBagLayout());
			Utils.setAllSize(this, size);
			init();
		}

		private void init() {
			setOpaque(false);
			add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			add(textField, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			add(isFromDB, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, NONE, new Insets(5, 5, 5, 5), 0, 0));
			paramCombo.setOpaque(false);
			paramCombo.addItem("Поиск по UID");
			paramCombo.addItem("Поиск по изменению в тексте");
			Utils.setAllSize(paramCombo, new Dimension(190, 25));
			add(paramCombo, new GridBagConstraints(0, 2, 1, 1, 0, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
			paramCombo.addActionListener(this);
		}

		public String getSearchText() {
			String res = Funcs.normalizeInput(textField.getText()).trim();
			if (Funcs.isValid(res))
				return res;
			return "";
		}

		public boolean isFromDB() {
			return isFromDB.isSelected();
		}

		public int getParamComboSelectedIndex() {
			return paramCombo.getSelectedIndex();
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==paramCombo){
				if(paramCombo.getSelectedIndex()==0)
					label.setText("Введите UID для поиска:");
				else if(paramCombo.getSelectedIndex()==1)
					label.setText("Введите текст для поиска:");
			}

		}
	}
}