/**
 * Cache' Java Class Generated for class kz.tamur.ods.cache.TValue on version Cache for Windows NT (Intel/P4) 5.0.20 (Build 6305U) Fri Sep 16 2005 12:06:03 EDT
 *
 * @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue</A>
**/

package kz.tamur.ods.cache;
import com.intersys.cache.*;
import com.intersys.objects.*;
import com.intersys.objects.reflect.*;
import com.intersys.classes.*;


public class TValue extends RegisteredObject  implements java.io.Serializable {

    private static String CACHE_CLASS_NAME = "kz.tamur.ods.cache.TValue";
    /**
           <p>NB: DO NOT USE IN APPLICATION(!!!).
           <br>Use <code>TValue._open</code> instead!</br></p>
           <p>
           Used to construct a Java object, corresponding to existing object
           in Cache database.
           </p>
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    */
    public TValue (CacheObject ref) throws CacheException {
        super (ref);
    }
    public TValue (Database db, String initstr) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME,initstr));
    }
    /**
       Creates a new instance of object "TValue" in Cache
       database and corresponding object of class
       <code>TValue</code>.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @throws CacheException in case of error.

              @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
     */
    public TValue (Database db) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME));
    }
    /**
       Returns class name of the class TValue as it is in
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
        checkAllFieldsValid(db, CACHE_CLASS_NAME, TValue.class);
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
        checkAllMethods(db, CACHE_CLASS_NAME, TValue.class);
    }
    private static int ii_binaryValue = 2;
    private static int jj_binaryValue = 0;
    private static int kk_binaryValue = 1;
    /**
       Verifies that indexes for property <code>binaryValue</code> in
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
    public static void checkbinaryValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "binaryValue",ii_binaryValue, jj_binaryValue, kk_binaryValue);
    }
    /**
       Returns value of property <code>binaryValue</code>.
       <Description>
       @return current value of <code>binaryValue</code> represented as
       <code>BinaryStream</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#binaryValue"> binaryValue</A>
    */
    public BinaryStream getbinaryValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_binaryValue,
                                                jj_binaryValue,
                                                Database.RET_OBJECT,
                                                "binaryValue");
        CacheObject cobj = dh.getCacheObject();
        if (cobj == null)
            return null;
        return (BinaryStream)(cobj.newJavaInstance());
    }

    /**
       Sets new value for <code>binaryValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>BinaryStream</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#binaryValue"> binaryValue</A>
    */
    public void setbinaryValue(BinaryStream value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_binaryValue, jj_binaryValue,kk_binaryValue, Database.RET_OBJECT, "binaryValue", dh);
        return;
    }

    private static int ii_boolValue = 3;
    private static int jj_boolValue = 0;
    private static int kk_boolValue = 2;
    /**
       Verifies that indexes for property <code>boolValue</code> in
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
    public static void checkboolValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "boolValue",ii_boolValue, jj_boolValue, kk_boolValue);
    }
    /**
       Returns value of property <code>boolValue</code>.
       <Description>
       @return current value of <code>boolValue</code> represented as
       <code>java.lang.Boolean</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#boolValue"> boolValue</A>
    */
    public java.lang.Boolean getboolValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_boolValue,
                                                jj_boolValue,
                                                Database.RET_PRIM,
                                                "boolValue");
       return dh.getBoolean();
    }

    /**
       Sets new value for <code>boolValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Boolean</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#boolValue"> boolValue</A>
    */
    public void setboolValue(java.lang.Boolean value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_boolValue, jj_boolValue,kk_boolValue, Database.RET_PRIM, "boolValue", dh);
        return;
    }

    private static int ii_dateValue = 4;
    private static int jj_dateValue = 0;
    private static int kk_dateValue = 3;
    /**
       Verifies that indexes for property <code>dateValue</code> in
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
    public static void checkdateValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "dateValue",ii_dateValue, jj_dateValue, kk_dateValue);
    }
    /**
       Returns value of property <code>dateValue</code>.
       <Description>
       @return current value of <code>dateValue</code> represented as
       <code>java.sql.Date</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#dateValue"> dateValue</A>
    */
    public java.sql.Date getdateValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_dateValue,
                                                jj_dateValue,
                                                Database.RET_PRIM,
                                                "dateValue");
       return dh.getDate();
    }

    /**
       Sets new value for <code>dateValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.sql.Date</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#dateValue"> dateValue</A>
    */
    public void setdateValue(java.sql.Date value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_dateValue, jj_dateValue,kk_dateValue, Database.RET_PRIM, "dateValue", dh);
        return;
    }

    private static int ii_floatValue = 5;
    private static int jj_floatValue = 0;
    private static int kk_floatValue = 4;
    /**
       Verifies that indexes for property <code>floatValue</code> in
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
    public static void checkfloatValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "floatValue",ii_floatValue, jj_floatValue, kk_floatValue);
    }
    /**
       Returns value of property <code>floatValue</code>.
       <Description>
       @return current value of <code>floatValue</code> represented as
       <code>java.lang.Double</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#floatValue"> floatValue</A>
    */
    public java.lang.Double getfloatValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_floatValue,
                                                jj_floatValue,
                                                Database.RET_PRIM,
                                                "floatValue");
       return dh.getDouble();
    }

    /**
       Sets new value for <code>floatValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Double</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#floatValue"> floatValue</A>
    */
    public void setfloatValue(java.lang.Double value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_floatValue, jj_floatValue,kk_floatValue, Database.RET_PRIM, "floatValue", dh);
        return;
    }

    private static int ii_index = 6;
    private static int jj_index = 0;
    private static int kk_index = 5;
    /**
       Verifies that indexes for property <code>index</code> in
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
    public static void checkindexValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "index",ii_index, jj_index, kk_index);
    }
    /**
       Returns value of property <code>index</code>.
       <Description>
       @return current value of <code>index</code> represented as
       <code>java.lang.Integer</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#index"> index</A>
    */
    public java.lang.Integer getindex() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_index,
                                                jj_index,
                                                Database.RET_PRIM,
                                                "index");
       return dh.getInteger();
    }

    /**
       Sets new value for <code>index</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Integer</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#index"> index</A>
    */
    public void setindex(java.lang.Integer value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_index, jj_index,kk_index, Database.RET_PRIM, "index", dh);
        return;
    }

    private static int ii_langId = 7;
    private static int jj_langId = 0;
    private static int kk_langId = 6;
    /**
       Verifies that indexes for property <code>langId</code> in
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
    public static void checklangIdValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "langId",ii_langId, jj_langId, kk_langId);
    }
    /**
       Returns value of property <code>langId</code>.
       <Description>
       @return current value of <code>langId</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#langId"> langId</A>
    */
    public java.lang.Long getlangId() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_langId,
                                                jj_langId,
                                                Database.RET_PRIM,
                                                "langId");
       return dh.getLong();
    }

    /**
       Sets new value for <code>langId</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#langId"> langId</A>
    */
    public void setlangId(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_langId, jj_langId,kk_langId, Database.RET_PRIM, "langId", dh);
        return;
    }

    private static int ii_longValue = 8;
    private static int jj_longValue = 0;
    private static int kk_longValue = 7;
    /**
       Verifies that indexes for property <code>longValue</code> in
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
    public static void checklongValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "longValue",ii_longValue, jj_longValue, kk_longValue);
    }
    /**
       Returns value of property <code>longValue</code>.
       <Description>
       @return current value of <code>longValue</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#longValue"> longValue</A>
    */
    public java.lang.Long getlongValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_longValue,
                                                jj_longValue,
                                                Database.RET_PRIM,
                                                "longValue");
       return dh.getLong();
    }

    /**
       Sets new value for <code>longValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#longValue"> longValue</A>
    */
    public void setlongValue(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_longValue, jj_longValue,kk_longValue, Database.RET_PRIM, "longValue", dh);
        return;
    }

    private static int ii_objId = 9;
    private static int jj_objId = 0;
    private static int kk_objId = 8;
    /**
       Verifies that indexes for property <code>objId</code> in
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
    public static void checkobjIdValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "objId",ii_objId, jj_objId, kk_objId);
    }
    /**
       Returns value of property <code>objId</code>.
       <Description>
       @return current value of <code>objId</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#objId"> objId</A>
    */
    public java.lang.Long getobjId() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_objId,
                                                jj_objId,
                                                Database.RET_PRIM,
                                                "objId");
       return dh.getLong();
    }

    /**
       Sets new value for <code>objId</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#objId"> objId</A>
    */
    public void setobjId(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_objId, jj_objId,kk_objId, Database.RET_PRIM, "objId", dh);
        return;
    }

    private static int ii_objValue = 11;
    private static int jj_objValue = 0;
    private static int kk_objValue = 9;
    /**
       Verifies that indexes for property <code>objValue</code> in
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
    public static void checkobjValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "objValue",ii_objValue, jj_objValue, kk_objValue);
    }
    /**
       Returns value of property <code>objValue</code>.
       <Description>
       @return current value of <code>objValue</code> represented as
       <code>kz.tamur.ods.cache.TObject</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#objValue"> objValue</A>
    */
    public kz.tamur.ods.cache.TObject getobjValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_objValue,
                                                jj_objValue,
                                                Database.RET_OBJECT,
                                                "objValue");
        CacheObject cobj = dh.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TObject)(cobj.newJavaInstance());
    }

    /**
       Sets new value for <code>objValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>kz.tamur.ods.cache.TObject</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#objValue"> objValue</A>
    */
    public void setobjValue(kz.tamur.ods.cache.TObject value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_objValue, jj_objValue,kk_objValue, Database.RET_OBJECT, "objValue", dh);
        return;
    }

    private static int ii_stringValue = 12;
    private static int jj_stringValue = 0;
    private static int kk_stringValue = 10;
    /**
       Verifies that indexes for property <code>stringValue</code> in
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
    public static void checkstringValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "stringValue",ii_stringValue, jj_stringValue, kk_stringValue);
    }
    /**
       Returns value of property <code>stringValue</code>.
       <Description>
       @return current value of <code>stringValue</code> represented as
       <code>java.lang.String</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#stringValue"> stringValue</A>
    */
    public java.lang.String getstringValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_stringValue,
                                                jj_stringValue,
                                                Database.RET_PRIM,
                                                "stringValue");
       return dh.getString();
    }

    /**
       Sets new value for <code>stringValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.String</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#stringValue"> stringValue</A>
    */
    public void setstringValue(java.lang.String value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_stringValue, jj_stringValue,kk_stringValue, Database.RET_PRIM, "stringValue", dh);
        return;
    }

    private static int ii_timeValue = 13;
    private static int jj_timeValue = 0;
    private static int kk_timeValue = 11;
    /**
       Verifies that indexes for property <code>timeValue</code> in
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
    public static void checktimeValueValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "timeValue",ii_timeValue, jj_timeValue, kk_timeValue);
    }
    /**
       Returns value of property <code>timeValue</code>.
       <Description>
       @return current value of <code>timeValue</code> represented as
       <code>java.sql.Time</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#timeValue"> timeValue</A>
    */
    public java.sql.Time gettimeValue() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_timeValue,
                                                jj_timeValue,
                                                Database.RET_PRIM,
                                                "timeValue");
       return dh.getTime();
    }

    /**
       Sets new value for <code>timeValue</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.sql.Time</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#timeValue"> timeValue</A>
    */
    public void settimeValue(java.sql.Time value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_timeValue, jj_timeValue,kk_timeValue, Database.RET_PRIM, "timeValue", dh);
        return;
    }

    private static int ii_trId = 14;
    private static int jj_trId = 0;
    private static int kk_trId = 12;
    /**
       Verifies that indexes for property <code>trId</code> in
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
    public static void checktrIdValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "trId",ii_trId, jj_trId, kk_trId);
    }
    /**
       Returns value of property <code>trId</code>.
       <Description>
       @return current value of <code>trId</code> represented as
       <code>java.lang.Long</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#trId"> trId</A>
    */
    public java.lang.Long gettrId() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_trId,
                                                jj_trId,
                                                Database.RET_PRIM,
                                                "trId");
       return dh.getLong();
    }

    /**
       Sets new value for <code>trId</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Long</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#trId"> trId</A>
    */
    public void settrId(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_trId, jj_trId,kk_trId, Database.RET_PRIM, "trId", dh);
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%ClassName"> Method %ClassName</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%Extends"> Method %Extends</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%GetParameter"> Method %GetParameter</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%GetParameter"> Method %GetParameter</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%IsA"> Method %IsA</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TValue#%PackageName"> Method %PackageName</A>
    */
    public static java.lang.String sys_PackageName (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%PackageName",args,Database.RET_PRIM);
        return res.getString();
    }
}
