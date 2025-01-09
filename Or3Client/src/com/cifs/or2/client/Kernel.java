package com.cifs.or2.client;

import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_LOCAL;
import static kz.tamur.comps.Constants.NAME_CLASS_CONTROL_FOLDER;
import static kz.tamur.comps.Constants.NAME_CLASS_CONTROL_FOLDER_ROOT;
import static kz.tamur.util.CollectionTypes.COLLECTION_NONE;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.DataUtil;
import kz.tamur.util.KrnUtil;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.AnyPair;
import com.cifs.or2.kernel.BlobValue;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.FilterDate;
import com.cifs.or2.kernel.FloatValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnSearchResult;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.LongPair;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.util.MultiMap;
import com.cifs.or2.util.UnaryFunction;

public class Kernel {
    public static final long IC_STRING = 1;
    public static final long IC_INTEGER = 2;
    public static final long IC_TIME = 3;
    public static final long IC_DATE = 4;
    public static final long IC_BOOL = 5;
    public static final long IC_MEMO = 6;
    public static final long IC_MMEMO = 7;
    public static final long IC_FLOAT = 8;
    public static final long IC_MSTRING = 9;
    public static final long IC_BLOB = 10;

    public static KrnClass SC_PLACE;
    public static KrnClass SC_GUICONTAINER;
    public static KrnClass SC_UI;
    public static KrnClass SC_UI_FOLDER;
    public static KrnClass SC_LANGUAGE;
    public static KrnClass SC_GUICOMPONENT;
    public static KrnClass SC_COMBOBOX;
    public static KrnClass SC_TABLE;
    public static KrnClass SC_TREETABLE;
    public static KrnClass SC_COLUMNPLACE;
    public static KrnClass SC_COMBOCOLUMN;
    public static KrnClass SC_HIPERTREE;
    public static KrnClass SC_FILTER;
    public static KrnClass SC_FILTER_FOLDER;
    public static KrnClass SC_SRV_FLR;
    public static KrnClass SC_USER;
    public static KrnClass SC_USER_FOLDER;
    public static KrnClass SC_BASE;
    public static KrnClass SC_CONFIG_LOCAL;
    public static KrnClass SC_NOTE;
    public static KrnClass SC_TIMER;
    public static KrnClass SC_FLOW;
    public static KrnClass SC_PROCESS_DEF;
    public static KrnClass SC_PROCESS_DEF_FOLDER;
    public static KrnClass SC_CONTROL_FOLDER;
    public static KrnClass SC_CONTROL_FOLDER_ROOT;
    public static KrnClass SC_REPORT_PRINTER;
    public static KrnClass SC_REPORT_FOLDER;
    public static KrnClass SC_POPUP;
    public static KrnClass SC_MENUITEMSDESC;
    public static KrnClass SC_IMPORT;
    public static KrnClass SC_EXPORT;
    public static KrnClass SC_BOX_EXCHANGE;
    public static KrnClass SC_BOX_FOLDER;
    
    public static KrnClass SC_PD_POLICY;

    public static KrnObject[] LANGUAGES;
    public static KrnObject[] MENU_ITEMS_HELP;
    /* rm - Run Mode */
    public static final int rm_AUTO = 0;
    public static final int rm_MANUAL = 1;

    /* et - Export Type */
    public static final int et_NONE = -1;
    public static final int et_FIRST_EXPORT = 0;
    public static final int et_SECOND_EXPORT = 1;
    public static final int et_NEXT_EXPORT = 2;

    /* Login type */
    public static final int LOGIN_USUAL = 0;
    public static final int LOGIN_CERT = 1;
    public static final int LOGIN_DN = 2;
    public static final int LOGIN_LDAP = 3;
    public static final int LOGIN_KALKAN = 4;

    protected ArrayList<ClassTreeModel> classTrees_;

    protected Map<Long, ClassNode> cnodesByClassId_;
    protected Map<String, ClassNode> cnodesByClassName_;
    protected Map<String,Long> langsByCode;
    protected Map<String, ASTStart> exprByMethodUid_;

    protected ClassNode hieararchyRoot_;
    protected PackageNode packageHieararchyRoot;
    protected DefaultTreeModel classHierarchy_;
    protected DefaultTreeModel packageHierarchy;
    protected boolean isAutoCommit_ = true;
    protected User user_;
    protected MultiMap<String, Filter> filters_ = new MultiMap<String, Filter>();
    protected MapMap<String, String, EventListenerList> filterParamListeners_ = new MapMap<String, String, EventListenerList>();

    protected PropertyChangeSupport propSupport = new PropertyChangeSupport(this);
    protected Map<Long, KrnAttribute[]> revAttrs = new HashMap<Long, KrnAttribute[]>();
    protected Map<Long, KrnAttribute[]> linkAttrs = new HashMap<Long, KrnAttribute[]>();
    protected Map<Long, KrnAttribute> attrById = new HashMap<Long, KrnAttribute>();

    
    protected KrnObject currDb;
    protected InetAddress address;
    protected String serverHost="localhost";
    protected String serverPort="";
    protected String baseName;

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Kernel.class.getName());
    
    //protected Timer callbackTimer;
    protected Thread callback;
    /** Новый вид интерфейсов.*/
    protected boolean seUI = false;
    /** Спец. вид интерфейсов.*/
    protected boolean advancedUI = false;
    
    /**
     * Для кэширования объектов по UID
     */
    protected static Map<String, KrnObject> objByUid =
    	new WeakHashMap<String, KrnObject>();

    /**
     * Для кэширования объектов по ID
     */
    protected static Map<Long, KrnObject> objById =
    	new WeakHashMap<Long, KrnObject>();
    
    protected boolean allClassesReceived = false;
    public static boolean reloadFlt = false;
    
    /** Выдает иерархию классов в виде модели для дерева
     *  @return модель представляющая иерархию классов
     */
    public synchronized TreeModel getClassHierarchy() {
        if (classHierarchy_ == null)
            classHierarchy_ = new DefaultTreeModel(hieararchyRoot_, false);

        return classHierarchy_;
    }
    
    public synchronized TreeModel getPackageHierarchy() {
        if (packageHierarchy == null)
        	packageHierarchy = new DefaultTreeModel(packageHieararchyRoot, false);

        return packageHierarchy;
    }
    
    /** Выдает иерархию атрибутов классов в виде модели для дерева
     *  @param cls класс, структура атрибутов которого возвращается
     *  @return модель представляющая иерархию атрибутов
     */
    public synchronized TreeModel getClassTree(ClassNode cls) {
    	boolean canViewAttr = getUser().hasRight(Or3RightsNode.ATTRIBUTES_VIEW_RIGHT);
        boolean canViewMethod = getUser().hasRight(Or3RightsNode.METHODS_VIEW_RIGHT);

        CastNode root = new CastNode(null, cls, canViewAttr, canViewMethod);

        try {
            root.load();
        } catch (KrnException e) {
            e.printStackTrace();
        }

        ClassTreeModel model = new ClassTreeModel(root, classTrees_.size() + 1);
        classTrees_.add(model);

        return model;
    }

    /** Освобождает ресурсы, занимаемые моделью дерева атрибутов.
     *  Должна обязательно вызываться по окончании работы с моделью дерва
     *  атрибутов
     *  @param model модель
     */
    public synchronized void releseClassTree(TreeModel model) {
        classTrees_.remove(model);
    }
    
    public static void setInstance(Kernel krn) {
    	inst_ = krn;
        if (SecurityContextHolder.getKernel() == null)
        	SecurityContextHolder.setKernel(inst_);
    }

    /** Возвращает объект Kernel. Применен шаблон проектирования Singleton
     *  @return объект Kernel
     */
    public static synchronized Kernel instance() {
        if (inst_ == null) {
            inst_ = new Kernel();
        }
        if (SecurityContextHolder.getKernel() == null)
        	SecurityContextHolder.setKernel(inst_);
        return inst_;
    }

    public boolean isAlive() {
        try {
            return s_.ping(us.id);
        } catch (Exception e) {
        	log.error(e, e);
            return false;
        }
    }

    public Note[] getNotes() {
        return s_.getNotes(us.id);
    }
    
    public synchronized long createLongTransaction() throws KrnException {
        return s_.createLongTransaction(us.id);
    }

    public synchronized void commitLongTransaction(long tid, long otid)
            throws KrnException {
        s_.commitLongTransaction(us.id, tid);
    }

    public synchronized void rollbackLongTransaction(long tid)
            throws KrnException {
        s_.rollbackLongTransaction(us.id, tid);
    }

    public synchronized void updateReferences(long cid, long aid)
            throws KrnException {
        s_.updateReferences(us.id, cid, aid);
    }

    public synchronized void deleteUnusedObjects(long cid, long aid)
            throws KrnException {
        s_.deleteUnusedObjects(us.id, cid, aid);
    }

    public synchronized long[] getSelectedBases() throws KrnException {
        return s_.getSelectedBases(us.id);
    }

    public synchronized void selectBases(long[] baseIds) throws KrnException {
        s_.selectBases(us.id, baseIds);
        propSupport.firePropertyChange("bases", null, baseIds);
    }

    /** Осуществляет освобождение ресурсов сервера
     *  и полное отключение от него.
     *  Должна быть вызвана перед завершением приложения во избежание
     *  утечек ресурсов сервера
     */
    public synchronized void release() {
    	if (callback != null) {
    		callback.interrupt();
    		callback = null;
    	}

/*    	if (callbackTimer != null) {
    		callbackTimer.cancel();
    		callbackTimer = null;
    	}
*/      if (s_ != null && us != null) {
            s_.release(us.id);
            s_ = null;
		}
    }

    /** Возвращает класс по его идентификатору
     *  @param id идентификатор класса
     *  @return класс
     */
    public synchronized KrnClass getClass(long id) throws KrnException {
        ClassNode cnode = getClassNode(id);
        return (cnode == null) ? null : cnode.getKrnClass();
    }

    /** Возвращает узел в дереве классов по идентификатору класса
     *  @param id идентификатор класса
     *  @return узел в дереве классов
     */
    public synchronized ClassNode getClassNode(long id) throws KrnException {
        ClassNode cnode = cnodesByClassId_.get(new Long(id));
        if (cnode == null) {
        	KrnClass cls = s_.getClassById(us.id, id);
        	if (cls != null) {
        		cnode = new ClassNode(cls);
        		addClass(cnode);
        	}
        }
        return cnode;
    }

    /** Возвращает класс по его имени
     *  @param name имя класса
     *  @return класс
     */
    public synchronized KrnClass getClassByName(String name)
            throws KrnException {
        ClassNode cnode = getClassNodeByName(name);
        return (cnode == null) ? null : cnode.getKrnClass();
    }
    
    public synchronized List<KrnClass> getClassesByNameWithOptions(String name, long searchMethod)
            throws KrnException {
    	List<KrnClass> clses = new ArrayList<KrnClass>();
        List<ClassNode> cnodes = getClassNodesByNameWithOptions(name, searchMethod);
        if (cnodes.size() > 0) {
        	for(ClassNode cnode: cnodes) {
        		if(cnode != null)
        		clses.add(cnode.getKrnClass());
        	}
        }
        return clses;
    }
    
    /** Возвращает класс по его имени
     *  @param name имя класса
     *  @return <code>true</code> если класс найден
     */
    public synchronized boolean checkExistenceClassByName(String name) {
        ClassNode cnode;
        try {
            cnode = getClassNodeByName(name);
            return cnode != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** Возвращает класс по его идентификатору
     *  @param id идентификатор класса
     *  @return класс
     */
/*    public synchronized KrnClass getClassById(long id) throws KrnException {
        return s_.getClassById(us.id, id);
    }
*/    
    public List<KrnClass> getClasses(long baseClassId, boolean withSubclasses) throws KrnException {
    	return s_.getClasses(us.id, baseClassId, withSubclasses);
    }
    
    public synchronized KrnClass[] getClasses(long clsId) throws KrnException {
    	return s_.getClasses(us.id, clsId);
    }
    
    public synchronized KrnClass[] getClasses() throws KrnException {
    	return s_.getClasses(us.id);
    }

    public String renameClassTable(KrnClass cls, String newname) throws KrnException {
    	return s_.renameClassTable(us.id, cls, newname);
    }
    
    public String renameAttrTable(KrnAttribute attr, String newname) throws KrnException {
    	return s_.renameAttrTable(us.id, attr, newname);
    }

    /** Возвращает все классы
     *  @return массив классов
     */
    public synchronized void getClasses(Collection<ClassNode> cnodes) throws KrnException{
    	if (!allClassesReceived) {
        	KrnClass[] clss = s_.getClasses(us.id);
        	for (KrnClass cls : clss) {
                ClassNode cnode = cnodesByClassId_.get(cls.id);
                if (cnode == null) {
                	cnode = new ClassNode(cls);
                	addClass(cnode);
                }
                cnodes.add(cnode);
        	}
        	allClassesReceived = true;
    	} else {
    		cnodes.addAll(cnodesByClassId_.values());
    	}
    }

    /** Возвращает узел в дереве классов по имени класса
     *  @param name имя класса
     *  @return узел в дереве классов
     */
    public synchronized ClassNode getClassNodeByName(String name)
            throws KrnException {
        ClassNode cnode = cnodesByClassName_.get(name);
        if (cnode == null) {
        	KrnClass cls = s_.getClassByName(us.id, name);
        	if (cls != null) {
	            cnode = new ClassNode(cls);
		        addClass(cnode);
        	}
        }
        return cnode;
    }
    
    public synchronized List<ClassNode> getClassNodesByNameWithOptions(String name, long searchMethod)
            throws KrnException {
    	String entryS = new String();
    	String nameS = name.toString().toUpperCase(Constants.OK);
    	List<ClassNode> cnodes = new ArrayList<ClassNode>();
    	if (searchMethod == ComparisonOperations.SEARCH_START_WITH) 
    		for (Map.Entry<String, ClassNode> entry : cnodesByClassName_.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.startsWith(nameS)) {
        			cnodes.add(cnodesByClassName_.get(entry.getKey()));
        		}
        	}
    	else if (searchMethod == ComparisonOperations.CO_EQUALS) 
    		for (Map.Entry<String, ClassNode> entry : cnodesByClassName_.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.equals(nameS)) {
        			cnodes.add(cnodesByClassName_.get(entry.getKey()));
        		}
        	}
    	else if (searchMethod == ComparisonOperations.SEARCH_START_WITH) 
    		for (Map.Entry<String, ClassNode> entry : cnodesByClassName_.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.contains(nameS)) {
        			cnodes.add(cnodesByClassName_.get(entry.getKey()));
        		}
        	}
    	
        if (cnodes.size() == 0) {
        	List<KrnClass> clses = s_.getClassesByNameWithOptions(us.id, name, searchMethod);
        	if (clses.size() > 0) 
        		for (KrnClass cls: clses) {
        			ClassNode cnode = new ClassNode(cls);
        			addClass(cnode);
        			cnodes.add(cnode);
        		}
        }
        return cnodes;
    }

    public synchronized void setAutoCommit(boolean isAutoCommit) {
        isAutoCommit_ = isAutoCommit;
    }

    public synchronized Map<String, Object> getInterfaceVars(long flowId)
    		throws KrnException {
    	
    	return s_.getInterfaceVars(us.id, flowId);
    }

    public synchronized LongPair[] commit2(
    		List<CacheChangeRecord> changes,
            long tid
    ) throws KrnException {
        return s_.commit2(us.id, changes, tid);
    }

    /** Создает новый класс в системе
     *  @param baseClass базовый класс
     *  @param name имя создаваемого класса
     *  @return узел в дереве классов
     */
    public synchronized void createClass(KrnClass baseClass, String name, boolean isRepl, String tname, String comment, int mod) throws KrnException {
        KrnClass newClass = s_.createClass(us.id, baseClass, name, isRepl, tname, mod);

    	// Запись комментария класса
        if (comment.length() > 0) {
            setClassComment(newClass.uid, comment);
        }

        // Добавление записи в ClassTree
        ClassNode baseClassNode = getClassNode(baseClass);
        ClassNode childClassNode = new ClassNode(newClass);
        if (classHierarchy_ != null) {
            classHierarchy_.insertNodeInto(childClassNode, baseClassNode, baseClassNode.getChildCount());
        }
        
        // Добавление записи в PackageTree
        int index = newClass.getName().lastIndexOf("::");
        if (index > 0) {
        	String packageFullPath = newClass.getName().substring(0, index);
            PackageNode basePackageNode = PackageNode.getPackageNodeByPackageName(packageFullPath);
            if (basePackageNode != null) {
				String nodeName = newClass.getName().substring(index + 2);
				PackageNode childPackageNode = new PackageNode(newClass, nodeName);
		        if (packageHierarchy != null) {
		        	packageHierarchy.insertNodeInto(childPackageNode, basePackageNode, basePackageNode.getChildCount());
		        }
            } else {
            	PackageNode basePackageNode1 = null;
            	while(basePackageNode == null) {
            		if (packageFullPath.contains("::")) {
            			String packageName = packageFullPath.substring(0, packageFullPath.indexOf("::"));
            			String path = (basePackageNode1 == null ? "" : basePackageNode1.getPackageFullPath() + "::") + packageName;
            			if (PackageNode.getPackageNodeByPackageName(path) == null) {
            				PackageNode childPackageNode = new PackageNode(new Package(packageName, basePackageNode1 == null ? packageHieararchyRoot.getPackage() : basePackageNode1.getPackage()), path);
            		        if (packageHierarchy != null) {
            		        	packageHierarchy.insertNodeInto(childPackageNode, basePackageNode1 == null ? packageHieararchyRoot : basePackageNode1, basePackageNode1 == null ? packageHieararchyRoot.getChildCount() : basePackageNode1.getChildCount());
            		        }
                        	basePackageNode1 = childPackageNode;
            			}
            			packageFullPath = packageFullPath.substring(packageFullPath.indexOf("::") + 2);	
            		} else {
            			String packageName = packageFullPath;
            			String path = basePackageNode1 == null ? "" : basePackageNode1.getPackageFullPath() + "::" + packageName;
            			basePackageNode = PackageNode.getPackageNodeByPackageName(path);
            			if (basePackageNode == null) {
            				basePackageNode = new PackageNode(new Package(packageName, basePackageNode1 == null ? packageHieararchyRoot.getPackage() : basePackageNode1.getPackage()), path);
            		        if (packageHierarchy != null) {
            		        	packageHierarchy.insertNodeInto(basePackageNode, basePackageNode1 == null ? packageHieararchyRoot : basePackageNode1, basePackageNode1 == null ? packageHieararchyRoot.getChildCount() : basePackageNode1.getChildCount());
            		        }
            			}
            		}
            	}
				String nodeName = newClass.getName().substring(index + 2);
				PackageNode childPackageNode = new PackageNode(newClass, nodeName);
		        if (packageHierarchy != null) {
		        	packageHierarchy.insertNodeInto(childPackageNode, basePackageNode, basePackageNode.getChildCount());
		        }
            }
        } else {
            PackageNode basePackageNode = PackageNode.getPackageNodeByPackageName("(default package)");
    		String nodeName = newClass.getName();
    		if (nodeName.contains("::")) {
    			nodeName = nodeName.substring(index + 2);
    		}
            PackageNode childPackageNode = new PackageNode(newClass, nodeName);
	        if (packageHierarchy != null) {
	        	packageHierarchy.insertNodeInto(childPackageNode, basePackageNode, basePackageNode.getChildCount());
	        }
        }
    }

    public synchronized KrnClass changeClass(
    		KrnClass cls,
    		KrnClass baseCls,
    		String name,
    		boolean isRepl
    ) throws KrnException {
		return s_.changeClass(us.id, cls, baseCls, name, isRepl);
    }


    /** Удаляет класс из системы
     *  @param cls класс
     */
    public synchronized void deleteClass(KrnClass cls) throws KrnException {
    	s_.deleteClass(us.id, cls);
        ClassNode cnode = removeClass(cls);

        if (classHierarchy_ != null) {
            classHierarchy_.removeNodeFromParent(cnode);
        }
        
        PackageNode pnode = PackageNode.packageNodesByClassId.remove(cls.id);
        if (pnode != null && packageHierarchy != null) {
        	packageHierarchy.removeNodeFromParent(pnode);
        }
    }

    /** Возвращает атрибуты класса
     *  @param cls класс
     *  @return список атрибутов
     */
    public synchronized List<KrnAttribute> getAttributes(KrnClass cls) {
        ClassNode cnode = cnodesByClassId_.get(
                new Long(cls.id)
        );
        return cnode.getAttributes();
    }

    /**
     * Возвращает атрибуты заданного типа класса
     * 
     * @param classId
     *            класс
     * @param inherited
     *            брать наследуемые?
     * @return список атрибутов
     */
    public synchronized List<KrnAttribute> getAttributesByTypeId(long classId, boolean inherited) throws KrnException {
        List<KrnAttribute> res = new ArrayList<KrnAttribute>();
        KrnAttribute[] attrs = s_.getAttributesByTypeId(us.id, classId, inherited);
        for (int i = 0; i < attrs.length; i++)
            res.add(attrs[i]);
        return res;
    }

    /**
     * Возвращает атрибуты с заданным именем
     * 
     * @param name
     *            имя атрибута
     * @return список атрибутов
     */
    public synchronized List<KrnAttribute> getDependAttrs(KrnAttribute attr) throws KrnException {
        return s_.getDependAttrs(us.id,attr);
    }
    /**
     * Возвращает атрибуты с заданным именем
     * 
     * @param name
     *            имя атрибута
     * @return список атрибутов
     */
    public synchronized List<KrnAttribute> getAttributesByName(String name, long searchMethod) throws KrnException {
        List<KrnAttribute> res = s_.getAttributesByName(us.id,name, searchMethod);
        return res;
    }
    
    public synchronized KrnAttribute[] getAttributesByClassId(KrnClass cls) throws KrnException {
        return s_.getAttributes(us.id, cls);
    }

    /** Поиск строки и.или UID для панели Search
     *  @param objTitle строка поиска
     *  @param objUID   UID объекта тип атрибута
     */
    public synchronized List<KrnSearchResult> getConfigsByConditions(String objTitle, String objUID)
    			throws KrnException {
		return s_.getConfigsByConditions(us.id, objTitle, objUID);
    }

    public synchronized KrnAttribute createAttribute(
            KrnClass cls,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier
            ) throws KrnException {
        return createAttribute(cls, type, name, collType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
    }
    
    /** Создает новый атрибут
     *  @param cls класс, в котором создается атрибут
     *  @param type тип атрибута
     *  @param name имя атрибута
     *  @param collType является ли атрибут множественным
     *  @param isUnique является ли атрибут уникальным
     *  @return созданный атрибут
     */
    public synchronized KrnAttribute createAttribute(
            KrnClass cls,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypt
            ) throws KrnException {
        KrnAttribute attr = s_.createAttribute(
        		us.id, cls, type, name, collType, isUnique, isIndexed,
        		isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypt);
        addAttribute(attr);
        return attr;
    }
    
    public synchronized KrnAttribute changeAttribute(
            KrnAttribute attr,
            KrnClass type,
            String name,
            int colType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier) throws KrnException {
        return changeAttribute(attr, type, name, colType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
    }

    public synchronized KrnAttribute changeAttribute(
            KrnAttribute attr,
            KrnClass type,
            String name,
            int colType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypt) throws KrnException {
        long oldTypeId = attr.typeClassId;
        final KrnAttribute newAttr = s_.changeAttribute(
        		us.id, attr, type, name, colType, isUnique, isIndexed,
        		isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypt);

        attrById.put(attr.id, newAttr);

//        for (int i = 0; i < classTrees_.size(); ++i) {
//            ClassTreeModel model = classTrees_.get(i);
//
//            List<AttrNode> anodes = new LinkedList<AttrNode>();
//            try {
//                ((AttrNode) model.getRoot()).findNodes(
//                        false,
//                        anodes,
//                        new UnaryFunction() {
//                            public boolean check(Object o) {
//                                if (o instanceof MethodNode) {
//                                    return false;
//                                }
//                                return (newAttr.id == ((AttrNode) o).attr_.id);
//                            }
//                        }
//                );
//            } catch (KrnException e) {
//                e.printStackTrace();
//            }
//
//            for (Iterator<AttrNode> it = anodes.iterator(); it.hasNext();) {
//                AttrNode node = it.next();
//                CommonUtil.copy(node.attr_, newAttr);
//                if (oldTypeId != newAttr.typeClassId) {
//                    if (node.children_ != null)
//                        node.children_.clear();
//                    node.loaded_ = false;
//                }
//                model.nodeChanged(node);
//            }
//        }
        
        removeAttribute(attr);
        addAttribute(newAttr);
        
        return attr;
    }

    /** Удаляет атрибут
     *  @param attr удаляемый атрибут
     */
    public synchronized void deleteAttribute(KrnAttribute attr)
            throws KrnException {
        s_.deleteAttribute(us.id, attr);
        removeAttribute(attr);
    }

    /** Возвращает уникальный атрибут
     *  @param cls класс в котром ищется атрибут
     *  @return уникальный атрибут или null если такого нет
     */
    public synchronized KrnAttribute findUniqueAttribute(KrnClass cls) {
        List<KrnAttribute> attrs = getAttributes(cls);
        if (attrs != null) {
            for (int i = 0; i < attrs.size(); ++i) {
                KrnAttribute attr = attrs.get(i);
                if (attr.isUnique)
                    return attr;
            }
        }
        return null;
    }

    /** Возвращает объекты класса
     *  @param cls класс
     *  @return массив всех объектов класса
     */
    public synchronized KrnObject[] getClassObjects(KrnClass cls, long tid)
            throws KrnException {
        return s_.getClassObjects(us.id, cls, new long[0], tid);
    }
    
    public synchronized KrnObject[] getClassOwnObjects(KrnClass cls, long tid)
    		throws KrnException {
    	return s_.getClassOwnObjects(us.id, cls, tid);
    }
    
    public synchronized Map<String, String> getStringUidMap(String[] scopeUids)
    		throws KrnException {
    	return s_.getStringUidMap(us.id, scopeUids);
    }

    public synchronized KrnObject[] getClassObjects(KrnClass cls,
			long[] filterIds, long tid) throws KrnException {
		return s_.getClassObjects2(us.id, cls, filterIds, new int[1], tid);
	}

    public synchronized KrnObject[] getClassObjects(KrnClass cls,
                                                    long[] filterIds,
                                                    int[] limit,
                                                    long tid)
            throws KrnException {
    	if (limit[0] == 0) {
    		return s_.getClassObjects(us.id, cls, filterIds, tid);
    	} else {
    		return s_.getClassObjects2(us.id, cls, filterIds, limit, tid);
    	}
    }
    
	public List<Object[]> getClassObjects(
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int[] limit,
			long tid
	) throws KrnException {
		return getClassObjects(cls, req, filterIds, limit, tid, null);
	}
	
	public List<Object[]> getClassObjects(
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int[] limit,
			long tid,
			String info
	) throws KrnException {
		if (info != null && user_ != null)
			info = "USER:" + user_.getName() + " " + info;
		QueryResult res = s_.getClassObjects3(us.id, cls, req, filterIds, limit[0], tid, info);
		limit[0] = res.totalRows;
		return res.rows;
	}
	public synchronized List<KrnObject> filter(KrnObject filterObj, int limit, long trId) throws KrnException
	{
	    return s_.filter(us.id,filterObj,limit,trId);
	}
    
    public String compileFilter(long filterId,Element xml) throws KrnException {
        return s_.compileFilter(us.id, filterId,xml);
    }

    public List<KrnObject> filterLocal(String sql, int limit,int beginRow,int endRow, long trId) throws KrnException
	{
	    return s_.filterLocal(us.id,sql,limit,beginRow,endRow,trId);
	}
    public synchronized KrnObject[] getObjectsByAttribute(
            long classId,
            long attrId,
            long langId,
            int op,
            Object value,
            long tid
            ) throws KrnException {
        return s_.getObjectsByAttribute(
        		us.id, classId, attrId, langId, op, value, tid);
    }
    public synchronized KrnObject[] getObjectsByAttribute(
            long classId,
            long attrId,
            long langId,
            int op,
            Object value,
            long tid,
            KrnAttribute[] krnAttrs
            ) throws KrnException {
        return s_.getObjectsByAttribute(
                us.id, classId, attrId, langId, op, value, tid,krnAttrs);
    }

    public synchronized int getMaxIndex(long objectId, long attrId, long langId, long tid)
            throws KrnException {
        return s_.getMaxIndex(us.id, objectId,attrId,langId, tid);
    }

    public synchronized KrnIndex createIndex(KrnClass cls,KrnAttribute[] attrs,boolean[] descs) throws KrnException{
    	KrnIndex ret = s_.createIndex(us.id, cls, attrs,descs);
    	return ret;
    }
    
    public synchronized KrnIndex[] getIndexesByClassId(KrnClass cls) throws KrnException {
    	return s_.getIndexesByClassId(us.id, cls);
    }
    
    public synchronized KrnIndexKey[] getIndexKeysByIndexId(KrnIndex ndx) throws KrnException{
    	return s_.getIndexKeysByIndexId(us.id, ndx);
    }
    public synchronized KrnAttribute[] getAttributesForIndexing(KrnClass cls) throws KrnException{
    	return s_.getAttributesForIndexing(us.id, cls);
    }
    
    public synchronized void deleteIndex(KrnIndex ndx) throws KrnException {
    	s_.deleteIndex(us.id, ndx);
    }
    
    /** Создает новый объект
     *  @param cls класс объекта
     *  @return вновь созданный объект
     */
    public synchronized KrnObject createObject(KrnClass cls, long tid)
        throws KrnException {
        KrnObject res = s_.createObject(us.id, cls, tid);
        return res;
    }

    public synchronized KrnObject createObject(KrnClass cls, String uid, long tid)
			throws KrnException {
		KrnObject res = s_.createObjectWithUid(us.id, cls, uid, tid);
		return res;
	}

    /** Удаляет объект
     *  @param obj удаляемый объекта
     */
    public synchronized void deleteObject(KrnObject obj, long tid)
            throws KrnException {
        s_.deleteObject(us.id, obj, tid);
    }

    /** Устанавливает значение атрибута объекта типа Объект
     *  @param objectId id объекта, чей атрибут устанавливается
     * @param attrId id устанвливаемого атрибута
     * @param index индекс (0 - если атрибут не множественный)
     * @param val id объекта - значения
     * @param insert
     */
    public synchronized void setObject(
            long objectId,
            long attrId,
            int index,
            long val,
            long tid, boolean insert) throws KrnException {
    	s_.setObject(us.id, objectId, attrId, index, val, tid, insert);
    }

    /** Устанавливает значение атрибута объекта типа Объект
     *  @param objectId id объекта, чей атрибут устанавливается
     * @param classId id класса объекта, чей атрибут устанавливается
     * @param attrName имя устанвливаемого атрибута
     * @param index индекс (0 - если атрибут не множественный)
     * @param val id объекта - значения
     * @param insert
     */
    public synchronized void setObject(
            long objectId,
            long classId,
            String attrName,
            int index,
            long val,
            long tid, boolean insert) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setObject(objectId, (int)attr.id, index, val, tid, insert);
    }

    /** Устанавливает значение атрибута объекта типа Строка
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId id устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setString(
            long objectId,
            long attrId,
            int index,
            long langId,
            String val,
            long tid
            ) throws KrnException {
    	s_.setString(us.id, objectId, attrId, index, langId, false, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Строка
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setString(
            long objectId,
            long classId,
            String attrName,
            int index,
            long langId,
            String val,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setString(objectId, (int)attr.id, index, langId, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Memo
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId id устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setMemo(
            int objectId,
            int attrId,
            int index,
            int langId,
            String val,
            int tid
            ) throws KrnException {
    	s_.setString(us.id, objectId, attrId, index, langId, true, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Memo
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setMemo(
            int objectId,
            int classId,
            String attrName,
            int index,
            int langId,
            String val,
            int tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setMemo(objectId, (int)attr.id, index, langId, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа blob
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId id устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setBlob(
            long objectId,
            long attrId,
            int index,
            byte[] val,
            long langId,
            long tid
            ) throws KrnException {
    	s_.setBlob(us.id, objectId, attrId, index, val, langId, tid);
    }

    /** Устанавливает значение атрибута объекта типа blob
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setBlob(
            long objectId,
            long classId,
            String attrName,
            int index,
            byte[] val,
            long langId,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setBlob(objectId, (int)attr.id, index,val, langId, tid);
    }

    /** Устанавливает значение атрибута объекта типа Целое
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setLong(
            long objectId,
            long attrId,
            int index,
            long val,
            long tid
            ) throws KrnException {
    	s_.setLong(us.id, objectId, attrId, index, val, tid);
    }

    // Сырая реализация. Метод используется только для массового сохранения свойств роли пользователей 
    public synchronized void setLong(List<Long> objectsIds, long attrId, long val, long tid) throws KrnException {
    	s_.setLong(us.id, objectsIds, attrId, val, tid);
    }
    
    /** Устанавливает значение атрибута объекта типа Целое
     *  @param objectId объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setLong(
            long objectId,
            long classId,
            String attrName,
            int index,
            long val,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setLong(objectId, attr.id, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Float
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setFloat(
            long objectId,
            long attrId,
            int index,
            double val,
            long tid
            ) throws KrnException {
    	s_.setFloat(us.id, objectId, attrId, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Float
     *  @param objectId объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setFloat(
            int objectId,
            int classId,
            String attrName,
            int index,
            double val,
            int tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setFloat(objectId, (int)attr.id, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Date
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setDate(
            long objectId,
            long attrId,
            int index,
            Date val,
            long tid
            ) throws KrnException {
    	s_.setDate(us.id, objectId, attrId, index, kz.tamur.util.Funcs.convertDate(val), tid);
    }

    /** Устанавливает значение атрибута объекта типа Date
     *  @param objectId объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setDate(
            int objectId,
            int classId,
            String attrName,
            int index,
            Date val,
            int tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setDate(objectId, (int)attr.id, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Time
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setTime(
            long objectId,
            long attrId,
            int index,
            Date val,
            long tid
            ) throws KrnException {
    	s_.setTime(us.id, objectId, attrId, index, kz.tamur.util.Funcs.convertTime(val), tid);
    }

    /** Устанавливает значение атрибута объекта типа Time
     *  @param objectId объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public synchronized void setTime(
            long objectId,
            long classId,
            String attrName,
            int index,
            Date val,
            int tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setTime(objectId, attr.id, index, val, tid);
    }

    /** Возвращает значения атрибута объекта типа Объект
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public synchronized KrnObject[] getObjects(KrnObject obj, KrnAttribute attr,
                                               long tid) throws KrnException {
        return getObjects(obj, attr, new long[0], tid);
    }

    public synchronized KrnObject[] getObjectsLiveOfClass(KrnClass cls) throws KrnException {
        return s_.getObjectsLiveOfClass(us.id, cls);
    }

    public synchronized KrnObject[] getObjects(KrnObject obj, KrnAttribute attr, long[] filterIds, long tid) throws KrnException {
        return s_.getObjects(us.id, obj.id, attr.id, filterIds, tid);
    }

    /** Возвращает значения атрибута объекта типа Объект
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public synchronized KrnObject[] getObjects(KrnObject obj, String attrName, long tid) throws KrnException {
        return getObjects(obj, attrName, new long[0], tid);
    }

    public KrnObject[] getObjects(KrnObject obj, String attrName, long[] filterIds, long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getObjects(obj, attr, filterIds, tid);
    }

    //Service
    public synchronized int[] pushObjectAtion(
            int actId,
            int objId,
            int[] r_id,
            int rp_id,
            int lId,
            int forward
            ) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public synchronized int[] setSrvObject(
            int[] roleId,
            int actId,
            int objId,
            int tId,
            int tId_in,
            boolean cre_tId
            ) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public synchronized void  changeTimerTask(long objId, boolean isDelete ) throws KrnException {
        s_.changeTimerTask(us.id, objId,isDelete);
    }

    public synchronized void  executeTask(long objId) throws KrnException {
        s_.executeTask(us.id, objId);
    }

    public synchronized int[] setDynActObject(
            int[] roleObjId,
            int actId,
            int objId,
            int tId,
            boolean cre_tId
            ) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public synchronized SuperMap[] getMapList(long[] flowIds) throws KrnException {
        return s_.getMapList(us.id, flowIds);
    }
    public synchronized long getTasksCount() throws KrnException {
        return s_.getTasksCount(us.id);
    }
    public synchronized Activity[] getTaskList() throws KrnException {
        return s_.getTaskList(us.id);
    }
    public synchronized void setTaskListFilter(Map<String, Object> params) throws KrnException {
        AnyPair[] apars=new AnyPair[params.size()];
        int i=0;
            for(Iterator<String> it=params.keySet().iterator();it.hasNext();){
                String key=it.next();
                Object param =params.get(key);
                if(param instanceof Date)
                     param=kz.tamur.util.Funcs.convertDate((Date)param);
                apars[i++]=new AnyPair(key,param);
        }
        s_.setTaskListFilter(us.id, apars);
    }
    
    public synchronized boolean showUserDB() {
    	try {
    		return s_.showUserDB(us.id);
    	} catch (KrnException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public synchronized Activity getTask(long flowId,long ifsPar, boolean isCheckEvent, boolean onlyMy) throws KrnException {
        return s_.getTask(us.id, flowId, ifsPar, isCheckEvent, onlyMy);
    }
    
    public synchronized void updateUsers(KrnObject[] obj) throws KrnException {
    	s_.updateUsers(us.id, obj);
    }
    
    public synchronized void updateUser(KrnObject obj, String name) throws KrnException {
        s_.updateUser(us.id, obj, name);
    }

    public synchronized void userCreated(String name) throws KrnException {
        s_.userCreated(us.id, name);
    }

    public synchronized void userDeleted(String name) throws KrnException {
        s_.userDeleted(us.id, name);
    }

    public synchronized void userRightsChanged(String name) throws KrnException {
        s_.userRightsChanged(us.id, name);
    }

    public synchronized void userBlocked(String name) throws KrnException {
        s_.userBlocked(us.id, name);
    }

    public synchronized void userUnblocked(String name) throws KrnException {
        s_.userUnblocked(us.id, name);
    }

    public synchronized boolean setSelectedObjects(long flowId,long nodeId,KrnObject[] sel_objs) throws KrnException {
        return s_.setSelectedObjects(us.id, flowId,nodeId,sel_objs);
    }

    public synchronized void setLang(KrnObject lang) throws KrnException {
        s_.setLang(us.id, lang);
    }
    
    public synchronized void reloadProcessDefinition(long processDefId) throws KrnException {
        s_.reloadProcessDefinition(us.id, processDefId);
    }

    public synchronized long[] getProcessDefinitions() throws KrnException {
        return s_.getProcessDefinitions(us.id);
    }

    public synchronized String[] startProcess(long processDefinition, Map<String, Object> vars) throws KrnException, ProcessException{
        return s_.startProcess(us.id, processDefinition, vars);
    }

    public synchronized boolean cancelProcess(long processId, String nodeId, boolean isAll, boolean forceCancel) throws KrnException {
        return s_.cancelProcess(us.id, processId, nodeId, isAll, forceCancel);
    }
    
    public synchronized boolean reloadFlow(long flowId) throws KrnException {
        return s_.reloadFlow(us.id, flowId);
    }

    public synchronized void setPermitPerform( long flowId, boolean permit ) throws KrnException {
        s_.setPermitPerform(us.id, flowId,permit);
    }
    public synchronized Object openInterface( long uiId,long flowId,long trId,long pdId) throws KrnException {
        return s_.openInterface(us.id, uiId,flowId,trId,pdId);
    }
    public synchronized String[] performActivitys(Activity[] activitys,String transition,String event) throws KrnException {
        return s_.performActivitys(us.id, activitys,transition,event);
    }
    public synchronized String[] performActivitys(Activity[] activitys,String transition) throws KrnException {
        return s_.performActivitys(us.id, activitys,transition,null);
    }

    public synchronized void startTransport(int transportId) throws KrnException {
        s_.startTransport(us.id, transportId);
    }
    public synchronized void restartTransport(int transportId) throws KrnException {
        s_.restartTransport(us.id, transportId);
    }
    public synchronized String resendMessage(Activity act) throws KrnException {
        return s_.resendMessage(us.id, act);
    }
    public synchronized byte[] getTransportParam(int transportId) throws KrnException {
        return s_.getTransportParam(us.id, transportId);
    }
    public synchronized void setTransportParam(byte[] data,int transportId) throws KrnException {
        s_.setTransportParam(us.id, data,transportId);
    }
    public synchronized void reloadBox(KrnObject obj) throws KrnException {
        s_.reloadBox(us.id, obj);
    }
    public synchronized void saveFilter(long filterId) throws KrnException {
        s_.saveFilter(us.id, filterId);
    }
    public synchronized ObjectValue[] getObjectValues(
            long[] objIds, KrnAttribute attr, long tid) throws KrnException {
        return getObjectValues(objIds, attr.classId, attr, new long[0], tid);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            KrnAttribute attr,
            long[] filterIds,
            long tid
            ) throws KrnException {
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, tid);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            KrnAttribute attr,
            long[] filterIds,
            int[] limit,
            long tid
            ) throws KrnException {
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, limit, tid);
    }

    public QueryResult getObjectValues(
            long[] objIds,
            long classId,
            KrnAttribute attr,
            long[] filterIds,
            long tid,
            AttrRequest req
            ) throws KrnException {
    	return s_.getObjectValues(us.id, objIds, attr.id, filterIds, tid, req);
    }

    public QueryResult getObjectValues(
            long[] objIds,
            long classId,
            KrnAttribute attr,
            long[] filterIds,
            int[] limit,
            long tid,
            AttrRequest req
            ) throws KrnException {
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, limit, tid, req);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            String attrName,
            long tid
            ) throws KrnException {
        return getObjectValues(objIds, classId, attrName, new long[0], tid);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            String attrName,
            long[] filterIds,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, /*new int[1], */tid);
    }

    /** Возвращает значения атрибута объекта типа Строка
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public synchronized String[] getStrings(KrnObject obj, KrnAttribute attr,
                                            long langId, long tid)
            throws KrnException {
    	if (attr.isMultilingual && langId == 0) {
    		langId = getDataLanguage().id;
    	}
        String[] strings = s_.getStrings(us.id, obj.id, attr.id, langId, false, tid);
        return strings;
    }

    /** Возвращает значения атрибута объекта типа Строка
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public synchronized String[] getStrings(KrnObject obj, String attrName,
                                            long langId, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getStrings(obj, attr, langId, tid);
    }

    public synchronized byte[] getBlob(long objId, KrnAttribute attr, int index, long langId,
                                       long tid) throws KrnException {
    	try {
			byte[] data = s_.getBlob(us.id, objId, attr.id, index, langId, tid, true);
			return DataUtil.decompress(data);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(0, e.getMessage());
		}
    }

    public synchronized byte[] getBlob(KrnObject obj, String attrName,
                                       int index, long langId, long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getBlob(obj.id, attr, index, langId, tid);
    }

    public synchronized byte[][] getBlobs(long objId, KrnAttribute attr, long langId,
                                       long tid) throws KrnException {
        byte[][] res = s_.getBlobs(us.id, objId, attr.id,langId, tid, true);
        for (int i = 0; i < res.length; i++) {
        	try {
	        	res[i] = DataUtil.decompress(res[i]);
	    	} catch (IOException e) {
	    		log.error(e.getMessage(), e);
	    		throw new KrnException(0, e.getMessage());
	    	}
        }
        return res;
    }

    public synchronized byte[][] getBlobs(KrnObject obj, String attrName,
                                       int langId, int tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getBlobs(obj.id, attr, langId, tid);
    }
    
    public synchronized BlobValue[] getBlobValues(
            long[] objIds,
            long classId,
            String attrName,
            long langId,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        BlobValue[] res = s_.getBlobValues(us.id, objIds, attr.id, langId, tid);
        
        for (int i = 0; i < res.length; i++) {
        	try {
	        	res[i].value = DataUtil.decompress(res[i].value);
	    	} catch (IOException e) {
	    		log.error(e.getMessage(), e);
	    		throw new KrnException(0, e.getMessage());
	    	}
        }
        return res;

    }

    public synchronized StringValue[] getStringValues(
            long[] objIds,
            KrnAttribute attr,
            long langId,
            boolean isMemo,
            long tid
            ) throws KrnException {
        return s_.getStringValues(us.id, objIds, attr.id, langId, isMemo, tid);
    }

    public synchronized StringValue[] getStringValues(
            long[] objIds,
            long classId,
            String attrName,
            long langId,
            boolean isMemo,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getStringValues(objIds, attr, langId, isMemo, tid);
    }

    public SortedSet<Value> getValues(long[] objIds, long attrId, long langId, long tid) throws KrnException {
    	return s_.getValues(us.id, objIds, attrId, langId, tid);
    }

    /** Возвращает значения атрибута объекта типа Memo
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public synchronized String[] getMemos(KrnObject obj, KrnAttribute attr,
                                          int langId, int tid)
            throws KrnException {
        return s_.getStrings(us.id, (int)obj.id, (int)attr.id, langId, true, tid);
    }

    /** Возвращает значения атрибута объекта типа Memo
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public synchronized String[] getMemos(KrnObject obj, String attrName,
                                          int langId, int tid)
            throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getMemos(obj, attr, langId, tid);
    }

    /** Возвращает значения атрибута объекта типа Целое
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public synchronized long[] getLongs(KrnObject obj, KrnAttribute attr,
                                       long tid) throws KrnException {
        return s_.getLongs(us.id, obj.id, attr.id, tid);
    }

    /** Возвращает значения атриб
     * ут
     * а объекта типа Целое
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public synchronized long[] getLongs(KrnObject obj, String attrName, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getLongs(obj, attr, tid);
    }

    public synchronized LongValue[] getLongValues(long[] objIds,
                                                  KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getLongValues(us.id, objIds, (int)attr.id, tid);
    }

    public synchronized LongValue[] getLongValues(long[] objIds, long classId,
                                                  String attrName, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getLongValues(objIds, attr, tid);
    }

    /** Возвращает значения атрибута объекта типа Float
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public synchronized double[] getFloats(KrnObject obj, KrnAttribute attr,
                                           long tid)
            throws KrnException {
        return s_.getFloats(us.id, obj, attr, tid);
    }

    /** Возвращает значения атрибута объекта типа Float
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public synchronized double[] getFloats(KrnObject obj, String attrName,
                                           long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getFloats(obj, attr, tid);
    }

    public synchronized FloatValue[] getFloatValues(long[] objIds,
                                                    KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getFloatValues(us.id, objIds, attr.id, tid);
    }

    public synchronized FloatValue[] getFloatValues(long[] objIds, long classId,
                                                    String attrName, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getFloatValues(objIds, attr, tid);
    }

    public synchronized DateValue[] getDateValues(long[] objIds, KrnAttribute attr, long tid) throws KrnException {
        return s_.getDateValues(us.id, objIds, attr.id, tid);
    }

    public synchronized DateValue[] getDateValues2(long[] objIds, KrnAttribute attr, long tid) throws KrnException {
        return s_.getDateValues2(us.id, objIds, attr.id, tid);
    }
    
    public synchronized TimeValue[] getTimeValues(long[] objIds,
                                                  KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getTimeValues(us.id, objIds, attr.id, tid);
    }

    public synchronized void deleteValue(long objectId, long attrId,
                                         int[] indexes, long tid)
            throws KrnException {
    	s_.deleteValue(us.id, objectId, attrId, indexes, 0, tid);
    }

    public synchronized void deleteValue(long objectId, long classId,
                                         String attrName, int[] indexes,
                                         long tid) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        deleteValue(objectId, attr.id, indexes, tid);
    }

    public synchronized void deleteValue(long objectId, long classId,
			String attrName, Collection<Object> values, long tid)
			throws KrnException {
		ClassNode cnode = getClassNode(classId);
		KrnAttribute attr = cnode.getAttribute(attrName);
		deleteValue(objectId, attr.id, values, tid);
	}

    public synchronized void deleteValue(
    		long objectId, long attrId, Collection<Object> values, long tid)
    		throws KrnException {
    	s_.deleteValueInSet(us.id, objectId, attrId, values.toArray(new Object[values.size()]), tid);
    }

    public synchronized int getNextValue(long seqId, long tr_id) throws KrnException {
        return s_.getNextValue(us.id, seqId, tr_id);
    }

    public synchronized int getLastValue(long seqId) throws KrnException {
        return s_.getLastValue(us.id, seqId);
    }

    public synchronized void useValue(long seqId, long value, String strVal, long tr_id) throws KrnException {
        s_.useValue(us.id, seqId, value, strVal, tr_id);
    }

    public synchronized void skipValue(long seqId, long value, String strVal, long tr_id) throws KrnException {
        s_.skipValue(us.id, seqId, value, strVal, tr_id);
    }

    public synchronized void unuseValue(long seqId, String oldStrValue, long newValue,
                           String newStrValue, long tr_id) throws KrnException {
        s_.unuseValue(us.id, seqId, oldStrValue, newValue, newStrValue, tr_id);
    }

    public synchronized void rollbackSeqValues(long seqId, long tr_id) throws KrnException {
        s_.rollbackSeqValues(us.id, seqId, tr_id);
    }

    public synchronized long[] getSkippedValues(long seqId) throws KrnException {
        return s_.getSkippedValues(us.id, seqId);
    }

    public synchronized void init(String userName, String path, String newPath, String confPath, String serverHost, String serverPort, String baseName,
    		String typeClient, String ip, String host, int loginType, SessionOpsOperations ops) throws KrnException {
    	init(userName, path, newPath, confPath, serverHost, serverPort, baseName, typeClient, ip, host, loginType, ops, false, false, false, null);
    }
    
    public synchronized void init(String userName, String path, String newPath, String confPath, String serverHost, String serverPort, String baseName,
    		String typeClient, String ip, String host, int loginType, SessionOpsOperations ops, boolean force, boolean sLogin, boolean isUseECP, String signedData) throws KrnException {
        this.baseName = baseName;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        s_ = ops;

        switch (loginType) {
        case LOGIN_CERT:
            us = s_.loginWithCert(baseName, userName, typeClient, path, ip, host, true);
            break;
        case LOGIN_USUAL:
        	if (isUseECP)
        		us = s_.login(baseName, userName, typeClient, path, newPath, confPath, ip, host, true, force, sLogin, isUseECP, signedData);
        	else
        		us = s_.login(baseName, userName, typeClient, path, newPath, confPath, ip, host, true, force, sLogin);
            break;
        case LOGIN_DN:
            us = s_.loginWithDN(baseName, userName, typeClient, ip, host, true, force);
            break;
        case LOGIN_LDAP:
            us = s_.loginWithLDAP(baseName, userName, typeClient, ip, host, true);
            break;
        case LOGIN_KALKAN:
            us = s_.loginWithECP(baseName, userName, typeClient, ip, host, true);
            break;
        }
        
        callback = getCallback();

        try {
            classTrees_ = new ArrayList<ClassTreeModel>();
            cnodesByClassId_ = new TreeMap<Long, ClassNode>();
            cnodesByClassName_ = new TreeMap<String, ClassNode>();
            exprByMethodUid_ = new TreeMap<String, ASTStart>();
            
            hieararchyRoot_ = new ClassNode(getClassByName("Объект"));
            packageHieararchyRoot = new PackageNode();
            langsByCode = new HashMap<String, Long>();

            SC_UI = getClassByName("UI");
            SC_UI_FOLDER = getClassByName("UIFolder");

            SC_LANGUAGE = getClassByName("Language");
            LANGUAGES = getClassObjects(SC_LANGUAGE, 0);
            SC_MENUITEMSDESC = getClassByName("MenuItemsDesc");
            MENU_ITEMS_HELP = getClassObjects(SC_MENUITEMSDESC, 0);

            SC_GUICOMPONENT = getClassByName("GuiComponent");
            SC_FILTER = getClassByName("Filter");
            SC_FILTER_FOLDER = getClassByName("FilterFolder");
            SC_HIPERTREE = getClassByName("HiperTree");
            SC_USER = getClassByName("User");
            SC_USER_FOLDER = getClassByName("UserFolder");
            SC_TIMER = getClassByName("Timer");
            SC_FLOW = getClassByName("Flow");
            SC_PROCESS_DEF = getClassByName("ProcessDef");
            SC_PROCESS_DEF_FOLDER = getClassByName("ProcessDefFolder");
            SC_CONTROL_FOLDER = checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER) ? getClassByName(NAME_CLASS_CONTROL_FOLDER) : null;
            SC_CONTROL_FOLDER_ROOT = checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER_ROOT) ? getClassByName(NAME_CLASS_CONTROL_FOLDER_ROOT) : null;
            SC_REPORT_PRINTER = getClassByName("ReportPrinter");
            SC_REPORT_FOLDER = getClassByName("ReportFolder");
            SC_CONFIG_LOCAL = checkExistenceClassByName(NAME_CLASS_CONFIG_LOCAL) ? getClassByName(NAME_CLASS_CONFIG_LOCAL) : null;
            SC_BASE = getClassByName("Структура баз");
            SC_NOTE = getClassByName("Note");
            SC_IMPORT = getClassByName("Import");
            SC_EXPORT = getClassByName("Export");
            SC_BOX_EXCHANGE = getClassByName("BoxExchange");
            SC_BOX_FOLDER = getClassByName("BoxFolder");
            
            SC_PD_POLICY = getClassByName("Политика учетных записей");

        } catch (Exception ex) {
            // Игнорируем ошибку для возможности добавить недостающие классы при помощи конструктора
            ex.printStackTrace();
        }

        user_ = new User(us.userObj, this, us.typeClient);
        LangItem.initialize(this);
    }
    
    public synchronized void init(String userName, String path, String serverHost, String serverPort,
    		String typeClient, String ip, String host, int loginType, SessionOpsOperations ops) throws KrnException {
        this.baseName = null;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        s_ = ops;

        switch (loginType) {
        case LOGIN_USUAL:
            us = s_.login(null, userName, typeClient, path, null, null, ip, host, true, false, false, false, null);
            break;
        }
        
        callback = getCallback();
        
        user_ = new User(userName);
    }

    public Thread getCallback() {
    	if (callback == null)
            callback = new ClientCallback(this);
    	return callback;
    }

    public synchronized KrnObject getInterfaceLanguage() {
        return user_.ifcLang;
    }

    public synchronized KrnObject getDataLanguage() {
        return user_.dataLang;
    }

    public synchronized void changePassword(String dsName, String nameUs, String typeClient, String ip, String pcName, KrnObject object, char[] oldPwd,char[] newPwd,char[] confPwd)throws KrnException {
        s_.changePassword(dsName, nameUs, typeClient, ip, pcName, object, oldPwd,newPwd,confPwd);
    }
    
    public synchronized void verifyPassword(String dsName, String nameUs, KrnObject object, char[] newPwd, 
    		String name, boolean admin, boolean isLogged, String psw, Date lastChangeTime) throws KrnException {
        s_.verifyPassword(dsName, nameUs, object, newPwd, name, admin, isLogged, psw, kz.tamur.util.Funcs.convertTime(lastChangeTime));
    }

    public synchronized KrnObject getInterface() {
        return user_.ifc;
    }

    public synchronized void setInterfaceLanguage(KrnObject langObj)
            throws KrnException {
        if (user_.ifcLang == null || langObj.id != user_.ifcLang.id) {
            setObject(
                    (int)user_.object.id,
                    (int)user_.object.classId,
                    "interface language",
                    0,
                    (int)langObj.id,
                    0, false);
            user_.ifcLang = langObj;
        }
    }

    public synchronized void setDataLanguage(KrnObject lang)
            throws KrnException {
        s_.setDataLanguage(us.id, lang);
        if (user_.dataLang == null || lang.id != user_.dataLang.id) {
            setObject(user_.object.id, user_.object.classId,
                    "data language", 0, lang.id, 0, false);
            user_.dataLang = lang;
        }
    }

    public User getUser() {
        return user_;
    }

    public List<Filter> getFilters(String className) {
        return filters_.get(className);
    }

    public synchronized void addFilter(Filter filter) {
        filters_.put(filter.className, filter);
    }

    public synchronized long[] getFilteredObjectIds(
            long[] filterIds, FilterDate[] dates, int[] limit, long trId) throws KrnException {
        return s_.getFilteredObjectIds(us.id, filterIds, dates,limit, trId);
    }

    public synchronized long[] getFilteredObjectIds2(
            String[] filterUids, FilterDate[] dates, int[] limit) throws KrnException {
        return s_.getFilteredObjectIds2(us.id, filterUids, dates, limit);
    }

    public Kernel() {
        try{
            address=InetAddress.getLocalHost();
        }catch (UnknownHostException ex){
            ex.printStackTrace();
        }
    }

    protected void addClass(ClassNode cw) {
        cnodesByClassId_.put(new Long(cw.getKrnClass().id), cw);
        cnodesByClassName_.put(cw.getKrnClass().name, cw);
    }

    protected ClassNode removeClass(KrnClass cls) {
        cnodesByClassName_.remove(cls.name);
        return cnodesByClassId_.remove(new Long(cls.id));
    }

    protected ClassNode getClassNode(KrnClass cls) {
        return cnodesByClassId_.get(new Long(cls.id));
    }

    protected ClassNode getClassNodeById(long id) {
        return cnodesByClassId_.get(id);
    }

    protected void addAttribute(final KrnAttribute attr) {
    	attrById.put(attr.id, attr);
        ClassNode cnode = cnodesByClassId_.get(
                new Long(attr.classId)
        );

        cnode.addAttribute(attr);

        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);

            List<AttrNode> anodes = new LinkedList<AttrNode>();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                AttrNode an = (AttrNode)o;
                                if (an.attr_ != null) {
                                    return (attr.classId == an.attr_.typeClassId);
                                } else {
                                    return false;
                                }
                            }
                        }
                );
            } catch (KrnException e) {
                e.printStackTrace();
            }

            for (Iterator<AttrNode> it = anodes.iterator(); it.hasNext();) {
                AttrNode parent = it.next();
                model.insertNodeInto(new AttrNode(parent, attr, parent.canViewAttrs, parent.canViewMethods), parent,
                                     parent.getChildCount());
            }
        }
    }

    protected void removeAttribute(final KrnAttribute attr) {
    	attrById.remove(attr.id);
    	if(attr.rAttrId>0 && linkAttrs.containsKey(attr.rAttrId)){
    		linkAttrs.remove(attr.rAttrId);
    	}
        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);

            List<AttrNode> anodes = new LinkedList<AttrNode>();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                            	if (o instanceof MethodNode) {
                            		return false;
                            	}
                                return (attr.id == ((AttrNode) o).attr_.id);
                            }
                        }
                );
            } catch (KrnException e) {
                e.printStackTrace();
            }

            for (Iterator<AttrNode> it = anodes.iterator(); it.hasNext();)
                model.removeNodeFromParent(it.next());
        }

        ClassNode cnode = cnodesByClassId_.get(
                new Long(attr.classId)
        );
        cnode.removeAttribute(attr);
    }

    protected static Kernel inst_;
    protected SessionOpsOperations s_;
	protected UserSessionValue us;
	private String cert;

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void change(ClassNode node, KrnClass baseClass, String name, boolean isRepl) throws KrnException {
        KrnClass cls = changeClass(node.getKrnClass(), baseClass, name, isRepl);
        ClassNode parent = (ClassNode) node.getParent();
        if (parent.getKrnClass().id != baseClass.id) {
            removeClass(node.getKrnClass());
            List<KrnClass> subClss = Kernel.instance().getClasses(node.getKrnClass().id, true);
            for(KrnClass tempCls: subClss) {
            	removeClass(tempCls);
            }
            if (classHierarchy_ != null)
                classHierarchy_.removeNodeFromParent(node);
            ClassNode newnode = new ClassNode(cls);
            addClass(newnode);
            for(KrnClass tempCls: subClss) {
            	ClassNode tempNode = new ClassNode(tempCls);
            	addClass(tempNode);
            }
            if (classHierarchy_ != null)
                classHierarchy_.insertNodeInto(
                		newnode, getClassNode(baseClass.id), 0);
        } else {
            node.setKrnClass(cls);
            classHierarchy_.nodeChanged(node);
        }
    }

    public class MethodNode extends AttrNode {

        private KrnMethod method;

        public MethodNode(AttrNode parent, KrnMethod method, boolean canViewAttrs, boolean canViewMethods) {
            super(parent, null, canViewAttrs, canViewMethods);
            this.method = method;
        }

        public KrnMethod getMethod() {
            return method;
        }

        public void setMethod(KrnMethod method) {
			this.method = method;
		}

		public boolean isLeaf() {
            return true;
        }

        public int getChildCount() {
            return 0;
        }

        public TreeNode getChildAt(int childIndex) {
            return null;
        }

        public String toString() {
            return method.name + "()";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof KrnMethod) {
                return method.uid.equals(((KrnMethod)o).uid);
            } else {
                return false;
            }
        }

        public int compareTo(AttrNode n) {
            int res = 0;
            if (!(n instanceof MethodNode)) {
                return 1;
            } else {
                if (res == 0) {
                    res = method.name.compareTo(((MethodNode)n).method.name);
                }
                if (res == 0) {
                    return method.uid.compareTo(((MethodNode)n).method.uid);
                }
            }
            return res;
        }
    }
    
    public boolean hasSessionInitialized() {
    	return s_ != null;
    }

    public class CastNode extends AttrNode {

        private ClassNode cls;

        public CastNode(AttrNode parent, ClassNode cls, boolean canViewAttrs, boolean canViewMethods) {
            super(parent,
            		KrnUtil.createDummyAttribute(cls.getName(), cls.getId()),
            		canViewAttrs,
            		canViewMethods);
            this.cls = cls;
        }

        public ClassNode getType() throws KrnException {
            return cls;
        }

        public String toString() {
            return "<" + cls.getName() + ">";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof CastNode) {
                return cls.getKrnClass().id == ((CastNode)o).cls.getKrnClass().id;
            } else {
                return false;
            }
        }
    }

    /** Класс представляющий узел в дереве атрибутов */
    public class AttrNode implements MutableTreeNode, Comparable<AttrNode> {
        private AttrNode parent_;
        private KrnAttribute attr_;
        public List<AttrNode> children_;
        public boolean loaded_;
        private MultiMap<Long, AttrNode> attrNodesByAttrId_;
        public boolean canViewAttrs = true;
        public boolean canViewMethods = true;

        /** Конструирует узел
         *  @param parent родительский узел
         *  @param attr атрибут представляемый узлом
         */
        public AttrNode(AttrNode parent, KrnAttribute attr, boolean canViewAttrs, boolean canViewMethods) {
            parent_ = parent;
            attr_ = attr;
            loaded_ = false;
            attrNodesByAttrId_ = new MultiMap<Long, AttrNode>();
            this.canViewAttrs = canViewAttrs;
            this.canViewMethods = canViewMethods;
        }

        /** Возвращает атрибут представляемый узлом */
        public KrnAttribute getKrnAttribute() {
            return attr_;
        }

        /** Возвращает узел в виде строки */
        public String toString() {
        	String title = attr_.name;
        	try {
	        	ClassNode cw = getClassNode(attr_.typeClassId);
	            if (cw != null)
	                title = title + ":" + cw;
	            if (attr_.collectionType != COLLECTION_NONE)
	                title = title + "[]";
        	} catch (KrnException e) {
        		log.error(e, e);
        	}
            return title;
        }

        /** Сравнивает с узлом */
        public boolean equals(Object o) {
            AttrNode node = (AttrNode) o;
            if (node.attr_ != null) {
                if (attr_.id != node.attr_.id)
                    return false;

                if (parent_ == null && node.parent_ == null)
                    return true;

                if (parent_ != null && node.parent_ != null)
                    return (parent_.attr_.id == node.parent_.attr_.id);
            }

            return false;
        }

        public int compareTo(AttrNode n) {
            int res = 0;
            if (attr_.classId != n.attr_.classId) {
                try {
                    int level1 = (attr_.classId > 0)
                            ? getClassNode(attr_.classId).getLevel() : 0;
                    int level2 = (n.attr_.classId > 0)
                            ? getClassNode(n.attr_.classId).getLevel() : 0;
                    res = level1 - level2;
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            if (res == 0) {
                res = attr_.name.compareTo(n.attr_.name);
            }
            if (res == 0) {
                if (attr_.id < n.attr_.id)
                    res = -1;
                else if (attr_.id > n.attr_.id)
                    res = 1;
            }
            return res;
        }
        // Реализация интерфейса MutableTreeNode

        public Enumeration<AttrNode> children() {
            try {
                load();
            } catch (KrnException e) {
                e.printStackTrace();
            }
            return Collections.enumeration(children_);
        }

        public boolean getAllowsChildren() {
            return (!loaded_ || children_ != null);
        }

        public TreeNode getChildAt(int childIndex) {
            try {
                load();
            } catch (KrnException e) {
                e.printStackTrace();
            }
            return children_.get(childIndex);
        }

        public int getChildCount() {
            try {
                load();
            } catch (KrnException e) {
                e.printStackTrace();
            }
            return (children_ == null) ? 0 : children_.size();
        }

        public int getIndex(TreeNode node) {
            return children_.indexOf(node);
        }

        public TreeNode getParent() {
            return parent_;
        }

        public boolean isLeaf() {
        	if (attr_.typeClassId < 99)
        		return true;
            return loaded_ ? (children_ == null || children_.size() == 0) : false;
        }

        public void insert(MutableTreeNode child, int index) {
            try {
                load();
            } catch (KrnException e) {
                e.printStackTrace();
            }
            if (children_ == null)
                children_ = new ArrayList<AttrNode>();
            children_.add(index, (AttrNode)child);
        }

        public void remove(int index) {
            children_.remove(index);
        }

        public void remove(MutableTreeNode node) {
            children_.remove(node);
        }

        public void removeFromParent() {
            AttrNode parent = (AttrNode) getParent();
            parent.remove(this);
        }

        public void setParent(MutableTreeNode newParent) {
        }

        public void setUserObject(Object object) {
        }
        
        public ClassNode getType() throws KrnException {
            return getClassNode(attr_.typeClassId);
        }

        public void load() throws KrnException {
            if (!loaded_) {
                ClassNode cw = getType();
                if (cw != null) {
                    List<KrnAttribute> chs = cw.getAttributes();
                    if (canViewAttrs && chs != null && chs.size() > 0) {
                        children_ = new ArrayList<AttrNode>(chs.size());
                        for (int i = 0; i < chs.size(); ++i) {
                            KrnAttribute attr = (KrnAttribute) chs.get(i);
                            AttrNode anode = new AttrNode(this, attr, canViewAttrs, canViewMethods);
                            children_.add(anode);
                            attrNodesByAttrId_.put(new Long(attr.id), anode);
                        }
                        Collections.sort(children_);
                    }
                    List<KrnMethod> methods = cw.getMethods();
                    if (canViewMethods && methods != null) {
                        List<MethodNode> sortList = new ArrayList<MethodNode>();
                        for (KrnMethod m : methods) {
                            MethodNode mn = new MethodNode(this, m, canViewAttrs, canViewMethods);
                            sortList.add(mn);
                        }
                        Collections.sort(sortList);
                        if (sortList.size() > 0) {
                            if (children_ == null)
                                children_ = new ArrayList<AttrNode>(sortList.size());

                            children_.addAll(sortList);
                        }
                    }

                    if (this.getParent() != null && cw.getId() > 99) {
                    	List<ClassNode> subClasses = new ArrayList<ClassNode>();
                    	cw.getSubClasses(subClasses);
                    	for (ClassNode subClass : subClasses) {
                    		CastNode cn = new CastNode(this, subClass, canViewAttrs, canViewMethods);
                    		children_.add(cn);
                    	}
                    }
                }
                loaded_ = true;
            }
        }

        public void findNodes(
                boolean forceLoad,
                Collection<AttrNode> nodes,
                UnaryFunction comparator
                ) throws KrnException {
            if (forceLoad)
                load();

            if (loaded_ && children_ != null) {
                for (int i = 0; i < children_.size(); ++i) {
                    AttrNode child = children_.get(i);
                    child.findNodes(forceLoad, nodes, comparator);
                }
            }

            if (comparator.check(this))
                nodes.add(this);
        }
    }

    protected class ClassTreeModel extends DefaultTreeModel {
        private int id_;

        public ClassTreeModel(TreeNode root, int id) {
            super(root);
            id_ = id;
        }

        public boolean equals(Object o) {
            return (id_ == ((ClassTreeModel) o).id_);
        }
    }

    public synchronized void runReplication()
            throws KrnException {
        s_.runReplication(us.id);
    }

    public void setCurentUser()
            throws KrnException {
        KrnClass cls_ = getClassByName("Role");
        KrnObject[] objs_ = getClassObjects(cls_, 0);
        long[] ids_ = Funcs.makeObjectIdArray(objs_);
        ObjectValue[] obj_a = getObjectValues(ids_, (int)cls_.id, "user", 0);
        Map<Long, Long> userMap_ = new HashMap<Long, Long>();
        ids_ = new long[obj_a.length];
        for (int i = 0; i < ids_.length; ++i) {
            ids_[i] = (int)obj_a[i].value.id;
            userMap_.put(new Long(obj_a[i].objectId),
                         new Long(obj_a[i].value.id));
        }
        KrnClass cls_tabl = getClassByName("Зап табл роль исп док");
        objs_ = getClassObjects(cls_tabl, -1);
        ids_ = Funcs.makeObjectIdArray(objs_);
        obj_a = getObjectValues(ids_, (int)cls_tabl.id, "роль", -1);
        for (int i = 0; i < obj_a.length; ++i) {
            long value_ = userMap_.get(new Long(obj_a[i].value.id));
            setObject((int)obj_a[i].objectId, (int)cls_tabl.id, "user", 0, value_,
                      (int)obj_a[i].tid, false);
        }
    }

    public KrnObject getCurrentDb() {
    	if (currDb == null && user_.object != null) {
    		try {
    			currDb = s_.getCurrentDb(us.id);
    		} catch (KrnException e) {
    			log.error(e, e);
    		}
    	}
        return currDb;
    }

    public synchronized String getUId(long id)
            throws KrnException {
        KrnObject[] objs = s_.getObjectsById(us.id, new long[] {id},-1);
        return (objs.length > 0) ? objs[0].uid : null;
    }

    public synchronized KrnObject[] getReplRecords(int log_type, long replicationID)
            throws KrnException {
        return s_.getReplRecords(us.id, log_type, replicationID);
    }

    public synchronized void createConfirmationFile(long DbId)
            throws KrnException {
        s_.createConfirmationFile(us.id, DbId);
    }

    public synchronized int getChanges(int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws KrnException {
        return s_.getChanges(us.id, action, info, scriptOnBeforeAction, scriptOnAfterAction);
    }

    public synchronized void  setChanges(final ImportResultListener importResultListener) {
    	new Thread() {
    		public void run() {
    			String result;
    			int event = 0;
    			try {
	    			result = s_.setChanges(us.id);
	    			if (result == null || result.equals("")) {
	    				result = "Импорт данных успешно завершен.";
	    			} else if (result.indexOf("Внимание!") == -1) {
	    				result = "Ошибка при импорте данных!\n" + result;
	    				event = 1;
	    			}
    			} catch (KrnException e) {
    				e.printStackTrace();
    				result = "Ошибка при импорте данных!";
    				event = 1;
    			}
    			if (importResultListener != null) {
    				importResultListener.importResult(event, result);
    			}
    		}
    	}.start();
    }

    public KrnAttribute getAttributeByName(long clsId, String name) throws KrnException {
        ClassNode cn = getClassNode(clsId);
        return cn.getAttribute(name);
    }

    public KrnAttribute getAttributeByName(KrnClass cls, String name) throws KrnException {
        ClassNode cn = getClassNode(cls.id);
        return cn.getAttribute(name);
    }
    
    public KrnAttribute getAttributeByNameTracing(KrnClass cls, String name) {
        try {
            return getAttributeByName(cls, name);
        } catch (KrnException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized KrnAttribute getAttributeById(long id)
            throws KrnException {
    	KrnAttribute attr = attrById.get(id);
    	if (attr == null) {
    		attr = s_.getAttributeById(us.id, id);
    		attrById.put(id, attr);
    	}
    	return attr;
    }
    
    public synchronized void putAttrById(long id, KrnAttribute attr) {
		attrById.put(id, attr);
    }

	public synchronized String getStringsSingular(long objId, long attrId, long langId, boolean isMemo, boolean isNotNull) throws KrnException {
		String[] vals = s_.getStrings(us.id, objId, attrId, langId, isMemo, 0);
		if (vals.length == 0) {
			if (isNotNull) {
				throw new KrnException(0, "У объекта " + objId + " не определено значение свойства id=" + attrId);
			} else
				return "";
		}
		return vals[0];
	}

    public synchronized long getLongsSingular(KrnObject obj, KrnAttribute attr, boolean isNotNull) throws KrnException {
        long[] vals = s_.getLongs(us.id, obj.id, attr.id, 0);
        if (vals.length == 0) {
            if (isNotNull) {
                throw new KrnException(0, "У объекта [id=" + obj.id + "] не определено значение свойства '" + attr.name + "' [id=" + attr.id + "]");
            } else
                return 0;
        }
        return vals[0];
    }
    
	public synchronized long getLongsSingular(KrnObject obj, String attrName, boolean isNotNull) throws KrnException {
		ClassNode cnode = getClassNode(obj.classId);
		KrnAttribute attr = cnode.getAttribute(attrName);
		long[] vals = s_.getLongs(us.id, obj.id, attr.id, 0);
		if (vals.length == 0) {
			if (isNotNull) {
				throw new KrnException(0, "У объекта [id=" + obj.id + "] не определено значение свойства '" + attr.name + "' [id=" + attr.id + "]");
			} else
				return 0;
		}
		return vals[0];
	}

	public synchronized KrnObject getObjectsSingular(long objId, long attrId, boolean isNotNull) throws KrnException {
		KrnObject[] vals = s_.getObjects(us.id, objId, attrId, new long[0], 0);
		if (vals.length == 0) {
			if (isNotNull) {
				throw new KrnException(0, "У объекта " + objId + " не определено значение свойства id=" + attrId);
			} else
				return null;
		}
		return vals[0];
	}

    public synchronized KrnObject[] getChildDbs(boolean recursive, boolean onlyPhisycal)
            throws KrnException {
        return s_.getChildDbs(us.id, recursive, onlyPhisycal);
    }

    public void setFilterParam(String fuid, String pid, List<Object> values)
            throws KrnException {
    	Object[] vs = values != null ? new Object[values.size()] : new Object[0];
    	for (int i=0; i<vs.length; i++) {
    		Object obj = values.get(i);
    		if (obj instanceof Date)
    			obj = kz.tamur.util.Funcs.convertTime((Date)obj);
    		
    		vs[i] = obj;
    	}
        boolean changed = s_.setFilterParam(us.id, fuid, pid, vs);
        if (changed) {
	        EventListenerList ll = filterParamListeners_.get(fuid, pid);
	        if(ll != null) {
	            Object[] listeners = ll.getListenerList();
	            for (int i = listeners.length - 2; i >= 0; i -= 2) {
	                if (listeners[i] == FilterParamListener.class) {
	                    FilterParamListener l = (FilterParamListener) listeners[i + 1];
	                    l.filterParamChanged(fuid, pid, values);
	                }
	            }
	        }
	        ll = filterParamListeners_.get(fuid, "");
	        if(ll != null) {
	            Object[] listeners = ll.getListenerList();
	            for (int i = listeners.length - 2; i >= 0; i -= 2) {
	                if (listeners[i] == FilterParamListener.class) {
	                    FilterParamListener l = (FilterParamListener) listeners[i + 1];
	                    l.filterParamChanged(fuid, pid, values);
	                }
	            }
	        }
        }
    }

    public List<Object> getFilterParam(String fuid, String pid) throws KrnException {
        Object[] vs = s_.getFilterParam(us.id, fuid, pid);
        List<Object> res = new ArrayList<Object>(vs.length);
        for (int i = 0; i < vs.length; i++) {
            if (vs[i] instanceof Time) vs[i] = kz.tamur.util.Funcs.convertTime((Time)vs[i]);
            res.add(vs[i]);
        }
        return res;
    }
    
    public  Map<String,List<Object>> getFilterParams(String fuid) throws KrnException {
        Map<String, List<Object>> res = s_.getFilterParams(us.id,fuid);
        for(List<Object> rs : res.values()){
        	for (int i = 0; i < rs.size(); i++) {
        		Object r = rs.get(i);
        		if (r instanceof Time) r = kz.tamur.util.Funcs.convertTime((Time)r);
        		rs.set(i,r);
        	}
        }
        return res;
    }
    public void clearFilterParams(String fuid) throws KrnException {
    	boolean changed = s_.clearFilterParams(us.id, fuid);
    	if (changed) {
	        Map<String, EventListenerList> m = filterParamListeners_.get(fuid);
	        if (m != null) {
		        for (Iterator<String> paramIt = m.keySet().iterator(); paramIt.hasNext();) {
		        	String pid = paramIt.next();
		            EventListenerList ls = m.get(pid);
		            Object[] listeners = ls.getListenerList();
		            for (int i = listeners.length - 2; i >= 0; i -= 2) {
		                if (listeners[i] == FilterParamListener.class) {
		                    FilterParamListener l =
		                            (FilterParamListener) listeners[i + 1];
		                    l.filterParamChanged(fuid, pid, null);
		                }
		            }
		        }
	        }
    	}
    }

    public void addFilterParamListener(String fuid, String param,
            FilterParamListener l) {
        EventListenerList ls = filterParamListeners_.get(fuid, param);
        if (ls == null) {
            ls = new EventListenerList();
            filterParamListeners_.put(fuid, param, ls);
        }
        ls.add(FilterParamListener.class, l);
    }

    public void removeFilterParamListener(FilterParamListener l) {
    }

    public void clearFilterParams() {
        for (Iterator<String> fuidIt = filterParamListeners_.keySet().iterator(); fuidIt.hasNext();) {
            String fuid = fuidIt.next();
            Map<String, EventListenerList> m = filterParamListeners_.get(fuid);
            for (Iterator<String> paramIt = m.keySet().iterator(); paramIt.hasNext();) {
                EventListenerList ls = filterParamListeners_.get(fuid, paramIt.next());
                Object[] listeners = ls.getListenerList();
                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == FilterParamListener.class) {
                        FilterParamListener l =
                                (FilterParamListener) listeners[i + 1];
                        l.clearParam();
                    }
                }
            }
        }
    }

    public synchronized KrnObject[] cloneObject2(KrnObject[] obj, long get_tr_id, long set_tr_id) throws KrnException {
    	return s_.cloneObject2(us.id, obj, get_tr_id, set_tr_id);
    }
    
    public KrnObject[] getObjectsByUid(String[] uids, long trId) throws KrnException {
        return s_.getObjectsByUid(us.id, uids, trId);
    }

    public KrnObject getClassObjectByUid(long clsId,  String uid, long trId) throws KrnException {
        return s_.getClassObjectByUid(us.id, clsId, uid, trId);
    }
    
    public KrnObject getObjectByUid(String uid, long trId) throws KrnException {
    	KrnObject[] objs = getObjectsByUid(new String[] {uid}, trId);
    	return objs.length > 0 ? objs[0] : null;
    }
    
    public KrnObject[] getObjectsByIds(long[] ids,long trId) throws KrnException {
        return s_.getObjectsById(us.id, ids,trId);
    }

    public KrnObject getObjectById(long id, long trId) throws KrnException {
    	KrnObject[] objs = s_.getObjectsById(us.id, new long[] {id}, trId);
    	return objs.length > 0 ? objs[0] : null;
    }
    
    public Map<Long, String> getObjectUids(long[] ids)
            throws KrnException {
        KrnObject[] objs = s_.getObjectsById(us.id, ids,-1);
        Map<Long, String> res = new HashMap<Long, String>();
        for (int i = 0; i < objs.length; i++) {
            KrnObject obj = objs[i];
            res.put(new Long(obj.id), obj.uid);
        }
        return res;
    }

    public void addPropertyListener(String propertyName,
                                    PropertyChangeListener l) {
        propSupport.addPropertyChangeListener(propertyName, l);
    }

    // Обратные атрибуты
    public KrnAttribute[] getRevAttributes(long attrId) throws KrnException {
        Long id = new Long(attrId);
        KrnAttribute[] res = revAttrs.get(id);
        if (res == null) {
            long[] revAttrIds = s_.getRevAttributes(us.id, attrId);
            res = new KrnAttribute[revAttrIds.length];
            for (int i = 0; i < revAttrIds.length; ++i) {
                res[i] = getAttributeById(revAttrIds[i]);
            }
            revAttrs.put(id, res);
        }
        return res;
    }
    
    public List<KrnAttribute> getRevAttributes2(long attrId) throws KrnException {
    	return s_.getRevAttributes2(us.id, attrId);
    }


    public KrnAttribute[] getLinkAttributes(long attrId) throws KrnException {
        Long id = new Long(attrId);
        KrnAttribute[] res = linkAttrs.get(id);
        if (res == null) {
            long[] revAttrIds = s_.getLinkAttributes(us.id, attrId);
            res = new KrnAttribute[revAttrIds.length];
            for (int i = 0; i < revAttrIds.length; ++i) {
                res[i] = getAttributeById(revAttrIds[i]);
            }
            linkAttrs.put(id, res);
        }
        return res;
    }

    public String getXml(KrnObject obj) throws KrnException {
        return s_.getXml(us.id, obj);
    }

    public KrnObject[] getFilteredObjects(KrnObject filterObj, int limit, long trId)
            throws KrnException {
        return s_.getFilteredObjects(us.id, filterObj, limit, trId);
    }

    public KrnObject[] getFilteredObjects(KrnObject filterObj, int limit,int beginRow,int endRow, long trId)
            throws KrnException {
        return s_.getFilteredObjects(us.id, filterObj, limit,beginRow,endRow, trId);
    }
    public long filterCount(KrnObject filterObj, long trId)
	    	throws KrnException {
    	return s_.filterCount(us.id, filterObj, trId);
	}

    public List<Object> filterGroup(KrnObject filterObj, long trId)
	    	throws KrnException {
    	return s_.filterGroup(us.id, filterObj, trId);
	}
    public long filterToAttr(KrnObject filterObj, long pobjId, long attrId, long trId)
	    	throws KrnException {
    	return s_.filterToAttr(us.id, filterObj, pobjId, attrId, trId);
	}
    public boolean isSubclassOf(long classId, long parentClassId) throws KrnException {
    	ClassNode cnode = (ClassNode)getClassNode(classId);
        while (cnode != null && cnode.getKrnClass().id != parentClassId) {
        	cnode = (ClassNode)getClassNode(cnode.getKrnClass().id).getParent();
        }
        return (cnode != null);
    }

    public boolean isObjectLock(long objId, long processId) throws KrnException {
        return s_.isCachedObjectLock(us.id, objId, processId);
    }

    public void lock(long objId, long processId, long flowId) {
        s_.cachedLock(us.id, objId, processId, flowId);
    }

    public void unlock(long objId, long processId, long flowId) {
        s_.cachedUnlock(us.id, objId, processId, flowId);
    }

    public KrnObject[] getConflictLocker(long objId, long flowId,long pdId) {
        return s_.getCachedConflictLocker(us.id, objId, flowId, pdId);
    }

    public  KrnObject getLocker(long objId, long processId) {
        KrnObject res = s_.getCachedLocker(us.id, objId, processId);
        return (res.id == 0) ? null : res;
    }

    public  Collection<Lock> getLockers(long objId) {
    	Collection<Lock> res = s_.getLocksByObjectId(us.id, objId);
        return res;
    }
    public String getLocker(long objId)  throws KrnException {
        String res = s_.isCachedObjectLock(us.id, objId);
        return res;
    }
    public void rollbackLocked() throws KrnException {
        s_.rollbackLocked(us.id);
    }

    protected void addMethod(final KrnMethod m) {
        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);
            List<AttrNode> anodes = new LinkedList<AttrNode>();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                AttrNode an = (AttrNode)o;
                                if (an.attr_ != null) {
                                    return (m.classId == an.attr_.typeClassId);
                                } else {
                                    return false;
                                }
                            }
                        }
                );
            } catch (KrnException e) {
                e.printStackTrace();
            }

            for (Iterator<AttrNode> it = anodes.iterator(); it.hasNext();) {
                AttrNode parent = (AttrNode) it.next();
                model.insertNodeInto(new MethodNode(parent, m, parent.canViewAttrs, parent.canViewMethods), parent,
                                     parent.getChildCount());
            }
        }
    }

    protected void methodChange(final KrnMethod m) {
        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);
            List<AttrNode> anodes = new LinkedList<AttrNode>();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                if (o instanceof MethodNode) {
                                    return (m.uid.equals(((MethodNode)o).method.uid));
                                } else {
                                    return false;
                                }
                            }
                        }
                );
            } catch (KrnException e) {
                e.printStackTrace();
            }

            for (Iterator<AttrNode> it = anodes.iterator(); it.hasNext();) {
                MethodNode node = (MethodNode) it.next();
                node.method = m;
                model.nodeChanged(node);
            }
        }

    }

    protected void delMethod(final KrnMethod m) {
        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);
            List<AttrNode> mnodes = new LinkedList<AttrNode>();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        mnodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                AttrNode an = (AttrNode)o;
                                if (an instanceof MethodNode) {
                                    return m.uid.equals(((MethodNode)an).getMethod().uid);
                                } else {
                                    return false;
                                }
                            }
                        }
                );
            } catch (KrnException e) {
                e.printStackTrace();
            }
            for (Iterator<AttrNode> it = mnodes.iterator(); it.hasNext();) {
                MethodNode mnode = (MethodNode) it.next();
                //mnode.removeFromParent();
                model.removeNodeFromParent(mnode);
                //model.nodeChanged(mnode.getParent());
            }
        }
    }

    public synchronized ASTStart getMethodTemplate(KrnMethod m) throws Throwable {
    	ASTStart expr = exprByMethodUid_.get(m.uid);
    	if (expr == null) {
			byte[] bs = s_.getMethodExpression(us.id, m.uid);
            try {
            	expr = OrLang.createStaticTemplate(new InputStreamReader(
                		new ByteArrayInputStream(bs), "UTF-8"));
			} catch (Throwable e) {
				log.error("Ошибка в коде метода '" + getClassNode(m.classId).getName() + "." + m.name + "'", e);
				throw e;
			}
            exprByMethodUid_.put(m.uid, expr);
    	}
		return expr;
    }
    
    public synchronized void removeMethodFromCache(String uid) {
    	exprByMethodUid_.remove(uid);
    }

    public synchronized void dbExport(String dir, String separator) {
        s_.dbExport(us.id, dir, separator);
    }

    public long[] getLangs(long objId, long attrId, long tid) throws KrnException {
        return s_.getLangs(us.id, objId, attrId, tid);
    }
    
    public boolean isDel(KrnObject obj, long trId) throws KrnException{
    	return s_.isDel(us.id, obj, trId);
    }

    public boolean isDeleted(KrnObject obj) throws KrnException {
        return s_.isDeleted(us.id, obj);
    }
    
    public long execute(String cmd, Map<String, Object> vars, boolean closeSession) throws KrnException {
    	return s_.execute(us.id, cmd, vars, closeSession);
    }

    public Object executeMethod(KrnObject obj, KrnObject this_, String name, List<Object> args, long trId) throws KrnException {
    	return s_.executeMethod(us.id, obj, this_, name, args, trId);
    }

    public Object executeMethod(KrnClass cls, KrnClass this_, String name, List<Object> args, long trId) throws KrnException {
    	return s_.executeMethod(us.id, cls, this_, name, args, trId);
    }

    public long getLangIdByCode(String code){
        if(langsByCode.size()==0){
            try {
                long[] ids=Funcs.makeObjectIdArray(LANGUAGES);
                StringValue[] svs=getStringValues(ids,SC_LANGUAGE.id,"code",0,false,0);
                for (StringValue sv : svs) {
                    if (sv.index == 0)
                        langsByCode.put(sv.value, sv.objectId);
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return langsByCode.get(code);
    }
    
    public synchronized UserSessionValue[] getUserSessions() throws KrnException {
    	return s_.getUserSessions(us.id);
    }
    
    public synchronized UserSessionValue[] getUserSessions(int criteria, String txt, String txt2) throws KrnException {
    	return s_.getUserSessions(us.id, criteria, txt, txt2);
    }
    
    public synchronized void killUserSession(UUID usId, boolean blockUser) throws KrnException {
    	s_.killUserSessions(us.id, usId, blockUser);
    }

    public synchronized void refreshMethodsForReplication() throws KrnException {
        s_.refreshMethodsForReplication(us.id);
    }
    
    
    public synchronized String getClassComment(long clsId) throws KrnException {
    	return s_.getClassComment(us.id, clsId);
    }

    public synchronized void setClassComment(String clsUid, String comment) throws KrnException {
    	s_.setClassComment(us.id, clsUid, comment);
    }

    public synchronized String getAttributeComment(long attrId) throws KrnException {
    	return s_.getAttributeComment(us.id, attrId);
    }

    public synchronized void setAttributeComment(String attrUid, String comment) throws KrnException {
    	s_.setAttributeComment(us.id, attrUid, comment);
    }

    public synchronized String getMethodComment(String methodId) throws KrnException {
    	return s_.getMethodComment(us.id, methodId);
    }
    
    // Метод, возвращающий список всех аттрибутов
  	public List<KrnMethod> getAllMethods() throws KrnException{
  		return s_.getAllMethods(us.id);
    }
  	
	// Метод, возвращающий HashMap всех аттрибутов
   	public Map<String, KrnMethod> getMethodsMap() throws KrnException{
   		return s_.getMethodsMap(us.id);
     }
    
    public synchronized KrnMethod[] getMethodsByName(String name, long searchMethod) throws KrnException {
        return s_.getMethodsByName(us.id, name, searchMethod);
    }
    
    public synchronized KrnMethod[] getMethodsByUid(String name, long searchMethod) throws KrnException {
        return s_.getMethodsByUid(us.id, name, searchMethod);
    }
    
    public synchronized KrnMethod getMethodById(String id) throws KrnException {
    	return s_.getMethodById(us.id, id);
    }
    
    public synchronized void setMethodComment(String methodId, String comment) throws KrnException {
    	s_.setMethodComment(us.id, methodId, comment);
    }

    public synchronized void releaseEngagedObject(long objId) throws KrnException {
    	s_.releaseEngagedObject(us.id, objId);
    }

    public synchronized void unlockMethod(String muid) throws KrnException {
    	s_.unlockMethod(us.id, muid);
    }
    public synchronized UserSessionValue getObjectBlocker(long objId) throws KrnException {
    	return s_.getObjectBlocker(us.id, objId);
    }

    public synchronized UserSessionValue blockObject(long objId) throws KrnException {
    	return s_.blockObject(us.id, objId);
    }

    public synchronized UserSessionValue blockMethod(String muid) throws KrnException {
    	return s_.lockMethod(us.id, muid);
    }
    public synchronized UserSessionValue vcsLockObject(long objId) throws KrnException {
    	return s_.vcsLockObject(us.id, objId);
    }

    public synchronized UserSessionValue vcsLockModel(String uid, int modelChangeType) throws KrnException {
    	return s_.vcsLockModel(us.id, uid, modelChangeType);
    }

    public synchronized void writeLogRecord(SystemEvent event, String description) throws KrnException{
        s_.writeLogRecord(us.id, event, description);
    }

    public synchronized void writeLogRecord(String loggerName, String type, String event, String description) throws KrnException{
        s_.writeLogRecord(us.id, loggerName, type, event, description);
    }

    public synchronized void writeLogRecord(String type, String event, String description) throws KrnException{
        s_.writeLogRecord(us.id, null, type, event, description);
    }

    public synchronized int blockServer(boolean serverBlocked) {
        return s_.blockServer(us.id, serverBlocked);
    }

    public synchronized void sendMessage(UUID toUsId, String message) throws KrnException {
    	s_.sendMessage(us.id, toUsId, message);
    }

    public synchronized void sendMessage(String message) throws KrnException {
    	s_.sendMessage(us.id, message);
    }

    public synchronized void interfaceChanged(long id) throws KrnException {
    	s_.interfaceChanged(us.id, id);
    }

    public synchronized int isServerBlocked() throws KrnException {
        return s_.isServerBlocked(us.id);
    }
    public List<String> showDbLocks() throws KrnException {
        return s_.showDbLocks(us.id);
    }
    
	public synchronized long getNextGeneratedNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		return s_.getNextGeneratedNumber(us.id, docTypeUid, period, initNumber);
	}
	
	public synchronized long setLastGeneratedNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		return s_.setLastGeneratedNumber(us.id, docTypeUid, period, initNumber);
	}

	public synchronized boolean rejectGeneratedNumber(String docTypeUid, Number period, Number number, Date date) throws Exception {
		return s_.rejectGeneratedNumber(us.id, docTypeUid, period, number, kz.tamur.util.Funcs.convertTime(date));
	}

	public synchronized long getOldGeneratedNumber(String docTypeUid, Number period) throws Exception {
		return s_.getOldGeneratedNumber(us.id, docTypeUid, period);
	}
	
	public synchronized KrnObject saveNumber(String className, String attrName, String kadastrNumber) throws Exception {
		return s_.saveNumber(us.id, className, attrName, kadastrNumber);
	}

	public synchronized KrnMethod createMethod(
			KrnClass cls,
			String name,
			boolean isClassMethod,
			byte[] expr
	) throws KrnException {
		return s_.createMethod(us.id, cls, name, isClassMethod, expr);
	}
	
	public synchronized KrnMethod changeMethod(
			String uid,
			String name,
			boolean isClassMethod,
			byte[] expr
	) throws KrnException {
		return s_.changeMethod(us.id, uid, name, isClassMethod, expr);
	}
	
	public synchronized KrnMethod rollbackMethod(String uid) throws KrnException {
		return s_.rollbackMethod(us.id, uid);
	}

	public synchronized void deleteMethod(
			String uid
	) throws KrnException {
		s_.deleteMethod(us.id, uid);
	}

    public synchronized String getMethodExpression(
    		String uid
    ) throws KrnException {
		byte[] bs = s_.getMethodExpression(us.id, uid);
        try {
        	return bs.length > 0 ? new String(bs, "UTF-8") : "";
		} catch (Throwable e) {
			log.error(e, e);
			throw new KrnException(0, e.getMessage());
		}
    }

    public synchronized KrnMethod[] getMethods(
    		long clsId
    ) throws KrnException {
    	return s_.getMethods(us.id, clsId);
    }

    public List<Object[]> getObjects(long[] objIds, AttrRequest req, long tid) throws KrnException {
    	return s_.getObjects(us.id, objIds, req, tid).rows;
    }
    
    protected void cacheObject(KrnObject obj) {
    	synchronized (objById) {
    		objById.put(obj.id, obj);
    	}
    	synchronized (objByUid) {
    		objByUid.put(obj.uid, obj);
    	}
    }
    
    public KrnObject getCachedObjectByUid(String uid) throws KrnException {
    	synchronized (objByUid) {
    		if (objByUid.containsKey(uid))
    			return objByUid.get(uid);
    		KrnObject obj = getObjectByUid(uid, -1);
    		if (obj == null) {
    			objByUid.put(uid, null);
    		} else {
    			cacheObject(obj);
    			return obj;
    		}
		}
    	return null;
    }
    
    public KrnObject getCachedObjectById(Long id) throws KrnException {
    	synchronized (objById) {
    		if (objById.containsKey(id))
    			return objById.get(id);
    		KrnObject obj = getObjectById(id, -1);
    		if (obj == null) {
    			objById.put(id, null);
    		} else {
    			cacheObject(obj);
    			return obj;
    		}
		}
    	return null;
    }
    
    public void addToCache(Set<String> uids) throws KrnException {
    	synchronized (objByUid) {
	    	uids.removeAll(objByUid.keySet());
	    	if (uids.size() > 0) {
	    		KrnObject[] objs = getObjectsByUid(uids.toArray(new String[uids.size()]), 0);
	    		for (KrnObject obj : objs)
	    			cacheObject(obj);
	    	}
    	}
    }

    public int truncateClass(KrnClass cls) throws KrnException {
    	return s_.truncateClass(us.id, cls);
    }
    
    // Контекстный поиск
    public List<Object> search(String pattern, int results, int[] searchProperties, boolean[] searchArea) throws KrnException {
    	return s_.search(us.id, pattern, results, searchProperties, searchArea);
    }
    
    // Индексирование всех базы
    public void indexDatabase(boolean fullIndex) throws KrnException {
    	s_.indexHierarchy(us.id, "Объект", fullIndex);
    }
    
    // Индексирование классов
    public synchronized void indexClass(KrnClass kClass, boolean fullIndex) throws KrnException {
    	s_.indexClass(us.id, kClass, fullIndex);
    }
    
    // Обнуление папки с индексами атрибута или методов
    public synchronized void dropIndex(KrnAttribute krnAttribute) throws KrnException {
    	s_.dropIndex(us.id, krnAttribute);
    }
    
    // Обнуление всех папок с индексами
    public synchronized boolean dropIndexFolder() throws KrnException {
    	return s_.dropIndexFolder(us.id);
    }

    // Обнуление папки с индексами триггеров
    public synchronized void dropTriggersIndexFolder() throws KrnException {
    	s_.dropTriggersIndexFolder(us.id);
    }

    // Обнуление папки с индексами изменений
    public synchronized void dropVcsChangesIndexFolder() throws KrnException {
    	s_.dropVcsChangesIndexFolder(us.id);
    }
    
    // Индексирование объектов
    public synchronized void indexObject(KrnObject krnObject, KrnClass krnClass, KrnAttribute krnAttribute, boolean foolIndex) throws KrnException {
    	s_.indexObject(us.id, krnObject, krnClass, krnAttribute, foolIndex);
    }
    
    // Индексирование методов
    public synchronized void indexMethod(KrnMethod method) throws KrnException {
    	s_.indexMethod(us.id, method);
    }
    
    // Индексирование триггеров
    public synchronized void indexTrigger(OrlangTriggerInfo trigger) throws KrnException {
    	s_.indexTrigger(us.id, trigger);
    }
    
    // Индексирование изменений объектов проектирования
    public synchronized void indexVcsChange(KrnVcsChange change) throws KrnException {
    	s_.indexVcsChange(us.id, change);
    }
    
    public String getIndexDirectory() throws KrnException {
    	return s_.getIndexDirectory(us.id);
    }
    
    public String getLastIndexingInfo() throws KrnException {
    	return s_.getLastIndexingInfo(us.id);
    }
    
    public void setLastIndexingInfo(String lastIndexingInfo) throws KrnException {
    	s_.setLastIndexingInfo(us.id, lastIndexingInfo);
    }
    
    //Вернуть корень иерархии классов
    public ClassNode getHierarchyRoot(){
    	return hieararchyRoot_;
    }
    
    //Выделение ключевого слово в поиске
    public String getHighlightedFragments(String objUid, long attrId, long langId, String pattern) throws KrnException {
    	return s_.getHighlightedFragments(us.id, objUid, attrId, langId, pattern);
    }
    
    public boolean stringSearch(String content, String pattern, long langId) {
    	return s_.stringSearch(us.id, content, pattern, langId);
    }
    
    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
    
    public ModelChanges getModelChanges(long changeId) throws KrnException {
    	return s_.getModelChanges(us.id, changeId);
    }
    
    public DataChanges getDataChanges(long classId, long changeId, AttrRequest req) throws KrnException {
    	return s_.getDataChanges(us.id, classId, changeId, req);
    }
    
	public Element prepareReport(long reportId, KrnObject lang, KrnObject[] srvObjs, FilterDate[] fds, long trId)
            throws KrnException {
        return s_.prepareReport(us.id, reportId, lang, srvObjs, fds, trId).xml;
    }
    
	public byte[] convertOfficeDocument(byte[] docData, String outputFormat) throws KrnException {
		try {
			docData = DataUtil.compress(docData, 9);
			docData = s_.convertOfficeDocument(us.id, docData, outputFormat);
			return DataUtil.decompress(docData);
		} catch (Exception e) {
			log.error(e, e);
			throw new KrnException(0, e.getMessage());
		}
	}
	
    public String getBaseName() {
    	return baseName;
    }
   
    public UUID getUUID() {
    	return us.id;
    }

    public boolean isSE_UI() {
        return Constants.SE_UI;
    }

    public void setSE_UI(boolean se_ui) {
        seUI = se_ui;
    }

    public boolean isADVANCED_UI() {
        return advancedUI;
    }

    public void setADVANCED_UI(boolean advanced_ui) {
        advancedUI = advanced_ui;
    }
	
    public boolean sendMailMessage(String host, String port,String user, String path,
    		String[] froms,String[] tos,String theme,String text,String mime,String charSet) throws KrnException {
    	return s_.sendMailMessage(us.id,host, port,user,path,froms, tos, theme, text, mime, charSet);
    }

    public boolean isValidEmailAddress(String email){
        return s_.isValidEmailAddress(us.id, email);
    }

    public boolean checkUserHasRight(SystemAction action, long userId, KrnObject subject) throws KrnException {
    	return s_.checkUserHasRight(us.id, action, userId, subject);
    }

    public String[][] getColumnsInfo(String tableName) throws KrnException {//TODO: Tedit
    	return s_.getColumnsInfo(us.id, tableName);
    }
    
    public boolean columnMove(int[] cols, String tableName) throws KrnException {//TODO: Tedit
    	return s_.columnMove(us.id, cols, tableName);
    }
    
    public List<Long> getUserSubjects(SystemAction action, long userId) throws KrnException {
    	return s_.getUserSubjects(us.id, action, userId);
    }

    public UserSessionValue getUserSession() {
    	return us;
    }
    
    // Записывать изменения значений в репликационный файл?
	public boolean isDataLog() {
		return s_.isDataLog();
	}
	
	public void setDataLog(boolean logData) {
		s_.setDataLog(logData);
	}
	
	public void addAttrChangeListener(long classId) throws KrnException {
		s_.addAttrChangeListener(us.id, classId);
	}

	public byte[] getUserPhoto(String uid) throws KrnException {
		return s_.getUserPhoto(uid);
	}

	public List<Long> findForeignProcess(long processDefId, long cutObjId) throws KrnException {
    	return s_.findForeignProcess(us.id, processDefId, cutObjId);
    }

	
	public String generateWS(byte[] wsdlFileInBytes, String fileName, String packageName, String methodName) {
		return s_.generateWS(us.id, wsdlFileInBytes, fileName, packageName,
				methodName);
	}

	public byte[] generateXML(String serviceName, int type) throws KrnException {
		return s_.generateXML(us.id, serviceName, type);
	}

	public List<ProjectConfiguration> getChildConfigurations(String dsParent) {
		return s_.getChildConfigurations(dsParent);
	}

	public void addConfiguration(ProjectConfiguration c, String dsParent) {
		s_.addConfiguration(c, dsParent);
	}

	public void removeConfiguration(String dsName, String dsParent) {
		s_.removeConfiguration(dsName, dsParent);
	}

	public void moveConfiguration(String dsName, String dsParent) {
		s_.moveConfiguration(dsName, dsParent);
	}
	
	public void changeConfiguration(String dsName, ProjectConfiguration c) {
		s_.changeConfiguration(dsName, c);
	}

	public void saveAllConfigurations() {
		s_.saveAllConfigurations();
	}
	public List<Long> findProcessByUiType(String uiType) throws KrnException {
		return s_.findProcessByUiType(us.id,uiType);
	}
	
	public String getReplicationDirectoryPath() throws KrnException {
		return s_.getReplicationDirectoryPath(us.id);
	}
    
	public List<TriggerInfo> getTriggers(KrnClass cls) throws KrnException {
		return s_.getTriggers(us.id, cls);
	}

	public String createTrigger(String triggerContext) throws KrnException {
		return s_.createTrigger(us.id, triggerContext);
	}

	public String removeTrigger(String triggerName) throws KrnException {
		return s_.removeTrigger(us.id, triggerName);
	}
	
    public List<Object> downloadFile(String source) throws KrnException {
		return s_.downloadFile(us.id, source);
    }
    
    public String getNextProcessNode(long flowId) throws KrnException {
		return s_.getNextProcessNode(us.id, flowId);
	}
    
    public List<String> getListProcedure(String type) throws KrnException {
		return s_.getListProcedure(us.id, type);
	}
    public byte[] getProcedureContent(String name,String type) throws KrnException {
		return s_.getProcedureContent(us.id, name,type);
	}
    public String createProcedure(String name,List params,String body) throws KrnException {
		return s_.createProcedure(us.id,name, params, body);
	}
    public boolean deleteProcedure(String name,String type) throws KrnException {
		return s_.deleteProcedure(us.id,name, type);
	}
    
    public void setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction) throws KrnException {
    	KrnAttribute attr = s_.setAttrTriggerEventExpression(us.id, expr, attrId, mode, isZeroTransaction);
    	if (attr != null) {
            removeAttribute(attr);
            addAttribute(attr);
    	}
	}
    
    public void setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction) throws KrnException {
    	ClassNode node = getClassNode(clsId);
    	KrnClass cls = s_.setClsTriggerEventExpression(us.id, expr, clsId, mode, isZeroTransaction);
        node.setKrnClass(cls);
        classHierarchy_.nodeChanged(node);
	}
    
    public boolean initServerTasks() throws KrnException{
		return s_.initServerTasks(us.id);
	}
    
    public List<Long> getVcsGroupObjects(long clsId) throws KrnException{
		return s_.getVcsGroupObjects(us.id,clsId);
    }
    
    public List<KrnVcsChange> getVcsChanges(int isFixd,int isRepld,long userId,long replId) throws KrnException{
		return s_.getVcsChanges(us.id,isFixd,isRepld,userId,replId);
    }
    
	public List<KrnVcsChange> getVcsChangesByUID(int isFixd, int isRepld, long userId, long replId, String uid) throws KrnException {
		return s_.getVcsChangesByUID(us.id, isFixd, isRepld, userId, replId, uid);
	}
    
	public List<KrnVcsChange> getVcsHistoryChanges(boolean isModel, String uid, int typyId, boolean isLastChange) throws KrnException {
		return s_.getVcsHistoryChanges(us.id, isModel, uid, typyId, isLastChange);
	}

	public List<KrnVcsChange> getVcsDifChanges(boolean isModel,long[] ids) throws KrnException {
		return s_.getVcsDifChanges(us.id, isModel, ids);
	}
	
	public String getVcsHistoryDataIncrement(KrnVcsChange change) throws KrnException{
		return s_.getVcsHistoryDataIncrement(us.id,change);
    }
    public void commitVcsObjects(List<KrnVcsChange> changes, String comment) throws KrnException{
		s_.commitVcsObjects(us.id, changes, comment);
	}
    
    public boolean setVcsUserForObject(KrnVcsChange change, long userId) throws KrnException{
        return s_.setVcsUserForObject(us.id, change, userId);
    }
    public void rollbackVcsObjects(List<KrnVcsChange> changes) throws KrnException{
    	s_.rollbackVcsObjects(us.id, changes);
	}
    
    public void orderChanged(String operation, String type, List<String> orderIds) throws KrnException {
		s_.orderChanged(us.id, operation, type, orderIds);
    }
    public Map<Long,Long> getActiveFlows() throws KrnException{
		return s_.getActiveFlows(us.id);
	}
    
    public boolean getBindingModuleToUserMode() throws KrnException {
		return s_.getBindingModuleToUserMode(us.id);
    }

    public boolean isDbReadOnly() throws KrnException {
    	return s_.isDbReadOnly(us.id);
    }
    
    public List<OrlangTriggerInfo> getOrlangTriggersInfo() throws KrnException {
    	return s_.getOrlangTriggersInfo(us.id);
    }
    
    public synchronized KrnClass getClassByUid(String uid) throws KrnException {
    	return s_.getClassByUid(us.id, uid);
    }
    
    public synchronized KrnAttribute getAttributeByUid(String uid) throws KrnException {
    	return s_.getAttributeByUid(us.id, uid);
    }
    
    public synchronized List<KrnAttribute> getAttributesByUidPart(String uid, long searchMethod) throws KrnException {
    	return s_.getAttributesByUidPart(us.id, uid, searchMethod);
    }
    
    public synchronized KrnClass getClassById(long id) throws KrnException {
    	return s_.getClassById(us.id, id);
    }
    
    public synchronized KrnAttribute getAttribute(long id) throws KrnException {
    	return s_.getAttributeById(us.id, id);
    }
    
    public void setDbId(String name,long value) throws KrnException {
    	s_.setDbId(us.id,name,value);
    }

    public boolean convertLinkForSysDb(long newBaseId, long oldBaseId) throws KrnException {
    	return s_.convertLinkForSysDb(us.id,newBaseId,oldBaseId);
    }
    
    public long getId(String tname) throws KrnException {
    	return s_.getId(us.id, tname);
    }
    
    public long getLastId(String tname) throws KrnException {
    	return s_.getLastId(us.id,tname);
    }
    
    public boolean isAllowConvertDb() throws KrnException {
    	return s_.isAllowConvertDb(us.id);
    }
    
    public String getUrlConnection() throws KrnException {
    	return s_.getUrlConnection(us.id);
    }
    
    public void stopTerminalThread(long threadId) {
    	s_.stopTerminalThread(threadId);
    }
    
    public List runSql(String sql,int limit,boolean isUpdate) throws KrnException {
    	return s_.runSql(us.id, sql, limit, isUpdate);
    }
    
    public void setLoggingGetObjSql(boolean logginGetObjSql) throws KrnException {
    	s_.setLoggingGetObjSql(us.id, logginGetObjSql);
    }
    
    public void getout(KrnObject user, String message) throws KrnException {
    	s_.getout(us.id, user, message);
    }
    
    public void sendMessage(KrnObject user, String message) throws KrnException {
    	s_.sendMessage(us.id, user, message);
    }
    
    public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid) throws KrnException {
    	return s_.sendNotification(us.id, user, message, uid, cuid, -1);
    }
    
    public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, long trId) throws KrnException {
    	return s_.sendNotification(us.id, user, message, uid, cuid, trId);
    }
    
    public Date getServerStartupDatetime() {
    	return s_.getServerStartupDatetime(us.id);
    }
    
    public long getLoggedInUsersCount(Date period) {
    	return s_.getLoggedInUsersCount(us.id, period);
    }
    
    public long getLoggedOutUsersCount(Date period) {
    	return s_.getLoggedOutUsersCount(us.id, period);
    }
    
    public List<Long> getFiltersContainingAttr(KrnAttribute attr) throws KrnException {
    	return s_.getFiltersContainingAttr(us.id, attr);
    }
    
    public boolean isRNDB() {
    	try {
    		return s_.isRNDB(us.id);
    	} catch (KrnException e) {
    		return false;
    	}
    }
    
    public boolean isULDB() {
    	try {
    		return s_.isULDB(us.id);
    	} catch (KrnException e) {
    		return false;
    	}
    }
    
    public boolean hasUseECP() {
    	try {
    		return s_.hasUseECP(us.id);
    	} catch (KrnException e) {
    		return false;
    	}
    }

    //Jcr repository
    public String putRepositoryData(String paths, String fileName, byte[] data) throws KrnException {
    	return s_.putRepositoryData(us.id, paths, fileName, data);
    }
    public byte[] getRepositoryData(String docId) throws KrnException {
    	return s_.getRepositoryData(us.id, docId);
    }
    public String getRepositoryItemName(String docId) throws KrnException {
    	return s_.getRepositoryItemName(us.id, docId);
    }
    public String getRepositoryItemType(String docId) throws KrnException {
    	return s_.getRepositoryItemType(us.id, docId);
    }
    public boolean dropRepositoryItem(String docId) throws KrnException {
    	return s_.dropRepositoryItem(us.id, docId);
    }
    public List<String> searchByQuery(String searchName) throws KrnException {
    	return s_.searchByQuery(us.id, searchName);
    }
    public boolean isProcessRunning(long flowId) throws KrnException {
    	return s_.isProcessRunning(us.id, flowId);
    }
    
	public String getProcAllowed() throws KrnException {
    	return s_.getProcAllowed(us.id);
    }
    
    public String getProcDenied() throws KrnException {
    	return s_.getProcDenied(us.id);
    }
    
    public boolean getActivateScheduler() {
        return s_.getActivateScheduler(us.id);
    }

    public void setActivateScheduler(boolean activateScheduler) {
        s_.setActivateScheduler(us.id, activateScheduler);
    }
}