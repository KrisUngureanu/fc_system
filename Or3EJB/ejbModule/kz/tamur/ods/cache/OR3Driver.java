/**
 * Cache' Java Class Generated for class kz.tamur.ods.cache.OR3Driver on version Cache for Windows NT (Intel/P4) 5.0.20 (Build 6305U) Fri Sep 16 2005 12:06:03 EDT
 *
 * @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver</A>
**/

package kz.tamur.ods.cache;
import com.intersys.cache.*;
import com.intersys.objects.*;
import com.intersys.classes.*;


public abstract class OR3Driver  implements java.io.Serializable {

    private static String CACHE_CLASS_NAME = "kz.tamur.ods.cache.OR3Driver";
    /**
     <p>Runs method changeAttribute in Cache.</p>
     @param db represented as Database
     @param attrId represented as java.lang.Long
     @param name represented as java.lang.String
     @param flags represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#changeAttribute"> Method changeAttribute</A>
    */
    public static kz.tamur.ods.cache.TAttr changeAttribute (Database db, java.lang.Long attrId, java.lang.String name, java.lang.Long flags) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(attrId);
        args[1] = new Dataholder(name);
        args[2] = new Dataholder(flags);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"changeAttribute",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TAttr)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method changeClass in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @param name represented as java.lang.String
     @param isRepl represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#changeClass"> Method changeClass</A>
    */
    public static kz.tamur.ods.cache.TClass changeClass (Database db, java.lang.Long classId, java.lang.String name, java.lang.Boolean isRepl) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(name);
        args[2] = new Dataholder(isRepl);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"changeClass",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TClass)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method compileClass in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#compileClass"> Method compileClass</A>
    */
    public static void compileClass (Database db, java.lang.Long classId) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(classId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"compileClass",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method createAttribute in Cache.</p>
     @param db represented as Database
     @param id represented as java.lang.Long
     @param classId represented as java.lang.Long
     @param name represented as java.lang.String
     @param typeId represented as java.lang.Long
     @param colType represented as java.lang.Integer
     @param isUnique represented as java.lang.Boolean
     @param isIndexed represented as java.lang.Boolean
     @param isMultilingual represented as java.lang.Boolean
     @param size represented as java.lang.Integer
     @param flags represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#createAttribute"> Method createAttribute</A>
    */
    public static kz.tamur.ods.cache.TAttr createAttribute (Database db, java.lang.Long id, java.lang.Long classId, java.lang.String name, java.lang.Long typeId, java.lang.Integer colType, java.lang.Boolean isUnique, java.lang.Boolean isIndexed, java.lang.Boolean isMultilingual, java.lang.Integer size, java.lang.Long flags) throws CacheException {
        Dataholder[] args = new Dataholder[10];
        args[0] = new Dataholder(id);
        args[1] = new Dataholder(classId);
        args[2] = new Dataholder(name);
        args[3] = new Dataholder(typeId);
        args[4] = new Dataholder(colType);
        args[5] = new Dataholder(isUnique);
        args[6] = new Dataholder(isIndexed);
        args[7] = new Dataholder(isMultilingual);
        args[8] = new Dataholder(size);
        args[9] = new Dataholder(flags);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"createAttribute",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TAttr)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method createClass in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @param parentId represented as java.lang.Long
     @param isRepl represented as java.lang.Boolean
     @param id represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#createClass"> Method createClass</A>
    */
    public static kz.tamur.ods.cache.TClass createClass (Database db, java.lang.String name, java.lang.Long parentId, java.lang.Boolean isRepl, java.lang.Long id) throws CacheException {
        Dataholder[] args = new Dataholder[4];
        args[0] = new Dataholder(name);
        args[1] = new Dataholder(parentId);
        args[2] = new Dataholder(isRepl);
        args[3] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"createClass",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TClass)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method createLongTransaction in Cache.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#createLongTransaction"> Method createLongTransaction</A>
    */
    public static java.lang.Long createLongTransaction (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"createLongTransaction",args,Database.RET_PRIM);
        return res.getLong();
    }
    /**
     <p>Runs method createObject in Cache.</p>
     @param db represented as Database
     @param typeId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param id represented as java.lang.Long
     @param uid represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#createObject"> Method createObject</A>
    */
    public static kz.tamur.ods.cache.TObject createObject (Database db, java.lang.Long typeId, java.lang.Long trId, java.lang.Long id, java.lang.String uid) throws CacheException {
        Dataholder[] args = new Dataholder[4];
        args[0] = new Dataholder(typeId);
        args[1] = new Dataholder(trId);
        args[2] = new Dataholder(id);
        args[3] = new Dataholder(uid);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"createObject",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TObject)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method deleteAttribute in Cache.</p>
     @param db represented as Database
     @param attrId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#deleteAttribute"> Method deleteAttribute</A>
    */
    public static void deleteAttribute (Database db, java.lang.Long attrId) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(attrId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"deleteAttribute",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method deleteClass in Cache.</p>
     @param db represented as Database
     @param id represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#deleteClass"> Method deleteClass</A>
    */
    public static void deleteClass (Database db, java.lang.Long id) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"deleteClass",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method getAttributeById in Cache.</p>
     @param db represented as Database
     @param id represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getAttributeById"> Method getAttributeById</A>
    */
    public static kz.tamur.ods.cache.TAttr getAttributeById (Database db, java.lang.Long id) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getAttributeById",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TAttr)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getAttributeByName in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @param name represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getAttributeByName"> Method getAttributeByName</A>
    */
    public static kz.tamur.ods.cache.TAttr getAttributeByName (Database db, java.lang.Long classId, java.lang.String name) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(name);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getAttributeByName",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TAttr)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getAttributesByClassId in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @param inherited represented as java.lang.Boolean
     @param _res represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getAttributesByClassId"> Method getAttributesByClassId</A>
    */
    public static void getAttributesByClassId (Database db, java.lang.Long classId, java.lang.Boolean inherited, com.intersys.objects.SList _res) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(inherited);
        args[2] = new Dataholder(_res);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getAttributesByClassId",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method getAttributesByTypeId in Cache.</p>
     @param db represented as Database
     @param typeId represented as java.lang.Long
     @param inherited represented as java.lang.Boolean
     @param _res represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getAttributesByTypeId"> Method getAttributesByTypeId</A>
    */
    public static void getAttributesByTypeId (Database db, java.lang.Long typeId, java.lang.Boolean inherited, com.intersys.objects.SList _res) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(typeId);
        args[1] = new Dataholder(inherited);
        args[2] = new Dataholder(_res);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getAttributesByTypeId",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method getClassById in Cache.</p>
     @param db represented as Database
     @param id represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getClassById"> Method getClassById</A>
    */
    public static kz.tamur.ods.cache.TClass getClassById (Database db, java.lang.Long id) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getClassById",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TClass)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getClassByName in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getClassByName"> Method getClassByName</A>
    */
    public static kz.tamur.ods.cache.TClass getClassByName (Database db, java.lang.String name) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(name);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getClassByName",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TClass)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getClasses in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @param subClasses represented as java.lang.Boolean
     @param recursive represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getClasses"> Method getClasses</A>
    */
    public static ListOfObjects getClasses (Database db, java.lang.Long classId, java.lang.Boolean subClasses, java.lang.Boolean recursive) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(subClasses);
        args[2] = new Dataholder(recursive);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getClasses",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method get_Id in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getId"> Method getId</A>
    */
    public static java.lang.Long get_Id (Database db, java.lang.String name) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(name);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getId",args,Database.RET_PRIM);
        return res.getLong();
    }
    /**
     <p>Runs method getLangs in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getLangs"> Method getLangs</A>
    */
    public static ListOfDataTypes getLangs (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Long trId) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(trId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getLangs",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfDataTypes)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getObjects in Cache.</p>
     @param db represented as Database
     @param classId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getObjects"> Method getObjects</A>
    */
    public static ListOfObjects getObjects (Database db, java.lang.Long classId, java.lang.Long trId) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(trId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getObjects",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getObjectsByAttribute in Cache.</p>
     @param db represented as Database
     @param attrId represented as java.lang.Long
     @param langId represented as java.lang.Long
     @param op represented as java.lang.Integer
     @param value represented as java.lang.String
     @param trId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getObjectsByAttribute"> Method getObjectsByAttribute</A>
    */
    public static ListOfObjects getObjectsByAttribute (Database db, java.lang.Long attrId, java.lang.Long langId, java.lang.Integer op, java.lang.String value, java.lang.Long trId) throws CacheException {
        Dataholder[] args = new Dataholder[5];
        args[0] = new Dataholder(attrId);
        args[1] = new Dataholder(langId);
        args[2] = new Dataholder(op);
        args[3] = new Dataholder(value);
        args[4] = new Dataholder(trId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getObjectsByAttribute",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getObjectsByIds in Cache.</p>
     @param db represented as Database
     @param ids represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getObjectsByIds"> Method getObjectsByIds</A>
    */
    public static ListOfObjects getObjectsByIds (Database db, com.intersys.objects.SList ids) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(ids);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getObjectsByIds",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getObjectsByUids in Cache.</p>
     @param db represented as Database
     @param uids represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getObjectsByUids"> Method getObjectsByUids</A>
    */
    public static ListOfObjects getObjectsByUids (Database db, com.intersys.objects.SList uids) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(uids);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getObjectsByUids",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getRevAttributes in Cache.</p>
     @param db represented as Database
     @param attrId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getRevAttributes"> Method getRevAttributes</A>
    */
    public static ListOfObjects getRevAttributes (Database db, java.lang.Long attrId) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(attrId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getRevAttributes",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method getValues in Cache.</p>
     @param db represented as Database
     @param objIds represented as com.intersys.objects.SList
     @param attrId represented as java.lang.Long
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#getValues"> Method getValues</A>
    */
    public static ListOfObjects getValues (Database db, com.intersys.objects.SList objIds, java.lang.Long attrId, java.lang.Long langId, java.lang.Long trId) throws CacheException {
        Dataholder[] args = new Dataholder[4];
        args[0] = new Dataholder(objIds);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(langId);
        args[3] = new Dataholder(trId);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getValues",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfObjects)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method install in Cache.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#install"> Method install</A>
    */
    public static void install (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"install",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setBlob in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param index represented as java.lang.Integer
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param value represented as BinaryStream
     @param ins represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setBlob"> Method setBlob</A>
    */
    public static void setBlob (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Integer index, java.lang.Long langId, java.lang.Long trId, BinaryStream value, java.lang.Boolean ins) throws CacheException {
        Dataholder[] args = new Dataholder[7];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(index);
        args[3] = new Dataholder(langId);
        args[4] = new Dataholder(trId);
        args[5] = new Dataholder(value);
        args[6] = new Dataholder(ins);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setBlob",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setDate in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param index represented as java.lang.Integer
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param value represented as java.sql.Date
     @param ins represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setDate"> Method setDate</A>
    */
    public static void setDate (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Integer index, java.lang.Long langId, java.lang.Long trId, java.sql.Date value, java.lang.Boolean ins) throws CacheException {
        Dataholder[] args = new Dataholder[7];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(index);
        args[3] = new Dataholder(langId);
        args[4] = new Dataholder(trId);
        args[5] = new Dataholder(value);
        args[6] = new Dataholder(ins);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setDate",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setId in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @param value represented as java.lang.Long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setId"> Method setId</A>
    */
    public static void setId (Database db, java.lang.String name, java.lang.Long value) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(name);
        args[1] = new Dataholder(value);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setId",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setRevAttributes in Cache.</p>
     @param db represented as Database
     @param attrId represented as java.lang.Long
     @param revAttrIds represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setRevAttributes"> Method setRevAttributes</A>
    */
    public static void setRevAttributes (Database db, java.lang.Long attrId, com.intersys.objects.SList revAttrIds) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(attrId);
        args[1] = new Dataholder(revAttrIds);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setRevAttributes",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method setTime in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param index represented as java.lang.Integer
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param value represented as java.sql.Time
     @param ins represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setTime"> Method setTime</A>
    */
    public static void setTime (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Integer index, java.lang.Long langId, java.lang.Long trId, java.sql.Time value, java.lang.Boolean ins) throws CacheException {
        Dataholder[] args = new Dataholder[7];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(index);
        args[3] = new Dataholder(langId);
        args[4] = new Dataholder(trId);
        args[5] = new Dataholder(value);
        args[6] = new Dataholder(ins);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setTime",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setValue in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param index represented as java.lang.Integer
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param value represented as java.lang.String
     @param ins represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setValue"> Method setValue</A>
    */
    public static void setValue (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Integer index, java.lang.Long langId, java.lang.Long trId, java.lang.String value, java.lang.Boolean ins) throws CacheException {
        Dataholder[] args = new Dataholder[7];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(index);
        args[3] = new Dataholder(langId);
        args[4] = new Dataholder(trId);
        args[5] = new Dataholder(value);
        args[6] = new Dataholder(ins);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setValue",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method setValue2 in Cache.</p>
     @param db represented as Database
     @param objId represented as java.lang.Long
     @param attrId represented as java.lang.Long
     @param index represented as java.lang.Integer
     @param langId represented as java.lang.Long
     @param trId represented as java.lang.Long
     @param value represented as java.lang.String
     @param ins represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#setValue2"> Method setValue2</A>
    */
    public static void setValue2 (Database db, java.lang.Long objId, java.lang.Long attrId, java.lang.Integer index, java.lang.Long langId, java.lang.Long trId, java.lang.String value, java.lang.Boolean ins) throws CacheException {
        Dataholder[] args = new Dataholder[7];
        args[0] = new Dataholder(objId);
        args[1] = new Dataholder(attrId);
        args[2] = new Dataholder(index);
        args[3] = new Dataholder(langId);
        args[4] = new Dataholder(trId);
        args[5] = new Dataholder(value);
        args[6] = new Dataholder(ins);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setValue2",args,Database.RET_NONE);
        return;
    }
    /**
     <p>Runs method test in Cache.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.OR3Driver#test"> Method test</A>
    */
    public static void test (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"test",args,Database.RET_NONE);
        return;
    }
}
