package com.cifs.or2.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

/** Класс представляющий узел в иерархии классов */
public class ClassNode implements MutableTreeNode, Comparable<ClassNode> {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ClassNode.class.getName());
    private KrnClass cls_;
    private List<ClassNode> children_;
    private Map<Long, KrnAttribute> attrs_;
    private Map<String, KrnAttribute> attrsByName_;
    private int level;
    private Map<String, KrnMethod> methods;
    
    public long getId() {
    	return cls_.id;
    }

    public String getName() {
    	return cls_.name;
    }

    public ClassNode() {
    	cls_ = new KrnClass();
    	children_ = new ArrayList<ClassNode>();
    }
    
    public ClassNode(KrnClass cls, int level) {
        cls_ = cls;
        children_ = new ArrayList<ClassNode>();
        this.level = level;
    }

    public void add(ClassNode child) {
    	children_.add(child);
    }
    
    /** Конструирует узел в иерархии классов
     *  @param cls класс, который будет представлен в иерархии
     */
    public ClassNode(KrnClass cls) throws KrnException {
        cls_ = cls;
        Kernel krn = SecurityContextHolder.getKernel();
        if (cls.parentId > 0) {
            level = Funcs.add(krn.getClassNode(cls.parentId).level, 1);
        } else {
            level = 0;
        }
        krn.addClass(this);
    }

    /** Возвращает атрибуты класса, представлемого узлом
     *  @return список атрибутов
     */
    public List<KrnAttribute> getAttributes() {
        try {
        	Map<Long, KrnAttribute> attrs = loadAttrs();
            return new ArrayList<KrnAttribute>(attrs.values());
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Добавляет атрибут к классу, представлемого узлом
     *  @param attr атрибут
     */
    public void addAttribute(KrnAttribute attr) {
        try {
        	Map<Long, KrnAttribute> attrs = loadAttrs();
        	if (attrs != null) {
	            attrs.put(attr.id, attr);
	        	Map<String, KrnAttribute> attrs2 = loadAttrsByName();
	            attrs2.put(attr.name, attr);
	            if (children_ != null) {
	                for (int i = 0; i < children_.size(); ++i)
	                    ((ClassNode) children_.get(i)).addAttribute(attr);
	            }
        	}
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /** Возвращает индекс атрибута класса, представлемого узлом
     *  @return индекс атрибута или -1 если такого нет
     */
    public int indexOfAttribute(KrnAttribute attr) {
        try {
        	Map<Long, KrnAttribute> attrs = loadAttrs();
        	List<KrnAttribute> list = new ArrayList<KrnAttribute>(attrs.values());
            for (int i = 0; i < list.size(); ++i) {
                KrnAttribute a = (KrnAttribute) list.get(i);
                if (a.id == attr.id)
                    return i;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Возвращает атрибут класса, представлемого узлом, по его имени
     *  @return атрибут или null если такого нет
     */
    public KrnAttribute getAttribute(String name) {
        try {
        	Map<String, KrnAttribute> attrs = loadAttrsByName();
        	return attrs.get(name);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Удаляет атрибут из класса, представлемого узлом
     *  @param attr удаляемый атрибутов
     */
    public void removeAttribute(KrnAttribute attr) {
        if (attrs_ != null) attrs_.remove(attr.id);
        if (attrsByName_ != null) attrsByName_.remove(attr.name);
        if (children_ != null) {
            for (int i = 0; i < children_.size(); ++i) {
                ((ClassNode) children_.get(i)).removeAttribute(attr);
            }
        }
    }

    /** Возвращает класс, представлемый узлом
     *  @return класс
     */
    public KrnClass getKrnClass() {
        return cls_;
    }

    public void setKrnClass(KrnClass cls) {
        cls_ = cls;
    }

    /** Возвращает узел в виде строки */
    public String toString() {
        return cls_.name;
    }

    /** Сравнивает с узлом */
    public boolean equals(Object o) {
        return cls_.id == ((ClassNode) o).cls_.id;
    }

    public int compareTo(ClassNode n) {
        int res = cls_.name.compareTo(n.cls_.name);
        if (res == 0) {
            if (cls_.id < n.cls_.id)
                res = -1;
            else if (cls_.id > n.cls_.id)
                res = 1;
        }
        return res;
    }

    public int getLevel() {
        return level;
    }

    public String renameClassTable(KrnClass baseClass, String newname)
            throws KrnException {
    	Kernel krn = SecurityContextHolder.getKernel();
    	if (krn != null)
    		newname = krn.renameClassTable(baseClass, newname);
    	if (newname != null) {
    		cls_.tname = newname;
    		return newname;
    	}
    	return null;
    }
    
    public String renameAttrTable(KrnAttribute attr, String newname)
            throws KrnException {
    	Kernel krn = SecurityContextHolder.getKernel();
    	if (krn != null)
    		newname = krn.renameAttrTable(attr, newname);
    	if (newname != null) {
    		cls_.tname = newname;
    		return newname;
    	}
    	return null;
    }

    // Реализация интерфейса MutableTreeNode

    public Enumeration<ClassNode> children() {
        try {
            List<ClassNode> children = loadChildren();
            return Collections.enumeration(children);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public TreeNode getChildAt(int childIndex) {
        try {
            List<ClassNode> children = loadChildren();
            return children.get(childIndex);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getChildCount() {
        try {
            List<ClassNode> children = loadChildren();
            return children.size();
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getIndex(TreeNode node) {
        try {
            List<ClassNode> children = loadChildren();
            return children.indexOf(node);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void getSuperClasses(Collection<ClassNode> supers) {
        ClassNode cn = this;
        while (cn != null) {
            // Добавляем старший класс в список, текущий класс включительно
            supers.add(cn);
            cn = (ClassNode) cn.getParent();
        }
    }

    public void getSubClasses(List<ClassNode> children) {
        int cnt = getChildCount();
        for (int i = 0; i < cnt; i++) {
            ClassNode cn = (ClassNode)getChildAt(i);
            // Добавляем ребенка в список
            children.add(cn);
            // Рекурсивно добавляем всех детей в список
            cn.getSubClasses(children);
        }
    }

    public boolean isLeaf() {
        return children_ == null ? false : children_.size() == 0;
    }

    public void insert(MutableTreeNode child, int index) {
        log.info("ClassNode.insert " + child + " " + index);
        try {
            List<ClassNode> children = loadChildren();
            children.add(index, (ClassNode)child);
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void remove(int index) {
        children_.remove(index);
    }

    public void remove(MutableTreeNode node) {
        children_.remove(node);
    }

    public void removeFromParent() {
        ClassNode parent = (ClassNode) getParent();
        parent.remove(this);
    }

    public void setParent(MutableTreeNode newParent) {
    }

    public void setUserObject(Object object) {
    }
    
    public List<ClassNode> getClassNodes(){
    	try {
			return loadChildren();
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    private List<ClassNode> loadChildren() throws KrnException {
    	Kernel krn = SecurityContextHolder.getKernel();
    	if (krn == null) krn = Kernel.instance();
        if (children_ == null && krn!=null) {
            KrnClass[] classes = krn.getClasses(cls_.id);
            if (classes.length > 0) {
                children_ = new ArrayList<ClassNode>(classes.length);
                for (int i = 0; i < classes.length; ++i) {
                    ClassNode cw = krn.getClassNode(classes[i]);
                    if (cw == null)
                    	cw = new ClassNode(classes[i]);
                    children_.add(cw);
                }
                Collections.sort(children_);
            } else {
            	children_ = new ArrayList<ClassNode>();
            }
        }
        return children_;
    }

    private synchronized Map<Long, KrnAttribute> loadAttrs() throws KrnException {
    	Kernel krn = SecurityContextHolder.getKernel();
        if (attrs_ == null && krn!=null) {
            attrs_ = new HashMap<Long, KrnAttribute>();
            attrsByName_ = new HashMap<String, KrnAttribute>();
            KrnAttribute[] attrs = krn.getAttributesByClassId(cls_);
            if (attrs.length > 0) {
                for (int i = 0; i < attrs.length; ++i) {
                    attrs_.put(attrs[i].id, attrs[i]);
                    if (!attrsByName_.containsKey(attrs[i].name)) attrsByName_.put(attrs[i].name, attrs[i]);
                    krn.putAttrById(attrs[i].id, attrs[i]);
                }
            }
        }
        return attrs_;
    }

    private Map<String, KrnAttribute> loadAttrsByName() throws KrnException {
    	loadAttrs();
        return attrsByName_;
    }

    public void addMethod(KrnMethod m) throws KrnException {
		loadMethods();
    	// Добавляем метод в кэш класса
		if (methods != null) methods.put(m.name, m);
    }

    public void removeMethod(KrnMethod m) throws KrnException {
		loadMethods();
    	// Добавляем метод в кэш класса
    	if (methods != null) methods.remove(m.name);
    }

    public KrnMethod createMethod(String name, boolean isClassMethod, String expr) throws KrnException {
		loadMethods();
		KrnMethod m = null;
		if (methods != null) {
	    	byte[] bs = new byte[0];
	    	try {
	    		bs = expr.getBytes("UTF-8");
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    	}
	    	// Создаем метод на сервере
	    	Kernel krn = SecurityContextHolder.getKernel();
	    	m = krn.createMethod(cls_, name, isClassMethod, bs);
	    	// Добавляем метод в кэш класса
	    	methods.put(m.name, m);
	    	krn.addMethod(m);
		}
    	return m;
    }

    public KrnMethod changeMethod(
    		String uid,
    		String name,
    		boolean isClassMethod,
    		String expr
    		) throws KrnException {
		loadMethods();
		KrnMethod m = null;
		
		if (methods != null) {
	    	m = getMethodByUid(uid);
	    	if (m == null) {
	    		throw new KrnException(0,
	    				"Метод с id=" + uid + " не найден в классе '"
	    				+ cls_.name + "'");
	    	}
	
	    	byte[] bs = new byte[0];
	    	try {
	    		bs = expr.getBytes("UTF-8");
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    	}
	    	Kernel krn = SecurityContextHolder.getKernel();
	    	String oldName=m.name;
	    	m = krn.changeMethod(uid, name, isClassMethod, bs);
	    	methods.remove(oldName);
	    	krn.removeMethodFromCache(uid);
	    	methods.put(m.name, m);
	        krn.methodChange(m);
		}
        return m;
    }

    public synchronized void rollbackMethodByUid(String uid) throws KrnException {
		loadMethods();
		KrnMethod m = null;
		if (methods != null) {
			m = getMethodByUid(uid);
	    	if (m == null) {
	    		throw new KrnException(0,
	    				"Метод с id=" + uid + " не найден в классе '"
	    				+ cls_.name + "'");
	    	}

	    	Kernel krn = SecurityContextHolder.getKernel();
	    	String oldName=m.name;
	    	m = krn.rollbackMethod(uid);
	    	methods.remove(oldName);
	    	krn.removeMethodFromCache(uid);
	    	methods.put(m.name, m);
	        krn.methodChange(m);
		}
    }
    public KrnMethod getMethodByUid(String uid) throws KrnException {
		loadMethods();
		if (methods != null) {
	    	Iterator<Entry<String, KrnMethod>> it =
	    		methods.entrySet().iterator();
	    	while (it.hasNext()) {
	    		Entry<String, KrnMethod> e = it.next();
	    		if (e.getValue().uid.equals(uid)) {
	    			return e.getValue();
	    		}
	    	}
		}
    	return null;
    }

    public KrnMethod getMethod(String name) throws KrnException {
		loadMethods();
		
		if (methods != null) {
	    	KrnMethod res = methods.get(name);
	    	if (res == null) {
	    		ClassNode parent = (ClassNode)getParent();
	    		if (parent != null) {
	    			res = parent.getMethod(name);
	    		}
	    	}
	    	return res;
		}
		return null;
    }

    public List<KrnMethod> getMethods() throws KrnException {
		loadMethods();
		if (methods != null)
			return new ArrayList<KrnMethod>(methods.values());
		else
			return Collections.emptyList();
    }

    public void deleteMethod(KrnMethod method) throws KrnException {
    	loadMethods();
    	
    	if (methods != null) {
	    	// Удаляем метод на сервере
	    	Kernel krn = SecurityContextHolder.getKernel();
	        krn.delMethod(method);
	    	krn.deleteMethod(method.uid);
	    	// Удаляем метод из кэша выражений
	    	krn.removeMethodFromCache(method.uid);
	    	// Удаляем метод из кэша класса
	    	Iterator<Entry<String, KrnMethod>> it =
	    		methods.entrySet().iterator();
	    	while (it.hasNext()) {
	    		Entry<String, KrnMethod> e = it.next();
	    		if (e.getValue().uid.equals(method.uid)) {
	    			it.remove();
	    			break;
	    		}
	    	}
    	}
    }

    public String getMethodExpression(String name) throws KrnException {
    	KrnMethod m = getMethod(name);
    	Kernel krn = SecurityContextHolder.getKernel();
		return krn.getMethodExpression(m.uid);
    }

    public ASTStart getMethodTemplate(String name) throws Throwable {
    	KrnMethod m = getMethod(name);
    	if (m == null) {
    		throw new KrnException(0,
    				"Метод '" + name + "' не найден в классе '" + cls_.name + "'");
    	}
    	Kernel krn = SecurityContextHolder.getKernel();
		return krn.getMethodTemplate(m);
    }
    
    private synchronized void loadMethods() throws KrnException {
    	Kernel krn = SecurityContextHolder.getKernel();
    	if (methods == null && krn != null) {
	    	methods = new HashMap<String, KrnMethod>();
	    	KrnMethod[] ms = krn.getMethods(cls_.id);
	    	for (KrnMethod m : ms) {
	    		methods.put(m.name, m);
	    	}
    	}
    }

    public TreeNode getParent() {
    	Kernel krn = SecurityContextHolder.getKernel();
		return krn.getClassNodeById(cls_.parentId);
    }
}
