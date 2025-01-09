package kz.tamur.lang.parser;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import kz.tamur.lang.EvalException;

public class TranslatorVisitor implements ParserVisitor {

	private String className;
	private PrintWriter writer;

	public TranslatorVisitor(String className, PrintWriter writer) {
		this.className = className;
		this.writer = writer;
	}

	@Override
	public Object visit(SimpleNode node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTStart node, Object data) throws Exception {
		writer.println("package or3.compiled;");
		writer.println("public class " + className + " {");
		writer.println("public void run(java.util.Map<String,Object> vars) {");

		Object res = node.childrenAccept(this, data);

		writer.println("}");
		writer.println("}");
		return res;
	}

	@Override
	public Object visit(ASTDecl node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTSet node, Object data) throws Exception {
		ASTVar var = (ASTVar) node.children[0];
		writer.print("vars.put(\"" + var.getName() + "\",");
		node.children[1].jjtAccept(this, data);
		writer.println(");");
		return null;
	}

	@Override
	public Object visit(ASTReturn node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBreak node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTContinue node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTIf node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBlock node, Object data) throws Exception {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(ASTForeach node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTWhile node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTVar node, Object data) throws Exception {
		boolean cast = false;
		if (cast = "Systems".equals(node.getName())) {
			writer.print("((kz.tamur.or3ee.common.lang.SystemWrp)");
		}
		
		writer.print("vars.get(\"" + node.getName() + "\")");
		
		if (cast) {
			writer.print(")");
		}
		return null;
	}

	@Override
	public Object visit(ASTGlobalCall node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTExec node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTMethod node, Object data) throws Exception {
		writer.print(node.getName());
		writer.print("(");
		if (node.children != null) {
			node.children[0].jjtAccept(this, data);
			for (int i = 1; i < node.children.length; ++i) {
				writer.print(",");
				node.children[i].jjtAccept(this, data);
			}
		}
		writer.println(");");

		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) throws Exception {
		Iterator<String> it = node.getNames().iterator();
		writer.print(it.next());
		while (it.hasNext()) {
			writer.print(".");
			writer.print(it.next());
		}
		return null;
	}

	@Override
	public Object visit(ASTBinary node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTUnary node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTField node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTAccess node, Object data) throws Exception {
		node.children[0].jjtAccept(this, data);
		for (int i = 1; i < node.children.length; i++) {
			writer.print(".");
			data = node.children[i].jjtAccept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(ASTIndex node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTConstruct node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTNumber node, Object data) throws Exception {
		writer.print(node.getValue());
		return null;
	}

	@Override
	public Object visit(ASTString node, Object data) throws Exception {
		String text = node.getText().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
		writer.print("\"" + text + "\"");
		return null;
	}

	@Override
	public Object visit(ASTBoolean node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTNull node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTTry node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTCatch node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTFinally node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTThrow node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTFunc node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTFuncArgs node, Object data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
