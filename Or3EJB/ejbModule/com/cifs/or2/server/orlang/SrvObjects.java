package com.cifs.or2.server.orlang;

import static kz.tamur.or3ee.common.SessionIds.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.imageio.ImageIO;

import kz.tamur.SecurityContextHolder;
import kz.tamur.common.ErrorCodes;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.lang.*;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Value;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.wf.WorkflowException;
import kz.tamur.util.Base64;
import kz.tamur.util.CollectionTypes;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.tigris.gef.base.CmdSaveGraphics;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;

/**
 * Created by IntelliJ IDEA.
 * Date: 11.01.2005
 * Time: 10:12:05
 * 
 * @author berik
 */
public class SrvObjects extends Objects implements CollectionTypes {

    /** session. */
    private Session session;

    /** xml time fmt. */
    private static ThreadLocalDateFormat xmlTimeFmt = new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /** xml date fmt. */
    private static ThreadLocalDateFormat xmlDateFmt = new ThreadLocalDateFormat("yyyy-MM-dd");

    /**
     * Конструктор класса SrvObjects.
     * 
     * @param session
     *            the session
     * @param log
     *            the log
     */
    public SrvObjects(Session session) {
        this.session = session;
    }

    
    @Override
    public KrnClass getClass(String name) {
        return session.getClassByName(name);
    }

    
    @Override
    public List<KrnClass> getClasses(long baseClassId, boolean withSubclasses) {
        try {
            return session.getClasses(baseClassId, withSubclasses);
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    
    @Override
    public List<KrnAttribute> getClassAttributes(String name) {
        KrnClass cls = session.getClassByName(name);
        if (cls != null)
        	return session.getClassAttributes(cls);
        else
        	return null;
    }

    
    @Override
    public KrnClass getClassById(Number id) {
        return session.getClassById(id.longValue());
    }

    
    /**
     * Получить Krn-класс по его <code>uid</code>.
     * 
     * @param uid
     *            идентификатор класса.
     * @return Krn-класс
     */
    public KrnClass getClassByUid(String uid) {
        try {
            return session.getClassByUid(uid);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    /**
     * Получить все не системные классы.
     * 
     * @return список Krn-классов.
     */
    public List<KrnClass> getAllClasses() {
        try {
            return session.getAllClasses();
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    /**
     * Получить методы класса по его <code>id</code>.
     * 
     * @param clsId
     *            код класса.
     * @return список методов класса.
     */
    public List<SrvMethodWrp> getMethods(long clsId) {
        ArrayList<SrvMethodWrp> res = new ArrayList<SrvMethodWrp>();
        try {
            KrnMethod[] methods = session.getMethods(clsId);
            for (int i = 0; i < methods.length; ++i) {
                res.add(new SrvMethodWrp(methods[i], session));
            }
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    
    @Override
    public KrnAttribute getAttribute(KrnClass cls, String name) {
        return session.getAttributeByName(cls.getKrnClass(), name);
    }

    
    @Override
    public KrnAttribute getAttributeById(Number id) {
        return session.getAttributeById(id.longValue());
    }

    @Override
    public KrnObject getObject(String uid) {
        try {
            return session.getObjectByUid(uid, session.getContext().trId);
        } catch (KrnException e) {
            if (e.code > 100) {
                SecurityContextHolder.getLog().warn(e.getMessage());
            } else {
                SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }

    
    @Override
    public KrnObject getObject(Number id) {
        try {
            return session.getObjectById(id.longValue(), -1);
        } catch (KrnException e) {
            if (e.code > 100) {
                SecurityContextHolder.getLog().warn(e.getMessage());
            } else {
                SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }

    
    @Override
    public KrnObject createObject(String className) {
        Context ctx = session.getContext();
        return createObject(className, ctx.trId);
    }

    /**
     * Создать объект в классе с именем <code>className</code> в заданной транзакции.
     * Пример:
     * 
     * <pre>
     * $person = $Objects.createObject(“Персонал”, 0)
     * </pre>
     * 
     * @param className
     *            имя класса, экземпляром которого будет созданный объект.
     * @param trId
     *            id транзакции.
     * @return созданный объект.
     */
    public KrnObject createObject(String className, long trId) {
        try {
            KrnClass cls = session.getClassByName(className);
            return session.createObject(cls, trId);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    /**
     * Создать объект с заданным идентификатором <code>uid</code>.
     * 
     * @param className
     *            имя класса, экземпляром которого будет созданный объект.
     * @param uid
     *            идентификатор объекта.
     * @return krn object
     */
    public KrnObject createObject(String className, String uid) {
        try {
            Context ctx = session.getContext();
            KrnClass cls = session.getClassByName(className);
            return session.createObjectWithUid(cls, uid, ctx.trId);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    
    @Override
    public KrnObject cloneObject(KrnObject obj) {
        try {
            Context ctx = session.getContext();
            KrnObject[] srcs = { obj.getKrnObject() };
            return session.cloneObject2(srcs, ctx.trId, ctx.trId)[0];
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    @Override
    public List<KrnObject> filter(KrnObject filter, int limit,int beginRow,int endRow) throws Exception {
        Context ctx = session.getContext();
        return session.filter(filter.getKrnObject(), limit,beginRow,endRow, ctx.trId);
    }
    
    @Override
    public long filterCount(KrnObject filter) throws Exception {
        Context ctx = session.getContext();
        long res = session.filterCount(filter.getKrnObject(), ctx.trId);
        return res;
    }

    @Override
    public List<Object> filterGroup(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        String uid = filter.getUID();
        session.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                session.setFilterParam(uid, name, value);
            }
        }
        long trId = allTransactions ? -1 : session.getContext().trId;
        List<Object> res = session.filterGroup(filter.getKrnObject(), trId);
        return res;
    }
    @Override
    public List<Object> filterGroup(KrnObject filter) throws Exception {
        Context ctx = session.getContext();
        List<Object> res = session.filterGroup(filter.getKrnObject(), ctx.trId);
        return res;
    }
    
    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра.
     * 
     * @param filter
     *            применяемый фильтр.
     * @param params
     *            объект <code>Map</code> с именами и значениями параметров, используемыми фильтром <code>filter</code>.
     * @param allTransactions
     *            <code>true</code> – применить фильтр ко всем транзакциям (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – применить фильтр только к нулевым транзакциям (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @param limit
     *            органичение количества полученных значений.
     * @param beginRow
     *            номер начальной строки.
     * @param endRow
     *            номер конечной строки.
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    @Override
    public List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions, int limit,int beginRow,int endRow) throws Exception {
        String uid = filter.getUID();
        session.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                session.setFilterParam(uid, name, value);
            }
        }
        long trId = allTransactions ? -1 : session.getContext().trId;
        return session.filter(filter.getKrnObject(), limit,beginRow,endRow, trId);
    }
    
    @Override
    public long filterCount(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        String uid = filter.getUID();
        session.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                session.setFilterParam(uid, name, value);
            }
        }
        long trId = allTransactions ? -1 : session.getContext().trId;
        long res = session.filterCount(filter.getKrnObject(), trId);
        return res;
    }

    
    @Override
    public List<KrnObject> getClassObjects(String className) {
        Context ctx = session.getContext();
        return getClassObjects(className, ctx.trId);
    }

    /**
     * Получить объекты заданного класса и определённой транзакции.
     * 
     * @param className
     *            имя класса.
     * @param trId
     *            id транзакции.
     * @return объекты класса.
     */
    public List<KrnObject> getClassObjects(String className, Number trId) {
        try {
            KrnClass cls = session.getClassByName(className);
            KrnObject[] objs = session.getClassObjects(cls, new long[0], trId.longValue());
            List<KrnObject> res = new ArrayList<KrnObject>(objs.length);
            for (int i = 0; i < objs.length; i++) {
                res.add(objs[i]);
            }
            return res;
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    
    @Override
    public List sort(List objs, String path) {
        return null;
    }

    
    @Override
    public Sequence getSequence(String uid) {
        try {
            return new SrvSequence(session.getObjectByUid(uid, session.getContext().trId), session);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    
    @Override
    public List<KrnObject> find(String path, Object value) throws Exception {
        return find(path, value, null);
    }

    /**
     * Найти в БД объект.
     * 
     * Оператор <code>find</code> ищет значение атрибута, напрямую связанного с классом.
     * Конструкция <code>className.className.attrName</code> неприемлема.
     * 
     * @param path
     *            шаблон <code>className.attrName</code>, указывается имя класса и атрибут, где производиться поиск.
     * @param value
     *            значение, которое необходимо найти.
     * @param lang
     *            язык данных на котором производится поиск.
     * @return список объектов, подпадающих под условия поиска.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> find(String path, Object value, KrnObject lang) throws Exception {
        return find(path, value, lang, false);
    }

    /**
     * Найти в БД объект.
     * 
     * Оператор <code>find</code> ищет значение атрибута, напрямую связанного с классом.
     * Конструкция <code>className.className.attrName</code> неприемлема.
     * 
     * @param path
     *            шаблон <code>className.attrName</code>, указывается имя класса и атрибут, где производиться поиск.
     * @param value
     *            значение, которое необходимо найти.
     * @param lang
     *            язык данных на котором производится поиск.
     * @param allTransactions
     *            <code>true</code> – искать во всех транзакциях (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – искать только в нулевых транзакциях (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @return список объектов, подпадающих под условия поиска.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> find(String path, Object value, KrnObject lang, boolean allTransactions) throws Exception {
        long langId = -1;
        if (lang != null)
            langId = lang.getKrnObject().id;
        Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(session, path);
        KrnClass cls = session.getClassByName(path.substring(0, path.indexOf('.')));
        if (ps.length == 1) {
            KrnAttribute attr = ps[0].first;
            if (langId == -1)
                langId = attr.isMultilingual ? session.getUserSession().getDataLanguage().id : 0;
            long trId = allTransactions ? -1 : session.getContext().trId;
            KrnObject[] objs = session
                    .getObjectsByAttribute(cls.id, attr.id, langId, ComparisonOperations.CO_EQUALS, value, trId);
            return Arrays.asList(objs);
        } else {
            SecurityContextHolder.getLog().error("Objects.find: Требуется путь с глубиной 1");
        }
        return Collections.emptyList();
    }

    
    @Override
    public List getSqlResult(String sql, List vals, List params) {
        try {
            throw new KrnException(0, "Not implemented");
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public void removeObject(int id) {
        SecurityContextHolder.getLog().error("Not implemented");
    }

    
    @Override
    public String getUID(int id) {
        try {
            return session.getUId(id);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return "";
    }

    
    @Override
    public void clearFilterParam(KrnObject filter) {
        session.clearFilterParams(filter.getUID());
    }

    
    @Override
    public void setFilterParam(KrnObject filter, Map params) {
        try {
            String uid = filter.getUID();
            if (params != null) {
                for (Iterator it = params.keySet().iterator(); it.hasNext();) {
                    String name = (String) it.next();
                    Object value = params.get(name);
                    session.setFilterParam(uid, name, value);
                }
            }
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
    }

    @Override
    public List getFilterParam(String fuid, String pid) {
        List res = session.getFilterParams(fuid, pid);
        if (res != null) {
            ArrayList params = new ArrayList();
            for (int i = 0; i < res.size(); i++)
                params.add(res.get(i));
            return params;
        }
        return Collections.EMPTY_LIST;
    }

    
    @Override
    public List<KrnAttribute> getAttributesByType(KrnClass cls, boolean inherited) {
        List<KrnAttribute> res = null;
        try {
            res = new ArrayList<KrnAttribute>();
            KrnAttribute[] list = session.getAttributesByTypeId(cls.getId(), inherited);
            for (KrnAttribute attr : list) {
                res.add(attr);
            }
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    
    @Override
    public boolean stopProcess(Activity activity) throws KrnException {
        return session.cancelProcess(activity.flowId, activity.msg, true, false);
    }

    public boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException {
        return session.cancelProcess(activity.flowId, activity.msg, true, forceCancel);
    }

    /**
     * Остановка текущего процесса.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void stopProcess() throws KrnException {
        Context ctx = session.getContext();
        stopProcess(ctx.flowId);
    }

    public void stopProcess(boolean forceCancel) throws KrnException {
        Context ctx = session.getContext();
        stopProcess(ctx.flowId, forceCancel);
    }

    /**
     * Остановка процесса.
     * 
     * @param flowId
     *            id останавливаемого процесса.
     * @throws KrnException
     *             the krn exception
     */
    public void stopProcess(long flowId) throws KrnException {
        session.cancelProcess(flowId, null, true, false);
    }

    public void stopProcess(long flowId, boolean forceCancel) throws KrnException {
    	session.cancelProcess(flowId, null, true, forceCancel);
    }
    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param data
     *            сохраняемый текст в виде массива байтов.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(byte[] data, String dir, String name) {
        return saveExprToFile(data, dir, name, null, true);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param data
     *            сохраняемый текст в виде массива байтов.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param isAppend
     *            дозапись? если <code>true</code> то информация в файл будет дозаписанна, иначе файл будет перезаписан.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(byte[] data, String dir, String name, boolean isAppend) {
        return saveExprToFile(data, dir, name, null, true);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param data
     *            сохраняемый текст в виде массива байтов.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(byte[] data, String dir, String name, String encoding) {
        return saveExprToFile(data, dir, name, encoding, true);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param data
     *            сохраняемый текст в виде массива байтов.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @param isAppend
     *            дозапись? если <code>true</code> то информация в файл будет дозаписанна, иначе файл будет перезаписан.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(byte[] data, String dir, String name, String encoding, boolean isAppend) {
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        try {
            return saveExprToFile(new String(data, encoding), dir, name, encoding, isAppend);
        } catch (UnsupportedEncodingException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return false;
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param expr
     *            сохраняемый текст.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(String expr, String dir, String name) {
        return saveExprToFile(expr, dir, name, null, true);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param expr
     *            сохраняемый текст.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param isAppend
     *            дозапись? если <code>true</code> то информация в файл будет дозаписанна, иначе файл будет перезаписан.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(String expr, String dir, String name, boolean isAppend) {
        return saveExprToFile(expr, dir, name, null, isAppend);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param expr
     *            сохраняемый текст.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(String expr, String dir, String name, String encoding) {
        return saveExprToFile(expr, dir, name, encoding, true);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param expr
     *            сохраняемый текст.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @param isAppend
     *            дозапись? если <code>true</code> то информация в файл будет дозаписанна, иначе файл будет перезаписан.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(String expr, String dir, String name, String encoding, boolean isAppend) {
        boolean res = false;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file, isAppend));
            os.write(expr.getBytes(encoding));
            os.close();
            res = true;
        } catch (IOException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    /**
     * Сохранить XML в файл.
     * 
     * @param xml
     *            сохраняемая XML.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return true, в случае успеха.
     */
    public boolean saveXmlToFile(Element xml, String dir, String name, String encoding) {
        boolean res = false;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name + ".xml");
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            org.jdom.output.Format ft = org.jdom.output.Format.getPrettyFormat();
            ft.setEncoding(encoding);
            XMLOutputter opr = new XMLOutputter(ft);
            xml.detach();
            opr.output(new Document(xml), os);
            os.close();
            res = true;
        } catch (IOException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    /**
     * Получить XML из файла.
     * 
     * @param dir
     *            директория хранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return объект XML
     * @throws Exception
     *             the exception
     */
    public Element getXmlFromFile(String dir, String name, String encoding) throws Exception {
        Element e;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name);
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is, encoding);
        e = doc == null ? null : doc.getRootElement();
        return e;
    }

    /**
     * Сохранить изображение из базы данных в файл.
     * 
     * @param object
     *            Krn-объект базы данных, в котором находится изображение
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param langId
     *            язык данных, для ображения к базе данных
     * @return true, в случае успеха
     */
    public boolean saveImageToFile(KrnObject object, String dir, String name, long langId) {
        KrnObject proc = object.getKrnObject();
        boolean res = false;
        try {
            KrnClass cls = session.getClassById(proc.classId);
            KrnAttribute attr_d = session.getAttributeByName(cls, "diagram");
            KrnAttribute attr_msg = session.getAttributeByName(cls, "message");
            byte[] data_ = session.getBlob(proc.id, attr_d.id, 0, 0, 0);
            byte[] msg_ = session.getBlob(proc.id, attr_msg.id, 0, langId, 0);
            ServiceModel model = new ServiceModel(false, proc, langId);
            JGraph graph = new JGraph(model);
            graph.getEditor().setElementsSelectable(false);
            try {
                InputStream is = new ByteArrayInputStream(data_);
                InputStream is_msg = msg_.length > 0 ? new ByteArrayInputStream(msg_) : null;
                model.loads(is, is_msg, graph);
                is.close();
            } catch (Exception e) {
                SecurityContextHolder.getLog().error(e, e);
            }
            File fileDir = new File(dir);
            fileDir.mkdirs();
            File file = new File(fileDir, name + ".jpeg");
            try {
                graph.addNotify();
                graph.setVisible(true);
                Globals.curEditor(graph.getEditor());
                CmdSaveGraphics cmd = new CmdSaveGraphics("SaveAsJPEG") {
                    protected void saveGraphics(OutputStream os, Editor editor, Rectangle rect) throws IOException {
                        BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
                        Graphics g = img.createGraphics();
                        g.setColor(Color.white);
                        g.fillRect(0, 0, rect.width, rect.height);
                        g.translate(-rect.x, -rect.y);
                        editor.print(g);
                        g.dispose();
                        ImageIO.write(img, "jpeg", os);
                    }
                };
                OutputStream os = new FileOutputStream(file);
                cmd.setStream(os);
                cmd.doIt();
                os.close();
                res = true;
            } catch (IOException e) {
                SecurityContextHolder.getLog().error(e, e);
            }
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    /**
     * Удаляет файл..
     * 
     * @param path
     *            путь к файлу
     */
    public boolean deleteFile(String path) {
        boolean res = false;
        File file = new File(path);
        if(file.exists()) {
            res = file.delete();
        }
        return res;
    }
    /**
     * Создание XML из списка Krn-объектов..
     * 
     * @param objs
     *            список объектов для обработки
     * @param cf
     *            это объект специального класса <b>"Описание процесса сбора данных"</b>, описывающий выгрузку данных.
     *            Этот класс в свою очередь задействует еще два класса <b>"Описание проц сбора дан по классу"</b>, <b>"Описание проц сбора дан по атрибуту"</b>.
     *            С помощью этих классов можно настроить любой сложности выгрузку. для них есть специальные интерфейсы.
     * @param langs
     *            список языков для обработки
     * @return полученный список XML элементов
     * @throws Exception
     *             the exception
     */
    public List<Element> createXml(List<KrnObject> objs, KrnObject cf, List<KrnObject> langs) throws Exception {
        return createXml(objs, cf, langs, false);
    }

    /**
     * Создание XML из списка Krn-объектов.
     * 
     * @param objs
     *            список объектов для обработки
     * @param cf
     *            это объект специального класса <b>"Описание процесса сбора данных"</b>, описывающий выгрузку данных.
     *            Этот класс в свою очередь задействует еще два класса <b>"Описание проц сбора дан по классу"</b>, <b>"Описание проц сбора дан по атрибуту"</b>.
     *            С помощью этих классов можно настроить любой сложности выгрузку. для них есть специальные интерфейсы.
     * @param langs
     *            список языков для обработки
     * @param debug
     *            отладка?
     * @return полученный список XML элементов
     * @throws Exception
     *             the exception
     */
    public List<Element> createXml(List<KrnObject> objs, KrnObject cf, List<KrnObject> langs, boolean debug) throws Exception {
        Map<Long, Integer> clsMap = new HashMap<Long, Integer>();
        Map<Long, Integer> attrMap = new HashMap<Long, Integer>();
        if (cf != null) {
            fillCfMaps(cf.getKrnObject(), clsMap, attrMap);
        }
        final long tid = session.getContext().trId;
        List<Element> res = new ArrayList<Element>();
        for (KrnObject obj : objs) {
            res.add(createXml(obj.getKrnObject(), false, clsMap, attrMap, langs, tid, debug));
        }
        return res;
    }

    /**
     * Создание XML элемента из Krn-объекта.
     * 
     * @param obj
     *            объект для обработки.
     * @param ref
     *            нет атрибутов?
     * @param clsMap
     *            карта выгрузки классов по классу <b>"Описание проц сбора дан по классу"</b>.
     * @param attrMap
     *            карта выгрузки атрибутов по классу <b>"Описание проц сбора дан по атрибуту"</b>.
     * @param langs
     *            список языков для обработки
     * @param tid
     *            id транзакциb, в которой происходит обработка.
     * @param debug
     *            отладка?
     * @return полученный XML элемент
     * @throws Exception
     *             the exception
     */
    public Element createXml(KrnObject obj, boolean ref, Map<Long, Integer> clsMap, Map<Long, Integer> attrMap,
            List<KrnObject> langs, long tid, boolean debug) throws Exception {
        Element res = null;
        long[] ids = { obj.id };
        KrnClass cls = session.getClassById(obj.classId);
        if (ref) {
            res = new Element("ref");
        } else {
            res = new Element("object");
            Integer clsFlags = clsMap.get(cls.id);
            if (clsFlags == null || (clsFlags.intValue() & 1) == 0) {
                boolean exclude = (clsFlags != null && (clsFlags & 2) > 0);
                KrnAttribute[] attrs = session.getAttributes(cls);
                for (KrnAttribute attr : attrs) {
                    Integer attrFlags = attrMap.get(attr.id);
                    if ((attrFlags == null && !exclude) || (attrFlags != null && (attrFlags & 1) == 0)) {
                        ref = (attrFlags != null && (attrFlags & 2) > 0) ? false : true;
                        if (attr.isMultilingual) {
                            for (KrnObject lang : langs) {
                                SortedSet<Value> vs = session.getValues(ids, attr.id, lang.getId(), tid);
                                res.addContent(createAttrXml(attr, vs, lang, tid, ref, clsMap, attrMap, langs, exclude, debug));
                            }
                        } else {
                            SortedSet<Value> vs = session.getValues(ids, attr.id, 0, tid);
                            res.addContent(createAttrXml(attr, vs, null, tid, ref, clsMap, attrMap, langs, exclude, debug));
                        }
                    }
                }
            }
        }
        res.setAttribute("uid", obj.uid);
        res.setAttribute("class", cls.name);
        return res;
    }

    /**
     * Creates the attr xml.
     * 
     * @param attr
     *            the attr
     * @param vs
     *            the vs
     * @param lang
     *            the lang
     * @param tid
     *            the tid
     * @param ref
     *            the ref
     * @param clsMap
     *            the cls map
     * @param attrMap
     *            the attr map
     * @param langs
     *            the langs
     * @param exclude
     *            the exclude
     * @param debug
     *            the debug
     * @return element
     * @throws Exception
     *             the exception
     */
    private Element createAttrXml(KrnAttribute attr, SortedSet<Value> vs, KrnObject lang, long tid, boolean ref,
            Map<Long, Integer> clsMap, Map<Long, Integer> attrMap, List<KrnObject> langs, boolean exclude, boolean debug)
            throws Exception {
        Element attrXml = new Element("attr");
        attrXml.setAttribute("name", attr.name);
        if (lang != null) {
            attrXml.setAttribute("lang", lang.getUID());
        }
        if (vs == null || vs.size() == 0) {
            attrXml.setAttribute("empty", "true");
        } else {
            for (Value v : vs) {
                Element valueXml = null;
                if (attr.typeClassId == CID_STRING || attr.typeClassId == CID_MEMO) {
                    valueXml = new Element("string");
                    if (v.value != null) {
                        valueXml.setText((String) v.value);
                    }
                } else if (attr.typeClassId == CID_INTEGER) {
                    valueXml = new Element("long");
                    if (v.value != null) {
                        valueXml.setText(v.value.toString());
                    }
                } else if (attr.typeClassId == CID_TIME) {
                    valueXml = new Element("timestamp");
                    if (v.value != null) {
                        valueXml.setText(xmlTimeFmt.format((Date) v.value));
                    }
                } else if (attr.typeClassId == CID_DATE) {
                    valueXml = new Element("date");
                    if (v.value != null) {
                        valueXml.setText(xmlDateFmt.format((Date) v.value));
                    }
                } else if (attr.typeClassId == CID_BOOL) {
                    valueXml = new Element("bool");
                    if (v.value != null) {
                        valueXml.setText(((Boolean) v.value) ? "true" : "false");
                    }
                } else if (attr.typeClassId == CID_FLOAT) {
                    valueXml = new Element("float");
                    if (v.value != null) {
                        valueXml.setText(v.value.toString());
                    }
                } else if (attr.typeClassId == CID_BLOB) {
                    valueXml = new Element("blob");
                    if (v.value != null) {
                        valueXml.setText(Base64.encodeBytes((byte[]) v.value));
                    }
                } else if (attr.typeClassId >= 99) {
                    if (v.value != null) {
                        valueXml = createXml((KrnObject) v.value, ref, clsMap, attrMap, langs, tid, debug);
                    } else {
                        valueXml = new Element("ref");
                    }
                }
                valueXml.setAttribute("i", "" + v.index);
                if (v.value == null) {
                    valueXml.setAttribute("null", "true");
                }
                attrXml.addContent(valueXml);
            }
        }
        return attrXml;
    }

    /**
     * Fill cf maps.
     * 
     * @param cf
     *            the cf
     * @param clsMap
     *            the cls map
     * @param attrMap
     *            the attr map
     * @throws Exception
     *             the exception
     */
    private void fillCfMaps(KrnObject cf, Map<Long, Integer> clsMap, Map<Long, Integer> attrMap) throws Exception {

        KrnClass cfCls = session.getClassByName("Описание процесса сбора данных");
        KrnAttribute clsCfAttr = session.getAttributeByName(cfCls, "описание класса");

        KrnClass clsCfCls = session.getClassByName("Описание проц сбора дан по классу");
        KrnAttribute clsNameAttr = session.getAttributeByName(clsCfCls, "наименование");
        KrnAttribute attrCfAttr = session.getAttributeByName(clsCfCls, "описание атрибута");
        KrnAttribute clsExcludeAttr = session.getAttributeByName(clsCfCls, "исключить");
        KrnAttribute clsDefExcludeAttr = session.getAttributeByName(clsCfCls, "по умолчанию исключать");

        KrnClass attrCfCls = session.getClassByName("Описание проц сбора дан по атрибуту");
        KrnAttribute attrNameAttr = session.getAttributeByName(attrCfCls, "наименование");
        KrnAttribute attrExcludeAttr = session.getAttributeByName(attrCfCls, "исключить");
        KrnAttribute attrDeepAttr = session.getAttributeByName(attrCfCls, "глубокое копирование");

        final long[] fids = new long[0];
        final long tid = session.getContext().trId;

        // ограничения классов
        KrnObject[] clsCfs = session.getObjects(cf.id, clsCfAttr.id, fids, tid);
        long[] ids = Funcs.makeObjectIdArray(clsCfs);
        // имена классов
        Map<Long, KrnClass> clsByClsCf = new HashMap<Long, KrnClass>();
        SortedSet<Value> vs = session.getValues(ids, clsNameAttr.id, 0, tid);
        for (Value v : vs) {
            KrnClass cls = session.getClassByName((String) v.value);
            clsByClsCf.put(v.objectId, cls);
        }

        // исключения классов
        vs = session.getValues(ids, clsExcludeAttr.id, 0, tid);
        for (Value v : vs) {
            if ((Boolean) v.value) {
                KrnClass cls = clsByClsCf.get(v.objectId);
                clsMap.put(cls.id, 1);
            }
        }

        // исключения по умолчанию классов
        vs = session.getValues(ids, clsDefExcludeAttr.id, 0, tid);
        for (Value v : vs) {
            if ((Boolean) v.value) {
                KrnClass cls = clsByClsCf.get(v.objectId);
                Integer i = clsMap.get(cls.id);
                i = (i != null) ? i & 2 : 2;
                clsMap.put(cls.id, i);
            }
        }

        // ограничения атрибутов
        SortedSet<Value> attrCfs = session.getValues(ids, attrCfAttr.id, 0, tid);
        Map<Long, KrnClass> clsByAttrCf = new HashMap<Long, KrnClass>();
        ids = new long[attrCfs.size()];
        int i = 0;
        for (Value v : attrCfs) {
            KrnObject o = (KrnObject) v.value;
            clsByAttrCf.put(o.id, clsByClsCf.get(v.objectId));
            ids[i++] = o.id;
        }

        // имена атрибутов
        Map<Long, KrnAttribute> attrByCf = new HashMap<Long, KrnAttribute>();
        vs = session.getValues(ids, attrNameAttr.id, 0, tid);
        for (Value v : vs) {
            KrnClass cls = clsByAttrCf.get(v.objectId);
            KrnAttribute attr = session.getAttributeByName(cls, (String) v.value);
            attrByCf.put(v.objectId, attr);
            attrMap.put(attr.id, 0);
        }

        // исключения атрибутов
        vs = session.getValues(ids, attrExcludeAttr.id, 0, tid);
        for (Value v : vs) {
            if ((Boolean) v.value) {
                KrnAttribute attr = attrByCf.get(v.objectId);
                attrMap.put(attr.id, 1);
            }
        }

        // глубокое копирование атрибутов
        vs = session.getValues(ids, attrDeepAttr.id, 0, tid);
        for (Value v : vs) {
            if ((Boolean) v.value) {
                KrnAttribute attr = attrByCf.get(v.objectId);
                attrMap.put(attr.id, 2);
            }
        }
    }

    /**
     * Разбирать xml для того чтобы залить все данные в ней содержащиеся в БД.
     * Парсит XML созданные методом <code>createXml</code>.
     * При этом метод <code>parseXml</code> создает все объекты, содержащиеся в XML и которых нет в БД.
     * Если они уже есть в БД, то метод обновляет их атрибуты.
     * Эта функция используется при различных взаимодействиях систем, обмене данными.
     * 
     * @param fileName
     *            полное имя файла с XML.
     * @return список созданных/обновлённых объектов.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> parseXml(String fileName) throws Exception {
        return parseXml(fileName, false);
    }

    /**
     * Разбирать xml для того чтобы залить все данные в ней содержащиеся в БД.
     * Парсит XML созданные методом <code>createXml</code>.
     * При этом метод <code>parseXml</code> создает все объекты, содержащиеся в XML и которых нет в БД.
     * Если они уже есть в БД, то метод обновляет их атрибуты.
     * Эта функция используется при различных взаимодействиях систем, обмене данными.
     * 
     * @param fileName
     *            полное имя файла с XML.
     * @param restoreDeleted
     *            восстанавливать удалённые?
     * @return список созданных/обновлённых объектов.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> parseXml(String fileName, boolean restoreDeleted) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File(fileName));
        return parseXml(doc.getRootElement(), false);
    }

    /**
     * Разбирать xml для того чтобы залить все данные в ней содержащиеся в БД.
     * Парсит XML созданные методом <code>createXml</code>.
     * При этом метод <code>parseXml</code> создает все объекты, содержащиеся в XML и которых нет в БД.
     * Если они уже есть в БД, то метод обновляет их атрибуты.
     * Эта функция используется при различных взаимодействиях систем, обмене данными.
     * 
     * @param xml
     *            объект XML для парсинга.
     * @return список созданных/обновлённых объектов.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> parseXml(Element xml) throws Exception {
        return parseXml(xml, false);
    }

    /**
     * Разбирать xml для того чтобы залить все данные в ней содержащиеся в БД.
     * Парсит XML созданные методом <code>createXml</code>.
     * При этом метод <code>parseXml</code> создает все объекты, содержащиеся в XML и которых нет в БД.
     * Если они уже есть в БД, то метод обновляет их атрибуты.
     * Эта функция используется при различных взаимодействиях систем, обмене данными.
     * 
     * @param xml
     *            объект XML для парсинга.
     * @param restoreDeleted
     *            восстанавливать удалённые?
     * @return список созданных/обновлённых объектов.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> parseXml(Element xml, boolean restoreDeleted) throws Exception {
        final long tid = session.getContext().trId;
        Map<String, KrnObject> lbObjs = createObjects(xml, tid, restoreDeleted);
        List<Element> objTags = xml.getChildren();
        int sz = objTags.size();
        List<KrnObject> res = new ArrayList<KrnObject>(sz);
        int i = 0;
        int step = sz / 100;
        for (Element e : objTags) {
            res.add(parseObjectXml(e, tid, lbObjs, restoreDeleted));
            if (step > 0 && ++i % step == 0) {
                SecurityContextHolder.getLog().info((i / step) + "%");
            }
        }
        return res;
    }

    /**
     * Parses the object xml.
     * 
     * @param xml
     *            the xml
     * @param tid
     *            the tid
     * @param lbObjs
     *            the lb objs
     * @return krn object
     * @throws Exception
     *             the exception
     */
    private KrnObject parseObjectXml(Element xml, long tid, Map<String, KrnObject> lbObjs) throws Exception {
        return parseObjectXml(xml, tid, lbObjs, false);
    }

    /**
     * Parses the object xml.
     * 
     * @param xml
     *            the xml
     * @param tid
     *            the tid
     * @param lbObjs
     *            the lb objs
     * @param restoreDeleted
     *            the restore deleted
     * @return krn object
     * @throws Exception
     *             the exception
     */
    private KrnObject parseObjectXml(Element xml, long tid, Map<String, KrnObject> lbObjs, boolean restoreDeleted)
            throws Exception {
        String uid = xml.getAttributeValue("uid");
        KrnObject obj = session.getDirtyObjectByUid(uid, tid);
        if (obj == null)
            throw new KrnException(ErrorCodes.OBJECT_NOT_FOUND, "Объект с uid=" + uid + " не найден.");
        if (session.isDeleted(obj)) {
            SecurityContextHolder.getLog().info("Object is deleted! [" + uid + "]");
            return null;
        }
        KrnClass cls = session.getClassById(obj.classId);
        long[] ids = { obj.id };
        List<Element> attrTags = xml.getChildren();
        for (Element attrTag : attrTags) {
            KrnAttribute attr = null;
            String attrId = attrTag.getAttributeValue("id");
            if (attrId != null) {
                attr = session.getAttributeById(Long.parseLong(attrId));
            } else {
                attr = session.getAttributeByName(cls, attrTag.getAttributeValue("name"));
            }
            if (attr == null) {
                continue;
            }
            if (attr.id == session.getAttributeByName(session.getClassByName("Объект"), "deleting").id) {
                session.deleteObject(obj, 0);
                break;
            }
            String langUid = attrTag.getAttributeValue("lang");
            long langId = langUid != null ? session.getObjectByUid(langUid, tid).id : 0;
            if (langUid != null && langUid.equals("1.733"))
                continue;

            SortedSet<Value> vs = null;
            if ("true".equals(attrTag.getAttributeValue("empty"))) {
                // Считываем значения атрибута для дальнейшего удаления
                vs = session.getValues(ids, attr.id, langId, tid);
            } else {
                List<Element> valueTags = attrTag.getChildren();
                // Сначала распарсиваем все вложенные объекты
                for (Element valueTag : valueTags) {
                    if ("object".equals(valueTag.getName())) {
                        parseObjectXml(valueTag, tid, lbObjs);
                    }
                }
                // Распарсиваем значения атрибута
                // Считываем значения атрибута после распарсивания вложенных
                // объектов, т.к. возможны изменения через обратные атрибуты
                vs = session.getValues(ids, attr.id, langId, tid);
                for (Element valueTag : valueTags) {
                    int ind = Integer.parseInt(valueTag.getAttributeValue("i"));
                    String valueTagName = valueTag.getName();
                    Object value = null;
                    if (!"true".equals(valueTag.getAttributeValue("null"))) {
                        if ("string".equals(valueTagName)) {
                            value = valueTag.getText();
                        } else if ("long".equals(valueTagName)) {
                            value = new Long(valueTag.getText());
                        } else if ("timestamp".equals(valueTagName)) {
                            value = new java.sql.Timestamp(xmlTimeFmt.parse(valueTag.getText()).getTime());
                        } else if ("date".equals(valueTagName)) {
                            value = new java.sql.Date(xmlDateFmt.parse(valueTag.getText()).getTime());
                        } else if ("bool".equals(valueTagName)) {
                            value = new Boolean(valueTag.getText());
                        } else if ("float".equals(valueTagName)) {
                            value = new Double(valueTag.getText());
                        } else if ("blob".equals(valueTagName)) {
                            value = Base64.decode(valueTag.getText());
                        } else if ("ref".equals(valueTagName) || "object".equals(valueTagName)) {
                            String vuid = valueTag.getAttributeValue("uid");
                            if (vuid != null) {
                                try {
                                    KrnObject vobj = session.getDirtyObjectByUid(vuid, tid);
                                    value = vobj;
                                } catch (KrnException ex) {
                                    SecurityContextHolder.getLog().error(ex.getMessage());
                                }
                            } else {
                                String label = valueTag.getAttributeValue("label");
                                value = lbObjs.get(label);
                            }
                        }
                    }
                    if (value != null) {
                        boolean found = false;
                        for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                            Value v = it.next();
                            if (attr.collectionType == COLLECTION_SET) {
                                if (valuesEquals(value, v.value)) {
                                    found = true;
                                    it.remove();
                                    break;
                                }
                            } else if (v.index == ind) {
                                if (valuesEquals(value, v.value)) {
                                    found = true;
                                }
                                it.remove();
                                break;
                            }
                        }
                        if (!found) {
                            if (value instanceof KrnObject) {
                                value = ((KrnObject) value).id;
                            }
                            try{
                            	session.setValue(obj.id, attr.id, ind, langId, value, tid, false);
                            }catch(Throwable tex){
                            	System.out.println("Ошибка при заливке объекта:obj.id="+obj.id+";attr.id="+attr.id+";value="+Funcs.sanitizeHtml(value.toString()));
                            	tex.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (!vs.isEmpty()) {
                deleteValues(obj.id, attr, vs, tid);
            }

        }
        return obj;
    }

    /**
     * Values equals.
     * 
     * @param v1
     *            the v1
     * @param v2
     *            the v2
     * @return true, в случае успеха
     */
    private boolean valuesEquals(Object v1, Object v2) {
        if (v1 instanceof KrnObject && v2 instanceof KrnObject)
            return ((KrnObject) v1).uid.equals(((KrnObject) v2).uid);
        else if (v1 != null)
            return v1.equals(v2);
        else
            return v2 == null;
    }

    /**
     * Delete values.
     * 
     * @param objId
     *            the obj id
     * @param attr
     *            the attr
     * @param vs
     *            the vs
     * @param tid
     *            the tid
     * @throws KrnException
     *             the krn exception
     */
    private void deleteValues(long objId, KrnAttribute attr, SortedSet<Value> vs, long tid) throws KrnException {
        if (attr.collectionType == COLLECTION_SET) {
            List<Object> vals = new ArrayList<Object>(vs.size());
            for (Value v : vs) {
                vals.add(v.value);
            }
            session.deleteValue(objId, attr.id, vals, tid);
        } else {
            int[] inds = new int[vs.size()];
            int i = 0;
            for (Value v : vs) {
                inds[i++] = v.index;
            }
            session.deleteValue(objId, attr.id, inds, 0, tid);
        }
    }

    /**
     * Creates the objects.
     * 
     * @param xml
     *            the xml
     * @param tid
     *            the tid
     * @return map
     * @throws Exception
     *             the exception
     */
    private Map<String, KrnObject> createObjects(Element xml, long tid) throws Exception {
        return createObjects(xml, tid, false);
    }

    /**
     * Creates the objects.
     * 
     * @param xml
     *            the xml
     * @param tid
     *            the tid
     * @param restoreDeleted
     *            the restore deleted
     * @return map
     * @throws Exception
     *             the exception
     */
    private Map<String, KrnObject> createObjects(Element xml, long tid, boolean restoreDeleted) throws Exception {

        Map<String, KrnObject> lbObjs = new HashMap<String, KrnObject>();
        List<Element> objTags = XPath.selectNodes(xml, "//object");
        for (Element e : objTags) {
            String uid = e.getAttributeValue("uid");
            String label = e.getAttributeValue("label");
            KrnObject obj = null;
            if (uid != null) {
                try {
                    obj = session.getDirtyObjectByUid(uid, tid);
                    // if (restoreDeleted && session.isDeleted(obj.id))
                    // restoreObject(uid);
                    if (label != null) {
                        lbObjs.put(label, obj);
                    }
                } catch (KrnException ex) {
                    SecurityContextHolder.getLog().error(ex);
                }
            }
            if (obj == null) {
                try {
                    KrnClass cls = null;
                    String str = e.getAttributeValue("class");
                    try {
                        long clsId = Long.parseLong(str);
                        cls = session.getClassById(clsId);
                    } catch (NumberFormatException ne) {
                        cls = session.getClassByName(str);
                    }
                    if (uid == null) {
                        obj = session.createObject(cls, tid);
                        e.setAttribute("uid", obj.uid);
                    } else {
                        obj = session.createObjectWithUid(cls, uid, tid);
                    }
                    if (label != null) {
                        lbObjs.put(label, obj);
                    }
                } catch (KrnException ee) {
                    SecurityContextHolder.getLog().error("not found " + e.getAttributeValue("class"));
                    throw new KrnException(ee.code, ee.getMessage());
                }
            }
        }
        return lbObjs;
    }

    /**
     * Получить массив файлов из указанной директории.
     * 
     * @param dirName
     *            директория для поиска файлов
     * @return массив файлов, полученный из директории
     * @throws Exception
     *             the exception
     */
    public File[] getListFiles(String dirName) throws Exception {
        File[] files = null;
        try {
            File fileDir = new File(dirName);
            if (fileDir.isDirectory()) {
                files = fileDir.listFiles();
            }
        } catch (Exception e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return files;
    }

    /**
     * Получить XML из файла.
     * 
     * @param fileName
     *            полное имя файла.
     * @return объект XML
     * @throws Exception
     *             the exception
     */
    public Element loadFileToXml(String fileName) throws Exception {
        Element xml = null;
        try {
            File file = new File(fileName);
            if (!file.isDirectory()) {
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(is);
                is.close();
                xml = doc.getRootElement();
            }
        } catch (Exception e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return xml;
    }

    /**
     * Загрузка XML из массива байтов xml-файла.
     * 
     * @param buf
     *            массив байтов xml-файла.
     * @throws Exception
     *             the exception
     */
    public void load(byte[] buf) throws Exception {
         try {
     		session.setLoadingFile(true);
     		SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new ByteArrayInputStream(buf), "UTF-8");
            Element e = doc.getRootElement();
	        parseXml(e, false);
 	        SecurityContextHolder.getLog().info("loading completed.");
     	} finally {
     		session.setLoadingFile(false);
     	}
    }
    
    /**
     * Загрузка XML для разбора из файла.
     * 
     * @param fileName
     *            полное имя файла.
     * @throws Exception
     *             the exception
     */
    public void load(String fileName) throws Exception {
        load(fileName, false);
    }

    /**
     * Загрузка XML для разбора из файла.
     * 
     * @param fileName
     *            полное имя файла.
     * @param restoreDeleted
     *            восстанавливать удалённые?
     * @throws Exception
     *             the exception
     */
    public void load(String fileName, boolean restoreDeleted) throws Exception {
    	try {
    		session.setLoadingFile(true);
    		XmlOp xml = new SrvXml(session);
	        File fileDir = new File(fileName);
	        if (fileDir.isDirectory()) {
	            String dir = fileDir.getPath();
	            File[] files = fileDir.listFiles();
	            for (int i = 0; i < files.length; i++) {
	                File file = files[i];
	                Element e = xml.load(dir + "/" + file.getName());
	                parseXml(e, restoreDeleted);
	                session.commitTransaction();
	                SecurityContextHolder.getLog().info((i * 100 / files.length) + "% " + file.getName());
	            }
	        } else {
	            Element e = xml.load(fileName);
	            parseXml(e, restoreDeleted);
	        }
	        SecurityContextHolder.getLog().info("loading completed.");
    	} finally {
    		session.setLoadingFile(false);
    	}
    }

    /**
     * Обновляет связи обратных атрибутов.
     * 
     * @param cid
     *            the cid
     * @param aid
     *            the aid
     * @deprecated Эта функция устарела, так как раньше обратные атрибуты работали по-другому.
     *             Сейчас обратным атрибутом является просто ссылка (на самом деле в реляционной таблице не создается колонка).
     *             А раньше для обратного атрибута создавали отдельный атрибут и туда сохранялся обратный объект.
     *             И время от времени этот объект отцеплялся от своего обратного объекта и, таким образом, происходила
     *             рассинхронизации данных, что приводила к ошибкам в работе Системы. Чтобы это дело исправлять нужна была такая функция,
     *             которая восстанавливает связи.
     *             В общем, она теперь не нужна. Но в системах некоторых заказчиках еще используется старый метод обратных связей,
     *             которые, по-хорошему, надо бы постепенно перевести на новый метод. И тогда надобность этой функции совсем отпадет.
     */
    public void updateReferences(long cid, long aid) {
        try {
            session.updateReferences(cid, aid);
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
    }

    /**
     * Возвращает состояние заданного транспорта.
     * 
     * Каждый вид транспорта пронумерован.
     * Например, «Электронная папка» - 1, «эл. почта» - 2 и т.д (они в списке выбора транспорта по номеру и отсортированы – раздел «Обмен» в дизайнере).
     * Загрузка транспорта настраивается в конфигурационном файле <i>or3-transport.properties</i>, расположение которого определяет параметр <i>tpropsFiles</i> в <i>web.xml</i>
     * Например, чтобы включить транспорт «Электронная папка» в файле нужно параметр <code>Dir_ready</code> прописать следующим образом:
     * 
     * <pre>
     * Dir_ready = yes
     * </pre>
     * 
     * @param transportId
     *            номер транспорта.
     * @return <code>true</code>, если транспорт загружен.
     */
    public boolean isTransportReady(int transportId) {
        try {
            byte[] data = session.getTransportParam(transportId);
            if (data != null && data.length > 0) {
                try {
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                    Element root = doc.getRootElement();
                    Element param = root.getChild("ready");
                    return "true".equals(param.getText());
                } catch (IOException e) {
                    SecurityContextHolder.getLog().error(e, e);
                } catch (JDOMException e) {
                    SecurityContextHolder.getLog().error(e, e);
                }
            }
        } catch (KrnException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return false;
    }

    /**
     * Получить Krn-объект текущего процесса.
     * 
     * @return Krn-объект текущего процесса
     * @throws Exception
     *             the exception
     */
    public KrnObject getFlowObject() throws Exception {
        Context ctx = session.getContext();
        return session.getObjectById(ctx.flowId, 0);
    }

    /**
     * Задать инициатора процессу..
     * 
     * @param user
     *            новое значение инициатора - Krn-объект пользователь
     * @throws Exception
     *             the exception
     */
    public void setProcessInitiator(KrnObject user) throws Exception {
        Context ctx = session.getContext();
        session.setProcessInitiator(ctx.flowId, user.id);
    }

    /**
     * Продвижение процесса на следующий шаг.
     * 
     * @param flowId
     *            номер процесса, который следует продвинуть.
     * @param args
     *            список, содержащий значения, которые будут использоваться внутри процесса.
     *            Доступ к этим значения изнутри процесс выполняется через переменную <code>$ARGS</code>.
     *            Этих параметров может и не быть. По умолчанию он пустой.
     * @return результат выполнения метода, <b>0</b> если выполненн успешно.
     */
    public long performActivity(long flowId, Object args) {
        try {
            return session.getExeComp(session.getDsName()).performActivity(flowId, args, session);
        } catch (WorkflowException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return 0;
    }

    /**
     * Создать запрос.
     * 
     * @param path
     *            путь к классу
     * @param lang
     *            язык
     * @return объект <code>SrvQuery</code>
     * @throws KrnException
     *             the krn exception
     */
    public SrvQuery createQuery(String path, KrnObject lang) throws KrnException {
        return createQuery(path, lang, session.getContext().trId);
    }

    public SrvQuery createQuery(String path, KrnObject lang, long tid) throws KrnException {
        return new SrvQuery(path, lang, session, tid);
    }

    public SrvQuery createQuery(String path) throws KrnException {
    	return createQuery(path, null);
    }

    public SrvQuery createQuery(String path, long tid) throws KrnException {
    	return createQuery(path, null, tid);
    }
    public String getCurDirExchange() {
        return session.getCurDirExchange();
    }
	//Jcr repository
	@Override
	public String putRepositoryData(String paths, String fileName, byte[] data) {
    	return session.putRepositoryData(paths, fileName, data);
	}

	@Override
	public byte[] getRepositoryData(String docId) {
    	return session.getRepositoryData(docId);
	}

	@Override
	public String getRepositoryItemName(String docId) throws Exception {
    	return session.getRepositoryItemName(docId);
	}

	@Override
	public String getRepositoryItemType(String docId) throws Exception {
    	return session.getRepositoryItemType(docId);
	}

	@Override
	public boolean dropRepositoryItem(String docId) throws Exception {
    	return session.dropRepositoryItem(docId);
	}
	@Override
	public List<String> searchByQuery(String searchName) throws Exception {
    	return session.searchByQuery(searchName);
	}
}
