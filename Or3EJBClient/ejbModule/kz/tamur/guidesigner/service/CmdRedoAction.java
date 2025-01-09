package kz.tamur.guidesigner.service;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kz.tamur.guidesigner.service.ui.ActivityStateNode;
import kz.tamur.guidesigner.service.ui.DecisionStateNode;
import kz.tamur.guidesigner.service.ui.EndStateNode;
import kz.tamur.guidesigner.service.ui.EndSyncNode;
import kz.tamur.guidesigner.service.ui.ForkNode;
import kz.tamur.guidesigner.service.ui.InBoxStateNode;
import kz.tamur.guidesigner.service.ui.JoinNode;
import kz.tamur.guidesigner.service.ui.NoteStateNode;
import kz.tamur.guidesigner.service.ui.OutBoxStateNode;
import kz.tamur.guidesigner.service.ui.ProcessStateNode;
import kz.tamur.guidesigner.service.ui.ReportStateNode;
import kz.tamur.guidesigner.service.ui.StartStateNode;
import kz.tamur.guidesigner.service.ui.StartSyncNode;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.SubProcessStateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;

import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.graph.presentation.NetEdge;
import org.tigris.gef.graph.presentation.NetPort;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CmdRedoAction {
	
	private static Document selectedDocument;
	private static Node actionsElement;
	
	public static void Redo(MainFrame mainFrame) {		
		selectedDocument = mainFrame.getTabbedContent().getSelectedDocument();
		if (ServiceActionsConteiner.isContein(selectedDocument.getKrnObject().id)) {
			actionsElement = ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).getXMLDocumentRedo().getElementsByTagName("actions").item(0);
			String nodeID = null;
			String edgeID = null;
			String nodeClass = null;
			String nodeName = null;
			String edgeName = null;
			StateNode sNode = null;
			String oldValue = null;
			int x = 0;
			int y = 0;
			int height = 0;
			int width = 0;
			Editor currentEditor;
            SelectionManager selManager;
			if (actionsElement.hasChildNodes()) {
				ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).setCanClean(false);
				if (actionsElement.getLastChild().getNodeName().equals("add-node")) {
					currentEditor = Globals.curEditor();
	                selManager = currentEditor.getSelectionManager();
					Node addAction = actionsElement.getLastChild();	//последний add
					NodeList addActionElements = addAction.getChildNodes();
					for (int i = 0; i < addActionElements.getLength(); i++) {
						if (addActionElements.item(i).getNodeName().equals("node-ID")) {
							nodeID = addActionElements.item(i).getTextContent();
						}
					}
					actionsElement.removeChild(addAction);
					if (!nodeID.equals(null)) {
						Map<String, StateNode> nodes = selectedDocument.getModel().getNodesMap();
						selectedDocument.getModel().removeNode(nodes.get(nodeID));	
						selManager.deselectAll();
					}
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-node")) {
					Node dropAction = actionsElement.getLastChild();	//последний drop
					NodeList dropActionElements = dropAction.getChildNodes();
					for (int i = 0; i < dropActionElements.getLength(); i++) {
						if (dropActionElements.item(i).getNodeName().equals("node-ID")) {
							nodeID = dropActionElements.item(i).getTextContent();
						} else if (dropActionElements.item(i).getNodeName().equals("node-class")) {
							nodeClass = dropActionElements.item(i).getTextContent();
						} else if (dropActionElements.item(i).getNodeName().equals("node-name")) {					
							nodeName = dropActionElements.item(i).getTextContent();
						} else if (dropActionElements.item(i).getNodeName().equals("node-location")) {
							NodeList position = dropActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						} else if (dropActionElements.item(i).getNodeName().equals("node-dimension")) {
							NodeList dimension = dropActionElements.item(i).getChildNodes();
							for (int j = 0; j < dimension.getLength(); j++) {
								if (dimension.item(j).getNodeName().equals("height")) {
									height = Integer.parseInt(dimension.item(j).getTextContent());
								} else if (dimension.item(j).getNodeName().equals("width")) {
									width = Integer.parseInt(dimension.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(dropAction);
					if (!nodeID.equals(null) && !nodeClass.equals(null)) { 
						if (nodeClass.equals("ActivityStateNode")) {
							sNode = new ActivityStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("DecisionStateNode")) {
							sNode = new DecisionStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("EndStateNode")) {
							sNode = new EndStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("EndSyncNode")) {
							sNode = new EndSyncNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("ForkNode")) {
							sNode = new ForkNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("InBoxStateNode")) {
							sNode = new InBoxStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("JoinNode")) {
							sNode = new JoinNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("NoteStateNode")) {
							sNode = new NoteStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("OutBoxStateNode")) {
							sNode = new OutBoxStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("ProcessStateNode")) {
							sNode = new ProcessStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("ReportStateNode")) {
							sNode = new ReportStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("StartStateNode")) {
							sNode = new StartStateNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("StartSyncNode")) {
							sNode = new StartSyncNode(nodeID, selectedDocument.getModel());
						} else if (nodeClass.equals("SubProcessStateNode")) {
							sNode = new SubProcessStateNode(nodeID, selectedDocument.getModel());
						} 			
						sNode.initialize(null);
						selectedDocument.getModel().addNode(sNode);	
						NodeProperty[] propNames = ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).getElementsPropertyNames(nodeID);
						Object[] propValues = ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).getElementsPropertyValues(nodeID);
						for (int k = 0; k < propNames.length; k++) {
							sNode.setProperty(propNames[k], propValues[k]);
						}
						ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).setUndoRedoCall(true);
						sNode.getPresentation().setX(x);
						sNode.getPresentation().setY(y);
						sNode.getPresentation().setHeight(height);
						sNode.getPresentation().setWidth(width);
						ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).setUndoRedoCall(false);
						sNode.setName(nodeName);						
					}
				} else if (actionsElement.getLastChild().getNodeName().equals("change-property")) {
					currentEditor = Globals.curEditor();
	                selManager = currentEditor.getSelectionManager();
	                String propertyID = null;
					Node changePropertyAction = actionsElement.getLastChild();	//последний change-property
					NodeList changePropertyActionElements = changePropertyAction.getChildNodes();
					for (int i = 0; i < changePropertyActionElements.getLength(); i++) {
						if (changePropertyActionElements.item(i).getNodeName().equals("node-ID")) {
							nodeID = changePropertyActionElements.item(i).getTextContent();
						} else if (changePropertyActionElements.item(i).getNodeName().equals("node-name")) {					
							nodeName = changePropertyActionElements.item(i).getTextContent();	
						} else if (changePropertyActionElements.item(i).getNodeName().equals("property-ID")) {
							propertyID = changePropertyActionElements.item(i).getTextContent();	
						} else if (changePropertyActionElements.item(i).getNodeName().equals("old-value")) {
							oldValue = changePropertyActionElements.item(i).getTextContent();	
						}									
					}						
					actionsElement.removeChild(changePropertyAction);
					StateNode node = selectedDocument.getModel().getNodesMap().get(nodeID);
					if (node.getModel() != null) {
						new ServiceItem(node, mainFrame).setValue(ServiceActionsConteiner.getServiceActions(node.getModel().geKrnObject()).getProperty(propertyID), oldValue);
						selManager.deselectAll();
						selManager.select(selectedDocument.getModel().getNodesMap().get(nodeID).getPresentation());
					} else {
						Long modelID = ((ServiceModel)selectedDocument.getGraph().getEditor().getGraphModel()).geKrnObject();
						new ServiceItem(node, mainFrame).setValue(ServiceActionsConteiner.getServiceActions(modelID).getProperty(propertyID), oldValue);
						selManager.deselectAll();
						selManager.select(((ServiceModel)selectedDocument.getGraph().getEditor().getGraphModel()).getNodesMap().get(nodeID).getPresentation());
					}						
				} else if (actionsElement.getLastChild().getNodeName().equals("change-location")) {
					Node changeLocationAction = actionsElement.getLastChild();	//последний change-location
					NodeList changeLocationActionElements = changeLocationAction.getChildNodes();
					for (int i = 0; i < changeLocationActionElements.getLength(); i++) {
						if (changeLocationActionElements.item(i).getNodeName().equals("node-ID")) {
							nodeID = changeLocationActionElements.item(i).getTextContent();
						} else if (changeLocationActionElements.item(i).getNodeName().equals("node-name")) {					
							nodeName = changeLocationActionElements.item(i).getTextContent();							
						} else if (changeLocationActionElements.item(i).getNodeName().equals("old-location") || changeLocationActionElements.item(i).getNodeName().equals("location")) {
							NodeList position = changeLocationActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						} else if (changeLocationActionElements.item(i).getNodeName().equals("old-dimension") || changeLocationActionElements.item(i).getNodeName().equals("dimension")) {
							NodeList dimension = changeLocationActionElements.item(i).getChildNodes();
							for (int j = 0; j < dimension.getLength(); j++) {
								if (dimension.item(j).getNodeName().equals("height")) {
									height = Integer.parseInt(dimension.item(j).getTextContent());
								} else if (dimension.item(j).getNodeName().equals("width")) {
									width = Integer.parseInt(dimension.item(j).getTextContent());
								}
							}
						} 							
					}
					actionsElement.removeChild(changeLocationAction);				
					Rectangle oldRectangle = new Rectangle(x, y, width, height);
					if (!nodeID.equals(null)) {							
						selectedDocument.getModel().getNodesMap().get(nodeID).getPresentation().setBounds(oldRectangle);
						selectedDocument.getGraph().repaint();
						((StateNode)selectedDocument.getModel().getNodesMap().get(nodeID)).getPresentation().updateEdges();
					}
				} else if (actionsElement.getLastChild().getNodeName().equals("add-edge")) {		
					NetPort sourcePort = null;
	                NetPort destinationPort = null;
	                currentEditor = Globals.curEditor();
	                selManager = currentEditor.getSelectionManager();
					Node addEdgeAction = actionsElement.getLastChild();	//последний add-edge
					NodeList addEdgeActionElements = addEdgeAction.getChildNodes();
					for (int i = 0; i < addEdgeActionElements.getLength(); i++) {
						if (addEdgeActionElements.item(i).getNodeName().equals("edge-ID")) {
							edgeID = addEdgeActionElements.item(i).getTextContent();	
							break;
						}
					}
					actionsElement.removeChild(addEdgeAction);		
					if (!edgeID.equals(null)) {
						List<NetEdge> edges = selectedDocument.getModel().getEdges();
						for (int i = 0; i < edges.size(); i++) {
							if (((NetEdge) edges.get(i)).getId().equals(edgeID)) {
								sourcePort = ((NetEdge) edges.get(i)).getSourcePort();
				                destinationPort = ((NetEdge) edges.get(i)).getDestPort();
				                if (sourcePort != null)
									sourcePort.removeEdge((TransitionEdge) edges.get(i));
								if (destinationPort != null)
									destinationPort.removeEdge((TransitionEdge) edges.get(i));	
								selectedDocument.getModel().removeEdge(edges.get(i));
								break;
							}
						}
						selManager.deselectAll();
					}							
				} else if (actionsElement.getLastChild().getNodeName().equals("drop-edge")) {
					String sourceId = null;
					String destinationId = null;
					Vector<Point> edgePoints = new Vector<Point>();
					Node dropEdgeAction = actionsElement.getLastChild();	//последний drop-edge
					NodeList dropEdgeActionElements = dropEdgeAction.getChildNodes();
					for (int i = 0; i < dropEdgeActionElements.getLength(); i++) {
						if (dropEdgeActionElements.item(i).getNodeName().equals("edge-ID")) {
							edgeID = dropEdgeActionElements.item(i).getTextContent();													
						} else if (dropEdgeActionElements.item(i).getNodeName().equals("edge-name")) {
							edgeName = dropEdgeActionElements.item(i).getTextContent();													
						} else if (dropEdgeActionElements.item(i).getNodeName().equals("edge-source")) {
							NodeList edgeSource = dropEdgeActionElements.item(i).getChildNodes();
							for (int j = 0; j < edgeSource.getLength(); j++) {
								if (edgeSource.item(j).getNodeName().equals("source-id")) {
									sourceId = edgeSource.item(j).getTextContent();
									break;
								} 
							}						
						} else if (dropEdgeActionElements.item(i).getNodeName().equals("edge-destination")) {
							NodeList edgeDestination = dropEdgeActionElements.item(i).getChildNodes();
							for (int j = 0; j < edgeDestination.getLength(); j++) {
								if (edgeDestination.item(j).getNodeName().equals("destination-id")) {
									destinationId = edgeDestination.item(j).getTextContent();
									break;
								} 
							}			
						} else if (dropEdgeActionElements.item(i).getNodeName().equals("point")) {
							NodeList pointPosition = dropEdgeActionElements.item(i).getChildNodes();
							for (int j = 0; j < pointPosition.getLength(); j++) {
								if (pointPosition.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(pointPosition.item(j).getTextContent());
								} else if (pointPosition.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(pointPosition.item(j).getTextContent());
								}
							}
							edgePoints.add(new Point(x, y));
						}								
					}
					actionsElement.removeChild(dropEdgeAction);		
					TransitionEdge edge = new TransitionEdge(edgeID, selectedDocument.getModel());
					edge.setName(edgeName);
                    edge.connect(selectedDocument.getModel(), selectedDocument.getModel().getNodesMap().get(sourceId).getPort(0), selectedDocument.getModel().getNodesMap().get(destinationId).getPort(0));
                    try {
                    	selectedDocument.getModel().addEdge(edge);
                        edge.setPoints(edgePoints);
                    } catch (Exception e) {}
				} else if (actionsElement.getLastChild().getNodeName().equals("add-point")) {	
					int pointID = -1;
					Node addPointAction = actionsElement.getLastChild();	//последний add-point
					NodeList addPointActionElements = addPointAction.getChildNodes();
					for (int i = 0; i < addPointActionElements.getLength(); i++) {
						if (addPointActionElements.item(i).getNodeName().equals("edge-ID")) {
							edgeID = addPointActionElements.item(i).getTextContent();	
						} else if (addPointActionElements.item(i).getNodeName().equals("point-ID")) {
							pointID = Integer.parseInt(addPointActionElements.item(i).getTextContent());	
						} else if (addPointActionElements.item(i).getNodeName().equals("location")) {
							NodeList position = addPointActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(addPointAction);					
					if (!edgeID.equals(null)) {
						List<NetEdge> edges = selectedDocument.getModel().getEdges();
						for (int i = 0; i < edges.size(); i++) {
							if (((NetEdge) edges.get(i)).getId().equals(edgeID)) {	
								Vector<Point> pointsVector = ((TransitionEdge)edges.get(i)).getPoints();
								pointsVector.remove(pointID);
								((TransitionEdge)edges.get(i)).setPoints(pointsVector);
								ArrayList<StateNode> nodesArray = new ArrayList<StateNode> (((ServiceModel) selectedDocument.getGraph().getEditor().getGraphModel()).getNodesMap().values());
								for (int j = 0; j < nodesArray.size(); j ++) {
									((StateNode) nodesArray.get(j)).getPresentation().updateEdges();
								}
								selectedDocument.getGraph().repaint();
					            ServiceActionsConteiner.getServiceActions(mainFrame.getSelectedDocument().getKrnObject().id).removePointFromEdge((TransitionEdge) edges.get(i), new Point(x, y), pointID);
					            mainFrame.setProcessModified(true);
								break;
							}
						}
					}			
				} else if (actionsElement.getLastChild().getNodeName().equals("remove-point")) {	
					int pointID = -1;
					Node removePointAction = actionsElement.getLastChild();	//последний remove-point
					NodeList removePointActionElements = removePointAction.getChildNodes();
					for (int i = 0; i < removePointActionElements.getLength(); i++) {
						if (removePointActionElements.item(i).getNodeName().equals("edge-ID")) {
							edgeID = removePointActionElements.item(i).getTextContent();	
						} else if (removePointActionElements.item(i).getNodeName().equals("point-ID")) {
							pointID = Integer.parseInt(removePointActionElements.item(i).getTextContent());	
						} else if (removePointActionElements.item(i).getNodeName().equals("location")) {
							NodeList position = removePointActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						}
					}
					actionsElement.removeChild(removePointAction);						
					if (!edgeID.equals(null)) {
						List<NetEdge> edges = selectedDocument.getModel().getEdges();
						for (int i = 0; i < edges.size(); i++) {
							if (((NetEdge) edges.get(i)).getId().equals(edgeID)) {	
								Vector<Point> pointsVector = ((TransitionEdge)edges.get(i)).getPoints();
								pointsVector.add(pointID, new Point(x, y));
								((TransitionEdge)edges.get(i)).setPoints(pointsVector);
								ArrayList<StateNode> nodesArray = new ArrayList<StateNode> (((ServiceModel) selectedDocument.getGraph().getEditor().getGraphModel()).getNodesMap().values());
								for (int j = 0; j < nodesArray.size(); j ++) {
									((StateNode) nodesArray.get(j)).getPresentation().updateEdges();
								}
								selectedDocument.getGraph().repaint();
					            ServiceActionsConteiner.getServiceActions(mainFrame.getSelectedDocument().getKrnObject().id).addPointToEdge((TransitionEdge) edges.get(i), new Point(x, y), pointID);
					            mainFrame.setProcessModified(true);
								break;
							}
						}
					}			
				} else if (actionsElement.getLastChild().getNodeName().equals("replace-point")) {	
					int pointID = -1;
					int newX = 0;
					int newY = 0;
					Node replacePointAction = actionsElement.getLastChild();	//последний replace-point
					NodeList replacePointActionElements = replacePointAction.getChildNodes();
					for (int i = 0; i < replacePointActionElements.getLength(); i++) {
						if (replacePointActionElements.item(i).getNodeName().equals("edge-ID")) {
							edgeID = replacePointActionElements.item(i).getTextContent();	
						} else if (replacePointActionElements.item(i).getNodeName().equals("point-ID")) {
							pointID = Integer.parseInt(replacePointActionElements.item(i).getTextContent());	
						} else if (replacePointActionElements.item(i).getNodeName().equals("old-location")) {
							NodeList position = replacePointActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									x = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									y = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						} else if (replacePointActionElements.item(i).getNodeName().equals("new-location")) {
							NodeList position = replacePointActionElements.item(i).getChildNodes();
							for (int j = 0; j < position.getLength(); j++) {
								if (position.item(j).getNodeName().equals("x")) {
									newX = Integer.parseInt(position.item(j).getTextContent());
								} else if (position.item(j).getNodeName().equals("y")) {
									newY = Integer.parseInt(position.item(j).getTextContent());
								}
							}
						}
					}					
					if (!edgeID.equals(null)) {
						List<NetEdge> edges = selectedDocument.getModel().getEdges();
						for (int i = 0; i < edges.size(); i++) {
							if (((NetEdge) edges.get(i)).getId().equals(edgeID)) {	
								Vector pointsVector = ((TransitionEdge)edges.get(i)).getPoints();
								((Point) pointsVector.get(pointID)).setLocation(new Point(x, y));
								((TransitionEdge)edges.get(i)).setPoints(pointsVector);
								ArrayList<StateNode> nodesArray = new ArrayList<StateNode> (((ServiceModel) selectedDocument.getGraph().getEditor().getGraphModel()).getNodesMap().values());
								for (int j = 0; j < nodesArray.size(); j ++) {
									((StateNode) nodesArray.get(j)).getPresentation().updateEdges();
								}
								selectedDocument.getGraph().repaint();
					            ServiceActionsConteiner.getServiceActions(mainFrame.getSelectedDocument().getKrnObject().id).replacePointInEdge((TransitionEdge) edges.get(i), new Point(newX, newY), new Point(x, y), pointID);
					            mainFrame.setProcessModified(true);
								break;
							}
						}
					}			
				}
				ServiceActionsConteiner.getServiceActions(selectedDocument.getKrnObject().id).setCanClean(true);
			}			
		}		
	}	
}

