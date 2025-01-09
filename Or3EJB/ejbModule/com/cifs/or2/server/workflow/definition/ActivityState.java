package com.cifs.or2.server.workflow.definition;

import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:16:02
 * To change this template use File | Settings | File Templates.
 */
public interface ActivityState extends State {
	ASTStart getAssignment();
    String getAssignmentKrn();
    void setAssignment(String assignment);
}
