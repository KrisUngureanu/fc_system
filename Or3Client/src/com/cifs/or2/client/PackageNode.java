package com.cifs.or2.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cifs.or2.kernel.KrnClass;

public class PackageNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private Package pckg;
	private KrnClass cls;
	private String packageFullPath;
	
	public static Map<String, PackageNode> packageNodesByName = Collections.synchronizedMap(new HashMap<String, PackageNode>());
	public static Map<Long, PackageNode> packageNodesByClassId = Collections.synchronizedMap(new HashMap<Long, PackageNode>());
	
	public PackageNode() {
		super("Root package");
		pckg = new PackageConstructor().getRootPackage();
		createNodes(this);
	}
	
	public PackageNode(Package pckg, String packageFullPath) {
		super(pckg.getName());
		this.packageFullPath = packageFullPath;
		packageNodesByName.put(packageFullPath, this);
		this.pckg = pckg;
		createNodes(this);
	}
	
	public PackageNode(KrnClass cls, String nodeTitle) {
		super(nodeTitle);
		this.cls = cls;
		packageNodesByClassId.put(cls.id, this);
	}
	
	public Package getPackage() {
		return pckg;
	}
	
	public String getPackageFullPath() {
		return packageFullPath;
	}
	
	public boolean isRoot() {
		return pckg.getParent() == null;
	}
	
	public boolean isEmpty() {
		return pckg.getChildren().size() == 0 && pckg.getClasses().size() == 0;
	}

	public KrnClass getKrnClass() {
		return cls;
	}

	private void createNodes(PackageNode node) {
		Package pckg = node.getPackage();
		if (pckg != null) {
	    	for (Package child: pckg.getChildren()) {
	    		String packageFullPath = (node.packageFullPath == null ?  "" : (node.packageFullPath + "::")) + child.getName();
	    		PackageNode pn = new PackageNode(child, packageFullPath);
	    		add(pn);
	    	}
	    	for (KrnClass cls: pckg.getClasses()) {
	    		String nodeTitle = cls.getName();
	    		if (nodeTitle.contains("::")) {
	    			nodeTitle = nodeTitle.substring(nodeTitle.lastIndexOf("::") + 2);
	    		}
				node.add(new PackageNode(cls, nodeTitle));
	    	}

		}
    }
	
	public static PackageNode getPackageNodeByPackageName(String name) {
		return packageNodesByName.get(name);
	}
}