package kz.tamur.lang.parser;

import java.util.ArrayList;
import java.util.List;

public class ASTBinary extends SimpleNode {
    public static final int MUL = 1;
    public static final int DIV = 2;
    public static final int ADD = 3;
    public static final int SUB = 4;
    public static final int GT = 5;
    public static final int LT = 6;
    public static final int GE = 7;
    public static final int LE = 8;
    public static final int EQ = 9;
    public static final int NE = 10;
    public static final int AND = 11;
    public static final int OR = 12;
    public static final int MOD = 13;
    public static final int BITAND = 14;
    public static final int BITOR = 15;

    private List<Integer> ops = new ArrayList<Integer>();
    
    public static final String[] OP_NAMES = {
    	"NOP", "*", "/", "+", "-", ">", "<", ">=", "<=", "==", "!=",
    	"&&", "||", "%", "&", "|"};

    public void addOp(int op) {
    	ops.add(op);
    }
    
    public int getOp(int index) {
    	return ops.get(index);
    }

  public ASTBinary(int id) {
    super(id);
  }

  public ASTBinary(Parser p, int id) {
    super(p, id);
  }

    public Object childrenAccept(ParserVisitor visitor, Object data) throws Exception {
        if (children != null) {
            Object[] res = new Object[children.length];
          for (int i = 0; i < children.length; ++i) {
            res[i] = children[i].jjtAccept(visitor, data);
          }
            return res;
        }
        return new Object[0];
    }

  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
