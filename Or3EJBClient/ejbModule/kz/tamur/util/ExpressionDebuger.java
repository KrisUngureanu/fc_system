package kz.tamur.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kz.tamur.rt.Utils;
import kz.tamur.lang.ErrRecord;
import kz.tamur.lang.parser.ASTAccess;
import kz.tamur.lang.parser.ASTMethod;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.lang.parser.ASTString;
import kz.tamur.lang.parser.ASTVar;
import kz.tamur.lang.parser.Node;
import kz.tamur.rt.orlang.ClientOrLang;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

/**
 * Author: kazakbala Date: 12.07.2004 Time: 18:43:08
 */
public class ExpressionDebuger implements DebuggerInterface {

	private ClientOrLang lng;
	private Set<String> vars = new HashSet<String>();
    private List<ErrRecord> errs = new ArrayList<ErrRecord>();
    private KrnMethod sourceMethod;
    
    private static final boolean skipAccessModifiersInDebug = Boolean.parseBoolean(System.getProperty("skipAccessModifiersInDebug", "false"));
    
    public ExpressionDebuger(ClientOrLang lang) {
    	this.lng = lang;
    }

    public void clear() {
        vars.clear();
        errs.clear();
    }
    
    public void setSourceMethod(KrnMethod sourceMethod) {
    	this.sourceMethod = sourceMethod;
    }

    public void debugExpression(String module, String expr) {
        ASTStart ctemplate = createTemplate(module, expr, errs);
        if (ctemplate != null) {
            checkPaths(module, ctemplate, errs);
            checkUID(module, ctemplate, errs);
        }
    }

    @Override
    public void debugExpression(String module, String expr, Set<String> vars) {
        ASTStart ctemplate = createTemplate(module, expr, vars, errs);
        if (ctemplate != null) {
            checkPaths(module, ctemplate, errs);
            checkUID(module, ctemplate, errs);
        }
    }

    @Override
    public List<ErrRecord> getErrors() {
    	List<ErrRecord> errors = new ArrayList<>(errs);
    	Collections.sort(errors, new Comparator<ErrRecord>() {
			@Override
			public int compare(ErrRecord rec1, ErrRecord rec2) {
				return rec1.line - rec2.line;
			}
		});
    	return errors;
    }

    private ASTStart createTemplate(String module, String expr, List<ErrRecord> errs) {
        return lng.check(module, expr, vars, errs);
    }

    private ASTStart createTemplate(String module, String expr, Set<String> vars, List<ErrRecord> errs) {
        return lng.check(module, expr, vars, errs);
    }

    private void checkPaths(String module, Node t, List<ErrRecord> errs) {
        if (t instanceof ASTAccess) {
            Node node = t.jjtGetChild(0);
            if (node instanceof ASTVar) {
                String var = ((ASTVar) node).getName();
                String name;
                for (int i = 0; i < t.jjtGetNumChildren(); i++) {
                    node = t.jjtGetChild(i);
                    if (node instanceof ASTMethod) {
                        ASTMethod m = (ASTMethod) node;
                        name = m.getName();
                        if ("getAttr".equals(name) || "deleteAttr".equals(name)
                        		|| "createQuery".equals(name) || "addPath".equals(name) || "getClass".equals(name)
                        		|| "getClassObjects".equals(name)) {
                        	if (m.jjtGetNumChildren() > 0) {
	                            node = m.jjtGetChild(0);
	                            if (node == null) {
	                                errs.add(new ErrRecord(module, m.getLine(), var + "." + name));
	                            }
	                            if (node instanceof ASTString) {
	                                String path = ((ASTString) node).getText();
	                                checkPath(module, m.getLine(), path, errs, name);
	                            }
                        	}
                        } else if ("setAttr".equals(name)) {
                        	if (m.jjtGetNumChildren() < 2 || m.jjtGetNumChildren() > 4) {
                        		errs.add(new ErrRecord(module, m.getLine(), "Неверное количество параметров метода 'setAttr'"));
                        	} else {
	                            node = m.jjtGetChild(0);
	                            if (node instanceof ASTString) {
	                                String path = ((ASTString) node).getText();
	                                checkPath(module, m.getLine(), path, errs, name);
	                            } else {
	                        		errs.add(new ErrRecord(module, m.getLine(), "Неверный тип параметра 'путь' в методе 'setAttr'"));
	                            }
                        	}
                        } else if ("exec".equals(name) || "sexec".equals(name)) {
                        	if (m.jjtGetNumChildren() > 0) {
                        		node = m.jjtGetChild(0);
	                            if (node instanceof ASTString) {
	                                String methodName = ((ASTString) node).getText();
	                                Kernel krn = Kernel.instance();
	                                boolean isExistsMethod = false;
									try {
										KrnMethod[] methods = krn.getMethodsByName(methodName, 0);
										if (methods.length > 0) {
											isExistsMethod = true;
										}
									} catch (KrnException e) {
										e.printStackTrace();
									}
	                                if (!isExistsMethod) {
	                            		errs.add(new ErrRecord(module, m.getLine(), "Вызываемый метод '" + methodName + "' не существует"));
	                                }
	                            } else {
	                        		errs.add(new ErrRecord(module, m.getLine(), "Неверный тип параметра с именем вызываемого метода в блоке " + name));
	                            }
                        	} else {
                        		errs.add(new ErrRecord(module, m.getLine(), "Не задан параметр с именем вызываемого метода в блоке " + name));
                        	}
                        }
                    }
                }
            }
        }
        for (int i = 0; i < t.jjtGetNumChildren(); i++) {
            checkPaths(module, t.jjtGetChild(i), errs);
        }
    }

    private void checkUID(String module, Node t, List<ErrRecord> errs) {
        if (t instanceof ASTAccess) {
            Node node = t.jjtGetChild(0);
            if (node instanceof ASTVar) {
                String var = ((ASTVar) node).getName();
                String name;
                for (int i = 0; i < t.jjtGetNumChildren(); i++) {
                    node = t.jjtGetChild(i);
                    if (node instanceof ASTMethod) {
                        ASTMethod m = (ASTMethod) node;
                        name = m.getName();
                        if ("getObject".equals(name)) {
                        	if (m.jjtGetNumChildren() > 0) {
	                            node = m.jjtGetChild(0);
	                            if (node == null) {
	                                errs.add(new ErrRecord(module, m.getLine(), var + "." + name));
	                            }
	                            if (node instanceof ASTString) {
	                                String path = ((ASTString) node).getText();
	                                checkUID(module, m.getLine(), path, errs);
	                            }
                        	}
                        }
                    }
                }
            }
        }
        for (int i = 0; i < t.jjtGetNumChildren(); i++) {
            checkUID(module, t.jjtGetChild(i), errs);
        }
    }

    private void checkUID(String module, int line, String uid, List<ErrRecord> errs) {
        try {
            if (Kernel.instance().getCachedObjectByUid(uid) == null) {
                errs.add(new ErrRecord(module, line, "Не найден UID: " + uid));
            }
        } catch (KrnException e) {
            errs.add(new ErrRecord(module, line, "Ошибка при поиске UID: " + uid));
        }
    }

    private void checkPath(String module, int line, String path, List<ErrRecord> errs, String name) {
        try {
            String[] attrs = path.split("\\.");
            if (attrs.length > 0) {
                String path_attr = attrs[0].replaceAll("\\(.*?\\)", "");
                KrnAttribute[] atrib = Utils.getAttributesForPath(path_attr);
                if (atrib == null) {
                    errs.add(new ErrRecord(module, line, "Ошибка пути: " + path_attr));
                    return;
                }
                
                for (int i = 1; i < attrs.length; i++) {
                    // удалить двойные квадратные скобки
                    // удалить дополнение к пути, описанное в круглых скобках
                    path_attr += ".";
                    path_attr += attrs[i].replaceAll("\\[\\]", "").replaceAll("\\(.*?\\)", "");
                    atrib = Utils.getAttributesForPath(path_attr);
                    if (atrib == null) {
                        errs.add(new ErrRecord(module, line, "Ошибка пути: " + path_attr));
                        break;
                    } else if (!skipAccessModifiersInDebug && ("setAttr".equals(name) || "deleteAttr".equals(name))) {
                    	if (i == attrs.length - 1) {
                    		KrnAttribute attr = atrib[atrib.length - 1];
                    		if (attr.accessModifierType == 2) {	// Private
                    			if (sourceMethod == null || attr.classId != sourceMethod.classId) {
                            		errs.add(new ErrRecord(module, line, "Атрибут '" + attr.name + "' с модификатором доступа Private может быть модифицирован только из методов своего класса"));
                    			}
                    		} else if (attr.accessModifierType == 1) {	// Protected
                    			if (sourceMethod == null || !checkHierarchy(attr.classId, sourceMethod.classId)) {
                            		errs.add(new ErrRecord(module, line, "Атрибут '" + attr.name + "' с модификатором доступа Protected может быть модифицирован только из методов своего класса или подчиненных"));
                    			}
                    		}
                    	}
                    }
                }
            }
        } catch (Exception e1) {
            errs.add(new ErrRecord(module, line, "Ошибка пути: " + path));
        }
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
}