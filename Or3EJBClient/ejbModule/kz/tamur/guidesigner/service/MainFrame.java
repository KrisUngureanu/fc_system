package kz.tamur.guidesigner.service;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Utils.createDesignerToolBar;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NO;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NOACTION;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.guidesigner.ButtonsFactory.createCompButton;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.guidesigner.ComponentsTool;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ProcessFrameTemplate;
import kz.tamur.guidesigner.UIDPattern;
import kz.tamur.guidesigner.service.cmd.CmdCheck;
import kz.tamur.guidesigner.service.cmd.CmdCreateProcess;
import kz.tamur.guidesigner.service.cmd.CmdDeleteFromSrvModel;
import kz.tamur.guidesigner.service.cmd.CmdOpenProcess;
import kz.tamur.guidesigner.service.cmd.CmdPasteImpl;
import kz.tamur.guidesigner.service.cmd.CmdSaveProcess;
import kz.tamur.guidesigner.service.cmd.CmdViewHistory;
import kz.tamur.guidesigner.service.cmd.CmdViewItem;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.Utils;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.LangItem;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.OpenElementPanel;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tigris.gef.base.Cmd;
import org.tigris.gef.base.CmdAdjustGrid;
import org.tigris.gef.base.CmdAdjustGuide;
import org.tigris.gef.base.CmdAlign;
import org.tigris.gef.base.CmdCopy;
import org.tigris.gef.base.CmdDistribute;
import org.tigris.gef.base.CmdOpenWindow;
import org.tigris.gef.base.CmdPrint;
import org.tigris.gef.base.CmdPrintPageSetup;
import org.tigris.gef.base.CmdReorder;
import org.tigris.gef.base.CmdSavePGML;
import org.tigris.gef.base.CmdSaveSVG;
import org.tigris.gef.base.CmdSelectAll;
import org.tigris.gef.base.CmdSelectInvert;
import org.tigris.gef.base.CmdSelectNext;
import org.tigris.gef.base.CmdSetMode;
import org.tigris.gef.base.CmdZoom;
import org.tigris.gef.base.ModeBroom;
import org.tigris.gef.base.ModeCreateFigCircle;
import org.tigris.gef.base.ModeCreateFigInk;
import org.tigris.gef.base.ModeCreateFigLine;
import org.tigris.gef.base.ModeCreateFigPoly;
import org.tigris.gef.base.ModeCreateFigRRect;
import org.tigris.gef.base.ModeCreateFigRect;
import org.tigris.gef.base.ModeCreateFigSpline;
import org.tigris.gef.base.ModeCreateFigText;
import org.tigris.gef.base.ModeSelect;
import org.tigris.gef.event.GraphSelectionEvent;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 12:18:33
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JPanel implements ActionListener, ProcessFrameTemplate {
    // Commands
    private Cmd createCmd = new CmdCreateProcess("Create", this);
    private Cmd openCmd = new CmdOpenProcess("Open", this);
    private Cmd saveCmd = new CmdSaveProcess("Save", this, CmdSaveProcess.SAVE_ALL);
    private Cmd savePGMLCmd = new CmdSavePGML();
    private Cmd saveSVGCmd = new CmdSaveSVG();
    private Cmd printCmd = new CmdPrint();
    private Cmd printSetupCmd = new CmdPrintPageSetup((CmdPrint) printCmd);
    private Cmd prefCmd = new CmdOpenWindow("org.tigris.gef.base.PrefsEditor", "Preferences...");

    private OrGradientMenuBar menuBar = new OrGradientMenuBar();
    // private Component view;
    private ControlTabbedContent tabbedContent = ServiceControl.instance(this).getContentTabs();

    private JMenu fileMenu = new JMenu("Файл");
    private JMenuItem historyItem = createMenuItem("История", "history.png");
    private JMenuItem openItem = createMenuItem("Открыть");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem saveAsPGMLItem = createMenuItem("Сохранить как PGML...");
    private JMenuItem saveAsSVGItem = createMenuItem("Сохранить как SVG...");
    private JMenuItem printItem = createMenuItem("Печать");
    private JMenuItem printSetupItem = createMenuItem("Настройка печати...");
    private JMenuItem prefItem = createMenuItem("Установки...");
    private JMenuItem exitItem = createMenuItem("Закрыть");

    private JMenu viewMenu = new JMenu("Вид");
    private JMenuItem adjustGridItem = createMenuItem("Режим сетки");
    private JCheckBoxMenuItem adjustGridSnapItem = kz.tamur.comps.Utils.createCheckMenuItem("Привязать к сетке");
    private JMenuItem zoomInItem = createMenuItem("Приблизить (+50%)", "ZoomIn");
    private JMenuItem zoomOutItem = createMenuItem("Отдалить (-50%)", "ZoomOut");
    private JMenuItem zoomNormItem = createMenuItem("Восстановить (100%)");

    private JMenu editMenu = new JMenu("Редактирование");
    private JMenu selectMenu = new JMenu("Режим выбора");
    private JMenuItem selectAllItem = createMenuItem("Выделить всё");
    private JMenuItem selectPrevItem = createMenuItem("Предыдущий элемент");
    private JMenuItem selectNextItem = createMenuItem("Следующий элемент");
    private JMenuItem selectInvertItem = createMenuItem("Инвертировать выделение");
    private JMenuItem undoItem = createMenuItem("Отменить");
    private JMenuItem redoItem = createMenuItem("Повторить");
    private JMenuItem copyItem = createMenuItem("Копировать");
    private JMenuItem pasteItem = createMenuItem("Вставить");
    private JMenuItem checkItem = createMenuItem("Проверка процесса", "runDebug");
    private JMenuItem viewItem = createMenuItem("Просмотреть", "ServPrev");
    private JMenuItem searchItem = createMenuItem("Поиск узла", "Find");
    private JMenuItem deleteItem = createMenuItem("Удалить");

    private JMenu arrangeMenu = new JMenu("Упорядочить");
    private JMenu alignMenu = new JMenu("Выравнивание");
    private JMenuItem alignTopsItem = createMenuItem("По верхнему краю");
    private JMenuItem alignBottomsItem = createMenuItem("По нижнему краю");
    private JMenuItem alignLeftItem = createMenuItem("По левому краю");
    private JMenuItem alignRightItem = createMenuItem("По правому краю");
    private JMenuItem alignHCenterItem = createMenuItem("В центре по горизонтали");
    private JMenuItem alignVCenterItem = createMenuItem("В центре по вертикали");
    private JMenuItem alignGridItem = createMenuItem("По сетке");

    private JMenu distribute = new JMenu("Распределение");
    private JMenuItem distrHSpacingItem = createMenuItem("Горизонтальная разбивка", "DistrHSpace");
    private JMenuItem distrHCenterItem = createMenuItem("По горизонтали", "DistrHCenter");
    private JMenuItem distrVCenterItem = createMenuItem("По вертикали", "DistrVCenter");
    private JMenuItem distrVSpacingItem = createMenuItem("Вертикальная разбивка", "DistrVSpace");

    private JMenuItem reorderBackItem = createMenuItem("На задний план", "ReorderBack");
    private JMenuItem reorderFrontItem = createMenuItem("На передний план", "ReorderFront");
    private JMenuItem forwardItem = createMenuItem("Вперёд", "Forward");
    private JMenuItem backwardItem = createMenuItem("Назад", "Backward");

    private JToolBar fileToolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton historyBtn = createToolButton("history.png", "История");
    private JButton createBtn = createToolButton("Create", "Создать");
    private JButton openBtn = createToolButton("Open", "Открыть");
    private JButton saveBtn = createToolButton("Save", "Сохранить");
    private JButton printBtn = createToolButton("ReportPrinter", "Печать");
    private JButton backBtn = createToolButton("Back", "Выбрать предыдущий элемент");
    private JButton nextBtn = createToolButton("Next", "Выбрать следующий элемент");
    private JButton importProcessBtn = new JButton("Импорт");
    private JButton exportProcessBtn = new JButton("Экспорт");
    private JButton copyBtn = createToolButton("Copy", "Копировать");
    private JButton pasteBtn = createToolButton("Paste", "Вставить");
    private JButton deleteBtn = createToolButton("Trash", "Удалить");
    private JButton gridBtn = createToolButton("Grid", "Режим сетки");
    private JButton checkBtn = createToolButton("runDebug", "Проверка процесса");
    private JButton viewHistoryBtn = createToolButton("ServiceHistory.gif", "История изменения");
    private JToggleButton selectBtn = createCompButton(null, getImageIcon("Unselected"));
    private JToggleButton broomBtn = createCompButton(null, getImageIcon("Broom"));

    private JToolBar decorToolBar = createDesignerToolBar();
    private JToggleButton circleBtn = createCompButton(new CmdSetMode(ModeCreateFigCircle.class, ""), null, getImageIcon("Circle"));
    private JToggleButton rectBtn = createCompButton(new CmdSetMode(ModeCreateFigRect.class, ""), null, getImageIcon("Rect"));
    private JToggleButton rRectBtn = createCompButton(new CmdSetMode(ModeCreateFigRRect.class, ""), null, getImageIcon("RRect"));
    private JToggleButton lineBtn = createCompButton(new CmdSetMode(ModeCreateFigLine.class, ""), null, getImageIcon("Line1"));
    private JToggleButton polyBtn = createCompButton(new CmdSetMode(ModeCreateFigPoly.class, ""), null, getImageIcon("Polygon"));
    private JToggleButton textBtn = createCompButton(new CmdSetMode(ModeCreateFigText.class, ""), null, getImageIcon("Label"));
    private JToggleButton splineBtn = createCompButton(new CmdSetMode(ModeCreateFigSpline.class, ""), null, getImageIcon("Spline"));
    private JToggleButton inkBtn = createCompButton(new CmdSetMode(ModeCreateFigInk.class, ""), null, getImageIcon("Ink"));

    private ButtonGroup bg = new ButtonGroup();

    private LanguageCombo langSelector = new LanguageCombo();
    private JToolBar toolBarPanel = kz.tamur.comps.Utils.createDesignerToolBar();
    private JLabel statusTextLab = Utils.createLabel("");
    private JLabel selectedNode = Utils.createLabel("");
    private ServicesController serviceController = new ServicesController(this);
    private JToolBar toolBar = new NodePalette();
    private ServicePropertyEditor propEditor;
    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private ServicesTree tree = null;
    private AbstractDesignerTreeNode lastNode = null;
    private long langId = com.cifs.or2.client.Utils.getInterfaceLangId();

    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");

    private ComponentsTool tools = new ComponentsTool();

    private JComboBox lastServicesCombo = Utils.createCombo();
    private ArrayList<Date> keys;
    private SortedMap<Date, Map<Long, String>> items;
    private boolean canOpen = false;
    private final String notFoundClass = "Класс 'Action' не найден или не соответствует формату!";

    private boolean isOpaque = !kz.tamur.rt.MainFrame.TRANSPARENT_DIALOG;
    private JTextField uidView = new JTextField();
    private JLabel procOwner = kz.tamur.rt.Utils.createLabel("");
    
    public void setOwner(KrnObject obj) {
    	if(obj != null)
    		procOwner.setText("Владелец: " + (kz.tamur.comps.Utils.getObjOwner(obj)!= null? kz.tamur.comps.Utils.getObjOwner(obj): ""));
    }
    

    public MainFrame() throws HeadlessException {
        getToolkit().addAWTEventListener(serviceController, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setLayout(new BorderLayout());
        add(tabbedContent, BorderLayout.CENTER);
        add(toolBarPanel, BorderLayout.NORTH);
        initToolBars();
        initMenu();
        propEditor = new ServicePropertyEditor(this);
        propEditor.setPreferredSize(new Dimension(300, 200));
        add(propEditor, BorderLayout.WEST);
        setOpaque(isOpaque);
        buttonsEnabled(false);
        
        updateStatusBar();
        
        tabbedContent.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Document doc = getSelectedDocument();
                if (doc != null) {
                    GraphSelectionEvent evt = new GraphSelectionEvent(doc.getGraph().getEditor(), doc.getGraph().selectedFigs());
                    propEditor.selectionChanged(evt);
                }
                if (tabbedContent.isService() && !saveBtn.isEnabled()) {
                    buttonsEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.PROCESS_EDIT_RIGHT));
                }
            }
        });

        tabbedContent.fireChange();

        tabbedContent.addContainerListener(new ContainerAdapter() {
            public void componentRemoved(ContainerEvent e) {
                if (tabbedContent.isService() && tabbedContent.getTabCount() > 0) {
                    Document doc = getSelectedDocument();
                    GraphSelectionEvent evt = new GraphSelectionEvent(doc.getGraph().getEditor(), doc.getGraph().selectedFigs());
                    propEditor.selectionChanged(evt);
                }
            }
        });
        statusBar.addAnyComponent(new JLabel(getImageIcon("ServiceTab")));
        statusBar.addAnyComponent(statusTextLab);

        statusBar.addEmptySpace();
        statusBar.addSeparator();
        statusBar.addLabel("UID: ");
        statusBar.addTextField(uidView);
        statusBar.addSeparator();
        statusBar.addLabel(procOwner);
        statusBar.addSeparator();

        statusBar.addAnyComponent(selectedNode);
        statusBar.addEmptySpace();
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
        tree = kz.tamur.comps.Utils.getServicesTree();
        tree.setFrame(this);
        ServiceActionsConteiner.setMainFrame(this);
    }

    public OrGradientMenuBar getMenu() {
        return menuBar;
    }

    public int processExit() {
        Document[] docs = getModifiedDocuments();
        int res;
        if (docs.length > 0) {
            String mess = "Процессы :";
            for (int i = 0; i < docs.length; i++) {
                Document doc = docs[i];
                if (i != docs.length - 1) {
                    mess = mess + "'" + doc.getTitle() + "', \n";
                } else {
                    mess = mess + "'" + doc.getTitle() + "'";
                }
            }
            mess = mess + " модифицированы!\n Сохранить изменения?";
            res = MessagesFactory.showMessageDialog((JFrame) this.getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            switch (res) {
            case BUTTON_YES:
                saveCmd.doIt();
                return BUTTON_YES;
            case BUTTON_NO:
                return BUTTON_NO;
            default:
                res = BUTTON_CANCEL;
                break;
            }
        } else {
            res = BUTTON_NOACTION;
        }
        propEditor.processExit();
        return res;
    }

    private void initToolBars() {
        decorToolBar.setBorderPainted(false);
        fileToolBar.setBorderPainted(false);
        toolBar.setBorderPainted(false);

        // File toolbar
        fileToolBar.add(new JLabel(getImageIcon("decor")));
        fileToolBar.add(historyBtn);
        fileToolBar.add(createBtn);
        fileToolBar.add(openBtn);
  /*      fileToolBar.add(importProcessBtn); // Это зачем тут? проектировщикам мешается
        fileToolBar.add(exportProcessBtn);*/
        fileToolBar.add(saveBtn);
        fileToolBar.addSeparator();
        fileToolBar.add(printBtn);
        fileToolBar.addSeparator();
        fileToolBar.add(backBtn);
        fileToolBar.add(nextBtn);
        fileToolBar.add(copyBtn);
        fileToolBar.add(pasteBtn);
        fileToolBar.add(deleteBtn);
        fileToolBar.addSeparator();
        fileToolBar.add(gridBtn);
        fileToolBar.add(checkBtn);
        fileToolBar.add(viewHistoryBtn);
        fileToolBar.addSeparator();
        fileToolBar.add(selectBtn);
        fileToolBar.add(broomBtn);
        for (int i = 0; i < fileToolBar.getComponentCount(); i++) {
            Component c = fileToolBar.getComponentAtIndex(i);
            if (c != null && (c instanceof JButton || c instanceof JToggleButton)) {
                ((AbstractButton) c).addActionListener(this);
            }
        }

        decorToolBar.add(new JLabel(getImageIcon("decor")));
        circleBtn.setToolTipText("Окружность");
        decorToolBar.add(circleBtn);
        rectBtn.setToolTipText("Прямоугольник");
        decorToolBar.add(rectBtn);
        rRectBtn.setToolTipText("Прямоугольник с закруглёнными углами");
        decorToolBar.add(rRectBtn);
        lineBtn.setToolTipText("Линия");
        decorToolBar.add(lineBtn);
        textBtn.setToolTipText("Подпись");
        decorToolBar.add(textBtn);
        polyBtn.setToolTipText("Многоугольник");
        decorToolBar.add(polyBtn);
        splineBtn.setToolTipText("Плавная кривая");
        decorToolBar.add(splineBtn);
        inkBtn.setToolTipText("Кривая");
        decorToolBar.add(inkBtn);

        toolBarPanel.add(fileToolBar, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Constants.INSETS_0, 0, 0));
        bg.add(selectBtn);
        selectBtn.setToolTipText("Отменить выделение");
        bg.add(broomBtn);
        broomBtn.setToolTipText("Скребок");
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component c = toolBar.getComponent(i);
            if (c instanceof AbstractButton) {
                bg.add((AbstractButton) c);
            }
        }
        for (int i = 0; i < decorToolBar.getComponentCount(); i++) {
            Component c = decorToolBar.getComponent(i);
            if (c instanceof AbstractButton) {
                bg.add((AbstractButton) c);
            }
        }
        tools.addToolBar("Инструменты", toolBar);
        tools.addToolBar("Декоративные", decorToolBar);
        toolBarPanel.add(tools, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Constants.INSETS_0, 0, 0));
        Dimension dimension = new Dimension(330, 25);
        lastServicesCombo.setPreferredSize(dimension);
        lastServicesCombo.setMaximumSize(dimension);
        lastServicesCombo.setMinimumSize(new Dimension(50, 25));
        lastServicesCombo.addItem(notFoundClass);
        lastServicesCombo.setToolTipText("Измененные процессы");
        toolBarPanel.add(lastServicesCombo);
        loadServices();
        lastServicesCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (String.valueOf(lastServicesCombo.getSelectedItem()).equals(notFoundClass)) {
                    return;
                }
                try {
                    if (canOpen) {
                        KrnObject oo = Kernel.instance().getObjectById(new ArrayList<Long>(items.get(keys.get(lastServicesCombo.getSelectedIndex())).keySet()).get(0), 0);
                        ((CmdOpenProcess) openCmd).doIt(oo, String.valueOf(lastServicesCombo.getSelectedItem()), null);
                    }

                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        });
        toolBarPanel.setOpaque(isOpaque);
        fileToolBar.setOpaque(isOpaque);
        tools.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        decorToolBar.setOpaque(isOpaque);
    }

    public void loadServices() {
        if (ServiceActionsConteiner.getServicesMode()) {
            items = ServiceActionsConteiner.getLastServices();
            keys = new ArrayList<Date>(items.keySet());
            canOpen = false;
            lastServicesCombo.removeAllItems();
            for (int i = 0; i < items.size() && i < 10; i++) {
                lastServicesCombo.addItem(new ArrayList<String>(items.get(keys.get(i)).values()).get(0));
            }
            canOpen = true;
        }
    }

    public void setDocument(Document doc, KrnObject nodeObj) {
        if (doc != null) {
            doc.getGraph().addGraphSelectionListener(propEditor);
            tabbedContent.addServiceTab(doc,nodeObj);
            HistoryWithDate hwd = new HistoryWithDate(doc.getKrnObject(), new Date());
            Kernel.instance().getUser().addSrvInHistory(hwd,doc.getTitle());
        }
        validate();
    }

    public Document getSelectedDocument() {
        return tabbedContent.getSelectedDocument();
    }

    public Document getDocument(int idx) {
        return tabbedContent.getDocument(idx);
    }

    public Document[] getModifiedDocuments() {
        ArrayList<Document> list = new ArrayList<Document>();
        for (int i = 0; i < tabbedContent.getTabCount(); i++) {
            if (tabbedContent.isTabModified(i) && tabbedContent.isService(i)) {
                list.add(tabbedContent.getDocument(i));
            }
        }
        return list.toArray(new Document[list.size()]);
    }

    public void placeDividers() {
        propEditor.setDeviderLocation(0.5);
        validate();
    }

    private void initMenu() {
        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK);
        KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK);
        KeyStroke altF4 = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK);

        KeyStroke leftArrow, rightArrow, upArrow, downArrow;
        leftArrow = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        rightArrow = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        upArrow = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        downArrow = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);

        KeyStroke sLeftArrow, sRightArrow, sUpArrow, sDownArrow;
        sLeftArrow = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK);
        sRightArrow = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK);
        sUpArrow = KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK);
        sDownArrow = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK);

        KeyStroke delKey, ctrlZ, ctrlY, ctrlX, ctrlC, ctrlV, ctrlG, ctrlU, ctrlB, ctrlF, sCtrlB, sCtrlF;
        delKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
        ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK);
        ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
        ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
        ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
        ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK);
        ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK);
        sCtrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
        sCtrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
        KeyStroke f3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
        //
        menuBar.setOpaque(false);
        menuBar.add(new JLabel(getImageIcon("menuFlag")));
        fileMenu.setFont(Utils.getDefaultFont());
        fileMenu.setForeground(Utils.getDarkShadowSysColor());
        fileMenu.setMnemonic('Ф');
        fileMenu.addSeparator();
        fileMenu.add(historyItem);
        fileMenu.add(openItem);
        openItem.setAccelerator(ctrlO);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        saveItem.setAccelerator(ctrlS);
        fileMenu.add(saveAsPGMLItem);
        fileMenu.add(saveAsSVGItem);
        fileMenu.addSeparator();
        fileMenu.add(printItem);
        printItem.setAccelerator(ctrlP);
        fileMenu.add(printSetupItem);
        fileMenu.addSeparator();
        fileMenu.add(prefItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        exitItem.setAccelerator(altF4);
        for (int i = 0; i < fileMenu.getItemCount(); i++) {
            JMenuItem mi = fileMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        menuBar.add(fileMenu);

        viewMenu.setFont(Utils.getDefaultFont());
        viewMenu.setForeground(Utils.getDarkShadowSysColor());
        viewMenu.setMnemonic('В');
        viewMenu.add(adjustGridItem);
        viewMenu.add(adjustGridSnapItem);
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(zoomNormItem);
        for (int i = 0; i < viewMenu.getItemCount(); i++) {
            JMenuItem mi = viewMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        menuBar.add(viewMenu);

        editMenu.setFont(Utils.getDefaultFont());
        editMenu.setForeground(Utils.getDarkShadowSysColor());
        editMenu.setMnemonic('Р');
        selectMenu.setFont(Utils.getDefaultFont());
        selectMenu.setForeground(Utils.getDarkShadowSysColor());
        selectMenu.setMnemonic('Р');
        selectMenu.add(selectAllItem);
        selectMenu.add(selectPrevItem);
        selectMenu.add(selectNextItem);
        selectMenu.add(selectInvertItem);
        for (int i = 0; i < selectMenu.getItemCount(); i++) {
            JMenuItem mi = selectMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        editMenu.add(selectMenu);
        editMenu.add(undoItem);
        undoItem.setEnabled(false);
        undoItem.setAccelerator(ctrlZ);
        editMenu.add(redoItem);
        redoItem.setEnabled(false);
        redoItem.setAccelerator(ctrlY);
        editMenu.add(copyItem);
        copyItem.setAccelerator(ctrlC);
        editMenu.add(pasteItem);
        pasteItem.setAccelerator(ctrlV);
        editMenu.add(checkItem);
        editMenu.add(viewItem);
        editMenu.add(searchItem);
        searchItem.setAccelerator(f3);
        editMenu.addSeparator();
        editMenu.add(deleteItem);
        deleteItem.setAccelerator(delKey);
        for (int i = 0; i < editMenu.getItemCount(); i++) {
            JMenuItem mi = editMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        menuBar.add(editMenu);

        arrangeMenu.setFont(Utils.getDefaultFont());
        // arrangeMenu.setForeground(Utils.getDarkShadowSysColor());
        arrangeMenu.setMnemonic('У');
        alignMenu.setFont(Utils.getDefaultFont());
        alignMenu.setForeground(Utils.getDarkShadowSysColor());
        alignMenu.setMnemonic('В');
        alignMenu.add(alignTopsItem);
        alignMenu.add(alignBottomsItem);
        alignMenu.add(alignLeftItem);
        alignMenu.add(alignRightItem);
        alignMenu.add(alignHCenterItem);
        alignMenu.add(alignVCenterItem);
        alignMenu.add(alignGridItem);
        for (int i = 0; i < alignMenu.getItemCount(); i++) {
            JMenuItem mi = alignMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        arrangeMenu.add(alignMenu);

        distribute.setFont(Utils.getDefaultFont());
        distribute.setForeground(Utils.getDarkShadowSysColor());
        distribute.setMnemonic('с');
        distribute.add(distrHSpacingItem);
        distribute.add(distrHCenterItem);
        distribute.add(distrVSpacingItem);
        distribute.add(distrVCenterItem);
        arrangeMenu.add(distribute);
        for (int i = 0; i < distribute.getItemCount(); i++) {
            JMenuItem mi = distribute.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        reorderBackItem.setAccelerator(sCtrlB);
        reorderFrontItem.setAccelerator(sCtrlF);
        backwardItem.setAccelerator(ctrlB);
        forwardItem.setAccelerator(ctrlF);
        arrangeMenu.add(reorderBackItem);
        arrangeMenu.add(reorderFrontItem);
        arrangeMenu.add(backwardItem);
        arrangeMenu.add(forwardItem);
        for (int i = 0; i < arrangeMenu.getItemCount(); i++) {
            JMenuItem mi = arrangeMenu.getItem(i);
            if (mi != null) {
                mi.addActionListener(this);
            }
        }
        menuBar.add(arrangeMenu);
    }

    public void open() {
        openCmd.doIt();
    }

    public void load(KrnObject process, KrnObject objectNode) {
        String title = "";
        try {
            String[] pr = Kernel.instance().getStrings(process, "title", 0l, 0);
            if (pr != null && pr.length > 0) {
                title = pr[0];
                open(process, title,objectNode);
            } else {
                System.out.println("Процесс не существует!");
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(KrnObject process, String title, KrnObject nodeObj) {
        ((CmdOpenProcess) openCmd).doIt(process, title,nodeObj);
        // сохранение в историю
        HistoryWithDate hwd = new HistoryWithDate(process, new Date());
        Kernel.instance().getUser().addSrvInHistory(hwd,title);
        
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Cmd command = null;
        LangItem li = (LangItem) langSelector.getSelectedItem();
        if (src == historyItem || src == historyBtn) {
            showHistory((JComponent)historyBtn);
        } else if (src == openItem || src == openBtn) {
            command = openCmd;
        } else if (src == saveItem || src == saveBtn) {
            command = saveCmd;
        } else if (src == createBtn) {
            command = createCmd;
        } else if (src == saveAsPGMLItem) {
            command = savePGMLCmd;
        } else if (src == saveAsSVGItem) {
            command = saveSVGCmd;
        } else if (src == printItem || src == printBtn) {
            command = printCmd;
        } else if (src == printSetupItem) {
            command = printSetupCmd;
        } else if (src == prefItem) {
            command = prefCmd;
        } else if (src == exitItem) {
            if (processExit() != BUTTON_CANCEL) {
                Or3Frame frm = (Or3Frame) getTopLevelAncestor();
                frm.closeCurrent();
            }
        } else if (src == adjustGridItem || src == gridBtn) {
            command = new CmdAdjustGrid();
        } else if (src == adjustGridSnapItem) {
            command = new CmdAdjustGuide();
        } else if (src == zoomInItem) {
            command = new CmdZoom(1.5);
        } else if (src == zoomOutItem) {
            command = new CmdZoom(0.5);
        } else if (src == zoomNormItem) {
            command = new CmdZoom(0.0);
        } else if (src == selectAllItem) {
            command = new CmdSelectAll();
        } else if (src == selectPrevItem || src == backBtn) {
            command = new CmdSelectNext(false);
        } else if (src == selectNextItem || src == nextBtn) {
            command = new CmdSelectNext(true);
        } else if (src == selectInvertItem) {
            command = new CmdSelectInvert();
        } else if (src == copyItem || src == copyBtn) {
            command = new CmdCopy();
        } else if (src == undoItem) {
            CmdUndoAction.Undo(this);
        } else if (src == redoItem) {
            CmdRedoAction.Redo(this);
        } else if (src == checkItem || src == checkBtn) {
            command = new CmdCheck(this);
        } else if (src == viewHistoryBtn) {
            command = new CmdViewHistory("История изменения",this);
        } else if (src == pasteItem || src == pasteBtn) {
            command = new CmdPasteImpl(this);
        } else if (src == viewItem /* || src == viewBtn */) {
            command = new CmdViewItem("", this);
        } else if (src == searchItem) {
            searchItem();
        } else if (src == deleteItem || src == deleteBtn) {
            command = new CmdDeleteFromSrvModel(this);
        } else if (src == alignTopsItem) {
            command = new CmdAlign(CmdAlign.ALIGN_TOPS);
        } else if (src == alignBottomsItem) {
            command = new CmdAlign(CmdAlign.ALIGN_BOTTOMS);
        } else if (src == alignLeftItem) {
            command = new CmdAlign(CmdAlign.ALIGN_LEFTS);
        } else if (src == alignRightItem) {
            command = new CmdAlign(CmdAlign.ALIGN_RIGHTS);
        } else if (src == alignHCenterItem) {
            command = new CmdAlign(CmdAlign.ALIGN_H_CENTERS);
        } else if (src == alignVCenterItem) {
            command = new CmdAlign(CmdAlign.ALIGN_V_CENTERS);
        } else if (src == alignGridItem) {
            command = new CmdAlign(CmdAlign.ALIGN_TO_GRID);
        } else if (src == distrHSpacingItem) {
            command = new CmdDistribute(CmdDistribute.H_SPACING);
        } else if (src == distrHCenterItem) {
            command = new CmdDistribute(CmdDistribute.H_CENTERS);
        } else if (src == distrVSpacingItem) {
            command = new CmdDistribute(CmdDistribute.V_SPACING);
        } else if (src == distrVCenterItem) {
            command = new CmdDistribute(CmdDistribute.V_CENTERS);
        } else if (src == reorderBackItem) {
            command = new CmdReorder(CmdReorder.SEND_TO_BACK);
        } else if (src == reorderFrontItem) {
            command = new CmdReorder(CmdReorder.BRING_TO_FRONT);
        } else if (src == backwardItem) {
            command = new CmdReorder(CmdReorder.SEND_BACKWARD);
        } else if (src == forwardItem) {
            command = new CmdReorder(CmdReorder.BRING_FORWARD);
        } else if (src == selectBtn) {
            command = new CmdSetMode(ModeSelect.class, "Select");
        } else if (src == broomBtn) {
            command = new CmdSetMode(ModeBroom.class, "Broom");
        } else if (src == langSelector) {
            if (((LanguageCombo) src).isSelfChange()) {
                setInterfaceLanguage();
                TreeUIDMap.clearMap(AbstractDesignerTreeNode.SERVICE_NODE);
                tree.setLangId(li.obj.id);
                propEditor.setLang();
            }
        }
        else if(src==importProcessBtn){
            importProcess();
        }
        else if(src==exportProcessBtn) {
            exportProcess();
        }
        if (command != null) {
            command.doIt();
        }

    }
    
    private void searchItem() {
    	  SearchItemPanel sip = new SearchItemPanel();
          DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Поиск узла", sip);
          dlg.setResizable(false);
          dlg.setCancelVisible(false);
          dlg.show();
          if (dlg.isOK()) {
        	  String searchingText = sip.getSearchText();
              
        	  ServiceModel model = tabbedContent.getSelectedDocument().getModel();
        	  for (Entry<String, StateNode> entry: model.nodeMap.entrySet()) {
        		  if (searchingText.equals(entry.getKey())) {
        			  tabbedContent.getSelectedDocument().getGraph().selectByOwner(entry.getValue());
        			  return;
        		  }
        	  }
        	  List<TransitionEdge> edges = model.getEdges();
        	  for (TransitionEdge edge: edges) {
        		  if (searchingText.equals(edge.getId())) {
        			  tabbedContent.getSelectedDocument().getGraph().selectByOwner(edge);
        			  return;
        		  }
        	  }
              MessagesFactory.showMessageDialog((JFrame) this.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Узел с указынным идентификатором не найден!");
          }
    }
    
    private Element getXml(KrnObject obj, String attribute) {
        Element xml = null;
        Kernel krn = Kernel.instance();
        byte[] data = null;
        try {
            data = krn.getBlob(obj, attribute, 0, 0, 0);
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
    private void exportProcess() {
        DesignerTree tree = this.getServicesTree();
        tree.setShowPopupEnabled(true);
        OpenElementPanel osp = new OpenElementPanel(tree);
        DesignerDialog dlg = new DesignerDialog((JFrame)this.getTopLevelAncestor(), "Выбор процесса", osp);
        osp.setSearchUIDPanel(true);
        tree.requestFocusInWindow();
        osp.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        if (osp.getTree().getSelectedNode()==null ||!osp.getTree().getSelectedNode().isLeaf()) {
            dlg.setOkEnabled(false);
        } else {
            dlg.setOkEnabled(true);
        }
        
       
        dlg.show();
        Kernel krn = Kernel.instance();
        if (dlg.isOK()) {
          DesignerTreeNode[] serviceNodes =osp.getTree().getSelectedNodes();
          String path = "C:\\Users\\Администратор\\Desktop\\filterXmls\\";
          String titles = "";
          for(int i = 0;i<serviceNodes.length;i++) {
              ServiceNode serviceNode =(ServiceNode) serviceNodes[i];
              String title = serviceNode.getTitle();
              //String title = ((AbstractDesignerTreeNode)serviceNodes[i]).toString();
              String filename = title.replaceAll("[^a-zA-Zа-яА-Я0-9,.\\s-]", "_");
              String xmlFilePath =path+filename+".xml";
              FileWriter file = null;
              try {
                  file = new FileWriter(xmlFilePath,true);
              } catch (IOException e1) {
                  e1.printStackTrace();
              }
            if (serviceNodes[i] == null || !((AbstractDesignerTreeNode)serviceNodes[i]).isLeaf())
                    return;
this.setLastNode((AbstractDesignerTreeNode)serviceNodes[i]);
KrnObject serviceObj = serviceNodes[i].getKrnObj();
if (serviceObj == null) {
return;
}               
                Element processDef = new Element("processDef");
                org.jdom.Document doc = new  org.jdom.Document(processDef);

                Element xml = getXml(serviceObj,"diagram");
                xml.detach();
                Element config = getXml(serviceObj, "config");
                config.detach();
                doc.getRootElement().addContent(xml);
                doc.getRootElement().addContent(config);
                SAXBuilder builder = new SAXBuilder();
                builder.setValidation(false);
                ServiceModel model = new ServiceModel(true, serviceObj, langId);
                model.setMf(this);
                KrnClass cls;
                try {
                    cls = krn.getClassByName("ProcessDef");
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
                List ls = LangItem.getAll();
                long[] langs = new long[ls.size()];
                for (int ii=0; ii< ls.size(); ii++) {
                    langs[ii] = ((LangItem)ls.get(ii)).obj.id;
                }
         
                for (long lang1 : langs) {
                    Map<String,String> str = new HashMap<String,String>();
                
                    byte[] msg = null;
                    byte[] strings = null;
                    try {
                        msg = krn.getBlob(serviceObj, "message", 0, lang1, 0);
                        strings = krn.getBlob(serviceObj, "strings", 0, lang1, 0);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    if (msg.length > 0) {
                        Element msgXml = null;
                        try {
                            msgXml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
                        } catch (JDOMException e) {
                            e.printStackTrace();  
                        } catch (IOException e) {
                            e.printStackTrace(); 
                        }
                        msgXml.detach();
                        msgXml.setAttribute("lang",Long.toString(lang1));
                        processDef.addContent(msgXml);   
                        }
                if(strings.length>0)
                {
                    Element stringsXml = null;
                    try {
                        stringsXml = builder.build(new ByteArrayInputStream(strings)).getRootElement();
                    } catch (JDOMException e) {
                        e.printStackTrace();  
                    } catch (IOException e) {
                        e.printStackTrace(); 
                    }
                    stringsXml.detach();
                    stringsXml.setAttribute("lang",Long.toString(lang1));
                    processDef.addContent(stringsXml);  
                }
                
                
                }
                XMLOutputter out = new XMLOutputter();
                out.getFormat().setEncoding("UTF-8");
                try {
                    out.output(doc, file);
                    file.close();
                    titles=titles.concat("-"+title+"\n");
                } catch (IOException e) {
                    e.printStackTrace(); 
                }}
        
        String msg = "";
        if(serviceNodes.length>1) {
        msg = "Фильтры:"+"\n"+titles+"успешно экспортированы ";
        }
        else if(serviceNodes.length==1) {
            msg =  "Фильтр"+"\n"+titles+"успешно экспортирован ";
        }
        else {
            msg = "Ничего не было экспортировано";
        }
        MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                msg + "в "+"\n"+path);
        }
    }

    JFileChooser fc;
    SAXBuilder builder;
    
	private void importProcess() {
		Element processDef = null;
		File file[] = null;
		builder = new SAXBuilder();
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(MainFrame.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFiles();
			String titles = "";

			for (int i = 0; i < file.length; i++) {
				String title = "";
				title = file[i].getName();
				titles += "-" + title + "\n";
				try {
					org.jdom.Document document = (org.jdom.Document) builder.build(file[i]);
					processDef = document.getRootElement();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// create new node in servicetree
				final ServicesTree.ServiceTreeModel treeModel = (ServicesTree.ServiceTreeModel) tree.getModel();
				String servName = title;
				ServiceNode node = null;
				try {
					node = (ServiceNode) treeModel.createChildNode(servName, false);
				} catch (KrnException e) {
					e.printStackTrace();
				}
				ServiceModel model = new ServiceModel(true, node.getKrnObj(), this.getInterfaceLanguage().id);
				model.setMf(this);

				Document doc = new Document(node.getKrnObj(), servName, model);

				Kernel krn = Kernel.instance();
				KrnClass cls = null;
				try {
					cls = krn.getClassByName("ProcessDef");
				} catch (KrnException e) {
					e.printStackTrace();
				}
				ByteArrayOutputStream os = null;
				XMLOutputter out = new XMLOutputter();
				out.getFormat().setEncoding("UTF-8");

				// setBlob - diagram
				Element diagram = processDef.getChild("diagram");
				try {
					os = new ByteArrayOutputStream();
					out.output(diagram, os);
					krn.setBlob(doc.getKrnObject().id, cls.id, "diagram", 0, os.toByteArray(), 0, 0);
					os.close();

				} catch (IOException e) {
					e.printStackTrace();
				} catch (KrnException e) {
					e.printStackTrace();
				}

				// setBlob - message(rus and kaz)

				List<Element> messages = processDef.getChildren("message");
				for (Element message : messages) {
					os = new ByteArrayOutputStream();
					long langCode = Long.parseLong(message.getAttributeValue("lang"));
					try {
						out.output(message, os);
						krn.setBlob(doc.getKrnObject().id, cls.id, "message", 0, os.toByteArray(), langCode, 0);
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (KrnException e) {
						e.printStackTrace();
					}
				}
				// setBlob - strings(rus and kaz)

				List<Element> strings = processDef.getChildren("strings");
				for (Element string : strings) {
					os = new ByteArrayOutputStream();
					long langCode = Long.parseLong(string.getAttributeValue("lang"));
					try {
						out.output(string, os);
						krn.setBlob(doc.getKrnObject().id, cls.id, "strings", 0, os.toByteArray(), langCode, 0);
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (KrnException e) {
						e.printStackTrace();
					}
				}

				// setBlob - config
				Element config = processDef.getChild("process-definition");
				try {
					os = new ByteArrayOutputStream();
					out.output(config, os);
					krn.setBlob(doc.getKrnObject().id, cls.id, "config", 0, os.toByteArray(), 0, 0);
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (KrnException e) {
					e.printStackTrace();
				}
			}
			String msg = "";
			if (file.length > 1) {
				msg = "Фильтры:" + "\n" + titles + "успешно импортированы";
			} else if (file.length == 1) {
				msg = "Фильтр" + "\n" + titles + "успешно импортированы";
			} else {
				msg = "Ничего не было импортировано";
			}
			MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, msg);
		}
	}
        
    public void buttonsEnabled(boolean isEnabled) {
        saveBtn.setEnabled(isEnabled);
        saveItem.setEnabled(isEnabled);
        saveAsPGMLItem.setEnabled(isEnabled);
        saveAsSVGItem.setEnabled(isEnabled);
        nextBtn.setEnabled(isEnabled);
        backBtn.setEnabled(isEnabled);
        selectMenu.setEnabled(isEnabled);
        alignMenu.setEnabled(isEnabled);
        distribute.setEnabled(isEnabled);
        backwardItem.setEnabled(isEnabled);
        forwardItem.setEnabled(isEnabled);
        reorderBackItem.setEnabled(isEnabled);
        reorderFrontItem.setEnabled(isEnabled);
        copyBtn.setEnabled(isEnabled);
        copyItem.setEnabled(isEnabled);
        printBtn.setEnabled(isEnabled);
        printItem.setEnabled(isEnabled);
        pasteBtn.setEnabled(isEnabled);
        pasteItem.setEnabled(isEnabled);
        deleteBtn.setEnabled(isEnabled);
        deleteItem.setEnabled(isEnabled);
        viewItem.setEnabled(isEnabled);
        searchItem.setEnabled(isEnabled);
        gridBtn.setEnabled(isEnabled);
        viewHistoryBtn.setEnabled(isEnabled);
        checkBtn.setEnabled(isEnabled);
        adjustGridItem.setEnabled(isEnabled);
        adjustGridSnapItem.setEnabled(isEnabled);
        zoomInItem.setEnabled(isEnabled);
        zoomOutItem.setEnabled(isEnabled);
        zoomNormItem.setEnabled(isEnabled);
        prefItem.setEnabled(isEnabled);
        printSetupItem.setEnabled(isEnabled);
    }

    public boolean isProcessOpened(Long id) {
        return tabbedContent.isExistSrv(id);
    }

    public void renameProcess(KrnObject source, String newTitle, boolean isOpened) {
        ServiceNode root = tree.getRoot();
        ServiceNode sourceNode = (ServiceNode) root.find(source).getLastPathComponent();
        if (isOpened) {
            tabbedContent.renameTab(newTitle);
        } else {
            if (tabbedContent.isExistSrv(source.id)) {
                tabbedContent.renameTab(newTitle);
            }
        }
        sourceNode.rename(newTitle);
        tree.renameProcess(sourceNode);
    }

    public void removeCurrentTab(Long id) {
        if (tabbedContent.isExistSrv(id)) {
            tabbedContent.removeCurrentTab();
            tabbedContent.fireChange();
        }
    }

    public void removeTab(KrnObject obj) {
        ServicesTree.ServiceTreeModel model = (ServicesTree.ServiceTreeModel) tree.getModel();
        ServiceNode root = tree.getRoot();
        ServiceNode sourceNode = (ServiceNode) root.find(obj).getLastPathComponent();
        try {
            model.deleteNode(sourceNode, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        removeCurrentTab(obj.id);
        ServiceActionsConteiner.removeFromConteiner(obj.id);
    }

    public void setProcessModified(boolean isModified) {
        tabbedContent.setProcessModified(isModified);
    }
    
    public void setProcessModified(Document d, boolean isModified) {
        tabbedContent.setProcessModified(d, isModified);
    }

    public void setSelectionMode() {
        selectBtn.setSelected(true);
    }
    
    public void reloadTree() {
    	tree = kz.tamur.comps.Utils.getServicesTree(true);
        tree.setFrame(this);
    }

    public ServicesTree getServicesTree() {
        return tree;
    }

    public void createCopyPropcess(KrnObject source, KrnObject target, String title) {
        final ServicesTree.ServiceTreeModel model = (ServicesTree.ServiceTreeModel) tree.getModel();
        ServiceNode root = tree.getRoot();
        ServiceNode sourceNode = (ServiceNode) root.find(source).getLastPathComponent();
        ServiceNode parentNode = (ServiceNode) sourceNode.getParent();
        try {
            model.addNode(new ServiceNode(target, title, langId, parentNode.getChildCount(), title, title, sourceNode.getRuntimeIndex(),
                    sourceNode.isTab(), sourceNode.getTabName(), sourceNode.getTabNameKz(), sourceNode.getHotKey(),
                    sourceNode.isBtnToolBar(), sourceNode.getIcon()), parentNode, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    public AbstractDesignerTreeNode getLastNode() {
        return lastNode;
    }

    public void setLastNode(AbstractDesignerTreeNode lastNode) {
        this.lastNode = lastNode;
    }

    public void setInterfaceLanguage() {
        LangItem li = (LangItem) langSelector.getSelectedItem();
        if (li != null) {
            for (int i = 0; i < tabbedContent.getComponentCount(); i++) {
                if (tabbedContent.isService(i)) {
                    Document doc = tabbedContent.getDocument(i);
                    doc.getModel().setLang(li.obj.id, doc.getGraph());
                }
            }
            langId = li.obj.id;
        }
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

    public void setLangId() {
        propEditor.setLang();
    }

    public void applyRights(User user) {
        boolean res = user.hasRight(Or3RightsNode.PROCESS_VIEW_RIGHT);
        openItem.setEnabled(res);
        openBtn.setEnabled(res);

        res = user.hasRight(Or3RightsNode.PROCESS_CREATE_RIGHT);
        createBtn.setEnabled(res);
    }

    public void searchDiagramComponent(String title) {
        for (Object o : getSelectedDocument().getModel().getNodes()) {
            if (((StateNode) o).getId().equals(title)) {
                getSelectedDocument().getGraph().select(((StateNode) o).getPresentation());
                return;
            }
        }
        for (Object o : this.getSelectedDocument().getModel().getEdges()) {
            if (((TransitionEdge) o).getId().equals(title)) {
                getSelectedDocument().getGraph().select(((TransitionEdge) o).getPresentation());
                return;
            }
        }
    }

    /**
     * @return the redoItem
     */
    public JMenuItem getRedoItem() {
        return redoItem;
    }

    /**
     * @return the undoItem
     */
    public JMenuItem getUndoItem() {
        return undoItem;
    }

    /**
     * @return the tabbedContent
     */
    public ControlTabbedContent getTabbedContent() {
        return tabbedContent;
    }

    /**
     * @return the selectedNode
     */
    public JLabel getSelectedNode() {
        return selectedNode;
    }

    /**
     * @return the statusTextLab
     */
    public JLabel getStatusTextLab() {
        return statusTextLab;
    }

    public void showHistory(JComponent swObj) {
        JPopupMenu pm = new JPopupMenu();
        Iterator it = Kernel.instance().getUser().config.getSrvHistoryObjs().iterator();
        JMenuItem item;
        while (it.hasNext()) {
            final KrnObject objTmp = (KrnObject) it.next();
            pm.add(item = createMenuItem("["+objTmp.id+"]-"+Kernel.instance().getUser().config.getSrvName(objTmp)));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    load(objTmp,null);
                }
            });
        }
        pm.show(swObj, swObj.getX(), swObj.getY());
    }
    /**
     * Получить toolBarPanel.
     * 
     * @return the toolBarPanel
     */
    public JToolBar getToolBarPanel() {;
    return toolBarPanel;
    }

    /**
     * Получить toolBar.
     * 
     * @return the propEditor
     */
    public ServicePropertyEditor getPropEditor() {
        return propEditor;
    }
    /**
     * Пересобрать интерфейс.
     */
    public void rebuildPanels() {
        tabbedContent.setServiceControlMode(false);
        if (toolBarPanel != null) {
            remove(toolBarPanel);
            add(toolBarPanel, BorderLayout.NORTH);
            toolBarPanel.setVisible(true);
        }
        if (propEditor != null) {
            remove(propEditor);
            add(propEditor, BorderLayout.WEST);
        }
        remove(tabbedContent);
        add(tabbedContent, BorderLayout.CENTER);
        revalidate();
    }

    public void showUUID(String uid) {
        uidView.setText(uid);
    }

    @Override
    public void addEdge(TransitionEdge edge, KrnObject obj) {
        ServiceActionsConteiner.getServiceActions(obj.id).addEdge((TransitionEdge) edge);
    }

    @Override
    public void removeEdge(TransitionEdge edge, KrnObject obj) {
        ServiceActionsConteiner.getServiceActions(obj.id).removeEdge((TransitionEdge) edge);
    }

    @Override
    public void addNode(StateNode node, KrnObject obj) {
        ServiceActionsConteiner.getServiceActions(obj.id).addNode((StateNode)node);
    }

    @Override
    public void removeNode(StateNode node, KrnObject obj) {
        ServiceActionsConteiner.getServiceActions(obj.id).removeNode((StateNode) node);
    }

    @Override
    public void changeNodeLocation(KrnObject obj, StateNode owner,
            Rectangle boundsOld, Rectangle boundsNew) {
        ServiceActionsConteiner.getServiceActions(obj.id).changeNodeLocation(owner, boundsOld, boundsNew);
    }

    @Override
    public boolean getUndoRedoCall(KrnObject obj) {
        return ServiceActionsConteiner.getServiceActions(obj.id).getUndoRedoCall();
    }

    /* (non-Javadoc)
     * @see kz.tamur.guidesigner.ProcessFrameTemplate#findServiceNode(com.cifs.or2.kernel.KrnObject)
     */
    @Override
    public ServiceNode findServiceNode(KrnObject obj) {
        ServicesTree tree = Or3Frame.instance().getServiceFrame().getServicesTree();
        return (ServiceNode) tree.searchByUID(obj.getUID());
    }
    
    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText(Or3Frame.getCurrentDbName());
        currentUserLable.setText(Or3Frame.getCurrentDbName());
    }
    
    class SearchItemPanel extends JPanel {

	    private JLabel label = Utils.createLabel("Введите идентификатор узла:");
	    private JTextField textField = Utils.createDesignerTextField();

		public SearchItemPanel() {
	        super(new GridBagLayout());
	        setOpaque(false);
	        Utils.setAllSize(this, new Dimension(200, 50));
	        add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, HORIZONTAL, Constants.INSETS_5, 0, 0));
	        add(textField, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
	    }

	    public String getSearchText() {
	        return textField.getText().trim();
	    }

	    public void setSearchText(String text) {
	        textField.setText(text);
	    }
    }
}