package com.cifs.or2.server.plugins;

import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.server.orlang.SrvPlugin;
import lotus.domino.*;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 09.10.2009
 * Time: 16:21:53
 * To change this template use File | Settings | File Templates.
 */
public class Lotus implements SrvPlugin {
    lotus.domino.Session lotusSession;
    com.cifs.or2.server.Session s;
    Database database;
    private static org.apache.commons.logging.Log log = LogFactory.getLog(Lotus.class);
    public com.cifs.or2.server.Session getSession() {
        return s;
    }

    public void setSession(com.cifs.or2.server.Session session) {
        s = session;
    }
    /**
     * Коннект к бд
     * @param host 
     * @param user
     * @param password
     * @param dbpath
     * @return бд
     */
    public Database connect(String host,String user,String password,String dbpath) {
        try {
            NotesThread.sinitThread();
            lotusSession = NotesFactory.createSession(host,user,password);
            database = lotusSession.getDatabase("", dbpath);
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return database;
    }
    /**
     * Коннект к бд
     * @param password
     * @param dbpath
     * @return бд
     */
    public Database connect(String password,String dbpath) {
        try {
            NotesThread.sinitThread();
            lotusSession = NotesFactory.createSession((String)null,(String)null,password);
            database = lotusSession.getDatabase("", dbpath);
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return database;
    }
    /**
     * Возвращает коллекцию документов из бд
     * @param database бд
     * @param filter фильтр
     * @return коллекция документов
     * @see lotus.domino.Database#search(String)
     */
    public DocumentCollection getDocuments(Database database,String filter){
        DocumentCollection docs=null;
        try{
            if (!database.isOpen())
              System.out.println("erul/Republic.nsf does not exist on snapper");
            else{
                    System.out.println("Title of names.nsf: \"" + database.getTitle()+ "\"") ;
                    if(filter==null)
                        docs=database.getAllDocuments();
                    else
                        docs = database.search(filter);
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return docs;
    }
    /**
     * Преобразовать в дату
     * @param date дата объект
     * @return дата
     */
    public KrnDate toDate(Object date) {
    	KrnDate d_=null;
        if (date != null && date instanceof String && ((String)date).length() > 9) {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            if(((String)date).charAt(4)=='-')
                df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            try {
                d_= new KrnDate(df.parse((String)date).getTime());
            } catch (ParseException e) {
//                log.error(e, e);
            }
            return d_;
        }else if(date!=null && date instanceof DateTime){
            try {
                d_=new KrnDate(((DateTime)date).toJavaDate().getTime());
            } catch (Exception e) {
                log.error(e, e);
            }
            return d_;
        }
        return null;
    }
    /**
     * Преобразовать в число
     * @param str строка
     * @return число
     */
    public Integer toInt(String str) {
        Integer res = null;
        if (str != null && str.length() > 0 && !str.equals("-")) {
            try {
                return new Integer(str);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
        return res;
    }
    public Integer toInt(Double dbl) {
        return dbl.intValue();
    }
    /**
     * Преобразовать в число с плавающей точкой
     * @param value объект
     * @return число, типа double
     */
    public Double toFloat(Object value) {
        Double res = null;
        String str=null;
        if(value instanceof String){
        	str=(String)value;
        }else if(value instanceof Double){
        	res=(Double)value;
        }
        if (str != null && str.length() > 0 && !str.equals("-")) {
            try {
                res = new Double(str.trim());
            } catch (NumberFormatException e) {
                str = str.replace(',', '.');
                try {
                    res = new Double(str.trim());
                } catch (NumberFormatException e1) {
                    log.error(e1, e1);
                }
            }
        }
        return res;
    }
    /**
     * Возвращает значение элемента
     * @param name 
     * @param doc
     * @return
     * @see lotus.domino.Document#getItemValue(String)
     */
    public Vector getItemValue(String name,Document doc){
        Vector res=new Vector();
        try{
            res=doc.getItemValue(name);
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Отсоединиться от бд
     * @param database
     * @see lotus.domino.Base#recycle()
     */
    public void disconnect(Database database){
        if(this.database!=null){
            try{
                this.database.recycle();
            }catch(NotesException e){
                e.printStackTrace();
            }
        }
        if(this.lotusSession!=null){
            try{
                this.lotusSession.recycle();
            }catch(NotesException e){
                e.printStackTrace();
            }finally{
                this.database=null;
                this.lotusSession=null;
                NotesThread.stermThread();
            }
        }
    }
}
