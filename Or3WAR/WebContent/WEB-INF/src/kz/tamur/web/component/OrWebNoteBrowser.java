package kz.tamur.web.component;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.guidesigner.noteeditor.NotePageNode;
import kz.tamur.web.common.WebSession;
import org.jdom.input.SAXBuilder;
import org.jdom.CDATA;
import org.jdom.Element;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: �������
 * Date: 11.10.2005
 * Time: 10:35:07
 * To change this template use File | Settings | File Templates.
 */
public class OrWebNoteBrowser {
    //private OrTextPane editor = new OrTextPane();
	private NotePageNode root;
    private KrnObject obj;
    private String title;
    private long langId;
    private boolean initialized = false;
    private WebSession session;
    private String html;
    private String href;
    
    private Map<Integer, NotePageNode> nodesById = new TreeMap<>();
    private int index = 0;

    public OrWebNoteBrowser(KrnObject krnObj, long langId, WebSession s) {
        super();
        this.session = s;
        this.obj = krnObj;
        this.langId = langId;
        this.title = getTitle(obj);
    }

    private synchronized void init() {
        if (!initialized)
            initialize();
    }

    public void release() {
    	obj = null;
    	html = null;
    	href = null;
    	root.release();
    	root = null;
    }
    
    private void initialize() {
        initialized = true;
        root = loadNotePage(obj);
    }

    public String getTitle() {
        return title;
    }

    public String getTitle(KrnObject obj) {
        try {
            long langId = this.langId;
            String[] titles = session.getKernel().getStrings(obj,"title",langId,0);
            if (titles.length > 0) {
                title = titles[0];
            } else {
                title = session.getResource().getString("noname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    public KrnObject getKrnObject() {
        return obj;
    }

    private NotePageNode loadNotePage(KrnObject obj) {
        org.jdom.Element xml;
        NotePageNode note = null;
        try {
            final Kernel krn = session.getKernel();
            long langId = this.langId;
            byte[] data = krn.getBlob(obj, "content", 0, langId, 0);
            if (data.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                SAXBuilder b = new SAXBuilder();
                xml = b.build(is).getRootElement();
                note = getTree(xml);
                is.close();
            } else {
                note = new NotePageNode(index++, "Начало", null);
            }
            nodesById.put(note.getId(), note);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    private NotePageNode getTree(org.jdom.Element e) {
        NotePageNode node = null;
        if (e.getName().equals("folder")) {
            String title = e.getAttributeValue("name");
            byte[] html = null;
            if (e.getContentSize() > 0) {
                java.util.List<?> list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText().getBytes();
                        break;
                    }
                }
            }

            node = new NotePageNode(index++, title, html);
            
            if (e.getChildren().size() > 0) {
                java.util.List<?> list = e.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Element) {
                        node.add(getTree((org.jdom.Element) list.get(i)));
                    }
                }
            }
        } else if (e.getName().equals("func")) {
            String title = e.getAttributeValue("name");
            byte[] html = null;
            if (e.getContentSize() > 0) {
                java.util.List<?> list = e.getContent();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof CDATA) {
                        html = ((CDATA)list.get(i)).getText().getBytes();
                        break;
                    }
                }
            }
            node = new NotePageNode(index++, title, html);
        }
        nodesById.put(node.getId(), node);
        return node;
    }
    
    public String getFolderJSON(String id, long lid) {
    	init();

    	JsonArray arr = new JsonArray();

    	int nodeId = (id == null) ? 0 : Integer.parseInt(id);
    	NotePageNode p = nodesById.get(nodeId);
    	
    	for (Enumeration<NotePageNode> en = p.children(); en.hasMoreElements(); ) {
    		NotePageNode child = en.nextElement();
    		
            JsonObject row = new JsonObject();
            row.add("id", child.getId());
        	row.add("text", child.toString());
        	row.add("state", child.isLeaf() ? "open" : "closed");
        	if (child.isLeaf()) {
	        	row.add("iconCls", "tree-folder");
        	}
        	row.add("parent", id);
       		arr.add(row);
    	}
        return arr.toString();
    }
    
    public String getContent(String id) {
    	init();

    	int nodeId = (id == null) ? 0 : Integer.parseInt(id);
    	NotePageNode p = nodesById.get(nodeId);
    	
        return p.getContent() != null ? new String(p.getContent()) : "-";
    }
}
