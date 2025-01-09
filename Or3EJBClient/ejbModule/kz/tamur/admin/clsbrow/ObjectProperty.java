package kz.tamur.admin.clsbrow;


import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import javax.swing.*;
import java.util.*;

public abstract class ObjectProperty {
    private ObjectProperty parent;
    private List<ObjectProperty> children;

    private KrnAttribute attr;

    protected ObjectProperty(ObjectProperty parent, KrnAttribute attr) {
        this.attr=attr;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }
    public KrnAttribute getAttr(){
        return attr;
    }

    public String getId() {
        return ""+attr.id;
    }

    public String getType() {
        String type="";
        try {
            KrnClass cls= Kernel.instance().getClass(attr.typeClassId);
            type=cls.name+(attr.isMultilingual?".M":"")+(attr.collectionType>0?"[]":"");
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return type;
    }

    public String getTtitle() {
        return attr.name;
    }



    public ObjectProperty getParent() {
        return parent;
    }

    protected void addChild(ObjectProperty child) {
        if (children == null) {
            children = new ArrayList<ObjectProperty>();
        }
        children.add(child);
    }

    public void addChildren(List<ObjectProperty> children) {
        if (this.children == null) {
            this.children = new ArrayList<ObjectProperty>(children);
        } else {
            this.children.addAll(children);
        }
    }

    public List<ObjectProperty> getChildren() {
        if (children != null) {
            return children;
        }
        return Collections.emptyList();
    }
    public ObjectProperty getChild(String id){
        for(ObjectProperty prop:children){
            if(prop.getId().equals(id))
                return prop;
        }
        return null;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    public void removeAllChildren() {
        if (children != null) {
            children.clear();
        }
    }



	@Override
    public String toString() {
        return attr.name;
    }

    public abstract ObjectEditorDelegate createEditorDelegate(JTable table);
    public  ObjectRendererDelegate createRendererDelegate(JTable table){
        return null;
    }

}
