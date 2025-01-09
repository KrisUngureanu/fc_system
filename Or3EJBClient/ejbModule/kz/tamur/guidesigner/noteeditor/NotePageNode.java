package kz.tamur.guidesigner.noteeditor;

import kz.tamur.comps.Constants;
import kz.tamur.util.AbstractDesignerTreeNode;
import javax.swing.text.html.HTMLDocument;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 10.10.2005
 * Time: 16:04:16
 * To change this template use File | Settings | File Templates.
 */

public class NotePageNode extends AbstractDesignerTreeNode implements Comparable {

    private boolean isLeaf;
    private String header;
    private HTMLDocument htmlDoc;
    private String text;

    public NotePageNode(String title, HTMLDocument htmlDoc, boolean isLeaf, int index) {
        krnObj = null;
        isLoaded = false;
        this.title = title;
        if (htmlDoc == null)
            this.htmlDoc = kz.tamur.rt.Utils.createHTMLDocument();
        else
            this.htmlDoc = htmlDoc;
        this.isLeaf = isLeaf;
    }

    public HTMLDocument getContent() {
        return htmlDoc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setContent(HTMLDocument html) {
        this.htmlDoc = html;
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
        }
    }

    public boolean equals(Object obj) {
    	return this == obj;
    }


    public boolean isLeaf() {
        return isLeaf;
    }

    public int compareTo(Object o) {
        if (o != null && o instanceof NotePageNode) {
            String otitle = ((NotePageNode) o).toString().toLowerCase(Constants.OK);
            if (title.toLowerCase(Constants.OK) != null && otitle.toLowerCase(Constants.OK) != null)
                return title.toLowerCase(Constants.OK).compareTo(otitle.toLowerCase(Constants.OK));
            if (title.toLowerCase(Constants.OK) == otitle.toLowerCase(Constants.OK))
                return 0;
            if (title == null)
                return -1;
        }
        return 1;
    }

    public void setTitle(String str) {
        header = str;
    }

    public String getTitle() {
        return header;
    }
}


