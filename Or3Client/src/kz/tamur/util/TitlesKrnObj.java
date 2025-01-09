package kz.tamur.util;

import com.cifs.or2.kernel.KrnObject;

/**
 * The Class TitlesKrnObj.
 *
 * @author Lebedev Sergey
 */
public class TitlesKrnObj {
    
    /** obj. */
    public KrnObject obj;
    
    /** title. */
    public String title;
    
    /** children. */
    //public List<Object> children;

    /**
     * Конструктор класса TitlesKrnObj.
     *
     * @param obj the obj
     * @param title the title
     * @param children the children
     */
    public TitlesKrnObj(KrnObject obj, String title) {//, List<Object> children) {
        super();
        this.obj = obj;
        this.title = title;
        //this.children = children;
    }
}
