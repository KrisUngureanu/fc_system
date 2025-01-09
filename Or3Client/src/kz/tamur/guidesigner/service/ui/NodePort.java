package kz.tamur.guidesigner.service.ui;

import org.tigris.gef.graph.presentation.NetPort;
import org.tigris.gef.graph.GraphModel;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 18:53:05
 * To change this template use File | Settings | File Templates.
 */
public class NodePort extends NetPort {
    public NodePort(StateNode parent) {
        super(parent);
    }

    protected Class defaultEdgeClass(NetPort netPort) {
        return TransitionEdge.class;
    }

    public boolean canConnectTo(GraphModel graphModel, Object o) {
        return true;
    }
}
