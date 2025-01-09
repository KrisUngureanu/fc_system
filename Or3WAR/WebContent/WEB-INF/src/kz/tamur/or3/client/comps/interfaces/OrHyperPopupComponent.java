package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

import javax.swing.table.TableCellRenderer;

public interface OrHyperPopupComponent extends OrGuiComponent {
	// TODO Перенести в OrGuiComponent после перевода всех
	// компонентов на новый вариант
        //Object getValue();
    TableCellRenderer getCellRenderer();

    void setValue(Object value);
}