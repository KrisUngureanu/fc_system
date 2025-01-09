package kz.tamur.or3.reports;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class JasperUtil {
	
	public static byte[] formPdfReport(byte[] jrxmlBytes, Map<String, Object> m) {
		InputStream templateStream = new ByteArrayInputStream(jrxmlBytes);
		
		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
			
			JasperPrint jp = JasperFillManager.fillReport(jasperReport, m, new JREmptyDataSource());
			return JasperExportManager.exportReportToPdf(jp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] formPdfReport(String filePath, Map<String, Object> m) {
		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
			
			JasperPrint jp = JasperFillManager.fillReport(jasperReport, m, new JREmptyDataSource());
			return JasperExportManager.exportReportToPdf(jp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] formPdfReport(InputStream is, Map<String, Object> m) {
		try {
	    	long t1 = System.currentTimeMillis();
	    	JasperReport jasperReport = JasperCompileManager.compileReport(is);
	    	long t2 = System.currentTimeMillis();
	    	System.out.println(t2 - t1);
			
			JasperPrint jp = JasperFillManager.fillReport(jasperReport, m, new JREmptyDataSource());
	    	long t3 = System.currentTimeMillis();
	    	System.out.println(t3 - t2);
			byte[] res = JasperExportManager.exportReportToPdf(jp);
	    	long t4 = System.currentTimeMillis();
	    	System.out.println(t4 - t3);
	    	return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image loadImage(byte[] imageBytes) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
			Image res = loadImage(is);
			is.close();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image loadImage(File file) {
		try {
			FileInputStream is = new FileInputStream(file);
			Image res = loadImage(is);
			is.close();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image loadImage(String fileName) {
		try {
			FileInputStream is = new FileInputStream(fileName);
			Image res = loadImage(is);
			is.close();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image loadImage(InputStream imageStream) {
		try {
			Image res = ImageIO.read(imageStream);
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
