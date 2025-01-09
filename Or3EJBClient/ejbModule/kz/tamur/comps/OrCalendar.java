package kz.tamur.comps;

import static java.awt.GridBagConstraints.*;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static kz.tamur.rt.Utils.setAllSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.rt.MainFrame;

/**
 * The Class OrCalendar.
 * 
 * @author Sergey Lebedev
 */
public class OrCalendar extends GradientPanel implements PropertyChangeListener, ActionListener {

    /** Константа RIGHT_SPINNER. */
    public static final int RIGHT_SPINNER = 0;

    /** Константа LEFT_SPINNER. */
    public static final int LEFT_SPINNER = 1;

    /** Константа NO_SPINNER. */
    public static final int NO_SPINNER = 2;

    /** The btn ok. */
    private JButton btnOk = new JButton();

    /** The btn cancel. */
    private JButton btnCancel = new JButton();

    /** The btn today. */
    private JButton btnToday = new JButton();

    /** The calendar. */
    private Calendar calendar;

    /** The control pane. */
    private GradientPanel controlPane = new GradientPanel();

    /** The btn back month. */
    private JButton btnBackMonth = new JButton();

    /** The btn next month. */
    private JButton btnNextMonth = new JButton();

    /** The btn back year. */
    private JButton btnBackYear = new JButton();

    /** The btn next year. */
    private JButton btnNextYear = new JButton();

    /** The view. */
    private JLabel view = new JLabel();

    /** The year chooser. */
    private OrYearChooser yearChooser = new OrYearChooser(this, btnBackYear, btnNextYear, view);

    /** The month chooser. */
    private OrMonthChooser monthChooser = new OrMonthChooser(this, btnBackMonth, btnNextMonth, view);

    /** The day chooser. */
    private OrDayChooser dayChooser = new OrDayChooser(this);

    /** The btn size. */
    private Dimension btnSize = new Dimension(20, 20);

    /** Константа dfSymb. */
    private static final DateFormatSymbols dfSymb = new DateFormatSymbols();

    /** Константа MONTHS. */
    private static final String MONTHS[] = dfSymb.getMonths();

    /** The resource. */
    private ResourceBundle resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, kz.tamur.rt.Utils.getLocale());

    /** The parent. */
    private JComponent parent;

    /**
     * Создание нового or calendar.
     * 
     * @param parent
     *            the parent
     */
    public OrCalendar(JComponent parent) {
        super();
        this.parent = parent;
        init(RIGHT_SPINNER);
    }

    /**
     * Inits the.
     * 
     * @param monthSpinner
     *            the month spinner
     */
    private void init(int monthSpinner) {

        calendar = Calendar.getInstance();
        // задать первый день недели как понедельник
        calendar.setFirstDayOfWeek(MONDAY);
        updateView();
        monthChooser.setBackBtn(btnBackMonth);
        monthChooser.setNextBtn(btnNextMonth);
        monthChooser.setView(view);

        yearChooser.setBackBtn(btnBackYear);
        yearChooser.setNextBtn(btnNextYear);
        yearChooser.setView(view);

        dayChooser.setCalendar(calendar);
        monthChooser.setMonth(calendar.get(MONTH));
        yearChooser.setYear(calendar.get(YEAR));

        monthChooser.setYearChooser(yearChooser);
        monthChooser.setDayChooser(dayChooser);
        yearChooser.setDayChooser(dayChooser);

        setDefaultBackground();
        setLayout(new GridBagLayout());
        controlPane.setLayout(new GridBagLayout());
        controlPane.setOpaque(false);
        initButton(btnOk, "ok.png");
        initButton(btnCancel, "cancel.png");
        initButton(btnToday, "calendar_today.png");
        initButton(btnBackMonth, "back.png");
        initButton(btnNextMonth, "next.png");
        initButton(btnBackYear, "first.png");
        initButton(btnNextYear, "end.png");

        updateToolTipCalendar();

        setFont(kz.tamur.rt.Utils.getDefaultFont());

        controlPane.add(btnBackYear, new GridBagConstraints(0, 0, 1, 1, 0, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        controlPane.add(btnBackMonth, new GridBagConstraints(1, 0, 1, 1, 1, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        controlPane.add(view, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(1, 3, 1, 3), 0, 0));
        controlPane.add(btnNextMonth, new GridBagConstraints(3, 0, 1, 1, 1, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));
        controlPane.add(btnNextYear, new GridBagConstraints(4, 0, 1, 1, 0, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));

        add(btnOk, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, Constants.INSETS_1, 0, 0));
        add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0, WEST, NONE, Constants.INSETS_1, 0, 0));
        add(btnToday, new GridBagConstraints(2, 0, 1, 1, 1, 0, WEST, NONE, Constants.INSETS_1, 0, 0));

        add(controlPane, new GridBagConstraints(0, 1, 3, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        add(dayChooser, new GridBagConstraints(0, 2, 3, 1, 1, 1, NORTH, NONE, Constants.INSETS_1, 0, 0));

        dayChooser.addPropertyChangeListener(this);
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
        btnToday.addActionListener(this);
    }

    /**
     * Inits the button.
     * 
     * @param button
     *            the button
     * @param icon
     *            the icon
     */
    private void initButton(JButton button, String icon) {
        kz.tamur.rt.Utils.setAllSize(button, btnSize);
        button.setIcon(kz.tamur.rt.Utils.getImageIconFull(icon));
        button.setBorder(BorderFactory.createEmptyBorder());
        // установка цвета только для того чтобы активировать прозрачность
        button.setBackground(kz.tamur.rt.Utils.getLightGraySysColor());
        button.setBackground(Color.WHITE);
        button.setOpaque(false);
    }

    /**
     * Update view.
     */
    private void updateView() {
        int month = calendar.get(MONTH);// источник месяца может быть некорректен
        view.setText(MONTHS[month] + ", " + calendar.get(YEAR));
    }

    /**
     * Sets the background.
     */
    public void setDefaultBackground() {
        if (Constants.SE_UI && !MainFrame.GRADIENT_MAIN_FRAME.isEmpty()) {
            setGradient(MainFrame.GRADIENT_MAIN_FRAME);
        } else {
            setGradient(Constants.GLOBAL_DEF_GRADIENT);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();
        if (object == btnOk) {
            firePropertyChange("choice of date", null, calendar);
        } else if (object == btnCancel) {
            firePropertyChange("choice of date", null, null);
        } else if (object == btnToday) {
            Calendar today = Calendar.getInstance(Locale.getDefault());
            monthChooser.setMonth(today.get(MONTH));
            yearChooser.setYear(today.get(YEAR));
            dayChooser.setDay(today.get(DAY_OF_MONTH));
            firePropertyChange("choice of date", null, calendar);
        }
    }

    /**
     * Sets the calendar.
     * 
     * @param date
     *            the date
     * @param flag
     *            the flag
     */
    private void setCalendar(Calendar date, boolean flag) {
        Calendar cDate = calendar;
        calendar = date;
        if (flag) {
            yearChooser.setYear(date.get(YEAR));
            monthChooser.setMonth(date.get(MONTH));
            dayChooser.setDay(date.get(DAY_OF_MONTH));
        }
        repaint();
        firePropertyChange("calendar", cDate, calendar);
    }

    /**
     * Установить calendar.
     * 
     * @param date
     *            the new calendar
     */
    public void setCalendar(Calendar date) {
        setCalendar(date, true);
    }

    /**
     * Получить calendar.
     * 
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Установить date.
     * 
     * @param uDate
     *            the new date
     */
    public void setDate(Date uDate) {
        Calendar cDate = Calendar.getInstance(Locale.getDefault());
        // задать первый день недели как понедельник
        cDate.setFirstDayOfWeek(MONDAY);
        if (uDate != null) {
            cDate.clear();
            cDate.setTime(uDate);
        }
        setCalendar(cDate, true);
    }

    /**
     * Получить date.
     * 
     * @return the date
     */
    public Date getDate() {
        return calendar.getTime();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (dayChooser != null) {
            dayChooser.setFont(font);
            view.setFont(font);
        }

    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        if (dayChooser != null) {
            dayChooser.setForeground(color);
        }
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (dayChooser != null) {
            dayChooser.setBackground(color);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (calendar != null) {
            Calendar cDate = (Calendar) calendar.clone();
            if (evt.getPropertyName().equals("day")) {
                cDate.set(DAY_OF_MONTH, ((Integer) evt.getNewValue()).intValue());
                setCalendar(cDate, false);
                firePropertyChange("choice of date", null, calendar);
            } else if (evt.getPropertyName().equals("month")) {
                cDate.set(MONTH, ((Integer) evt.getNewValue()).intValue());
                setCalendar(cDate, false);
            } else if (evt.getPropertyName().equals("year")) {
                cDate.set(YEAR, ((Integer) evt.getNewValue()).intValue());
                setCalendar(cDate, false);
            }
        }

    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (dayChooser != null) {
            dayChooser.setEnabled(flag);
            monthChooser.setEnabled(flag);
            yearChooser.setEnabled(flag);
        }
    }

    /**
     * Получить day chooser.
     * 
     * @return the day chooser
     */
    public OrDayChooser getDayChooser() {
        return dayChooser;
    }

    /**
     * Получить month chooser.
     * 
     * @return the month chooser
     */
    public OrMonthChooser getMonthChooser() {
        return monthChooser;
    }

    /**
     * Получить year chooser.
     * 
     * @return the year chooser
     */
    public OrYearChooser getYearChooser() {
        return yearChooser;
    }

    /**
     * Получить btn ok.
     * 
     * @return the btn ok
     */
    public JButton getBtnOk() {
        return btnOk;
    }

    /**
     * Получить btn cancel.
     * 
     * @return the btn cancel
     */
    public JButton getBtnCancel() {
        return btnCancel;
    }

    /**
     * Получить btn today.
     * 
     * @return the btn today
     */
    public JButton getBtnToday() {
        return btnToday;
    }

    /**
     * Update tool tip calendar.
     */
    public void updateToolTipCalendar() {
        btnOk.setToolTipText(resource.getString("okDate"));
        btnCancel.setToolTipText(resource.getString("cancelDate"));
        btnToday.setToolTipText(resource.getString("todayDate"));
        btnBackMonth.setToolTipText(resource.getString("backMonth"));
        btnNextMonth.setToolTipText(resource.getString("nextMonth"));
        btnBackYear.setToolTipText(resource.getString("backYear"));
        btnNextYear.setToolTipText(resource.getString("nextYear"));
    }

    /**
     * Pack.
     */
    public void pack() {
        if (parent != null) {
            parent.revalidate();
            if (parent instanceof JPopupMenu) {
                ((JPopupMenu) parent).pack();
            }
        }
    }

}

class OrDayChooser extends GradientPanel implements ActionListener, KeyListener, FocusListener {
    private OrTransparentButton days[];
    private OrTransparentButton selectedDay;

    private static final DateFormatSymbols dfSymb = new DateFormatSymbols();
    private static final String DAYS[] = setMonday(dfSymb.getShortWeekdays());
    private static Dimension size = new Dimension(24, 24);
    private int day;
    private Calendar today;
    private Calendar calendar;
    private GradientPanel parent;
    private static final Color RED_COLOR = new Color(164, 0, 0);
    private static final Color BLUE_COLOR = new Color(0, 0, 164);
    private static final Color TODAY_COLOR = new Color(164, 0, 0);
    private static final Color HEADER_COLOR = new Color(180, 180, 200);
    private static final Color SELECTED_COLOR = new Color(160, 160, 160);

    public OrDayChooser(GradientPanel parent) {
        this((Calendar.getInstance(Locale.getDefault())).get(DAY_OF_MONTH), parent);
        setOpaque(false);
    }

    public OrDayChooser(int iDay, GradientPanel parent) {
        this.parent = parent;
        days = new OrTransparentButton[49];
        selectedDay = null;

        today = Calendar.getInstance(Locale.getDefault());
        // задать первый день недели как понедельник
        today.setFirstDayOfWeek(MONDAY);
        calendar = (Calendar) today.clone();
        calendar.setFirstDayOfWeek(MONDAY);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, Constants.INSETS_0, 0, 0);

        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 7; ++j) {
                int k = j + 7 * i;
                // если это первая строчка
                if (i == 0) {
                    // компоновка заголовка
                    days[k] = new OrTransparentButton() {
                        public void addMouseListener(MouseListener mouselistener) {
                        }

                        public boolean isFocusable() {
                            return false;
                        }
                    };
                    days[k].setBackground(HEADER_COLOR);

                } else {
                    days[k] = new OrTransparentButton();
                    setAllSize(days[k], size);
                    days[k].addActionListener(this);
                    days[k].addKeyListener(this);
                    days[k].addFocusListener(this);
                    days[k].setBackground(Color.WHITE); // без установки фона прозрачность не работает
                }
                repaint();
                gbc.gridx = j;
                gbc.gridy = i;
                add(days[k], gbc);
            }
        }
        init();
        setDay(iDay);
    }

    /**
     * Пересобирает массив с наименованиями дней недели,
     * делая понедельний первым днёв в недели
     * 
     * @param days
     *            массив с наименованиями дней
     * @return string[] новый массив с наименованиями дней
     */
    private static String[] setMonday(String[] days) {
        String sun;
        sun = days[1];
        for (int i = 1; i < 7; i++) {
            days[i] = days[i + 1];
        }
        days[7] = sun;
        return days;
    }

    /**
     * Инициализация компонента
     */
    protected void init() {

        for (int i = 0, y = 1; i < 7; ++i, ++y) {
            days[i].setText(DAYS[y]);
            days[i].setForeground(y > 5 ? RED_COLOR : BLUE_COLOR);
        }
        drawDays();
    }

    /**
     * Прорисовка дней
     */
    protected void drawDays() {
        Calendar cDate = (Calendar) calendar.clone();
        cDate.setFirstDayOfWeek(2);
        cDate.set(DAY_OF_MONTH, 1);
        int dayOfWeek = cDate.get(DAY_OF_WEEK);
        // следующий индекс элемента после заголовка
        int i = 7;
        // скрыть элементы до начала месяца
        if(dayOfWeek==1){
	        for (int m = 2; m <= 7; ++m, ++i) {
	            days[i].setVisible(false);
	            days[i].setText("");
	        }
        }else{
	        for (int m = 2; m < dayOfWeek; ++m, ++i) {
	            days[i].setVisible(false);
	            days[i].setText("");
	        }
        }

        Color color = getForeground();
        int curMonth = cDate.get(MONTH);

        int j = 0;
        int k;
        // отобразить дни текущего месяца
        for (; curMonth == cDate.get(MONTH);) {
            k = i + j;
            days[k].setText(Integer.toString(j + 1));
            days[k].setVisible(true);
            days[k].setForeground(cDate.get(DAY_OF_YEAR) == today.get(DAY_OF_YEAR) && cDate.get(YEAR) == today.get(YEAR) ? TODAY_COLOR
                    : color);

            if (j + 1 == day) {
                days[k].setOpaque(true);
                days[k].setBackground(SELECTED_COLOR);
                selectedDay = days[k];
            } else {
                days[k].setOpaque(false);
            }
            days[k].repaint();
            ++j;
            cDate.add(DAY_OF_MONTH, 1);
        }
        // убрать оставшиеся дни
        for (int z = i + j; z < 49; ++z) {
            days[z].setVisible(false);
            days[z].setText("");
        }
        // если последний или предпоследний ряд пустой
        if (i + j >= 35) {
            revalidate();
            parent.revalidate();
            ((OrCalendar) parent).pack();
        }
    }

    public void setDay(int iDay) {
        setDay(iDay, false);
    }

    /**
     * Установить день, возникает при выборе дня в календаре.
     * 
     * @param iDay
     *            the new day
     */
    public void setDay(int iDay, boolean isNew) {
        if (iDay < 1) {
            iDay = 1;
        }

        Calendar cDate = (Calendar) calendar.clone();
        calendar.setFirstDayOfWeek(2);
        cDate.set(DAY_OF_MONTH, 1);
        cDate.add(MONTH, 1);
        cDate.add(DAY_OF_MONTH, -1);
        int lastDay = cDate.get(DAY_OF_MONTH);
        if (iDay > lastDay)
            iDay = lastDay;

        int k = day;
        day = iDay;
        if (selectedDay != null) {
            selectedDay.setOpaque(false);
            selectedDay.repaint();
        }

        for (int i = 7; i < 49; i++) {
            if (!days[i].getText().equals(Integer.toString(day)))
                continue;
            selectedDay = days[i];
            selectedDay.setOpaque(true);
            selectedDay.setBackground(SELECTED_COLOR);
            break;
        }

        firePropertyChange("day", isNew ? 0 : k, day);
    }

    public int getDay() {
        return day;
    }

    public void setMonth(int month) {
        calendar.set(MONTH, month);
        setDay(day);
        drawDays();
    }

    public void setYear(int year) {
        calendar.set(YEAR, year);
        drawDays();
    }

    public void setCalendar(Calendar cDate) {
        calendar = cDate;
        drawDays();
    }

    public void setFont(Font font) {
        if (days != null) {
            for (int i = 0; i < 49; i++)
                days[i].setFont(font);
        }
    }

    public void setForeground(Color color) {
        super.setForeground(color);
        if (days != null) {
            for (int i = 7; i < 49; i++)
                days[i].setForeground(color);

            drawDays();
        }
    }

    public void actionPerformed(ActionEvent actionevent) {
        OrTransparentButton btnDay = (OrTransparentButton) actionevent.getSource();
        int i = (new Integer(btnDay.getText())).intValue();
        setDay(i, true);
    }

    public void focusGained(FocusEvent focusevent) {
        JButton jbutton = (JButton) focusevent.getSource();
        String s = jbutton.getText();
        if (s != null && !s.equals(""))
            actionPerformed(new ActionEvent(focusevent.getSource(), 0, null));
    }

    public void focusLost(FocusEvent focusevent) {
    }

    public void keyPressed(KeyEvent keyevent) {
    }

    public void keyTyped(KeyEvent keyevent) {
    }

    public void keyReleased(KeyEvent keyevent) {
    }

    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        for (short s = 0; s < days.length; s++)
            if (days[s] != null)
                days[s].setEnabled(flag);
    }

}

class OrYearChooser {
    private OrDayChooser dayChooser = null;
    private int year = 0;
    private JButton backBtn;
    private JButton nextBtn;
    private JLabel view;
    private OrCalendar orCalendar;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(OrYearChooser.this);

    public OrYearChooser(OrCalendar orCalendar, int year, JButton backBtn, JButton nextBtn, JLabel view) {
        setOrCalendar(orCalendar);
        setBackBtn(backBtn);
        setNextBtn(nextBtn);
        setView(view);
        setYear(year);
        pcs.addPropertyChangeListener(orCalendar);

        this.backBtn.addMouseListener(new MouseAdapter() {
            YearTimer remY = new YearTimer(false);

            @Override
            public void mousePressed(MouseEvent e) {
                setYear(OrYearChooser.this.year - 1);
                remY.reset();
                remY.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                remY.stop();
            }
        });

        this.nextBtn.addMouseListener(new MouseAdapter() {
            YearTimer addY = new YearTimer(true);

            @Override
            public void mousePressed(MouseEvent e) {
                setYear(OrYearChooser.this.year + 1);
                addY.reset();
                addY.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addY.stop();
            }
        });
    }

    public OrYearChooser(OrCalendar orCalendar, JButton backBtn, JButton nextBtn, JLabel view) {
        this(orCalendar, Calendar.getInstance(Locale.getDefault()).get(YEAR), backBtn, nextBtn, view);
    }

    public void setYear(int year) {
        int oldYear = this.year;
        this.year = year;
        if (dayChooser != null) {
            view.setText(view.getText().replaceFirst("\\d+$", year + ""));
            dayChooser.setYear(year);
        }
        pcs.firePropertyChange("year", oldYear, year);
    }

    public int getYear() {
        return year;
    }

    public void setDayChooser(OrDayChooser jdaychooser) {
        dayChooser = jdaychooser;
    }

    public void setBackBtn(JButton backBtn) {
        this.backBtn = backBtn;
    }

    public void setNextBtn(JButton nextBtn) {
        this.nextBtn = nextBtn;
    }

    public void setView(JLabel view) {
        this.view = view;
    }

    public void setEnabled(boolean flag) {
        backBtn.setEnabled(flag);
        nextBtn.setEnabled(flag);
    }

    public void setOrCalendar(OrCalendar orCalendar) {
        this.orCalendar = orCalendar;
    }

    class YearTimer extends Timer {
        int k = 0;
        int defDelay;
        ActionListener actL;
        boolean isAdd;

        public YearTimer(boolean isAdd) {
            super(100, null);
            this.isAdd = isAdd;
            actL = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (k == 0) {
                        defDelay = getDelay();
                        setDelay(defDelay * 5);
                    } else if (k < 5) {
                        setDelay(defDelay * 2);
                    } else {
                        setDelay(defDelay);
                    }

                    if (k != 0) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                setYear(OrYearChooser.this.year + (YearTimer.this.isAdd ? 1 : -1));
                            }
                        });
                    }
                    k++;
                }
            };
            addActionListener(actL);
        }

        public void reset() {
            k = 0;
            setDelay(100);
        }
    }

}

class OrMonthChooser {

    private static final DateFormatSymbols dfSymb = new DateFormatSymbols();
    private static final String MONTHS[] = dfSymb.getMonths();

    private int oldBarValue = 0;
    private int month = 0;

    private OrDayChooser dayChooser = null;
    private OrYearChooser yearChooser = null;

    private JButton backBtn;
    private JButton nextBtn;
    private JLabel view;
    private OrCalendar orCalendar;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(OrMonthChooser.this);

    public OrMonthChooser(OrCalendar orCalendar, JButton backBtn, JButton nextBtn, JLabel view) {
        this(orCalendar, Calendar.getInstance(Locale.getDefault()).get(MONTH), backBtn, nextBtn, view);
    }

    public OrMonthChooser(OrCalendar orCalendar, int month, JButton backBtn, JButton nextBtn, JLabel view) {
        setOrCalendar(orCalendar);
        setBackBtn(backBtn);
        setNextBtn(nextBtn);
        setView(view);
        setMonth(month);
        pcs.addPropertyChangeListener(orCalendar);
        this.backBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMonth(OrMonthChooser.this.month - 1);
            }
        });
        this.nextBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMonth(OrMonthChooser.this.month + 1);
            }
        });
    }

    public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {

        boolean flag = true;
        int newBarValue = adjustmentevent.getValue();
        if (newBarValue > oldBarValue)
            flag = false;

        oldBarValue = newBarValue;

        int j = getMonth();
        if (flag) {
            if (++j == 12) {
                j = 0;
                if (yearChooser != null)
                    yearChooser.setYear(yearChooser.getYear() + 1);
            }
        } else if (--j == -1) {
            j = 11;
            if (yearChooser != null)
                yearChooser.setYear(yearChooser.getYear() - 1);
        }
        setMonth(j);
    }

    public void setMonth(int month) {
        int oldMonth = this.month;
        if (month < 0) { // переход на старый год
            this.month = 11;
            yearChooser.setYear(yearChooser.getYear() - 1);
        } else if (month > 11) { // переход на новый год
            this.month = 0;
            yearChooser.setYear(yearChooser.getYear() + 1);
        } else {
            this.month = month;
        }
        view.setText(view.getText().replaceFirst(".*,", MONTHS[this.month] + ","));
        if (dayChooser != null) {
            dayChooser.setMonth(this.month);
        }
        pcs.firePropertyChange("month", oldMonth, month);
    }

    public int getMonth() {
        return month;
    }

    public void setDayChooser(OrDayChooser jdaychooser) {
        dayChooser = jdaychooser;
    }

    public void setBackBtn(JButton backBtn) {
        this.backBtn = backBtn;
    }

    public void setNextBtn(JButton nextBtn) {
        this.nextBtn = nextBtn;
    }

    public void setView(JLabel view) {
        this.view = view;
    }

    public void setYearChooser(OrYearChooser jyearchooser) {
        yearChooser = jyearchooser;
    }

    public void setEnabled(boolean flag) {
        backBtn.setEnabled(flag);
        nextBtn.setEnabled(flag);
    }

    public void setOrCalendar(OrCalendar orCalendar) {
        this.orCalendar = orCalendar;
    }
}
