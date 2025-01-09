package com.cifs.or2.server;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 21.10.2003
 * Time: 16:04:46
 * To change this template use Options | File Templates.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.server.orlang.SrvQuery;
import com.cifs.or2.server.workflow.organisation.OrganisationComponent;
import com.cifs.or2.util.Funcs;

import kz.tamur.or3ee.common.UserSession;

public class UserSrv implements Serializable {

	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + UserSrv.class);

	private String sign = "";
    private KrnObject userObj;
    private KrnObject baseObj;
    private KrnObject balansObj;
    private KrnObject intLangObj;
    private KrnObject dataLangObj;
    private String name;
    private boolean isAdmin;
    private boolean isMulti;
    private String iin;
    private String ip;

    // процессы, запускаемые при входе в систему
    transient private KrnObject[] process;
    // все потомки
    transient private KrnObject[] parents;
    // непосредственные родители
    transient private KrnObject[] immediateParents;
    // дети - если это роль
    transient private KrnObject[] children;

    public UserSrv(String name) {
    	this.name = name;
    	this.userObj = new KrnObject(-1, "-1", -1);
    	this.balansObj = new KrnObject(-1, "-1", -1);
    	this.baseObj = new KrnObject(-1, "-1", -1);
    }
    
    public UserSrv(Session s, String typeClient, KrnObject obj, String ip, String pcName, boolean callbakcs, OrganisationComponent orgComp) throws KrnException {
        userObj = obj;
        KrnClass groupCls = s.getClassByName("UserFolder");
        
        try {
            SrvQuery sq = getSrvQuery(s);
            sq.execute(obj, s);

            // считываем всех потомков
            reloadParents(s, orgComp);
			
            // если папка, то считываем детей
            if (obj.classId == groupCls.id) {
				KrnAttribute attr = s.getAttributeByName(groupCls, "children");
				children = s.getObjects(obj.id, attr.id, new long[0], 0);
			}
            this.isAdmin = sq.getBooleanAttr("User.admin", false);
            this.isMulti = sq.getBooleanAttr("User.multi", false);
            this.iin = sq.getStringAttr("User.iin");
            this.ip = sq.getStringAttr("User.ip_address");
            // Язык интерфейса
            this.intLangObj = sq.getObjectAttr("User.interface language");
            // Язык данных
            this.dataLangObj = sq.getObjectAttr("User.data language");
            // Логин
            this.name = sq.getStringAttr("User.name");
            // Подпись
            if (dataLangObj != null) {
                this.sign = sq.getStringAttr("User.sign", dataLangObj);
            }
            // База
            this.baseObj = sq.getObjectAttr("User.base");
            this.balansObj = sq.getObjectAttr("User.баланс_ед");

        } catch (Exception e) {
            // Игнорируем ошибку для возможности добавить недостающие классы
            // при помощи Администратора
            log.error(e, e);
        }
    }

    // считывает и возвращает процессы при входе в систему
    public KrnObject[] getProcess(Session s) {
        if (parents != null) {
            try {
                final KrnClass groupCls = s.getClassByName("UserFolder");
                final KrnAttribute attr = s.getAttributeByName(groupCls, "process");
                long[] ids=Funcs.makeObjectIdArray(parents);
                ObjectValue[] ovs = s.getObjectValues(ids, attr.id, new long[0], 0);
                if (ovs.length > 0) {
                    Set<KrnObject> process_ = new HashSet<>();
                    for (int i=0; i<ovs.length; ++i) {
                    	if (!process_.contains(ovs[i].value))
                    		process_.add(ovs[i].value);
                    }
                    process = process_.toArray(new KrnObject[process_.size()]);
                }
            } catch(KrnException e) {
                log.error(e, e);
            }
        }
        return process != null ? Arrays.copyOf(process, process.length) : null;
    }
    
    public KrnObject[] getParents() {
        return parents != null ? Arrays.copyOf(parents, parents.length) : null;
    }
    
    public KrnObject[] getImmediateParents() {
        return immediateParents != null ? Arrays.copyOf(immediateParents, immediateParents.length) : null;
    }
    
    public KrnObject[] getChildren() {
        return children != null ? Arrays.copyOf(children, children.length) : null;
    }

    public String getUserSign() {
        return sign;
    }

    public void reloadParents(Session s, OrganisationComponent orgComp) {
        KrnClass userCls = s.getClassByName("User");
        
        List<KrnObject> parentList = new ArrayList<>();

    	long[] ids = new long[] {userObj.id};
    	if (userObj.classId == userCls.id) {
            KrnObject[] objs = loadImmediateParents(s);
            if (objs.length > 0) {
                immediateParents = objs;
                
                for (int i=0; i<objs.length; ++i) {
                	if (objs[i] != null && !parentList.contains(objs[i])) {
                		parentList.add(objs[i]);
                	}
                }
                ids = Funcs.makeObjectIdArray(objs);
            }
    	}
        orgComp.getParentRoles(ids, parentList);
    	this.parents = parentList.toArray(new KrnObject[parentList.size()]);
    }
    
    public KrnObject[] loadImmediateParents(Session s) {
        KrnClass userCls = s.getClassByName("User");
        KrnAttribute parentAttr = s.getAttributeByName(userCls, "parent");
        
        try {
            return s.getObjects(userObj.id, parentAttr.id, new long[0], 0);
        } catch (KrnException e) {
        	log.error(e, e);
        }
        return new KrnObject[0];
    }

    public KrnObject getUserObj() {
    	return userObj;
    }

    public KrnObject getBaseObj() {
    	return baseObj;
    }
    
    public KrnObject getBalansObj() {
    	return balansObj;
    }

    public KrnObject getInterfaceLang() {
    	return intLangObj;
    }

    public KrnObject getDataLang() {
    	return dataLangObj;
    }

    public String getName() {
    	return name;
    }
    
    public boolean isAdmin() {
    	return isAdmin;
    }
    
    public boolean isMulti() {
    	return isMulti;
    }

    public String getUserName() {
    	return name;
    }
    
	public long getUserId() {
		return userObj.id;
	}

	public long getBaseId() {
    	return baseObj != null ? baseObj.id : 0;
    }
	
	public long getBalansId() {
    	return balansObj != null ? balansObj.id : 0;
    }

	public String getIin() {
		return iin;
	}

	public String getIp() {
		return ip;
	}

	private SrvQuery getSrvQuery(Session s) throws KrnException {
		SrvQuery sq = new SrvQuery("User.iin", null, s)
					 .addPath("User.ip_address")
					 .addPath("User.ip_address2")
					 .addPath("User.admin")
					 .addPath("User.interface language")
					 .addPath("User.data language")
					 .addPath("User.name")
					 .addPath("User.multi")
					 .addPath("User.base")
					 .addPath("User.баланс_ед");
		List<KrnObject> langs = s.getSystemLangs();
		for (KrnObject lang : langs)
			sq.addPath("User.sign", lang);
		return sq;
	}
}
