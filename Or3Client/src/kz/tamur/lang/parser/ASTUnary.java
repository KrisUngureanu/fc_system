package kz.tamur.lang.parser;

public class ASTUnary extends SimpleNode {

    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int NOT = 3;

    private int op;

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

  public ASTUnary(int id) {
    super(id);
  }

  public ASTUnary(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
