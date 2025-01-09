package kz.tamur.or3.util;

public class FGARule {
	private long id;
	private String name;
	private String className;
	private String attrNames;
	private String operations;
	private String expression;
	private boolean blocked;
	
	public FGARule(long id, String name, String className, String attrNames,
			String operations, String expression, boolean blocked) {
		super();
		this.id = id;
		this.name = name;
		this.className = className;
		this.attrNames = attrNames;
		this.operations = operations;
		this.expression = expression;
		this.blocked = blocked;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(Object value) {
		if (value instanceof String) {
			name = (String) value;
		} else {
			name = "";
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(Object value) {
		if (value instanceof String) {
			className = (String) value;
		} else {
			className = "";
		}
	}

	public String getAttrNames() {
		return attrNames;
	}

	public void setAttrNames(Object value) {
		if (value instanceof String) {
			attrNames = (String) value;
		} else {
			attrNames = "";
		}
	}

	public String getOperations() {
		return operations;
	}

	public void setOperations(Object value) {
		if (value instanceof String) {
			operations = (String) value;
		} else {
			operations = "";
		}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setExpression(Object value) {
		if (value instanceof String) {
			expression = (String) value;
		} else {
			expression = "";
		}
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
