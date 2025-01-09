package kz.tamur.guidesigner.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

public class SearchPropertiesPanel extends JPanel implements ActionListener, ItemListener {
	
	private JButton searchSettingsBtn = ButtonsFactory.createToolButton("SearchSettingsIcon", ".png", "Параметры поиска");
    private JLabel propertiesLabel = kz.tamur.rt.Utils.createLabel("Параметры поиска");
	private JCheckBox caseSensitive = kz.tamur.rt.Utils.createCheckBox("Учет регистра", false);
	private JRadioButton evenOneWord = kz.tamur.rt.Utils.createRadioButton("Содержит любое из этих слов");
	private JRadioButton allWordsWithOrder = kz.tamur.rt.Utils.createRadioButton("Выражение целиком");
	private JRadioButton allWordsWithoutOrder = kz.tamur.rt.Utils.createRadioButton("Выражение без учета порядка слов");
	private JRadioButton regex = kz.tamur.rt.Utils.createRadioButton("Регулярное выражение");
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private JPopupMenu settingsPopup = new JPopupMenu();
	private ButtonGroup group = new ButtonGroup();
	
	private JCheckBox searchAreaAll = kz.tamur.rt.Utils.createCheckBox("Везде", true);
	private JCheckBox searchAreaMethods = kz.tamur.rt.Utils.createCheckBox("Методы", false);
	private JCheckBox searchAreaTriggers = kz.tamur.rt.Utils.createCheckBox("Триггеры", false);
	private JCheckBox searchAreaProcesses = kz.tamur.rt.Utils.createCheckBox("Процессы", false);
	private JCheckBox searchAreaInterfaces = kz.tamur.rt.Utils.createCheckBox("Интерфейсы", false);
	private JCheckBox searchAreaFilters = kz.tamur.rt.Utils.createCheckBox("Фильтры", false);
	private JCheckBox searchAreaReports = kz.tamur.rt.Utils.createCheckBox("Отчеты", false);
	private JCheckBox searchAreaChanges = kz.tamur.rt.Utils.createCheckBox("Изменения", false);

	public SearchPropertiesPanel() {
		super(new BorderLayout());
		setOpaque(isOpaque);
		searchSettingsBtn.addActionListener(this);
		setBorder(BorderFactory.createEmptyBorder());
		add(searchSettingsBtn, BorderLayout.CENTER);
		initPopup();
	}
	
	private void initPopup() {
		settingsPopup.setBorder(new RoundedCornerBorder(Color.GRAY));
		propertiesLabel.setForeground(Color.BLUE);
		caseSensitive.addActionListener(this);
		caseSensitive.setToolTipText("Выбрано значение \"Учитывать регистр\"");
		evenOneWord.setToolTipText("Поиск хотя бы одного слова, содержащегося в выражении");
		allWordsWithOrder.setToolTipText("Поиск всего выражения с учетом порядка следования слов");
		allWordsWithoutOrder.setToolTipText("Поиск всего выражения без учетом порядка следования слов");
		regex.setToolTipText("Поиск по регулярному выражению");
        group.add(evenOneWord);
        group.add(allWordsWithOrder);
        group.add(allWordsWithoutOrder);
        group.add(regex);
        allWordsWithOrder.setSelected(true);
		JPanel attributesPanel = new JPanel(new GridBagLayout());
		attributesPanel.add(propertiesLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(caseSensitive, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
		attributesPanel.add(evenOneWord, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(allWordsWithOrder, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(allWordsWithoutOrder, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(regex, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		searchAreaMethods.setEnabled(false);
		searchAreaTriggers.setEnabled(false);
		searchAreaProcesses.setEnabled(false);
		searchAreaInterfaces.setEnabled(false);
		searchAreaFilters.setEnabled(false);
		searchAreaReports.setEnabled(false);
		searchAreaChanges.setEnabled(false);
		searchAreaAll.addItemListener(this);
		searchAreaMethods.addItemListener(this);
		searchAreaTriggers.addItemListener(this);
		searchAreaProcesses.addItemListener(this);
		searchAreaInterfaces.addItemListener(this);
		searchAreaFilters.addItemListener(this);
		searchAreaReports.addItemListener(this);
		searchAreaChanges.addItemListener(this);
		attributesPanel.add(Utils.createLabel("Область поиска:"), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
		attributesPanel.add(searchAreaAll, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaMethods, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaTriggers, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaProcesses, new GridBagConstraints(0, 10, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaInterfaces, new GridBagConstraints(0, 11, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaFilters, new GridBagConstraints(0, 12, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaReports, new GridBagConstraints(0, 13, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(searchAreaChanges, new GridBagConstraints(0, 14, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	    
		settingsPopup.add(attributesPanel);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == searchSettingsBtn) {
			settingsPopup.show(this, searchSettingsBtn.getLocation().x, searchSettingsBtn.getLocation().y + 35);
		} if (source == caseSensitive) {
			caseSensitive.setToolTipText(caseSensitive.isSelected() ? "Выбрано значение \"Учитывать регистр\"" : "Выбрано значение \"Не учитывать регистр\"");
		}
	}
	
	public int[] getSearchProperties() {
		int[] searchProperties = new int[2];
		searchProperties[0] = caseSensitive.isSelected() ? 1 : 0;
		if (evenOneWord.isSelected()) {
			searchProperties[1] = 0;
		} else if (allWordsWithOrder.isSelected()) {
			searchProperties[1] = 1;
		} else if (allWordsWithoutOrder.isSelected()) {
			searchProperties[1] = 2;
		} else if (regex.isSelected()) {
			searchProperties[1] = 3;
		}
		return searchProperties;
	}
	
	public void setSearchProperties(int[] searchProperties) {
		caseSensitive.setSelected(searchProperties[0] == 1 ? true : false);
		if (searchProperties[1] == 0) {
			group.setSelected(evenOneWord.getModel(), true);
		} else if (searchProperties[1] == 1) {
			group.setSelected(allWordsWithOrder.getModel(), true);
		} else if (searchProperties[1] == 2) {
			group.setSelected(allWordsWithoutOrder.getModel(), true);
		} else {
			group.setSelected(regex.getModel(), true);
		}
	}

	public boolean[] getSearchArea() {
		boolean[] searchArea = new boolean[8];
		searchArea[0] = searchAreaAll.isSelected();
		searchArea[1] = searchAreaMethods.isSelected();
		searchArea[2] = searchAreaTriggers.isSelected();
		searchArea[3] = searchAreaProcesses.isSelected();
		searchArea[4] = searchAreaInterfaces.isSelected();
		searchArea[5] = searchAreaFilters.isSelected();
		searchArea[6] = searchAreaReports.isSelected();
		searchArea[7] = searchAreaChanges.isSelected();
		return searchArea;
	}
	
	public void setSearchArea(boolean[] searchArea) {
		searchAreaAll.setSelected(searchArea[0]);
		searchAreaMethods.setSelected(searchArea[1]);
		searchAreaTriggers.setSelected(searchArea[2]);
		searchAreaProcesses.setSelected(searchArea[3]);
		searchAreaInterfaces.setSelected(searchArea[4]);
		searchAreaFilters.setSelected(searchArea[5]);
		searchAreaReports.setSelected(searchArea[6]);
		searchAreaChanges.setSelected(searchArea[7]);
		setEnableSearchArea(!searchArea[0]);
	}
	
	public String getSearchAreaString() {
		boolean[] searchArea = getSearchArea();
		List<String> res = new ArrayList<>();
		for (int i = 0; i < searchArea.length; i++) {
			if (i == 0 && searchArea[i]) {
				res.add("Везде");
				break;
			} else if (i == 1 && searchArea[i]) {
				res.add("Методы");
			} else if (i == 2 && searchArea[i]) {
				res.add("Триггеры");
			} else if (i == 3 && searchArea[i]) {
				res.add("Процессы");
			} else if (i == 4 && searchArea[i]) {
				res.add("Интерфейсы");
			} else if (i == 5 && searchArea[i]) {
				res.add("Фильтры");
			} else if (i == 6 && searchArea[i]) {
				res.add("Отчеты");
			}
		}
		return res.toString();
	}
	
	public void setEnableSearchArea(boolean isEnable) {
		searchAreaMethods.setEnabled(isEnable);
		searchAreaTriggers.setEnabled(isEnable);
		searchAreaProcesses.setEnabled(isEnable);
		searchAreaInterfaces.setEnabled(isEnable);
		searchAreaFilters.setEnabled(isEnable);
		searchAreaReports.setEnabled(isEnable);
		searchAreaChanges.setEnabled(isEnable);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		if (src == searchAreaAll) {
			if (searchAreaAll.isSelected()) {
				setEnableSearchArea(false);
			} else {
				setEnableSearchArea(true);
			}
		}
	}
}