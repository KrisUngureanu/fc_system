package kz.tamur.or3.client.props.inspector;

import kz.tamur.Or3Frame;
import kz.tamur.or3.client.props.ComboToolTipProperty;
import kz.tamur.or3.client.props.ComboToolTipPropertyItem;
import kz.tamur.guidesigner.DesignerFrame;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import com.sun.awt.AWTUtilities;

import java.awt.*;
import java.awt.event.*;

/**
 * Компонент расширяющий выпадающий список JComboBox
 * 
 * В нём реализованно: - чередование цветов пунктов списка - всплывающая подсказка (картинка) приенаведении на пункт меню
 * 
 * @author Sergey Lebedev
 * 
 */
public class ComboToolTipEditorDelegate extends JComboBox implements EditorDelegate {

    private ComboToolTipPropertyItem value;
    private PropertyEditor propertyEditor;
    ComboToolTipPropertyItem items[];

    // Окно всплывающей подсказки
    JWindow tollTip = null;

    int prevIndx = -1;

    /**
     * Показывает всплывающую подсказку
     * 
     * @param pathIco
     *            имя картинки, которую необходимо отобразить на подсказке
     * @param x
     *            координаты подсказки по X
     * @param y
     *            координаты подсказки по Y
     */
    private void showToolTip(String pathIco, int x, int y) {
        // картинка подсказки
        ImageIcon imgTest = null;
        try {
            // получить картинку
            imgTest = kz.tamur.rt.Utils.getImageIconFull(pathIco);
        } catch (NullPointerException e) {
            System.out.println("Картинка " + pathIco + " не найдена!");
            return;
        }
        // Всплывающая подсказка
        tollTip = new JWindow();
        // контейнер для картинки
        JLabel pict = new JLabel();
        // поместить картинку в контейнер
        pict.setIcon(imgTest);
        // добавить контейнер на окно подсказки
        tollTip.add(pict);
        // задать координаты вывода окна
        tollTip.setLocation(x, y);
        // установка прозрачности окна
        AWTUtilities.setWindowOpaque(tollTip, false);
        // компоновка
        tollTip.pack();
        // вывод
        tollTip.setVisible(true);
        tollTip.requestFocusInWindow();
    }

    /**
     * Скрывает всплывающее окошко с подсказкой
     */
    private void hideToolTip() {
        if (tollTip != null) {
            tollTip.setVisible(false);
            tollTip.dispose();
        }
    }

    /**
     * Конструктор класса
     * 
     * @param comboToolTipProperty
     *            в данном атрибуте передаются пункты меню которые необходимо вывести
     * @param table
     *            предок компонента
     */
    public ComboToolTipEditorDelegate(ComboToolTipProperty comboToolTipProperty, final JTable table) {
        // сформировать список возможностями суперкласса
        super(comboToolTipProperty.getItems());
        // извлечь пункты меню в объект для дальнейшего использования
        items = (ComboToolTipPropertyItem[]) comboToolTipProperty.getItems();
        // Создать собственный Рендер
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        // назначить Рендер текущему классу
        this.setRenderer(renderer);
        this.setFont(table.getFont());
        this.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor()));
        this.setBackground(kz.tamur.rt.Utils.getLightSysColor());
        this.setEditable(false);

        // запомнить выбранный пункт списка
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                value = (ComboToolTipPropertyItem) getSelectedItem();
            }
        });
        // обработка нажатия клавиш
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                // отслеживание нажатие клавиш
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // прекратить редактирование
                    propertyEditor.stopCellEditing();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // отменить редактирование
                    propertyEditor.cancelCellEditing();
                } else {
                    return;
                }
                // передать фокус таблице
                table.requestFocusInWindow();
                // закрыть подсказку
                hideToolTip();
            }
        });
        // получить выпадающий список компонента
        Object popup = getUI().getAccessibleChild(null, 0);
        if (popup instanceof ComboPopup) {
            ((ComboPopup) popup).getList().addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    propertyEditor.stopCellEditing();
                    table.requestFocusInWindow();
                    // закрыть подсказку
                    hideToolTip();
                }

                public void mouseExited(MouseEvent e) {
                    // закрыть подсказку
                    hideToolTip();
                }
            });
        }
    }

    /**
     * Получить количество строк для отображения?
     */
    public int getClickCountToStart() {
        return 1;
    }

    /**
     * Получить редактор компонента
     */
    public Component getEditorComponent() {
        return this;
    }

    /**
     * Получить значение компонента
     */
    public Object getValue() {
        return value;
    }

    /**
     * Установить значение компонента
     * 
     * @param value
     *            объект, который будет устанавливаться
     */
    public void setValue(Object value) {
        this.value = (ComboToolTipPropertyItem) value;
        this.setSelectedItem(value);
    }

    /**
     * Установить редактор свойств
     * 
     * @param editor
     *            редактор
     */
    public void setPropertyEditor(PropertyEditor editor) {
        propertyEditor = editor;
    }

    /**
     * Собственный Рендер компонета
     * 
     * @author Sergey Lebedev
     * 
     */
    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            // Если пункт, для отрисовки не пуст
            // пометить блок показа всплывающей подсказки, для корректоного выхода из него при возникновении исключения
            showToolTip: if (value != null) {
                // установить для ячейки текст, переведя из переданого объекта
                // TITLE
                setText(value.toString());
                // если пункт выбран и это не повторное срабатывание
                if (isSelected && index != prevIndx) {
                    // запомнить индекс пункта
                    prevIndx = index;
                    // ширина комбобокса
                    final int w = ComboToolTipEditorDelegate.this.getWidth();// list.getWidth();
                    // ВИДИМАЯ высота списка
                    final int h = ComboToolTipEditorDelegate.this.getHeight() * list.getVisibleRowCount();

                    // Скрыть предыдущую подсказку
                    hideToolTip();
                    Point locationList;
                    // позиция списка выбора на дисплее
                    Point locationCombo;
                    // позиция инспектора свойств компонентов (необходимо когда
                    // инспектор в плавающем режиме)
                    Point locationInspector;
                    try {
                        locationList = list.getTopLevelAncestor().getMousePosition();
                        locationCombo = ComboToolTipEditorDelegate.this.getLocationOnScreen();
                        locationInspector = DesignerFrame.instance().getLocationInspector();
                    } catch (NullPointerException e) {
                        // выйти из блока showToolTip
                        break showToolTip;
                    }
                    // вычисление координат по оси Х
                    int x = (int) (locationCombo.getX() - 321);
                    // если расчётные координаты за границами дисплея
                    if (x < 0) {
                        // изменить координаты так, чтобы подсказка выводилась
                        // справа от списка(плюс три пикселя для визуального
                        // отделения подсказки от компонента)
                        x = (int) locationCombo.getX() + w + 3;
                    }
                    // координаты по оси Y
                    // для списка (на каком пункте меню находиться курсор мыши)
                    final int pYL = (int) locationList.getY();
                    // для комбобокса
                    final int pYC = (int) locationCombo.getY();

                    // вычисление координат по оси Y (плюс 10 - поправка)
                    int y = pYL + pYC + 10;

                    // получить высоту разрешения дисплея
                    final int scrSizeY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

                    // если вычисленные координаты находятся за границами экрана
                    if (y > scrSizeY) {
                        // Если испектор свойств находиться в плавающем режиме
                        if (DesignerFrame.instance().isInspectorFloat()) {
                            y = pYC - (h - pYL) - 100;
                            if (y + 70 > scrSizeY) {
                                int yI = (int) locationInspector.getY();
                                y = yI + pYC - (pYC - pYL) - 30;
                            }
                        } else {
                            y = pYC - (h - pYL) - 100;
                            // если вычисленные координаты находятся за границами экрана
                            if (y > scrSizeY) {
                                // если главное окно максимизированно
                                if (Or3Frame.instance().getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                                    y = pYC - (pYC - pYL) - 15;
                                } else {
                                    int yF = (int) Or3Frame.instance().getLocationOnScreen().getY();
                                    y = yF + pYC - (pYC - pYL) - 15;
                                }
                            }
                        }
                    }

                    // показать подсказку
                    showToolTip(((ComboToolTipPropertyItem) value).pathIco, x, y);
                }
            }// конец блока showToolTip

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                // выделить необходимые цвета
                Color cB = list.getBackground();
                Color cF = list.getForeground();
                // Массивы для промежуточного хранения цветов
                float[] aB = new float[3];
                float[] aF = new float[3];
                Color.RGBtoHSB(cB.getRed(), cB.getGreen(), cB.getBlue(), aB);
                Color.RGBtoHSB(cF.getRed(), cF.getGreen(), cF.getBlue(), aF);
                // реализация чередования цветов для строчек списка
                if (index % 2 == 0) {
                    setBackground(cB);
                    setForeground(cF);
                } else {
                    setBackground(Color.getHSBColor(aB[0], aB[1] / 3, aB[2] / 2));
                    setForeground(Color.getHSBColor(aF[0], aF[1] / 3, aF[2] / 2));
                }
            }
            return this;
        }
    }
}
