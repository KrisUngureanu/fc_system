package kz.tamur.guidesigner.xmldesigner;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.DesignerTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.users.Or3RightsNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 15.06.2005
 * Time: 9:30:57
 * To change this template use File | Settings | File Templates.
 */
public class XmlFrame extends JPanel implements PropertyChangeListener, ActionListener,
        TreeSelectionListener {

    private JSplitPane splitPane = new JSplitPane();
    private Tabbed tabbedPane = new Tabbed();

    private XmlTree createXmlTree;
    private XmlTree parseXmlTree;
    private XmlTree defaultXmlTree;

    private ExpressionEditor exprEditor = new ExpressionEditor("");
    private JPanel emptyPanel = new JPanel();
    private CardLayout layout = new CardLayout();
    private JPanel workPanel = new JPanel(layout);
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить");


    private Set saveList = new HashSet();
    private int oldIndex;

    private boolean canEdit = false;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public XmlFrame() {
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FUNCS_EDIT_RIGHT);

        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBar.add(saveBtn);
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(this);
        try {
            createXmlTree = getXmlTree(Kernel.instance().getClassByName("CreateXmlRoot"));
            parseXmlTree = getXmlTree(Kernel.instance().getClassByName("ParseXmlRoot"));
            //defaultXmlTree = getXmlTree(Kernel.instance().getClassByName("DefaultXmlRoot"));
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (createXmlTree != null) {
            createXmlTree.addTreeSelectionListener(this);
            tabbedPane.addTab(createXmlTree.getModel().getRoot().toString(),
                    new JScrollPane(createXmlTree));
        }
        if (parseXmlTree != null) {
            parseXmlTree.addTreeSelectionListener(this);
            tabbedPane.addTab(parseXmlTree.getModel().getRoot().toString(),
                    new JScrollPane(parseXmlTree));
        }
        if (defaultXmlTree != null) {
            defaultXmlTree.addTreeSelectionListener(this);
            tabbedPane.addTab(defaultXmlTree.getModel().getRoot().toString(),
                    new JScrollPane(defaultXmlTree));
        }
        tabbedPane.setFont(Utils.getDefaultFont());
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (saveBtn.isEnabled()) {
                    String mess = "";
                    //int idx = tabbedPane.getSelectedIndex();
                    //if (idx == 1 || idx == 2) {
                        mess = "Дерево \"" + tabbedPane.getTitleAt(oldIndex) +
                                "\" - модифицировано!\nСохранить изменения?";
                    //} else if (idx == 0 || idx == 2) {
                    //    mess = "Дерево \"" + tabbedPane.getTitleAt(1) +
                    //            "\" - модифицировано!\nСохранить изменения?";
                    //} else if (idx == 0 || idx == 1) {
                    //    mess = "Дерево \"" + tabbedPane.getTitleAt(2) +
                    //            "\" - модифицировано!\nСохранить изменения?";
                    //}
                    int res = MessagesFactory.showMessageDialog(
                            (Frame)getTopLevelAncestor(),
                            MessagesFactory.QUESTION_MESSAGE, mess);
                    if (res == ButtonsFactory.BUTTON_YES) {
                        saveTree();
                    } else {
                        if (saveList.size() > 0) {
                            Iterator it = saveList.iterator();
                            while(it.hasNext()) {
                                XmlNode node = (XmlNode)it.next();
                                node.reloadExpression();
                                node.setModify(false);
                            }
                        }
                        saveList.clear();
                        saveBtn.setEnabled(false);
                    }
                }
                oldIndex = tabbedPane.getSelectedIndex();
                exprEditor.setExpression("");
                layout.show(workPanel, "emptyArea");
            }
        });
        tabbedPane.fireChange();
        splitPane.setDividerSize(10);
        splitPane.setOneTouchExpandable(true);
        splitPane.setLeftComponent(tabbedPane);
        workPanel.add(emptyPanel, "emptyArea");
        exprEditor.addPropertyChangeListener(this);
        workPanel.add(exprEditor, "expressionArea");
        layout.show(workPanel, "emptyArea");
        splitPane.setRightComponent(workPanel);
        splitPane.setDividerLocation(0.5);
        add(toolBar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private XmlTree getXmlTree(KrnClass cls) {
        XmlTree xmlTree = null;
        Kernel krn = Kernel.instance();
        try {
            KrnObject[] objs = krn.getClassObjects(cls, 0);
            String name="";
            if(cls.name.equals("CreateXmlRoot")) name="Web-сервисы других систем";
            if(cls.name.equals("ParseXmlRoot")) name="Мои web-сервисы";
            if(cls.name.equals("DefaultXmlRoot")) name="Общие функции";
            if(objs==null || objs.length==0){
                KrnObject obj=krn.createObject(cls,0);
                krn.setString(obj.id,obj.classId,"name",0,0,name,0);
            }
            KrnObject xmlRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = {xmlRoot.id};
            StringValue[] svs = krn.getStringValues(ids, cls.id, "name",
                    0, false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }
            
            title = name;
            XmlNode inode = new XmlNode(xmlRoot, title, 0);
            xmlTree = new XmlTree(inode);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return xmlTree;
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.3);
        validate();
    }

    public void valueChanged(TreeSelectionEvent e) {
        DesignerTree tree = (DesignerTree) e.getSource();
        if (tree != null) {
            if (tree.getSelectedNode().isLeaf()) {
                exprEditor.setExpression(
                        ((XmlNode)tree.getSelectedNode()).getExpressionText());
                layout.show(workPanel, "expressionArea");
            } else {
                layout.show(workPanel, "emptyArea");
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        DesignerTree tree = null;
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) {
            tree = createXmlTree;
        } else if (idx == 1) {
            tree = parseXmlTree;
        } else if (idx == 2) {
            tree = defaultXmlTree;
        }
        if (tree != null) {
            XmlNode node = (XmlNode)tree.getSelectedNode();
            if ("change".equals(evt.getPropertyName()) ||
                    "insert".equals(evt.getPropertyName()) ||
                    "remove".equals(evt.getPropertyName())) {
                node.setExpressionText(exprEditor.getExpression());
                node.setModify(true);
                saveBtn.setEnabled(canEdit);
            } else if ("copy".equals(evt.getPropertyName())) {
                node.setModify(true);
                saveBtn.setEnabled(canEdit);
            }
            saveList.add(node);
        }
    }

    private void saveTree() {
        if (saveList.size() > 0) {
            Iterator it = saveList.iterator();
            while(it.hasNext()) {
                XmlNode node = (XmlNode)it.next();
                node.save();
                node.setModify(false);
            }
        }
        saveList.clear();
        saveBtn.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == saveBtn) {
            saveTree();
        }
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            if (saveBtn.isEnabled()) {
                String mess = "";
                int idx = tabbedPane.getSelectedIndex();
                if (idx == 1 || idx == 2) {
                    mess = "Дерево \"" + tabbedPane.getTitleAt(0) +
                            "\" - модифицировано!\nСохранить изменения?";
                } else if (idx == 0 || idx == 2) {
                    mess = "Дерево \"" + tabbedPane.getTitleAt(1) +
                            "\" - модифицировано!\nСохранить изменения?";
                } else if (idx == 0 || idx == 1) {
                    mess = "Дерево \"" + tabbedPane.getTitleAt(2) +
                            "\" - модифицировано!\nСохранить изменения?";
                }
                int res = MessagesFactory.showMessageDialog(
                        (Frame)getTopLevelAncestor(),
                        MessagesFactory.QUESTION_MESSAGE, mess);
                if (res == ButtonsFactory.BUTTON_YES) {
                    saveTree();
                    return res;
                } else {
                    return res;
                }
            }
            exprEditor.setExpression("");
            layout.show(workPanel, "emptyArea");
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    class Tabbed extends OrBasicTabbedPane {
        public Tabbed() {
            super();
        }
        public Tabbed(Color foregroundTabs, Color selectedForegroundTab) {
            super();
        }

        public void fireChange() {
            fireStateChanged();
        }
    }


}
