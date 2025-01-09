package kz.tamur.guidesigner.serviceControl;

import static java.awt.event.KeyEvent.VK_H;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NO;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.guidesigner.InterfaceActionsConteiner.getInterfaceActions;
import static kz.tamur.guidesigner.service.ServiceActionsConteiner.getServiceActions;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Factories;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrAccordion;
import kz.tamur.comps.OrCollapsiblePanel;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrPopUpPanel;
import kz.tamur.comps.OrScrollPane;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.ColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.ComponentsTreeModel;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.guidesigner.EmptyComponent;
import kz.tamur.guidesigner.InterfaceActionsConteiner;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.InterfaceTree.InterfaceTreeModel;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.filters.FilterItem;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.filters.OrFilterNode;
import kz.tamur.guidesigner.filters.OrFilterTree;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.service.Document;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.ObjectHistory;
import kz.tamur.guidesigner.service.ServiceActionsConteiner;
import kz.tamur.guidesigner.service.cmd.CmdCopyProcess;
import kz.tamur.guidesigner.service.cmd.CmdDeleteProcess;
import kz.tamur.guidesigner.service.cmd.CmdRenameProcess;
import kz.tamur.guidesigner.service.cmd.CmdSaveProcess;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.warnings.PathWarningsList;
import kz.tamur.guidesigner.warnings.PathWarningsListItem;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.inspector.PropertyTable;
import kz.tamur.or3.client.util.AllMouseEventProcessor;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.ServiceControlNode;

import org.tigris.gef.base.Cmd;
import org.w3c.dom.Node;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Панель вкладок для работы редактора с интерфейсами, процессами, фильтрами и отчётами.
 * 
 * @author Sergey Lebedev
 */
public class ControlTabbedContent extends OrBasicTabbedPane implements ActionListener, PropertyListener, PropertyChangeListener,
        OrFrame {

    /** Карта содержит номер вкладки и объект, содержащийся в ней. */
    private Map<Integer, KrnObject> objTabs = new HashMap<Integer, KrnObject>();

    /** Карта содержит номер вкладки и её заголовок. */
    private Map<Integer, String> titlesTabs = new HashMap<Integer, String>();

    /** Карта содержит номер вкладки и узел проектного дерева из которого она была открыта. */
    private Map<Integer, KrnObject> nodesTabs = new HashMap<Integer, KrnObject>();

    /** Карта содержит id процесса и документ, привязанный к нему. */
    private Map<Long, Document> docs = new TreeMap<Long, Document>();

    /** Карта содержит номер вкладки и id процесса, содержащегося в ней. */
    private Map<Integer, Long> docIds = new HashMap<Integer, Long>();

    /** Карта содержит id интерфейса и фрейм, привязанный к нему. */
    private Map<Long, InterfaceFrame> interfaces = new TreeMap<Long, InterfaceFrame>();

    /** Карта содержит номер вкладки и id интерфейса, содержащегося в ней. */
    private Map<Integer, Long> interfaceObjIds = new HashMap<Integer, Long>();

    /** Карта содержит id фильтра и запись, привязанная к нему. */
    private Map<Long, FilterRecord> filters = new TreeMap<Long, FilterRecord>();

    /** Карта содержит номер вкладки и id фильтра, содержащегося в ней. */
    private Map<Integer, Long> filterIds = new HashMap<Integer, Long>();

    /** Дерево фильтров. */
    private FiltersTree tree;

    /** Панель фильтров. */
    private FiltersPanel filterPanel;

    /** Фрейм процессов. */
    private MainFrame serviceFrame;

    private KrnObject lastSelectSrv = null;
    private KrnObject lastSelectIfc = null;
    private KrnObject lastSelectFlt = null;
    private KrnObject lastSelectRpt = null;

    /* Иконки вкладок. */
    private static ImageIcon icoSrv = getImageIconFull("serviceNode.png");
    private static ImageIcon icoSrvRO = getImageIconFull("serviceNodeRO.png");
    private static ImageIcon icoSrvMod = getImageIconFull("serviceNodeMod.png");
    private static ImageIcon icoIfr = getImageIconFull("interfaceNode.png");
    private static ImageIcon icoIfrRO = getImageIconFull("interfaceNodeRO.png");
    private static ImageIcon icoIfrMod = getImageIconFull("interfaceNodeMod.png");
    private static ImageIcon icoFlt = getImageIconFull("filterNode.png");
    private static ImageIcon icoFltRO = getImageIconFull("filterNodeRO.png");
    private static ImageIcon icoFltMod = getImageIconFull("filterNodeMod.png");
    private static ImageIcon icoRpt = getImageIconFull("reportNode.png");
    private static ImageIcon icoRptRO = getImageIconFull("reportNodeRO.png");
    private static ImageIcon icoRptMod = getImageIconFull("reportNodeMod.png");
    /* Контекстное меню вкладок. */
    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miSaveSrv = createMenuItem("Сохранить процесс", "serviceNode-save.png");
    private JMenuItem miCloseSrv = createMenuItem("Закрыть процесс", "serviceNode-close.png");
    private JMenuItem miCopySrv = createMenuItem("Создать копию процесса", "serviceNode-copy.png");
    private JMenuItem miRenameSrv = createMenuItem("Переименовать процесс", "serviceNode-ren.png");
    private JMenuItem miDeleteSrv = createMenuItem("Удалить процесс", "rubbish.png");
    private JMenuItem miHistorySrv = createMenuItem("Показать историю изменения процесса", "serviceNode-info.png");
    private JMenuItem miSaveIfr = createMenuItem("Сохранить интерфейс", "interfaceNode-save.png");
    private JMenuItem miCloseIfr = createMenuItem("Закрыть интерфейс", "interfaceNode-close.png");
    private JMenuItem miCopyIfr = createMenuItem("Создать копию интерфейса", "interfaceNode-copy.png");
    private JMenuItem miRenameIfr = createMenuItem("Переименовать интерфейс", "interfaceNode-ren.png");
    private JMenuItem miDeleteIfr = createMenuItem("Удалить интерфейс", "rubbish.png");
    private JMenuItem miHistoryIfr = createMenuItem("Показать историю изменения интерфейса", "interfaceNode-info.png");
    private JMenuItem miSaveFlt = createMenuItem("Сохранить фильтр", "filterNode-save.png");
    private JMenuItem miCloseFlt = createMenuItem("Закрыть фильтр", "filterNode-close.png");
    private JMenuItem miCopyFlt = createMenuItem("Создать копию фильтра", "filterNode-copy.png");
    private JMenuItem miRenameFlt = createMenuItem("Переименовать фильтр", "filterNode-ren.png");
    private JMenuItem miDeleteFlt = createMenuItem("Удалить фильтр", "rubbish.png");
    private JMenuItem miSaveRpt = createMenuItem("Сохранить отчёт", "reportNode-save.png");
    private JMenuItem miCloseRpt = createMenuItem("Закрыть отчёт", "reportNode-close.png");
    private JMenuItem miCopyRpt = createMenuItem("Создать копию отчёта", "serviceNode-copy.png");
    private JMenuItem miRenameRpt = createMenuItem("Переименовать отчёт", "serviceNode-ren.png");
    private JMenuItem miDeleteRpt = createMenuItem("Удалить отчёт", "rubbish.png");

    /* Команды работы с процессами. */
    private Cmd saveCmd;
    private Cmd renameCmd;
    private Cmd copyCmd;
    private Cmd deleteCmd;

    /** Флаг работы компонента в режиме управления проектами (общий редактор). */
    private boolean serviceControlMode = false;
    private int lastSelectedTab = -1;

    /** Копируемый узел дерева фильтра. */
    // TODO неактуально в новом функционале - позже переделать
    private OrFilterNode copyNode;
    private static ControlTabbedContent contentTabs;

    /**
     * Создание нового экземпляра класса.
     * 
     * @param serviceFrame
     *            the main frame
     */
    protected ControlTabbedContent(final MainFrame mainFrame) {
        super();

        final User user = Kernel.instance().getUser();
        this.serviceFrame = mainFrame;
        // конструирование меню
        hideItemMenu();
        KeyStroke ctrlH = KeyStroke.getKeyStroke(VK_H, KeyEvent.CTRL_MASK);
        miHistorySrv.setAccelerator(ctrlH);
        miHistoryIfr.setAccelerator(ctrlH);
        // общее меню
        pm.add(miSaveSrv);
        pm.add(miSaveIfr);
        pm.add(miSaveFlt);
        pm.add(miSaveRpt);
        pm.add(miCloseSrv);
        pm.add(miCloseIfr);
        pm.add(miCloseFlt);
        pm.add(miCloseRpt);
        pm.add(miCopySrv);
        pm.add(miCopyIfr);
        pm.add(miCopyFlt);
        pm.add(miCopyRpt);
        pm.add(miRenameSrv);
        pm.add(miRenameIfr);
        pm.add(miRenameFlt);
        pm.add(miRenameRpt);
        pm.add(miDeleteSrv);
        pm.add(miDeleteIfr);
        pm.add(miDeleteFlt);
        pm.add(miDeleteRpt);
        pm.add(miHistorySrv);
        pm.add(miHistoryIfr);

        // слушатели для редакторов процессов и интерфейсов
        addChangeListener(new ChangeListener() {
            void select() {
                ServiceControlNode selNode = (ServiceControlNode)kz.tamur.comps.Utils.getServicesControlTree().getSelectedNode();
                // убрать ложные срабатывания во время выбора объекта в дереве, когда сам объект открывается из дерева
                if (selNode != null) {
                    KrnObject selObj = selNode.getValue();
                    if (selObj != null && selObj.equals((objTabs.get(getSelectedIndex())))) {
                     // запомнить последную открытую вкладку
                        if (serviceControlMode) {
                            lastSelectedTab = getSelectedIndex();
                        }
                        keepObj(lastSelectedTab, selObj,selNode.getKrnObj());
                        return;
                    }
                }
                kz.tamur.comps.Utils.getServicesControlTree().setSelectedNode2(nodesTabs.get(getSelectedIndex()));
             // запомнить последную открытую вкладку
                if (serviceControlMode) {
                    lastSelectedTab = getSelectedIndex();
                }
            };

            public void stateChanged(ChangeEvent e) {
                long selID;
                long idTab = getObjIdTab();
                if (idTab == Kernel.SC_PROCESS_DEF.id) {
                    if (getSelectedDocument() != null) {
                        selID = getSelectedDocument().getKrnObject().id;
                        if (ServiceActionsConteiner.isContein(selID)) {
                            getServiceActions(selID).setCanClean(false);
                            getServiceActions(selID).setUndoRedoActivity(mainFrame);
                            getServiceActions(selID).setCanClean(true);
                        } else {
                            ServiceActionsConteiner.resetUndoRedoActivity();
                        }
                    }
                    if (serviceControlMode) {
                        ServiceControl.instance().showService(null);
                        select();
                    }
                    lastSelectSrv = objTabs.get(getSelectedIndex());
                    serviceFrame.showUUID(lastSelectSrv == null ? "" : lastSelectSrv.uid);
                    serviceFrame.setOwner(lastSelectSrv);
                    Or3Frame.instance().setStatusBar(serviceFrame.getStatusBar());
                } else if (idTab == Kernel.SC_UI.id) {
                    selID = getKrnObjectIfr().id;
                    if (getSelectedIndex() != -1 && InterfaceActionsConteiner.isContein(selID)) {
                        getInterfaceActions(selID).setCanClean(false);
                        getInterfaceActions(selID).setUndoRedoActivity();
                        getInterfaceActions(selID).setCanClean(true);
                    } else {
                        InterfaceActionsConteiner.resetUndoRedoActivity();
                    }
                    if (serviceControlMode) {
                        ServiceControl.instance().showInterface(null);
                        select();
                    }
                    lastSelectIfc = objTabs.get(getSelectedIndex());
                    DesignerFrame.instance().showUUID(lastSelectIfc == null ? "" : lastSelectIfc.uid);
                    DesignerFrame.instance().setOwner(lastSelectIfc);
                    Or3Frame.instance().setStatusBar(DesignerFrame.instance().getStatusBar());
                } else if (idTab == Kernel.SC_FILTER.id) { // this one
                    if (serviceControlMode) {
                        ServiceControl.instance().showFilter(null);
                        select();
                    }
                    lastSelectFlt = objTabs.get(getSelectedIndex());
                    filterPanel.showUUID(lastSelectFlt == null ? "" : lastSelectFlt.uid);
                    filterPanel.setOwner(lastSelectFlt);
                    filterPanel.setFilterObject(lastSelectFlt);
                    filterPanel.clearRightComponents();
                    Or3Frame.instance().setStatusBar(filterPanel.getStatusBar());
                } else if (idTab == Kernel.SC_REPORT_PRINTER.id) {
                    if (serviceControlMode) {
                        ServiceControl.instance().showReport(null);
                        select();
                    }
                    lastSelectRpt = objTabs.get(getSelectedIndex());
                    Or3Frame.instance().setStatusBar(Or3Frame.instance().getReportFrame().getStatusBar());
                }

                // запомнить последную открытую вкладку
                if (serviceControlMode && lastSelectedTab != getSelectedIndex()) {
                    lastSelectedTab = getSelectedIndex();
                    if (nodesTabs.get(getSelectedIndex()) != null) {
                        kz.tamur.comps.Utils.getServicesControlTree().setSelectedNode2(nodesTabs.get(getSelectedIndex()));
                    } else if (objTabs.get(getSelectedIndex()) != null) {
                        kz.tamur.comps.Utils.getServicesControlTree().setSelectedNode(objTabs.get(getSelectedIndex()));
                    }
                }
            }
        });

        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                if (isService()) {
                    if (keyCode == VK_H && event.isControlDown()) {
                        new ObjectHistory(getSelectedDocument().getKrnObject().id, "Процесс");
                    }
                } else if (isInterface()) {
                    if (keyCode == VK_H && event.isControlDown()) {
                        new ObjectHistory(getKrnObjectIfr().id, "Интерфейс");
                    }

                }
            }

            public void keyReleased(KeyEvent event) {
            }

            public void keyTyped(KeyEvent event) {
            }
        });

        addMouseListener(new AllMouseEventProcessor() {
            public void process(MouseEvent e) {
                if (e.isPopupTrigger() && getSelectedIndex() > -1) {
                    hideItemMenu();
                    if (isService() && isEnabledAt(getSelectedIndex())) {
                        Document doc = getSelectedDocument();
                        if (doc != null) {
                            boolean readOnly = doc.isReadOnly();
                            miSaveSrv.setVisible(true);
                            miCloseSrv.setVisible(true);
                            miCopySrv.setVisible(true);
                            miRenameSrv.setVisible(true);
                            miDeleteSrv.setVisible(true);
                            miHistorySrv.setVisible(true);
                            miSaveSrv.setEnabled(readOnly ? false : isTabModified(getSelectedIndex()));
                            miCloseSrv.setEnabled(true);
                            miCopySrv.setEnabled(user.hasRight(Or3RightsNode.PROCESS_CREATE_RIGHT));
                            miRenameSrv.setEnabled(!readOnly);
                            miDeleteSrv.setEnabled(!readOnly && user.hasRight(Or3RightsNode.PROCESS_DELETE_RIGHT));
                        }
                    } else if (isInterface() && isEnabledAt(getSelectedIndex())) {
                    	InterfaceFrame frm = getSelectedFrame();
                    	if (frm != null) {
                            boolean readOnly = frm.isReadOnly();
	                    	miSaveIfr.setVisible(true);
	                        miCloseIfr.setVisible(true);
	                        miCopyIfr.setVisible(true);
	                        miRenameIfr.setVisible(true);
	                        miDeleteIfr.setVisible(true);
	                        miHistoryIfr.setVisible(true);
                            miSaveIfr.setEnabled(readOnly ? false : isTabModified(getSelectedIndex()));
                            miCloseIfr.setEnabled(true);
                            miCopyIfr.setEnabled(user.hasRight(Or3RightsNode.INTERFACE_CREATE_RIGHT));
                            miRenameIfr.setEnabled(!readOnly);
                            miDeleteIfr.setEnabled(!readOnly && user.hasRight(Or3RightsNode.INTERFACE_DELETE_RIGHT));
                        }
                    } else if (isFilter() && filterIds != null && filterIds.size() > 0 && isEnabledAt(getSelectedIndex())) {
                        FilterRecord f = getSelectedFilterRecord();
                        if (f != null) {
                            boolean readOnly = f.isReadOnly();
	                    	miSaveFlt.setVisible(true);
	                        miCloseFlt.setVisible(true);
	                        miCopyFlt.setVisible(true);
	                        miRenameFlt.setVisible(true);
	                        miDeleteFlt.setVisible(true);
	                        
                            miSaveFlt.setEnabled(readOnly ? false : (isTabModified(getSelectedIndex()) && user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT)));
	                        miCloseFlt.setEnabled(true);
                            miDeleteFlt.setEnabled(!readOnly && user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT));
                        }
                    } else if (isReport() && isEnabledAt(getSelectedIndex())) {
                        miSaveRpt.setVisible(true);
                        miCloseRpt.setVisible(true);
                        miCopyRpt.setVisible(true);
                        miRenameRpt.setVisible(true);
                        miDeleteRpt.setVisible(true);
                        miSaveRpt.setEnabled(true);
                        miCloseRpt.setEnabled(true);
                    }
                    pm.show(ControlTabbedContent.this, e.getX(), e.getY());
                }
            }
        });
        initCommands();

        // общий блок
        for (int i = 0; i < pm.getComponentCount(); i++) {
            Component c = pm.getComponent(i);
            if (c instanceof JMenuItem) {
                ((JMenuItem) c).addActionListener(this);
            }
        }
        Factories.instance().addPropertyChangeListener(this);
        setFont(Utils.getDefaultFont());
    }

    public static ControlTabbedContent instance(MainFrame mainFrame) {
        if (contentTabs == null) {
            contentTabs = new ControlTabbedContent(mainFrame);
        }
        return contentTabs;
    }

    public static ControlTabbedContent instance() {
        return contentTabs;
    }

    /**
     * Запомнить объект.
     * 
     * @param index
     *            номер вкладки.
     * @param obj
     *            объект
     */
    public void keepObj(int index, KrnObject obj, KrnObject nodeObj) {
        objTabs.put(index, obj);
        nodesTabs.put(index, nodeObj);
    }
    
    public void removeFltTabByObj(KrnObject obj) {
    	int idx = -1;
    	for(Entry<Integer, KrnObject> entry: objTabs.entrySet()) {
    		if(entry.getValue().uid.equals(obj.uid)) {
    			idx = entry.getKey();
    		}
    	}
    	if(idx > -1) {
//    		removeObj(idx);
    		try {
				setSelectedIndex(idx);
				removeSelectedFlt();
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    /**
     * Удалить объект.
     * 
     * @param index
     *            номер вкладки.
     */
    public void removeObj(int index) {
        // objTabs.remove(index);
        updateAllMaps(index);
    }

    /**
     * На выбранной вкладке отчёт?
     * 
     * @return true, если отчёт
     */
    public boolean isReport() {
        return getObjIdTab() == Kernel.SC_REPORT_PRINTER.id;
    }

    /**
     * На выбранной вкладке процесс?
     * 
     * @return true, если процесс
     */
    public boolean isService() {
        return getObjIdTab() == Kernel.SC_PROCESS_DEF.id;
    }

    /**
     * На выбранной вкладке интерфейс?
     * 
     * @return true, если интерфейс
     */
    public boolean isInterface() {
        return getObjIdTab() == Kernel.SC_UI.id;
    }

    /**
     * На выбранной вкладке фильтр?
     * 
     * @return true, если фильтр
     */
    public boolean isFilter() {
        return getObjIdTab() == Kernel.SC_FILTER.id;
    }

    /**
     * На вкладке отчёт?
     * 
     * @param indx
     *            номер вкладки
     * @return true, если отчёт
     */
    public boolean isReport(int indx) {
        return getObjIdTab(indx) == Kernel.SC_REPORT_PRINTER.id;
    }

    /**
     * На вкладке процесс?
     * 
     * @param indx
     *            номер вкладки
     * @return true, если процесс
     */
    public boolean isService(int indx) {
        return getObjIdTab(indx) == Kernel.SC_PROCESS_DEF.id;
    }

    /**
     * На вкладке интерфейс?
     * 
     * @param indx
     *            номер вкладки
     * @return true, если интерфейс
     */
    public boolean isInterface(int indx) {
        return getObjIdTab(indx) == Kernel.SC_UI.id;
    }

    /**
     * На вкладке фильтр?
     * 
     * @param indx
     *            номер вкладки
     * @return true, если фильтр
     */
    public boolean isFilter(int indx) {
        return getObjIdTab(indx) == Kernel.SC_FILTER.id;
    }

    /**
     * Получить id объекта текущей вкладки.
     * 
     * @return the id объекта
     */
    private long getObjIdTab() {
        return getObjIdTab(getSelectedIndex());
    }

    /**
     * Получить id объекта вкладки.
     * 
     * @param indx
     *            номер вкладки
     * @return the id объекта
     */
    private long getObjIdTab(int indx) {
        KrnObject obj = objTabs.get(indx);
        return obj == null ? -1 : obj.classId;
    }

    /**
     * Инициализации команд.
     */
    private void initCommands() {
        saveCmd = new CmdSaveProcess("Save", serviceFrame, CmdSaveProcess.SAVE_CURRENT);
        copyCmd = new CmdCopyProcess("Copy", serviceFrame, true);
        renameCmd = new CmdRenameProcess("Rename", serviceFrame, true);
        deleteCmd = new CmdDeleteProcess("Delete", serviceFrame);
    }

    /**
     * Переименование вкладки с процессом.
     * 
     * @param newTitle
     *            новый заголовок вкладки
     */
    public void renameTab(String newTitle) {
        Long id = docIds.get(getSelectedIndex());
        Document doc = docs.get(id);
        doc.setTitle(newTitle);
        setTitleAt(getSelectedIndex(), newTitle);
    }

    /**
     * Дабавить вкладку процесса
     * 
     * @param doc
     *            документ процесса
     */
    public void addServiceTab(final Document doc, KrnObject nodeObj) {
        setVisible(true);
        ImageIcon image = (doc.isReadOnly()) ? icoSrvRO : icoSrv;
        KrnObject obj = doc.getKrnObject();
        docs.put(obj.id, doc);
        int tc = getTabCount();
        docIds.put(tc, obj.id);
        keepObj(tc, obj, nodeObj);
        super.addTab(doc.getTitle(), image, doc.getGraph());
        setSelectedIndex(tc);
        repaint();

        doc.getGraph().addMouseListener(new MouseListener() {
            private Map<String, Vector> currentPoints = new HashMap<String, Vector>();
            private Map<String, Vector> newPointss = new HashMap<String, Vector>();

            private String edgeID = null;
            private Vector<Point> oldPoints = new Vector<Point>();
            private Vector<Point> newPoints = new Vector<Point>();
            private TransitionEdge currentEdge = null;

            public void mouseReleased(MouseEvent e) {
                if (doc.getGraph().selectedFigs().size() == 1
                        && doc.getGraph().selectedFigs().get(0) instanceof FigTransitionEdge) {
                    Point addedPoint = null;
                    Point removedPoint = null;
                    Point replacedPointBefore = null;
                    Point replacedPointAfter = null;
                    int pointID = -1;
                    List<TransitionEdge> edges = getSelectedDocument().getModel().getEdges();
                    for (int i = 0; i < edges.size(); i++) {
                        if (edges.get(i).getId().equals(edgeID)) {
                            newPoints = edges.get(i).getPoints();
                            currentEdge = edges.get(i);
                            break;
                        }
                    }
                    if (newPoints.size() > oldPoints.size()) {
                        for (int i = 1; i < oldPoints.size(); i++) {
                            if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
                                addedPoint = (Point) newPoints.get(i);
                                pointID = i;
                                break;
                            }
                        }
                        ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).addPointToEdge(
                                currentEdge, addedPoint, pointID);
                        serviceFrame.setProcessModified(true);
                    } else if (newPoints.size() < oldPoints.size()) {
                        for (int i = 1; i < newPoints.size(); i++) {
                            if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
                                removedPoint = (Point) oldPoints.get(i);
                                pointID = i;
                                break;
                            }
                        }
                        ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).removePointFromEdge(
                                currentEdge, removedPoint, pointID);
                        serviceFrame.setProcessModified(true);
                    } else if (newPoints.size() == oldPoints.size()) {
                        for (int i = 1; i < newPoints.size() - 1; i++) {
                            if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
                                replacedPointBefore = (Point) oldPoints.get(i);
                                replacedPointAfter = (Point) newPoints.get(i);
                                pointID = i;
                                ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id)
                                        .replacePointInEdge(currentEdge, replacedPointBefore, replacedPointAfter, pointID);
                                serviceFrame.setProcessModified(true);
                                break;
                            }
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (doc.getGraph().selectedFigs().size() == 1
                        && doc.getGraph().selectedFigs().get(0) instanceof FigTransitionEdge) {
                    List<TransitionEdge> edges = getSelectedDocument().getModel().getEdges();
                    for (int i = 0; i < edges.size(); i++) {
                        if (edges.get(i).getPresentation().equals(doc.getGraph().selectedFigs().get(0))) {
                            edgeID = edges.get(i).getId();
                            oldPoints = edges.get(i).getPoints();
                            break;
                        }
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

    }

    /**
     * Проверка, есть ли на панели вкладка с заданным id объекта.
     * если существует - выбирает вкладку на панели.
     * 
     * @param id
     *            id объекта
     * @return true, если существует
     */
    public boolean isExistSrv(Long id) {
        if (docs.containsKey(id)) {
            setSelectedComponent((docs.get(id)).getGraph());
            return true;
        }
        return false;
    }

    /**
     * Удаление текущей вкладки.
     */
    public void removeCurrentTab() {
        remove(getSelectedIndex());
    }

    /**
     * Получить документ процесса по номеру вкладки.
     * 
     * @param idx
     *            номер вкладки
     * @return документ
     */
    public Document getDocument(int idx) {
        Long l = docIds.get(idx);
        return l == null ? null : docs.get(l);
    }

    /**
     * Получить документ процесса выбранной вкладки
     * 
     * @return the документ или <code>null</code>
     */
    public Document getSelectedDocument() {
        if (getSelectedIndex() > -1 && isService()) {
            return getDocument(getSelectedIndex());
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == miDeleteSrv || src == miRenameSrv || src == miDeleteIfr || src == miRenameIfr || src == miDeleteFlt || src == miRenameFlt) {
        	try {
    			Kernel krn = Kernel.instance();
    			if (krn.getBindingModuleToUserMode()) {
    				KrnObject obj = null;
    				if (isService()) {
    					obj = getSelectedDocument().getKrnObject();
    				} else if (isInterface()) {
    					obj = getSelectedFrame().getUiObject();
    				} else if (isFilter()) {
    					obj = getSelectedFilter().getObj();
    				}
    				if (obj != null) {
	    				KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
	    				if (developerObjs.length > 0) {
	    					long ownerId = developerObjs[0].id;
	    					long currentUserId = krn.getUserSession().userObj.id;
	    					if (ownerId != currentUserId) {
	    						KrnObject userObj = krn.getObjectById(ownerId, 0);
	    						if (userObj != null) { // Владелец объекта существует
	    							KrnClass userCls = krn.getClassByName("User");
	    							KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
	    							String userName = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
	    							StringBuilder message = new StringBuilder("Невозможно ");
	    							if (src == miDeleteSrv || src == miDeleteIfr || src == miDeleteFlt) {
	    								message.append("удалить");
	    							} else {
	    								message.append("переименовать");
	    							}
	    							message.append(" данный ");
	    							if (src == miDeleteSrv || src == miRenameSrv) {
	    								message.append("процесс");
	    							} else if (src == miDeleteIfr || src == miRenameIfr) {
	    								message.append("интерфейс");
	    							} else {
	    								message.append("фильтр");
	    							}
	    							message.append("! Владельцем объекта является пользователь " + userName + ".");
	    							MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, message.toString());
	    							return;
	    						}
	    					}
	    				}
    				}
    			}
    		} catch (KrnException ex) {
    			ex.printStackTrace();
    		}
        }
        if (src == miSaveSrv) {
            saveCmd.doIt();
        } else if (src == miCloseSrv) {
            try {
                removeCurrent();
                activeTypeTabs(Kernel.SC_PROCESS_DEF.id);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        } else if (src == miCopySrv) {
            copyCmd.doIt();
        } else if (src == miRenameSrv) {
            renameCmd.doIt();
        } else if (src == miDeleteSrv) {
            deleteCmd.doIt();
        } else if (src == miHistorySrv) {
            new ObjectHistory(this.getSelectedDocument().getKrnObject().id, "Процесс");
        } else if (src == miSaveIfr) {
            saveCurrentIfr();
        } else if (src == miCloseIfr) {
            try {
                closeCurrent();
                activeTypeTabs(Kernel.SC_UI.id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (src == miCopyIfr) {
            copyInterface();
        } else if (src == miRenameIfr) {
            renameInterface();
        } else if (src == miDeleteIfr) {
            deleteInterface();
        } else if (src == miHistoryIfr) {
            new ObjectHistory(getKrnObjectIfr().id, "Интерфейс");

        } else if (src == miSaveFlt) {
            saveSelectedFlt(null);
            setAllSaveEnabled();
        } else if (src == miCloseFlt) {
            String title = getTitleAt(getSelectedIndex());
            FilterRecord fr = filters.get(filterIds.get(getSelectedIndex()));
            if (!fr.isReadOnly() && fr.isModified() && getSelectedFilter().getObj().id>0) {
                int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE,
                        "Фильтр '" + title + "' был модифицирован!\n Сохранить изменения?");
                if (res != ButtonsFactory.BUTTON_NOACTION) {
                    if (res == ButtonsFactory.BUTTON_YES) {
                        saveSelectedFlt(null);
                    } else if (res == ButtonsFactory.BUTTON_CANCEL) {
                        return;
                    }
                }
            }
            try {
                removeSelectedFlt();
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            setAllSaveEnabled();
            activeTypeTabs(Kernel.SC_FILTER.id);
        } else if (src == miCopyFlt) {
        } else if (src == miRenameFlt) {
        } else if (src == miDeleteFlt) {
            deleteCurrent();
        } else if (src == miSaveRpt) {
        } else if (src == miCloseRpt) {
            removeCurrentRpt();
            activeTypeTabs(Kernel.SC_REPORT_PRINTER.id);
        } else if (src == miCopyRpt) {
        } else if (src == miRenameRpt) {
        } else if (src == miDeleteRpt) {
        }
    }

    /**
     * Закрыть текущий отчёт.
     */
    private void removeCurrentRpt() {
        int idx = getSelectedIndex();
        remove(idx);
        fireChange();
    }
    
    public void removeSrvTabByObj(KrnObject obj) {
    	int idx = -1;
    	for(Entry<Integer, KrnObject> entry: objTabs.entrySet()) {
    		if(entry.getValue().uid.equals(obj.uid)) {
    			idx = entry.getKey();
    		}
    	}
    	if(idx > -1) {
//    		removeObj(idx);
    		try {
				setSelectedIndex(idx);
				removeCurrent();
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    /**
     * Закрыть текущий процесс.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void removeCurrent() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = docIds.get(idx);
//        Icon icon = getIconAt(idx);
        Document curService = docs.get(objId);
        String objUid = curService.getKrnObject().uid;        
        if (EditorWindow.delTabs(objUid)) {
        	return;
        }
        Icon icon = getIconAt(idx);
        if (icon == icoSrvMod) {
            int res = MessagesFactory.showMessageDialog((JFrame) serviceFrame.getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, "Процесс '" + getTitleAt(idx) + "' модифицирован.\n"
                            + "Сохранить изменения?");
            switch (res) {
            case BUTTON_YES:
                saveCmd.doIt();
                docs.remove(objId);
                docIds.remove(idx);
                remove(idx);
                Kernel.instance().releaseEngagedObject(objId);
                break;
            case BUTTON_NO:
                docs.remove(objId);
                docIds.remove(idx);
                remove(idx);
                Kernel.instance().releaseEngagedObject(objId);
                ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).removeChanges();
                break;
            case BUTTON_CANCEL:
                return;
            }
        } else {
            Document doc = docs.remove(objId);
            docIds.remove(idx);
            remove(idx);
            if (!doc.isReadOnly())
                Kernel.instance().releaseEngagedObject(objId);
        }
        if (ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).canSave()) {
            ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).writeObject();
            serviceFrame.loadServices();
        }
        ServiceActionsConteiner.removeFromConteiner(curService.getKrnObject().id);
        fireChange();
    }

    /**
     * Установить признак модификации объекта.
     * 
     * @param isModified
     *            новый признак модификации объекта
     */
    public void setProcessModified(boolean isModified) {
        if (getSelectedIndex() != -1) {
            Document d = docs.get(docIds.get(getSelectedIndex()));
            if (!d.isReadOnly()) {
                setIconAt(getSelectedIndex(), isModified ? icoSrvMod : icoSrv);
                if (ServiceActionsConteiner.isContein(getSelectedDocument().getKrnObject().id)) {
                    if (ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).getCanClean()) {
                        Node actionsElement = ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id)
                                .getXMLDocumentRedo().getElementsByTagName("actions").item(0);
                        while (actionsElement.hasChildNodes()) {
                            actionsElement.removeChild(actionsElement.getLastChild());
                        }
                    }
                    ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).setUndoRedoActivity(
                            serviceFrame);
                }
            }
        }
    }
    
    public void setProcessModified(Document d, boolean isModified) {
        if (d!= null) {            
            if (!d.isReadOnly()) {
            	Set<Integer> idkeys = docIds.keySet();
            	int k = 0;
            	for(int idKey: idkeys) {
            		if(docIds.get(idKey) == d.getKrnObject().id) {
            			k = idKey;
            			break;
            		}
            	}
                setIconAt(k, isModified ? icoSrvMod : icoSrv);
                if (ServiceActionsConteiner.isContein(d.getKrnObject().id)) {
                    if (ServiceActionsConteiner.getServiceActions(d.getKrnObject().id).getCanClean()) {
                        Node actionsElement = ServiceActionsConteiner.getServiceActions(d.getKrnObject().id)
                                .getXMLDocumentRedo().getElementsByTagName("actions").item(0);
                        while (actionsElement.hasChildNodes()) {
                            actionsElement.removeChild(actionsElement.getLastChild());
                        }
                    }
                    ServiceActionsConteiner.getServiceActions(d.getKrnObject().id).setUndoRedoActivity(
                            serviceFrame);
                }
            }
        }
    }

    /**
     * Проверка, изменена ли вкладка.
     * 
     * @param indx
     *            номер вкладки
     * @return true, если вкладка изменена
     */
    public boolean isTabModified(int indx) {
        Icon ico = getIconAt(indx);
        return icoSrvMod.equals(ico) || icoIfrMod.equals(ico) || icoFltMod.equals(ico) || icoRptMod.equals(ico);
    }

    /**
     * Принудительно вызвать событие изменения.
     */
    public void fireChange() {
        fireStateChanged();
    }

    /**
     * Проверка, есть ли на панели вкладка с заданным id объекта.
     * если существует - выбирает вкладку на панели.
     * 
     * @param id
     *            id объекта
     * @return true, если существует
     */
    public boolean isExistIfr(Long objId) {
        if (interfaces.containsKey(objId)) {
            setSelectedComponent(interfaces.get(objId).getRootPanel());
            return true;
        }
        return false;
    }

    /**
     * Получить карту интерфейсов.
     * 
     * @return the interfaces
     */
    public Map<Long, InterfaceFrame> getInterfaces() {
        return interfaces;
    }

    /**
     * Добавить вкладку с интерфейсом.
     * 
     * @param frm
     *            фрейм интерфейса
     */
    public void addTab(InterfaceFrame frm, KrnObject nodeObj) {
        setVisible(true);
        ImageIcon image = (frm.isReadOnly()) ? icoIfrRO : icoIfr;
        KrnObject obj = frm.getUiObject();
        interfaces.put(obj.id, frm);
        int tc = getTabCount();
        interfaceObjIds.put(tc, obj.id);
        keepObj(tc, obj, nodeObj);
        super.addTab(frm.getTitle(), image, frm.getRootPanel());
        setSelectedIndex(tc);
        repaint();
    }

    /**
     * Получить основную панель фрейма выбранного интерфейса.
     * 
     * @return корневая панель фрейма
     */
    public OrGuiComponent getOrGuiComponent() {
        return getOrGuiComponent(getSelectedIndex());
    }

    /**
     * Получить основную панель фрейма неоходимого интерфейса.
     * 
     * @param idx
     *            номер вкладки с интрфейсом
     * @return корневая панель фрейма
     */
    public OrGuiComponent getOrGuiComponent(int idx) {
        Long objId = interfaceObjIds.get(idx);
        Component c = interfaces.get(objId).getRootPanel();
        return (OrGuiComponent) c;
    }

    /**
     * Получить <code>KrnObject</code>> выбранного интерфейса.
     * 
     * @return KrnObject
     */
    public KrnObject getKrnObjectIfr() {
        return getKrnObjectIfr(getSelectedIndex());
    }

    /**
     * Получить <code>KrnObject</code>> необходимого интерфейса.
     * 
     * @param idx
     *            номер вкладки
     * @return KrnObject
     */
    public KrnObject getKrnObjectIfr(int idx) {
        Long objId = interfaceObjIds.get(idx);
        return objId == null ? null : interfaces.get(objId).getUiObject();
    }

    /**
     * Получить заголовок выбранного интерфейса.
     * 
     * @return заголовок
     */
    public String getTitleIfr() {
        Long objId = interfaceObjIds.get(getSelectedIndex());
        return objId == null ? null : interfaces.get(objId).getTitle();
    }

    /**
     * Получить узел дерева интерфейсов для выбранного интерфейса
     * 
     * @return InterfaceNode
     */
    public InterfaceNode getSelectedNodeIfr() {
        DesignerFrame frame = (DesignerFrame) getTopLevelAncestor();
        final InterfaceTree tree = frame.getInterfaceTree();
        InterfaceNode root = tree.getRoot();
        return (InterfaceNode) root.find(getKrnObjectIfr()).getLastPathComponent();
    }

    /**
     * Получить количество компонентов на интерфейсе.
     * 
     * @param idx
     *            номер вкладки с интерфейсом
     * @return количество компонентов
     */
    public int getCounter(int idx) {
        Long objId = interfaceObjIds.get(idx);
        return interfaces.get(objId).getInterfaceCounter();
    }

    protected void processContainerEvent(ContainerEvent e) {
        if (e.getID() == ContainerEvent.COMPONENT_ADDED) {
            miSaveSrv.setVisible(true);
            miCloseSrv.setVisible(true);
            miCopySrv.setVisible(true);
            miRenameSrv.setVisible(true);
            miDeleteSrv.setVisible(true);
        } else if (e.getID() == ContainerEvent.COMPONENT_REMOVED) {
            if (getComponentCount() == 0) {
                miSaveSrv.setVisible(false);
                miCloseSrv.setVisible(false);
                miCopySrv.setVisible(false);
                miRenameSrv.setVisible(false);
                miDeleteSrv.setVisible(false);
            }
        }
        super.processContainerEvent(e);
    }
    
    /**
     * Сохранить текущий интерфейс.
     */
    private boolean saveCurrentIfr() {
        try {
            InterfaceFrame frame = getSelectedFrame();
            boolean isIgnore = checkReferences(frame);
            if (isIgnore) {
	            frame.save(null);
	            if (frame.isModified() && !frame.isReadOnly()) {
	                frame.setModified(false);
	                setIconAt(getSelectedIndex(), icoIfr);
	            }
            }
            return isIgnore;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean checkReferences(InterfaceFrame frame) {
    	try {
	    	OrGuiComponent rootPanel = (OrGuiComponent) frame.getRootPanel();
			PropertyNode pn = rootPanel.getProperties().getChild("ref").getChild("data");
			if (pn != null) {
				PropertyValue pv = rootPanel.getPropertyValue(pn);
				if (pv != null) {
					String path = pv.stringValue(); // Данные.Привязка к БД
					KrnClass targetCls = extractClassFromPath(path);
					if (targetCls != null) {
			            List<PathWarningsListItem> warnings = new ArrayList<PathWarningsListItem>();
			            viewInterfaceComponents(rootPanel, targetCls, warnings);
			            if (warnings.size() > 0) {
							PathWarningsList warningsList = new PathWarningsList();
							warningsList.addToList(warnings.toArray());
							DesignerModalFrame dialog = new DesignerModalFrame((Frame) this.getTopLevelAncestor(), "Внимание! Обнаружены ссылки на другие классы.", warningsList);
							warningsList.setParent(dialog);
							dialog.setOkText("Игнорировать");
							dialog.setCancelBtnText("Исправить");
							dialog.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dialog.getSize()));
							dialog.show();
							if (dialog.getResult() == 1) {
								PathWarningsListItem selectedItem = (PathWarningsListItem) warningsList.getList().getSelectedValue();
								if (selectedItem == null) {
									selectedItem = warnings.get(0);
								}
			                	DesignerFrame.instance().getController().addSelection(selectedItem.getComponent(), false);
			                	PropertyTable table = DesignerFrame.instance().getInspector().getPropTable();
			        	        List<Property> children = table.getPropertyTableModel().getChildren();
			        	        for (int i = 0; i < children.size(); i++) {
			        	        	Property property = children.get(i);
			        				if (property.toString().equals(selectedItem.getProperty().toString())) {
			                        	table.setRowSelectionInterval(i + 1, i + 1);
			        					break;
			        				}
			        			}
								return false;
							}
			            }
					}
				}
			}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
		return true;
    }
    
    private KrnClass extractClassFromPath(String path) {
    	KrnClass cls = null;
    	StringTokenizer st = new StringTokenizer(path, ".");
		int count = st.countTokens();
		if (count > 0) {
			try {
				String clsName = st.nextToken();
				int p = clsName.indexOf('(');
	        	if (p != -1)
	        		clsName = clsName.substring(0, p);
	        	
				cls = Kernel.instance().getClassByName(clsName);
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
		return cls;
    }
    
    private List<KrnClass> extractClassFromExpression(String expression) {
    	List<KrnClass> list = new ArrayList<KrnClass>();
    	Pattern pattern = Pattern.compile("\\$Interface.getAttr\\(\"([^\")]*)\"\\)");
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
        	String path = matcher.group(1);
        	KrnClass cls = extractClassFromPath(path);
        	if (cls != null && !list.contains(cls)) {
        		list.add(cls);
        	}
        }
		return list;
    }
    
    private void viewInterfaceComponents(OrGuiComponent component, KrnClass targetCls, List<PathWarningsListItem> warnings) {
    	viewComponentProperties(component, component.getProperties(), targetCls, warnings);
        if (component instanceof OrGuiContainer && !(component instanceof OrScrollPane) && !(component instanceof OrTable)) {
            Container cont = (Container) component;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                Component comp = cont.getComponent(i);
                if (comp instanceof OrGuiComponent) {
                	viewInterfaceComponents((OrGuiComponent) comp, targetCls, warnings);
                }
            }
        } else if (component instanceof OrGuiContainer && component instanceof OrScrollPane) {
            OrScrollPane cont = (OrScrollPane) component;
            for (int i = 0; i < cont.getOrComponentCount(); i++) {
                Component comp = cont.getOrComponent(i);
                if (comp instanceof OrGuiComponent) {
                	viewInterfaceComponents((OrGuiComponent) comp, targetCls, warnings);
                }
            }
        } else if (component instanceof OrTable) {
            JTable table = ((OrTable) component).getJTable();
            OrTableModel tabModel = (OrTableModel) table.getModel();
            for (int i = 0; i < table.getColumnCount(); i++) {
                OrTableColumn tc = tabModel.getColumn(i);
                if (tc != null) {
                	viewInterfaceComponents(tc, targetCls, warnings);
                }
            }
            OrPanel pan = ((OrTable) component).getAddPan();
            if (pan != null) {
            	viewInterfaceComponents(pan, targetCls, warnings);
            }
        } else if (component instanceof OrPopUpPanel) {
            OrPanel panel = ((OrPopUpPanel) component).getMainPanel();
            if (panel != null) {
                Component[] comps = panel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof OrGuiComponent) {
                    	viewInterfaceComponents((OrGuiComponent) comp, targetCls, warnings);
                    }
                }
            }
        } else if (component instanceof OrCollapsiblePanel) {
            OrPanel panel = ((OrCollapsiblePanel) component).getContent();
            if (panel != null) {
                Component[] comps = panel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof OrGuiComponent) {
                    	viewInterfaceComponents((OrGuiComponent) comp, targetCls, warnings);
                    }
                }
            }
        } else if (component instanceof OrAccordion) {
            List<OrPanel> panels = ((OrAccordion) component).getContent();
            if (panels != null) {
                for (OrPanel panel : panels) {
                    Component[] comps = panel.getComponents();
                    for (Component comp : comps) {
                        if (comp instanceof OrGuiComponent) {
                        	viewInterfaceComponents((OrGuiComponent) comp, targetCls, warnings);
                        }
                    }
                }
            }
        }
    }
    
    private void viewComponentProperties(OrGuiComponent component, PropertyNode property, KrnClass targetCls, List<PathWarningsListItem> warnings) {
    	for (int i = 0; i < property.getChildCount(); i++) {
    		PropertyNode child = property.getChildAt(i);
        	if (child.getType() == Types.EXPR) {
        		// Проверить внутри выражения все Interface.getAttr("...")
        		PropertyValue pv = component.getPropertyValue(child);
        		if (pv != null) {
        			String expression = pv.stringValue();
        			if (expression != null && expression.length() > 0) {
        				List<KrnClass> list = extractClassFromExpression(expression);
        				for (KrnClass cls: list) {
            				if (!targetCls.equals(cls)) {
            					String title = "";
            					PropertyNode pn;
            					if (component.getProperties() instanceof ColumnPropertyRoot) {
            						pn = component.getProperties().getChild("header").getChild("text");
            					} else {
            						pn = component.getProperties().getChild("title");
            					}
            					if (pn != null) {
            						pv = component.getPropertyValue(pn);
            						if (pv != null) {
            							title = pv.stringValue();
            						}
            					}
            					if (title.trim().length() == 0) {
            						title = component.getUUID();
            					}
            					warnings.add(new PathWarningsListItem("Компонент '" + title + "' в свойстве '" +  child.getFullName() +
    			            			"' содержит ссылку на класс '" + cls.name + "', отличающийся от класса интерфейса '" + targetCls.name + "'.", child, component));
            				}
        				}
        			}
        		}
        	} else if (child.getType() == Types.REF && "ref.data".equals(child.getFullPath())) {
        		// Проверить путь привязки к БД
        		PropertyValue pv = component.getPropertyValue(child);
        		if (pv != null) {
        			String path = pv.stringValue();
        			if (path != null && path.length() > 0) {
        				KrnClass cls = extractClassFromPath(path);
        				if (!targetCls.equals(cls)) {
        					String title = "";
        					PropertyNode pn;
        					if (component.getProperties() instanceof ColumnPropertyRoot) {
        						pn = component.getProperties().getChild("header").getChild("text");
        					} else {
        						pn = component.getProperties().getChild("title");
        					}
        					if (pn != null) {
        						pv = component.getPropertyValue(pn);
        						if (pv != null) {
        							title = pv.stringValue();
        						}
        					}
        					if (title.trim().length() == 0) {
        						title = component.getUUID();
        					}
        					warnings.add(new PathWarningsListItem("Компонент '" + title + "' в свойстве '" +  child.getFullName() +
			            			"' содержит ссылку на класс '" + cls.name + "', отличающийся от класса интерфейса '" + targetCls.name + "'.", child, component));
        				}
        			}
        		}
        	}
        	if (child.getChildCount() > 0) {
        		viewComponentProperties(component, child, targetCls, warnings);
        	}
    	}
    }
    
    public void removeIfcTabByObj(KrnObject obj) {
    	int idx = -1;
    	for(Entry<Integer, KrnObject> entry: objTabs.entrySet()) {
    		if(entry.getValue().uid.equals(obj.uid)) {
    			idx = entry.getKey();
    		}
    	}
    	if(idx > -1) {
//    		removeObj(idx);
    		try {
				setSelectedIndex(idx);
				closeCurrent();
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public void closeCurrent() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = interfaceObjIds.get(idx);
        ArrayList<Long> loadingInterfacesID = (ArrayList<Long>) DesignerFrame.getLoadingInterfaceID();
        int deletedInterfaceID = 0;
        for (int i = 0; i < loadingInterfacesID.size(); i++) {
            if (loadingInterfacesID.get(i) == objId)
                deletedInterfaceID = i;
        }
        InterfaceFrame curInterface = interfaces.get(objId);
        String objUid = curInterface.getUiObject().uid;
        if(EditorWindow.delTabs(objUid)) {
        	return;
        }
        if (isModifiedIfr(getSelectedIndex())) {
            String mess = "Интерфейс '" + getTitleAt(getSelectedIndex()) + "' был модифицирован.\nСохранить изменения?";
            int t = MessagesFactory.showMessageDialog((Frame) this.getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            if (t == ButtonsFactory.BUTTON_YES) {
                if (saveCurrentIfr()) {
	                interfaces.remove(objId);
	                loadingInterfacesID.remove(deletedInterfaceID);
	                interfaceObjIds.remove(idx);
	                remove(idx);
	                Kernel.instance().releaseEngagedObject(objId);
                } else {
                	return;
                }
            } else if (t == ButtonsFactory.BUTTON_CANCEL) {
                return;
            } else if (t == ButtonsFactory.BUTTON_NO) {
                interfaces.remove(objId);
                loadingInterfacesID.remove(deletedInterfaceID);
                interfaceObjIds.remove(idx);
                remove(idx);
                Kernel.instance().releaseEngagedObject(objId);
                InterfaceActionsConteiner.getInterfaceActions(curInterface.getUiObject().id).removeChanges();
            }
        } else {
            InterfaceFrame frm = interfaces.remove(objId);
            loadingInterfacesID.remove(deletedInterfaceID);
            interfaceObjIds.remove(idx);
            remove(idx);
            if (!frm.isReadOnly())
                Kernel.instance().releaseEngagedObject(objId);
        }
        if (InterfaceActionsConteiner.getInterfaceActions(curInterface.getUiObject().id).canSave()) {
            InterfaceActionsConteiner.getInterfaceActions(curInterface.getUiObject().id).writeObject();
            DesignerFrame.loadInterfaces();
        }
        InterfaceActionsConteiner.removeFromConteiner(curInterface.getUiObject().id);
        fireStateChanged();
    }

    /**
     * Копирование интерфейса.
     */
    private void copyInterface() {
        String oldName = getTitleAt(getSelectedIndex());
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.COPY_TYPE, getTitleAt(getSelectedIndex()));
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Создание копии интерфейса", cp);
        dlg.pack();
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            String ifcName = cp.getElementName();
            if (ifcName == null) {
                JOptionPane.showMessageDialog(this, "Неверное имя интерфейса!", "Сообщение", JOptionPane.ERROR_MESSAGE);
            } else {
                Kernel krn = Kernel.instance();
                krn.setAutoCommit(false);
                try {
                    KrnObject ui = krn.createObject(Kernel.SC_UI, 0);
                    DesignerFrame.getLoadingInterfaceID().add(ui.id);
                    KrnObject lang = krn.getInterfaceLanguage();
                    long langId = (lang != null) ? lang.id : 0;
                    InterfaceFrame sourceFrame = getSelectedFrame();
                    krn.setString(ui.id, ui.classId, "title", 0, langId, ifcName, 0);
            		long currentUserId = krn.getUserSession().userObj.id;
                    krn.setLong(ui.id, ui.classId, "developer", 0, currentUserId, 0);
                    krn.writeLogRecord(SystemEvent.EVENT_COPY_INTERFACE, "'" + oldName + "' в '" + ifcName + "'");
                    InterfaceFrame frame = sourceFrame.makeCopy(ui, null);
                    // генерация новых UUID для копии интерфейса
                    PropertyHelper.forseGenUUID = true;
                    PropertyHelper.genUUID = false;
                    frame.load(DesignerFrame.instance().getProgressBar());
                    frame.save(DesignerFrame.instance().getProgressBar());
                    addTab(frame, null);
                    addCopyToTree(sourceFrame.getUiObject(), ui, ifcName);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    PropertyHelper.forseGenUUID = false;
                }
                krn.setAutoCommit(true);
            }
        }
    }

    /**
     * Проверяет, является вкладка с интерфейсов изменённой.
     * 
     * @param idx
     *            номер вкладки
     * @return true, если изменена
     */
    public boolean isModifiedIfr(int idx) {
        Long objId = interfaceObjIds.get(idx);
        if (objId == null) {
            return false;
        } else {
            InterfaceFrame p = interfaces.get(objId);
            return p.isModified();
        }
    }

    /**
     * Задать флаг модификации вкладки с интерфейсом
     * 
     * @param isModified
     *            флаг модификации
     * @param idx
     *            номер вкладки
     */
    public void setModifiedIfr(boolean isModified, int idx) {
        Long objId = interfaceObjIds.get(idx);
        InterfaceFrame p = interfaces.get(objId);
        p.setModified(isModified);
        if (!p.isReadOnly()) {
            if (!isModified) {
                setIconAt(idx, icoIfr);
            } else {
                setIconAt(idx, icoIfrMod);
            }
        }
    }

    @Override
    public int getComponentCount() {
        return getTabCount();
    }

    /**
     * Переименовать интерфейс
     */
    private void renameInterface() {
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, getTitleAt(getSelectedIndex()));
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Переименование интерфейса", cp);
        dlg.show();
        int res = dlg.getResult();
        if (res == ButtonsFactory.BUTTON_OK) {
            Kernel krn = Kernel.instance();
            try {
                KrnObject lang = krn.getInterfaceLanguage();
                KrnClass cls = krn.getClassByName("UI");
                String oldName = getTitleIfr();
                KrnObject obj = getKrnObjectIfr(getSelectedIndex());
                KrnAttribute attr = krn.getAttributeByName(cls, "title");
                krn.setString(obj.id, attr.id, 0, lang.id, cp.getElementName(), 0);
                krn.writeLogRecord(SystemEvent.EVENT_RENAME_INTERFACE, "'" + oldName + "' в '" + cp.getElementName() + "'");
                setTitleAt(getSelectedIndex(), cp.getElementName());
                DesignerFrame frame = (DesignerFrame) getParent().getParent().getParent();
                final InterfaceTree tree = frame.getInterfaceTree();
                InterfaceNode root = tree.getRoot();
                InterfaceNode source = (InterfaceNode) root.find(getKrnObjectIfr()).getLastPathComponent();
                source.rename(cp.getElementName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Удалить интерфейс.
     */
    private void deleteInterface() {
        String mess = "Удалить интерфейс '" + getTitleAt(getSelectedIndex()) + "'?";
        int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            try {
                final DesignerFrame frame = (DesignerFrame) getParent().getParent().getParent();
                final InterfaceTree tree = frame.getInterfaceTree();
                InterfaceTreeModel model = (InterfaceTreeModel) tree.getModel();
                InterfaceNode root = tree.getRoot();
                InterfaceNode sourceNode = (InterfaceNode) root.find(getKrnObjectIfr()).getLastPathComponent();
                model.deleteNode(sourceNode, false);
                closeCurrent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Дабавить копию интерфейса в дерево интерфейсов
     * 
     * @param source
     *            интерфейс-источник
     * @param target
     *            интерфейс, новый
     * @param title
     *            заголовок интерфейса
     */
    private void addCopyToTree(KrnObject source, KrnObject target, String title) {
        DesignerFrame frame = DesignerFrame.instance();
        final InterfaceTree tree = frame.getInterfaceTree();
        InterfaceTreeModel model = (InterfaceTreeModel) tree.getModel();
        InterfaceNode root = tree.getRoot();
        InterfaceNode sourceNode = (InterfaceNode) root.find(source).getLastPathComponent();
        InterfaceNode parentNode = (InterfaceNode) sourceNode.getParent();
        try {
            model.addNode(new InterfaceNode(target, title, tree.getLangId()), parentNode, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("counter".equals(evt.getPropertyName())) {
            if (getSelectedIndex() > -1) {
                InterfaceFrame ir = getSelectedFrame();
                if (ir != null) {
                    ir.setComponentCounter((Integer) evt.getNewValue());
                }
            }
        }
    }

    /**
     * Получить интерфейс с выбранной вкладки
     * 
     * @return интерфейс или <code>null</code>
     */
    public InterfaceFrame getSelectedFrame() {
        int i = getSelectedIndex();
        if (i != -1 && isInterface()) {
            return getFrameAt(i);
        }
        return null;
    }

    /**
     * Получить интерфейс с определённой вкладки
     * 
     * @param idx
     *            номер вкладки
     * @return интерфейс
     */
    public InterfaceFrame getFrameAt(int idx) {
        Long l = interfaceObjIds.get(idx);
        return l == null ? null : interfaces.get(l);
    }

    public KrnObject getInterfaceLang() {
        return com.cifs.or2.client.Utils.getInterfaceLang();
    }

    public void setInterfaceLang(KrnObject lang) {
    }

    public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
        setInterfaceLang(lang);
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    public String getNextUid() {
        return null;
    }

    public String getString(String uid) {
        return null;
    }

    public String getString(String uid, String defStr) {
        return null;
    }

    public byte[] getBytes(String s) {
        return null;
    }

    public void setString(String uid, String str) {
    }

    public void setBytes(String s, byte[] bytes) {
    }

    public void addRefGroup(int group, CheckContext context) {
    }

    public Cache getCash() {
        return null;
    }

    public long getFlowId() {
        return 0;
    }

    public long getTransactionId() {
        return 0;
    }
    public Map<String, OrRef> getContentRef() {
        return null;
    }

    public List getRefGroups(int group) {
        return null;
    }

    public Map<String, OrRef> getRefs() {
        return null;
    }

    public OrGuiComponent getPanel() {
        return null;
    }

    public int getTransactionIsolation() {
        return 0;
    }

    public ReportPrinter getReportPrinter(long id) {
        return null;
    }

    public KrnObject getDataLang() {
        return null;
    }

    public int getEvaluationMode() {
        return 0;
    }

    public void setRootReport(ReportRecord reportRecord) {
    }

    public InterfaceManager getInterfaceManager() {
        return null;
    }

    public void setAllwaysFocused(OrGuiComponent comp) {
    }

    /**
     * Проход всех записей фильтров, если хоть одна запись модифицированна
     * даётся разрешение на сохранение (активируется кнопка "Сохранить")
     */
    private void setAllSaveEnabled() {
        if (getTabCount() > 0) {
            for (int i = 0; i < getTabCount(); i++) {
                if (isFilter(i)) {
                    FilterRecord filterRecord = filters.get(filterIds.get(i));
                    if (!filterRecord.isReadOnly() && filterRecord.isModified()) {
                        filterPanel.setSaveEnabled(true);
                        return;
                    }
                }
            }
        }
        filterPanel.setSaveEnabled(false);
    }

    /**
     * Добавить вкладку с фитром
     * 
     * @param title
     *            заголовок фильтра
     * @param tree
     *            дерево фильтров
     * @param parent
     *            родительский узел
     * @param readOnly
     *            флаг "только для чтения"
     */
    public void addFilterTab(String title, OrFilterTree tree, FilterNode parent, boolean readOnly, KrnObject nodeObj) {
        setVisible(true);
        JScrollPane sp = new JScrollPane(tree);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        ImageIcon image = (readOnly) ? icoFltRO : icoFlt;
        filters.put(parent.getKrnObj().id, new FilterRecord(tree, parent, false, readOnly));
        int tc = getTabCount();
        filterIds.put(tc, parent.getKrnObj().id);
        keepObj(tc, parent.getKrnObj(), nodeObj);
        super.addTab(title, image, sp);
        setSelectedIndex(tc);
        tree.addPropertyListener(this);
    }

    /**
     * Добавить вкладку с отчётом
     * 
     * @param rf
     *            ReportPanel
     */
    public void addReport(ReportPanel rf, KrnObject nodeObj) {
        setVisible(true);
        AbstractDesignerTreeNode selNode = rf.getTree().getSelectedNode();
        int tc = getTabCount();
        keepObj(tc, selNode.getKrnObj(), nodeObj);
        super.addTab(selNode.toString(), icoRpt, new JPanel());
        setSelectedIndex(tc);
        repaint();
    }

    /**
     * Удалить текущую вкладку с фильтром
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void removeSelectedFlt() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = filterIds.remove(idx);
        FilterRecord f = filters.remove(objId);
        remove(idx);
        if (!f.readOnly) {
            Kernel.instance().releaseEngagedObject(objId);
        }
        fireChange();
    }

    /**
     * Проверяет, открыт ли фильтр в редакторе
     * если открыт, активирует вкладку
     * 
     * @param id
     *            фильтра
     * @return true, если открыт
     */
    public boolean isFilterOpened(Long id) {
        for (int i = 0; i < getTabCount(); i++) {
            if (id.equals(filterIds.get(i))) {
                setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет, модифицированна ли вкладка с фильтром
     * 
     * @param id
     *            идентификатор фильтра
     * @return true, если модифицированна
     */
    public boolean isTabModified(Long id) {
        FilterRecord p = filters.get(id);
        return p.isModified;
    }

    /**
     * Получить копируемый узел дерва фильтров.
     * 
     * @return копируемый узел
     */
    public OrFilterNode getCopyNode() {
        return copyNode;
    }

    /**
     * Установить копируемый узел дерва фильтров.
     * 
     * @param copyNode
     *            копируемый узел
     */
    public void setCopyNode(OrFilterNode copyNode) {
        this.copyNode = copyNode;
    }

    /**
     * Получить OrFilterTree по индексу вкладки в панели.
     * 
     * @return OrFilterTree
     */
    public OrFilterTree getSelectedFilter() {
        int idx = getSelectedIndex();
        if (idx != -1 && isFilter()) {
            return filters.get(filterIds.get(idx)).o;
        }
        return null;
    }

    /**
     * Получить запись фильтра по индексу вкладки в панели.
     * 
     * @return FilterRecord
     */
    public FilterRecord getSelectedFilterRecord() {
        int idx = getSelectedIndex();
        if (idx != -1 && isFilter()) {
            return filters.get(filterIds.get(idx));
        }
        return null;
    }

    /**
     * Задать флаг модификации вкладки с фильтром
     * 
     * @param isModified
     *            флаг модификации
     */
    private void setTabModified(boolean isModified) {
        if (getSelectedIndex() != -1) {
            FilterRecord f = filters.get(filterIds.get(getSelectedIndex()));
            f.setModified(isModified);
            if (!f.isReadOnly()) {
                setIconAt(getSelectedIndex(), isModified ? icoFltMod : icoFlt);
            }
        }
    }

    /**
     * * Задать флаг модификации вкладки с фильтром и записи фильтра
     * 
     * @param isModified
     *            флаг модификации
     * @param f
     *            запись фильтра
     */
    private void setTabModified(boolean isModified, FilterRecord f) {
        f.setModified(isModified);
        if (!f.isReadOnly()) {
            for (Entry<Integer, Long> ent : filterIds.entrySet()) {
                if (f.parent.getKrnObj().id == ent.getValue()) {
                    setIconAt(ent.getKey(), isModified ? icoFltMod : icoFlt);
                }
            }
        }
    }

    /**
     * Удалить текущий фильтр
     */
    private void deleteCurrent() {
        int idx = getSelectedIndex();
        Long objId = filterIds.get(idx);
        String mess = "Удалить фильтр '" + getTitleAt(idx) + "'?";
        int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            try {
                if (tree != null) {
                    FilterRecord fr = filters.get(objId);
                    final FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel) tree.getModel();
                    model.deleteNode(fr.parent, false);
                    removeSelectedFlt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Установить filterPanel для панели вкладок.
     * 
     * @param filterPanel
     *            the new main ancestor
     */
    public void setFilterPanel(FiltersPanel mainAncestor) {
        this.filterPanel = mainAncestor;
    }

    /**
     * Сохранить текущий фильтр
     * 
     * @param rec
     *            FilterRecord
     */
    private void saveSelectedFlt(FilterRecord rec) {
        if (filterPanel != null) {
        	if(rec==null)
        		rec = filters.get(filterIds.get(getSelectedIndex()));
            if (!rec.isReadOnly()) {
                filterPanel.saveCurrent(rec.o);
                rec.setModified(false);
                setTabModified(false,rec);
            }
        }
    }

    /**
     * Сохранить все фильтры.
     */
    public void saveAll() {
        for (int i = 0; i < getTabCount(); i++) {
        	Long objId = filterIds.get(i);
        	if (objId != null) {
        		FilterRecord p = filters.get(objId);
        		if(p.isModified()) {
        			saveSelectedFlt(p);
        		}
        	} 
        }
        if (getTabCount() == 1) {
            filterPanel.setSaveEnabled(false);
        } else if (getTabCount() > 1) {
            for (int i = 0; i < getTabCount(); i++) {
            	Long objId = filterIds.get(i);
            	if (objId != null) {
            		FilterRecord p = filters.get(objId);
            		if(!p.isReadOnly() && p.isModified()) {
            			filterPanel.setSaveEnabled(true);
            			break;
            		}
            	}
            }
        }
        setTabModified(false);
        miSaveFlt.setVisible(false);
    }

    /**
     * Получить index for.
     * 
     * @param obj
     *            the obj
     * @return the index for
     */
    public int getIndexFor(KrnObject obj) {
        for (Entry<Integer, KrnObject> ent : objTabs.entrySet()) {
            if (ent.getValue().id == obj.id) {
                return ent.getKey();
            }
        }
        return -1;
    }

    /**
     * Задать дерево фильтров.
     * 
     * @param tree
     *            the new tree
     */
    public void setTree(FiltersTree tree) {
        this.tree = tree;
    }

    /**
     * Получить modified titles.
     * 
     * @return the modified titles
     */
    public String[] getModifiedTitles() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getTabCount(); i++) {
            FilterRecord rec = filters.get(filterIds.get(i));
            if (!rec.isReadOnly() && rec.isModified()) {
                list.add(rec.parent.toString());
            }
        }
        String[] res = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    /**
     * Получить owner.
     * 
     * @return the owner
     */
    public FiltersPanel getOwner() {
        return filterPanel.isOwnerFilters() ? filterPanel : null;
    }

    /**
     * Получить filters.
     * 
     * @return the filters
     */
    public Map<Long, FilterRecord> getFilters() {
        return filters;
    }

    @Override
    public void propertyModified(OrGuiComponent c) {

        if (isFilter() && c instanceof OrFilterNode) {
            OrFilterNode fn = (OrFilterNode) c;
            FilterNode n = fn.getFilterNode();
            FilterRecord p = filters.get(n.getKrnObj().id);
            if (!p.isReadOnly() && p.parent.getKrnObj().id>0) {
                setTabModified(true, p);
                if (filterPanel != null) {
                    filterPanel.setSaveEnabled(true);
                }
            }
        } else if (isInterface()) {
            if (getSelectedIndex() > -1) {
                InterfaceFrame p = getSelectedFrame();
                if (!p.isModified() && !p.isReadOnly()) {
                    p.setModified(true);
                    setIconAt(getSelectedIndex(), icoIfrMod);
                }
            }
        } else {
            System.out.print("Конфликт! Изменение нетипичных свойств для текущего редактора ");
            if (isService()) {
                System.out.println("процесссов");
            } else if (isInterface()) {
                System.out.println("интерфейсов");
            } else if (isFilter()) {
                System.out.println("фильтров");
            } else if (isReport()) {
                System.out.println("отчётов");
            }
        }

    }
    
    public void propertyModified(OrGuiComponent c, InterfaceFrame ifc) {
    	if(ifc != null) {
    		Set<Integer> idkeys = interfaceObjIds.keySet();
    		int k = 0;
    		for(int idKey: idkeys) {
    			if(interfaceObjIds.get(idKey) == ifc.getUiObject().id) {
    				k = idKey;
    				break;
    			}
    		}               
    		if (!ifc.isModified() && !ifc.isReadOnly()) {
    			ifc.setModified(true);
    			setIconAt(k, icoIfrMod);
    		}
    	}
    }

    @Override
    public void propertyModified(OrGuiComponent c, PropertyNode property) {
        if (isFilter()) {
            propertyModified(c);
            String prop_m = property.getName();
            if ("title".equals(prop_m) || "unionFlr".equals(prop_m) || "attrFlr".equals(prop_m) || "operFlr".equals(prop_m)
                    || "krnObjFlr".equals(prop_m) || "exprFlr".equals(prop_m) || "compAttrFlr".equals(prop_m)) {
                OrFilterNode fn = (OrFilterNode) c;
                FilterNode n = fn.getFilterNode();
                FilterRecord p = filters.get(n.getKrnObj().id);
                OrFilterTree ftree = p.o;
                DefaultTreeModel model = (DefaultTreeModel) ftree.getModel();
                model.nodeChanged((OrFilterNode) c);
            }
        }
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {
    }

    /**
     * Disable item menu.
     */
    private void hideItemMenu() {
        miSaveSrv.setVisible(false);
        miSaveIfr.setVisible(false);
        miSaveFlt.setVisible(false);
        miSaveRpt.setVisible(false);
        miCloseSrv.setVisible(false);
        miCloseIfr.setVisible(false);
        miCloseFlt.setVisible(false);
        miCloseRpt.setVisible(false);
        miCopySrv.setVisible(false);
        miCopyIfr.setVisible(false);
        miCopyFlt.setVisible(false);
        miCopyRpt.setVisible(false);
        miRenameSrv.setVisible(false);
        miRenameIfr.setVisible(false);
        miRenameFlt.setVisible(false);
        miRenameRpt.setVisible(false);
        miDeleteSrv.setVisible(false);
        miDeleteIfr.setVisible(false);
        miDeleteFlt.setVisible(false);
        miDeleteRpt.setVisible(false);
        miHistorySrv.setVisible(false);
        miHistoryIfr.setVisible(false);
        miSaveSrv.setEnabled(false);
        miSaveIfr.setEnabled(false);
        miSaveFlt.setEnabled(false);
        miSaveRpt.setEnabled(false);
        miCloseSrv.setEnabled(false);
        miCloseIfr.setEnabled(false);
        miCloseFlt.setEnabled(false);
        miCloseRpt.setEnabled(false);
        miCopySrv.setEnabled(false);
        miCopyIfr.setEnabled(false);
        miCopyFlt.setEnabled(false);
        miCopyRpt.setEnabled(false);
        miRenameSrv.setEnabled(false);
        miRenameIfr.setEnabled(false);
        miRenameFlt.setEnabled(false);
        miRenameRpt.setEnabled(false);
        miDeleteSrv.setEnabled(false);
        miDeleteIfr.setEnabled(false);
        miDeleteFlt.setEnabled(false);
        miDeleteRpt.setEnabled(false);
        miHistorySrv.setEnabled(false);
        miHistoryIfr.setEnabled(false);
    }

    /**
     * Получить main frame.
     * 
     * @return the serviceFrame
     */
    public MainFrame getMainFrame() {
        return serviceFrame;
    }

    /**
     * Установить main frame.
     * 
     * @param serviceFrame
     *            the serviceFrame to set
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.serviceFrame = mainFrame;
    }

    
    public Icon getIconAt(int index) {
        return index == -1 ? null : super.getIconAt(index);
    }

    /**
     * Active type tabs.
     * 
     * @param type
     *            the type
     */
    public void activeTypeTabs(long type) {
        boolean isEnabledOne = false;
        boolean isEnabled = false;
        int index1 = -1;
        int index2 = -1;
        for (Entry<Integer, KrnObject> ent : objTabs.entrySet()) {
            int indx = ent.getKey();
            KrnObject value = ent.getValue();
            isEnabled = serviceControlMode || type == -1 || type == ent.getValue().classId;
            setEnabledAt(indx, isEnabled);
            isEnabledOne = isEnabledOne || isEnabled;

            if (isEnabledAt(indx)) {
                if (getTitleAt(indx).isEmpty() && titlesTabs.get(indx) != null) {
                    setTitleAt(indx, titlesTabs.get(indx));
                }
            } else if (!getTitleAt(indx).isEmpty()) {
                titlesTabs.put(indx, getTitleAt(indx));
                setTitleAt(indx, "");
            }
            if (!serviceControlMode && type == ent.getValue().classId) {
                setVisible(isEnabledOne);
                index1 = indx;
                if (value.equals(lastSelectSrv) || value.equals(lastSelectIfc) || value.equals(lastSelectFlt)
                        || value.equals(lastSelectRpt)) {
                    index2 = indx;
                }
            }

        }

        if (index2 != -1) {
            setSelectedIndex(index2);
        } else if (index1 != -1) {
            setSelectedIndex(index1);
        }
    }

    /**
     * Active all tabs.
     */
    public void activeAllTabs() {
        activeTypeTabs(-1);
    }

    /**
     * Проверяет, является ли service control mode.
     * 
     * @return true, если service control mode
     */
    public boolean isServiceControlMode() {
        return serviceControlMode;
    }

    /**
     * Установить service control mode.
     * 
     * @param mode
     *            the new service control mode
     */
    public void setServiceControlMode(boolean mode) {
        serviceControlMode = mode;
    }

    public void addReport(ReportPrinter report) {
    }

    /**
     * Проход всех вкладок и изменение иконок отчётов на первоначальные
     */
    public void saveAllReport() {
        for (int i = 0; i < objTabs.size(); i++) {
            if (isReport(i)) {
                setIconAt(i, icoRpt);
            }
        }
    }

    /**
     * Пометить текущую вкладку с отчётом, как модифицированную (смена иконки)
     * 
     * @param isMod
     *            флаг модификации
     */
    public void setModifiedRpt(boolean isMod) {
        if (getTabCount() > 0) {
            setIconAt(getSelectedIndex(), isMod ? icoRptMod : icoRpt);
        }
    }

    public void remove(int index) {
        if (isService(index)) {
            serviceFrame.buttonsEnabled(false);
            serviceFrame.getPropEditor().setEmptyState();
        } else if (isInterface(index)) {
            DesignerFrame frame = DesignerFrame.instance();
            EmptyComponent emptyComponent = new EmptyComponent();
            frame.getInspector().setObject(new GuiComponentItem(emptyComponent, null));
            ((ComponentsTreeModel) frame.getComponentsTree().getModel()).setRoot(emptyComponent);
            frame.setToolButtonsEnabled(false);
        } else if (isFilter(index)) {
            filterPanel.getInspector().setObject(new FilterItem(null, filterPanel));
            filterPanel.getInspector().repaint();
        }

        removeObj(index);
        removeTabAt(index);

    }

    /**
     * Обновление карт индексов в связи с удалением кладки
     */
    private void updateAllMaps(int index) {
        objTabs = updateMap1(objTabs, index);
        nodesTabs = updateMap1(nodesTabs, index);
        titlesTabs = updateMap2(titlesTabs, index);
        docIds = updateMap3(docIds, index);
        interfaceObjIds = updateMap3(interfaceObjIds, index);
        filterIds = updateMap3(filterIds, index);
    }

    private Map<Integer, KrnObject> updateMap1(Map<Integer, KrnObject> objTabs2, int index) {
        Map<Integer, KrnObject> targetMap = new HashMap<Integer, KrnObject>();
        int indx;
        for (Entry<Integer, KrnObject> ent : objTabs2.entrySet()) {
            indx = ent.getKey();
            if (indx != index) {
                if (indx > index) {
                    indx--;
                }
                targetMap.put(indx, ent.getValue());
            }
        }
        return targetMap;
    }

    private Map<Integer, String> updateMap2(Map<Integer, String> objTabs2, int index) {
        Map<Integer, String> targetMap = new HashMap<Integer, String>();
        int indx;
        for (Entry<Integer, String> ent : objTabs2.entrySet()) {
            indx = ent.getKey();
            if (indx != index) {
                if (indx > index) {
                    indx--;
                }
                targetMap.put(indx, ent.getValue());
            }
        }
        return targetMap;
    }

    private Map<Integer, Long> updateMap3(Map<Integer, Long> objTabs2, int index) {
        Map<Integer, Long> targetMap = new HashMap<Integer, Long>();
        int indx;
        for (Entry<Integer, Long> ent : objTabs2.entrySet()) {
            indx = ent.getKey();
            if (indx != index) {
                if (indx > index) {
                    indx--;
                }
                targetMap.put(indx, ent.getValue());
            }
        }
        return targetMap;
    }

    /**
     * Запись фильтра.
     */
    public static class FilterRecord {

        /** Дерево фильтров. */
        public OrFilterTree o;

        /** Родительский узел дерева. */
        public FilterNode parent;

        /** Флаг модификации. */
        public boolean isModified = false;

        /** Флаг "только для чтения" */
        public boolean readOnly = false;

        /**
         * Создание нового экземпляра класса.
         * 
         * @param o
         *            дерево фильтров.
         * @param parent
         *            Родительский узел дерева.
         * @param isModified
         *            Флаг модификации.
         * @param readOnly
         *            Флаг "только для чтения"
         */
        public FilterRecord(OrFilterTree o, FilterNode parent, boolean isModified, boolean readOnly) {
            this.o = o;
            this.parent = parent;
            this.isModified = isModified;
            this.readOnly = readOnly;
        }

        /**
         * Установить флаг модификации
         * 
         * @param mod
         *            новый флаг модификации
         */
        public void setModified(boolean mod) {
            isModified = mod;
        }

        /**
         * Модифицирован?
         * 
         * @return true, если модифицирован
         */
        public boolean isModified() {
            return isModified;
        }

        /**
         * Проверяет, установлен ли флаг "только для чтения".
         * 
         * @return true, если read only
         */
        public boolean isReadOnly() {
            return readOnly;
        }

        /**
         * Установить флаг "только для чтения".
         * 
         * @param readOnly
         *            новое значение флага "только для чтения"
         */
        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }

        /**
         * Получить родительский узел дерева.
         * 
         * @return the parent
         */
        public FilterNode getParent() {
            return parent;
        }
    }

    /**
     * Получить индекс последней открытой вкладки в режиме управления проектом.
     * 
     * @return the lastSelectedTab
     */
    public int getLastSelectedTab() {
        return lastSelectedTab;
    }

}
