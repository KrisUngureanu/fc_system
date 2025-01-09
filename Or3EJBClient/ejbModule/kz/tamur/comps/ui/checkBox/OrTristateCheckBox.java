package kz.tamur.comps.ui.checkBox;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.event.*;



import static kz.tamur.comps.Constants.NOT_SELECTED;
import static kz.tamur.comps.Constants.SELECTED;
import static kz.tamur.comps.Constants.DONT_CARE;

/**
 * Класс реализует трёхпозиционный чекбокс.
 */
public class OrTristateCheckBox extends OrBasicCheckBox {

    /** The model. */
    private TristateDecorator model;

    /**
     * Разрешено ли переключаться по щелчку мыши или клавиши в третье состояние чекбокса
     * если <code>false</code>, то переключение возможно только программым путём
     */
    private boolean isAllowedSelectTri;

    /**
     * Конструктор класса.
     * 
     * @param text
     *            текст
     * @param initial
     *            инициирующее значение
     * @param isAllowedSelectTri
     *            разрешено ли переключаться по щелчку мыши или клавиши в третье состояние чекбокса
     *            если <code>false</code>, то переключение возможно только программым путём
     */
    public OrTristateCheckBox(String text, int initial, final boolean isAllowedSelectTri) {
        super(text);
        this.isAllowedSelectTri = isAllowedSelectTri;
        // Слушатель на нажатие мышки. 
        super.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                grabFocus();
                model.nextState();
            }
        });

        // Перегрузка карты действий клавиатуры.
        ActionMap map = new ActionMapUIResource();
        map.put("pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                grabFocus();
                model.nextState();
            }
        });

        map.put("released", null);
        SwingUtilities.replaceUIActionMap(this, map);
        
        // Задать модель в адаптере модели.
        model = new TristateDecorator(getModel());
        setModel(model);
        setState(initial);
    }

    /**
     * Создание нового or tristate check box.
     * 
     * @param text
     *            текст
     * @param initial
     *            инициирующее значение
     */
    public OrTristateCheckBox(String text, int initial) {
        this(text, initial, false);
    }

    /**
     * Конструктор класса.
     * @param text
     *            текст
     */
    public OrTristateCheckBox(String text) {
        this(text, DONT_CARE);
    }

    /**
     * Конструктор класса.
     */
    public OrTristateCheckBox() {
        this(null);
    }

    /**
     * Исключить добавление дополнительных слушателей мыши.
     * 
     * @param l
     *            слушатель
     */
    public void addMouseListener(MouseListener l) {
    }

    /**
     * Исключить добавление дополнительных слушателей клавиатуры. 
     * 
     * @param l
     *            слушатель
     */
    public void addKeyListener(KeyListener l) {
    }

    /**
     * Установка нового состояния компонента SELECTED, NOT_SELECTED или
     * DONT_CARE. Если состояние <code>null</code>, состояние переключается в DONT_CARE.
     * 
     * @param state
     *            новое состояние компонента
     */
    public void setState(int state) {
        model.setState(state);
    }

    /**
     * Возвращет текущее состояние комопнента, определяемое моделью
     * 
     * @return the state
     */
    public int getState() {
        return model.getState();
    }

    public void setSelected(boolean b) {
        setState(b ? SELECTED : NOT_SELECTED);
    }

    /**
     * Разрешено ли переключаться в третье состояние по действию пользователя
     * 
     * @return <code>true</code> если да.
     */
    public boolean isAllowedSelectTri() {
        return isAllowedSelectTri;
    }

    /**
     * Обёртка модели.
     */
    private class TristateDecorator implements ButtonModel {

        /** Модель. */
        private final ButtonModel other;

        /**
         * Создание нового tristate decorator.
         * 
         * @param other
         *            the other
         */
        private TristateDecorator(ButtonModel other) {
            this.other = other;
        }

        /**
         * Задать состояние компонента.
         * 
         * @param state
         *            состояние
         */
        private void setState(int state) {
            switch (state) {
            case NOT_SELECTED:
                other.setArmed(false);
                setPressed(false);
                setSelected(false);
                break;
            case SELECTED:
                other.setArmed(false);
                setPressed(false);
                setSelected(true);
                break;
            default:
            case DONT_CARE:
                other.setArmed(true);
                setPressed(false);
                setSelected(true);
                fireStateChanged();
                fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, isSelected() ? ItemEvent.SELECTED
                        : ItemEvent.DESELECTED));
                break;
            }
        }

        /**
         * Текущее состояние заключено в состояниях selection и armed модели
         * 
         * Возвратить состояние SELECTED если компонент selected, но не armed.
         * DONT_CARE если компонент selected и armed (grey)
         * NOT_SELECTED если компонент deselected.
         * 
         * @return состояние чекбокса
         */
        public int getState() {
            if (isSelected() && !isArmed()) {
                // Состояние компонента - выбран (стоит "галочка").
                return SELECTED;
            } else if (isSelected() && isArmed()) {
                // Состояние компонента не определено () компонент затемнён
                return DONT_CARE;
            } else {
                // Состояние компонента - не выбран
                return NOT_SELECTED;
            }
        }

        /** Получить следующее состояние компонента NOT_SELECTED, SELECTED и DONT_CARE. */
        private void nextState() {
            switch (getState()) {
            case NOT_SELECTED:
                setState(SELECTED);
                break;
            case SELECTED:
                setState(isAllowedSelectTri ? DONT_CARE : NOT_SELECTED);
                break;
            case DONT_CARE:
                setState(NOT_SELECTED);
                break;
            }
        }

        /**
         * Фильтрация: Никому нельзя менять состояние ARMED кроме нас.
         * 
         * @param b
         *            новое состояние
         */
        public void setArmed(boolean b) {
        }

        /**
         * Убрать фокус с компонента, при деактивации его
         * 
         * @param b
         *            активность компонента
         */
        public void setEnabled(boolean b) {
            setFocusable(b);
            other.setEnabled(b);
        }

        // Все эти методы делегируются к нашей модели, которая декорируется.

        public boolean isArmed() {
            return other.isArmed();
        }

        public boolean isSelected() {
            return other.isSelected();
        }

        public boolean isEnabled() {
            return other.isEnabled();
        }

        public boolean isPressed() {
            return other.isPressed();
        }

        public boolean isRollover() {
            return other.isRollover();
        }

        public void setSelected(boolean b) {
            other.setSelected(b);
        }

        public void setPressed(boolean b) {
            other.setPressed(b);
        }

        public void setRollover(boolean b) {
            other.setRollover(b);
        }

        public void setMnemonic(int key) {
            other.setMnemonic(key);
        }

        public int getMnemonic() {
            return other.getMnemonic();
        }

        public void setActionCommand(String s) {
            other.setActionCommand(s);
        }

        public String getActionCommand() {
            return other.getActionCommand();
        }

        public void setGroup(ButtonGroup group) {
            other.setGroup(group);
        }

        public void addActionListener(ActionListener l) {
            other.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            other.removeActionListener(l);
        }

        public void addItemListener(ItemListener l) {
            other.addItemListener(l);
        }

        public void removeItemListener(ItemListener l) {
            other.removeItemListener(l);
        }

        public void addChangeListener(ChangeListener l) {
            other.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            other.removeChangeListener(l);
        }

        public Object[] getSelectedObjects() {
            return other.getSelectedObjects();
        }
    }
}
