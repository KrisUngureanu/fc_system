package kz.tamur.guidesigner.service;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.service.cmd.CmdCopyProcess;
import kz.tamur.guidesigner.service.cmd.CmdDeleteProcess;
import kz.tamur.guidesigner.service.cmd.CmdRenameProcess;
import kz.tamur.guidesigner.service.cmd.CmdSaveProcess;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.AllMouseEventProcessor;
import kz.tamur.rt.Utils;

import org.tigris.gef.base.Cmd;
import org.w3c.dom.Node;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 30.09.2004
 * Time: 12:08:42
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTabbedContent extends OrBasicTabbedPane implements ActionListener {

    private Map<Long, Document> docs = new TreeMap<Long, Document>();
    private java.util.List<Long> docIds = new ArrayList<Long>();
    private MainFrame serviceFrame;

    private ImageIcon tabIcon = kz.tamur.rt.Utils.getImageIcon("ServiceTab");
    private ImageIcon tabModIcon = kz.tamur.rt.Utils.getImageIcon("ServiceTabMod");
    private ImageIcon tabIconRO = kz.tamur.rt.Utils.getImageIcon("ServiceTabRO");

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miSave = createMenuItem("Сохранить");
    private JMenuItem miClose = createMenuItem("Закрыть");
    private JMenuItem miCopy = createMenuItem("Создать копию", "CopyProcess");
    private JMenuItem miRename = createMenuItem("Переименовать", "RenameProcess");
    private JMenuItem miDelete = createMenuItem("Удалить");
    private JMenuItem miHistory = createMenuItem("Показать историю");
    //Commands
    private Cmd saveCmd;
    //private Cmd closeCmd;
    private Cmd renameCmd;
    private Cmd copyCmd;
    private Cmd deleteCmd;

    public ServiceTabbedContent(final MainFrame mainFrame) {
        super();
    	this.serviceFrame = mainFrame;    	
    	this.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {
				if (getSelectedIndex() != -1) {
					if (ServiceActionsConteiner.isContein(getSelectedDocument().getKrnObject().id)) {
						ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).setCanClean(false);
						ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).setUndoRedoActivity(serviceFrame);
						ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).setCanClean(true);
					} else {
						ServiceActionsConteiner.resetUndoRedoActivity();
					}
				}
			}
		});
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				int keyCode = event.getKeyCode();
				if (keyCode == KeyEvent.VK_H &&  event.isControlDown()) {
					new ObjectHistory(getSelectedDocument().getKrnObject().id, "Процесс");
				}
			}
			
			public void keyReleased(KeyEvent event) {}    	
			public void keyTyped(KeyEvent event) {}
		});
        setFont(Utils.getDefaultFont());
        initCommands();
        pm.add(miSave);
        pm.add(miClose);
        pm.addSeparator();
        pm.add(miCopy);
        pm.addSeparator();
        pm.add(miRename);
        pm.add(miDelete);
        pm.addSeparator();
        pm.add(miHistory);
        KeyStroke ctrlH;        
        ctrlH = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK);        
        miHistory.setAccelerator(ctrlH);
        for (int i = 0; i < pm.getComponentCount(); i++) {
            Component c = pm.getComponent(i);
            if (c instanceof JMenuItem) {
                ((JMenuItem)c).addActionListener(this);
            }
        }
        addMouseListener(new AllMouseEventProcessor() {
            public void process(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int idx = getSelectedIndex();
                    if (idx > -1) {
                        Document doc = getSelectedDocument();
                        if (doc != null && doc.isReadOnly()) {
                            miSave.setEnabled(false);
                            miCopy.setEnabled(false);
                            miRename.setEnabled(false);
                            miDelete.setEnabled(false);
                        } else {
                            miSave.setEnabled(true);
                            miCopy.setEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.PROCESS_CREATE_RIGHT));
                            miRename.setEnabled(true);
                            miDelete.setEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.PROCESS_DELETE_RIGHT));
                        }
                    }
                    pm.show(ServiceTabbedContent.this, e.getX(), e.getY());
                }
            }
        });
    }

    private void initCommands() {
        saveCmd = new CmdSaveProcess("Save", serviceFrame, CmdSaveProcess.SAVE_CURRENT);
        copyCmd = new CmdCopyProcess("Copy", serviceFrame, true);
        renameCmd = new CmdRenameProcess("Rename", serviceFrame, true);
        deleteCmd = new CmdDeleteProcess("Delete", serviceFrame);
    }

    public void renameTab(String newTitle) {
        Long id = docIds.get(getSelectedIndex());
        Document doc = docs.get(id);
        doc.setTitle(newTitle);
        setTitleAt(getSelectedIndex(), newTitle);
    }

    public void addServiceTab(final Document doc) {
        ImageIcon image = (doc.isReadOnly()) ? tabIconRO : tabIcon;
        docs.put(doc.getKrnObject().id, doc);
        docIds.add(doc.getKrnObject().id);
        addTab(doc.getTitle(), image, doc.getGraph());        
        setSelectedIndex(getTabCount() - 1);
        
        doc.getGraph().addMouseListener(new MouseListener() {
        	private Map<String, Vector> currentPoints = new HashMap<String, Vector>();
        	private Map<String, Vector> newPointss = new HashMap<String, Vector>();
        	
        	private String edgeID = null;
        	private Vector<Point> oldPoints = new Vector<Point>();
        	private Vector<Point> newPoints = new Vector<Point>();
        	private TransitionEdge currentEdge = null;
        	
			public void mouseReleased(MouseEvent e) {
				if (doc.getGraph().selectedFigs().size() == 1 && doc.getGraph().selectedFigs().get(0) instanceof FigTransitionEdge) {
					Point addedPoint = null;
					Point removedPoint = null;
					Point replacedPointBefore = null;
					Point replacedPointAfter = null;
					int pointID = -1;
					List<TransitionEdge> edges = getSelectedDocument().getModel().getEdges();
					for (int i = 0; i < edges.size(); i++) {
						if (edges.get(i).getId().equals(edgeID)) {
							newPoints = edges.get(i).getPoints();
							currentEdge = edges.get(i);
							break;
						}
					}
					if (newPoints.size() > oldPoints.size()) {
						for (int i = 1; i < oldPoints.size(); i++) {							
							if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
								addedPoint = (Point) newPoints.get(i);
								pointID = i;
								break;
							}
						}
		                ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).addPointToEdge(currentEdge, addedPoint, pointID);
		                serviceFrame.setProcessModified(true);
					} else if (newPoints.size() < oldPoints.size()) {
						for (int i = 1; i < newPoints.size(); i++) {
							if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
								removedPoint = (Point) oldPoints.get(i);
								pointID = i;
								break;
							}						
						}				
			            ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).removePointFromEdge(currentEdge, removedPoint, pointID);
		                serviceFrame.setProcessModified(true);
					} else if (newPoints.size() == oldPoints.size()) {
						for (int i = 1; i < newPoints.size() - 1; i++) {							
							if (!((Point) newPoints.get(i)).equals((Point) oldPoints.get(i))) {
								replacedPointBefore = (Point) oldPoints.get(i);
								replacedPointAfter = (Point) newPoints.get(i);
								pointID = i;
					            ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).replacePointInEdge(currentEdge, replacedPointBefore, replacedPointAfter, pointID);
				                serviceFrame.setProcessModified(true);
								break;
							}							
						}
					}
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (doc.getGraph().selectedFigs().size() == 1 && doc.getGraph().selectedFigs().get(0) instanceof FigTransitionEdge) {
					List<TransitionEdge> edges = getSelectedDocument().getModel().getEdges();
					for(int i = 0; i < edges.size(); i++) {
						if(edges.get(i).getPresentation().equals(doc.getGraph().selectedFigs().get(0))) {
							edgeID = edges.get(i).getId();
							oldPoints = edges.get(i).getPoints();
							break;
						}
					}
				}				
			}
			
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
    }

    public boolean isTabExist(Long id) {
        if (docs.containsKey(id)) {
            setSelectedComponent((docs.get(id)).getGraph());
            return true;
        }  else {
            return false;
        }
    }

    public void removeCurrentTab() {
        remove(getSelectedIndex());
    }

    public Document getDocument(int idx) {
        Long objId = docIds.get(idx);
        return docs.get(objId);
    }

    public Document getSelectedDocument() {
        if (getSelectedIndex() > -1) {
            Long objId = docIds.get(getSelectedIndex());
            return docs.get(objId);
        }
        else return null;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Cmd command = null;
        if (src == miSave) {
            command = saveCmd;
        } else if (src == miClose) {
        	try {
        		removeCurrent();
        	} catch (KrnException ex) {
        		ex.printStackTrace();
        	}
            return;
        } else if (src == miCopy) {
            command = copyCmd;
        } else if (src == miRename) {
            command = renameCmd;
        } else if (src == miDelete) {
            command = deleteCmd;        
	    } else if (src == miHistory) {
	    	new ObjectHistory(this.getSelectedDocument().getKrnObject().id, "Процесс");
	    }
        if (command != null) {
            command.doIt();
        }
    }

    public void removeCurrent() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = docIds.get(idx);
        Icon icon = getIconAt(idx);
        Document curService = docs.get(objId);
        if (icon == tabModIcon) {
            int res = MessagesFactory.showMessageDialog((JFrame)serviceFrame.getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, "Процесс '" +
                    getTitleAt(idx) + "' модифицирован.\n" +
                    "Сохранить изменения?");
            switch(res) {
                case ButtonsFactory.BUTTON_YES:
                    saveCmd.doIt();
                    docs.remove(objId);
                    docIds.remove(idx);
                    remove(idx);
                    Kernel.instance().releaseEngagedObject(objId);
                    break;
                case ButtonsFactory.BUTTON_NO:
                    docs.remove(objId);
                    docIds.remove(idx);
                    remove(idx);
                    Kernel.instance().releaseEngagedObject(objId);
                    ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).removeChanges();
                    break;
                case ButtonsFactory.BUTTON_CANCEL:
                    return;
            }
        } else {
            Document doc = docs.remove(objId);   
            docIds.remove(idx);
            remove(idx);
            if (!doc.isReadOnly())
                Kernel.instance().releaseEngagedObject(objId);
        }
        if (ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).canSave()) {
        	ServiceActionsConteiner.getServiceActions(curService.getKrnObject().id).writeObject();
	        serviceFrame.loadServices();
        }
        ServiceActionsConteiner.removeFromConteiner(curService.getKrnObject().id);
        fireChange();
    }
    
    public void settt() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = docIds.get(idx);
        Kernel.instance().releaseEngagedObject(objId);
    }

	public void setProcessModified(boolean isModified) {
        if (getSelectedIndex() != -1) {
            Document d = docs.get(docIds.get(getSelectedIndex()));
            if (!d.isReadOnly()) {
                if (isModified) {
                    setIconAt(getSelectedIndex(), tabModIcon);
                } else {
                    setIconAt(getSelectedIndex(), tabIcon);
                }
                if (ServiceActionsConteiner.isContein(getSelectedDocument().getKrnObject().id)) {
	                if (ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).getCanClean()) {
	                	Node actionsElement = ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).getXMLDocumentRedo().getElementsByTagName("actions").item(0);
	                	while (actionsElement.hasChildNodes()) {
	                		actionsElement.removeChild(actionsElement.getLastChild());
	                	}                	
	                }
	                ServiceActionsConteiner.getServiceActions(getSelectedDocument().getKrnObject().id).setUndoRedoActivity(serviceFrame);
                }
            }
        }
    }

    public boolean isTabModified() {
        return tabModIcon.equals(getIconAt(getSelectedIndex()));
    }
    
    public Document getFrameAt(int idx) {
        return docs.get(docIds.get(idx));
    }

    public void fireChange() {
        fireStateChanged();
    }

}