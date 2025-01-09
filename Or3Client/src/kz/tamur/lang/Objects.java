package kz.tamur.lang;

import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.parser.LangUtils;
import kz.tamur.util.Funcs;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created by IntelliJ IDEA.
 * Date: 11.01.2005
 * Time: 10:13:16
 * 
 * @author berik
 */
public abstract class Objects {

    /**
     * Получить Krn-класс по его имени.
     * 
     * @param name
     *            имя класса
     * @return Krn-класс
     */
	public abstract KrnClass getClass(String name);

    /**
     * Получить подклассы указанного класса.
     * Если <code>withSubclasses=true</code> то в конечный список классов
     * попадает вся иерархия классов, начиная с родительского.
     * 
     * @param baseClassId
     *            id корневого класса
     * @param withSubclasses
     *            включать подклассы?
     * @return список найденных Krn-классов
     */
	public abstract List<KrnClass> getClasses(long baseClassId, boolean withSubclasses);

    /**
     * Получить атрибуты указанного класса.
     * 
     * @param name
     *            имя класса.
     * @return атрибуты класса.
     */
    public abstract List<KrnAttribute> getClassAttributes(String name);

    /**
     * Получить Krn-класс по его id.
     * 
     * @param id
     *            код класса.
     * @return Krn-класс
     */
    public abstract KrnClass getClassById(Number id);
    
    /**
     * Получить Krn-атрибут из заданного класса.
     * 
     * @param cls
     *            Krn-класс, в котором находиться атрибут.
     * @param name
     *            имя атрибута.
     * @return найденны Krn-атрибут
     */
    public abstract KrnAttribute getAttribute(KrnClass cls, String name);

    /**
     * Получить Krn-атрибут по его id.
     * 
     * @param id
     *            код атрибута.
     * @return найденный Krn-атрибут.
     */
    public abstract KrnAttribute getAttributeById(Number id);

    /**
     * Получить список атрибутов указанного класса.
     * Если <code>inherited=true</code> возвращать все наследуемые атрибуты.
     * 
     * @param cls
     *            класс, атрибуты которого необходимо получить.
     * @param inherited
     *            брать наследуемые?
     * @return список найденных атрибутов.
     * @throws KrnException
     *             the krn exception
     */
    public abstract List<KrnAttribute> getAttributesByType(KrnClass cls, boolean inherited) throws KrnException;

    /**
     * Найти в БД объект с идентификатором <code>uid</code>.
     * Пример:
     * 
     * <pre>
     * $obj = $Objects.getObject(“17821.30884”)
     * </pre>
     * 
     * @param uid
     *            номер объекта.
     * @return найденный объект или <code>null</code>
     */
    public abstract KrnObject getObject(String uid);

    /**
     * Найти в БД объект с номером <code>id</code>.
     * Пример:
     * 
     * <pre>
     * $obj = $Objects.getObject(30884)
     * </pre>
     * 
     * @param id
     *            номер объекта.
     * @return найденный объект или <code>null</code>
     */
    public abstract KrnObject getObject(Number id);

    /**
     * Создать объект в классе с именем <code>className</code>.
     * Пример:
     * 
     * <pre>
     * $person = $Objects.createObject(“Персонал”)
     * </pre>
     * 
     * @param className
     *            имя класса, экземпляром которого будет созданный объект.
     * @return созданный объект.
     */
    public abstract KrnObject createObject(String className);

    /**
     * Копировать объект.
     * 
     * @param obj
     *            копируемый объект.
     * @return копия объекта.
     */
    public abstract KrnObject cloneObject(KrnObject obj);

    /**
     * Получить объекты заданного класса.
     * Пример:
     * 
     * <pre>
     * $list1 = $Objects.getClassObjects(“Персонал”)
     * </pre>
     * 
     * @param className
     *            имя класса.
     * @return объекты класса.
     */
    public abstract List<KrnObject> getClassObjects(String className);

    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра.
     * 
     * @param filter
     *            применяемый фильтр.
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> filter(KrnObject filter) throws Exception {
        return filter(filter, 0);
    }

    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра.
     * 
     * @param filter
     *            применяемый фильтр.
     * @param limit
     *            органичение количества полученных значений.
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> filter(KrnObject filter, int limit) throws Exception {
        return filter(filter, limit,-1,-1);
    }

    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра.
     * 
     * @param filter
     *            применяемый фильтр.
     * @param limit
     *            органичение количества полученных значений.
     * @param beginRow
     *            номер строки с которой предоставить данные.
     * @param endRow
     *            номер строки до которой предоставить данные.
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public abstract List<KrnObject> filter(KrnObject filter, int limit,int beginRow,int endRow) throws Exception;
    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра c предустановленными параметрами.
     * 
     * Пример:
     * 
     * <pre>
     * $params = $Objects.createMap()
     * $params.put("%Подразделение", $podr)
     * $list1 = $Objects.filter($Objects.get$Object(“34783.19876”), $params,
     * false)
     * </pre>
     * 
     * @param filter
     *            применяемый фильтр.
     * @param params
     *            объект <code>Map</code> с именами и значениями параметров, используемыми фильтром <code>filter</code>.
     * @param allTransactions
     *            <code>true</code> – применить фильтр ко всем транзакциям (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – применить фильтр только к нулевым транзакциям (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        return filter(filter, params, allTransactions, 0);
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
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions, int limit) throws Exception {
    	   return filter(filter, params, allTransactions, limit, -1, -1);
    }

    /**
     * Найти в БД объекты, удовлетворяющие критериям фильтра c предустановленными параметрами.
     * 
     * Пример:
     * 
     * <pre>
     * $params = $Objects.createMap()
     * $params.put("%Подразделение", $podr)
     * $list1 = $Objects.filter($Objects.get$Object(“34783.19876”), $params,
     * false)
     * </pre>
     * 
     * @param filter
     *            применяемый фильтр.
     * @param params
     *            объект <code>Map</code> с именами и значениями параметров, используемыми фильтром <code>filter</code>.
     * @param allTransactions
     *            <code>true</code> – применить фильтр ко всем транзакциям (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – применить фильтр только к нулевым транзакциям (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @param beginRow
     *            номер строки с которой предоставить данные.
     * @param endRow
     *            номер строки до которой предоставить данные.
     * @return возвращает список объектов удовлетворяющих критериям фильтра.
     * @throws Exception
     *             the exception
     */
    public abstract List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions, int limit, int beginRow, int endRow) throws Exception;

    /**
     * Получить результат аггрегации в фильтре.
     * 
     * @param filter
     *            запускаемый фильтр.
     * @param params
     *            объект <code>Map</code> с именами и значениями параметров, используемыми фильтром <code>filter</code>.
     * @param allTransactions
     *            <code>true</code> – применить фильтр ко всем транзакциям (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – применить фильтр только к нулевым транзакциям (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @return записи с результатом .
     * @throws Exception
     *             the exception
     */
    public abstract List<Object> filterGroup(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception;
    /**
     * Получить результат аггрегации в фильтре.
     * 
     * @param filter
     *            запускаемый фильтр.
     * @return записи с результатом .
     * @throws Exception
     *             the exception
     */
    public abstract List<Object> filterGroup(KrnObject filter) throws Exception;

    /**
     * Получить количество записей выдаваемых фильтром.
     * 
     * @param filter
     *            запускаемый фильтр.
     * @return количество записей.
     * @throws Exception
     *             the exception
     */
    public abstract long filterCount(KrnObject filter) throws Exception;

    /**
     * Получить количество записей выдаваемых фильтром.
     * 
     * @param filter
     *            запускаемый фильтр.
     * @param params
     *            объект <code>Map</code> с именами и значениями параметров, используемыми фильтром <code>filter</code>.
     * @param allTransactions
     *            <code>true</code> – применить фильтр ко всем транзакциям (использовать все объекты,
     *            в том числе и захваченные в обработку процессами). <code>false</code> – применить фильтр только к нулевым транзакциям (использовать все объекты,
     *            исключая объекты, захваченные в обработку процессами).
     * @return количество записей.
     * @throws Exception
     *             the exception
     */
    public abstract long filterCount(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception;

    /**
     * Сортировка списка объекта.
     * 
     * @param objs
     *            сортируемый список объектов.
     * @param path
     *            путь.
     * @return отсортированный список объектов
     * @deprecated не используется.
     */
    public abstract List sort(List objs, String path);

    /**
     * Создать объект <code>Sequence</code> для Krn-объекта с идентификатором <code>uid</code>.
     * 
     * @param uid
     *            идентификатор Krn-объекта.
     * @return объект <code>Sequence</code>
     * @deprecated не используется.
     */
    public abstract Sequence getSequence(String uid);

    /**
     * Найти в БД объект с заданным значением атрибута.
     * Пример:
     * 
     * <pre>
     * $var = $Objects.find(“Юрлицо.БИН”, “009124000321”)
     * </pre>
     * 
     * Оператор <code>find</code> ищет значение атрибута, напрямую связанного с классом.
     * Конструкция <code>className.className.attrName</code> неприемлема.
     * 
     * @param path
     *            шаблон <code>className.attrName</code>, указывается имя класса и атрибут, где производиться поиск.
     * @param value
     *            значение, которое необходимо найти.
     * @return список объектов, подпадающих под условия поиска.
     * @throws Exception
     *             the exception
     */
    public abstract List<KrnObject> find(String path, Object value) throws Exception;

    /**
     * Получить результат SQL запроса.
     * 
     * @param sql
     *            the sql
     * @param vals
     *            the vals
     * @param param
     *            the param
     * @return sql result
     * @deprecated не используется
     */
    public abstract List getSqlResult(String sql, List vals, List param);

    /**
     * Создать объект <code>List</code> – список.
     * Пример:
     * 
     * <pre>
     * $list1 = $Objects.createList()
     * </pre>
     * 
     * @return объект <code>List</code>.
     */
    public List createList() {
        return new ArrayList();
    }

    /**
     * Создать объект <code>List</code> – список и иничиализировать его значениями из коллекции <code>src</code>.
     * 
     * @param src
     *            колелкция для инициализации списка.
     * @return объект <code>List</code>.
     */
    public List createList(Collection src) {
        return new ArrayList(src);
    }

    /**
     * Создать объект <code>Set</code> – набор уникальных элементов.
     * Пример:
     * 
     * <pre>
     * $set1 = $Objects.createSet()
     * </pre>
     * 
     * @return объект <code>Set</code>.
     */
    public Set createSet() {
        return new HashSet();
    }

    /**
     * Создать объект <code>SortedSet</code> – сортируемый набор элементов.
     * 
     * @return объект <code>SortedSet</code>.
     */
    public SortedSet<Object> createSortedSet() {
        return new TreeSet<Object>();
    }

    /**
     * Создать объект <code>Map</code> - ассоциативный массив элементов {ключ, параметр}.
     * Пример:
     * 
     * <pre>
     * $map1 = $Objects.createMap()
     * </pre>
     * 
     * @return созданный объект <code>Map</code>.
     */
    public Map createMap() {
        return new HashMap();
    }

    /**
     * Создать объект <code>SortedMap</code> - ассоциативный сортированный массив элементов {ключ, параметр}.
     * Пример:
     * 
     * <pre>
     * $map = $Objects.createSortedMap()
     * </pre>
     * 
     * @return созданный объект <code>SortedMap</code>.
     */
    public SortedMap<Object, Object> createSortedMap() {
        return new TreeMap<Object, Object>();
    }

    /**
     * Создать массив элементов длинной <code>length</code> и типа <code>componentType</code>.
     * 
     * @param componentType
     *            тип элементов создаваемого массива.
     * @param length
     *            длина массива.
     * @return массив элементов.
     */
    public Object createArray(Class<?> componentType, int length) {
        return Array.newInstance(componentType, length);
    }

    
    /**
     * Создать массив элементов длинной <code>length</code>.
     * 
     * @param length
     *            длина массива.
     * @return массив элементов.
     */
    public Object createArray(int length) {
        return Array.newInstance(java.lang.Object.class, length);
    }

    /**
     * Удалить объект.
     * 
     * @param id
     *            код объекта.
     * @deprecated не используется.
     */
    public abstract void removeObject(int id);

    /**
     * Получить <code>uid</code> объекта по его <code>id</code>.
     * 
     * @param id
     *            код объекта
     * @return uid, идентичикатор объекта.
     */
    public abstract String getUID(int id);

    /**
     * Установить параметры фильтра.
     * 
     * Пример:
     * 
     * <pre>
     * $Objects.setFilterParam($Objects.getObject(“34783.19876”), $params)
     * </pre>
     * 
     * @param filter
     *            фильтр, которому задаются параметры.
     * @param params
     *            задаваемые параметры.
     * @throws Exception
     *             the exception
     */
    public abstract void setFilterParam(KrnObject filter, Map<String, Object> params) throws Exception;

    /**
     * Очистить параметры фильтра.
     * 
     * Пример:
     * 
     * <pre>
     * $Objects.clearFilterParam($Objects.getObject(“34783.19876”))
     * </pre>
     * 
     * @param filter
     *            фильтр, для которого очищаются параметры.
     * @throws KrnException
     *             the krn exception
     */
    public abstract void clearFilterParam(KrnObject filter) throws KrnException;

    /**
     * Получить параметр фильтра.
     * Пример:
     * 
     * <pre>
     * $var=$Objects.getFilterParam($Objects.getObject(“34783.19876”), “%podr”)
     * </pre>
     * 
     * @param fuid
     *            идентификатор фильтра.
     * @param pid
     *            имя параметра.
     * @return список, в котором содержиться найденное значение
     */
    public abstract List getFilterParam(String fuid, String pid);

     /**
     * Создать новый JAVA объект заданного класса.
     * 
     * @param className
     *            имя класса, объект которого необходимо создать.
     * @param arg1
     *            аргумент, передаваемый в конструктор.
     * @param arg2
     *            аргумент, передаваемый в конструктор.
     * @param arg3
     *            аргумент, передаваемый в конструктор.
     * @param arg4
     *            аргумент, передаваемый в конструктор.
     * @return созданный объект.
     * @throws Exception
     *             the exception
     */
    public Object createJavaObject(String className, Object... args) throws Exception {
    	Class<?>[] argTypes = new Class<?>[args.length];
    	for (int i = 0; i < args.length; i++) {
    		argTypes[i] = args[i] != null ? args[i].getClass() : null;
    	}
		Constructor<?> mostSpecCtr = null;
		int minK = Integer.MAX_VALUE;
        Class<?> cls = LangUtils.getType(className, null); 
        outer: for (Constructor<?> ctr : cls.getConstructors()) {
            Class<?>[] parTypes = ctr.getParameterTypes();
            if (parTypes.length == args.length) {
            	for (int i = 0; i < argTypes.length; i++) {
            		if (argTypes[i] != null && !Funcs.isAssignableFrom(parTypes[i], argTypes[i]))
            			continue outer;
            	}
    			int k = 0;
            	for (int i = 0; i < argTypes.length; i++) {
    				int ka = 0;
    				if (argTypes[i] != null) {
    					for (Class<?> argType = argTypes[i]; argType != null && !argType.equals(parTypes[i]); argType = argType.getSuperclass(), ka++);
    				}
    				k += (argTypes.length - i) * ka;
            	}
    			if (minK > k) {
    				minK = k;
    				mostSpecCtr = ctr;
    			}
            }
        }
        if (mostSpecCtr != null) {
	    	switch (argTypes.length) {
				case 0:
					return mostSpecCtr.newInstance();
				case 1:
					return mostSpecCtr.newInstance(args[0]);
				case 2:
					return mostSpecCtr.newInstance(args[0], args[1]);
				case 3:
					return mostSpecCtr.newInstance(args[0], args[1], args[2]);
				case 4:
					return mostSpecCtr.newInstance(args[0], args[1], args[2], args[3]);
			}
		   	return mostSpecCtr.newInstance(args);
        }
        throw new EvalException("Конструктор не найден");
    }

    /**
     * Изменяет порядок элементов во всем списке <code>list</code> на обратный.
     * 
     * @param list
     *            список для обработки.
     */
    public void reverseList(List list) {
        Collections.reverse(list);
    }

    /**
     * Остановка процесса.
     * 
     * @param activity
     *            запущенный процесс.
     * @throws KrnException
     *             the krn exception
     */
    public abstract boolean stopProcess(Activity activity) throws KrnException;
    public abstract boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException;
    
	public JsonObject createJsonObject() {
		return new JsonObject();
	}

	public JsonObject createJsonObject(String jsonStr) {
		return JsonObject.readFrom(jsonStr);
	}

	public JsonArray createJsonArray() {
		return new JsonArray();
	}
	
    public JsonObject sendJson(String url, String method, JsonObject obj) {
    	return sendJson(url, method, obj.toString());
    }
    
    public JsonObject sendJson(String url, String method, JsonObject obj, String user, String pd) {
    	return sendJson(url, method, obj.toString(), user, pd);
    }    
    
    public JsonObject sendJson(String url, String method, Map<String, Object> pars) {
    	return sendJson(url, method, mapToString(pars));
    }
    
    public JsonObject sendJson(String url, String method, Map<String, Object> pars, String user, String pd) {
    	return sendJson(url, method, mapToString(pars), user, pd);
    }
    
    private String mapToString(Map<String, Object> pars) {
    	StringBuilder sb = new StringBuilder();
        for (Iterator<String> keys = pars.keySet().iterator(); keys.hasNext(); ) {
        	String key = keys.next();
        	sb.append(key).append("=").append(pars.get(key));
        	if (keys.hasNext()) sb.append("&");
        }
        return sb.toString();
    }

    public JsonObject sendJson(String url, String method, String params) {
    	return sendJson(url, method, params, null, null);
    }
    
    public JsonObject sendJson(String url, String method, String params, String user, String pd) {
    	try {
    		final HttpParams httpParams = new BasicHttpParams();
    	    HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
    	    HttpClient hc = new DefaultHttpClient(httpParams);
	        HttpResponse r = null;
	    	
	    	if ("post".equalsIgnoreCase(method)) {
	    		HttpPost p = new HttpPost(url);
		        p.addHeader("content-type", "application/json; charset=UTF-8");
		        p.addHeader("Accept", "application/json");
		        if (user != null && pd != null) {
		        	String token = new String(Base64.encode((user + ":" + pd).getBytes()));
		        	p.addHeader("Authorization", "Basic " + token);
		        }
		        p.setEntity(new StringEntity(params, "UTF-8"));
		        r = hc.execute(p);
	    	} else if ("delete".equalsIgnoreCase(method)) {
	    		HttpGet p = new HttpGet(url + ((params != null && params.length() > 0) ? ("?" + params) : ""));
		        p.addHeader("Accept", "application/json; charset=UTF-8");
		        if (user != null && pd != null) {
		        	String token = new String(Base64.encode((user + ":" + pd).getBytes()));
		        	p.addHeader("Authorization", "Basic " + token);
		        }
		        r = hc.execute(p);
	    	} else if ("put".equalsIgnoreCase(method)) {
	    		HttpPut p = new HttpPut(url + "?" + params);
		        p.addHeader("content-type", "application/json; charset=UTF-8");
		        p.addHeader("Accept", "application/json");
		        if (user != null && pd != null) {
		        	String token = new String(Base64.encode((user + ":" + pd).getBytes()));
		        	p.addHeader("Authorization", "Basic " + token);
		        }
		        p.setEntity(new StringEntity(params, "UTF-8"));
		        r = hc.execute(p);
	    	} else {
	    		HttpGet p = new HttpGet(url + ((params != null && params.length() > 0) ? ("?" + params) : ""));
		        p.addHeader("Accept", "application/json");
		        if (user != null && pd != null) {
		        	String token = new String(Base64.encode((user + ":" + pd).getBytes()));
		        	p.addHeader("Authorization", "Basic " + token);
		        }
		        r = hc.execute(p);
	    	}
	
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        
	        r.getEntity().writeTo(bos);
	        bos.close();
	        
	        String res = bos.toString();
	        JsonObject o = null;
	        try {
	        	 o = JsonObject.readFrom(res);
	        } catch (Throwable pe) {
	        	o = new JsonObject();
	        	o.set("result", res);
	        }
	        SecurityContextHolder.getLog().info("sendJson result = " + o.toString());
	        return o;
    	} catch (Exception e) {
    		SecurityContextHolder.getLog().error(e, e);
    		return null;
    	}
    }
	//Jcr repository
    public abstract String putRepositoryData(String paths,String fileName, byte[] data);
    public abstract byte[] getRepositoryData(String docId);
    public abstract String getRepositoryItemName(String docId) throws Exception;
    public abstract String getRepositoryItemType(String docId) throws Exception;
    public abstract boolean dropRepositoryItem(String docId) throws Exception;
    public abstract List<String> searchByQuery(String searchName) throws Exception;
    //
}
