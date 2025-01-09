package kz.tamur.web.common.webgui;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;

import java.util.*;

import javax.swing.JTabbedPane;

import org.jdom.Element;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 19.07.2006
 * Time: 17:01:55
 * To change this template use File | Settings | File Templates.
 */
public class WebTabbedPane extends WebComponent implements JSONComponent {

	private static final WebComponent[] EMPTY_ARRAY = new WebComponent[0];
    protected List<WebComponent> tabs = new ArrayList<WebComponent>();
    protected List<String> titles = new ArrayList<String>();
    protected List<String> icons = new ArrayList<String>();
    protected int selectedIndex = 0;
    private int tabPlacement;

    // private Map hidenTabs = new HashMap();

    public WebTabbedPane(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public void addTab(String title, String iconName, WebComponent tab) {
        if (tab != null)
            tab.setParent(this);
        tabs.add(tab);
        titles.add(title);
        icons.add(iconName);
        tab.setParent(this);
    }

    public void setTitleAt(int i, String title) {
        titles.set(i, title);
        sendChangeProperty("setTitleAt", new String[] { i + "", title });
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        sendChangeProperty("selected", selectedIndex);
    }

    public void calculateSize() {
        super.calculateSize();
        for (int i = 0; i < tabs.size(); i++) {
            WebComponent child = (WebComponent) tabs.get(i);
            if (child.isVisible())
                child.calculateSize();
            // if ("100%".equals(child.sHeight)) child.sHeight = "96%";
        }
    }

    private boolean isVisible(int selectedIndex) {
        return tabs.get(selectedIndex).isVisible();
    }

    private int getNextClosestVisibleTabIndex(int selectedIndex) {
        while (selectedIndex < tabs.size() && !tabs.get(selectedIndex).isVisible()) {
            selectedIndex++;
        }
        return selectedIndex;
    }

    private int getPrevClosestVisibleTabIndex(int selectedIndex) {
        while (selectedIndex >= 0 && !tabs.get(selectedIndex).isVisible()) {
            selectedIndex--;
        }
        return selectedIndex;
    }

    private int getClosestVisibleTabIndex(int selectedIndex) {
        if (!isVisible(selectedIndex)) {
            int nextSelectedIndex = getNextClosestVisibleTabIndex(selectedIndex);

            if (nextSelectedIndex == tabs.size()) {
                nextSelectedIndex = getPrevClosestVisibleTabIndex(selectedIndex);
            }

            if (nextSelectedIndex > -1)
                return nextSelectedIndex;
        }
        return selectedIndex;
    }

    public JsonObject getTabXml2(int tabId) {
        WebComponent tab = (WebComponent) tabs.get(tabId);
        return tab.getJSON();
    }

    public WebComponent getWebComponent(String id) {
        if (id.equals(getId()))
            return this;
        for (int i = 0; i < tabs.size(); i++) {
            WebComponent comp = tabs.get(i);
            WebComponent res = comp.getWebComponent(id);
            if (res != null)
                return res;
        }
        return null;
    }

    public void setTabVisible(WebComponent tab, boolean isVisible) {
        for (int i = 0; i < tabs.size(); i++) {
            WebComponent component = tabs.get(i);
            if (component == tab) {
                tab.setVisible(isVisible);
                
	            JsonArray panels = new JsonArray();
	            JsonObject panel = new JsonObject();
	            panel.add("class", "tab-pane");
	            panel.add("index", i);
	            panel.add("v", tab.isVisible() ? 1 : 0);
	
	            panels.add(panel);
	            sendChangeProperty("tabs", panels);
                break;
            }
        }
    }

    protected void setTabPlacement(int place) {
        this.tabPlacement = place;
    }

    public WebComponent[] getComponents() {
        return tabs.toArray(EMPTY_ARRAY);
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if (tabPlacement == JTabbedPane.LEFT) {
            property.add("placement", "tabs-left");
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            property.add("placement", "tabs-below");
        } else if (tabPlacement == JTabbedPane.RIGHT) {
            property.add("placement", "tabs-right");
        }

/*		JSON пока не используется
 *         JsonArray heads = new JsonArray();
        this.selectedIndex = getClosestVisibleTabIndex(this.selectedIndex);
        for (int i = 0; i < tabs.size(); i++) {
            WebComponent tab = (WebComponent) tabs.get(i);
            if (tab.isVisible()) {
                JsonObject head = new JsonObject();
                head.add("index", i);
                if (i == selectedIndex) {
                    head.add("selected", true);
                }
                head.add("title", titles.get(i));
                JsonObject img = new JsonObject();
                img.add("src", icons.get(i));
                head.add("icon", img);
                heads.add(head);
            }
        }
        property.add("heads", heads);
*/
        boolean selectFirstVisible = false;
        JsonArray panels = new JsonArray();
        for (int i = 0; i < tabs.size(); i++) {
            WebComponent tab = (WebComponent) tabs.get(i);
            JsonObject panel = new JsonObject();
            panel.add("index", i);
            if (i == selectedIndex && tab.isVisible()) {
                panel.add("selected", true);
            } else if (i == selectedIndex && !tab.isVisible()) {
            	selectFirstVisible = true;
            } else if (selectFirstVisible && tab.isVisible()) {
                panel.add("selected", true);
            	selectFirstVisible = false;
            }
            
            panel.add("v", tab.isVisible() ? 1 : 0);

            // JSON пока не используется
            //int height = (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM) ? 93 : 100;
            //panel.add("height", height);
            //panel.add("panel", tab.getJSON());
            if (tab.isVisible()) {
            	tab.putJSON();
            } else {
                tab.setNeedToPutJSON();
            }
            panels.add(panel);
        }
        property.add("tabs", panels);
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}
