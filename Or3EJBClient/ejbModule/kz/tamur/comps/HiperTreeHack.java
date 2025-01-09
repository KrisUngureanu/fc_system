package kz.tamur.comps;

import java.util.HashMap;
import java.util.Map;

//@todo Будет время надо избавиться
public class HiperTreeHack {
    private static Map<Long, OrHiperTree> htrees = new HashMap<Long, OrHiperTree>();

    public static OrHiperTree getHiperTree(long krnObjectId) {
        return htrees.get(krnObjectId);
    }

    public static void setHiperTree(OrHiperTree htree) {
        htrees.put(htree.getKrnObject().id, htree);
    }
}
