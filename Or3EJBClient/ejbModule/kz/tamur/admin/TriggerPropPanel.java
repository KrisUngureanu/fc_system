package kz.tamur.admin;

import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getMidSysColor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import kz.tamur.comps.TriggerInfo;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class TriggerPropPanel extends JPanel implements ActionListener {
	
	private KrnAttribute attribute;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JTable existingTriggersTable;
    private TriggersTableModel model;
    private JTextArea sqlTextArea = new JTextArea();
    private JPopupMenu popup = new JPopupMenu();
    private JButton addTriggerBtn = ButtonsFactory.createToolButton("AddTemplateIcon", ".png", "Создать триггер");

    private JMenuItem removeTriggerItem = createMenuItem("Удалить триггер", "RemoveTemplateIcon.png");
	
	public TriggerPropPanel(KrnAttribute attribute) {
		this.attribute = attribute;
		init();
	}
	
	private void init() {
        setOpaque(isOpaque);
		setLayout(new BorderLayout());
		Utils.setAllSize(this, new Dimension(400, 400));
		
		// Существующие триггеры
        JPanel existingTriggersPanel = new JPanel(new BorderLayout());
        Utils.setAllSize(existingTriggersPanel, new Dimension(400, 150));
        existingTriggersPanel.setOpaque(isOpaque);
        Border border = BorderFactory.createLineBorder(getMidSysColor());
        existingTriggersPanel.setBorder(kz.tamur.rt.Utils.createTitledBorder(border, "Созданные триггеры"));
        add(existingTriggersPanel, BorderLayout.NORTH);
        
        model = new TriggersTableModel((ArrayList<TriggerInfo>) getTriggers(attribute));
    	existingTriggersTable = new JTable(model);
    	existingTriggersTable.setFont(Utils.getDefaultFont());
    	existingTriggersTable.getTableHeader().setFont(Utils.getDefaultFont());

    	JScrollPane existingTriggersSP = new JScrollPane(existingTriggersTable);
    	Utils.setAllSize(existingTriggersSP, new Dimension(400, 150));
    	existingTriggersPanel.add(existingTriggersSP, BorderLayout.CENTER);
    	
    	addTriggerBtn.addActionListener(this);
    	removeTriggerItem.addActionListener(this);
	    popup.add(removeTriggerItem);
    	
		addTriggerBtn.setEnabled(false);
	    sqlTextArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				check();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				check();				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				check();				
			}
			
			private void check() {
				if (sqlTextArea.getText().trim().length() == 0) {
					addTriggerBtn.setEnabled(false);
				} else {
					addTriggerBtn.setEnabled(true);
				}
			}
		});
	    
    	existingTriggersTable.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 2) {
	                    JPopupMenu popup = new JPopupMenu();
	                    JTextArea textArea = new JTextArea();
	                    textArea.setEditable(false);
	                    JScrollPane scrollPane = new JScrollPane(textArea);
	                    scrollPane.setBorder(BorderFactory.createEmptyBorder());
	                    Utils.setAllSize(scrollPane, new Dimension(150, 150));
	                    textArea.append(model.getValueAt(existingTriggersTable.getSelectedRow(), existingTriggersTable.getSelectedColumn()));
	                    popup.add(scrollPane);
	                    popup.show(TriggerPropPanel.this, e.getX(), e.getY());
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (existingTriggersTable.getSelectedColumn() > -1) {
						popup.show(TriggerPropPanel.this, e.getX(), e.getY());
					}
				}
			}
		});
    	
        JPanel newTriggerPanel = new JPanel(new GridBagLayout());
        newTriggerPanel.setLayout(new GridBagLayout());
        newTriggerPanel.setOpaque(isOpaque);
        newTriggerPanel.setBorder(kz.tamur.rt.Utils.createTitledBorder(border, "Создание триггера"));
        add(newTriggerPanel, BorderLayout.CENTER);
        
        JScrollPane sqlScrollPane = new JScrollPane(sqlTextArea);
        Utils.setAllSize(sqlScrollPane, new Dimension(370, 235));
        sqlScrollPane.setOpaque(isOpaque);
        sqlScrollPane.getViewport().setOpaque(isOpaque);

        newTriggerPanel.add(addTriggerBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        newTriggerPanel.add(sqlScrollPane, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
	}
	
	private List<TriggerInfo> getTriggers(KrnAttribute attribute) {
		List<TriggerInfo> triggers = new ArrayList<TriggerInfo>();
		try {
			KrnClass cls = Kernel.instance().getClass(attribute.classId);
			triggers.addAll(Kernel.instance().getTriggers(cls));
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return triggers;
	}
	
	 class TriggersTableModel extends AbstractTableModel {

			private final String[] COL_NAMES = { "Название", "Описание", "Тело" };
			private List<TriggerInfo> triggers;

			public TriggersTableModel(List<TriggerInfo> triggers) {
				this.triggers = triggers;
			}

			public List<TriggerInfo> getTriggers() {
				return triggers;
			}

			public void setTriggers(List<TriggerInfo> triggers) {
				this.triggers = triggers;
			}

			public int getRowCount() {
				return triggers.size();
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
						return String.class;
					case 1:
						return String.class;
					case 2:
						return String.class;
				}
				return null;
			}

			public String getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
					case 0:
						return ((TriggerInfo) triggers.get(rowIndex)).getName();
					case 1:
						return ((TriggerInfo) triggers.get(rowIndex)).getDescription();
					case 2:
						return ((TriggerInfo) triggers.get(rowIndex)).getBody();
				}
				return null;
			}

			public TriggerInfo getNode(int row) {
				return triggers.get(row);
			}

			public void fireTableDataChanged() {
				super.fireTableDataChanged();
			}
		}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == addTriggerBtn) {
			try {
				String triggerContext = sqlTextArea.getText();
				String status = Kernel.instance().createTrigger(triggerContext);
				if ("Success".equals(status)) {
			        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Триггер создан успешно!");
				} else {
			        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Ошибка при создании триггера!");
				}
		        model.setTriggers(getTriggers(attribute));
		        ((TriggersTableModel) existingTriggersTable.getModel()).fireTableDataChanged();
			} catch (KrnException e) {
				e.printStackTrace();
			}
		} else if (source == removeTriggerItem) {
			try {
				int row = existingTriggersTable.getSelectedRow();
				String triggerName = model.getValueAt(row, 0);
				String status = Kernel.instance().removeTrigger(triggerName);
				if ("Success".equals(status)) {
			        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Триггер удален успешно!");
				} else {
			        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Ошибка при удалении триггера!");
				}
		        model.setTriggers(getTriggers(attribute));
		        ((TriggersTableModel) existingTriggersTable.getModel()).fireTableDataChanged();
 			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
	}
}