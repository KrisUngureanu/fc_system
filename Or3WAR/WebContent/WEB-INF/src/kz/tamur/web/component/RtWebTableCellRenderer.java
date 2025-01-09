package kz.tamur.web.component;

import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebTableCellRenderer;
import kz.tamur.web.common.webgui.WebTable;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;

import javax.swing.table.TableModel;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 29.01.2007
 * Time: 10:13:09
 * To change this template use File | Settings | File Templates.
 */
public class RtWebTableCellRenderer implements WebTableCellRenderer {
    private TableAdapter adapter;

    public RtWebTableCellRenderer(TableAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public WebComponent getTableCellRenderer(WebTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        OrColumnComponent c = ((RtWebTableModel) adapter.getTable().getModel()).getColumn(column);
        boolean isCellEditable = ((TableModel) table.getModel()).isCellEditable(row, column);
        // row = ((RtWebTableModel)table.getModel()).getActualRow(row);
        return c.getCellRenderer(value, row, isCellEditable, isSelected, table.getId());
    }

    public void getTableCellRendererString(WebTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column, JsonObject obj) {
        OrColumnComponent c = ((RtWebTableModel) adapter.getTable().getModel()).getColumn(column);
        boolean isCellEditable = ((TableModel) table.getModel()).isCellEditable(row, column);
        c.getJSONValue(value, row, isCellEditable, isSelected, table.getId(), obj);
    }
}
