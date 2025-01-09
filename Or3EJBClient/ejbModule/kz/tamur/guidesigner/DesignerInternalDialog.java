package kz.tamur.guidesigner;


import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.service.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.05.2004
 * Time: 9:52:16
 * To change this template use File | Settings | File Templates.
 */
public class DesignerInternalDialog extends JPanel {

    public static final int BUTTON_FLOAT = 0;
    public static final int BUTTON_PIN = 1;
    public static final int BUTTON_HIDE = 2;

    public static final int INSPECTOR_DLG = 0;
    public static final int TREE_DLG = 1;

    private JLabel titleLab;
    private String title_;
    private ImageIcon icon_;

    private int dlgType;

    private JPanel titlePanel = new JPanel(new BorderLayout());
    private JPanel contentPanel = new JPanel(new BorderLayout());
    private JPanel buttonsPanel = new JPanel();

    private Component content;
    private Container topLevelAncestor;


    public DesignerInternalDialog(String title, ImageIcon icon,
                                  Container topLevelAncestor) {
        super();
        title_ = title;
        icon_ = icon;
        setSize(200, 200);
        contentPanel.setSize(200, 200);
        this.topLevelAncestor = topLevelAncestor;
        initTitleBar();
        this.setLayout(new BorderLayout());
        this.add(titlePanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
    }

    private void initTitle() {
        if (icon_ != null) {
            titleLab = new JLabel(icon_);
        } else {
            titleLab = new JLabel();
        }
        if (title_ != null) {
            titleLab.setText(title_);
        }
        titleLab.setOpaque(false);
        titleLab.setFont(new Font("Tahoma", Font.BOLD, 11));
        titleLab.setForeground(Color.white);
    }

    private void initTitleBar() {
        initTitle();
        initButtonsPanel();
        titlePanel.setPreferredSize(new Dimension(20, 18));
        titlePanel.setBackground(new Color(128, 145, 173));
        titleLab.setHorizontalAlignment(SwingConstants.LEFT);
        titlePanel.add(titleLab, BorderLayout.CENTER);
        if (topLevelAncestor instanceof DesignerFrame  || topLevelAncestor instanceof MainFrame) {
            titlePanel.add(buttonsPanel, BorderLayout.EAST);
        }
    }

    private void initButtonsPanel() {
        if (topLevelAncestor instanceof DesignerFrame || topLevelAncestor instanceof MainFrame) {
            buttonsPanel.setOpaque(false);
            buttonsPanel.setBorder(null);
            ((FlowLayout) buttonsPanel.getLayout()).setHgap(1);
            ((FlowLayout) buttonsPanel.getLayout()).setVgap(1);
            TitleButton floatBtn = new TitleButton(BUTTON_FLOAT);
            //TitleButton pinBtn = new TitleButton(BUTTON_PIN);
            TitleButton hideBtn = new TitleButton(BUTTON_HIDE);
            buttonsPanel.add(floatBtn);
            //buttonsPanel.add(pinBtn);
            buttonsPanel.add(hideBtn);
        }
    }


    public void addContent(Component component) {
        content = component;
        contentPanel.add(content, BorderLayout.CENTER);
    }

    public Component getContent() {
        return content;
    }

    class TitleButton extends JButton implements ActionListener {

        int type_;

        public TitleButton(int type) {
            super();
            type_ = type;
            init();
        }

        private void init() {
            setMargin(Constants.INSETS_0);
            setPreferredSize(Constants.BTN_EDITOR_SIZE);
            setBorder(null);
            setContentAreaFilled(false);
            switch(type_) {
                case BUTTON_FLOAT:
                    setIcon(kz.tamur.rt.Utils.getImageIcon("FloatMode"));
                    setToolTipText("Плавающий режим");
                    addActionListener(this);
                    break;
                case BUTTON_PIN:
                    setIcon(kz.tamur.rt.Utils.getImageIcon("UnpinMode"));
                    setToolTipText("Зафиксировать");
                    break;
                case BUTTON_HIDE:
                    setIcon(kz.tamur.rt.Utils.getImageIcon("HideMode"));
                    setToolTipText("Скрыть");
                    addActionListener(this);
                    break;
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (type_ == BUTTON_FLOAT) {
                if (topLevelAncestor instanceof DesignerFrame) {
                    if (dlgType == INSPECTOR_DLG) {
                        ((DesignerFrame)topLevelAncestor).setInspector(false);
                    } else if (dlgType == TREE_DLG) {
                        ((DesignerFrame)topLevelAncestor).setCompsTreeMode(true);
                    }
                }
            } else if (type_ == BUTTON_HIDE) {
                if (topLevelAncestor instanceof DesignerFrame) {
                    ((DesignerFrame)topLevelAncestor).hideInternalDialog(dlgType);
                }
            }

        }
    }

    public void setTitle(String title) {
        title_ = title;
        titleLab.setText(title_);
    }

    private Container getDesignerFrame(Container c) {
        if (!(c instanceof DesignerFrame)) {
            Container cont = c.getParent();
            if (cont != null) {
                if (cont instanceof DesignerFrame) {
                    return cont;
                } else {
                    return getDesignerFrame(cont.getParent());
                }
            } else {
                return null;
            }
        } else {
            return c;
        }
    }

    public void setDlgType(int type) {
        dlgType = type;
    }
}
