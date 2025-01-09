package kz.tamur.rt.adapters;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrTreeCtrl;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 26.11.2004
 * Time: 14:54:16
 * To change this template use File | Settings | File Templates.
 */
public class TreeCtrlAdapter extends TreeAdapter
        implements TreeSelectionListener {

    public TreeCtrlAdapter(UIFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, (OrTreeCtrl) c, isEditor);
        //tree.setBackground(Utils.getLightSysColor());
        tree.addTreeSelectionListener(this);
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this)
            check(e);
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

    protected void check(OrRefEvent e) {
        if (selfChange)
            return;
        OrRef ref = e.getRef();

        if (ref == dataRef) {
            if (ref.getAttribute() != null) {
                OrRef.Item item = ref.getItem(langId);
                try {
                    selfChange = true;
                    Node root = getRoot();
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
        tree.setSelectionPath(path);
        /*Node n;
        //switch (selectionMode.getIntVal()) {
        //    case 0:
                tree.setSelectionPath(path);
                return;
            case 1:
                n = (path!=null) ? (Node) path.getLastPathComponent() : null;
                if (n != null && n.isLeaf()) {
                    tree.setSelectionPath(path);
                }
                return;
            case 2:
                n = (path!=null) ? (Node) path.getLastPathComponent() : null;
                if (n != null && !n.isLeaf()) {
                    tree.setSelectionPath(path);
                }
                return;
        }   */
    }
}
