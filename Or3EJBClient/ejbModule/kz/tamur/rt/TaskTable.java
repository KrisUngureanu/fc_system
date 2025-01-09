package kz.tamur.rt;

import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF;
import static kz.tamur.comps.Constants.ACT_ALARM;
import static kz.tamur.comps.Constants.ACT_ALERT;
import static kz.tamur.comps.Constants.ACT_ARTICLE;
import static kz.tamur.comps.Constants.ACT_ARTICLE_STRING;
import static kz.tamur.comps.Constants.ACT_AUTO;
import static kz.tamur.comps.Constants.ACT_AUTO_STRING;
import static kz.tamur.comps.Constants.ACT_CANCEL;
import static kz.tamur.comps.Constants.ACT_DIALOG_STRING;
import static kz.tamur.comps.Constants.ACT_ERR;
import static kz.tamur.comps.Constants.ACT_FASTREPORT;
import static kz.tamur.comps.Constants.ACT_IN_BOX;
import static kz.tamur.comps.Constants.ACT_OUT_BOX;
import static kz.tamur.comps.Constants.ACT_PERMIT;
import static kz.tamur.comps.Constants.ACT_SUB_PROC;
import static kz.tamur.comps.Constants.DEFAULT_CURSOR;
import static kz.tamur.comps.Constants.HAND_CURSOR;
import static kz.tamur.comps.Constants.IFC_NOT_ABL;
import static kz.tamur.comps.Constants.MSWORD_EDITOR;
import static kz.tamur.comps.Constants.NAME_RESOURCES;
import static kz.tamur.comps.Constants.SE_UI;
import static kz.tamur.comps.Utils.getCenterLocationPoint;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.guidesigner.MessagesFactory.ERROR_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.INFORMATION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.OPTION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.QUESTION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.showMessageDialog;
import static kz.tamur.guidesigner.MessagesFactory.showOptionDialog;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.ReportLauncher;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.QuickSrvListPanel;
import kz.tamur.guidesigner.QuickSrvPanel;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.SubProcessStateNode;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.Base64;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.SortedFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.tigris.gef.base.CmdAdjustGrid;
import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.client.TaskTableFactory;
import com.cifs.or2.client.TaskTableInterface;
import com.cifs.or2.client.User;
import com.cifs.or2.client.gui.OrMultiLineToolTip;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.server.workflow.definition.EventType;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 01.09.2004
 * Time: 10:10:54
 */
public class TaskTable extends JPanel implements ActionListener, ReportObserver, TaskTableInterface {

    public static final String FLR_DATE_BEGIN = "beginDate";
    public static final String FLR_DATE_END = "endDate";
    public static final String FLR_ROW_BEGIN = "beginRow";
    public static final String FLR_ROW_END = "endRow";

    private ResourceBundle resource = ResourceBundle.getBundle(NAME_RESOURCES, new Locale("ru"));

    private static final Color bgColor = new Color(204, 204, 255);
    private static final Color bgColor2 = new Color(119, 104, 182);
    private static final int CM_ACTIVE_FLOW;
    private static final int CM_SEMAPHORE;
    private static final int CM_PROCESS;
    private static final int CM_OBJECT;
    private static final int CM_TASK;
    private static final int CM_OPEN;
    private static final int CM_VIEW;
    private static final int CM_NEXT;
    private static final int CM_DATE;
    private static final int CM_TIME;
    private static final int CM_CONTROL_DATE;
    private static final int CM_FROM;
    private static final int CM_INITIATOR;
    private static final int CM_DB;
    private static final int CM_KILL;

    private static final String[] COL_NAMES_RU;
    private static final String[] COL_NAMES_KZ;

    static {
        if (SE_UI) {
            COL_NAMES_RU = new String[] { "","", "Документ", "Задача", "", "", "", "Дата", "Время", "Дата контр.", "От кого", "" };
            COL_NAMES_KZ = new String[] { "","", "\u049a\u04b1жат", "Тапсырма", "", "", "", "К\u04afн", "Са\u0493ат",
                    "Тексеру к\u04afні", "Кімнен", "" };
            CM_ACTIVE_FLOW = 0;
            CM_SEMAPHORE = 1;
            CM_PROCESS = -1;
            CM_OBJECT = 2;
            CM_TASK = 3;
            CM_OPEN = 4;
            CM_VIEW = 5;
            CM_NEXT = 6;
            CM_DATE = 7;
            CM_TIME = 8;
            CM_CONTROL_DATE = 9;
            CM_FROM = 10;
            CM_INITIATOR = -1;
            CM_DB = -1;
            CM_KILL = 11;
        } else {
        	if(Kernel.instance().showUserDB()) {
        		COL_NAMES_RU = new String[] { "","", "Процесс", "Объект обработки", "Задача", "", "", "", "Дата", "Время",
        				"Дата контр.", "От кого", "Иниц.процесса", "База", "" };
        		COL_NAMES_KZ = new String[] { "","", "Процесс", "\u04e8\u04a3деу объектісі", "Тапсырма", "", "", "", "К\u04afн",
        				"Са\u0493ат", "Тексеру к\u04afні", "Кімнен", "Процесті\u04a3 баст.", "База", "" };
        		CM_ACTIVE_FLOW = 0;
        		CM_SEMAPHORE = 1;
        		CM_PROCESS = 2;
        		CM_OBJECT = 3;
        		CM_TASK = 4;
        		CM_OPEN = 5;
        		CM_VIEW = 6;
        		CM_NEXT = 7;
        		CM_DATE = 8;
        		CM_TIME = 9;
        		CM_CONTROL_DATE = 10;
        		CM_FROM = 11;
        		CM_INITIATOR = 12;
        		CM_DB = 13;
        		CM_KILL = 14;
        	} else {
        		COL_NAMES_RU = new String[] { "","", "Процесс", "Объект обработки", "Задача", "", "", "", "Дата", "Время",
        				"Дата контр.", "От кого", "Иниц.процесса", "" };
        		COL_NAMES_KZ = new String[] { "","", "Процесс", "\u04e8\u04a3деу объектісі", "Тапсырма", "", "", "", "К\u04afн",
        				"Са\u0493ат", "Тексеру к\u04afні", "Кімнен", "Процесті\u04a3 баст.", "" };
        		CM_ACTIVE_FLOW = 0;
        		CM_SEMAPHORE = 1;
        		CM_PROCESS = 2;
        		CM_OBJECT = 3;
        		CM_TASK = 4;
        		CM_OPEN = 5;
        		CM_VIEW = 6;
        		CM_NEXT = 7;
        		CM_DATE = 8;
        		CM_TIME = 9;
        		CM_CONTROL_DATE = 10;
        		CM_FROM = 11;
        		CM_INITIATOR = 12;
        		CM_DB = -1;
        		CM_KILL = 13;
        	}
        }
    }

    private static final Log clientLog = LogFactory.getLog("ClientLog");
    private static TaskTable taskTable;
    private long langId;
    private long lastRow=0,pgCount=30;
    public LangItem li;
    private HashMap<Long, JGraph> processMap_ = new HashMap<Long, JGraph>();
    private HashMap<Long, Pair<Activity,long[][]>> flowMap_ = new HashMap<Long, Pair<Activity,long[][]>>();
    private Set<Long> autoIfcSet_ = new TreeSet<Long>();
    private long autoIfcFlowId_ = 0;
    private CmdAdjustGrid cmd = new CmdAdjustGrid();
    private int loc = -1;
    private Long selectFlowId;

    private TaskTableModel model;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private Activity aAct = null;
    private Activity uiOpenAct = null;
    private KrnObject openUI = null;
    private boolean isAutoAct = false;
    private boolean isGrafVisible = false;
    private HashMap<Long, Activity> createMap = new HashMap<Long, Activity>();
    private User user;

    private DescTable table;
    private MainFrame.DescLabel counterLabel = kz.tamur.comps.Utils.createDescLabel("");
    private int selRowIdx;
    private int rowCount;
    private MainFrame frame;
    private boolean isRanning = false;
    private JPopupMenu popup;
    private JMenuItem menuItemActiveFlows;
    private JMenuItem menuItemWaitFlows;
    private JMenuItem menuItemResendMessage;
    private JMenuItem menuItemReloadFlow;
    private JMenuItem menuItemQuick = new JMenuItem();
    private JMenuItem menuItemHotKeys = new JMenuItem();
    private DescTableHeader header;
    private TreeSet<Long> reportSet = new TreeSet<Long>();
    private static final boolean taskFilter = "1".equals(System.getProperty("taskFilter"));

    private JLabel filterStatus;
    private JPanel filterPanel;
    private JButton allButton,leftButton,rightButton;
    private Map<String, Object> filterParams;
    private JScrollPane sp;

    private static final DateFormat FMT_DATE = new SimpleDateFormat("dd.MM.yyyy");
    private String searchString = "";
    private int method, currentRow, currentCol;
    private long lastSuperFlowId_ = -1;
    private int currNavi = 0;

    // инициализировать переменные цветом из констант
    private Color alarmColor = Utils.newColor(Utils.getRedColor());
    private Color alertColor = Utils.newColor(Utils.getDarkShadowSysColor());

    private boolean dialogIsActive = false;

    private ActionTask openInterfaceAction = new ActionTask(null, 4);

    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private MainFrame mng;

    public static TaskTable instance(boolean isRunning) {
        if (!isRunning && taskTable == null) {
            taskTable = new TaskTable();
            TaskTableFactory.instance().register(taskTable);
        }
        return taskTable;
    }

    public TaskTable() {
        super();
        setLayout(new BorderLayout(3, 3));
        mng = (MainFrame) InterfaceManagerFactory.instance().getManager();
    }

    public void initTaskTable(MainFrame owner) {
        frame = owner;
        li = frame.getselectedIfcLangItem();
        langId = li.obj.id;
        table = new DescTable();
        // Цвет строки с просроченной датой
        String alarmColor_ = System.getProperty("alarmColor");
        if (alarmColor_ != null) {
            alarmColor = new Color(Integer.valueOf(alarmColor_));
        }

        // Цвет строки с датой внимания
        String alertColor_ = System.getProperty("alertColor");
        if (alertColor_ != null) {
            alertColor = new Color(Integer.valueOf(alertColor_));
        }
        filterParams = new HashMap<String, Object>();
        // Фильтрование заданий
        if (taskFilter) {
            Date date = new Date();
            filterParams.put(FLR_DATE_BEGIN, date);
            filterParams.put(FLR_DATE_END, date);
        }
        try {
            Kernel.instance().setTaskListFilter(filterParams);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        model = new TaskTableModel();
        table.setModel(model);
        init();
        table.valueChanged(new ListSelectionEvent(this, 0, 0, false));
        setLang(langId, false);
        isRanning = true;
        checkActiveflows();
        if (Kernel.instance().getUser().isAdmin()) {
            popup = new JPopupMenu();
            menuItemActiveFlows = new JMenuItem("Обновить статус активности потоков");
            menuItemWaitFlows = new JMenuItem("Сообщения ожидающие ответа");
            menuItemResendMessage = new JMenuItem("Переотправка сообщения");
            menuItemReloadFlow = new JMenuItem("Перегрузка процесса");
            menuItemQuick = new JMenuItem(resource.getString("lastSrv"));
            menuItemHotKeys = new JMenuItem(resource.getString("hotKeys"));
            menuItemActiveFlows.addActionListener(this);
            menuItemWaitFlows.addActionListener(this);
            menuItemResendMessage.addActionListener(this);
            menuItemReloadFlow.addActionListener(this);
            menuItemQuick.addActionListener(this);
            menuItemHotKeys.addActionListener(this);
            popup.add(menuItemQuick);
            popup.add(menuItemHotKeys);
            popup.add(menuItemActiveFlows);
            popup.add(menuItemWaitFlows);
            popup.add(menuItemResendMessage);
            popup.add(menuItemReloadFlow);
        }
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showOperations(e);
            }

            public void mouseReleased(MouseEvent e) {
                showOperations(e);
            }
        });
    }

    public void taskReload(long flowId, long ifsPar) {
        if (isRanning) {
            reloadTasks(flowId, true, ifsPar);
        }
        if (frame != null) {
            frame.doAfterTaskListUpdate();
        }
    }

    public void actionPerformed(ActionEvent e) {
    	Object src=e.getSource();
    	if(src instanceof JMenuItem){
        JMenuItem item = (JMenuItem) e.getSource();
        // Attribute Operations
        if (item == menuItemResendMessage && getSelectedActivity() != null) {
            try {
                Vector<Activity> acts = getSelectedActivitys();
                for (Activity act : acts) {
                    String res = Kernel.instance().resendMessage(act);
                    JFrame frm = (JFrame) getTopLevelAncestor();
                    if (!res.equals("")) {
                        showMessageDialog(frm, INFORMATION_MESSAGE, "Сообщение '" + res + "' передано в транспортную систему.",
                                li);
                    } else {
                        showMessageDialog(frm, ERROR_MESSAGE, "Сообщение не найдено!", li);
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }

        } else if (item == menuItemActiveFlows) {
        	checkActiveflows();
        } else if (item == menuItemWaitFlows) {
            getWaitFlows();
        } else if (item == menuItemQuick) {
            Window cnt = (Window) getTopLevelAncestor();
            quickList(cnt);
        } else if (item == menuItemHotKeys) {
            callHotKeyList();
        } else if (item == menuItemReloadFlow) {
            try {
                Vector<Activity> acts = getSelectedActivitys();
                for (Activity act : acts) {
                    boolean res = Kernel.instance().reloadFlow(act.flowId);
                    if (res) {
                        reloadTasks(act.flowId, false, act.ui.id > 0 && act.infUi.id > 0 ? 2 : act.infUi.id > 0 ? 1 : 0);
                        String text = "Процесс успешно перегружен";
                        JFrame frm = (JFrame) getTopLevelAncestor();
                        showMessageDialog(frm, INFORMATION_MESSAGE, text, li);
                    } else {
                        String text = "Процесс перегрузить не удалось!";
                        JFrame frm = (JFrame) getTopLevelAncestor();
                        showMessageDialog(frm, ERROR_MESSAGE, text, li);
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
        }else if(src==leftButton){
            filterParams.clear();
            filterParams.put(FLR_ROW_END, lastRow>=pgCount?lastRow -= pgCount:0);
            filterParams.put(FLR_ROW_BEGIN, lastRow>=pgCount?lastRow - pgCount + 1:0);
            try {
                Kernel.instance().setTaskListFilter(filterParams);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            model.reload();
            model.fireTableDataChanged();
            table.valueChanged(new ListSelectionEvent(this, 0, 0, false));
            leftButton.setEnabled(lastRow>0);
            rightButton.setEnabled(rowCount==0 || rowCount>=pgCount);
        	
        }else if(src==rightButton){
                filterParams.clear();
                filterParams.put(FLR_ROW_BEGIN, lastRow+(lastRow>0?1:0));
                filterParams.put(FLR_ROW_END, lastRow += pgCount);
                try {
                    Kernel.instance().setTaskListFilter(filterParams);
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
                model.reload();
                model.fireTableDataChanged();
                table.valueChanged(new ListSelectionEvent(this, 0, 0, false));
                leftButton.setEnabled(lastRow>0);
                rightButton.setEnabled(rowCount==0 || rowCount>=pgCount);
        }else if(src==allButton){
            filterParams.clear();
            lastRow=0;
            try {
                Kernel.instance().setTaskListFilter(filterParams);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            model.reload();
            model.fireTableDataChanged();
            table.valueChanged(new ListSelectionEvent(this, 0, 0, false));
            leftButton.setEnabled(true);
            rightButton.setEnabled(true);
        }
    	
    }

    void setGraf(int navi) {
    	currNavi = navi!=0?currNavi+navi:navi;
        if (table.getSelectedRow() >= 0) {
            Activity act_ = getSelectedActivity();
            if (act_ == null || act_.processDefId == null)
                return;
            long[][] nodes = null;
            JGraph graph = null;
            if (act_.processDefId.length > currNavi) {
            	Pair<Activity,long[][]> p=null;
            	if(currNavi>0){
            		p=(act_.superFlowIds!=null? flowMap_.get(act_.superFlowIds[0]):null);
            		int i=currNavi-1;
            		while(i-- > 0) p = (p.first.superFlowIds!=null? flowMap_.get(p.first.superFlowIds[0]):null);
                    if(processMap_.get(new Long(p.first.processDefId[0]))==null){
                    	model.loadProcess(p.first);
                    }
                    graph = (JGraph) processMap_.get(new Long(p.first.processDefId[0]));
                	if(p!=null && p.second!=null)
                		nodes=p.second;
                	else 
                		nodes=new long[0][0];
                }else if(currNavi<0){
                	p =flowMap_.get(act_.subFlowId);
            		int i=currNavi+1;
            		while(i++ < 0) p=flowMap_.get(p.first.subFlowId);
                    if(processMap_.get(new Long(p.first.processDefId[0]))==null){
                    	model.loadProcess(p.first);
                    }
                    graph = (JGraph) processMap_.get(new Long(p.first.processDefId[0]));
                	if(p!=null && p.second!=null)
                		nodes=(long[][])p.second;
                	else 
                		nodes=new long[0][0];
                }else{
                	nodes = act_.nodesId;
                    if(processMap_.get(new Long(act_.processDefId[0]))==null){
                    	model.loadProcess(act_);
                    }
                    graph = (JGraph) processMap_.get(new Long(act_.processDefId[0]));
                }
                selectFlowId = new Long(p!=null?p.first.flowId:act_.flowId);
                mng.setEnabledSubBtn(currNavi>0 ||(currNavi <= 0 && ((p==null && act_.subFlowId>0)|| (p!=null && p.first.subFlowId>0))));
                mng.setEnabledSuperBtn(act_.processDefId.length > currNavi+1);
            }
            if (graph != null && graph.getGraphModel() != null) {
                ((ServiceModel) graph.getGraphModel()).paintTrace(nodes);
            }
            loc = mng.showCurrentProcess(graph, loc);
        } else
            mng.showCurrentProcess(null, loc);
    }

    public void setGrafVisible(boolean isSelected) {
        isGrafVisible = isSelected;
        disposeGraf(0);
    }

    public void disposeGraf(int navi) {
        if (isGrafVisible)
            setGraf(navi);
        else
            loc = mng.showCurrentProcess(null, loc);
    }

    private void setGraphGrid() {
        try {
            for (int i = 0; i < 2; i++) {
                cmd.doIt();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getWaitFlows() {
        byte[] data;
        JTable table = new JTable();
        DefaultTableModel tmodel = new DefaultTableModel();
        String[] columns = new String[] { "Дата-время процесса", "Тип сообщения", "Версия", "БИН,РНН,ОКПО",
                "  Получатель        ", "  Дата-время сообщ  ", "  id_message        ", "  id_producer       ",
                "  id_initiator      ", "  Регистрирующий орган           ", "  Субъект           ", "  Отправитель       ",
                "  Ошибки       ", "Примечание" };
        String[] tags = new String[] { "", "//ct:message_type_name", "", "", "//ct:destination", "//ct:date_time_message",
                "//ct:id_message", "//ct:id_producer", "//ct:id_initiator", "", "", "//ct:sender", "", "" };
        table.setModel(tmodel);
        for (String column : columns) {
            tmodel.addColumn(column);
        }
        for (Object o : model.data) {
            Activity act = (Activity) o;
            String[] row = new String[tags.length];
            JGraph graph = (JGraph) processMap_.get(new Long(act.processDefId[0]));
            StateNode node = null;
            if (graph != null && graph.getGraphModel() != null)
                node = ((ServiceModel) graph.getGraphModel()).getNodesMap().get("" + act.nodesId[0][act.nodesId[0].length - 1]);
            if (!(node instanceof SubProcessStateNode)) {
                String id_p = "";
                try {
                    data = Kernel.instance().getBlob(new KrnObject(act.flowId, "", Kernel.SC_FLOW.id), "variables", 0, 0, -1);
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                    Element root = doc.getRootElement();
                    List<Element> objTags = XPath.selectNodes(root, "//var[@name='XML']");
                    if (objTags.size() > 0 && !objTags.get(0).getText().equals("")) {
                        Document doc1 = builder.build(new ByteArrayInputStream(objTags.get(0).getText().getBytes("UTF-8")),
                                "UTF-8");
                        Element root1 = doc1.getRootElement();
                        for (int i = 0; i < tags.length; ++i) {
                            if (tags[i].equals(""))
                                continue;
                            objTags = XPath.selectNodes(root1, tags[i]);
                            if (objTags.size() > 0) {
                                String item = objTags.get(0).getText();
                                row[i] = item;
                            }

                        }
                        id_p = row[6];
                        objTags = XPath.selectNodes(root1, "//BIN");
                        row[3] = "";
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[3] += "BIN:" + item + ";";
                        }
                        objTags = XPath.selectNodes(root1, "//RNN");
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[3] += "RNN:" + item + ";";
                        }
                        objTags = XPath.selectNodes(root1, "//OKPO");
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[3] += "OKPO:" + item + ";";
                        }
                        objTags = XPath.selectNodes(root1, "//RegAuth");
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getChild("Code").getText();
                            String item1 = "";
                            if (objTags.get(0).getChild("Name") != null)
                                item1 = objTags.get(0).getChild("Name").getText();
                            row[tags.length - 5] = item + ":" + item1;
                        }
                        objTags = XPath.selectNodes(root1, "//Version");
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[2] = item;
                        }
                        objTags = XPath.selectNodes(root1, "//Error");
                        row[tags.length - 2] = "";
                        if (objTags.size() > 0) {
                            for (Element objt : objTags) {
                                if (objt.getChild("Code") != null) {
                                    String item = objt.getChild("Code").getText();
                                    String item1 = objt.getChild("Specification").getText();
                                    row[tags.length - 2] += item + ":" + item1 + ";";

                                } else if (objt.getChild("Type") != null) {
                                    Element e = (Element) objt.getChildren("Type").get(0);
                                    String item = ((Element) e.getContent().get(0)).getText();
                                    String item1 = ((Element) e.getContent().get(1)).getText();
                                    String item2 = objt.getChild("Specification").getText();
                                    row[tags.length - 2] += item + ":" + item1 + ";" + item2 + ";";

                                }
                            }
                        }
                        objTags = XPath.selectNodes(root1, "//note");
                        if (objTags.size() > 0) {
                            for (Element objt : objTags) {
                                String item = "note";
                                String item1 = objt.getText();
                                row[tags.length - 2] += item + ":" + item1 + ";";
                            }
                        }
                    }
                    objTags = XPath.selectNodes(root, "//var[@name='tagLegalPerson']");
                    if (objTags.size() > 0 && !objTags.get(0).getText().equals("")) {
                        Document doc1 = builder.build(new ByteArrayInputStream(objTags.get(0).getText().getBytes("UTF-8")),
                                "UTF-8");
                        Element root1 = doc1.getRootElement();
                        objTags = XPath.selectNodes(root1, "//RegAuth");
                        if ((row[tags.length - 5] == null || row[tags.length - 5].equals("")) && objTags.size() > 0) {
                            String item = "", item1 = "";
                            if (objTags.get(0).getChild("Code") != null)
                                item = objTags.get(0).getChild("Code").getText();
                            if (objTags.get(0).getChild("Name") != null)
                                item1 = objTags.get(0).getChild("Name").getText();
                            row[tags.length - 5] = item + ":" + item1;
                        }
                        objTags = XPath.selectNodes(root1, "//Version");
                        if ((row[2] == null || row[2].equals("")) && objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[2] = item;
                        }
                    } else {
                        objTags = XPath.selectNodes(root, "//var[@name='personTag_']");
                        if (objTags.size() > 0 && !objTags.get(0).getText().equals("")) {
                            Document doc1 = builder.build(new ByteArrayInputStream(objTags.get(0).getText().getBytes("UTF-8")),
                                    "UTF-8");
                            Element root1 = doc1.getRootElement();
                            objTags = XPath.selectNodes(root1, "//TaxAuth");
                            if ((row[tags.length - 5] == null || row[tags.length - 5].equals("")) && objTags.size() > 0) {
                                String item = objTags.get(0).getChild("Code").getText();
                                String item1 = objTags.get(0).getChild("Name").getText();
                                row[tags.length - 5] = item + ":" + item1;
                            }
                            objTags = XPath.selectNodes(root1, "//Version");
                            if ((row[2] == null || row[2].equals("")) && objTags.size() > 0) {
                                String item = objTags.get(0).getText();
                                row[2] = item;
                            }
                        }
                    }
                    if (act.date != null) {
                        row[0] = dateFormat.format(Funcs.convertTime(act.date));
                    }
                    objTags = XPath.selectNodes(root, "//var[@name='RETURN']");
                    if (objTags.size() > 0 && objTags.get(0).getAttributeValue("type").equals("string")) {
                        String item = objTags.get(0).getText();
                        row[tags.length - 4] = item;
                    } else {
                        objTags = XPath.selectNodes(root, "//var[@name='title_']");
                        if (objTags.size() > 0) {
                            String item = objTags.get(0).getText();
                            row[tags.length - 4] = item;
                        }
                    }
                    row[tags.length - 1] = act.titles[0];
                    if ((act.param & ACT_IN_BOX) != ACT_IN_BOX) {
                        row[tags.length - 2] = act.titles[1];
                    }
                    tmodel.addRow(row);
                } catch (KrnException e) {
                    e.printStackTrace();
                    System.out.println(">>>" + id_p);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(">>>" + id_p);
                }
            }
        }
        JScrollPane debugPane;
        debugPane = new JScrollPane(table);
        debugPane.setOpaque(isOpaque);
        debugPane.getViewport().setOpaque(isOpaque);
        mng.setCursor(DEFAULT_CURSOR);
        DesignerDialog dlg = new DesignerDialog(mng, "Потоки ожидающие ответа", false, debugPane);
        dlg.setLanguage(langId);
        dlg.setSize(700, 550);
        dlg.setLocation(getCenterLocationPoint(dlg.getSize()));
        dlg.show();
    }

    public void getDebugPane() {
        if (table.getSelectedRow() >= 0) {
            try {
                boolean isDebug = (System.getProperty("Flow") != null && System.getProperty("Flow").equals("debug"));
                byte[] data;
                long flow_id = isGrafVisible ? selectFlowId.longValue() : getSelectedActivity().flowId;
                long tr_id = isGrafVisible ? flowMap_.get(flow_id).first.trId : getSelectedActivity().trId;
                if (isDebug) {
                    data = Kernel.instance().getBlob(new KrnObject(flow_id, "", Kernel.SC_FLOW.id), "debug", 0, 0, -1);
                    if (data.length == 0) {
                        data = Kernel.instance().getBlob(new KrnObject(flow_id, "", Kernel.SC_FLOW.id), "variables", 0, 0, -1);
                        isDebug = false;
                    }
                } else
                    data = Kernel.instance().getBlob(new KrnObject(flow_id, "", Kernel.SC_FLOW.id), "variables", 0, 0, -1);
                JScrollPane debugPane;
                if (data.length > 0) {
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                    Element root = doc.getRootElement();
                    DebugTree tree = new DebugTree(root, flow_id, tr_id, isDebug);
                    debugPane = new JScrollPane(tree);
                } else {
                    debugPane = new JScrollPane();
                }
                mng.setCursor(DEFAULT_CURSOR);
                DesignerDialog dlg = new DesignerDialog(mng, "Состояние переменных процесса", false, debugPane);
                dlg.setLanguage(langId);
                dlg.setSize(700, 550);
                dlg.setLocation(getCenterLocationPoint(dlg.getSize()));
                dlg.show();
            } catch (KrnException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Activity getSelectedActivity() {
        return model.getSelectedActivity(table.getSelectedRow());
    }

    public Activity getActivityById(long flowId) {
        return model.getActivity(flowId);
    }

    public Vector<Activity> getSelectedActivitys() {
        return model.getSelectedActivitys(table.getSelectedRows());
    }

    public void setSelectedActivity(long flowId) {
        for (int i = 0; i < model.data.size(); ++i) {
            if (((Activity) model.data.get(i)).flowId == flowId) {
                table.setRowSelectionInterval(i, i);
                MoveTableView(i);
                return;
            }
        }
    }

    void reloadTasks(long flowId, boolean isStartAutoAct, long ifsPar) {
        model.reloadActivity(flowId, isStartAutoAct, ifsPar);
        Activity act = getSelectedActivity();
        if(aAct!=null && model.getActivity(aAct.flowId)==null)
        	aAct=null;
        if (isGrafVisible) {
            if (act != null && act.flowId == flowId)
                setGraf(0);
            else if (act == null)
                setGrafVisible(false);
        }
        if ((isStartAutoAct && aAct != null && !isAutoAct()) /*&& (act==null || act.autoIfc)*/) {
            if (!aAct.autoIfc) {
                setAutoAct(true);
            }
            if (model.data.contains(aAct) && (openUI == null || !openUI.equals(aAct.ui) || (aAct.transitions != null && aAct.transitions.length > 0))) {
                System.out.println("Запуск автоинтерфейса: userId="+user.getObject().getId()+"; actorId="+aAct.actorId);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        startAutoIfc();
                    }
                });
            } else {
            //    aAct = null;
                setAutoAct(false);
            }
        }
        if(rowCount!= model.data.size()){
        	rowCount= model.data.size();
        	setCounterText();
        }
    }

    private void startAutoIfc() {
        JFrame frm = (JFrame) getTopLevelAncestor();
        try {
            aAct.autoIfc = !Application.instance().isMonitorTask();
            this.setSelectedActivity(aAct.flowId);
            Object res_open=Kernel.instance().openInterface(aAct.ui.id,aAct.flowId,aAct.trId,aAct.processDefId.length>0?aAct.processDefId[0]:-1);
            if (res_open==null || (res_open instanceof Number && ((Number)res_open).intValue() == 1)) {
	           if (!Application.instance().isMonitorTask() && frm != null) {
	                CursorToolkit.startWaitCursor(frm);
	            }
	            boolean isDialog = ACT_DIALOG_STRING.equals(aAct.uiType) || ACT_AUTO_STRING.equals(aAct.uiType);
	            if ((autoIfcFlowId_ == aAct.flowId || aAct.rootFlowId == autoIfcFlowId_|| Funcs.indexOf(autoIfcFlowId_, aAct.superFlowIds) != -1) && (!aAct.autoIfc || !isDialog)) {
	                openUI = aAct.ui;
	                setAutoAct(true);
	                openUI(aAct);
	            } else if (isDialog){
	                openUI = aAct.ui;
	                UIFrame c = mng.getInterface(aAct.ui, (aAct.objs.length > 0 || aAct.uiFlag == 1) ? aAct.objs : null, aAct.trId,
	                        InterfaceManager.SERVICE_MODE, aAct.flowId, false, false);
	                DesignerDialog dlg = new DesignerDialog(frm, c.getPanel().getTitle(), (aAct.param & ACT_CANCEL) == ACT_CANCEL,
	                        (OrPanel) c.getPanel());
	                dlg.setLanguage(langId);
	                dlg.setSize(c.getPanel().getPrefSize());
	                dlg.setLocation(getCenterLocationPoint(dlg.getSize()));
	                int res = 0;
	                while (res == 0) {
	                    dlg.show();
	                    if (aAct == null) {
	                        break;
	                    }
	                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
	                        OrRef ref = c.getRef();
	                        ReqMsgsList msg = ref.canCommit();
	                        int errors = msg.getListSize();
	                        if (errors > 0) {
	                            SortedFrame sdlg = new SortedFrame(frm, resource.getString("errors"));
	                            msg.setParent(sdlg);
	                            sdlg.setOption(new String[] { resource.getString("continue"), resource.getString("exit") });
	                            sdlg.setContent(msg);
	                            sdlg.setLocation(getCenterLocationPoint(sdlg.getSize()));
	                            sdlg.show();
	                            res = sdlg.getResult();
	                        }
	                        if (errors == 0) {
	                            if (errors == 0)
	                                res = 1;
	                            if (res != 1 || (res == 1 && errors == 0)) {
	                                c.getCash().commit(aAct.flowId);
	                                c.getRef().commitChanges(null);
	                                // 8.10.05 Vital
	                                if (c.getRef().getItems(c.getRef().getLangId()) != null
	                                        && c.getRef().getItems(c.getRef().getLangId()).size() != 0
	                                        && c.getRef().getSelectedItems().size() == 0) {
	                                    c.getRef().setSelectedItems(new int[] { 0 });
	                                }
	                                //
	                                List<OrRef.Item> a_sel = c.getRef().getSelectedItems();
	                                if (a_sel.size() == 0) {
	                                    String text = resource.getString("checkObjectMessage");
	                                    showMessageDialog(frm, ERROR_MESSAGE, text, li);
	                                    res = 0;
	                                    continue;
	                                }
	                                KrnObject[] selObjs = new KrnObject[a_sel.size()];
	                                for (int i = 0; i < a_sel.size(); i++) {
	                                    OrRef.Item item = a_sel.get(i);
	                                    selObjs[i] = (KrnObject) item.getCurrent();
	                                }
	                                int result_ = -1;
	                                String res_ = "";
	                                if (aAct.transitions.length > 1) {
	                                    String[] trs = new String[aAct.transitions.length];
	                                    for (int i = 0; i < trs.length; ++i) {
	                                        trs[i] = aAct.transitions[i].substring(0, aAct.transitions[i].indexOf(";"));
	                                    }
	                                    result_ = showOptionDialog(frm, OPTION_MESSAGE, trs, li);
	                                    if (result_ == -1) {
	                                        break;
	                                    } else {
	                                        for (int i = 0; i < trs.length; ++i) {
	                                            if (result_ == i) {
	                                                res_ = aAct.transitions[i].substring(aAct.transitions[i].lastIndexOf(";") + 1);
	                                                break;
	                                            }
	                                        }
	                                    }
	                                }
	                                CursorToolkit.startWaitCursor(frm == null ? this : frm);
	                                if (Kernel.instance().setSelectedObjects(aAct.flowId,
	                                        aAct.nodesId[0][aAct.nodesId[0].length - 1], selObjs)) {
	                                    Kernel.instance().setPermitPerform(aAct.flowId, true);
	                                    String[] res_s = Kernel.instance().performActivitys(new Activity[] { getSelectedActivity() },
	                                            res_);
	                                    if (res_s.length > 0) {
	                                        if (res_s.length == 1 && res_s[0].equals("synch")) {
	                                            setAutoIfcFlowId_(aAct.flowId);
	                                        } else {
	                                            // обработка ошибок
	                                            String msg_ = res_s[0];
	                                            for (int i = 1; i < res_s.length; ++i)
	                                                msg_ += "\n" + res_s[i];
	                                            showMessageDialog(frm, ERROR_MESSAGE, msg_);
	                                            res = 0;
	                                            continue;
	                                        }
	                                    }
	                                    autoIfcSet_.add(aAct.flowId);
	                                    aAct.param |= IFC_NOT_ABL;
	                                    aAct.ui.id = -1;
	                                    aAct.infUi.id = -1;
	                                    if ((aAct.param & ACT_PERMIT) == ACT_PERMIT)
	                                        aAct.param ^= ACT_PERMIT;
	
	                                    if (res_s.length == 1 && res_s[0].equals("synch")) {
	                                        reloadTasks(aAct.flowId, true, aAct.ui.id > 0 && aAct.infUi.id > 0 ? 2
	                                                : aAct.infUi.id > 0 ? 1 : 0);
	                                    }
	                                }
	                                if (isGrafVisible) {
	                                    setGraf(0);
	                                }
	                                repaint();
	                            } else {
	                                res = 0;
	                            }
	                        } else if ((aAct.param & ACT_CANCEL) == ACT_CANCEL && res == ButtonsFactory.BUTTON_CANCEL) {
	                            Kernel.instance().cancelProcess(aAct.flowId, aAct.msg, false, true);
	                            repaint();
	                            break;
	                        }
	                    } else if ((aAct.param & ACT_CANCEL) == ACT_CANCEL && dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
	                        Kernel.instance().cancelProcess(aAct.flowId, aAct.msg, false, true);
	                        repaint();
	                        break;
	                    } else {
	                        res = 1;
	                        break;
	                    }
	                }
	                mng.releaseInterface(false);
	
	            }
	            if (frm != null) {
	                CursorToolkit.stopWaitCursor(frm);
	            }
            }
       } catch (KrnException e) {
            if (frm != null) {
                CursorToolkit.stopWaitCursor(frm);
            }
            e.printStackTrace();
        } finally {
            if (aAct != null && !aAct.autoIfc) {
                 aAct = null;
                setAutoAct(false);
                // autoIfcFlowId_ = 0;
            }
        }
    }

    private void startIfcDialog() {
        Activity act_ = getSelectedActivity();
        try {
            JFrame frm = (JFrame) getTopLevelAncestor();
            UIFrame c = mng.getInterface(act_.ui, (act_.objs.length > 0 || act_.uiFlag == 1) ? act_.objs : null, act_.trId,
                    InterfaceManager.SERVICE_MODE, act_.flowId, false, false);
            DesignerDialog dlg = new DesignerDialog(frm, c.getPanel().getTitle(), (Component) c.getPanel());
            dlg.setLanguage(langId);
            CursorToolkit.stopWaitCursor(frm);
            int res = 0;
            while (res == 0) {
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    OrRef ref = c.getRef();
                    ReqMsgsList msg = ref.canCommit();
                    int errors = msg.getListSize();
                    if (errors > 0) {
                        SortedFrame sdlg = new SortedFrame(frm, resource.getString("errors"));
                        msg.setParent(sdlg);
                        sdlg.setOption(new String[] { resource.getString("continue"), resource.getString("exit") });
                        sdlg.setContent(msg);
                        sdlg.setLocation(getCenterLocationPoint(sdlg.getSize()));
                        dlg.show();
                        res = dlg.getResult();
                    }
                    if (res != BUTTON_NOACTION && res == BUTTON_OK || errors == 0) {
                        res = 1;
                        c.getCash().commit(act_.flowId);
                        c.getRef().commitChanges(null);
                        if (msg.hasFatalErrors()) {
                            Kernel.instance().setPermitPerform(act_.flowId, false);
                            // act_.param |= ACT_PERMIT;
                        } else {
                            Kernel.instance().setPermitPerform(act_.flowId, true);
                            act_.param |= ACT_PERMIT;
                            reloadTasks(act_.flowId, false, act_.ui.id > 0 && act_.infUi.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : 0);
                        }
                        List<OrRef.Item> a_sel = c.getRef().getSelectedItems();
                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
                        for (int i = 0; i < a_sel.size(); i++) {
                            OrRef.Item item = (OrRef.Item) a_sel.get(i);
                            selObjs[i] = (KrnObject) item.getCurrent();
                        }
                        CursorToolkit.startWaitCursor(frm);
                        Kernel.instance().setSelectedObjects(act_.flowId, act_.nodesId[0][act_.nodesId[0].length - 1], selObjs);
                        repaint();
                    }
                } else {
                    res = 1;
                    break;
                }
            }
            mng.releaseInterface(false);

        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        JPanel counter = new JPanel();
        counter.setLayout(new BorderLayout(3, 3));
        if (SE_UI) {
            counter.setBackground(bgColor2);
            counterLabel.setForeground(Color.WHITE);
        }

        setCounterText();

        header = new DescTableHeader(table.getColumnModel());
        table.setTableHeader(header);

        // activeFlows
        TableColumn tc = table.getColumnModel().getColumn(CM_ACTIVE_FLOW);
        tc.setCellRenderer(new TaskCellRenderer());
        TaskCellEditor tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        // warning
        tc = table.getColumnModel().getColumn(CM_SEMAPHORE);
        tc.setCellRenderer(new TaskCellRenderer());
        tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        // Заголовок
        tc = table.getColumnModel().getColumn(CM_OBJECT);
        tc.setPreferredWidth(160);
        tc.setMinWidth(50);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());

        // Задача
        tc = table.getColumnModel().getColumn(CM_TASK);
        tc.setPreferredWidth(100);
        tc.setMinWidth(50);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());

        tc = table.getColumnModel().getColumn(CM_OPEN);
        tc.setCellRenderer(new TaskCellRenderer());
        tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc = table.getColumnModel().getColumn(CM_VIEW);
        tc.setCellRenderer(new TaskCellRenderer());
        tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc = table.getColumnModel().getColumn(CM_NEXT);
        tc.setCellRenderer(new TaskCellRenderer());
        tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        // Дата
        tc = table.getColumnModel().getColumn(CM_DATE);
        tc.setPreferredWidth(60);
        tc.setMaxWidth(60);
        tc.setMinWidth(30);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());
        // Время
        tc = table.getColumnModel().getColumn(CM_TIME);
        tc.setPreferredWidth(50);
        tc.setMaxWidth(50);
        tc.setMinWidth(30);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());
        // Дата контроля
        tc = table.getColumnModel().getColumn(CM_CONTROL_DATE);
        tc.setPreferredWidth(70);
        tc.setMaxWidth(70);
        tc.setMinWidth(30);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());

        // От кого
        tc = table.getColumnModel().getColumn(CM_FROM);
        tc.setPreferredWidth(150);
        tc.setMaxWidth(300);
        tc.setMinWidth(50);
        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
        }
        tc.setCellRenderer(new TaskCellRenderer());

        // Удаление процесса
        tc = table.getColumnModel().getColumn(CM_KILL);
        tc.setCellRenderer(new TaskCellRenderer());
        tce = new TaskCellEditor();
        tce.setClickCountToStart(1);
        tc.setCellEditor(tce);
        tc.setPreferredWidth(20);
        tc.setMaxWidth(20);
        tc.setMinWidth(20);

        if (!SE_UI) {
            tc.setHeaderRenderer(createHeader());
            // Инициатор процесса
            tc = table.getColumnModel().getColumn(CM_INITIATOR);
            tc.setPreferredWidth(100);
            tc.setMaxWidth(300);
            tc.setMinWidth(50);
            tc.setHeaderRenderer(createHeader());
            tc.setCellRenderer(new TaskCellRenderer());

            // Заголовок
            tc = table.getColumnModel().getColumn(CM_TASK);
            tc.setPreferredWidth(200);
            tc.setMinWidth(50);
            tc.setHeaderRenderer(createHeader());
            tc.setCellRenderer(new TaskCellRenderer());

            // Суперпроцесс
            tc = table.getColumnModel().getColumn(CM_PROCESS);
            tc.setPreferredWidth(260);
            tc.setMinWidth(50);
            tc.setHeaderRenderer(createHeader());
            tc.setCellRenderer(new TaskCellRenderer());

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        if(Kernel.instance().showUserDB()) {
        	tc = table.getColumnModel().getColumn(CM_DB);
        	tc.setPreferredWidth(120);
        	tc.setMaxWidth(300);
        	tc.setMinWidth(50);
        	tc.setHeaderRenderer(createHeader());
        	if (!SE_UI) {
        		tc.setHeaderRenderer(createHeader());
        	}  
        	tc.setCellRenderer(new TaskCellRenderer());  
        }
        
        JTableHeader header = table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        add(counter, BorderLayout.NORTH);
        counter.add(counterLabel, BorderLayout.EAST);
        counterLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        // Фильтрование заданий

        filterStatus = new JLabel();
        filterPanel = new JPanel();
        leftButton = new JButton("<");
        allButton = new JButton("||");
        rightButton = new JButton(">");
        leftButton.addActionListener(this);
        allButton.addActionListener(this);
        rightButton.addActionListener(this);
        leftButton.setEnabled(false);

        filterPanel.setLayout(new FlowLayout());
        if (taskFilter) {
            filterStatus.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        readFilterParams();
                    }
                }

            });
            filterPanel.add(filterStatus);
            filterPanel.add(leftButton);
            filterPanel.add(allButton);
            filterPanel.add(rightButton);
            counter.add(filterPanel, BorderLayout.WEST);
            updateFilterStatus();
        }

        sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        if (SE_UI) {
            sp.getViewport().setBackground(bgColor);
            sp.setBackground(bgColor);
            table.getTableHeader().setBackground(bgColor);
        }

        revalidate();
    }

    protected void MoveTableView(int i) {
        JViewport vp = sp.getViewport();
        Point p = vp.getViewPosition();
        int lastRow = vp.getHeight() / table.getRowHeight() - 1;
        p.y = Math.max(0, (i - lastRow) * table.getRowHeight());
        vp.setViewPosition(p);
    }

    private String getMultiLine(String src) {
        String strResultCurrent = "";
        strResultCurrent = src.replaceAll("\\\\n", "\n");
        return strResultCurrent.trim();
    }

    private TableCellRenderer createHeader() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(Utils.getLightSysColor());
                        setBackground(Utils.getDarkShadowSysColor());
                        setFont(Utils.getDefaultFont());
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                setText(value.toString());
                repaint();
                return this;
            }
        };
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
                model.setSortAcs(!model.isSortAcs());
            } else {
                model.setSortColumn(modelIndex);
            }
            for (int i = 0; i < model.getColumnCount(); i++) {
                TableColumn column = colModel.getColumn(i);
                int index = column.getModelIndex();
                JLabel renderer = (JLabel) column.getHeaderRenderer();
                if (renderer != null)
                    renderer.setIcon(model.getColumnIcon(index));
            }
            table.getTableHeader().repaint();
            Activity act_ = null;
            if (table.getSelectedRow() > -1)
                act_ = getSelectedActivity();
            model.sortData();
            table.tableChanged(new TableModelEvent(model));
            if (act_ != null) {
                int index = model.data.indexOf(act_);
                if (index > -1)
                    table.setRowSelectionInterval(index, index);
            }
            repaint();
        }
    }

    class TaskTableModel extends AbstractTableModel {

        private Vector<Activity> data;
        // private ActivityComparator actComp = new ActivityComparator();
        private Kernel krn = Kernel.instance();
        private boolean isSortAcs = true;
        private int sortColumn = CM_DATE;

        public final ImageIcon SORT_UP = kz.tamur.rt.Utils.getImageIcon("SortUpLight");
        public final ImageIcon SORT_DOWN = kz.tamur.rt.Utils.getImageIcon("SortDownLight");

        public TaskTableModel() {
            reload();
        }

        public void reload() {
            try {
                data = new Vector<Activity>();
                user = krn.getUser();
                Activity[] data_ = krn.getTaskList();
                List<Activity> data_v = new Vector();
                for (int i = 0; i < data_.length; ++i) {
                    if (data_[i] == null || data_[i].flowId < 0)
                        continue;
                    data.add(data_[i]);
                    flowMap_.put(new Long(data_[i].flowId), new Pair<Activity,long[][]>(data_[i], data_[i].nodesId));
                    if (data_[i].superFlowIds!=null)
                        data_v.add(data_[i]);
                }
                for(Activity as:data_v){
                	Pair<Activity,long[][]> ps = (as.superFlowIds!=null ? flowMap_.get(as.superFlowIds[0]):null);
                	if(ps!=null) ps.first.subFlowId=as.flowId;
                }
                if (data_.length > 0)
                    sortData();
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        public Icon getColumnIcon(int column) {
            if (column == sortColumn)
                return isSortAcs ? SORT_UP : SORT_DOWN;
            return null;
        }

        public boolean isSortAcs() {
            return isSortAcs;
        }

        public void setSortAcs(boolean sortAcs) {
            isSortAcs = sortAcs;
        }

        public int getSortColumn() {
            return sortColumn;
        }

        public void setSortColumn(int sortColumn) {
            this.sortColumn = sortColumn;
        }

        public void reloadActivity(long flowId, boolean isStartAutoAct, long ifsPar) {
            try {
                if (flowId <= 0) {
                    return;
                }
                Activity act = getActivity(flowId);
                Activity act_ = krn.getTask(flowId, ifsPar, true, true);
                int i = act==null?-1:data.indexOf(act);
                if (act_.flowId == -1) {
                    act_ = new Activity();
                    act_.flowId = flowId;
                    act_.autoIfc = !Application.instance().isMonitorTask();
                    if (act != null) {
                        act = (Activity) data.get(i);
                        data.remove(i);
                        fireTableRowsDeleted(i, i);
                        Long fId = new Long(act.flowId);
                        if (flowMap_.containsKey(fId))
                            flowMap_.remove(fId);
                        act.article = null;
                        act.articleLang = null;
                        act = null;
                    }
                } else if (act != null) {
                    act = (Activity) data.get(i);
                    act.actorId = act_.actorId;
                    act.titles = act_.titles;
                    act.nodesId = act_.nodesId;
                    act.userFrom = act_.userFrom;
                    act.userInit = act_.userInit;
                    act.userBase_name = act_.userBase_name;
                    act.param = act_.param;
                    act.setUI(act_.ui);
                    act.uiType = act_.uiType;
                    act.objs = act_.objs;
                    act.uiFlag = act_.uiFlag;
                    act.infUi = act_.infUi;
                    act.uiTypeInf = act_.uiTypeInf;
                    act.infObjs = act_.infObjs;
                    act.date = act_.date;
                    act.controlDate = act_.controlDate;
                    act.msg = act_.msg;
                    act.trId = act_.trId;
                    act.article = act_.article;
                    act.articleLang = act_.articleLang;
                    act.color = act_.color;
                    act.autoIfc = act_.autoIfc;
                    if (act.processDefId == null) {
                        createMap.remove(new Long(act.flowId));
                        act.processDefId = act_.processDefId;
                    }
                    if (isStartAutoAct && autoIfcSet_.contains(act.flowId) && aAct == null && (act.param & ACT_AUTO) == ACT_AUTO) {
                        aAct = act;
                    }
                    act.transitions = act_.transitions;
                    if (aAct != null) {
                        aAct.autoIfc = !Application.instance().isMonitorTask();
                    }
                    act.timeActive=act_.timeActive;
                } else if (act_ != null) {
                    data.add(act_);

                    Set<String> uids = new HashSet<String>();
                    Map<Long, Element> confXmls = new HashMap<Long, Element>();
                    for (int j = 0; j < act_.processDefId.length; ++j) {
                        long pid = act_.processDefId[j];
                        if (!processMap_.containsKey(pid)) {
                            confXmls.put(pid, preloadProcessUids(pid, uids));
                        }
                    }
                    krn.addToCache(uids);
                    for (int j = 0; j < act_.processDefId.length; ++j) {
                        long pid = act_.processDefId[j];
                        if (!processMap_.containsKey(pid)) {
                            loadProcess(pid, confXmls.get(pid));
                        }
                    }

                    fireTableRowsInserted(data.size() - 1, data.size() - 1);
                    flowMap_.put(act_.flowId, new Pair<Activity,long[][]>(act_,act_.nodesId));
                    if (aAct == null /*&& autoIfcSet_.contains(act_.flowId)*/ && (act_.param & ACT_AUTO) == ACT_AUTO) {
                    	if(frame.getUser().getUserSign().equals(act_.userFrom))//Предотвратить открытие интерфейса у другого пользователя
                    		aAct = act_;
                    }
                    act = act_;
                    i = data.size() - 1;
                    if (lastSuperFlowId_ > -1 && act_.processDefId.length > 1 && act_.processDefId[1] == lastSuperFlowId_) {
                        setSelectedActivity(act_.flowId);
                        lastSuperFlowId_ = -1;
                    }
                }
                if (act != null && (act.flowId == autoIfcFlowId_ || act.rootFlowId == autoIfcFlowId_ || Funcs.indexOf(autoIfcFlowId_, act.superFlowIds) != -1)) {
                    aAct = act;
                }
                if (act != null && autoIfcSet_.contains(act.flowId))
                    autoIfcSet_.remove(act.flowId);
                if (act != null && act.processDefId != null) {

                    Set<String> uids = new HashSet<String>();
                    Map<Long, Element> confXmls = new HashMap<Long, Element>();
                    for (int j = 0; j < act.processDefId.length; ++j) {
                        long pid = act_.processDefId[j];
                        if (!processMap_.containsKey(pid)) {
                            confXmls.put(pid, preloadProcessUids(pid, uids));
                        }
                    }
                    krn.addToCache(uids);
                    for (int j = 0; j < act.processDefId.length; ++j) {
                        long pid = act_.processDefId[j];
                        if (!processMap_.containsKey(pid)) {
                            loadProcess(pid, confXmls.get(pid));
                        }
                    }

                    JGraph graph_ = (JGraph) processMap_.get(new Long(act.processDefId[0]));
                    ServiceModel m = (ServiceModel) graph_.getGraphModel();
                    m.setLang(langId, graph_);
                    if ((act.titles[0] == null || act.titles[0].equals("")) && act.nodesId != null && act.nodesId.length > 0
                            && act.nodesId[0].length > 0) {
                        act.titles[0] = m.getNodeName(act.nodesId[0][act.nodesId[0].length - 1]);
                    }
                    if (act.transitions.length > 1) {
                        for (int k = 0; k < act.transitions.length; ++k) {
                            String trs = act.transitions[k].substring(act.transitions[k].indexOf(",") + 1,
                                    act.transitions[k].lastIndexOf(","));
                            String trs_to = act_.transitions[k].substring(act.transitions[k].lastIndexOf(",") + 1);
                            act.transitions[k] = m.getEdgeName(Long.valueOf(trs), langId) + "," + trs + "," + trs_to;
                        }
                    }
                }
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Activity getActivity(long flowId) {
            int sz = data.size();
            for (int i = 0; i < sz; ++i) {
                Activity a = data.get(i);
                if (a.flowId == flowId) {
                    return a;
                }
            }
            return null;
        }
        
        public void loadProcess() {
            // Предварительная загрузка объектов в процессах
            Set<String> uids = new HashSet<String>();
            Map<Long, Element> confXmls = new HashMap<Long, Element>();
            for (int i = 0; i < data.size(); ++i) {
                Activity act = data.get(i);
                for (int j = 0; j < act.processDefId.length; ++j) {
                    long pid = act.processDefId[j];
                    if (!processMap_.containsKey(pid)) {
                        confXmls.put(pid, preloadProcessUids(pid, uids));
                    }
                }
            }
            Kernel krn = Kernel.instance();
            try {
                krn.addToCache(uids);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // Загрузка процессов
            for (int i = 0; i < data.size(); ++i) {
                Activity act = data.get(i);
                if (act == null || act.processDefId == null)
                    continue;
                // Загрузка самих процессов
                for (int j = 0; j < act.processDefId.length; ++j) {
                    long pid = act.processDefId[j];
                    loadProcess(pid, confXmls.get(pid));
                }
                JGraph graph_ = (JGraph) processMap_.get(new Long(act.processDefId[0]));
                ServiceModel m = (ServiceModel) graph_.getGraphModel();
                m.setLang(langId, graph_);
                if (act != null && (act.titles[0] == null || act.titles[0].length() == 0) && act.nodesId != null && act.nodesId.length > 0
                        && act.nodesId[0].length > 0) {
                    act.titles[0] = m.getNodeName(act.nodesId[0][act.nodesId[0].length - 1]);
                }
                if (act.transitions.length > 1) {
                    for (int k = 0; k < act.transitions.length; ++k) {
                        String trs = act.transitions[k].substring(act.transitions[k].indexOf(";") + 1,
                                act.transitions[k].lastIndexOf(";"));
                        String trs_to = act.transitions[k].substring(act.transitions[k].lastIndexOf(";") + 1);
                        act.transitions[k] = m.getEdgeName(Long.valueOf(trs), langId) + ";" + trs + ";" + trs_to;
                    }
                }
            }
        }

        public void loadProcess(Activity act) {
            if (act == null || act.processDefId == null)  return;
            // Предварительная загрузка объектов в процессах
            Set<String> uids = new HashSet<String>();
            Map<Long, Element> confXmls = new HashMap<Long, Element>();
            for (int i = 0; i < act.processDefId.length; ++i) {
                long pid = act.processDefId[i];
                if (!processMap_.containsKey(pid)) {
                    confXmls.put(pid, preloadProcessUids(pid, uids));
                }
            }
            Kernel krn = Kernel.instance();
            try {
                krn.addToCache(uids);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
                // Загрузка самих процессов
                for (int i = 0; i < act.processDefId.length; ++i) {
                    long pid = act.processDefId[i];
                    loadProcess(pid, confXmls.get(pid));
                }
                JGraph graph_ = (JGraph) processMap_.get(new Long(act.processDefId[0]));
                ServiceModel m = (ServiceModel) graph_.getGraphModel();
                m.setLang(langId, graph_);
        }
        private void loadProcess(long processId, Element confXml) {
            // Загрузка процессов
            try {
                Long key = new Long(processId);
                if (!processMap_.containsKey(key)) {
                    KrnObject proc = new KrnObject(key.longValue(), "", Kernel.SC_PROCESS_DEF.id);
                    byte[] msg_ = Kernel.instance().getBlob(proc.id, krn.getAttributeByName(Kernel.SC_PROCESS_DEF, "message"), 0,
                            langId, 0);
                    ServiceModel model = new ServiceModel(false, proc, langId);
                    JGraph graph = new JGraph(model);
                    graph.getEditor().setElementsSelectable(false);
                    setGraphGrid();
                    try {
                        InputStream is_msg = msg_.length > 0 ? new ByteArrayInputStream(msg_) : null;
                    	model.loadLangs(is_msg, langId);
                    	if (is_msg != null) is_msg.close();
                    	if (confXml != null) model.load(confXml, graph);
                        StateNode node = model.getStartNode();
                        if (node != null) {
                            node.getPresentation().setFillColor(Utils.getLightGreenColor().darker());
                            List edges = node.getPresentation().getFigEdges();
                            if (edges.size() > 0) {
                                ((FigTransitionEdge) edges.get(0)).setLineColor(Utils.getLightGreenColor().darker());
                                ((FigTransitionEdge) edges.get(0)).getDestArrowHead().setFillColor(
                                        Utils.getLightGreenColor().darker());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    model.setLang(langId, graph);
                    processMap_.put(key, graph);
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        public String getColumnName(int column) {
            return "KZ".equals(li.code) ? COL_NAMES_KZ[column] : COL_NAMES_RU[column];
        }

        public int getRowCount() {
            return data.size();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == CM_OPEN || columnIndex == CM_VIEW || columnIndex == CM_NEXT || columnIndex == CM_KILL;
        }

        public int getColumnCount() {
            return COL_NAMES_RU.length;
        }

        public Activity getSelectedActivity(int row) {
            if (row > -1 && row < data.size())
                return (Activity) data.get(row);
            else
                return null;
        }

        public Vector<Activity> getSelectedActivitys(int[] row) {
            Vector<Activity> acts = new Vector<Activity>();
            for (int i = 0; i < row.length; ++i) {
                if (row[i] > -1 && row[i] < data.size())
                    acts.add(data.get(row[i]));
            }
            return acts;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Activity act = data.size() > rowIndex ? (Activity) data.get(rowIndex) : null;
            if (act == null)
                return null;
            if (columnIndex == CM_PROCESS) {
                if (act.titles.length > 3 && act.titles[3].length() > 0) {
                    return act.titles[3];
                } else if (act.processDefId != null && act.processDefId.length > 0
                        && processMap_.get(new Long(act.processDefId[0])) != null)
                    return ((ServiceModel) ((JGraph) processMap_.get(new Long(act.processDefId[0]))).getGraphModel())
                            .getProcess().getName();

            } else if (columnIndex == CM_OBJECT) {
                return act.titles.length > 1 ? act.titles[1] : "";

            } else if (columnIndex == CM_TASK) {
                return act.titles.length > 0 ? act.titles[0] : "";

            } else if (columnIndex == CM_DATE) {
                if (act.date != null)
                    return dateFormat.format(act.date);

            } else if (columnIndex == CM_TIME) {
                if (act.date != null)
                    return timeFormat.format(act.date);

            } else if (columnIndex == CM_CONTROL_DATE) {
                if (act.controlDate == null)
                    return resource.getString("mask");
                else
                    return dateFormat.format(act.controlDate);
            } else if (columnIndex == CM_FROM) {
                return act.userFrom;
            } else if (columnIndex == CM_INITIATOR) {
                return act.userInit;
            } else if(columnIndex == CM_DB) {
            	return act.userBase_name;
            }
            return null;
        }

		public void sortData() {
            Collections.sort(data, new TaskComparator(sortColumn, isSortAcs));
        }

        class ActivityComparator implements Comparator {
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof Activity) || !(o2 instanceof Activity)) {
                    return 0;
                }
                return (((Activity) o1).flowId < ((Activity) o2).flowId ? -1
                        : (((Activity) o1).flowId == ((Activity) o2).flowId ? 0 : 1));
            }

        }

        class TaskComparator implements Comparator {

            protected int sortColumn;
            protected boolean isSortAsc;

            public TaskComparator(int sortColumn, boolean sortAsc) {
                this.sortColumn = sortColumn;
                isSortAsc = sortAsc;
            }

            public int compare(Object o1, Object o2) {
                int res = 0;
                if ((o1 instanceof Activity) && (o2 instanceof Activity)) {
                    Activity a1 = (Activity) o1;
                    Activity a2 = (Activity) o2;
                    if (sortColumn == CM_ACTIVE_FLOW) {
                        res = a1.timeActive>a2.timeActive?1:a1.timeActive<a2.timeActive?-1:0;
                    }else if (sortColumn == CM_SEMAPHORE) {
                        int status1 = 0;
                        if ((a1.param & ACT_ERR) == ACT_ERR)
                            status1 = 2;
                        else if ((a1.processDefId == null || (a1.param & ACT_PERMIT) != ACT_PERMIT || (a1.param & ACT_IN_BOX) == ACT_IN_BOX)
                                && (a1.param & ACT_ARTICLE) != ACT_ARTICLE && (a1.param & ACT_FASTREPORT) != ACT_FASTREPORT)
                            status1 = 1;
                        int status2 = 0;
                        if ((a2.param & ACT_ERR) == ACT_ERR)
                            status2 = 2;
                        else if ((a2.processDefId == null || (a2.param & ACT_PERMIT) != ACT_PERMIT || (a2.param & ACT_IN_BOX) == ACT_IN_BOX)
                                && (a2.param & ACT_ARTICLE) != ACT_ARTICLE && (a2.param & ACT_FASTREPORT) != ACT_FASTREPORT)
                            status2 = 1;

                        res = (status1 < status2 ? -1 : (status1 == status2 ? 0 : 1));
                    } else if (sortColumn == CM_PROCESS) {
                        String s1 = a1.titles[3];
                        String s2 = a2.titles[3];
                        res = s1.compareTo(s2);
                    } else if (sortColumn == CM_OBJECT) {
                        String s1 = a1.titles[1];
                        String s2 = a2.titles[1];
                        res = s1.compareTo(s2);
                    } else if (sortColumn == CM_TASK) {
                        String s1 = a1.titles[0];
                        String s2 = a2.titles[0];
                        res = s1.compareTo(s2);
                    } else if (sortColumn == CM_OPEN) {
                        res = isEnable(a1, sortColumn) ? !isEnable(a2, sortColumn) ? 1 : 0 : isEnable(a2, sortColumn) ? -1 : 0;
                    } else if (sortColumn == CM_VIEW) {
                        res = isEnable(a1, sortColumn) ? !isEnable(a2, sortColumn) ? 1 : 0 : isEnable(a2, sortColumn) ? -1 : 0;
                    } else if (sortColumn == CM_NEXT) {
                        res = isEnable(a1, sortColumn) ? !isEnable(a2, sortColumn) ? 1 : 0 : isEnable(a2, sortColumn) ? -1 : 0;
                    } else if (sortColumn == CM_DATE) {
                        Date d1 = a1.date;
                        Date d2 = a2.date;
                        res = d1==null?d2==null?0:-1:d2==null?1:d1.compareTo(d2);
                    } else if (sortColumn == CM_TIME) {
                        Date t1 = a1.date;
                        Date t2 = a2.date;
                        res = t1==null?t2==null?0:-1:t2==null?1:t1.compareTo(t2);
                    } else if (sortColumn == CM_CONTROL_DATE) {
                        Date c1 = a1.controlDate;
                        Date c2 = a2.controlDate;
                        res = c1==null?c2==null?0:-1:c2==null?1:c1.compareTo(c2);
                    } else if (sortColumn == CM_FROM) {
                        String uf1 = a1.userFrom;
                        String uf2 = a2.userFrom;
                        res = (uf1==null?"":uf1).compareTo(uf2==null?"":uf2);
                    } else if (sortColumn == CM_INITIATOR) {
                        String ui1 = a1.userInit;
                        String ui2 = a2.userInit;
                        res = (ui1==null?"":ui1).compareTo(ui2==null?"":ui2);
                    } else if (sortColumn == CM_DB) {
                    	String udb1 = a1.userBase_name;
                    	String udb2 = a2.userBase_name;
                    	res = (udb1==null?"":udb1).compareTo(udb2==null?"":udb2);
                    } else if (sortColumn == CM_KILL) {
                        res = isEnable(a1, sortColumn) ? !isEnable(a2, sortColumn) ? 1 : 0 : isEnable(a2, sortColumn) ? -1 : 0;
                    }
                    if (!isSortAsc) {
                        res = -res;
                    }
                }
                return res;
            }

            public boolean equals(Object obj) {
                if (obj instanceof TaskComparator) {
                    TaskComparator compObj = (TaskComparator) obj;
                    return (compObj.sortColumn == sortColumn) && (compObj.isSortAsc == isSortAsc);
                }
                return false;
            }
        }
    }

    class TaskCellRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, final Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setOpaque(true);
            TaskTableModel taskModel = (TaskTableModel) table.getModel();
            Activity act = taskModel.getSelectedActivity(row);
            if (!isSelected) {
                if (act != null && act.color != 0)
                    setBackground(new Color((int) act.color));
                else if (SE_UI)
                    setBackground(row % 2 == 0 ? Color.white : bgColor);
                else
                    setBackground(new Color(204, 204, 204));
            } else {
                setBackground(SE_UI ? bgColor2 : Utils.getLightSysColor());
            }
            if (act != null) {
                if ((act.param & ACT_ALARM) == ACT_ALARM) {
                    setForeground(alarmColor);
                } else if ((act.param & ACT_ALERT) == ACT_ALERT) {
                    setForeground(alertColor);
                } else {
                    setForeground(Color.BLACK);
                }
                if (column == CM_ACTIVE_FLOW) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon(act.timeActive>0?"activeFlow":"pasiveFlow"));
                        setToolTipText(act.timeActive>0?new Date(act.timeActive).toString():"");

                }else if (column == CM_SEMAPHORE) {
                    if ((act.param & ACT_ERR) == ACT_ERR)
                        setIcon(kz.tamur.rt.Utils.getImageIcon("red"));
                    else if ((act.processDefId == null || (act.param & ACT_PERMIT) != ACT_PERMIT || (act.param & ACT_IN_BOX) == ACT_IN_BOX)
                            && (act.param & ACT_ARTICLE) != ACT_ARTICLE && (act.param & ACT_FASTREPORT) != ACT_FASTREPORT)
                        setIcon(kz.tamur.rt.Utils.getImageIcon("yellow"));
                    else
                        setIcon(kz.tamur.rt.Utils.getImageIcon("green"));

                    if (!SE_UI) {
                        StringBuilder temp = new StringBuilder(256);
                        temp.append(table.getColumnName(1)).append(":").append(table.getValueAt(row, 1)).append("\n")
                                .append(table.getColumnName(2)).append(":").append(table.getValueAt(row, 2)).append("\n")
                                .append(table.getColumnName(3)).append(":").append(table.getValueAt(row, 3)).append("\n")
                                .append(table.getColumnName(7)).append(":").append(table.getValueAt(row, 7))
                                .append("\n" + table.getColumnName(8)).append(":").append(table.getValueAt(row, 8)).append("\n")
                                .append(table.getColumnName(9)).append(":").append(table.getValueAt(row, 9)).append("\n")
                                .append(table.getColumnName(10)).append(":").append(table.getValueAt(row, 10)).append("\n")
                                .append(table.getColumnName(11)).append(":" + table.getValueAt(row, 11));
                        setToolTipText(temp.toString());
                    }
                } else if (column == CM_PROCESS || column == CM_OBJECT || column == CM_TASK || column == CM_FROM
                        || column == CM_INITIATOR || column == CM_DB) {
                    setFont(Utils.getDefaultFont());
                    setText(value != null ? value.toString() : "");
                    setToolTipText((String) table.getValueAt(row, column));
                    if (column == 2 && act.titles.length > CM_OBJECT && act.titles[CM_OBJECT].length() > 0) {
                        setToolTipText(getMultiLine(act.titles[CM_OBJECT]));
                    }
                } else if (column == CM_DATE || column == CM_TIME || column == CM_CONTROL_DATE) {
                    if (column == CM_CONTROL_DATE) {
                        setFont(getFont().deriveFont(Font.BOLD));
                        setForeground(Color.RED.darker());
                    } else
                        setFont(Utils.getDefaultFont());
                    setHorizontalAlignment(JLabel.CENTER);
                    setText(value != null ? value.toString() : "");
                } else if (column == CM_OPEN) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("FormTab"));
                    setHorizontalAlignment(JLabel.CENTER);
                    setToolTipText("KZ".equals(li.code) ? "Интерфейс ашу" : "Открыть интерфейс");
                    setCursor(HAND_CURSOR);
                    setEnabled(isEnable(act, column));
                } else if (column == CM_VIEW) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("Tree"));
                    setHorizontalAlignment(JLabel.CENTER);
                    setToolTipText("KZ".equals(li.code) ? "Интерфейс ашу" : "Открыть интерфейс");
                    setCursor(HAND_CURSOR);
                    setEnabled(isEnable(act, column));
                } else if (column == CM_NEXT) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("actionRun"));
                    setHorizontalAlignment(JLabel.CENTER);
                    setToolTipText("KZ".equals(li.code) ? "Келес? \u049bадам" : "Следующий шаг");
                    setCursor(HAND_CURSOR);
                    setEnabled(isEnable(act, column));
                } else if (column == CM_KILL) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("DeleteProc"));
                    setHorizontalAlignment(JLabel.CENTER);
                    setToolTipText("KZ".equals(li.code) ? "Процесс болдырмау" : "Удалить процесс");
                    setCursor(HAND_CURSOR);
                    setEnabled(isEnable(act, column));
                }
            }

            if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
                // непрозрачность для текста
                JLabel newComp = new JLabel() {
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        // значение параметра прозрачности для текста
                        if (value != null) {
                            g.drawString(value.toString(), 2, 10);
                        }
                        if (getIcon() != null) {
                            g.drawImage(((ImageIcon) getIcon()).getImage(), 0, -2, null);
                        }
                    }
                };
                newComp.setIcon(getIcon());
                newComp.setFont(getFont());
                newComp.setBackground(getBackground());
                newComp.setForeground(getForeground());
                newComp.setOpaque(true);
                newComp.setAlignmentX(getAlignmentX());
                newComp.setAlignmentY(getAlignmentY());
                newComp.setToolTipText(getToolTipText());
                return newComp;
            }
            return this;
        }
    }

    private boolean isEnable(Activity act, int column) {
        boolean res = false;
        if (column == CM_OPEN) {
            res = (act.processDefId != null && (act.ui.id > 0 || (act.param & ACT_FASTREPORT) == ACT_FASTREPORT) && !reportSet
                    .contains(act.flowId));
        } else if (column == CM_VIEW) {
            res = (act.processDefId != null && (act.ui.id <0 || user.isAdmin()) && act.infUi.id > 0);
        } else if (column == CM_NEXT) {
            res = ((act.param & ACT_ARTICLE) == ACT_ARTICLE && (act.processDefId != null && act.ui.id > 0 && !reportSet
                    .contains(act.flowId)))
                    || ((act.param & ACT_FASTREPORT) == ACT_FASTREPORT)
                    || (user.isAdmin() && ((act.param & ACT_ERR) == ACT_ERR) && (act.param & ACT_PERMIT) == ACT_PERMIT)
                    || (((act.param & ACT_PERMIT) == ACT_PERMIT) &&  (act.ui.id > 0 || act.infUi.id < 0)
                    		&& ((act.param & ACT_IN_BOX) != ACT_IN_BOX) && ((act.param & ACT_OUT_BOX) != ACT_OUT_BOX) 
                    		&& ((act.param & ACT_SUB_PROC) != ACT_SUB_PROC));
        } else if (column == CM_KILL) {
            res = act.processDefId == null || ((act.param & ACT_CANCEL) == ACT_CANCEL);
        }
        return res;
    }

    class TaskCellEditor extends DefaultCellEditor {

        private JButton button = new JButton("");

        public TaskCellEditor() {
            super(new JTextField());
            initButton();
        }

        private void initButton() {
            button.setBorder(null);
            button.setOpaque(true);
            button.setCursor(HAND_CURSOR);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (column == CM_OPEN) {
                button.setIcon(kz.tamur.rt.Utils.getImageIcon("FormTab"));
            } else if (column == CM_VIEW) {
                button.setIcon(kz.tamur.rt.Utils.getImageIcon("Tree"));
            } else if (column == CM_NEXT) {
                button.setIcon(kz.tamur.rt.Utils.getImageIcon("actionRun"));
            } else if (column == CM_KILL) {
                button.setIcon(kz.tamur.rt.Utils.getImageIcon("DeleteProc"));
            }
            ActionListener[] ls = button.getActionListeners();
            for (int i = 0; i < ls.length; ++i)
                button.removeActionListener(ls[i]);
            button.addActionListener(new ActionTask(this, column));
            return button;
        }
    }

    class ActionTask implements ActionListener {

        private TableCellEditor cellEditor;
        private int column;

        public ActionTask(TableCellEditor cellEditor, int column) {
            this.cellEditor = cellEditor;
            this.column = column;
        }

        public void actionPerformed(ActionEvent e) {
            JFrame frm = (JFrame) getTopLevelAncestor();
            Activity act_;
            if (column == CM_OPEN) {
                try {
                    act_ = getSelectedActivity();
                    if (act_ == null || !isEnable(act_, column)) {
                    } else if (act_.timeActive>0) {
                        String text = "Выполняется предыдущее действие. Подождите!";
                        showMessageDialog(frm, INFORMATION_MESSAGE, text, li);
                    } else if ((act_.param & ACT_FASTREPORT) == ACT_FASTREPORT) {
                        long editorType = 0; // act_.articleType;

                        Kernel krn = Kernel.instance();
                        KrnObject flow = new KrnObject(act_.flowId, "", Kernel.SC_FLOW.id);
                        byte[] article = (act_.article != null && act_.article.length > 0) ? act_.article : krn.getBlob(flow,
                                "article", 0, 0, -1);

                        InputStream is = new ByteArrayInputStream(article);
                        SAXBuilder builder = new SAXBuilder();
                        Element report = builder.build(is).getRootElement();

                        File dir = new File("doc");
                        dir.mkdirs();

                        ReportLauncher.generateReport(report, editorType, resource.getString("formReport"));
                    } else if (act_.ui == null || act_.ui.id <= 0) {
                        String text = resource.getString("ifcNotExistMessage");
                        showMessageDialog(frm, ERROR_MESSAGE, text, li);
                    } else if (mng != null) {
                        if (isGrafVisible)
                            loc = mng.getSplitLoc(loc);
                        Object res_open=Kernel.instance().openInterface(act_.ui.id,act_.flowId,act_.trId,act_.processDefId.length>0?act_.processDefId[0]:-1);
                        if (res_open==null || (res_open instanceof Number && ((Number)res_open).intValue() == 1)) {
                            CursorToolkit.startWaitCursor(frm);
	                        if (ACT_DIALOG_STRING.equals(act_.uiType) || ACT_AUTO_STRING.equals(act_.uiType)) {
	                            UIFrame c = mng.getInterface(act_.ui, (act_.objs.length > 0 || act_.uiFlag == 1) ? act_.objs : null,
	                                    act_.trId, InterfaceManager.SERVICE_MODE, act_.flowId, false, false);
	                            DesignerDialog dlg = new DesignerDialog(frm, c.getPanel().getTitle(), (Component) c.getPanel());
	                            dlg.setLanguage(langId);
	                            CursorToolkit.stopWaitCursor(frm);
	                            int res = 0;
	                            while (res == 0) {
	                                dlg.show();
	                                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
	                                    OrRef ref = c.getRef();
	                                    ReqMsgsList msg = ref.canCommit();
	                                    int errors = msg.getListSize();
	                                    if (errors > 0) {
	                                        SortedFrame sdlg = new SortedFrame(frm, resource.getString("errors"));
	                                        msg.setParent(sdlg);
	                                        sdlg.setOption(new String[] { resource.getString("continue"), resource.getString("exit") });
	                                        sdlg.setContent(msg);
	                                        sdlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(sdlg.getSize()));
	                                        sdlg.show();
	                                        res = sdlg.getResult();
	                                    }
	                                    if (errors == 0) {
	                                        res = 1;
	                                        c.getCash().commit(act_.flowId);
	                                        c.getRef().commitChanges(null);
	                                        if (msg.hasFatalErrors()) {
	                                            Kernel.instance().setPermitPerform(act_.flowId, false);
	                                            // act_.param |= ACT_PERMIT;
	                                        } else {
	                                            Kernel.instance().setPermitPerform(act_.flowId, true);
	                                            act_.param |= ACT_PERMIT;
	                                            reloadTasks(act_.flowId, false, act_.ui.id > 0 && act_.infUi.id > 0 ? 2
	                                                    : act_.infUi.id > 0 ? 1 : 0);
	                                        }
	                                        List<OrRef.Item> a_sel = c.getRef().getSelectedItems();
	                                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
	                                        for (int i = 0; i < a_sel.size(); i++) {
	                                            OrRef.Item item = (OrRef.Item) a_sel.get(i);
	                                            selObjs[i] = (KrnObject) item.getCurrent();
	                                        }
	                                        CursorToolkit.startWaitCursor(frm);
	                                        Kernel.instance().setSelectedObjects(act_.flowId,
	                                                act_.nodesId[0][act_.nodesId[0].length - 1], selObjs);
	                                        repaint();
	                                    }
	                                } else {
	                                    res = 1;
	                                    break;
	                                }
	                            }
	                            mng.releaseInterface(false);
	
	                        } else if (ACT_ARTICLE_STRING.equals(act_.uiType)) {
	                            reportSet.add(act_.flowId);
	                            long lid = act_.articleLang != null ? act_.articleLang.id : 0;
	                            Kernel krn = Kernel.instance();
	                            File dir = new File("doc");
	                            dir.mkdirs();
	
	                            KrnObject flow = new KrnObject(act_.flowId, "", Kernel.SC_FLOW.id);
	                            if (lid <= 0 && krn.getAttributeByName(krn.getClassByName("Flow"), "article_lang") != null) {
	                                KrnObject[] langs = krn.getObjects(flow, "article_lang", -1);
	                                lid = (langs != null && langs.length > 0) ? langs[0].id : 0;
	                            }
	
	                            byte[] article = (act_.article != null && act_.article.length > 0) ? act_.article : krn.getBlob(flow,
	                                    "article", 0, 0, -1);
	
	                            byte[] htmlBuf = null;
	                            KrnAttribute htmlAttr = krn.getAttributeByName(krn.getClass(act_.ui.classId), "htmlTemplate");
	                            if (htmlAttr != null) {
	                                htmlBuf = krn.getBlob(act_.ui, "htmlTemplate", 0, lid, 0);
	                            }
	                            if (htmlBuf != null && htmlBuf.length > 0) {
	                                ReportLauncher.viewHtmlReport(article, htmlBuf, resource.getString("formReport"), TaskTable.this,
	                                        act_.flowId, dir);
	                            } else if (article.length > 0 && article[0] == '<') {
	                                ByteArrayInputStream bis = new ByteArrayInputStream(article);
	                                SAXBuilder builder = new SAXBuilder();
	                                Element root = builder.build(bis).getRootElement();
	                                List res = XPath.selectNodes(root, "//@src");
	
	                                byte[] bytes;
	                                if (res != null && res.size() > 0) {
	                                    for (int i = 0; i < res.size(); i++) {
	                                        Attribute attr = (Attribute) res.get(i);
	                                        String img = attr.getValue();
	                                        if (img.length() > 0) {
	                                            File f = Funcs.createTempFile("img", ".tmp", dir);
	                                            f.deleteOnExit();
	                                            FileOutputStream fos = new FileOutputStream(f);
	                                            fos.write(Base64.decode(img));
	                                            fos.close();
	                                            attr.setValue(f.getAbsolutePath());
	                                        }
	                                    }
	                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
	                                    XMLOutputter opr = new XMLOutputter();
	                                    opr.getFormat().setEncoding("UTF-8");
	                                    opr.output(root, os);
	                                    os.close();
	                                    bytes = os.toByteArray();
	                                } else {
	                                    bytes = article;
	                                }
	                                File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
	                                xmlFile.deleteOnExit();
	
	                                OutputStream os = new FileOutputStream(xmlFile);
	                                os.write(bytes);
	                                os.close();
	
	                                byte[] data = krn.getBlob(act_.ui, "config", 0, 0, 0);
	
	                                InputStream is = new ByteArrayInputStream(data);
	                                builder = new SAXBuilder();
	                                Element config = builder.build(is).getRootElement();
	                                Element type = config.getChild("editorType");
	                                int editorType = MSWORD_EDITOR;
	                                if (type != null) {
	                                    editorType = Integer.parseInt(type.getText());
	                                }
	
	                                Element macrosElement = config.getChild("macros");
	                                String macros = "";
	                                if (macrosElement != null) {
	                                    macros = macrosElement.getText();
	                                }
	                                String templatePD = config.getChildText("templatePassword");
	
	                                byte[] buf = krn.getBlob(act_.ui, "template", 0, lid, 0);
	
	                                String suffix = (editorType == MSWORD_EDITOR) ? ".doc" : ".xls";
	                                if (!"jacob".equals(System.getProperty("reportType")))
	                                    suffix += "x";
	
	                                String nameFile = act_.titles.length>4 && !"".equals(act_.titles[4])?act_.titles[4]: act_.titles[3];
	                                File docFile = (nameFile != null) ? new File(dir, nameFile + suffix) : Funcs.createTempFile("xxx", suffix, dir);
	                                if(docFile.exists()) docFile = Funcs.createTempFile(nameFile, suffix, dir);
	                                docFile.deleteOnExit();
	                                os = new FileOutputStream(docFile);
	                                os.write(buf);
	                                os.close();
	
	                                ReportLauncher.viewReport(docFile.getAbsolutePath(), xmlFile.getAbsolutePath(), editorType,
	                                        resource.getString("formReport"), macros, templatePD, TaskTable.this, act_.flowId);
	                            } else if (article.length > 4 && article[0] == '%' && article[1] == 'P') {
	                                ReportLauncher.viewPdfReport(article, resource.getString("formReport"), TaskTable.this,
	                                        act_.flowId, dir, true);
	                            } else if (article.length > 0) {
	                                long time = System.currentTimeMillis();
	                                try {
	                                    byte[] data = krn.getBlob(act_.ui, "config", 0, 0, 0);
	
	                                    InputStream is = new ByteArrayInputStream(data);
	                                    SAXBuilder builder = new SAXBuilder();
	                                    Element config = builder.build(is).getRootElement();
	                                    Element type = config.getChild("editorType");
	                                    int editorType = MSWORD_EDITOR;
	                                    if (type != null) {
	                                        editorType = Integer.parseInt(type.getText());
	                                    }
	
	                                    String suffix = (editorType == MSWORD_EDITOR) ? ".doc" : ".xls";
	                                    if (!"jacob".equals(System.getProperty("reportType")))
	                                        suffix += "x";
	
	                                    String nameFile = act_.titles.length>4 && !"".equals(act_.titles[4])?act_.titles[4]: act_.titles[3];
	                                    File docFile = Funcs.createTempFile(nameFile, suffix, dir);
	                                    docFile.deleteOnExit();
	                                    FileOutputStream os = new FileOutputStream(docFile);
	                                    os.write(article);
	                                    os.close();
	                                    Runtime ru = Runtime.getRuntime();
	                                    ru.exec("cmd /c \"" + docFile.getAbsolutePath() + "\"");
	                                } catch (Exception ex) {
	                                    ex.printStackTrace();
	                                }
	                                System.out.println("Forming report time: " + (System.currentTimeMillis() - time));
	                                
	                                if (act_.flowId > 0) {
	                                    TaskTable.this.setReportComplete(act_.flowId);
	                                }
	                            }
	                        } else {
	                            openUI(act_);
	                        }
	                        CursorToolkit.stopWaitCursor(frm);
                        }else {
                        	String msg="Интерфейс недоступен данному пользователю!";
                        	if(res_open instanceof String)
                        		msg=(String)res_open;
                        	showMessageDialog(frm, INFORMATION_MESSAGE, msg,li);
	                    }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (column == CM_VIEW) {
                try {
                    act_ = getSelectedActivity();
                    if (act_ == null || !isEnable(act_, column)) {
                    } else if (act_.infUi == null || act_.infUi.id <= 0) {
                        String text = resource.getString("ifcNotExistMessage");
                        showMessageDialog(frm, ERROR_MESSAGE, text, li);
                    } else if (mng != null) {
                        if (isGrafVisible)
                            loc = mng.getSplitLoc(loc);
                        CursorToolkit.startWaitCursor(frm);
                        // Kernel.instance().openInterface(act_.flowId);
                        if (ACT_DIALOG_STRING.equals(act_.uiTypeInf)) {
                            UIFrame c = mng.getInterface(act_.infUi, (act_.infObjs.length > 0) ? act_.infObjs : null, act_.trId,
                                    InterfaceManager.SERVICE_MODE, act_.flowId, false, false);
                            DesignerDialog dlg = new DesignerDialog(frm, c.getTitle(), (Component) c.getPanel());
                            dlg.setLanguage(langId);
                            CursorToolkit.stopWaitCursor(frm);
                            int res = 0;
                            while (res == 0) {
                                dlg.show();
                                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                                    OrRef ref = c.getRef();
                                    ReqMsgsList msg = ref.canCommit();
                                    int errors = msg.getListSize();
                                    if (errors > 0) {
                                        SortedFrame sdlg = new SortedFrame(frm, resource.getString("errors"));
                                        msg.setParent(sdlg);
                                        sdlg.setOption(new String[] { resource.getString("continue"), resource.getString("exit") });
                                        sdlg.setContent(msg);
                                        sdlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(sdlg.getSize()));
                                        dlg.setVisible(true);
                                        res = dlg.getResult();
                                    }
                                    if (res != BUTTON_NOACTION && res == BUTTON_OK || errors == 0) {
                                        res = 1;
                                        c.getRef().commitChanges(null);
                                        List<OrRef.Item> a_sel = c.getRef().getSelectedItems();
                                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
                                        for (int i = 0; i < a_sel.size(); i++) {
                                            OrRef.Item item = (OrRef.Item) a_sel.get(i);
                                            selObjs[i] = (KrnObject) item.getCurrent();
                                        }
                                        CursorToolkit.startWaitCursor(frm);
                                        Kernel.instance().setSelectedObjects(act_.flowId,
                                                act_.nodesId[0][act_.nodesId[0].length - 1], selObjs);
                                        repaint();
                                    }
                                } else {
                                    res = 1;
                                    break;
                                }
                            }
                            mng.releaseInterface(false);
                        } else {
                            mng.setEnabledGraf(false);
                            try {
                                mng.absolute(act_.infUi, (act_.infObjs.length > 0) ? act_.infObjs : null, "",
                                        InterfaceManager.READONLY_MODE, false, act_.trId, act_.flowId, false, "");
                            } catch (KrnException es) {
                                es.printStackTrace();
                                clientLog.info("Ошибка при открытии интерфейса.id=" + act_.ui.id);
                            }
                            uiOpenAct = act_;
                        }
                        CursorToolkit.stopWaitCursor(frm);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (column == CM_NEXT) {
                act_ = getSelectedActivity();
                if (isEnable(act_, column)) {
                    next(frm);
                }
            } else if (column == CM_KILL) {
                Vector acts = getSelectedActivitys();
                CursorToolkit.startWaitCursor(frm);
                int result=-1;
                String text = resource.getString("killProcMessage") + "\n'" + acts.size() + "'";
                for (int i = 0; i < acts.size(); ++i) {
                    act_ = (Activity) acts.get(i);
                    if (isEnable(act_, column)) {
                        try {
                            if (result==-1){
                            	if(acts.size()==1){
		                            String service_txt = "";
		                            if (act_.processDefId != null && act_.processDefId.length > 0
		                                    && processMap_.get(new Long(act_.processDefId[0])) != null)
		                                service_txt = ((ServiceModel) ((JGraph) processMap_.get(new Long(act_.processDefId[0])))
		                                        .getGraphModel()).getProcess().getName();
		                            text = resource.getString("killProcMessage") + "\n'" + service_txt + "'";
                            	}
                            	result = showMessageDialog(frm, QUESTION_MESSAGE, text, li);
                            }
                            if (result == ButtonsFactory.BUTTON_YES) {
                                Kernel.instance().cancelProcess(act_.flowId, act_.msg, true, true);
                                int index = model.data.indexOf(act_);
                                model.data.remove(act_);
                                model.fireTableRowsDeleted(index, index);
                                if(act_.ui!=null) act_.ui.id = -1;
                                if(act_.infUi!=null) act_.infUi.id = -1;
                                if ((act_.param & ACT_PERMIT) == ACT_PERMIT)
                                    act_.param ^= ACT_PERMIT;
                                repaint();
                            }
                        } catch (KrnException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                CursorToolkit.stopWaitCursor(frm);
            }
            if (cellEditor != null)
                cellEditor.stopCellEditing();
        }
    }

    public int next(JFrame frm) {
        Activity act_ = getSelectedActivity();
        return next(act_, frm);
    }

    public int next(Activity act_, JFrame frm) {
        try {
            return next(act_, frm, false);
        } catch (KrnException e) {
        }
        return -1;
    }

    public int next(Activity act_, JFrame frm, boolean force) throws KrnException {
        CursorToolkit.startWaitCursor(frm);
        int result = -1;
        if(act_.timeActive>0){
            String msg = "Выполняется предыдущее действие!";
            result =showMessageDialog(frm, INFORMATION_MESSAGE, msg);
        }else{
	        try {
	            int result_ = -1;
	            String res_ = "";
	            if (!force) {
	                result = showMessageDialog(frm, QUESTION_MESSAGE, resource.getString("nextStepMessage"), li);
	            }
	            act_.timeActive=1;
	            if (force || result == ButtonsFactory.BUTTON_YES) {
	                if (act_.transitions.length > 1) {
	                    String[] trs = new String[act_.transitions.length];
	                    for (int i = 0; i < trs.length; ++i) {
	                        trs[i] = new String(act_.transitions[i].substring(0, act_.transitions[i].indexOf(",")));
	                    }
	                    result_ = showOptionDialog(frm, OPTION_MESSAGE, trs, li);
	                    if (result_ == -1) {
	                    	result = ButtonsFactory.BUTTON_CANCEL;
	                        CursorToolkit.stopWaitCursor(frm);
	                    } else {
	                        for (int i = 0; i < trs.length; ++i) {
	                            if (result_ == i) {
	                                res_ = new String(act_.transitions[i].substring(act_.transitions[i].lastIndexOf(",") + 1));
	                                break;
	                            }
	                        }
	                    }
	                }
	                if (force || result == ButtonsFactory.BUTTON_YES) {
		                CursorToolkit.startWaitCursor(frm);
		                if (act_.processDefId.length > 1) {
		                    lastSuperFlowId_ = act_.processDefId[1];
		                } else if (act_.processDefId.length > 0) {
		                    lastSuperFlowId_ = act_.processDefId[0];
		                }
		                String[] res = Kernel.instance().performActivitys(new Activity[] { act_ }, res_);
		                if(res.length>0 && res[0].indexOf("deferred")==0) {
		                    result =showMessageDialog(frm, MessagesFactory.QUESTION_MESSAGE, res[0].substring(8));
		                	if(result==ButtonsFactory.BUTTON_OK || result==ButtonsFactory.BUTTON_YES) {
		                        res = Kernel.instance().performActivitys(new Activity[] { act_ }, res_,EventType.DEFERRED_PERFORM_OF_ACTIVITY.toString());
		                	}else {
		                        reloadTasks(act_.flowId, true, act_.ui.id > 0 && act_.infUi.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : 0);
		                        CursorToolkit.stopWaitCursor(frm);
		                		return ButtonsFactory.BUTTON_NOACTION;
		                	}
		                }
		                if (res.length == 1 && res[0].equals("synch")) {
		                    setAutoIfcFlowId_(act_.flowId);
		                }
		                if (res.length > 0 && !res[0].equals("synch")) {
		                    Kernel.instance().setPermitPerform(act_.flowId, false);
		                    if ((act_.param & ACT_PERMIT) == ACT_PERMIT) {
		                        act_.param ^= ACT_PERMIT;
		                    }
		                    repaint();
		                    StringBuilder msg = new StringBuilder(res[0]);
		                    for (int i = 1; i < res.length; ++i) {
		                        msg.append("\n").append(res[i]);
		                    }
		                    showMessageDialog(frm, ERROR_MESSAGE, msg.toString().replaceFirst("^\\!", ""));
		                    if (force && msg.charAt(0) != '!') {
		                        throw new KrnException(0, msg.toString());
		                    }
		                } else {
		                    autoIfcSet_.add(act_.flowId);
		                    if (uiOpenAct == act_) {
		                        uiOpenAct = null;
		                    }
		                    act_.ui.id = -1;
		                    act_.infUi.id = -1;
		                    if ((act_.param & ACT_PERMIT) == ACT_PERMIT) {
		                        act_.param ^= ACT_PERMIT;
		                    }
		                    if (isGrafVisible) {
		                        setGraf(0);
		                    }
		                    if (res.length == 1 && res[0].equals("synch")) {
		                        reloadTasks(act_.flowId, true, act_.ui.id > 0 && act_.infUi.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : 0);
		                    }
		                    repaint();
		                }
	                }
	            }
	        } catch (KrnException e1) {
	            e1.printStackTrace();
	            if (force) {
	                throw new KrnException(0, e1.getMessage());
	            }
	        }
        }
        CursorToolkit.stopWaitCursor(frm);
        return result;
    }

    public void setPermitPerform(boolean permit) {
        Activity act = getSelectedActivity();
        if (act == null)
            return;
        if (permit && (act.param & ACT_PERMIT) != ACT_PERMIT) {
            act.param |= ACT_PERMIT;
            model.fireTableCellUpdated(table.getSelectedRow(), 2);
        } else if (!permit && (act.param & ACT_PERMIT) == ACT_PERMIT) {
            act.param ^= ACT_PERMIT;
            model.fireTableCellUpdated(table.getSelectedRow(), 2);
        }
    }

    public void setLang(long lang, boolean par) {
        // if(langId==lang) return;
        langId = lang;
        li = LangItem.getById(langId);
        resource = ResourceBundle.getBundle(NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
        // for (int i = 0; i < model.data.size(); ++i) {
        // JGraph graph_ = (JGraph) processMap_.get(new Long(((Activity) model.data.get(i)).processDefId[0]));
        // ((ServiceModel)graph_.getGraphModel()).setLang(langId,graph_);
        // }
        for (Iterator it = processMap_.values().iterator(); it.hasNext();) {
            JGraph graph_ = (JGraph) it.next();
            ((ServiceModel) graph_.getGraphModel()).setLang(langId, graph_);
        }
        for (int i = 0; i < model.getColumnCount(); ++i) {
            table.getColumnModel().getColumn(i).setHeaderValue(model.getColumnName(i));
        }
        if (par) {
            for (int i = 0; i < model.data.size(); ++i) {
                Activity act = (Activity) model.data.get(i);
                reloadTasks(act.flowId, false, act.ui.id > 0 && act.infUi.id > 0 ? 2 : act.infUi.id > 0 ? 1 : 0);
            }
        }
        table.getTableHeader().repaint();

        table.setDesc(resource.getString("taskTableDesc"));
        header.setDesc(resource.getString("taskTableHeaderDesc"));
        counterLabel.setDesc(resource.getString("counterLabelDesc"));
        menuItemQuick.setText(resource.getString("lastSrv"));
        menuItemHotKeys.setText(resource.getString("hotKeys"));
    }

    public Activity startProcess(String flowId, List<String> param) {
        setAutoAct(false);
        this.MoveTableView(model.data.size() - 1);
        Activity act = new Activity();
        act.flowId = Long.valueOf(flowId).longValue();
        act.rootFlowId = act.flowId;
        act.titles = new String[] { resource.getString("createProcess"), "", "" };
        act.param = ACT_SUB_PROC;
        act.timeActive=1;
        // если монир задач не отображается, то установить флаг автоматического открытия интерфейса
        act.autoIfc = !Application.instance().isMonitorTask();
        model.data.add(act);
        this.setSelectedActivity(act.flowId);
        model.fireTableRowsInserted(model.data.size() - 1, model.data.size() - 1);
        createMap.put(new Long(act.flowId), act);
        autoIfcSet_.add(act.flowId);
        this.MoveTableView(model.data.size() - 1);
        if (param.contains("autoIfc") || param.contains("synch")) {
            setAutoIfcFlowId_(act.flowId);
        }
        if (param.contains("synch")) {
            reloadTasks(act.flowId, true, 0);
        }
        return act;
    }

    public boolean stopProcess(Activity act, boolean forceCancel) throws KrnException {
        boolean res = Kernel.instance().cancelProcess(act.flowId, act.msg, true, forceCancel);
        if (res) {
	        int index = model.data.indexOf(act);
	        if (index > -1) {
		        model.data.remove(act);
		        model.fireTableRowsDeleted(index, index);
		        act.ui.id = -1;
		        act.infUi.id = -1;
		        if ((act.param & ACT_PERMIT) == ACT_PERMIT) {
		            act.param ^= ACT_PERMIT;
		        }
		        repaint();
	        }
        }
        return res;
    }

    public List<Activity> findProcess(KrnObject def, KrnObject obj) {
        List<Activity> res = new ArrayList<Activity>();
        synchronized (model.data) {
            for (Activity act : model.data)
                if (Funcs.indexOf(def.id, act.processDefId) != -1)
                    if (Funcs.indexOf(obj, act.objs) != -1 || Funcs.indexOf(obj, act.infObjs) != -1)
                        res.add(act);
        }
        return res;
    }

    public List<Long> findForeignProcess(KrnObject def, KrnObject obj) throws KrnException {
        return Kernel.instance().findForeignProcess(def.id, obj.id);
    }
    
    class IfcLoader extends SwingWorker {

        private Activity act;

        public IfcLoader(Activity act) {
            super();
            this.act = act;
        }

        public Object construct() {
            mng.setEnabledGraf(false);
            try {
                mng.absolute(act.ui, (act.objs.length > 0 || act.uiFlag == 1) ? act.objs : null, "",
                        InterfaceManager.SERVICE_MODE, false, act.trId, act.flowId, false, "");
            } catch (KrnException e) {
                e.printStackTrace();
                clientLog.info("Ошибка при открытии интерфейса.id=" + act.ui.id);
            }
            uiOpenAct = act;
            return null;
        }
    }

    private void setCounterText() {
        counterLabel.setText(new StringBuilder().append(selRowIdx>0?" "+(selRowIdx+(lastRow==0?lastRow:lastRow-pgCount)):"").append(" / ").append(rowCount+(lastRow==0?lastRow:lastRow-pgCount)).append(" ").toString());
    }

    void showOperations(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * запуск процесса по Горячим клавишам.
     * 
     * @param i
     *            номер горячей клавишы.
     */
    public void callHotKey(int i) {
        dialogIsActive = true;
        QuickSrvPanel qlisted = new QuickSrvPanel();
        if (qlisted.quickKeyNameGet(i) == null || qlisted.quickKeyIdGet(i) == -1)
            return;
        String text = TaskTable.instance(false).resource.getString("startProcMessage");

        int result = showMessageDialog((JFrame) getTopLevelAncestor(), QUESTION_MESSAGE, text + ":'" + qlisted.quickKeyNameGet(i)
                + "'?", li);

        if (result == ButtonsFactory.BUTTON_YES) {
            try {
                String[] res_ = Kernel.instance().startProcess(qlisted.quickKeyIdGet(i), null);

                if (res_.length > 0 && !res_[0].equals("")) {
                    CursorToolkit.stopWaitCursor(this);
                    String msg = res_[0];
                    showMessageDialog((JFrame) getTopLevelAncestor(), ERROR_MESSAGE, msg);

                } else {
                    List<String> param = new ArrayList<String>();
                    if (res_.length > 3) {
                        param.add(res_[3]);
                    }
                    startProcess(res_[1], param);
                }
                CursorToolkit.stopWaitCursor(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        dialogIsActive = false;
    }

    /**
     * Вызов панели Горячих Клавиш
     */
    public void callHotKeyList() {
        dialogIsActive = true;
        Window cnt3 = (Window) getTopLevelAncestor();
        QuickSrvPanel qpanel = new QuickSrvPanel(false);
        DesignerDialog dlg = new DesignerDialog(true, cnt3, "QPanel!!!", qpanel);
        dlg.setOnlyOkButton();
        dlg.setOkVisible(false);

        dlg.show();
        dialogIsActive = false;
    }

    /**
     * Просто для вызова quickList, потому что не думал что придется идти через KeyEventDispatcher
     */
    public void callquickList() {
        quickList((Window) getTopLevelAncestor());
    }

    /**
     * Вызов панели Последние добавления в процессы
     * 
     * @param parent
     */
    public void quickList(Window parent) {
        dialogIsActive = true;
        final QuickSrvListPanel quickSrvListp = new QuickSrvListPanel();
        if (!quickSrvListp.isClear()) {
            final DesignerDialog dlg = new DesignerDialog(parent, "QuickSrvPanel, QuickList", quickSrvListp);
            quickSrvListp.getList().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Object ob[] = quickSrvListp.getList().getSelectedValues();
                    if (ob.length > 1)
                        return;
                    if (e.getClickCount() == 2) {
                        e.consume();
                        dlg.processOkClicked();
                    }
                }
            });

            dlg.show();

            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String text = TaskTable.instance(false).resource.getString("startProcMessage");
                long id = quickSrvListp.getSelectedId();
                if (id == -1) {
                    return;
                }
                quickSrvListp.write(quickSrvListp.selectedItemName, quickSrvListp.selectedItemPath, String.valueOf(id));
                int result = showMessageDialog((JFrame) getTopLevelAncestor(), QUESTION_MESSAGE, text + ":'"
                        + quickSrvListp.selectedItemName + "'?", TaskTable.instance(false).li);
                if (result == ButtonsFactory.BUTTON_YES) {

                    // ------------
                    try {
                        CursorToolkit.startWaitCursor(this);
                        String[] res_ = Kernel.instance().startProcess(id, null);
                        if (res_.length > 0 && !res_[0].equals("")) {
                            CursorToolkit.stopWaitCursor(this);
                            String msg = res_[0];
                            showMessageDialog((JFrame) getTopLevelAncestor(), ERROR_MESSAGE, msg);
                            quickSrvListp.deleteById(id);
                        } else {
                            List<String> param = new ArrayList<String>();
                            if (res_.length > 3) {
                                param.add(res_[3]);
                            }
                            startProcess(res_[1], param);
                        }
                        CursorToolkit.stopWaitCursor(this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        dialogIsActive = false;
    }

    public boolean getDialogIsActive() {
        return dialogIsActive;
    }

    class DescTable extends JTable implements Descriptionable {
        private String desc;
        private float alpha;

        public DescTable() {
            super();
            alpha = 1f - MainFrame.TRANSPARENT_CELL_TABLE / 100f;
            KeyAdapter ka = new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    Window cnt = (Window) getTopLevelAncestor();
                    if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                        find(cnt);
                    } else if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown()) {
                        // потом уберу!!!
                        Window cnt2 = (Window) getTopLevelAncestor();
                        quickList(cnt2);
                    } else if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {
                        callHotKeyList();
                    } else if (e.getKeyCode() == KeyEvent.VK_F3 && e.isShiftDown()) {
                        CursorToolkit.startWaitCursor(cnt);
                        int res = findPrev(searchString.toLowerCase(Constants.OK), method, currentRow, currentCol, getModel());

                        CursorToolkit.stopWaitCursor(cnt);

                        if (res > -1) {
                            currentRow = res / 100;
                            currentCol = res - currentRow * 100;

                            setRowSelectionInterval(currentRow, currentRow);
                            setColumnSelectionInterval(currentCol, currentCol);
                            scrollToVisible(currentRow, currentCol);
                        } else {
                            currentRow = -1;
                            currentCol = 0;
                            MessagesFactory.showMessageNotFound(cnt);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                        CursorToolkit.startWaitCursor(cnt);
                        int res = findNext(searchString.toLowerCase(Constants.OK), method, currentRow, currentCol, getModel());

                        CursorToolkit.stopWaitCursor(cnt);
                        if (res > -1) {
                            currentRow = res / 100;
                            currentCol = res - currentRow * 100;

                            setRowSelectionInterval(currentRow, currentRow);
                            setColumnSelectionInterval(currentCol, currentCol);
                            scrollToVisible(currentRow, currentCol);
                        } else {
                            currentRow = -1;
                            currentCol = 0;
                            MessagesFactory.showMessageNotFound(cnt);
                        }
                    }

                    // HotKeysListener
                    if (e.getKeyCode() > 47 && e.getKeyCode() < 58 && e.isControlDown()) {
                        // QuickSrvPanel qlisted = new QuickSrvPanel();
                        for (int i = 0; i < 10; i++) {
                            if (e.getKeyCode() == (0x30 + i)) {
                                callHotKey(i);
                                break;
                            }
                        }
                    }

                }
            };
            addKeyListener(ka);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        openInterfaceAction.actionPerformed(null);
                    }
                    super.mouseClicked(e);
                }

            });
        }

        public String getDesc() {
            return desc;
        }

        protected void paintComponent(Graphics g) {
            if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
            super.paintComponent(g);
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void valueChanged(ListSelectionEvent e) {
            super.valueChanged(e);
            boolean isAdjusting = e.getValueIsAdjusting();
            if (isAdjusting && mng != null) {
            } else if (!isAdjusting && mng != null) {
                if (isGrafVisible)
                    setGraf(0);
            }
            rowCount = model.data.size();
            selRowIdx = table.getSelectedRow() + 1;
            setCounterText();
        }

        public JToolTip createToolTip() {
            return new OrMultiLineToolTip();
        }

        public void find(Window parent) {
            SearchInterfacePanel sip = new SearchInterfacePanel();
            sip.setSearchMethod(ComparisonOperations.CO_CONTAINS);
            if (searchString != null)
                sip.setSearchText(searchString);
            final DesignerDialog dlg = new DesignerDialog(parent, "Поиск элемента", sip);

            sip.getSearchField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dlg.processCancelClicked();
                    }
                }
            });

            dlg.show();

            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                CursorToolkit.startWaitCursor(dlg);
                searchString = sip.getSearchText();
                method = sip.getSearchMethod();

                currentRow = -1;
                currentCol = 0;

                int res = findNext(searchString.toLowerCase(Constants.OK), method, currentRow, currentCol, getModel());

                CursorToolkit.stopWaitCursor(dlg);

                if (res > -1) {
                    currentRow = res / 100;
                    currentCol = res - currentRow * 100;

                    setRowSelectionInterval(currentRow, currentRow);
                    setColumnSelectionInterval(currentCol, currentCol);
                    scrollToVisible(currentRow, currentCol);
                } else {
                    currentRow = -1;
                    currentCol = 0;
                    MessagesFactory.showMessageNotFound(parent);
                }
            }

        }

        private int findNext(String str, int method, int fromRow, int fromCol, TableModel model) {
            int rowCount = model.getRowCount();
            int colCount = model.getColumnCount();

            for (int r = fromRow + 1; r < rowCount; r++) {
                for (int i = 0; i < colCount; i++) {
                    Object val = model.getValueAt(r, i);
                    if (val instanceof String) {
                        String s = ((String) val).toLowerCase();
                        if ((method == ComparisonOperations.CO_CONTAINS && s.contains(str)) || (method == ComparisonOperations.SEARCH_START_WITH && s.startsWith(str))
                                || (method == ComparisonOperations.CO_EQUALS && s.startsWith(str))) {
                            return r * 100 + i;
                        }
                    }
                }
            }

            return -1;
        }

        private int findPrev(String str, int method, int fromRow, int fromCol, TableModel model) {
            if (fromRow == -1)
                fromRow = model.getRowCount();
            int colCount = model.getColumnCount();
            for (int r = fromRow - 1; r >= 0; r--) {
                for (int i = 0; i < colCount; i++) {
                    Object val = model.getValueAt(r, i);
                    if (val instanceof String) {
                        String s = ((String) val).toLowerCase(Constants.OK);
                        if ((method == ComparisonOperations.CO_CONTAINS && s.contains(str)) || (method == ComparisonOperations.SEARCH_START_WITH && s.startsWith(str))
                                || (method == ComparisonOperations.CO_EQUALS && s.startsWith(str))) {
                            return r * 100 + i;
                        }
                    }
                }
            }
            return -1;
        }

        public void scrollToVisible(int rowIndex, int vColIndex) {
            if (!(getParent() instanceof JViewport)) {
                return;
            }
            JViewport viewport = (JViewport) getParent();
            // This rectangle is relative to the table where the
            // northwest corner of cell (0,0) is always (0,0).
            Rectangle rect = getCellRect(rowIndex, vColIndex, true);

            // The location of the view relative to the table
            Rectangle viewRect = viewport.getViewRect();
            // Translate the cell location so that it is relative
            // to the view, assuming the northwest corner of the
            // view is (0,0).
            rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

            // Calculate location of rect if it were at the center of view
            int centerX = (viewRect.width - rect.width) / 2;
            int centerY = (viewRect.height - rect.height) / 2;

            // Fake the location of the cell so that scrollRectToVisible
            // will move the cell to the center
            if (rect.x < centerX) {
                centerX = -centerX;
            }
            if (rect.y < centerY) {
                centerY = -centerY;
            }
            rect.translate(centerX, centerY);
            if (viewRect.x + rect.x < 0)
                rect.x = -viewRect.x;
            if (viewRect.y + rect.y < 0)
                rect.y = -viewRect.y;
            // Scroll the area into view.
            viewport.scrollRectToVisible(rect);
        }
    }

    class DescTableHeader extends JTableHeader implements Descriptionable {
        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public DescTableHeader(TableColumnModel cm) {
            super(cm);
        }
    }

    private void readFilterParams() {
        Frame frm = (Frame) getTopLevelAncestor();
        TaskFilterParamsDialog dlg = new TaskFilterParamsDialog(frm, filterParams);
        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
        dlg.show();
        int res = dlg.getResult();
        if (res != BUTTON_NOACTION && res == BUTTON_OK) {
            updateFilterStatus();
            try {
                Kernel.instance().setTaskListFilter(filterParams);
            } catch (KrnException e) {
                e.printStackTrace();
            }
            model.reload();
            model.fireTableDataChanged();
            table.valueChanged(new ListSelectionEvent(this, 0, 0, false));
        }
    }

    public void setReportComplete(long flowId) {
        reportSet.remove(flowId);
        table.repaint();
    }

    private void updateFilterStatus() {
        StringBuilder title = new StringBuilder("Фильтр: ");
        if (filterParams.size() > 0) {
            Object param1 = filterParams.get(FLR_DATE_BEGIN);
            Object param2 = filterParams.get(FLR_DATE_END);
            if (param1 != null || param2 != null) {
                title.append("[Дата");
                if (param1 != null) {
                    title.append(" с " + FMT_DATE.format(param1));
                }
                if (param2 != null) {
                    title.append(" по " + FMT_DATE.format(param2));
                }
                title.append("]");
            }
            filterStatus.setForeground(Color.red.darker());
        } else {
            filterStatus.setForeground(Color.black);
            title.append("<нет>");
        }
        filterStatus.setText(title.toString());
    }

    public void openUI(Activity act) {
        mng.setEnabledGraf(false);
        try {
            mng.absolute(act.ui, (act.objs.length > 0 || act.uiFlag == 1) ? act.objs : null, "", InterfaceManager.SERVICE_MODE,
                    false, act.trId, act.flowId, false, act.uiType);
        } catch (KrnException es) {
            es.printStackTrace();
            clientLog.info("Ошибка при открытии интерфейса.id=" + act.ui.id);
        }
        uiOpenAct = act;
    }

    /**
     * Получить table.
     * 
     * @return the table
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Получить scroll.
     * 
     * @return the scroll
     */
    public JScrollPane getScroll() {
        return sp;
    }

    private Element preloadProcessUids(long pid, Collection<String> uids) {
        KrnObject proc = new KrnObject(pid, "", SC_PROCESS_DEF.id);
        try {
            byte[] data_ = Kernel.instance().getBlob(proc.id, Kernel.instance().getAttributeByName(SC_PROCESS_DEF, "diagram"), 0,
                    0, 0);
            if (data_.length == 0) {
                return null;
            }
            InputStream is = new ByteArrayInputStream(data_);
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Element xml = builder.build(is).getRootElement();
            is.close();
            // Загрузка ссылок на KrnObject в кэш
            XPath xp = XPath.newInstance("//property[starts-with(@name,'KRN')]");
            List<Element> elems = xp.selectNodes(xml);
            for (Element elem : elems) {
                uids.add(elem.getText());
            }
            return xml;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @return the autoIfcSet_
     */
    public Set<Long> getAutoIfcSet() {
        return autoIfcSet_;
    }

    /**
     * @return the isGrafVisible
     */
    public boolean isGrafVisible() {
        return isGrafVisible;
    }

    public boolean isAutoAct() {
        return isAutoAct;
    }

    public void setAutoAct(boolean isAutoAct) {
        this.isAutoAct = isAutoAct;
    }

    /**
     * @return the resource
     */
    public ResourceBundle getResource() {
        return resource;
    }

    @Override
    public void doOnNotification(SystemNote note) {
        if (frame != null) {
            frame.doOnNotification(note);
        }
    }

    /**
     * @return the frame
     */
    public MainFrame getFrame() {
        return frame;
    }

    @Override
    public void setProgressMinimum(int val) {
        if (frame != null) {
            frame.getProgress().setMinimum(val);
        }
    }

    @Override
    public void setProgressCaption(String text) {
        if (frame != null) {
            frame.getProgressLabel().setText("Формирование отчета:");
        }
    }

    @Override
    public void setProgressMaximum(int val) {
        if (frame != null) {
            frame.getProgress().setMaximum(val);
        }
    }

    @Override
    public void setProgressValue(int val) {
        if (frame != null) {
            frame.getProgress().setValue(val);
        }
    }

    /**
     * @param autoIfcFlowId_
     *            the autoIfcFlowId_ to set
     */
    public void setAutoIfcFlowId_(long autoIfcFlowId) {
        autoIfcFlowId_ = autoIfcFlowId;
        setAutoAct(false);
    }

    public void setOpenUI(KrnObject openUI) {
        if (openUI==null) {
            aAct = null;
        }
        this.openUI = openUI;
    }
    public void checkActiveflows(){
    	Map<Long,Long> activeFlows;
		try {
			activeFlows = Kernel.instance().getActiveFlows();
	    	for(Activity act :model.data){
	    		if(activeFlows.containsKey(act.flowId))
	    			act.timeActive=activeFlows.get(act.flowId);
	    		else if(act.timeActive>0)
	    			act.timeActive=0;
	    	}
	    	repaint();
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
