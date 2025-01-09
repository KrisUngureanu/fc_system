package kz.tamur.gbdul.fl;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import kz.tamur.gbdul.fl.message.RequestIin;
import kz.tamur.gbdul.fl.message.Response;
import kz.tamur.gbdul.fl.person.Person;

public class Helper {
	public static String marshallRequest(RequestIin r) {
		try {
	    	JAXBElement<RequestIin> rr = new JAXBElement<RequestIin>(new QName("", "requestIin"), RequestIin.class, null, r);
	    	
	    	StringWriter sw = new StringWriter();
	    	JAXBContext.newInstance("kz.tamur.gbdul.fl.message").createMarshaller().marshal(rr, sw);
	    	
	    	System.out.println(sw.toString());
	    	
	    	
	    	String xm = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><GUID>51954f01-9919-4691-9de4-19512278a2fe</GUID>" +
	    			"<inquiryGUID>df32633e-0d7c-454f-be2e-5200b123d84c</inquiryGUID><dateMessage>2013-12-28T16:26:52.197+06:00</dateMessage>" +
	    			"<messageResult><code>00000</code><nameRu>Сообщение успешно обработано</nameRu><nameKz>Хабарлама сәтті өңделді</nameKz>" +
	    			"<changeDate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/></messageResult>" +
	    			"<sender><code>GBDFL</code><nameRu>ГБД ФЛ</nameRu><nameKz>ЖТ МДБ</nameKz><changeDate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
	    			"</sender><receiver><code>FINCENTER</code><nameRu>ИС АО «Финансовый центр» МОНРК</nameRu>" +
	    			"<nameKz>ҚРБҒМ «Қаржы орталығы» АҚ АЖ</nameKz><changeDate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
	    			"</receiver><persons><person><iin>080624651992</iin><fio><surname>АБИМОЛДА</surname><firstname>ИНАЯТ</firstname>" +
	    			"<secondname>ДАНИЯРҚЫЗЫ</secondname></fio><birthDate>2008-06-24</birthDate><sex><code>2</code><nameRu>Женский</nameRu>" +
	    			"<nameKz>Әйел</nameKz><changeDate>2013-04-23T16:08:33+06:00</changeDate></sex><nationality><code>999</code>" +
	    			"<nameRu>НЕ УКАЗАНА</nameRu><nameKz>КӨРСЕТІЛГЕН ЖОҚ</nameKz><changeDate>2008-03-01T13:21:45+06:00</changeDate>" +
	    			"</nationality><citizenship><code>398</code><nameRu>КАЗАХСТАН</nameRu><nameKz>ҚАЗАҚСТАН</nameKz><changeDate>2008-03-01T13:21:44+06:00</changeDate></citizenship>" +
	    			"<personStatus><code>0</code><nameRu>Нормальный</nameRu><nameKz>Қалыпты</nameKz><changeDate>2008-03-01T13:21:45+06:00</changeDate></personStatus>" +
	    			"<addDocs><birthSvidNumber>0568489</birthSvidNumber><birthSvidBeginDate>2008-07-03</birthSvidBeginDate>" +
	    			"<birthSvidIssueOrg>ТАЛГАРСКИЙ РАЙОННЫЙ ОТДЕЛ ЗАГС</birthSvidIssueOrg></addDocs><regAddress><city>-</city></regAddress>" +
	    			"<birthPlace><country><code>398</code><nameRu>КАЗАХСТАН</nameRu><nameKz>ҚАЗАҚСТАН</nameKz>" +
	    			"<changeDate>2008-03-01T13:21:44+06:00</changeDate></country><district><code>1907</code><nameRu>АЛМАТИНСКАЯ</nameRu><nameKz>АЛМАТЫ</nameKz>" +
	    			"<changeDate>2008-03-01T13:21:45+06:00</changeDate></district><region><code>1907211</code><nameRu>ИЛИЙСКИЙ РАЙОН</nameRu><nameKz>ІЛЕ АУДАНЫ</nameKz>" +
	    			"<changeDate>2008-03-01T13:21:45+06:00</changeDate></region><city>ӨТЕГЕН БАТЫР</city></birthPlace><documents/><removed>false</removed></person></persons>" +
	    			"<version>1</version><ds:Signature></ds:Signature></response>";
	    	
			int a = xm.indexOf("<ds:Signature");
			int b = xm.indexOf("</ds:Signature>");
			xm = xm.substring(0, a) + xm.substring(b+15);

			StringReader sr = new StringReader(xm);
	    	JAXBElement<Response> res = (JAXBElement<Response>)JAXBContext.newInstance("kz.tamur.gbdul.fl.service").createUnmarshaller().unmarshal(sr);
	    	Response rs = res.getValue();
	    	
	    	System.out.println(rs.getPersons().getPerson().size());
	    	
	    	return sw.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
    	kz.tamur.gbdul.fl.message.ObjectFactory f1 = new kz.tamur.gbdul.fl.message.ObjectFactory();
    	RequestIin r = f1.createRequestIin();
    	r.setGUID("ddddddddd");
    	
    	marshallRequest(r);
	}
}
