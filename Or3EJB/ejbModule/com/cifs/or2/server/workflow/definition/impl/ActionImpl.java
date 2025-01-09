package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.workflow.definition.Action;
import com.cifs.or2.server.workflow.definition.EventType;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:28:38
 * To change this template use File | Settings | File Templates.
 */
public class ActionImpl implements Action {
    private Element xml;
    private EventType eventType;
    private ASTStart expression;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        DefinitionObjectImpl.setProperty("event", xml, eventType.toString(),
                                         true);
    }

    public ASTStart getExpression() {
        return expression;
    }

    public void setExpression(String expr) {
    	expression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        DefinitionObjectImpl.setProperty("expression", xml, expr, false);
    }

    public ActionImpl(Element xml) {
        this.xml = xml;
        String type = DefinitionObjectImpl.getProperty("event", xml);
        eventType = EventType.fromText(type);
        String expr = DefinitionObjectImpl.getProperty("expression", xml);
    	expression = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }

    public Element getXml() {
        return xml;
    }
}
