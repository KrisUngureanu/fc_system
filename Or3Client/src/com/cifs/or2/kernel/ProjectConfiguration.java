package com.cifs.or2.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

public class ProjectConfiguration implements Serializable {
	private static final long serialVersionUID = 3966226454198654909L;
	
	private String name;
	private String dsName;
	private String schemeName;
	private boolean notifyErrors;
	private boolean withTransactions;
	// DataSource Fields
	private String jndiName;
	private String poolName;
	private String connectionUrl;
	private String driver;
	private String transactionIsolation;
	private int minPoolSize = 10;
	private int maxPoolSize = 10;
	private boolean prefill;
	private boolean useStrictMin;
	private String flushStrategy;
	private String userName;
	private String pd;
	private boolean sharePst;
	private long pstSize;
	private String serverPlugins;
	private String transportProperties;
	private String triggerExceptions;
	private String replicationDir;
	private String webContext;

	private int maxUserCount = 10;

	transient private List<ProjectConfiguration> children = new ArrayList<ProjectConfiguration>();
	
	private ProjectConfiguration parent;
	private String oldDsName = null;
	
	public ProjectConfiguration(String name, String dsName, String schemeName) {
		super();
		this.name = name;
		this.dsName = dsName;
		this.schemeName = schemeName;
		this.oldDsName = null;
	}
	
	public ProjectConfiguration(String name, String dsName, String schemeName,
			boolean notifyErrors, boolean withTransactions, String jndiName,
			String poolName, String connectionUrl, String driver,
			String transactionIsolation, int minPoolSize, int maxPoolSize,
			boolean prefill, boolean useStrictMin, String flushStrategy,
			String userName, String pd, boolean sharePst, long pstSize,
			String serverPlugins, String transportProperties,
			String triggerExceptions, String replicationDir, String webContext, int maxUserCount) {
		super();
		this.name = name;
		this.dsName = this.oldDsName = dsName;
		this.schemeName = schemeName;
		this.notifyErrors = notifyErrors;
		this.withTransactions = withTransactions;
		this.jndiName = jndiName;
		this.poolName = poolName;
		this.connectionUrl = connectionUrl;
		this.driver = driver;
		this.transactionIsolation = transactionIsolation;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.prefill = prefill;
		this.useStrictMin = useStrictMin;
		this.flushStrategy = flushStrategy;
		this.userName = userName;
		this.pd = pd;
		this.sharePst = sharePst;
		this.pstSize = pstSize;
		this.serverPlugins = serverPlugins;
		this.transportProperties = transportProperties;
		this.triggerExceptions = triggerExceptions;
		this.replicationDir = replicationDir;
		this.webContext = webContext;
		this.maxUserCount = maxUserCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	public String getSchemeName() {
		return schemeName;
	}
	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	public boolean isNotifyErrors() {
		return notifyErrors;
	}
	public void setNotifyErrors(boolean notifyErrors) {
		this.notifyErrors = notifyErrors;
	}
	public boolean isWithTransactions() {
		return withTransactions;
	}
	public void setWithTransactions(boolean withTransactions) {
		this.withTransactions = withTransactions;
	}
	public String getJndiName() {
		return jndiName;
	}
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getTransactionIsolation() {
		return transactionIsolation;
	}
	public void setTransactionIsolation(String transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public boolean isPrefill() {
		return prefill;
	}
	public void setPrefill(boolean prefill) {
		this.prefill = prefill;
	}
	public boolean isUseStrictMin() {
		return useStrictMin;
	}
	public void setUseStrictMin(boolean useStrictMin) {
		this.useStrictMin = useStrictMin;
	}
	public String getFlushStrategy() {
		return flushStrategy;
	}
	public void setFlushStrategy(String flushStrategy) {
		this.flushStrategy = flushStrategy;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return pd;
	}
	public void setPassword(String pd) {
		this.pd = pd;
	}
	public boolean isSharePst() {
		return sharePst;
	}
	public void setSharePst(boolean sharePst) {
		this.sharePst = sharePst;
	}
	public long getPstSize() {
		return pstSize;
	}
	public void setPstSize(long pstSize) {
		this.pstSize = pstSize;
	}
	public String getServerPlugins() {
		return serverPlugins;
	}
	public void setServerPlugins(String serverPlugins) {
		this.serverPlugins = serverPlugins;
	}
	public String getTransportProperties() {
		return transportProperties;
	}
	public void setTransportProperties(String transportProperties) {
		this.transportProperties = transportProperties;
	}
	public String getTriggerExceptions() {
		return triggerExceptions;
	}
	public void setTriggerExceptions(String triggerExceptions) {
		this.triggerExceptions = triggerExceptions;
	}
	public String getReplicationDir() {
		return replicationDir;
	}
	public void setReplicationDir(String replicationDir) {
		this.replicationDir = replicationDir;
	}
	public String getWebContext() {
		return webContext;
	}
	public void setWebContext(String webContext) {
		this.webContext = webContext;
	}

	public ProjectConfiguration getParent() {
		return parent;
	}

	public void setParent(ProjectConfiguration parent) {
		this.parent = parent;
	}

	public List<ProjectConfiguration> getChildren() {
		if (children == null) children = new ArrayList<ProjectConfiguration>();
		return children;
	}

	public void addChild(ProjectConfiguration pc) {
		if (children == null) children = new ArrayList<ProjectConfiguration>();
		children.add(pc);
		pc.setParent(this);
	}

	public void load(ProjectConfiguration c) {
		this.name = c.name;
		this.dsName = this.oldDsName = c.dsName;
		this.schemeName = c.schemeName;
		this.notifyErrors = c.notifyErrors;
		this.withTransactions = c.withTransactions;
		this.jndiName = c.jndiName;
		this.poolName = c.poolName;
		this.connectionUrl = c.connectionUrl;
		this.driver = c.driver;
		this.transactionIsolation = c.transactionIsolation;
		this.minPoolSize = c.minPoolSize;
		this.maxPoolSize = c.maxPoolSize;
		this.prefill = c.prefill;
		this.useStrictMin = c.useStrictMin;
		this.flushStrategy = c.flushStrategy;
		this.userName = c.userName;
		this.pd = c.pd;
		this.sharePst = c.sharePst;
		this.pstSize = c.pstSize;
		this.serverPlugins = c.serverPlugins;
		this.transportProperties = c.transportProperties;
		this.triggerExceptions = c.triggerExceptions;
		this.replicationDir = c.replicationDir;
		this.webContext = c.webContext;
		this.maxUserCount = c.maxUserCount;
	}

	public String getOldDsName() {
		return oldDsName;
	}

	public void setOldDsName(String oldDsName) {
		this.oldDsName = oldDsName;
	}

	public Element toXml() {
		Element e = new Element("project");
		Element d = new Element("datasource");
		e.addContent(d);
		
		e.setAttribute("name", name);
		e.setAttribute("distinctName", dsName);
		e.setAttribute("schemeName", schemeName);
		e.setAttribute("notifyErrors", notifyErrors ? "true" : "false");
		e.setAttribute("watchTransactions", withTransactions ? "true" : "false");
		
		e.addContent(new Element("webContext").setText(webContext));
		e.addContent(new Element("replication.directory").setText(replicationDir));
		e.addContent(new Element("server.plugins").setText(serverPlugins));
		e.addContent(new Element("trigger.exceptions").setText(triggerExceptions));
		e.addContent(new Element("transport.properties").setText(transportProperties));
		
		d.setAttribute("jndi-name", jndiName);
		d.setAttribute("pool-name", poolName);
		
		d.addContent(new Element("driver").setText(driver));
		d.addContent(new Element("connection-url").setText(connectionUrl));
		d.addContent(new Element("transaction-isolation").setText(transactionIsolation));

		if (minPoolSize > 0 || maxPoolSize > 0 || prefill || useStrictMin || (flushStrategy != null && flushStrategy.length() > 0)) {
			Element p = new Element("pool");
			d.addContent(p);
			p.addContent(new Element("prefill").setText(prefill ? "true" : "false"));
			if (minPoolSize > 0) p.addContent(new Element("min-pool-size").setText(String.valueOf(minPoolSize)));
			if (maxPoolSize > 0) p.addContent(new Element("max-pool-size").setText(String.valueOf(maxPoolSize)));
			p.addContent(new Element("use-strict-min").setText(useStrictMin ? "true" : "false"));
			if (flushStrategy != null && flushStrategy.length() > 0) p.addContent(new Element("flush-strategy").setText(flushStrategy));
		}
		if (pstSize > 0 || sharePst) {
			Element p = new Element("statement");
			d.addContent(p);
			p.addContent(new Element("share-prepared-statements").setText(sharePst ? "true" : "false"));
			if (pstSize > 0) p.addContent(new Element("prepared-statement-cache-size").setText(String.valueOf(pstSize)));
		}
		Element p = new Element("security");
		d.addContent(p);
		if (userName != null) p.addContent(new Element("user-name").setText(userName));
		if (pd != null) p.addContent(new Element("password").setText(pd));
		
		if (maxUserCount > 0) e.addContent(new Element("maxUserCount").setText(String.valueOf(maxUserCount)));
		
		if (children != null && children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				e.addContent(children.get(i).toXml());
			}
		}
		return e;
	}
}
