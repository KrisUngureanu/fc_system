package kz.tamur.guidesigner;

import kz.tamur.guidesigner.hypers.HyperNode;
import kz.tamur.util.AbstractDesignerTreeNode;

import java.util.Locale;

import javax.swing.tree.TreeNode;

/**
 * Date: 07.06.2018
 *
 */
public class UIDIfcPattern implements FindPattern {
    private String str;

    public UIDIfcPattern(String str) {
        this.str = str.toLowerCase(Locale.ROOT);
    }

    public boolean isMatches(Object obj) {
     	 if (obj instanceof TreeNode) {
          	HyperNode node = (HyperNode)obj;
          	if (node.isLeaf()) {
          		if(node.getIfcObject() != null){
          			String s1 = node.getIfcObject().uid;   	            
       	            return s1.equals(str); 
          		}   	              	              	            
          	}
          }
          return false;        	
    } 
}