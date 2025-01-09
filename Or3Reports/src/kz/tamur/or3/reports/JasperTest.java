package kz.tamur.or3.reports;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JasperTest {

	public static void main(String[] args) {
		try {
			long t = System.currentTimeMillis();
			
			InputStream templateStream = JasperTest.class.getResourceAsStream("/reports/fc_bank.jrxml");
			//JasperReport jasperReport = JasperCompileManager.compileReportToFile("/reports/fc_bank.jrxml", "fc_bank.jprint");

			Map<String, Object> m = new HashMap<String, Object>();
  
			m.put("bankName", "АО \"Народный Банк Казахстана\"");
			m.put("obl", "ҚЫЗЫЛОРДА ОБЛЫСЫ / КЫЗЫЛОРДИНСКАЯ ОБЛАСТЬ");
			m.put("city", "");
			m.put("department", "Отделение банка №5");
			m.put("fio", "Каримов Сакжан");
			m.put("iin", "030219500115");
			m.put("dogovorNumber", "5475687567");
			m.put("dogovorDate", "26.11.2013 ж.");
			m.put("regNumber", "106/2-2014 ");
			m.put("regDate", "17.02.2014 ж.");

			Image image = JasperUtil.loadImage(JasperTest.class.getResourceAsStream("/images/ecp.png"));
			//InputStream image = JasperTest.class.getResourceAsStream("/images/ecp.png");
			m.put("barCodeImg", image);
			
			byte[] pdfBytes = JasperUtil.formPdfReport(templateStream, m);
			templateStream.close();
			
	    	FileOutputStream os = new FileOutputStream("fc_bank2.pdf");
	    	os.write(pdfBytes);
	    	os.close();
	    	
	    	System.out.println(System.currentTimeMillis() - t);
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
}
