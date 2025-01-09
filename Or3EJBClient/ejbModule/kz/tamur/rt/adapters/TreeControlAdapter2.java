package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 26.11.2004
 * Time: 14:54:16
 */
public class TreeControlAdapter2 extends TreeAdapter2 implements TreeSelectionListener {

    public TreeControlAdapter2(OrFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this) {
            try {
                check(e);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void clear() {
    }

    public void valueChanged(TreeSelectionEvent e) {
        try {
            if (!selfChange) {
                Node n = getSelectedNode();
                if (n != null) {
                    boolean calcOwner = OrCalcRef.setCalculations();
                    OrRef.Item item = dataRef.getItem(0);
                    if (item == null)
                        dataRef.insertItem(0, n.getObject(), this, this, false);
                    else
                        dataRef.changeItem(n.getObject(), this, this);
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                }
                Node[] nodes = getSelectedNodes();
                KrnObject[] objs = new KrnObject[nodes.length];
                for (int i = 0; i < nodes.length; ++i)
                    objs[i] = nodes[i].getObject();
                dataRef.setSelectedItems(objs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void check(OrRefEvent e) throws KrnException {
        if (selfChange)
            return;
        OrRef ref = e.getRef();

        if (ref == dataRef) {
            if (ref.getAttribute() != null) {
                OrRef.Item item = ref.getItem(langId);
                try {
                    selfChange = true;
                    Node root = (Node) getModel().getRoot();
                    if (root != null) {
                        if (item == null || item.getCurrent() == null)
                            setSelectionPath(null);
                        else {
                            TreePath path = root.find((KrnObject) item.getCurrent(), true);
                            if (path == null) {
                                path = root.find((KrnObject) item.getCurrent(), false);
                            }
                            setSelectionPath(path);
                        }
                    }
                } finally {
                    selfChange = false;
                }
            }
        }
    }

    public void setSelectionPath(TreePath path) {
        // tree.setSelectionPath(path);
    }

    public void setSelectedNode(Node node, Object originator) throws KrnException {
        selectedNodes.clear();
        selectedNodes.add(node);
        try {
            selfChange = true;
            dataRef.absolute(node.item.index, originator);
        } finally {
            selfChange = false;
        }
    }

    public void setSelectedNodes(Node[] nodes) {
        selectedNodes.clear();
        int[] inds = new int[nodes.length];
        int i = 0;
        for (Node node : nodes) {
            inds[i++] = node.item.index;
            selectedNodes.add(node);
        }
        if (nodes.length > 0) {
            dataRef.absolute(nodes[nodes.length - 1].item.index, this);
        }
        dataRef.setSelectedItems(inds);
    }

}
