package kz.tamur.lang;

import kz.tamur.lang.parser.ASTAccess;
import kz.tamur.lang.parser.ASTMethod;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.lang.parser.ASTString;
import kz.tamur.lang.parser.ASTVar;
import kz.tamur.lang.parser.Introspector;
import kz.tamur.lang.parser.Node;
import kz.tamur.lang.parser.Parser;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnAttributeOperations;
import com.cifs.or2.kernel.KrnClassOperations;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObjectOperations;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OrLang implements KrnClassOperations, KrnAttributeOperations, KrnObjectOperations {

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + OrLang.class.getName());
    
    public static final int BEFORE_OPEN_TYPE = 0;
    public static final int AFTER_OPEN_TYPE = 1;
    public static final int BEFORE_CLOSE_TYPE = 2;
    public static final int AFTER_CLOSE_TYPE = 3;
    public static final int CREATE_XML_TYPE = 4;
    public static final int BEFORE_MODIF_TYPE = 5;
    public static final int AFTER_MODIF_TYPE = 6;
    public static final int BEFORE_JOB_TYPE = 7;
    public static final int AFTER_JOB_TYPE = 8;
    public static final int BEFORE_ADD_TYPE = 9;
    public static final int AFTER_ADD_TYPE = 10;
    public static final int DYNAMIC_IFC_TYPE = 11;
    public static final int BEFORE_DELETE_TYPE = 12;
    public static final int AFTER_DELETE_TYPE = 13;
    public static final int BEFORE_DELETE_ROW_TYPE = 14;
    public static final int AFTER_DELETE_ROW_TYPE = 15;
    public static final int ACTION_TYPE = 16;
    public static final int CONSTRAINTS_TYPE = 17;
    public static final int AFTER_COPY_TYPE = 18;
    public static final int BEFORE_CLEAR_JOB_TYPE = 19;
    public static final int AFTER_MOVE_TYPE = 20;
    public static final int NEXT_PAGE_TYPE = 21;
    public static final int TITLE_EXPR_TEXT = 21;
    public static final int HYPER_POPUP_EXPR_TYPE = 22;
    public static final int X_AXIS = 23;
    public static final int Y_AXIS = 24;

    private static Introspector introspector = new Introspector();
//    private Map<Node, Map<Class, Method>> methodCache = new IdentityHashMap<Node, Map<Class,Method>>();

	public static ThreadLocal<ASTMethod> currMethod = new ThreadLocal<ASTMethod>();
	public static ThreadLocal<Map<Class, Map<Node, Method>>> methodCaches = new ThreadLocal<Map<Class, Map<Node, Method>>>();

	protected OrLang() {
    }

    public static ASTStart createStaticTemplate(String expr) {
    	return createStaticTemplate(expr, log);
    }
    
    public static ASTStart createStaticTemplate(String expr, Log log) {
        if (expr != null) {
            try {
                return createStaticTemplate(new StringReader(expr));
            } catch (RuntimeException e) {
                log.error("Ошибка в формуле: " + expr);
                throw e;
            }
        }
        return null;
    }

    public static ASTStart createStaticTemplate(Reader reader) {
        Parser p = new Parser(reader);
        try {
        	ASTStart start = p.Start();
        	processLiterals(start);
            return start;
        } catch (Throwable e) {
            StringTokenizer st = new StringTokenizer(e.getMessage(), "\n");
            throw new RuntimeException("Синтаксическая ошибка!\n" + st.nextToken());
        }
    }

    public static ASTStart createStaticTemplate(String expr, Collection<String> uids) {
    	return createStaticTemplate(expr, uids, log);
    }
    
    public static ASTStart createStaticTemplate(String expr, Collection<String> uids, Log log) {
        if (expr != null && expr.length() > 0) {
            Parser p = new Parser(new StringReader(expr));
            try {
                ASTStart res = p.Start();
            	processLiterals(res);
                findUids(res, uids);
                return res;
            } catch (Throwable e) {
                log.error("Ошибка в формуле: " + expr);
                log.error(e, e);
                StringTokenizer st = new StringTokenizer(e.getMessage(), "\n");
                throw new RuntimeException("Синтаксическая ошибка!\n" + st.nextToken());
            }
        }
        return null;
    }
    
    private static void findUids(Node node, Collection<String> uids) {
    	if (node instanceof ASTMethod) {
    		ASTMethod mnode = (ASTMethod) node;
    		if ("getObject".equals(mnode.getName())) {
        		Node parent = node.jjtGetParent();
        		if (parent instanceof ASTAccess && parent.jjtGetNumChildren() == 2) {
        			Node left = parent.jjtGetChild(0);
            		if (left instanceof ASTVar && "Objects".equals(((ASTVar) left).getName())) {
            			Node child = mnode.jjtGetChild(0);
            			if (child instanceof ASTString)
            				uids.add(((ASTString) child).getText());
            			return;
            		}
        		}
    		}
    	}
    	int childCount = node.jjtGetNumChildren();
    	for (int i = 0; i < childCount; i++)
    		findUids(node.jjtGetChild(i), uids);
    }
    
    private static Pattern newLinePtn = Pattern.compile("((?:^|[^\\\\])(?:\\\\\\\\)*)\\\\n");
    private static Pattern rPtn = Pattern.compile("((?:^|[^\\\\])(?:\\\\\\\\)*)\\\\r");
    private static Pattern tabPtn = Pattern.compile("((?:^|[^\\\\])(?:\\\\\\\\)*)\\\\t");
    private static Pattern backSlashPtn = Pattern.compile("\\\\", Pattern.LITERAL);
    private static Pattern quotePtn = Pattern.compile("\\\"", Pattern.LITERAL);
    
    private static void processLiterals(Node node) {
    	if (node instanceof ASTString) {
    		ASTString strNode = (ASTString)node;
    		String str = strNode.getText();
            str = newLinePtn.matcher(str).replaceAll("$1\n");
            str = rPtn.matcher(str).replaceAll("$1\r");
            str = tabPtn.matcher(str).replaceAll("$1\t");
            str = backSlashPtn.matcher(str).replaceAll(Matcher.quoteReplacement("\\"));
            str = quotePtn.matcher(str).replaceAll(Matcher.quoteReplacement("\""));
            strNode.setText(str);
    	}
    	int childCount = node.jjtGetNumChildren();
    	for (int i = 0; i < childCount; i++)
    		processLiterals(node.jjtGetChild(i));
    }
    
/*    private static ASTStartOpt optimize(ASTStart start) {
    	ASTStartOpt res = new ASTStartOpt(Parser.JJTSTART);
    	return res;
    }

	private static void optimize(SimpleNode node, Map<String, List<ASTMethod>> vars) {
		if (node instanceof ASTBlock || node instanceof ASTStart) {
			Map<String, List<ASTMethod>> newVars = new HashMap<String, List<ASTMethod>>(vars);
			for (int i = 0; i < node.jjtGetNumChildren(); i++)
				optimize((SimpleNode)node.jjtGetChild(i), newVars);
			for (String varName : newVars.keySet()) {
				List<ASTMethod> mnodes = newVars.get(varName);
				if (mnodes.size() > 1) {
					// Есть смысл оптимизировать
					String arbVarName = varName + "_" + "ARB";
					ASTSet arbSet = new ASTSet(Parser.JJTSET);
					ASTVar arbVar = new ASTVar(Parser.JJTVAR);
					arbVar.setName(arbVarName);
				}
			}
			
		} else if (node instanceof ASTMethod) {
			ASTMethod mnode = (ASTMethod)node;
			if ("getAttr".equals(mnode.getName())) {
				ASTAccess anode = (ASTAccess)mnode.jjtGetParent();
				if (anode.jjtGetNumChildren() == 2 && anode.jjtGetChild(1) == mnode && anode.jjtGetChild(0) instanceof ASTVar) {
					ASTVar vnode = (ASTVar)anode.jjtGetChild(0);
					//curr.add(vnode.getName());
				}
			}
		} else if (node instanceof ASTSet) {
			ASTSet snode = (ASTSet)node;
			if (snode.jjtGetChild(0) instanceof ASTVar) {
				ASTVar vnode = (ASTVar)snode.jjtGetChild(0);
				curr.remove(vnode.getName());
			}
		}
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
			optimize((SimpleNode)node.jjtGetChild(i));
	}
*/

    public Method getMethod(Node node, Class c, String name, Object[] params) throws Exception {
    	Method method = null;
    	Map<Class, Map<Node, Method>> classCache = methodCaches.get();
    	Map<Node, Method> methodCache = null;
    	if (classCache != null) {
        	methodCache = classCache.get(c);
        	if (methodCache == null) {
        		methodCache = new IdentityHashMap<Node, Method>();
        		classCache.put(c, methodCache);
        	} else {
        		method = methodCache.get(node);
        	}
    	}
    	if (method == null) {
        	method = introspector.getMethod(c, name, params);
        	if (methodCache != null && method != null) {
        		methodCache.put(node, method);
        	}
    	}
    	return method;
    }
    
	public static void setCurrentMethod(ASTMethod method) {
		if (method != null) {
			currMethod.set(method);
		} else {
			currMethod.remove();
		}
	}
	
    public abstract KrnAttribute getAttrByPath(String path) throws KrnException;
}