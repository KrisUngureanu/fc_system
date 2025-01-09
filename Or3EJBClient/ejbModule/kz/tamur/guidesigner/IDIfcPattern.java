package kz.tamur.guidesigner;

import java.util.Locale;

import javax.swing.tree.TreeNode;

import kz.tamur.guidesigner.hypers.HyperNode;

public class IDIfcPattern implements FindPattern {

	 private long id;

	    public IDIfcPattern(long id) {
	        this.id = id;
	    }

	    public boolean isMatches(Object obj) {
	     	 if (obj instanceof TreeNode) {
	          	HyperNode node = (HyperNode)obj;
	          	if (node.isLeaf()) {
	          		if(node.getIfcObject() != null){
	          			long id1 = node.getIfcObject().id;   	            
	       	            return id1 == id;
	          		}   	              	              	            
	          	}
	          }
	          return false;        	
	    } 
}
