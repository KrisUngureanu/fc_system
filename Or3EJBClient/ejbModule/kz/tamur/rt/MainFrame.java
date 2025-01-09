package kz.tamur.rt;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.or3ee.common.TransportIds.MQ_TRANSPORT;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconExt;
import static kz.tamur.rt.Utils.getImageIconJpg;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrCheckBoxMenuItem;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrHiperTree;
import kz.tamur.comps.OrMenu;
import kz.tamur.comps.OrMenuItem;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrSplitPane;
import kz.tamur.comps.OrTabbedPane;
import kz.tamur.comps.OrTreeCtrl;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.comps.ui.OrGradientToolBar;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.QuickSrvListPanel;
import kz.tamur.guidesigner.SmallLinePanel;
import kz.tamur.guidesigner.noteeditor.NoteBrowser;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.users.ChatPanel;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.rt.adapters.OrRefListener;
import kz.tamur.rt.adapters.PanelAdapter;
import kz.tamur.rt.adapters.ReportPrinterAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.AboutDialog;
import kz.tamur.util.FrameTemplate;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.ReqMsgsList.MsgListItem;
import kz.tamur.util.SortedFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.HelpFile;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PathWordChange;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.client.User;
import com.cifs.or2.client.gui.FilterMenuItem;
import com.cifs.or2.client.gui.UserFontDlg;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.util.MMap;
/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.04.2004
 * Time: 15:05:45
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends FrameTemplate implements ActionListener, InterfaceManager, ReportObserver {
    
    private static GlobalConfig config = GlobalConfig.instance(Kernel.instance());
    private static Config conf = Utils.mergeConfig(config.getConfig());
    
    /** Хранит параметры градиентной заливки для главного фрейма системы. */
    public static  GradientColor GRADIENT_MAIN_FRAME = conf.getGradientMainFrame();

    /** Хранит параметры градиентной заливки для панели управления системы. */
    public static GradientColor GRADIENT_CONTROL_PANEL = conf.getGradientControlPanel();

    /** Хранит параметры градиентной заливки для панели меню системы. */
    public static GradientColor GRADIENT_MENU_PANEL = conf.getGradientMenuPanel();

    /** Настройка прозрачности панелей системы, позволяет включить/выключить возможность работы с ней. */
    public static boolean TRANSPARENT_MAIN = conf.isTransparentMain();
    
    public static boolean TRANSPARENT_DIALOG = conf.isTransparentDialog();

    /** Цвет, определяющий основную цветовую гамму интерфейсов. */
    private static Color colorMain = conf.getColorMain();
    
    /** Хранит прозрачность ячеек таблиц и деревьев */
    public static int TRANSPARENT_CELL_TABLE = conf.getTransparentCellTable();

    /** Хранит Цвет заголовков таблиц и деревьев */
    private static Color colorHeaderTable = conf.getColorHeaderTable();

    /** Хранит цвет фона заголовка выбранной вкладки */
    public static Color colorTabTitle = conf.getColorTabTitle();

    /** Хранит цвет фона заголовка фоновых вкладок */
    public static Color colorBackTabTitle = conf.getColorBackTabTitle();

    /** Хранит цвет шрифта заголовка выбранной вкладки */
    public static Color colorFontTabTitle = conf.getColorFontTabTitle();

    /** Хранит цвет шрифта заголовка фоновых вкладок */
    public static Color colorFontBackTabTitle = conf.getColorFontBackTabTitle();

    /** Хранит прозрачность заголовка фоновой вкладки */
    private static int transparentBackTabTitle = conf.getTransparentBackTabTitle();

    /** Хранит прозрачность фона заголовка выбранной вкладки */
    private static int transparentSelectedTabTitle = conf.getTransparentSelectedTabTitle();

    /** Хранит параметры градиентной заливки для полей не прошедших ФЛК */
    private static GradientColor colfldNoFLC = conf.getGradientFieldNOFLC();
    public static Color COLOR_FIELD_NO_FLC = (colfldNoFLC != null && colfldNoFLC.isEnabled()) ? colfldNoFLC.getStartColor()
            : new Color(255, 204, 204);
    
    /** Хранит названия иконок для компонентов */
    public static Map<String, String> iconsSettings = new HashMap<String, String>();
    
    private static final Log clientLog = LogFactory.getLog("ClientLog");
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private FrameManager frameManager = new FrameManager(this);
    private OrHiperTree currHiperTree;

    String defUserName = "";
    boolean isQuickStartShow = true;
    private Cursor rt;
    static boolean _iscurhelp;
    static int _helplangId;
    private KrnObject selectedIfcLang;
    private LangItem selectedIfcLangItem;
    
    Component leftComponent = null;
    Component rightComponent = null;
    
    private ChatPanel chatFrame;
    private static Timer changeTimer;
    private static JLabel newMessages = Utils.createLabel("Вам пришло сообщение!");
    private static boolean isRun = false;
    private Timer newMessageTest;

    private JLabel dataLandLabel = Utils.createLabel(res.getString("datalang"));

    private JSplitPane mainSpliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    //Элементы строки состояния
    JLabel actionLabel = Utils.createLabel("");
    JLabel progressLabel = Utils.createLabel("");
    JLabel alarmLabel = Utils.createLabel("Вам пришло уведомление!");

    OrGradientToolBar toolBar = kz.tamur.comps.Utils.createGradientToolBar();

    ToolButton applyBtn = new ToolButton("Apply32", res.getString("applyChanges"), actionLabel);
    ToolButton rollbackBtn = new ToolButton("Cancel32", res.getString("cancelChanges"), actionLabel);
    ToolButton prevBtn = new ToolButton("BackPage32", res.getString("backPage"), actionLabel);
    ToolButton runBtn = new ToolButton("buttonRun", res.getString("buttonRun"), actionLabel);
    ToolButton printBtn = new ToolButton("Printer32", res.getString("print"), actionLabel);
    ButtonsFactory.DesignerCompButton grafBtn = ButtonsFactory.createCompButton(res.getString("processMap"),
            Constants.SE_UI ? getImageIconExt("SrvMap", ".png") : getImageIcon("SrvMap"));
    ToolButton superBtn = new ToolButton("SuperProc", res.getString("superProcess"), actionLabel);
    ToolButton subBtn = new ToolButton("SubProc", res.getString("subProcess"), actionLabel);
    ToolButton debugBtn = new ToolButton("DebugProc",".gif", res.getString("debugProcess"), actionLabel);
    ToolButton pdBtn = new ToolButton("access", res.getString("pwdChange"), actionLabel);
    ToolButton hotKeyBtn = new ToolButton("hotKeys32",".gif", "HotKeys", actionLabel);
    ToolButton infoExchangeBtn = new ToolButton("info",".png", "infoStatusExchange", actionLabel);
    ButtonsFactory.DesignerCompButton exchangeBtn = ButtonsFactory.createCompButton(res.getString("exchange"),
            Constants.SE_UI ? getImageIconExt("BoxNode", ".png") : getImageIcon("BoxNode"));
    
    private OrGradientMenuBar mainMenu = new OrGradientMenuBar();
    private JMenuItem processMenu = null;
    private JMenu fileMenu = new ClientMenu(false, res.getString("file"));
    private JMenuItem openItem = new ClientMenuItem(res.getString("interface"));
    private JMenuItem exitItem = new ClientMenuItem(res.getString("exitMenu"), "Delete");
    private JCheckBoxMenuItem chatItem = kz.tamur.comps.Utils.createCheckMenuItem("Чат", ButtonsFactory.FN_CHAT);

    JMenu paramMenu = new ClientMenu(false, res.getString("settings"));
    JMenu langMenu = new ClientMenu(res.getString("interfaceLang"), true);
    JMenuItem editorItem = new ClientMenuItem(res.getString("editorSetup"));
    JMenuItem langItem = new ClientMenuItem(res.getString("language"));
    JCheckBoxMenuItem beepAllow=new ClientCheckBoxMenuItem(res.getString("beep"));
    JCheckBoxMenuItem scrollTabProc=new ClientCheckBoxMenuItem(res.getString("scrollTabProc"));
    JMenuItem configItem = new ClientMenuItem(res.getString("confInterface"));

    JMenu helpMenu = new ClientMenu(false, res.getString("help"));
    private JMenuItem helpItem = new ClientMenuItem(res.getString("helpTip"), "helpCtx");
    private JMenuItem help2Item = new ClientMenuItem(res.getString("helpMain"), "help");
    private JMenuItem aboutItem = new ClientMenuItem(res.getString("about"), "aboutS");
    private JMenuItem manualItem = new ClientMenuItem(res.getString("manual"), "help");

    private JMenu windowMenu;
    private JMenuItem tasksItem;
    private JMenuItem archiveItem;
    private JMenuItem dictItem;
    
    private JMenuItem[] lastAddsMenuItems = new JMenuItem[0]; 
    private JMenuItem runStartSrv = new ClientMenuItem(res.getString("runSrv"), "taskMenu");
    private JMenuItem searchSrv = new ClientMenuItem(res.getString("searchSrv"), "taskMenu");

    private JMenu reportMenu_ = new ClientMenu(res.getString("reports"));

    JPopupMenu docMenu;

    Border border2;
    private JMenuItem transItem = new JMenuItem();

    //MainFrame.ConnectionChecker connChecker = new MainFrame.ConnectionChecker();
    private DescLabel userLabel = kz.tamur.comps.Utils.createDescLabel("");
    private DescLabel accessStatus = kz.tamur.comps.Utils.createDescLabel("");

    private UIFrame mainUI;

    private LanguageCombo dataLangSelector = new LanguageCombo();

    private RuntimeController controller = new RuntimeController();
    private JMenu otherItem = new ClientMenu("Дополнительно...");
    private JProgressBar progress = new JProgressBar();
    
    private String appTitle = Funcs.sanitizeUsername(System.getProperty("title", ""));
    private long oldLangId = -1;
    public static final int NONE = 0;
    public static final int HELP = 1;
    private int helpMode = NONE;
    public String currLang = "RU";
    private ClientMenuItem[] helpMenuItems;
    private boolean isFirstBtn=true; 
    private static Timer stopAlarm;
    private PopupWindow popUpMessage;
    /** Возможно дублирование!
     * для исключения, необходимо вести список ProcessItem для которых была добавлена кнопка
     * */
    private List<ProcessItem> additionalItem = new ArrayList<ProcessItem>();
    public static final boolean ADVANCED_UI = "1".equals(System.getProperty("advanced_ui", "0"));
    private User user;
    /**
     * Construct the frame
     */
    public MainFrame(String currLang) {
        getToolkit().addAWTEventListener(controller,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.KEY_EVENT_MASK);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        if(currLang!=null){
            this.currLang=currLang;
        }
        
        iconsSettings.put("iconPopupColumn", Funcs.sanitizeHtml(System.getProperty("iconPopupColumn", "null")));
        iconsSettings.put("iconHyperPopup", Funcs.sanitizeHtml(System.getProperty("iconHyperPopup", "null")));
        iconsSettings.put("iconHyperColumn", Funcs.sanitizeHtml(System.getProperty("iconHyperColumn", "null")));
        iconsSettings.put("iconNodeParentExp", Funcs.sanitizeHtml(System.getProperty("iconNodeParentExp", "null")));
        iconsSettings.put("iconNodeParentCol", Funcs.sanitizeHtml(System.getProperty("iconNodeParentCol", "null")));
        iconsSettings.put("iconCalendar", Funcs.sanitizeHtml(System.getProperty("iconCalendar", "null")));
        iconsSettings.put("iconCopy", Funcs.sanitizeHtml(System.getProperty("iconCopy", "null")));
        
        try {
            InterfaceManagerFactory.instance().register(this);
            jbInit();
            setEnabledGraf(true);
            if (!ADVANCED_UI || mainUI == null)
            	showUserInterface(true);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainSpliter.setDividerLocation(1.0);
            validate();
            repaint();
        }
    }
    
    private class MyDispatcher implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (currHiperTree != null && currHiperTree.activeSrv) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q && !TaskTable.instance(false).getDialogIsActive()) {
                    TaskTable.instance(false).callquickList();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W && !TaskTable.instance(false).getDialogIsActive()) {
                    TaskTable.instance(false).callHotKeyList();
                }
                if (e.isControlDown() && e.getKeyCode() > 47 && e.getKeyCode() < 58 && !TaskTable.instance(false).getDialogIsActive()) {
                    for (int i = 0; i < 10; i++) {
                        if (e.getKeyCode() == (0x30 + i)) {
                            TaskTable.instance(false).callHotKey(i);
                            break;
                        }
                    }
                }
            }
            if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
                firePropertyChange("stopSearch", false, true);
            }
            return false;
        }
    }
    
    private void jbInit() throws Exception {
        Dimension size = new Dimension(35, 35);
    	Kernel krn = Kernel.instance();
    	user = krn.getUser();

        this.setTitle("Пользовательское приложение");
        this.setIconImage(getImageIcon("icon").getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Перезагрузка класса настроек
        reloadGlobalConfig();
        //Global KeyListener!!!
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
        
        applyBtn.addActionListener(this);
        Utils.setAllSize(applyBtn, size);
        applyBtn.setEnabled(false);
        applyBtn.setDesc(res.getString("applyChangesDesc"));

        rollbackBtn.addActionListener(this);
        Utils.setAllSize(rollbackBtn, size);
        rollbackBtn.setEnabled(false);
        rollbackBtn.setDesc(res.getString("cancelChangesDesc"));

        prevBtn.addActionListener(this);
        CSH.setHelpIDString(prevBtn, "system.nav.prev");
        Utils.setAllSize(prevBtn, size);
        prevBtn.setEnabled(false);
        prevBtn.setDesc(res.getString("backPageDesc"));

        runBtn.addActionListener(this);
        Utils.setAllSize(runBtn, size);
        runBtn.setEnabled(false);

        printBtn.addActionListener(this);
        Utils.setAllSize(printBtn, size);
        printBtn.setDesc(res.getString("printDesc"));

        grafBtn.addActionListener(this);
        Utils.setAllSize(grafBtn, size);
        grafBtn.setDesc(res.getString("processMapDesc"));

        superBtn.addActionListener(this);
        Utils.setAllSize(superBtn, size);
        superBtn.setDesc(res.getString("superProcessDesc"));

        subBtn.addActionListener(this);
        Utils.setAllSize(subBtn, size);
        subBtn.setDesc(res.getString("subProcessDesc"));

        debugBtn.addActionListener(this);
        Utils.setAllSize(debugBtn, size);
        debugBtn.setDesc(res.getString("debugProcessDesc"));

        pdBtn.addActionListener(this);
        Utils.setAllSize(pdBtn, size);
        pdBtn.setDesc(res.getString("pwdChangeDesc"));

        exchangeBtn.addActionListener(this);
        Utils.setAllSize(exchangeBtn, size);
        exchangeBtn.setDesc(res.getString("exchangeDesc"));

        infoExchangeBtn.addActionListener(this);
        Utils.setAllSize(infoExchangeBtn, size);
        infoExchangeBtn.setDesc(res.getString("infoExchangeDesc"));
		if(!user.isAdmin()) {
			exchangeBtn.setEnabled(false);
			infoExchangeBtn.setEnabled(false);
		}
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                try {
                    int shiftPressed = e.getModifiers() & InputEvent.ALT_MASK;
                    CommitResult cr = null;
                    if (shiftPressed != 0 && e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                        cr = beforePrevious();
                    }
                    afterPrevious(true, true, cr);
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        });

        toolBar.setBorder(border2);
        transItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0, false));
        toolBar.add(rollbackBtn);
        toolBar.add(applyBtn);
        toolBar.add(prevBtn);
        toolBar.add(runBtn);
        toolBar.add(printBtn);
        printBtn.setEnabled(false);
        Utils.setAllSize(grafBtn, size);
        toolBar.add(grafBtn);
        toolBar.add(superBtn);
        toolBar.add(subBtn);
        if (user.isAdmin()) {
            toolBar.add(debugBtn);
        }
        toolBar.add(pdBtn);
        toolBar.add(infoExchangeBtn);
        toolBar.add(exchangeBtn);
        // Инициализация главного меню
        openItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79, java.awt.event.KeyEvent.CTRL_MASK, false));
        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(115, java.awt.event.KeyEvent.ALT_MASK, false));
        helpItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.KeyEvent.SHIFT_MASK, false));


        mainMenu.add(fileMenu);
        String mode = Funcs.normalizeInput(System.getProperty("mode"));
        if (mode != null && mode.equals("debug"))
            fileMenu.add(openItem);
        
        if (ADVANCED_UI) {
        	processMenu = loadProcessMenu();
        	if (processMenu != null)
        		mainMenu.add(processMenu);
        }
        
        // почистить временную переменную
        additionalItem = null;

        runStartSrv.addActionListener(this);
        
        searchSrv.addActionListener(this);

        fileMenu.add(searchSrv);
        
        //Меню последних процессов, все время обновляет при выборе
        fileMenu.addMenuListener(new MenuListener() {
        	public void menuSelected(MenuEvent e) {
            	//list of last Added Srv
                loadLastSrvListToMenu();
            }
            public void menuDeselected(MenuEvent e){}
            public void menuCanceled(MenuEvent e){}
            }
        );
        if (getChatMode()) {
	        fileMenu.addSeparator();        
	        fileMenu.add(chatItem);
        }
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        List<LangItem> langItems = LangItem.getAll();
        ButtonGroup bg = new ButtonGroup();
        selectedIfcLangItem = LangItem.getById(Kernel.instance().getInterfaceLanguage().id);
        selectedIfcLang = selectedIfcLangItem.obj;
        for (int i = 0; i < langItems.size(); i++) {
            MenuLangItem mli = new MenuLangItem(langItems.get(i), false);
            mli.addActionListener(this);
            bg.add(mli);
            String code = mli.getLangItem().code;
            if ("RU".equals(code) || "KZ".equals(code) || "EN".equals(code)) {
                langMenu.add(mli);
            } else {
                otherItem.add(mli);
            }

            if (selectedIfcLang.id == mli.getLangItem().obj.id) {
                mli.setSelected(true);
            }
        }
        if (otherItem.getItemCount() > 0) {
            langMenu.add(otherItem);
        }
        beepAllow.setFont(Utils.getDefaultFont());
        scrollTabProc.setFont(Utils.getDefaultFont());
        configItem.setFont(Utils.getDefaultFont());
        paramMenu.add(langMenu);
        paramMenu.add(beepAllow);
        paramMenu.add(scrollTabProc);
        paramMenu.add(configItem);
        scrollTabProc.addActionListener(this);
        configItem.addActionListener(this);
        mainMenu.add(paramMenu);

        mainMenu.add(reportMenu_);
        reportMenu_.setVisible(false);

        if (!Constants.SE_UI && !ADVANCED_UI) {
            windowMenu = new ClientMenu(res.getString("window"));

            tasksItem = new ClientMenuItem(res.getString("taskMenu"), "taskMenu");
            tasksItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.KeyEvent.ALT_MASK, true));
            tasksItem.addActionListener(this);
            windowMenu.add(tasksItem);

            archiveItem = new ClientMenuItem(res.getString("archiveMenu"), "archiveMenu");
            archiveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.KeyEvent.ALT_MASK, true));
            archiveItem.addActionListener(this);
            windowMenu.add(archiveItem);

            dictItem = new ClientMenuItem(res.getString("catalogMenu"), "catalogMenu");
            dictItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.KeyEvent.ALT_MASK, true));
            dictItem.addActionListener(this);
            windowMenu.add(dictItem);

            mainMenu.add(windowMenu);
        }
        manualItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0, true));
        
        mainMenu.add(helpMenu);
        helpMenu.add(helpItem);
        aboutItem.addActionListener(this);
        manualItem.addActionListener(this);
        
        HelpSet hs = getHelpSet();
        if (hs != null) {
            HelpBroker hb = hs.createHelpBroker();
            CSH.setHelpIDString(help2Item, "main");
            help2Item.addActionListener(new CSH.DisplayHelpFromSource(hb));
        }
        CreateHelpMenu();
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        helpMenu.add(manualItem);
        openItem.addActionListener(this);
        exitItem.addActionListener(this);
        editorItem.addActionListener(this);
        helpItem.addActionListener(this);
        langItem.addActionListener(this);
        transItem.addActionListener(this);
        chatItem.addItemListener(new ItemListener(){
        	public void itemStateChanged(ItemEvent e) {   
        		if (chatItem.isSelected())
                {   
        			if (chatFrame == null)
        				chatFrame = new ChatPanel();
                	leftComponent = mainSpliter.getLeftComponent();
                	rightComponent = mainSpliter.getRightComponent();
                	if (leftComponent != null)
                		mainSpliter.remove(leftComponent);
                	if (rightComponent != null)
                		mainSpliter.remove(rightComponent);                	
                	mainSpliter.setLeftComponent(chatFrame);
                	dropToolBar();
                    validate();
                    repaint(); 
                    if (newMessageTest.isRunning())
    	        		newMessageTest.stop();
    				if (changeTimer != null && changeTimer.isRunning()) {
    					changeTimer.stop();
    					isRun = false;
    					newMessages.setForeground(Utils.getLightSysColor()); 
    					newMessages.setIcon(getImageIcon("NoMessage"));
    	 			}       	
                }
                else
                {
            		mainSpliter.remove(mainSpliter.getLeftComponent());
                	mainSpliter.setLeftComponent(leftComponent);
                	if (rightComponent != null)
                		mainSpliter.setRightComponent(rightComponent);
                	leftComponent = null;
                	rightComponent = null;
                	setToolbar(toolBar);
                	validate();
                    repaint();
                    if (!newMessageTest.isRunning())
        	        	newMessageTest.start();
                }
    	        chatFrame.setTimerCanWork(chatItem.isSelected());
        	}
        });
        
        // Создание основного интерфейса пользователя
        if (ADVANCED_UI) {
	        KrnObject uiObj = user.getIfc();
	        if (uiObj != null) {
	        	mainUI = frameManager.createFrame(uiObj, null, progress, progressLabel,null);
	        	absolute(uiObj, null, null, ARCH_RO_MODE, false, -1, 0, true, progress,"");
	        	doAfterOpen(mainUI);
	        	mainUI.setEvaluationMode(ARCH_RO_MODE);
	        	rollbackBtn.setEnabled(true);
	        }
        }
        
        mainSpliter.setDividerSize(3);
        JComponent h = createHyperTree(mainUI);
        currHiperTree = (OrHiperTree) h;
        
        mainSpliter.setLeftComponent(h);
        
        getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
        getContentPane().add(mainSpliter, BorderLayout.CENTER);
        if (Constants.SE_UI) {
            toolBar.setOpaque(false);
            mainMenu.setOpaque(false);
       }
        setToolbar(toolBar);
        setJMenuBar(mainMenu);

        res = ResourceBundle.getBundle( Constants.NAME_RESOURCES, new Locale("KZ".equals(selectedIfcLangItem.code)?"kk":"ru"));
        
        changeTitles(res);
        try{
            KrnClass cls_app=Kernel.instance().getClassByName("App");
            KrnObject[] objs=Kernel.instance().getClassObjects(cls_app,0);
            if(objs.length>0){
                String[] titles=Kernel.instance().getStrings(objs[0],"title",0,0);
                if(titles.length>0) appTitle = Funcs.sanitizeUsername(titles[0]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        setTitle(appTitle);
        dataLandLabel.setText(res.getString("datalang"));
        setDefaultIfcLanguage(currLang);
        exchangeBtn.setSelected(isTransportReady(MQ_TRANSPORT));
        setScrollTab();
        TaskTable tbl = currHiperTree.getTaskTable();
        // показать или скрыть монитор задач
        tbl.setVisible(Application.instance().isMonitorTask());
        if (Constants.SE_UI) {
            processMenu.setForeground(Utils.getDarkShadowSysColor());
            // градиент главного окна
            getContentGradientPanel().setGradient(GRADIENT_MAIN_FRAME);
            if (TRANSPARENT_MAIN) {
                getOrMenuBar().setGradient(GRADIENT_CONTROL_PANEL);
                mainSpliter.setOpaque(false);
                currHiperTree.setOpaque(false);
                currHiperTree.getTasksSpliter().setOpaque(false);
                tbl.setOpaque(false);
                if (tbl.getTable() != null) {
                    tbl.getTable().setOpaque(false);
                }
                if (tbl.getScroll() != null) {
                    tbl.getScroll().setOpaque(false);
                    tbl.getScroll().getViewport().setOpaque(false);
                }
                applyBtn.setOpaque(false);
                rollbackBtn.setOpaque(false);
                prevBtn.setOpaque(false);
                runBtn.setOpaque(false);
                printBtn.setOpaque(false);
                grafBtn.setOpaque(false);
                superBtn.setOpaque(false);
                subBtn.setOpaque(false);
                debugBtn.setOpaque(false);
                pdBtn.setOpaque(false);
                hotKeyBtn.setOpaque(false);
                infoExchangeBtn.setOpaque(false);
                exchangeBtn.setOpaque(false);

                if (processMenu != null) {
                    processMenu.setOpaque(false);
                }
                if (fileMenu != null) {
                    fileMenu.setOpaque(false);
                }
                if (paramMenu != null) {
                    paramMenu.setOpaque(false);
                }
                if (langMenu != null) {
                    langMenu.setOpaque(false);
                }
                if (helpMenu != null) {
                    helpMenu.setOpaque(false);
                }
                if (windowMenu != null) {
                    windowMenu.setOpaque(false);
                }
                if (reportMenu_ != null) {
                    reportMenu_.setOpaque(false);
                }
            }
            repaint();
        }
        if (getChatMode()) {
	        //Проверка новых сообщений каждые 5 секунд
	        newMessageTest = new Timer(5000, new ActionListener() {
	            public void actionPerformed(ActionEvent e) {    
	     			if (isRun) {
	     				changeTimer.stop();
	     				isRun = false;
	     			}
	            	if (getNewMessagesCount() > 0) {
	            		changeTimer = new Timer(500,new ActionListener() {
	            		     public void actionPerformed(ActionEvent e) {
	            		    	 if (newMessages.getForeground() == Utils.getLightSysColor())
	            		    		 newMessages.setForeground(Color.RED);
	            		    	 else if (newMessages.getForeground() == Color.RED)
	            		    		 newMessages.setForeground(Color.BLUE);
	            		    	 else if (newMessages.getForeground() == Color.BLUE)
	            		    		 newMessages.setForeground(Color.RED);
	            		      }
	            		   });
	            		changeTimer.start();
	            		isRun = true;
	            	}
	            	else
	            		newMessages.setForeground(Utils.getLightSysColor()); 
	            }
	        });
	        newMessageTest.start(); 
        }
        
        alarmLabel.setVisible(false);
        stopAlarm = new Timer(15000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (alarmLabel.isVisible() && alarmLabel.getForeground()==Color.RED) {
                    alarmLabel.setForeground(Color.BLACK);
                    //popUpMessage.setVisible(false);
                }else if (alarmLabel.isVisible() && alarmLabel.getForeground()==Color.BLACK) {
                    //alarmLabel.setVisible(false);
                    stopAlarm.stop();
                }
            }
        });
        
    }
    
    private static boolean getChatMode() {
		try {
			Kernel krn = Kernel.instance();
			if (krn.checkExistenceClassByName("ChatClass")) {
				int counter = 0;
				KrnClass chatClass = krn.getClassByName("ChatClass");
				String[] needAttributes = {"canDeleteFrom", "canDeleteTo", "datetime", "from", "status", "text", "to"};
				List<KrnAttribute> chatAttributes = krn
						.getAttributes(chatClass);
				for (int i = 0; i < needAttributes.length; i++)
					for (int j = 0; j < chatAttributes.size(); j++)
						if (needAttributes[i].equals(chatAttributes.get(j).name))
							counter++;
				if (counter == 7)
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
    
    private static float getNewMessagesCount() {
    	float newMessagesCount = 0;     	  	
    	try {
            Kernel krn = Kernel.instance();           
            String fromUser = krn.getUser().getName();            	
        	KrnClass class_ = krn.getClassByName("ChatClass");   
        	KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
            long[] massivObjectsId = new long[massivObjects.length];
            for (int j = 0; j < massivObjects.length; j++)
            	massivObjectsId[j] = massivObjects[j].id;
            StringValue[] massivTo = krn.getStringValues(massivObjectsId, class_.id, "to", 0, false, 0);
            StringValue[] massivFrom = krn.getStringValues(massivObjectsId, class_.id, "from", 0, false, 0);
            StringValue[] massivStatus = krn.getStringValues(massivObjectsId, class_.id, "status", 0, false, 0);
            UserSessionValue[] userSessions = krn.getUserSessions();                        
            for (int i = 0; i < massivObjects.length; i++)                   	
		    	if (massivTo[i].value.equals(fromUser) && massivStatus[i].value.equals("New"))
		            for (int j = 0; j < userSessions.length; j++)                   	
		            	if (userSessions[j].name.equals(massivFrom[i].value))
		            		newMessagesCount++;                
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return newMessagesCount;    	
    }
    
    /*
     * Обновляет файловое меню, для отображения последних процессов
     * @since 2011/06/07
     * @version 0.1
     */
    private void loadLastSrvListToMenu(){
    	QuickSrvListPanel qlist = new QuickSrvListPanel(true);
    	String[] names = qlist.getNameList();
    	long[] ids = qlist.getIdList();
    	String[] paths = qlist.getPathList();
    	int items = names.length;
    	
    	if(items == 0) return;
    	
    	fileMenu.removeAll();
    	
    	lastAddsMenuItems = new ClientMenuItem[items];
    	
    	//from init
    	String mode = Funcs.normalizeInput(System.getProperty("mode"));
        if (mode != null && mode.equals("debug"))
            fileMenu.add(openItem);
        
        //fileMenu.add(runStartSrv);

        fileMenu.add(searchSrv);
        
    	if(fileMenu.getItemCount()!=0)fileMenu.addSeparator();
    	fileMenu.add(new JLabel(res.getString("lastSrv")));
    	for(int i = 0; i < items; i++){
            JMenuItem jmenu = new ClientMenuItem(names[i], "taskMenu");
    		jmenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	String text =TaskTable.instance(false).getResource().getString("startProcMessage");
                	QuickSrvListPanel qlist = new QuickSrvListPanel(true);
                	String[] names = qlist.getNameList();
                	int timed = -1;
                	for(int i = 0; i < lastAddsMenuItems.length; i++) {
                		if(e.getActionCommand().equals(lastAddsMenuItems[i].getText())) timed++;
                		if(lastAddsMenuItems[i].equals(e.getSource()))
                			break;
                	}
                	System.out.println("tiemd: " + timed);
                	if(timed==-1) timed=0;
                	for(int i = 0; i < names.length; i++){
                		if(e.getActionCommand().equals(names[i]) && timed--==0){
                			int result=MessagesFactory.showMessageDialog(
                					(JFrame)TaskTable.instance(false).getTopLevelAncestor(),
                					MessagesFactory.QUESTION_MESSAGE, text+":'"+ names[i] +"'?", TaskTable.instance(false).li);
                			if(result ==ButtonsFactory.BUTTON_YES) {                				
                				try {
                					String[] res_=Kernel.instance().startProcess(qlist.getIdList()[i], null);
                					if (res_.length > 0 && !res_[0].equals("")) {
                						String msg = res_[0];
                						MessagesFactory.showMessageDialog((JFrame)TaskTable.instance(false).getTopLevelAncestor(),
                								MessagesFactory.ERROR_MESSAGE, msg);
                						qlist.deleteById(qlist.getIdList()[i]);
                					} else {
                					    List<String> param = new ArrayList<String>();
                				            if (res_.length > 3) {
                				                param.add(res_[3]);
                				            }
                						TaskTable.instance(false).startProcess(res_[1], param);
                						qlist.write(names[i], qlist.getPathList()[i], String.valueOf(qlist.getIdList()[i]));
                					}
                				} catch (Exception ex) {
                					ex.printStackTrace();
                				}
                			}
                		}
                	}
                	
                }
    		});
    		lastAddsMenuItems[i] = jmenu;
    		fileMenu.add(lastAddsMenuItems[i]);
    	}    	
    	fileMenu.addSeparator();
        fileMenu.add(exitItem);
    }

    private JComponent createHyperTree(UIFrame mainUI) {
        Kernel krn = Kernel.instance();
        try {
            KrnClass cls = krn.getClassByName("MainTree");
            KrnObject obj = krn.getClassObjects(cls, 0)[0];
            OrHiperTree hyperTree = new OrHiperTree(null, obj, "", "",
                    Collections.EMPTY_MAP, Collections.EMPTY_MAP,
                    Collections.EMPTY_MAP, Mode.RUNTIME, Collections.EMPTY_MAP,
                    mainUI);
            hyperTree.setMinimumSize(new Dimension(300, 1000));
            return hyperTree;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JComponent createStatusBar() throws KrnException {
        dataLangSelector.setDesc(res.getString("dataLangDesc"));
        accessStatus.setDesc(res.getString("baseLabelDesc"));
        userLabel.setDesc(res.getString("userLabelDesc"));
        accessStatus.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        userLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        dataLangSelector.addActionListener(this);
        dataLangSelector.setSelectedLanguage(Kernel.instance().getDataLanguage());
        // Загрузка наименования
        final Kernel krn = Kernel.instance();
        String title = getCurrentDbName();
        accessStatus.setFont(Utils.getDefaultFont());
        accessStatus.setForeground(Utils.getDarkShadowSysColor());
        accessStatus.setIcon(getImageIcon("Drive"));
        accessStatus.setIconTextGap(10);
        accessStatus.setText(title);
        DesignerStatusBar statusBar = new DesignerStatusBar();        
        statusBar.setFirstComponent(stateFrame);
        statusBar.addAnyComponent(actionLabel, 1);
        statusBar.addSeparator();
        statusBar.addAnyComponent(alarmLabel, 1);
        statusBar.addSeparator();
        newMessages.setForeground(Utils.getLightSysColor());
    	newMessages.setBackground(Utils.getLightSysColor());
        statusBar.addAnyComponent(newMessages);
        statusBar.addSeparator();
        userLabel.setIcon(getImageIcon("userNode"));
        userLabel.setIconTextGap(5);
        userLabel.setText(krn.getUser().getUserSign() + ": " + krn.getUser().getName());
        statusBar.addAnyComponent(userLabel);
        statusBar.addSeparator();
        //accessStatus.setText(krn.getUser().getBaseCode());
        statusBar.addAnyComponent(accessStatus);
        statusBar.addSeparator();
        Dimension size = new Dimension(150, 15);
        Utils.setAllSize(progressLabel, size);
        progressLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        progressLabel.setText("");
        statusBar.addAnyComponent(progressLabel);

        progress.setBorderPainted(false);
        Utils.setAllSize(progress, size);
        statusBar.addAnyComponent(progress);
        statusBar.addSeparator();
        statusBar.addAnyComponent(dataLandLabel);
        statusBar.addAnyComponent(dataLangSelector);
        statusBar.addCorner();
        return statusBar;
    }

    public static String getCurrentDbName() {
        Kernel krn = Kernel.instance();
        KrnObject db = krn.getCurrentDb();
        String name = "";
        try {
            String[] names = krn.getStrings(db,"наименование", 0, 0);
            name = names.length > 0 ? names[0] : null;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return "БД: " + name;
    }

    private void showUserInterface(boolean isHiperTree) {
        KrnObject iObj = Kernel.instance().getInterface();
        if (iObj != null) {
            try {
                absolute(iObj, null, "", InterfaceManager.SERVICE_MODE, isHiperTree, 0, 0, false,"");
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        } else {
            frameManager.absolute(new KrnObject(0, "", 0), null, null, null,null);
        }
    }

    //Процедура выхода из приложения
    protected void processWindowEvent(WindowEvent e) {
    	boolean res = true;
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            try {
                res = processClose();
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
        if (res)
        	super.processWindowEvent(e);
    }

    public boolean processClose() throws KrnException {
        int n = MessagesFactory.showMessageDialog(this,
                MessagesFactory.QUESTION_MESSAGE,
                res.getString("exitMessage"), selectedIfcLangItem);
        if (n == ButtonsFactory.BUTTON_YES) {
            try {
            	CommitResult cr = commitCurrent(new String[]{res.getString("continue"),
                                               res.getString("exit")});
                if (cr == CommitResult.CONTINUE_EDIT) {
                    return false;
                }
                //connChecker.interrupt();
                String name=Kernel.instance().getUser().getName();
                String shost=Kernel.instance().getServerHost();
                String sport=Kernel.instance().getServerPort();
                Kernel.instance().release();
                clientLog.info("Завершение работы.Пользователь:"+name+" Сервер host:"+shost+" port:"+sport);
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    //connChecker.interrupt();
                    Kernel.instance().release();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    // ActionListener implementation
    public void actionPerformed(ActionEvent e) {
        Cursor oldCursor = getCursor();
        Cursor tempcur = getCursor();

        if (!tempcur.getName().equals("MyHelp_red32x32")) {
            CursorToolkit.startWaitCursor(this);
        }

        final Kernel krn = Kernel.instance();
        final Object src = e.getSource();
        try {
            // Выход из программы
            if (src == exitItem) {
                if (!oldCursor.getName().equals("MyHelp_red32x32")) {
                	processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }
            } else if (src == tasksItem) {
                if (currHiperTree != null &&
                        currHiperTree.getBtnSrv().isVisible()) {
                    currHiperTree.getBtnSrv().doClick();
                }               
            } else if (src == archiveItem) {
                if (currHiperTree != null &&
                        currHiperTree.getBtnArh().isVisible()) {
                    currHiperTree.getBtnArh().doClick();
                }
            } else if (src == dictItem) {
                if (currHiperTree != null &&
                        currHiperTree.getBtnSpr().isVisible()) {
                    currHiperTree.getBtnSpr().doClick();
                }
            } else if (src == editorItem) {
                if (!oldCursor.getName().equals("MyHelp_red32x32")) {
                    UserFontDlg dlg = new UserFontDlg(this, "Выбор шрифта и настройка фона...", true);
                    dlg.setVisible(true);
                }
            } else if (src == runStartSrv) {
            	SmallLinePanel slPanel = new SmallLinePanel(SmallLinePanel.RUN_SRV, currHiperTree, res.getLocale());
        		DesignerDialog dlg = new DesignerDialog((Window)TaskTable.instance(false).getTopLevelAncestor(), res.getString("runSrv"), slPanel);
        		dlg.setOnlyOkButton();
        		dlg.setOkVisible(false);
        		dlg.show();
        		
            } else if (src == searchSrv) {
            	SmallLinePanel slPanel = new SmallLinePanel(SmallLinePanel.SEARCH_SRV_IN_ORHIPERTREE, currHiperTree, res.getLocale());
        		DesignerDialog dlg = new DesignerDialog((Window)TaskTable.instance(false).getTopLevelAncestor(), res.getString("searchSrv"), slPanel);
        		dlg.setOnlyOkButton();
        		dlg.setOkVisible(false);
        		dlg.show();
            }
            // Открытие интерфейса
            else if (src == openItem) {
                /*KrnClass uiCls = krn.getClassByName("UI");
                InterfaceList intfList = new InterfaceList(uiCls, "title");
                intfList.setFont(Utils.getDefaultFont());
                JScrollPane sp = new JScrollPane(intfList);
                sp.setPreferredSize(new Dimension(600, 400));
                OpenInterfacePanel pan;// = new OpenInterfacePanel(sp, intfList);
                DesignerDialog dlg = new DesignerDialog(this, "Открытие интерфейса", pan);
                dlg.show();
                if (dlg.isOK()) {
                    KrnObject obj = intfList.getSelectedObject();
                    try {
                        absolute(obj, null, "", 0, false, 0, null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Object[] options = {"   ОК   "};
                        JOptionPane.showOptionDialog(this, "Ошибка загрузки интерфейса.",
                                "Внимание!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                                null, options, options[0]);
                    }
                }
                setCursor(cursor_);*/
            } else if (src == helpItem) {
                if (getGlassPane().getCursor().getType() == Cursor.WAIT_CURSOR) {
                    CursorToolkit.stopWaitCursor(this);
                }
                changeHelpMode();
            }

            // Переход на предыдущий интерфейс
            else if (src == prevBtn) {
                CommitResult cr = beforePrevious();
                afterPrevious(true, true, cr);
                CursorToolkit.stopWaitCursor(this);
            }
            else if (src == runBtn) {
                CommitResult cr = beforePrevious();
                if (cr == CommitResult.WITH_ERRORS  || cr == CommitResult.WITHOUT_ERRORS) {
                    int res = TaskTable.instance(false).next(this);
                    if (res == ButtonsFactory.BUTTON_YES) {
                        afterPrevious(true, true, cr);
                    }
                }else
                    afterPrevious(true, true, cr);
                CursorToolkit.stopWaitCursor(this);
            }
            // Сохранение изменений
            else if (src == applyBtn) {
            	boolean[] refresh = { false };
                commitCurrent(new String[]{res.getString("continue"),
                                           res.getString("save")}, refresh);
                if (refresh[0])
                    rollbackCurrent();
            }
            // Отмена изменений
            else if (src == rollbackBtn) {
                int res1 = MessagesFactory.showMessageDialog(this,
                        MessagesFactory.QUESTION_MESSAGE,
                                                    res.getString("cancelChanges") + "?", selectedIfcLangItem);
                if (res1 == ButtonsFactory.BUTTON_YES) {
                    rollbackCurrent();
                }
            }
            // печать документов
            else if (src == printBtn) {
                UIFrame frame = frameManager.getCurrentFrame();
                if (frame != null) {
                    if (docMenu == null) {
                        docMenu = new JPopupMenu();
                        ReportRecord root = frame.getRootReport();
                        loadReports(root, docMenu);

/*
                        Collection printers = frame.getReports();
                        for (Iterator it = printers.iterator(); it.hasNext();) {
                            ReportPrinter p = (ReportPrinter) it.next();
                            PrinterItem mi = new PrinterItem(p);
                            mi.addActionListener(this);
                            docMenu.add(mi);
                        }
*/
                    }
                    docMenu.show(printBtn, 0, printBtn.getHeight());
                }
            }
            // просмотр графа процесса
            else if (src == grafBtn) {
                TaskTable.instance(false).setGrafVisible(grafBtn.isSelected());
            } else if (src == superBtn) {
                TaskTable.instance(false).disposeGraf(1);
            } else if (src == subBtn) {
                TaskTable.instance(false).disposeGraf(-1);
            } else if (src == debugBtn) {
                TaskTable.instance(false).getDebugPane();
            } else if (src == pdBtn) {
            	showChangePasswordDialog();
            } else if (src == infoExchangeBtn) {
            	boolean isReady=isTransportReady(MQ_TRANSPORT);
                exchangeBtn.setSelected(isReady);
                MessagesFactory.showMessageDialog( this, MessagesFactory.INFORMATION_MESSAGE,isReady?"MQ запущен!":"MQ остановлен!");
            } else if (src == exchangeBtn) {
                krn.startTransport(((JToggleButton)src).isSelected()?MQ_TRANSPORT:-MQ_TRANSPORT);
            } 
            // Запуск отчета
//            else if (src instanceof JButton && ((JButton)src).getParent() instanceof PrinterLangItem) {
            else if (src instanceof PrinterLangItem) {
                if (oldCursor.getName().equals("helpcursor")) {
                } else {
                    PrinterLangItem pi = (PrinterLangItem) src;
                    PrinterMenu pm = pi.getPrinterMenu();
                    pi.setBackground(Utils.getLightGraySysColor());
                    long time = System.currentTimeMillis();
                    pm.getPrinter().print(pi.getLanguage());
                    System.out.println("Report time: " + (System.currentTimeMillis() - time));
                }
            }
            else if (src instanceof PrinterMenuItem) {
                if (oldCursor.getName().equals("helpcursor")) {
                } else {
                    PrinterMenuItem pmi = (PrinterMenuItem) src;
                    if (pmi.getLanguage() != null) {
                        pmi.setBackground(Utils.getLightGraySysColor());
                        long time = System.currentTimeMillis();
                        pmi.getPrinter().print(pmi.getLanguage());
                        System.out.println("Report time: " + (System.currentTimeMillis() - time));
                    }
                }
            }
            // Задание пользовательского фильтра
            else if (src instanceof FilterMenuItem) {
                FilterMenuItem fmi = (FilterMenuItem) src;
                krn.getUser().setCurrentFilter(fmi.filter);
                //@todo Разобраться frm.commit();
                //UIFrame frm = frameManager.getCurrentFrame();
                //frm.rollback();
            //}
            // Управление доступом
/*
            else if (src == accessItem) {

                if (oldCursor.getName().equals("helpcursor")) {
                } else {
                    if (!oldCursor.getName().equals("helpcursor")) {
                        setCursor(cursor_);
                    }
                }
*/
            } else if (src instanceof MenuLangItem) {
                // Выбор языка интерфейса
                LangItem li = ((MenuLangItem) src).getLangItem();
                if (li != null) {
                    if ("KZ".equals(li.code)) {
                        res = ResourceBundle.getBundle(
                                Constants.NAME_RESOURCES, new Locale("kk"));
                    } else {
                        res = ResourceBundle.getBundle(
                                Constants.NAME_RESOURCES, new Locale("ru"));
                    }
                    changeTitles(res);
                    UIFrame frm = frameManager.getCurrentFrame();
                    String title = "";
                    if(selectedIfcLang.id != li.obj.id){
                    	selectedIfcLang = li.obj;
                        Utils.setLangId(selectedIfcLang.id);
                        krn.setLang(li.obj);
                        selectedIfcLangItem = li;
                        if(frm!=null){
                            frm.setInterfaceLang(selectedIfcLang, res, true);
                            if (frm.getPanel() != null)
                                title = frm.getPanel().getTitle();
                        }
                        changePrinterTitles(docMenu);
                        changeHelpTitles();
                    }
                    if (!"Безымянный".equals(title) && title!=null && !title.equals("")) {
                        setTitle(appTitle + " - [" + title + "]");
                    } else {
                        setTitle(appTitle);
                    }
                    currHiperTree.setLang(li.obj.id);
                    TaskTable.instance(false).setLang(li.obj.id,true);
                    dataLandLabel.setText(res.getString("datalang"));
                    Properties props = new Properties();
                    try {
                        File dir = new File(Utils.getUserWorkingDir());
                        dir.mkdirs();

                        File f = new File(dir, "propsJboss");

                        FileInputStream fis = new FileInputStream(f);
                        props.load(fis);
                        fis.close();
                        FileOutputStream fos = new FileOutputStream(f);
                        //props.setProperty("login", defUserName);
                        //props.setProperty("quickstart", isQuickStartShow ? "1" : "0");
                        props.setProperty("currlang", li.code);
                        props.store(fos, "Properties");
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            } else if (src == dataLangSelector) {
                // Выбор языка данных
                LangItem li = (LangItem) dataLangSelector.getSelectedItem();
                if (li != null && li.obj != null && li.obj.id != oldLangId) {
                    oldLangId = li.obj.id;
                    UIFrame frm = frameManager.getCurrentFrame();
                    frm.setDataLang(li.obj, true);
                    changePrinterSelection(docMenu);
                    Properties props = new Properties();
                    try {
                        File dir = new File(Utils.getUserWorkingDir());
                        dir.mkdirs();

                        File f = new File(dir, "propsJboss");

                        FileInputStream fis = new FileInputStream(f);
                        props.load(fis);
                        fis.close();
                        FileOutputStream fos = new FileOutputStream(f);
                        props.setProperty("currdatalang", li.code);
                        props.store(fos, "Properties");
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else if (src == scrollTabProc) {
                currHiperTree.setScrollTabProc(scrollTabProc.isSelected());
                Properties props = new Properties();
                try {
                    File dir = new File(Utils.getUserWorkingDir());
                    dir.mkdirs();

                    File f = new File(dir, "propsJboss");

                    FileInputStream fis = new FileInputStream(f);
                    props.load(fis);
                    fis.close();
                    FileOutputStream fos = new FileOutputStream(f);
                    props.setProperty("scrollTab", scrollTabProc.isSelected()?"true":"false");
                    props.store(fos, "Properties");
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else  if (src == configItem) {
                showConfigEditor();
            } else  if (src == aboutItem) {
                showAbout();
            } else  if (src == manualItem) {
                showManual();
            }
            // Обновление

            //else if (src == refreshBtn)
            //  refreshCurrent();
        } catch (KrnException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        if (getGlassPane().getCursor().getType() == Cursor.WAIT_CURSOR) {
            CursorToolkit.stopWaitCursor(this);
        }
    }

    public void changeHelpMode() {
        if (helpMode == HELP) {
            CursorToolkit.stopCursor(this);
            helpMode = NONE;
            controller.setCurrentMode(helpMode);
        } else {
            rt = kz.tamur.comps.Utils.getHelpCursor();
            CursorToolkit.startCursor(this, rt);
            helpMode = HELP;
            controller.setCurrentMode(helpMode);
        }
    }

    private void loadReports(ReportRecord parent, Object menu) {
        List<ReportRecord> records = parent.getChildren();
        for (ReportRecord record : records) {
            if (record.isFolder()) {
                UIFrame frame = frameManager.getCurrentFrame();
                String name = record.getName(frame);
                ReportMenu subMenu = new ReportMenu(name, record, true);
                loadReports(record, subMenu);
                if (menu instanceof JMenu)
                    ((JMenu)menu).add(subMenu);
                else
                    ((JPopupMenu)menu).add(subMenu);
            } else {
                boolean reportVisible = false;
                try {
                    KrnObject obj = Kernel.instance().getObjectsByIds(new long[] {record.getObjId()},-1)[0];

                	ClassNode cnode = Kernel.instance().getClassNode(obj.classId);
                    KrnAttribute basesAttr = cnode.getAttribute("bases");
                    KrnObject[] bases = null;
                    if (basesAttr != null)
                        bases = Kernel.instance().getObjects(obj, "bases", 0);
                    else
                        clientLog.debug("Attribute \"bases\" not found!");
                    //KrnObject[] bases = Kernel.instance().getObjects(obj, "bases", 0);
                    KrnObject curDb = Kernel.instance().getCurrentDb();
                    if (bases != null && bases.length > 0) {
                        for (int k=0; k<bases.length; k++) {
                            KrnObject base = bases[k];
                            if (base.id == curDb.id) {
                                reportVisible = true;
                                break;
                            }
                        }
                    } else {
                        reportVisible = true;
                    }
                } catch (Exception e) {
                    reportVisible = true;
                }

                if (reportVisible) {
                    UIFrame frame = frameManager.getCurrentFrame();
                    ReportPrinter rp = new ReportPrinterAdapter(frame,
                            frame.getPanel(), record);

                    JMenuItem ri = null;
                    List<LangItem> langItems = LangItem.getAll();
                    int reportsCount = 0;
                    LangItem existLang = null;
                    for (int i = 0; i < langItems.size(); i++) {
                        LangItem li = langItems.get(i);
                        if (rp.hasReport(li.obj)) {
                            existLang = li;
                            reportsCount++;
                        }
                    }

                    if (reportsCount == 1)
                        ri = new PrinterMenuItem(rp, true, existLang);
                    else
                        ri = new PrinterMenu(rp, true);

                    //PrinterItem mi = new PrinterItem(rp);
                    //mi.addActionListener(this);
                    if (menu instanceof JMenu)
                        ((JMenu)menu).getPopupMenu().add(ri);
                    else
                        ((JPopupMenu)menu).add(ri);
                }
            }
        }

    }

    
    private void unloadReports(Object menu) {
    	if (menu != null) {
	        Component children[] = null;
	    	if (menu instanceof JMenu)
	    		children = ((JMenu)menu).getComponents();
	        else
	        	children = ((JPopupMenu)menu).getComponents();
	    	
	        for (Component c : children) {
	            if (c instanceof PrinterMenuItem) {
	            	if (((PrinterMenuItem)c).visibleRef != null) {
		            	((PrinterMenuItem)c).visibleRef.removeOrRefListener((PrinterMenuItem)c);
		            	((PrinterMenuItem)c).visibleRef.removeFromParents();
		            	((PrinterMenuItem)c).visibleRef = null;
	            	}
	            } else if (c instanceof JMenu) {
	            	unloadReports(c);
	            }
	        }
    	}
    }

    @Override
    public CommitResult beforePrevious() throws KrnException {
        return beforePrevious(true, true);
    }
    
    @Override
    public CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException {
        return beforePrevious(check, canIgnore, null, null);
    }
    
    @Override    
    public CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException {
    	CommitResult cr = CommitResult.CONTINUE_EDIT;
        if (frameManager.hasPrev()) {
            CursorToolkit.startWaitCursor(this);
            UIFrame frm = frameManager.getCurrentFrame();
            OrPanel p = frm.getPanel();
            ASTStart template = p.getBeforeCloseTemplate();
            if (template != null) {
                ClientOrLang orlang = new ClientOrLang(frm);
                Map<String, Object> vc = new HashMap<String, Object>();
                try {
                	boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                    if (calcOwner)
                    	OrCalcRef.makeCalculations();
                } catch (Exception ex) {
                    Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
                }
            }
            cr = commitCurrent( new String[] { titleContinueEdit == null ? res.getString("continue") : titleContinueEdit, titleiIgnoreError == null ? res.getString("ignore") : titleiIgnoreError}, null, check, canIgnore);
            if (!check || cr != CommitResult.CONTINUE_EDIT) {
                TaskTable.instance(false).setOpenUI(null);
                ASTStart templateCl = p.getAfterCloseTemplate();
                if (templateCl != null) {
                    ClientOrLang orlang = new ClientOrLang(frm);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    try {
                    	boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
                        if (calcOwner)
                        	OrCalcRef.removeCalculations();
                    } catch (Exception ex) {
                        Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
                    }
                }
            }
        }
        return cr;
    }

    @Override
    public void afterPrevious(boolean isShow, boolean check, CommitResult cr) throws KrnException {
        if (frameManager.hasPrev() && (!check || cr != CommitResult.CONTINUE_EDIT)) {
            if (frameManager.getCurrentFrame() != null) {
                frameManager.getCurrentFrame().clear();
            }
            frameManager.prev();
            Activity activity = TaskTable.instance(false).getSelectedActivity();
            if (activity != null) {
                activity.autoIfc = false;
            }
            if (isShow) {
                showCurrent(frameManager.getCurrentFrame().getEvaluationMode(), "");
            }
            if (/* ADVANCED_UI && */mainUI != null && frameManager.getIndex() == 0) {
                rollbackCurrent();
            }
        }
    }
        
    public UIFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid,
            int mode, long flowId, boolean shareCash, boolean fork) throws KrnException {

    	FrameManager fm = frameManager;
        UIFrame oldFrm = fm.getCurrentFrame();
        UIFrame frm = fm.absolute(uiObj, shareCash ? oldFrm : null, progress, progressLabel,selectedIfcLang);
        
        if (frm != null) {
        	if (shareCash && oldFrm != null) {
        		frm.setCache(oldFrm.getCash());
                oldFrm.getCash().setLogIfcId(frm.getObj().id);
        	} else {
        		frm.getCash().reset(flowId);
        	}
            frm.setTransactionId(tid);
            frm.setInterfaceLang(selectedIfcLang, res, false);
            //frm.setInterfaceLang(selectedIfcLang, false);
            frm.setDataLang(((LangItem)dataLangSelector.getSelectedItem()).obj, false);
            frm.setFlowId(flowId);
            frm.setEvaluationMode(mode);
            frm.setInitialObjs(objs);

            Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
            objs = doBeforeOpen(frm, objs, objsMap);
            evaluateAllRefs(frm, objs, objsMap);
            doAfterOpen(frm);
            Component[] components = frm.getPanel().getComponents();
            for (int i = 0; i < components.length; i++) {
            	getComponents(components[i]);
            }
        }
        return frm;
    }

    private void getComponents(Component component) {
    	if (component instanceof OrGuiContainer || component instanceof JViewport) {
    		Component[] components = ((Container) component).getComponents();
            for (int i = 0; i < components.length; i++) {
            	getComponents(components[i]);
            }
    	} else if (component instanceof OrTreeCtrl) {
    		if (((OrTreeCtrl) component).getRowCount() > 0) {
    			if (((OrTreeCtrl) component).getSelectionCount() > 0) {
		    		int selectedIndex = ((OrTreeCtrl) component).getSelectionRows()[0];
		    		((OrTreeCtrl) component).collapseRow(0);
		    		((OrTreeCtrl) component).expandRow(0);
		    		if (selectedIndex < ((OrTreeCtrl) component).getRowCount()) {
		    			((OrTreeCtrl) component).setSelectionRow(selectedIndex);
		    		}
    			} else {
		    		((OrTreeCtrl) component).setSelectionRow(0);
    			}
    		}
    	}
    }
    
    public UIFrame getInterfacePanel(
    		KrnObject uiObj,
    		KrnObject[] objs,
    		long tid,
    		int mode,
    		boolean shareCash,
    		boolean fork
    ) throws KrnException {
        UIFrame oldFrm = frameManager.getCurrentFrame();
        UIFrame frm = getInterface(uiObj, objs, tid, mode, oldFrm.getFlowId(), shareCash, fork);
        frm.setInterfaceLang(selectedIfcLang, res);
        return frm;
    }


    public void releaseInterface(boolean commit) {

        UIFrame frm = frameManager.getCurrentFrame();
        OrPanel p = (OrPanel)frm.getPanel();
        ASTStart template = p.getBeforeCloseTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("COMMIT", commit);
            try {
            	boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
            }
        }
        if (!frm.isSharedCache() && (frm.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
        	if (commit) {
	            try {
	            	frm.getCash().commit(frm.getFlowId());
	                frm.getRef().commitChanges(this);
	            } catch (KrnException ex) {
	                ex.printStackTrace();
	                MessagesFactory.showMessageDialog( this, MessagesFactory.ERROR_MESSAGE,Constants.ERROR_MESSAGE_1 + ex.getMessage());
	            }
        	} else {
        		try {
        			frm.getCash().rollback(frm.getFlowId());
	            } catch (KrnException ex) {
	                ex.printStackTrace();
	                MessagesFactory.showMessageDialog( this, MessagesFactory.ERROR_MESSAGE,Constants.ERROR_MESSAGE_1 + ex.getMessage());
	            }
        	}
            frm.clear();
        } else {
        	if (frm.isSharedCache()) {
            	if (commit)
            		frm.getCash().clearCacheChange(frm.getObj().id, this);
            	else
            		frm.getCash().undoCacheChange(frm.getObj().id, this);
                
        		frm.setCache(null);
        	} else
        		frm.clear();
        }
        ASTStart templateCl = p.getAfterCloseTemplate();
        if (templateCl != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("COMMIT", commit);
            try {
            	boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
                if (calcOwner)
                	OrCalcRef.removeCalculations();
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
            }
        }
        frameManager.prev();
    }

    public KrnObject getInterfaceLang() {
        return frameManager.getCurrentFrame().getIfcLang();
    }

    public KrnObject getDataLang() {
        return frameManager.getCurrentFrame().getDataLang();
    }

    public boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath, int mode, boolean isHiperTree, long tid, long flowId, boolean isBlockErrors, String uiType) throws KrnException {
        if (uiObj != null) {
            if(!isBlockErrors) {
            	CommitResult cr = commitCurrent(new String[]{res.getString("continue"),
                                                        res.getString("exit")});
                if (cr == CommitResult.CONTINUE_EDIT) {
                    return false;
                }
            }
            new IfcLoader(uiObj, objs, refPath, mode, isHiperTree, tid, flowId, isBlockErrors,uiType).start();
        }
    	return true;
    }

    public boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath,
                            int mode, boolean isHiperTree, long tid, long flowId,
                            boolean isBlockErrors, JProgressBar progress, String uiType)
 throws KrnException {
        CursorToolkit.startWaitCursor(this);
        long start = System.currentTimeMillis();
        boolean result = false;
        UIFrame frm = frameManager.absolute(uiObj, null, progress, progressLabel,selectedIfcLang);
        if (frm != null) {
            LangItem li = (LangItem) dataLangSelector.getSelectedItem();
            frm.setTransactionId(tid);
            frm.setInterfaceLang(selectedIfcLang, res, false);
            frm.setDataLang(li.obj, false);
            frm.setFlowId(flowId);
            frm.getCash().reset(flowId);
            frm.setInitialObjs(objs);

            Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
            objs = doBeforeOpen(frm, objs, objsMap);

            PanelAdapter pa = frm.getPanelAdapter();
            if (pa.isEnabled()) {
                if (mode == SPR_RO_MODE) {
                    pa.setEnabled(pa.getDataRef() == null);
                } else if (mode == SERVICE_MODE || mode == SPR_RW_MODE || mode == ARCH_RW_MODE || mode == ARCH_RO_MODE) {
                    pa.setEnabled(true);
                } else {
                    pa.setEnabled(false);
                }
            } else {
                pa.setEnabled(false);
            }

            evaluateAllRefs(frm, objs, objsMap);
            if (!ADVANCED_UI || mainUI == null || frameManager.getIndex() > 0) {
                showCurrent(mode, uiType);
            }
            result = true;
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("Время генерации интерфейса : " + elapsedTimeMillis / 1000F + " сек.");
        CursorToolkit.stopWaitCursor(this);
        return result;
    }


    public int getEvaluationMode() {
        return (frameManager.getCurrentFrame() != null) ? frameManager.getCurrentFrame().getEvaluationMode() : 0;
    }

    public Kernel getKernel() {
        return Kernel.instance();
    }

    public UIFrame getCurrentInterface() {
        return frameManager.getCurrentFrame();
    }

    public int getSplitLoc(int loc_){
        int loc = mainSpliter.getDividerLocation();
        if(mainSpliter.getRightComponent() == null){
            if(loc_==-1)
            loc_ = (int) ((mainSpliter.getBounds().getWidth() - mainSpliter.getDividerSize()) * 1 / 3);
        }else{
            loc_=loc;
        }
        return loc_;
    }
    public int showCurrentProcess(JGraph p,int loc_) /*throws KrnException  */ {
        if (grafBtn.isEnabled()) {
            if (p != null) {
                ((JGraph) p).setPreferredSize(new Dimension(10, 10));
            } else {
                setEnabledSuperBtn(false);
                setEnabledSubBtn(false);
            }
            if (mainSpliter.getRightComponent() != p) {
                int loc = mainSpliter.getDividerLocation();
                if(mainSpliter.getRightComponent() == null){
                    if(loc_==-1)
                    loc_ = (int) ((mainSpliter.getBounds().getWidth() - mainSpliter.getDividerSize()) * 1 / 3);
                }else{
                    loc_=loc;
                }
                mainSpliter.setDividerLocation(loc_);
                mainSpliter.setRightComponent(p);
            }
            validate();
            repaint();
        }
        return loc_;
    }

    private void showCurrent(int mode, String uiType) throws KrnException {
        boolean isDlg = Constants.ACT_DIALOG_STRING.equals(uiType) || Constants.ACT_AUTO_STRING.equals(uiType);
        UIFrame frm = frameManager.getCurrentFrame();
        frm.setInterfaceLang(selectedIfcLang, res);
        frm.setDataLang(((LangItem) dataLangSelector.getSelectedItem()).obj, true);
        frm.setEvaluationMode(mode);
        Component c = mainSpliter.getRightComponent();
        controller.setFrame(frm);
        if (c != null && !isDlg) {
            mainSpliter.remove(c);
        }
        OrPanel p = frm.getPanel();
        String title = (p != null) ? p.getTitle() : "";
        if (title != null && !title.equals("")) {
            setTitle(appTitle + " - [" + Funcs.sanitizeUsername(title) + "]");
        }
        unloadReports(docMenu);
        docMenu = null;
        printBtn.setEnabled(frm.getRootReport() != null && frm.getRootReport().getChildren().size() > 0);
        if (p != null) {
            p.setPreferredSize((Dimension) null);
        }
        if (frameManager.getIndex() > 0) {
            if (!Constants.SE_UI && !ADVANCED_UI) {
                tasksItem.setEnabled(false);
                archiveItem.setEnabled(false);
                dictItem.setEnabled(false);
            }
            if (isDlg) {
                DesignerDialog dlg = new DesignerDialog(this, title, p);
                dlg.setLanguage(selectedIfcLang.id);
                int resDlg;
                do {
                    dlg.show();
                    resDlg = dlg.getResult();
                    if (resDlg == ButtonsFactory.BUTTON_OK) {
                        doAfterOpen(frm);
                        OrRef ref = frm.getRef();
                        ReqMsgsList msg = ref.canCommit();
                        int errors = msg.getListSize();
                        if (errors > 0) {
                            SortedFrame sdlg = new SortedFrame(this, res.getString("errors"));
                            msg.setParent(sdlg);
                            sdlg.setOption(new String[] { res.getString("continue"), res.getString("exit") });
                            sdlg.setContent(msg);
                            sdlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(sdlg.getSize()));
                            dlg.show();
                            resDlg = dlg.getResult();
                        }
                        Activity act_ = TaskTable.instance(false).getSelectedActivity();
                        if (resDlg != BUTTON_NOACTION && resDlg == BUTTON_OK || errors == 0) {
                            TaskTable.instance(false).setAutoAct(false);
                            if (errors == 0)
                                resDlg = 1;
                            if (resDlg != 1 || (resDlg == 1 && errors == 0)) {
                                frm.getCash().commit(act_.flowId);
                                frm.getRef().commitChanges(null);
                                if (frm.getRef().getItems(frm.getRef().getLangId()) != null
                                        && frm.getRef().getItems(frm.getRef().getLangId()).size() != 0
                                        && frm.getRef().getSelectedItems().size() == 0) {
                                    frm.getRef().setSelectedItems(new int[] { 0 });
                                }
                                List<OrRef.Item> a_sel = frm.getRef().getSelectedItems();
                                if (a_sel.size() == 0) {
                                    String text = res.getString("checkObjectMessage");
                                    MessagesFactory.showMessageDialog(this, MessagesFactory.ERROR_MESSAGE, text,
                                            getselectedIfcLangItem());
                                    resDlg = 0;
                                    continue;
                                }
                                KrnObject[] selObjs = new KrnObject[a_sel.size()];
                                for (int i = 0; i < a_sel.size(); i++) {
                                    OrRef.Item item = a_sel.get(i);
                                    selObjs[i] = (KrnObject) item.getCurrent();
                                }
                                int result_ = -1;
                                String res_ = "";
                                if (act_.transitions.length > 1) {
                                    String[] trs = new String[act_.transitions.length];
                                    for (int i = 0; i < trs.length; ++i) {
                                        String trs_i = act_.transitions[i].substring(act_.transitions[i].indexOf(";"));
                                        trs[i] = act_.transitions[i].substring(0, act_.transitions[i].indexOf(";"));
                                    }
                                    result_ = MessagesFactory.showOptionDialog(this, MessagesFactory.OPTION_MESSAGE, trs,
                                            getselectedIfcLangItem());
                                    if (result_ == -1) {
                                        break;
                                    } else {
                                        for (int i = 0; i < trs.length; ++i) {
                                            if (result_ == i) {
                                                res_ = act_.transitions[i].substring(act_.transitions[i].lastIndexOf(";") + 1);
                                                break;
                                            }
                                        }
                                    }
                                }
                                CursorToolkit.startWaitCursor(this);
                                if (Kernel.instance().setSelectedObjects(act_.flowId,
                                        act_.nodesId[0][act_.nodesId[0].length - 1], selObjs)) {
                                    Kernel.instance().setPermitPerform(act_.flowId, true);
                                    String[] res_s = Kernel.instance().performActivitys(
                                            new Activity[] { TaskTable.instance(false).getSelectedActivity() }, res_);
                                    if (res_s.length > 0) {
                                        if (res_s.length == 1 && res_s[0].equals("synch")) {
                                            TaskTable.instance(false).setAutoIfcFlowId_(act_.flowId);
                                        } else {
                                            // обработка ошибок
                                            String msg_ = res_s[0];
                                            for (int i = 1; i < res_s.length; ++i)
                                                msg_ += "\n" + res_s[i];
                                            MessagesFactory.showMessageDialog(this, MessagesFactory.ERROR_MESSAGE, msg_);
                                            resDlg = 0;
                                            continue;
                                        }
                                    }
                                    TaskTable.instance(false).getAutoIfcSet().add(act_.flowId);
                                    act_.param |= Constants.IFC_NOT_ABL;
                                    act_.ui.id = -1;
                                    act_.infUi.id = -1;
                                    if ((act_.param & Constants.ACT_PERMIT) == Constants.ACT_PERMIT)
                                        act_.param ^= Constants.ACT_PERMIT;
                                    if (res_s.length == 1 && res_s[0].equals("synch")) {
                                        TaskTable.instance(false).reloadTasks(act_.flowId, true,
                                                act_.ui.id > 0 && act_.infUi.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : 0);
                                    }
                                }
                                if (TaskTable.instance(false).isGrafVisible())
                                    TaskTable.instance(false).setGraf(0);
                                
                                repaint();
                            } else {
                                resDlg = 0;
                            }
                        }
                    }
                } while (resDlg == 0);

                InterfaceManagerFactory.instance().getManager().releaseInterface(false);
            } else {
                mainSpliter.setLeftComponent(null);
                prevBtn.setEnabled(true);
                runBtn.setEnabled(mode == SERVICE_MODE);
                rollbackBtn.setEnabled(true);
                applyBtn.setEnabled(true);
                mainSpliter.setRightComponent(p);
            }
            setEnabledGraf(false);
            if (p != null) {
                OrButton defaultButton = p.getDefaultButton();
                if (defaultButton != null)
                    getRootPane().setDefaultButton((JButton) defaultButton);
                p.requestFocus();
            }
        } else if (frameManager.getIndex() == 0) {
            if (!Constants.SE_UI && !ADVANCED_UI) {
                tasksItem.setEnabled(true);
                archiveItem.setEnabled(true);
                dictItem.setEnabled(true);
            }
            mainSpliter.setLeftComponent(currHiperTree);
            setTitle(appTitle);
            if (p != null)
                mainSpliter.remove(p);
            setEnabledGraf(true);
            validate();
            currHiperTree.setMonitorFocus();

            prevBtn.setEnabled(false);
            runBtn.setEnabled(false);
            rollbackBtn.setEnabled(ADVANCED_UI && mainUI != null);
            applyBtn.setEnabled(false);
        }
        if (!isDlg) {
            validate();
            repaint();
            doAfterOpen(frm);
        }

        if (frm.getAllwaysFocusedComponent() instanceof Component) {
            ((Component) frm.getAllwaysFocusedComponent()).requestFocusInWindow();
        }

    }

    public CommitResult commitCurrent() throws KrnException {
        return commitCurrent(new String[] { res.getString("continue"), res.getString("save") }, null, true, false);
    }

    public CommitResult commitCurrent(String[] options) throws KrnException {
        return commitCurrent(options, null, true, true);
    }

    private CommitResult commitCurrent(String[] options, boolean[] refresh) throws KrnException {
        return commitCurrent(options, refresh, true, true);
    }

    public CommitResult commitCurrent(String[] options, boolean[] refresh, boolean check, boolean canIgnore) throws KrnException {
        CommitResult result = CommitResult.WITHOUT_ERRORS;
        UIFrame frm = frameManager.getCurrentFrame();
        if (frm != null && frm.getRef() != null && frm.getRef().getType() != null &&
                (frm.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            try {
            	int r = BUTTON_CANCEL;
                ReqMsgsList msg = frm.getRef().canCommit();
                if (check && msg.getListSize() > 0) {
                    final SortedFrame dlg = new SortedFrame(this, res.getString("errors"));
                    msg.setParent(dlg);
                	dlg.setOption(options);
                    if (msg.hasFatalErrors() && !canIgnore)
                    	dlg.setOnlyOkButton();
                    
                    dlg.setContent(msg);
                    dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
                    dlg.show();
                    r= dlg.getResult();
                }
                if (check && r != BUTTON_NOACTION && r == BUTTON_OK) {
                	result = CommitResult.CONTINUE_EDIT;
                } else {
                	frm.getCash().commit(frm.getFlowId());
                    frm.getRef().commitChanges(this);
                    boolean b = doAfterCommit(frm);
                    if (refresh != null)
                    	refresh[0] = b;
                    if (frm.getFlowId() > 0) {
                        if (msg.hasFatalErrors()) {
                            Kernel.instance().setPermitPerform(frm.getFlowId(), false);
                            TaskTable.instance(false).setPermitPerform(false);
                            result = CommitResult.WITH_FATAL_ERRORS;
                        } else {
                            Kernel.instance().setPermitPerform(frm.getFlowId(), true);
                            TaskTable.instance(false).setPermitPerform(true);
                            if (msg.getListSize() > 0)
                            	result = CommitResult.WITH_ERRORS;
                        }
                        List<OrRef.Item> a_sel = frm.getRef().getSelectedItems();
                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
                        for (int i = 0; i < a_sel.size(); i++) {
                            OrRef.Item item = a_sel.get(i);
                            selObjs[i] = (KrnObject) item.getCurrent();
                        }
                        Activity act_=TaskTable.instance(false).getSelectedActivity();
                        if(act_!=null){
                            Kernel.instance().setSelectedObjects(act_.flowId,act_.nodesId[0][act_.nodesId[0].length-1], selObjs);
                            TaskTable.instance(false).taskReload(act_.flowId,act_.infUi.id>0 && act_.ui.id>0?2:act_.infUi.id>0?1:act_.ui.id>0?0:-1);
                        }
                    }
                }
            } catch (KrnException ex) {
                Container container = (Frame)InterfaceManagerFactory.instance().getManager();
                MessagesFactory.showMessageDialogBig((Frame)container, MessagesFactory.ERROR_MESSAGE,
                            "Ошибка при сохранении интерфейса!\r\n" + ex.getMessage());

                ex.printStackTrace();
                MessagesFactory.showMessageDialog(this, MessagesFactory.ERROR_MESSAGE,Constants.ERROR_MESSAGE_1 + ex.getMessage());
                Kernel.instance().setPermitPerform(frm.getFlowId(), false);
                TaskTable.instance(false).setPermitPerform(false);
            }
        }
        return result;
    }

    public void rollbackCurrent() {
        UIFrame frame = frameManager.getCurrentFrame();
        if (frame != null) {
            try {
                if (frame.getRef() != null) {
                	frame.getCash().rollback(frame.getFlowId());
                }
                frame.getPanelAdapter().clearFilterParam();
                
                Map<String, KrnObject[]> objsMap = new HashMap<String, KrnObject[]>();
                KrnObject[] objs = frame.getInitialObjs();
                objs = doBeforeOpen(frame, objs, objsMap);

                evaluateAllRefs(frame, objs, objsMap);

                doAfterOpen(frame);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setEnabledGraf(boolean enabled_) {
        grafBtn.setEnabled(enabled_);
        setEnabledSuperBtn(grafBtn.isSelected() && enabled_);
        setEnabledSubBtn(grafBtn.isSelected() && enabled_);
    }

    public void setEnabledSuperBtn(boolean enabled_) {
        superBtn.setEnabled(enabled_ && user.isAdmin());
    }

    public void setEnabledSubBtn(boolean enabled_) {
        subBtn.setEnabled(enabled_ && user.isAdmin());
    }

    public OrRef getRef() {
        return frameManager.getCurrentFrame().getRef();
    }

    public Cache getCash() {
        if (frameManager != null) {
            UIFrame frame = frameManager.getCurrentFrame();
            if (frame != null) {
                return frame.getCash();
            }
        }
        return null;
    }

    class PrinterMenu extends JMenu implements OrRefListener {
        private ReportPrinter p_;
        private OrCalcRef visibleRef;

        public PrinterMenu(ReportPrinter p, boolean submenu) {
            super(p.toString());
            setFont(Utils.getDefaultFont());
            p_ = p;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }

            String visFunc = p.getVisibilityFunc();
            try {
	            if (visFunc != null && visFunc.trim().length() > 0) {
	            	String propertyName = "Свойство: Видимость";
	                visibleRef = new OrCalcRef(visFunc, false, Mode.RUNTIME, p.getFrame().getRefs(),
	                		p.getFrame().getTransactionIsolation(), p.getFrame(), p.getFrame().getPanel(), propertyName, p.getFrame().getPanel().getAdapter());
	                visibleRef.addOrRefListener(PrinterMenu.this);

	                ClientOrLang orlang = new ClientOrLang(p.getFrame());
                    try {
                    	ASTStart template = OrLang.createStaticTemplate(visFunc);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(template, vc, p.getFrame().getPanel().getAdapter(), new Stack<String>());
                        if (calcOwner) {
                            OrCalcRef.removeCalculations();
                        }
                        Object rez = vc.get("RETURN");
                        setVisible(((Number) rez).intValue() == 1);

                    } catch (Exception e) {
                        System.out.println(e);
                    }
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }

            List<LangItem> langItems = LangItem.getAll();
            for (int i = 0; i < langItems.size(); i++) {
                LangItem li = langItems.get(i);
                if (p.hasReport(li.obj)) {
                    PrinterLangItem pi = new PrinterLangItem(this, li);
                    pi.addActionListener(MainFrame.this);
                    add(pi);
                }
            }
        }

        @Override
		public void valueChanged(OrRefEvent e) {
        	OrRef ref = e.getRef();
            if (ref == null) 
                return;
            
	        if (ref == visibleRef) {
	    		if (visibleRef.getValue(0) != null) {
	                setVisible(((Number) visibleRef.getValue(0)).intValue() == 1);
	            }
	        }
		}

		@Override
		public void changesCommitted(OrRefEvent e) {
		}

		@Override
		public void changesRollbacked(OrRefEvent e) {
		}

		@Override
		public void pathChanged(OrRefEvent e) {
		}

		@Override
		public void checkReqGroups(OrRef ref, List<MsgListItem> errMsgs,
				List<MsgListItem> reqMsgs, Stack<Pair> locs) {
		}

		@Override
		public void clear() {
		}

		@Override
		public void stateChanged(OrRefEvent e) {
		}

		@Override
		public void setFocus(int index, OrRefEvent e) {
		}

		public void changeTitle() {
            setText(p_.toString());
        }

        public void changeSelection() {
            //To change body of created methods use File | Settings | File Templates.
        }

        public ReportPrinter getPrinter() {
            return p_;
        }
    }

    class PrinterMenuItem extends JMenuItem implements OrRefListener {
        private ReportPrinter p_;
        private LangItem langItem;
        private OrCalcRef visibleRef;

        public PrinterMenuItem(ReportPrinter p, boolean submenu, LangItem li) {
            super(p.toString());
            setFont(Utils.getDefaultFont());
            p_ = p;
            langItem = li;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }

            String visFunc = p.getVisibilityFunc();
            try {
	            if (visFunc != null && visFunc.trim().length() > 0) {
	            	String propertyName = "Свойство: Видимость";
	                visibleRef = new OrCalcRef(visFunc, false, Mode.RUNTIME, p.getFrame().getRefs(),
	                		p.getFrame().getTransactionIsolation(), p.getFrame(), p.getFrame().getPanel(), propertyName, p.getFrame().getPanel().getAdapter());
	                visibleRef.addOrRefListener(PrinterMenuItem.this);

	                ClientOrLang orlang = new ClientOrLang(p.getFrame());
                    try {
                    	ASTStart template = OrLang.createStaticTemplate(visFunc);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(template, vc, p.getFrame().getPanel().getAdapter(), new Stack<String>());
                        if (calcOwner) {
                            OrCalcRef.removeCalculations();
                        }
                        Object rez = vc.get("RETURN");
                        setVisible(((Number) rez).intValue() == 1);

                    } catch (Exception e) {
                        System.out.println(e);
                    }
	            }
            } catch (Exception e) {
            	e.printStackTrace();
            }
            addActionListener(MainFrame.this);
        }

        @Override
		public void valueChanged(OrRefEvent e) {
        	OrRef ref = e.getRef();
            if (ref == null) 
                return;
            
	        if (ref == visibleRef) {
	    		if (visibleRef.getValue(0) != null) {
	                setVisible(((Number) visibleRef.getValue(0)).intValue() == 1);
	            }
	        }
		}

		@Override
		public void changesCommitted(OrRefEvent e) {
		}

		@Override
		public void changesRollbacked(OrRefEvent e) {
		}

		@Override
		public void pathChanged(OrRefEvent e) {
		}

		@Override
		public void checkReqGroups(OrRef ref, List<MsgListItem> errMsgs,
				List<MsgListItem> reqMsgs, Stack<Pair> locs) {
		}

		@Override
		public void clear() {
		}

		@Override
		public void stateChanged(OrRefEvent e) {
		}

		@Override
		public void setFocus(int index, OrRefEvent e) {
		}

		public void changeTitle() {
            setText(p_.toString());
        }

        public void changeSelection() {
        }

        public ReportPrinter getPrinter() {
            return p_;
        }

        public KrnObject getLanguage() {
            return langItem.obj;
        }
    }

    private class PrinterLangItem extends OrMenuItem {
        private LangItem langItem;
        PrinterMenu pm;

        public PrinterLangItem(PrinterMenu pm, LangItem item) {
            super(item.name, item.icon);
            this.pm = pm;
            langItem = item;
            setFont(Utils.getDefaultFont());
        }

        public KrnObject getLanguage() {
            return langItem.obj;
        }

        public PrinterMenu getPrinterMenu() {
            return pm;
        }
    }

    private void changePrinterTitles(Object menu) {
        Component[] children = new Component[0];
        if (menu instanceof JPopupMenu) {
            children = ((JPopupMenu)menu).getComponents();
        } else if (menu instanceof JMenu) {
            children = ((JMenu)menu).getPopupMenu().getComponents();
        }

        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            if (child instanceof PrinterMenu) {
                PrinterMenu pi = (PrinterMenu)child;
                pi.changeTitle();
            } else if (child instanceof PrinterMenuItem) {
                PrinterMenuItem pmi = (PrinterMenuItem)child;
                pmi.changeTitle();
            } else if (child instanceof ReportMenu) {
                ReportMenu rm = (ReportMenu)child;
                rm.changeTitle();
                changePrinterTitles(rm);
            }
        }
    }

    private void changePrinterSelection(Object menu) {
        Component[] children = new Component[0];
        if (menu instanceof JPopupMenu) {
            children = ((JPopupMenu)menu).getComponents();
        } else if (menu instanceof JMenu) {
            children = ((JMenu)menu).getPopupMenu().getComponents();
        }

        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            if (child instanceof PrinterMenu) {
                PrinterMenu pi = (PrinterMenu)child;
                pi.changeSelection();
            } else if (child instanceof ReportMenu) {
                ReportMenu rm = (ReportMenu)child;
                changePrinterSelection(rm);
            }
        }
    }

    private class ConnectionChecker extends Thread {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                if (!Kernel.instance().isAlive()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Соединение с сервером утеряно.\nПопытайтесь перегрузить приложение.", "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        }
    }

    public boolean checkcurishelp() {
        return _iscurhelp;
    }

    public int gethelplangId() {
        return _helplangId;
    }

    public void sethelplangId(int helplangId) {
        _helplangId = helplangId;
    }

    //Вспомогательные классы компонентов Vital

    class ClientMenu extends OrMenu {
        
        public ClientMenu(String s) {
            this(true,s);
        }

        public ClientMenu(boolean isOpaque, String s) {
            super(s == null ? "" : s);
            this.isOpaque = isOpaque;
            setFont(Utils.getDefaultFont());
            init();
        }

        public ClientMenu(String s, boolean submenu) {
            super(s == null ? "" : s);
            setFont(Utils.getDefaultFont());
            subMenu = submenu;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }
            init();
        }
        
        public ClientMenu(Action action, boolean submenu) {
            this(true,action,submenu);
        }
        public ClientMenu(boolean isOpaque, Action action, boolean submenu) {
            super(action);
            this.isOpaque = isOpaque;
            setFont(Utils.getDefaultFont());
            subMenu = submenu;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }
            init();
        }

        public void setText(String text) {
            int beg = text.indexOf('&');
            if (beg > -1) {
                char m = text.charAt(beg+1);
                text = text.substring(0, beg) + text.substring(beg+2);
                super.setText(text);
                setMnemonic(m);
                setDisplayedMnemonicIndex(beg);
            } else {
                super.setText(text);
            }
            init();
        }
        
        
        private void init() {
            if (Constants.SE_UI) {
                setForeground(Utils.getDarkShadowSysColor());
                setOpaque(isOpaque || subMenu);
                if (isOpaque) {
                    setGradient(GRADIENT_MENU_PANEL);
                }
            }
        }
    }

    class ReportMenu extends JMenu {
        private ReportRecord record;

        public ReportMenu(String s, ReportRecord r) {
            super(s);
            setFont(Utils.getDefaultFont());
            record = r;
            //setForeground(Utils.getDarkShadowSysColor());
        }
        public ReportMenu(String s, ReportRecord r, boolean submenu) {
            super(s);
            setFont(Utils.getDefaultFont());
            record = r;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }
        }

        public void changeTitle() {
            UIFrame frame = frameManager.getCurrentFrame();
            setText(record.getName(frame));
        }
    }


    class ClientCheckBoxMenuItem extends OrCheckBoxMenuItem {
        public ClientCheckBoxMenuItem(String s) {
            super(s);
            setFont(Utils.getDefaultFont());
            //setForeground(Utils.getDarkShadowSysColor());
            if(Constants.SE_UI) {
                setOpaque(true);
                setGradient(GRADIENT_MENU_PANEL);
            }
        }

        public void setText(String text) {
            int beg = text.indexOf('&');
            if (beg > -1) {
                char m = text.charAt(beg+1);
                text = text.substring(0, beg) + text.substring(beg+2);
                super.setText(text);
                setMnemonic(m);
                setDisplayedMnemonicIndex(beg);
            } else {
                super.setText(text);
            }
        }
    }

    class ClientMenuItem extends OrMenuItem {

        public ClientMenuItem(String s) {
            super(s);
            setFont(Utils.getDefaultFont());
            if(Constants.SE_UI) {
                setOpaque(true);
                setGradient(GRADIENT_MENU_PANEL);
            }
        }

        public ClientMenuItem(String s, String iconName) {
            super(s);
            setIcon(getImageIcon(iconName));
            setFont(Utils.getDefaultFont());
            if (Constants.SE_UI) {
                setOpaque(true);
                setGradient(GRADIENT_MENU_PANEL);
            }
        }
       
        public ClientMenuItem(Action action) {
            super(action);
            setFont(Utils.getDefaultFont());
            addProcessBtn((ProcessItem)action);
            if(Constants.SE_UI) {
                setOpaque(true);
                setGradient(GRADIENT_MENU_PANEL);
            }
        }

        public void setText(String text) {
            int beg = text.indexOf('&');
            if (beg > -1) {
                char m = text.charAt(beg+1);
                text = text.substring(0, beg) + text.substring(beg+2);
                super.setText(text);
                setMnemonic(m);
                setDisplayedMnemonicIndex(beg);
            } else {
                super.setText(text);
            }
        }
    }

    class MenuLangItem extends OrCheckBoxMenuItem {

        private LangItem langItem;

        public MenuLangItem(LangItem item, boolean selected) {
            super(item.name, item.icon, selected);
            langItem = item;
            setFont(Utils.getDefaultFont());
            setGradient(GRADIENT_MENU_PANEL);
        }

        public LangItem getLangItem() {
            return langItem;
        }

        public void setText(String text) {
            int beg = text.indexOf('&');
            if (beg > -1) {
                char m = text.charAt(beg+1);
                text = text.substring(0, beg) + text.substring(beg+2);
                super.setText(text);
                setMnemonic(m);
                setDisplayedMnemonicIndex(beg);
            } else {
                super.setText(text);
            }
        }
    }

    public HelpSet getHelpSet() {
        HelpSet hs = null;
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            hs = new HelpSet(cl, MainFrame.class.getResource("com/cifs/or2/client/help/main/main.hs"));
        } catch (Exception ee) {
            System.out.println("HelpSet: " + ee.getMessage());
        }
        return hs;
    }

    public void CreateHelpMenu() {
        URL url = MainFrame.class.getResource("HelpMenu/config.xml");
        HelpSet law = getHelpSet();
        if (url != null) {
            SAXBuilder builder = new SAXBuilder();
            List xml = null;
            try {
                xml = builder.build(url).getRootElement().getChildren();
                for (int i = 0; i < xml.size(); i++) {
                    Element item = (Element) xml.get(i);
                    Element type = item.getChild("type");
                    if (type.getText().equals("menu")) {
                        Element val = item.getChild("title");
                        String title = val.getText();
                        JMenuItem menuItem = new ClientMenuItem(title);
                        helpMenu.add(menuItem);
                        if (law != null) {
                            HelpBroker hb = law.createHelpBroker();
                            CSH.setHelpIDString(menuItem, "main");
                            menuItem.addActionListener(new CSH.DisplayHelpFromSource(hb));
                        }
                    } else if (type.getText().equals("divider")) {
                        helpMenu.addSeparator();
                    }
                }
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //достаем пользоватея и смотрим его помощь если есть то вставляем помощь в меню
        Kernel krn = Kernel.instance();
        User user = krn.getUser();
        List<KrnObject> helps = user.getHelp();
        long langId = Kernel.instance().getInterfaceLanguage().id;

        List<ClientMenuItem> helpItemsList = new ArrayList<ClientMenuItem>();
        if (helps != null && helps.size() > 0) {
            helpMenu.addSeparator();
            helpMenuItems = new ClientMenuItem[helps.size()];
            int i = 0;
            for (KrnObject help : helps) {
                NoteBrowser browser = new NoteBrowser(help,false,langId);
                ClientMenuItem menuItem = new ClientMenuItem(browser.getTitle());
                helpItemsList.add(menuItem);
                menuItem.setIcon(getImageIcon("helpIcon"));
                helpMenu.add(menuItem);
                menuItem.addActionListener(new HelpItemActionAdapter(browser, menuItem));
            }
        }
        
        List<HelpFile> helpFiles = user.getHelpFiles();
        if (helpFiles != null && helpFiles.size() > 0) {
            helpMenu.addSeparator();
            for (HelpFile hf : helpFiles) {
                ClientMenuItem menuItem = new ClientMenuItem(hf.getTitle(langId));
                helpItemsList.add(menuItem);
                menuItem.setIcon(Utils.getImageIcon("DocField"));
                helpMenu.add(menuItem);
                menuItem.addActionListener(new HelpFileItemActionAdapter(hf, menuItem));
            }
        }

        helpMenuItems = new ClientMenuItem[helpItemsList.size()];
        for (int i=0; i<helpItemsList.size(); i++)
        	helpMenuItems[i] = helpItemsList.get(i);
    }

    private void evaluateAllRefs(UIFrame frm, KrnObject[] objs, Map<String, KrnObject[]> objsMap) throws KrnException {
        Map<String, OrRef> contents = frm.getContentRef();
        int count = 0;
        int scount = 0;
        int size = 1;
        OrRef ref = frm.getRef();
        Map<String, OrRef> refs = frm.getRefs();
        if (ref != null) {
            count += ref.getListenersCount(progress);
        	scount += ref.getSimpleListenersCount(progress);
        }
        progress.setMinimum(0);
        progress.setMaximum(count);
        progress.setValue(0);
        progressLabel.setText("Загрузка данных:");
        
        //if (ref != null)
        //	size = ref.preEvaluate(objs, null);
        ////////////////////////////////////
        progress.setMaximum(size * count + scount);
        ////////////////////////////////////
        contents = frm.getContentRef();
        for (Iterator<OrRef> langIt = contents.values().iterator(); langIt.hasNext();) {
            ref = langIt.next();
            if (ref.getParent() == null && !ref.isHyperPopup())
                try {
                    ref.evaluate((KrnObject[]) null, this);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
        }
        ref = frm.getRef();
        refs = frm.getRefs();
        
        for (Iterator<OrRef> langIt = refs.values().iterator(); langIt.hasNext();) {
            OrRef chRef = langIt.next();
            if (chRef.getParent() == null && (ref == null || !chRef.toString().equals(ref.toString())))
                try {
                    chRef.evaluate(objsMap.get(chRef.toString()), this);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
        }
        if (ref != null)
            ref.evaluate(objs, null);
        if (ref != null)
            ref.stateChanged(new OrRefEvent(ref, -1, -1, null));
        
        progress.setValue(0);
        progressLabel.setText("");
    }


    private void showAbout() {
        ImageIcon icon = null;
        String path = System.getProperty("about");
        if (path != null && path.length() > 0) {
            icon = getImageIconJpg(path);
        }
        if (icon == null) {
            icon = getImageIconJpg("LoginBox");
        }
        final JDialog wnd = new AboutDialog(this, icon);
        wnd.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(wnd.getSize()));
        wnd.setVisible(true);
    }

    public boolean getBeepAllow(){
        return beepAllow.isSelected();
    }

    public boolean getScrollTabProc(){
        return scrollTabProc.isSelected();
    }

    private void changeTitles(ResourceBundle res) {
        //Menu
        fileMenu.setText(res.getString("file"));
        openItem.setText(res.getString("interface"));
        exitItem.setText(res.getString("exitMenu"));
        //accessItem.setText(res.getString("openMenu"));
        
        searchSrv.setText(res.getString("searchSrv"));

        paramMenu.setText(res.getString("settings"));
        langMenu.setText(res.getString("interfaceLang"));
        editorItem.setText(res.getString("editorSetup"));
        langItem.setText(res.getString("language"));
        beepAllow.setText(res.getString("beep"));
        scrollTabProc.setText(res.getString("scrollTabProc"));

        if (!Constants.SE_UI && !ADVANCED_UI) {
	        windowMenu.setText(res.getString("window"));
	        tasksItem.setText(res.getString("taskMenu"));
	        archiveItem.setText(res.getString("archiveMenu"));
	        dictItem.setText(res.getString("catalogMenu"));
        }
        
        helpMenu.setText(res.getString("help"));
        helpItem.setText(res.getString("helpTip"));
        help2Item.setText(res.getString("helpMain"));
        aboutItem.setText(res.getString("about"));
        manualItem.setText(res.getString("manual"));

        reportMenu_.setText(res.getString("reports"));

        //Buttons
        applyBtn.setToolTipText(res.getString("applyChanges"));
        applyBtn.setDesc(res.getString("applyChangesDesc"));
        rollbackBtn.setToolTipText(res.getString("cancelChanges"));
        rollbackBtn.setDesc(res.getString("cancelChangesDesc"));
        prevBtn.setToolTipText(res.getString("backPage"));
        runBtn.setToolTipText(res.getString("buttonRun"));
        printBtn.setToolTipText(res.getString("print"));
        grafBtn.setToolTipText(res.getString("processMap"));
        superBtn.setToolTipText(res.getString("superProcess"));
        subBtn.setToolTipText(res.getString("subProcess"));
        debugBtn.setToolTipText(res.getString("debugProcess"));
        pdBtn.setToolTipText(res.getString("pwdChange"));
        infoExchangeBtn.setToolTipText(res.getString("infoStatusExchange"));
        exchangeBtn.setToolTipText(res.getString("exchange"));

        infoExchangeBtn.setDesc(res.getString("infoExchangeDesc"));
        exchangeBtn.setDesc(res.getString("exchangeDesc"));
        prevBtn.setDesc(res.getString("backPageDesc"));
        printBtn.setDesc(res.getString("printDesc"));
        grafBtn.setDesc(res.getString("processMapDesc"));
        superBtn.setDesc(res.getString("superProcessDesc"));
        subBtn.setDesc(res.getString("subProcessDesc"));
        debugBtn.setDesc(res.getString("debugProcessDesc"));
        pdBtn.setDesc(res.getString("pwdChangeDesc"));
        dataLangSelector.setDesc(res.getString("dataLangDesc"));
        accessStatus.setDesc(res.getString("baseLabelDesc"));
        userLabel.setDesc(res.getString("userLabelDesc"));
        //pwdChange.setRes(res);
    }

    private void changeHelpTitles() {
        if (helpMenuItems != null) {
            for (ClientMenuItem item : helpMenuItems) {
                HelpItemActionAdapter adapter = null;
                ActionListener[] listeners = item.getActionListeners();
                for (ActionListener l : listeners) {
                    if (l instanceof HelpItemActionAdapter) {
                        adapter = (HelpItemActionAdapter)l;
                    }
                }
                if (adapter != null) {
                    adapter.setLangId(selectedIfcLang.id);
                    item.setText(adapter.browser.getTitle());
                }
            }
        }
    }

    public void setScrollTab() {
        Properties props = new Properties();
        String res="false";
        try {
            String workDir = Utils.getUserWorkingDir();
            if (Funcs.isValid(workDir)) {
    	        File dir = Funcs.getCanonicalFile(workDir);
                dir.mkdirs();

                File f = new File(dir, "propsJboss");

                FileInputStream fis = new FileInputStream(f);
                props.load(fis);
                fis.close();
                String res_=props.getProperty("scrollTab");
                if (res_!=null)
                	res=res_;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        if(res.equals("true")){
            currHiperTree.setScrollTabProc(true);
            scrollTabProc.setSelected(true);
        }
    }

    public void setDefaultIfcLanguage(String code) {
        Properties props = new Properties();
        LangItem li=null;
        try {
            String workDir = Utils.getUserWorkingDir();
            if (Funcs.isValid(workDir)) {
    	        File dir = Funcs.getCanonicalFile(workDir);
	            dir.mkdirs();
	
	            File f = new File(dir, "propsJboss");
	            FileInputStream fis = new FileInputStream(f);
	            props.load(fis);
	            fis.close();
	            String code_=props.getProperty("currlang");
	            if(code_!=null){
	                li = LangItem.getByCode(code_);
	
	            }
	            if(li==null){
	                li = LangItem.getByCode(code != null ? code : "RU");
	                FileOutputStream fos = new FileOutputStream(f);
	                props.setProperty("currlang", li.code);
	                props.store(fos, "Properties");
	                fos.close();
	            }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (li != null) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            changeTitles(res);
            UIFrame frm = frameManager.getCurrentFrame();
            String title = "";
            if(selectedIfcLang.id != li.obj.id){
                selectedIfcLang = li.obj;
                try {
                    Kernel.instance().setLang(li.obj);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                selectedIfcLangItem = li;
                if(frm!=null){
                    frm.setInterfaceLang(selectedIfcLang, res);
                    title = frm.getPanel().getTitle();
                }
                currHiperTree.setLang(li.obj.id);
                dataLandLabel.setText(res.getString("datalang"));
                changePrinterTitles(docMenu);
            }
            if (!"Безымянный".equals(title)&& title!=null && !title.equals("")) {
                setTitle(appTitle + " - [" + Funcs.sanitizeUsername(title) + "]");
            } else {
                setTitle(appTitle);
            }
            dataLandLabel.setText(res.getString("datalang"));
            for(int i = 0; i < langMenu.getItemCount(); i++) {
                MenuLangItem item = (MenuLangItem)langMenu.getItem(i);
                if (selectedIfcLang.id == item.getLangItem().obj.id) {
                    item.setSelected(true);
                    break;
                }
            }
            currHiperTree.setLang(li.obj.id);
        }
        TaskTable.instance(false).initTaskTable(this);
    }

    public void setDefaultDataLanguage(String code) {
        LangItem li = LangItem.getByCode(code != null ? code : "RU");
        if (li != null) {
            dataLangSelector.setSelectedLanguage(li.obj);
            UIFrame frm = frameManager.getCurrentFrame();
            if (frm != null) frm.setDataLang(li.obj, true);
            changePrinterSelection(docMenu);
            Properties props = new Properties();
            try {
                String workDir = Utils.getUserWorkingDir();
                if (Funcs.isValid(workDir)) {
        	        File dir = Funcs.getCanonicalFile(workDir);
	                dir.mkdirs();
	
	                File f = new File(dir, "propsJboss");
	
	                FileInputStream fis = new FileInputStream(f);
	                props.load(fis);
	                fis.close();
	                FileOutputStream fos = new FileOutputStream(f);
	                props.setProperty("currdatalang", li.code);
	                props.store(fos, "Properties");
	                fos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public LangItem getselectedIfcLangItem(){
        return selectedIfcLangItem;
    }

    class HelpItemActionAdapter implements ActionListener {
        private NoteBrowser browser;
        private JMenuItem menuItem;

        public HelpItemActionAdapter(NoteBrowser browser, JMenuItem menuItem) {
            this.browser = browser;
            this.menuItem = menuItem;
        }

        public void actionPerformed(ActionEvent e) {
            browser.init();
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(browser.getNavigator(), BorderLayout.NORTH);
            panel.add(browser, BorderLayout.CENTER);
            browser.setDividerLocation(200);

            JFrame dlg = new JFrame(browser.getTitle());
            dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dlg.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    menuItem.setEnabled(true);
                    super.windowClosing(e);
                }
            });
            dlg.setIconImage(getImageIcon("icon").getImage());
            dlg.setExtendedState(dlg.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            dlg.getContentPane().add(panel);
            dlg.setSize(Utils.getScreenSize(dlg));
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            menuItem.setEnabled(false);
            dlg.setVisible(true);
        }

        public void setLangId(long langId) {
            browser = new NoteBrowser(browser.getKrnObject(),false,langId);
        }
    }
    
    class HelpFileItemActionAdapter implements ActionListener {
        private HelpFile helpFile;
        private JMenuItem menuItem;

        public HelpFileItemActionAdapter(HelpFile helpFile, JMenuItem menuItem) {
            this.helpFile = helpFile;
            this.menuItem = menuItem;
        }

        public void actionPerformed(ActionEvent e) {
            try {
            	String fileName = helpFile.getFileName();
            	byte[] content = helpFile.getContent();
                if (fileName != null && content != null) {
                	File tmpDir = new File(System.getProperty("java.io.tmpdir"));
                	File tmpFile = Funcs.getFreeFile(tmpDir, fileName);
                	tmpFile.deleteOnExit();
                	
                	Funcs.write(content, tmpFile);

                	String str = tmpFile.getAbsolutePath();
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

            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        public String getTitle(long langId) {
            return helpFile.getTitle(langId);
        }
    }

    public static class DescLabel extends JLabel implements Descriptionable {
        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
    private boolean isTransportReady(int transportId){
        byte[] data=null;
        String ready="";
        Kernel krn=Kernel.instance();
        try{
                data=krn.getTransportParam(MQ_TRANSPORT);
        } catch(KrnException e){
            e.printStackTrace();
        }
        if(data!=null && data.length>0){
          try{
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                Element root = doc.getRootElement();
                Element param=root.getChild("ready");
                ready=param.getText();
          } catch(IOException e){
              e.printStackTrace();
          } catch(JDOMException e){
              e.printStackTrace();
          }
        }
        return ready.equals("true");

    }
    
    class IfcLoader extends SwingWorker {

		private KrnObject[] objs;
		private KrnObject ui;
		private boolean isHiperTree;
		private int mode;
		private long trId;
		private long flowId;
		private boolean isBlockErrors;
		private String refPath;
		private String uiType;

        public IfcLoader(KrnObject uiObj, KrnObject[] objs, String refPath, int mode, boolean isHiperTree, long tid, long flowId, boolean isBlockErrors, String uiType) {
            super();
            this.objs = objs;
            ui = uiObj;
            this.isHiperTree = isHiperTree;
            this.refPath = refPath;
            this.mode = mode;
            trId = tid;
            this.flowId = flowId;
            this.isBlockErrors = isBlockErrors;
            this.uiType = uiType;
        }

        public Object construct() {
            ((MainFrame) InterfaceManagerFactory.instance().getManager()).setEnabledGraf(false);
            try {
                absolute(ui, objs,refPath, mode, isHiperTree, trId, flowId, isBlockErrors, progress, uiType);
            } catch (KrnException e) {
                e.printStackTrace();
                clientLog.info("Ошибка при открытии интерфейса.id="+ui.id);
            }
            return null;
        }
    }

	public JProgressBar getProgress() {
		return progress;
	}

	public JLabel getProgressLabel() {
		return progressLabel;
	}
	
	private KrnObject[] doBeforeOpen(UIFrame frm, KrnObject[] objs, Map<String, KrnObject[]> objsMap) {
        OrPanel p = frm.getPanel();
        ASTStart template = p.getBeforeOpenTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm, true);
            Map<String, Object> vc = new HashMap<String, Object>();
            if (objs != null)
            	vc.put("OBJS", Funcs.makeList(objs));
            try {
            	boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                // До открытия интерфейса все отложенные вычисления игнорируются
    			if (calcOwner)
    				OrCalcRef.removeCalculations();
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие перед открытием");
            }
            List<KrnObject> objList = (List<KrnObject>)vc.get("OBJS");
            if (objList != null) {// && objList.size()>0) {
                objs = objList.toArray(new KrnObject[objList.size()]);
            }
            Map<String, KrnObject[]> cobjsMap = orlang.getObjsMap();
            if (cobjsMap != null)
            	objsMap.putAll(cobjsMap);
        }
        return objs;
	}
	
	private void doAfterOpen(UIFrame frm) {
        OrPanel p = frm.getPanel();
        if (p != null) {
	        ASTStart template = p.getAfterOpenTemplate();
	        if (template != null) {
	            ClientOrLang orlang = new ClientOrLang(frm);
	            Map<String, Object> vc = new HashMap<String, Object>();
	            try {
	            	boolean calcOwner = OrCalcRef.setCalculations();
	                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
	    			if (calcOwner)
	    				OrCalcRef.makeCalculations();
	            } catch (Exception ex) {
	                Util.showErrorMessage(p, ex.getMessage(), "Действие после открытия");
	            }
	        }
        }
	}

	public void keyPressed(KeyEvent arg0) {
		
	}

	public void keyReleased(KeyEvent arg0) {
		
	}

	public void keyTyped(KeyEvent arg0) {
	}
	
    private JMenuItem loadProcessMenu() throws KrnException {
        int index;
        String title;
        boolean isTab;
        boolean isBtnToolBar;
        String hotKey;
        byte[] icon;
        Kernel krn = Kernel.instance();
        // Считать все папки процессов со всеми необходимыми атрибутами
        KrnClass prFolderCls = krn.getClassByName("ProcessDefFolder");
        long langId = krn.getInterfaceLanguage().id;
        AttrRequest ar = new AttrRequestBuilder(prFolderCls, krn).add("parent").add("runtimeIndex").add("isTab")
                .add("title", langId).add("tabName", langId).add("isBtnToolBar").add("hotKey", langId).add("icon", langId)
                .build();
        List<Object[]> recs = krn.getClassObjects(prFolderCls, ar, new long[0], new int[] { 0 }, 0);

        KrnClass prRootCls = krn.getClassByName("ProcessDefRoot");
        MMap<Long, ProcessItem, ? extends Set<ProcessItem>> map = new MMap<Long, ProcessItem, TreeSet<ProcessItem>>((Class<TreeSet<ProcessItem>>) new TreeSet<ProcessItem>().getClass());
        ProcessItem prRoot = null;
        for (Object[] rec : recs) {
            KrnObject parent = (KrnObject) rec[2];
            index = (rec[3] != null) ? ((Number) rec[3]).intValue() : 0;
            title = (rec[6] != null) ? (String) rec[6] : (String) rec[5];
            isTab = (rec[4] != null) ? (Boolean) rec[4] : false;
            try {
                isBtnToolBar = (rec[7] != null) ? (Boolean) rec[7] : false;
                hotKey = (rec[8] != null) ? (String) rec[8] : "";
                icon = (rec[9] != null) ? (byte[]) rec[9] : null;
            } catch (Exception e) {
                hotKey = "";
                isBtnToolBar = false;
                icon = null;
            }
            
            ProcessItem prItem = new ProcessItem((KrnObject) rec[0], title, index, true, isTab, isBtnToolBar, hotKey, icon);
            if (prItem.processObj.classId == prRootCls.id) {
                prRoot = prItem;
            } else if (parent != null) {
                map.put(((KrnObject) parent).id, prItem);
            }
        }

        // Считать данные процессов пользователя со всеми необходимыми атрибутами
    	long[] prIds = null;
    	
    	List<Long> procs = krn.getUserSubjects(SystemAction.ACTION_START_PROCESS, krn.getUser().getObject().id);
    	if (procs != null)
    		prIds = Funcs.makeLongArray(procs);
    	else {
    		prIds = krn.getProcessDefinitions();
    	}

        KrnClass prCls = krn.getClassByName("ProcessDef");
        ar = new AttrRequestBuilder(prCls, krn).add("parent").add("runtimeIndex").add("title", langId).add("isBtnToolBar").add("hotKey", langId).add("icon", langId).build();
        recs = krn.getObjects(prIds, ar, 0);
        for (Object[] rec : recs) {
            KrnObject parent = (KrnObject) rec[2];
            index = rec[3] != null ? ((Number) rec[3]).intValue() : 0;
            try {
              isBtnToolBar = (rec[5] != null) ? ((Long) rec[5])==1 : false;
            hotKey = (rec[6] != null) ? (String) rec[6] : "";
            icon = (rec[7] != null) ? (byte[]) rec[7] : null;  
            } catch (Exception e) {
                hotKey = "";
                isBtnToolBar = false;
                icon = null;
            }
            
            ProcessItem prItem = new ProcessItem((KrnObject) rec[0], (String) rec[4], index, false, true, isBtnToolBar, hotKey, icon);
            if (parent != null) {
                map.put(((KrnObject) parent).id, prItem);
            }
        }
        // создание меню процессов
        // для возврата на старый алгоритм необходимо после названия метода добавить _
        return createProcessMenu(prRoot, map);
    }
	

    /**
     * Первичный метод создания меню Реализация без рекурсии
     * 
     * @param root
     *            корень меню (обычно это пункт "Процессы")
     * @param map
     *            карта объектов
     * @return сформированное меню процессов
     */
    private JMenuItem createProcessMenu(ProcessItem root, MMap<Long, ProcessItem, ? extends Set<ProcessItem>> map) {
        if (!root.isFolder) {
            return new ClientMenuItem(root);
        } else {
            Collection<ProcessItem> children = map.get(root.processObj.id);
            if (children == null) {
                return null;
            }
            // задать корень меню
            ClientMenu menu = new ClientMenu(false, root, true);
            // перебор всех потомков корневого элемента
            for (ProcessItem child : children) {
                menu = createProcessMenuIsTab(child, map, menu, createProcessMenu_(child, map));
            }
            return menu;
        }
    }    
    
    /**
     * оригинальный метод
     * Содание элементов меню
     * Рекурсивный метод
     * @param root предок меню
     * @param map карта объектов
     * @return элемент меню
     */
    private JMenuItem createProcessMenu_(ProcessItem root, MMap<Long, ProcessItem, ? extends Set<ProcessItem>> map) {
        if (!root.isFolder) {
            return new ClientMenuItem(root);
        } else {
            Collection<ProcessItem> children = map.get(root.processObj.id);
            if (children != null) {
                ClientMenu menu = null;
                for (ProcessItem child : children) {
                    JMenuItem mi = createProcessMenu_(child, map);
                    if (mi != null) {
                        if (menu == null) {
                            menu = new ClientMenu(root, true);
                        }
                        menu.add(mi);
                    }
                }
                return menu;
            }
        }
        return null;
    }
	


    /**
     * Дополнительный рекурсивный метод создания меню Работает в связке с
     * первычным методом Отличается тем что в нём идёт проверка на тип "потомка"
     * пункт добавляется в меню только если он "Вкладка"
     * 
     * @param child
     *            объект из карты, претендуемый на добавление в меню
     * @param map
     *            карта объектов
     * @param menu
     *            уже собранное предыдущими рекурсивными вызовами меню
     * @param mi
     *            потомок, претендуемый на добавление в меню
     * @return меню объектов
     */
    private ClientMenu createProcessMenuIsTab(ProcessItem child, MMap<Long, ProcessItem, ? extends Set<ProcessItem>> map, ClientMenu menu,
            JMenuItem mi) {
        if (child.isTab) {
            if (mi != null) {
                menu.add(mi);
            }
        } else {
            Collection<ProcessItem> _children = map.get(child.processObj.id);
            if (_children != null) {
                for (ProcessItem _child : _children) {
                    menu = createProcessMenuIsTab(_child, map, menu, createProcessMenu_(_child, map));
                }
            }
        }
        return menu;
    }

	private class ProcessItem extends AbstractAction implements Comparable {
		
		private final KrnObject processObj;
		private final int index;
		private final boolean isFolder;
		private final boolean isTab;
		private final boolean isBtnToolBar;
		private final String hotKey;
		private final byte[] icon;

		public ProcessItem(KrnObject processObj, String title, int index, boolean isFolder,boolean isTab,boolean isBtnToolBar,String hotKey,byte[] icon) {
			super(title);
			this.processObj = processObj;
			this.index = index;
			this.isFolder = isFolder;
			this.isTab = isTab;
			this.isBtnToolBar = isBtnToolBar;
			this.hotKey = hotKey;
			this.icon = icon;
			setEnabled(true);
		}

        public void actionPerformed(ActionEvent e) {
            try {
                String text = res.getString("startProcMessage");
                int result = MessagesFactory.showMessageDialog(MainFrame.this, MessagesFactory.QUESTION_MESSAGE, text + ":'"
                        + this.getValue(Action.NAME) + "'?", TaskTable.instance(false).li);
                if (result == ButtonsFactory.BUTTON_YES) {
                    CursorToolkit.startWaitCursor(MainFrame.this);
                    String[] res_ = Kernel.instance().startProcess(processObj.id, null);
                    //Предупреждение позволяющее пользователю запустить процесс по своему усмотрению
                    if(res_.length > 1 && res_[1].indexOf("deferred")==0) {
                        result=MessagesFactory.showMessageDialog(MainFrame.this, MessagesFactory.QUESTION_MESSAGE, res_[0]);
                        if (result == ButtonsFactory.BUTTON_YES) {
                    		Map<String, Object> vars=new HashMap<String, Object>();
                    		vars.put("DEFERRED", "DEFERRED");
                            res_ = Kernel.instance().startProcess(processObj.id, vars);
                    	}else {
                            CursorToolkit.stopWaitCursor(MainFrame.this);
                    		return ;
                    	}
                    }
                    if (res_.length > 0 && !res_[0].equals("")) {
                        CursorToolkit.stopWaitCursor(MainFrame.this);
                        String msg = res_[0];
                        MessagesFactory.showMessageDialog(MainFrame.this, MessagesFactory.ERROR_MESSAGE, msg);
                    } else {
                        List<String> param = new ArrayList<String>();
                        // если монитор событий скрыт - отобразить интерфейс
                        if (!Application.instance().isMonitorTask()) {
                            param.add("autoIfc");
                        }
                        if (res_.length > 3) {
                            param.add(res_[3]);
                        }
                        TaskTable.instance(false).startProcess(res_[1], param);
                    }
                    if (Application.instance().isMonitorTask()) {
                        CursorToolkit.stopWaitCursor(MainFrame.this);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

		public int compareTo(Object o) {
			ProcessItem prItem = (ProcessItem)o;
			int res = index - ((ProcessItem)o).index;
			if (res == 0) {
				res = processObj.id > prItem.processObj.id ? 1 : processObj.id < prItem.processObj.id ? -1 : 0;
			}
			return res;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof ProcessItem)
				return processObj.id == ((ProcessItem)obj).processObj.id;
			return false;
		}
    }

    public PathWordChange showChangePasswordDialog() {
        PathWordChange pdChange = new PathWordChange(this, getselectedIfcLangItem(), res, Kernel.instance().getUser().object);
        pdChange.setVisible(true);
        return pdChange;
    }
    
    private boolean doAfterCommit(UIFrame frm) {
        OrPanel p = (OrPanel)frm.getPanel();
        ASTStart template = p.getAfterSaveTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
            try {
            	boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                Object res = vc.get("RETURN");
                if (calcOwner)
                	OrCalcRef.makeCalculations();
                if (res instanceof Boolean)
                	return ((Boolean)res).booleanValue();
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после сохранения");
            }
        }
        return false;
    }
    
    public void doAfterTaskListUpdate() {
        for (UIFrame frm : frameManager.getFrames()) {
            if (frm.getObj() != null && frm.getPanel() != null) {
                OrPanel p = (OrPanel) frm.getPanel();
                ASTStart template = p.getAfterTaskListUpdateTemplate();
                if (template != null) {
                    ClientOrLang orlang = new ClientOrLang(frm);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    try {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } catch (Exception ex) {
                        Util.showErrorMessage(p, ex.getMessage(), "Действие после обновления списка задач");
                    }
                }
            }
        }
    }
    
    public void doOnNotification(SystemNote note) { 
        for (UIFrame frm : frameManager.getFrames()) {
            if (frm.getObj() != null) {
                OrPanel p = (OrPanel) frm.getPanel();
                ASTStart template = p.getOnNotificationTemplate();
                if (template != null) {
                    ClientOrLang orlang = new ClientOrLang(frm);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    vc.put("NOTIFICATION_DATA", note.data);
                    vc.put("NOTIFICATION_TYPE", note.type);
                    vc.put("NOTIFICATION_TIME", new KrnDate(note.time.getTime()));
                    if (note.from != null)
                    	vc.put("NOTIFICATION_FROM", note.from.userObj);
                    try {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } catch (Exception ex) {
                        Util.showErrorMessage(p, ex.getMessage(), "Действие при получении уведомления");
                    }
                }
                getTemplateNotification(frm, p.getComponents(), note);
            }
        }
     // Уведомить пользователя
        showAlarm(note.title);
    }

    
    public void getTemplateNotification(OrFrame frm, Component[] comps, SystemNote note) {
        if (comps != null && comps.length > 0) {
            for (Component comp : comps) {
                if (comp instanceof OrPanel) {
                    OrPanel p = (OrPanel) comp;
                    ASTStart template = p.getOnNotificationTemplate();
                    if (template != null) {
                        ClientOrLang orlang = new ClientOrLang(frm);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("NOTIFICATION_DATA", note.data);
                        vc.put("NOTIFICATION_TYPE", note.type);
                        vc.put("NOTIFICATION_TIME", new KrnDate(note.time.getTime()));
                        if (note.from != null)
                            vc.put("NOTIFICATION_FROM", note.from.userObj);
                        try {
                            boolean calcOwner = OrCalcRef.setCalculations();
                            orlang.evaluate(template, vc, p.getAdapter(), new Stack<String>());
                            Object res = vc.get("RETURN");
                            if (calcOwner)
                                OrCalcRef.makeCalculations();
                        } catch (Exception ex) {
                            Util.showErrorMessage(p, ex.getMessage(), "Действие при получении уведомления");
                        }
                    }
                    getTemplateNotification(frm, p.getComponents(), note);
                }else if (comp instanceof OrTabbedPane) {
                    getTemplateNotification(frm, ((OrTabbedPane)comp).getComponents(), note);
                }else if (comp instanceof OrSplitPane) {
                    getTemplateNotification(frm, ((OrSplitPane)comp).getComponents(), note);
                }
            }
        }
    } 
    
    private void addProcessBtn(ProcessItem action) {
        if (action.isBtnToolBar) {
            for(int i=0;i<additionalItem.size();++i) {
                if (action.equals(additionalItem.get(i))) {
                    return;
                }
            }
            additionalItem.add(action);
            OrTransparentButton prBtn = new OrTransparentButton(action);
            prBtn.setToolTipText(action.getValue(Action.NAME).toString());
            prBtn.setIcon(Utils.processCreateImage(((ProcessItem) action).icon));
            Utils.setAllSize(prBtn, new Dimension(32, 32));
            prBtn.setText(null);
            prBtn.setMargin(new Insets(0, isFirstBtn ? 6 : 3, 0, 0));
            if (isFirstBtn) {
                isFirstBtn = false;
            }
            toolBar.add(prBtn);
        }
    }
    
    /**
     * @return the config
     */
    public static GlobalConfig getConfig() {
        return config;
    }
    
    /**
     * @return the config
     */
    public static void reloadGlobalConfig() {
        config.reloadParam();
        conf = Utils.mergeConfig(config.getConfig());
        GRADIENT_MAIN_FRAME = conf.getGradientMainFrame();
        GRADIENT_CONTROL_PANEL = conf.getGradientControlPanel();
        GRADIENT_MENU_PANEL = conf.getGradientMenuPanel();
        TRANSPARENT_MAIN = conf.isTransparentMain();
        TRANSPARENT_DIALOG = conf.isTransparentDialog();
        colorMain = conf.getColorMain();
        TRANSPARENT_CELL_TABLE = conf.getTransparentCellTable();
        colorHeaderTable = conf.getColorHeaderTable();
        colorTabTitle = conf.getColorTabTitle();
        colorBackTabTitle = conf.getColorBackTabTitle();
        colorFontTabTitle = conf.getColorFontTabTitle();
        colorFontBackTabTitle = conf.getColorFontBackTabTitle();
        transparentBackTabTitle = conf.getTransparentBackTabTitle();
        transparentSelectedTabTitle = conf.getTransparentSelectedTabTitle();
        colfldNoFLC = conf.getGradientFieldNOFLC();
        COLOR_FIELD_NO_FLC = (colfldNoFLC != null && colfldNoFLC.isEnabled()) ? colfldNoFLC.getStartColor()
                : new Color(255, 204, 204);
    }
    
    private void showConfigEditor() {
        LocalEditor editor = new LocalEditor();
        JScrollPane scroller = new JScrollPane(editor);
        kz.tamur.rt.Utils.setAllSize(scroller, new Dimension(480, 600));
        scroller.setOpaque(!TRANSPARENT_MAIN);
        scroller.getViewport().setOpaque(!TRANSPARENT_MAIN);
        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Редактор локальной конфигурации", scroller, false, true);
        dlg.show();
        if (dlg.isOK()) {
            editor.setAllConfig();
        } else if (dlg.getResult()==ButtonsFactory.BUTTON_DEFAULT){
            editor.resetConfig();
        }
    }

    /**
     * Получить main ui.
     *
     * @return the mainUI
     */
    public UIFrame getMainUI() {
        return mainUI;
    }

    /**
     * Получить менеджер фреймов.
     *
     * @return frameManager
     */
    public FrameManager getFrameManager() {
        return frameManager;
    }
    
    public void showAlarm(String title) {
        if (title == null || title.length() == 0) {
            title = "Вам пришло уведомление!";
        }
        alarmLabel.setVisible(true);
        alarmLabel.setForeground(Color.RED);
        popUpMessage = new PopupWindow(title);
        popUpMessage.setVisible(true);
        stopAlarm.start();
    }
    
    public void hideAlarm() {
        alarmLabel.setVisible(false);
    }
    
    @Override
    public void setState(int state) {
        switch (state) {
        case STATE_DEF:
            stateFrame.setText("");
            break;
        case STATE_FIND:
            stateFrame.setText(res.getString("stateFind")); 
            break;
        default:
            break;
        }
    }
    /**
     * @return the res
     */
    public ResourceBundle getRes() {
        return res;
    }
	public User getUser(){
		return user;
	}
    private void showManual() {
        String curDir = Funcs.getSystemProperty("user.dir");
        String manual = Funcs.getSystemProperty("manual");

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            System.out.println("Нет поддержки открытия файлов!");
        }

        try {
            File man;
            if (manual == null) {
                man = Funcs.getCanonicalFile(curDir + "/manual.doc");
                if (man.exists()) {
                    desktop.open(man);
                } else {
                    man = Funcs.getCanonicalFile(curDir + "/manual.docx");
                    if (man.exists()) {
                        desktop.open(man);
                    } else {
                        man = Funcs.getCanonicalFile(curDir + "/manual.pdf");
                        if (man.exists()) {
                            desktop.open(man);
                        } else {
                            System.out.println("Файлы " + man.getAbsolutePath() + " (.doc, .docx) не найдены!");
                        }
                    }
                }
            } else {
                man = Funcs.getCanonicalFile(curDir + "/" + manual);
                if (man.exists()) {
                    desktop.open(man);
                } else {
                    System.out.println("Файл " + man.getAbsolutePath() + " не найден!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void setReportComplete(long flowId) {
	}

	@Override
	public void setProgressCaption(String text) {
		progressLabel.setText(text);
	}

	@Override
	public void setProgressMinimum(int val) {
		progress.setMinimum(val);
	}

	@Override
	public void setProgressMaximum(int val) {
		progress.setMaximum(val);
	}

	@Override
	public void setProgressValue(int val) {
		progress.setValue(val);
	}
}