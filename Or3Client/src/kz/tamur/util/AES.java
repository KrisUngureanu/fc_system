package kz.tamur.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	private String sk;
	private String kga;
	private String mda;
	private String salt;
	private int mdl;
	
	private ThreadLocalDateFormat hmFormat = new ThreadLocalDateFormat("HH:mm");

	public AES(String sk, String kga, String mda, String salt, int mdl) throws NoSuchAlgorithmException {
		this.sk = sk;
		this.kga = kga;
		this.mda = mda;
		this.salt = salt;
		this.mdl = mdl;
		KeyGenerator kg = KeyGenerator.getInstance(kga);
		kg.init(256);
	}

	private SecretKeySpec getSK() throws Exception {
		MessageDigest md = MessageDigest.getInstance(mda);
		md.update(this.sk.getBytes("ISO-8859-1"));
		md.update(salt.getBytes("ISO-8859-1"));
		byte[] k = md.digest();
		k = Arrays.copyOf(k, mdl);
		return new SecretKeySpec(k, kga);
	}

	private SecretKeySpec getSK(String time) throws Exception {
		MessageDigest md = MessageDigest.getInstance(mda);
		md.update(time.getBytes("ISO-8859-1"));
		md.update(this.sk.getBytes("ISO-8859-1"));
		md.update(salt.getBytes("ISO-8859-1"));
		byte[] k = md.digest();
		k = Arrays.copyOf(k, mdl);
		return new SecretKeySpec(k, kga);
	}

	public byte[] encrypt(String userData) throws Exception {
		SecretKeySpec spec = getSK();
		Cipher cipher = Cipher.getInstance(kga);
		cipher.init(Cipher.ENCRYPT_MODE, spec);
		return cipher.doFinal(userData.getBytes());
	}

	public byte[] encryptUsingTime(String userData) throws Exception {
		Calendar c = Calendar.getInstance();
		int m1 = c.get(Calendar.MINUTE);
		
		int m2 = (int) (10 * Math.round((1.0d * m1)/10));
		c.add(Calendar.MINUTE, m2 - m1);
		
		String d = hmFormat.format(c.getTime());
		//System.out.println("key = " + d + this.sk);

		SecretKeySpec spec = getSK(d);
		Cipher cipher = Cipher.getInstance(kga);
		cipher.init(Cipher.ENCRYPT_MODE, spec);
		return cipher.doFinal(userData.getBytes());
	}

	public byte[] decrypt(byte[] encrypted) throws Exception {
		SecretKeySpec spec = getSK();
		Cipher cipher = Cipher.getInstance(kga);
		cipher.init(Cipher.DECRYPT_MODE, spec);
		return cipher.doFinal(encrypted);
	}

	public byte[] decrypt3Times(byte[] encrypted) throws Exception {
		Calendar c = Calendar.getInstance();

		int m1 = c.get(Calendar.MINUTE);
		
		int m2 = (int) (10 * Math.round((1.0d * m1)/10));
		c.add(Calendar.MINUTE, m2 - m1);
		
		String d = hmFormat.format(c.getTime());
		//System.out.println("key1 = " + d + this.sk);
		SecretKeySpec spec = getSK(d);
		Cipher cipher = Cipher.getInstance(kga);
		cipher.init(Cipher.DECRYPT_MODE, spec);
		
		byte[] res = null;
		
		try {
			res = cipher.doFinal(encrypted);
		} catch (Exception e1) {
			System.out.println("key1 doesn't match!!!");

			c.add(Calendar.MINUTE, -10);
			d = hmFormat.format(c.getTime());
			//System.out.println("key2 = " + d + this.sk);
			spec = getSK(d);
			cipher.init(Cipher.DECRYPT_MODE, spec);
			try {
				res = cipher.doFinal(encrypted);
			} catch (Exception e2) {
				System.out.println("key2 doesn't maych!!!");

				c.add(Calendar.MINUTE, 20);
				d = hmFormat.format(c.getTime());
				//System.out.println("key3 = " + d + this.sk);
				spec = getSK(d);
				cipher.init(Cipher.DECRYPT_MODE, spec);
				try {
					res = cipher.doFinal(encrypted);
				} catch (Exception e3) {
					System.out.println("key3 doesn't maych!!!");
				}			
			}			
		}
		return res;
	}

	public static String toBase64(String str) {
		str = str.replaceAll("-", "+").replaceAll("_", "/");
		while (str.length()%4 > 0) {
			str = str + "=";
		}
		return str;
	}
	
	public static String fromBase64(String str) {
		str = str.replaceAll("\\+", "-").replaceAll("\\/", "_");
		int beg = str.indexOf("=");
		if (beg > -1) {
			return str.substring(0, beg);
		}
		return str;
	}
/*
	public static void main(String[] args) throws Exception {
		System.out.println(Funcs.sanitizeHtml(System.getProperty("user.timezone")));
		System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));

		System.out.println(UUID.randomUUID());
		
		AES aes = new AES("35dfe6b8-12ed-4b30-bc81-bb96d8f94605");
		//byte[] b1 = aes.encryptUsingTime("кадрДаулетМаг");
		byte[] b1 = aes.encrypt("itrud_pub");
		byte[] b3 = aes.decrypt(b1);
		String str = new String(b3);

		System.out.println("Message:" + str);
		
		String token = new String(kz.gov.pki.kalkan.util.encoders.Base64.encode(b1));
		System.out.println("before = " + token);
		token = fromBase64(token);
		System.out.println("after = " + token);
		
		//String token = "Gh90j-W8eDYkuhwc1fqdmA";
		
		token = AES.toBase64("4WyZ2TK1aiVHb6q9Xcu8GPqymUZWrp8LVZj1ODQV2do");
		System.out.println("after = " + token);
		byte[] b2 = Base64.decode(token);
		b3 = aes.decrypt3Times(b2);
		str = new String(b3);

		//b3 = new AES("35dfe6b8-12ed-4b30-bc81-bb96d8f94605").decrypt(b2);
		//str = new String(b3);

		System.out.println("Message:" + str);
		
	}
*/
}
