package kz.tamur.guidesigner;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.AbstractDesignerTreeCellRenderer;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DesignerTreeModel;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;
import kz.tamur.util.ServiceControlNode;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceTree extends DesignerTree {

    private String searchString = "";
    private int typeComboIndex = 0, conditionComboIndex = 0;
    FindPattern pattern; 

    private long langId;

    public InterfaceTree(InterfaceNode root, long langId) {
        super(root);
        this.root = root;
        this.langId = langId;
        model = new InterfaceTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        if (isOpaque) {
            setBackground(kz.tamur.rt.Utils.getLightSysColor());
        }
        miSendToRecycle.addActionListener(this);
    }

    @Override
    protected void showPopup(MouseEvent e) {
        if (getSelectedNode() != null) {
            if (getSelectedNode().isLeaf()) {
            	miSendToRecycle.setVisible(true);
            } else {
            	miSendToRecycle.setVisible(false);
            }
        } else {
        	miSendToRecycle.setVisible(false);
        }
        super.showPopup(e);
    }
    
    protected void defaultDeleteOperations() throws KrnException {
        AbstractDesignerTreeNode node = getSelectedNode();
        ControlTabbedContent tc = ControlTabbedContent.instance();
        if (tc.isExistIfr(node.getKrnObj().id)) {
            tc.closeCurrent();
        }
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel(1);
        sip.setSearchText(searchString);
        sip.setTypeIndex(typeComboIndex);
        sip.setConditionIndex(conditionComboIndex);
        DesignerDialog dlg = new DesignerDialog(
                (JDialog)getTopLevelAncestor(), "Поиск интерфейса", sip);
        dlg.show();
        if (dlg.isOK()) {
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
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();
        }
    }


    public class InterfaceTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public InterfaceTreeModel(TreeNode root) {
            super(root);
        }

        public void renameNode() {
            AbstractDesignerTreeNode selNode = getSelectedNode();
            CreateElementPanel cp = new CreateElementPanel(
                    CreateElementPanel.RENAME_TYPE, selNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Переименование интерфейса", cp);
            dlg.show();
            int res = dlg.getResult();
            if (res == ButtonsFactory.BUTTON_OK) {
                Kernel krn = Kernel.instance();
                try {
                    KrnClass cls = krn.getClassByName("UI");
                    KrnObject obj = selNode.getKrnObj();
                    String oldName = selNode.toString();
                    KrnAttribute attr = krn.getAttributeByName(cls, "title");
                    ControlTabbedContent tc =  ControlTabbedContent.instance();
                    String newName = cp.getElementName();
                    if (tc.isExistIfr(obj.id)) {
                        tc.setTitleAt(tc.getSelectedIndex(), newName);
                    }
                    krn.setString(obj.id, attr.id, 0, langId, newName, 0);
                    krn.writeLogRecord(SystemEvent.EVENT_RENAME_INTERFACE, "'" + oldName + "' в '" + newName + "'");
                    InterfaceNode source = (InterfaceNode) InterfaceTree.this.root.find(obj).getLastPathComponent();
                    source.rename(newName);
                    rename(source);
                    
                    // Обновить узлы в дереве управления
                    ServicesControlTree tree = kz.tamur.comps.Utils.getServicesControlTree();
                    List<ServiceControlNode> sns = tree.findAllChild(obj);
                    if (sns!= null) {
                        for(ServiceControlNode sn:sns) {
                            tree.renameServiceControlNode2(sn,newName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void rename(InterfaceNode node) {
            if (node != null) {
                TreeNode[] tpath = getPathToRoot(node);
                fireTreeNodesChanged(this, tpath, null, null);
            }
        }
        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            return createFolderNode(title,null);
        }

        public AbstractDesignerTreeNode createFolderNode(String title, InterfaceNode selNode) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("UIFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            if(selNode == null)
                selNode = (InterfaceNode)getSelectedNode();
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            krn.setObject(uiObj.id, uiObj.classId, "children", 0, obj.id, 0, false);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
            InterfaceNode node = new InterfaceNode(obj, title, langId);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("UI");
            final KrnObject obj = krn.createObject(cls, 0);
            AbstractDesignerTreeNode selNode = (InterfaceNode)root;
            AbstractDesignerTreeNode inode = getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            } else if(inode.isLeaf()) {
                selNode = (InterfaceNode)inode.getParent();
            }
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children", idx, obj.id, 0, false);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
            InterfaceNode node = new InterfaceNode(obj, title, langId);
            insertNodeInto(node, selNode, selNode.getChildCount());
            krn.writeLogRecord(SystemEvent.EVENT_CREATE_INTERFACE, title);
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(String title,KrnObject obj,AbstractDesignerTreeNode selNode) throws KrnException {
            final Kernel krn = Kernel.instance();
            if(selNode==null)
                selNode = (InterfaceNode)root;
            final KrnClass cls = krn.getClassByName("UI");
            if(obj==null)
            {
                obj = krn.createObject(cls, 0);
            }
            else {
                obj = krn.createObject(cls, obj.uid, 0);
            }
            long clsid =cls.id;
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, obj.classId, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children",
                    idx, obj.id, 0, false);
            InterfaceNode node = new InterfaceNode(obj, title, langId);
            insertNodeInto(node, selNode, selNode.getChildCount());
            krn.writeLogRecord(SystemEvent.EVENT_CREATE_INTERFACE, title);
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(KrnObject obj, KrnObject parentObj) throws KrnException {
            Kernel kernel = Kernel.instance();
            InterfaceNode parentNode = (InterfaceNode) find(parentObj);
            int idx = parentNode.getChildCount();
            KrnClass cls = kernel.getClassByName("UI");
            KrnAttribute titleAttr = kernel.getAttributeByName(cls, "title");
            String title = kernel.getStringsSingular(obj.id, titleAttr.id, 0, false, true);
            kernel.setObject(parentObj.id, parentObj.classId, "children", idx, obj.id, 0, false);
            InterfaceNode node = new InterfaceNode(obj, title, langId);
            insertNodeInto(node, parentNode, idx);
            kernel.writeLogRecord(SystemEvent.EVENT_RESTORE_UI_FROM_RECYCLE, title);
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            InterfaceNode parent = (InterfaceNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
                    Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {

                krn.deleteObject(node.getKrnObj(), 0);
                krn.writeLogRecord(SystemEvent.EVENT_DELETE_INTERFACE, node.toString());
            }
        }

        public void addNode(AbstractDesignerTreeNode node,
                AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new InterfaceNode(node.getKrnObj(), node.toString(),
                        node.getLangId());
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId,
                    "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            l.setIcon(kz.tamur.rt.Utils.getImageIcon(leaf ? "FormTab" : expanded ? "Open" : "CloseFolder"));
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
					InterfaceNode selectedNode = (InterfaceNode) getSelectedNode();
					KrnObject obj = selectedNode.getKrnObj();
					KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
					if (developerObjs.length > 0) {
						long ownerId = developerObjs[0].id;
						long currentUserId = krn.getUserSession().userObj.id;
						if (ownerId != currentUserId) {
							KrnObject userObj = krn.getObjectById(ownerId, 0);
							if (userObj != null) {	// Владелец интерфейса существует
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
				    				message.append(" выбранный интерфейс! ");
				    			} else {
				    				message.append(" выбранную папку! ");
				    			}
				    			message.append("Владельцем объекта является пользователь " + userName + ".");
				                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, message.toString());
				            	return;
							}
						}
					}
				}
			} catch (KrnException ex) {
				ex.printStackTrace();
			}
    	}
        if (src == miRename) {
            try {
                UserSessionValue us = krn.getObjectBlocker(getSelectedNode().getKrnObj().id);
                if (us != null) {
                    MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Интерфейс '" + getSelectedNode().toString() + "' заблокирован!\n" + "Пользователь: " + us.name + "\n" + "IP адрес: " + us.ip + "\n" + "Имя компьютера: " + us.pcName);
                    return;
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            renameInterface();
        } else if (src == miSendToRecycle) {
        	try {
				sendToRecycle();
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
        }
        super.actionPerformed(e);
    }
    
    private void sendToRecycle() throws KrnException {
    	InterfaceNode selectedNode = (InterfaceNode) getSelectedNode();
    	int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Перемещение интерфейса '" + selectedNode.toString() + "' в корзину!\nПродолжить?");
        if (res == ButtonsFactory.BUTTON_YES) {
       	 	// Считать все свойства объекта UI и перезаписать их на объект класса UIRecycle
        	 Kernel kernel = Kernel.instance();
        	 KrnObject obj = selectedNode.getKrnObj();
        	 KrnClass cls = kernel.getClassByName("UIRecycle");
        	 KrnObject objRecycle = kernel.createObject(cls, 0);

        	 byte[] config = kernel.getBlob(obj, "config", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "config", 0, config, 0, 0);
        	 
        	 KrnObject[] filtersFolder = kernel.getObjects(obj, "filtersFolder", 0);
        	 if (filtersFolder != null && filtersFolder.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "filtersFolder", 0, filtersFolder[0].id, 0, true);
        	 }
        	 
        	 KrnObject[] parent = kernel.getObjects(obj, "parent", 0);
        	 if (parent != null && parent.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "parent", 0, parent[0].id, 0, true);
        	 }

        	 byte[] strings = kernel.getBlob(obj, "strings", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "strings", 0, strings, 0, 0);
        	 
        	 String[] title = kernel.getStrings(obj, "title", 0, 0);
        	 if (title != null && title.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "title", 0, 0, title[0], 0);
        	 }
        	 
        	 byte[] webConfig = kernel.getBlob(obj, "webConfig", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "webConfig", 0, webConfig, 0, 0);
        	 
        	 long[] webConfigChanged = kernel.getLongs(obj, "webConfigChanged", 0);
        	 if (webConfigChanged != null && webConfigChanged.length > 0) {
        		 kernel.setLong(objRecycle.id, cls.id, "webConfigChanged", 0, webConfigChanged[0], 0);
        	 }
        	 
        	 String eventInitiator = kernel.getUser().getName();
    		 kernel.setString(objRecycle.id, cls.id, "eventInitiator", 0, 0, eventInitiator, 0);
        	 
    		 kernel.setString(objRecycle.id, cls.id, "uid", 0, 0, obj.uid, 0);
    		 
    		 SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm");
    		 java.util.Date currentDate = new java.util.Date();
    		 String eventDate = format.format(currentDate);
    		 kernel.setString(objRecycle.id, cls.id, "eventDate", 0, 0, eventDate, 0);

             // Выделение родительского узла
    		 InterfaceNode parentNode = (InterfaceNode) selectedNode.getParent();
             setSelectedNode(parentNode);
    		 
     		// Удаление узла из дерева
             ((InterfaceTreeModel) getModel()).removeNodeFromParent(selectedNode);
             
        	 KrnObject parentObj = parentNode.getKrnObj();
        	 Collection<Object> values = Collections.singletonList((Object) obj);
        	 kernel.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);	// Удаление из списка детей
        	 kernel.deleteObject(obj, 0);
        	 kernel.writeLogRecord(SystemEvent.EVENT_SEND_UI_TO_RECYCLE, selectedNode.toString());
        }
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.COPY_TYPE, copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Вставка копии интерфейса", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String ifcName = cp.getElementName();
                if (ifcName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя интерфейса!", "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    try {
                        String oldName = copyNode.toString();
                        if (!copyNode.isCutProcess()) {
                            byte[] data = krn.getBlob(copyNode.getKrnObj(), "config", 0, 0, 0);
                            KrnObject ui = krn.createObject(Kernel.SC_UI, 0);
                            krn.setString(ui.id, ui.classId, "title", 0, langId, ifcName, 0);
                    		long currentUserId = krn.getUserSession().userObj.id;
                            krn.setLong(ui.id, ui.classId, "developer", 0, currentUserId, 0);
                            krn.setBlob(ui.id, ui.classId, "config", 0, data, 0, 0);
                            List langs = LangItem.getAll();
                            MapMap stringsMap = new MapMap();
                            for (int i = 0; i < langs.size(); i++) {
                                LangItem item = (LangItem) langs.get(i);
                                Map m = new HashMap();
                                stringsMap.put(new Long(item.obj.id), m);
                                byte[] strings = krn.getBlob(copyNode.getKrnObj(), "strings", 0, item.obj.id, 0);
                                if (strings.length > 0) {
                                    ByteArrayInputStream is = new ByteArrayInputStream(strings);
                                    SAXBuilder b = new SAXBuilder();
                                    Element e = b.build(is).getRootElement();
                                    List chs = e.getChildren();
                                    for (int j = 0; j < chs.size(); j++) {
                                        Element ch = (Element) chs.get(j);
                                        String uid = ch.getAttributeValue("uid");
                                        if (ch.getContentSize() > 0) {
                                            for (int k = 0; k < ch.getContentSize(); k++) {
                                                if (ch.getContent(k) instanceof CDATA) {
                                                    String s = ((CDATA) ch.getContent(k)).getText();
                                                    byte[] value = s.getBytes();
                                                    m.put(uid, value);
                                                } else if (ch.getContent(k) instanceof Text) {
                                                    String value = ch.getText();
                                                    m.put(uid, value);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            XMLOutputter out = new XMLOutputter();
                            out.getFormat().setEncoding("UTF-8");
                            Iterator it = stringsMap.keySet().iterator();
                            while (it.hasNext()) {
                                Long lid = (Long) it.next();
                                Element e = new Element("Messages");
                                Map msgs = null;
                                msgs = stringsMap.get(lid);
                                for (Iterator uidIt = msgs.keySet().iterator(); uidIt.hasNext();) {
                                    String uid = (String) uidIt.next();
                                    Element ch = new Element("Msg");
                                    ch.setAttribute("uid", uid);
                                    Object msg = msgs.get(uid);
                                    if (msg instanceof String) {
                                        ch.setText((String) msg);
                                    } else if (msg instanceof byte[]) {
                                        CDATA cdata = new CDATA(new String((byte[]) msg));
                                        ch.addContent(cdata);
                                    }
                                    e.addContent(ch);
                                }
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                out.output(e, os);
                                os.close();
                                krn.setBlob(ui.id, ui.classId, "strings", 0, os.toByteArray(), lid.longValue(), 0);
                            }
                            krn.writeLogRecord(SystemEvent.EVENT_COPY_INTERFACE, "'" + oldName + "' в '" + ifcName + "'");
                            model.addNode(new InterfaceNode(ui, ifcName, langId), parent, false);
                            // Генерация новых UUID для копии интерфейса 
                            InterfaceFrame frame = new InterfaceFrame(ui);
                            PropertyHelper.forseGenUUID = true;
                            PropertyHelper.genUUID = false;
                            krn.getObjectById(langId, 0);
                            frame.setInterfaceLang(krn.getObjectById(langId, 0));
                            frame.load(DesignerFrame.instance().getProgressBar());
                            frame.setModified(true); 
                            frame.save(DesignerFrame.instance().getProgressBar());
                        } else {
                            krn.setString(copyNode.getKrnObj().id, copyNode.getKrnObj().classId, "title", 0, langId, ifcName, 0);
                            model.deleteNode(copyNode, true);
                            model.addNode(new InterfaceNode(copyNode.getKrnObj(), ifcName, langId), parent, false);
                            defaultDeleteOperations();
                            krn.writeLogRecord(SystemEvent.EVENT_MOVE_INTERFACE, "'" + oldName + "' в '" + ifcName + "' в папку '" + parent.toString() + "'");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        PropertyHelper.forseGenUUID = false; 
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }
    }

    private void renameInterface() {
        model.renameNode();
    }

    public void renameInterface(InterfaceNode node) {
        ((InterfaceTreeModel)model).rename(node);
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
        KrnClass cls = null;
        Kernel krn = Kernel.instance();
        try {
            cls = krn.getClassByName("UIRoot");
            KrnObject uiRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = {uiRoot.id};
            StringValue[] svs = krn.getStringValues(ids, cls.id, "title", langId,
                    false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }
            root = new InterfaceNode(uiRoot, title, langId);
            model = new InterfaceTreeModel(root);
            setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();
    }

    public DesignerTreeNode[] getSelectedNodes() {
        ArrayList<InterfaceNode> list = new ArrayList<InterfaceNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            InterfaceNode node = (InterfaceNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        InterfaceNode[] res = new InterfaceNode[list.size()];
        list.toArray(res);
        return res;
    }
    public DesignerTreeNode[] getSelectedNodes(boolean includeNonLeafNodes) {
        ArrayList<InterfaceNode> list = new ArrayList<InterfaceNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            InterfaceNode node = (InterfaceNode)path.getLastPathComponent();
            list.add(node);
        }
        InterfaceNode[] res = new InterfaceNode[list.size()];
        list.toArray(res);
        return res;
    }


    public void traverseDir(File file,InterfaceNode selectedNode){    
        InterfaceTreeModel treeModel =(InterfaceTreeModel)this.getModel();
        try {
            InterfaceNode newFolderNode = (InterfaceNode)treeModel.createFolderNode(file.getName(),selectedNode);
            File childFiles[] = file.listFiles();
            for(File childFile:childFiles)
            {
                if(childFile.isDirectory()) {
                    traverseDir(childFile,newFolderNode);}
                else {
                    importToXml(newFolderNode, childFile);
                }
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        }

    }
    public void chooseFilesFromLocalDir(){
        File file[] = null;
        JFileChooser  fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFiles();
            for(int i = 0;i<file.length;i++) {
                InterfaceNode selectedNode = (InterfaceNode)getSelectedNode();
                if(file[i].isDirectory())
                {
                    traverseDir(file[i],selectedNode);
                }
                else {
                    importToXml(selectedNode,file[i]);
                }
            }
        }}
    public void importToXml(InterfaceNode selectedNode,File fileToImport) {
        Element config = null;
        Element ifc = null;
        SAXBuilder builder = new SAXBuilder();
        String title = fileToImport.getName();
        title = title.substring(0, title.indexOf('.')-1);
        InterfaceTreeModel treeModel =(InterfaceTreeModel)this.getModel();
        KrnObject obj = null;
        KrnClass cls = null;
        Kernel krn = Kernel.instance();
        try {
            cls = krn.getClassByName("UI");
        } catch (KrnException e) {
            e.printStackTrace();
        }
        try {
            org.jdom.Document document = (org.jdom.Document) builder.build(fileToImport);
            ifc= document.getRootElement();   

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element krnclas = ifc.getChild("KrnClass");
        String uid = krnclas.getChildText("uid");
        String id_value = ifc.getChild("KrnClass").getChildText("id");
        long id = Integer.parseInt(id_value);
        KrnObject oldObj = new KrnObject(id,uid,cls.id);
        try {
            AbstractDesignerTreeNode newNode = treeModel.createChildNode(title,oldObj,selectedNode);
            obj = newNode.getKrnObj();
        } catch (KrnException e1) {
            if(e1.getMessage().startsWith("java.sql.SQLException: Duplicate entry"))
            {e1.printStackTrace();
                MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                        "Интерфейс с таким uid уже существует, выбранный вами файл"+title+"не был импортирован");
            }
            return;
        }

        config = ifc.getChild("Component");
        ByteArrayOutputStream os =null;
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        try {
            os = new ByteArrayOutputStream();
            out.output(config, os);
            krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KrnException e) {
            e.printStackTrace();
        }

        for(Object e : ifc.getChildren("title"))
        {long langid = Long.parseLong(((Element)e).getAttributeValue("langId"));
        try {

            krn.setString(obj.id, cls.id, "title", 0, langid,((Element)e).getValue() , 0);
        } catch (KrnException e1) {
            e1.printStackTrace();
        }
        }
        
        for(Object e : ifc.getChildren("Messages"))
        {   String longlangid=((Element)e).getAttributeValue("langId");
        ((Element)e).removeAttribute("langId");
        long langid = Long.parseLong(longlangid);
        os = new ByteArrayOutputStream();
        try {
            out.output((Element)e, os);
            os.close();
            krn.setBlob(obj.id, obj.classId, "strings", 0,
                    os.toByteArray(), langid, 0);
        } catch (IOException e2) {

            e2.printStackTrace();
        }
        catch (KrnException e1) {
            e1.printStackTrace();
        }
        }

        List<Element> webConfigs = ifc.getChildren("webConfig");
        for(Element webConfig: webConfigs) 
        {
            os = new ByteArrayOutputStream();
            long langid = Long.parseLong(webConfig.getAttributeValue("langId"));
            try {
                out.output(webConfig.getContent(), os);
                os.close();
                krn.setBlob(obj.id, obj.classId, "webConfig", 0, os.toByteArray(), langid,0);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (KrnException e1) {
                e1.printStackTrace();
            }            
         } 
 }



    public void iterateAndExport(List<DesignerTreeNode> ifcNodes,String directory){
        for(DesignerTreeNode ifcNode_obj: ifcNodes)
        {
            InterfaceNode ifcNode = (InterfaceNode) ifcNode_obj;
            if(ifcNode.isLeaf())
            {exportIfc(ifcNode,directory);}
            else {
                String newDir = directory+"\\"+ifcNode.getTitle();
                File dir = new File(directory);
                dir.mkdir();
                iterateAndExport(ifcNode.children(true),newDir);
            }
        }
    }

    private void setPath(String path) {

        System.out.println(path);
    }


    private Element getXmlFromByte(byte[] string) {
        Element stringElem = null;
        if (string.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(string);
            SAXBuilder b = new SAXBuilder();
            try {
                stringElem = b.build(is).getRootElement();
                is.close();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringElem.detach();
        }

        return stringElem;
    }

    private void exportIfc(InterfaceNode ifcNode,String directory)
    {
        // String path = "C:\\Users\\Администратор\\Desktop\\filterXmls\\"+parentFolder;
        String titles = "";
        String title = ifcNode.getTitle();
        String filename = title.replaceAll("[^a-zA-Zа-яА-Я0-9,.\\s-]", "_");
        File dir = new File(directory);
        dir.mkdir();
        String xmlFilePath="";
        try {
            xmlFilePath = dir.getCanonicalPath()+"\\"+filename+".xml";
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        FileWriter file = null;
        try {
            file = new FileWriter(xmlFilePath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (ifcNode == null || !ifcNode.isLeaf())
            return;
        setSelectedNode(ifcNode);
        KrnObject ifcObj = ifcNode.getKrnObj();        
        KrnClass cls;
        Kernel krn = Kernel.instance();
        try {
            cls = krn.getClassByName("UI");
        } catch (KrnException e1) {
            e1.printStackTrace();
        }
        //root
        Element ifc= new Element("ifc");
        org.jdom.Document doc = new  org.jdom.Document(ifc);

        //config
        Element components = getXml(ifcObj,"config");
        components.detach();
        doc.getRootElement().addContent(components);

        long [] langIds = {krn.getLangIdByCode("RU"), krn.getLangIdByCode("KZ")};

        try {
            //KrnClass
            Element oldClass  =new Element("KrnClass");      
            Element uid = new Element("uid");
            uid.setText(ifcObj.uid);
            oldClass.addContent(uid);
            Element id = new Element("id");
            id.setText(Long.toString(ifcObj.id));
            oldClass.addContent(id);
            doc.getRootElement().addContent(oldClass);

            //title ru
            String[] titleRu = krn.getStrings(ifcObj, "title", langIds[0], 0);
            Element titleRuElem = new Element("title");
            titleRuElem.setAttribute("langId",Long.toString(langIds[0]));
            if(titleRu.length>0) {
                titleRuElem.setText(titleRu[0]);
            } 
            doc.getRootElement().addContent(titleRuElem);

            //title kz
            String[] titleKz = krn.getStrings(ifcObj, "title", langIds[1], 0);
            Element titleKzElem = new Element("title");
            titleKzElem.setAttribute("langId",Long.toString(langIds[1]));
            if(titleKz.length>0) {
                titleKzElem.setText(titleKz[0]);
            }
            doc.getRootElement().addContent(titleKzElem);

            //webConfig ru
            Element webConfig_ru =new Element("webConfig");
            webConfig_ru.setAttribute("langId",Long.toString(langIds[0]));
            byte[] webConfigRu = krn.getBlob(ifcObj, "webConfig", 0, langIds[0], 0);
            Element webConfigRuElem = getXmlFromByte(webConfigRu);
            if(webConfigRuElem!=null) {
                webConfig_ru.addContent(webConfigRuElem);}
            else {
                webConfigRuElem = new Element("webConfig");  
            }
            doc.getRootElement().addContent(webConfig_ru);

            //webConfig kz
            Element webConfig_kz =new Element("webConfig");
            webConfig_kz.setAttribute("langId",Long.toString(langIds[1]));
            byte[] webConfigKz = krn.getBlob(ifcObj, "webConfig", 0, langIds[1], 0);
            Element webConfigKzElem = getXmlFromByte(webConfigKz);
            if(webConfigKzElem!=null) {
                webConfig_kz.addContent(webConfigKzElem);}
            else {
                webConfigRuElem = new Element("webConfig");  
            }
            doc.getRootElement().addContent(webConfig_kz);

            //messages ru
            byte []stringRu = krn.getBlob(ifcObj, "strings", 0, langIds[0], 0);
            Element stringRuElem = getXmlFromByte(stringRu);
            if(stringRuElem==null) {
                stringRuElem = new Element("Messages");
            }
            stringRuElem.setAttribute("langId",Long.toString(langIds[0]));
            doc.getRootElement().addContent(stringRuElem);

            //messages kz
            byte[]stringKz = krn.getBlob(ifcObj, "strings", 0, langIds[1], 0);
            Element stringKzElem = getXmlFromByte(stringKz);
            if(stringKzElem==null) {
                stringKzElem = new Element("Messages");
            }
            stringKzElem.setAttribute("langId",Long.toString(langIds[1]));
            doc.getRootElement().addContent(stringKzElem);

        } catch (KrnException e) {
            try {
                file.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        try {
            out.output(doc, file);
            file.close();
            titles=titles.concat("-"+title+"\n");
        } catch (IOException e) {
            e.printStackTrace(); 
        }


        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);

        //get filtersFolder
        //get parent    
    }

    public KrnObject getSelectedIfc() {
        TreePath[] paths = getSelectionPaths();
        if (paths != null && paths.length > 0) {
            TreePath path = paths[0];
            InterfaceNode node = (InterfaceNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                return node.getKrnObj();
            }
        }
        return null;
    }
    
    /**
     * Получить корень дерева.
     *
     * @return the root
     */
    public InterfaceNode getRoot() {
        return (InterfaceNode) getModel().getRoot();
    }
}
