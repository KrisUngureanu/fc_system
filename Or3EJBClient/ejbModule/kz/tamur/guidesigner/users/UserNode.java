package kz.tamur.guidesigner.users;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;

import java.util.*;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.PasswordService;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import static com.cifs.or2.client.Kernel.SC_USER;
import static com.cifs.or2.client.Kernel.SC_USER_FOLDER;
import static com.cifs.or2.client.Kernel.SC_CONFIG_LOCAL;
import static com.cifs.or2.client.Kernel.SC_BASE;
import static com.cifs.or2.client.Kernel.SC_LANGUAGE;
import static com.cifs.or2.client.Kernel.SC_UI;
import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF;
import static com.cifs.or2.client.Kernel.SC_NOTE;
import static com.cifs.or2.client.Kernel.SC_HIPERTREE;
import static kz.tamur.rt.Utils.toBoolean;

/**
 * Created by IntelliJ IDEA. User: Vital Date: 13.10.2004 Time: 11:31:34 To
 * change this template use File | Settings | File Templates.
 */
public class UserNode extends AbstractDesignerTreeNode {
    public static final int PROPERTY_NOT_CHANGED = 0;
    public static final int PROPERTY_CHANGED = 2;
    public static final int LOGIN_TOO_SHORT = 3;

    private KrnObject baseStructureObj;
    private KrnObject dataLangObj;
    private KrnObject ifcLangObj;
    private KrnObject ifcObj;
    private KrnObject configObj;
    private String pd;
    private String sign;
    private String sign_kz;
    private String doljnost;
    private String email;
    private String ip_address;
    private KrnObject[] hypers;
    private boolean isEditor = false;
    /** необходимо ли отображать монитор заданий пользователю */
    private int monitorTask = 1;
    /** Флаг отображения панели задач у пользоватля в WEB интерфейсе. */
    private int toolBar = 1;
    
    private boolean isAdmin = false;
    private boolean isAdded = false;
    private boolean isDeveloper = true;
    private boolean isBlocked = false;
    private boolean isMulti = false;
    private String iin;
    private boolean isOnlyECP = false;
    private boolean isModified = false;
    // private KrnObject helpObj;
    private KrnObject processObj;
    private boolean isEditable = true;
    private boolean pdChanged;
    private KrnObject[] helpObjs;

    private static KrnAttribute blockedAttr;
    private static KrnAttribute developerAttr;
    private static KrnAttribute multiAttr;
    private static KrnAttribute emailAttr;
    private static KrnAttribute iinAttr;
    private static KrnAttribute ipAddressAttr;
    private static KrnAttribute onlyECPAttr;
    private static KrnAttribute activeAttr;

    private static KrnAttribute isToolBarAttr;

    private static KrnAttribute config;
    private Element or3Rights;
    private static Map<Long, String> allTitleMap_ = new HashMap<Long, String>();
    private Comparator AllStringComparator = new AllStringComparator();
    private String pdOriginal;
    protected static Kernel krn;
    final static long lang_ru;
    final static long lang_kz;
    
    private Version version;
    private boolean isNewNode = false;

    static {
        krn = Kernel.instance();
        developerAttr = krn.getAttributeByNameTracing(SC_USER, "developer");
        blockedAttr = krn.getAttributeByNameTracing(SC_USER, "blocked");
        multiAttr = krn.getAttributeByNameTracing(SC_USER, "multi");
        emailAttr = krn.getAttributeByNameTracing(SC_USER, "email");
        ipAddressAttr = krn.getAttributeByNameTracing(SC_USER, "ip_address");
        iinAttr = krn.getAttributeByNameTracing(SC_USER, "iin");
        onlyECPAttr = krn.getAttributeByNameTracing(SC_USER, "onlyECP");
        activeAttr = krn.getAttributeByNameTracing(SC_USER, "activated");
        isToolBarAttr = krn.getAttributeByNameTracing(SC_CONFIG_LOCAL, "isToolBar");
        config = krn.getAttributeByNameTracing(SC_USER, "config");
        lang_ru = krn.getLangIdByCode("RU");
        lang_kz = krn.getLangIdByCode("KZ");
    }

    class AllStringComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            AbstractDesignerTreeNode n1 = (AbstractDesignerTreeNode) o1;
            AbstractDesignerTreeNode n2 = (AbstractDesignerTreeNode) o2;
            if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else if (n1.isLeaf() && !n2.isLeaf()) {
                return 1;
            } else if (!n1.isLeaf() && n2.isLeaf()) {
                return -1;
            } else {
                return n1.toString().compareTo(n2.toString());
            }
        }
    }

    public UserNode() {
        isLoaded = true;
        krnObj = new KrnObject(0, "", 0);
        title = "";
    }

    /**
     * Создание нового узла с пользователем
     *
     * @param obj объект - пользователь
     * @param name имя
     * @param pd пароль
     * @param sign the sign
     * @param sign_kz the sign_kz
     * @param baseStructure the base structure
     * @param dataLang the data lang
     * @param ifcLang the ifc lang
     * @param ifcObj the ifc obj
     * @param doljnost должность
     * @param email  email
     * @param ip_address ip адрес
     * @param isEditor the is editor
     * @param isAdmin пользователь администратор?
     * @param isDeveloper пользователь разработчик?
     * @param isBlocked пользователь блокирован?
     * @param isMulti the is multi
     * @param iin the iin
     * @param isOnlyECP the is only ecp
     * @param config конфигурация пользователя
     * @param index the index
     */
    public UserNode(KrnObject obj, String name, String pd, String sign, String sign_kz, KrnObject baseStructure,
            KrnObject dataLang, KrnObject ifcLang, KrnObject ifcObj, String doljnost, String email, String ip_address,
            boolean isEditor, boolean isAdmin, boolean isDeveloper, boolean isBlocked, boolean isMulti, String iin,
            boolean isOnlyECP, KrnObject config, int monitorTask, int toolBar, 
            KrnObject[] hypers, KrnObject processObj, Element or3Rights, KrnObject[] helpObjs, int index, boolean isNewNode) {
    	nodeType = USER_NODE;
        krnObj = obj;
        isLoaded = false;
        title = name;
        oldName = name;
        this.pd = pd;
        this.pdChanged = false;
        this.sign = sign != null ? sign : "";
        this.sign_kz = (sign_kz != null) ? sign_kz : "";
        baseStructureObj = baseStructure;
        dataLangObj = dataLang;
        ifcLangObj = ifcLang;
        this.ifcObj = ifcObj;
        this.doljnost = doljnost;
        this.email = email;
        this.ip_address = ip_address;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.isMulti = isMulti;
        this.isDeveloper = "sys_admin".equals(name) || isDeveloper;
        this.isBlocked = isBlocked;
        this.iin = iin;
        this.isOnlyECP = isOnlyECP;
        if (config == null) {
            createConfigObj();
        } else {
            configObj = config;
            this.monitorTask = monitorTask;
            this.toolBar = toolBar;
        }

        if (!isLeaf()) {
    		this.hypers = hypers;
        	this.processObj = processObj;
        	this.or3Rights = or3Rights;
    		this.helpObjs = helpObjs;
        }
        
        version = new Version(this);
        this.isNewNode = isNewNode;
    }

    
    public boolean isLeaf() {
        return krnObj == null ? false : krnObj.classId == SC_USER.id;
    }

    public KrnObject getIfcObject() {
        return ifcObj;
    }

    public void rename(String newName) {
        title = newName;
        final Kernel krn = Kernel.instance();
        try {
            krn.setString(krnObj.id, krnObj.classId, "name", 0, 0, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                try {
                	ObjectValue[] childValues = krn.getObjectValues(new long[]{krnObj.id}, krnObj.classId, "children", 0);
                    long[] ids = new long[childValues.length];
                    for (int i = 0; i < childValues.length; i++) {
                        ids[i] = childValues[i].value.id;
                    }
                    
                    AttrRequestBuilder arb = new AttrRequestBuilder(SC_USER_FOLDER, krn).add("name").add("password")
                            .add("sign", lang_ru).add("sign", lang_kz).add("base").add("data language").add("interface language")
                            .add("interface").add("doljnost").add("email");

                    if (ipAddressAttr != null) {
                        arb.add("ip_address");
                    }
                    arb.add("editor").add("admin");

                    if (developerAttr != null) {
                        arb.add(developerAttr.name);
                    }
                    if (blockedAttr != null) {
                        arb.add(blockedAttr.name);
                    }
                    if (multiAttr != null) {
                        arb.add(multiAttr.name);
                    }
                    if (iinAttr != null) {
                        arb.add(iinAttr.name);
                    }
                    if (onlyECPAttr != null) {
                        arb.add(onlyECPAttr.name);
                    }

                    arb.add("config").add("process").add("or3rights").add("hyperMenu").add("helps");

                    List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
                    
                    final Map<Long, String> logins_ = new HashMap<Long, String>();
                    final Map<Long, String> pds_ = new HashMap<Long, String>();
                    final Map<Long, String> signs_ = new HashMap<Long, String>();
                    final Map<Long, String> signsK_ = new HashMap<Long, String>();
                    final Map<Long, KrnObject> bases_ = new HashMap<Long, KrnObject>();
                    final Map<Long, KrnObject> dataLangs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, KrnObject> ifcLangs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, KrnObject> ifcs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, String> doljs_ = new HashMap<Long, String>();
                    final Map<Long, String> emails_ = new HashMap<Long, String>();
                    final Map<Long, String> ips_ = new HashMap<Long, String>();
                    final Map<Long, Boolean> editors_ = new HashMap<Long, Boolean>();
                    final Map<Long, Boolean> admins_ = new HashMap<Long, Boolean>();
                    final Map<Long, Boolean> devs_ = new HashMap<Long, Boolean>();
                    final Map<Long, Boolean> blocks_ = new HashMap<Long, Boolean>();
                    final Map<Long, Boolean> multis_ = new HashMap<Long, Boolean>();
                    final Map<Long, String> iins_ = new HashMap<Long, String>();
                    final Map<Long, Boolean> onlyECPs_ = new HashMap<Long, Boolean>();
                    final Map<Long, KrnObject> configs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, KrnObject> processObjs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, Element> or3Rights_ = new HashMap<Long, Element>();
                    Map<Long, List<KrnObject>> hypers_ = new HashMap<Long, List<KrnObject>>();
                    Map<Long, List<KrnObject>> helpObjs_ = new HashMap<Long, List<KrnObject>>();

                    List<Long> bids = new ArrayList<Long>();
                    List<Long> lids = new ArrayList<Long>();
                    List<Long> iids = new ArrayList<Long>();
                    List<Long> cids = new ArrayList<Long>();
                    List<Long> pids = new ArrayList<Long>();
                    List<Long> hids = new ArrayList<Long>();
                    List<Long> hlids = new ArrayList<Long>();
                    
                    for (Object[] row : rows) {
                    	KrnObject obj = (KrnObject)row[0];
                    	
                    	if (row[2] != null)
                    		logins_.put(obj.id, (String)row[2]);
                    	if (row[3] != null)
                    		pds_.put(obj.id, (String)row[3]);
                    	if (row[4] != null)
                    		signs_.put(obj.id, (String)row[4]);
                    	if (row[5] != null)
                    		signsK_.put(obj.id, (String)row[5]);
                    	if (row[6] != null) {
                    		KrnObject base = (KrnObject)row[6];
                    		bases_.put(obj.id, base);

                    		if (!bids.contains(base.id) && !allTitleMap_.containsKey(base.id))
                    			bids.add(base.id);
                    	}
                    	if (row[7] != null) {
                    		KrnObject lang = (KrnObject)row[7];
                    		dataLangs_.put(obj.id, lang);

                    		if (!lids.contains(lang.id) && !allTitleMap_.containsKey(lang.id))
                    			lids.add(lang.id);
                    	}
                    	if (row[8] != null) {
                    		KrnObject lang = (KrnObject)row[8];
                    		ifcLangs_.put(obj.id, lang);

                    		if (!lids.contains(lang.id) && !allTitleMap_.containsKey(lang.id))
                    			lids.add(lang.id);
                    	}
                    	if (row[9] != null) {
                    		KrnObject lang = (KrnObject)row[9];
                    		ifcs_.put(obj.id, lang);

                    		if (!iids.contains(lang.id) && !allTitleMap_.containsKey(lang.id))
                    			iids.add(lang.id);
                    	}
                    	if (row[10] != null)
                    		doljs_.put(obj.id, (String)row[10]);
                        
                    	int count = 10;

                        if (emailAttr != null) {
                            if (row[++count] != null)
                                emails_.put(obj.id, (String)row[count]);
                        }
                    	if (ipAddressAttr != null) {
                    		if (row[++count] != null)
                    			ips_.put(obj.id, (String)row[count]);
                    	}
                    	if (row[++count] != null)
                    		editors_.put(obj.id, (Boolean)row[count]);
                    	if (row[++count] != null)
                    		admins_.put(obj.id, (Boolean)row[count]);
                    	if (developerAttr != null) {
	                    	if (row[++count] != null)
	                    		devs_.put(obj.id, (Boolean)row[count]);
                    	}
                    	if (blockedAttr != null) {
	                    	if (row[++count] != null)
	                    		blocks_.put(obj.id, (Boolean)row[count]);
                    	}
                    	if (multiAttr != null) {
	                    	if (row[++count] != null)
	                    		multis_.put(obj.id, (Boolean)row[count]);
                    	}
                    	if (iinAttr != null) {
	                    	if (row[++count] != null)
	                    		iins_.put(obj.id, (String)row[count]);
                    	}
                    	if (onlyECPAttr != null) {
	                    	if (row[++count] != null)
	                    		onlyECPs_.put(obj.id, (Boolean)row[count]);
                    	}
                        if (row[++count] != null) {
                            KrnObject c = (KrnObject) row[count];
                            configs_.put(obj.id, c);

                            if (!cids.contains(c.id))
                                cids.add(c.id);
                        }
                    	if (row[++count] != null) {
                    		KrnObject proc = (KrnObject)row[count];
                    		processObjs_.put(obj.id, proc);

                    		if (!pids.contains(proc.id) && !allTitleMap_.containsKey(proc.id))
                    			pids.add(proc.id);
                    	}
                    	
                    	if (row[++count] != null) {
                    		byte[] b = (byte[])row[count];
                    		
                            try {
                                if (b.length > 0) {
                                    SAXBuilder builder = new SAXBuilder();
                                    Document doc = builder.build(new ByteArrayInputStream(b), "UTF-8");
                                    Element or3Rights = doc.getRootElement();
                                    or3Rights_.put(obj.id, or3Rights);
                                }
                            } catch (JDOMException e) {
                                System.out.println("Не могу прочитать права or3rights!");
                            } catch (Exception e) {
                                System.out.println("Свойство or3rights[blob] не найдено в классе UserFolder!");
                            }
                    	}
                    	
                    	if (row[++count] != null) {
                    		List<Value> ovs = (List<Value>)row[count];
                            for (Value ov : ovs) {
                            	KrnObject hyper = (KrnObject)ov.value;
                            	List<KrnObject> arr = hypers_.get(obj.id);
                            	if (arr == null) {
                            		arr = new ArrayList<KrnObject>();
                                	hypers_.put(obj.id, arr);
                            	}
                            	arr.add(hyper);

                            	if (!hids.contains(hyper.id) && !allTitleMap_.containsKey(hyper.id))
                        			hids.add(hyper.id);
                            }
                    	}
                    	if (row[++count] != null) {
                    		List<Value> ovs = (List<Value>)row[count];
                            for (Value ov : ovs) {
                            	KrnObject help = (KrnObject)ov.value;
                            	List<KrnObject> arr = helpObjs_.get(obj.id);
                            	if (arr == null) {
                            		arr = new ArrayList<KrnObject>();
                                	helpObjs_.put(obj.id, arr);
                            	}
                            	arr.add(help);

                            	if (!hlids.contains(help.id) && !allTitleMap_.containsKey(help.id))
                        			hlids.add(help.id);
                            }
                    	}
                    }
                    
                    if (bids.size() > 0) {
                    	long[] baseIds = kz.tamur.rt.Utils.toIdsArray(bids);
                    	
                    	setItemsTitle(baseIds, SC_BASE.id, "наименование", lang_ru, krn);
                    }
                    if (lids.size() > 0) {
                    	long[] langIds = kz.tamur.rt.Utils.toIdsArray(lids);
                    	
                    	setItemsTitle(langIds, SC_LANGUAGE.id, "name", lang_ru, krn);
                    }
                    if (iids.size() > 0) {
                    	long[] ifcIds = kz.tamur.rt.Utils.toIdsArray(iids);
                    	
                    	setItemsTitle(ifcIds, SC_UI.id, "title", lang_ru, krn);
                    }
                    if (pids.size() > 0) {
                    	long[] procIds = kz.tamur.rt.Utils.toIdsArray(pids);
                    	
                    	setItemsTitle(procIds, SC_PROCESS_DEF.id, "title", lang_ru, krn);
                    }
                    if (hids.size() > 0) {
                    	long[] hyperIds = kz.tamur.rt.Utils.toIdsArray(hids);
                    	
                    	setItemsTitle(hyperIds, SC_HIPERTREE.id, "title", lang_ru, krn);
                    }
                    if (hlids.size() > 0) {
                    	long[] helpIds = kz.tamur.rt.Utils.toIdsArray(hlids);
                    	
                    	setItemsTitle(helpIds, SC_NOTE.id, "title", lang_ru, krn);
                    }

                    Map<Long, Integer> monitors_ = new HashMap<Long, Integer>();
                    Map<Long, Integer> toolbars_ = new HashMap<Long, Integer>();

                    if (cids.size() > 0) {
                    	long[] confIds = kz.tamur.rt.Utils.toIdsArray(cids);
                    	
                        AttrRequestBuilder arb2 = new AttrRequestBuilder(SC_CONFIG_LOCAL, krn).add("isMonitor");
                        if (isToolBarAttr != null)
                        	arb2.add("isToolBar");

                        List<Object[]> rows2 = krn.getObjects(confIds, arb2.build(), 0);
                        
                        for (Object[] row2 : rows2) {
                        	KrnObject obj = (KrnObject)row2[0];
                        	
                        	if (row2[2] != null)
                        		monitors_.put(obj.id, ((Number)row2[2]).intValue());
                        	if (isToolBarAttr != null && row2[3] != null)
                        		toolbars_.put(obj.id, ((Number)row2[3]).intValue());
                        }
                    }

                    List<UserNode> children = new ArrayList<UserNode>();
                    for (int i = 0; i < childValues.length; i++) {
                        ObjectValue ov = childValues[i];
                        long id = ov.value.id;
                        String login = logins_.get(id);
                        if (login == null) {
                            login = "Безымянный";
                        }
                        String pd = pds_.get(id);
                        if (pd == null)
                        	pd = "";
                        String dolj = doljs_.get(id);
                        if (dolj == null)
                            dolj = "";
                        String email = emails_.get(id);
                        if (email == null)
                            email = "";
                        String ip_address = ips_.get(id);
                        if (ip_address == null)
                            ip_address = "";
                        String signS = signs_.get(id);
                        if (signS == null)
                            signS = "";
                        String signS_ = signsK_.get(id);
                        if (signS_ == null)
                            signS_ = "";
                        KrnObject baseObject = bases_.get(id);
                        KrnObject dataLang = dataLangs_.get(id);
                        KrnObject ifcLang = ifcLangs_.get(id);
                        KrnObject ifcObject = ifcs_.get(id);
                        
                        Boolean b = editors_.get(id);
                        final boolean edt = b != null ? b : false;
                        
                        b = admins_.get(id);
                        final boolean adm = b != null ? b : false;
                        
                        b = devs_.get(id);
                        final boolean isDeveloper = b != null ? b : false;
                        
                        b = blocks_.get(id);
                        final boolean isBlocked = b != null ? b : false;
                        
                        b = multis_.get(id);
                        final boolean isMulti = b != null ? b : false;

                        String iin = iins_.get(id);
                        
                        b = onlyECPs_.get(id);
                        final boolean isOnlyECP = b != null ? b : false;
                        
                        KrnObject configObject = null;
                        int monitorTask = 0;
                        int toolBar = 0;
                        try {
                            configObject = configs_.get(id);
                            Integer k = monitors_.get(configObject.id);
                            monitorTask = k != null ? k : 0;
                            k = toolbars_.get(configObject.id);
                            toolBar = k != null ? k : 0;
                        } catch (Exception e) {
                        }
                        List<KrnObject> temp = hypers_.get(id);
                        KrnObject[] hypers = null;
                    	if (temp != null)
                    		hypers = temp.toArray(new KrnObject[temp.size()]);

                        KrnObject processObj = processObjs_.get(id);
                        Element or3Rights = or3Rights_.get(id);
                        
                        temp = helpObjs_.get(id);
                        KrnObject[] helpObjs = null;
                    	if (temp != null)
                    		helpObjs = temp.toArray(new KrnObject[temp.size()]);

                        if (Kernel.instance().getUser().isDeveloper() ||(!"sys_admin".equals(login) && !isDeveloper) ) {
                        	UserNode childNode = new UserNode(ov.value, login, pd, signS, signS_, baseObject, dataLang, ifcLang,
                                    ifcObject, dolj, email, ip_address, edt, adm,
                                    isDeveloper, isBlocked, isMulti, iin, isOnlyECP, configObject, monitorTask, toolBar,
                                    hypers, processObj, or3Rights, helpObjs, ov.index, false);
                        	TreeUIDMap.put(((KrnObject) ov.value).uid, childNode);
                        	if(childNode != null) {
                        		children.add(childNode);
                        	}
                            
                        }
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addAllChildren(List children) {
        // сортировка в алфавитном порядке всех потомков
        Collections.sort(children, AllStringComparator);
        for (int i = 0; i < children.size(); i++) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode) children.get(i);
            add(node);
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
        if (this.title.equals(name))
            return PROPERTY_NOT_CHANGED;
        PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode();
        if (pnode != null) {
            if (name.length() < pnode.getPolicyWrapper().getMinLoginLength()) {
                return LOGIN_TOO_SHORT;
            }
        }
        this.title = name;
        return PROPERTY_CHANGED;
    }

    public String getPassword() {
        return pd;
    }

    /**
     * Верификация пароля по новой политике безопасности
     * 
     * @param ipd
     *            пароль, для верификации
     * @return информационное сообщение, если верификация не пройдена, или NULL
     *         если все в порядке
     */
    public String setPassword(String ipd) {
        pdChanged = false;
        // хеширование нового пароля
        String newPD = PasswordService.getInstance().encrypt(ipd);
        if (((ipd == null || ipd.length() == 0)) || ipd.equals("*******") || pd.equals(newPD)) {
            return "NOT_CHANGED";
        }
        String mess = null;
        Kernel krn = Kernel.instance();
        KrnObject o = getKrnObj();
        String[] userName = null;
        long[] isAdmin = null;
        String[] prevPD = null;
        try {
            krn.setString(o.id, o.classId, "name", 0, 0, toString(), 0);
            userName = krn.getStrings(o, "name", 0, 0);
            isAdmin = krn.getLongs(o, "admin", 0);
            prevPD = krn.getStrings(o, "previous passwords", 0, 0);
        } catch (Exception e) {
            kz.tamur.rt.Utils.outErrorCreateAttrUser("previous passwords");
        }
        PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode();
        mess = pnode.getPolicyWrapper().verificationPassAndLogin(o, ipd.toCharArray(), userName[0], toBoolean(isAdmin[0]), false, new Locale("ru"));
        String pds[] = null;
        long k = 0;
        String pdsw = null;
        if (mess == null) {
            // сравнение пароля с предыдущими (полное соответствие)
            if (prevPD != null && prevPD.length != 0 && !prevPD[0].isEmpty()) {
            	pdsw = prevPD[0];
                pds = pdsw.split(";");
                k = (toBoolean(isAdmin[0])) ? pnode.getPolicyWrapper().getNumPassDublAdmin() : pnode.getPolicyWrapper().getNumPassDubl();
                // если "ноль", то не применять
                if (k > 0) {
                    for (String pd : pds) {
                        if (pd.equals(newPD)) {
                            return "Пароль не должен повторять " + k + " предыдущих паролей!";
                        }
                    }
                }
            }
            pd = newPD;
            pdOriginal = ipd;
            pdChanged = true;
        }
        return mess;
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

    public int getMonitor() {
        return monitorTask;
    }

    public boolean isMonitor() {
        return monitorTask == Constants.SELECTED;
    }
    
    public void setMonitor(int monitor) {
        monitorTask = monitor;
    }
    
    public int getToolBar() {
        return toolBar;
    }

    public boolean isToolBar() {
        return toolBar == Constants.SELECTED;
    }

    public void setToolBar(int toolBar) {
        this.toolBar = toolBar;
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

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean isMulti) {
        this.isMulti = isMulti;
    }

    public String getIIN() {
        return iin;
    }

    public void setIIN(String iin) {
        this.iin = iin;
    }

    public String getEMail() {
        return email;
    }

    public void setEMail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ip_address;
    }

    public void setIpAddress(String ip_address) {
        this.ip_address = ip_address;
    }

    public boolean isOnlyECP() {
        return isOnlyECP;
    }

    public void setOnlyECP(boolean isOnlyECP) {
        this.isOnlyECP = isOnlyECP;
    }

    /*
     * public void setHelp(KrnObject object) { helpObj = object; }
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

    public void checkAccess(long baseId, List<Long> nodes) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            UserNode node = (UserNode) getChildAt(i);
            if (node.isLeaf()) {
                if ((node.getBaseStructureObj() != null && node.getBaseStructureObj().id != baseId)
                        || nodes.contains(node.getKrnObj().id))
                    remove(i);
            } else {
                node.checkAccess(baseId, nodes);
                if (node.getChildCount() == 0)
                    remove(i);
            }
        }
    }

    public void getVIPUsers(String name, List<Long> nodes) {
        List<Long> res = new ArrayList<Long>();
        for (int i = getChildCount() - 1; i >= 0; i--) {
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
        for (int i = getChildCount() - 1; i >= 0; i--) {
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

    public void saveUser(Map<Integer, List<Long>> isMonitorValues, Map<Integer, List<Long>> isToolbarValues) {
    	List<String> changedProps = getChangedProps();
    	System.out.println("#############################################" + title + "|||ModifiedProperties: " + changedProps);
    	if (isNewNode || changedProps.size() > 0) {
	        Kernel krn = Kernel.instance();
	        try {
	            final long lang_ru = krn.getLangIdByCode("RU");
	            final long lang_kz = krn.getLangIdByCode("KZ");
	            KrnObject o = getKrnObj();
	            if (isNewNode || changedProps.contains("name")) {
	            	krn.setString(o.id, o.classId, "name", 0, 0, toString(), 0);
	            }
	            Date tm = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka")).getTime();
	            if (activeAttr != null) {
	                TimeValue[] tv = krn.getTimeValues(new long[] { o.id }, activeAttr, 0);
	                if (tv.length == 0) {
	                    // запомнить дату создания пользователя
	                    krn.setTime(o.id, o.classId, "activated", 0, tm, 0);
	                }
	            }
	            String[] userName = krn.getStrings(o, "name", 0, 0);
	            long[] isAdminOld = krn.getLongs(o, "admin", 0);
	            boolean pdChange = false;
	            String[] prevPD = null;
	            try {
	            	prevPD = krn.getStrings(o, "previous passwords", 0, 0);
	            } catch (Exception e) {
	                kz.tamur.rt.Utils.outErrorCreateAttrUser("previous passwords");
	            }
	
	            if (pdChanged) {
	                String mess = null;
	                PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode();
	
	                // верификация пароля на соотвествие требованиям
	                // политики
	                // безопасности
	                if (pdOriginal != null)
	                	mess = pnode.getPolicyWrapper().verificationPassAndLogin(o, pdOriginal.toCharArray(), userName[0], isAdmin(), true, new Locale("ru"));
	                
	                String pds[] = null;
	                long k = 0;
	                String pdsw = null;
	                if (mess == null) {
	                    // сравнение пароля с предыдущими (полное соответствие)
	                    if (prevPD != null && prevPD.length != 0 && !prevPD[0].isEmpty()) {
	                    	pdsw = prevPD[0];
	                        pds = pdsw.split(";");
	                        k = isAdmin() ? pnode.getPolicyWrapper().getNumPassDublAdmin() : pnode.getPolicyWrapper().getNumPassDubl();
	                        // если "ноль", то не применять
	                        if (k > 0) {
	                            for (String pd : pds) {
	                                if (pd.equals(getPassword())) {
	                                    MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE,
	                                            "Пароль не должен повторять " + k + " предыдущих паролей!");
	                                    return;
	                                }
	                            }
	                        }
	                    }
	                    // даже если политика отключена, пароли помнить нужно
	                    k = (k == 0) ? 20 : k;
	
	                    // теперь необходимо проследить чтобы записать новый
	                    // хеш
	                    // пароля в атрибут, при превышении размерности
	                    // атрибута
	                    // массив урезается
	
	                    int l = pds == null ? 0 : pds.length;
	
	                    // при достижении или превышении дозволенных границ
	                    if (l >= k) {
	                        int k_ = (int) k;
	                        String[] pds_ = new String[k_];
	                        // в обратном порядке переписать в промежуточный массив
	                        // все данные с предыдущего массива, с конца, пропустив
	                        // одну позицию
	                        for (int i = k_ - 2; i >= 0; --i) {
	                            pds_[i] = pds[--l];
	                        }
	
	                        // последнему элементу массива присвоить
	                        // значение нового пароля
	                        pds_[--k_] = getPassword();
	                        // сборка нового значения для атрибута
	                        // "previous passwords"
	                        StringBuilder b = new StringBuilder();
	                        for (int i = 0;; i++) {
	                            b.append(pds_[i]);
	                            if (i == k_)
	                                break;
	                            b.append(";");
	                        }
	                        pdsw = b.toString();
	                    } else {
	                        if (pdsw == null) {
	                        	pdsw = getPassword();
	                        } else {
	                        	pdsw += ";" + getPassword();
	                        }
	                    }
	                    try {
	                        // записать новый пул хешей паролей
	                        krn.setMemo((int) o.id, (int) o.classId, "previous passwords", 0, 0, pdsw, 0);
	                    } catch (Exception e) {
	                        kz.tamur.rt.Utils.outErrorCreateAttrUser("previous passwords");
	                    }
	                    // запись нового пароля
	                    krn.setString(o.id, o.classId, "password", 0, 0, getPassword(), 0);
	                    krn.writeLogRecord(SystemEvent.EVENT_CHANGE_PASSWORD, "for user " + toString());
	                    pdChange = true;
	                    pdChanged = false;
	                    try {
	                        // записать дату изменения пароля
	                        krn.setLong(o.id, o.classId, "isLogged", 0, 0, 0);
	                        krn.setTime(o.id, o.classId, "дата изменения пароля", 0, tm, 0);
	                        krn.setTime(o.id, o.classId, "activated", 0, tm, 0);
	                    } catch (Exception e) {
	                        kz.tamur.rt.Utils.outErrorCreateAttrUser("isLogged, дата изменения пароля, activated");
	                    }
	
	                } else {
	                    MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE, mess);
	                    return;
	                }
	            }
	            if (isAdmin() && (isAdmin() != toBoolean(isAdminOld[0])) && !pdChange) {
	                MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.ERROR_MESSAGE,
	                        "Когда пользователь становится администратором системы ему необходимо сменить пароль!");
	                // обновить свойства пользователя
	                setAdmin(false);
	                Or3Frame.instance().getContentPane().repaint();
	                return;
	            }
	            if (isNewNode || changedProps.contains("sign")) {
	            	krn.setString(o.id, o.classId, "sign", 0, lang_ru, getSign(), 0);
	            }
	            if (isNewNode || changedProps.contains("signKz")) {
	            	krn.setString(o.id, o.classId, "sign", 0, lang_kz, getSignKz(), 0);
	            }
	            if (isNewNode || changedProps.contains("doljnost")) {
	            	krn.setString(o.id, o.classId, "doljnost", 0, com.cifs.or2.client.Utils.getInterfaceLangId(), getDoljnost(), 0);
	            }
	            if (isNewNode || changedProps.contains("email")) {
		            if (emailAttr != null) {
		                krn.setString(o.id, o.classId, "email", 0, 0, getEmail(), 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("ip_address")) {
		            if (ipAddressAttr != null) {
		                krn.setString(o.id, o.classId, "ip_address", 0, 0, getIpAddress(), 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("base")) {
		            KrnObject baseObj = getBaseStructureObj();
		            if (baseObj != null) {
		                krn.setObject(o.id, o.classId, "base", 0, baseObj.id, 0, false);
		            } else {
		                krn.deleteValue(o.id, o.classId, "base", new int[] { 0 }, 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("languageData")) {
		            KrnObject dataLangObj = getDataLangObj();
		            if (dataLangObj != null) {
		                krn.setObject(o.id, o.classId, "data language", 0, dataLangObj.id, 0, false);
		            }
	            }
	            if (isNewNode || changedProps.contains("languageIfs")) {
		            KrnObject ifcLangObj = getIfcLangObj();
		            if (ifcLangObj != null) {
		                krn.setObject(o.id, o.classId, "interface language", 0, ifcLangObj.id, 0, false);
		            }
	            }
	            if (isNewNode || changedProps.contains("interface")) {
		            KrnObject userObj = getIfcObject();
		            if (userObj != null) {
		                krn.setObject(o.id, o.classId, "interface", 0, getIfcObject().id, 0, false);
		            } else {
		                krn.deleteValue(o.id, o.classId, "interface", new int[] { 0 }, 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("editor")) {
	            	krn.setLong(o.id, o.classId, "editor", 0, (isEditor()) ? 1 : 0, 0);
	            }
	            if (isNewNode || changedProps.contains("admin")) {
	            	krn.setLong(o.id, o.classId, "admin", 0, (isAdmin()) ? 1 : 0, 0);
	            }
	            if (isNewNode || changedProps.contains("blocked")) {
		            if (blockedAttr != null) {
		                long[] ls = krn.getLongs(o, blockedAttr, 0);
		                if (ls != null && ls.length > 0 && ls[0] == 1) {
		                    if (!isBlocked()) {
		                        krn.userUnblocked(toString());
		                        if (activeAttr != null) {
		                            // запомнить дату активации пользователя
		                            krn.setTime(o.id, o.classId, "activated", 0, tm, 0);
		                        }
		                    }
		                } else {
		                    if (isBlocked()) {
		                        krn.userBlocked(toString());
		                    }
		                }
		                krn.setLong(o.id, blockedAttr.id, 0, isBlocked() ? 1 : 0, 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("multi")) {
		            if (multiAttr != null) {
		                krn.setLong(o.id, o.classId, "multi", 0, (isMulti()) ? 1 : 0, 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("iin")) {
		            if (iinAttr != null) {
		                krn.setString(o.id, o.classId, "iin", 0, 0, getIIN(), 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("onlyECP")) {
		            if (onlyECPAttr != null) {
		                krn.setLong(o.id, o.classId, "onlyECP", 0, isOnlyECP() ? 1 : 0, 0);
		            }
	            }
	            if (isNewNode || changedProps.contains("isMonitor") || changedProps.contains("isToolBar")) {
		            boolean isCreated = true;
		            if (configObj == null) {
		            	isCreated = createConfigObj();
		            }
		            if (isCreated) {
			            if (isNewNode || changedProps.contains("isMonitor")) {
			            	if (isMonitorValues == null) {
				            	krn.setLong(configObj.id, configObj.classId, "isMonitor", 0, (long) monitorTask, 0);
			            	} else {
			            		List<Long> idsList = isMonitorValues.get(monitorTask);
			            		if (idsList == null) {
			            			idsList = new ArrayList<Long>();
			            			isMonitorValues.put(monitorTask, idsList);
			            		}
			            		idsList.add(configObj.id);
			            	}
			            }
			            if (isNewNode || changedProps.contains("isToolBar")) {
			            	if (isToolbarValues == null) {
				            	krn.setLong(configObj.id, configObj.classId, "isToolBar", 0, (long) toolBar, 0);
			            	} else {
			            		List<Long> idsList = isToolbarValues.get(toolBar);
			            		if (idsList == null) {
			            			idsList = new ArrayList<Long>();
			            			isToolbarValues.put(toolBar, idsList);
			            		}
			            		idsList.add(configObj.id);
			            	}
			            }
		            }
	            }
	            
                updateVersion();
	
	            krn.updateUser(o, toString());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    }

    public Element getOr3Rights() {
        return or3Rights;
    }

    public void setOr3Rights(Element element) {
        this.or3Rights = element;
    }

    public String getItemTitle(KrnObject obj) {
        return allTitleMap_.get(obj.id);
    }

    public String setItemTitle(KrnObject obj, String title) {
        return allTitleMap_.put(obj.id, title);
    }

    private void setItemsTitle(ObjectValue[] ovs, String attrName, long lang, Kernel krn) throws KrnException {
        if (ovs.length > 0) {
            long[] ids = new long[ovs.length];
            for (int i = 0; i < ovs.length; i++) {
                ids[i] = ovs[i].value.id;
            }
            StringValue[] titles = krn.getStringValues(ids, ovs[0].value.classId, attrName, lang, false, 0);
            for (StringValue sv : titles) {
            	allTitleMap_.put(sv.objectId, sv.value);
            }
        }
    }

    public static void setItemsTitle(long[] ids, long classId, String attrName, long lang, Kernel krn) throws KrnException {
        if (ids.length > 0) {
            StringValue[] titles = krn.getStringValues(ids, classId, attrName, lang, false, 0);
            for (StringValue sv : titles) {
            	allTitleMap_.put(sv.objectId, sv.value);
            }
        }
    }

    private void setItemsTitle(KrnObject obj, String attrName, long lang, Kernel krn) throws KrnException {
        if (obj != null) {
            String[] titles = krn.getStrings(obj, attrName, lang, 0);
            if (titles.length > 0)
            	allTitleMap_.put(obj.id, titles[0]);
        }
    }

    /**
     * Получить config obj.
     * 
     * @return the configObj
     */
    public KrnObject getConfigObj() {
        return configObj;
    }

    /**
     * Создание класса объекта для хранения локальных настроек
     */
    public boolean createConfigObj() {
        try {
            if (krn.checkExistenceClassByName(Constants.NAME_CLASS_CONFIG_LOCAL)) {
                configObj = krn.createObject(krn.getClassByName(Constants.NAME_CLASS_CONFIG_LOCAL), 0);
                krn.setObject(krnObj.id, krnObj.classId, "config", 0, configObj.id, 0, false);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Ошибка создания конфигурации пользователя!");
        }
        return false;
    }

    public List<String> getChangedProps() {
    	return version.getChangedProps(this);
    }
    
    public boolean isNewNode() {
    	return isNewNode;
    }
    
    public void updateVersion() {
    	isNewNode = false;
    	version.fillFields(this);
    }
    
    private class Version {
    	
    	private String name;
    	
    	private String pd;
    	private String sign;
    	private String signKz;
    	private String doljnost;
    	private String email;
    	private String ip_address;
    	private KrnObject base;
    	private KrnObject languageData;
    	private KrnObject languageIfs;
    	private KrnObject ifc;
    	private boolean admin;
    	private boolean blocked;
    	private boolean multi;
    	private String iin;
    	private boolean onlyECP;
    	
    	private KrnObject[] hyperMenu;
    	private boolean editor;
    	private KrnObject[] helps;
    	private KrnObject process;
    	private Element or3Rights;
    	
    	private boolean isMonitor;
    	private boolean isToolBar;

    	public Version(UserNode userNode) {
    		fillFields(userNode);
    	}
    			
    	public void fillFields(UserNode userNode) {
    		this.name = userNode.getName();
    		if (userNode.isLeaf()) {
	    		this.pd = userNode.getPassword();
	    		this.sign = userNode.getSign();
	    		this.signKz = userNode.getSignKz();
	    		this.doljnost = userNode.getDoljnost();
	    		this.email = userNode.getEmail();
	    		this.ip_address = userNode.getIpAddress();
	    		this.base = userNode.getBaseStructureObj();
	    		this.languageData = userNode.getDataLangObj();
	    		this.languageIfs = userNode.getIfcLangObj();
	    		this.ifc = userNode.getIfcObject();
	    		this.admin = userNode.isAdmin();
	    		this.blocked = userNode.isBlocked();
	    		this.multi = userNode.isMulti();
	    		this.iin = userNode.getIIN();
	    		this.onlyECP = userNode.isOnlyECP();
    		} else {
	    		this.hyperMenu = userNode.getHypers();
	    		this.editor = userNode.isEditor();
	    		this.helps = userNode.getHelp();
	    		this.process = userNode.getProcess();
	    		this.or3Rights = userNode.getOr3Rights();
    		}
    		this.isMonitor = userNode.isMonitor();
    		this.isToolBar = userNode.isToolBar();
    	}
    	
    	 public List<String> getChangedProps(UserNode userNode) {
	    	List<String> changedProps = new ArrayList<>();
	    	if (!compareStrings(this.name, userNode.getName())) {
				changedProps.add("name");
			}
			if (userNode.isLeaf()) {
				if (!compareStrings(this.pd, userNode.getPassword())) {
					changedProps.add("password");
				}
				if (!compareStrings(this.sign, userNode.getSign())) {
					changedProps.add("sign");
				}
				if (!compareStrings(this.signKz, userNode.getSignKz())) {
					changedProps.add("signKz");
				}
				if (!compareStrings(this.doljnost, userNode.getDoljnost())) {
					changedProps.add("doljnost");
				}
				if (!compareStrings(this.email, userNode.getEmail())) {
					changedProps.add("email");
				}
				if (!compareStrings(this.ip_address, userNode.getIpAddress())) {
					changedProps.add("ip_address");
				}
				if (!compareKrnObjects(this.base, userNode.getBaseStructureObj())) {
					changedProps.add("base");
				}
				if (!compareKrnObjects(this.languageData, userNode.getDataLangObj())) {
					changedProps.add("languageData");
				}
				if (!compareKrnObjects(this.languageIfs, userNode.getIfcLangObj())) {
					changedProps.add("languageIfs");
				}
				if (!compareKrnObjects(this.ifc, userNode.getIfcObject())) {
					changedProps.add("interface");
				}
				if (this.admin != userNode.isAdmin()) {
					changedProps.add("admin");
				}
				if (this.blocked != userNode.isBlocked()) {
					changedProps.add("blocked");
				}
				if (this.multi != userNode.isMulti()) {
					changedProps.add("multi");
				}
				if (!compareStrings(this.iin, userNode.getIIN())) {
					changedProps.add("iin");
				}
				if (this.onlyECP != userNode.isOnlyECP()) {
					changedProps.add("onlyECP");
				}
			} else {
				if (!compareArrays(this.hyperMenu, userNode.getHypers())) {
					changedProps.add("hyperMenu");
				}
				if (this.editor != userNode.isEditor()) {
					changedProps.add("editor");
				}
				if (!compareArrays(this.helps, userNode.getHelp())) {
					changedProps.add("helps");
				}
				if (!compareKrnObjects(this.process, userNode.getProcess())) {
					changedProps.add("process");
				}
				if (!compareElements(this.or3Rights, userNode.getOr3Rights())) {
					changedProps.add("or3Rights");
				}
			}
			if (this.isMonitor != userNode.isMonitor()) {
				changedProps.add("isMonitor");
			}
			if (this.isToolBar != userNode.isToolBar()) {
				changedProps.add("isToolBar");
			}
	    	return changedProps;
	    }
    	 
  		public boolean compareStrings(String s1, String s2) {
 			if (s1 == s2 || (s1 == null && s2 == null)) {
				return true;
			}
 			if ((s1 == null && s2 != null) || (s1 != null && s2 == null)) {
				return false;
			}
 			return s1.equals(s2);
  		}
    	
 		public boolean compareKrnObjects(KrnObject o1, KrnObject o2) {
 			if (o1 == o2 || (o1 == null && o2 == null)) {
				return true;
			}
 			if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
				return false;
			}
 			return o1.equals(o2);
 		}
    	 
		public boolean compareArrays(KrnObject[] a1, KrnObject[] a2) {
			if (a1 == a2 || (a1 == null && a2 == null)) {
				return true;
			}
			if ((a1 == null && a2 != null) || (a1 != null && a2 == null)) {
				return false;
			}
			if (a1.length != a2.length) {
				return false;
			}
			for (int i = 0; i < a1.length; i++) {
				Object o1 = a1[i];
				Object o2 = a2[i];
				if (!(o1 == null ? o2 == null : o1.equals(o2)))
					return false;
			}
			return true;
		}
		
		public boolean compareElements(Element e1, Element e2) {
			if (e1 == e2 || (e1 == null && e2 == null)) {
				return true;
			}
			if ((e1 == null && e2 != null) || (e1 != null && e2 == null)) { 
				return false;
			}
			try {
				StringWriter sw1 = new StringWriter();
				StringWriter sw2 = new StringWriter();
				new XMLOutputter().output(e1, sw1);
				new XMLOutputter().output(e2, sw2);
				return sw1.toString().equals(sw2.toString());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
    }
}