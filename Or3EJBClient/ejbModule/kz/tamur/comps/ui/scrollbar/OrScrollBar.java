package kz.tamur.comps.ui.scrollbar;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrScrollBar extends JScrollBar {
    public OrScrollBar() {
        super();
    }

    public OrScrollBar(int orientation) {
        super(orientation);
    }

    public OrScrollBar(int orientation, int value, int extent, int min, int max) {
        super(orientation, value, extent, min, max);
    }

    public boolean isDrawBorder() {
        return getWebUI().isDrawBorder();
    }

    public void setDrawBorder(boolean drawBorder) {
        getWebUI().setDrawBorder(drawBorder);
    }

    public int getRound() {
        return getWebUI().getRound();
    }

    public void setRound(int rounding) {
        getWebUI().setRound(rounding);
    }

    public Color getScrollBg() {
        return getWebUI().getScrollBg();
    }

    public void setScrollBg(Color scrollBg) {
        getWebUI().setScrollBg(scrollBg);
    }

    public Color getScrollBorder() {
        return getWebUI().getScrollBorder();
    }

    public void setScrollBorder(Color scrollBorder) {
        getWebUI().setScrollBorder(scrollBorder);
    }

    public Color getScrollBarBorder() {
        return getWebUI().getScrollBarBorder();
    }

    public void setScrollBarBorder(Color scrollBarBorder) {
        getWebUI().setScrollBarBorder(scrollBarBorder);
    }

    public Color getScrollGradientLeft() {
        return getWebUI().getScrollGradientLeft();
    }

    public void setScrollGradientLeft(Color scrollGradientLeft) {
        getWebUI().setScrollGradientLeft(scrollGradientLeft);
    }

    public Color getScrollGradientRight() {
        return getWebUI().getScrollGradientRight();
    }

    public void setScrollGradientRight(Color scrollGradientRight) {
        getWebUI().setScrollGradientRight(scrollGradientRight);
    }

    public Color getScrollSelGradientLeft() {
        return getWebUI().getScrollSelGradientLeft();
    }

    public void setScrollSelGradientLeft(Color scrollSelGradientLeft) {
        getWebUI().setScrollSelGradientLeft(scrollSelGradientLeft);
    }

    public Color getScrollSelGradientRight() {
        return getWebUI().getScrollSelGradientRight();
    }

    public void setScrollSelGradientRight(Color scrollSelGradientRight) {
        getWebUI().setScrollSelGradientRight(scrollSelGradientRight);
    }

    public OrScrollBarUI getWebUI() {
        return (OrScrollBarUI) getUI();
    }

}
