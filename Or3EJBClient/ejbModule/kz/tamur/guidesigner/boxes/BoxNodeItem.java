package kz.tamur.guidesigner.boxes;

import kz.tamur.or3.client.props.*;
import kz.tamur.comps.Constants;
import java.io.UnsupportedEncodingException;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.util.KrnObjectItem;

public class BoxNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private BoxPanel owner;
    public BoxNodeItem(Object item, BoxPanel owner){
       this.item = item;
        this.owner=owner;
    }

    public Property getProperties() {
            proot = new FolderProperty(null,null, "Элементы");
            if(item!=null){
                if(item instanceof BoxNode){
                    if(((BoxNode)item).isLeaf()){
                        Property name = new StringProperty(proot,"name", "Наименование");
                        TreeProperty base =new TreeProperty(proot,"base","База данных","Структура баз");
                        Property urlIn = new StringProperty(proot,"urlIn", "UrlIn");
                        Property urlOut = new StringProperty(proot,"urlOut", "UrlOut");
                        Property pathIn = new StringProperty(proot,"pathIn", "PathIn");
                        Property pathOut = new StringProperty(proot,"pathOut", "PathOut");
                        Property pathTypeIn = new StringProperty(proot,"pathTypeIn", "PathTypeIn");
                        Property pathTypeOut = new StringProperty(proot,"pathTypeOut", "PathTypeOut");
                        Property pathInit = new StringProperty(proot,"pathInit", "PathInit");
                        Property charSet = new StringProperty(proot,"charSet", "CharSet");
                        ExprProperty config=new ExprProperty(proot,"config","Config");
                        ComboProperty oper= new ComboProperty(proot, "transport","Тип транспорта");
                        oper.addItem("", "")
                            .addItem(""+Constants.TRANSPORT_LOCAL, Constants.TRANSPORT_LOCAL)
                            .addItem(""+Constants.TRANSPORT_EMAIL, Constants.TRANSPORT_EMAIL)
                            .addItem(""+Constants.TRANSPORT_MQ_CLIENT, Constants.TRANSPORT_MQ_CLIENT)
                            .addItem(""+Constants.TRANSPORT_MQ_JMS, Constants.TRANSPORT_MQ_JMS)
                            .addItem(""+Constants.TRANSPORT_JBOSS_JMS, Constants.TRANSPORT_JBOSS_JMS)
                            .addItem(""+Constants.TRANSPORT_WS, Constants.TRANSPORT_WS)
                            .addItem(""+Constants.TRANSPORT_SGDS, Constants.TRANSPORT_SGDS)
                        	.addItem(""+Constants.TRANSPORT_DIIOP, Constants.TRANSPORT_DIIOP);
//                        	.addItem(""+Constants.TRANSPORT_OPENMQ, Constants.TRANSPORT_OPENMQ);
                        ComboProperty typeMsg= new ComboProperty(proot, "typeMsg","Тип сообщения");
                        typeMsg.addItem("", "")
                            .addItem(Constants.MSG_XML, Constants.MSG_XML)
                            .addItem(Constants.MSG_FILE, Constants.MSG_FILE);
                    }else{
                        Property name = new StringProperty(proot,"name", "Наименование группы");
                    }

              }
            }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof BoxNode){
                    if("name".equals(prop.getId()))
                        res=""+((BoxNode)item).getName();
                    else if("base".equals(prop.getId())){
                        res=((BoxNode)item).getBaseStructureObj();
                        if(res instanceof KrnObject)
                            res=new KrnObjectItem((KrnObject)res,((BoxNode)item).getBaseTitle());
                        else
                            res=null;
                    }else if("urlIn".equals((prop.getId())))
                        res=""+((BoxNode)item).getUrlIn();
                    else if("urlOut".equals((prop.getId())))
                        res=""+((BoxNode)item).getUrlOut();
                    else if("pathIn".equals((prop.getId())))
                        res=""+((BoxNode)item).getPathIn();
                    else if("pathOut".equals((prop.getId())))
                        res=""+((BoxNode)item).getPathOut();
                    else if("pathTypeIn".equals((prop.getId())))
                        res=""+((BoxNode)item).getPathTypeIn();
                    else if("pathTypeOut".equals((prop.getId())))
                        res=""+((BoxNode)item).getPathTypeOut();
                    else if("pathInit".equals((prop.getId())))
                        res=""+((BoxNode)item).getPathInit();
                    else if("charSet".equals((prop.getId())))
                        res=""+((BoxNode)item).getCharSet();
                    else if("config".equals((prop.getId()))){
                        try {
                            res=((BoxNode)item).getConfig();
                            if(res instanceof byte[] && ((byte[])res).length>0){
                                res=new String((byte[])res,"UTF-8");
                            }else
                                res=null;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }else if("transport".equals((prop.getId()))){
                        res=""+((BoxNode)item).getTransport();
                    }else if("typeMsg".equals((prop.getId()))){
                        long tm= ((BoxNode)item).getTypeMsg();
                        res = tm==1? Constants.MSG_FILE: Constants.MSG_XML;

                    }
            }
            if(res==null) res="";
            if(prop instanceof ComboProperty){
                res=((ComboProperty)prop).getItem(res!=null?res.toString():"");
            }else if(prop instanceof ExprProperty && res instanceof String){
                res= new Expression((String)res);
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof BoxNode){
                if("name".equals(prop.getId()))
                    ((BoxNode)item).setName((String)value);
                else if("base".equals(prop.getId())){
                    KrnObject obj=null;
                    if(value instanceof KrnObjectItem){
                        obj=((KrnObjectItem)value).obj;
                        ((BoxNode)item).setBaseTitle(((KrnObjectItem)value).title);
                    }
                    ((BoxNode)item).setBase(obj);
                }else if("urlIn".equals(prop.getId()))
                    ((BoxNode)item).setUrlIn((String)value);
                else if("urlOut".equals(prop.getId()))
                    ((BoxNode)item).setUrlOut((String)value);
                else if("pathIn".equals(prop.getId()))
                    ((BoxNode)item).setPathIn((String)value);
                else if("pathOut".equals(prop.getId()))
                    ((BoxNode)item).setPathOut((String)value);
                else if("pathTypeIn".equals(prop.getId()))
                    ((BoxNode)item).setPathTypeIn((String)value);
                else if("pathTypeOut".equals(prop.getId()))
                    ((BoxNode)item).setPathTypeOut((String)value);
                else if("pathInit".equals(prop.getId()))
                    ((BoxNode)item).setPathInit((String)value);
                else if("charSet".equals(prop.getId()))
                    ((BoxNode)item).setCharSet((String)value);
                else if("config".equals(prop.getId()))
                    try {
                        ((BoxNode)item).setConfig(value instanceof Expression?((Expression)value).text.toString().getBytes("UTF-8"):null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                else if("transport".equals(prop.getId()))
                        ((BoxNode)item).setTransport(((ComboPropertyItem)value).id);
                else if("typeMsg".equals(prop.getId()))
                        ((BoxNode)item).setTypeMsg(Constants.MSG_FILE.equals(((ComboPropertyItem)value).id)?Constants.MSG_FILE_INT: Constants.MSG_XML_INT);
            }
            owner.setModified((BoxNode)item);
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
