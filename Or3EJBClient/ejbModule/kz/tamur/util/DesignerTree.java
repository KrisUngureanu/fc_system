package kz.tamur.util;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Factories;
import kz.tamur.comps.Mode;
import kz.tamur.comps.PropertyValue;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.guidesigner.KrnObjectPattern;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.NodeFinder;
import kz.tamur.guidesigner.UIDPattern;
import kz.tamur.guidesigner.boxes.BoxNode;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.filters.OrFilterNode;
import kz.tamur.guidesigner.filters.OrFilterTree;
import kz.tamur.guidesigner.noteeditor.NoteNode;
import kz.tamur.guidesigner.noteeditor.NotePageNode;
import kz.tamur.guidesigner.reports.ReportNode;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.UserNode;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.Utils;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.util.CursorToolkit;
/**
 * User: vital
 * Date: 19.01.2005
 * Time: 15:12:53
 */
public abstract class DesignerTree extends JTree implements 
		DropTargetListener, DragSourceListener, DragGestureListener,
        MouseListener, ActionListener, KeyListener {
    protected DropTarget dropTarget = null;
    protected DragSource dragSource = null;
    protected boolean isDragStarted;
    protected double lastLocation;
    protected DesignerTreeModel model;
    protected NodeFinder finder = new NodeFinder();

    protected AbstractDesignerTreeNode root;
    protected AbstractDesignerTreeNode copyNode = null;

    private boolean isShowPopupEnabled = true;

    protected JPopupMenu pm = new JPopupMenu();
    protected JMenuItem miCreateFolder = createMenuItem("Создать папку", "CloseFolder");
    protected JMenuItem miCreateElement = createMenuItem("Создать элемент", "Create");
    protected JMenuItem miCopy =  createMenuItem("Копировать", "Copy");
    protected JMenuItem miCut =  createMenuItem("Вырезать", "Cut");
    protected JMenuItem miPaste =  createMenuItem("Вставить", "Paste");
    protected JMenuItem miDelete =  createMenuItem("Удалить");
    protected JMenuItem miSendToRecycle = createMenuItem("Отправить в корзину...");
    protected JMenuItem miRename =  createMenuItem("Переименовать", "Rename");
    protected JMenuItem miFind =  createMenuItem("Найти");
    protected JMenuItem miFindNext =  createMenuItem("Найти далее...");
    protected JMenuItem miImport = createMenuItem("Импортировать");
    protected JMenuItem miExport = createMenuItem("Экспортировать");
    protected JMenuItem miEdit = createMenuItem("Редактировать");
    protected JMenuItem miView = createMenuItem("Посмотреть");
    
    protected JMenuItem miCreateWS = createMenuItem("Создать web-сервис", "Create");
    protected JMenuItem miGenerate = createMenuItem("Сгенерировать пользовательские классы");
    
    private JMenuItem canMoveCheck = createMenuItem("Разрешить перемещение узлов", "MoveNodeDisableIcon.png");
    private boolean isShowMoveCheck = false;
    private boolean isSelectedMoveCheck = false;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean canMove = false;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    protected DesignerTree(TreeNode root) {
        super(root);
        if (root instanceof InterfaceNode || root instanceof ServiceNode || root instanceof FilterNode || root instanceof ReportNode) {
        	isShowMoveCheck = true;
        }
        _init(Kernel.instance().getUser().isDeveloper());
    }

    protected DesignerTree(TreeNode root, boolean enableDragAndDrop) {
        super(root);
        if (root instanceof InterfaceNode || root instanceof ServiceNode || root instanceof FilterNode || root instanceof ReportNode) {
        	isShowMoveCheck = true;
        }
        _init(enableDragAndDrop);
    }

    private void _init(boolean enableDragAndDrop) {
        setOpaque(isOpaque);
        setCellRenderer(new CellRenderer());
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.INTERFACE_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.INTERFACE_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.INTERFACE_CREATE_RIGHT);

        if (isShowMoveCheck ? true : (enableDragAndDrop && canMove)) {
            dropTarget = new DropTarget(this, this);
            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
        }
        
        initPopup();
        addMouseListener(this);
        addKeyListener(this);
    }

    protected abstract void defaultDeleteOperations() throws KrnException;

    protected void initPopup() {
        pm.add(miCreateFolder);
        miCreateFolder.addActionListener(this);
        pm.add(miCreateElement);
        miCreateElement.addActionListener(this);
        pm.addSeparator();
        pm.add(miEdit);
        miEdit.addActionListener(this);
        pm.add(miView);
        miView.addActionListener(this);
        pm.add(miCopy);
        miCopy.addActionListener(this);
        pm.add(miCut);
        miCut.addActionListener(this);
        pm.add(miPaste);
        miPaste.addActionListener(this);
        pm.addSeparator();
        pm.add(miDelete);
        pm.add(miSendToRecycle);
        miSendToRecycle.setVisible(false);
        pm.addSeparator();
        pm.add(miFind);
     //   pm.add(miImport);
        miImport.addActionListener(this);
      //  pm.add(miExport);
        miExport.addActionListener(this);
        miFind.addActionListener(this);
        miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        pm.add(miFindNext);
        miFindNext.addActionListener(this);
        miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        pm.add(miRename);
        miRename.addActionListener(this);
        miDelete.addActionListener(this);
        miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        if (isShowMoveCheck) {
	        pm.add(canMoveCheck);
	        canMoveCheck.addActionListener(this);
        }
    }
    
    public NodeFinder getFinder() {
    	return finder;
    }

    protected abstract void find();

    public AbstractDesignerTreeNode find(KrnObject obj) {
    	if (obj != null)
    		return (AbstractDesignerTreeNode) searchByUID(obj.uid);
        return null;
    };
    
    public TreeNode searchById(long id) {
    	KrnObject obj = null;
    	try {
    		obj = Kernel.instance().getObjectById(id, 0);
    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
    	
    	if(obj != null) {
    		return searchByUID(obj.uid);
    	}    	
    	return null;
    }
    
    public TreeNode searchByUID(String uid) {
        // Найденный узел в дереве - возвращаем в конце функции
        TreeNode result = TreeUIDMap.get(uid);

        // Если данный узел еще ни разу не был считан из базы, то ищем в базе
        if (result == null) {
	        Kernel krn = Kernel.instance();
	
	        List<String> parentUids = null;

			try {
		        // Находим объект в базе по UID
				KrnObject obj = krn.getObjectByUid(uid, 0);
				
				if(obj != null) {
					
					boolean isBaseSearch = this instanceof kz.tamur.guidesigner.InterfaceTree 
							|| this instanceof kz.tamur.guidesigner.filters.FiltersTree 
							|| this instanceof kz.tamur.guidesigner.service.ServicesTree
							|| this instanceof kz.tamur.guidesigner.reports.ReportTree
							|| this instanceof kz.tamur.guidesigner.users.UserTree
							|| this instanceof kz.tamur.guidesigner.boxes.BoxTree;
					
			        // Класс найденного объекта
			        KrnClass cls = krn.getClassById(obj.classId);
					
					// Если объект является стандартным метаданным
			        if (
							((cls.id == Kernel.SC_UI.id || cls.id == Kernel.SC_UI_FOLDER.id) && this instanceof kz.tamur.guidesigner.InterfaceTree) 
							|| ((cls.id == Kernel.SC_FILTER.id || cls.id == Kernel.SC_FILTER_FOLDER.id)	&& this instanceof kz.tamur.guidesigner.filters.FiltersTree)
							|| ((cls.id == Kernel.SC_PROCESS_DEF.id || cls.id == Kernel.SC_PROCESS_DEF_FOLDER.id) && this instanceof kz.tamur.guidesigner.service.ServicesTree)
							|| ((cls.id == Kernel.SC_REPORT_PRINTER.id || cls.id == Kernel.SC_REPORT_FOLDER.id)	&& this instanceof kz.tamur.guidesigner.reports.ReportTree)
							|| ((cls.id == Kernel.SC_USER.id || cls.id == Kernel.SC_USER_FOLDER.id)	&& this instanceof kz.tamur.guidesigner.users.UserTree)
							|| ((cls.id == Kernel.SC_BOX_EXCHANGE.id || cls.id == Kernel.SC_BOX_FOLDER.id) && this instanceof kz.tamur.guidesigner.boxes.BoxTree)) {
						
						KrnAttribute parentAttr = krn.getAttributeByName(obj.classId, "parent");
						parentUids = new ArrayList<>();
						
						KrnObject tmpObj = obj;
						while (tmpObj != null) {
							KrnObject[] parents = krn.getObjects(tmpObj, parentAttr, 0);
							
							if (parents != null && parents.length > 0) {
								tmpObj = parents[0];
								parentUids.add(tmpObj.uid);
							} else 
								tmpObj = null;
						}
						
						// Пробегаемся по родителям от старшего к младшему и считываем детей для каждого
						for (int i = parentUids.size() -1; i >= 0; i--) {
							result = TreeUIDMap.get(parentUids.get(i));
							if(i == parentUids.size() - 1 && result == null) {
								KrnObject rootObj = krn.getObjectByUid(parentUids.get(i), 0);
								result = finder.findFirst((AbstractDesignerTreeNode)model.getRoot(), new KrnObjectPattern(rootObj));
								TreeUIDMap.put(rootObj.uid, (AbstractDesignerTreeNode)result);
							}
							if (result != null) {
								((AbstractDesignerTreeNode)result).load();
							}
						}
						result = TreeUIDMap.get(uid);
						if(result == null) {
							result = TreeUIDMap.get(parentUids.get(0));
							List<DesignerTreeNode> childList = ((AbstractDesignerTreeNode)result).children(true);
							for(DesignerTreeNode node: childList) {
								if(node.getKrnObj().uid.equals(uid)) {
									TreeUIDMap.put(uid, (AbstractDesignerTreeNode)node);
								}
							}
							result = TreeUIDMap.get(uid);
					
						}
					} else if (!isBaseSearch && root != null) {
						// Если объект не стандартное метаданное
						result = finder.findFirst(root, new UIDPattern(uid));
					}
				}
				
			} catch (KrnException e) {
				e.printStackTrace();
			}
        } else {
        	// Если тип найденной ноды не соответсвует типу дерева
        	if ((result instanceof ServiceNode && ! (this instanceof kz.tamur.guidesigner.service.ServicesTree))
        			|| (result instanceof InterfaceNode && !(this instanceof kz.tamur.guidesigner.InterfaceTree))
					|| (result instanceof FilterNode && !(this instanceof kz.tamur.guidesigner.filters.FiltersTree))
					|| (result instanceof ReportNode && !(this instanceof kz.tamur.guidesigner.reports.ReportTree))
					|| (result instanceof UserNode && !(this instanceof kz.tamur.guidesigner.users.UserTree))
					|| (result instanceof BoxNode && !(this instanceof kz.tamur.guidesigner.boxes.BoxTree))
        			)
        		
        		result = null;
        }
        return result;
    }

    public DesignerTreeNode[] getSelectedNodes() {
        ArrayList<DesignerTreeNode> list = new ArrayList<DesignerTreeNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            DesignerTreeNode node = (DesignerTreeNode)path.getLastPathComponent();
            list.add(node);
        }
        DesignerTreeNode[] res = new DesignerTreeNode[list.size()];
        list.toArray(res);
        return res;
    }
    public DesignerTreeNode[] getSelectedNodes(boolean includeNonLeafNodes)
    {
        return null;
    }
    public AbstractDesignerTreeNode getSelectedNode() {
        TreePath path = getSelectionPath();
        return path == null ? null : (AbstractDesignerTreeNode) path.getLastPathComponent();
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        lastLocation = dtde.getLocation().getY();
        isDragStarted = false;
        Point loc = dtde.getLocation();
        int idx = getRowForLocation(loc.x, loc.y);
        if (idx != -1) {
            AbstractDesignerTreeCellRenderer cr = (AbstractDesignerTreeCellRenderer) getCellRenderer();
            if (cr.setDragRow(idx))
                repaint();
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    	
    }

    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                KrnObject krnObj = (KrnObject) transferable.getTransferData(DataFlavor.stringFlavor);
                AbstractDesignerTreeNode s = (AbstractDesignerTreeNode) root.find(krnObj).getLastPathComponent();
                String mes = (s.isLeaf()) ? "Переместить элемент '" : "Переместить папку '";
                int res = MessagesFactory.showMessageDialog(this.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                        mes + s.toString() + "' ?");
                if (res == ButtonsFactory.BUTTON_YES) {
                    dtde.getDropTargetContext().dropComplete(addElement(s, dtde.getLocation()));
            		canMoveCheck.setIcon(kz.tamur.rt.Utils.getImageIconExt("MoveNodeDisableIcon", ".png"));
            		isSelectedMoveCheck = false;
                }
                AbstractDesignerTreeCellRenderer cr = (AbstractDesignerTreeCellRenderer) getCellRenderer();
                cr.setDragRow(-1);
                repaint();
            } else {
                dtde.rejectDrop();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Exception: " + exception.getMessage());
            dtde.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            System.err.println("Exception: " + ufException.getMessage());
            dtde.rejectDrop();
        }
    }

    public void dragExit(DropTargetEvent dte) {
        isDragStarted = true;
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    	
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    public void dragDropEnd(DragSourceDropEvent dsde) {

    }

    public void dragExit(DragSourceEvent dse) {

    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        DesignerTreeNode node = getSelectedNode();
        if (node != null) {
        	if (isShowMoveCheck ? isSelectedMoveCheck : true) {
	            TransferableObject selected = new TransferableObject(node.getKrnObj());
	            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, selected, this);
        	}
        }
    }

    public class TransferableObject implements Transferable {
        private static final int STRING = 0;
        private final DataFlavor[] flavors = {DataFlavor.stringFlavor};
        private KrnObject transfer;

        public TransferableObject(KrnObject transfer) {
            this.transfer = transfer;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return (DataFlavor[]) flavors.clone();
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) return true;
            }
            return false;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (flavor.equals(flavors[STRING]))
                return transfer;
            else
                throw new UnsupportedFlavorException(flavor);
        }
    }

    public boolean addElement(Object s, Point location) {
        try {
            AbstractDesignerTreeNode firstNode = (AbstractDesignerTreeNode) s;
            AbstractDesignerTreeNode secondNode = null;

            TreePath tp = getPathForLocation(location.x, location.y);
            if (tp != null) {
                secondNode = (AbstractDesignerTreeNode) tp.getLastPathComponent();
            }
            AbstractDesignerTreeNode tempNode = secondNode;
            while (tempNode != null) {
                if (tempNode.equals(firstNode)) {
                    System.out.println("Нельзя переместить узел внутрь самого себя");
                    return false;
                }
                tempNode = (AbstractDesignerTreeNode) tempNode.getParent();
            }
            if (firstNode != null && secondNode != null) {
                AbstractDesignerTreeNode parent = null;
                if (!secondNode.isLeaf()) {
                    parent = secondNode;
                } else {
                    parent = (AbstractDesignerTreeNode)secondNode.getParent();
                }
                if (parent == null) {
                    System.out.println("Нельзя вынести узел на один уровень с корневым узлом");
                    return false;
                }

                if (root != null) {
                    model.deleteNode(firstNode, true);
                    model.addNode(firstNode, parent, true);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void treeStartSelection(AbstractDesignerTreeNode node) {
        TreePath path = new TreePath(node);
        setSelectionPath(path);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && getSelectedNode()!=null && getSelectedNode().isLeaf()) {
            Container c = getTopLevelAncestor();
            if (c instanceof DesignerDialog &&
                    ((DesignerDialog)c).getRootPane().getDefaultButton() != null) {
                ((DesignerDialog)c).getRootPane().getDefaultButton().doClick();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
    	//On Linux!!!
/*        if (e.isPopupTrigger()) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                setSelectionPath(path);
            }
            if (isShowPopupEnabled()) {
                showPopup(e);
            }
        }*/
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null) {
               // setSelectionPath(path);
                if (isSelectionEmpty()) {
                    setSelectionPath(path);
                }
            }
            if (isShowPopupEnabled()) {
                showPopup(e);
            }
        }
    }

    protected void showPopup(MouseEvent e) {
if(getSelectedNodes().length>1)
{
    MenuElement[] elements=pm.getSubElements();
    JMenuItem item = null;
    for(MenuElement element : elements)
    {
        Component c = element.getComponent();
        if(c instanceof JMenuItem)
        item = (JMenuItem)c;
        if(!(item == miExport))
        {
          item.setEnabled(false);
        }
    }
}
else {
        if (getSelectedNode().isLeaf()) {
        	if(getSelectedNode() instanceof FilterNode) {
        		miView.setEnabled(true);
        	} else miView.setEnabled(false);
        	miEdit.setEnabled(true);
            miCreateFolder.setEnabled(false);
            miCreateElement.setEnabled(false);
            miCopy.setEnabled(canCreate);
            miCut.setEnabled(canCreate);
            if((getSelectedNode() instanceof NotePageNode) || (getSelectedNode() instanceof NoteNode)){
            	miPaste.setEnabled(true);
            }else
            	miPaste.setEnabled(false);
        } else {
        	miView.setEnabled(false);
        	miEdit.setEnabled(false);
            miImport.setEnabled(true);
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
        miRename.setEnabled(canEdit);}
        pm.show(e.getComponent(), e.getX(), e.getY());
    }


    public boolean isShowPopupEnabled() {
        return isShowPopupEnabled;
    }

    public void setShowPopupEnabled(boolean showPopupEnabled) {
        isShowPopupEnabled = showPopupEnabled;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == miDelete || src == miRename) {
            try {
                AbstractDesignerTreeNode node = getSelectedNode();
                if (node != null && !(node instanceof NotePageNode)) {
                    UserSessionValue us = Kernel.instance().getObjectBlocker(node.getKrnObj().id);
                    if (us != null) {
                        MessagesFactory.showMessageDialog(getTopLevelAncestor(),
                                MessagesFactory.ERROR_MESSAGE,
                                "Объект '" + node.toString() + "' заблокирован!\n" +
                                "Пользователь: " + us.name + "\n" +
                                "IP адрес: " + us.ip + "\n" +
                                "Имя компьютера: " + us.pcName);
                        return;
                    }
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
        
        if (src == miEdit) {
        	editMethod();
        }

        if (src == miView) {
        	final Container cont = getTopLevelAncestor();
        	AbstractDesignerTreeNode fnode = getSelectedNode();
        	KrnObject obj = fnode.getKrnObj();    	
        	if (obj != null) {
        		openFilter(obj);
        	}
        }
        
        if (src == miCreateFolder) {
            createFolder();
        } else if (src == miCreateElement || src == miCreateWS) {
            createElement();
        } else if (src == miCopy) {
            copyElement();
        } else if (src == miPaste) {
            pasteElement();
        } else if (src == miCut) {
            cutElement();
        } else if (src == miRename) {
            model.renameNode();
        } else if (src == miDelete) {
            keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED, KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_DELETE, KeyEvent.CHAR_UNDEFINED));
        } else if (src == miFind) {
            find();
        } else if (src == miFindNext) {
            keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED,
                    KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_F3,
                    KeyEvent.CHAR_UNDEFINED));
        } else if (src == canMoveCheck) {
        	if (isSelectedMoveCheck) {
        		canMoveCheck.setIcon(kz.tamur.rt.Utils.getImageIconExt("MoveNodeDisableIcon", ".png"));
        		isSelectedMoveCheck = false;
        	} else {
        		canMoveCheck.setIcon(kz.tamur.rt.Utils.getImageIconExt("MoveNodeEnableIcon", ".png"));
        		isSelectedMoveCheck = true;
        	}
        }
        else if(src == miExport)
        {   String directory = "C:\\Users\\Администратор\\Desktop\\filterXmls\\";
           iterateAndExport(Arrays.asList(getSelectedNodes(true)), directory);
        }
        else if(src == miImport)
        {
            chooseFilesFromLocalDir();
        }
    }
    public void iterateAndExport(List<DesignerTreeNode> list, String directory)
    {
    }
 
    public void chooseFilesFromLocalDir()
    {}
    protected void copyElement() {
        copyNode = getSelectedNode();
        copyNode.setCopyProcessStarted(true);
        setCursor(Constants.HAND_CURSOR);
    }

    protected void cutElement() {
        copyNode = getSelectedNode();
        copyNode.setCopyProcessStarted(true);
        copyNode.setCutProcess(true);
        setCursor(Constants.HAND_CURSOR);
    }

    protected abstract void pasteElement();
    
    private org.jdom.Element getXml(KrnObject obj) {
        Kernel krn = Kernel.instance();
        org.jdom.Element xml = null;
        byte[] data = null;
        try {
            data = krn.getBlob(obj, "config", 0, 0, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (data.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            SAXBuilder b = new SAXBuilder();
            try {
                xml = b.build(is).getRootElement();
                is.close();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return xml;
    }
    
    public void distantOpenFilter(final KrnObject obj) {
    	openFilter(obj);
    }
    
    private void openFilter(final KrnObject obj) {
    	FiltersTree tree = kz.tamur.comps.Utils.getFiltersTree(obj);
    	final Container cont = getTopLevelAncestor();
    	JPanel filterPane = new JPanel();
    	final DesignerDialog dlg = new DesignerDialog(cont instanceof JFrame ? (JFrame) cont : (Dialog) cont, "", filterPane);
    	FilterNode filter = (FilterNode) tree.find(obj);
		JScrollPane sp = null;
		final Kernel krn = Kernel.instance();
		OrFilterNode node;
	    try {
	    	byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
	        if (data.length > 0) {
		    	org.jdom.Element xml = getXml(obj);   
		        node = (OrFilterNode) Factories.instance().create(xml, Mode.PREVIEW, filter);
		    } else {
		        node = (OrFilterNode) Factories.instance().create("FilterNode", filter);
		        node.getXml().getChild("title").setText(filter.toString());
		    }
		    KrnObject lang = krn.getInterfaceLanguage();
		    long langId = (lang != null) ? lang.id : 0;
		    node.setLangId(langId);
		    node.setPropertyValue(new PropertyValue(node.getXml().getChild("title"), node.getProperties().getChild("title"), langId));
		    OrFilterTree ftree = new OrFilterTree(node, obj, null);
		    ftree.setEditable(false);
		    sp = new JScrollPane(ftree);
		    validate();
		    repaint();
		    // Сохранение в историю
		    HistoryWithDate hwd = new HistoryWithDate(obj, new Date());
		    krn.getUser().addFltInHistory(hwd,filter.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
		JButton moveToMainBtn = ButtonsFactory.createToolButton("MoveToEditor.png", "Переместить в редактор");
		moveToMainBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Or3Frame.instance().jumpFilter(obj);
				// close view dialog
				dlg.dispose();
				// get expression editor
				Window owner = ((DesignerDialog) cont).getOwner();
				// close if dialog, don't close if main frame 
				if (cont instanceof DesignerDialog) {
					cont.setVisible(false);
				}
				if (owner instanceof DesignerDialog) {
					owner.setVisible(false); 
				}
			}
		});

		toolBar.add(moveToMainBtn);
		filterPane.setLayout(new BorderLayout());
	    filterPane.add(toolBar, BorderLayout.NORTH);
		filterPane.add(sp, BorderLayout.CENTER);
		dlg.setTitle(filter.toString());
		dlg.add(filterPane);
		Dimension dim = kz.tamur.comps.Utils.getMaxWindowSizeActDisplay();
		dlg.setSize(new Dimension(dim.width - 500, dim.height - 500));
		dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
		dlg.show();
	}
    
    protected void editMethod() {
    	final Container cont = getTopLevelAncestor();
    	AbstractDesignerTreeNode fnode = getSelectedNode();
    	KrnObject obj = fnode.getKrnObj();    	
    	if (obj != null) {
    		if(fnode instanceof FilterNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
//	    	    if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
//	    	        Or3Frame.instance().getDesignerFrame().load(obj,null);
//	    	    }else {
//	    	    	Or3Frame.instance().jumpFilter(obj); 
//	    	    }
    	    	Or3Frame.instance().jumpFilter(obj);
	    	    cont.setVisible(false);
    		} else if (fnode instanceof InterfaceNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
    			Or3Frame.instance().jumpInterface(obj);
	    	    cont.setVisible(false);
    		} else if (fnode instanceof ServiceNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
    	    	Or3Frame.instance().jumpService(obj);
	    	    cont.setVisible(false);
    		} else if (fnode instanceof ReportNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
    	    	Or3Frame.instance().jumpReport(obj);  
	    	    cont.setVisible(false);
    		} else if (fnode instanceof UserNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
    	    	Or3Frame.instance().jumpUser(obj);   
	    	    cont.setVisible(false);
    		} else if (fnode instanceof BoxNode) {
    			Window owner = ((DesignerDialog) cont).getOwner();
    	    	if(owner!=null) {	    	    		
    	    		if(owner instanceof DesignerDialog) {
    	    			owner.dispose();
    	    		}
    	    	}
    	    	Or3Frame.instance().jumpBox(obj);  
	    	    cont.setVisible(false);
    		}
    		
    		
    		
    	}
//    	if (fnode!=null && fnode.isLeaf()) {
//            FiltersPanel flrPanel = new FiltersPanel(true);
//            flrPanel.load(fnode,null);
//            DesignerDialog dlgEdit = new DesignerDialog(
//                    (Dialog)me.getTopLevelAncestor(),
//                        "Корректировка фильтра - " + fnode.toString(),
//                    flrPanel, false, false, false);
//            dlgEdit.setOnlyOkButton();
//            Insets in = Toolkit.getDefaultToolkit().getScreenInsets(
//                    me.getGraphicsConfiguration());
//            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
//            dlgEdit.setLocation(in.left, in.top);
//            dlgEdit.setSize(ss.width - in.right, ss.height - in.bottom);
//            dlgEdit.show();
//            flrPanel.processExit();
//            filtersTree.renameFilter(fnode, fnode.toString());
//        }
    }

    protected void createFolder() {
        AbstractDesignerTreeNode node = getSelectedNode();
        if (!node.isLeaf()) {
            CreateElementPanel p = null;
            if (this instanceof ServicesTree) {
                p = new CreateElementPanel(
                        CreateElementPanel.CREATE_SERVICE_FOLDER_TYPE, "");
            } else {
                p = new CreateElementPanel(
                        CreateElementPanel.CREATE_FOLDER_TYPE, "");
            }
            DesignerDialog dlg = null;
            if (getTopLevelAncestor() instanceof JDialog) {
                dlg = new DesignerDialog(
                        (JDialog)getTopLevelAncestor(), "Создание папки", p);
            } else {
                dlg = new DesignerDialog(
                        (Frame)getTopLevelAncestor(), "Создание папки", p);
            }
            dlg.show();
            if (dlg.isOK()) {
                try {
                    if (!(this instanceof ServicesTree)) {
                        model.createFolderNode(p.getElementName());
                    } else {
                        ((ServicesTree)this).createServiceFolder(p.getElementName(),
                                p.isServiceTab(), p.getServiceTabName());
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected void createElement() {
        AbstractDesignerTreeNode node = getSelectedNode();
        if (!node.isLeaf()) {
            CreateElementPanel p = new CreateElementPanel(
                    CreateElementPanel.CREATE_ELEMENT_TYPE, "");
            DesignerDialog dlg = null;
            if (getTopLevelAncestor() instanceof JDialog) {
                dlg = new DesignerDialog(
                        (JDialog)getTopLevelAncestor(), "Создание элемента", p);
            } else {
                dlg = new DesignerDialog(
                        (Frame)getTopLevelAncestor(), "Создание элемента", p);
            }
            dlg.show();
            if (dlg.isOK()) {
                try {
                	AbstractDesignerTreeNode newNode = model.createChildNode(p.getElementName());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            AbstractDesignerTreeNode node = getSelectedNode();
            try {
                if (node != null && !(node instanceof NotePageNode)) {
	                UserSessionValue us = Kernel.instance().getObjectBlocker(node.getKrnObj().id);
	                if (us != null) {
	                    MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Объект '" + node.toString() + "' заблокирован!\n" + "Пользователь: " + us.name + "\n" + "IP адрес: " + us.ip + "\n" + "Имя компьютера: " + us.pcName);
	                    return;
	                }
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
            
            if (node != null && !node.equals(model.getRoot())) {
                try {
                    String mes = "";
                    if (node.isLeaf()) {
                        mes = "Удаление элемента '" + node.toString() + "'!\nПродолжить?";
                    } else {
                        mes = "Удалить папку '" + node.toString() + "' и всё её содержимое?";
                    }
                    int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mes);
                    if (res == ButtonsFactory.BUTTON_YES) {
                        defaultDeleteOperations();
                        model.deleteNode(node, false);
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
            find();
        } else if (e.getKeyCode() == KeyEvent.VK_F3) {
            Thread t = new Thread(new Runnable() {
                public void run() {
            		Kernel.instance();
                    TreeNode fnode = finder.findNext();
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
                    }
                }
            });
            t.start();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (copyNode != null) {
                CursorToolkit.stopWaitCursor(this);
                copyNode.setCopyProcessStarted(false);
                repaint();
                copyNode = null;
            }
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }
    
    /**
     * Возвращает корневой узел дерева. 
     */
    public AbstractDesignerTreeNode getRoot() {
        return (AbstractDesignerTreeNode) getModel().getRoot();
    }
    
    protected class CellRenderer extends AbstractDesignerTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            l.setIcon(leaf ? null : kz.tamur.rt.Utils.getImageIcon(expanded ? "Open" : "CloseFolder"));
            l.setOpaque(selected || isOpaque);
            return l;
        }
    }

    /**
     * @return the miFind
     */
    public JMenuItem getMiFind() {
        return miFind;
    }
    
    public void setSelectedNode(long objId) {
        KrnObject obj = null;
        try {
            obj = Utils.getObjectById(objId, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (obj != null) {
            setSelectedNode(obj);
        }
    }

    public void setSelectedNode(KrnObject obj) {
        AbstractDesignerTreeNode selNode = getSelectedNode();
        if (selNode == null || !obj.equals(selNode.getKrnObj())) {
        	AbstractDesignerTreeNode node = (AbstractDesignerTreeNode) searchByUID(obj.uid);
	        if (node != null) {
	            TreePath tpath = new TreePath(node.getPath());
	            setSelectionPath(tpath);
	            scrollPathToVisible(tpath);
	        }
        }
    }
    
    public void setSelectedNode(AbstractDesignerTreeNode node) {
        if (node != null) {
            TreePath tpath = new TreePath(node.getPath());
            setSelectionPath(tpath);
            scrollPathToVisible(tpath);
        }
    }
    
    public Element getXml(KrnObject obj,String attribute)
    {
    Element xml = null;
        Kernel krn = Kernel.instance();
        byte[] data = null;
        try {
            data = krn.getBlob(obj, attribute, 0, 0, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (data.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            SAXBuilder b = new SAXBuilder();
            try {
                xml = b.build(is).getRootElement();
                is.close();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
            }
        }
        return xml;
}
    
}
