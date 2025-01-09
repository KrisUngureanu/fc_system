package kz.tamur.rt.login;

import kz.tamur.util.AbstractDesignerTreeNode;
import org.jdom.Element;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class ServerNode extends AbstractDesignerTreeNode {

    protected boolean isLoaded = false;
    protected String title;
    protected String oldName;
    protected long langId;
    protected boolean isCopyProcessStarted = false;
    protected boolean isCutProcess = false;
    protected boolean isLeaf=false;
    protected Element xml;

    protected ServerNode(Element xml) {
        this.xml=xml;
        this.title=xml.getAttributeValue("name");
        this.isLeaf="true".equals(xml.getAttributeValue("isLeaf"));
    }

    public boolean isLeaf() {
        return isLeaf;
    }



    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                List nodes=xml.getChildren();
                    List children = new ArrayList();
                    for (int i = 0; i < nodes.size(); i++) {
                        Element node=(Element)nodes.get(i);
                        children.add(new ServerNode(node));
                    }
                    addAllChildren(children);
            }
        }
    }

    public void addAllChildren(List children) {
        for (int i = 0; i < children.size(); i++) {
            ServerNode node = (ServerNode)children.get(i);
            add(node);
        }
    }

    public Enumeration children() {
        load();
        return super.children();
    }

    public int getChildCount() {
        load();
        return super.getChildCount();
    }

    public int getLoadedChildCount() {
        return super.getChildCount();
    }

    public String toString() {
        return title;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AbstractDesignerTreeNode) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)obj;
            if(title!=null)
                return title.equals(node.toString());
        }
        return false;
    }

    public TreePath find(String name) {
        if (this.toString().equals(name)) {
            return new TreePath(getPath());
        }
        load();
        TreePath result = null;
        for (Enumeration c = children(); c.hasMoreElements();) {
            ServerNode child =
                    (ServerNode) c.nextElement();
            result = child.find(name);
            if (result != null)
                break;
        }
        return result;
    }

    public void rename(String newName) {
        title = newName;
        xml.setAttribute("name",title);
    }
    public String getOldName() {
        return oldName;
    }

    public void rename() {
        oldName = title;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public boolean isCopyProcessStarted() {
        return isCopyProcessStarted;
    }

    public void setCopyProcessStarted(boolean copyProcessStarted) {
        isCopyProcessStarted = copyProcessStarted;
    }
    public Element getXml(){
        return xml;
    }
    public boolean isCutProcess() {
        return isCutProcess;
    }

    public void setCutProcess(boolean cutProcess) {
        isCutProcess = cutProcess;
    }


}
