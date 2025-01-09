package kz.tamur.guidesigner.filters;

import static kz.tamur.comps.Utils.getCenterLocationPoint;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.admin.clsbrow.ObjectBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Factories;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent.FilterRecord;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.props.InspectorOwner;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.Utils;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DateField;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.LangItem;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.ObjectList;
import kz.tamur.util.OpenElementPanel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * User: vital
 * Date: 04.12.2004
 * Time: 17:14:05
 */
public class FiltersPanel extends JPanel implements ActionListener, TreeSelectionListener, InspectorOwner {

    private JSplitPane basicSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
        public void remove(Component component) {
            super.remove(component);
        };
    };
    JLabel errorMsg  = new JLabel();
    JPanel inputPanel = new JPanel();
    JSplitPane rightComponents  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    JTextArea filterResultsArea = new JTextArea();
    JPanel objectBrowser=null;
    //JScrollPane scrollPane = new JScrollPane(filterResultsArea);
    JScrollPane scrollPane;
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private static ControlTabbedContent tabbedContent = ServiceControl.instance().getContentTabs();
    private PropertyInspector inspector = new PropertyInspector(this);
    JPanel filterResultsPanel = new JPanel();
    private JButton historyBtn = createToolButton("history.png", "История");
    private JButton newBtnLocal = ButtonsFactory.createToolButton("CreateLocal", "Создать новый локальный фильтр");
//    private JButton openBtnLocal = ButtonsFactory.createToolButton("OpenLocal", "Открыть существующий локальный фильтр");
//    private JButton saveBtnLocal = ButtonsFactory.createToolButton("SaveLocal", "Сохранить локальный фильтр");
    private JButton newBtn = ButtonsFactory.createToolButton("Create", "Создать новый фильтр");
    private JButton openBtn = ButtonsFactory.createToolButton("Open", "Открыть существующий фильтр");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton newInNodeBtn = ButtonsFactory.createToolButton("createInNode", "Создать внутри узла");
    private JButton newBeforNodeBtn = ButtonsFactory.createToolButton("createBeforeNode", "Создать перед узлом");
    private JButton delNodeBtn = ButtonsFactory.createToolButton("Delete", "Удалить узел фильтра");
    private JButton cutNodeBtn = ButtonsFactory.createToolButton("Cut", "Вырезать узел фильтра");
    private JButton replaceFilteredAttrBut = ButtonsFactory.createToolButton("ReplaceAttr", ".png", "Заменить фильтруемый атрибут");
    private JButton replaceBut = ButtonsFactory.createToolButton("S&R", "Заменить");
    private JButton insInNodeBtn = ButtonsFactory.createToolButton("pasteInNode", "Вставить внутри узла");
    private JButton insBeforNodeBtn = ButtonsFactory.createToolButton("pasteBeforeNode", "Вставить перед узлом");
    private JButton copyNodeBtn = ButtonsFactory.createToolButton("Copy", "Скопировать узел фильтра");
    private JButton filterHistoryBtn = ButtonsFactory.createToolButton("ServiceHistory.gif", "История изменений");
    private JButton filterBtn = ButtonsFactory.createToolButton("runDebug", "Запуск фильтра");
    private JButton importBtn = new JButton("Импорт");
    private JButton exportBtn = new JButton("Экспорт");
    private FiltersTree tree;

    private boolean isInspectorFloat = false;
    private boolean isOwnerFilters = false;
    List<KrnObject> krnobj = new ArrayList<KrnObject>();
    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private LanguageCombo langSelector = new LanguageCombo();
    private long langId = com.cifs.or2.client.Utils.getInterfaceLangId();

    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");

    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JTextField uidView = new JTextField();
    private JLabel filterOwner = kz.tamur.rt.Utils.createLabel("");
    private OrFilterNode localNode=null;//фильтр на локальном комаьютере
    private KrnObject localFobj=null;
    GridBagConstraints c = new GridBagConstraints();
    
    public void setOwner(KrnObject obj) {
    	if(obj != null)
    		filterOwner.setText("Владелец: " + (kz.tamur.comps.Utils.getObjOwner(obj)!= null? kz.tamur.comps.Utils.getObjOwner(obj): ""));
    }

    public FiltersPanel(boolean isOwnerFilters) {
        super(new BorderLayout());
        this.isOwnerFilters = isOwnerFilters;
        init();
    }

    private void init() {
        errorMsg.setForeground(Color.RED);
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.FILTERS_CREATE_RIGHT);
        updateStatusBar();
        tree = kz.tamur.comps.Utils.getFiltersTreeContent(tabbedContent);
        tabbedContent.setTree(tree);
        newBtn.addActionListener(this);
        historyBtn.addActionListener(this);
        openBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        newBtnLocal.addActionListener(this);
//        openBtnLocal.addActionListener(this);
//        saveBtnLocal.addActionListener(this);
        newInNodeBtn.addActionListener(this);
        newBeforNodeBtn.addActionListener(this);
        delNodeBtn.addActionListener(this);
        cutNodeBtn.addActionListener(this);
        copyNodeBtn.addActionListener(this);
        replaceFilteredAttrBut.addActionListener(this);
        replaceBut.addActionListener(this);
        insInNodeBtn.addActionListener(this);
        insBeforNodeBtn.addActionListener(this);
        filterBtn.addActionListener(this);
        filterHistoryBtn.addActionListener(this);
        importBtn.addActionListener(this);
        exportBtn.addActionListener(this);
        newBtn.setEnabled(false);

        if (!isOwnerFilters) {
            toolBar.add(newBtn);
            newBtn.setEnabled(canCreate);
            toolBar.add(openBtn);
            toolBar.add(historyBtn);
        }
        toolBar.add(saveBtn);
        toolBar.add(newBtnLocal);
//        toolBar.add(openBtnLocal);
//        toolBar.add(saveBtnLocal);
        toolBar.add(newInNodeBtn);
        toolBar.add(newBeforNodeBtn);
        toolBar.add(copyNodeBtn);
        toolBar.add(delNodeBtn);
        toolBar.add(cutNodeBtn);
        toolBar.add(insInNodeBtn);
        toolBar.add(insBeforNodeBtn);
        toolBar.add(replaceFilteredAttrBut);
        toolBar.add(replaceBut);
        toolBar.add(filterHistoryBtn); 
        toolBar.add(filterBtn); 
        /*toolBar.add(importBtn); // Это зачем тут? проектировщикам мешается
        toolBar.add(exportBtn);*/
        filterResultsPanel.setLayout(new GridLayout(1,1));
        scrollPane = new JScrollPane(filterResultsPanel);
        rightSplitPane.setTopComponent(scrollPane);
        JScrollPane sp = new JScrollPane(inputPanel);
        rightSplitPane.setBottomComponent(sp);
        rightComponents.setLeftComponent(tabbedContent);
        rightComponents.setRightComponent(rightSplitPane);

        basicSplit.setLeftComponent(inspector);
        basicSplit.setRightComponent(rightComponents);

        add(toolBar, BorderLayout.NORTH);
        add(basicSplit, BorderLayout.CENTER);
        tabbedContent.setFilterPanel(this);
        TabbedChangeAdapter tca = new TabbedChangeAdapter();
        tabbedContent.addChangeListener(tca);
        tabbedContent.addContainerListener(tca);
        saveBtn.setEnabled(false);
//        saveBtnLocal.setEnabled(false);
        newInNodeBtn.setEnabled(false);
        newBeforNodeBtn.setEnabled(false);
        delNodeBtn.setEnabled(false);
        cutNodeBtn.setEnabled(false);
        copyNodeBtn.setEnabled(false);
        replaceFilteredAttrBut.setEnabled(false);
        replaceBut.setEnabled(false);
        insInNodeBtn.setEnabled(false);
        insBeforNodeBtn.setEnabled(false);
        filterHistoryBtn.setEnabled(false);
        filterBtn.setEnabled(false);
        tabbedContent.fireChange();

        statusBar.addEmptySpace();
        statusBar.addSeparator();
        statusBar.addLabel("UID: ");
        statusBar.addTextField(uidView);        
        statusBar.addSeparator();
        statusBar.addLabel(filterOwner);
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
        statusBar.addSeparator();

        statusBar.addLabel(" Язык : ");
        statusBar.addAnyComponent(langSelector);
        statusBar.addCorner();
        add(statusBar, BorderLayout.SOUTH);
        langSelector.addActionListener(this);
        inspector.getDialog("Фильтры");

        setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        basicSplit.setOpaque(isOpaque);
    }

    public void placeDividers() {
        basicSplit.setDividerLocation(200);
        rightComponents.setDividerLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 500);
        rightSplitPane.setDividerLocation(0.7);
        validate();
    }

    public void setSaveEnabled(boolean isEnabled) {
        saveBtn.setEnabled(isEnabled && canEdit /*&& !tabbedContent.getSelectedFilter().getRoot().equals(localNode)*/);
 //   	saveBtnLocal.setEnabled(tabbedContent.getSelectedIndex()>=0);
  }

    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();
            LangItem li = (LangItem) langSelector.getSelectedItem();
            if (src == newBtn) {
                create(null);
            }  else if (src == historyBtn) {
                showHistory((JComponent)src);
            } else if (src == openBtn) {
                open(src);
            } else if (src == newBtnLocal) {//фильтр на локальном комаьютере
                createLocal();
            }/* else if (src == openBtnLocal) {//фильтр на локальном комаьютере
                openLocal();
            } else if (src == saveBtnLocal) {//фильтр на локальном комаьютере
                saveLocal();
            } */
            else if(src == importBtn)
            {
                importFilterFromXmlFile();
            }
            else if(src == exportBtn)
            {
                open(src);
            }
            else if (src == saveBtn) {
                tabbedContent.saveAll();
                saveBtn.setEnabled(false);
            } else if (src == langSelector) {
                setInterfaceLanguage();
                TreeUIDMap.clearMap(AbstractDesignerTreeNode.FILTER_NODE);
                tree.setLangId(li.obj.id);
            } else if (tabbedContent.getComponentCount() >= 1) {
                OrFilterTree ftree = tabbedContent.getSelectedFilter();
                if (ftree == null)
                    return;
                if (src == newInNodeBtn) {
                    ftree.create(OrFilterTree.AFTER);
                } else if (src == newBeforNodeBtn) {
                    ftree.create(OrFilterTree.BEFOR);
                } else if (src == insInNodeBtn) {
                    ftree.paste(OrFilterTree.AFTER);
                    insBeforNodeBtn.setEnabled(false);
                    insInNodeBtn.setEnabled(false);
                } else if (src == insBeforNodeBtn) {
                    ftree.paste(OrFilterTree.BEFOR);
                    insBeforNodeBtn.setEnabled(false);
                    insInNodeBtn.setEnabled(false);
                } else if (src == copyNodeBtn) {
                    ftree.copy();
                    insBeforNodeBtn.setEnabled(true);
                    insInNodeBtn.setEnabled(true);
                } else if (src == replaceFilteredAttrBut) {  
                	replaceFilteredAttr();
                } else if (src == replaceBut) {  
                	replace();
                } else if (src == cutNodeBtn) {
                    ftree.cut();
                    insBeforNodeBtn.setEnabled(true);
                    insInNodeBtn.setEnabled(true);
                } else if (src == delNodeBtn) {
                    ftree.delete();
                } else if(src == filterHistoryBtn){
                	KrnObject obj=ftree.getObj();
                	String uid=obj.uid;
                	List<KrnVcsChange> changes=Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, uid);
                	if(changes.size()>0) {
                		Or3Frame.historysPanel.refreshTable(changes.get(0), true);
                	}
                    DesignerDialog dlg  = new DesignerDialog((Window) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                    dlg.setMinimumSize(new Dimension(900, 70));
                    dlg.show();
                } else if(src == filterBtn){
                    clsNames.clear();
                    paramNames.clear();
                    clearRightComponents();
                    populateInputPanel();
                    nodesWithParams.clear();
                } else if (objectBrowser!=null && src == ((ObjectBrowser) objectBrowser).floatModeBtn) {
                    if (!isOpaque) {
                    	objectBrowser.setOpaque(true);
                    	((GradientPanel) objectBrowser).setGradient(MainFrame.GRADIENT_MAIN_FRAME.isEmpty() ? kz.tamur.comps.Constants.GLOBAL_DEF_GRADIENT : MainFrame.GRADIENT_MAIN_FRAME);
                    }
                    JFrame frame = new JFrame("Результат выполнения фильтра");
                    frame.getContentPane().add(objectBrowser);
                    frame.pack();
                    frame.setIconImage(Or3Frame.instance().getIconImage());
                    frame.setLocationRelativeTo(null);
                    frame.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(frame.getWidth(), frame.getHeight()));
                    frame.addWindowListener(new WindowAdapter() { 
                        public void windowClosing(WindowEvent e) {
				            filterResultsPanel.add(objectBrowser);
				            filterResultsPanel.repaint();
				            filterResultsPanel.revalidate();
                            ((ObjectBrowser) objectBrowser).floatModeBtn.setVisible(true);
                        }
                    });
                    ((ObjectBrowser) objectBrowser).floatModeBtn.setVisible(false);
                    frame.show();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    private void replaceFilteredAttr() {
    	DesignerDialog dlg = new DesignerDialog((JFrame) this.getTopLevelAncestor(), "Заменить фильтруемый атрибут", new ReplaceFilteredAttrPanel());
    	dlg.setOnlyOkButton();
        dlg.setResizable(false);
        dlg.setOkText("Закрыть");
        dlg.show();
    }
    
    private void replace() {
    	 ReplacePanel replacePanel = new ReplacePanel(this, tabbedContent.getSelectedFilter());
         replacePanel.addPropertyListener(tabbedContent);
         DesignerDialog dlg = new DesignerDialog((JFrame) this.getTopLevelAncestor(), "Найти и заменить", replacePanel);
         dlg.setOnlyOkButton();
         dlg.setResizable(false);
         dlg.setOkText("Закрыть");
         dlg.show();
         OrFilterNode fn = replacePanel.getFilterNode();
         if (fn != null) {
             TreePath path = new TreePath(fn.getPath());
             tabbedContent.getSelectedFilter().setSelectionPath(path);
         }
    }
    
    class ReplaceFilteredAttrPanel extends JPanel implements ActionListener {
    	
        private JLabel currentFilteredAttr = Utils.createLabel("Текущий фильтруемый аттрибут:", JLabel.RIGHT);
        private JLabel newFilteredAttr = Utils.createLabel("Новый фильтруемый аттрибут:", JLabel.RIGHT);
        
        private JTextField currentTF = Utils.createDesignerTextField();
        private JTextField newTF = Utils.createDesignerTextField();
        private JButton refBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        private JButton replaceBtn = ButtonsFactory.createToolButton("ok.png", "Заменить");


    	public ReplaceFilteredAttrPanel() {
    		setLayout(new GridBagLayout());
    		Utils.setAllSize(this, new Dimension(550, 75));
    		Utils.setAllSize(currentTF, new Dimension(325, 18));
    		Utils.setAllSize(newTF, new Dimension(325, 18));

    		currentTF.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					isEnable();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					isEnable();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					isEnable();
				}
			});
    		
    		newTF.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					isEnable();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					isEnable();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					isEnable();
				}
			});
    		
    		refBtn.setToolTipText("Выбрать путь");
    		replaceBtn.setEnabled(false);
    		replaceBtn.addActionListener(this);
    		
    		add(currentFilteredAttr, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0));
    		add(currentTF, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 5), 0, 0));

    		add(newFilteredAttr, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0));
    		add(newTF, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 5), 0, 0));
    		add(refBtn, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 5), 0, 0));
    		add(replaceBtn, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 5), 0, 0));
    	}
    	
		private void isEnable() {
			if (currentTF.getText().trim().length() > 0 && newTF.getText().trim().length() > 0) {
				replaceBtn.setEnabled(true);
			} else {
				replaceBtn.setEnabled(false);
			}
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == refBtn) {
				ClassBrowser cb = new ClassBrowser(null, true);
				DesignerDialog dlg;
				if (getTopLevelAncestor() instanceof JFrame) {
					dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите путь", cb);
				} else {
					dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите путь", cb);
				}
				dlg.setSize(new Dimension(900, 600));
				dlg.setLocation(getCenterLocationPoint(900, 600));
				cb.setSize(new Dimension(900, 600));
				cb.setSplitLocation();
				dlg.show();
				int res = dlg.getResult();
				if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                    String path = cb.getSelectedPath();
                    if (path.length() > 0) {
                    	newTF.setText(path);
                    }
				}
			} else if (e.getSource() == replaceBtn) {
				OrFilterTree filterTree = tabbedContent.getSelectedFilter();
				OrFilterNode filterNode = filterTree.getSelectedFilterNode();
				
				replaceValue(filterNode);
				
				inspector.setObject(new FilterItem(filterNode, FiltersPanel.this));
                inspector.repaint();
			}
		}
		
		private void replaceValue(OrFilterNode filterNode) {
			PropertyValue pv = filterNode.getPropertyValue(OrFilterNode.PROPS.getChild("attrFlr"));
			String attrFlr = pv.stringValue();
			if (currentTF.getText().equals(attrFlr)) {
				filterNode.setPropertyValue(new PropertyValue(newTF.getText(), OrFilterNode.PROPS.getChild("attrFlr")));
				setFilterModified(filterNode);
			}
			for(int i = 0; i < filterNode.getChildCount(); i++) {
				replaceValue((OrFilterNode) filterNode.getChildAt(i));
			}
	    }
    }

    private void importFilterFromXmlFile() { 
        Kernel krn = Kernel.instance();
        FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel) tree.getModel();
        SAXBuilder builder = new SAXBuilder();
        String newline ="\n";
        File file[] = null;
        Element xml = null;
        String titles="";
        JFileChooser fc = new JFileChooser();;
        fc.setMultiSelectionEnabled(true);
        int returnVal = fc.showOpenDialog(FiltersPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFiles();
            for(int i = 0;i<file.length;i++) {
                try {
					Document document = (Document) builder.build(file[i]);
					xml = document.getRootElement();
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(xml!=null) {
                    String title =xml.getChildText("title");
                    try {
                        FilterNode filter_node = (FilterNode) model.createChildNode(title);
                        KrnObject obj = filter_node.getKrnObj();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        XMLOutputter out = new XMLOutputter();
                        out.getFormat().setEncoding("UTF-8");
                        try {
                            out.output(xml, os);
                            os.close();
                            titles+="-"+title+"\n";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                       
                        krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
                        // Сохранение sql для фильтра и некоторых параметров
                        krn.saveFilter(obj.id);
                       
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }}
               String msg = "";
                if(file.length>1)
                {  msg = "Фильтры:"+"\n"+titles+"успешно импортированы"; 
          }
                else if(file.length==1)
                {  msg = "Фильтр"+"\n"+titles+"успешно импортированы";
                }
                else {
                    msg = "Ничего не было импортировано";
                }
                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,msg);
                }
}
    

    private void exportFilterToXmlFile(DesignerTreeNode filterNodes[]) {
    
      
    //    xmlOutputer.setFormat(Format.getPrettyFormat());
     String titles = "";
     String path = "C:\\Users\\Администратор\\Desktop\\filterXmls\\";
        for(int i=0;i<filterNodes.length;i++)
        {
           FilterNode filterNode =(FilterNode) filterNodes[i];
           String title = filterNode.getTitle();
           KrnObject filterobj = filterNode.getKrnObj();
           Element xml = getXml(filterobj);
           xml.detach();
           Document doc = new Document(xml);
           String filename = title.replaceAll("[^a-zA-Zа-яА-Я0-9,.\\s-]", "_");
          String xmlFilePath =path+filename+".xml";
           
           FileWriter file = null;
           try {
               file = new FileWriter(xmlFilePath,true);
           } catch (IOException e1) {
               e1.printStackTrace();
           }
           xmlFilePath =xmlFilePath.replace("\\", "/");
           XMLOutputter xmlOutputer = new XMLOutputter();
            try {
                if(file!=null)
                    xmlOutputer.output(doc, file);
                titles=titles.concat("-"+title+"\n");
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        String msg = "";
        if(filterNodes.length>1) {
        msg = "Фильтры:"+"\n"+titles+"успешно экспортированы ";
        }
        else if(filterNodes.length==1) {
            msg =  "Фильтр"+"\n"+titles+"успешно экспортирован ";
        }
        else {
            msg = "Ничего не было экспортировано";
        }
        MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                msg + "в "+"\n"+path);
       
        }
    List<Element> nodesWithParams = new ArrayList<Element>();
    List<String> paramNames = new ArrayList<String>();
    
    //read config file, collect components and parameter names to lists
    private void listNodesWithParams(Element component) {  
        Element children = component.getChild("children");
        if(children==null||children.getValue().equals(""))
        {
            Element compFlr = component.getChild("compFlr");
            if(compFlr!=null&& compFlr.getValue().equals("1"))
            {
                String exprFlr = component.getChild("valFlr").getChild("exprFlr").getValue();
                if(exprFlr.startsWith("%"))
                {
                    nodesWithParams.add(component);
                    paramNames.add(exprFlr);
                }
            }
        }
        else { List<Element> childComponents = children.getChildren("Component");
               for(Element childComponent : childComponents)
               {listNodesWithParams(childComponent);}
        }
    }

    HashMap<String,Object> mapValues = new HashMap<String,Object>();
    
    private void populateInputPanel() {
        final GridBagLayout layout = new GridBagLayout();
        inputPanel.setLayout(layout);
        c.fill = GridBagConstraints.HORIZONTAL;
        listNodesWithParams(tabbedContent.getSelectedFilter().getRoot().getXml());    

        int y = 0;
        mapValues.clear();
        for(Element element: nodesWithParams)
        {
            createInputRow(element, y);
            y++;
        }
        JButton prodolzhit = new JButton("Выполнить");
        c.gridx = 2;
        c.gridy = y;
        inputPanel.add(prodolzhit,c);
        prodolzhit.addActionListener(new ActionListener() {//sobrat vse znacheniya vvedennye userom v list i peredat filtru
            public void actionPerformed(ActionEvent e)
            { 
                changefilterResultsPanel(lastSelectFlt);
                return;
/*                List<Object> paramsToSubmit = new ArrayList<Object>();
                filterResultsPanel.removeAll();
                filterResultsPanel.updateUI();
                int nonStringCount = 0;
                int iComp = 0;
                int fieldCount = 0;

                while(iComp<inputPanel.getComponentCount())
                {
                    Component component = inputPanel.getComponent(iComp);    
                    if (component instanceof JButton) {
                        GridBagConstraints gbc =layout.getConstraints(component);
                        if(gbc.gridx == 1) {
                        Object obj = null;
                        JTextField field = null;
                        for (Component comp : inputPanel.getComponents()) {
                            
                            if(comp instanceof JTextField) {
                                GridBagConstraints gbcField =layout.getConstraints(comp);
                            if (gbc.gridy == gbcField.gridy) {
                               field = (JTextField)comp;
                               break;
                            }}
                        }                        
                        if(nonStringCount<mapValues.size()) {
                            String fieldText =( (JTextField)field).getText();
                            if(!fieldText.equals("")) {
                                obj =  mapValues.get(nonStringCount);
                                nonStringCount++;
                                paramsToSubmit.add(obj);}
                            else {
                                paramsToSubmit.add(obj);
                            }
                        }else
                        	paramsToSubmit.add(obj);
                    }
                        iComp++;
                    }
                    else if(component instanceof DateField)
                    {
                        fieldCount++;
                        Object dateValue =  ((DateField)component).getValue();//KrnDate
                        Date dateObject =(Date)dateValue;//KrnDate
                        paramsToSubmit.add(dateObject);
                        }
                    else if(component instanceof JTextField)
                    {
                        boolean objType = false;
                        for (Component comp : inputPanel.getComponents()) {
                            if(comp instanceof JButton) {
                            GridBagConstraints gbc =layout.getConstraints(comp);
                            GridBagConstraints gbcField =layout.getConstraints(component);
                            if(gbcField.gridx==gbc.gridx+1&&gbcField.gridy==gbc.gridy)
                            {objType = true;
                                break;
                            }}
                        }        
                        if(objType==false) {
                            fieldCount++;
                            String fieldText = ((JTextField)component).getText();
                            if(fieldText.equals(""))
                            {fieldText=null;
                            paramsToSubmit.add(fieldText);
                            }
                            else {
                                List<String> items = Arrays.asList(fieldText.split("\\s*,\\s*"));
                                String clsName = clsNames.get(fieldCount-1);
                                if(clsName.equals("date")||clsName.equals("time")){
                                    List<Date> dateList = new ArrayList<Date>();
                                    SimpleDateFormat dateFormat = null;
                                    if(clsName.equals("date")) {
                                        dateFormat  = new SimpleDateFormat("dd.MM.yyyy");}
                                    else if(clsName.equals("time")) {
                                        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
                                    }
                                    Date date = null;
                                    for(String item: items)
                                    {
                                        try {
                                            date = dateFormat.parse(item); 
                                            dateList.add(date);
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                    }
                                    paramsToSubmit.add(dateList);
                        }
                                    else {paramsToSubmit.add(items);}
                                }
                    }}
                    iComp++;
                }
//                changefilterResultsPanel(lastSelectFlt,paramsToSubmit);
 * 
 */
            }
        }); 
    }
    String operFlr;
    List<String>clsNames = new ArrayList<String>();
    
    //sozdat neobhodimye GUI componenty dlya vvoda userom: JTextField, JLabel, JButton
	private void createInputRow(Element component, int y) {
		operFlr = component.getChild("operFlr").getText();
		boolean multipleSelection = operFlr.equals("9") || operFlr.equals("10");
		JButton krnBtn = null;
		String paramName = component.getChild("valFlr").getChild("exprFlr").getValue();

		JLabel label = new JLabel(paramName);
		c.gridx = 0;
		c.gridy = y;
		inputPanel.add(label, c);

		JButton button = kz.tamur.comps.Utils.createBtnEditorIfc(this);
		JTextField field = null;
		final String path = component.getChild("attrFlr").getValue();
		final KrnClass cls = getClassFromAttrPath(path);
		if (cls != null) {
			String clsName = cls.getName();
			clsNames.add(clsName);
			c.gridx = 2;
			c.gridy = y;

			if (clsName.equals("date")) {
				if (!multipleSelection) {
					field = new DateField();
					((DateField) field).setValue(null);
				} else {
					field = new JTextField(15);
				}

				inputPanel.add(field, c);
			} else if (clsName.equals("time")) {
				if (!multipleSelection) {
					field = new DateField();
					((DateField) field).setDateFormat(Constants.DD_MM_YYYY_HH_MM_SS_SSS);
					((DateField) field).setValue(null);
				} else {
					field = new JTextField(15);
				}

				inputPanel.add(field, c);
			} else {
				field = new JTextField(15);
				inputPanel.add(field, c);
			}
			final InputField inputField = new InputField(field);
			inputField.paramName = paramName;
			if (cls.id <= 99 && multipleSelection) {
				krnBtn = kz.tamur.comps.Utils.createBtnEditor(this);
				krnBtn.setName(paramName);
				c.gridx = 3;
				c.gridy = y;
				inputPanel.add(krnBtn, c);

				krnBtn.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						List<String> items = Arrays.asList(inputField.getText().split("\\s*,\\s*"));
						Object vals[] = items.toArray();
						int dateFormat = 0;
						if (cls.getName().equals("time")) {
							dateFormat = 3;
						}
						ArrayPropertyFieldForFilter apf = new ArrayPropertyFieldForFilter(cls, vals, dateFormat);
						Container cont = getTopLevelAncestor();
						DesignerDialog dlg;

						if (cont instanceof Dialog) {
							dlg = new DesignerDialog((Dialog) cont, path, apf);
						} else {
							dlg = new DesignerDialog((Frame) cont, path, apf);
						}
						dlg.show();
						if (dlg.isOK()) {
							Vector data = apf.getList();
							String fieldContent = "";
							String fieldString = "";
							Object[] value_ = new Object[data.size()];
							for (int i = 0; i < data.size(); i++) {
								Object rowValue = ((Vector) data.get(i)).get(0);
								if (rowValue instanceof DateField) {
									value_[i] = ((DateField) rowValue).getValue();

									fieldString = ((KrnDate) value_[i]).toString(
											((SimpleDateFormat) kz.tamur.util.Funcs.getDateFormat(dateFormat).get())
													.toPattern());
								} else if (rowValue instanceof JTextField) {
									fieldString = ((JTextField) rowValue).getText();
								}
								fieldContent = fieldContent.concat(fieldString);
								if (!(i == data.size() - 1)) // posle poslednego
																// ob'ekta
																// zapyataya ne
																// nuzhna
								{
									fieldContent = fieldContent.concat(",");
								}
							}
							inputField.setText(fieldContent);
							mapValues.put(inputField.paramName, fieldContent);
						}

					}
				});
			} else if (cls.id > 99) {
				c.gridx = 1;
				c.gridy = y;
				inputPanel.add(button, c);
				button.setName(paramName);
				button.addActionListener(new ActionListener() {
					Kernel krn = Kernel.instance();
					String attr_name = null;

					public void actionPerformed(ActionEvent e) {
						Collection list_ = krn.getAttributes(cls);
						Vector attr_l = new Vector();
						for (Iterator it = list_.iterator(); it.hasNext();) {
							KrnAttribute attr_ = (KrnAttribute) it.next();
							if (attr_.typeClassId == Kernel.IC_STRING || attr_.typeClassId == Kernel.IC_MEMO 
									|| attr_.typeClassId == Kernel.IC_MSTRING || attr_.typeClassId == Kernel.IC_MMEMO) {
								attr_l.add(attr_.name);
							}
						}
						if (attr_l.size() > 0) {
							JList attr_list = new JList(attr_l);
							final JScrollPane scroller = new JScrollPane(attr_list);
							scroller.setPreferredSize(new Dimension(400, 200));
							DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
									"Выберите атрибут для отображения объектов", scroller, true);
							dlg.show();
							int res = dlg.getResult();
							if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
								attr_name = (String) attr_list.getSelectedValue();
								String selectedObjects = objectsDialog(cls, attr_name, operFlr,
										((JButton) e.getSource()).getName());
								inputField.setText(selectedObjects);
							} else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
								return;
							} else {
								return;
							}
						}
					}
				});
			} else {
				mapValues.put(paramName, field);
			}
		} else {
			MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
					"Ошибка в пути: \n" + path);
		}
	}
	
    private String objectsDialog(KrnClass cls,String attr_name,String operFlr,String paramName){
        Object value = null;
        ObjectList oList = null;
        try {

            oList = new ObjectList(cls, attr_name);
            oList.setSingleSelectionMode(false);
            if(operFlr.equals("9")||operFlr.equals("10"))
            {
                oList.setSingleSelectionMode(false);
            }
            else { oList.setSingleSelectionMode(true);}
        } catch (KrnException exception) {
            exception.printStackTrace();
        }
        if(value instanceof Vector && ((Vector)value).size()>0){
            int[] indexs=new int[((Vector)value).size()];
            int i=0;
            for(KrnObjectItem obj:((Vector<KrnObjectItem>)value)){
                indexs[i++]=oList.getIndexById((int)obj.obj.id);
            }
            oList.setSelectedIndices(indexs);
        }
        JScrollPane sp = new JScrollPane(oList);
        sp.setPreferredSize(new Dimension(600, 600));
        String label_="";
        DesignerDialog dlg = new DesignerDialog(
                Or3Frame.instance(),
                "Выберите объект", sp,true);
        dlg.show();
        int res = dlg.getResult();
        if (res != ButtonsFactory.BUTTON_NOACTION
                && res == ButtonsFactory.BUTTON_OK) {
            KrnObject[] objs = oList.getSelectedObjects();

            if (objs != null && objs.length>0) {
                if(objs.length==1)
                {
                    mapValues.put(paramName,objs[0]);
                }
                else {
                    List<KrnObject> objsToList = Arrays.asList(objs);
                    mapValues.put(paramName,objsToList);
                }

                String[] titles = oList.getSelectedTitles();
                Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                for(int i=0;i<objs.length;i++){

                    value_.add(new KrnObjectItem(objs[i],titles[i]));
                }

                for(int i=0;i<((Vector)value_).size();i++){
                    label_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value_).get(i)).title;
                }
            }else{
                mapValues.remove(paramName);
            }
        }
        return label_;
    }
    private KrnClass getClassFromAttrPath(String path){ //given path, find krn class
        KrnClass cls = null;
        KrnAttribute attr=null;
        Kernel krn = Kernel.instance();

        try {
            KrnAttribute[] attr_a=Utils.getAttributesForPath(path);
            if(attr_a!=null && attr_a.length>0)
                cls=krn.getClass((attr=attr_a[attr_a.length-1]).typeClassId);
            else if(path.indexOf(".")<0)
                cls=krn.getClassByName(path);

        } catch (KrnException e1) {

            return null;
        }
        return cls;
    }
    
    private void changefilterResultsPanel(KrnObject filterObj) {
        Kernel krn  = Kernel.instance();
        try {
        	krn.clearFilterParams(filterObj.uid);
        	for(String paramName: paramNames){
                Object paramValue = mapValues.get(paramName);
            	if(paramValue instanceof JTextField){
            		if(paramValue instanceof DateField){
                        Object dateValue =  ((DateField)paramValue).getValue();//KrnDate
                        paramValue =(Date)dateValue;//KrnDate
            		}else
            			paramValue=((JTextField)paramValue).getText();
            	}
                if(paramValue instanceof List){ 
                    krn.setFilterParam(filterObj.uid, paramName, (List)paramValue);
                }else{
            		if(paramValue!=null&&paramValue.equals(""))
                    	{paramValue=null;}
                    krn.setFilterParam(filterObj.uid, paramName,Collections.singletonList(paramValue));
            	}
            }
		} catch (KrnException e) {}
        KrnObject[] krnObjects = null;
        try {
        	if(filterObj.id>0) {
        		krnObjects =  krn.getFilteredObjects(filterObj, 0, -1);
        	}else {
        		String sql=krn.compileFilter(filterObj.id, localNode.getXml());
        		List<KrnObject> krnObjects_ =  krn.filterLocal(sql,0,-1,-1, 0);
                krnObjects= krnObjects_.toArray(new KrnObject[krnObjects_.size()]);
        	}
            if(objectBrowser!=null)
            	filterResultsPanel.remove(objectBrowser);
            objectBrowser = new ObjectBrowser(Arrays.asList(krnObjects), false);
            ((ObjectBrowser) objectBrowser).floatModeBtn.addActionListener(this);;
            filterResultsPanel.add(objectBrowser);
            filterResultsPanel.repaint();
            filterResultsPanel.revalidate();
        } catch (KrnException e) {
            e.printStackTrace();
           filterResultsPanel.remove(objectBrowser);
           errorMsg.setText("Произошла ошибка. Возможно филтьр был построен не правильно");
           filterResultsPanel.add(errorMsg);
           filterResultsPanel.revalidate();
           filterResultsPanel.repaint();
        }
    }
    
    private void create(KrnObject nodeObj) {
        tree.setShowPopupEnabled(false);
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.CREATE_NEW_ELEMENT_TYPE, "", tree);
        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Создание фильтра", cp);
        dlg.show();
        if (dlg.isOK()) {
            FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel) tree.getModel();
            FilterNode node = null;
            try {
                String title = cp.getElementName();
                node = (FilterNode) model.createChildNode(title);//dobavit filternode v roditelskuiu papku
                OrGuiComponent c = Factories.instance().create("FilterNode", node);//create orfilternode
                Kernel krn = Kernel.instance();
                KrnObject lang = krn.getInterfaceLanguage();
                long langId = (lang != null) ? lang.id : 0;
                c.setLangId(langId);
                c.setPropertyValue(new PropertyValue(title, langId, c.getProperties().getChild("title")));
                String path = getClassPath();
                c.setPropertyValue(new PropertyValue(path, langId, c.getProperties().getChild("attrFlr")));
                save(node.getKrnObj(), (OrFilterNode) c);
                OrFilterTree ftree = new OrFilterTree((OrFilterNode) c, node.getKrnObj(), tabbedContent);
                ftree.addTreeSelectionListener(this);
                tabbedContent.addFilterTab(title, ftree, node, false,nodeObj);
                HistoryWithDate hwd = new HistoryWithDate(node.getKrnObj(), new Date());
                krn.getUser().addFltInHistory(hwd,node.toString());
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    public void openLocal() {
        SAXBuilder builder = new SAXBuilder();
        File file = null;
        Element xml = null;
        JFileChooser fc = new JFileChooser();;
        fc.setMultiSelectionEnabled(true);
        int returnVal = fc.showOpenDialog(FiltersPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            try {
				Document document = (Document) builder.build(file);
				xml = document.getRootElement();
            } catch (JDOMException e) {
                e.printStackTrace();
                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                        "Произошла ошибка при открытии существующего фильтра!");
            } catch (IOException e) {
                e.printStackTrace();
                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                        "Произошла ошибка при открытии существующего фильтра!");
            }
            if(xml!=null) {
                try {
                	if(localFobj!=null && tabbedContent.getIndexFor(localFobj)>=0) {
                		tabbedContent.remove(tabbedContent.getIndexFor(localFobj));
                	}
                    String title = "Локальный фильтр";
                    localFobj=new KrnObject(-1,"FilterLocal",Kernel.SC_FILTER.id);
                    FilterNode node = new FilterNode(localFobj,title,0,0);
                   localNode = new OrFilterNode(xml, Mode.DESIGN, Factories.instance(), node);
                   OrFilterTree ftree = new OrFilterTree(localNode, localFobj, tabbedContent);
                   ftree.addTreeSelectionListener(this);
                   tabbedContent.addFilterTab(title, ftree, node, false,null);
                } catch (KrnException e) {
                    e.printStackTrace();
                    MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                            "Произошла ошибка при открытии существующего фильтра!");
                }
            }
        }
    }

    private void createLocal() {
    	if(localFobj!=null && tabbedContent.getIndexFor(localFobj)>=0) {
    		tabbedContent.setSelectedIndex(tabbedContent.getIndexFor(localFobj));
    		return;
    	}
        FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel) tree.getModel();
           try {
                String title = "Локальный фильтр";
                localFobj=new KrnObject(-1,"FilterLocal",Kernel.SC_FILTER.id);
                FilterNode node = new FilterNode(localFobj,title,0,0);
                localNode = (OrFilterNode)Factories.instance().create("FilterNode", node);//create orfilternode
                Kernel krn = Kernel.instance();
                KrnObject lang = krn.getInterfaceLanguage();
                long langId = (lang != null) ? lang.id : 0;
                localNode.setLangId(langId);
                localNode.setPropertyValue(new PropertyValue(title, langId, localNode.getProperties().getChild("title")));
                String path = getClassPath();
                localNode.setPropertyValue(new PropertyValue(path, langId, localNode.getProperties().getChild("attrFlr")));
                OrFilterTree ftree = new OrFilterTree(localNode, localFobj, tabbedContent);
                ftree.addTreeSelectionListener(this);
                tabbedContent.addFilterTab(title, ftree, node, false,null);
            } catch (KrnException e) {
                e.printStackTrace();
                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                        "Произошла ошибка при создании фильтра!");
            }
    }

    private void saveLocal() {
    	JFileChooser fc = new JFileChooser();
    	int retFlag = fc.showSaveDialog(FiltersPanel.this);
    	if(retFlag == JFileChooser.APPROVE_OPTION){
    		File file = fc.getSelectedFile();
    		try {
    	        FileOutputStream os = new FileOutputStream(file);
    	        XMLOutputter out = new XMLOutputter();
    	        out.getFormat().setEncoding("UTF-8");
    	        out.output(tabbedContent.getSelectedFilter().getRoot().getXml(), os);
    	        os.close();
			} catch (IOException e) {
				e.printStackTrace();
                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                        "Произошла ошибка при сохранении конфигурации фильтра!");
			}
    	}
    }

    public void saveCurrent(OrFilterTree tree) {
    	if(tree.getObj().equals(localFobj))
    		saveLocal();
    	else
    		save(tree.getObj(), tree.getRoot());
    }

    private void save(KrnObject obj, OrFilterNode root) {
    	if(root.equals(localNode)) return;
        final Kernel krn = Kernel.instance();
        try {
            PropertyValue pvl = root.getPropertyValue(OrFilterNode.PROPS.getChild("attrFlr"));
            if (pvl == null || pvl.stringValue().equals("")) {
                KrnClass flrCls = krn.getClassByName("Filter");
                if (obj.classId == flrCls.id) {
//                    int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
//                            "Не определен фильтруемый класс! Сохранить?");
//                    if (res == ButtonsFactory.BUTTON_NO) {
//                        return;
//                    }
                    
                }
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(root.getXml(), os);
            os.close();
            krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
            // Сохранение sql для фильтра и некоторых параметров
            krn.saveFilter(obj.id);
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        open(openBtn);
    }
    
    public void open(Object src) {
        tree.setShowPopupEnabled(true);
        OpenElementPanel op = new OpenElementPanel(tree);
        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Открытие фильтра", op);
        op.setSearchUIDPanel(true);
        tree.requestFocusInWindow();
		if (src == exportBtn) {
        	op.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }
        dlg.show();
		if (dlg.isOK()) {
			if (src == openBtn) {
				load((FilterNode) op.getTree().getSelectedNode(), null);
			} else if (src == exportBtn) {
				exportFilterToXmlFile(op.getTree().getSelectedNodes());
			}
		}
    }
    
    FilterNode filterNode;
    public void load(KrnObject filter, KrnObject nodeObj) {
        filterNode = (FilterNode) tree.find(filter);
        load(filterNode,nodeObj);
    }
    
    public void refreashNode(KrnObject filter) {
        tree.refreashNode(filter);
    }
    Element xml;
    public void load(FilterNode fnode, KrnObject nodeObj) {
        if (fnode == null)
            return;
        
        final Kernel krn = Kernel.instance();

        filterNode = fnode;
        KrnObject obj = fnode.getKrnObj();
        int index = tabbedContent.getIndexFor(obj);
        if (index > -1) {
            tabbedContent.setSelectedIndex(index);
            // Сохранение в историю
            HistoryWithDate hwd = new HistoryWithDate(obj, new Date());
            krn.getUser().addFltInHistory(hwd,fnode.toString());
            return;
        }
//        try {
//	        if (krn.getBindingModuleToUserMode()) {
//				KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
//	        	if (developerObjs.length > 0) {
//	        		long ownerId = developerObjs[0].id;
//	        		long currentUserId = krn.getUserSession().userObj.id;
//	        		if (ownerId != currentUserId) {
//	        			KrnObject userObj = krn.getObjectById(ownerId, 0);
//	        			if (userObj != null) {	// Владелец фильтра существует
//	            			KrnClass userCls = krn.getClassByName("User");
//	            			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
//	            			String userName = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
////	                        MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного фильтра является пользователь " + userName + "!");
//	        			}
//	        		}
//	        	}
//	        }
//        } catch(KrnException e) {
//        	e.printStackTrace();
//        }
        OrFilterNode node;
        try {
            boolean readOnly = false;
            try {
            	UserSessionValue us = krn.vcsLockObject(obj.id);
                if (us != null) {
                    int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Фильтр '" + fnode.toString() + "' редактируется!\nПользователь: " + us.name + "\nОткрыть фильтр в режиме просмотра?", 235, 130);
                    if (res == BUTTON_YES) {
                        readOnly = true;
                    } else {
                        return;
                    }
                }
                if(!readOnly){
	                us = krn.blockObject(obj.id);
	                if (us != null) {
	                    int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Фильтр '" + fnode.toString() + "' заблокирован!\nПользователь: " + us.name + "\nIP адрес: " + us.ip + "\nИмя компьютера: " + us.pcName + "\nОткрыть фильтр в режиме просмотра?", 235, 130);
	                    if (res != ButtonsFactory.BUTTON_YES) {
	                        return;
	                    }
	                    readOnly = true;
	                }
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
            byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
            if (data.length > 0) {
                /*  ByteArrayInputStream is = new ByteArrayInputStream(data);
                SAXBuilder b = new SAXBuilder();

                xml = b.build(is).getRootElement();
                is.close();*/
                xml = getXml(obj);   
                node = (OrFilterNode) Factories.instance().create(xml, Mode.PREVIEW, fnode);
            } else {
                node = (OrFilterNode) Factories.instance().create("FilterNode", fnode);
                node.getXml().getChild("title").setText(fnode.toString());
            }
            KrnObject lang = krn.getInterfaceLanguage();
            long langId = (lang != null) ? lang.id : 0;
            node.setLangId(langId);
            node.setPropertyValue(new PropertyValue(node.getXml().getChild("title"), node.getProperties().getChild("title"), langId));            
            OrFilterTree ftree = new OrFilterTree(node, obj, tabbedContent);
            PropertyValue attrFlrValue = new PropertyValue(node.getXml().getChild("attrFlr"), node.getProperties().getChild("attrFlr"), langId);
            if(attrFlrValue.getValue() == null) {
            	String path = getClassPath();
            	node.setPropertyValue(new PropertyValue(path, langId, node.getProperties().getChild("attrFlr")));
            	saveCurrent(ftree);
            }
            tabbedContent.addFilterTab(fnode.toString(), ftree, fnode, readOnly,nodeObj);
            ftree.addTreeSelectionListener(this);
            validate();
            repaint();
            // Сохранение в историю
            HistoryWithDate hwd = new HistoryWithDate(obj, new Date());
            krn.getUser().addFltInHistory(hwd,fnode.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Element getXml(KrnObject obj) {
        Kernel krn = Kernel.instance();
        byte[] data = null;
        try {
            data = krn.getBlob(obj, "config", 0, 0, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (data.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            SAXBuilder b = new SAXBuilder();
            try {
                xml = b.build(is).getRootElement();
                is.close();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return xml;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof OrFilterNode) {
                OrFilterNode flr_node = (OrFilterNode) o;
                flr_node.setEnable();
                if (((OrFilterTree) e.getSource()).getLastSelectedPathComponent() == null) {
                    newInNodeBtn.setEnabled(false);
                    delNodeBtn.setEnabled(false);
                    copyNodeBtn.setEnabled(false);
                    replaceFilteredAttrBut.setEnabled(false);
                    cutNodeBtn.setEnabled(false);
                    newBeforNodeBtn.setEnabled(false);
                    insBeforNodeBtn.setEnabled(false);
                    insInNodeBtn.setEnabled(false);
                } else {
                    if ((flr_node).isRoot()) {
                        newInNodeBtn.setEnabled(true);
                        delNodeBtn.setEnabled(false);
                        copyNodeBtn.setEnabled(false);
                        replaceFilteredAttrBut.setEnabled(false);
                        cutNodeBtn.setEnabled(false);
                        newBeforNodeBtn.setEnabled(false);
                        insBeforNodeBtn.setEnabled(false);
                        insInNodeBtn.setEnabled(false);
                    } else {
                        newInNodeBtn.setEnabled(true);
                        delNodeBtn.setEnabled(true);
                        copyNodeBtn.setEnabled(true);
                        replaceFilteredAttrBut.setEnabled(true);
                        cutNodeBtn.setEnabled(true);
                        newBeforNodeBtn.setEnabled(true);
                        insBeforNodeBtn.setEnabled(true);
                        insInNodeBtn.setEnabled(true);
                    }
                }
                if (((OrFilterTree) e.getSource()).copyNode != null) {
                    insBeforNodeBtn.setEnabled(!(flr_node).isRoot());
                    insInNodeBtn.setEnabled(true);
                } else {
                    insBeforNodeBtn.setEnabled(false);
                    insInNodeBtn.setEnabled(false);
                }
                inspector.setObject(new FilterItem(o, this));
            }
        }
    }

    class TabbedChangeAdapter extends ContainerAdapter implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (tabbedContent.getComponentCount() > 0) {
                OrFilterTree ftree = tabbedContent.getSelectedFilter();
                Object fnode = null;
                if (ftree != null) {
                    if (ftree.getSelectionPath() != null) {
                        fnode = ftree.getSelectionPath().getLastPathComponent();
                    }
                    if (tabbedContent.getCopyNode() != null) {
                        ftree.setCopyNode(tabbedContent.getCopyNode());
                    }
                    if (fnode == null) {
                        fnode = tabbedContent.getSelectedFilter().getRoot();
                        KrnObject krnObj = ((OrFilterNode)fnode).getFilterNode().getKrnObj();
                        xml = getXml(krnObj);
                        newInNodeBtn.setEnabled(false);
                        newBeforNodeBtn.setEnabled(false);
                        delNodeBtn.setEnabled(false);
                        copyNodeBtn.setEnabled(false);
                        replaceFilteredAttrBut.setEnabled(false);
                        cutNodeBtn.setEnabled(false);
                        insBeforNodeBtn.setEnabled(false);
                        insInNodeBtn.setEnabled(false);
                    } else {
                        fnode = tabbedContent.getSelectedFilter().getRoot();
                        KrnObject krnObj = ((OrFilterNode)fnode).getFilterNode().getKrnObj();
                        xml = getXml(krnObj);
                        if (((OrFilterNode) fnode).isRoot()) {

                            newInNodeBtn.setEnabled(true);
                            delNodeBtn.setEnabled(false);
                            copyNodeBtn.setEnabled(false);
                            replaceFilteredAttrBut.setEnabled(false);
                            cutNodeBtn.setEnabled(false);
                            newBeforNodeBtn.setEnabled(false);
                            insBeforNodeBtn.setEnabled(false);
                            insInNodeBtn.setEnabled(false);
                        } else {
                            newInNodeBtn.setEnabled(true);
                            delNodeBtn.setEnabled(true);
                            copyNodeBtn.setEnabled(true);
                            replaceFilteredAttrBut.setEnabled(true);
                            cutNodeBtn.setEnabled(true);
                            newBeforNodeBtn.setEnabled(true);
                            insBeforNodeBtn.setEnabled(true);
                            insInNodeBtn.setEnabled(true);
                        }
                        if (ftree.copyNode != null) {
                            insBeforNodeBtn.setEnabled(!((OrFilterNode) fnode).isRoot());
                            insInNodeBtn.setEnabled(true);
                        } else {
                            insBeforNodeBtn.setEnabled(false);
                            insInNodeBtn.setEnabled(false);
                        }
                    }
                    inspector.setObject(new FilterItem(fnode, FiltersPanel.this));
                    inspector.repaint();
                } else {
                    inspector.setObject(new FilterItem(null, FiltersPanel.this));
                    inspector.repaint();
                }
                filterHistoryBtn.setEnabled(true);
                filterBtn.setEnabled(true);
                replaceBut.setEnabled(true);
            } else {
                filterHistoryBtn.setEnabled(false);
                filterBtn.setEnabled(false);
                replaceBut.setEnabled(false);
                clearRightComponents();
            }
        }

        public void componentAdded(ContainerEvent e) {
            super.componentAdded(e);
        }

        public void componentRemoved(ContainerEvent e) {
            super.componentRemoved(e);
        }
    }

    public int processExit() {
        int res = ButtonsFactory.BUTTON_NOACTION;
        if (saveBtn.isEnabled()) {
            String[] fNames = tabbedContent.getModifiedTitles();
            String mess = "Фильтры: \n";
            for (int i = 0; i < fNames.length; i++) {
                mess = mess + "\"" + fNames[i] + "\"\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            Container cont = getTopLevelAncestor();
            if (cont instanceof Frame) {
                res = MessagesFactory.showMessageDialog((Frame) cont, MessagesFactory.CONFIRM_MESSAGE, mess);
            } else {
                res = MessagesFactory.showMessageDialog((Dialog) cont, MessagesFactory.CONFIRM_MESSAGE, mess);
            }
            if (res == ButtonsFactory.BUTTON_YES) {
                tabbedContent.saveAll();
                saveBtn.setEnabled(false);
            }
        }
        inspector.processExit();
        return res;
    }

    public void setInspector(boolean isInspector) {
        ServiceControl sc = ServiceControl.instance();
        if (sc.getContentTabs().isServiceControlMode()) {
            if(!inspector.isInspectorFloat()){
                sc.getPropertyPanel().add(inspector, sc.PROP_FLT);
            }
        } else {
            basicSplit.setLeftComponent(inspector);
            basicSplit.setDividerLocation(0.3);
            basicSplit.setDividerSize(3);
        }
    }

    public boolean isInspectorFloat() {
        return isInspectorFloat;
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    public void setInterfaceLanguage() {
        LangItem li = (LangItem) langSelector.getSelectedItem();
        if (li != null) {
            langId = li.obj.id;
        }
    }

    public void setEmptyState() {
        inspector.setObject(new FilterItem(null, this));
    }

    public KrnObject getInterfaceLanguage() {
        return ((LangItem) langSelector.getSelectedItem()).obj;
    }

    public long getRusLang() {
        return langSelector.getRusLang().id;
    }

    public long getKazLang() {
        return langSelector.getKazLang().id;
    }

    public void setFilterModified(OrFilterNode node) {
    	if(localNode==null || !localNode.equals(node.getRoot()))
    		tabbedContent.propertyModified(node);
       	((DefaultTreeModel) tabbedContent.getSelectedFilter().getModel()).nodeChanged(node);
    }

    public ControlTabbedContent getTabbedContent() {
        return tabbedContent;
    }
    
    public void reloadTree(){
    	tree = kz.tamur.comps.Utils.getFiltersTreeContent(tabbedContent, true);
    }

    /**
     * @return the tree
     */
    public FiltersTree getTree() {
        return tree;
    }

    /**
     * Удаление из дизайнера открытого фильтра.
     * 
     * @param id
     *            ID удаляемого фильтра
     */
    public void removeCurrentTab(Long id) {
        if (tabbedContent.isFilterOpened(id)) {
            try {
                if (tree != null) {
                    FilterRecord fr = tabbedContent.getFilters().get(id);
                    FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel) tree.getModel();
                    model.deleteNode(fr.getParent(), false);
                }
                tabbedContent.removeSelectedFlt();
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }
    public void showHistory(JComponent swObj) {
        JPopupMenu pm = new JPopupMenu();
        Iterator it = Kernel.instance().getUser().config.getFltHistoryObjs().iterator();
        JMenuItem item;
        while (it.hasNext()) {
            final KrnObject objTmp = (KrnObject) it.next();
            pm.add(item = createMenuItem("["+objTmp.id+"]-"+Kernel.instance().getUser().config.getFltName(objTmp)));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    load(objTmp,null);
                }
            });
        }
        pm.show(swObj, swObj.getX(), swObj.getY());
    }

    /**
     * Получить toolBar.
     * 
     * @return the toolBar
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Получить inspector.
     * 
     * @return the inspector
     */
    public PropertyInspector getInspector() {
        return inspector;
    }

    /**
     * Пересобрать интерфейс
     */
    public void rebuildPanels() {
        tabbedContent.setServiceControlMode(false);
        remove(toolBar);
        add(toolBar, BorderLayout.NORTH);
        toolBar.setVisible(true);
        basicSplit.remove(inspector);
        basicSplit.setLeftComponent(inspector);
        basicSplit.remove(rightComponents);
        basicSplit.setRightComponent(rightComponents);
        rightComponents.remove(tabbedContent);
        rightComponents.setLeftComponent(tabbedContent);
        rightComponents.remove(rightSplitPane);
        rightComponents.setRightComponent(rightSplitPane);
        revalidate();
    }

    /**
     * @return the isLocalFilters
     */
    public boolean isOwnerFilters() {
        return isOwnerFilters;
    }

    public void showUUID(String uid) {
        uidView.setText(uid);
    }
    public void clearRightComponents()
    {
        inputPanel.removeAll();
        inputPanel.revalidate();
        inputPanel.repaint();
        filterResultsPanel.removeAll();
        filterResultsPanel.repaint();
    }

    KrnObject lastSelectFlt;
    public void setFilterObject(KrnObject lastSelectFlt) {
        this.lastSelectFlt = lastSelectFlt;
        showUUID(lastSelectFlt.uid); 
//       	saveBtnLocal.setEnabled(tabbedContent.getSelectedIndex()>=0);

    }
    
    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText(Or3Frame.getCurrentDbName());
        currentUserLable.setText(Or3Frame.getCurrentUserName());
    }
    
    private String getClassPath () {
    	String path = null;
        final Kernel krn = Kernel.instance();
        ClassBrowser cb = null;
        try {
        	ClassNode cls = krn.getClassNodeByName("Объект"); 
        	cb = new ClassBrowser(cls, true);
        	Dimension cbDim = kz.tamur.comps.Utils.getMaxWindowSize();
        	cb.setPreferredSize(new Dimension(cbDim.width*3/5,cbDim.height*3/5)); 
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        DesignerDialog dlg =
				new DesignerDialog(Or3Frame.instance(),
						"Выберите путь фильтруемого объекта", cb);
		dlg.show();
		int res = dlg.getResult();
		if (res != ButtonsFactory.BUTTON_NOACTION
				&& res == ButtonsFactory.BUTTON_OK) {
			path = cb.getSelectedPath();
		}
		return path;
    }
}
