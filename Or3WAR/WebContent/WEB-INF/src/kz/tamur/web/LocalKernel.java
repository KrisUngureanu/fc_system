package kz.tamur.web;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.CommonUtil;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Driver2;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.util.LangItem;
import kz.tamur.web.common.WebClientCallback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Filter;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.AnyPair;
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
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.util.UnaryFunction;

public class LocalKernel extends Kernel {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + LocalKernel.class.getName());

     /**
      * Для кэширования объектов по UID
      */
     protected static Map<Integer, Map<String, KrnObject>> multiObjByUid =
     	new WeakHashMap<Integer, Map<String, KrnObject>>();

     /**
      * Для кэширования объектов по ID
      */
     protected static Map<Integer, Map<Long, KrnObject>> multiObjById =
     	new WeakHashMap<Integer, Map<Long, KrnObject>>();

     private static Map<Integer, Map<Long, ClassNode>> multiCnodesByClassId = new HashMap<Integer, Map<Long,ClassNode>>();
     private static Map<Integer, Map<String, ClassNode>> multiCnodesByClassName = new HashMap<Integer, Map<String,ClassNode>>();
     private static Map<Integer, Map<String, ASTStart>> multiExprsByMethodUid = new HashMap<Integer, Map<String, ASTStart>>();
     
	private int configNumber = 0;
	private static List<Integer> listenerInitialized = new ArrayList<Integer>();

	public LocalKernel(int configNumber) {
		super();
		this.configNumber = configNumber;
	}

	/** Выдает иерархию классов в виде модели для дерева
     *  @return модель представляющая иерархию классов
     */
    public synchronized TreeModel getClassHierarchy() {
        if (classHierarchy_ == null)
            classHierarchy_ = new DefaultTreeModel(hieararchyRoot_, false);

        return classHierarchy_;
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
        	log.error(e, e);
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
    public void releseClassTree(TreeModel model) {
        classTrees_.remove(model);
    }
    
    public void selectBases(long[] baseIds) throws KrnException {
        s_.selectBases(us.id, baseIds);
        propSupport.firePropertyChange("bases", null, baseIds);
    }

    /** Осуществляет освобождение ресурсов сервера
     *  и полное отключение от него.
     *  Должна быть вызвана перед завершением приложения во избежание
     *  утечек ресурсов сервера
     */
    public void release() {
/*    	if (callbackTimer != null) {
    		callbackTimer.cancel();
    		callbackTimer = null;
    	}
*/
    	if (s_ != null && us != null)
            s_.release(us.id);

    	if (callback != null) {
    		callback.interrupt();
    		callback = null;
    	}
    }

    /** Возвращает класс по его идентификатору
     *  @param id идентификатор класса
     *  @return класс
     */
    public KrnClass getClass(long id) throws KrnException {
        ClassNode cnode = getClassNode(id);
        return (cnode == null) ? null : cnode.getKrnClass();
    }

    /** Возвращает узел в дереве классов по идентификатору класса
     *  @param id идентификатор класса
     *  @return узел в дереве классов
     */
    public ClassNode getClassNode(long id) throws KrnException {
        ClassNode cnode = cnodesByClassId_.get(new Long(id));
        if (cnode == null) {
            cnode = new ClassNode(s_.getClassById(us.id, id));
            addClass(cnode);
        }
        return cnode;
    }

    /** Возвращает класс по его имени
     *  @param name имя класса
     *  @return класс
     */
    public KrnClass getClassByName(String name)
            throws KrnException {
        ClassNode cnode = getClassNodeByName(name);
        return (cnode == null) ? null : cnode.getKrnClass();
    }

    /** Возвращает узел в дереве классов по имени класса
     *  @param name имя класса
     *  @return узел в дереве классов
     */
    public ClassNode getClassNodeByName(String name)
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

    public void setAutoCommit(boolean isAutoCommit) {
        isAutoCommit_ = isAutoCommit;
    }
    
    /** Создает новый класс в системе
     *  @param baseClass базовый класс
     *  @param name имя создаваемого класса
     *  @return узел в дереве классов
     */
    public ClassNode createClass(KrnClass baseClass, String name, boolean isRepl, int mod)
            throws KrnException {
        KrnClass newClass = s_.createClass(us.id, baseClass, name, isRepl, null, mod);

        ClassNode baseNode = getClassNode(baseClass);
        ClassNode newChild = new ClassNode(newClass);

        if (classHierarchy_ != null) {
            classHierarchy_.insertNodeInto(
                    newChild, baseNode, baseNode.getChildCount()
            );
        }

        return newChild;
    }

    public KrnClass changeClass(
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
    public void deleteClass(KrnClass cls) throws KrnException {
    	s_.deleteClass(us.id, cls);
        ClassNode cnode = removeClass(cls);

        if (classHierarchy_ != null)
            classHierarchy_.removeNodeFromParent(cnode);
    }

    /** Возвращает атрибуты класса
     *  @param cls класс
     *  @return список атрибутов
     */
    public List<KrnAttribute> getAttributes(KrnClass cls) {
        ClassNode cnode = cnodesByClassId_.get(
                new Long(cls.id)
        );
        return cnode.getAttributes();
    }

    /** Возвращает атрибуты заданного типа класса
     *  @param classId класс
     *  @return список атрибутов
     */
    public List<KrnAttribute> getAttributesByTypeId(long classId, boolean inherited) throws KrnException {
        List<KrnAttribute> res = new ArrayList<KrnAttribute>();
        KrnAttribute[] attrs = s_.getAttributesByTypeId(us.id, classId, inherited);
        for (int i = 0; i < attrs.length; i++)
            res.add(attrs[i]);
        return res;
    }

    public KrnAttribute[] getAttributesByClassId(KrnClass cls) throws KrnException {
        return s_.getAttributes(us.id, cls);
    }

    /** Поиск строки и.или UID для панели Search
     *  @param objTitle строка поиска
     *  @param objUID   UID объекта тип атрибута
     */
    public List<KrnSearchResult> getConfigsByConditions(String objTitle, String objUID)
    			throws KrnException {
		return s_.getConfigsByConditions(us.id, objTitle, objUID);
    }

    
    /** Создает новый атрибут
     *  @param cls класс, в котором создается атрибут
     *  @param type тип атрибута
     *  @param name имя атрибута
     *  @param collType является ли атрибут множественным
     *  @param isUnique является ли атрибут уникальным
     *  @return созданный атрибут
     */
    public KrnAttribute createAttribute(
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
            int accessModifier) throws KrnException {
        KrnAttribute attr = s_.createAttribute(
        		us.id, cls, type, name, collType, isUnique, isUnique,
        		isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier);
        addAttribute(attr);
        return attr;
    }

    public KrnAttribute changeAttribute(
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
        long oldTypeId = attr.typeClassId;
        final KrnAttribute newAttr = s_.changeAttribute(
        		us.id, attr, type, name, colType, isUnique, isIndexed,
        		isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier);

        attrById.put(attr.id, newAttr);

        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);

            List anodes = new LinkedList();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                if (o instanceof MethodNode) {
                                    return false;
                                }
                                return (newAttr.id
                                        == ((AttrNode) o).getKrnAttribute().id);
                            }
                        }
                );
            } catch (KrnException e) {
            	log.error(e, e);
            }

            for (Iterator it = anodes.iterator(); it.hasNext();) {
                AttrNode node = (AttrNode) it.next();
                CommonUtil.copy(node.getKrnAttribute(), newAttr);
                if (oldTypeId != newAttr.typeClassId) {
                    if (node.children_ != null)
                        node.children_.clear();
                    node.loaded_ = false;
                }
                model.nodeChanged(node);
            }
        }
        return attr;
    }

    /** Удаляет атрибут
     *  @param attr удаляемый атрибут
     */
    public void deleteAttribute(KrnAttribute attr)
            throws KrnException {
        s_.deleteAttribute(us.id, attr);
        removeAttribute(attr);
    }

    /** Возвращает уникальный атрибут
     *  @param cls класс в котром ищется атрибут
     *  @return уникальный атрибут или null если такого нет
     */
    public KrnAttribute findUniqueAttribute(KrnClass cls) {
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
    public KrnObject[] getClassObjects(KrnClass cls, long tid)
            throws KrnException {
        return s_.getClassObjects(us.id, cls, new long[0], tid);
    }

    public KrnObject[] getClassObjects(KrnClass cls,
			long[] filterIds, long tid) throws KrnException {
		return s_.getClassObjects2(us.id, cls, filterIds, new int[1], tid);
	}

    public KrnObject[] getClassObjects(KrnClass cls,
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
		QueryResult res = s_.getClassObjects3(us.id, cls, req, filterIds, limit[0], tid);
		limit[0] = res.totalRows;
		return res.rows;
	}

    public KrnObject[] getObjectsByAttribute(
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

    public int getMaxIndex(long objectId, long attrId, long langId, long tid)
            throws KrnException {
        return s_.getMaxIndex(us.id, objectId,attrId,langId, tid);
    }

    public KrnIndex createIndex(KrnClass cls,KrnAttribute[] attrs,boolean[] descs) throws KrnException{
    	KrnIndex ret = s_.createIndex(us.id, cls, attrs,descs);
    	return ret;
    }
    
    public KrnIndex[] getIndexesByClassId(KrnClass cls) throws KrnException {
    	return s_.getIndexesByClassId(us.id, cls);
    }
    
    public KrnIndexKey[] getIndexKeysByIndexId(KrnIndex ndx) throws KrnException{
    	return s_.getIndexKeysByIndexId(us.id, ndx);
    }
    public KrnAttribute[] getAttributesForIndexing(KrnClass cls) throws KrnException{
    	return s_.getAttributesForIndexing(us.id, cls);
    }
    
    public void deleteIndex(KrnIndex ndx) throws KrnException {
    	s_.deleteIndex(us.id, ndx);
    }
    
    /** Создает новый объект
     *  @param cls класс объекта
     *  @return вновь созданный объект
     */
    public KrnObject createObject(KrnClass cls, long tid)
        throws KrnException {
        KrnObject res = s_.createObject(us.id, cls, tid);
        return res;
    }

    public KrnObject createObject(KrnClass cls, String uid, long tid)
			throws KrnException {
		KrnObject res = s_.createObjectWithUid(us.id, cls, uid, tid);
		return res;
	}

    /** Удаляет объект
     *  @param obj удаляемый объекта
     */
    public void deleteObject(KrnObject obj, long tid)
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
    public void setObject(
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
    public void setObject(
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
    public void setString(
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
    public void setString(
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
    public void setMemo(
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
    public void setMemo(
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
    public void setBlob(
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
    public void setBlob(
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
    public void setLong(
            long objectId,
            long attrId,
            int index,
            long val,
            long tid
            ) throws KrnException {
    	s_.setLong(us.id, objectId, attrId, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Целое
     *  @param objectId объекта, чей атрибут устанавливается
     *  @param classId id класса объекта, чей атрибут устанавливается
     *  @param attrName имя устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public void setLong(
            long objectId,
            long classId,
            String attrName,
            int index,
            long val,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setLong(objectId, (int)attr.id, index, val, tid);
    }

    /** Устанавливает значение атрибута объекта типа Float
     *  @param objectId id объекта, чей атрибут устанавливается
     *  @param attrId устанвливаемого атрибута
     *  @param index индекс (0 - если атрибут не множественный)
     *  @param val значение атрибута
     */
    public void setFloat(
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
    public void setFloat(
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
    public void setDate(
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
    public void setDate(
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
    public void setTime(
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
    public void setTime(
            int objectId,
            int classId,
            String attrName,
            int index,
            Date val,
            int tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        setTime(objectId, (int)attr.id, index, val, tid);
    }

    /** Возвращает значения атрибута объекта типа Объект
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public KrnObject[] getObjects(KrnObject obj, KrnAttribute attr,
                                               long tid) throws KrnException {
        return getObjects(obj, attr, new long[0], tid);
    }

    public KrnObject[] getObjectsLiveOfClass(KrnClass cls) throws KrnException {
        return s_.getObjectsLiveOfClass(us.id, cls);
    }

    public KrnObject[] getObjects(
            KrnObject obj,
            KrnAttribute attr,
            long[] filterIds,
            long tid
            ) throws KrnException {
        return s_.getObjects(us.id, obj.id, attr.id, filterIds, tid);
    }

    /** Возвращает значения атрибута объекта типа Объект
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public KrnObject[] getObjects(KrnObject obj, String attrName,
                                               long tid) throws KrnException {
        return getObjects(obj, attrName, new long[0], tid);
    }

    public KrnObject[] getObjects(KrnObject obj, String attrName, long[] filterIds, long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return attr == null ? null : getObjects(obj, attr, filterIds, tid);
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

    public void changeTimerTask(long objId, boolean isDelete ) throws KrnException {
        s_.changeTimerTask(us.id, objId,isDelete);
    }

    public void  executeTask(long objId) throws KrnException {
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

    public SuperMap[] getMapList(long[] flowIds) throws KrnException {
        return s_.getMapList(us.id, flowIds);
    }
    public Activity[] getTaskList() throws KrnException {
        return s_.getTaskList(us.id);
    }
    public void setTaskListFilter(Map params) throws KrnException {
        AnyPair[] apars=new AnyPair[params.size()];
        int i=0;
            for(Iterator it=params.keySet().iterator();it.hasNext();){
                Object key=it.next();
                Object value=params.get(key);
                if(value instanceof Date){
                com.cifs.or2.kernel.Date date=kz.tamur.util.Funcs.convertDate((Date)value);
                apars[i++]=new AnyPair((String)key,date);
                }else{
                    apars[i++]=new AnyPair((String)key,value);
                }
        }
        s_.setTaskListFilter(us.id, apars);
    }
    
    public Activity getTask(long flowId,long ifsPar, boolean isCheckEvent, boolean onlyMy) throws KrnException {
        return s_.getTask(us.id, flowId, ifsPar, isCheckEvent, onlyMy);
    }
    
    public void updateUser(KrnObject obj, String name) throws KrnException {
        s_.updateUser(us.id, obj, name);
    }

    public void userCreated(String name) throws KrnException {
        s_.userCreated(us.id, name);
    }

    public void userDeleted(String name) throws KrnException {
        s_.userDeleted(us.id, name);
    }

    public void userRightsChanged(String name) throws KrnException {
        s_.userRightsChanged(us.id, name);
    }

    public void userBlocked(String name) throws KrnException {
        s_.userBlocked(us.id, name);
    }

    public void userUnblocked(String name) throws KrnException {
        s_.userUnblocked(us.id, name);
    }

    public boolean setSelectedObjects(long flowId,long nodeId,KrnObject[] sel_objs) throws KrnException {
        return s_.setSelectedObjects(us.id, flowId,nodeId,sel_objs);
    }

    public void setLang(KrnObject lang) throws KrnException {
        s_.setLang(us.id, lang);
    }
    public void reloadProcessDefinition(long processDefId) throws KrnException {
        s_.reloadProcessDefinition(us.id, processDefId);
    }

    public long[] getProcessDefinitions() throws KrnException {
        return s_.getProcessDefinitions(us.id);
    }

    public String[] startProcess(long processDefinition) throws KrnException {
        return s_.startProcess(us.id, processDefinition, null);
    }

    public boolean cancelProcess(long processId, String nodeId, boolean isAll, boolean forceCancel) throws KrnException {
        return s_.cancelProcess(us.id, processId, nodeId, isAll, forceCancel);
    }
    public boolean reloadFlow(long flowId) throws KrnException {
        return s_.reloadFlow(us.id, flowId);
    }

    public void setPermitPerform( long flowId, boolean permit ) throws KrnException {
        s_.setPermitPerform(us.id, flowId,permit);
    }
    public Object openInterface( long ui,long flowId,long trId,long pdId) throws KrnException {
        return s_.openInterface(us.id, ui,flowId,trId,pdId);
    }
    public String[] performActivitys(Activity[] activitys,String transition) throws KrnException {
        return s_.performActivitys(us.id, activitys,transition,null);
    }

    public void startTransport(int transportId) throws KrnException {
        s_.startTransport(us.id, transportId);
    }
    public void restartTransport(int transportId) throws KrnException {
        s_.restartTransport(us.id, transportId);
    }
    public String resendMessage(Activity act) throws KrnException {
        return s_.resendMessage(us.id, act);
    }
    public byte[] getTransportParam(int transportId) throws KrnException {
        return s_.getTransportParam(us.id, transportId);
    }
    public void setTransportParam(byte[] data,int transportId) throws KrnException {
        s_.setTransportParam(us.id, data,transportId);
    }
    public void reloadBox(KrnObject obj) throws KrnException {
        s_.reloadBox(us.id, obj);
    }
    public void saveFilter(long filterId) throws KrnException {
        s_.saveFilter(us.id, filterId);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds, KrnAttribute attr, long tid) throws KrnException {
        return getObjectValues(objIds, attr.classId, attr, new long[0], new int[1], tid);
    }

    public ObjectValue[] getObjectValues(
            long[] objIds,
            KrnAttribute attr,
            long[] filterIds,
            int[] limit,
            long tid
            ) throws KrnException {
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, limit, tid);
    }

    public ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            String attrName,
            long tid
            ) throws KrnException {
        return getObjectValues(objIds, classId, attrName, new long[0], tid);
    }

    public ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            String attrName,
            long[] filterIds,
            int[] limit,
            long tid
            ) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getObjectValues(objIds, attr, filterIds, limit, tid);
    }

    /** Возвращает значения атрибута объекта типа Строка
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public String[] getStrings(KrnObject obj, KrnAttribute attr,
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
    public String[] getStrings(KrnObject obj, String attrName,
                                            long langId, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getStrings(obj, attr, langId, tid);
    }

    public byte[] getBlob(long objId, KrnAttribute attr, int index, long langId,
                                       long tid) throws KrnException {
		byte[] data = s_.getBlob(us.id, objId, attr.id, index, langId, tid, false);
		return data;
    }

    public byte[] getBlob(KrnObject obj, String attrName,
                                       int index, long langId, long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getBlob(obj.id, attr, index, langId, tid);
    }

    public byte[][] getBlobs(long objId, KrnAttribute attr, long langId,
                                       long tid) throws KrnException {
        byte[][] res = s_.getBlobs(us.id, objId, attr.id,langId, tid, false);
        return res;
    }

    public byte[][] getBlobs(KrnObject obj, String attrName,
                                       int langId, int tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return s_.getBlobs(us.id, (int)obj.id, (int)attr.id, langId, tid, false);
    }

    public StringValue[] getStringValues(
            long[] objIds,
            KrnAttribute attr,
            long langId,
            boolean isMemo,
            long tid
            ) throws KrnException {
        return s_.getStringValues(us.id, objIds, (int)attr.id, langId, isMemo, tid);
    }

    public StringValue[] getStringValues(
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

    /** Возвращает значения атрибута объекта типа Memo
     *  @param obj объект
     *  @param attr атрибут
     *  @return массив значений атрибута
     */
    public String[] getMemos(KrnObject obj, KrnAttribute attr,
                                          int langId, int tid)
            throws KrnException {
        return s_.getStrings(us.id, (int)obj.id, (int)attr.id, langId, true, tid);
    }

    /** Возвращает значения атрибута объекта типа Memo
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public String[] getMemos(KrnObject obj, String attrName,
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
    public long[] getLongs(KrnObject obj, KrnAttribute attr,
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
    public long[] getLongs(KrnObject obj, String attrName, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getLongs(obj, attr, tid);
    }

    public LongValue[] getLongValues(long[] objIds,
                                                  KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getLongValues(us.id, objIds, (int)attr.id, tid);
    }

    public LongValue[] getLongValues(long[] objIds, long classId,
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
    public double[] getFloats(KrnObject obj, KrnAttribute attr,
                                           long tid)
            throws KrnException {
        return s_.getFloats(us.id, obj, attr, tid);
    }

    /** Возвращает значения атрибута объекта типа Float
     *  @param obj объект
     *  @param attrName атрибут
     *  @return массив значений атрибута
     */
    public double[] getFloats(KrnObject obj, String attrName,
                                           long tid) throws KrnException {
        ClassNode cnode = getClassNode(obj.classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getFloats(obj, attr, tid);
    }

    public FloatValue[] getFloatValues(long[] objIds,
                                                    KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getFloatValues(us.id, objIds, attr.id, tid);
    }

    public FloatValue[] getFloatValues(long[] objIds, long classId,
                                                    String attrName, long tid)
            throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        return getFloatValues(objIds, attr, tid);
    }

    public DateValue[] getDateValues(long[] objIds,
                                                  KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getDateValues(us.id, objIds, attr.id, tid);
    }

    public TimeValue[] getTimeValues(long[] objIds,
                                                  KrnAttribute attr, long tid)
            throws KrnException {
        return s_.getTimeValues(us.id, objIds, attr.id, tid);
    }

    public void deleteValue(long objectId, long attrId,
                                         int[] indexes, long tid)
            throws KrnException {
    	s_.deleteValue(us.id, objectId, attrId, indexes, 0, tid);
    }

    public void deleteValue(long objectId, long classId,
                                         String attrName, int[] indexes,
                                         long tid) throws KrnException {
        ClassNode cnode = getClassNode(classId);
        KrnAttribute attr = cnode.getAttribute(attrName);
        deleteValue(objectId, (int)attr.id, indexes, tid);
    }

    public void deleteValue(long objectId, long classId,
			String attrName, Collection<Object> values, long tid)
			throws KrnException {
		ClassNode cnode = getClassNode(classId);
		KrnAttribute attr = cnode.getAttribute(attrName);
		deleteValue(objectId, (int) attr.id, values, tid);
	}

    public void deleteValue(
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

    @Override
    public void init(String userName, String path, String newPath, String confPath, String serverHost, String serverPort, String baseName, String typeClient,
			String ip, String host, int loginType, SessionOpsOperations ops) throws KrnException {
    	init(userName, path, newPath, confPath, serverHost, serverPort, baseName, typeClient, ip, host, loginType, ops, false, false, false, null);
    }
    
    @Override
    public void init(String userName, String path, String newPath, String confPath, String serverHost, String serverPort, String baseName, String typeClient,
			String ip, String host, int loginType, SessionOpsOperations ops, boolean force, boolean sLogin, boolean isUseECP, String signedData) throws KrnException {
    	
    	this.baseName = baseName;
    	this.serverHost = serverHost;
    	this.serverPort = serverPort;
    	this.s_ = ops;
    	
    	SecurityContextHolder.setKernel(this);

		if (LOGIN_CERT == loginType)
    		us = s_.loginWithCert(baseName, userName, typeClient, path, ip, host, true);
    	else if (LOGIN_USUAL == loginType)
    		us = s_.login(baseName, userName, typeClient, path, newPath, confPath, ip, host, true, force, sLogin, isUseECP, signedData);
    	else if (LOGIN_DN == loginType)
    		us = s_.loginWithDN(baseName, userName, typeClient, ip, host, true, force);
    	else if (LOGIN_LDAP == loginType)
    		us = s_.loginWithLDAP(baseName, userName, typeClient, ip, host, true);
        //s_ = mgr_.getSession(userName, path, address.getHostAddress(), address.getHostName(), args, callback);

    	try {
            classTrees_ = new ArrayList<ClassTreeModel>();
            
        	synchronized (multiCnodesByClassId) {
        		cnodesByClassId_ = multiCnodesByClassId.get(configNumber);
        		if (cnodesByClassId_ == null) {
        			cnodesByClassId_ = Collections.synchronizedMap(new HashMap<Long, ClassNode>());
        			multiCnodesByClassId.put(configNumber, cnodesByClassId_);
        		}
        	}
        	synchronized (multiCnodesByClassName) {
        		cnodesByClassName_ = multiCnodesByClassName.get(configNumber);
        		if (cnodesByClassName_ == null) {
        			cnodesByClassName_ = Collections.synchronizedMap(new HashMap<String, ClassNode>());
        			multiCnodesByClassName.put(configNumber, cnodesByClassName_);
        		}
        	}
        	synchronized (multiExprsByMethodUid) {
        		exprByMethodUid_ = multiExprsByMethodUid.get(configNumber);
        		if (exprByMethodUid_ == null) {
        			exprByMethodUid_ = Collections.synchronizedMap(new HashMap<String, ASTStart>());
        			multiExprsByMethodUid.put(configNumber, exprByMethodUid_);
        		}
        	}
        	synchronized (listenerInitialized) {
        		if (!listenerInitialized.contains(configNumber)) {
        			Driver2.addModelChangeListener(new ModelChangeAdapter(configNumber, baseName));
        			listenerInitialized.add(configNumber);
        		}
			}
        	
            hieararchyRoot_ = new ClassNode(getClassByName("Объект"));
            langsByCode=new HashMap<String,Long>();

    		SC_UI = getClassByName("UI");

            SC_LANGUAGE = getClassByName("Language");
            LANGUAGES = getClassObjects(SC_LANGUAGE, 0);
            SC_MENUITEMSDESC = getClassByName("MenuItemsDesc");
            MENU_ITEMS_HELP = getClassObjects(SC_MENUITEMSDESC, 0);

            SC_GUICOMPONENT = getClassByName("GuiComponent");
            SC_FILTER = getClassByName("Filter");
            SC_HIPERTREE = getClassByName("HiperTree");
            SC_USER = getClassByName("User");
            SC_USER_FOLDER = getClassByName("UserFolder");
            SC_TIMER = getClassByName("Timer");
            SC_FLOW = getClassByName("Flow");
            SC_PROCESS_DEF = getClassByName("ProcessDef"); 
            SC_CONTROL_FOLDER = checkExistenceClassByName(Constants.NAME_CLASS_CONTROL_FOLDER) ? getClassByName(Constants.NAME_CLASS_CONTROL_FOLDER) : null;
            SC_CONTROL_FOLDER_ROOT = checkExistenceClassByName(Constants.NAME_CLASS_CONTROL_FOLDER_ROOT) ? getClassByName(Constants.NAME_CLASS_CONTROL_FOLDER_ROOT) : null;
            SC_REPORT_PRINTER = getClassByName("ReportPrinter");
        } catch (Exception e) {
            // Игнорируем ошибку для возможности добавить недостающие классы
            // при помощи Администратора
        	log.error(e, e);
        }
        user_ = new User(us.userObj, this, us.typeClient);
//        updateUser(user_.getObject(),"");
        LangItem.initialize(this);
        
        callback = getCallback();
        //callbackTimer = new Timer();
        //callbackTimer.schedule(callback, 10000, 10000);
    }
    
    public Thread getCallback() {
    	if (callback == null)
            callback = new WebClientCallback(this);
    	return callback;
    }

    public KrnObject getInterfaceLanguage() {
        return user_.getIfcLang();
    }

    public KrnObject getDataLanguage() {
        return user_.getDataLanguage();
    }

    public KrnObject getInterface() {
        return user_.getIfc();
    }

    public void setInterfaceLanguage(KrnObject langObj)
            throws KrnException {
        if (user_.getIfcLang() == null || langObj.id != user_.getIfcLang().id) {
            setObject(
                    (int)user_.object.id,
                    (int)user_.object.classId,
                    "interface language",
                    0,
                    (int)langObj.id,
                    0, false);
            user_.setIfcLang(langObj);
        }
    }

    public void setDataLanguage(KrnObject lang)
            throws KrnException {
        s_.setDataLanguage(us.id, lang);
        if (user_.getDataLanguage() == null || lang.id != user_.getDataLanguage().id) {
            setObject(user_.object.id, user_.object.classId,
                    "data language", 0, lang.id, 0, false);
            user_.setDataLanguage(lang);
        }
    }

    public User getUser() {
        return user_;
    }

    public ArrayList getFilters(String className) {
        return (ArrayList) filters_.get(className);
    }

    public void addFilter(Filter filter) {
        filters_.put(filter.className, filter);
    }

    public long[] getFilteredObjectIds(
            long[] filterIds, FilterDate[] dates, int[] limit, long trId) throws KrnException {
        return s_.getFilteredObjectIds(us.id, filterIds, dates,limit, trId);
    }

    public long[] getFilteredObjectIds2(
            String[] filterUids, FilterDate[] dates, int[] limit) throws KrnException {
        return s_.getFilteredObjectIds2(us.id, filterUids, dates, limit);
    }

    protected void addClass(ClassNode cw) {
        cnodesByClassId_.put(new Long(cw.getId()), cw);
        cnodesByClassName_.put(cw.getName(), cw);
    }

    protected ClassNode removeClass(KrnClass cls) {
        cnodesByClassName_.remove(cls.name);
        return cnodesByClassId_.remove(new Long(cls.id));
    }

    protected ClassNode getClassNode(KrnClass cls) {
        return cnodesByClassId_.get(new Long(cls.id));
    }

    protected void addAttribute(final KrnAttribute attr) {
    	attrById.put(attr.id, attr);
        ClassNode cnode = cnodesByClassId_.get(
                new Long(attr.classId)
        );

        cnode.addAttribute(attr);

        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);

            List anodes = new LinkedList();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                                AttrNode an = (AttrNode)o;
                                if (an.getKrnAttribute() != null) {
                                    return (attr.classId == an.getKrnAttribute().typeClassId);
                                } else {
                                    return false;
                                }
                            }
                        }
                );
            } catch (KrnException e) {
            	log.error(e, e);
            }

            for (Iterator it = anodes.iterator(); it.hasNext();) {
                AttrNode parent = (AttrNode) it.next();
                model.insertNodeInto(new AttrNode(parent, attr, parent.canViewAttrs, parent.canViewMethods), parent,
                                     parent.getChildCount());
            }
        }
    }

    protected void removeAttribute(final KrnAttribute attr) {
    	attrById.remove(attr.id);
        for (int i = 0; i < classTrees_.size(); ++i) {
            ClassTreeModel model = classTrees_.get(i);

            List anodes = new LinkedList();
            try {
                ((AttrNode) model.getRoot()).findNodes(
                        false,
                        anodes,
                        new UnaryFunction() {
                            public boolean check(Object o) {
                            	if (o instanceof MethodNode) {
                            		return false;
                            	}
                                return (attr.id == ((AttrNode) o).getKrnAttribute().id);
                            }
                        }
                );
            } catch (KrnException e) {
            	log.error(e, e);
            }

            for (Iterator it = anodes.iterator(); it.hasNext();)
                model.removeNodeFromParent((AttrNode) it.next());
        }

        ClassNode cnode = cnodesByClassId_.get(
                new Long(attr.classId)
        );
        cnode.removeAttribute(attr);
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
    	if (currDb == null) {
    		try {
    			currDb = s_.getCurrentDb(us.id);
    		} catch (KrnException e) {
    			log.error(e, e);
    		}
    	}
        return currDb;
    }

    public String getUId(long id)
            throws KrnException {
        KrnObject[] objs = s_.getObjectsById(us.id, new long[] {id},-1);
        return (objs.length > 0) ? objs[0].uid : null;
    }

    public KrnAttribute getAttributeByName(KrnClass cls, String name)
            throws KrnException {
        ClassNode cn = getClassNode(cls.id);
        return cn.getAttribute(name);
    }

    public KrnAttribute getAttributeById(long id)
            throws KrnException {
    	KrnAttribute attr = attrById.get(id);
    	if (attr == null) {
    		attr = s_.getAttributeById(us.id, id);
    		attrById.put(id, attr);
    	}
    	return attr;
    }

    public String getStringsSingular(
            long objId,
            long attrId,
            long langId,
            boolean isMemo,
            boolean isNotNull
            )
            throws KrnException {
        String[] vals = s_.getStrings(us.id, objId, attrId, langId, isMemo, 0);
        if (vals.length == 0) {
            if (isNotNull) {
                throw new KrnException(0,
                        "У объекта " + objId
                        + " не определено значение свойства id=" + attrId);
            } else
                return "";
        }
        return vals[0];
    }

    public long getLongsSingular(
            KrnObject obj,
            KrnAttribute attr,
            boolean isNotNull
            ) throws KrnException {
        long[] vals = s_.getLongs(us.id, obj.id, attr.id, 0);
        if (vals.length == 0) {
            if (isNotNull) {
                throw new KrnException(0, "У объекта [id=" + obj.id + "] не определено значение свойства '" + attr.name + "' [id=" + attr.id + "]");
            } else
                return 0;
        }
        return vals[0];
    }

    public KrnObject getObjectsSingular(
            long objId,
            long attrId,
            boolean isNotNull
            )
            throws KrnException {
        KrnObject[] vals = s_.getObjects(us.id, objId, attrId, new long[0], 0);
        if (vals.length == 0) {
            if (isNotNull) {
                throw new KrnException(0, "У объекта " + objId + " не определено значение свойства id=" + attrId);
            } else
                return null;
        }
        return vals[0];
    }

    public KrnObject getObjectByUid(String uid, long trId) throws KrnException {
    	KrnObject[] objs = s_.getObjectsByUid(us.id, new String[] {uid}, trId);
    	return objs.length > 0 ? objs[0] : null;
    }
    
    public KrnObject getObjectById(long id, long trId) throws KrnException {
    	KrnObject[] objs = s_.getObjectsById(us.id, new long[] {id}, trId);
    	return objs.length > 0 ? objs[0] : null;
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

    public boolean isSubclassOf(long classId, long parentClassId) throws KrnException {
    	ClassNode cnode = (ClassNode)getClassNode(classId);
        while (cnode != null && cnode.getId() != parentClassId) {
        	cnode = (ClassNode)getClassNode(cnode.getId()).getParent();
        }
        return (cnode != null);
    }

    @Override
    public boolean isSE_UI() {
        return seUI;
    }

    @Override
    public boolean isADVANCED_UI() {
        return advancedUI;
    }
	
    protected void cacheObject(KrnObject obj) {
    	Map<Long, KrnObject> idMap = null;

    	synchronized (multiObjById) {
    		idMap = multiObjById.get(configNumber);
    		if (idMap == null) {
    			idMap = new WeakHashMap<Long, KrnObject>();
    			multiObjById.put(configNumber, idMap);
    		}
    	}

    	synchronized (idMap) {
    		idMap.put(obj.id, obj);
    	}

    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	synchronized (uidMap) {
    		uidMap.put(new String(obj.uid), obj);
    	}
    }
    
    public KrnObject getCachedObjectByUid(String uid) throws KrnException {
    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	KrnObject obj = null;
    	synchronized (uidMap) {
    		if (uidMap.containsKey(uid))
    			return uidMap.get(uid);
    		obj = getObjectByUid(uid, -1);
    		if (obj == null) {
    			uidMap.put(uid, null);
    		}
		}
    	if (obj != null) {
    		cacheObject(obj);
    		return obj;
    	}

    	return null;
    }
    
    public KrnObject getCachedObjectById(Long id) throws KrnException {
    	Map<Long, KrnObject> idMap = null;

    	synchronized (multiObjById) {
    		idMap = multiObjById.get(configNumber);
    		if (idMap == null) {
    			idMap = new WeakHashMap<Long, KrnObject>();
    			multiObjById.put(configNumber, idMap);
    		}
    	}

    	KrnObject obj = null;
    	synchronized (idMap) {
    		if (idMap.containsKey(id))
    			return idMap.get(id);
    		obj = getObjectById(id, -1);
    		if (obj == null) {
    			idMap.put(id, null);
    		}
		}
    	if (obj != null) {
    		cacheObject(obj);
    		return obj;
    	}
    	return null;
    }
    
    public void addToCache(Set<String> uids) throws KrnException {
    	Map<String, KrnObject> uidMap = null;
    	
    	synchronized (multiObjByUid) {
    		uidMap = multiObjByUid.get(configNumber);
    		if (uidMap == null) {
    			uidMap = new WeakHashMap<String, KrnObject>();
    			multiObjByUid.put(configNumber, uidMap);
    		}
    	}

    	synchronized (uidMap) {
	    	uids.removeAll(uidMap.keySet());
    	}
    	
    	if (uids.size() > 0) {
    		KrnObject[] objs = getObjectsByUid(uids.toArray(new String[uids.size()]), 0);
    		for (KrnObject obj : objs)
    			cacheObject(obj);
    	}
    }
    
	public String getNextProcessNode(long flowId) throws KrnException {
		return s_.getNextProcessNode(us.id, flowId);
	}
	
    protected static ClassNode getClassNodeById(int configNumber, long classId, Session s) {
    	Map<Long, ClassNode> map = null;
    	synchronized (multiCnodesByClassId) {
        	map = multiCnodesByClassId.get(configNumber);
		}
    	ClassNode cnode = map.get(classId);
        if (cnode == null) {
        	KrnClass cls = s.getClassById(classId);
        	int level = 0;
            if (cls.parentId > 0)
                level = getClassNodeById(configNumber, cls.parentId, s).getLevel() + 1;

            cnode = new ClassNode(cls, level);

            map.put(new Long(classId), cnode);
            
            Map<String, ClassNode> map2 = null;
        	synchronized (multiCnodesByClassId) {
        		map2 = multiCnodesByClassName.get(configNumber);
    		}
            map2.put(cnode.getName(), cnode);
        }
        return cnode;
    }

    protected static void removeClassNode(int configNumber, KrnClass cls) {
    	Map<Long, ClassNode> map = null;
    	synchronized (multiCnodesByClassId) {
        	map = multiCnodesByClassId.get(configNumber);
		}
    	map.remove(cls.id);
    	
        Map<String, ClassNode> map2 = null;
    	synchronized (multiCnodesByClassId) {
    		map2 = multiCnodesByClassName.get(configNumber);
		}
        map2.remove(cls.name);
    }
    
    public static void removeMethodFromCache(int configNumber, String uid) {
    	Map<String, ASTStart> map = null;
    	synchronized (multiExprsByMethodUid) {
        	map = multiExprsByMethodUid.get(configNumber);
		}
    	map.remove(uid);
    }

}