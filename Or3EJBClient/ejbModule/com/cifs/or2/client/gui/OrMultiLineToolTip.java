package com.cifs.or2.client.gui;

/**
 * Title:        OR2
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CIFS
 * @author Zafir Anjum
 * @version 1.0
 */

// OrMultiLineToolTip.java

import javax.swing.*;

import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicToolTipUI;


public class OrMultiLineToolTip extends JToolTip {
    private static final String uiClassID = "ToolTipUI";

    String tipText;
    JComponent component;

    public OrMultiLineToolTip() {
        updateUI();
    }


    public void updateUI() {
        setUI(MultiLineToolTipUI.createUI(this));
    }


    public void setColumns(int columns) {
        this.columns = columns;
        this.fixedwidth = 0;
    }

    public int getColumns() {
        return columns;
    }

    public void setFixedWidth(int width) {
        this.fixedwidth = width;
        this.columns = 0;
    }

    public int getFixedWidth() {
        return fixedwidth;
    }

    protected int columns = 0;
    protected int fixedwidth = 0;
}

 class MultiLineToolTipUI extends BasicToolTipUI {
 	static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
   	Font smallFont;
	static JToolTip tip;
	protected CellRendererPane rendererPane;

	private static JTextArea textArea ;

	public static ComponentUI createUI(JComponent c) {
	    return sharedInstance;
	}

	public MultiLineToolTipUI() {
	    super();
	}

	public void installUI(JComponent c) {
	    super.installUI(c);
		tip = (JToolTip)c;
	    rendererPane = new CellRendererPane();
	    c.add(rendererPane);
	}

	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

	    c.remove(rendererPane);
	    rendererPane = null;
	}

	public void paint(Graphics g, JComponent c) {
	    Dimension size = c.getSize();
            textArea.setBackground(c.getBackground());
		rendererPane.paintComponent(g, textArea, c, 1, 1,
					    size.width - 1, size.height - 1, true);
	}

	public Dimension getPreferredSize(JComponent c) {
		String tipText = ((JToolTip)c).getTipText();
		if (tipText == null)
			return new Dimension(0,0);
		textArea = new JTextArea(tipText );
                rendererPane.removeAll();
		rendererPane.add(textArea );
		textArea.setWrapStyleWord(true);

		int width = ((OrMultiLineToolTip)c).getFixedWidth();
		int columns = ((OrMultiLineToolTip)c).getColumns();

		if( columns > 0 )
		{
			textArea.setColumns(columns);
			textArea.setSize(0,0);
  		textArea.setLineWrap(true);
			textArea.setSize( textArea.getPreferredSize() );
		}
		else if( width > 0 )
		{
		textArea.setLineWrap(true);
			Dimension d = textArea.getPreferredSize();
			d.width = width;
			d.height++;
			textArea.setSize(d);
		}
		else
			textArea.setLineWrap(false);


		Dimension dim = textArea.getPreferredSize();

		dim.height += 1;
		dim.width += 1;
		return dim;
	}

	public Dimension getMinimumSize(JComponent c) {
	    return getPreferredSize(c);
	}

	public Dimension getMaximumSize(JComponent c) {
	    return getPreferredSize(c);
	}

}










