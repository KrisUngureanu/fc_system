
package kz.tamur.gbdul.fl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.fl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FindPersonByIin_QNAME = new QName("http://digisign.webservice.request.universal.interactive.nat", "findPersonByIin");
    private final static QName _FindPersonByIinResponse_QNAME = new QName("http://digisign.webservice.request.universal.interactive.nat", "findPersonByIinResponse");
    private final static QName _Exception_QNAME = new QName("http://digisign.webservice.request.universal.interactive.nat", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.fl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FindPersonByIin }
     * 
     */
    public FindPersonByIin createFindPersonByIin() {
        return new FindPersonByIin();
    }

    /**
     * Create an instance of {@link FindPersonByIinResponse }
     * 
     */
    public FindPersonByIinResponse createFindPersonByIinResponse() {
        return new FindPersonByIinResponse();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://digisign.webservice.request.universal.interactive.nat", name = "findPersonByIin")
    public JAXBElement<FindPersonByIin> createFindPersonByIin(FindPersonByIin value) {
        return new JAXBElement<FindPersonByIin>(_FindPersonByIin_QNAME, FindPersonByIin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIinResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://digisign.webservice.request.universal.interactive.nat", name = "findPersonByIinResponse")
    public JAXBElement<FindPersonByIinResponse> createFindPersonByIinResponse(FindPersonByIinResponse value) {
        return new JAXBElement<FindPersonByIinResponse>(_FindPersonByIinResponse_QNAME, FindPersonByIinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://digisign.webservice.request.universal.interactive.nat", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

}
