package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.FigLine;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 19.03.2005
 * Time: 16:34:22
 * To change this template use File | Settings | File Templates.
 */
public class FigLineEdge extends FigLine {
    private static int nextId;
    private String id;
    private String name;
    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public FigLineEdge(int i, int i1, int i2, int i3, Color color,boolean enabled) {
        super(i, i1, i2, i3, color);
        id = "" + (++nextId);
        this.enabled=enabled;
    }

    public FigLineEdge(int i, int i1, int i2, int i3,boolean enabled) {
        super(i, i1, i2, i3);
        id = "" + (++nextId);
        this.enabled=enabled;
    }
    public FigLineEdge(int i, int i1, int i2, int i3,String id,boolean enabled) {
        super(i, i1, i2, i3);
        this.id = id;
        int ind = Integer.parseInt(id);
        if (nextId < ind) {
            nextId = ind;
        }
        this.enabled=enabled;
    }
    public String getTipString(MouseEvent me) {
        if(!enabled)
            return null;
        return "Связь";
    }

}
