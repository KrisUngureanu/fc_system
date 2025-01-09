package kz.tamur.admin;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 20.07.2004
 * Time: 11:10:55
 * To change this template use File | Settings | File Templates.
 */
public class ServiceBrowser extends JPanel {} //implements ActionListener {

    /*private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JTabbedPane tabbedPane = new JTabbedPane();
    private PropertyInspector inspector;
    private KrnObject srvObj = null;
    private OrGuiComponent srvManagerComp;
    private ArrayList treeList = new ArrayList();

    private JToolBar toolBar = Utils.createDesignerToolBar();

    private JButton createTabBtn = ButtonsFactory.createToolButton("createTab",
            "Создать закладку");
    private JButton deleteTabBtn = ButtonsFactory.createToolButton("deleteTab",
            "Удалить закладку");
    private JButton createElementBtn = ButtonsFactory.createToolButton("createElement",
            "Создать элемент");
    private JButton deleteElementBtn = ButtonsFactory.createToolButton("deleteElement",
            "Удалить элемент");
    private SaveButton saveBtn = new SaveButton("Save", "Изменить элемент");
    private JButton copyElementBtn = ButtonsFactory.createToolButton("copyElement",
            "Копировать элемент в буфер");
    private JButton pasteElementBtn = ButtonsFactory.createToolButton("pasteElement",
            "Вставить элемент из буфера");
    private JButton printElementBtn = ButtonsFactory.createToolButton("printElement",
            "Печать элемента");
    private JButton debugElementBtn = ButtonsFactory.createToolButton("debugElement",
            "Проверить");

    public ServiceBrowser() {
        super(new BorderLayout());
        init();
    }

    public void setInspector(Frame owner) {
        if (inspector == null) {
            inspector = new PropertyInspector(owner, true);
            DesignerInternalDialog insp = new DesignerInternalDialog("Инспектор",
                    kz.tamur.rt.Utils.getImageIcon("inspector"));
            insp.addContent(inspector.getContentPane());
            inspector.setTopLevelComponent(insp);
            inspector.getModel().addPropertyListener(saveBtn);
            splitPane.setRightComponent(insp);
            splitPane.setDividerLocation(0.7);
            splitPane.validate();
            loadServices();
        }
    }

    private void init() {
        toolBar.add(createTabBtn);
        createTabBtn.addActionListener(this);
        toolBar.add(deleteTabBtn);
        deleteTabBtn.addActionListener(this);
        toolBar.addSeparator(new Dimension(10,10));
        toolBar.add(createElementBtn);
        createElementBtn.addActionListener(this);
        toolBar.add(deleteElementBtn);
        deleteElementBtn.addActionListener(this);
        toolBar.add(copyElementBtn);
        copyElementBtn.addActionListener(this);
        toolBar.add(pasteElementBtn);
        pasteElementBtn.addActionListener(this);
        pasteElementBtn.setEnabled(false);
        toolBar.add(printElementBtn);
        toolBar.add(debugElementBtn);
        toolBar.addSeparator(new Dimension(10,10));
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(this);
        toolBar.add(saveBtn);
        tabbedPane.setFont(Utils.getDefaultFont());
        splitPane.setLeftComponent(tabbedPane);
        add(toolBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    void loadServices() {
        try {
            final Kernel krn = Kernel.instance();
            KrnClass srvCls = krn.getClassByName("ServiceManager");
            srvObj = krn.getClassObjects(srvCls, 0)[0];
            KrnObject lang = krn.getInterfaceLanguage();
            int langId = (lang != null) ? lang.id : 0;
            Element xml = null;
            byte[] data = krn.getBlob(srvObj, "config", 0, 0, 0);
            if (data.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                SAXBuilder b = new SAXBuilder();
                xml = b.build(is).getRootElement();
                is.close();
                srvManagerComp = Factories.instance().create(xml, Mode.DESIGN);
            } else {
                xml = new Element("Component");
                xml.setAttribute("class", "ServiceManager");
                srvManagerComp = Factories.instance().create(xml, Mode.DESIGN);
            }
            treeList.clear();
            PropertyValue pv = srvManagerComp.getPropertyValue(
                    srvManagerComp.getProperties().getChild("children"));
            if (!pv.isNull()) {
                List roots = pv.elementValue().getChildren();
                for (int i = 0; i < roots.size(); i++) {
                    Element child = (Element) roots.get(i);
                    OrGuiComponent c = Factories.instance().create(child, Mode.DESIGN);
                    addServiceRoot(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void addServiceRoot(OrGuiComponent c) {
        if (c == null) {
            c = Factories.instance().create("ServiceRoot");
            PropertyHelper.addProperty(new PropertyValue(c.getXml(),
                    srvManagerComp.getProperties().getChild("children")),
                    srvManagerComp.getXml());

        }
        PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("title"));
        if (!pv.isNull()) {
            String title = pv.stringValue();
            ServicesTreeModel model = new ServicesTreeModel(c);
            ServicesTree tree = new ServicesTree();
            tree.setCellRenderer(new ServicesTreeRenderer());
            tree.setModel(model);
            tree.addPropertyListener(inspector);
            inspector.getModel().addPropertyListener(model);
            treeList.add(tree);
            tabbedPane.addTab(title, tree);
            model.setTreeParentTab(tabbedPane);
            tabbedPane.revalidate();
        }
    }

    private void addServiceElement(OrGuiComponent c) {
        ServicesTree tree = (ServicesTree)treeList.get(
                tabbedPane.getSelectedIndex());
        OrGuiComponent parent = (OrGuiComponent)tree.getLastSelectedPathComponent();
        if (parent instanceof ServiceRoot) {
            if (c == null) {
                c = Factories.instance().create("ServiceSection");
            }
            ((ServiceRoot)parent).addServiceSection(c, false);
            ((ServicesTreeModel)tree.getModel()).fireServicesTreeModelChanged(c);
        } else if (parent instanceof ServiceSection) {
            if (c == null) {
                c = Factories.instance().create("OrService");
            }
            ((ServiceSection)parent).addService(c, false);
            ((ServicesTreeModel)tree.getModel()).fireServicesTreeModelChanged(c);
        } else if (parent instanceof OrService) {
            if (c == null) {
                c = Factories.instance().create("ServiceAction");
            }
            ((OrService)parent).addAction(c, false);
            ((ServicesTreeModel)tree.getModel()).fireServicesTreeModelChanged(c);
        }
    }

    void save() {
        try {
            final Kernel krn = Kernel.instance();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLOutputter out = new XMLOutputter();
            out.setEncoding("UTF-8");
            out.output(srvManagerComp.getXml(), os);
            os.close();
            krn.setBlob(srvObj.id, srvObj.classId, "config", 0, os.toByteArray(), 0, 0);
            saveBtn.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTab() {
        ServicesTree tree =
                (ServicesTree)treeList.get(tabbedPane.getSelectedIndex());
        ServicesTreeModel model = (ServicesTreeModel)tree.getModel();
        ServiceRoot root = (ServiceRoot)model.getRoot();
        PropertyValue pv = root.getPropertyValue(
                root.getProperties().getChild("title"));
        String title = "Не определён";
        if (!pv.isNull()) {
            title = pv.stringValue();
        }
        int res = -1;
        res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.CONFIRM_MESSAGE,
                "Подтвердите удаление закладки \"" + title + "\"!");
        if (res != ButtonsFactory.BUTTON_NOACTION &&
                res == ButtonsFactory.BUTTON_YES) {
            res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.QUESTION_MESSAGE, "Удаление закладки приведёт к удалению\n" +
                    "всех подчинённых элементов!\n" + "Продолжить?");
            if (res != ButtonsFactory.BUTTON_NOACTION &&
                    res == ButtonsFactory.BUTTON_YES) {
                int idx = tabbedPane.getSelectedIndex();
                tabbedPane.remove(idx);
                treeList.remove(tree);
                PropertyHelper.removeProperty(
                        new PropertyValue(root.getXml(),
                                srvManagerComp.getProperties().getChild("children")),
                        srvManagerComp.getXml());
                tabbedPane.validate();
                if (!saveBtn.isEnabled()) {
                    saveBtn.setEnabled(true);
                }
            }
        }
    }

    private void deleteElement() {
        ServicesTree tree = (ServicesTree)treeList.get(
                tabbedPane.getSelectedIndex());
        OrGuiComponent c = (OrGuiComponent)tree.getLastSelectedPathComponent();
        TreePath path = tree.getSelectionPath();
        OrGuiComponent parent =
                (OrGuiComponent)path.getParentPath().getLastPathComponent();
        String title = null;
        PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("title"));
        if (!pv.isNull()) {
            title = pv.stringValue();
        }
        int res = -1;
        res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.CONFIRM_MESSAGE,
                "Подтвердите удаление элемента \"" + title +"\"?");
        if (res != ButtonsFactory.BUTTON_NOACTION &&
                res == ButtonsFactory.BUTTON_YES) {
            if (parent instanceof OrService) {
                ((OrService)parent).deleteAction(c);
            } else if (parent instanceof ServiceSection) {
                ((ServiceSection)parent).deleteService(c);
            } else if (parent instanceof ServiceRoot) {
                ((ServiceRoot)parent).deleteSection(c);
            }
            ServicesTreeModel model = (ServicesTreeModel)tree.getModel();
            model.fireServicesTreeModelChanged(parent);
            if (!saveBtn.isEnabled()) {
                saveBtn.setEnabled(true);
            }
        }
    }

    private void copyElement() {
        ServicesTree tree = (ServicesTree)treeList.get(
                tabbedPane.getSelectedIndex());
        OrGuiComponent c = (OrGuiComponent)tree.getLastSelectedPathComponent();
        Element newXml = null;
        if (c != null) {
            newXml = (Element)c.getXml().clone();
        } else {
            MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.ERROR_MESSAGE,
                    "Отсутствует элемент для копирования!");
            return;
        }
        try {
            StringWriter sw = new StringWriter();
            XMLOutputter out = new XMLOutputter();
            out.setEncoding("UTF-8");
            out.output(newXml, sw);
            sw.close();
            Clipboard clip = getToolkit().getSystemClipboard();
            StringSelection ss = new StringSelection(sw.toString());
            clip.setContents(ss, null);
            pasteElementBtn.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pasteElement() {

        final int ROOT = 0;
        final int SECTION = 1;
        final int SERVICE = 2;

        Clipboard clip = getToolkit().getSystemClipboard();
        OrGuiComponent newComp = null;
        String title = null;
        ServicesTree tree = (ServicesTree)treeList.get(
                tabbedPane.getSelectedIndex());
        OrGuiComponent parent =
                (OrGuiComponent)tree.getLastSelectedPathComponent();
        int reciver = -1;

        if (parent instanceof ServiceRoot) {
            reciver = ROOT;
        } else if (parent instanceof ServiceSection) {
            reciver = SECTION;
        } else if (parent instanceof OrService) {
            reciver = SERVICE;
        }

        try {
            DataFlavor df = DataFlavor.stringFlavor;
            StringReader reader = (StringReader)df.getReaderForText(
                    clip.getContents(null));
            Element xml = new SAXBuilder().build(reader).getRootElement();
            newComp = Factories.instance().create(xml, Mode.DESIGN);
            if (newComp != null) {
                PropertyValue pv = newComp.getPropertyValue(
                        newComp.getProperties().getChild("title"));
                if (!pv.isNull()) {
                    title = pv.stringValue() + " (копия)";
                }
                newComp.setPropertyValue(new PropertyValue(title,
                        newComp.getProperties().getChild("title")));
                if (reciver != -1) {
                    switch(reciver) {
                        case ROOT:
                            if (newComp instanceof ServiceSection) {
                                ((ServiceRoot)parent).addServiceSection(newComp, true);
                            } else {
                                MessagesFactory.showMessageDialog(
                                        (Frame)getTopLevelAncestor(),
                                        MessagesFactory.ERROR_MESSAGE,
                                        "В буфере обмена элемент, несоответствующий\n" +
                                        "выбранной позиции для подчинения!");
                            }
                            break;
                        case SECTION:
                            if (newComp instanceof OrService) {
                                ((ServiceSection)parent).addService(newComp, true);
                            } else {
                                MessagesFactory.showMessageDialog(
                                        (Frame)getTopLevelAncestor(),
                                        MessagesFactory.ERROR_MESSAGE,
                                        "В буфере обмена элемент, несоответствующий\n" +
                                        "выбранной позиции для подчинения!");
                            }
                            break;
                        case SERVICE:
                            if (newComp instanceof ServiceAction) {
                                ((OrService)parent).addAction(newComp, true);
                            } else {
                                MessagesFactory.showMessageDialog(
                                        (Frame)getTopLevelAncestor(),
                                        MessagesFactory.ERROR_MESSAGE,
                                        "В буфере обмена элемент, несоответствующий\n" +
                                        "выбранной позиции для подчинения!");
                            }
                            break;
                    }
                    ServicesTreeModel model = (ServicesTreeModel)tree.getModel();
                    model.fireServicesTreeModelChanged(parent);
                    if (!saveBtn.isEnabled()) {
                        saveBtn.setEnabled(true);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        pasteElementBtn.setEnabled(false);
        /*ServicesTree tree = (ServicesTree)treeList.get(
                tabbedPane.getSelectedIndex());
        OrGuiComponent c = (OrGuiComponent)tree.getLastSelectedPathComponent();
        TreePath path = tree.getSelectionPath();
        OrGuiComponent parent =
                (OrGuiComponent)path.getParentPath().getLastPathComponent();
        String title = null;
        PropertyValue pv = c.getPropertyValue(c.getProperties().getChild("title"));
        if (!pv.isNull()) {
            title = pv.stringValue() + " (копия)";
        }
        if (parent instanceof OrService) {
            ((OrService)parent).addAction(newComp, false);
        } else if (parent instanceof ServiceSection) {
            ((ServiceSection)parent).addService(newComp, false);
        } else if (parent instanceof ServiceRoot) {
            ((ServiceRoot)parent).addServiceSection(newComp, false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == createTabBtn) {
            addServiceRoot(null);
        } else if (src == createElementBtn) {
            addServiceElement(null);
        } else if (src == saveBtn) {
            save();
        } else if (src == deleteTabBtn) {
            deleteTab();
        } else if (src == deleteElementBtn) {
            deleteElement();
        } else if (src == copyElementBtn) {
            copyElement();
        } else if (src == pasteElementBtn) {
            pasteElement();
        }
    }

    class SaveButton extends ButtonsFactory.DesignerToolButton implements PropertyListener {

        public SaveButton(String iconName, String toolTip) {
            super(iconName, toolTip);
        }

        public void propertyModified(OrGuiComponent c) {
            setEnabled(true);
        }

        public void propertyModified(OrGuiComponent c, int propertyEvent) {
            setEnabled(true);
        }
    } */


