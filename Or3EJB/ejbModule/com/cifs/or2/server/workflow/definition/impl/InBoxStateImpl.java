package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 04.05.2005
 * Time: 16:21:37
 * To change this template use File | Settings | File Templates.
 */
public class InBoxStateImpl extends OutBoxStateImpl{
    private ASTStart objTitleExpression = null;
    private ASTStart dateAlarmExpression = null;
    private ASTStart dateAlertExpression = null;
    public InBoxStateImpl(ProcessDefinition processDefinition,
                             ProcessBlock processBlock,
                             Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
        String expr = getProperty("titleObj", xml);
        objTitleExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = getProperty("dateAlarm", xml);
        dateAlarmExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = getProperty("dateAlert", xml);
        dateAlertExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }
    public void refreash(ProcessDefinition processDefinition, Element xml, Session s){
           super.refreash(processDefinition, xml, s);
           String expr = getProperty("titleObj", xml);
           objTitleExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           expr = getProperty("dateAlarm", xml);
           dateAlarmExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           expr = getProperty("dateAlert", xml);
           dateAlertExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }
    public ASTStart getObjTitleExpression() {
        return objTitleExpression;
    }

    public ASTStart getDateAlarmExpression() {
        return dateAlarmExpression;
    }

    public ASTStart getDateAlertExpression() {
        return dateAlertExpression;
    }
    public String getType() {
        return "inbox-state";
    }
}
