package kz.tamur.guidesigner.serviceControl;

import static com.cifs.or2.client.Kernel.SC_CONTROL_FOLDER;
import static com.cifs.or2.client.Kernel.SC_FILTER;
import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF;
import static com.cifs.or2.client.Kernel.SC_REPORT_PRINTER;
import static com.cifs.or2.client.Kernel.SC_UI;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static kz.tamur.or3.util.SystemEvent.EVENT_COPY_INTERFACE;
import static kz.tamur.or3.util.SystemEvent.EVENT_COPY_PROCESS;
import static kz.tamur.or3.util.SystemEvent.EVENT_COPY_REPORT;
import static kz.tamur.or3.util.SystemEvent.EVENT_CREATE_FILTER;
import static kz.tamur.or3.util.SystemEvent.EVENT_COPY_FILTER;
import static kz.tamur.or3.util.SystemEvent.EVENT_CREATE_INTERFACE;
import static kz.tamur.or3.util.SystemEvent.EVENT_CREATE_PROCESS;
import static kz.tamur.or3.util.SystemEvent.EVENT_CREATE_REPORT;
import static kz.tamur.or3.util.SystemEvent.EVENT_RENAME_FILTER;
import static kz.tamur.or3.util.SystemEvent.EVENT_RENAME_INTERFACE;
import static kz.tamur.or3.util.SystemEvent.EVENT_RENAME_PROCESS;
import static kz.tamur.or3.util.SystemEvent.EVENT_RENAME_REPORT;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.createMenu;
import static kz.tamur.rt.Utils.getImageIconFull;
import static kz.tamur.util.CreateElementPanel.COPY_TYPE;
import static kz.tamur.util.CreateElementPanel.CREATE_ELEMENT_TYPE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Factories;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.MenuScroller;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.EmptyFrame;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportNode;
import kz.tamur.guidesigner.reports.ReportNodeItem;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.Document;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.ServiceActionsConteiner;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.service.cmd.CmdSaveProcess;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.AbstractDesignerTreeCellRenderer;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DesignerTreeModel;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.FrameTemplate;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.ServiceControlNode;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.util.CursorToolkit;

/**
 * Класс управления проектным деревом.
 * 
 * @author Sergey Lebedev
 * 
 */
public class ServicesControlTree extends DesignerTree implements ActionListener, MouseListener, KeyListener {

    static long classFldID = SC_CONTROL_FOLDER.id;
    /** The search string. */
    private String searchString = "";

    /** The frm. */
    private MainFrame frm;

    /** The lang id. */
    private long langId;

    /** Флаг разрешения редактирования процесса. */
    private boolean isResolveEditS = false;

    /** Флаг разрешения удаления процесса. */
    private boolean isResolveDeleteS = false;

    /** Флаг разрешения создания процесса. */
    private boolean isResolveCreateS = false;

    /** Флаг разрешения редактирования интерфейса. */
    private boolean isResolveEditU = false;

    /** Флаг разрешения удаления интерфейса. */
    private boolean isResolveDeleteU = false;

    /** Флаг разрешения создания интерфейса. */
    private boolean isResolveCreateU = false;

    /** Флаг разрешения редактирования фильтра. */
    private boolean isResolveEditF = false;

    /** Флаг разрешения удаления фильтра. */
    private boolean isResolveDeleteF = false;

    /** Флаг разрешения создания фильтра. */
    private boolean isResolveCreateF = false;

    /** Флаг разрешения редактирования отчёта. */
    private boolean isResolveEditR = false;

    /** Флаг разрешения удаления отчёта. */
    private boolean isResolveDeleteR = false;

    /** Флаг разрешения создания отчёта. */
    private boolean isResolveCreateR = false;

    /* Пункты контекстного меню. */
    private JMenuItem miFind = createMenuItem("Найти");
    protected JMenuItem miFindNext = createMenuItem("Найти далее...");
    /** The mi create service. */
    private JMenuItem miCreateService = createMenuItem("Создать процесс", "serviceCreate.png");

    /** The mi add service. */
    private JMenuItem miAddService = createMenuItem("Добавить процесс", "serviceAdd.png");

    /** The mi add node sub elements. */
    private JMenuItem miServiceElements = createMenuItem("Добавить элементы узла", "FnClasses2.png");

    /** The mi add node sub elements. */
    private JMenu mElementParents = createMenu("Родители элемента");

    /** The mi create interface. */
    private JMenuItem miCreateInterface = createMenuItem("Создать интерфейс", "interfaceCreate.png");

    /** The mi add interface. */
    private JMenuItem miAddInterface = createMenuItem("Добавить интерфейс", "interfaceAdd.png");

    /** The mi create filter. */
    private JMenuItem miCreateFilter = createMenuItem("Создать фильтр", "filterCreate.png");

    /** The mi add filter. */
    private JMenuItem miAddFilter = createMenuItem("Добавить фильт", "filterAdd.png");

    /** The mi create report. */
    private JMenuItem miCreateReport = createMenuItem("Создать отчёт", "reportCreate.png");

    /** The mi add report. */
    private JMenuItem miAddReport = createMenuItem("Добавить отчёт", "reportAdd.png");

    /** The mi create folder. */
    private JMenuItem miCreateFolder = createMenuItem("Создать папку", "folderCreate.png");

    /** The mi view. */
    private JMenuItem miView = createMenuItem("Отобразить", "viewEditor.png");

    /** The mi rename. */
    private JMenuItem miRename = createMenuItem("Переименовать", "rename.png");

    /** The mi copy. */
    private JMenuItem miCopy = createMenuItem("Копировать", "copy.png");

    /** The mi cut. */
    private JMenuItem miCut = createMenuItem("Вырезать", "cut.png");

    /** The mi paste. */
    private JMenuItem miPaste = createMenuItem("Вставить", "paste.png");

    private JMenuItem miCancel = createMenuItem("Отменить", "undo.png");

    /** The mi delete only selected node. */
    private JMenuItem miDeleteOnlySelectedNode = createMenuItem("Удалить выбранный", "delete.png");

    /** The mi delete node from tree. */
    private JMenuItem miDeleteNodeFromTree = createMenuItem("Удалить из дерева", "destroyNode.png");

    private JMenuItem miReplaceElement = createMenuItem("Заменить", "replace.png");

    /** The mi copy with changeover. */
    private JMenuItem miChangeoverCopy = createMenuItem("Создать копию с замещением", "copy.png");

    private JMenuItem miRole = createMenuItem("Роль", "groupNode.png");

    /** The krn. */
    private Kernel krn;

    /** Единственный экземпляр класса Or3Frame. */
    private Or3Frame or3Frame;

    /** Дерево процессов. */
    private ServicesTree treeSrv;

    /** Дерево интерфейсов. */
    private InterfaceTree treeInf;

    /** Дерево фильтров. */
    private FiltersTree treeFlt;

    /** Дерево отчётов. */
    private ReportTree treeRpt;

    /** Флаг чтобы узнать что открытие инициировано деревом */
    private boolean selfChanged = false;

    private List<KrnObject> listForAdd = new ArrayList<KrnObject>();
    /**
     * Создание нового services control tree.
     * 
     * @param root
     *            the root
     * @param frm
     *            the frm
     */
    public ServicesControlTree(ServiceControlNode root, MainFrame frm) {
        super(root);
        krn = Kernel.instance();
        User user = krn.getUser();
        or3Frame = Or3Frame.instance();
        langId = Utils.getInterfaceLangId();

        treeSrv = kz.tamur.comps.Utils.getServicesTree();
        treeInf = kz.tamur.comps.Utils.getInterfaceTree();
        treeFlt = kz.tamur.comps.Utils.getFiltersTree();

        treeRpt = kz.tamur.comps.Utils.getReportTree(null);

        // определить права пользователя
        isResolveEditS = user.hasRight(Or3RightsNode.PROCESS_EDIT_RIGHT);
        isResolveDeleteS = user.hasRight(Or3RightsNode.PROCESS_DELETE_RIGHT);
        isResolveCreateS = user.hasRight(Or3RightsNode.PROCESS_CREATE_RIGHT);

        isResolveEditU = user.hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT);
        isResolveDeleteU = user.hasRight(Or3RightsNode.INTERFACE_DELETE_RIGHT);
        isResolveCreateU = user.hasRight(Or3RightsNode.INTERFACE_CREATE_RIGHT);

        isResolveEditF = user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT);
        isResolveDeleteF = user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT);
        isResolveCreateF = user.hasRight(Or3RightsNode.FILTERS_CREATE_RIGHT);

        isResolveEditR = user.hasRight(Or3RightsNode.REPORTS_EDIT_RIGHT);
        isResolveDeleteR = user.hasRight(Or3RightsNode.REPORTS_DELETE_RIGHT);
        isResolveCreateR = user.hasRight(Or3RightsNode.REPORTS_CREATE_RIGHT);

        this.root = root;
        this.frm = frm;
        model = new ServiceControlTreeModel(root);
        pm = new JPopupMenu();
        pm.add(miFind);
        pm.add(miFindNext);
        pm.addSeparator();
        pm.add(miView);
        pm.addSeparator();
        pm.add(miCreateService);
        pm.add(miAddService);
        pm.addSeparator();
        pm.add(miCreateInterface);
        pm.add(miAddInterface);
        pm.addSeparator();
        pm.add(miCreateFilter);
        pm.add(miAddFilter);
        pm.addSeparator();
        pm.add(miCreateReport);
        pm.add(miAddReport);
        pm.addSeparator();
        pm.add(miServiceElements); // элементы узла
        pm.add(mElementParents); // Родители узла в этом дереве
        pm.addSeparator();
        pm.add(miCreateFolder);
        pm.addSeparator();
        pm.add(miRename);
        pm.add(miCopy);
        pm.add(miCut);
        pm.add(miPaste);
        pm.add(miReplaceElement);
        pm.add(miCancel);
        pm.addSeparator();
        pm.add(miChangeoverCopy);
        pm.addSeparator();
        pm.add(miRole);
        pm.addSeparator();
        pm.add(miDeleteOnlySelectedNode);
        pm.add(miDeleteNodeFromTree);

        miCreateService.addActionListener(this);
        miAddService.addActionListener(this);
        miServiceElements.addActionListener(this); // элементы процеса

        miCreateInterface.addActionListener(this);
        miAddInterface.addActionListener(this);
        miCreateFilter.addActionListener(this);
        miAddFilter.addActionListener(this);
        miCreateReport.addActionListener(this);
        miAddReport.addActionListener(this);
        miCreateFolder.addActionListener(this);
        miView.addActionListener(this);

        miRename.addActionListener(this);
        miCopy.addActionListener(this);
        miCut.addActionListener(this);
        miReplaceElement.addActionListener(this);
        miDeleteOnlySelectedNode.addActionListener(this);
        miDeleteNodeFromTree.addActionListener(this);
        miPaste.addActionListener(this);
        miCancel.addActionListener(this);
        miChangeoverCopy.addActionListener(this);
        miRole.addActionListener(this);
        miFind.addActionListener(this);
        miFindNext.addActionListener(this);

        miView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        miDeleteNodeFromTree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        miDeleteOnlySelectedNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK));
        miRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        miCancel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

        miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        miCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        miReplaceElement.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

        // в зависимости от прав, отобразить тот, или иной функционал
        miCreateService.setEnabled(isResolveCreateS);
        miCreateInterface.setEnabled(isResolveCreateU);
        miCreateFilter.setEnabled(isResolveCreateF);
        miCreateReport.setEnabled(isResolveCreateR);

        MenuScroller.setScrollerFor(mElementParents);

        setModel(model);

        setCellRenderer(new CellRenderer());
        if (isOpaque) {
            setBackground(kz.tamur.rt.Utils.getLightSysColor());
        }
        setOpaque(isOpaque);
        // убрать раскрытие узла по двойному клику
        setToggleClickCount(0);
        setCopyNode(null);
        
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (getSelectedNode() != null && ((ServiceControlNode) getSelectedNode()).getValue() != null) {
                    ServiceControl.instance().showUUID(((ServiceControlNode) getSelectedNode()).getValue().uid);
                } else {
                    ServiceControl.instance().showUUID("");
                }

                if (getSelectedNode() != null) {
                    or3Frame.setState(getSelectedNode().toString());
                } else {
                    or3Frame.setState(0);
                }
                
            }
        });
        
    }

    /**
     * Установить frame.
     * 
     * @param frm
     *            the new frame
     */
    public void setFrame(MainFrame frm) {
        if (this.frm == null) {
            this.frm = frm;
        }
    }

    
    protected void defaultDeleteOperations() {
        AbstractDesignerTreeNode node = getSelectedNode();
        frm.removeCurrentTab(node.getKrnObj().id);
    }

    

    /**
     * The Class ServiceControlTreeModel.
     */
    public class ServiceControlTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        /** The is move. */
        private boolean isMove;

        /* */
        private String newChangeoverUID;

        /**
         * Создание нового service control tree model.
         * 
         * @param root
         *            the root
         */
        public ServiceControlTreeModel(TreeNode root) {
            super(root);
        }

        
        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            return null;
        }

        
        public void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
            super.fireTreeStructureChanged(source, path, childIndices, children);
        }

        
        public void renameNode() {
            ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
            String oldName = selNode.toString();
            String newName;
            KrnObject object = selNode.getValue();
            KrnObject node = selNode.getKrnObj();
            String tObject;

            if (selNode.isService()) {
                tObject = EVENT_RENAME_PROCESS.getName();
            } else if (selNode.isInterface()) {
                tObject = EVENT_RENAME_INTERFACE.getName();
            } else if (selNode.isFilter()) {
                tObject = EVENT_RENAME_FILTER.getName();
            } else if (selNode.isReport()) {
                tObject = EVENT_RENAME_REPORT.getName();
            } else if (selNode.isFolder()) {
                tObject = "Переименование директории";
            } else {
                tObject = "Переименование элемента";
            }

            CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, oldName, langId);
            DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), tObject, cp);

            do {
                // показать диалог
                dlg.show();
                // обработать результат диалога
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    newName = cp.getElementName();
                    if (!oldName.equals(newName)) {
                        if (selNode.isService()) {
                            // получить дерево процессов
                            try {
                                // изменить объект в базе
                                krn.setString(object.id, object.classId, "title", 0, langId, newName, 0);
                                // Получить корень дерева процессов
                                ServiceNode rootS = treeSrv.getRoot();
                                // получить узел с переименовываемым объектом
                                ServiceNode servNode = (ServiceNode) rootS.find(object).getLastPathComponent();
                                // если был открыт дизайнер фреймов, то переименовать вкладку с процессом
                                if (or3Frame.getServiceFrame() != null) {
                                    // если узел открыт в дизайнере процессов
                                    ControlTabbedContent tc = or3Frame.getServiceFrame().getTabbedContent();
                                    if (selNode.isOpened() || tc.isExistSrv(object.id)) {
                                        tc.renameTab(newName);
                                    }
                                }
                                // переименовать узел в дереве процессов
                                servNode.rename(newName);
                                treeSrv.renameProcess(servNode);
                                // записать в лог действие
                                krn.writeLogRecord(EVENT_RENAME_PROCESS, "'" + oldName + "' в '" + newName + "'");
                            } catch (KrnException e) {
                                System.out.println("Ошибка переименования процесса!");
                                e.printStackTrace();
                            }
                        } else if (selNode.isInterface()) {
                            try {
                                KrnAttribute attr = krn.getAttributeByName(SC_UI, "title");
                                ControlTabbedContent tc = ControlTabbedContent.instance();
                                if (tc.isExistIfr(object.id)) {
                                    tc.setTitleAt(tc.getSelectedIndex(), newName);
                                }
                                // изменить объект в базе
                                krn.setString(object.id, attr.id, 0, langId, newName, 0);
                                // получить дерево интерфейсов
                                // Получить корень дерева интерфейсов
                                InterfaceNode rootI = (InterfaceNode) treeInf.getRoot();
                                // получить узел с переименовываемым объектом
                                InterfaceNode source = (InterfaceNode) rootI.find(object).getLastPathComponent();
                                // переименовать узел в дереве процессов
                                source.rename(newName);
                                treeInf.renameInterface(source);
                                // записать в лог действие
                                krn.writeLogRecord(EVENT_RENAME_INTERFACE, "'" + oldName + "' в '" + newName + "'");
                            } catch (Exception e) {
                                System.out.println("Ошибка переименования интерфейса!");
                                e.printStackTrace();
                            }
                        } else if (selNode.isFilter()) {
                            try {
                                KrnAttribute attr = krn.getAttributeByName(SC_FILTER, "title");
                                // изменить объект в базе
                                krn.setString(object.id, attr.id, 0, langId, newName, 0);
                                // получить дерево фильтров
                                // Получить корень дерева фильтров
                                FilterNode rootF = treeFlt.getRoot();
                                // получить узел с переименовываемым объектом
                                FilterNode source = (FilterNode) rootF.find(object).getLastPathComponent();
                                // переименовать узел в дереве фильтров
                                treeFlt.renameFilter(source, newName);
                                // записать в лог действие
                                krn.writeLogRecord(EVENT_RENAME_FILTER, "'" + oldName + "' в '" + newName + "'");
                            } catch (Exception e) {
                                System.out.println("Ошибка переименования фильтра!");
                                e.printStackTrace();
                            }
                        } else if (selNode.isReport()) {
                            try {
                                KrnAttribute attr = krn.getAttributeByName(SC_REPORT_PRINTER, "title");
                                krn.setString(object.id, attr.id, 0, langId, newName, 0);
                                byte[] conf = krn.getBlob(object, "config", 0, langId, 0);
                                String xml;
                                if (conf == null || conf.length == 0) {
                                    xml = "<Component class=\"ReportPrinter\"><title><L1>" + newName
                                            + "</L1></title><groupType>false</groupType><editorType>0</editorType><titleKaz><L1>"
                                            + newName + "</L1></titleKaz></Component>";
                                } else {
                                    xml = new String(conf);
                                    xml.replaceFirst("<title><L1>.*?</L1></title>", newName);
                                }
                                krn.setBlob(object.id, SC_REPORT_PRINTER.id, "config", 0, xml.getBytes(), 0, 0);
                                ReportPanel rf = or3Frame.getReportFrame();
                                if (rf != null) {
                                    ReportTree tree = rf.getTree();
                                    // получить узел с переименовываемым объектом
                                    ReportNode source = (ReportNode) tree.getRoot().find(object).getLastPathComponent();
                                    source.setTitle(newName);
                                    tree.renameReport(source);
                                    // если переименовываемый объект открыт в инспекторе свойств, то нужно его обновить
                                    if (tree.getSelectedNode().getKrnObj().equals(object)) {
                                        rf.getInspector().setObject(new ReportNodeItem(source, rf));
                                        OrGuiComponent cm = source.getOrGuiComponent();
                                        if (cm != null) {
                                            cm.setPropertyValue(new PropertyValue(source.toString(), cm.getProperties().getChild(
                                                    "title")));
                                        }
                                    }
                                }
                                krn.writeLogRecord(EVENT_RENAME_REPORT, "'" + oldName + "' в '" + newName + "'");
                            } catch (KrnException e) {
                                System.out.println("Ошибка переименования отчёта!");
                                e.printStackTrace();
                            }
                        } else if (selNode.isFolder()) {
                            renameServiceControlNode2(selNode, newName);
                        }

                        if (!selNode.isFolder()) {
                            // Обновить узлы в дереве управления
                            List<ServiceControlNode> sns = findAllChild(selNode.getValue());
                            if (sns != null) {
                                for (ServiceControlNode sn : sns) {
                                    renameServiceControlNode2(sn, newName);
                                }
                            }
                        }
                    }
                } else {
                    // выход из диалога переименования элемента
                    break;
                }
            } while (oldName.equals(newName));
        }

        public void setRoleNode() {
            ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
            KrnObject node = selNode.getKrnObj();
            if (selNode.getType() == 1) {
                selNode.setType(0);
            } else {
                selNode.setType(1);
            }
            repaint();

            try {
                if (krn.getAttributeByName(SC_CONTROL_FOLDER, "type") != null) {
                    krn.setLong(node.id, classFldID, "type", 0, selNode.getType(), 0);
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }

        }

        /**
         * Rename.
         * 
         * @param node
         *            the node
         */
        public void rename(ServiceControlNode node) {
            if (node != null) {
                TreeNode[] tpath = getPathToRoot(node);
                fireTreeNodesChanged(this, tpath, null, null);
            }
        }

        
        public int getChildCount(Object parent) {
            return ((ServiceControlNode) parent).getChildCount();
        }

        
        public Object getChild(Object parent, int index) {
            return ((ServiceControlNode) parent).getChildAt(index);
        }

        /**
         * Создать процесс.
         * 
         * @param oldObj
         *            the old obj
         * @throws KrnException
         *             the krn exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private void createService(KrnObject oldObj) throws KrnException, IOException {
            final boolean isCopy = oldObj != null;
            String oldName = null;
            if (copyNode != null) {
                oldName = copyNode.toString();
            }
            String oldTitle = isCopy ? "Копия " + oldName : "";

            CreateElementPanel p = new CreateElementPanel(isCopy ? COPY_TYPE : CREATE_ELEMENT_TYPE, oldTitle, langId);
            DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), isCopy ? "Копирование Процесса"
                    : "Создание Процесса", p);
            dlg.show();
            if (dlg.isOK()) {
                final KrnClass clsR = krn.getClassByName("ProcessDefRoot");
                final KrnObject newObj = krn.createObject(SC_PROCESS_DEF, 0);
                // создать новую директорию
                KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                byte[] data = null;
                byte[] msg_rus = null;
                byte[] msg_kaz = null;
                // инициализация serviceFrame
                or3Frame.quickStartService(false, false);
                MainFrame frm = or3Frame.getServiceFrame();
                String newName = p.getElementName().isEmpty() ? oldTitle : p.getElementName();
                ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
                KrnObject selObj = selNode.getKrnObj();
                KrnObject root = krn.getClassObjects(clsR, 0)[0];
                krn.setString(newObj.id, SC_PROCESS_DEF.id, "title", 0, langId, newName, 0);
                krn.setString(newFld.id, newFld.classId, "title", 0, langId, newName, 0);
                krn.setObject(newFld.id, newFld.classId, "value", 0, newObj.id, 0, false);
                krn.setObject(newFld.id, newFld.classId, "parent", 0, selObj.id, 0, false);
                if (isCopy) {
                    data = krn.getBlob(oldObj, "diagram", 0, 0, 0);
                    msg_rus = krn.getBlob(oldObj, "message", 0, frm.getRusLang(), 0);
                    msg_kaz = krn.getBlob(oldObj, "message", 0, frm.getKazLang(), 0);
                    krn.writeLogRecord(EVENT_COPY_PROCESS, "'" + oldName + "' в '" + newName + "'");
                } else {
                    krn.writeLogRecord(EVENT_CREATE_PROCESS, newName);
                }

                int idx = selNode.getChildCount();
                ServiceControlNode node = new ServiceControlNode(newFld, newObj, newName, -1, langId, idx);
                insertNodeInto(node, selNode, selNode.getChildCount());

                // Добавить созданный узел в дерево процессов

                ServiceNode rootS = (ServiceNode) ((DefaultTreeModel) treeSrv.getModel()).getRoot();
                ServiceNode nodeS = new ServiceNode(newObj, newName, langId, rootS.getChildCount(), newName, newName, 0, false,
                        null, null, null, false, null);
                ((DefaultTreeModel) treeSrv.getModel()).insertNodeInto(nodeS, rootS, rootS.getChildCount());

                long[] vals = krn.getLongs(root, "children", 0);
                krn.setObject(root.id, root.classId, "children", vals.length, newObj.id, 0, false);
                kz.tamur.rt.Utils.createFilterFolder(newObj, newName, langId);

                ServiceModel model = new ServiceModel(true, newObj, langId);

                model.setMf(frm);
                Document doc = new Document(newObj, newName, model);
                if (isCopy) {
                    if (data.length > 0) {
                        InputStream is_msg_rus = msg_rus.length > 0 ? new ByteArrayInputStream(msg_rus) : null;
                        model.loadLangs(is_msg_rus, frm.getRusLang());
                        InputStream is_msg_kaz = msg_kaz.length > 0 ? new ByteArrayInputStream(msg_kaz) : null;
                        model.loadLangs(is_msg_kaz, frm.getKazLang());
                        InputStream is = new ByteArrayInputStream(data);
                        model.load(is, doc.getGraph());
                        is.close();
                        if (is_msg_rus != null) {
                            is_msg_rus.close();
                        }
                        if (is_msg_kaz != null) {
                            is_msg_kaz.close();
                        }
                    }
                    CmdSaveProcess saveCmd = new CmdSaveProcess("Save", frm, CmdSaveProcess.SAVE_CURRENT);
                    saveCmd.save(doc);
                }
                frm.setDocument(doc, node.getKrnObj());
                frm.getServicesTree().removeTreeSelectionListener(p);
                ServiceActionsConteiner.instance(node);
            }
        }

        private int addService() {
            return addService(null);
        }

        /**
         * Добавить процесс.
         * 
         * @return
         */
        private int addService(ServiceControlNode selNode) {
            DesignerDialog dlg;
            OpenElementPanel op = new OpenElementPanel(treeSrv);
            dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Выбор процесса", op);
            op.setSearchUIDPanel(true);
            dlg.setVisible(true);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                ServiceNode proc = (ServiceNode) op.getTree().getSelectedNode();
                final KrnObject procUI = op.getNodeObj(proc);
                long serviceId = procUI.id;
                try {
                    if (selNode == null) {
                        selNode = (ServiceControlNode) getSelectedNode();
                    }
                    KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                    krn.setString(newFld.id, classFldID, "title", 0, langId, proc.getTitle(), 0);
                    krn.setObject(newFld.id, classFldID, "value", 0, serviceId, 0, false);
                    krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);
                    int idx = selNode.getChildCount();
                    ServiceControlNode node = new ServiceControlNode(newFld, procUI, proc.getTitle(), -1, langId, idx);
                    insertNodeInto(node, selNode, selNode.getChildCount());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            return dlg.getResult();
        }

        /**
         * Создать директорию.
         * 
         * @param oldObj
         *            the old obj
         */
        private void createFolder(KrnObject oldObj) {
            final boolean isCopy = oldObj != null;
            try {
                CreateElementPanel p = new CreateElementPanel(CreateElementPanel.CREATE_FOLDER_TYPE_2, "NewFolder", langId);
                DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Создание папки", p);
                dlg.show();
                if (dlg.isOK()) {
                    ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
                    KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                    krn.setString(newFld.id, classFldID, "title", 0, langId, p.getElementName(), 0);
                    krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);
                    int type = 0;
                    if (krn.getAttributeByName(SC_CONTROL_FOLDER, "type") != null) {
                        if (p.isRole()) {
                            type = 1;
                        }
                        krn.setLong(newFld.id, classFldID, "type", 0, type, 0);

                    }
                    int idx = selNode.getChildCount();
                    ServiceControlNode node = new ServiceControlNode(newFld, null, p.getElementName(), type, langId, idx);

                    insertNodeInto(node, selNode, selNode.getChildCount());
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        /**
         * Создать интерфейс.
         * 
         * @param oldObj
         *            the old obj
         * @throws KrnException
         *             the krn exception
         */
        private void createInterface(KrnObject oldObj) throws KrnException {
            final boolean isCopy = oldObj != null;
            String oldName = null;
            if (copyNode != null) {
                oldName = copyNode.toString();
            }
            String oldTitle = isCopy ? "Копия " + oldName : "";

            CreateElementPanel p = new CreateElementPanel(isCopy ? COPY_TYPE : CREATE_ELEMENT_TYPE, oldTitle, langId);
            DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), isCopy ? "Копирование Интерфейса"
                    : "Создание Интерфейса", p);
            dlg.show();
            if (dlg.isOK()) {
                String newName = p.getElementName().isEmpty() ? oldTitle : p.getElementName();
                final KrnClass clsR = krn.getClassByName("UIRoot");
                final KrnObject obj = krn.createObject(SC_UI, 0);
                ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
                KrnObject root = krn.getClassObjects(clsR, 0)[0];

                krn.setString(obj.id, SC_UI.id, "title", 0, langId, newName, 0);

                KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                krn.setString(newFld.id, classFldID, "title", 0, langId, newName, 0);
                krn.setObject(newFld.id, classFldID, "value", 0, obj.id, 0, false);
                krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);

                // Копирование содержимого интерфейса
                if (isCopy) {
                    krn.setAutoCommit(false);
                    try {
                        if (!copyNode.isCutProcess()) {

                            byte[] data = krn.getBlob(getCopyNode().getValue(), "config", 0, 0, 0);
                            krn.setBlob(obj.id, obj.classId, "config", 0, data, 0, 0);

                            List<LangItem> langs = LangItem.getAll();
                            MapMap stringsMap = new MapMap();
                            for (int i = 0; i < langs.size(); i++) {
                                LangItem item = (LangItem) langs.get(i);
                                Map m = new HashMap();
                                stringsMap.put(new Long(item.obj.id), m);
                                byte[] strings = krn.getBlob(getCopyNode().getValue(), "strings", 0, item.obj.id, 0);
                                if (strings.length > 0) {
                                    ByteArrayInputStream is = new ByteArrayInputStream(strings);
                                    SAXBuilder b = new SAXBuilder();
                                    Element e = b.build(is).getRootElement();
                                    List chs = e.getChildren();
                                    for (int j = 0; j < chs.size(); j++) {
                                        Element ch = (Element) chs.get(j);
                                        String uid = ch.getAttributeValue("uid");
                                        if (ch.getContentSize() > 0) {
                                            for (int k = 0; k < ch.getContentSize(); k++) {
                                                if (ch.getContent(k) instanceof CDATA) {
                                                    String s = ((CDATA) ch.getContent(k)).getText();
                                                    byte[] value = s.getBytes();
                                                    m.put(uid, value);
                                                } else if (ch.getContent(k) instanceof Text) {
                                                    String value = ch.getText();
                                                    m.put(uid, value);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            XMLOutputter out = new XMLOutputter();
                            out.getFormat().setEncoding("UTF-8");
                            Iterator it = stringsMap.keySet().iterator();
                            while (it.hasNext()) {
                                Long lid = (Long) it.next();
                                Element e = new Element("Messages");
                                Map msgs = null;
                                msgs = stringsMap.get(lid);
                                for (Iterator uidIt = msgs.keySet().iterator(); uidIt.hasNext();) {
                                    String uid = (String) uidIt.next();
                                    Element ch = new Element("Msg");
                                    ch.setAttribute("uid", uid);
                                    Object msg = msgs.get(uid);
                                    if (msg instanceof String) {
                                        ch.setText((String) msg);
                                    } else if (msg instanceof byte[]) {
                                        CDATA cdata = new CDATA(new String((byte[]) msg));
                                        ch.addContent(cdata);
                                    }
                                    e.addContent(ch);
                                }
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                out.output(e, os);
                                os.close();
                                krn.setBlob(obj.id, obj.classId, "strings", 0, os.toByteArray(), lid.longValue(), 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);

                    krn.writeLogRecord(EVENT_COPY_INTERFACE, "'" + copyNode.toString() + "' в '" + newName + "'");
                } else {
                    krn.writeLogRecord(EVENT_CREATE_INTERFACE, newName);
                }

                int idx = selNode.getChildCount();
                ServiceControlNode node = new ServiceControlNode(newFld, obj, newName, -1, langId, idx);
                insertNodeInto(node, selNode, selNode.getChildCount());

                // Добавить созданный узел в дерево интерфейсов
                InterfaceNode nodeI = new InterfaceNode(obj, newName, langId);
                InterfaceNode rootI = (InterfaceNode) ((DefaultTreeModel) treeInf.getModel()).getRoot();
                ((DefaultTreeModel) treeInf.getModel()).insertNodeInto(nodeI, rootI, rootI.getChildCount());

                long[] vals = krn.getLongs(root, "children", 0);
                krn.setObject(root.id, root.classId, "children", vals.length, obj.id, 0, false);

                or3Frame.quickStartIfc(false, false);
                or3Frame.getDesignerFrame().load(obj, node.getKrnObj());

                newChangeoverUID = obj.uid;
            }
        }

        private int addInterface() {
            return addInterface(null);
        }

        /**
         * Добавить интерфейс.
         * 
         * @return
         */
        private int addInterface(ServiceControlNode selNode) {
            DesignerDialog dlg;
            OpenElementPanel op = new OpenElementPanel(treeInf);
            dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Выбор интерфейса", op);
            op.setSearchUIDPanel(true);
            dlg.setVisible(true);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                InterfaceNode inf = (InterfaceNode) op.getTree().getSelectedNode();
                if (!inf.isLeaf()) {
                    MessagesFactory.showMessageDialog((Window) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                            "Невозможен выбор папки!");
                    return ButtonsFactory.BUTTON_CANCEL;
                }

                final KrnObject obj = op.getNodeObj(inf);
                try {
                    if (selNode == null) {
                        selNode = (ServiceControlNode) getSelectedNode();
                    }
                    KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                    krn.setString(newFld.id, classFldID, "title", 0, langId, inf.toString(), 0);
                    krn.setObject(newFld.id, classFldID, "value", 0, obj.id, 0, false);
                    krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);

                    int idx = selNode.getChildCount();
                    ServiceControlNode node = new ServiceControlNode(newFld, obj, inf.toString(), -1, langId, idx);
                    insertNodeInto(node, selNode, selNode.getChildCount());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            return dlg.getResult();
        }

        /**
         * Создать фильтр.
         * 
         * @param oldObj
         *            the old obj
         * @throws KrnException
         *             the krn exception
         */
        private void createFilter(KrnObject oldObj) throws KrnException {
            final boolean isCopy = oldObj != null;
            String oldName = null;
            if (copyNode != null) {
                oldName = copyNode.toString();
            }
            String oldTitle = isCopy ? "Копия " + oldName : "";

            CreateElementPanel p = new CreateElementPanel(isCopy ? COPY_TYPE : CREATE_ELEMENT_TYPE, oldTitle, langId);
            DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), isCopy ? "Копирование Фильтра"
                    : "Создание Фильтра", p);
            dlg.show();
            if (dlg.isOK()) {
                String newName = p.getElementName().isEmpty() ? oldTitle : p.getElementName();
                final KrnClass clsR = krn.getClassByName("FilterRoot");
                final KrnObject obj = krn.createObject(SC_FILTER, 0);
                ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
                KrnObject root = krn.getClassObjects(clsR, 0)[0];
                krn.setString(obj.id, SC_FILTER.id, "title", 0, langId, newName, 0);

                KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                krn.setString(newFld.id, classFldID, "title", 0, langId, newName, 0);
                krn.setObject(newFld.id, classFldID, "value", 0, obj.id, 0, false);
                krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);

                if (isCopy) {
                    byte[] data = krn.getBlob(getCopyNode().getValue(), "config", 0, 0, 0);
                    byte[] dataSql = krn.getBlob(getCopyNode().getValue(), "exprSql", 0, 0, 0);
                    krn.setBlob(obj.id, obj.classId, "config", 0, data, 0, 0);
                    krn.setBlob(obj.id, obj.classId, "exprSql", 0, dataSql, 0, 0);
                    krn.writeLogRecord(EVENT_COPY_FILTER, "'" + oldName + "' в '" + newName + "'");
                } else {
                    krn.writeLogRecord(EVENT_CREATE_FILTER, newName);
                }
                int idx = selNode.getChildCount();
                ServiceControlNode node = new ServiceControlNode(newFld, obj, newName, -1, langId, idx);
                insertNodeInto(node, selNode, selNode.getChildCount());

                // Добавить созданный узел в дерево интерфейсов

                FilterNode rootF = (FilterNode) ((DefaultTreeModel) treeFlt.getModel()).getRoot();
                FilterNode nodeF = new FilterNode(obj, newName, langId, rootF.getChildCount());
                ((DefaultTreeModel) treeFlt.getModel()).insertNodeInto(nodeF, rootF, rootF.getChildCount());

                long[] vals = krn.getLongs(root, "children", 0);
                krn.setObject(root.id, root.classId, "children", vals.length, obj.id, 0, false);

                or3Frame.quickStartFilters(false, false);

                FiltersTree treeFlt2 = or3Frame.getFiltersFrame().getTree();
                FilterNode rootF2 = (FilterNode) ((DefaultTreeModel) treeFlt2.getModel()).getRoot();
                FilterNode nodeF2 = new FilterNode(obj, newName, langId, rootF2.getChildCount());
                ((DefaultTreeModel) treeFlt2.getModel()).insertNodeInto(nodeF2, rootF2, rootF2.getChildCount());

                or3Frame.getFiltersFrame().load(obj, node.getKrnObj());
                newChangeoverUID = obj.uid;

            }
        }

        private int addFilter() {
            return addFilter(null);
        }

        /**
         * Добавить фильтр.
         * 
         * @return
         */
        private int addFilter(ServiceControlNode selNode) {
            DesignerDialog dlg;
            OpenElementPanel op = new OpenElementPanel(treeFlt);
            dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Выбор фильтра", op);
            op.setSearchUIDPanel(true);
            dlg.setVisible(true);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                FilterNode proc = (FilterNode) op.getTree().getSelectedNode();
                final KrnObject filterUI = op.getNodeObj(proc);
                long id = filterUI.id;
                try {
                    if (selNode == null) {
                        selNode = (ServiceControlNode) getSelectedNode();
                    }
                    int idx = selNode.getChildCount();
                    String[] attr = krn.getStrings(filterUI, "title", 0, 0);

                    KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                    krn.setString(newFld.id, classFldID, "title", 0, langId, attr[0], 0);
                    krn.setObject(newFld.id, classFldID, "value", 0, id, 0, false);
                    krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);

                    ServiceControlNode node = new ServiceControlNode(newFld, filterUI, attr[0], -1, langId, idx);
                    insertNodeInto(node, selNode, selNode.getChildCount());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            return dlg.getResult();
        }

        /**
         * Создать отчёт.
         * 
         * @param oldObj
         *            the old obj
         * @throws KrnException
         *             the krn exception
         * @throws JDOMException
         *             the jDOM exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private void createReport(KrnObject oldObj) throws KrnException, JDOMException, IOException {
            final boolean isCopy = oldObj != null;
            String oldName = null;
            if (copyNode != null) {
                oldName = copyNode.toString();
            }
            String oldTitle = isCopy ? "Копия " + oldName : "";

            CreateElementPanel p = new CreateElementPanel(isCopy ? COPY_TYPE : CREATE_ELEMENT_TYPE, oldTitle, langId);
            DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), isCopy ? "Копирование Отчёта"
                    : "Создание Отчёта", p);
            dlg.show();
            if (dlg.isOK()) {
                String newName = p.getElementName().isEmpty() ? "Безымянный" : p.getElementName();
                final KrnClass clsR = krn.getClassByName("ReportRoot");
                final KrnObject obj = krn.createObject(SC_REPORT_PRINTER, 0);
                ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
                KrnObject root = krn.getClassObjects(clsR, 0)[0];
                krn.setString(obj.id, SC_REPORT_PRINTER.id, "title", 0, langId, newName, 0);

                KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                krn.setString(newFld.id, classFldID, "title", 0, langId, newName, 0);
                krn.setObject(newFld.id, classFldID, "value", 0, obj.id, 0, false);
                krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);

                OrGuiComponent comp;
                OrFrame frame = new EmptyFrame();
                if (isCopy) {
                    byte[] data = krn.getBlob(getCopyNode().getValue(), "config", 0, 0, 0);
                    Element xml = null;
                    krn.setBlob(obj.id, obj.classId, "config", 0, data, 0, 0);
                    if (data.length > 0) {
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
                        SAXBuilder b = new SAXBuilder();
                        xml = b.build(is).getRootElement();
                        is.close();
                        comp = Factories.instance().create(xml, Mode.DESIGN, null);
                    } else {
                        comp = Factories.instance().create("ReportPrinter", null);
                    }
                    comp.setPropertyValue(new PropertyValue(newName, langId, comp.getProperties().getChild("title")));
                    krn.writeLogRecord(EVENT_COPY_REPORT, "'" + oldName + "' в '" + newName + "'");
                } else {
                    String xml = "<Component class=\"ReportPrinter\"><title><L1>" + newName
                            + "</L1></title><groupType>false</groupType><editorType>0</editorType><titleKaz><L1>" + newName
                            + "</L1></titleKaz></Component>";
                    krn.setBlob(obj.id, SC_REPORT_PRINTER.id, "config", 0, xml.getBytes(), 0, 0);

                    comp = Factories.instance().create("ReportPrinter", frame);
                    comp.setPropertyValue(new PropertyValue(newName, comp.getProperties().getChild("title")));
                    comp.setPropertyValue(new PropertyValue(newName, comp.getProperties().getChild("titleKaz")));
                    comp.setPropertyValue(new PropertyValue(0, comp.getProperties().getChild("editorType")));
                    krn.writeLogRecord(EVENT_CREATE_REPORT, newName);
                }

                int idx = selNode.getChildCount();
                ServiceControlNode node = new ServiceControlNode(newFld, obj, newName, -1, langId, idx);
                // внести свойства в компонент

                insertNodeInto(node, selNode, selNode.getChildCount());

                long[] vals = krn.getLongs(root, "children", 0);
                krn.setObject(root.id, root.classId, "children", vals.length, obj.id, 0, false);

                // Добавить созданный узел в дерево
                ReportNode rootR = (ReportNode) ((DefaultTreeModel) treeRpt.getModel()).getRoot();
                ReportNode nodeR = new ReportNode(obj, newName, comp, rootR.getChildCount(), frame, new ArrayList<KrnObject>());
                ((DefaultTreeModel) treeRpt.getModel()).insertNodeInto(nodeR, rootR, rootR.getChildCount());
                ReportPanel rf = or3Frame.getReportFrame();
                if (rf != null) {
                    ReportTree tree = rf.getTree();
                    rootR = (ReportNode) ((DefaultTreeModel) tree.getModel()).getRoot();
                    ((DefaultTreeModel) tree.getModel()).insertNodeInto(nodeR, rootR, rootR.getChildCount());
                }

                or3Frame.quickStartReports(false);
                or3Frame.getReportFrame().load(obj);

                newChangeoverUID = obj.uid;
            }
        }

        private int addReport() {
            return addReport(null);
        }

        /**
         * Добавить отчёт.
         * 
         * @return
         */
        private int addReport(ServiceControlNode selNode) {
            DesignerDialog dlg;
            OpenElementPanel op = new OpenElementPanel(treeRpt);
            dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Выбор процесса", op);
            op.setSearchUIDPanel(true);
            dlg.setVisible(true);
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                ReportNode reportNode = (ReportNode) op.getTree().getSelectedNode();
                final KrnObject objReport = op.getNodeObj(reportNode);
                try {
                    if (selNode == null) {
                        selNode = (ServiceControlNode) getSelectedNode();
                    }
                    // создать новую директорию
                    KrnObject newFld = krn.createObject(SC_CONTROL_FOLDER, 0);
                    krn.setString(newFld.id, classFldID, "title", 0, langId, reportNode.toString(), 0);
                    krn.setObject(newFld.id, classFldID, "value", 0, objReport.id, 0, false);
                    krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false);
                    int idx = selNode.getChildCount();
                    ServiceControlNode node = new ServiceControlNode(newFld, objReport, reportNode.toString(), -1, langId, idx);
                    insertNodeInto(node, selNode, selNode.getChildCount());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            return dlg.getResult();
        }

        public void addSubElements(KrnObject service, ServiceControlNode selNodeParent) throws KrnException {
            KrnObject serv = service;
            ServiceControlNode selNode = selNodeParent;
            String elementTitle = "";
            KrnObject subElement = null;
            boolean isContains = false;
            String parentUID = "";
            List<String> serviceElements = new ArrayList<String>();
            if (!selNode.isFolder()) {
                serviceElements = kz.tamur.util.XmlParserUtil.getObjectElements(serv);
            }
            if (serviceElements.size() > 0 || serviceElements != null) {
                KrnObject val;
                ServiceControlNode selNodeChild;
                StringValue[] svs;
                KrnObject newFld;
                ServiceControlNode newNode;
                boolean dontRep;
                ServiceControlNode supProcess;
                long id;
                for (int i = 0; i < serviceElements.size(); i++) {
                    isContains = false;
                    subElement = krn.getObjectByUid(serviceElements.get(i), 0);
                    if (subElement != null && listForAdd.contains(subElement)) {
                        id = subElement.classId;
                        if (id == SC_UI.id || id == SC_FILTER.id || id == SC_REPORT_PRINTER.id || id == SC_PROCESS_DEF.id) {
                            parentUID = " ";
                            for (int k = 1; k < selNode.getPath().length - 1; k++) {
                                val = ((ServiceControlNode) selNode.getPath()[k]).getValue();
                                if (val != null) {
                                    parentUID = val.uid;
                                    if (parentUID.equals(serv.uid)) {
                                        isContains = true;
                                    }
                                }
                            }
                            if (isContains == false) {
                                for (int j = 0; j < selNode.getChildCount(); j++) {
                                    selNodeChild = (ServiceControlNode) selNode.getChildAt(j);
                                    if (subElement.uid.equals(selNodeChild.getValue().uid)) {
                                        isContains = true;
                                    }
                                }
                            }
                            if (!isContains && !subElement.uid.equals(serv.uid)) {
                                svs = krn.getStringValues(new long[]{ subElement.id }, id, "title", langId, false, 0);
                                elementTitle = "Не назначен заголовок";
                                if (svs.length > 0 && svs[0] != null) {
                                    elementTitle = svs[0].value;
                                }
                                newFld = krn.createObject(SC_CONTROL_FOLDER, 0); // Создаем новый объект-ссылку
                                krn.setString(newFld.id, classFldID, "title", 0, langId, elementTitle, 0); // заголовок элемента
                                krn.setObject(newFld.id, classFldID, "value", 0, subElement.id, 0, false); // ИД элемента
                                krn.setObject(newFld.id, classFldID, "parent", 0, selNode.getKrnObj().id, 0, false); // Текущий узел. Объект ClassFolder
                                int idx = selNode.getChildCount();
                                newNode = new ServiceControlNode(newFld, subElement, elementTitle, -1, langId, idx);
                                insertNodeInto(newNode, selNode, selNode.getChildCount());
                                dontRep = true;
                                // поиск дубликатов интефейсов в супер процессе
                                if (SC_UI.id == id) {
                                    supProcess = getSuperProcess(newNode);
                                    if (supProcess != null) {
                                        dontRep = !supProcess.findLoadedChildValue(newNode);
                                    }
                                }
                                if (!getDeadLock(newNode, subElement) && dontRep) {
                                    addSubElements(subElement, newNode);
                                }
                            }
                        }
                    }
                }
            }
        }

        
        @Override
        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            return null;
        }

        
        @Override
        public void deleteNode(AbstractDesignerTreeNode node_, boolean isMove) throws KrnException {
        }

        /**
         * Если <code>isDel</code>=<code>false</code> - Разрушает связи узла и его потомков в проектном дереве
         * если <code>true</code> - Удаляет указанный узел из БД
         * Все связи узла и потомков в проектном дереве разрушаются.
         * 
         * @param node_
         *            the node_
         * @param isDel
         *            the is del
         * @throws KrnException
         *             the krn exception
         * @throws ClassNotFoundException
         *             the class not found exception
         */
        public void deleteSelectedNode(AbstractDesignerTreeNode node_, boolean isDel, boolean force) throws KrnException,
                ClassNotFoundException {
            final ServiceControlNode node = node_ == null ? (ServiceControlNode) getSelectedNode() : (ServiceControlNode) node_;
            KrnObject object = node.getValue();
            KrnObject nodeObj = node.getKrnObj();
            DesignerTree tree = null;
            SystemEvent event = null;
            String message = null;
            String message2 = isDel ? " из системы?" : " из проектного дререва?";
            if (node.isService()) {
                tree = treeSrv;
                event = SystemEvent.EVENT_DELETE_PROCESS;
                message = "процесс";
            } else if (node.isInterface()) {
                tree = treeInf;
                event = SystemEvent.EVENT_DELETE_INTERFACE;
                message = "интерфейс";
            } else if (node.isFilter()) {
                tree = treeFlt;
                event = SystemEvent.EVENT_DELETE_FILTER;
                message = "фильтр";
            } else if (node.isReport()) {
                tree = treeRpt;
                event = SystemEvent.EVENT_DELETE_REPORT;
                message = "отчёт";
            } else if (node.isFolder()) {
                tree = kz.tamur.comps.Utils.getServicesControlTree();
                event = SystemEvent.EVENT_DELETE_DIRECTORY;
                message = "директорию";
            }

            if (force
                    || JOptionPane.showConfirmDialog(getTopLevelAncestor(), "Вы действительно ходите удалить " + message
                            + message2, "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (isDel && !node.isFolder()) {
                    // получить узел с переименовываемым объектом из спец. дерева
                    AbstractDesignerTreeNode specNode = (AbstractDesignerTreeNode) tree.getRoot().find(object)
                            .getLastPathComponent();
                    ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(specNode);
                }

                // получить родительский элемент узла из проектного дерева
                ServiceControlNode parent = (ServiceControlNode) node.getParent();
                // удалить объекты в компонентах на интерфейсе проектного дерева
                removeNodeFromParent(node);

                // разрушить связи потомков
                List<KrnObject> list = new ArrayList<KrnObject>();
                // поиск всех дочерних узлов(включая вложенные) для удаления
                findAllChildren(nodeObj, list, !isDel);
                for (KrnObject obj : list) {
                    krn.deleteObject(obj, 0);
                }

                // при удалении отчёта с открытым дизайнером нужно удалить ветку и из дизайнера
                if (node.isReport() && or3Frame.getReportFrame() != null) {
                    ReportTree tree_ = or3Frame.getReportFrame().getTree();
                    AbstractDesignerTreeNode nodeR = (AbstractDesignerTreeNode) tree_.getRoot().find(object)
                            .getLastPathComponent();
                    // выделить родительский узел
                    ReportNode parentR = (ReportNode) nodeR.getParent();
                    if (parent != null) {
                        tree_.setSelectionPath(new TreePath(parentR));
                    }
                    ((DefaultTreeModel) tree_.getModel()).removeNodeFromParent(nodeR);
                }

                // при удалении процесса с открытым дизайнером нужно удалить процесс и из дизайнера
                if (node.isService() && or3Frame.getServiceFrame() != null) {
                    or3Frame.getServiceFrame().removeCurrentTab(object.id);
                }
                // при удалении интерфейса с открытым дизайнером нужно удалить интерфейс и из дизайнера
                if (node.isInterface() && or3Frame.getDesignerFrame() != null) {
                    or3Frame.getDesignerFrame().removeCurrentTab(object.id);
                }

                if (node.isFilter() && or3Frame.getFiltersFrame() != null) {
                    or3Frame.getFiltersFrame().removeCurrentTab(object.id);
                }
                // удалить объект узла из БД
                if (isDel) {// удалить узел из БД
                    krn.deleteObject(nodeObj, 0);
                    if (!node.isFolder()) {
                        krn.deleteObject(object, 0);
                    }
                    krn.writeLogRecord(event, node.toString());
                }
            }
        }

        @Override
        public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            insertNodeInto(node, parent, parent.getChildCount());
        }

        /**
         * Copy selected node.
         * 
         * @param isMove
         *            the is move
         */
        public void copySelectedNode(boolean isMove) {
            setCopyNode(getSelectedNode());
            this.isMove = isMove;
        }

        /**
         * Paste selected node.
         * 
         * @throws KrnException
         *             the krn exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws JDOMException
         *             the jDOM exception
         */
        public void pasteSelectedNode() throws KrnException, IOException, JDOMException {

            if (copyNode != null) {
                setCursor(Constants.WAIT_CURSOR);
                ServiceControlNode node = (ServiceControlNode) getSelectedNode();
                long id = node.getKrnObj().id;
                long copyId = copyNode.getKrnObj().id;
                if (isMove) {
                    // переписать ссылку на родителя у объекта
                    krn.setObject(copyId, classFldID, "parent", 0, id, 0, false);

                    removeNodeFromParent(copyNode);
                    insertNodeInto(copyNode, node, node.getChildCount());
                } else {
                    if (getCopyNode().isService()) {
                        createService(getCopyNode().getValue());
                    } else if (getCopyNode().isInterface()) {
                        createInterface(getCopyNode().getValue());
                    } else if (getCopyNode().isFilter()) {
                        createFilter(getCopyNode().getValue());
                    } else if (getCopyNode().isReport()) {
                        createReport(getCopyNode().getValue());
                    } else if (getCopyNode().isFolder()) {
                        createFolder(getCopyNode().getKrnObj());
                    }
                }
                setCopyNode(null);
                setCursor(Constants.DEFAULT_CURSOR);
            }
        }

        public void replaceSelectedNode() throws KrnException, ClassNotFoundException {
            ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
            ServiceControlNode parentNode = (ServiceControlNode) selNode.getParent();
            int rez = -1;
            if (selNode.isService()) {
                rez = getModel().addService(parentNode);
            } else if (selNode.isInterface()) {
                rez = getModel().addInterface(parentNode);
            } else if (selNode.isFilter()) {
                rez = getModel().addFilter(parentNode);
            } else if (selNode.isReport()) {
                rez = getModel().addReport(parentNode);
            }
            if (rez == ButtonsFactory.BUTTON_OK) {
                deleteSelectedNode(selNode, false, true);
            }
        }

        /**
         * Changeover copy selected node
         * 
         * @throws IOException
         * @throws KrnException
         * @throws JDOMException
         * 
         */
        public void changeoverCopySelectedNode() throws KrnException, IOException, JDOMException {
            copyNode = (ServiceControlNode) getSelectedNode();
            if (((ServiceControlNode) copyNode.getParent()).isInterface()) {
                org.jdom.filter.ElementFilter elementFilter = null;
                String[] options = { "YES", "NO" };
                int response = JOptionPane.showOptionDialog(null, "Будет создана копия элемента '" + getCopyNode().getTitle() + "' \n Все UID копируемого элемента в родительском элементе будут заменены на UID нового элемента\n\nПродолжить ?", "Внимание!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (response == JOptionPane.YES_OPTION) {
                    newChangeoverUID = "";
                    setSelectionPath(new TreePath(((ServiceControlNode) copyNode.getParent()).getPath()));

                    if (getCopyNode().isInterface()) {
                        elementFilter = new org.jdom.filter.ElementFilter("KrnObject");
                        createInterface(getCopyNode().getValue());
                    } else if (getCopyNode().isFilter()) {
                        elementFilter = new org.jdom.filter.ElementFilter("Filter");
                        createFilter(getCopyNode().getValue());
                    } else if (getCopyNode().isReport()) {
                        elementFilter = new org.jdom.filter.ElementFilter("Report");
                        createReport(getCopyNode().getValue());
                    }

                    // замена uid в родительском элементе
                    KrnObject newChangeoverObj = krn.getObjectByUid(newChangeoverUID, 0);
                    if (newChangeoverObj != null) {
                        String currentChangeoverUID = getCopyNode().getValue().uid;
                        KrnObject parentObj = ((ServiceControlNode) copyNode.getParent()).getValue();
                        byte[] configData = krn.getBlob(parentObj, "config", 0, 0, 0);
                        String newTit = krn.getStrings(newChangeoverObj, "title", langId, 0)[0];

                        Pattern uidInScriptPttrn = Pattern.compile("\\$Objects\\s*\\.\\s*getObject\\s*\\(\\s*\"([^\"]+)\"");
                        String elementUID = "";
                        ByteArrayInputStream is = new ByteArrayInputStream(configData);
                        SAXBuilder saxBuild = new SAXBuilder();
                        org.jdom.Element configRoot = saxBuild.build(is).getRootElement();
                        is.close();
                        org.jdom.Element krnUI = null;

                        if (getCopyNode().isInterface()) {
                            for (Iterator it = configRoot.getDescendants(elementFilter); it.hasNext();) {
                                krnUI = (Element) it.next();
                                if (krnUI.getAttribute("class").getValue().trim().equals("UI")) {
                                    elementUID = krnUI.getAttributeValue("id").trim();
                                    if (currentChangeoverUID.equals(elementUID)) {
                                        krnUI.setAttribute("id", newChangeoverUID);
                                        krnUI.setAttribute("title", newTit);
                                    }
                                }
                            }
                        } else if (getCopyNode().isFilter() || getCopyNode().isReport()) {
                            krnUI = null;
                            for (Iterator it = configRoot.getDescendants(elementFilter); it.hasNext();) {
                                krnUI = (Element) it.next();
                                elementUID = krnUI.getAttributeValue("id").trim();
                                if (currentChangeoverUID.equals(elementUID)) {
                                    krnUI.setAttribute("id", newChangeoverUID);
                                    krnUI.setAttribute("title", newTit);
                                }
                            }
                        }

                        XMLOutputter o = new XMLOutputter();
                        String newConf = o.outputString(configRoot);
                        newConf = newConf.replaceAll(currentChangeoverUID, newChangeoverUID);

                        krn.setBlob(parentObj.id, parentObj.classId, "config", 0, newConf.getBytes(), 0, 0);
                        newChangeoverUID = "";
                        // this.reload();
                    }
                }
            }
        }
    }

    /**
     * The Class CellRenderer.
     */
    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            ServiceControlNode node = (ServiceControlNode) value;
            if (node != null) {
                if (node.isFolder()) {
                    switch (node.getType()) {
                    case 1:
                        l.setIcon(getImageIconFull("groupNode.png"));
                        break;
                    default:
                        l.setIcon(getImageIconFull("folderNode.png"));
                    }
                } else if (node.isService()) {
                    l.setIcon(getImageIconFull("serviceNode.png"));
                } else if (node.isInterface()) {
                    l.setIcon(getImageIconFull("interfaceNode.png"));
                } else if (node.isFilter()) {
                    l.setIcon(getImageIconFull("filterNode.png"));
                } else if (node.isReport()) {
                    l.setIcon(getImageIconFull("reportNode.png"));
                } else {
                    l.setIcon(getImageIconFull("question.png"));
                }
                l.setForeground(Color.BLACK);
            }

            l.setBackground(Color.LIGHT_GRAY);

            l.setOpaque(selected || isOpaque);
            return l;
        }

    }

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == miCreateService) {
                getModel().createService(null);
            } else if (src == miAddService) {
                getModel().addService();
            } else if (src == miServiceElements) {
                addSubElements();
            } else if (src == miCreateInterface) {
                getModel().createInterface(null);
            } else if (src == miAddInterface) {
                getModel().addInterface();
            } else if (src == miCreateFilter) {
                getModel().createFilter(null);
            } else if (src == miAddFilter) {
                getModel().addFilter();
            } else if (src == miCreateReport) {
                getModel().createReport(null);
            } else if (src == miAddReport) {
                getModel().addReport();
            } else if (src == miCreateFolder) {
                getModel().createFolder(null);
            } else if (src == miView) {
                view();
            } else if (src == miRename) {
                getModel().renameNode();
            } else if (src == miCopy) {
                getModel().copySelectedNode(false);
            } else if (src == miCut) {
                getModel().copySelectedNode(true);
            } else if (src == miPaste) {
                getModel().pasteSelectedNode();
            } else if (src == miCancel) {
                if (copyNode != null) {
                    CursorToolkit.stopWaitCursor(this);
                    copyNode.setCopyProcessStarted(false);
                    repaint();
                    setCopyNode(null);
                }
            } else if (src == miReplaceElement) {
                getModel().replaceSelectedNode();
            } else if (src == miDeleteOnlySelectedNode) {
                getModel().deleteSelectedNode(null, true, false);
            } else if (src == miDeleteNodeFromTree) {
                getModel().deleteSelectedNode(null, false, false);
            } else if (src == miChangeoverCopy) {
                getModel().changeoverCopySelectedNode();
            } else if (src == miRole) {
                getModel().setRoleNode();
            } else if (src == miFind) {
                find();
            } else if (src == miFindNext) {
                keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED, KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_F3,
                        KeyEvent.CHAR_UNDEFINED));
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        } catch (JDOMException e4) {
            e4.printStackTrace();
        }
    }

    /**
     * Получить идентификатор языка.
     * 
     * @return id языка
     */
    public long getLangId() {
        return langId;
    }

    /**
     * Установить идентификатор языка.
     * 
     * @param langId
     *            the new lang id
     */
    public void setLangId(long langId) {
        this.langId = langId;
        Kernel krn = Kernel.instance();
        ServiceControlNode node = (ServiceControlNode) getSelectedNode();
        try {
            long[] ids = { root.getKrnObj().id };
            StringValue[] svs = krn.getStringValues(ids, root.getKrnObj().classId, "title", langId, false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }

            LongValue[] lvs = krn.getLongValues(ids, root.getKrnObj().classId, "type", 0);
            int type = 0;
            if (lvs.length > 0 && lvs[0] != null) {
                type = (int) lvs[0].value;
            }
            root = new ServiceControlNode(root.getKrnObj(), null, title, type, langId, 0);
            model = new ServiceControlTreeModel(root);
            setModel(model);
            if (node != null)
                setSelectedNode(node.getKrnObj());
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();

    }

    /**
     * Переименовать процесс.
     * 
     * @param node
     *            the node
     */
    public void renameServiceControlNode(ServiceControlNode node) {
        getModel().rename(node);
    }

    public void renameServiceControlNode2(ServiceControlNode node, String newName) {
        try {
            KrnAttribute attr = krn.getAttributeByName(SC_CONTROL_FOLDER, "title");
            // изменить объект в базе
            krn.setString(node.getKrnObj().id, attr.id, 0, langId, newName, 0);
        } catch (Exception e) {
            System.out.println("Ошибка переименования объекта!");
            e.printStackTrace();
        }
        // переименовать узел в дереве проектов
        node.rename(newName);
        getModel().rename(node);
    }

    @Override
    protected void showPopup(MouseEvent e) {
        ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
        if (selNode == null) {
            return;
        }
        miView.setEnabled(!selNode.isFolder());
        miRole.setEnabled(selNode.isFolder());
        miDeleteNodeFromTree.setEnabled(!selNode.isFolder());
        miReplaceElement.setEnabled(!selNode.isFolder());
        miServiceElements.setEnabled(selNode.isService() || selNode.isInterface());

        // динамическое формирование ссылок на родителей в текущем дереве
        if (!selNode.isFolder()) {
            miChangeoverCopy.setEnabled(true);
            final ServiceControlNode currentNode = selNode;
            mElementParents.removeAll();
            Set<ServiceControlNode> nodeParents = new HashSet<ServiceControlNode>();
            nodeParents = ((ServiceControlNode) model.getRoot()).findAllParents(currentNode.getValue());
            if (!nodeParents.isEmpty() && nodeParents.size() > 1) {
                mElementParents.setEnabled(true);
                Iterator itr = nodeParents.iterator();
                while (itr.hasNext()) {
                    final ServiceControlNode nodeParent = (ServiceControlNode) itr.next();
                    if (nodeParent == null) {
                        continue;
                    }
                    final JMenuItem mi = createMenuItem(nodeParent.getTitle());
                    mi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            TreePath tpath = new TreePath(nodeParent.getPath());

                            // переход на другой узел этого же элемента
                            // for(int j = 0; j < nodeParent.getChildCount(); j++){
                            // ServiceControlNode selNodeChild = (ServiceControlNode)nodeParent.getChildAt(j);
                            // if(currentNode.getValue().uid.equals(selNodeChild.getValue().uid)){
                            // tpath = new TreePath(selNodeChild.getPath());
                            // break;
                            // }
                            // }
                            setSelectionPath(tpath);
                            // scrollPathToVisible(tpath);
                            expandPath(tpath);
                        }
                    });
                    mElementParents.add(mi);
                }
            } else
                mElementParents.setEnabled(false);
        } else {
            mElementParents.setEnabled(false);
            miChangeoverCopy.setEnabled(false);
        }

        if (selNode.isInterface() || selNode.isFilter()) {
            if (((ServiceControlNode) selNode.getParent()).isInterface()) {
                miChangeoverCopy.setEnabled(true);
            } else
                miChangeoverCopy.setEnabled(false);
        } else
            miChangeoverCopy.setEnabled(false);

        pm.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Выбрать узел в дереве
     * 
     * @param obj
     *            - объект, который является значением узла, которого не обходимо выбрать
     */
    public void setSelectedNode(KrnObject obj) {
        if (!selfChanged) {
            setSelectedNode(getControlNodeByObject(obj));
        }
    }

    /**
     * Выбрать узел в дереве
     * 
     * @param obj
     *            - объект, который является узлом, и который необходимо выбрать
     * 
     */
    public void setSelectedNode2(KrnObject obj) {
        if (!selfChanged) {
            setSelectedNode(getControlNodeByObject2(obj));
        }
    }

    /**
     * Получить service node by object.
     * 
     * @param object
     *            the object
     * @return the service node by object
     */
    public ServiceControlNode getControlNodeByObject(KrnObject object) {
        return ((ServiceControlNode) getModel().getRoot()).findChildValue(object);
    }

    /**
     * Получить service node by object.
     * 
     * @param object
     *            the object
     * @return the service node by object
     */
    public ServiceControlNode getControlNodeByObject2(KrnObject object) {
        return ((ServiceControlNode) getModel().getRoot()).findChild(object);
    }

    public boolean getDeadLock(ServiceControlNode selNode, KrnObject object) {
        ServiceControlNode parent = (ServiceControlNode) selNode.getParent();
        return parent == null ? false : parent.getValue() != null && (parent.getValue().equals(object) || getDeadLock(parent, parent.getValue()));
    }

    /**
     * Получить супер-процесс элемента.
     * 
     * @param selNode
     *            узел, для которого производиться поиск супер-процесса
     * @return super process
     */
    public ServiceControlNode getSuperProcess(ServiceControlNode selNode) {
        // создать список для всех процессов-родителей
        ArrayList<ServiceControlNode> list = new ArrayList<ServiceControlNode>();
        getSuperProcess(selNode, list);
        if (list.size() == 0) {
            return null;
        } else {
            // верхний элемент списка и будет суперпроцессом для данного узла
            return list.get(list.size() - 1);
        }
    }

    /**
     * Получить список родительских процессов элемента.
     * 
     * Рекурсия.
     * 
     * @param selNode
     *            узел, для которого производиться поиск супер-процесса
     * @param list
     *            the list
     */
    public void getSuperProcess(ServiceControlNode selNode, List<ServiceControlNode> list) {
        ServiceControlNode parent = (ServiceControlNode) selNode.getParent();
        if (selNode != null && selNode.isService()) {
            list.add(selNode);
        }
        if (parent != null) {
            getSuperProcess(parent, list);
        }

    }

    /**
     * (non-Javadoc).
     * 
     * @see kz.tamur.util.DesignerTree#pasteElement()
     */
    @Override
    protected void pasteElement() {
    }

    /**
     * View.
     */
    private void view() {
        ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();

        selfChanged = true;
        CursorToolkit.startWaitCursor(this);
        ServiceControl sc = ServiceControl.instance();
        if (selNode.isService()) {
            sc.showService(selNode);
        } else if (selNode.isInterface()) {
            sc.showInterface(selNode);
        } else if (selNode.isFilter()) {
            sc.showFilter(selNode);
        } else if (selNode.isReport()) {
            sc.showReport(selNode);
        }
        CursorToolkit.stopWaitCursor(this);
        selfChanged = false;
    }

    /**
     * Find all children.
     * 
     * @param object
     *            the object
     * @param list
     *            the list
     * @param isAdd
     *            the is add
     */
    private void findAllChildren(KrnObject object, List<KrnObject> list, boolean isAdd) {
        if (isAdd) {
            list.add(object);
        }
        try {
            KrnObject[] objs = krn.getObjects(object, "children", 0);
            if (objs != null && objs.length > 0) {
                for (KrnObject obj : objs) {
                    findAllChildren(obj, list, true);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет, есть ли в дереве управления объект выбранного узла.
     * 
     * @param selNode
     *            узел, объект которого необходимо проверить.
     * @return true, если there
     */
    private boolean isThere(AbstractDesignerTreeNode selNode) {
        return selNode != null && getControlNodeByObject(selNode.getKrnObj()) != null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && getSelectedNode() != null) {
            view();
        }
    }
    
    public DesignerTreeNode[] getSelectedNodes() {
        List<ServiceControlNode> list = new ArrayList<ServiceControlNode>();
        TreePath[] paths = getSelectionPaths();
        for (TreePath path : paths) {
            ServiceControlNode node = (ServiceControlNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        ServiceControlNode[] res = new ServiceControlNode[list.size()];
        list.toArray(res);
        return res;
    }

    public List<ServiceControlNode> findAllChild(KrnObject obj) {
        return ((ServiceControlNode) model.getRoot()).findAllChild(obj);

    }

    public void keyPressed(KeyEvent e) {
        try {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) { // удалить из дерева
                getModel().deleteSelectedNode(null, false, false);
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE && e.isShiftDown()) { // удалить объект
                getModel().deleteSelectedNode(null, true, false);
            } else if (e.getKeyCode() == KeyEvent.VK_F2) { // переименовать
                getModel().renameNode();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // отменить
                if (copyNode != null) {
                    CursorToolkit.stopWaitCursor(this);
                    copyNode.setCopyProcessStarted(false);
                    repaint();
                    setCopyNode(null);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {// копировать
                getModel().copySelectedNode(false);
            } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {// вставить
                getModel().pasteSelectedNode();
            } else if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {// вырезать
                getModel().copySelectedNode(true);
            } else if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {// заменить
                getModel().replaceSelectedNode();
            } else if ((e.getKeyCode() == KeyEvent.VK_F && e.isControlDown())||e.getKeyCode() == KeyEvent.VK_F3) { // поиск
                find();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { // просмотр
                view();
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JDOMException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    protected void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Поиск", sip);
        dlg.show();
        if (dlg.isOK()) {
            final String searchString = sip.getSearchText();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    or3Frame.setState(FrameTemplate.STATE_FIND);
                    CursorToolkit.startWaitCursor(or3Frame);
                    
                    List<ServiceControlNode> nodes = ((ServiceControlNode) root).findNodeByTitle(searchString, sip.getSearchMethod());
                  
                    CursorToolkit.stopWaitCursor(or3Frame);
                    or3Frame.setState(FrameTemplate.STATE_DEF);
                    
                    if (nodes == null || nodes.size() == 0) {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    } else {

                        JPanel panel = new JPanel(new GridBagLayout());
                        JPanel panelIn = new JPanel(new GridBagLayout());
                        JLabel title = kz.tamur.rt.Utils.createLabel("Найденно: " + nodes.size());
                        final JLabel pth = kz.tamur.rt.Utils.createLabel("");

                        JScrollPane scroll = new JScrollPane(panelIn);
                        scroll.setOpaque(false);
                        scroll.getViewport().setOpaque(false);
                        JScrollPane scroll2 = new JScrollPane(pth);
                        scroll2.setOpaque(false);
                        scroll2.getViewport().setOpaque(false);
                        panel.setOpaque(false);
                        panelIn.setOpaque(false);
                        kz.tamur.rt.Utils.setAllSize(scroll, new Dimension(300, 300));
                        kz.tamur.rt.Utils.setAllSize(scroll2, new Dimension(300, 200));

                        panel.add(title, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, new Insets(10, 2, 2, 0), 0, 0));
                        panel.add(scroll, new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, BOTH, new Insets(1, 2, 2, 1), 0, 0));
                        panel.add(scroll2, new GridBagConstraints(0, 2, 1, 1, 1, 1, WEST, BOTH, new Insets(1, 2, 2, 1), 0, 0));
                        int i = 0;
                        GridBagConstraints gbc = new GridBagConstraints(0, i, 1, 1, 1, 0, WEST, HORIZONTAL, Constants.INSETS_1, 0, 0);
                        Dimension min = new Dimension(100, 25);
                        Dimension pref = new Dimension(200, 25);
                        Dimension max = new Dimension(500, 25);
                        for (final ServiceControlNode node : nodes) {
                            TreeNode[] path = node.getPath();
                            final StringBuilder fPath = new StringBuilder();
                            if (path.length > 1) {
                                for (int j = 1; j < path.length; j++) {
                                    fPath.append(j==1?"<html>":"<br>");
                                    if(j>0) {
                                        for (int k = 1; k < j; k++) {
                                            fPath.append("&nbsp;&nbsp;");
                                        }
                                    }
                                    fPath.append(path[j].toString());
                                }
                            }
                            OrTransparentButton item = new OrTransparentButton(new StringBuilder().append(i + 1).append(".- ") .append(node.toString()).toString(), null, getForeground()) {
                                @Override
                                public void setText(String text) {
                                    setTextSuperClass(text);
                                }
                            };
                            
                            item.setHorizontalAlignment(SwingConstants.LEFT);
                            item.setMinimumSize(min);
                            item.setPreferredSize(pref);
                            item.setMaximumSize(max);
                            item.setToolTipText("Для перехода - необходимо кликнуть.");
                            panelIn.add(item, gbc);
                            gbc.gridy = ++i;
                            item.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setSelectedNode(node);
                                }
                            });
                            item.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseEntered(MouseEvent e) {
                                    pth.setText(fPath.toString());
                                }
                            });
                        }
                        DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Результаты поиска", panel);
                        dlg.show();
                    }
                }
            });
            t.start();
        }
    }

    /**
     * @return the model
     */
    public ServiceControlTreeModel getModel() {
        return (ServiceControlTreeModel) model;
    }

    public ServiceControlNode getCopyNode() {
        return (ServiceControlNode) copyNode;
    }

    /**
     * @param copyNode
     *            the copyNode to set
     */
    public void setCopyNode(AbstractDesignerTreeNode copyNode) {
        this.copyNode = copyNode;
        boolean isNull = copyNode == null;
        miCopy.setEnabled(isNull);
        miCut.setEnabled(isNull);
        miPaste.setEnabled(!isNull);
        miReplaceElement.setEnabled(isNull);
        miCancel.setEnabled(!isNull);
    }

    /**
     * Вызывает дерево просмотра структуры объекта.
     * Выбранные объекты добавляет в дерево управления.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void addSubElements() throws KrnException {
        ServiceControlNode selNode = (ServiceControlNode) getSelectedNode();
        PreviewSubElements preview = new PreviewSubElements(Or3Frame.instance(), "Предварительный просмотр", selNode);
        preview.setVisible(true);
        listForAdd.clear();
        if (preview.getResult() == ButtonsFactory.BUTTON_OK) {
            setCursor(Constants.WAIT_CURSOR);
            List<TreePath> paths = preview.getSelectedPaths();
            if (paths != null && paths.size() > 0) {
                System.out.println("Количество добавляемых узлов: " + paths.size());
                StructureViewNode node;
                for (TreePath treePath : paths) {
                    node = ((StructureViewNode) treePath.getLastPathComponent());
                    listForAdd.add(node.getKrnObj());
                }
                getModel().addSubElements(selNode.getValue(), selNode);
                listForAdd.clear();
            }
            setCursor(Constants.DEFAULT_CURSOR);
        }
    }
}
