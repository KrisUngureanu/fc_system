package kz.tamur.comps.ui.textField;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class OrPropTextField extends JTextField {

    public OrPropTextField() {
    }

    public OrPropTextField(String text) {
        super(text);
    }

    public OrPropTextField(int columns) {
        super(columns);
    }

    public OrPropTextField(String text, int columns) {
        super(text, columns);
    }

    public OrPropTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    public void updateUI() {
        super.updateUI();
        if (getUI() instanceof OrTextFieldUI) {
            ((OrTextFieldUI) getUI()).setRound(0);
            ((OrTextFieldUI) getUI()).setDrawBorder(false);
            ((OrTextFieldUI) getUI()).setDrawFocus(false);
            setOpaque(true);
        }
    }

}
