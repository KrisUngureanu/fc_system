package kz.tamur.guidesigner.noteeditor;

import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.Base64;
import kz.tamur.comps.Constants;
import java.util.Enumeration;

import com.cifs.or2.server.sgds.HexStringOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: �������
 * Date: 10.10.2005
 * Time: 16:04:16
 * To change this template use File | Settings | File Templates.
 */

public class NotePageNode extends AbstractDesignerTreeNode implements Comparable {

    private byte[] html;
    private String text;
    int id;

    public NotePageNode(int id, String title, byte[] html) {
        krnObj = null;
        isLoaded = false;
        this.title = title;
        this.html = html != null ? formatHtml(html) : null;
        this.id = id;
    }
    
    public int getId() {
    	return id;
    }
    
    public void release() {
    	for (Enumeration<NotePageNode> en = children(); en.hasMoreElements(); ) {
    		en.nextElement().release();
    	}
    	removeAllChildren();
    	text = null;
    	html = null;
    }

    public byte[] getContent() {
        return html;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
    	return getChildCount() > 0;
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
    
    private byte[] formatHtml(byte[] src) {
        String str = new String(src);
        
        StringBuffer buf = new StringBuffer();
        int beg = str.indexOf("src=\"");
        int end = 0;
        while (beg > -1) {
            buf.append(str.substring(end, beg + 5));
            end = str.indexOf("\"", beg + 5);
            String imgHex = str.substring(beg + 5, end);
            byte[] imgBytes = HexStringOutputStream.fromHexString(imgHex);

            buf.append("data:image/png;base64,").append(Base64.encodeBytes(imgBytes));
            
            beg = str.indexOf("src=\"", end);
        }
        buf.append(str.substring(end, str.length()));
        
        return buf.toString().getBytes();
    }
}


