package kz.tamur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Version {
	private static String version = "v0-0-0";

	public Version() {
		try {
			InputStream in = getClass().getResourceAsStream("/META-INF/version.info");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			version = br.readLine();
			
		} catch (IOException e1) {
			System.out.println("Warning, version.info. Видать просто файл пока не создан, это не критично");
		}
	}
	
	public String getVersion(){
		return version;
	}
}
