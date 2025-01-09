package kz.tamur.rt.adapters;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kz.tamur.comps.OrAccordion;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrTextField;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class AccordionAdapter.
 *
 * @author Lebedev Sergey
 */
public class AccordionAdapter extends ComponentAdapter implements ActionListener {

    /** panel. */
    private OrAccordion panel;

    /**
     * Конструктор класса pop up panel adapter.
     * 
     * @param frame
     *            the frame
     * @param c
     *            the c
     * @param isEditor
     *            the is editor
     * @throws KrnException
     *             the krn exception
     */
    public AccordionAdapter(OrFrame frame, OrAccordion c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
        panel = c;
       // this.panel.addActionListener(this);
    }

    
    @Override
    public void clear() {
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        Container cont = panel.getTopLevelAncestor();
        // Если фокус находится на текстовом поле, то сохраняем значение этого поля перед выполнением формулы кнопки.
        if (cont instanceof Window) {
            Component comp = ((Window) cont).getFocusOwner();
            if (comp instanceof OrTextField) {
                ((OrTextField) comp).saveValue();
            }
        }
    }

    
    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        panel.setEnabled(isEnabled);
    }

}
