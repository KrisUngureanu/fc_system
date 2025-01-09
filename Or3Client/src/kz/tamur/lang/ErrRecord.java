package kz.tamur.lang;

public final class ErrRecord {
	
	public final String module;
	public final int line;
	public final String message;
	
	public ErrRecord(String module, int line, String message) {
		this.module = module;
		this.line = line;
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (module != null) {
			s.append(module);
		}
		if (line >= 0) {
			s.append("(" + line + ")");
		}
		if (s.length() > 0) {
			s.append(": ");
		}
		s.append(message);
		return s.toString();
	}
}
