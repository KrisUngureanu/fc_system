package kz.tamur.guidesigner;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;

/**
 * The Class LongPattern.
 * 
 * @author Lebedev Sergey
 */
public class LongPattern implements FindPattern {
    private long id;

    /**
     * Конструктор класса long pattern.
     * 
     * @param str
     *            str.
     */
    public LongPattern(long id) {
        this.id = id;
    }

    @Override
    public boolean isMatches(Object obj) {
        if (obj instanceof ClassNode) {
            long id = ((ClassNode) obj).getKrnClass().id;
            return this.id == id;
        }
        return false;
    }
}
