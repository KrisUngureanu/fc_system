package com.cifs.or2.client.gui;

import javax.swing.JMenuItem;

import com.cifs.or2.client.*;

public class FilterMenuItem extends JMenuItem {
    public Filter filter;

    public FilterMenuItem(Filter filter) {
        super(filter.title);
        this.filter = filter;
    }
}
