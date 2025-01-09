package kz.tamur.guidesigner.serviceControl;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.toolbar.OrToolBar;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.util.ServiceControlNode;

/**
 * Класс реализует панель управления проектным деревом.
 * 
 * @author Sergey Lebedev
 */
public class ServiceControl extends GradientPanel implements PropertyChangeListener {

    /** Панель состояния. */
    private DesignerStatusBar statusPanel = new DesignerStatusBar();

    /** Панель меню. */
    private OrGradientMenuBar menuBar = new OrGradientMenuBar();

    private JPanel layer = new JPanel();
    private JTextField uidView = new JTextField();

    /** Панель с проектным деревом. */
    private JPanel servecesPanel = new JPanel();

    /** Скрол для проектного дерева. */
    private JScrollPane servecesScroller;

    /** Проектное дерево. */
    private ServicesControlTree tree = null;

    /** Меню. */
    private JMenu mainMenu = new JMenu("Меню");

    /** The c layout. */
    private CardLayout cLayout = new CardLayout();

    /** The gb layout. */
    GridBagLayout gbLayout = new GridBagLayout();

    /** The main split. */
    private JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /** The second split. */
    private JSplitPane secondSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /** The property panel. */
    private GradientPanel propertyPanel = new GradientPanel();

    /** The tools panel. */
    private GradientPanel toolsPanel = new GradientPanel();

    /** The content panel. */
    private GradientPanel contentPanel = new GradientPanel();

    /** The content tabs. */
    private ControlTabbedContent contentTabs;

    /** Константа propSrv. */
    static final private String propSrv = "propSrv";

    /** Константа toolsSrv. */
    static final private String toolsSrv = "toolsSrv";

    /** Константа propIfc. */
    static final private String propIfc = "propIfc";

    /** Константа toolsIfc. */
    static final private String toolsIfc = "toolsIfc";

    /** Константа PROP_FLT. */
    static final public String PROP_FLT = "PROP_FLT";

    /** Константа toolsFlt. */
    static final private String toolsFlt = "toolsFlt";

    /** Константа propRpt. */
    static final private String propRpt = "propRpt";

    /** Константа toolsRpt. */
    static final private String toolsRpt = "toolsRpt";

    /** The serv control. */
    private static ServiceControl servControl = null;

    /** The is opaque. */
    private boolean isOpaque = !kz.tamur.rt.MainFrame.TRANSPARENT_DIALOG;

    private OrToolBar jtb = new OrToolBar(JToolBar.VERTICAL, JSplitPane.LEFT, true);
    boolean isDocked = false;
    boolean justLoaded = true;
    Object lastAncestor;
    Dimension jtbDimension;

    /**
     * Instance.
     * 
     * @return service control
     */
    public static ServiceControl instance() {
        return servControl;
    }

    /**
     * Instance.
     * 
     * @param mainFrame
     *            the main frame
     * @return service control
     */
    public static ServiceControl instance(kz.tamur.guidesigner.service.MainFrame mainFrame) {
        if (servControl == null) {
            servControl = new ServiceControl(mainFrame);
            servControl.init();
            // Recycle.instance(); TODO Разработка отложена
        }
        return servControl;
    }

    /**
     * Создание нового экземпляра класса.
     * 
     * @param mainFrame
     *            the main frame
     */
    public ServiceControl(kz.tamur.guidesigner.service.MainFrame mainFrame) {
        super();
        contentTabs = ControlTabbedContent.instance(mainFrame);
    }

    /**
     * Инициализация панели управления проектами.
     */
    private void init() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        setLayout(gbLayout);
        layer.setLayout(gbLayout);
        servecesPanel.setLayout(gbLayout);
        toolsPanel.setLayout(cLayout);
        propertyPanel.setLayout(cLayout);
        contentPanel.setLayout(gbLayout);

        jtb.setWindowResize(true);

        // Получить дерево управления проектом
        tree = kz.tamur.comps.Utils.getServicesControlTree();
        if (tree == null) {
            servecesScroller = new JScrollPane( kz.tamur.comps.Utils.createDescLabel("Для работы с деревом нет необходимых классов!"));
        } else {
            tree.setShowPopupEnabled(true);
            servecesScroller = new JScrollPane(tree);
        }

        layer.add(uidView, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
        layer.add(servecesScroller, new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, BOTH, Constants.INSETS_0, 0, 0));
        jtb.add(layer);

        mainSplit.setLeftComponent(jtb);
        mainSplit.setRightComponent(contentPanel);
        mainSplit.setDividerLocation(500);

        toolsPanel.setMinimumSize(new Dimension(40, 40));
        secondSplit.remove(contentTabs);
        secondSplit.setLeftComponent(propertyPanel);
        secondSplit.setRightComponent(contentTabs);
        secondSplit.setDividerLocation(250);

        uidView.setOpaque(isOpaque);
        uidView.setEditable(false);

        jtbDimension = jtb.getSize();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        contentPanel.add(toolsPanel, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        contentPanel.add(secondSplit, c);
        add(mainSplit, c);

        propertyPanel.setOpaque(isOpaque);
        jtb.setOpaque(isOpaque);
        layer.setOpaque(isOpaque);
        toolsPanel.setOpaque(isOpaque);
        contentPanel.setOpaque(isOpaque);
        mainSplit.setOpaque(isOpaque);
        secondSplit.setOpaque(isOpaque);
        servecesPanel.setOpaque(isOpaque);
        servecesScroller.setOpaque(isOpaque);
        servecesScroller.getViewport().setOpaque(isOpaque);
        setOpaque(isOpaque);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("ancestor")) {
            if (evt.getNewValue() == null) {
                lastAncestor = evt.getOldValue();
            }
            if (evt.getOldValue() == null) {
                if (evt.getNewValue() != lastAncestor) {
                    isDocked = !isDocked;
                    if (justLoaded)
                        justLoaded = !justLoaded;
                    else {
                        getParent().setVisible(isDocked);

                    }
                }
            }
        }
    }

    /**
     * Получить панель статусов.
     * 
     * @return Панель статусов
     */
    public DesignerStatusBar getStatusBar() {
        return statusPanel;
    }

    /**
     * Получить панель меню.
     * 
     * @return Панель меню
     */
    public OrGradientMenuBar getMenu() {
        return menuBar;
    }

    /**
     * Инициализация панели меню.
     */
    public void initMenu() {
        menuBar.add(mainMenu);
    }

    /**
     * Получить проектное дерево.
     * 
     * @return дерево
     */
    public ServicesControlTree getTree() {
        return tree;
    }

    /**
     * Получить главную панель вкладок.
     * 
     * @return contentTabs
     */
    public ControlTabbedContent getContentTabs() {
        return contentTabs;
    }

    /**
     * Отобразить процесс.
     * 
     * @param selNode
     *            узел дерева, из которого берётся объект для отображения.
     */
    public void showService(ServiceControlNode selNode) {
        kz.tamur.guidesigner.service.MainFrame sf = null;
        if (selNode != null) {
            Or3Frame.instance().quickStartService(false, false);
        }
        sf = Or3Frame.instance().getServiceFrame();
        if (toolsPanel.getComponentZOrder(sf.getToolBarPanel()) == -1) {
            toolsPanel.add(sf.getToolBarPanel(), toolsSrv);
        }
        if (propertyPanel.getComponentZOrder(sf.getPropEditor()) == -1) {
            propertyPanel.removeAll();
            propertyPanel.add(sf.getPropEditor(), propSrv);
        }
        cLayout.show(toolsPanel, toolsSrv);

        revalidate();

        if (selNode != null) {
            Or3Frame.instance().getServiceFrame().load(selNode.getValue(),selNode.getKrnObj());
        }

    }

    /**
     * Отобразить интерфейс.
     * 
     * @param selNode
     *            узел дерева, из которого берётся объект для отоюражения.
     */
    public void showInterface(ServiceControlNode selNode) {
        DesignerFrame df = null;
        if (selNode != null) {
            Or3Frame.instance().quickStartIfc(false, false);
        }
        df = Or3Frame.instance().getDesignerFrame();
        if (toolsPanel.getComponentZOrder(df.getBasicPanel()) == -1) {
            toolsPanel.add(df.getBasicPanel(), toolsIfc);
        }
        if (propertyPanel.getComponentZOrder(df.getSecondSplitPane()) == -1) {
            propertyPanel.removeAll();
            propertyPanel.add(df.getSecondSplitPane(), propIfc);
        }
        cLayout.show(toolsPanel, toolsIfc);
        revalidate();
        if (selNode != null) {
            Or3Frame.instance().getDesignerFrame().load(selNode.getValue(),selNode.getKrnObj());
        }
    }

    /**
     * Отобразить фильтр.
     * 
     * @param selNode
     *            узел дерева, из которого берётся объект для отоюражения.
     */
    public void showFilter(ServiceControlNode selNode) {
        if (selNode != null) {
            Or3Frame.instance().quickStartFilters(false, false);
        } 
        FiltersPanel ff = Or3Frame.instance().getFiltersFrame();
        if (toolsPanel.getComponentZOrder(ff.getToolBar()) == -1) {
            toolsPanel.add(ff.getToolBar(), toolsFlt);
        }
        if (propertyPanel.getComponentZOrder(ff.getInspector()) == -1) {
            propertyPanel.removeAll();
            propertyPanel.add(ff.getInspector(), PROP_FLT);
        }
        cLayout.show(toolsPanel, toolsFlt);
        revalidate();
        if (selNode != null) {
            Or3Frame.instance().getFiltersFrame().load(selNode.getValue(),selNode.getKrnObj());
        }
    }

    /**
     * Отобразить отчёт.
     * 
     * @param selNode
     *            узел дерева, из которого берётся объект для отображения.
     */
    public void showReport(ServiceControlNode selNode) {
        ReportPanel rf = null;
        if (selNode != null) {
            Or3Frame.instance().quickStartReports(false);
        }
        rf = Or3Frame.instance().getReportFrame();

        if (toolsPanel.getComponentZOrder(rf.getToolsPanel()) == -1) {
            toolsPanel.add(rf.getToolsPanel(), toolsRpt);
        }
        if (propertyPanel.getComponentZOrder(rf.getInspector()) == -1) {
            propertyPanel.removeAll();
            propertyPanel.add(rf.getInspector(), propRpt);
        }
        cLayout.show(toolsPanel, toolsRpt);
        if (selNode != null) {
            rf.load(selNode.getValue());
            contentTabs.addReport(rf,selNode.getKrnObj());
        }

        revalidate();
    }

    /**
     * Пересобрать интерфейс.
     */
    public void rebuildPanels() {
        contentTabs.setServiceControlMode(true);
        contentTabs.activeAllTabs();
        secondSplit.remove(contentTabs);
        secondSplit.setRightComponent(contentTabs);
    }

    public void showUUID(String uid) {
        uidView.setText(uid);
    }

    /**
     * @return the propertyPanel
     */
    public GradientPanel getPropertyPanel() {
        return propertyPanel;
    }
}
