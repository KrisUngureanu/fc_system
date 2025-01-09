package kz.tamur.guidesigner.service;

import com.cifs.or2.server.workflow.definition.EventType;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 15.09.2004
 * Time: 17:44:50
 * To change this template use File | Settings | File Templates.
 */
public class NodeEventType implements Serializable, Comparable {
    private static Map byName = new HashMap();
    private static Map byEventType = new HashMap();
    private EventType eventType;
    private String title;

    public static NodeEventType forName(String name) {
        return (NodeEventType)byName.get(name);
    }
    public static NodeEventType forEventType(EventType eventType) {
        return (NodeEventType)byEventType.get(eventType);
    }
    public NodeEventType(EventType eventType, String title) {
        this.eventType = eventType;
        this.title = title;
        byName.put(title, this);
        byEventType.put(eventType, this);
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getTitle() {
        return title;
    }

    public int compareTo(Object o) {
        return eventType.compareTo(((NodeEventType)o).eventType);
    }
}
