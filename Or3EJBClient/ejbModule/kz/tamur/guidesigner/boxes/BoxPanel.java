package kz.tamur.guidesigner.boxes;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;

import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.rt.MainFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.util.Funcs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 16:22:02
 * To change this template use File | Settings | File Templates.
 */
public class BoxPanel extends JPanel implements ActionListener, TreeSelectionListener,
        PropertyListener, ChangeListener, PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private List<BoxNode> saveList = new ArrayList<BoxNode>();
    private List<BoxNode> saveItems = new ArrayList<BoxNode>();
    //private EmptyComponent emptyComp = new EmptyComponent();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton createBtn = ButtonsFactory.createToolButton("Create", "Создать");
    private JButton deleteBtn = ButtonsFactory.createToolButton("Trash", "Удалить");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");

    private JSplitPane splitPane = new JSplitPane();
    private JSplitPane splitPaneTransport = new JSplitPane();
    private TransportPane transportPane;
    private BoxTree tree;
    private PropertyInspector inspector = new PropertyInspector(null);

    private DesignerStatusBar statusBar = new DesignerStatusBar();

    private JPopupMenu pMenu = new JPopupMenu();
    private JMenuItem createItem = createMenuItem("Создать");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem deleteItem = createMenuItem("Удалить");
    private BoxNode inode;
    private final Kernel krn = Kernel.instance();
    private int prevIndex = -1;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private JScrollPane sp;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public BoxPanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.BOXES_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.BOXES_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.BOXES_CREATE_RIGHT);
        initToolBar();
        initPopup();
        transportPane=new TransportPane();
        transportPane.tabbed.addChangeListener(this);
        add(splitPane, BorderLayout.CENTER);
        splitPaneTransport.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //add(statusBar, BorderLayout.SOUTH);
        if (tree == null) {
            KrnClass cls = null;
            try {
                cls = krn.getClassByName("BoxRoot");
                KrnObject[] objs = krn.getClassObjects(cls, 0);
                if(objs==null || objs.length==0){
                    KrnObject obj=krn.createObject(cls,0);
                    krn.setString(obj.id,obj.classId,"name",0,0,"Обмен",0);
                }
                KrnObject BoxRoot = krn.getClassObjects(cls, 0)[0];
                long[] ids = {BoxRoot.id};
                String title = krn.getStringValues(ids, cls.id, "name", 0,
                        false, 0)[0].value;
                KrnObject[] bases = krn.getObjects(BoxRoot,"base",0);
                KrnObject base=krn.getUser().getBase();
                inode = new BoxNode(BoxRoot, title,bases.length>0?bases[0]:base, "","", "","", "","","",new byte[0],"",0, 0,0);
                if(inode.getBaseStructureObj()!=null && inode.getBaseStructureObj().id!=base.id){
                    for(int i=0;i<inode.getChildCount();++i){
                        BoxNode node=(BoxNode)inode.getChildAt(i);
                        if(node.getBaseStructureObj()!=null && node.getBaseStructureObj().id!=base.id){
                            inode.remove(i--);
                        }else{
							for(int j=0;j<node.getChildCount();++j){
								BoxNode node_=(BoxNode)node.getChildAt(j);
								if(node_.getBaseStructureObj()!=null && node_.getBaseStructureObj().id!=base.id){
									node.remove(j--);
								}
							}
						}
					}
                }
                tree = new BoxTree(inode);
                tree.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.SINGLE_TREE_SELECTION);
                splitPane.setLeftComponent(sp = new JScrollPane(tree));
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                splitPane.setRightComponent(splitPaneTransport);
                splitPaneTransport.setDividerLocation(300);
                splitPaneTransport.setLeftComponent(inspector);
                splitPaneTransport.setRightComponent(transportPane);
                //inspector.getModel().addPropertyListener(this);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                pMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
//        if (canEdit) {
//            inspector.getModel().addPropertyChangeListener(tree);
//            inspector.getModel().addPropertyChangeListener(this);
//        }
//        inspector.getModel().setCanEdit(canEdit);
        transportPane.fireChangeTabbed();
        tree.setSelectionRow(0);
        
        setOpaque(isOpaque);
        splitPane.setOpaque(isOpaque);
        splitPaneTransport.setOpaque(isOpaque);
        
    }
    
    public void setSelectedRow(KrnObject obj) {
    	BoxNode node = (BoxNode) tree.find(obj);
    	tree.setSelectedNode(node);
    }

    public void placeDividers() {
        splitPaneTransport.setDividerLocation(0.5);
        validate();
    }
    private void initPopup() {
        pMenu.add(createItem);
        createItem.addActionListener(this);
        createItem.setEnabled(canCreate);
        pMenu.addSeparator();
        pMenu.add(saveItem);
        saveItem.addActionListener(this);
        pMenu.addSeparator();
        pMenu.add(deleteItem);
        deleteItem.addActionListener(this);
    }

    private void initToolBar() {
        toolBar.add(createBtn);
        createBtn.addActionListener(this);
        createBtn.setEnabled(canCreate);
        toolBar.add(saveBtn);
        saveBtn.addActionListener(this);
        toolBar.addSeparator();
        toolBar.add(deleteBtn);
        deleteBtn.addActionListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.WEST);
        add(panel, BorderLayout.NORTH);
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public DesignerStatusBar getStatusBar() {
        return statusBar;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == createBtn || src == createItem) {
            create();
        } else if (src == saveItem) {
            save((BoxNode)tree.getSelectedNode());
        } else if (src == deleteBtn || src == deleteItem) {
            deleteSelected();
        } else if (src == saveBtn) {
            saveAll();
        }
    }

    private void create() {
        CreateBoxPanel rp = new CreateBoxPanel();
        DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                "Создание пунктов обмена", rp);
        while(true){
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String BoxName = rp.getText();
                if (BoxName == null || BoxName.trim().equals("")) {
                    JOptionPane.showMessageDialog(this, "Имя элемента не должно быть пустым!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        BoxTree.BoxTreeModel model =
                                (BoxTree.BoxTreeModel)tree.getModel();
                        if (!rp.isFolder()) {
                            if (checkBoxName(BoxName,-1)) {
                                JOptionPane.showMessageDialog(this, "Элемент с именем '"+BoxName+"' уже существует!",
                                        "Сообщение", JOptionPane.ERROR_MESSAGE);
                                continue;
                            } else {
                                model.createChildNode(BoxName);
                            }
                        } else {
                            model.createFolderNode(BoxName);
                        }
                        validate();
                        repaint();
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }
    private boolean checkBoxName(String boxName,long id){
        try{
            KrnClass cls = krn.getClassByName("BoxExchange");
            KrnObject[] boxes = krn.getClassObjects(cls, 0);
            long[] ids = Funcs.makeObjectIdArray(boxes);
            StringValue[] svs = krn.getStringValues(ids, cls.id, "name", 0,
                    false, 0);
            for(int i=0;i<svs.length;++i){
                if(boxName.equals(svs[i].value) && svs[i].objectId!=id)
                return true;
            }
        }catch(KrnException e){
            e.printStackTrace();
            return true;
        }
        return false;

    }
    private void deleteSelected() {
        BoxNode node = (BoxNode)tree.getSelectedNode();
        String mess = "Вы действительно хотите удалить ";
        if (node.isLeaf()) {
            mess = mess + "пункт обмена '" + node.toString() + "'?";
        } else {
            mess = mess + "папку '" + node.toString() + "' и всё её содержимое?";
        }
        int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            tree.deleteNode(node);
        }
    }

    private int getUnsavedCount() {
        saveItems.clear();
        checkSavedItems(inode);
        return saveItems.size();
    }

    private BoxNode checkSavedItems(BoxNode root) {
        if (root.isModified()>0) {
            saveItems.add(root);
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((BoxNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private BoxNode prepareSaveList(BoxNode root) {
        if (root.isModified()>0) {
            saveList.add(root);
            return root;
        } else {
            int childCount = root.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    prepareSaveList((BoxNode)root.getChildAt(i));
                }
            }
        }
        return root;
    }

    private void saveAll() {
        saveList.clear();
        prepareSaveList(inode);
        for (int i = 0; i < saveList.size(); i++) {
            BoxNode node = saveList.get(i);
            save(node);
        }
        saveBtn.setEnabled(false);
        saveItem.setEnabled(false);
    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            saveList.clear();
            prepareSaveList(inode);
            String mess = "Пункты обмена: \n";
            for (int i = 0; i < saveList.size(); i++) {
                BoxNode rn = saveList.get(i);
                mess = mess + "\""+ rn.toString() + "\"\n";
            }
            mess = mess + "были модифицированы! Сохранить изменения?";
            int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                    MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                saveAll();
                return res;
            } else {
                return res;
            }
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    public void valueChanged(TreeSelectionEvent e) {
        BoxNode node = (BoxNode)e.getPath().getLastPathComponent();
        if (node != inode) {
            deleteBtn.setEnabled(canDelete);
            deleteItem.setEnabled(canDelete);
            if (node.isModified()>0) {
                saveItem.setEnabled(canEdit);
                saveBtn.setEnabled(canEdit);
            } else {
                saveItem.setEnabled(false);
            }
        } else {
            deleteBtn.setEnabled(false);
            deleteItem.setEnabled(false);
        }
        inspector.setObject(new BoxNodeItem(node,this));
    }

    public void placeDivider() {
        splitPane.setDividerLocation(0.5);
    }

    public void propertyModified(OrGuiComponent c) {
        //tree.renameProcess(c);
        saveBtn.setEnabled(canEdit);
        saveItem.setEnabled(canEdit);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {

    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==transportPane.tabbed) {
           transportPane.reload();
        }
    }
    
    private void save(BoxNode node) {
        final Kernel krn = Kernel.instance();
        try {
            KrnObject o = node.getKrnObj();
            if (node.isLeaf()) {
                if((node.isModified()& BoxNode.name_)==BoxNode.name_){
                    if (checkBoxName(node.toString(),o.id)) {
                        JOptionPane.showMessageDialog(this, "Элемент с именем '"+node.toString()+"' уже существует!",
                                "Сообщение", JOptionPane.ERROR_MESSAGE);
                        String [] names_=krn.getStrings(o,"name", 0, 0);
                        ((BoxTree.BoxTreeModel)tree.getModel()).rename(node,names_[0]);
                        inspector.setObject(new BoxNodeItem(node,this));
                        node.setModified(-BoxNode.name_);
                    } else if(node.getName().trim().equals("")) {
                        JOptionPane.showMessageDialog(this, "Имя элемента не должно быть пустым!",
                                "Сообщение", JOptionPane.ERROR_MESSAGE);
                        String [] names_=krn.getStrings(o,"name", 0, 0);
                        ((BoxTree.BoxTreeModel)tree.getModel()).rename(node,names_[0]);
                        inspector.setObject(new BoxNodeItem(node,this));
                        node.setModified(-BoxNode.name_);
                    } else {
                        krn.setString(o.id, o.classId, "name", 0, 0, node.toString(), 0);
                    }
                }
                if((node.isModified()& BoxNode.urlIn_)==BoxNode.urlIn_)
                    krn.setString(o.id, o.classId, "urlIn", 0, 0, node.getUrlIn(), 0);
                if((node.isModified()& BoxNode.urlOut_)==BoxNode.urlOut_)
                    krn.setString(o.id, o.classId, "urlOut", 0, 0, node.getUrlOut(), 0);
                if((node.isModified()& BoxNode.pathIn_)==BoxNode.pathIn_)
                    krn.setString(o.id, o.classId, "xpathIn", 0, 0, node.getPathIn(), 0);
                if((node.isModified()& BoxNode.pathOut_)==BoxNode.pathOut_)
                    krn.setString(o.id, o.classId, "xpathOut", 0, 0, node.getPathOut(), 0);
                if((node.isModified()& BoxNode.pathTypeIn_)==BoxNode.pathTypeIn_)
                    krn.setString(o.id, o.classId, "xpathTypeIn", 0, 0, node.getPathTypeIn(), 0);
                if((node.isModified()& BoxNode.pathTypeOut_)==BoxNode.pathTypeOut_)
                    krn.setString(o.id, o.classId, "xpathTypeOut", 0, 0, node.getPathTypeOut(), 0);
                if((node.isModified()& BoxNode.pathInit_)==BoxNode.pathInit_)
                    krn.setString(o.id, o.classId, "xpathIdInit", 0, 0, node.getPathInit(), 0);
                if((node.isModified()& BoxNode.charSet_)==BoxNode.charSet_)
                    krn.setString(o.id, o.classId, "charSet", 0, 0, node.getCharSet(), 0);
                if((node.isModified()& BoxNode.config_)==BoxNode.config_){
                    krn.setBlob(o.id, o.classId, "config", 0, node.getConfig(), 0,0);
                }
                if((node.isModified()& BoxNode.transport_)==BoxNode.transport_)
                    krn.setLong(o.id, o.classId, "transport", 0, node.getTransportInt(), 0);
                if((node.isModified()& BoxNode.typeMsg_)==BoxNode.typeMsg_)
                    krn.setLong(o.id, o.classId, "typeMsg", 0, node.getTypeMsg(), 0);
            }
            if((node.isModified()& BoxNode.base_)==BoxNode.base_){
                if(node.getBaseStructureObj()==null)
                   krn.deleteValue(o.id, o.classId, "base", new int[]{0}, 0);
                else
                   krn.setObject(o.id, o.classId, "base", 0,node.getBaseStructureObj().id, 0, false);
            }
            node.setModified(0);
            krn.reloadBox(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tree.repaint();
        saveItem.setEnabled(false);
        if (getUnsavedCount() > 0) {
            saveBtn.setEnabled(canEdit);
        } else {
            saveBtn.setEnabled(false);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        saveBtn.setEnabled(canEdit);
    }
    public void setModified(BoxNode node){
        ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
        saveItem.setEnabled(canEdit);
        saveBtn.setEnabled(canEdit);
    }

}
