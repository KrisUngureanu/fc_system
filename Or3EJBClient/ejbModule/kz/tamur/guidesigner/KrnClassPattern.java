package kz.tamur.guidesigner;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;

public class KrnClassPattern implements FindPattern {
    private long classId;

    public KrnClassPattern(long classId) {
        this.classId = classId;
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof ClassNode) {
            ClassNode node = (ClassNode)obj;
            return node.getKrnClass().id==this.classId;
        }
        return false;
    }
}
