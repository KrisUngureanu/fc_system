package kz.tamur.guidesigner.service;

import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF_FOLDER;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.FindPattern;
import kz.tamur.guidesigner.IDPattern;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.StringPattern;
import kz.tamur.guidesigner.UIDPattern;
import kz.tamur.guidesigner.service.cmd.CmdRenameProcess;
import kz.tamur.guidesigner.service.cmd.CmdSaveProcess;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.AbstractDesignerTreeCellRenderer;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DesignerTreeModel;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.LangItem;
import kz.tamur.util.ServiceFolderPropertyPanel;
import kz.tamur.util.ServiceNodeIndexPropPanel;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tigris.gef.base.Cmd;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class ServicesTree extends DesignerTree implements ActionListener {

    private Cmd renameCmd;


    private String searchString = "";
    private int typeComboIndex = 0, conditionComboIndex = 0;
    FindPattern pattern;
    
    private MainFrame frm;
    private CmdSaveProcess saveCmd;
    private long langId;

    private JMenuItem propItem = createMenuItem(
            "Своиства папки...", "FolderProp");

    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;

    public ServicesTree(ServiceNode root, MainFrame frm, boolean isRuntimeDesign) {
        super(root);
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.PROCESS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.PROCESS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.PROCESS_CREATE_RIGHT);

        this.root = root;
        this.frm = frm;
        this.langId=Utils.getInterfaceLangId();
        model = new ServiceTreeModel(root, isRuntimeDesign);
        setModel(model);
        setCellRenderer(new CellRenderer());
        if (isOpaque) {
            setBackground(kz.tamur.rt.Utils.getLightSysColor());
        }
        if (frm != null) {
            renameCmd = new CmdRenameProcess("Rename", frm, false);
            saveCmd = new CmdSaveProcess("Save", frm, CmdSaveProcess.SAVE_CURRENT);
        }
        //propItem.setEnabled(false);
        propItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServiceFolderPropertyPanel sp = new ServiceFolderPropertyPanel(false);
                ServiceNode node = (ServiceNode)getSelectedNode();
                if (!node.isLeaf()) {
                    sp.setTabName(node.getTabName());
                    sp.setTab(node.isTab());
                    sp.setIndex(node.getRuntimeIndex());
                    DesignerDialog dlg = null;
                    if (getTopLevelAncestor() instanceof Dialog) {
                        dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                                "Свойства папки [" + getSelectedNode().toString() + "]",
                                sp);
                    } else {
                        dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                                "Свойства папки [" + getSelectedNode().toString() + "]",
                                sp);
                    }
                    dlg.show();
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                        setFolderProperties(node.getKrnObj(),
                                sp.isTab(), sp.getInputName(), sp.getIndex());
                    }
                } else {
                    ServiceNodeIndexPropPanel p = new ServiceNodeIndexPropPanel(
                            String.valueOf(node.getRuntimeIndex()));
                    DesignerDialog dlg = null;
                    if (getTopLevelAncestor() instanceof Dialog) {
                        dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                                "Индекс процесса [" + getSelectedNode().toString() + "]",
                                p);
                    } else {
                        dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                                "Индекс процесса [" + getSelectedNode().toString() + "]",
                                p);
                    }
                    dlg.show();
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                        setRuntimeIndex(node.getKrnObj(), p.getIndex());
                    }
                }
            }
        });
        pm.add(propItem);
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path != null) {
                    ServiceNode node = (ServiceNode)path.getLastPathComponent();
                    if (!node.isLeaf()) {
                        propItem.setText("Свойства папки");
                        propItem.setIcon(kz.tamur.rt.Utils.getImageIcon("FolderProp"));
                    } else {
                        propItem.setText("Индекс процесса");
                        propItem.setIcon(kz.tamur.rt.Utils.getImageIcon("Hand"));
                    }
                }
            }
        });
        miSendToRecycle.addActionListener(this);
    }

    public void setFrame(MainFrame frm) {
        if (this.frm == null) {
            this.frm = frm;
            renameCmd = new CmdRenameProcess("Rename", frm, false);
            saveCmd = new CmdSaveProcess("Save", frm, CmdSaveProcess.SAVE_CURRENT);
        }
    }

    protected void defaultDeleteOperations() {
        AbstractDesignerTreeNode node = getSelectedNode();
        frm.removeCurrentTab(node.getKrnObj().id);
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel(1);
        sip.setSearchText(searchString);
        sip.setTypeIndex(typeComboIndex);
        sip.setConditionIndex(conditionComboIndex);
        DesignerDialog dlg = null;
        final Container cnt = getTopLevelAncestor();
        if (cnt instanceof Dialog) {
            dlg = new DesignerDialog((JDialog) cnt, "Поиск службы", sip);
        } else {
            dlg = new DesignerDialog((Frame) cnt, "Поиск службы", sip);
        }
        dlg.show();
        if (dlg.getResult() != ButtonsFactory.BUTTON_NOACTION && dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            searchString = sip.getSearchText();
            typeComboIndex = sip.getType();
            conditionComboIndex = sip.getCondition();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = null;
                    if(sip.getType() == 0){
                		pattern = new StringPattern(searchString, sip.getSearchMethod());
                	} else if (sip.getType() == 1){
                		pattern = new IDPattern(Long.parseLong(searchString));
                	} else if (sip.getType() == 2){
                		pattern = new UIDPattern(searchString);
                	}
                    fnode = finder.findFirst(node, pattern);
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(cnt);
                    }
                }
            });
            t.start();
        }
    }

    public void createServiceFolder(String title, boolean isTab, String tabName) {
        try {
            ((ServiceTreeModel)model).createFolderNode(title, isTab, tabName);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public class ServiceTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private boolean isRuntimeDesign;

        public ServiceTreeModel(TreeNode root, boolean isRuntimeDesign) {
            super(root);
            this.isRuntimeDesign = isRuntimeDesign;
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            return null;
        }

        public AbstractDesignerTreeNode createFolderNode(String title, boolean isTab, String tabName) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("ProcessDefFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            final long langId = Utils.getInterfaceLangId();
            ServiceNode selNode = (ServiceNode)getSelectedNode();
            KrnObject srvObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(srvObj.id, srvObj.classId, "children", idx, obj.id, 0, false);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
            setFolderProperties(obj, isTab, tabName, 0);
            ServiceNode node = new ServiceNode(obj, title, langId, idx, title, title, 0, false, null, null, null, false, null);
            node.setTab(isTab);
            node.setTabName(tabName);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title, boolean createFilterFolder,KrnObject obj) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("ProcessDef");
            if(obj==null)
               obj = krn.createObject(cls, 0);
            else
                obj = krn.createObject(cls, obj.uid, 0);
            final long langId = Utils.getInterfaceLangId();
            ServiceNode selNode = (ServiceNode)getSelectedNode();
            if(selNode==null) {
                selNode = (ServiceNode)root;
            }
            KrnObject  srvObj = selNode.getKrnObj();
            int idx = selNode.getChildCount();
            krn.setObject(srvObj.id, srvObj.classId, "children", idx, obj.id, 0, false);
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
            ServiceNode node = new ServiceNode(obj, title, langId, idx, title, title, 0, false, null, null, null, false, null);
            insertNodeInto(node, selNode, selNode.getChildCount());
            if(createFilterFolder) {
            kz.tamur.rt.Utils.createFilterFolder(obj,title,langId);}
            krn.writeLogRecord(SystemEvent.EVENT_CREATE_PROCESS, title);
            selNode = null;
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(KrnObject obj, KrnObject parentObj) throws KrnException {
            Kernel kernel = Kernel.instance();
            long langId = Utils.getInterfaceLangId();
   		 	ServiceNode parentNode = (ServiceNode) find(parentObj);
            int idx = parentNode.getChildCount();
            KrnClass cls = kernel.getClassByName("ProcessDef");
            KrnAttribute titleAttr = kernel.getAttributeByName(cls, "title");
            String title = kernel.getStringsSingular(obj.id, titleAttr.id, 0, false, true);
            kernel.setObject(parentObj.id, parentObj.classId, "children", idx, obj.id, 0, false);
            ServiceNode node = new ServiceNode(obj, title, langId, idx, title, title, 0, false, null, null, null, false, null);
            insertNodeInto(node, parentNode, idx);
            kernel.writeLogRecord(SystemEvent.EVENT_RESTORE_PROCESS_FROM_RECYCLE, title);
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            return createChildNode(title,true,null);
        }
        
        public AbstractDesignerTreeNode createChildNode(String title, boolean createFilterFolder) throws KrnException {
            return createChildNode(title,true,null);
        }
        
        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            ServiceNode parent = (ServiceNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values = Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
                krn.writeLogRecord(SystemEvent.EVENT_DELETE_PROCESS, node.toString());
            }
        }

        public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            KrnObject parentObj = parent.getKrnObj();
            if (!isMove) {
                node = new ServiceNode(node.getKrnObj(), node.toString(), langId,
                        parent.getChildCount(), node.toString(), node.toString(), ((ServiceNode)node).getRuntimeIndex(), ((ServiceNode)node).isTab(),
                        ((ServiceNode)node).getTabName(), ((ServiceNode)node).getTabNameKz(), ((ServiceNode)node).getHotKey(),
                        ((ServiceNode)node).isBtnToolBar(), ((ServiceNode)node).getIcon());
            }
            krn.setObject(parentObj.id, parentObj.classId, "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
            super.fireTreeStructureChanged(source, path, childIndices, children);
        }

        public void renameNode() {}

        public void rename(ServiceNode node) {
            if (node != null) {
                TreeNode[] tpath = getPathToRoot(node);
                fireTreeNodesChanged(this, tpath, null, null);
            }
        }

        public int getChildCount(Object parent) {
            ServiceNode snode= (ServiceNode)parent;
            if (isRuntimeDesign) {
                Enumeration children = snode.children();
                int cnt = 0;
                while (children.hasMoreElements()) {
                    ServiceNode child = (ServiceNode) children.nextElement();
                    if (!child.isTab()) {
                        cnt++;
                    }
                }
                return cnt;
            } else {
                return snode.getChildCount();
            }
        }

        public Object getChild(Object parent, int index) {
            ServiceNode snode= (ServiceNode)parent;
            if (isRuntimeDesign) {
                Enumeration children = snode.children();
                int pos = 0;
                while (children.hasMoreElements()) {
                    ServiceNode child = (ServiceNode) children.nextElement();
                    if (!child.isTab()) {
                        if (pos++ == index) {
                            return child;
                        }
                    }
                }
                return null;
            } else {
                return snode.getChildAt(index);
            }
        }
    }

    private void setRuntimeIndex(KrnObject obj, int index) {
        final Kernel krn = Kernel.instance();
        try {
            krn.setLong(obj.id, obj.classId, "runtimeIndex", 0, index, 0);
            ServiceNode node = (ServiceNode)getSelectedNode();
            node.setRuntimeIndex(index);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    private void setFolderProperties(KrnObject obj, boolean isTab, String tabTitle, int index) {
        final Kernel krn = Kernel.instance();
        final long langId = Utils.getInterfaceLangId();
        try {
            krn.setLong(obj.id, obj.classId, "runtimeIndex", 0, index, 0);
            krn.setLong(obj.id, obj.classId, "isTab", 0, (isTab) ? 1 : 0, 0);
            if (isTab) {
                krn.setString(obj.id, obj.classId, "tabName", 0, langId, tabTitle, 0);
            }
            ServiceNode node = (ServiceNode)getSelectedNode();
            node.setRuntimeIndex(index);
            node.setTab(isTab);
            node.setTabName(tabTitle);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                l.setIcon(kz.tamur.rt.Utils.getImageIcon("ServiceTab"));
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }

    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Kernel krn = Kernel.instance();
        if (src == miCut || src == miRename || src == miDelete) {
    		try {
				if (krn.getBindingModuleToUserMode()) {
					ServiceNode selectedNode = (ServiceNode) getSelectedNode();
					KrnObject obj = selectedNode.getKrnObj();
					KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
					if (developerObjs.length > 0) {
						long ownerId = developerObjs[0].id;
						long currentUserId = krn.getUserSession().userObj.id;
						if (ownerId != currentUserId) {
							KrnObject userObj = krn.getObjectById(ownerId, 0);
							if (userObj != null) {	// Владелец процесса существует
				    			KrnClass userCls = krn.getClassByName("User");
				    			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
				    			String userName = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
				    			StringBuilder message = new StringBuilder("Невозможно ");
				    			if (src == miCut) {
				    				message.append("вырезать");
				    			} else if (src == miRename) {
				    				message.append("переименовать");
				    			} else if (src == miDelete) {
				    				message.append("удалить");
				    			}
				    			if (selectedNode.isLeaf()) {
				    				message.append(" выбранный процесс! ");
				    			} else {
				    				message.append(" выбранную папку! ");
				    			}
				    			message.append("Владельцем объекта является пользователь " + userName + ".");
				                MessagesFactory.showMessageDialog((JFrame)frm.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, message.toString());
				            	return;
							}
						}
					}
				}
			} catch (KrnException ex) {
				ex.printStackTrace();
			}
    	}
        Cmd command = null;
        if (src == miRename) {
            try {
                UserSessionValue us = krn.getObjectBlocker(getSelectedNode().getKrnObj().id);
                if (us != null) {
                    MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Процесс '" + getSelectedNode().toString() + "' заблокирован!\n" + "Пользователь: " + us.name + "\n" + "IP адрес: " + us.ip + "\n" + "Имя компьютера: " + us.pcName);
                    return;
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            command = renameCmd;
        } else if (src == miSendToRecycle) {
        	try {
				sendToRecycle();
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
        } else {
            super.actionPerformed(e);
        }
        if (command != null) {
            command.doIt();
        }
    }
    
    private void sendToRecycle() throws KrnException {
    	AbstractDesignerTreeNode selectedNode = getSelectedNode();
    	int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Перемещение процесса '" + selectedNode.toString() + "' в корзину!\nПродолжить?");
        if (res == ButtonsFactory.BUTTON_YES) {
       	 	// Считать все свойства объекта ProcessDef и перезаписать их на объект класса ProcessDefRecycle
        	 Kernel kernel = Kernel.instance();
        	 KrnObject obj = selectedNode.getKrnObj();
        	 KrnClass cls = kernel.getClassByName("ProcessDefRecycle");
        	 KrnObject objRecycle = kernel.createObject(cls, 0);

        	 byte[] config = kernel.getBlob(obj, "config", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "config", 0, config, 0, 0);
        	 
        	 byte[] diagram = kernel.getBlob(obj, "diagram", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "diagram", 0, diagram, 0, 0);
        	 
        	 KrnObject[] filters = kernel.getObjects(obj, "filters", 0);
        	 if (filters != null && filters.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "filters", 0, filters[0].id, 0, true);
        	 }
        	 
        	 String[] hotkey = kernel.getStrings(obj, "hotKey", 0, 0);
        	 if (hotkey != null && hotkey.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "hotKey", 0, 0, hotkey[0], 0);
        	 }
        	 
        	 byte[] icon = kernel.getBlob(obj, "icon", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "icon", 0, icon, 0, 0);
        	 
        	 long[] isBtnToolBar = kernel.getLongs(obj, "isBtnToolBar", 0);
        	 if (isBtnToolBar != null && isBtnToolBar.length > 0) {
        		 kernel.setLong(objRecycle.id, cls.id, "isBtnToolBar", 0, isBtnToolBar[0], 0);
        	 }
        	 
        	 byte[] message = kernel.getBlob(obj, "message", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "message", 0, message, 0, 0);
        	 
        	 KrnObject[] parent = kernel.getObjects(obj, "parent", 0);
        	 if (parent != null && parent.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "parent", 0, parent[0].id, 0, true);
        	 }

        	 long[] runtimeIndex = kernel.getLongs(obj, "runtimeIndex", 0);
        	 if (runtimeIndex != null && runtimeIndex.length > 0) {
        		 kernel.setLong(objRecycle.id, cls.id, "runtimeIndex", 0, runtimeIndex[0], 0);
        	 }
        	 
        	 byte[] strings = kernel.getBlob(obj, "strings", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "strings", 0, strings, 0, 0);
        	 
        	 byte[] test = kernel.getBlob(obj, "test", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "test", 0, test, 0, 0);
        	 
        	 String[] title = kernel.getStrings(obj, "title", 0, 0);
        	 if (title != null && title.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "title", 0, 0, title[0], 0);
        	 }
        	 
        	 KrnObject[] balance = kernel.getObjects(obj, "баланс_ед", 0);
        	 if (balance != null && balance.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "баланс_ед", 0, balance[0].id, 0, true);
        	 }
        	 
        	 String eventInitiator = kernel.getUser().getName();
    		 kernel.setString(objRecycle.id, cls.id, "eventInitiator", 0, 0, eventInitiator, 0);
        	 
    		 kernel.setString(objRecycle.id, cls.id, "uid", 0, 0, obj.uid, 0);
    		 
    		 SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm");
    		 java.util.Date currentDate = new java.util.Date();
    		 String eventDate = format.format(currentDate);
    		 kernel.setString(objRecycle.id, cls.id, "eventDate", 0, 0, eventDate, 0);

         	
             // Выделение родительского узла
             ServiceNode parentNode = (ServiceNode) selectedNode.getParent();
             setSelectedNode(parentNode);
    		 
     		// Удаление узла из дерева
             ((ServiceTreeModel) getModel()).removeNodeFromParent(selectedNode);
             
        	 KrnObject parentObj = parentNode.getKrnObj();
        	 Collection<Object> values = Collections.singletonList((Object) obj);
        	 kernel.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);	// Удаление из списка детей
        	 kernel.deleteObject(obj, 0);
        	 kernel.writeLogRecord(SystemEvent.EVENT_SEND_PROCESS_TO_RECYCLE, selectedNode.toString());
        }
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.COPY_TYPE, copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(), "Вставка копии процесса", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String procName = cp.getElementName();
                if (procName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя процесса!", "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        Kernel krn = Kernel.instance();
						boolean isAllowed=true;
						KrnObject obj=copyNode.getKrnObj();
						String title=copyNode.getTitle();
						if (krn.getBindingModuleToUserMode()) {
							KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
							if (developerObjs.length > 0) {
								long ownerId = developerObjs[0].id;
								long currentUserId = krn.getUserSession().userObj.id;
								if (ownerId != currentUserId) {
									KrnObject userObj = krn.getObjectById(ownerId, 0);
									if (userObj != null) {	// Владелец процесса существует
						    			KrnClass userCls = krn.getClassByName("User");
						    			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
						    			String userName = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
						    			StringBuilder message = new StringBuilder("Невозможно ");
						    			message.append("Владельцем объекта является пользователь " + userName + ".");
						                MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, message.toString());
										isAllowed=false;
									}
								}
							}
						}
						
						if(isAllowed) {
							try {
								UserSessionValue us = krn.vcsLockObject(obj.id);
								if (us != null) {
									MessagesFactory.showMessageDialog(frm.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Процесс '" + title + "' редактируется!\nПользователь: " + us.name );
									isAllowed=false;
								}else {
									us = krn.blockObject(obj.id);
									if (us != null) {
										MessagesFactory.showMessageDialog(frm.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Процесс '" + title + "' заблокирован!\nПользователь: " + us.name + "\nIP адрес: " + us.ip + "\nИмя компьютера: " + us.pcName );
										isAllowed=false;
									}
								}
							} catch (KrnException e) {
								e.printStackTrace();
							}
						}
						if(isAllowed) {
	                        KrnObject lang = krn.getInterfaceLanguage();
	                        long langId = (lang != null) ? lang.id : 0;
	                        String oldName = copyNode.toString();
	                        if (!copyNode.isCutProcess()) {
	                            byte[] data = krn.getBlob(copyNode.getKrnObj(), "diagram", 0, 0, 0);
	                            byte[] msg_rus = krn.getBlob(copyNode.getKrnObj(), "message", 0, frm.getRusLang(), 0);
	                            byte[] msg_kaz = krn.getBlob(copyNode.getKrnObj(), "message", 0, frm.getKazLang(), 0);
	                            KrnObject service = krn.createObject(Kernel.SC_PROCESS_DEF, 0);
	                            krn.setString(service.id, service.classId, "title", 0, langId, procName, 0);
	                    		long currentUserId = krn.getUserSession().userObj.id;
	                            krn.setLong(service.id, service.classId, "developer", 0, currentUserId, 0);
	                            krn.writeLogRecord(SystemEvent.EVENT_COPY_PROCESS, "'" + oldName + "' в '" + procName + "'");
	                            model.addNode(new ServiceNode(service, procName, langId,
	                                    parent.getChildCount(), procName, procName, ((ServiceNode)copyNode).getRuntimeIndex(), ((ServiceNode)copyNode).isTab(),
	                                    ((ServiceNode)copyNode).getTabName(), ((ServiceNode)copyNode).getTabNameKz(), ((ServiceNode)copyNode).getHotKey(),
	                                    ((ServiceNode)copyNode).isBtnToolBar(), ((ServiceNode)copyNode).getIcon()), parent, false);
	                            ServiceModel model = new ServiceModel(true,service,langId);
	                            model.setMf(frm);
	                            Document doc = new Document(service, procName, model);
	                            if (data.length > 0) {
	                                InputStream is_msg_rus = msg_rus.length>0?new ByteArrayInputStream(msg_rus):null;
	                                model.loadLangs(is_msg_rus,frm.getRusLang());
	                                InputStream is_msg_kaz = msg_kaz.length>0?new ByteArrayInputStream(msg_kaz):null;
	                                model.loadLangs(is_msg_kaz,frm.getKazLang());
	                                InputStream is = new ByteArrayInputStream(data);
	                                model.load(is,doc.getGraph());
	                                is.close();
	                                if(is_msg_rus!=null)
	                                    is_msg_rus.close();
	                                if(is_msg_kaz!=null)
	                                    is_msg_kaz.close();
	                            }
	                            saveCmd.save(doc);
	                        } else {
	                            krn.setString(copyNode.getKrnObj().id, copyNode.getKrnObj().classId, "title", 0, langId, procName, 0);
	                            model.deleteNode(copyNode, true);
	                            model.addNode(new ServiceNode(copyNode.getKrnObj(),
	                                    procName,langId, parent.getChildCount(), procName, procName, ((ServiceNode)copyNode).getRuntimeIndex(),
	                                    ((ServiceNode)copyNode).isTab(), ((ServiceNode)copyNode).getTabName(), ((ServiceNode)copyNode).getTabNameKz(),
	                                    ((ServiceNode)copyNode).getHotKey(), ((ServiceNode)copyNode).isBtnToolBar(), ((ServiceNode)copyNode).getIcon()), parent, false);
	                            defaultDeleteOperations();
	                            krn.writeLogRecord(SystemEvent.EVENT_MOVE_PROCESS, "'" + oldName + "' в '" + procName + "' в папку '" + parent.toString() + "'");
	                        }
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
        Kernel krn = Kernel.instance();
        ServiceNode node=(ServiceNode)getSelectedNode();
        try {
            long[] ids = {root.getKrnObj().id};
	        long ruId = krn.getLangIdByCode("RU");
	        long kzId = krn.getLangIdByCode("KZ");
	        
	        AttrRequestBuilder arb = new AttrRequestBuilder(SC_PROCESS_DEF_FOLDER, krn)
	        		.add("title", ruId).add("title", kzId).add("runtimeIndex")
	        		.add("isTab").add("tabName", ruId).add("tabName", kzId);
	
	        String title = null;
	        String titleKz = null;
	        long runtimeIndex = 0;
	        boolean isTab = false;
	        String tabRu = null;
	        String tabKz = null;
	        
	        List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
	        if (rows.size() > 0) {
	            Object[] row = rows.get(0);
	        	
	            title = (row[2] != null) ? (String)row[2] : "Не определён";
	            titleKz = (row[3] != null) ? (String)row[3] : "";
	            runtimeIndex = (row[4] != null) ? (Long)row[4] : 0;
	            isTab = (row[5] != null) ? (Boolean)row[5] : false;
	
	            tabRu = (row[6] != null) ? (String)row[6] : "";
	            tabKz = (row[7] != null) ? (String)row[7] : "";
	        	
	        }
	        
            root = new ServiceNode(root.getKrnObj(), langId == ruId ? title : titleKz, langId, 0, title, titleKz, runtimeIndex, isTab, tabRu, tabKz, "", false, null);
            model = new ServiceTreeModel(root,false);
            setModel(model);
            if(node!=null) setSelectedNode(node.getKrnObj());
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();
    }

    public void renameProcess(ServiceNode node) {
        ((ServiceTreeModel)model).rename(node);
    }
    public String getTabName(){
        return ((ServiceNode)root).getTabName();
    }

    @Override
    protected void showPopup(MouseEvent e) {
        if (getSelectedNode() != null) {
            if (getSelectedNode().isLeaf()) {
            	miView.setEnabled(false);
            	miEdit.setEnabled(true);
            	miSendToRecycle.setVisible(true);
                miCreateFolder.setEnabled(false);
                miCreateElement.setEnabled(false);
                miCopy.setEnabled(canCreate);
                miCut.setEnabled(canCreate);
                miPaste.setEnabled(false);
            } else {
            	miView.setEnabled(false);
            	miEdit.setEnabled(false);
            	miSendToRecycle.setVisible(false);
                miCreateFolder.setEnabled(canCreate);
                miCreateElement.setEnabled(canCreate);
                miCopy.setEnabled(false);
                miCut.setEnabled(false);
                if (copyNode != null) {
                    miPaste.setEnabled(canCreate);
                } else {
                    miPaste.setEnabled(false);
                }
            }
            miDelete.setEnabled(!(getSelectedNode() == root) && canDelete);
            miRename.setEnabled(canEdit);
            pm.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    /**
     * Получить корень дерева.
     *
     * @return the root
     */
    public ServiceNode getRoot() {
        return (ServiceNode) getModel().getRoot();
    }
    
    public DesignerTreeNode[] getSelectedNodes() {
        List<ServiceNode> list = new ArrayList<ServiceNode>();
        TreePath[] paths = getSelectionPaths();
        for (TreePath path : paths) {
            ServiceNode node = (ServiceNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
            else {
                Enumeration ifcFolder = node.children();
                while(ifcFolder.hasMoreElements())
                {
                    ServiceNode leaf =(ServiceNode) ifcFolder.nextElement();
                    list.add(leaf);
                }
        }}
        ServiceNode[] res = new ServiceNode[list.size()];
        list.toArray(res);
        return res;
    }

    public DesignerTreeNode[] getOnlySelectedNodes() {
        List<ServiceNode> list = new ArrayList<ServiceNode>();
        TreePath[] paths = getSelectionPaths();
        for (TreePath path : paths) {
            ServiceNode node = (ServiceNode) path.getLastPathComponent();
            list.add(node);
        }
        ServiceNode[] res = new ServiceNode[list.size()];
        list.toArray(res);
        return res;
    }

    JFileChooser fc;
    SAXBuilder builder;
   public void importToXml() {
                            Element processDef = null;
                            File file[] = null;
                            builder = new SAXBuilder();
                            fc = new JFileChooser();
                            fc.setMultiSelectionEnabled(true);
                            int returnVal = fc.showOpenDialog(this);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                file = fc.getSelectedFiles();
                                String titles ="";
                                
                                for(int i = 0;i<file.length;i++) {
                                    String title ="";
                                    title = file[i].getName();
                                    titles+="-"+title+"\n";
                                    try {
                                             org.jdom.Document document = (org.jdom.Document) builder.build(file[i]);
                                             processDef= document.getRootElement();      
                                    } catch (JDOMException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Kernel krn = Kernel.instance();
                                    KrnClass cls = null;
                                   try {
                                       cls = krn.getClassByName("ProcessDef");
                                   } catch (KrnException e) {
                                       e.printStackTrace();
                                   }
                                    //create new node in servicetree
                                    final ServicesTree.ServiceTreeModel treeModel = (ServicesTree.ServiceTreeModel)getModel();
                                    String servName = title;
                                    ServiceNode node = null;
                                    Element krnclas = processDef.getChild("KrnClass");
                                    String uid = krnclas.getChildText("uid");
                                     String id_value = processDef.getChild("KrnClass").getChildText("id");
                                     long id = Integer.parseInt(id_value);
                                     KrnObject oldObj = new KrnObject(id,uid,cls.id);
                                    try {
                                        node = (ServiceNode)treeModel.createChildNode(servName, false,oldObj);
                                    } catch (KrnException e) {
                                        if(e.getMessage().startsWith("java.sql.SQLException: Duplicate entry"))
                                        {
                                            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                                                    "Фильтр с таким uid уже существует, выбранный вами фильтр не был импортирован");
                                        }
                                        return;
                                    }
                                    ServiceModel model = new ServiceModel(true, node.getKrnObj(), 122);

                                    Document doc = new Document(node.getKrnObj(), servName, model);
                                  
                                   
                                  
                                  ByteArrayOutputStream os = null;
                                  XMLOutputter out = new XMLOutputter();
                                  out.getFormat().setEncoding("UTF-8");

                                    //setBlob - diagram
                                 Element diagram = processDef.getChild("diagram");    
                                 try {
                                     os =  new ByteArrayOutputStream();
                                     out.output(diagram, os);
                                     krn.setBlob(doc.getKrnObject().id, cls.id, "diagram", 0,
                                             os.toByteArray(), 0, 0);
                                    os.close();
                            
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 } catch (KrnException e) {
                                     e.printStackTrace();
                                }  
                              
                                     //setBlob - message(rus and kaz)
                         
                                  
                                      List<Element> messages = processDef.getChildren("message");
                                      for(Element message:messages) {
                                          os =  new ByteArrayOutputStream();
                                      long langCode = Long.parseLong(message.getAttributeValue("lang"));
                                      try {
                                        out.output(message, os);
                                        krn.setBlob(doc.getKrnObject().id, cls.id, "message", 0, os.toByteArray(),langCode, 0);
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (KrnException e) {
                                        e.printStackTrace();
                                    }}
                                      //setBlob - strings(rus and kaz)
                                      
                                      List<Element> strings = processDef.getChildren("strings");
                                      for(Element string:strings) {
                                          os =  new ByteArrayOutputStream();
                                      long langCode = Long.parseLong(string.getAttributeValue("lang"));
                                      try {
                                        out.output(string, os);
                                        krn.setBlob(doc.getKrnObject().id, cls.id, "strings", 0, os.toByteArray(),langCode, 0);
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (KrnException e) {
                                        e.printStackTrace();
                                    }}
                                      
                                      
                                 //setBlob - config
                                 Element config = processDef.getChild("process-definition");
                                        try {
                                            os = new ByteArrayOutputStream();
                                            out.output(config, os);
                                            krn.setBlob(doc.getKrnObject().id, cls.id, "config", 0, os.toByteArray(), 0, 0);
                                            os.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (KrnException e) {
                                            e.printStackTrace();
                                        }

                                }}
                            }
    public void iterateAndExport(List<DesignerTreeNode> serviceNodes,String directory)
    {
        for(DesignerTreeNode serviceNode_obj: serviceNodes)
        {
            ServiceNode serviceNode = (ServiceNode) serviceNode_obj;
            if(serviceNode.isLeaf())
            {exportService(serviceNode,directory);}
            else {
                String newDir = directory+"\\"+serviceNode.getTitle();
                File dir = new File(directory);
                dir.mkdir();
                iterateAndExport(serviceNode.children(true),newDir);
            }
        }
    }

    private void exportService(ServiceNode serviceNode, String directory) {
          File dir = new File(directory);
          dir.mkdir();
          String titles = "";
          String title = serviceNode.getTitle();
          String filename = title.replaceAll("[^a-zA-Zа-яА-Я0-9,.\\s-]", "_");
          String xmlFilePath = "";
            try {
                xmlFilePath = dir.getCanonicalPath()+"\\"+filename+".xml";
            } catch (IOException e2) {
                e2.printStackTrace();
            }
              FileWriter file = null;
              try {
                  file = new FileWriter(xmlFilePath,true);
              } catch (IOException e1) {
                  e1.printStackTrace();
              }
            if (serviceNode == null || !((AbstractDesignerTreeNode)serviceNode).isLeaf())
                    return;

            KrnObject serviceObj = serviceNode.getKrnObj();
      
                //root
                Element processDef = new Element("processDef");
                org.jdom.Document doc = new  org.jdom.Document(processDef);
                
                //diagram
                Element xml = getXml(serviceObj,"diagram");
                xml.detach();
                doc.getRootElement().addContent(xml);
                
                //config
                Element config = getXml(serviceObj, "config");
                config.detach();
                doc.getRootElement().addContent(config);
                
                //KrnClass
                Element filtercls = new Element("KrnClass");
                Element uid = new Element("uid");
                uid.setText(serviceObj.uid);
                filtercls.addContent(uid);
                Element id = new Element("id");
                id.setText(Long.toString(serviceObj.id));
                filtercls.addContent(id);
                doc.getRootElement().addContent(filtercls);
                
                SAXBuilder builder = new SAXBuilder();
                builder.setValidation(false);

                KrnClass cls;
                Kernel krn = Kernel.instance();
                try {
                    cls = krn.getClassByName("ProcessDef");
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
                
                //messages and strings
                List ls = LangItem.getAll();
                long[] langs = new long[ls.size()];
                for (int ii=0; ii< ls.size(); ii++) {
                    langs[ii] = ((LangItem)ls.get(ii)).obj.id;
                }
         
                for (long lang1 : langs) {
                    Map<String,String> str = new HashMap<String,String>();
                
                    byte[] msg = null;
                    byte[] strings = null;
                    try {
                        msg = krn.getBlob(serviceObj, "message", 0, lang1, 0);
                        strings = krn.getBlob(serviceObj, "strings", 0, lang1, 0);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    
                    if (msg.length > 0) {
                        Element msgXml = null;
                        try {
                            msgXml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
                        } catch (JDOMException e) {
                            e.printStackTrace();  
                        } catch (IOException e) {
                            e.printStackTrace(); 
                        }
                        msgXml.detach();
                        msgXml.setAttribute("lang",Long.toString(lang1));
                        processDef.addContent(msgXml);   
                        }
                    
                    if(strings.length>0)
                    {
                    Element stringsXml = null;
                    try {
                        stringsXml = builder.build(new ByteArrayInputStream(strings)).getRootElement();
                    } catch (JDOMException e) {
                        e.printStackTrace();  
                    } catch (IOException e) {
                        e.printStackTrace(); 
                    }
                    stringsXml.detach();
                    stringsXml.setAttribute("lang",Long.toString(lang1));
                    processDef.addContent(stringsXml);  
                }
                
                
                }
                XMLOutputter out = new XMLOutputter();
                out.getFormat().setEncoding("UTF-8");
                try {
                    out.output(doc, file);
                    file.close();
                    titles=titles.concat("-"+title+"\n");
                } catch (IOException e) {
                    e.printStackTrace(); 
                }}
        
        String msg = "";

    public DesignerTreeNode[] getSelectedNodes(boolean foldersIncluded) {
        ArrayList<ServiceNode> list = new ArrayList<ServiceNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ServiceNode node = (ServiceNode)path.getLastPathComponent();
            list.add(node);
            }

        ServiceNode[] res = new ServiceNode[list.size()];
        list.toArray(res);
        return res;
    }
                            
}
