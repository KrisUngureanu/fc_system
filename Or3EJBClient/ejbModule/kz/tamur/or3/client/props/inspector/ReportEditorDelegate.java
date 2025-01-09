package kz.tamur.or3.client.props.inspector;

import java.util.List;

import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.Or3Frame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

public class ReportEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private ReportRecord value;
    private PropertyEditor editor;
    long langId = DesignerFrame.instance().getInterfaceLang().id;

	private JLabel label;
	private JButton exprBtn;

    public ReportEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
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
		if (value != null && !"".equals(value)) {
            ReportRecord expr = (ReportRecord)value;
            this.value = expr;
            List<ReportRecord> chs=expr.getChildren();
            String txt="";
            for(ReportRecord rr:chs){
                txt += ("".equals(txt)?"":",")+rr.toString(langId);
            }
            label.setText(expr.getName()+":"+txt);
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
        if (e.getSource() == exprBtn) {
            ReportEditor re =  new ReportEditor(MultiEditor.REPORT_EDITOR, -1, langId);
            if (value != null) {
                re.setOldReportValue(value);
            } else {
                re.setOldReportValue(new ReportRecord());
            }
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                    "Выберите отчёты", re);
            dlg.setLanguage(langId);
            dlg.show();
            int res = dlg.getResult();
            if (res != ButtonsFactory.BUTTON_NOACTION
                    && res == ButtonsFactory.BUTTON_OK) {
                value = re.getSelectedReportValue();
                editor.stopCellEditing();
            }else
                editor.cancelCellEditing();
        }
    }
}
