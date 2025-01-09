package com.cifs.or2.client.gui;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * Created by IntelliJ IDEA.
 * User: kazakbala
 * Date: 04.03.2004
 * Time: 15:36:17
 * To change this template use Options | File Templates.
 */
public interface OrGuiFocusListener extends FocusListener {

    public void focusGained(FocusEvent e);
    public void focusLost(FocusEvent e);
}
