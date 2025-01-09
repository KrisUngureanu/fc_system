package kz.tamur.util;

import kz.tamur.util.OrCellRenderer;
import kz.tamur.comps.OrDateField;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.DateColumnAdapter;
import javax.swing.*;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.06.2004
 * Time: 19:49:16
 * To change this template use File | Settings | File Templates.
 */
public class DateTableCellRenderer extends OrCellRenderer {

    JPanel p;
    JLabel lb;
    CalendarButton calBtn = null;
    DateColumnAdapter ca;

    public DateTableCellRenderer(DateColumnAdapter ca) {
        p = new JPanel(new BorderLayout());
        lb = new JLabel();
        p.add(lb, BorderLayout.CENTER);
        Font f = lb.getFont();
        lb.setFont(new Font(f.getName(), 0, f.getSize()));

        if (((OrDateField) ca.getColumn().getEditor()).isShowCalendar()) {
            calBtn = new CalendarButton(this, "Выбор даты из календаря");
            p.add(calBtn, BorderLayout.EAST);
        }
        this.ca = ca;
    }

    public Component getTableCellRendererComponent(JTable table, final Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
            // Реализуется непрозрачность текста и кнопки календаря
            p = new JPanel(new BorderLayout());
            p.setBorder(label.getBorder());
            changeComponent(table, value, isSelected, hasFocus, row, column, p);
            
            JLabel newLabel = new JLabel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    if (value != null) {
                        g.drawString((String) value,2, 10);
                    }
                    
                }
            };
            
            newLabel.setIcon(lb.getIcon());
            newLabel.setFont(lb.getFont());
            newLabel.setAlignmentX(lb.getAlignmentX());
            newLabel.setAlignmentY(lb.getAlignmentY());
            newLabel.setBackground(lb.getBackground());
            newLabel.setForeground(lb.getForeground());
            newLabel.setToolTipText(lb.getToolTipText());

            CalendarButton newCalBtn = new CalendarButton(this, "Выбор даты из календаря") {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    
                }
            };
     
            p.add(newLabel, BorderLayout.CENTER);
            if (((OrDateField) ca.getColumn().getEditor()).isShowCalendar()) {
                p.add(newCalBtn, BorderLayout.EAST);
            }
            changeComponent(table, value, isSelected, hasFocus, row, column, newLabel);
        } else {
            p.setBorder(label.getBorder());
            changeComponent(table, value, isSelected, hasFocus, row, column, p);
            lb.setText((String) value);
            changeComponent(table, value, isSelected, hasFocus, row, column, lb);
        }

        return p;
    }
}
