package kz.tamur.util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 25.05.2006
 * Time: 11:17:49
 * To change this template use File | Settings | File Templates.
 */
public class OrTreeLineToolTip extends JToolTip {

    private static final String uiClassID = "ToolTipUI";


    String tipText;
    JComponent component;

    public OrTreeLineToolTip() {
        updateUI();
    }


    public void updateUI() {
        setUI(TreeLineToolTipUI.createUI(this));
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

 class TreeLineToolTipUI extends BasicToolTipUI {
 	static TreeLineToolTipUI sharedInstance = new TreeLineToolTipUI();
   	Font smallFont;
	static JToolTip tip;
	protected CellRendererPane rendererPane;
     private String[] spaces = new String[20];

	private static JTextArea textArea ;

	public static ComponentUI createUI(JComponent c) {
	    return sharedInstance;
	}

	public TreeLineToolTipUI() {
	    super();
        String space = "  ";
        for (int i = 0; i < spaces.length; i++) {
            spaces[i] = (i == 0) ? space : space + "  ";
            space = space + "  ";
        }
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
        if (tipText != null) {
            StringTokenizer st = new StringTokenizer(tipText, ".");
            String resultStr = "";
            int i = 0;
            while(st.hasMoreTokens()) {
                resultStr = ("".equals(resultStr)) ?
                        resultStr + st.nextToken(): resultStr + "\n" + spaces[i] + "|_" + st.nextToken();
                i++;
            }

            textArea = new JTextArea(resultStr );
            rendererPane.removeAll();
            rendererPane.add(textArea );
            textArea.setWrapStyleWord(true);

            int width = ((OrTreeLineToolTip)c).getFixedWidth();
            int columns = ((OrTreeLineToolTip)c).getColumns();

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
        } else {
            return new Dimension(0, 0);
        }
    }

	public Dimension getMinimumSize(JComponent c) {
	    return getPreferredSize(c);
	}

	public Dimension getMaximumSize(JComponent c) {
	    return getPreferredSize(c);
	}


}
