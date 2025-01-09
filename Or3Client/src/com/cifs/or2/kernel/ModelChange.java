package com.cifs.or2.kernel;

import java.io.Serializable;

public class ModelChange implements Serializable {
	
    public static final int ENTITY_TYPE_CLASS     = 0;
    public static final int ENTITY_TYPE_ATTRIBUTE = 1;
    public static final int ENTITY_TYPE_METHOD = 2;
    public static final int ENTITY_TYPE_INDEX = 3;
    public static final int ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE = 4;
    public static final int ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE = 5;
    public static final int ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE = 6;
    public static final int ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE = 7;
    public static final int ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE = 8;
    public static final int ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE = 9;
    public static final int ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE = 10;
    public static final int ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE = 11;

    public static final int ACTION_CREATE = 0;
    public static final int ACTION_MODIFY = 1;
    public static final int ACTION_DELETE = 2;

	public final int changeType;
	public final int entityType;
	public final Object entity;
	public final String entityComment;
	public final byte[] entityData;
	
	public ModelChange(int changeType, int entityType, Object entity, String entityComment, byte[] entityData) {
		super();
		this.changeType = changeType;
		this.entityType = entityType;
		this.entity = entity;
		this.entityComment = entityComment;
		this.entityData = entityData;
	}
}