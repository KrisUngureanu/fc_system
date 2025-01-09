package kz.tamur.comps.ui.button;

import static kz.tamur.comps.Constants.DEFAULT_CURSOR;
import static kz.tamur.comps.Constants.HAND_CURSOR;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;

import kz.tamur.rt.Utils;

/**
 * Класс реализует прозрачную кнопку с заданным размером 24px.
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrTransparentButton extends JButton {
    private Color light;
    private Color back;
    protected String webNameIcon = null;
    protected String title;
    /**
     * Создание нового экземпляра кнопки
     * 
     * @param a
     *            Action
     */
    public OrTransparentButton(Action a) {
        super(a);
        init();
    }

    /**
     * Создание нового экземпляра кнопки
     */
    public OrTransparentButton() {
        super();
        init();
    }

    public OrTransparentButton(String title) {
        super(title);
        init();
    }

    public OrTransparentButton(String title, Color light, Color back) {
        super(title);
        this.light = light;
        this.back = back;
        init();
    }

    /**
     * Инициализация атрибутов кнопки
     */
    private void init() {
        setFont(kz.tamur.rt.Utils.getDefaultFont());
        setOpaque(false);

        if (back == null) {
            addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    setCursor(DEFAULT_CURSOR);
                }

                public void mouseEntered(MouseEvent e) {
                    setCursor(HAND_CURSOR);
                }

            });
        }else {
            addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    if (!isSelected()) {
                        setForeground(back);
                    }
                    setCursor(DEFAULT_CURSOR);
                }

                public void mouseEntered(MouseEvent e) {
                    setForeground(light == null ? Color.BLUE : light);
                    setCursor(HAND_CURSOR);
                }
            });
        }
    }
 
    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        if (isSelected()) {
            setForeground(light == null ? Color.BLUE : light);
        }else {
            setForeground(back);
        }
    }
    
    @Override
    public void setText(String text) {
        super.setText(Utils.castToHTML(text, this));
    }
    
    public void setTextSuperClass(String text) {
        super.setText(text);
    }
    
    public String getWebNameIcon() {
        return webNameIcon;
    }

    /**
     * @param webNameIcon the webNameIcon to set
     */
    public void setWebNameIcon(String webNameIcon) {
        this.webNameIcon = webNameIcon;
    }
    @Override
    public void setCursor(Cursor cursor) {
        if(isEnabled()) {
            super.setCursor(cursor);
        }
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    public void setTransparent(boolean transparent) {
        ButtonUI ui = getUI();
        if (ui instanceof OrButtonUI) {
            ((OrButtonUI)ui).setTransparent(transparent);
        }
    }
}
