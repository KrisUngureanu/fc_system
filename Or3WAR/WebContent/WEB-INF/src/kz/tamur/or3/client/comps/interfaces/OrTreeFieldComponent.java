package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 15.02.2007
 * Time: 14:26:12
 * To change this template use File | Settings | File Templates.
 */
public interface OrTreeFieldComponent extends OrGuiComponent {
    OrTreeComponent getOrTree();

    void setText(String text);

    TreeComponent getTree();

    boolean isTitleMode();
}
