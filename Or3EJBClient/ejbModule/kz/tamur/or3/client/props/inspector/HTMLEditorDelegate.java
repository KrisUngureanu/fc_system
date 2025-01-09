package kz.tamur.or3.client.props.inspector;

import kz.tamur.Or3Frame;

import kz.tamur.rt.Utils;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.noteeditor.HTMLEditor;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.util.ExpressionEditor;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

/**
 * Класс реализует ввод значения для свойства компонента {@link kz.tamur.comps.models#HTML_TEXT}
 * 
 * @author Sergey Lebedev
 * 
 */
public class HTMLEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener, EditorDelegateSet {

    /** значение компонента. */
    private Object value;
    
    private String stringValue = null;

    public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/** редактор свойств. */
    private PropertyEditor editor;

    /** метка, отображающее текущее значение. */
    private JLabel label;

    /** кнопка, вызывающая диалог ввода. */
    private JButton htmlBtn;
    private JButton expBtn;
    /**
     * кнопка очистки всплывающей подсказки.
     * необходимо от того что простое удаление символов
     * из поля не работает по причине того что в поле содержится HTML
     */
    private JButton clearBtn;

    /** The html. */
    private String html = "";

    /**
     * Конструктор класса.
     * 
     * @param table
     *            таблица свойств компонента
     */
    public HTMLEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        htmlBtn = new JButton();
        Utils.setAllSize(htmlBtn, Constants.BTN_EDITOR_SIZE);
        htmlBtn.addActionListener(this);
        htmlBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("edit"));
        htmlBtn.setToolTipText("Ввод форматируемого текста");

        expBtn = new JButton();
        Utils.setAllSize(expBtn, Constants.BTN_EDITOR_SIZE);
        expBtn.addActionListener(this);
        expBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("fx"));
        expBtn.setToolTipText("Редактор формул");

        clearBtn = new JButton();
        Utils.setAllSize(clearBtn, Constants.BTN_EDITOR_SIZE);
        clearBtn.addActionListener(this);
        clearBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("deleteAll"));
        clearBtn.setToolTipText("Очистить");

        add(label, new CnrBuilder().x(3).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(clearBtn, new CnrBuilder().x(2).build());
        add(htmlBtn, new CnrBuilder().x(1).build());
        add(expBtn, new CnrBuilder().x(0).build());
    }

    /**
     * Получить строку стартовой позиции.
     * 
     * @return стартовая позиция (обычно 1)
     */
    public int getClickCountToStart() {
        return 1;
    }

    /**
     * получить редактор.
     * 
     * @return the editor component
     */
    public Component getEditorComponent() {
        return this;
    }

    /**
     * получить значение свойства.
     * 
     * @return the value
     */

    public Object getValue() {
        return value;
    }

    
    public void setValue(Object value) {
    	if (value instanceof ExprEditorObject)
    		value = ((ExprEditorObject)value).getObject();
        if (value instanceof String) {
            byte[] expr = ((String) value).getBytes();
            this.value = expr;
            label.setText((String) value);
        } else if (value instanceof Expression) {
            this.value = (Expression) value;
            label.setText("Формула");
        } else if (value != null && !"".equals(value)) {
            byte[] expr = (byte[]) value;
            this.value = expr;
            label.setText(new String(expr));
        } else {
            this.value = null;
            label.setText("");
        }
    }

    
    public Component getRendererComponent() {
        return this;
    }

    
    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;

    }

    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == htmlBtn) {
            html = "";
            // извлечь html
            if (value instanceof byte[]) {
                html = new String((byte[]) value);
            }
            showHtmlEditor();
            editor.stopCellEditing();
        } else if (e.getSource() == clearBtn) {
            if (MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                    "Вы действительно ходите удалить значение?") == ButtonsFactory.BUTTON_YES) {
                html = "";
                value = null;
                label.setText("");
                editor.stopCellEditing();
            }else {
                editor.cancelCellEditing();
            }
        } else if (e.getSource() == expBtn) {
            String text = "";
            if (value != null) {
                if (value instanceof byte[]) {
                    text = new String((byte[]) value);
                } else if (value instanceof Expression) {
                    text = ((Expression) value).text;
                    stringValue = text;
                } else {
                    text = value.toString();
                }
            }
            ExpressionEditor exprEditor = new ExpressionEditor(text, HTMLEditorDelegate.this);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
            dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.show();
            if (dlg.isOK()) {
            	setExpression(exprEditor.getExpression());
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                editor.cancelCellEditing();
            }

        }
    }

    /**
     * Отобразить редактор подсказок
     */
    private void showHtmlEditor() {
        HTMLEditor htmlEd = new HTMLEditor();
        String htmlNew = "";
        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Редактор подсказок", htmlEd);
        if (!html.isEmpty()) {
            htmlEd.setHTML(html);
        }
        dlg.show();
        if (dlg.isOK()) {
            htmlNew = htmlEd.getHTML().trim();
            // Если значение изменено
            if (!html.equals(htmlNew)) {
                value = htmlNew.getBytes();
            }
        }
    }
    
    public void setExpression(String expression) {
    	 value = new Expression(expression);
         editor.stopCellEditing();
    }
}
