package kz.tamur.comps.ui.textField;

import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.text.*;

import kz.tamur.comps.Constants;
import kz.tamur.comps.MouseDelegator;
import kz.tamur.comps.ui.comboBox.OrAutoComboBox;

/**
 * Редактор для автодополняемого списка {@link kz.tamur.comps#OrAutoComboBox}
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrAutoTextField extends JTextField implements kz.tamur.comps.MouseTarget {

    /** Список с данными. */
    private List<String> dataList;

    /** Чувствительность к регистру? */
    private boolean isCaseSensitive;

    /** Ввод данных только из списка? */
    private boolean isStrict;

    /** Компонент в котором применяется данный редактор */
    private OrAutoComboBox autoComboBox;

    /**
     * Создание нового or auto text field.
     * 
     * @param list
     *            the list
     */
    public OrAutoTextField(List<String> list) {
        isCaseSensitive = true;
        isStrict = false;
        autoComboBox = null;
        if (list == null) {
            throw new IllegalArgumentException("Значение не может быть пустым!");
        } else {
            dataList = list;
            init();
        }
    }

    /**
     * Создание нового or auto text field.
     * 
     * @param list
     *            the list
     * @param b
     *            the b
     */
    public OrAutoTextField(List<String> list, OrAutoComboBox b) {
        isCaseSensitive = true;
        isStrict = false;
        autoComboBox = null;
        if (list == null) {
            throw new IllegalArgumentException("Значение не может быть пустым!");
        } else {
            dataList = list;
            autoComboBox = b;
            init();
        }
    }

    /**
     * Инициализация редактора
     */
    private void init() {
        setUI(new OrTextFieldUI(this));  // TODO убрать в новом UI для TextField 
        
        setOpaque(false);

        if  (getUI() instanceof OrTextFieldUI) {
            ((OrTextFieldUI) getUI()).setDrawBorder(false);
        }
        setMargin(new Insets(0, 3, 0, 1));

        setDocument(new AutoDocument());
        if (isStrict && dataList.size() > 0) {
            setText(dataList.get(0).toString());
        }

        MouseDelegator delegator = new MouseDelegator(this);
        addMouseListener(delegator);
    }

    /**
     * Получить совпадения строки с элементом списка.
     * 
     * @param string
     *            строка, совпадение с которой необходимо проверить
     * @return запись из списка, совпадающая с введённым значением
     */
    private String getMatch(String string) {
        String record;
        // перебор всего списка
        for (int i = 0; i < dataList.size(); i++) {
            record = dataList.get(i).toString();
            if (record != null) {
                if (!isCaseSensitive && record.toUpperCase(Constants.OK).startsWith(string.toUpperCase(Constants.OK)) || isCaseSensitive
                        && record.startsWith(string))
                    return record;
            }
        }
        return null;
    }

    public void replaceSelection(String s) {
        AutoDocument _lb = (AutoDocument) getDocument();
        if (_lb != null)
            try {
                int i = Math.min(getCaret().getDot(), getCaret().getMark());
                int j = Math.max(getCaret().getDot(), getCaret().getMark());
                _lb.replace(i, j - i, s, null);
            } catch (Exception exception) {
            }
    }

    /**
     * Регистрозависим?
     * 
     * @return true, если регистрозависим
     */
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    /**
     * Установить регистрозависимость
     * 
     * @param flag
     *            the new case sensitive
     */
    public void setCaseSensitive(boolean flag) {
        isCaseSensitive = flag;
    }

    /**
     * Проверяет, возможно ли вводить данные только из списка записей.
     * 
     * @return true, если strict
     */
    public boolean isStrict() {
        return isStrict;
    }

    /**
     * Установить ввод данных только из списка.
     * 
     * @param flag
     *            the new strict
     */
    public void setStrict(boolean flag) {
        isStrict = flag;
    }

    /**
     * Получить список записей
     * 
     * @return the data list
     */
    public List<String> getDataList() {
        return dataList;
    }

    /**
     * Установить новый список записей
     * 
     * @param list
     *            the new data list
     */
    public void setDataList(List<String> list) {
        if (list == null) {
            throw new IllegalArgumentException("Значение не может быть пустым!");
        } else {
            dataList = list;
            init();
        }
    }

    /**
     * The Class AutoDocument.
     */
    class AutoDocument extends PlainDocument {

        public void replace(int i, int j, String s, AttributeSet attributeset) throws BadLocationException {
            // обработка моментов возникающий когда значение в компоненте не изменяется, но метод вызывается,
            // событие changeValue. Возникает при потере фокуса компонентом
            if (i == 0 && s.length() == j && s.equals(getText(i, j))) {
                return;
            }
            super.remove(i, j);
            insertString(i, s, attributeset);
        }

        public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
            if (s == null || "".equals(s))
                return;
            String s1 = getText(0, i);
            String s2 = getMatch(s1 + s);
            int j = (i + s.length()) - 1;
            if (isStrict && s2 == null) {
                s2 = getMatch(s1);
                j--;
            } else if (!isStrict && s2 == null) {
                super.insertString(i, s, attributeset);
                return;
            }
            if (autoComboBox != null && s2 != null)
                autoComboBox.setSelectedValue(s2);
            super.remove(0, getLength());
            super.insertString(0, s2, attributeset);
            setSelectionStart(j + 1);
            setSelectionEnd(getLength());
        }
    }

    public void delegateMouseEvent(MouseEvent e) {
        // При клике на редактор, нужно перенести фокус на родительский comboBox для корректной отрисовки выделения поля
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            autoComboBox.requestFocusInWindow();
        }
    }

    public void delegateMouseMotionEvent(MouseEvent e) {}

}
