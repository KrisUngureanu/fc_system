package kz.tamur.guidesigner.hypers;

import com.cifs.or2.client.util.KrnObjectItem;
import kz.tamur.or3.client.props.*;

public class HyperNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private HyperPanel owner;
    public HyperNodeItem(Object item, HyperPanel owner){
       this.item = item;
        this.owner=owner;
    }

    public Property getProperties() {
            proot = new FolderProperty(null, null,"Элементы");
            if(item!=null){
                if(item instanceof HyperNode){
                    Property title = new StringProperty(proot,"title", "Заголовок");
                    Property title_kz = new StringProperty(proot,"title_kz", "ЗаголовокКаз");
                    Property index = new StringProperty(proot,"runtimeIndex", "Индекс");
                    if(((HyperNode)item).isLeaf()){
                        UiOrJumpProperty ui=new UiOrJumpProperty(proot,"hiperObj","Интерфейс");
                        CheckProperty  isDialog= new CheckProperty(proot,"isDialog", "Диалог");
                        CheckProperty  isChangeable= new CheckProperty(proot,"isChangeable", "Сохраняемый");
                        ImageProperty icon = new ImageProperty(proot, "icon", "Иконка");
                    }
              }
            }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof HyperNode){
                    if("runtimeIndex".equals(prop.getId())){
                        res=""+((HyperNode)item).getRuntimeIndex();
                    }else if("title".equals(prop.getId())){
                        res=item.toString();
                    }else if("title_kz".equals(prop.getId())){
                        res=((HyperNode)item).getTitleKz();
                    }else if("hiperObj".equals(prop.getId())){
                        KrnObjectItem obj=((HyperNode)item).getIfcObjectItem();
                        if(obj!=null){
                            res=obj;
                        }else
                            res=null;
                    }else if("isDialog".equals(prop.getId())){
                        res=((HyperNode)item).isDialog();
                    }else if("isChangeable".equals(prop.getId())){
                        res=((HyperNode)item).isChangeable();
                    }else if("icon".equals(prop.getId())) {
                    	res = ((HyperNode)item).getIcon();
                    }
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if(item!=null && item instanceof HyperNode && !(prop instanceof FolderProperty)){
            if("runtimeIndex".equals(prop.getId())){
                ((HyperNode)item).setRuntimeIndex(Integer.valueOf((String)value));
            }else if("title".equals(prop.getId())){
                ((HyperNode)item).rename((String)value);
            }else if("title_kz".equals(prop.getId())){
                ((HyperNode)item).setTitleKz((String)value);
            }else if("hiperObj".equals(prop.getId())){
                if(value instanceof KrnObjectItem){
                ((HyperNode)item).setIfcObjectItem((KrnObjectItem)value);
                }else if(value==null){
                    ((HyperNode)item).setIfcObjectItem(null);
                }
            }else if("isDialog".equals(prop.getId())){
                ((HyperNode)item).setDialog((Boolean)value);
            }else if("isChangeable".equals(prop.getId())){
                ((HyperNode)item).setChangeable((Boolean)value);
            }else if("icon".equals(prop.getId())) {
            	((HyperNode)item).setIcon((byte[]) value);
            }
            owner.setModified("runtimeIndex".equals(prop.getId()),(HyperNode)item);
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
