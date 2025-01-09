package kz.tamur.rt;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 16.12.2006
 * Time: 12:34:43
 * To change this template use File | Settings | File Templates.
 */
public class SearchWindow extends JWindow {
    private JTextField field;

    public SearchWindow(Window owner) {
        super(owner);
        init();
    }

    public SearchWindow(Frame owner) {
        super(owner);
        init();
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    private void init() {
        setAlwaysOnTop(true);
        setFocusable(false);
        field = new JTextField(3);
        field.setFocusable(false);
        setContentPane(field);
        pack();
    }

    public void setText(String text) {
    	field.setText("");
        try {
        	field.getDocument().insertString(0, text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        field.setColumns(text.length() + 2);
        pack();
    }

    public String addText(String text) {
        String t = field.getText();
        try {
        	field.getDocument().insertString(t.length(), text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        t = field.getText();
        field.setColumns(t.length() + 2);
        pack();
        return t;
    }

    public void setFound(boolean b) {
        if (b)
            field.setForeground(Color.black);
        else
            field.setForeground(Color.red);
    }

    public String deleteSymbol() {
        try {
            String t = field.getText();
            
            if (t.length() > 0) {
	            try {
	            	field.getDocument().remove(t.length() - 1, 1);
	    		} catch (BadLocationException e) {
	    			e.printStackTrace();
	    		}
            }
            t = field.getText();
            field.setColumns(t.length() + 2);
            pack();
            if (t.length() == 0) setVisible(false);
            return t;

        } catch (Exception e) {}
        return "";
    }
}
