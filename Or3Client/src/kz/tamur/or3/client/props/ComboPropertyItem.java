package kz.tamur.or3.client.props;

public class ComboPropertyItem {
	
	public final String id;
	public final String title;
	
	public ComboPropertyItem(String id, String title) {
		this.id = id;
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ComboPropertyItem) {
			return id.equals(((ComboPropertyItem)obj).id);
		}
		return false;
	}
}
