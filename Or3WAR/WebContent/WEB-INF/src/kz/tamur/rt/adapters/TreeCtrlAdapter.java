package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 26.11.2004
 * Time: 14:54:16
 * To change this template use File | Settings | File Templates.
 */
public class TreeCtrlAdapter extends TreeAdapter {

    public TreeCtrlAdapter(OrFrame frame, OrTreeComponent c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
        // Формула для вычисления потомков
        PropertyNode chExprNode = c.getProperties().getChild("ref").getChild("childrenExpr");
        if (chExprNode != null) {
            PropertyValue pv = tree.getPropertyValue(chExprNode);
            if (!pv.isNull()) {
                String str = pv.stringValue();
                dataRef.setChildrenExpr(str);
            }
        }
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this)
            check(e);
    }

    public void clear() {
    }

    public void treeSelectionChanged() {
        try {
            if (!selfChange) {
                Node n = getSelectedNode();
                if (n != null) {
                	boolean calcOwner = OrCalcRef.setCalculations(); 
                	try {
	                    OrRef.Item item = dataRef.getItem(0);
	                    if (item == null)
	                        dataRef.insertItem(0, n.getObject(), this, this, false);
	                    else
	                        dataRef.changeItem(n.getObject(), this, this);
                	} catch (Exception e) {
                		e.printStackTrace();
                	} finally {
	        			if (calcOwner)
	        				OrCalcRef.makeCalculations();
                	}
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
        	Node root = getRoot();
        	if (ref.getAttribute() != null) {
            	OrRef.Item item = ref.getItem(langId);
                try {
                    selfChange = true;
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
