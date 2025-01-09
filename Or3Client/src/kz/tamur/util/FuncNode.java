package kz.tamur.util;

import java.util.Locale;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 02.06.2005
 * Time: 17:22:32
 * To change this template use File | Settings | File Templates.
 */
public class FuncNode extends AbstractDesignerTreeNode implements Comparable {
    private String desc;
    private boolean isLeaf;
    public FuncNode(String title, String desc, boolean isLeaf, int index) {
        krnObj = null;
        isLoaded = false;
        this.title = title;
        this.desc = desc;
        this.isLeaf = isLeaf;
    }

    public String getDesc() {
        return desc;
    }

    protected void load() {
         if (!isLoaded) {
            isLoaded = true;
         }
    }

    public boolean equals(Object obj) {
         if (obj instanceof FuncNode) {
            FuncNode node = (FuncNode)obj;
            return (title.equals(node.toString()) && desc.equals(node.getDesc()));
        }
        return false;
    }


    public boolean isLeaf() {
        return isLeaf;
    }

    public void setDesc(String str) {
        desc = str;
    }

    public int compareTo(Object o) {
        if (o != null && o instanceof FuncNode) {
                String otitle = ((FuncNode) o).toString().toLowerCase(Constants.OK);
                if (title.toLowerCase(Constants.OK) != null && otitle.toLowerCase(Constants.OK) != null)
                    return title.toLowerCase(Constants.OK).compareTo(otitle.toLowerCase(Constants.OK));
                if (title.toLowerCase(Constants.OK) == otitle.toLowerCase(Constants.OK))
                    return 0;
                if (title == null)
                    return -1;
            }
            return 1;
    }
}
