/* Generated By:JJTree: Do not edit this line. ASTAccess.java */

package kz.tamur.lang.parser;

public class ASTAccess extends SimpleNode {
  public ASTAccess(int id) {
    super(id);
  }

  public ASTAccess(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
    return visitor.visit(this, data);
  }
}