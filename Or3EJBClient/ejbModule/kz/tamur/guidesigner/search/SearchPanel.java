package kz.tamur.guidesigner.search;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Cursors;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.boxes.BoxNode;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportNode;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.search.SearchResult.SearchTableModel;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.users.UserNode;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.UIDChooser;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.StringValue;

public class SearchPanel extends JPanel implements ActionListener, TreeSelectionListener {
	
	private SearchRunner sRunner;
	private IndexRunner iRunner;
	
    private JToolBar toolBar = Utils.createDesignerToolBar();
    private JLabel inputTextLabel = kz.tamur.rt.Utils.createLabel(" Введите ключевое слово для поиска: ");
    private JTextField textField = new JTextField();
//    private JCheckBox textInputModeCheck = kz.tamur.rt.Utils.createCheckBox("Редактирование", false);
    private JCheckBox searchModeCheck = kz.tamur.rt.Utils.createCheckBox("Название / UID", false);
    private JButton srchBtn = ButtonsFactory.createToolButton("SearchIcon", ".png", "Найти");
    private JButton pathBtn = ButtonsFactory.createToolButton("classes", "Выбор пути");
    private JButton uidBtn = ButtonsFactory.createToolButton("UID1", "Выбор ID объекта");
    private JButton UI = ButtonsFactory.createToolButton("HyperPopup", "Выберите интерфейсы");
    private JButton UserBtn = ButtonsFactory.createToolButton("userNode", "Выберите пользователя");
    private JButton processBtn = ButtonsFactory.createToolButton("ServiceTab", "Выбор процесса");
    private JButton reportBtn = ButtonsFactory.createToolButton("ReportNode", "Выбор отчета");
    private JButton filterBtn = ButtonsFactory.createToolButton("FilterNode", "Выбор фильтра");
    private JButton boxBtn = ButtonsFactory.createToolButton("BoxNode", "Выбор пункта обмена");
    private JButton idxAllBtn = ButtonsFactory.createToolButton("fullIndexing", "Индексация всех полей типа BLOB");
//    private JButton idxBtn = ButtonsFactory.createToolButton("partIndexing", "Индексация индексируемых полей типа BLOB");
    private JButton cancelIdx = ButtonsFactory.createToolButton("StopIcon", "Отменить индексацию");
    private JButton restoreBtn = ButtonsFactory.createToolButton("RestoreIcon", ".png", "Развернуть окно процесса индексирования");
    private JButton replaceBtn = ButtonsFactory.createToolButton("ReplJournal", "Заменить запись");
    private SearchPropertiesPanel searchPropertiesPanel = new SearchPropertiesPanel();
    private IndexPropertiesPanel indexPropertiesPanel;
    
    private JPopupMenu pMenu = new JPopupMenu();
    private JMenuItem deleteItem = createMenuItem("Удалить");

    private JSplitPane splitPane = new JSplitPane();
    private SearchTree tree;
    private String iconTreeNode;
    private static SearchResult searchResult;
    private JPanel rightPanel;
    private JProgressBar bottomPB;
    private JProgressBar topPB;
    private JProgressBar toolbarPB;
    private boolean stopThread;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private DesignerDialog dlg;
    private Map<String, List<Object>> searchingInfo = new HashMap<String, List<Object>>();
    private Map<String, List<Integer>> editedRows = new HashMap<String, List<Integer>>();
	private List<List<String[]>> groups = new ArrayList<List<String[]>>();
	private String[] queryParserEscSymbols = new String[] {"+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\", "/"};
	private List<KrnAttribute> attributes = null;
	private Map<Long,String> classNames=null;
	private List<KrnMethod> methods = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private Kernel kernel = Kernel.instance();
	private User user = kernel.getUser();
	private KrnClass userClass;
    
    public SearchPanel() {
        super(new BorderLayout());
        try {
    		attributes = new ArrayList<>();
    		List<KrnAttribute> allAttrs = kernel.getAttributesByTypeId(10, false);
    		methods = kernel.getAllMethods();
    		classNames = new HashMap<Long, String>();
    		for (KrnAttribute attr : allAttrs) {
				KrnClass cls = kernel.getClass(attr.classId);
    			if (attr.isIndexed || "ReportPrinter".equals(cls.name) || "UI".equals(cls.name)
    					 || "Filter".equals(cls.name) || "ProcessDef".equals(cls.name)) {
    				attributes.add(attr);
    				if (cls!=null) classNames.put(attr.id, cls.name);
    			}
    		}
    		Collections.sort(attributes, new Comparator<KrnAttribute>() {
				@Override
				public int compare(KrnAttribute o1, KrnAttribute o2) {
					if (o1.classId < o2.classId) return -1;
					else if (o1.classId > o2.classId) return 1;
					return o1.name.compareTo(o2.name);
				}
    		});
    		
//    		for (int i = 0; i < methods.size(); i++) {
//        		System.out.println(i + ". " + methods.get(i).name);
//    		}
    		userClass = kernel.getClassByName("User");
    		indexPropertiesPanel = new IndexPropertiesPanel(attributes, classNames);
    		if (kernel.getAttributeByName(userClass, "lastIndexingConfig") != null) {
    			Map<String, String> configuration = configurationChecking(kernel.getStrings(user.object, "lastIndexingConfig", 0, 0), attributes.size());
    			if (configuration.size() == 3) {
    				indexPropertiesPanel.setUserConfiguration(configuration);
    			}
    		}
		} catch (KrnException e) {
			e.printStackTrace();
		}
		

		
        initToolBar();
        initPopup();
        add(splitPane, BorderLayout.CENTER);
        if (tree == null) {
        	SearchNode inode = new SearchNode("История операций поиска", "", 0, 0);
            tree = new SearchTree(inode);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            splitPane.setLeftComponent(new JScrollPane(tree));
            tree.setSelectionRow(0);
        }
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                	showPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                	showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                pMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
		for (int i = 0; i < 12; i++) {
			groups.add(new ArrayList<String[]>());
		}
        searchResult = new SearchResult();
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        splitPane.setRightComponent(rightPanel);
        tree.setSelectionRow(0);       
        validate();        
    }
    
    private Map<String, String> configurationChecking(String[] configurationValues, int attributesCount) {
    	Map<String, String> configuration = new HashMap<String, String>();
    	if (configurationValues.length == 4 && configurationValues[1].length() == attributesCount) {
		    configuration.put("Analyzer Type", configurationValues[0]);
			configuration.put("Attributes Status", configurationValues[1]);
			configuration.put("Methods Status", configurationValues[2]);
			configuration.put("Triggers Status", configurationValues[3]);
    	}
    	return configuration;
    }
    
    public void setSplitLocation() {
        splitPane.setDividerLocation(0.3);
        splitPane.validate();
    }

    public static SearchResult getSearchResult() {
    	return searchResult;
    }
    
    public SearchPropertiesPanel getSearchPropertiesPanel() {
    	return searchPropertiesPanel;
    }
    
    private List<String> modifyObject(String[] object, Map<String, String[]> objsByUID) {
    	List<String> result = new ArrayList<String>();
    	String cls_Name = new String();
		String line_2 = "";

    	String typeObj = object[4];
    	String s = new String();
    	for (int i=0; i < object.length; i++) {
    		result.add(object[i]);
    	}
    	String objUID = object[0];
    	try {
			String className = "<Unaccessible>";				
			
			if (typeObj.equals("method")) {					
				className = "<Methods>";
				s = "Метод, UID " + objUID;
			} else if (typeObj.equals("changemethod")) {					
				className = "<Changes>";
				s = "Изменение, ID " + objUID;
			} else if (typeObj.equals("trigger")) {
				className = "<Triggers>";
				String[] params = objUID.split("_");
				String ownerType = params[1];
				if ("0".equals(ownerType)) {
					KrnClass cls = Kernel.instance().getClassByUid(params[0]);
					s = "Триггер класса '" + cls.name + "' (" + cls.id + ")";
				} else {
					KrnAttribute attr = Kernel.instance().getAttributeByUid(params[0]);
					s = "Триггер атрибута '" + attr.name + "' (" + attr.id + ")";
				}
			} else {
				String[] objByUID = objsByUID.get(objUID);
				if (objByUID != null) {
					className = objByUID[5] != null ? objByUID[5] : "<Unaccessible>";
					
					String title = objByUID[6] != null ? objByUID[6] : "null";
					
					if (className.equals("UI")) {
						line_2 = "Интерфейс: '" + title + "'";
					} else if (className.equals("ProcessDef")) {
						line_2 = "Процесс: '" + title + "'";
					} else if (className.equals("User")) {
						line_2 = "Пользователь: '" + title + "'";
					} else if (className.equals("Filter")) {
						line_2 = "Фильтр: '" + title + "'";
					} else if (className.equals("ReportPrinter")) {
						line_2 = "Отчет: '" + title + "'";
					} else if (className.equals("MSDoc")) {
						line_2 = "Файл: '" + title + "'";
					}else {
						line_2 = className+": '" + title + "'";
					}
				}
				s = "Объект класса " + className + ", UID объекта " + objUID;
			}
			cls_Name = className;
			// if (className.equals("ReportPrinter") && Kernel.instance().getLongs(obj, "parent", 0).length == 0)
			// cls_Name = "<Unaccessible>";
			//else 
			result.add(s);
		} catch (KrnException e) {
			e.printStackTrace();
		}
    	s = "";
		String line_1 = ""; 
		String language="";
		try {
			language = Long.parseLong(object[2]) == 0 ? "" : ", код языка "	+ object[2];
			if (object[1].equals("Method")) {
				KrnMethod method = Kernel.instance().getMethodById(object[0]);
				if (method != null) {
    				KrnClass cls = Kernel.instance().getClassById(method.classId);
    				String clsName = cls != null ? cls.name : "Класс с ID=" + method.classId + " не найден!";
    				line_1 = "Аттрибут 'Expression'. Метод: '" + method.name + "', класс:  '" + clsName + "'";
				} else {
					line_1 = "Метод не найден!";
				}
			}else if (object[1].equals("Change")) {
				List<KrnVcsChange> changes = Kernel.instance().getVcsDifChanges(true,new long[]{Long.valueOf(object[0])});
				if (changes.size()>0){
    				KrnClass cls = Kernel.instance().getClassById(changes.get(0).cvsChangeMethod.classId);
					line_1 = "Изменение метода дата:'"+changes.get(0).dateChange.toString("YYYY-MM-dd")+"'. Метод: '" + changes.get(0).title + "', класс:  '" + (cls!=null?cls.name:"null") + "'";
				} else
					line_1 = "";
			} else if (object[1].equals("Trigger")) {
				String[] params = objUID.split("_");
				String ownerType = params[1];
				String triggerType = params[2];
				line_1 = "Событие ";
				if ("0".equals(ownerType)) {
					if ("0".equals(triggerType)) {
						line_1 += "'Перед созданием объекта'";
					} else if ("1".equals(triggerType)) {
						line_1 += "'После создания объекта'";
					} else if ("2".equals(triggerType)) {
						line_1 += "'Перед удалением объекта'";
					} else {
						line_1 += "'После удаления объекта'";
					}
				} else {
					if ("0".equals(triggerType)) {
						line_1 += "'Перед изменением значения атрибута'";
					} else if ("1".equals(triggerType)) {
						line_1 += "'После изменения значения атрибута'";
					} else if ("2".equals(triggerType)) {
						line_1 += "'Перед удалением значения атрибута'";
					} else {
						line_1 += "'После удаления значения атрибута'";
					}
				}
			} else if (Kernel.instance().getAttributeById(Long.parseLong(object[1])) != null) {
				line_1 = "Аттрибут '" + Kernel.instance().getAttributeById(Long.parseLong(object[1])).name + "'" + language;
			}
		} catch (KrnException e) {
			e.printStackTrace(); 
		}
		
		s = line_1 + (line_1.length() > 0 ? "." : "") + (line_2.length() > 0 ? " " : "") + line_2 + (line_2.length() > 0 ? "." : "");
		result.add(s);
		result.add(cls_Name);
    	
    	return result;
    }
    
    private void initToolBar() {
        toolBar.add(inputTextLabel);
        toolBar.add(textField);
        Border lineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);
		Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
		CompoundBorder border = new CompoundBorder(lineBorder, emptyBorder);
		textField.setBorder(border);
        textField.setFont(new Font("Arial", Font.ITALIC, 12));
        textField.setPreferredSize(new Dimension(300, 24));
        textField.addActionListener(this);
        textField.addKeyListener(new KeyListener() {			
			public void keyTyped(KeyEvent e) {
				textFieldTest(e);
			}			
			
			public void keyReleased(KeyEvent e) {
				textFieldTest(e);
			}
			
			public void keyPressed(KeyEvent e) {
				textFieldTest(e);
			}
			
			private void textFieldTest(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_SPACE) {
					if (textField.getText().trim().length() == 0) {
						textField.setText(null);
					}
				} else if (key == KeyEvent.VK_ENTER) {
					if (srchBtn.isEnabled()) {
						srchBtn.doClick();
					}
				}
			}
		});
        textField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				testTextField();
			}
			
			public void removeUpdate(DocumentEvent e) {
				testTextField();
			}

			public void changedUpdate(DocumentEvent e) {
				testTextField();
			}
			
			private void testTextField() {				
				Pattern pattern = Pattern.compile(".+");
			    Matcher matcher = pattern.matcher(Funcs.normalizeInput(textField.getText()).trim());
			    if (matcher.matches()) {
 					srchBtn.setEnabled(true);
				} else {
					srchBtn.setEnabled(false);	
				}
			    pattern = Pattern.compile("\"{1}[\\pLа-яa-zА-ЯA-Z_0-9|\\.| |+|)|(|_]*\"{1} {1}[0-9]+\\.{1}[0-9]+", Pattern.UNICODE_CASE);
			    matcher = pattern.matcher(Funcs.normalizeInput(textField.getText()));
			    if (matcher.matches()) {
			    	searchModeCheck.setEnabled(true);
			    } else {
			        searchModeCheck.setEnabled(false);
			    }		    
			}
		});
        toolBar.add(searchModeCheck);
        searchModeCheck.setEnabled(false);
        searchModeCheck.setFocusPainted(false);
        searchModeCheck.setMargin(Constants.INSETS_0);
        searchModeCheck.addActionListener(this);
        toolBar.add(srchBtn);
        srchBtn.setEnabled(false);
        srchBtn.addActionListener(this);
        toolBar.add(searchPropertiesPanel);
        toolBar.addSeparator();
        toolBar.add(pathBtn);
        pathBtn.addActionListener(this);
        toolBar.add(uidBtn);
        uidBtn.addActionListener(this);
        toolBar.add(UI);
        UI.addActionListener(this);
        toolBar.add(UserBtn);
        UserBtn.addActionListener(this);
        toolBar.add(processBtn);
        processBtn.addActionListener(this);
        toolBar.add(filterBtn);
        filterBtn.addActionListener(this);
        toolBar.add(reportBtn);
        reportBtn.addActionListener(this);
        toolBar.add(boxBtn);
        boxBtn.addActionListener(this);
        toolBar.addSeparator();
//        toolBar.add(idxBtn);
//        idxBtn.addActionListener(this);
        toolBar.add(idxAllBtn);
        idxAllBtn.addActionListener(this);
        toolBar.add(indexPropertiesPanel);
        toolBar.add(replaceBtn);
        replaceBtn.setEnabled(false);
        replaceBtn.addActionListener(this);
    	toolbarPB = new JProgressBar();
        toolbarPB.setVisible(false);
        toolBar.add(toolbarPB);
        restoreBtn.setVisible(false);
        restoreBtn.addActionListener(this);
        toolBar.add(restoreBtn);
        cancelIdx.setVisible(false);
        cancelIdx.addActionListener(this);
        toolBar.add(cancelIdx);
        toolBar.addSeparator();        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.WEST);
        add(panel, BorderLayout.NORTH);
    }
    
    private void initPopup() {
        pMenu.add(deleteItem);
        deleteItem.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        iconTreeNode = "Find";
        if (src == deleteItem) {	
            deleteSelected();
        } else if (src == pathBtn) {
        	ClassNode cnode = null;
        	ClassBrowser cb = new ClassBrowser(cnode, true);        	
            DesignerDialog dlg = 
            	new DesignerDialog((Frame)getTopLevelAncestor(), "Выберите путь", cb);
            dlg.setVisible(true); 
            cb.setSplitLocation(0.5, 0.8);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String spath = cb.getSelectedPath();
                if (spath.length() > 0) {
                	textField.setText(spath);
                	iconTreeNode = "classes";	
                }
            }
        } else if (src == uidBtn) {
            ClassNode cnode = null;
        	ClassBrowser cb = new ClassBrowser(cnode, true);        	
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(), 
            		"Укажите путь к объекту", cb);
            dlg.setVisible(true);
            cb.setSplitLocation(0.5, 0.8);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String spath = cb.getSelectedPath();
                if (spath.length() > 0) { 
                    Cursor cursor = getCursor(); 
                	setCursor(Cursors.WAIT_CURSOR);
                	textField.setText(spath);
                    DesignerFrame.path_expr = spath;
                    UIDChooser uidlist = new UIDChooser(spath);
                    dlg = new DesignerDialog((Frame)getTopLevelAncestor(), 
                		"Выберите значение атрибута  [ " + spath + " ]", uidlist);
                    dlg.setVisible(true);
                    setCursor(cursor);
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    	textField.setText(uidlist.getStringUID());
                    	iconTreeNode = "UID1";
                    }
                }
            }
        } else if (src == UI) {
            DesignerFrame dsgFrame = DesignerFrame.instance();
            InterfaceTree tree = dsgFrame.getInterfaceTree();
        	try {
                OpenElementPanel pan = new OpenElementPanel(tree);
                pan.setSearchUIDPanel(true);		
                DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор интерфейса", pan);
                dlg.setVisible(true);
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    InterfaceNode node = (InterfaceNode) pan.getTree().getSelectedNode();
                    final KrnObject currUI = pan.getNodeObj(node);
                    KrnObject[] objs = new KrnObject[1];
                    objs[0] = currUI;
                    long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
                    Map<Long, String> uidmap = kernel.getObjectUids(objId);
                    String interfaceUID = uidmap.get(objId[0]);
                    String[] attr = kernel.getStrings(currUI, "title", 0, 0);
                    textField.setText("\"" + attr[0] + "\" " + interfaceUID);
                    iconTreeNode = "HyperPopup";
                }
        	} catch (Exception eu) {  eu.printStackTrace(); }
    	} else if (src == UserBtn) {
            try {		
                UserTree user_tree = Utils.getUserTree();
            	OpenElementPanel op = new OpenElementPanel(user_tree);
            	op.setSearchUIDPanel(true);
            	DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор пользователя", op);
                dlg.setVisible(true);
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    UserNode user = (UserNode) user_tree.getSelectedNode();
                    final KrnObject userUI = user.getKrnObj();
                    KrnObject[] objs = new KrnObject[1];
                    objs[0] = userUI;
                    long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
                    Map<Long, String> uidmap = kernel.getObjectUids(objId);
                    String userUID = uidmap.get(objId[0]);
                    String[] attr = kernel.getStrings(userUI, "name", 0, 0);
                    textField.setText("\"" + attr[0] + "\" " + userUID);
                    iconTreeNode = "userNode";
                }
            } catch (Exception eu) {
                eu.printStackTrace();
            }
    	} else if (src == processBtn) {
            try {
            	ServicesTree proc_tree = Utils.getServicesTree();
            	DesignerDialog dlg;
            	OpenElementPanel op = new OpenElementPanel(proc_tree);
            	dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор процесса", op);
            	op.setSearchUIDPanel(true);
                dlg.setVisible(true);
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    ServiceNode proc = (ServiceNode) op.getTree().getSelectedNode();
                    final KrnObject procUI = proc.getKrnObj();
                    KrnObject[] objs = new KrnObject[1];
                    objs[0] = procUI;
                    long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
                    Map<Long, String> uidmap = kernel.getObjectUids(objId);
                    String procUID = uidmap.get(objId[0]);
                    String[] attr = kernel.getStrings(procUI, "title", 0, 0);
                    textField.setText("\"" + attr[0] + "\" " + procUID);
                    iconTreeNode = "ServiceTab";
                }
            } catch (Exception eu) {
                eu.printStackTrace();
            }
    	} else if (src == filterBtn) {
    		try {
    			FiltersTree filter_tree = Utils.getFiltersTree();
    			DesignerDialog dlg;
    			OpenElementPanel op = new OpenElementPanel(filter_tree);
    			dlg = new DesignerDialog((Frame) getTopLevelAncestor(),	"Выбор фильтра", op);
    			op.setSearchUIDPanel(true);
    			dlg.setVisible(true);
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    FilterNode filter = (FilterNode) op.getTree().getSelectedNode();
                    final KrnObject filterUI = filter.getKrnObj();
                    KrnObject[] objs = new KrnObject[1];
                    objs[0] = filterUI;
                    long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
                    Map<Long, String> uidmap = kernel.getObjectUids(objId);
                    String filterUID = uidmap.get(objId[0]);
                    String[] attr = kernel.getStrings(filterUI, "title", 0, 0);
                    textField.setText("\"" + attr[0] + "\" " + filterUID);
                    iconTreeNode = "FilterNode";
                }   
            } catch (Exception eu) {
                eu.printStackTrace();
            }
    	} else if (src == reportBtn) {
			try {
				ReportTree report_tree = kz.tamur.comps.Utils.getReportTree(null);
				DesignerDialog dlg;
				OpenElementPanel op = new OpenElementPanel(report_tree);
				if (getTopLevelAncestor() instanceof JFrame) {
					dlg = new DesignerDialog((Frame) getTopLevelAncestor(),	"Выбор отчета", op);
				} else {
					dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор отчета", op);
				}
				op.setSearchUIDPanel(true);
				dlg.setVisible(true);
				if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
					ReportNode report = (ReportNode) op.getTree().getSelectedNode();
					final KrnObject reportUI = report.getKrnObj();
					KrnObject[] objs = new KrnObject[1];
					objs[0] = reportUI;
					long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
					Map<Long, String> uidmap = kernel.getObjectUids(objId);
					String reportUID = uidmap.get(objId[0]);
					String[] attr = kernel.getStrings(reportUI, "title", 0, 0);
					textField.setText("\"" + attr[0] + "\" " + reportUID);
					iconTreeNode = "ReportNode";
				}
			} catch (Exception eu) {
				eu.printStackTrace();
			}
    	} else if (src == boxBtn) {
    		try {
    			BoxTree box_tree = Utils.getBoxTree();
    			OpenElementPanel op = new OpenElementPanel(box_tree);
    			DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор пункт обмена", op);
    			op.setSearchUIDPanel(true);
    			dlg.setVisible(true);   
    			if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
    				BoxNode box = (BoxNode) box_tree.getSelectedNode();
    				final KrnObject boxUI = box.getKrnObj();
    				KrnObject[] objs = new KrnObject[1];
    				objs[0] = boxUI;
    				long[] objId = com.cifs.or2.util.Funcs.makeObjectIdArray(objs);
    				Map<Long, String> uidmap = kernel.getObjectUids(objId);
    				String boxUID = uidmap.get(objId[0]);
    				String[] attr = kernel.getStrings(boxUI, "name", 0, 0);
                    textField.setText("\"" + attr[0] + "\" " + boxUID);
                    iconTreeNode = "BoxNode";
    			}
            } catch (Exception eu) {
                eu.printStackTrace();
            }
    	} else if (src == srchBtn) {
    		String searchingPhrase = getSearchingText();
    		if (searchPropertiesPanel.getSearchProperties()[1] == 0) {
	    		boolean isContains = false;
	    		for (int i = 0; i < queryParserEscSymbols.length; i++) {
	    			if (searchingPhrase.contains(queryParserEscSymbols[i])) {
	    				isContains = true;
	    				break;
	    			}
	    		}
	    		if (isContains) {
	        		SearchOperationsWindow dialog = new SearchOperationsWindow("Предупреждение", 350, 150, 4, searchingPhrase);
	        		if (!dialog.getSelection()) {
	        			return;
	        		}       	      
	    		}
    		} else if (searchPropertiesPanel.getSearchProperties()[1] == 3) {
    			try {
    				Pattern.compile(Funcs.validate(searchingPhrase));
    			} catch (PatternSyntaxException exception) {
    	            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Регулярное выражение содержит ошибку!");
					return;
    			}
    		}
    		bottomPB = new JProgressBar();
    		bottomPB.setIndeterminate(true);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выполняется поиск...", bottomPB);
            dlg.hideOkButton();                
			sRunner = new SearchRunner(dlg);
    		sRunner.start();    			
			dlg.setVisible(true);			
    		if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
    			sRunner.setStop();        			
                srchBtn.setEnabled(true);
    		}
    	} else if (src == restoreBtn) {
    		cancelIdx.setVisible(false);
    		restoreBtn.setVisible(false);
    		toolbarPB.setVisible(false);
    		dlg.show();
    	} else if (src == cancelIdx) {
    		setStopThread(true);			
			while (iRunner.isAlive());
//			idxBtn.setEnabled(true);
			idxAllBtn.setEnabled(true);
			cancelIdx.setVisible(false);
			restoreBtn.setVisible(false);
			toolbarPB.setVisible(false);
    	}/* else if (src == idxBtn) {
            String mes = "Предшествующая индексация будет удалена. Продолжить?";
            int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mes);
            if (res == ButtonsFactory.BUTTON_YES) {
	    		Thread thread = new Thread() {
					public void run() {
						while(true) {
							if (!indexPropertiesPanel.getTimer().isRunning()) {
								indexDatabase(false);
								break;
							}
						}
						this.stop();
					}
				};
			thread.start();		
            }
    	}*/ else if (src == idxAllBtn) {
            String mes = "Вы действительно хотите запустить индексацию всех выбранных атрибутов? Это может занять длительное время.";
            int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mes);
            if (res == ButtonsFactory.BUTTON_YES) {
				Thread thread = new Thread() {
					public void run() {
						if (indexPropertiesPanel.getTimer() == null || !indexPropertiesPanel.getTimer().isRunning()) {
							int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
					                "Очистить результаты предыдущей индексации?");
							indexDatabase(true, res == ButtonsFactory.BUTTON_YES);
						} else {
							MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Индексация уже запущена!");
						}
					}
				};
				thread.start();			
            }
    	} else if (src == replaceBtn) {
        	JTable table = searchResult.getTable();
    		if (table.getSelectedRows().length == 0) {
            	MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Выберите объект из таблицы!");
    			return;
    		}
    		String oldContent = getSearchingText();
    		SearchOperationsWindow dialog = new SearchOperationsWindow("Замена выражения", 275, 125, 1, oldContent, null);
    		if (dialog.getSelection()) {    			
    			String newContent = Funcs.validate(Funcs.normalizeInput(dialog.getText()));
    			
    	    	oldContent = Funcs.validate(Funcs.normalizeInput(dialog.getWhatTextField().getText()));

    			boolean res = replaceSelectionNode(oldContent, newContent, dialog.isCaseSelected());
    			if (res) {
    				String nodeTitle = ((SearchNode) tree.getSelectedNode()).getTitle();
    				int selectedRow = table.getSelectedRow();    				
    				if (editedRows.containsKey(nodeTitle)) {
    					if (!editedRows.get(nodeTitle).contains(selectedRow)) {
    						List<Integer> indexes = new ArrayList<Integer>();
    						indexes.addAll(editedRows.get(nodeTitle));
    						indexes.add(selectedRow);
    						editedRows.put(nodeTitle, indexes);
    					}
    				} else {
    					editedRows.put(nodeTitle, Arrays.asList(selectedRow));
    				}
	    			SearchResult.CellRenderer renderer = searchResult.new CellRenderer(true, editedRows.get(nodeTitle));    			
	    			for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
	    				table.getColumnModel().getColumn(i).setCellRenderer(renderer);
	    			}
	    			table.validate();
	    			table.repaint();
    			}
    			MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, res ? "Процесс замены успешно завершен!" : "Замена не потребовалась!");
    		}       	            	   	
        } else if (src == searchModeCheck) {
        	if (searchModeCheck.isSelected()) {
				searchModeCheck.setToolTipText("Поиск по UID");
			} else {
				searchModeCheck.setToolTipText("Поиск по названию");
			}
        }
    }
    
    private boolean replaceSelectionNode (String oldContent, String newContent, boolean considerCase) {
    	JTable table = searchResult.getTable();
    	TableModel model = table.getModel();
    	int[] selectedRows = table.getSelectedRows();
    	boolean res = false; 
    	
    	for (int i=0; i < selectedRows.length; i++) {
    		String uid = ((SearchTableModel)model).getRealValueAt(selectedRows[i], 0);
    		boolean isMethod = "method".equals(((SearchTableModel)model).getRealValueAt(selectedRows[i], 4));
    		String attrId = ((SearchTableModel) model).getRealValueAt(selectedRows[i], 1);
    		long langId = Long.parseLong(((SearchTableModel) model).getRealValueAt(selectedRows[i], 2));
			
    		try {
    	    	newContent = Funcs.validate(Funcs.normalizeInput(newContent));
    	    	oldContent = Funcs.validate(Funcs.normalizeInput(oldContent));

    			if (isMethod) {
    				KrnMethod m = kernel.getMethodById(uid);
    				String oldStr = kernel.getMethodExpression(uid);
    				String newStr = replace(oldStr, oldContent, newContent, !considerCase);

					if (!newStr.equals(oldStr)) {
						kernel.changeMethod(uid, m.name, m.isClassMethod, newStr.getBytes());
						kernel.indexMethod(m);//переиндексирование метода после замены
						res = true;
					}
    			} else {
    				KrnObject obj = kernel.getObjectByUid(uid, 0);
    				if (obj != null) {
    					KrnAttribute attr = kernel.getAttributeById(Long.parseLong(attrId));
    					KrnClass cls = kernel.getClass(attr.classId);
    					byte[] oldBlob = kernel.getBlob(obj.id, attr, 0, langId, 0);
    					if (oldBlob != null && oldBlob.length > 0) {
    						String oldStr = new String(oldBlob,"UTF-8");
    						String newStr = replace(oldStr, oldContent, newContent, !considerCase);

    						if (!newStr.equals(oldStr)) {
    							kernel.setBlob(obj.id, attr.id, 0, newStr.getBytes("UTF-8"), langId, 0);
    							
    							String[] titles = kernel.getStrings(obj, "title", langId, 0);
    							String title = (titles!=null && titles.length>0) ? titles[0] : obj.uid;

        						if (cls.name.equals("UI")) {
        							try {
	        							InterfaceFrame frame = new InterfaceFrame(obj);
	        		                    frame.setInterfaceLang(kernel.getObjectById(kernel.getLangIdByCode("RU"), 0));
	        		                    frame.loadMass(null);
	        		                    frame.saveWebConfig(true);
	        		                    if (obj.uid.contains("."))
	        		                    	System.out.println("Finish HTML for UID=" + Funcs.sanitizeHtml(obj.uid));
        							} catch (Exception e) {
        								e.printStackTrace();
        							}
        		                    
        							kernel.writeLogRecord(SystemEvent.EVENT_CHANGE_INTERFACE, title);
        							kernel.interfaceChanged(obj.id);
        						}
        		               	else if (cls.name.equals("ProcessDef")) { 
        		               		kernel.reloadProcessDefinition(obj.id);
        		                    kernel.writeLogRecord(SystemEvent.EVENT_CHANGE_PROCESS, title);
        		               	} else if (cls.name.equals("Filter")) {
        		               		kernel.saveFilter(obj.id);
        		               	}
        						kernel.indexObject(obj, kernel.getClass(obj.classId), attr, true);//переиндексирование объекта после замены
        						res = true;
    						}
    					}
    				}
    			}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (KrnException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    	return res;
    }

    private String replace(String src, String toFind, String toReplace, boolean ignoreCase) {
    	if (ignoreCase) {
    		toFind = toFind.toLowerCase(Locale.ROOT);
    		String srcLow = src.toLowerCase(Locale.ROOT);
    		StringBuilder sb = new StringBuilder();
    		int lastPos = 0;
    		int pos = srcLow.indexOf(toFind, lastPos);
    		int i = 0;
    		
    		while (pos > -1 && i < Constants.MAX_ELEMENTS_COUNT_3) {
    			sb.append(srcLow.substring(lastPos, pos)).append(toReplace);
    			lastPos = pos + toFind.length();
    			pos = srcLow.indexOf(toFind, lastPos);
    			i++;
    		}
			sb.append(srcLow.substring(lastPos));
    		
			return sb.toString();
    	} else
    		return src.replace(toFind, toReplace);
    }
    
    public List<Object>  getSearchingInfo() {
    	SearchNode node = (SearchNode) tree.getSelectedNode();
		return searchingInfo.get(node.getTitle());
	}
    
    private void indexDatabase(boolean isFullIndexing, boolean clearIndexFolder) {
    	Map<Integer, Boolean> notIndexedAttributes = indexPropertiesPanel.getNotIndexedAttributes();
    	int isIdexingMethods = indexPropertiesPanel.isIdexingMethods();
    	int isIdexingTriggers = indexPropertiesPanel.isIdexingTriggers();
    	int isIdexingChanges = indexPropertiesPanel.isIdexingChanges();
    	bottomPB = new JProgressBar(0, attributes.size() - notIndexedAttributes.size() + isIdexingMethods + isIdexingTriggers+ isIdexingChanges);
    	GradientProgressBarUI bottomPBUI = new GradientProgressBarUI(Color.WHITE, Color.BLACK, Color.GRAY);
    	bottomPB.setUI(bottomPBUI);
    	bottomPB.setBorder(null);
    	bottomPB.setStringPainted(true);
    	bottomPB.setFont(new Font("Arial", Font.ITALIC, 12));
    	bottomPB.setOpaque(false);
	
    	topPB = new JProgressBar();
    	GradientProgressBarUI topPBUI = new GradientProgressBarUI(Color.WHITE, Color.BLACK, Color.GRAY);
    	topPB.setUI(topPBUI);
    	topPB.setBorder(null);
    	topPB.setStringPainted(true);
    	topPB.setFont(new Font("Arial", Font.ITALIC, 12));
    	topPB.setOpaque(false);
    	
    	GradientProgressBarUI toolbarPBUI = new GradientProgressBarUI(Color.WHITE, Color.BLACK, Color.GRAY);
    	toolbarPB.setMinimum(0);
    	toolbarPB.setMaximum(attributes.size() - notIndexedAttributes.size() + isIdexingMethods + isIdexingTriggers+ isIdexingChanges);
    	toolbarPB.setUI(toolbarPBUI);
    	toolbarPB.setBorder(null);
    	toolbarPB.setStringPainted(true);
    	toolbarPB.setFont(new Font("Arial", Font.ITALIC, 11));
    	toolbarPB.setOpaque(false);
    	toolbarPB.setMinimumSize(new Dimension(150, 15));
    	toolbarPB.setMaximumSize(new Dimension(150, 15));
    	toolbarPB.setPreferredSize(new Dimension(150, 15));
    	
    	JPanel mainPanel = new JPanel();
    	GridBagLayout layout = new GridBagLayout();
    	mainPanel.setLayout(layout);
    	mainPanel.setBackground(kz.tamur.rt.Utils.getLightSysColor());
    	mainPanel.setMinimumSize(new Dimension(500, 60));
    	mainPanel.setMaximumSize(new Dimension(500, 60));
    	mainPanel.setPreferredSize(new Dimension(500, 60));
    	mainPanel.add(bottomPB, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));	    	
    	mainPanel.add(topPB, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));	    	
    	mainPanel.setOpaque(isOpaque);
    	dlg = new DesignerDialog(Or3Frame.instance(), "Выполняется индексация базы", mainPanel, false, false, false, false, false, true);
    	dlg.getToBckBtn().setMinimumSize(new Dimension(130, 25));
    	dlg.getToBckBtn().setMaximumSize(new Dimension(130, 25));
    	dlg.getToBckBtn().setPreferredSize(new Dimension(130, 25));
    	dlg.getToBckBtn().setFocusable(false);
    	dlg.getToBckBtn().setFocusPainted(false);
    	dlg.getToBckBtn().setFont(new JButton().getFont());
    	dlg.getToBckBtn().setText("Фоновый режим");
    	dlg.getToBckBtn().addActionListener(new ActionListener() {				
			public void actionPerformed(ActionEvent e) {
				if (toolbarPB.isVisible() == false && restoreBtn.isVisible() == false && cancelIdx.isVisible() == false) {
					toolbarPB.setVisible(true);
	     			restoreBtn.setVisible(true);
	     			cancelIdx.setVisible(true);
				}
			}
		});
    	
    	dlg.getCancelBtn().setMinimumSize(new Dimension(90, 25));
    	dlg.getCancelBtn().setMaximumSize(new Dimension(90, 25));
    	dlg.getCancelBtn().setPreferredSize(new Dimension(90, 25));
    	dlg.getCancelBtn().setFocusable(false);
    	dlg.getCancelBtn().setFocusPainted(false);
    	dlg.getCancelBtn().setFont(new JButton().getFont());
    	dlg.getCancelBtn().setText("Завершить");
    	dlg.getCancelBtn().addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
				setStopThread(true);
				while (iRunner.isAlive());
//				idxBtn.setEnabled(true);
				idxAllBtn.setEnabled(true);
				dlg.dispose();
			}
		});

    	dlg.hideOkButton();
        dlg.setResizable(false);
		iRunner = new IndexRunner(dlg, bottomPB, topPB, toolbarPB, attributes, notIndexedAttributes, isIdexingMethods, isIdexingTriggers, isIdexingChanges, isFullIndexing, clearIndexFolder);
		setStopThread(false);
		iRunner.start();
		dlg.setVisible(true);			
    }

    private void addHistSearch(String text, String sIcon, java.util.List<String[]> list) {
    	try {
    		SearchTree.SearchTreeModel model = (SearchTree.SearchTreeModel) tree.getModel();
    		SearchNode node = (SearchNode) model.createChildNode(text, sIcon, list);
    		if (node != null) {
    			tree.setSelectedNode(node);
    		}
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }
    
    private void deleteSelected() {	
        SearchNode node = (SearchNode)tree.getSelectedNode();
        if (node.isLeaf()) {
        	String mess = "Вы действительно хотите удалить результаты поиска '";
            mess += node.toString() + "'?";
            int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.QUESTION_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
            	try	{
            		SearchTree.SearchTreeModel model = ( SearchTree.SearchTreeModel)tree.getModel();
        			tree.setSelectionRow(tree.getSelectionRows()[0] - 1);
            		model.deleteNode(node, true);
            	} catch (KrnException e) {
            		e.printStackTrace();
            	}
            }
        }
    }

	public void valueChanged(TreeSelectionEvent e) {
		SearchNode node = (SearchNode) tree.getSelectedNode();
		if (node != null && !node.equals(node.getRoot())) {
			replaceBtn.setEnabled(true);
			setSearchingMode((Integer) searchingInfo.get(node.getTitle()).get(0));
			searchPropertiesPanel.setSearchProperties((int[]) searchingInfo.get(node.getTitle()).get(1));
			textField.setText(String.valueOf(searchingInfo.get(node.getTitle()).get(2)));
			searchPropertiesPanel.setSearchArea((boolean[]) searchingInfo.get(node.getTitle()).get(4));
		} else {
			replaceBtn.setEnabled(false);
			textField.setText("");
			searchPropertiesPanel.setSearchProperties(new int[] {0, 1});
		}
		searchResult.init(node);
		SearchResult.CellRenderer renderer = searchResult.new CellRenderer(true, editedRows.get(((SearchNode) tree.getSelectedNode()).getTitle()));    			
		for (int i = 0; i < searchResult.getTable().getColumnModel().getColumnCount(); i++) {
			searchResult.getTable().getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
		rightPanel.removeAll();
		rightPanel.add(searchResult, BorderLayout.CENTER);
		rightPanel.validate();
	}

	public int processExit() {
		return 0;
	}
	
	private String getNodeTitle() {
		SearchNode node = (SearchNode) tree.getSelectedNode();
		if (!node.equals((SearchNode) tree.getRoot())) {
			return node.getTitle();
		} else {
			return null;
		}
	}
	
	public List<String> getSearchingWords() {
		List<String> searchingWords = new ArrayList<String>();
		String[] words = getNodeTitle().split(" ");
		for (int i = 0; i < words.length; i++) {
			searchingWords.add(words[i]);
		}
		return searchingWords; 
	}
	
	public String getSearchingText() {
		String inputString = Funcs.validate(Funcs.normalizeInput(textField.getText()).trim());
		String objectUIDSting = null;
		String objectNameString = null;
		Pattern pattern = Pattern.compile("(\"{1}([\\pLа-яa-zА-ЯA-Z_0-9|\\.| |+|)|(|_]*)\"{1} {1})([0-9]+\\.{1}[0-9]+)", Pattern.UNICODE_CASE);		//1 группа - название объекта (в ковычках и с пробелом на конце)
																																					//2 группа - название объекта
		if (searchModeCheck.isEnabled()) {
			if (searchModeCheck.isSelected()) {
				Matcher matcher = pattern.matcher(inputString);
				if (matcher.find()) {
					objectUIDSting = matcher.group(3);
				}
				return objectUIDSting;
			} else {
				Matcher matcher = pattern.matcher(inputString);
				if (matcher.find()) {
					objectNameString = matcher.group(2);
				}
				return objectNameString;
			}
		} else {				
			return inputString;				
		}		
	}
	
	private synchronized void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}
	
	private synchronized boolean isStopThread() {
		return stopThread;
	}
	
	private int getSearchingMode() {
		return searchModeCheck.isSelected() ? 1 : 0;
	}
	
	private void setSearchingMode(int searchingMode) {
		searchModeCheck.setSelected(searchingMode == 1 ? true : false);
	}
	
	class SearchRunner extends Thread {
		
		private boolean isStop=false;
		DesignerDialog dialog;
		
        public SearchRunner(DesignerDialog dialog) {
        	super();
        	this.dialog = dialog;
        }
        public void setStop(){
        	this.isStop=true;
        }
        public void run() {
        	
        	long begin = System.currentTimeMillis();
        	
        	srchBtn.setEnabled(false);
        	textField.setEditable(false);
    		final java.util.List<String[]> list = new java.util.ArrayList<String[]>();
			try {
				if(isStop) throw new Exception();
				List<Object> results = kernel.search(getSearchingText(), 300, searchPropertiesPanel.getSearchProperties(), searchPropertiesPanel.getSearchArea());
				if(isStop) throw new Exception();
				
				long end = System.currentTimeMillis();
				System.out.println("search 1 (from server) = " + (end - begin));
				begin = end;
				
				if (results != null && results.size() > 1) {
					List<String[]> objects = (List<String[]>) results.get(0);
					final List<String> queries = (List<String>) results.get(1);
					for (int i = 0; i < 12; i++) {
						groups.get(i).clear();
					}		
					int count = objects.size();

					Map<Long, List<Long>> idsByClassId = new HashMap<>();
					Map<String, String[]> objsByUID = new HashMap<>();
					Map<Long, String> uidsById = new HashMap<>();
					
					for (int i = 0; i < count && i < Constants.MAX_ELEMENTS_COUNT_1; i++) {
						String[] object = objects.get(i);
						String objUID = object[0];
						String typeObj = object[4];
						
						String[] objByUID = new String[8];
						for(int k=0; k < object.length; k++)
							objByUID[k] = object[k];
						
						objsByUID.put(objUID, objByUID);
						
						if ("class".equals(typeObj)) {
							KrnObject obj = kz.tamur.rt.Utils.getObjectByUid(objUID, 0);
							if (obj != null) {
								uidsById.put(obj.id, objUID);
								KrnClass cls = Kernel.instance().getClass(obj.classId);
								objByUID[5] = cls.name;
								List<Long> ids = idsByClassId.get(cls.id);
								if (ids == null) {
									ids = new ArrayList<>();
									idsByClassId.put(cls.id, ids);
								}
								ids.add(obj.id);
							}
						}
					}
					
					end = System.currentTimeMillis();
					System.out.println("search 2 (getObjectByUid) = " + (end - begin));
					begin = end;
					
					long msdocClsId = Kernel.instance().getClassByName("MSDoc").id;
					
					for (Long clsId : idsByClassId.keySet()) {
						long[] ids = com.cifs.or2.util.Funcs.makeLongArray(idsByClassId.get(clsId));
						
						try {
							StringValue[] svs = (clsId == msdocClsId)
									? Kernel.instance().getStringValues(ids, clsId, "filename", 0, false, 0)
									: Kernel.instance().getStringValues(ids, clsId, "title", 0, false, 0);
							
							for (StringValue sv : svs) {
								String[] objByUID = objsByUID.get(uidsById.get(sv.objectId));
								if (objByUID != null)
									objByUID[6] = sv.value;
							}
						} catch (Exception e) {e.printStackTrace();}
					}
					
					end = System.currentTimeMillis();
					System.out.println("search 3 (titles) = " + (end - begin));
					begin = end;
					
					for (int i = 0; i < count && i < Constants.MAX_ELEMENTS_COUNT_1; i++) {
						String[] initObject = objects.get(i);
						
						List<String> modifiedObject = modifyObject(initObject, objsByUID);

						String[] object = new String[8];
						for(int k=0; k<modifiedObject.size(); k++)
							object[k] = modifiedObject.get(k);
			        	
						if(isStop) throw new Exception();

						String className = object[7];

						if (className.equals("UI")) {					//Интерфейс
							groups.get(0).add(object);
						} else if (className.equals("ProcessDef")) {	//Процесс
							groups.get(1).add(object);
						} else if (className.equals("User")) {			//Пользователь
							groups.get(2).add(object);
						} else if (className.equals("Filter")) {		//Фильтр
							groups.get(3).add(object);
						} else if (className.equals("ReportPrinter")) {	//Отчет
							groups.get(4).add(object);
						} else if (className.equals("<Methods>")) {		//Метод
							groups.get(5).add(object);
						} else if (className.equals("class")) {			//Класс
							groups.get(6).add(object);
						} else if (className.equals("Unaccessible")) {	//Не определено
							groups.get(7).add(object);
						} else if (className.equals("<Triggers>")) {		//Триггер
							groups.get(8).add(object);
						} else if (className.equals("<Changes>")) {		//Изменение объектов контроля
							groups.get(9).add(object);
						} else if (className.equals("MSDoc")) {		//MsDoc
							groups.get(10).add(object);
						} else {		                               // Класс объектов не входящих в список
							groups.get(11).add(object);
						}
					}

					end = System.currentTimeMillis();
					System.out.println("search 4 (group) = " + (end - begin));
					begin = end;

					UIDComparator comparator = new UIDComparator();
					for (int i = 0; i < 12; i++) {
						Collections.sort(groups.get(i), comparator);
						list.addAll(groups.get(i)); 
					}
					dialog.dispose();
					
					end = System.currentTimeMillis();
					System.out.println("search 5 (sort) = " + (end - begin));
					begin = end;

					EventQueue.invokeLater(new Runnable() {
						
						@Override
						public void run() {
				            if (list.size() > 0) {
				            	List<Object> info = new ArrayList<Object>();
				            	info.add(getSearchingMode());							// Поиск по названию или по UID
					        	info.add(searchPropertiesPanel.getSearchProperties());	// Тип поиска
					        	info.add(textField.getText());							// Значение текстового поля
					        	info.add(queries);
					        	info.add(searchPropertiesPanel.getSearchArea());		// Область поиска
					        	String nodeTitle = getSearchingText() + " " + searchPropertiesPanel.getSearchAreaString().replace("[", "(").replace("]", ")");
					        	searchingInfo.put(nodeTitle, info);
					        	addHistSearch(nodeTitle, iconTreeNode, list);
				            } else {
				                MessagesFactory.showMessageNotFound(getTopLevelAncestor());
				            	tree.setSelectionRow(0);
				            }
						}
					});
				}
	        							// Запрос, построенный для поиска
			} catch (KrnException e) {
				e.printStackTrace();
			} catch (Throwable ex) {
				System.out.println("Cancel by user!!!!");
				ex.printStackTrace();
			}
        	textField.setEditable(true);
            srchBtn.setEnabled(true);
        }
    }
	
	class UIDComparator implements Comparator<String[]> {
		public int compare(String[] result_1, String[] result_2) {
			return result_1[0].compareTo(result_2[0]);
		}
	}
	
	class IndexRunner extends Thread {
		
		DesignerDialog dialog;
		JProgressBar bottomPB;
		JProgressBar topPB;
		JProgressBar toolbarPB;
		List<KrnAttribute> attributes;
		Map<Integer, Boolean> notIndexedAttributes;
		int isIdexingMethods;
		int isIdexingTriggers;
		int isIdexingChanges;
		boolean fullIndexing;
		boolean clearIndexFolder;
		StringBuilder lastIndexingInfo = new StringBuilder();
		
		public IndexRunner(DesignerDialog dialog, JProgressBar bottomPB, JProgressBar topPB, JProgressBar toolbarPB, List<KrnAttribute> attributes, Map<Integer, Boolean> notIndexedAttributes, 
				int isIdexingMethods, int isIdexingTriggers, int isIdexingChanges, boolean fullIndexing, boolean clearIndexFolder) {
        	super();
        	this.dialog = dialog;
        	this.bottomPB = bottomPB;
        	this.topPB = topPB;
        	this.toolbarPB = toolbarPB;
        	this.attributes = attributes;
        	this.notIndexedAttributes = notIndexedAttributes;
        	this.isIdexingMethods = isIdexingMethods;
        	this.isIdexingTriggers = isIdexingTriggers;
        	this.isIdexingChanges = isIdexingChanges;
        	this.fullIndexing = fullIndexing;
        	this.clearIndexFolder = clearIndexFolder;
        }
		
		public void run() {
//			idxBtn.setEnabled(false);
			idxAllBtn.setEnabled(false);
			long clId;
			KrnClass kClass;
			
			try {
				Map<String, String> configuration = indexPropertiesPanel.getUserConfiguration();
				kernel.setString(user.getObject().id, user.getObject().classId, "lastIndexingConfig", 0, 0, configuration.get("Analyzer Type"), 0);
				kernel.setString(user.getObject().id, user.getObject().classId, "lastIndexingConfig", 1, 0, configuration.get("Attributes Status"), 0);
				kernel.setString(user.getObject().id, user.getObject().classId, "lastIndexingConfig", 2, 0, configuration.get("Methods Status"), 0);
				kernel.setString(user.getObject().id, user.getObject().classId, "lastIndexingConfig", 3, 0, configuration.get("Triggers Status"), 0);
			} catch (KrnException e) {
				e.printStackTrace();
			}

			try {
				if (clearIndexFolder) {
					boolean b = kernel.dropIndexFolder();
					if (b) {
						System.out.println("Хранилище записей индексов очищено.");
					}
				}
			} catch (KrnException e) {
				System.out.println("Ошибка при определении директории хранилища записей индексов.");
			}
			int current = 1;
			int current_log = 1;
			long start = 0;
			
			lastIndexingInfo.setLength(0);
			lastIndexingInfo.append("Индексация атрибутов и методов\n");
			lastIndexingInfo.append("Пользователь: " + user.getName() + "\n");
			start = getProcessInfo("Start", start);
			lastIndexingInfo.append("Время запуска процесса индексации: " + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date(start)) + "\n");
			lastIndexingInfo.append("Количество найденных атрибутов: " + attributes.size() + "\n");
			lastIndexingInfo.append("Количество выбранных атрибутов: " + (attributes.size() - notIndexedAttributes.size()) + "\n");
			
			/*//start
			KrnAttribute attr=null;
			try {
				attributes.clear();
				attr=kernel.getAttributeById(4293);
				attributes.add(attr);
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//end*/
			
			for (int i = 0; i < attributes.size(); i++) {
				if (isStopThread()) {
					getProcessInfo("Stop", start);
					return;
				}
				KrnAttribute attribute = attributes.get(i);
				if (notIndexedAttributes.containsKey(i)) {
					lastIndexingInfo.append(current + "/" + attributes.size() + ". Атрибут '" + attribute.name + "' пропущен\n");
					current++;
					continue;
				}
				bottomPB.setValue(bottomPB.getValue() + 1);
				toolbarPB.setValue(toolbarPB.getValue() + 1);
				bottomPB.setString("Атрибут: \"" + attribute.name + "\"");
				toolbarPB.setString("Атрибут: \"" + attribute.name + "\"");
				topPB.setValue(0);
				topPB.setString("0%");
				try {
					clId = attribute.classId;
					kClass = kernel.getClass(clId);
					KrnObject [] kObjects = kernel.getClassObjects(kClass, 0);
					/*//start
					KrnObject tObj=kernel.getObjectById(397426, 0);
					KrnObject tObj1=kernel.getObjectById(15575574, 0);
					KrnObject tObj2=kernel.getObjectById(4374, 0);
					KrnObject tObj3=kernel.getObjectById(4315152, 0);
					kObjects =new KrnObject[]{tObj,tObj1,tObj2,tObj3};
					//end*/
					lastIndexingInfo.append(current + "/" + attributes.size() + ". Атрибут '" + attribute.name + "' проиндексирован, найдено " + kObjects.length +  " объектов.\n");
					System.out.println(current_log + "/" + (attributes.size() - notIndexedAttributes.size())  + ". Индексация атрибута: '" + attribute.name + "'. Найдено " + kObjects.length +  " объектов.");
					topPB.setMaximum(kObjects.length);

					lastIndexingInfo.append("Обнуление индексной папки атрибута: '" + attribute.name + ".\n");
					if (!clearIndexFolder) kernel.dropIndex(attribute);
					for (int j = 0; j < kObjects.length; j++) {
						KrnObject kObject = kObjects[j];
						if (isStopThread()) {
							getProcessInfo("Stop", start);
							return;
						}
						kernel.indexObject(kObject, kClass, attribute, fullIndexing);
						topPB.setValue(topPB.getValue() + 1);
						topPB.setString((float) (topPB.getValue() * 10000 / kObjects.length) / 100 + "%");
					}					
				} catch (KrnException e) {
					e.printStackTrace();
				}
				current++;
				current_log++;
			}
			
			if (isIdexingMethods == 1) {
				current = 1;
				bottomPB.setValue(bottomPB.getValue() + 1);
				toolbarPB.setValue(toolbarPB.getValue() + 1);
				bottomPB.setString("Индексация методов");
				toolbarPB.setString("Индексация методов");
				topPB.setValue(0);
				topPB.setString("0%");
				topPB.setMaximum(methods.size());
				lastIndexingInfo.append("Количество найденных методов: " + methods.size() + "\n");
				lastIndexingInfo.append("Обнуление индексной папки методов.\n");
				try {
					if (!clearIndexFolder) kernel.dropIndex(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (int i = 0; i < methods.size(); i++) {
					if (isStopThread()) {
						getProcessInfo("Stop", start);
						return;
					}
					KrnMethod method = methods.get(i);
					System.out.println(current + "/" + methods.size() + ". Индексация метода: '" + method.name + "'.");
					try {
						kernel.indexMethod(method);
						lastIndexingInfo.append(current + "/" + methods.size() + ". Метод '" + method.name + "' проиндексирован.\n");
						topPB.setValue(topPB.getValue() + 1);
						topPB.setString((float) (topPB.getValue() * 10000 / methods.size()) / 100 + "%");
					} catch (Exception e) {
						e.printStackTrace();
					}
					current++;
				}
			} else {
				lastIndexingInfo.append("Индексация методов пропущена.\n");
			}
			
			if (isIdexingTriggers == 1) {
				current = 1;
				bottomPB.setValue(bottomPB.getValue() + 1);
				toolbarPB.setValue(toolbarPB.getValue() + 1);
				bottomPB.setString("Индексация триггеров");
				toolbarPB.setString("Индексация триггеров");
				topPB.setValue(0);
				topPB.setString("0%");
				
				List<OrlangTriggerInfo> triggers = new ArrayList<OrlangTriggerInfo>();
				try {
					triggers = kernel.getOrlangTriggersInfo();
				} catch (KrnException e) {
					e.printStackTrace();
				}
				topPB.setMaximum(triggers.size());
				lastIndexingInfo.append("Количество найденных триггеров: " + triggers.size() + "\n");
				lastIndexingInfo.append("Обнуление индексной папки триггеров.\n");
				try {
					if (!clearIndexFolder) kernel.dropTriggersIndexFolder();
				} catch (Exception e) {
					e.printStackTrace();
				}
				OrlangTriggerInfo trigger;
				for (int i = 0; i < triggers.size(); i++) {
					if (isStopThread()) {
						getProcessInfo("Stop", start);
						return;
					}
					trigger = triggers.get(i);
					System.out.println(current + "/" + triggers.size() + ". Индексация триггера: " + (trigger.getOwnerType() == 0 ? "класс - " : "атрибут - ") + trigger.getOwnerName() + " (" + trigger.getOwnerId() + "), " + "событие - '" + trigger.getName() + "'.");
					try {
						kernel.indexTrigger(trigger);
						lastIndexingInfo.append(current + "/" + triggers.size() + ". Триггер '" + trigger.getName() + "' " + (trigger.getOwnerType() == 0 ? "класса - " : "атрибута - ") + trigger.getOwnerName() + " (" + trigger.getOwnerId() + ") проиндексирован.");
						topPB.setValue(topPB.getValue() + 1);
						topPB.setString((float) (topPB.getValue() * 10000 / triggers.size()) / 100 + "%");
					} catch (Exception e) {
						e.printStackTrace();
					}
					current++;
				}
			} else {
				lastIndexingInfo.append("Индексация триггеров пропущена.\n");
			}
			if (isIdexingChanges == 1) {
				current = 1;
				bottomPB.setValue(bottomPB.getValue() + 1);
				toolbarPB.setValue(toolbarPB.getValue() + 1);
				bottomPB.setString("Индексация изменений");
				toolbarPB.setString("Индексация изменений");
				topPB.setValue(0);
				topPB.setString("0%");
				
				List<KrnVcsChange> changes = new ArrayList<KrnVcsChange>();
				try {
					changes = kernel.getVcsDifChanges(true,null);
				} catch (KrnException e) {
					e.printStackTrace();
				}
				topPB.setMaximum(changes.size());
				lastIndexingInfo.append("Количество найденных изменений: " + changes.size() + "\n");
				lastIndexingInfo.append("Обнуление индексной папки изменений.\n");
				try {
					if (!clearIndexFolder) kernel.dropVcsChangesIndexFolder();
				} catch (Exception e) {
					e.printStackTrace();
				}
				KrnVcsChange change;
				for (int i = 0; i < changes.size(); i++) {
					if (isStopThread()) {
						getProcessInfo("Stop", start);
						return;
					}
					change = changes.get(i);
					System.out.println(current + "/" + changes.size() + ". Индексация изменений: '" + change.id+";"+ change.title+"'.");
					try {
						kernel.indexVcsChange(change);
						lastIndexingInfo.append(current + "/" + changes.size() + ". Объект контроля '" + change.title + "' проиндексирован.");
						topPB.setValue(topPB.getValue() + 1);
						topPB.setString((float) (topPB.getValue() * 10000 / changes.size()) / 100 + "%");
					} catch (Exception e) {
						e.printStackTrace();
					}
					current++;
				}
			} else {
				lastIndexingInfo.append("Индексация изменений пропущена.\n");
			}
			getProcessInfo("Finish", start);
			
			MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Процесс индексации успешно завершен!");			
			if (dialog.isShowing())
				dialog.dispose();
			else {
				cancelIdx.setVisible(false);
				restoreBtn.setVisible(false);
				toolbarPB.setVisible(false);
			}
//			idxBtn.setEnabled(true);
			idxAllBtn.setEnabled(true);
		}
		
		/** Метод, возвращающий информацию о результате выполнения индексации
		 * @param text - текст инструкции
		 * @param start - время начало индексации (в миллисекундах)
		 * @return Возвращает текущее время (в миллисекундах) и выводит на консоль информацию о выполнении индексации
		 * */
		private long getProcessInfo(String text, long start) {
			GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
			long milliseconds = calendar.getTimeInMillis() - start;
			if (text.equals("Start")) {
				System.out.println("Процесс индексирования запущен. Время " + dateFormat.format(calendar.getTime()) + ".");
			} else if (text.equals("Stop")) {
				System.out.println("Процесс индексирования прерван. Время " + dateFormat.format(calendar.getTime()) + ".");
				System.out.println("Время выполнения процесса " + String.format("%d:%d:%d", TimeUnit.MILLISECONDS.toHours(milliseconds), TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))));
				lastIndexingInfo.append("Неизвестное прерывание индексации: " + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(calendar.getTime()));
				setLastIndexingInfo();
			} else if (text.equals("Finish")) {
				System.out.println("Индексирование успешно завершено. Время " + dateFormat.format(calendar.getTime()) + ".");
				System.out.println("Время выполнения процесса " + String.format("%d:%d:%d", TimeUnit.MILLISECONDS.toHours(milliseconds), TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))));
				lastIndexingInfo.append("Успешное завершение индексации: " + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(calendar.getTime()));
				setLastIndexingInfo();
			}
			return calendar.getTimeInMillis();
		}
		
		private void setLastIndexingInfo() {
			try {
				kernel.setLastIndexingInfo(lastIndexingInfo.toString());
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
	}
}
