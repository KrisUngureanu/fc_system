package kz.tamur.plugins;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
//import kz.tamur.morena.ScanSession;
//import kz.tamur.morena.SynchronousHelper;
import kz.tamur.rt.orlang.ClientPlugin;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

//import eu.gnome.morena.Configuration;
//import eu.gnome.morena.Device;
//import eu.gnome.morena.Manager;
//import eu.gnome.morena.Scanner;

public class BarcodePlugin implements ClientPlugin {
	
//	static Manager manager;
//	static String deviceName;
//	static int pages = 0;

	public BarcodePlugin() {}
	
	public Map<String, List<byte[]>> getDocuments() {
		File destination = new File("C:\\destination");
		return getDocuments(destination);
	}
	
	public Map<String, List<byte[]>> getDocuments(File destination) {
    	Map<String, List<byte[]>> documentsByBarсode = new HashMap<>();
    	if (destination.exists() && destination.isDirectory()) {
    		File[] files = destination.listFiles();	// TODO Cделать фильтр файлов по формату
    		String barcode = null;
    		for (final File file: files) {
    		    try {
    		    	BufferedImage image = ImageIO.read(file);
    		    	if (image != null) {
    		    		LuminanceSource source = new BufferedImageLuminanceSource(image);
    	    		    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    	    		    Result result = null;
    	    		    try {
    	    		    	result = new MultiFormatReader().decode(bitmap);
    	    		    } catch(com.google.zxing.NotFoundException e) {
    	    		    	System.out.println("Штрих-код не найден на листе!");
    	    		    }
    	    		    if (result == null || result.getText() == null) {
        	    		    if (barcode != null && documentsByBarсode.containsKey(barcode)) {
        	    		    	documentsByBarсode.get(barcode).add(Files.readAllBytes(file.toPath()));
        	    		    }
    	    		    } else {
    	    		    	if (barcode != null && barcode.equals(result.getText())) {
        	    		    	documentsByBarсode.get(barcode).add(Files.readAllBytes(file.toPath()));
    	    		    	} else {
        	    		    	barcode = result.getText();
        	    		    	documentsByBarсode.put(barcode, new ArrayList<byte[]>() {{add(Files.readAllBytes(file.toPath()));}});
    	    		    	}
    	    		    } 
    		    	}
    		    } catch (IOException e) {
    		      e.printStackTrace();
    		    }
    		}
    	}
    	return documentsByBarсode;
    }
	
	public byte[] getPDFDocumentByBarcode(String myBarcode, String sourcePath, String destinationPath) throws DocumentException, MalformedURLException, IOException {
    	Map<String, List<String>> documentsByBarсode = new HashMap<>();
    	File source = new File(sourcePath);
    	if (source.exists() && source.isDirectory()) {
    		File[] files = source.listFiles();
    		String barcode = null;
    		System.out.println("Файлов в папке-источнике: " + files.length);
    		for (final File file: files) {
    		    try {
    		    	BufferedImage image = ImageIO.read(file);
    		    	if (image != null) {
        	    		double ratioX = 0.4;
        	    		double ratioY = 0.18;
        	    		
        	            BufferedImage input = new BufferedImage((int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()), BufferedImage.TYPE_BYTE_GRAY);
        	
        	            Graphics g = input.createGraphics();
        	            g.setColor(Color.white);
        	            g.fillRect(0, 0, (int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()));
        	            g.drawImage(image, 0, 0, (int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()),
        	            		image.getWidth() - (int)(ratioX * image.getWidth()), image.getHeight() - (int)(ratioY * image.getHeight()),
        	            		image.getWidth(), image.getHeight(), null);
        	            g.dispose();
        	            g = null;

    		    		LuminanceSource luminanceSource = new BufferedImageLuminanceSource(input);
    	    		    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
    	    		    Result result = null;
    	    		    try {
    	    		    	result = new MultiFormatReader().decode(bitmap);
    	    		    } catch(com.google.zxing.NotFoundException e) {
    	    		    	System.out.println("Штрих-код не найден на листе!");
    	    		    }
    	    		    if (result == null || result.getText() == null) {
        	    		    if (barcode != null && documentsByBarсode.containsKey(barcode)) {
        	    		    	documentsByBarсode.get(barcode).add(file.getAbsolutePath());
        	    		    }
    	    		    } else {
    	    		    	if (barcode != null && barcode.equals(result.getText())) {
        	    		    	documentsByBarсode.get(barcode).add(file.getAbsolutePath());
    	    		    	} else {
        	    		    	barcode = result.getText();
        	    		    	documentsByBarсode.put(barcode, new ArrayList<String>() {{add(file.getAbsolutePath());}});
    	    		    	}
    	    		    } 
    		    	}
    		    } catch (IOException e) {
    		      e.printStackTrace();
    		    }
    		}
    	}
		System.out.println("Размеченных документов: " + documentsByBarсode.size());
		System.out.println(documentsByBarсode);
    	if (documentsByBarсode.containsKey(myBarcode)) {
            File pdfFile = new File(destinationPath + myBarcode + ".pdf");
	    	Document document = new Document();
	        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        document.open();
	        List<String> files = documentsByBarсode.get(myBarcode);
	        for (String file : files) {
	    		System.out.println("Листов в документе: " + files.size());
	            document.newPage();
	            Image image = Image.getInstance(file);
	            image.setAbsolutePosition(0, 0);
	            image.setBorderWidth(0);
	            image.scaleAbsolute(PageSize.A4);
	            document.add(image);
	        }
	        document.close();
	        return Files.readAllBytes(pdfFile.toPath());
    	}
    	return null;
	}
	
	public void removeFiles(List<String> paths) {
		for(String path: paths) {
			File file = new File(path);
			if (file.delete()) {
				System.out.println("Файл " + path + " успешно удален!");
			} else {
				System.out.println("Ошибка удаления файла " + path + "!");
			}
		}
	}
	
	public List<Object> getPDFDocumentByBarcode(String myBarcode, List<String> sourcesPaths, String destinationPath) throws DocumentException, MalformedURLException, IOException {
    	for (String sourcePath: sourcesPaths) {
        	Map<String, List<String>> documentsByBarсode = new HashMap<>();
	    	File source = new File(sourcePath);
	    	if (source.exists() && source.isDirectory()) {
	    		File[] files = source.listFiles();
	    		String barcode = null;
	    		System.out.println("Количество Файлов в источнике " + source + ": " + files.length);
	    		boolean isFound = false;
	    		for (final File file: files) {
	    		    try {
	    		    	BufferedImage image = ImageIO.read(file);
	    		    	if (image != null) {
	        	    		double ratioX = 0.4;
	        	    		double ratioY = 0.18;
	        	    		
	        	            BufferedImage input = new BufferedImage((int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()), BufferedImage.TYPE_BYTE_GRAY);
	        	
	        	            Graphics g = input.createGraphics();
	        	            g.setColor(Color.white);
	        	            g.fillRect(0, 0, (int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()));
	        	            g.drawImage(image, 0, 0, (int)(ratioX * image.getWidth()), (int)(ratioY * image.getHeight()),
	        	            		image.getWidth() - (int)(ratioX * image.getWidth()), image.getHeight() - (int)(ratioY * image.getHeight()),
	        	            		image.getWidth(), image.getHeight(), null);
	        	            g.dispose();
	        	            g = null;
	
	    		    		LuminanceSource luminanceSource = new BufferedImageLuminanceSource(input);
	    	    		    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
	    	    		    Result result = null;
	    	    		    try {
	    	    		    	result = new MultiFormatReader().decode(bitmap);
	    	    		    } catch(com.google.zxing.NotFoundException e) {
	    	    		    	System.out.println("Штрих-код не найден на листе!");
	    	    		    }
	    	    		    if (result == null || result.getText() == null) {
	        	    		    if (barcode != null && documentsByBarсode.containsKey(barcode)) {
	        	    		    	documentsByBarсode.get(barcode).add(file.getAbsolutePath());
	        	    		    }
	    	    		    } else {
	    	    		    	if (barcode != null && barcode.equals(result.getText())) {
	        	    		    	documentsByBarсode.get(barcode).add(file.getAbsolutePath());
	    	    		    	} else {
	    	    		    		if (isFound) {
	    	    		    			break;
	    	    		    		}
	        	    		    	barcode = result.getText();
	        	    		    	documentsByBarсode.put(barcode, new ArrayList<String>() {{add(file.getAbsolutePath());}});
	        	    		    	if (myBarcode.equals(barcode)) {
	        	    		    		isFound = true;
	        	    		    	}
	    	    		    	}
	    	    		    }
	    		    	}
	    		    } catch (IOException e) {
	    		      e.printStackTrace();
	    		    }
	    		}
	    	}
			System.out.println("Количество размеченных документов: " + documentsByBarсode.size());
			System.out.println(documentsByBarсode);
	    	if (documentsByBarсode.containsKey(myBarcode)) {
	            File pdfFile = new File(destinationPath + myBarcode + ".pdf");
		    	Document document = new Document();
		        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		        document.open();
		        List<String> files = documentsByBarсode.get(myBarcode);
	    		System.out.println("Количество листов в документе: " + files.size());
		        for (String file : files) {
		            document.newPage();
		            Image image = Image.getInstance(file);
		            image.setAbsolutePosition(0, 0);
		            image.setBorderWidth(0);
		            image.scaleAbsolute(PageSize.A4);
		            document.add(image);
		        }
		        document.close();
		        List<Object> res = new ArrayList<>();
		        res.add(Files.readAllBytes(pdfFile.toPath()));
		        files.add(pdfFile.getAbsolutePath());
		        res.add(files);
		        return res;
	    	}
    	}
    	return null;
	}
	
//	public Map<String, List<byte[]>> getDocumentsFromScanner(List<String> args) {
//    	Map<String, List<byte[]>> documentsByBarсode = new HashMap<>();
//		try {
//			Configuration.setLogLevel(Level.FINEST);
//			manager = Manager.getInstance();
//			if (args.size() == 0) {
//				simpleScan(documentsByBarсode);
//			} else if (args.get(0).equalsIgnoreCase("batch")) {
//				batchScan(documentsByBarсode);
//			} else if (args.size() == 1) {
//				deviceName = args.get(0);
//				simpleScan(documentsByBarсode);
//			} else if (args.size() > 1 && args.get(1).equalsIgnoreCase("batch")) {
//				deviceName = args.get(0);
//				if (args.size() == 3) {
//					pages = Integer.parseInt(args.get(2));
//				}
//				batchScan(documentsByBarсode);
//			} else {
//				throw new IllegalArgumentException("Заданы неверные параметры!");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			manager.close();
//		}
//    	return documentsByBarсode;
//	}
	
//	private void simpleScan(Map<String, List<byte[]>> documentsByBarсode) throws Exception {
//		Device device = selectDevice();
//		Scanner scanner = (Scanner) device;
//		scanner.setMode(Scanner.RGB_8);
//		scanner.setResolution(75);
//		scanner.setFrame(0, 0, 622, 874);
//		BufferedImage image = SynchronousHelper.scanImage(device);
//		LuminanceSource source = new BufferedImageLuminanceSource(image);
//	    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//	    Result result = null;
//	    try {
//	    	result = new MultiFormatReader().decode(bitmap);
//	    } catch(com.google.zxing.NotFoundException e) {
//	    	System.out.println("Штрих-код не найден на листе!");
//	    }
//	    if (result != null && result.getText() != null) {
//	    	String barcode = result.getText();
//	    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
//		    	ImageIO.write(image, "BMP", baos);
//		    	if (documentsByBarсode.containsKey(barcode)) {
//			    	documentsByBarсode.get(barcode).add(baos.toByteArray());
//		    	} else {
//			    	documentsByBarсode.put(barcode, Arrays.asList(baos.toByteArray()));
//		    	}
//	    	}
//	    } 
//	}

//	private void batchScan(Map<String, List<byte[]>> documentsByBarсode) throws Exception {
//		Device device = selectDevice();
//		Scanner scanner = (Scanner) device;
//		scanner.setMode(Scanner.RGB_8);
//		scanner.setResolution(200);
//		int feederUnit = scanner.getFeederFunctionalUnit();
//		if (feederUnit < 0) {
//			feederUnit = 0;
//		}
//		if (scanner.isDuplexSupported()) {
//			scanner.setDuplexEnabled(true);
//		}
//		ScanSession session = new ScanSession();
//		try {
//			session.startSession(device, feederUnit, pages);
//			File file = null;
//    		String barcode = null;
//			while (null != (file = session.getImageFile())) {
//				BufferedImage image = ImageIO.read(file);
//			    LuminanceSource source = new BufferedImageLuminanceSource(image);
//    		    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//    		    Result result = null;
//    		    try {
//    		    	result = new MultiFormatReader().decode(bitmap);
//    		    } catch(com.google.zxing.NotFoundException e) {
//    		    	System.out.println("Штрих-код не найден на листе!");
//    		    }
//    		    if (result == null || result.getText() == null) {
//	    		    if (barcode != null && documentsByBarсode.containsKey(barcode)) {
//	    		    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
//		    		    	ImageIO.write(image, "BMP", baos);
//		    		    	documentsByBarсode.get(barcode).add(baos.toByteArray());
//	    		    	}
//	    		    }
//    		    } else {
//    		    	if (barcode != null && barcode.equals(result.getText())) {
//	    		    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
//		    		    	ImageIO.write(image, "BMP", baos);
//		    		    	documentsByBarсode.get(barcode).add(baos.toByteArray());
//	    		    	}
//    		    	} else {
//	    		    	barcode = result.getText();
//	    		    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
//		    		    	ImageIO.write(image, "BMP", baos);
//		    		    	documentsByBarсode.put(barcode, Arrays.asList(baos.toByteArray()));
//	    		    	}
//    		    	}
//    		    }
//			}
//		} catch (Exception e) {
//			if (session.isEmptyFeeder()) {
//				System.err.println("В устройстве подачи документов больше нет листов!");
//			} else {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private Device selectDevice() throws Exception {
//		List<? extends Device> devices = manager.listDevices();
//		Device device = null;
//		if (deviceName != null) {
//			for (int i = 0; i < devices.size(); i++) {
//				if (devices.get(i).toString().startsWith(deviceName)) {
//					device = devices.get(i);
//				}
//			}
//		} else {
//			for (int i = 0; i < devices.size(); i++) {
//				if (devices.get(i) instanceof Scanner) {
//					device = devices.get(i);
//					break;
//				}
//			}
//		}
//		if (device == null) {
//			throw new Exception("Устройства не обнаружены!");
//		}
//		System.out.println("Выбранное устройство: " + device);
//		return device;
//	}
}