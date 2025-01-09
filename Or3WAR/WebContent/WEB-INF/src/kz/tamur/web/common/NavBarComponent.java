package kz.tamur.web.common;

public class NavBarComponent implements Comparable<NavBarComponent>{
	
	private int sortId;
	private String divId;
	private String aId;
	private String href;
	private String title;
	private boolean isDynamic;
	private String divClass;
	private String icon;
	private int idSect;
	
	public NavBarComponent(int sortId, String divId, String aId, String href, String title, boolean isDynamic, String divClass, String icon, int idSect) {
		super();
		this.sortId = sortId;
		this.divId = divId;
		this.aId = aId;
		this.href = href;
		this.title = title;
		this.isDynamic = isDynamic;
		this.divClass = divClass;
		this.icon = icon;
		this.idSect = idSect;
	}

	public int getSortId() {
		return sortId;
	}

	public String getDivId() {
		return divId;
	}
	
	public String getAid() {
		return aId;
	}

	public String getHref() {
		return href;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDynamic() {
		return isDynamic;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public String getDivClass() {
		return divClass;
	}
	
	public int getIdSect() {
		return idSect;
	}

	@Override
	public int compareTo(NavBarComponent o) {
		
		return (int) (this.sortId - o.sortId);
	}

}
