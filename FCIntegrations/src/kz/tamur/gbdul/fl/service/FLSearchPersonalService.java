
package kz.tamur.gbdul.fl.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "FLSearchPersonalService", targetNamespace = "http://webservice.request.universal.interactive.nat/Binding", wsdlLocation = "./Search_UniversalServiceHttp_Service.wsdl")
public class FLSearchPersonalService
    extends Service
{

	public FLSearchPersonalService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }
    public FLSearchPersonalService() {
    	super(kz.tamur.gbdul.fl.service.FLSearchPersonalService.class.getResource("Search_UniversalServiceHttp_Service.wsdl"), new QName("http://webservice.request.universal.interactive.nat/Binding", "FLSearchPersonalService"));
    }

    /**
     * 
     * @return
     *     returns UniversalService
     */
    @WebEndpoint(name = "SearchBinding")
    public UniversalService getSearchBinding() {
        return super.getPort(new QName("http://webservice.request.universal.interactive.nat/Binding", "SearchBinding"), UniversalService.class);
    }


}
