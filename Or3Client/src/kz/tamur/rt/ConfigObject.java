package kz.tamur.rt;

import static kz.tamur.rt.Utils.addObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Класс для работы с конфигурацией объектов по UUID
 *
 * @author Sergey Lebedev
 */
public class ConfigObject {

    /** Карта свойств одного объекта */
    private Map<String, Map<String, KrnString>> properties = new HashMap<String, Map<String, KrnString>>();
    
    /** Объект класса шаблона Singltone */
    private static ConfigObject inst_;
    
    /** Карта разметки UUIDов и привызянных к ним KRN объектов */
    private Map<String, KrnObject> uuids = new HashMap<String, KrnObject>();
    
    /** Кернель */
    private Kernel krn;
    
    /** объект со всей конфигурацией UUIDов */
    private KrnObject configObj;
    
    private static Map<UUID, ConfigObject> instances_ = new HashMap<UUID, ConfigObject>();

    private ConfigObject() {
    	this(Kernel.instance());
    }
    
    private ConfigObject(Kernel krn) {
    	this.krn = krn;
    }

    public static synchronized ConfigObject instance(Kernel krn) {
        ConfigObject co = instances_.get(krn.getUUID());
    	if (co == null) {
            co = new ConfigObject(krn);
            instances_.put(krn.getUUID(), co);
        }
        return co;
    }

    public static synchronized ConfigObject instance(Kernel krn, KrnObject configObject) {
        ConfigObject co = instances_.get(krn.getUUID());
    	if (co == null) {
            co = new ConfigObject(krn);
            instances_.put(krn.getUUID(), co);
        }
    	co.configObj = configObject;
        return co;
    }
    
    /**
     * Для веба обязательно ощищать созданные экземпляры конфигураций, так как 
     * при каждом новом входе в Систему создается новый Kernel, а старые не
     * удаляются, что приводит к большим потерям памяти
     */
    public static synchronized void removeInstance(Kernel krn) {
        instances_.remove(krn.getUUID());
    }

    /**
     * Получить значение указанного свойства.
     *
     * @param uuid идентификатор объекта
     * @param name наименование свойства
     * @return значение свойства
     */
    public String getProperty(String uuid, String name) {
        if (properties !=null && properties.get(uuid) !=null && properties.get(uuid).get(name) !=null) {
            return properties.get(uuid).get(name).value;
        }
        return null;
    }

    /**
     * Задать значение свойства
     *
     * @param uuid идентификатор объекта
     * @param name наименование свойства
     * @param value значение свойства
     * @param obj KRN объект данного свойства 
     * @param uuidObj KRN объект данного идентификатора
     */
    public void setProperty(String uuid, String name, String value, KrnObject obj, KrnObject uuidObj) {
        // связать uuid c KRN объектом
        uuids.put(uuid, uuidObj);
        // получить карту свойств компонента по его UUID
        Map<String, KrnString> map = properties.get(uuid);
        if (map == null) {
            map = new HashMap<String, KrnString>();
            // связать в карте имя свойство с объектом, состоящим из значение свойства и его KRN объектом
            map.put(name, new KrnString(obj, value));
            // добвить в карту идентификаторов свойство данного идентификатора 
            properties.put(uuid, map);
        } else {
            // связать в карте имя свойство с объектом, состоящим из значение свойства и его KRN объектом
            map.put(name, new KrnString(obj, value));
        }
    }

    
    
    
    /**
     * Сохранить свойство
     *
     * @param uuid идентификатор объекта
     * @param name наименование свойства
     * @param value значение свойства
     * @param obj KRN объект данного свойства 
     */
    public void saveProperty(String uuid, String name, String value, KrnObject obj) {
        try {
            KrnObject uuidObj;
            if (properties.get(uuid) == null) {
                uuidObj = krn.createObject(krn.getClassByName("ConfigObject"), 0);
                krn.setString(uuidObj.id, uuidObj.classId, "uuid", 0, 0, uuid, 0);
                addObject(uuidObj, "properties", obj);
                krn.setString(obj.id, obj.classId, "name", 0, 0, name, 0);
                krn.setString(obj.id, obj.classId, "value", 0, 0, value, 0);
                addObject(configObj, "configByUUIDs", uuidObj);
            }else {
                if (properties.get(uuid).get(name) !=null) {
                    KrnObject o = properties.get(uuid).get(name).obj;
                    krn.setString(o.id, o.classId, "name", 0, 0, name, 0);
                    krn.setString(o.id, o.classId, "value", 0, 0, value, 0); 
                }else {
                    addObject(uuids.get(uuid), "properties", obj);
                    krn.setString(obj.id, obj.classId, "name", 0, 0, name, 0);
                    krn.setString(obj.id, obj.classId, "value", 0, 0, value, 0);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохранить свойство, если KRN объект данного свойства неизвестен
     *
     * @param uuid идентификатор объекта
     * @param name наименование свойства
     * @param value значение свойства
     */
    public void saveProperty(String uuid, String name, String value) {
        try {
            saveProperty(uuid, name, value, krn.createObject(krn.getClassByName("Property"), 0));
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить карту свойств по идентификатору.
     *
     * @param uuid идентификатор объекта
     * @return карта свойств
     */
    public Map<String, KrnString> getProperties(String uuid) {
        return properties.get(uuid);
    }

  /**
   * Дополнительный класс-контейнер
   * Хранит значение свойства и KRN объект данного свойства
   * @author Sergey Lebedev
   *
   */
    class KrnString {
        
        /** KRN объект свойства */
        KrnObject obj;
        
        /** значение свойства */
        String value;

        /**
         * Конструктор класса.
         *
         * @param obj KRN объект свойства
         * @param value значение свойства
         */
        public KrnString(KrnObject obj, String value) {
            super();
            this.obj = obj;
            this.value = value;
        }
    }
}
