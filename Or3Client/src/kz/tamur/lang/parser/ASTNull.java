/* Generated By:JJTree: Do not edit this line. ASTNull.java */

package kz.tamur.lang.parser;

public class ASTNull extends SimpleNode {
  public ASTNull(int id) {
    super(id);
  }

  public ASTNull(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}