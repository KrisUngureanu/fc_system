package kz.tamur.comps;

import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeControlAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrTreeControl2 extends OrTree2 implements OrGuiComponent {

    private static final long serialVersionUID = 1L;

    public static PropertyNode PROPS = new TreePropertyRoot();
    private boolean multiSelection = false;
    
    OrTreeControl2(Element xml, int mode, OrFrame frame) throws KrnException {
        super(xml, mode, frame);
        this.xml = xml;
        setRootVisible(false);
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        if (mode == Mode.RUNTIME) {
            kz.tamur.rt.Utils.setComponentTabFocusCircle(this);
            addFocusListener(new DefaultFocusAdapter(this));
            // Создаем адаптер
            setAdapter(new TreeControlAdapter2(frame, this, false));
            addTreeExpansionListener(new TreeControlExpansionListener());
            setLargeModel(true);
        }
        multiSelection = getPropertyValue(PROPS.getChild("pov").getChild("multiselection")).booleanValue();
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        TreeModel model = getModel();
        TreeNode root = (TreeNode) model.getRoot();
        if (root != null && root.getChildCount() > 0) {
            TreePath path = getSelectionPath();
            TreeAdapter.Node sn = (path != null) ? (TreeAdapter.Node) path.getLastPathComponent() : null;
            TreeAdapter.Node n = (TreeAdapter.Node) root.getChildAt(0);
            n.reset();
            if (sn != null) {
                path = n.find(sn.getObject(), false);
                setSelectionPath(path);
            }
        }
        Utils.processBorderProperties(this, frame);
    }

    public class TreeControlExpansionListener implements TreeExpansionListener {

        public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
            getAdapter().nodeExpanded((Node) e.getPath().getLastPathComponent());
        }

        public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
            getAdapter().nodeCollapsed((Node) e.getPath().getLastPathComponent());
        }
    }
    
    @Override
    public void setPropertyValue(PropertyValue value) {
        super.setPropertyValue(value);
        String name = value.getProperty().getName();
        if("multiselection".equals(name)) {
            multiSelection = value.booleanValue();
        }
    }
    
    /**
     * Компонент поддерживает мультивыбор значений?
     * @return <code>true</code> если поддерживает.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }
}
