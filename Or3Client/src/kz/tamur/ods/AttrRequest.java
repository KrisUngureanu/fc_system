package kz.tamur.ods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class AttrRequest implements Serializable {

	public long parentAttrId;
	public long attrId;
	public long langId;
	public int index;
	public int sortIndex;
	
	private AttrRequest parent;
	private List<AttrRequest> children = new ArrayList<AttrRequest>();
	
	public AttrRequest(AttrRequest parent) {
		this.parent = parent;
		if (parent != null)
			parent.children.add(this);
	}
	
	public AttrRequest getParent() {
		return parent;
	}
	
	public void add(AttrRequest child) {
		child.parent = this;
		children.add(child);
	}
	
	public List<AttrRequest> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public List<AttrRequest> getDescendants() {
		List<AttrRequest> res = new ArrayList<AttrRequest>();
		for (AttrRequest child : children)
			child.fillDescendantsList(res);
		return res;
	}
	
	private void fillDescendantsList(List<AttrRequest> list) {
		list.add(this);
		for (AttrRequest child : children) {
			child.fillDescendantsList(list);
		}
	}
	
	public void remove(AttrRequest child) {
		children.remove(child);
	}
}
