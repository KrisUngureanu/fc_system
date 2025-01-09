package kz.tamur.rt.login;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.*;
import kz.tamur.util.*;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.IOException;

import com.cifs.or2.kernel.KrnException;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getImageIcon;

public class ServerTree extends JTree implements  DropTargetListener, DragSourceListener, DragGestureListener,
        MouseListener, ActionListener, KeyListener {


    protected DesignerTreeModel model;
    protected NodeFinder finder = new NodeFinder(false);

    protected AbstractDesignerTreeNode root;
    protected AbstractDesignerTreeNode copyNode = null;
    protected DropTarget dropTarget = null;
    protected DragSource dragSource = null;
    protected boolean isDragStarted;
    protected double lastLocation;

    private boolean isShowPopupEnabled = true;

    protected JPopupMenu pm = new JPopupMenu();
    protected JMenuItem miCreateFolder =
            createMenuItem("Создать папку", "CloseFolder");
    protected JMenuItem miCreateElement =
             createMenuItem("Создать элемент", "Create");
    protected JMenuItem miCopy =  createMenuItem("Копировать", "Copy");
    protected JMenuItem miCut =  createMenuItem("Вырезать", "Cut");
    protected JMenuItem miPaste =  createMenuItem("Вставить", "Paste");
    protected JMenuItem miDelete =  createMenuItem("Удалить");
    protected JMenuItem miRename =  createMenuItem("Переименовать", "Rename");
    protected JMenuItem miFind =  createMenuItem("Найти");
    protected JMenuItem miFindNext =  createMenuItem("Найти далее...");

    private String lang;
    public ServerTree(ServerNode root,String lang) {
        super(root);
        this.lang=lang;
        this.root = root;
        model = new ServerTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_MOVE, this);
        initPopup();
        addMouseListener(this);
        addKeyListener(this);
    }


    public AbstractDesignerTreeNode getSelectedNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        } else {
            return (AbstractDesignerTreeNode)path.getLastPathComponent();
        }
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((JDialog) getTopLevelAncestor(), "Поиск", sip);
        dlg.show();
        if (dlg.isOK()) {
            final String searchString = sip.getSearchText();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString));
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        setSelectionPath(path);
                        scrollPathToVisible(path);
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor(),lang);
                    }
                }
            });
            t.start();

        }
    }

    private void initPopup() {
        pm.add(miCreateFolder);
        miCreateFolder.addActionListener(this);
        pm.add(miCreateElement);
        miCreateElement.addActionListener(this);
        pm.addSeparator();
        pm.add(miCopy);
        miCopy.addActionListener(this);
        pm.add(miCut);
        miCut.addActionListener(this);
        pm.add(miPaste);
        miPaste.addActionListener(this);
        pm.addSeparator();
        pm.add(miDelete);
        pm.addSeparator();
        pm.add(miFind);
        miFind.addActionListener(this);
        miFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                KeyEvent.CTRL_DOWN_MASK));
        pm.add(miFindNext);
        miFindNext.addActionListener(this);
        miFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        pm.add(miRename);
        miRename.addActionListener(this);
        miDelete.addActionListener(this);
        miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    }
 
    public class ServerTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public ServerTreeModel(TreeNode root) {
            super(root);
        }

        public void renameNode() {
            String oldName = getSelectedNode().toString();
            CreateElementPanel cp = new CreateElementPanel(
                    CreateElementPanel.RENAME_TYPE,oldName,lang);
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Переименование", cp);
            dlg.show();
            int res = dlg.getResult();
            if (res == ButtonsFactory.BUTTON_OK) {
                    oldName = getSelectedNode().toString();
                    ServerNode source =
                            (ServerNode)
                            ((ServerNode)ServerTree.this.root).find(oldName).getLastPathComponent();
                    source.rename(cp.getElementName());
                    TreeNode[] tp = getPathToRoot(source);
                    fireTreeNodesChanged(this, tp, null, null);
            }
        }

        public AbstractDesignerTreeNode createFolderNode(String title) {
            AbstractDesignerTreeNode selNode = getSelectedNode();
            Element e=new Element("node");
            e.setAttribute("name",title);
            e.setAttribute("isLeaf","false");
            ServerNode node = new ServerNode(e);
            insertNodeInto(node, selNode, selNode.getChildCount());
            ((ServerNode)selNode).getXml().addContent(e);
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title){
            AbstractDesignerTreeNode selNode = (ServerNode)root;
            AbstractDesignerTreeNode inode = getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            }
            Element e=new Element("node");
            e.setAttribute("name",title);
            e.setAttribute("isLeaf","true");
            e.setAttribute("serverType","JBossServer");
            e.setAttribute("url","localhost:1099");
            e.setAttribute("webUrl", "http://localhost:8080/Or3WAR");
            e.setAttribute("ear", "Or3EAR");
            e.setAttribute("baseName","");
            ServerNode node = new ServerNode(e);
            insertNodeInto(node, selNode, selNode.getChildCount());
            ((ServerNode)selNode).getXml().addContent(e);
            return node;
        }

/*
        private void deleteFilterNode(AbstractDesignerTreeNode node) throws KrnException {
            FiltersTree ftree = Utils.getFiltersTree();
            FilterNode filter = ftree.findByName(node.toString());
            if (filter != null) {
                FiltersTree.FilterTreeModel fModel =
                        (FiltersTree.FilterTreeModel)ftree.getModel();
                fModel.deleteNode(filter, false);
            }
        }
*/

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) {
            removeNodeFromParent(node);
            ((ServerNode)node).getXml().detach();
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove){
            if (!isMove) {
                node = new ServerNode(((ServerNode)node).getXml());
            }
            insertNodeInto(node, parent, parent.getChildCount());
            ((ServerNode)node).getXml().detach();
            ((ServerNode)parent).getXml().addContent(((ServerNode)node).getXml());
        }

    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            l.setIcon(getImageIcon(leaf ? "Drive" : expanded ? "Open" : "CloseFolder"));
            l.setOpaque(ServerTree.this.isOpaque() || selected);
            return l;
        }

    }


    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == miCreateFolder) {
            createFolder();
        } else if (src == miCreateElement) {
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
            keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED,
                    KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_DELETE,
                    KeyEvent.CHAR_UNDEFINED));
        } else if (src == miFind) {
            find();
        } else if (src == miFindNext) {
            keyPressed(new KeyEvent(this, KeyEvent.KEY_PRESSED,
                    KeyEvent.KEY_EVENT_MASK, -1, KeyEvent.VK_F3,
                    KeyEvent.CHAR_UNDEFINED));
        }

    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.CREATE_ELEMENT_TYPE,copyNode.toString(),lang);
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Вставка копии", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String serverName = cp.getElementName();
                if (serverName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        Element e=(Element)((ServerNode)copyNode).getXml().clone();
                        e.detach();
                        e.setAttribute("name",serverName);
                        ServerNode node= new ServerNode(e);
                        if (!copyNode.isCutProcess()) {
                            model.addNode(node, parent, false);
                        } else {
                            model.addNode(node, parent, false);
                            model.deleteNode(copyNode, true);
                            ((ServerNode)copyNode).getXml().detach();
                        }
//                        ((ServerNode)parent).getXml().addContent(e);
                        setSelectedNode(node.toString());
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

    private void createFolder() {
        AbstractDesignerTreeNode node = getSelectedNode();
        if (!node.isLeaf()) {
            CreateElementPanel p = new CreateElementPanel(
                        CreateElementPanel.CREATE_FOLDER_TYPE,"NewFolder",lang);
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
                        AbstractDesignerTreeNode newNode =model.createFolderNode(p.getElementName());
                    setSelectedNode(newNode.toString());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void createElement() {
        AbstractDesignerTreeNode node = getSelectedNode();
        if (!node.isLeaf()) {
            CreateElementPanel p = new CreateElementPanel(
                    CreateElementPanel.CREATE_ELEMENT_TYPE,"newElement",lang);
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
                    AbstractDesignerTreeNode newNode=model.createChildNode(p.getElementName());
                    setSelectedNode(newNode.toString());
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void copyElement() {
        copyNode = getSelectedNode();
        copyNode.setCopyProcessStarted(true);
        setCursor(Constants.HAND_CURSOR);
    }

    private void cutElement() {
        copyNode = getSelectedNode();
        copyNode.setCopyProcessStarted(true);
        copyNode.setCutProcess(true);
        setCursor(Constants.HAND_CURSOR);
    }
    private void renameServer() {
        model.renameNode();
    }

    public DesignerTreeNode[] getSelectedNodes() {
        ArrayList<ServerNode> list = new ArrayList<ServerNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ServerNode node = (ServerNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        ServerNode[] res = new ServerNode[list.size()];
        list.toArray(res);
        return res;
    }

    public void setSelectedNode(String name) {
        TreeModel m = getModel();
        ServerNode node = (ServerNode)finder.findFirst(
                (ServerNode)m.getRoot(), new StringPattern(name));
        if (node != null) {
            TreePath tpath = new TreePath(node.getPath());
            setSelectionPath(tpath);
            scrollPathToVisible(tpath);
        }
    }

    public ServerNode getServerNodeByName(String name) {
        TreeModel m = getModel();
        ServerNode node = (ServerNode)finder.findFirst(
                (ServerNode)m.getRoot(), new StringPattern(name));
        return node;
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
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        lastLocation = dtde.getLocation().getY();
        isDragStarted = false;
        Point loc = dtde.getLocation();
        int idx = getRowForLocation(loc.x, loc.y);
        if (idx != -1) {
            AbstractDesignerTreeCellRenderer cr =
                    (AbstractDesignerTreeCellRenderer)getCellRenderer();
            if (cr.setDragRow(idx))
                repaint();
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    public void drop(DropTargetDropEvent dtde) {
        int res = ButtonsFactory.BUTTON_NO;
        try {
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                String nodeName = (String) transferable.getTransferData(
                        DataFlavor.stringFlavor);
                AbstractDesignerTreeNode s =
                        (AbstractDesignerTreeNode) ((ServerNode)root).find(nodeName).getLastPathComponent();
                String mes = (s.isLeaf()) ? "Переместить элемент '" : "Переместить папку '";
                Container cnt = this.getTopLevelAncestor();
                if (cnt instanceof Dialog) {
                    res = MessagesFactory.showMessageDialog((Dialog)cnt,
                            MessagesFactory.QUESTION_MESSAGE, mes +
                            s.toString() + "' ?",lang);
                } else {
                    res = MessagesFactory.showMessageDialog((Frame)cnt,
                            MessagesFactory.QUESTION_MESSAGE, mes +
                            s.toString() + "' ?",lang);
                }
                if (res == ButtonsFactory.BUTTON_YES) {
                    dtde.getDropTargetContext().dropComplete(addElement(s, dtde.getLocation()));
                }
                AbstractDesignerTreeCellRenderer cr =
                        (AbstractDesignerTreeCellRenderer)getCellRenderer();
                cr.setDragRow(-1);
                repaint();
            } else {
                dtde.rejectDrop();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            dtde.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
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
            TransferableObject selected = new TransferableObject(node.toString());
            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, selected, this);
        }
    }

    public class TransferableObject implements Transferable {
        private static final int STRING = 0;
        private final DataFlavor[] flavors = {DataFlavor.stringFlavor};
        private String transfer;

        public TransferableObject(String transfer) {
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
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && getSelectedNode().isLeaf()) {
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
        if (e.isPopupTrigger()) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                setSelectionPath(path);
            }
            if (isShowPopupEnabled()) {
                showPopup(e);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                setSelectionPath(path);
            }
            if (isShowPopupEnabled()) {
                showPopup(e);
            }
        }
    }

    protected void showPopup(MouseEvent e) {
        if(getSelectedNode()!=null){
            if (getSelectedNode().isLeaf()) {
                miCreateFolder.setEnabled(false);
                miCreateElement.setEnabled(false);
                miCopy.setEnabled(true);
                miCut.setEnabled(true);
                miPaste.setEnabled(false);
            } else {
                miCreateFolder.setEnabled(true);
                miCreateElement.setEnabled(true);
                miCopy.setEnabled(false);
                miCut.setEnabled(false);
                if (copyNode != null) {
                    miPaste.setEnabled(true);
                } else {
                    miPaste.setEnabled(false);
                }
            }
            miDelete.setEnabled(!(getSelectedNode() == root));
            miRename.setEnabled(true);
            pm.show(e.getComponent(), e.getX(), e.getY());
        }
    }


    public boolean isShowPopupEnabled() {
        return isShowPopupEnabled;
    }

    public void setShowPopupEnabled(boolean showPopupEnabled) {
        isShowPopupEnabled = showPopupEnabled;
    }
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            AbstractDesignerTreeNode node = getSelectedNode();
            if (node != null && !node.equals(model.getRoot())) {
                try {
                    String mes = "";
                    if (node.isLeaf()) {
                        mes = "Удаление элемента '" + node.toString() + "'!\nПродолжить?";
                    } else {
                        mes = "Удалить папку '" + node.toString() +
                                "' и всё её содержимое?";
                    }
                    int res;
                    if (getTopLevelAncestor() instanceof JDialog) {
                        res = MessagesFactory.showMessageDialog(
                            (JDialog)getTopLevelAncestor(),
                            MessagesFactory.QUESTION_MESSAGE, mes,lang);
                    } else {
                        res = MessagesFactory.showMessageDialog(
                            (Frame)getTopLevelAncestor(),
                            MessagesFactory.QUESTION_MESSAGE, mes,lang);
                    }
                    if (res == ButtonsFactory.BUTTON_YES) {
                        ServerNode parent=(ServerNode)node.getParent();
                        model.deleteNode(node, false);
                        setSelectedNode(parent.getChildCount()>0?parent.getChildAt(parent.getChildCount()-1).toString():parent.toString());
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
                    TreeNode fnode = finder.findNext();
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                            MessagesFactory.showMessageSearchFinished(getTopLevelAncestor(), lang);
                    }
                }
            });
            t.start();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (copyNode != null) {
                setCursor(Constants.DEFAULT_CURSOR);
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
}
