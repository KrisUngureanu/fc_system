package com.cifs.or2.server.workflow.definition;

import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:50:23
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessState extends State {
	ASTStart getSubProcess();
	String getActorExpression(); 
}
