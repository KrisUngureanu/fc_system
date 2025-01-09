package kz.tamur.util.convert.nitreg;

import com.cifs.or2.server.Session;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.kernel.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 07.02.2007
 * Time: 12:29:39
 * To change this template use File | Settings | File Templates.
 */
public class ServiceComponent{

    public static final int ROLE_SRV=0,TABBED_SRV=1,SECTION_SRV=2,SERVICE_SRV=3,ACTION_SRV=4,OBJECT_SRV=5;
    KrnClass cls_srv;
    KrnClass cls_act;
    HashMap<Long,ServiceObject> srvMap=new HashMap<Long,ServiceObject>();
    HashMap<Long,ServiceObject> actMap=new HashMap<Long,ServiceObject>();
    protected class ServiceObject {
        public KrnObject obj_;
        public String title_="*";
        private String className_ = "";
        private String filterMsg_ = "";
        private KrnObject[] conflictSrv_;
        private KrnObject[] conflictObjSrv_;
        private String[] transitAttr_;
        private KrnObject[] transitSrv_;
        private KrnObject[] transitRuleSrv_;
        private String[] conflictSrvAttr_;
        private String[] createAttr_ ;
        private String[] statusAttr_;
        private String[] controlAttr_;
        private String[] contrStatusAttr_;
        private KrnObject[] status_;
        private KrnObject[] contrStatus_;
        private KrnObject[] h_obj_;
        private KrnObject[] actConds_;
        private KrnObject[] attrConds_;
        private KrnObject[] delConds_;
        private KrnObject[] nextActs_;
        private KrnObject[] e_obj_;
        private KrnObject f_obj_;
    }

    public void fill(Session session) throws KrnException {
        cls_srv=session.getClassByName("Service");
        KrnAttribute SRV_TITLE=session.getAttributeByName(cls_srv,"title");
        KrnAttribute SRV_CLASS_NAME=session.getAttributeByName(cls_srv,"className");
        KrnAttribute SRV_INTERFACE_ENTER=session.getAttributeByName(cls_srv,"interfaceEnter");
        KrnAttribute SRV_STATUS_RULE=session.getAttributeByName(cls_srv,"attrConds");
        KrnAttribute SRV_STATUS_ATTR=session.getAttributeByName(cls_srv,"statusAttr");
        KrnAttribute SRV_STATUS=session.getAttributeByName(cls_srv,"status");
        KrnAttribute SRV_CONTR_STATUS_ATTR=session.getAttributeByName(cls_srv,"contrStatusAttr");
        KrnAttribute SRV_CONTR_STATUS=session.getAttributeByName(cls_srv,"contrStatus");
        KrnAttribute SRV_CONFLICT_SRV=session.getAttributeByName(cls_srv,"conflictSrv");
        KrnAttribute SRV_CONFLICT_OBJ_SRV=session.getAttributeByName(cls_srv,"conflictObjSrv");
        KrnAttribute SRV_CONFLICT_SRV_ATTR=session.getAttributeByName(cls_srv,"conflictSrvAttr");
        KrnAttribute SRV_TRANSIT_SRV=session.getAttributeByName(cls_srv,"transitSrv");
        KrnAttribute SRV_TRANSIT_RULE_SRV=session.getAttributeByName(cls_srv,"transitRuleSrv");
        KrnAttribute SRV_TRANSIT_ATTR=session.getAttributeByName(cls_srv,"transitAttr");
        KrnAttribute ACTIONS=session.getAttributeByName(cls_srv,"actions");
        KrnObject[] objs=session.getClassObjects(cls_srv,new long[0],0);
        for(KrnObject obj:objs){
            ServiceObject srv=new ServiceObject();
            srv.obj_=obj;
            srvMap.put(obj.id,srv);
        }
        long[] ids=Funcs.makeObjectIdArray(objs);
        StringValue[] svs=session.getStringValues(ids,SRV_CLASS_NAME.id,0,false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.className_=sv.value;
        }
        svs=session.getStringValues(ids,SRV_TITLE.id,105,false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.title_=sv.value;
        }
        ObjectValue[] ovs=session.getObjectValues(ids,SRV_INTERFACE_ENTER.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.e_obj_=setObjectField(srv.e_obj_,ov.index,ov.value);
        }
        svs=session.getStringValues(ids,SRV_STATUS_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.statusAttr_=setStringField(srv.statusAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,SRV_STATUS_RULE.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.attrConds_=setObjectField(srv.attrConds_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,SRV_STATUS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.status_=setObjectField(srv.status_,ov.index,ov.value);
        }
        svs=session.getStringValues(ids,SRV_CONTR_STATUS_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.contrStatusAttr_=setStringField(srv.contrStatusAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,SRV_CONTR_STATUS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.contrStatus_=setObjectField(srv.contrStatus_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,SRV_CONFLICT_OBJ_SRV.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.conflictObjSrv_=setObjectField(srv.conflictObjSrv_,ov.index,ov.value);
        }
        svs=session.getStringValues(ids,SRV_CONFLICT_SRV_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.conflictSrvAttr_=setStringField(srv.conflictSrvAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,SRV_CONFLICT_SRV.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.conflictSrv_=setObjectField(srv.conflictSrv_,ov.index,ov.value);

        }
        svs=session.getStringValues(ids,SRV_TRANSIT_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject srv=srvMap.get(sv.objectId);
            srv.transitAttr_=setStringField(srv.transitAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,SRV_TRANSIT_RULE_SRV.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.transitRuleSrv_=setObjectField(srv.transitRuleSrv_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,SRV_TRANSIT_SRV.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject srv=srvMap.get(ov.objectId);
            srv.transitSrv_=setObjectField(srv.transitSrv_,ov.index,ov.value);
        }
        cls_act=session.getClassByName("Action");
        KrnAttribute ACT_TITLE=session.getAttributeByName(cls_act,"title");
        KrnAttribute ACT_INTERFACE_ENTER=session.getAttributeByName(cls_act,"interfaceEnter");
        KrnAttribute ACT_INTERFACE=session.getAttributeByName(cls_act,"interface");
        KrnAttribute ACT_INTERFACE_ATTR=session.getAttributeByName(cls_act,"interfaceAttr");
        KrnAttribute ACT_FILTER_ENTER=session.getAttributeByName(cls_act,"filterEnter");
        KrnAttribute ACT_FILTER_MSG=session.getAttributeByName(cls_act,"filterMsg");
        KrnAttribute ACT_ATTR_CONDITIONS=session.getAttributeByName(cls_act,"attrConds");
        KrnAttribute ACT_DEL_CONDITIONS=session.getAttributeByName(cls_act,"delConds");
        KrnAttribute ACT_CONDITIONS=session.getAttributeByName(cls_act,"actConds");
        KrnAttribute ACT_NEXT_ACTS=session.getAttributeByName(cls_act,"nextActs");
        KrnAttribute ACT_CONTROL_ATTR=session.getAttributeByName(cls_act,"controlAttr");
        KrnAttribute ACT_STATUS_ATTR=session.getAttributeByName(cls_act,"statusAttr");
        KrnAttribute ACT_STATUS=session.getAttributeByName(cls_act,"status");
        ovs=session.getObjectValues(ids,ACTIONS.id,new long[0],0);
        Collection<Long> ids_=new ArrayList<Long>();
        for(ObjectValue ov:ovs){
            ServiceObject act=new ServiceObject();
            act.obj_=ov.value;
            actMap.put(ov.value.id,act);
            ids_.add(ov.value.id);
        }
        ids=Funcs.makeLongArray(ids_);
        svs=session.getStringValues(ids,ACT_TITLE.id,105,false,0);
        for(StringValue sv:svs){
            ServiceObject act=actMap.get(sv.objectId);
            act.title_=sv.value;
        }
        ovs=session.getObjectValues(ids,ACT_INTERFACE_ENTER.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.e_obj_=setObjectField(act.e_obj_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,ACT_INTERFACE.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.h_obj_=setObjectField(act.h_obj_,ov.index,ov.value);
        }
        svs=session.getStringValues(ids,ACT_INTERFACE_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject act=actMap.get(sv.objectId);
            act.createAttr_=setStringField(act.createAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,ACT_FILTER_ENTER.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.f_obj_=ov.value;
        }
        svs=session.getStringValues(ids,ACT_FILTER_MSG.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject act=actMap.get(sv.objectId);
            act.filterMsg_=sv.value;
        }
        ovs=session.getObjectValues(ids,ACT_ATTR_CONDITIONS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.attrConds_=setObjectField(act.attrConds_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,ACT_DEL_CONDITIONS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.delConds_=setObjectField(act.delConds_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,ACT_CONDITIONS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.actConds_=setObjectField(act.actConds_,ov.index,ov.value);
        }
        ovs=session.getObjectValues(ids,ACT_NEXT_ACTS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.nextActs_=setObjectField(act.nextActs_,ov.index,ov.value);
        }
        svs=session.getStringValues(ids,ACT_CONTROL_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject act=actMap.get(sv.objectId);
            act.controlAttr_=setStringField(act.controlAttr_,sv.index,sv.value);
        }
        svs=session.getStringValues(ids,ACT_STATUS_ATTR.id,0,/*true*/false,0);
        for(StringValue sv:svs){
            ServiceObject act=actMap.get(sv.objectId);
            act.statusAttr_=setStringField(act.statusAttr_,sv.index,sv.value);
        }
        ovs=session.getObjectValues(ids,ACT_STATUS.id,new long[0],0);
        for(ObjectValue ov:ovs){
            ServiceObject act=actMap.get(ov.objectId);
            act.status_=setObjectField(act.status_,ov.index,ov.value);
        }
    }
    private KrnObject[] setObjectField(KrnObject[] dest_,int index_,KrnObject obj_){
        if (dest_ == null)
            dest_= new KrnObject[index_ + 1];
        if (dest_.length <= index_) {
            KrnObject[] objs_a = new KrnObject[index_ + 1];
            System.arraycopy(dest_, 0, objs_a, 0, dest_.length);
            dest_ = objs_a;
        }
        dest_[index_]= obj_;
        return dest_;
    }

    private String[] setStringField(String[] dest_,int index_,String str_){
        if (dest_ == null)
            dest_= new String[index_ + 1];
        if (dest_.length <= index_) {
            String[] objs_a = new String[index_ + 1];
            System.arraycopy(dest_, 0, objs_a, 0, dest_.length);
            dest_ = objs_a;
        }
        dest_[index_]= str_;
        return dest_;
    }
    public String  getInterfaces(KrnObject obj){
        ServiceObject srv=null;
        if(obj.classId==cls_srv.id){
            srv=srvMap.get(obj.id);
        }else if(obj.classId==cls_act.id){
            srv=actMap.get(obj.id);
        }
        if(srv!=null){
            String res="/*";
            if(srv.e_obj_!=null && srv.e_obj_.length>0){
                res+="Интерфейсы для ввода:";
                res+="uid="+srv.e_obj_[0].uid;
                for(int i=1;i<srv.e_obj_.length;++i)
                    res+=",uid="+srv.e_obj_[i];
                res+="\n";
            }
            if(srv.h_obj_!=null && srv.h_obj_.length>0){
                res+="Интерфейсы обработки:";
                res+="uid="+srv.h_obj_[0].uid;
                for(int i=1;i<srv.h_obj_.length;++i)
                    res+=",uid="+srv.h_obj_[i].uid;
                res+="\n";
            }
            res+="*/";
            return res;
        }else return "";
    }

    public String  getTitle(KrnObject obj){
        ServiceObject srv;
        if(obj.classId==cls_srv.id){
            srv=srvMap.get(obj.id);
            return srv.title_;
        }else if(obj.classId==cls_act.id){
            srv=actMap.get(obj.id);
            return srv.title_;
        }else
            return "*";
    }

    public String  getDescription(KrnObject obj){
        ServiceObject srv;
        if(obj.classId==cls_srv.id){
            srv=srvMap.get(obj.id);
        }else if(obj.classId==cls_act.id){
            srv=actMap.get(obj.id);
        }else
            return "";
        String res="/*";
        if(!srv.className_.equals("")){
            res+="    КЛАСС ОБРАБАТЫВАЕМОГО ОБЪЕКТА:"+srv.className_;
            res+="\n";

        }
        if(srv.f_obj_!=null){
            res+="    ФИЛЬТР НА СОЗДАНИЕ ОБЪЕКТА:uid="+srv.f_obj_.uid;
            res+="\n";
            res+=srv.filterMsg_;
            res+="\n";
        }
        if(srv.controlAttr_!=null && srv.controlAttr_.length>0){
            res+="    ДАТА КОНТРОЛЯ:"+srv.controlAttr_[0];
            res+="\n";
            if(srv.controlAttr_.length>1){
                res+="    ДАТА ПРЕДУПРЕЖДЕНИЯ:"+srv.controlAttr_[1];
                res+="\n";
            }
            if(srv.controlAttr_.length>2){
                res+="    ДАТА АВТОПЕРЕМЕЩЕНИЯ:"+srv.controlAttr_[2];
                res+="\n";
            }
        }
        Object[] str_a= srv.contrStatusAttr_ ;
        if(str_a!=null && str_a.length>0){
            res+="    КОНТРОЛЬ СОСТОЯНИЯ АТРИБУТОВ";
            res+="\n";
            for (Object aStr_a : str_a) {
                String str_ = (String) aStr_a;
                res += "\n     " + str_;
            }
            res+="\n";
        }
        str_a= srv.statusAttr_ ;
        int i1=0,i2=0,i3=0,i4=0,i5=0,i6=0,i7=0;
        ArrayList<String> al_in=new ArrayList<String>();
        ArrayList<String> al_out=new ArrayList<String>();
        if(str_a!=null && str_a.length>0){
            for (Object aStr_a : str_a) {
                String str_ = (String) aStr_a, str_s, str_s1="", str_s2 = "";
                i1 = str_.indexOf("|");
                if(i1<0)
                    i1 = str_.length();
                else{
                    i2 = str_.indexOf("|", i1 + 1);
                    if(i2<0)
                        i2 = str_.length();
                    else{
                        i3 = str_.indexOf("|", i2 + 1);
                        i4 = str_.indexOf("|", i3 + 1);
                        i5 = str_.indexOf("|", i4 + 1);
                        i6 = str_.indexOf("|", i5 + 1);
                        i7 = str_.indexOf("|", i6 + 1);
                    }
                }
                str_s = "     Атрибут:" + str_.substring(0, i1);
                if (i2 > i1 + 1)str_s1 = "     В состояние:" + str_.substring(i1 + 1, i2);
                if (i5 > i4 + 1) str_s2 = "     По условию:" + str_.substring(i4 + 1, i5);
                if (i6 > i5 + 1 && str_.substring(i5 + 1, i6).equals("true")) str_s1 += " >> Копия";
                if (i3>i2 + 1 && str_.substring(i2 + 1, i3).equals("true")) {
                    al_in.add(str_s);
                    al_in.add(str_s1);
                    if (!str_s2.equals("")) al_in.add(str_s2);
                } else {
                    al_out.add(str_s);
                    al_out.add(str_s1);
                    if (i5 > i4 + 1) al_out.add(str_s2);
                }
                if (i7 > 0) {
                    res += str_.substring(i6 + 1, i7).equals("true") ? " При обратном движении" : "";
                    res += "\n";
                }
            }
        }
        str_a= srv.createAttr_ ;
        if(al_in.size()>0 || (str_a!=null && str_a.length>0)){
            res+="    ПРИ ВХОДЕ.";
            res+="\n";
        }
        if(str_a!=null && str_a.length>0){
            res+="    СОЗДАЕТ ДОКУМЕНТЫ";
            res+="\n";
            for (Object aStr_a : str_a) {
                String str_ = (String) aStr_a;
                i1 = str_.indexOf("|");
                if (i1 < 0) {
                    res += "     Создает:" + str_;
                    res += "\n";
                    continue;
                }
                i2 = str_.indexOf("|", i1 + 1);
                i3 = str_.indexOf("|", i2 + 1);
                i4 = str_.indexOf("|", i3 + 1);
                i5 = str_.indexOf("|", i4 + 1);
                res += "     Создает:" + str_.substring(0, i1);
                res += "\n";
                if (i2 > i1 + 1) {
                    res += "     На основе:" + str_.substring(i1 + 1, i2);
                    res += "\n";
                }
                if (i3 > i2 + 1) {
                    res += "     По условию:" + str_.substring(i2 + 1, i3);
                    res += str_.substring(i3 + 1, i4).equals("true") ? " Удаляется" : "";
                    res += "\n";
                }
                if (i5 > 0) {
                    res += str_.substring(i4 + 1, i5).equals("true") ? " При обратном движении" : "";
                    res += "\n";
                }
            }
            res+="\n";
        }
        if(al_in.size()>0){
           res+="    УСТАНАВЛИВАЕТ СОСТОЯНИЯ АТРИБУТОВ";
            res+="\n";
            for (String anAl_in : al_in) {
                res += anAl_in;
                res += "\n";
            }
            res+="\n";
        }
        if(al_out.size()>0){
            res+="    ПРИ ВЫХОДЕ.";
            res+="\n";
            res+="    УСТАНАВЛИВАЕТ СОСТОЯНИЯ АТРИБУТОВ";
            res+="\n";
            for (String anAl_out : al_out) {
                res += anAl_out;
                res += "\n";
            }
            res+="\n";
        }
        str_a= srv.transitAttr_ ;
        if(str_a!=null && str_a.length>0){
            res+="    ПЕРЕДАЕТ ДОКУМЕНТЫ В СЛУЖБЫ";
            res+="\n";
            for (Object aStr_a : str_a) {
                String str_ = (String) aStr_a;
                i1 = str_.indexOf("|");
                if(i1<0) continue;
                i2 = str_.indexOf("|", i1 + 1);
                i3 = str_.indexOf("|", i2 + 1);
                i4 = str_.indexOf("|", i3 + 1);
                res += "     Передает документ:" + str_.substring(0, i1);
                res += "\n";
                res += "     В службу:" + str_.substring(i1 + 1, i2);
                res += "\n";
                if (i3 > i2 + 1) {
                    res += "     По условию:" + str_.substring(i2 + 1, i3);
                    res += "\n";
                }
                res += str_.substring(i3 + 1, i4).equals("true") ? "     При движении назад" : "";
                res += "\n";
            }
            res+="\n";
        }
        res+="*/";
        return res;
    }
}