package kz.tamur.server.login;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Base64;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.SessionManager;

public class LtpaLoginModule implements LoginModule {

	private SessionManager mgr;
	private String token;
	private String name;
	private String ip;
	private String computer;

	private boolean verification;

	private DirContext ctx;

	private CallbackHandler callbackHandler;
	private Subject subject;

	private Properties options;

    public static String WEBSPHERE_SK; //"5p/P5XkNHhwHfRotT8eNYyrf1FgT+JfhLN3iaKgbA0c="
    public static String WEBSPHERE_CA; // cipher algr "DESede/ECB/PKCS5Padding"
    public static String WEBSPHERE_SKF; // "TripleDES"
    public static String WEBSPHERE_DA; // digest alg "SHA"
    public static int WEBSPHERE_DL; // digest strength "24"

	public LtpaLoginModule() {
	}

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean commit() throws LoginException {
/*		try {
			Session session = mgr.getImplSession2(name, null, ip, computer, client, false);
			subject.getPrincipals().add(new UserSession(session, name));
		} catch (KrnException e) {
			e.printStackTrace();
			throw new Or3LoginException(e.code, e.message);
		}
*/		return true;
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.callbackHandler = callbackHandler;
		this.options = new Properties();
		this.options.putAll(options);
		this.subject = subject;
	}

	public boolean login() throws LoginException {
		
		NameCallback ncb = new NameCallback("LTPA токен");
		Or3LoginContextCallback lcb = new Or3LoginContextCallback();

		if (callbackHandler == null)
			throw new LoginException("callback is null");
		try {
			callbackHandler.handle(new Callback[] {ncb, lcb});
		} catch (IOException e) {

			throw new LoginException(e.toString());
		} catch (UnsupportedCallbackException e) {

			throw new LoginException(
				e.toString()
						+ "callbackHandler does not support name or password callback");
		}

		token = ncb.getName();
		
		if (token == null)
			throw new LoginException("name must not be null");
		
		name = decriptToken(token);

		if (name == null)
			throw new LoginException("Пользователь не авторизован.");

		try {
			Hashtable props = new Hashtable();
			props.put(Context.INITIAL_CONTEXT_FACTORY, options
				.getProperty(Context.INITIAL_CONTEXT_FACTORY));
			props.put(Context.PROVIDER_URL, options
				.getProperty(Context.PROVIDER_URL));
			props.put(Context.SECURITY_PRINCIPAL, options.getProperty("user"));
			props.put(Context.SECURITY_CREDENTIALS, options.getProperty("passwd"));
			props.put(Context.SECURITY_AUTHENTICATION, "simple");
			
			ctx = new InitialDirContext(props);
			NamingEnumeration<SearchResult> ress = ctx.search("o=aprk", "(&(objectclass=dominoPerson)(cn=" + name + "))", null);
			if (ress.hasMore()) {
				SearchResult res = ress.next();
				String name = res.getNameInNamespace();
				Attributes attrs = ctx.getAttributes(name, new String[]{"employeeId"});
				Attribute attr = attrs.get("employeeId");
				this.name = findUserName((String)attr.get());
/*				Attributes attrs = ctx.getAttributes(name, new String[]{"givenname", "sn"});
				Attribute attr = attrs.get("givenname");
				this.name = (String)attr.get();
				attr = attrs.get("sn");
				this.name += " " + attr.get();
*/
			}
			verification = true;
			ctx.close();
		} catch (NamingException e) {
			throw new LoginException(e.toString() + "  " + e.getRootCause());
		}

		mgr = lcb.getMgr();
		
		ip = lcb.getIp();
		
		computer = lcb.getComputer();

		return verification;
	}

	public boolean logout() throws LoginException {
		return false;
	}
	
	private String decriptToken(String token) {
        String webSphereK = WEBSPHERE_SK; // you can get this from your Websphere configuration
        String webSpherePD = "ltpa"; // you can also get this from your Websphere cofiguration

        if (token != null && token.length() > 0) {
            String ltpa3DESK = webSphereK;
            String ltpaPD = webSpherePD;
            try {
                byte[] sK = getSK(ltpa3DESK, ltpaPD);
                String ltpaPlaintext = new String(decryptLtpaToken(token, sK), "UTF8");
                StringTokenizer st = new StringTokenizer(ltpaPlaintext, "%");
                Integer tokenNums = st.countTokens();
                if (tokenNums > 1) {
                    String userInfo = st.nextToken();
                    String expires = st.nextToken();

                    Date d = new Date(Long.parseLong(expires));
                    if (d.before(new Date())) return null;
                    String uid = null;
                    StringTokenizer prs1;
                    StringTokenizer prs2;
                    StringTokenizer prs3;
                    String paramName;
                    prs1 = new StringTokenizer(userInfo, "/");
                    while ((uid == null) && (prs1.hasMoreTokens())) {
                        prs2 = new StringTokenizer(prs1.nextToken(), ",");
                        while ((uid == null) && (prs2.hasMoreTokens())) {
                            prs3 = new StringTokenizer(prs2.nextToken(), "=");
                            paramName = prs3.nextToken();
                            if (paramName.equals("CN")) {
                                uid = prs3.nextToken();
                            }
                        }
                    }
                    return uid;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

    private static byte[] getSK(String shared3DES, String pd) throws Exception {
        MessageDigest md = MessageDigest.getInstance(WEBSPHERE_DA);
        md.update(pd.getBytes());
        byte[] hash3DES = new byte[WEBSPHERE_DL];
        System.arraycopy(md.digest(), 0, hash3DES, 0, WEBSPHERE_DL - 4);
        Arrays.fill(hash3DES, WEBSPHERE_DL - 4, WEBSPHERE_DL, (byte) 0);
        // decrypt the real key and return it
        return decrypt(Base64.decode(shared3DES), hash3DES);
    }

    public static byte[] decryptLtpaToken(String encryptedLtpaToken, byte[] k) throws Exception {
        final byte[] ltpaByteArray = Base64.decode(encryptedLtpaToken);
        return decrypt(ltpaByteArray, k);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] k) throws Exception {
        final Cipher cipher = Cipher.getInstance(WEBSPHERE_CA);
        final KeySpec kSpec = new DESedeKeySpec(k);
        final Key sk = SecretKeyFactory.getInstance(WEBSPHERE_SKF).generateSecret(kSpec);

        cipher.init(Cipher.DECRYPT_MODE, sk);
        return cipher.doFinal(ciphertext);
    }

	private String findUserName(String employeeId) throws LoginException {
		try {
	        Session s = SrvUtils.getSession();
	        try {
		        KrnClass personCls = s.getClassByName("Персонал");
		        KrnAttribute uidAttr = s.getAttributeByName(personCls, "номер личного дела");
		        KrnAttribute userAttr = s.getAttributeByName(personCls, "пользователь системы");
		        KrnClass userCls = s.getClassByName("User");
		        KrnAttribute userNameAttr = s.getAttributeByName(userCls, "name");
		        KrnObject[] personObjs = s.getObjectsByAttribute(personCls.id, uidAttr.id, 0, ComparisonOperations.CO_EQUALS, employeeId, 0);
		        if (personObjs.length > 1) {
		        	throw new LoginException("Найдено несколько сотрудников с номером личного дела '" + employeeId + "'");
		        } else if (personObjs.length == 0) {
		        	throw new LoginException("Сотрудник с номером личного дела '" + employeeId + "' не найден.");
		        }
		        KrnObject[] userObjs = s.getObjects(personObjs[0].id, userAttr.id, new long[0], 0);
		        if (userObjs.length == 0) {
		        	throw new LoginException("Пользователь для данного сотрудника не создан или заблокирован.");
		        }
		        String[] names = s.getStrings(userObjs[0].id, userNameAttr.id, 0, false, 0);
		        return names.length > 0 ? names[0] : null;
	        } finally {
	        	s.release();
	        }
		} catch (KrnException e) {
			e.printStackTrace();
			throw new LoginException(e.getMessage());
		}
	}
}
