package kz.tamur.guidesigner.scheduler;

import static kz.tamur.comps.Utils.createDesignerToolBar;
import static kz.tamur.comps.Utils.getCenterLocationPoint;
import static kz.tamur.comps.Utils.getUserTree;
import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserNode;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.ProcessNode;
import kz.tamur.rt.ProcessUserComponent;
import kz.tamur.rt.Utils;
import kz.tamur.util.SchedulerListCellRenderer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import other.treetable.JTreeTable;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 27.04.2005
 * Time: 17:17:15
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerPane extends JPanel implements ActionListener, ListSelectionListener {
    private static DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private JPanel taskPane = new JPanel(new BorderLayout());
    private JTreeTable treeTable = new JTreeTable();
    private JTree tree;
    private JScrollPane tablePane;
    private JPanel itemPane = new JPanel(new BorderLayout());
    private JSplitPane basicSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JToolBar toolBar = createDesignerToolBar();
    private JToolBar toolBarItem = createDesignerToolBar();
    private JButton newBtn = ButtonsFactory.createToolButton("Shed", "Создать новое задание");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton delBtn = ButtonsFactory.createToolButton("ShedDel", "Удалить задание");
    private JButton execBtn = ButtonsFactory.createToolButton("ShedExec", "Выполнить задание");
    private JButton userBtn = ButtonsFactory.createToolButton("userNode", "Выберите пользователя");
    private JButton procesBtn = ButtonsFactory.createToolButton("ServiceTab", "Выберите процесс");
    private JButton protocolBtn = ButtonsFactory.createToolButton("ViewLog", "Просмотреть протокол");
    private ButtonsFactory.DesignerCompButton statusBtn = ButtonsFactory.createCompButton("Статус выполнения заданий", Utils.getImageIcon("Status"));

    private Kernel krn = Kernel.instance();
    boolean schedulerStatus = krn.getActivateScheduler();
    private JCheckBox schedulerActivator = Utils.createCheckBox(schedulerStatus ? "Деактивировать планировщик" : "Активировать планировщик", schedulerStatus);

    private JTextArea protocolList = new JTextArea();
    private JPanel comandPane = new JPanel();
    private JPanel exePane = new JPanel(new GridBagLayout());
    private JRadioButton exeYesBtn = new JRadioButton("Да");
    private JRadioButton exeNowBtn = new JRadioButton("При старте");
    private JRadioButton exeToDayBtn = new JRadioButton("В день старта");
    private JRadioButton exeNoBtn = new JRadioButton("Нет");
    private ButtonGroup groupExe = new ButtonGroup();
    private JLabel userLabel = Utils.createLabel("Пользователь");
    private JLabel nameLabel = Utils.createLabel("Наименование");
    private JLabel procLabel = Utils.createLabel("Процесс");
    private JTextField userText = Utils.createDesignerTextField();
    private JTextField nameText = Utils.createDesignerTextField();
    private JTextField procText = Utils.createDesignerTextField();
    private JPanel plainPane = new JPanel();
    private JLabel plainLabel = Utils.createLabel("  Время запуска");
    private JPanel schedulerPane = new JPanel();
    private JPanel minutPane = new JPanel();
    private JPanel hourPane = new JPanel();
    private JPanel daymPane = new JPanel();
    private JPanel daywPane = new JPanel();
    private JPanel monthPane = new JPanel();
    private JRadioButton minutAllBtn = new JRadioButton("Все");
    private JRadioButton minutChckBtn = new JRadioButton("Выбор");
    private JPanel radioMinutPane = new JPanel();
    private ButtonGroup groupMinut = new ButtonGroup();
    private JRadioButton hourAllBtn = new JRadioButton("Все");
    private JRadioButton hourChckBtn = new JRadioButton("Выбор");
    private JPanel radioHourPane = new JPanel();
    private ButtonGroup groupHour = new ButtonGroup();
    private JRadioButton daymAllBtn = new JRadioButton("Все");
    private JRadioButton daymChckBtn = new JRadioButton("Выбор");
    private JPanel radioDaymPane = new JPanel();
    private ButtonGroup groupDaym = new ButtonGroup();
    private JRadioButton daywAllBtn = new JRadioButton("");
    private JRadioButton daywChckBtn = new JRadioButton("Выбор");
    private JPanel radioDaywPane = new JPanel();
    private ButtonGroup groupDayw = new ButtonGroup();
    private JRadioButton monthAllBtn = new JRadioButton("Все");
    private JRadioButton monthChckBtn = new JRadioButton("Выбор");
    private JPanel radioMonthPane = new JPanel();
    private ButtonGroup groupMonth = new ButtonGroup();
    private JPanel minutListPane = new JPanel();
    private JList minut = new JList(new Object[]{"0 ", "1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 ", "8 ", "9 ", "10 ", "11 ", "12 ", "13 ", "14 ",
            "15 ", "16 ", "17 ", "18 ", "19 ", "20 ", "21 ", "22 ", "23 ", "24 ", "25 ", "26 ", "27 ", "28 ", "29 ",
            "30 ", "31 ", "32 ", "33 ", "34 ", "35 ", "36 ", "37 ", "38 ", "39 ", "40 ", "41 ", "42 ", "43 ", "44 ",
            "45 ", "46 ", "47 ", "48 ", "49 ", "50 ", "51 ", "52 ", "53 ", "54 ", "55 ", "56 ", "57 ", "58 ", "59 "});
    private JPanel hourListPane = new JPanel();
    private JList hour = new JList(new Object[]{"0 ", "1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 ", "8 ", "9 ", "10 ", "11 ",
            "12 ", "13 ", "14 ", "15 ", "16 ", "17 ", "18 ", "19 ", "20 ", "21 ", "22 ", "23 "});
    private JPanel daymListPane = new JPanel();
    private JList daym = new JList(new Object[]{"1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 ", "8 ", "9 ", "10 ", "11 ", "12 ", "13 ", "14 ", "15 ",
            "16 ", "17 ", "18 ", "19 ", "20 ", "21 ", "22 ", "23 ", "24 ", "25 ", "26 ", "27 ", "28 ", "29 ", "30 ", "31 "});
    private JPanel monthListPane = new JPanel();
    private JList month = new JList(new Object[]{"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль"
            , "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"});
    private JPanel daywListPane = new JPanel();
    private JList dayw = new JList(new Object[]{"Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"});
    //    private String[] headerList=new String[]{"Наименование задания","Статус выполнения","Пользователь","Процессы"};
    private HashMap<Object, SchedObject> srvsMap = new HashMap<Object, SchedObject>();
    private HashMap<Object, SchedObject> usersMap = new HashMap<Object, SchedObject>();
    private HashMap<Long, TimerObject> timers = new HashMap<Long, TimerObject>();
    private TimerModel tm;
    private KrnClass cls_folder;
    private KrnClass cls_protocol;
    private KrnAttribute attr_child;
    private KrnObject filter_protocol;
    private KrnAttribute attr_protocol_timer;
    private KrnAttribute attr_protocol_start;
    private KrnAttribute attr_protocol_finish;
    private KrnAttribute attr_protocol_next;
    private KrnAttribute attr_protocol_err;
    private KrnAttribute attr_protocol_status;
    private String cls_p_tname;
    private String attr_p_start_tname;
    private String attr_p_finish_tname;
    private String attr_p_status_tname;
    private String attr_p_timer_tname;
    private String attr_p_err_tname;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private Thread statusThread=null;
    private long threadSleep=15000;
    private int countUpdates=50;
    private boolean isUpdateStatus=true;
    public SchedulerPane() {
        super(new BorderLayout());
        load();
        init();
        setBlock(schedulerStatus);
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.TASKS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.TASKS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.TASKS_CREATE_RIGHT);

        Color color = this.getBackground();
        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBar.add(newBtn);
        toolBar.add(delBtn);
        toolBar.add(execBtn);
        toolBar.add(statusBtn);
        
        if (System.getProperty("showActPanel", "0").equals("1")) {
            toolBar.add(schedulerActivator);
        }
		schedulerActivator.setToolTipText(schedulerActivator.isSelected() ? "Планировщик активирован" : "Планировщик деактивирован");
        schedulerActivator.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				schedulerActivator.setToolTipText(schedulerActivator.isSelected() ? "Планировщик активирован" : "Планировщик деактивирован");
				schedulerActivator.setText(schedulerActivator.isSelected() ? "Деактивировать планировщик" : "Активировать планировщик");
	            if (schedulerActivator.isSelected()) {
	                krn.setActivateScheduler(true);
	                setBlock(true);
	            } else {
	                krn.setActivateScheduler(false);
	                setBlock(false);
	            }
			}
		});

        toolBarItem.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBarItem.add(saveBtn);
        toolBarItem.add(userBtn);
        toolBarItem.add(procesBtn);
        toolBarItem.add(protocolBtn);
        taskPane.add(tablePane, BorderLayout.CENTER);
        basicSplit.setLeftComponent(taskPane);
        basicSplit.setRightComponent(itemPane);
        taskPane.add(toolBar, BorderLayout.NORTH);
        itemPane.add(toolBarItem, BorderLayout.NORTH);
        add(basicSplit, BorderLayout.CENTER);
        //verticalLayout.setVgap(0);
        //verticalLayout.setHgap(0);
        //verticalLayout.setAlignment(0);
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        //comandPane.setBorder(BorderFactory.createLoweredBevelBorder());
        //plainPane.setBorder(BorderFactory.createLoweredBevelBorder());
        minutPane.setBorder(Utils.createTitledBorder(b, "Минуты"));
        hourPane.setBorder(Utils.createTitledBorder(b, "Часы"));
        daymPane.setBorder(Utils.createTitledBorder(b, "Дни месяца"));
        daywPane.setBorder(Utils.createTitledBorder(b, "Дни недели"));
        monthPane.setBorder(Utils.createTitledBorder(b, "Месяцы"));
        comandPane.setLayout(new BoxLayout(comandPane, BoxLayout.Y_AXIS));

        userText.setPreferredSize(new Dimension(150, 20));
        userText.setEditable(false);
        procText.setPreferredSize(new Dimension(300, 20));
        procText.setEditable(false);
        nameText.setPreferredSize(new Dimension(450, 20));

        groupExe.add(exeYesBtn);
        groupExe.add(exeNowBtn);
        groupExe.add(exeToDayBtn);
        groupExe.add(exeNoBtn);
        exePane.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 5), 0, 0));
        exePane.add(nameText, new GridBagConstraints(1, 0, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
        exePane.add(userLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 20, 5), 0, 0));
        exePane.add(userText, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 20, 0), 0, 0));
        exePane.add(procLabel, new GridBagConstraints(2, 1, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 10, 20, 5), 0, 0));
        exePane.add(procText, new GridBagConstraints(3, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 20, 0), 0, 0));
        JPanel p = new JPanel();
        p.add(exeYesBtn);
        p.add(exeNowBtn);
        p.add(exeToDayBtn);
        p.add(exeNoBtn);
        p.setBorder(Utils.createTitledBorder(b, "Выполнять"));
        exePane.add(p, new GridBagConstraints(4, 0, 1, 2, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(0, 30, 20, 0), 0, 0));
        comandPane.add(exePane);
        plainPane.setLayout(new BoxLayout(plainPane, BoxLayout.Y_AXIS));
        plainPane.add(plainLabel);
        schedulerPane.setLayout(new GridLayout(1, 5, 0, 0));
        minut.setLayoutOrientation(JList.VERTICAL_WRAP);
        minut.setVisibleRowCount(11);
        minut.setCellRenderer(new SchedulerListCellRenderer());
        minut.setFixedCellHeight(20);
        minut.setFixedCellWidth(20);
        hour.setLayoutOrientation(JList.VERTICAL_WRAP);
        hour.setVisibleRowCount(11);
        hour.setCellRenderer(new SchedulerListCellRenderer());
        hour.setFixedCellHeight(20);
        hour.setFixedCellWidth(20);
        daym.setLayoutOrientation(JList.VERTICAL_WRAP);
        daym.setVisibleRowCount(11);
        daym.setCellRenderer(new SchedulerListCellRenderer());
        daym.setFixedCellHeight(20);
        daym.setFixedCellWidth(20);
        minutPane.setLayout(new BoxLayout(minutPane, BoxLayout.Y_AXIS));
        minutPane.add(radioMinutPane);
        minutPane.add(minutListPane);
        minutListPane.add(minut);
        minut.setBackground(color);
        radioMinutPane.setLayout(new BoxLayout(radioMinutPane, BoxLayout.Y_AXIS));
        radioMinutPane.add(minutAllBtn);
        radioMinutPane.add(minutChckBtn);
        groupMinut.add(minutAllBtn);
        groupMinut.add(minutChckBtn);
        schedulerPane.add(minutPane);
        hourPane.setLayout(new BoxLayout(hourPane, BoxLayout.Y_AXIS));
        hourPane.add(radioHourPane);
        hourPane.add(hourListPane);
        hourListPane.add(hour);
        hour.setBackground(color);
        radioHourPane.setLayout(new BoxLayout(radioHourPane, BoxLayout.Y_AXIS));
        radioHourPane.add(hourAllBtn);
        radioHourPane.add(hourChckBtn);
        groupHour.add(hourAllBtn);
        groupHour.add(hourChckBtn);
        schedulerPane.add(hourPane);
        daymPane.setLayout(new BoxLayout(daymPane, BoxLayout.Y_AXIS));
        daymPane.add(radioDaymPane);
        daymPane.add(daymListPane);
        daymListPane.add(daym);
        daym.setBackground(color);
        radioDaymPane.setLayout(new BoxLayout(radioDaymPane, BoxLayout.Y_AXIS));
        radioDaymPane.add(daymAllBtn);
        radioDaymPane.add(daymChckBtn);
        groupDaym.add(daymAllBtn);
        groupDaym.add(daymChckBtn);
        schedulerPane.add(daymPane);
        monthPane.setLayout(new BoxLayout(monthPane, BoxLayout.Y_AXIS));
        monthPane.add(radioMonthPane);
        monthPane.add(monthListPane);
        monthListPane.add(month);
        month.setBackground(color);
        month.setPreferredSize(new Dimension(150, 220));
        month.setCellRenderer(new SchedulerListCellRenderer());
        month.setFixedCellHeight(18);
        radioMonthPane.setLayout(new BoxLayout(radioMonthPane, BoxLayout.Y_AXIS));
        radioMonthPane.add(monthAllBtn);
        radioMonthPane.add(monthChckBtn);
        groupMonth.add(monthAllBtn);
        groupMonth.add(monthChckBtn);
        schedulerPane.add(monthPane);
        daywPane.setLayout(new BoxLayout(daywPane, BoxLayout.Y_AXIS));
        daywPane.add(radioDaywPane);
        daywPane.add(daywListPane);
        dayw.setBackground(color);
        dayw.setPreferredSize(new Dimension(150, 200));
        dayw.setCellRenderer(new SchedulerListCellRenderer());
        dayw.setFixedCellHeight(20);
        radioDaywPane.setLayout(new BoxLayout(radioDaywPane, BoxLayout.Y_AXIS));
        radioDaywPane.add(daywAllBtn);
        radioDaywPane.add(daywChckBtn);
        groupDayw.add(daywAllBtn);
        groupDayw.add(daywChckBtn);
        daywListPane.add(dayw);
        schedulerPane.add(daywPane);
        plainPane.add(schedulerPane);
        comandPane.add(plainPane);
        JScrollPane schedPane = new JScrollPane(comandPane);
        itemPane.add(schedPane, BorderLayout.CENTER);
        minut.addListSelectionListener(this);
        hour.addListSelectionListener(this);
        daym.addListSelectionListener(this);
        month.addListSelectionListener(this);
        dayw.addListSelectionListener(this);
        nameText.addActionListener(this);
        exeYesBtn.addActionListener(this);
        exeNowBtn.addActionListener(this);
        exeToDayBtn.addActionListener(this);
        exeNoBtn.addActionListener(this);
        minutAllBtn.addActionListener(this);
        minutChckBtn.addActionListener(this);
        hourAllBtn.addActionListener(this);
        hourChckBtn.addActionListener(this);
        daymAllBtn.addActionListener(this);
        daymChckBtn.addActionListener(this);
        monthAllBtn.addActionListener(this);
        monthChckBtn.addActionListener(this);
        daywAllBtn.addActionListener(this);
        daywChckBtn.addActionListener(this);
        newBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        delBtn.addActionListener(this);
        execBtn.addActionListener(this);
        statusBtn.addActionListener(this);
        userBtn.addActionListener(this);
        procesBtn.addActionListener(this);
        protocolBtn.addActionListener(this);
        saveBtn.setEnabled(false);
        treeTable.getColumnModel().getColumn(1).setCellRenderer(new TaskTableCellRenderer());
        treeTable.getColumnModel().getColumn(2).setCellRenderer(new TaskTableCellRenderer());
        treeTable.getColumnModel().getColumn(11).setCellRenderer(new TaskTableCellRenderer());
        execBtn.setEnabled(false);
        newBtn.setEnabled(canCreate);
        delBtn.setEnabled(false);
        userBtn.setEnabled(false);
        procesBtn.setEnabled(false);
        protocolBtn.setEnabled(false);
        minutChckBtn.setEnabled(false);
        hourChckBtn.setEnabled(false);
        daymChckBtn.setEnabled(false);
        daywChckBtn.setEnabled(false);
        monthChckBtn.setEnabled(false);
        minutAllBtn.setEnabled(false);
        hourAllBtn.setEnabled(false);
        daymAllBtn.setEnabled(false);
        daywAllBtn.setEnabled(false);
        monthAllBtn.setEnabled(false);
        exePane.setEnabled(false);
        exeYesBtn.setEnabled(false);
        exeNowBtn.setEnabled(false);
        exeToDayBtn.setEnabled(false);
        exeNoBtn.setEnabled(false);
        minut.setEnabled(false);
        hour.setEnabled(false);
        daym.setEnabled(false);
        dayw.setEnabled(false);
        month.setEnabled(false);
        statusBtn.setOpaque(false);

        setOpaque(isOpaque);
        schedPane.setOpaque(isOpaque);
        schedPane.getViewport().setOpaque(isOpaque);
        p.setOpaque(isOpaque);

        tablePane.setOpaque(isOpaque);
        tablePane.getViewport().setOpaque(isOpaque);
        schedulerPane.setOpaque(isOpaque);
        taskPane.setOpaque(isOpaque);
        tree.setOpaque(isOpaque);
        itemPane.setOpaque(isOpaque);
        basicSplit.setOpaque(isOpaque);
        comandPane.setOpaque(isOpaque);
        exePane.setOpaque(isOpaque);
        exeYesBtn.setOpaque(isOpaque);
        exeNowBtn.setOpaque(isOpaque);
        exeToDayBtn.setOpaque(isOpaque);
        exeNoBtn.setOpaque(isOpaque);
        plainPane.setOpaque(isOpaque);
        minutPane.setOpaque(isOpaque);
        hourPane.setOpaque(isOpaque);
        daymPane.setOpaque(isOpaque);
        daywPane.setOpaque(isOpaque);
        monthPane.setOpaque(isOpaque);
        minutAllBtn.setOpaque(isOpaque);
        minutChckBtn.setOpaque(isOpaque);
        radioMinutPane.setOpaque(isOpaque);
        hourAllBtn.setOpaque(isOpaque);
        hourChckBtn.setOpaque(isOpaque);
        radioHourPane.setOpaque(isOpaque);
        daymAllBtn.setOpaque(isOpaque);
        daymChckBtn.setOpaque(isOpaque);
        radioDaymPane.setOpaque(isOpaque);
        daywAllBtn.setOpaque(isOpaque);
        daywChckBtn.setOpaque(isOpaque);
        radioDaywPane.setOpaque(isOpaque);
        monthAllBtn.setOpaque(isOpaque);
        monthChckBtn.setOpaque(isOpaque);
        radioMonthPane.setOpaque(isOpaque);
        minutListPane.setOpaque(isOpaque);
        hourListPane.setOpaque(isOpaque);
        daymListPane.setOpaque(isOpaque);
        monthListPane.setOpaque(isOpaque);
        daywListPane.setOpaque(isOpaque);
        minut.setOpaque(isOpaque);
        hour.setOpaque(isOpaque);
        daym.setOpaque(isOpaque);
        month.setOpaque(isOpaque);
        dayw.setOpaque(isOpaque);
    }

    private void load() {
        try {
            KrnClass cls_root = krn.getClassByName("TimerRoot");
            cls_folder = krn.getClassByName("TimerFolder");
            attr_child = krn.getAttributeByName(cls_folder, "children");
            KrnObject[] objs_root = krn.getClassObjects(cls_root, 0);
            KrnObject lang_ = krn.getInterfaceLanguage();
            if (objs_root == null || objs_root.length == 0) {
                KrnObject obj = krn.createObject(cls_root, 0);
                krn.setString(obj.id, obj.classId, "title", 0, lang_.id, "Планировщик", 0);
                objs_root = new KrnObject[]{obj};
            }
            //
            try {
                cls_protocol = krn.getClassByName("TimerProtocol");
                attr_protocol_start = krn.getAttributeByName(cls_protocol, "timeStart");
                attr_protocol_finish = krn.getAttributeByName(cls_protocol, "timeFinish");
                attr_protocol_next = krn.getAttributeByName(cls_protocol, "timeNextStart");
                attr_protocol_err = krn.getAttributeByName(cls_protocol, "err");
                attr_protocol_status = krn.getAttributeByName(cls_protocol, "status");
                attr_protocol_timer = krn.getAttributeByName(cls_protocol, "timer");
                cls_p_tname = cls_protocol.tname != null && cls_protocol.tname.length() > 0 ? cls_protocol.tname : "ct" + cls_protocol.id;
                attr_p_start_tname = attr_protocol_start.tname != null && attr_protocol_start.tname.length() > 0 ? attr_protocol_start.tname : "cm" + attr_protocol_start.id;
                attr_p_finish_tname = attr_protocol_finish.tname != null && attr_protocol_finish.tname.length() > 0 ? attr_protocol_finish.tname : "cm" + attr_protocol_finish.id;
                attr_p_timer_tname = attr_protocol_timer.tname != null && attr_protocol_timer.tname.length() > 0 ? attr_protocol_timer.tname : "cm" + attr_protocol_timer.id;
                attr_p_status_tname = attr_protocol_status.tname != null && attr_protocol_status.tname.length() > 0 ? attr_protocol_status.tname : "cm" + attr_protocol_status.id;
                attr_p_err_tname = attr_protocol_err.tname != null && attr_protocol_err.tname.length() > 0 ? attr_protocol_err.tname : "cm" + attr_protocol_err.id;
                KrnAttribute ft_attr = krn.getAttributeByName(Kernel.SC_FILTER, "title");
                KrnObject[] fobjs = krn.getObjectsByAttribute(Kernel.SC_FILTER.id, ft_attr.id, lang_.id, 0, "TimerProtocol", 0);
                if (fobjs != null && fobjs.length > 0)
                    filter_protocol = fobjs[0];
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //
            KrnObject[] objs = krn.getClassObjects(Kernel.SC_TIMER, 0);
            KrnObject lang = krn.getDataLanguage();
            long[] ids = Funcs.makeObjectIdArray(objs);
            SAXBuilder builder = new SAXBuilder();
            for (KrnObject obj : objs) {
                try {
                    TimerObject timer = new TimerObject(obj);
                    timers.put(obj.id, timer);
                    byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
                    if (data.length > 0) {
                        InputStream is = new ByteArrayInputStream(data);
                        timer.doc = builder.build(is);
                        if (timer.doc.getRootElement().getChild("month") != null)
                            timer.monthCol = timer.doc.getRootElement().getChild("month").getValue();
                        if (timer.doc.getRootElement().getChild("dayw") != null)
                            timer.weekDaysCol = timer.doc.getRootElement().getChild("dayw").getValue();
                        if (timer.doc.getRootElement().getChild("daym") != null)
                            timer.monthsDaysCol = timer.doc.getRootElement().getChild("daym").getValue();
                        if (timer.doc.getRootElement().getChild("hour") != null)
                            timer.hourCol = timer.doc.getRootElement().getChild("hour").getValue();
                        if (timer.doc.getRootElement().getChild("minut") != null)
                            timer.minuteCol = timer.doc.getRootElement().getChild("minut").getValue();
                    } else {
                        timer.doc = new Document(new Element("timer"));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            StringValue[] svs = krn.getStringValues(ids, Kernel.SC_TIMER.id, "title", lang.id, false, 0);
            for (StringValue sv : svs) {
                TimerObject timer = timers.get(new Long(sv.objectId));
                if (sv.index == 0)
                    timer.title = sv.value;
                timer.oldName = sv.value;
            }
            TreeSet<Object> ts = new TreeSet<Object>();
            ObjectValue[] ovs = krn.getObjectValues(ids, Kernel.SC_TIMER.id, "process", 0);
            for (ObjectValue ov : ovs) {
                Object key = ov.value.id;
                SchedObject proces = srvsMap.get(key);
                TimerObject timer = timers.get(new Long(ov.objectId));
                if (proces == null)
                    srvsMap.put(key, proces = new SchedObject(ov.value));
                if (timer.srvs == null) {
                    timer.srvs = new Vector<SchedObject>();
                }
                timer.srvs.add(proces);
                ts.add(key);
            }
            long[] ids_ = Funcs.makeLongArray(ts);
            svs = krn.getStringValues(ids_, Kernel.SC_PROCESS_DEF.id, "title", lang.id, false, 0);
            for (StringValue sv1 : svs) {
                SchedObject proces = srvsMap.get(new Long(sv1.objectId));
                if (sv1.index == 0)
                    proces.title = sv1.value;
            }
            ts.clear();
            ovs = krn.getObjectValues(ids, Kernel.SC_TIMER.id, "user", 0);
            for (ObjectValue ov1 : ovs) {
                Object key = ov1.value.id;
                SchedObject user = usersMap.get(key);
                TimerObject timer = timers.get(new Long(ov1.objectId));
                if (user == null)
                    usersMap.put(key, user = new SchedObject(ov1.value));
                timer.user = user;
                ts.add(key);
            }
            ids_ = Funcs.makeLongArray(ts);
            svs = krn.getStringValues(ids_, Kernel.SC_USER.id, "name", 0, false, 0);
            for (StringValue sv2 : svs) {
                SchedObject user = usersMap.get(new Long(sv2.objectId));
                if (sv2.index == 0)
                    user.title = sv2.value;
            }
            LongValue[] lvs = krn.getLongValues(ids, Kernel.SC_TIMER.id, "redy", 0);
            for (LongValue lv : lvs) {
                TimerObject timer = timers.get(new Long(lv.objectId));
                timer.redy = lv.value;
            }

            tm = new TimerModel(timers.get(new Long(objs_root[0].id)), timers);
            treeTable.setModel(tm);
            tree = treeTable.getTree();
            treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            treeTable.getSelectionModel().addListSelectionListener(this);
            tree.setSelectionRow(0);
            tablePane = new JScrollPane(treeTable);
            
			filterNodes();
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

	private void filterNodes() {
		try {
			List<String> procAllowedUIDs = new ArrayList<String>(); 
			String allowed = krn.getProcAllowed();
			if (allowed != null && allowed.length() > 0) {
				procAllowedUIDs.addAll(Arrays.asList(allowed.split(",")));
			}
			Object[] children = tm.getChildren(tm.getRoot());
			for (int i = 0; i < children.length; i++) {
				TimerNode timerNode = (TimerNode) children[i];
				TimerObject timerObject = timerNode.timer;
				Vector<SchedObject> srvs = timerObject.srvs;
				if (srvs != null) {
					for (SchedObject schedObject: srvs) {
						if (procAllowedUIDs.size() > 0) {
							if (!procAllowedUIDs.contains(schedObject.obj.uid)) {
								tm.removeNodeFromParent(timerNode);
							}
						}
					}
				}
			}
			
			List<String> procDeniedUIDs = new ArrayList<String>(); 
			String denied = krn.getProcDenied();
			if (denied != null && denied.length() > 0) {
				procDeniedUIDs.addAll(Arrays.asList(denied.split(",")));
			}
			children = tm.getChildren(tm.getRoot());
			for (int i = 0; i < children.length; i++) {
				TimerNode timerNode = (TimerNode) children[i];
				TimerObject timerObject = timerNode.timer;
				Vector<SchedObject> srvs = timerObject.srvs;
				if (srvs != null) {
					for (SchedObject schedObject: srvs) {
						if (procDeniedUIDs.size() > 0) {
							if (procDeniedUIDs.contains(schedObject.obj.uid)) {
								tm.removeNodeFromParent(timerNode);
							}
						}
					}
				}
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}

    private int[] getStrToIntArray(String str, int shift) {
        StringTokenizer st = new StringTokenizer(str, ",", false);
        int[] res = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            String st_ = st.nextToken();
            res[i++] = Integer.valueOf(st_.trim()) + shift;
        }
        Arrays.sort(res);
        return res;
    }

    private String getProcsByString(Collection col) {
        String res = "";
        for (Object aCol : col) {
            SchedObject srv = (SchedObject) aCol;
            if (res.equals(""))
                res += srv.title;
            else
                res += "\n" + srv.title;
        }
        return res;
    }

    private String getObjArrayToStr(Object[] obj) {
        String res = "";
        if (obj == null || obj.length == 0) return res;
        res = obj[0].toString().trim();
        for (int i = 1; i < obj.length; ++i) {
            res += "," + obj[i].toString().trim();
        }
        return res;
    }

    private void setObject(long id) {
        isSelectingTimer = true;
        Document doc = null;
        TimerObject timer = timers.get(id);
        if (timer != null)
            doc = timer.doc;
        minut.removeSelectionInterval(0, 59);
        hour.removeSelectionInterval(0, 23);
        daym.removeSelectionInterval(0, 30);
        month.removeSelectionInterval(0, 11);
        dayw.removeSelectionInterval(0, 6);
        if (doc != null) {
            Element xml = doc.getRootElement();
            Element e = xml.getChild("minut");
            if (e != null && e.getText().length() > 0) {
                int[] indexs = getStrToIntArray(e.getText(), 0);
                minutChckBtn.setSelected(true);
                minut.setSelectedIndices(indexs);
                minut.setEnabled(true);
            } else {
                minutAllBtn.setSelected(true);
                minut.setEnabled(false);
                minut.removeSelectionInterval(0, 59);
            }
            e = xml.getChild("hour");
            if (e != null) {
                int[] indexs = getStrToIntArray(e.getText(), 0);
                hourChckBtn.setSelected(true);
                hour.setSelectedIndices(indexs);
                hour.setEnabled(true);
            } else {
                hourAllBtn.setSelected(true);
                hour.setEnabled(false);
                hour.removeSelectionInterval(0, 23);
            }
            e = xml.getChild("daym");
            if (e != null) {
                int[] indexs = getStrToIntArray(e.getText(), -1);
                daymChckBtn.setSelected(true);
                daym.setSelectedIndices(indexs);
                daym.setEnabled(true);
            } else {
                daymAllBtn.setSelected(true);
                daym.setEnabled(false);
                daym.removeSelectionInterval(0, 30);
            }
            e = xml.getChild("month");
            if (e != null && e.getText().length() > 0) {
                int[] indexs = getStrToIntArray(e.getText(), 0);
                monthChckBtn.setSelected(true);
                month.setSelectedIndices(indexs);
                month.setEnabled(true);
            } else {
                monthAllBtn.setSelected(true);
                month.setEnabled(false);
                month.removeSelectionInterval(0, 11);
            }
            e = xml.getChild("dayw");

            if (e != null) {
                int[] indexs = getStrToIntArray(e.getText(), -1);
                daywChckBtn.setSelected(true);
                dayw.setSelectedIndices(indexs);
                dayw.setEnabled(true);
            } else {
                daywAllBtn.setSelected(true);
                dayw.setEnabled(false);
                dayw.removeSelectionInterval(0, 6);
            }
        } else {
            minutAllBtn.setSelected(true);
            hourAllBtn.setSelected(true);
            daymAllBtn.setSelected(true);
            monthAllBtn.setSelected(true);
            daywAllBtn.setSelected(true);
            minut.setEnabled(false);
            hour.setEnabled(false);
            daym.setEnabled(false);
            dayw.setEnabled(false);
            month.setEnabled(false);
        }
        if (timer != null) {
            //Выполнять
            if (timer.redy == Constants.TIMER_ACTIVE) {
                exeYesBtn.setSelected(true);
            } else if (timer.redy == Constants.TIMER_RUN_AT_START) {
                exeNowBtn.setSelected(true);
            } else if (timer.redy == Constants.TIMER_RUN_DAY_START) {
                exeToDayBtn.setSelected(true);
            } else
                exeNoBtn.setSelected(true);
            //Пользователь
            if (timer.user != null) {
                userText.setText(timer.user.title);
            } else
                userText.setText("");
            //Службы
            if (timer.srvs != null) {
                String str = getProcsByString(timer.srvs);
                procText.setText(str);
            } else {
                procText.setText("");
            }
            nameText.setText(timer.title);
        }
        isSelectingTimer = false;
        saveBtn.setEnabled(timer!=null && timer.isModified);
        repaint();
    }

    public void setSaveEnabled(boolean isEnabled) {
        saveBtn.setEnabled(isEnabled && canEdit);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        int indexs[] = tree.getSelectionRows();
        if(indexs.length==0) return;
        int selectedRow = indexs[0];
        TimerObject timer = (TimerObject) treeTable.getValueAt(selectedRow, 0);
        Element xml = null;
        if (timer.doc != null) {
            xml = timer.doc.getRootElement();
        }
        if (src == minutAllBtn || src == minutChckBtn || src == hourAllBtn || src == hourChckBtn ||
                src == daymAllBtn || src == daymChckBtn || src == monthAllBtn || src == monthChckBtn || src == daywAllBtn ||
                src == daywChckBtn || src == exeYesBtn || src == exeNowBtn || src == exeToDayBtn || src == exeNoBtn ||
                src == minut || src == hour || src == daym || src == dayw || src == month || src == nameText) {
            if (!treeTable.getSelectionModel().isSelectionEmpty()) {
                saveBtn.setEnabled(true);
            }
        }
        if (src == newBtn) {
            create();
        } else if (src == delBtn) {
            delete();
        } else if (src == execBtn) {
            execute();
        } else if (src == statusBtn) {
        	if(!statusBtn.isSelected()) {
        		isUpdateStatus=false;
        	}else
        		updateStatus();
        } else if (src == saveBtn) {
            save();
            saveBtn.setEnabled(false);
        } else if (src == protocolBtn) {
            viewLog();
        } else if (src == exeYesBtn) {
            timer.redy = Constants.TIMER_ACTIVE;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == exeNoBtn) {
            timer.redy = Constants.TIMER_NOT_ACTIVE;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == exeNowBtn) {
            timer.redy = Constants.TIMER_RUN_AT_START;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == exeToDayBtn) {
            timer.redy = Constants.TIMER_RUN_DAY_START;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == minutAllBtn) {
            minut.setEnabled(false);
            if (xml.getChild("minut") != null) {
                xml.removeChild("minut");
            }
            timer.minuteCol = null;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == minutChckBtn) {
            minut.setEnabled(true);
        } else if (src == hourAllBtn) {
            hour.setEnabled(false);
            if (xml.getChild("hour") != null) {
                xml.removeChild("hour");
            }
            timer.hourCol = null;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == hourChckBtn) {
            hour.setEnabled(true);
        } else if (src == daymAllBtn) {
            daym.setEnabled(false);
            if (xml.getChild("daym") != null) {
                xml.removeChild("daym");
            }
            timer.monthsDaysCol = null;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == daymChckBtn) {
            daym.setEnabled(true);
        } else if (src == monthAllBtn) {
            month.setEnabled(false);
            if (xml.getChild("month") != null) {
                xml.removeChild("month");
            }
            timer.monthCol = null;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == monthChckBtn) {
            month.setEnabled(true);
        } else if (src == daywAllBtn) {
            dayw.setEnabled(false);
            if (xml.getChild("dayw") != null) {
                xml.removeChild("dayw");
            }
            timer.weekDaysCol = null;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
            timer.isModified = true;
        } else if (src == daywChckBtn) {
            dayw.setEnabled(true);
        } else if (src == nameText) {
            timer.title = nameText.getText();
            timer.isModified = true;
            tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());

        } else if (src == userBtn) {
            int sel = treeTable.getSelectedRow();
            if (sel < 0) return;
            UserTree user_tree = getUserTree();
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                    "Выбор пользователя", new JScrollPane(user_tree));
            dlg.setSize(700, 550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                UserNode user = (UserNode) user_tree.getSelectedNode();
                timer.user = new SchedObject(user.getKrnObj());
                timer.user.title = user.toString();
                userText.setText(timer.user.title);
                timer.isModified = true;
                tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
                if (!treeTable.getSelectionModel().isSelectionEmpty())
                    saveBtn.setEnabled(canEdit);
            }
        } else if (src == procesBtn) {
            int sel = treeTable.getSelectedRow();
            if (sel < 0) return;
            ProcessUserComponent proc_tree = new ProcessUserComponent(false);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                    "Выбор процесса", proc_tree);
            dlg.setSize(700, 550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                ProcessNode process = proc_tree.getSelectedProcess();
                if (process != null) {
                    SchedObject proc = new SchedObject(process.getKrnObject());
                    proc.title = process.toString();
                    timer.srvs = new Vector<SchedObject>();
                    timer.srvs.add(proc);
                }
                String str = getProcsByString(/*curProc*/ timer.srvs);
                procText.setText(str);
                timer.isModified = true;
                tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
                if (!treeTable.getSelectionModel().isSelectionEmpty())
                    saveBtn.setEnabled(canEdit);
            }
        }

        if ((/*curTimer*/ timer == null /*|| curTimer.obj.classId!=Kernel.SC_TIMER.id*/) && saveBtn.isEnabled())
            saveBtn.setEnabled(false);
    }

    private void create() {
        try {
            int sel = treeTable.getSelectedRow();
            if (sel < 0) return;
            TimerObject timer_p = (TimerObject) treeTable.getValueAt(sel, 0);
            KrnObject obj;
            CreateSchedPane rp = new CreateSchedPane();
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                    "Создание таймеров", rp);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String timerName = rp.getText();
                if (timerName == null || timerName.trim().equals("")) {
                    JOptionPane.showMessageDialog(this, "Имя элемента не должно быть пустым!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (!rp.isFolder()) {
                        obj = krn.createObject(Kernel.SC_TIMER, 0);
                    } else {
                        obj = krn.createObject(cls_folder, 0);
                    }
                    krn.setObject(timer_p.obj.id, attr_child.id, 0, obj.id, 0, false);
                    krn.setString(obj.id, obj.classId, "title", 0, krn.getDataLanguage().id, timerName, 0);
                    TimerObject timer = new TimerObject(obj);
                    timer.title = timerName;
                    timer.oldName = timerName;
                    timers.put(obj.id, timer);
                    TimerNode node = new TimerNode(timer, timers);
                    TimerNode node_p = (TimerNode) tree.getSelectionPath().getLastPathComponent();
                    tm.insertNodeInto(node, node_p, node_p.getChildCount());
                    TreePath path = new TreePath(tm.getPathToRoot(node_p));
                    tree.expandPath(path);
                    tree.setSelectionPath(path);
                    treeTable.requestFocusInWindow();
                    timer.doc = new Document(new Element("timer"));
                    setObject(obj.id);
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }
    private void updateStatus() {
    	if(statusThread==null || !statusThread.isAlive()) {
		    statusThread = new Thread() {
		        public void run() {
		        	int count=0;
		        	while(true){
		        		try {
		        			getCurrentStatus();
		        			if(count>countUpdates || !isUpdateStatus) {
		        				isUpdateStatus=true;
		        				if(statusBtn.isSelected()) statusBtn.setSelected(false);
		        				break;
		        			}
		        			count++;
		            		sleep(threadSleep);
		        		} catch(Throwable e) {
		        			e.printStackTrace();
		        		}
		        	}
		        }
		    };
		    statusThread.start();
    	}
    }

    private void getCurrentStatus() {
    	String sql = "SELECT c_obj_id," + attr_p_start_tname+"," + attr_p_finish_tname
    			+"," + attr_p_timer_tname+"," + attr_p_status_tname+"," + attr_p_err_tname
    			+ " FROM " + cls_p_tname 
    			+ " WHERE " + attr_p_timer_tname+" IS NOT NULL AND c_obj_id in (SELECT max(c_obj_id) "
    			+ " FROM "+cls_p_tname
    			+ " WHERE " + attr_p_status_tname+"= [1] "
    			+ " GROUP BY " + attr_p_timer_tname+")";   	
    			try {
					java.util.List res = krn.runSql(sql,0, false);
		            if (res != null && res.size() > 0) {
		                for (Object re : res) {
		                	long obj_id=Long.parseLong(((List)re).get(0).toString());
		                	Object ts=((List)re).get(1);
		                	Object tf=((List)re).get(2);
		                	Object ter=((List)re).get(5);
		                	long timer_id=(((List)re).get(3)==null?-1:Long.parseLong(((List)re).get(3).toString()));
		                	if(timers.containsKey(timer_id)) {
		                		TimerObject timer=timers.get(timer_id);
		                		timer.timeStart= (ts!=null?ts.toString():"");
		                		timer.timeFinish= (tf!=null?tf.toString():"");
		                		timer.err= (ter!=null && !"".equals(ter));
		                	}
		                }
		                if(tree.getSelectionPath().getParentPath()!=null)
		                	tm.nodeChanged((TimerNode) tree.getSelectionPath().getParentPath().getLastPathComponent());
		                else
		                	tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
		            }
				} catch (KrnException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    }
    private void viewLog() {
        int sel = treeTable.getSelectedRow();
        if (sel < 0) return;
        try {
            TimerObject timer = (TimerObject) treeTable.getValueAt(sel, 0);
            Long objId = timer.obj.id;

            String memos = "";
            long[] ids = null;
            //Подготовка протокола
            String sql = "SELECT DISTINCT o.c_obj_id, o." + attr_p_start_tname
                    + " FROM " + cls_p_tname + " o"
                    + " WHERE o.c_is_del = 0 AND o.c_tr_id=0 AND " + "o." + attr_p_timer_tname + " = " + objId
                    + " ORDER BY o." + attr_p_start_tname + " DESC";
            java.util.List res = krn.runSql(sql, 50, false);
            int i = 0;
            if (res != null && res.size() > 0) {
                ids = new long[res.size()];
                for (Object re : res) {
                    Object o1 = ((ArrayList) re).get(0);
                    if (o1 instanceof BigDecimal)
                        ids[i++] = ((BigDecimal) o1).longValue();
                    else
                        ids[i++] = (Long) o1;
                }
            }
            if (ids != null && ids.length > 0) {
                LongValue[] statuss = krn.getLongValues(ids, attr_protocol_status, 0);
                TimeValue[] starts = krn.getTimeValues(ids, attr_protocol_start, 0);
                TimeValue[] nexts = krn.getTimeValues(ids, attr_protocol_next, 0);
                StringValue[] errs = krn.getStringValues(ids, attr_protocol_err, 0, false, 0);
                Map<Long, String> result = new TreeMap<>();
                if (statuss.length > 0) {
                    for (LongValue v : statuss) {
                        result.put(v.objectId, v.value == 0 ? "Планировщик стартован:" : v.value == 1 ? "Задание стартовано:" : "Ошибка при выполнении:");
                    }
                    if (starts.length > 0)
                        for (TimeValue v : starts) {
                            String rv = result.get(v.objectId);
                            result.put(v.objectId, (rv != null ? rv : "") + tf.format(kz.tamur.util.Funcs.convertTime(v.value)));
                        }
                    if (nexts.length > 0)
                        for (TimeValue v : nexts) {
                            String rv = result.get(v.objectId);
                            result.put(v.objectId, (rv != null ? rv + "; " : "") + "Следующее выполнение:" + tf.format(kz.tamur.util.Funcs.convertTime(v.value)));
                        }
                    if (errs.length > 0)
                        for (StringValue v : errs) {
                            String rv = result.get(v.objectId);
                            result.put(v.objectId, (rv != null ? rv + ";" : "") + "'" + v.value + "'");
                        }
                }
                for (long key : result.keySet()) {
                    memos += result.get(key) + "\n";
                }
            }
            protocolList.setText(memos);
            protocolList.setEditable(false);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                    "Просмотр протокола", false, new JScrollPane(protocolList));
            dlg.setSize(500, 500);
            dlg.setLocation(getCenterLocationPoint(500, 500));
            dlg.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void delete() {
        try {
            int sel = treeTable.getSelectedRow();
            if (sel < 0) return;
            int result = MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(),
                    MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите удалить запись?");
            if (result != ButtonsFactory.BUTTON_YES) return;
            TimerNode node = (TimerNode) tree.getSelectionPath().getLastPathComponent();
            TimerNode node_p = (TimerNode) node.getParent();
            TimerObject timer = (TimerObject) treeTable.getValueAt(sel, 0);
            Long objId = timer.obj.id;
            int index = node_p.getIndex(node);
            int count = node.getChildCount();
            if (count > 0) {
                deleteNode(node, count);
            }
            Collection<Object> values =
                    Collections.singletonList((Object) node.getObject().obj);
            krn.deleteValue(node_p.getObject().obj.id, node_p.getObject().obj.classId, "children", values, 0);
            krn.deleteObject(timer.obj, 0);
            timers.remove(objId);
            tm.removeNodeFromParent(node);
            TreePath path;
            if (index > 0)
                path = new TreePath(tm.getPathToRoot(node_p.getChildAt(index - 1)));
            else
                path = new TreePath(tm.getPathToRoot(node_p));
            tree.setSelectionPath(path);
            tree.setSelectionPath(path);
            krn.changeTimerTask(timer.obj.id, true);
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void execute() {
        try {
            int sel = treeTable.getSelectedRow();
            if (sel < 0) return;
            TimerObject timer = (TimerObject) treeTable.getValueAt(sel, 0);
            krn.executeTask(timer.obj.id);
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteNode(TimerNode node, int count) {
        try {
            int[] index = new int[count];
            for (int i = 0; i < count; ++i) {
                index[i] = i;
                TimerNode child = (TimerNode) node.getChildAt(i);
                int count_ch = child.getChildCount();
                if (count_ch > 0)
                    deleteNode(child, count_ch);
                krn.deleteObject(child.getObject().obj, 0);
            }
            krn.deleteValue(node.getObject().obj.id, node.getObject().obj.classId, "children", index, 0);
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void save() {
        try {
            Iterator<Map.Entry<Long, TimerObject>> it = timers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, TimerObject> pair = it.next();
                TimerObject timer = pair.getValue();
                Long objId = timer.obj.id;
                if (timer.isModified) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        if (timer.doc != null) {
                            XMLOutputter out = new XMLOutputter();
                            out.getFormat().setEncoding("UTF-8");
                            out.output(timer.doc.getRootElement(), os);
                            krn.setBlob(objId.longValue(), Kernel.SC_TIMER.id, "config", 0, os.toByteArray(), 0, 0);
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                    if (timer.user != null)
                        krn.setObject(objId.longValue(), Kernel.SC_TIMER.id, "user", 0, timer.user.obj.id, 0, false);
                    if (timer.srvs == null) return;
                    int l = (timer.srvs != null ? timer.srvs.size() : 0) - timer.srvs.size();
                    int[] del = new int[l > 0 ? l : 0];
                    for (int j = 0; j < l; ++j) {
                        del[j] = j + timer.srvs.size();
                    }

                    if (timer.srvs.size() > 0) {
                        for (int i = 0; i < timer.srvs.size(); ++i) {
                            krn.setObject(objId.longValue(), Kernel.SC_TIMER.id, "process", i, (timer.srvs.get(i)).obj.id, 0, false);
                        }
                    }

                    if (del.length > 0) {
                        krn.deleteValue(objId.longValue(), Kernel.SC_TIMER.id, "process", del, 0);
                    }

                    krn.setLong(objId.longValue(), Kernel.SC_TIMER.id, "redy", 0, timer.redy, 0);

                    if (nameText.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(this, "Имя элемента не должно быть пустым!", "Сообщение", JOptionPane.ERROR_MESSAGE);
                        nameText.setText(timer.oldName);
                        timer.title = timer.oldName;
                    } else {
                        krn.setString(objId.longValue(), Kernel.SC_TIMER.id, "title", 0, krn.getDataLanguage().id, timer.title, 0);
                        timer.oldName = timer.title;
                    }
                    tm.nodeChanged((TimerNode) tree.getSelectionPath().getLastPathComponent());
                    krn.changeTimerTask(timer.obj.id, false);
                    
                    filterNodes();
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isSelectingTimer = false;

    public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;
        Object src = e.getSource();
        if (src instanceof ListSelectionModel) {
            ListSelectionModel lsm =
                    (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
                int selectedRow = lsm.getMinSelectionIndex();
                TimerObject timer = (TimerObject) treeTable.getValueAt(selectedRow, 0);
                if (timer.obj.classId == Kernel.SC_TIMER.id) {
                    delBtn.setEnabled(canDelete);
                    execBtn.setEnabled(true);
                    newBtn.setEnabled(false);
                    userBtn.setEnabled(canEdit);
                    procesBtn.setEnabled(canEdit);
                    protocolBtn.setEnabled(canEdit);
                    minutChckBtn.setEnabled(canEdit);
                    hourChckBtn.setEnabled(canEdit);
                    daymChckBtn.setEnabled(canEdit);
                    daywChckBtn.setEnabled(canEdit);
                    monthChckBtn.setEnabled(canEdit);
                    minutAllBtn.setEnabled(canEdit);
                    hourAllBtn.setEnabled(canEdit);
                    daymAllBtn.setEnabled(canEdit);
                    daywAllBtn.setEnabled(canEdit);
                    monthAllBtn.setEnabled(canEdit);
                    exeYesBtn.setEnabled(canEdit);
                    exeNowBtn.setEnabled(canEdit);
                    exeToDayBtn.setEnabled(canEdit);
                    exeNoBtn.setEnabled(canEdit);
                } else {
                    delBtn.setEnabled(false);
                    execBtn.setEnabled(false);
                    newBtn.setEnabled(canCreate);
                    userBtn.setEnabled(false);
                    procesBtn.setEnabled(false);
                    protocolBtn.setEnabled(false);
                    minutChckBtn.setEnabled(false);
                    hourChckBtn.setEnabled(false);
                    daymChckBtn.setEnabled(false);
                    daywChckBtn.setEnabled(false);
                    monthChckBtn.setEnabled(false);
                    minutAllBtn.setEnabled(false);
                    hourAllBtn.setEnabled(false);
                    daymAllBtn.setEnabled(false);
                    daywAllBtn.setEnabled(false);
                    monthAllBtn.setEnabled(false);
                    exePane.setEnabled(false);
                    exeYesBtn.setEnabled(false);
                    exeNowBtn.setEnabled(false);
                    exeToDayBtn.setEnabled(false);
                    exeNoBtn.setEnabled(false);
                    timer.doc = null;
                }

                if (timer.obj.classId == Kernel.SC_TIMER.id) {
                    minut.setEnabled(canEdit);
                    hour.setEnabled(canEdit);
                    daym.setEnabled(canEdit);
                    month.setEnabled(canEdit);
                    dayw.setEnabled(canEdit);
                }
                setObject(timer.obj.id);
            } else {
                setObject(-1);
            }
        } else if (src instanceof JList) {
            if (!treeTable.getSelectionModel().isSelectionEmpty()) {
//                saveBtn.setEnabled(canEdit);
            }

            if (!isSelectingTimer) {
                int[] indexs = tree.getSelectionRows();
                int selectedRow = indexs[0];
                TimerObject timer = (TimerObject) treeTable.getValueAt(selectedRow, 0);
                Element xml = null;
                if (timer.doc != null) {
                    xml = timer.doc.getRootElement();
                    String mins = "";
                    boolean par;
                    if (minutChckBtn.isSelected()) {
                        if (!minut.isSelectionEmpty()) {
                            mins = getObjArrayToStr(minut.getSelectedValues());
                        }
                        if (mins.length() > 0) {
                            Element minut = xml.getChild("minut");
                            if (minut == null) {
                                minut = new Element("minut");
                                xml.addContent(minut);
                            }
                            minut.setText(mins);
                            timer.minuteCol = mins;
                            timer.isModified = true;
                        } else {
                            xml.removeChild("minut");
                            timer.minuteCol = null;

                        }
                    } else {
                        xml.removeChild("minut");
                        timer.minuteCol = null;
                    }

                    String hs = "";
                    if (hourChckBtn.isSelected()) {
                        if (!hour.isSelectionEmpty()) {
                            hs = getObjArrayToStr(hour.getSelectedValues());
                        }
                        if (hs.length() > 0) {
                            Element hour = xml.getChild("hour");
                            if (hour == null) {
                                hour = new Element("hour");
                                xml.addContent(hour);
                            }
                            hour.setText(hs);
                            timer.hourCol = hs;
                            timer.isModified = true;
                        } else {
                            xml.removeChild("hour");
                            timer.hourCol = null;
                        }
                    } else {
                        xml.removeChild("hour");
                        timer.hourCol = null;
                    }

                    String day = "";
                    if (daymChckBtn.isSelected()) {
                        if (!daym.isSelectionEmpty()) {
                            day = getObjArrayToStr(daym.getSelectedValues());
                        }
                        if (day.length() > 0) {
                            Element dayMonth = xml.getChild("daym");
                            if (dayMonth == null) {
                                dayMonth = new Element("daym");
                                xml.addContent(dayMonth);
                            }
                            dayMonth.setText(day);
                            timer.monthsDaysCol = day;
                            timer.isModified = true;
                        } else {
                            xml.removeChild("daym");
                            timer.monthsDaysCol = null;
                        }
                    } else {
                        xml.removeChild("daym");
                        timer.monthsDaysCol = null;
                    }

                    String months = "";
                    par = false;
                    if (monthChckBtn.isSelected()) {
                        if (!month.isSelectionEmpty()) {
                            int[] m0 = month.getSelectedIndices();
                            for (int aM0 : m0) {
                                months += (par ? "," + (aM0) : "" + (aM0));
                                if (!par)
                                    par = true;
                            }
                        }
                        if (months.length() > 0) {
                            Element month = xml.getChild("month");
                            if (month == null) {
                                month = new Element("month");
                                xml.addContent(month);
                            }
                            month.setText(months);
                            timer.monthCol = months;
                            timer.isModified = true;
                        } else {
                            xml.removeChild("month");
                            timer.monthCol = null;
                        }
                    } else {
                        xml.removeChild("month");
                        timer.monthCol = null;
                    }

                    day = "";
                    par = false;
                    if (daywChckBtn.isSelected()) {
                        if (!dayw.isSelectionEmpty()) {
                            int[] d0 = dayw.getSelectedIndices();
                            for (int aD0 : d0) {
                                day += (par ? "," + (aD0 + 1) : "" + (aD0 + 1));
                                if (!par)
                                    par = true;
                            }
                        }
                        if (day.length() > 0) {
                            Element dayWeek = xml.getChild("dayw");
                            if (dayWeek == null) {
                                dayWeek = new Element("dayw");
                                xml.addContent(dayWeek);
                            }
                            dayWeek.setText(day);
                            timer.weekDaysCol = day;
                            timer.isModified = true;
                        } else {
                            xml.removeChild("dayw");
                            timer.weekDaysCol = null;
                        }
                    } else {
                        xml.removeChild("dayw");
                        timer.weekDaysCol = null;
                    }
                }
          }
            repaint();
        }
    }

    public void placeDivider() {
        basicSplit.setDividerLocation(0.3);
        validate();
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            int sel = treeTable.getSelectedRow();
            if (sel == -1) return ButtonsFactory.BUTTON_NOACTION;
            TimerObject timer = (TimerObject) treeTable.getValueAt(sel, 0);
            if(!timer.isModified) return ButtonsFactory.BUTTON_NOACTION;
            String fNames = treeTable.getValueAt(sel, 0).toString();
            String mess = "Задание: \n\"" + fNames + "\"\nбыло модифицировано! Сохранить изменения?";
            int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                save();
                return res;
            } else {
                return res;
            }
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    class TaskTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            setText((value != null) ? value.toString() : "");
            setFont(Utils.getDefaultFont());
            switch (column) {
                case 1:
                    if (value != null &&
                            value.toString().equals("Не выполнять")) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("TaskNo"));
                    } else if (value != null && !value.toString().equals("")) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("TaskOk"));
                    } else setIcon(null);
                    break;
                case 2:
                    if (value != null && !value.toString().equals("")) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("userNode"));
                    } else setIcon(null);

                    break;
                case 11:
                    if (value != null && !value.toString().equals("")) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("TaskNo"));
                    } else {
                    	setIcon(null);
                    }
                    break;
            }
//            setOpaque(true);
            if (isSelected) {
                setBackground(Utils.getSysColor());
            } else {
                setBackground(Color.white);
            }
            return this;
        }
    }

    public void setBlock(boolean isBlocked) {
        tablePane.setVisible(isBlocked);
        itemPane.setVisible(isBlocked);
        newBtn.setVisible(isBlocked);
        delBtn.setVisible(isBlocked);
        execBtn.setVisible(isBlocked);
        placeDivider();
    }
}
