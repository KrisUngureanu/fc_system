package kz.tamur;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.VERTICAL;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Constants.INSETS_0;
import static kz.tamur.comps.Utils.createCheckMenuItem;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.guidesigner.ButtonsFactory.FN_ACTIVE_USERS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_BASE;
import static kz.tamur.guidesigner.ButtonsFactory.FN_BOXES;
import static kz.tamur.guidesigner.ButtonsFactory.FN_CHAT;
import static kz.tamur.guidesigner.ButtonsFactory.FN_CLASSES;
import static kz.tamur.guidesigner.ButtonsFactory.FN_CONFIG;
import static kz.tamur.guidesigner.ButtonsFactory.FN_CONFIGS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_FILTERS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_FUNC;
import static kz.tamur.guidesigner.ButtonsFactory.FN_HYPERS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_INTERFACES;
import static kz.tamur.guidesigner.ButtonsFactory.FN_PROC;
import static kz.tamur.guidesigner.ButtonsFactory.FN_RECYCLE;
import static kz.tamur.guidesigner.ButtonsFactory.FN_REPL;
import static kz.tamur.guidesigner.ButtonsFactory.FN_REPORTS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_RIGHTS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_SCHEDULER;
import static kz.tamur.guidesigner.ButtonsFactory.FN_SEARCH;
import static kz.tamur.guidesigner.ButtonsFactory.FN_SERVICES;
import static kz.tamur.guidesigner.ButtonsFactory.FN_SERVICES_CONTROL;
import static kz.tamur.guidesigner.ButtonsFactory.FN_TERMINAL;
import static kz.tamur.guidesigner.ButtonsFactory.FN_USERS;
import static kz.tamur.guidesigner.ButtonsFactory.FN_VCS_CHANGE;
import static kz.tamur.guidesigner.ButtonsFactory.createFunctionButton;
import static kz.tamur.guidesigner.MessagesFactory.ERROR_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.showMessageDialog;
import static kz.tamur.rt.MainFrame.GRADIENT_MAIN_FRAME;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createListBox;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getDefaultFont;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconJpg;
import static kz.tamur.rt.Utils.getLightSysColor;
import static kz.tamur.rt.Utils.getScreenDesigner;
import static kz.tamur.rt.Utils.getSysColor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import kz.tamur.admin.Classes;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.Utils;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.ButtonsFactory.FunctionToolButton;
import kz.tamur.guidesigner.ConfigEditor;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.Recycle;
import kz.tamur.guidesigner.Splash;
import kz.tamur.guidesigner.bases.BasePanel;
import kz.tamur.guidesigner.boxes.BoxPanel;
import kz.tamur.guidesigner.changemon.ChangeMonFrame;
import kz.tamur.guidesigner.changemon.ChangeMonHistoryPanel;
import kz.tamur.guidesigner.config.ConfigurationsPanel;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.guidesigner.expr.TabObj;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.hypers.HyperPanel;
import kz.tamur.guidesigner.languages.LangRegPanel;
import kz.tamur.guidesigner.procdesigner.ProcedureFrame;
import kz.tamur.guidesigner.replication.ReplicationFrame;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.guidesigner.scheduler.SchedulerPane;
import kz.tamur.guidesigner.search.SearchPanel;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.terminal.TerminalPanel;
import kz.tamur.guidesigner.userrights.UserRightsPane;
import kz.tamur.guidesigner.users.ActiveUsersPanel;
import kz.tamur.guidesigner.users.ChatPanel;
import kz.tamur.guidesigner.users.EmptyChatPanel;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserPanel;
import kz.tamur.guidesigner.xmldesigner.XmlFrame;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.rt.ReportWrapper;
import kz.tamur.rt.login.LoginBox;
import kz.tamur.util.AboutDialog;
import kz.tamur.util.AppState;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.FrameTemplate;
import kz.tamur.util.Funcs;
import kz.tamur.util.XmlParserUtil;
import kz.tamur.util.crypto.KalkanUtil;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import com.cifs.or2.client.ClientCallback;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PathWordChange;
import com.cifs.or2.client.ReplProgressListener;
import com.cifs.or2.client.ScriptExecResultListener;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Vital Date: 26.10.2004
 * Time: 10:59:09
 */
public class Or3Frame extends FrameTemplate implements ItemListener, ActionListener, ReplProgressListener, ScriptExecResultListener {
    private CardLayout layout = new CardLayout() {

        /**
         * При отображении компонента необходимо делать его видимым, так как свойство его могут быть изменены.
         * 
         * @see java.awt.CardLayout#show(java.awt.Container, java.lang.String)
         */
        public void show(Container parent, String name) {
            parent.setVisible(true);
            super.show(parent, name);
        }
    };
    private JButton but = new JButton();
    private OrGradientMenuBar mainMenuBar = new OrGradientMenuBar();
    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private DesignerStatusBar currentStatusBar = statusBar;
    private JMenu panelMenu = new JMenu("Панель");
    private JMenu serviceMenu = new JMenu("Сервис");
    private JMenu helpMenu = new JMenu("?");
    private JCheckBoxMenuItem emptyItem = new JCheckBoxMenuItem("", true);
    private JCheckBoxMenuItem serviceItem = createCheckMenuItem("Процессы", FN_SERVICES);
    private JCheckBoxMenuItem classesItem = createCheckMenuItem("Классы", FN_CLASSES);
    private JCheckBoxMenuItem interfacesItem = createCheckMenuItem("Интерфейсы", FN_INTERFACES);
    private JCheckBoxMenuItem filtersItem = createCheckMenuItem("Фильтры", FN_FILTERS);
    private JCheckBoxMenuItem usersItem = createCheckMenuItem("Пользователи", FN_USERS);
    private JCheckBoxMenuItem reportsItem = createCheckMenuItem("Отчёты", FN_REPORTS);
    private JCheckBoxMenuItem basesItem = createCheckMenuItem("Структура баз", FN_BASE);
    private JCheckBoxMenuItem boxesItem = createCheckMenuItem("Пункты обмена", FN_BOXES);
    private JCheckBoxMenuItem schedItem = createCheckMenuItem("Планировщик", FN_SCHEDULER);
    private JCheckBoxMenuItem hypersItem = createCheckMenuItem("Гиперменю", FN_HYPERS);
    private JCheckBoxMenuItem funcItem = createCheckMenuItem("Функции", FN_FUNC);
    private JCheckBoxMenuItem procItem = createCheckMenuItem("Процедуры", FN_PROC);
    private JCheckBoxMenuItem changeMonItem = createCheckMenuItem("Монитор изменений", FN_VCS_CHANGE);
    private JCheckBoxMenuItem activeUsersItem = createCheckMenuItem("Активные пользователи", FN_ACTIVE_USERS);
    private JCheckBoxMenuItem replItem = createCheckMenuItem("Репликация", FN_REPL);
    private JCheckBoxMenuItem searchItem = createCheckMenuItem("Поиск", FN_SEARCH);
    private JCheckBoxMenuItem terminalItem = createCheckMenuItem("Консоль", FN_TERMINAL);
    private JCheckBoxMenuItem configItem = createCheckMenuItem("Настройки", FN_CONFIG);
    private JCheckBoxMenuItem serviceControlItem = createCheckMenuItem("Управление", FN_SERVICES_CONTROL);
    private JCheckBoxMenuItem recycleItem = createCheckMenuItem("Корзина", FN_RECYCLE);
    private JCheckBoxMenuItem chatItem = createCheckMenuItem("Чат", FN_CHAT);
    private JCheckBoxMenuItem rightsItem = createCheckMenuItem("Права доступа", FN_RIGHTS);
    private JCheckBoxMenuItem configsItem = createCheckMenuItem("Конфигурации", FN_CONFIGS);
    private JMenuItem closeItem = createMenuItem("Выход");

    private JMenuItem quickStartItem = createMenuItem("Быстрый старт...", "QuickStart");
    private JMenuItem langRegItem = createMenuItem("Регистрация языков", "LangReg");
    private JMenuItem closeFuncItem = createMenuItem("Свернуть все функции", "CloseFunc");

    private JMenuItem aboutItem = createMenuItem("О программе");

    private ButtonGroup menuBg = new ButtonGroup();
    private JToolBar buttonsBar = Utils.createDesignerToolBar();
    private JToolBar buttonsBarRight = Utils.createDesignerToolBar();
    private FunctionToolButton emptyBtn = new FunctionToolButton("", true);
    private FunctionToolButton previousBtn;
    private FunctionToolButton serviceBtn = createFunctionButton(FN_SERVICES);
    private FunctionToolButton ifcBtn = createFunctionButton(FN_INTERFACES);
    private FunctionToolButton classesBtn = createFunctionButton(FN_CLASSES);
    private FunctionToolButton filtersBtn = createFunctionButton(FN_FILTERS);
    private FunctionToolButton usersBtn = createFunctionButton(FN_USERS);
    private FunctionToolButton reportsBtn = createFunctionButton(FN_REPORTS);
    private FunctionToolButton hyperBtn = createFunctionButton(FN_HYPERS);
    private FunctionToolButton baseBtn = createFunctionButton(FN_BASE);
    private FunctionToolButton boxBtn = createFunctionButton(FN_BOXES);
    private FunctionToolButton schedBtn = createFunctionButton(FN_SCHEDULER);
    private FunctionToolButton xmlBtn = createFunctionButton(FN_FUNC);
    private FunctionToolButton procBtn = createFunctionButton(FN_PROC);
    private FunctionToolButton changeMonBtn = createFunctionButton(FN_VCS_CHANGE);
    private FunctionToolButton activeUsersBtn = createFunctionButton(FN_ACTIVE_USERS);
    private FunctionToolButton replBtn = createFunctionButton(FN_REPL);
    private FunctionToolButton searchBtn = createFunctionButton(FN_SEARCH);
    private FunctionToolButton terminalBtn = createFunctionButton(FN_TERMINAL);
    private FunctionToolButton serviceControlBtn = createFunctionButton(FN_SERVICES_CONTROL);
    private FunctionToolButton recycleBtn = createFunctionButton(FN_RECYCLE);
    private FunctionToolButton configBtn = createFunctionButton(FN_CONFIG);
    private FunctionToolButton rightsBtn = createFunctionButton(FN_RIGHTS);
    private FunctionToolButton configsBtn = createFunctionButton(FN_CONFIGS);
    private ButtonGroup bg = new ButtonGroup();
    private GradientPanel workAreaPanel = new GradientPanel(layout);
    private GradientPanel controlAreaPanel = new GradientPanel(layout);
    private GradientPanel emptyPanel = new GradientPanel();
    private MainFrame serviceFrame;
    private Classes classFrame;
    private DesignerFrame designerFrame;
    private ReportPanel reportFrame;
    private UserPanel userFrame;
    private HyperPanel hyperFrame;
    private BasePanel baseFrame;
    private BoxPanel boxesFrame;
    private FiltersPanel filtersFrame;
    private SchedulerPane schedFrame;
    private XmlFrame xmlFrame;
    private ProcedureFrame procFrame;
    private ChangeMonFrame changeMonFrame;
    private ReplicationFrame replFrame;
    private ActiveUsersPanel activeUsersFrame;
    private SearchPanel searchFrame;
    private TerminalPanel terminalFrame;
    private ConfigEditor configEditor;
    private Recycle recycle;
    private ServiceControl serviceControl;
    private ChatPanel chatFrame;
    private EmptyChatPanel emptyChatFrame;
    private UserRightsPane rightsFrame;
    private ConfigurationsPanel configsFrame;
    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");
    private JLabel workStatusLabel = createLabel("");
    public static ChangeMonHistoryPanel historysPanel;

    /**
     * Защищает от повторного вызова закрузки компонента в редакторе
     * Причина при выборе клике по кнопке (например <code>serviceBtn</code>) срабатывает обработка события и на <code>serviceItem</code>
     */
    private boolean blockChangeItem = false;

    /** Индекс дисплея конструктора, инициализируется после авторизации. */
    Kernel krn;
    User user;
    public int screen = -1;

    public static boolean isQuickStartShow = true;
    public static QuickStartPanel quickStart;
    /** Индекс дисплея конструктора, используется до авторизации. */
    public static int screen_ = getScreenDesigner();

    private boolean isFuncsExist = false;
    private static Timer newMessageTest;
    private static JLabel newMessages = createLabel("Вам пришло сообщение!");
    private static boolean isOpaque = false;
    private static Or3Frame or3Frame = null;
    private static String serverType = "";
    private static String host = "";
    private static String port = "";
    private static String baseName = "";
    private static String webUrl = "";
    private static String earName = "";
    private static final String appTitle = "Инструменты разработчика и администратора Or3";
    private static ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    private static boolean isSysDb = false;

    public static Or3Frame instance() {
        if (or3Frame == null) {
            or3Frame = new Or3Frame();
        }
        return or3Frame;
    }
    
    Or3Frame() throws HeadlessException {
        screen = screen_;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	if (Kernel.instance().getUser() != null) {
    		isOpaque = !kz.tamur.rt.MainFrame.TRANSPARENT_DIALOG;
    		init();
        	historysPanel = new ChangeMonHistoryPanel();
    	}
    	setIconImage(getImageIcon("icon").getImage());
        setTitle(appTitle);
    }

    private class MyDispatcher implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                firePropertyChange("stopSearch", false, true);
            }
            return false;
        }
    }

    /**
     * Инициализация необходимых классов.
     */
    public void initControl() {
        if (serviceFrame == null) {
            serviceFrame = new MainFrame();
            serviceFrame.applyRights(Kernel.instance().getUser());
            workAreaPanel.add(serviceFrame, "serviceDesigner");
        }
        if (filtersFrame == null) {
            filtersFrame = new FiltersPanel(false);
            workAreaPanel.add(filtersFrame, "filtersDesigner");
        }
    }

    private void initMenu() {
        kz.tamur.rt.MainFrame.reloadGlobalConfig();
        panelMenu.setFont(getDefaultFont());
        panelMenu.setForeground(getLightSysColor());
        panelMenu.setMnemonic('Ф');
        panelMenu.add(serviceItem);
        panelMenu.add(classesItem);
        panelMenu.add(interfacesItem);
        panelMenu.add(filtersItem);
        panelMenu.add(usersItem);
        panelMenu.add(reportsItem);
        panelMenu.add(basesItem);
        panelMenu.add(boxesItem);
        panelMenu.add(hypersItem);
        panelMenu.add(schedItem);
        panelMenu.add(isFuncsExist ? funcItem : activeUsersItem);
        panelMenu.add(procItem);
        panelMenu.add(changeMonItem);
        panelMenu.add(replItem);
        panelMenu.add(searchItem);
        panelMenu.add(terminalItem);
        panelMenu.add(serviceControlItem);
        panelMenu.add(configItem);
        // panelMenu.add(recycleItem); TODO разработка отложена
        panelMenu.add(chatItem);
        panelMenu.add(rightsItem);
        panelMenu.add(configsItem);
        panelMenu.addSeparator();
        panelMenu.add(closeItem);

        serviceItem.addItemListener(this);
        classesItem.addItemListener(this);
        interfacesItem.addItemListener(this);
        filtersItem.addItemListener(this);
        usersItem.addItemListener(this);
        reportsItem.addItemListener(this);
        basesItem.addItemListener(this);
        boxesItem.addItemListener(this);
        hypersItem.addItemListener(this);
        schedItem.addItemListener(this);
        replItem.addItemListener(this);
        searchItem.addItemListener(this);
        terminalItem.addItemListener(this);
        configItem.addItemListener(this);
        chatItem.addItemListener(this);
        closeItem.addActionListener(this);
        serviceControlItem.addItemListener(this);
        recycleItem.addItemListener(this);
        rightsItem.addItemListener(this);
        configsItem.addItemListener(this);
        if (isFuncsExist) {
            funcItem.addItemListener(this);
        } else {
            activeUsersItem.addItemListener(this);
        }
        procItem.addItemListener(this);
        changeMonItem.addItemListener(this);
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        menuBg.add(emptyItem);
        menuBg.add(serviceItem);
        menuBg.add(classesItem);
        menuBg.add(interfacesItem);
        menuBg.add(filtersItem);
        menuBg.add(usersItem);
        menuBg.add(reportsItem);
        menuBg.add(basesItem);
        menuBg.add(boxesItem);
        menuBg.add(hypersItem);
        menuBg.add(schedItem);
        menuBg.add(isFuncsExist ? funcItem : activeUsersItem);
        menuBg.add(procItem);
        menuBg.add(changeMonItem);
        menuBg.add(replItem);
        menuBg.add(searchItem);
        menuBg.add(serviceControlItem);
        menuBg.add(recycleItem);
        menuBg.add(terminalItem);
        menuBg.add(configItem);
        menuBg.add(chatItem);
        menuBg.add(rightsItem);
        menuBg.add(configsItem);

        serviceMenu.setFont(getDefaultFont());
        serviceMenu.setForeground(getLightSysColor());
        serviceMenu.setMnemonic('С');
        serviceMenu.add(quickStartItem);
        serviceMenu.add(langRegItem);
        serviceMenu.add(closeFuncItem);

        quickStartItem.addActionListener(this);
        langRegItem.addActionListener(this);
        closeFuncItem.addActionListener(this);

        helpMenu.setFont(getDefaultFont());
        helpMenu.setForeground(getLightSysColor());
        helpMenu.setMnemonic('?');
        helpMenu.add(aboutItem);

        aboutItem.addActionListener(this);

        mainMenuBar.add(new JLabel(getImageIcon("menuFlag")));
        mainMenuBar.add(panelMenu);
        mainMenuBar.add(serviceMenu);
        mainMenuBar.add(helpMenu);
        setPrimaryMenuBar(mainMenuBar);
    }

    private void init() {
        String funcs = Funcs.getSystemProperty("funcs");
        if (funcs != null && "1".equals(funcs)) {
            isFuncsExist = true;
        }
        // Global KeyListener!!!
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        initMenu();
        getRootPane().setDefaultButton(but);
        GridBagLayout gbl = new GridBagLayout();
        getContentPane().setLayout(gbl);
        buttonsBar.setOrientation(JToolBar.VERTICAL);
        buttonsBarRight.setOrientation(JToolBar.VERTICAL);
        buttonsBar.add(serviceBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(classesBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(ifcBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(changeMonBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(serviceControlBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(isFuncsExist ? xmlBtn : activeUsersBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(searchBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(boxBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(baseBtn);
        buttonsBar.addSeparator();
        buttonsBar.add(procBtn);
        //buttonsBar.add(changeMonBtn);
        // buttonsBar.add(recycleBtn); TODO разработка отложена

        buttonsBarRight.add(filtersBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(usersBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(reportsBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(hyperBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(schedBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(replBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(terminalBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(configBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(rightsBtn);
        buttonsBarRight.addSeparator();
        buttonsBarRight.add(configsBtn);

        getContentPane().add(buttonsBar, new GridBagConstraints(0, 0, 1, 1, 0, 1, WEST, VERTICAL, INSETS_0, 0, 0));
        getContentPane().add(buttonsBarRight, new GridBagConstraints(3, 0, 1, 1, 0, 1, EAST, VERTICAL, INSETS_0, 0, 0));
        serviceBtn.addItemListener(this);
        classesBtn.addItemListener(this);
        ifcBtn.addItemListener(this);
        filtersBtn.addItemListener(this);
        usersBtn.addItemListener(this);
        reportsBtn.addItemListener(this);
        hyperBtn.addItemListener(this);
        baseBtn.addItemListener(this);
        boxBtn.addItemListener(this);
        schedBtn.addItemListener(this);
        rightsBtn.addItemListener(this);
        configsBtn.addItemListener(this);
        if (isFuncsExist) {
            xmlBtn.addItemListener(this);
        } else {
            activeUsersBtn.addItemListener(this);
        }
        replBtn.addItemListener(this);
        searchBtn.addItemListener(this);
        terminalBtn.addItemListener(this);
        configBtn.addItemListener(this);
        serviceControlBtn.addItemListener(this);
        recycleBtn.addItemListener(this);
        procBtn.addItemListener(this);
        changeMonBtn.addItemListener(this);

        bg.add(emptyBtn);
        bg.add(serviceBtn);
        bg.add(classesBtn);
        bg.add(ifcBtn);
        bg.add(filtersBtn);
        bg.add(usersBtn);
        bg.add(reportsBtn);
        bg.add(hyperBtn);
        bg.add(baseBtn);
        bg.add(boxBtn);
        bg.add(schedBtn);
        bg.add(rightsBtn);
        bg.add(configsBtn);
        bg.add(isFuncsExist ? xmlBtn : activeUsersBtn);
        bg.add(replBtn);
        bg.add(searchBtn);
        bg.add(terminalBtn);
        bg.add(configBtn);
        bg.add(serviceControlBtn);
        bg.add(recycleBtn);
        bg.add(procBtn);
        bg.add(changeMonBtn);
        workAreaPanel.add(emptyPanel, "emptyPanel");
        getContentPane().add(controlAreaPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1, EAST, BOTH, INSETS_0, 0, 0));
        getContentPane().add(workAreaPanel, new GridBagConstraints(2, 0, 1, 1, 1, 1, CENTER, BOTH, INSETS_0, 0, 0));

        workStatusLabel.setIconTextGap(10);
        statusBar.addAnyComponent(workStatusLabel,1);
        statusBar.addSeparator();
        statusBar.addAnyComponent(newMessages);
        statusBar.addSeparator();
        statusBar.addAnyComponent(currentDbName);
        statusBar.addSeparator();
        currentUserLable.setIcon(getImageIcon("User"));
        statusBar.addAnyComponent(currentUserLable);
        statusBar.addSeparator();
        dsLabel.setIcon(getImageIcon("HostConn"));
        dsLabel.setIconTextGap(10);
        statusBar.addAnyComponent(dsLabel);
        statusBar.addSeparator();
        serverLabel.setIcon(getImageIcon("PortConn"));
        serverLabel.setIconTextGap(10);
        statusBar.addAnyComponent(serverLabel);
        statusBar.addCorner();

        setStatusBar(currentStatusBar);
        ((GradientPanel) getContentPane()).setGradient(GRADIENT_MAIN_FRAME.isEmpty() ? Constants.GLOBAL_DEF_GRADIENT
                : GRADIENT_MAIN_FRAME);
        controlAreaPanel.setOpaque(isOpaque);
        workAreaPanel.setOpaque(isOpaque);
        emptyPanel.setOpaque(isOpaque);
        buttonsBar.setOpaque(isOpaque);
        buttonsBarRight.setOpaque(isOpaque);
        controlAreaPanel.setMinimumSize(new Dimension(300, 200));
    }

    public void setStatusBar(DesignerStatusBar statusBar) {
        getContentPane().remove(currentStatusBar);
        currentStatusBar = statusBar;
        if (stateFrame.getParent() != null) {
            stateFrame.getParent().remove(stateFrame);
        }
        statusBar.setFirstComponent(stateFrame);
        getContentPane().add(statusBar, new GridBagConstraints(0, 1, 4, 1, 1, 0, CENTER, GridBagConstraints.HORIZONTAL, INSETS_0, 0, 0));
        getContentPane().validate();
        getContentPane().repaint();
    }

    public void closeCurrent() {
        layout.show(workAreaPanel, "emptyPanel");
        setStatusBar(statusBar);
        setJMenuBar(null);
        emptyItem.setSelected(true);
        markBtn(emptyBtn);
    }

    public SearchPanel getSearchPanel() {
        return searchFrame;
    }

    public void quickStartClasses() {
        if (classFrame == null) {
            classFrame = new Classes();
            classFrame.applyRights(Kernel.instance().getUser());
            workAreaPanel.add(classFrame, "classDesigner");
        }
        setJMenuBar(null);
        setStatusBar(classFrame.getStatusBar());
        classFrame.setSplitLocation();
        layout.show(workAreaPanel, "classDesigner");
        setTitle(appTitle + " - [Дизайнер классов]");
        classesItem.setSelected(true);
        markBtn(classesBtn);
    }

    public void quickStartUsers() {
        if (userFrame == null) {
            try {
                userFrame = new UserPanel();
                workAreaPanel.add(userFrame, "userDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (userFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            userFrame.placeDivider();
            layout.show(workAreaPanel, "userDesigner");
            setTitle(appTitle + " - [Управление пользователями]");
            usersItem.setSelected(true);
            markBtn(usersBtn);
        }
    }

    public void quickStartSearch() {
        if (searchFrame == null) {
            try {
                searchFrame = new SearchPanel();
                workAreaPanel.add(searchFrame, "searchDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (searchFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            searchFrame.setSplitLocation();
            layout.show(workAreaPanel, "searchDesigner");
            setTitle(appTitle + " - [Поиск и замена]");
            searchItem.setSelected(true);
            markBtn(searchBtn);
        }
    }

    public void quickStartHypers() {
        if (hyperFrame == null) {
            try {
                hyperFrame = new HyperPanel();
                workAreaPanel.add(hyperFrame, "hyperDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (hyperFrame != null) {
            setJMenuBar(null);
            setStatusBar(hyperFrame.getStatusBar());
            hyperFrame.placeDivider();
            layout.show(workAreaPanel, "hyperDesigner");
            setTitle(appTitle + " - [Управление гиперменю]");
            hypersItem.setSelected(true);
            markBtn(hyperBtn);
        }
    }

    public void quickStartBase() {
        if (baseFrame == null) {
            try {
                baseFrame = new BasePanel();
                workAreaPanel.add(baseFrame, "baseDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (baseFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            baseFrame.placeDivider();
            layout.show(workAreaPanel, "baseDesigner");
            setTitle(appTitle + " - [Управление структурами баз данных]");
            basesItem.setSelected(true);
            markBtn(baseBtn);
        }
    }

    public void quickStartXmlFrame() {
        if (xmlFrame == null) {
            try {
                xmlFrame = new XmlFrame();
                workAreaPanel.add(xmlFrame, "xmlDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (xmlFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            xmlFrame.placeDivider();
            layout.show(workAreaPanel, "xmlDesigner");
            setTitle(appTitle + " - [Управление функциями]");
            markBtn(xmlBtn);
        }
    }

    public void quickStartProcFrame() {
        if (procFrame == null) {
            try {
                procFrame = new ProcedureFrame();
                workAreaPanel.add(procFrame, "procDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (procFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            procFrame.placeDivider();
            layout.show(workAreaPanel, "procDesigner");
            setTitle(appTitle + " - [Управление процедурами]");
            markBtn(procBtn);
        }
    }
    public void quickStartChangeMonFrame() {
        if (changeMonFrame == null) {
            try {
            	changeMonFrame = new ChangeMonFrame();
                workAreaPanel.add(changeMonFrame, "changeMonDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (changeMonFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            layout.show(workAreaPanel, "changeMonDesigner");
            setTitle(appTitle + " - [Управление изменениями]");
            markBtn(changeMonBtn);
        }
    }
    public void quickStartActivUsersFrame() {
        if (activeUsersFrame == null) {
            try {
                activeUsersFrame = new ActiveUsersPanel();
                workAreaPanel.add(activeUsersFrame, "activeUsers");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (activeUsersFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            layout.show(workAreaPanel, "activeUsers");
            setTitle(appTitle + " - [Управление активными пользователями]");
            activeUsersItem.setSelected(true);
            markBtn(activeUsersBtn);
        }
    }

    public void quickStartChat() {
        if (chatFrame == null) {
            try {
        	    NativeInterface.open();

        	    chatFrame = new ChatPanel();
        	    workAreaPanel.add(chatFrame, "chat");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (chatFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            layout.show(workAreaPanel, "chat");
            setTitle(appTitle + " - [Чат]");
            markBtn(emptyBtn);
        }
    }

    public void quickStartEmptyChat() {
        if (emptyChatFrame == null) {
            try {
                emptyChatFrame = new EmptyChatPanel();
                workAreaPanel.add(emptyChatFrame, "emptyChat");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (emptyChatFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            layout.show(workAreaPanel, "emptyChat");
            setTitle(appTitle + " - [Чат]");
            markBtn(emptyBtn);
        }
    }

    public void quickStartReplFrame() {
        if (replFrame == null) {
            try {
                replFrame = new ReplicationFrame();
                workAreaPanel.add(replFrame, "replicationFrame");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (replFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            layout.show(workAreaPanel, "replicationFrame");
            setTitle(appTitle + " - [Репликация баз данных]");
            replItem.setSelected(true);
            markBtn(replBtn);
        }
    }

    public void quickStartScheduler() {
        if (schedFrame == null) {
            try {
                schedFrame = new SchedulerPane();
                workAreaPanel.add(schedFrame, "schedDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (schedFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            schedFrame.placeDivider();
            layout.show(workAreaPanel, "schedDesigner");
            setTitle(appTitle + " - [Управление планированием заданий]");
            schedItem.setSelected(true);
            markBtn(schedBtn);
        }
    }

    public void quickStartUserRights() {
        if (rightsFrame == null) {
            try {
            	rightsFrame = new UserRightsPane();
                workAreaPanel.add(rightsFrame, "userRightsDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (rightsFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            rightsFrame.placeDivider();
            layout.show(workAreaPanel, "userRightsDesigner");
            setTitle(appTitle + " - [Управление правами доступа]");
            rightsItem.setSelected(true);
            markBtn(rightsBtn);
        }
    }

    public void quickStartConfigurations() {
        if (configsFrame == null) {
            try {
            	configsFrame = new ConfigurationsPanel();
                workAreaPanel.add(configsFrame, "configurationsDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (configsFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            configsFrame.placeDivider();
            layout.show(workAreaPanel, "configurationsDesigner");
            setTitle(appTitle + " - [Управление конфигурациями OR3]");
            configsItem.setSelected(true);
            markBtn(configsBtn);
        }
    }
    
    public void jumpBox(KrnObject obj) {
    	quickStartBoxes();
    	boxesFrame.setSelectedRow(obj);
    }

    public void quickStartBoxes() {
        if (boxesFrame == null) {
            try {
                boxesFrame = new BoxPanel();
                workAreaPanel.add(boxesFrame, "boxDesigner");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (boxesFrame != null) {
            setJMenuBar(null);
            setStatusBar(statusBar);
            boxesFrame.placeDivider();
            layout.show(workAreaPanel, "boxDesigner");
            setTitle(appTitle + " - [Управление пунктами обмена]");
            boxesItem.setSelected(true);
            markBtn(boxBtn);
        }
    }

    public void quickStartTerminal() {
        if (terminalFrame == null) {
            terminalFrame = new TerminalPanel();
            workAreaPanel.add(terminalFrame, "Terminator");
        }
        if (terminalFrame != null) {
            setJMenuBar(terminalFrame.getMenu());
            setStatusBar(statusBar);
            terminalFrame.placeDivider();
            layout.show(workAreaPanel, "Terminator");
            setTitle(appTitle + " - [Console]");
            terminalItem.setSelected(true);
            markBtn(terminalBtn);
        }
    }

    public void quickStartReports() {
        quickStartReports(true);
    }

    public void quickStartReports(boolean isChange) {
        if (reportFrame == null) {
            reportFrame = new ReportPanel();
            workAreaPanel.add(reportFrame, "reportDesigner");
        }
        setJMenuBar(null);
        setStatusBar(reportFrame.getStatusBar());
        if (isChange) {
            reportFrame.placeDivider();
            reportFrame.rebuildPanels();
            layout.show(workAreaPanel, "reportDesigner");
            setTitle(appTitle + " - [Дизайнер отчётов]");
            reportsItem.setSelected(true);
            markBtn(reportsBtn);
        }
    }

    public void quickStartConfig() {
        if (configEditor == null) {
            configEditor = new ConfigEditor();
            workAreaPanel.add(configEditor, "Config");
        }
        setStatusBar(statusBar);
        configEditor.initMenu();
        setJMenuBar(configEditor.getMenu());
        layout.show(workAreaPanel, "Config");
        setTitle(appTitle + " - [Конфигуратор]");
        configItem.setSelected(true);
        markBtn(configBtn);
        configEditor.reloadConfig();
    }

    public void quickStartServiceControl() {
        CursorToolkit.startWaitCursor(this);
        if (designerFrame == null) {
            designerFrame = DesignerFrame.instance();
            designerFrame.applyRights(Kernel.instance().getUser());
            workAreaPanel.add(designerFrame, "guiDesigner");
        }
        if (reportFrame == null) {
            reportFrame = new ReportPanel();
            workAreaPanel.add(reportFrame, "reportDesigner");
        }
        if (serviceControl == null) {
            serviceControl = ServiceControl.instance();
            controlAreaPanel.add(serviceControl, "ServiceControl");
        }
        serviceControl.rebuildPanels();
        serviceControl.initMenu();

        ControlTabbedContent tc = serviceControl.getContentTabs();
        int lst = tc.getLastSelectedTab();
        if (lst != -1 && lst < tc.getTabCount()) {
            tc.setSelectedIndex(lst);
        } else {
            tc.fireChange();
        }
        setJMenuBar(serviceControl.getMenu());
        workAreaPanel.setVisible(false);
        layout.show(controlAreaPanel, "ServiceControl");
        setTitle(appTitle + " - [Управление процессами]");
        serviceControlItem.setSelected(true);
        markBtn(serviceControlBtn);
        setStatusBar(statusBar);
        if (serviceControl.getTree() == null) {
            setJMenuBar(null);
        }
        CursorToolkit.stopWaitCursor(this);
    }

    public void quickStartRecycle() {

        if (recycle == null) {
            recycle = new Recycle();
            workAreaPanel.add(recycle, "Recycle");
        }
        setJMenuBar(recycle.getMenu());
        layout.show(workAreaPanel, "Recycle");
        setTitle(appTitle + " - [Корзина]");
        recycleItem.setSelected(true);
        markBtn(recycleBtn);
    }

    public void jumpMethod(KrnMethod method) {
        quickStartClasses();
        classFrame.load(method);
    }
    
    public void jumpTrigger(String uid, int ownerType, int triggerType) {
        quickStartClasses();
        classFrame.load(uid, ownerType, triggerType);
    }

    public void jumpInterface(KrnObject object) {
        if (ServiceControl.instance() == null) {
            Or3Frame.instance().initControl();
        }
        quickStartIfc(false);
        designerFrame.load(object, null);
    }

    public void jumpInterface(KrnObject object, String title) {
        if (ServiceControl.instance() == null) {
            Or3Frame.instance().initControl();
        }
        try {
            Map<String, String> comps = kz.tamur.util.XmlParserUtil.parseUIXml(object, (int) Kernel.instance().getUser()
                    .getIfcLang().id, title);
            if (comps.size() == 0) {
                quickStartIfc(false);
                designerFrame.load(object, true, null);
                return;
            }

            XmlParserUtil.ResultListModel model = new XmlParserUtil().new ResultListModel(comps);
            JList list = createListBox(model, 0);
            list.setBackground(getLightSysColor());
            list.setSelectedIndex(0);
            list.setOpaque(isOpaque);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Найдено в следующих компонентах", list);
            dlg.show();
            if (dlg.getResult() == BUTTON_OK) {
                quickStartIfc(false);
                designerFrame.load(object, true, null);
                designerFrame.searchComponentByTitle(model.getIdAt(list.getSelectedIndex()), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void jumpService(KrnObject object) {
        if (ServiceControl.instance() == null) {
            Or3Frame.instance().initControl();
        }
    	quickStartService(false);
        serviceFrame.load(object, null);
    }

    public void jumpService(KrnObject object, String title) {
        if (ServiceControl.instance() == null) {
            Or3Frame.instance().initControl();
        }
        try {
            Map<String, String> comps = kz.tamur.util.XmlParserUtil.parseDiagramXML(object, (int) Kernel.instance().getUser()
                    .getIfcLang().id, title);

            if (comps.size() == 0) {
                quickStartService(false);
                serviceFrame.load(object, null);
                return;
            }

            XmlParserUtil.ResultListModel model = new XmlParserUtil().new ResultListModel(comps);
            JList list = createListBox(model, 0);
            list.setBackground(getLightSysColor());
            list.setSelectedIndex(0);
            list.setOpaque(isOpaque);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Найдено в следующих объектах графа", list);
            dlg.show();
            if (dlg.getResult() == BUTTON_OK) {
                quickStartService(false);
                serviceFrame.load(object, null);
                serviceFrame.searchDiagramComponent(model.getIdAt(list.getSelectedIndex()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpUser(KrnObject object) {
        quickStartUsers();
        userFrame.load(object);
    }

    public void jumpFilter(KrnObject object) {
        quickStartFilters(false);
        filtersFrame.load(object, null);
    }

    public void refreashFilter(KrnObject object) {
    	initControl();
        filtersFrame.refreashNode(object);
    }

    public void jumpReport(KrnObject object) {
        quickStartReports();
        reportFrame.load(object);
    }

    public void jumpClass(KrnObject object, String attrID) {
        quickStartClasses();
        classFrame.load(object, attrID);
    }

    public void jumpClassProperty(KrnClass cls) {
        quickStartClasses();
        classFrame.load(cls);
    }

    public void jumpAttrProperty(KrnAttribute attr) {
        quickStartClasses();
        classFrame.load(attr);
    }

    public InterfaceNode createInterface() {
        quickStartIfc(false);
        return designerFrame.create();
    }

    public void quickStartFilters(boolean isQuick) {
        quickStartFilters(isQuick, true);
    }

    public void quickStartFilters(boolean isQuick, boolean isChange) {
        if (designerFrame == null) {
            designerFrame = DesignerFrame.instance();
            designerFrame.applyRights(Kernel.instance().getUser());
            workAreaPanel.add(designerFrame, "guiDesigner");
        }
        if (filtersFrame == null) {
            filtersFrame = new FiltersPanel(false);
            workAreaPanel.add(filtersFrame, "filtersDesigner");
        }

        setJMenuBar(null);
        setStatusBar(filtersFrame.getStatusBar());
        if (isChange) {
            filtersFrame.rebuildPanels();
            filtersFrame.getTabbedContent().activeTypeTabs(Kernel.SC_FILTER.id);
            layout.show(workAreaPanel, "filtersDesigner");
            filtersFrame.placeDividers();
            setTitle(appTitle + " - [Дизайнер фильтров]");
            filtersItem.setSelected(true);
            markBtn(filtersBtn);
        }
        if (isQuick) {
            filtersFrame.open();
        }
    }

    public void quickStartIfc(boolean isQuick) {
        quickStartIfc(isQuick, true);
    }

    public void quickStartIfc(boolean isQuick, boolean isChange) {
        if (designerFrame == null) {
            designerFrame = DesignerFrame.instance();
            designerFrame.applyRights(Kernel.instance().getUser());
            workAreaPanel.add(designerFrame, "guiDesigner");
        }

        setJMenuBar(designerFrame.getMenu());
        setStatusBar(designerFrame.getStatusBar());
        if (isChange) {
            designerFrame.rebuildPanels();
            // деактивация вкладок других типов
            DesignerFrame.getTabbedContent().activeTypeTabs(Kernel.SC_UI.id);
            layout.show(workAreaPanel, "guiDesigner");
            designerFrame.placeDividers();
            setTitle(appTitle + " - [Дизайнер интерфейсов]");
            interfacesItem.setSelected(true);
            markBtn(ifcBtn);
        }
        if (isQuick) {
            designerFrame.load();
        }
    }

    public void quickStartService(boolean isQuick) {
        quickStartService(isQuick, true);
    }

    public void quickStartService(boolean isQuick, boolean isChange) {
        setJMenuBar(serviceFrame.getMenu());
        setStatusBar(serviceFrame.getStatusBar());
        if (isChange) {
            serviceFrame.rebuildPanels();
            serviceFrame.getTabbedContent().activeTypeTabs(Kernel.SC_PROCESS_DEF.id);
            layout.show(workAreaPanel, "serviceDesigner");
            serviceFrame.placeDividers();
            setTitle(appTitle + " - [Дизайнер процессов]");
            serviceItem.setSelected(true);
            markBtn(serviceBtn);

        }
        if (isQuick) {
            serviceFrame.open();
        }
    }

    public void setClassStatusBarInfo(String text, Icon icon, String subText) {
        if (classFrame != null) {
            classFrame.setSelNodeInfo(text);
            classFrame.setSubIcon(icon);
            classFrame.setIdField(subText);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (src instanceof FunctionToolButton) {
                blockChangeItem = true;
            }
            
            if (Kernel.instance().getBaseName() == null && src != configsBtn && src != configsItem) {
                MessagesFactory.showMessageDialog(or3Frame, MessagesFactory.ERROR_MESSAGE, "Конфигурация не выбрана!");
                return;
            }
            
            CursorToolkit.startWaitCursor(this);
            initControl();
            if (src == serviceBtn || src == serviceItem && !blockChangeItem) {
            //    initControl();
                quickStartService(false);
            } else if (src == classesBtn || src == classesItem && !blockChangeItem) {
            //    initControl();
                quickStartClasses();
            } else if (src == ifcBtn || src == interfacesItem && !blockChangeItem) {
            //    initControl();
                quickStartIfc(false);
            } else if (src == reportsBtn || src == reportsItem && !blockChangeItem) {
            //    initControl();
                quickStartReports();
            } else if (src == filtersBtn || src == filtersItem && !blockChangeItem) {
            //    initControl();
                quickStartFilters(false);
            } else if (src == usersBtn || src == usersItem && !blockChangeItem) {
            //   initControl();
                quickStartUsers();
            } else if (src == searchBtn || src == searchItem && !blockChangeItem) {
            //    initControl();
                quickStartSearch();
            } else if (src == hyperBtn || src == hypersItem && !blockChangeItem) {
            //    initControl();
                quickStartHypers();
            } else if (src == baseBtn || src == basesItem && !blockChangeItem) {
                quickStartBase();
            } else if (src == boxBtn || src == boxesItem && !blockChangeItem) {
                quickStartBoxes();
            } else if (src == schedBtn || src == schedItem && !blockChangeItem) {
                quickStartScheduler();
            } else if (src == xmlBtn || src == funcItem && !blockChangeItem) {
                quickStartXmlFrame();
            } else if (src == procBtn || src == procItem && !blockChangeItem) {
                quickStartProcFrame();
            } else if (src == changeMonBtn || src == changeMonItem && !blockChangeItem) {
                quickStartChangeMonFrame();
            } else if (src == activeUsersBtn || src == activeUsersItem && !blockChangeItem) {
                quickStartActivUsersFrame();
            } else if (src == replBtn || src == replItem && !blockChangeItem) {
                quickStartReplFrame();
            } else if (src == terminalBtn || src == terminalItem && !blockChangeItem) {
                quickStartTerminal();
            } else if (src == chatItem) {
                if (getChatMode())
                    quickStartChat();
                else
                    quickStartEmptyChat();
            } else if (src == configBtn || src == configItem && !blockChangeItem) {
                quickStartConfig();
            } else if (src == serviceControlBtn || src == serviceControlItem && !blockChangeItem) {
            //    initControl();
                quickStartServiceControl();
            } else if (src == recycleBtn || src == recycleItem && !blockChangeItem) {
            //    initControl();
                quickStartRecycle();
            } else if (src == rightsBtn || src == rightsItem && !blockChangeItem) {
                quickStartUserRights();
            } else if (src == configsBtn || src == configsItem && !blockChangeItem) {
                quickStartConfigurations();
            }
            controlAreaPanel.setVisible(src == serviceControlBtn || src == serviceControlItem && !blockChangeItem);

            if (src instanceof FunctionToolButton) {
                blockChangeItem = false;
            }
            Or3Frame.instance().setState(0);
            CursorToolkit.stopWaitCursor(this);
        }
        try {
            if (getChatMode()) {
                if (chatItem.isSelected()) {
                    if (newMessageTest.isRunning())
                        newMessageTest.stop();
                    
                    newMessages.setForeground(getLightSysColor());
                    newMessages.setIcon(getImageIcon("NoMessage"));
                } else if (!newMessageTest.isRunning()) {
                    newMessageTest.start();
                }
                chatFrame.setTimerCanWork(chatItem.isSelected());
            }
        } catch (Exception ex) {
        }
    }

    // Переопределение стандартных свойств компонентов
    private static void initUIManagerProps() {
        List<Serializable> buttonGradient = Arrays.asList(.3f, 0f, getLightSysColor(), Color.white, getSysColor());
        Object colorBtn = UIManager.get("Button.background");
        List<Object> menuGradient = Arrays.asList(.3f, 0f, colorBtn, colorBtn, colorBtn);

        UIManager.put("ToggleButton.gradient", buttonGradient);

        UIManager.put("Tree.expandedIcon", getImageIcon("OpenTreeIcon"));
        UIManager.put("Tree.collapsedIcon", getImageIcon("CloseTreeIcon"));
        UIManager.put("Tree.selectionBackground", getSysColor());

        UIManager.put("SplitPane.dividerSize", 3);

        UIManager.put("ScrollBar.thumb", getSysColor());
        UIManager.put("ScrollBar.thumbHighlight", getLightSysColor());
        UIManager.put("ScrollBar.thumbShadow", getDarkShadowSysColor());
        UIManager.put("ScrollBar.gradient", buttonGradient);

        UIManager.put("MenuBar.background", getDarkShadowSysColor());
        UIManager.put("MenuBar.gradient", menuGradient);

        UIManager.put("Menu.selectionBackground", getLightSysColor());
        UIManager.put("Menu.background", getDarkShadowSysColor());
        UIManager.put("Menu.foreground", getLightSysColor());

        UIManager.put("MenuItem.selectionBackground", getSysColor());
        UIManager.put("CheckBoxMenuItem.selectionBackground", getLightSysColor());

        UIManager.put("ToolTip.background", getLightSysColor());
        UIManager.put("ToolTip.font", getDefaultFont());

        UIManager.put("List.selectionBackground", getSysColor());

        UIManager.put("Table.selectionBackground", getSysColor());

        UIManager.put("Separator.foreground", getDarkShadowSysColor());

        UIManager.put("ProgressBar.background", getLightSysColor());
        UIManager.put("ProgressBar.foreground", getDarkShadowSysColor());
        UIManager.put("ProgressBar.cellLength", 6);
        UIManager.put("ProgressBar.cellSpacing", 2);
        UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(getDarkShadowSysColor()));

        UIManager.put("RadioButton.font", getDefaultFont());
        UIManager.put("RadioButton.foreground", getDarkShadowSysColor());

        UIManager.put("Slider.horizontalThumbIcon", getImageIcon("Slider"));
        UIManager.put("Slider.verticalThumbIcon", getImageIcon("VSlider"));

        UIManager.put("FileView.hardDriveIcon", getImageIcon("Drive"));
        UIManager.put("FileView.floppyDriveIcon", getImageIcon("Save"));
        UIManager.put("FileView.directoryIcon", getImageIcon("NewFolder"));
        UIManager.put("FileView.computerIcon", getImageIcon("Comp"));
        UIManager.put("FileView.fileIcon", getImageIcon("Create"));

        UIManager.put("FileChooser.upFolderIcon", getImageIcon("UpFolder"));
        UIManager.put("FileChooser.homeFolderIcon", getImageIcon("Home"));
        UIManager.put("FileChooser.newFolderIcon", getImageIcon("NewFolder"));
        UIManager.put("FileChooser.listViewIcon", getImageIcon("ListView"));
        UIManager.put("FileChooser.detailsViewIcon", getImageIcon("DetailsView"));
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть директорию");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("TextField.inactiveForeground", getDarkShadowSysColor());
        UIManager.put("FileChooserUI", "kz.tamur.util.OrFileChooserUI");

        UIManager.put("TextField.inactiveForeground", Color.black);
    }

    public boolean processClose() {
        try {
            if (serviceFrame != null && serviceFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (designerFrame != null && designerFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (userFrame != null && userFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (reportFrame != null && reportFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (hyperFrame != null && hyperFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (baseFrame != null && baseFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (schedFrame != null && schedFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (filtersFrame != null && filtersFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (xmlFrame != null && xmlFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (activeUsersFrame != null && activeUsersFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }

            if (chatFrame != null && chatFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (boxesFrame != null && boxesFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (searchFrame != null && searchFrame.processExit() == BUTTON_CANCEL) {
                return false;
            }
            if (configEditor != null && configEditor.processExit() == BUTTON_CANCEL) {
                return false;
            }
            krn.getUser().saveHistories();
            Kernel.instance().release();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    protected void exit() {
    	super.processWindowEvent(new WindowEvent(Or3Frame.instance(), WindowEvent.WINDOW_CLOSING));
    }
    
    protected void processWindowEvent(WindowEvent e) {
    	boolean res = true;
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        	// Сохранение кода, введенного в терминале
        	if (terminalFrame != null) {
        		String expression = terminalFrame.getExpression();
        		if (expression.length() > 0) {
    				if (!saveExpressionToFile(expression, "Сохранить введенный код в терминале в файл?")) {
    					return;
    				}
        		}
        	}
        	// Сохранение кода, введенного в редакторах EditorWindow
        	Collection<TabObj> tabs = EditorWindow.TabWnd.getTabs();
        	int i = 1;
        	for (TabObj tab : tabs) {
        		ExpressionEditor ex = (ExpressionEditor) tab.comp;
        		if (ex.onChanged) {
        			String expression = ex.getExpression();
        			if (!ex.text.equals(expression) && expression.length() > 0) {
        				if (!saveExpressionToFile(expression, "Сохранить введенный код во вкладке \"" + tab.Title + "\" в файл?")) {
        					return;
        				}
        			}
        		}
        		i++;
			}
            res = processClose();
        }
        if (res)
        	super.processWindowEvent(e);
    }
    
    private boolean saveExpressionToFile(String expression, String message) {
        int result = MessagesFactory.showMessageDialog(or3Frame, MessagesFactory.CONFIRM_MESSAGE, message);
        if (result == ButtonsFactory.BUTTON_CANCEL || result == ButtonsFactory.BUTTON_NOACTION) {
        	return false;
        } else if (result == ButtonsFactory.BUTTON_YES) {
        	JFileChooser fileChooser = new JFileChooser() {
        		@Override
        	    public void approveSelection(){
        	        File selectedFile = getSelectedFile();
					if (selectedFile.exists() && getDialogType() == SAVE_DIALOG) {
	                    int result = MessagesFactory.showMessageDialog(or3Frame, MessagesFactory.QUESTION_MESSAGE, "Файл уже существует. Заменить?");
        	            switch(result){
        	                case ButtonsFactory.BUTTON_YES:
        	                    super.approveSelection();
        	                    return;
        	                case ButtonsFactory.BUTTON_NO:
        	                    return;
        	                case ButtonsFactory.BUTTON_NOACTION:
        	                    return;
        	            }
        	        }
        	        super.approveSelection();
        	    } 
        	};
        	fileChooser.setDialogTitle("Сохранение кода в файл");
        	fileChooser.setSelectedFile(new File("code.txt"));
        	int userSelection = fileChooser.showSaveDialog(or3Frame);
        	if (userSelection == JFileChooser.APPROVE_OPTION) {
        	    String path = fileChooser.getSelectedFile().getAbsolutePath();
        	    if (!path.endsWith(".txt")) {
        	    	path = path + ".txt";
        	    }
				try {
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
					out.write(expression);
					out.close();
				} catch (IOException eч) {
					eч.printStackTrace();
				}
			} else {
				return false;
			}
        }
        return true;
    }
    
    private static boolean login(String dsName) throws Exception {
    	ClientCallback cb = (ClientCallback) Kernel.instance().getCallback();
    	if (cb != null)
    		cb.setFrame(null);
    	
    	Kernel.instance().release();
    	
    	if (dsName != null)
    		System.setProperty("selSrv", "0");
    	
        Splash splash = new Splash(Splash.DESIGNER);
        splash.setVisible(true);
        LoginBox lbox = new LoginBox(or3Frame, false);
        String name = "";
        String pd = "";
        String keyFilePath = "";
        InetAddress address = InetAddress.getLocalHost();
        Kernel krn = null;
        User user = null;
        boolean sLogin = false;
        while (true) {
            if (!sLogin) {
                splash.setVisible(false);
                lbox.result = -1;
                lbox.setVisible(true);
                if (lbox.result == BUTTON_CANCEL) {
                    return false;
                }
                splash.setVisible(true);
                name = lbox.getUserName();
                pd = lbox.getPassword();
            }
            keyFilePath = lbox.getKeyFilePath();
            isQuickStartShow = lbox.isQuickStartShow();
            if ("1".equals(Funcs.getSystemProperty("selSrv"))) {
                serverType = lbox.getServerType();
                host = lbox.getHost();
                port = lbox.getPort();
                baseName = lbox.getBaseName();
                webUrl = lbox.getWebUrl();
                earName = lbox.getEarName();
            } else if (dsName == null) {
	            serverType = Funcs.getSystemProperty("serverType");
	            host = Funcs.getSystemProperty("host");
	            port = Funcs.getSystemProperty("port");
	            baseName = Funcs.getSystemProperty("dsName");
	            webUrl = Funcs.getSystemProperty("webUrl");
	            earName = Funcs.getSystemProperty("earName");
            } else {
            	baseName = dsName;
            }

            String ip = address.getHostAddress();
            String pcName = address.getHostName();
            try {
                SessionOpsOperations ops = ("Wildfly Cluster".equals(serverType))
                		? lookup(serverType, Funcs.sanitizeSQL(host), Funcs.sanitizeSQL(port), Funcs.sanitizeSQL(earName), true)
                		: lookup(serverType, Funcs.sanitizeSQL(host), Integer.parseInt(port), Funcs.sanitizeSQL(earName), true);
                // Авторизация
                if (keyFilePath != null && !keyFilePath.isEmpty()) {
                    String random = ops.randomString();
                    String sign = KalkanUtil.createPkcs7(keyFilePath, pd, random, false);
                    Kernel.instance().init(sign, null, null, null, host, port, baseName, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, Kernel.LOGIN_KALKAN, ops);
                } else {
                	if (baseName == null || baseName.length() == 0)
                		Kernel.instance().init(name, pd, host, port, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, Kernel.LOGIN_USUAL, ops);
                	else
                		Kernel.instance().init(name, pd, null, null, host, port, baseName, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, Kernel.LOGIN_USUAL, ops, false, sLogin, false, null);
                }
                break;
            } catch (KrnException e) {
                ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
                PathWordChange pdChange;
                switch (e.code) {
                case ErrorCodes.USER_NO_BASE:
                case ErrorCodes.USER_NO_IFC_LANG:
                case ErrorCodes.USER_NO_DATA_LANG:
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString(e.getMessage()), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_HAS_CONNECT:
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("userHasConnected"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_NOT_FOUND:
                    lbox.clearPassword();
                    showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                    break;
                case ErrorCodes.SERVER_NOT_AVAILABLE:
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("serverDisconnect"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_IS_EXPIRED:
                    pdChange = new PathWordChange(baseName, name, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, lbox, lbox.getCurrLang(), res,
                            e.object, ErrorCodes.USER_IS_EXPIRED);
                    pdChange.setVisible(true);
                    sLogin = true;
                    if(pdChange.isChangePass()) {
                    	pd = pdChange.getNewPassword();  
                    }
                    break;
                case ErrorCodes.USER_IS_BLOCKED:
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("userIsBlocked"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_IS_ENDED:
                    pdChange = new PathWordChange(baseName, name, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, lbox, lbox.getCurrLang(), res,
                            e.object, ErrorCodes.USER_IS_ENDED);
                    pdChange.setVisible(true);
                    // если пользователь пароль не изменил
                    if (!pdChange.isChangePass()) {
                        showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                        Kernel.instance().release();
                        return false;
                    }
                    break;
                case ErrorCodes.USER_NOT_LOGIN:
                    pdChange = new PathWordChange(baseName, name, Constants.CLIENT_TYPE_DESIGNER, ip, pcName, lbox, lbox.getCurrLang(), res,
                            e.object, ErrorCodes.USER_NOT_LOGIN);
                    pdChange.setVisible(true);
                    // если пользователь пароль не изменил
                    if (!pdChange.isChangePass()) {
                        showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                        Kernel.instance().release();
                        return false;
                    }
                    lbox.clearPassword();
                    break;
                default:
                    showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage()+"\r\nКод ошибки: "+e.code, lbox.getCurrLang());
                    break;
                }
            }
        }
        
        krn = Kernel.instance();
        user = krn.getUser();
        lbox.setUserData();
        lbox.dispose();
        Or3Frame frm = Or3Frame.instance();
        frm.krn = krn;
        frm.user = user;
        ClientCallback callback = (ClientCallback) krn.getCallback();
        callback.setFrame(frm);
        callback.setImportProgressListener(frm);
        callback.setScriptExecResultListener(frm);
        callback.start();
        frm.setUsername(name);
        setPassword(pd);

        String noRights = Funcs.getSystemProperty("noRights");
        if ("1".equals(noRights)) {
            user.setHasOr3Rights(false);
        }
        frm.applyViewRights();
        splash.setVisible(false);
        splash.dispose();
        
        return true;
    }

    public static void main(String[] args) throws Exception {
        newMessages.setForeground(getLightSysColor());
        newMessages.setBackground(getLightSysColor());
        newMessages.setIcon(getImageIcon("NoMessage"));
        // задать текущий режим работы
        AppState.CURRENT_MODE = Mode.DESIGN;
        String look = "kz.tamur.comps.ui.OrLookAndFeel";
        try {
            UIManager.setLookAndFeel(look);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initUIManagerProps();

        if (login(null)) {
	        Or3Frame frm = Or3Frame.instance();
	        frm.setLocation(Utils.getCenterLocationPoint(frm.getSize()));
	        frm.setVisible(true);
	        if (isQuickStartShow) {
	            DesignerDialog dlg = new DesignerDialog(frm, "Быстрый старт", getQuickStartPanel(frm), false, false, false, true);
	            dlg.setResizable(false);
	            dlg.setOkText("Закрыть");
	            dlg.setOnlyOkButton();
	            dlg.show();
	        }
	
	        if (getChatMode()) {
	            // Проверка новых сообщений каждые 10 секунд
	            newMessageTest = new Timer(10000, new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                	if(Kernel.instance().isAlive()) {
		                    if (getNewMessagesCount() > 0) {
		                        EventQueue.invokeLater(new Runnable() {
									public void run() {
		                                if (newMessages.getForeground() == getLightSysColor()) {
		                                    newMessages.setForeground(Color.RED);
		                                    newMessages.setIcon(getImageIcon("NewMessage"));
		                                } else if (newMessages.getForeground() == Color.RED)
		                                    newMessages.setForeground(Color.BLUE);
		                                else if (newMessages.getForeground() == Color.BLUE)
		                                    newMessages.setForeground(Color.RED);
									}
		                        });
		                    } else {
		                        newMessages.setForeground(getLightSysColor());
		                        newMessages.setIcon(getImageIcon("NoMessage"));
		                    }
	                	} else if(newMessageTest.isRunning())
	            			newMessageTest.stop();
	                }
	            });
	            newMessageTest.start();
	        }
	        
	        if ("true".equals(Funcs.getSystemProperty("isSysDb")))
	        	isSysDb = true;
	        else {
		        Kernel krn = Kernel.instance();
		        KrnObject currDb = krn.getCurrentDb();
		        try {
		            KrnClass currDbCls = krn.getClass(currDb.classId);
			        if("Корень структуры баз".equals(currDbCls.name))
			        	isSysDb = true;
		        } catch (KrnException e) {
		            e.printStackTrace();
		        }
	        }
        } else {
        	Or3Frame.instance().exit();
        }
    }

    public static boolean isSysDb() {
        return isSysDb;
    }

    private static float getNewMessagesCount() {
        float newMessagesCount = 0;
        try {
            Kernel krn = Kernel.instance();
            String toUser = krn.getUser().getName();
            KrnClass msgCls = krn.getClassByName("ChatClass");
            KrnAttribute statusAttr = krn.getAttributeByName(msgCls, "status");
            KrnObject[] massivObjects = krn.getObjectsByAttribute(msgCls.id, statusAttr.id, 0, ComparisonOperations.CO_EQUALS, "New", 0);
            long[] massivObjectsId = new long[massivObjects.length];
            for (int j = 0; j < massivObjects.length; j++)
                massivObjectsId[j] = massivObjects[j].id;
            
            StringValue[] massivTo = krn.getStringValues(massivObjectsId, msgCls.id, "to", 0, false, 0);
            for (int i = 0; i < massivObjects.length; i++)
                if (massivTo[i].value.equals(toUser))
                	newMessagesCount++;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newMessagesCount;
    }

    private static boolean getChatMode() {
        try {
            Kernel krn = Kernel.instance();
            if (krn.checkExistenceClassByName("ChatClass")) {
                int counter = 0;
                KrnClass chatClass = krn.getClassByName("ChatClass");
                String[] needAttributes = { "canDeleteFrom", "canDeleteTo", "datetime", "from", "status", "text", "to" };
                List<KrnAttribute> chatAttributes = krn.getAttributes(chatClass);
                for (int i = 0; i < needAttributes.length; i++) {
                    for (int j = 0; j < chatAttributes.size(); j++) {
                        if (needAttributes[i].equals(chatAttributes.get(j).name)) {
                            counter++;
                        }
                    }
                }
                if (counter == 7) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAbout() {
        ImageIcon icon = getImageIconJpg("splash");
        final JDialog wnd = new AboutDialog(this, icon);
        wnd.setLocation(Utils.getCenterLocationPoint(wnd.getSize()));
        wnd.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == aboutItem) {
            showAbout();
        } else if (src == closeItem) {
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (src == quickStartItem) {
            DesignerDialog dlg = new DesignerDialog(this, "Быстрый старт", getQuickStartPanel(this), false, false, false, true);
            dlg.setResizable(false);
            dlg.setOkText("Закрыть");
            dlg.setOnlyOkButton();
            dlg.show();
        } else if (src == closeFuncItem) {
            closeAllFunc();
        } else if (src == langRegItem) {
            LangRegPanel tp = new LangRegPanel();
            DesignerDialog dlg = new DesignerDialog(this, "Регистрация языков", tp);
            dlg.show();
            if (dlg.getResult() == BUTTON_OK) {
                tp.save();
            }
        }
    }

    public void closeAllFunc() {
        emptyItem.setSelected(true);
        markBtn(emptyBtn);
        setJMenuBar(null);
        setTitle(appTitle);
        layout.show(workAreaPanel, "emptyPanel");
    }

    public void setUsername(String username_) {
        ReportWrapper.username = username_;
        if (user != null && !user.isDeveloper()) {
            getQuickStartPanel(this).disableDeveloperButtons();
            serviceItem.setEnabled(false);
            serviceBtn.setEnabled(false);
            classesItem.setEnabled(false);
            classesBtn.setEnabled(false);
            interfacesItem.setEnabled(false);
            ifcBtn.setEnabled(false);
            filtersBtn.setEnabled(false);
            filtersItem.setEnabled(false);
            reportsItem.setEnabled(false);
            reportsBtn.setEnabled(false);
            basesItem.setEnabled(false);
            baseBtn.setEnabled(false);
            hyperBtn.setEnabled(false);
            hypersItem.setEnabled(false);
            xmlBtn.setEnabled(false);
            funcItem.setEnabled(false);
            procItem.setEnabled(false);
            changeMonItem.setEnabled(false);
            serviceControlBtn.setEnabled(false);
            serviceControlItem.setEnabled(false);
            recycleBtn.setEnabled(false);
            recycleItem.setEnabled(false);
        }
    }

    public void applyViewRights() {
        getQuickStartPanel(this).applyViewRights(user);
        boolean res = user.hasRight(Or3RightsNode.PROCESS_VIEW_RIGHT);
        serviceItem.setEnabled(res);
        serviceBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.CLASSES_VIEW_RIGHT);
        classesItem.setEnabled(res);
        classesBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.INTERFACE_VIEW_RIGHT);
        interfacesItem.setEnabled(res);
        ifcBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.BASES_VIEW_RIGHT);
        basesItem.setEnabled(res);
        baseBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.BOXES_VIEW_RIGHT);
        boxesItem.setEnabled(res);
        boxBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.FUNCS_VIEW_RIGHT);
        funcItem.setEnabled(res);
        xmlBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.PROCS_VIEW_RIGHT);
        procItem.setEnabled(res);
        procBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.VCS_CHANGE_VIEW_RIGHT);
        changeMonItem.setEnabled(res);
        changeMonBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.FILTERS_VIEW_RIGHT);
        filtersBtn.setEnabled(res);
        filtersItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.USERS_VIEW_RIGHT);
        usersItem.setEnabled(res);
        usersBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.SEARCH_MAKE_RIGHT);
        searchItem.setEnabled(res);
        searchBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.REPORTS_VIEW_RIGHT);
        reportsItem.setEnabled(res);
        reportsBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.MENU_VIEW_RIGHT);
        hyperBtn.setEnabled(res);
        hypersItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.TASKS_VIEW_RIGHT);
        schedBtn.setEnabled(res);
        schedItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.USER_RIGHT_VIEW_RIGHT);
        rightsBtn.setEnabled(res);
        rightsItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.REPLICATION_VIEW_RIGHT);
        replBtn.setEnabled(res);
        replItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.TERMINAL_VIEW_RIGHT);
        terminalBtn.setEnabled(res);
        terminalItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.CONFIG_VIEW_RIGHT);
        configBtn.setEnabled(res);
        configItem.setEnabled(res);

        res = user.hasRight(Or3RightsNode.SERVICE_CONTROL_VIEW_RIGHT);
        serviceControlBtn.setEnabled(res);
        serviceControlItem.setEnabled(res);
        recycleBtn.setEnabled(res);
        recycleItem.setEnabled(res);
    }

    public static QuickStartPanel getQuickStartPanel(Or3Frame frm) {
        if (quickStart == null) {
            quickStart = new QuickStartPanel(frm);
        }
        return quickStart;
    }

    public static void setPassword(String pd) {
        ReportWrapper.userpd = pd;
    }

    public static String getServerType() {
        return "Сервер: " + host + ":" + port + " (" + serverType + ")";
    }

    public static String getUrl() {
        return "Сервер: " + host + ":" + port;
    }

    public static String getWebUrl() {
        return webUrl;
    }

    public static String getBaseName() {
        return "Конфигурация: " + ((baseName != null && baseName.length() > 0) ? baseName : "НЕ ВЫБРАНА");
    }

    public static String getCurrentDbName() {
        Kernel krn = Kernel.instance();
        KrnObject db = krn.getCurrentDb();
        String name = "";
        if (db != null) {
	        try {
	            name = krn.getStringsSingular(db.id, krn.getAttributeByName(krn.getClassByName("Структура баз"), "наименование").id,
	                    0, false, false);
	        } catch (KrnException e) {
	            e.printStackTrace();
	        }
        }
        return "БД: " + name;
    }
    
    public static String getCurrentUserName() {
    	Kernel krn = Kernel.instance();
        return "Пользователь: " + krn.getUser().getName();
    }

    public static void setUrl(String url) {
        port = url;
    }

    public void setVisible(boolean visible) {
        if (visible) {
        	serverLabel.setText(getServerType());
            dsLabel.setText(getBaseName());
            currentDbName.setText(getCurrentDbName());
            currentUserLable.setText(getCurrentUserName());
        }
        super.setVisible(visible);
    }

    public void setWorkStatusLabelText(String text) {
        workStatusLabel.setText(text);
    }

    public void setWorkStatusLabelIcon(String iconName) {
        workStatusLabel.setIcon(getImageIcon(iconName));
    }

    public void fullScreenIfc() {
        FullScreenFrame f = FullScreenFrame.instance();
        f.addPanel(DesignerFrame.instance());
        f.setVisible(true);
    }

    class WindowFocusAdapter implements WindowFocusListener {
        private JWindow wnd;

        public WindowFocusAdapter(JWindow wnd) {
            this.wnd = wnd;
        }

        public void windowGainedFocus(WindowEvent e) {
        }

        public void windowLostFocus(WindowEvent e) {
            if (wnd != null && wnd.isShowing())
                wnd.dispose();
            wnd = null;
        }
    }

    public static SessionOpsOperations lookup(String serverType, String host, int port, String earName, boolean remote) throws KrnException {
        try {
        	if (Funcs.isValid(host) && host.length() > 0 && host.length() < 30) {
	            if ("JBossAS7".equals(serverType)) {
	                return new JBoss7Authenticator().authenticate(host, port, earName, remote);
	            } else if ("Wildfly".equals(serverType)) {
	            	return new WildflyAuthenticator().authenticate(host, port, earName, remote);
	            } else if ("Wildfly 14+".equals(serverType)) {
	            	return new WildflyAuthenticator().authenticate(host, port, earName, remote);
	            	//return new Wildfly14Authenticator().authenticate(host, port, earName, remote);
	            } else if ("Weblogic".equals(serverType)) {
	                return new WeblogicAuthenticator().authenticate(host, port, earName, remote);
	            } else if ("JBossEAP".equals(serverType)) {
	                return new JBossEAPAuthenticator().authenticate(host, port, earName, remote);
/*	            } else if ("JBossServer".equals(serverType)) {
	                Properties props = new Properties();
	                props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
	                props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
	                props.put("java.naming.provider.url", host + ":" + port);
	
	                Context ic = new InitialContext(props);
	                Object obj = ic.lookup("Or3EAR/SessionOps/" + (remote ? "remote" : "local"));
	                ic.close();
	                return (SessionOpsOperations) obj;
*/	            }
        	}
        } catch (NamingException e) {
            e.printStackTrace();
            String msg = "Сервер не доступен";
            throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, msg);
        }
        throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, "Не определен тип сервера");
    }

    public static SessionOpsOperations lookup(String serverType, String hosts, String ports, String earName, boolean remote) throws KrnException {
        try {
        	if (Funcs.isValid(host) && host.length() > 0 && host.length() < 30) {
	            if ("Wildfly Cluster".equals(serverType)) {
	            	WildflyClusterAuthenticator auth = new WildflyClusterAuthenticator();
	            	SessionOpsOperations ops = auth.authenticate(hosts, ports, earName, remote);
	            	
	            	host = auth.getConnectedHost();
	            	port = auth.getConnectedPort();
	            	
	            	return ops;
	            }
        	}
        } catch (NamingException e) {
            e.printStackTrace();
            String msg = "Сервер не доступен";
            throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, msg);
        }
        throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, "Не определен тип сервера");
    }

    /**
     * @return the apptitle
     */
    public static String getApptitle() {
        return appTitle;
    }

    /**
     * @return the classFrame
     */
    public Classes getClassFrame() {
        return classFrame;
    }

    /**
     * @return the serviceFrame
     */
    public MainFrame getServiceFrame() {
        return serviceFrame;
    }
    /**
     * @return the designerFrame
     */
    public DesignerFrame getDesignerFrame() {
        return designerFrame;
    }

    /**
     * @return the filtersFrame
     */
    public FiltersPanel getFiltersFrame() {
        return filtersFrame;
    }

    /**
     * @return the reportFrame
     */
    public ReportPanel getReportFrame() {
        return reportFrame;
    }

    public void markBtn(FunctionToolButton btn) {
        if (previousBtn != null) {
            previousBtn.setMarked(false);
        }
        btn.setSelected(true);
        btn.setMarked(true);
        previousBtn = btn;
    }
    
    @Override
    public void setState(int state) {
        switch (state) {
        case STATE_DEF:
            setState("");
            break;
        case STATE_FIND:
            setState("Производится поиск...  Для отмены нажмите Esc.");
            break;
        }
    }

    @Override
    public void setState(String state) {
        stateFrame.setText(state);
    }

	public void connect(final String dsName) {
		try {
			login(dsName);
        	serverLabel.setText(getServerType());
            dsLabel.setText(getBaseName());
            currentDbName.setText(getCurrentDbName());
            currentUserLable.setText(getCurrentUserName());
            
            if (serviceFrame != null)
            	serviceFrame.updateStatusBar();
            if (reportFrame != null)
            	reportFrame.updateStatusBar();
            if (classFrame != null)
            	classFrame.updateStatusBar();
            if (hyperFrame != null)
            	hyperFrame.updateStatusBar();
            if (designerFrame != null)
            	designerFrame.updateStatusBar();
            if (filtersFrame != null)
            	filtersFrame.updateStatusBar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void replFilesProgress(int type, int filesCount, int currentFileNumber, String currentFileName, Time importTime) {
		if (replFrame != null) {
			replFrame.replFilesProgress(type, filesCount, currentFileNumber, currentFileName, importTime);
		}
	}

	@Override
	public void replChangesProgress(int type, int currentChangeNumber, int changesCount, String changeType, String changeId) {
		if (replFrame != null) {
			replFrame.replChangesProgress(type, currentChangeNumber, changesCount, changeType, changeId);
		}
	}

	@Override
	public void scriptExecResult(int resultCode, Map<String, Object> varsMap, String message) {
		if (terminalFrame != null) {
			terminalFrame.scriptExecResult(resultCode, varsMap, message);
		}
	}
}