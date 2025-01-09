package com.cifs.or2.server.db;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.naming.NamingException;

import com.cifs.or2.kernel.ProjectConfiguration;

import kz.tamur.DriverException;

@Local
public interface ConnectionManagerLocal {
    public boolean addDatabase(Database db) throws NamingException, DriverException;

    public Database createDatabase(String name, String schemeName, String jndiName, String type, String replDir, boolean withTransactionWatchDog, String dbSeparateClassIds,String fileStoreType) throws NamingException, DriverException;

    public Database getDatabase(String name);
    
    public Set<String> getDatabaseNames();

    public Object getInitParamByName(String paramName);
    
	public void addUser(String userName, String userPass);
    
	public boolean authorizeUser(String userName, String userPass);
	
	public void addConfiguration(String dsName, ProjectConfiguration config);

	public void removeConfiguration(String dsName);

	public ProjectConfiguration getRoot();
	
	public ProjectConfiguration getConfiguration(String dsName);
	
	public List<ProjectConfiguration> getChildConfigurations(String dsName);

	public void saveAllConfigurations();
	
}
