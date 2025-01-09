/**
 * Cache' Java Class Generated for class kz.tamur.ods.cache.TObject on version Cache for Windows NT (Intel/P4) 5.0.20 (Build 6305U) Fri Sep 16 2005 12:06:03 EDT
 *
 * @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject</A>
**/

package kz.tamur.ods.cache;
import com.intersys.cache.*;
import com.intersys.objects.*;
import com.intersys.objects.reflect.*;
import com.intersys.classes.*;


public class TObject extends RegisteredObject  implements java.io.Serializable {

    private static String CACHE_CLASS_NAME = "kz.tamur.ods.cache.TObject";
    /**
           <p>NB: DO NOT USE IN APPLICATION(!!!).
           <br>Use <code>TObject._open</code> instead!</br></p>
           <p>
           Used to construct a Java object, corresponding to existing object
           in Cache database.
           </p>
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    */
    public TObject (CacheObject ref) throws CacheException {
        super (ref);
    }
    public TObject (Database db, String initstr) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME,initstr));
    }
    /**
       Creates a new instance of object "TObject" in Cache
       database and corresponding object of class
       <code>TObject</code>.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @throws CacheException in case of error.

              @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
     */
    public TObject (Database db) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME));
    }
    /**
       Returns class name of the class TObject as it is in
      Cache Database. Note, that this is a static method, so no
      object specific information can be returned. Use
      <code>getCacheClass().geName()</code> to get the class name
      for specific object.
       @return Cache class name as a <code>String</code>
      @see #getCacheClass()
      @see com.intersys.objects.reflect.CacheClass#getName()
     */
    public static String getCacheClassName( ) {
        return CACHE_CLASS_NAME;
    }

   /**
           Allows access metadata information about type of this object
           in Cache database. Also can be used for dynamic binding (accessing
           properties and calling methods without particular class known).

           @return <code>CacheClass</code> object for this object type.
   */
    public CacheClass getCacheClass( ) throws CacheException {
        return mInternal.getCacheClass();
    }

    /**
       Verifies that all fields from Cache class are exposed with
       accessor methods in Java class and that values for indexes in
       zObjVal are the same as in Cache. It does not return anything
       but it throws an exception in case of inconsistency.

       <p>But if there is any inconsistency in zObjVal indexes this is fatal and class can not work correctly and must be regenerated

       @param db Database used for connection. Note that if you are
       using multiple databases the class can be consistent with one
       and inconsistent with another.
       @throws InvalidClassException if any inconsistency is found.
       @throws CacheException if any error occurred during
       verification, e.g. communication error with Database.
       @see com.intersys.objects.InvalidPropertyException

     */
    public static void checkAllFieldsValid(Database db ) throws CacheException {
        checkAllFieldsValid(db, CACHE_CLASS_NAME, TObject.class);
    }

    /**
       Verifies that all fields from Cache class are exposed with
       accessor methods in Java class and that values for indexes in
       zObjVal are the same as in Cache. It does not return anything
       but it throws an exception in case of inconsistency.

       <p>But if there is any inconsistency in zObjVal indexes this is fatal and class can not work correctly and must be regenerated

       @param db Database used for connection. Note that if you are
       using multiple databases the class can be consistent with one
       and inconsistent with another.
       @throws InvalidClassException if any inconsistency is found.
       @throws CacheException if any error occurred during
       verification, e.g. communication error with Database.
       @see com.intersys.objects.InvalidPropertyException

     */
    public static void checkAllMethods(Database db ) throws CacheException {
        checkAllMethods(db, CACHE_CLASS_NAME, TObject.class);
    }
    private static int ii_clsId = 1;
    private static int jj_clsId = 0;
    private static int kk_clsId = 1;
    /**
       Verifies that indexes for property <code>clsId</code> in
       zObjVal are the same as in Cache. It does not return anything
       but it throws an exception in case of inconsistency.

       <p> Please note, that if there is any inconsistency in zObjVal
       indexes this is fatal and class can not work correctly and must
       be regenerated.

       @param db Database used for connection. Note that if you are
       using multiple databases the class can be consistent with one
       and inconsistent with another.
       @throws InvalidClassException if any inconsistency is found.
       @throws CacheException if any error occurred during
       verification, e.g. communication error with Database.
       @see #checkAllFieldsValid

     */
    public static void checkclsIdValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "clsId",ii_clsId, jj_clsId, kk_clsId);
    }
    /**
       Returns value of property <code>clsId</code>.
       <Description>
       @return current value of <code>clsId</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#clsId"> clsId</A>
    */
    public java.lang.Long getclsId() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_clsId,
                                                jj_clsId,
                                                Database.RET_PRIM,
                                                "clsId");
       return dh.getLong();
    }

    /**
       Sets new value for <code>clsId</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#clsId"> clsId</A>
    */
    public void setclsId(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_clsId, jj_clsId,kk_clsId, Database.RET_PRIM, "clsId", dh);
        return;
    }

    private static int ii_id = 2;
    private static int jj_id = 0;
    private static int kk_id = 2;
    /**
       Verifies that indexes for property <code>id</code> in
       zObjVal are the same as in Cache. It does not return anything
       but it throws an exception in case of inconsistency.

       <p> Please note, that if there is any inconsistency in zObjVal
       indexes this is fatal and class can not work correctly and must
       be regenerated.

       @param db Database used for connection. Note that if you are
       using multiple databases the class can be consistent with one
       and inconsistent with another.
       @throws InvalidClassException if any inconsistency is found.
       @throws CacheException if any error occurred during
       verification, e.g. communication error with Database.
       @see #checkAllFieldsValid

     */
    public static void checkidValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "id",ii_id, jj_id, kk_id);
    }
    /**
       Returns value of property <code>id</code>.
       <Description>
       @return current value of <code>id</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#id"> id</A>
    */
    public java.lang.Long getid() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_id,
                                                jj_id,
                                                Database.RET_PRIM,
                                                "id");
       return dh.getLong();
    }

    /**
       Sets new value for <code>id</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#id"> id</A>
    */
    public void setid(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_id, jj_id,kk_id, Database.RET_PRIM, "id", dh);
        return;
    }

    private static int ii_uid = 3;
    private static int jj_uid = 0;
    private static int kk_uid = 3;
    /**
       Verifies that indexes for property <code>uid</code> in
       zObjVal are the same as in Cache. It does not return anything
       but it throws an exception in case of inconsistency.

       <p> Please note, that if there is any inconsistency in zObjVal
       indexes this is fatal and class can not work correctly and must
       be regenerated.

       @param db Database used for connection. Note that if you are
       using multiple databases the class can be consistent with one
       and inconsistent with another.
       @throws InvalidClassException if any inconsistency is found.
       @throws CacheException if any error occurred during
       verification, e.g. communication error with Database.
       @see #checkAllFieldsValid

     */
    public static void checkuidValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "uid",ii_uid, jj_uid, kk_uid);
    }
    /**
       Returns value of property <code>uid</code>.
       <Description>
       @return current value of <code>uid</code> represented as
       <code>java.lang.String</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#uid"> uid</A>
    */
    public java.lang.String getuid() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_uid,
                                                jj_uid,
                                                Database.RET_PRIM,
                                                "uid");
       return dh.getString();
    }

    /**
       Sets new value for <code>uid</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.String</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#uid"> uid</A>
    */
    public void setuid(java.lang.String value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_uid, jj_uid,kk_uid, Database.RET_PRIM, "uid", dh);
        return;
    }

    /**
     <p>Runs method sys_ClassName in Cache.</p>
     <p>Description: Returns the object's class name. The <var>fullname</var> determines how the
class name is represented. If it is 1 then it returns the full class name
including any package qualifier. If it is 0 (the default) then it returns the
name of the class without the package, this is mainly for backward compatibility
with the pre-package behaviour of %ClassName.</p>
     @param db represented as Database
     @param fullname represented as java.lang.Boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%ClassName"> Method %ClassName</A>
    */
    public static java.lang.String sys_ClassName (Database db, java.lang.Boolean fullname) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(fullname);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%ClassName",args,Database.RET_PRIM);
        return res.getString();
    }
    /**
     <p>Runs method sys_Extends in Cache.</p>
     <p>Description: Returns true (1) if this class is inherited either via primary or secondary inheritance from 'isclass'.</p>
     @param db represented as Database
     @param isclass represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%Extends"> Method %Extends</A>
    */
    public static java.lang.Integer sys_Extends (Database db, java.lang.String isclass) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(isclass);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Extends",args,Database.RET_PRIM);
        return res.getInteger();
    }
    /**
     <p>Runs method sys_GetParameter in Cache.</p>
     <p>Description: This method returns the value of a parameter at runtime</p>
     @param db represented as Database
     default argument paramname set to ""
     @throws CacheException if any error occured while running the method.
     @see #sys_GetParameter(Database,java.lang.String)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%GetParameter"> Method %GetParameter</A>
    */
    public static java.lang.String sys_GetParameter (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%GetParameter",args,Database.RET_PRIM);
        return res.getString();
    }
    /**
     <p>Runs method sys_GetParameter in Cache.</p>
     <p>Description: This method returns the value of a parameter at runtime</p>
     @param db represented as Database
     @param paramname represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%GetParameter"> Method %GetParameter</A>
    */
    public static java.lang.String sys_GetParameter (Database db, java.lang.String paramname) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(paramname);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%GetParameter",args,Database.RET_PRIM);
        return res.getString();
    }
    /**
     <p>Runs method sys_IsA in Cache.</p>
     <p>Description: Returns true (1) if instances of this class are also instances of the isclass parameter.
That is 'isclass' is a primary superclass of this object.</p>
     @param db represented as Database
     @param isclass represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%IsA"> Method %IsA</A>
    */
    public static java.lang.Integer sys_IsA (Database db, java.lang.String isclass) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(isclass);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%IsA",args,Database.RET_PRIM);
        return res.getInteger();
    }
    /**
     <p>Runs method sys_PackageName in Cache.</p>
     <p>Description: Returns the object's package name.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TObject#%PackageName"> Method %PackageName</A>
    */
    public static java.lang.String sys_PackageName (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%PackageName",args,Database.RET_PRIM);
        return res.getString();
    }
}
