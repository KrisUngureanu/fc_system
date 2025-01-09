package kz.tamur.guidesigner.terminal;

import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.EmptyFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.ErrRecord;
import kz.tamur.lang.parser.EvaluatorVisitor;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.TerminalInterfaceManager;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.ExpressionDebuger;
import kz.tamur.util.ExpressionEditor;
import other.treetable.JTreeTable;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
/**
 * Панель Консоли в Конструкторе
 * @author g009c1233
 * @since 2011/06/07
 * @version 0.1
 */
public class TerminalPanel extends JPanel implements ActionListener {
	ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
	private JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane terSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JSplitPane propSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private DesignerStatusBar statusPanel = new DesignerStatusBar();
	private JLabel view;
	private OrGradientMenuBar menuBar = new OrGradientMenuBar();
	private PropertyPanel pPanel = new PropertyPanel();
	private JPanel consolePanel = new JPanel();
	private JPanel infPanel = new JPanel();
	private JPanel leftSide = new JPanel(new BorderLayout());
	
	private JMenu fileMenu = new JMenu(res.getString("run"));
	private JMenuItem serverItem = createMenuItem(res.getString("server"));
	private JMenuItem clientItem = createMenuItem(res.getString("client"));
	
	private JMenu resetMenu = new JMenu(res.getString("resetData"));
	private JMenuItem servRstItem = createMenuItem(res.getString("server"));
	private JMenuItem cliRstItem = createMenuItem(res.getString("client"));
	
	private ExpressionEditor ex = new ExpressionEditor("");
	
	private JLabel labell = new JLabel("");
	private JButton outClearBtn = new JButton(res.getString("clearConsole"));
	private JButton runOnClient = new JButton("Выполнить в Клиенте");
	private JButton runOnServer = new JButton("Выполнить на Сервере");
	private JButton cancelExec = new JButton("Остановить");
	private JTextArea consoleOut = new JTextArea();
	private JScrollPane consoleScroll;
	private javax.swing.text.Document commentDoc = consoleOut.getDocument();
	
	private JScrollPane infScroll = new JScrollPane();
	private JTable propTable = new JTable();
	private JTable infTable = new JTable();
	private JTreeTable infTreeTable = new JTreeTable();
	private JTree tree;
	private VariableInfoModel model = null;
	private HashMap<Long,VariableObject> variables = new HashMap<Long, VariableObject>();
	
	private JPopupMenu popupMenu = new JPopupMenu();
	int row = -1;
	final Kernel krn = Kernel.instance();
	private ClientOrLang lang = new ClientOrLang(new EmptyFrame(krn));
	
    private ExpressionDebuger debuger = new ExpressionDebuger(lang);
	
	//for server
	private HashMap<String, Object> varsMap = new HashMap<String, Object>();
	//for client
	private HashMap<String, Object> vars = new HashMap<String, Object>();
	//currentUsing Map
	private HashMap<String, Object> currVarsMap = new HashMap<String, Object>();
	//true-server, false - client
	private boolean serverUsed = false;
	
	private String startString = "Время запуска: ";
	private String finishString = "Время завершения: ";
	private String durationString = "Продолжительность: ";
	private JLabel startLabel = createLabel(startString);
	private JLabel finishLabel = createLabel(finishString);
	private JLabel durationLabel = createLabel(durationString);
	
	private int mode;
	private Thread terminalThread;
	private long terminalThreadId;
	private boolean flag = false;
	
	private Map<Long, ClientOrLang> langsMap = new HashMap<>();
	
	/**
	 * Панель Консоли в Конструкторе
	 */
	public TerminalPanel(){
		super(new BorderLayout());
		view = createLabel("Just view");
		add(mainSplit, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
		initMenu();
		init();
		initStatusBar();
		
		TerminalInterfaceManager mgr = new TerminalInterfaceManager(krn);
                InterfaceManagerFactory.instance().register(mgr);
	}
	
	/**
	 * Инициализация менюшки
	 */
	public void initMenu(){
		menuBar.add(view);
		fileMenu.add(serverItem);
		fileMenu.addSeparator();
		fileMenu.add(clientItem);
		resetMenu.add(servRstItem);
		resetMenu.addSeparator();
		resetMenu.add(cliRstItem);
		menuBar.add(fileMenu);
		menuBar.add(view);
		menuBar.add(resetMenu);
		
		serverItem.addActionListener(this);
		clientItem.addActionListener(this);
		
		servRstItem.addActionListener(this);
		cliRstItem.addActionListener(this);
		
		fileMenu.setMnemonic(KeyEvent.VK_D);
	}
	
	/**
	 * Основная инициализация
	 */
	public void init(){
		initLeftSide();
		initRightSide();
		mainSplit.setLeftComponent(leftSide);
		mainSplit.setRightComponent(propSplit);
		mainSplit.setDividerLocation(1.0);
		mainSplit.validate();
	}
	
	/**
	 * Инициализация левой части панели. Редактор и Вывод
	 */
	public void initLeftSide() {
		consoleScroll = new JScrollPane(consoleOut);
		consolePanel.setLayout(new GridBagLayout());
		JPanel runPanel = new JPanel(new FlowLayout());
		consolePanel.add(runPanel, new GridBagConstraints(0, 0, 1, 1, 2, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		runPanel.add(runOnClient);
		runOnClient.addActionListener(this);
		runPanel.add(runOnServer);
		runOnServer.addActionListener(this);
		cancelExec.setEnabled(false);
		runPanel.add(cancelExec);
		cancelExec.addActionListener(this);
		consolePanel.add(outClearBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 0, 3), 0, 0));
		outClearBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				consoleOut.setText("");
			}
		});
		
		JPanel infoPanel = new JPanel();
		infoPanel.add(startLabel);
		infoPanel.add(durationLabel);
		infoPanel.add(finishLabel);
		consolePanel.add(infoPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		consolePanel.add(consoleScroll, new GridBagConstraints(0, 2, 2, 2, 2, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 1, 0, 0), 0, 0));
		
		//TextAreaOutputStream textOut = new TextAreaOutputStream(consoleOut);
		//TextAreaOutputStream textOut = new TextAreaOutputStream(consoleOut);
		FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
		TextAreaOutputStream textOut = new TextAreaOutputStream(fdOut, 256);
		textOut.setTarget(consoleOut);
		PrintStream outStream = null;
		try {
			outStream = new PrintStream(textOut, true, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		System.setOut(outStream);
		System.setErr(outStream);
		
		consoleOut.setMinimumSize(new Dimension(100, 200));
		consoleOut.setEditable(false);
		
		terSplit.setDividerSize(7);
		terSplit.setTopComponent(ex);
		terSplit.setBottomComponent(consolePanel);
		leftSide.add(terSplit, BorderLayout.CENTER);
	}
	
	/**
	 * Инициализация правой панельки, Панель переменных и панель подробной информации о переменной
	 */
	public void initRightSide() {
		propSplit.add(pPanel);
		pPanel.select.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        if(pPanel.select.getSelectedItem().equals(res.getString("server"))){
		        	pPanel.updateProps(varsMap);
		        } else if(pPanel.select.getSelectedItem().equals(res.getString("client"))){
		        	pPanel.updateProps(vars);
		        }
		    }
		});
		propTable = pPanel.getTable();
		propTable.setAutoCreateRowSorter(true);
		infPanel.setLayout(new GridBagLayout());
		propSplit.add(infPanel);
		infTable = new JTable();
		infScroll = new JScrollPane(infTreeTable);
		tree = infTreeTable.getTree();
		
		infTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		infPanel.add(new JLabel(res.getString("informations")),new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 3), 0, 0));
		infPanel.add(infScroll, new GridBagConstraints(0, 1, 2, 3, 2, 2,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(3, 0, 0, 0), 0, 0));
		JMenuItem deleteVar = new JMenuItem("Delete Var");
		deleteVar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (row != -1) {
					deleteFromMap();
				}
				row = -1;
			}
		});
		popupMenu.add(deleteVar);
		JMenuItem deleteAllVars = new JMenuItem("Delete All Vars");
		deleteAllVars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (row != -1) {
					deleteAllFromMap();
				}
				row = -1;
			}
		});
		popupMenu.add(deleteAllVars);
		propTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e){
				showPopup(e);
			}
			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					row = propTable.rowAtPoint(e.getPoint());
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
		});
		propTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e){
				if(!e.getValueIsAdjusting()) {
					int selectedRowNum = propTable.getSelectedRow();
					if(selectedRowNum != -1) {
						Object obj = propTable.getValueAt(selectedRowNum, 1);
						updateInfTable(obj);
					}
				}
			}
		});
	}
	
	/**
	 * Обновление инфорционной панели, 
	 * @param obj переменная, о которой выводится информация
	 */
	private void updateInfTable(Object obj) {
		VariableObject objRoot = new VariableObject(obj);
		objRoot.name = (String) propTable.getValueAt(propTable.getSelectedRow(), 0);
		objRoot.vrs = createVector(obj);

		model = new VariableInfoModel(objRoot, variables);
		infTreeTable.setModel(model);
		tree = infTreeTable.getTree();
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent e) {
				VariableInfoNode expandedNode = ((VariableInfoNode)(e.getPath().getLastPathComponent()));
				if(expandedNode.getChildCount() == 1 && ((VariableInfoNode)expandedNode.getChildAt(0)).getVariable().var.equals("KartoshkaFreee")) {
					tree.collapsePath(e.getPath());
					VariableInfoNode freeChild = (VariableInfoNode)(expandedNode.getChildAt(0));
					model.removeNodeFromParent(freeChild);
					addChilds(expandedNode.getVariable(), expandedNode);
					tree.expandPath(e.getPath());
				}
			}
			
			public void treeCollapsed(TreeExpansionEvent e) {
				if(e.getPath().getParentPath() == null)
					tree.expandPath(e.getPath());
			}
		});
		infTreeTable.getColumn(infTreeTable.getColumnName(1)).setCellRenderer(new cellPanelToShowObj());
		infTreeTable.getColumn(infTreeTable.getColumnName(1)).setCellEditor((new cellPanelToEditor()));
	}
	
	/**
	 * Вектор деток obj-екта, первая, для отображения в начале
	 * @param obj переменная
	 * @return вектор детей
	 */
	public Vector<VariableObject> createVector(Object obj) {
		Vector<VariableObject> vector = new Vector<VariableObject>();
		
		variables = new HashMap<Long, VariableObject>(); 
		Long counter = 1L;
		
		if (obj != null) {
			Field[] o = obj.getClass().getDeclaredFields();
			for(Field f : o) {
				VariableObject vo = null;
				VariableObject free = new VariableObject("KartoshkaFreee");
				try {
					vo = new VariableObject(getValueOf(obj, f.getName()));
					vo.name = f.getName();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(f.getType().isArray())
					try {
						vo.vrs = new Vector<VariableObject>();
						int in = 0;
						if(vo.var instanceof char[]) {
							in = ((char[])vo.var).length;
							if(in > 0) {
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof long[]){
							in = ((long[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof Long[]) {
							in = ((Long[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof int[]) {
							in = ((int[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof byte[]) {
							in = ((byte[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof short[]) {
							in = ((short[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof float[]) {
							in = ((float[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof double[]) {
							in = ((double[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof boolean[]) {
							in = ((boolean[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						} else if(vo.var instanceof Object[]){
							in = ((Object[])vo.var).length;
							if(in > 0){
								vo.vrs.add(free);
							}
						}
						vo.name = vo.name + " - ["+ in +"]";
					} catch (Exception e) {
						e.printStackTrace();
					}
	
				else if(!f.getType().isPrimitive() && vo.var != null && vo.var.getClass().getDeclaredFields().length > 0){
					vo.vrs.add(free);
				}
				if(vo!=null)
				vector.add(vo);
				variables.put(counter++, vo);
			}
		}		
		
		return vector;
	}
	
	//WorkingOne
	/*
	//and createMap for TreeTable
	public Vector<VariableObject> createVector(Object obj) {
		Vector<VariableObject> vector = new Vector<VariableObject>();
		variables = new HashMap<Long, VariableObject>(); 
		Long counter = 1L;
		
		Field[] o = obj.getClass().getDeclaredFields();
		//Field[] o = obj.getClass().getFields();
		for(Field f : o) {
			VariableObject vo = null;
			try {
				vo = new VariableObject(getValueOf(obj, f.getName()));

				vo.name = f.getName();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(f.getType().isArray())
				try {
					//vo.vrs = createVector(getValueOf(obj, f.getName()));
					vo.vrs = new Vector<VariableObject>();
					int in = 0;
					if(vo.var instanceof char[]) {
						in = ((char[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((char[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "char";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof long[]){
						in = ((long[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((long[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "long";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof Long[]) {
						in = ((Long[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((Long[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "Long";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof int[]) {
						in = ((int[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((int[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "int";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof byte[]) {
						in = ((byte[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((byte[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "byte";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof short[]) {
						in = ((short[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((short[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "short";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof float[]) {
						in = ((float[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((float[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "float";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof double[]) {
						in = ((double[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((double[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "double";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof boolean[]) {
						in = ((boolean[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((boolean[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "boolean";
							vo.vrs.add(vobj);
						}
					//} else {
					} else if(vo.var instanceof Object[]){
						in = ((Object[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((Object[])vo.var)[i]);
							vobj.name = "["+i+"]";
							//vobj.type = vobj.getClass().getName();
							//vo.vrs.add(vobj);
							//vo.vrs = addVector(vobj, 1);
							vobj.vrs = addVector(vobj.var,1);
							vo.vrs.add(vobj);
							//may be need to add working with object
						}
					}
					vo.name = vo.name + "-["+ in +"]";
				} catch (Exception e) {
					e.printStackTrace();
				}
			//class & his attrs! Enumes, ...
			else if(!f.getType().isPrimitive()){
				vo.vrs = addVector(vo.var, 0);
				
			}
			if(vo!=null)
			vector.add(vo);
			variables.put(counter++, vo);
		}
		
		return vector;
	}
	*/
	
	/**
	 * добавление веток
	 */
	public void addChilds(VariableObject varObj, VariableInfoNode parent){
		Vector<VariableObject> vector = getVector(varObj);
		if(vector == null) return;
		Object[] childs = vector.toArray();
		for(int i = 0; i < childs.length; i++) {
			model.insertNodeInto(new VariableInfoNode((VariableObject)(childs [i]), new HashMap()), parent, parent.getChildCount());
		}
		
	}
	
	/**
	 * Взять вектор деток переменной
	 * @param v переменнаяОб\ект
	 * @return вектор деток
	 */
	public Vector<VariableObject> getVector(VariableObject v){
		Object obj = v.var;
		Vector<VariableObject> vector = new Vector<VariableObject>();
		
		if(obj == null) return null;
		
		if(obj.getClass().isArray()){
			int in = 0;
			if(obj instanceof char[]) {
				in = ((char[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((char[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "char";
					vector.add(vobj);
				}
			} else if(obj instanceof long[]){
				in = ((long[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((long[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "long";
					vector.add(vobj);
				}
			} else if(obj instanceof Long[]) {
				in = ((Long[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((Long[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "Long";
					vector.add(vobj);
				}
			} else if(obj instanceof int[]) {
				in = ((int[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((int[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "int";
					vector.add(vobj);
				}
			} else if(obj instanceof byte[]) {
				in = ((byte[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((byte[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "byte";
					vector.add(vobj);
				}
			} else if(obj instanceof short[]) {
				in = ((short[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((short[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "short";
					vector.add(vobj);
				}
			} else if(obj instanceof float[]) {
				in = ((float[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((float[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "float";
					vector.add(vobj);
				}
			} else if(obj instanceof double[]) {
				in = ((double[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((double[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "double";
					vector.add(vobj);
				}
			} else if(obj instanceof boolean[]) {
				in = ((boolean[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((boolean[])obj)[i]);
					vobj.name = "[" + i + "]";
					vobj.type = "boolean";
					vector.add(vobj);
				}
				//} else {
			} else if(obj instanceof Object[]){
				in = ((Object[])obj).length;
				for(int i = 0; i < in;i++){
					VariableObject vobj = new VariableObject(((Object[])obj)[i]);
					if (vobj.var != null) {
						vobj.name = "[" + i + "]";
						if(vobj.var.getClass().getDeclaredFields().length > 0) {
							vobj.vrs.add(new VariableObject("KartoshkaFreee"));
						}
						vector.add(vobj);
					}
				}
			} else {
			}
		}else {
			Field[] o = obj.getClass().getDeclaredFields();
			for(Field f : o) {
				VariableObject vo = null;
				try {
					vo = new VariableObject(getValueOf(obj, f.getName()));
					vo.name = f.getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
				VariableObject voFree = new VariableObject("KartoshkaFreee");
				voFree.vrs = new Vector<VariableObject>();
				if(vo.var == null) continue;//ATTENSION!!!
				if(f.getType().isArray()) {
					int in = 0;
					if(vo.var instanceof char[]) {
						in = ((char[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof long[]){
						in = ((long[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof Long[]) {
						in = ((Long[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof int[]) {
						in = ((int[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof byte[]) {
						in = ((byte[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof short[]) {
						in = ((short[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof float[]) {
						in = ((float[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof double[]) {
						in = ((double[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else if(vo.var instanceof boolean[]) {
						in = ((boolean[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
						//} else {
					} else if(vo.var instanceof Object[]){
						in = ((Object[])vo.var).length;
						if(in>0)
						vo.vrs.add(voFree);
					} else {
						System.err.println("aaaaaaaaaaa out of Arrays!!!");
					}
					vo.name = f.getName() + " - ["+ in +"]";
					
				} else if(!f.getType().isPrimitive() && vo.var.getClass().getDeclaredFields().length > 0){
					vo.name = f.getName();
					vo.vrs.add(voFree);
				}
				if(vo!=null)
					vector.add(vo);
			}
		}
		
		v.vrs = vector;
		
		return vector;
	}
	
	
	//WorkingOne
	/*
	public Vector<VariableObject> addVector(Object obj, int max){
		Vector<VariableObject> vector = new Vector<VariableObject>();
		//may be must return vector???
		if(obj == null) return null;
		if(max > 5) return null;
		
		Field[] o = obj.getClass().getDeclaredFields();
		for(Field f : o) {
			if(f.getType().isArray()){
				try {
					VariableObject vo = new VariableObject(getValueOf(obj, f.getName()));
					if(vo.var == null)
						return vector;
						
					int in = 0;
					if(vo.var instanceof char[]) {
						in = ((char[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((char[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "char";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof long[]){
						in = ((long[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((long[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "long";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof Long[]) {
						in = ((Long[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((Long[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "Long";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof int[]) {
						in = ((int[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((int[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "int";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof byte[]) {
						in = ((byte[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((byte[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "byte";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof short[]) {
						in = ((short[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((short[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "short";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof float[]) {
						in = ((float[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((float[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "float";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof double[]) {
						in = ((double[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((double[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "double";
							vo.vrs.add(vobj);
						}
					} else if(vo.var instanceof boolean[]) {
						in = ((boolean[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((boolean[])vo.var)[i]);
							vobj.name = "[" + i + "]";
							vobj.type = "boolean";
							vo.vrs.add(vobj);
						}
					//} else {
					} else if(vo.var instanceof Object[]){
						in = ((Object[])vo.var).length;
						for(int i = 0; i < in;i++){
							VariableObject vobj = new VariableObject(((Object[])vo.var)[i]);
							vobj.name = "["+i+"]";
							vobj.type = vobj.getClass().getName();
							//vo.vrs.add(vobj);
							Object fobj = getValueOf(obj, f.getName());
							//vo.vrs = addVector(vobj, max + 1);
							vobj.vrs = addVector(vobj.var, max + 1);
							vo.vrs.add(vobj);
						}
					} else {
						System.err.println("aaaaaaaaaaa out of Arrays!!!");
					}
					vo.name = f.getName() + "-["+ in +"]";
					if(vo!=null)
						vector.add(vo);
				}catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					Object fobj = getValueOf(obj, f.getName());
					if(fobj==null)continue;
					VariableObject vo = new VariableObject(fobj);
					vo.name = f.getName();
					if(!f.getType().isPrimitive())
						vo.vrs = addVector(fobj, max + 1);
					if(vo!=null)
						vector.add(vo);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return vector;
	}*/
	
	/**
	 * получить переменную об\екта переменной
	 */
	public Object getValueOf(Object clazz, String lookingForValue)
    throws Exception {
		Field field = clazz.getClass().getDeclaredField(lookingForValue);
		field.setAccessible(true);
		Class<?> clazzType = field.getType();/*
		if (clazzType.toString().equals("double"))
			return field.getDouble(clazz);
		else if (clazzType.toString().equals("int"))
			return field.getInt(clazz);
		else if (clazzType.toString().equals("boolean"))
			return field.getBoolean(clazz);
		else if (clazzType.toString().equals("byte"))
			return field.getByte(clazz);
		else if (clazzType.toString().equals("char"))
			return field.getChar(clazz);
		else if (clazzType.toString().equals("float"))
			return field.getFloat(clazz);
		else if (clazzType.toString().equals("long"))
			return field.getLong(clazz);
		else if (clazzType.toString().equals("short"))
			return field.getShort(clazz);*/
		//Object toReturn = field.get(clazz);
		return field.get(clazz);
	}
	
	/**
	 * очистить
	 */
	public void deleteFromMap(){
		if(serverUsed){
			varsMap.remove(propTable.getValueAt(row, 0));
			pPanel.updateProps(varsMap);
			currVarsMap = varsMap;
		} else {
			vars.remove(propTable.getValueAt(row, 0));
			pPanel.updateProps(vars);
			currVarsMap = vars;
		}
		pPanel.repaint();
	}
	
	public void deleteAllFromMap(){
		if(serverUsed){
			for (int i = 0; i < propTable.getRowCount(); i++) {
				varsMap.remove(propTable.getValueAt(i, 0));
			}
			pPanel.updateProps(varsMap);
			currVarsMap = varsMap;
		} else {
			for (int i = 0; i < propTable.getRowCount(); i++) {
				vars.remove(propTable.getValueAt(i, 0));
			}
			pPanel.updateProps(vars);
			currVarsMap = vars;
		}
		pPanel.repaint();
	}
	
	/**
	 * Инициализация строкисостаяния
	 */
	public void initStatusBar(){
		statusPanel.addLabel("in stat");
		statusPanel.addSeparator();
        statusPanel.addAnyComponent(view,1);
		statusPanel.addCorner();
	}
	
	public String getExpression() {
		return ex.getExpression();
	}
	
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == servRstItem) {
        	varsMap = new HashMap<String, Object>();
        	pPanel.updateProps(varsMap);
        	return;
        } else if(src == cliRstItem) {
        	vars = new HashMap<String, Object>();
        	pPanel.updateProps(vars);
        	return;
        } else if (src == cancelExec) {
        	if (mode == 0) {
        		krn.stopTerminalThread(terminalThreadId);
        	} else {
        		if (terminalThread != null && terminalThread.isAlive()) {
        			ClientOrLang lng = langsMap.remove(terminalThread.getId());
        			EvaluatorVisitor ev = lng.evalsMap.remove(terminalThread.getId());
        			if (ev != null) {
        				ev.setBreaking(3);
        			}
		    		flag = false;
	        	}
        	}
        	return;
        }
        debuger.clear();
        debuger.debugExpression("", ex.getExpression());
        List<ErrRecord> errors = debuger.getErrors();
        int ans = 0;
        if(errors.size() > 0) 
        	ans = JOptionPane.showConfirmDialog(this,"It may consist an errors! \n Run?!?!?!", "Run???", JOptionPane.YES_NO_OPTION);
        if(ans != 0) return;
		if (src == serverItem || src == runOnServer || src == clientItem || src == runOnClient) {
			finishLabel.setText(finishString);
			flag = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					clientItem.setEnabled(false);
					runOnClient.setEnabled(false);
					serverItem.setEnabled(false);
					runOnServer.setEnabled(false);
					cancelExec.setEnabled(true);
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					final Date start = new Date();
					startLabel.setText(startString + dateFormat.format(start));;
					while (flag) {
						long millis = new Date().getTime() - start.getTime();
						String duration = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
																		  TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
																		  TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
						durationLabel.setText(durationString + duration);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
		        	Date finish = new Date();
					finishLabel.setText(finishString + dateFormat.format(finish));
					clientItem.setEnabled(true);
					runOnClient.setEnabled(true);
					serverItem.setEnabled(true);
					runOnServer.setEnabled(true);
					cancelExec.setEnabled(false);
				}
			}).start();
			if (src == serverItem || src == runOnServer) {
				mode = 0;
				runOnServer();
			} else {
				mode = 1;
				runOnClient();
			}
        }
        pPanel.repaint();
    }
	
    /**
     * делители
     */
	public void placeDivider(){
		mainSplit.setDividerLocation(0.75);
		terSplit.setDividerLocation(0.75);
		propSplit.setDividerLocation(0.6);
		validate();
	}
	
	public DesignerStatusBar getStatusBar() {
		return statusPanel;
	}
	
	public OrGradientMenuBar getMenu() {
		return menuBar;
	}
	
	/**
	 * Выполнить на клиенте
	 */
	public void runOnClient() {
		terminalThread = new Thread() {
    		public void run() {
    			ClientOrLang lng = new ClientOrLang(new EmptyFrame(krn));
    			langsMap.put(this.getId(), lng);
    			lng.evaluate(ex.getExpression(), vars, null, true, new Stack<String>(), this.getId());
	        	pPanel.select.setSelectedItem(res.getString("client"));
	        	currVarsMap = vars;
	        	serverUsed = false;
	    		flag = false;
	            pPanel.repaint();
    		}
    	};
    	terminalThread.start();
	}
	
    public void runOnServer() {
    	terminalThread = new Thread() {
    		public void run() {
    			try {
    				terminalThreadId = krn.execute(ex.getExpression(), varsMap, true);
				} catch (KrnException e) {
					e.printStackTrace();
				}
    		}
    	};
    	terminalThread.start();
    }
    
	public void scriptExecResult(int resultCode, Map<String, Object> varsMap, String message) {
		if (resultCode == 0) {
			// 0 - Успешное выполнение скрипта 
			this.varsMap = (HashMap<String, Object>) varsMap;
	    	pPanel.select.setSelectedItem(res.getString("server"));
	    	currVarsMap = (HashMap<String, Object>) varsMap;
	    	serverUsed = true;
		} else {
			// Ошибка при выполнении скрипта
            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Ошибка при выполнении скрипта." + (message != null && message.length() > 0 ? "\n " + message : ""));
		}
		flag = false;
        pPanel.repaint();
	}
}