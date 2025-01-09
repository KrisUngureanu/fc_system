package kz.tamur.guidesigner.reports;

import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconExt;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.reports.ReportTree.ReportTreeModel;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.LangMenuItem;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.ReportWrapperPOI;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.Utils;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.ServiceControlNode;
import kz.tamur.Or3Frame;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.cifs.or2.client.ClientCallback;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.StringValue;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 02.11.2004
 * Time: 11:47:06
 * To change this template use File | Settings | File Templates.
 */

public class ReportPanel extends JPanel implements ActionListener, TreeSelectionListener, PropertyListener {

    private List<ReportNode> saveList = new ArrayList<ReportNode>();
    private List<ReportNode> saveItems = new ArrayList<ReportNode>();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    // private JButton createBtn = ButtonsFactory.createToolButton("Create", "Создать");
    private JButton historyBtn = createToolButton("history.png", "История");
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Поиск");

    private JSplitPane splitPane = new JSplitPane();
    private ReportTree tree;
    private PropertyInspector inspector = new PropertyInspector(null);

    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private LanguageCombo langSelector = new LanguageCombo();

    private JButton templateBtn = ButtonsFactory.createToolButton("DocField", "Шаблон");
    private JButton viewHistoryBtn = ButtonsFactory.createToolButton("ServiceHistory.gif", "История изменений");
    private ReportNode inode;
    private OrFrame frame = new EmptyFrame();
    private JScrollPane scrollTree;

    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");

    private JPopupMenu editReportMenu;

    private JMenu downloadMenu = new JMenu("Выгрузить jasper-шаблон");
    private JMenu uploadMenu = new JMenu("Загрузить jasper-шаблон");
    
    private JMenuItem downloadItemRu = new JMenuItem("Русский", getImageIconExt("RULangFlag", ".png"));
    private JMenuItem downloadItemKz = new JMenuItem("Казахский", getImageIconExt("KZLangFlag", ".png"));
    private JMenuItem uploadItemRu = new JMenuItem("Русский", getImageIconExt("RULangFlag", ".png"));
    private JMenuItem uploadItemKz = new JMenuItem("Казахский", getImageIconExt("KZLangFlag", ".png"));

    private boolean canEdit = false;
    private boolean canDelete = false;

    private JButton resaveBtn2003 = ButtonsFactory.createToolButton("addSingle", "Пересохранить шаблон для Office 2003");
    private JButton resaveAllBtn2003 = ButtonsFactory.createToolButton("addAll", "Пересохранить все шаблоны для Office 2003");

    private JButton resaveBtn2007 = ButtonsFactory.createToolButton("addSingle", "Пересохранить шаблон для Office 2007");
    private JButton resaveAllBtn2007 = ButtonsFactory.createToolButton("addAll", "Пересохранить все шаблоны для Office 2007");
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    public static ControlTabbedContent tabbedContent = ServiceControl.instance().getContentTabs();
    JPanel toolsPanel = new JPanel(new BorderLayout());
    private JTextField uidView = new JTextField();
    
    public ReportPanel() {
        super(new BorderLayout());
        init();
    }

    public void reloadTree() {
        Kernel krn = Kernel.instance();
        KrnClass cls = null;
        try {
            cls = krn.getClassByName("ReportRoot");
            KrnObject reportRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = { reportRoot.id };
            String title = "Безымянный";
            StringValue[] svs = krn.getStringValues(ids, cls.id, "title", frame.getInterfaceLang().id, false, 0);
            if (svs.length > 0) {
                title = svs[0].value;
            }
            inode = new ReportNode(reportRoot, title, null, 0, frame);
            tree = new ReportTree(inode, frame);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            splitPane.remove(scrollTree);
            scrollTree = new JScrollPane(tree);
            scrollTree.setOpaque(isOpaque);
            scrollTree.getViewport().setOpaque(isOpaque);
            splitPane.setLeftComponent(scrollTree);
            placeDivider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tree.addTreeSelectionListener(this);
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.REPORTS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.REPORTS_DELETE_RIGHT);

        initToolBar();
        updateStatusBar();

        add(splitPane, BorderLayout.CENTER);
        final Kernel krn = Kernel.instance();
        if (tree == null) {
        	ReportTree reportTree = kz.tamur.comps.Utils.getReportTree(null);
            inode = (ReportNode) reportTree.getRoot();
            
            KrnClass cls = null;
            try {
                cls = krn.getClassByName("ReportRoot");
                KrnObject[] objs = krn.getClassObjects(cls, 0);
                if (objs == null || objs.length == 0) {
                    KrnObject obj = krn.createObject(cls, 0);
                    krn.setString(obj.id, obj.classId, "title", 0, frame.getInterfaceLang().id, "Отчеты", 0);
                }
                KrnObject reportRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = { reportRoot.id };
                String title = krn.getStringValues(ids, cls.id, "title", frame.getInterfaceLang().id, false, 0)[0].value;
//                inode = new ReportNode(reportRoot, title, null, 0, frame);
                tree = new ReportTree(inode, frame);
                tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                scrollTree = new JScrollPane(tree);
                splitPane.setLeftComponent(scrollTree);
                splitPane.setRightComponent(inspector);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        tree.addTreeSelectionListener(this);
        tree.setSelectionRow(0);
        scrollTree.setOpaque(isOpaque);
        scrollTree.getViewport().setOpaque(isOpaque);
        setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        toolsPanel.setOpaque(isOpaque);
        
        ((ClientCallback) Kernel.instance().getCallback()).setReportConstructorListener(new ReportConstructor(Kernel.instance()));
    }

    private void initToolBar() {
        toolBar.add(historyBtn);
        historyBtn.addActionListener(this);
        toolBar.addSeparator();
        toolBar.add(saveBtn);
        saveBtn.addActionListener(this);
        toolBar.add(searchBtn);
        searchBtn.addActionListener(this);
        toolBar.addSeparator();
        toolBar.add(deleteBtn);
        deleteBtn.addActionListener(this);
        toolBar.addSeparator();
        toolBar.add(templateBtn);
        templateBtn.addActionListener(this);
        toolBar.add(viewHistoryBtn);
        viewHistoryBtn.addActionListener(this);

        toolBar.addSeparator();
        toolBar.add(resaveBtn2003);
        resaveBtn2003.addActionListener(this);
        toolBar.add(resaveAllBtn2003);
        resaveAllBtn2003.addActionListener(this);

        toolBar.addSeparator();
        toolBar.add(resaveBtn2007);
        resaveBtn2007.addActionListener(this);
        toolBar.add(resaveAllBtn2007);
        resaveAllBtn2007.addActionListener(this);
        
        toolsPanel.add(toolBar, BorderLayout.WEST);
        add(toolsPanel, BorderLayout.NORTH);
        saveBtn.setEnabled(false);
        templateBtn.setEnabled(false);
        viewHistoryBtn.setEnabled(false);
        
        statusBar.addEmptySpace();
        statusBar.addSeparator();
        statusBar.addLabel("UID: ");
        statusBar.addTextField(uidView);
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

        statusBar.addLabel(" Язык отчётов:");
        langSelector.addActionListener(this);
        statusBar.addAnyComponent(langSelector);
        statusBar.addCorner();
        frame.setInterfaceLang(((LangItem) langSelector.getSelectedItem()).obj);
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == historyBtn) {
            showHistory((JComponent)src);
        } else if (src == deleteBtn) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
        } else if(src == searchBtn) {
        	tree.find();
        } else if (src == resaveBtn2003) {
            ReportNode node = (ReportNode) tree.getSelectedNode();

            String mess = "Вы действительно хотите пересохранить отчёт '" + node.toString() + "' для Office 2003?";

            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                int count = resaveTemplateTo2003();
                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Пересохранено "
                        + count + " шаблонов!");
            }
        } else if (src == resaveAllBtn2003) {
            ReportNode node = (ReportNode) tree.getSelectedNode();

            String mess = "Вы действительно хотите пересохранить содержимое папки '" + node.toString() + "' для Office 2003?";

            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                int count = resaveAllTemplateTo2003(node);
                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Пересохранено "
                        + count + " шаблонов!");
            }
        } else if (src == resaveBtn2007) {
            ReportNode node = (ReportNode) tree.getSelectedNode();

            String mess = "Вы действительно хотите пересохранить отчёт '" + node.toString() + "' для Office 2007?";

            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                int count = resaveTemplateTo2007();
                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Пересохранено "
                        + count + " шаблонов!");
            }
        } else if (src == resaveAllBtn2007) {
            ReportNode node = (ReportNode) tree.getSelectedNode();

            String mess = "Вы действительно хотите пересохранить содержимое папки '" + node.toString() + "' для Office 2007?";

            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                int count = resaveAllTemplateTo2007(node);
                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Пересохранено "
                        + count + " шаблонов!");
            }
        } else if (src == templateBtn) {
            if (editReportMenu == null) {
                editReportMenu = new JPopupMenu();
                List<LangItem> items = LangItem.getAll();
                for (LangItem item : items) {
                    JMenuItem mi = new LangMenuItem(item);
                    mi.addActionListener(this);
                    editReportMenu.add(mi);
                }
                
                downloadMenu.setIcon(getImageIcon("SaveLocal"));
                uploadMenu.setIcon(getImageIcon("SaveOnDisk"));
                editReportMenu.add(downloadMenu);
                editReportMenu.add(uploadMenu);
                
                downloadItemRu.addActionListener(this);
                downloadItemKz.addActionListener(this);
                uploadItemRu.addActionListener(this);
                uploadItemKz.addActionListener(this);
                downloadMenu.add(downloadItemRu);
                downloadMenu.add(downloadItemKz);
                uploadMenu.add(uploadItemRu);
                uploadMenu.add(uploadItemKz);
            }
            editReportMenu.show(templateBtn, 0, templateBtn.getHeight());
        } else if (src == viewHistoryBtn) {
        	ReportNode node = (ReportNode) tree.getSelectedNode();
        	if(node!=null) {
        		KrnObject obj=node.getKrnObj();
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
        } else if (src == langSelector) {
            frame.setInterfaceLang(((LangItem) langSelector.getSelectedItem()).obj);
            TreeUIDMap.clearMap(AbstractDesignerTreeNode.REPORT_NODE);
            reloadTree();
        } else if (src instanceof LangMenuItem) {
            showTemplate(((LangMenuItem) src).getLangItem().obj);
        } else if (src == downloadItemRu) {
            long rusLangId = LangItem.getByCode("RU").obj.id;
            downloadTemplate(rusLangId);
        } else if (src == downloadItemKz) {
            long rusLangId = LangItem.getByCode("KZ").obj.id;
            downloadTemplate(rusLangId);
        } else if (src == uploadItemRu) {
            long rusLangId = LangItem.getByCode("RU").obj.id;
            uploadTemplate(rusLangId);
        } else if (src == uploadItemKz) {
            long rusLangId = LangItem.getByCode("KZ").obj.id;
            uploadTemplate(rusLangId);
        }        
    }

    private void showTemplate(KrnObject lang) {
        showTemplate(lang.id);
    }

    private void showTemplate(long langId) {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        long id = node.getKrnObj().id;

        OrGuiComponent comp = node.getOrGuiComponent();
        PropertyValue pv = comp.getPropertyValue(comp.getProperties().getChild("editorType"));

        String fname = null;
        if (id > 0) {
            if (!pv.isNull()) {
                String title = "xxx" + id + "l" + langId;
                fname = (pv.intValue() == Constants.MSWORD_EDITOR) ? "doc/" + title + ".doc" : "doc/" + title + ".xls";
                try {
                    final Kernel krn = Kernel.instance();
                    File file = null;
                    byte[] buf = krn.getBlob(node.getKrnObj(), "template", 0, langId, 0);
                    if (buf.length > 0) {
                        File dir = new File("doc");
                        dir.mkdirs();
                        file = new File(fname);
                        FileOutputStream os = new FileOutputStream(file);
                        os.write(buf);
                        os.close();
                    }
                    String fileName = (file != null) ? file.getAbsolutePath() : "no-file";

                    String webUrl = Or3Frame.getWebUrl() + "/report";

                    ReportLauncher.createReport(id, langId, fileName, pv.intValue(), title, webUrl, krn.getUserSession().id.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void deleteSelected() {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        String mess = "Вы действительно хотите удалить ";
        if (node.isLeaf()) {
            mess = mess + "отчёт '" + node.toString() + "'?";
        } else {
            mess = mess + "папку '" + node.toString() + "' и всё её содержимое?";
        }
        int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            tree.deleteNode(node);
        }
    }

    private int getUnsavedCount() {
        saveItems.clear();
        checkSavedItems(inode);
        return saveItems.size();
    }

    private ReportNode checkSavedItems(ReportNode root) {
        if (root.isLeaf() && root.isModified()) {
            saveItems.add(root);
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((ReportNode) root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private ReportNode prepareSaveList(ReportNode root) {
        if (root.isLeaf() && root.isModified()) {
            saveList.add(root);
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((ReportNode) root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        prepareSaveList(inode);
        for (int i = 0; i < saveList.size(); i++) {
            ReportNode node = saveList.get(i);
            save(node);
        }
        saveBtn.setEnabled(false);
        tabbedContent.saveAllReport();
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Отчёты: \n";
            for (int i = 0; i < saveList.size(); i++) {
                ReportNode rn = saveList.get(i);
                mess = mess + rn.toString() + "\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            int res = MessagesFactory.showMessageDialog((Frame) getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                saveAll();
                return res;
            } else {
                return res;
            }
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    public void valueChanged(TreeSelectionEvent e) {
        ReportNode node = (ReportNode) e.getPath().getLastPathComponent();
        if (node.isLeaf()) {
            // OrGuiComponent comp = node.getOrGuiComponent();
            // inspector.setComponent(comp, frame);
            inspector.setObject(new ReportNodeItem(node, this));
            tree.setLastSelectedNode(node);
            deleteBtn.setEnabled(canDelete);

            resaveBtn2003.setEnabled(false);
            resaveAllBtn2003.setEnabled(false);
            resaveBtn2007.setEnabled(false);
            resaveAllBtn2007.setEnabled(false);
            if (canEdit) {
            	try {
	            	final Kernel krn = Kernel.instance();
	                byte[] data = krn.getBlob(node.getKrnObj(), "config", 0, 0, 0);
	
	                InputStream is = new ByteArrayInputStream(data);
	                SAXBuilder builder = new SAXBuilder();
	                Element xml = builder.build(is).getRootElement();
	
	                String str = xml.getChildText("editorType");
	                int editorType = (str != null && str.length() > 0) ? Integer.valueOf(str) : -1;
	                
	                if (editorType == Constants.MSWORD_EDITOR || editorType == Constants.MSEXCEL_EDITOR) {
	                    resaveBtn2003.setEnabled(canEdit);
	                    resaveBtn2007.setEnabled(canEdit);
	                } else {
	                    resaveBtn2003.setEnabled(false);
	                    resaveBtn2007.setEnabled(false);
	                }
            	} catch (Throwable ex) {
            		ex.printStackTrace();
            	}
            }

            OrGuiComponent cm = node.getOrGuiComponent();
            if (cm != null)
                cm.setPropertyValue(new PropertyValue(node.toString(), cm.getProperties().getChild("title")));
            if (node.isModified()) {
                saveBtn.setEnabled(canEdit);
                tabbedContent.setModifiedRpt(canEdit);
            }
            templateBtn.setEnabled(canEdit);
            viewHistoryBtn.setEnabled(true);
         // сохранение в историю
            HistoryWithDate hwd = new HistoryWithDate(node.getKrnObj(), new Date());
            Kernel.instance().getUser().addRptInHistory(hwd, node.toString());
            showUUID(node.getKrnObj().uid);
        } else {
            inspector.setObject(new ReportNodeItem(null, this));
            deleteBtn.setEnabled(node == inode ? false : canDelete);
            templateBtn.setEnabled(false);
            resaveBtn2003.setEnabled(false);
            resaveAllBtn2003.setEnabled(canEdit);
            resaveBtn2007.setEnabled(false);
            resaveAllBtn2007.setEnabled(canEdit);
            viewHistoryBtn.setEnabled(false);
            showUUID("");
        }
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.5);
    }

    public void propertyModified(OrGuiComponent c) {
        ReportNode repNode = (ReportNode) tree.getSelectedNode();
        ReportNode rn;
        if (!repNode.getOrGuiComponent().equals(c)) {
            rn = tree.getLastSelectedNode();
            OrGuiComponent cm = rn.getOrGuiComponent();
            String title = cm.getPropertyValue(cm.getProperties().getChild("title")).stringValue();
            if (!rn.toString().equals(title)) {
                rn.rename(title);
            }
            rn.setModified(true);

        } else {
            String title = c.getPropertyValue(c.getProperties().getChild("title")).stringValue();
            rn = (ReportNode) tree.getSelectedNode();
            if (!rn.toString().equals(title)) {
                rn.rename(title);
            }
            ((ReportNode) tree.getSelectedNode()).setModified(true);

        }
        tree.renameReport(rn);
        saveBtn.setEnabled(canEdit);
        tabbedContent.setModifiedRpt(canEdit);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    private void save(ReportNode node) {
        final Kernel krn = Kernel.instance();
        if (node.isLeaf()) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                XMLOutputter out = new XMLOutputter();
                out.getFormat().setEncoding("UTF-8");
                out.output(node.getOrGuiComponent().getXml(), os);
                os.close();
                KrnObject o = node.getKrnObj();
                krn.setBlob(o.id, o.classId, "config", 0, os.toByteArray(), 0, 0);
                krn.setString(o.id, o.classId, "title", 0, frame.getInterfaceLang().id, node.toString(), 0);
                node.save();
                node.setModified(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tree.repaint();
            
         // Обновить узлы в дереве управления!
            ServicesControlTree tree = kz.tamur.comps.Utils.getServicesControlTree();
            List<ServiceControlNode> sns = tree.findAllChild(node.getKrnObj());
            if (sns != null) {
                for (ServiceControlNode sn : sns) {
                    tree.renameServiceControlNode2(sn, node.toString());
                }
            }
            if (getUnsavedCount() > 0) {
                saveBtn.setEnabled(canEdit);
                tabbedContent.setModifiedRpt(canEdit);
            } else {
                saveBtn.setEnabled(false);
                tabbedContent.setModifiedRpt(false);
            }
        }
    }

    public void nodeRename() {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        ((ReportTreeModel) tree.getModel()).rename(node);
    }

    public void setModified() {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        node.setModified(true);
        saveBtn.setEnabled(canEdit);
        tabbedContent.setModifiedRpt(canEdit);
    }

    private int resaveTemplateTo2007() {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        return resaveTemplateTo2007(node);
    }

    private int resaveTemplateTo2003() {
        ReportNode node = (ReportNode) tree.getSelectedNode();
        return resaveTemplateTo2003(node);
    }

    private int resaveTemplateTo2007(ReportNode node) {
        int res = 0;

        long rusLangId = LangItem.getByCode("RU").obj.id;
        long kazLangId = LangItem.getByCode("KZ").obj.id;

        try {
            final Kernel krn = Kernel.instance();

            System.out.println("Resaving word template = " + node.toString());

            System.out.println("Getting rus template...");
            byte[] buf = krn.getBlob(node.getKrnObj(), "template", 0, rusLangId, 0);

            byte[] data = krn.getBlob(node.getKrnObj(), "config", 0, 0, 0);

            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();

            String str = xml.getChildText("editorType");
            int editorType = (str != null && str.length() > 0) ? Integer.valueOf(str) : -1;
            if (editorType == Constants.MSWORD_EDITOR || editorType == Constants.MSEXCEL_EDITOR) {
	            str = xml.getChildText("macros");
	            String macros = (str != null && str.length() > 0) ? str : null;
	            String suffix = (editorType == Constants.MSWORD_EDITOR) ? ".doc" : ".xls";
	
	            String suffix2 = (editorType == Constants.MSWORD_EDITOR) ? ((macros != null) ? ".docm" : ".docx")
	                    : ((macros != null) ? ".xlsm" : ".xlsx");
	
	            if (buf.length > 0) {
	                File dir = new File("doc");
	                dir.mkdirs();
	
	                System.out.println("Saving to file...");
	
	                File file = Funcs.createTempFile("xxx", suffix, dir);
	                FileOutputStream os = new FileOutputStream(file);
	                os.write(buf);
	                os.close();
	
	                String fileName = file.getAbsolutePath();
	
	                File file2 = Funcs.createTempFile("xxx", suffix2, dir);
	                file2.deleteOnExit();
	
	                String fileName2 = file2.getAbsolutePath();
	
	                // boolean needResave = ReportWrapperPOI.resaveWordReport(fileName, fileName2);
	                if (editorType == Constants.MSWORD_EDITOR)
	                    ReportWrapperPOI.resaveWordReport(fileName, fileName2, (macros != null) ? 13 : 12);
	                else {
	                    System.out.println("Deleting file2...");
	                    file2.delete();
	                    ReportWrapperPOI.resaveExcelReport(fileName, fileName2, (macros != null) ? 52 : 51);
	                }
	
	                System.out.println("Deleting file1...");
	                file.delete();
	
	                // if (needResave) {
	                System.out.println("Loading from file...");
	                file = new File(fileName2);
	                FileInputStream fis = new FileInputStream(file);
	                buf = new byte[(int) file.length()];
	                fis.read(buf);
	                fis.close();
	                System.out.println("Saving to base...");
	                Kernel.instance().setBlob(node.getKrnObj().id, node.getKrnObj().classId, "template", 0, buf, rusLangId, 0);
	                res++;
	                // }
	                System.out.println("Deleting file2...");
	                if (editorType == Constants.MSEXCEL_EDITOR)
	                    file2 = new File(fileName2);
	                file2.delete();
	            }
	
	            System.out.println("Getting kaz template...");
	            buf = krn.getBlob(node.getKrnObj(), "template", 0, kazLangId, 0);
	            if (buf.length > 0) {
	                File dir = new File("doc");
	                dir.mkdirs();
	
	                System.out.println("Saving to file...");
	                File file = Funcs.createTempFile("xxx", suffix, dir);
	                FileOutputStream os = new FileOutputStream(file);
	                os.write(buf);
	                os.close();
	
	                String fileName = file.getAbsolutePath();
	
	                File file2 = Funcs.createTempFile("xxx", suffix2, dir);
	                file2.deleteOnExit();
	
	                String fileName2 = file2.getAbsolutePath();
	
	                // boolean needResave = ReportWrapperPOI.resaveWordReport(fileName, fileName2);
	                if (editorType == Constants.MSWORD_EDITOR)
	                    ReportWrapperPOI.resaveWordReport(fileName, fileName2, (macros != null) ? 13 : 12);// 0
	                else {
	                    System.out.println("Deleting file2...");
	                    file2.delete();
	                    ReportWrapperPOI.resaveExcelReport(fileName, fileName2, (macros != null) ? 52 : 51);// 56
	                }
	
	                System.out.println("Deleting file1...");
	                file.delete();
	
	                // if (needResave) {
	                System.out.println("Loading from file...");
	                file = new File(fileName2);
	                FileInputStream fis = new FileInputStream(file);
	                buf = new byte[(int) file.length()];
	                fis.read(buf);
	                fis.close();
	                System.out.println("Saving to base...");
	                Kernel.instance().setBlob(node.getKrnObj().id, node.getKrnObj().classId, "template", 0, buf, kazLangId, 0);
	                res++;
	                // }
	                System.out.println("Deleting file2...");
	                if (editorType == Constants.MSEXCEL_EDITOR)
	                    file2 = new File(fileName2);
	                file2.delete();
	            }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Saved = " + res);
        return res;
    }

    private int resaveTemplateTo2003(ReportNode node) {
        int res = 0;

        long rusLangId = LangItem.getByCode("RU").obj.id;
        long kazLangId = LangItem.getByCode("KZ").obj.id;

        try {
            final Kernel krn = Kernel.instance();

            System.out.println("Resaving word template = " + node.toString());

            System.out.println("Getting rus template...");
            byte[] buf = krn.getBlob(node.getKrnObj(), "template", 0, rusLangId, 0);

            byte[] data = krn.getBlob(node.getKrnObj(), "config", 0, 0, 0);

            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();

            String str = xml.getChildText("editorType");
            int editorType = (str != null && str.length() > 0) ? Integer.valueOf(str) : -1;
            
            if (editorType == Constants.MSWORD_EDITOR || editorType == Constants.MSEXCEL_EDITOR) {
	            str = xml.getChildText("macros");
	            //String macros = (str != null && str.length() > 0) ? str : null;
	            String suffix = (editorType == Constants.MSWORD_EDITOR) ? ".doc" : ".xls";
	
	            if (buf.length > 0) {
	                File dir = new File("doc");
	                dir.mkdirs();
	
	                System.out.println("Saving to file...");
	
	                File file = Funcs.createTempFile("xxx", suffix, dir);
	                FileOutputStream os = new FileOutputStream(file);
	                os.write(buf);
	                os.close();
	
	                String fileName = file.getAbsolutePath();
	
	                File file2 = Funcs.createTempFile("xxx", suffix, dir);
	                file2.deleteOnExit();
	
	                String fileName2 = file2.getAbsolutePath();
	
	                // boolean needResave = ReportWrapperPOI.resaveWordReport(fileName, fileName2);
	                if (editorType == Constants.MSWORD_EDITOR)
	                    ReportWrapperPOI.resaveWordReport(fileName, fileName2, 0);
	                else {
	                    System.out.println("Deleting file2...");
	                    file2.delete();
	                    ReportWrapperPOI.resaveExcelReport(fileName, fileName2, 56);
	                }
	
	                System.out.println("Deleting file1...");
	                file.delete();
	
	                // if (needResave) {
	                System.out.println("Loading from file...");
	                file = new File(fileName2);
	                FileInputStream fis = new FileInputStream(file);
	                buf = new byte[(int) file.length()];
	                fis.read(buf);
	                fis.close();
	                System.out.println("Saving to base...");
	                Kernel.instance().setBlob(node.getKrnObj().id, node.getKrnObj().classId, "template", 0, buf, rusLangId, 0);
	                res++;
	                // }
	                System.out.println("Deleting file2...");
	                if (editorType == Constants.MSEXCEL_EDITOR)
	                    file2 = new File(fileName2);
	                file2.delete();
	            }
	
	            System.out.println("Getting kaz template...");
	            buf = krn.getBlob(node.getKrnObj(), "template", 0, kazLangId, 0);
	            if (buf.length > 0) {
	                File dir = new File("doc");
	                dir.mkdirs();
	
	                System.out.println("Saving to file...");
	                File file = Funcs.createTempFile("xxx", suffix, dir);
	                FileOutputStream os = new FileOutputStream(file);
	                os.write(buf);
	                os.close();
	
	                String fileName = file.getAbsolutePath();
	
	                File file2 = Funcs.createTempFile("xxx", suffix, dir);
	                file2.deleteOnExit();
	
	                String fileName2 = file2.getAbsolutePath();
	
	                // boolean needResave = ReportWrapperPOI.resaveWordReport(fileName, fileName2);
	                if (editorType == Constants.MSWORD_EDITOR)
	                    ReportWrapperPOI.resaveWordReport(fileName, fileName2, 0);
	                else {
	                    System.out.println("Deleting file2...");
	                    file2.delete();
	                    ReportWrapperPOI.resaveExcelReport(fileName, fileName2, 56);
	                }
	
	                System.out.println("Deleting file1...");
	                file.delete();
	
	                // if (needResave) {
	                System.out.println("Loading from file...");
	                file = new File(fileName2);
	                FileInputStream fis = new FileInputStream(file);
	                buf = new byte[(int) file.length()];
	                fis.read(buf);
	                fis.close();
	                System.out.println("Saving to base...");
	                Kernel.instance().setBlob(node.getKrnObj().id, node.getKrnObj().classId, "template", 0, buf, kazLangId, 0);
	                res++;
	                // }
	                System.out.println("Deleting file2...");
	                if (editorType == Constants.MSEXCEL_EDITOR)
	                    file2 = new File(fileName2);
	                file2.delete();
	            }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Saved = " + res);
        return res;
    }

    private int resaveAllTemplateTo2007(ReportNode rn) {
        int res = 0;

        if (!rn.isLeaf()) {
            for (Enumeration en = rn.children(); en.hasMoreElements();) {
                ReportNode child = (ReportNode) en.nextElement();
                res += resaveAllTemplateTo2007(child);
            }
        } else {
            res += resaveTemplateTo2007(rn);
        }

        return res;
    }

    private int resaveAllTemplateTo2003(ReportNode rn) {
        int res = 0;

        if (!rn.isLeaf()) {
            for (Enumeration en = rn.children(); en.hasMoreElements();) {
                ReportNode child = (ReportNode) en.nextElement();
                res += resaveAllTemplateTo2003(child);
            }
        } else {
            res += resaveTemplateTo2003(rn);
        }

        return res;
    }
    
    public void unselect() {
    	tree.setSelectedNode(tree.getRoot());
    }

    public void load(KrnObject report) {
        ReportNode reportNode = (ReportNode) tree.find(report);
        if (reportNode != null){
        	tree.setSelectedNode(reportNode);
        	HistoryWithDate hwd = new HistoryWithDate(report, new Date());
        	Kernel.instance().getUser().addRptInHistory(hwd,reportNode.toString());
        }
    }

    /**
     * @return the tree
     */
    public ReportTree getTree() {
        return tree;
    }

    /**
     * @return the inspector
     */
    public PropertyInspector getInspector() {
        return inspector;
    }
    public void showHistory(JComponent swObj) {
        JPopupMenu pm = new JPopupMenu();
        Iterator<KrnObject> it = Kernel.instance().getUser().config.getRptHistoryObjs().iterator();
        JMenuItem item;
        while (it.hasNext()) {
            final KrnObject objTmp = it.next();
            pm.add(item = createMenuItem("["+objTmp.id+"]-"+Kernel.instance().getUser().config.getRptName(objTmp)));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    load(objTmp);
                }
            });
        }
        pm.show(swObj, swObj.getX(), swObj.getY());
    }

    /**
     * @return the toolBar
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    public void rebuildPanels() {
        toolsPanel.remove(toolBar);
        toolsPanel.add(toolBar, BorderLayout.WEST);
        toolBar.setVisible(true);
        splitPane.remove(inspector);
        splitPane.setRightComponent(inspector);
        splitPane.revalidate();
    }

    /**
     * @return the toolsPanel
     */
    public JPanel getToolsPanel() {
        return toolsPanel;
    }
    
    public void showUUID(String uid) {
        uidView.setText(uid);
    }

    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText(Or3Frame.getCurrentDbName());
        currentUserLable.setText(Or3Frame.getCurrentUserName());
    }
    
    private void downloadTemplate(long langId) {
    	ReportNode node = (ReportNode) tree.getSelectedNode();
    	if(node!=null && node.isLeaf()) {
    		KrnObject obj=node.getKrnObj();
			try {
				JFileChooser fChooser = kz.tamur.comps.Utils.createOpenChooser(Constants.JASPER_FILTER);
				if (fChooser.showOpenDialog(Or3Frame.instance()) == JFileChooser.APPROVE_OPTION) {
					File sf = fChooser.getSelectedFile();
					Utils.setLastSelectDir(sf.getParentFile().toString());
					if (sf != null) {
						try {
							String canonicalPath = Funcs.normalizeInput(sf.getCanonicalPath());
							if (canonicalPath.matches(".+")) {
								Path f = Paths.get(canonicalPath);
								
					            byte[] buf = Kernel.instance().getBlob(obj, "template", 0, langId, 0);

								if (buf != null && buf.length > 0) {
									OutputStream out = Files.newOutputStream(f);
									out.write(buf);
									out.close();
								}
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			} catch (KrnException e1) {
				e1.printStackTrace();
			}
    	}
    }
    
    private void uploadTemplate(long langId) {
    	ReportNode node = (ReportNode) tree.getSelectedNode();
    	if(node!=null && node.isLeaf()) {
    		KrnObject obj=node.getKrnObj();
			try {
				JFileChooser fChooser = kz.tamur.comps.Utils.createOpenChooser(Constants.JASPER_FILTER);
				if (fChooser.showOpenDialog(Or3Frame.instance()) == JFileChooser.APPROVE_OPTION) {
					File sf = fChooser.getSelectedFile();
					Utils.setLastSelectDir(sf.getParentFile().toString());
					
	                FileInputStream fis = new FileInputStream(sf);
	                byte[] buf = new byte[(int) sf.length()];
	                fis.read(buf);
	                fis.close();

	                Kernel.instance().setBlob(obj.id, obj.classId, "template", 0, buf, langId, 0);
				}
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
    	}
    }
}
