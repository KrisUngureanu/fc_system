package kz.tamur.guidesigner.changemon;

import javax.swing.*;

import com.toedter.calendar.JCalendar;

import kz.tamur.rt.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

public class CalendarButton extends JButton {
    private ActionListener copyAdapter;

    private DateField dateField;

    public CalendarButton(String title) {
        super(Utils.getImageIcon("JCalendar"));
        setPreferredSize(new Dimension(20, 20));
        setMargin(new Insets(0, 0, 0, 0));
        setCursor(Cursor.getDefaultCursor());
        setToolTipText(title);
    }

    public void setDataField(DateField dataField) {
        this.dateField=dataField;
        this.copyAdapter = new CalendarAdapter();
        addActionListener(copyAdapter);
    }

    public void setCopyTitle(String title) {
        setToolTipText(title);
    }
    private class CalendarAdapter implements ActionListener, PropertyChangeListener {
        
        JPopupMenu popup;
        boolean dateSelected = false;
        JCalendar c;
        private boolean initialized = false;
        
        public CalendarAdapter() {
                c = new JCalendar(false);
                c.getDayChooser().addPropertyChangeListener(this);
            c.getDayChooser().setAlwaysFireDayProperty(true);
            
            popup = new JPopupMenu() {
                public void setVisible(boolean b) {
                    Boolean isCanceled = (Boolean) getClientProperty(
                            "JPopupMenu.firePopupMenuCanceled");

                    if (b || (!b && dateSelected) ||
                            ((isCanceled != null) && !b && isCanceled.booleanValue())) {
                        super.setVisible(b);
                    }
                }
            };
            popup.setLightWeightPopupEnabled(true);

            popup.add(c);
        }
        
        public void actionPerformed(ActionEvent e) {
                initialized = false;
                
                Component calBtn = (Component) e.getSource();
            int x = calBtn.getWidth() - (int) popup.getPreferredSize().getWidth();
            int y = calBtn.getY() + calBtn.getHeight();

            Calendar calendar = Calendar.getInstance();
            Object val = dateField.getValue();
            calendar.setTime(val instanceof Date ? (Date)val : new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            c.setCalendar(calendar);
            popup.show(calBtn, x, y);
            initialized = true;
            dateSelected = false;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
                // защита от ненужного срабатывания перед выводом календарика
                if (!initialized){
                        return;
                }
            if (evt.getPropertyName().equals("day")) {
                dateSelected = true;
                popup.setVisible(false);
                // получение даты из календаря
                Date value = new Date(c.getCalendar().getTimeInMillis());
                try {
                    dateField.setValue(value);
                    dateField.postActionEvent();
                } catch (Exception e) {
                        e.printStackTrace();
                }
            } else if (evt.getPropertyName().equals("date")) {
                Object value = evt.getNewValue();
                try {
                    dateField.setValue((Date)value);
                } catch (Exception e) {
                        e.printStackTrace();
                }
            }
        }
    }
}

