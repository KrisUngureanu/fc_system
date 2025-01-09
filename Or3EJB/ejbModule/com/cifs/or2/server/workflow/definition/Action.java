package com.cifs.or2.server.workflow.definition;

import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:25:20
 * To change this template use File | Settings | File Templates.
 */
public interface Action {
    EventType getEventType();
    ASTStart getExpression();
    void setExpression(String expr);
}
