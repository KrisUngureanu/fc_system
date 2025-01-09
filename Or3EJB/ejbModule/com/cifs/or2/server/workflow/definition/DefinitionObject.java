package com.cifs.or2.server.workflow.definition;

import java.util.Collection;

import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:09:24
 * To change this template use File | Settings | File Templates.
 */
public interface DefinitionObject {
    String getName();
    void setName(String name);
    boolean hasName();

    ASTStart getTitle();
    void setTitle(String title);

    String getDescription();
    void setDescription(String desc);

    ProcessDefinition getProcessDefinition();
    Collection getActions();
    Action createAction(EventType eventType, String expression);
    void removeAction(Action action);
}
