package kz.tamur.guidesigner;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.inspector.PropertyTableModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cifs.or2.kernel.KrnException;

public class CmdRedoAction {	

	private static InterfaceFrame selectedInterface;
	private static Node actionsElement;
	
	public static void Redo(DesignerFrame designerFrame) {		
		selectedInterface = DesignerFrame.tabbedContent.getSelectedFrame();
		if (InterfaceActionsConteiner.isContein(selectedInterface.getUiObject().id)) {
		    InterfaceActions ai = InterfaceActionsConteiner.getInterfaceActions(selectedInterface.getUiObject().id);
			actionsElement = ai.getXMLDocumentRedo().getElementsByTagName("actions").item(0);
			if (actionsElement.hasChildNodes()) {				
				String componentID = null;
				String containerID = null;
				ai.setCanClean(false);
				if (actionsElement.getLastChild().getNodeName().equals("add-component")) {
					Node addAction = actionsElement.getLastChild();	//последний add
					NodeList addActionElements = addAction.getChildNodes();
					for (int i = 0; i < addActionElements.getLength(); i++) {
						if (addActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = addActionElements.item(i).getTextContent();
						}
					}
					actionsElement.removeChild(addAction);					
					OrGuiComponent component = ai.getGUIComponent(componentID);
					designerFrame.getController().dropComponent(component);
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-component")) {
					Node dropAction = actionsElement.getLastChild();	//последний drop
					NodeList dropActionElements = dropAction.getChildNodes();	
					String titleValueID = null;
					Object x = null;
					Object y = null;
					Object height = null;
					Object width = null;
					for (int i = 0; i < dropActionElements.getLength(); i++) {
						if (dropActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = dropActionElements.item(i).getTextContent();
						} else if (dropActionElements.item(i).getNodeName().equals("container-ID")) {
							containerID = dropActionElements.item(i).getTextContent();
						} else if (dropActionElements.item(i).getNodeName().equals("title-ID")) {
							titleValueID = dropActionElements.item(i).getTextContent();				
						} else if (dropActionElements.item(i).getNodeName().equals("location")) {
							NodeList position = dropActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x-ID")) {
									x = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y-ID")) {
									y = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(position.item(j).getTextContent());
								}
							}
						} else if (dropActionElements.item(i).getNodeName().equals("dimension")) {
							NodeList dimension = dropActionElements.item(i).getChildNodes();
							for (int j = 0; j < dimension.getLength(); j++) {
								if (dimension.item(j).getNodeName().equals("height-ID")) {
									height = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(dimension.item(j).getTextContent());
								} else if (dimension.item(j).getNodeName().equals("width-ID")) {
									width = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(dimension.item(j).getTextContent());
								}
							}
						} 
					}					
					actionsElement.removeChild(dropAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					OrGuiContainer container = (OrGuiContainer) InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(containerID);
			    	if (container instanceof OrGuiContainer) {
			    		try {
			    			designerFrame.getController().createComponent(container, component);
						} catch (KrnException exception) {
							exception.printStackTrace();
						}			    		
			    		PropertyValue titleValue = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getTitleValue(titleValueID);
			    		GuiComponentItem componentItem = new GuiComponentItem(component, designerFrame);
						component.setPropertyValue(titleValue);
						component.setPropertyValue((PropertyValue) x);
						component.setPropertyValue((PropertyValue) y);
						component.setPropertyValue((PropertyValue) height);
						component.setPropertyValue((PropertyValue) width);
						designerFrame.getInspector().updateObject(componentItem);
			    	}					
				} else if (actionsElement.getLastChild().getNodeName().equals("change-property")) {
					String propertyID = null;
					String valueID = null;
					Node changePropertyAction = actionsElement.getLastChild();	//последний change-property
					NodeList changePropertyActionElements = changePropertyAction.getChildNodes();
					for (int i = 0; i < changePropertyActionElements.getLength(); i++) {
						if (changePropertyActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = changePropertyActionElements.item(i).getTextContent();
						} else if (changePropertyActionElements.item(i).getNodeName().equals("property-ID")) {
							propertyID = changePropertyActionElements.item(i).getTextContent();	
						} else if (changePropertyActionElements.item(i).getNodeName().equals("value-ID")) {
							valueID = changePropertyActionElements.item(i).getTextContent();	
						} 						
					}						
					actionsElement.removeChild(changePropertyAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					GuiComponentItem componentItem = new GuiComponentItem(component, designerFrame);
					Property property = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getProperties().get(propertyID);
					Object oldValue = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(valueID);
					PropertyTableModel tableModel = new PropertyTableModel(componentItem);					
					if (oldValue.getClass().getSimpleName().equals("String") && oldValue.equals("")) {
						tableModel.setValueAt(null, property, 1);
					} else {
						tableModel.setValueAt(oldValue, property, 1);
					}
					designerFrame.getInspector().updateObject(componentItem);
				}  else if (actionsElement.getLastChild().getNodeName().equals("add-column")) {
					Node addColumnAction = actionsElement.getLastChild();	//последний addColumn
					NodeList addColumnActionElements = addColumnAction.getChildNodes();
					int x = 0;
					for (int i = 0; i < addColumnActionElements.getLength(); i++) {
						if (addColumnActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = addColumnActionElements.item(i).getTextContent();
						} else if (addColumnActionElements.item(i).getNodeName().equals("constraints")) {
							NodeList constraints = addColumnActionElements.item(i).getChildNodes();
							for (int j = 0; j < constraints.getLength(); j++) {
								if (constraints.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(constraints.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(addColumnAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					((OrPanel) component).insertCols(x, -1);
					((OrPanel) component).validate();
					((OrPanel) component).repaint();
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-column")) {
					Node dropColumnAction = actionsElement.getLastChild();	//последний dropColumn
					NodeList dropColumnActionElements = dropColumnAction.getChildNodes();
					int x = 0;
					for (int i = 0; i < dropColumnActionElements.getLength(); i++) {
						if (dropColumnActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = dropColumnActionElements.item(i).getTextContent();
						} else if (dropColumnActionElements.item(i).getNodeName().equals("constraints")) {
							NodeList constraints = dropColumnActionElements.item(i).getChildNodes();
							for (int j = 0; j < constraints.getLength(); j++) {
								if (constraints.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(constraints.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(dropColumnAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					((OrPanel) component).insertCols(x, 1);
					((OrPanel) component).validate();
					((OrPanel) component).repaint();
				} else if (actionsElement.getLastChild().getNodeName().equals("add-row")) {
					Node addRowAction = actionsElement.getLastChild();	//последний addRow
					NodeList addRowActionElements = addRowAction.getChildNodes();
					int y = 0;
					for (int i = 0; i < addRowActionElements.getLength(); i++) {
						if (addRowActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = addRowActionElements.item(i).getTextContent();
						} else if (addRowActionElements.item(i).getNodeName().equals("constraints")) {
							NodeList constraints = addRowActionElements.item(i).getChildNodes();
							for (int j = 0; j < constraints.getLength(); j++) {
								if (constraints.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(constraints.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(addRowAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					((OrPanel) component).insertRows(y, -1);
					((OrPanel) component).validate();
					((OrPanel) component).repaint();
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-row")) {
					Node dropRowAction = actionsElement.getLastChild();	//последний dropRow
					NodeList dropRowActionElements = dropRowAction.getChildNodes();
					int y = 0;
					for (int i = 0; i < dropRowActionElements.getLength(); i++) {
						if (dropRowActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = dropRowActionElements.item(i).getTextContent();
						} else if (dropRowActionElements.item(i).getNodeName().equals("constraints")) {
							NodeList constraints = dropRowActionElements.item(i).getChildNodes();
							for (int j = 0; j < constraints.getLength(); j++) {
								if (constraints.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(constraints.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(dropRowAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					((OrPanel) component).insertRows(y, 1);
					((OrPanel) component).validate();
					((OrPanel) component).repaint();
				} else if (actionsElement.getLastChild().getNodeName().equals("paste-component")) {					
                    Node pasteComponentAction = actionsElement.getLastChild();	//последний pasteComponent
					NodeList pasteComponentActionElements = pasteComponentAction.getChildNodes();
					int componentIndex = 0;
					for (int i = 0; i < pasteComponentActionElements.getLength(); i++) {
						if (pasteComponentActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = pasteComponentActionElements.item(i).getTextContent();
						} else if (pasteComponentActionElements.item(i).getNodeName().equals("container-ID")) {
							containerID = pasteComponentActionElements.item(i).getTextContent();
						} else if (pasteComponentActionElements.item(i).getNodeName().equals("component-index")) {
							componentIndex = Integer.parseInt(pasteComponentActionElements.item(i).getTextContent());
						}
					}
					actionsElement.removeChild(pasteComponentAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(selectedInterface.getUiObject().id).getGUIComponent(componentID);
					if (component.isCopy()) {
						JLayeredPane layeredPane = ((JFrame) designerFrame.getTopLevelAncestor()).getLayeredPane();	
						layeredPane.remove(componentIndex);
						layeredPane.validate();
						layeredPane.repaint();
	                    InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).dropPastedComponent(componentID, containerID);				
					}
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-pasted-component")) {
					Node dropPastedComponentAction = actionsElement.getLastChild();	//последний dropPastedComponent
					NodeList dropPastedComponentActionElements = dropPastedComponentAction.getChildNodes();
					for (int i = 0; i < dropPastedComponentActionElements.getLength(); i++) {
						if (dropPastedComponentActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = dropPastedComponentActionElements.item(i).getTextContent();
						} else if (dropPastedComponentActionElements.item(i).getNodeName().equals("container-ID")) {
							containerID = dropPastedComponentActionElements.item(i).getTextContent();
						}
					}
					actionsElement.removeChild(dropPastedComponentAction);
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					OrGuiComponent container = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(containerID);
					if (component != null) {
						designerFrame.getController().addSelection(component, false);
						designerFrame.getController().copyToBuffer();
						designerFrame.getController().pasteComponent((OrGuiContainer) container);
					}
				} else if (actionsElement.getLastChild().getNodeName().equals("cut-component")) {					
					Node cutComponentAction = actionsElement.getLastChild();	//последний cutComponent
					NodeList cutComponentActionElements = cutComponentAction.getChildNodes();
					String titleValueID = null;
					Object x = null;
					Object y = null;
					Object height = null;
					Object width = null;
					for (int i = 0; i < cutComponentActionElements.getLength(); i++) {
						if (cutComponentActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = cutComponentActionElements.item(i).getTextContent();
						} else if (cutComponentActionElements.item(i).getNodeName().equals("container-ID")) {
							containerID = cutComponentActionElements.item(i).getTextContent();
						} else if (cutComponentActionElements.item(i).getNodeName().equals("title-ID")) {
							titleValueID = cutComponentActionElements.item(i).getTextContent();			
						} else if (cutComponentActionElements.item(i).getNodeName().equals("location")) {
							NodeList position = cutComponentActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x-ID")) {
									x = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y-ID")) {
									y = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(position.item(j).getTextContent());
								}
							}
						} else if (cutComponentActionElements.item(i).getNodeName().equals("dimension")) {
							NodeList dimension = cutComponentActionElements.item(i).getChildNodes();
							for (int j = 0; j < dimension.getLength(); j++) {
								if (dimension.item(j).getNodeName().equals("height-ID")) {
									height = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(dimension.item(j).getTextContent());
								} else if (dimension.item(j).getNodeName().equals("width-ID")) {
									width = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getValues().get(dimension.item(j).getTextContent());
								}
							}
						} 
					}					
					actionsElement.removeChild(cutComponentAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					OrGuiContainer container = (OrGuiContainer) InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(containerID);
			    	if (container instanceof OrGuiContainer) {
			    		try {
			    			designerFrame.getController().createComponent(container, component);
						} catch (KrnException exception) {
							exception.printStackTrace();
						}			    		
			    		PropertyValue titleValue = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getTitleValue(titleValueID);
			    		GuiComponentItem componentItem = new GuiComponentItem(component, designerFrame);
						component.setPropertyValue(titleValue);
						component.setPropertyValue((PropertyValue) x);
						component.setPropertyValue((PropertyValue) y);
						component.setPropertyValue((PropertyValue) height);
						component.setPropertyValue((PropertyValue) width);
						designerFrame.getInspector().updateObject(componentItem);
			    	}					
				} else if (actionsElement.getLastChild().getNodeName().equals("move-component")) {	
					Node moveComponentAction = actionsElement.getLastChild();	//последний moveComponent
					NodeList moveComponentActionElements = moveComponentAction.getChildNodes();
					
					String sourceContainerID = null;
					String targetContainerID = null;
					
					Point sourcePoint = null;
					Point targetPoint = null;
					
					int x = 0;
					int y = 0;
					int height = 0;
					int width = 0;
					
					for (int i = 0; i < moveComponentActionElements.getLength(); i++) {
						if (moveComponentActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = moveComponentActionElements.item(i).getTextContent();
						} else if (moveComponentActionElements.item(i).getNodeName().equals("source-container-ID")) {
							sourceContainerID = moveComponentActionElements.item(i).getTextContent();
						} else if (moveComponentActionElements.item(i).getNodeName().equals("target-container-ID")) {
							targetContainerID = moveComponentActionElements.item(i).getTextContent();						
						} else if (moveComponentActionElements.item(i).getNodeName().equals("source-point")) {
							NodeList source = moveComponentActionElements.item(i).getChildNodes();
							for (int j = 0; j < source.getLength(); j++) {
								if (source.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(source.item(j).getTextContent());
								} else if (source.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(source.item(j).getTextContent());
								}
							}
							sourcePoint = new Point(x, y);
						} else if (moveComponentActionElements.item(i).getNodeName().equals("target-point")) {
							NodeList target = moveComponentActionElements.item(i).getChildNodes();
							for (int j = 0; j < target.getLength(); j++) {
								if (target.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(target.item(j).getTextContent());
								} else if (target.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(target.item(j).getTextContent());
								}
							}
							targetPoint = new Point(x, y);
						} else if (moveComponentActionElements.item(i).getNodeName().equals("properties")) {
							NodeList properties = moveComponentActionElements.item(i).getChildNodes();
							for (int j = 0; j < properties.getLength(); j++) {
								if (properties.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(properties.item(j).getTextContent());
								} else if (properties.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(properties.item(j).getTextContent());
								} else if (properties.item(j).getNodeName().equals("width")) {
									width = Integer.parseInt(properties.item(j).getTextContent());
								} else if (properties.item(j).getNodeName().equals("height")) {
									height = Integer.parseInt(properties.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(moveComponentAction);
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					OrGuiContainer sourceContainer = (OrGuiContainer) InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(sourceContainerID);
					OrGuiContainer targetContainer = (OrGuiContainer) InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(targetContainerID);					
					designerFrame.getController().moveComponent(component, sourceContainer, targetContainer, sourcePoint, targetPoint, true, x, y, height, width);
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-moved-component")) {
					Node dropMovedComponentAction = actionsElement.getLastChild();	//последний dropMovedComponent
					NodeList dropMovedComponentActionElements = dropMovedComponentAction.getChildNodes();
					for (int i = 0; i < dropMovedComponentActionElements.getLength(); i++) {
						if (dropMovedComponentActionElements.item(i).getNodeName().equals("component-ID")) {
							componentID = dropMovedComponentActionElements.item(i).getTextContent();
						}
					}
					actionsElement.removeChild(dropMovedComponentAction);					
					OrGuiComponent component = InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).getGUIComponent(componentID);
					designerFrame.getController().dropComponent(component);
				}
				InterfaceActionsConteiner.getInterfaceActions(selectedInterface.getUiObject().getKrnObject().id).setCanClean(true);
			}
		}
	}	
}
			

