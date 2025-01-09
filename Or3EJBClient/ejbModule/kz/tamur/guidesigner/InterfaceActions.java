package kz.tamur.guidesigner;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Property;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class InterfaceActions {

	private Kernel kernel = Kernel.instance();
	private final String currentUser = kernel.getUser().getName();
	private final String objectType = "Интерфейс";
	private String interfaceName;
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
							//1 - запись действий в документ xmlDocumentUndo и xmlDocumentToSave;
							//2 - запись действий в документ xmlDocumentRedo;
							//3 - запись действий в документ xmlDocumentUndo, xmlDocumentRedo и xmlDocumentToSave;
							//4 - запись действий в документ xmlDocumentToRename;
							//5 - запись действий в документ xmlDocumentToSave;
							//6 - запись действий в документ xmlDocumentRedo и xmlDocumentToSave;
	
	private Map<String, OrGuiComponent> GUIComponents = new HashMap<String, OrGuiComponent>();
	private Map<String, PropertyValue> titleValues = new HashMap<String, PropertyValue>();
	private Map<String, Property> properties = new HashMap<String, Property>();
	private Map<String, Object> values = new HashMap<String, Object>();	
	
	private boolean canClean = true;
	private int lastID = 0;
	
	// Создает объект InterfaceActions при открытии интерфейса
	public InterfaceActions(long ifcId, String title) {
		this.interfaceName = title;
		this.ID = ifcId;		
		initXMLDocument();
		MODE = 1;
	}
	
	public Map<String, Object> getValues() {
		return values;
	}
	
	public Map<String, Property> getProperties() {
		return properties;
	}		
	
	public Map<String, OrGuiComponent> getGUIComponents() {
		return GUIComponents;
	}
	
	public OrGuiComponent getGUIComponent(String componentID) {
		return GUIComponents.get(componentID);
	}
	
	public String getNextID() {
		return String.valueOf(lastID++);
	}
	
	public void setCanClean(boolean canClean) {
		this.canClean = canClean;
	}
	
	public int getMode() {
		return MODE;
	}
	
	public void setMode(int currentMode) {
		MODE = currentMode;
	}	
	
	public PropertyValue getTitleValue(String componentUID) {
		return titleValues.get(componentUID);
	}
	
	public Map<String, PropertyValue> getTitleValues() {
		return titleValues;
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
		addRootToXML("interface", "interface", null, null, null);
		addChildToXML("interface-id", "interface-id", String.valueOf(ID), "interface", null, null);
		addChildToXML("interface-name", "interface-name", interfaceName, "interface", null, null);
		addChildToXML("user-name", "user-name", currentUser, "interface", null, null);
		addChildToXML("time", "time", convertDate(), "interface", null, null);
		addChildToXML("actions", "actions", null, "interface", null, null);
	}
	
	private String convertDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
		return dateFormat.format(getEditingDate());
	}
	
	private Date getEditingDate() {
		return new Date();
	}
	
	public void createEditTag() {
		addChildToXML("editing", "editing", null, "actions", null, null);
		MODE = 1;
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
	
	public void addComponent(String componentID, OrGuiComponent component) {
		addChildToXML("add-component", "add-component", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "add-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "add-component", null, null);
		setUndoRedoActivity();
	}
	
	public void dropComponent(String componentID, OrGuiComponent component, String containerID, OrGuiContainer container, String titleValueID) {		
		PropertyNode position = component.getProperties().getChild("pos");
		int x = component.getPropertyValue(position.getChild("x")).intValue();
		int y = component.getPropertyValue(position.getChild("y")).intValue();
		int width = component.getPropertyValue(position.getChild("width")).intValue();
		int height = component.getPropertyValue(position.getChild("height")).intValue();
		
		String xID = getNextID();
		String yID = getNextID();
		String widthID = getNextID();
		String heightID = getNextID();
		values.put(xID, component.getPropertyValue(position.getChild("x")));
		values.put(yID, component.getPropertyValue(position.getChild("y")));
		values.put(widthID, component.getPropertyValue(position.getChild("width")));
		values.put(heightID, component.getPropertyValue(position.getChild("height")));
		
		addChildToXML("drop-component", "drop-component", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "drop-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "drop-component", null, null);
		addChildToXML("container-ID", "container-ID", containerID, "drop-component", null, null);
		addChildToXML("title-ID", "title-ID", titleValueID, "drop-component", null, null);
		addChildToXML("location", "location", null, "drop-component", null, null);
		addChildToXML("x", "x", String.valueOf(x), "location", null, null);
		addChildToXML("x-ID", "x-ID", xID, "location", null, null);
		addChildToXML("y", "y", String.valueOf(y), "location", null, null);
		addChildToXML("y-ID", "y-ID", yID, "location", null, null);
		addChildToXML("dimension", "dimension", null, "drop-component", null, null);
		addChildToXML("width", "width", String.valueOf(width), "dimension", null, null);
		addChildToXML("width-ID", "width-ID", widthID, "dimension", null, null);
		addChildToXML("height", "height", String.valueOf(height), "dimension", null, null);
		addChildToXML("height-ID", "height-ID", heightID, "dimension", null, null);
		setUndoRedoActivity();
	}
	public void propertyChanged(String componentID, OrGuiComponent component, String propertyID, Property property, String oldValueID, Object oldValue, Object newValue) {
		addChildToXML("change-property", "change-property", null, "actions", "type", component.getClass().getSimpleName());
		addChildToXML("component-ID", "component-ID", componentID, "change-property", null, null);
		addChildToXML("property-ID", "property-ID", propertyID, "change-property", null, null);
		addChildToXML("property-title", "property-title", property.getTtitle(), "change-property", null, null);
		addChildToXML("value-ID", "value-ID",  oldValueID, "change-property", null, null);
		if (String.valueOf(oldValue).equals("null")) {
			addChildToXML("old-value", "old-value", "", "change-property", null, null);
		} else {			
			addChildToXML("old-value", "old-value", String.valueOf(oldValue), "change-property", null, null);
		}
		if (String.valueOf(newValue).equals("null")) {
			addChildToXML("new-value", "new-value", "", "change-property", null, null);		
		} else {
			addChildToXML("new-value", "new-value", String.valueOf(newValue), "change-property", null, null);		
		}	
		setUndoRedoActivity();
	}
	
	public void addColumn(String componentID, OrGuiComponent component, int gridx, int gridy) {
		addChildToXML("add-column", "add-column", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "add-column", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "add-column", null, null);
		addChildToXML("constraints", "constraints", null, "add-column", null, null);
		addChildToXML("x", "x", String.valueOf(gridx), "constraints", null, null);
		addChildToXML("y", "y", String.valueOf(gridy), "constraints", null, null);
		setUndoRedoActivity();
	}
	
	public void dropColumn(String componentID, OrGuiComponent component, int gridx, int gridy) {
		addChildToXML("drop-column", "drop-column", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "drop-column", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "drop-column", null, null);
		addChildToXML("constraints", "constraints", null, "drop-column", null, null);
		addChildToXML("x", "x", String.valueOf(gridx), "constraints", null, null);
		addChildToXML("y", "y", String.valueOf(gridy), "constraints", null, null);
		setUndoRedoActivity();
	}
	
	public void addRow(String componentID, OrGuiComponent component, int gridx, int gridy) {
		addChildToXML("add-row", "add-row", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "add-row", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "add-row", null, null);
		addChildToXML("constraints", "constraints", null, "add-row", null, null);
		addChildToXML("x", "x", String.valueOf(gridx), "constraints", null, null);
		addChildToXML("y", "y", String.valueOf(gridy), "constraints", null, null);
		setUndoRedoActivity();
	}
	
	public void dropRow(String componentID, OrGuiComponent component, int gridx, int gridy) {
		addChildToXML("drop-row", "drop-row", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "drop-row", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "drop-row", null, null);
		addChildToXML("constraints", "constraints", null, "drop-row", null, null);
		addChildToXML("x", "x", String.valueOf(gridx), "constraints", null, null);
		addChildToXML("y", "y", String.valueOf(gridy), "constraints", null, null);
		setUndoRedoActivity();
	}	
	
	public void cutComponent(String componentID, OrGuiComponent component, String containerID, OrGuiContainer container, String titleValueID) {
		PropertyNode position = component.getProperties().getChild("pos");
		int x = component.getPropertyValue(position.getChild("x")).intValue();
		int y = component.getPropertyValue(position.getChild("y")).intValue();
		int width = component.getPropertyValue(position.getChild("width")).intValue();
		int height = component.getPropertyValue(position.getChild("height")).intValue();
			
		String xID = getNextID();
		String yID = getNextID();
		String widthID = getNextID();
		String heightID = getNextID();
		values.put(xID, component.getPropertyValue(position.getChild("x")));
		values.put(yID, component.getPropertyValue(position.getChild("y")));
		values.put(widthID, component.getPropertyValue(position.getChild("width")));
		values.put(heightID, component.getPropertyValue(position.getChild("height")));
		
		addChildToXML("cut-component", "cut-component", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "cut-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "cut-component", null, null);
		addChildToXML("container-ID", "container-ID", containerID, "cut-component", null, null);
		addChildToXML("title-ID", "title-ID", titleValueID, "cut-component", null, null);
		addChildToXML("location", "location", null, "cut-component", null, null);
		addChildToXML("x", "x", String.valueOf(x), "location", null, null);
		addChildToXML("x-ID", "x-ID", xID, "location", null, null);
		addChildToXML("y", "y", String.valueOf(y), "location", null, null);
		addChildToXML("y-ID", "y-ID", yID, "location", null, null);
		addChildToXML("dimension", "dimension", null, "cut-component", null, null);
		addChildToXML("width", "width", String.valueOf(width), "dimension", null, null);
		addChildToXML("width-ID", "width-ID", widthID, "dimension", null, null);
		addChildToXML("height", "height", String.valueOf(height), "dimension", null, null);
		addChildToXML("height-ID", "height-ID", heightID, "dimension", null, null);
		setUndoRedoActivity();	
	}
	public void pasteComponent(String componentID, OrGuiComponent component, String containerID, int componentIndex) {
		addChildToXML("paste-component", "paste-component", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "paste-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "paste-component", null, null);
		addChildToXML("container-ID", "container-ID", containerID, "paste-component", null, null);
		addChildToXML("component-index", "component-index", String.valueOf(componentIndex), "paste-component", null, null);
		setUndoRedoActivity();
	}
	
	public void dropMovedComponent(String componentID, OrGuiComponent component) {
		addChildToXML("drop-moved-component", "drop-moved-component", null, "actions", null, null);
		addChildToXML("component-type", "component-type", component.getClass().getSimpleName(), "drop-moved-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "drop-moved-component", null, null);
		setUndoRedoActivity();
	}
	
	public void dropPastedComponent(String componentID, String containerID) {
		addChildToXML("drop-pasted-component", "drop-pasted-component", null, "actions", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "drop-pasted-component", null, null);
		addChildToXML("container-ID", "container-ID", containerID, "drop-pasted-component", null, null);
		setUndoRedoActivity();
	}
	
	public void moveComponent(String operationID, String componentID, String sourceContainerID, String targetContainerID, Point sourcePoint, Point targetPoint, int x, int y, int width, int height) {
		addChildToXML("move-component", "move-component", null, "actions", null, null);
		addChildToXML("operation-ID", "operation-ID", operationID, "move-component", null, null);
		addChildToXML("component-ID", "component-ID", componentID, "move-component", null, null);
		addChildToXML("source-container-ID", "source-container-ID", sourceContainerID, "move-component", null, null);
		addChildToXML("target-container-ID", "target-container-ID", targetContainerID, "move-component", null, null);
		
		addChildToXML("source-point", "source-point", null, "move-component", null, null);
		addChildToXML("x", "x", String.valueOf(sourcePoint.x), "source-point", null, null);
		addChildToXML("y", "y", String.valueOf(sourcePoint.y), "source-point", null, null);
		
		addChildToXML("target-point", "target-point", null, "move-component", null, null);
		addChildToXML("x", "x", String.valueOf(targetPoint.x), "target-point", null, null);
		addChildToXML("y", "y", String.valueOf(targetPoint.y), "target-point", null, null);
		
		addChildToXML("properties", "properties", null, "move-component", null, null);
		addChildToXML("x", "x", String.valueOf(x), "properties", null, null);
		addChildToXML("y", "y", String.valueOf(y), "properties", null, null);
		addChildToXML("width", "width", String.valueOf(width), "properties", null, null);
		addChildToXML("height", "height", String.valueOf(height), "properties", null, null);
		
		setUndoRedoActivity();
		
//		writeXMLFile("-redo", xmlDocumentRedo);
//		writeXMLFile("-undo", xmlDocumentUndo);
//		writeXMLFile("-save", xmlDocumentToSave);
	}	
	
	public void writeXMLFile(String fileName, Document xmlDocument) {
//		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
//		File xmlFile = new File("C:/Users/Администратор/Or3EE/" + String.valueOf(processID) + "_" + dateFormat.format(getEditingDate()) + ".xml");
		File xmlFile = new File("C:/xml/" + fileName + ".xml");		
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
	
	public void setUndoRedoActivity() {
		if (canClean) {
        	Node actionsElement = xmlDocumentRedo.getElementsByTagName("actions").item(0);
        	while (actionsElement.hasChildNodes()) {
        		actionsElement.removeChild(actionsElement.getLastChild());
        	}                	
        }
		DesignerFrame designerFrame = InterfaceActionsConteiner.getDesignerFrame();
		try {
			if (xmlDocumentUndo.getElementsByTagName("actions").item(0).hasChildNodes()) {
				designerFrame.undoItem.setText("Отменить (" + xmlDocumentUndo.getElementsByTagName("actions").item(0).getChildNodes().getLength() + ")");
				designerFrame.undoItem.setEnabled(true);
			} else {
				designerFrame.undoItem.setText("Отменить");
				designerFrame.undoItem.setEnabled(false);
			}
			if (xmlDocumentRedo.getElementsByTagName("actions").item(0).hasChildNodes()) {
				designerFrame.redoItem.setText("Повторить (" + xmlDocumentRedo.getElementsByTagName("actions").item(0).getChildNodes().getLength() + ")");
				designerFrame.redoItem.setEnabled(true);
			} else {
				designerFrame.redoItem.setText("Повторить");
				designerFrame.redoItem.setEnabled(false);
			}
		} catch (Exception e) {}
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
		try {
			Node actionsElementToSave = xmlDocumentToSave.getElementsByTagName("actions").item(0);
			NodeList list = actionsElementToSave.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				actionsElementToSave.removeChild(list.item(i));
			}
		} catch (NullPointerException e) {}
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
						break;
					}
				}
				if (actionObject == null) {
					actionObject = kernel.createObject(actionClass, 0);
				}
				kernel.setString(actionObject.id, actionClass.id, "name", 0, 0, interfaceName, 0);	
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
	
	public String convertXMLToString(Document xmlDocument) { 
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
