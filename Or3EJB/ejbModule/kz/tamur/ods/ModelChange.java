package kz.tamur.ods;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.12.2005
 * Time: 9:59:17
 * To change this template use File | Settings | File Templates.
 */
public class ModelChange {
    public final long id;
    public final int entityType;
    public final int action;
    public final String entityId;
    public ModelChange(long id, int entityType, int action, String entityId) {
        this.id = id;
        this.entityType = entityType;
        this.action = action;
        this.entityId = entityId;
    }
}
