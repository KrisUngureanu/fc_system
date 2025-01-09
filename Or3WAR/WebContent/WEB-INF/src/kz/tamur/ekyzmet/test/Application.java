package kz.tamur.ekyzmet.test;

import java.io.Serializable;

public final class Application implements Serializable {

	private static final long serialVersionUID = -7426750956290901198L;

	public final String iin;
	public final String firstName;
	public final String lastName;
	public final String middleName;
	public final Program program;
	
	public Application(String iin, String firstName, String lastName, String middleName, Program program) {
		super();
		this.iin = iin;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.program = program;
	}
}
