package kz.tamur.server.wf;

import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.EventType;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 14.12.2005
 * Time: 13:36:51
 * To change this template use File | Settings | File Templates.
 */
public class FlowState {
    public Node node;
    public EventType eventType;

    public FlowState(Node node, EventType eventType) {
        this.node = node;
        this.eventType = eventType;
    }
}
