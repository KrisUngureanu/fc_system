/**
 * Cache' Java Class Generated for class kz.tamur.ods.cache.TClass on version Cache for Windows NT (Intel/P4) 5.0.20 (Build 6305U) Fri Sep 16 2005 12:06:03 EDT
 *
 * @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass</A>
**/

package kz.tamur.ods.cache;
import com.intersys.cache.*;
import com.intersys.objects.*;
import com.intersys.objects.reflect.*;
import com.intersys.classes.*;


public class TClass extends Persistent {

    private static String CACHE_CLASS_NAME = "kz.tamur.ods.cache.TClass";
    /**
           <p>NB: DO NOT USE IN APPLICATION(!!!).
           <br>Use <code>TClass._open</code> instead!</br></p>
           <p>
           Used to construct a Java object, corresponding to existing object
           in Cache database.
           </p>
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    */
    public TClass (CacheObject ref) throws CacheException {
        super (ref);
    }
    public TClass (Database db, String initstr) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME,initstr));
    }
    /**
       Creates a new instance of object "TClass" in Cache
       database and corresponding object of class
       <code>TClass</code>.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @throws CacheException in case of error.

              @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
     */
    public TClass (Database db) throws CacheException {
        super (((SysDatabase)db).newCacheObject (CACHE_CLASS_NAME));
    }
    /**
       Runs method <code> %OpenId </code> in Cache to open an object
       from Cache database and creates corresponding object of class
       <code>TClass</code>.

       @return <code> RegisteredObject </code>, corresponding to opened
       object. This object may be of <code>TClass</code> or of
      any of its subclasses. Cast to <code>TClass</code> is
      guaranteed to pass without <code>ClassCastException</code> exception.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
      <code>Id</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
           @see #TClass
     */
    public static RegisteredObject _open (Database db, Id id) throws CacheException {
        CacheObject cobj = (((SysDatabase)db).openCacheObject(CACHE_CLASS_NAME, id.toString()));
        return (RegisteredObject)(cobj.newJavaInstance());
    }
    /**
       Runs method <code> %OpenId </code> in Cache to open an object
       from Cache database and creates corresponding object of class
       <code>TClass</code>.

       @return <code> RegisteredObject </code>, corresponding to opened
       object. This object may be of <code>TClass</code> or of
      any of its subclasses. Cast to <code>TClass</code> is
      guaranteed to pass without <code>ClassCastException</code> exception.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
      <code>Id</code>.
      @param concurrency Concurrency level.  represented as
      <code>Concurrency</code>.

      Here are concurrency values, see Object Concurrency Options in your on-line Cache' documentation for more information.
      @see <a href = "http://BERIK:1972/csp/docbook/DocBook.UI.Page.cls?KEY=GOBJ_concurrency"> Object Concurrency Options.</A>

      <TABLE border="1"
      summary="Object Concurrency Options.">
      <CAPTION><EM>Object Concurrency Options</EM></CAPTION>
      <TR><TD>0 </TD><TD>No locking, no locks are used</TD></TR>
      <TR><TD>1 </TD><TD>Atomic</TD></TR>
      <TR><TD>2 </TD><TD>Shared</TD></TR>
      <TR><TD>3 </TD><TD>Shared/Retained</TD></TR>
      <TR><TD>4 </TD><TD>Exclusive</TD></TR>
      </TABLE>

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
           @see #TClass
     */
    public static RegisteredObject _open (Database db, Id id, int concurrency) throws CacheException {
        CacheObject cobj = (((SysDatabase)db).openCacheObject(CACHE_CLASS_NAME, id.toString(), concurrency));
        return (RegisteredObject)(cobj.newJavaInstance());
    }
    /**
       Runs method <code> %Open </code> in Cache to open an object
       from Cache database and creates corresponding object of class
       <code>TClass</code>.

       @return <code> RegisteredObject </code>, corresponding to opened
       object. This object may be of <code>TClass</code> or of
      any of its subclasses. Cast to <code>TClass</code> is
      guaranteed to pass without <code>ClassCastException</code> exception.

       @param db <code>Database</code> object used for connection with
       Cache database.
       @param oid Object ID as specified in Cache. represented as
      <code>Oid</code>.


       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
           @see #TClass(com.intersys.objects.Database)
     */
    public static RegisteredObject _open (Database db, Oid oid) throws CacheException {
        CacheObject cobj = (((SysDatabase)db).openCacheObject(CACHE_CLASS_NAME, oid.getData()));
        return (RegisteredObject)(cobj.newJavaInstance());
    }
    /**
       Runs method <code> %Open </code> in Cache to open an object
       from Cache database and creates corresponding object of class
       <code>TClass</code>.

       @return <code> RegisteredObject </code>, corresponding to opened
       object. This object may be of <code>TClass</code> or of
      any of its subclasses. Cast to <code>TClass</code> is
      guaranteed to pass without <code>ClassCastException</code> exception.

       @param db <code>Database</code> object used for connection with
       Cache database.
       @param oid Object ID as specified in Cache. represented as
      <code>Oid</code>.
      @param concurrency Concurrency level.  represented as
      <code>Concurrency</code>.

      Here are concurrency values, see Object Concurrency Options in your on-line Cache' documentation for more information.
      @see <a href = "http://BERIK:1972/csp/docbook/DocBook.UI.Page.cls?KEY=GOBJ_concurrency"> Object Concurrency Options.</A>

      <TABLE border="1"
      summary="Object Concurrency Options.">
      <CAPTION><EM>Object Concurrency Options</EM></CAPTION>
      <TR><TD>0 </TD><TD>No locking, no locks are used</TD></TR>
      <TR><TD>1 </TD><TD>Atomic</TD></TR>
      <TR><TD>2 </TD><TD>Shared</TD></TR>
      <TR><TD>3 </TD><TD>Shared/Retained</TD></TR>
      <TR><TD>4 </TD><TD>Exclusive</TD></TR>
      </TABLE>

      @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
           @see #TClass(com.intersys.objects.Database)
     */
    public static RegisteredObject _open (Database db, Oid oid, int concurrency) throws CacheException {
        CacheObject cobj = (((SysDatabase)db).openCacheObject(CACHE_CLASS_NAME, oid.getData(), concurrency));
        return (RegisteredObject)(cobj.newJavaInstance());
    }
    /**
       Runs method <code> %Delete </code> in Cache to delete an object
       from Cache database.

       Deletes the stored version of the object with OID <var>oid</var> from the database.
       It does not remove any in-memory versions of the object that may be present.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
       <code>Id</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static void delete (Database db, Id id) throws CacheException {
        ((SysDatabase)db).deleteObject(CACHE_CLASS_NAME, id);
    }
    /**
       Runs method <code> %Delete </code> in Cache to delete an object
       from Cache database.

       Deletes the stored version of the object with OID <var>oid</var> from the database.
       It does not remove any in-memory versions of the object that may be present.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
       <code>Id</code>.
       @param concurrency Concurrency level.  represented as
       <code>Concurrency</code>.

      Here are concurrency values, see Object Concurrency Options in your on-line Cache' documentation for more information.
      @see <a href = "http://BERIK:1972/csp/docbook/DocBook.UI.Page.cls?KEY=GOBJ_concurrency"> Object Concurrency Options.</A>

      <TABLE border="1"
      summary="Object Concurrency Options.">
      <CAPTION><EM>Object Concurrency Options</EM></CAPTION>
      <TR><TD>0 </TD><TD>No locking, no locks are used</TD></TR>
      <TR><TD>1 </TD><TD>Atomic</TD></TR>
      <TR><TD>2 </TD><TD>Shared</TD></TR>
      <TR><TD>3 </TD><TD>Shared/Retained</TD></TR>
      <TR><TD>4 </TD><TD>Exclusive</TD></TR>
      </TABLE>


       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static void delete (Database db, Id id, int concurrency) throws CacheException {
        ((SysDatabase)db).deleteObject(CACHE_CLASS_NAME, id, concurrency);
    }
    /**
       Runs method <code> %Delete </code> in Cache to delete an object
       from Cache database.

       Deletes the stored version of the object with OID <var>oid</var> from the database.
       It does not remove any in-memory versions of the object that may be present.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
       <code>Id</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static void _deleteId (Database db, Id id) throws CacheException {
        delete(db, id);
    }
    /**
       Runs method <code> %Delete </code> in Cache to delete an object
       from Cache database.

       Deletes the stored version of the object with OID <var>oid</var> from the database.
       It does not remove any in-memory versions of the object that may be present.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
       <code>Id</code>.
       @param concurrency Concurrency level.  represented as
       <code>Concurrency</code>.

      Here are concurrency values, see Object Concurrency Options in your on-line Cache' documentation for more information.
      @see <a href = "http://BERIK:1972/csp/docbook/DocBook.UI.Page.cls?KEY=GOBJ_concurrency"> Object Concurrency Options.</A>

      <TABLE border="1"
      summary="Object Concurrency Options.">
      <CAPTION><EM>Object Concurrency Options</EM></CAPTION>
      <TR><TD>0 </TD><TD>No locking, no locks are used</TD></TR>
      <TR><TD>1 </TD><TD>Atomic</TD></TR>
      <TR><TD>2 </TD><TD>Shared</TD></TR>
      <TR><TD>3 </TD><TD>Shared/Retained</TD></TR>
      <TR><TD>4 </TD><TD>Exclusive</TD></TR>
      </TABLE>


       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static void _deleteId (Database db, Id id, int concurrency) throws CacheException {
        delete(db, id, concurrency);
    }
    /**
       Runs method <code> %Exists </code> in Cache to see if an object exists.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
      <code>Id</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_existsId(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static boolean exists (Database db, Id id) throws CacheException {
        return ((SysDatabase)db).existsObject(CACHE_CLASS_NAME, id);
    }
    /**
       Runs method <code> %Exists </code> in Cache to see if an object exists.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param id ID as specified in Cache represented as
      <code>Id</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_existsId(com.intersys.objects.Database, com.intersys.objects.Id)
           @see #TClass
     */
    public static Boolean _existsId (Database db, Id id) throws CacheException {
        return new Boolean(exists(db, id));
    }
    /**
       Returns class name of the class TClass as it is in
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
        checkAllFieldsValid(db, CACHE_CLASS_NAME, TClass.class);
    }

    /**
       Runs method <code> %Exists </code> in Cache to see if an object exists.

       @return <code> RegisteredObject </code>, corresponding to opened
       object. This object may be of <code>TClass</code> or of
      any of its subclasses. Cast to <code>TClass</code> is
      guaranteed to pass without <code>ClassCastException</code> exception.

       @param db <code>Database</code> object used for connection with
       Cache database.

       @param oid Object ID as specified in Cache. represented as
      <code>Oid</code>.

       @throws CacheException in case of error.
      @see java.lang.ClassCastException
           @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
           @see #TClass
     */
    public static boolean exists (Database db, Oid oid) throws CacheException {
        return exists (db, oid, CACHE_CLASS_NAME);
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
        checkAllMethods(db, CACHE_CLASS_NAME, TClass.class);
    }
    private static int ii_id = 3;
    private static int jj_id = 0;
    private static int kk_id = 3;
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
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#id"> id</A>
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
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#id"> id</A>
    */
    public void setid(java.lang.Long value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_id, jj_id,kk_id, Database.RET_PRIM, "id", dh);
        return;
    }

    private static int ii_isRepl = 4;
    private static int jj_isRepl = 0;
    private static int kk_isRepl = 4;
    /**
       Verifies that indexes for property <code>isRepl</code> in
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
    public static void checkisReplValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "isRepl",ii_isRepl, jj_isRepl, kk_isRepl);
    }
    /**
       Returns value of property <code>isRepl</code>.
       <Description>
       @return current value of <code>isRepl</code> represented as
       <code>java.lang.Boolean</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#isRepl"> isRepl</A>
    */
    public java.lang.Boolean getisRepl() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_isRepl,
                                                jj_isRepl,
                                                Database.RET_PRIM,
                                                "isRepl");
       return dh.getBoolean();
    }

    /**
       Sets new value for <code>isRepl</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.Boolean</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#isRepl"> isRepl</A>
    */
    public void setisRepl(java.lang.Boolean value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_isRepl, jj_isRepl,kk_isRepl, Database.RET_PRIM, "isRepl", dh);
        return;
    }

    private static int ii_name = 5;
    private static int jj_name = 0;
    private static int kk_name = 5;
    /**
       Verifies that indexes for property <code>name</code> in
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
    public static void checknameValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "name",ii_name, jj_name, kk_name);
    }
    /**
       Returns value of property <code>name</code>.
       <Description>
       @return current value of <code>name</code> represented as
       <code>java.lang.String</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#name"> name</A>
    */
    public java.lang.String getname() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_name,
                                                jj_name,
                                                Database.RET_PRIM,
                                                "name");
       return dh.getString();
    }

    /**
       Sets new value for <code>name</code>.
       <Description>
       @param value new value to be set represented as
       <code>java.lang.String</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#name"> name</A>
    */
    public void setname(java.lang.String value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_name, jj_name,kk_name, Database.RET_PRIM, "name", dh);
        return;
    }

    private static int ii_parent = 7;
    private static int jj_parent = 0;
    private static int kk_parent = 6;
    /**
       Verifies that indexes for property <code>parent</code> in
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
    public static void checkparentValid (Database db) throws CacheException {
        checkZobjValid(db, CACHE_CLASS_NAME, "parent",ii_parent, jj_parent, kk_parent);
    }
    /**
       Returns value of property <code>parent</code>.
       <Description>
       @return current value of <code>parent</code> represented as
       <code>kz.tamur.ods.cache.TClass</code>

       @throws CacheException if any error occurred during value retrieval.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#parent"> parent</A>
    */
    public kz.tamur.ods.cache.TClass getparent() throws CacheException {
        Dataholder dh = mInternal.getProperty(ii_parent,
                                                jj_parent,
                                                Database.RET_OBJECT,
                                                "parent");
        CacheObject cobj = dh.getCacheObject();
        if (cobj == null)
            return null;
        return (kz.tamur.ods.cache.TClass)(cobj.newJavaInstance());
    }

    /**
       Sets new value for <code>parent</code>.
       <Description>
       @param value new value to be set represented as
       <code>kz.tamur.ods.cache.TClass</code>.
       @throws CacheException if any error occurred during value setting.
       @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#parent"> parent</A>
    */
    public void setparent(kz.tamur.ods.cache.TClass value) throws CacheException {
        Dataholder dh = new Dataholder (value);
        mInternal.setProperty(ii_parent, jj_parent,kk_parent, Database.RET_OBJECT, "parent", dh);
        return;
    }

    /**
     <p>Runs method sys_BMEBuilt in Cache.</p>
     @param db represented as Database
     @param bmeName represented as com.intersys.objects.StringHolder
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%BMEBuilt"> Method %BMEBuilt</A>
    */
    public static java.lang.Boolean sys_BMEBuilt (Database db, com.intersys.objects.StringHolder bmeName) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        int[] _refs = new int[1];
        args[0] = Dataholder.create (bmeName.value);
        _refs[0] = 1;
        Dataholder[] res=db.runClassMethod(CACHE_CLASS_NAME,"%BMEBuilt",_refs,args,Database.RET_PRIM);
        bmeName.set(res[1].getString());
        return res[0].getBoolean();
    }
    /**
     <p>Runs method sys_BuildIndices in Cache.</p>
     @param db represented as Database
     default argument idxlist set to ""
     @throws CacheException if any error occured while running the method.
     @see #sys_BuildIndices(Database,com.intersys.objects.SList)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%BuildIndices"> Method %BuildIndices</A>
    */
    public static void sys_BuildIndices (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%BuildIndices",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_BuildIndices in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%BuildIndices"> Method %BuildIndices</A>
    */
    public static void sys_BuildIndices (Database db, com.intersys.objects.SList idxlist) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(idxlist);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%BuildIndices",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_CheckUnique in Cache.</p>
     @param db represented as Database
     default argument idxlist set to ""
     @throws CacheException if any error occured while running the method.
     @see #sys_CheckUnique(Database,com.intersys.objects.SList)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%CheckUnique"> Method %CheckUnique</A>
    */
    public static void sys_CheckUnique (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%CheckUnique",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_CheckUnique in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%CheckUnique"> Method %CheckUnique</A>
    */
    public static void sys_CheckUnique (Database db, com.intersys.objects.SList idxlist) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(idxlist);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%CheckUnique",args,Database.RET_PRIM);
        db.parseStatus(res);
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%ClassName"> Method %ClassName</A>
    */
    public static java.lang.String sys_ClassName (Database db, java.lang.Boolean fullname) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(fullname);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%ClassName",args,Database.RET_PRIM);
        return res.getString();
    }
    /**
     <p>Runs method sys_Delete in Cache.</p>
     <p>Description: Deletes the stored version of the object with OID <var>oid</var> from the database. 
It does not remove any in-memory versions of the object that may be present.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.

<p>Returns a <CLASS>%Status</CLASS> value indicating success or failure.

<p>Internally, <METHOD>%Delete</METHOD> initiates a transaction and then invokes the storage 
interface method <METHOD>%DeleteData</METHOD>. If <METHOD>%DeleteData</METHOD> succeeds, the 
transaction is committed, otherwise it is rolled back. </p>
     @param db represented as Database
     default argument oid set to ""
     default argument concurrency set to -1
     @throws CacheException if any error occured while running the method.
     @see #sys_Delete(Database,com.intersys.objects.Oid,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Delete"> Method %Delete</A>
    */
    public static void sys_Delete (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Delete",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_Delete in Cache.</p>
     <p>Description: Deletes the stored version of the object with OID <var>oid</var> from the database. 
It does not remove any in-memory versions of the object that may be present.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.

<p>Returns a <CLASS>%Status</CLASS> value indicating success or failure.

<p>Internally, <METHOD>%Delete</METHOD> initiates a transaction and then invokes the storage 
interface method <METHOD>%DeleteData</METHOD>. If <METHOD>%DeleteData</METHOD> succeeds, the 
transaction is committed, otherwise it is rolled back. </p>
     @param db represented as Database
     @param oid represented as com.intersys.objects.Oid
     default argument concurrency set to -1
     @throws CacheException if any error occured while running the method.
     @see #sys_Delete(Database,com.intersys.objects.Oid,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Delete"> Method %Delete</A>
    */
    public static void sys_Delete (Database db, com.intersys.objects.Oid oid) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(oid);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Delete",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_Delete in Cache.</p>
     <p>Description: Deletes the stored version of the object with OID <var>oid</var> from the database. 
It does not remove any in-memory versions of the object that may be present.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.

<p>Returns a <CLASS>%Status</CLASS> value indicating success or failure.

<p>Internally, <METHOD>%Delete</METHOD> initiates a transaction and then invokes the storage 
interface method <METHOD>%DeleteData</METHOD>. If <METHOD>%DeleteData</METHOD> succeeds, the 
transaction is committed, otherwise it is rolled back. </p>
     @param db represented as Database
     @param oid represented as com.intersys.objects.Oid
     @param concurrency represented as java.lang.Integer
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Delete"> Method %Delete</A>
    */
    public static void sys_Delete (Database db, com.intersys.objects.Oid oid, java.lang.Integer concurrency) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(oid);
        args[1] = new Dataholder(concurrency);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Delete",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_DeleteExtent in Cache.</p>
     <p>Description: Delete all instances of this class from its extent. On exit <var>instancecount</var> 
contains the original number of instances while <var>deletecount</var> contains 
the number of instances actually deleted.

<p>Internally, <METHOD>%DeleteExtent</METHOD> iterates over the set of instances in the 
collection and invokes the <METHOD>%Delete</METHOD> method.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.

<p>Returns a <CLASS>%Status</CLASS> value indicating success or failure.</p>
     @param db represented as Database
     @param concurrency represented as java.lang.Integer
     @param deletecount represented as com.intersys.objects.StringHolder
     @param instancecount represented as com.intersys.objects.StringHolder
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%DeleteExtent"> Method %DeleteExtent</A>
    */
    public static void sys_DeleteExtent (Database db, java.lang.Integer concurrency, com.intersys.objects.StringHolder deletecount, com.intersys.objects.StringHolder instancecount) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        int[] _refs = new int[2];
        args[0] = new Dataholder(concurrency);
        args[1] = Dataholder.create (deletecount.value);
        _refs[0] = 2;
        args[2] = Dataholder.create (instancecount.value);
        _refs[1] = 3;
        Dataholder[] res=db.runClassMethod(CACHE_CLASS_NAME,"%DeleteExtent",_refs,args,Database.RET_PRIM);
        deletecount.set(res[1].getString());
        instancecount.set(res[2].getString());
        db.parseStatus(res[0]);
        return;
    }
    /**
     <p>Runs method sys_DeleteId in Cache.</p>
     <p>Description: Deletes the stored version of the object with ID <var>id</var> from the database. 

<p><METHOD>%DeleteId</METHOD> is identical in operation to the <METHOD>%Delete</METHOD> method except 
that it uses and Id value instead of an OID value to find an object.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.</p>
     @param db represented as Database
     @param id represented as java.lang.String
     default argument concurrency set to -1
     @throws CacheException if any error occured while running the method.
     @see #sys_DeleteId(Database,java.lang.String,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%DeleteId"> Method %DeleteId</A>
    */
    public static void sys_DeleteId (Database db, java.lang.String id) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%DeleteId",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_DeleteId in Cache.</p>
     <p>Description: Deletes the stored version of the object with ID <var>id</var> from the database. 

<p><METHOD>%DeleteId</METHOD> is identical in operation to the <METHOD>%Delete</METHOD> method except 
that it uses and Id value instead of an OID value to find an object.

Refer to <LINK href=/classref/AboutConcurrency.html>About Concurrency</LINK> for more details 
on the optional <var>concurrency</var> argument.</p>
     @param db represented as Database
     @param id represented as java.lang.String
     @param concurrency represented as java.lang.Integer
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%DeleteId"> Method %DeleteId</A>
    */
    public static void sys_DeleteId (Database db, java.lang.String id, java.lang.Integer concurrency) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(id);
        args[1] = new Dataholder(concurrency);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%DeleteId",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_Exists in Cache.</p>
     @param db represented as Database
     default argument oid set to ""
     @throws CacheException if any error occured while running the method.
     @see #sys_Exists(Database,com.intersys.objects.Oid)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Exists"> Method %Exists</A>
    */
    public static java.lang.Boolean sys_Exists (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Exists",args,Database.RET_PRIM);
        return res.getBoolean();
    }
    /**
     <p>Runs method sys_Exists in Cache.</p>
     @param db represented as Database
     @param oid represented as com.intersys.objects.Oid
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Exists"> Method %Exists</A>
    */
    public static java.lang.Boolean sys_Exists (Database db, com.intersys.objects.Oid oid) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(oid);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%Exists",args,Database.RET_PRIM);
        return res.getBoolean();
    }
    /**
     <p>Runs method sys_ExistsId in Cache.</p>
     @param db represented as Database
     @param id represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%ExistsId"> Method %ExistsId</A>
    */
    public static java.lang.Boolean sys_ExistsId (Database db, java.lang.String id) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(id);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%ExistsId",args,Database.RET_PRIM);
        return res.getBoolean();
    }
    /**
     <p>Runs method sys_Extends in Cache.</p>
     <p>Description: Returns true (1) if this class is inherited either via primary or secondary inheritance from 'isclass'.</p>
     @param db represented as Database
     @param isclass represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%Extends"> Method %Extends</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%GetParameter"> Method %GetParameter</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%GetParameter"> Method %GetParameter</A>
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
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%IsA"> Method %IsA</A>
    */
    public static java.lang.Integer sys_IsA (Database db, java.lang.String isclass) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(isclass);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%IsA",args,Database.RET_PRIM);
        return res.getInteger();
    }
    /**
     <p>Runs method sys_KillExtent in Cache.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%KillExtent"> Method %KillExtent</A>
    */
    public static void sys_KillExtent (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%KillExtent",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_PackageName in Cache.</p>
     <p>Description: Returns the object's package name.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%PackageName"> Method %PackageName</A>
    */
    public static java.lang.String sys_PackageName (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%PackageName",args,Database.RET_PRIM);
        return res.getString();
    }
    /**
     <p>Runs method sys_PurgeIndices in Cache.</p>
     @param db represented as Database
     default argument idxlist set to ""
     @throws CacheException if any error occured while running the method.
     @see #sys_PurgeIndices(Database,com.intersys.objects.SList)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%PurgeIndices"> Method %PurgeIndices</A>
    */
    public static void sys_PurgeIndices (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%PurgeIndices",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_PurgeIndices in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%PurgeIndices"> Method %PurgeIndices</A>
    */
    public static void sys_PurgeIndices (Database db, com.intersys.objects.SList idxlist) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(idxlist);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%PurgeIndices",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortBegin in Cache.</p>
     @param db represented as Database
     default argument idxlist set to ""
     default argument excludeunique set to 0
     @throws CacheException if any error occured while running the method.
     @see #sys_SortBegin(Database,com.intersys.objects.SList,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortBegin"> Method %SortBegin</A>
    */
    public static void sys_SortBegin (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortBegin",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortBegin in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     default argument excludeunique set to 0
     @throws CacheException if any error occured while running the method.
     @see #sys_SortBegin(Database,com.intersys.objects.SList,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortBegin"> Method %SortBegin</A>
    */
    public static void sys_SortBegin (Database db, com.intersys.objects.SList idxlist) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(idxlist);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortBegin",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortBegin in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     @param excludeunique represented as java.lang.Integer
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortBegin"> Method %SortBegin</A>
    */
    public static void sys_SortBegin (Database db, com.intersys.objects.SList idxlist, java.lang.Integer excludeunique) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(idxlist);
        args[1] = new Dataholder(excludeunique);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortBegin",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortEnd in Cache.</p>
     @param db represented as Database
     default argument idxlist set to ""
     default argument commit set to 1
     @throws CacheException if any error occured while running the method.
     @see #sys_SortEnd(Database,com.intersys.objects.SList,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortEnd"> Method %SortEnd</A>
    */
    public static void sys_SortEnd (Database db) throws CacheException {
        Dataholder[] args = new Dataholder[0];
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortEnd",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortEnd in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     default argument commit set to 1
     @throws CacheException if any error occured while running the method.
     @see #sys_SortEnd(Database,com.intersys.objects.SList,java.lang.Integer)
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortEnd"> Method %SortEnd</A>
    */
    public static void sys_SortEnd (Database db, com.intersys.objects.SList idxlist) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(idxlist);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortEnd",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method sys_SortEnd in Cache.</p>
     @param db represented as Database
     @param idxlist represented as com.intersys.objects.SList
     @param commit represented as java.lang.Integer
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#%SortEnd"> Method %SortEnd</A>
    */
    public static void sys_SortEnd (Database db, com.intersys.objects.SList idxlist, java.lang.Integer commit) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(idxlist);
        args[1] = new Dataholder(commit);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"%SortEnd",args,Database.RET_PRIM);
        db.parseStatus(res);
        return;
    }
    /**
     <p>Runs method ClassNameIdxExists in Cache.</p>
     @param K1 represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=USER&CLASSNAME=kz.tamur.ods.cache.TClass#ClassNameIdxExists"> Method ClassNameIdxExists</A>
    */
    public java.lang.Boolean ClassNameIdxExists (java.lang.String K1) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(K1);
        Dataholder res=mInternal.runInstanceMethod("ClassNameIdxExists",args,Database.RET_PRIM);
        return res.getBoolean();
    }
    /**
     <p>Returns a CallableStatement for query Extent.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
    */
    public static CacheQuery query_Extent (Database db) throws CacheException {
        return new CacheQuery(db, "kz_tamur_ods_cache.TClass_Extent", 0, 0);
    }

    /**
     <p>Returns a CallableStatement for query byId.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
    */
    public static CacheQuery query_byId (Database db) throws CacheException {
        return new CacheQuery(db, "kz_tamur_ods_cache.TClass_byId", 1, 2);
    }

    /**
     <p>Returns a CallableStatement for query byName.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
    */
    public static CacheQuery query_byName (Database db) throws CacheException {
        return new CacheQuery(db, "kz_tamur_ods_cache.TClass_byName", 1, 2);
    }

    /**
     <p>Returns a CallableStatement for query byParent.</p>
     @param db represented as Database
     @throws CacheException if any error occured while running the method.
    */
    public static CacheQuery query_byParent (Database db) throws CacheException {
        return new CacheQuery(db, "kz_tamur_ods_cache.TClass_byParent", 1, 2);
    }

    public static Object addToBatchInsert (Object batch, java.sql.Connection con, Long id, Boolean isRepl, String name, Long parent) throws java.sql.SQLException {
        Object value;
        boolean isUnicode = true;
        String locale = null;
        if (con != null) {
            try {
                com.intersys.jdbc.CacheConnection c = com.intersys.cache.jdbcutil.JDBCAdapter.getCacheConnection (con);
                isUnicode = c.isServerUnicode();
                locale = c.getServerLocale();
            } catch (CacheException x) {
                throw new java.sql.SQLException ("Connection is not a CacheConnection.");
            }
        }
        if (batch == null)
            batch = new com.intersys.jdbc.QuickStatement.Batch (isUnicode,locale);
        com.intersys.jdbc.QuickStatement.Batch qbatch = (com.intersys.jdbc.QuickStatement.Batch) batch;
        com.intersys.jdbc.SysListProxy.setInteger (qbatch.list, 5); // number of columns
        com.intersys.jdbc.SysListProxy.setLongWrapper(qbatch.list, id);
        com.intersys.jdbc.SysListProxy.setBooleanWrapper(qbatch.list, isRepl);
        com.intersys.jdbc.SysListProxy.setString(qbatch.list, name);
        com.intersys.jdbc.SysListProxy.setLongWrapper(qbatch.list, parent);
        com.intersys.jdbc.SysListProxy.setUndefined(qbatch.list); // for x__classname
        qbatch.counter++;
        return qbatch;
    }
    
    public static java.util.List executeBatchInsert (java.sql.Connection con, Object batch, int nolock) throws java.sql.SQLException {
        Object ids = com.intersys.jdbc.QuickStatement.Batch.execute ("kz_tamur_ods_cache", "t_classes", -5, batch, con, nolock);
        return new SList (ids);
        }
}
