package kz.tamur.web.common;

import com.eclipsesource.json.JsonObject;

public interface JSONCellComponent {
    
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state);
}
