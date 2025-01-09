
package kz.tamur.gbdul.fl.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import kz.tamur.gbdul.fl.message.Request;
import kz.tamur.gbdul.fl.message.RequestDoc;
import kz.tamur.gbdul.fl.message.RequestFio;
import kz.tamur.gbdul.fl.message.RequestIin;
import kz.tamur.gbdul.fl.message.Response;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.fl.service package. 
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

    private final static QName _RequestIin_QNAME = new QName("", "requestIin");
    private final static QName _Request_QNAME = new QName("webservice.request.universal.interactive.nat", "request");
    private final static QName _Response_QNAME = new QName("", "response");
    private final static QName _RequestDoc_QNAME = new QName("webservice.request.universal.interactive.nat", "requestDoc");
    private final static QName _RequestFio_QNAME = new QName("webservice.request.universal.interactive.nat", "requestFio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.fl.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestIin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "requestIin")
    public JAXBElement<RequestIin> createRequestIin(RequestIin value) {
        return new JAXBElement<RequestIin>(_RequestIin_QNAME, RequestIin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Request }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "webservice.request.universal.interactive.nat", name = "request")
    public JAXBElement<Request> createRequest(Request value) {
        return new JAXBElement<Request>(_Request_QNAME, Request.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "response")
    public JAXBElement<Response> createResponse(Response value) {
        return new JAXBElement<Response>(_Response_QNAME, Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestDoc }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "webservice.request.universal.interactive.nat", name = "requestDoc")
    public JAXBElement<RequestDoc> createRequestDoc(RequestDoc value) {
        return new JAXBElement<RequestDoc>(_RequestDoc_QNAME, RequestDoc.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestFio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "webservice.request.universal.interactive.nat", name = "requestFio")
    public JAXBElement<RequestFio> createRequestFio(RequestFio value) {
        return new JAXBElement<RequestFio>(_RequestFio_QNAME, RequestFio.class, null, value);
    }

}
