package kz.tamur.guidesigner.users;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.red5.client.net.rtmp.RTMPClient;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.or3.client.lang.SystemOp;
import kz.tamur.rt.MainFrame;
import chrriis.dj.nativeswing.swtimpl.components.FlashPluginOptions;
import chrriis.dj.nativeswing.swtimpl.components.JFlashPlayer;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.kernel.UserSessionValue;

public class ChatPanel extends JPanel {
	
	private Kernel krn = Kernel.instance();
	private JTable usersTable;
	private ActiveUsersTableModel usersTableModel;
	private JButton refreshButton = ButtonsFactory.createToolButton("BoxNode",
			"Обновить");
	private ColorTextPane allText = new ColorTextPane();
	private JTextArea myText = new JTextArea();
	private JToolBar toolBar = Utils.createDesignerToolBar();
	private JLabel activeDialogs = new JLabel("Контакты:");
	private MainFrame.DescLabel counterLabel = Utils.createDescLabel("");
	private int selRowIdx;
	private int rowCount;
	private final ImageIcon SORT_UP = kz.tamur.rt.Utils
			.getImageIcon("SortUpLight");
	private final ImageIcon SORT_DOWN = kz.tamur.rt.Utils
			.getImageIcon("SortDownLight");
	private final String newStatus = "New";
	private final String oldStatus = "Old";
	private final String canDeleteYes = "Yes";
	private final String canDeleteNo = "No";
	private int[] usersNewMessages;
	private ArrayList<String> usersNames = new ArrayList<String>();
	private JTableHeader myHeader;
	private TableColumn Column;
	private int[] selectedUsersLoad;
	private int[] selectedUsersRefresh;
	private int[] selectedUsersSend;
	private int[] selectedUsersClear;
	private JButton sendButton = new JButton("Отправить Ctrl+Enter ");
	private JLabel dialogHistory = new JLabel("История переписки:");
	private JPopupMenu chatPopup = new JPopupMenu();
	private String fromUser = krn.getUser().getName();
	private static int LOAD_MODE = 3; // 3 - Today; 2 - Week; 1 - Month; 0 - All
	private JLabel showLabel = kz.tamur.rt.Utils
			.createLabel("Показывать сообщения за:");
	private JLabel weekLabel = kz.tamur.rt.Utils.createLabel("Неделю");
	private JLabel monthLabel = kz.tamur.rt.Utils.createLabel("Месяц");
	private JLabel allPeriodLabel = kz.tamur.rt.Utils
			.createLabel("Весь период");
	private Color defaultColor = showLabel.getForeground();
	private Calendar currentCalendar;
	private JScrollPane allTextScroll;
	private JScrollPane myTextScroll;
	private int caretPosition;
	private Timer refreshContent;
	private boolean currentPosition = true;

	private JFlashPlayer flashPlayer = new JFlashPlayer();
	private RTMPClient rtmpClient;

	private static final ImageIcon defUserIcon = new ImageIcon(ChatPanel.class.getResource("/kz/tamur/comps/images/defaultAvatar.gif"));
	private static final ImageIcon cameraOnIcon = new ImageIcon(ChatPanel.class.getResource("/kz/tamur/comps/images/cameraOn.png"));
	private static final ImageIcon watchingYouIcon = new ImageIcon(ChatPanel.class.getResource("/kz/tamur/comps/images/watchingYou.png"));

	private Map<String, Red5User> red5Users = new HashMap<String, Red5User>();

	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private List<DesignerDialog> videoDialogs = new ArrayList<DesignerDialog>();

	public ChatPanel() {
		super();

		toolBar.add(refreshButton);
		toolBar.setBorder(null);

		allText.setEditable(false);
		myText.addKeyListener(new KeyHandler());
		this.setLayout(new GridBagLayout());

		UserSessionValue[] userSessions = new UserSessionValue[0];
		try {
			userSessions = krn.getUserSessions();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Red5User[] cuttingUserSessionValue = convertUserSessionValue(userSessions);
		usersTableModel = new ActiveUsersTableModel(cuttingUserSessionValue);
		usersTable = new JTable(usersTableModel) {
			public void valueChanged(ListSelectionEvent e) {
				super.valueChanged(e);
				setCounterText();
				loadMessages(allText);
			}
		};
		usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		usersTable.getColumnModel().getColumn(0).setPreferredWidth(55);
		usersTable.getColumnModel().getColumn(0).setMaxWidth(55);
		usersTable.getColumnModel().getColumn(1).setPreferredWidth(55);
		usersTable.getColumnModel().getColumn(1).setMaxWidth(55);
		usersTable.getColumnModel().getColumn(2).setPreferredWidth(55);
		usersTable.getColumnModel().getColumn(2).setMaxWidth(55);
		usersTable.setRowHeight(55);

		refreshTable();

		JMenuItem historyDelete = kz.tamur.rt.Utils.createMenuItem("Удалить архив сообщений");
		chatPopup.add(historyDelete);
		historyDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Удалить архив сообщений"))
					clearHistory();
			}
		});
		final JMenuItem watchCamera = kz.tamur.rt.Utils.createMenuItem("Просмотр камеры собеседника");
		chatPopup.add(watchCamera);
		watchCamera.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getSource().equals(watchCamera))
					watchCamera();
			}
		});
		MouseListener myListener = new TablePopupListener(chatPopup, usersTable);
		usersTable.addMouseListener(myListener);

		usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel tablePain = new JPanel();
		tablePain.setLayout(new BorderLayout(3, 3));
		JPanel counter = new JPanel();
		counter.setLayout(new BorderLayout(3, 3));
		counter.add(counterLabel, BorderLayout.EAST);
		JScrollPane usersScrollPane = new JScrollPane(usersTable);
		usersScrollPane.setOpaque(isOpaque);
		usersScrollPane.getViewport().setOpaque(isOpaque);
		tablePain.add(usersScrollPane);

		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshTable();
			}
		});

		// Обновление сообщений и списка контактов каждые 5 секунд
		refreshContent = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMessages(allText);
				refreshTable();
			}
		});

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new GridBagLayout());
		headerPanel.add(
				activeDialogs,
				new CnrBuilder().x(0).y(0).wtx(1).wty(1)
						.fill(GridBagConstraints.NONE)
						.anchor(GridBagConstraints.NORTHWEST).ins(5, 5, 5, 5)
						.build());
		headerPanel.add(
				counter,
				new CnrBuilder().x(1).y(0).wtx(1).wty(1)
						.fill(GridBagConstraints.NONE)
						.anchor(GridBagConstraints.NORTHEAST).ins(5, 5, 5, 5)
						.build());

		allTextScroll = new JScrollPane(allText);
		allTextScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		myText.setWrapStyleWord(true);
		myText.setLineWrap(true);
		myTextScroll = new JScrollPane(myText);
		myTextScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		//allTextScroll.setMinimumSize(new Dimension(400, 300));
		//allTextScroll.setMaximumSize(new Dimension(400, 300));
		allTextScroll.setPreferredSize(new Dimension(400, 300));

		myTextScroll.setMinimumSize(new Dimension(150, 150));
		myTextScroll.setMaximumSize(new Dimension(150, 150));
		myTextScroll.setPreferredSize(new Dimension(150, 150));

		final String sessionId = krn.getUserSession().id.toString();

		FlashPluginOptions options = new FlashPluginOptions();
		Map<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = -2795797287826408167L;
			{
				put("red5_server_address",
						"rtmp://192.168.13.107:1935/Or3Red5Chat/public");
				put("avatar",
						"http://localhost:5080/Or3Red5Chat/default_avatar_m.gif");
				put("nickname", sessionId);
			}
		};
		options.setVariables(map);
		flashPlayer.addFlashPlayerListener(new ChatFlashListener(this,
				"192.168.13.107", 1935, "Or3Red5Chat", sessionId));
		flashPlayer.load(this.getClass(), "/resources/myvideo.swf", options);
		flashPlayer.setPreferredSize(new Dimension(234, 270));
		flashPlayer.setMaximumSize(new Dimension(234, 270));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(flashPlayer);
		rightPanel.add(allTextScroll);

		JPanel periodPanel = new JPanel();
		periodPanel.setLayout(new FlowLayout());
		periodPanel.setMinimumSize(new Dimension(400, 20));
		periodPanel.setMaximumSize(new Dimension(400, 20));
		periodPanel.setPreferredSize(new Dimension(400, 20));
		JLabel iconLabel = new JLabel(
				kz.tamur.rt.Utils.getImageIcon("ChatClock"));
		JLabel separatorOne = kz.tamur.rt.Utils.createLabel("\t");
		JLabel separatorTwo = kz.tamur.rt.Utils.createLabel("\t");
		JLabel separatorThree = kz.tamur.rt.Utils.createLabel("\t");
		periodPanel.add(iconLabel);
		periodPanel.add(showLabel);
		periodPanel.add(separatorOne);
		periodPanel.add(weekLabel);
		periodPanel.add(separatorTwo);
		periodPanel.add(monthLabel);
		periodPanel.add(separatorThree);
		periodPanel.add(allPeriodLabel);
		weekLabel.addMouseListener(new periodLabelListener());
		monthLabel.addMouseListener(new periodLabelListener());
		allPeriodLabel.addMouseListener(new periodLabelListener());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(headerPanel,
				new CnrBuilder().x(0).y(0).fill(GridBagConstraints.BOTH)
						.anchor(GridBagConstraints.NORTHWEST).ins(0, 0, 0, 0)
						.build());
		mainPanel.add(
				tablePain,
				new CnrBuilder().x(0).y(1).wtx(1).wty(1)
						.fill(GridBagConstraints.BOTH)
						.anchor(GridBagConstraints.NORTHWEST).ins(0, 0, 0, 0)
						.build());
		mainPanel.add(dialogHistory,
				new CnrBuilder().x(1).y(0).fill(GridBagConstraints.NONE)
						.anchor(GridBagConstraints.CENTER).ins(0, 0, 0, 0)
						.build());
		mainPanel.add(
				myTextScroll,
				new CnrBuilder().x(0).y(2).wtx(1).wty(0)
						.fill(GridBagConstraints.HORIZONTAL)
						.anchor(GridBagConstraints.NORTHWEST).ins(0, 0, 0, 0)
						.build());
		mainPanel.add(
				rightPanel,
				new CnrBuilder().x(1).y(1).wtx(0).wty(1).h(2)
						.fill(GridBagConstraints.VERTICAL)
						.anchor(GridBagConstraints.NORTHEAST).ins(0, 0, 0, 0)
						.build());
		mainPanel.add(periodPanel, new CnrBuilder().x(1).y(3).wtx(0).wty(0)
				.anchor(GridBagConstraints.CENTER).ins(0, 0, 0, 0).build());

		this.add(toolBar,
				new CnrBuilder().x(0).y(0).fill(GridBagConstraints.NONE)
						.anchor(GridBagConstraints.NORTHWEST).build());
		this.add(
				mainPanel,
				new CnrBuilder().x(0).y(1).fill(GridBagConstraints.BOTH)
						.anchor(GridBagConstraints.WEST).wtx(1).wty(1)
						.ins(0, 2, 4, 2).build());

		mainPanel.add(sendButton,
				new CnrBuilder().x(0).y(3).fill(GridBagConstraints.NONE)
						.anchor(GridBagConstraints.CENTER).ins(5, 5, 5, 5)
						.build());
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedUsersSend = usersTable.getSelectedRows();
				if (selectedUsersSend.length == 0) {
					JOptionPane.showMessageDialog(getTopLevelAncestor(),
							"Пользователи не выбраны!");
					return;
				} else {
					if (myText.getText().trim().equals("")) {
						myText.setText("");
						return;
					}
					String[] toUser = new String[selectedUsersSend.length];
					for (int i = 0; i < selectedUsersSend.length; i++)
						toUser[i] = (String) usersTable.getModel().getValueAt(
								selectedUsersSend[i], 4);
					try {
						KrnClass class_ = krn.getClassByName("ChatClass");
						KrnObject object_ = krn.createObject(class_, 0);
						Date date_ = new Date();
						for (int i = 0; i < toUser.length; i++) {
							krn.setString(object_.id, class_.id, "from", 0, 0,
									fromUser, 0);
							krn.setString(object_.id, class_.id, "to", 0, 0,
									toUser[i], 0);
							krn.setString(object_.id, class_.id, "text", 0, 0,
									myText.getText(), 0);
							krn.setString(object_.id, class_.id, "status", 0,
									0, newStatus, 0);
							krn.setString(object_.id, class_.id,
									"canDeleteFrom", 0, 0, canDeleteNo, 0);
							krn.setString(object_.id, class_.id, "canDeleteTo",
									0, 0, canDeleteNo, 0);
							krn.setTime(object_.id, class_.id, "datetime", 0,
									date_, 0);
						}
					} catch (KrnException e1) {
						e1.printStackTrace();
					}
					currentPosition = false;
					loadMessages(allText);
					currentPosition = true;
					myText.setText("");
				}
			}
		});

		setCounterText();
		setOpaque(isOpaque);
		tablePain.setOpaque(isOpaque);
		counter.setOpaque(isOpaque);
		toolBar.setOpaque(isOpaque);
		refreshButton.setOpaque(isOpaque);
	}

	// Запускает или останавливает таймер для обновления сообщений и списка
	// контактов
	public void setTimerCanWork(boolean canWork) {
		if (canWork) {
			if (!refreshContent.isRunning())
				refreshContent.start();
		} else if (refreshContent.isRunning())
			refreshContent.stop();
	}

	// Очищает архив сообщений
	private void clearHistory() {
		selectedUsersClear = usersTable.getSelectedRows();
		String[] toUser = new String[selectedUsersClear.length];
		for (int i = 0; i < selectedUsersClear.length; i++)
			toUser[i] = (String) usersTable.getModel().getValueAt(
					selectedUsersClear[i], 4);
		try {
			KrnClass class_ = krn.getClassByName("ChatClass");
			KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
			long[] massivObjectsId = new long[massivObjects.length];
			for (int j = 0; j < massivObjects.length; j++)
				massivObjectsId[j] = massivObjects[j].id;

			StringValue[] massivFrom = krn.getStringValues(massivObjectsId,
					class_.id, "from", 0, false, 0);
			StringValue[] massivTo = krn.getStringValues(massivObjectsId,
					class_.id, "to", 0, false, 0);
			StringValue[] massivCanDeleteFrom = krn.getStringValues(
					massivObjectsId, class_.id, "canDeleteFrom", 0, false, 0);
			StringValue[] massivCanDeleteTo = krn.getStringValues(
					massivObjectsId, class_.id, "canDeleteTo", 0, false, 0);

			for (int j = 0; j < massivObjects.length; j++)
				if (massivFrom[j].value.equals(fromUser))
					if (massivCanDeleteTo[j].value.equals(canDeleteYes))
						krn.deleteObject(
								krn.getObjectById(massivObjectsId[j], 0), 0);
					else
						krn.setString(massivObjectsId[j], class_.id,
								"canDeleteFrom", 0, 0, canDeleteYes, 0);
				else if (massivTo[j].value.equals(fromUser))
					if (massivCanDeleteFrom[j].value.equals(canDeleteYes))
						krn.deleteObject(
								krn.getObjectById(massivObjectsId[j], 0), 0);
					else
						krn.setString(massivObjectsId[j], class_.id,
								"canDeleteTo", 0, 0, canDeleteYes, 0);
		} catch (KrnException e) {
			e.printStackTrace();
		}

	}

	// Просмотр камеры
	private void watchCamera() {
		int row  = usersTable.getSelectedRow();
		final String uid = (String) usersTable.getModel().getValueAt(row, 3);
		String name = (String) usersTable.getModel().getValueAt(row, 4);
		try {
			FlashPluginOptions options = new FlashPluginOptions();
			Map<String, String> map = new HashMap<String, String>() {
				private static final long serialVersionUID = -2795797287826408167L;
				{
					put("red5_server_address", "rtmp://192.168.13.107:1935/Or3Red5Chat/public");
					put("watchee", uid);
					put("watcher", krn.getUserSession().id.toString());
				}
			};
			options.setVariables(map);
			
			JFlashPlayer flash = new JFlashPlayer();
			//flashPlayer.addFlashPlayerListener(new ChatFlashListener(this,
			//		"192.168.13.107", 1935, "Or3Red5Chat", uid));
			flash.load(this.getClass().getResource("/resources/invideo.swf").toExternalForm(), options);
			flash.setPreferredSize(new Dimension(320, 240));

            final DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(), "Камера - " + name, flash);
            dlg.setNoButton();
            dlg.setModal(false);
            dlg.setResizable(false);
            
            setPosition(dlg);
            
            dlg.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					videoDialogs.remove(dlg);
					super.windowClosed(e);
					if (rtmpClient != null) {
						rtmpClient.invoke("offwatch", new Object[] {uid, krn.getUserSession().id.toString()}, null);
					}
				}
			});
            
            videoDialogs.add(dlg);
            dlg.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setPosition(DesignerDialog dlg) {
		int width = 345;
		int height = 287;
		
    	int scrX = getTopLevelAncestor().getLocationOnScreen().x;
    	int scrY = getTopLevelAncestor().getLocationOnScreen().y;
    	int scrWidth = getTopLevelAncestor().getWidth();
    	int scrHeight = getTopLevelAncestor().getHeight();
    	
    	int x = 0, y = 0;
    	if (videoDialogs.size() > 0) {
    		DesignerDialog last = videoDialogs.get(videoDialogs.size() - 1);
    		Point lastLoc = last.getLocation();
    		x = lastLoc.x + width;
    		y = lastLoc.y;
    		if (x + width > scrX + scrWidth) {
    			x = scrX;
    			y -= height;
    		}
    	} else {
    		x = scrX;
    		y = scrY + scrHeight - height;
    	}
        dlg.setLocation(x, y);
	}

	// Загружает сообщения из базы данных
	private void loadMessages(ColorTextPane allText) {
		selectedUsersLoad = usersTable.getSelectedRows();
		if (selectedUsersLoad.length == 0) {
			allText.setText("");
			return;
		}
		caretPosition = allTextScroll.getVerticalScrollBar().getValue();
		currentCalendar = GregorianCalendar.getInstance();

		String[] toUser = new String[selectedUsersLoad.length];
		for (int i = 0; i < selectedUsersLoad.length; i++)
			toUser[i] = (String) usersTable.getModel().getValueAt(
					selectedUsersLoad[i], 4);
		try {
			KrnClass class_ = krn.getClassByName("ChatClass");
			KrnAttribute attribute_ = krn
					.getAttributeByName(class_, "datetime");
			KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
			long[] massivObjectsId = new long[massivObjects.length];
			for (int j = 0; j < massivObjects.length; j++)
				massivObjectsId[j] = massivObjects[j].id;

			StringValue[] massivText = krn.getStringValues(massivObjectsId,
					class_.id, "text", 0, false, 0);
			StringValue[] massivFrom = krn.getStringValues(massivObjectsId,
					class_.id, "from", 0, false, 0);
			StringValue[] massivTo = krn.getStringValues(massivObjectsId,
					class_.id, "to", 0, false, 0);
			StringValue[] massivStatus = krn.getStringValues(massivObjectsId,
					class_.id, "status", 0, false, 0);
			StringValue[] massivCanDeleteFrom = krn.getStringValues(
					massivObjectsId, class_.id, "canDeleteFrom", 0, false, 0);
			StringValue[] massivCanDeleteTo = krn.getStringValues(
					massivObjectsId, class_.id, "canDeleteTo", 0, false, 0);
			TimeValue[] massivDateTime = krn.getTimeValues(massivObjectsId,
					attribute_, 0);

			allText.setText("");
			for (int j = 0; j < massivObjects.length; j++)
				if ((massivFrom[j].value.equals(fromUser) && massivTo[j].value
						.equals(toUser[0]))
						|| (massivTo[j].value.equals(fromUser) && massivFrom[j].value
								.equals(toUser[0]))) {
					Date messageDate = new Date(
							massivDateTime[j].value.year - 1900,
							massivDateTime[j].value.month,
							massivDateTime[j].value.day,
							massivDateTime[j].value.hour,
							massivDateTime[j].value.min,
							massivDateTime[j].value.sec);
					if (datesCompare(messageDate)) {
						if (massivFrom[j].value.equals(fromUser)
								&& massivCanDeleteFrom[j].value
										.equals(canDeleteNo)) {
							allText.appendText(massivFrom[j].value + " ("
									+ convertDate(messageDate) + ")\n",
									Color.RED, true);
							allText.appendText(massivText[j].value + "\n\n",
									Color.RED, false);
						} else if (massivTo[j].value.equals(fromUser)
								&& massivCanDeleteTo[j].value
										.equals(canDeleteNo)) {
							allText.appendText(massivFrom[j].value + " ("
									+ convertDate(messageDate) + ")\n",
									Color.BLUE, true);
							allText.appendText(massivText[j].value + "\n\n",
									Color.BLUE, false);
						}
					}
					if (massivTo[j].value.equals(fromUser)
							&& massivStatus[j].value.equals(newStatus))
						krn.setString(massivObjectsId[j], class_.id, "status",
								0, 0, oldStatus, 0);
				}
			try {
				if (currentPosition)
					allText.setCaretPosition(caretPosition);
			} catch (Exception e) {
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}

	private boolean datesCompare(Date messageDate) {
		Calendar messageCalendar = GregorianCalendar.getInstance();
		messageCalendar.setTime(messageDate);
		switch (LOAD_MODE) {
		case 0:
			return true;
		case 1:
			if (currentCalendar.get(Calendar.YEAR) == messageCalendar
					.get(Calendar.YEAR))
				if ((currentCalendar.get(Calendar.DAY_OF_YEAR) - messageCalendar
						.get(Calendar.DAY_OF_YEAR)) < 30)
					return true;
		case 2:
			if (currentCalendar.get(Calendar.YEAR) == messageCalendar
					.get(Calendar.YEAR))
				if ((currentCalendar.get(Calendar.DAY_OF_YEAR) - messageCalendar
						.get(Calendar.DAY_OF_YEAR)) < 7)
					return true;
		case 3:
			if (currentCalendar.get(Calendar.YEAR) == messageCalendar
					.get(Calendar.YEAR))
				if ((currentCalendar.get(Calendar.DAY_OF_YEAR) - messageCalendar
						.get(Calendar.DAY_OF_YEAR)) < 1)
					return true;
		default:
			return false;
		}
	}

	private String convertDate(Date messageDate) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return dateFormat.format(messageDate);
	}

	// Обновляет таблицу активных пользователей
	public void refreshTable() {
		selectedUsersRefresh = usersTable.getSelectedRows();
		try {
			UserSessionValue[] userSessions = krn.getUserSessions();
			Red5User[] cuttingUserSessionValue = convertUserSessionValue(userSessions);
			ActiveUsersTableModel myModel = (ActiveUsersTableModel) usersTable
					.getModel();
			if (cuttingUserSessionValue.length > 0) {
				KrnClass class_ = krn.getClassByName("ChatClass");
				KrnObject[] massivObjects = krn.getClassObjects(class_, 0);
				long[] massivObjectsId = new long[massivObjects.length];
				for (int j = 0; j < massivObjects.length; j++)
					massivObjectsId[j] = massivObjects[j].id;

				usersNewMessages = new int[cuttingUserSessionValue.length];
				for (int j = 0; j < usersNewMessages.length; j++)
					usersNewMessages[j] = 0;

				StringValue[] massivFrom = krn.getStringValues(massivObjectsId,
						class_.id, "from", 0, false, 0);
				StringValue[] massivTo = krn.getStringValues(massivObjectsId,
						class_.id, "to", 0, false, 0);
				StringValue[] massivStatus = krn.getStringValues(
						massivObjectsId, class_.id, "status", 0, false, 0);
				for (int i = 0; i < massivObjects.length; i++) {
					if (massivTo[i].value.equals(fromUser)
							&& massivStatus[i].value.equals(newStatus)) {
						for (int j = 0; j < cuttingUserSessionValue.length; j++)
							if (massivFrom[i].value
									.equals(cuttingUserSessionValue[j].name))
								usersNewMessages[j]++;
					}
				}
				usersNames.clear();
				for (int j = 0; j < cuttingUserSessionValue.length; j++)
					if (usersNewMessages[j] > 0)
						usersNames.add(cuttingUserSessionValue[j].name);
				myModel.setUsers(cuttingUserSessionValue);
				myModel.fireTableDataChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTableAlignment(JLabel.CENTER, usersTable, usersNames);
		usersTable.setRowSelectionAllowed(true);
		usersTable.setRequestFocusEnabled(true);
		for (int i = 0; i < selectedUsersRefresh.length; i++)
			usersTable.setRowSelectionInterval(selectedUsersRefresh[i],
					selectedUsersRefresh[i]);
	}
	
	public void tableChanged() {
		ActiveUsersTableModel myModel = (ActiveUsersTableModel) usersTable.getModel();
		myModel.fireTableDataChanged();
	}

	// Выравнивает заголовки таблицы и значения в ячейках по центру
	private void setTableAlignment(int anyAlignment, JTable anyTable,
			ArrayList<String> anyList) {
		new TableRowColor(anyTable, "Логин", anyList);
		myHeader = anyTable.getTableHeader();
		myHeader.setUpdateTableInRealTime(true);
		myHeader.addMouseListener(new ColumnListener());
		myHeader.setReorderingAllowed(false);
		for (int i = 0; i < anyTable.getColumnCount(); i++) {
			Column = anyTable.getColumnModel().getColumn(i);
			DefaultTableCellRenderer newRenderer = new DefaultTableCellRenderer();
			newRenderer
					.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
			newRenderer.setBackground(Color.LIGHT_GRAY);
			newRenderer.setHorizontalAlignment(anyAlignment);
			Column.setHeaderRenderer(newRenderer);
			if (i == usersTableModel.getSortColumn())
				newRenderer.setIcon(usersTableModel.getColumnIcon(i));
		}
		anyTable.setSelectionBackground(Color.LIGHT_GRAY);
		anyTable.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		anyTable.updateUI();
	}

	// Конвертирует объекты UserSessionValues в объекты ChatUsers
	private Red5User[] convertUserSessionValue(UserSessionValue[] uss) {
		ArrayList<ChatUsers> tempContainer = new ArrayList<ChatUsers>();
		ArrayList<String> existingUsers = new ArrayList<String>();
		existingUsers.addAll(red5Users.keySet());

		for (int i = 0; i < uss.length; i++) {
			String uid = uss[i].id.toString();
			boolean contains = existingUsers.remove(uid);
			if (!contains) {
				Icon icon = null;
				try {
					byte[] photo = krn.getUserPhoto(uid);
					if (photo != null && photo.length > 0)
						icon = new ImageIcon(new SystemOp(krn).getScaledImage(photo, 50, 50, "PNG"));
				} catch (Exception e) {
				}

				red5Users.put(uid, new Red5User(uid, uss[i].name, uss[i].ip,
						uss[i].startTime, icon, false, false));
			}
		}

		for (String uid : existingUsers)
			red5Users.remove(uid);

		int i = 0;
		Red5User[] res = new Red5User[red5Users.size()];
		for (Red5User user : red5Users.values())
			res[i++] = user;
		return res;
	}

	private void setCounterText() {
		rowCount = usersTable.getModel().getRowCount();
		selRowIdx = usersTable.getSelectedRow() + 1;
		counterLabel.setText(selRowIdx + " / " + rowCount + " ");
	}

	public class ActiveUsersTableModel extends AbstractTableModel {
		private final String[] COL_NAMES = {"Кам", "См", "", "Сессия", "Логин", "IP адрес",
				"Время входа" };
		private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		private boolean isSortAsc = false;
		private int sortColumn = 6;

		java.util.List<Red5User> users;

		public ActiveUsersTableModel(Red5User[] users) {
			int size = users != null ? users.length : 0;
			this.users = new ArrayList<Red5User>(size);
			for (int i = 0; i < size; i++) {
				this.users.add(users[i]);
			}
			sortData();
		}

		public int getRowCount() {
			return users.size();
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
			case 1:
				return Boolean.class;
			default:
				return String.class;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return users.get(rowIndex).cameraSwitchedOn;
			case 1:
				return users.get(rowIndex).watchingYou;
			case 2:
				return users.get(rowIndex).uid.toString();
			case 3:
				return users.get(rowIndex).uid.toString();
			case 4:
				return users.get(rowIndex).name;
			case 5:
				return users.get(rowIndex).ip;
			case 6:
				return df.format(users.get(rowIndex).startTime);
			}
			return null;
		}

		public Red5User getUser(int row) {
			return users.get(row);
		}

		public void setUsers(Red5User[] users) {
			int size = users != null ? users.length : 0;
			this.users = new ArrayList<Red5User>(size);
			for (int i = 0; i < size; i++) {
				this.users.add(users[i]);
			}
			sortData();
		}

		public boolean hasUser(Red5User u) {
			for (Red5User us : users) {
				if (us.uid.equals(u.uid))
					return true;
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
			Collections.sort(users, new UsersComparator(sortColumn, isSortAsc));
		}

		public void fireTableDataChanged() {
			super.fireTableDataChanged();
			setCounterText();
		}
	}

	class ColumnListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			TableColumnModel colModel = usersTable.getColumnModel();
			int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
			int modelIndex = colModel.getColumn(columnModelIndex)
					.getModelIndex();
			if (modelIndex < 0) {
				return;
			}
			if (usersTableModel.getSortColumn() == modelIndex) {
				usersTableModel.setSortAsc(!usersTableModel.isSortAsc());
			} else {
				usersTableModel.setSortColumn(modelIndex);
			}
			for (int i = 0; i < usersTableModel.getColumnCount(); i++) {
				TableColumn column = colModel.getColumn(i);
				int index = column.getModelIndex();
				JLabel renderer = (JLabel) column.getHeaderRenderer();
				renderer.setIcon(usersTableModel.getColumnIcon(index));
			}
			usersTable.getTableHeader().repaint();
			usersTableModel.sortData();
			usersTable.tableChanged(new TableModelEvent(usersTableModel));
			repaint();
		}
	}

	class UsersComparator implements Comparator<Red5User> {

		protected int sortColumn;
		protected boolean isSortAsc;

		public UsersComparator(int sortColumn, boolean sortAsc) {
			this.sortColumn = sortColumn;
			isSortAsc = sortAsc;
		}

		public int compare(Red5User u1, Red5User u2) {
			int res = 0;
			if (u1 == null)
				res = -1;
			else if (u2 == null)
				res = 1;
			else {
				switch (sortColumn) {
				case 0:
					res = Boolean.valueOf(u1.cameraSwitchedOn).compareTo(u2.cameraSwitchedOn);
				case 1:
					res = Boolean.valueOf(u1.watchingYou).compareTo(u2.watchingYou);
				case 2:
				case 3:
					res = u1.uid.compareTo(u2.uid);
					break;
				case 4:
					res = u1.name.compareTo(u2.name);
					break;
				case 5:
					res = u1.ip.compareTo(u2.ip);
					break;
				case 6:
					res = u1.startTime.compareTo(u2.startTime);
					break;
				}
			}
			if (!isSortAsc) {
				res = -res;
			}
			return res;
		}
	}

	class ColorTextPane extends JTextPane {

		public ColorTextPane() {
			super();
		}

		private void appendText(String line, Color myColor, boolean isBold) {
			try {
				StyledDocument myDoc = this.getStyledDocument();
				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, myColor);
				StyleConstants.setFontSize(keyWord, 12);
				StyleConstants.setItalic(keyWord, true);
				StyleConstants.setBold(keyWord, isBold);
				myDoc.insertString(myDoc.getLength(), line, keyWord);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private class KeyHandler implements KeyListener {

		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER && event.isControlDown())
				sendButton.doClick();
		}

		public void keyReleased(KeyEvent event) {
		}

		public void keyTyped(KeyEvent event) {
		}
	}

	class ColorRenderer extends JLabel implements TableCellRenderer {
		private String columnName;
		private ArrayList<String> namesList;

		public ColorRenderer(String columnName, ArrayList<String> namesList) {
			this.columnName = columnName;
			this.namesList = namesList;
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Object columnValue = table.getValueAt(row, table.getColumnModel()
					.getColumnIndex(columnName));
			if (value != null && column != 0 && column != 1 && column != 2)
				setText(value.toString());
			else
				setText(null);
			
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
				for (int i = 0; i < namesList.size(); i++)
					if (columnValue.equals(namesList.get(i))) {
						setBackground(Color.PINK);
						setForeground(Color.BLUE);
					}
			}
			setHorizontalAlignment(JLabel.CENTER);
			setFont(new Font("Arial", Font.ITALIC, 12));
			
			if (column == 2) {
				Red5User user = red5Users.get(value.toString());
				if (user != null && user.photo != null)
					setIcon(user.photo);
				else
					setIcon(defUserIcon);
			} else if (column == 0 && Boolean.TRUE.equals(value)) {
				setIcon(cameraOnIcon);
			} else if (column == 1 && Boolean.TRUE.equals(value)) {
				setIcon(watchingYouIcon);
			}
			else
				setIcon(null);

			return this;
		}
	}

	class TableRowColor {
		public TableRowColor(JTable anyTable, String columnName,
				ArrayList<String> namesList) {
			ColorRenderer anyColorRenderer = new ColorRenderer(columnName,
					namesList);
			for (int i = 0; i < anyTable.getColumnCount(); i++)
				anyTable.getColumn(anyTable.getColumnName(i)).setCellRenderer(
						anyColorRenderer);
		}
	}

	class TablePopupListener extends MouseAdapter {

		private JPopupMenu myPopup;
		private JTable myTable;

		public TablePopupListener(JPopupMenu anyPopup, JTable anyTable) {
			this.myPopup = anyPopup;
			this.myTable = anyTable;
		}

		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			firePopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			firePopup(e);
		}

		public void firePopup(MouseEvent e) {
			if (e.isPopupTrigger() && myTable.getModel().getRowCount() != 0
					&& myTable.getSelectedRow() != -1)
				myPopup.show(myTable, e.getX(), e.getY());

		}
	}

	class periodLabelListener extends MouseAdapter {

		private Color oldColor;

		public void mouseExited(MouseEvent event) {
			if (!event.getComponent().getForeground().equals(Color.BLUE))
				event.getComponent().setForeground(oldColor);
		}

		public void mouseEntered(MouseEvent event) {
			if (!event.getComponent().getForeground().equals(Color.BLUE)) {
				this.oldColor = event.getComponent().getForeground();
				event.getComponent().setForeground(Color.RED);
			}
		}

		public void mouseClicked(MouseEvent event) {
			firePeriod(event);
		}

		public void firePeriod(MouseEvent event) {
			if (event.getSource() == weekLabel)
				if (LOAD_MODE == 2) {
					LOAD_MODE = 3;
					event.getComponent().setForeground(defaultColor);
				} else {
					LOAD_MODE = 2;
					event.getComponent().setForeground(Color.BLUE);
					monthLabel.setForeground(defaultColor);
					allPeriodLabel.setForeground(defaultColor);
				}
			else if (event.getSource() == monthLabel)
				if (LOAD_MODE == 1) {
					LOAD_MODE = 3;
					event.getComponent().setForeground(defaultColor);
				} else {
					LOAD_MODE = 1;
					event.getComponent().setForeground(Color.BLUE);
					weekLabel.setForeground(defaultColor);
					allPeriodLabel.setForeground(defaultColor);
				}
			else if (event.getSource() == allPeriodLabel)
				if (LOAD_MODE == 0) {
					LOAD_MODE = 3;
					event.getComponent().setForeground(defaultColor);
				} else {
					LOAD_MODE = 0;
					event.getComponent().setForeground(Color.BLUE);
					weekLabel.setForeground(defaultColor);
					monthLabel.setForeground(defaultColor);
				}
			loadMessages(allText);
		}
	}

	public int processExit() {
		return ButtonsFactory.BUTTON_NOACTION;
	}

	public Red5User getRed5User(String uid) {
		return red5Users.get(uid);
	}

	public void setRtmpClient(RTMPClient rtmpClient) {
		this.rtmpClient = rtmpClient;
	}
}
