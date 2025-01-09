package kz.tamur.guidesigner;

import com.cifs.or2.client.Kernel;
import kz.tamur.comps.ComponentsTreeRenderer;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.props.inspector.PropertyInspector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 18.05.2004
 * Time: 10:03:49
 * To change this template use File | Settings | File Templates.
 */
public class TabbedStateAdapter extends ContainerAdapter implements ChangeListener {

    private PropertyInspector inspector;
    private ComponentsTree componentsTree;
    private EmptyComponent emptyComponent = new EmptyComponent();
    private DesignerFrame df;

    public TabbedStateAdapter(PropertyInspector inspector, ComponentsTree tree, DesignerFrame df) {
        this.inspector = inspector;
        this.componentsTree = tree;
        this.df = df;
    }

    public void stateChanged(ChangeEvent e) {
        ControlTabbedContent tc = (ControlTabbedContent) e.getSource();
        if (tc.isInterface()) {
            if (tc.getComponentCount() >= 1) {
                InterfaceFrame frame = tc.getSelectedFrame();
                if (frame != null) {
                    inspector.setObject(new GuiComponentItem(frame.getRootPanel(), DesignerFrame.instance()));
                    inspector.repaint();
                    if (frame != null) {
                        ((ComponentsTreeRenderer) componentsTree.getCellRenderer()).setFrame(frame);
                        ((ComponentsTreeModel) componentsTree.getModel()).setRoot((OrGuiComponent) frame.getRootPanel());
                    }
                    df.setToolButtonsEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT));
                }
            }
        }
    }

    public void componentAdded(ContainerEvent e) {
        super.componentAdded(e);
    }

    public void componentRemoved(ContainerEvent e) {
        super.componentRemoved(e);
        ControlTabbedContent tc = (ControlTabbedContent) e.getContainer();
        stateChanged(new ChangeEvent(tc));
    }
}
