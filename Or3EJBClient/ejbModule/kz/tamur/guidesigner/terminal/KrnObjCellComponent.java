package kz.tamur.guidesigner.terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.Or3Frame;
import kz.tamur.admin.clsbrow.ObjectBrowser;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;

public class KrnObjCellComponent extends JPanel {
    JButton showButton = ButtonsFactory.createEditorButton(ButtonsFactory.IFC_EDITOR);
    
    JLabel label = new JLabel();

    KrnObjCellComponent(final Object ob) {
        super(new BorderLayout());
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ob instanceof KrnObject) {
                    KrnObject ko = (KrnObject) ob;

                    KrnClass cls = null;
                    try {
                        cls = Kernel.instance().getClass(ko.classId);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    if (cls == null)
                        return;
                    ObjectBrowser ob = null;
                    try {
                        ob = new ObjectBrowser(cls, false);
                        ob.setSelectedObject(ko, null);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    if (ob == null)
                        return;
                    Container cont = getTopLevelAncestor();
                    if (cont instanceof Dialog) {
                        DesignerDialog dlg = new DesignerDialog((Dialog) cont, "Объекты класса [" + cls.name + "]", ob);

                        dlg.show();
                    } else {
                        JFrame frame = new JFrame("Объекты класса [" + cls.name + "]");
                        frame.setSize(256, 256);
                        frame.setIconImage(Or3Frame.instance().getIconImage());
                        frame.setLocationRelativeTo(null);
                        frame.getContentPane().add(ob);
                        frame.pack();
                        frame.show();
                    }
                }
            }
        });
        add(label, BorderLayout.LINE_START);
        add(showButton, BorderLayout.LINE_END);
        showButton.setPreferredSize(new Dimension(20, 5));
    }

    public void load(JTable table, Object value, boolean isSelected, int row, int column) {
        label.setText(value.toString());
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }
}
