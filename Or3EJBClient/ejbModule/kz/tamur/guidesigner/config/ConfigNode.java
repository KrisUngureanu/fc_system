package kz.tamur.guidesigner.config;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;

import java.util.*;
import java.util.Date;
import java.io.ByteArrayInputStream;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.ods.Value;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.PasswordService;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
public class ConfigNode extends AbstractDesignerTreeNode {
    public static final int PROPERTY_NOT_CHANGED = 0;
    public static final int PROPERTY_CHANGED = 2;
    public static final int LOGIN_TOO_SHORT = 3;

    private ProjectConfiguration config;
    private boolean isModified = false;
    
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

    public ConfigNode() {
        isLoaded = true;
        title = "";
    }

    /**
     * Создание нового узла с пользователем
     *
     * @param obj объект - пользователь
     * @param name имя
     * @param password пароль
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
    public ConfigNode(ProjectConfiguration config) {
        this.config = config;
        this.title = config.getName();
    }

    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    public void rename(String newName) {
        title = newName;
        config.setName(newName);
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            try {
            	List<ProjectConfiguration> configs = Kernel.instance().getChildConfigurations(config.getDsName());

                List<ConfigNode> children = new ArrayList<ConfigNode>();
                for (int i = 0; i < configs.size(); i++) {
                	ProjectConfiguration c = configs.get(i);
                	children.add(new ConfigNode(c));
                }
                addAllChildren(children);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addAllChildren(List children) {
        // сортировка в алфавитном порядке всех потомков
        Collections.sort(children, new AllStringComparator());
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

    public String getName() {
        return title;
    }

    public String getDsName() {
        return config.getDsName();
    }

    public ProjectConfiguration getConfig() {
		return config;
	}

	public void setConfig(ProjectConfiguration config) {
		this.config = config;
	}

	public int setName(String name) {
        if (this.title.equals(name))
            return PROPERTY_NOT_CHANGED;
        this.title = name;
        return PROPERTY_CHANGED;
    }
}
