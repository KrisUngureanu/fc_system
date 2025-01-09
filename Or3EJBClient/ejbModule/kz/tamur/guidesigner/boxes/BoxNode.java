package kz.tamur.guidesigner.boxes;

import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.comps.Constants;
import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 15:27:35
 * To change this template use File | Settings | File Templates.
 */
public class BoxNode extends AbstractDesignerTreeNode {

    private static KrnClass boxCls;
    //Class fields
    private String urlIn;
    private String urlOut;
    private long transport;
    private long typeMsg=0;
    //
    private int isModified = 0;
    private String pathIn;
    private String pathOut;
    private String pathTypeIn;
    private String pathTypeOut;
    private String pathInit;
    private byte[] config;
    private String charSet;
    private KrnObject base;
    private String baseTitle;
    public static int name_ = 0x0001;
    public static int urlIn_ = 0x0002;
    public static int urlOut_ = 0x0004;
    public static int transport_ = 0x0008;
    public static int pathIn_ = 0x0010;
    public static int pathOut_= 0x0020;
    public static int pathTypeIn_ = 0x0040;
    public static int pathTypeOut_ = 0x0080;
    public static int base_ = 0x0100;
    public static int pathInit_ = 0x0200;
    public static int charSet_ = 0x0400;
    public static int config_ = 0x0800;
    public static int typeMsg_ = 0x1000;

    public BoxNode(KrnObject obj, String name,
                   KrnObject base, String urlIn,
                   String urlOut, String pathIn,
                   String pathOut, String pathTypeIn,
                   String pathTypeOut,String pathInit,
                   byte[] config,String charSet,
                   long transport,int index,long typeMsg) {
    	nodeType = BOX_NODE;
        this.pathIn = pathIn;
        this.pathOut = pathOut;
        this.pathTypeIn = pathTypeIn;
        this.pathTypeOut = pathTypeOut;
        this.pathInit = pathInit;
        this.config = config;
        this.charSet=charSet;
        krnObj = obj;
        isLoaded = false;
        this.title = name;
        this.base=base;
        this.urlIn = urlIn;
        this.urlOut = urlOut;
        this.transport = transport;
        this.typeMsg = typeMsg;
        final Kernel krn =  Kernel.instance();
        final long lang_ru =  krn.getLangIdByCode("RU");
        try{
            if(base!=null){
                String[] titles = krn.getStrings(base,"наименование",lang_ru,0);
                if(titles.length>0)
                    baseTitle=titles[0];
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean isLeaf() {
        if (boxCls == null) {
            try {
                boxCls = Kernel.instance().getClassByName("BoxExchange");
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return krnObj.classId == boxCls.id;
    }





    public void rename(String newName) {
        title = newName;
/*        final Kernel krn = Kernel.instance();
        try {
            krn.setString(krnObj.id, krnObj.classId,
                    "name", 0, 0, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
*/
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            final Kernel krn =  Kernel.instance();
            if (!isLeaf()) {
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids,krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] names = krn.getStringValues(ids, krnObj.classId,
                            "name", 0, false, 0);
                    ObjectValue[] bases = krn.getObjectValues(ids, krnObj.classId,
                            "base", new long[0], 0);
                    StringValue[] urlsIn = krn.getStringValues(ids, krnObj.classId,
                            "urlIn", 0, false, 0);
                    StringValue[] urlsOut = krn.getStringValues(ids, krnObj.classId,
                            "urlOut", 0, false, 0);
                    StringValue[] pathsIn = krn.getStringValues(ids, krnObj.classId,
                            "xpathIn", 0, false, 0);
                    StringValue[] pathsOut = krn.getStringValues(ids, krnObj.classId,
                            "xpathOut", 0, false, 0);
                    StringValue[] pathsTypeIn = krn.getStringValues(ids, krnObj.classId,
                            "xpathTypeIn", 0, false, 0);
                    StringValue[] pathsTypeOut = krn.getStringValues(ids, krnObj.classId,
                            "xpathTypeOut", 0, false, 0);
                    StringValue[] pathsIdInit = krn.getStringValues(ids, krnObj.classId,
                            "xpathIdInit", 0, false, 0);
                    StringValue[] charSets = krn.getStringValues(ids, krnObj.classId,
                            "charSet", 0, false, 0);
                    LongValue[] transports = krn.getLongValues(ids, krnObj.classId,
                            "transport",0);
                    LongValue[] typeMsgs = krn.getLongValues(ids, krnObj.classId,
                            "typeMsg",0);
                    sort(ovs, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            ObjectValue ov1 = (ObjectValue) o1;
                            ObjectValue ov2 = (ObjectValue) o2;
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                return (ov1.index < ov2.index) ? -1 : 1;
                            }
                        }
                    });
                    List children;
                    children = new ArrayList();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String name = "Безымянный";
                        for (int j = 0; j < names.length; j++) {
                            if (names[j].objectId == ov.value.id) {
                                name = names[j].value;
                                break;
                            }
                        }
                        KrnObject base = null;
                        for (int j = 0; j < bases.length; j++) {
                            if (bases[j].objectId == ov.value.id) {
                                base = bases[j].value;
                                break;
                            }
                        }
                        String urlIn = "";
                        for (int j = 0; j < urlsIn.length; j++) {
                            if (urlsIn[j].objectId == ov.value.id) {
                                urlIn = urlsIn[j].value;
                                break;
                            }
                        }
                        String urlOut = "";
                        for (int j = 0; j < urlsOut.length; j++) {
                            if (urlsOut[j].objectId == ov.value.id) {
                                urlOut = urlsOut[j].value;
                                break;
                            }
                        }
                        String pathIn = "";
                        for (int j = 0; j < pathsIn.length; j++) {
                            if (pathsIn[j].objectId == ov.value.id) {
                                pathIn = pathsIn[j].value;
                                break;
                            }
                        }
                        String pathOut = "";
                        for (int j = 0; j < pathsOut.length; j++) {
                            if (pathsOut[j].objectId == ov.value.id) {
                                pathOut = pathsOut[j].value;
                                break;
                            }
                        }
                        String pathTypeIn = "";
                        for (int j = 0; j < pathsTypeIn.length; j++) {
                            if (pathsTypeIn[j].objectId == ov.value.id) {
                                pathTypeIn = pathsTypeIn[j].value;
                                break;
                            }
                        }
                        String pathTypeOut = "";
                        for (int j = 0; j < pathsTypeOut.length; j++) {
                            if (pathsTypeOut[j].objectId == ov.value.id) {
                                pathTypeOut = pathsTypeOut[j].value;
                                break;
                            }
                        }
                        String pathInit = "";
                        for (int j = 0; j < pathsIdInit.length; j++) {
                            if (pathsIdInit[j].objectId == ov.value.id) {
                                pathInit = pathsIdInit[j].value;
                                break;
                            }
                        }
                        String charSet = "";
                        for (int j = 0; j < charSets.length; j++) {
                            if (charSets[j].objectId == ov.value.id) {
                                charSet = charSets[j].value;
                                break;
                            }
                        }
                        long transport = 0;
                        for (int j = 0; j < transports.length; j++)
                            if (transports[j].objectId == ov.value.id) {
                                transport = transports[j].value;
                                break;
                            }
                        long typeMsg = 0;
                        for (int j = 0; j < typeMsgs.length; j++)
                            if (typeMsgs[j].objectId == ov.value.id) {
                                typeMsg = typeMsgs[j].value;
                                break;
                            }
                        byte[] config=krn.getBlob(ov.value,"config",0,0,0);
                        BoxNode childNode = new kz.tamur.guidesigner.boxes.
                                BoxNode(ov.value, name,base, urlIn,urlOut,pathIn,pathOut,pathTypeIn,
                                        pathTypeOut,pathInit,config,charSet,transport, ov.index,typeMsg);
                        TreeUIDMap.put(((KrnObject) ov.value).uid, childNode);
                        children.add(childNode);
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    public int isModified() {
        return isModified;
    }

    public void setModified(int modified) {
        if(modified>0)
          isModified |= modified;
        else if(modified==0)
            isModified = modified;
        else
            isModified ^=(- modified);
    }


    public String getName() {
        return title;
    }

    public void setBase(KrnObject base) {
        this.base = base;
        setModified(base_);
    }

    public void setName(String name) {
        this.title = name;
        setModified(name_);
    }

    public KrnObject getBaseStructureObj() {
        return base;
    }
    public String getUrlIn() {
        return urlIn;
    }

    public void setUrlIn(String urlIn) {
        this.urlIn = urlIn;
        setModified(urlIn_);
    }


    public String getPathTypeIn() {
        return pathTypeIn;
    }

    public String getPathTypeOut() {
        return pathTypeOut;
    }
    public void setPathTypeIn(String pathTypeIn) {
        this.pathTypeIn = pathTypeIn;
        setModified(pathTypeIn_);
    }

    public void setPathTypeOut(String pathTypeOut) {
        this.pathTypeOut = pathTypeOut;
        setModified(pathTypeOut_);
    }

    public String getUrlOut() {
        return urlOut;
    }

    public void setUrlOut(String urlOut) {
        this.urlOut = urlOut;
        setModified(urlOut_);
    }

    public String getPathIn() {
        return pathIn;
    }

    public void setPathIn(String pathIn) {
        this.pathIn = pathIn;
        setModified(pathIn_);
    }

    public String getPathOut() {
        return pathOut;
    }

    public void setPathOut(String pathOut) {
        this.pathOut = pathOut;
        setModified(pathOut_);
    }
    public String getPathInit() {
        return pathInit;
    }
    public void setPathInit(String pathInit) {
        this.pathInit = pathInit;
        setModified(pathInit_);
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
        setModified(charSet_);
    }
    public void setConfig(byte[]value) {
        this.config = value;
        setModified(config_);
    }
    public String getTransport() {
        if(transport==TransportIds.DIRECTORY){
            return Constants.TRANSPORT_LOCAL;
        }else if(transport==TransportIds.MAIL){
            return Constants.TRANSPORT_EMAIL;
        }else if(transport==TransportIds.MQ_TRANSPORT){
            return Constants.TRANSPORT_MQ_CLIENT;
        }else if(transport==TransportIds.MQ_JMS){
            return Constants.TRANSPORT_MQ_JMS;
        }else if(transport==TransportIds.JBOSS_JMS){
            return Constants.TRANSPORT_JBOSS_JMS;
        }else if(transport==TransportIds.WEB_SERVICE){
            return Constants.TRANSPORT_WS;
        }else if(transport==TransportIds.SGDS_TRANSPORT){
            return Constants.TRANSPORT_SGDS;
        }else if(transport==TransportIds.LOTUS_DIIOP){
            return Constants.TRANSPORT_DIIOP;
 //       }else if(transport==TransportIds.OPEN_MQ){
 //           return Constants.TRANSPORT_OPENMQ;
        }else {
            return "";
        }

    }

    public long getTransportInt() {
         return transport;
    }
    public void setTransport(String transport) {
        if(transport.equals(Constants.TRANSPORT_LOCAL)){
            this.transport=TransportIds.DIRECTORY;
        }else if(transport.equals(Constants.TRANSPORT_EMAIL)){
            this.transport=TransportIds.MAIL;
        }else if(transport.equals(Constants.TRANSPORT_MQ_CLIENT)){
            this.transport=TransportIds.MQ_TRANSPORT;
        }else if(transport.equals(Constants.TRANSPORT_MQ_JMS)){
            this.transport=TransportIds.MQ_JMS;
        }else if(transport.equals(Constants.TRANSPORT_JBOSS_JMS)){
            this.transport=TransportIds.JBOSS_JMS;
        }else if(transport.equals(Constants.TRANSPORT_WS)){
            this.transport=TransportIds.WEB_SERVICE;
        }else if(transport.equals(Constants.TRANSPORT_SGDS)){
            this.transport=TransportIds.SGDS_TRANSPORT;
        }else if(transport.equals(Constants.TRANSPORT_DIIOP)){
            this.transport=TransportIds.LOTUS_DIIOP;
//        }else if(transport.equals(Constants.TRANSPORT_OPENMQ)){
//            this.transport=TransportIds.OPEN_MQ;
        }else {
            this.transport=0;
        }
        setModified(transport_);

    }

    public String getCharSet() {
        return charSet;
    }

    public byte[] getConfig() {
        return config;
    }

    public String getBaseTitle() {
        return baseTitle;
    }

    public void setBaseTitle(String baseTitle) {
        this.baseTitle=baseTitle;
    }

    public long getTypeMsg() {
    return typeMsg;
}

    public void setTypeMsg(long typeMsg) {
        this.typeMsg = typeMsg;
        setModified(typeMsg_);
    }
}


