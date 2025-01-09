package kz.tamur.lang.parser;

import java.util.List;
import java.util.ArrayList;

public class ASTIf extends SimpleNode {

    private SimpleNode cond;
    private ASTBlock block;
    private ASTBlock elseBlock;
    private List<ASTIf> elifs;

    public ASTIf(int id) {
        super(id);
    }

    public ASTIf(Parser p, int id) {
        super(p, id);
    }

    public List<ASTIf> getElifs() {
        return elifs;
    }

    public void addElif(ASTIf elif) {
        if (elifs == null) {
            elifs = new ArrayList<ASTIf>();
        }
        elifs.add(elif);
    }

    public SimpleNode getCondition() {
        return cond;
    }

    public void setCondition(SimpleNode cond) {
        this.cond = cond;
    }

    public ASTBlock getBlock() {
        return block;
    }

    public void setBlock(ASTBlock block) {
        this.block = block;
    }

    public ASTBlock getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(ASTBlock elseBlock) {
        this.elseBlock = elseBlock;
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(ParserVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }
}
