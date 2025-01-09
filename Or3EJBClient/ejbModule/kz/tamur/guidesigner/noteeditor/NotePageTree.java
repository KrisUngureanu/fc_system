package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.util.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StringContent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.jdom.CDATA;
import org.jdom.Element;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDropEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 10.10.2005
 * Time: 16:03:39
 * To change this template use File | Settings | File Templates.
 */
public class NotePageTree extends DesignerTree {
    public NotePageTree(NotePageNode root, boolean editable) {
        super(root, editable);
        this.root = root;
        model = new NotePageTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());
    }

    protected void defaultDeleteOperations() {
    }

    protected void find() {
    }

    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            NotePageNode s = (NotePageNode) getSelectedNode();

            String mes = (s.isLeaf()) ? "Переместить элемент '" : "Переместить папку '";
            int res =  MessagesFactory.showMessageDialog(this.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, mes + s.toString() + "' ?");
            if (res == ButtonsFactory.BUTTON_YES) {
                dtde.getDropTargetContext().dropComplete(addElement(s, dtde.getLocation()));
            }
            AbstractDesignerTreeCellRenderer cr =
                    (AbstractDesignerTreeCellRenderer) getCellRenderer();
            cr.setDragRow(-1);
            repaint();
        } else {
            dtde.rejectDrop();
        }
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        DesignerTreeNode node = getSelectedNode();
        if (node != null) {
            TransferableObject selected = new TransferableObject(node.toString());
            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, selected, this);
        }
    }

  private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("helpFolderOpen"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("helpFolderClose"));
                }
            } else {
                l.setIcon(kz.tamur.rt.Utils.getImageIcon("helpLeaf"));
            }
            l.setOpaque(selected || isOpaque);
            return l;
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

    public class NotePageTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        public NotePageTreeModel(TreeNode root) {
            super(root);
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            NotePageNode selNode = (NotePageNode) root;
            NotePageNode inode = (NotePageNode) getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            }
            int idx = selNode.getChildCount();
            NotePageNode node = new NotePageNode(title, null, false, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;

        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            NotePageNode selNode = (NotePageNode) root;
            NotePageNode inode = (NotePageNode) getSelectedNode();
            if (inode != null && !inode.isLeaf()) {
                selNode = inode;
            }
            int idx = selNode.getChildCount();
            NotePageNode node = new NotePageNode(title, null, true, idx);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            removeNodeFromParent(node);
        }

        public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
        	if(parent.isLeaf()){
        		AbstractDesignerTreeNode parent_=(AbstractDesignerTreeNode)parent.getParent();
        		insertNodeInto(node, parent_, parent_.getIndex(parent));
        		
        	}else
        		insertNodeInto(node, parent, parent.getChildCount());
        }
        public void renameNode() {
            NotePageNode node = (NotePageNode) getSelectedNode();
            if (node == root) {

            } else {
                CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, node.toString());
                DesignerDialog dlg = null;
                if (getTopLevelAncestor() instanceof JDialog)
                dlg = new DesignerDialog((Dialog) getTopLevelAncestor(),
                        "Переименование заголовка", cp);
                else if (getTopLevelAncestor() instanceof JFrame)
                dlg = new DesignerDialog((JFrame) getTopLevelAncestor(),
                        "Переименование заголовка", cp);
                dlg.show();
                int res = dlg.getResult();
                if (res == ButtonsFactory.BUTTON_OK) {
                    try {
                        NotePageNode source = (NotePageNode) getSelectedNode();
                        source.rename(cp.getElementName());
                        TreeNode[] tp = getPathToRoot(source);
                        fireTreeNodesChanged(this, tp, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    protected void pasteElement() {
    	AbstractDesignerTreeNode parent=getSelectedNode();
        if (copyNode != null) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Создание копии справки", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String newName = cp.getElementName();
                if (newName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя справки!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                        String oldName = copyNode.toString();
                        if (!copyNode.isCutProcess()) {
                           	Element e=packNodes((NotePageNode)copyNode);
                        	NotePageNode node=getTree(e);
                        	if(!newName.equals(oldName))
                        		node.rename(newName);
                        	try {
                    			model.addNode(node,parent, false);
                    		} catch (KrnException ex) {
                    			ex.printStackTrace();
                    		}
                        } else {
                            defaultDeleteOperations();
                        	try {
                                model.deleteNode(copyNode, true);
                            	if(!newName.equals(oldName))
                            		copyNode.rename(newName);
                    			model.addNode(copyNode,parent, false);
                    		} catch (KrnException ex) {
                    			ex.printStackTrace();
                    		}
                        }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }
    }

    private NotePageNode getTree(Element e) {
        NotePageNode node = null;
        if (e.getName().equals("folder")) {
            String str = e.getAttribute("name").getValue();
            HTMLDocument htmlDoc = null;
            String html = "";
            if (e.getContentSize() > 0) {
                java.util.List list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText();
                        try {
                            if (!html.equals("")) {
                                StringContent strCont = new StringContent(html.length());
                                strCont.insertString(0, html);
                                htmlDoc = new HTMLDocument(Utils.getOrCSS());
                                HTMLEditorKit kit = new HTMLEditorKit();
                                ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
                                kit.read(is, htmlDoc, 0);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }

            node = new NotePageNode(str, htmlDoc, false, 0);
            if (e.getChildren().size() > 0) {
                java.util.List list = e.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Element) {
                        node.add(getTree((org.jdom.Element) list.get(i)));
                    }
                }
                return node;
            }
        } else if (e.getName().equals("func")) {
            String title = e.getAttribute("name").getValue();
            String html = "";
            HTMLDocument htmlDoc = null;
            if (e.getContentSize() > 0) {
                java.util.List list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText();
                        try {
                            if (!html.equals("")) {
                                StringContent strCont = new StringContent(html.length());
                                strCont.insertString(0, html);
                                htmlDoc = new HTMLDocument(Utils.getOrCSS());
                                HTMLEditorKit kit = new HTMLEditorKit();
                                ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
                                kit.read(is, htmlDoc, 0);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }
            node = new NotePageNode(title, htmlDoc, true, 0);
            return node;
        }
        return node;
    }
  public Element packNodes(NotePageNode node) {
        Element element = null;
        if (!node.isLeaf()) {
            element = new Element("folder");
            element.setAttribute("name", node.toString());
            HTMLDocument html = node.getContent();
            String value = encodeDoc(html);
            CDATA cdata = new CDATA(value);
            element.addContent(cdata);
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    NotePageNode n = (NotePageNode) e.nextElement();
                    element.addContent(packNodes(n));
                }
            }
        } else if (node.isLeaf()) {
            element = new Element("func");
            element.setAttribute("name", node.toString());
            HTMLDocument html = node.getContent();
            String value = encodeDoc(html);
            CDATA cdata = new CDATA(value);
            element.addContent(cdata);
            return element;
        }
        return element;
    }
    private String encodeDoc(HTMLDocument html) {
        String value = "";
            if (html != null) {
            try {
                String tmp = html.getText(0, html.getLength());
                if (!"".equals(tmp)) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HTMLEditorKit kit = new HTMLEditorKit();
                    kit.write(os, html, 0, html.getLength());
                    os.close();
                    value = os.toString();
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }}
        return value;
    }
}
