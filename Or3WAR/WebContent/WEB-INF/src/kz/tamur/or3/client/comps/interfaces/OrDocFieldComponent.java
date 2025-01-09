package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

import javax.swing.table.TableCellRenderer;

import com.eclipsesource.json.JsonObject;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: erik
 * Date: 31.03.2008
 * Time: 18:32:59
 * To change this template use File | Settings | File Templates.
 */
public interface OrDocFieldComponent extends OrGuiComponent {
    File getFileToUpload();
    TableCellRenderer getCellRenderer();

    JsonObject open(File f);
    JsonObject edit(File f);
}
