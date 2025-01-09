package com.cifs.or2.client.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Title: OR2
 * Description:
 * Copyright: Copyright (c) 2001
 * Company: CIFS
 * 
 * @author
 * @version 1.0
 */

public class OrKazakhAdapter extends KeyAdapter {

    private KeyEvent e1;

    public OrKazakhAdapter() {
    }

    public void keyTyped(KeyEvent e) {
        if ((int) e.getKeyChar() == 63) {
            e.setKeyChar(e1.getKeyChar());
        }
    }

    public void keyPressed(KeyEvent e) {
        if ((int) e.getKeyChar() == 63) {
            e1 = e;
            final boolean isShiftDown = e.isShiftDown();
            switch (e.getKeyCode()) {
            case 47:
                e1.setKeyChar(e.getKeyChar());
                break;
            case 50: // digit 2
                e1.setKeyChar(isShiftDown ? '\u04D8' : '\u04D9');
                break;
            case 52:
                e1.setKeyChar(isShiftDown ? '\u04A2' : '\u04A3');
                break;
            case 53:
                e1.setKeyChar(isShiftDown ? '\u0492' : '\u0493');
                break;
            case 56:
                e1.setKeyChar(isShiftDown ? '\u04AE' : '\u04AF');
                break;
            case 57:
                e1.setKeyChar(isShiftDown ? '\u04B0' : '\u04B1');
                break;
            case 48:
                e1.setKeyChar(isShiftDown ? '\u049A' : '\u049B');
                break;
            case 45:
                e1.setKeyChar(isShiftDown ? '\u04E8' : '\u04E9');
                break;
            case 61:
                e1.setKeyChar(isShiftDown ? '\u04BA' : '\u04BB');
                break;

            }
        }
    }
}