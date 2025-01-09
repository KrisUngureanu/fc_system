package kz.tamur.or3.client.props.inspector;

import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.Or3Frame;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;

import com.cifs.or2.client.gui.OrIconToolTip;
import com.cifs.or2.client.util.CnrBuilder;

/**
 * The Class ImageEditorDelegate.
 */
public class ImageEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

    /** The value. */
    private byte[] value;
    
    /** The editor. */
    private PropertyEditor editor;
    
    /** Метка для отображения значения. */
    private OrLabelIconToolTip label;
    
    /** Кнопка вызывающая диалог выбора иконки. */
    private JButton iconBtn;
    
    /** Кнопка очистки поля. */
    private JButton clearBtn;

    /**
     * Создание нового image editor delegate.
     *
     * @param table the table
     */
    public ImageEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());

        label = new OrLabelIconToolTip();
        label.setFont(table.getFont());

        iconBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        iconBtn.setToolTipText("Выбрать картинку");
        
        clearBtn = new JButton();
        clearBtn.setMargin(Constants.INSETS_0);
        Utils.setAllSize(clearBtn, Constants.BTN_EDITOR_SIZE);
        clearBtn.addActionListener(this);
        clearBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("deleteAll"));
        clearBtn.setToolTipText("Очистить");

        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(clearBtn, new CnrBuilder().x(1).build());
        add(iconBtn, new CnrBuilder().x(0).build());
    }

    
    public int getClickCountToStart() {
        return 1;
    }

    
    public Component getEditorComponent() {
        return this;
    }

    
    public Object getValue() {
        return value;
    }

    
    public void setValue(Object value) {
        if (value instanceof String) {
            byte[] expr = ((String) value).getBytes();
            this.value = expr;
            if (value != null && !((String) value).isEmpty()) {
                label.setText("Задана картинка");
            } else {
                label.setText("");
            }
            // установить иконку на подсказку
            label.setIconToolTip(Utils.processCreateImage(expr));
            label.setToolTipText("");
        } else if (value != null && !"".equals(value)&&value instanceof byte[]) {
            byte[] expr = (byte[]) value;
            this.value = expr;
                label.setText(expr.length > 0?"Задана картинка":"");
            // установить иконку на подсказку
            label.setIconToolTip(Utils.processCreateImage(expr));
            label.setToolTipText("");
        } else {
            this.value = null;
            label.setText("");
            // удалить иконку из подсказки
            label.setIconToolTip(null);
            label.setToolTipText(null);
        }
    }

    
    public Component getRendererComponent() {
        return this;
    }

    
    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;

    }

    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == iconBtn) {
            JFileChooser fChooser = kz.tamur.comps.Utils.createOpenChooser(Constants.IMAGE_FILTER);
            if (fChooser.showOpenDialog(Or3Frame.instance()) == JFileChooser.APPROVE_OPTION) {
                File sf = fChooser.getSelectedFile();
                Utils.setLastSelectDir(sf.getParentFile().toString());
                if (sf != null) {
                    byte[] val = null;
                    try {
                    	value = Funcs.read(sf);
                        editor.stopCellEditing();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                editor.cancelCellEditing();
            }
        } else if (e.getSource() == clearBtn) {
            if (MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                    "Вы действительно ходите удалить иконку?") == ButtonsFactory.BUTTON_YES) {
                value = null;
                label.setText("");
                // удалить иконку из подсказки
                label.setIconToolTip(null);
                label.setToolTipText(null);
                editor.stopCellEditing();
            } else {
                editor.cancelCellEditing();
            }
        }
    }
}

/**
 * Класс реализующий метку со всплывающей подсказкой в которой показывается иконка
 * 
 * @author Sergey Lebedev
 * 
 */
class OrLabelIconToolTip extends JLabel {
    private ImageIcon icon = null;

    OrLabelIconToolTip() {
        super();
    }

    public void setIconToolTip(ImageIcon icon) {
        this.icon = icon;
    }

    public JToolTip createToolTip() {
        return new OrIconToolTip(icon);
    }
}
