package com.cifs.or2.client;

import com.cifs.or2.kernel.FilterDate;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.ResourceBundle;

public class ReadFilterDatesPanel extends JPanel {
    // Size constants
    private static final Dimension LABEL_SIZE = new Dimension(100, 25);
    private static final Dimension FIELD_SIZE = new Dimension(80, 25);

    private DateField firstDate_ = new DateField();
    private DateField lastDate_ = new DateField();
    private DateField currDate_ = new DateField();

    private long fid_;
    private long flags_;

    public ReadFilterDatesPanel(long fid, long flags, ResourceBundle res) {
        fid_ = fid;
        flags_ = flags;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Инициализация компонента для ввода начала периода
        String title = (res != null)
                    ? res.getString("filterDatesBegin")
                    : "Начало периода";
        if ((flags & 0x02) > 0)
            initField(title, firstDate_);

        // Инициализация компонента для ввода конца периода
        title = (res != null)
                    ? res.getString("filterDatesEnd")
                    : "Конец периода";
        if ((flags & 0x04) > 0)
            initField(title, lastDate_);

        // Инициализация компонента для ввода текущей даты
        title = (res != null)
                    ? res.getString("filterDatesCurrent")
                    : "Текущая дата";
        if ((flags & 0x01) > 0)
            initField(title, currDate_);
    }

    public Date getFirstDate() {
        return getDate(firstDate_);
    }

    public Date getLastDate() {
        return getDate(lastDate_);
    }

    public Date getCurrDate() {
        return getDate(currDate_);
    }

    public FilterDate[] getFilterDates() {
        FilterDate[] fds = new FilterDate[countDates()];
        int j = 0;
        Date d = null;

        if ((flags_ & 1) > 0) {
            d = getCurrDate();
            if (d != null)
                fds[j++] = new FilterDate(fid_, 0, kz.tamur.util.Funcs.convertDate(d));
            else fds[j++] = new FilterDate(fid_, 0, null);
        }
        if ((flags_ & 2) > 0) {
            d = getFirstDate();
            if (d != null)
                fds[j++] = new FilterDate(fid_, 1, kz.tamur.util.Funcs.convertDate(d));
            else fds[j++] = new FilterDate(fid_, 1, null);
        }
        if ((flags_ & 4) > 0) {
            d = getLastDate();
            if (d != null)
                fds[j++] = new FilterDate(fid_, 2, kz.tamur.util.Funcs.convertDate(d));
            else fds[j++] = new FilterDate(fid_, 2, null);
        }
        return fds;
    }

    private void initField(String label, DateField field) {
        JPanel tp = new JPanel();
        JLabel tl = new JLabel(label);

        tp.add(tl);

        field.setPreferredSize(FIELD_SIZE);
        tp.add(field);

        add(tp);

        if (field == currDate_) {
            currDate_.setValue(kz.tamur.util.Funcs.getCurrDate());
        }
    }

    private Date getDate(DateField field) {
        return field.getValue();
    }

    private int countDates() {
        switch ((int)flags_) {
        case 1:
        case 2:
        case 4:
            return 1;
        case 3:
        case 5:
        case 6:
            return 2;
        case 7:
            return 3;
        }
        return 0;
    }
}
