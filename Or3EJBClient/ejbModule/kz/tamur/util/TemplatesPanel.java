package kz.tamur.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.UIDChooser.UIDs;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class TemplatesPanel extends JPanel implements ActionListener {
	private int MODE;
	private DesignerTree tree;
	private JList list;
	private Timer changeTimer;
	private JPanel buttonsPanel = new JPanel();
	private JPanel contentPanel = new JPanel();
	private JTable templatesTable;
	private JScrollPane tableScrollPane;
	private JCheckBox applyTemplate = kz.tamur.rt.Utils.createCheckBox("Применить шаблон", true);
	private JCheckBox ifcAccess = kz.tamur.rt.Utils.createCheckBox("Интерфейсы", true);
	private JCheckBox usrAccess = kz.tamur.rt.Utils.createCheckBox("Пользователи", true);
	private JCheckBox procAccess = kz.tamur.rt.Utils.createCheckBox("Процессы", true);
	private JCheckBox repAccess = kz.tamur.rt.Utils.createCheckBox("Отчеты", true);
	private JCheckBox fltrAccess = kz.tamur.rt.Utils.createCheckBox("Фильтры", true);
	private JCheckBox boxAccess = kz.tamur.rt.Utils.createCheckBox("Пункты обмены", true);
	private JCheckBox idAccess = kz.tamur.rt.Utils.createCheckBox("ID объектов", true);
	private JCheckBox[] accessCheckBoxes = {ifcAccess, usrAccess, procAccess, repAccess, fltrAccess, boxAccess, idAccess};
	private List<String> accessValues = new ArrayList<String>();
	private JButton addTemplateBtn = ButtonsFactory.createToolButton("AddTemplateIcon", ".png", "Добавить шаблон");
	private JButton removeTemplateBtn = ButtonsFactory.createToolButton("RemoveTemplateIcon", ".png", "Удалить шаблон");
	private JButton editTemplateBtn = ButtonsFactory.createToolButton("EditTemplateIcon", ".png", "Изменить шаблон");
	private JButton showInfoBtn = ButtonsFactory.createToolButton("InfoIcon", ".png", "Информация");
	private JButton accessBtn = ButtonsFactory.createToolButton("AccessIcon", ".png", "Доступ к шаблону");
	private JButton maximizeTSPBtn = ButtonsFactory.createToolButton("MaximizeIcon", ".png", "Развернуть панель");
	private JButton minimizeTSPBtn = ButtonsFactory.createToolButton("MinimizeIcon", ".png", "Свернуть панель");
	private Dimension TSPDimension = new Dimension(this.getSize().width, 25);
	private Dimension CPDimension = new Dimension(this.getSize().width, 0);
	private Dimension BPDimension = new Dimension(new Dimension(35, 100));
	private Dimension tableScrollDimension = new Dimension(350, 80);
	private JTextField viewTextField = new OrPropTextField();
	private String currentTemplate = "Текущий шаблон: ";
	private Kernel kernel = Kernel.instance();
	private User user = kernel.getUser();
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private JLabel currentTemplateLabel = kz.tamur.rt.Utils.createLabel(currentTemplate);	
	private static final String[] PREFIXES = new String[] { "ifc", "usr", "proc", "rep", "fltr", "box", "id" };
	//Набор шаблонов предусмотренных по умолчанию
	private static final String[] DEFAULT_TEMPLATES = new String[] { "#set($variableName = $Objects.getObject(\"UID\")){1111111}", "$Objects.getObject(\"UID\"){0000100}" };
	private String cyrillic = new String("абвгдеёжзиыйклмнопрстуфхцчшщьъэюяАБВГДЕЁЖЗИЫЙКЛМНОПРСТУФХЦЧШЩЬЪЭЮЯ");
	private String[] latin = { "a", "b", "v", "g", "d", "e", "yo", "g", "z", "i", "y", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "tz", "ch", "sh", "sh", "", "", "e", "yu", "ya",
							   "A", "B", "V", "G", "D", "E", "YO", "G", "Z", "I", "Y", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "TZ", "CH", "SH", "SH", "", "", "E", "YU", "YA" };
	private Pattern templatePattern = Pattern.compile("(.+)(\\{([01]{7})\\}$)");
	
	private List<Integer> indexControl = new ArrayList<Integer>();

    public TemplatesPanel(DesignerTree tree) {
        setTree(tree);
        init();
    }
	
	public TemplatesPanel(JList list) {
		this.list = list;
		MODE = 6;
		init();
	}

	private void init() {
		setOpaque(isOpaque);
		setLayout(new GridBagLayout());
		setMaximumSize(TSPDimension);
		setMinimumSize(TSPDimension);
		setPreferredSize(TSPDimension);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));		
		TemplatesTableModel model = new TemplatesTableModel((ArrayList<String>) loadTemplates());
		templatesTable = new JTable(model);
		templatesTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = templatesTable.getSelectedRow();
						if (selectedRow > -1) {
							applyTemplate.setEnabled(true);
							removeTemplateBtn.setEnabled(true);
							editTemplateBtn.setEnabled(true);
							accessBtn.setEnabled(true);
							if (MODE == 6) {
								insertIntoTemplate((UIDs) list.getSelectedValue());
							} else {
								insertIntoTemplate(tree.getSelectedNode());
							}
							currentTemplateLabel.setText(currentTemplate + String.valueOf(templatesTable.getModel().getValueAt(selectedRow, 0)));
						} else {
							currentTemplateLabel.setText(currentTemplate);
							removeTemplateBtn.setEnabled(false);
							editTemplateBtn.setEnabled(false);
							if (viewTextField.getText().trim().equals("")) {
								applyTemplate.setEnabled(false);
								accessBtn.setEnabled(false);
							}
						}
						setCBValues(selectedRow);
					}
				});
		templatesTable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					if (templatesTable.getSelectedRow() > -1) {
						templatesTable.clearSelection();
					}
				}
			}
		});
		templatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templatesTable.setFont(new Font("Calibri", Font.ITALIC, 12));
		templatesTable.setSelectionBackground(Color.PINK);
		templatesTable.setSelectionForeground(Color.BLUE);
		templatesTable.setBackground(Color.WHITE);
		final TableCellRenderer headerRenderer = templatesTable.getTableHeader().getDefaultRenderer();
		TableCellRenderer renderer = new TableCellRenderer() {
			private final Border border = BorderFactory.createEmptyBorder();

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (component instanceof JComponent)
					((JComponent) component).setBorder(border);
				return component;
			}
		};
		templatesTable.getTableHeader().setDefaultRenderer(renderer);
		tableScrollPane = new JScrollPane(templatesTable);
		tableScrollPane.setPreferredSize(tableScrollDimension);
		tableScrollPane.setMaximumSize(tableScrollDimension);
		tableScrollPane.setMinimumSize(tableScrollDimension);
		tableScrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
		tableScrollPane.setBackground(templatesTable.getTableHeader().getBackground());
		tableScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		addTemplateBtn.setEnabled(false);
		removeTemplateBtn.setEnabled(false);
		editTemplateBtn.setEnabled(false);
		addTemplateBtn.addActionListener(this);
		removeTemplateBtn.addActionListener(this);
		editTemplateBtn.addActionListener(this);
		showInfoBtn.addActionListener(this);
		accessBtn.addActionListener(this);

		buttonsPanel.setLayout(new GridBagLayout());
		buttonsPanel.setPreferredSize(BPDimension);
		buttonsPanel.setMaximumSize(BPDimension);
		buttonsPanel.setMinimumSize(BPDimension);
		buttonsPanel.setOpaque(isOpaque);
		buttonsPanel.add(addTemplateBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 5), 0, 0));
		buttonsPanel.add(removeTemplateBtn, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 5), 0, 0));
		buttonsPanel.add(editTemplateBtn, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 5), 0, 0));
//		buttonsPanel.add(showInfoBtn, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 5), 0, 0));
		buttonsPanel.add(accessBtn, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 0, 5), 0, 0));

		viewTextField.setFont(new Font("Calibri", Font.ITALIC, 14));
		viewTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		viewTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				checkTemplate();
			}

			public void insertUpdate(DocumentEvent e) {
				checkTemplate();
			}

			public void changedUpdate(DocumentEvent e) {
				checkTemplate();
			}

			private void checkTemplate() {
				if (viewTextField.getText().trim().length() > 0) {
					addTemplateBtn.setEnabled(true);
					accessBtn.setEnabled(true);
					if (templatesTable.getSelectedRow() > -1) {
						editTemplateBtn.setEnabled(true);
					}
				} else {
					addTemplateBtn.setEnabled(false);
					accessBtn.setEnabled(false);
					editTemplateBtn.setEnabled(false);
				}
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(isOpaque);
		panel.add(tableScrollPane, BorderLayout.NORTH);
		panel.add(viewTextField, BorderLayout.SOUTH);

		contentPanel.setLayout(new BorderLayout());
		contentPanel.setMaximumSize(CPDimension);
		contentPanel.setMinimumSize(CPDimension);
		contentPanel.setPreferredSize(CPDimension);
		contentPanel.add(buttonsPanel, BorderLayout.WEST);
		contentPanel.add(panel, BorderLayout.CENTER);
		contentPanel.setOpaque(isOpaque);
		applyTemplate.setFocusPainted(false);
		applyTemplate.setOpaque(isOpaque);

		add(maximizeTSPBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		add(currentTemplateLabel, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(applyTemplate, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(contentPanel, new GridBagConstraints(0, 1, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 2, 1), 0, 0));

		maximizeTSPBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeTimer = new Timer(1, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TSPDimension.height = TSPDimension.height + 1;
						CPDimension.height = CPDimension.height + 1;
						setVisible(false);
						setVisible(true);
						if (TSPDimension.height == 125) {
							if (changeTimer.isRunning()) {
								changeTimer.stop();
								remove(maximizeTSPBtn);
								add(minimizeTSPBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
							}
						}
						setVisible(true);
					}
				});
				changeTimer.start();
			}
		});

		minimizeTSPBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeTimer = new Timer(1, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TSPDimension.height = TSPDimension.height - 1;
						CPDimension.height = CPDimension.height - 1;
						setVisible(false);
						if (TSPDimension.height == 25) {
							if (changeTimer.isRunning()) {
								changeTimer.stop();
								remove(minimizeTSPBtn);
								add(maximizeTSPBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
							}
						}
						setVisible(true);
					}
				});
				changeTimer.start();
			}
		});
		if (templatesTable.getRowCount() > 0) {
			templatesTable.setRowSelectionInterval(0, 0);
		}
	}

	private void setCBValues(int index) {
		if (index > -1) {
			char[] CBValues = accessValues.get(index).toCharArray();
			for (int i = 0; i < accessCheckBoxes.length; i++) {
				accessCheckBoxes[i].setSelected(CBValues[i] == '1' ? true : false);
			}
		} else {
			for (int i = 0; i < accessCheckBoxes.length; i++) {
				accessCheckBoxes[i].setSelected(true);
			}
		}
	}
	
	private String convertToLatin(String variableName) {
		char[] chr = variableName.toCharArray();
		StringBuffer newName = new StringBuffer("");
		for (int i = 0; i < chr.length; i++) {
			int index = cyrillic.indexOf(chr[i]);
			if (index != -1)
				newName.append(latin[index]);
			else
				newName.append(chr[i]);
		}
		return newName.toString();
	}

    public void insertIntoTemplate(AbstractDesignerTreeNode node) {
        insertIntoTemplate(node, null);
    }

    public void insertIntoTemplate(AbstractDesignerTreeNode node, KrnObject nodeObj) {
        if (nodeObj == null && node != null) {
            nodeObj = getNodeObj(node);
        }
        if (templatesTable.getSelectedRow() > -1) {
            String template = String.valueOf(((TemplatesTableModel) templatesTable.getModel()).getObjectValueAt(
                    templatesTable.getSelectedRow(), 0));
            if (node == null) {
                viewTextField.setText(template);
            } else {
                template = template.replaceAll("UID", nodeObj.uid);
                String variableName = node.toString().replaceAll(" ", "_");
                if (variableName.length() > 15) {
                    variableName = variableName.substring(0, 15);
                }
                variableName = PREFIXES[MODE].concat(convertToLatin(variableName));
                template = template.replaceAll("variableName", variableName);
                viewTextField.setText(template);
            }
        }
    }

	public void insertIntoTemplate(UIDs item) {
		if (templatesTable.getSelectedRow() > -1) {
			String template = String.valueOf(((TemplatesTableModel) templatesTable.getModel()).getObjectValueAt(templatesTable.getSelectedRow(), 0));
			if (item == null) {
				viewTextField.setText(template);
			} else {
				template = template.replaceAll("UID", item.getUID());
				String variableName = item.toString().replaceAll(" ", "_");
				if (variableName.length() > 15) {
					variableName = variableName.substring(0, 15);
				}
				variableName = PREFIXES[MODE].concat(convertToLatin(variableName));
				template = template.replaceAll("variableName", variableName);
				viewTextField.setText(template);
			}
		}
	}

    public String[] getCodeTemplate() {
        UIDs item = null;
        AbstractDesignerTreeNode node = null;
        String[] text = new String[2];

        if (MODE == 6) {
            item = (UIDs) list.getSelectedValue();
        } else {
            node = tree.getSelectedNode();
        }
        StringBuilder txt = new StringBuilder(150);
        switch (MODE) {
        case 0:
            txt.append("// Интерфейс - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 1:
            txt.append("// Пользователь - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 2:
            txt.append("// Процесс - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 3:
            txt.append("// Отчет - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 4:
            txt.append("// Фильтр - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 5:
            txt.append("// Пункт обмена - ").append(node.toString()).append(" \"").append(getNodeObj(node).uid).append("\"\n");
            break;
        case 6:
            try {
                KrnObject object = kernel.getObjectByUid(item.getUID(), 0);
                String className = kernel.getClass(object.classId).name;
                txt.append("//").append(className).append(" - ").append(item.toString()).append(" \"").append(item.getUID())
                        .append("\"\n");
            } catch (KrnException e) {
                txt.append("//ID объекта - ").append(item.toString()).append(" \"").append(item.getUID()).append("\"\n");
                e.printStackTrace();
            }
            break;
        default:
            txt.append("");
        }
        text[0] = txt.toString();
        text[1] = applyTemplate.isSelected() && templatesTable.getSelectedRow() > -1 ? viewTextField.getText() : "";
        return text;
    }

	public void actionPerformed(ActionEvent e) {
		Object key = e.getSource();
		if (key == addTemplateBtn) {
			String template = viewTextField.getText();
			int position = ((TemplatesTableModel) templatesTable.getModel()).getTemplates().size();
			if (accessCheckBoxes[MODE].isSelected() == true) {
				accessValues.add(getAccessCode(false));
				((TemplatesTableModel) templatesTable.getModel()).getTemplates().add(position, template);
				((TemplatesTableModel) templatesTable.getModel()).fireTableRowsInserted(position, position);
				templatesTable.getSelectionModel().setSelectionInterval(position, position);
			}		
			try {
				int position2 = kernel.getStrings(user.object, "templates", 0, 0).length;				
				kernel.setString(user.getObject().id, user.getObject().classId,	"templates", position2, 0, template  + getAccessCode(true), 0);
				indexControl.add(position2);
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
		} else if (key == removeTemplateBtn) {
			int selectedRow = templatesTable.getSelectedRow();
			accessValues.remove(selectedRow);
			((TemplatesTableModel) templatesTable.getModel()).getTemplates().remove(selectedRow);
			((TemplatesTableModel) templatesTable.getModel()).fireTableRowsDeleted(selectedRow, selectedRow);
			try {
				kernel.deleteValue(user.getObject().id, user.getObject().classId, "templates", new int[] { indexControl.get(selectedRow) }, 0);
				indexControl.remove(selectedRow);
				for(int i = selectedRow; i<((TemplatesTableModel) templatesTable.getModel()).getTemplates().size();i++  ) {
					indexControl.set(i, indexControl.get(i)-1);
				}
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			if (selectedRow > 0) {
				templatesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
			}
		} else if (key == editTemplateBtn) {
			int selectedRow = templatesTable.getSelectedRow();
			String template = viewTextField.getText();
			String accValue = getAccessCode(true);
			boolean removeRow = false;
			if (accessCheckBoxes[MODE].isSelected() == true) {
				accessValues.remove(selectedRow);
				accessValues.add(selectedRow, getAccessCode(false));
				((TemplatesTableModel) templatesTable.getModel()).getTemplates().remove(selectedRow);
				((TemplatesTableModel) templatesTable.getModel()).getTemplates().add(selectedRow, template);
				((TemplatesTableModel) templatesTable.getModel()).fireTableRowsUpdated(selectedRow, selectedRow);
				templatesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
			} else {
				removeRow = true;
//				accessValues.remove(selectedRow);
				((TemplatesTableModel) templatesTable.getModel()).getTemplates().remove(selectedRow);
				((TemplatesTableModel) templatesTable.getModel()).fireTableRowsDeleted(selectedRow, selectedRow);
				if (selectedRow > 0) {
					templatesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
				}
			}
			try {
				if(accValue.contains("1")) {
					kernel.setString(user.getObject().id, user.getObject().classId, "templates", indexControl.get(selectedRow), 0, template + accValue, 0);
				} else {
					kernel.deleteValue(user.getObject().id, user.getObject().classId, "templates", new int[] { indexControl.get(selectedRow) }, 0);
					for(int i = selectedRow+1; i<accessValues.size();i++  ) {
						indexControl.set(i, indexControl.get(i)-1);
					}
				}
				if(removeRow) {
					accessValues.remove(selectedRow);
					indexControl.remove(selectedRow);	
				}
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
		} else if (key == showInfoBtn) {
			JPopupMenu popup = new JPopupMenu();
			popup.setOpaque(isOpaque);
			popup.setBorder(BorderFactory.createEmptyBorder());
			popup.add(kz.tamur.rt.Utils.createLabel("Новый шаблон должен содержать в себе имя переменной и UID"));
			popup.show(contentPanel, showInfoBtn.getLocation().x + 35, showInfoBtn.getLocation().y + 35);
		} else if (key == accessBtn) {
			JPopupMenu popup = new JPopupMenu();
			popup.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
			JPanel accessPanel = new JPanel();
			accessPanel.setLayout(new GridBagLayout());
			
			for (int i = 0; i < accessCheckBoxes.length; i++) {
				accessCheckBoxes[i].setOpaque(isOpaque);
				accessPanel.add(accessCheckBoxes[i], new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}

			popup.add(accessPanel);
			popup.show(contentPanel, showInfoBtn.getLocation().x + 35, showInfoBtn.getLocation().y + 81);
		}
	}
	
	private String getAccessCode(boolean isFull) {
		StringBuffer code = new StringBuffer();
		code.append(isFull ? "{" : "");
		code.append(ifcAccess.isSelected() == true ? "1" : "0");
		code.append(usrAccess.isSelected() == true ? "1" : "0");
		code.append(procAccess.isSelected() == true ? "1" : "0");
		code.append(repAccess.isSelected() == true ? "1" : "0");
		code.append(fltrAccess.isSelected() == true ? "1" : "0");
		code.append(boxAccess.isSelected() == true ? "1" : "0");
		code.append(idAccess.isSelected() == true ? "1" : "0");
		code.append(isFull ? "}" : "");
		return code.toString();
	}

    private List<String> loadTemplates() {
        try {
            String[] allTemplates = kernel.getStrings(user.object, "templates", 0, 0);
            if (allTemplates.length == 0) {
                for (int i = 0; i < DEFAULT_TEMPLATES.length; i++) {
                    kernel.setString(user.getObject().id, user.getObject().classId, "templates", i, 0, DEFAULT_TEMPLATES[i], 0);
                }
                return filterTemplates(DEFAULT_TEMPLATES);
            } else {
                return filterTemplates(allTemplates);
            }
        } catch (KrnException exception) {
            exception.printStackTrace();
        }
        return null;
    }
	
	private List<String> filterTemplates(String[] allTemplates) {
		List<String> templates = new ArrayList<String>();
		indexControl.clear();
		for (int i = 0; i < allTemplates.length; i++) {
		    Matcher matcher = templatePattern.matcher(allTemplates[i]);
		    if (matcher.find()) {
		    	if (matcher.group(3).toCharArray()[MODE] == '1') {
		    		templates.add(matcher.group(1));
		    		indexControl.add(i);
		    		accessValues.add(matcher.group(3));
		    	}
		    }
		}
		return templates;
	}

	/**
     * @param tree the tree to set
     */
    public void setTree(DesignerTree tree) {
        this.tree = tree;
        if (tree instanceof InterfaceTree) {
            MODE = 0;
        } else if (tree instanceof UserTree) {
            MODE = 1;
        } else if (tree instanceof ServicesTree) {
            MODE = 2;
        } else if (tree instanceof ReportTree) {
            MODE = 3;
        } else if (tree instanceof FiltersTree) {
            MODE = 4;
        } else if (tree instanceof BoxTree) {
            MODE = 5;
        }
    }
    
    public KrnObject getNodeObj(AbstractDesignerTreeNode node) {
        return node instanceof ServiceControlNode ? ((ServiceControlNode) node).getValue() : node.getKrnObj();
    }
    
    class TemplatesTableModel extends AbstractTableModel {

		private final String[] COL_NAMES = { "Шаблон" };
		private List<String> templates;

		public TemplatesTableModel(List<String> templates) {
			this.templates = templates;
		}

		public List<String> getTemplates() {
			return templates;
		}

		public void setTemplates(List<String> templates) {
			this.templates = templates;
		}

		public int getRowCount() {
			return templates.size();
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
			}
			return null;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return templates.get(rowIndex);
			}
			return null;
		}

		public Object getObjectValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return templates.get(rowIndex);
			}
			return null;
		}

		public String getNode(int row) {
			return templates.get(row);
		}

		public boolean hasNode(String template) {
			for (String templateNode : templates) {
				if (templateNode.equals(template))
					return true;
			}
			return false;
		}

		public void fireTableDataChanged() {
			super.fireTableDataChanged();
		}
	}
}
