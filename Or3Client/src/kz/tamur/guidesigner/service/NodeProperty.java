package kz.tamur.guidesigner.service;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 15.09.2004
 * Time: 17:22:26
 * To change this template use File | Settings | File Templates.
 */
public class NodeProperty implements Serializable, Comparable {
    private static Map byName = new HashMap();

    private String name;
    private String title;

    public static NodeProperty forName(String name) {
        return (NodeProperty)byName.get(name);
    }

    public NodeProperty(String name, String title) {
        this.name = name;
        this.title = title;
        byName.put(name, this);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int compareTo(Object o) {
        return name.compareTo(((NodeProperty)o).name);
    }
}
