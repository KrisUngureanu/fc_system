package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeTableAdapter2;
import kz.tamur.comps.OrGuiComponent;

import java.util.ResourceBundle;

public interface OrTreeComponent2 extends OrGuiComponent, TreeComponent {
	//void setValue(Object value);
        //Object getValue();
    void changeTitles(ResourceBundle res);

    TreeTableAdapter2 getTableAdapter();
    
	void setAdapter(TreeAdapter2 treeAdapter);
}