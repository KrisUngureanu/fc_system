package kz.tamur.web.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.Base64;

public final class LtpaController extends HttpServlet {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + LtpaController.class);
    private static final int MAX_TOKEN_COUNT = 1000;
    
    private static List<String> UNHARDCODED = new ArrayList<>();
    
    static {
    	UNHARDCODED.add("pvSr2d/0/xn1pTi+Mv8NOUnArM39dSV/eIO0kPDs+yw=");
    	UNHARDCODED.add("p4p800se");
    	UNHARDCODED.add("DESede/ECB/PKCS5Padding");
    	UNHARDCODED.add("TripleDES");
    	UNHARDCODED.add("SHA");
    	UNHARDCODED.add("24");
    }
    
    public static String WEBSPHERE_SK; // "(pvSr2d/0/xn1pTi+Mv8NOUnArM39dSV/eIO0kPDs+yw=)"
    public static String WEBSPHERE_PD; // "(p4p800se)"
    public static String WEBSPHERE_CA; // cipher algr "DESede/ECB/PKCS5Padding"
    public static String WEBSPHERE_SKF; // "TripleDES"
    public static String WEBSPHERE_DA; // digest alg "SHA"
    public static int WEBSPHERE_DL; // digest strength "24"
    

    public void init(final ServletConfig config) throws ServletException {
        // Секретный ключ для авторизации без пароля из другой внешней системы
    	Iterator<String> it = UNHARDCODED.iterator();
    	WEBSPHERE_SK = it.next();
        WEBSPHERE_PD = it.next();
        WEBSPHERE_CA = it.next();
        WEBSPHERE_SKF = it.next();
        WEBSPHERE_DA = it.next();
        WEBSPHERE_DL = Integer.parseInt(it.next());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieName = "LtpaToken";
        String sessionToken = "";
        String webSphereK = WEBSPHERE_SK; // you can get this from your Websphere configuration
        String webSpherePd = WEBSPHERE_PD; // you can also get this from your Websphere cofiguration
        		
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (Funcs.normalizeInput(cookies[i].getName()).equals(cookieName)) {
                sessionToken = Funcs.normalizeInput(cookies[i].getValue());
            }
        }

        if (sessionToken != "") {
            String ltpa3DESK = webSphereK;
            String ltpaPD = webSpherePd;
            try {
                byte[] sK = getSK(ltpa3DESK, ltpaPD);
                String ltpaPlaintext = new String(decryptLtpaToken(sessionToken, sK));
                StringTokenizer st = new StringTokenizer(ltpaPlaintext, "%");
                Integer tokenNums = st.countTokens();
                if (tokenNums > 1) {
                    String userInfo = st.nextToken();

                    //String expires = st.nextToken();
                    //Date d = new Date(Long.parseLong(expires));
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    
                    String uid = "1";
                    StringTokenizer prs1;
                    StringTokenizer prs2;
                    StringTokenizer prs3;
                    String paramName;
                    prs1 = new StringTokenizer(userInfo, "/");

                    int count1 = prs1.countTokens();
                    if (count1 < MAX_TOKEN_COUNT) {
                    	for (int i = 0; i < count1; i++) {
                            prs2 = new StringTokenizer(prs1.nextToken(), ",");
                            int count2 = prs2.countTokens();
                            if (count2 < MAX_TOKEN_COUNT) {
                            	for (int j = 0; j < count2; j++) {
                            		prs3 = new StringTokenizer(prs2.nextToken(), "=");
                            		paramName = Funcs.normalizeInput(prs3.nextToken());
                            		if (paramName.equals("uid")) {
                            			uid = prs3.nextToken();
                            			if (!"1".equals(uid)) break;
                            		}
                            	}
                            }
                			if (!"1".equals(uid)) break;
                    	}
                    }
                    
/*
                    try {
                        java.sql.Statement stmt;
                        java.sql.ResultSet rs;
                        Class.forName("com.mysql.jdbc.Driver");
                        String url = "jdbc:mysql://localhost:3306/tcl-cor";
                        Connection con = DriverManager.getConnection(url, "university", "YbzpFqlfh");
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        rs = stmt.executeQuery("select EMP_ID from employee where LDAP_UID='" + uid + "';");
                        rs.next();
                        int empId = rs.getInt("EMP_ID");
                        stmt = con.createStatement();
                        try {
                            stmt.executeUpdate("update user set USER_LTPATOKEN_EXPIRE='" + sdf.format(d) + "', USER_LTPATOKEN='" + sessionToken + "' where EMP_STU_ID='" + empId + "' and USER_TYPE='emp';");
                        } catch (Exception e) {
                            log.info(e);
                        }
                        con.close();
                    } catch (Exception e) {
                        log.info(e);
                    }
*/
                }
            } catch (Exception e) {
                log.error(e);
            }
        }

        request.getRequestDispatcher("http://do.aprk.kz/authorize.php").forward(request, response);
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
        final Key sK = SecretKeyFactory.getInstance(WEBSPHERE_SKF).generateSecret(kSpec);

        cipher.init(Cipher.DECRYPT_MODE, sK);
        return cipher.doFinal(ciphertext);
    }
}
