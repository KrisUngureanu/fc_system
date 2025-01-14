
package kz.tamur.fl.search;

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
@WebServiceClient(name = "SearchServiceService", targetNamespace = "http://webservice.request.universal.interactive.nat", wsdlLocation = "SearchService_.wsdl")
public class SearchServiceService
    extends Service
{

    private final static URL SEARCHSERVICESERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(kz.tamur.fl.search.SearchServiceService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = kz.tamur.fl.search.SearchServiceService.class.getResource(".");
            url = new URL(baseUrl, "SearchService_.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'SearchService_.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SEARCHSERVICESERVICE_WSDL_LOCATION = url;
    }

    public SearchServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SearchServiceService() {
        super(SEARCHSERVICESERVICE_WSDL_LOCATION, new QName("http://webservice.request.universal.interactive.nat", "SearchServiceService"));
    }

    /**
     * 
     * @return
     *     returns SearchService
     */
    @WebEndpoint(name = "SearchServicePort")
    public SearchService getSearchServicePort() {
        return super.getPort(new QName("http://webservice.request.universal.interactive.nat", "SearchServicePort"), SearchService.class);
    }

}
