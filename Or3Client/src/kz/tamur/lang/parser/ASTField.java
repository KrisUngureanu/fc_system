package kz.tamur.lang.parser;


public class ASTField extends SimpleNode {

	private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

  public ASTField(int id) {
    super(id);
  }

  public ASTField(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
