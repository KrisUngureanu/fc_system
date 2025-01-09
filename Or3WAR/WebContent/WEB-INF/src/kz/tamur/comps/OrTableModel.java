package kz.tamur.comps;

import kz.tamur.rt.adapters.ColumnAdapter;
import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 06.05.2004
 * Time: 12:18:43
 * To change this template use File | Settings | File Templates.
 */
public interface OrTableModel {
    void setInterfaceLangId(long langId);
    int getColumnCount();
    Color getZebra1Color();
    Color getZebra2Color();
    public Class getColumnClass(int columnIndex);
    public ColumnAdapter getColumnAdapter(int columnIndex);
    public Map getUniqueMap();
}
