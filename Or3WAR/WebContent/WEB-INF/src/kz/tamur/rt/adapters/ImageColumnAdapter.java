package kz.tamur.rt.adapters;

import java.util.List;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.web.OrWebImage;

import com.cifs.or2.kernel.KrnException;

/**
 * Класс-Атаптер для столбца иконок таблицы
 * 
 * @author Sergey Lebedev
 */
public class ImageColumnAdapter extends ColumnAdapter {
    OrWebImage renderer = null;
    /**
     * Конструктор класса
     * 
     * @param frame
     *            the frame
     * @param column
     *            the column
     * @throws KrnException
     *             the krn exception
     */

    public ImageColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        renderer = (OrWebImage)column.getEditor();
    }

    
    public Object getObjectValueAt(int i) {
        if (dataRef != null) {
            List<Item> items = dataRef.getItems(0);
            OrRef.Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        } else if (calcRef != null) {
            Item item = calcRef.getItem(0, i);
            if (item != null && !item.isDeleted) {
            	return item.getCurrent();
            }
        } else if (renderer != null) {
        	renderer.getWebImagePath();
        }
        return null;
    }
}
