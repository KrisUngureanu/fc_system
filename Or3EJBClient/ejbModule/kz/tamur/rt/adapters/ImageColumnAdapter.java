package kz.tamur.rt.adapters;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.util.Funcs;
import kz.tamur.util.OrCellRenderer;

import com.cifs.or2.kernel.KrnException;

/**
 * Класс-Атаптер для столбца иконок таблицы
 * 
 * @author Sergey Lebedev
 */
public class ImageColumnAdapter extends ColumnAdapter {
    
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

    public ImageColumnAdapter(UIFrame frame, OrImageColumn column) throws KrnException {
        super(frame, column, false);
        renderer = new ImageCellRenderer();
    }

    
    public Object getObjectValueAt(int i) {
        if (dataRef != null) {
            List<Item> items = dataRef.getItems(0);
            OrRef.Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                byte[] val = null;
                if (item.getCurrent() != null) {
                    val = null;
                    if(item.getCurrent() instanceof File) {
                        try {
                            val = Funcs.read((File) item.getCurrent());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }else if(item.getCurrent() instanceof byte[]) {
                        val = (byte[]) item.getCurrent();
                    }
                }
                if(val!=null) {
                    ((ImageCellRenderer) renderer).setIcon(kz.tamur.rt.Utils.processCreateImage(val));
                } else {
                    ((ImageCellRenderer) renderer).setIcon(null);
                }
            }
        } else if (calcRef != null) {
            Item item = calcRef.getItem(0, i);
            if (item != null && !item.isDeleted) {
                byte[] val = null;
                if (item.getCurrent() != null && item.getCurrent() instanceof File) {
                    val = null;
                    try {
                        val = Funcs.read((File) item.getCurrent());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ((ImageCellRenderer) renderer).setIcon(kz.tamur.rt.Utils.processCreateImage(val));
                } else {
                    ((ImageCellRenderer) renderer).setIcon(null);
                }
            }
        }
        return null;
    }

    /**
     * The Class ImageCellRenderer.
     */
    public class ImageCellRenderer extends OrCellRenderer {
        /** отображаемая иконка */
        ImageIcon icon=null;
        /**
         * Создание нового image cell renderer.
         */
        public ImageCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            final JLabel lb = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lb.setIcon(icon);
            return lb;
        }
        public void setIcon(ImageIcon icon) {
         this.icon = icon;   
        }
    }
}
