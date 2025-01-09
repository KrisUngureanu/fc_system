package com.cifs.or2.kernel;

import java.util.List;
import java.util.Stack;

public interface KrnClassOperations {
	
	List<KrnObject> getObjects(KrnClass clazz);
	void refreshObjects(KrnClass clazz) throws KrnException;
	void refreshObjects(List<KrnObject> objs) throws KrnException;
	List<KrnObject> find(KrnClass clazz, String path, Object value, KrnObject lang, int compOper);
	KrnObject createObject(KrnClass clazz) throws KrnException;
	KrnObject createObject(KrnClass clazz, boolean save) throws KrnException;
	KrnObject createObject(KrnClass clazz, String uid) throws KrnException;
	Object exec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable;
	Object sexec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable;
    KrnClass getParentClass(KrnClass clazz);
    KrnClass getAttrClass(KrnClass clazz, String attrName);
    List<KrnAttribute> getAttributes(KrnClass clazz);
	KrnAttribute getAttribute(KrnClass clazz, String name);
	
	List<KrnObject> save(KrnClass clazz, List<KrnObject> objj, boolean executeTriggers, boolean logRecords) throws KrnException;

}
