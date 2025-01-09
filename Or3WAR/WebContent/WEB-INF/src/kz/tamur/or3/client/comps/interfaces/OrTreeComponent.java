package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.comps.OrGuiComponent;

import java.util.ResourceBundle;

public interface OrTreeComponent extends OrGuiComponent, TreeComponent {
	// TODO Перенести в OrGuiComponent после перевода всех
	// компонентов на новый вариант
	//void setValue(Object value);
        //Object getValue();
    void changeTitles(ResourceBundle res);

    TreeTableAdapter getTableAdapter();
}