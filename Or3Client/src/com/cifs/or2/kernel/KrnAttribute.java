package com.cifs.or2.kernel;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public final class KrnAttribute implements Serializable {
	
	public static final int AGGREGATE = 0x04;
	public static final int MANDATORY = 0x08;
	public static final int FULLTEXT = 0x10;
	public static final int GROUP = 0x20;
	
	transient private static ThreadLocal<KrnAttributeOperations> operations = new ThreadLocal<KrnAttributeOperations>();
	
	public String uid = null;
	public long id = 0L;
	public String name = null;
	public long classId = 0L;
	public long typeClassId = 0L;
	public int collectionType = (int) 0;
	public boolean isUnique = false;
	public boolean isMultilingual = false;
	public boolean isIndexed = false;
	public int size = (int) 0;
	public long flags = 0L;
	public boolean isRepl = false;
	public long rAttrId = 0L;
	public long sAttrId = 0L;
	public boolean sDesc = false;
	public String tname = null;
	public byte[] beforeEventExpr;
	public byte[] afterEventExpr;
	public byte[] beforeDelEventExpr;
	public byte[] afterDelEventExpr;
	public int beforeEventTr;
	public int afterEventTr;
	public int beforeDelEventTr;
	public int afterDelEventTr;
	public int accessModifierType = (int) 0;
	public boolean isEncrypt = false;

	public KrnAttribute() {}

	/***
	 * Конструктор копирования
	 * @param attr оригинал
	 */
	public KrnAttribute(KrnAttribute attr) {
		uid = attr.uid;
		id = attr.id;
		name = attr.name;
		classId = attr.classId;
		typeClassId = attr.typeClassId;
		collectionType = attr.collectionType;
		isUnique = attr.isUnique;
		isMultilingual = attr.isMultilingual;
		isIndexed = attr.isIndexed;
		size = attr.size;
		flags = attr.flags;
		isRepl = attr.isRepl;
		rAttrId = attr.rAttrId;
		sAttrId = attr.sAttrId;
		sDesc = attr.sDesc;
		tname = attr.tname;
		beforeEventExpr = attr.beforeEventExpr;
		afterEventExpr = attr.afterEventExpr;
		beforeDelEventExpr = attr.beforeDelEventExpr;
		afterDelEventExpr = attr.afterDelEventExpr;
		beforeEventTr = attr.beforeEventTr;
		afterEventTr = attr.afterEventTr;
		beforeDelEventTr = attr.beforeDelEventTr;
		afterDelEventTr = attr.afterDelEventTr;
		accessModifierType = attr.accessModifierType;
		isEncrypt = attr.isEncrypt;
	}
	
	public KrnAttribute(String uid, long id, String name, long classId,
			long typeClassId, int collectionType, boolean isUnique,
			boolean isMultilingual, boolean isIndexed, int size,
			long flags, boolean isRepl, long rAttrId, long sAttrId,
			boolean sDesc, String tname, byte[] beforeEventExpression, byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression, byte[] afterDeleteEventExpression,
			int beforeEventTr, int afterEventTr, int beforeDelEventTr, int afterDelEventTr, int accessModifierType) {
		this(uid, id, name, classId, typeClassId, collectionType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, rAttrId, sAttrId, sDesc, tname, beforeEventExpression,
			 afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr, afterEventTr, beforeDelEventTr, afterDelEventTr, accessModifierType, false);
	}

	public KrnAttribute(String uid, long id, String name, long classId,
			long typeClassId, int collectionType, boolean isUnique,
			boolean isMultilingual, boolean isIndexed, int size,
			long flags, boolean isRepl, long rAttrId, long sAttrId,
			boolean sDesc, String tname, byte[] beforeEventExpression, byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression, byte[] afterDeleteEventExpression,
			int beforeEventTr, int afterEventTr, int beforeDelEventTr, int afterDelEventTr, int accessModifierType, boolean isEncrypt) {
		this.uid = uid;
		this.id = id;
		this.name = name;
		this.classId = classId;
		this.typeClassId = typeClassId;
		this.collectionType = collectionType;
		this.isUnique = isUnique;
		this.isMultilingual = isMultilingual;
		this.isIndexed = isIndexed;
		this.size = size;
		this.flags = flags;
		this.isRepl = isRepl;
		this.rAttrId = rAttrId;
		this.sAttrId = sAttrId;
		this.sDesc = sDesc;
		this.tname = tname;
		this.beforeEventExpr = beforeEventExpression;
		this.afterEventExpr = afterEventExpression;
		this.beforeDelEventExpr = beforeDeleteEventExpression;
		this.afterDelEventExpr = afterDeleteEventExpression;
		this.beforeEventTr = beforeEventTr;
		this.afterEventTr = afterEventTr;
		this.beforeDelEventTr = beforeDelEventTr;
		this.afterDelEventTr = afterDelEventTr;
		this.accessModifierType = accessModifierType;
		this.isEncrypt = isEncrypt;
	}
	
	/**
	 * Обязательность
	 * @return true or false
	 */
	public boolean isMandatory() {
		return (flags & MANDATORY) > 0;
	}
	/**
	 * Установить обязательсноть
	 * @param mandatory true or false
	 */
	public void setMandatory(boolean mandatory) {
		if (mandatory != isMandatory())
			flags ^= MANDATORY;
	}
	/**
	 * Агрегация
	 * @return true or false
	 */
	public boolean isAggregate() {
		return (flags & AGGREGATE) > 0;
	}
	/**
	 * Установить агрегацию 
	 * @param aggregate true or false
	 */
	public void setAggregate(boolean aggregate) {
		if (aggregate != isAggregate())
			flags ^= AGGREGATE;
	}

	public boolean isFullText() {
		return (flags & FULLTEXT) > 0;
	}

	public void setFullText(boolean fullText) {
		if (fullText != isFullText())
			flags ^= FULLTEXT;
	}
	
	public boolean isGroup() {
		return (flags & GROUP) > 0;
	}

	public void setGroup(boolean multipl_update) {
		if (multipl_update != isGroup())
			flags ^= GROUP;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof KrnAttribute)
			return id == ((KrnAttribute)obj).id;
		return false;
	}
	/**
	 * Установить операции
	 * @param operations операции
	 */
	public static void setOperations(KrnAttributeOperations operations) {
		KrnAttribute.operations.set(operations);
	}
	/**
	 * Возвращает операции
	 * @return операции
	 */
	public static KrnAttributeOperations getOperations() {
		return KrnAttribute.operations.get();
	}
	/**
	 * Удалить операции
	 */
	public static void removeOperations() {
		KrnAttribute.operations.remove();
	}

	public KrnClass getCls() {
		return operations.get().getCls(this);
	}
	
	public KrnClass getType() {
		return operations.get().getType(this);
	}
	/**
	 * Возвращает список обратных атрибутов
	 * @return список обратных атрибутов
	 */
	public List<KrnAttribute> getRevAttributes() {
		return operations.get().getRevAttributes(this);
	}
}