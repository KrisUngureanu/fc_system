package kz.tamur.guidesigner.hypers;

import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.or3.client.props.*;

public class ServiceNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private ProcessMenuTabbedPane owner;

    public ServiceNodeItem(Object item, ProcessMenuTabbedPane owner) {
        this.item = item;
        this.owner = owner;
    }

    public Property getProperties() {
        proot = new FolderProperty(null, null, "Элементы");
        if (item != null && item instanceof ServiceNode) {
            new StringProperty(proot, "index", "Индекс");
            if (!((ServiceNode) item).isLeaf()) {
                new CheckProperty(proot, "tabbed", "Закладка");
                new StringProperty(proot, "title_t", "Имя закладки");
                new StringProperty(proot, "title_t_kz", "Имя закладки на каз. языке");
            }
            new StringProperty(proot, "title", "Наименование");
            new StringProperty(proot, "title_kz", "Наименование на каз. языке");
            if (((ServiceNode) item).isLeaf()) {
                new CheckProperty(proot, "isBtnToolBarProp", "Кнопка на панели инструментов");
                new StringProperty(proot, "hotKeyProp", "Горячие клавиши");
                new ImageProperty(proot, "iconProp", "Иконка");
            }
        }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res = "";
        if (item != null && !(prop instanceof FolderProperty) && item instanceof ServiceNode) {
            final String id = prop.getId();
            if ("index".equals(id)) {
                res = "" + ((ServiceNode) item).getRuntimeIndex();
            } else if ("tabbed".equals(id)) {
                res = ((ServiceNode) item).isTab();
            } else if ("title_t".equals(id)) {
                res = ((ServiceNode) item).getTabName();
            } else if ("title_t_kz".equals(id)) {
                res = ((ServiceNode) item).getTabNameKz();
            } else if ("title".equals(id)) {
                res = ((ServiceNode) item).getTitle();
            } else if ("title_kz".equals(id)) {
                res = ((ServiceNode) item).getTitleKz();
            } else if ("isBtnToolBarProp".equals(id)) {
                res = ((ServiceNode) item).isBtnToolBar();
            } else if ("hotKeyProp".equals(id)) {
                res = ((ServiceNode) item).getHotKey();
            } else if ("iconProp".equals(id)) {
                res = ((ServiceNode) item).getIcon();
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if (item != null && item instanceof ServiceNode && !(prop instanceof FolderProperty)) {
            final String id = prop.getId();
            if ("index".equals(id)) {
                ((ServiceNode) item).setRuntimeIndex(Integer.valueOf((String) value));
            } else if ("tabbed".equals(id)) {
                ((ServiceNode) item).setTab((Boolean) value);
            } else if ("title_t".equals(id)) {
                ((ServiceNode) item).setTabName((String) value);
            } else if ("title_t_kz".equals(id)) {
                ((ServiceNode) item).setTabNameKz((String) value);
            } else if ("title".equals(id)) {
                ((ServiceNode) item).setTitle((String) value);
            } else if ("title_kz".equals(id)) {
                ((ServiceNode) item).setTitleKz((String) value);
            } else if ("isBtnToolBarProp".equals(id)) {
                ((ServiceNode) item).setBtnToolBar((Boolean) value);
            } else if ("hotKeyProp".equals(id)) {
                ((ServiceNode) item).setHotKey((String) value);
            } else if ("iconProp".equals(id)) {
                ((ServiceNode) item).setIcon((byte[]) value);
            }
            owner.setModified("index".equals(id), "tabbed".equals(id), (ServiceNode) item);
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

    public String getTitle() {
        String title = "";
        if (item != null) {
            title = " - " + item.toString();
        }
        return "Процессы" + title;
    }

    @Override
    public Property getNewProperties() {
        return null;
    }
}
