package kz.tamur.guidesigner.reports;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.User;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.ByteArrayInputStream;

import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserNode;
import kz.tamur.comps.*;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.*;

import org.jdom.input.SAXBuilder;
import org.jdom.Element;

import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getLightSysColor;
import static kz.tamur.or3.util.SystemEvent.EVENT_COPY_REPORT;
import static kz.tamur.or3.util.SystemEvent.EVENT_MOVE_REPORT;
import static kz.tamur.or3.util.SystemEvent.EVENT_CREATE_REPORT;
import static kz.tamur.rt.Utils.getImageIcon;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class ReportTree extends DesignerTree {


    private String searchString = "";
    private int typeComboIndex = 0, conditionComboIndex = 0;
    private OrFrame emptyFrame;
    private ReportNode lastSelectedNode;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;

    public ReportTree(final ReportNode root, OrFrame emptyFrame) {
        super(root);
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.REPORTS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.REPORTS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.REPORTS_CREATE_RIGHT);

        this.root = root;
        this.emptyFrame = emptyFrame;
        model = new ReportTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(getLightSysColor());
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                ReportNode node = (ReportNode)e.getPath().getLastPathComponent();
                miRename.setVisible(!node.isLeaf());
            }
        });
        miSendToRecycle.addActionListener(this);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == miSendToRecycle) {
			try {
				sendToRecycle();
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
		}
		super.actionPerformed(e);
	}
    
    private void sendToRecycle() throws KrnException {
    	ReportNode selectedNode = (ReportNode) getSelectedNode();
    	int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Перемещение отчета '" + selectedNode.toString() + "' в корзину!\nПродолжить?");
        if (res == ButtonsFactory.BUTTON_YES) {
       	 	// Считать все свойства объекта ReportPrinter и перезаписать их на объект класса ReportPrinterRecycle
        	 Kernel kernel = Kernel.instance();
        	 KrnObject obj = selectedNode.getKrnObj();
        	 KrnClass cls = kernel.getClassByName("ReportPrinterRecycle");
        	 KrnObject objRecycle = kernel.createObject(cls, 0);
        	 
        	 String[] constraints = kernel.getMemos(obj, "constraints", 0, 0);
        	 if (constraints != null) {
        		 for(int i = 0; i < constraints.length; i++) {
        			 kernel.setMemo((int) objRecycle.id, (int) cls.id, "constraints", i, 0, constraints[i], 0);
        		 }
        	 }

        	 String[] descInfo = kernel.getMemos(obj, "descInfo", 0, 0);
        	 if (descInfo != null) {
        		 for(int i = 0; i < descInfo.length; i++) {
        			 kernel.setMemo((int) objRecycle.id, (int) cls.id, "descInfo", i, 0, descInfo[i], 0);
        		 }
        	 }
        	 
        	 long[] flags = kernel.getLongs(obj, "flags", 0);
        	 if (flags != null) {
        		 for (int i = 0; i < flags.length; i++) {
        			 kernel.setLong(objRecycle.id, cls.id, "flags", i, flags[i], 0);
        		 }
        	 }
        	 
        	 String[] ref = kernel.getMemos(obj, "ref", 0, 0);
        	 if (ref != null) {
        		 for(int i = 0; i < ref.length; i++) {
        			 kernel.setMemo((int) objRecycle.id, (int) cls.id, "ref", i, 0, ref[i], 0);
        		 }
        	 }
        	 
        	 String[] title = kernel.getStrings(obj, "title", 0, 0);
        	 if (title != null && title.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "title", 0, 0, title[0], 0);
        	 }
        	 
//        	 KrnObject[] bases = kernel.getObjects(obj, "bases", new long[] { 0 }, 0);
//        	 if (bases != null) {
//        		 for(int i = 0; i < bases.length; i++) {
//        			 kernel.setObject(objRecycle.id, cls.id, "bases", i, bases[i].id, 0, true);
//        		 }
//        	 }
        	 
        	 byte[] config = kernel.getBlob(obj, "config", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "config", 0, config, 0, 0);
        	 
        	 byte[] data = kernel.getBlob(obj, "data", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "data", 0, data, 0, 0);
        	 
        	 byte[] data2 = kernel.getBlob(obj, "data2", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "data2", 0, data2, 0, 0);

        	 KrnObject[] parent = kernel.getObjects(obj, "parent", 0);
        	 if (parent != null && parent.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "parent", 0, parent[0].id, 0, true);
        	 }
        	 
        	 byte[] template = kernel.getBlob(obj, "template", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "template", 0, template, 0, 0);
        	 
        	 byte[] template2 = kernel.getBlob(obj, "template2", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "template2", 0, template2, 0, 0);

        	 KrnObject[] rootReport = kernel.getObjects(obj, "базовый отчет", 0);
        	 if (rootReport != null && rootReport.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "базовый отчет", 0, rootReport[0].id, 0, true);
        	 }
        	 
        	 String eventInitiator = kernel.getUser().getName();
    		 kernel.setString(objRecycle.id, cls.id, "eventInitiator", 0, 0, eventInitiator, 0);
        	 
    		 kernel.setString(objRecycle.id, cls.id, "uid", 0, 0, obj.uid, 0);
    		 
    		 SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm");
    		 java.util.Date currentDate = new java.util.Date();
    		 String eventDate = format.format(currentDate);
    		 kernel.setString(objRecycle.id, cls.id, "eventDate", 0, 0, eventDate, 0);

             // Выделение родительского узла
    		 ReportNode parentNode = (ReportNode) selectedNode.getParent();
             setSelectedNode(parentNode);
    		 
     		// Удаление узла из дерева
             ((ReportTreeModel) getModel()).removeNodeFromParent(selectedNode);
             
        	 KrnObject parentObj = parentNode.getKrnObj();
        	 Collection<Object> values = Collections.singletonList((Object) obj);
        	 kernel.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);	// Удаление из списка детей
        	 kernel.deleteObject(obj, 0);
        	 kernel.writeLogRecord(SystemEvent.EVENT_SEND_REPORT_TO_RECYCLE, selectedNode.toString());
        }
    }
    
    protected void defaultDeleteOperations() {}

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel(1);
        sip.setSearchText(searchString);
        sip.setTypeIndex(typeComboIndex);
        sip.setConditionIndex(conditionComboIndex);
        DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Поиск отчёта", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            typeComboIndex = sip.getType();
            conditionComboIndex = sip.getCondition();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
            	public void run() {
            		Kernel.instance();
                	TreeNode fnode = null;
                	if (sip.findByUID()) {
                		StringTokenizer st = new StringTokenizer(searchString, ".");
                	    if (st.countTokens() < 2) {
            	            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Неверный формат UID-а!");
            	            return;
            	        }
//                        fnode = finder.findFirst(node, new UIDPattern(searchString));
                	    fnode = searchByUID(searchString);
                	} else if(sip.findByID()) {
                		long id = Long.parseLong(searchString);
                		fnode = searchById(id);
                	} else {
                        fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                	}
                    if (fnode != null) {
                    	ReportTree.this.setSelectedNode((ReportNode)fnode);
//                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
//                        if (path != null) {
//                            setSelectionPath(path);
//                            scrollPathToVisible(path);
//                        }
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();

        }
    }


    public DesignerTreeNode[] getSelectedNodes() {
        ArrayList<ReportNode> list = new ArrayList<ReportNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ReportNode node = (ReportNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        ReportNode[] res = new ReportNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Вставка копии отчёта", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String reportName = cp.getElementName();
                if (reportName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя интерфейса!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    final long langId = emptyFrame.getInterfaceLang().id;
                    try {
                        if (!copyNode.isCutProcess()) {
                            final KrnClass cls = krn.getClassByName("ReportPrinter");
                            byte[] data = krn.getBlob(copyNode.getKrnObj(),
                                    "config", 0, 0, 0);
                            Element xml = null;
                            KrnObject repObj = krn.createObject(cls, 0);
                            krn.setString(repObj.id, repObj.classId,
                                    "title", 0, langId, reportName, 0);
                            krn.setBlob(repObj.id, repObj.classId,
                                    "config", 0, data, 0, 0);
                            OrGuiComponent comp = null;
                            if (data.length > 0) {
                                ByteArrayInputStream is = new ByteArrayInputStream(data);
                                SAXBuilder b = new SAXBuilder();
                                xml = b.build(is).getRootElement();
                                is.close();
                                comp = Factories.instance().create(xml, Mode.DESIGN, null);
                            } else {
                                comp = Factories.instance().create("ReportPrinter", null);
                            }
                            comp.setPropertyValue(new PropertyValue(reportName, langId,
                                    comp.getProperties().getChild("title")));
                            model.addNode(new ReportNode(repObj, reportName, comp,
                                    parent.getChildCount(), emptyFrame), parent, false);
                            krn.writeLogRecord(EVENT_COPY_REPORT, "'" + copyNode.toString() + "' в '" + reportName + "'");
                        } else {
                            krn.setString(copyNode.getKrnObj().id,
                                    copyNode.getKrnObj().classId, "title", 0,
                                    langId, reportName, 0);
                            model.deleteNode(copyNode, true);
                            model.addNode(new ReportNode(copyNode.getKrnObj(),
                                    reportName, ((ReportNode)copyNode).getOrGuiComponent(),
                                    parent.getChildCount(), emptyFrame), parent, false);
                            defaultDeleteOperations();
                            krn.writeLogRecord(EVENT_MOVE_REPORT, "'" + copyNode.toString() + "' в '" + reportName + "'");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                    krn.setAutoCommit(true);
                }

            }
        }
    }

    public void deleteNode(ReportNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


    public class ReportTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private ReportNode rootNode;

        public ReportTreeModel(TreeNode root) {
            super(root);
            rootNode = (ReportNode)root;
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("ReportFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            final long langId = Utils.getInterfaceLangId();
            AbstractDesignerTreeNode selNode = getSelectedNode();
            if (selNode == null) {
                selNode = rootNode;
            } else if (selNode.isLeaf()) {
                 selNode = (ReportNode)selNode.getParent();
            }
            TreePath parent = new TreePath(selNode);
            setSelectionPath(parent);
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children",
                    idx, obj.id, 0, false);
            ReportNode node = new ReportNode(obj, title, null, idx, emptyFrame);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("ReportPrinter");
            final KrnObject obj = krn.createObject(cls, 0);
            final long langId = Utils.getInterfaceLangId();
            AbstractDesignerTreeNode selNode = getSelectedNode();
            KrnObject uiObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(uiObj.id, uiObj.classId, "children",
                    idx, obj.id, 0, false);
            OrGuiComponent comp = Factories.instance().create("ReportPrinter", emptyFrame);
            comp.setPropertyValue(new PropertyValue(title,
                    comp.getProperties().getChild("title")));
            ReportNode node = new ReportNode(obj, title, comp, idx, emptyFrame);
            node.setModified(true);
            insertNodeInto(node, selNode, selNode.getChildCount());
            krn.writeLogRecord(EVENT_CREATE_REPORT, title);
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(KrnObject obj, KrnObject parentObj) throws KrnException {
            Kernel kernel = Kernel.instance();
            ReportNode parentNode = (ReportNode) find(parentObj);
            int idx = parentNode.getChildCount();
            KrnClass cls = kernel.getClassByName("ReportPrinter");
            KrnAttribute titleAttr = kernel.getAttributeByName(cls, "title");
            String title = kernel.getStringsSingular(obj.id, titleAttr.id, 0, false, true);
            kernel.setObject(parentObj.id, parentObj.classId, "children", idx, obj.id, 0, false);
            OrGuiComponent comp = Factories.instance().create("ReportPrinter", emptyFrame);
            comp.setPropertyValue(new PropertyValue(title, comp.getProperties().getChild("title")));
            ReportNode node = new ReportNode(obj, title, comp, idx, emptyFrame);
            insertNodeInto(node, parentNode, idx);
            kernel.writeLogRecord(SystemEvent.EVENT_RESTORE_REPORT_FROM_RECYCLE, title);
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            ReportNode parent = (ReportNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
            }
        }

        public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new ReportNode(node.getKrnObj(), node.toString(),
                        ((ReportNode)node).getOrGuiComponent(), parent.getChildCount(), emptyFrame);
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId,
                    "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void renameNode() {
            CreateElementPanel cp = new CreateElementPanel(
                    CreateElementPanel.RENAME_TYPE, getSelectedNode().toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Переименование папки", cp);
            dlg.show();
            int res = dlg.getResult();
            if (res == ButtonsFactory.BUTTON_OK) {
                Kernel krn = Kernel.instance();
                try {
                    KrnClass cls = krn.getClassByName("ReportFolder");
                    KrnObject obj = getSelectedNode().getKrnObj();
                    KrnAttribute attr = krn.getAttributeByName(cls, "title");
                    String newName = cp.getElementName();
                    krn.setString(obj.id, attr.id, 0, emptyFrame.getInterfaceLang().id, newName, 0);
                    ReportNode source = (ReportNode) ReportTree.this.root.find(obj).getLastPathComponent();
                    source.rename(newName);
                    TreeNode[] tp = getPathToRoot(source);
                    fireTreeNodesChanged(this, tp, null, null);
                } catch (KrnException e) {
                    e.printStackTrace();
                }

            }
        }

        public void rename(ReportNode node) {
            if (node != null) {
                TreeNode[] tpath = getPathToRoot(node);
                fireTreeNodesChanged(this, tpath, null, null);
                repaint();
            }
        }

    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            ReportNode node = (ReportNode) value;
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (selected) {
                l.setBackground(getDarkShadowSysColor());
                l.setForeground(node.isModified() ? Color.yellow : Color.white);
            } else {
                l.setBackground(getLightSysColor());
                l.setForeground(node.isModified() ? Color.red : Color.black);
            }
            if (!leaf) {
                l.setIcon(getImageIcon(expanded ? "Open" : "CloseFolder"));
            } else {
                OrGuiComponent print = node.getOrGuiComponent();
                if (print != null) {
                    PropertyValue pv = print.getPropertyValue(print.getProperties().getChild("editorType"));
                    if (!pv.isNull()) {
                        int type = pv.intValue();
                        if (type == Constants.MSWORD_EDITOR) {
                            l.setIcon(getImageIcon("Word"));
                        } else if (type == Constants.MSEXCEL_EDITOR) {
                            l.setIcon(getImageIcon("Excel"));
                        } else if (type == Constants.JASPER_EDITOR) {
                            l.setIcon(getImageIcon("hr"));
                        }
                    } else {
                        l.setIcon(getImageIcon("ReportPrinter"));
                    }
                }
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }

    }


    public void renameReport(ReportNode node) {
        ((ReportTreeModel)model).rename(node);
    }

    public ReportNode find(String title) {
        TreeNode n = finder.findFirst(root, new StringPattern(title));
        return (ReportNode)n;
    }

    public ReportNode getLastSelectedNode() {
        return lastSelectedNode;
    }

    public void setLastSelectedNode(ReportNode lastSelectedNode) {
        this.lastSelectedNode = lastSelectedNode;
    }
    
    public void setSelectedNode(ReportNode selectedNode) {
        TreePath tpath = new TreePath(selectedNode.getPath());
        if(tpath != null){
        	setSelectionPath(tpath);
        	scrollPathToVisible(tpath);
        }
    }

	protected void showPopup(MouseEvent e) {
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

    /**
     * Получить корень дерева.
     *
     * @return the root
     */
    public ReportNode getRoot() {
        return (ReportNode) getModel().getRoot();
    }
    
}
