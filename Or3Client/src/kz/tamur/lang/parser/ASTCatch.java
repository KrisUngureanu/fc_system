/* Generated By:JJTree: Do not edit this line. ASTCatch.java */

package kz.tamur.lang.parser;

public class ASTCatch extends SimpleNode {
  public ASTCatch(int id) {
    super(id);
  }

  public ASTCatch(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}
