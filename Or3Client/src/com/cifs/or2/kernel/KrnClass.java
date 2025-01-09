package com.cifs.or2.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import kz.tamur.ods.ComparisonOperations;

@SuppressWarnings("serial")
public final class KrnClass implements Serializable {
	
	public String uid = null;
	public long id = (long) 0;
	public long parentId = (long) 0;
	public boolean isRepl = false;
	public int modifier = (int) 0;
	public String name = null;
	public String tname = null;
	public byte[] beforeCreateObjExpr;
	public byte[] afterCreateObjExpr;
	public byte[] beforeDeleteObjExpr;
	public byte[] afterDeleteObjExpr;
	
	public int beforeCreateObjTr;
	public int afterCreateObjTr;
	public int beforeDeleteObjTr;
	public int afterDeleteObjTr;
	
	transient private static ThreadLocal<KrnClassOperations> operations = new ThreadLocal<KrnClassOperations>();

	public KrnClass() {}

	public KrnClass(String uid, long id, long parentId, boolean isRepl,
			int modifier, String name, String tname,
			byte[] beforeCreateObjExpr, byte[] afterCreateObjExpr,
			byte[] beforeDeleteObjExpr, byte[] afterDeleteObjExpr,
			int beforeCreateObjTr, int afterCreateObjTr,
			int beforeDeleteObjTr, int afterDeleteObjTr) {
		this.uid = uid;
		this.id = id;
		this.parentId = parentId;
		this.isRepl = isRepl;
		this.modifier = modifier;
		this.name = name;
		this.tname = tname;
		this.beforeCreateObjExpr = beforeCreateObjExpr;
		this.afterCreateObjExpr = afterCreateObjExpr;
		this.beforeDeleteObjExpr = beforeDeleteObjExpr;
		this.afterDeleteObjExpr = afterDeleteObjExpr;
		this.beforeCreateObjTr = beforeCreateObjTr;
		this.afterCreateObjTr = afterCreateObjTr;
		this.beforeDeleteObjTr = beforeDeleteObjTr;
		this.afterDeleteObjTr = afterDeleteObjTr;
	}
	
	/**
	 * Возвращает id родительского класса
	 * @return id класса родителя
	 */
	public long getParentId() {
	    return parentId;
	}
	
	/**
	 * Репликация
	 * @return true - если реплицирован, иначе false
	 */
	public boolean getisRepl() {
	    return isRepl;
	}
	
	/**
	 * Возвращает кол-во модификаций
	 * @return кол-во мод-й
	 */
	public int getModifier() {
	    return modifier;
	}
	
	/**
	 * 	Возвращает ссылку на текущий класс
	 * @return текущий класс
	 */
	public KrnClass getKrnClass() {
		return this;
	}
	
	/**
	 * Возвращает id класса
	 * @return id текущего класса
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Возвращает имя класса
	 * @return имя текущего класса
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Метод возвращает uid класса
	 * @return uid текущего класса
	 */
	public String getUid() {
		return uid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof KrnClass)
			return id == ((KrnClass)obj).id;
		return false;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
	
	/**
	 * Установить операции
	 * @param operations операции
	 * @see java.lang.ThreadLocal#set(Object)
	 */
	public static void setOperations(KrnClassOperations operations) {
		KrnClass.operations.set(operations);
	}
	
	/**
	 * Возвращает оперции
	 * @return операции
	 * @see java.lang.ThreadLocal#get()
	 */
	public static KrnClassOperations getOperations() {
		return KrnClass.operations.get();
	}
	
	/**
	 * Удалить операции
	 * @see java.lang.ThreadLocal#remove()
	 */
	public static void removeOperations() {
		KrnClass.operations.remove();
	}
	
	/**
	 * Возвращает список объектов класса
	 * @return список объектов текущего класса
	 */
	public List<KrnObject> getObjects() { 
		return operations.get().getObjects(this);
	}
	
	/**
	 * Возвращает список объектов текущего класса, у которых значение атрибута равно указанному в параметре с учетом операции сравнения
	 * @param path путь к атрибуту
	 * @param value значение атрибута
	 * @param lang язык(объект класса Language)
	 * @param op операция сравнения(0-5)
	 * @return список объектов текущего класса
	 */
	public List<KrnObject> find(String path, Object value, KrnObject lang,int op) {
		return operations.get().find(this, path, value, lang, op);
	}
	
	/**
	 * Возвращает список объектов текущего класса, у которых значение атрибута равно указанному в параметре
	 * @param path путь к атрибуту
	 * @param value значение атрибута
	 * @param lang язык(объект класса Language)
	 * @return список объектов текущего класса
	 */
	public List<KrnObject> find(String path, Object value, KrnObject lang) {
		return operations.get().find(this, path, value, lang, ComparisonOperations.CO_EQUALS);
	}
	
	/**
	 * Возвращает список объектов текущего класса, у которых значение атрибута равно указанному в параметре
	 * @param path путь к атрибуту
	 * @param value значение атрибута
	 * @return список объектов текущего класса
	 */
	public List<KrnObject> find(String path, Object value) {
		return operations.get().find(this, path, value, null, ComparisonOperations.CO_EQUALS);
	}
	
	/**
	 * Создать объект текущего класса
	 * @return объект текущего класса
	 * @throws KrnException
	 */
	public KrnObject createObject() throws KrnException {
		return operations.get().createObject(this);
	}
	
	/**
	 * Создать объект текущего класса с указанием сохранности, 
	 * TRUE - сохранить, FALSE - не сохранять
	 * @param save true or false
	 * @return объект текущего класса
	 * @throws KrnException
	 */
	public KrnObject createObject(boolean save) throws KrnException {
		return operations.get().createObject(this, save);
	}
	
	/**
	 * Создать объект текущего класса с заданным uid
	 * @param uid
	 * @return объект текущего класса
	 * @throws KrnException
	 */
	public KrnObject createObject(String uid) throws KrnException {
		return operations.get().createObject(this, uid);
	}
	
    /**
     * Сохранить объект
     * @return объект KrnObject
     * @throws KrnException
     */
    public List<KrnObject> save(List<KrnObject> objjs) throws KrnException {
    	return operations.get().save(this, objjs, false, true);
    }

    public List<KrnObject> save(List<KrnObject> objjs, boolean executeTriggers, boolean logRecords) throws KrnException {
    	return operations.get().save(this, objjs, executeTriggers, logRecords);
    }

    /**
	 * Выполнить на стороне клиента
	 * @param _this класс
	 * @param methodName имя метода
	 * @param args список аргументов
	 * @param callStack стэк вызовов
	 * @return объект Object
	 * @throws Throwable
	 */
	public Object exec(KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
		return operations.get().exec(this, _this, methodName, args, callStack);
	}
	
	/**
	 * Выполнить на стороне сервера
	 * @param _this класс
	 * @param methodName имя метода
	 * @param args список аргументов
	 * @param callStack стэк вызовов
	 * @return объект Object
	 * @throws Throwable
	 */
    public Object sexec(KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
    	return operations.get().sexec(this, _this, methodName, args, callStack);
    }
    
    /**
     * Возвращает родительский класс
     * @return класс родитель
     */
    public KrnClass getParentClass() {
		return operations.get().getParentClass(this);
	}
    
	/**
	 * Возвращает класс указанного атрибута
	 * @param attrName имя атрибута
	 * @return класс, содержащий данный атрибут
	 */
	public KrnClass getAttrClass(String attrName) {
		return operations.get().getAttrClass(this, attrName);
	}
	
	/**
	 * Возвращает список атрибутов
	 * @return список атрибутов текущего класса
	 */
	public List<KrnAttribute> getAttributes() {
		return operations.get().getAttributes(this);
	}
	
	/**
	 * Возвращает атрибут по указанному имени
	 * @param name имя атрибута
	 * @return значение атрибута текущего класса
	 */
	public KrnAttribute getAttribute(String name) {
		return operations.get().getAttribute(this, name);
	}
	
	/**
	 * Обновить объекты текущего класса
	 * @throws KrnException
	 */
	public void refreshObjects() throws KrnException {
		operations.get().refreshObjects(this);
	}
	
	public void refreshObjects(List<KrnObject> objs) throws KrnException {
		operations.get().refreshObjects(objs);
	}

	@Override
	public String toString() {
		return "KrnClass [id=" + id + ", name=" + name + "]";
	}
	
	public boolean isVirtual() {
		return (modifier & 1) > 0; 
	}
	
	public boolean ignoreScheme() {
		return (modifier & 2) > 0; 
	}
}