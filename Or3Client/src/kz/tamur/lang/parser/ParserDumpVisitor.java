package kz.tamur.lang.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kz.tamur.lang.ErrRecord;

public class ParserDumpVisitor implements ParserVisitor {

    //private GlobalFunc gfunc;
	private final String module;
	private Set<String> vars;
	private List<ErrRecord> errs;

	private static Map<String, Class> decls = new HashMap<String, Class>();
	
	private Set<String> funcs = new HashSet<String>();
	
	public ParserDumpVisitor(String module,
							 Set<String> vars,
							 List<ErrRecord> errs) {
		this.module = module;
		this.vars = vars;
		this.errs = errs;
	}
	
	public Object visit(SimpleNode node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTStart node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTVar node, Object data) throws Exception {
		String name = node.getName();
		if (!vars.contains(name)) {
			String msg = "Переменная $" + name + " возможно не инициализирована.";
			errs.add(new ErrRecord(module, node.getLine(), msg));
		}
		return null;
	}

	public Object visit(ASTType node, Object data) throws Exception {
		String name = node.getNames().get(0);
		Class type = LangUtils.getType(name, null);
		if (type == null) {
			String msg = "Тип " + name + " не объявлен.";
			errs.add(new ErrRecord(module, node.getLine(), msg));
		}
		return null;
	}

	public Object visit(ASTSet node, Object data) throws Exception {
        ASTVar vnode = (ASTVar)node.children[0];
        vars.add(vnode.getName());
		node.children[1].jjtAccept(this, data);
		return null;
	}

	public Object visit(ASTIf node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTNumber node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTBoolean node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTAccess node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTMethod node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTConstruct node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTString node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTForeach node, Object data) throws Exception {
        ASTVar vnode = (ASTVar)node.children[0];
        node.children[1].jjtAccept(this, data);
        vars.add(vnode.getName());
		for (int i = 2; i < node.children.length; ++i) {
			node.children[i].jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTWhile node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTUnary node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTBinary node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTBlock node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTGlobalCall node, Object data) throws Exception {
        ASTMethod m = (ASTMethod)node.children[0];
        if (!funcs.contains(m.getName())) {
			String msg = "Функция " + m.getName() + " возможно не определена.";
			errs.add(new ErrRecord(module, m.getLine(), msg));
        }
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTIndex node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTField node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTExec node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTNull node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTDecl node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTBreak node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTContinue node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTReturn node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTTry node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	public Object visit(ASTCatch node, Object data) throws Exception {
        ASTVar vnode = (ASTVar)node.children[1];
        vars.add(vnode.getName());
		node.children[2].jjtAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTFinally node, Object data) throws Exception {
		return null;
	}

	@Override
	public Object visit(ASTThrow node, Object data) throws Exception {
		return null;
	}

	@Override
	public Object visit(ASTFunc node, Object data) throws Exception {
		funcs.add(node.getName());
		return null;
	}

	@Override
	public Object visit(ASTFuncArgs node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

/* end */
