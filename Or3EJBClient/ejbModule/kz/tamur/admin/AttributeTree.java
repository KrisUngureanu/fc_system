package kz.tamur.admin;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Kernel.AttrNode;
import com.cifs.or2.client.Kernel.CastNode;
import com.cifs.or2.client.Kernel.MethodNode;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

import kz.tamur.guidesigner.KrnClassPattern;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3.client.util.ClientUtils;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

public class AttributeTree extends JTree {
	private static final long serialVersionUID = 1L;
	private static final Color INH_ATTR_COLOR = Color.blue.darker();
	private static final Color REV_ATTR_COLOR = new Color(160, 0, 80);
	private static final Color CAST_ATTR_COLOR = new Color(0, 102, 0);
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private Stack <KrnAttribute> attributes = new Stack<KrnAttribute>();
    private ClassTree classTree;
    
	public AttributeTree() {
		super();
		AttributeTreeCellRenderer renderer = new AttributeTreeCellRenderer();
		renderer.setBackgroundNonSelectionColor(isOpaque ? Color.lightGray : new Color(0, 0, 0, 0));
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setLeafIcon(null);
		renderer.setBackgroundSelectionColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
		renderer.setBorderSelectionColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
		putClientProperty("JTree.lineStyle", "Angled");
		setCellRenderer(renderer);
		setBackground(Color.lightGray);
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.isControlDown()) {
					String str = null;
					switch (ke.getKeyCode()) {
						case KeyEvent.VK_N:
							str = getNodeName();
							ClientUtils.setClipboard(str);
							break;
						case KeyEvent.VK_I:
							str = String.valueOf(getNodeID());
							ClientUtils.setClipboard(str);
							break;
						case KeyEvent.VK_U:
							str = getNodeUID();
							ClientUtils.setClipboard(str);
							break;
						case KeyEvent.VK_T:
							str = getNodeTypeName();
							ClientUtils.setClipboard(str);
							break;
						case KeyEvent.VK_P:
							str = getNodeClassName();
							ClientUtils.setClipboard(str);
							break;
						case KeyEvent.VK_F:
							findFirst();
							break;
						default:
							break;
					}
				} else if (ke.getKeyCode() == KeyEvent.VK_F3) {
					findNext();
				}
			}
		});
	}
	
	public void setClassTree(ClassTree classTree) {
		this.classTree = classTree;
	}
	
	private void findFirst() {
		KrnAttribute selectedAttr = getSelectedAttribute();
		Container owner = getTopLevelAncestor();
		if (selectedAttr == null) {
            MessagesFactory.showMessageDialog(owner instanceof Frame ? (Frame) owner : (Dialog) owner, MessagesFactory.INFORMATION_MESSAGE, "Выберите атрибут!");
            return;
		}
		try {
			TreeNode fnode = null;
			attributes.addAll(Kernel.instance().getRevAttributes2(selectedAttr.id));
            if (attributes.size() > 0) {
            	TreeNode node = (TreeNode) classTree.getModel().getRoot();
                KrnAttribute revAttr = attributes.peek();
                fnode = classTree.getFinder().findFirst(node, new KrnClassPattern(revAttr.classId));
                if (fnode != null) {
                    try {
                    	classTree.setSelectedPath((ClassNode) fnode);
                        setSelectedPath(classTree.getSelectedClass().name+"."+revAttr.name);
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }
                    attributes.pop();
                }
            }
            if (fnode == null) {
                MessagesFactory.showMessageNotFound(owner);
            }
		} catch (KrnException e) {
            MessagesFactory.showMessageDialog(owner instanceof Frame ? (Frame) owner : (Dialog) owner, MessagesFactory.ERROR_MESSAGE, "Ошибка при выполнении поиска!");
			e.printStackTrace();
		}
	}
	
	private void findNext() {
		TreeNode fnode = null;
        if (attributes.size() > 0) {
        	TreeNode node = (TreeNode) classTree.getModel().getRoot();
            KrnAttribute revAttr = attributes.peek();
            fnode = classTree.getFinder().findFirst(node, new KrnClassPattern(revAttr.classId));
            if (fnode != null) {
                try {
                	classTree.setSelectedPath((ClassNode) fnode);
                    setSelectedPath(classTree.getSelectedClass().name+"."+revAttr.name);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                attributes.pop();
            }
        }
        if (fnode == null) {
            MessagesFactory.showMessageSearchFinished(getTopLevelAncestor());
        }
	}

    public Object getSelectedObject() {
        TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.MethodNode) {
                return ((Kernel.MethodNode)o).getMethod();
            } else {
                return ((Kernel.AttrNode)o).getKrnAttribute();
            }
        }
        return null;
    }

	public KrnAttribute getSelectedAttribute() {
		TreePath path = getSelectionPath();
		Kernel.AttrNode anode = (path == null) ? null : (Kernel.AttrNode) path.getLastPathComponent();
		return (anode == null) ? null : anode.getKrnAttribute();
	}

	public KrnAttribute[] getSelectedAttributes() {
		TreePath path = getSelectionPath();
		if (path != null) {
			Object[] objs = path.getPath();
			KrnAttribute[] attrs = new KrnAttribute[objs.length];
			for (int i = 0; i < objs.length; ++i)
				attrs[i] = ((Kernel.AttrNode) objs[i]).getKrnAttribute();
			return attrs;
		}
		return null;
	}
    
	public KrnMethod getSelectedMethod() {
		TreePath path = getSelectionPath();
		if (path != null) {
			Object o = path.getLastPathComponent();
			if (o instanceof Kernel.MethodNode) {
				return ((Kernel.MethodNode) o).getMethod();
			}
		}
		return null;
	}
  
    public int getSelectedAttributesCount() {
        TreePath[] paths = getSelectionPaths();
        return (paths!=null) ? paths.length : 0;
    }

	public KrnAttribute[] getSelectedAttributes(int i) {
		KrnAttribute[] attrs = null;
		TreePath[] paths = getSelectionPaths();
		if (paths != null) {
			TreePath path = paths[i];
			Object[] objs = path.getPath();
			attrs = new KrnAttribute[objs.length];
			for (int j = 0; j < objs.length; ++j)
				attrs[j] = ((Kernel.AttrNode) objs[j]).getKrnAttribute();
		}
		return attrs;
	}

    public void setSelectedPath(String path) throws KrnException {
        TreeModel m = getModel();
        List<Kernel.AttrNode> nodes = new ArrayList<Kernel.AttrNode>();
        Kernel.AttrNode n = (Kernel.AttrNode)m.getRoot();
        nodes.add(n);
        if (path != null && path.length() > 0) {
	        PathElement2[] pes = Utils.parsePath2(path);
	        if (pes != null && pes.length > 0) {
	            for (int i = 1; i < pes.length; i++) {
	            	KrnAttribute attr = pes[i].attr;
	            	AttrNode attrNode = null;
	                for (int j = 0; j < n.getChildCount(); j++) {
	                    AttrNode child = (AttrNode)n.getChildAt(j);
	                    if (child instanceof MethodNode) {
	                    	break;
	                    }
	                    if (child.getKrnAttribute().id == attr.id) {
	                        attrNode = child;
	                        break;
	                    }
	                }
	                if (attrNode != null) {
	                	n = attrNode;
	                	nodes.add(attrNode);
	                	if (pes[i].type.id != attr.typeClassId) {
	                        for (int j = 0; j < n.getChildCount(); j++) {
	                            AttrNode child = (AttrNode)n.getChildAt(j);
	                            if (child instanceof CastNode && child.getType().getId() == pes[i].type.id) {
	                            	n = child;
	                                nodes.add(child);
	                                break;
	                            }
	                        }
	                	}
	                } else {
	                	break;
	                }
	            }
	        }
        }
        TreePath tpath = new TreePath(nodes.toArray(new Kernel.AttrNode[nodes.size()]));
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }
    
    public void setSelectedPath(KrnMethod method) throws KrnException {
        TreeModel m = getModel();
        Kernel.AttrNode root = (Kernel.AttrNode)m.getRoot();
        List<Kernel.AttrNode> nodes = new ArrayList<Kernel.AttrNode>();
        nodes.add(root);
        Enumeration children=root.children();
        while(children.hasMoreElements()){
            Object obj=children.nextElement();
            if(obj instanceof MethodNode && ((MethodNode)obj).getMethod().uid.equals(method.uid)){
                nodes.add((MethodNode)obj);
            }
        }
        TreePath tpath = new TreePath(nodes.toArray(new Kernel.AttrNode[nodes.size()]));
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }
    
	public void setSelectedPath(KrnAttribute attribute) throws KrnException {
		TreeModel m = getModel();
		Kernel.AttrNode root = (Kernel.AttrNode) m.getRoot();
		List<Kernel.AttrNode> nodes = new ArrayList<Kernel.AttrNode>();
		nodes.add(root);
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			Object obj = children.nextElement();
			if (obj instanceof AttrNode) {
				KrnAttribute attr = ((AttrNode) obj).getKrnAttribute();
				if (attr != null && attr.uid.equals(attribute.uid)) {
					nodes.add((AttrNode) obj);
				}
			}
		}
		TreePath tpath = new TreePath(nodes.toArray(new Kernel.AttrNode[nodes.size()]));
		setSelectionPath(tpath);
		scrollPathToVisible(tpath);
	}
    
	//Вернуть название узла
    public String getNodeName(){
    	TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.MethodNode) {
                return ((Kernel.MethodNode)o).getMethod().name;
            } else {
                return ((Kernel.AttrNode)o).getKrnAttribute().name;
            }
        }
    	return "";
    }
    
    //Вернуть id выбранного узла
    public long getNodeID(){
    	TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.AttrNode) {                
                return ((Kernel.AttrNode)o).getKrnAttribute().id;
            }
        }
    	return 0;
    }
    //Вернуть uid узла
    public String getNodeUID(){
    	TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.MethodNode) {
                return ((Kernel.MethodNode)o).getMethod().uid;
            } else {
                return ((Kernel.AttrNode)o).getKrnAttribute().uid;
            }
        }
    	return "";
    }
    
    //Вернуть название родительского класса атрибута
    public String getNodeClassName(){
    	TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.AttrNode) {
            	long classId = ((Kernel.AttrNode)o).getKrnAttribute().classId;
            	Kernel krn = Kernel.instance();
            	try {
					ClassNode cnode = krn.getClassNode(classId);
					return cnode.getName();
				} catch (KrnException e) {
					return "Is not determined";
				}
            }
        }
    	return "";
    }
    
    //Вернуть название типа атрибута
    public String getNodeTypeName(){
    	TreePath path = getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof Kernel.AttrNode) {
            	long classId = ((Kernel.AttrNode)o).getKrnAttribute().typeClassId;
            	Kernel krn = Kernel.instance();
            	try {
					ClassNode cnode = krn.getClassNode(classId);
					return cnode.getName();
				} catch (KrnException e) {
					return "Is not determined";
				}
            }
        }
    	return "";
    }

    private static Icon statIcon;
    private static Icon nonstatIcon;

    static {
        statIcon = kz.tamur.rt.Utils.getImageIcon("methodStat");
        nonstatIcon = kz.tamur.rt.Utils.getImageIcon("method");
    }

    class AttributeTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 1L;

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Color c = Color.black;
            Color c1 = Color.white;
            if (value instanceof Kernel.MethodNode) {
                boolean mStatic = ((Kernel.MethodNode) value).getMethod().isClassMethod;
                l.setIcon(mStatic ? statIcon : nonstatIcon);
            } else if (value instanceof CastNode) {
                c = CAST_ATTR_COLOR;
            } else if (value instanceof Kernel.AttrNode) {
                KrnAttribute krnAttr = ((Kernel.AttrNode) value).getKrnAttribute();

                Icon icon = AttributeTreeIconLoader.getIcon(krnAttr.isRepl, krnAttr.isMultilingual, krnAttr.isMandatory(),
                        krnAttr.isIndexed, krnAttr.rAttrId != 0, krnAttr.isAggregate());
                l.setIcon(icon);

                Kernel.AttrNode node = (Kernel.AttrNode) value;
                KrnAttribute attr = node.getKrnAttribute();

                if (attr != null) {
                    if (attr.rAttrId != 0) {
                        c = REV_ATTR_COLOR;
                    } else {
                        Kernel.AttrNode parent = (Kernel.AttrNode) node.getParent();
                        KrnAttribute pattr = parent != null ? parent.getKrnAttribute() : null;
                        if (pattr != null && attr.classId != pattr.typeClassId) {
                            c = INH_ATTR_COLOR;
                        }
                    }
                }
            }
            l.setForeground(sel ? c1 : c);
            l.setBackground(kz.tamur.rt.Utils.getDarkShadowSysColor());
            l.setOpaque(selected);
            return l;
        }
    }
}