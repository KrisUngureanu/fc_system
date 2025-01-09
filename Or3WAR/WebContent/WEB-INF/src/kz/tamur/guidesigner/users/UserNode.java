package kz.tamur.guidesigner.users;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.ClassNode;

import java.util.*;
import java.io.ByteArrayInputStream;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.PasswordService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class UserNode extends AbstractDesignerTreeNode {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + UserNode.class.getName());
	
    public static final int PROPERTY_NOT_CHANGED = 0;
    public static final int PASSWORD_TOO_SHORT = 1;
    public static final int PROPERTY_CHANGED = 2;
    public static final int LOGIN_TOO_SHORT = 3;
    public static final int LOGIN_TOO_LONG = 4;
    public static final int PASSWORD_TOO_LONG = 5;
    
    private static KrnClass userCls;
    //Class fields
    private KrnObject baseStructureObj;
    private KrnObject dataLangObj;
    private KrnObject ifcLangObj;
    private KrnObject ifcObj;
    private String pd;
    private String sign;
    private String sign_kz;
    private String doljnost;
    private String email;
    private KrnObject[] hypers;
    private boolean isEditor = false;
    private boolean isAdmin = false;
    private boolean isAdded = false;
    private boolean isDeveloper = true;
    private boolean isBlocked = false;
    //
    private boolean isModified = false;
//    private KrnObject helpObj;
    private KrnObject processObj;
    private boolean isEditable = true;
    private boolean pdChanged;
    private KrnObject[] helpObjs;

    private static KrnAttribute blockedAttr;
    private static KrnAttribute developerAttr;
    private Element or3Rights;
    private Map<Long,String> titleMap=new HashMap<Long,String>();

    public UserNode() {
        this(Kernel.instance());
    }

    public UserNode(Kernel krn) {
        try {
            if (userCls == null)
                userCls = krn.getClassByName("User");
            if (developerAttr == null)
                developerAttr = krn.getAttributeByName(userCls, "developer");
            if (blockedAttr == null)
                blockedAttr = krn.getAttributeByName(userCls, "blocked");
        } catch (KrnException e) {
            e.printStackTrace();
        }

        isLoaded = true;
        krnObj = new KrnObject(0, "", 0);
    }

    public UserNode(KrnObject obj, String name, String pd, String sign, String sign_kz,
                    KrnObject baseStructure, KrnObject dataLang, KrnObject ifcLang,
                    KrnObject ifcObj, String doljnost, String email, boolean isEditor, boolean isAdmin,
                    boolean isDeveloper, boolean isBlocked, int index) {
        krnObj = obj;
        isLoaded = false;
        this.title = name;
        this.oldName = name;
        this.pd = pd;
        this.pdChanged = false;
        this.sign = sign != null ? sign : "";
        this.sign_kz = (sign_kz != null) ? sign_kz : "";
        this.baseStructureObj = baseStructure;
        this.dataLangObj = dataLang;
        this.ifcLangObj = ifcLang;
        this.ifcObj = ifcObj;
        this.doljnost = doljnost;
        this.email = email;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.isDeveloper = "sys_admin".equals(name) ? true : isDeveloper;
        this.isBlocked = isBlocked;
        final Kernel krn =  Kernel.instance();
        final long lang_ru =  krn.getLangIdByCode("RU");
        try{
            if(baseStructureObj!=null){
                String[] titles = krn.getStrings(baseStructureObj,"наименование",lang_ru,0);
                if(titles.length>0)
                    titleMap.put(baseStructureObj.id,titles[0]);
            }
            if(dataLangObj!=null){
                String[] titles = krn.getStrings(dataLangObj,"name",0,0);
                if(titles.length>0)
                    titleMap.put(dataLangObj.id,titles[0]);
            }
            if(ifcLangObj!=null){
                String[] titles = krn.getStrings(ifcLangObj,"name",0,0);
                if(titles.length>0)
                    titleMap.put(ifcLangObj.id,titles[0]);
            }
            if(ifcObj!=null){
                String[] titles = krn.getStrings(ifcObj,"title",lang_ru,0);
                if(titles.length>0)
                    titleMap.put(ifcObj.id,titles[0]);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if (!isLeaf()) {
            long[] oids = {obj.id};
            ObjectValue[] ovs = new ObjectValue[0];
            try {
                ovs = krn.getObjectValues(oids,obj.classId, "hyperMenu", 0);
                if (ovs.length > 0) {
                    hypers = new KrnObject[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        hypers[i] = ovs[i].value;
                    }
                }
                setItemsTitle(ovs,"title",lang_ru,krn);
                ovs = krn.getObjectValues(oids,obj.classId, "process", 0);
                if (ovs.length > 0) {
                        processObj = ovs[0].value;
                }
                setItemsTitle(ovs,"title",lang_ru,krn);
                ovs = new ObjectValue[0];

                try {
                    byte[] b = krn.getBlob(obj, "or3rights", 0, 0, 0);
                    if (b != null && b.length > 0) {
                        SAXBuilder builder = new SAXBuilder();
                        Document doc = builder.build(new ByteArrayInputStream(b),"UTF-8");
                        or3Rights = doc.getRootElement();
                    }
                } catch (JDOMException e) {
                    log.warn("Не могу прочитать права or3rights!");
                } catch (Exception e) {
                    log.warn("Свойство or3rights[blob] не найдено в классе UserFolder!");
                }
/*
                try {
                ovs = krn.getObjectValues(oids, obj.classId,"help",0);
                if (ovs.length >0) {
                    helpObj = ovs[0].value;
                }    } catch(Exception eee) {

                }
*/
                try {
                    ovs = krn.getObjectValues(oids, obj.classId, "helps",0);
                    if (ovs != null && ovs.length > 0) {
                        helpObjs = new KrnObject[ovs.length];
                        for (int k = 0; k<ovs.length; k++) {
                            helpObjs[k] = ovs[k].value;
                        }
                    } else {
                        helpObjs = null;
                    }
                    setItemsTitle(ovs,"title",lang_ru,krn);
                } catch(Exception eee) {
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isLeaf() {
        if (krnObj == null) return false;
        else return krnObj.classId == userCls.id;
    }




    public KrnObject getIfcObject() {
        return ifcObj;
    }


    public void rename(String newName) {
        title = newName;
        final Kernel krn = Kernel.instance();
        try {
            krn.setString(krnObj.id, krnObj.classId,
                    "name", 0, 0, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            final Kernel krn =  Kernel.instance();
            final long lang_ru =  krn.getLangIdByCode("RU");
            final long lang_kz =  krn.getLangIdByCode("KZ");
            if (!isLeaf()) {
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids,krnObj.classId, "hyperMenu", 0);
                    hypers = new KrnObject[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        hypers[i] = ovs[i].value;
                    }
                    ovs = krn.getObjectValues(oids,krnObj.classId, "process", 0);
                    if (ovs.length >0) {
                        processObj = ovs[0].value;
                    }
/*
                    try {
                    ovs = krn.getObjectValues(oids, krnObj.classId, "help", 0);
                    if (ovs.length >0) {
                        helpObj = ovs[0].value;
                    }    } catch(Exception asd) {

                    }
*/
                    try {
                        byte[] b = krn.getBlob(krnObj, "or3rights", 0, 0, 0);
                        if (b != null && b.length > 0) {
                            SAXBuilder builder = new SAXBuilder();
                            Document doc = builder.build(new ByteArrayInputStream(b),"UTF-8");
                            or3Rights = doc.getRootElement();
                        }
                    } catch (JDOMException e) {
                        log.warn("Не могу прочитать права or3rights!");
                    } catch (Exception e) {
                        log.warn("Свойство or3rights[blob] не найдено в классе UserFolder!");
                    }

                    try {
                        ovs = krn.getObjectValues(oids, krnObj.classId, "helps",0);
                        if (ovs != null && ovs.length > 0) {
                            helpObjs = new KrnObject[ovs.length];
                            for (int k = 0; k<ovs.length; k++) {
                                helpObjs[k] = ovs[k].value;
                            }
                        } else {
                            helpObjs = null;
                        }
                    } catch(Exception eee) {
                    }

                    ovs = krn.getObjectValues(oids,krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] names = krn.getStringValues(ids, krnObj.classId,
                            "name", 0, false, 0);
                    StringValue[] pds = krn.getStringValues(ids, krnObj.classId,
                            "password", 0, false, 0);
                    StringValue[] signs = krn.getStringValues(ids, krnObj.classId,
                            "sign", lang_ru, false, 0);
                    StringValue[] signs_kz = krn.getStringValues(ids, krnObj.classId,
                            "sign",lang_kz, false, 0);
                    ObjectValue[] bases = krn.getObjectValues(ids, krnObj.classId,
                            "base", 0);
                    ObjectValue[] dataLangs = krn.getObjectValues(ids, krnObj.classId,
                            "data language", 0);
                    ObjectValue[] ifcLangs = krn.getObjectValues(ids, krnObj.classId,
                            "interface language", 0);
                    ObjectValue[] ifcs = krn.getObjectValues(ids, krnObj.classId,
                            "interface", 0);
                    StringValue[] doljs = krn.getStringValues(ids, krnObj.classId,
                            "doljnost", Utils.getInterfaceLangId(krn), false, 0);
                    StringValue[] emails = null;
                    try {
                        emails = krn.getStringValues(ids, krnObj.classId,
                                "email", 0, false, 0);
                    } catch (Exception ex) {
                        log.warn("Не найден атрибут \"email\"");
                    }
                    LongValue[] editor = krn.getLongValues(ids, krnObj.classId,
                            "editor", 0);
                    LongValue[] admin = krn.getLongValues(ids, krnObj.classId,
                            "admin", 0);
                    LongValue[] devs = null;
                    if (developerAttr != null) {
                    	devs = krn.getLongValues(ids, developerAttr, 0);
                    }
                    LongValue[] blocks = null;
                    if (blockedAttr != null) {
                    	blocks = krn.getLongValues(ids, blockedAttr, 0);
                    }
                    Arrays.sort(ovs, new Comparator<ObjectValue>() {
                        public int compare(ObjectValue ov1, ObjectValue ov2) {
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                return (ov1.index < ov2.index) ? -1 : 1;
                            }
                        }
                    });
                    List children = new ArrayList();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String login = "Безымянный";
                        for (int j = 0; j < names.length; j++) {
                            if (names[j].objectId == ov.value.id) {
                                login = names[j].value;
                                break;
                            }
                        }
                        String pd = "";
                        for (int j = 0; j < pds.length; j++) {
                            if (pds[j].objectId == ov.value.id) {
                                pd = pds[j].value;
                                break;
                            }
                        }
                        String dolj = "";
                        for (int j = 0; j < doljs.length; j++) {
                            if (doljs[j].objectId == ov.value.id) {
                                dolj = doljs[j].value;
                                break;
                            }
                        }
                        String email = "";
                        if (emails != null) {
                            for (int j = 0; j < emails.length; j++) {
                                if (emails[j].objectId == ov.value.id) {
                                    email = emails[j].value;
                                    break;
                                }
                            }
                        }
                        String signS = "";
                        for (int j = 0; j < signs.length; j++) {
                            if (signs[j].objectId == ov.value.id) {
                                signS = signs[j].value;
                                break;
                            }
                        }
                        String signS_ = "";
                        for (int j = 0; j < signs_kz.length; j++) {
                            if (signs_kz[j].objectId == ov.value.id) {
                                signS_ = signs_kz[j].value;
                                break;
                            }
                        }
                        KrnObject baseObject = null;
                        for (int j = 0; j < bases.length; j++) {
                            if (bases[j].objectId == ov.value.id) {
                                baseObject = bases[j].value;
                            }
                        }
                        KrnObject dataLang = null;
                        for (int j = 0; j < dataLangs.length; j++) {
                            if (dataLangs[j].objectId == ov.value.id) {
                                dataLang = dataLangs[j].value;
                            }
                        }
                        KrnObject ifcLang = null;
                        for (int j = 0; j < ifcLangs.length; j++) {
                            if (ifcLangs[j].objectId == ov.value.id) {
                                ifcLang = ifcLangs[j].value;
                            }
                        }
                        KrnObject ifcObject = null;
                        for (int j = 0; j < ifcs.length; j++) {
                            if (ifcs[j].objectId == ov.value.id) {
                                ifcObject = ifcs[j].value;
                            }
                        }
                        long edt = 0;
                        for (int j = 0; j < editor.length; j++) {
                            if (editor[j].objectId == ov.value.id) {
                                edt = editor[j].value;
                            }
                        }
                        long adm = 0;
                        for (int j = 0; j < admin.length; j++) {
                            if (admin[j].objectId == ov.value.id) {
                                adm = admin[j].value;
                            }
                        }
                        boolean isDeveloper = true;
                        if (devs != null) {
                            isDeveloper = false;
	                        for (int j = 0; j < devs.length; j++) {
	                            if (devs[j].objectId == ov.value.id) {
	                                isDeveloper = devs[j].value == 1;
	                                break;
	                            }
	                        }
                    	}
                        boolean isBlocked = false;
                        if (blocks != null) {
	                        for (int j = 0; j < blocks.length; j++) {
	                            if (blocks[j].objectId == ov.value.id) {
	                                isBlocked = blocks[j].value == 1;
	                                break;
	                            }
	                        }
                        }
                        children.add(new UserNode(ov.value, login, pd, signS, signS_,baseObject,
                                dataLang, ifcLang, ifcObject, dolj, email,
                                (edt != 0) ? true : false,
                                (adm != 0) ? true : false, isDeveloper, isBlocked, ov.index));
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public void setIfcObject(KrnObject obj) {
        ifcObj = obj;
    }

    public KrnObject getBaseStructureObj() {
        return baseStructureObj;
    }

    public void setBaseStructureObj(KrnObject baseStructureObj) {
        this.baseStructureObj = baseStructureObj;
    }

    public KrnObject getDataLangObj() {
        return dataLangObj;
    }

    public void setDataLangObj(KrnObject dataLangObj) {
        this.dataLangObj = dataLangObj;
    }

    public KrnObject getIfcLangObj() {
        return ifcLangObj;
    }

    public void setIfcLangObj(KrnObject ifcLangObj) {
        this.ifcLangObj = ifcLangObj;
    }

    public String getName() {
        return title;
    }

    public int setName(String name) {
        if (title.equals(name)) {
            return PROPERTY_NOT_CHANGED;
        }
        PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode(Kernel.instance());
        if (pnode != null) {
            if (name.length() < pnode.getPolicyWrapper().getMinLoginLength()) {
                return LOGIN_TOO_SHORT;
            }
            if (name.length() > pnode.getPolicyWrapper().getMaxLengthLogin()) {
                return LOGIN_TOO_LONG;
            }
        }
        title = name;
        return PROPERTY_CHANGED;
    }

    public String getPassword() {
        return pd;
    }

    public int setPassword(String pd) {
    	if (pd == null || pd.isEmpty()) {
    	    return PROPERTY_NOT_CHANGED;
    	}
        String newPD = PasswordService.getInstance().encrypt(pd);
        if (pd.equals(newPD)) {
            return PROPERTY_NOT_CHANGED;
        }
        PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode(Kernel.instance());
        if (pnode != null) {
            if (pd.length() < pnode.getPolicyWrapper().getMinPasswordLength()) {
                return PASSWORD_TOO_SHORT;
            }
            if (pd.length() > pnode.getPolicyWrapper().getMaxLengthPass()) {
                return PASSWORD_TOO_LONG;
            }
        }
        pd = newPD;
        pdChanged = true;
        return PROPERTY_CHANGED;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignKz() {
        return sign_kz;
    }
    public void setSignKz(String sign_kz) {
        this.sign_kz = sign_kz;
    }

    public String getDoljnost() {
        return doljnost;
    }

    public void setDoljnost(String doljnost) {
        this.doljnost = doljnost;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public KrnObject[] getHypers() {
        return hypers;
    }

/*
    public KrnObject getHelp() {
        return helpObj;
    }
*/

    public KrnObject[] getHelp() {
        return helpObjs;
    }

    public void setHypers(KrnObject[] hypers) {
        this.hypers = hypers;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isDeveloper() {
		return isDeveloper;
	}

	public void setDeveloper(boolean isDeveloper) {
		this.isDeveloper = isDeveloper;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

/*
    public void setHelp(KrnObject object) {
        helpObj = object;
    }
*/
    public void setHelp(KrnObject[] objs) {
        helpObjs = objs;
    }

    public KrnObject getProcess() {
        return processObj;
    }
    public void setProcess(KrnObject object) {
        processObj = object;
    }

    public boolean findRootUserByName(String name) {
    	int count = getChildCount();
    	for (int i = 0; i < count; i++) {
            if (name.equals(getChildAt(i).toString())) {
                return true;
            }
    	}
        return false;
    }

    public void checkAccess(long baseId, List nodes) {
        for (int i = getChildCount() - 1; i>=0; i--) {
            UserNode node = (UserNode) getChildAt(i);
            if (node.isLeaf()) {
                if ((node.getBaseStructureObj() != null &&
                     node.getBaseStructureObj().id != baseId) ||
                     nodes.contains(node.getKrnObj().id))
                            remove(i);
            } else {
                node.checkAccess(baseId, nodes);
                if (node.getChildCount() == 0)
                    remove(i);
            }
        }
    }

    public void removeDeveloperUsers() {
        for (int i = getChildCount() - 1; i>=0; i--) {
            UserNode node = (UserNode) getChildAt(i);
            if (node.isLeaf()) {
                if (node.isDeveloper)
                	remove(i);
            } else {
                node.removeDeveloperUsers();
            }
        }
    }

    public void getVIPUsers(String name, List nodes) {
        List res = new ArrayList();
        for (int i = getChildCount() - 1; i>=0; i--) {
            UserNode node = (UserNode) getChildAt(i);
            if (node.isLeaf()) {
                String nodeName = node.toString();
                if (name.equals(nodeName))
                    return;
                else
                    res.add(node.getKrnObj().id);
            }
        }
        nodes.addAll(res);
        setEditable(false);
        for (int i = getChildCount() - 1; i>=0; i--) {
            UserNode node = (UserNode) getChildAt(i);
            if (!node.isLeaf()) {
                node.getVIPUsers(name, nodes);
            }
        }
    }

    private void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void saveUser() {
        Kernel krn = Kernel.instance();
        try {
            final long lang_ru =  krn.getLangIdByCode("RU");
            final long lang_kz =  krn.getLangIdByCode("KZ");

            KrnObject o = getKrnObj();
            krn.setString(o.id, o.classId, "name", 0, 0, toString(), 0);

            if (pdChanged) {
                krn.setString(o.id, o.classId, "password", 0, 0, getPassword(), 0);
                PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode(Kernel.instance());
                if (pnode != null && pnode.getPolicyWrapper().getMaxValidPeriod() > 0) {
                    try {
                        ClassNode cls = krn.getClassNode(o.classId);
                        KrnAttribute attr = cls.getAttribute("дата истечения срока действия пароля");
                        Calendar c = new GregorianCalendar();
                        c.add(Calendar.DAY_OF_MONTH, (int)pnode.getPolicyWrapper().getMaxValidPeriod());
                        krn.setDate(o.id, attr.id, 0, c.getTime(), 0);
                    } catch (Exception e) {
                        log.warn("Не найден атрибут \"дата истечения срока действия пароля\"");
                    }
                } else {
                    try {
                        krn.deleteValue(o.id, o.classId, "дата истечения срока действия пароля",
                                        new int[]{0}, 0);
                    } catch (Exception e) {
                        log.warn("Не найден атрибут \"дата истечения срока действия пароля\"");
                    }
                }
            }
            krn.setString(o.id, o.classId, "sign", 0, lang_ru, getSign(), 0);
            krn.setString(o.id, o.classId, "sign", 0, lang_kz, getSignKz(), 0);
            krn.setString(o.id, o.classId, "doljnost", 0,
                    com.cifs.or2.client.Utils.getInterfaceLangId(),
                    getDoljnost(), 0);

            try {
                krn.setString(o.id, o.classId, "email", 0, 0, getEmail(), 0);
            } catch (Exception e) {
                log.warn("Не найден атрибут \"email\"");
            }
            KrnObject baseObj = getBaseStructureObj();
            if (baseObj != null) {
                krn.setObject(o.id, o.classId, "base", 0,
                        baseObj.id, 0, false);
            } else {
                krn.deleteValue(o.id, o.classId, "base", new int[] {0}, 0);
            }
            KrnObject dataLangObj = getDataLangObj();
            if (dataLangObj != null) {
                krn.setObject(o.id, o.classId, "data language", 0,
                        dataLangObj.id, 0, false);
            }
            KrnObject ifcLangObj = getIfcLangObj();
            if (ifcLangObj != null) {
                krn.setObject(o.id, o.classId, "interface language", 0,
                        ifcLangObj.id, 0, false);
            }
            KrnObject userObj = getIfcObject();
            if (userObj != null) {
                krn.setObject(o.id, o.classId, "interface", 0,
                        getIfcObject().id, 0, false);
            } else {
                krn.deleteValue(o.id, o.classId, "interface", new int[] {0}, 0);
            }
            krn.setLong(o.id, o.classId, "editor", 0,
                    (isEditor()) ? 1 : 0, 0);
            krn.setLong(o.id, o.classId, "admin", 0,
                    (isAdmin()) ? 1 : 0, 0);

            if (blockedAttr != null) {
            	long[] ls = krn.getLongs(o, blockedAttr, 0);
            	if (ls != null && ls.length > 0 && ls[0] == 1) {
            		if (!isBlocked()) {
            			krn.userUnblocked(toString());
            		}
            	} else {
            		if (isBlocked()) {
            			krn.userBlocked(toString());
            		}
            	}
	            krn.setLong(o.id, blockedAttr.id, 0, isBlocked() ? 1 : 0, 0);

            }

            krn.updateUser(o, toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Element getOr3Rights() {
        return or3Rights;
    }

    public void setOr3Rights(Element element) {
        this.or3Rights = element;
    }
    public String getItemTitle(KrnObject obj){
        return titleMap.get(obj.id);
    }
    public String setItemTitle(KrnObject obj,String title){
        return titleMap.put(obj.id,title);
    }
    private void setItemsTitle(ObjectValue[]ovs,String attrName,long lang,Kernel krn) throws KrnException{
        if(ovs.length>0){
            long[] ids = new long[ovs.length];
            for (int i = 0; i < ovs.length; i++) {
                ids[i] = ovs[i].value.id;
            }
            StringValue[] titles = krn.getStringValues(ids, ovs[0].value.classId,attrName,lang, false, 0);
            for(StringValue sv:titles){
                titleMap.put(sv.objectId,sv.value);
            }
        }

    }
    private void setItemsTitle(KrnObject obj,String attrName,long lang,Kernel krn) throws KrnException{
        if(obj!=null){
            String[] titles = krn.getStrings(obj,attrName,lang,0);
            if(titles.length>0)
                titleMap.put(obj.id,titles[0]);
        }

    }
}