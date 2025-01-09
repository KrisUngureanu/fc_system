package kz.tamur.util;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.Utilities;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ModelChange;
import com.cifs.or2.kernel.UserSessionValue;

import kz.tamur.Or3Frame;
import kz.tamur.admin.AttributeTreeIconLoader;
import kz.tamur.admin.ClassTreeIconLoader;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Factories;
import kz.tamur.comps.Mode;
import kz.tamur.comps.PropertyValue;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.guidesigner.expr.TabObj;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.filters.OrFilterNode;
import kz.tamur.guidesigner.filters.OrFilterTree;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3.client.util.ClientUtils;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.UnderlineHighlighter.UnderlineHighlightPainter;
import kz.tamur.util.editor.OrTextPane;


/**
 * Created by IntelliJ IDEA.
 * User: KazakBala
 * Date: 17.02.2005
 * Time: 10:42:10
 * To change this template use File | Settings | File Templates.
 */

/**
 * Remake by Eclipse Helios
 * User: naik
 * Date: 01.06.2011
 * Time: 09:00:00
 */
public class EditorPane extends OrTextPane implements KeyListener, CaretListener, MouseListener {
	
	private static final int AUTOCOMPLETE_POPUP_WIDTH = 400;
	private static final int AUTOCOMPLETE_POPUP_HEIGHT = 194;
	private static final int KEY_POPUP_WIDTH = 500;
	private static final int KEY_POPUP_HEIGHT = 300;
	private static final Color POPUP_BACKGROUND_COLOR = new Color(230,230,230);
	private static final Color POPUP_BORDER_COLOR = new Color(150,150,150);
	private static final Pattern beginVarPattern = Pattern.compile("\\$(\\w*)$");//признак начала переменной
	private static final Pattern beginOperPattern = Pattern.compile("(#\\w*)$");//признак начала оператора
	private static final Pattern beginFuncPattern = Pattern.compile("\\$\\w+\\.(\\w*)$");//признак начала функции
	private static final Pattern beginClassPattern = Pattern.compile("\\$Objects\\.(?:createObject|getClassObjects|getClass)\\(\"([^.\"]*)$");//признак №1 начала названия класса
	private static final Pattern beginClassPattern2 = Pattern.compile("\\$\\w+\\.(?:getAttr|setAttr|deleteAttr|addPath|createQuery|find)\\(\"([^.\"]*)$");//признак №2 начала названия класса
	private static final Pattern beginAttrPattern = Pattern.compile("\\$\\w+\\.(?:getAttr|setAttr|deleteAttr|addPath|createQuery|find)\\(\"([^.\"\\)]+)((?:\\.[^.\"\\)]+)*)\\.([^.\"\\)]*)$");//признак начала название атрибута
	private static final Pattern beginMethodPattern = Pattern.compile("\\$\\w+\\.(?:exec|sexec)\\(\"([^\"]*)");	// Признак начала метода
	private static final Pattern beginMethodFromGetClassPattern = Pattern.compile("\\$Objects.getClass\\(\"([^\"]*)\"\\)\\.(?:exec|sexec)\\(\"([^\"]*)");	// Признак начала метода и сам метод exec|sexec запускается непосредственно от метода getClass

	private static final Pattern varPattern = Pattern.compile("\\$\\w*");//переменная
	private static final Pattern operPattern = Pattern.compile("#\\w*");//оператор
	private static final Pattern funcPattern = Pattern.compile("\\$\\w+\\.(\\w*)");//функция
	private static final Pattern subAttrPattern = Pattern.compile("\\.([^.]+)");//Податрибуты
	private static final Pattern classNamePattern = Pattern.compile("\\$Objects\\.(?:createObject|getClassObjects|getClass)\\(\"([^.\"\\)\\(]*)");//Название класса
	private static final Pattern classNamePattern2 = Pattern.compile("\\$\\w+\\.(?:getAttr|setAttr|deleteAttr|addPath|createQuery|find)\\(\"([^.\"\\)\\(]*)");//Название класс 2
	private static final Pattern attributeNamePattern = Pattern.compile("\\.([^.\"\\)\\(]*)");//Название атрибута
	
	private static final Pattern fastInjectPattern = Pattern.compile("^(\\s*)([a-bA-z]+)\\s*$");//Признак быстрой подстановки
	private static final Pattern smartTabPattern = Pattern.compile("^(\\s*).*$");//Признак автотабулирования кода
	private static final Pattern blockLinePattern = Pattern.compile("#(if|elseif|else|while|foreach|try|function)(.+#end)?");//Признак однострочной записи операторов
	private static final Pattern unTabLine = Pattern.compile("( *\\t?)?([^\\n]*(\\n|$))");//Описание строки для удаления TAB
	private static final Pattern commentLine = Pattern.compile("\\s*(\\/\\/)?[^\\n]*(\\n|$)");//Описания закомментированной строки
    private static final Pattern beginOperatorPattern = Pattern.compile("#(foreach|while|if|elseif|else|try|catch|finally|function|end)");//признак начала операторов, которые должны завершаться #end

	private Matcher matcher;
    public Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(new Color(0, 153, 204));
    Highlighter hilite = getHighlighter();
    ExpressionEditor panel;
    int old_dot, old_x, old_y = 0;
    Map<String, String> vars = new HashMap<String, String>();
    Map<String, String> funcs = new HashMap<String, String>();
    Map<String, KrnClass> paths = new TreeMap<String, KrnClass>();
    Map<String, KrnClass> subPaths = new TreeMap<String, KrnClass>();
    private JPopupMenu fxpopup = new JPopupMenu();
    private JPopupMenu popup = new JPopupMenu();
    private JPopupMenu keypopup = new JPopupMenu();
    private DefaultListModel listModel = new DefaultListModel(); 
    private DefaultListModel listModelContext = new DefaultListModel(); 
    private JList list = new JList(listModel);
    
    private JTextArea text = new JTextArea();
    Rectangle rectToHighlight = new Rectangle(0, 0, 0, 0);
    Font editorFont = new Font("monospaced", 0, 12);
    private static int lineHeight;
    private static String frag;
    private static AUTOCOMPLETE_TYPE autocompleteType;
    private static ImageIcon methodIcon = kz.tamur.rt.Utils.getImageIcon("method");
    private static ImageIcon statMethodIcon = kz.tamur.rt.Utils.getImageIcon("methodStat");
    private int caretReturnPosition;//каретка возврата
    private Map<String,String> molds = new TreeMap<String,String>();//все шаблоны для быстрой вставки
    private JTable keyTable;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    private Kernel kernel = Kernel.instance();
    private LastPosition properties = new LastPosition(EditorPane.this);
    private Pattern pattern = Pattern.compile(".exec\\(\"(.*)\"", Pattern.UNICODE_CASE);
    private Pattern objectUIDPattern = Pattern.compile(".getObject\\(\"([^\"]*)\"", Pattern.UNICODE_CASE);//признак объекта
    private KrnClass clsUI;
    private KrnClass clsUser;
	private KrnClass clsProcessDef;
	private KrnClass clsFilter;
	private KrnClass clsReportPrinter;
	private KrnClass clsBoxExchange;
	private static Kernel krn = Kernel.instance();

	private	JPopupMenu methodsListPopup = new JPopupMenu();
    private DefaultListModel methodsListModel = new DefaultListModel(); 
    private JList methodsList = new JList(methodsListModel);
    
    public EditorPane(ExpressionEditor p, Map<String, String> varmap, Map<String, String> funcmap) {
    	panel = p;        
        vars = varmap;
        funcs = funcmap;
        setDocument(new ExprDoc(vars, funcs));
        setFont(editorFont);
        setBackground(Utils.getSilverColor());
        addKeyListener(this);
        addCaretListener(this);
        getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				UpdateCaretReturnPosition();				
			}
			
			public void insertUpdate(DocumentEvent e) {
				UpdateCaretReturnPosition();
			}
			
			public void changedUpdate(DocumentEvent e) {
				UpdateCaretReturnPosition();
			}
			
			private void UpdateCaretReturnPosition(){
				caretReturnPosition = getCaretPosition();				
			}
		});
        
        lineHeight = this.getFontMetrics(editorFont).getHeight();
        list.setBorder(BorderFactory.createLineBorder(POPUP_BACKGROUND_COLOR, 1));
        list.setCellRenderer(new CellRenderList());
        list.setOpaque(true);
        list.setBackground(POPUP_BACKGROUND_COLOR);
        list.setFocusable(false);
        list.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
        		if(e.getClickCount() == 2){       			
        			int ndx = list.getSelectedIndex();
        			if(ndx != -1){
        				smartInject();
        				hideAutocomplete();        				
        			}
        		}
        	}
        });
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setFocusable(false);
        
        fxpopup.add(scroll);
        fxpopup.setBorder(BorderFactory.createLineBorder(POPUP_BORDER_COLOR,1));
        fxpopup.setPopupSize(AUTOCOMPLETE_POPUP_WIDTH, AUTOCOMPLETE_POPUP_HEIGHT);
        fxpopup.setFocusable(false);
        
        popup.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        
        methodsList.setBorder(BorderFactory.createLineBorder(POPUP_BACKGROUND_COLOR, 1));
        methodsList.setCellRenderer(new CellRenderList());
        methodsList.setOpaque(true);
        methodsList.setBackground(POPUP_BACKGROUND_COLOR);
        methodsList.setFocusable(false);
        methodsList.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2) {
        			methodsListPopup.setVisible(false);
    				MethodItem item = (MethodItem) methodsList.getSelectedValue();
	        		openMethod(item.getMethod());	
        		}
        	}
        });
        
        JScrollPane methodsListScroll = new JScrollPane(methodsList);
        methodsListScroll.setBorder(null);
        methodsListScroll.setFocusable(false);

        methodsListPopup.add(methodsListScroll);
        methodsListPopup.setBorder(BorderFactory.createLineBorder(POPUP_BORDER_COLOR,1));
        methodsListPopup.setPopupSize(AUTOCOMPLETE_POPUP_WIDTH, AUTOCOMPLETE_POPUP_HEIGHT);
        methodsListPopup.setFocusable(false);

        keyTable = createKeyTable();
        JScrollPane keyTableScroll = new JScrollPane(keyTable);        
        keyTableScroll.setBorder(null);
        keyTableScroll.setFocusable(false);
        
        keypopup.add(keyTableScroll);
        keypopup.setBorder(BorderFactory.createLineBorder(POPUP_BORDER_COLOR,1));
        keypopup.setPopupSize(KEY_POPUP_WIDTH, KEY_POPUP_HEIGHT);
        keypopup.setFocusable(false);
        
        text.setWrapStyleWord(true);
        text.setFont(Utils.getDefaultFont());
        text.setBackground(Utils.getLightSysColor());
        setOpaque(false);
        setDoubleBuffered(true);
        setBackground(Utils.getSilverColor());
        addMouseListener(this);
        try {
			clsUI = krn.getClassByName("UI");
			clsProcessDef = krn.getClassByName("ProcessDef");
			clsFilter = krn.getClassByName("Filter");
			clsReportPrinter = krn.getClassByName("ReportPrinter");
			clsUser = krn.getClassByName("User");
			clsBoxExchange = krn.getClassByName("BoxExchange");
		} catch (KrnException error) {
			error.printStackTrace();
		}

        addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				
				if (properties.getStatus()) {
					AttributeSet attributeSet = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
					((DefaultStyledDocument) getDocument()).setCharacterAttributes(properties.start, properties.getLenght(), attributeSet, true);
					
				    Highlighter highlighter = EditorPane.this.getHighlighter();
				    Highlighter.Highlight[] highlighters = highlighter.getHighlights();
					for (int i = 0; i < highlighters.length; i++) {
					    if (highlighters[i].getPainter() instanceof UnderlineHighlightPainter) {
					    	highlighter.removeHighlight(highlighters[i]);
					    }
					}
					setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					properties.setStatus(false);
				}
				
				if (getText().length() == 0)
					return;
				
				int position = 0;
				try {
					position = viewToModel(e.getPoint());
				} catch (Exception ex) {
				}
				if (position == 0 || position == getText().length()) {
					return;
				}
				
				int lineStart = -1;
				int lineEnd = -1;
				
			    try {
					lineStart = Utilities.getRowStart(EditorPane.this, position);
					lineEnd = Utilities.getRowEnd(EditorPane.this, position);
				} catch (BadLocationException exception) {
					System.out.println("dddddddddddddddd = " + lineStart + ", " + lineEnd);
				}
			    try {
			    if (lineStart > -1 && lineEnd >= lineStart) {
					
					Matcher matcher = pattern.matcher(getText(lineStart, lineEnd - lineStart));
					Matcher matcherObject = objectUIDPattern.matcher(getText(lineStart, lineEnd - lineStart));
					while (matcher.find()) {
						int start = matcher.start(1) + lineStart;
						int end = matcher.end(1) + lineStart;
						if (position > start && position < end) {
			            	String methodName = getText(start, end - start);
			                KrnMethod[] methods = null;
							try {
								methods = kernel.getMethodsByName(methodName, ComparisonOperations.CO_EQUALS);
							} catch (KrnException exception) {
								exception.printStackTrace();
							}
							if (methods != null && methods.length > 0) {
								AttributeSet attributeSet = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
								((DefaultStyledDocument) getDocument()).setCharacterAttributes(start, end - start, attributeSet, true);
							    UnderlineHighlightPainter highlighter = new UnderlineHighlightPainter(Color.BLUE);
								EditorPane.this.getHighlighter().addHighlight(start, end, highlighter);
								if (properties.getKey())
									setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
								properties.init(start, end, methods);
							}
						}
					}
					while (matcherObject.find()) {
						int start = matcherObject.start(1) + lineStart;
						int end = matcherObject.end(1) + lineStart;
						if (position > start && position < end) {
			            	String objectUID = getText(start, end - start);
			                KrnObject object = null;
							try {
								object = kernel.getObjectByUid(objectUID, 0);
							} catch (KrnException exception) {
								exception.printStackTrace();
							}
							if (object != null) {
								AttributeSet attributeSet = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
								((DefaultStyledDocument) getDocument()).setCharacterAttributes(start, end - start, attributeSet, true);
							    UnderlineHighlightPainter highlighter = new UnderlineHighlightPainter(Color.BLUE);
								EditorPane.this.getHighlighter().addHighlight(start, end, highlighter);
								if (properties.getKey())
									setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
								properties.init(start, end, object);
							}
						}
					}
			    }
				} catch (BadLocationException exception) {
					exception.printStackTrace();
				}
			}
		});
        setSize(30000, 30000);
        Insets insets = new Insets(5, 0, 5, 5);
        setMargin(insets);

        TabStop[] tstops = new TabStop[10];
        for (int i = 0; i < 10; i++) {
            tstops[i] = new TabStop(50 * (i + 1), TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
        }
        TabSet tabs = new TabSet(tstops);
        Style style = getLogicalStyle();
        StyleConstants.setTabSet(style, tabs);
        setLogicalStyle(style);
        setSelectionColor(new Color(82, 109, 165));
        setSelectedTextColor(Color.WHITE);
        readClassNames();
    }

    private void openMethod(final KrnMethod method) {
    	try {
//			if (krn.getBindingModuleToUserMode()) {
//	        	if (method.ownerId > 0) {
//	        		long currentUserId = krn.getUserSession().userObj.id;
//	        		if (method.ownerId != currentUserId) {
//	        			KrnObject userObj = krn.getObjectById(method.ownerId, 0);
//	        			if (userObj != null) {	// Владелец метода существует
//	            			KrnClass userCls = krn.getClassByName("User");
//	            			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
//	            			String userName = krn.getStringsSingular(method.ownerId, userNameAttr.id, 0, false, false);
//	                        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного метода является пользователь " + userName + "!");
//	        			}
//	        		}
//	        	}
//	        }
			final ClassNode clsNode = krn.getClassNode(method.classId);
			String expr = clsNode.getMethodExpression(method.name);
	        JTextArea exprArea = new JTextArea();
	        exprArea.setText(expr);
	        final ExpressionEditor ex = new ExpressionEditor(exprArea.getText(), EditorPane.this, method, true, true);
	        ex.setSourceMethodToDebugger(method);
	        Container cont = getTopLevelAncestor();
	        DesignerDialog dlg = new DesignerDialog(cont instanceof JFrame ? (JFrame) cont : (Dialog) cont, method.name, ex);
	        String authorStr = "Автор: " + (kz.tamur.comps.Utils.getMethodOwner(method)!= null? kz.tamur.comps.Utils.getMethodOwner(method): "");
			dlg.setMethodAuthor(authorStr);
	        ex.setDialog(dlg);
	        Dimension dim = kz.tamur.comps.Utils.getMaxWindowSizeActDisplay();
	        dlg.setSize(new Dimension(dim.width - 300, dim.height - 300));
	        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
	        dlg.setOkEnabled(!ex.isReadOnly());
	        dlg.show();
	        if (dlg.isOK()) {
	        	try {
					if(!ex.isReadOnly()) {
						clsNode.changeMethod(method.uid, method.name, method.isClassMethod, ex.getExpression());
						try {
							Kernel.instance().unlockMethod((String) method.uid);
						} catch (KrnException e1) {
							e1.printStackTrace();
						}
					}
				} catch (KrnException exception) {
					exception.printStackTrace();
					MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + exception.getMessage());
				}
	        } else {
	        	if (ex.isMovedToMain()) {
	        		TabObj tabObj = EditorWindow.TabWnd.DTP.TabMap.get(method.uid);
	        		if (tabObj != null) {
	        			EditorWindow.TabWnd.DTP.selectTab(tabObj.comp);
	        			EditorWindow.TabWnd.toFront();
	        		} else {
	        			//                        	if (method.ownerId > 0) {
	        			//                        		long currentUserId = krn.getUserSession().userObj.id;
	        			//                        		if (method.ownerId != currentUserId) {
	        			//                        			KrnObject userObj = krn.getObjectById(method.ownerId, 0);
	        			//                        			if (userObj != null) {	// Владелец метода существует
	        			//	                        			KrnClass userCls = krn.getClassByName("User");
	        			//	                        			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
	        			//	                        			String userName = krn.getStringsSingular(method.ownerId, userNameAttr.id, 0, false, false);
	        			//		                                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного метода является пользователь " + userName + "!");
	        			//                        			}
	        			//                        		}
	        			//                        	}

	        			boolean readOnly=false;
	        			try {
	        				UserSessionValue us = krn.vcsLockModel(method.uid, ModelChange.ENTITY_TYPE_METHOD);
	        				if (us != null) {
	        					StringBuilder mess = new StringBuilder();
	        					mess.append("Метод '").append(method.name).append("' редактируется!\nПользователь: ").append(us.name).append("\nОткрыть метод в режиме просмотра?");
	        					int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess.toString(), 235, 130);
	        					if (res == BUTTON_YES) {
	        						readOnly = true;
	        					} else {
	        						return;
	        					}
	        				}
	        				if (!readOnly) readOnly = !krn.getUser().hasRight(Or3RightsNode.METHODS_EDIT_RIGHT);
	        				if(!readOnly){
	        					us = krn.blockMethod(method.uid);
	        					if (us != null) {
	        						StringBuilder mess = new StringBuilder();
	        						mess.append("Метод '").append(method.name).append("' заблокирован!\nПользователь: ").append(us.name).append("\nIP адрес: ").append(us.ip).append("\nИмя компьютера: ").append(us.pcName).append("\nОткрыть метод в режиме просмотра?");
	        						int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess.toString(), 235, 130);
	        						if (res == BUTTON_YES) {
	        							readOnly = true;
	        						} else {
	        							return;
	        						}
	        					}
	        				}
	        			} catch (KrnException exc) {
	        				exc.printStackTrace();
	        			}
	        			final ExpressionEditor exEd = new ExpressionEditor(exprArea.getText(), this, method, readOnly);
	        			exEd.setSourceMethodToDebugger(method);
	        			JLabel ownerLabel = Utils.createLabel("Автор: " + (kz.tamur.comps.Utils.getMethodOwner(method)!= null? kz.tamur.comps.Utils.getMethodOwner(method): ""));
	        			exEd.getStatusBar().addLabel(ownerLabel);

	        			ActionListener btnaction = new ActionListener() {
	        				@Override
	        				public void actionPerformed(ActionEvent e) {
	        					try {
	        						clsNode.changeMethod(method.uid, method.name, method.isClassMethod, exEd.getExpression());
	        					} catch (KrnException e1) {
	        						e1.printStackTrace();
	        						Container cnt = getTopLevelAncestor();
	        						MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE, "Нельзя редактировать метод!\r\n" + e1.getMessage());
	        					}
	        				}
	        			};
	        			String title = clsNode.getName() + "." + method.name; 
	        			EditorWindow.addTab(method.uid, title, exEd, btnaction, "ClassBrowser");
	        		}

				} else {
					if(!ex.isReadOnly()) {
						try {
							Kernel.instance().unlockMethod((String) method.uid);
						} catch (KrnException e1) {
							e1.printStackTrace();
						}
					}
				}
	        }
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }
    
    private org.jdom.Element getXml(KrnObject obj) {
        Kernel krn = Kernel.instance();
        org.jdom.Element xml = null;
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
    
    //open an Interface panel and close DesignerDialog
    private void openInterface(final KrnObject obj) {
    	final Container cont = getTopLevelAncestor();
    	Or3Frame.instance().jumpInterface(obj);
//         	EditorPane.this.grabFocus();
    	Or3Frame.instance().requestFocus();
         	if (cont instanceof DesignerDialog) cont.setVisible(false);
    }
    //open a Process panel and close DesignerDialog
    private void openProcess(final KrnObject obj) {
    	final Container cont = getTopLevelAncestor();
    	Or3Frame.instance().jumpService(obj);
//    		EditorPane.this.grabFocus();
    	Or3Frame.instance().requestFocus();
    		if (cont instanceof DesignerDialog) cont.setVisible(false);
    }
    //open a User panel and close DesignerDialog
    private void openUser(final KrnObject obj) {
    	final Container cont = getTopLevelAncestor();
    	Or3Frame.instance().jumpUser(obj);
//    		EditorPane.this.grabFocus();
    	Or3Frame.instance().requestFocus();
    		if (cont instanceof DesignerDialog) cont.setVisible(false);
    }
    	
    
    
    private void openFilter(final KrnObject obj) {
    	FiltersTree tree = kz.tamur.comps.Utils.getFiltersTree(obj);
    	final Container cont = getTopLevelAncestor();
    	JPanel filterPane = new JPanel();
    	final DesignerDialog dlg = new DesignerDialog(cont instanceof JFrame ? (JFrame) cont : (Dialog) cont, "", filterPane);
    	dlg.getEmptyLab().setText("Владелец: " + (kz.tamur.comps.Utils.getObjOwner(obj) != null ? kz.tamur.comps.Utils.getObjOwner(obj): ""));
    	FilterNode filter = (FilterNode) tree.find(obj);
		JScrollPane sp = null;
		final Kernel krn = Kernel.instance();
		OrFilterNode node;
	    try {
	    	byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
	        if (data.length > 0) {
		    	org.jdom.Element xml = getXml(obj);   
		        node = (OrFilterNode) Factories.instance().create(xml, Mode.PREVIEW, filter);
		    } else {
		        node = (OrFilterNode) Factories.instance().create("FilterNode", filter);
		        node.getXml().getChild("title").setText(filter.toString());
		    }
		    KrnObject lang = krn.getInterfaceLanguage();
		    long langId = (lang != null) ? lang.id : 0;
		    node.setLangId(langId);
		    node.setPropertyValue(new PropertyValue(node.getXml().getChild("title"), node.getProperties().getChild("title"), langId));
		    OrFilterTree ftree = new OrFilterTree(node, obj, null);
		    ftree.setEditable(false);
		    sp = new JScrollPane(ftree);
		    validate();
		    repaint();
		    // Сохранение в историю
		    HistoryWithDate hwd = new HistoryWithDate(obj, new Date());
		    krn.getUser().addFltInHistory(hwd,filter.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
		JButton moveToMainBtn = ButtonsFactory.createToolButton("MoveToEditor.png", "Переместить в редактор");
		moveToMainBtn.addActionListener(new ActionListener() {
           	public void actionPerformed(ActionEvent e) {
            	Or3Frame.instance().jumpFilter(obj);            	
            	dlg.dispose();
//            	EditorPane.this.grabFocus();
            	Or3Frame.instance().getFiltersFrame().requestFocus();
            	if (cont instanceof JFrame)  ((JFrame) cont).setState(Frame.ICONIFIED);
            	if (cont instanceof DesignerDialog) 
            		cont.setVisible(false);
            }
       });
		
		toolBar.add(moveToMainBtn);
		filterPane.setLayout(new BorderLayout());
	    filterPane.add(toolBar, BorderLayout.NORTH);
		filterPane.add(sp, BorderLayout.CENTER);
		dlg.setTitle(filter.toString());
		dlg.add(filterPane);
		Dimension dim = kz.tamur.comps.Utils.getMaxWindowSizeActDisplay();
		dlg.setSize(new Dimension(dim.width - 500, dim.height - 500));
		dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
		dlg.show();
	}
    
 	private void openReport(final KrnObject obj) {
 		/*final Container cont = getTopLevelAncestor();
    	Or3Frame.instance().jumpReport(obj);
    		EditorPane.this.grabFocus();
    		if (cont instanceof DesignerDialog) cont.setVisible(false);*/
 		final Container cont = getTopLevelAncestor();
		JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
		JButton moveToMainBtn = ButtonsFactory.createToolButton("MoveToEditor.png", "Переместить в редактор");
		
		ReportPanel report_panel = new ReportPanel();
		toolBar.add(moveToMainBtn);
		report_panel.add(toolBar, BorderLayout.NORTH);
		report_panel.load(obj);
		final DesignerDialog dlg = new DesignerDialog(cont instanceof JFrame ? (JFrame) cont : (Dialog) cont, "Отчеты", report_panel);
		moveToMainBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Or3Frame.instance().jumpReport(obj);
//            	EditorPane.this.grabFocus();
				dlg.dispose();
				Or3Frame.instance().requestFocus();
				if (cont instanceof JFrame)  ((JFrame) cont).setState(Frame.ICONIFIED);
				if (cont instanceof DesignerDialog) cont.setVisible(false);
//            cont.setVisible(false); 
//            if (cont instanceof DesignerDialog) cont.setVisible(false);
//            cont.setVisible(false); 

           	}
		});
        Dimension dim = kz.tamur.comps.Utils.getMaxWindowSizeActDisplay();
        dlg.setSize(new Dimension(dim.width - 300, dim.height - 300));
        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
        dlg.show();
		
	}
 	
 	 //open a BoxExchange("Пункт обмена") panel and close DesignerDialog
 	 private void openBoxExchange(final KrnObject obj) {
 		final Container cont = getTopLevelAncestor();
     	Or3Frame.instance().jumpBox(obj);
//     		EditorPane.this.grabFocus();
      	Or3Frame.instance().requestFocus();
     		if (cont instanceof DesignerDialog) cont.setVisible(false);
 		
 	}
 	 
 	 
  
    public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();

        if (parent instanceof JViewport) {
            int w = ((JViewport) parent).getWidth();

            Dimension
                    min = ui.getPreferredSize(this),
                    max = ui.getMaximumSize(this);

            if ((w >= min.width) && (w <= max.width)) return true;
        }

        return false;
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    		properties.setKey(true);
    	}
        Document doc = getDocument();
        try {
        	//Обработка клавиш на окне автодополнения
            if(fxpopup.isShowing()){
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				     hideAutocomplete();
				     return;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT){//Стрелка "Вправо"
					if(getCaretPosition() == Utilities.getRowEnd(this, getCaretPosition())){
						hideAutocomplete();
					}
					return;
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_PERIOD) {//Клавиша "Enter"
					smartInject();
					hideAutocomplete();
					e.consume();
					return;
				} else if (ClientUtils.navigationList(list, listModel, e.getKeyCode(), 11)) {//Навигация по List
					e.consume();//поглотить клавишу
					return;
				}            	 
            }
            //Обработка клавиш на окне списка комбинаций
            if(keypopup.isShowing()){
            	if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            		hideCombinations();
            		return;
            	}else if(ClientUtils.navigationTable(keyTable, e.getKeyCode(), 13)){//Навигация по таблице
            		e.consume();//поглотить клавишу
            		return;
            	}
            }        	
        	
            if (e.getKeyChar() == '9' && e.isControlDown()) {//вставка скобок ()
            	int pos = getCaretPosition();
                doc.insertString(pos, "()", null);
            } else if (e.getKeyChar() == '9' && e.isAltDown()) {//вставка скобок () + курсор между скобок
            	int pos = getCaretPosition();
                doc.insertString(pos, "()", null);
                setCaretPosition(pos + 1);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {//{CTRL + SPACE}
            	showAutocomplete();
            } else if (e.getKeyCode() == KeyEvent.VK_K && e.isControlDown()){//{CTRL + K}
            	showCombinations();
            } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                String need = "";
                int pos = getCaretPosition();
                int start = Utilities.getWordStart(this, pos);
                int end = Utilities.getWordEnd(this, pos);
                String txt = getText(start, end - start);
                if (txt.indexOf(".") > 0) {
                    String[] parts = txt.split("[.]");
                    for (int i = 0; i < parts.length; i++) {
                        if (pos > start && pos < start + parts[i].length()) {
                            need = parts[i];
                            break;
                        } else {
                            start += parts[i].length();

                        }
                    }
                } else
                    need = txt;
                if (!need.equals("")) {
                    String desc = "";
                    if (vars.containsKey(need)) {
                        desc = (String) vars.get(need);
                    } else if (funcs.containsKey(need)) {
                        desc = (String) funcs.get(need);
                    }
                    if (desc != null && desc.length() > 0) {
                        int X = (int) (((getCaret()).getMagicCaretPosition()).getX());
                        int Y = (int) (((getCaret()).getMagicCaretPosition()).getY());
                        text.setText(desc);
                        text.setOpaque(true);
                        text.setEditable(false);
                        popup.add(text);
                        popup.show(this, X, Y + 17);
                    }
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_G && e.isControlDown())) {//{CTRL + G}
                showGoLineDlg();
            } else if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {//{CTRL + F}
                showFindDlg(0);
            } else if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {//{CTRL + R}
                showFindDlg(1);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB && !e.isShiftDown()) {//{TAB}
            	e.consume();
                addTabs();
            } else if (e.getKeyCode() == KeyEvent.VK_TAB && e.isShiftDown()) {//{SHIFT + TAB}
            	e.consume();
                removeTabs();
            } else if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {//{CTRL + Z}
                panel.undo();
            } else if (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown()) {//{CTRL + Y}
                panel.redo();
            } else if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown()) {//{CTRL + Q} 	
            	setCaretPosition(caretReturnPosition);
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {//{ENTER}
            	e.consume();
            	smartTab();
            } else if ((e.getKeyCode() == KeyEvent.VK_SLASH 
            		|| e.getKeyCode() == KeyEvent.VK_DIVIDE) && e.isControlDown()){//{CTRL + /}
            	//Комментирование / Раскомментирование кода
            	smartComments();
            } else if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()){//{CTRL + D}
            	deleteLines();
            } else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()){//{CTRL + S}
            	panel.saveBut();
            } else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()){//{CTRL + S}
	        	panel.saveBut();
	        }
	        } catch (BadLocationException e1) {
        	System.out.println("Ошибка позиционирования курсора (BadLocationException)");
        }
    }
    
    //показать popup автодополнения
    synchronized private void showAutocomplete() throws BadLocationException{
    	Caret curCaret = getCaret();
    	if(curCaret == null) return;
    	Point caretPoint = curCaret.getMagicCaretPosition();
    	if(caretPoint == null) return;
    	
        int X = (int) (caretPoint.getX());
        int Y = (int) (caretPoint.getY());
        Point point = computeCoord(X, Y);
        X = (int)point.getX();
        Y = (int)point.getY();  
        
        fillPopup();
        if(listModel.getSize() > 0){
        	fxpopup.show(this,X,Y);
        	list.setSelectedIndex(0);
        	list.ensureIndexIsVisible(0);
        }else{
        	hideAutocomplete();
        }
    	
    }
    
    //показать popup акселераторов
    private void showCombinations(){
    	int leftX = (this.getWidth() - KEY_POPUP_WIDTH) / 2;
    	keypopup.show(this,leftX,0);
    }
    
    /**
     * вернуть всю строку, в которой находится каретка
     * @return
     * @throws BadLocationException
     */
    private Line getCurLine() throws BadLocationException{
    	Document doc = getDocument();
    	int caretPos = getCaretPosition();//Текущая позиция курсора
    	int rowStartPos = Utilities.getRowStart(this, caretPos);//Позиция начала строки
        int rowEndPos = Utilities.getRowEnd(this, caretPos);//Позиция конца строки
        int posInLine = caretPos - rowStartPos;
        String txt = doc.getText(rowStartPos, rowEndPos - rowStartPos);
    	return new Line(txt,posInLine);
    }
    
    //проверить окружена ли текущая позиция курсора кавычками
    public boolean isSurroundQuotes(){
    	try {
			Line line = getCurLine();
			int pos = line.getPosInLine();
			String str = line.getLine();
			if(str != null && pos != 0 && pos != str.length()){
				if(str.substring(pos-1, pos).equals("\"") && str.substring(pos, pos+1).equals("\"")){
					return true;
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    //вычисление координаты показа меню автодополнения
    private Point computeCoord(int curX,int curY){
    	double scrHeight = getToolkit().getScreenSize().getHeight();
    	double elemTop = getLocationOnScreen().getY() + curY;
    	
    	int x = curX;
    	int y = curY;    	
    	
    	if(elemTop + AUTOCOMPLETE_POPUP_HEIGHT + getLineHeight() >= scrHeight){
    		y -= AUTOCOMPLETE_POPUP_HEIGHT;
    	}else{
    		y += getLineHeight();
    	}
    	
    	return new Point(x,y);
    }
    
    // "Умное" внедрение дополненного кода
	synchronized private void smartInject() {
		try {
			int ndx = list.getSelectedIndex();
			if (ndx != -1) {
				Document doc = getDocument();
				int caretPos = getCaretPosition();	// Текущая позиция курсора 
				int rowStartPos = Utilities.getRowStart(this, caretPos);	// Позиция начала строки 
				int rowEndPos = Utilities.getRowEnd(this, caretPos);	// Позиция конца строки
				int posInLine = caretPos - rowStartPos;
				String txt = doc.getText(rowStartPos, rowEndPos - rowStartPos);
				Object obj = listModel.get(ndx);
				if (obj instanceof String) {
					switch (autocompleteType) {
					case VAR:
						matcher = varPattern.matcher(txt);
						smartInject(matcher, rowStartPos, posInLine, (String) obj, 0);
						break;
					case OPERATOR:
						matcher = operPattern.matcher(txt);
						smartInject(matcher, rowStartPos, posInLine, (String) obj, 0);
						break;
					case FUNCTION:
						matcher = funcPattern.matcher(txt);
						while (matcher.find()) {
							if (matcher.start(1) <= posInLine && posInLine <= matcher.end(1)) {
								doc.remove(rowStartPos + matcher.start(1), matcher.end(1) - matcher.start(1));
								String inj = (String) obj;
								if (!"createObject".equals(inj)
										&& !"getClassObjects".equals(inj)
										&& !"getClass".equals(inj)
										&& !"getAttr".equals(inj)
										&& !"setAttr".equals(inj)
										&& !"deleteAttr".equals(inj)
										&& !"createQuery".equals(inj)
										&& !"addPath".equals(inj)
										&& !"sexec".equals(inj)
										&& !"exec".equals(inj)
										&& !"find".equals(inj)){
									doc.insertString(rowStartPos + matcher.start(1), inj + "()", null);
									setCaretPosition(getCaretPosition() - 1);	// Возвращаем курсор на одну позицию назад
								} else {
									doc.insertString(rowStartPos + matcher.start(1), inj + "(\"\")", null);
									setCaretPosition(getCaretPosition() - 2);	// Возвращаем курсор на две позиции назад
								}
								break;
							}
						}
						break;
					}
				} else if (obj instanceof KrnClass) {
					switch (autocompleteType) {
					case CLASS:
						matcher = classNamePattern.matcher(txt);
						smartInject(matcher, rowStartPos, posInLine, ((KrnClass) obj).name, 1);
						break;
					case CLASS2:
						matcher = classNamePattern2.matcher(txt);
						smartInject(matcher, rowStartPos, posInLine, ((KrnClass) obj).name, 1);
						break;
					}
				} else if (obj instanceof KrnAttribute) {
					switch (autocompleteType) {
					case ATTRIBUTE:
						matcher = attributeNamePattern.matcher(txt);
						smartInject(matcher, rowStartPos, posInLine, ((KrnAttribute) obj).name, 1);
						break;
					}
				} else if (obj instanceof KrnMethod) {
					switch (autocompleteType) {
					case METHOD:
						matcher = beginMethodFromGetClassPattern.matcher(txt);
						if (matcher.find()) {
							matcher.reset();
							smartInject(matcher, rowStartPos, posInLine, ((KrnMethod) obj).name, 2);
							// Замена названия класса, если он указан неверно
							try {
								KrnClass cls = krn.getClass(((KrnMethod) obj).classId);
								if (!cls.name.equals(matcher.group(1))) {
									posInLine = matcher.end(1);
									matcher.reset();
									smartInject(matcher, rowStartPos, posInLine, cls.name, 1);
								}
							} catch (KrnException e) {
								e.printStackTrace();
							}
						} else {
							matcher = beginMethodPattern.matcher(txt);
							if (matcher.find()) {
								matcher.reset();
								smartInject(matcher, rowStartPos, posInLine, ((KrnMethod) obj).name, 1);
							}
						}
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
    
    //вставка текста
    void smartInject(Matcher matcher,int rowStartPos,int posInLine,String inj,int group) throws BadLocationException{
    	Document doc = getDocument();
    	while(matcher.find()){
			if(matcher.start() < posInLine && posInLine <= matcher.end()){
				doc.remove(rowStartPos + matcher.start(group), matcher.end(group) - matcher.start(group));				 
				doc.insertString(rowStartPos + matcher.start(group), inj, null);
				break;
			}
		}
    }
    
    //быстрая вставка
    synchronized boolean fastInject(String line){
    	//System.out.println("fastInject(" + line + ")");
    	try{
	    	matcher = fastInjectPattern.matcher(line);
	    	if(matcher.find()){
	    		int ndxOffset = 1, ndxAbbr = 2;
	    		String offset = matcher.group(ndxOffset);
	    		String abbr = matcher.group(ndxAbbr);
	    		int caretPos = getCaretPosition();//Текущая позиция курсора
	    		int rowStartPos = Utilities.getRowStart(this, caretPos);//Позиция начала строки
	    		Document doc = getDocument();
	    		String mold = molds.get(abbr.toLowerCase(Constants.OK));
	    		if(mold != null){
	    			doc.remove(rowStartPos + matcher.start(ndxAbbr),matcher.end() - matcher.start(ndxAbbr));
	    			String str = mold.replaceAll("\\*", offset);
	    			int cursor = str.indexOf("|");//позиция курсора в шаблоне
	    			str = str.replace("|", "");//удаляем пометку курсора
	    			doc.insertString(rowStartPos + matcher.start(2),str, null);   			
	    			if(cursor != -1){
	    				cursor = rowStartPos + matcher.start(ndxAbbr) + cursor;
	    				setCaretPosition(cursor);
	    			}
	    			return true;
	    		}
	    	}	    	
    	}catch(BadLocationException e){
    		e.printStackTrace();
    	}
    	return false;
    }

    //скрыть меню автодополнения
    void hideAutocomplete(){
    	fxpopup.setVisible(false);    	
    }
    
  //скрыть окно подсказок комбинаций
    void hideCombinations(){
    	keypopup.setVisible(false);    	
    }
    
    //получить высоту строки
    private int getLineHeight(){    	
    	return lineHeight;
    }
    
    //"умное" выравнивание кода
    synchronized private void smartTab() throws BadLocationException{
    	Line line = getCurLine();
    	matcher = smartTabPattern.matcher(line.getLine());    	    	
    	if(matcher.find()){
    		int ndxOffset = 1;//группа смещения
    		String offset = matcher.group(ndxOffset);
    		String lineBegin = line.getLineBegin();
        	int caretPos = getCaretPosition();//текущая позиция курсора    		
    		Document doc = getDocument();
    		String offsetStr = "\n" + offset;
    		if(isAddIndention(lineBegin)){
    			offsetStr += "\t";
    		}
    		doc.insertString(caretPos, offsetStr, null);
    		setCaretPosition(caretPos + offsetStr.length());
    	}
    }
    
    //Проверка на подстановку отступа
    private boolean isAddIndention(String line){
    	line = consumeComment(line);
    	matcher = blockLinePattern.matcher(line);
    	if(matcher.find()){
    		int ndx = 2;//#end и всё, что ему предшествует
    		System.out.println(ndx + " = " + matcher.group(ndx));
    		if(matcher.group(ndx) == null){   
    			return true;
    		}    		
    	}
    	return false;
    }
    
    //Очистка строки от комментариев
    private String consumeComment(String str){//очистить код от комментариев
    	return str.replaceAll("\\/\\*.*?\\*\\/", "").replaceAll("\\/\\/.*$", "");
    }

    //"умное" комментирование кода
	synchronized private void smartComments() throws BadLocationException{
		CodeBlock block = new CodeBlock(this);		
 		matcher = commentLine.matcher(block.getText());
 		String procText = "";
 		
 		//предварительный анализ на закомментированность всех строк
 		boolean allComment = true;//состояние, когда все строки закомментированы
 		int ndxComment = 1;//группа комментария 		
 		while(matcher.find() && !"".equals(matcher.group())){
 			if(matcher.group(ndxComment) == null){
 				allComment = false;
 				break;
 			}
 		}
 		System.out.println(procText);
 		if(!allComment || block.isEmptyBlock()){
 			//закомментировать код
 			procText = "//" + block.getText().replaceAll("\\n", "\n//");
 		}else{
 			//убрать комментарии
 			procText = "";
 			matcher.reset();
 			while(matcher.find()){
 				procText += matcher.group().replaceFirst("\\/\\/", "");
 			}
 		}
 		block.replaceSelection(procText);
 		if(block.isCodeBlock()){
 			block.selectBlock(procText);
 		}
	}

	//смещение кода вправо
    synchronized private void addTabs() throws BadLocationException{
    	CodeBlock block = new CodeBlock(this);
		block.selectBlock();
        String procText = "\t" + block.getText().replaceAll("\\n", "\n\t");
        if (procText.equals("\t")) {
        	block.insertTab();
        }
        block.replaceSelection(procText);
        if (block.isCodeBlock()) {
    		block.selectBlock(procText);
        } else {
        	block.moveCaret(+1);
        }
    }
    
    //смещение кода влево
    synchronized private void removeTabs() throws BadLocationException{
    	CodeBlock block = new CodeBlock(this);
        matcher = unTabLine.matcher(block.getText());
        String procText = "";
        int ndxWithoutTab = 2;
        while (matcher.find()) {
        	procText += matcher.group(ndxWithoutTab);
        }
        block.replaceSelection(procText);
        if (block.isCodeBlock()){
        	block.selectBlock(procText);
        } else {
        	block.moveCaret(-1);
        }
    }
    
    //удаление строк
    synchronized private void deleteLines() throws BadLocationException{
    	CodeBlock block = new CodeBlock(this);
    	block.delete();
    }

    //освобождение клавишы
    public void keyReleased(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    		properties.setKey(false);
    	}
    	try {
    		//вызов автодополнения
			if(fxpopup.isShowing()){
				if(!(e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown())
						&& e.getKeyCode() != KeyEvent.VK_CONTROL
						&& e.getKeyCode() != KeyEvent.VK_DOWN
						&& e.getKeyCode() != KeyEvent.VK_UP
						&& e.getKeyCode() != KeyEvent.VK_PAGE_DOWN
						&& e.getKeyCode() != KeyEvent.VK_PAGE_UP
						&& e.getKeyCode() != KeyEvent.VK_HOME
						&& e.getKeyCode() != KeyEvent.VK_END){
					showAutocomplete();
				}				
			}else{
				if(e.getKeyCode() == KeyEvent.VK_PERIOD){
					showAutocomplete();
				}
			}
			
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
    }

    public void caretUpdate(CaretEvent e) {
        removeHighlights(this, hilite);
        int pos = getCaretPosition();
        if (pos != 0) {
            try {
                Document doc = getDocument();
                if (doc.getLength() >= pos + 1) {
	                String sign = doc.getText(pos, 1);
	                if (sign.equals("(")) {
	                    String str_end = doc.getText(pos, doc.getLength() - pos);
	                    int q_pos = getLastPos(str_end, '(');
	                    highlight(pos, "(", hilite);
	                    highlight(q_pos + pos, ")", hilite);
	                } else if (sign.equals(")")) {
	                    String str_begin = doc.getText(0, pos);
	                    int q_pos = getLastPos(str_begin, ')');
	                    highlight(pos, ")", hilite);
	                    highlight(q_pos, "(", hilite);
	                } else if (sign.equals("#")) {
	                    if (doc.getLength() >= pos + 3) {
		                    String sign1 = doc.getText(pos,3);
		                    int end_pos=doc.getLength() - pos;
		                    if (sign1.equals("#en")) {
		                        String str_end = doc.getText(0, pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#end");
		                        if(q_poss.size()>0) {
		                            pos = q_poss.get(0).first;
		                            end_pos = doc.getLength() - pos;
		                            sign1 = doc.getText(pos,3);
		                        }
		                    } else if(sign1.equals("#el")) {
		                            String str_end = doc.getText(0,pos);
		                            ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#else");
		                            if(q_poss.size()>0) {
		                                pos = q_poss.get(0).first;
		                                end_pos=doc.getLength() - pos;
		                                sign1 = doc.getText(pos,3);
		                            }
		                    } else if(sign1.equals("#ca")) {
		                        String str_end = doc.getText(0, pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#catch");
		                        if(q_poss.size()>0) {
		                            pos = q_poss.get(0).first;
		                            end_pos=doc.getLength() - pos;
		                            sign1 = doc.getText(pos,3);
		                        }
		                    } else if(sign1.equals("#fi")) {
		                        String str_end = doc.getText(0, pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#finally");
		                        if(q_poss.size()>0) {
		                            pos = q_poss.get(0).first;
		                            end_pos=doc.getLength() - pos;
		                            sign1 = doc.getText(pos,3);
		                        }
		                    }
		                    if(sign1.equals("#fo")){
		                        String str_end = doc.getText(pos, end_pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#foreach");
		                        if(q_poss.size()>0) {
			                        highlight(pos, "#foreach", hilite);
			                        highlight(q_poss.get(q_poss.size()-1).first + pos, "#end", hilite);
		                        }
		                    }else if(sign1.equals("#wh")){
		                        String str_end = doc.getText(pos, end_pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#while");
		                        highlight(pos, "#while", hilite);
		                        highlight(q_poss.get(q_poss.size()-1).first + pos, "#end", hilite);
		                    }else if (sign1.equals("#fu")) {
		                        String str_end = doc.getText(pos, end_pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#function");
		                        highlight(pos, "#function", hilite);
		                        highlight(q_poss.get(q_poss.size()-1).first + pos, "#end", hilite);
		                    }else if(sign1.equals("#if")){
		                        String str_end = doc.getText(pos, end_pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#if");
		                        highlight(pos, "#if", hilite);
		                        int count=q_poss.size();
		                        highlight(q_poss.get(q_poss.size()-1).first + pos, "#end", hilite);
		                        for(int i=0;i<count-1;i++) {
		                            highlight(q_poss.get(i).first + pos, q_poss.get(i).second, hilite);
		                        }
		                    } else if(sign1.equals("#tr")){
		                        String str_end = doc.getText(pos, end_pos);
		                        ArrayList<Pair<Integer, String>> q_poss = getLastPos(str_end, "#try");
		                        highlight(pos, "#try", hilite);
		                        int count=q_poss.size();
		                        highlight(q_poss.get(q_poss.size()-1).first + pos, "#end", hilite);
		                        for(int i=0;i<count-1;i++) {
		                            highlight(q_poss.get(i).first + pos, q_poss.get(i).second, hilite);
		                        }
		                    }
	                    }
	                }
                }
            } catch (BadLocationException e1) {
            	System.out.println("caretUpdate - Error BadLocationException");
                e1.printStackTrace();
            }

        }

        int dot = e.getDot();
        int mark = e.getMark();
        
        if (dot == mark) {
            try {
                Rectangle caretCoords = modelToView(dot);
                if (caretCoords != null) {
                    panel.posinfo.setText(((caretCoords.y + 1) / 17 + 1) + " : " + ((caretCoords.x - 11) / 7 + 1));
                }
            } catch (BadLocationException ble) {
                System.out.println("caretUpdate - BadLocationException");
            } catch (Exception ble) {
                System.out.println("caretUpdate - Exception");
            }
            painter();
        }
    }

    public void painter() {
        Element root = this.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(getCaretPosition());
        Element lineElement = root.getElement(line);
        int start = lineElement.getStartOffset();
        Linehighlight(start);
    }

    public void Linehighlight(int start) {
        try {
            Rectangle r = modelToView(start);
            if (r == null) return;
            r.x = 0;
            r.width = getWidth();
            if (!r.equals(rectToHighlight)) {
                repaint(rectToHighlight);
                rectToHighlight = r;
                repaint(rectToHighlight);
            }
        } catch (Exception e) {}
    }


    protected void paintComponent(Graphics g) {
        g.setColor(Utils.getSilverColor());
        Rectangle r = getVisibleRect();
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(new Color(255, 255, 215));
        g.fillRect(rectToHighlight.x,
                rectToHighlight.y,
                rectToHighlight.width,
                rectToHighlight.height);
        super.paintComponent(g);
    }

    public void highlight(int pos, String pattern, Highlighter hilite) throws BadLocationException {
        hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
    }

    public void removeHighlights(JTextComponent textComp, Highlighter hilite) {
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i = 0; i < hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    
    public void fillMethodsList(KrnMethod[] methods) {
    	methodsListModel.clear();
    	methodsListModel = new DefaultListModel();
   		for (int i = 0; i < methods.length; i++) {
			methodsListModel.addElement(new MethodItem(methods[i]));
		}
   		methodsList.setModel(methodsListModel);

    }

    public void mouseClicked(MouseEvent e) {
	   	if (properties.getStatus() && properties.getKey() && properties.getMethods() != null) {
	   		if (properties.getMethods().length == 1) {
        		openMethod(properties.getMethods()[0]);	
	   		} else {
		   		fillMethodsList(properties.getMethods());
		   		methodsListPopup.show(this, e.getX(), e.getY());
	   		}
    	   		
//    		MethodEditorPanel methodEditorPanel = new MethodEditorPanel(properties.getMethods());
//    		final JDialog dialog = new JDialog();
//    		dialog.setResizable(false);
//    		dialog.setMaximumSize(new Dimension(514, 313));
//    		dialog.setMinimumSize(new Dimension(514, 313));
//    		dialog.setPreferredSize(new Dimension(514, 313));
//    		dialog.add(methodEditorPanel);
//    		dialog.setLocation(new Point(e.getXOnScreen(), e.getYOnScreen()));
//    		dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
//    		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//    		dialog.setAlwaysOnTop(true);
//    		dialog.addWindowFocusListener(new WindowFocusListener() {
//                public void windowGainedFocus(WindowEvent e) {}
//
//                public void windowLostFocus(WindowEvent e) {
//                    if (e.getOppositeWindow() == null || SwingUtilities.isDescendingFrom(e.getOppositeWindow(), dialog)) {
//                        return;
//                    }
//                    dialog.dispose();
//                    properties.setKey(false);
//                }
//            });
//    		dialog.setVisible(true);
    	}
    	if (properties.getStatus() && properties.getKey() && properties.getObject() != null) {
    		KrnObject object = properties.getObject();
    		if (clsUI != null && object.classId == clsUI.id) {
    			//Показ интерфейсов
    			openInterface(object);
    			
			} else if (clsProcessDef != null && object.classId == clsProcessDef.id) {
				//Показ процессов
				openProcess(object);			
			} else if (clsFilter != null && object.classId == clsFilter.id) { 
				//Показ фильтров
				openFilter(object);
         	} else if (clsUser != null && object.classId == clsUser.id) { 
				//Показ пользователей
				openUser(object);
         	} else if (clsBoxExchange != null && object.classId == clsBoxExchange.id) {
         		// Показ пункта обмена
         		openBoxExchange(object);
         	}
			else if (clsReportPrinter != null && object.classId == clsReportPrinter.id) {
				//Показ отчетов
				openReport(object);
			}
    		EditorPane.this.grabFocus();
    	}
        painter();
    }

    public void mouseEntered(MouseEvent e) {
        painter();
    }

    public void mouseExited(MouseEvent e) {
        painter();
    }

    public void mousePressed(MouseEvent e) {
        painter();
    }

    public void mouseReleased(MouseEvent e) {
        if (panel.isMarked()) {
            highlightDublicate();
        }
        painter();
    }

    public void insertString(int pos, String str, boolean replace) {
        if (replace) {
            int dot = getCaret().getDot();
            int mark = getCaret().getMark();
            if (dot != mark) {
                int selectS = getSelectionStart();
                replaceSelection(str);
                setSelection(selectS, selectS + str.length(), false);
            } else {
                try {
                    getDocument().insertString(getCaretPosition(), str, null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showGoLineDlg() {
        Document doc = getDocument();
        GoLineBox box = new GoLineBox();
        DesignerDialog dlg;
        if (getTopLevelAncestor() instanceof JFrame) {
            dlg = new DesignerDialog((Frame) panel.getTopLevelAncestor(), "Переход на строку", box);
        } else {
            dlg = new DesignerDialog((Dialog) panel.getTopLevelAncestor(), "Переход на строку", box);
        }

        dlg.show();
        int res = dlg.getResult();
        if (res != ButtonsFactory.BUTTON_NOACTION
                && res == ButtonsFactory.BUTTON_OK) {
            int line = box.getLineNum();
            Element rootElement = ((ExprDoc) doc).getDefaultRootElement();
            int startOffset = 0;
            if (rootElement.getElementCount() > line) {
                startOffset = rootElement.getElement(line - 1).getStartOffset();
            } else {
                int max = rootElement.getElementCount();
                startOffset = rootElement.getElement(max - 1).getStartOffset();
            }
            setCaretPosition(startOffset);
           }
    }

    public static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    public ArrayList<Pair<Integer, String>> getLastPos(String str,String first) {
        ArrayList<Pair<Integer, String>> poss=new ArrayList<Pair<Integer, String>>();
        int start=0;
        matcher = beginOperatorPattern.matcher(str);
        if("#foreach".equals(first) 
        		|| "#while".equals(first) 
        		|| "#if".equals(first)
        		|| "#try".equals(first)
        		|| "#function".equals(first)
        		) {
        	//поиск завершающего #end
            int count=0;
            int if_count=0;
            boolean par=true;
            while (matcher.find(start)) {
                start = matcher.end();
                if ("#end".equals(matcher.group())) {
                    if (count==1) {
                        poss.add(new Pair<Integer, String>(matcher.start(), matcher.group()));
                        break;
                    } else {
                        count--;
                        if (if_count>0) if_count--;
                    }
                } else if (!"#else".equals(matcher.group()) && !"#elseif".equals(matcher.group())
                		&& !"#catch".equals(matcher.group()) && !"#finally".equals(matcher.group())) {
                    count++;
                }
                if ("#if".equals(first)) {//поиск промежуточных #else
                    if(!par && ("#foreach".equals(matcher.group()) 
                    		|| "#while".equals(matcher.group()) 
                    		|| "#if".equals(matcher.group())
                    		|| "#try".equals(matcher.group())
                    		|| "#function".equals(matcher.group())
                    		)){
                        if_count++; 
                    }else if((if_count==0 || count==0) && ("#else".equals(matcher.group()) || "#elseif".equals(matcher.group()))) {
                        poss.add(new Pair<Integer, String>(matcher.start(), matcher.group()));
                    } 
                    if(par) par=false;
                }
                if ("#try".equals(first)) {//поиск промежуточных #catch #finally
                    if(!par && ("#foreach".equals(matcher.group()) 
                    		|| "#while".equals(matcher.group()) 
                    		|| "#if".equals(matcher.group())
                    		|| "#try".equals(matcher.group())
                    		|| "#function".equals(matcher.group())
                    		)){
                        if_count++; 
                    }else if((if_count==0 || count==0) && ("#catch".equals(matcher.group()) || "#finally".equals(matcher.group()))) {
                        poss.add(new Pair<Integer, String>(matcher.start(), matcher.group()));
                    } 
                    if(par) par=false;
                }
            }
        } else if("#end".equals(first) || "#else".equals(first)
        		 || "#catch".equals(first) || "#finally".equals(first)) {
        	//поиск начального оператора
            while(matcher.find(start)) {
                start = matcher.end();
                if ("#end".equals(matcher.group()) && str.length()>matcher.start()) {
                    if (poss.size() > 0) poss.remove(0);
                } else if(!"#else".equals(matcher.group()) && !"#elseif".equals(matcher.group())
                		&& !"#catch".equals(matcher.group()) && !"#finally".equals(matcher.group())) {
                    poss.add(0, new Pair<Integer, String>(matcher.start(), matcher.group()));
                }
            }
        }
        return poss;
    }
    
    public int getLastPos(String str, char p1) {
        int pos = 0;
        int bad = 0;
        char p2 = ' ';
        if (p1 == '(')
            p2 = ')';
        else if (p1 == ')') p2 = '(';
        if (p1 == '(') {
            for (int i = 1; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == p1)
                    bad++;
                else {
                    if (ch == p2) {
                        if (bad > 0) {
                            bad--;
                            continue;
                        } else
                            return i;
                    }
                }
            }
        } else {
            for (int i = str.length() - 1; i > 1; i--) {
                char ch = str.charAt(i);

                if (ch == p1)
                    bad++;
                else {
                    if (ch == p2) {
                        if (bad > 0) {
                            bad--;
                            continue;
                        } else
                            return i;
                    }
                }
            }
        }
        return pos;
    }

    public void setErrors(String str) {
        //panel.setErrors(str);
    }


    public void fillPopup() throws BadLocationException{
//    	System.out.println("fillPopup...");
    	Line line = getCurLine();
    	String lineBegin = line.getLineBegin();
//    	System.out.println("lineBegin = " + line.getLineBegin());
    	listModel.clear();
    	listModelContext.clear();
    	
    	listModel = new DefaultListModel();
    	listModelContext = new DefaultListModel();
    	
    	if (lineBegin.endsWith("$Systems.") || lineBegin.endsWith("$SysOr3.")) {
    		return;
    	}
    	if(fastInject(line.getLine())) {
    		return;
    	}
    	matcher = beginVarPattern.matcher(lineBegin); 
    	if(matcher.find()){//Переменная
    		autocompleteType = AUTOCOMPLETE_TYPE.VAR;
    		frag = matcher.group(1);
    		boolean showAll = frag == null || frag.equals("");	
	        for (Iterator<String> fnIt = vars.keySet().iterator(); fnIt.hasNext();) {
	            final String func = fnIt.next();
	            final String func_l = func.toLowerCase(Constants.OK);
	            if ((!"false".equals(func_l) && !"true".equals(func)) && (showAll || func_l.startsWith(frag.toLowerCase(Constants.OK)))){                
	                listModel.addElement("$" + func);	                
	            }
	        }
	        //список используемых переменных начинающихся с frag
            Document doc = getDocument();
            String docTxt = doc.getText(0,doc.getLength());
            matcher = varPattern.matcher(docTxt);
            String frag_="$"+frag;
            int pos=getCaretPosition()-frag_.length();
            while(matcher.find()) {
                String varStr = matcher.group();
                int pos_=matcher.start();
                if (pos!=pos_ && !"$".equals(frag_) && varStr.toLowerCase(Constants.OK).startsWith(frag_.toLowerCase(Constants.OK)) && !listModel.contains(varStr)){                
                    listModel.addElement(varStr);                       
                }
            }
	        
        }else{//Оператор
        	matcher = beginOperPattern.matcher(lineBegin);
        	if(matcher.find()){
        		autocompleteType = AUTOCOMPLETE_TYPE.OPERATOR;
	        	frag = matcher.group(1);
	        	boolean showAll = frag == null || frag.equals("");
		        for (Iterator<String> fnIt = funcs.keySet().iterator(); fnIt.hasNext();) {
		            final String func = fnIt.next();
		            final String func_l = func.toLowerCase(Constants.OK);	            
		            if (showAll || func_l.startsWith(frag.toLowerCase(Constants.OK))) {
		                listModel.addElement(func);
		            }
		        }
        	}else{//функции
        		matcher = beginFuncPattern.matcher(lineBegin);
        		if(matcher.find()){
        			autocompleteType = AUTOCOMPLETE_TYPE.FUNCTION;
        			frag = matcher.group(1);
        			boolean showAll = frag == null || frag.equals("");
        			for (Iterator<String> fnIt = funcs.keySet().iterator(); fnIt.hasNext();) {
    		            final String func = fnIt.next();
    		            final String func_l = func.toLowerCase(Constants.OK);	            
    		            if (!func_l.substring(0, 1).equals("#") && (showAll || func_l.startsWith(frag.toLowerCase(Constants.OK)))) {    		                
    		                listModel.addElement(func);
    		            }
    		            //поиск функций по контексту, вставляет предлагаемые функции в конец списка
    		            if (!func_l.substring(0, 1).equals("#") && func_l.contains(frag.toLowerCase(Constants.OK))) {    		                
    		                listModelContext.addElement(func);
    		            }
    		        }
        			//совмещаем два списка
        			for (int index = 0; index < listModelContext.getSize(); index++) {
        				final String func_context = listModelContext.getElementAt(index).toString();
        				if(!listModel.contains(func_context)) {
        					listModel.addElement(func_context);
        				}
        			}
        			
        			
        		}else{//Название класса
        			matcher = beginClassPattern.matcher(lineBegin);
        			boolean find = matcher.find();
        			autocompleteType = AUTOCOMPLETE_TYPE.CLASS;
        			if(!find){
        				matcher = beginClassPattern2.matcher(lineBegin);
        				find = matcher.find();
        				if(find){
        					autocompleteType = AUTOCOMPLETE_TYPE.CLASS2;
        				}
        			}
        			if(find){//дополнение названия класса      				
        				frag = matcher.group(1);
        				boolean showAll = frag == null || frag.equals("");
        				for(KrnClass cls : paths.values()){
        					final String row = cls.name;
        					final String row_l = row.toLowerCase(Constants.OK);
        					final String frag_l = frag.toLowerCase(Constants.OK);
        					if(showAll || row_l.indexOf(frag_l) != -1 ){
        						listModel.addElement(cls);
        					}
        				}
        					
        			}else{//Название атрибута
        				matcher = beginAttrPattern.matcher(lineBegin);
        				if(matcher.find()){
        					try {
	        					autocompleteType = AUTOCOMPLETE_TYPE.ATTRIBUTE;
	        					Kernel krn = Kernel.instance();
	        					String textClass = matcher.group(1);        					
	        					String textSubAttrs = matcher.group(2);
	        					frag = matcher.group(3);
	        					
	        					KrnClass cls = paths.get(textClass);
	        					KrnClass lastClass = cls;//последний класс в пути
	        					
	        					matcher = subAttrPattern.matcher(textSubAttrs);
	        					
								while(matcher.find()){
									String curPath = matcher.group(1);
									KrnAttribute readAttr = krn.getAttributeByName(lastClass, curPath);
									if(readAttr != null){
										lastClass = krn.getClass(readAttr.typeClassId);
									}else{
										lastClass = null;
										break;
									}
								}
								
								if(lastClass != null){//корректный путь
		        					Map<String, KrnAttribute> attrs;							
									attrs = new TreeMap<String,KrnAttribute>();				
									for(KrnAttribute attr : krn.getAttributesByClassId(lastClass)){
										attrs.put(attr.name, attr);
									}
		        					boolean showAll = frag == null || frag.equals("");
		        					for(KrnAttribute attr : attrs.values()){
		            					final String row = attr.name;
		            					final String row_l = row.toLowerCase(Constants.OK);
		            					final String frag_l = frag.toLowerCase(Constants.OK);
		            					if( (showAll || row_l.indexOf(frag_l) != -1) && attr.id !=1 && attr.id != 2){
		            						listModel.addElement(attr);
		            					}
		            				}
								}
							} catch (KrnException e) {
								e.printStackTrace();
							}
        				} else { // Название метода
        					matcher = beginMethodFromGetClassPattern.matcher(lineBegin);
							if (matcher.find()) {
								try {
									KrnClass cls = krn.getClassByName(matcher.group(1));
									if (cls == null) {
										// Окраска записей в красный цвет, если нет указанного класса
										((CellRenderList) list.getCellRenderer()).setClassExistance(true);
										frag = matcher.group(2);
										selectMethods(-1);
									} else {
										((CellRenderList) list.getCellRenderer()).setClassExistance(false);
										frag = matcher.group(2);
										selectMethods(cls.id);
									}
								} catch (KrnException e) {
									e.printStackTrace();
								}
							} else {
	        					matcher = beginMethodPattern.matcher(lineBegin);
	        					if (matcher.find()) {
									try {
										((CellRenderList) list.getCellRenderer()).setClassExistance(false);
										frag = matcher.group(1);
										selectMethods(-1);
									} catch (KrnException e) {
										e.printStackTrace();
									}
								}
							}
        				}
        			}        			
        		}
        	}
        }
    	list.setModel(listModel);
    }
    
    private void selectMethods(long clsId) throws KrnException {
		autocompleteType = AUTOCOMPLETE_TYPE.METHOD;
		boolean showAll = frag == null || frag.equals("");
		Map<String, KrnMethod> methods = new TreeMap<String, KrnMethod>();
		if (clsId < 0) {
			for (KrnMethod method : krn.getAllMethods()) {
				methods.put(method.name, method);
			}
		} else {
			for (KrnMethod method : krn.getMethods(clsId)) {
				methods.put(method.name, method);
			}
		}
		for (KrnMethod method : methods.values()) {
			String row = method.name;
			String row_l = row.toLowerCase(Constants.OK);
			String frag_l = frag.toLowerCase(Constants.OK);
			if (showAll || row_l.indexOf(frag_l) != -1) {
				listModel.addElement(method);
			}
		}
    }

    private class GoLineBox extends JPanel {
        JTextField gotf = Utils.createDesignerTextField();

        private GoLineBox() {
            setOpaque(isOpaque);
            JLabel golbl = Utils.createLabel("Номер строки");
            OrTextDocument doc = new OrTextDocument(gotf, Mode.RUNTIME);
            doc.setIncludeChars("09");
            doc.setExcludeChars("");
            gotf.setDocument(doc);
            setLayout(new GridBagLayout());
            add(golbl, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(30, 5, 30, 5), 0, 0));
            add(gotf, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(30, 0, 30, 5), 0, 0));
        }

        private int getLineNum() {
            String ln = gotf.getText().trim();
            if (ln != null && ln.length() > 0) {
                Integer line = new Integer(ln);
                return line.intValue();
            }
            return 0;
        }
    }

    public int[] getRCtoPos(int pos) {
        Rectangle caretCoords = null;
        int[] rc = new int[2];
        try {
            caretCoords = modelToView(pos);

            if (caretCoords != null) {
                rc[0] = (caretCoords.y + 1) / 17 + 1;
                rc[1] = (caretCoords.x - 11) / 7 + 1;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rc;
    }
    
    public void showFindDlg(int i) {
        String selected = "";
        int dot = getCaret().getDot();
        int mark = getCaret().getMark();
        if (dot != mark) {
            selected = getSelectedText();
        }
        FindDialog findDlg = new FindDialog(this, i, selected);
        findDlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(findDlg.getSize()));
        findDlg.setVisible(true);
    }
    
    //Считать названия классов
    private void readClassNames(){
    	try {
	    	Kernel krn = Kernel.instance();
	    	java.util.List<ClassNode> cnodes = new ArrayList<ClassNode>(); 
	    	krn.getClasses(cnodes);
			for(ClassNode cls : cnodes){
				paths.put(cls.getName(), cls.getKrnClass());
			}
			
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }
    
    //Загрузка сокращений
    private void loadMolds(){    	
    	molds.put("if", "#if(|)\n*\t\n*#end");
    	molds.put("ifel", "#if(|)\n*\t\n*#else\n*\t\n*#end");
    	molds.put("ifels", "#if(|)\n*\t\n*#else\n*\t\n*#end");
    	molds.put("ifelse", "#if(|)\n*\t\n*#else\n*\t\n*#end");
    	molds.put("for", "#foreach($| in $)\n*\t\n*#end");
    	molds.put("foreach", "#foreach($| in $)\n*\t\n*#end");
    	molds.put("while", "#while(|)\n*\t\n*#end");
    	molds.put("try", "#try\n*\t|\n*#catch\n*\t\n*#finally\n*\t\n*#end");
    	molds.put("func", "#function fname($|)\n*\t\n*#end");
    	//molds.put("do", "#do\n*\t\n*#while(|)");
    	molds.put("set","#set($| = )");
    	molds.put("e","#end");
    	molds.put("end","#end");
    	molds.put("el","#else");
    	molds.put("els","#else");
    	molds.put("else","#else");
    	molds.put("elif", "#elseif");
    	molds.put("elsif", "#elseif");
    	molds.put("elseif", "#elseif");
    	molds.put("br", "#break");
    	molds.put("bre", "#break");
    	molds.put("brea", "#break");
    	molds.put("break", "#break");
    	molds.put("con", "#continue");
    	molds.put("cont", "#continue");
    	molds.put("conti", "#continue");
    	molds.put("contin", "#continue");
    	molds.put("continu", "#continue");
    	molds.put("continue", "#continue");
    	molds.put("ret","#return ");
    	molds.put("retu","#return ");
    	molds.put("retur","#return ");
    	molds.put("return","#return ");
    	molds.put("pr","$Systems.println(\"|\")");
    	molds.put("var","$Systems.printVar($|)");
    	molds.put("prvar","$Systems.printVar($|)");    	
    	molds.put("msg","$Systems.showMessage(\"|\")");
    	molds.put("showmsg","$Systems.showMessage(\"|\")");
    	molds.put("showmessage","$Systems.showMessage(\"|\")");
    	molds.put("list","#set($| = $Objects.createList())");
    	molds.put("createlist","#set($| = $Objects.createList())");
    	molds.put("map","#set($| = $Objects.createMap())");
    	molds.put("createmap","#set($| = $Objects.createMap())");
    	molds.put("createset","#set($| = $Objects.createSet())");
    	molds.put("date","#set($| = $Date.getCurrDate())");
    	molds.put("time","#set($| = $Date.getCurrTime())");
    	molds.put("err","#set($ERRMSG = \"|\")");
    	molds.put("errmsg","#set($ERRMSG = \"|\")");
    	molds.put("selobj","#set($| = $SELOBJ)");
    	molds.put("interface","#set($| = $Interface)");    	
    	molds.put("user","#set($| = $USER)");
    	molds.put("base","#set($| = $BASE)");
    	molds.put("xml","#set($| = $XML)");
    	molds.put("server","#return $SERVER");
    	molds.put("withouttrid", "#set($WITHOUTTRID = \"1\")");
    	molds.put("commit", "$SYSTEM.commitLongTransaction(|)");
    	molds.put("rollback", "$SYSTEM.rollbackLongTransaction(|)");
    	molds.put("refreshobjects","$Objects.getClass(|).refreshObjects()");
    	molds.put("resavefilter","$SysOr3.resaveFilter()");
    	molds.put("resavetriggers","$SysOr3.resaveTriggers()");
    	molds.put("setfp","$Objects.setFilterParam($filter, $map)");
    	molds.put("getfp","#set($| = $Objects.getFilterParam($filterUID, \"%\"))");
    }
    
    //Заполнение модели таблицы акселераторов
    private KeyTableModel createKeyTableModel(){
    	java.util.List<KeyTableRow> rows = new ArrayList<KeyTableRow>();
    	if(molds != null){
    		for(Map.Entry<String, String> entry : molds.entrySet()){
    			String value = entry.getValue();    			
    			value = value.replaceAll("\\*", "");
    			value = value.replaceAll("\\|", "");
    			rows.add(new KeyTableRow(" " + entry.getKey()," " + value));
    		}
    	}
    	rows.add(new KeyTableRow(" {Ctrl + Q}", " Переход к месту последнего изменения"));
    	rows.add(new KeyTableRow(" {Ctrl + K}", " Показать окно с комбинациями клавиш"));
    	rows.add(new KeyTableRow(" {Ctrl + Пробел}", " Вызвать окно автодополнения / применить комбинацию"));
    	rows.add(new KeyTableRow(" {Ctrl + F}", " Поиск"));
    	rows.add(new KeyTableRow(" {Ctrl + R}", " Замена"));
    	rows.add(new KeyTableRow(" {Ctrl + G}", " Переход на строку"));
    	rows.add(new KeyTableRow(" {Ctrl + 9}", " Вставка пустых скобок ()"));
    	rows.add(new KeyTableRow(" {Alt + 9}", " Вставка скобок ()"));
    	rows.add(new KeyTableRow(" {Tab}", " Увеличение отступа кода"));
    	rows.add(new KeyTableRow(" {Shift + Tab}", " Уменьшение отступа кода"));
    	rows.add(new KeyTableRow(" {Ctrl + /}", " Закомментировать/раскомментировать строку(и)"));
    	rows.add(new KeyTableRow(" {Ctrl + D}", " Удалить строку(и)"));
    	return new KeyTableModel(rows);
    }
    
    //Создание таблицы комбинаций
    private JTable createKeyTable(){
    	loadMolds();
    	final JTable table = new JTable();    	
    	KeyTableModel keyTableModel = createKeyTableModel();
        TableRowSorter<KeyTableModel> keyTableSorter = new TableRowSorter<KeyTableModel>(keyTableModel); 
        table.setModel(keyTableModel);
        table.setRowSorter(keyTableSorter);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(0).setCellRenderer(new KeyTableRender());
        table.getColumnModel().getColumn(1).setCellRenderer(new KeyTableRender());
        table.setRowHeight(20);//высота строк
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);
        table.setShowVerticalLines(false);
        table.setFocusable(false);
        //настройка заголовка
        JTableHeader head = table.getTableHeader();
        head.setBackground(new Color(206,222,231));
        head.setFont(Utils.getAppTitleFont());
    	return table;
    }
    
    //Прорисовка элементов меню автодополнения
    private static class CellRenderList extends JLabel implements ListCellRenderer {
    	
    	private boolean isExistsClass = false;
    	
    	public void setClassExistance(boolean isExistsClass) {
    		this.isExistsClass = isExistsClass;
    	}
    	
		public Component getListCellRendererComponent(JList list,Object obj,int index,boolean selected,boolean b){			
			JLabel lbl = Utils.createLabel("Вид отображения не определен");
			lbl.setOpaque(true);
			lbl.setFont(Utils.getDefaultComponentFont());
			lbl.setForeground(Utils.getShadowsGreyColor());
			if(selected){//выделенный элемент				
				lbl.setBackground(Utils.getSilverColor());
			}else{//не выделенный элемент
				lbl.setBackground(POPUP_BACKGROUND_COLOR);
			}
			if(obj instanceof String){
				String html = "";
				String row = (String)obj;
				String startRow = "";
				String endRow = row;				
				if(row.length() > 0 && frag.length() > 0 && row.length() >= frag.length()){					
					if(autocompleteType == AUTOCOMPLETE_TYPE.VAR){
						startRow = row.substring(0,frag.length() + 1);
						endRow = row.substring(frag.length() + 1);
						html = "" + 
								"<html>" + 
								"<b>" + startRow + "</b>" + endRow +					
								"</html>";
						
					}else if(autocompleteType == AUTOCOMPLETE_TYPE.OPERATOR || autocompleteType == AUTOCOMPLETE_TYPE.FUNCTION){
						//дублируем строку названия функции и переводим её в нижний регистр
						String row_dup = row.toLowerCase();
						String frag_lower = frag.toLowerCase();
						//находим вхождение искомой подстроки и потом меняем её на жирную
						int start_pos = row_dup.indexOf(frag_lower);
						if(start_pos>=0) {
							String new_frag = row.substring(start_pos, start_pos+frag.length());
							//Заменить первую подстроку на жирную подстроку
							html = row.replaceFirst(new_frag,"<b>"+new_frag+"</b>");
							html = "<html>"+html+"</html>";
						}
					}
				}
				switch(autocompleteType){
				case FUNCTION:
				case OPERATOR: lbl.setForeground(Utils.getKeywordColor()); break;
				case VAR: lbl.setForeground(Utils.getVariableColor()); break;
				}
				
				lbl.setText(html);
				lbl.setIcon(null);
			}else if(obj instanceof KrnClass){
				KrnClass cls = (KrnClass)obj;
				String row = cls.name;
				if(row.length() > 0 && frag != null && frag.length() > 0){	
					Pattern p = Pattern.compile(frag,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);					
					Matcher m = p.matcher(row);
					row = m.replaceAll("<b>$0</b>");
				}
				String html = "" + 
				"<html>" + row + "</html>";
				lbl.setText(html);
				lbl.setIcon(ClassTreeIconLoader.getIcon(cls));
			}else if(obj instanceof KrnAttribute){
				try {
					KrnAttribute attr = (KrnAttribute)obj;
					String row = attr.name;
					if(row.length() > 0 && frag != null && frag.length() > 0){	
						Pattern p = Pattern.compile(frag,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);					
						Matcher m = p.matcher(row);
						row = m.replaceAll("<b>$0</b>");
					}
					Kernel krn = Kernel.instance();
					KrnClass cls = krn.getClass(attr.typeClassId);
					if(cls != null){
						row += "<font color=gray> : " + cls.name + "</font>";
					}
					String html = "" + 
					"<html>" + row + "</html>";
					lbl.setText(html);
					lbl.setIcon(AttributeTreeIconLoader.getIcon(attr));
				} catch (KrnException e) {
					e.printStackTrace();
				}
			}else if(obj instanceof KrnMethod){
				KrnMethod method = (KrnMethod)obj;
				String row = method.name;
				if(row.length() > 0 && frag != null && frag.length() > 0){	
					Pattern p = Pattern.compile(frag,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);					
					Matcher m = p.matcher(row);
					row = m.replaceAll("<b>$0</b>");
				}
				String html = "" + 
				"<html>" + row + "</html>";
				lbl.setText(html);
				if(method.isClassMethod){
					lbl.setIcon(statMethodIcon);
				}else{
					lbl.setIcon(methodIcon);
				}
				if (isExistsClass) {
					lbl.setForeground(Color.RED);
				}
			} else if (obj instanceof MethodItem) {
				String html = "<html>" + ((MethodItem) obj).getTitle() + "</html>";
				lbl.setText(html);
				lbl.setIcon(null);
			}
			return lbl;
		}
	}
    
    class MethodItem {
    	private KrnMethod method;
    	
    	public MethodItem(KrnMethod method) {
    		this.method = method;
    	}
    	
    	public String getTitle() {
    		StringBuilder title = new StringBuilder("Метод '" + method.name + "'");
			try {
				title.append(", класс '" +  kernel.getClass(method.classId).name + "'");
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
			return title.toString();
    	}
    	
    	public KrnMethod getMethod() {
    		return method;
    	}
    }

    public void highlightDublicate() {
        try {
            Document doc = getDocument();
            String txt = getSelectedText();
            if (txt == null) {
                int caretPos = getCaretPosition(); // Текущая позиция курсора
                int start = Utilities.getWordStart(this, caretPos); // Позиция начала слова
                int end = Utilities.getWordEnd(this, caretPos); // Позиция окончания слова
                int lengthW = end - start;
                txt = doc.getText(start, lengthW);
            }
          //  Pattern pattern = Pattern.compile("\\b" + txt + "\\b");
            Pattern pattern = Pattern.compile(txt);
            Matcher matcher = pattern.matcher(doc.getText(0, doc.getLength()));
            while (matcher.find()) {
                highlight(matcher.start(), txt, hilite);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
}

//типы автодополения
enum AUTOCOMPLETE_TYPE{
	VAR,
	OPERATOR,
	FUNCTION,
	CLASS,
	CLASS2,
	ATTRIBUTE,
	METHOD
	
}

//Информация о текущей строке
class Line{
	private final String line;
	private final int posInLine;
	Line(String line,int posInLine){
		this.line = line;
		this.posInLine = posInLine;
	}
	String getLine(){
		return line;
	}
	String getLineBegin(){
		return line.substring(0, posInLine); 
	}
	String getLineEnd(){
		return line.substring(posInLine);
	}
	int getPosInLine(){
		return posInLine;
	}
}

//Информация о блоке кода
class CodeBlock{
	private final EditorPane pane;
	private final int dot;
	private final int mark;
	private final int beginPos;
	private final int endPos;
	private String text;

	CodeBlock(EditorPane pane) throws BadLocationException{
		this.pane = pane;
		dot = pane.getCaret().getDot();//позиция курсора
		mark = pane.getCaret().getMark();//позиция начала выделения
        beginPos = Utilities.getRowStart(pane,Math.min(mark, dot));//начало выделения
        endPos = Utilities.getRowEnd(pane, Math.max(mark, dot));//конец выделения
        text = pane.getText(beginPos, endPos - beginPos);
	}
	
	public int getDot() {
		return dot;
	}
	
	public int getMark() {
		return mark;
	}
	
	public int getBeginPosition() {
		return beginPos;
	}
	
	public int getEndPosition() {
		return endPos;
	}
	
	public EditorPane getPane() {
		return pane;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	//Выделяем фрагмент старого кода
	void selectBlock(){
		pane.setSelectionStart(beginPos);
		pane.setSelectionEnd(endPos);
	}
	//Замена содержимого
	void replaceSelection(String newText){
		selectBlock();
		pane.replaceSelection(newText);
	}
	//Выделяем фрагмент нового кода
	void selectBlock(String newText){
		pane.setSelectionStart(beginPos);
		pane.setSelectionEnd(beginPos + newText.length());
	}
	
	boolean isEmptyBlock(){
		return beginPos == endPos;
	}
	
	boolean isCodeBlock(){
		return dot != mark;
	}
	//Передвинуть каретку относительно текущей позиции
	void moveCaret(int step){
		int newPos = dot + step; 
		if(newPos > 0){
			pane.setCaretPosition(newPos);
		}
	}
	//Удаление блока кода
	void delete() throws BadLocationException{
		pane.setSelectionStart(beginPos);
		pane.setSelectionEnd(endPos + 1);
		pane.replaceSelection("");
	}
	//Вставка tab
	void insertTab() throws BadLocationException{
		//вставка одного /t - не сработавает,
		//поэтому вставляется /t/t, последний из которых удаляется
		pane.getDocument().insertString(dot, "\t\t", null);
		pane.getDocument().remove(dot + 1, 1);
	}
	
}

class KeyTableRow{
	final String accelerator;
	final String desciption;
	KeyTableRow(String accelerator,String desciption){
		this.accelerator = accelerator;
		this.desciption = desciption;
	}
	String getAccelerator(){
		return this.accelerator;
	}
	String getDesciption(){
		return this.desciption;
	}
}

class KeyTableModel extends AbstractTableModel{
	final String[] colNames = {"Комбинация", "Описание"};
	final java.util.List<KeyTableRow> rows;
	KeyTableModel(java.util.List<KeyTableRow> rows){		
		this.rows = rows;
	}
	public int getColumnCount(){
		return colNames.length;    		
	}
	public int getRowCount(){
		return rows.size();
	}
	public Object getValueAt(int row, int col){
		KeyTableRow tableRow = rows.get(row);
		if(tableRow != null){
			switch(col){
			case 0: return tableRow.getAccelerator();
			case 1: return tableRow.getDesciption();
			}
		}
		return "not found";
	}
	public String getColumnName(int column){
		return colNames[column];
	}
}

class KeyTableRender extends JLabel implements TableCellRenderer{
	   public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
		   JLabel label = Utils.createLabel();
		   label.setFont(Utils.getDefaultComponentFont());
		   label.setOpaque(true);
		   
		   if(isSelected){
			   label.setBackground(new Color(222,189,99));			   
		   }else{
			   label.setBackground(row % 2 == 0 ? new Color(231,231,231) : new Color(206,222,231));
		   }
		   if(value != null){
			   String str = value.toString();
			   if(str.indexOf("{") != -1){
				   label.setText("<html><b>" + str + "</b></html>");
			   }else{
				   label.setText(str);
			   }
			   
			   label.setToolTipText(str);
		   }
		   return label;
	   }
}

class KeyTableHeaderRenderer extends JLabel implements ListCellRenderer{
	public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHashFocus){
		JLabel label = Utils.createLabel();
		label.setFont(Utils.getDefaultComponentFont());
		label.setOpaque(true);
		if(value != null){
			label.setText(value.toString());
			label.setToolTipText(value.toString());
		}
		
		return label;
	}
}

class Match {
	private int start;
	private int end;
	private String text;
	
	public Match(int start, int end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;
	}
	
	public int start() {
		return start;
	}
	
	public int end() {
		return end;
	}
	
	public String text() {
		return text;
	}
}

class LastPosition {
	public int start;
	public int end;
	public KrnMethod[] methods;
	public KrnObject object;
	private boolean isKeyPressed = false;
	private boolean status = false;
	private EditorPane editorPane;
	
	public LastPosition(EditorPane editorPane) {
		this.editorPane = editorPane;
	}
	
	public LastPosition(int start, int end, KrnMethod[] methods) {
		this.start = start;
		this.end = end;
		this.methods = methods;
	}
	
	public void init(int start, int end, KrnMethod[] methods) {
		this.start = start;
		this.end = end;
		this.methods = methods;
		this.object = null;
		status = true;
	}
	public void init(int start, int end, KrnObject object) {
		this.start = start;
		this.end = end;
		this.object = object;
		this.methods =null;
		status = true;
	}
	
	public int getLenght() {
		return end - start;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setKey(boolean isKeyPressed) {
		this.isKeyPressed = isKeyPressed;
		if (isKeyPressed && status) {
			editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}
	}
	
	public boolean getKey() {
		return isKeyPressed;
	}
	
	public KrnMethod[] getMethods() {
		return methods;
	}
	public KrnObject getObject() {
		return object;
	}
}