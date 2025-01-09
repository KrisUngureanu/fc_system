package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.FILTER;
import static kz.tamur.comps.models.Types.KRNOBJECT;
import static kz.tamur.comps.models.Types.MSTRING;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.REPORT;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STRING;
import static kz.tamur.comps.models.Types.VIEW_STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import kz.tamur.util.Funcs;

/**
 * The Class PropertyNode.
 * 
 * @author Lebedev Sergey
 */
public class PropertyNode {

    /** parent. */
    private PropertyNode parent;

    /** children. */
    private List<PropertyNode> children = new ArrayList<PropertyNode>();

    /** by name. */
    private Map<String, PropertyNode> byName = new HashMap<String, PropertyNode>();

    /** name. */
    private String name;

    /** type. */
    private int type = -1;

    /** type flr. */
    private Integer typeFlr;

    /** path. */
    private String[] path;

    /** full path. */
    private String fullPath;

    /** enum values. */
    private EnumValue[] enumValues;

    /** krn class name. */
    private String krnClassName;

    /** title attr name. */
    private String titleAttrName;

    /** is array. */
    private boolean isArray;

    /** default value. */
    private Object defaultValue;

    /** type value. */
    private long typeValue;

    /** plain mode. */
    private boolean plainMode = false;

    /** full name. */
    private String fullName = "Элементы";

    /** p list. */
    private List<PropertyNode> pList = new ArrayList<PropertyNode>();

    /**
     * Конструктор класса property node.
     * 
     * @param parent
     *            the parent
     * @param name
     *            the name
     * @param type
     *            the type
     * @param enumValues
     *            the enum values
     * @param isArray
     *            the is array
     * @param defaultValue
     *            the default value
     */
    public PropertyNode(PropertyNode parent, String name, int type, EnumValue[] enumValues, boolean isArray, Object defaultValue) {
        this(parent, name, type, enumValues, isArray,defaultValue,-1);
    	
    }
    /**
     * Конструктор класса property node.
     * 
     * @param parent
     *            the parent
     * @param name
     *            the name
     * @param type
     *            the type
     * @param enumValues
     *            the enum values
     * @param isArray
     *            the is array
     * @param defaultValue
     *            the default value
     */
    public PropertyNode(PropertyNode parent, String name, int type, EnumValue[] enumValues, boolean isArray, Object defaultValue, long typeValue) {
    	name = Funcs.normalizeInput(name);
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.typeValue = typeValue;
        this.defaultValue = defaultValue;
        if (enumValues != null) {
            this.enumValues = enumValues.clone();
        }
        this.isArray = isArray;
        if (parent != null) {
            parent.addChild(this);
            path = new String[parent.path.length + 1];
            System.arraycopy(parent.path, 0, path, 0, parent.path.length);
            path[path.length - 1] = name;
            fullPath = (path.length > 1) ? new StringBuilder(parent.fullPath).append('.').append(name).toString() : name;
            fullName = (path.length > 1) ? new StringBuilder(parent.fullName).append('.').append(En2Ru.translate(name)).toString() : En2Ru.translate(name);
        } else {
            path = new String[0];
        }

        if (EXPR == type || REF == type || STRING == type || MSTRING == type || RSTRING == type || VIEW_STRING == type || FILTER == type || REPORT == type || KRNOBJECT == type) {
            PropertyReestr.registerProperty(this);
        }
    }

    public PropertyNode(int index, PropertyNode parent, String name, int type, EnumValue[] enumValues, boolean isArray, Object defaultValue) {
    	name = Funcs.normalizeInput(name);
    	this.parent = parent;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        if (enumValues != null) {
            this.enumValues = enumValues.clone();
        }
        this.isArray = isArray;
        if (parent != null) {
            parent.addChild(this, index);
            path = new String[parent.path.length + 1];
            System.arraycopy(parent.path, 0, path, 0, parent.path.length);
            path[path.length - 1] = name;

            fullPath = (path.length > 1) ? new StringBuilder(parent.fullPath).append('.').append(name).toString() : name;
            fullName = (path.length > 1) ? new StringBuilder(parent.fullName).append('.').append(En2Ru.translate(name)).toString() : En2Ru.translate(name);
        } else {
            path = new String[0];
        }

        if (EXPR == type || MSTRING == type || REF == type || RSTRING == type || VIEW_STRING == type || FILTER == type || Types.REPORT == type) {
            PropertyReestr.registerProperty(this);
        }
    }

    public Map<String, PropertyNode> getByName() {
        return byName;
    }

    public String getName() {
        return name;
    }

    public void addChild(PropertyNode node) {
        children.add(node);
        byName.put(node.name, node);
    }

    public void addChild(PropertyNode node, int index) {
        children.add(index, node);
        byName.put(node.name, node);
    }

    public void removeChild(String name) {
        Object child = byName.remove(name);
        if (child != null) {
            children.remove(child);
        }
    }

    /**
     * Удаляет всех потомков у ветки за исключением тех, чьи имена ему переданы.
     * 
     * @param names
     *            массив имён потомков, удалять которые не нужно
     */
    public void removeAllChildExcept(String[] names) {
        int count = getChildCount();
        if (names.length == 0) {
            return;
        }
        boolean isExcept;
        for (int i = 0; i < count; ++i) {
            PropertyNode node = getChildAt(i);
            String nameA = node.getName();
            isExcept = false;
            for (String name : names) {
                if (name.equals(nameA)) {
                    isExcept = true;
                    break;
                }
            }
            if (!isExcept) {
                removeChild(nameA);
                // при удалении уменьшить размер массива и индекс цикла
                --count;
                --i;
            }
        }
    }

    public PropertyNode getChild(String name) {
        PropertyNode prop = (PropertyNode) byName.get(name);
        return prop;
    }

    public int getType() {
        return type;
    }

    public Integer getTypeFlr() {
        return typeFlr;
    }

    public void setTypeFlr(Integer typeFlr) {
        this.typeFlr = typeFlr;
    }

    public String[] getPath() {
        return path;
    }

    @Override
    public String toString() {
        return plainMode ? fullName : En2Ru.translate(name);
    }

    public PropertyNode getChildAt(int childIndex) {
        return (PropertyNode) children.get(childIndex);
    }

    public int getChildCount() {
        return children.size();
    }

    public int getIndex(PropertyNode child) {
        for (int i = 0; i < children.size(); i++) {
            PropertyNode propertyNode = (PropertyNode) children.get(i);
            if (propertyNode.equals(child)) {
                return i;
            }
        }
        return -1;
    }

    public PropertyNode getParent() {
        return parent;
    }

    public EnumValue[] getEnumValues() {
        if (enumValues != null) {
            return (EnumValue[]) enumValues.clone();
        }
        return null;
    }

    public String getKrnClassName() {
        return krnClassName;
    }

    public String getTitleAttrName() {
        return titleAttrName;
    }

    public void setKrnClass(String className, String title) {
        krnClassName = Funcs.normalizeInput(className);
        titleAttrName = Funcs.normalizeInput(title);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PropertyNode) {
            if (fullPath != null) {
                return fullPath.equals(((PropertyNode) obj).fullPath);
            }
        }
        return false;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public long getTypeValue() {
        return typeValue;
    }
    public void setPlainMode(boolean plainMode) {
        this.plainMode = plainMode;
        for (int i = 0; i < children.size(); i++) {
            ((PropertyNode) children.get(i)).setPlainMode(plainMode);
        }
    }

    private void createTreePath(PropertyNode node) {
        pList.add(node);
        PropertyNode parent = node.getParent();
        if (parent != null) {
            createTreePath(parent);
        }
    }

    public TreePath getTreePath() {
        createTreePath(this);
        if (pList.size() > 0) {
            List<PropertyNode> l = new ArrayList<PropertyNode>();
            for (int i = pList.size() - 1; i >= 0; i--) {
                PropertyNode propertyNode = (PropertyNode) pList.get(i);
                l.add(propertyNode);
            }
            PropertyNode[] path = new PropertyNode[l.size()];
            for (int i = 0; i < l.size(); i++) {
                PropertyNode propertyNode = (PropertyNode) l.get(i);
                path[i] = propertyNode;
            }
            return new TreePath(path);
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return fullPath.hashCode();
    }

    public String getFullPath() {
        return fullPath;
    }

    public EnumValueToolTip[] getEnumToolTipValues() {
        return (EnumValueToolTip[]) enumValues.clone();
    }
    
    public String getFullName() {
    	return fullName;
    }
}
