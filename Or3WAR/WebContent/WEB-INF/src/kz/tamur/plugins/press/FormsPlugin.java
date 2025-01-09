package kz.tamur.plugins.press;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom.Element;

import kz.tamur.rt.orlang.AbstractClientPlugin;
import kz.tamur.util.ComUtil;
import kz.tamur.util.Funcs;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.jacob.com.ComThread;

public class FormsPlugin extends AbstractClientPlugin {

    private static DateFormat FMT_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public Element parseJrnForm(byte[] fileContent) throws Exception {
        Element res = new Element("Form");
        File file = Funcs.createTempFile("or3", ".xls");
        File picture = Funcs.createTempFile("or3", "frm");
        Funcs.write(fileContent, file);

        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        try {
            Dispatch wbooks = excel.getProperty("Workbooks").toDispatch();
            Dispatch wbook = Dispatch.call(wbooks, "Open", file.getCanonicalPath(), new Variant(0), new Variant(true)).toDispatch();

           // Variant v1 = new Variant("'"+file.getCanonicalPath()+"'!ЭтаКнига.pictureToFile");
            Variant v1 = new Variant("ЭтаКнига.pictureToFile");
            Variant v2 = new Variant(picture.getCanonicalPath());
            Dispatch.call(excel.getObject(), "Run", v1, v2);

            BufferedImage bi = ImageIO.read(picture);
            if (bi != null) {
            	ImageIO.write(bi, "jpg", picture);
            }

            Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();

            String lastName = ComUtil.getProperty(sheet, "Rows(6).Cells(3).Text").getString();
            String firstName = ComUtil.getProperty(sheet, "Rows(7).Cells(3).Text").getString();
            String middleName = ComUtil.getProperty(sheet, "Rows(8).Cells(3).Text").getString();
            Variant v = ComUtil.getProperty(sheet, "Rows(9).Cells(3).Value");
            v = v.changeType(Variant.VariantDate);
            Date birthDate = v.getJavaDate();

            String docSeries = ComUtil.getProperty(sheet, "Rows(13).Cells(3).Text").getString();
            String docNumber = ComUtil.getProperty(sheet, "Rows(14).Cells(3).Text").getString();
            String docAuth = ComUtil.getProperty(sheet, "Rows(15).Cells(3).Text").getString();
            v = ComUtil.getProperty(sheet, "Rows(16).Cells(3).Value");
            v = v.changeType(Variant.VariantDate);
            Date docDate = v.getJavaDate();

            String addr = ComUtil.getProperty(sheet, "Rows(18).Cells(3).Text").getString();
            String phone = ComUtil.getProperty(sheet, "Rows(19).Cells(3).Text").getString();
            String email = ComUtil.getProperty(sheet, "Rows(20).Cells(3).Text").getString();
            String mobile = ComUtil.getProperty(sheet, "Rows(21).Cells(3).Text").getString();

            sheet = Dispatch.call(wbook, "Worksheets", new Variant(2)).toDispatch();
            String docType = ComUtil.getProperty(sheet, "Rows(4).Cells(2).Text").getString();
            String position = ComUtil.getProperty(sheet, "Rows(3).Cells(2).Text").getString();
            String org = ComUtil.getProperty(sheet, "Rows(2).Cells(2).Text").getString();
            String country = ComUtil.getProperty(sheet, "Rows(5).Cells(2).Text").getString();
            String city = ComUtil.getProperty(sheet, "Rows(6).Cells(2).Text").getString();

            Dispatch.call(wbook, "Close", new Variant(false));

            Element e = new Element("LastName");
            e.setText(lastName);
            res.addContent(e);

            e = new Element("FirstName");
            e.setText(firstName);
            res.addContent(e);

            e = new Element("MiddleName");
            e.setText(middleName);
            res.addContent(e);

            e = new Element("BirthDate");
            e.setText(FMT_DATE.format(birthDate));
            res.addContent(e);

            e = new Element("DocType");
            e.setText(docType);
            res.addContent(e);

            e = new Element("DocSeries");
            e.setText(docSeries);
            res.addContent(e);

            e = new Element("DocNumber");
            e.setText(docNumber);
            res.addContent(e);

            e = new Element("DocAuth");
            e.setText(docAuth);
            res.addContent(e);

            e = new Element("DocDate");
            e.setText(FMT_DATE.format(docDate));
            res.addContent(e);

            e = new Element("Addr");
            e.setText(addr);
            res.addContent(e);

            e = new Element("Phone");
            e.setText(phone);
            res.addContent(e);

            e = new Element("Email");
            e.setText(email);
            res.addContent(e);

            e = new Element("Mobile");
            e.setText(mobile);
            res.addContent(e);

            e = new Element("Organization");
            e.setText(org);
            res.addContent(e);

            e = new Element("Position");
            e.setText(position);
            res.addContent(e);

            e = new Element("Country");
            e.setText(country);
            res.addContent(e);

            e = new Element("City");
            e.setText(city);
            res.addContent(e);

            e = new Element("Picture");
            e.setText(picture.getCanonicalPath());
            res.addContent(e);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            excel.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
        if(picture.exists()) {
        	picture.deleteOnExit();
        }
        file.delete();
        return res;
    }

    public byte[] createJrnList(byte[] template, Element jurs) throws Exception {
        File file = Funcs.createTempFile("or3", ".xls");
        Funcs.write(template, file);

        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        try {
            Dispatch wbooks = excel.getProperty("Workbooks").toDispatch();
            Dispatch wbook = Dispatch.call(wbooks, "Open", file.getCanonicalPath(), new Variant(0), new Variant(false)).toDispatch();

            Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();
            Dispatch.call(sheet, "Unprotect");

            String orgUid = jurs.getAttributeValue("org");
            // Записываем UID СМИ
        	Dispatch row = Dispatch.call(sheet, "Rows", new Variant(1)).toDispatch();
        	Dispatch cell = Dispatch.call(row, "Cells", new Variant(1)).toDispatch();
    		Dispatch.put(cell, "Value", new Variant(orgUid));
            
            List<Element> jurList = jurs.getChildren();
            int i = 0;
            for (Element jur : jurList) {
            	row = Dispatch.call(sheet, "Rows", new Variant(3 + i++)).toDispatch();
            	String s = jur.getChildText("LastName");
            	cell = Dispatch.call(row, "Cells", new Variant(1)).toDispatch();
        		Dispatch.put(cell, "Locked", new Variant(true));
            	if (s.length() > 0) {
            		Dispatch.put(cell, "Value", new Variant(s));
            	}
            	s = jur.getChildText("FirstName");
            	cell = Dispatch.call(row, "Cells", new Variant(2)).toDispatch();
        		Dispatch.put(cell, "Locked", new Variant(true));
            	if (s.length() > 0) {
            		Dispatch.put(cell, "Value", s);
            	}
            	s = jur.getChildText("MiddleName");
            	cell = Dispatch.call(row, "Cells", new Variant(3)).toDispatch();
        		Dispatch.put(cell, "Locked", new Variant(true));
            	if (s.length() > 0) {
            		Dispatch.put(cell, "Value", s);
            	}
            	s = jur.getChildText("Id");
            	if (s.length() > 0) {
                	cell = Dispatch.call(row, "Cells", new Variant(5)).toDispatch();
            		Dispatch.put(cell, "Value", s);
            	}
            }
            Dispatch.call(sheet, "Protect", "", true, true, true);
            Dispatch.call(wbook, "Save");
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            excel.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
        byte[] res = Funcs.read(file);
        file.delete();
        return res;
    }

    public Element parseJrnList(byte[] content) throws Exception {
        File file = Funcs.createTempFile("or3", ".xls");
        Funcs.write(content, file);

        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        Element list = new Element("List");
        try {
            Dispatch wbooks = excel.getProperty("Workbooks").toDispatch();
            Dispatch wbook = Dispatch.call(wbooks, "Open", file.getCanonicalPath(), new Variant(0), new Variant(true)).toDispatch();

            Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();
            // Разбираем UID СМИ
        	Dispatch row = Dispatch.call(sheet, "Rows", 1).toDispatch();
        	Dispatch cell = Dispatch.call(row, "Cells", 1).toDispatch();
        	Variant v = Dispatch.call(cell, "Value");
        	String s = v.isNull() ? null : v.toString();
        	if (s != null && s.length() > 0) {
        		list.setAttribute("org", s);
        	}
            // Разбираем список журналистов
            Element jurs = new Element("Jrns");
            for (int i = 0; i < 30; i++) {
            	Element jur = new Element("Jrn");
            	row = Dispatch.call(sheet, "Rows", 3 + i).toDispatch();
            	cell = Dispatch.call(row, "Cells", 5).toDispatch();
            	v = Dispatch.call(cell, "Value");
            	s = v.isNull() ? null : v.toString();
            	if (s != null && s.length() > 0) {
            		Element e = new Element("Id");
            		e.setText(s);
            		jur.addContent(e);
            	} else {
                	cell = Dispatch.call(row, "Cells", 1).toDispatch();
                	v = Dispatch.call(cell, "Value");
                	s = v.isNull() ? null : v.toString();
                	if (s != null && s.length() > 0) {
                		Element e = new Element("LastName");
                		e.setText(s);
                		jur.addContent(e);
                    	cell = Dispatch.call(row, "Cells", 2).toDispatch();
                    	v = Dispatch.call(cell, "Value");
                    	s = v.isNull() ? null : v.toString();
                    	if (s != null && s.length() > 0) {
                    		e = new Element("FirstName");
                    		e.setText(s);
                    		jur.addContent(e);
                    	}
                    	cell = Dispatch.call(row, "Cells", 3).toDispatch();
                    	v = Dispatch.call(cell, "Value");
                    	s = v.isNull() ? null : v.toString();
                    	if (s != null && s.length() > 0) {
                    		e = new Element("MiddleName");
                    		e.setText(s);
                    		jur.addContent(e);
                    	}
                	} else {
                		break;
                	}
            	}
            	cell = Dispatch.call(row, "Cells", 4).toDispatch();
            	v = Dispatch.call(cell, "Value");
            	s = v.isNull() ? null : v.toString();
            	if (s != null && s.length() > 0) {
            		Element e = new Element("Decision");
            		e.setText(s);
            		jur.addContent(e);
            	}
            	jurs.addContent(jur);
            }
            list.addContent(jurs);
            
            // Разбираем список проносимого оборудования
            Element equipments = new Element("Equipments");
            sheet = Dispatch.call(wbook, "Worksheets", new Variant(2)).toDispatch();
            for (int i = 0; i < 30; i++) {
            	Element equipment = new Element("Equipment");
            	row = Dispatch.call(sheet, "Rows", 3 + i).toDispatch();
            	cell = Dispatch.call(row, "Cells", 1).toDispatch();
            	v = Dispatch.call(cell, "Value");
            	s = v.isNull() ? null : v.toString();
            	if (s != null && s.length() > 0) {
            		Element e = new Element("Name");
            		e.setText(s);
            		equipment.addContent(e);
                	cell = Dispatch.call(row, "Cells", 2).toDispatch();
                	v = Dispatch.call(cell, "Value");
                	s = v.isNull() ? null : v.toString();
                	if (s != null && s.length() > 0) {
                		e = new Element("SerialNumber");
                		e.setText(s);
                		equipment.addContent(e);
                	}
            	} else {
            		break;
            	}
            	equipments.addContent(equipment);
            }
            list.addContent(equipments);

            Dispatch.call(wbook, "Close", new Variant(false));
            file.delete();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            excel.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
        return list;
    }

    public static void main(String args[]) throws Exception {
    	File f = new File("C:\\Temp\\Список.xls");
    	byte[] content = Funcs.read(f);
    	FormsPlugin plg = new FormsPlugin();
    	Element jurs = new Element("Jurs");
    	Element jur = new Element("Jur");
    	Element e = new Element("FirstName");
    	e.setText("Берик");
    	jur.addContent(e);
    	e = new Element("LastName");
    	e.setText("Берентаев");
    	jur.addContent(e);
    	e = new Element("MiddleName");
    	e.setText("Муратович");
    	jur.addContent(e);
    	e = new Element("Id");
    	e.setText("123.456");
    	jur.addContent(e);
    	jurs.addContent(jur);
    	plg.createJrnList(content, jurs);
    }
}
