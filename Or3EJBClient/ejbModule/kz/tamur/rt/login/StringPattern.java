package kz.tamur.rt.login;

import kz.tamur.guidesigner.FindPattern;
import kz.tamur.util.AbstractDesignerTreeNode;


public class StringPattern implements FindPattern {
    private String name;

    public StringPattern(String name) {
        this.name = name;
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof AbstractDesignerTreeNode) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)obj;
            return node.toString().equals(this.name);
        }
        return false;
    }
}

