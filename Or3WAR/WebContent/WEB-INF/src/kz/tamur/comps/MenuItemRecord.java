package kz.tamur.comps;

/**
 * User: vital
 * Date: 20.01.2005
 * Time: 10:00:28
 */
public class MenuItemRecord {

    private String expr;
    private String title;

    public MenuItemRecord(String title, String expr) {
        this.title = title;
        this.expr = expr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MenuItemRecord) {
            return title.equals(((MenuItemRecord)obj).getExpr());
        }
        return false;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String toString() {
        return title;
    }
}
