package kz.tamur.fc.bank.service;

import javax.jws.HandlerChain;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.6
 * 2015-08-10T16:52:46.172+06:00
 * Generated source version: 2.4.6
 * 
 */
@WebServiceClient(name = "DataPumpService", 
                  wsdlLocation = "./DataPumpService.wsdl",
                  targetNamespace = "https://ws.creditinfo.com") 
@HandlerChain(file = "chainFcb.xml")
public class DataPumpService extends Service {

    public DataPumpService() {
        super(kz.tamur.fc.bank.service.DataPumpService.class.getResource("DataPumpService.wsdl"), new QName("https://ws.creditinfo.com", "DataPumpService"));
    }
    
    /**
     *
     * @return
     *     returns DataPumpServiceSoap
     */
    @WebEndpoint(name = "DataPumpServiceSoap12")
    public DataPumpServiceSoap getDataPumpServiceSoap12() {
        return super.getPort(new QName("https://ws.creditinfo.com", "DataPumpServiceSoap12"), DataPumpServiceSoap.class);
    }

    /**
     *
     * @return
     *     returns DataPumpServiceSoap
     */
    @WebEndpoint(name = "DataPumpServiceSoap")
    public DataPumpServiceSoap getDataPumpServiceSoap() {
        return super.getPort(new QName("https://ws.creditinfo.com", "DataPumpServiceSoap"), DataPumpServiceSoap.class);
    }


}
