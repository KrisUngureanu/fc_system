package kz.tamur.guidesigner.config;

import java.util.Locale;

import kz.tamur.or3.client.props.*;

public class ConfigNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private ConfigurationsPanel owner;

    // текстовые константы
    private final String CONFIG_NAME = "Наименование";
    private final String CONFIG_DS_NAME = "Уникальное имя";
    private final String DB_SCHEME_NAME = "Наименование схемы БД";
    private final String DB_POOL_NAME = "Наименования пула БД";
    private final String DB_CONNECTION_URL = "Адрес подключения к БД";
    private final String DB_DRIVER = "Тип драйвера";
    private final String DB_TRANSACTION_ISOLATION = "Уровень изолированности транзакций";
    private final String DB_MIN_POOL = "Мин. количество соединений в пуле";
    private final String DB_MAX_POOL = "Макс. количество соединений в пуле";
    private final String DB_POOL_PREFILL = "Заполнить пул предварительно";
    private final String DB_POOL_USE_STRICT_MIN = "Строго следить за минимальным количеством соединений";
    private final String DB_POOL_FLUSH_STRATEGY = "Способ уничтожения соединений при ошибке";
    private final String DB_USER_NAME = "Имя пользователя БД";
    private final String DB_PD = "Пароль БД";
    private final String DB_PST_CACHE = "Кешировать запросы";
    private final String DB_PST_CACHE_SIZE = "Количество запросов в кеше";
    private final String SERVER_PLUGINS = "Путь к файлу описания плагинов";
    private final String TRANSPORT_PROPERTIES = "Путь к файлу настройки транспортных систем";
    private final String TRIIGER_EXCEPTIONS = "Путь к файлу описания исключений триггеров";
    private final String REPLICATION_DIR = "Папка репликаций";
    private final String WEB_CONTEXT = "Контекст веб-приложения";

    public ConfigNodeItem(Object item, ConfigurationsPanel owner) {
        this.item = item;
        this.owner = owner;
    }

    public Property getProperties() {
        proot = new FolderProperty(null, null, "Элементы");
        if (item instanceof ConfigNode) {
            new StringProperty(proot, CONFIG_NAME, CONFIG_NAME);
            new StringProperty(proot, CONFIG_DS_NAME, CONFIG_DS_NAME);
            new StringProperty(proot, DB_SCHEME_NAME, DB_SCHEME_NAME);
            new StringProperty(proot, DB_POOL_NAME, DB_POOL_NAME);
            new StringProperty(proot, DB_CONNECTION_URL, DB_CONNECTION_URL);
            ComboProperty p = new ComboProperty(proot, DB_DRIVER, DB_DRIVER);
            p.addItem("", "")
		            	.addItem("mysql", "mysql")
		            	.addItem("oracle", "oracle")
		            	.addItem("mssql", "mssql");
            p = new ComboProperty(proot, DB_TRANSACTION_ISOLATION, DB_TRANSACTION_ISOLATION);
            p.addItem("", "")
			        	.addItem("TRANSACTION_READ_UNCOMMITTED", "TRANSACTION_READ_UNCOMMITTED")
			        	.addItem("TRANSACTION_READ_COMMITTED", "TRANSACTION_READ_COMMITTED")
			        	.addItem("TRANSACTION_REPEATABLE_READ", "TRANSACTION_REPEATABLE_READ")
			        	.addItem("TRANSACTION_SERIALIZABLE ", "TRANSACTION_SERIALIZABLE ")
			        	.addItem("TRANSACTION_NONE", "TRANSACTION_NONE");

            new StringProperty(proot, DB_MIN_POOL, DB_MIN_POOL);
            new StringProperty(proot, DB_MAX_POOL, DB_MAX_POOL);
            new CheckProperty(proot, DB_POOL_PREFILL, DB_POOL_PREFILL);
            new CheckProperty(proot, DB_POOL_USE_STRICT_MIN, DB_POOL_USE_STRICT_MIN);
            p = new ComboProperty(proot, DB_POOL_FLUSH_STRATEGY, DB_POOL_FLUSH_STRATEGY);
            p.addItem("", "")
			        	.addItem("FailingConnectionOnly", "FailingConnectionOnly")
			        	.addItem("InvalidIdleConnections", "InvalidIdleConnections")
			        	.addItem("IdleConnections", "IdleConnections")
			        	.addItem("Gracefully", "Gracefully")
			        	.addItem("EntirePool", "EntirePool")
			        	.addItem("AllInvalidIdleConnections", "AllInvalidIdleConnections")
			        	.addItem("AllIdleConnections", "AllIdleConnections")
			        	.addItem("AllGracefully", "AllGracefully")
			        	.addItem("AllConnections", "AllConnections");
            
            new StringProperty(proot, DB_USER_NAME, DB_USER_NAME);
            new PasswordProperty(proot, DB_PD, DB_PD);
            new CheckProperty(proot, DB_PST_CACHE, DB_PST_CACHE);
            new StringProperty(proot, DB_PST_CACHE_SIZE, DB_PST_CACHE_SIZE);
            new StringProperty(proot, SERVER_PLUGINS, SERVER_PLUGINS);
            new StringProperty(proot, TRANSPORT_PROPERTIES, TRANSPORT_PROPERTIES);
            new StringProperty(proot, TRIIGER_EXCEPTIONS, TRIIGER_EXCEPTIONS);
            new StringProperty(proot, REPLICATION_DIR, REPLICATION_DIR);
            new StringProperty(proot, WEB_CONTEXT, WEB_CONTEXT);
        }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res = "";
        String id = prop.getId();
        if (item instanceof ConfigNode) {
            if (CONFIG_NAME.equals(id))
                res = ((ConfigNode) item).getName();
            else if (CONFIG_DS_NAME.equals(id))
                res = ((ConfigNode) item).getConfig().getDsName();
            else if (DB_SCHEME_NAME.equals(id))
                res = ((ConfigNode) item).getConfig().getSchemeName();
            else if (DB_POOL_NAME.equals(id))
                res = ((ConfigNode) item).getConfig().getPoolName();
            else if (DB_CONNECTION_URL.equals(id))
                res = ((ConfigNode) item).getConfig().getConnectionUrl();
            else if (DB_DRIVER.equals(id))
                res = ((ConfigNode) item).getConfig().getDriver();
            else if (DB_TRANSACTION_ISOLATION.equals(id))
                res = ((ConfigNode) item).getConfig().getTransactionIsolation();
            else if (DB_MIN_POOL.equals(id))
                res = ((ConfigNode) item).getConfig().getMinPoolSize();
            else if (DB_MAX_POOL.equals(id))
                res = ((ConfigNode) item).getConfig().getMaxPoolSize();
            else if (DB_POOL_PREFILL.equals(id))
                res = ((ConfigNode) item).getConfig().isPrefill();
            else if (DB_POOL_USE_STRICT_MIN.equals(id))
                res = ((ConfigNode) item).getConfig().isUseStrictMin();
            else if (DB_POOL_FLUSH_STRATEGY.equals(id))
                res = ((ConfigNode) item).getConfig().getFlushStrategy();
            else if (DB_USER_NAME.equals(id))
                res = ((ConfigNode) item).getConfig().getUserName();
            else if (DB_PD.equals(id))
                res = "*******";
        	else if (DB_PST_CACHE.equals(id))
        		res = ((ConfigNode) item).getConfig().isSharePst();
            else if (DB_PST_CACHE_SIZE.equals(id))
                res = ((ConfigNode) item).getConfig().getPstSize();
            else if (SERVER_PLUGINS.equals(id))
                res = ((ConfigNode) item).getConfig().getServerPlugins();
            else if (TRANSPORT_PROPERTIES.equals(id))
                res = ((ConfigNode) item).getConfig().getTransportProperties();
            else if (TRIIGER_EXCEPTIONS.equals(id))
                res = ((ConfigNode) item).getConfig().getTriggerExceptions();
            else if (REPLICATION_DIR.equals(id))
                res = ((ConfigNode) item).getConfig().getReplicationDir();
            else if (WEB_CONTEXT.equals(id))
                res = ((ConfigNode) item).getConfig().getWebContext();
        }

        if (prop instanceof ComboProperty)
            res = ((ComboProperty)prop).getItem(res != null ? res.toString() : "");

        return res;
    }

    public void setValue(Property prop, Object value) {
        if (item instanceof ConfigNode) {
            String id = prop.getId();
            if (CONFIG_NAME.equals(id)) {
                ((ConfigNode) item).setName((String) value);
                ((ConfigNode) item).getConfig().setName((String) value);
            } else if (CONFIG_DS_NAME.equals(id))
            	((ConfigNode) item).getConfig().setDsName((String) value);
            else if (DB_SCHEME_NAME.equals(id))
            	((ConfigNode) item).getConfig().setSchemeName((String) value);
            else if (DB_POOL_NAME.equals(id)) {
            	((ConfigNode) item).getConfig().setPoolName((String) value);
            	((ConfigNode) item).getConfig().setJndiName("java:/" + (String) value);
            } else if (DB_CONNECTION_URL.equals(id))
            	((ConfigNode) item).getConfig().setConnectionUrl((String) value);
            else if (DB_DRIVER.equals(id))
            	((ConfigNode) item).getConfig().setDriver(((ComboPropertyItem)value).id);
            else if (DB_TRANSACTION_ISOLATION.equals(id))
            	((ConfigNode) item).getConfig().setTransactionIsolation(((ComboPropertyItem)value).id);
            else if (DB_MIN_POOL.equals(id))
            	((ConfigNode) item).getConfig().setMinPoolSize(Integer.parseInt((String) value));
            else if (DB_MAX_POOL.equals(id))
            	((ConfigNode) item).getConfig().setMaxPoolSize(Integer.parseInt((String) value));
            else if (DB_POOL_PREFILL.equals(id))
            	((ConfigNode) item).getConfig().setPrefill(toBoolean(value));
            else if (DB_POOL_USE_STRICT_MIN.equals(id))
            	((ConfigNode) item).getConfig().setUseStrictMin(toBoolean(value));
            else if (DB_POOL_FLUSH_STRATEGY.equals(id))
            	((ConfigNode) item).getConfig().setFlushStrategy(((ComboPropertyItem)value).id);
            else if (DB_USER_NAME.equals(id))
            	((ConfigNode) item).getConfig().setUserName((String) value);
            else if (DB_PD.equals(id))
            	((ConfigNode) item).getConfig().setPassword(String.valueOf((char[]) value));
            else if (DB_PST_CACHE.equals(id))
            	((ConfigNode) item).getConfig().setSharePst(toBoolean(value));
            else if (DB_PST_CACHE_SIZE.equals(id))
            	((ConfigNode) item).getConfig().setPstSize(Integer.parseInt((String) value));
            else if (SERVER_PLUGINS.equals(id))
            	((ConfigNode) item).getConfig().setServerPlugins((String) value);
            else if (TRANSPORT_PROPERTIES.equals(id))
            	((ConfigNode) item).getConfig().setTransportProperties((String) value);
            else if (TRIIGER_EXCEPTIONS.equals(id))
            	((ConfigNode) item).getConfig().setTriggerExceptions((String) value);
            else if (REPLICATION_DIR.equals(id))
            	((ConfigNode) item).getConfig().setReplicationDir((String) value);
            else if (WEB_CONTEXT.equals(id))
            	((ConfigNode) item).getConfig().setWebContext((String) value);
            
        }
        owner.setModified((ConfigNode) item);
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}
    
    public String getTitle() {
        return "";
    }

    public Property getNewProperties() {
        return null;
    }
    
    public static Boolean toBoolean(Object o) {
        return (o instanceof Boolean) ? (Boolean) o : o.toString().equals("1") || o.toString().toUpperCase(Locale.ROOT).equals("TRUE");
    }
}
