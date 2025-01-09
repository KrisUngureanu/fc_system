package kz.tamur.comps.ui;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.button.OrTransparentButton;

/**
 * The Class OrSpinner.
 *
 * @author Sergey Lebedev
 */
public class OrSpinner extends JPanel {
    
    /** метка отображения значения компонента */
    private JLabel valueView = new JLabel();
    
    /** Кнопка увеличения значения. */
    private OrTransparentButton upBtn = new OrTransparentButton();
    
    /** Кнопка уменьшения значения */
    private OrTransparentButton downBtn = new OrTransparentButton();
    
    /** Максимальное значение компонента */
    private int maxValue;
    
    /** Минимальное значение компонента */
    private int minValue;
    
    /** Шаг увеличения/уменьшения значения */
    private int step;
    
    /** Стартовое значение компонента */
    private int initValue;
    
    /** Текущее значение компонента */
    private int value = -1;
    
    /** Родительский компонент, которому будет послано событие о смене значения */
    JComponent comp;
    
    /** Поддержка отправки событий */
    PropertyChangeSupport ps = new PropertyChangeSupport(this);

    /**
     * Создание нового or spinner.
     *
     * @param comp the comp
     * @param maxValue the max value
     * @param minValue the min value
     * @param step the step
     * @param initValue the init value
     */
    public OrSpinner(JComponent comp, int maxValue, int minValue, int step, int initValue) {
        super(new GridBagLayout());
        initialize(comp, maxValue, minValue, step, initValue);
    }

    /**
     * Создание нового or spinner.
     *
     * @param comp the comp
     */
    public OrSpinner(JComponent comp) {
        super(new GridBagLayout());
        initialize(comp,100, 0, 1, 0);
    }

    /**
     * Создание нового or spinner.
     *
     * @param comp the comp
     * @param layout the layout
     */
    public OrSpinner(JComponent comp, LayoutManager layout) {
        super(layout);
        initialize(comp,100, 0, 1, 0);
    }

    /**
     * Создание нового or spinner.
     *
     * @param comp the comp
     * @param isDoubleBuffered the is double buffered
     */
    public OrSpinner(JComponent comp, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        setLayout(new GridBagLayout());
        initialize(comp,100, 0, 1, 0);
    }

    /**
     * Создание нового or spinner.
     *
     * @param comp the comp
     * @param layout the layout
     * @param isDoubleBuffered the is double buffered
     */
    public OrSpinner(JComponent comp, LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        initialize(comp,100, 0, 1, 0);
    }

    /**
     * Initialize.
     *
     * @param comp the comp
     * @param maxValue the max value
     * @param minValue the min value
     * @param step the step
     * @param initValue the init value
     */
    private void initialize(JComponent comp, int maxValue, int minValue, int step, int initValue) {
        this.comp = comp;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.step = step;
        this.initValue = initValue;
        upBtn.setIcon(kz.tamur.rt.Utils.getImageIconFull("up.png"));
        downBtn.setIcon(kz.tamur.rt.Utils.getImageIconFull("down.png"));
        valueView.setText(initValue+"");
        setOpaque(false);
        upBtn.setBackground(Color.black);
        downBtn.setBackground(Color.black);
        upBtn.setOpaque(false);
        downBtn.setOpaque(false);
        kz.tamur.rt.Utils.setAllSize(upBtn, new Dimension(12, 12));
        kz.tamur.rt.Utils.setAllSize(downBtn, new Dimension(12, 12));
        add(valueView, new GridBagConstraints(0, 0, 1, 2, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        add(upBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_0, 0, 0));
        add(downBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_0, 0, 0));

        ps.addPropertyChangeListener((PropertyChangeListener) comp);
        upBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int value_ = value + OrSpinner.this.step;
                if (value_ <= OrSpinner.this.maxValue) {
                    ps.firePropertyChange("countRowPage", value, value_);
                    value = value_;
                    valueView.setText(value + "");
                    
                    
                }

            }
        });
        
        downBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int value_ = value - OrSpinner.this.step;
                if (value_ >= OrSpinner.this.minValue) {
                    ps.firePropertyChange("countRowPage", value, value_);
                    value = value_;
                    valueView.setText(value + "");
                }
            }
        });
    }

    /**
     * Получить value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Установить value.
     *
     * @param value the new value
     */
    public void setValue(int value) {
        this.value = value;
        valueView.setText(value + "");
        
    }

    /**
     * Получить max value.
     *
     * @return the max value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Установить max value.
     *
     * @param maxValue the new max value
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Получить min value.
     *
     * @return the min value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Установить min value.
     *
     * @param minValue the new min value
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * Получить step.
     *
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * Установить шаг.
     *
     * @param step новый шаг смены значений
     */
    public void setStep(int step) {
        this.step = step;
    }
}
