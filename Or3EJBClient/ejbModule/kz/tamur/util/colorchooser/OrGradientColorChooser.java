package kz.tamur.util.colorchooser;

import static kz.tamur.comps.Constants.DIAGONAL;
import static kz.tamur.comps.Constants.DIAGONAL2;
import static kz.tamur.comps.Constants.HORIZONTAL;
import static kz.tamur.comps.Constants.VERTICAL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.multiSlider.MThumbSlider;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.rt.Utils;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.BOTH;
/**
 * The Class OrGradientColorChooser.
 * 
 * @author Sergey Lebedev
 */
public class OrGradientColorChooser extends OrColorChooser {

    /** Кнопка задания начального цвета градиента. */
    private JButton setStartColor = new JButton("Задать начальный цвет");

    /** Кнопка задания конечного цвета градиента. */
    private JButton setEndColor = new JButton("Задать конечный цвет");

    /** Выпадающий список задания ориентации градиента. */
    private JComboBox setOrientation = new JComboBox();

    /** Панель на которой размещены компоненты управления градиентом. */
    private JPanel control = new JPanel(new GridBagLayout());

    /** Панель с градиентной заливкой служит для демонстрации текущего градиента по настройкам в диалоге. */
    private GradientPanel viewGradient = new GradientPanel();

    /** флажок включающий цикличность градиента. */
    private JCheckBox isCycle = new JCheckBox("Зациклить");
    
    private JCheckBox isEnabled = new JCheckBox("Активировать");

    MThumbSlider mSlider = new MThumbSlider(2);

    /** Поле ввода стартовой позиции градиента. */
    private JTextField startRange = new JTextField();

    /** Поле ввода конечной позиции градиента. */
    private JTextField endRange = new JTextField();
    
    /** The start label. */
    private JLabel startLabel = new JLabel("Точка начала");
    
    /** The end label. */
    private JLabel endLabel = new JLabel("Точка окончания");

    /**
     * Конструктор диалога задания градиента.
     *
     * @param startColor начальный цвет
     * @param endColor конечный цвет
     * @param orientation ориентация
     * @param isCycle цикличность
     * @param positionStartColor позиция стартового цвета
     * @param positionEndColor позиция коенчного цвета
     * @param isEnabled the is enabled
     */
    public OrGradientColorChooser(Color startColor, Color endColor, int orientation, boolean isCycle, int positionStartColor,
            int positionEndColor, boolean isEnabled) {
        super(startColor);
        // инициализация градиента
        setStartColor(startColor);
        setEndColor(endColor);
        setOrientation(orientation);
        setCycle(isCycle);
        setEnabledGradient(isEnabled);
        setPositionStartColor(positionStartColor);
        setPositionEndColor(positionEndColor);
        setPreferredSize(new Dimension(820, 320));
        // инициализация диалога
        initSub();
    }
    public OrGradientColorChooser(GradientColor gradient) {
        super(gradient.getStartColor());
        // инициализация градиента
        setStartColor(gradient.getStartColor());
        setEndColor(gradient.getEndColor());
        setOrientation(gradient.getOrientation());
        setCycle(gradient.isCycle());
        setEnabled(gradient.isEnabled());
        setPositionStartColor(gradient.getPositionStartColor());
        setPositionEndColor(gradient.getPositionEndColor());
        setPreferredSize(new Dimension(820, 320));
        // инициализация диалога
        initSub();
    }
    
    /**
     * Создание нового or gradient color chooser.
     */
    public OrGradientColorChooser() {
        super(Color.white);
        setStartColor(Color.white);
        setEndColor(Utils.getLightGraySysColor());
        setOrientation(HORIZONTAL);
        setCycle(true);
        setPositionStartColor(0);
        setPositionEndColor(50);
        setPreferredSize(new Dimension(820, 320));
        initSub();
    }
    /**
     * Инициализация панели.
     */
    void initSub() {
        viewGradient.setBorder(Utils.createTitledBorder(b, "Пример заливки"));
        startLabel.setFont(Utils.getDefaultFont());
        endLabel.setFont(Utils.getDefaultFont());
        setStartColor.setFont(Utils.getDefaultFont());
        Utils.setAllSize(setStartColor, new Dimension(160, 25));
        
        setStartColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setStartColor(getColor());
                mSlider.setFillColorAt(getStartColor(),  0); 
                mSlider.repaint();
                viewGradient.repaint();
            }
        });

        setEndColor.setFont(Utils.getDefaultFont());
        Utils.setAllSize(setEndColor, new Dimension(160, 25));
        setEndColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setEndColor(getColor());
                   mSlider.setTrackFillColor(getEndColor());
                   mSlider.repaint();
                viewGradient.repaint();
            }
        });

        setOrientation.setEditable(false);
        setOrientation.addItem("Горизонтальная");
        setOrientation.addItem("Вертикальная");
        setOrientation.addItem("Диагональная");
        setOrientation.addItem("Диагональная 2");

        switch (getOrientation()) {
        case HORIZONTAL:
            setOrientation.setSelectedIndex(0);
            break;
        case VERTICAL:
            setOrientation.setSelectedIndex(1);
            break;
        case DIAGONAL:
            setOrientation.setSelectedIndex(2);
            break;
        case DIAGONAL2:
            setOrientation.setSelectedIndex(3);
            break;
        default:
            setOrientation.setSelectedIndex(0);
        }

        setOrientation.setFont(Utils.getDefaultFont());
        
        setOrientation.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch (setOrientation.getSelectedIndex()) {
                case 0:
                    setOrientation(HORIZONTAL);
                    break;
                case 1:
                    setOrientation(VERTICAL);
                    break;
                case 2:
                    setOrientation(DIAGONAL);
                    break;
                case 3:
                    setOrientation(DIAGONAL2);
                    break;
                default:
                    setOrientation(HORIZONTAL);
                }
                viewGradient.repaint();
            }
        });
        
        isCycle.setFont(Utils.getDefaultFont());
        isCycle.setSelected(isCycle());
        isCycle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setCycle(isCycle.isSelected());
                viewGradient.repaint();
            }
        });
        
        isEnabled.setFont(Utils.getDefaultFont());
        isEnabled.setSelected(isEnabled());
        isEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setEnabledGradient(isEnabled.isSelected());
                viewGradient.repaint();
            }
        });
        Dimension sz = new Dimension(30, 22);
        Utils.setAllSize(startRange, sz);
        startRange.setText(String.valueOf(getPositionStartColor()));

        Utils.setAllSize(endRange, sz);
        endRange.setText(String.valueOf(getPositionEndColor()));

        
      
        mSlider.setValueAt(getPositionStartColor(), 0);                        
        mSlider.setValueAt(getPositionEndColor(), 1); 
        mSlider.setFillColorAt(getStartColor(),  0); 
        mSlider.setTrackFillColor(getEndColor());
        mSlider.putClientProperty( "JSlider.isFilled", Boolean.TRUE ); 
        mSlider.setMajorTickSpacing(50);
        mSlider.setMinorTickSpacing(10);
        mSlider.setPaintTicks(true);
        mSlider.setPaintLabels(true);
        
        mSlider.setMaximum(100);
        mSlider.setMinimum(0);
        
        mSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                    startRange.setText(String.valueOf(mSlider.getValueAt(0)));
                    setPositionStartColor(mSlider.getValueAt(0));
                    endRange.setText(String.valueOf(mSlider.getValueAt(1)));
                    setPositionEndColor(mSlider.getValueAt(1));
                    viewGradient.repaint();
                mSlider.repaint();
            }
        });

        startRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если в поле число
                String text = startRange.getText();
                if (text.replaceAll("\\d", "").equals("")) {
                    int value = Integer.parseInt(text);
                    if (value > -1 && value < 101) {
                        mSlider.setValueAt(value,0);
                        setPositionStartColor(value);
                        viewGradient.repaint();
                    }
                }

            }
        });
        
        endRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если в поле число
                String text = endRange.getText();
                if (text.replaceAll("\\d", "").equals("")) {
                    int value = Integer.parseInt(text);
                    if (value > -1 && value < 101) {
                        mSlider.setValueAt(value,0);
                        setPositionEndColor(value);
                        viewGradient.repaint();
                    }
                }
            }
        });

        control.setBorder(Utils.createTitledBorder(getBorder(), "Градиентная заливка"));
        
        control.setOpaque(isOpaque);
        isCycle.setOpaque(isOpaque);
        isEnabled.setOpaque(isOpaque);
        mSlider.setOpaque(isOpaque);
        
        control.add(setStartColor, new GridBagConstraints(0, 0, 2, 1, 1, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        control.add(setEndColor, new GridBagConstraints(0, 1, 2, 1, 1, 0, LINE_START, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(setOrientation, new GridBagConstraints(0, 2, 1, 1, 1, 0, LINE_START, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(isCycle, new GridBagConstraints(0, 3, 1, 1, 1, 0, CENTER, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(isEnabled, new GridBagConstraints(1, 3, 1, 1, 1, 0, CENTER, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(startLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, LINE_START, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(startRange, new GridBagConstraints(1, 4, 1, 1, 0, 0, CENTER, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(endLabel, new GridBagConstraints(0, 5, 1, 1, 1, 0, LINE_START, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(endRange, new GridBagConstraints(1, 5, 1, 1, 0, 0, CENTER, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(mSlider, new GridBagConstraints(0, 6, 2, 1, 0, 0, CENTER, NONE, new Insets(4, 1, 1, 1), 0, 0));
        control.add(viewGradient, new GridBagConstraints(0, 7, 2, 1, 1, 1, CENTER, BOTH, new Insets(4, 1, 1, 1), 0, 0));
        add(control, new GridBagConstraints(2, 0, 1, 3, 0, 0, CENTER, BOTH, new Insets(1, 1, 1, 1), 0, 0));
    }

    /**
     * Установить начальный цвет градиента.
     *
     * @param startColor the new start color
     */
    public void setStartColor(Color startColor) {
        viewGradient.setStartColor((startColor == null) ? Color.black : startColor);
    }

    /**
     * Установить конечный цвет градиента.
     *
     * @param endColor the new end color
     */
    public void setEndColor(Color endColor) {
        viewGradient.setEndColor((endColor == null) ? Color.black : endColor);
    }

    /**
     * Установить ориентацию градиента.
     *
     * @param orientation the new orientation
     */
    public void setOrientation(int orientation) {
        viewGradient.setOrientation(orientation);
    }

    /**
     * Установить цикличность градиента.
     * 
     * @param isCycle
     *            the new cycle
     */
    public void setCycle(boolean isCycle) {
        viewGradient.setCycle(isCycle);
    }

    /**
     * Установить позицию начального цвета градиента.
     *
     * @param positionStartColor the new position start color
     */
    public void setPositionStartColor(int positionStartColor) {
        viewGradient.setPositionStartColor(positionStartColor);
    }

    /**
     * Установить позицию конечного цвета градиента.
     *
     * @param positionEndColor the new position end color
     */
    public void setPositionEndColor(int positionEndColor) {
        viewGradient.setPositionEndColor(positionEndColor);
    }

    /**
     * Циклический градиент активен?.
     *
     * @return true, if is cycle
     */
    public boolean isCycle() {
        return viewGradient.isCycle();
    }
    
    /**
     * Получить позицию начального цвета градиента
     * Позиция отсчитывается в процентах от начала линии градиента.
     *
     * @return the position start color
     */
    public int getPositionStartColor() {
        return viewGradient.getPositionStartColor();
    }

    /**
     * Получить позицию конечного цвета градиента
     * Позиция отсчитывается в процентах от начала линии градиента.
     *
     * @return the position end color
     */
    public int getPositionEndColor() {
        return viewGradient.getPositionEndColor();
    }

    /**
     * Получить начальный цвет градиента.
     *
     * @return the start color
     */
    public Color getStartColor() {
        return viewGradient.getStartColor();
    }

    /**
     * Получить конечный цвет градиента.
     *
     * @return the end color
     */
    public Color getEndColor() {
        return viewGradient.getEndColor();
    }

    /**
     * Получить ориентацию градиента.
     *
     * @return the orientation
     */
    public int getOrientation() {
        return viewGradient.getOrientation();
    }
    
    public boolean isEnabledGradient() {
        return viewGradient.isEnabledGradient();
    }

    public void setEnabledGradient(boolean enabled) {
        viewGradient.setEnabledGradient(enabled);
    }
    
    public GradientColor getGradient() {
        return new GradientColor(getStartColor(),getEndColor(),getOrientation(),isCycle(),getPositionStartColor(),getPositionEndColor(),isEnabledGradient());
    }
}
