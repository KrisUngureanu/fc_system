package kz.tamur.guidesigner.procdesigner;

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
import kz.tamur.lang.ErrRecord;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 15.06.2005
 * Time: 9:30:57
 * To change this template use File | Settings | File Templates.
 */
public class ProcedureFrame extends JPanel implements PropertyChangeListener, ActionListener,
        TreeSelectionListener {

    private JSplitPane splitPane = new JSplitPane();
    private Tabbed tabbedPane = new Tabbed();

    private ProcTree procedureTree;
    private ProcTree functionTree;
    private ProcTree packageTree;
    private ProcTree packageBodyTree;

    private ExpressionEditor exprEditor = new ExpressionEditor("",false);
    private JPanel emptyPanel = new JPanel();
    private CardLayout layout = new CardLayout();
    private JPanel workPanel = new JPanel(layout);
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить");
    private ProcTree currTree=null;


    private int oldIndex;

    private boolean canEdit = false;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    public static String PROCEDURE="PROCEDURE";
    public static String FUNCTION="FUNCTION";
    public static String PACKAGE="PACKAGE";
    public static String PACKAGE_BODY="PACKAGE BODY";
    public static String SUCCESSFUL_COMPIL="УСПЕШНАЯ КОМПИЛЯЦИЯ";
    
    public ProcedureFrame() {
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
            procedureTree = getProcTree(PROCEDURE);
            functionTree = getProcTree(FUNCTION);
//            packageTree = getProcTree(PACKAGE);
//            packageBodyTree = getProcTree(PACKAGE_BODY);
        if (procedureTree != null) {
            procedureTree.addTreeSelectionListener(this);
            tabbedPane.addTab(procedureTree.getModel().getRoot().toString(),
                    new JScrollPane(procedureTree));
        }
        if (functionTree != null) {
            functionTree.addTreeSelectionListener(this);
            tabbedPane.addTab(functionTree.getModel().getRoot().toString(),
                    new JScrollPane(functionTree));
        }
        if (packageTree != null) {
            packageTree.addTreeSelectionListener(this);
            tabbedPane.addTab(packageTree.getModel().getRoot().toString(),
                    new JScrollPane(packageTree));
        }
        if (packageBodyTree != null) {
            packageBodyTree.addTreeSelectionListener(this);
            tabbedPane.addTab(packageBodyTree.getModel().getRoot().toString(),
                    new JScrollPane(packageBodyTree));
        }
        tabbedPane.setFont(Utils.getDefaultFont());
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ProcTree tree = null;
                int idx = tabbedPane.getSelectedIndex();
                if (idx == 0) {
                    tree = procedureTree;
                } else if (idx == 1) {
                    tree = functionTree;
                } else if (idx == 2) {
                    tree = packageTree;
                } else if (idx == 3) {
                    tree = packageBodyTree;
                }
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
                    } else if(currTree!=null){
                        if (currTree.saveList.size() > 0) {
                            Iterator it = currTree.saveList.iterator();
                            while(it.hasNext()) {
                                ProcNode node = (ProcNode)it.next();
                                node.reloadExpression();
                                node.setModify(false);
                            }
                        }
                        currTree.saveList.clear();
                        saveBtn.setEnabled(false);
                    }
                }
                oldIndex = tabbedPane.getSelectedIndex();
                exprEditor.setExpression("");
                layout.show(workPanel, "emptyArea");
                if (tree != null && tree.getSelectedNode()!=null) {
                	valueChanged(new TreeSelectionEvent(tree,null,false,null, null));
                }
                currTree=tree;
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

    private ProcTree getProcTree(String type) {
        ProcTree procTree = null;
        Kernel krn = Kernel.instance();
        try {
            java.util.List<String> procNames = krn.getListProcedure(PACKAGE_BODY.equals(type)?PACKAGE:type);
            ProcNode inode = new ProcNode(type,type, procNames, 0);
            procTree = new ProcTree(exprEditor,inode,type);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return procTree;
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.33);
        validate();
    }

    public void valueChanged(TreeSelectionEvent e) {
        DesignerTree tree = (DesignerTree) e.getSource();
        if (tree != null) {
        	ProcNode selNode=(ProcNode) tree.getSelectedNode();
            if (selNode!=null && selNode.isLeaf()) {
                exprEditor.setExpression(
                        ((ProcNode)tree.getSelectedNode()).getExpressionText());
                layout.show(workPanel, "expressionArea");
                if(selNode.isModify() && !saveBtn.isEnabled())
                	saveBtn.setEnabled(true);
                else if(!selNode.isModify())
                	saveBtn.setEnabled(false);
            } else {
                layout.show(workPanel, "emptyArea");
            }
        }
        exprEditor.debugProcedure(null);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        ProcTree tree = null;
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) {
            tree = procedureTree;
        } else if (idx == 1) {
            tree = functionTree;
        } else if (idx == 2) {
            tree = packageTree;
        } else if (idx == 3) {
            tree = packageBodyTree;
        }
        if (tree != null) {
            ProcNode node = (ProcNode)tree.getSelectedNode();
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
            tree.saveList.add(node);
        }
    }

    private void saveTree() {
        if (currTree!=null && currTree.saveList.size() > 0) {
            ProcNode selNode = (ProcNode)currTree.getSelectedNode();
            Iterator it = currTree.saveList.iterator();
        	ArrayList<ErrRecord> errors=new ArrayList<ErrRecord>();
        	int i=0;
            while(it.hasNext()) {
            	ProcNode node = (ProcNode)it.next();
                if(selNode!=null && selNode.isLeaf() && !selNode.equals(node))
                	continue;
                String resNode = node.save();
                String res=resNode;
               	it.remove();
            	node.setModify(false);
                if(!"".equals(res)){
                	errors.add(new ErrRecord("",i,node.toString()+"->"+res));
                	node.setValid(false);
                }else{
                	errors.add(new ErrRecord("",i,node.toString()+"->"+SUCCESSFUL_COMPIL));
                	node.setValid(true);
                }
                i++;
            }
        	exprEditor.debugProcedure(errors);
        }
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
                } else if (idx == 0 || idx == 1) {
                    mess = "Дерево \"" + tabbedPane.getTitleAt(3) +
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
