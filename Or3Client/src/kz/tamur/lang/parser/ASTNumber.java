package kz.tamur.lang.parser;

public class ASTNumber extends SimpleNode {

  private Number value;
  
  public ASTNumber(int id) {
    super(id);
  }

  public ASTNumber(Parser p, int id) {
    super(p, id);
  }

  public Number getValue() {
    return value;
  }

  public void setValue(Number value) {
    this.value = value;
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
