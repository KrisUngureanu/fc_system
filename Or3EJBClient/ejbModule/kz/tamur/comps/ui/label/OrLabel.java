package kz.tamur.comps.ui.label;

import javax.swing.*;
import java.awt.*;

import kz.tamur.comps.ui.ext.Painter;

/**
 * 
 * @author Sergey Lebedev
 *
 */
public class OrLabel extends JLabel {
    public OrLabel() {
        super();
    }

    public OrLabel(Insets margin) {
        super();
        setMargin(margin);
    }

    public OrLabel(Icon image) {
        super(image);
    }

    public OrLabel(Icon image, Insets margin) {
        super(image);
        setMargin(margin);
    }

    public OrLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public OrLabel(Icon image, int horizontalAlignment, Insets margin) {
        super(image, horizontalAlignment);
        setMargin(margin);
    }

    public OrLabel(String text) {
        super(text);
    }

    public OrLabel(String text, Insets margin) {
        super(text);
        setMargin(margin);
    }

    public OrLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public OrLabel(String text, int horizontalAlignment, Insets margin) {
        super(text, horizontalAlignment);
        setMargin(margin);
    }

    public OrLabel(String text, Icon icon) {
        super(text, icon, LEADING);
    }

    public OrLabel(String text, Icon icon, Insets margin) {
        super(text, icon, LEADING);
        setMargin(margin);
    }

    public OrLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public OrLabel(String text, Icon icon, int horizontalAlignment, Insets margin) {
        super(text, icon, horizontalAlignment);
        setMargin(margin);
    }

    public Insets getMargin() {
        return getWebUI().getMargin();
    }

    public void setMargin(Insets margin) {
        getWebUI().setMargin(margin);
    }

    public void setMargin(int top, int left, int bottom, int right) {
        setMargin(new Insets(top, left, bottom, right));
    }

    public void setMargin(int spacing) {
        setMargin(spacing, spacing, spacing, spacing);
    }

    public Painter getPainter() {
        return getWebUI().getPainter();
    }

    public void setPainter(Painter painter) {
        getWebUI().setPainter(painter);
    }

    public boolean isDrawShade() {
        return getWebUI().isDrawShade();
    }

    public void setDrawShade(boolean drawShade) {
        getWebUI().setDrawShade(drawShade);
    }

    public Color getShadeColor() {
        return getWebUI().getShadeColor();
    }

    public void setShadeColor(Color shadeColor) {
        getWebUI().setShadeColor(shadeColor);
    }

    public OrLabelUI getWebUI() {
        return (OrLabelUI) getUI();
    }

}
