package kz.tamur.or3.client.props;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

public abstract class Property {

	private Property parent;
	private PropertyNode node;
	private List<Property> children;

	private String title;
	private String id;
	
	public Property(Property parent, PropertyNode node) {
		this.parent = parent;
		this.node = node;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	protected Property(Property parent, String id, String title) {
		this.id = id;
		this.title = title;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	public String getId() {
		return node != null ? node.getName() : id;
	}
	
	public String getTtitle() {
		return title;
	}
	
	public PropertyNode getNode() {
		return node;
	}

	public void setNode(PropertyNode node) {
		this.node = node;
	}

	public Property getParent() {
		return parent;
	}
	
	protected void addChild(Property child) {
		if (children == null) {
			children = new ArrayList<Property>();
		}
		children.add(child);
	}
	
	public void addChildren(List<Property> children) {
		if (this.children == null) {
			this.children = new ArrayList<Property>(children);
		} else {
			this.children.addAll(children);
		}
	}
	
	public List<Property> getChildren() {
		if (children != null) {
			return children;
		}
		return Collections.emptyList();
	}
	
    public Property getChild(String id){
        if(children!=null){
            for(Property prop:children){
                if(prop.getId().equals(id))
                    return prop;
            }
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
		return node != null ? node.toString() : title;
	}

	public abstract EditorDelegate createEditorDelegate(JTable table);
	
	public RendererDelegate createRendererDelegate(JTable table) {
		return null;
	}
}
