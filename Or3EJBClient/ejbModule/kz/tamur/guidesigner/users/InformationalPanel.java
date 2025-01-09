package kz.tamur.guidesigner.users;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;

public class InformationalPanel extends JPanel implements ActionListener {
	
	private JButton informationalPanelBtn = ButtonsFactory.createToolButton("info1", ".png", "Информационныя панель");
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private JPopupMenu informationalPopup = new JPopupMenu();
	private DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private Kernel kernel = Kernel.instance();
	
	private String loggedInUsersCountText = "Количество вошедших пользователей: ";
	private String loggedOutUsersCountText = "Количество вышедших пользователей: ";
	private String loggedInTodayUsersCountText = "Количество вошедших пользователей за сегодня: ";
	private String loggedOutTodayUsersCountText = "Количество вышедших пользователей за сегодня: ";
	
	private JLabel loggedInUsersCountLabel = kz.tamur.rt.Utils.createLabel();
	private JLabel loggedOutUsersCountLabel = kz.tamur.rt.Utils.createLabel();
	private JLabel loggedInTodayUsersCountLabel = kz.tamur.rt.Utils.createLabel();
	private JLabel loggedOutTodayUsersCountLabel = kz.tamur.rt.Utils.createLabel();
	
	public InformationalPanel() {
		super(new BorderLayout());
		setOpaque(isOpaque);
		informationalPanelBtn.addActionListener(this);
		setBorder(BorderFactory.createEmptyBorder());
		add(informationalPanelBtn, BorderLayout.CENTER);
		initPopup();
	}
	
	private void initPopup() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		Utils.setAllSize(mainPanel, new Dimension(300, 80));
		
		JLabel serverStartupDatetimeLabel = kz.tamur.rt.Utils.createLabel("Время запуска системы: " + format.format(kernel.getServerStartupDatetime()));
		serverStartupDatetimeLabel.setForeground(Color.RED);
		mainPanel.add(serverStartupDatetimeLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		mainPanel.add(loggedInUsersCountLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(loggedOutUsersCountLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(loggedInTodayUsersCountLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(loggedOutTodayUsersCountLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		informationalPopup.add(mainPanel);
	}
	
	private void updateInfo() {
		long loggedInUsersCount = kernel.getLoggedInUsersCount(null);
		loggedInUsersCountLabel.setText(loggedInUsersCountText + loggedInUsersCount);
		
		long loggedOutUsersCount = kernel.getLoggedOutUsersCount(null);
		loggedOutUsersCountLabel.setText(loggedOutUsersCountText + loggedOutUsersCount);

		Calendar calendar = Calendar.getInstance();  
		calendar.set(Calendar.HOUR_OF_DAY, 0);  
		calendar.set(Calendar.MINUTE, 0);  
		calendar.set(Calendar.SECOND, 0);  
		calendar.set(Calendar.MILLISECOND, 0);
		
		long loggedInTodayUsers = kernel.getLoggedInUsersCount(calendar.getTime());
		loggedInTodayUsersCountLabel.setText(loggedInTodayUsersCountText + loggedInTodayUsers);

		long loggedOutTodayUsers = kernel.getLoggedOutUsersCount(calendar.getTime());
		loggedOutTodayUsersCountLabel.setText(loggedOutTodayUsersCountText + loggedOutTodayUsers);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == informationalPanelBtn) {
			updateInfo();
			informationalPopup.show(this, informationalPanelBtn.getLocation().x, informationalPanelBtn.getLocation().y + 35);
		}
	}
}