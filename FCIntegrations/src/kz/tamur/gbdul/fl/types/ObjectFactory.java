
package kz.tamur.gbdul.fl.types;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.fl.types package. 
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

    private final static QName _FindPersonByFioResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByFioResponse");
    private final static QName _FindPerson_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPerson");
    private final static QName _FindPersonByIin_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByIin");
    private final static QName _FindPersonByDocResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByDocResponse");
    private final static QName _FindPersonByFio_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByFio");
    private final static QName _Exception_QNAME = new QName("http://webservice.request.universal.interactive.nat", "Exception");
    private final static QName _FindPersonByIinResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByIinResponse");
    private final static QName _FindPersonResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonResponse");
    private final static QName _FindPersonByDoc_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByDoc");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.fl.types
     * 
     */
    public ObjectFactory() {
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
     * Create an instance of {@link FindPersonByDoc }
     * 
     */
    public FindPersonByDoc createFindPersonByDoc() {
        return new FindPersonByDoc();
    }

    /**
     * Create an instance of {@link FindPersonByFio }
     * 
     */
    public FindPersonByFio createFindPersonByFio() {
        return new FindPersonByFio();
    }

    /**
     * Create an instance of {@link FindPersonResponse }
     * 
     */
    public FindPersonResponse createFindPersonResponse() {
        return new FindPersonResponse();
    }

    /**
     * Create an instance of {@link FindPerson }
     * 
     */
    public FindPerson createFindPerson() {
        return new FindPerson();
    }

    /**
     * Create an instance of {@link FindPersonByDocResponse }
     * 
     */
    public FindPersonByDocResponse createFindPersonByDocResponse() {
        return new FindPersonByDocResponse();
    }

    /**
     * Create an instance of {@link FindPersonByFioResponse }
     * 
     */
    public FindPersonByFioResponse createFindPersonByFioResponse() {
        return new FindPersonByFioResponse();
    }

    /**
     * Create an instance of {@link FindPersonByIin }
     * 
     */
    public FindPersonByIin createFindPersonByIin() {
        return new FindPersonByIin();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByFioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByFioResponse")
    public JAXBElement<FindPersonByFioResponse> createFindPersonByFioResponse(FindPersonByFioResponse value) {
        return new JAXBElement<FindPersonByFioResponse>(_FindPersonByFioResponse_QNAME, FindPersonByFioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPerson }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPerson")
    public JAXBElement<FindPerson> createFindPerson(FindPerson value) {
        return new JAXBElement<FindPerson>(_FindPerson_QNAME, FindPerson.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByIin")
    public JAXBElement<FindPersonByIin> createFindPersonByIin(FindPersonByIin value) {
        return new JAXBElement<FindPersonByIin>(_FindPersonByIin_QNAME, FindPersonByIin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByDocResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByDocResponse")
    public JAXBElement<FindPersonByDocResponse> createFindPersonByDocResponse(FindPersonByDocResponse value) {
        return new JAXBElement<FindPersonByDocResponse>(_FindPersonByDocResponse_QNAME, FindPersonByDocResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByFio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByFio")
    public JAXBElement<FindPersonByFio> createFindPersonByFio(FindPersonByFio value) {
        return new JAXBElement<FindPersonByFio>(_FindPersonByFio_QNAME, FindPersonByFio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIinResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByIinResponse")
    public JAXBElement<FindPersonByIinResponse> createFindPersonByIinResponse(FindPersonByIinResponse value) {
        return new JAXBElement<FindPersonByIinResponse>(_FindPersonByIinResponse_QNAME, FindPersonByIinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonResponse")
    public JAXBElement<FindPersonResponse> createFindPersonResponse(FindPersonResponse value) {
        return new JAXBElement<FindPersonResponse>(_FindPersonResponse_QNAME, FindPersonResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByDoc }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByDoc")
    public JAXBElement<FindPersonByDoc> createFindPersonByDoc(FindPersonByDoc value) {
        return new JAXBElement<FindPersonByDoc>(_FindPersonByDoc_QNAME, FindPersonByDoc.class, null, value);
    }

}
