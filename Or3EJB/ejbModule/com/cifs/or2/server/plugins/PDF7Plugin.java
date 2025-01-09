package com.cifs.or2.server.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import kz.tamur.lang.BarcodeOp;
//import kz.tamur.rn3.qrcode.QRCodeElement;

public class PDF7Plugin implements SrvPlugin {

	public static final String ARIAL = "arial.ttf";
	public static final String TIMES = "times.ttf";
	public static final String SANS = "FreeSans.ttf";
	
    Session s;
    
    @Override
    public Session getSession() {
        return s;
    }

    @Override
    public void setSession(Session session) {
        s = session;
    }
    
	public OutputStream createByteArrayOutputStream() throws Exception {
		return new ByteArrayOutputStream();
	}

	public PdfReader createReader(byte[] src) throws Exception {
		return new PdfReader(new ByteArrayInputStream(src));
	}

	public PdfReader createReader(InputStream is) throws Exception {
		return new PdfReader(is);
	}

	public PdfReader createReader(String fileName) throws Exception {
		return new PdfReader(fileName);
	}

	public PdfReader createReader(File src) throws Exception {
		return new PdfReader(new FileInputStream(src));
	}

	public PdfWriter createWriter(OutputStream os) throws Exception {
    	return new PdfWriter(os);
	}

	public PdfWriter createWriter(String fileName) throws Exception {
    	return new PdfWriter(fileName);
	}

	public PdfWriter createWriter(File dest) throws Exception {
    	return new PdfWriter(dest);
	}
	
	// Создание существующего документа PDF для чтения
	public PdfDocument createPdf(PdfReader reader) throws Exception {
		return new PdfDocument(reader);
	}

	// Создание нового документа PDF для записи
	public PdfDocument createPdf(PdfWriter writer) throws Exception {
		return new PdfDocument(writer);
	}

	// Создание документа PDF для редактирования
	public PdfDocument createPdf(PdfReader reader, PdfWriter writer) throws Exception {
		return new PdfDocument(reader, writer);
	}

	public Document createDocumentRoot(PdfDocument pdfDoc) throws Exception {
		Document document = new Document(pdfDoc, PageSize.A4);
		return document;
	}

	public Document createDocumentRoot(PdfDocument pdfDoc, PageSize size) throws Exception {
		Document document = new Document(pdfDoc, size);
		return document;
	}

	public Document createDocumentRoot(PdfDocument pdfDoc, Number left, Number right, Number top, Number bottom) throws Exception {
		Document document = new Document(pdfDoc, PageSize.A4);
		document.setMargins(top.floatValue(), right.floatValue(), bottom.floatValue(), left.floatValue());
		return document;
	}

	public Document createDocumentRoot(PdfDocument pdfDoc, PageSize size, Number left, Number right, Number top, Number bottom) throws Exception {
		Document document = new Document(pdfDoc, size);
		document.setMargins(top.floatValue(), right.floatValue(), bottom.floatValue(), left.floatValue());
		return document;
	}

	public void savePdf(Document document) {
    	document.close();
	}
	
	// считать фонт из файла в Or3Client.jar (доступны arial.ttf, times.ttf, FreeSans.ttf)
	public byte[] loadFont(String fontName) throws Exception {
		InputStream fis = PDF7Plugin.class.getResourceAsStream("/" + fontName);
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();
		return b;
	}

	public PdfFont createFont(String fontName) throws Exception {
		return PdfFontFactory.createFont(loadFont(fontName), PdfEncodings.IDENTITY_H, true);
	}

	public PdfFont createFont(byte[] fontData) throws Exception {
		return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H, true);
	}

	public Color createColor(Number red, Number green, Number blue) {
		return new DeviceRgb(red.intValue(), green.intValue(), blue.intValue());
	}
	
	public Color createColor(Number cyan, Number magenta, Number yellow, Number black) {
		return new DeviceCmyk(cyan.intValue(), magenta.intValue(), yellow.intValue(), black.intValue());
	}

	public Document newPage(Document root) {
		return root.add(new AreaBreak());
	}

	public Document newPage(Document root, PageSize size) {
		return root.add(new AreaBreak(size));
	}

	public Document newPage(Document root, PageSize size, Number left, Number right, Number top, Number bottom) {
		Document doc = newPage(root, size);
		doc.setMargins(top.floatValue(), right.floatValue(), bottom.floatValue(), left.floatValue());
    	return doc;
	}

	public Table createTable(java.util.List<Number> widths) throws Exception {
		return createTable(widths, 100);
	}
	
	public Table createTable(java.util.List<Number> widths, Number widthPercent) throws Exception {
		float p = widthPercent.floatValue() / 100;
        
        UnitValue[] ws = new UnitValue[widths.size()];
        for (int i=0; i<widths.size(); i++)
        	ws[i] = UnitValue.createPercentValue(p * widths.get(i).floatValue());
        
		Table table = new Table(ws);
    	return table;
	}
	
	public Cell createCell() throws Exception {
        Cell pdfCell = new Cell();
    	return pdfCell;
	}

	public Cell createCell(int rowspan, int colspan) throws Exception {
		Cell pdfCell = new Cell(rowspan, colspan);
    	return pdfCell;
	}

	public Cell createCell(Image img) throws Exception {
		Cell pdfCell = new Cell();
		pdfCell.add(img);
    	return pdfCell;
	}
	
	public void setText(Cell cell, String text, PdfFont font, Number height, Color color) {
		cell.add(new Paragraph(text)).setFont(font).setFontSize(height.floatValue());
		if (color != null)
			cell.setFontColor(color);
	}
	
	public Image createImage(JFreeChart bar, int width, int height) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bar.createBufferedImage(width, height), "PNG", os);
		os.close();
		return createImage(os.toByteArray(), width, height);
	}

	public Image createImage(byte[] b, float width, float height) throws Exception {
		Image img = new Image(ImageDataFactory.create(b));
    	if (width > 0 && height > 0)
    		img.scaleAbsolute(width, height);
    	else if (width > 0)
    		img.scaleToFit(width, img.getImageHeight() * width / img.getImageWidth());
    	else if (height > 0)
    		img.scaleToFit(img.getImageWidth() * height / img.getImageHeight(), height);
    	return img;
	}

    public void addHeader(PdfDocument pdf, IBlockElement headerElement, float left, float right, float top) {
    	pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PageHeaderEventHandler(headerElement, left, right, top));
    }

    public void addFooter(PdfDocument pdf, IBlockElement footerElement, float left, float right, float bottom) {
    	pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PageFooterEventHandler(footerElement, left, right, bottom));
    }


    public static void main(String[] args) throws Exception {
    	PDF7Plugin plugin = new PDF7Plugin();
    	
    	File dest = new File("D://pdf7-1.pdf");
    	
    	//Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        
        // Create a PdfFont
        PdfFont arial = PdfFontFactory.createFont(plugin.loadFont(ARIAL), PdfEncodings.IDENTITY_H, true);
        PdfFont sans = PdfFontFactory.createFont(plugin.loadFont(SANS), PdfEncodings.IDENTITY_H, true);
        PdfFont times = PdfFontFactory.createFont(plugin.loadFont(TIMES), PdfEncodings.IDENTITY_H, true);
        
        Color gray = new DeviceCmyk(0.f, 0.f, 0.f, 0.875f);
        Color black = DeviceRgb.BLACK;
        
        // Текст для QR
		String text = "Подразделение: Отдел города Темиртау по земельному кадастру и недвижимости филиала некоммерческого акционерного общества «Государственная корпорация «Правительство для граждан» по Карагандинской области";
		text = text + "\nНомер заказа: 45ebcaed-929c-4b7e-8ad2-485ec10d02c8";
		text = text + "\nДата заказа: 04.10.2020 20:55";
		text = text + "\nЗаявитель: 100440000133; 760625301668";
		text = text + "\nУполномоченный представитель: 900319350069";
		text = text + "\nАдрес объекта кондоминиума: обл. Карагандинская, г. Темиртау, мкр. 9, д. 29";
		text = text + "\nКадастровый номер: 09:145:029:019; 09:145:029:019:1";
		text = text + "\nВид недвижимости: Земельный участок; Многоквартирный дом";
		text = text
				+ "\nЭЦП: MIIEuDCCBGKgAwIBAgIURtXc4BleN5gXSZORXqecSofLwYYwDQYJKoMOAwoBAQECBQAwUzELMAkGA1UEBhMCS1oxRDBCBgNVBAMMO9Kw0JvQotCi0KvSmiDQmtCj05jQm9CQ0J3QlNCr0KDQo9Co0Ksg0J7QoNCi0JDQm9Cr0pogKEdPU1QpMB4XDTIwMDUxMTE2MDcyN1oXDTIxMDUxMTE2MDcyN1owggFiMSAwHgYDVQQDDBfQn9Cg0JjQmdCc0JDQmiDQmNCS0JDQnTEXMBUGA1UEBAwO0J/QoNCY0JnQnNCQ0JoxGDAWBgNVBAUTD0lJTjkwMDMxOTM1MDA2OTELMAkGA1UEBhMCS1oxHDAaBgNVBAcME9Cd0KPQoC3QodCj0JvQotCQ0J0xHDAaBgNVBAgME9Cd0KPQoC3QodCj0JvQotCQ0J0xazBpBgNVBAoMYtCi0J7QktCQ0KDQmNCp0JXQodCi0JLQniDQoSDQntCT0KDQkNCd0JjQp9CV0J3QndCe0Jkg0J7QotCS0JXQotCh0KLQktCV0J3QndCe0KHQotCs0K4gItCi0JDQnNCj0KAiMRgwFgYDVQQLDA9CSU4wMjAzNDAwMDI3NTMxHTAbBgNVBCoMFNCS0JDQodCY0JvQrNCV0JLQmNCnMRwwGgYJKoZIhvcNAQkBFg1JVkFOQFRBTVVSLktaMGwwJQYJKoMOAwoBAQEBMBgGCiqDDgMKAQEBAQEGCiqDDgMKAQMBAQADQwAEQH6suoqKBDHITqDR1dvT9uysFzIgvCt9xOvQ0/2257Bl7SOWQOPTHIZcbUcyrho3QK3WseGWcH7ir9GMdg9upMKjggHrMIIB5zAOBgNVHQ8BAf8EBAMCBsAwKAYDVR0lBCEwHwYIKwYBBQUHAwQGCCqDDgMDBAECBgkqgw4DAwQBAgUwDwYDVR0jBAgwBoAEW2pz6TAdBgNVHQ4EFgQU77Oc+tdtj2NMj9G4vbLlfQx2KiswXgYDVR0gBFcwVTBTBgcqgw4DAwIBMEgwIQYIKwYBBQUHAgEWFWh0dHA6Ly9wa2kuZ292Lmt6L2NwczAjBggrBgEFBQcCAjAXDBVodHRwOi8vcGtpLmdvdi5rei9jcHMwWAYDVR0fBFEwTzBNoEugSYYiaHR0cDovL2NybC5wa2kuZ292Lmt6L25jYV9nb3N0LmNybIYjaHR0cDovL2NybDEucGtpLmdvdi5rei9uY2FfZ29zdC5jcmwwXAYDVR0uBFUwUzBRoE+gTYYkaHR0cDovL2NybC5wa2kuZ292Lmt6L25jYV9kX2dvc3QuY3JshiVodHRwOi8vY3JsMS5wa2kuZ292Lmt6L25jYV9kX2dvc3QuY3JsMGMGCCsGAQUFBwEBBFcwVTAvBggrBgEFBQcwAoYjaHR0cDovL3BraS5nb3Yua3ovY2VydC9uY2FfZ29zdC5jZXIwIgYIKwYBBQUHMAGGFmh0dHA6Ly9vY3NwLnBraS5nb3Yua3owDQYJKoMOAwoBAQECBQADQQD7J0RhkUMoegmwANd1fw88rdDAkCTBQKuUqmH7Y8dgEl2EVV59P38EAQt+8YhJYlkcTX9+EObl079rldf1z9a4";

        // Создаем массив QR-кодов
		ArrayList<byte[]> listQrCode = new ArrayList<byte[]>();

		int i = 0;
		int max = 7;
		int porc = 700;

		BarcodeOp BARCODE = new BarcodeOp();

		java.util.List<String> text_porcs = BARCODE.split(text, porc, "-");
		int text_porcs_size = text_porcs.size();

		/*
		 * kz.tamur.rn3.qrcode.ObjectFactory factory = new
		 * kz.tamur.rn3.qrcode.ObjectFactory();
		 * 
		 * while (i < max && i < text_porcs_size) { QRCodeElement element =
		 * factory.createQRCodeElement(); element.setCreationDate(new
		 * com.cifs.or2.kernel.KrnDate().getXmlDate());
		 * element.setElementData(text_porcs.get(i)); element.setElementNumber(i + 1);
		 * element.setElementAmount(text_porcs_size);
		 * element.setObjectID(java.util.UUID.randomUUID().toString());
		 * 
		 * java.io.StringWriter writer2 = new java.io.StringWriter();
		 * kz.tamur.shep.common.JAXBContextBuilder.buildContext("kz.tamur.rn3.qrcode").
		 * createMarshaller() .marshal(factory.createQRCodeElement(element), writer2);
		 * 
		 * listQrCode.add(BARCODE.getQRCodeFor(writer2.toString(), 30, 30));
		 * writer2.close();
		 * 
		 * i = i + 1; }
		 */		
		UnitValue[] widths = new UnitValue[max];

		for (i=0; i<max; i++) {
			widths[i] = UnitValue.createPercentValue(100.0f / max);
		}

		Table table = new Table(widths);
        
		text = "Осы құжат «Электрондық құжат жəне электрондық цифрлық қолтаңба туралы» 2003 жылғы 7 қаңтардағы N 370-II ҚРЗ 1 бабына сəйкес қағаз жеткiзгiштегi құжатпен бiрдей.\n";
		text = text
				+ "Данный документ согласно пункту 1 статьи 370-II ЗРК от 7 января 2003 года «Об электронном документе и электронной цифровой подписи» равнозначен документу на бумажном носителе.\n";

		Cell cell = new Cell(1, widths.length).add(new Paragraph(text)).setFont(times).setFontColor(black).setFontSize(8)
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setTextAlignment(TextAlignment.JUSTIFIED)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(Border.NO_BORDER);

		table.addCell(cell);

		for (i=0; i<max; i++) {
			
			if (i < listQrCode.size()) {
				byte[] data = listQrCode.get(listQrCode.size() - i - 1);
				
				Image img = plugin.createImage(data, 70, 70);
				FileOutputStream fos = new FileOutputStream("D:/" + i + ".png");
				fos.write(data);
				fos.close();
			 	cell = new Cell().add(img);
			} else {
				cell = new Cell();
				cell.setHeight(70);
				
				cell.add(new Paragraph("i = " + i)).setFont(times).setFontColor(gray).setFontSize(8);
			}
			cell.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setTextAlignment(TextAlignment.JUSTIFIED)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(Border.NO_BORDER);
			
			table.addCell(cell);
		}
        
		text = "*штрих-код ЭҮП-тен алынған және Өтініш берушінің электрондық-цифрлық қолтаңбасы қойылған деректерді қамтиды\n";
		text = text
				+ "*штрих-код содержит данные, полученные из ПЭП и подписанные электронно-цифровой подписью Заявителя\n";

		cell = new Cell(1, widths.length).add(new Paragraph(text)).setFont(times).setFontColor(black).setFontSize(8)
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setTextAlignment(TextAlignment.JUSTIFIED)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(Border.NO_BORDER);
		
		table.addCell(cell);

        // Вешаем обработчик конца страницы
        plugin.addFooter(pdf, table, 40, 40, 160);

        // Initialize document
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(60, 40, 60, 40);

		document.add(table);

		Paragraph paragraph;

		text = "Отдел города Темиртау по земельному кадастру и недвижимости филиала некоммерческогоакционерного общества «Государственная корпорация «Правительство для граждан» по Карагандинской области";
		paragraph = new Paragraph(text + "\n \n \n").setFont(times).setBold().setFontSize(12).setFontColor(black);
		paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
		paragraph.setTextAlignment(TextAlignment.CENTER);
		paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		text = "ЗАЯВЛЕНИЕ";
		text = text + "\n№ 67e7a85c-5c05-4974-b816-1575d5f859e8";
		text = text + "\nо государственной регистрации объекта кондоминиума";
		paragraph = new Paragraph(text + "\n \n").setFont(times).setBold().setFontSize(12).setFontColor(black);
		paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
		paragraph.setTextAlignment(TextAlignment.CENTER);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);
		
		paragraph = new Paragraph("Заявители:\n").setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		text = "Товарищество с ограниченной ответственностью \"Связист\", БИН 100440000133";
		text = text + "\nАБЕШОВ АБЕШОВ МУРАТУЛЫ, 25.06.1976 г.р., ИИН 760625301668";
		paragraph = new Paragraph(text + "\n").setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		paragraph.setMarginLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);
		
		paragraph = new Paragraph("От имени которых действует:\n").setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		paragraph = new Paragraph("ПРИЙМАК ИВАН ВАСИЛЬЕВИЧ, 1990.03.19 г.р., ИИН 900319350069 на основаниидоверенности № 74ba9d15 от 01.10.2020 г.\n")
				.setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		paragraph.setMarginLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);
		
		
		paragraph = new Paragraph("\nПрошу зарегистрировать объект кондоминиума.\n \nСведения об объекте кондоминиума:\n")
				.setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		text = "Адрес объекта кондоминиума: обл. Карагандинская, г. Темиртау, мкр. 9, д. 29\n";
		text = text + "Вид недвижимости: Земельный участок, Многоквартирный дом\n";
		text = text + "Количество вторичных объектов, находящихся в раздельной собственности: 24\n";
		text = text + "Общая площадь здания (первичный объект): 1326.7\n";
		text = text
				+ "Полезная площадь всех жилых и нежилых помещений, находящихся в раздельнойсобственности (вторичные объекты): 846. кв/м\n";
		text = text + "Общая площадь земельного участка для эксплуатации здания (первичный объект): 0.0396га\n";
		text = text + "Кадастровый номер земельного участка: 09:145:029:019\n";
		text = text
				+ "Краткое описание мест общего пользования с указанием площади: 20 квартир, лестичнаяклетка, подвал, чердак и электрощитовая\n";
		paragraph = new Paragraph(text).setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		paragraph.setMarginLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		paragraph = new Paragraph("К заявлению прилагаю (ем) следующие документы:\n").setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		paragraph.setMarginTop(3);
		document.add(paragraph);

		text = "1. Документ об оплате: вид Чек No 2c370698dfe9 на сумму 2405.0 тенге";
		text = text + "\n2. Документы, на основании которых осуществляется государственная регистрация";
		text = text + "\n (решение местных исполнительных органов по предоставлению земельного участка,";
		text = text + "\n идентификационный документ на земельный участок):";
		paragraph = new Paragraph(text).setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		paragraph.setMarginLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);

		text = "Государственный акт на ЗУ.pdf (No 1 от 19.01.2018 г.)";
		text = text + "\nРешение МИО по предоставлению ЗУ.pdf (No 2.6 от 13.08.2019 г.)\n";
		paragraph = new Paragraph(text + text + text + text + text + text + text + text + text + text).setFont(times).setFontSize(12).setFontColor(black);
		paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
		paragraph.setMarginLeft(35);
		//paragraph.setKeepTogether(true);
		paragraph.setFirstLineIndent(14);
		document.add(paragraph);
		
		//Close document
        document.close();
	}
    
    protected static class PageHeaderEventHandler implements IEventHandler {

    	private IBlockElement headerElement;
    	private float left;
    	private float right;
    	private float top;
    	
    	public PageHeaderEventHandler(IBlockElement headerElement, float left, float right, float top) {
            this.headerElement = headerElement;
            this.left = left;
            this.right = right;
            
            this.top = top;
        }
    	
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();

            Canvas canvas = new Canvas(page, new Rectangle(left, page.getPageSize().getHeight() - top, page.getPageSize().getWidth() - left - right, page.getPageSize().getHeight()));
            canvas.add(headerElement);

            canvas.close();
        }
    }

    protected static class PageFooterEventHandler implements IEventHandler {

    	private IBlockElement footerElement;
    	private float left;
    	private float right;
    	private float bottom;
    	
    	public PageFooterEventHandler(IBlockElement footerElement, float left, float right, float bottom) {
            this.footerElement = footerElement;
            this.left = left;
            this.right = right;
            
            this.bottom = bottom;
        }
    	
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();

            Canvas canvas = new Canvas(page, new Rectangle(left, 0, page.getPageSize().getWidth() - left - right, bottom));
            canvas.add(footerElement);

            canvas.close();
        }
    }
}
