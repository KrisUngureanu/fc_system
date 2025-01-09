package kz.tamur.lang.parser;

public class ASTIndex extends SimpleNode {
  public ASTIndex(int id) {
    super(id);
  }

  public ASTIndex(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
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
}
