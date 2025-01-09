package kz.tamur.util;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.UIDPattern;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.ods.Value;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.TreeUIDMap;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.05.2004
 * Time: 18:59:04
 */
public class OpenElementPanel extends JPanel implements ActionListener {

    private DesignerTree tree;
    private DesignerTree treePr;
    private ServiceControlNode root;
    private boolean isShowPanel;
    private JPanel searchPanel = new JPanel();
    private JLabel searchLabel = kz.tamur.rt.Utils.createLabel("Введите UID для поиска: ");
    private JTextField searchText = kz.tamur.rt.Utils.createDesignerTextField();
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Найти", true);
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Скопировать UID", true);
    private JLabel objOwner = kz.tamur.rt.Utils.createLabel("");
    // for owner to send dialog OK button enabled or disabled  
    private int isCheck = 0;
    private boolean isOkEnabled;

    private TemplatesPanel templateSelectionPanel;
    private DualTreePanel selector;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public OpenElementPanel(DesignerTree tree) {
        this(tree, null, false);
    }

    public OpenElementPanel(DesignerTree tree, boolean isShowTemplatesPanel) {
        this(tree, null, isShowTemplatesPanel);
    }
    
    public OpenElementPanel(DesignerTree tree, boolean isShowTemplatesPanel, int isCheck, boolean isOkEnabled) {
        this(tree, null, isShowTemplatesPanel);
        this.isCheck = isCheck;
        this.isOkEnabled = isOkEnabled;
    }

    public OpenElementPanel(DesignerTree tree, ServiceControlNode root, boolean isShowPanel) {
        this.tree = tree;
        this.root = root;
        this.isShowPanel = isShowPanel;
        init();
    }
    
    void init() {
        selector = new DualTreePanel(tree, root);
        treePr = selector.getTreePr();

        setLayout(new BorderLayout());
        searchPanel.setLayout(new GridBagLayout());
        setOpaque(isOpaque);
        searchPanel.setOpaque(isOpaque);
        searchBtn.addActionListener(this);
        copyBtn.addActionListener(this);
        copyBtn.setEnabled(false);

        Insets ins = new Insets(4, 5, 4, 0);
        searchPanel.add(searchLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(searchText, new GridBagConstraints(1, 0, 1, 1, 0, 0, WEST, NONE, new Insets(4, 1, 4, 0), 0, 0));
        searchPanel.add(searchBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(copyBtn, new GridBagConstraints(3, 0, 1, 1, 1, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(objOwner, new GridBagConstraints(4, 0, 1, 1, 1, 0, WEST, NONE, ins, 0, 0));

        searchText.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				copyBtn.setEnabled(searchText.getText().trim().length() > 0);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				copyBtn.setEnabled(searchText.getText().trim().length() > 0);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				copyBtn.setEnabled(searchText.getText().trim().length() > 0);
			}
		});
        searchText.setText(tree.getSelectedNode() != null? tree.getSelectedNode().getKrnObj().uid : "");
        if (tree instanceof FiltersTree || tree instanceof InterfaceTree || tree instanceof ServicesTree) 
        	objOwner.setText("Владелец: " + (tree.getSelectedNode() != null? (Utils.getObjOwner(tree.getSelectedNode().getKrnObj()) != null? Utils.getObjOwner(tree.getSelectedNode().getKrnObj()): ""): ""));
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                AbstractDesignerTreeNode node = (AbstractDesignerTreeNode) e.getPath().getLastPathComponent();
                DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
                if (node == null) {
                	if (dialog != null) {
	                    if (tree instanceof FiltersTree) {
	                        dialog.setEditEnabled(false);
	                    }
	                    dialog.setOkEnabled(false);
                	}
                } else {
                	if (dialog != null) {
                		if(isCheck == Constants.NEED_CHECK_FOLDER) {
                			dialog.setOkEnabled(isOkEnabled);
                		} else { 
                		dialog.setOkEnabled(node.isLeaf());}
	                    if (tree instanceof FiltersTree) {
	                        dialog.setEditEnabled(node.isLeaf());
	                    }
	                    //else
	                    	//dialog.setOkEnabled(true);
                	}
                }
                TreePath[] selPaths = tree.getSelectionPaths();
            	if (selPaths == null) {
            		{
	                    if (tree instanceof FiltersTree) {
	                        dialog.setEditEnabled(false);
	                    }
	                    dialog.setOkEnabled(false);
                	}
            	}
                if (templateSelectionPanel != null) {
                    templateSelectionPanel.insertIntoTemplate(node);
                }
                searchText.setText(node != null ? node.getKrnObj().uid : "");
                
                if (tree instanceof FiltersTree || tree instanceof InterfaceTree || tree instanceof ServicesTree)
                	objOwner.setText("Владелец: " + (node != null? (Utils.getObjOwner(node.getKrnObj()) != null? Utils.getObjOwner(node.getKrnObj()) : ""): ""));
            }
        });
        
        if (treePr != null) {
            treePr.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    ServiceControlNode node = (ServiceControlNode) e.getPath().getLastPathComponent();
                    if (templateSelectionPanel != null) {
                        templateSelectionPanel.insertIntoTemplate(node, getNodeObj(node));
                    }
                    searchText.setText(node.getValue().uid);
                }
            });
        }
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchByUID(searchText.getText(), false);
                }
            }
        });

        setPreferredSize(new Dimension(600, 400));

        searchPanel.setVisible(false);
        add(searchPanel, BorderLayout.NORTH);
        add(selector, BorderLayout.CENTER);

        if (isShowPanel) {
            templateSelectionPanel = new TemplatesPanel(tree);
            add(templateSelectionPanel, BorderLayout.SOUTH);
        }

    }
    
    public void setSelectedFilterNode(KrnObject selectedFilterNode) {
		selector.getTree().setSelectedNode(selectedFilterNode);
    }
    
    public void setSearchText(String uid) {
    	searchText.setText(uid);
    }

    public String[] getSelectedObject() {
        templateSelectionPanel.setTree(selector.getSelectedTree());
        return templateSelectionPanel.getCodeTemplate();
    }

    public void actionPerformed(ActionEvent e) {
        Object key = e.getSource();
        if (key == searchBtn) {
            searchByUID(searchText.getText(), false);
        } else if (key == copyBtn) {
        	StringSelection selection = new StringSelection(searchText.getText().trim());
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(selection, selection);
        }
    }

    public TreeNode searchByUID(String uid, boolean justReturn) {
    	// Проверяем формат UID
        if (uid.indexOf('.') < 0) {
            MessagesFactory.showMessageDialog((Dialog) this.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, "Неверный формат UID!");
            return null;
        }

		CursorToolkit.startWaitCursor(OpenElementPanel.this.getTopLevelAncestor());

		// Ищем узел в дереве
		TreeNode result = tree.searchByUID(uid);
		// Текущий выбранный узел в дереве
		AbstractDesignerTreeNode selNode = tree.getSelectedNode();

		if (!justReturn) {
			if (result == null) {
				MessagesFactory.showMessageNotFound(OpenElementPanel.this.getTopLevelAncestor());
			} else if (!result.equals(selNode)) {
				TreePath path = new TreePath(((DefaultMutableTreeNode) result).getPath());
				if (path != null) {
					tree.setSelectionPath(path);
					tree.scrollPathToVisible(path);
				}
			} else if (result.equals(selNode)) {
				MessagesFactory.showMessageSearchFinished(OpenElementPanel.this.getTopLevelAncestor());
			}
		}
		
		CursorToolkit.stopWaitCursor(OpenElementPanel.this.getTopLevelAncestor());
		return result;
    }

    public void setSearchUIDPanel(boolean visible) {
        searchPanel.setVisible(visible);
    }

    public DesignerTree getTree() {
        return selector.getSelectedTree();
    }

    public KrnObject getNodeObj(AbstractDesignerTreeNode node) {
        return node instanceof ServiceControlNode ? ((ServiceControlNode) node).getValue() : node.getKrnObj();
    }
}