package kz.tamur.guidesigner.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

public class IndexPropertiesPanel extends JPanel implements ActionListener {
	
	private JButton indexSettingsBtn = ButtonsFactory.createToolButton("IndexSettingsIcon", ".png", "Параметры индексации");
    private JLabel propertiesLabel = kz.tamur.rt.Utils.createLabel("Параметры индексации");
	private JButton selectAllBtn = ButtonsFactory.createToolButton("SelectAllIcon", ".png", "Выбрать все атрибуты");
	private JButton deselectAllBtn = ButtonsFactory.createToolButton("DeselectAllIcon", ".png", "Убрать все атрибуты");
	private JButton indexingInfoBtn = ButtonsFactory.createToolButton("IndexingInfoIcon", ".png", "Информация о последней индексации");
	private JCheckBox methodsIndexing = kz.tamur.rt.Utils.createCheckBox("Индексация методов", true);
	private JCheckBox triggersIndexing = kz.tamur.rt.Utils.createCheckBox("Индексация триггеров", true);
	private JCheckBox changesIndexing = kz.tamur.rt.Utils.createCheckBox("Индексация изменений", false);
	
	private JRadioButton analyzerStandard = kz.tamur.rt.Utils.createRadioButton("StandardAnalyzer");
    private JRadioButton analyzerWhitespace = kz.tamur.rt.Utils.createRadioButton("WhitespaceAnalyzer");
	private JRadioButton analyzerRussian = kz.tamur.rt.Utils.createRadioButton("RussianAnalyzer");
	private ButtonGroup group = new ButtonGroup();
	private Map<Integer, Boolean> checkBoxValuesMap = new HashMap<Integer, Boolean>();
    private List<KrnAttribute> attributes;
    private Map<Long,String> classNames;
    private JTextArea lastIndexingInfo;
    private JScrollPane scrollPaneTA;
    private CheckList checkList;
    private JScrollPane scrollPaneCL;
	private Timer changeTimer;
	
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private JPopupMenu settingsPopup = new JPopupMenu();

	public IndexPropertiesPanel(List<KrnAttribute> attributes,Map<Long,String> classNames) {
		super(new BorderLayout());
		this.attributes = attributes;
		this.classNames=classNames;
		setOpaque(isOpaque);
		indexSettingsBtn.addActionListener(this);
		selectAllBtn.addActionListener(this);
		deselectAllBtn.addActionListener(this);
		indexingInfoBtn.addActionListener(this);
		setBorder(BorderFactory.createEmptyBorder());
		add(indexSettingsBtn, BorderLayout.CENTER);
		initPopup();
	}
	
	private void initPopup() {
		settingsPopup.setBorder(new RoundedCornerBorder(Color.GRAY));
		settingsPopup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent event) {
				if (scrollPaneTA.isVisible()) {
					lastIndexingInfo.setText("");
					if (changeTimer != null && changeTimer.isRunning()) {
						changeTimer.stop();
					}
					changeTimer = new Timer(1, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							scrollPaneTA.setVisible(false);
							scrollPaneTA.setMinimumSize(new Dimension(scrollPaneTA.getMinimumSize().width - 3, scrollPaneTA.getMinimumSize().height - 5));
							scrollPaneTA.setMaximumSize(new Dimension(scrollPaneTA.getMaximumSize().width - 3, scrollPaneTA.getMaximumSize().height - 5));
							scrollPaneTA.setPreferredSize(new Dimension(scrollPaneTA.getPreferredSize().width - 3, scrollPaneTA.getPreferredSize().height - 5));
							scrollPaneTA.setVisible(true);
							settingsPopup.setVisible(false);     
							IndexPropertiesPanel.this.setMinimumSize(new Dimension(IndexPropertiesPanel.this.getMinimumSize().width - 3, IndexPropertiesPanel.this.getMinimumSize().height - 5));
							IndexPropertiesPanel.this.setMaximumSize(new Dimension(IndexPropertiesPanel.this.getMaximumSize().width - 3, IndexPropertiesPanel.this.getMaximumSize().height - 5));
							IndexPropertiesPanel.this.setPreferredSize(new Dimension(IndexPropertiesPanel.this.getPreferredSize().width  - 3, IndexPropertiesPanel.this.getPreferredSize().height - 5));
							if (scrollPaneTA.getHeight() == 0) {
								if (changeTimer.isRunning()) {
									changeTimer.stop();
									scrollPaneTA.setVisible(false);
									return;
								}
							}
							settingsPopup.setVisible(true); 
						}
					});
					changeTimer.start();
				}
			}
		});
		
		JPanel attributesPanel = new JPanel(new GridBagLayout());
		
		propertiesLabel.setForeground(Color.BLUE);
		attributesPanel.add(propertiesLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		group.add(analyzerStandard);
		analyzerStandard.setSelected(true);
        group.add(analyzerWhitespace);
        analyzerWhitespace.setEnabled(false);
        group.add(analyzerRussian);
        analyzerRussian.setEnabled(false);
        attributesPanel.add(analyzerStandard, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		attributesPanel.add(analyzerWhitespace, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		attributesPanel.add(analyzerRussian, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	    
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		Dimension dimension = new Dimension(18, 18);
		selectAllBtn.setMinimumSize(dimension);
		deselectAllBtn.setMinimumSize(dimension);
		selectAllBtn.setMaximumSize(dimension);
		deselectAllBtn.setMaximumSize(dimension);
		selectAllBtn.setPreferredSize(dimension);
		deselectAllBtn.setPreferredSize(dimension);
		buttonsPanel.add(selectAllBtn);
		buttonsPanel.add(deselectAllBtn);
		buttonsPanel.add(indexingInfoBtn);
		
		lastIndexingInfo = new JTextArea();
		lastIndexingInfo.setEditable(false);
		lastIndexingInfo.setFont(new Font("Arial", Font.PLAIN, 10));
		lastIndexingInfo.setForeground(Color.DARK_GRAY);
		scrollPaneTA = new JScrollPane(lastIndexingInfo);
		Dimension dimensionSPTA = new Dimension(150, 0);
		scrollPaneTA.setVisible(false);
		scrollPaneTA.setMinimumSize(dimensionSPTA);
		scrollPaneTA.setMaximumSize(dimensionSPTA);
		scrollPaneTA.setPreferredSize(dimensionSPTA);

		
		checkList = new CheckList();
		scrollPaneCL = new JScrollPane(checkList);
		scrollPaneCL.setAlignmentX(LEFT_ALIGNMENT);
		scrollPaneCL.getVerticalScrollBar().setUnitIncrement(16);
		scrollPaneCL.setBorder(BorderFactory.createEmptyBorder());
		Dimension dimensionSPCL = new Dimension(250, 300);
		scrollPaneCL.setMinimumSize(dimensionSPCL);
		scrollPaneCL.setMaximumSize(dimensionSPCL);
		scrollPaneCL.setPreferredSize(dimensionSPCL);
		
		attributesPanel.add(buttonsPanel, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, -5, 0, 0), 0, 0));
		attributesPanel.add(scrollPaneTA, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, -15, 0, 0), 0, 0));
		attributesPanel.add(scrollPaneCL, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, -15, 0, 0), 0, 0));
		methodsIndexing.setForeground(Color.RED);
		attributesPanel.add(methodsIndexing, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		triggersIndexing.setForeground(Color.RED);
		attributesPanel.add(triggersIndexing, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		changesIndexing.setForeground(Color.RED);
		attributesPanel.add(changesIndexing, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		settingsPopup.add(attributesPanel);
	}
	
	public void setUserConfiguration(Map<String, String> configuration) {
		String analyzerType = configuration.get("Analyzer Type");
		if (analyzerType.equals("0")) {
			group.setSelected(analyzerStandard.getModel(), true);
		} else if (analyzerType.equals("1")) {
			group.setSelected(analyzerWhitespace.getModel(), true);
		} else {
			group.setSelected(analyzerRussian.getModel(), true);
		}
		char[] attributesStatus = configuration.get("Attributes Status").toCharArray();
		for (int i = 0; i < attributesStatus.length; i++) {
			((JCheckBox) checkList.getComponent(i)).setSelected(attributesStatus[i] == '1' ? true : false);
		}
		methodsIndexing.setSelected(configuration.get("Methods Status").equals("1") ? true : false);
		triggersIndexing.setSelected("1".equals(configuration.get("Triggers Status")) ? true : false);
		changesIndexing.setSelected("1".equals(configuration.get("Changes Status")) ? true : false);
	}

	public Map<String, String> getUserConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		StringBuilder attributesStatus = new StringBuilder(); 
		for (boolean status: checkBoxValuesMap.values()) {
			attributesStatus.append(status ? 1 : 0);
		}
		configuration.put("Analyzer Type", String.valueOf(getIndexingAnalyzer()));
		configuration.put("Attributes Status", attributesStatus.toString());
		configuration.put("Methods Status", String.valueOf(isIdexingMethods()));
		configuration.put("Triggers Status", String.valueOf(isIdexingTriggers()));
		configuration.put("Changes Status", String.valueOf(isIdexingChanges()));
		return configuration;
	}
	
	public Map<Integer, Boolean> getNotIndexedAttributes() {
		Map<Integer, Boolean> notIndexedAttributes = new HashMap<Integer, Boolean>();
		for (int i = 0; i < checkBoxValuesMap.size(); i++) {
			if (!checkBoxValuesMap.get(i)) {
				notIndexedAttributes.put(i, false);
			}
		}
		return notIndexedAttributes;
	}
	
	public int isIdexingMethods() {
		return methodsIndexing.isSelected() ? 1 : 0;
	}
	
	public int isIdexingTriggers() {
		return triggersIndexing.isSelected() ? 1 : 0;
	}
	
	public int isIdexingChanges() {
		return changesIndexing.isSelected() ? 1 : 0;
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == indexSettingsBtn) {
			settingsPopup.show(this, indexSettingsBtn.getLocation().x, indexSettingsBtn.getLocation().y + 35);
		} else if (source == selectAllBtn) {
			changeStatus(true);
		} else if (source == deselectAllBtn) {
			changeStatus(false);
		} else if (source == indexingInfoBtn) {
			final boolean isVisible = scrollPaneTA.isVisible();
			try {
				lastIndexingInfo.setText(isVisible ? "" : Kernel.instance().getLastIndexingInfo());
				if (changeTimer != null && changeTimer.isRunning()) {
					changeTimer.stop();
				}
				changeTimer = new Timer(1, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						scrollPaneTA.setVisible(false);
						scrollPaneTA.setMinimumSize(new Dimension(scrollPaneTA.getMinimumSize().width + (isVisible ? -3 : 3), scrollPaneTA.getMinimumSize().height + (isVisible ? -5 : 5)));
						scrollPaneTA.setMaximumSize(new Dimension(scrollPaneTA.getMaximumSize().width + (isVisible ? -3 : 3), scrollPaneTA.getMaximumSize().height + (isVisible ? -5 : 5)));
						scrollPaneTA.setPreferredSize(new Dimension(scrollPaneTA.getPreferredSize().width + (isVisible ? -3 : 3), scrollPaneTA.getPreferredSize().height + (isVisible ? -5 : 5)));
						scrollPaneTA.setVisible(true);
						settingsPopup.setVisible(false);     
						IndexPropertiesPanel.this.setMinimumSize(new Dimension(IndexPropertiesPanel.this.getMinimumSize().width + (isVisible ? -3 : 3), IndexPropertiesPanel.this.getMinimumSize().height + (isVisible ? -5 : 5)));
						IndexPropertiesPanel.this.setMaximumSize(new Dimension(IndexPropertiesPanel.this.getMaximumSize().width + (isVisible ? -3 : 3), IndexPropertiesPanel.this.getMaximumSize().height + (isVisible ? -5 : 5)));
						IndexPropertiesPanel.this.setPreferredSize(new Dimension(IndexPropertiesPanel.this.getPreferredSize().width + (isVisible ? -3 : 3), IndexPropertiesPanel.this.getPreferredSize().height + (isVisible ? -5 : 5)));
						settingsPopup.setVisible(true); 
						if (scrollPaneTA.getHeight() == (isVisible ? 0 : 400)) {
							if (changeTimer.isRunning()) {
								changeTimer.stop();
								scrollPaneTA.setVisible(isVisible ? false : true);
							}
						}
					}
				});
				changeTimer.start();
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
	}
		
	/** Метод, возвращающий тип выбранного анализатора
	 * @return Возвращает тип анализатора: 0 - StandardAnalyzer, 1 - WhitespaceAnalyzer, 2 - RussianAnalyzer, -1 - в остальных случаях
	 * */
	public int getIndexingAnalyzer() {
		if (analyzerStandard.isSelected()) {
			return 0;
		} else if (analyzerWhitespace.isSelected()) {
			return 1;
		} else if (analyzerRussian.isSelected()) {
			return 2;
		} else {
			return -1;
		}
	}
	
	private void changeStatus(boolean status) {
		for (int i = 0; i < checkList.getComponentCount(); i++) {
			((JCheckBox) checkList.getComponent(i)).setSelected(status);
		}
	}
	
	public class CheckList extends JPanel {
		
		public CheckList() {
			super();
			setBorder(BorderFactory.createEmptyBorder());
			if (attributes != null) {
				setLayout(new GridLayout(attributes.size(), 1));
				for (int i = 0; i < attributes.size(); i++) {
					String className = classNames.get(attributes.get(i).id);
 		    		JCheckBox checkBox = kz.tamur.rt.Utils.createCheckBox(attributes.get(i).name+(className!=null?"-"+className:""), true);
 		    		checkBox.setFont(new Font("Arial", Font.ITALIC, 10));
		    		add(checkBox);
		    		checkBoxValuesMap.put(i, checkBox.isSelected());
		    		checkBox.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							checkBoxValuesMap.put(CheckList.this.getComponentZOrder((JCheckBox) e.getSource()), ((JCheckBox) e.getSource()).isSelected());
						}
					});
		    	}
	    	}
		}
	}

	public Timer getTimer() {
		return changeTimer;
	}
}