package com.cifs.or2.server.plugins;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.tamur.comps.Constants;
import kz.tamur.lang.BarcodeOp;
import kz.tamur.or3.server.lang.SystemOp;
//import kz.tamur.rn3.qrcode.QRCodeElement;
import kz.tamur.util.Funcs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class PDFPlugin implements SrvPlugin {

	private static final Map<String, BaseFont> _fontMap = new HashMap<>();
	
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

	public com.itextpdf.text.Document createPdf() throws Exception {
		return createPdf(PageSize.A4);
	}

	public com.itextpdf.text.Document createPdf(Rectangle size) throws Exception {
    	com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(size, 0, 0, 0, 0); 
		return pdfDoc;
	}

	public PdfReader createReader(byte[] src) throws Exception {
		PdfReader reader = new PdfReader(src);
		return reader;
	}

	public PdfReader createReader(File src) throws Exception {
		PdfReader reader = new PdfReader(new FileInputStream(src));
		return reader;
	}

	public PdfWriter createWriter(com.itextpdf.text.Document pdfDoc, OutputStream os) throws Exception {
    	PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
		return writer;
	}

	public com.itextpdf.text.Document createPdf(OutputStream os) throws Exception {
    	com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4, 0, 0, 0, 0); 
    	com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
    	pdfDoc.open();
		return pdfDoc;
	}
	
	public Object[] createPdfAndWriter(OutputStream os) throws Exception {
    	com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4, 0, 0, 0, 0); 
    	PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
    	pdfDoc.open();
		return new Object[] {pdfDoc, writer};
	}

	public void savePdf(com.itextpdf.text.Document pdfDoc) {
    	pdfDoc.close();
	}
	
	public BaseFont createFont(com.itextpdf.text.Document pdfDoc, String fontName) throws Exception {
		BaseFont font = _fontMap.get(fontName);
		if (font == null) {
			InputStream fis = PDFPlugin.class.getResourceAsStream("/" + fontName);
			File fontFile = Funcs.createTempFile("font", ".ttf", Constants.DOCS_DIRECTORY);
	        FileOutputStream out = new FileOutputStream(fontFile);
	
	        Funcs.writeStream(fis, out, Constants.MAX_IMAGE_SIZE);
	        fis.close();
	        out.flush();
	        out.close();
	        
	        font = BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	        _fontMap.put(fontName, font);
	        
	        fontFile.delete();
		}
        return font;
	}

	public Font createFont(String fontName) throws Exception {
		InputStream fis = PDFPlugin.class.getResourceAsStream("/" + fontName);
		Font font = Font.createFont(Font.TRUETYPE_FONT, fis);
        return font;
	}

	public BaseColor createColor(int red, int green, int blue) {
		return new BaseColor(red, green, blue);
	}
	
	public void newPage(com.itextpdf.text.Document pdfDoc, Number left, Number right, Number top, Number bottom) {
		newPage(pdfDoc, PageSize.A4, left.floatValue(), right.floatValue(), top.floatValue(), bottom.floatValue());
	}

	public void newPage(com.itextpdf.text.Document pdfDoc, Rectangle size, Number left, Number right, Number top, Number bottom) {
		pdfDoc.setPageSize(size);
		pdfDoc.setMargins(left.floatValue(), right.floatValue(), top.floatValue(), bottom.floatValue());
    	pdfDoc.newPage();
	}

	public com.itextpdf.text.pdf.PdfPTable createTable(int maxColumnCount, List widths, Number left, Number right) throws Exception {
		return createTable(maxColumnCount, widths, left, right, 100);
	}
	
	public com.itextpdf.text.pdf.PdfPTable createTable(int maxColumnCount, List widths, Number left, Number right, Number widthPercent) throws Exception {
		com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(maxColumnCount);
		float p = widthPercent.floatValue() / 100;
        table.setTotalWidth(p * (PageSize.A4.getWidth() - left.floatValue() - right.floatValue()));
        table.setLockedWidth(true);
        
        float[] ws = new float[widths.size()];
        for (int i=0; i<widths.size(); i++)
        	ws[i] = p * ((Number)widths.get(i)).floatValue() * PageSize.A4.getWidth() / 100;
        table.setWidths(ws);
    	return table;
	}

	public com.itextpdf.text.pdf.PdfPTable createTable(com.itextpdf.text.Document pdfDoc, int maxColumnCount, List widths, Number left, Number right) throws Exception {
		return createTable(pdfDoc, maxColumnCount, widths, left, right, 100);
	}
	
	public com.itextpdf.text.pdf.PdfPTable createTable(com.itextpdf.text.Document pdfDoc, int maxColumnCount, List widths, Number left, Number right, Number widthPercent) throws Exception {
		com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(maxColumnCount);
		float p = widthPercent.floatValue() / 100;
        table.setTotalWidth(p * (pdfDoc.getPageSize().getWidth() - left.floatValue() - right.floatValue()));
        table.setLockedWidth(true);
        
        float[] ws = new float[widths.size()];
        for (int i=0; i<widths.size(); i++)
        	ws[i] = p * ((Number)widths.get(i)).floatValue() * pdfDoc.getPageSize().getWidth() / 100;
        table.setWidths(ws);
    	return table;
	}

	public com.itextpdf.text.pdf.PdfPCell createCell() throws Exception {
        com.itextpdf.text.pdf.PdfPCell pdfCell = new com.itextpdf.text.pdf.PdfPCell();
    	return pdfCell;
	}

	public com.itextpdf.text.pdf.PdfPCell createCell(PdfPTable t) throws Exception {
        com.itextpdf.text.pdf.PdfPCell pdfCell = new com.itextpdf.text.pdf.PdfPCell(t);
    	return pdfCell;
	}

	public com.itextpdf.text.pdf.PdfPCell createCell(Image img, boolean fit) throws Exception {
        com.itextpdf.text.pdf.PdfPCell pdfCell = new com.itextpdf.text.pdf.PdfPCell(img, fit);
    	return pdfCell;
	}

	public void setText(com.itextpdf.text.pdf.PdfPCell cell, String text, BaseFont bf, Number height, int style) {
		setText(cell, text, bf, height.intValue(), style, null);
	}
	
	public void setText(com.itextpdf.text.pdf.PdfPCell cell, String text, BaseFont bf, Number height, int style, BaseColor color) {
		com.itextpdf.text.Font pf = new com.itextpdf.text.Font(bf, height.intValue(), style, color);
		cell.setPhrase(new com.itextpdf.text.Phrase(text, pf));
	}

	public com.itextpdf.text.Chunk createChunk(String text, BaseFont bf, int height, int style, BaseColor color) {
		com.itextpdf.text.Font pf = new com.itextpdf.text.Font(bf, height, style, color);
		return (new com.itextpdf.text.Chunk(text, pf));
	}

	public Image createImage(JFreeChart bar, int width, int height) throws Exception {
		Image img = Image.getInstance(bar.createBufferedImage(width, height), Color.WHITE);
    	return img;
	}

	public Image createImage(String imageName, int width, int height) throws Exception {
    	URL url = PDFPlugin.class.getResource("/" + imageName);
    	Image img = Image.getInstance(url);
    	if (width > 0 && height > 0)
    		img.scaleAbsolute(width, height);
    	else if (width > 0)
    		img.scaleToFit(width, img.getHeight()*width/img.getWidth());
    	else if (height > 0)
    		img.scaleToFit(img.getWidth()*height/img.getHeight(), height);
    	return img;
	}

	public Image createImage(byte[] b, int width, int height) throws Exception {
    	Image img = Image.getInstance(b);
    	if (width > 0 && height > 0)
    		img.scaleAbsolute(width, height);
    	else if (width > 0)
    		img.scaleToFit(width, img.getHeight()*width/img.getWidth());
    	else if (height > 0)
    		img.scaleToFit(img.getWidth()*height/img.getHeight(), height);
    	return img;
	}

	public JFreeChart getBarChart(List rowKeys, List colKeys, Map<Comparable, Map> values, 
							String title, String xTitle, String yTitle, PlotOrientation orientation,
							RectangleEdge legendPos, Number tickUnit, Font axisFont, Font labelFont, Font legendFont, Font titleFont) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i=0; i<rowKeys.size(); i++) {
        	Comparable rowKey = (Comparable)rowKeys.get(i);
        	Map vals = values.get(rowKey);
            for (int j=0; j<colKeys.size(); j++) {
            	Comparable colKey = (Comparable)colKeys.get(j);
                dataset.setValue((Number)vals.get(colKey), rowKey, colKey);
            }
        }
        if (orientation == null) orientation = PlotOrientation.VERTICAL;
        JFreeChart bar = ChartFactory.createBarChart(title, xTitle, yTitle, dataset, orientation, true, false, false);
        
        if (legendFont != null)
        	bar.getLegend().setItemFont(legendFont);
		if (legendPos != null)
			bar.getLegend().setPosition(legendPos);
        
        CategoryPlot plot = (CategoryPlot)bar.getPlot();
        if (tickUnit != null && tickUnit.doubleValue() > 0) {
			NumberAxis xAxis = (NumberAxis)plot.getRangeAxis();
			xAxis.setTickLabelFont(axisFont);
        }
        if (axisFont != null) {
			plot.getRangeAxis().setTickLabelFont(axisFont);
			plot.getDomainAxis().setTickLabelFont(axisFont);
        }
        if (labelFont != null) {
			plot.getRangeAxis().setLabelFont(labelFont);
			plot.getDomainAxis().setLabelFont(labelFont);
        }
        if (titleFont != null) {
        	bar.getTitle().setFont(titleFont);
        }
		return bar;
    }
	
    public void addHtmlToPdf(Document document, PdfWriter writer, String htmlText) throws Exception {
        XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
        fontImp.register("/FreeSans.ttf");
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(htmlText.getBytes(Charset.forName("UTF-8"))), null, Charset.forName("UTF-8"), fontImp);
    }
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PDFPlugin p = new PDFPlugin();
		
/*		OutputStream os = p.createByteArrayOutputStream();
		FileInputStream fis = new FileInputStream("F:\\erik\\PROTOCOL.pdf");
		byte[] src = new byte[fis.available()];
		fis.read(src);
		fis.close();
				
		com.itextpdf.text.Document pdfDoc = p.createPdf();
		PdfReader reader = p.createReader(src);
		PdfWriter writer = p.createWriter(pdfDoc, os);
    	pdfDoc.open();
        PdfContentByte canvas = writer.getDirectContent();
		
		int n = reader.getNumberOfPages();

        for (int i = 0; i < n;) {
        	pdfDoc.newPage();
        	PdfImportedPage page = writer.getImportedPage(reader, ++i);
            canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);

            Image qrCode = p.createImage(new SystemOp(null).readFile("F:\\vLBM9Q4523g.jpg"), 100, 100);
            qrCode.setAbsolutePosition((PageSize.A4.getWidth() - qrCode.getScaledWidth()), 0);
            
            pdfDoc.add(qrCode);
        }
		
    	p.savePdf(pdfDoc);
    	os.close();
    	
    	byte[] b = ((ByteArrayOutputStream)os).toByteArray();
    	FileOutputStream fos = new FileOutputStream("C:/tmppdf.pdf");
    	fos.write(b);
    	fos.close();
*/
    	//System.exit(1);
    	
		OutputStream os = p.createByteArrayOutputStream();
		com.itextpdf.text.Document pdfDoc = p.createPdf();
		PdfWriter writer = p.createWriter(pdfDoc, os);
    	pdfDoc.open();
    	PdfContentByte canvas = writer.getDirectContent();
		
        File[] pdfFiles = new File[] {
        		new File("D:\\tmp\\RN\\1.pdf"),
        		new File("D:\\tmp\\RN\\2.pdf"),
        		new File("D:\\tmp\\RN\\3.pdf"),
        		new File("D:\\tmp\\RN\\2022-08-05\\дубликат-4.pdf")
        };
        
        for (File pdf : pdfFiles) {
        	PdfReader reader = p.createReader(pdf);
    		
    		int n = reader.getNumberOfPages();

            for (int i = 1; i <= n; i++) {
            	PdfImportedPage page = writer.getImportedPage(reader, i);
            	Rectangle pageSize = reader.getPageSizeWithRotation(i);
            	
            	pdfDoc.setPageSize(pageSize);
            	pdfDoc.newPage();

            	if (pageSize.getRotation() == 0) {
            		canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);
            	} else if (pageSize.getRotation() == 90) {
            		canvas.addTemplate(page, 0, -1f, 1f, 0, 0, pageSize.getHeight());
            	} else if (pageSize.getRotation() == 180) {
            		canvas.addTemplate(page, -1f, 0, 0, -1f, pageSize.getWidth(), pageSize.getHeight());
            	} else if (pageSize.getRotation() == 270) {
            		canvas.addTemplate(page, 0, 1f, -1f, 0, pageSize.getWidth(), 0);
            	}
            }
        }
		
    	p.savePdf(pdfDoc);
    	os.close();
    	
    	byte[] b = ((ByteArrayOutputStream)os).toByteArray();
    	FileOutputStream fos = new FileOutputStream("d:/tmp/RN/2022-08-05/res.pdf");
    	fos.write(b);
    	fos.close();

    	System.exit(1);
    	
        Image backgnd = p.createImage(new SystemOp(null).readFile("F:\\vLBM9Q4523g.jpg"), 555, 790);
        backgnd.setAbsolutePosition(20, 20);

		//com.itextpdf.text.Document pdfDoc = p.createPdf(os, img);
    	//com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4);//
    	//pdfDoc.setMargins(0, 0, 0, 0); 
    	//PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
    	//pdfDoc.open();

		float left = 30;
		float right = 20;
		float top = 25; float bootom = 20;
		
		BaseFont times = p.createFont(pdfDoc, "times.ttf");
		p.newPage(pdfDoc, left, right, top, bootom);
		
		List widths = new ArrayList();
		
		widths.add(100);
		
		com.itextpdf.text.pdf.PdfPTable table = p.createTable(pdfDoc, 1, widths, left, right, 80);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		BaseColor red = p.createColor(51, 102, 255);
		BaseColor darkBlue = p.createColor(51, 51, 153);
		BaseColor darkRed = p.createColor(153, 51, 102);
		
		com.itextpdf.text.pdf.PdfPCell c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(180f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_BOTTOM);
    	p.setText(c, "ҚАЗАҚСТАН РЕСПУБЛИКАСЫ", times, 12, com.itextpdf.text.Font.BOLD, darkBlue);
        table.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(30f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "БАЙЛАНЫС ЖӘНЕ АҚПАРАТ АГЕНТТІГІ", times, 12, com.itextpdf.text.Font.BOLD, darkBlue);
        table.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(15f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	p.setText(c, "АҚПАРАТТЫҚ РЕСУРСТАР МЕН АҚПАРАТТЫҚ ЖҮЙЕЛЕРДІ ТІРКЕУ ТУРАЛЫ", times, 9, com.itextpdf.text.Font.BOLD);
        table.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(40f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "К У Ә Л І К", times, 24, com.itextpdf.text.Font.BOLD, darkRed);
        table.addCell(c);

        pdfDoc.add(table);

		widths = new ArrayList();
		
		widths.add(30);
		widths.add(40);
		widths.add(30);
		
		com.itextpdf.text.pdf.PdfPTable table2 = p.createTable(pdfDoc, 3, widths, left, right, 80);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(40f);
    	c.setHorizontalAlignment(Element.ALIGN_LEFT);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "Астана қаласы", times, 12, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(40f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "№ АВ-4324235234", times, 12, com.itextpdf.text.Font.NORMAL);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setFixedHeight(40f);
    	c.setHorizontalAlignment(Element.ALIGN_RIGHT);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "21 ноября 2007 года", times, 12, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setIndent(20f);
    	//c.setFixedHeight(40f);
    	c.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
    	p.setText(c, "Осы куәлік “Ақпараттандыру туралы” Қазақстан Республикасының Заңы және Қазақстан Республикасы Үкіметінің 2007 жылғы 21 қарашадағы № 1124 қаулысымен бекітілген Электрондық ақпараттық ресурстар мен ақпараттық жүйелердің мемлекеттік тіркелімін және депозитарийді жүргізу ережелеріне сәйкес берілді", times, 11, com.itextpdf.text.Font.NORMAL);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setPaddingTop(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_BOTTOM);
    	p.setText(c, "ref=Свидетельство.объект учета ИТР.1own владелец.участник системы.наименование полное", times, 12, com.itextpdf.text.Font.BOLD);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(1);
    	c.setColspan(3);
    	c.setPaddingTop(-1f);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "(ақпараттық ресурстар мен ақпараттық жүйелер иесінің атауы)", times, 9, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_BOTTOM);
    	p.setText(c, "ref=Свидетельство.объект учета ИТР.1own владелец.участник системы.адрес почтовый", times, 12, com.itextpdf.text.Font.BOLD);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(1);
    	c.setColspan(3);
    	c.setPaddingTop(-1f);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "(ақпараттық ресурстар мен ақпараттық жүйелер иесінің мекен-жайы)", times, 9, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setPaddingBottom(5f);
    	c.setHorizontalAlignment(Element.ALIGN_LEFT);
    	p.setText(c, "ақпараттық ресурстар мен ақпараттық жүйелердің Мемлекеттік тіркеліміне", times, 12, com.itextpdf.text.Font.NORMAL);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_BOTTOM);
    	p.setText(c, "ref=Свидетельство.объект учета ИТР.1nam v наименование", times, 12, com.itextpdf.text.Font.BOLD);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(1);
    	c.setColspan(3);
    	c.setPaddingTop(-1f);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "(ақпараттық ресурстар мен ақпараттық жүйелердің атауы)", times, 9, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);

        c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_BOTTOM);
    	p.setText(c, "ref=Свидетельство.объект учета ИТР<База данных>.2dbt v тип бд.мультинаименование", times, 12, com.itextpdf.text.Font.BOLD);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(1);
    	c.setColspan(3);
    	c.setPaddingTop(-1f);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "(дерек қорының түрінің атауы)", times, 9, com.itextpdf.text.Font.ITALIC);
        table2.addCell(c);
        
        c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	Chunk ph1 = p.createChunk("ref=", times, 12, com.itextpdf.text.Font.BOLD, null);
    	Chunk ph2 = p.createChunk(" № ", times, 12, com.itextpdf.text.Font.NORMAL, null);
    	Chunk ph3 = p.createChunk("ref=Свидетельство.объект учета ИТР.8ren регистр номер", times, 12, com.itextpdf.text.Font.BOLD, null);
    	
    	Phrase ph = Phrase.getInstance("");
    	ph.add(ph1);
    	ph.add(ph2);
    	ph.add(ph3);
    	c.setPhrase(ph);
        table2.addCell(c);

		c = p.createCell();
    	c.setBorder(0);
    	c.setColspan(3);
    	c.setPaddingTop(-1f);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_CENTER);
    	p.setText(c, "нөмірімен тіркелді", times, 12, com.itextpdf.text.Font.NORMAL);
        table2.addCell(c);

        pdfDoc.add(table2);

		widths = new ArrayList();
		
		widths.add(50);
		widths.add(50);
		
		com.itextpdf.text.pdf.PdfPTable table3 = p.createTable(pdfDoc, 2, widths, left, right, 70);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

        c = p.createCell();
    	c.setBorder(0);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_LEFT);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "ref=", times, 12, com.itextpdf.text.Font.BOLD);
        table3.addCell(c);

        c = p.createCell();
    	c.setBorder(0);
    	c.setPaddingBottom(10f);
    	c.setHorizontalAlignment(Element.ALIGN_LEFT);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "ref=", times, 12, com.itextpdf.text.Font.BOLD);
        table3.addCell(c);

        pdfDoc.add(table3);
		
		widths = new ArrayList();
		
		widths.add(100);
		
		com.itextpdf.text.pdf.PdfPTable table4 = p.createTable(pdfDoc, 1, widths, left, right, 80);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

        c = p.createCell();
    	c.setBorder(0);
    	c.setHorizontalAlignment(Element.ALIGN_RIGHT);
    	c.setVerticalAlignment(Element.ALIGN_TOP);
    	p.setText(c, "ref=Свидетельство.подпись.qrcode", times, 10, com.itextpdf.text.Font.NORMAL);
        table4.addCell(c);

        pdfDoc.add(table4);

        List rs = new ArrayList();
		rs.add("План");
		rs.add("Кор");
		rs.add("Факт");
		
		List cs = new ArrayList();
		cs.add("Квартал 1");
		cs.add("Квартал 2");
		cs.add("Квартал 3");
		cs.add("Квартал 4");
		
		Map values = new HashMap<Comparable, Map>();
		Map vals = new HashMap();
		vals.put("Квартал 1", 1);
		vals.put("Квартал 2", 2);
		vals.put("Квартал 3", 3);
		vals.put("Квартал 4", 4);
		values.put("План", vals);
		vals = new HashMap();
		vals.put("Квартал 1", 3);
		vals.put("Квартал 2", 4);
		vals.put("Квартал 3", 3);
		vals.put("Квартал 4", 3);
		values.put("Кор", vals);
		vals = new HashMap();
		vals.put("Квартал 1", 2);
		vals.put("Квартал 2", 2);
		vals.put("Квартал 3", 2);
		vals.put("Квартал 4", 2);
		values.put("Факт", vals);
		
		Font f1 = p.createFont("arial.ttf").deriveFont(10f);
		Font f2 = f1.deriveFont(Font.ITALIC);
		Font f3 = f1.deriveFont(30f);
		Font f4 = f2.deriveFont(40f);

    	widths = new ArrayList();
		widths.add(100);
/*		com.itextpdf.text.pdf.PdfPTable table2 = p.createTable(pdfDoc, 1, widths, left, right, 50);

        JFreeChart bar = p.getBarChart(rs, cs, values, "График 12", "ось х", "Ось У", PlotOrientation.VERTICAL, RectangleEdge.TOP, 1,
        		f1, f2, f3, f4);
        
        Image image = p.createImage(bar, 200, 200);
    	c = p.createCell(image, false);
		//c.setPaddingLeft(100);
        table2.addCell(c);

        c = p.createCell(table2);
        c.setRowspan(2);
        c.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(c);

        c = p.createCell();
        table.addCell(c);

//        pdfDoc.add(table);

    	URL url = PDFPlugin.class.getResource("/" + "www.png");
    	image = Image.getInstance(url);
    	pdfDoc.add(image);
*/
    	if (backgnd != null) {
    		//PdfContentByte canvas = writer.getDirectContentUnder();
    		//canvas.addImage(backgnd);
    	}

    	p.savePdf(pdfDoc);
    	os.close();
    	
    	//byte[] b = ((ByteArrayOutputStream)os).toByteArray();
    	//FileOutputStream fos = new FileOutputStream("C:/tmppdf.pdf");
    	//fos.write(b);
    	//fos.close();
	}
	
	public static void main3(String[] args) throws Exception {
		PDFPlugin PDFPlugin = new PDFPlugin();

		OutputStream outputStream = PDFPlugin.createByteArrayOutputStream();
		com.itextpdf.text.Document PDFDocument = PDFPlugin.createPdf(outputStream);
		PdfWriter writer = PDFPlugin.createWriter(PDFDocument, outputStream);
		

		BaseFont font_times = PDFPlugin.createFont(PDFDocument, "times.ttf");
		BaseColor font_color = PDFPlugin.createColor(0, 0, 0);

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

		ArrayList<byte[]> listQrCode = new ArrayList<byte[]>();

		int i = 0;
		int max = 7;
		int porc = 700;

		BarcodeOp BARCODE = new BarcodeOp();

		List<String> text_porcs = BARCODE.split(text, porc, "-");
		int text_porcs_size = text_porcs.size();

		//kz.tamur.rn3.qrcode.ObjectFactory factory = new kz.tamur.rn3.qrcode.ObjectFactory();

		/*
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
		i = 0;
		ArrayList<Object> widths = new ArrayList<Object>();

		while (i < max) {
			widths.add(100 / max);
			i = i + 1;
		}

		PdfPCell cell;
		
		ArrayList widths2 = new ArrayList();
		widths2.add(100);
		
		PdfPTable table2 = new PdfPTable(1);
        table2.setTotalWidth(523);
        PdfPCell cell2 = new PdfPCell(new Phrase("This is a test document"));
        cell2.setBackgroundColor(BaseColor.ORANGE);
        table2.addCell(cell2);
        cell2 = new PdfPCell(new Phrase("This is a copyright notice"));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table2.addCell(cell2);
        
		

		PDFPlugin.addTableToFooter(PDFDocument, outputStream, writer, table2, 0, -1, 40, 135);
		PDFDocument.open();

		PDFPlugin.newPage(PDFDocument, com.itextpdf.text.PageSize.A4, 60, 40, 60, 40);

		 
		PdfPTable table = PDFPlugin.createTable(PDFDocument, widths2.size(), widths2, 40, 40);
		table.setKeepTogether(false);
		table.setSplitRows(true);

		text = "Осы құжат «Электрондық құжат жəне электрондық цифрлық қолтаңба туралы» 2003 жылғы 7 қаңтардағы N 370-II ҚРЗ 1 бабына сəйкес қағаз жеткiзгiштегi құжатпен бiрдей.\n";
		text = text
				+ "Данный документ согласно пункту 1 статьи 370-II ЗРК от 7 января 2003 года «Об электронном документе и электронной цифровой подписи» равнозначен документу на бумажном носителе.\n";

		cell = PDFPlugin.createCell();
		PDFPlugin.setText(cell, text, font_times, 8, 0, font_color);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		cell.setBorder(0);
		//cell.setColspan(widths.size());
		table.addCell(cell);
		PDFDocument.add(table);
		
		table = PDFPlugin.createTable(PDFDocument, widths.size(), widths, 40, 40);
		table.setKeepTogether(false);
		table.setSplitRows(true);

		i = 0;
		Image img;
		while (i < max) {
			i = i + 1;

			if (i <= 2) {//listQrCode.size()) {
				//img = Image.getInstance(listQrCode.get(listQrCode.size() - 1));
				img = PDFPlugin.createImage(listQrCode.get(listQrCode.size() - 4), 70, 0);
				FileOutputStream fos = new FileOutputStream("D:/" + i + ".png");
				fos.write(img.getOriginalData());
				fos.close();
			 	cell = PDFPlugin.createCell(img, false);
				//cell = PDFPlugin.createCell();
				//cell.setImage(img);
				//PDFPlugin.setText(cell, "i = " + i, font_times, 8, 0, font_color);
			} else {
				img = null;
				cell = PDFPlugin.createCell();
				cell.setFixedHeight(70);
				PDFPlugin.setText(cell, "i = " + i, font_times, 8, 0, font_color);
				
				cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
				cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			}
			cell.setBorder(0);
			// FileOutputStream fos = new FileOutputStream("D:/" + i + ".png");
			// fos.write(img.getOriginalData());
			// fos.close();

			table.addCell(cell);
		}
		PDFDocument.add(table);
		
		table = PDFPlugin.createTable(PDFDocument, widths2.size(), widths2, 40, 40);
		table.setKeepTogether(false);
		table.setSplitRows(true);
		
		text = "*штрих-код ЭҮП-тен алынған және Өтініш берушінің электрондық-цифрлық қолтаңбасы қойылған деректерді қамтиды\n";
		text = text
				+ "*штрих-код содержит данные, полученные из ПЭП и подписанные электронно-цифровой подписью Заявителя\n";

		cell = PDFPlugin.createCell();
		PDFPlugin.setText(cell, text, font_times, 8, 0, font_color);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		cell.setBorder(0);
		//cell.setColspan(widths.size());
		table.addCell(cell);
		PDFDocument.add(table);
		//PDFPlugin.addTableToFooter(PDFDocument, outputStream, table, 0, -1, 40, 135);
		
		

		//PDFDocument.add(table);

		com.itextpdf.text.Font fontHead = new com.itextpdf.text.Font(font_times, 12, 1, font_color);
		com.itextpdf.text.Font font = new com.itextpdf.text.Font(font_times, 12, 0, font_color);

		Paragraph paragraph;

		text = "Отдел города Темиртау по земельному кадастру и недвижимости филиала некоммерческогоакционерного общества «Государственная корпорация «Правительство для граждан» по Карагандинской области";
		paragraph = new Paragraph(text + "\n \n \n", fontHead);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);
		//PDFPlugin.newPage(PDFDocument, com.itextpdf.text.PageSize.A4, 60, 40, 60, 40);


		text = "ЗАЯВЛЕНИЕ";
		text = text + "\n№ 67e7a85c-5c05-4974-b816-1575d5f859e8";
		text = text + "\nо государственной регистрации объекта кондоминиума";
		paragraph = new Paragraph(text + "\n \n", fontHead);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);
		//PDFPlugin.newPage(PDFDocument, com.itextpdf.text.PageSize.A4, 60, 40, 60, 40);

		paragraph = new Paragraph("Заявители:\n", font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		text = "Товарищество с ограниченной ответственностью \"Связист\", БИН 100440000133";
		text = text + "\nАБЕШОВ АБЕШОВ МУРАТУЛЫ, 25.06.1976 г.р., ИИН 760625301668";
		paragraph = new Paragraph(text + "\n", font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		paragraph.setIndentationLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		
		paragraph = new Paragraph("От имени которых действует:\n", font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		paragraph = new Paragraph(
				"ПРИЙМАК ИВАН ВАСИЛЬЕВИЧ, 1990.03.19 г.р., ИИН 900319350069 на основаниидоверенности № 74ba9d15 от 01.10.2020 г.\n",
				font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		paragraph.setIndentationLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		paragraph = new Paragraph(
				"\nПрошу зарегистрировать объект кондоминиума.\n \nСведения об объекте кондоминиума:\n", font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

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
		paragraph = new Paragraph(text, font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		paragraph.setIndentationLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		paragraph = new Paragraph("К заявлению прилагаю (ем) следующие документы:\n", font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		paragraph.setSpacingBefore(3);
		PDFDocument.add(paragraph);

		text = "1. Документ об оплате: вид Чек No 2c370698dfe9 на сумму 2405.0 тенге";
		text = text + "\n2. Документы, на основании которых осуществляется государственная регистрация";
		text = text + "\n (решение местных исполнительных органов по предоставлению земельного участка,";
		text = text + "\n идентификационный документ на земельный участок):";
		paragraph = new Paragraph(text, font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		paragraph.setIndentationLeft(25);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);

		text = "Государственный акт на ЗУ.pdf (No 1 от 19.01.2018 г.)";
		text = text + "\nРешение МИО по предоставлению ЗУ.pdf (No 2.6 от 13.08.2019 г.)\n";
		paragraph = new Paragraph(text + text + text + text + text + text + text + text + text + text, font);
		paragraph.setAlignment(com.itextpdf.text.Element.ALIGN_JUSTIFIED);
		paragraph.setIndentationLeft(35);
		//paragraph.setKeepTogether(true);
		paragraph.setLeading(14);
		PDFDocument.add(paragraph);
		// PDFDocument.add(table);
		PDFPlugin.savePdf(PDFDocument);

		outputStream.close();

		byte[] b = ((ByteArrayOutputStream) outputStream).toByteArray();
		FileOutputStream fos = new FileOutputStream("D:/pdf_bad.pdf");
		fos.write(b);
		fos.close();

	}

	public void addTableToFooter(com.itextpdf.text.Document pdfDoc, OutputStream os, PdfPTable table) throws Exception {
		FooterTable event = new FooterTable(table);
		PdfWriter writer = PdfWriter.getInstance(pdfDoc, os);
		writer.setPageEvent(event);
	}
	
	public void addTableToFooter(com.itextpdf.text.Document pdfDoc, OutputStream os, PdfPTable table, int rowStart, int rowEnd, float xPos, float yPos) throws Exception {
		FooterTable event = new FooterTable(table, rowStart, rowEnd, xPos, yPos);
		PdfWriter writer = PdfWriter.getInstance(pdfDoc, os);
		writer.setPageEvent(event);
	}

	public void addTableToFooter(com.itextpdf.text.Document pdfDoc, OutputStream os, PdfWriter writer, PdfPTable table, int rowStart, int rowEnd, float xPos, float yPos) throws Exception {
		FooterTable event = new FooterTable(table, rowStart, rowEnd, xPos, yPos);
		writer.setPageEvent(event);
	}

	public class FooterTable extends PdfPageEventHelper {
		protected PdfPTable footer;
		protected int rowStart = 0;
		protected int rowEnd = -1;
		protected float xPos = 36;
		protected float yPos = 64;

		com.itextpdf.text.Font ffont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
		
		public FooterTable(PdfPTable footer) {
			this.footer = footer;
		}

		public FooterTable(PdfPTable footer, int rowStart, int rowEnd, float xPos, float yPos) {
			this.footer = footer;
			this.rowStart = rowStart;
			this.rowEnd = rowEnd;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			// TODO Auto-generated method stub
			super.onStartPage(writer, document);
			PdfContentByte cb = writer.getDirectContent();
			
			//if (document.getPageNumber() == 2) { 
				
				PdfPTable table2 = new PdfPTable(1);
		        table2.setTotalWidth(523);
		        PdfPCell cell2 = new PdfPCell(new Phrase("This is a test document"));
		        cell2.setBackgroundColor(BaseColor.ORANGE);
		        table2.addCell(cell2);
		        cell2 = new PdfPCell(new Phrase("This is a copyright notice"));
		        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
		        table2.addCell(cell2);
		        
//		        cb.beginMarkedContentSequence(PdfName.ARTIFACT);
				table2.writeSelectedRows(rowStart, rowEnd, xPos, yPos, cb);
//				cb.endMarkedContentSequence();
			//}
				
			      // BOTTOM LEFT
			      ColumnText.showTextAligned(writer.getDirectContent(),
			               Element.ALIGN_CENTER, new Phrase("BOTTOM LEFT"),
			               xPos+15, yPos, 0);
			 
			      // BOTTOM MEDIUM
			      ColumnText.showTextAligned(writer.getDirectContent(),
			               Element.ALIGN_CENTER, new Phrase("BOTTOM MEDIUM"),
			               xPos+315, yPos, 0);
			 
			      // BOTTOM RIGHT
			      ColumnText.showTextAligned(writer.getDirectContent(),
			               Element.ALIGN_CENTER, new Phrase("BOTTOM RIGHT"),
			               xPos+515, yPos, 0);
		}
	}
}
