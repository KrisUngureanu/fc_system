package kz.tamur.or3.client.props.inspector;

import kz.tamur.util.StyledEditor;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.Or3Frame;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.cifs.or2.client.util.CnrBuilder;

public class DescEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private byte[] value;
    private PropertyEditor editor;

	private JLabel label;
	private JButton descBtn;

    public DescEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        descBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(descBtn, new CnrBuilder().x(0).build());
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
        if(value instanceof String){
            byte[] expr = ((String)value).getBytes();
            this.value = expr;
			label.setText((String)value);

        }else if(value != null && !"".equals(value)) {
            byte[] expr = (byte[])value;
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
		this.editor=editor;

	}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == descBtn) {
           String text = "";
            if (value instanceof byte[]) {
                    text = new String(value);
            }
            StyledDocument doc = new DefaultStyledDocument();
            StyledEditor editor_ = new StyledEditor(doc);
            RTFEditorKit kit = (RTFEditorKit)editor_.getEditor().getEditorKit();
            ByteArrayInputStream is = new ByteArrayInputStream(text.getBytes());
            try {
                kit.read(is, doc, 0);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Редактор текста", editor_);
            dlg.show();
            if (dlg.isOK()) {
                try {
                    doc = editor_.getDocument();
                    String s = doc.getText(0, doc.getLength());
                    if (!"".equals(s)) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        kit.write(os, doc, 0, doc.getLength());
                        os.close();
                        String temp = os.toString();
                        int end = temp.lastIndexOf("\\par");
                        temp = temp.substring(0, end) + temp.substring(end + 4);
                        value = temp.getBytes();
                    } else {
                        value = null;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            editor.stopCellEditing();
        }
    }

}
