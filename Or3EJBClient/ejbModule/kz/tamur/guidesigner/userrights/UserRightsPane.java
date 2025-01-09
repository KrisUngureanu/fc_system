package kz.tamur.guidesigner.userrights;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.*;
import com.cifs.or2.util.Funcs;

import kz.tamur.comps.ui.checkBox.OrBasicCheckBox;
import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.hypers.HyperTree;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.ods.Value;
import kz.tamur.or3.client.props.inspector.CellBorder;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.DesignerTreeNode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static kz.tamur.comps.Utils.getUserTree;
import static kz.tamur.comps.Utils.getCenterLocationPoint;
import static kz.tamur.comps.Utils.createDesignerToolBar;
import static kz.tamur.rt.Utils.getImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 27.04.2005
 * Time: 17:17:15
 * To change this template use File | Settings | File Templates.
 */
public class UserRightsPane extends JPanel implements ActionListener, ListSelectionListener {
    private static Icon trueIcon;
    private static Icon falseIcon;

    static {
        OrBasicCheckBox chb = new OrBasicCheckBox();
        chb.setAnimate(false);
        chb.setOpaque(false);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setBounds(0, 0, 20, 16);
        chb.paint(img.createGraphics());
        falseIcon = new ImageIcon(img);

        img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setSelected(true);
        chb.paint(img.createGraphics());
        trueIcon = new ImageIcon(img);
    }

    /** Главная панель */
	private JPanel taskPane=new JPanel(new BorderLayout());
    
	/** Таблица с правами доступа */
	private JTable rightsTable;
	private RightsTableModel model;
    private JScrollPane tablePane;

	private JTable table1;
	private ChildTableModel model1;
	private JTable table2;
	private ChildTableModel model2;
	private JTable table3;
	private ChildTableModel model3;
	private JTable table4;
	private ChildTableModel model4;

    /** Разделитель между таблицей и деталями права доступа */
    private JSplitPane basicSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /** Главное меню */
    private JToolBar toolBar = createDesignerToolBar();
    private JToolBar toolBarItem = createDesignerToolBar();
    
    private JButton newBtn = ButtonsFactory.createToolButton("Shed", "Создать новое право доступа");
    private JButton saveBtn = ButtonsFactory.createToolButton("Save", "Сохранить всё");
    private JButton delBtn = ButtonsFactory.createToolButton("ShedDel", "Удалить право доступа");
    
    private JButton userBtn = ButtonsFactory.createToolButton("userNode","Выберите пользователя");
    private JButton procesBtn = ButtonsFactory.createToolButton("ServiceTab", "Выберите процесс");
    private JButton archBtn = ButtonsFactory.createToolButton("ServiceTab", "Выберите архив");
    private JButton dictBtn = ButtonsFactory.createToolButton("ServiceTab", "Выберите НСИ");

    private JButton userDelBtn = ButtonsFactory.createToolButton("Delete","Удалить пользователя");
    private JButton procDelBtn = ButtonsFactory.createToolButton("Delete","Удалить процесс");
    private JButton archDelBtn = ButtonsFactory.createToolButton("Delete","Удалить архив");
    private JButton dictDelBtn = ButtonsFactory.createToolButton("Delete","Удалить НСИ");

    private JPanel itemPane=new JPanel(new BorderLayout());
    
    private JPanel schedulerPane=new JPanel();
    
    private JPanel usersPane = new JPanel();
    private JPanel processPane = new JPanel();
    private JPanel archPane=new JPanel();
    private JPanel dictPane=new JPanel();
    
    private HashMap<Long, RightObject> rights = new HashMap<Long, RightObject>();
    private List<ChildObject> actions = new ArrayList<ChildObject>();
    private Kernel krn = Kernel.instance();
    private KrnClass cls_folder;
    private KrnAttribute attr_child;
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public UserRightsPane() {
        super(new BorderLayout());
        load();
        init();
    }

    private void init() {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.USER_RIGHT_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.USER_RIGHT_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.USER_RIGHT_CREATE_RIGHT);

        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBar.add(newBtn);
        toolBar.add(delBtn);
        
        toolBarItem.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBarItem.add(saveBtn);
        toolBarItem.add(userBtn);
        toolBarItem.add(procesBtn);
        toolBarItem.add(archBtn);
        toolBarItem.add(dictBtn);
        toolBarItem.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBarItem.add(userDelBtn);
        toolBarItem.add(procDelBtn);
        toolBarItem.add(archDelBtn);
        toolBarItem.add(dictDelBtn);
        
    	model = new RightsTableModel(rights.values());
        rightsTable = new RightsTable(model);
        rightsTable.getSelectionModel().addListSelectionListener(this);

        rightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });

        rightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = rightsTable.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn tc = rightsTable.getColumnModel().getColumn(i);
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            tc.setHeaderRenderer(r);
            if (i == model.getSortColumn())
                r.setIcon(model.getColumnIcon(i));
        }

        rightsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rightsTable.getColumnModel().getColumn(0).setPreferredWidth(450);
        rightsTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        rightsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        rightsTable.getColumnModel().getColumn(3).setPreferredWidth(400);
        rightsTable.getColumnModel().getColumn(4).setPreferredWidth(30);
        rightsTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        
        tablePane = new JScrollPane(rightsTable);
        taskPane.add(tablePane,BorderLayout.CENTER);
        basicSplit.setLeftComponent(taskPane);
        basicSplit.setRightComponent(itemPane);
        
        taskPane.add(toolBar, BorderLayout.NORTH);
        itemPane.add(toolBarItem, BorderLayout.NORTH);
        
        add(basicSplit,BorderLayout.CENTER);
        
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());

        schedulerPane.setLayout(new GridLayout(1, 4, 0, 0));
        
    	model1 = new ChildTableModel();
        table1 = new JTable(model1);
        table1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane sp1 = new JScrollPane(table1);
        sp1.setBorder(Utils.createTitledBorder(b, "Пользователи"));
        {
        	DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            table1.getColumnModel().getColumn(0).setHeaderRenderer(r);
            r.setIcon(model1.getColumnIcon());
        }        
        JTableHeader header1 = table1.getTableHeader();
        header1.addMouseListener(new childTableColumnListener(table1, model1));
        
    	model2 = new ChildTableModel();
        table2 = new JTable(model2);
        table2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane sp2 = new JScrollPane(table2);
        sp2.setBorder(Utils.createTitledBorder(b, "Доступные процессы"));
        {
        	DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            table2.getColumnModel().getColumn(0).setHeaderRenderer(r);
            r.setIcon(model2.getColumnIcon());
        }
        JTableHeader header2 = table2.getTableHeader();
        header2.addMouseListener(new childTableColumnListener(table2, model2));
        

    	model3 = new ChildTableModel();
        table3 = new JTable(model3);
        table3.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane sp3 = new JScrollPane(table3);
        sp3.setBorder(Utils.createTitledBorder(b, "Доступные архивы"));
        {
        	DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            table3.getColumnModel().getColumn(0).setHeaderRenderer(r);
            r.setIcon(model3.getColumnIcon());
        }
        JTableHeader header3 = table3.getTableHeader();
        header3.addMouseListener(new childTableColumnListener(table3, model3));
        

    	model4 = new ChildTableModel();
        table4 = new JTable(model4);
        table4.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane sp4 = new JScrollPane(table4);
        sp4.setBorder(Utils.createTitledBorder(b, "Доступные НСИ"));
        {
        	DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setBackground(Utils.getLightGraySysColor());
            table4.getColumnModel().getColumn(0).setHeaderRenderer(r);
            r.setIcon(model4.getColumnIcon());
        }
        JTableHeader header4 = table4.getTableHeader();
        header4.addMouseListener(new childTableColumnListener(table4, model4));
        

        schedulerPane.add(sp1);
        schedulerPane.add(sp2);
        schedulerPane.add(sp3);
        schedulerPane.add(sp4);
        //JScrollPane schedPane = new JScrollPane(schedulerPane);
        itemPane.add(schedulerPane,BorderLayout.CENTER);
        newBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        delBtn.addActionListener(this);
        userBtn.addActionListener(this);
        procesBtn.addActionListener(this);
        archBtn.addActionListener(this);
        dictBtn.addActionListener(this);
        userDelBtn.addActionListener(this);
        procDelBtn.addActionListener(this);
        archDelBtn.addActionListener(this);
        dictDelBtn.addActionListener(this);
        saveBtn.setEnabled(false);
        newBtn.setEnabled(canCreate);
        delBtn.setEnabled(false);
        userBtn.setEnabled(false);
        procesBtn.setEnabled(false);
        archBtn.setEnabled(false);
        dictBtn.setEnabled(false);
        userDelBtn.setEnabled(false);
        procDelBtn.setEnabled(false);
        archDelBtn.setEnabled(false);
        dictDelBtn.setEnabled(false);

        setOpaque(isOpaque);
        
        sp1.setOpaque(isOpaque);
        sp2.setOpaque(isOpaque);
        sp3.setOpaque(isOpaque);
        sp4.setOpaque(isOpaque);
        
        rightsTable.setOpaque(isOpaque);
        table1.setOpaque(isOpaque);
        tablePane.setOpaque(isOpaque);
        tablePane.getViewport().setOpaque(isOpaque);
        schedulerPane.setOpaque(isOpaque);
        taskPane.setOpaque(isOpaque);
        itemPane.setOpaque(isOpaque);
        basicSplit.setOpaque(isOpaque);
    }
    public void load(){
        try{
        	KrnClass cls = krn.getClassByName("SystemRight");
        	KrnClass userCls = krn.getClassByName("User");
        	KrnClass hyperCls = krn.getClassByName("HiperTree");
        	KrnClass procCls = krn.getClassByName("ProcessDef");
        	KrnObject[] objs = krn.getClassObjects(cls, 0);
        	long[] objIds = Funcs.makeObjectIdArray(objs);
        	KrnClass actCls = krn.getClassByName("SystemAction");
        	
        	AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn).add("action", new AttrRequestBuilder(actCls, krn).add("code").add("name"))
        			.add("name").add("description").add("block").add("deny").add("userOrRole").add("процесс").add("архив").add("НСИ");
        	
        	List<Object[]> rows = krn.getObjects(objIds, arb.build(), 0);
        
        	for (Object[] row : rows) {
        		KrnObject right = (KrnObject)row[0];
        		
        		KrnObject aObj = (KrnObject) arb.getValue("action", row);
        		ChildObject action = null;
                if (aObj != null) {
                    long code = arb.getLongValue("action.code", row);
                    String name = arb.getStringValue("action.name", row);
                    action = new ChildObject(aObj, (int)code, name);
                }
                String name = arb.getStringValue("name", row);
                String desc = arb.getStringValue("description", row);
                boolean blocked = arb.getBooleanValue("block", row);
                boolean denied = arb.getBooleanValue("deny", row);

                List<ChildObject> userOrRoles = null;
                List<Value> list = (List<Value>) arb.getValue("userOrRole", row);
                if (list != null && list.size() > 0) {
                	long[] userIds = new long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                    	userIds[i] = ((KrnObject) list.get(i).value).id;
                    }
                    
                    userOrRoles = new ArrayList<ChildObject>(list.size());
                	AttrRequestBuilder arb2 = new AttrRequestBuilder(userCls, krn).add("name");
                	List<Object[]> rows2 = krn.getObjects(userIds, arb2.build(), 0);
                	for (Object[] row2 : rows2) {
                		KrnObject user = (KrnObject)row2[0];
                		
                		String uName = arb2.getStringValue("name", row2);
                		userOrRoles.add(new ChildObject(user, uName));
                	}
                }

                List<ChildObject> procs = null;
                list = (List<Value>) arb.getValue("процесс", row);
                if (list != null) {
                	long[] procIds = new long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                    	procIds[i] = ((KrnObject) list.get(i).value).id;
                    }

                    procs = new ArrayList<ChildObject>(list.size());
                	AttrRequestBuilder arb2 = new AttrRequestBuilder(procCls, krn).add("title");
                	List<Object[]> rows2 = krn.getObjects(procIds, arb2.build(), 0);
                	for (Object[] row2 : rows2) {
                		KrnObject user = (KrnObject)row2[0];
                		
                		String uName = arb2.getStringValue("title", row2);
                		procs.add(new ChildObject(user, uName));
                	}
                }

                List<ChildObject> archs = null;
                list = (List<Value>) arb.getValue("архив", row);
                if (list != null) {
                	long[] archIds = new long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                    	archIds[i] = ((KrnObject) list.get(i).value).id;
                    }
                    
                    archs = new ArrayList<ChildObject>(list.size());
                	AttrRequestBuilder arb2 = new AttrRequestBuilder(hyperCls, krn).add("title");
                	List<Object[]> rows2 = krn.getObjects(archIds, arb2.build(), 0);
                	for (Object[] row2 : rows2) {
                		KrnObject user = (KrnObject)row2[0];
                		
                		String uName = arb2.getStringValue("title", row2);
                		archs.add(new ChildObject(user, uName));
                	}
                }

                List<ChildObject> dicts = null;
                list = (List<Value>) arb.getValue("НСИ", row);
                if (list != null) {
                	long[] dictIds = new long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                    	dictIds[i] = ((KrnObject) list.get(i).value).id;
                    }

                    dicts = new ArrayList<ChildObject>(list.size());
                	AttrRequestBuilder arb2 = new AttrRequestBuilder(hyperCls, krn).add("title");
                	List<Object[]> rows2 = krn.getObjects(dictIds, arb2.build(), 0);
                	for (Object[] row2 : rows2) {
                		KrnObject user = (KrnObject)row2[0];
                		
                		String uName = arb2.getStringValue("title", row2);
                		dicts.add(new ChildObject(user, uName));
                	}
                }
                
                RightObject ro = new RightObject(right, action, name, desc, blocked, denied, userOrRoles, procs, archs, dicts);
                rights.put(right.id, ro);
        	}
        	
        	if (rightsTable != null)
        		rightsTable.invalidate();
        	
        	arb = new AttrRequestBuilder(actCls, krn).add("code").add("name");
        	
        	rows = krn.getClassObjects(actCls, arb.build(), new long[0], new int[] {0}, 0);
        
        	for (Object[] row : rows) {
        		KrnObject aObj = (KrnObject)row[0];
        		ChildObject action = null;
                if (aObj != null) {
                    long code = arb.getLongValue("code", row);
                    String name = arb.getStringValue("name", row);
                    action = new ChildObject(aObj, (int)code, name);
                    
                    actions.add(action);
                }
        	}
            Collections.sort(actions, new ActionComparator());
        } catch (KrnException e){
            e.printStackTrace();
        }
    }

    private int[] getStrToIntArray(String str,int shift){
        StringTokenizer st= new StringTokenizer(str,",",false);
        int[] res=new int[st.countTokens()];
        int i=0;
        while(st.hasMoreTokens()){
            String st_=st.nextToken();
            res[i++]= Integer.valueOf(st_.trim()) +shift;
        }
        Arrays.sort(res);
        return res;
    }

    private String getObjArrayToStr(Object[] obj) {
        String res="";
        if(obj==null ||obj.length==0)return res;
        res=obj[0].toString().trim();
        for(int i=1;i<obj.length;++i){
            res+=","+obj[i].toString().trim();
        }
        return res;
    }

    public void setSaveEnabled(boolean isEnabled) {
        saveBtn.setEnabled(isEnabled && canEdit);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == newBtn) {
            create();
        } else if (src == delBtn) {
            delete();
        } else if (src == saveBtn) {
            save();
            saveBtn.setEnabled(false);
        } else if (src == userBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            RightObject ro = model.getRowObject(sel);
            UserTree tree = getUserTree(true);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                        "Выбор пользователя", new JScrollPane(tree));
            dlg.setSize(700,550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                DesignerTreeNode[] nodes = tree.getOnlySelectedNodes();
                if(nodes != null) {
            		List<ChildObject> cos = ro.getUserOrRoles();
                	for (DesignerTreeNode node : nodes) {
                		boolean contains = false;
                		for (ChildObject co : cos) {
                			if (co.getObj().id == node.getKrnObj().id) {
                				contains = true;
                				break;
                			}
                		}
                		if (!contains)
                			cos.add(new ChildObject(node.getKrnObj(), node.toString()));
                	}
            		userDelBtn.setEnabled(canEdit && cos.size() > 0);
                    ro.setChanged(true);
                	setSaveEnabled(true);
                	model1.setData(cos);
                	table1.repaint();
                }
            }
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        } else if (src == procesBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            
            ServicesTree tree = kz.tamur.comps.Utils.getServicesTree();
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                        "Выбор процесса", new JScrollPane(tree));
            dlg.setSize(700,550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                DesignerTreeNode[] nodes = tree.getOnlySelectedNodes();
                RightObject ro = model.getRowObject(sel);
                if(nodes != null) {
            		List<ChildObject> cos = ro.getProcs();
                	for (DesignerTreeNode node : nodes) {
                		boolean contains = false;
                		for (ChildObject co : cos) {
                			if (co.getObj().id == node.getKrnObj().id) {
                				contains = true;
                				break;
                			}
                		}
                		if (!contains)
                			cos.add(new ChildObject(node.getKrnObj(), node.toString()));
                	}
                    ro.setChanged(true);
                	setSaveEnabled(true);
            		procDelBtn.setEnabled(canEdit && cos.size() > 0);
                	model2.setData(cos);
                	table2.repaint();
                }
            }
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        } else if (src == archBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            HyperTree tree = kz.tamur.comps.Utils.getHyperTree();
            tree.setRootVisible(false);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                        "Выбор архива", new JScrollPane(tree));
            dlg.setSize(700,550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                DesignerTreeNode[] nodes = tree.getOnlySelectedNodes();
                RightObject ro = model.getRowObject(sel);
                if(nodes != null) {
            		List<ChildObject> cos = ro.getArchs();
                	for (DesignerTreeNode node : nodes) {
                		boolean contains = false;
                		for (ChildObject co : cos) {
                			if (co.getObj().id == node.getKrnObj().id) {
                				contains = true;
                				break;
                			}
                		}
                		if (!contains)
                			cos.add(new ChildObject(node.getKrnObj(), node.toString()));
                	}
            		archDelBtn.setEnabled(canEdit && cos.size() > 0);
                    ro.setChanged(true);
                	setSaveEnabled(true);
                	model3.setData(cos);
                	table3.repaint();
                }
            }
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        } else if (src == dictBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            HyperTree tree = kz.tamur.comps.Utils.getHyperTree();
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            tree.setRootVisible(false);
            DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(),
                        "Выбор НСИ", new JScrollPane(tree));
            dlg.setSize(700,550);
            dlg.setLocation(getCenterLocationPoint(700, 550));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                DesignerTreeNode[] nodes = tree.getOnlySelectedNodes();
                RightObject ro = model.getRowObject(sel);
                if(nodes != null) {
            		List<ChildObject> cos = ro.getDicts();
                	for (DesignerTreeNode node : nodes) {
                		boolean contains = false;
                		for (ChildObject co : cos) {
                			if (co.getObj().id == node.getKrnObj().id) {
                				contains = true;
                				break;
                			}
                		}
                		if (!contains)
                			cos.add(new ChildObject(node.getKrnObj(), node.toString()));
                	}
            		dictDelBtn.setEnabled(canEdit && cos.size() > 0);
                    ro.setChanged(true);
                	setSaveEnabled(true);
                	model4.setData(cos);
                	table4.repaint();
                }
            }
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        } else if (src == userDelBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            int sel1 = table1.getSelectedRow();
            if (sel1<0) return;

            RightObject ro = model.getRowObject(sel);
    		List<ChildObject> cos = ro.getUserOrRoles();
    		ChildObject co = model1.getRowObject(sel1);
    		model1.removeData(co);
    		cos.remove(co);
    		userDelBtn.setEnabled(canEdit && cos.size() > 0);
    		
            ro.setChanged(true);
        	setSaveEnabled(true);
        	model1.setData(cos);
        	table1.repaint();
            if (sel1 >= model1.getRowCount()) sel1 = model1.getRowCount() - 1;
            if (sel1 > -1)
            	table1.getSelectionModel().setSelectionInterval(sel1, sel1);
        } else if (src == procDelBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            int sel1 = table2.getSelectedRow();
            if (sel1<0) return;

            RightObject ro = model.getRowObject(sel);
    		List<ChildObject> cos = ro.getProcs();
    		ChildObject co = model2.getRowObject(sel1);
    		model2.removeData(co);
    		cos.remove(co);
    		procDelBtn.setEnabled(canEdit && cos.size() > 0);
    		
            ro.setChanged(true);
        	setSaveEnabled(true);
        	model2.setData(cos);
        	table2.repaint();
            if (sel1 >= model2.getRowCount()) sel1 = model2.getRowCount() - 1;
            if (sel1 > -1)
            	table2.getSelectionModel().setSelectionInterval(sel1, sel1);
        } else if (src == archDelBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            int sel1 = table3.getSelectedRow();
            if (sel1<0) return;

            RightObject ro = model.getRowObject(sel);
    		List<ChildObject> cos = ro.getArchs();
    		ChildObject co = model3.getRowObject(sel1);
    		model3.removeData(co);
    		cos.remove(co);
    		archDelBtn.setEnabled(canEdit && cos.size() > 0);
    		
            ro.setChanged(true);
        	setSaveEnabled(true);
        	model3.setData(cos);
        	table3.repaint();

            if (sel1 >= model3.getRowCount()) sel1 = model3.getRowCount() - 1;
            if (sel1 > -1)
            	table3.getSelectionModel().setSelectionInterval(sel1, sel1);
        } else if (src == dictDelBtn) {
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            int sel1 = table4.getSelectedRow();
            if (sel1<0) return;

            RightObject ro = model.getRowObject(sel);
    		List<ChildObject> cos = ro.getDicts();
    		ChildObject co = model4.getRowObject(sel1);
    		model4.removeData(co);
    		cos.remove(co);
    		dictDelBtn.setEnabled(canEdit && cos.size() > 0);
    		
            ro.setChanged(true);
        	setSaveEnabled(true);
        	model4.setData(cos);
        	table4.repaint();
        	
            if (sel1 >= model4.getRowCount()) sel1 = model4.getRowCount() - 1;
            if (sel1 > -1)
            	table4.getSelectionModel().setSelectionInterval(sel1, sel1);
        }
    }

    private void create() {
        try{
            KrnClass cls = krn.getClassByName("SystemRight");
            KrnObject obj = krn.createObject(cls, 0);
            ChildObject action = new ChildObject(null, -1, "");
            RightObject ro = new RightObject(obj, action, "Новое право доступа", "", false, false, null, null, null, null);
            
            model.addData(ro);
            int i = model.getRowCount() - 1;
            rightsTable.getSelectionModel().setSelectionInterval(i, i);
            rightsTable.scrollRectToVisible(rightsTable.getCellRect(i, 0, false));
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void delete() {
        try{
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            int result = MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(),
                    MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите удалить запись?");
            if (result != ButtonsFactory.BUTTON_YES) return;

            RightObject ro = model.getRowObject(sel);
            krn.deleteObject(ro.getObj(), 0);
            
            model.removeData(ro);
            
            if (sel >= model.getRowCount()) sel = model.getRowCount() - 1;
            if (sel > -1)
            	rightsTable.getSelectionModel().setSelectionInterval(sel, sel);

        }catch (KrnException ex){
            ex.printStackTrace();
        }
    }

    private void save() {
        try{
            int sel = rightsTable.getSelectedRow();
            if (sel<0) return;
            
            RightObject ro = model.getRowObject(sel);
            krn.setString(ro.getObj().id, ro.getObj().classId, "name", 0, krn.getInterfaceLanguage().id, ro.getName(), 0);
            krn.setString(ro.getObj().id, ro.getObj().classId, "description", 0, krn.getInterfaceLanguage().id, ro.getDesc(), 0);

            krn.setLong(ro.getObj().id, ro.getObj().classId, "block", 0, ro.isBlocked() ? 1 : 0, 0);
            krn.setLong(ro.getObj().id, ro.getObj().classId, "deny", 0, ro.isDenied() ? 1 : 0, 0);

            if (ro.getAction() == null)
            	krn.deleteValue(ro.getObj().id, ro.getObj().classId, "action", new int[] {0}, 0);
            else
            	krn.setObject(ro.getObj().id, ro.getObj().classId, "action", 0, ro.getAction().getObj().id, 0, false);
            
            KrnClass cls = krn.getClass(ro.getObj().classId);
            KrnAttribute attr = krn.getAttributeByName(cls, "userOrRole");
            Set<Value> vals = krn.getValues(new long[] {ro.getObj().id}, attr.id, 0, 0);
            int[] indexes = new int[vals.size()];
            int i = 0;
            for (Value val : vals) {
            	indexes[i++] = val.index;
            }
        	krn.deleteValue(ro.getObj().id, attr.id, indexes, 0);
            List<ChildObject> cos = ro.getUserOrRoles();
            for (i=0; i<cos.size(); i++) {
            	krn.setObject(ro.getObj().id, attr.id, i, cos.get(i).getObj().id, 0, false);
            }
            
            attr = krn.getAttributeByName(cls, "процесс");
            vals = krn.getValues(new long[] {ro.getObj().id}, attr.id, 0, 0);
            if (vals != null && vals.size() > 0) {
	            indexes = new int[vals.size()];
	            i = 0;
	            for (Value val : vals) {
	            	indexes[i++] = val.index;
	            }
	        	krn.deleteValue(ro.getObj().id, attr.id, indexes, 0);
	        }
            cos = ro.getProcs();
            for (i=0; i<cos.size(); i++) {
            	krn.setObject(ro.getObj().id, attr.id, i, cos.get(i).getObj().id, 0, false);
            }
            
            attr = krn.getAttributeByName(cls, "архив");
            vals = krn.getValues(new long[] {ro.getObj().id}, attr.id, 0, 0);
            if (vals != null && vals.size() > 0) {
	            indexes = new int[vals.size()];
	            i = 0;
	            for (Value val : vals) {
	            	indexes[i++] = val.index;
	            }
	        	krn.deleteValue(ro.getObj().id, attr.id, indexes, 0);
            }
            cos = ro.getArchs();
            for (i=0; i<cos.size(); i++) {
            	krn.setObject(ro.getObj().id, attr.id, i, cos.get(i).getObj().id, 0, false);
            }

            attr = krn.getAttributeByName(cls, "НСИ");
            vals = krn.getValues(new long[] {ro.getObj().id}, attr.id, 0, 0);
            if (vals != null && vals.size() > 0) {
            	indexes = new int[vals.size()];
	            i = 0;
	            for (Value val : vals) {
	            	indexes[i++] = val.index;
	            }
	        	krn.deleteValue(ro.getObj().id, attr.id, indexes, 0);
            }
            cos = ro.getDicts();
            for (i=0; i<cos.size(); i++) {
            	krn.setObject(ro.getObj().id, attr.id, i, cos.get(i).getObj().id, 0, false);
            }

            ro.setChanged(false);
        }catch (KrnException ex){
            ex.printStackTrace();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;
        Object src=e.getSource();
        if(src instanceof ListSelectionModel) {
        	ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        if (!lsm.isSelectionEmpty()) {
	            int selectedRow = lsm.getMinSelectionIndex();
	            RightObject ro = model.getRowObject(selectedRow);
	            model1.setData(ro.getUserOrRoles());
	            table1.repaint();
	            model2.setData(ro.getProcs());
	            table2.invalidate();
	            model3.setData(ro.getArchs());
	            table3.invalidate();
	            model4.setData(ro.getDicts());
	            table4.invalidate();
	            
	            delBtn.setEnabled(canDelete);
	            userBtn.setEnabled(canEdit && ro.getActionCode() > -1);
	            procesBtn.setEnabled(canEdit && (ro.getActionCode() == SystemAction.ACTION_START_PROCESS.getCode() || ro.getActionCode() == SystemAction.ACTION_STOP_PROCESS.getCode()));
	            archBtn.setEnabled(canEdit && ro.getActionCode() == SystemAction.ACTION_VIEW_ARCHIVE.getCode());
	            dictBtn.setEnabled(canEdit && ro.getActionCode() == SystemAction.ACTION_EDIT_DICTIONARY.getCode());

	            userDelBtn.setEnabled(canEdit && ro.getUserOrRoles().size() > 0);
	            procDelBtn.setEnabled(canEdit && ro.getProcs().size() > 0);
	            archDelBtn.setEnabled(canEdit && ro.getArchs().size() > 0);
	            dictDelBtn.setEnabled(canEdit && ro.getDicts().size() > 0);

	            setSaveEnabled(ro.isChanged());
	        } else
	        	saveBtn.setEnabled(false);
        }
    }

    public void placeDivider() {
        basicSplit.setDividerLocation(0.3);
        validate();

    }

    public int processExit() {
        if (saveBtn.isEnabled()) {
            int sel = rightsTable.getSelectedRow();
            if(sel==-1) return ButtonsFactory.BUTTON_NOACTION;
            String fNames = rightsTable.getValueAt(sel,0).toString();
            String mess = "Задание: \n\"" + fNames + "\"\nбыло модифицировано! Сохранить изменения?";
            int res=MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, mess);
            if (res == ButtonsFactory.BUTTON_YES) {
                save();
                return res;
            } else {
                return res;
            }
        }
        return ButtonsFactory.BUTTON_NOACTION;
    }

    class TaskTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            setText((value != null) ? value.toString() : "");
            setFont(Utils.getDefaultFont());
            switch(column) {
                case 1:
                    if (value != null &&
                            value.toString().equals("Не выполнять")) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("TaskNo"));
                    } else if (value != null && !value.toString().equals("")){
                        setIcon(kz.tamur.rt.Utils.getImageIcon("TaskOk"));
                    }else setIcon(null);

                    break;
                case 2:
                    if (value != null && !value.toString().equals("")){
                    setIcon(kz.tamur.rt.Utils.getImageIcon("userNode"));
                    }else setIcon(null);

                    break;
            }
//            setOpaque(true);
            if (isSelected) {
                setBackground(Utils.getSysColor());
            } else {
                setBackground(Color.white);
            }
            return this;
        }
    }
    
    private static final ImageIcon SORT_UP = getImageIcon("SortUpLight");
    private static final ImageIcon SORT_DOWN = getImageIcon("SortDownLight");

    private class RightsTableModel extends AbstractTableModel {
        private final String[] COL_NAMES = {"Наименование", "Описание", "Действие", "Пользователи", "Блок", "Запрет"};
        private boolean isSortAsc = false;
        private int sortColumn = 0;

        java.util.List<RightObject> rights;

        public RightsTableModel(Collection<RightObject> rights) {
            int size = rights != null ? rights.size() : 0;
            this.rights = new ArrayList<RightObject>(size);
            this.rights.addAll(rights);
            sortData();
        }
        
        public void addData(RightObject ro) {
            this.rights.add(ro);
            fireTableRowsInserted(this.rights.size() - 1, this.rights.size() - 1);
        }

        public void removeData(RightObject ro) {
            int i = this.rights.indexOf(ro);
            this.rights.remove(i);
            fireTableRowsDeleted(i, i);
        }

        public int getRowCount() {
            return rights.size();
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public String getColumnName(int columnIndex) {
            return COL_NAMES[columnIndex];
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                return String.class;
            case 4:
            case 5:
                return Boolean.class;
            }
            return null;
        }
        
        public RightObject getRowObject(int rowIndex) {
        	return rights.get(rowIndex);
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                return rights.get(rowIndex).getName();
            case 1:
                return rights.get(rowIndex).getDesc();
            case 2:
                return rights.get(rowIndex).getActionName();
            case 3:
                return rights.get(rowIndex).getUserOrRolesString();
            case 4:
                return rights.get(rowIndex).isBlocked();
            case 5:
                return rights.get(rowIndex).isDenied();
            }
            return null;
        }
        
        @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                rights.get(rowIndex).setName((String)aValue);
                break;
            case 1:
                rights.get(rowIndex).setDesc((String)aValue);
                break;
            case 2:
            	int index = ((Number)aValue).intValue();
            	ChildObject action = index > 0 ? actions.get(index - 1) : null;
                rights.get(rowIndex).setAction(action);
                break;
            case 4:
                rights.get(rowIndex).setBlocked((Boolean)aValue);
                break;
            case 5:
                rights.get(rowIndex).setDenied((Boolean)aValue);
                break;
            }
            rights.get(rowIndex).setChanged(true);
            setSaveEnabled(true);
            fireTableRowsUpdated(rowIndex, rowIndex);
            
            userBtn.setEnabled(canEdit && rights.get(rowIndex).getActionCode() > -1);
            procesBtn.setEnabled(canEdit && (rights.get(rowIndex).getActionCode() == SystemAction.ACTION_START_PROCESS.getCode() || rights.get(rowIndex).getActionCode() == SystemAction.ACTION_STOP_PROCESS.getCode()));
            archBtn.setEnabled(canEdit && rights.get(rowIndex).getActionCode() == SystemAction.ACTION_VIEW_ARCHIVE.getCode());
            dictBtn.setEnabled(canEdit && rights.get(rowIndex).getActionCode() == SystemAction.ACTION_EDIT_DICTIONARY.getCode());

            userDelBtn.setEnabled(canEdit && rights.get(rowIndex).getUserOrRoles().size() > 0);
            procDelBtn.setEnabled(canEdit && rights.get(rowIndex).getProcs().size() > 0);
            archDelBtn.setEnabled(canEdit && rights.get(rowIndex).getArchs().size() > 0);
            dictDelBtn.setEnabled(canEdit && rights.get(rowIndex).getDicts().size() > 0);
		}

        @Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
                return true;
            }
            return false;
		}

		public boolean isSortAsc() {
            return isSortAsc;
        }

        public void setSortAsc(boolean sortAsc) {
            isSortAsc = sortAsc;
        }

        public int getSortColumn() {
            return sortColumn;
        }

        public void setSortColumn(int sortColumn) {
            this.sortColumn = sortColumn;
        }

        public Icon getColumnIcon(int column) {
            if (column == sortColumn) {
                return isSortAsc ? SORT_UP : SORT_DOWN;
            }
            return null;
        }

        public void sortData() {
            Collections.sort(rights, new RightsComparator(sortColumn, isSortAsc));
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
            //setCounterText();
        }
    }
    
    class childTableColumnListener extends MouseAdapter {
    	
    	JTable table;
    	ChildTableModel model;
    	public childTableColumnListener(JTable table, ChildTableModel model) {
    		this.table = table;
    		this.model = model;
    	}
    	
		public void mouseClicked(MouseEvent e) {
			TableColumnModel colModel = table.getColumnModel(); 
			model.setSortAsc(!model.isSortAsc);
			JLabel renderer = (JLabel) colModel.getColumn(0).getHeaderRenderer();
			renderer.setIcon(model.getColumnIcon());
			model.sortData();
			table.repaint();
		}    	
    }

    class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = rightsTable.getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
            if (modelIndex < 0) {
                return;
            }
            if (model.getSortColumn() == modelIndex) {
                model.setSortAsc(!model.isSortAsc());
            } else {
                model.setSortColumn(modelIndex);
            }
            for (int i = 0; i < model.getColumnCount(); i++) {
                TableColumn column = colModel.getColumn(i);
                int index = column.getModelIndex();
                JLabel renderer = (JLabel) column.getHeaderRenderer();
                renderer.setIcon(model.getColumnIcon(index));
            }
            rightsTable.getTableHeader().repaint();
            model.sortData();
            rightsTable.tableChanged(new TableModelEvent(model));
            repaint();
        }
    }
    
    class ChildObjComparator implements Comparator<ChildObject> {
    	protected boolean isSortAsc;
    	
    	public ChildObjComparator(boolean sortAsc) {
    		isSortAsc = sortAsc;
    	}
    	
		public int compare(ChildObject o1, ChildObject o2) {
			int res = 0;
			if(o1 == null)
				res = -1;
			else if (o2 == null)
				res = 1;
			else {
				res = o1.getTitle().compareTo(o2.getTitle());
			}
			
			if(!isSortAsc)
				res = -res;
			return res;
		}
    }

    class RightsComparator implements Comparator<RightObject> {

        protected int sortColumn;
        protected boolean isSortAsc;

        public RightsComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(RightObject u1, RightObject u2) {
            int res = 0;
            if (u1 == null)
                res = -1;
            else if (u2 == null)
                res = 1;
            else {
                switch (sortColumn) {
                case 0:
                    res = u1.getName().compareTo(u2.getName());
                    break;
                case 1:
                    res = u1.getDesc().compareTo(u2.getDesc());
                    break;
                case 3:
                    res = (u1.isBlocked() == u2.isBlocked() ? 0 : (u1.isBlocked() ? 1 : -1));
                    break;
                case 4:
                    res = (u1.isDenied() == u2.isDenied() ? 0 : (u1.isDenied() ? 1 : -1));
                    break;
                }
            }
            if (!isSortAsc) {
                res = -res;
            }
            return res;
        }
    }

    class ActionComparator implements Comparator<ChildObject> {

        public int compare(ChildObject u1, ChildObject u2) {
            return u1.getCode() > u2.getCode() ? 1 : u1.getCode() == u2.getCode() ? 0 : -1;
        }
    }

    private class ChildTableModel extends AbstractTableModel {
        private final String[] COL_NAMES = {"Наименование"};
        private boolean isSortAsc = true;
//        private int sortColumn = 0;

		java.util.List<ChildObject> objs;

        public ChildTableModel() {
		}

        public ChildTableModel(List<ChildObject> objs) {
            this();
            if (objs != null) setData(objs);
            sortData();
        }

        public void setData(List<ChildObject> objs) {
            int size = objs != null ? objs.size() : 0;
            this.objs = new ArrayList<ChildObject>(size);
            if (objs != null) {
            	for (ChildObject obj : objs)
            		this.objs.add(obj);
            }
            fireTableDataChanged();
            sortData();
        }
        
        public void setSortAsc(boolean isSortAsc) {
			this.isSortAsc = isSortAsc;
		}

        public ChildObject getRowObject(int rowIndex) {
        	return objs.get(rowIndex);
        }

        public void removeData(ChildObject o) {
            this.objs.remove(o);
        }

        public int getRowCount() {
            return objs != null ? objs.size() : 0;
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public String getColumnName(int columnIndex) {
            return COL_NAMES[columnIndex];
        }
        
        public void sortData() {
        	Collections.sort(objs, new ChildObjComparator(isSortAsc));
        }
        
        public Icon getColumnIcon() {
        	return isSortAsc ? SORT_UP : SORT_DOWN;
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return String.class;
            }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                return objs.get(rowIndex) != null ? objs.get(rowIndex).getTitle() : "";
            }
            return null;
        }
    }

    public class RightsEditor extends AbstractCellEditor implements TableCellEditor {

        private Border cellBorder;
        private Component editor;

        public RightsEditor(JTable t) {
            cellBorder = new CellBorder(t.getGridColor());
        }

        public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
            if (column == 0 || column == 1) {
            	OrPropTextField editor = new OrPropTextField();
            	editor.setText(value instanceof String ? (String)value : "");
            	editor.setBorder(cellBorder);
            	this.editor = editor;
                return editor;
            } else if (column == 2) {
            	JComboBox cb = new JComboBox();
                cb.setFont(table.getFont());
                cb.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor()));
                cb.setBackground(kz.tamur.rt.Utils.getLightSysColor());
                cb.setEditable(false);

                cb.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        	RightsEditor.this.stopCellEditing();
                            table.requestFocusInWindow();
                        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        	RightsEditor.this.cancelCellEditing();
                            table.requestFocusInWindow();
                        }
                    }
                });

                Object popup = cb.getUI().getAccessibleChild(null, 0);
                if (popup instanceof ComboPopup) {
                    ((ComboPopup)popup).getList().addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            super.mouseReleased(e);
                            RightsEditor.this.stopCellEditing();
                            table.requestFocusInWindow();
                        }
                    });
                }
                
                cb.addItem("");
                cb.addItem("Вход в систему");
                cb.addItem("Запуск процесса");
                cb.addItem("Остановка процесса");
                cb.addItem("Просмотр архива");
                cb.addItem("Редактирование записей НСИ");
                
                cb.setSelectedItem(value);
                
            	this.editor = cb;
                return cb;
            } else if (column == 4 || column == 5) {
            	OrBasicCheckBox editor = new OrBasicCheckBox();
            	editor.setOpaque(false);
            	editor.setAnimate(false);

            	editor.setSelected(value instanceof Boolean ? (Boolean)value : false);
            	editor.setBorder(cellBorder);
            	this.editor = editor;
            	editor.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
			        	RightsEditor.this.stopCellEditing();
					}
				});
                return editor;
            }

            return null;
        }

        public Object getCellEditorValue() {
        	Object val = null;
            if (editor instanceof OrPropTextField) {
            	val = ((OrPropTextField)editor).getText();
            } else if (editor instanceof OrBasicCheckBox) {
            	val = ((OrBasicCheckBox)editor).isSelected();
            } else if (editor instanceof JComboBox) {
            	val = ((JComboBox)editor).getSelectedIndex();
            }
            return val;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
            	if (editor instanceof OrBasicCheckBox)
            		return ((MouseEvent) e).getClickCount() >= 1;
            	else
            		return ((MouseEvent) e).getClickCount() >= 2;
            }
            return false;
        }
    }
    
    class RightsRenderer extends DefaultTableCellRenderer {

        private Border cellBorder;
        private Component renderer;

        private Color secondColor = kz.tamur.rt.Utils.getLightSysColor();

        public RightsRenderer(JTable table) {
            cellBorder = new CellBorder(table.getGridColor());
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            Component comp = null;

            if (column < 4) {
            	OrPropTextField editor = new OrPropTextField();
            	editor.setText(value instanceof String ? (String)value : "");
            	comp = editor;
            } else {
            	JLabel lb = new JLabel();
                if (value != null && value instanceof Boolean) {
                    lb.setIcon(((Boolean) value).booleanValue() ? trueIcon : falseIcon);
                } else {
                    lb.setIcon(falseIcon);
                }
                lb.setText("");
                comp = lb;
            }

            if (isSelected) {
                comp.setBackground(table.getSelectionBackground());
            } else {
                if ((row % 2) == 0) {
                    comp.setBackground(secondColor);
                } else
                    comp.setBackground(table.getBackground());
            }

            ((JComponent) comp).setBorder(cellBorder);
            return comp;
        }
    }

    class RightsTable extends JTable {
    	RightsEditor editor = null;
    	RightsRenderer renderer = null;
    	
		public RightsTable(TableModel dm) {
			super(dm);
			editor = new RightsEditor(this);
			renderer = new RightsRenderer(this);
		}

		@Override
		public TableCellEditor getCellEditor() {
			return editor;
		}
		
		public TableCellRenderer getCellRenderer(int row, int column) {
			return renderer;
		}
    	
		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if (column != 3) {
				return editor;
			}
			return super.getCellEditor(row, column);
		}
    }
    
    protected void showPopup(MouseEvent e) {
        int[] rows = rightsTable.getSelectedRows();
        if (rows != null && rows.length > 0) {
            //popUp.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
