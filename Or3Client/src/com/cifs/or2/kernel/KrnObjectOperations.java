package com.cifs.or2.kernel;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public interface KrnObjectOperations {
    long getCurrentTransactionId();
    Object getAttr(KrnObject objj, String path, KrnObject lang, long trId) throws KrnException;
    void setAttr(KrnObject objj, String path, Object value, KrnObject lang, long trId) throws KrnException;
    void deleteAttr(KrnObject objj, String path, KrnObject lang, boolean cascade, long trId) throws KrnException;
    void deleteAttr(KrnObject objj, String path, KrnObject lang, int index, long trId) throws Exception;
    void deleteAttr(KrnObject objj, String path, Object value, long trId) throws Exception;
    void delete(KrnObject objj, boolean delRefs, long trId) throws KrnException;
    boolean like(KrnObject objj, KrnObject obj, KrnObject lang, long trId) throws KrnException;
    boolean isLock(KrnObject objj) throws KrnException;
    boolean isLock(KrnObject objj, KrnObject service) throws KrnException;
    void lock(KrnObject objj) throws KrnException;
    void lock(KrnObject objj, KrnObject locker) throws KrnException;
    boolean isLocked(KrnObject objj, KrnObject locker) throws KrnException;
    List<KrnObject> getConflictLocker(KrnObject objj);
    KrnObject getLocker(KrnObject objj, KrnObject service) throws KrnException;
    String getLocker(KrnObject objj) throws KrnException;
    List<KrnObject> getLockers(KrnObject objj) throws KrnException;
    void unlock(KrnObject objj) throws KrnException;
    List<String> getProcessLocker(KrnObject objj); // TODO выяснить что это такое и избавиться
    Object exec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable;
    boolean isDeleted(KrnObject objj) throws KrnException;
	KrnClass getCls(KrnObject objj) throws Exception;
	KrnClass getClassById(long classId) throws KrnException;
    Object sexec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable;
    boolean beforeSave(KrnObject objj) throws KrnException;
    KrnObject save(KrnObject objj) throws KrnException;
    long filterToAttr(KrnObject objj,KrnObject fobj,String path,Map<String,Object> params,long trId) throws KrnException;
    boolean isDel(KrnObject obj, long trId) throws KrnException;
}
