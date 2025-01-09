package kz.tamur.guidesigner;

import kz.tamur.comps.Utils;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DualTreePanel;
import kz.tamur.util.ServiceControlNode;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.rt.MainFrame;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.Or3Frame;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.rt.Utils.*;

/**
 * User: vital
 * Date: 11.12.2004
 * Time: 11:58:47
 */
public class MultiEditor extends JPanel implements TreeSelectionListener, ActionListener, ListSelectionListener {

    public static final int BASE_EDITOR = 0;
    public static final int REPORT_EDITOR = 1;
    public static final int FILTER_EDITOR = 2;
    public static final int NOTE_EDITOR = 3;
    public static final int MENU_EDITOR = 4;

    public static final int INTERFACE_AREA = 0;
    public static final int PROCESS_AREA = 1;

    private DesignerTree tree;

    private ClassBrowser classBrowser;
    private String defaultClass = "";
    private String lastPath;
    private Object value;
    private long oldFilterId;

    private MultiEditorTableModel tableModel = new MultiEditorTableModel();

    private JTable table = new JTable(tableModel);
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem editItem = createMenuItem("Редактировать");
    private JMenuItem viewItem = createMenuItem("Посмотреть");
    
    private JPanel searchPanel = new JPanel();
    private JLabel searchLabel = kz.tamur.rt.Utils.createLabel("Введите UID для поиска: ");
    private JTextField searchText = kz.tamur.rt.Utils.createDesignerTextField();
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Найти", true);
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Скопировать UID", true);
    private JLabel LabelR = kz.tamur.rt.Utils.createLabel("UID: ");
    private JTextField searchTextR = kz.tamur.rt.Utils.createDesignerTextField();
    private JButton copyBtnR = ButtonsFactory.createToolButton("Copy", "Скопировать UID", true);
    
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    private DefaultListModel model = new DefaultListModel();
    private JList selectedList = new JList(model);
    private JToggleButton rootBtn = ButtonsFactory.createToggleButton(false, "UpLevel", "");
    private JButton addBtn = ButtonsFactory.createToolButton("addSingle", "", "", true);
    private JButton removeBtn = ButtonsFactory.createToolButton("removeSingle", "", "", true);
    private JButton removeAllBtn = ButtonsFactory.createToolButton("removeAll", "", "", true);
    private DualTreePanel selector;
    
    private KrnObject[] oldValue;

    private int editorType;
    private int area;
    private TreeNode filterRoot;
    public DesignerTreeNode initNode;
    
    public MultiEditor(int editorType, int area) {
        this.editorType = editorType;
        this.area = area;
        setLayout(new GridBagLayout());
        init();
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = (table.getSelectedRow() != -1) ? table.getSelectedRow() : 0;
                Object o = tableModel.getValueAt(row, table.getSelectedColumn());
                removeBtn.setEnabled(o != null);
            }
        });
        
        popup.add(editItem);
        editItem.addActionListener(this);
        popup.add(viewItem);
        viewItem.addActionListener(this);
        TableColumn tc = table.getColumnModel().getColumn(1);
        tc.setCellEditor(new DataCellEditor());
        tc = table.getColumnModel().getColumn(2);
        tc.setCellEditor(new DataCellEditor());
        JTableHeader th = table.getTableHeader();
        th.setFont(getDefaultFont());
        th.setForeground(getDarkShadowSysColor());
        table.setFont(getDefaultFont());
        table.setForeground(getDarkShadowSysColor());
        selectedList.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent e) {
            	if (e.isPopupTrigger())  showPopup(e);                
            }
        	
        	public void showPopup(MouseEvent e) {
        		popup.show(e.getComponent(),e.getX(),e.getY());
        	}
        });
    }
   
    private void init() {
    	searchPanel.setLayout(new GridBagLayout());
        searchPanel.setOpaque(isOpaque);
        searchBtn.addActionListener(this);
        copyBtn.addActionListener(this);
        copyBtn.setEnabled(false);
        copyBtnR.addActionListener(this);
        copyBtnR.setEnabled(false);

        Insets ins = new Insets(4, 5, 4, 0);
        searchPanel.add(searchLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(searchText, new GridBagConstraints(1, 0, 1, 1, 0, 0, WEST, NONE, new Insets(4, 1, 4, 0), 0, 0));
        searchPanel.add(searchBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(copyBtn, new GridBagConstraints(3, 0, 1, 1, 1, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(LabelR, new GridBagConstraints(4, 0, 1, 1, 0, 0, WEST, NONE, ins, 0, 0));
        searchPanel.add(searchTextR, new GridBagConstraints(5, 0, 1, 1, 0, 0, WEST, NONE, new Insets(4, 1, 4, 0), 0, 0));
        searchPanel.add(copyBtnR, new GridBagConstraints(6, 0, 1, 1, 1, 0, WEST, NONE, ins, 0, 0));
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
        searchTextR.getDocument().addDocumentListener(new DocumentListener() {

        	@Override
        	public void removeUpdate(DocumentEvent e) {
        		copyBtnR.setEnabled(searchTextR.getText().trim().length() > 0);
        	}

        	@Override
        	public void insertUpdate(DocumentEvent e) {
        		copyBtnR.setEnabled(searchTextR.getText().trim().length() > 0);
        	}

        	@Override
        	public void changedUpdate(DocumentEvent e) {
        		copyBtnR.setEnabled(searchTextR.getText().trim().length() > 0);
        	}
        });

        setPreferredSize(new Dimension(600, 400));
        switch (editorType) {
        case BASE_EDITOR:
        	tree = Utils.getBaseTree();
        	break;
        case REPORT_EDITOR:
            tree = Utils.getReportTree(DesignerFrame.instance().getInterfaceLang());
            break;
        case FILTER_EDITOR:
            try {
                Kernel krn = Kernel.instance();
                KrnClass cls = krn.getClassByName("FilterRoot");
                KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
                long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
                long[] ids = { filterRoot.id };
                StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId, false, 0);
                String title = "Не определён";
                if (strs.length > 0) {
                    title = strs[0].value;
                }
                this.filterRoot = new FilterNode(filterRoot, title, langId, 0);
            } catch (KrnException e) {
                e.printStackTrace();
            }

            if (area == INTERFACE_AREA) {
                tree = Utils.getFiltersTree();
            } else {
                tree = Utils.getFiltersTree();
                validate();
                repaint();
                rootBtn.setEnabled(false);
            }
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                	AbstractDesignerTreeNode node = (AbstractDesignerTreeNode) e.getPath().getLastPathComponent();
                    Container cnt = getTopLevelAncestor();
                    if (cnt instanceof DesignerDialog) {
                        FilterNode fn = (FilterNode) e.getPath().getLastPathComponent();
//                        ((DesignerDialog) cnt).setOkEnabled(fn.isLeaf());
                    }
                    searchText.setText(node.getKrnObj().uid);
                    TreePath[] selPaths = tree.getSelectionPaths();
                	if (selPaths == null) {
                		{
                			searchText.setText("");
    	                    addBtn.setEnabled(false);
                    	}
                	}
                }
            });
            break;
        case NOTE_EDITOR:
            tree = Utils.getNotesTree();
            break;
        case MENU_EDITOR:
            tree = Utils.getHyperTree();
            break;
        }

        tree.addTreeSelectionListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        selector = new DualTreePanel(tree);
        if (selector.getTreePr()!=null) {
            selector.getTreePr().addTreeSelectionListener(this);
        }
        selector.setPreferredSize(new Dimension(255, 100));
        selectedList.setFont(getDefaultFont());
        selectedList.setBackground(getLightSysColor());
        selectedList.setForeground(getDarkShadowSysColor());
        selectedList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int idx = -1;
				idx = selectedList.getSelectedIndex();
				if(idx != -1) {
					DesignerTreeNode n = (DesignerTreeNode) model.get(idx);
		        	KrnObject obj = n.getKrnObj();
	                searchTextR.setText(obj.uid);
				}
				else {
					searchTextR.setText("");
				}
				removeBtn.setEnabled(idx != -1);
	        	
			}
		});
        
        selectedList.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent e) {
            	if (e.isPopupTrigger())  showPopup(e);                
            }
        	
        	public void showPopup(MouseEvent e) {
        		
        		popup.show(e.getComponent(), e.getX(), e.getY());
        	}
		});
        Dimension sz = new Dimension(30, 30);
        setAllSize(rootBtn,sz);
        setAllSize(addBtn,sz);
        setAllSize(removeBtn,sz);
        setAllSize(removeAllBtn,sz);
        addBtn.setEnabled(false);
        removeBtn.setEnabled(false);
        rootBtn.addActionListener(this);
        addBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        removeAllBtn.addActionListener(this);
        JLabel lab = createLabel("Структуры баз");
        if (editorType == REPORT_EDITOR) {
            lab.setText("   Отчёты");
        } else if (editorType == FILTER_EDITOR) {
            lab.setText("   Фильтры");
        } else if (editorType == NOTE_EDITOR) {
            lab.setText("   Помощь");
        } else if (editorType == MENU_EDITOR) {
            lab.setText("   Меню");
        }
        add(searchPanel, new GridBagConstraints(0, 0, 6, 1, 0, 0, CENTER, BOTH, new Insets(0, 0, 1, 0), 0, 0));
        add(lab, new GridBagConstraints(0, 1, 1, 1, 0, 0, WEST, HORIZONTAL, new Insets(5, 0, 3, 0), 0, 0));
        add(selector, new GridBagConstraints(0, 2, 2, 4, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        if (editorType == FILTER_EDITOR) {
            add(rootBtn, new GridBagConstraints(2, 2, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 5, 0, 5), 0, 0));
        }
        add(addBtn, new GridBagConstraints(2, 3, 1, 1, 0, 0, CENTER, NONE, new Insets(10, 5, 0, 5), 0, 0));
        add(removeBtn, new GridBagConstraints(2, 4, 1, 1, 0, 0, CENTER, NONE, new Insets(10, 5, 0, 5), 0, 0));
        add(removeAllBtn, new GridBagConstraints(2, 5, 1, 1, 0, 0, NORTH, NONE, new Insets(10, 5, 0, 5), 0, 0));
        lab = createLabel("Выбранные структуры баз");
        if (editorType == REPORT_EDITOR) {
            lab.setText("Выбранные отчёты");
        } else if (editorType == FILTER_EDITOR) {
            lab.setText("Выбранные фильтры");
        } else if (editorType == NOTE_EDITOR) {
            lab.setText("Выбранная помощь");
        } else if (editorType == MENU_EDITOR) {
            lab.setText("Выбранные пункты гиперменю");
        }
        add(lab, new GridBagConstraints(3, 1, 1, 1, 0, 0, WEST, HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        JScrollPane sp = new JScrollPane(editorType == REPORT_EDITOR ? table : selectedList);
        sp.setPreferredSize(new Dimension(255, 100));
        add(sp, new GridBagConstraints(3, 2, 2, 4, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        initNode = tree.getSelectedNode();
    }
    
    public void setAddBtnEnabled(boolean isEnabled) {
    	addBtn.setEnabled(isEnabled);
    }

    public void valueChanged(TreeSelectionEvent e) {
        DesignerTreeNode node = (DesignerTreeNode) e.getPath().getLastPathComponent();
        if (node instanceof ServiceControlNode) {
            boolean enabled = false;
            if (node != null && selector.getTreePr() != null) {
                switch (selector.getTypeTree()) {
                case 0:
                    enabled = ((ServiceControlNode) node).isService();
                    break;
                case 1:
                    enabled = ((ServiceControlNode) node).isInterface();
                    break;
                case 2:
                    enabled = ((ServiceControlNode) node).isFilter();
                    break;
                case 3:
                    enabled = ((ServiceControlNode) node).isReport();
                    break;
                case 4:
                    enabled = true;
                    break;
                default:
                    break;
                }
            }
            addBtn.setEnabled(enabled);
        } else {
            addBtn.setEnabled(editorType == REPORT_EDITOR ? !tableModel.exists(node) : !isListExists(node));
        }
//        removeBtn.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == editItem) {
        	int idx = selectedList.getSelectedIndex();
        	DesignerTreeNode n = (DesignerTreeNode) model.get(idx);
        	KrnObject obj = n.getKrnObj();
        	if (obj != null) {
        		final Container cont = getTopLevelAncestor();
        		if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
	    	        Or3Frame.instance().getDesignerFrame().load(obj,null);
	    	    }else {
	    	        Or3Frame.instance().jumpFilter(obj);    	        
	    	    }
	    	    cont.setVisible(false);
        	}
        }
        
        else if (src == viewItem) {
        	int idx = selectedList.getSelectedIndex();
        	DesignerTreeNode n = (DesignerTreeNode) model.get(idx);
        	KrnObject obj = n.getKrnObj();   	
        	if (obj != null || tree != null ) {
        		tree.distantOpenFilter(obj);
        	}
        }
        else if (src == addBtn) {
            DesignerTreeNode[] nodes = null;
            nodes = selector.getSelectedTree().getSelectedNodes();
            FilterNode firstNode = (FilterNode)selector.getSelectedTree().getSelectedNode();
            for (int i = 0; i < nodes.length; i++) {
                FilterNode node = (FilterNode)nodes[i];
                if (editorType != REPORT_EDITOR && (editorType != FILTER_EDITOR || area == -1)) {
                	if(!isListExists(node) && node.isLeaf())
                		model.addElement(node);
                    addBtn.setEnabled(!isListExists(node));
                    if(!firstNode.isLeaf()) {
                    	addBtn.setEnabled(true);
                    }
                } else if (editorType == FILTER_EDITOR) {
                    model.addElement(new FilterRecord(node.getKrnObj(), node.toString()));
                } else {
                    ReportRecord rec = new ReportRecord(node.getKrnObj().id, "", 0, "", "", false);
                    tableModel.addReport(rec);
                }
            }
        }
        else if (src == removeBtn) {
            if (editorType != REPORT_EDITOR) {
                int idxes[] = selectedList.getSelectedIndices();
                for (int i=idxes.length-1;i>-1;i--) {
                	model.remove(idxes[i]);
                }
                selectedList.setSelectedIndex(-1);
                DesignerTreeNode[] nodes = null;
                nodes = selector.getSelectedTree().getSelectedNodes();
                int k = 0;
	                for (int i = 0; i < nodes.length; i++) {
	                	DesignerTreeNode node = nodes[i];
	                	if(!isListExists(node)) k++;
                }
	                if (k>0) addBtn.setEnabled(true);
            } else {
                tableModel.deleteRecord(tableModel.getReportAt(table.getSelectedRow()));
            }
        }
        else if (src == removeAllBtn) {
            if (editorType != REPORT_EDITOR) {
                int count = model.getSize();
                for (int i = count - 1; i >= 0; i--) {
                    model.remove(i);
                }
                DesignerTreeNode[] nodes = null;
                nodes = selector.getSelectedTree().getSelectedNodes();
                if (nodes.length > 0)
                	addBtn.setEnabled(true);
            } else {
                for (int i = table.getRowCount() - 1; i >= 0; i--) {
                    tableModel.deleteRecord(tableModel.getReportAt(i));
                }
            }
        } 
        else if (src == rootBtn) {
            if (rootBtn.isSelected()) {
                if (filterRoot != null) {
                    ((DefaultTreeModel) selector.getSelectedTree().getModel()).setRoot(filterRoot);
                }
            }
        }
        else if (src == searchBtn) {
            searchByUID(searchText.getText());
        } 
        else if (src == copyBtn) {
        	StringSelection selection = new StringSelection(searchText.getText().trim());
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(selection, selection);
        }
        else if (src == copyBtnR) {
        	StringSelection selection = new StringSelection(searchTextR.getText().trim());
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(selection, selection);
        }
        selector.getSelectedTree().repaint();
    }
    
    private void searchByUID(String uid) {
        String message = "";
        final String UID = searchText.getText();
        StringTokenizer st = new StringTokenizer(uid, ".");
        if (st.countTokens() < 2) {
            message = "Неверный формат UID-а!";
            MessagesFactory.showMessageDialog((Dialog) this.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, message);
            return;
        }
     //   tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode) tree.getModel().getRoot()).getPath()));
        Thread t = new Thread(new Runnable() {
            public void run() {
                CursorToolkit.startWaitCursor(MultiEditor.this.getTopLevelAncestor());
                AbstractDesignerTreeNode selNode = tree.getSelectedNode();
                TreeNode fnode = tree.searchByUID(UID);
                if (fnode != null && selNode != fnode) {
                    TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        tree.scrollPathToVisible(path);
                    }
                } else if (selNode == fnode) {
                    MessagesFactory.showMessageSearchFinished(MultiEditor.this.getTopLevelAncestor());
                } else {
                    MessagesFactory.showMessageNotFound(MultiEditor.this.getTopLevelAncestor());
                }
                CursorToolkit.stopWaitCursor(MultiEditor.this.getTopLevelAncestor());
            }
        });
        t.start();
    }

    public void valueChanged(ListSelectionEvent e) {
        Object o = null;
        if (editorType != REPORT_EDITOR) {
            o = selectedList.getSelectedValue();
        }
        removeBtn.setEnabled(o != null);
    }

    public DesignerTreeNode[] getSelectedNodeValues() {
        DesignerTreeNode[] res = null;
        int size = model.getSize();
        if (size > 0) {
            res = new DesignerTreeNode[size];
            for (int i = 0; i < size; i++) {
                res[i] = (DesignerTreeNode) model.getElementAt(i);
            }
        }
        return res;
    }

    public KrnObject[] getSelectedValues() {
        KrnObject[] res = null;
        int size = model.getSize();
        if (size > 0) {
            res = new KrnObject[size];
            for (int i = 0; i < size; i++) {
                res[i] = ((DesignerTreeNode) model.getElementAt(i)).getKrnObj();
            }
        }
        return res;
    }

    public KrnObject[] getOldValue() {
        return oldValue;
    }
    
    public void setSearchText(String uid) {
    	searchText.setText(uid);
    }

    public void setOldValue(KrnObject[] oldValue) {
        this.oldValue = oldValue;
        if (oldValue != null && oldValue.length > 0) {
            for (int i = 0; i < oldValue.length; i++) {
                KrnObject krnObject = oldValue[i];
                DesignerTreeNode n = selector.getSelectedTree().find(krnObject);
                if (n != null) {
                    model.addElement(n);
                }
            }
            selector.getSelectedTree().repaint();
        }
    }

    class MultiEditorTableModel extends AbstractTableModel {

        public final String[] COL_NAMES = { "Отчёт", "Данные", "Фильтр" };

        private ArrayList reports = new ArrayList();

        public boolean exists(DesignerTreeNode node) {
            for (int i = 0; i < reports.size(); i++) {
                ReportRecord reportRecord = (ReportRecord) reports.get(i);
                if (node.getKrnObj().id == reportRecord.getObjId()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return (reports.size() > 0) ? reports.size() : 0;
        }

        public void addReport(ReportRecord rec) {
            reports.add(rec);
            fireTableDataChanged();
        }

        public DesignerTreeNode deleteRecord(ReportRecord rec) {
            try {
                reports.remove(rec);
                DesignerTreeNode node = selector.getSelectedTree().find(getObjectById(rec.getObjId(), 0));
                int idx = table.getSelectedRow();
                fireTableRowsDeleted(idx, idx);
                return node;
            } catch (KrnException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ReportRecord getReportAt(int rowIndex) {
            if (reports.size() > 0) {
                return (ReportRecord) reports.get(rowIndex);
            }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (reports.size() > 0) {
                ReportRecord rep = (ReportRecord) reports.get(rowIndex);
                switch (columnIndex) {
                case 0:
                    return rep.getTitle(DesignerFrame.instance().getInterfaceLang().id);
                case 1:
                    return rep.getPath();
                case 2:
                    return new Long(rep.getFilterId()) + ":" + Utils.getFilterNameById(rep.getFilterId());
                default:
                    return "";
                }
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ReportRecord rec = getReportAt(rowIndex);
            switch (columnIndex) {
            case 1:
                rec.setPath("" + aValue);
                break;
            case 2:
                StringTokenizer st = new StringTokenizer(aValue.toString(), ":");
                if (st.hasMoreTokens()) {
                    rec.setFilterId(new Integer(st.nextToken()).intValue());
                } else {
                    rec.setFilterId(0);
                }
                break;
            }
        }
    }

    public void setOldReportValue(ReportRecord[] oldVals) {
        for (int i = 0; i < oldVals.length; i++) {
            ReportRecord oldVal = oldVals[i];
            tableModel.addReport(oldVal);
        }
    }

    public void setOldFiltersValue(FilterRecord[] oldVals) {
        for (int i = 0; i < oldVals.length; i++) {
            FilterRecord oldVal = oldVals[i];
            model.addElement(oldVal);
        }
        /*
         * FilterRecord fr = oldVals[oldVals.length - 1];
         * if (fr != null) {
         * ((FiltersTree)tree).setSelectedNode(fr.getObjId());
         * }
         */
    }

    public ReportRecord[] getSelectedReportValues() {
        ReportRecord[] res = new ReportRecord[table.getRowCount()];
        for (int i = 0; i < table.getRowCount(); i++) {
            ReportRecord re = tableModel.getReportAt(i);
            res[i] = re;
        }
        return res;
    }

    public FilterRecord[] getSelectedFilterValues() {
        FilterRecord[] res = new FilterRecord[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            FilterRecord re = (FilterRecord) model.getElementAt(i);
            res[i] = re;
        }
        return res;
    }

    private class DataCellEditor extends DefaultCellEditor {

        private JTextField field = createDesignerTextField();

        public DataCellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }

        public boolean stopCellEditing() {
            value = field.getText();
            return super.stopCellEditing();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            MultiEditor.this.value = value;
            field.setLayout(new BorderLayout());
            field.setText("" + value);
            final JButton btn = ButtonsFactory.createEditorButton(ButtonsFactory.DEFAULT_EDITOR);
            final Object val = value;
            if (column == 1) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            ClassBrowser cb = getClassBrowser(val.toString());
                            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите путь", cb);
                            dlg.show();
                            int res = dlg.getResult();
                            if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                                String path = cb.getSelectedPath();
                                field.setText(path);
                                MultiEditor.this.value = path;
                            }
                            stopCellEditing();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else if (column == 2) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            MultiEditor me = new MultiEditor(MultiEditor.FILTER_EDITOR, MultiEditor.INTERFACE_AREA);
                            if (val != null && !"0:".equals(val.toString())) {
                                StringTokenizer st = new StringTokenizer(val.toString(), ":");
                                FilterRecord frec = null;
                                long id = 0;
                                String title = "";
                                if (st.hasMoreTokens()) {
                                    id = Long.parseLong(st.nextToken());
                                    title = st.nextToken();
                                }
                                frec = new FilterRecord(Kernel.instance().getCachedObjectById(id), title);
                                me.setOldFiltersValue(new FilterRecord[] { frec });
                            }
                            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите фильтр", me);
                            dlg.show();
                            int res = dlg.getResult();
                            if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                                long fid = me.getSelectedFilterValues()[0].getObjId();
                                field.setText(fid + ":" + Utils.getFilterNameById(fid));
                                MultiEditor.this.value = new Long(fid);
                            }
                            stopCellEditing();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
            field.add(btn, BorderLayout.EAST);
            return field;
        }

        public Object getCellEditorValue() {
            return MultiEditor.this.value;
        }
    }

    private ClassBrowser getClassBrowser(String value) {
        final Kernel krn = Kernel.instance();
        ClassNode cls = null;
        String s = "";
        try {
            if ("".equals(value)) {
                if ("".equals(defaultClass)) {
                    cls = krn.getClassNodeByName("Объект");
                } else {
                    cls = krn.getClassNodeByName(defaultClass);
                }
            } else {
                try {
                    s = getClassNameFromPath(value.toString());
                    cls = krn.getClassNodeByName(s);
                    defaultClass = s;
                } catch (KrnException e) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, "\"" + s
                            + "\" - ошибочное имя класса!");
                }
            }
            classBrowser = new ClassBrowser(cls, true);
            classBrowser.setPreferredSize(new Dimension(800, 500));
            if (lastPath != null && !"".equals(lastPath)) {
                classBrowser.setSelectedPath(lastPath);
            }
            return classBrowser;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getClassNameFromPath(String path) {
        StringTokenizer st = new StringTokenizer(path, ".");
        String s = st.nextToken();
        return s;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public boolean isListExists(DesignerTreeNode node) {
        for (int i = 0; i < model.size(); i++) {
            DesignerTreeNode n = (DesignerTreeNode) model.get(i);
            if(node != null )
            if (node.getKrnObj().id == n.getKrnObj().id) {
                return true;
            }
        }
        return false;
    }

    public void setOldFilterNode(long oldFilterNode) {
        this.oldFilterId = oldFilterNode;
        selector.getSelectedTree().setSelectedNode(oldFilterId);
    }

    public long getOldFilterNode() {
        return oldFilterId;
    }

    public DesignerTree getTree() {
        return selector.getSelectedTree();
    }

}