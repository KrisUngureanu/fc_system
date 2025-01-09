
package kz.tamur.gbdul.fl.message;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.fl.message package. 
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

    private final static QName _RequestFioSecondname_QNAME = new QName("", "secondname");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.fl.message
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link RequestFio }
     * 
     */
    public RequestFio createRequestFio() {
        return new RequestFio();
    }

    /**
     * Create an instance of {@link RequestDoc }
     * 
     */
    public RequestDoc createRequestDoc() {
        return new RequestDoc();
    }

    /**
     * Create an instance of {@link RequestIin }
     * 
     */
    public RequestIin createRequestIin() {
        return new RequestIin();
    }

    /**
     * Create an instance of {@link Response.Persons }
     * 
     */
    public Response.Persons createResponsePersons() {
        return new Response.Persons();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "secondname", scope = RequestFio.class)
    public JAXBElement<String> createRequestFioSecondname(String value) {
        return new JAXBElement<String>(_RequestFioSecondname_QNAME, String.class, RequestFio.class, value);
    }

}
