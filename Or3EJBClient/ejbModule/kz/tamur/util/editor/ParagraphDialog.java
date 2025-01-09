package kz.tamur.util.editor;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.util.Funcs;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.security.SecureRandom;
import java.awt.event.FocusEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 24.06.2005
 * Time: 17:11:50
 * To change this template use File | Settings | File Templates.
 */
public class ParagraphDialog extends JDialog {
    protected int m_option = JOptionPane.CLOSED_OPTION;
    protected MutableAttributeSet m_attributes;
    protected JTextField m_lineSpacing;
    protected JTextField m_spaceAbove;
    protected JTextField m_spaceBelow;
    protected JTextField m_firstIndent;
    protected JTextField m_leftIndent;
    protected JTextField m_rightIndent;
    protected ButtonsFactory.ToggleButton m_btLeft;
    protected ButtonsFactory.ToggleButton m_btCenter;
    protected ButtonsFactory.ToggleButton m_btRight;
    protected ButtonsFactory.ToggleButton m_btJustified;
    protected ParagraphPreview m_preview;
    private Font FONT = kz.tamur.rt.Utils.getDefaultFont();
    JButton btOK = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK);
    JButton btCancel = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
    public ParagraphDialog(Frame parent) {
        super(parent, "Paragraph", true);
        init();
    }

    public ParagraphDialog(Dialog parent) {
        super(parent, "Paragraph", true);
        init();
    }

    private void init() {
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS));
        JPanel p = new JPanel(new GridLayout(1, 2, 5, 2));
        JPanel ps = new JPanel(new GridLayout(3, 2, 10, 2));
        TitledBorder tlt = new TitledBorder(new EtchedBorder(), "Интервал");
        tlt.setTitleFont(FONT);
        ps.setBorder(tlt);
        JLabel linespace = new JLabel("Между строк:");
        linespace.setFont(FONT);
        ps.add(linespace);

        m_lineSpacing = new JTextField();
        m_lineSpacing.setFont(FONT);
        ps.add(m_lineSpacing);
        JLabel spaceAbove = new JLabel("Перед:");
        spaceAbove.setFont(FONT);
        ps.add(spaceAbove);
        m_spaceAbove = new JTextField();
        m_spaceAbove.setFont(FONT);
        ps.add(m_spaceAbove);

        JLabel spaceBelow = new JLabel("Space below:");
        spaceBelow.setFont(FONT);
        ps.add(spaceBelow);

        m_spaceBelow = new JTextField();
        m_spaceBelow.setFont(FONT);
        ps.add(m_spaceBelow);

        p.add(ps);
        JPanel pi = new JPanel(new GridLayout(3, 2, 10, 2));
        TitledBorder tlt2 = new TitledBorder(new EtchedBorder(), "Отступ");
        tlt2.setTitleFont(FONT);
        pi.setBorder(tlt2);
        JLabel firstIndent = new JLabel("Первая строка:");
        firstIndent.setFont(FONT);
        pi.add(firstIndent);

        m_firstIndent = new JTextField();
        m_firstIndent.setFont(FONT);
        pi.add(m_firstIndent);
        JLabel leftIndent=new JLabel("Слева:");
        leftIndent.setFont(FONT);
        pi.add(leftIndent);

        m_leftIndent = new JTextField();
        m_leftIndent.setFont(FONT);
        pi.add(m_leftIndent);
        JLabel rightIndent = new JLabel("Right indent:");
        rightIndent.setFont(FONT);
        pi.add(rightIndent);

        m_rightIndent = new JTextField();
        m_rightIndent.setFont(FONT);
        pi.add(m_rightIndent);
        p.add(pi);
        getContentPane().add(p);
        getContentPane().add(Box.createVerticalStrut(5));
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(10));
        JLabel align = new JLabel("Выравнивание:");
        align.setFont(FONT);
        p.add(align);

        p.add(Box.createHorizontalStrut(20));
        ButtonGroup bg = new ButtonGroup();
        m_btLeft = ButtonsFactory.createToggleButton(false, "pLeft", "По левому краю");
        bg.add(m_btLeft);
        p.add(m_btLeft);
        m_btCenter = ButtonsFactory.createToggleButton(false, "pCenter", "По центру");
        bg.add(m_btCenter);
        p.add(m_btCenter);
        m_btRight = ButtonsFactory.createToggleButton(false, "pRight", "По правому краю");
        bg.add(m_btRight);
        p.add(m_btRight);
        m_btJustified = ButtonsFactory.createToggleButton(false, "pJustify","По ширине");
        bg.add(m_btJustified);
        p.add(m_btJustified);
        getContentPane().add(p);
        p = new JPanel(new BorderLayout());
        TitledBorder tlt3 = new TitledBorder(new EtchedBorder(), "Образец");
        tlt3.setTitleFont(FONT);
        p.setBorder(tlt3);
        m_preview = new ParagraphPreview();
        p.add(m_preview, BorderLayout.CENTER);
        getContentPane().add(p);
        p = new JPanel(new FlowLayout());
        JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));

        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_option = JOptionPane.OK_OPTION;
                setVisible(false);
            }
        };
        btOK.addActionListener(lst);
        p1.add(btOK);

        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                m_option = JOptionPane.CANCEL_OPTION;
                setVisible(false);
            }
        };
        btCancel.addActionListener(lst);
        p1.add(btCancel);
        p.add(p1);
        getContentPane().add(p);
        pack();
        setResizable(false);
        FocusListener flst = new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                updatePreview();
            }
        };
        m_lineSpacing.addFocusListener(flst);
        m_spaceAbove.addFocusListener(flst);
        m_spaceBelow.addFocusListener(flst);
        m_firstIndent.addFocusListener(flst);
        m_leftIndent.addFocusListener(flst);
        m_rightIndent.addFocusListener(flst);
        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        };
        m_btLeft.addActionListener(lst);
        m_btCenter.addActionListener(lst);
        m_btRight.addActionListener(lst);
        m_btJustified.addActionListener(lst);
    }

    public void setAttributes(AttributeSet a) {
        m_attributes = new SimpleAttributeSet(a);
        m_lineSpacing.setText(Float.toString(StyleConstants.getLineSpacing(a)));
        m_spaceAbove.setText(Float.toString(StyleConstants.getSpaceAbove(a)));
        m_spaceBelow.setText(Float.toString(StyleConstants.getSpaceBelow(a)));
        m_firstIndent.setText(Float.toString(StyleConstants.getFirstLineIndent(a)));
        m_leftIndent.setText(Float.toString(StyleConstants.getLeftIndent(a)));
        m_rightIndent.setText(Float.toString(StyleConstants.getRightIndent(a)));
        int alignment = StyleConstants.getAlignment(a);
        if (alignment == StyleConstants.ALIGN_LEFT)
            m_btLeft.setSelected(true);
        else if (alignment == StyleConstants.ALIGN_CENTER)
            m_btCenter.setSelected(true);
        else if (alignment == StyleConstants.ALIGN_RIGHT)
            m_btRight.setSelected(true);
        else if (alignment == StyleConstants.ALIGN_JUSTIFIED)
            m_btJustified.setSelected(true);
        updatePreview();
    }

    public AttributeSet getAttributes() {
        if (m_attributes == null)
            return null;

        float value;
        try {
            value = Float.parseFloat(m_lineSpacing.getText());
            StyleConstants.setLineSpacing(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        try {
            value = Float.parseFloat(m_spaceAbove.getText());
            StyleConstants.setSpaceAbove(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        try {
            value = Float.parseFloat(m_spaceBelow.getText());
            StyleConstants.setSpaceBelow(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        try {
            value = Float.parseFloat(m_firstIndent.getText());
            StyleConstants.setFirstLineIndent(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        try {
            value = Float.parseFloat(m_leftIndent.getText());
            StyleConstants.setLeftIndent(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        try {
            value = Float.parseFloat(m_rightIndent.getText());
            StyleConstants.setRightIndent(m_attributes, value);
        } catch (NumberFormatException ex) {
        }
        StyleConstants.setAlignment(m_attributes, getAlignment());
        return m_attributes;
    }

    public int getOption() {
        return m_option;
    }

    protected void updatePreview() {
        m_preview.repaint();
    }

    protected int getAlignment() {
        if (m_btLeft.isSelected())
            return StyleConstants.ALIGN_LEFT;
        if (m_btCenter.isSelected())
            return StyleConstants.ALIGN_CENTER;
        else if (m_btRight.isSelected())
            return StyleConstants.ALIGN_RIGHT;
        else
            return StyleConstants.ALIGN_JUSTIFIED;
    }

    class ParagraphPreview extends JPanel {
        protected Font m_fn = new Font("Monospace", Font.PLAIN, 6);
        protected String m_dummy = "abcdefghjklm";
        private static final float m_scaleX = 0.25f;
        protected static final float m_scaleY = 0.25f;

        protected static final float MAX_SPACE = 1000f;

        protected SecureRandom m_random = new SecureRandom();

        public ParagraphPreview() {
            setBackground(Color.white);
            setForeground(Color.black);
            setOpaque(true);
            setBorder(new LineBorder(Color.black));
            setPreferredSize(new Dimension(120, 56));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            float lineSpacing = 0;
            float spaceAbove = 0;
            float spaceBelow = 0;
            float firstIndent = 0;
            float leftIndent = 0;
            float rightIndent = 0;
            
            String str = m_lineSpacing.getText();
            try {
            	if (str.matches(".+"))
            		lineSpacing = Funcs.checkFloat(Float.parseFloat(str), 0, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	lineSpacing = 0;
            }
            str = m_spaceAbove.getText();
            try {
            	if (str.matches(".+"))
            		spaceAbove = Funcs.checkFloat(Float.parseFloat(str), -10f, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	spaceAbove = 0;
            }
            str = m_spaceBelow.getText();
            try {
            	if (str.matches(".+"))
            		spaceBelow = Funcs.checkFloat(Float.parseFloat(str), -10f, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	spaceBelow = 0;
            }
            str = m_firstIndent.getText();
            try {
            	if (str.matches(".+"))
            		firstIndent = Funcs.checkFloat(Float.parseFloat(str), -10f, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	firstIndent = 0;
            }
            str = m_leftIndent.getText();
            try {
            	if (str.matches(".+"))
            		leftIndent = Funcs.checkFloat(Float.parseFloat(str), -10f, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	leftIndent = 0;
            }
            str = m_rightIndent.getText();
            try {
            	if (str.matches(".+"))
            		rightIndent = Funcs.checkFloat(Float.parseFloat(str), -10f, MAX_SPACE);
            } catch (NumberFormatException ex) {
            	rightIndent = 0;
            }
            g.setFont(m_fn);
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getAscent();
            int s = Math.max((int) (lineSpacing * m_scaleY), 1);
            int s1 = Math.max((int) (spaceAbove * m_scaleY), 0) + s;
            int s2 = Math.max((int) (spaceBelow * m_scaleY), 0) + s;
            int y = 5 + h;
            int xMarg = 20;
            int x0 = Math.max((int) (firstIndent * m_scaleX) + xMarg, 3);
            int x1 = Math.max((int) (leftIndent * m_scaleX) + xMarg, 3);
            int x2 = Math.max((int) (rightIndent * m_scaleX) + xMarg, 3);
            int xm0 = getWidth() - xMarg;
            int xm1 = getWidth() - x2;
            int n = (getHeight() - (2 * h + s1 + s2 - s + 10)) / (h + s);
            n = Math.max(n, 1);
            g.setColor(Color.lightGray);
            int x = xMarg;
            drawLine(g, x, y, xm0, xm0, fm, StyleConstants.ALIGN_LEFT);
            y += h + s1;
            g.setColor(Color.gray);
            int alignment = getAlignment();

            if (n < Constants.MAX_ELEMENTS_COUNT_4) {
	            for (int k = 0; k < n && k < Constants.MAX_ELEMENTS_COUNT_4; k++) {
	                x = (k == 0 ? x0 : x1);
	                int xLen = (k == n - 1 ? xm1 / 2 : xm1);
	                if (k == n - 1 && alignment == StyleConstants.ALIGN_JUSTIFIED)
	                    alignment = StyleConstants.ALIGN_LEFT;
	                drawLine(g, x, y, xm1, xLen, fm, alignment);
	                y += h + s;
	            }
            }
            y += s2 - s;
            x = xMarg;
            g.setColor(Color.lightGray);
            drawLine(g, x, y, xm0, xm0, fm, StyleConstants.ALIGN_LEFT);
        }

        protected void drawLine(Graphics g, int x, int y, int xMax,
                                int xLen, FontMetrics fm, int alignment) {
            if (y > getHeight() - 3)
                return;
            
            StringBuffer s = new StringBuffer();
            String str1;
            int xx = x;
            while (true) {
                int m = m_random.nextInt(10) + 1;
                str1 = m_dummy.substring(0, m) + " ";
                int len = fm.stringWidth(str1);
                if (xx + len >= xLen)
                    break;
                xx += len;
                s.append(str1);
            }
            String str = s.toString();
            switch (alignment) {
                case StyleConstants.ALIGN_LEFT:
                    g.drawString(str, x, y);
                    break;
                case StyleConstants.ALIGN_CENTER:
                    xx = (xMax + x - fm.stringWidth(str)) / 2;
                    g.drawString(str, xx, y);
                    break;
                case StyleConstants.ALIGN_RIGHT:
                    xx = xMax - fm.stringWidth(str);
                    g.drawString(str, xx, y);
                    break;
                case StyleConstants.ALIGN_JUSTIFIED:
                    int i = 0;
                    while (i++ < 10000) {
                    	if (x + fm.stringWidth(str) < xMax)
                    		str += "a";
                    	else
                    		break;
                    }
                    g.drawString(str, x, y);
                    break;
            }
        }
    }

}
