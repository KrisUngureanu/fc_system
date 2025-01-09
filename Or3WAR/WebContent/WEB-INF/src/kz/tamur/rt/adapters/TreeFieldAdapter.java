package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.*;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.or3.client.comps.interfaces.OrTreeFieldComponent;

import javax.swing.tree.*;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 26.11.2004
 * Time: 14:54:16
 */
public class TreeFieldAdapter extends TreeAdapter {
    OrTreeFieldComponent treeField;
    private KrnObject object;

    public TreeFieldAdapter(OrFrame frame, OrTreeFieldComponent c, boolean isEditor) throws KrnException {
        super(frame, c.getOrTree(), isEditor);
        treeField = c;
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

    public void filterParamChanged(String fuid, String pid, List<?> values) {
        super.filterParamChanged(fuid, pid, values);
        if (value == null || value instanceof KrnObject) {
            setObject((KrnObject) value);
            update();
        }
    }

    private void update() {
        if (dataRef != null) {
            OrRef.Item item = dataRef.getItem(0);
            setObject((item != null) ? (KrnObject) item.getCurrent() : null);
        }
        String s = "";
        TreeAdapter.Node root = getRoot();
        if (root != null && object != null) {
            TreePath path = root.find(object, true);
            if (path == null) {
                path = root.find(object, false);
            }
            if (!treeField.isTitleMode()) {
                if (path != null) {
                    s = path.getLastPathComponent().toString();
                }
                treeField.setText(s);
            } else {
                Object[] nodes = null;
                if (path != null)
                    nodes = path.getPath();
                if (nodes != null && nodes.length > 0) {
                    treeField.getTree().setModel(setTitleTree(nodes));
                    expandAll();
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodes[0]);
                    StringBuilder title = new StringBuilder();
                    for (int i = 1; i < nodes.length; i++) {
                        if (i > 1) {
                            for (int k = 0; k < 3 * i + 3; k++)
                                title.append("&nbsp;");
                        }
                        title.append(nodes[i].toString()).append("<br>");

                        node.add(new DefaultMutableTreeNode(nodes[i]));
                        node = (DefaultMutableTreeNode) node.getChildAt(0);
                    }

                    treeField.setText(title.toString());
                } else {
                    treeField.getTree().setModel(null);
                    treeField.setText("");
                }
            }
        } else {
            if (treeField.isTitleMode()) {
                treeField.getTree().setModel(null);
            }
            treeField.setText("");
        }
    }

    public void setValue(TreeAdapter.Node n) throws Exception {
        StringBuilder title = new StringBuilder();
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
                } else
                	n = null;
            }
        } else {
            obj = (KrnObject) doBeforeModification(null);
        }
        if (n != null) {
            if (n.trec2 != null) {
                StringTokenizer st = new StringTokenizer(n.toString(), ":");
                title.append(st.nextToken());
            } else {
                if (!treeField.isTitleMode()) {
                    title.append(n.toString());
                } else {
                    TreeNode[] nodes = n.getPath();
                    if (nodes.length > 0) {
                        treeField.getTree().setModel(setTitleTree(nodes));
                        expandAll();
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodes[0]);
                        for (int i = 1; i < nodes.length; i++) {
                            if (i > 1) {
                                for (int k = 0; k < 3 * i + 3; k++)
                                    title.append("&nbsp;");
                            }
                            title.append(nodes[i].toString()).append("<br>");
                            node.add(new DefaultMutableTreeNode(nodes[i]));
                            node = (DefaultMutableTreeNode) node.getChildAt(0);
                        }
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
            try {
                if (item != null && obj != null) {
                	ref.changeItem(obj, this, this);
                } else if (item != null && obj == null) {
                	ref.deleteItem(this, this);
                } else if (obj != null) {
                	ref.insertItem(0, obj, this, this, false);
                }
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
        	}
        }
        setObject(obj);
        treeField.setText(title.toString());
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
        while (node.getChildCount() > 0 && !node.getChildAt(0).isLeaf()) {
            node = (DefaultMutableTreeNode) node.getChildAt(0);
        }
        TreePath path = new TreePath(node.getPath());
        treeField.getTree().expandPath(path);
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
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterModAction, vc, this, new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(treeField, ex.getMessage(), "Действие после модификации");
            	log.error("Ошибка при выполнении формулы 'Действие после модификации' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                    OrCalcRef.makeCalculations();
            }
        }
    }

    public KrnObject getObject() {
        return object;
    }

    public void setObject(KrnObject object) {
        this.object = object;
    }

    public void clearValue() {
        try {
            setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
