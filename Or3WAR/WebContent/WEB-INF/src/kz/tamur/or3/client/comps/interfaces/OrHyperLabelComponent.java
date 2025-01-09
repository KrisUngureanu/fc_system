package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

import javax.swing.table.TableCellRenderer;

public interface OrHyperLabelComponent extends OrGuiComponent {
	// TODO Перенести в OrGuiComponent после перевода всех
	// компонентов на новый вариант
	//void setValue(Object value);
        //Object getValue();
    boolean isBlockErrors();

    TableCellRenderer getCellRenderer();
    boolean isArchiv();
    void setValue(Object value);
}