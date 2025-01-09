package kz.tamur.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfImageObject;

public class PDFUtils {
	public static Image loadImageFromPDF(String fileName) throws IOException {
		PdfReader reader = new PdfReader(new FileInputStream(fileName));

		if (reader.getNumberOfPages() > 0) {
			PdfDictionary page = reader.getPageN(1);
			
			PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
	        PdfDictionary xobjects = resources.getAsDict(PdfName.XOBJECT);
	        
	        for (Iterator<PdfName> it = xobjects.getKeys().iterator(); it.hasNext(); ) {
		        PdfName imgRef = it.next();
		        PdfStream stream = xobjects.getAsStream(imgRef);
		        
		        PdfImageObject iObj = new PdfImageObject((PRStream)stream);
		        BufferedImage img = iObj.getBufferedImage();
		        
		        saveImageToFile(img);

		        System.out.println(imgRef + " " + stream);
		        //System.out.println(imgRef);
	        }
		        //PRStream stream = (PRStream) xobjects.getAsStream(imgRef);
		}
		
		return null;
	}
	
	public static File saveImageToFile(BufferedImage img) {
		try {
	        File f = File.createTempFile("image", null);
	        FileOutputStream os = new FileOutputStream(f);
			ImageIO.write(img, "PNG", os);
			os.close();
			return f;
		} catch (IOException e) {
			System.err.println("Error writing image");
		}
		return null;
	}
	
	/*public static byte[] convertToPDF(File wordFile) {
		FileInputStream is = null;
		
		try {
			is = new FileInputStream(wordFile);
			return convertToPDF(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Funcs.closeQuietly(is);
		}
		return null;
	}
	
	public static byte[] convertToPDF(byte[] wordDoc) {
		ByteArrayInputStream is = new ByteArrayInputStream(wordDoc);
		try {
			return convertToPDF(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Funcs.closeQuietly(is);
		}
		return null;
	}

	public static byte[] convertToPDF(InputStream wordIs) throws IOException {
		XWPFDocument oDoc = new XWPFDocument(wordIs);
		
		PdfOptions options = PdfOptions.create();
		PdfConverter converter = (PdfConverter)PdfConverter.getInstance();
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		converter.convert(oDoc, os, options);
		
		os.close();
		return os.toByteArray();
	}*/

	public static void main(String[] args) throws IOException {
		//Image img = loadImageFromPDF("F:\\work\\workspace_gbdrn (cluster)\\Or3EJBClient\\doc\\jpg2pdf.pdf");
		//Image img2 = loadImageFromPDF("F:\\work\\workspace_gbdrn (cluster)\\Or3EJBClient\\doc\\MGS AP 01-11-2018.pdf");
		//Image img3 = loadImageFromPDF("F:\\work\\workspace_gbdrn (cluster)\\Or3EJBClient\\doc\\image2315394950441039832.tmp.png");
		
		//byte[] pdf = convertToPDF(new File("D:\\tmp\\pdf\\Приказ.docx"));
		
		//Funcs.write(pdf, new File("D:\\tmp\\pdf\\Приказ.pdf"));
		
	}
}
