package kz.tamur.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;

import static com.cifs.or2.client.Kernel.SC_CONTROL_FOLDER;

/**
 * The Class ServiceControlNode.
 * 
 * @author Sergey Lebedev
 */
public class ServiceControlNode extends AbstractDesignerTreeNode {
    /** The is runtime design. */
    private boolean isRuntimeDesign = false;

    /** Тип узла - Директория. */
    private boolean isFolder = false;

    /** Тип узла - Процесс. */
    private boolean isService = false;

    /** Тип узла - Интерфейс. */
    private boolean isInterface = false;

    /** Тип узла - Фильтр. */
    private boolean isFilter = false;

    /** Тип узла - Отчёт. */
    private boolean isReport = false;
    
    /** Тип директории*/
    private int type = 0;

    /** The krn. */
    private static Kernel krn;
    private boolean isOpened;
    private KrnObject value;

    private static Map<Long,TitlesKrnObj> sctMap = new HashMap<Long, TitlesKrnObj>();
    private static KrnAttribute valueAttr = null;
    private static KrnAttribute titleAttr = null;

    static {
    	krn = Kernel.instance();
    	try {
    		valueAttr = krn.getAttributeByName(SC_CONTROL_FOLDER, "value");
    		titleAttr = krn.getAttributeByName(SC_CONTROL_FOLDER, "title");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * Создание нового service control node.
     * 
     * @param srvObj
     *            the srv obj
     * @param title
     *            the title
     * @param langId
     *            the lang id
     * @param index
     *            the index
     */
    public ServiceControlNode(KrnObject nodeObj, KrnObject value, String title, int type, long langId, int index) {
        krnObj = nodeObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
        this.type = type;
        if (value == null) {
            isFolder = true;
        } else {
            this.value = value;
            long id = value.classId;
            isService = id == Kernel.SC_PROCESS_DEF.id;
            isInterface = id == Kernel.SC_UI.id;
            isFilter = id == Kernel.SC_FILTER.id;
            isReport = id == Kernel.SC_REPORT_PRINTER.id;
        }
    }

    @Override
    protected void load() {
        if (!isLoaded && krnObj != null) {
            isLoaded = true;
            long[] oids = { krnObj.id };
            ObjectValue[] ovs = new ObjectValue[0];
            try {
                ovs = krn.getObjectValues(oids, krnObj.classId, "children", 0);
                long[] ids = new long[ovs.length];
                for (int i = 0; i < ovs.length; i++) {
                    ids[i] = ovs[i].value.id;
                }
            
                List<ServiceControlNode> children = new ArrayList<ServiceControlNode>();
                try {
                    AttrRequestBuilder arb = new AttrRequestBuilder(SC_CONTROL_FOLDER, krn).add("title").add("value").add("type");
                    
                    List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
                    for (Object[] row : rows) {
                        KrnObject obj = (KrnObject) row[0];

                        long type = (row.length < 5 || row[4] == null) ? 0 : (Long) row[4];

                        children.add(new ServiceControlNode(obj, (KrnObject) row[3], (String) row[2], (int) type, langId, 0));
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }

                addAllChildren(children, Utils.getServiceControlNodeComparator());
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }
    
	public ServiceControlNode findChild(KrnObject obj) {
        if (obj == null) {
            return null;
        }
        
        if (krnObj.id == obj.id) {
            return this;
        }

        List<KrnObject> pathToObject = new ArrayList<KrnObject>();
        pathToObject.add(obj);
        
        ServiceControlNode root = null;
        try {
	        KrnObject[] parents = krn.getObjects(obj, "parent", 0);
	        while (parents != null && parents.length > 0) {
	        	obj = parents[0];
	        	pathToObject.add(obj);
	        	parents = krn.getObjects(obj, "parent", 0);
	        }
		        
	        root = this;
	        for (int i = pathToObject.size() - 2; i >= 0; i--) {
	        	KrnObject o = pathToObject.get(i);
	            for (Enumeration c = root.children(); c.hasMoreElements();) {
	            	ServiceControlNode child = (ServiceControlNode) c.nextElement();
	                if (child.krnObj.id == o.id) {
	                	root = child;
	                	break;
	                }
	            }
	        }
	        return root;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
	
    public ServiceControlNode findChildValue(KrnObject obj) {
        if (obj == null) {
            return null;
        }

        try {
            KrnObject[] nodes = krn.getObjectsByAttribute(SC_CONTROL_FOLDER.id, valueAttr.id, 0, ComparisonOperations.CO_EQUALS,
                    obj.id, 0);
            if (nodes.length > 0) {
                obj = nodes[0];
                if (krnObj.id == obj.id) {
                    return this;
                }

                List<KrnObject> pathToObject = new ArrayList<KrnObject>();
                pathToObject.add(obj);

                ServiceControlNode root = null;
                KrnObject[] parents = krn.getObjects(obj, "parent", 0);
                while (parents != null && parents.length > 0) {
                    obj = parents[0];
                    pathToObject.add(obj);
                    parents = krn.getObjects(obj, "parent", 0);
                }

                root = this;
                for (int i = pathToObject.size() - 2; i >= 0; i--) {
                    KrnObject o = pathToObject.get(i);
                    for (Enumeration c = root.children(); c.hasMoreElements();) {
                        ServiceControlNode child = (ServiceControlNode) c.nextElement();
                        if (child.krnObj.id == o.id) {
                            root = child;
                            break;
                        }
                    }
                }
                return root;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<ServiceControlNode> findAllParents(KrnObject obj) {
        if (obj == null) {
            return null;
        }
        Set<ServiceControlNode> elementParents = new HashSet<ServiceControlNode>();
        try {
            KrnObject[] nodes = krn.getObjectsByAttribute(SC_CONTROL_FOLDER.id, valueAttr.id, 0, ComparisonOperations.CO_EQUALS,
                    obj.id, 0);
            for (KrnObject node : nodes) {
                elementParents.add((ServiceControlNode) findChild(node).getParent());
            }
            return elementParents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ServiceControlNode> findAllChild(KrnObject obj) {
        if (obj == null) {
            return null;
        }
        List<ServiceControlNode> elementParents = new ArrayList<ServiceControlNode>();
        try {
            KrnObject[] nodes = krn.getObjectsByAttribute(SC_CONTROL_FOLDER.id, valueAttr.id, 0, ComparisonOperations.CO_EQUALS,
                    obj.id, 0);
            for (KrnObject node : nodes) {
                elementParents.add((ServiceControlNode) findChild(node));
            }
            return elementParents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
    public List<ServiceControlNode> findNodeByTitle(String title, int typeSearch) {
        if (title == null) {
            return null;
        }

        if (typeSearch == ComparisonOperations.SEARCH_START_WITH) {
            title = title.toUpperCase(Constants.OK) + "%";
        } else if (typeSearch == ComparisonOperations.CO_CONTAINS){
            title = "%" + title.toUpperCase(Constants.OK) + "%";
        }

        List<ServiceControlNode> elementParents = new ArrayList<ServiceControlNode>();
        try {
            KrnObject[] nodes = krn.getObjectsByAttribute(SC_CONTROL_FOLDER.id, titleAttr.id, 0, typeSearch, title, 0);
            for (KrnObject node : nodes) {
                elementParents.add((ServiceControlNode) findChild(node));
            }
            return elementParents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    /**
     * Поиск значения узла среди загруженных элементов дерева.
     *
     * @param obj the obj
     * @return true, в случае успеха
     */
    public boolean findLoadedChildValue(ServiceControlNode obj) {
        if (obj != null) {
            if (!this.equals(obj) && getValue().equals(obj.getValue())) {
                return true;
            } else {
                if (getChildCount() != 0) {
                    Enumeration<ServiceControlNode> childElements = children();
                    while (childElements.hasMoreElements()) {
                        ServiceControlNode hn = childElements.nextElement();
                        if (hn.findLoadedChildValue(obj)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
	/**
     * Пересортировать.
     */
    public void resort() {
        List<ServiceControlNode> ch = new ArrayList<ServiceControlNode>();
        Enumeration<ServiceControlNode> childElements = children();
        while (childElements.hasMoreElements()) {
            ServiceControlNode hn = childElements.nextElement();
            if (!hn.isLeaf()) {
                hn.resort();
            }
            ch.add(hn);
        }
        removeAllChildren();
        for (ServiceControlNode sn : ch) {
            add(sn);
        }
    }

    /**
     * Получить title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Установить title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Проверяет, является ли report.
     * 
     * @return <code>true</code> если узел - Отчёт
     */
    public boolean isReport() {
        return isReport;
    }

    /**
     * Проверяет, является ли folder.
     * 
     * @return <code>true</code> если узел - Директория
     */
    public boolean isFolder() {
        return isFolder;
    }

    /**
     * Проверяет, является ли service.
     * 
     * @return <code>true</code> если узел - Процесс
     */
    public boolean isService() {
        return isService;
    }

    /**
     * Проверяет, является ли interface.
     * 
     * @return <code>true</code> если узел - Интерфейс
     */
    public boolean isInterface() {
        return isInterface;
    }

    /**
     * Проверяет, является ли filter.
     * 
     * @return <code>true</code> если узел - Фильтр
     */
    public boolean isFilter() {
        return isFilter;
    }

    /**
     * Проверяет, является ли runtime design.
     * 
     * @return true, если runtime design
     */
    public boolean isRuntimeDesign() {
        return isRuntimeDesign;
    }

    /**
     * Установить runtime design.
     * 
     * @param runtimeDesign
     *            the new runtime design
     */
    public void setRuntimeDesign(boolean runtimeDesign) {
        isRuntimeDesign = runtimeDesign;
    }

    /**
     * Открыт ли узел в дизайнере
     * 
     * @return <code>true</code> если открыт
     */
    public boolean isOpened() {
        return isOpened;
    }

    /**
     * Отметить узел как открытый или нет в дизайнере
     * 
     * @param isOpene
     *            <code>true</code> если открыт
     */
    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    /**
     * @return the value
     */
    public KrnObject getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(KrnObject value) {
        this.value = value;
    }
    
    /**
     * @return the sctMap
     */
    public static Map<Long, TitlesKrnObj> getNodeMap() {
    	return sctMap;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
}
