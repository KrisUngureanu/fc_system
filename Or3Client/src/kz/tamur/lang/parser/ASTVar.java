package kz.tamur.lang.parser;

public class ASTVar extends SimpleNode {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.intern();
    }

  public ASTVar(int id) {
    super(id);
  }

  public ASTVar(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
