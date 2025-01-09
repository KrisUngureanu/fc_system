package com.cifs.or2.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cifs.or2.kernel.KrnClass;

public class Package {
	
	private String name;
	private Package parent;
	private List<Package> children = Collections.synchronizedList(new ArrayList<Package>());
	private List<KrnClass> classes = Collections.synchronizedList(new ArrayList<KrnClass>());
	
	public Package(String name, Package parent) {
		this.name = name;
		this.parent = parent;
		if (parent != null) { 
			parent.addChild(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Package getParent() {
		return parent;
	}

	public void setParent(Package parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return parent == null;
	}
	
	public List<Package> getChildren() {
		return children;
	}
	
	public void addChild(Package child) {
		children.add(child);
	}
	
	public List<KrnClass> getClasses() {
		return classes;
	}
	
	public void addClass(KrnClass cls) {
		classes.add(cls);
	}

}