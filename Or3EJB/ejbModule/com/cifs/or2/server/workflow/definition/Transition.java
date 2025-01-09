package com.cifs.or2.server.workflow.definition;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:13:14
 * To change this template use File | Settings | File Templates.
 */
public interface Transition extends DefinitionObject {
  Node getFrom();
  Node getTo();
    void setTo(Node node);
}
