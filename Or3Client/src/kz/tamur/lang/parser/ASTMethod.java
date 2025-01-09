package kz.tamur.lang.parser;



public class ASTMethod extends SimpleNode {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  public ASTMethod(int id) {
    super(id);
  }

  public ASTMethod(Parser p, int id) {
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
