package kz.tamur.guidesigner.search;

import java.util.ArrayList;
import kz.tamur.util.AbstractDesignerTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 15:27:35
 * To change this template use File | Settings | File Templates.
 */

public class SearchNode extends AbstractDesignerTreeNode {
    
	public static int SEARCH_TYPE_FOLDER = 0;
	public static int SEARCH_TYPE_TEXT = 1;
	public static int SEARCH_TYPE_ATTR = 2;
    private String title;
    private String text;					// what find
    private int type;						// type node (see above)
    private int count;						// match number - letter
    private String icon;					// icon name (path)
    private java.util.List<String[]> searchRes;
    
    public SearchNode()	{
    	super();
    }
    
    public SearchNode(String title, String text, int type, int count) {
		super();
		this.title = title;
		this.text = text;
		this.type = type;
		this.count = count;
	}

    public SearchNode(String title, String text, int type, int count, String icon) {
		super();
		this.title = title;
		this.text = text;
		this.type = type;
		this.count = count;
		this.icon = icon;
	}
    
    public SearchNode(String text, String sIcon, java.util.List<String[]> searchRes) {
    	super();
    	title = text;
    	this.text = text;
		type = SEARCH_TYPE_TEXT;
		count = searchRes.size();
    	this.icon = sIcon;
    	this.searchRes = searchRes;
    }

	public boolean isLeaf() {
		return type > SEARCH_TYPE_FOLDER;
    }

    public String toString() {
        return title;
    }

    public String readIcon() {
    	return icon;
    }
	
	protected void load() {
	}
	
    public boolean equals(Object obj) {
        if (obj instanceof AbstractDesignerTreeNode) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)obj;
            return title.equals(node.toString());
        }
        return false;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public String getIcon() {
		return icon;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public java.util.List<String[]> getSearchRes() {
		return searchRes;
	}
	
	public void setSearchResult(String txt, String icon, ArrayList table) {}
}


