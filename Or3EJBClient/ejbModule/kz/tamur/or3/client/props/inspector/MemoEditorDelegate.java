package kz.tamur.or3.client.props.inspector;

import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.util.Funcs;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.Or3Frame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

public class MemoEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Object value;
    private PropertyEditor editor;

	private JLabel label;
	private JButton memoBtn;
    long langId = DesignerFrame.instance().getInterfaceLang().id;

    public MemoEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        memoBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(memoBtn, new CnrBuilder().x(0).build());
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
        this.value=value;
        if(value instanceof String){
            label.setText(value.toString());
        }else{
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
        if (e.getSource() == memoBtn) {
                String data=(String)value;
                value=editMemo(data);
                if((value==null && data!=null) ||(!value.equals(data)))
                    editor.stopCellEditing();
            }
    }
    private String editMemo(String data){
            String text = (data != null) ? data : "";
            JTextArea editor = new JTextArea(text);
            JScrollPane scroller = new JScrollPane(editor);
            scroller.setPreferredSize(new Dimension(600, 400));
            DesignerDialog dlg = new DesignerDialog(
                    Or3Frame.instance(),
                    "Ввод и редактирование коментария", scroller);
            dlg.show();
            String text_ = Funcs.normalizeInput(editor.getText());
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK && !text.equals(text_)) {
                    return text_;
            }
        return data;
    }

}
