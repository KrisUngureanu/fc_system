/**
 * Cache' Java Class Generated for class kz.tamur.ods.cache.OR3Driver2 on version Cache for Windows NT (Intel/P4) 5.0.20 (Build 6305U) Fri Sep 16 2005 12:06:03 EDT
 *
 * @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=BERIK&CLASSNAME=kz.tamur.ods.cache.OR3Driver2</A>
**/

package kz.tamur.ods.cache;
import com.intersys.cache.*;
import com.intersys.objects.*;
import com.intersys.classes.*;


public abstract class OR3Driver2  implements java.io.Serializable {

    private static String CACHE_CLASS_NAME = "kz.tamur.ods.cache.OR3Driver2";
    /**
     <p>Runs method getClasses in Cache.</p>
     @param db represented as Database
     @param classId represented as long
     @param subClasses represented as boolean
     @param recursive represented as boolean
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=BERIK&CLASSNAME=kz.tamur.ods.cache.OR3Driver2#getClasses"> Method getClasses</A>
    */
    public static ListOfDataTypes getClasses (Database db, long classId, boolean subClasses, boolean recursive) throws CacheException {
        Dataholder[] args = new Dataholder[3];
        args[0] = new Dataholder(classId);
        args[1] = new Dataholder(subClasses);
        args[2] = new Dataholder(recursive);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getClasses",args,Database.RET_OBJECT);
        CacheObject cobj = res.getCacheObject();
        if (cobj == null)
            return null;
        return (ListOfDataTypes)(cobj.newJavaInstance());
    }
    /**
     <p>Runs method get_Id in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=BERIK&CLASSNAME=kz.tamur.ods.cache.OR3Driver2#getId"> Method getId</A>
    */
    public static long get_Id (Database db, java.lang.String name) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(name);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getId",args,Database.RET_PRIM);
        return res.getLongValue();
    }
    /**
     <p>Runs method getNextId in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=BERIK&CLASSNAME=kz.tamur.ods.cache.OR3Driver2#getNextId"> Method getNextId</A>
    */
    public static long getNextId (Database db, java.lang.String name) throws CacheException {
        Dataholder[] args = new Dataholder[1];
        args[0] = new Dataholder(name);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"getNextId",args,Database.RET_PRIM);
        return res.getLongValue();
    }
    /**
     <p>Runs method setId in Cache.</p>
     @param db represented as Database
     @param name represented as java.lang.String
     @param value represented as long
     @throws CacheException if any error occured while running the method.
     @see <a href = "http://BERIK:1972/apps/documatic/%25CSP.Documatic.cls?APP=1&PAGE=CLASS&LIBRARY=BERIK&CLASSNAME=kz.tamur.ods.cache.OR3Driver2#setId"> Method setId</A>
    */
    public static void setId (Database db, java.lang.String name, long value) throws CacheException {
        Dataholder[] args = new Dataholder[2];
        args[0] = new Dataholder(name);
        args[1] = new Dataholder(value);
        Dataholder res=db.runClassMethod(CACHE_CLASS_NAME,"setId",args,Database.RET_NONE);
        return;
    }
}
