package kz.tamur.server.plugins;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class LdapPlugin implements SrvPlugin {

	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String create(String name, List<Attribute> attrs,
			Map<String, String> env) {
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return err.toString();
		}
		Attributes as = new BasicAttributes();
		for (Attribute attr : attrs) {
			as.put(attr);
		}
		try {
			ctx.bind(name, null, as);
		} catch (NamingException e) {
			e.printStackTrace();
			return e.getMessage().replaceAll("\u0000", "");
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String rename(String oldName, String newName, Map<String, String> env) {
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return err.toString();
		}
		try {
			ctx.rename(oldName, newName);
		} catch (NamingException e) {
			e.printStackTrace();
			return e.getMessage().replaceAll("\u0000", "");
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String update(String name, String filter, List<Attribute> addAttrs,
			List<Attribute> replaceAttrs, List<String> deleteAttrs,
			Map<String, String> env) {
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return err.toString();
		}
		int sz = 0;
		if (addAttrs != null)
			sz += addAttrs.size();
		if (replaceAttrs != null)
			sz += replaceAttrs.size();
		if (deleteAttrs != null)
			sz += deleteAttrs.size();
		ModificationItem[] modItems = new ModificationItem[sz];
		int i = 0;
		if (replaceAttrs != null) {
			for (Attribute attr : replaceAttrs) {
				modItems[i++] = new ModificationItem(
						DirContext.REPLACE_ATTRIBUTE, attr);
			}
		}
		if (deleteAttrs != null) {
			for (String id : deleteAttrs) {
				modItems[i++] = new ModificationItem(
						DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(id));
			}
		}
		if (addAttrs != null) {
			for (Attribute attr : addAttrs) {
				modItems[i++] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
						attr);
			}
		}
		try {
			if (filter != null) {
				NamingEnumeration<SearchResult> ress = ctx.search(name, filter,
						null);
				if (ress.hasMore()) {
					SearchResult res = ress.next();
					name = res.getNameInNamespace();
				} else {
					return null;
				}
			}
			ctx.modifyAttributes(name, modItems);
		} catch (NamingException e) {
			e.printStackTrace();
			return e.getMessage().replaceAll("\u0000", "");
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String search(String name, String filter, boolean recursive,
			Map<String, String> env, List<String> errs) {
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return err.toString();
		}
		try {
			if (filter != null) {
				SearchControls sc = new SearchControls();
				sc.setSearchScope(recursive ? SearchControls.SUBTREE_SCOPE
						: SearchControls.ONELEVEL_SCOPE);
				NamingEnumeration<SearchResult> ress = ctx.search(name, filter,
						sc);
				if (ress.hasMore()) {
					SearchResult res = ress.next();
					return res.getNameInNamespace();
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			errs.add(e.getMessage().replaceAll("\u0000", ""));
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
				errs.add(e.getMessage());
			}
		}
		return null;
	}

	public Map<String,Attributes> search(
			String name,
			String filter,
			List<String> retAttrs,
			boolean recursive,
			Map<String, String> env,
			List<String> errs) {
		
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			errs.add(err.toString());
			return null;
		}
		try {
			if (filter != null) {
				SearchControls sc = new SearchControls();
				sc.setSearchScope(recursive ? SearchControls.SUBTREE_SCOPE
						: SearchControls.ONELEVEL_SCOPE);
				sc.setReturningAttributes(retAttrs.toArray(new String[retAttrs.size()]));
				NamingEnumeration<SearchResult> ress = ctx.search(name, filter,
						sc);
				if (ress.hasMore()) {
					SearchResult res = ress.next();
					Map<String, Attributes> r = new HashMap<String, Attributes>();
					r.put(res.getNameInNamespace(), res.getAttributes());
					return r;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			errs.add(e.getMessage().replaceAll("\u0000", ""));
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
				errs.add(e.getMessage());
			}
		}
		return null;
	}

	public List<String> searchList(String name, String filter, boolean recursive,
			Map<String, String> env, List<String> errs) {
		
		StringBuilder err = new StringBuilder();
		List<String> res = new ArrayList<String>();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return res;
		}
		try {
			if (filter != null) {
				SearchControls sc = new SearchControls();
				sc.setSearchScope(recursive ? SearchControls.SUBTREE_SCOPE
						: SearchControls.ONELEVEL_SCOPE);
				NamingEnumeration<SearchResult> ress = ctx.search(name, filter,
						sc);
				while (ress.hasMore()) {
					SearchResult sres = ress.next();
					res.add(sres.getNameInNamespace());
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			errs.add(e.getMessage().replaceAll("\u0000", ""));
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
				errs.add(e.getMessage());
			}
		}
		return res;
	}
	
	public String delete(String name, Map<String, String> env) {
		StringBuilder err = new StringBuilder();
		DirContext ctx = connect(env, err);
		if (ctx == null) {
			return err.toString();
		}
		try {
			ctx.destroySubcontext(name);
		} catch (NamingException e) {
			e.printStackTrace();
			return e.getMessage().replaceAll("\u0000", "");
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Attribute createAttribute(String key, Object value) {
		return new BasicAttribute(key, value);
	}

	private DirContext connect(Map<String, String> env, StringBuilder err) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.putAll(env);
		try {
			return new InitialDirContext(props);
		} catch (NamingException e) {
			e.printStackTrace();
			err.append(e.getMessage().replaceAll("\u0000", ""));
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		LdapPlugin plg = new LdapPlugin();
		StringBuilder err = new StringBuilder();
		Map<String, String> env = new HashMap<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://ad.akorda.kz:389");
		env.put(Context.SECURITY_PRINCIPAL, "KadryService@akorda.kz");
		env.put(Context.SECURITY_CREDENTIALS, "123456");
		DirContext ctx = plg.connect(env, err);
		SearchControls sc = new SearchControls(SearchControls.SUBTREE_SCOPE, 0,
				0, null, true, true);
		NamingEnumeration<SearchResult> ress = ctx.search(
				"OU=Test_Kadry, DC=akorda,DC=kz", "(&(objectClass=user))", sc);
		while (ress.hasMore()) {
			SearchResult res = ress.next();
			System.out.println(res.getNameInNamespace());
		}
		// ctx.rename("CN=neworganizationalPerson,OU=sec1,OU=or3dev,DC=test,DC=tamur,DC=kz",
		// "CN=neworganizationalPerson,OU=sec2,OU=or3dev,DC=test,DC=tamur,DC=kz");
		ctx.close();
	}

	public static void main2(String[] args) throws Exception {
		LdapPlugin plg = new LdapPlugin();
		Map<String, String> env = new HashMap<String, String>();
		String ldapURL = "ldap://129.0.1.138:62222";
		String userName = "";
		String userPass = "";

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		// env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
		env.put(Context.PROVIDER_URL, ldapURL);
		env.put(Context.SECURITY_PRINCIPAL, userName);
		env.put(Context.SECURITY_CREDENTIALS, userPass);
		env.put("java.naming.ldap.attributes.binary", "userCertificate");
		
		List<File> fs = plg.getCertificatesFromLDAP(env, "", "*");
		for (File f : fs) {
			System.out.println(f.getAbsolutePath());
		}
	}

	private LdapContext connectCertLDAP(Map<String, String> env) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.putAll(env);
		try {
			return new InitialLdapContext(props, null);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<File> getCertificatesFromLDAP(Map<String, String> props,
			String dnName, String fio) {
		try {
			LdapContext ctx = connectCertLDAP(props);
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(dnName, "cn=" + fio, ctls);
			List<File> fs = getDigiSignCertificate(answer);
			ctx.close();
			return fs;
		} catch (Exception e) {
			System.out.println("Exception: " + e
					+ "\n Possible, there were enter not correct data");
			return Collections.emptyList();
		}
	}

	private List<File> getDigiSignCertificate(NamingEnumeration<SearchResult> answer) {
		List<File> fs = new ArrayList<File>();
		File dir = new File("certs");
		dir.mkdirs();
		try {
			if (answer.hasMoreElements()) {
				while (answer.hasMore()) {
					SearchResult sr = answer.next();
					Attributes pp = sr.getAttributes();
					for (Enumeration e = pp.getAll(); e.hasMoreElements();) {
						Attribute attr = (Attribute) e.nextElement();
						String attrID = attr.getID();
						try {
							if (attrID.equals("userCertificate")) {
								for (int i = 0; i < attr.size(); i++) {
									byte[] csd_b = (byte[]) attr.get(i);
									ByteArrayInputStream bis = new ByteArrayInputStream(csd_b);
									CertificateFactory cf1 = CertificateFactory.getInstance("X.509");
									Collection c1 = cf1.generateCertificates(bis);
									Iterator i1 = c1.iterator();
									while (i1.hasNext()) {
										X509Certificate cert = (X509Certificate) i1.next();
										if (cert.getKeyUsage()[0]) {
											java.security.Principal principal = cert
												.getSubjectDN();
											File f = File.createTempFile("cert", ".cer", dir);
									        FileOutputStream fos = new FileOutputStream(f);
									        fos.write(cert.getEncoded());
									        fos.close();
											f.deleteOnExit();
									        System.out.println("SubjectDN: " + principal.getName() + "\n\n");
											
											fs.add(f);
										}
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fs;
	}
}