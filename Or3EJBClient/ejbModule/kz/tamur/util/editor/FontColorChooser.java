package kz.tamur.util.editor;

import kz.tamur.util.colorchooser.OrColorChooser;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 28.06.2005
 * Time: 11:22:44
 * To change this template use File | Settings | File Templates.
 */
public class FontColorChooser extends JDialog {
    private OrColorChooser colors;
    public FontColorChooser(Frame parent, Color color) {
        super(parent, "Цвет шрифта",true);
        colors = new OrColorChooser(color);
        getContentPane().add(colors);
        pack();
    }

    public FontColorChooser(Dialog parent, Color color) {
        super(parent, "Цвет шрифта",true);
        colors = new OrColorChooser(color);
        getContentPane().add(colors);
        pack();
    }

    public Color getColor() {
        return colors.getColor();
    }
}
