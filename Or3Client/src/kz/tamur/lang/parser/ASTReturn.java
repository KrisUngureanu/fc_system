/* Generated By:JJTree: Do not edit this line. ASTReturn.java */

package kz.tamur.lang.parser;

public class ASTReturn extends SimpleNode {
  public ASTReturn(int id) {
    super(id);
  }

  public ASTReturn(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
