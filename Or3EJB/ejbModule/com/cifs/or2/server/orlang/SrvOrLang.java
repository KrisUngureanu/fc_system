package com.cifs.or2.server.orlang;

import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_FLOAT;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_MEMO;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;
import static kz.tamur.or3ee.common.SessionIds.CID_TIME;
import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.BarcodeOp;
import kz.tamur.lang.DateOp;
import kz.tamur.lang.GlobalFunc;
import kz.tamur.lang.MathOp;
import kz.tamur.lang.Objects;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.StringOp;
import kz.tamur.lang.StringResources;
import kz.tamur.lang.SystemOp;
import kz.tamur.lang.parser.ASTMethod;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.lang.parser.EvaluatorVisitor;
import kz.tamur.lang.parser.LangUtils;
import kz.tamur.lang.parser.Node;
import kz.tamur.ods.Lock;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.or3ee.server.lang.SrvSystemWrp;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.crypto.XmlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnAttributeOperations;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnClassOperations;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnObjectOperations;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.func.Util;

public class SrvOrLang extends OrLang {

    private SrvSystemWrp sysOps;
    private Objects objOps;
    private DateOp dateOp;
    private BarcodeOp barOp;
    private SrvXml srvXml;
    private StringOp strOp;
    private Map<String, SrvPlugin> plugins;
    private GlobalFunc gfunc;
    private Session s;
    private SrvReport srvReport;
    private SrvSignature srvSignature;
    private MathOp mathOp;
    private SystemOp sysOp;
    
    private static int isSrv = 1;
    private Map<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objCache = new HashMap<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>();
    private Set<KrnObject> objSet = new HashSet<KrnObject>();

    private static Map<String, List<Element>> plugs = new HashMap<String, List<Element>>();
    private static AtomicLong lastObjId = new AtomicLong(0);
    
    public static ThreadLocal<List<CacheChangeRecord>> cacheRecs = new ThreadLocal<List<CacheChangeRecord>>();

    private static Map<String,File> pluginsFiles = new HashMap<String, File>();
    
    public Map<Long, EvaluatorVisitor> evalsMap = new HashMap<>();
    
    public static void addPluginsFile(String dsName, String fileName) {
    	if (!pluginsFiles.containsKey(dsName)) {
    		pluginsFiles.put(dsName, new File(fileName));
    	}
    }
    
    /**
     * Для хранения стека исполнения текущего потока. Необходимо для получения стека из любого места.
     * Изначально необходимость возникла для диагностики долгих транзакций.
     */
    private static ThreadLocal<Stack<String>> executionStack = new ThreadLocal<Stack<String>>();

	public static ThreadLocal<IdentityHashMap<ASTMethod, SrvQueryCache>> queryCaches = new ThreadLocal<IdentityHashMap<ASTMethod,SrvQueryCache>>();

	public SrvOrLang(Session session) {
        s = session;
        Log log = getLog();
        objOps = new SrvObjects(session);
        dateOp = new DateOp(s.getFilterDates());
        barOp = new BarcodeOp();
        srvXml = new SrvXml(s);
        strOp = new StringOp();
        mathOp = new MathOp();
        srvReport = new SrvReport(s);
        srvSignature = new SrvSignature();
        gfunc = new SrvGlobalFunc(s);
        sysOp = new kz.tamur.or3.server.lang.SystemOp(s);
        sysOps = new SrvSystemWrp();
        sysOps.setSession(session);
        loadPlugins(log);
    }

    public boolean evaluate(String expr, Map<String, Object> vars, StringResources sr, Stack<String> callStack) throws Exception {
        return evaluate(expr, vars, sr, true, callStack);
    }

    public boolean evaluate(String expr, Map<String, Object> vars, StringResources sr, boolean wrap, Stack<String> callStack) throws Exception {
        return evaluate2(expr, vars, sr, wrap, callStack, -1);
    }

    public boolean evaluate2(String expr, Map<String, Object> vars, StringResources sr, boolean wrap, Stack<String> callStack, long threadId) throws Exception {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();
        Stack<String> oldStack = executionStack.get();

        Log oldLog = SecurityContextHolder.getLog();
    	Log log = getLog();
    	
    	SecurityContextHolder.setLog(log);
    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
    		executionStack.set(callStack);
	        preprocess(vars);
	        strOp.setStringResources(sr);
	        ASTStart start = OrLang.createStaticTemplate(expr, getLog());
	        EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, null);
	        if (threadId > -1) {
	        	evalsMap.put(threadId, ev);
	        }
	        ev.visit(start, null);
	        postprocess(vars);
    	} catch (Exception e) {
//    		ErrorsNotification.notifyErrors(e.getMessage(), ""+((OrException)e).getErrorCode(),e,s.getDsName());
    		throw e;
    	} finally {
    		executionStack.set(oldStack);
        	SecurityContextHolder.setLog(oldLog);
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
    	}
        return true;
    }
    
    public KrnAttribute getAttrByPath(String path) throws KrnException {
		KrnAttribute attr = null;
        Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, path);
		if (ps != null && ps.length > 0) {
			Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
			attr = p.first;
		}
		return attr;
    }

    public boolean evaluate(ASTStart t, Map<String, Object> vars, StringResources sr, boolean wrap, Stack<String> callStack, KrnMethod sourceMethod) throws Exception {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();
        Stack<String> oldStack = executionStack.get();

        Log oldLog = SecurityContextHolder.getLog();
    	Log log = getLog();
    	
    	SecurityContextHolder.setLog(log);
    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
    		executionStack.set(callStack);
	    	preprocess(vars);
	        strOp.setStringResources(sr);
	        EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, sourceMethod);
	        ev.visit(t, null);
	        postprocess(vars);
    	} catch (Exception e) {
//    		ErrorsNotification.notifyErrors(e.getMessage(),""+((OrException)e).getErrorCode(),e,s.getDsName());
    		throw e;
    	} finally {
    		executionStack.set(oldStack);
        	SecurityContextHolder.setLog(oldLog);
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
    	}
        return true;
    }

    private void preprocess(Map<String, Object> vars) {
        vars.remove("RETURN");
        vars.put("Systems", sysOps);
        vars.put("Objects", objOps);
        vars.put("Date", dateOp);
        vars.put("BARCODE", barOp);
        vars.put("USER", s.getUser());
        vars.put("USER_SESSION", s.getUserSession());
        try {
			vars.put("BASE", s.getCurrentDb());
		} catch (KrnException e) {
			getLog().error(e, e);
		}
        vars.put("Xml", srvXml);
        vars.put("Strings", strOp);
        vars.put("Math", mathOp);
        vars.put("Report", srvReport);
        vars.put("Signature", srvSignature);
        if (!vars.containsKey("ILANG"))
        	vars.put("ILANG", s.getUserSession().getIfcLang());
        if (plugins != null && plugins.size() != 0) {
        	vars.putAll(plugins);
        }
        vars.put("SYSTEM", sysOp);
        vars.put("SESSION", s);
        vars.put("isSrv", isSrv);
    }

    private void postprocess(Map<String, Object> vars) {
        vars.remove("SESSION");
        vars.remove("Objects");
        vars.remove("Date");
        vars.remove("BARCODE");
        vars.remove("USER");
        vars.remove("USER_SESSION");
        vars.remove("BASE");
       	vars.remove("ILANG");
        vars.remove("Xml");
        vars.remove("Strings");
        vars.remove("Math");
        vars.remove("Report");
        vars.remove("Signature");
        vars.remove("Systems");
        vars.remove("isSrv");
        if (plugins != null && plugins.size() != 0) {
            for (Iterator<String> fnIt = plugins.keySet().iterator(); fnIt.hasNext();) {
                String var = fnIt.next();
                vars.remove(var);
            }
        }
        vars.remove("SYSTEM");
    }

    private void loadPlugins(Log log) {
        if (plugins == null) {
            plugins = new HashMap<String, SrvPlugin>();

            List<Element> plugs = loadPlugs(s.getUserSession().getDsName(), log);
            
            if (plugs != null) {
	        	for (int i = 0; i < plugs.size(); i++) {
                    Element item = plugs.get(i);
                    String var = item.getAttribute("var").getValue();
                    String classpath = item.getAttribute("class").getValue();
                    try {
                        Class<?> c = LangUtils.getType(classpath, null);
                        if (c == null) throw new ClassNotFoundException(classpath);
                        Object obj = c.newInstance();
                        if (obj != null) {
	                        SrvPlugin plugin = SrvPlugin.class.cast(obj);
	                        plugin.setSession((Session)s);
                            plugins.put(var, plugin);
                        }
                    } catch (ClassNotFoundException e) {
                        log.info(e, e);
                        log.info("Ошибка при загрузке плагина [" + classpath + "]");
                    } catch (IllegalAccessException e) {
                        log.info(e, e);
                    } catch (InstantiationException e) {
                        log.info(e, e);
                    }

                }
            }
        }
    }

    private static synchronized List<Element> loadPlugs(String dsName, Log log) {
    	List<Element> dsPlugs = plugs.get(dsName);
    	if (dsPlugs == null) {
    		File xml = pluginsFiles.get(dsName);
            if (xml != null && xml.exists()) {
                SAXBuilder builder = XmlUtil.createSaxBuilder();
                try {
                	dsPlugs = new ArrayList<Element>(builder.build(xml).getRootElement().getChildren());
                } catch (JDOMException e) {
        			log.error(e, e);
	            } catch (IOException e) {
	    			log.error(e, e);
	            }
            } else {
            	log.warn("Не найден файл для серверных плагинов, для конфигурации dataSourceNames = " + dsName);
                dsPlugs = Collections.emptyList();
	        }
            plugs.put(dsName, dsPlugs);
    	}
    	return dsPlugs;
    }

    public DateOp getDateOp() {
        return dateOp;
    }

	@Override
	public List<KrnObject> getObjects(KrnClass clazz) {
        try {
            Context ctx = s.getContext();
            KrnObject[] objs = s.getClassObjects(
                    clazz, new long[0], ctx.trId);
            return Arrays.asList(objs);
        } catch (KrnException e) {
            getLog().error(e, e);
    		return null;
        }
	}

	@Override
    public List<KrnObject> find(KrnClass clazz, String path, Object value, KrnObject lang, int compOper) {
        Log log = getLog();
        try {
            long langId = -1;
            if(lang != null)
                langId=lang.id;
            Pair[] ps = SrvUtils.parsePath(s, path);
            if (ps.length == 1) {
                KrnAttribute attr = (KrnAttribute)ps[0].first;
                if(langId == -1)
                    langId = attr.isMultilingual ? s.getUserSession().getDataLanguage().id : 0;
                return Arrays.asList(s.getObjectsByAttribute(
                        clazz.id, attr.id, langId, compOper, value, -1));
            } else {
                log.error("Objects.find: Требуется путь с глубиной 1");
            }
        } catch (KrnException e) {
            log.error(e, e);
        }
        return Collections.emptyList();
    }
	
	@Override
	public KrnObject createObject(KrnClass clazz) throws KrnException {
        Context ctx = s.getContext();
        return s.createObject(clazz, ctx.trId);
	}
	
	@Override
	public KrnObject createObject(KrnClass clazz, boolean save)
			throws KrnException {
		if (save) {
			return createObject(clazz);
		} else {
			long id = lastObjId.decrementAndGet();
			KrnObject objj=new KrnObject(id, "", clazz.id);
			objCache.put(objj, new HashMap<Pair<KrnAttribute, Long>, Object>());
			return objj;
		}
	}

	@Override
	public KrnObject createObject(KrnClass clazz, String uid) throws KrnException {
        Context ctx = s.getContext();
        return s.createObjectWithUid(clazz, uid, ctx.trId);
	}

	@Override
	public Object exec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
        Map<String, Object> vars = new HashMap<String, Object>();
    	return exec(clazz, _this, methodName, args, callStack, vars);
	}
	
	public Object exec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack, Map<String, Object> vars) throws Throwable {
		KrnMethod m = s.getMethodByName(clazz.id, methodName);
		if (m != null) {
	    	ASTStart expr = s.getMethodExpression(m);
	        vars.put("THIS", _this);
	        vars.put("ARGS", args);
	    	KrnClass cls = s.getClassById(m.classId);
	        if(cls.parentId > 0) {
	            vars.put("SUPER", s.getClassById(cls.parentId));
	        } else {
	            vars.put("SUPER", this);
	        }
	    	String threadName = Thread.currentThread().getName();
	    	try {
	    		Thread.currentThread().setName(threadName + "(" + methodName + ")");
	            evaluate(expr, vars, null, false, callStack, m);
	            return vars.get("RETURN");
	        } catch (Throwable e) {
	            StringBuffer msg = new StringBuffer();
	            msg.append(clazz.name);
	            msg.append('.');
	            msg.append(methodName);
	            msg.append('(');
	            if (args.size() > 0) {
	                msg.append(args.get(0));
	            }
	            for (int i = 1; i < args.size(); i++) {
	                msg.append(",");
	                msg.append(args.get(i));
	            }
	            msg.append(')');
	            getLog().error(msg);
	            throw e;
			} finally {
				Thread.currentThread().setName(threadName);
	        }
		} else {
			throw new KrnException(0, "Не найден метод '" + methodName + "' в классе " + clazz.getName());
		}
	}
	
	public Object exec(ASTStart expr, KrnClass cls, List<Object> args, Stack<String> callStack, Map<String, Object> vars) throws Throwable {
        vars.put("THIS", cls);
        vars.put("ARGS", args);
        if(cls.parentId > 0) {
            vars.put("SUPER", s.getClassById(cls.parentId));
        } else {
            vars.put("SUPER", this);
        }
    	String threadName = Thread.currentThread().getName();
    	try {
    		Thread.currentThread().setName(threadName + "(trigger)");
            evaluate(expr, vars, null, false, callStack, null);
            return vars.get("RETURN");
        } catch (Throwable e) {
            StringBuffer msg = new StringBuffer();
            msg.append(cls.name);
            msg.append('.');
            msg.append('(');
            if (args.size() > 0) {
                msg.append(args.get(0));
            }
            for (int i = 1; i < args.size(); i++) {
                msg.append(",");
                msg.append(args.get(i));
            }
            msg.append(')');
            getLog().error(msg);
            throw e;
		} finally {
			Thread.currentThread().setName(threadName);
        }
	}

	@Override
    public KrnClass getParentClass(KrnClass clazz) {
		return s.getClassById(clazz.parentId);
    }

	@Override
    public KrnClass getAttrClass(KrnClass clazz, String attrName) {
        KrnAttribute attr = s.getAttributeByName(clazz, attrName);
        return s.getClassById(attr.typeClassId);
    }

	@Override
    public List<KrnAttribute> getAttributes(KrnClass clazz) {
        return Arrays.asList(s.getAttributes(clazz));
    }

	@Override
	public KrnAttribute getAttribute(KrnClass clazz, String name) {
		return s.getAttributeByName(clazz, name);
	}
	
	@Override
	public void refreshObjects(KrnClass clazz) {
		//функция выполняет обновление c_id в таблице t_changes объектов текущего класса
		//это бывает необходимо при репликации, когда нужно чтобы в репликационный экспорт попали данные объектов текущего класса
		//такая необходимость возникает когда забыли пометить текущий класс как реплицируемый, а экспорт уже выполнен
		//после пометки как реплицируемый необходимо запустить данную функцию
        Log log = getLog();
		try {
			KrnClass clsLanguage = s.getClassByName("Language");
			KrnAttribute attrIsUsed = s.getAttributeByName(clsLanguage, "lang?");
			KrnObject[] langs = s.getClassObjects(clsLanguage, new long[]{}, 0);
			KrnObject[] objs = s.getClassObjects(clazz, new long[]{}, 0);
			List<KrnObject> langList = new ArrayList<KrnObject>();
			for (KrnObject lang : langs) {
				long langIsUsed = s.getLongsSingular(lang, attrIsUsed, true);
				if (langIsUsed == 1) {
					langList.add(lang);
				}
			}
	        List<KrnAttribute> attrs = getAttributes(clazz);
			for (KrnObject obj : objs) {
				s.refreshObjectCreating(obj);
		        for (KrnAttribute attr : attrs) {
		        	if (attr.id == 1 || attr.id == 2) continue;
		        	if (attr.rAttrId > 0) continue;
		        	log.info("attr.id = " + attr.id + " " + attr.name);
		            if (attr.isMultilingual) {
		    			for (KrnObject lang : langList) {
			                SortedSet<kz.tamur.ods.Value> values = 
			                	s.getValues(new long[]{obj.id}, attr.id, lang.id, 0);
			                for (kz.tamur.ods.Value value : values) {
			                    s.setValue(obj.id, attr.id, value.index, lang.id, value.value, 0, false);
			                }
		    			}
		            } else {
		                SortedSet<kz.tamur.ods.Value> values = 
		                	s.getValues(new long[]{obj.id}, attr.id, 0, 0);
		                if (attr.typeClassId > 99) 
			                for (kz.tamur.ods.Value value : values) {
			                    s.setValue(obj.id, attr.id, value.index, 0, ((KrnObject) value.value).id, 0, false);
			                }
		                else
			                for (kz.tamur.ods.Value value : values) {
			                    s.setValue(obj.id, attr.id, value.index, 0, value.value, 0, false);
			                }
		            }
		        }
			}
		} catch (KrnException e) {
			log.error(e);
		}
        log.debug("Refreshing of objects data of class "+clazz.name+" completed.");
	}

	// Attribute Operations
	@Override
    public KrnClass getCls(KrnAttribute atter) {
		return s.getClassById(atter.classId);
    }

	@Override
	public KrnClass getType(KrnAttribute atter) {
		return s.getClassById(atter.typeClassId);
    }
    
	@Override
    public List<KrnAttribute> getRevAttributes(KrnAttribute atter) {
    	List<KrnAttribute> res = new ArrayList<KrnAttribute>();
    	try {
    		long[] rattrs = s.getRevAttributes(atter.id);
    		for (long attr : rattrs) {
    			res.add(s.getAttributeById(attr));
    		}
    	} catch (KrnException e) {
			getLog().error(e, e);
    	}
    	return res;
    }
	
	// Object operatinos

    @Override
	public long getCurrentTransactionId() {
		return s.getContext().trId;
	}

    @Override
    public Object getAttr(KrnObject objj, String path, KrnObject lang, long trId) throws KrnException {
    	SrvQueryCache queryCache = SrvOrLang.getQueryCache();
    	if (queryCache == null) {
    		queryCache = new SrvQueryCache();
    	}
    	queryCache.init(path, s);
    	
    	//Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, path);
        Pair<KrnObject, SortedMap<Integer, Object>> res = SrvUtils.getObjectAttr(
                objj, path, trId, lang != null ? lang.id : 0, s, true);
        if (res != null) {
        	SortedMap<Integer, Object> map = res.second;
        	//Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
            KrnAttribute attr = queryCache.getAttribute();
            Integer index = queryCache.getIndex();
            if (attr.collectionType == COLLECTION_ARRAY
            		|| attr.collectionType == COLLECTION_SET) {
                int last = (map.size() > 0) ? ((Number)map.lastKey()).intValue() : -1;
                if (index instanceof Number) {
                    int i = index.intValue();
                    if (i < 0) {
                        i = last + 1 + i;
                    }
                    return map.get(new Integer(i));
                } else if (index == null) {
                    List list = new ArrayList(last + 1);
                    for (int i = 0; i <= last; i++) {
                        Object val = map.get(new Integer(i));
                        list.add(val);
                    }
                    return list;
                }
            } else {
            	Object o = map.get(new Integer(0));
            	if (index == null) {
            		List list = new ArrayList(1);
            		if (o != null) {
            			list.add(o);
            		}
            		o = list;
            	}
                return o;
            }
        }
        return null;
    }

    @Override
    public void setAttr(KrnObject objj, String path, Object value, KrnObject lang, long trId) throws KrnException {
    	if (value instanceof File) {
    		try {
    			value = Funcs.read((File)value);
    		} catch (IOException e) {
    			getLog().error(e,  e);
    			throw new KrnException(0, e.getMessage());
    		}
    	}
    	KrnObject obj = objj;
        Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, path);
    	Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
        
    	if(objSet.contains(obj) || objj.id < 0 || p.first.isGroup()){
        	Map<Pair<KrnAttribute, Long>, Object> map = objCache.get(objj);
        	if(map==null){
    			objCache.put(objj, map = new HashMap<Pair<KrnAttribute, Long>, Object>());
        	}
        	long langId = lang != null ? lang.id : 0;
        	if (p.first.typeClassId == CID_DATE && value instanceof KrnDate)
        		value = new java.sql.Date(((KrnDate)value).getTime());
        	else if (p.first.typeClassId == CID_TIME && value instanceof KrnDate)
        		value = new java.sql.Timestamp(((KrnDate)value).getTime());
        	map.put(new Pair<KrnAttribute, Long>(p.first, langId), value);
        	return;
        }

        KrnAttribute attr = p.first;
        
        if (value instanceof KrnObject && ((KrnObject)value).id < 0 && attr.rAttrId > 0) {
        	KrnAttribute ra = s.getAttributeById(attr.rAttrId);
        	Map<Pair<KrnAttribute, Long>, Object> map = objCache.get((KrnObject)value);
        	map.put(new Pair<KrnAttribute, Long>(ra, 0L), objj);
        }
        
        Pair res = SrvUtils.getObjectAttr(
                obj, path, trId, lang != null ? lang.id : 0, s, true);
        if (res != null) {
            SortedMap map = (SortedMap)res.second;
            int i = 0;
            if (attr.collectionType > 0 && map.size() > 0) {
                int last = ((Number)map.lastKey()).intValue();
                if (p.second instanceof Number) {
                    i = ((Number)p.second).intValue();
                    if (i < 0) {
                        i = last + 1 + i;
                    }
                } else {
                    i = last + 1;
                }
            }
            if (i >= 0) {
                obj = (KrnObject)res.first;
                Util.setObjectAttr(obj, attr, i, lang != null ? lang.id : 0, trId,
                                   value, s);
            }
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, boolean cascade, long trId) throws KrnException {
        KrnObject obj = objj;
        long langId = lang != null ? lang.id : s.getContext().langId;
        Pair[] ps = SrvUtils.parsePath(s, path);
        Pair res = SrvUtils.getObjectAttr(obj, path, trId, langId, s, true);
        if (res != null) {
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            obj = (KrnObject)res.first;
            SortedMap map = (SortedMap)res.second;
            if (map.size() > 0) {
                int[] inds = new int[map.size()];
                int i = 0;
                List<Object> values = new ArrayList<Object>();
                for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                    Number j = (Number) it.next();
                    Object v = map.get(j);
                    if (cascade && v instanceof KrnObject) {
                    	s.deleteObject((KrnObject)v, trId);
                    }
                    inds[i++] = j.intValue();
                    values.add(v);
                }
                if (attr.collectionType == 0 || attr.collectionType == 1)
                    s.deleteValue(obj.id, attr.id, inds, 0, trId);
                else if (attr.collectionType == 2)
                    s.deleteValue(obj.id, attr.id, values, trId);
            }
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, int index, long trId) throws KrnException {
        KrnObject obj = objj;
        long langId = lang != null ? lang.id : s.getContext().langId;
        Pair[] ps = SrvUtils.parsePath(s, path);
        Pair res = SrvUtils.getObjectAttr(obj, path, trId, langId, s, true);
        if (res != null) {
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            obj = (KrnObject)res.first;
            SortedMap<Integer, Object> map = (SortedMap<Integer, Object>)res.second;
            if (index < 0 && map.size() > 0) {
            	index = map.lastKey() + index + 1;
            }
            if (map.containsKey(new Integer(index))) {
                int[] inds = {index};
                s.deleteValue(obj.id, attr.id, inds, 0, trId);
            }
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, Object value, long trId) throws KrnException {
        Pair[] ps = SrvUtils.parsePath(s, path);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
        List<Object> l = new ArrayList<Object>();
        l.add(value);
        s.deleteValue(objj.id, attr.id, l, trId);
    }

    @Override
    public void delete(KrnObject objj, boolean delRefs, long trId) throws KrnException {
        s.deleteObject(objj, trId, delRefs);
    }

    @Override
    public boolean isLock(KrnObject objj) throws KrnException {
        	Collection<Lock> locks = s.getLocksByObjectId(objj.id);
            return locks!=null && locks.size()>0;
    }

    @Override
    public boolean isLock(KrnObject objj, KrnObject locker) throws KrnException {
        if(locker == null){
        	Collection<Lock> locks = s.getLocksByObjectId(objj.id);
            return locks!=null && locks.size()>0;
        }else
             return s.isObjectLock(objj.id,locker.id);
    }
    
    @Override
    public String getLocker(KrnObject objj) throws KrnException {
    	return s.isObjectLock(objj.id);
    }
    
    @Override
    public void lock(KrnObject objj) throws KrnException {
        Context ctx = s.getContext();
       /* if (objj.isLocked(objj)) {
        	getLog().info("Объект уже заблокирован!");
        } else {*/
            s.lock(objj.id,ctx.flowId);
      //  }
    }

    @Override
    public void lock(KrnObject objj, KrnObject locker) throws KrnException {
        Context ctx = s.getContext();
        s.lockObject(objj.id, locker.id, Lock.LOCK_FLOW, ctx.flowId, null);
    }

    @Override
    public void unlock(KrnObject objj) throws KrnException {
        Context ctx = s.getContext();
        s.unlock(objj.id,ctx.flowId);
    }

    @Override
    public boolean like(KrnObject objj, KrnObject obj, KrnObject lang, long trId) throws KrnException {
    	List<KrnObject> langs = null;
    	if (lang == null)
    		langs = s.getSystemLangs();
    	else {
    		langs = new ArrayList<KrnObject>();
    		langs.add(lang);
    	}
        KrnAttribute[] attrs = s.getAttributes(
                s.getClassById(obj.classId));
        for (int i = 0; i < attrs.length; i++) {
            KrnAttribute attr = (KrnAttribute) attrs[i];
            if (attr.name.equals("creating") || attr.name.equals("deleting"))
                continue;
            if (attr.typeClassId >= 99) {
                KrnObject[] lvs1 = s.getObjects(objj.id, attr.id, new long[0], trId);
                KrnObject[] lvs2 = s.getObjects(obj.id, attr.id, new long[0], trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (lvs1[y].id != lvs2[y].id)
                            return false;
                    }
                else return false;
            } else
            if (attr.typeClassId == CID_INTEGER
                || attr.typeClassId == CID_BOOL) {
                long[] lvs1 = s.getLongs(objj.id, attr.id, trId);
                long[] lvs2 = s.getLongs(obj.id, attr.id, trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (lvs1[y] != lvs2[y])
                            return false;
                    }
                else return false;
            } else
            if (attr.typeClassId == CID_STRING
                || attr.typeClassId == CID_MEMO) {
            	for (KrnObject l : langs) {
                	long langId = l.id;
                    String[] lvs1 = s.getStrings(objj.id, attr.id, langId,
                            attr.typeClassId == CID_MEMO, trId);
                    String[] lvs2 = s.getStrings(obj.id, attr.id, langId,
                            attr.typeClassId == CID_MEMO, trId);
                    if (lvs1.length == lvs2.length)
                        for (int y = 0; y < lvs1.length; y++) {
                            if (!lvs1[y].equals(lvs2[y]))
                                return false;
                        }
                    else return false;
            	}
            } else
            if (attr.typeClassId == CID_DATE) {
                com.cifs.or2.kernel.Date[] lvs1 = s.getDates(objj.id, attr.id, trId);
                com.cifs.or2.kernel.Date[] lvs2 = s.getDates(obj.id, attr.id, trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (lvs1[y].day != lvs2[y].day)
                            return false;
                        if (lvs1[y].month != lvs2[y].month)
                            return false;
                        if (lvs1[y].year != lvs2[y].year)
                            return false;
                    }
                else return false;
            } else
            if (attr.typeClassId == CID_TIME) {
                com.cifs.or2.kernel.Time[] lvs1 = s.getTimes(objj.id, attr.id, trId);
                com.cifs.or2.kernel.Time[] lvs2 = s.getTimes(obj.id, attr.id, trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (lvs1[y].day != lvs2[y].day)
                            return false;
                        if (lvs1[y].month != lvs2[y].month)
                            return false;
                        if (lvs1[y].year != lvs2[y].year)
                            return false;
                        if (lvs1[y].hour != lvs2[y].hour)
                            return false;
                        if (lvs1[y].min != lvs2[y].min)
                            return false;
                        if (lvs1[y].sec != lvs2[y].sec)
                            return false;
                        if (lvs1[y].msec != lvs2[y].msec)
                            return false;
                    }
                else return false;
            } else
            if (attr.typeClassId == CID_FLOAT) {
                double[] lvs1 = s.getFloats(objj, attr, trId);
                double[] lvs2 = s.getFloats(obj, attr, trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (lvs1[y] != lvs2[y])
                            return false;
                    }
                else return false;
            } /*else //решено BLOB не проверять из-за того, что будет отнимать очень мног времени
            //потому что сравнивать надо каждый байт
            if (attr.typeClassId == CID_BLOB) {
                byte[][] lvs1 = session.getBlobs(getKrnObject().id, attr.id, ctx.langId, ctx.trId);
                byte[][] lvs2 = session.getBlobs(obj.getKrnObject().id, attr.id, ctx.langId, ctx.trId);
                if (lvs1.length == lvs2.length)
                    for (int y = 0; y < lvs1.length; y++) {
                        if (!lvs1[y].equals(lvs2[y])) //неправилный контроль, здесь нужно сверять каждый байт!
                            return false;
                    }
                else return false;
            }*/
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getProcessLocker(KrnObject objj) {
        return  s.getProcessLocker(objj.id);
    }
    
    @Override
    public List<KrnObject> getConflictLocker(KrnObject objj) {
        Context ctx = s.getContext();
        return Arrays.asList(s.getConflictLocker(objj.id, ctx.flowId,ctx.pdId));
    }
    
    @Override
    public KrnObject getLocker(KrnObject objj, KrnObject service) {
        return s.getLocker(objj.id, service.id);
    }

    @Override
    public List<KrnObject> getLockers(KrnObject objj) throws KrnException{
    	Collection<Lock> locks = s.getLocksByObjectId(objj.id);
    	ArrayList<KrnObject> res = new ArrayList<KrnObject>();
    	if(locks!=null && locks.size()>0){
    		for(Lock lock:locks){
    			res.add(s.getObjectById(lock.lockerId, 0));
    		}
       	}
    	return res;
    }
    @Override
    public Object exec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
        Log log = getLog();
    	KrnMethod m = s.getMethodByName(objj.classId, methodName);
    	ASTStart expr = s.getMethodExpression(m);
        Map<String, Object> vc = new HashMap<String, Object>();
        vc.put("THIS", _this);
        vc.put("ARGS", args);
        KrnClass cls = s.getClassById(m.classId);
        if (cls.parentId > 0) {
        	KrnObject sobj = new KrnObject(objj.id, objj.uid, cls.parentId);
        	vc.put("SUPER", sobj);
        }
    	String threadName = Thread.currentThread().getName();
    	try {
    		Thread.currentThread().setName(threadName + "(" + methodName + ")");
            evaluate(expr, vc, null, false, callStack, m);
            return vc.get("RETURN");
        } catch (Throwable e) {
            StringBuffer msg = new StringBuffer();
            msg.append(s.getClassById(objj.classId).name);
            msg.append('.');
            msg.append(methodName);
            msg.append('(');
            if (args.size() > 0) {
                msg.append(args.get(0));
            }
            for (int i = 1; i < args.size(); i++) {
                msg.append(",");
                msg.append(args.get(i));
            }
            msg.append(')');
            log.error(msg);
            throw e;
		} finally {
			Thread.currentThread().setName(threadName);
        }
    }
    
    
    @Override
	public boolean isLocked(KrnObject objj, KrnObject locker)
			throws KrnException {
    	return s.isObjectLock(objj.id, locker.id);
	}

	@Override
	public Object sexec(KrnObject objj, KrnObject _this, String methodName,
			List<Object> args, Stack<String> callStack) throws Throwable {
		
		return exec(objj, _this, methodName, args, callStack);
		//throw new KrnException(0, "Not implemented.");
	}

	@Override
	public Object sexec(KrnClass clazz, KrnClass _this, String methodName,
			List<Object> args, Stack<String> callStack) throws Throwable {
		
		return exec(clazz, _this, methodName, args, callStack);
		//throw new KrnException(0, "Not implemented.");
	}
	
	@Override 
	public boolean isDel(KrnObject obj, long trId) {
		return s.isDel(obj, trId);
	}

	@Override
    public boolean isDeleted(KrnObject objj) {
        return s.isDeleted(objj);
    }

	@Override
	public KrnClass getCls(KrnObject objj) throws Exception {
		return s.getClassById(objj.classId);
	}

	@Override
	public boolean beforeSave(KrnObject objj) throws KrnException {
		if(objj.id>0){
				objSet.add(objj);
			return true;
		}
		return false;
	}
	@Override
	public KrnObject save(KrnObject objj) throws KrnException {
		objSet.remove(objj);
		Map<Pair<KrnAttribute, Long>, Object> map = objCache.remove(objj);
		if (map != null) {
	        Context ctx = s.getContext();
	        KrnClass cls = s.getClassById(objj.classId);
	        if(objj.id>0)
		        return s.updateObject(cls,objj, map, ctx.trId);
	        else
	        	return s.createObject(cls, map, ctx.trId);
		}
		return objj;
	}

	/* Сохранение объектов одного класса */
	@Override
	public List<KrnObject> save(KrnClass cls, List<KrnObject> objjs, boolean executeTriggers, boolean logRecords) throws KrnException {
		if (objjs.size() > 0) {
			List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objsToCreate = new ArrayList<>();
			List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objsToUpdate = new ArrayList<>();
			for (KrnObject objj : objjs) {
				objSet.remove(objj);
				Map<Pair<KrnAttribute, Long>, Object> map = objCache.remove(objj);
				if (map != null) {
			        if(objj.id > 0)
			        	objsToUpdate.add(new Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>(objj, map));
			        else
			        	objsToCreate.add(new Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>(objj, map));
				}
			}
	        Context ctx = s.getContext();
	        KrnClass objCls = s.getClassById(objjs.get(0).classId);
	        
	        if (cls.id != objCls.id) throw new KrnException(0, "Попытка сохранения объектов другого класса!");
	        //TODO: Сохранение уже существующих объектов 
	        if (objsToUpdate.size() > 0)
	        	s.updateObjects(cls, objsToUpdate, ctx.trId, executeTriggers, logRecords);
	        if (objsToCreate.size() > 0)
	        	s.createObjects(cls, objsToCreate, ctx.trId, executeTriggers, logRecords);
		}
		return objjs;
	}

	// Сохранение всех объектов с групповыми атрибутами
	public void save() throws KrnException {
		Set<KrnObject> removeSet = new HashSet<KrnObject>();
		for (KrnObject objj : objCache.keySet()) {
			objSet.remove(objj);
			removeSet.add(objj);
			Map<Pair<KrnAttribute, Long>, Object> map = objCache.get(objj);
			if (map != null) {
				Context ctx = s.getContext();
				KrnClass cls = s.getClassById(objj.classId);
				if (objj.id > 0)
					s.updateObject(cls, objj, map, ctx.trId);
/*
				else s.createObject(cls, map, ctx.trId);
*/ 			}
		}
		for (KrnObject objj : removeSet)
			objCache.remove(objj);
	}
	
	public void cleaObjCache(){
		objSet.clear();
		objCache.clear();
	}
	public static Collection<String> getExecutionStack() {
		Stack<String> stack = executionStack.get();
		if (stack != null)
			return Collections.unmodifiableCollection(stack);
		return null;
	}
	
	private Log getLog() {
    	return LogFactory.getLog(s.getDsName() + "." + s.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
	}

	@Override
	public void refreshObjects(List<KrnObject> objs) throws KrnException {
		Log log = getLog();
		try {
			KrnClass clsLanguage = s.getClassByName("Language");
			KrnAttribute attrIsUsed = s.getAttributeByName(clsLanguage, "lang?");
			KrnObject[] langs = s.getClassObjects(clsLanguage, new long[] {}, 0);
			List<KrnObject> langList = new ArrayList<KrnObject>();
			for (KrnObject lang : langs) {
				long langIsUsed = s.getLongsSingular(lang, attrIsUsed, true);
				if (langIsUsed == 1) {
					langList.add(lang);
				}
			}
			
			for (KrnObject obj: objs) {
				KrnClass cls = s.getClassById(obj.getClassId());
				List<KrnAttribute> attrs = getAttributes(cls);
				s.refreshObjectCreating(obj);
				for (KrnAttribute attr : attrs) {
					if (attr.id == 1 || attr.id == 2)
						continue;
					if (attr.rAttrId > 0)
						continue;
					log.info("attr.id = " + attr.id + " " + attr.name);
					if (attr.isMultilingual) {
						for (KrnObject lang : langList) {
							SortedSet<kz.tamur.ods.Value> values = s.getValues(new long[] { obj.id }, attr.id, lang.id, 0);
							for (kz.tamur.ods.Value value : values) {
								s.setValue(obj.id, attr.id, value.index, lang.id, value.value, 0, false);
							}
						}
					} else {
						SortedSet<kz.tamur.ods.Value> values = s.getValues(new long[] { obj.id }, attr.id, 0, 0);
						if (attr.typeClassId > 99) {
							for (kz.tamur.ods.Value value : values) {
								s.setValue(obj.id, attr.id, value.index, 0, ((KrnObject) value.value).id, 0, false);
							}
						} else {
							for (kz.tamur.ods.Value value : values) {
								s.setValue(obj.id, attr.id, value.index, 0, value.value, 0, false);
							}
						}
					}
				}
				log.debug("Refreshing of object data of class " + cls.name + " completed.");
			}
		} catch (KrnException e) {
			log.error(e);
		}		
	}

	@Override
	public KrnClass getClassById(long classId) throws KrnException {
		return s.getClassById(classId);
	}
	
	public static SrvQueryCache getQueryCache() {
		SrvQueryCache queryCache = null;
		ASTMethod method = currMethod.get();
		if (method != null) {
			IdentityHashMap<ASTMethod, SrvQueryCache> map = queryCaches.get();
			if (map != null) {
				queryCache = map.get(method);
				if (queryCache == null) {
					queryCache = new SrvQueryCache();
					map.put(method, queryCache);
				}
			}
		}
		return queryCache;
	}
	
	public static void createQueryCache() {
		queryCaches.set(new IdentityHashMap<ASTMethod, SrvQueryCache>());
		methodCaches.set(new IdentityHashMap<Class, Map<Node,Method>>());
	}
	
	public static void destroyQueryCache() {
		queryCaches.remove();
		methodCaches.remove();
	}

	@Override
	public long filterToAttr(KrnObject objj, KrnObject fobj, String path, Map<String, Object> params, long trId)
			throws KrnException {
        String uid = fobj.getUID();
        s.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                s.setFilterParam(uid, name, value);
            }
        }
        Pair[] ps = SrvUtils.parsePath(s, path);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
		return s.filterToAttr(fobj, objj.id, attr.id, trId);
	}
}