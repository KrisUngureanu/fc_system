package kz.tamur.guidesigner.expr;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;

public class ClassPropDialog extends JDialog {

	private static final long serialVersionUID = -1971201964615361333L;
	private final Component BodyComponent;
	
    public ClassPropDialog(Dialog owner, String title, Component comp) {
        super(owner, title, true);
        this.BodyComponent = comp;
        //init();
    }
    
    public ClassPropDialog(Frame owner, String title, Component comp) {
        super(owner, title, true);
        this.BodyComponent = comp;
        //init();
    }

}
