package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ActivityState;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:12:39
 * To change this template use File | Settings | File Templates.
 */
public class ActivityStateImpl extends StateImpl implements ActivityState {
    private ASTStart assignmentExpression = null;
    private String assignmentExpressionKrn="";

    private ASTStart uiExpression=null;
    private String errorExpression=null;
    private String uiExpressionKrn="";
    private String uiType="";
    private ASTStart objExpression = null;
    private String objExpressionKrn="";
    private ASTStart objTitleExpression = null;
    private ASTStart dateAlarmExpression = null;
    private ASTStart dateAlertExpression = null;
    private ASTStart paramExpression = null;
    private String chopperEnable="";
    private String transitionToId="";
    private String taskColor="";
    private boolean isAutoNext=false;
    private boolean isReportRequire=false;

    public ASTStart getAssignment() {
        return assignmentExpression;
    }

    public void setAssignment(String expr) {
        assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }

    public ASTStart getUiExpression() {
        return uiExpression;
    }
    public String getErrorExpression() {
        return errorExpression;
    }

    public String getUiType() {
        return uiType;
    }
    public ASTStart getObjExpression() {
        return objExpression;
    }

    public void setObjExpression(String expr) {
    	objExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }

    public ASTStart getDateAlarm() {
        return dateAlarmExpression;
    }

    public ASTStart getDateAlert() {
        return dateAlertExpression;
    }

    public ASTStart getObjTitleExpression() {
        return objTitleExpression;
    }

    public ASTStart getParamExpression() {
        return paramExpression;
    }

    public String getTaskColor() {
        return taskColor;
    }

    public boolean isAutoNext() {
        return isAutoNext;
    }
    public boolean isReportRequire() {
        return isReportRequire;
    }
    public void setParamExpression(String expr) {
        paramExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }

    public ActivityStateImpl(ProcessDefinition processDefinition,
                             ProcessBlock processBlock,
                             Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
        String expr;
        uiExpressionKrn = getProperty("KRNprocessUi", xml);
        if (uiExpressionKrn == null || uiExpressionKrn.length() == 0) {
	           expr = getProperty("processUi", xml);
	           uiExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        errorExpression = getProperty("processError", xml);
        //errorExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        assignmentExpressionKrn = getProperty("KRNassignment", xml);
        if (assignmentExpressionKrn == null || assignmentExpressionKrn.length() == 0) {
	           expr = getProperty("assignment", xml);
	           assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        objExpressionKrn = getProperty("KRNprocessObj", xml);
        if (objExpressionKrn == null || objExpressionKrn.length() == 0) {
	           expr = getProperty("processObj", xml);
	           objExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        expr = getProperty("titleObj", xml);
        objTitleExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = getProperty("paramObj", xml);
        paramExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = getProperty("dateAlarm", xml);
        dateAlarmExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = getProperty("dateAlert", xml);
        dateAlertExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;

        uiType = getProperty("processUiType", xml);
        chopperEnable = getProperty("enableChopper", xml);
        taskColor = getProperty("taskColor", xml);
        isAutoNext = "true".equals(getProperty("isAutoNext", xml));
        isReportRequire = "true".equals(getProperty("isReportRequire", xml));
    }
    public void refreash(ProcessDefinition processDefinition, Element xml, Session s){
           super.refreash(processDefinition, xml, s);
           String expr;

           uiExpressionKrn = getProperty("KRNprocessUi", xml);
           if (uiExpressionKrn == null || uiExpressionKrn.length() == 0) {
	           expr = getProperty("processUi", xml);
	           uiExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           }
           errorExpression = getProperty("processError", xml);
           assignmentExpressionKrn = getProperty("KRNassignment", xml);
           if (assignmentExpressionKrn == null || assignmentExpressionKrn.length() == 0) {
	           expr = getProperty("assignment", xml);
	           assignmentExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           }
           objExpressionKrn = getProperty("KRNprocessObj", xml);
           if (objExpressionKrn == null || objExpressionKrn.length() == 0) {
	           expr = getProperty("processObj", xml);
	           objExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           }
           expr = getProperty("titleObj", xml);
           objTitleExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           expr = getProperty("paramObj", xml);
           paramExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           expr = getProperty("dateAlarm", xml);
           dateAlarmExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
           expr = getProperty("dateAlert", xml);
           dateAlertExpression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;

           uiType = getProperty("processUiType", xml);
           chopperEnable = getProperty("enableChopper", xml);
           taskColor = getProperty("taskColor", xml);
           isAutoNext = "true".equals(getProperty("isAutoNext", xml));
           isReportRequire = "true".equals(getProperty("isReportRequire", xml));
    }


    public String getChopperEnable() {
        return chopperEnable;
    }

    public String getTransitionTo() {
        return transitionToId;
    }

    public void setTransitionTo(String transitionToId) {
        this.transitionToId = transitionToId;
    }
    public String getType() {
        return "activity-state";
    }

    public String getUiExpressionKrn() {
        return uiExpressionKrn;
    }

    public String getAssignmentKrn() {
        return assignmentExpressionKrn;
    }

    public String getObjExpressionKrn() {
        return objExpressionKrn;
    }
}
