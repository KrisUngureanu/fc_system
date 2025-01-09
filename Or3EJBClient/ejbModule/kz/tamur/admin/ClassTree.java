package kz.tamur.admin;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

import kz.tamur.guidesigner.*;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;


public class ClassTree extends JTree {

    private NodeFinder finder = new NodeFinder();
    private String searchString = "";
    int modeComboIndex = 0, typeComboIndex = 0, conditionComboIndex = 0;
    private Stack <KrnMethod> methods = new Stack<KrnMethod>();
    private Stack <KrnAttribute> attributes = new Stack<KrnAttribute>();
    private boolean isMethod = false;
    private boolean isAttr = false;
    private int type = 0;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private AttributeTree attrTree;
    
    public ClassTree() {
        super(Kernel.instance().getClassHierarchy());
        jbInit();
    }

    public ClassTree(TreeModel m) {
        super(m);
        jbInit();
    }

    public void setAttrTree(AttributeTree attrTree) {
    	this.attrTree = attrTree;
    }
    
    public NodeFinder getFinder() {
    	return finder;
    }
    
    private void jbInit() {
        ClassTreeCellRenderer dtcr_ = new ClassTreeCellRenderer();
        dtcr_.setBackgroundNonSelectionColor(isOpaque ? Color.lightGray : new Color(0, 0, 0, 0));
        dtcr_.setClosedIcon(null);
        dtcr_.setOpenIcon(null);
        dtcr_.setLeafIcon(null);
        dtcr_.setBackgroundSelectionColor(Utils.getDarkShadowSysColor());
        dtcr_.setBorderSelectionColor(Utils.getDarkShadowSysColor());
        putClientProperty("JTree.lineStyle", "Angled");
        setBackground(Color.lightGray);
        setCellRenderer(dtcr_);
        setOpaque(isOpaque);
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                	attributes.clear();
                    find();
                } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                    if(isMethod){
                        if(methods.size() > 0){
                            TreeNode    node = (TreeNode) getModel().getRoot();
                            KrnMethod md=methods.peek();
                            TreeNode fnode = finder.findFirst(node, new KrnClassPattern(md.classId));
                            if (fnode != null) {
                                try {
                                    setSelectedPath((ClassNode) fnode);
                                    attrTree.setSelectedPath(md);
                                } catch (KrnException ex) {
                                    ex.printStackTrace();
                                }
                                methods.pop();
                            }
                        } else {
                            MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
                        }
                    }else if (isAttr) {
                        if (attributes.size() > 0) {
                            TreeNode node = (TreeNode) getModel().getRoot();
                            KrnAttribute attr=attributes.peek();
                            TreeNode fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
                            if (fnode != null) {
                                try {
                                    setSelectedPath((ClassNode) fnode);
                                    attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
                                } catch (KrnException ex) {
                                    ex.printStackTrace();
                                }
                                attributes.pop();
                            }
                        } else {
                            MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
                        }
                   } else {
                	   if (type == 3) {
                           if (attributes.size() > 0) {
	                		   TreeNode node = (TreeNode) getModel().getRoot();
	                           KrnAttribute attr = attributes.peek();
	                           TreeNode fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
	                           if (fnode != null) {
	                               try {
	                                   setSelectedPath((ClassNode) fnode);
	                                   attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
	                               } catch (KrnException e1) {
	                                   e1.printStackTrace();
	                               }
	                               attributes.pop();
	                           }
                           } else {
                               MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
                           }
                	   } else {
	                        Thread t = new Thread(new Runnable() {
	                            public void run() {
	                                TreeNode fnode = finder.findNext();
	                                if (fnode != null) {
	                                    try {
	                                        setSelectedPath((ClassNode) fnode);
	                                    } catch (KrnException ex) {
	                                        ex.printStackTrace();
	                                    }
	                                } else {
	                                    MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
	                                }
	                            }
	                        });
	                        t.start();
                	   }
                    }
                }
            }
        });
    }

    public void find(AttributeTree attrTree) {
    	this.attrTree=attrTree;
    	find();
    }
    private void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(getModel().getRoot()));
        if (isMethod) {
            isMethod = false;
            if (methods.size() > 0)
                methods.clear();
        }
        SearchInterfacePanel sip = new SearchInterfacePanel(true);
        sip.setSearchText(searchString);
        sip.setModeIndex(modeComboIndex);
        sip.setTypeIndex(typeComboIndex);
        sip.setConditionIndex(conditionComboIndex);
        
        DesignerDialog dlg = new DesignerDialog((Window) getTopLevelAncestor(), "Поиск", sip);
        dlg.setResizable(false);
        dlg.show();
        try {
            if (dlg.isOK()) {
                searchString = sip.getSearchText();
                isMethod = sip.isMethod();
                isAttr = sip.isAttr();
                modeComboIndex = sip.getMode();
            	typeComboIndex = sip.getType();
            	conditionComboIndex = sip.getCondition();
            	finder.setSearchMode(true);  // установить search=true в NodeFinder
                if (isMethod) {
                	type = sip.getType();
                	if(type == 0) {
	                    KrnMethod[] mds = Kernel.instance().getMethodsByName(searchString, sip.getSearchMethod());
	                    Arrays.sort(mds, new Comparator<KrnMethod>() {
	                        public int compare(KrnMethod o1, KrnMethod o2) {
	                            return (o1.classId < o2.classId ? -1 : o1.classId == o2.classId ? o2.name.compareTo(o1.name) : 1);
	                        }
	                    });
	                    this.methods.addAll(Arrays.asList(mds));
	                    if (methods.size() > 0) {
	                        TreeNode node = (TreeNode) getModel().getRoot();
	                        KrnMethod md=methods.peek();
	                        TreeNode fnode = finder.findFirst(node, new KrnClassPattern(md.classId));
	                        if (fnode != null) {
	                            try {
	                                setSelectedPath((ClassNode) fnode);
	                                attrTree.setSelectedPath(md);
	                            } catch (KrnException e) {
	                                e.printStackTrace();
	                            }
	                            methods.pop();
	                        }
	                    } else {
	                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
	                    }
                	} else if(type == 1) {
                		KrnMethod[] mds = Kernel.instance().getMethodsByUid(searchString, sip.getSearchMethod());
	                    Arrays.sort(mds, new Comparator<KrnMethod>() {
	                        public int compare(KrnMethod o1, KrnMethod o2) {
	                            return (o1.classId < o2.classId ? -1 : o1.classId == o2.classId ? o2.name.compareTo(o1.name) : 1);
	                        }
	                    });
	                    this.methods.addAll(Arrays.asList(mds));
	                    if (methods.size() > 0) {
	                        TreeNode node = (TreeNode) getModel().getRoot();
	                        KrnMethod md=methods.peek();
	                        TreeNode fnode = finder.findFirst(node, new KrnClassPattern(md.classId));
	                        if (fnode != null) {
	                            try {
	                                setSelectedPath((ClassNode) fnode);
	                                attrTree.setSelectedPath(md);
	                            } catch (KrnException e) {
	                                e.printStackTrace();
	                            }
	                            methods.pop();
	                        }
	                    } else {
	                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
	                    }
                	}
                	
                }else if (isAttr) {
                	type = sip.getType();
                	if (type == 0) {
	                    List<KrnAttribute> attrs = Kernel.instance().getAttributesByName(searchString, sip.getSearchMethod());
	                    this.attributes.addAll(attrs);
	                    if (attributes.size() > 0) {
	                        TreeNode node = (TreeNode) getModel().getRoot();
	                        KrnAttribute attr=attributes.peek();
	                        TreeNode fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
	                        if (fnode != null) {
	                            try {
	                                setSelectedPath((ClassNode) fnode);
	                                attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
	                            } catch (KrnException e) {
	                                e.printStackTrace();
	                            }
	                            attributes.pop();
	                        }
	                    } else {
	                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
	                    }
                	} else if (type == 1) {
                		KrnAttribute attr = Kernel.instance().getAttributeById(Long.parseLong(searchString));
	                    if (attr != null) {
	                        TreeNode node = (TreeNode) getModel().getRoot();
	                     
	                        TreeNode fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
	                        if (fnode != null) {
	                            try {
	                                setSelectedPath((ClassNode) fnode);
	                                attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
	                            } catch (KrnException e) {
	                                e.printStackTrace();
	                            }
	                          
	                        }
	                    } else {
	                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
	                    }                      
                        
                	} else if (type == 2) {
                		this.attributes.clear();
                		List<KrnAttribute> attrs = Kernel.instance().getAttributesByUidPart(searchString, sip.getSearchMethod());
	                    this.attributes.addAll(attrs);
	                    if (attributes.size() > 0) {
	                        TreeNode node = (TreeNode) getModel().getRoot();
	                        KrnAttribute attr=attributes.peek();
	                        TreeNode fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
	                        if (fnode != null) {
	                            try {
	                                setSelectedPath((ClassNode) fnode);
	                                attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
	                            } catch (KrnException e) {
	                                e.printStackTrace();
	                            }
	                            attributes.pop();
	                        }
	                    } else {
	                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
	                    }
                	}
                	
                } else {
                    TreeNode node = getSelectedNode();
                    if (node == null) {
                        node = (TreeNode) getModel().getRoot();
                    }
                    if (searchString.isEmpty()) {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                        return;
                    }
                    TreeNode fnode = null;
                    type = sip.getType();
                    if (type == 0) {
                        fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                        if (fnode != null) {
                        	setSelectedPath((ClassNode) fnode);
                        }
                    } else if (type == 1) {
                        long id = Long.parseLong(searchString);
                        fnode = finder.findFirst(node, new LongPattern(id));
                        if (fnode != null) {
                        	setSelectedPath((ClassNode) fnode);
                        }
                    } else if (type == 2) {
                    	String uid = searchString;
                    	fnode = finder.findFirst(node, new UIDPatternClass(uid, sip.getSearchMethod()));
                        if (fnode != null) {
                        	setSelectedPath((ClassNode) fnode);
                        }
                    	 
                    } else {
                    	List<KrnClass> krnClasses = Kernel.instance().getClassesByNameWithOptions(searchString, sip.getSearchMethod());
                    	if (krnClasses.size() == 0) {
                    		Container owner = getTopLevelAncestor();
                            MessagesFactory.showMessageDialog(owner instanceof Frame ? (Frame) owner : (Dialog) owner, MessagesFactory.INFORMATION_MESSAGE, "Класс с таким именем не найден!");
                    		return;
                    	}
                    	for (KrnClass krnClass : krnClasses) { 
	                        List<KrnAttribute> attrs = Kernel.instance().getAttributesByTypeId(krnClass.id, false);
	                        this.attributes.addAll(attrs);
                    	}
                        if (attributes.size() > 0) {
                            node = (TreeNode) getModel().getRoot();
                            KrnAttribute attr = attributes.peek();
                            fnode = finder.findFirst(node, new KrnClassPattern(attr.classId));
                            if (fnode != null) {
                                try {
                                    setSelectedPath((ClassNode) fnode);
                                    attrTree.setSelectedPath(getSelectedClass().name+"."+attr.name);
                                } catch (KrnException e) {
                                    e.printStackTrace();
                                }
                                attributes.pop();
                            }
                        }
                    }
                    if (fnode == null) {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public KrnClass getSelectedClass() {
        TreePath path = getSelectionPath();
        ClassNode node = (path == null) ? null : (ClassNode) path.getLastPathComponent();
        return (node == null) ? null : node.getKrnClass();
    }
    
    public KrnClass[] getSelectedClasses() {
    	TreePath path = getSelectionPath();
    	if(path != null) {
    		Object[] objs = path.getPath();
    		KrnClass[] classes = new KrnClass[objs.length];    		
    		for(int i=0; i<objs.length; i++){
    			classes[i] = ((ClassNode) objs[i]).getKrnClass();    			
    		}
    		return classes;
    	}
    	return null;
    }

    public ClassNode getSelectedNode() {
        TreePath path = getSelectionPath();
        ClassNode node = (path == null) ? null : (ClassNode) path.getLastPathComponent();
        return (node == null) ? null : node;
    }


    public void setSelectedPath(ClassNode node) throws KrnException {
        DefaultTreeModel m = (DefaultTreeModel) getModel();
        TreeNode[] path = m.getPathToRoot(node);
        TreePath tpath = new TreePath(path);
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }

    class ClassTreeCellRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            boolean isRepl = ((ClassNode) value).getKrnClass().isRepl;
            boolean isVirtual = ((ClassNode) value).getKrnClass().isVirtual();
            l.setIcon(ClassTreeIconLoader.getIcon(isRepl, isVirtual));
            l.setForeground(sel ? Color.white : Color.black);
            l.setBackground(Utils.getDarkShadowSysColor());
            l.setOpaque(selected);
            return l;
        }
    }

    public KrnMethod getSearchMethod() {
        if (methods.size() > 0)
            return methods.peek();
        else
        	return null;
    }
    
    //Вернуть название узла
    public String getNodeName() {
    	ClassNode cn = getSelectedNode();
    	if (cn != null) {
    		return cn.getKrnClass().name;
    	}
    	return "";
    }
    
    //Вернуть ID узла
    public long getNodeID() {
    	ClassNode cn = getSelectedNode();
    	if (cn != null) {
    		return cn.getKrnClass().id;
    	}
    	return 0;
    }
    
    //Вернуть UID узла
    public String getNodeUID() {
    	ClassNode cn = getSelectedNode();
    	if (cn != null) {
    		return cn.getKrnClass().uid;
    	}
    	return null;
    }
}