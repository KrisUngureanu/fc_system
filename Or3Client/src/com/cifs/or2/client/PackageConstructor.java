package com.cifs.or2.client;

import java.util.HashMap;
import java.util.Map;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class PackageConstructor {
	
	private Kernel kernel = Kernel.instance();
	
	private Map<String, Package> packages = new HashMap<>();
	
	private Package rootPackage = new Package("Package folder", null);
	private Package defaultPackage = new Package("(default package)", rootPackage);
	
	public PackageConstructor() {
    	packages.put(rootPackage.getName(), rootPackage);
    	packages.put(defaultPackage.getName(), defaultPackage);

    	try {
	    	KrnClass[] allClasses = kernel.getClasses();
	    	for (int i = 0; i < allClasses.length; i++) {
	    		KrnClass cls = allClasses[i];
	    		String[] parts = cls.getName().split("::");
				Package pckg = null;
				for (int j = 0; j < parts.length; j++) {
					if (j == parts.length - 1) {
						if (pckg == null) {
							defaultPackage.addClass(cls);
						} else {
							pckg.addClass(cls);
						}
					} else {
						if (packages.containsKey(parts[j])) {
							pckg = packages.get(parts[j]);
						} else {
							pckg = new Package(parts[j], pckg == null ? rootPackage : pckg);
							packages.put(pckg.getName(), pckg);
						}
					}
				}
	    	}
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }
	
	public Package getRootPackage() {
		return rootPackage;
	}
    
    public String toString() {
    	String prefix = "";
    	return visit(rootPackage, prefix); 
    }
    
    private String visit(Package pckg, String prefix) {
    	StringBuilder sb = new StringBuilder((pckg.isRoot() ? "" : "\n") + prefix + pckg.getName());
    	prefix += "\t"; 
//    	for (Package child: pckg.getChildren()) {
//    		sb.append("\n" + prefix + "пакет_" + child.getName());
//    	}
    	for (Package child: pckg.getChildren()) {
    		sb.append(visit(child, prefix));
    	}
    	for (KrnClass cls: pckg.getClasses()) {
    		String clsName = cls.getName();
//    		if (clsName.contains("::")) {
//    			clsName = clsName.substring(clsName.lastIndexOf("::") + 2);
//    		}
    		sb.append("\n" + prefix + "класс_" + clsName);
    	}
    	return sb.toString();	
    }
}