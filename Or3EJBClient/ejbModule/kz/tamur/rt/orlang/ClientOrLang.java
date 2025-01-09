package kz.tamur.rt.orlang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.OrFrame;
import kz.tamur.lang.BarcodeOp;
import kz.tamur.lang.ClientSignature;
import kz.tamur.lang.DateOp;
import kz.tamur.lang.ErrRecord;
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
import kz.tamur.lang.parser.ParserDumpVisitor;
import kz.tamur.ods.Lock;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.or3ee.client.lang.ClientSystemWrp;
import kz.tamur.or3ee.common.lang.SystemWrp;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.TaskTable;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.util.CollectionTypes;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

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

public class ClientOrLang extends OrLang implements KrnClassOperations, KrnAttributeOperations, KrnObjectOperations {

    private static Log log = LogFactory.getLog(ClientOrLang.class);

    private SystemWrp sysWrp;
    private static Objects objOps = new ClientObjects();
    private InterfaceWrp ifcWrp;
    private static DateOp dateOp = new DateOp();
    private BarcodeOp barOp = new BarcodeOp();
    private static StringOp strOp = new StringOp();
    private static MathOp mathOp = new MathOp();
    private static ClientSignature signOp = new ClientSignature();
    private static XmlOp xmlOp = new ClientXmlOp();
    private static GlobalFunc gfunc = new ClientGlobalFunc();
    private static SystemOp sysOp;
    private static Map<String, Object> plugins;
    private static Set<String> sysVars;
    private static int isSrv = 0;
    private ClientUniversalGenerator uniGen;
    
    private static ThreadLocal<JComponent> comp = new ThreadLocal<JComponent>();

    public Map<Long, EvaluatorVisitor> evalsMap = new HashMap<>();

    static {
        loadPlugins();
    }

    public ClientOrLang(OrFrame frame) {
    	this(frame, false);
    }
    
    public ClientOrLang(OrFrame frame, boolean beforeOpen) {
        Kernel krn = Kernel.instance();
        SecurityContextHolder.setFrame(frame);
        SecurityContextHolder.setKernel(krn);
        SecurityContextHolder.setLog(log);
        
        ifcWrp = new InterfaceWrp(frame, beforeOpen);
        sysWrp = new ClientSystemWrp(krn);
        sysOp = new kz.tamur.or3.client.lang.SystemOp(krn);
        uniGen = new ClientUniversalGenerator(krn); 
    }

    public boolean evaluate(String expr, Map<String, Object> vars, CheckContext ctx, Stack<String> callStack) {
    	return evaluate(expr, vars, ctx, true, callStack, -1);
    }

    public boolean evaluate(String expr, Map<String, Object> vars, CheckContext ctx, boolean wrap, Stack<String> callStack, long threadId) {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();

    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
            preprocess(vars, wrap);
            ifcWrp.setContext(ctx);
	        addMsgToCallStack(ctx, callStack);
            EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, null);
	        if (threadId > -1) {
	        	evalsMap.put(threadId, ev);
	        }
            ev.visit(OrLang.createStaticTemplate(expr), null);
            postprocess(vars, wrap);
            return true;
        } catch (Exception e) {
            log.error(e, e);
            return false;
		} finally {
	        KrnClass.setOperations(cls_ops);
	        KrnAttribute.setOperations(attr_ops);
	        KrnObject.setOperations(obj_ops);
		}
    }

    public void evaluate2(String expr, Map<String, Object> vars, CheckContext ctx, boolean wrap, Stack<String> callStack) throws Exception {
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();

    	KrnClass.setOperations(this);
    	KrnAttribute.setOperations(this);
    	KrnObject.setOperations(this);

    	try {
	        preprocess(vars, wrap);
	        ifcWrp.setContext(ctx);
	        addMsgToCallStack(ctx, callStack);
	        EvaluatorVisitor ev = new EvaluatorVisitor(vars, gfunc, callStack, this, null);
	        ev.visit(OrLang.createStaticTemplate(expr), null);
	        postprocess(vars, wrap);
    	} catch (Exception e) {
    		throw e;
    	} finally {
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
        	if(frm instanceof UIFrame){
	        	msg  += "ifcUid: " + ((UIFrame)frm).getUid();
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

    private static void loadPlugins() {
    	synchronized (ClientOrLang.class) {
	        if (plugins == null) {
	        	sysVars = new HashSet<String>();
	            sysVars.add("Systems");
	            sysVars.add("Objects");
	            sysVars.add("Interface");
	            sysVars.add("Date");
	            sysVars.add("BARCODE");
	            sysVars.add("Strings");
	            sysVars.add("Math");
	            sysVars.add("Xml");
	            sysVars.add("Gener");
	            sysVars.add("USER");
	            sysVars.add("BASE");
	            sysVars.add("THIS");
	            sysVars.add("SUPER");
	            sysVars.add("ARGS");
	            sysVars.add("INTERFACE");
	            sysVars.add("SERVER");
	            sysVars.add("FLOW");
	            sysVars.add("NULL");
	            sysVars.add("SELOBJ");
	            sysVars.add("XML");
	            sysVars.add("SELOBJS");
	            sysVars.add("Signature");
	            sysVars.add("Report");
	            sysVars.add("ILANG");
	            sysVars.add("Math");
	            sysVars.add("FORK_CONTEXTS");
	            sysVars.add("FORK_CONTEXT");
	            sysVars.add("SYSTEM");
	            sysVars.add("Signature");
	            sysVars.add("Numerator");
	            plugins = new HashMap<String, Object>();
	            InputStream is = null;
	            File xml = new File("clientplugs.xml");
	            if (!xml.exists()) {
	            	is = ClientOrLang.class.getResourceAsStream("/kz/tamur/or3/client/plugins/ClientPlugins.xml");
	            } else {
	            	try {
	            		is = new FileInputStream(xml);
	            	} catch (FileNotFoundException e) {
	            		log.error(e);
	            	}
	            }
	            if (is != null) {
	                SAXBuilder builder = new SAXBuilder();
	                List plugs = null;
	                try {
	                    plugs = builder.build(is).getRootElement().getChildren();
	                    for (int i = 0; i < plugs.size(); i++) {
	                        Element item = (Element)plugs.get(i);
	                        String var = item.getAttribute("var").getValue();
	                        sysVars.add(var);
	                        String classpath = item.getAttribute("class").getValue();
	                        try {
	                            Class<?> c = LangUtils.getType(classpath, null);
	                            if (c == null) throw new ClassNotFoundException(classpath);
	                            Object obj = c.newInstance();
	                            if (obj != null)
	                                plugins.put(var, obj);
	                        } catch (ClassNotFoundException e) {
	                            log.info(e, e);
	                            System.out.println("Ошибка при загрузке плагина [" + classpath + "]");
	                        } catch (IllegalAccessException e) {
	                            log.info(e, e);
	                        } catch (InstantiationException e) {
	                            log.info(e, e);
	                        }
	                    }
	                    is.close();
	                } catch (JDOMException e) {
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                System.out.println("Не найден файл для клиентских плагинов");
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

    public ASTStart check(String module, String expr, Set<String> vars, List<ErrRecord> errs) {
        ASTStart start = null;
        try {
        	start = OrLang.createStaticTemplate(expr);
        } catch (Throwable e) {
        	String msg = "Parsing expression: ";
        	errs.add(new ErrRecord(module, -1, msg + e.getMessage()));
        }
        if (start != null) {
        	try {
        		vars.addAll(sysVars);
            	ParserDumpVisitor v = new ParserDumpVisitor(module, vars, errs);
        		v.visit(start, null);
            } catch (Throwable e) {
            	String msg = "Evaluating: ";
            	errs.add(new ErrRecord(module, -1, msg + e.getMessage()));
            }
        }
        return start;
    }

    public static OrFrame getFrame() {
    	return (OrFrame)SecurityContextHolder.getFrame();
    }

    public static void setFrame(OrFrame frame) {
    	SecurityContextHolder.setFrame(frame);
    }

    public static Kernel getKernel() {
    	return SecurityContextHolder.getKernel();
    }

    public static JComponent getComponent() {
    	return comp.get();
    }

    public static void setComponent(JComponent jcomp) {
    	comp.set(jcomp);
    }

	@Override
	public List<KrnObject> getObjects(KrnClass clazz) {
		try {
			InterfaceManager mgr = InterfaceManagerFactory.instance()
					.getManager();
			Cache cash = mgr.getCash();
			SortedSet<ObjectRecord> recs = cash.getObjects(clazz.id, 0, null);
			List<KrnObject> res = new ArrayList<KrnObject>(recs.size());
			for (Record rec : recs) {
				res.add((KrnObject) rec.getValue());
			}
			return res;
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<KrnObject> find(KrnClass clazz, String path, Object value, KrnObject lang, int compOper) {
		try {
			long lid = lang != null ? lang.id : 0;
			final Kernel krn = getKernel();
			Pair[] ps = Utils.parsePath(path);
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
		InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
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
		InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
		Cache cash = mgr.getCash();
		Record rec = cash.createObject(clazz.id, uid);
		return (KrnObject) rec.getValue();
	}

	@Override
	public Object exec(KrnClass clazz, KrnClass _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
		Kernel krn = getKernel();
		try {
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
			e.printStackTrace();
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
			log.error(msg);
			throw e;
		}
	}
	
	@Override
    public KrnClass getParentClass(KrnClass clazz) {
        try {
            return getKernel().getClass(clazz.parentId);
        } catch (KrnException e) {
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
            log.error(e, e);
        }
        return null;
    }
    
	@Override
    public KrnClass getType(KrnAttribute atter) {
        try {
            return getKernel().getClassNode(atter.typeClassId).getKrnClass();
        } catch (KrnException e) {
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
    		e.printStackTrace();
    	}
    	return new ArrayList<KrnAttribute>();
    }

	@Override
    public Object getAttr(KrnObject objj, String path, KrnObject lang, long trId) {
    	OrFrame frame = ClientOrLang.getFrame();
        Object obj = objj;
        Cache cash = frame.getCash();
        try {
            PathElement2[] ps = Utils.parsePath2(path);
            for (int i = 1; obj != null && i < ps.length; i++) {
                KrnAttribute attr = ps[i].attr;
                long[] ids = {((KrnObject) obj).id};
                long lid = (attr.isMultilingual) ? (lang != null ? lang.id : Utils.getDataLangId()) : 0;
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
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

	@Override
    public void setAttr(KrnObject objj, String path, Object value, KrnObject lang, long trId) {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        try {
            Pair[] ps = Utils.parsePath(path);
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            long lid = lang != null ? lang.id
            		: attr.isMultilingual ? Utils.getDataLangId() : 0;
            long[] ids = { objj.id };
            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
            int i = 0;
            if (attr.collectionType > 0) {
            	if(attr.collectionType==CollectionTypes.COLLECTION_SET) {
            		Record r_set=Funcs.findByValue(recs, objj.id, attr.id, lid, value);
                    if (r_set !=null) {
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
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
    
	@Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, boolean cascade, long trId) {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        try {
            Pair[] ps = Utils.parsePath(path);
            Pair p = ps[ps.length - 1];
            KrnAttribute attr = (KrnAttribute)p.first;
            long lid = attr.isMultilingual ? Utils.getDataLangId() : 0;
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
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, KrnObject lang, int index, long trId) throws Exception {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        Pair[] ps = Utils.parsePath(path);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute)p.first;
        long lid = attr.isMultilingual ? Utils.getDataLangId() : 0;
        Record rec = cash.getRecord(objj.id, attr, lid, index);
        if (rec != null) {
    		cash.deleteObjectAttribute(rec, this, false);
        }
    }

    @Override
    public void deleteAttr(KrnObject objj, String path, Object value, long trId) throws Exception {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        Pair[] ps = Utils.parsePath(path);
        Pair p = ps[ps.length - 1];
        KrnAttribute attr = (KrnAttribute) p.first;
        long lid = 0;
    	if (attr.isMultilingual) {
	        if (value != null && ((KrnObject) value).classId == 101) {
	        	lid = ((KrnObject) value).id;
	        	long[] ids = { objj.id };
	            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
	            Record rec = Funcs.find(recs, objj.id, attr.id, lid, 0);
	            if (rec != null) {
	        		cash.deleteObjectAttribute(rec, this, false);
	            }
	            return;
	        } else {
        		lid = Utils.getDataLangId();
	        }
    	}
    	Record rec = cash.getRecord(objj.id, attr, lid, value);
        if (rec != null) {
    		cash.deleteObjectAttribute(rec, this, false);
        }
    }

	@Override
    public void delete(KrnObject objj, boolean delRefs, long trId) {
    	OrFrame frame = ClientOrLang.getFrame();
        Cache cash = frame.getCash();
        try {
            SortedSet<ObjectRecord> recs = cash.findRecords(new KrnObject[] {objj});
            if (recs.size() > 0) {
                cash.deleteObject(recs.first(), this);
            }
        } catch (KrnException e) {
            e.printStackTrace();
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
			Activity act = TaskTable.instance(false).getSelectedActivity();
			lockerId=act.processDefId[act.processDefId.length - 1];
		}else{
			lockerId=service.id;
		}
        return krn.isObjectLock(objj.id,lockerId);
    }

	@Override
    public void lock(KrnObject objj) throws KrnException {
        Kernel krn = ClientOrLang.getKernel();
        Activity act = TaskTable.instance(false).getSelectedActivity();
        /*if (objj.isLocked(objj)) {
            System.out.println("Объект уже заблокирован!");
        } else {*/
            krn.lock(objj.id, act.processDefId[0], act.flowId);
     //   }
    }

	@Override
    public void lock(KrnObject objj, KrnObject locker) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
        Activity act = TaskTable.instance(false).getSelectedActivity();
        krn.lock(objj.id, locker.id, act.flowId);
    }
	@Override
    public boolean isLocked(KrnObject objj, KrnObject locker) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
    	return krn.isObjectLock(objj.id, locker.id);
    }

	@Override
    public List<KrnObject> getConflictLocker(KrnObject objj) {
    	Kernel krn = ClientOrLang.getKernel();
        Activity act = TaskTable.instance(false).getSelectedActivity();
        return Arrays.asList(krn.getConflictLocker(objj.id,
                act.flowId,act.processDefId.length>0?act.processDefId[0]:0));
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
        return res;
    }
    
	@Override
    public String getLocker(KrnObject objj){
    	try{
        	Kernel krn = ClientOrLang.getKernel();
	        return krn.getLocker(objj.id);
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return null;

    }

	@Override
    public void unlock(KrnObject objj) throws KrnException {
    	Kernel krn = ClientOrLang.getKernel();
        Activity act = TaskTable.instance(false).getSelectedActivity();
        krn.unlock(objj.id,
        		act.processDefId[0],
                act.flowId);
    }
    
	@Override
    public Object exec(KrnObject objj, KrnObject _this, String methodName, List<Object> args, Stack<String> callStack) throws Throwable {
    	Kernel krn = ClientOrLang.getKernel();
    	try {
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
            log.error(msg);
            throw e;
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
	public KrnObject save(KrnObject objj) throws KrnException {
		return objj;
	}

	@Override
	public List<KrnObject> save(KrnClass cls, List<KrnObject> objjs, boolean executeTriggers, boolean logRecords) throws KrnException {
		return objjs;
	}
	
	@Override
	public boolean beforeSave(KrnObject objj) throws KrnException {
		return false;
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
        PathElement2[] ps = Utils.parsePath2(path);
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