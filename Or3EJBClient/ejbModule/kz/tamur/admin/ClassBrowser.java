package kz.tamur.admin;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.rt.Utils.createMenuItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Kernel.AttrNode;
import com.cifs.or2.client.PackageNode;
import com.cifs.or2.client.RevAttrPanel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.ModelChange;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.util.CursorToolkit;

import kz.tamur.Or3Frame;
import kz.tamur.admin.clsbrow.ObjectBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.expr.ActionListenerRt;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.guidesigner.expr.WndTableEditor;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.ClientUtils;
import kz.tamur.or3.client.util.GuiUtil;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.Funcs;

@SuppressWarnings("serial")
public class ClassBrowser extends JPanel implements ActionListener, PropertyChangeListener {

	MethodSplitPane methodtSplitPane= new MethodSplitPane();
	JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    JSplitPane splitPanel = new JSplitPane();
    JSplitPane classHierarchySplitPanel = new JSplitPane();
    JSplitPane comSplitPanel = new JSplitPane();
    JTextPane comText = new JTextPane();
    JTabbedPane treesTabbedPane = new JTabbedPane();
    JScrollPane packageTreeScrollPane = new JScrollPane();
    JScrollPane classHierarchyScrollPane = new JScrollPane();
    JScrollPane hierarchyTreeScrollPane = new JScrollPane();
    JScrollPane jScrollPane2 = new JScrollPane();
    JScrollPane jScrollPane3 = new JScrollPane();
    JSplitPane In_jPanel2and3 = new JSplitPane();
    JSplitPane In_P23andEditor = new JSplitPane();
    final JCheckBox jCheckBox1 = new javax.swing.JCheckBox();
    ClassTree classTree;
    PackageTree packageTree;
    AttributeTree attrTree;
	JTree classHierarhyTree;
    ClassNode root_;
    private TreePath lastTypePath = null;
    private ClassNode lastTypeNode = null;
    private ClassNode lastBaseClassNode = null;
    boolean filterHasAttr = false;
  
    JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();

//    public JToggleButton inspBtn = ButtonsFactory.createCompButton("Инспектор", kz.tamur.comps.Utils.getImageIcon("inspector"));
    private Action truncateAction = new TruncateAction("Удалить объекты");
    
    JButton clsFindBtn = ButtonsFactory.createToolButton("Find", "Найти класс/метод");
    JButton clsRefreshBtn = ButtonsFactory.createToolButton("Home", "Вернуться");
    JButton clsCreateBtn = ButtonsFactory.createToolButton("createClass", "Создать класс");
    JButton clsDelBtn = ButtonsFactory.createToolButton("deleteClass", "Удалить класс");
    JButton clsPropBtn = ButtonsFactory.createToolButton("propClass", "Свойства класса");
    JButton clsObjBtn = ButtonsFactory.createToolButton("objClass", "Объекты класса");
    JButton clsTruncateBtn = ButtonsFactory.createToolButton(truncateAction);
    JButton quickRepBtn = ButtonsFactory.createToolButton("quickRep", "Поля быстрых отчётов");
    //JButton renameBtn = ButtonsFactory.createToolButton("rename", "запустить переименование таблиц");
    
    JButton attrCreateBtn = ButtonsFactory.createToolButton("createAttr", "Создать атрибут");
    JButton deleteButton = ButtonsFactory.createToolButton("deleteAttr", "Удалить");
    JButton attrPropBtn = ButtonsFactory.createToolButton("propAttr", "Свойства");
    JButton attrUpdateBtn = ButtonsFactory.createToolButton("updateAttr", "Обновить связи");
    JButton attrDelObjsBtn = ButtonsFactory.createToolButton("deleteObjs", "Удалить неиспользуемые объекты");
    JButton methodCreateBtn = ButtonsFactory.createToolButton("createMethod", "Создать метод");
    
    WSGeneratorPanel wsGeneratorPanel = new WSGeneratorPanel();
    
    FavoritesClassesPanel favoritesClasses = new FavoritesClassesPanel(this);
    JButton viewTriggersBtn = ButtonsFactory.createToolButton("Events.png", "Показать триггеры"); // заменить иконку
    JButton exportClassHierarchy = ButtonsFactory.createToolButton("ExportClassHierarchyIcon.png", "Экспорт иерархии классов");
    JButton recycleBtn = ButtonsFactory.createToolButton("Recycle.png", "Корзина");
    JButton viewHistoryBtn = ButtonsFactory.createToolButton("ServiceHistory.gif", "История изменения"); // заменить иконку
    JButton methodBtn = ButtonsFactory.createToolButton("runDebug", "Запуск метода");

    // Popup menus
    JPopupMenu classOperations = new JPopupMenu();
    JMenuItem classCreateItem = createMenuItem("Создать класс");
    JMenuItem classDeleteItem = createMenuItem("Удалить класс");
    JMenuItem classPropItem = createMenuItem("Свойства класса");
    JMenuItem classObjectsItem = createMenuItem("Объекты класса");
    JMenuItem searchObjectsItem = createMenuItem("Поиск объектов");
    JMenuItem classTruncateItem = createMenuItem(truncateAction);
    JMenu classCopyMenu = kz.tamur.rt.Utils.createMenu("Копировать");
    JMenuItem classCopyNameItem = createMenuItem("Название класса");
    JMenuItem classCopyIDItem = createMenuItem("ID класса");
    JMenuItem classCopyUIDItem = createMenuItem("UID класса");
    JMenuItem classReportAttrsItem = createMenuItem("Поля быстрых отчетов");
    JMenuItem classFindItem = createMenuItem("Найти");
    JMenuItem addToFavoriteItem = createMenuItem("Добавить в избранные");

    JPopupMenu attrOperations = new JPopupMenu();
    JMenuItem attrCreateItem = createMenuItem("Создать атрибут");
    JMenuItem revAttrCreateItem = createMenuItem("Создать обратный атрибут");
    JMenuItem deleteItem = createMenuItem("Удалить");
    JMenu attrCopyMenu = kz.tamur.rt.Utils.createMenu("Копировать");
    JMenuItem attrCopyNameItem = createMenuItem("Название атрибута");
    JMenuItem attrCopyIDItem = createMenuItem("ID атрибута");
    JMenuItem attrCopyUIDItem = createMenuItem("UID атрибута");
    JMenuItem attrCopyTypeNameItem = createMenuItem("Название типа атрибута");
    JMenuItem attrCopyClassNameItem = createMenuItem("Название типа (родительский класс)");
    
    JMenuItem attrPropItem = createMenuItem("Свойства", "propAttr");
    JMenuItem aoUpdateRefs = createMenuItem("Обновить связи");
    JMenuItem aoDeleteUnused = createMenuItem("Удалить неисползуемые объекты");
    JMenuItem findRevAttrItem = createMenuItem("Найти обратный атрибут");
    JMenuItem createMethod = createMenuItem("Создать метод", "createMethod");
    JMenuItem methodCopyNameItem = createMenuItem("Копировать название метода", "copyName");
    JMenuItem methodEdit = createMenuItem("Редактировать метод", "edit");
    
    JMenuItem editTablePropItem = createMenuItem("Редактировать таблицу"); //TODO: Tedit
    JMenuItem editTablePropItemattr = createMenuItem("Редактировать таблицу"); //TODO: Tedit
    
    JMenuItem addTriggerItem = createMenuItem("Триггеры СУБД");
    
    JMenu attrEventsMenu = kz.tamur.rt.Utils.createMenu("События");
    JMenuItem beforeChangeEvent = createMenuItem("Перед изменением значения атрибута");
    JMenuItem afterChangeEvent = createMenuItem("После изменения значения атрибута");
    JMenuItem beforeDeleteEvent = createMenuItem("Перед удалением значения атрибута");
    JMenuItem afterDeleteEvent = createMenuItem("После удаления значения атрибута");
    
    JMenu clsEventsMenu = kz.tamur.rt.Utils.createMenu("События");
    JMenuItem beforeCreateObjEvent = createMenuItem("Перед созданием объекта");
    JMenuItem afterCreateObjEvent = createMenuItem("После создания объекта");
    JMenuItem beforeDeleteObjEvent = createMenuItem("Перед удалением объекта");
    JMenuItem afterDeleteObjEvent = createMenuItem("После удаления объекта");

    // Экспортировать классы и объекты
    JMenu clsExportMenu = kz.tamur.rt.Utils.createMenu("Экспорт");
    JMenuItem exportClsItem = createMenuItem("Выгрузить класс в XML");
    // Импортировать классы и объекты
    JMenu clsImportMenu = kz.tamur.rt.Utils.createMenu("Импорт");
    JMenuItem importClsItem = createMenuItem("Создать класс из XML");

    Kernel krn_ = Kernel.instance();
    DesignerStatusBar statusBar = new DesignerStatusBar();
    JLabel statusLabel = Utils.createLabel("");
    private boolean isDialog = false;
    private boolean canCreate = false;
    private boolean canDelete = false;
    private boolean canEdit = false;
    private boolean canCreateAttr = false;
    private boolean canDeleteAttr = false;
    private boolean canEditAttr = false;
    private boolean canCreateMethod = false;
    private boolean canDeleteMethod = false;
    private boolean canEditMethod = false;
    
    private static final Icon methodIcon = kz.tamur.rt.Utils.getImageIcon("method");
    private static final Icon statMethodIcon = kz.tamur.rt.Utils.getImageIcon("methodStat");

    private static final double DIVIDER_1 = 0.3;
    private static final double DIVIDER_2 = 0.8;
    private static double divider1;
    private static double divider2;
    private Properties props = new Properties();
    private boolean selfChanging = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private JProgressBar bottomPB; 
    
    public ClassBrowser() {}

    public void setSplitLocation() {
        int width = getTopLevelAncestor().getWidth();

//        if (isDialog) {
//            selfChanging = true;
//            //TODO TEST 04.09.2013
//            comSplitPanel.setDividerLocation((int) (width * divider2)); //TODO edit
//            splitPanel.setDividerLocation((int) (width * divider1)); //TODO edit
//            selfChanging = false;
//        } else {
//            comSplitPanel.setDividerLocation((int) (width * DIVIDER_2)); //TODO edit
//            splitPanel.setDividerLocation((int) (width * DIVIDER_1)); //TODO edit
//        }
    }

    public void setSplitLocation(double divider1, double divider2) {
        comSplitPanel.setDividerLocation(divider2);
        //splitPanel.setDividerLocation(divider1); //TODO edit
    }

    public void initialize() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassBrowser(ClassNode root, boolean isDialog) {
        if (root != null) {
            root_ = root;
        }
        try {
            this.isDialog = isDialog;

            if (isDialog) {
                String workDir = Utils.getUserWorkingDir();
                if (Funcs.isValid(workDir)) {
        	        File dir = Funcs.getCanonicalFile(workDir);
        	        dir.mkdirs();

	                File f = new File(dir, "propsJboss");
	                if (f.exists()) {
	                    try {
	                        FileInputStream fis = new FileInputStream(f);
	                        props.load(fis);
	                        fis.close();
	                        String par = props.getProperty("divider1");
	                        if (par != null && Double.parseDouble(par) > 0) {
	                            divider1 = Double.parseDouble(par);
	                        } else {
	                            divider1 = DIVIDER_1;
	                        }
	                        par = props.getProperty("divider2");
	                        if (par != null && Double.parseDouble(par) > 0) {
	                            divider2 = Double.parseDouble(par);
	                        } else {
	                            divider2 = DIVIDER_2;
	                        }
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
                }
            }
            jbInit();
        } catch (Exception e) {
            divider1 = DIVIDER_1;
            divider2 = DIVIDER_2;
            e.printStackTrace();
        }
        if (root_ != null) {
            classTree.setModel(new DefaultTreeModel(root_));
            updateAttrTree(root_);
        }
    }

    public void setRoot(ClassNode root) {
        root_ = root;
        if (root_ != null) {
            classTree.setModel(new DefaultTreeModel(root_));
            updateAttrTree(root_);
        }
    }

    public void updateAttrTree() {
			classTree_valueChanged(null);
    }
    public int getSelectedAttributesCount() {
        return attrTree.getSelectedAttributesCount();
    }

    public KrnAttribute[] getSelectedAttributes(int i) {
        return attrTree.getSelectedAttributes(i);
    }

    public KrnAttribute[] getSelectedAttributes() {
        return attrTree.getSelectedAttributes();
    }

    public KrnClass getSelectedClass() {
        return classTree.getSelectedClass();
    }

    public KrnClass[] getSelectedClasses() {
        return classTree.getSelectedClasses();
    }

    public KrnMethod getSelectedMethod() {
        return attrTree.getSelectedMethod();
    }

    public String getSelectedPath() {
        KrnMethod krnMethod = getSelectedMethod();
        if (krnMethod != null) {
            return getSelectedClass().name + "<method>" + krnMethod.name;
        } 
        
        if (getSelectedAttributes()!=null) {
        	return com.cifs.or2.client.Utils.getPathForAttributes(getSelectedAttributes());
        } else {
        	return com.cifs.or2.client.Utils.getPathForClass(getSelectedClass());
        }
    }

    /**Component initialization*/
    private void jbInit() throws Exception {
        classTree = new ClassTree();
        packageTree = new PackageTree();
		classHierarhyTree = new JTree(new DefaultTreeModel(null, false));
        attrTree = new AttributeTree();
        classTree.setAttrTree(attrTree);
        packageTree.setAttrTree(attrTree);
        attrTree.setClassTree(classTree);
        this.setLayout(new BorderLayout());
        classTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showClassOperations(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showClassOperations(e);
            }
        });
        packageTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showClassOperations(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showClassOperations(e);
            }
        });
        classTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                classTree_valueChanged(e);
                try {
                	KrnClass cls = classTree.getSelectedClass();
                	if(cls != null && cls.id != 99){
                		setComment("Комментарий:\n" + Kernel.instance().getClassComment(cls.id));
                	}
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
            }
        });
        packageTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                packageTree_valueChanged(e);
                try {
                	KrnClass cls = packageTree.getSelectedClass();
					if (cls != null && cls.id != 99) {
						setComment("Комментарий:\n" + Kernel.instance().getClassComment(cls.id));
					}
            		// Вывести иерархию дерева
            		showClassHierarchy(cls);
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
            }
        });
        classTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.isAltDown() && ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        showClassProperties();
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        attrTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showAttrOperations(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showAttrOperations(e);
            }
        });
        attrTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                try {
                    attrTree_valueChanged(e);
                    Object ob = attrTree.getSelectedObject();
                    if (ob != null) {
                        if (ob instanceof KrnAttribute) {
                            KrnAttribute attr = (KrnAttribute) ob;
                            if (attr.id != 0 || attr.uid != null) {
                                setComment("Комментарий:\n" + Kernel.instance().getAttributeComment(attr.id));
                            }
                        } else if (ob instanceof KrnMethod) {
                            KrnMethod method = (KrnMethod) attrTree.getSelectedObject();
                            setComment("Комментарий:\n" + Kernel.instance().getMethodComment(method.uid));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        attrTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.isAltDown() && ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        showAttrProperties();
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // My Initializations
        attrTree.setModel(null);
        /*
         JFrame fr = getParentFrame();
         JMenuBar mb = (fr == null) ? null : fr.getJMenuBar();
         JMenu classMenu = (mb == null) ? null : new JMenu ("Класс");
         JMenu attrMenu  = (mb == null) ? null : new JMenu ("Атрибут");
         if (classMenu != null) mb.add (classMenu, 1);
         if (attrMenu != null)  mb.add (attrMenu, 2);
         */
        // Инициализация меню операций над классами
        classCreateItem.addActionListener(this);
        classCreateItem.setEnabled(false);
        classOperations.add(classCreateItem);
        clsCreateBtn.setEnabled(false);
//    if (classMenu != null) classMenu.add (classCreateItem);

        classDeleteItem.addActionListener(this);
        classDeleteItem.setEnabled(false);
        classOperations.add(classDeleteItem);

        clsDelBtn.setEnabled(false);
//    if (classMenu != null) classMenu.add (classCreateItem);

        classObjectsItem.addActionListener(this);
        searchObjectsItem.addActionListener(this);
        classObjectsItem.setEnabled(false);
        searchObjectsItem.setEnabled(false);

        classCopyNameItem.addActionListener(this);
        classCopyIDItem.addActionListener(this);
        classCopyUIDItem.addActionListener(this);
        classCopyMenu.add(classCopyNameItem);
        classCopyMenu.add(classCopyIDItem);
        classCopyMenu.add(classCopyUIDItem);
        classCopyMenu.setEnabled(false);

        classOperations.add(classObjectsItem);
        classOperations.add(searchObjectsItem);
        classOperations.add(classTruncateItem);
        classOperations.add(classCopyMenu);
        classOperations.add(classFindItem);
        classFindItem.addActionListener(this);

        clsObjBtn.setEnabled(false);
        truncateAction.setEnabled(false);
//    if (classMenu != null) classMenu.add (classObjectsItem);

        //classConstraintsItem.addActionListener(this);
        //classConstraintsItem.setEnabled(false);
        //classOperations.add(classConstraintsItem);

        classReportAttrsItem.addActionListener(this);
        classReportAttrsItem.setEnabled(false);
        classOperations.add(classReportAttrsItem);

        quickRepBtn.setEnabled(false);

        addToFavoriteItem.addActionListener(this);
        classOperations.add(addToFavoriteItem);
        
        classPropItem.setAccelerator(KeyStroke.getKeyStroke('\n', KeyEvent.ALT_MASK));
        classPropItem.addActionListener(this);
        classPropItem.setEnabled(false);
        classOperations.add(classPropItem);
        
        beforeCreateObjEvent.addActionListener(this);
        clsEventsMenu.add(beforeCreateObjEvent);
        afterCreateObjEvent.addActionListener(this);
        clsEventsMenu.add(afterCreateObjEvent);
        beforeDeleteObjEvent.addActionListener(this);
        clsEventsMenu.add(beforeDeleteObjEvent);
        afterDeleteObjEvent.addActionListener(this);
        clsEventsMenu.add(afterDeleteObjEvent);
        clsEventsMenu.setEnabled(false);
        classOperations.add(clsEventsMenu);
        
        editTablePropItem.setEnabled(false);//TODO: Tedit
        editTablePropItem.addActionListener(this); //TODO: Tedit
        classOperations.add(editTablePropItem); //TODO: Tedit

        exportClsItem.addActionListener(this);
        clsExportMenu.add(exportClsItem);
        classOperations.add(clsExportMenu);

        importClsItem.addActionListener(this);
        clsImportMenu.add(importClsItem);
        classOperations.add(clsImportMenu);

        clsPropBtn.setEnabled(false);
//    if (classMenu != null) classMenu.add (classPropItem);

        // Инициализация меню операций над атрибутами
        attrCreateItem.addActionListener(this);
        attrCreateItem.setEnabled(false);

        attrCreateBtn.setEnabled(false);

        revAttrCreateItem.addActionListener(this);
        revAttrCreateItem.setEnabled(false);


        //    if (attrMenu != null) attrMenu.add (attrCreateItem);

        deleteItem.addActionListener(this);
        deleteItem.setEnabled(false);

        deleteButton.setEnabled(false);
//    if (attrMenu != null) attrMenu.add (attrDeleteItem);

        attrCopyNameItem.addActionListener(this);
        attrCopyNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        attrCopyIDItem.addActionListener(this);
        attrCopyIDItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
        attrCopyUIDItem.addActionListener(this);
        attrCopyUIDItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
        attrCopyTypeNameItem.addActionListener(this);
        attrCopyTypeNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
        attrCopyClassNameItem.addActionListener(this);
        attrCopyClassNameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
        attrCopyMenu.add(attrCopyNameItem);
        attrCopyMenu.add(attrCopyIDItem);
        attrCopyMenu.add(attrCopyUIDItem);
        attrCopyMenu.add(attrCopyTypeNameItem);
        attrCopyMenu.add(attrCopyClassNameItem);
        attrCopyMenu.setEnabled(false);

        attrPropItem.setAccelerator(KeyStroke.getKeyStroke('\n', KeyEvent.ALT_MASK));
        attrPropItem.addActionListener(this);
        attrPropItem.setEnabled(false);
        
        editTablePropItemattr.addActionListener(this); //TODO: Tedit
        editTablePropItemattr.setEnabled(false);
        
        createMethod.addActionListener(this);
        methodCopyNameItem.addActionListener(this);
        methodEdit.addActionListener(this);
        
        addTriggerItem.addActionListener(this);
        addTriggerItem.setEnabled(false);
        beforeChangeEvent.addActionListener(this);
        afterChangeEvent.addActionListener(this);
        beforeDeleteEvent.addActionListener(this);
        afterDeleteEvent.addActionListener(this);
        attrEventsMenu.setEnabled(false);

        attrOperations.add(attrCreateItem);
        attrOperations.add(revAttrCreateItem);
        attrOperations.add(createMethod);
        attrOperations.addSeparator();
        attrOperations.add(classCopyMenu);
        attrOperations.add(attrCopyMenu);
        attrOperations.add(methodCopyNameItem);
        attrOperations.add(methodEdit);
        attrOperations.add(attrPropItem);
        attrOperations.add(addTriggerItem);
        attrEventsMenu.add(beforeChangeEvent);
        attrEventsMenu.add(afterChangeEvent);
//        attrEventsMenu.add(beforeDeleteEvent);
//        attrEventsMenu.add(afterDeleteEvent);
        attrOperations.add(attrEventsMenu);
        attrOperations.add(editTablePropItemattr); //TODO: Tedit
        attrOperations.add(deleteItem);
        attrOperations.addSeparator();
        attrOperations.add(aoUpdateRefs);
        attrOperations.add(aoDeleteUnused);
        attrOperations.add(findRevAttrItem);

        attrPropBtn.setEnabled(false);

        aoUpdateRefs.addActionListener(this);
        aoUpdateRefs.setEnabled(false);

        attrUpdateBtn.setEnabled(false);

        aoDeleteUnused.addActionListener(this);
        aoDeleteUnused.setEnabled(false);

        findRevAttrItem.addActionListener(this);
        findRevAttrItem.setEnabled(false);
        
        attrDelObjsBtn.setEnabled(false);
        // ToolBars
        clsFindBtn.addActionListener(this);
        clsRefreshBtn.addActionListener(this);
        clsCreateBtn.addActionListener(this);
        clsDelBtn.addActionListener(this);
        clsPropBtn.addActionListener(this);
        clsObjBtn.addActionListener(this);
        quickRepBtn.addActionListener(this);
        //renameBtn.addActionListener(this);
        viewTriggersBtn.addActionListener(this);

        attrCreateBtn.addActionListener(this);
        deleteButton.addActionListener(this);
        attrPropBtn.addActionListener(this);
        attrUpdateBtn.addActionListener(this);
        attrDelObjsBtn.addActionListener(this);

        methodCreateBtn.addActionListener(this);

        methodCreateBtn.setEnabled(false);

        exportClassHierarchy.addActionListener(this);
        exportClassHierarchy.setVisible(false);

        viewHistoryBtn.setEnabled(false);
        viewHistoryBtn.addActionListener(this);
        methodBtn.setEnabled(false);
        methodBtn.addActionListener(this);

        recycleBtn.addActionListener(this);

        clsTruncateBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("deleteObjects"));
        clsTruncateBtn.setText("");
        clsTruncateBtn.setToolTipText("Удалить объекты класса вместе с ссылками на них");

        classTruncateItem.setIcon(kz.tamur.rt.Utils.getImageIcon("deleteObjects"));        
        classTruncateItem.setToolTipText("Удалить объекты класса вместе с ссылками на них");

        toolBar.add(clsRefreshBtn);
        toolBar.add(clsCreateBtn);
        toolBar.add(clsDelBtn);
        toolBar.add(clsPropBtn);
        toolBar.add(clsObjBtn);
        toolBar.add(clsTruncateBtn);
        toolBar.add(clsFindBtn);
        toolBar.add(quickRepBtn);
        //toolBar.add(renameBtn);
        toolBar.addSeparator();
        toolBar.add(attrCreateBtn);
        toolBar.add(deleteButton);
        toolBar.add(attrUpdateBtn);
        toolBar.add(attrDelObjsBtn);
        toolBar.addSeparator();
        toolBar.add(methodCreateBtn);
        toolBar.addSeparator();
        toolBar.add(attrPropBtn);
        toolBar.addSeparator();
        toolBar.add(favoritesClasses);
        toolBar.add(exportClassHierarchy);
        toolBar.addSeparator();
        toolBar.add(wsGeneratorPanel);
        toolBar.add(viewTriggersBtn);
        toolBar.add(viewHistoryBtn);
        toolBar.add(methodBtn);
        toolBar.add(recycleBtn);
//        toolBar.add(inspBtn);
//        inspBtn.setVisible(false);
        JPanel p = new JPanel(new BorderLayout());
        p.add(toolBar, BorderLayout.WEST);
        this.add(p, BorderLayout.NORTH);
        this.add(splitPanel, BorderLayout.CENTER);//+

        jScrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setMinimumSize(new Dimension(120, 60));
        jScrollPane2.setMinimumSize(new Dimension(120, 60));
        jScrollPane2.setPreferredSize(new Dimension(900, jScrollPane2.getHeight()));
        jScrollPane3.getViewport().add(comText, null);
        
        comText.setEditable(false);
        In_jPanel2and3.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        packageTreeScrollPane.setMinimumSize(new Dimension(120, 300));
        hierarchyTreeScrollPane.setMinimumSize(new Dimension(120, 300));
        classHierarchyScrollPane.setMinimumSize(new Dimension(120, 200));
        
        treesTabbedPane.setFont(Utils.getTabbedFont());
        classHierarchySplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        classHierarchySplitPanel.setDividerLocation(0.8);
        classHierarchySplitPanel.setResizeWeight(0.8);
        classHierarchySplitPanel.setTopComponent(packageTreeScrollPane);
        classHierarchySplitPanel.setBottomComponent(classHierarchyScrollPane);
        treesTabbedPane.addTab("Package Explorer", kz.tamur.rt.Utils.getImageIcon("packageExplorer"), classHierarchySplitPanel);
        treesTabbedPane.addTab("Type Hierarchy", kz.tamur.rt.Utils.getImageIcon("typeHierarchy"), hierarchyTreeScrollPane);
        treesTabbedPane.setSelectedIndex(1);
        treesTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
	        	if (treesTabbedPane.getSelectedIndex() == 0) {
	        		packageTree_valueChanged(null);
	        	} else {
	        		classTree_valueChanged(null);
	        	}
			}
		});
        splitPanel.setLeftComponent(treesTabbedPane);
        In_jPanel2and3.setLeftComponent(jScrollPane2);
        In_jPanel2and3.setRightComponent(rightSplitPane);
        rightSplitPane.setTopComponent(jScrollPane3);
        rightSplitPane.setResizeWeight(0.16);
        methodtSplitPane.init();
        rightSplitPane.setBottomComponent(methodtSplitPane);

        In_P23andEditor.setOrientation(JSplitPane.VERTICAL_SPLIT);
        In_P23andEditor.setTopComponent(In_jPanel2and3);
        
		JButton btnGetEditor = new JButton("Get Editor");
		final JPanel jPanel1 = new javax.swing.JPanel();
		jCheckBox1.setText("Панель редактора.");
		btnGetEditor.setMargin(Constants.INSETS_2);
		btnGetEditor.setOpaque(false);
		final JPanel jPanel2 = new javax.swing.JPanel();
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
            	EditorIsVisible(jPanel1, jPanel2, jCheckBox1.isSelected());
            }
        });
        
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
		In_P23andEditor.setBottomComponent(jPanel1);
        
        splitPanel.setRightComponent(In_P23andEditor);

        In_jPanel2and3.setResizeWeight(0.8);
        splitPanel.setResizeWeight(0.16);
        //comSplitPanel.setResizeWeight(0.3);
        In_P23andEditor.setResizeWeight(0.5); // TODO: spilit
        
        jScrollPane2.getViewport().add(attrTree, null);
        packageTreeScrollPane.getViewport().add(packageTree, null);
        hierarchyTreeScrollPane.getViewport().add(classTree, null);
        classHierarchyScrollPane.getViewport().add(classHierarhyTree, null);
        
		DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				ClassHierarchyNode node = (ClassHierarchyNode) value;
				KrnClass cls = node.getKrnClass();
				l.setIcon(ClassTreeIconLoader.getIcon(cls.isRepl, cls.isVirtual()));
				l.setForeground(sel ? Color.white : Color.black);
				l.setBackground(Utils.getDarkShadowSysColor());
				l.setOpaque(selected);
				return l;
			}
		};
    	cellRenderer.setBackgroundNonSelectionColor(isOpaque ? Color.lightGray : new Color(0, 0, 0, 0));
    	cellRenderer.setClosedIcon(null);
    	cellRenderer.setOpenIcon(null);
    	cellRenderer.setLeafIcon(null);
    	cellRenderer.setBackgroundSelectionColor(Utils.getDarkShadowSysColor());
    	cellRenderer.setBorderSelectionColor(Utils.getDarkShadowSysColor());
    	
    	classHierarhyTree.putClientProperty("JTree.lineStyle", "Angled");
        classHierarhyTree.setBackground(Color.lightGray);
        classHierarhyTree.setCellRenderer(cellRenderer);
        classHierarhyTree.setOpaque(isOpaque);

//    if (attrMenu != null) attrMenu.add (attrPropItem);
        statusBar.addAnyComponent(statusLabel);
        statusBar.addEmptySpace();
        statusBar.addCorner();
        if (isDialog) {
            this.add(statusBar, BorderLayout.SOUTH);
            comSplitPanel.addPropertyChangeListener(this);
            splitPanel.addPropertyChangeListener(this);
        }
        this.setPreferredSize(new Dimension(900, 500));

        EditorIsVisible(jPanel1, jPanel2, false);
        
        setOpaque(isOpaque);
        splitPanel.setOpaque(isOpaque);
        comSplitPanel.setOpaque(isOpaque);
        //tabpane.setOpaque(isOpaque);
        In_jPanel2and3.setOpaque(isOpaque);
        In_P23andEditor.setOpaque(isOpaque);
        packageTreeScrollPane.setOpaque(isOpaque);
        hierarchyTreeScrollPane.setOpaque(isOpaque);
        jScrollPane2.setOpaque(isOpaque);
        jScrollPane3.setOpaque(isOpaque);
        packageTreeScrollPane.getViewport().setOpaque(isOpaque);
        hierarchyTreeScrollPane.getViewport().setOpaque(isOpaque);
        jScrollPane2.getViewport().setOpaque(isOpaque);
        jScrollPane3.getViewport().setOpaque(isOpaque);
        attrTree.setOpaque(isOpaque);
        comText.setOpaque(isOpaque);
    }
    
    class ClassHierarchyNode extends DefaultMutableTreeNode {
    	
    	private KrnClass cls;
    	
    	public ClassHierarchyNode(KrnClass cls) {
//    		super(cls.getName().contains("::") ? cls.getName().substring(cls.getName().lastIndexOf("::") + 2) : cls.getName());
    		super(cls.getName());
    		this.cls = cls;
    	}
    	
    	public KrnClass getKrnClass() {
    		return cls;
    	}
    }
    
    private void showClassHierarchy(KrnClass cls) throws KrnException {
    	if (cls != null) {
	    	List<KrnClass> classes = new ArrayList<>();
	    	getClassHierarchy(classes, cls);
	    	ClassHierarchyNode top = new ClassHierarchyNode(classes.get(classes.size() - 1));
	    	ClassHierarchyNode parent = top;
	    	for (int i = classes.size() - 2; i >= 0; i--) {
	    		ClassHierarchyNode child = new ClassHierarchyNode(classes.get(i));
	    		parent.add(child);
	    		parent = child;
	    		if (i == 0) {
	    			addSubclasses(child);
	    		}
	    	}
	    	classHierarhyTree.setModel(new DefaultTreeModel(top, false));
	    	expandPath(classHierarhyTree, classHierarhyTree.getModel(), new javax.swing.tree.TreePath(top));
    	} else {
    		classHierarhyTree.setModel(null);
    	}
    }
    
    private void addSubclasses(ClassHierarchyNode parent) {
    	try {
			KrnClass[] classes = krn_.getClasses(parent.getKrnClass().id);
			for (int i = 0; i < classes.length; i++) {
	    		ClassHierarchyNode child = new ClassHierarchyNode(classes[i]);
	    		parent.add(child);
	    		addSubclasses(child);
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }
    
    private void expandPath(javax.swing.JTree tree, javax.swing.tree.TreeModel model, javax.swing.tree.TreePath path) {
		tree.expandPath(path);
		Object parent = path.getLastPathComponent();
		for (int i = 0; i < model.getChildCount(parent); i++) {
			expandPath(tree, model, path.pathByAddingChild(model.getChild(parent, i)));
		}
    }
    
    private void getClassHierarchy(List<KrnClass> classes, KrnClass cls) throws KrnException {
    	classes.add(cls);
    	if (cls.getParentId() > 0) {
    		getClassHierarchy(classes, Kernel.instance().getClass(cls.getParentId()));
    	}
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!selfChanging && JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(evt.getPropertyName())) {
            int width = getTopLevelAncestor().getWidth();

            if (splitPanel.equals(evt.getSource())) {
                divider1 = ((Number) evt.getNewValue()).doubleValue() / width;
            } else if (comSplitPanel.equals(evt.getSource())) {
                divider2 = ((Number) evt.getNewValue()).doubleValue() / width;
            }

            String workDir = Utils.getUserWorkingDir();
            if (Funcs.isValid(workDir)) {
    	        File dir = Funcs.getCanonicalFile(workDir);

	            File f = new File(dir, "propsJboss");
	            if (f.exists()) {
	                try {
	                    FileInputStream fis = new FileInputStream(f);
	                    props.load(fis);
	                    fis.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	            try {
	                FileOutputStream fos = new FileOutputStream(f);
	                props.setProperty("divider1", String.valueOf(divider1));
	                props.setProperty("divider2", String.valueOf(divider2));
	                props.store(fos, "Properties");
	                fos.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
            }
        }
    }

    void showClassOperations(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	if (treesTabbedPane.getSelectedIndex() == 0) {
	            TreePath path = packageTree.getPathForLocation(e.getX(), e.getY());
	            if (path != null) {
	            	packageTree.setSelectionPath(path);
	            } else {
	            	packageTree.setSelectionRow(1);
	            }
            	KrnClass cls = packageTree.getSelectedNode().getKrnClass();
            	if (cls != null) {
    	        	if (favoritesClasses.isContain(cls.id)) {
    	        		addToFavoriteItem.setEnabled(false);
    	        	} else {
    	        		addToFavoriteItem.setEnabled(true);
    	        	}
    	        	classOperations.show(e.getComponent(), e.getX(), e.getY());
            	}
        	} else {
	            TreePath path = classTree.getPathForLocation(e.getX(), e.getY());
	            if (path != null) {
	                classTree.setSelectionPath(path);
	            } else {
	                classTree.setSelectionRow(1);
	            }
	        	if (favoritesClasses.isContain(classTree.getSelectedClass().id)) {
	        		addToFavoriteItem.setEnabled(false);
	        	} else {
	        		addToFavoriteItem.setEnabled(true);
	        	}
	        	classOperations.show(e.getComponent(), e.getX(), e.getY());
        	}
        }
    }

    void showAttrOperations(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	if (treesTabbedPane.getSelectedIndex() == 0) {
        		return;
        	}
            TreePath path = attrTree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                attrTree.setSelectionPath(path);
            } else {
                attrTree.setSelectionRow(0);
            }
            Object obj = attrTree.getSelectedObject();

            if (obj instanceof KrnAttribute) {
                attrCopyMenu.setVisible(attrTree.getSelectedAttribute().id != 0);
                classCopyMenu.setVisible(attrTree.getSelectedAttribute().id == 0);
                methodCopyNameItem.setVisible(false);
                methodEdit.setVisible(false);
                addTriggerItem.setVisible(attrTree.getSelectedAttribute().id != 1 && attrTree.getSelectedAttribute().id != 2);
                attrEventsMenu.setVisible(attrTree.getSelectedAttribute().id != 1 && attrTree.getSelectedAttribute().id != 2 && attrTree.getSelectedAttribute().rAttrId == 0);
            } else if (obj instanceof KrnMethod) {//метод
                classCopyMenu.setVisible(false);
                attrCopyMenu.setVisible(false);
                methodCopyNameItem.setVisible(true);
                methodEdit.setVisible(true);
                addTriggerItem.setVisible(false);
                attrEventsMenu.setVisible(false);
            }
            attrOperations.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public KrnAttribute getSelectedAttribute() {
        return attrTree.getSelectedAttribute();
    }

    public void setSelectedPath(String path) throws KrnException {
        classTree.setSelectedPath(root_);
        attrTree.setSelectedPath(path);
        statusLabel.setText(path);
        Or3Frame.instance().setWorkStatusLabelText(path);
    }
    
    public void setClsTriggerEventExpression(final int mode) {
    	Object sobj;
    	if (treesTabbedPane.getSelectedIndex() == 0) {
    		sobj = packageTree.getSelectedClass();
    	} else {
    		sobj = classTree.getSelectedClass();
    	}
		if (sobj != null && sobj instanceof KrnClass) {
			try {
			    final KrnClass cls = krn_.getClassById(((KrnClass) sobj).id);
			    if (cls == null) return;
			    boolean readOnly = false;
            	UserSessionValue us = krn_.vcsLockModel(cls.uid, Utils.getModelChangeTypeByTriggerType(mode, 0));
                if (us != null) {
                    if (triggerVcsLock(cls, 0, mode, us) == BUTTON_YES) {
                        readOnly = true;
                    } else {
                        return;
                    }
                }
			    byte[] bs;
			    int tr;
			    String event; 
			    if (mode == 0) {
			    	bs = cls.beforeCreateObjExpr;
			    	tr = cls.beforeCreateObjTr;
			    	event = "Перед созданием объекта";
			    } else if (mode == 1) {
			    	bs = cls.afterCreateObjExpr;
			    	tr = cls.afterCreateObjTr;
			    	event = "После создания объекта";
			    } else if (mode == 2) {
			    	bs = cls.beforeDeleteObjExpr;
			    	tr = cls.beforeDeleteObjTr;
			    	event = "Перед удалением объекта";
			    } else {
			    	bs = cls.afterDeleteObjExpr;
			    	tr = cls.afterDeleteObjTr;
			    	event = "После удаления объекта";
			    }
			    String expr = (bs != null && bs.length > 0) ? new String(bs, "UTF-8") : "";
			    JTextArea exprArea = new JTextArea();
			    exprArea.setText(expr);
                final ExpressionEditor ex = new ExpressionEditor(exprArea.getText(), tr, this, cls, mode, readOnly);
                ex.setVisibleTransactionCheck(true);
				ActionListener btnaction = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						try {
							krn_.setClsTriggerEventExpression(ex.getExpression(), cls.id, mode, ex.getStatusTansactionCheck());
						} catch (KrnException e) {
							e.printStackTrace();
    		                Container cnt = getTopLevelAncestor();
    		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
						}
					}
				};
				EditorWindow.addTab(cls.uid, "Класс '" + cls.name + "', событие '" + event + "'", ex, btnaction, "ClassBrowser");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
    }

    public void setAttrTriggerEventExpression(final int mode) {
    	Object sobj = attrTree.getSelectedObject();
		if (sobj instanceof KrnAttribute) {
			try {
			    final KrnAttribute attr = krn_.getAttribute(((KrnAttribute) sobj).id);
			    if (attr == null) return;
				boolean readOnly = false;
            	UserSessionValue us = krn_.vcsLockModel(attr.uid, Utils.getModelChangeTypeByTriggerType(mode, 1));
                if (us != null) {
                    if (triggerVcsLock(attr, 1, mode, us) == BUTTON_YES) {
                        readOnly = true;
                    } else {
                        return;
                    }
                }
			    byte[] bs;
			    int tr;
			    String event; 
			    if (mode == 0) {
			    	bs = attr.beforeEventExpr;
			    	tr = attr.beforeEventTr;
			    	event = "Перед изменением значения атрибута";
			    } else if (mode == 1) {
			    	bs = attr.afterEventExpr;
			    	tr = attr.afterEventTr;
			    	event = "После изменения значения атрибута";
			    } else if (mode == 2) {
			    	bs = attr.beforeDelEventExpr;
			    	tr = attr.beforeDelEventTr;
			    	event = "Перед удалением значения атрибута";
			    } else {
			    	bs = attr.afterDelEventExpr;
			    	tr = attr.afterDelEventTr;
			    	event = "После удаления значения атрибута";
			    }
			    String expr = (bs != null && bs.length > 0) ? new String(bs, "UTF-8") : "";
			    JTextArea exprArea = new JTextArea();
			    exprArea.setText(expr);
			    final ExpressionEditor ex = new ExpressionEditor(exprArea.getText(), tr, this, attr, mode, readOnly);
                ex.setVisibleTransactionCheck(true);
				ActionListener btnaction = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						try {
							krn_.setAttrTriggerEventExpression(ex.getExpression(), attr.id, mode, ex.getStatusTansactionCheck());
							updateAttrTree(krn_.getClassNode(attr.classId));
						} catch (KrnException e) {
							e.printStackTrace();
    		                Container cnt = getTopLevelAncestor();
    		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
						}
					}
				};
				EditorWindow.addTab(attr.uid, "Атрибут '" + attr.name + "', событие '" + event + "'", ex, btnaction, "ClassBrowser");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final Kernel krn = Kernel.instance();
        try {
            AbstractButton item = (AbstractButton) e.getSource();
            Container cont = this.getTopLevelAncestor();
            DesignerDialog dlg = null;
            int messRes = -1;
            if (item == attrPropItem || item == attrPropBtn) {
                showAttrProperties();
            } else if (item == addTriggerItem) {
            	showTriggerProperties();
            } else if (item == beforeCreateObjEvent) {
            	setClsTriggerEventExpression(0);
            } else if (item == afterCreateObjEvent) {
            	setClsTriggerEventExpression(1);
            } else if (item == beforeDeleteObjEvent) {
            	setClsTriggerEventExpression(2);
            } else if (item == afterDeleteObjEvent) {
            	setClsTriggerEventExpression(3);
            } else if (item == beforeChangeEvent) {
            	setAttrTriggerEventExpression(0);
            } else if (item == afterChangeEvent) {
            	setAttrTriggerEventExpression(1);
            } else if (item == beforeDeleteEvent) {
            	setAttrTriggerEventExpression(2);
            } else if (item == afterDeleteEvent) {
            	setAttrTriggerEventExpression(3);
            } else if (item == editTablePropItemattr) {
            	KrnAttribute katr = attrTree.getSelectedAttribute();
            	String tname = kz.tamur.or3.util.Tname.getAttrTableName(katr);
            	editTableShow(tname, katr.name, krn);
            }else if (item == attrCreateItem || item == attrCreateBtn) {
                KrnAttribute par = attrTree.getSelectedAttribute();
                
                final KrnClass cls = Kernel.instance().getClass(par.typeClassId);
                if (cls.isVirtual()) {
                    MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Создание атрибута в виртуальном классе запрещено!");
                    return;
                }
                final AttrPropPanel ap = new AttrPropPanel(cls, null, null, "");

                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont, "Создание атрибута", ap);
                } else {
                    dlg = new DesignerDialog((Frame) cont, "Создание атрибута", ap);
                }
                ap.setLastSelectedClass(lastTypeNode);
                dlg.setOkButtonActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String emptyMess = ap.getEmptyMessage();
                        if (emptyMess.length() > 0) {
                            MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, emptyMess);
                            return;
                        } else {
                            try {
                            	KrnAttribute existingAttr = krn.getAttributeByName(cls, ap.getAttrName());
                            	if (existingAttr != null) {
                            		KrnClass existingAttrCls = krn.getClassById(existingAttr.classId);
                            		JOptionPane.showMessageDialog(ap, "Атрибут с таким именем уже имеется в " + (cls.id == existingAttrCls.id ? "данном" : "родительском") + " классе '" + existingAttrCls.name + "'.", "Ошибка создания атрибута", JOptionPane.ERROR_MESSAGE);
                                    return;
                            	}
                            	ClassNode clazz = krn.getClassNode(cls.id);
                            	List<ClassNode> subClasses = new ArrayList<>();
                            	clazz.getSubClasses(subClasses);
                            	for (ClassNode subClass : subClasses) {
                            		existingAttr = subClass.getAttribute(ap.getAttrName());
                            		if (existingAttr != null) {
                                		KrnClass existingAttrCls = krn.getClassById(existingAttr.classId);
                                		JOptionPane.showMessageDialog(ap, "Атрибут с таким именем уже имеется в дочернем классе '" + existingAttrCls.name + "'.", "Ошибка создания атрибута", JOptionPane.ERROR_MESSAGE);
                                        return;
                                	}
                            	}
                                long flags = ap.getFlags();
                                if (ap.isAggregate()) {
                                    flags |= KrnAttribute.AGGREGATE;
                                }
                                KrnAttribute attr = krn.createAttribute(
                                        cls, ap.getType(), ap.getAttrName(),
                                        ap.collType(), ap.isUnique(),
                                        ap.isIndexed(), ap.isMultiLang(),
                                        ap.isRepl(), ap.getAttrSize(),
                                        flags, 0, 0, false, ap.getAttrTname(),
                                        ap.getAccessModifierType());
                                String comment = ap.getComment();
                                if (comment.length() > 0) {
                                    krn.setAttributeComment(attr.uid, comment);
                                }
                                JButton b = (JButton) e.getSource();
                                Dialog d = (Dialog) b.getTopLevelAncestor();
                                d.dispose();
                            } catch (KrnException e1) {
                                //e1.printStackTrace();
                            	System.out.println(e1.getMessage());
                                JButton b = (JButton) e.getSource();
                                Dialog d = (Dialog) b.getTopLevelAncestor();
                                JOptionPane.showMessageDialog(d,
                                	    "Невозможно создать аттрибут.\nПроверьте параметры создания атрибута.",
                                	    "create attribute error",
                                	    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
                dlg.show();
                lastTypeNode = ap.getLastSelectedClass();
                
            } else if (item == revAttrCreateItem) {
            	KrnAttribute par = attrTree.getSelectedAttribute();
                
                final ClassNode cls = Kernel.instance().getClassNode(par.typeClassId);
                final RevAttrPanel ap = new RevAttrPanel(cls, null, lastTypeNode, "");

                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont, "Создание обратного атрибута", ap);
                } else {
                    dlg = new DesignerDialog((Frame) cont, "Создание обратного атрибута", ap);
                }
                dlg.setOkButtonActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String errMsg = ap.getErrorMessage();
                        if (errMsg.length() > 0) {
                            MessagesFactory.showMessageDialog(Or3Frame.instance(),
                                    MessagesFactory.ERROR_MESSAGE, errMsg);
                            return;
                        } else {
                            try {
                                KrnAttribute rattr = ap.getRevAttribute();
                                long revAttrId = (rattr != null) ? rattr.id : 0;
                                KrnAttribute sattr = ap.getSortAttribute();
                                long sortAttrId = (sattr != null) ? sattr.id : 0;
                                long flags = 0;
                                if (ap.isAggregate()) {
                                    flags |= KrnAttribute.AGGREGATE;
                                }
                                KrnAttribute attr = krn.createAttribute(
                                        cls.getKrnClass(),
                                        ap.getType().getKrnClass(), ap.getName(),
                                        ap.getColType(), false,
                                        false, false, false, 0, flags, revAttrId,
                                        sortAttrId, ap.isSortDesc(), null, 0);
                                String comment = ap.getComment();
                                if (comment.length() > 0) {
                                    krn.setAttributeComment(attr.uid, comment);
                                }
                                JButton b = (JButton) e.getSource();
                                Dialog d = (Dialog) b.getTopLevelAncestor();
                                d.dispose();
                            } catch (KrnException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
                dlg.show();
                lastTypeNode = ap.getLastSelectedClass();
            
            } else if (item == deleteItem || item == deleteButton) {
                Object sobj = attrTree.getSelectedObject();
                if (sobj instanceof KrnAttribute) {
                    deleteAttribute((KrnAttribute) sobj);
                } else if (sobj instanceof KrnMethod) {
                	final KrnMethod mth = (KrnMethod) sobj;
                	try {
                    	UserSessionValue us = krn.vcsLockModel(mth.uid, ModelChange.ENTITY_TYPE_METHOD);
                        if (us != null) {
                        	MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, "Невозможно удалить выбранный метод! Метод редактируется пользователем " + us.name + ".");
                        	return;
                        }
                    } catch (KrnException exc) {
                        exc.printStackTrace();
                    }
                    if (krn.getBindingModuleToUserMode()) {
                    	if (mth.ownerId > 0) {
                    		long currentUserId = krn.getUserSession().userObj.id;
                    		if (mth.ownerId != currentUserId) {
                    			KrnObject userObj = krn.getObjectById(mth.ownerId, 0);
                    			if (userObj != null) {	// Владелец метода существует
                        			KrnClass userCls = krn.getClassByName("User");
                        			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
                        			String userName = krn.getStringsSingular(mth.ownerId, userNameAttr.id, 0, false, false);
	                                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, "Невозможно удалить выбранный метод! Владельцем объекта является пользователь " + userName + ".");
                                	return;
                    			}
                    		}
                    	}
                    }
                    deleteMethod(mth);
                }
                return;
            } else if (item == aoUpdateRefs || item == attrUpdateBtn) {
                String msg = "Вы уверены что хотите продолжить операцию?";
                if (cont instanceof Dialog) {
                    messRes = MessagesFactory.showMessageDialog((Dialog) cont,
                            MessagesFactory.CONFIRM_MESSAGE, msg);
                } else {
                    messRes = MessagesFactory.showMessageDialog((Frame) cont,
                            MessagesFactory.CONFIRM_MESSAGE, msg);
                }
                if (ButtonsFactory.BUTTON_YES == messRes) {
                    KrnAttribute attr = attrTree.getSelectedAttribute();
                    krn.updateReferences(attr.classId, attr.id);
                }
            } else if (item == aoDeleteUnused || item == attrDelObjsBtn) {
                String msg = "Вы уверены что хотите продолжить операцию?";
                if (cont instanceof Dialog) {
                    messRes = MessagesFactory.showMessageDialog((Dialog) cont,
                            MessagesFactory.CONFIRM_MESSAGE, msg);
                } else {
                    messRes = MessagesFactory.showMessageDialog((Frame) cont,
                            MessagesFactory.CONFIRM_MESSAGE, msg);
                }
                if (ButtonsFactory.BUTTON_YES == messRes) {
                    KrnAttribute attr = attrTree.getSelectedAttribute();
                    krn.deleteUnusedObjects(attr.classId, attr.id);
                }
            } else if (item == findRevAttrItem) {
            	KrnAttribute attr = attrTree.getSelectedAttribute();
            	List<KrnAttribute> revAttrs = Kernel.instance().getRevAttributes2(attr.id);
            	if (revAttrs.size() == 0) {
	                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Обратный атрибут не найден!");
            	} else {
                	classTree.setSelectedPath(krn.getClassNode(revAttrs.get(0).classId));
                	attrTree.setSelectedPath(revAttrs.get(0));
            	}
            } else if (item == createMethod || item == methodCreateBtn) {
                MethodPanel mp = new MethodPanel(null, "",false);
                Container cnt = getTopLevelAncestor();
                if (cnt instanceof Frame) {
                    dlg = new DesignerDialog((Frame) cnt,
                            "Создание метода", mp);
                } else {
                    dlg = new DesignerDialog((Dialog) cnt,
                            "Создание метода", mp);
                }
                boolean passed = false;
                while (!passed) {
                    dlg.show();
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                        try {
                            ClassNode cNode = null;
                            Object par = attrTree.getSelectedObject();
                            if (par != null) {
                                if (par instanceof KrnMethod) {
                                    final KrnClass cls = Kernel.instance().getClass(((KrnMethod) par).classId);
                                    cNode = krn.getClassNode(cls.id);
                                } else {
                                    final KrnClass cls = Kernel.instance().getClass(((KrnAttribute) par).typeClassId);
                                    cNode = krn.getClassNode(cls.id);
                                }
                                if (cNode != null) {
                                    KrnMethod method = cNode.createMethod(
                                            mp.getMethodName(),
                                            mp.isClassMethod(),
                                            mp.getExpression());
                                    String comment = mp.getComment();
                                    if (comment.length() > 0) {
                                        krn.setMethodComment(method.uid, comment);
                                    }
                                    passed = true;
                                }
                            }
                        } catch (Exception ex) {
                            MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(),
                                    MessagesFactory.ERROR_MESSAGE, "Отсутствуют необходимые параметры\n"
                                    + "для создания метода!");
                        }
                    } else {
                        passed = true;
                    }
                }
            } else if (item == attrCopyNameItem) {//Копирование название атрибута
                String str = attrTree.getNodeName();
                ClientUtils.setClipboard(str);
            } else if (item == attrCopyIDItem) {
                String str = "" + attrTree.getNodeID();
                ClientUtils.setClipboard(str);
            } else if (item == attrCopyUIDItem) {
                String str = attrTree.getNodeUID();
                ClientUtils.setClipboard(str);
            } else if (item == attrCopyTypeNameItem) {
                String str = attrTree.getNodeTypeName();
                ClientUtils.setClipboard(str);
            } else if (item == attrCopyClassNameItem) {
                String str = attrTree.getNodeClassName();
                ClientUtils.setClipboard(str);
            } else if (item == methodCopyNameItem) {
                String str = attrTree.getNodeName();
                ClientUtils.setClipboard(str);
            } else if (item == methodEdit) {//редактировать метод
                try {
                    Object sobj = attrTree.getSelectedObject();
                    if (sobj instanceof KrnMethod) {
                        final KrnMethod mth = (KrnMethod) sobj;
//                        if (krn.getBindingModuleToUserMode()) {
//                        	if (mth.ownerId > 0) {
//                        		long currentUserId = krn.getUserSession().userObj.id;
//                        		if (mth.ownerId != currentUserId) {
//                        			KrnObject userObj = krn.getObjectById(mth.ownerId, 0);
//                        			if (userObj != null) {	// Владелец метода существует
//	                        			KrnClass userCls = krn.getClassByName("User");
//	                        			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
//	                        			String userName = krn.getStringsSingular(mth.ownerId, userNameAttr.id, 0, false, false);
//		                                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного метода является пользователь " + userName + "!");
//                        			}
//                        		}
//                        	}
//                        }
                        
                        final ClassNode clsNode = krn.getClassNode(mth.classId);
                        boolean readOnly=false;
                        try {
                        	UserSessionValue us = krn.vcsLockModel(mth.uid, ModelChange.ENTITY_TYPE_METHOD);
                            if (us != null) {
                                if (mthVcsLock(mth.name, us) == BUTTON_YES) {
                                    readOnly = true;
                                } else {
                                    return;
                                }
                            }
                            if (!readOnly) readOnly = !krn.getUser().hasRight(Or3RightsNode.METHODS_EDIT_RIGHT);
                            if(!readOnly){
                                us = krn.blockMethod(mth.uid);
                                if (us != null && !us.id.equals(krn.getUUID())) {
                                    if (mthLock(mth.name, us) == BUTTON_YES) {
                                        readOnly = true;
                                    } else
                                        return;
                                }
                            }
                        } catch (KrnException exc) {
                            exc.printStackTrace();
                        }
                        String expr = clsNode.getMethodExpression(mth.name);
                        JTextArea exprArea = new JTextArea();
                        exprArea.setText(expr);
                        
                        final ExpressionEditor ex = new ExpressionEditor(exprArea.getText(), this, mth, readOnly);
                        ex.setSourceMethodToDebugger(mth);
                        JLabel ownerLabel = Utils.createLabel("Автор: " + (kz.tamur.comps.Utils.getMethodOwner(mth)!= null? kz.tamur.comps.Utils.getMethodOwner(mth): ""));
                        ex.getStatusBar().addLabel(ownerLabel);
                        
						ActionListener btnaction = new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									clsNode.changeMethod(mth.uid, mth.name, mth.isClassMethod, ex.getExpression());
								} catch (KrnException e1) {
									e1.printStackTrace();
									Container cnt = getTopLevelAncestor();
									MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e1.getMessage());
								}
							}
						};
                      String title = clsNode.getName() + "." + mth.name; 
                      EditorWindow.addTab(mth.uid, title, ex, btnaction, "ClassBrowser");

                    }
                } catch (KrnException exc) {
                    exc.printStackTrace();
                }
                // Class Operations
            } else if (item == classDeleteItem || item == clsDelBtn) {
        		KrnClass cls;
            	if (treesTabbedPane.getSelectedIndex() == 0) {
            		cls = packageTree.getSelectedClass();
            	} else {
            		cls = classTree.getSelectedClass();
            	}
        		if (cls == null) {
        			return;
        		}
                String msg = "Вы действительно хотите удалить класс '" + cls.name + "'?";
                if (cont instanceof Dialog) {
                    messRes = MessagesFactory.showMessageDialog((Dialog) cont, MessagesFactory.CONFIRM_MESSAGE, msg);
                } else {
                    messRes = MessagesFactory.showMessageDialog((Frame) cont, MessagesFactory.CONFIRM_MESSAGE, msg);
                }
                if (ButtonsFactory.BUTTON_YES == messRes) {
                	try {
	                    Kernel.instance().deleteClass(cls);
	                } catch (KrnException ex) {
	                	MessagesFactory.showMessageDialog((Frame) cont, MessagesFactory.ERROR_MESSAGE, "Нельзя удалить класс! " + ex.getMessage());
	                    ex.printStackTrace();
	                }
                }
            } else if (item == addToFavoriteItem) {
            	Kernel kernel = Kernel.instance();
            	User user = kernel.getUser();
            	int index = kernel.getLongs(user.getObject(), "favoritesClasses", 0).length;
            	kernel.setLong(user.getObject().id, user.getObject().classId, "favoritesClasses", index, classTree.getSelectedClass().id, 0);
            	favoritesClasses.updateList();
            } else if (item == classPropItem || item == clsPropBtn) {
                //Показать свойства класса
                showClassProperties();
            } else if (item == editTablePropItem) { //TODO: Tedit
            	KrnClass base = classTree.getSelectedClass();
            	String tname = kz.tamur.or3.util.Tname.getClassTableName(base);
            	editTableShow(tname, base.name, krn);
        	} else if (item == classCreateItem || item == clsCreateBtn) {
        		KrnClass baseCls;
            	if (treesTabbedPane.getSelectedIndex() == 0) {
	                baseCls = packageTree.getSelectedClass();
            	} else {
	                baseCls = classTree.getSelectedClass();
            	}
                if (baseCls == null) {
                	return;
                }
	            kz.tamur.admin.ClassPropPanel cp = new kz.tamur.admin.ClassPropPanel(null, baseCls, "");
	            if (cont instanceof Dialog) {
	                dlg = new DesignerDialog((Dialog) cont, "Создание класса", cp);
	            } else {
	                dlg = new DesignerDialog((Frame) cont, "Создание класса", cp);
	            }
	            cp.setparentDesignerDialog(dlg);
	            dlg.setResizable(false);
	            dlg.setOkButtonActionListener(ClassPropDialogOkListener(dlg, cp, krn, baseCls));
	            dlg.show();
            } else if (item == classObjectsItem || item == clsObjBtn) {
            	KrnClass cls;
            	if (treesTabbedPane.getSelectedIndex() == 0) {
            		cls = packageTree.getSelectedClass();
            	} else {
            		cls = classTree.getSelectedClass();
            	}
                if (cls == null) {
                	return;
                }
                if (cls.isVirtual()) {
                	return;
                }
                if (cls.id == 99) {
            		int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, "Открытие списка всех объектов может занять продолжительное время. Вы уверены?");
                    if (ButtonsFactory.BUTTON_YES != res) {
                    	return;
                    }
                }
                ObjectBrowser ob = new ObjectBrowser(cls, false);
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont, "Объекты класса [" + cls.name + "]", ob);
                    dlg.show();
                } else {
                    if (!isOpaque) {
                        ob.setOpaque(true);
                        ob.setGradient(MainFrame.GRADIENT_MAIN_FRAME.isEmpty() ? kz.tamur.comps.Constants.GLOBAL_DEF_GRADIENT : MainFrame.GRADIENT_MAIN_FRAME);
                    }
                    JFrame frame = new JFrame("Объекты класса [" + cls.name + "]");
                    frame.getContentPane().add(ob);
                    frame.pack();
                    frame.setIconImage(Or3Frame.instance().getIconImage());
                    frame.setLocationRelativeTo(null);
                    frame.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(frame.getWidth(), frame.getHeight()));
                    frame.show();
                }
            } else if (item == searchObjectsItem) {
            	KrnClass cls;
            	if (treesTabbedPane.getSelectedIndex() == 0) {
            		cls = packageTree.getSelectedClass();
            	} else {
            		cls = classTree.getSelectedClass();
            	}
            	if (cls == null) {
            		return;
            	}
                if (cls.isVirtual()) {
                	return;
                }
                ObjectBrowser ob = new ObjectBrowser(cls, true);
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont, "Поиск объектов класса [" + cls.name + "]", ob);
                    dlg.show();
                } else {
                    if (!isOpaque) {
                        ob.setOpaque(true);
                        ob.setGradient(MainFrame.GRADIENT_MAIN_FRAME.isEmpty() ? kz.tamur.comps.Constants.GLOBAL_DEF_GRADIENT : MainFrame.GRADIENT_MAIN_FRAME);
                    }
                    JFrame frame = new JFrame("Поиск объектов класса [" + cls.name + "]");
                    frame.getContentPane().add(ob);
                    frame.pack();
                    frame.setIconImage(Or3Frame.instance().getIconImage());
                    frame.setLocationRelativeTo(null);
                    frame.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(frame.getWidth(), frame.getHeight()));
                    frame.show();
                }
            } else if (item == viewTriggersBtn) {
            	TriggersPanel triggersPanel = new TriggersPanel(this);
            	if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont, "Триггеры", triggersPanel);
                    dlg.show();
                } else {
                	if (!isOpaque) {
                		triggersPanel.setOpaque(true);
                		triggersPanel.setGradient(MainFrame.GRADIENT_MAIN_FRAME.isEmpty() ? kz.tamur.comps.Constants.GLOBAL_DEF_GRADIENT : MainFrame.GRADIENT_MAIN_FRAME);
                    }
                    JFrame frame = new JFrame("Триггеры");
                    frame.getContentPane().add(triggersPanel);
                    frame.pack();
                    frame.setIconImage(Or3Frame.instance().getIconImage());
                    frame.setLocationRelativeTo(null);
                    frame.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(frame.getWidth(), frame.getHeight()));
                    frame.show();
                }
            } else if (item == viewHistoryBtn) {
            	KrnClass cls=classTree.getSelectedClass();
            	String uid=cls.uid;
            	Object obj=attrTree.getSelectedObject();
            	if(obj!=null && obj instanceof KrnAttribute)
                	uid=((KrnAttribute)obj).uid;
            	else if(obj!=null && obj instanceof KrnMethod)
                	uid=((KrnMethod)obj).uid;
            	List<KrnVcsChange> changes=krn.getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, uid);
            	KrnVcsChange change=null;
            	if(changes.size()>0) {
            		change=changes.get(0);
            	}
        		Or3Frame.historysPanel.refreshTable(change, true);
                dlg = new DesignerDialog((Window) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                dlg.setMinimumSize(new Dimension(900, 70));
                dlg.show();
            } else if (item == methodBtn) {
            	Object obj=attrTree.getSelectedObject();
            	if(obj!=null && obj instanceof KrnMethod)
            	methodtSplitPane.populateInputPanel((KrnMethod)obj);
            } else if (item == classReportAttrsItem || item == quickRepBtn) {
                KrnClass cls = classTree.getSelectedClass();
                ReportAttrsPanel cp = new ReportAttrsPanel(cls.id);
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog) cont,
                            "Атрибуты быстрых отчетов для класса [" + cls.name + "]", cp);
                } else {
                    dlg = new DesignerDialog((Frame) cont,
                            "Атрибуты быстрых отчетов для класса [" + cls.name + "]", cp);
                }
                dlg.show();
            } else if (item == clsRefreshBtn) {
                ClassNode cls = krn.getClassNodeByName("Объект");
                setRoot(cls);
                classTree.revalidate();
                classTree.repaint();
            } else if (item == clsFindBtn || item == classFindItem) {
                classTree.find(attrTree);
            } else if (item == classCopyNameItem) {
                String str = classTree.getNodeName();
                ClientUtils.setClipboard(str);
            } else if (item == classCopyIDItem) {
                String str = "" + classTree.getNodeID();
                ClientUtils.setClipboard(str);
            } else if (item == classCopyUIDItem) {
                String str = classTree.getNodeUID();
                ClientUtils.setClipboard(str);
            } else if (item == exportClassHierarchy) {
            	exportClassHierarchy();
            /*} else if (item == renameBtn) {
                int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                        MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите переименовать все таблицы и колонки базы данных?"
                        		+ " Операция требует длительного времени и не может быть остановлена или возвращена назад.");
                if (res == ButtonsFactory.BUTTON_YES) {
        			System.out.println("Операция начата");
                	renameBegin(classTree.getNodeID());
        			System.out.println("Операция завершена");
                }*/
	        } else if (item == recycleBtn) {
	        	openRecycle();
	        } else if (item == clsExportMenu) {
	        	exportClass(classTree.getSelectedClass());
	        } else if (item == clsExportMenu) {
	        	importClassTo(classTree.getSelectedClass());
	        }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }
    
	private void openRecycle() {
        RecyclePanel recyclePanel = new RecyclePanel();
        DesignerDialog dlg = GuiUtil.createDesignerDialog(getTopLevelAncestor(), "Корзина", recyclePanel);
        dlg.setCancelVisible(false);
        dlg.setOkText("Закрыть");
        dlg.setResizable(false);
        dlg.show();
    }
    
    private int mthLock(String name, UserSessionValue us) {
        StringBuilder mess = new StringBuilder();
        mess.append("Метод '").append(name).append("' заблокирован!\nПользователь: ").append(us.name).append("\nIP адрес: ")
                .append(us.ip).append("\nИмя компьютера: ").append(us.pcName).append("\nОткрыть метод в режиме просмотра?");
        return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                mess.toString(), 235, 130);
    }

	private int mthVcsLock(String name, UserSessionValue us) {
		StringBuilder mess = new StringBuilder();
		mess.append("Метод '").append(name).append("' редактируется!\nПользователь: ").append(us.name).append("\nОткрыть метод в режиме просмотра?");
		return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess.toString(), 235, 130);
	}
	
	private int triggerVcsLock(Object triggerOwner, int ownerType, int triggerType, UserSessionValue us) {
		StringBuilder mess = new StringBuilder();
		mess.append("Триггер '" + Utils.getTriggerNameByTriggerType(triggerType, 1) + (ownerType == 0 ? "' класса '" : "' атрибута '"))
				.append(ownerType == 0 ? ((KrnClass) triggerOwner).name : ((KrnAttribute) triggerOwner).name)
				.append("' редактируется!\nПользователь: ").append(us.name).append("\nОткрыть триггер в режиме просмотра?");
		return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess.toString(), 235, 130);
	}
	
    private void renameBegin(long classId) {
    	Kernel krn = Kernel.instance();
    	try {
			if (classId > 99) {
				ClassNode cls = krn.getClassNode(classId);
		        String newname = Funcs.generateName(cls.getName());
				System.out.println(cls.getName() + " ... processing");
				
				if (cls.getKrnClass().tname == null || cls.getKrnClass().tname.length() == 0) {
					newname = cls.renameClassTable(cls.getKrnClass(), newname);
					System.out.println("Class " + cls.getKrnClass().id + " complete result: " + newname);
				}
				for(KrnAttribute t_attr : cls.getAttributes()) {
					if(t_attr.id == 1 || t_attr.id == 2 || t_attr.classId != cls.getId() || t_attr.rAttrId != 0 || (t_attr.tname != null && t_attr.tname.length() > 0))
						continue;
					
			        String newname1 = Funcs.generateName(t_attr.name);
			        newname1 = cls.renameAttrTable(t_attr, newname1);
					System.out.println("Attr " + t_attr.id + " complete result: " + newname1);
				}
			}
			KrnClass[] clss = krn.getClasses(classId);
			for (KrnClass cls : clss) {
				renameBegin(cls.id);
			}
    	} catch (KrnException e) {
			e.printStackTrace();
		}
    }
    
    private void exportClassHierarchy() {
    	ClassNode node = (ClassNode) classTree.getModel().getRoot();
    	StringBuilder builder = new StringBuilder();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:/ClassHierarchy.txt"));
			writer.write(getChilds(node, builder).toString());
			writer.close();
		} catch (IOException e) {}
    }
    
    private StringBuilder getChilds(ClassNode node, StringBuilder builder) {
    	for (int j = 0; j < node.getLevel(); j++) {
			builder.append("\t");
		}
    	builder.append(node.getLevel() + " класс: " + node.getName() + "\n");
    	List<KrnAttribute> attributes = node.getAttributes();
    	Collections.sort(attributes, new Comparator<KrnAttribute>() {
    		  public int compare(KrnAttribute attribute_1, KrnAttribute attribute_2) {
    			  return attribute_1.name.compareTo(attribute_2.name);
    		  }
    	});
    	for (int j = 0; j < attributes.size(); j++) {
    		for (int k = 0; k < node.getLevel() + 1; k++) {
    			builder.append("\t");
    		}
        	try {
				builder.append("- Аттрибут: " + attributes.get(j).name + ", Тип: " + Kernel.instance().getClass(attributes.get(j).typeClassId).getName() + "\n");
			} catch (KrnException e) {
				e.printStackTrace();
			}
    	}
    	if (node.getChildCount() > 0) {
    		for (int i = 0; i < node.getChildCount(); i++) {
    			getChilds((ClassNode) node.getChildAt(i), builder);
    		}
		}
    	return builder;
    }
    
    //Показать панель свойств класса
    private String checkDestination(KrnClass cls, KrnClass base) {
    	String result = new String();    	
    	if(cls.id == base.id) {
    		result = "Нельзя переместить класс в самого себя!";
    	}
    	
    	try {
			List<KrnClass> subClss = Kernel.instance().getClasses(cls.id, true);
			for(KrnClass tempCls: subClss) {
				if(base.id == tempCls.id) {
					result = "Нельзя переместить класс в его подкласс!";
				}
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
    	return result;
    }
    private List<String> checkDublAttrs(KrnClass cls, KrnClass base) {
    	List<String> result = new ArrayList<String>();
    	try {    		 
    		KrnClass ownerCls = Kernel.instance().getClassById(cls.parentId);
    		List<KrnAttribute> baseAttrs = Kernel.instance().getAttributes(base);
    		List<KrnAttribute> clsAttrs = Kernel.instance().getAttributes(cls);
    		List<KrnAttribute> ownClsAttrs = Kernel.instance().getAttributes(ownerCls);
    		for(int i=clsAttrs.size()-1;i>=0;i--) {
    			if(ownClsAttrs.contains(clsAttrs.get(i))) {
    				clsAttrs.remove(i);
    			}
    		}
    		for(KrnAttribute attr: clsAttrs) {
    			for(KrnAttribute baseAttr:baseAttrs) {
    				if(baseAttr.name.trim().equals(attr.name.trim()))
    				result.add("Класс " + base.name + " и Класс " + cls.name +" имеют аттрибут с одинаковыми названиями: " + attr.name);
    			}
    		}
    		List<KrnClass> subClss = Kernel.instance().getClasses(cls.id, false);
    		for(KrnClass tempCls: subClss) {
    			result.addAll(checkDublAttrs(tempCls, base));
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
		return result;
    }
    
    private void showClassProperties() throws KrnException {
        final Kernel krn = Kernel.instance();
        final Container cont = this.getTopLevelAncestor();
        final DesignerDialog dlg;

        final KrnClass cls;
    	if (treesTabbedPane.getSelectedIndex() == 0) {
    		cls = packageTree.getSelectedClass();
    	} else {
    		cls = classTree.getSelectedClass();
    	}
        if (cls == null) {
            return;
        }
        if (kz.tamur.comps.Constants.IS_DEBUG) {
            System.out.println("Quering class: id=" + cls.id + " name='" + cls.name + "' " + "tname: '" + cls.tname + "'");
        }
        if (cls.id == 99) {
        	dlg = null;
            MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "Свойства класса 'Объект' не доступны");
            return;
        }
        KrnClass base = krn.getClass(cls.parentId);
        final String comment = krn.getClassComment(cls.id);
        final kz.tamur.admin.ClassPropPanel cp = new kz.tamur.admin.ClassPropPanel(cls, base, comment, cls.isVirtual());
//        String tableName = cp.tableName;
        cp.setLastSelectedClass(lastBaseClassNode);
        if (cont instanceof Dialog) {
            dlg = new DesignerDialog((Dialog) cont, "Свойства класса" + (canEdit ? "" : " [Режим просмотра]"), cp);
        } else {
            dlg = new DesignerDialog((Frame) cont, "Свойства класса" + (canEdit ? "" : " [Режим просмотра]"), cp);
        }
        cp.setparentDesignerDialog(dlg);
        dlg.setOkVisible(canEdit);
        dlg.setOkEnabled(false);
        dlg.setOkButtonActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClassNode node;
				boolean isOK = true;
				try {
					node = krn.getClassNode(cls.id);

		            String newName = cp.getClassName();
		            KrnClass base = cp.getBaseClass();
		            if (!newName.equals(cls.name) || base.id != cls.parentId || cls.isRepl != cp.isRepl()) {
		            	String dest = checkDestination(cls, base);
		            	if(dest!=null && dest.length()>0) {
		            		JOptionPane.showMessageDialog(dlg,
		            			    dest,
		            			    "Ошибка",
		            			    JOptionPane.ERROR_MESSAGE);
		            		return;
		            	}
		            	List<String> dublAttrs = checkDublAttrs(cls,base);
		            	if(dublAttrs.size()>0) {
		            		String warning = "";
		            		for(String str: dublAttrs) {
		            			warning += str + "\n";
		            		}
		            		warning += "\n" + "Принимайте меры пожалуйста!";
		            		JOptionPane.showMessageDialog(dlg,
		            			    warning,
		            			    "Ошибка",
		            			    JOptionPane.ERROR_MESSAGE);
		            		return;
		            	}
		                krn.change(node, base, newName, cp.isRepl());
		                node = krn.getClassNode(cls.id);
		            }
		            lastBaseClassNode = cp.getLastSelectedClass();
//		            int res = MessagesFactory.showMessageDialog(dlg,
//	                        MessagesFactory.QUESTION_MESSAGE,
//	                        "Установить флаг для всех дочерних классов?");
//		            if (res == ButtonsFactory.BUTTON_YES) {
//		                setReplFlagAllChildren(node, cp.isRepl());
//		            }
		            // Описание класса
		            if (!comment.equals(cp.getComment())) {
		                krn.setClassComment(cls.uid, cp.getComment());
		            }
		            
		            if (cp.getTableName() != null) { //TODO r tname
		            	if (
		            			(cls.tname != null 
		            			&& cls.tname.length() != 0 
		            			&& !cp.getTableName().equalsIgnoreCase(cls.tname)
		            			)
		            			|| ( (cls.tname == null || cls.tname.length() == 0)
				            	&& !cp.getTableName().equalsIgnoreCase("ct" + cls.id)
		            			)
		            	){
			            	if (node.renameClassTable(cls, cp.getTableName()) == null) {
			            		cp.setNotTableName(cp.getTableName());
			            		isOK = false;
			            	}
		            	}
		            	//krn.renameClassTable(cls, cp.getTableName());
		            }
		            
				} catch (Exception e1) {
//					isOK = false;
//					int ia = e1.getMessage().lastIndexOf("Duplicate entry");
//	        		int ib = e1.getMessage().lastIndexOf("for key");
//	        		if (ia != -1 && ib != -1 && ia < ib) {
//	            		String column = e1.getMessage().substring(ib + 9, e1.getMessage().length() - 1);
//	            		if (column.equalsIgnoreCase("c_name") || column.equalsIgnoreCase("c_name_UNIQUE")) {
//	            			cp.setNotClassName(cp.getClassName());
//	            		}// else if (column.equals("c_tname") || column.equals("c_tname_UNIQUE")) {
//	            		//	cp.setNotTableName(cp.getTableName());
//	            		//}
//	            		
//	        		} else if (e1.getMessage().equalsIgnoreCase("C_NAME")) {
//	        			cp.setNotClassName(cp.getClassName());
//	        		} else {
//	        			cp.setErrorMessange("Неудалось выполнить операцию.");
//	        			//e1.printStackTrace();
//	        		}
//	            	//select * from dba_ind_columns where index_name='?';
					
					
//					isOK = false;
//					int ia = e1.getMessage().lastIndexOf("Duplicate entry");
//	        		int ib = e1.getMessage().lastIndexOf("for key");
//	        		if (ia != -1 && ib != -1 && ia < ib) {
//	            		String column = e1.getMessage().substring(ib + 9, e1.getMessage().length() - 1);
//	            		if (column.equals("c_name") || column.equals("c_name_UNIQUE")) {
//	            			cp.setNotClassName(cp.getClassName());
//	            		} else if (column.equals("c_tname") || column.equals("c_tname_UNIQUE")) {
//	            			cp.setNotTableName(cp.getTableName());
//	            		}
//	        		} else 
	        		if (e1.getMessage() != null && e1.getMessage().equalsIgnoreCase("C_NAME")) {
	        			cp.setNotClassName(cp.getClassName());
	        		} else if (e1.getMessage() != null && e1.getMessage().equalsIgnoreCase("C_TNAME")) {
	        			cp.setNotTableName(cp.getTableName());
	        		} else {
	        			cp.setErrorMessange("Неудалось выполнить операцию.");
	        		}
	        		
				}
				if (isOK){
					dlg.dispose();
				}
			}
		});
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            //
        }

    }

    //Показать панель свойств атрибутов
    public void showAttrtreeNodeProperties() throws KrnException {
        showAttrProperties();
    }

    /** Показать панель свойств атрибутов
     * */
    private void showAttrProperties() throws KrnException {
        int[] rows = attrTree.getSelectionRows();
        final Kernel krn = Kernel.instance();
        DesignerDialog dlg = null;
        Object ob = attrTree.getSelectedObject();
        if (ob == null) {
            return;
        }

        if (ob instanceof KrnAttribute) {
            KrnAttribute attr = (KrnAttribute) ob;
            // Если выбран корень дерева атрибутов, то не показываем свойства класса
            if (attr.id == 0 || attr.uid == null) {
                return;
            }

            if (kz.tamur.comps.Constants.IS_DEBUG) {
                System.out.println("Quering attribute: " + attr.id + " ' " + attr.name + " ' " + attr.tname);
            }
            if (attr.rAttrId == 0) {
                changeAttribute(attr);
            } else {
                changeRevAttribute(attr);
            }
        } else {
            KrnMethod method = (KrnMethod) attrTree.getSelectedObject();
//            if (krn.getBindingModuleToUserMode()) {
//            	if (method.ownerId > 0) {
//            		long currentUserId = krn.getUserSession().userObj.id;
//            		if (method.ownerId != currentUserId) {
//            			KrnObject userObj = krn.getObjectById(method.ownerId, 0);
//            			if (userObj != null) {	// Владелец метода существует
//                			KrnClass userCls = krn.getClassByName("User");
//                			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
//                			String userName = krn.getStringsSingular(method.ownerId, userNameAttr.id, 0, false, false);
//                            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного метода является пользователь " + userName + "!");
//            			}
//            		}
//            	}
//            }
            String comment = krn.getMethodComment(method.uid);
            boolean readOnly=false;
            try {
            	UserSessionValue us = krn.vcsLockModel(method.uid, ModelChange.ENTITY_TYPE_METHOD);
                if (us != null) {
                    if (mthVcsLock(method.name, us) == BUTTON_YES) {
                        readOnly = true;
                    } else {
                        return;
                    }
                }
                if(!readOnly){
                    us = krn.blockMethod(method.uid);
                    if (us != null && !us.id.equals(krn.getUUID())) {
                        if (mthLock(method.name, us) == BUTTON_YES) {
                            readOnly = true;
                        } else
                            return;
                    }
                }
            } catch (KrnException exc) {
                exc.printStackTrace();
            }

            MethodPanel mp = new MethodPanel(method, comment,readOnly);
            Container cnt = getTopLevelAncestor();
            dlg = new DesignerDialog((Window) cnt, "Свойства метода" + (canEditMethod ? "" : " [Режим просмотра]"), mp);
            dlg.setOkVisible(canEditMethod);
            dlg.setOkEnabled(false);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                if ("".equals(mp.getMethodName())) {
                    MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Отсутствуют необходимые параметры\nдля создания метода!");
                } else {
	                KrnMethod par = (KrnMethod) attrTree.getSelectedObject();
	                final KrnClass cls = Kernel.instance().getClass(par.classId);
	                ClassNode cNode = krn.getClassNode(cls.id);
	                try {
	                	cNode.changeMethod(method.uid, mp.getMethodName(), mp.isClassMethod(), mp.getExpression());
	                } catch (Exception e) {
		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
	                }
	                if (!comment.equals(mp.getComment())) {
	                    krn.setMethodComment(method.uid, mp.getComment());
	                }
                }
            }
			if(!readOnly){
				try {
					Kernel.instance().unlockMethod(method.uid);
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
			}
        }
        if (rows.length > 0) {
            attrTree.setSelectionRow(rows[0]);
        }
    }
    
    private void showTriggerProperties() throws KrnException {
    	int[] rows = attrTree.getSelectionRows();
        final Kernel krn = Kernel.instance();
        Object ob = attrTree.getSelectedObject();
        if (ob == null) {
            return;
        }
        
        if (ob instanceof KrnAttribute) {
            KrnAttribute attr = (KrnAttribute) ob;
            // Если выбран корень дерева атрибутов, то не показываем свойства класса
            if (attr.id == 0 || attr.uid == null) {
                return;
            }
            
            KrnClass cls = Kernel.instance().getClass(attr.classId);
            KrnClass type = Kernel.instance().getClass(attr.typeClassId);
            String comment = krn.getAttributeComment(attr.id);
            TriggerPropPanel ap = new TriggerPropPanel(attr);
            DesignerDialog dlg = GuiUtil.createDesignerDialog(getTopLevelAncestor(), "Триггеры СУБД", ap);
            dlg.setCancelVisible(false);
            dlg.setOkText("Закрыть");
            dlg.setResizable(false);
            dlg.show();
            return;
        }
    }

    private void deleteAttribute(KrnAttribute attr) throws KrnException {
        try {
            List<KrnAttribute> sattrs=Kernel.instance().getDependAttrs(attr);
            if(sattrs!=null && sattrs.size()>0){
            	String msg="";
            	for(KrnAttribute sattr:sattrs){
            		KrnClass dcls=Kernel.instance().getClass(sattr.classId);
            		if(!"".equals(msg))
                		msg+=";\n";
            		msg+="'"+sattr.name+"',класс-'"+dcls.name+"'";
            	}
        		msg+="\n для сортировки!";
                Container cnt = getTopLevelAncestor();
                MessagesFactory.showMessageDialog(cnt, MessagesFactory.INFORMATION_MESSAGE,
                        "Удаление невозможно!\n"
                        + "Удаляемый атрибут используется в атрибутах:\n"+msg);
                return;
            }
        } catch (KrnException e) {}
        int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "При удалении атрибута \"" + attr.name + "\" все текущие значения будут потеряны.\n Вы уверены что хотите продолжить операцию?");
        if (res == ButtonsFactory.BUTTON_YES) {
            CursorToolkit.startWaitCursor(getTopLevelAncestor());
            Kernel krn = Kernel.instance();
            List<Long> filtersIds = krn.getFiltersContainingAttr(attr);
            if (filtersIds.size() > 0) {     	
            	FiltersDialogPanel fp = new FiltersDialogPanel(filtersIds, attr.name);
                Container cont = getTopLevelAncestor();
                DesignerDialog dlg1 = new DesignerDialog(cont instanceof Dialog ?  (Dialog) cont : (Frame) cont, "Удаление атрибута в фильтрах", fp);
                dlg1.setOkText("Продолжить");
                dlg1.setResizable(false);
                dlg1.show();
	            if (dlg1.getResult() == ButtonsFactory.BUTTON_OK) {
	            	showResaveAttrInFilters(krn.getClassById(attr.classId), attr, filtersIds, 2, null);  
	            } else {
	            	return;
	            }
            }
			try {
				Kernel.instance().deleteAttribute(attr);
            } catch (KrnException e) {
	            Container cnt = getTopLevelAncestor();
	            MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя удалить атрибут! " + e.getMessage());
            } finally {
                CursorToolkit.stopWaitCursor(getTopLevelAncestor());
            }
        }
    }

    private void deleteMethod(KrnMethod m) throws KrnException {
        int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Удалить метод - " + m.name + "?");
        if (res == ButtonsFactory.BUTTON_YES) {
            final KrnClass cls = Kernel.instance().getClass(m.classId);
            ClassNode cNode = Kernel.instance().getClassNode(cls.id);
            cNode.deleteMethod(m);
        }
    }
    
    private void changeAttributeShow(DesignerDialog dlg, Kernel krn, KrnClass type, String comment, KrnClass cls, AttrPropPanel ap, KrnAttribute attr)  throws KrnException {
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            String emptyMess = ap.getEmptyMessage();
            if (emptyMess.length() > 0) {
                MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, emptyMess);
                changeAttributeShow(dlg, krn, type, comment, cls, ap, attr);
                return;
            }
            String name = ap.getAttrName();
            String tname = ap.getAttrTname();
            type = ap.getType();
            cls = ap.getAttrClass();            
            int collType = ap.collType();
            boolean isUnique = ap.isUnique();
            boolean isMultiLang = ap.isMultiLang();
            boolean isIndexed = ap.isIndexed();
            boolean isRepl = ap.isRepl();
            boolean isEncrypt = ap.isEncrypt();
            int attrSize = ap.getAttrSize();
            long flag = ap.getFlags();
            int accessModifier = ap.getAccessModifierType();
            if (attr.classId != cls.id) {
                String msg =
                        "При переназначении атрибута все текущие значения "
                        + "будут потеряны.\n"
                        + "Вы уверены что хотите продолжить операцию?";
                int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(),
                        MessagesFactory.CONFIRM_MESSAGE, msg);
                if (ButtonsFactory.BUTTON_YES == res) {
                    KrnAttribute nattr = krn.createAttribute(cls, type, name, collType, isUnique, isIndexed, isMultiLang, isRepl,
                            attrSize, flag, 0, 0, false, tname, accessModifier, isEncrypt);
                    krn.deleteAttribute(attr);
                    attr = nattr;
                }
            } else if (attr.typeClassId != type.id) {
                String msg =
                        "При переназначении типа атрибута все текущие значения "
                        + "могут быть потеряны.\n"
                        + "Вы уверены что хотите продолжить операцию?";
                int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(),
                        MessagesFactory.CONFIRM_MESSAGE, msg);
                if (ButtonsFactory.BUTTON_YES == res) {
                	try {
                		KrnAttribute nattr = krn.changeAttribute(attr, type, name, collType,
	                            isUnique, isIndexed, isMultiLang, isRepl, attrSize, flag, 0, 0, false, tname, accessModifier, isEncrypt);
                		attr = nattr;
                		updateAttrTree(krn.getClassNode(attr.classId));
	                } catch (Exception e_atr){
	                	String mesg = "Изменение атрибута с данными параметрами невозможно.";
	            		if (e_atr.getMessage().equals("TNAME")){
	            			mesg = "Параметр 'имя таблици' = '" + tname + "' недопустим.";
	            		}
	            		if (e_atr.getMessage().equals("NAME")){
	            			mesg = "Параметр 'наименование' = '" + name + "' недопустим.";
	            		}
	            		MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, mesg);
	            		changeAttributeShow(dlg, krn, type, comment, cls, ap, attr);
	            		return;
	            	}
                }
            } else if (!name.equals(attr.name)
                    || collType != attr.collectionType
                    || isUnique != attr.isUnique
                    || isMultiLang != attr.isMultilingual
                    || isIndexed != attr.isIndexed
                    || isRepl != attr.isRepl
                    || attrSize != attr.size
                    || flag != attr.flags
                    || ((tname != null && attr.tname == null) || (tname != null && !tname.equals(attr.tname)))
                    || accessModifier != attr.accessModifierType
                    || isEncrypt != attr.isEncrypt
                    ) {
            	try {
            		if (!name.equals(attr.name)/* || ((tname != null && attr.tname == null) || (tname != null && !tname.equals(attr.tname)))*/) {
                        List<Long> filtersIds = krn.getFiltersContainingAttr(attr);
                        if (filtersIds.size() > 0) {     	
                        	FiltersDialogPanel fp = new FiltersDialogPanel(filtersIds, attr.name);
                            Container cont = getTopLevelAncestor();
                            DesignerDialog dlg1 = new DesignerDialog(cont instanceof Dialog ?  (Dialog) cont : (Frame) cont, "Изменение атрибута в фильтрах", fp);
                            dlg1.setOkText("Продолжить");
                            dlg1.setResizable(false);
                            dlg1.show();
            	            if (dlg1.getResult() == ButtonsFactory.BUTTON_OK) {
            	            	showResaveAttrInFilters(krn.getClassById(attr.classId), attr, filtersIds, 1, name);  
            	            } else {
            	            	return;
            	            }
                        }             			
            		}
            		KrnAttribute nattr = krn.changeAttribute(attr, type, name, collType, isUnique, isIndexed, isMultiLang, isRepl, attrSize, flag, 0, 0, false, tname, accessModifier, isEncrypt);
            		attr = nattr;
            		updateAttrTree(krn.getClassNode(attr.classId));
            	} catch (Exception e_atr){
                	String mesg = "Изменение атрибута с данными параметрами невозможно.";
                	if (e_atr.getMessage() != null && e_atr.getMessage().trim().length() != 0) {
	            		if (e_atr.getMessage().equals("TNAME")){
	            			mesg = "Параметр 'имя таблици' = '" + tname + "' недопустим.";
	            		}
	            		if (e_atr.getMessage().equals("NAME")){
	            			mesg = "Параметр 'наименование' = '" + name + "' недопустим.";
	            		}
            		}
            		MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, mesg);
            		changeAttributeShow(dlg, krn, type, comment, cls, ap, attr);
            		return;
            	}
            }
            if (!comment.equals(ap.getComment())) {
                krn.setAttributeComment(attr.uid, ap.getComment());
            }
            lastTypeNode = ap.getLastSelectedClass();
        }
    }
    
	private void showResaveAttrInFilters(KrnClass cls, KrnAttribute attr, List<Long> filtersIds, int mode, String newName) {
		bottomPB = new JProgressBar();
		bottomPB.setIndeterminate(true);
		DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Пересохранение фильтров", bottomPB);
		dlg.hideOkButton();
		dlg.setCancelVisible(false);
		class MyRunnable implements Runnable {
			private Kernel krn = Kernel.instance();
			private KrnClass cls;
			private KrnAttribute attr;
			private DesignerDialog dlg;
			private List<Long> filtersIds;
			private int mode;
			private String newName;

			public MyRunnable(KrnClass cls, KrnAttribute attr, DesignerDialog dlg, List<Long> filtersIds, int mode, String newName) {
				this.cls = cls;
				this.attr = attr;
				this.dlg = dlg;
				this.filtersIds = filtersIds;
				this.mode = mode;
				this.newName = newName;
			}

			public void run() {
				for (int i = 0; i < filtersIds.size(); i++) {
					try {
						KrnObject filterObj = krn.getObjectById(filtersIds.get(i), 0);
						byte[] data = krn.getBlob(filterObj, "config", 0, 0, 0);
						if (data.length > 0) {
							SAXBuilder saxBuilder = new SAXBuilder();
							ByteArrayInputStream is = new ByteArrayInputStream(data);
							Element xml = saxBuilder.build(is).getRootElement();
							is.close();
							if (mode == 1) {
								findAttrInList(xml.getChildren(), cls.name + "." + attr.name, cls.name + "." + newName, mode);
							} else if (mode == 2) {
								findAttrInList(xml.getChildren(), cls.name + "." + attr.name, null, mode);
							}
							XMLOutputter xmlOutputter = new XMLOutputter();
							StringWriter elemStrWriter = new StringWriter();
							xmlOutputter.output(xml, elemStrWriter);
							byte[] xmlBytes = elemStrWriter.toString().getBytes(Charset.defaultCharset());
							krn.setBlob(filterObj.id, filterObj.getClassId(), "config", 0, xmlBytes, 0, 0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				dlg.dispose();
			}
		}
		Thread t = new Thread(new MyRunnable(cls, attr, dlg, filtersIds, mode, newName));
		t.start();
		dlg.setVisible(true);
	}	
	
	private boolean findAttrInList(List<Element> elements, String name, String newName, int mode) {		
		Iterator iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			String ss = element.getText();
			if ((element.getName() == "attrFlr") && (name.equals(element.getText()))) {
				if (mode == 1) {
					element.setText(newName);
				} else if (mode == 2) {
					return true;
				}
			} else if (element.getName() == "children") {
				List<Element> components = element.getChildren("Component");
				if (!(components.isEmpty())) {
					Iterator iterator1 = components.iterator();
					while (iterator1.hasNext()) {
						Element component = (Element) iterator1.next();
						List<Element> subElements = component.getChildren();
						if (findAttrInList(subElements, name, newName, mode)) {
							iterator1.remove();
						}
					}
				}
			}
		}
		return false;
	}	
	
    private void changeAttribute(KrnAttribute attr) throws KrnException {
        Kernel krn = Kernel.instance();
        KrnClass cls = Kernel.instance().getClass(attr.classId);
        KrnClass type = Kernel.instance().getClass(attr.typeClassId);
        String comment = krn.getAttributeComment(attr.id);
        AttrPropPanel ap = new AttrPropPanel(cls, attr, type, comment);
        DesignerDialog dlg = GuiUtil.createDesignerDialog(getTopLevelAncestor(), "Свойства атрибута" + (canEditAttr ? "" : " [Режим просмотра]"), ap);
        ap.setLastSelectedClass(lastTypeNode);
        dlg.setOkVisible(canEditAttr);
        dlg.setOkEnabled(false);  
        changeAttributeShow(dlg, krn, type, comment, cls, ap, attr);
        return;
    }

    private void changeRevAttribute(KrnAttribute attr) throws KrnException {
        Kernel krn = Kernel.instance();
        ClassNode cls = Kernel.instance().getClassNode(attr.classId);
        String comment = krn.getAttributeComment(attr.id);
        RevAttrPanel ap = new RevAttrPanel(cls, attr, lastTypeNode, comment);
        DesignerDialog dlg = GuiUtil.createDesignerDialog(getTopLevelAncestor(), "Свойства обратного атрибута" + (canEditAttr ? "" : " [Режим просмотра]"), ap);
        dlg.setOkVisible(canEditAttr);
        dlg.setOkEnabled(false);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {

            String emptyMess = ap.getErrorMessage();
            if (emptyMess.length() > 0) {
                MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, emptyMess);
                return;
            }

            String name = ap.getName();
            ClassNode type = ap.getType();
            KrnAttribute rattr = ap.getRevAttribute();
            long rattrId = (rattr != null) ? rattr.id : 0;
            KrnAttribute sattr = ap.getSortAttribute();
            long sattrId = (sattr != null) ? sattr.id : 0;
            boolean sDesc = ap.isSortDesc();
            int colType = ap.getColType();
            boolean aggregate = ap.isAggregate();

            if (!name.equals(attr.name)
                    || rattrId != attr.rAttrId
                    || sattrId != attr.sAttrId
                    || sDesc != attr.sDesc
                    || colType != attr.collectionType
                    || aggregate != ((attr.flags & 0x04) > 0)) {
                long flags = attr.flags;
                if (aggregate != (attr.flags & KrnAttribute.AGGREGATE) > 0) {
                    flags ^= KrnAttribute.AGGREGATE;
                }
                krn.changeAttribute(attr, type.getKrnClass(), name,
                        colType, false, false, false, false,
                        0, flags, rattrId, sattrId, sDesc, null, 0);
            }
            if (!comment.equals(ap.getComment())) {
                krn.setAttributeComment(attr.uid, ap.getComment());
            }
            lastTypeNode = ap.getLastSelectedClass();
        }
        return;
    }

    void classTree_valueChanged(TreeSelectionEvent e) {
        ClassNode cls = classTree.getSelectedNode();
        if (cls != null) {
            assemlyInfoToStatusBar();
        	editTablePropItem.setEnabled(cls.getKrnClass().id > 99);//TODO: Tedit
            classPropItem.setEnabled(cls.getKrnClass().id > 99);
            clsEventsMenu.setEnabled(cls.getKrnClass().id > 99 && !cls.getKrnClass().isVirtual());
            classDeleteItem.setEnabled(canDelete);
            classTruncateItem.setEnabled(canDelete);
            classCreateItem.setEnabled(canCreate);
            classObjectsItem.setEnabled(true);
            searchObjectsItem.setEnabled(true);
            classReportAttrsItem.setEnabled(true);
            classCopyMenu.setEnabled(true);
            revAttrCreateItem.setEnabled(true);

            clsDelBtn.setEnabled(canDelete);
            clsCreateBtn.setEnabled(canCreate);
            clsPropBtn.setEnabled(true);
            viewHistoryBtn.setEnabled(true);
            clsObjBtn.setEnabled(true);
            truncateAction.setEnabled(canDelete);
            quickRepBtn.setEnabled(true);

            updateAttrTree(cls);
            KrnMethod searchMethod = classTree.getSearchMethod();
            if (searchMethod != null) {
                try {
                    attrTree.setSelectedPath(searchMethod);
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
        	editTablePropItem.setEnabled(false);//TODO: Tedit
            classPropItem.setEnabled(false);
            clsEventsMenu.setEnabled(false);
            classDeleteItem.setEnabled(false);
            classCreateItem.setEnabled(false);
            classTruncateItem.setEnabled(false);
            classObjectsItem.setEnabled(false);
            searchObjectsItem.setEnabled(false);
            classReportAttrsItem.setEnabled(false);
            classCopyMenu.setEnabled(false);
            revAttrCreateItem.setEnabled(false);

            clsDelBtn.setEnabled(false);
            clsCreateBtn.setEnabled(false);
            clsPropBtn.setEnabled(false);
            clsObjBtn.setEnabled(false);
            truncateAction.setEnabled(false);
            quickRepBtn.setEnabled(false);
            viewHistoryBtn.setEnabled(false);
       }
        attrPropItem.setEnabled(false);
        addTriggerItem.setEnabled(false);
        attrEventsMenu.setEnabled(false);
        editTablePropItemattr.setEnabled(false); //TODO: Tedit
        attrCopyMenu.setEnabled(false);
        attrCreateItem.setEnabled(false);
        deleteItem.setEnabled(false);
        aoUpdateRefs.setEnabled(false);
        aoDeleteUnused.setEnabled(false);

        attrPropBtn.setEnabled(false);
        attrCreateBtn.setEnabled(false);
        deleteButton.setEnabled(false);
        attrUpdateBtn.setEnabled(false);
        attrDelObjsBtn.setEnabled(false);
    }

    void packageTree_valueChanged(TreeSelectionEvent e) {
        PackageNode pn = packageTree.getSelectedNode();
        if (pn != null && pn.getKrnClass() != null) {
        	KrnClass cls = pn.getKrnClass();
            assemlyInfoToStatusBar();
        	editTablePropItem.setEnabled(cls.id > 99);
            classPropItem.setEnabled(cls.id > 99);
            clsEventsMenu.setEnabled(cls.id > 99 && !cls.isVirtual());
            classDeleteItem.setEnabled(canDelete);
            classTruncateItem.setEnabled(canDelete);
            classCreateItem.setEnabled(canCreate);
            classObjectsItem.setEnabled(true);
            searchObjectsItem.setEnabled(true);
            classReportAttrsItem.setEnabled(true);
            classCopyMenu.setEnabled(true);
            revAttrCreateItem.setEnabled(true);

            clsDelBtn.setEnabled(canDelete);
            clsCreateBtn.setEnabled(canCreate);
            clsPropBtn.setEnabled(true);
            clsObjBtn.setEnabled(true);
            truncateAction.setEnabled(canDelete);
            quickRepBtn.setEnabled(true);

            try {
				updateAttrTree(cls);
			} catch (KrnException e1) {
				e1.printStackTrace();
			}
//            KrnMethod searchMethod = classTree.getSearchMethod();
//            if (searchMethod != null) {
//                try {
//                    attrTree.setSelectedPath(searchMethod);
//                } catch (KrnException ex) {
//                    ex.printStackTrace();
//                }
//            }
        } else {
            attrTree.setModel(null);
        	editTablePropItem.setEnabled(false);
            classPropItem.setEnabled(false);
            clsEventsMenu.setEnabled(false);
            classDeleteItem.setEnabled(false);
            classCreateItem.setEnabled(false);
            classTruncateItem.setEnabled(false);
            classObjectsItem.setEnabled(false);
            searchObjectsItem.setEnabled(false);
            classReportAttrsItem.setEnabled(false);
            classCopyMenu.setEnabled(false);
            revAttrCreateItem.setEnabled(false);

            clsDelBtn.setEnabled(false);
            clsCreateBtn.setEnabled(false);
            clsPropBtn.setEnabled(false);
            clsObjBtn.setEnabled(false);
            truncateAction.setEnabled(false);
            quickRepBtn.setEnabled(false);
        }
        attrPropItem.setEnabled(false);
        addTriggerItem.setEnabled(false);
        attrEventsMenu.setEnabled(false);
        editTablePropItemattr.setEnabled(false);
        attrCopyMenu.setEnabled(false);
        attrCreateItem.setEnabled(false);
        deleteItem.setEnabled(false);
        aoUpdateRefs.setEnabled(false);
        aoDeleteUnused.setEnabled(false);

        attrPropBtn.setEnabled(false);
        attrCreateBtn.setEnabled(false);
        deleteButton.setEnabled(false);
        attrUpdateBtn.setEnabled(false);
        attrDelObjsBtn.setEnabled(false);
    }
    
    void attrTree_valueChanged(TreeSelectionEvent e) throws KrnException {
        Object o = attrTree.getSelectedObject();
        if (o != null) {
            Object ob = attrTree.getSelectionPath().getLastPathComponent();
            if (ob.equals(attrTree.getModel().getRoot())) {
                //Корень дерева атрибутов
                attrPropItem.setEnabled(false);
                addTriggerItem.setEnabled(false);
                attrEventsMenu.setEnabled(false);
                editTablePropItemattr.setEnabled(false); //TODO: Tedit
                attrCopyMenu.setEnabled(false);
                deleteItem.setEnabled(false);
                aoUpdateRefs.setEnabled(false);
                aoDeleteUnused.setEnabled(false);
                attrPropBtn.setEnabled(false);
                deleteButton.setEnabled(false);
                attrUpdateBtn.setEnabled(false);
                attrDelObjsBtn.setEnabled(false);
                KrnAttribute attr = (KrnAttribute) o;
                methodCreateBtn.setEnabled(attr.typeClassId >= 99 && canCreateMethod);
                createMethod.setEnabled(attr.typeClassId >= 99 && canCreateMethod);
                attrCreateBtn.setEnabled(attr.typeClassId > 99 && canCreateAttr);
                attrCreateItem.setEnabled(attr.typeClassId > 99 && canCreateAttr);
                revAttrCreateItem.setEnabled(attr.typeClassId > 99 && canCreateAttr);
                findRevAttrItem.setEnabled(false);
                if(methodBtn.isEnabled())  methodtSplitPane.clear();
                methodBtn.setEnabled(false);
           } else if (o instanceof KrnAttribute) {
                KrnAttribute attr = (KrnAttribute) o;
                AttrNode attrRoot = (AttrNode) attrTree.getModel().getRoot();
                long rootTypeId = attrRoot.getType().getId();
                KrnAttribute[] rattrs = new KrnAttribute[0];
                try {
                    rattrs = Kernel.instance().getRevAttributes(attr.id);
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
                aoUpdateRefs.setEnabled(rattrs.length == 1);
                aoDeleteUnused.setEnabled(rattrs.length == 1);
                attrUpdateBtn.setEnabled(rattrs.length == 1);
                attrDelObjsBtn.setEnabled(rattrs.length == 1);

                methodCreateBtn.setEnabled(attr.typeClassId >= 99 && canCreateMethod);
                createMethod.setEnabled(attr.typeClassId >= 99 && canCreateMethod);
                attrCreateBtn.setEnabled(attr.typeClassId > 99 && canCreateAttr);
                attrCreateItem.setEnabled(attr.typeClassId > 99 && canCreateAttr);
                revAttrCreateItem.setEnabled(attr.typeClassId > 99 && canCreateAttr);

                attrPropItem.setEnabled(rootTypeId > 99);
                addTriggerItem.setEnabled(rootTypeId > 99);
                attrEventsMenu.setEnabled(rootTypeId > 99);
                editTablePropItemattr.setEnabled(attr.collectionType != 0 && attr.rAttrId == 0); //TODO: Tedit
                attrCopyMenu.setEnabled(rootTypeId > 99);
                attrPropBtn.setEnabled(rootTypeId > 99);

                deleteItem.setEnabled(rootTypeId > 99 && canDeleteAttr);
                deleteButton.setEnabled(rootTypeId > 99 && canDeleteAttr);
                if(methodBtn.isEnabled())  methodtSplitPane.clear();
                methodBtn.setEnabled(false);
                
                findRevAttrItem.setEnabled(attr.rAttrId == 0);
            } else {
                aoUpdateRefs.setEnabled(false);
                aoDeleteUnused.setEnabled(false);
                attrUpdateBtn.setEnabled(false);
                attrDelObjsBtn.setEnabled(false);

                attrPropItem.setEnabled(true);
                addTriggerItem.setEnabled(true);
                attrEventsMenu.setEnabled(true);
                editTablePropItemattr.setEnabled(false); //TODO: Tedit ??TEST1
                attrCopyMenu.setEnabled(true);
                attrPropBtn.setEnabled(true);

                deleteItem.setEnabled(canDeleteMethod);
                deleteButton.setEnabled(canDeleteMethod);

                methodCreateBtn.setEnabled(false);
                createMethod.setEnabled(false);
                if(methodBtn.isEnabled())  methodtSplitPane.clear();
                methodBtn.setEnabled(true);
                findRevAttrItem.setEnabled(false);
            }
        } else {
            attrPropItem.setEnabled(false);
            addTriggerItem.setEnabled(false);
            attrEventsMenu.setEnabled(false);
            editTablePropItemattr.setEnabled(true); //TODO: Tedit
            attrCopyMenu.setEnabled(false);
            deleteItem.setEnabled(false);
            aoUpdateRefs.setEnabled(false);
            aoDeleteUnused.setEnabled(false);
            attrPropBtn.setEnabled(false);
            deleteButton.setEnabled(false);
            attrUpdateBtn.setEnabled(false);
            attrDelObjsBtn.setEnabled(false);
            methodCreateBtn.setEnabled(false);
            attrCreateBtn.setEnabled(false);
            attrCreateItem.setEnabled(false);
            revAttrCreateItem.setEnabled(false);
            createMethod.setEnabled(false);
            findRevAttrItem.setEnabled(false);
        }
        assemlyInfoToStatusBar();
    }

    // Считываем выбранные элементы дерева классов и атрибутов
    public void assemlyInfoToStatusBar() {
        // Оба дерева должны быть инициализированы
        if (attrTree == null || classTree == null || packageTree == null) {
            return;
        }
        Object o = attrTree.getSelectedObject();
        KrnClass cls = null;
        int tabIndex = treesTabbedPane.getSelectedIndex();
        if (tabIndex == 0) {
        	PackageNode pn = packageTree.getSelectedNode();
        	if (pn != null) {
        		cls = pn.getKrnClass();
        	}
        } else {
        	ClassNode cn = classTree.getSelectedNode();
        	if (cn != null) {
        		cls = cn.getKrnClass();
        	}
        }
        // Выбран элемент в дереве атрибутов
        if (o != null) {
            Object ob = attrTree.getSelectionPath().getLastPathComponent();
            if (ob.equals(attrTree.getModel().getRoot())) {
                KrnAttribute attr = (KrnAttribute) o;
                if (cls != null) {
                    Or3Frame.instance().setClassStatusBarInfo(attr.name, ClassTreeIconLoader.getIcon(cls.isRepl, cls.isVirtual()), "ID = " + cls.id + "  UID=" + cls.uid);
                }
            } else if (o instanceof KrnAttribute) {
                KrnAttribute krnAttr = (KrnAttribute) o;
                if (krnAttr.id != 0 && krnAttr.uid != null) {
                    Or3Frame.instance().setClassStatusBarInfo(com.cifs.or2.client.Utils.getPathForAttributes(getSelectedAttributes()), AttributeTreeIconLoader.getIcon(krnAttr), "ID=" + krnAttr.id + "  UID=" + krnAttr.uid);
                } else {
                    Or3Frame.instance().setClassStatusBarInfo(com.cifs.or2.client.Utils.getPathForAttributes(getSelectedAttributes()), null, "");
                }
            } else {
            	try {
            		KrnMethod krnMethod = (KrnMethod) o;
            		Kernel krn = Kernel.instance();
            		KrnClass cls_ = krn.getClassByName("User");
            		KrnAttribute attr = krn.getAttributeByName(cls_, "name");
            		String name = krn.getStringsSingular(krnMethod.ownerId, attr.id, 0, false, false);
            		if (name == null || name == "")
            			name = " не задан";

            		Or3Frame.instance().setClassStatusBarInfo(com.cifs.or2.client.Utils.getPathForMethod(getSelectedMethod()) + "   Автор: " + name, krnMethod.isClassMethod ? statMethodIcon : methodIcon, 
            				krnMethod.isClassMethod ? "Метод класса" + "	UID=" + krnMethod.uid : "метод" + "	UID=" + krnMethod.uid);
            	} catch (KrnException e) {
            		e.printStackTrace();
            	}
            }
        } else if (cls != null) { // Выбран элемент в дереве классов
            Or3Frame.instance().setClassStatusBarInfo(com.cifs.or2.client.Utils.getPathForClasses(getSelectedClasses()), ClassTreeIconLoader.getIcon(cls.isRepl, cls.isVirtual()), "ID = " + cls.id + "  UID=" + cls.uid);
        }
    }

    public void updateAttrTree(ClassNode cls) {
        TreeModel model = attrTree.getModel();
        attrTree.setModel(null);
        Kernel.instance().releseClassTree(model);
        attrTree.setModel(Kernel.instance().getClassTree(cls));
    }

    public void updateAttrTree(KrnClass cls) throws KrnException {
        TreeModel model = attrTree.getModel();
        attrTree.setModel(null);
        Kernel.instance().releseClassTree(model);
        ClassNode cn = Kernel.instance().getClassNode(cls.id);
        attrTree.setModel(Kernel.instance().getClassTree(cn));
    }
    
    public TreePath getLastTypePath() {
        return lastTypePath;
    }

    public void setLastTypePath(TreePath lastTypePath) {
        this.lastTypePath = lastTypePath;
    }

    private void setReplFlagAllChildren(ClassNode node, boolean isRepl) {
        try {
            TreeNode parent = node.getParent();
            KrnClass baseClass = null;
            if (parent != null) {
                baseClass = ((ClassNode) parent).getKrnClass();
            }
            String name = node.getKrnClass().name;
            final Kernel krn = Kernel.instance();
            krn.change(node, baseClass, name, isRepl);
            if (!node.isLeaf()) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    ClassNode childNode = (ClassNode) node.getChildAt(i);
                    setReplFlagAllChildren(childNode, isRepl);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void applyRights(User user) {
        canCreate = user.hasRight(Or3RightsNode.CLASSES_CREATE_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.CLASSES_DELETE_RIGHT);
        canEdit = user.hasRight(Or3RightsNode.CLASSES_EDIT_RIGHT);

        canCreateAttr = user.hasRight(Or3RightsNode.ATTRIBUTES_CREATE_RIGHT);
        canEditAttr = user.hasRight(Or3RightsNode.ATTRIBUTES_EDIT_RIGHT);
        canDeleteAttr = user.hasRight(Or3RightsNode.ATTRIBUTES_DELETE_RIGHT);

        canCreateMethod = user.hasRight(Or3RightsNode.METHODS_CREATE_RIGHT);
        canEditMethod = user.hasRight(Or3RightsNode.METHODS_EDIT_RIGHT);
        canDeleteMethod = user.hasRight(Or3RightsNode.METHODS_DELETE_RIGHT);
    }

    private class TruncateAction extends AbstractAction {

        public TruncateAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
    		KrnClass cls;
        	if (treesTabbedPane.getSelectedIndex() == 0) {
        		cls = packageTree.getSelectedClass();
        	} else {
        		cls = classTree.getSelectedClass();
        	}
    		if (cls == null) {
    			return;
    		}
            String msg = "Вы действительно хотите удалить все объекты класса '" + cls.name + "'?";
            Container cont = ClassBrowser.this.getTopLevelAncestor();
            int res = -1;
            res = MessagesFactory.showMessageDialog(cont, MessagesFactory.QUESTION_MESSAGE, msg);
            if (ButtonsFactory.BUTTON_YES == res) {
                Cursor oldCursor = cont.getCursor();
                CursorToolkit.startWaitCursor(cont);
                int count = 0;
                try {
                    count = krn_.truncateClass(cls);
                } catch (KrnException ex) {
                    CursorToolkit.stopWaitCursor(cont);
                    ex.printStackTrace();
                    MessagesFactory.showMessageDialogBig(ClassBrowser.this.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, ex.getMessage());
                }
                cont.setCursor(oldCursor);
                MessagesFactory.showMessageDialog(ClassBrowser.this.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, count + " объектов успешно удалено.");
                CursorToolkit.stopWaitCursor(cont);
            }
        }
    }

    private void setComment(String txt) {
        comText.setText(txt);
    }
    
    private ActionListener ClassPropDialogOkListener(final DesignerDialog dlg, final kz.tamur.admin.ClassPropPanel cp, final Kernel krn, final KrnClass base){
    	return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	        	try {
	                krn.createClass(base, cp.getClassName(), cp.isRepl(), cp.getTableName(), cp.getComment(), cp.isVirtual() ? 1 : 0);
	                dlg.dispose();
	        	} catch (KrnException ke){
	        		if (ke.getMessage().equalsIgnoreCase("C_NAME")) {
	        			cp.setNotClassName(cp.getClassName());
	        		} else if (ke.getMessage().equalsIgnoreCase("C_TNAME")) {
	        			cp.setNotTableName(cp.getTableName());
	        		} else {
	        			cp.setErrorMessange("Неудалось выполнить операцию.");
	        		}
	        	}
			}
		};
    }
    
    private void EditorIsVisible(final JPanel jPanel1, final JPanel jPanel2, boolean isVisible){
    	if (isVisible){
            jPanel1.setPreferredSize(new Dimension(100, 100));
            jPanel1.setMinimumSize(new Dimension(300, 300));
            jPanel1.setMaximumSize(new Dimension(9999, 9999));
    		final ActionListener offParked = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	jCheckBox1.setSelected(false);
                	//EditorIsVisible(jPanel1, jPanel2, false);
                }
            };
            EditorWindow.moveToComponent(jPanel2, "ClassBrowser", offParked);
            In_P23andEditor.setDividerSize(2);
            In_P23andEditor.setDividerLocation(0.2D);
            In_P23andEditor.setResizeWeight(0.2D);
            //In_P23andEditor.setEnabled(true);
        } else {
        	int Psize = 18;
        	jPanel1.setPreferredSize(new Dimension(50, Psize));
        	jPanel1.setMinimumSize(new Dimension(50, Psize));
        	jPanel1.setMaximumSize(new Dimension(50, Psize));
        	EditorWindow.moveToComponent(null, null, null);
        	jPanel2.repaint();
        	In_P23andEditor.setDividerSize(0);
        	In_P23andEditor.setResizeWeight(1.0D);
        	In_P23andEditor.setDividerLocation(1.0D);
        	In_P23andEditor.setDividerLocation(In_P23andEditor.getHeight() - Psize);
        	//In_P23andEditor.setEnabled(false);
        }
    }
    
    private void editTableShow(final String tname, final String name, final Kernel krn){
    	Container cont = this.getTopLevelAncestor();
    	
    	ActionListenerRt<Boolean, int[]> al = new ActionListenerRt<Boolean, int[]>() {
            @Override
			public Boolean action(int[] cols) {
            	try {
                	if (krn.columnMove(cols, tname)) {
                		return true;
                	}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	return false;
            }
        };
        
        try {
    		System.out.println(tname);
    		String[][] colinf = krn.getColumnsInfo(tname);
    		if (colinf != null) {
    			final String title = name+" ["+tname+"]";
				if (cont instanceof Dialog) {
	                new WndTableEditor((Dialog)cont, colinf, title, al);
	            } else if (cont instanceof Frame) {
	                new WndTableEditor((Frame)cont, colinf, title, al);
	            }
    		}
    	} catch (Exception ex) {}
    }
    
    private void exportClass(KrnClass selectedClass) {
		// TODO Auto-generated method stub
		
	}

	private void importClassTo(KrnClass selectedClass) {
		// TODO Auto-generated method stub
		
	}
}
