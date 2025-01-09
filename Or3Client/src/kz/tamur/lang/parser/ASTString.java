package kz.tamur.lang.parser;

public class ASTString extends SimpleNode {

    private String text;

  public ASTString(int id) {
    super(id);
  }

  public ASTString(Parser p, int id) {
    super(p, id);
  }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.intern();
    }

  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
