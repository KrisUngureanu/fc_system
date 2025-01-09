package kz.tamur.or3.client.props.inspector;

public class ExprEditorObject {
	private Object object;	
	private String string;
	public ExprEditorObject(Object obj, String str) {
		object = obj;
		string = str;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public String getString() {
		return string;
	}
	
	public void setString(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		if (object != null)
			return object.toString();
		else
			return null;
	}
}
