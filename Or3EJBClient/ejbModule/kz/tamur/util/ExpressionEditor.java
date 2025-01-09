package kz.tamur.util;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static kz.tamur.comps.Constants.INSETS_0;
import static kz.tamur.comps.Utils.getCenterLocationPoint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tigris.gef.base.SelectionReshape;
import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;

import kz.tamur.Or3Frame;
import kz.tamur.admin.AttrPropPanel;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.admin.DownloadFilePanel;
import kz.tamur.admin.MethodPanel;
import kz.tamur.admin.TriggersPanel;
import kz.tamur.comps.Constants;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.EmptyFrame;
import kz.tamur.guidesigner.ExpressionStatusBar;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ReportEditor.DataCellEditor;
import kz.tamur.guidesigner.boxes.BoxPropertyEditor;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.ServiceItem;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.service.cmd.CmdViewItem;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.ServiceNodeIfc;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.lang.ErrRecord;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.inspector.BoxOrExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.ExprDelegate;
import kz.tamur.or3.client.props.inspector.ExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.HTMLEditorDelegate;
import kz.tamur.or3.client.props.inspector.TreeOrExprEditorDelegate;
import kz.tamur.or3.client.util.GuiUtil;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.MenuItemEditorPanel.MenuItemPropertyEditor;
import kz.tamur.util.colorchooser.OrColorChooser;

public class ExpressionEditor extends JPanel implements ActionListener {
    public EditorPane editor;
    public String text = "";
    public boolean onChanged = false;
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private ExpressionStatusBar statusBar = new ExpressionStatusBar();
    private JButton pathBtn = ButtonsFactory.createToolButton("classes", "Выбор пути");
    private JButton uidBtn = ButtonsFactory.createToolButton("UID1", "Выбор ID объекта");
    private JButton UI = ButtonsFactory.createToolButton("HyperPopup", "Выбор интерфейса");
    private JButton UserBtn = ButtonsFactory.createToolButton("userNode", "Выбор пользователя");
    private JButton printBut = ButtonsFactory.createToolButton("ReportPrinter", "Печать");
    private JButton saveBut = ButtonsFactory.createToolButton("SaveIcon", "Сохранить");
    private JButton saveOnBut = ButtonsFactory.createToolButton("SaveOnDisk", "Сохранить на диске");
    private JButton openFromBut = ButtonsFactory.createToolButton("OpenFrom", "Открыть из...");
    private JButton UnDoBut = ButtonsFactory.createToolButton("unDo", "Отменить");
    private JButton ReDoBut = ButtonsFactory.createToolButton("reDo", "Применить");
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Копировать");
    private JButton pasteBtn = ButtonsFactory.createToolButton("Paste", "Вставить");
    private JButton cutBtn = ButtonsFactory.createToolButton("Cut", "Вырезать");
    private JButton findBtn = ButtonsFactory.createToolButton("Find", "Найти и заменить");
    private JButton replaceBtn = ButtonsFactory.createToolButton("S&R", "Заменить");
    private JButton goLineBtn = ButtonsFactory.createToolButton("goLine", "Перейти на строку");
    private JButton processBtn = ButtonsFactory.createToolButton("ServiceTab", "Выбор процесса");
    private JButton filterBtn = ButtonsFactory.createToolButton("FilterNode", "Выбор фильтра");
    private JButton debugBtn = ButtonsFactory.createToolButton("runDebug", "Проверка");
    private JButton boxBtn = ButtonsFactory.createToolButton("BoxNode", "Выбор пункта обмена");
    private JButton helpBtn = ButtonsFactory.createToolButton("fx", "Редактор формул");
    private JButton decisionBtn = ButtonsFactory.createToolButton("Edge", "Выбор перехода");
    private JButton marksBtn = ButtonsFactory.createToolButton("mark", "Выбор метки");
    private JButton reportBtn = ButtonsFactory.createToolButton("ReportNode", "Выбор отчета");
    private JButton colorBtn = ButtonsFactory.createToolButton("colorChooserSm", "Выбор цвета");
    private JButton markerBtn = ButtonsFactory.createToolButton("marker",".png", "Активировать маркер");
    private JButton closeTabBtn = new JButton(kz.tamur.rt.Utils.getImageIcon("deleteAll"));
    private JButton downloadFileBtn = ButtonsFactory.createToolButton("DownloadFile.png", "Скачать файл");
    private JCheckBox transactionCheck = Utils.createCheckBox("В нулевой транзакции", true);
    private JButton moveToMainBtn = ButtonsFactory.createToolButton("MoveToEditor.png", "Переместить в редактор");
    private JButton viewHistoryBtn = ButtonsFactory.createToolButton("ServiceHistory.gif", "История изменения"); // заменить иконку
    
 // created for using to send OpenElementPanel dialog OK button enabled or disabled 

    public JLabel posinfo = new JLabel();
    private ExpressionDebuger debuger = new ExpressionDebuger(new ClientOrLang(new EmptyFrame()));
    private static Map<String, String> funcs_map;
    private static Map<String, String> vars_map;
    Map MaskedUID = new HashMap();
    private Highlighter.HighlightPainter myHighlightPainter = new EditorPane.MyHighlightPainter(new Color(255, 149, 149));
    Highlighter hilite;
    JPanel bottomPane = new JPanel();
    Tabbed tabPane = new Tabbed();
    JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JTextPane todoPanel = new JTextPane();
    private JTextPane mistakes = new JTextPane();
    private int ErrorBtn = 0;
    private int TodoBtn = 1;

    public boolean isModified = false;
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);;
    protected Action m_undoAction;
    protected Action m_redoAction;
    protected UndoManager m_undo = new UndoManager();
    private JScrollPane editorSP;
    private Object sourceObject = null;
    public Object getSourceObject() {
		return sourceObject;
	}

	private KrnMethod method = null;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private boolean isMarked = false;
    private boolean readOnly = false;
    private boolean isShowMoveToMainBtn = false;
    private boolean isMovedToMain = false;
    private boolean isCheck = false;
    
    private Inspectable ins = null;
    private Property prop = null;
    private OrGuiComponent comp = null; 
	private PropertyNode node = null;
	private Object stNode = null;	
	private NodeProperty nodeProp = null;
	private NodeEventType nodeEvent = null;
	private kz.tamur.guidesigner.service.Document doc = null;
	private InterfaceFrame ifc = null;
	
	public ExpressionStatusBar getStatusBar() {
		return statusBar;
	}
    
    public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	private Dialog dlg;
    
    Icon marker = kz.tamur.rt.Utils.getImageIconExt("marker",".png");
    Icon markerYes = kz.tamur.rt.Utils.getImageIconExt("markerYes",".png");
    
    private Icon cancelIcon = kz.tamur.rt.Utils.getImageIconExt("cancel",".png");
    private Icon UIDIcon = kz.tamur.rt.Utils.getImageIconExt("UID1",".gif");
    private ExecutorService executor;

    private Object triggerOwner;
    private int mode;
    
    private KrnObject lastSelectedFilterNode = null;
    
    private kz.tamur.guidesigner.service.MainFrame serviceFrm = null;

	public ExpressionEditor(final String expr) {
        initMaps();
        initToolBar(false);
        init(expr);       
    }
    
    public ExpressionEditor(final String expr,boolean initToolBar) {
        initMaps();
        if(initToolBar) initToolBar(false);
        init(expr);       
    }
    
    public ExpressionEditor(final String expr, Object sourceObject) {
        this.sourceObject = sourceObject;
        initMaps();
        initToolBar(sourceObject == null ? false : true);
        init(expr);
    }
    
    public ExpressionEditor(final String expr, Object sourceObject, Inspectable ins, Property prop, OrGuiComponent comp, PropertyNode node, InterfaceFrame ifc) {
        this.sourceObject = sourceObject;
        initMaps();
        initToolBar(sourceObject == null ? false : true);
        init(expr);
        this.ins = ins;
        this.prop = prop;
        this.comp = comp;
        this.node = node;
        this.ifc = ifc;
        this.readOnly = ifc.isReadOnly();
        ((ExprEditorDelegate)sourceObject).setExpression(getExpression());
        
    }
    
    public ExpressionEditor(final String expr, Object sourceObject, Inspectable ins, Property prop, Object stNode, NodeProperty nodeProp, NodeEventType nodeEvent, kz.tamur.guidesigner.service.Document doc) {
        this.sourceObject = sourceObject;
        initMaps();
        initToolBar(sourceObject == null ? false : true);
        init(expr);
        this.ins = ins;
        this.prop = prop;
        this.stNode = stNode;
        this.nodeProp = nodeProp;
        this.nodeEvent = nodeEvent;
        this.doc = doc;
        this.readOnly = doc.isReadOnly();
        ((ExprEditorDelegate)sourceObject).setExpression(getExpression());
    }
    
    public ExpressionEditor(final String expr, int tr, Object sourceObject, Object triggerOwner, int mode, boolean readOnly) {
        this.sourceObject = sourceObject;
        this.triggerOwner = triggerOwner;
        this.mode = mode;
    	this.readOnly = readOnly;
        initMaps();
        transactionCheck.setSelected(tr == 0);
        initToolBar(sourceObject == null ? false : true);
        init(expr);
    }
    
    public ExpressionEditor(final String expr, Object sourceObject, KrnMethod method,boolean readOnly) {
        this.sourceObject = sourceObject;
        this.method = method;
    	this.readOnly = readOnly;
        initMaps();
        initToolBar(sourceObject == null ? false : true);
        init(expr);
    }
    
	public ExpressionEditor(final String expr, Object sourceObject, KrnMethod method, boolean readOnly, boolean isShowMoveToMainBtn) {
		this.sourceObject = sourceObject;
		this.method = method;
		this.readOnly = readOnly;
		this.isShowMoveToMainBtn = isShowMoveToMainBtn;
		initMaps();
		initToolBar(sourceObject == null ? false : true);
		init(expr);
	}
    
    private void initToolBar(boolean isShowSaveBut) {
    	 toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
         if(!readOnly){
	         if (isShowSaveBut) {
	         	toolBar.add(saveBut);
	         }
        	 toolBar.add(saveBut);
        	 toolBar.addSeparator();
         }
         saveBut.setEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.METHODS_EDIT_RIGHT));
         toolBar.add(saveOnBut);
         toolBar.add(openFromBut);
         toolBar.addSeparator();
         toolBar.add(UnDoBut);
         toolBar.add(ReDoBut);
         toolBar.addSeparator();
         toolBar.add(cutBtn);
         toolBar.add(copyBtn);
         toolBar.add(pasteBtn);
         toolBar.addSeparator();
         toolBar.add(findBtn);
         toolBar.add(replaceBtn);
         toolBar.addSeparator();
         toolBar.add(goLineBtn);
         toolBar.add(colorBtn);
         toolBar.add(markerBtn);
         toolBar.addSeparator();
         toolBar.add(printBut);
         toolBar.addSeparator();
         toolBar.add(pathBtn);
         toolBar.add(uidBtn);
         toolBar.add(UI);
         toolBar.add(UserBtn);
         toolBar.add(processBtn);
         toolBar.add(filterBtn);
         toolBar.add(reportBtn);
         toolBar.add(boxBtn);
         toolBar.add(decisionBtn);
         toolBar.add(marksBtn);
         toolBar.addSeparator();
         posinfo = Utils.createLabel("1 : 1");
         toolBar.add(debugBtn);
         toolBar.addSeparator();
         toolBar.add(helpBtn);
         toolBar.add(viewHistoryBtn);
         toolBar.addSeparator();
         toolBar.add(downloadFileBtn);
         toolBar.addSeparator();
         transactionCheck.setVisible(false);
		 transactionCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				onChanged = true;
			}
		 });
         toolBar.add(transactionCheck);
         if (isShowMoveToMainBtn) {
        	 toolBar.add(moveToMainBtn);
         }

         saveBut.addActionListener(this);
         saveOnBut.addActionListener(this);
         openFromBut.addActionListener(this);
         UnDoBut.addActionListener(m_undoAction);
         ReDoBut.addActionListener(m_redoAction);
         cutBtn.addActionListener(this);
         copyBtn.addActionListener(this);
         pasteBtn.addActionListener(this);
         findBtn.addActionListener(this);
         replaceBtn.addActionListener(this);
         goLineBtn.addActionListener(this);
         colorBtn.addActionListener(this);
         markerBtn.addActionListener(this);
         pathBtn.addActionListener(this);
         uidBtn.addActionListener(this);
         UI.addActionListener(this);
         UserBtn.addActionListener(this);
         processBtn.addActionListener(this);
         filterBtn.addActionListener(this);
         reportBtn.addActionListener(this);
         boxBtn.addActionListener(this);
         decisionBtn.addActionListener(this);
         marksBtn.addActionListener(this);
         debugBtn.addActionListener(this);
         helpBtn.addActionListener(this);
         downloadFileBtn.addActionListener(this);
         moveToMainBtn.addActionListener(this);
         viewHistoryBtn.addActionListener(this);
    }
    
    public void setSourceMethodToDebugger(KrnMethod sourceMethod) {
    	debuger.setSourceMethod(sourceMethod);
    }
    
    public void setVisibleTransactionCheck(boolean visible) {
    	transactionCheck.setVisible(visible);
    }
    
    public boolean getStatusTansactionCheck() {
    	return transactionCheck.isSelected();
    }
    
    public void setDialog(Dialog dlg) {
    	this.dlg = dlg;
    }
    
    public boolean isMovedToMain() {
    	return isMovedToMain;
    }
    
    private void init(String expr) {
    	text = expr;
    	m_undo.setLimit(-1);
        setOpaque(isOpaque);
        setLayout(new BorderLayout());
        editor = new EditorPane(this, vars_map, funcs_map);
       
        posinfo.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
    			editor.showGoLineDlg();
        	}
        });

        m_undoAction = new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        };

        m_redoAction = new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        };
        viewHistoryBtn.setEnabled(method!=null || triggerOwner!=null);
        JPanel p = new JPanel(new GridBagLayout());
        p.add(toolBar, new GridBagConstraints(0, 0, 4, 1, 0, 0, CENTER, HORIZONTAL, INSETS_0, 0, 0));
        p.add(new JLabel(" "), new GridBagConstraints(4, 0, 2, 1, 2, 0, CENTER, HORIZONTAL, INSETS_0, 0, 0));
        p.add(posinfo, new GridBagConstraints(6, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
        add(p, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);

        editorSP = new JScrollPane(editor);
        editorSP.getViewport().setSize(300000, 3000);
        editorSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TextLineNumber lineNumber = new TextLineNumber(editor);
        editorSP.setRowHeaderView(lineNumber);
        editorSP.setBackground(editor.getBackground());
        mainSplit.setTopComponent(editorSP);
        mainSplit.setDividerLocation(1.0);
        bottomPane.setLayout(new GridBagLayout());
        JPanel closePane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePane.setBackground(Utils.getSysColor());
        closeTabBtn.setOpaque(true);
        closeTabBtn.setBorder(BorderFactory.createEmptyBorder());
        closeTabBtn.addActionListener(this);
        closePane.add(closeTabBtn);
        bottomPane.add(closePane, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, INSETS_0, 0, 0));
        tabPane.setFont(Utils.getDefaultFont());
        JScrollPane scr = new JScrollPane(mistakes);
        tabPane.addTab("Проверка", kz.tamur.rt.Utils.getImageIcon("runDebug"), scr);
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabPane.fireChange();
        mistakes.setEditable(false);
        todoPanel.setEditable(false);
        bottomPane.add(tabPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, BOTH, INSETS_0, 0, 0));
        add(mainSplit, BorderLayout.CENTER);
        editor.setText(expr);
        editor.getDocument().addDocumentListener(new DocumentChangeListener(expr));
        
        editor.getDocument().addDocumentListener(new DocumentListener(){
        	@Override
        	public void changedUpdate(DocumentEvent e) {
        		onChanged = true;
        	}
        	
        	@Override
        	public void insertUpdate(DocumentEvent e) {
        		//
        	}
        	
        	@Override
        	public void removeUpdate(DocumentEvent e) {
        		onChanged = true;
        	}
        });
        
        editor.requestFocusInWindow();
        editor.grabFocus();
        editor.getDocument().addUndoableEditListener(new Undoer((ExprDoc)editor.getDocument()));
    }
    
    public void focusEditor() {
        editor.requestFocusInWindow();
        editor.grabFocus();
    }

    public String getExpression() {
        return editor.getText();
    }

    public void setExpression(String expr) {
        isModified = true;
        editor.setVisible(false);
        editorSP.getViewport().remove(editor);
        editorSP.getViewport().setView(new JLabel("Загрузка"));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        editor.setText(expr);
        editor.getDocument().addDocumentListener(new DocumentChangeListener(expr));
        isModified = false;
        clearUnodRedo();
        editor.getDocument().addUndoableEditListener(new Undoer((ExprDoc)editor.getDocument()));
        editorSP.getViewport().setView(editor);
        editor.setVisible(true);
    }

    private static synchronized void initMaps() {
    	if (funcs_map != null)
    		return;
    	funcs_map = new TreeMap<String, String>();
    	vars_map = new TreeMap<String, String>();
        try {
            Kernel krn = Kernel.instance();
            SAXBuilder builder = new SAXBuilder();
            KrnClass cl;
            cl = krn.getClassByName("OrLang");
            if (cl != null) {
                KrnObject[] objs = krn.getClassObjects(cl, 0);
                if (objs.length > 0) {
                    KrnObject obj = objs[0];
                    byte[] func_data = krn.getBlob(obj, "funcs", 0, 0, 0);
                    if (func_data.length > 0) {
                        InputStream is = new ByteArrayInputStream(func_data);
                        Element func_doc = builder.build(is).getRootElement();
                        MakeMap(func_doc, funcs_map);
                    }
                    byte[] var_data = krn.getBlob(obj, "vars", 0, 0, 0);
                    if (var_data.length > 0) {
                        InputStream is = new ByteArrayInputStream(var_data);
                        Element var_doc = builder.build(is).getRootElement();
                        MakeMap(var_doc, vars_map);
                    }
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    private static void MakeMap(org.jdom.Element e, Map<String, String> map) {
        if (e.getName().equals("folder")) {
            if (e.getChildren().size() > 0) {
                java.util.List list = e.getChildren();
                for (Object aList : list) {
                    MakeMap((Element) aList, map);
                }
            }
        } else if (e.getName().equals("func")) {
            String code = e.getAttribute("name").getValue();
            String desc = e.getText();
            map.put(code, desc);
        }
    }

    public void actionPerformed(ActionEvent e) {
        final Document doc = editor.getDocument();
        try {
            final Object src = e.getSource();
            if (src == pathBtn) {
                String path = DesignerFrame.path_expr;
                ClassNode cnode = getClassNode(path);
                ClassBrowser cb = new ClassBrowser(cnode, true);
                if (path != null && !"".equals(path))
                    cb.setSelectedPath(path);
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
                    String spath = cb.getSelectedPath();
                    if (spath.length() > 0){
                    	String[] strs = spath.split("<method>");
                    	if (strs.length == 1){
                    		DesignerFrame.path_expr = spath;
                    	} else {
                    		DesignerFrame.path_expr = strs[0];
                    		spath = strs[1];
                    	}
                    }
                    int position = editor.getCaretPosition();
                    if (editor.isSurroundQuotes()){
                    	editor.insertString(position, spath, true);
                    } else {
                    	editor.insertString(position, "\"" + spath + "\"", true);
                    }
                    editor.grabFocus();
                }
            } else if (src == uidBtn) {
            	if (executor != null && !executor.isTerminated()) {
	            	executor.shutdownNow();
	            	uidBtn.setIcon(UIDIcon);
            	} else {
            		executor = Executors.newSingleThreadExecutor();
            		executor.submit(new Runnable() {
						@Override
						public void run() {
							try {
				            	uidBtn.setIcon(cancelIcon);
				                String path = DesignerFrame.path_expr;
				                ClassNode cnode = getClassNode(path);
				                ClassBrowser cb = new ClassBrowser(cnode, true);
				                if (path != null && !"".equals(path))
				                    cb.setSelectedPath(path);
				                DesignerDialog dialog;
				                if (getTopLevelAncestor() instanceof JFrame) {
				                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите путь", cb);
				                } else {
				                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите путь", cb);
				                }
				                dialog.setSize(new Dimension(800, 600));
				                dialog.setLocation(getCenterLocationPoint(800, 600));
				                dialog.show();
				                int res = dialog.getResult();
				                if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
				                    String spath = cb.getSelectedPath();
				                    if (spath.length() > 0) {
				                        DesignerFrame.path_expr = spath;
				                    }
				                    UIDChooser uidList = new UIDChooser(spath, true);
				                    if (getTopLevelAncestor() instanceof JFrame) {
				                    	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите значение", uidList);
				                    } else {
				                    	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите значение", uidList);
				                    }
				                    dialog.show();
				                    dialog.setSize(new Dimension(700, 500));
				                    dialog.setLocation(getCenterLocationPoint(700, 500));
				                    res = dialog.getResult();
				                    if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
				                    	if (uidList.getValuesList().getSelectedValue() != null) {
				                    		if (uidList.getSelectedUID().length > 1) {
				                    			
					                    		int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
					                    		int lineEnd = Utilities.getRowEnd(editor, editor.getCaretPosition());
					                    		int position = editor.getCaretPosition();
					                    		boolean hasMoreText = false;
					                    		if(editor.getDocument().getLength()>lineEnd)
					                    			hasMoreText = true;
					                    		if(position == lineStart) {
					                    			doc.insertString(lineStart, uidList.getSelectedUID()[0], null);
					                    			int setPos = position + uidList.getSelectedUID()[0].length();
					                    			doc.insertString(setPos,uidList.getSelectedUID()[1], null);
					                    			if(hasMoreText) {
					                    				doc.insertString(setPos + uidList.getSelectedUID()[1].length(),"\n", null);
					                    			}
					                    		} else {
					                    			doc.insertString(lineEnd,"\n" + uidList.getSelectedUID()[0], null);
					                    			int setPos = lineEnd + uidList.getSelectedUID()[0].length() + 1;
					                    			doc.insertString(setPos,uidList.getSelectedUID()[1], null);
					                    		}
				                    		} else {
				                    			int lineEnd = Utilities.getRowEnd(editor, editor.getCaretPosition());
				                    			int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
						                        int position = editor.getCaretPosition();
						                        if(position == lineStart) {
						                        doc.insertString(position, uidList.getSelectedUID()[0], null);
						                        } else {
						                        	doc.insertString(lineEnd,"\n" + uidList.getSelectedUID()[0], null);
						                        }
				                    		}
					                        editor.grabFocus();
				                    	}
				                    }
				                }
				            	uidBtn.setIcon(UIDIcon);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
            		executor.shutdown();
            	}
            } else if (src == UI) {
                InterfaceTree ifc_tree = kz.tamur.comps.Utils.getInterfaceTree();
                OpenElementPanel panel = new OpenElementPanel(ifc_tree, true, Constants.NEED_CHECK_FOLDER, true);
                panel.setSearchUIDPanel(true);
                DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор интерфейса", panel);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор интерфейса", panel);
                }
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	if(dialog != null )
                		dialog.setOkEnabled(false);
                } else {
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) ifc_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    ifc_tree.setSelectionPath(tpath);
                    ifc_tree.scrollPathToVisible(tpath);
                }
                dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                }
            } else if (src == UserBtn) {
                UserTree user_tree = kz.tamur.comps.Utils.getUserTree();
                OpenElementPanel panel = new OpenElementPanel(user_tree, true, Constants.NEED_CHECK_FOLDER, true);
                panel.setSearchUIDPanel(true);
                DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор пользователя", panel);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор пользователя", panel);
                }                 
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	dialog.setOkEnabled(false);
                } else {
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) user_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    user_tree.setSelectionPath(tpath);
                    user_tree.scrollPathToVisible(tpath);
                }
                dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                }
            } else if (src == processBtn) {
                ServicesTree proc_tree = kz.tamur.comps.Utils.getServicesTree();
                OpenElementPanel panel = new OpenElementPanel(proc_tree, true, Constants.NEED_CHECK_FOLDER, true);
                panel.setSearchUIDPanel(true);
                DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор процесса", panel);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор процесса", panel);
                }
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	dialog.setOkEnabled(false);
                } else {
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) proc_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    proc_tree.setSelectionPath(tpath);
                    proc_tree.scrollPathToVisible(tpath);
                }
                dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                }
            } else if (src == reportBtn) {
                ReportTree report_tree = kz.tamur.comps.Utils.getReportTree(null);
                OpenElementPanel panel = new OpenElementPanel(report_tree, true, Constants.NEED_CHECK_FOLDER, true);
                panel.setSearchUIDPanel(true);
                DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор отчета", panel);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор отчета", panel);
                }
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	dialog.setOkEnabled(false);
                } else {
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) report_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    report_tree.setSelectionPath(tpath);
                    report_tree.scrollPathToVisible(tpath);
                }
                dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                }
            } else if (src == filterBtn) {
                FiltersTree filter_tree = kz.tamur.comps.Utils.getFiltersTree();
                OpenElementPanel panel = new OpenElementPanel(filter_tree, true, Constants.NEED_CHECK_FOLDER, true);
                if (lastSelectedFilterNode != null) {
                	panel.setSelectedFilterNode(lastSelectedFilterNode);
                }
                panel.setSearchUIDPanel(true);
                final DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор фильтра", panel, false, false, true);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор фильтра", panel, false, false, true);
                }
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	dialog.setOkEnabled(false);
            		dialog.setEditEnabled(false);
                } 
                else {          	
                	dialog.setEditEnabled(node.isLeaf());                	                	
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) filter_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    filter_tree.setSelectionPath(tpath);
                    filter_tree.scrollPathToVisible(tpath);
                }
            	dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {                     
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                    lastSelectedFilterNode = filter_tree.getSelectedNode().getKrnObj();
                } else if (dialog.getResult() == ButtonsFactory.BUTTON_EDIT) {
                    FilterNode filterNode = (FilterNode) filter_tree.getSelectedNode();
                    FiltersPanel filterPanel = new FiltersPanel(true);
                    filterPanel.load(filterNode,null);
                    DesignerDialog editingDialog;
                    if (getTopLevelAncestor() instanceof JFrame) {
                    	editingDialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Корректировка фильтра", filterPanel, false, false, false);
                    } else {
                    	editingDialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Корректировка фильтра", filterPanel, false, false, false);
                    }
                    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
                    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                    editingDialog.setLocation(insets.left, insets.top);
                    editingDialog.setSize(dimension.width - insets.right, dimension.height - insets.bottom);
                    editingDialog.show();
                    filterPanel.processExit();
                    filter_tree.renameFilter(filterNode, filterNode.toString());
                } 
            } else if (src == boxBtn) {
                BoxTree box_tree = kz.tamur.comps.Utils.getBoxTree();
                OpenElementPanel panel = new OpenElementPanel(box_tree, true, Constants.NEED_CHECK_FOLDER, true);
                panel.setSearchUIDPanel(true);
                DesignerDialog dialog;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор пункта обмена", panel);
                } else {
                	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор пункта обмена", panel);
                }
                AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
                if (node == null) {
                	dialog.setOkEnabled(false);
                } else {
                	dialog.setOkEnabled(true);
                	DefaultTreeModel m = (DefaultTreeModel) box_tree.getModel();
                    TreeNode[] path = m.getPathToRoot(node);
                    TreePath tpath = new TreePath(path);
                    box_tree.setSelectionPath(tpath);
                    box_tree.scrollPathToVisible(tpath);
                }
                dialog.show();
                if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {
                	int lineStart = Utilities.getRowStart(editor, editor.getCaretPosition());
                    doc.insertString(lineStart, panel.getSelectedObject()[0], null);
                	int position = editor.getCaretPosition();
                    doc.insertString(position, panel.getSelectedObject()[1], null);
                    editor.grabFocus();
                }
            } else if (src == decisionBtn) {
            	if (serviceFrm != null) {
	                JGraph graph_ = serviceFrm.getPropEditor().getGraph();
	                if (graph_ != null) {
	                    graph_.setPreferredSize(new Dimension(700, 600));
	                    /*DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор перехода",graph_);*/
	                    DesignerDialog dlg;
	                    if (getTopLevelAncestor() instanceof JFrame) {
	                        dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор перехода", graph_);
	                    } else {
	                        dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор перехода", graph_);
	                    }
	                    dlg.setSize(700, 550);
	                    dlg.setLocation(getCenterLocationPoint(700, 550));
	                    dlg.show();
	                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
	                        Vector sel = graph_.getEditor().getSelectionManager().selections();
	                        String val = "Безымянный";
	                        if (sel.size() > 0) {
	                            if (sel.get(0) instanceof SelectionReshape) {
	                                Object sl = ((SelectionReshape) sel.get(0)).getContent();
	                                if (sl instanceof FigTransitionEdge) {
	                                    val = ((TransitionEdge) ((FigTransitionEdge) sl).getOwner()).getName();
	                                }
	                            }
	                        }
	                        int pos = editor.getCaretPosition();
	                        doc.insertString(pos, (val == null || val.equals("")) ? "Безымянный" : val, null);
	                        editor.grabFocus();
	                    }
	                }
                }
            } else if (src == marksBtn) {
            	try {
                	if (serviceFrm != null) {
	            		ServiceModel model = serviceFrm.getPropEditor().getServiceModel();
		                if (model != null) {
		                    JList valList = new JList();
		                    long langId = model.getDefaultLangId();
		                    Map strs = model.getStrings(langId);
		                    String[] strs_ = new String[strs.size()];
		                    int i = 0;
		                    for (Object o : strs.keySet()) {
		                        String key = (String) o;
		                        strs_[i++] = key + "=" + strs.get(key);
		                    }
		                    DefaultListModel lm = new DefaultListModel();
		                    valList.setModel(lm);
		                    valList.setFont(Utils.getDefaultFont());
		                    Arrays.sort(strs_);
		                    for (i = 0; i < strs_.length; i++) {
		                        lm.addElement(strs_[i]);
		                    }
		                    DesignerDialog dlg;
		                    if (getTopLevelAncestor() instanceof JFrame) {
		                        dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор метки", valList);
		                    } else {
		                        dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор метки", valList);
		                    }
		                    dlg.show();
		                    dlg.setSize(new Dimension(700, 500));
		                    dlg.setLocation(getCenterLocationPoint(700, 500));
		                    int res = dlg.getResult();
		                    if (res != ButtonsFactory.BUTTON_NOACTION
		                            && res == ButtonsFactory.BUTTON_OK) {
		                        int pos = editor.getCaretPosition();
		                        String result = (String) valList.getSelectedValue();
		                        doc.insertString(pos, result.substring(0, result.indexOf("=")), null);
		                        editor.grabFocus();
		                    }
		                }
                	}
            	} catch (NullPointerException exception) {}
            } else if (src == viewHistoryBtn && method!=null) {
            	List<KrnVcsChange> changes=Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, method.uid);
        		KrnVcsChange change=null;
            	if(changes.size()>0) {
            		change=changes.get(0);
            	}
        		Or3Frame.historysPanel.refreshTable(change, true);
                DesignerDialog dlg;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                } else {
                	dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                }
                dlg.setMinimumSize(new Dimension(900, 70));
                dlg.show();
            } else if (src == viewHistoryBtn && triggerOwner!=null) {
            	List<KrnVcsChange> changes=null;
        		KrnVcsChange change=null;
    			if (triggerOwner instanceof KrnClass) {
	            	changes=Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, ((KrnClass)triggerOwner).uid);
    			}else if (triggerOwner instanceof KrnAttribute) {
		            changes=Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, ((KrnAttribute)triggerOwner).uid);
    			}
            	if(changes!=null && changes.size()>0) {
            		for(KrnVcsChange ch:changes) {
            			if(ch.isTrigger) {
            				change=ch;
            				break;
            			}
            		}
            	}
        		Or3Frame.historysPanel.refreshTable(change, true);
                DesignerDialog dlg;
                if (getTopLevelAncestor() instanceof JFrame) {
                	dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                } else {
                	dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
                }
                dlg.setMinimumSize(new Dimension(900, 70));
                dlg.show();
            } else if (src == debugBtn) {
                debug();
            } else if (src == helpBtn) {
                FuncsEditor funcsEditor = new FuncsEditor(vars_map, funcs_map);
                /*DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Редактор формул и переменных", FuncsEditor);*/
                DesignerDialog dlg;
                if (getTopLevelAncestor() instanceof JFrame) {
                    dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Редактор формул и переменных", funcsEditor);
                } else {
                    dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Редактор формул и переменных", funcsEditor);
                }
                dlg.setSize(800, 550);
                dlg.setLocation(getCenterLocationPoint(700, 550));
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    initMaps();
                }
            } else if (src == cutBtn) {
                editor.cut();
            } else if (src == copyBtn) {
                editor.copy();
            } else if (src == pasteBtn) {
                editor.paste();
            } else if (src == closeTabBtn) {
                mainSplit.setBottomComponent(bottomPane);
                mainSplit.setDividerLocation(1.0);
            } else if (src == findBtn) {
                editor.showFindDlg(0);
            } else if (src == replaceBtn) {
                editor.showFindDlg(1);
            } else if (src == goLineBtn) {
                editor.showGoLineDlg();
            } else if (src == colorBtn) {
                showColorChooser();
            } else if (src == saveOnBut) {
            	saveOnBut();
            } else if (src == saveBut) {
            	saveBut();
            } else if (src == openFromBut) {
            	openFromBut();
            } else if (src == markerBtn) {
                isMarked = !isMarked;
                markerBtn.setIcon(isMarked ? markerYes : marker);
            } else if (src == downloadFileBtn) {
            	downloadFileFromServer();
            } else if (src == moveToMainBtn) {
            	moveToMainBtn.setVisible(false);
            	isMovedToMain = true;
            	dlg.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void downloadFileFromServer() {
    	DownloadFilePanel downloadFilePanel = new DownloadFilePanel();
        DesignerDialog dlg = GuiUtil.createDesignerDialog(getTopLevelAncestor(), "Загрузка файлов с сервера", downloadFilePanel);
        dlg.setCancelVisible(false);
        dlg.setOkText("Закрыть");
        dlg.setResizable(false);
        dlg.show();
    }

    public void showErrors(List<ErrRecord> errors) {
        Document mdoc = mistakes.getDocument();
        try {
            for (ErrRecord err : errors) {
                mdoc.insertString(mdoc.getLength(), err + "\n", Utils.getStyle("error"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void showColorChooser() {
        OrColorChooser clrDlg = new OrColorChooser(null);
        DesignerDialog dlg;
        if (getTopLevelAncestor() instanceof JFrame) {
            dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выберите цвет", clrDlg);
        } else {
            dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выберите цвет", clrDlg);
        }
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            try {
                Color color = clrDlg.getColor();
                int pos = editor.getCaretPosition();
                editor.getDocument().insertString(pos, "" + color.getRGB(), null);
                editor.grabFocus();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private ClassNode getClassNode(String path) {
        ClassNode cls = null;
        final Kernel krn = Kernel.instance();
        String s = "";
        try {
            if ("".equals(path)) {
                cls = krn.getClassNodeByName("Объект");
            } else {
                try {
                    s = getClassNameFromPath(path);
                    cls = krn.getClassNodeByName(s);
                } catch (KrnException e) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.ERROR_MESSAGE, "\"" + s +
                            "\" - ошибочное имя класса!");
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }

        return cls;
    }

    private String getClassNameFromPath(String path) {
        StringTokenizer st = new StringTokenizer(path.toString(), ".");
        String s = st.nextToken();
        return s;
    }

//    public void setFilterFolder(KrnObject filterFolder) {
//        this.filterFolder = filterFolder;
//    }

    private void markError(String pattern, int position) {
        try {

            ExprDoc doc = (ExprDoc) editor.getDocument();
            Document mdoc = mistakes.getDocument();
            hilite = editor.getHighlighter();
            String text;
            text = doc.getText(0, doc.getLength());
            if (position == -1) {
                int pos = 0;
                while ((pos = text.indexOf(pattern, pos)) >= 0) {
                    int[] rc = editor.getRCtoPos(pos);
                    MutableAttributeSet style = new SimpleAttributeSet();
                    StyleConstants.setComponent(style, new ReturnBtn(ErrorBtn, pos));
                    mdoc.insertString(mdoc.getLength(), "j", style);
                    mdoc.insertString(mdoc.getLength(), " [" + rc[0] + ":" + rc[1] + "] : ", Utils.getStyle("bold"));
                    mdoc.insertString(mdoc.getLength(), "Не верный аттрибут: " + pattern + "\n", Utils.getStyle("error"));
                    hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                    pos += pattern.length();
                }
            } else {
                int[] rc = editor.getRCtoPos(position);
                MutableAttributeSet style = new SimpleAttributeSet();
                StyleConstants.setComponent(style, new ReturnBtn(ErrorBtn, position));
                mdoc.insertString(mdoc.getLength(), "j", style);
                mdoc.insertString(mdoc.getLength(), " [" + rc[0] + ":" + rc[1] + "] : ", Utils.getStyle("bold"));
                mdoc.insertString(mdoc.getLength(), "Формула " + pattern + " не содержит параметры\n", Utils.getStyle("error"));
                hilite.addHighlight(position, position + pattern.length(), myHighlightPainter);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        ps.addPropertyChangeListener(l);
    }

    public int errorsCount() {
    	debuger.clear();
    	debuger.debugExpression("", editor.getText());
        List errors = debuger.getErrors();
        return errors.size();
    }

    private class ReturnBtn extends JButton {
        private ReturnBtn(int type, final int pos) {
            if (type == ErrorBtn) {
                setIcon(kz.tamur.rt.Utils.getImageIcon("junitError"));
                setToolTipText("Перейти к ошибке");
            } else if (type == TodoBtn) {
                setIcon(kz.tamur.rt.Utils.getImageIcon("bulb"));
                setToolTipText("Перейти к заметке");
            }
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editor.setCaretPosition(pos);
                    editor.grabFocus();
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    setCursor(Constants.HAND_CURSOR);
                }
            });
            setSize(20, 20);
            setMaximumSize(new Dimension(20, 20));
            setPreferredSize(new Dimension(20, 20));
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        ps.removePropertyChangeListener(l);
    }

    private class DocumentChangeListener implements DocumentListener {
        String expr;

        private DocumentChangeListener(String expr) {
            this.expr = expr;
        }

        public void changedUpdate(DocumentEvent e) {
            if (!isModified) {
                ps.firePropertyChange("changed", expr, editor.getText());
            }
        }

        public void insertUpdate(DocumentEvent e) {
            if (!isModified) {
                ps.firePropertyChange("insert", expr, editor.getText());
            }
        }

        public void removeUpdate(DocumentEvent e) {
            if (!isModified) {
                ps.firePropertyChange("remove", expr, editor.getText());
            }
        }
    }
    
    private List<ErrRecord> checkEventExpr(String expr) {
    	List<ErrRecord> errs = new ArrayList<ErrRecord>();
    	String[] methods = {".setAttr(", ".deleteAttr(", ".createObject(", ".delete(", ".lock(", ".unlock("};
    	String[] lines = expr.split("\n");
    	for(String method:methods) {
    		for(int i=0;i<lines.length;i++) {
    			if(!lines[i].startsWith("//"))
    			if(lines[i].contains(method)) {
    				int j = i+1;
    				String m = method.substring(1);
    				ErrRecord er = new ErrRecord("", j, "метод " + m + ") не применим в данном событии!");
    				errs.add(er);
    			}
    		}
    	}
    	return errs;
    }
    
    public int getErrs() {
    	int res = -1;
    	mistakes.setText("");
    	debuger.clear();
    	debuger.debugExpression("", editor.getText());
    	List<ErrRecord> errors = debuger.getErrors();
    	String errs ="";
    	if (errors.size() > 0) 
    		for (ErrRecord err:errors) {
    			errs += err +"\n";
    		}
    	if(isCheck) {
    		List<ErrRecord> eventErrs = checkEventExpr(this.getExpression());
    		if(eventErrs.size()>0)
    		for (ErrRecord err : eventErrs) {
    			errs += err +"\n";
    		}
    	}
    	Component parent = this.getTopLevelAncestor();
    	if(sourceObject instanceof ExprEditorDelegate) {
    		Component grandParent = ((Component)(ExprEditorDelegate)sourceObject).getParent();
    		if(grandParent != null) {
    			
    		}
    	}
    	
    	if(errs.length() > 0) {
    		String[] options = {"Сохранить" , "Отмена"};
    		JTextArea textArea = new JTextArea(errs);
    		JScrollPane scrollPane = new JScrollPane(textArea);
    		scrollPane.setPreferredSize(new Dimension(500, 200));
    		res = JOptionPane.showOptionDialog(this,
    				scrollPane,
    				"Ошибка",
    				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]
    				);
    	} 
    	return res;
    }
    
    public void debug() {
        try {
            mainSplit.setBottomComponent(bottomPane);
            mainSplit.setDividerLocation(0.7);
            Document mdoc = mistakes.getDocument();
            mistakes.setText("");
            mdoc.insertString(0, "Проверка начата\n", Utils.getStyle("myvars"));
            debuger.clear();
            debuger.debugExpression("", editor.getText());
            List<ErrRecord> errors = debuger.getErrors();
            if(isCheck) {
            	errors.addAll(checkEventExpr(this.getExpression()));
            }
            if (errors.size() > 0) {
                showErrors(errors);
            } else {
                try {
                    mdoc.insertString(mdoc.getLength(), "Ошибок нет\n", Utils.getStyle("myvars"));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    public void debugProcedure(List<ErrRecord> errors) {
        try {
            mainSplit.setBottomComponent(bottomPane);
            mainSplit.setDividerLocation(0.7);
            Document mdoc = mistakes.getDocument();
            mistakes.setText("");
            mdoc.insertString(0, "Результат компиляции\n", Utils.getStyle("myvars"));
            debuger.clear();
            if (errors!=null && errors.size() > 0) {
                showErrors(errors);
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected void updateUndo() {
        if (m_undo.canUndo()) {
            m_undoAction.setEnabled(true);
            m_undoAction.putValue(Action.NAME,
                    m_undo.getUndoPresentationName());
        } else {
            m_undoAction.setEnabled(false);
            m_undoAction.putValue(Action.NAME, "Undo");
        }
        if (m_undo.canRedo()) {
            m_redoAction.setEnabled(true);
            m_redoAction.putValue(Action.NAME,
                    m_undo.getRedoPresentationName());
        } else {
            m_redoAction.setEnabled(false);
            m_redoAction.putValue(Action.NAME, "Redo");
        }

        UnDoBut.setEnabled(m_undo.canUndo());
                ReDoBut.setEnabled(m_undo.canRedo());
    }

    private void clearUnodRedo() {
        m_undo.discardAllEdits();
        updateUndo();
    }

    class Undoer implements UndoableEditListener {
    	ExprDoc doc = null;
    	List<UndoableEdit> undoes = new ArrayList<UndoableEdit>();
    	
        public Undoer(ExprDoc doc) {
        	this.doc = doc;
        	doc.setUndoer(this);
            m_undo.discardAllEdits();
            updateUndo();
        }

        public void undoableEditHappened(UndoableEditEvent e) {
            AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) e.getEdit();
            	if (e.getSource() instanceof ExprDoc) {
            		ExprDoc doc = (ExprDoc) e.getSource();
            		
            		if (doc.isReplaceAction()) {
    					undoes.add(e.getEdit());
	            	} else {
		                m_undo.addEdit(e.getEdit());
		                updateUndo();
	            	}
            	}
        }
        
        public void completeComplexUndo() {
			if (undoes.size() > 0) {
				MultiUndo undo = new MultiUndo(undoes);
                m_undo.addEdit(undo);
                updateUndo();
                undoes.clear();
			}
        }
    }

    public void undo() {
        try {
            if (m_undo.canUndo())
                m_undo.undo();
            updateUndo();
        } catch (CannotUndoException ex) {
        } finally {
            UnDoBut.setEnabled(m_undo.canUndo());
            ReDoBut.setEnabled(m_undo.canRedo());
        }
    }

    public void redo() {
        try {
            if (m_undo.canRedo())
                m_undo.redo();
            updateUndo();
        } catch (CannotRedoException ex) {
        } finally {
            UnDoBut.setEnabled(m_undo.canUndo());
            ReDoBut.setEnabled(m_undo.canRedo());
        }
    }

    class Tabbed extends OrBasicTabbedPane {
        public void fireChange() {
            fireStateChanged();
        }
    }

    public void saveOnBut() {
    	JFileChooser fc = new JFileChooser();
    	int retFlag = fc.showSaveDialog(this);
    	if(retFlag == JFileChooser.APPROVE_OPTION){
    		File file = fc.getSelectedFile();
    		System.out.println("getSelectedFile " + fc.getSelectedFile().getName());
    		try {
				BufferedWriter br = new BufferedWriter(new FileWriter(file));
				br.write(editor.getText());
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public boolean saveBut() {
    	if (sourceObject != null) {
    		if (sourceObject instanceof ExprEditorDelegate) {
    			int res = getErrs();
    			if(res == 1) return true;
    			((ExprEditorDelegate) sourceObject).setStringValue(getExpression());
    		} else if(sourceObject instanceof ExprDelegate) {
    			((ExprDelegate) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof AttrPropPanel) {
    			((AttrPropPanel) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof BoxOrExprEditorDelegate) {
    			int res = getErrs();
    			if(res == 1) return true;
    			((BoxOrExprEditorDelegate) sourceObject).setStringValue(getExpression());
    		} else if (sourceObject instanceof BoxPropertyEditor) {
    			((BoxPropertyEditor) sourceObject).setCellEditorValue(getExpression().getBytes());
    		} else if (sourceObject instanceof CmdViewItem) {
    			((CmdViewItem) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof HTMLEditorDelegate) {
    			int res = getErrs();
    			if(res == 1) return true;
    			((HTMLEditorDelegate) sourceObject).setStringValue(getExpression());
    		} else if (sourceObject instanceof ExpressionCellEditor) {
    			int res = getErrs();
    			if(res == 1) return true;
    			((ExpressionCellEditor) sourceObject).setStringValue(getExpression());
    		} else if (sourceObject instanceof TreeOrExprEditorDelegate) {
    			int res = getErrs();
    			if(res == 1) return true;
    			((TreeOrExprEditorDelegate) sourceObject).setStringValue(getExpression());
    		} else if (sourceObject instanceof DataCellEditor) {
    			((DataCellEditor) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof MethodPanel) {
    			((MethodPanel) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof MenuItemPropertyEditor) {
    			((MenuItemPropertyEditor) sourceObject).setExpression(getExpression());
    		} else if (sourceObject instanceof ClassBrowser || sourceObject instanceof TriggersPanel || sourceObject instanceof EditorPane)  {
    			if (triggerOwner == null) {
	        		try {
	        			int res = getErrs();
	        			if(res == 1) return true;
	        			
	            		ClassNode classNode = Kernel.instance().getClassNode(method.classId);
						classNode.changeMethod(method.uid, method.name, method.isClassMethod, getExpression());
						onChanged = false;
	        		} catch (KrnException e) {
						e.printStackTrace();
		                Container cnt = getTopLevelAncestor();
		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
					}
    			} else {
        		    Kernel krn = Kernel.instance();
        			if (triggerOwner instanceof KrnClass) {
        				try {
        					krn.setClsTriggerEventExpression(getExpression(), ((KrnClass) triggerOwner).id, mode, transactionCheck.isSelected());
    						onChanged = false;
        				} catch (KrnException e) {
        					e.printStackTrace();
    		                Container cnt = getTopLevelAncestor();
    		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
        				}
        			} else if (triggerOwner instanceof KrnAttribute) {
        				try {
	        				krn.setAttrTriggerEventExpression(getExpression(), ((KrnAttribute) triggerOwner).id, mode, transactionCheck.isSelected());
	        				if (sourceObject instanceof ClassBrowser) {
	        					((ClassBrowser) sourceObject).updateAttrTree(krn.getClassNode(((KrnAttribute) triggerOwner).classId));
	        				} else {
	        					((TriggersPanel) sourceObject).getClassBrowser().updateAttrTree(krn.getClassNode(((KrnAttribute) triggerOwner).classId));
	        				}
							onChanged = false;
        				} catch (KrnException e) {
        					e.printStackTrace();
    		                Container cnt = getTopLevelAncestor();
    		                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e.getMessage());
        				}
        			}
        			if (sourceObject instanceof TriggersPanel) {
        				((TriggersPanel) sourceObject).refreshTable();
        			}
    			}
    		}
    	}
    	if(!readOnly && ins instanceof GuiComponentItem && comp != null && onChanged) {
    		comp.setPropertyValue(new PropertyValue(getExpression(), node));
    		((GuiComponentItem) ins).setValue(prop, getExpression(), ifc);
    		onChanged = false;
    		((ExprEditorDelegate)sourceObject).setExpression(getExpression());
    		((ExprEditorDelegate)sourceObject).getEditor().getTable().repaint();
    	}
    	if(!readOnly && ins instanceof ServiceItem && stNode != null && onChanged) {
    		if(nodeProp != null && stNode instanceof ServiceNodeIfc) {
    			((ServiceNodeIfc)stNode).setProperty(nodeProp, getExpression());
    		} else if(nodeEvent != null && stNode instanceof ServiceNodeIfc) {
    			((ServiceNodeIfc)stNode).setAction(nodeEvent, getExpression());
    		}
    		
    		((ServiceItem)ins).setValue( prop, getExpression(), doc);
    		onChanged = false;
    		((ExprEditorDelegate)sourceObject).setExpression(getExpression());
    		((ExprEditorDelegate)sourceObject).getEditor().getTable().repaint();
    	}
    	
    	return false;
    }
    
    public void openFromBut() {
    	JFileChooser fc = new JFileChooser();
    	int retFlag = fc.showOpenDialog(this);
    	if(retFlag == JFileChooser.APPROVE_OPTION) {
    		File file = fc.getSelectedFile();
    		try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				
				try {
					String txt = new String();
					String buf = br.readLine();
					while(buf != null){
						txt += buf;
						buf = br.readLine();
					}
					editor.setText(txt);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    		
    	}
    }

    public boolean isMarked() {
        return isMarked;
    }
    
	class MultiUndo extends AbstractUndoableEdit {
		private List<UndoableEdit> undoes;
		
		protected MultiUndo(List<UndoableEdit> undoes) {
			super();
			this.undoes = new ArrayList<UndoableEdit>(undoes);
		}

		public void undo() throws CannotUndoException {
			super.undo();
			for (int i = undoes.size() - 1; i >= 0; i--) {
				undoes.get(i).undo();
			}
		}

		public void redo() throws CannotRedoException {
			super.redo();
			for (int i = 0; i < undoes.size(); i++) {
				undoes.get(i).redo();
			}
		}
	}
	
	public boolean isReadOnly(){
		return readOnly || !Kernel.instance().getUser().hasRight(Or3RightsNode.METHODS_EDIT_RIGHT);
	}
	
	public boolean isMethodExpr(){
		return method!=null;
	}

    public kz.tamur.guidesigner.service.MainFrame getServiceFrm() {
		return serviceFrm;
	}

	public void setServiceFrm(kz.tamur.guidesigner.service.MainFrame serviceFrm) {
		this.serviceFrm = serviceFrm;
	}
}