package kz.tamur.util.editor;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.util.Funcs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.AttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 24.06.2005
 * Time: 18:06:26
 * To change this template use File | Settings | File Templates.
 */
public class FontDialog extends JDialog {
    protected int m_option = JOptionPane.CLOSED_OPTION;
    protected OpenList m_lstFontName;
    protected OpenList m_lstFontSize;
    protected MutableAttributeSet m_attributes;
    protected JCheckBox m_chkBold;
    protected JCheckBox m_chkItalic;
    protected JCheckBox m_chkUnderline;
    protected JCheckBox m_chkStrikethrough;
    protected JCheckBox m_chkSubscript;
    protected JCheckBox m_chkSuperscript;
    protected JComboBox m_cbColor;
    protected JLabel m_preview;
    private String[] names;
    private String[] sizes;
    private Window parent;
    private Font FONT = kz.tamur.rt.Utils.getDefaultFont();
    public FontDialog(Frame parent,
                      String[] names, String[] sizes) {
        super(parent, "Шрифты", true);
        this.names = names;
        this.sizes = sizes;
        this.parent = parent;
        init();
    }

    public FontDialog(Dialog parent,
                      String[] names, String[] sizes) {
        super(parent, "Шрифты", true);
        this.names = names;
        this.sizes = sizes;
        this.parent = parent;
        init();
    }
    private void init() {
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS));
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
        TitledBorder tlt = new TitledBorder(new EtchedBorder(), "Щрифты");
        tlt.setTitleFont(FONT);
        p.setBorder(tlt);
        m_lstFontName = new OpenList(names, "Шрифт:");
        m_lstFontName.setFont(FONT);
        p.add(m_lstFontName);
        m_lstFontSize = new OpenList(sizes, "Размер:");
        m_lstFontSize.setFont(FONT);
        p.add(m_lstFontSize);
        getContentPane().add(p);
        p = new JPanel(new GridLayout(2, 3, 10, 5));
        tlt = new TitledBorder(new EtchedBorder(), "Начертание");
        tlt.setTitleFont(FONT);
        p.setBorder(tlt);
        m_chkBold = new JCheckBox("Полужирный");
        m_chkBold.setFont(FONT);
        p.add(m_chkBold);
        m_chkItalic = new JCheckBox("Курсив");
        m_chkItalic.setFont(FONT);
        p.add(m_chkItalic);
        m_chkUnderline = new JCheckBox("Подчеркнутый");
        m_chkUnderline.setFont(FONT);
        p.add(m_chkUnderline);
        m_chkStrikethrough = new JCheckBox("Зачеркнутый");
        m_chkStrikethrough.setFont(FONT);
        p.add(m_chkStrikethrough);
        m_chkSubscript = new JCheckBox("Подстрочный");
        m_chkSubscript.setFont(FONT);
        p.add(m_chkSubscript);
        m_chkSuperscript = new JCheckBox("Надстрочный");
        m_chkSuperscript.setFont(FONT);
        p.add(m_chkSuperscript);
        getContentPane().add(p);
        getContentPane().add(Box.createVerticalStrut(5));
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(10));
        JLabel lb = new JLabel("Цвет шрифта:");
        lb.setFont(FONT);
        p.add(lb);
        p.add(Box.createHorizontalStrut(20));
        m_cbColor = new JComboBox();
        int[] values = new int[]{0, 128, 192, 255};
        for (int r = 0; r < values.length; r++) {
            for (int g = 0; g < values.length; g++) {
                for (int b = 0; b < values.length; b++) {
                    Color c = new Color(values[r], values[g], values[b]);
                    m_cbColor.addItem(c);
                }
            }
        }
        m_cbColor.setRenderer(new ColorComboRenderer());
        p.add(m_cbColor);
        p.add(Box.createHorizontalStrut(10));
        getContentPane().add(p);
        p = new JPanel(new BorderLayout());
        tlt = new TitledBorder(new EtchedBorder(), "Образец");
        tlt.setTitleFont(FONT);
        p.setBorder(tlt);
        m_preview = new JLabel("Абвг дежз", JLabel.CENTER);
        m_preview.setBackground(Color.white);
        m_preview.setForeground(Color.black);
        m_preview.setOpaque(true);
        m_preview.setBorder(new LineBorder(Color.black));
        m_preview.setPreferredSize(new Dimension(120, 40));
        p.add(m_preview, BorderLayout.CENTER);
        getContentPane().add(p);
        p = new JPanel(new FlowLayout());
        JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
        JButton btOK = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK);
        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_option = JOptionPane.OK_OPTION;
                setVisible(false);
            }
        };
        btOK.addActionListener(lst);
        p1.add(btOK);
        JButton btCancel = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
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
        Dimension d1 = getSize();
        Dimension d2 = parent.getSize();
        int x = Math.max((d2.width - d1.width) / 2, 0);
        int y = Math.max((d2.height - d1.height) / 2, 0);
        setBounds(x, y, d1.width, d1.height);
        ListSelectionListener lsel = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updatePreview();
            }
        };
        m_lstFontName.addListSelectionListener(lsel);
        m_lstFontSize.addListSelectionListener(lsel);
        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        };
        m_chkBold.addActionListener(lst);
        m_chkItalic.addActionListener(lst);
        m_cbColor.addActionListener(lst);
    }

    public void setAttributes(AttributeSet a) {
        m_attributes = new SimpleAttributeSet(a);
        String name = StyleConstants.getFontFamily(a);
        m_lstFontName.setSelected(name);
        int size = StyleConstants.getFontSize(a);
        m_lstFontSize.setSelectedInt(size);
        m_chkBold.setSelected(StyleConstants.isBold(a));
        m_chkItalic.setSelected(StyleConstants.isItalic(a));
        m_chkUnderline.setSelected(StyleConstants.isUnderline(a));
        m_chkStrikethrough.setSelected(StyleConstants.isStrikeThrough(a));
        m_chkSubscript.setSelected(StyleConstants.isSubscript(a));
        m_chkSuperscript.setSelected(StyleConstants.isSuperscript(a));
        m_cbColor.setSelectedItem(StyleConstants.getForeground(a));
        updatePreview();
    }

    public AttributeSet getAttributes() {
        if (m_attributes == null)
            return null;
        StyleConstants.setFontFamily(m_attributes,
                m_lstFontName.getSelected());
        StyleConstants.setFontSize(m_attributes,
                m_lstFontSize.getSelectedInt());
        StyleConstants.setBold(m_attributes,
                m_chkBold.isSelected());
        StyleConstants.setItalic(m_attributes,
                m_chkItalic.isSelected());
        StyleConstants.setUnderline(m_attributes,
                m_chkUnderline.isSelected());
        StyleConstants.setStrikeThrough(m_attributes,
                m_chkStrikethrough.isSelected());
        StyleConstants.setSubscript(m_attributes,
                m_chkSubscript.isSelected());
        StyleConstants.setSuperscript(m_attributes,
                m_chkSuperscript.isSelected());
        StyleConstants.setForeground(m_attributes,
                (Color) m_cbColor.getSelectedItem());
        return m_attributes;
    }

    public int getOption() {
        return m_option;
    }

    protected void updatePreview() {
        String name = m_lstFontName.getSelected();
        int size = m_lstFontSize.getSelectedInt();
        if (size <= 0)
            return;
        int style = Font.PLAIN;
        if (m_chkBold.isSelected())
            style |= Font.BOLD;
        if (m_chkItalic.isSelected())
            style |= Font.ITALIC;
// Bug Alert! This doesn’t work if only style is changed.
        Font fn = new Font(name, style, size);
        m_preview.setFont(fn);
        Color c = (Color) m_cbColor.getSelectedItem();
        m_preview.setForeground(c);
        m_preview.repaint();
    }
}

class OpenList extends JPanel
        implements ListSelectionListener, ActionListener {
    protected JLabel m_title;
    protected JTextField m_text;
    protected JList m_list;
    protected JScrollPane m_scroll;
    private Font FONT = kz.tamur.rt.Utils.getDefaultFont();
    public OpenList(String[] data, String title) {
        setLayout(null);
        m_title = new JLabel(title, JLabel.LEFT);
        m_title.setFont(FONT);
        add(m_title);
        m_text = new JTextField();
        m_text.addActionListener(this);
        add(m_text);
        m_list = new JList(data);
        m_list.setVisibleRowCount(4);
        m_list.addListSelectionListener(this);
        m_list.setFont(FONT);
        m_scroll = new JScrollPane(m_list);
        add(m_scroll);
    }

    public void setSelected(String sel) {
        m_list.setSelectedValue(sel, true);
        m_text.setText(sel);
    }

    public String getSelected() {
        return Funcs.sanitizeHtml(m_text.getText());
    }

    public void setSelectedInt(int value) {
        setSelected(Integer.toString(value));
    }

    public int getSelectedInt() {
        try {
            return Integer.parseInt(getSelected());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        Object obj = m_list.getSelectedValue();
        if (obj != null)
            m_text.setText(obj.toString());
    }

    public void actionPerformed(ActionEvent e) {
        ListModel model = m_list.getModel();
        String key = m_text.getText().toLowerCase(Constants.OK);
        for (int k = 0; k < model.getSize(); k++) {
            String data = (String) model.getElementAt(k);
            if (data.toLowerCase(Constants.OK).startsWith(key)) {
                m_list.setSelectedValue(data, true);
                break;
            }
        }
    }

    public void addListSelectionListener(ListSelectionListener lst) {
        m_list.addListSelectionListener(lst);
    }

    public Dimension getPreferredSize() {
        Insets ins = getInsets();
        Dimension d1 = m_title.getPreferredSize();
        Dimension d2 = m_text.getPreferredSize();
        Dimension d3 = m_scroll.getPreferredSize();
        int w = Math.max(Math.max(d1.width, d2.width), d3.width);
        int h = d1.height + d2.height + d3.height;
        return new Dimension(w + ins.left + ins.right,
                h + ins.top + ins.bottom);
    }

    public Dimension getMaximumSize() {
        Insets ins = getInsets();
        Dimension d1 = m_title.getMaximumSize();
        Dimension d2 = m_text.getMaximumSize();
        Dimension d3 = m_scroll.getMaximumSize();
        int w = Math.max(Math.max(d1.width, d2.width), d3.width);
        int h = d1.height + d2.height + d3.height;
        return new Dimension(w + ins.left + ins.right,
                h + ins.top + ins.bottom);
    }

    public Dimension getMinimumSize() {
        Insets ins = getInsets();
        Dimension d1 = m_title.getMinimumSize();
        Dimension d2 = m_text.getMinimumSize();
        Dimension d3 = m_scroll.getMinimumSize();
        int w = Math.max(Math.max(d1.width, d2.width), d3.width);
        int h = d1.height + d2.height + d3.height;
        return new Dimension(w + ins.left + ins.right,
                h + ins.top + ins.bottom);
    }

    public void doLayout() {
        Insets ins = getInsets();
        Dimension d = getSize();
        int x = ins.left;
        int y = ins.top;
        int w = d.width - ins.left - ins.right;
        int h = d.height - ins.top - ins.bottom;
        Dimension d1 = m_title.getPreferredSize();
        m_title.setBounds(x, y, w, d1.height);
        y += d1.height;
        Dimension d2 = m_text.getPreferredSize();
        m_text.setBounds(x, y, w, d2.height);
        y += d2.height;
        m_scroll.setBounds(x, y, w, h - y);
    }
}

class ColorComboRenderer extends JPanel implements ListCellRenderer {
    protected Color m_color = Color.black;
    protected Color m_focusColor =
            (Color) UIManager.get("List.selectionBackground");
    protected Color m_nonFocusColor = Color.white;

    public Component getListCellRendererComponent(JList list,
                                                  Object obj, int row, boolean sel, boolean hasFocus) {
        if (hasFocus || sel)
            setBorder(new CompoundBorder(new MatteBorder(2, 10, 2, 10, m_focusColor),
                    new LineBorder(Color.black)));
        else
            setBorder(new CompoundBorder(new MatteBorder(2, 10, 2, 10, m_nonFocusColor),
                    new LineBorder(Color.black)));
        if (obj instanceof Color)
            m_color = (Color) obj;
        return this;
    }

    public void paintComponent(Graphics g) {
        setBackground(m_color);
        super.paintComponent(g);
    }
}