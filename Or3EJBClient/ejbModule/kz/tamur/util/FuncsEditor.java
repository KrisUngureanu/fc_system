package kz.tamur.util;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: KazakBala
 * Date: 16.05.2005
 * Time: 10:18:57
 */
public class FuncsEditor extends JPanel implements ActionListener {
    private JComboBox ForV = new JComboBox();
    private JList list = new JList(new DefaultListModel());
    private Map<String, String> vars = new HashMap<String, String>();
    private Map<String, String> funcs = new HashMap<String, String>();
    private JTextArea desc = new JTextArea();
    private String FUNC = "Функции";
    private String VAR = "Переменные";
    private JButton saveBtn = new JButton("Сохранить");
    private JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private FuncsTree tree;
    private OrBasicTabbedPane tabbed = new OrBasicTabbedPane();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem miCreateFolder = createMenuItem("Создать папку", "CloseFolder");
    private JMenuItem miCreateElement = createMenuItem("Создать элемент", "Create");
    private JMenuItem miDelete =  createMenuItem("Удалить", "Delete");

    public FuncsEditor(Map<String, String> vars_, Map<String, String> funcs_) {
        vars = vars_;
        funcs = funcs_;
        ForV.addItem(FUNC);
        ForV.addItem(VAR);
        ForV.setSelectedItem(FUNC);
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints constr = new GridBagConstraints();
        constr.fill = GridBagConstraints.BOTH;
        constr.anchor = GridBagConstraints.WEST;
        constr.weightx = 0;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 0;
        constr.gridheight = 1;
        constr.gridwidth = 2;
        toolBar.add(ForV);
        add(toolBar, constr);

        constr.anchor = GridBagConstraints.CENTER;
        constr.weightx = 0;
        constr.weighty = 1;
        constr.gridx = 0;
        constr.gridy = 1;
        constr.gridheight = 1;
        constr.gridwidth = 1;
        constr.insets = Constants.INSETS_1;
        constr.weightx = 1;
        constr.weighty = 1;
        constr.gridx = 1;
        constr.gridy = 1;
        constr.gridheight = 1;
        constr.gridwidth = 1;
        JScrollPane sc = new JScrollPane(desc);
        sc.setPreferredSize(new Dimension(600, 400));
        //add(sc, constr);
        JSplitPane split = new JSplitPane();
        split.setDividerLocation(150); 
        split.setLeftComponent(tabbed);
        split.setRightComponent(sc);
        add(split, constr);


        JPanel btmPane = new JPanel();
        constr.weightx = 1;
        constr.weighty = 0;
        constr.gridx = 1;
        constr.gridy = 2;
        constr.gridheight = 1;
        constr.gridwidth = 1;
        btmPane.add(saveBtn);
        add(btmPane, constr);
        saveBtn.setFont(Utils.getDefaultFont());
        
        btmPane.setOpaque(isOpaque);
        setOpaque(isOpaque);
        split.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);

        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        ForV.setFont(Utils.getDefaultFont());
        list.setFont(Utils.getDefaultFont());
        desc.setFont(Utils.getDefaultFont());
        ForV.addActionListener(this);
        saveBtn.addActionListener(this);


        list.addListSelectionListener(new MySelect());
        Element e = getXML(FUNC);
        FuncNode node = null;
        if (e != null) {
            node = getTree(e);
            sortList();
        }
        tree = new FuncsTree(node);
        tabbed.addTab("Logic", new JScrollPane(tree));
        tabbed.addTab("Alphabetic", new JScrollPane(list));
        tabbed.setFont(Utils.getDefaultFont());
        tabbed.setSelectedIndex(0);

        popupMenu.add(miCreateFolder);
        miCreateFolder.addActionListener(this);
        popupMenu.add(miCreateElement);
        miCreateElement.addActionListener(this);
        popupMenu.add(miDelete);
        miDelete.addActionListener(this);
        tree.setShowPopupEnabled(false);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					FuncNode selectedNode = (FuncNode) tree.getSelectedNode();
					if (selectedNode != null) {
						showPopup(e, selectedNode);
					}
				}
			}
		});
    }

    private void showPopup(MouseEvent e, FuncNode selectedNode) {
    	if (selectedNode.isLeaf()) {
    		miCreateFolder.setEnabled(false);
    		miCreateElement.setEnabled(false);
    	} else {
    		miCreateFolder.setEnabled(true);
    		miCreateElement.setEnabled(true);
    	}
    	if (selectedNode.equals(tree.getRoot())) {
    		miDelete.setEnabled(false);
    	} else {
    		miDelete.setEnabled(true);
    	}
    	popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == ForV) {
			Object obj = ForV.getSelectedItem();
			((DefaultListModel) list.getModel()).clear();
			FuncNode node = getTree(getXML(obj.toString()));
			((FuncsTree.FuncsTreeModel) tree.getModel()).setRoot(node);
			sortList();
		} else if (src == saveBtn) {
			FuncNode node = (FuncNode) tree.getSelectedNode();
			node.setDesc(desc.getText());
			saveTree();
		} else if (src == miCreateFolder) {
			createFolder();
		} else if (src == miCreateElement) {
			createElement();
		} else if (src == miDelete) {
			delete();
		}
	}
        
	private void createFolder() {
		CreateElementPanel p = new CreateElementPanel(CreateElementPanel.CREATE_FOLDER_TYPE, "");
		DesignerDialog dlg = null;
		if (getTopLevelAncestor() instanceof JDialog) {
			dlg = new DesignerDialog((JDialog) getTopLevelAncestor(), "Создание папки", p);
		} else {
			dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Создание папки", p);
		}
		dlg.show();
		if (dlg.isOK()) {
			try {
				FuncNode createdNode = (FuncNode) ((DesignerTreeModel) tree.getModel()).createFolderNode(p.getElementName());
				tree.setSelectedNode(createdNode);
			} catch (KrnException e) {
				e.printStackTrace();
			}

		}
	}
    
    private void createElement() {
		CreateElementPanel p = new CreateElementPanel(CreateElementPanel.CREATE_ELEMENT_TYPE, "");
		DesignerDialog dlg = null;
		if (getTopLevelAncestor() instanceof JDialog) {
			dlg = new DesignerDialog((JDialog) getTopLevelAncestor(), "Создание элемента", p);
		} else {
			dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Создание элемента", p);
		}
		dlg.show();
		if (dlg.isOK()) {
			try {
				FuncNode createdNode = (FuncNode) ((DesignerTreeModel) tree.getModel()).createChildNode(p.getElementName());
				tree.setSelectedNode(createdNode);
	            ((DefaultListModel) list.getModel()).addElement(createdNode);
	            sortList();
			} catch (KrnException e) {
				e.printStackTrace();
			}

		}
    }
    
    private void delete() {
    	FuncNode selectedNode = (FuncNode) tree.getSelectedNode();
    	FuncNode parentNode = (FuncNode) selectedNode.getParent();
    	try {
			((DesignerTreeModel) tree.getModel()).deleteNode(selectedNode, true);
			tree.setSelectedNode(parentNode);

		} catch (KrnException e) {
			e.printStackTrace();
		}
    }

    private Element getItems(String str) {
        File url = null;
        if (str.equals("Функции"))
            url = new File("funcs.xml");
        else if (str.equals("Переменные"))
            url = new File("vars.xml");
        if (url != null) {
            SAXBuilder builder = new SAXBuilder();
            Element xml = null;
            try {
                xml = builder.build(url).getRootElement();
                return xml;
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Element getXML(String str) {
        Element xml_doc = null;
        try {
            Kernel krn = Kernel.instance();
            SAXBuilder builder = new SAXBuilder();
            KrnClass cl = null;
            cl = krn.getClassByName("OrLang");
            KrnObject[] objs = krn.getClassObjects(cl, 0);

            if (objs.length > 0) {
                KrnObject obj = objs[0];
                byte[] func_data = null;
                if (str.equals("Функции")) {
                    func_data = krn.getBlob(obj, "funcs", 0, 0, 0);}
                else if (str.equals("Переменные"))
                    func_data = krn.getBlob(obj, "vars", 0, 0, 0);
                if (func_data.length > 0) {
                    InputStream is = new ByteArrayInputStream(func_data);
                    xml_doc = (Element) builder.build(is).getRootElement();
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return xml_doc;
    }

/*    private void makeList(Map map) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.clear();
        int i = 0;
        for (Iterator fnIt = map.keySet().iterator(); fnIt.hasNext();) {
            final String func = (String) fnIt.next();
            model.add(i, func);
            i++;
        }
    }*/

    private void sortList() {
        DefaultListModel model = ((DefaultListModel)list.getModel());
        Object obj[] = model.toArray();
        List jlist = Arrays.asList(obj);
        Collections.sort(jlist);
        model.clear();
        for (int i=0; i< jlist.size(); i++)
            model.add(i,jlist.get(i));
    }

    private FuncNode getTree(Element e) {
        FuncNode node = null;
        if (e.getName().equals("folder")) {
            String str = e.getAttribute("name").getValue();
            String desc = e.getText();
            node = new FuncNode(str, desc, false, 0);
            if (e.getChildren().size() > 0) {
                java.util.List list = e.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    node.add(getTree((Element) list.get(i)));
                }
                return node;
            }
        } else if (e.getName().equals("func")) {
            String code = e.getAttribute("name").getValue();
            String desc = e.getText();
            node = new FuncNode(code, desc, true, 0);
            ((DefaultListModel) list.getModel()).addElement(node);
            return node;
        }
        return node;
    }

    private void saveTree() {
        FuncNode node = (FuncNode) ((FuncsTree.FuncsTreeModel) tree.getModel()).getRoot();
        Element element = visitAllNodes(node);
        saveToBase(element, ForV.getSelectedItem().toString());
    }

    public Element visitAllNodes(FuncNode node) {
        Element element = null;
        if (!node.isLeaf()) {
            element = new Element("folder");
            element.setAttribute("name", node.toString());
            element.setText(node.getDesc());
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    FuncNode n = (FuncNode) e.nextElement();
                    element.addContent(visitAllNodes(n));
                }
            }
        } else if (node.isLeaf()) {
            element = new Element("func");
            element.setAttribute("name", node.toString());
            element.setText(node.getDesc());
            return element;
        }
        return element;
    }


    private void saveToBase(Element element, String str) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(element, os);
            Kernel krn = Kernel.instance();
            KrnClass cl = krn.getClassByName("OrLang");
            KrnObject[] objs = krn.getClassObjects(cl, 0);
            if (objs.length > 0) {
                KrnObject obj = objs[0];
                String attrName = (str.equals(FUNC)) ? "funcs" : "vars";
                krn.setBlob(obj.id, obj.classId, attrName, 0, os.toByteArray(), 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class MySelect implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent evt) {
            JList list = (JList) evt.getSource();
            FuncNode selected = (FuncNode) list.getSelectedValue();
            if (selected != null) {
                desc.setText(selected.getDesc());
            }

        }
    }

    public class FuncsTree extends DesignerTree implements TreeSelectionListener {
        public FuncsTree(FuncNode root) {
            super(root);
            this.root = root;
            model = new FuncsTreeModel(root);
            setModel(model);
            setCellRenderer(new CellRenderer());
            setBackground(Utils.getLightSysColor());
            addTreeSelectionListener(this);
        }

        protected void defaultDeleteOperations() {
        }

        protected void find() {
        }

        public void drop(DropTargetDropEvent dtde) {
            int res = ButtonsFactory.BUTTON_NO;
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                FuncNode s = (FuncNode) getSelectedNode();
                String mes = (s.isLeaf()) ? "Переместить элемент '" : "Переместить папку '";
                res = MessagesFactory.showMessageDialog(this.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                        mes + s.toString() + "' ?");
                if (res == ButtonsFactory.BUTTON_YES) {
                    dtde.getDropTargetContext().dropComplete(addElement(s, dtde.getLocation()));
                }
                AbstractDesignerTreeCellRenderer cr = (AbstractDesignerTreeCellRenderer) getCellRenderer();
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

        protected void pasteElement() {
        }

        public void valueChanged(TreeSelectionEvent e) {
        	FuncNode selectedNode = (FuncNode) getSelectedNode();
        	if (selectedNode != null) {
        		String str = ((FuncNode) getSelectedNode()).getDesc();
        		desc.setText(str);
        	}
        }


        public class FuncsTreeModel extends DefaultTreeModel implements DesignerTreeModel {

            public FuncsTreeModel(TreeNode root) {
                super(root);
            }

            public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
                FuncNode selNode = (FuncNode) root;
                FuncNode inode = (FuncNode) getSelectedNode();
                if (inode != null && !inode.isLeaf()) {
                    selNode = inode;
                }
                int idx = selNode.getChildCount();
                FuncNode node = new FuncNode(title, "", false, idx);
                insertNodeInto(node, selNode, selNode.getChildCount());
                saveTree();
                return node;

            }

            public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
                FuncNode selNode = (FuncNode) root;
                FuncNode inode = (FuncNode) getSelectedNode();
                if (inode != null && !inode.isLeaf()) {
                    selNode = inode;
                }
                int idx = selNode.getChildCount();
                FuncNode node = new FuncNode(title, "", true, idx);
                insertNodeInto(node, selNode, selNode.getChildCount());
                saveTree();
                return node;
            }

            public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
                removeNodeFromParent(node);
                saveTree();
            }

            public void addNode(AbstractDesignerTreeNode node, AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
                insertNodeInto(node, parent, parent.getChildCount());
            }

            public void renameNode() {
                FuncNode node = (FuncNode) getSelectedNode();
                if (node != root) {
                    CreateElementPanel cp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, node.toString());
                    DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(),
                            "Переименование интерфейса", cp);
                    dlg.show();
                    int res = dlg.getResult();
                    if (res == ButtonsFactory.BUTTON_OK) {
                        try {
                            FuncNode source = (FuncNode) getSelectedNode();
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

        private class CellRenderer extends AbstractDesignerTreeCellRenderer {
            
            private Icon OPENED_FOLDER = kz.tamur.rt.Utils.getImageIcon("Open"); 
            private Icon CLOSED_FOLDER = kz.tamur.rt.Utils.getImageIcon("CloseFolder"); 

            public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                          boolean selected,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row, boolean hasFocus) {
                JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, leaf, row, hasFocus);
                if (!leaf) {
                    if (expanded) {
                        l.setIcon(OPENED_FOLDER);
                    } else {
                        l.setIcon(CLOSED_FOLDER);
                    }
                } else {
                    //l.setIcon(kz.tamur.rt.Utils.getImageIcon("func"));
                    l.setIcon(null);
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
    }

}

