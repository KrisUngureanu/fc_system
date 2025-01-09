package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import com.cifs.or2.server.workflow.definition.ActivityState;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 04.05.2005
 * Time: 16:34:01
 * To change this template use File | Settings | File Templates.
 */
public class OutBoxStateImpl extends StateImpl implements ActivityState {
    private ASTStart boxExpression = null;
    private ASTStart assignmentExpression;
    private String boxExpressionKrn="";
    private String assignmentExpressionKrn;

    public OutBoxStateImpl(ProcessDefinition processDefinition,
                             ProcessBlock processBlock,
                             Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
        String expr;
        boxExpressionKrn = getProperty("KRNexchangeBox", xml);
        assignmentExpressionKrn = getProperty("KRNassignment", xml);

        if (boxExpressionKrn == null || boxExpressionKrn.length() == 0) {
	        expr = getProperty("exchangeBox", xml);
	        boxExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (assignmentExpressionKrn == null || assignmentExpressionKrn.length() == 0) {
        	expr = getProperty("assignment", xml);
        	assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
    }
    public void refreash(ProcessDefinition processDefinition, Element xml, Session s){
        super.refreash(processDefinition, xml, s);
        
        String expr;
        boxExpressionKrn = getProperty("KRNexchangeBox", xml);
        assignmentExpressionKrn = getProperty("KRNassignment", xml);

        if (boxExpressionKrn == null || boxExpressionKrn.length() == 0) {
	        expr = getProperty("exchangeBox", xml);
	        boxExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (assignmentExpressionKrn == null || assignmentExpressionKrn.length() == 0) {
        	expr = getProperty("assignment", xml);
        	assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
    }
    public ASTStart getBoxExpression() {
        return boxExpression;
    }

    public void setBoxExpression(String expr) {
    	boxExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }


    public ASTStart getAssignment() {
        return assignmentExpression;
    }

    public void setAssignment(String expr) {
        assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }
    public String getType() {
        return "outbox-state";
    }

    public String getBoxExpressionKrn() {
        return boxExpressionKrn;
    }

    public String getAssignmentKrn() {
        return assignmentExpressionKrn;
    }
}
