package com.cifs.or2.server.workflow.definition;

import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:08:31
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessDefinition extends ProcessBlock {
    long getId();
    StartState getStartState();
    EndState getEndState();
    ASTStart getResponsibleExpr();
    ASTStart getChopperId();
    ASTStart getConflict();
    ASTStart getUiExpressionInf();
    String getActorExpressionInf();
    ASTStart getObjExpressionInf();
    String getUiTypeInf();
    ASTStart getInspectors();
    ASTStart getTitleExpr();
    StartState createStartState();
    byte[] serialize();
}
