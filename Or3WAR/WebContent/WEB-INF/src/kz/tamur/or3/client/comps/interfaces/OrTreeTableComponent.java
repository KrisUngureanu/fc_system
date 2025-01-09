package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.TreeAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 11.06.2007
 * Time: 17:21:44
 * To change this template use File | Settings | File Templates.
 */
public interface OrTreeTableComponent extends OrTableComponent {
    OrTreeComponent getTree();
    TreeAdapter getTreeAdapter();
}
