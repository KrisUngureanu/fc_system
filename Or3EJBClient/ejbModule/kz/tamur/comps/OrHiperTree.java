package kz.tamur.comps;

/**
 * Title:        OR2
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CIFS
 * @author  Tostanovskiy V.I.
 * @version 1.0
 */

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.gui.*;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.guidesigner.hypers.HyperNode;
import kz.tamur.guidesigner.hypers.HyperTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.ProcessUserComponent;
import kz.tamur.rt.TaskTable;
import kz.tamur.rt.Descriptionable;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.LangItem;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.Locale;

public class OrHiperTree extends JPanel implements  ActionListener, KeyListener, MouseListener {

    int SPR_IFC_MODE = 0x04;
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private ImageIcon curWork_icon_kz = kz.tamur.rt.Utils.getImageIcon("tasksKaz");
    private ImageIcon catalog_icon_kz = kz.tamur.rt.Utils.getImageIcon("catalogKaz");
    private ImageIcon archiv_icon_kz = kz.tamur.rt.Utils.getImageIcon("archivKaz");
    private ImageIcon curWork_icon_ru = kz.tamur.rt.Utils.getImageIcon("tasks");
    private ImageIcon catalog_icon_ru = kz.tamur.rt.Utils.getImageIcon("catalog");
    private ImageIcon archiv_icon_ru = kz.tamur.rt.Utils.getImageIcon("archiv");
    private BorderLayout borderLayout = new BorderLayout();
    private KrnObject guiObj_, main_ifc;
    private KrnObject[] guiObjs_;
    private JSplitPane sp_;
    private Component lc_;
    private int width_ = 0, col_comp = 2;
    private HyperTree  hiperTreeSpr, hiperTreeArh;
    private JScrollPane spTreeArh, spTreeSpr;
    private Color oldColor;
    private ProcessUserComponent spTree_m;
    private Kernel krn = Kernel.instance();
    private String title_;
    private JPanel this_p;
    private JButton oldBtn;
    private DescButton btnSrv = new DescButton();
    private DescButton btnSpr = new DescButton();
    private DescButton btnArh = new DescButton();
    public static final int SHOW_YES = 1;
    public static final int SHOW_NO = 0;
    public static final OrEnum[] SHOW_HELP = {new OrEnum("Показывать справку", SHOW_YES),
                                              new OrEnum("Не показывать справку", SHOW_NO)};
    protected OrEnum show_help, oldshow_help;

    kz.tamur.rt.InterfaceManager mgr_;
    private JSplitPane tasksSpliter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private TaskTable taskTable = TaskTable.instance(false);
    public boolean activeSrv = true;
    private UIFrame mainUI;

    public OrHiperTree(OrGuiComponent parent,
                       KrnObject guiObj,
                       String title,
                       String refPath, //val?
                       Map flags, //val6
                       Map refPaths, //val?7
                       Map constraints, //val 8
                       int mode,
                       Map refs,
                       UIFrame mainUI) throws KrnException {
    	this.mainUI = mainUI;
        final Kernel krn = Kernel.instance();
        final long lid = Utils.getInterfaceLangId();
        long kzId = LangItem.getByCode("KZ").obj.id;
        if(lid==kzId){
            btnSrv.setIcon(curWork_icon_kz);
            btnArh.setIcon(archiv_icon_kz);
            btnSpr.setIcon(catalog_icon_kz);
        }else{
            btnSrv.setIcon(curWork_icon_ru);
            btnArh.setIcon(archiv_icon_ru);
            btnSpr.setIcon(catalog_icon_ru);
        }
        btnSrv.setDesc(res.getString("btnSrvDesc"));
        btnArh.setDesc(res.getString("btnArhDesc"));
        btnSpr.setDesc(res.getString("btnSprDesc"));

        this.title_ = title;
        guiObjs_ = krn.getObjects(guiObj, "hipers", 0);
        guiObj_ = guiObj;
        Set read = Kernel.instance().getUser().getReadOnlyItems();
        Set write = Kernel.instance().getUser().getReadWriteItems();
        HyperNode inode = new HyperNode(guiObjs_[1].id > guiObjs_[0].id?guiObjs_[0]:guiObjs_[1], "Меню:Архивы", "Меню:Архивы", null, null, 0,
                read, write, lid, false, null);
        hiperTreeArh = new HyperTree(inode,false,false);
        hiperTreeArh.setShowPopupEnabled(false);
        hiperTreeArh.addMouseListener(this);
        hiperTreeArh.addKeyListener(this);
        spTreeArh= new JScrollPane(hiperTreeArh);
        hiperTreeArh.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        inode = new HyperNode(guiObjs_[1].id > guiObjs_[0].id?guiObjs_[1]:guiObjs_[0], "Меню:Справочники", "Меню:Справочники", null, null, 0, read, write, lid, false, null);
        hiperTreeSpr = new HyperTree(inode,false,false);
        hiperTreeSpr.setShowPopupEnabled(false);
        hiperTreeSpr.addMouseListener(this);
        hiperTreeSpr.addKeyListener(this);
        spTreeSpr= new JScrollPane(hiperTreeSpr);
        hiperTreeSpr.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        String[] strs = krn.getStrings(guiObj, "title", lid, 0);
        if (strs.length > 0)
            title_ = strs[0];
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void jbInit() throws Exception {
        this.setLayout(borderLayout);
        btnSpr.setBorderPainted(false);
        btnSrv.setBorderPainted(false);
        btnArh.setBorderPainted(false);
        oldColor = btnSpr.getBackground();

        if (!Constants.SE_UI) {
	        JPanel btnPane = new JPanel();
	        btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.Y_AXIS));
	        btnPane.setBorder(BorderFactory.createEtchedBorder());
	        btnPane.add(btnSpr, 0);
	        btnPane.add(btnArh, 0);
	        btnPane.add(btnSrv, 0);
	        btnPane.setPreferredSize(new Dimension(25, 100));
	        this.add(btnPane, BorderLayout.WEST);
        }
        
        this_p = this;
        tasksSpliter.setLeftComponent(taskTable);
        
    	if (!MainFrame.ADVANCED_UI) {
    		spTree_m = new ProcessUserComponent(true);
    		if(spTree_m.isProcesable())
        		tasksSpliter.setRightComponent(spTree_m);
    		else
    			tasksSpliter.setDividerLocation(1);
    	} else if (mainUI != null) {
    		tasksSpliter.setRightComponent(mainUI.getPanel());
    	} else {
			tasksSpliter.setDividerLocation(1);
    	}
    	
        btnSrv.setVisible(true);
        btnArh.setVisible(true);
        btnSpr.setVisible(true);
        main_ifc = krn.getUser().getIfc();
        //Добавление слушателя мышки в режиме конструктора
        //(изображение стандартного курсора в поле зрения дерева)
        if (btnSrv.isVisible()) {
            this.add(tasksSpliter, BorderLayout.CENTER);
            btnSrv.setBackground(Color.white);
            oldBtn = btnSrv;
        } else if (btnArh.isVisible()) {
            this.add(spTreeArh, BorderLayout.CENTER);
            btnArh.setBackground(Color.white);
            oldBtn = btnArh;
        } else if (btnSpr.isVisible()) {
            this.add(spTreeSpr, BorderLayout.CENTER);
            btnSpr.setBackground(Color.white);
            oldBtn = btnSpr;
        } else
            return;
        btnSpr.addActionListener(this);
        btnArh.addActionListener(this);
        btnSrv.addActionListener(this);
        hiperTreeSpr.setRootVisible(false);
/*
        for (int i = 0; i < hiperTreeSpr.getRowCount(); ++i) {
            if (hiperTreeSpr.getPathForRow(i).getPathCount() < 3)
                hiperTreeSpr.expandRow(i);
        }
*/

        hiperTreeArh.setRootVisible(false);
/*
        for (int i = 0; i < hiperTreeArh.getRowCount(); ++i) {
            if (hiperTreeArh.getPathForRow(i).getPathCount() < 3)
                hiperTreeArh.expandRow(i);
        }
*/
        tasksSpliter.addComponentListener(new ComponentAdapter() {
        	
        	private boolean dividerLocationSet = false;

			@Override
			public void componentResized(ComponentEvent e) {
				if (!dividerLocationSet) {
					int p = 0;
					if (mainUI != null) {
						Dimension sz = mainUI.getPanel().getPrefSize();
						if (sz != null) {
							p = tasksSpliter.getHeight() - sz.height;
							tasksSpliter.setDividerLocation(p);
						}
					}
					if (p == 0) {
				        tasksSpliter.setDividerLocation(0.5);
					}
					dividerLocationSet = true;
				}
			}
		});
    }

    public void actionPerformed(ActionEvent e) {
    	activeSrv = false;
        Object item = e.getSource();
        mgr_ = kz.tamur.rt.InterfaceManagerFactory.instance().getManager();
        if (item == btnSrv || item == btnArh || item == btnSpr) {
            if(oldBtn == (JButton) item) return;
            KrnObject[] sprIfc = new KrnObject[1];
            Component spTree = spTreeArh;
            if (item == btnSrv) {
            	activeSrv = true;
                spTree = spTree_m;
                sprIfc[0] = null/*ier_c_m.getIfcSpr()*/;
            } else if (item == btnSpr) {
                spTree = spTreeSpr;
            } else
            ;
//            if (mode_ == OrGuiContainer.MODE_NORMAL) {
                if (item == btnSrv
                        && (this_p.getComponentCount() < col_comp
                        || !this_p.getComponent(col_comp - 1).equals(spTree))) {
                    Container c = getParent();
                    if (c instanceof JSplitPane) {

                        ((JSplitPane) c).setDividerLocation(1);


                    }
                }
                try {
                    if(mgr_!=null) {
                        mgr_.absolute(sprIfc[0] != null ? main_ifc : null, sprIfc, "",
                                SPR_IFC_MODE, true, 0, 0, false,"");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (this_p.getComponentCount() > 1) {
                    if (lc_ == null) {
                        sp_ = (JSplitPane) ((JButton) item).getParent().getParent().getParent();
                        lc_ = sp_.getLeftComponent();
                    }
                    width_ = lc_.getWidth();
                    if (!this_p.getComponent(1).equals(spTree)) {
                        this_p.remove(1);
                        if (spTree == spTree_m) {
                            this_p.add(tasksSpliter, BorderLayout.CENTER);
                        } else {
                            this_p.add(spTree, BorderLayout.CENTER);
                        }
                        ((JButton) item).setBackground(Color.white);
                        ((JPanel) lc_).setPreferredSize(new Dimension(width_, 100));
                        oldBtn.setBackground(oldColor);
                    } else {
                        sp_.setDividerLocation(0);
                        this_p.remove(1);
                        ((JButton) item).setBackground(oldColor);
                        width_ = lc_.getWidth();
                        ((JPanel) lc_).setPreferredSize(new Dimension(0, 100));
                    }
                    sp_.remove(lc_);
                    sp_.add(lc_);
                    if (item == btnSrv) {
                    } else if(spTree instanceof JScrollPane){
                        ((JTree) ((JScrollPane)spTree).getViewport().getView()).grabFocus();
                    }else
                        ((JTree) spTree).grabFocus();
                    oldBtn = (JButton) item;
                } else {
                    if (spTree == spTree_m) {
                        this_p.add(tasksSpliter, BorderLayout.CENTER);
                    } else {
                        this_p.add(spTree, BorderLayout.CENTER);
                    }
                    ((JButton) item).setBackground(Color.white);
                    ((JPanel) lc_).setPreferredSize(new Dimension(width_, 100));
                    sp_.remove(lc_);
                    sp_.add(lc_);
                    if (item == btnSrv) {
                    } else if(spTree instanceof JScrollPane){
                        ((JTree) ((JScrollPane)spTree).getViewport().getView()).grabFocus();
                    }else
                        ((JTree) spTree).grabFocus();
                }
                oldBtn = (JButton) item;
                JSplitPane pane = ((JSplitPane) sp_);
                        int loc = pane.getDividerLocation();
                        loc = (int)((pane.getBounds().getWidth()-pane.getDividerSize())*1/3);
                            pane.setDividerLocation(loc);
/*            } else {
                if (this_p.getComponentCount() > 1) {
                    if (!this_p.getComponent(1).equals(spTree)) {
                        this_p.remove(1);
                        if (spTree == spTree_m) {
                            this_p.add(tasksSpliter, BorderLayout.CENTER);
                        } else {
                            this_p.add(spTree, BorderLayout.CENTER);
                        }
                        ((JButton) item).setBackground(Color.white);
                        oldBtn.setBackground(oldColor);
                        if(spTree instanceof JScrollPane){
                            ((JTree) ((JScrollPane)spTree).getViewport().getView()).grabFocus();
                        }else
                            ((JTree) spTree).grabFocus();
                    } else {
                        ((JButton) item).setBackground(oldColor);
                        this_p.remove(1);
                    }
                } else {
                    if (spTree == spTree_m) {
                        this_p.add(tasksSpliter, BorderLayout.CENTER);
                    } else {
                        this_p.add(spTree, BorderLayout.CENTER);
                    }
                    ((JButton) item).setBackground(Color.white);
                    if(spTree instanceof JScrollPane){
                        ((JTree) ((JScrollPane)spTree).getViewport().getView()).grabFocus();
                    }else
                        ((JTree) spTree).grabFocus();
                }
                oldBtn = (JButton) item;
            }
 */           if (oldBtn.getBackground() == Color.white && oldBtn == btnSrv && mgr_ != null){
                ((MainFrame) mgr_).setEnabledGraf(true);
                TaskTable.instance(true).disposeGraf(0);
            }
            else if (mgr_ != null) ((MainFrame) mgr_).setEnabledGraf(false);

            validate();
            repaint();
        }
        Container c = getParent();
        if (c instanceof JSplitPane) {
            ((JSplitPane) c).setDividerLocation(0.3);
            JSplitPane pane = ((JSplitPane) c);
                        int loc = pane.getDividerLocation();
                        loc = (int)((pane.getBounds().getWidth()-pane.getDividerSize())*1/3);
                            pane.setDividerLocation(loc);
        }
    }

    public void setMonitorFocus() {
        if (oldBtn == btnSrv) {
            TaskTable.instance(false).disposeGraf(0);
        } else if (oldBtn == btnSpr) {
            hiperTreeSpr.grabFocus();
            if( mgr_!=null) ((MainFrame)mgr_).setEnabledGraf(btnSrv.getBackground()==Color.white);
        } else if (oldBtn == btnArh) {
            hiperTreeArh.grabFocus();
            if( mgr_!=null) ((MainFrame)mgr_).setEnabledGraf(btnSrv.getBackground()==Color.white);
        }
    }

    public void mouseClicked(MouseEvent e) {
        HyperTree src=(HyperTree)e.getSource();
        HyperNode node= (HyperNode)src.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && e.getClickCount() == 2 &&
            src.getSelectionPath() ==src.getPathForLocation(e.getX(), e.getY()) ){
            try{
                if (src == hiperTreeSpr) {
                    int mode = node.isReadOnly() ?
                            kz.tamur.rt.InterfaceManager.SPR_RO_MODE
                            : kz.tamur.rt.InterfaceManager.SPR_RW_MODE;
                    mgr_.absolute(node.getIfcObject(), null, "",
                            mode, true, 0, 0, true,"");
                } else if (src == hiperTreeArh) {
                    int mode = node.isChangeable() ?
                            kz.tamur.rt.InterfaceManager.ARCH_RW_MODE
                            : kz.tamur.rt.InterfaceManager.ARCH_RO_MODE;
                    mgr_.absolute(node.getIfcObject(), null, "",
                            mode, true, 0, 0, true,"");
                }
            } catch(KrnException ex){
                ex.printStackTrace();
            }
        }
    }

    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }

    public void keyPressed(KeyEvent e) {
        HyperTree src=(HyperTree)e.getSource();
        HyperNode node= (HyperNode)src.getLastSelectedPathComponent();
        if (node!=null && node.isLeaf() &&
            (e.getKeyCode() == KeyEvent.VK_INSERT ||e.getKeyCode() == KeyEvent.VK_ENTER))
            try{
                if (src == hiperTreeSpr) {
                    int mode = node.isReadOnly() ?
                            kz.tamur.rt.InterfaceManager.SPR_RO_MODE
                            : kz.tamur.rt.InterfaceManager.SPR_RW_MODE;
                    mgr_.absolute(node.getIfcObject(), null, "",
                            mode, true, 0, 0, true,"");
                } else if (src == hiperTreeArh) {
                    int mode = node.isChangeable() ?
                            kz.tamur.rt.InterfaceManager.ARCH_RW_MODE
                            : kz.tamur.rt.InterfaceManager.ARCH_RO_MODE;
                    mgr_.absolute(node.getIfcObject(), null, "",
                            mode, true, 0, 0, true,"");
                }
//            mgr_.absolute(node.getIfcObject(), null, "", 0, true, 0, 0, false);
            }catch(KrnException ex){
                ex.printStackTrace();
            }
    }
    public void setScrollTabProc(boolean isScrollTabProc){
        if (spTree_m!=null) {
        spTree_m.setScrollTabProc(isScrollTabProc);}
    }
    public void setLang(long langId){
    	if (spTree_m != null)
    		spTree_m.setLang(langId);
        hiperTreeSpr.setLang(langId);
        hiperTreeArh.setLang(langId);
        LangItem li = LangItem.getById(langId);
        if (li != null && "KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
                btnSrv.setIcon(curWork_icon_kz);
                btnArh.setIcon(archiv_icon_kz);
                btnSpr.setIcon(catalog_icon_kz);
        } else {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("ru"));
                btnSrv.setIcon(curWork_icon_ru);
                btnArh.setIcon(archiv_icon_ru);
                btnSpr.setIcon(catalog_icon_ru);
        }
        btnSrv.setDesc(res.getString("btnSrvDesc"));
        btnArh.setDesc(res.getString("btnArhDesc"));
        btnSpr.setDesc(res.getString("btnSprDesc"));
    }
    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }

    public JButton getBtnSrv() {
        return btnSrv;
    }

    public JButton getBtnSpr() {
        return btnSpr;
    }

    public JButton getBtnArh() {
        return btnArh;
    }

    public class DescButton extends JButton implements Descriptionable {
        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
    
    public Object[] searchByName(String name, boolean fol, int mode){
    	return spTree_m.searchByName(name, fol, mode);
    }
    
    public boolean setActive(Object o){
    	if (spTree_m.setActive(o))
    		return true;
    	return false;
    }
    
    public String[] seeWordInMap(String word){
    	return spTree_m.seeWordInMap(word);
    }
    
    public Object getMap_Obj() {
        return spTree_m.getMap_Obj();
    }

    public Object getMap_() {
        return spTree_m.getMap_();
    }

    public KrnObject getKrnObject() {
        return guiObj_;
    }

    /**
     * Получить tasks spliter.
     *
     * @return the tasks spliter
     */
    public JSplitPane getTasksSpliter() {
        return tasksSpliter;
    }

    /**
     * Получить таблицу задач.
     *
     * @return таблица задач
     */
    public TaskTable getTaskTable() {
        return taskTable;
    }
}
