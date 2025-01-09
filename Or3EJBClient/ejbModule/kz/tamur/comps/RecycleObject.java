package kz.tamur.comps;

import com.cifs.or2.kernel.KrnObject;

/**
 * The Class RecycleObject.
 *
 * @author Lebedev Sergey
 */
public class RecycleObject {
    
    /** Объект Корзины. */
    public KrnObject object;
    
    /** Удалённый объект. */
    public KrnObject value;
    
    /** Родитель удалённого объекта. */
    public KrnObject parentValue;
    
    /** Дата и время удаления. */
    public java.util.Date time;
    
    /** Удаливший пользователь. */
    public KrnObject user;
    
    /** Заголовок объекта. */
    public String title;

    /**
     * Конструктор класса recycle object.
     *
     * @param object the object
     * @param value the value
     * @param parentValue the parent value
     * @param time the time
     * @param user the user
     */
    public RecycleObject(KrnObject object, KrnObject value, KrnObject parentValue, java.util.Date time, KrnObject user) {
        super();
        this.object = object;
        this.value = value;
        this.parentValue = parentValue;
        this.time = time;
        this.user = user;
    }
}
