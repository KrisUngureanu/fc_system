package kz.tamur.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import kz.tamur.comps.OrCalendar;
import kz.tamur.comps.OrDateField;
import com.cifs.or2.kernel.KrnDate;

/**
 * The Class CalendarAdapter.
 */
public class CalendarAdapter implements ActionListener, PropertyChangeListener {

    /** Всплывающая менюшка, в которую садится календарь*/
    private JPopupMenu popup;
    
    /** Календарь */
    private OrCalendar calendar;

    /** Флаг выбора даты */
    private boolean isDateSelected = false;
    
    /** The date field. */
    private OrDateField dateField;
    
    /** The initialized. */
    private boolean isInit = false;

    /**
     * Создание нового адаптера календаря.
     *
     * @param df компонент <code>OrDateField</code> для которого создаётся адаптер
     */
    public CalendarAdapter(OrDateField df) {
        this.dateField = df;
        popup = new JPopupMenu() {
            public void setVisible(boolean b) {
                Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");

                if (b || (!b && isDateSelected) || ((isCanceled != null) && !b && isCanceled.booleanValue())) {
                    super.setVisible(b);

                    if (!b) {
                        if (dateField.getAdapter().isEditor() && isInit) {
                            dateField.getCellEditor().stopCellEditing();
                        }
                    }
                }
            }
        };
        calendar = new OrCalendar(popup);
        calendar.addPropertyChangeListener(this);
        initAction(popup);
        popup.setLightWeightPopupEnabled(true);
        popup.add(calendar);
        popup.pack();
    }

    /**
     * TODO Не работает, Оставлено для дальнейшей реализации 
     * 
     * Метод должен переопределять работу клавиш на всплывающей менюшке
     * по Enter - выбор даты
     * стрелки - навигация по календарику и т.д.
     *
     * @param popup the popup
     */
    private void initAction(JPopupMenu popup) {
        InputMap im = popup.getInputMap(JPopupMenu.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        final Action oldEnterAction = popup.getActionMap().get(im.get(enter));
        im.put(enter, im.get(enter));
        Action enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                inputEnterKey();
                oldEnterAction.actionPerformed(e);
            }
        };
        popup.getActionMap().put(im.get(enter), enterAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        isInit = false;
        Component calBtn = (Component) e.getSource();
        int x = calBtn.getWidth() - (int) popup.getPreferredSize().getWidth();
        int y = calBtn.getY() + calBtn.getHeight();
        Calendar calendar = Calendar.getInstance();
        Object val = dateField.getValue();
        calendar.setTime(val instanceof Date ? (Date) val : new Date());
        this.calendar.setCalendar(calendar);
        popup.show(calBtn, x, y);
        isInit = true;
        isDateSelected = false;
    }

    
    public void propertyChange(PropertyChangeEvent evt) {
        if (isInit && evt.getPropertyName().equals("choice of date")) {
            isDateSelected = true;
            popup.setVisible(false);
            Object value = evt.getNewValue();
            if (value != null) {
                Calendar date = (Calendar) value;
                // из даты необходимо убрать лишние значения
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                value = new KrnDate(date.getTimeInMillis());
                // получение даты из календаря
                try {
                    dateField.getAdapter().changeValue(value);
                    dateField.setValue(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Метод должен выполнятся при нажатии Enter в открытой PopupMenu
     */
    public void inputEnterKey() {
        if (!isInit) {
            return;
        }
        isDateSelected = true;
        popup.setVisible(false);
        // получение даты из календаря
        Object value = new KrnDate(calendar.getCalendar().getTimeInMillis());

        try {
            dateField.getAdapter().changeValue(value);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        dateField.setValue(value);
    }

    /**
     * Обновление подсказок в календарике
     * Вызывается при смене языка
     */
    public void updateToolTipCalendar() {
        calendar.updateToolTipCalendar();
    }
}
