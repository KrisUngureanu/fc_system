package kz.tamur.ods;


public class TriggerChange extends ModelChange {
	
	public String name;
	public int type;
	public String expr;
	public int tr;

	public TriggerChange(long id, int action, String entityId, String name, int type, String expr, int tr) {
		super(id, 3, action, entityId);
		this.name = name;
		this.type = type;
		this.expr = expr;
		this.tr = tr;
	}

	public TriggerChange(long id, int action, String entityId, String name, int type, int tr) {
		super(id, 3, action, entityId);
		this.name = name;
		this.type = type;
		this.tr = tr;
	}
}