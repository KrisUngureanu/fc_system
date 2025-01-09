package kz.tamur.web.common.table;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 05.07.2006
 * Time: 10:56:37
 * To change this template use File | Settings | File Templates.
 */
public interface WebTableCellRenderer {

    String getTableCellRendererString(WebTable table, Object value,
			              boolean isSelected, boolean hasFocus,
				      int row, int column); 

    String getTableCellRendererStringJq(WebTable table, Object value,
            boolean isSelected, boolean hasFocus,
	      int row, int column); 
}
