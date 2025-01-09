package kz.tamur.or3.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.util.Funcs;

public class SystemRight implements Serializable {
	private long id;
	private String name;
	private String description;
	private transient ASTStart formula;
	private byte[] expression;
	private int action;
	private boolean blocked;
	private boolean denying;
	private List<KrnObject> usersOrRoles = new ArrayList<KrnObject>();
	private List<KrnObject> subjects = new ArrayList<KrnObject>();
	
	public SystemRight(long id, String name, String desc, byte[] expression, int action, boolean deny, boolean block) {
		super();
		this.id = id;
		this.name = name;
		this.description = desc;
		this.action = action;
		this.blocked = block;
		this.denying = deny;
		
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

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(Object value) {
		if (value instanceof String) {
			description = (String) value;
		} else {
			description = "";
		}
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public List<KrnObject> getUsers() {
		return usersOrRoles;
	}

	public void addUser(KrnObject userOrRole) {
		usersOrRoles.add(userOrRole);
	}

	public void clearUsers() {
		usersOrRoles.clear();
	}
	
	public boolean isForUser(List<Long> userIds) {
		for (KrnObject role : usersOrRoles) {
			for (long userId : userIds) {
				if (userId == role.id)
					return true;
			}
		}
		return false;
	}

	public List<KrnObject> getSubjects() {
		return subjects;
	}

	public void addSubject(KrnObject subject) {
		subjects.add(subject);
	}

	public void clearSubjects() {
		subjects.clear();
	}

	public boolean isForSubject(long subjectId) {
		for (KrnObject subject : subjects) {
			if (subjectId == subject.id)
				return true;
		}
		return false;
	}

	public boolean isDenying() {
		return denying;
	}

	public void setDenying(boolean denying) {
		this.denying = denying;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SystemRight && ((SystemRight)obj).getId() == id);
	}
}
