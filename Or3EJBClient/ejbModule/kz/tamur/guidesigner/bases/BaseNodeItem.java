package kz.tamur.guidesigner.bases;

import kz.tamur.or3.client.props.*;

public class BaseNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private BasePanel owner;
    public BaseNodeItem(Object item, BasePanel owner){
       this.item = item;
        this.owner=owner;
    }

    public Property getProperties() {
            proot = new FolderProperty(null,null, "Элементы");
            if(item!=null){
                if(item instanceof BaseNode){
                    Property name = new StringProperty(proot,"name", "Наименование");
                    Property flag = new StringProperty(proot,"flags", "Флаг");
                    Property level = new StringProperty(proot,"level", "Уровень");
                        new CheckProperty(proot, "isPhysical","Физически раздельная?");
              }
            }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof BaseNode){
                    if("name".equals(prop.getId()))
                        res=""+((BaseNode)item).getName();
                    else if("flags".equals((prop.getId())))
                        res=""+((BaseNode)item).getFlags();
                    else if("level".equals((prop.getId())))
                        res=""+((BaseNode)item).getLevel();
                    else if("isPhysical".equals((prop.getId())))
                        res=((BaseNode)item).isPhysical();
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof BaseNode){
                if("name".equals(prop.getId()))
                    ((BaseNode)item).setName((String)value);
                if("level".equals(prop.getId()))
                    ((BaseNode)item).setLevel(Integer.valueOf((String)value));
                if("flags".equals(prop.getId()))
                    ((BaseNode)item).setFlags(Integer.valueOf((String)value));
                else if("isPhysical".equals(prop.getId()))
                        ((BaseNode)item).setAsPhysical(value instanceof Boolean?(Boolean)value:Boolean.FALSE);
            }
            owner.setModified((BaseNode)item);
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

	public String getTitle(){
        return "";
    }

    @Override
    public Property getNewProperties() {
        return null;
    }
}
