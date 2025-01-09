package kz.tamur.guidesigner.filters;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;

import javax.swing.*;
import javax.swing.tree.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.util.*;
import kz.tamur.comps.Constants;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class FiltersTree extends DesignerTree implements ActionListener {

    private long langId;
    private ControlTabbedContent tcontent;

    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;

    public FiltersTree(FilterNode root, long langId) {
        super(root);
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.FILTERS_CREATE_RIGHT);

        this.root = root;
        this.langId = langId;
        model = new FilterTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());

        //miRename.setEnabled(false);
        //addTreeSelectionListener(this);
        
        miSendToRecycle.addActionListener(this);
    }

    public FiltersTree(FilterNode root, long langId,ControlTabbedContent tcontent) {
        super(root);
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.FILTERS_CREATE_RIGHT);
        this.root = root;
        this.langId = langId;
        this.tcontent=tcontent;
        model = new FilterTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());
        miSendToRecycle.addActionListener(this);
    }

    protected void defaultDeleteOperations() throws KrnException {
        AbstractDesignerTreeNode node = getSelectedNode();
        if (tcontent!=null && tcontent.isFilterOpened(node.getKrnObj().id)) {
            tcontent.removeSelectedFlt();
        }
    }

    public FilterNode findByName(String name) {
        setSelectionPath(new TreePath(root));
        AbstractDesignerTreeNode node = getSelectedNode();
        if (node == null) {
            node = root;
        }
        TreeNode fnode = finder.findFirst(
                node, new StringPattern(name));
        if (fnode != null) {
            setSelectedNode(((FilterNode)fnode).getKrnObj());
            return (FilterNode)fnode;
        } else {
            return null;
        }
    }

    @Override
    public void setSelectionModel(TreeSelectionModel selectionModel) {
        super.setSelectionModel(selectionModel);
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((JDialog) getTopLevelAncestor(), "Поиск фильтра", sip);
        dlg.show();
        if (dlg.isOK()) {
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = finder.findFirst(node, new StringPattern(sip.getSearchText(), sip.getSearchMethod()));
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        setSelectionPath(path);
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();
        }
    }


    public class FilterTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public FilterTreeModel(TreeNode root) {
            super(root);
        }

        public void renameFilter(FilterNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("FilterFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            FilterNode selNode = (FilterNode)getSelectedNode();
            KrnObject filterObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(filterObj.id, filterObj.classId, "children", idx, obj.id, 0, false);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
            FilterNode node = new FilterNode(obj, title, langId, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }
        
        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            return createChildNode(title,null);
        }

		public AbstractDesignerTreeNode createChildNode(String title, KrnObject obj) throws KrnException {
			final Kernel krn = Kernel.instance();
			final KrnClass cls = krn.getClassByName("Filter");
			if (obj == null)
				obj = krn.createObject(cls, 0);
			else
				obj = krn.createObject(cls, obj.uid, 0);
			FilterNode selNode = (FilterNode) root;
			FilterNode inode = (FilterNode) getSelectedNode();
			if (inode != null && !inode.isLeaf()) {
				selNode = inode;
			}
			KrnObject filterObj = selNode.getKrnObj();
			krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
			int idx = selNode.getChildCount();
			krn.setObject(filterObj.id, filterObj.classId, "children", idx, obj.id, 0, false);
    		long currentUserId = krn.getUserSession().userObj.id;
            krn.setLong(obj.id, obj.classId, "developer", 0, currentUserId, 0);
			FilterNode node = new FilterNode(obj, title, langId, idx);
			insertNodeInto(node, selNode, selNode.getChildCount());
			krn.writeLogRecord(SystemEvent.EVENT_CREATE_FILTER, title);
			return node;
		}

		public AbstractDesignerTreeNode createChildNode(KrnObject obj, KrnObject parentObj) throws KrnException {
            Kernel kernel = Kernel.instance();
            FilterNode parentNode = (FilterNode) find(parentObj);
            int idx = parentNode.getChildCount();
            KrnClass cls = kernel.getClassByName("Filter");
            KrnAttribute titleAttr = kernel.getAttributeByName(cls, "title");
            String title = kernel.getStringsSingular(obj.id, titleAttr.id, 0, false, true);
            kernel.setObject(parentObj.id, parentObj.classId, "children", idx, obj.id, 0, false);
            FilterNode node = new FilterNode(obj, title, langId, idx);
            insertNodeInto(node, parentNode, idx);
            kernel.writeLogRecord(SystemEvent.EVENT_RESTORE_FILTER_FROM_RECYCLE, title);
            return node;
        }
		
        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            FilterNode parent = (FilterNode)node.getParent();
            if(parent==null) return;//Для локального фильтра
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values = Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
                krn.writeLogRecord(SystemEvent.EVENT_DELETE_FILTER, node.toString());
            }
        }

        public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new FilterNode(node.getKrnObj(), node.toString(), node.getLangId(), parent.getChildCount());
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId, "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void renameNode() {
            FilterNode node = (FilterNode)getSelectedNode();
            CreateElementPanel op = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, node.toString());
            DesignerDialog dlg;
            if (getTopLevelAncestor() instanceof Dialog) {
                dlg = new DesignerDialog((Dialog)getTopLevelAncestor(), "Переименование фильтра", op);
            } else {
                dlg = new DesignerDialog((Frame)getTopLevelAncestor(), "Переименование фильтра", op);
            }
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                Kernel krn = Kernel.instance();
                try {
                    KrnClass cls = krn.getClassByName("Filter");
                    KrnObject obj = getSelectedNode().getKrnObj();
                    KrnAttribute attr = krn.getAttributeByName(cls, "title");
                    String newName = op.getElementName();
                    krn.setString(obj.id, attr.id, 0, langId, newName, 0);
                    krn.writeLogRecord(SystemEvent.EVENT_RENAME_FILTER, "'" + node.toString() + "' в '" + newName + "'");
                    FilterNode source = (FilterNode) FiltersTree.this.root.find(obj).getLastPathComponent();
                    source.rename(newName);
                    TreeNode[] tp = getPathToRoot(source);
                    fireTreeNodesChanged(this, tp, null, null);
                    
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
                l.setIcon(kz.tamur.rt.Utils.getImageIcon("FilterNode"));
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }

    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.COPY_TYPE, copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(), "Создание копии фильтра", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String filterName = cp.getElementName();
                if (filterName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя фильтра!", "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    try {
                        String oldName = copyNode.toString();
                        if (!copyNode.isCutProcess()) {
                            byte[] data = krn.getBlob(copyNode.getKrnObj(), "config", 0, 0, 0);
                            byte[] dataSql = krn.getBlob(copyNode.getKrnObj(), "exprSql", 0, 0, 0);
                            KrnObject filterObj = krn.createObject(Kernel.SC_FILTER, 0);
                            krn.setString(filterObj.id, filterObj.classId, "title", 0, langId, filterName, 0);
                    		long currentUserId = krn.getUserSession().userObj.id;
                            krn.setLong(filterObj.id, filterObj.classId, "developer", 0, currentUserId, 0);
                            krn.setBlob(filterObj.id, filterObj.classId, "config", 0, data, 0, 0);
                            krn.setBlob(filterObj.id, filterObj.classId, "exprSql", 0, dataSql, 0, 0);
                            krn.writeLogRecord(SystemEvent.EVENT_COPY_FILTER, "'" + oldName + "' в '" + filterName + "'");
                            model.addNode(new FilterNode(filterObj, filterName, langId, parent.getChildCount()), parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id, copyNode.getKrnObj().classId, "title", 0, langId, filterName, 0);
                            model.deleteNode(copyNode, true);
                            model.addNode(new FilterNode(copyNode.getKrnObj(), filterName, langId, parent.getChildCount()), parent, false);
                            defaultDeleteOperations();
                            krn.writeLogRecord(SystemEvent.EVENT_MOVE_FILTER, "'" + oldName + "' в '" + filterName + "' в папку '" + parent.toString() + "'");
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

    public void renameFilter(FilterNode node, String title) {
        ((FilterTreeModel)model).renameFilter(node, title);
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
        Kernel krn = Kernel.instance();
        try {
            KrnClass cls = krn.getClassByName("FilterRoot");
            KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = {filterRoot.id};
            StringValue[] svs = krn.getStringValues(ids, cls.id, "title", langId,
                    false, 0);
            String title = "Не назначен";
            if (svs.length > 0 && svs[0] != null) {
                title = svs[0].value;
            }
            root = new FilterNode(filterRoot, title, langId, 0);
            model = new FilterTreeModel(root);
            setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validate();
        repaint();
    }

    public DesignerTreeNode[] getSelectedNodes() {
        List<FilterNode> list = new ArrayList<FilterNode>();
        TreePath[] paths = getSelectionPaths();
        if(paths != null)
        for (TreePath path : paths) {
            FilterNode node = (FilterNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
            else {
                Enumeration ifcFolder = node.children();
                while(ifcFolder.hasMoreElements())
                {
                    FilterNode leaf =(FilterNode) ifcFolder.nextElement();
                    list.add(leaf);
                }
            }
        }
        FilterNode[] res = new FilterNode[list.size()];
        list.toArray(res);
        return res;
    }

    @Override
    protected void showPopup(MouseEvent e) {
        if (getSelectedNode().isLeaf()) {
            miSendToRecycle.setVisible(true);
            miCreateFolder.setEnabled(false);
            miCreateElement.setEnabled(false);
            miCopy.setEnabled(canCreate);
            miCut.setEnabled(canCreate);
            miPaste.setEnabled(false);
            miEdit.setEnabled(true);
            if(getSelectedNode() instanceof FilterNode) {
        		miView.setEnabled(true);
        	} else miView.setEnabled(false);
        } else {
            miSendToRecycle.setVisible(false);
            miCreateFolder.setEnabled(canCreate);
            miCreateElement.setEnabled(canCreate);
            miCopy.setEnabled(false);
            miCut.setEnabled(false);
            miEdit.setEnabled(false);
            miView.setEnabled(false);
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
    public FilterNode getRoot() {
        return (FilterNode) getModel().getRoot();
    }
    JFileChooser fc;
    SAXBuilder builder;
    public void importToXml() { 
        fc = new JFileChooser();
        Kernel krn = Kernel.instance();
        FiltersTree.FilterTreeModel model = (FiltersTree.FilterTreeModel)getModel();
        SAXBuilder builder = new SAXBuilder();
        String newline ="\n";
        File file[] = null;
        Element xml = null;
        String titles="";
        fc.setMultiSelectionEnabled(true);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFiles();
            for(int i = 0;i<file.length;i++) {
                try {
                    Document document = (Document) builder.build(file[i]);
                    xml= document.getRootElement();      
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                KrnClass cls = null;
                try {
                    cls = krn.getClassByName("Filter");
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                Element krnclas = xml.getChild("KrnClass");
                String uid = krnclas.getChildText("uid");
                String id_value = xml.getChild("KrnClass").getChildText("id");
                long id = Integer.parseInt(id_value);
                KrnObject oldObj = new KrnObject(id,uid,cls.id);
                if(xml!=null) {
                    String title =xml.getChildText("title");
                    try {
                        FilterNode filter_node = (FilterNode) model.createChildNode(title,oldObj);
                        KrnObject obj = filter_node.getKrnObj();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        XMLOutputter out = new XMLOutputter();
                        out.getFormat().setEncoding("UTF-8");
                        try {
                            out.output(xml, os);
                            os.close();
                            titles+="-"+title+"\n";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
                        // Сохранение sql для фильтра и некоторых параметров
                        krn.saveFilter(obj.id);

                    } catch (KrnException e1) {
                        if(e1.getMessage().startsWith("java.sql.SQLException: Duplicate entry"))
                        {
                            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                                    "Фильтр с таким uid уже существует, выбранный вами фильтр не был импортирован");
                        }
                        return;
                    }
                }}
            String msg = "";
        }
    }
    public void iterateAndExport(List<DesignerTreeNode> ifcNodes)
    {
        for(DesignerTreeNode ifcNode_obj: ifcNodes)
        {
            FilterNode ifcNode = (FilterNode) ifcNode_obj;
            if(ifcNode.isLeaf())
            {exportFilter(ifcNode,(FilterNode)ifcNode.getParent());}
            else {
                iterateAndExport(ifcNode.children(true));
            }
        }
    }
    //Метод проверяет наличие в фильтре параметрических условий
    public boolean isExistsExprFlrs(KrnObject filterobj){
    	boolean res=false;
        Element xml = getXml(filterobj,"config");
        List<Element> ls=null;
        try {
        	//наличие узлов с функцией  в условии
			ls=XPath.selectNodes(xml,"//Component/compFlr[text()=1]" );
	        if(ls!=null && ls.size()>0){
	        	String txt="";
	        	for(Element els:ls){
	            	//наличие узла с выражением в условии
	        		Element el =(Element)XPath.selectSingleNode(els,"../valFlr/exprFlr[text()]" );
	        		if(el!=null && !"".equals(el.getText())){
	                	res=true;
	                	break;
	        		}
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return res;
    }

    private void exportFilter(FilterNode filterNode,FilterNode parentNode) {
        String path = "C:\\Users\\Администратор\\Desktop\\filterXmls\\"+parentNode.getTitle();
        File dir = new File(path);
        dir.mkdir();
        String title = filterNode.getTitle();
        KrnObject filterobj = filterNode.getKrnObj();

        Element xml = getXml(filterobj,"config");
        xml.detach();
        Document doc = new Document(xml);
        Element filtercls = new Element("KrnClass");
        doc.getRootElement().addContent(filtercls);
        Element uid = new Element("uid");
        uid.setText(filterobj.uid);
        filtercls.addContent(uid);
        Element id = new Element("id");
        id.setText(Long.toString(filterobj.id));
        filtercls.addContent(id);

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
        xmlFilePath =xmlFilePath.replace("\\", "/");
        XMLOutputter xmlOutputer = new XMLOutputter();
        try {
            if(file!=null)
                xmlOutputer.output(doc, file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Kernel krn = Kernel.instance();
        if (src == miCut || src == miRename || src == miDelete) {
    		try {
				if (krn.getBindingModuleToUserMode()) {
					FilterNode selectedNode = (FilterNode) getSelectedNode();
					KrnObject obj = selectedNode.getKrnObj();
					KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
					if (developerObjs.length > 0) {
						long ownerId = developerObjs[0].id;
						long currentUserId = krn.getUserSession().userObj.id;
						if (ownerId != currentUserId) {
							KrnObject userObj = krn.getObjectById(ownerId, 0);
							if (userObj != null) {	// Владелец фильтра существует
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
				    				message.append(" выбранный фильтр! ");
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
    	} else	if (src == miSendToRecycle) {
			try {
				sendToRecycle();
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
    	}
        super.actionPerformed(e);
    }
    
    private void sendToRecycle() throws KrnException {
    	AbstractDesignerTreeNode selectedNode = getSelectedNode();
    	int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Перемещение фильтра '" + selectedNode.toString() + "' в корзину!\nПродолжить?");
        if (res == ButtonsFactory.BUTTON_YES) {
       	 	// Считать все свойства объекта Filter и перезаписать их на объект класса FilterRecycle
        	 Kernel kernel = Kernel.instance();
        	 KrnObject obj = selectedNode.getKrnObj();
        	 KrnClass cls = kernel.getClassByName("FilterRecycle");
        	 KrnObject objRecycle = kernel.createObject(cls, 0);

        	 String[] className = kernel.getStrings(obj, "className", 0, 0);
        	 if (className != null && className.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "className", 0, 0, className[0], 0);
        	 }
        	 
        	 byte[] config = kernel.getBlob(obj, "config", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "config", 0, config, 0, 0);
        	 
        	 long[] dateSelect = kernel.getLongs(obj, "dateSelect", 0);
        	 if (dateSelect != null && dateSelect.length > 0) {
        		 kernel.setLong(objRecycle.id, cls.id, "dateSelect", 0, dateSelect[0], 0);
        	 }

        	 byte[] exprSql = kernel.getBlob(obj, "exprSql", 0, 0, 0);
        	 kernel.setBlob(objRecycle.id, cls.id, "exprSql", 0, exprSql, 0, 0);

        	 KrnObject[] parent = kernel.getObjects(obj, "parent", 0);
        	 if (parent != null && parent.length > 0) {
        		 kernel.setObject(objRecycle.id, cls.id, "parent", 0, parent[0].id, 0, true);
        	 }
        	 
        	 String[] title = kernel.getStrings(obj, "title", 0, 0);
        	 if (title != null && title.length > 0) {
        		 kernel.setString(objRecycle.id, cls.id, "title", 0, 0, title[0], 0);
        	 }
        	 
        	 String eventInitiator = kernel.getUser().getName();
    		 kernel.setString(objRecycle.id, cls.id, "eventInitiator", 0, 0, eventInitiator, 0);
        	 
    		 kernel.setString(objRecycle.id, cls.id, "uid", 0, 0, obj.uid, 0);
    		 
    		 SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy hh:mm");
    		 java.util.Date currentDate = new java.util.Date();
    		 String eventDate = format.format(currentDate);
    		 kernel.setString(objRecycle.id, cls.id, "eventDate", 0, 0, eventDate, 0);

         	
             // Выделение родительского узла
             FilterNode parentNode = (FilterNode) selectedNode.getParent();
             setSelectedNode(parentNode);
    		 
     		// Удаление узла из дерева
             ((FilterTreeModel) getModel()).removeNodeFromParent(selectedNode);
             
        	 KrnObject parentObj = parentNode.getKrnObj();
        	 Collection<Object> values = Collections.singletonList((Object) obj);
        	 kernel.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);	// Удаление из списка детей
        	 kernel.deleteObject(obj, 0);
        	 kernel.writeLogRecord(SystemEvent.EVENT_SEND_FILTER_TO_RECYCLE, selectedNode.toString());
        }
    }
    public void refreashNode(KrnObject obj) {
    	FilterNode node=(FilterNode)find(obj);
    	if(node==null) return;
    	KrnObject krnObj=null;
    	try {
			krnObj=Kernel.instance().getObjectById(obj.id, 0);
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FilterNode parent=(FilterNode)node.getParent();
    	if(krnObj==null && parent !=null) {
 		// Удаление узла из дерева
    		((FilterTreeModel) getModel()).removeNodeFromParent(node);
    	}
    }
}