package kz.tamur.rt;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.SmallLinePanel;
import kz.tamur.or3.util.SystemAction;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.util.MMap;
/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 17.02.2005
 * Time: 15:50:56
 * To change this template use File | Settings | File Templates.
 */
public class ProcessUserComponent extends OrBasicTabbedPane{
    private final Kernel krn = Kernel.instance();
    private MMap<Long, ProcessObject, ? extends List<ProcessObject>> childrenMap_;
    private HashMap<Long, ProcessObject> map_obj;
    private ArrayList<ProcessFolderObject> map_tabbed = new ArrayList<ProcessFolderObject>();
    private boolean isProcesable_;
    private KrnClass cls_,cls_fold;
    private TreeSet langMap = new TreeSet();
    private long lang;

    public ProcessUserComponent(boolean isRunTime) {
        super();
        // Загрузка процессов
        langMap.add(new Long(lang = Utils.getInterfaceLangId()));
        loadProcess();
        //

        //Формирование закладок и деревьев
        if(map_tabbed != null && map_tabbed.size() > 0) {
            isProcesable_ = true;
            Collections.sort(map_tabbed, new Comparator<ProcessFolderObject>() {
                public int compare(ProcessFolderObject o1, ProcessFolderObject o2) {
                    if (o1 == null) {
                        return -1;
                    } else if (o2 == null) {
                        return 1;
                    } else {
                        return (o1.index < o2.index) ? -1 : 1;
                    }
                }
            });
            for (ProcessFolderObject obj : map_tabbed){
               ProcessNode node_ = new ProcessNode(childrenMap_, obj);
               ProcessTree process_ = new ProcessTree(node_,isRunTime);
               JScrollPane sp= new JScrollPane(process_);
               add(obj.toString(), sp);
               process_.setRootVisible(false);
            }
            
            ActionMap am = getActionMap();
            am.put("doLeft", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int idx = getSelectedIndex();
                    idx = (idx > 0) ? idx - 1 : getTabCount() - 1;
                    setSelectedIndex(idx);
                }
            });
            am.put("doRight", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int idx = getSelectedIndex();
                    idx = (idx < getTabCount() - 1) ? idx + 1 : 0;
                    setSelectedIndex(idx);
                }
            });

            InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), "doLeft");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), "doRight");
            fireStateChanged();
        }
    }

    private void loadProcess(){
        try{
    		//Загрузка служб
        	
        	long[] process_ = null;
        	
        	List<Long> procs = krn.getUserSubjects(SystemAction.ACTION_START_PROCESS, krn.getUser().getObject().id);
        	if (procs != null)
        		process_ = Funcs.makeLongArray(procs);
        	else {
                process_ = krn.getProcessDefinitions();
        	}
        	
            if (process_ == null || process_.length == 0)
            	return;

    		childrenMap_ = new MMap<Long, ProcessObject, ArrayList<ProcessObject>>(
    				(Class<ArrayList<ProcessObject>>)new ArrayList<ProcessObject>().getClass());
            map_obj = new HashMap();
            ProcessFolderObject root = null;

            KrnClass cls_root = krn.getClassByName("ProcessDefRoot");
            cls_fold = krn.getClassByName("ProcessDefFolder");
            cls_ = krn.getClassByName("ProcessDef");
            
            AttrRequestBuilder arb = new AttrRequestBuilder(cls_, krn).add("parent").add("runtimeIndex");
            
            arb.add("title", krn.getLangIdByCode("RU"));
            arb.add("title", krn.getLangIdByCode("KZ"));
            
            arb.add("message", krn.getLangIdByCode("RU"));
            arb.add("message", krn.getLangIdByCode("KZ"));

    		List<Object[]> pRecs = krn.getObjects(process_, arb.build(), 0);
            for(Object[] rec : pRecs) {
            	ProcessObject po = new ProcessObject(rec, Utils.getInterfaceLangId(), krn);
                map_obj.put(po.obj.id, po);
                if (po.parentObj != null)
                	childrenMap_.put(po.parentObj.id, po);
            }

            // Загружаем все папки, в том числе и корень
    		arb = new AttrRequestBuilder(cls_fold, krn).add("parent").add("runtimeIndex").add("isTab");
            
    		arb.add("title", krn.getLangIdByCode("RU"));
            arb.add("title", krn.getLangIdByCode("KZ"));
            arb.add("tabName", krn.getLangIdByCode("RU"));
            arb.add("tabName", krn.getLangIdByCode("KZ"));

    		List<Object[]> pfRecs = krn.getClassObjects(cls_fold, arb.build(), new long[0], new int[] {0}, 0);
            for(Object[] rec : pfRecs) {
            	ProcessFolderObject po = new ProcessFolderObject(rec, Utils.getInterfaceLangId(), krn);
                map_obj.put(po.obj.id, po);
                if (po.obj.classId == cls_root.id) {
                	root = po;
                	root.isTab = true;
                }
                if (po.parentObj != null)
                	childrenMap_.put(po.parentObj.id, po);
            }
            
            evaluateTabs(root);
        }catch(KrnException ex){
            ex.printStackTrace();
        }
    }
    
    private boolean evaluateTabs(ProcessFolderObject pf) {
    	List<ProcessObject> children = childrenMap_.get(pf.obj.id);
		
		if ((children == null || children.size() == 0) && pf.parentObj != null) {
			childrenMap_.get(pf.parentObj.id).remove(pf);
			return false;
		}
		
		children = new ArrayList<ProcessObject>(children);
    	
		if (pf.isTab) {
			boolean res = false;
    		for (ProcessObject child : children) {
    			if (child instanceof ProcessFolderObject) {
    				if (evaluateTabs((ProcessFolderObject)child)) {
    					res = true;
    				}
    			} else {
    				res = true;
    			}
    		}
    		if (res)
    			map_tabbed.add(pf);
    		else if (pf.parentObj != null)
    			childrenMap_.get(pf.parentObj.id).remove(pf);
    		return false;
    	} else {
    		boolean res = false;
    		for (ProcessObject child : children) {
    			if (child instanceof ProcessFolderObject) {
    				if (evaluateTabs((ProcessFolderObject)child))
    					res = true;
    			} else {
    				res = true;
    			}
    		}
    		if (!res) {
    			childrenMap_.get(pf.parentObj.id).remove(pf);
    		}
    		return res;
    	}
    }
    
    public void setScrollTabProc(boolean isScrollTabProc){
        if(isScrollTabProc)
            setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        else
            setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    }
    public void setLang(long langId){
        if(lang==langId || map_obj==null) return;
        lang=langId;
        if(!langMap.contains(new Long(lang))){
            langMap.add(new Long(lang));
        }else{
            for(Iterator it=map_obj.values().iterator();it.hasNext();){
                ProcessObject proc=(ProcessObject)it.next();
                proc.setLangId(lang);
            }
        }
        int i=0;
        Component[] comps=getComponents();
        for(Component comp:comps){
            if(comp instanceof JScrollPane){
                ProcessTree pt=(ProcessTree)((JScrollPane)comp).getViewport().getComponent(0);
                setTitleAt(i,(pt.getModel().getRoot()).toString());
                ((DefaultTreeModel)pt.getModel()).nodeStructureChanged((TreeNode)pt.getModel().getRoot());
            }
            i++;
        }
    }

    public boolean isProcesable(){
       return isProcesable_;
    }
    public ProcessNode getSelectedProcess(){
        ProcessNode node;
        node = ((ProcessTree)((JScrollPane)getSelectedComponent()).getViewport().getView()).getSelectedProcess();
        return node;

    }
    
    public String[] seeWordInMap(String word){
    	ArrayList list = new ArrayList();
    	Set Keys = map_obj.keySet();
    	Iterator<Long> keyIter = Keys.iterator();
    	while(keyIter.hasNext()){
    		Long key = keyIter.next();
    		if(childrenMap_.get(key)!=null) continue;
    		Object cal = map_obj.get(key);
    		if(cal.toString().toLowerCase(Constants.OK).startsWith(word.toLowerCase(Constants.OK))){
    			list.add(cal.toString());
    		}
    	}
    	return (String[])list.toArray(new String[0]);
    }
    
    public boolean setActive(Object o){
    	TreePath path = (TreePath)o;
    	ProcessTree pTree = null;
    	int index = this.indexOfTab(path.getPath()[0].toString());
		this.setSelectedIndex(index);
		pTree = (ProcessTree)((JScrollPane)getComponent(index)).getViewport().getView();
    	if(pTree==null) return false;
    	pTree.scrollPathToVisible(path);  
        pTree.setSelectionPath(path);
        return true;
    }
    
    //Еслт будет тормозить, надо переписать и не трогать долбанное дерево.
    public Object[] searchByName(String name, boolean fol, int mode) {
    	ArrayList returnArr = new ArrayList();
		Object[] ooo = this.getComponents();
    	for(int i = 0; i < this.getComponentCount(); i++){
    		//make by this.getComp...
    		ProcessTree pTree = (ProcessTree)((JScrollPane) ooo[i]).getViewport().getView();
    		DefaultMutableTreeNode node = null, nodee = null;
    		node = (DefaultMutableTreeNode)pTree.model.getRoot();
    		expandAll(pTree, new TreePath(node), true);
    		//Enumeration en = node.breadthFirstEnumeration(); why not work?!?!?!
    		Enumeration en = node.depthFirstEnumeration();
    		
    		while(en.hasMoreElements()) 
            { 
                nodee = (DefaultMutableTreeNode)en.nextElement();
                if(!fol)if(!nodee.isLeaf())continue;
                
                if(mode == SmallLinePanel.CONTAIN){
                if(nodee.toString().toLowerCase(Constants.OK).contains(name.toLowerCase(Constants.OK)))
                { 
                	TreeNode[] nodes = pTree.model.getPathToRoot(nodee); 
                    TreePath path = new TreePath(nodes);
                    returnArr.add(path);                       
                } }
                else if(mode == SmallLinePanel.STARTS){
                if(nodee.toString().toLowerCase(Constants.OK).startsWith(name.toLowerCase(Constants.OK)))
                { 
                	TreeNode[] nodes = pTree.model.getPathToRoot(nodee); 
                    TreePath path = new TreePath(nodes);
                    returnArr.add(path);                       
                }}
                else if(mode == SmallLinePanel.ENDS){
                if(nodee.toString().toLowerCase(Constants.OK).endsWith(name.toLowerCase(Constants.OK)))
                {
                	TreeNode[] nodes = pTree.model.getPathToRoot(nodee); 
                    TreePath path = new TreePath(nodes);
                    returnArr.add(path);
                }}
            } 
    		
    		expandAll(pTree, new TreePath(node), false);
    		pTree.expandPath(new TreePath(pTree.model.getPathToRoot(node)));
    	}
    	return returnArr.toArray(new TreePath[returnArr.size()]);
    }
    
    private void expandAll(JTree tree, TreePath parent, boolean b) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
          for (Enumeration e = node.children(); e.hasMoreElements();) {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            expandAll(tree, path, b);
          }
        }
        if(b)
        	tree.expandPath(parent); 
        else 
        	tree.collapsePath(parent);
      }
    
    public Object getMap_Obj() {
    	if(map_obj == null) System.out.println("map_obj is null");
    	return map_obj;
    }
    
    public Object getMap_(){
    	if(childrenMap_ == null) System.out.println("map_ is null");
    	return childrenMap_;
    }
}
