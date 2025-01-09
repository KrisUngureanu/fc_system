package kz.tamur.server.login.ldap;

public interface LdapOptions {

		//checks if roles are to be populated
	public static final String LDAP_POPULATE_ROLES = "LDAPPopulateRoles";
		// contins ldap context factory
	public static final String LDAP_CONTEXT_FACTORY= "LDAPContextFactory";
		//url of ldap server
	public static final String LDAP_SERVER_URL = "LDAPServerUrl";
		//like uid,cn of user
	public static final String LDAP_USER_KEY = "LDAPUserKey";
		//attribute name for roles
	public static final String LDAP_ROLES_KEY = "LDAPRolesKey";
		//authentication type of user
	public static final String LDAP_AUTH_TYPE = "LDAPAuthenticationType";
		//name for password attribute
	public static final String LDAP_PASSWORD_KEY= "LDAPPasswordKey";
		//objectclass for user entries
	public static final String LDAP_OBJECT_CLASS="LDAPObjectClass";
		//Realm name
	public static final String LDAP_REALM_NAME="realmname";

	//options without defaults
	// super context of user,that is directory in which all user entries are maintained
	public static final String LDAP_USER_SUP_CONTEXT = "LDAPUserSuperContext";
	//password of SuperUser
	public static final String LDAP_SUPER_USER_PASSWORD = "LDAPSuperUserPassword";
	//DN of SuperUser
	public static final String LDAP_SUPER_USER_DN= "LDAPSuperUserDN";
	//Schema provided from console
	public static final String LDAP_USER_SCHEMA= "LDAPUserSchema";
}
