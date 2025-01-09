package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

import javax.swing.*;

public interface OrComboBoxComponent extends OrGuiComponent {
	// TODO Перенести в OrGuiComponent после перевода всех
	// компонентов на новый вариант
	//void setValue(Object value);
        //Object getValue();
    void removeAllItems();
    void setModel(ComboBoxModel aModel);
    void setSelectedItem(Object anObject);
    void setSelectedIndex(int anIndex);
    int getItemCount();
    Object getItemAt(int index);
    Object getSelectedItem();

    int getSelectedIndex();
    void setValue(Object value);
	int getAppearance();
}
