package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.CursorToolkit;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.NodeFinder;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.StringPattern;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.orlang.ClientOrLang;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 26.11.2004
 * Time: 14:54:16
 */
public class TreeFieldAdapter extends TreeAdapter implements ActionListener {

    OrTreeField treeField;
    private KrnObject object;
    private OrCellEditor cellEditor;
    private String title = "";
    private boolean isFolderSelect = false;
    private int dialogWidth;
    private int dialogHeight;

    private String searchString;
    protected NodeFinder finder = new NodeFinder();
    private boolean hasClearBtn = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public TreeFieldAdapter(UIFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, ((OrTreeField) c).getOrTree(), isEditor);
        this.treeField = (OrTreeField) c;
        this.treeField.getOrTree().setTreeFieldButton(this.treeField);
        PropertyNode pn = treeField.getProperties().getChild("pov");
        PropertyNode node = pn.getChild("activity").getChild("editable");
        PropertyValue pv = treeField.getPropertyValue(node);
        if (pv != null && !pv.isNull()) {
            treeField.setEnabled(!pv.booleanValue());
        }
        node = treeField.getProperties().getChild("title");
        pv = treeField.getPropertyValue(node);
        if (!pv.isNull()) {
            title = pv.stringValue();
        }
        node = pn.getChild("dialogSize").getChild("dialogWidth");
        pv = treeField.getPropertyValue(node);
        if (!pv.isNull()) {
            dialogWidth = pv.intValue();
        } else {
            dialogWidth = ((Integer) node.getDefaultValue()).intValue();
        }
        node = pn.getChild("dialogSize").getChild("dialogHeight");
        pv = treeField.getPropertyValue(node);
        if (!pv.isNull()) {
            dialogHeight = pv.intValue();
        } else {
            dialogHeight = ((Integer) node.getDefaultValue()).intValue();
        }

        treeField.addActionListener(this);
        kz.tamur.rt.Utils.setComponentFocusCircle(treeField);
        node = treeField.getProperties().getChild("view").getChild("folderSelect");
        pv = treeField.getPropertyValue(node);
        if (!pv.isNull()) {
            isFolderSelect = pv.booleanValue();
        } else {
            isFolderSelect = ((Boolean) treeField.getProperties().getChild("view").getChild("folderSelect").getDefaultValue())
                    .booleanValue();
        }
        treeField.getOrTree().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreeNode tn = (TreeNode) e.getPath().getLastPathComponent();
                DesignerDialog dlg = (DesignerDialog) treeField.getOrTree().getTopLevelAncestor();
                if (dlg != null) {
                    dlg.setOkEnabled(isFolderSelect || tn.isLeaf());
                }
            }
        });

        if (treeField.isClearBtnExists()) {
            this.treeField.addDeleteMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        clearValue();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        this.treeField.setXml(null);
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (!selfChange && e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == rootRef || ref == dataRef || ref == rootCalcRef) {
                update();
            } else if (ref == activityRef) {
            }
        }
    }

    private void update() {
        if (dataRef != null) {
            OrRef.Item item = dataRef.getItem(0);
            object = (item != null) ? (KrnObject) item.getCurrent() : null;
        }
        String s = "";
        TreeAdapter.Node root = getRoot();
        if (root != null && object != null) {
            TreePath path = root.find(object, true);
            if (path == null) {
                path = root.find(object, false);
            }
            if (!treeField.isTitleMode()) {
                if (path != null)
                    s = path.getLastPathComponent().toString();
                treeField.setText(s);
            } else {
                Object[] nodes = null;
                if (path != null)
                    nodes = path.getPath();
                if (nodes != null && nodes.length > 0) {
                    treeField.getTree().setModel(setTitleTree(nodes));
                    expandAll();
                } else {
                    treeField.getTree().setModel(null);
                }
            }
        } else {
            if (!treeField.isTitleMode()) {
                treeField.setText("");
            } else {
                treeField.getTree().setModel(null);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (treeField.isHelpClick()) {
            treeField.setHelpClick(false);
        } else {
            try {
                JScrollPane sp = new JScrollPane(tree);
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                tree.setOpaque(isOpaque);
                sp.setPreferredSize(new Dimension(dialogWidth, dialogHeight));
                Container top = treeField.getTopLevelAncestor();
                final DesignerDialog dlg;

                if (top instanceof Frame)
                    dlg = new DesignerDialog((Frame) treeField.getTopLevelAncestor(), title, sp, true);
                else if (top instanceof Dialog)
                    dlg = new DesignerDialog((Dialog) treeField.getTopLevelAncestor(), title, sp, true);
                else
                    dlg = null;

                dlg.setLanguage(treeField.getFrame().getInterfaceLang().id);
                if (object != null) {
                    TreePath path = getRoot().find(object, true);
                    if (path == null) {
                        path = getRoot().find(object, false);
                    }
                    if (path != null) {
                        tree.expandPath(path);
                        tree.setSelectionPath(path);
                    }
                }
                MouseAdapter ma = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (e.getClickCount() == 2) {
                            if (getSelectedNode().isLeaf()) {
                                dlg.processOkClicked();
                            }
                        }
                    }
                };
                KeyAdapter ka = new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        super.keyPressed(e);
                        if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                            find(dlg);
                        } else if (e.getKeyCode() == KeyEvent.VK_F3 && e.isShiftDown()) {
                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    CursorToolkit.startWaitCursor(dlg);
                                    TreeNode fnode = finder.findPrev();
                                    if (fnode != null) {
                                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                        if (path != null) {
                                            tree.setSelectionPath(path);
                                            tree.scrollPathToVisible(path);
                                        }
                                        CursorToolkit.stopWaitCursor(dlg);
                                    } else {
                                        CursorToolkit.stopWaitCursor(dlg);
                                        MessagesFactory.showMessageSearchFinished(dlg);
                                    }
                                }
                            });
                            t.start();
                        } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    CursorToolkit.startWaitCursor(dlg);
                                    TreeNode fnode = finder.findNext();
                                    if (fnode != null) {
                                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                        if (path != null) {
                                            tree.setSelectionPath(path);
                                            tree.scrollPathToVisible(path);
                                        }
                                        CursorToolkit.stopWaitCursor(dlg);
                                    } else {
                                        CursorToolkit.stopWaitCursor(dlg);
                                        MessagesFactory.showMessageSearchFinished(dlg);
                                    }
                                }
                            });
                            t.start();

                        }
                    }
                };
                tree.addMouseListener(ma);
                tree.addKeyListener(ka);
                dlg.show();
                tree.removeMouseListener(ma);
                tree.removeKeyListener(ka);
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    setValue(getSelectedNode());
                    if (cellEditor != null) {
                        cellEditor.stopCellEditing();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    clearValue();
                } else {
                    if (cellEditor != null) {
                        cellEditor.cancelCellEditing();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearValue() throws Exception {
        setValue(null);
        if (dataRef != null) {
            OrRef ref = dataRef;
            OrRef.Item item = ref.getItem(0);
            if (item != null && item.getCurrent() != null) {
                ref.deleteItem(this, this);
            }
        }
        treeField.setText("");
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
    }

    public void find(final DesignerDialog parent) {
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        sip.setSearchMethod(ComparisonOperations.CO_CONTAINS);
        if (searchString != null)
            sip.setSearchText(searchString);
        final DesignerDialog dlg = new DesignerDialog(parent, "Поиск элемента", sip);
        dlg.show();

        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    CursorToolkit.startWaitCursor(dlg);
                    searchString = sip.getSearchText();
                    Node node = getRoot();
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                    CursorToolkit.stopWaitCursor(dlg);
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            tree.setSelectionPath(path);
                            tree.scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(parent);
                    }
                }
            });
            t.start();
        }
    }

    private void setValue(TreeAdapter.Node n) throws Exception {
        String title = null;
        KrnObject obj = null;
        if (n != null) {
            KrnObject objBefore = n.getObject();
            try {
                obj = (KrnObject) doBeforeModification(objBefore);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (obj != objBefore) {
                TreePath path = getRoot().find(obj, true);
                if (path == null) {
                    path = getRoot().find(obj, false);
                }

                if (path != null) {
                    n = (Node) path.getLastPathComponent();
                }
            }
        } else {
            doBeforeModification(null);
        }
        if (n != null) {
            if (n.trec2 != null) {
                StringTokenizer st = new StringTokenizer(n.toString(), ":");
                title = st.nextToken();
            } else {
                if (!treeField.isTitleMode()) {
                    title = n.toString();
                } else {
                    TreeNode[] nodes = n.getPath();
                    if (nodes.length > 0) {
                        treeField.getTree().setModel(setTitleTree(nodes));
                        expandAll();
                    } else {
                        treeField.getTree().setModel(null);
                    }
                }
            }
            obj = n.getObject();
        }
        OrRef ref = dataRef;
        if (ref != null) {
            OrRef.Item item = ref.getItem(0);

            boolean calcOwner = OrCalcRef.setCalculations();

            if (item == null)
                ref.insertItem(0, obj, this, this, false);
            else
                ref.changeItem(obj, this, this);

            if (calcOwner)
                OrCalcRef.makeCalculations();
        }
        object = obj;
        treeField.setText(title);
        doAfterModification();
        updateParamFilters(obj);
    }

    private TreeModel setTitleTree(Object[] nodes) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(nodes[0]);
        DefaultMutableTreeNode node = root;
        for (int i = 1; i < nodes.length; i++) {
            node.add(new DefaultMutableTreeNode(nodes[i]));
            node = (DefaultMutableTreeNode) node.getChildAt(0);
        }
        return new DefaultTreeModel(root);
    }

    private void expandAll() {
        DefaultTreeModel model = (DefaultTreeModel) treeField.getTree().getModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
        while (node.getChildCount() > 0 && !node.getChildAt(0).isLeaf())
            node = (DefaultMutableTreeNode) node.getChildAt(0);
        TreePath path = new TreePath(node.getPath());
        treeField.getTree().expandPath(path);
    }

    class OrTreeCellEditor extends OrCellEditor {
        public OrTreeCellEditor() {
            treeField.setHorizontalAlignment(SwingConstants.LEFT);
            treeField.setIcon(null);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valueChanged(new OrRefEvent(dataRef, 0, -1, null));
            treeField.setBackground(table.getBackground());
            treeField.setForeground(table.getForeground());
            treeField.setText((value != null) ? value.toString() : "");
            return treeField;
        }

        public Object getValueFor(Object obj) {
            OrRef.Item item = (OrRef.Item) obj;
            if (item != null) {
                KrnObject val = (KrnObject) item.getCurrent();
                TreeAdapter.Node root = getRoot();
                if (root != null && val != null) {
                    TreePath path = root.find(val, true);
                    if (path == null) {
                        path = root.find(val, false);
                    }
                    if (!treeField.isTitleMode()) {
                        if (path != null)
                            return path.getLastPathComponent().toString();
                    }
                }
            }
            return null;
        }

        public Object getCellEditorValue() {
            return treeField.getText();
        }
    }

    public OrCellEditor getCellEditor() {
        if (cellEditor == null) {
            cellEditor = new TreeFieldAdapter.OrTreeCellEditor();
        }
        return cellEditor;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        treeField.setEnabled(isEnabled);
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            treeField.setText("");
        }
    }

    protected void doAfterModification() throws Exception {
        if (afterModAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p != null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p != null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            orlang.evaluate(afterModAction, vc, this, new Stack<String>());
        }
    }

}
