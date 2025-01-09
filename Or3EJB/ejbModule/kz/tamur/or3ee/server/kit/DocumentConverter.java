package kz.tamur.or3ee.server.kit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.docx4j.Docx4jProperties;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class DocumentConverter {

	private static OfficeManager mgr;
	private static OfficeDocumentConverter cvr;

	public static synchronized void init(String officeHome,
			String templateProfileDir) {
/*		if (officeHome != null) {
			DefaultOfficeManagerConfiguration cfg = new DefaultOfficeManagerConfiguration();
			cfg.setOfficeHome(officeHome);
			if (templateProfileDir != null)
				cfg.setTemplateProfileDir(new File(templateProfileDir));
			mgr = cfg.buildOfficeManager();
			mgr.start();
			cvr = new OfficeDocumentConverter(mgr);
		}
*/	}

	public static synchronized void release() {
/*		if (mgr != null) {
			mgr.stop();
			mgr = null;
		}
*/	}

	public static void convert(File src, File dst, String outputFormat) {
		DocumentFormat of = cvr.getFormatRegistry().getFormatByMediaType(
				outputFormat);
		cvr.convert(src, dst, of);
	}

	public static byte[] convert(byte[] src, String outputFormat) throws Exception {
		return convertDocxToPdf(new ByteArrayInputStream(src));
		/*
		DocumentFormat of = cvr.getFormatRegistry().getFormatByMediaType(
				outputFormat);
		File srcFile = File.createTempFile("doc", "tmp");
		Funcs.write(src, srcFile);
		File dstFile = File.createTempFile("doc", "tmp");
		cvr.convert(srcFile, dstFile, of);
		byte[] res = Funcs.read(dstFile);
		srcFile.delete();
		dstFile.delete();
		return res;
		*/
	}

	public static void main(String[] args) throws Exception {
		long millis = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream("D:\\tmp\\Uvedomlenie.docx");
		byte[] res = convertDocxToPdf(fis);
		long millis2 = System.currentTimeMillis();
    	System.out.println(millis2 - millis);
		fis.close();
    	FileOutputStream os = new FileOutputStream("D:\\tmp\\Uvedomlenie.docx.pdf");
    	os.write(res);
    	os.close();
		millis = System.currentTimeMillis();
    	System.out.println(millis - millis2);
	}
	
	public static byte[] convertDocxToPdf(InputStream is) throws Exception {
		//ByteArrayInputStream is = new ByteArrayInputStream(src);
		Docx4jProperties.getProperties().put("docx4j.Log4j.Configurator.disabled", "true");
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
		wordMLPackage.setFontMapper(new IdentityPlusMapper());
		org.docx4j.convert.out.pdf.PdfConversion c = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(
				wordMLPackage);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		c.output(os, null);
		os.close();
		return os.toByteArray();
	}
}
