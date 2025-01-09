package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.DefinitionObject;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.Action;
import com.cifs.or2.server.workflow.definition.EventType;
import org.jdom.Element;
import org.jdom.Attribute;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:40:07
 * To change this template use File | Settings | File Templates.
 */
public class DefinitionObjectImpl implements DefinitionObject {
    private ProcessDefinition processDefinition;
    private String name;
    private ASTStart title;
    private String description;
    private List actions;
    protected Element xml;
	protected Session session;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setProperty("name", xml, name, true);
    }

    public ASTStart getTitle() {
        return title;
    }

    public void setTitle(String title) {
    	this.title = (title != null && title.trim().length() > 0) ? OrLang.createStaticTemplate(title) : null;
        setProperty("title", xml, title, false);
    }

    public boolean hasName() {
        return (name != null);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
        setProperty("description", xml, desc, false);
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public Collection getActions() {
        return actions;
    }

    public Action createAction(EventType eventType, String expression) {
        ActionImpl action = new ActionImpl(new Element("action"));
        action.setEventType(eventType);
        action.setExpression(expression);
        actions.add(action);
        xml.addContent(action.getXml());
        return action;
    }

    public void removeAction(Action action) {
        actions.remove(action);
        xml.removeContent(((ActionImpl)action).getXml());
    }

    protected DefinitionObjectImpl(ProcessDefinition processDefinition,
                                   Element xml, String name, Session s) {
        init(processDefinition, xml, name, s);
    }

    protected void init(ProcessDefinition processDefinition, Element xml, String name, Session s) {
    	this.session = s;
        this.processDefinition = processDefinition;
        this.xml = xml;
        if (name != null) {
        	this.name = name;
        } else
        	this.name = getProperty("name", xml);
        String expr = getProperty("title", xml);
        title = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        description = getProperty("description", xml);
        actions = new ArrayList();
        List es = xml.getChildren("action");
        for (int i = 0; i < es.size(); i++) {
            Element e = (Element) es.get(i);
            actions.add(new ActionImpl(e));
        }
    }

    public void refreash(ProcessDefinition processDefinition, Element xml, String name, Session s) {
    	init(processDefinition, xml, name, s);
    }

    protected DefinitionObjectImpl() {
    }

    public static String getProperty(String name, Element xml) {
        Attribute attr = xml.getAttribute(name);
        if (attr != null) {
            return attr.getValue();
        } else {
            Element e = xml.getChild(name);
            if (e != null) {
                return e.getText();
            }
        }
        return null;
    }

    public static void setProperty(String name, Element xml, String value,
                                   boolean asAttribute) {
        Attribute attr = xml.getAttribute(name);
        Element e = xml.getChild(name);
        if (attr != null) {
            attr.setValue(value);
        } else if (e != null) {
            e.setText(value);
        } else if (asAttribute) {
            xml.setAttribute(name, value);
        } else {
            e = new Element(name);
            e.setText(value);
            xml.addContent(e);
        }
    }

    public Element getXml() {
        return xml;
    }
}
