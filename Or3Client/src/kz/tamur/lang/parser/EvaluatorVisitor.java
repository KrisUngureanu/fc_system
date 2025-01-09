package kz.tamur.lang.parser;

import kz.tamur.lang.*;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalNumberFormat;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;

public class EvaluatorVisitor implements ParserVisitor {

    private Map<String, Object> vars;
    private GlobalFunc gfunc;
    private Stack<String> callStack;
    
    private OrLang orLang;

    private static Pattern varPtn = Pattern.compile("\\$(\\w+|(\\{(\\w+)\\}))");
    
    private int breaking = 0;
    
    private static final int BR_CONTINUE = 1;
    private static final int BR_BREAK = 2;
    private static final int BR_RETURN = 3;
    public static final int BR_INTERRUPT = 4;

    private static ThreadLocalNumberFormat longFormat_ = new ThreadLocalNumberFormat(null, (char)0, (char)0, false, 0, -1);
    private static ThreadLocalNumberFormat floatFormat_ = new ThreadLocalNumberFormat(null, '.', (char)0, false, 6, -1);

    private static int MAX_STACK_SIZE = Integer.parseInt(Funcs.normalizeInput(System.getProperty("lang.max_stack_size", "64")));
    
    private Map<String, ASTFunc> funcs = new HashMap<String, ASTFunc>();
    private Set<String> sysVars = new HashSet<String>();
    
    private KrnMethod sourceMethod;
    private static final boolean skipAccessModifiersInExecution = Boolean.parseBoolean(System.getProperty("skipAccessModifiersInExecution", "false"));
    
    public EvaluatorVisitor(
            Map<String, Object> vars,
            GlobalFunc gfunc,
            Stack<String> callStack,
            OrLang orLang, KrnMethod sourceMethod) {

        this.vars = vars;
        this.callStack = callStack;
        this.gfunc = gfunc;
        this.orLang = orLang;
        this.sysVars.addAll(vars.keySet());
    	this.sourceMethod = sourceMethod;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTStart node, Object data) throws Exception {
    	breaking = Thread.currentThread().isInterrupted() ? BR_INTERRUPT : 0;
    	if (breaking > 0)
    		return null;
    	try {
    		return node.childrenAccept(this, data);
    	} catch (EvalException e) {
    		throw new EvalException(e.getMessage(), makeErrorMessage(e.getMessage(), ""), e);
    	}
    }

    @Override
    public Object visit(ASTSet node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        ASTVar var = (ASTVar)node.children[0];
        vars.put(var.getName(), node.children[1].jjtAccept(this, data));
        return null;
    }

    @Override
    public Object visit(ASTIf node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        boolean cond = check(node.children[0].jjtAccept(this, data));
        if (cond) {
            node.getBlock().jjtAccept(this, data);
        }
        if (!cond) {
            List<ASTIf> elifs = node.getElifs();
            if (elifs != null) {
                for (int i = 0; i < elifs.size(); i++) {
                    ASTIf elif = elifs.get(i);
                    cond = check(elif.children[0].jjtAccept(this, data));
                    if (cond) {
                        elif.getBlock().jjtAccept(this, data);
                        break;
                    }
                }
            }
        }
        if (!cond) {
            ASTBlock elseBlock = node.getElseBlock();
            if (elseBlock != null) {
                elseBlock.jjtAccept(this, data);
            }
        }
        return null;
    }

    private boolean check(Object cond) {
        if (cond instanceof Boolean) {
            return ((Boolean)cond).booleanValue();
        } else {
            return cond != null;
        }
    }

    @Override
    public Object visit(ASTVar node, Object data) {
    	if (breaking > 0)
    		return null;
        return vars.get(node.getName());
    }

    @Override
    public Object visit(ASTType node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        int index = 1;
        StringBuilder name = new StringBuilder();
        List<String> names = node.getNames();
        name.append(names.get(0));
        Class type = LangUtils.getType(name.toString(), null);
        while (type == null && index < names.size()) {
            name.append('.').append(names.get(index++));
            type = LangUtils.getType(name.toString(), null);
        }
        Object res = type;
        if (type != null && index < names.size()) {
        	Class innerType = null;
            do {
        		name.append('$').append(names.get(index));
        		innerType = LangUtils.getType(name.toString(), null);
        		if (innerType != null) {
        			res = type = innerType;
        			index++;
        		}
            } while (innerType != null && index < names.size());    	
        	
            for (int i = index; i < names.size(); i++) {
                res = getField(res, names.get(i), node.getLine());
            }
        }
        return res;
    }

    @Override
    public Object visit(ASTDecl node, Object data) throws Exception {
        return null;
    }

    @Override
    public Object visit(ASTAccess node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        data = node.children[0].jjtAccept(this, data);
        for (int i = 1; i < node.children.length; i++) {
            data = node.children[i].jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        String name = node.getName();
        Object[] params = null;

        if (callStack.size() >= MAX_STACK_SIZE)
	        throw new EvalException(node.getLine() + ": Переполнение стэка вызовов функций.");
	    params = (Object[])node.childrenAccept(this, data);
	    if (data == null) {
	        throw new EvalException(node.getLine() + ": Вызов метода на нулевом объекте");
	    }
	    if (("exec".equals(name) || "sexec".equals(name)) && (data instanceof KrnObject || data instanceof KrnClass)) {
	        String or3method = (String)params[0];
	        List<Object> or3params = new ArrayList<Object>(params.length - 1);
	        for (int i = 1; i < params.length; or3params.add(params[i++]));
	        Object _this = data;
	        if (data.equals(vars.get("SUPER"))) {
	            _this = vars.get("THIS");
	            if (_this == null) {
	                _this = data;
	            }
	        }
	        params = new Object[]{_this, or3method, or3params, callStack};
	        callStack.push(node.getLine() + ": " + makeSignature(name, new Object[]{_this, or3method, or3params}));
	    } else
	    	callStack.push(node.getLine() + ": " + makeSignature(name, params));
	    	
	    Object res = null;
	    Method m = null;
	    if (data instanceof Class) {
	        m = orLang.getMethod(node, (Class)data, name, params);
	    }
	    if (m == null) {
	    	m = orLang.getMethod(node, data.getClass(), name, params);
	    }
	    if (m != null) {
	        try {
	        	OrLang.setCurrentMethod(node);
	        	
	        	Object[] finalParams = params;
	        	Class<?>[] mpars = m.getParameterTypes();
	        	if (mpars.length > 0 && mpars[mpars.length - 1].isArray() && 
	        			(params.length == mpars.length - 1 
	        			|| params.length > mpars.length 
	        			|| params.length == mpars.length && 
	        				(!params[params.length - 1].getClass().isArray() 
	        				|| (params[params.length - 1].getClass().getComponentType() == Byte.TYPE
	        					&& mpars[mpars.length - 1].getComponentType() != Byte.TYPE
	        					)
	        				)
	        			)
	        		) {
	        		finalParams = new Object[mpars.length];
	        		for (int i = 0; i < mpars.length - 1; i++) {
	        			finalParams[i] = params[i];
	        		}
	        		Object[] arrParam = new Object[params.length - mpars.length + 1];
	        		finalParams[mpars.length - 1] = arrParam;
	        		for (int i = mpars.length - 1; i < params.length; i++) {
	        			arrParam[i - mpars.length + 1] = params[i];
	        		}
	        	}
	        	
	        	if (!skipAccessModifiersInExecution && ("setAttr".equals(name) || "deleteAttr".equals(name))) {
	        		KrnAttribute attr = orLang.getAttrByPath((String) finalParams[0]);
	        		if (attr != null) {
		        		if (attr.accessModifierType == 2) {	// Private
	            			if (sourceMethod == null || attr.classId != sourceMethod.classId) {
	            				throw new EvalException(node.getLine() + ": Атрибут '" + attr.name + "' с модификатором доступа Private может быть модифицирован только из методов своего класса");
	            			}
	            		} else if (attr.accessModifierType == 1) {	// Protected
	            			if (sourceMethod == null || !checkHierarchy(attr.classId, sourceMethod.classId)) {
	            				throw new EvalException(node.getLine() + ": Атрибут '" + attr.name + "' с модификатором доступа Protected может быть модифицирован только из методов своего класса или подчиненных");
	            			}
	            		}
	        		}
	        	}
	        	
	            res = m.invoke(data, finalParams);
	        } catch (InvocationTargetException e) {
	        	if (callStack.empty() || callStack.peek().length() > 0) {
	            	if (e.getCause() instanceof EvalException)
	            		callStack.push(e.getCause().getMessage());
	            	else if (e.getCause() != null)
	            		callStack.push(e.getCause().toString());
	        		callStack.push("");
	        	}
	            throw new EvalException(node.getLine() + ": Ошибка при вызове метода:'"+name+"'", e.getCause());
	        } catch (Exception e) {
	    		callStack.push(e.getMessage());
	        	throw new EvalException(node.getLine() + ": Ошибка при вызове метода:'"+name+"' (" + e.getMessage() + ")", e);
	        } finally {
	        	OrLang.setCurrentMethod(null);
	        }
	    } else {
	        throw new EvalException(node.getLine() + ": Метод не найден");
	    }
	    callStack.pop();
	    return res;
    }
    
    private boolean checkHierarchy(long attrClsId, long sourceMthClsId) {
    	if (attrClsId == sourceMthClsId) {
    		return true;
    	} else {
			try {
				KrnClass cls = Kernel.instance().getClassById(sourceMthClsId);
				if (cls == null) {
					return false;
				} else {
					return checkHierarchy(attrClsId, cls.parentId);
				}
			} catch (KrnException e) {
				e.printStackTrace();
				return false;
			}
    	}
    }

    @Override
    public Object visit(ASTConstruct node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        Object[] params = new Object[node.children.length - 1];
        for (int i = 0; i < params.length; ++i) {
            params[i] = node.children[i + 1].jjtAccept(this, data);
        }
        Class cls = (Class)node.children[0].jjtAccept(this, data);
    	Method m = orLang.getMethod(node, cls, cls.getSimpleName(), params);
        if (m != null) {
            return m.invoke(null, params);
        }
        return null;
    }

    @Override
    public Object visit(ASTNumber node, Object data) {
    	if (breaking > 0)
    		return null;
        return node.getValue();
    }

    @Override
    public Object visit(ASTBoolean node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        return node.getValue();
    }

    @Override
    public Object visit(ASTNull node, Object data) throws Exception {
        return null;
    }

    @Override
    public Object visit(ASTString node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        String str = node.getText();
        try {
	        StringBuffer res = new StringBuffer(str.length() * 2);
	        Matcher matcher = varPtn.matcher(str);
	        while (matcher.find()) {
	            String var = matcher.group(3);
	            if (var == null) {
	                var = matcher.group(1);
	            }
	            Object value = vars.get(var);
	            if (value == null) {
	                value = matcher.group();
	            }
	            if (value instanceof Integer || value instanceof Long) {
	            	value = longFormat_.format(value);
	            }
	            else if (value instanceof Double) {
	            	value = floatFormat_.format(value);
	            }
	            matcher.appendReplacement(res, Matcher.quoteReplacement(value.toString()));
	        }
	        matcher.appendTail(res);
	        str = res.toString();
        } catch (java.lang.OutOfMemoryError e) {
        	throw new EvalException(node.getLine() + ": переполнение памяти!");
        }
        return str;
    }

    @Override
    public Object visit(ASTForeach node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        ASTVar var = (ASTVar)node.children[0];
        Object obj = node.children[1].jjtAccept(this, data);
        Collection<?> values = null;
        if (obj instanceof Collection) {
        	values = (Collection<?>)obj;
        } else if (obj instanceof JsonArray) {
        	values = ((JsonArray)obj).values();
        } else if (obj!=null && obj.getClass().isArray()) {
        	values = Arrays.asList((Object[])obj);
        }
        if (node.jjtGetNumChildren() > 2) {
            if (values == null) {
                throw new EvalException(node.getLine() + ": foreach с нулевым списком");
            }
            try {
	            for (Iterator<?> it = values.iterator(); it.hasNext() && !Thread.interrupted();) {
	                vars.put(var.getName(), it.next());
	                node.children[2].jjtAccept(this, data);
	            	if (breaking > 0) {
	            		if (breaking == BR_CONTINUE) {
	            			breaking = 0;
	            		} else if (breaking == BR_BREAK) {
	            			breaking = 0;
	            			break;
	            		} else {
	            			break;
	            		}
	            	}
	            }
            } catch (ConcurrentModificationException cme) {
                throw new EvalException(node.getLine() + ": foreach попытка модификации коллекции внутри цикла во время ее использования самим циклом", cme);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTWhile node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        int childCount = node.jjtGetNumChildren();
        if (childCount > 0) {
            while(toBoolean(node.children[0].jjtAccept(this, data))
            		&& !Thread.interrupted()) {
                if (childCount > 1) {
                    node.children[1].jjtAccept(this, data);
                }
            	if (breaking > 0) {
            		if (breaking == BR_CONTINUE) {
            			breaking = 0;
            		} else if (breaking == BR_BREAK) {
            			breaking = 0;
            			break;
            		} else {
            			break;
            		}
            	}
           }
        }
        return null;
    }

    public void setBreaking(int breaking) {
    	this.breaking = breaking;
    }
    
    @Override
    public Object visit(ASTUnary node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        int op = node.getOp();
        Object val = node.children[0].jjtAccept(this, data);
        if (val instanceof Number) {
            Number num = (Number)val;
            if (op == ASTUnary.MINUS) {
                return minus(num);
            } else if (op == ASTUnary.PLUS) {
                return num;
            }
        }
        if (op == ASTUnary.NOT) {
            return toBoolean(val) ? Boolean.FALSE : Boolean.TRUE;
        }
        return null;
    }

    @Override
    public Object visit(ASTBinary node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        int op = node.getOp(0);
        if (op == ASTBinary.AND) {
            return and(node, data);
        } else if (op == ASTBinary.OR) {
            return or(node, data);
        } else {
            Object[] params = (Object[])node.childrenAccept(this, data);
            Object res = params[0];
            for (int i = 1; i < node.children.length; i++) {
            	op = node.getOp(i - 1);
	            if (op == ASTBinary.ADD) {
	                res = add(node, res, params[i]);
	            } else if (op == ASTBinary.SUB) {
	                res = sub(node, (Number)res, (Number)params[i]);
	            } else if (op == ASTBinary.MUL) {
	                res = mul(node, (Number)res, (Number)params[i]);
	            } else if (op == ASTBinary.DIV) {
	                res = div(node, (Number)res, (Number)params[i]);
	            } else if (op == ASTBinary.MOD) {
	                res = mod(node, (Number)res, (Number)params[i]);
	            } else if (op == ASTBinary.BITAND) {
	                res = bitand(node, (Number)res, (Number)params[i]);
	            } else if (op == ASTBinary.BITOR) {
	                res = bitor(node, (Number)res, (Number)params[i]);
	            } else {
	                String opName = ASTBinary.OP_NAMES[op];
	                if (params[0] == null) {
	                    throw new EvalException(node.getLine() + ": '" + opName + "' левый операнд не определен");
	                } else if (params[1] == null) {
	                    throw new EvalException(node.getLine() + ": '" + opName + "' правый операнд не определен");
	                }
	                try {
		                if (op == ASTBinary.GE) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return !((KrnDate)params[0]).before((KrnDate)params[1]);
		                    } else {
		                        return new Boolean(((Number)params[0]).doubleValue()
		                                >= ((Number)params[1]).doubleValue());
		                    }
		                } else if (op == ASTBinary.GT) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return ((KrnDate)params[0]).after((KrnDate)params[1]);
		                    } else {
		                        return new Boolean(((Number)params[0]).doubleValue()
		                                > ((Number)params[1]).doubleValue());
		                    }
		                } else if (op == ASTBinary.LE) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return !((KrnDate)params[0]).after((KrnDate)params[1]);
		                    } else {
		                        return new Boolean(((Number)params[0]).doubleValue()
		                                <= ((Number)params[1]).doubleValue());
		                    }
		                } else if (op == ASTBinary.LT) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return ((KrnDate)params[0]).before((KrnDate)params[1]);
		                    } else {
		                        return new Boolean(((Number)params[0]).doubleValue()
		                                < ((Number)params[1]).doubleValue());
		                    }
		                } else if (op == ASTBinary.EQ) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return ((KrnDate)params[0]).equals((KrnDate)params[1]);
		                    } else if(params[0] instanceof Boolean && params[1] instanceof Boolean) {
		                        return ((Boolean)params[0]).equals((Boolean)params[1]);
		                    } else if(params[0] instanceof Boolean && params[1] instanceof Number) {
		                        return new Boolean(Math.abs(((Boolean)params[0] ? 1.0d : 0.0d)
		                                - ((Number)params[1]).doubleValue()) <= 1e-6);
		                    } else if(params[0] instanceof Number && params[1] instanceof Boolean) {
		                        return new Boolean(Math.abs(((Boolean)params[1] ? 1.0d : 0.0d)
		                                - ((Number)params[0]).doubleValue()) <= 1e-6);
		                    } else {
		                        return new Boolean(Math.abs(((Number)params[0]).doubleValue()
		                                - ((Number)params[1]).doubleValue()) <= 1e-6);
		                    }
		                } else if (op == ASTBinary.NE) {
		                    if(params[0] instanceof KrnDate && params[1] instanceof KrnDate) {
		                        return !((KrnDate)params[0]).equals((KrnDate)params[1]);
		                    } else if(params[0] instanceof Boolean && params[1] instanceof Boolean) {
		                        return !((Boolean)params[0]).equals((Boolean)params[1]);
		                    } else if(params[0] instanceof Boolean && params[1] instanceof Number) {
		                        return new Boolean(Math.abs(((Boolean)params[0] ? 1.0d : 0.0d)
		                                - ((Number)params[1]).doubleValue()) > 1e-6);
		                    } else if(params[0] instanceof Number && params[1] instanceof Boolean) {
		                        return new Boolean(Math.abs(((Boolean)params[1] ? 1.0d : 0.0d)
		                                - ((Number)params[0]).doubleValue()) > 1e-6);
		                    } else {
		                        return new Boolean(Math.abs(((Number)params[0]).doubleValue()
		                                - ((Number)params[1]).doubleValue()) > 1e-6);
		                    }
		                }
	                } catch (Throwable e) {
		                throw new EvalException(node.getLine() + ": " + e.getMessage());
	                }
	            }
            }
            return res;
        }
    }

    @Override
    public Object visit(ASTBlock node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTGlobalCall node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        ASTMethod m = (ASTMethod)node.children[0];
        Object[] params = null;

		if (callStack.size() >= MAX_STACK_SIZE)
            throw new EvalException(node.getLine() + ": Переполнение стэка вызовов функций.");
        params = (Object[])m.childrenAccept(this, data);
        callStack.push(m.getLine() + ": " + makeSignature(m.getName(), params));

        Object res = null;
        
        ASTFunc func = funcs.get(m.getName());
        if (func != null) {
        	res = visit(func, params);
        } else {
        	res = gfunc.invoke(m.getName(), params, callStack);
        }
        callStack.pop();
        return res;
    }

    @Override
    public Object visit(ASTIndex node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        int index = ((Number)node.children[0].jjtAccept(this, data)).intValue();
        if (data.getClass().isArray()) {
            return Array.get(data, index);
        } else if (data instanceof List) {
            return ((List)data).get(index);
        }
        return null;
    }

    @Override
    public Object visit(ASTField node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        String name = node.getName();
        return getField(data, name, node.getLine());
    }

    private Object getField(Object data, String name, int line) throws Exception {
        if (data == null) {
            String msg = line + ": Получение свойства \""
                    + name + "\" на нулевом объекте";
            throw new EvalException(msg);
        }
        Object res = null;
        Class cls = (data instanceof Class) ? (Class)data : data.getClass();
        if ("length".equals(name) && cls.isArray()) {
            res = new Integer(Array.getLength(data));
        } else if ("class".equals(name)) {
                res = cls;
        } else {
            Field field = cls.getField(name);
            if (field != null) {
                res = field.get(data);
            }
        }
        return res;
    }

    Object add(ASTBinary node, Object n1, Object n2) throws EvalException {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '+' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '+' правый операнд не определен");
        }
        try {
	        if (n1 instanceof String || n2 instanceof String) {
	            return new StringBuilder(n1.toString()).append(n2).toString();
	        } else if (n1 instanceof Double || n2 instanceof Double) {
	            return new Double(((Number)n1).doubleValue() + ((Number)n2).doubleValue());
	        } else if (n1 instanceof Float || n2 instanceof Float) {
	            return new Float(((Number)n1).floatValue() + ((Number)n2).floatValue());
	        } else if (n1 instanceof Integer && n2 instanceof Integer) {
	            return new Integer(((Number)n1).intValue() + ((Number)n2).intValue());
	        } else if (n1 instanceof Number || n2 instanceof Number) {
	            return new Long(((Number)n1).longValue() + ((Number)n2).longValue());
	        }
        } catch (Throwable e) {
            throw new EvalException(node.getLine() + ": " + e.getMessage());
        }
        return null;
    }

    Number sub(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '-' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '-' правый операнд не определен");
        }
        try {
	        if (n1 instanceof Double || n2 instanceof Double) {
	            return new Double(n1.doubleValue() - n2.doubleValue());
	        } else if (n1 instanceof Float || n2 instanceof Float) {
	            return new Float(n1.floatValue() - n2.floatValue());
	        } else if (n1 instanceof Integer && n2 instanceof Integer) {
	            return new Integer(n1.intValue() - n2.intValue());
	        } else {
	            return new Long(n1.longValue() - n2.longValue());
	        }
	    } catch (Throwable e) {
	        throw new EvalException(node.getLine() + ": " + e.getMessage());
	    }
    }

    Number mul(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '*' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '*' правый операнд не определен");
        }
        try {
	        if (n1 instanceof Double || n2 instanceof Double) {
	            return new Double(n1.doubleValue() * n2.doubleValue());
	        } else if (n1 instanceof Float || n2 instanceof Float) {
	            return new Float(n1.floatValue() * n2.floatValue());
	        } else if (n1 instanceof Integer && n2 instanceof Integer) {
	            return new Integer(n1.intValue() * n2.intValue());
	        } else {
	            return new Long(n1.longValue() * n2.longValue());
	        }
	    } catch (Throwable e) {
	        throw new EvalException(node.getLine() + ": " + e.getMessage());
	    }
    }

    Number div(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '/' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '/' правый операнд не определен");
        }
        try {
		    if (n1 instanceof Double || n2 instanceof Double) {
		        return new Double(n1.doubleValue() / n2.doubleValue());
		    } else if (n1 instanceof Float || n2 instanceof Float) {
		        return new Float(n1.floatValue() / n2.floatValue());
		    } else if (n1 instanceof Integer && n2 instanceof Integer) {
		        return new Integer(n1.intValue() / n2.intValue());
		    } else {
		        return new Long(n1.longValue() / n2.longValue());
		    }
		} catch (Throwable e) {
		    throw new EvalException(node.getLine() + ": " + e.getMessage());
		}
    }

    Number mod(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '%' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '%' правый операнд не определен");
        }
        try {
        	return new Long(n1.longValue() % n2.longValue());
		} catch (Throwable e) {
		    throw new EvalException(node.getLine() + ": " + e.getMessage());
		}
    }

    Number bitand(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '%' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '%' правый операнд не определен");
        }
        return new Long(n1.longValue() & n2.longValue());
    }

    Number bitor(ASTBinary node, Number n1, Number n2) throws Exception {
        if (n1 == null) {
            throw new EvalException(node.getLine() + ": '%' левый операнд не определен");
        } else if (n2 == null) {
            throw new EvalException(node.getLine() + ": '%' правый операнд не определен");
        }
        return new Long(n1.longValue() | n2.longValue());
    }

    Boolean and(ASTBinary node, Object data) throws Exception {
        boolean b = true;
        for (int i = 0; b && i < node.children.length; i++) {
            Object o = node.children[i].jjtAccept(this, data);
            if (o instanceof Boolean) {
                b = ((Boolean)o).booleanValue();
            } else {
                b = o != null;
            }
        }
        return new Boolean(b);
    }

    Boolean or(ASTBinary node, Object data) throws Exception {
        boolean b = false;
        for (int i = 0; !b && i < node.children.length; i++) {
            Object o = node.children[i].jjtAccept(this, data);
            if (o instanceof Boolean) {
                b = ((Boolean)o).booleanValue();
            } else {
                b = o != null;
            }
        }
        return new Boolean(b);
    }

    boolean toBoolean(Object o) {
        if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else {
            return o != null;
        }
    }

    Number minus(Number n) {
        if (n instanceof Double) {
            return new Double(-n.doubleValue());
        } else if (n instanceof Float) {
            return new Float(-n.floatValue());
        } else if (n instanceof Long) {
            return new Long(-n.longValue());
        } else if (n instanceof Integer) {
            return new Integer(-n.intValue());
        }
        return null;
    }

    public static String makeSignature(String name, Object[] params) {
        StringBuilder res = new StringBuilder();
        res.append(name);
        res.append('(');
        if (params != null && params.length > 0) {
            res.append((params[0] != null)
                    ? params[0].getClass().getSimpleName() : Object.class.getName());
            res.append("[");
            makeString(params[0], res);
            res.append("]");
            for (int i = 1; i < params.length; i++) {
                res.append(',');
                res.append((params[i] != null)
                        ? params[i].getClass().getName() : Object.class.getSimpleName());
                res.append('[');
                makeString(params[i], res);
                res.append(']');
            }
        }
        res.append(')');
        return res.toString();
    }
    
    private static void makeString(Object o, StringBuilder res) {
    	if (o == null) {
    		res.append("null");
    	} else if (o instanceof Collection) {
    		res.append('[');
    		Collection c = (Collection)o;
	    	Iterator it = c.iterator();
	    	int i = 0;
	    	for (; i < 10 && it.hasNext(); i++) {
	    		makeString(it.next(), res);
	    		res.append(',');
	    	}
	    	if (i >= 10)
	    		res.append("...");
    		res.append(']');
    	} else if (o instanceof Map) {
    		res.append('{');
    		Map m = (Map)o;
	    	int i = 0;
	    	for (Object key : m.keySet()) {
	    		if (i > 9) {
	    			res.append("...");
	    			break;
	    		}
	    		if (i > 0)
	    			res.append(',');
	    		makeString(key, res);
	    		res.append("=>");
	    		makeString(m.get(key), res);
	    	}
    		res.append('}');
    	} else {
    		res.append(o);
    	}
    }

    private String makeErrorMessage(String msg, String exceptionMessage) {
        StringBuilder res = new StringBuilder();
        int sz = callStack.size();
        for(int i = 0; i < sz; i++) {
        	String m = callStack.get(i);
        	if (m != null && m.length() > 0) {
        		res.append(m);
        		res.append("\n");
        	}
        }
        if (msg != null && (callStack.empty() || (callStack.peek() != null && callStack.peek().length() > 0)))
            res.append(msg);
        if (exceptionMessage != null)
            res.append(exceptionMessage);
        return res.toString();
    }

    @Override
    public Object visit(ASTExec node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        Object[] params = null;
        String fileName = null;

        if (callStack.size() >= MAX_STACK_SIZE)
            throw new EvalException(node.getLine() + ": Переполнение стэка вызовов функций.");
        params = (Object[])node.childrenAccept(this, data);
        fileName = (String) params[0];
        List<Object> args = new ArrayList<Object>(params.length - 1);
        for (int i = 1; i < params.length; args.add(params[i++]));
        callStack.push(node.getLine() + ": " + makeSignature("Exec '" + fileName + "' ", params));
        Object res = gfunc.exec(fileName, args, callStack);
        callStack.pop();
        return res;
    }

    @Override
    public Object visit(ASTBreak node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
    	breaking = BR_BREAK;
        return null;
    }

    @Override
    public Object visit(ASTContinue node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
    	breaking = BR_CONTINUE;
        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
    	Object val = node.children[0].jjtAccept(this, data);
    	vars.put("RETURN", val);
    	breaking = BR_RETURN;
        return val;
    }

	@Override
	public Object visit(ASTTry node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
    	Object res = null;
    	int stackSize = callStack.size();
    	try {
    		res = node.children[0].jjtAccept(this, data);
    	} catch (Throwable e) {
    		if (e instanceof EvalException)
    			e = new EvalException(makeErrorMessage(e.getMessage(), ""), e.getCause());
    		else
    			e.initCause(new EvalException(makeErrorMessage(e.getMessage(), ""), e.getCause()));

    		ASTCatch matchedCatch = null;
    		catchSearch : for (int i = 1; i < node.children.length; i++) {
    			Node n = node.children[i];
    			if (!(n instanceof ASTCatch))
    				break; // Дошли до finally
	    		ASTCatch c = (ASTCatch)node.children[i];
	    		String tn = ((ASTType)c.children[0]).getName();
	    		Class<?> cls = e.getClass();
	    		while (cls != null) {
	    			if (cls.getName().equals(tn)) {
	    				matchedCatch = c;
	        			break catchSearch;
	    			}
	    			cls = cls.getSuperclass();
	    		}
    		}
    		if (matchedCatch != null) {
    	        ASTVar var = (ASTVar)matchedCatch.children[1];
    	        vars.put(var.getName(), e);
    			matchedCatch.children[2].jjtAccept(this, data);
    		} else {
    			throw new Exception(e.getMessage(), e.getCause());
    		}
    		// Если мы обработали исключение, то убираем вызовы в трае из стека
    		while (callStack.size() > stackSize)
    			callStack.pop();
    	} finally {
    		Node lastNode = node.children[node.children.length - 1];
    		if (lastNode instanceof ASTFinally) {
    			lastNode.jjtAccept(this, data);
    		}
    	}
    	return res;
	}

	@Override
	public Object visit(ASTCatch node, Object data) throws Exception {
    	if (breaking > 0)
    		return null;
        return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(ASTFinally node, Object data) throws Exception {
        return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(ASTThrow node, Object data) throws Exception {
		throw (Exception)node.children[0].jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTFunc node, Object data) throws Exception {
		if (data instanceof Object[]) {
			Object[] params = (Object[])data;
			Map<String, Object> oldVars = this.vars;
			try {
				Map<String, Object> funcVars = new HashMap<String, Object>();
				for (String name : sysVars) {
					funcVars.put(name, vars.get(name));
				}
				if (node.children[0] instanceof ASTFuncArgs) {
					ASTFuncArgs args = (ASTFuncArgs)node.children[0];
					for (int i = 0; i < args.children.length && i < params.length; i++) {
						funcVars.put(((ASTVar)args.children[i]).getName(), params[i]);
					}
				}
				this.vars = funcVars;
				node.children[node.children.length - 1].jjtAccept(this, data);
				return funcVars.get("RETURN");
			} finally {
				breaking = 0;
				this.vars = oldVars;
			}
		} else {
			funcs.put(node.getName(), node);
		}
		return null;
	}

	@Override
	public Object visit(ASTFuncArgs node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}
}
