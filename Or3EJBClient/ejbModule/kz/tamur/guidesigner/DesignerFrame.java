package kz.tamur.guidesigner;

import static kz.tamur.comps.Utils.createDesignerToolBar;
import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.BORDER;
import static kz.tamur.comps.models.Types.COLOR;
import static kz.tamur.comps.models.Types.DOUBLE;
import static kz.tamur.comps.models.Types.ENUM;
import static kz.tamur.comps.models.Types.ENUM_TOOL_TIP;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.FILTER;
import static kz.tamur.comps.models.Types.FONT;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.IMAGE;
import static kz.tamur.comps.models.Types.INTEGER;
import static kz.tamur.comps.models.Types.KRNOBJECT;
import static kz.tamur.comps.models.Types.KRNOBJECT_ID;
import static kz.tamur.comps.models.Types.MSTRING;
import static kz.tamur.comps.models.Types.PMENUITEM;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.REPORT;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.SEQUENCE;
import static kz.tamur.comps.models.Types.STRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;
import static kz.tamur.comps.models.Types.VIEW_STRING;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NO;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kz.tamur.Or3Frame;
import kz.tamur.admin.AdminFrame;
import kz.tamur.comps.ComponentsTreeRenderer;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Factories;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrColumnComponent;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrScrollPane;
import kz.tamur.comps.OrSplitPane;
import kz.tamur.comps.OrTabbedPane;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.guidesigner.noteeditor.NoteEditor;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.lang.ErrRecord;
import kz.tamur.or3.client.props.ComboProperty;
import kz.tamur.or3.client.props.ComboToolTipProperty;
import kz.tamur.or3.client.props.ExprProperty;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.FolderProperty;
import kz.tamur.or3.client.props.InspectorOwner;
import kz.tamur.or3.client.props.KrnOrExprProperty;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.TreeOrExprProperty;
import kz.tamur.or3.client.props.inspector.ExprDelegate;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.or3.client.props.inspector.PropertyTableModel;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.ExpressionDebuger;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.FrameTemplate;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.OrFileChooserUI;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.03.2004
 * Time: 13:39:08
 */
public class DesignerFrame extends JPanel implements ItemListener, ActionListener, PropertyListener, InspectorOwner {

    private AdminFrame admFrame;
    private OrGradientMenuBar menuBar = new OrGradientMenuBar();
    private JToolBar toolBar = createDesignerToolBar();
    private JToolBar standartCompsToolBar = createDesignerToolBar();
    private JToolBar containersCompsToolBar = createDesignerToolBar();
    private JToolBar userCompsToolBar = createDesignerToolBar();
    private JToolBar hyperCompsToolBar = createDesignerToolBar();
    private JToolBar treesCompsToolBar = createDesignerToolBar();
    private JToolBar tablesCompsToolBar = createDesignerToolBar();
    private JToolBar decorCompsToolBar = createDesignerToolBar();
    private ComponentsTool tools = new ComponentsTool();
    private JComboBox projectsCombo = kz.tamur.rt.Utils.createCombo();
    private JPanel basicPanel = new JPanel(new GridBagLayout());
    public static ControlTabbedContent tabbedContent;
    private JToggleButton debugBtn = ButtonsFactory.createFunctionButton(ButtonsFactory.FN_DEBUG);
    private JButton classesBtn = ButtonsFactory.createFunctionButton1(ButtonsFactory.FN_CLASSES);
    private JComponent designerPanel;
    private JSplitPane basicSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JSplitPane secondSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private ButtonGroup mainBG = new ButtonGroup();
    private DesignerStatusBar statusPanel = new DesignerStatusBar();
    private JButton historyBtn = ButtonsFactory.createToolButton("history.png", "История");
    private JButton openBut = ButtonsFactory.createToolButton("Open", "Открыть интерфейс");
    private JButton saveBut = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JToggleButton unselectBut = ButtonsFactory.createCompButton(null, getImageIcon("Unselected"));
    private JButton saveOnBut = ButtonsFactory.createToolButton("SaveOnDisk", "Сохранить на диске в виде XML");
    private JButton openFromBut = ButtonsFactory.createToolButton("OpenFrom", "Открыть из...");
    private JButton findCompBut = ButtonsFactory.createToolButton("SearchComp", "Найти компонент");
    private JButton copyBut = ButtonsFactory.createToolButton("Copy", "Копировать");
    private JButton pasteBut = ButtonsFactory.createToolButton("Paste", "Вставить");
    private JButton cutBut = ButtonsFactory.createToolButton("Cut", "Вырезать");
    private JButton deleteBut = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton findBut = ButtonsFactory.createToolButton("Find", "Найти");
    private JButton replaceBut = ButtonsFactory.createToolButton("S&R", "Заменить");
    private JButton previewBut = ButtonsFactory.createToolButton("Preview", "Просмотр");
    private JButton previewWebBut = ButtonsFactory.createToolButton("PreviewWeb", "Просмотр на браузере");
    private JButton fullScreenBut = ButtonsFactory.createToolButton("FullScreen", "Полноэкранный режим разработки");
    private JButton viewHistoryBut = ButtonsFactory.createToolButton("ServiceHistory.gif", "История изменения");
    private JMenu viewMenu = new DesinerMenu("Вид");
    private JCheckBoxMenuItem inspectorItem = Utils.createCheckMenuItem("Инспектор свойств", ButtonsFactory.FN_INSPECTOR);
    private JCheckBoxMenuItem compsItem = Utils.createCheckMenuItem("Компоненты", ButtonsFactory.FN_TREE);
    private JMenuItem multiInspectorItem = createMenuItem("Компоненты и Инспектор свойств", "MultiInspector");
    private JMenuItem previewItem = createMenuItem("Просмотр");
    private JMenuItem previewWebItem = createMenuItem("Просмотр на браузере");
    private JMenu editMenu = new DesinerMenu("Редактирование");
    public JMenuItem undoItem = createMenuItem("Отменить");
    public JMenuItem redoItem = createMenuItem("Повторить");
    private JMenuItem copyItem = createMenuItem("Копировать");
    private JMenuItem pasteItem = createMenuItem("Вставить");
    private JMenuItem cutItem = createMenuItem("Вырезать");
    private JMenuItem deleteItem = createMenuItem("Удалить");
    private JMenuItem findItem = createMenuItem("Найти");
    private JMenuItem replaceItem = createMenuItem("Заменить");
    private JMenuItem findComponentItem = createMenuItem("Найти компонент");
    private JMenuItem exportInterfacesItem = createMenuItem("Экспортировать интерфейсы в файл");
    private JMenuItem importInterfacesItem = createMenuItem("Импортировать интерфейсы из файла");
    private JMenu fileMenu = new DesinerMenu("Файл");
    private JMenuItem historyItem = createMenuItem("История", "history.png");
    private JMenuItem createItem = createMenuItem("Создать");
    private JMenuItem openItem = createMenuItem("Открыть");
    private JMenuItem saveItem = createMenuItem("Сохранить всё");
    private JMenuItem saveOnItem = createMenuItem("Сохранить на диске...");
    private JMenuItem openFromItem = createMenuItem("Открыть из...");
    private JMenuItem closeItem = createMenuItem("Закрыть");
    private JMenu toolsMenu = new DesinerMenu("Инструменты");
    private JMenuItem classesItem = createMenuItem("Классы");
    private JMenuItem debugItem = createMenuItem("Отладчик");
    private JMenuItem noteItem = createMenuItem("Редактор подсказок", "helpEditor");
    private ComponentsTreeDlg compsTreeDlg;
    private MultiInspectorDlg multiInspectorDlg;
    private ImageIcon ic1 = getImageIcon("CompsTree");
    private DesignerInternalDialog treeIntDlg = new DesignerInternalDialog("Компоненты", ic1, this);
    private static JComboBox lastInterfacesCombo = kz.tamur.rt.Utils.createCombo();
    private static ArrayList<Date> keys;
    private static SortedMap<Date, Map<Long, String>> items;
    private static boolean canOpen = false;
    private final String notFoundClass = "Класс 'Action' не найден или не соответствует формату!";
    private LanguageCombo langSelector = new LanguageCombo();
    private DebugPanel debugPanel = null;
    private PropertyInspector inspector = new PropertyInspector(this);
    private Controller controller = new Controller(inspector);
    private JProgressBar pBar = new JProgressBar();
    private JLabel actionLabel = new JLabel();
    public InterfaceTree tree = null;
    private AbstractDesignerTreeNode lastNode = null;
    private ComponentsTree componentTree = new ComponentsTree();
    private ComponentsTreeModel treeModel = new ComponentsTreeModel(null);
    private JLabel ifcLabel = new JLabel(getImageIcon("FormTab"));
    private Kernel kernel = Kernel.instance();
    private ArrayList<OrGuiComponent> searchList = new ArrayList<OrGuiComponent>();
    private int searchIndex = 0;
    private SearchPanel searchPanel;
    private ReplacePanel replacePanel;
    public static DesignerFrame designerFrame = null;
    public static String path_expr = new String();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private static ArrayList<Long> loadingInterfaceID = new ArrayList<Long>();
    private JTextField uidView = new JTextField();
    private JLabel ifcOwner = kz.tamur.rt.Utils.createLabel("");
    private JButton checkBtn = ButtonsFactory.createToolButton("runDebug", "Проверка процесса");
       
    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");
    
    private KrnClass UIClass;
    private KrnAttribute titleAttr;
    private KrnAttribute developerAttr;
    private KrnAttribute configAttr;
    private KrnAttribute stringsAttr;
    private KrnAttribute webConfigAttr;
    private KrnAttribute webConfigChangedAttr;
    private KrnAttribute parentAttr;
    private KrnAttribute filtersFolderAttr;
    
    private KrnObject ruLangObj;
    private KrnObject kzLangObj;
    
    public void setOwner(KrnObject obj) {
    	if(obj != null)
    		ifcOwner.setText("Владелец: " + (kz.tamur.comps.Utils.getObjOwner(obj)!= null? kz.tamur.comps.Utils.getObjOwner(obj): ""));
    }

    public static DesignerFrame instance() {
        if (designerFrame == null)
            designerFrame = new DesignerFrame();
        return designerFrame;
    }

    DesignerFrame() {
        if (ServiceControl.instance() != null)
            tabbedContent = ServiceControl.instance().getContentTabs();
        else
            tabbedContent = ServiceControl.instance(null).getContentTabs();

        getToolkit().addAWTEventListener(controller, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        setLayout(new BorderLayout());
        initMenu();
        initToolBar();
        initInspectorAndTree();
        initStatusBar();
        add(basicPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);
        basicSplitPane.setDividerSize(3);
        basicSplitPane.setLeftComponent(mainSplitPane);
        basicSplitPane.setDividerLocation(1.0);
        add(basicSplitPane, BorderLayout.CENTER);

        langSelector.addActionListener(this);
        if (tree == null) {
            tree = kz.tamur.comps.Utils.getInterfaceTree();
        }
        debugItem.addActionListener(this);
        noteItem.addActionListener(this);
        inspector.getDialog("Интерфейсы");
        InterfaceActionsConteiner.setDesignerFrame(this);
        
        try {
			UIClass = kernel.getClassByName("UI");
		    titleAttr = kernel.getAttributeByName(UIClass, "title");
		    developerAttr = kernel.getAttributeByName(UIClass, "developer");
		    configAttr = kernel.getAttributeByName(UIClass, "config");
		    stringsAttr = kernel.getAttributeByName(UIClass, "strings");
		    webConfigAttr = kernel.getAttributeByName(UIClass, "webConfig");
		    webConfigChangedAttr = kernel.getAttributeByName(UIClass, "webConfigChanged");
		    parentAttr = kernel.getAttributeByName(UIClass, "parent");
		    filtersFolderAttr = kernel.getAttributeByName(UIClass, "filtersFolder");
		    
		    
			KrnClass langClass = kernel.getClassByName("Language");
			KrnAttribute codeAttr = kernel.getAttributeByName(langClass, "code");
		    ruLangObj = kernel.getObjectsByAttribute(langClass.id, codeAttr.id, 0, 0, "RU", 0)[0];
		    kzLangObj = kernel.getObjectsByAttribute(langClass.id, codeAttr.id, 0, 0, "KZ", 0)[0];
		} catch (KrnException e) {
			e.printStackTrace();
		}

        projectsCombo.addItem("default");
        projectsCombo.addItem("ЕГКН");
        projectsCombo.setSelectedIndex(0);
        kz.tamur.rt.Utils.setAllSize(projectsCombo, new Dimension(70, 22));
        projectsCombo.setToolTipText("Выбор проекта");
    }

    public void placeDividers() {
        mainSplitPane.setDividerLocation(0.3);
        secondSplitPane.setDividerLocation(0.5);
        validate();
    }

    public PropertyInspector getInspector() {
        return inspector;
    }

    public Controller getController() {
        return controller;
    }

    public ComponentsTree getComponentsTree() {
        return componentTree;
    }

    private void initStatusBar() {
        ifcLabel.setFont(kz.tamur.rt.Utils.getDefaultFont());
        ifcLabel.setForeground(kz.tamur.rt.Utils.getDarkShadowSysColor());
        ifcLabel.setHorizontalAlignment(SwingConstants.LEFT);
        controller.setClassLabel(ifcLabel);
        controller.setTopLevelAncestor(this);
        statusPanel.addLabel("UID: ");
        statusPanel.addTextField(uidView);
        statusPanel.addSeparator();
        statusPanel.addLabel(ifcOwner);
        statusPanel.addSeparator();
        
        statusPanel.addAnyComponent(ifcLabel, 1);
//        statusPanel.addEmptySpace();
        statusPanel.addSeparator();
        
        updateStatusBar();

        statusPanel.addAnyComponent(currentDbName);
        statusPanel.addSeparator();
        currentUserLable.setIcon(getImageIcon("User"));
        statusPanel.addAnyComponent(currentUserLable);
        statusPanel.addSeparator();
        dsLabel.setIcon(getImageIcon("HostConn"));
        dsLabel.setIconTextGap(10);
        statusPanel.addAnyComponent(dsLabel);
        statusPanel.addSeparator();
        serverLabel.setIcon(getImageIcon("PortConn"));
        serverLabel.setIconTextGap(10);
        statusPanel.addAnyComponent(serverLabel);
        statusPanel.addSeparator();
        actionLabel.setOpaque(false);
        statusPanel.addAnyComponent(actionLabel);
        statusPanel.addSeparator();
        pBar.setBorderPainted(false);
        pBar.setPreferredSize(new Dimension(150, 15));
        pBar.setMinimumSize(new Dimension(150, 15));
        pBar.setMaximumSize(new Dimension(150, 15));
        statusPanel.addAnyComponent(pBar);
        statusPanel.addSeparator();
        statusPanel.addLabel(" Язык интерфейса: ");
        statusPanel.addAnyComponent(langSelector);
        statusPanel.addCorner();
    }

    private void initMenu() {
        menuBar.add(new JLabel(getImageIcon("menuFlag")));
        editMenu.add(undoItem);
        undoItem.setEnabled(false);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoItem.addActionListener(this);
        editMenu.add(redoItem);
        redoItem.setEnabled(false);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redoItem.addActionListener(this);
        editMenu.add(copyItem);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.addSeparator();
        editMenu.add(deleteItem);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editMenu.addSeparator();
        editMenu.add(findItem);
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        editMenu.add(replaceItem);
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        editMenu.add(findComponentItem);
        editMenu.addSeparator();
        editMenu.add(exportInterfacesItem);
        editMenu.add(importInterfacesItem);
        
        inspectorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        compsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));

        inspectorItem.addItemListener(this);
        inspectorItem.setSelected(true);
        viewMenu.add(inspectorItem);
        compsItem.addItemListener(this);
        compsItem.setSelected(true);
        viewMenu.add(compsItem);
        viewMenu.add(multiInspectorItem);
        multiInspectorItem.addActionListener(this);
        viewMenu.addActionListener(this);
        viewMenu.addSeparator();
        viewMenu.add(previewItem);
        viewMenu.add(previewWebItem);
        previewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        previewWebItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, ActionEvent.ALT_MASK));
        createItem.addActionListener(this);

        historyItem.addActionListener(this);

        openItem.addActionListener(this);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        saveItem.addActionListener(this);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        previewItem.addActionListener(this);
        previewWebItem.addActionListener(this);
        closeItem.addActionListener(this);
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        saveOnItem.addActionListener(this);
        openFromItem.addActionListener(this);
        deleteItem.addActionListener(this);
        classesItem.addActionListener(this);
        cutItem.addActionListener(this);
        findItem.addActionListener(this);
        replaceItem.addActionListener(this);
        findComponentItem.addActionListener(this);
        exportInterfacesItem.addActionListener(this);
        importInterfacesItem.addActionListener(this);

        fileMenu.add(historyItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(saveOnItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);
        toolsMenu.add(noteItem);
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.setOpaque(false);
        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            Component c = menuBar.getComponent(i);
            if (c instanceof JMenu) {
                c.setForeground(kz.tamur.rt.Utils.getLightSysColor());
            }
        }
    }

    public OrGradientMenuBar getMenu() {
        return menuBar;
    }

    public static ControlTabbedContent getTabbedContent() {
        return tabbedContent;
    }

    private void initToolBar() {
        fullScreenBut.setEnabled(false);
        historyBtn.addActionListener(this);
        openBut.addActionListener(this);
        saveBut.addActionListener(this);
        findCompBut.addActionListener(this);
        previewBut.addActionListener(this);
        previewWebBut.addActionListener(this);
        viewHistoryBut.addActionListener(this);
        fullScreenBut.addActionListener(this);
        saveOnBut.addActionListener(this);
        openFromBut.addActionListener(this);
        deleteBut.addActionListener(this);
        copyBut.addActionListener(this);
        pasteBut.addActionListener(this);
        cutBut.addActionListener(this);
        findBut.addActionListener(this);
        replaceBut.addActionListener(this);
        checkBtn.addActionListener(this);
        
        setOpaque(isOpaque);
        basicPanel.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        tools.setOpaque(isOpaque);
        standartCompsToolBar.setOpaque(isOpaque);
        containersCompsToolBar.setOpaque(isOpaque);
        userCompsToolBar.setOpaque(isOpaque);
        hyperCompsToolBar.setOpaque(isOpaque);
        treesCompsToolBar.setOpaque(isOpaque);
        tablesCompsToolBar.setOpaque(isOpaque);
        decorCompsToolBar.setOpaque(isOpaque);

        tabbedContent.setOpaque(isOpaque);
        mainSplitPane.setOpaque(isOpaque);
        basicSplitPane.setOpaque(isOpaque);

        toolBar.setBorderPainted(false);
        standartCompsToolBar.setBorderPainted(false);
        containersCompsToolBar.setBorderPainted(false);
        userCompsToolBar.setBorderPainted(false);
        hyperCompsToolBar.setBorderPainted(false);
        treesCompsToolBar.setBorderPainted(false);
        tablesCompsToolBar.setBorderPainted(false);
        decorCompsToolBar.setBorderPainted(false);

        toolBar.add(new JLabel(getImageIcon("decor")));
        toolBar.add(historyBtn);
        toolBar.add(openBut);
        toolBar.add(saveBut);
        toolBar.addSeparator();
        toolBar.add(saveOnBut);
        toolBar.addSeparator();
        toolBar.add(copyBut);
        toolBar.add(pasteBut);
        toolBar.add(cutBut);
        toolBar.addSeparator();
        toolBar.add(findBut);
        toolBar.add(replaceBut);
        toolBar.add(findCompBut);
        toolBar.addSeparator();
        toolBar.add(deleteBut);
        toolBar.addSeparator();
        toolBar.add(previewBut);
        toolBar.addSeparator();
        toolBar.add(projectsCombo);
        toolBar.add(previewWebBut);
        toolBar.addSeparator();
        toolBar.add(viewHistoryBut);
        toolBar.addSeparator();
        mainBG.add(unselectBut);
        toolBar.add(unselectBut);
        toolBar.add(checkBtn);
        toolBar.addSeparator();
        unselectBut.setSelected(true);
        unselectBut.setToolTipText("Отменить выделение");
        toolBar.addSeparator();
        standartCompsToolBar.add(new JLabel(getImageIcon("decor")));
        containersCompsToolBar.add(new JLabel(getImageIcon("decor")));
        userCompsToolBar.add(new JLabel(getImageIcon("decor")));
        hyperCompsToolBar.add(new JLabel(getImageIcon("decor")));
        treesCompsToolBar.add(new JLabel(getImageIcon("decor")));
        tablesCompsToolBar.add(new JLabel(getImageIcon("decor")));

        tools.addToolBar("Стандартные", standartCompsToolBar);
        tools.addToolBar("Контейнеры", containersCompsToolBar);
        tools.addToolBar("Пользовательские", userCompsToolBar);
        tools.addToolBar("Гипер компоненты", hyperCompsToolBar);
        tools.addToolBar("Древовидные", treesCompsToolBar);
        tools.addToolBar("Табличные", tablesCompsToolBar);

        Factories.ComponentButton[] names = Factories.instance().getNames();
        for (int i = 0; i < names.length; i++) {
            ImageIcon icon = getImageIconFull(names[i].icon);
            JToggleButton btn = ButtonsFactory.createCompButton(names[i].name, icon);
            btn.addItemListener(this);
            mainBG.add(btn);
            switch (names[i].status) {
            case Constants.STANDART_COMP:
                standartCompsToolBar.add(btn);
                break;
            case Constants.CONTAINER_COMP:
                containersCompsToolBar.add(btn);
                break;
            case Constants.USER_COMP:
                userCompsToolBar.add(btn);
                break;
            case Constants.HYPER_COMP:
                hyperCompsToolBar.add(btn);
                break;
            case Constants.TREES_COMP:
                treesCompsToolBar.add(btn);
                break;
            case Constants.TABLE_COMP:
                tablesCompsToolBar.add(btn);
                break;
            case Constants.DECOR_COMP:
                decorCompsToolBar.add(btn);
                break;
            default:
                standartCompsToolBar.add(btn);
                break;
            }
        }

        basicPanel.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, Constants.INSETS_0, 0, 0));
        basicPanel.add(tools, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        setToolButtonsEnabled(false);
        Dimension dimension = new Dimension(330, 25);
        lastInterfacesCombo.setPreferredSize(dimension);
        lastInterfacesCombo.setMaximumSize(dimension);
        lastInterfacesCombo.setMinimumSize(new Dimension(50, 25));
        lastInterfacesCombo.addItem(notFoundClass);
        lastInterfacesCombo.setToolTipText("Измененные интерфейсы");
        basicPanel.add(lastInterfacesCombo);
        loadInterfaces();
        lastInterfacesCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (String.valueOf(lastInterfacesCombo.getSelectedItem()).equals(notFoundClass)) {
                    return;
                }
                try {
                    if (canOpen) {
                        tree.setSelectedNode(kernel.getObjectById(
                                new ArrayList<Long>(items.get(keys.get(lastInterfacesCombo.getSelectedIndex())).keySet()).get(0),
                                0));
                        AbstractDesignerTreeNode node = tree.getSelectedNode();
                        if (node == null || !node.isLeaf())
                            return;
                        KrnObject ui = node.getKrnObj();
                        lastNode = node;
                        if (!tabbedContent.isExistIfr(ui.id)) {
                            boolean readOnly = !kernel.getUser().hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT);
                            if (!readOnly) {
                                try {
                                	UserSessionValue us = kernel.vcsLockObject(ui.id);
                                    if (us != null) {
                                        if (ifcVcsLock(node.toString(), us) == BUTTON_YES) {
                                            readOnly = true;
                                        } else {
                                            return;
                                        }
                                    }
                                    if(!readOnly){
	                                    us = kernel.blockObject(ui.id);
	                                    if (us != null) {
	                                        if (ifcLock(node.toString(), us) == BUTTON_YES) {
	                                            readOnly = true;
	                                        } else
	                                            return;
	                                    }
                                    }
                                } catch (KrnException e) {
                                    e.printStackTrace();
                                }
                            }
                            actionLabel.setIcon(getImageIcon("OpenAni"));
                            new IfcLoader(ui, readOnly, null).start();
                            // Сохранение в историю
                            HistoryWithDate hwd = new HistoryWithDate(ui, new Date());
                            kernel.getUser().addIfcInHistory(hwd, node.toString());
                            InterfaceActionsConteiner.instance(ui.getId(), node.toString());
                            loadingInterfaceID.add(ui.id);
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadInterfaces() {
        if (InterfaceActionsConteiner.getInterfacesMode()) {
            items = InterfaceActionsConteiner.getLastInterfaces();
            keys = new ArrayList<Date>(items.keySet());
            canOpen = false;
            lastInterfacesCombo.removeAllItems();
            for (int i = 0; i < items.size() && i < 10; i++) {
                lastInterfacesCombo.addItem(new ArrayList<String>(items.get(keys.get(i)).values()).get(0));
            }
            canOpen = true;
        }
    }

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (src instanceof ButtonsFactory.DesignerCompButton) {
            ButtonsFactory.DesignerCompButton btn = (ButtonsFactory.DesignerCompButton) src;
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.setCurrCompCalss(btn.compClass);
            } else {
                controller.setCurrCompCalss(null);
            }
        }
        if (src instanceof ButtonsFactory.FunctionToolButton) {
            splitManipulator(e, src);
        }
        if (src instanceof JCheckBoxMenuItem) {
            splitManipulator(e, src);
        }
    }

    private void initInspectorAndTree() {
        componentTree.setCellRenderer(new ComponentsTreeRenderer(null));
        componentTree.setModel(treeModel);
        componentTree.addPropertyListener(controller);
        controller.addPropertyListener(componentTree);
        treeIntDlg.addContent(new JScrollPane(componentTree));
        treeIntDlg.setDlgType(DesignerInternalDialog.TREE_DLG);
        secondSplitPane.add(treeIntDlg);
        secondSplitPane.add(inspector);
        TabbedStateAdapter tsa = new TabbedStateAdapter(inspector, componentTree, this);
        tabbedContent.addChangeListener(tsa);
        tabbedContent.addContainerListener(tsa);
        mainSplitPane.setLeftComponent(secondSplitPane);
        mainSplitPane.setRightComponent(tabbedContent);
        controller.addPropertyListener(tabbedContent);
        controller.addPropertyListener(this);
        controller.addPropertyListener(treeModel);
    }

    public void applyRights(User user) {
        boolean res = user.hasRight(Or3RightsNode.INTERFACE_VIEW_RIGHT);
        openItem.setEnabled(res);
        openBut.setEnabled(res);
    }

    public static class DesinerMenu extends JMenu {
        public DesinerMenu() {
            super();
            init();
        }

        public DesinerMenu(String title) {
            super(title);
            init();
        }

        void init() {
            setFont(kz.tamur.rt.Utils.getDefaultFont());
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        LangItem li = (LangItem) langSelector.getSelectedItem();
        if (src == fullScreenBut) {
            Or3Frame.instance().fullScreenIfc();
        } else if (src == saveItem || src == saveBut) {
            save();
        } else if (src == historyItem || src == historyBtn) {
            showHistory((JComponent) src);
        } else if (src == openItem || src == openBut) {
            load();
        } else if (src == previewItem || src == previewBut) {
            try {
                preview();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if(src == previewWebItem || src == previewWebBut) {
        	try {
                previewWeb();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (src == langSelector && ((LanguageCombo) src).isSelfChange()) {
            setInterfaceLanguage();
            ComponentsTreeRenderer rend = (ComponentsTreeRenderer) componentTree.getCellRenderer();
            rend.setLangId(li.obj.id);
            treeModel.fireComponentsTreeModelChanged(this);
            TreeUIDMap.clearMap(AbstractDesignerTreeNode.INTERFACE_NODE);
            tree.setLangId(li.obj.id);
        } else if (src == unselectBut) {
            controller.setCurrCompCalss(null);
        } else if (src == closeItem && processExit() != BUTTON_CANCEL) {
            Or3Frame frm = (Or3Frame) getTopLevelAncestor();
            frm.closeCurrent();
            frm.setTitle(Or3Frame.getApptitle());
        } else if (src == saveOnBut || src == saveOnItem) {
            saveOnDisk();
        } else if (src == classesBtn || src == classesItem) {
            showClasses();
        } else if (src == deleteBut || src == deleteItem) {
            controller.deleteComponent();
        } else if (src == copyBut || src == copyItem) {
            CursorToolkit.startWaitCursor(this);
            controller.copyToBuffer();
            CursorToolkit.stopWaitCursor(this);
        } else if (src == undoItem) {
            CmdUndoAction.Undo(this);
        } else if (src == redoItem) {
            CmdRedoAction.Redo(this);
        } else if (src == pasteBut || src == pasteItem) {
            OrGuiComponent comp = tabbedContent.getOrGuiComponent();
            if (tabbedContent.getOrGuiComponent() instanceof OrGuiContainer) {
                CursorToolkit.startWaitCursor(this);
                controller.pasteComponent((OrGuiContainer) comp);
                CursorToolkit.stopWaitCursor(this);
            }
        } else if (src == cutBut || src == cutItem) {
            controller.cutComponent();
        } else if (src == findBut || src == findItem) {
            showSearchDialog();
        } else if (src == replaceBut || src == replaceItem) {
            showReplaceDialog();
        } else if (src == debugItem) {
            showDebug();
        } else if (src == findComponentItem || src == findCompBut) {
            searchComponentByTitle();
        } else if (src == exportInterfacesItem) {
        	exportInterfaces();
        } else if (src == importInterfacesItem) {
        	importInterfaces();
        } else if (src == noteItem) {
            showNoteEditor();
        } else if (src == multiInspectorItem) {
            setMultiInspector();
        } else if (src == checkBtn) {
        	checkGuiComponents();
        } else if (src == viewHistoryBut) {
        	KrnObject obj=tabbedContent.getSelectedFrame().getUiObject();
        	if(obj!=null) {
				try {
					List<KrnVcsChange> changes = Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, obj.uid);
		        	if(changes.size()>0) {
		        		Or3Frame.historysPanel.refreshTable(changes.get(0), true);
		        	}
		        	DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
		            dlg.setMinimumSize(new Dimension(900, 70));
		            dlg.show();
				} catch (KrnException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
            
        }
    }
    
    private void exportInterfaces() {
		System.out.println("Запуск экспорта интерфейсов!");
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			org.w3c.dom.Element interfacesElement = doc.createElement("Interfaces");
			doc.appendChild(interfacesElement);
			
	    	InterfaceNode interfaceNode = tree.getRoot();
	    	addInterface(doc, interfacesElement, interfaceNode);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:/Interfaces.xml"));
			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
		System.out.println("Экспорт интерфейсов завершен!");
    }
    
    private void addInterface(Document doc, org.w3c.dom.Element interfacesElement, InterfaceNode interfaceNode) {
    	KrnObject obj = interfaceNode.getKrnObj();
    	if (interfaceNode.isLeaf()) {
    		try {
//    			if (!"30824.28428".equals(obj.uid)) {
//    				return;
//    			}
	        	org.w3c.dom.Element interfaceElement = doc.createElement("Interface");
	        	interfaceElement.setAttribute("uid", obj.uid);
	        	
	        	// Обработка атрибута title
	        	String titleRU = kernel.getStringsSingular(obj.id, titleAttr.id, ruLangObj.id, false, false);
	        	if (titleRU.length() > 0) {
		        	org.w3c.dom.Element titleRUElement = doc.createElement("titleRU");
		        	titleRUElement.setTextContent(titleRU);
		        	interfaceElement.appendChild(titleRUElement);
	        	}
	        	
	        	String titleKZ = kernel.getStringsSingular(obj.id, titleAttr.id, kzLangObj.id, false, false);
	        	if (titleKZ.length() > 0) {
		        	org.w3c.dom.Element titleKZElement = doc.createElement("titleKZ");
		        	titleKZElement.setTextContent(titleKZ);
		        	interfaceElement.appendChild(titleKZElement);
	        	}
	        	
	        	// Обработка атрибута developer
	        	KrnObject developerObj = kernel.getObjectsSingular(obj.id, developerAttr.id, false);
	        	if (developerObj != null) {
		        	org.w3c.dom.Element developerElement = doc.createElement("developer");
		        	developerElement.setTextContent(developerObj.uid);
		        	interfaceElement.appendChild(developerElement);
	        	}
	        	
	        	// Обработка атрибута config
	        	byte[] config = kernel.getBlob(obj.id, configAttr, 0, 0, 0);
	        	if (config != null && config.length > 0) {
		        	org.w3c.dom.Element configElement = doc.createElement("config");
					configElement.setTextContent(new String(config, "UTF-8"));
		        	interfaceElement.appendChild(configElement);
	        	}

	        	// Обработка атрибута strings
	        	byte[] stringsRU = kernel.getBlob(obj.id, stringsAttr, 0, ruLangObj.id, 0);
	        	if (stringsRU != null && stringsRU.length > 0) {
		        	org.w3c.dom.Element stringsRUElement = doc.createElement("stringsRU");
		        	stringsRUElement.setTextContent(new String(stringsRU, "UTF-8"));
		        	interfaceElement.appendChild(stringsRUElement);
	        	}
	        	
	        	byte[] stringsKZ = kernel.getBlob(obj.id, stringsAttr, 0, kzLangObj.id, 0);
	        	if (stringsKZ != null && stringsKZ.length > 0) {
		        	org.w3c.dom.Element stringsKZElement = doc.createElement("stringsKZ");
		        	stringsKZElement.setTextContent(new String(stringsKZ, "UTF-8"));
		        	interfaceElement.appendChild(stringsKZElement);
	        	}
	        	
	        	// Обработка атрибута webConfig
	        	byte[] webConfigRU = kernel.getBlob(obj.id, webConfigAttr, 0, ruLangObj.id, 0);
	        	if (webConfigRU != null && webConfigRU.length > 0) {
		        	org.w3c.dom.Element webConfigRUElement = doc.createElement("webConfigRU");
		        	webConfigRUElement.setTextContent(new String(webConfigRU, "UTF-8"));
		        	interfaceElement.appendChild(webConfigRUElement);
	        	}
	        	
	        	byte[] webConfigKZ = kernel.getBlob(obj.id, webConfigAttr, 0, kzLangObj.id, 0);
	        	if (webConfigKZ != null && webConfigKZ.length > 0) {
		        	org.w3c.dom.Element webConfigKZElement = doc.createElement("webConfigKZ");
		        	webConfigKZElement.setTextContent(new String(webConfigKZ, "UTF-8"));
		        	interfaceElement.appendChild(webConfigKZElement);
	        	}
	        	
	        	// Обработка атрибута webConfigChanged
	        	long webConfigChanged = kernel.getLongsSingular(obj, webConfigChangedAttr, false);
	        	org.w3c.dom.Element webConfigChangedElement = doc.createElement("webConfigChanged");
	        	webConfigChangedElement.setTextContent(String.valueOf(webConfigChanged));
	        	interfaceElement.appendChild(webConfigChangedElement);

	        	// Обработка атрибута parent
	        	KrnObject parentObj = kernel.getObjectsSingular(obj.id, parentAttr.id, false);
	        	if (parentObj != null) {
		        	org.w3c.dom.Element parentElement = doc.createElement("parent");
		        	parentElement.setTextContent(parentObj.uid);
		        	interfaceElement.appendChild(parentElement);
	        	}

	        	// Обработка атрибута filtersFolder
	        	KrnObject[] filtersFolderObjs = kernel.getObjects(obj, filtersFolderAttr, 0);
	        	if (filtersFolderObjs.length > 0) {
		        	org.w3c.dom.Element filtersFoldersElement = doc.createElement("filtersFolders");
		        	for (int i = 0; i < filtersFolderObjs.length; i++) {
			        	org.w3c.dom.Element filtersFolderElement = doc.createElement("filtersFolder");
			        	filtersFolderElement.setTextContent(filtersFolderObjs[i].uid);
			        	filtersFoldersElement.appendChild(filtersFolderElement);

		        	}
		        	interfaceElement.appendChild(filtersFoldersElement);
	        	}
	        	
	        	interfacesElement.appendChild(interfaceElement);
				System.out.println("Экспортирован интерфейс '" + titleRU + "' (uid: " + obj.uid + ")");
			} catch (KrnException | DOMException | UnsupportedEncodingException e) {
				System.out.println("Ошибка при экспорте интерфейса (uid: " + obj.uid + ")");
				e.printStackTrace();
			}
    	} else {
    		for (int i = 0; i < interfaceNode.getChildCount(); i++) {
    			addInterface(doc, interfacesElement, (InterfaceNode) interfaceNode.getChildAt(i));
    		}
    	}
    }
    
    private void importInterfaces() {
    	JFileChooser fileChooser = new JFileChooser();
    	int res = fileChooser.showDialog(null, "Выбор файла");				
    	if (res == JFileChooser.APPROVE_OPTION) {
    		File file = fileChooser.getSelectedFile();
    		try {
    	        SAXParserFactory saxParseFactory = SAXParserFactory.newInstance();
    	        saxParseFactory.setValidating(false);
	            SAXParser saxParser = saxParseFactory.newSAXParser();
	            XMLReader xmlReader = saxParser.getXMLReader();
	            SAXInterfacesParser interfacesParser = new SAXInterfacesParser();
    	        xmlReader.setContentHandler(interfacesParser);
    	        xmlReader.setErrorHandler(new ErrorHandler() {
    	            private String getParseExceptionInfo(SAXParseException spe) {
    	                String systemId = spe.getSystemId();
    	                if (systemId == null) {
    	                    systemId = "null";
    	                }
    	                String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
    	                return info;
    	            }

    	            public void warning(SAXParseException spe) throws SAXException {
    	                System.err.println("Warning: " + getParseExceptionInfo(spe));
    	            }

    	            public void error(SAXParseException spe) throws SAXException {
    	                String message = "Error: " + getParseExceptionInfo(spe);
    	                throw new SAXException(message);
    	            }

    	            public void fatalError(SAXParseException spe) throws SAXException {
    	                String message = "Fatal Error: " + getParseExceptionInfo(spe);
    	                throw new SAXException(message);
    	            }
				});
	        	InputSource is = new InputSource(new FileInputStream(file));
	        	xmlReader.parse(is);
			} catch (SAXException | ParserConfigurationException | IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
	private class SAXInterfacesParser extends DefaultHandler {

    	private String uid = null;
		
		private String titleRU = null;
		private String titleKZ = null;
		private String developer = null;
		private String config = null;
		private String stringsRU = null;
		private String stringsKZ = null;
		private String webConfigRU = null;
		private String webConfigKZ = null;
		private String webConfigChanged = null;
		private String parent = null;
		private List<String> filtersFolders = new ArrayList<>();
		
        private StringBuffer elementValue = null;
	
		@Override 
		public void startDocument() throws SAXException { 
		  System.out.println("Запуск разбора xml-файла."); 
		} 
		
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (qName == "Interface") {
				uid = atts.getValue("uid");
			} else if (qName == "filtersFolders") {
				filtersFolders.clear();
			}
			elementValue = new StringBuffer();
		}
		
		@Override
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
			if (qName == "Interface") {
				processData();
				uid = null;
				titleRU = null;
				titleKZ = null;
				developer = null;
				config = null;
				stringsRU = null;
				stringsKZ = null;
				webConfigRU = null;
				webConfigKZ = null;
				webConfigChanged = null;
				parent = null;
				filtersFolders.clear();
			} else if ("titleRU".equals(qName)) {
				titleRU = elementValue.toString();
			} else if ("titleKZ".equals(qName)) {
				titleKZ = elementValue.toString();
			} else if ("developer".equals(qName)) {
				developer = elementValue.toString();
			} else if ("config".equals(qName)) {
				config = elementValue.toString();
			} else if ("stringsRU".equals(qName)) {
				stringsRU = elementValue.toString();
			} else if ("stringsKZ".equals(qName)) {
				stringsKZ = elementValue.toString();
			} else if ("webConfigRU".equals(qName)) {
				webConfigRU = elementValue.toString();
			} else if ("webConfigKZ".equals(qName)) {
				webConfigKZ = elementValue.toString();
			} else if ("webConfigChanged".equals(qName)) {
				webConfigChanged = elementValue.toString();
			} else if ("parent".equals(qName)) {
				parent = elementValue.toString();
			} else if ("filtersFolder".equals(qName)) {
				filtersFolders.add(elementValue.toString());
			}
		}
		
		private void processData() {
//			System.out.println(uid);
//			System.out.println(titleRU);
//			System.out.println(titleKZ);
//			System.out.println(developer);
//			System.out.println(config);
//			System.out.println(stringsRU);
//			System.out.println(stringsKZ);
//			System.out.println(webConfigRU);
//			System.out.println(webConfigKZ);
//			System.out.println(webConfigChanged);
//			System.out.println(parent);
			
			try {
				Kernel krn = Kernel.instance();
				KrnObject obj = krn.getObjectByUid(uid, 0);
				if (obj == null) {
					obj = krn.createObject(UIClass, uid, 0);
					System.out.println("Создание интерфейса (uid: " + uid + ")");
				} else {
					System.out.println("Обновление интерфейса (uid: " + uid + ")");
				} 
				 
				if (titleRU != null) {
					krn.setString(obj.id, titleAttr.id, 0, ruLangObj.id, titleRU, 0);
					System.out.println("\tЗапись значения атрибута '" + titleAttr.name + "' (RU)");
				}
				
				if (titleKZ != null) {
					krn.setString(obj.id, titleAttr.id, 0, kzLangObj.id, titleKZ, 0);
					System.out.println("\tЗапись значения атрибута '" + titleAttr.name + "' (KZ)");
				}
				
				if (developer != null) {
					KrnObject developerObj = krn.getObjectByUid(developer, 0);
					if (developerObj != null) {
						krn.setObject(obj.id, developerAttr.id, 0, developerObj.id, 0, false);
						System.out.println("\tЗапись значения атрибута '" + developerAttr.name + "'");
					}
				}
				
				if (config != null) {
					try {
						krn.setBlob(obj.id, configAttr.id, 0, config.getBytes("UTF-8"), 0, 0);
						System.out.println("\tЗапись значения атрибута '" + configAttr.name + "'");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
				if (stringsRU != null) {
					try {
						krn.setBlob(obj.id, stringsAttr.id, 0, stringsRU.getBytes("UTF-8"), ruLangObj.id, 0);
						System.out.println("\tЗапись значения атрибута '" + stringsAttr.name + "' (RU)");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				if (stringsKZ != null) {
					try {
						krn.setBlob(obj.id, stringsAttr.id, 0, stringsKZ.getBytes("UTF-8"), kzLangObj.id, 0);
						System.out.println("\tЗапись значения атрибута '" + stringsAttr.name + "' (KZ)");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
				if (webConfigRU != null) {
					try {
						krn.setBlob(obj.id, webConfigAttr.id, 0, webConfigRU.getBytes("UTF-8"), ruLangObj.id, 0);
						System.out.println("\tЗапись значения атрибута '" + webConfigAttr.name + "' (RU)");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				if (webConfigKZ != null) {
					try {
						krn.setBlob(obj.id, webConfigAttr.id, 0, webConfigKZ.getBytes("UTF-8"), kzLangObj.id, 0);
						System.out.println("\tЗапись значения атрибута '" + webConfigAttr.name + "' (KZ)");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				krn.setLong(obj.id, webConfigChangedAttr.id, 0, Long.parseLong(webConfigChanged), 0);
				System.out.println("\tЗапись значения атрибута '" + webConfigChangedAttr.name + "'");
				
				if (parent != null) {
					KrnObject parentObj = krn.getObjectByUid(parent, 0);
					if (parentObj != null) {
						krn.setObject(obj.id, parentAttr.id, 0, parentObj.id, 0, false);
						System.out.println("\tЗапись значения атрибута '" + parentAttr.name + "'");
					}
				}
				
				if (filtersFolders != null) {
					for (int i = 0; i < filtersFolders.size(); i++) {
						KrnObject filtersFolderObj = krn.getObjectByUid(filtersFolders.get(i), 0);
						if (filtersFolderObj != null) {
							krn.setObject(obj.id, filtersFolderAttr.id, i, filtersFolderObj.id, 0, false);
							System.out.println("\tЗапись значения атрибута '" + filtersFolderAttr.name + "'");
						}
					}
				}
				System.out.println("Обработка интерфейса завершена (uid: " + uid + ")");
			} catch (KrnException e) {
				System.out.println("Ошибка при обработке интерфейса (uid: " + uid + ")");
				e.printStackTrace();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			elementValue.append(ch, start, length);
		}
		
		@Override
		public void endDocument() {
			System.out.println("Перезагрузка дерева интерфейсов");
			tree = kz.tamur.comps.Utils.getInterfaceTree(true);
			System.out.println("Разбор xml-файла завершен.");
		}
	}
    
    /**
     * Проверяет формулы Gui компонентов
     * 
     * */
    private void checkGuiComponents() {
    	
    	//Берем выбранную вкладку в дизайнере
    	InterfaceFrame ui = ControlTabbedContent.instance().getSelectedFrame();
    	
    	//Вытаскиваем из вкладки главную панель с компонентами
    	JComponent cmp = ui.getRootPanel();
    	Component[] cList = cmp.getComponents();
    	
    	//Проходим по всем компонентам главного интерфейса и проверяем формулы
    	List<ErrRecord> errors = findErrors(cList);
    	
    	
    	final ErrTableData td = new ErrTableData();
    	td.setErrList(errors);
    	final JTable errTbl = new JTable(td);
        
        JScrollPane debugPane=new JScrollPane(errTbl);
        DesignerDialog dlg = new DesignerDialog((JFrame)tabbedContent.getTopLevelAncestor(),
                "Результат проверки интерфейса",
                false,
                debugPane);
       
        errTbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                	//Получаем имя компонента
                	String cmpName = (String)td.getValueAt(errTbl.getSelectedRow(),0);

                	//Берем имя компонента
                	String [] f = cmpName.split(" - ");
                	
                	//Получаем все элементы на компоненте
                	Component[] c = tabbedContent.getComponents();
                	
                	
                	//Парсим компоненты - ищем среди них нужное свойство 
                	OrGuiComponent cmp = findComponentByProp(c, "varName", f[0]);
                	
                	
                	//Выделяем компонент с ошибкой
                	if (cmp != null) {
                		controller.addSelection(cmp, false);
                	}
                	
                	if(errTbl.getSelectedColumn() == 1) {
                	
                		PropertyTableModel model = inspector.getModel();
                		
                		for(int i = 0; i < model.getChildCount(model.getRoot()); i++){
                			Property pr = (Property) model.getChild(model.getRoot(), i);
                			
                			//сравнить проперти в компонентах с проперти в ощибке
                			if(pr.toString().equals(f[1])) {
                				
                				//Берем значение свойства в инспекторе
                				PropertyParser pp = new PropertyParser();
                				pp.setItem(cmp);
                				Expression expr = (Expression) pp.getValue(pr);
                				
                				//Создаем собственный делегат для сохранения значений формул без использования таблиц инспектора
                				ExprDelegate d = new ExprDelegate(errTbl, pr.getId(), cmp, pr);
                				
                				//Вызываем редактор формул
                				ExpressionEditor exprEditor = new ExpressionEditor(expr.getExprString(),d);
                				DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
                                dlg.setSize(new Dimension(Utils.getMaxWindowSizeActDisplay()));
                                dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
                                dlg.show();
                                
                                //Если нажал на ок, то сохранить
                                if (dlg.isOK()) {
                                    d.setExpression(exprEditor.getExpression());
                                    
                                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                                    dlg.dispose();
                                }
                                
                			}
                		}
                	}
                		//Закрываем окно с ошибками
                    	((Window) errTbl.getTopLevelAncestor()).dispose();
                }
            }
        });
        
        
        //Размер, расположение и метод для появления диалогового окна
        dlg.setSize(700,550);
        dlg.setLocation(Utils.getCenterLocationPoint(700, 550));
        dlg.show();
        
	}
    
    /**
     * Применяем метод для прохождения по компонентам и поиску компонента нужного значением
     * 
     * @param
     * 		с - массив компонентов
     * 
     * @param
     * 		propName - имя свойства которое мы ищем
     * 
     * @param
     * 		propValue - значение свойства
     * 
     * */
    private OrGuiComponent findComponentByProp(Component[] c, String propName, String propValue) {
    	for(int i = 0; i < c.length; i++) {
    		
    		//Если текущий компонент яв-ся экземпляром класса OrGuiComponent (для исключения Spacer)
    		if(c[i] instanceof OrGuiComponent) {
    			
    			//Если текущий элемент является OrPanel, то вызвать проверку составляющих его компонентов
        		if(c[i] instanceof OrPanel) {
        			Component[] tmpPanel = ((OrPanel)c[i]).getComponents();
        			
        			//Добавляем найденные ошибки в лист
        			OrGuiComponent сmp = findComponentByProp(tmpPanel, propName, propValue);
        			if (сmp != null) {
        				return сmp;
        			}
        			
        		}
        		//Если текущий элемент является OrTabbedPane, то вызвать проверку составляющих его компонентов
        		else if(c[i] instanceof OrTabbedPane) {
        			Component[] tmpPanel = ((OrTabbedPane)c[i]).getComponents();
        			
        			OrGuiComponent сmp = findComponentByProp(tmpPanel, propName, propValue);
        			if (сmp != null) {
        				return сmp;
        			}
        			
        		}
        		
        		//Если текущий компонент не является OrPanel и OrTabbedPane, то вызвать проверку его нодов с формулами
        		else {
        			OrGuiComponent tmpComp = (OrGuiComponent)c[i];
        			
        			
        			//Берем проперти рут и ищем в нем все ноды со свойством Expr
        			PropertyNode pn = tmpComp.getProperties().getChild(propName);
        			Element e = tmpComp.getXml();
        			PropertyValue pv = tmpComp.getPropertyValue(pn);
        			
        			
        			if(pv.stringValue().equalsIgnoreCase(propValue)) {
        				return tmpComp;
        			}
        		}
        	}
    		
    	}
		return null;
	}
    
    
    
    /**
     * TableModel для таблицы с ошибками
     * 
     *
     * 
     * */
    public class ErrTableData extends AbstractTableModel {
    	
    	private List<ErrRecord> errList = new ArrayList<ErrRecord>();
    	private String [] colName = {"Компонент - Свойство", "Ошибка", "Строка"};
    	
		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return errList.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0:
					return errList.get(rowIndex).module;
			
				case 1: 
					return errList.get(rowIndex).message;
					
				case 2: 
					return errList.get(rowIndex).line;
				
				default: 
					return "";
			}
		}

		public void setErrList(List<ErrRecord> errList) {
			this.errList = errList;
		}
		
		//Метод для получения имен колонок
		public String getColumnName(int col){
			return colName[col];
		}
    }
    
    public class PropertyParser {
    	private Object item;
    	
    	public void setItem(Object item) {
    		this.item = item;
    	}
    	
    	
    	public Object getValue(Property prop) {
            Object res = "";
            if (item != null && !(prop instanceof FolderProperty)) {
                if (item instanceof OrGuiComponent) {
                    PropertyValue pv = getPropertyValue(((OrGuiComponent) item), getPath(prop));
                    if (pv != null && pv.objectValue() != null) {
                        int pt = pv.getProperty().getType();
                        Pair p;
                        switch (pt) {
                        case INTEGER:
                            res = String.valueOf(pv.intValue());
                            break;
                        case DOUBLE:
                            res = String.valueOf(pv.doubleValue());
                            break;
                        case STRING:
                        case VIEW_STRING:
                        case MSTRING:
                            res = pv.stringValue();
                            break;
                        case BOOLEAN:
                            res = pv.booleanValue();
                            break;
                        case KRNOBJECT:
                            if (pv.getKrnClassName() != null && !"".equals(pv.getKrnClassName()) && pv.getKrnObjectId() != null
                                    && !"".equals(pv.getKrnObjectId())) { // TODO оптимизировать
                                StringTokenizer st_ids = new StringTokenizer(pv.getKrnObjectId(), ",");
                                StringTokenizer st_titles = new StringTokenizer(pv.getTitle(), ",");
                                Vector<KrnObjectItem> objs1 = new Vector<KrnObjectItem>();
                                while (st_ids.hasMoreTokens()) {
                                    String id_ = st_ids.nextToken();
                                    String title_ = "";
                                    if (st_titles.hasMoreTokens()) {
                                        title_ = st_titles.nextToken();
                                    }
                                    try {
                                        KrnObject[] krn_obj = Kernel.instance().getObjectsByIds(new long[] { Long.valueOf(id_) }, -1);
                                        if (krn_obj.length > 0) {
                                            objs1.add(new KrnObjectItem(krn_obj[0], title_));
                                        }
                                    } catch (KrnException e) {
                                        e.printStackTrace();
                                    }
                                }
                                res = objs1;
                            }
                            break;
                        case REF:
                            res = pv.stringValue();
                            break;
                        case EXPR:
                            res = pv.stringValue();
                            break;
                        case COLOR:
                            res = pv.colorValue();
                            break;
                        case FONT:
                            res = pv.fontValue();
                            break;
                        case BORDER:
                            res = pv.borderValue();
                            break;
                        case IMAGE:
                            res = pv.getImageValue();
                            break;
                        case STYLEDTEXT:
                            p = pv.resourceStringValue();
                            if (p != null) {
                                res = p.second;
                            }
                            break;
                        case REPORT:
                            res = pv.reportValue();
                            break;
                        case SEQUENCE:
                            res = "";
                            break;
                        case FILTER:
                            Object value = pv.objectValue();
                            Vector<KrnObjectItem> objs = new Vector<KrnObjectItem>();
                            if (value instanceof FilterRecord) {
                                FilterRecord fr = (FilterRecord) value;
                                objs.add(new KrnObjectItem(fr.getKrnObject(), fr.getTitle()));
                            } else if (value instanceof FilterRecord[]) {
                                for (FilterRecord fr : (FilterRecord[]) value) {
                                    objs.add(new KrnObjectItem(fr.getKrnObject(), fr.getTitle()));
                                }
                            }
                            res = objs;
                            break;
                        case PMENUITEM:
                            res = pv.menuItemsValues();
                            break;
                        case ENUM:
                        case ENUM_TOOL_TIP:
                            res = pv.stringValue();
                            break;
                        case RSTRING:
                            p = pv.resourceStringValue();
                            if (p != null) {
                                res = p.second;
                            }
                            break;
                        case KRNOBJECT_ID:// получение идентификаторов выбранных объектов
                            String krnObj = pv.getKrnObjectId();
                            if (krnObj != null && !krnObj.isEmpty()) {
                                // удаление левой части идентификатора
                                res = krnObj.replaceAll("\\d+\\.", "");
                            }
                            break;
                        case HTML_TEXT:
                            if (pv.objectValue() instanceof Expression) {
                                res = pv.objectValue();
                            } else {
                                p = pv.resourceStringValue();
                                if (p != null) {
                                    res = p.second;
                                }
                            }
                            break;
                        case GRADIENT_COLOR:
                            res = pv.objectValue();
                            break;
                        default:
                            res = pv.objectValue();
                            break;
                        }
                    } else if (pv != null && pv.getProperty() != null) {
                        res = pv.getProperty().getDefaultValue();
                    }
                }
                if (res == null)
                    res = "";
                if (prop instanceof ComboProperty) {
                    res = ((ComboProperty) prop).getItem(res.toString());
                } else if (prop instanceof ComboToolTipProperty) {
                    res = ((ComboToolTipProperty) prop).getItem(res.toString());
                } else if (prop instanceof ExprProperty || (prop instanceof KrnOrExprProperty && res instanceof String)
                        || (prop instanceof TreeOrExprProperty && res instanceof String)) {
                    res = new Expression(res.toString());
                }
            }
            return res;
        }
    	
    	private String getPath(Property prop){
            if (prop==null) {
                return null;
            }
            String res= prop.getId();
            Property prop_=prop.getParent();
            while(prop_.getId()!=null && !"Root".equals(prop_.getId())){
                 res=prop_.getId()+"."+res;
                 prop_=prop_.getParent();
            }
            return res;
        }
    	
    	private PropertyValue getPropertyValue(OrGuiComponent c, String propId) {
            PropertyValue res = null;
            if (propId != null) {
                StringTokenizer st = new StringTokenizer(propId, ".");
                PropertyNode pn = c.getProperties();
                while (pn != null && st.hasMoreTokens()) {
                    pn = pn.getChild(st.nextToken());
                }
                if (pn != null) {
                    res = c.getPropertyValue(pn);
                }
            }
            return res;
        }
    }
    
    
    

    /**
     * Находит ошибки в компонентах находящихся на интерфейсе
     * 
     * @param
     *  	cList - Массив компонентов
     * @return
     * 		Лист с ошибками в выражениях
     */
    private List<ErrRecord> findErrors(Component[] cList) {
		List<ErrRecord> errorList = new ArrayList<ErrRecord>();
		for (int i = 0; i < cList.length; i++) {
			if (cList[i] instanceof OrGuiComponent) {
				if (cList[i] instanceof OrPanel || cList[i] instanceof OrTabbedPane) {
					Component[] comps = ((Container) cList[i]).getComponents();
					List<ErrRecord> tmpList = findErrors(comps);
					if (tmpList.size() > 0) {
						errorList.addAll(tmpList);
					}
				} else if (cList[i] instanceof OrTable) {
					OrTable table = (OrTable) cList[i];
					int count =  table.getColumnCount();
					for (int j = 0; j < count; j++) {
						OrColumnComponent column = table.getColumnAt(j);
						if (column != null) {	// Так как OrTreeTable2 для индекся 0 возвращает всегда null
							PropertyNode pn = column.getProperties();
							List<PropertyNode> tmpList = findPropertiesByType(pn, 9);
							if (tmpList.size() > 0) {
								errorList.addAll(getExpErrors(tmpList, column));
							}
						}
					}
				} else {
					OrGuiComponent tmpComp = (OrGuiComponent) cList[i];
					PropertyNode pn = tmpComp.getProperties();
					List<PropertyNode> tmpList = findPropertiesByType(pn, 9);
					if (tmpList.size() > 0) {
						errorList.addAll(getExpErrors(tmpList, tmpComp));
					}
				}
			}
		}
    	return errorList;
    }
    
    
    /**
     * Метод для поиска свойства типа type в структуре свойств компонента
     * 	@param 
     * 		compPn - где ищем.
     * 	@param
     * 		type - тип свойства
     *  
     * */
    private List<PropertyNode> findPropertiesByType(PropertyNode compPn, int type) {
    	List<PropertyNode> nodeList = new ArrayList<PropertyNode>();
    	
    	for(int i = 0; i < compPn.getChildCount(); i++) {
    		PropertyNode tmpPn = compPn.getChildAt(i);
    		if(tmpPn.getType() == type) {
    			nodeList.add(tmpPn);
    		} else if(tmpPn.getChildCount() > 0) {
    			nodeList.addAll(findPropertiesByType(tmpPn, type));
    		}
    	}
		return nodeList;
    }
    
    /**
     * Поиск ошибок в выражениях
     * @param 
     * 		listWithExpr - лист свойств которые необходимо проверить
     * @param
     * 		tmpComp - XML элемента
     * 
     * @return
     * 		Возвращает лист с ошибками
     * */
    private List<ErrRecord> getExpErrors(List<PropertyNode> listWithExpr, OrGuiComponent tmpComp) {
    	List<ErrRecord> errors = new ArrayList<ErrRecord>();

    	for(int i = 0; i < listWithExpr.size(); i++) {
    		PropertyNode pn = listWithExpr.get(i);
    		Element e = tmpComp.getXml();
			PropertyValue pv = PropertyHelper.getPropertyValue(pn, e, tabbedContent);
			ExpressionDebuger debugger = new ExpressionDebuger(new ClientOrLang(tabbedContent));
			PropertyValue varNameProp = tmpComp.getPropertyValue(tmpComp.getProperties().getChild("varName"));
			
			//Получаем имя компонента и проверяемое свойство. Отображаем его ввиде строкиы
			String testedCmp = varNameProp.toString() + " - " + pn.getFullName();

			debugger.debugExpression(testedCmp ,(String)pv.getValue());
			List<ErrRecord> l = debugger.getErrors();
			if(l.size() > 0) {
				errors.addAll(l);
			}
    	}
    	return errors;
    }
    


	private void showDebug() {
        if (debugPanel == null) {
            debugPanel = new DebugPanel();
            debugPanel.setDf(this);
        }
        basicSplitPane.setRightComponent(debugPanel);
        basicSplitPane.setDividerLocation(0.7);
        basicSplitPane.setDividerSize(3);
    }

    public void closeDebug() {
        basicSplitPane.remove(debugPanel);
        basicSplitPane.setDividerSize(0);
        basicSplitPane.setDividerLocation(1.0);
    }

    public int processExit() {
        StringBuilder mess = new StringBuilder("Интерфейсы:\n");
        boolean isMessage = false;
        for (int i = 0; i < tabbedContent.getTabCount(); i++) {
            if (tabbedContent.isInterface(i) && tabbedContent.isModifiedIfr(i)) {
                isMessage = true;
                String str = tabbedContent.getTitleAt(i);
                mess.append("\"").append(str).append("\"\n");
            }
        }
        if (isMessage) {
            mess.append("Сохранить изменения?");
            int res = MessagesFactory.showMessageDialog((JFrame) this.getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE,
                    mess.toString());
            if (res == BUTTON_YES) {
                save();
                return BUTTON_YES;
            } else if (res == BUTTON_NO) {
                return BUTTON_NO;
            } else {
                return BUTTON_CANCEL;
            }
        }
        inspector.processExit();
        return BUTTON_NOACTION;
    }

    private void save() {
        try {
            CursorToolkit.startWaitCursor(this);
            for (int i = 0; i < tabbedContent.getTabCount(); i++) {
                if (tabbedContent.isModifiedIfr(i)) {
                    InterfaceFrame frame = (InterfaceFrame) tabbedContent.getFrameAt(i);
                    frame.save(null);
                    tabbedContent.setModifiedIfr(false, i);
                }
            }
            CursorToolkit.stopWaitCursor(this);
        } catch (Exception e) {
            CursorToolkit.stopWaitCursor(this);
            e.printStackTrace();
        }
    }

    public void load(KrnObject object, KrnObject objectNode) {
        load(object, false, objectNode);
    }

    public void load(KrnObject object, boolean joinThread, KrnObject objectNode) {
        if (object != null) {
            if (!tabbedContent.isExistIfr(object.id)) {
//            	try {
//	            	if (kernel.getBindingModuleToUserMode()) {
//	    				KrnObject[] developerObjs = kernel.getObjects(object, "developer", 0);
//	                	if (developerObjs.length > 0) {
//	                		long ownerId = developerObjs[0].id;
//	                		long currentUserId = kernel.getUserSession().userObj.id;
//	                		if (ownerId != currentUserId) {
//	                			KrnObject userObj = kernel.getObjectById(ownerId, 0);
//	                			if (userObj != null) {	// Владелец интерфейса существует
//	                    			KrnClass userCls = kernel.getClassByName("User");
//	                    			KrnAttribute userNameAttr = kernel.getAttributeByName(userCls, "name");
//	                    			String userName = kernel.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
//	                                MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного интерфейса является пользователь " + userName + "!");
//	                			}
//	                		}
//	                	}
//	                }
//				} catch (KrnException e) {
//					e.printStackTrace();
//				}
                boolean readOnly = !kernel.getUser().hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT);
                KrnObject lang = kernel.getInterfaceLanguage();
                long langId = (lang != null) ? lang.id : 0;
                String name = "Безымянный";
                try {
                    String[] strs = kernel.getStrings(object, "title", langId, 0);
                    if (strs != null && strs.length > 0) {
                        name = strs[0];
                    }
                    if (!readOnly) {
                    	UserSessionValue us = kernel.vcsLockObject(object.id);
                        if (us != null) {
                            if (ifcVcsLock(name, us) == BUTTON_YES) {
                                readOnly = true;
                            } else {
                                return;
                            }
                        }
                        if(!readOnly){
	                        us = kernel.blockObject(object.id);
	                        if (us != null) {
	                            if (ifcLock(name, us) == BUTTON_YES) {
	                                readOnly = true;
	                            } else {
	                                return;
	                            }
	                        }
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                actionLabel.setIcon(getImageIcon("OpenAni"));
                loadingInterfaceID.add(object.id);
                IfcLoader loader = new IfcLoader(object, readOnly, objectNode);
                InterfaceActionsConteiner.instance(object.getId(), name);
                loader.start();
                if (joinThread) {
                    loader.get();
                }
            }
         // Сохранение в историю
            String name = "Безымянный";
            KrnObject lang = kernel.getInterfaceLanguage();
            long langId = (lang != null) ? lang.id : 0;
            try {
            String[] strs = kernel.getStrings(object, "title", langId, 0);
            if (strs != null && strs.length > 0) {
            	name = strs[0];
            }
            HistoryWithDate hwd = new HistoryWithDate(object, new Date());
            kernel.getUser().addIfcInHistory(hwd, name);
            } catch(Exception e) {e.printStackTrace();}
        }
    }

    public void load() {
        try {
            tree.setShowPopupEnabled(true);
            OpenElementPanel pan = new OpenElementPanel(tree);
            pan.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            DesignerDialog dlg = new DesignerDialog((JFrame) this.getTopLevelAncestor(), "Открытие интерфейса", pan);
            pan.setSearchUIDPanel(true);
            if (lastNode != null) {
                tree.setSelectedNode(lastNode.getKrnObj());
                pan.getTree().requestFocusInWindow();
            }
            dlg.show();
            if (dlg.getResult() == BUTTON_OK) {
                AbstractDesignerTreeNode node = pan.getTree().getSelectedNode();
                if (node == null || !node.isLeaf())
                    return;
                KrnObject ui = pan.getNodeObj(node);
                lastNode = node;
                if (!tabbedContent.isExistIfr(ui.id)) {
//                	if (kernel.getBindingModuleToUserMode()) {
//        				KrnObject[] developerObjs = kernel.getObjects(ui, "developer", 0);
//                    	if (developerObjs.length > 0) {
//                    		long ownerId = developerObjs[0].id;
//                    		long currentUserId = kernel.getUserSession().userObj.id;
//                    		if (ownerId != currentUserId) {
//                    			KrnObject userObj = kernel.getObjectById(ownerId, 0);
//                    			if (userObj != null) {	// Владелец интерфейса существует
//                        			KrnClass userCls = kernel.getClassByName("User");
//                        			KrnAttribute userNameAttr = kernel.getAttributeByName(userCls, "name");
//                        			String userName = kernel.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
//                                    MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного интерфейса является пользователь " + userName + "!");
//                    			}
//                    		}
//                    	}
//                    }
                    boolean readOnly = !kernel.getUser().hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT);
                    if (!readOnly) {
                        try {
                        	UserSessionValue us = kernel.vcsLockObject(ui.id);
                            if (us != null) {
                                if (ifcVcsLock(node.toString(), us) == BUTTON_YES) {
                                    readOnly = true;
                                } else {
                                    return;
                                }
                            }
                            if(!readOnly){
	                            us = kernel.blockObject(ui.id);
	                            if (us != null) {
	                                if (ifcLock(node.toString(), us) == BUTTON_YES) {
	                                    readOnly = true;
	                                } else {
	                                    return;
	                                }
	                            }
                            }
                        } catch (KrnException e) {
                            e.printStackTrace();
                        }
                    }
                    actionLabel.setIcon(getImageIcon("OpenAni"));
                    loadingInterfaceID.add(ui.id);
                    new IfcLoader(ui, readOnly, null).start();
                    // Действие "Открытие интерфейса"
                    InterfaceActionsConteiner.instance(ui.getId(), node.toString());
                }
                // Cохранение в историю
                HistoryWithDate hwd = new HistoryWithDate(ui, new Date());
                kernel.getUser().addIfcInHistory(hwd, node.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Вывод сообщения о том что интерфейс заблокирован и вопрос об его открытии в режиме просмотра.
     * 
     * @param name
     *            имя интерфейса.
     * @param us
     *            пользователь, заблокировавший интерфейс.
     * @return результат выбора пользователя.
     */
    private int ifcLock(String name, UserSessionValue us) {
        StringBuilder mess = new StringBuilder();
        mess.append("Интерфейс '").append(name).append("' заблокирован!\nПользователь: ").append(us.name).append("\nIP адрес: ")
                .append(us.ip).append("\nИмя компьютера: ").append(us.pcName).append("\nОткрыть интерфейс в режиме просмотра?");
        return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                mess.toString(), 235, 130);
    }

    /**
     * Вывод сообщения о том что интерфейс уже редактируется другим пользователем и вопрос об его открытии в режиме просмотра.
     * 
     * @param name
     *            имя интерфейса.
     * @param us
     *            пользователь, начавший редактировать интерфейс.
     * @return результат выбора пользователя.
     */
    private int ifcVcsLock(String name, UserSessionValue us) {
        StringBuilder mess = new StringBuilder();
        mess.append("Интерфейс '").append(name).append("' редактируется!\nПользователь: ").append(us.name).append("\nОткрыть интерфейс в режиме просмотра?");
        return MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                mess.toString(), 235, 130);
    }
    public static List<Long> getLoadingInterfaceID() {
        return loadingInterfaceID;
    }

    public void setInterfaceLanguage() {
        LangItem li = (LangItem) langSelector.getSelectedItem();
        if (li != null) {
            for (int i = 0; i < tabbedContent.getComponentCount(); i++) {
                OrFrame frm = tabbedContent.getFrameAt(i);
                if (frm != null)
                	frm.setInterfaceLang(li.obj);
            }
            inspector.getPropTable().repaint();
        }
    }

    public KrnObject getInterfaceLang() {
        return ((LangItem) langSelector.getSelectedItem()).obj;
    }

    public InterfaceNode create() {
        tree.setShowPopupEnabled(false);
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.CREATE_NEW_ELEMENT_TYPE, null, tree);
        DesignerDialog dlg = new DesignerDialog((JFrame) this.getTopLevelAncestor(), "Создание интерфейса", cp);
        dlg.pack();
        dlg.setOkEnabled(false);
        dlg.show();
        if (dlg.getResult() == BUTTON_OK) {
            String ifcName = cp.getElementName();
            if (ifcName == null) {
                JOptionPane.showMessageDialog(this, "Неверное имя интерфейса!", "Сообщение", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    tree.removeTreeSelectionListener(cp);
                    InterfaceTree.InterfaceTreeModel model = (InterfaceTree.InterfaceTreeModel) tree.getModel();
                    AbstractDesignerTreeNode node = model.createChildNode(ifcName);
                    InterfaceFrame ifcFrame = new InterfaceFrame(node.getKrnObj());
                    designerPanel = ifcFrame.getRootPanel();
                    ((OrGuiComponent) designerPanel).getXml().setAttribute("elementCount", "0");
                    setInterfaceLanguage();
                    validate();
                    repaint();
                    actionLabel.setIcon(getImageIcon("Opened"));
                    return (InterfaceNode) node;
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    class IfcLoader extends SwingWorker {

        private KrnObject ui;
        private KrnObject nodeObj;
        private boolean isReadOnly = false;

        public IfcLoader(KrnObject ui, boolean isRO, KrnObject nodeObj) {
            super();
            this.ui = ui;
            this.nodeObj = nodeObj;
            this.isReadOnly = isRO;
        }

        public Object construct() {
            LangItem li = (LangItem) langSelector.getSelectedItem();
            CursorToolkit.startWaitCursor(DesignerFrame.this);
            InterfaceFrame frm = new InterfaceFrame(ui);
            frm.setReadOnly(isReadOnly);
            frm.setInterfaceLang(li.obj);
            try {
                frm.load(pBar);
                componentTree.setCellRenderer(new ComponentsTreeRenderer(frm));
                tabbedContent.addTab(frm, nodeObj);
                validate();
                repaint();
                actionLabel.setIcon(getImageIcon("Opened"));
                CursorToolkit.stopWaitCursor(DesignerFrame.this);
            } catch (Exception e) {
                CursorToolkit.stopWaitCursor(DesignerFrame.this);
                e.printStackTrace();
                actionLabel.setIcon(getImageIcon("OpenErr"));
            }
            inspector.setObject(new GuiComponentItem(frm.getRootPanel(), designerFrame));
            return null;
        }
    }

    public void propertyModified(OrGuiComponent c) {
        disarmed();
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {
        if (c instanceof OrSplitPane && "dividerLocation".equals(property.getName())) {
            inspector.setObject(new GuiComponentItem(c, this));
        }
    }

    private void disarmed() {
        unselectBut.setSelected(true);
        controller.setCurrCompCalss(null);
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    private void splitManipulator(ItemEvent e, Object src) {
        if (src instanceof JCheckBoxMenuItem) {
            JCheckBoxMenuItem im = (JCheckBoxMenuItem) src;
            if (im == compsItem) {
                if (inspectorItem.isSelected()) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        secondSplitPane.setDividerSize(3);
                        secondSplitPane.setDividerLocation(0.5);
                    } else {
                        secondSplitPane.setDividerLocation(0.0);
                        secondSplitPane.setDividerSize(0);
                    }
                } else {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        mainSplitPane.setDividerLocation(0.3);
                        mainSplitPane.setDividerSize(3);
                        secondSplitPane.setDividerSize(0);
                        secondSplitPane.setDividerLocation(1.0);
                    } else {
                        mainSplitPane.setDividerSize(0);
                        mainSplitPane.setDividerLocation(0.0);
                    }
                }
            } else if (src == inspectorItem) {
                if (compsItem.isSelected()) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        secondSplitPane.setDividerSize(3);
                        secondSplitPane.setDividerLocation(0.5);
                    } else {
                        secondSplitPane.setDividerLocation(1.0);
                        secondSplitPane.setDividerSize(0);
                    }
                } else {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        mainSplitPane.setDividerSize(3);
                        mainSplitPane.setDividerLocation(0.3);
                        secondSplitPane.setDividerLocation(0.0);
                        secondSplitPane.setDividerSize(0);
                    } else {
                        mainSplitPane.setDividerSize(0);
                        mainSplitPane.setDividerLocation(0.0);
                    }
                }
            }
        }
    }

    private void preview() throws Exception {
        if (tabbedContent.getTabCount() > 0) {
            new DesignerPreviewFrame(tabbedContent.getOrGuiComponent().getXml(), tabbedContent.getTitleAt(tabbedContent
                    .getSelectedIndex()), getInterfaceLang().id);
        }
    }
    
    private void previewWeb() throws Exception {
    	if (tabbedContent.getTabCount() > 0) {
    		new DesignerWebPreviewFrame(tabbedContent.getSelectedFrame().getUiObject(), tabbedContent.getTitleAt(tabbedContent.getSelectedIndex()), getInterfaceLang().id, projectsCombo.getSelectedIndex());
    	}
    }

    public DesignerStatusBar getStatusBar() {
        return statusPanel;
    }

    private void saveOnDisk() {
        JFileChooser fc = kz.tamur.comps.Utils.createSaveChooser(Constants.XML_FILTER);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
            	String name = ((OrFileChooserUI) fc.getUI()).getFileName() + ".xml";
            	name = name.replace("/", "").replace("\\", "");
                File f = Funcs.getCanonicalFile(Funcs.getCanonicalFile(fc.getCurrentDirectory().getAbsolutePath()), name);
                XMLOutputter outputter = new XMLOutputter();
                outputter.getFormat().setEncoding("UTF-8");
                FileOutputStream writer = new FileOutputStream(f);
                outputter.output(tabbedContent.getOrGuiComponent().getXml(), writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showClasses() {
        if (admFrame == null) {
            admFrame = new AdminFrame();
            admFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        if (!admFrame.isShowing()) {
            admFrame.show();
        }
    }

    public void setToolButtonsEnabled(boolean isEnabled) {
        findBut.setEnabled(isEnabled);
        findItem.setEnabled(isEnabled);
        replaceBut.setEnabled(isEnabled);
        replaceItem.setEnabled(isEnabled);
        copyBut.setEnabled(isEnabled);
        copyItem.setEnabled(isEnabled);
        pasteBut.setEnabled(isEnabled);
        pasteItem.setEnabled(isEnabled);
        cutBut.setEnabled(isEnabled);
        cutItem.setEnabled(isEnabled);
        previewBut.setEnabled(isEnabled);
        viewHistoryBut.setEnabled(isEnabled);
        projectsCombo.setEnabled(isEnabled);
        previewWebBut.setEnabled(isEnabled);
        fullScreenBut.setEnabled(isEnabled);
        previewItem.setEnabled(isEnabled);
        previewWebItem.setEnabled(isEnabled);
        deleteBut.setEnabled(isEnabled);
        deleteItem.setEnabled(isEnabled);
        saveBut.setEnabled(isEnabled);
        saveItem.setEnabled(isEnabled);
        saveOnBut.setEnabled(isEnabled);
        saveOnItem.setEnabled(isEnabled);
        findCompBut.setEnabled(isEnabled);
        debugBtn.setEnabled(isEnabled);
        findComponentItem.setEnabled(isEnabled);
        checkBtn.setEnabled(isEnabled);
    }

    public void showSearchDialog() {
        if (!findBut.isEnabled()) {
            return;
        }
        if (searchPanel == null) {
            searchPanel = new SearchPanel();
        }
        searchPanel.setDf(this);
        searchPanel.setResultComponent(null);
        searchPanel.setResultContainer(null);

        SearchDialog dlg = new SearchDialog((JFrame) this.getTopLevelAncestor(), "Поиск", searchPanel);
        dlg.setResizable(false);
        dlg.getFindBtn().addActionListener(searchPanel);
        dlg.getClearBtn().addActionListener(searchPanel);
        dlg.show();
        OrGuiComponent c = searchPanel.getResultComponent();
        if (c != null) {
            tabbedContent.setSelectedComponent(searchPanel.getResultContainer());
            controller.addSelection(c, false);
        }
    }

    public void showReplaceDialog() {
        if (!replaceBut.isEnabled()) {
            return;
        }
        if (replacePanel == null) {
            replacePanel = new ReplacePanel();
            replacePanel.addPropertyListener(tabbedContent);
        }
        replacePanel.setDf(this);
        replacePanel.setResultComponent(null);
        replacePanel.setResultContainer(null);

        DesignerDialog dlg = new DesignerDialog((JFrame) this.getTopLevelAncestor(), "Найти и заменить", replacePanel);
        dlg.setOnlyOkButton();
        dlg.setResizable(false);
        dlg.setOkText("Закрыть");

        dlg.show();
        OrGuiComponent c = replacePanel.getResultComponent();
        if (c != null) {
            try {
                tabbedContent.setSelectedComponent(replacePanel.getResultContainer());
                controller.addSelection(c, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void reloadTree() {
    	tree = kz.tamur.comps.Utils.getInterfaceTree(true);
    }

    public InterfaceTree getInterfaceTree() {
        return tree;
    }

    public void hideInternalDialog(int type) {
        switch (type) {
        case DesignerInternalDialog.TREE_DLG:
            if (compsItem.isSelected()) {
                compsItem.doClick();
            }
            break;
        case DesignerInternalDialog.INSPECTOR_DLG:
            if (inspectorItem.isSelected()) {
                inspectorItem.doClick();
            }
            break;
        }
    }

    public void setCompsTreeMode(boolean isFloat) {
        if (isFloat) {
            multiInspectorItem.setEnabled(false);
            compsTreeDlg = new ComponentsTreeDlg((Frame) getTopLevelAncestor(), "Компоненты", this);
            compsTreeDlg.setContentPane((Container) treeIntDlg.getContent());
            if (inspectorItem.isSelected()) {
                secondSplitPane.setDividerLocation(0.0);
                secondSplitPane.setDividerSize(0);
            } else {
                mainSplitPane.setDividerLocation(0.0);
                mainSplitPane.setDividerSize(0);
            }
            compsTreeDlg.setSize(300, 500);
            compsTreeDlg.setLocation(0, 0);
            compsItem.setSelected(false);
            compsTreeDlg.show();
        } else {
            if (compsTreeDlg != null) {
                Container cont = compsTreeDlg.getContentPane();
                if (cont != null) {
                    treeIntDlg.addContent(cont);
                    compsTreeDlg.dispose();
                    if (inspectorItem.isSelected()) {
                        multiInspectorItem.setEnabled(true);
                    }
                }
            } else {
                treeIntDlg.addContent(multiInspectorDlg.getC1());
            }
            if (inspectorItem.isSelected()) {
                secondSplitPane.setDividerLocation(0.5);
                secondSplitPane.setDividerSize(3);
            } else {
                mainSplitPane.setDividerLocation(0.3);
                mainSplitPane.setDividerSize(3);
                secondSplitPane.setDividerLocation(1.0);
            }
            compsItem.setSelected(true);
        }
    }

    public void setInspector(boolean isInspector) {
        if (isInspector) {
            inspectorItem.setSelected(false);
            if (!compsItem.isSelected()) {
                mainSplitPane.setDividerLocation(0.0);
                mainSplitPane.setDividerSize(0);
            }
        } else {
            secondSplitPane.add(inspector);
            if (compsItem.isSelected()) {
                multiInspectorItem.setEnabled(true);
                secondSplitPane.setDividerLocation(0.5);
                secondSplitPane.setDividerSize(3);
            } else {
                mainSplitPane.setDividerLocation(0.3);
                mainSplitPane.setDividerSize(3);
                secondSplitPane.setDividerLocation(0.0);
            }
            inspectorItem.setSelected(true);
        }
    }

    public void searchComponentByTitle() {
        componentTree.requestFocusInWindow();
        String title = "";
        SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Поиск компонента", sip);
        dlg.show();
        if (dlg.isOK()) {
            title = sip.getSearchText();
            searchComponentByTitle(title, true);
        }
    }

    public void searchComponentByTitle(String title, boolean showUnsuccess) {
        searchList.clear();
        searchIndex = 0;
        if (showUnsuccess)
            search(tabbedContent.getOrGuiComponent(), title, tabbedContent.getOrGuiComponent());
        else
            search(tabbedContent.getOrGuiComponent(tabbedContent.getTabCount() - 1), title,
                    tabbedContent.getOrGuiComponent(tabbedContent.getTabCount() - 1));
        if (searchList.size() > 0) {
            OrGuiComponent c = (OrGuiComponent) searchList.get(0);
            if (c != null) {
                ComponentsTreeModel m = (ComponentsTreeModel) componentTree.getModel();
                componentTree.setSelectionPath(m.getPathToRoot(c));
                componentTree.scrollPathToVisible(m.getPathToRoot(c));
                componentTree.requestFocusInWindow();
            }
        } else {
            if (showUnsuccess) {
                MessagesFactory.showMessageNotFound(getTopLevelAncestor());
            }
        }

    }

    public void searchNext() {
        if (searchList.size() > 0) {
            searchIndex++;
            if (searchIndex < searchList.size()) {
                OrGuiComponent c = (OrGuiComponent) searchList.get(searchIndex);
                if (c != null) {
                    ComponentsTreeModel m = (ComponentsTreeModel) componentTree.getModel();
                    componentTree.setSelectionPath(m.getPathToRoot(c));
                    componentTree.scrollPathToVisible(m.getPathToRoot(c));
                    componentTree.requestFocusInWindow();
                }
            } else {
                MessagesFactory.showMessageNotFound(getTopLevelAncestor());
            }
        }
    }

    private void search(OrGuiComponent c, String title, OrGuiComponent container) {
        PropertyNode pn = c.getProperties().getChild("title");
        if (pn != null) {
            PropertyValue pv = c.getPropertyValue(pn);
            if (!pv.isNull() && pv.toString().startsWith(title)) {
                searchList.add(c);
            }
            if (c instanceof OrTable) {
                OrTable t = (OrTable) c;
                for (int i = 0; i < t.getTableComponentCount(); i++) {
                    OrTableColumn tc = t.getTableComponent(i);
                    if (tc.getTitle().startsWith(title)) {
                        searchList.add(tc);
                    }
                }
            }
            if (c instanceof OrGuiContainer && !(c instanceof OrScrollPane)) {
                Container cont = (Container) c;
                for (int i = 0; i < cont.getComponentCount(); i++) {
                    Component comp = cont.getComponent(i);
                    if (comp instanceof OrGuiComponent) {
                        search((OrGuiComponent) comp, title, container);
                    }
                }
            } else if (c instanceof OrGuiContainer && c instanceof OrScrollPane) {
                OrScrollPane cont = (OrScrollPane) c;
                for (int i = 0; i < cont.getOrComponentCount(); i++) {
                    Component comp = cont.getOrComponent(i);
                    if (comp instanceof OrGuiComponent) {
                        search((OrGuiComponent) comp, title, container);
                    }
                }
            }
        }
    }

    private void showNoteEditor() {
        NoteEditor editor = new NoteEditor();
        FrameTemplate dlg = new FrameTemplate();
        dlg.setIconImage(getImageIcon("iconHelpEditor").getImage());
        dlg.setJMenuBar(editor.getMenuBar());
        dlg.setTitle("Редактор подсказок и помощи");
        dlg.getContentPane().add(editor);
        dlg.show();
    }

    public JProgressBar getProgressBar() {
        return pBar;
    }

    private void setMultiInspector() {
        multiInspectorDlg = new MultiInspectorDlg((Frame) getTopLevelAncestor(), "Компоненты и Инспектор", this);
        Component c1 = treeIntDlg.getContent();
        multiInspectorDlg.setComponents(c1, inspector);
        inspectorItem.setSelected(false);
        compsItem.setSelected(false);
        multiInspectorDlg.setSize(new Dimension(800, 600));
        multiInspectorDlg.setLocation(0, 0);
        multiInspectorDlg.setVisible(true);
    }

    /**
     * Перегружает свойства компонента. Используется в компонентах с динамическими свойствами
     * 
     * @param c
     */
    public void reloadProp(OrGuiComponent c) {
        controller.updateProperties(c);
    }

    public void setModified(OrGuiComponent c) {
        tabbedContent.propertyModified(c);
    }
    
    public void setModified(OrGuiComponent c, InterfaceFrame ifc) {
        tabbedContent.propertyModified(c, ifc);
    }

    public boolean isPlainMode() {
        return inspector.isPlainMode();
    }

    /**
     * Проверяет находится ли инспектор свойств в плавающем режиме
     */
    public boolean isInspectorFloat() {
        return inspector.isInspectorFloat();
    }

    /**
     * Метод
     * Возвращает позицию инспектора свойств на экране
     * 
     * @return координаты <code>Point</code>
     */
    public Point getLocationInspector() {
        return inspector.getLocationOnScreen();
    }

    public long getRusLang() {
        return langSelector.getRusLang().id;
    }

    public long getKazLang() {
        return langSelector.getKazLang().id;
    }

    /**
     * Удаляет из дизайнера открытый в нём интерфейс
     * 
     * @param id
     *            ID интерфейса
     */
    public void removeCurrentTab(Long id) {
        if (tabbedContent.isExistIfr(id)) {
            try {
                tabbedContent.closeCurrent();
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    public void showHistory(JComponent swObj) {
        JPopupMenu pm = new JPopupMenu();
        Iterator it = kernel.getUser().config.getIfcHistoryObjs().iterator();
        JMenuItem item;
        while (it.hasNext()) {
            final KrnObject objTmp = (KrnObject) it.next();
            pm.add(item = createMenuItem("[" + objTmp.id + "]-" + kernel.getUser().config.getIfcName(objTmp)));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    load(objTmp, null);
                }
            });
        }
        pm.show(swObj, swObj.getX(), swObj.getY());
    }

    /**
     * Получить basicPanel.
     * 
     * @return the basic panel
     */
    public JPanel getBasicPanel() {
        return basicPanel;
    }

    /**
     * Получить secondSplitPane.
     * 
     * @return the secondSplitPane
     */
    public JSplitPane getSecondSplitPane() {
        return secondSplitPane;
    }

    /**
     * Пересобрать интерфейс.
     */
    public void rebuildPanels() {
        tabbedContent.setServiceControlMode(false);
        remove(basicPanel);
        add(basicPanel, BorderLayout.NORTH);
        basicPanel.setVisible(true);

        mainSplitPane.remove(secondSplitPane);
        mainSplitPane.setLeftComponent(secondSplitPane);

        mainSplitPane.remove(tabbedContent);
        mainSplitPane.setRightComponent(tabbedContent);
        mainSplitPane.revalidate();

        revalidate();
    }

    public void showUUID(String uid) {
        uidView.setText(uid);
    }

    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText("Пользователь ");
        currentUserLable.setText(Or3Frame.getCurrentDbName());
    }
}