package kz.tamur.rt;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cifs.or2.client.DateField;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerModalFrame;

public class TaskFilterParamsDialog extends DesignerModalFrame implements ActionListener {
    private Map<String, Object> params;
    private DateField begDate = new DateField();
    private DateField endDate = new DateField();
    private static JPanel content_ = new JPanel();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public TaskFilterParamsDialog(Frame owner, Map<String, Object> params) {
        super(owner, "Введите параметры фильтра", content_ = new JPanel());

        this.params = params;

        Object v = params.get(TaskTable.FLR_DATE_BEGIN);
        if (v instanceof Date) {
            begDate.setValue((Date) v);
        }

        v = params.get(TaskTable.FLR_DATE_END);
        if (v instanceof Date) {
            endDate.setValue((Date) v);
        }
        content_.setOpaque(isOpaque);
        content_.setLayout(new GridLayout(2, 2, 5, 5));
        content_.add(new JLabel("Начальная дата:"));
        content_.add(begDate);
        content_.add(new JLabel("Конечная дата:"));
        content_.add(endDate);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okBtn) {
            dialogResult = ButtonsFactory.BUTTON_OK;
            Date v = begDate.getValue();
            if (v != null) {
                params.put(TaskTable.FLR_DATE_BEGIN, v);
            } else {
                params.remove(TaskTable.FLR_DATE_BEGIN);
            }
            v = endDate.getValue();
            if (v != null) {
                params.put(TaskTable.FLR_DATE_END, v);
            } else {
                params.remove(TaskTable.FLR_DATE_END);
            }
            dispose();
        } else if (src == cancelBtn) {
            dispose();
        }
    }

}
