package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;

public interface ServiceNodeIfc {
	
	String getId();
	
	String getName();
	
	void setProperty(NodeProperty prop, String expression);
	
	void setAction(NodeEventType type, String expression);
	
	
}
