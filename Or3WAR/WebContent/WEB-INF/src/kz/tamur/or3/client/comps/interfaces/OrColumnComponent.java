package kz.tamur.or3.client.comps.interfaces;

import com.eclipsesource.json.JsonObject;

import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.comps.OrGuiComponent;

public interface OrColumnComponent extends OrGuiComponent {
    ColumnAdapter getAdapter();

    OrGuiComponent getEditor(int row);

    OrGuiComponent getEditor();

    String getTitle();

    String getIconName();

    void setIconName(String name);

    void getJSONValue(Object value, int row, boolean cellEditable, boolean isSelected, String tid, JsonObject b);

    WebComponent getCellRenderer(Object value, int row, boolean cellEditable, boolean isSelected, String tid);

    JsonObject getCellEditor(Object value, int row, String tid, boolean cellEditable);

    JsonObject getCellEditor(int row);

    boolean isCanSort();

    boolean isHelpClick();

    int getRotation();

    void setVisible(boolean isVisible);

    boolean isVisible();

	boolean isCellEditable(int row);
	
}
