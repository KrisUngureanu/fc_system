package kz.tamur.guidesigner.users;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS;
import static kz.tamur.comps.Constants.INSETS_2;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;

import static kz.tamur.comps.Constants.BY_SRVNAME;
import static kz.tamur.comps.Constants.BY_DB;
import static kz.tamur.comps.Constants.BY_SESSION;
import static kz.tamur.comps.Constants.BY_CLIENTTYPE;
import static kz.tamur.comps.Constants.BY_LOGIN;
import static kz.tamur.comps.Constants.BY_IP;
import static kz.tamur.comps.Constants.BY_COMP;
import static kz.tamur.comps.Constants.BY_TIME;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.UserSessionValue;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 31.01.2009
 * Time: 13:37:56
 */
public class ActiveUsersPanel extends JPanel implements ActionListener {

    private JTable userTable;
    private JButton refreshBtn = createToolButton("BoxNode", "Обновить");
    private JButton killBtn = createToolButton("Delete", "Отключить пользователей");
    private JButton sendBtn = createToolButton("MailTo", "Отправить сообщение");
    private JButton blockBtn = createToolButton(null, "Заблокировать сервер");
    private InformationalPanel informationalPanel = new InformationalPanel();
    private JPopupMenu popUp = new JPopupMenu();
    private JMenuItem miKillUser = createMenuItem("Отключить пользователей", "Delete");
    private JMenuItem miSendMUser = createMenuItem("Отправить сообщение", "MailTo");

    private static final ImageIcon LOCK = getImageIcon("serverLock");
    private static final ImageIcon UNLOCK = getImageIcon("serverLockGrey");
    private static final ImageIcon SORT_UP = getImageIcon("SortUpLight");
    private static final ImageIcon SORT_DOWN = getImageIcon("SortDownLight");
    
    private JRadioButton bySrvName = new JRadioButton("Имя сервера");
    private JRadioButton byDB = new JRadioButton("База данных");
    private JRadioButton bySession = new JRadioButton("Сессия");
    private JRadioButton byClientType= new JRadioButton("Тип клиента");
    private JRadioButton byLogin = new JRadioButton("Логин");
    private JRadioButton byIP = new JRadioButton("IP адрес");
    private JRadioButton byComp = new JRadioButton("Компьютер");
    private JRadioButton byTime = new JRadioButton("Время входа");
    private ButtonGroup btnGr = new ButtonGroup();
    private JLabel srchLabel = new JLabel("строка поиска: ");
    private JTextField srchText = new JTextField("");
    private JPanel datePanel = new JPanel();
    private DateTimeField begDate = new DateTimeField();
    private DateTimeField endDate = new DateTimeField();
    private CalendarButton calBtn1, calBtn2; 
    private JButton srchBtn = new JButton("  Найти  ");
    private JButton clrBtn = new JButton("  Очистить  ");
    private Date stTime;
    private Date tmpTime;
    private SearchParam srchPar;
    private final int checkUserCount = 500;
    private JLabel noteLabel = new JLabel("Найдено более " + checkUserCount + " активных пользователей. Чтобы их отобразить, нажмите 'Найти'.");
    

    private JTextArea messageArea = new JTextArea("Через одну минуту Вы будете отключены от сервера.\n"
            + "Пожалуйста, сохраните измененные данные и выйдите из программы, для проведения работ на сервере!");

    private Timer timer;
    private int serverBlocked = 0;
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private boolean canSendMsg;
    private boolean canBanUser;
    private boolean canBlockServer;
    private MainFrame.DescLabel counterLabel = kz.tamur.comps.Utils.createDescLabel("");
    private int selRowIdx;
    private int rowCount;
    private ActiveUsersTableModel model;

    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ActiveUsersPanel() {
        super();
        stTime = tmpTime = new Date();
        toolBar.add(sendBtn);
        toolBar.add(killBtn);
        toolBar.add(refreshBtn);
        toolBar.add(blockBtn);
        toolBar.add(informationalPanel);
        toolBar.setBorder(null);
        
        btnGr.add(bySrvName);
        btnGr.add(byDB);
        btnGr.add(bySession);
        btnGr.add(byClientType);
        btnGr.add(byLogin);
        btnGr.add(byIP);
        btnGr.add(byComp);
        btnGr.add(byTime);
        
        JPanel srchPanel = new JPanel();
        srchPanel.setBorder(new EmptyBorder(5,5,10,5));
        srchPanel.setLayout(new GridBagLayout());
        JPanel rdBtnPanel = new JPanel();
        rdBtnPanel.setLayout(new BoxLayout(rdBtnPanel, BoxLayout.X_AXIS));
        rdBtnPanel.add(bySrvName);
        rdBtnPanel.add(byDB);
        rdBtnPanel.add(bySession);
        rdBtnPanel.add(byClientType);
        rdBtnPanel.add(byLogin);
        rdBtnPanel.add(byIP);
        rdBtnPanel.add(byComp);
        rdBtnPanel.add(byTime);
//        Collections.list(btnGr.getElements()).get(0).setSelected(true);
        
        EmptyBorder eb = new EmptyBorder(0,0,0,5);
        bySrvName.setBorder(eb);
        byDB.setBorder(eb);
        bySession.setBorder(eb);
        byClientType.setBorder(eb);
        byLogin.setBorder(eb);
        byIP.setBorder(eb);
        byComp.setBorder(eb);
        byTime.setBorder(eb);
        
        bySrvName.addActionListener(this);
        byDB.addActionListener(this);
        bySession.addActionListener(this);
        byClientType.addActionListener(this);
        byLogin.addActionListener(this);
        byIP.addActionListener(this);
        byComp.addActionListener(this);
        byTime.addActionListener(this);
        
		begDate.setSize(new Dimension(150, 20));
        begDate.setMinimumSize(new Dimension(150, 20));
        endDate.setSize(new Dimension(150, 20));
		endDate.setMinimumSize(new Dimension(150, 20));
		datePanel.setLayout(new GridBagLayout());
		datePanel.add(new JLabel("c:"), new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(begDate, new GridBagConstraints(1, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn1=new CalendarButton("c:");
		calBtn1.setDataField(begDate);
		datePanel.add(calBtn1, new GridBagConstraints(2, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(new JLabel("по:"), new GridBagConstraints(3, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		datePanel.add(endDate, new GridBagConstraints(4, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn2=new CalendarButton("по:");
		calBtn2.setDataField(endDate);
		datePanel.add(calBtn2, new GridBagConstraints(5,0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
		calBtn1.addActionListener(this);
		
		JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        srchBtn.setPreferredSize(new Dimension(70,25));
        clrBtn.setPreferredSize(new Dimension(90, 25));
        btnPanel.add(srchBtn, new CnrBuilder().ins(0, 0, 0, 5).build()); 
        btnPanel.add(clrBtn);
        
        srchPanel.add(rdBtnPanel, new CnrBuilder().x(0).y(0).w(4).anchor(CENTER).ins(5, 5, 0, 5).build());
        
        srchPanel.add(datePanel, new CnrBuilder().x(0).y(1).w(3).anchor(CENTER).ins(5,20,0,5).build());
        datePanel.setVisible(false);
        
        srchPanel.add(srchLabel, new CnrBuilder().x(0).y(1).anchor(CENTER).ins(5, 20, 0, 5).build());
        srchText.setMinimumSize(new Dimension(250, 25)); 
        srchText.setPreferredSize(new Dimension(250, 25));
        
        srchPanel.add(srchText, new CnrBuilder().x(1).y(1).anchor(CENTER).ins(5, 5, 0, 5).build());
        srchPanel.add(btnPanel, new CnrBuilder().x(3).y(1).anchor(EAST).ins(5, 5, 0, 5).build());
        srchText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				srchBtn.doClick();
				
			}
		});
        
    	begDate.setEditable(false);
		endDate.setEditable(false);
		calBtn1.setEnabled(false);
		calBtn2.setEnabled(false);
		srchText.setEditable(false);
        
        User user = Kernel.instance().getUser();
        canSendMsg = user.hasRight(Or3RightsNode.REPLICATION_SENDMSG_RIGHT);
        canBanUser = user.hasRight(Or3RightsNode.REPLICATION_BANUSER_RIGHT);
        canBlockServer = user.hasRight(Or3RightsNode.REPLICATION_BLOCKSERVER_RIGHT);
        setLayout(new GridBagLayout());

        JLabel l = new JLabel("Активные пользователи:");

        Kernel krn = Kernel.instance();
        UserSessionValue[] us = new UserSessionValue[0];        
        try {
            us = krn.getUserSessions();
            if(us.length > checkUserCount) {
            	us = null;
            } else {
            	srchPar = new SearchParam(BY_TIME, null, Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(stTime));
            	noteLabel.setVisible(false);
            }
            serverBlocked = krn.isServerBlocked();
        } catch (KrnException e) {
            e.printStackTrace();
        }

        if (serverBlocked > 0) {
            blockBtn.setToolTipText("Разблокировать сервер");
        }
        blockBtn.setIcon(serverBlocked > 0 ? UNLOCK : LOCK);        
        model = new ActiveUsersTableModel(us);
        userTable = new JTable(model) {
            public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);
                setCounterText();
            }
        };

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });

        JTableHeader header = userTable.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn tc = userTable.getColumnModel().getColumn(i);
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            tc.setHeaderRenderer(r);
            if (i == model.getSortColumn())
                r.setIcon(model.getColumnIcon(i));
        }

        userTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userTable.setDefaultRenderer(String.class, new CellRenderer());
        JPanel tablePain = new JPanel();
        tablePain.setLayout(new BorderLayout(3, 3));
        JPanel counter = new JPanel();
        counter.setLayout(new BorderLayout(3, 3));
        counter.add(counterLabel, BorderLayout.EAST);
        JScrollPane sp = new JScrollPane(userTable);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        sp.setMinimumSize(new Dimension(600, 200));
        tablePain.add(sp);
        messageArea.setPreferredSize(new Dimension(300, 100));
        messageArea.setMinimumSize(new Dimension(200, 100));
        messageArea.setMaximumSize(new Dimension(400, 100));
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setBorder(new LineBorder(Color.black, 1, true));

        refreshBtn.addActionListener(this);
        timer = new Timer(20000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTableByTime();
            }
        });
        timer.setDelay(20000);
        timer.setInitialDelay(20000);
        timer.setRepeats(true);
        timer.start();

        blockBtn.addActionListener(this);
        sendBtn.addActionListener(this);
        killBtn.addActionListener(this);
        miKillUser.addActionListener(this);
        miSendMUser.addActionListener(this);
        srchBtn.addActionListener(this);
        clrBtn.addActionListener(this);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());
        listPanel.add(srchPanel, new CnrBuilder().x(0).y(0).anchor(CENTER).ins(5, 5, 0, 5).build());
        listPanel.add(l, new CnrBuilder().x(0).y(1).anchor(CENTER).ins(5, 5, 0, 5).build());
        listPanel.add(tablePain, new CnrBuilder().x(0).y(2).fill(BOTH).wtx(1).wty(1).build());
        noteLabel.setFont(new Font(noteLabel.getFont().getName(), Font.PLAIN, 36));
        noteLabel.setForeground(Color.green);
        listPanel.add(noteLabel, new CnrBuilder().x(0).y(3).anchor(CENTER).build());
        add(toolBar, new CnrBuilder().x(0).y(0).anchor(WEST).build());
        add(counter, new CnrBuilder().x(1).y(0).anchor(EAST).build());
        add(listPanel, new CnrBuilder().x(0).y(1).w(2).fill(BOTH).anchor(WEST).wtx(1).wty(1).ins(5, 5, 0, 5).build());

        popUp.add(miKillUser);
        popUp.add(miSendMUser);

        sendBtn.setVisible(canSendMsg);
        messageArea.setVisible(canSendMsg);
        blockBtn.setVisible(canBlockServer);
        killBtn.setVisible(canBanUser);
        setCounterText();
        setOpaque(isOpaque);
        // Установка прозрачности, зависящей от глобальных настроек системы
        listPanel.setOpaque(isOpaque);
        tablePain.setOpaque(isOpaque);
        counter.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
    }
    
    private void critSelectionChanged() {    	
    	if(byTime.isSelected()) {
    		begDate.setEditable(true);
    		endDate.setEditable(true);
    		calBtn1.setEnabled(true);
    		calBtn2.setEnabled(true);
    		datePanel.setVisible(true);
    		srchLabel.setVisible(false);
    		srchText.setVisible(false);
    	} else {
    		srchText.setEditable(true);
    		datePanel.setVisible(false);
    		srchLabel.setVisible(true);
    		srchText.setVisible(true);
    	}
    }
    
    private int getSelectedCrit() {
    	int res = -1;
    	ButtonModel selected = btnGr.getSelection();
    	if(selected != null) {
    		if(bySrvName.isSelected()) {
    			return BY_SRVNAME;
    		} else if(byDB.isSelected()) {
    			return BY_DB;
    		}else if(bySession.isSelected()) {
    			return BY_SESSION;
    		}else if(byClientType.isSelected()) {
    			return BY_CLIENTTYPE;
    		}else if(byLogin.isSelected()) {
    			return BY_LOGIN;
    		}else if(byIP.isSelected()) {
    			return BY_IP;
    		}else if(byComp.isSelected()) {
    			return BY_COMP;
    		} else if(byTime.isSelected()) {
    			return BY_TIME;
    		}
    	}
    	return res;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Kernel krn = Kernel.instance();
        if (sendBtn.equals(src) || miSendMUser.equals(src)) {
            int[] rows = userTable.getSelectedRows();
            if (rows == null || rows.length == 0) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "Пользователи не выбраны!");
            } else {
                DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Редактирование сообщения",
                        new JScrollPane(messageArea));
                dlg.setSize(400, 500);
                dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(400, 500));
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    try {
                        for (int row : rows) {
                            krn.sendMessage((UUID) userTable.getModel().getValueAt(row, 2), messageArea.getText());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (refreshBtn.equals(src)) {
        	tmpTime = new Date();
        	SearchParam srchPar = new SearchParam(BY_TIME, null, Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(tmpTime));
            refreshTable(srchPar);
        } else if (blockBtn.equals(src)) {
            serverBlocked = krn.blockServer(serverBlocked > 0);
            blockBtn.setToolTipText(serverBlocked > 0 ? "Разблокировать сервер" : "Заблокировать сервер");
            blockBtn.setIcon(serverBlocked > 0 ? UNLOCK : LOCK);
        } else if (killBtn.equals(src) || miKillUser.equals(src)) {
            int messRes = MessagesFactory.showMessageDialog(this.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                    "Вы уверены, что хотите отключить выбранных пользователей?");
            if (ButtonsFactory.BUTTON_YES == messRes) {
                int[] rows = userTable.getSelectedRows();
                if (rows == null || rows.length == 0)
                    JOptionPane.showMessageDialog(getTopLevelAncestor(), "Пользователи не выбраны!");
                try {
                    for (int row : rows) {
                         UUID uuid = (UUID) userTable.getModel().getValueAt(row, 2);
                        if (uuid.equals(Kernel.instance().getUUID())) {
                            System.out.println("Нельзя отключить собственную сессию!");
                        }else {
                            krn.killUserSession(uuid, false);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                refreshTableByTime();
            }
        } else if(srchBtn.equals(src)) {
        	
        	int selected = getSelectedCrit();
        	tmpTime = new Date();
        	String stTimeStr = Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(tmpTime);
        	SearchParam srchPar = new SearchParam(BY_TIME, null, stTimeStr);
        	noteLabel.setVisible(false);
        	String txt = srchText.getText();
        	if(selected != -1) {
        		if(txt != null && txt.length() > 0 && !byTime.isSelected()) {
        			srchPar = new SearchParam(selected, txt, stTimeStr);
            		refreshTable(srchPar);
            	} else if(byTime.isSelected()) {
            		String fVal = null;
            		String lVal = null;
            		if(begDate.getValue() != null) {
            			fVal = Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(begDate.getValue());
            		}
            		if(endDate.getValue() != null) {
            			if(endDate.getValue().after(tmpTime))
            				lVal = stTimeStr;
            			else 
            				lVal = Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(endDate.getValue());
            		}
            		
            		srchPar.txt = fVal;
            		srchPar.txt2 = lVal != null? lVal : stTimeStr;
            		refreshTable(srchPar);
        		} else {
        			refreshTable(srchPar);
        		}
        	} else {
        		refreshTable(srchPar);
        	}
        	
        	
        } else if(bySrvName.equals(src) || byDB.equals(src) || bySession.equals(src) || byClientType.equals(src) || byLogin.equals(src) || byIP.equals(src) || byComp.equals(src) || byTime.equals(src)) {
        	critSelectionChanged();
        } else if(clrBtn.equals(src)) {
        	begDate.setEditable(false);
    		endDate.setEditable(false);
    		calBtn1.setEnabled(false);
    		calBtn2.setEnabled(false);
    		srchText.setEditable(false);
        	btnGr.clearSelection();
        	srchText.setText("");
        	begDate.setValue(null);
        	endDate.setValue(null);
        	srchBtn.doClick();
        }
    }
    
    UserSessionValue[] getUserSessionBySrchParam(Kernel krn, SearchParam param) {
    	try {
    		if(param != null && param.type != -1) {
    			return krn.getUserSessions(param.type, param.txt, param.txt2);
    		} else {
    			return krn.getUserSessions();
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public void refreshTableByTime() {
    	try{
    		Kernel krn = Kernel.instance();
        	String timeStr = Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).format(stTime);
        	UserSessionValue[] oldUs = null;
        	if(srchPar != null) {
        		oldUs = getUserSessionBySrchParam(krn, srchPar);
        	}
        	UserSessionValue[] newUs = krn.getUserSessions(BY_TIME, timeStr, null);
        	ActiveUsersTableModel tm = (ActiveUsersTableModel) userTable.getModel();
        	int len = oldUs != null ? newUs != null? oldUs.length + newUs.length: oldUs.length : newUs != null? newUs.length : 0; 
        	if((len != tm.getRowCount())) {
        		tm.addUsersToTop(oldUs, newUs);
            	tm.fireTableDataChanged();
            	return;
        	}
        	if(newUs != null)
        	for (UserSessionValue u : newUs) {
        		if (!tm.hasUser(u)) {
        			tm.addUsersToTop(oldUs, newUs);
        			tm.fireTableDataChanged();
        			return;
        		}
        	}
        	if(oldUs != null)
        	for (UserSessionValue u : oldUs) {
        		if (!tm.hasUser(u)) {
        			tm.addUsersToTop(oldUs, newUs);
        			tm.fireTableDataChanged();
        			return;
        		}
        	}
        	
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
    
    public void refreshTable(SearchParam srchPar) {
    	try {
            Kernel krn = Kernel.instance();
            UserSessionValue[] us = new UserSessionValue[0];
            if(srchPar != null) {
            	us = krn.getUserSessions(srchPar.type, srchPar.txt, srchPar.txt2);
            } 
            if(us != null && us.length > checkUserCount) {
            	int messRes = MessagesFactory.showMessageDialog(this.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                        "Найдено " + us.length + " пользователей. Вы уверены, что хотите всех загрузить?");
                if (ButtonsFactory.BUTTON_YES == messRes) {
                	stTime = tmpTime;
                	this.srchPar = srchPar;
                	ActiveUsersTableModel tm = (ActiveUsersTableModel) userTable.getModel();                     
                    tm.setUsers(us);
                    tm.fireTableDataChanged();
                }
            } else {
            	stTime = tmpTime;
            	this.srchPar = srchPar;
            	ActiveUsersTableModel tm = (ActiveUsersTableModel) userTable.getModel(); 
                
                tm.setUsers(us);
                tm.fireTableDataChanged();
                
                if(us == null || us.length == 0) {
                	JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "активные пользователи не найдены!");
                    return;
                }
            }            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refreshTable() {
        try {
            Kernel krn = Kernel.instance();
            UserSessionValue[] us = krn.getUserSessions();
            ActiveUsersTableModel tm = (ActiveUsersTableModel) userTable.getModel();
            if (us.length != tm.getRowCount()) {
                tm.setUsers(us);
                tm.fireTableDataChanged();
                return;
            }
            for (UserSessionValue u : us) {
                if (!tm.hasUser(u)) {
                    tm.setUsers(us);
                    tm.fireTableDataChanged();
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setCounterText() {
        rowCount = userTable.getModel().getRowCount();
        selRowIdx = userTable.getSelectedRow() + 1;
        counterLabel.setText(selRowIdx + " / " + rowCount + " ");
        sendBtn.setEnabled(selRowIdx > 0);
        killBtn.setEnabled(selRowIdx > 0);
    }

    private class ActiveUsersTableModel extends AbstractTableModel {
        private final String[] COL_NAMES = { "Имя сервера","База данных", "Сессия", "Тип клиента", "Логин", "IP адрес", "Компьютер",
                "Время входа" };
        private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        private boolean isSortAsc = false;
        private int sortColumn = 7;

        java.util.List<UserSessionValue> users;

        public ActiveUsersTableModel(UserSessionValue[] users) {
            int size = users != null ? users.length : 0;
            this.users = new ArrayList<UserSessionValue>(size);
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
                return Long.class;
            default:
                return String.class;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                return users.get(rowIndex).serverId;
            case 1:
                return users.get(rowIndex).dsName;
            case 2:
                return rowIndex < users.size() ? users.get(rowIndex).id : null;
            case 3:
                return users.get(rowIndex).typeClient;
            case 4:
                return users.get(rowIndex).name;
            case 5:
                return users.get(rowIndex).ip;
            case 6:
                return users.get(rowIndex).pcName;
            case 7:
                return df.format(users.get(rowIndex).startTime);
            }
            return null;
        }
        
        public void addUsersToTop(UserSessionValue[] oldUsers, UserSessionValue[] newUsers) {        	
        	this.users = new ArrayList<>();
        	int size = oldUsers != null ? oldUsers.length : 0;
        	for(int i = 0; i < size; i++) {
        		this.users.add(oldUsers[i]);
        	}
        	size = newUsers != null? newUsers.length : 0;
        	for(int i = 0; i < size; i++) {
        		this.users.add(newUsers[i]);
        	}
        	sortData();
        }

        public void setUsers(UserSessionValue[] users) {
            int size = users != null ? users.length : 0;
            this.users = new ArrayList<UserSessionValue>(size);
            for (int i = 0; i < size; i++) {
                this.users.add(users[i]);
            }
            sortData();
        }

        public boolean hasUser(UserSessionValue u) {
            for (UserSessionValue us : users) {
                if (us.id.equals(u.id)) {
                    return true;
                }
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
            if (column == sortColumn) {
                return isSortAsc ? SORT_UP : SORT_DOWN;
            }
            return null;
        }

        public void sortData() {
            Collections.sort(users, new UsersComparator(sortColumn, isSortAsc));
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
            setCounterText();
        }
    }

    class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = userTable.getColumnModel();
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
            userTable.getTableHeader().repaint();
            model.sortData();
            userTable.tableChanged(new TableModelEvent(model));
            repaint();
        }
    }

    class UsersComparator implements Comparator<UserSessionValue> {

        protected int sortColumn;
        protected boolean isSortAsc;

        public UsersComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(UserSessionValue u1, UserSessionValue u2) {
            int res = 0;
            if (u1 == null)
                res = -1;
            else if (u2 == null)
                res = 1;
            else {
                switch (sortColumn) {
                case 0:
                    res = u1.serverId.compareTo(u2.serverId);
                    break;
                case 1:
                    res = u1.dsName.compareTo(u2.dsName);
                    break;
                case 2:
                    res = u1.id.compareTo(u2.id);
                    break;
                case 3:
                    res = u1.typeClient.compareTo(u2.typeClient);
                    break;
                case 4:
                    res = u1.name.compareTo(u2.name);
                    break;
                case 5:
                    res = u1.ip.compareTo(u2.ip);
                    break;
                case 6:
                    res = u1.pcName.compareTo(u2.pcName);
                    break;
                case 7:
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

    public int processExit() {
        return ButtonsFactory.BUTTON_NOACTION;
    }

    protected void showPopup(MouseEvent e) {
        int[] rows = userTable.getSelectedRows();
        if (rows != null && rows.length > 0) {
            popUp.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    class SearchParam {
    	int type;
    	String txt;
    	String txt2;
    	
    	SearchParam(int type, String txt, String txt2){
    		this.type = type;
    		this.txt = txt;
    		this.txt2 = txt2;
    	}
    }
    
    public Color getForeground(int row, boolean isSelected) {
		Color fontColor;
		if (isSelected)
			fontColor = model.users.get(row).startTime.after(stTime)? Color.BLUE :Color.WHITE;
    	else
    		fontColor = model.users.get(row).startTime.after(stTime)? Color.BLUE :Color.BLACK;
		return fontColor;
	}
    
    public Color getBackground(int row, boolean isSelected) {
		Color backgroundColor = Color.WHITE;

		if (!isSelected)
			backgroundColor = Color.WHITE;
		else
			backgroundColor = Utils.getMidSysColor();
    	
		return backgroundColor;
	}
    
    class CellRenderer implements TableCellRenderer{

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel component = new JLabel();
			component.setFont(new Font("Dialog", Font.PLAIN, 12));
			component.setOpaque(true);
        	component.setBackground(getBackground(row, isSelected));
	    	component.setForeground(getForeground(row, isSelected));	
	    	ActiveUsersTableModel tblModel = (ActiveUsersTableModel) table.getModel();
	    	Object obj = tblModel.getValueAt(row, column);
	    	component.setText(obj != null ? obj.toString() : "");
	    	component.setIcon(null);
	    	
			return component;
		}
    	
    }
}
