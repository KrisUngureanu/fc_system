package kz.tamur.rt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kz.tamur.util.AbstractDesignerTreeNode;

public class TreeUIDMap {
	
	private static Map<String, AbstractDesignerTreeNode> treeUidMap = new HashMap<>();
	
	public static void put(String key, AbstractDesignerTreeNode node) {
		treeUidMap.put(key, node);
	}
	
	public static AbstractDesignerTreeNode get(String key) {
		return treeUidMap.get(key);
	}
	
	public static void clearMap(int nodeType) {
		Iterator<String> it = treeUidMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(treeUidMap.get(key).getNodeType() == nodeType) {
				it.remove();
			}
		}
	}
}
