package kz.tamur.web.common.webgui;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 23.01.2007
 * Time: 11:44:13
 * To change this template use File | Settings | File Templates.
 */
public interface WebTableCellRenderer {
    void getTableCellRendererString(WebTable table, Object value,
			              boolean isSelected, boolean hasFocus,
				      int row, int column, JsonObject b);

    WebComponent getTableCellRenderer(WebTable table, Object value, boolean isSelected, boolean hasFocus,
	      int row, int column);
}