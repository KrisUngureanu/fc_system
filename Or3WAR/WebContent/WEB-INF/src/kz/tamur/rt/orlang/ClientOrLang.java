package kz.tamur.rt.orlang;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.OrFrame;
import kz.tamur.lang.BarcodeOp;
import kz.tamur.lang.DateOp;
import kz.tamur.lang.GlobalFunc;
import kz.tamur.lang.MathOp;
import kz.tamur.lang.Objects;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.StringOp;
import kz.tamur.lang.SystemOp;
import kz.tamur.lang.XmlOp;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.lang.parser.EvaluatorVisitor;
import kz.tamur.lang.parser.LangUtils;
import kz.tamur.ods.Lock;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.or3ee.client.lang.ClientSystemWrp;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.common.lang.SystemWrp;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.util.CollectionTypes;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.component.WebFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnAttributeOperations;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnClassOperations;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnObjectOperations;
import com.cifs.or2.kernel.UserSessionValue;

public class ClientOrLang extends OrLang {

    private static SystemWrp sysWrp = new ClientSystemWrp();
    private static Objects objOps = new ClientObjects();
    private InterfaceWrp ifcWrp;
    private static DateOp dateOp = new DateOp();
    private BarcodeOp barOp = new BarcodeOp();
    private static StringOp strOp = new StringOp();
    private static MathOp mathOp = new MathOp();
    private static ClientSignature signOp = new ClientSignature();
    private static XmlOp xmlOp = new ClientXmlOp();
    private static GlobalFunc gfunc = new ClientGlobalFunc();
    private static SystemOp sysOp = new kz.tamur.or3.client.lang.SystemOp();
    private static List<Element> plugs;
    private static Map<String, Object> plugins;
    private static ClientUniversalGenerator uniGen = new ClientUniversalGenerator();
    private static Map<Long, Map<String, ASTStart>> actions = new HashMap<Long, Map<String, ASTStart>>();
    
    private static int isSrv = 0;

    public ClientOrLang(OrFrame frame) {
    	this(frame, false);
    }
    
    public ClientOrLang(OrFrame frame, boolean beforeOpen) {
        this(frame, (frame != null) ? frame.getKernel() : Kernel.instance(), beforeOpen);
    }
    
    public ClientOrLang(OrFrame frame, Kernel krn, boolean beforeOpen) {
    	SecurityContextHolder.setFrame(frame);
    	SecurityContextHolder.setKernel(krn);
        
        ifcWrp = new InterfaceWrp(frame, beforeOpen);

        loadPlugins();
    }

    public ClientOrLang(Kernel krn) {
    	this((OrFrame)null, krn, false);
    }
    
    public boolean evaluate(String expr, Map<String, Object> vars, CheckContext ctx, Stack<String> callStack) {
    	return evaluate(expr, vars, ctx, true, callStack);
    }

    public boolean evaluate(String expr, Map<String, Object> vars, CheckContext ctx, boolean wrap, Stack<String> callStack) {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();

        Log oldLog = SecurityContextHolder.getLog();
    	Log log = getLog();

    	SecurityContextHolder.setLog(log);
    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
            preprocess(vars, wrap);
            ifcWrp.setContext(ctx);
	        addMsgToCallStack(ctx, callStack);
            EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, null);
            ev.visit(OrLang.createStaticTemplate(expr, getLog()), null);
            postprocess(vars, wrap);
            return true;
        } catch (Exception e) {
            log.error(e, e);
            return false;
		} finally {
        	SecurityContextHolder.setLog(oldLog);
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
		}
    }

    public void evaluate2(String expr, Map<String, Object> vars, CheckContext ctx, boolean wrap, Stack<String> callStack) throws Exception {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();

        Log oldLog = SecurityContextHolder.getLog();
    	Log log = getLog();

    	SecurityContextHolder.setLog(log);
    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
	        preprocess(vars, wrap);
	        ifcWrp.setContext(ctx);
	        addMsgToCallStack(ctx, callStack);
	        EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, null);
	        ev.visit(OrLang.createStaticTemplate(expr, getLog()), null);
	        postprocess(vars, wrap);
    	} catch (Exception e) {
    		throw e;
    	} finally {
        	SecurityContextHolder.setLog(oldLog);
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
    	}
    }

    public boolean evaluate(ASTStart t, Map<String, Object> vars, CheckContext ctx, Stack<String> callStack) throws Exception {
        return evaluate(t, vars, ctx, true, callStack, null);
    }

    public boolean evaluate(ASTStart t, Map<String, Object> vars, CheckContext ctx, boolean wrap, Stack<String> callStack, KrnMethod sourceMethod) throws Exception {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();

        Log oldLog = SecurityContextHolder.getLog();
    	Log log = getLog();

    	SecurityContextHolder.setLog(log);
    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
	        preprocess(vars, wrap);
	        ifcWrp.setContext(ctx);
	        addMsgToCallStack(ctx, callStack);
	        EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, sourceMethod);
	        ev.visit(t, null);
	        postprocess(vars, wrap);
    	} catch (Exception e) {
    		throw e;
    	} finally {
        	SecurityContextHolder.setLog(oldLog);
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
    	}
        return true;
    }

    private void addMsgToCallStack(CheckContext ctx, Stack<String> callStack){
    	//Добавлены реквизиты интерфейса, на котором выполняется формула, 
    	//чтобы по логу можно было легко найти источник ошибки
        if(callStack.isEmpty() && ctx!=null && ctx instanceof ComponentAdapter && ((ComponentAdapter)ctx).getFrame()!=null){
        	OrFrame frm=((ComponentAdapter)ctx).getFrame();
        	String msg="";
        	if(frm instanceof WebFrame){
	        	msg  += "ifcUid: " + ((WebFrame)frm).getInterfaceUid();
        	}
        	msg+="; flowId: "+frm.getFlowId()+"; tid: "+frm.getTransactionId()+"; UUID:"+ctx.getUUID();
        	callStack.add(msg);
        }
    }
    private void preprocess(Map<String, Object> vars, boolean wrap) {
    	Kernel krn = getKernel();
    	
        vars.remove("RETURN");
        vars.put("Systems", sysWrp);
        vars.put("Objects", objOps);
        vars.put("Interface", ifcWrp);
        vars.put("Date", dateOp);
        vars.put("BARCODE", barOp);
        vars.put("Strings", strOp);
        vars.put("Math", mathOp);
        vars.put("Xml", xmlOp);
        vars.put("Signature", signOp);
        vars.put("Numerator", uniGen);
        User user = krn.getUser();
        if (user != null) {
        	vars.put("USER", user.getObject());
        }
        vars.put("USER_SESSION", krn.getUserSession());
        vars.put("BASE", krn.getCurrentDb());
        if (plugins != null && plugins.size() != 0) {
            for (Iterator<String> fnIt = plugins.keySet().iterator(); fnIt.hasNext();) {
                String var = fnIt.next();
                Object obj = plugins.get(var);
                vars.put(var, obj);
            }
        }
        vars.put("SYSTEM", sysOp);
    	vars.put("ILANG", krn.getInterfaceLanguage());
    	vars.put("isSrv", isSrv);
    }

    private void postprocess(Map<String, Object> vars, boolean wrap) {
        vars.remove("Objects");
        vars.remove("Interface");
        vars.remove("Date");
        vars.remove("BARCODE");
        vars.remove("Strings");
        vars.remove("Math");
        vars.remove("Xml");
        vars.remove("USER");
        vars.remove("USER_SESSION");
        vars.remove("BASE");
        vars.remove("Signature");
        vars.remove("Numerator");
       	vars.remove("ILANG");
       	vars.remove("isSrv");
        if (plugins != null && plugins.size() != 0) {
            for (Iterator<String> fnIt = plugins.keySet().iterator(); fnIt.hasNext();) {
                String var = fnIt.next();
                vars.remove(var);
            }
        }
        vars.remove("SYSTEM");
    }

    private static List<Element> loadPlugs() {
    	if (plugs == null) {
    		InputStream is = ClientOrLang.class.getResourceAsStream("/kz/tamur/rt/orlang/clientplugs.xml");
        	Log log = getLog();

	        if (is != null) {
	            SAXBuilder builder = new SAXBuilder();
	            try {
            		plugs = new ArrayList<Element>((List<Element>)builder.build(is).getRootElement().getChildren());
                } catch (JDOMException e) {
            		log.error(e, e);
	            } catch (IOException e) {
	        		log.error(e, e);
	            } finally {
	            	kz.tamur.rt.Utils.closeQuietly(is);
	            }
            } else {
                log.error("Не найден файл для клиентских плагинов");
                plugs = Collections.emptyList();
	        }
    	}
    	return plugs;
    }
    
    private static synchronized void loadPlugins() {
        if (plugins == null) {
            plugins = new HashMap<String, Object>();

            List<Element> plugs = loadPlugs();
            Log log = getLog();
            
            if (plugs != null) {
	        	for (int i = 0; i < plugs.size(); i++) {
	                Element item = (Element)plugs.get(i);
	                String var = item.getAttribute("var").getValue();
	                String classpath = item.getAttribute("class").getValue();
	                try {
	                	log.info("ClassLoaderWeb1: " + ClientOrLang.class.getClassLoader());
                        Class<?> c = LangUtils.getType(classpath, null, ClientOrLang.class.getClassLoader(), ClientOrLang.class);
                        if (c == null) throw new ClassNotFoundException(classpath);
                        Object obj = c.newInstance();
	                    if (obj != null)
	                        plugins.put(var, obj);
	                } catch (ClassNotFoundException e) {
	                    log.info(e, e);
	                    log.error("Ошибка при загрузке плагина [" + classpath + "]");
	                } catch (IllegalAccessException e) {
	                    log.info(e, e);
	                } catch (InstantiationException e) {
	                    log.info(e, e);
	                }
	            }
            }
        }
    }

    public InterfaceWrp getIfcWrp() {
        return ifcWrp;
    }

    public DateOp getDateOp() {
        return dateOp;
    }

    public static SystemOp getSysOp() {
		return sysOp;
	}

	public static synchronized ASTStart getStaticTemplate(long ifcId, String key, String expr, Log log) {
    	ASTStart a = null;
    	if (expr != null && expr.length() > 0) {
        	Map<String, ASTStart> m = actions.get(ifcId);
        	if (m == null) {
        		m = new HashMap<String, ASTStart>();
        		actions.put(ifcId, m);
        	}
        	a = m.get(key);
    		if (a == null) {
                try {
                    a = OrLang.createStaticTemplate(expr, log);
                    m.put(key, a);
                } catch (Throwable e) {
                    log.error("Ошибка в формуле: " + expr);
                    log.error(e, e);
                    StringTokenizer st = new StringTokenizer(e.getMessage(), "\n");
                    throw new RuntimeException("Синтаксическая ошибка!\n" + st.nextToken());
                }
    		}
        }
        return a;
    }
    
    public static void clearActions(long ifcId) {
    	synchronized (actions) {
    		actions.remove(ifcId);
    	}
    }
    
    public static WebFrame getFrame() {
    	return (WebFrame) SecurityContextHolder.getFrame();
    }

    public static void setFrame(WebFrame frame) {
    	SecurityContextHolder.setFrame(frame);
    }

    public static Kernel getKernel() {
    	return SecurityContextHolder.getKernel();
    }

	@Override
	public List<KrnObject> getObjects(KrnClass clazz) {
		try {
			InterfaceManager mgr = getFrame().getInterfaceManager();
			Cache cash = mgr.getCash();
			Set<ObjectRecord> recs = cash.getObjects(clazz.id, 0, null);
			List<KrnObject> res = new ArrayList<KrnObject>(recs.size());
			for (Record rec : recs) {
				res.add((KrnObject) rec.getValue());
			}
			return res;
		} catch (KrnException e) {
            Log log = getLog();
    		log.error(e, e);
		}
		return null;
	}
	
	@Override
	public List<KrnObject> find(KrnClass clazz, String path, Object value, KrnObject lang, int compOper) {
        Log log = getLog();
		try {
			long lid = lang != null ? lang.id : 0;
			final Kernel krn = getKernel();
			Pair[] ps = Utils.parsePath(path, krn);
			if (ps.length == 1) {
				KrnAttribute attr = (KrnAttribute) ps[0].first;
				KrnClass cls = clazz;
				KrnObject[] objs = krn.getObjectsByAttribute(cls.id, attr.id,
						lid, compOper, value, 0);
				return Arrays.asList((KrnObject[])objs);
			} else {
				log.error("$Objects.find: Требуется путь с глубиной 1");
			}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return null;
	}

	@Override
	public KrnObject createObject(KrnClass clazz) {
		InterfaceManager mgr = getFrame().getInterfaceManager();
		Cache cash = mgr.getCash();
		Record rec = cash.createObject(clazz.id);
		return (KrnObject) rec.getValue();
	}

	@Override
	public KrnObject createObject(KrnClass clazz, boolean save)
			throws KrnException {
		return createObject(clazz);
	}

	@Override
	public KrnObject createObject(KrnClass clazz, String uid) {
		InterfaceManager mgr = getFrame().getInterfaceManager();
		Cache cash = mgr.getCash();
		Record rec = cash.createObject(clazz.id, uid);
		return (KrnObject) rec.getValue();
	}

	@Override
	public Object exec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
		Kernel krn = getKernel();
    	String threadName = Thread.currentThread().getName();
    	try {
    		Thread.currentThread().setName(threadName + "(" + methodName + ")");
			ClassNode cnode = krn.getClassNode(clazz.id);
			KrnMethod m = cnode.getMethod(methodName);
			ASTStart expr = krn.getMethodTemplate(m);
			if (expr != null) {
				Map<String, Object> vc = new HashMap<String, Object>();
				vc.put("THIS", _this);
				vc.put("ARGS", args);
				long sClsId = krn.getClass(m.classId).parentId;
				if (sClsId > 0) {
					vc.put("SUPER", krn.getClass(sClsId));
				}
				evaluate(expr, vc, ifcWrp.getContext(), false, callStack, m);
				return vc.get("RETURN");
			}
			return null;
		} catch (Throwable e) {
			StringBuffer msg = new StringBuffer();
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
            Log log = getLog();
			log.error(msg, e);
			throw e;
		} finally {
			Thread.currentThread().setName(threadName);
		}
	}
	
	@Override
    public KrnClass getParentClass(KrnClass clazz) {
        try {
            return getKernel().getClass(clazz.parentId);
        } catch (KrnException e) {
            Log log = getLog();
            log.error(e, e);
        }
        return null;
    }

	@Override
    public KrnClass getAttrClass(KrnClass clazz, String attrName) {
        try {
            Kernel krn = getKernel();
            KrnAttribute attr = krn.getAttributeByName(clazz, attrName);
            return krn.getClass(attr.typeClassId);
        } catch (KrnException e) {
            Log log = getLog();
            log.error(e, e);
        }
        return null;
    }
	
	@Override
    public List<KrnAttribute> getAttributes(KrnClass clazz) {
        Kernel krn = getKernel();
        return new ArrayList<KrnAttribute>(krn.getAttributes(clazz));
    }

	@Override
	public KrnAttribute getAttribute(KrnClass clazz, String name) {
		try {
			return getKernel().getAttributeByName(clazz, name);
		} catch (KrnException e) {
            Log log = getLog();
			log.error(e);
		}
		return null;
	}

	@Override
	public void refreshObjects(KrnClass clazz) throws KrnException {
		// Not implemented
	}

	@Override
    public KrnClass getCls(KrnAttribute atter) {
        try {
            return getKernel().getClassNode(atter.classId).getKrnClass();
        } catch (KrnException e) {
            Log log = getLog();
            log.error(e, e);
        }
        return null;
    }
    
	@Override
    public KrnClass getType(KrnAttribute atter) {
        try {
            return getKernel().getClassNode(atter.typeClassId).getKrnClass();
        } catch (KrnException e) {
            Log log = getLog();
            log.error(e, e);
        }
        return null;
    }
    
	@Override
    public List<KrnAttribute> getRevAttributes(KrnAttribute atter) {
    	try {
    		KrnAttribute[] rattrs = getKernel().getRevAttributes(atter.id);
    		return Arrays.asList(rattrs);
    	} catch (KrnException e) {
            Log log = getLog();
    		log.error(e, e);
    	}
    	return new ArrayList<KrnAttribute>();
    }

	@Override
    public Object getAttr(KrnObject objj, String path, KrnObject lang, long trId) {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Object obj = objj;
        Cache cash = frame.getCash();
        try {
            PathElement2[] ps = Utils.parsePath2(path, krn);
            for (int i = 1; obj != null && i < ps.length; i++) {
                KrnAttribute attr = ps[i].attr;
                long[] ids = {((KrnObject) obj).id};
                long lid = (attr.isMultilingual) ? (lang != null ? lang.id : Utils.getDataLangId(krn)) : 0;
                SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
                if (!recs.isEmpty()) {
                    obj = get(ids[0], attr.id, lid, ps[i].index, recs);
                } else {
                    obj = null;
                }
            }
            if (obj == null && ps.length > 0) {
                if (ps[ps.length - 1].index == null) {
                    obj = Collections.EMPTY_LIST;
                }
            }
            return obj;
        } catch (Throwable e) {
            Log log = getLog();
    		log.error(e, e);
        }
        return null;
    }

	@Override
    public void setAttr(KrnObject objj, String path, Object value, KrnObject lang, long trId) throws KrnException {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Cache cash = frame.getCash();
        try {
            Pair[] ps = Utils.parsePath(path, krn);
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            long lid = lang != null ? lang.id
            		: attr.isMultilingual ? Utils.getDataLangId(krn) : 0;
            long[] ids = { objj.id };
            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
            int i = 0;
            if (attr.collectionType > 0) {
            	if(attr.collectionType==CollectionTypes.COLLECTION_SET) {
            		Record r_set=Funcs.findByValue(recs, objj.id, attr.id, lid, value);
                    if (r_set !=null) {
                        Log log = getLog();
                    	log.warn("Объект с таким идентификатором содержится в атрибуте типа набор!");
                    	return;
                    }
            	}
            	Record r = Funcs.findLast(recs, objj.id, attr.id, lid);
            	int lastIndex = r != null ? r.getIndex() : -1;
                if (p.second instanceof Number) {
                    i = ((Number)p.second).intValue();
                    if (i < 0) {
                        i = lastIndex + i + 1;
                    }
                } else if (p.second instanceof String){
                    i = lastIndex;
                } else {
                	i = lastIndex + 1;
                }
            }
            if (i < 0) {
            	i = 0;
            }
            Record rec = Funcs.find(recs, objj.id, attr.id, lid, i);
            if (rec != null) {
                cash.changeObjectAttribute(
                		rec, value, null);
            } else {
                cash.insertObjectAttribute(
                		objj, attr, i, lid, value, this);
            }
        } catch (Throwable e) {
            Log log = getLog();
    		log.error(e, e);
    		if (e instanceof KrnException)
    			throw e;
    		else
    			throw new KrnException("Ошибка при setAttr", 0, e);
        }
    }
    
	@Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, boolean cascade, long trId) throws KrnException {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Cache cash = frame.getCash();
        try {
            Pair[] ps = Utils.parsePath(path, krn);
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            long lid = attr.isMultilingual ? Utils.getDataLangId(krn) : 0;
            long[] ids = { objj.id };
            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
            int i = 0;
            if (attr.collectionType > 0) {
            	Record r = Funcs.findLast(recs, objj.id, attr.id, lid);
            	int lastIndex = r != null ? r.getIndex() : 0;
            	if (p.second == null) {
            		i = -1;
            	} else if (p.second instanceof Number) {
                    i = ((Number)p.second).intValue();
                    if (i < 0) {
                        i = lastIndex + i + 1;
                    }
                } else {
                    i = lastIndex;
                }
            }
            if (i == -1) {
            	for (Record rec : recs) {
            		Object v = rec.getValue();
            		if (cascade && v instanceof KrnObject) {
            			ObjectRecord r = cash.findRecord((KrnObject)v);
            			cash.deleteObject(r, this);
            		}
            		cash.deleteObjectAttribute(rec, this, false);
            	}
            } else {
	            Record rec = Funcs.find(recs, objj.id, attr.id, lid, i);
	            if (rec != null) {
            		Object v = rec.getValue();
            		if (cascade && v instanceof KrnObject) {
            			ObjectRecord r = cash.findRecord((KrnObject)v);
            			cash.deleteObject(r, this);
            		}
            		cash.deleteObjectAttribute(rec, this, false);
	            }
            }
        } catch (KrnException e) {
            Log log = getLog();
    		log.error(e, e);
    		throw e;
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, int index, long trId) throws Exception {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Cache cash = frame.getCash();
        Pair[] ps = Utils.parsePath(path, krn);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
        long lid = attr.isMultilingual ? Utils.getDataLangId(krn) : 0;
        Record rec = cash.getRecord(objj.id, attr, lid, index);
        if (rec != null) {
    		cash.deleteObjectAttribute(rec, this, false);
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, Object value, long trId) throws Exception {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Cache cash = frame.getCash();
        Pair[] ps = Utils.parsePath(path, krn);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
        long lid = attr.isMultilingual ? Utils.getDataLangId(krn) : 0;
        Record rec = cash.getRecord(objj.id, attr, lid, value);
        if (rec != null) {
    		cash.deleteObjectAttribute(rec, this, false);
        }
    }

	@Override
    public void delete(KrnObject objj, boolean delRefs, long trId) throws KrnException {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        try {
            SortedSet<ObjectRecord> recs = cash.findRecords(new KrnObject[] {objj});
            if (recs.size() > 0) {
                cash.deleteObject(recs.first(), this);
            }
        } catch (KrnException e) {
            Log log = getLog();
    		log.error(e, e);
    		throw e;
        }
    }

	@Override
    public boolean isLock(KrnObject objj) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
		String locker = krn.getLocker(objj.id);
		return locker!=null && locker.length()>0;
    }

	@Override
    public boolean isLock(KrnObject objj, KrnObject service) throws KrnException {
		Kernel krn = ClientOrLang.getKernel();
		long lockerId=-1;
		if(service==null){
	    	WebFrame frm = ClientOrLang.getFrame();
	    	
	        Activity act = getFrame().getSession().getTaskHelper().getActivityById(frm.getFlowId());
	        if (act != null)
				lockerId=act.processDefId[act.processDefId.length - 1];
	        else
	        	lockerId = 0;
		}else{
			lockerId=service.id;
		}
        return krn.isObjectLock(objj.id, lockerId);
    }

	@Override
    public void lock(KrnObject objj) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	WebFrame frm = ClientOrLang.getFrame();
    	
        Activity act = getFrame().getSession().getTaskHelper().getActivityById(frm.getFlowId());
        if (act != null)
            krn.lock(objj.id, act.processDefId[0], act.flowId);
        else
        	throw new KrnException(0, "Попытка заблокировать объект не в бизнес-процессе!");
    }

	@Override
    public void lock(KrnObject objj, KrnObject locker) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	WebFrame frm = ClientOrLang.getFrame();
    	
        Activity act = getFrame().getSession().getTaskHelper().getActivityById(frm.getFlowId());
        if (act != null)
            krn.lock(objj.id, locker.id, act.flowId);
        else
        	throw new KrnException(0, "Попытка заблокировать объект не в бизнес-процессе!");
    }
    
	@Override
    public boolean isLocked(KrnObject objj, KrnObject locker) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	return krn.isObjectLock(objj.id, locker.id);
    }

	@Override
    public List<KrnObject> getConflictLocker(KrnObject objj) {
    	Kernel krn = ClientOrLang.getKernel();
    	WebFrame frm = ClientOrLang.getFrame();
    	
        Activity act = getFrame().getSession().getTaskHelper().getActivityById(frm.getFlowId());
        if (act != null)
            return Arrays.asList(krn.getConflictLocker(objj.id,
                    act.flowId,act.processDefId.length>0?act.processDefId[0]:0));
        else
        	return Collections.emptyList();
    }

	@Override
    public KrnObject getLocker(KrnObject objj, KrnObject service) {
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        try {
            Cache cash = frame.getCash();
            KrnObject srv =
                    krn.getObjectsByUid(new String[] {service.uid}, cash.getTransactionId())[0];
            return krn.getLocker(objj.id, srv.id);
        } catch (KrnException e) {
    		getLog().error(e, e);
        }
        return null;
    }

	@Override
    public List<KrnObject> getLockers(KrnObject objj) {
    	Kernel krn = ClientOrLang.getKernel();
    	ArrayList<KrnObject> res=new ArrayList<KrnObject>();
        try {
            Collection<Lock> locks= krn.getLockers(objj.id);
            for(Lock lock:locks){
    			res.add(krn.getObjectById(lock.lockerId, 0));
            }
        } catch (KrnException e) {
        	getLog().error(e, e);
        }
        return res;
    }

	@Override
    public String getLocker(KrnObject objj){
    	try{
        	Kernel krn = ClientOrLang.getKernel();
	        return krn.getLocker(objj.id);
		} catch (KrnException e) {
			getLog().error(e, e);
		}
		return null;

    }

	@Override
    public void unlock(KrnObject objj) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	WebFrame frm = ClientOrLang.getFrame();
    	
        Activity act = getFrame().getSession().getTaskHelper().getActivityById(frm.getFlowId());
        
        if (act != null)
        	krn.unlock(objj.id, act.processDefId[0], act.flowId);
        else
        	throw new KrnException(0, "Попытка разблокировать объект не в бизнес-процессе!");
    }
    
	@Override
    public Object exec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
    	Kernel krn = ClientOrLang.getKernel();
    	String threadName = Thread.currentThread().getName();
    	try {
    		Thread.currentThread().setName(threadName + "(" + methodName + ")");
    		ClassNode cnode = krn.getClassNode(objj.classId);
    		KrnMethod m = cnode.getMethod(methodName);
    		ASTStart expr = krn.getMethodTemplate(m);
	        if (expr != null) {
	            Map<String, Object> vc = new HashMap<String, Object>();
	            vc.put("THIS", _this);
	            vc.put("ARGS", args);
	            KrnClass cls = krn.getClass(m.classId);
	            if (cls.parentId > 0) {
	            	KrnObject sobj = new KrnObject(objj.id, objj.uid, cls.parentId);
		            vc.put("SUPER", sobj);
	            }
                evaluate(expr, vc, ifcWrp.getContext(), false, callStack, m);
	            return vc.get("RETURN");
	        }
	        return null;
    	} catch (Throwable e) {
            StringBuffer msg = new StringBuffer();
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
    }

    private Object get(long objId, long attrId, long langId, Object index, SortedSet<Record> recs) {
        Record last = Funcs.findLast(recs, objId, attrId, langId);
        if (index == null) {
            Vector<Object> v = new Vector<Object>(recs.size());
            for (Record rec : recs) {
            	Object value = rec.getValue();
            	if (value instanceof KrnDate)
            		value = ((KrnDate)value).clone();
                v.add(value);
            }
            return v;
        }
        if (index instanceof Integer) {
            int i = recs.size() - 1;
            int j = ((Integer) index).intValue();
            if (j < 0) {
                i = i + 1 + j;
            } else {
                i = j;
            }
            Iterator<Record> rit = recs.iterator();
            Record r = null;
            while(rit.hasNext() && i-- >= 0) {
            	r = rit.next();
            }
            Object value = (r != null) ? r.getValue() : null;
        	if (value instanceof KrnDate)
        		value = ((KrnDate)value).clone();
            return value;
        }
        Object value = last.getValue();
    	if (value instanceof KrnDate)
    		value = ((KrnDate)value).clone();
        return value;
    }
    
    @Override
    public boolean isDel(KrnObject obj, long trId) throws KrnException {
    	boolean res = false;
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ClientOrLang.getKernel();
        Cache cash = frame.getCash();
        res = cash.isDel(obj);
        if(res == false) {
        	return krn.isDel(obj, trId);
        }
        return res;
    }
    
	@Override
    public boolean isDeleted(KrnObject objj) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
        return krn.isDeleted(objj);
    }

	@Override
	public KrnClass getCls(KrnObject objj) throws Exception {
    	Kernel krn = ClientOrLang.getKernel();
		return krn.getClassNode(objj.classId).getKrnClass();
	}

	@Override
    public Object sexec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
		long trId = 0;
    	OrFrame frame = ClientOrLang.getFrame();
    	if (frame != null) {
            Cache cash = frame.getCash();
            if (cash != null)
            	trId = cash.getTransactionId();
    	}
    	Kernel krn = ClientOrLang.getKernel();
    	Object res = krn.executeMethod(objj, _this, methodName, args, trId);
    	return res;
    }

	@Override
    public Object sexec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
		long trId = 0;
    	OrFrame frame = ClientOrLang.getFrame();
    	if (frame != null) {
            Cache cash = frame.getCash();
            if (cash != null)
            	trId = cash.getTransactionId();
    	}
    	Kernel krn = ClientOrLang.getKernel();
    	Object res = krn.executeMethod(clazz, _this, methodName, args, trId);
    	return res;
    }

	@Override
	public boolean like(KrnObject objj, KrnObject obj, KrnObject lang, long trId) {
		// NOT IMPLEMENTED
		return false;
	}

	@Override
	public List<String> getProcessLocker(KrnObject objj) {
		return null;
	}

	@Override
	public long getCurrentTransactionId() {
		long trId = 0;
    	OrFrame frame = ClientOrLang.getFrame();
    	if (frame != null) {
            Cache cash = frame.getCash();
            if (cash != null)
            	trId = cash.getTransactionId();
    	}

		return trId;
	}

	public Map<String, KrnObject[]> getObjsMap() {
		return ifcWrp.getObjMap();
	}

	@Override
	public boolean beforeSave(KrnObject objj) throws KrnException {
		return false;
	}

	@Override
	public KrnObject save(KrnObject objj) throws KrnException {
		return objj;
	}
	
	@Override
	public List<KrnObject> save(KrnClass cls, List<KrnObject> objjs, boolean executeTriggers, boolean logRecords) throws KrnException {
		return objjs;
	}

	public static Log getLog() {
		UserSessionValue us = getKernel().getUserSession();
    	return  WebSessionManager.getLog(us.dsName, us.logName);
	}

	@Override
	public void refreshObjects(List<KrnObject> objs) throws KrnException {}
	
	@Override
	public KrnClass getClassById(long classId) throws KrnException {
		return ClientOrLang.getKernel().getClass(classId);
	}

	@Override
	public KrnAttribute getAttrByPath(String path) throws KrnException {
		KrnAttribute attr = null;
		Kernel krn = ClientOrLang.getKernel();
        PathElement2[] ps = Utils.parsePath2(path, krn);
		if (ps != null && ps.length > 0) {
			PathElement2 p = ps[ps.length - 1];
			if (p.attr != null) {
				attr = p.attr;
			}
		}
		return attr;
	}

	@Override
	public long filterToAttr(KrnObject objj, KrnObject fobj, String path, Map<String, Object> params, long trId)
			throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	krn.clearFilterParams();
        String uid = fobj.getUID();
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        Pair[] ps = Utils.parsePath(path,krn);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
		return krn.filterToAttr(fobj, objj.id, attr.id, trId);
	}
}