package com.cifs.or2.server.workflow.definition;

import java.util.Collection;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:12:27
 * To change this template use File | Settings | File Templates.
 */
public interface Node extends DefinitionObject {
    ProcessBlock getProcessBlock();
    Collection getArrivingTransitions();
    Collection getLeavingTransitions();
    Transition createTransition();
    void removeTransition(Transition tr);
    Rectangle getBounds();
    void setBounds(Rectangle rect);
    String getId();
    String getType();
}
