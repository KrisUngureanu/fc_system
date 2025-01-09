package kz.tamur.or3.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.util.Funcs;

public class ProtocolRule implements Serializable {
	private long id;
	private String name;
	private transient ASTStart formula;
	private byte[] expression;
	private int event;
	private int eventType;
	private boolean deny;
	private boolean blocked;
	
	public ProtocolRule(long id, String name, byte[] expression, int type, int event, boolean deny, boolean block) {
		super();
		this.id = id;
		this.name = name;
		this.event = event;
		this.eventType = type;
		this.deny = deny;
		this.blocked = block;
		
		try {
			expression = Funcs.normalizeInput(expression, "UTF-8");
			this.formula = (expression != null && expression.length > 0) ? OrLang.createStaticTemplate(new InputStreamReader(
						new ByteArrayInputStream(expression), "UTF-8")) : null;
			this.expression = expression;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ASTStart getFormula() {
		if (formula == null && expression != null) {
			try {
				this.formula = (expression != null && expression.length > 0) ? OrLang.createStaticTemplate(new InputStreamReader(
							new ByteArrayInputStream(expression), "UTF-8")) : null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return formula;
	}

	public int getEvent() {
		return event;
	}

	public void setName(Object value) {
		if (value instanceof String) {
			name = (String) value;
		} else {
			name = "";
		}
	}

	public void setExpression(byte[] expression) {
		try {
			expression = Funcs.normalizeInput(expression, "UTF-8");
			this.formula = (expression != null && expression.length > 0) ? OrLang.createStaticTemplate(new InputStreamReader(
						new ByteArrayInputStream(expression), "UTF-8")) : null;
			this.expression = expression;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEvent(int event) {
		this.event = event;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public boolean isDeny() {
		return deny;
	}

	public void setDeny(boolean deny) {
		this.deny = deny;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ProtocolRule && ((ProtocolRule)obj).getId() == id);
	}
}
