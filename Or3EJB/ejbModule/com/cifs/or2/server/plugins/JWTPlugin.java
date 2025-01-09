package com.cifs.or2.server.plugins;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;
import com.eclipsesource.json.JsonObject;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;

public class JWTPlugin implements SrvPlugin {
	
	private static final byte JWT_PART_SEPARATOR = (byte)46;
	
	private Session session;

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Map<String, Object> decodeToken(String token, String pubKey) {
		Map<String, Object> res = new HashMap<>();
		
		try {
			String[] parts = token.split("\\.");
	        if (parts.length == 2 && token.endsWith(".")) {
	            //Tokens with alg='none' have empty String as Signature.
	            parts = new String[] {parts[0], parts[1], ""};
	        }
	        if (parts.length != 3) {
	            throw new Exception(String.format("The token was expected to have 3 parts, but got %s.", parts.length));
	        }
	        
	        Or3JWtToken jwt = new Or3JWtToken(parts[0], parts[1], parts[2]);
		    boolean valid = jwt.verifySignature(pubKey);
		    System.out.println("valid: " + valid);
		    
		    res.put("valid", valid);
		    
		    res.put("alg", jwt.getHeaderJson().get("alg"));
		    res.put("typ", jwt.getHeaderJson().get("typ"));
		    
		    res.put("uin", jwt.getPayloadJson().get("uin"));
		    res.put("sid", jwt.getPayloadJson().get("sid"));
		    res.put("dts", jwt.getPayloadJson().get("dts"));
		    res.put("dte", jwt.getPayloadJson().get("dte"));
		    res.put("binc", jwt.getPayloadJson().get("binc"));
		    res.put("iat", jwt.getPayloadJson().get("iat"));
		    res.put("exp", jwt.getPayloadJson().get("exp"));
		} catch (Exception e) {
		    e.printStackTrace();
		    res.put("error", e.getMessage());
		    res.put("valid", false);
		}

		return res;
	}
	
	public static void main(String[] args) {
		
		String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aW4iOiIxMjM0NTY3ODkxMzIiLCJzaWQiOiJNQ0RCX1NFUlZJQ0U7TUdPVl9TTVNfR1ciLCJkdHMiOiIyMDIxLTA0LTEzVDA3OjM2OjQwIiwiZHRlIjoiMjAyMS0wNC0xM1QwNzo0Njo0MCIsImJpbmMiOiIwMTIzNDU2Nzg5MTIzIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOiIxNTE2MjM5NjIyIn0.LCbPwu4dQ7e2r5-bFfKVYR7eIUL-4uFetsQ-CHEFs-BptBsCYnnGYe30Wg1ch2Hv-TwTJD3U4s1V3kBRH8JI6joX82EVXn00Pjz80VxjxVEZt-k9bB6f29XY_Uq5nB3qFTxG1ofGOC8LHjLQeoAWyZALvTmVfYZpH6G2teK8mjkETVO8yktx9M921RoVe_EdL6qxt46Xx6cJnzWJuCVbEFsOHPfE71Ea16JqjrhK7uKhNS4YXs6t832vTjzhfFydeqYjgljNj2rfZrQMK4KXPEBPKt1zJ5ScQe24AZWGbcD_P1vvgnURkazTbGdkzJ68HaCm_7ysAxWfOPqrNOHrOw";
		String pubKey = "-----BEGIN PUBLIC KEY-----\r\n" + 
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjwEDOZQ5BhLt0HKkJfQ\r\n" + 
				"uLGfw1BR5HQ3Yx26/Aryf2kZAQ5/O0EKPJKdb8lC6Z/Y/yH39dEzuluQk0ACW88b\r\n" + 
				"N1F5XVH4sc+08cS1miHwjz5ct6W6A5Mww/lLNeJGqpJWaueq409mO/FU3RIzrEeH\r\n" + 
				"3qFYFi4UPgBzCjt70iDUWtFo2jfsm696oUz1eIbo191mIfSl3GCgK644mxGAOLXn\r\n" + 
				"tRkUX4RKYST0noiiZ4021rtG6oahSPVvdLKNX27fjSyLg1Nj24ybEZkkAalLnGfH\r\n" + 
				"0fdo8ddSzmPEtK6rvYKd3WWsoZu7vxULLs4qWunFCyW4PmMcjxkO6tGNLKpeGBpc\r\n" + 
				"uQIDAQAB\r\n" + 
				"-----END PUBLIC KEY-----";
		
		Map<String, Object> res = new JWTPlugin().decodeToken(token, pubKey);
		
		System.out.println(res);
		
		System.out.println("----------------------------------------------------------------------------");

		token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aW4iOiIxMjM0NTY3ODkxMzIiLCJzaWQiOiJNQ0RCX1NFUlZJQ0U7TUdPVl9TTVNfR1ciLCJkdHMiOiIyMDIxLTA0LTEzVDA3OjM2OjQwIiwiZHRlIjoiMjAyMS0wNC0xM1QwNzo0Njo0MCIsImJpbmMiOiIwMTIzNDU2Nzg5MTIzIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOiIxNTE2MjM5NjIyIn0.LCbPwu4dQ7e2r5-bFfKVYR7eIUL-4uFetsQ-CHEFs-BptBsCYnnGYe30Wg1ch2Hv-TwTJD3U4s1V3kBRH8JI6joX82EVXn00Pjz80VxjxVEZt-k9bB6f29XY_Uq5nB3qFTxG1ofGOC8LHjLQeoAWyZALvTmVfYZpH6G2teK8mjkETVO8yktx9M921RoVe_EdL6qxt46Xx6cJnzWJuCVbEFsOHPfE71Ea16JqjrhK7uKhNS4YXs6t832vTjzhfFydeqYjgljNj2rfZrQMK4KXPEBPKt1zJ5ScQe24AZWGbcD_P1vvgnURkazTbGdkzJ68HaCm_7ysAxWfOPqrNOHrOw";
		pubKey = "-----BEGIN CERTIFICATE----- MIIG6zCCBNOgAwIBAgIUb6w9cFeIv32H/2Yr+zwRsL7sescwDQYJKoZIhvcNAQEL BQAwUjELMAkGA1UEBhMCS1oxQzBBBgNVBAMMOtKw0JvQotCi0KvSmiDQmtCj05jQ m9CQ0J3QlNCr0KDQo9Co0Ksg0J7QoNCi0JDQm9Cr0pogKFJTQSkwHhcNMjEwMzEw MTExNDU3WhcNMjIwMzEwMTExNDU3WjCCASExJjAkBgNVBAMMHdCW0JXQotCf0JjQ odCe0JIg0J3Qo9Cg0JHQldCaMRkwFwYDVQQEDBDQltCV0KLQn9CY0KHQntCSMRgw FgYDVQQFEw9JSU45MTA4MTAzNTAwODMxCzAJBgNVBAYTAktaMXgwdgYDVQQKDG/Q otCe0JLQkNCg0JjQqdCV0KHQotCS0J4g0KEg0J7Qk9Cg0JDQndCY0KfQldCd0J3Q ntCZINCe0KLQktCV0KLQodCi0JLQldCd0J3QntCh0KLQrNCuICJBUkNUSUMgVEVD SE5PTE9HWSBHUk9VUCIxGDAWBgNVBAsMD0JJTjE4MDM0MDAyNjI2NzEhMB8GA1UE KgwY0KHQkNCT0JDQndCU0KvQmtCe0JLQmNCnMIIBIjANBgkqhkiG9w0BAQEFAAOC AQ8AMIIBCgKCAQEApNJqtRfz/Ze4wLMi2VjrTBJD0y/XTZoWJBWLjoUtjFULEKdr cEhklEMYbaUYC7g/0rJHEA1LjsXcdxsbCer45UFO5WRVtNGEWW8lZ9IMdE8iKjaM NtcCiUdNv8yKAT7F5VKUzOHwfGevFP8gi4GQjDZnK5XWV/hMrSVBPRXYvseIKqQ+ fC4WidB6nYljEdxtmxnCr6cZx/BYdO3dl4rFfSJICA3sgdbwcUtAMCID4doNesMq 0SVDQcyJMrAHpK8ooDrLTGWFydpXvQu8PFPdBiG12PVZJvFhecB1iSYSRUvsm34B W+iGIm3/eXf1JsRGr0iC5FmFRQ8Like74gIIzQIDAQABo4IB5jCCAeIwDgYDVR0P AQH/BAQDAgWgMCgGA1UdJQQhMB8GCCsGAQUFBwMCBggqgw4DAwQBAgYJKoMOAwME AQIBMA8GA1UdIwQIMAaABFtqdBEwHQYDVR0OBBYEFCtRSLLHB52ihQmztz8hCrvn devKMF4GA1UdIARXMFUwUwYHKoMOAwMCAjBIMCEGCCsGAQUFBwIBFhVodHRwOi8v cGtpLmdvdi5rei9jcHMwIwYIKwYBBQUHAgIwFwwVaHR0cDovL3BraS5nb3Yua3ov Y3BzMFYGA1UdHwRPME0wS6BJoEeGIWh0dHA6Ly9jcmwucGtpLmdvdi5rei9uY2Ff cnNhLmNybIYiaHR0cDovL2NybDEucGtpLmdvdi5rei9uY2FfcnNhLmNybDBaBgNV HS4EUzBRME+gTaBLhiNodHRwOi8vY3JsLnBraS5nb3Yua3ovbmNhX2RfcnNhLmNy bIYkaHR0cDovL2NybDEucGtpLmdvdi5rei9uY2FfZF9yc2EuY3JsMGIGCCsGAQUF BwEBBFYwVDAuBggrBgEFBQcwAoYiaHR0cDovL3BraS5nb3Yua3ovY2VydC9uY2Ff cnNhLmNlcjAiBggrBgEFBQcwAYYWaHR0cDovL29jc3AucGtpLmdvdi5rejANBgkq hkiG9w0BAQsFAAOCAgEAZeK6Tty6eDUXDSx1HfohQAQlCIpfuOv0ZgntbbReLMEw T0lqzSwlegZgzwLQ98YErC3fcrImf8g1ewowrPzVmNY569ZmofJ/+lD0+s7Pg33E +9qVERlNAuVhUxquqBHBO2nezFOAJmr6To2zNuRILAJoZjJXTPMJiVXH49k8ZQP/ qZd2bzJKVFS97DB69p6znSUhx92fSz2HjGDjqBoIUzdz1YpuLoCgsOhXxImkqVx+ iQPo8uV7PgEP0yWuAs7lEdnXduIdTnvRskWvpESLoOAp0OW93Jk8R9hb4PmWCScw x4wF5plvMaEnDDzNnizU3hfRMt/SmIqzSwPZvPDibjNBYktni7LPTrr/4U82ViTH SUgrbXzSKpa8gZKmR/aaaD7koouelfNy3ESti9+FGujYKl3ZO18ezTnlfqeO4TSO GXoJrEVs7MAOS3C6Uh5ptz9ZUlBZNj+br0WiDA4E/dD+7QtMMg0Zz27F1mNSurX3 Ihxms0AcCWG1/L6x0CU0OB847bx0+NyxGplQncnE2ZD5fokV70IgldSGEGBMlvSH D5Zvtbzl1XJM4RdKvTDeVdeltltCcZSiy6Oa15MIfss5WYTvj+URjGeQJ4rLy16v lohNxImGSXp3UVp+jukilHJWKq4kRRTZTBF7F+ZvHhAVUTaH4v4udtTnlHKr/PM= -----END CERTIFICATE-----";
		
		res = new JWTPlugin().decodeToken(token, pubKey);
		
		System.out.println(res);

	}
	
	public static RSAPublicKey getPublicKey(String key) {
		try {
			if (key.contains("-----BEGIN PUBLIC KEY-----")) {
				KeyFactory kf = KeyFactory.getInstance("RSA");
				
				key = key.replaceAll("\\r|\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
				
				byte[] keyB = new org.apache.commons.net.util.Base64().decode(key);
				System.out.println("key: " + bytesToHex(keyB));
				
				X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(keyB);
				RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
				return pubKey;
			} else {
				key = key.replaceAll("\\r|\\n", "").replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "");
				
				byte[] keyB = new org.apache.commons.net.util.Base64().decode(key);
				System.out.println("key: " + bytesToHex(keyB));
				
				ByteArrayInputStream is = new ByteArrayInputStream(keyB);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate c = (X509Certificate) cf.generateCertificate(is);
				RSAPublicKey pubKey = (RSAPublicKey) c.getPublicKey();
				return pubKey;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	private static class Or3JWtToken {
		
		String header;
		String payload;
		String signature;
		
		JsonObject headerJson;
		JsonObject payloadJson;
		
		public Or3JWtToken(String header, String payload, String signature) throws Exception {
			super();
			this.header = header;
			this.payload = payload;
			this.signature = signature;
			
			org.apache.commons.net.util.Base64 b64 = new org.apache.commons.net.util.Base64();

			System.out.println("header: " + bytesToHex(b64.decode(header)));
			System.out.println("payload: " + bytesToHex(b64.decode(payload)));

			System.out.println("header: " + new String(b64.decode(header), "UTF-8"));
		    System.out.println("payload: " + new String(b64.decode(payload), "UTF-8"));

		    
			headerJson = JsonObject.readFrom(new String(b64.decode(header), "UTF-8"));

			payloadJson = JsonObject.readFrom(new String(b64.decode(payload), "UTF-8"));
            
            System.out.println("header: " + headerJson);
		    System.out.println("payload: " + payloadJson);
		}

		public boolean verifySignature(String key) throws Exception {
			final Signature s = Signature.getInstance("SHA256withRSA");
	        s.initVerify(JWTPlugin.getPublicKey(key));
	        s.update(header.getBytes());
	        s.update(JWT_PART_SEPARATOR);
	        s.update(payload.getBytes());

			byte[] b = new org.apache.commons.net.util.Base64(true).decode(signature.getBytes());
			System.out.println("signature: " + bytesToHex(b));
			//System.out.println("signature: " + new String(b, StandardCharsets.US_ASCII));
			
			return s.verify(b);
		}

		public JsonObject getHeaderJson() {
			return headerJson;
		}

		public JsonObject getPayloadJson() {
			return payloadJson;
		}
	}
}
