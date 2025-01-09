package kz.tamur.guidesigner.service;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kz.tamur.guidesigner.InterfaceActionsConteiner;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.or3.client.props.Property;
import kz.tamur.util.AbstractDesignerTreeNode;

import org.tigris.gef.graph.presentation.NetPort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class ServiceActions {

	private Kernel kernel = Kernel.instance();
	private final String currentUser = kernel.getUser().getName();
	private final String objectType = "Процесс";
	private String serviceName;
	public long ID;
	
	private Document xmlDocumentUndo;
	private Document xmlDocumentRedo;
	private Document xmlDocumentToRename;
	private Document xmlDocumentToSave;
	private Map<String, Element> xmlElementsUndo = new HashMap<String, Element>(); 
	private Map<String, Element> xmlElementsRedo = new HashMap<String, Element>();
	private Map<String, Element> xmlElementsToRename = new HashMap<String, Element>(); 
	private Map<String, Element> xmlElementsToSave = new HashMap<String, Element>(); 
	private int MODE = 3;	//Режим записи действий: 
							//1 - запись действий в документ xmlDocument, xmlDocumentUndo и xmlDocumentToSave;
							//2 - запись действий в документ xmlDocumentRedo;
							//3 - запись действий в документ xmlDocument, xmlDocumentUndo, xmlDocumentRedo и xmlDocumentToSave;
							//4 - запись действий в документ xmlDocumentToRename;
							//5 - запись действий в документ xmlDocumentToSave;
							//6 - запись действий в документ xmlDocumentRedo и xmlDocumentToSave;
	
	private Map<String, NodeProperty[]> elementsPropertyNames = new HashMap<String, NodeProperty[]>(); 
	private Map<String, Object[]> elementsPropertyValues = new HashMap<String, Object[]>(); 
	private Map<String, Property> propertyConteiner = new HashMap<String, Property>(); 
	private boolean canClean = true;
	private boolean isInit = false;
	private boolean undoRedoCall = false; 

	// Создает объект InterfaceActions при открытии процесса
	public ServiceActions(AbstractDesignerTreeNode node) {
		this.serviceName = node.toString();
		this.ID = node.getKrnObj().id;		
		initXMLDocument();
		MODE = 1;
	}
	
	public boolean getCanClean() {
		return canClean;
	}
	
	public void setCanClean(boolean canClean) {
		this.canClean = canClean;
	}
	
	public boolean getUndoRedoCall() {
		return undoRedoCall;
	}
	
	public void setUndoRedoCall(boolean undoRedoCall) {
		this.undoRedoCall = undoRedoCall;
	}
	
	public int getMode() {
		return MODE;
	}
	
	public void setMode(int currentMode) {
		MODE = currentMode;
	}
		
	public Property getProperty(String propertyID) {
		return propertyConteiner.get(propertyID);
	}
		
	private void initXMLDocument() {
		DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlBuilder = null;
		try {
			xmlBuilder = xmlFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		xmlDocumentUndo = xmlBuilder.newDocument();	
		xmlDocumentRedo = xmlBuilder.newDocument();
		xmlDocumentToSave = xmlBuilder.newDocument();	
		addRootToXML("service", "service", null, null, null);
		addChildToXML("service-id", "service-id", String.valueOf(ID), "service", null, null);
		addChildToXML("service-name", "service-name", serviceName, "service", null, null);
		addChildToXML("user-name", "user-name", currentUser, "service", null, null);
		addChildToXML("time", "time", convertDate(), "service", null, null);
		addChildToXML("actions", "actions", null, "service", null, null);
		MODE = 1;
		isInit = true;
	}
	
	private String convertDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
		return dateFormat.format(getEditingDate());
	}
	
	private Date getEditingDate() {
		return new Date();
	}
	
	private void addRootToXML(String rootElementName, String title, String value, String attribute, String attributeValue) {			
		switch (MODE) {
			case 1:
				addRootByMode(xmlElementsUndo, xmlDocumentUndo, rootElementName, title, value, attribute, attributeValue);
				addRootByMode(xmlElementsToSave, xmlDocumentToSave, rootElementName, title, value, attribute, attributeValue);
				break;
			case 2:
				addRootByMode(xmlElementsRedo, xmlDocumentRedo, rootElementName, title, value, attribute, attributeValue);
				break;
			case 3:
				addRootByMode(xmlElementsUndo, xmlDocumentUndo, rootElementName, title, value, attribute, attributeValue);
				addRootByMode(xmlElementsRedo, xmlDocumentRedo, rootElementName, title, value, attribute, attributeValue);
				addRootByMode(xmlElementsToSave, xmlDocumentToSave, rootElementName, title, value, attribute, attributeValue);
				break;
			case 4:
				addRootByMode(xmlElementsToRename, xmlDocumentToRename, rootElementName, title, value, attribute, attributeValue);
				break;
			case 5:
				addRootByMode(xmlElementsToSave, xmlDocumentToSave, rootElementName, title, value, attribute, attributeValue);
			case 6:
				addRootByMode(xmlElementsRedo, xmlDocumentRedo, rootElementName, title, value, attribute, attributeValue);
				addRootByMode(xmlElementsToSave, xmlDocumentToSave, rootElementName, title, value, attribute, attributeValue);
				break;			
		}	
	}	
	
	private void addRootByMode(Map<String, Element> anyElements, Document anyDocument, String rootElementName, String title, String value, String attribute, String attributeValue) {
		anyElements.put(rootElementName, anyDocument.createElement(title));
		if (value != null) {
			Text textNode = anyDocument.createTextNode(value);
			anyElements.get(rootElementName).appendChild(textNode);
		}
		if (attribute != null && attributeValue != null)
			anyElements.get(rootElementName).setAttribute("attribute", "attributeValue");	
		anyDocument.appendChild(anyElements.get(rootElementName));			
	}
	
	private void addChildToXML(String childElementName, String title, String value, String rootElementName, String attribute, String attributeValue) {		
		switch (MODE) {
			case 1:
				addChildByMode(xmlElementsUndo, xmlDocumentUndo, childElementName, title, value, rootElementName, attribute, attributeValue);
				addChildByMode(xmlElementsToSave, xmlDocumentToSave, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
			case 2:
				addChildByMode(xmlElementsRedo, xmlDocumentRedo, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
			case 3:
				addChildByMode(xmlElementsUndo, xmlDocumentUndo, childElementName, title, value, rootElementName, attribute, attributeValue);
				addChildByMode(xmlElementsRedo, xmlDocumentRedo, childElementName, title, value, rootElementName, attribute, attributeValue);
				addChildByMode(xmlElementsToSave, xmlDocumentToSave, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
			case 4:
				addChildByMode(xmlElementsToRename, xmlDocumentToRename, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
			case 5:
				addChildByMode(xmlElementsToSave, xmlDocumentToSave, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
			case 6:
				addChildByMode(xmlElementsRedo, xmlDocumentRedo, childElementName, title, value, rootElementName, attribute, attributeValue);
				addChildByMode(xmlElementsToSave, xmlDocumentToSave, childElementName, title, value, rootElementName, attribute, attributeValue);
				break;
		}			
	}
	
	private void addChildByMode(Map<String, Element> anyElements, Document anyDocument, String childElementName, String title, String value, String rootElementName, String attributeName, String attributeValue) {
		anyElements.put(childElementName, anyDocument.createElement(title));
		if (value != null) {
			Text textNode = anyDocument.createTextNode(value);
			anyElements.get(childElementName).appendChild(textNode);
		}
		if (attributeName != null && attributeValue != null) {
			anyElements.get(childElementName).setAttribute(attributeName, attributeValue);
		}			
		anyElements.get(rootElementName).appendChild(anyElements.get(childElementName));	
	}
	
	public void writeXMLFile(Long processID, Document xmlDocument) {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");	
		File xmlFile = new File("C:/Users/Администратор/Or3EE/" + String.valueOf(processID) + "_" + dateFormat.format(getEditingDate()) + ".xml");		
	    Transformer xmlTransformer = null;
		try {
			xmlTransformer = TransformerFactory.newInstance().newTransformer();
			xmlTransformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	    try {
	    	xmlTransformer.transform(new DOMSource(xmlDocument), new StreamResult(new FileOutputStream(xmlFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}      
	}
		
	public Document getXMLDocumentUndo() {
		return xmlDocumentUndo;
	}
	
	public Document getXMLDocumentRedo() {
		return xmlDocumentRedo;
	}
	
	public Document getXMLDocumentToSave() {
		return xmlDocumentToSave;
	}
	
	public void changeNodeLocation(StateNode node, Rectangle oldLocation, Rectangle newLocation) {			
		String nodeID = null;
		if (isInit && xmlDocumentUndo.getElementsByTagName("actions").item(0).hasChildNodes()) {
			if (xmlDocumentUndo.getElementsByTagName("actions").item(0).getLastChild().getNodeName().equals("change-location")) {			
				NodeList changeLocationActionElements = xmlDocumentUndo.getElementsByTagName("actions").item(0).getLastChild().getChildNodes();
				for (int i = 0; i < changeLocationActionElements.getLength(); i++) {
					if (changeLocationActionElements.item(i).getNodeName().equals("node-ID")) {
						nodeID = changeLocationActionElements.item(i).getTextContent();
						if (nodeID.equals(node.getId())) {								
							for (int j = 0; j < changeLocationActionElements.getLength(); j++) {					
								if (changeLocationActionElements.item(j).getNodeName().equals("new-location")) {
									NodeList position = changeLocationActionElements.item(j).getChildNodes();
									for (int k = 0; k < position.getLength(); k++) {
										if (position.item(k).getNodeName().equals("x")) {
											position.item(k).setTextContent(String.valueOf((int) newLocation.getX()));
										} else if (position.item(k).getNodeName().equals("y")) {
											position.item(k).setTextContent(String.valueOf((int) newLocation.getY()));
										}
									}
								} else if (changeLocationActionElements.item(j).getNodeName().equals("new-dimension")) {
									NodeList dimension = changeLocationActionElements.item(j).getChildNodes();
									for (int k = 0; k < dimension.getLength(); k++) {
										if (dimension.item(k).getNodeName().equals("height")) {
											dimension.item(k).setTextContent(String.valueOf((int) newLocation.getHeight()));
										} else if (dimension.item(k).getNodeName().equals("width")) {
											dimension.item(k).setTextContent(String.valueOf((int) newLocation.getWidth()));
										}
									}
								} 							
							}
							if (xmlDocumentToSave.getElementsByTagName("actions").item(0).hasChildNodes())
							{
								xmlDocumentToSave.getElementsByTagName("actions").item(0).removeChild(xmlDocumentToSave.getElementsByTagName("actions").item(0).getLastChild());
							}
							Node lastChild = xmlDocumentUndo.getElementsByTagName("actions").item(0).getLastChild();
							NodeList lastChildNodes = lastChild.getChildNodes();
							MODE = 5;
							String elementName = "change-location";
							String nodeName = "node-name";	
							addChildToXML(elementName, "change-location", null, "actions", "type", node.getType());
							addChildToXML(nodeID, "node-ID", node.getId(), elementName, null, null);
							addChildToXML(nodeName, "node-name", node.getName(), elementName, null, null);								
							for (int k = 0; k < lastChildNodes.getLength(); k++) {
								if (changeLocationActionElements.item(k).getNodeName().equals("location")) {
									addChildToXML("location", "location", null, elementName, null, null);
									NodeList location = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < location.getLength(); s++) {
										if (location.item(s).getNodeName().equals("x")) {
											addChildToXML("x", "x", location.item(s).getTextContent(), "location", null, null);
										} else if (location.item(s).getNodeName().equals("y")) {
											addChildToXML("y", "y", location.item(s).getTextContent(), "location", null, null);
										}
									}
								} else if (changeLocationActionElements.item(k).getNodeName().equals("old-location")) {
									addChildToXML("old-location", "old-location", null, elementName, null, null);
									NodeList location = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < location.getLength(); s++) {
										if (location.item(s).getNodeName().equals("x")) {
											addChildToXML("x", "x", location.item(s).getTextContent(), "old-location", null, null);
										} else if (location.item(s).getNodeName().equals("y")) {
											addChildToXML("y", "y", location.item(s).getTextContent(), "old-location", null, null);
										}
									}
								} else if (changeLocationActionElements.item(k).getNodeName().equals("new-location")) {
									addChildToXML("new-location", "new-location", null, elementName, null, null);
									NodeList location = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < location.getLength(); s++) {
										if (location.item(s).getNodeName().equals("x")) {
											addChildToXML("x", "x", location.item(s).getTextContent(), "new-location", null, null);
										} else if (location.item(s).getNodeName().equals("y")) {
											addChildToXML("y", "y", location.item(s).getTextContent(), "new-location", null, null);
										}
									}
								} else if (changeLocationActionElements.item(k).getNodeName().equals("dimension")) {
									addChildToXML("dimension", "dimension", null, elementName, null, null);
									NodeList dimension = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < dimension.getLength(); s++) {
										if (dimension.item(s).getNodeName().equals("height")) {
											addChildToXML("height", "height", dimension.item(s).getTextContent(), "dimension", null, null);
										} else if (dimension.item(s).getNodeName().equals("width")) {
											addChildToXML("width", "width", dimension.item(s).getTextContent(), "dimension", null, null);
										}
									}
								} else if (changeLocationActionElements.item(k).getNodeName().equals("old-dimension")) {
									addChildToXML("old-dimension", "old-dimension", null, elementName, null, null);
									NodeList dimension = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < dimension.getLength(); s++) {
										if (dimension.item(s).getNodeName().equals("height")) {
											addChildToXML("height", "height", dimension.item(s).getTextContent(), "old-dimension", null, null);
										} else if (dimension.item(s).getNodeName().equals("width")) {
											addChildToXML("width", "width", dimension.item(s).getTextContent(), "old-dimension", null, null);
										}
									}
								} else if (changeLocationActionElements.item(k).getNodeName().equals("new-dimension")) {
									addChildToXML("new-dimension", "new-dimension", null, elementName, null, null);
									NodeList dimension = changeLocationActionElements.item(k).getChildNodes();
									for (int s = 0; s < dimension.getLength(); s++) {
										if (dimension.item(s).getNodeName().equals("height")) {
											addChildToXML("height", "height", dimension.item(s).getTextContent(), "new-dimension", null, null);
										} else if (dimension.item(s).getNodeName().equals("width")) {
											addChildToXML("width", "width", dimension.item(s).getTextContent(), "new-dimension", null, null);
										}
									}
								}								
							}
							MODE = 1;
							return;
						}
					}
				}
			}			
		}		
		
		boolean isChangedLocation = (oldLocation.x != newLocation.x || oldLocation.y != newLocation.y);
		boolean isChangedDimension = (oldLocation.width != newLocation.width || oldLocation.height != newLocation.height);
		String elementName = "change-location";
		String nodeName = "node-name";	
		String Location = isChangedLocation ? "old-location" : "location";
		String x = "x";
		String y = "y";	
		String Dimension = isChangedDimension ? "old-dimension" : "dimension";			
		String height = "height";
		String width = "width";	
		addChildToXML(elementName, "change-location", null, "actions", "type", node.getType());
		addChildToXML(nodeID, "node-ID", node.getId(), elementName, null, null);
		addChildToXML(nodeName, "node-name", node.getName(), elementName, null, null);		
		addChildToXML(Location, Location, null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf((int) oldLocation.getX()), Location, null, null);
		addChildToXML(y, "y", String.valueOf((int) oldLocation.getY()), Location, null, null);
		if (isChangedLocation) {
			String newLoc = "new-location";
			addChildToXML(newLoc, "new-location", null, elementName, null, null);
			addChildToXML(x, "x", String.valueOf((int) newLocation.getX()), newLoc, null, null);
			addChildToXML(y, "y", String.valueOf((int) newLocation.getY()), newLoc, null, null);
		}					
		addChildToXML(Dimension, Dimension, null, elementName, null, null);
		addChildToXML(height, "height", String.valueOf((int) oldLocation.getHeight()), Dimension, null, null);
		addChildToXML(width, "width", String.valueOf((int) oldLocation.getWidth()), Dimension, null, null);
		if (isChangedDimension) {
			String newDimension = "new-dimension";
			addChildToXML(newDimension, "new-dimension", null, elementName, null, null);
			addChildToXML(height, "height", String.valueOf((int) newLocation.getHeight()), newDimension, null, null);
			addChildToXML(width, "width", String.valueOf((int) newLocation.getWidth()), newDimension, null, null);
		}
	}
	
	public void addEdge(TransitionEdge edge) {		
		String elementName = "add-edge";
		String edgeID = "edge-ID";			
		addChildToXML(elementName, "add-edge", null, "actions", "type", edge.getPresentation().getTipString(null));
		addChildToXML(edgeID, "edge-ID", edge.getId(), elementName, null, null);
	}	
	
	public void addNode(StateNode node) {
		String elementName = "add-node";
		String nodeID = "node-ID";		
		addChildToXML(elementName, "add-node", null, "actions", "type", node.getType());
		addChildToXML(nodeID, "node-ID", node.getId(), elementName, null, null);
	}	
	
	public void removeEdge(TransitionEdge edge) {		
		String elementName = "drop-edge";
		String edgeID = "edge-ID";		
		String edgeName = "edge-name";		
		String edgeSource = "edge-source";
		String edgeSourceID = "source-id";		
		String edgeDestination = "edge-destination";	
		String edgeDestinationID = "destination-id";
		String x = "x";
		String y = "y";	
		String edgePoint = "point";
		addChildToXML(elementName, "drop-edge", null, "actions", "type", edge.getPresentation().getTipString(null));
		addChildToXML(edgeID, "edge-ID", edge.getId(), elementName, null, null);
		addChildToXML(edgeName, "edge-name", edge.getName(), elementName, null, null);
		addChildToXML(edgeSource, "edge-source", null, elementName, "type", ((StateNode) ((NetPort) edge.getSourcePort()).getParent()).getType());
		addChildToXML(edgeSourceID, "source-id", ((StateNode) ((NetPort) edge.getSourcePort()).getParent()).getId(), edgeSource, null, null);
		addChildToXML(edgeDestination, "edge-destination", null, elementName, "type", ((StateNode) ((NetPort) edge.getDestPort()).getParent()).getType());
		addChildToXML(edgeDestinationID, "destination-id", ((StateNode) ((NetPort) edge.getDestPort()).getParent()).getId(), edgeDestination, null, null);
		Vector<Point> edgePoints = new Vector<Point>();	
		edgePoints = edge.getPoints();
		for (int i = 0; i < edgePoints.size(); i++) {
			addChildToXML(edgePoint, "point", null, elementName, null, null);
			addChildToXML(x, "x", String.valueOf(edgePoints.get(i).x), edgePoint, null, null);
			addChildToXML(y, "y", String.valueOf(edgePoints.get(i).y), edgePoint, null, null);
		}
	}	

	public void removeNode(StateNode node) {
		String elementName = "drop-node";
		String nodeID = "node-ID";	
		String nodeClass = "node-class";
		String nodeName = "node-name";	
		String location = "node-location";
		String x = "x";
		String y = "y";	
		String dimension = "node-dimension";
		String height = "height";
		String width = "width";	
		addChildToXML(elementName, "drop-node", null, "actions", "type", node.getType());
		addChildToXML(nodeID, "node-ID", node.getId(), elementName, null, null);
		addChildToXML(nodeClass, "node-class", node.getClass().getSimpleName(), elementName, null, null);
		addChildToXML(nodeName, "node-name", node.getName(), elementName, null, null);
		addChildToXML(location, "node-location", null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf(node.getPresentation().getX()), location, null, null);
		addChildToXML(y, "y", String.valueOf(node.getPresentation().getY()), location, null, null);
		addChildToXML(dimension, "node-dimension", null, elementName, null, null);
		addChildToXML(height, "height", String.valueOf(node.getPresentation().getHeight()), dimension, null, null);
		addChildToXML(width, "width", String.valueOf(node.getPresentation().getWidth()), dimension, null, null);		
		NodeProperty[] propertyNames =  node.getProperties();
		Object[] propertyValues = new Object[propertyNames.length]; 
		for (int i = 0; i < propertyNames.length; i++) {
			propertyValues[i] = node.getProperty(propertyNames[i]);
		}
		elementsPropertyNames.put(node.getId(), propertyNames);
		elementsPropertyValues.put(node.getId(), propertyValues);			
	}	
	
	public NodeProperty[] getElementsPropertyNames(String nodeID) {
		NodeProperty[] propertyNames = elementsPropertyNames.get(nodeID);
		elementsPropertyNames.remove(nodeID);
		return propertyNames;
	}
	
	public Object[] getElementsPropertyValues(String nodeID) {
		Object[] propertyValues = elementsPropertyValues.get(nodeID);
		elementsPropertyValues.remove(nodeID);
		return propertyValues;
	}	
	
	public void propertyChanged(StateNode element, String propertyName, Object oldValue, Object newValue, Property property) {	
		String elementName = "change-property";
		String nodeID = "node-ID";	
		String nodeName = "node-name";	
		String propID = "property-ID";	
		String propTitle = "property-title";	
		String oldVal = "old-value";	
		String newVal = "new-value";		
		addChildToXML(elementName, "change-property", null, "actions", "type", element.getType());
		addChildToXML(nodeID, "node-ID", element.getId(), elementName, null, null);
		addChildToXML(nodeName, "node-name", element.getName(), elementName, null, null);
		propertyConteiner.put(property.getId(), property);
		addChildToXML(propID, "property-ID", property.getId(), elementName, null, null);
		addChildToXML(propTitle, "property-title", property.getTtitle(), elementName, null, null);
		if (String.valueOf(oldValue).equals("null")) {
			addChildToXML(oldVal, "old-value", "", elementName, null, null);
		} else {
			addChildToXML(oldVal, "old-value", String.valueOf(oldValue), elementName, null, null);
		}
		if (String.valueOf(newValue).equals("null")) {
			addChildToXML(newVal, "new-value", "", elementName, null, null);		
		} else {
			addChildToXML(newVal, "new-value", String.valueOf(newValue), elementName, null, null);		
		}		
	}	
	
	public void addPointToEdge(TransitionEdge edge, Point addedPoint, int pointID) {
		String elementName = "add-point";
		String edgeID = "edge-ID";	
		String pointsCount = "points-count";	
		String location = "location";
		String x = "x";
		String y = "y";	
		addChildToXML(elementName, "add-point", null, "actions", null, null);
		addChildToXML(edgeID, "edge-ID", edge.getId(), elementName, null, null);
		addChildToXML(pointsCount, "points-count", String.valueOf(edge.getPoints().size()), elementName, null, null);
		if (pointID != -1) {
			addChildToXML("point-ID", "point-ID", String.valueOf(pointID), elementName, null, null);
		}
		addChildToXML(location, "location", null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf(addedPoint.x), location, null, null);
		addChildToXML(y, "y", String.valueOf(addedPoint.y), location, null, null);
	}
	
	public void removePointFromEdge(TransitionEdge edge, Point removedPoint, int pointID) {
		String elementName = "remove-point";
		String edgeID = "edge-ID";	
		String pointsCount = "points-count";	
		String location = "location";
		String x = "x";
		String y = "y";	
		addChildToXML(elementName, "remove-point", null, "actions", null, null);
		addChildToXML(edgeID, "edge-ID", edge.getId(), elementName, null, null);
		addChildToXML(pointsCount, "points-count", String.valueOf(edge.getPoints().size()), elementName, null, null);
		if (pointID != -1) { 
			addChildToXML("point-ID", "point-ID", String.valueOf(pointID), elementName, null, null);
		}
		addChildToXML(location, "location", null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf(removedPoint.x), location, null, null);
		addChildToXML(y, "y", String.valueOf(removedPoint.y), location, null, null);	
	}
	
	public void replacePointInEdge(TransitionEdge edge, Point replacedPointBefore, Point replacedPointAfter, int pointID) {
		String elementName = "replace-point";
		String edgeID = "edge-ID";	
		String pointsCount = "points-count";	
		String oldLocation = "old-location";
		String newLocation = "new-location";
		String x = "x";
		String y = "y";	
		addChildToXML(elementName, "replace-point", null, "actions", null, null);
		addChildToXML(edgeID, "edge-ID", edge.getId(), elementName, null, null);
		addChildToXML(pointsCount, "points-count", String.valueOf(edge.getPoints().size()), elementName, null, null);
		if (pointID != -1) { 
			addChildToXML("point-ID", "point-ID", String.valueOf(pointID), elementName, null, null);
		}
		addChildToXML(oldLocation, "old-location", null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf(replacedPointBefore.x), oldLocation, null, null);
		addChildToXML(y, "y", String.valueOf(replacedPointBefore.y), oldLocation, null, null);			
		addChildToXML(newLocation, "new-location", null, elementName, null, null);
		addChildToXML(x, "x", String.valueOf(replacedPointAfter.x), newLocation, null, null);
		addChildToXML(y, "y", String.valueOf(replacedPointAfter.y), newLocation, null, null);	
	}
	
    public void setUndoRedoActivity(MainFrame mainFrame) {
        try {
            mainFrame.getUndoItem().setEnabled(xmlDocumentUndo.getElementsByTagName("actions").item(0).hasChildNodes());
            mainFrame.getRedoItem().setEnabled(xmlDocumentRedo.getElementsByTagName("actions").item(0).hasChildNodes());
        } catch (Exception e) {
        }
    }	
			
	public boolean canSave() {
		Node actionsElementToSave = xmlDocumentToSave.getElementsByTagName("actions").item(0);
		if (actionsElementToSave.getChildNodes().getLength() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void removeChanges() {
		Node actionsElementToSave = xmlDocumentToSave.getElementsByTagName("actions").item(0);
		int childsCount = actionsElementToSave.getChildNodes().getLength();
		NodeList list = actionsElementToSave.getChildNodes();
		for (int i = 0; i < childsCount; i++) {
			if(list.item(i)!=null)
				actionsElementToSave.removeChild(list.item(i));
		}
	}
	
	public void writeObject() {
		if (InterfaceActionsConteiner.getInterfacesMode()) {
			try {
				KrnClass actionClass = kernel.getClassByName("Action");
				KrnObject[] actionObjects = kernel.getClassObjects(actionClass, 0);
				KrnObject actionObject = null;
				for (int i = 0; i < actionObjects.length; i++) {
					if (kernel.getLongs(actionObjects[i], "id", 0)[0] == ID) {
						actionObject = actionObjects[i];
					}
				}
				if (actionObject == null) {
					actionObject = kernel.createObject(actionClass, 0);
				}
				kernel.setString(actionObject.id, actionClass.id, "name", 0, 0, serviceName, 0);	
				kernel.setLong(actionObject.id, actionClass.id, "id", 0, ID, 0);	
				kernel.setString(actionObject.id, actionClass.id, "type", 0, 0, objectType, 0);	
				kernel.setString(actionObject.id, actionClass.id, "user", 0, 0, currentUser, 0);	
				kernel.setTime(actionObject.id, actionClass.id, "editingDate", 0, getEditingDate(), 0);
				kernel.setMemo((int) actionObject.id, (int) actionClass.id, "log", 0, 0, convertXMLToString(xmlDocumentToSave), 0);
			}
			catch (KrnException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public String convertXMLToString(Document xmlDocument) 
    { 
		try { 
		    StringWriter stringWriter = new StringWriter(); 
		    Transformer xmlTransformer = TransformerFactory.newInstance().newTransformer(); 
		    xmlTransformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
		    xmlTransformer.transform(new DOMSource(xmlDocument), new StreamResult(stringWriter)); 
		    return stringWriter.toString();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		}
		return null; 
    }
}
