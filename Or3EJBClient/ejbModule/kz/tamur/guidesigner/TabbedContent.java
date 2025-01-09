package kz.tamur.guidesigner;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.InterfaceTree.InterfaceTreeModel;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.service.ObjectHistory;
import kz.tamur.guidesigner.service.ServicesTree.ServiceTreeModel;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.AllMouseEventProcessor;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.rt.Utils;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.05.2004
 * Time: 10:55:35
 */
public class TabbedContent extends OrBasicTabbedPane implements PropertyListener, PropertyChangeListener, OrFrame {

    private boolean isFullScreen = false;

    private Map<Long, InterfaceFrame> interfaces = new TreeMap<Long, InterfaceFrame>();
    private List<Long> interfaceObjIds = new ArrayList<Long>();

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miSave = createMenuItem("Сохранить");
    private JMenuItem miClose = createMenuItem("Закрыть");
    private JMenuItem miCopy = createMenuItem("Создать копию");
    private JMenuItem miRename = createMenuItem("Переименовать");
    private JMenuItem miDelete = createMenuItem("Удалить");
    private JMenuItem miHistory = createMenuItem("Показать историю");
    
    private static TabbedContent tabbedContent;

    private ImageIcon iconNorm = kz.tamur.rt.Utils.getImageIcon("FormTab");
    private ImageIcon iconRO = kz.tamur.rt.Utils.getImageIcon("FormTabRO");
    private ImageIcon iconMod = kz.tamur.rt.Utils.getImageIcon("FormTabMod");
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public static TabbedContent instance() {
        if (tabbedContent == null) {
            tabbedContent = new TabbedContent();
        }
        return tabbedContent;
    }

    protected TabbedContent() {
        super();
        this.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {
				if (getSelectedIndex() != -1 && InterfaceActionsConteiner.isContein(getSelectedKrnObject().id)) {
					InterfaceActionsConteiner.getInterfaceActions(getSelectedKrnObject().id).setCanClean(false);
					InterfaceActionsConteiner.getInterfaceActions(getSelectedKrnObject().id).setUndoRedoActivity();
					InterfaceActionsConteiner.getInterfaceActions(getSelectedKrnObject().id).setCanClean(true);
				} else {
					InterfaceActionsConteiner.resetUndoRedoActivity();
				}
			}
		});
    	this.addKeyListener(new KeyListener() {
    		public void keyPressed(KeyEvent event) {
    			int keyCode = event.getKeyCode();
    			if (keyCode == KeyEvent.VK_H &&  event.isControlDown()) {
    		    	new ObjectHistory(getSelectedKrnObject().id, "Интерфейс");
    			}
    		}
    		
    		public void keyReleased(KeyEvent event) {}    	
    		public void keyTyped(KeyEvent event) {}
		});
        setFont(Utils.getDefaultFont());
        setBorder(null);
        miSave.setIcon(kz.tamur.rt.Utils.getImageIcon("Save"));
        miSave.setEnabled(false);
        miSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCurrent();
            }
        });
        miClose.setEnabled(false);
        miClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try {
            		removeCurrent();
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
        });
        miCopy.setEnabled(false);
        miCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyInterface();
            }
        });
        miRename.setEnabled(false);
        miRename.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameInterface();
            }
        });
        miDelete.setEnabled(false);
        miDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteInterface();
            }
        });
        
        KeyStroke ctrlH = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK);        
        miHistory.setAccelerator(ctrlH);
        miHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	new ObjectHistory(getSelectedKrnObject().id, "Интерфейс");
			}
		});
        
        pm.add(miSave);
        pm.add(miClose);
        pm.addSeparator();
        pm.add(miCopy);
        pm.addSeparator();
        pm.add(miRename);
        pm.add(miDelete);
        pm.addSeparator();
        pm.add(miHistory);

        addMouseListener(new AllMouseEventProcessor() {
            public void process(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int idx = getSelectedIndex();
                    if (idx > -1) {
                        InterfaceFrame frm = getSelectedFrame();
                        if (frm.isReadOnly()) {
                            miSave.setEnabled(false);
                            miCopy.setEnabled(false);
                            miRename.setEnabled(false);
                            miDelete.setEnabled(false);
                        } else {
                            miSave.setEnabled(true);
                            miCopy.setEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.INTERFACE_CREATE_RIGHT));
                            miRename.setEnabled(true);
                            miDelete.setEnabled(Kernel.instance().getUser().hasRight(Or3RightsNode.INTERFACE_DELETE_RIGHT));
                        }
                    }
                    pm.show(TabbedContent.this, e.getX(), e.getY());
                }
            }
        });
        Factories.instance().addPropertyChangeListener(this);
        setOpaque(isOpaque);
    }

    public boolean isExists(Long objId) {
        if (interfaces.containsKey(objId)) {
            setSelectedComponent(interfaces.get(objId).getRootPanel());
            return true;
        } else {
            return false;
        }
    }
    
    public Map<Long, InterfaceFrame> getInterfaces() {
    	return interfaces;
    }

    public void addTab(InterfaceFrame frm) {
        ImageIcon image = (frm.isReadOnly()) ? iconRO : iconNorm;
        interfaces.put(frm.getUiObject().id, frm);
        interfaceObjIds.add(frm.getUiObject().id);
        super.addTab(frm.getTitle(), image, frm.getRootPanel());
        setSelectedIndex(getComponentCount() - 1);
    }

    public OrGuiComponent getSelectedOrGuiComponent() {
        Long objId = interfaceObjIds.get(getSelectedIndex());
        Component c = interfaces.get(objId).getRootPanel();
        return (OrGuiComponent)c;
    }

    public KrnObject getSelectedKrnObject() {
        Long objId = interfaceObjIds.get(getSelectedIndex());
        return interfaces.get(objId).getUiObject();
    }

    public String getSelectedTitle() {
        Long objId = interfaceObjIds.get(getSelectedIndex());
        return interfaces.get(objId).getTitle();
    }

    public InterfaceNode getSelectedInterfaceNode() {
        DesignerFrame frame = (DesignerFrame)getTopLevelAncestor();
        final InterfaceTree tree = frame.getInterfaceTree();
        InterfaceNode root = tree.getRoot();
        return (InterfaceNode)root.find(getSelectedKrnObject()).getLastPathComponent();
    }

    public OrGuiComponent getOrGuiComponent(int idx) {
        Long objId = interfaceObjIds.get(idx);
        Component c = interfaces.get(objId).getRootPanel();
        return (OrGuiComponent)c;
    }

    public int getCounter(int idx) {
        Long objId = interfaceObjIds.get(idx);
        return interfaces.get(objId).getInterfaceCounter();
    }

    public KrnObject getKrnObject(int idx) {
        Long objId = interfaceObjIds.get(idx);
        return interfaces.get(objId).getUiObject();
    }

    protected void processContainerEvent(ContainerEvent e) {
        if (e.getID() == ContainerEvent.COMPONENT_ADDED) {
            miSave.setEnabled(true);
            miClose.setEnabled(true);
            miCopy.setEnabled(true);
            miRename.setEnabled(true);
            miDelete.setEnabled(true);
        } else if (e.getID() == ContainerEvent.COMPONENT_REMOVED) {
            if (getComponentCount() == 0) {
                miSave.setEnabled(false);
                miClose.setEnabled(false);
                miCopy.setEnabled(false);
                miRename.setEnabled(false);
                miDelete.setEnabled(false);
            }
        }
        super.processContainerEvent(e);
    }

    private void saveCurrent() {
        try {
            InterfaceFrame frame = getSelectedFrame();
            frame.save(null);
            //String oldKey = getTitleAt(getSelectedIndex());
            //InterfaceFrame p = (InterfaceFrame)interfaces.get(oldKey);
            if (frame.isModified() && !frame.isReadOnly()) {
                frame.setModified(false);
                setIconAt(getSelectedIndex(), iconNorm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeCurrent() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = interfaceObjIds.get(idx);
    	ArrayList<Long> loadingInterfacesID = (ArrayList<Long>) DesignerFrame.getLoadingInterfaceID();
    	int deletedInterfaceID = 0; 
    	for (int i = 0; i < loadingInterfacesID.size(); i++) {
    		if (loadingInterfacesID.get(i) == objId)
    			deletedInterfaceID = i;
    	}
        InterfaceFrame curInterface = interfaces.get(objId);
        if (isTabModifaied(getSelectedIndex())) {
            String mess = "Интерфейс '" + getTitleAt(getSelectedIndex()) +
                    "' был модифицирован.\nСохранить изменения?";
            int t = MessagesFactory.showMessageDialog(
                    (Frame)this.getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, mess);
            if (t == ButtonsFactory.BUTTON_YES) {
                saveCurrent();
                interfaces.remove(objId);
                loadingInterfacesID.remove(deletedInterfaceID);
                interfaceObjIds.remove(objId);
                remove(idx);
                Kernel.instance().releaseEngagedObject(objId);
            } else if (t == ButtonsFactory.BUTTON_CANCEL) {
                return;
            } else if (t == ButtonsFactory.BUTTON_NO){
                interfaces.remove(objId);
                loadingInterfacesID.remove(deletedInterfaceID);
                interfaceObjIds.remove(objId);
                remove(idx);
                Kernel.instance().releaseEngagedObject(objId);
            }
        } else {
            InterfaceFrame frm = interfaces.remove(objId);
            loadingInterfacesID.remove(deletedInterfaceID);
            interfaceObjIds.remove(objId);
            remove(idx);
            if (!frm.isReadOnly())
                Kernel.instance().releaseEngagedObject(objId);
        }
        fireStateChanged();
    }

    private void copyInterface() {
        String oldName = getTitleAt(getSelectedIndex());
        CreateElementPanel cp =
                new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                        getTitleAt(getSelectedIndex()));
        DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                "Создание копии интерфейса", cp);
        dlg.pack();
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            String ifcName = cp.getElementName();
            if (ifcName == null) {
                JOptionPane.showMessageDialog(this, "Неверное имя интерфейса!",
                        "Сообщение", JOptionPane.ERROR_MESSAGE);
            } else {
                Kernel krn = Kernel.instance();
                krn.setAutoCommit(false);
                try {
                    KrnObject ui = krn.createObject(Kernel.SC_UI, 0);
                    DesignerFrame.getLoadingInterfaceID().add(ui.id);
                    KrnObject lang = krn.getInterfaceLanguage();
                    long langId = (lang != null) ? lang.id : 0;
                    InterfaceFrame sourceFrame = getSelectedFrame();
                    krn.setString(ui.id, ui.classId, "title", 0, langId, ifcName, 0);
                    krn.writeLogRecord(SystemEvent.EVENT_COPY_INTERFACE, "'" + oldName + "' в '" + ifcName + "'");
                    InterfaceFrame frame = sourceFrame.makeCopy(ui, null);
                    frame.load(DesignerFrame.instance().getProgressBar());
                    addTab(frame);
                    addCopyToTree(sourceFrame.getUiObject(), ui, ifcName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                krn.setAutoCommit(true);
            }
        }
    }

    public boolean isTabModifaied(int idx) {
        Long objId = interfaceObjIds.get(idx);
        InterfaceFrame p = interfaces.get(objId);
        return p.isModified();
    }

    public void setModified(boolean isModified, int idx) {
        Long objId = interfaceObjIds.get(idx);
        InterfaceFrame p = interfaces.get(objId);
        p.setModified(isModified);
        if (!p.isReadOnly()) {
            if (!isModified) {
                setIconAt(idx, iconNorm);
            } else {
                setIconAt(idx , iconMod);
            }
        }
    }

    public void propertyModified(OrGuiComponent c) {
        if (getSelectedIndex() > -1) {
            InterfaceFrame p = getSelectedFrame();
            if (!p.isModified() && !p.isReadOnly()) {
                p.setModified(true);
                setIconAt(getSelectedIndex(), iconMod);
            }
        }
    }
    
    public void propertyModified(OrGuiComponent c, PropertyNode property) {}

    public int getComponentCount() {
        return interfaces.size();
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {}

    private void renameInterface() {
        CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, getTitleAt(getSelectedIndex()));
        DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(), "Переименование интерфейса", cp);
        dlg.show();
        int res = dlg.getResult();
        if (res == ButtonsFactory.BUTTON_OK) {
            Kernel krn = Kernel.instance();
            try {
                //String oldKey = getTitleAt(getSelectedIndex());
                KrnObject lang = krn.getInterfaceLanguage();
                KrnClass cls = krn.getClassByName("UI");
                String oldName = getSelectedTitle();
                KrnObject obj = getKrnObject(getSelectedIndex());
                KrnAttribute attr = krn.getAttributeByName(cls, "title");
                krn.setString(obj.id, attr.id, 0, lang.id,
                        cp.getElementName(), 0);
                krn.writeLogRecord(SystemEvent.EVENT_RENAME_INTERFACE, "'" + oldName + "' в '" + cp.getElementName() + "'");
                setTitleAt(getSelectedIndex(),cp.getElementName());
                DesignerFrame frame =
                        (DesignerFrame)getParent().getParent().getParent();
                final InterfaceTree tree = frame.getInterfaceTree();
                InterfaceNode root = tree.getRoot();
                InterfaceNode source = (InterfaceNode)root.find(getSelectedKrnObject()).getLastPathComponent();
                source.rename(cp.getElementName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteInterface() {
        String mess = "Удалить интерфейс '" + getTitleAt(getSelectedIndex()) + "'?";
        int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            try {
                final DesignerFrame frame = (DesignerFrame)getParent().getParent().getParent();
                final InterfaceTree tree = frame.getInterfaceTree();
                ServiceTreeModel model = (ServiceTreeModel) tree.getModel();
                InterfaceNode root = tree.getRoot();
                InterfaceNode sourceNode = (InterfaceNode)root.find(getSelectedKrnObject()).getLastPathComponent();
                model.deleteNode(sourceNode, false);
                removeCurrent();
                InterfaceActionsConteiner.removeFromConteiner(sourceNode.getKrnObj().id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addCopyToTree(KrnObject source, KrnObject target, String title) {
        DesignerFrame frame = DesignerFrame.instance();
        final InterfaceTree tree = frame.getInterfaceTree();
        InterfaceTreeModel model = (InterfaceTreeModel) tree.getModel();
        InterfaceNode root = tree.getRoot();
        InterfaceNode sourceNode = (InterfaceNode)root.find(source).getLastPathComponent();
        InterfaceNode parentNode = (InterfaceNode)sourceNode.getParent();
        try {
            model.addNode(new InterfaceNode(
                    target, title, tree.getLangId()), parentNode, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("counter".equals(evt.getPropertyName())) {
            if (getSelectedIndex() > -1) {
                InterfaceFrame ir = getSelectedFrame();
                if (ir != null)
                    ir.setComponentCounter((Integer)evt.getNewValue());
            }
        }
    }

    public InterfaceFrame getSelectedFrame() {
        int i = getSelectedIndex();
        if (i == -1) {
            return null;
        } else {
            return interfaces.get(interfaceObjIds.get(i));
        }
    }

    public OrFrame getFrameAt(int idx) {
        return interfaces.get(interfaceObjIds.get(idx));
    }

    public KrnObject getInterfaceLang() {
        return com.cifs.or2.client.Utils.getInterfaceLang();
    }

    public void setInterfaceLang(KrnObject lang) {}

    public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
        setInterfaceLang(lang);
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    public String getNextUid() {
        return null;
    }

    public String getString(String uid) {
        return null;
    }

    public String getString(String uid, String defStr) {
        return null;
    }

    public byte[] getBytes(String s) {
        return null;
    }

    public void setString(String uid, String str) {}

    public void setBytes(String s, byte[] bytes) {}

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public Component setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
        if (isFullScreen) {
            Component comp = getSelectedFrame().getRootPanel();
            removeTabAt(getSelectedIndex());
            return comp;
        } else {
            return null;
        }
    }

	public void addRefGroup(int group, CheckContext context) {}

	public Cache getCash() {
		return null;
	}

	public long getFlowId() {
		return 0;
	}
    public long getTransactionId() {
        return 0;
    }

	public Map<String, OrRef> getContentRef() {
		return null;
	}

	public List getRefGroups(int group) {
		return null;
	}

	public Map<String, OrRef> getRefs() {
		return null;
	}

	public OrGuiComponent getPanel() {
		return null;
	}

	public int getTransactionIsolation() {
		return 0;
	}

	public ReportPrinter getReportPrinter(long id) {
		return null;
	}

	public void addReport(ReportPrinter report) {}

	public KrnObject getDataLang() {
		return null;
	}

	public int getEvaluationMode() {
		return 0;
	}

    public void setRootReport(ReportRecord reportRecord) {}

	public InterfaceManager getInterfaceManager() {
		return null;
	}
	
	public void setAllwaysFocused(OrGuiComponent comp) {}
}
