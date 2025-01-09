package kz.tamur.fc.bank.record;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class was generated by Apache CXF 2.4.6 2014-01-29T09:20:21.332+06:00
 * Generated source version: 2.4.6
 * 
 */
@WebServiceClient(name = "FCBANKService", wsdlLocation = "fc.wsdl", targetNamespace = "http://record.bank.fc.tamur.kz")
public class FCBANKService extends Service {

	public final static URL WSDL_LOCATION;

	public final static QName SERVICE = new QName(
			"http://record.bank.fc.tamur.kz", "FCBANKService");
	public final static QName FCBANKServiceSoap = new QName(
			"http://record.bank.fc.tamur.kz", "FCBANKServiceSoap");
	static {
		URL url = FCBANKService.class.getResource("fc.wsdl");
		if (url == null) {
			java.util.logging.Logger.getLogger(FCBANKService.class.getName())
					.log(java.util.logging.Level.INFO,
							"Can not initialize the default wsdl from {0}",
							"fc.wsdl");
		}
		WSDL_LOCATION = url;
	}

	public FCBANKService(URL wsdlLocation) {
		super(wsdlLocation, SERVICE);
	}

	public FCBANKService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public FCBANKService() {
		super(WSDL_LOCATION, SERVICE);
	}

	/**
	 * 
	 * @return returns FCBANKServiceSoap
	 */
	@WebEndpoint(name = "FCBANKServiceSoap")
	public FCBANKServiceSoap getFCBANKServiceSoap() {
		return super.getPort(FCBANKServiceSoap, FCBANKServiceSoap.class);
	}
}