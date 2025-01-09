package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.TreeAdapter2;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 11.06.2007
 * Time: 17:21:44
 * To change this template use File | Settings | File Templates.
 */
public interface OrTreeTableComponent2 extends OrTableComponent {
    OrTreeComponent2 getTree();
    TreeAdapter2 getTreeAdapter();
}
