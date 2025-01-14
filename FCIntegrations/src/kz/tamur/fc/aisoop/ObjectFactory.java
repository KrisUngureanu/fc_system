//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.10.23 at 10:55:02 AM ALMT 
//


package kz.tamur.fc.aisoop;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fc.aisoop package. 
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

    private final static QName _Response_QNAME = new QName("http://aisOOP.kz", "response");
    private final static QName _Request_QNAME = new QName("http://aisOOP.kz", "request");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fc.aisoop
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link PersonIin }
     * 
     */
    public PersonIin createPersonIin() {
        return new PersonIin();
    }

    /**
     * Create an instance of {@link ResPersonInfo }
     * 
     */
    public ResPersonInfo createResPersonInfo() {
        return new ResPersonInfo();
    }

    /**
     * Create an instance of {@link MainMessage }
     * 
     */
    public MainMessage createMainMessage() {
        return new MainMessage();
    }

    /**
     * Create an instance of {@link ResPersonInfos }
     * 
     */
    public ResPersonInfos createResPersonInfos() {
        return new ResPersonInfos();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://aisOOP.kz", name = "response")
    public JAXBElement<Response> createResponse(Response value) {
        return new JAXBElement<Response>(_Response_QNAME, Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Request }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://aisOOP.kz", name = "request")
    public JAXBElement<Request> createRequest(Request value) {
        return new JAXBElement<Request>(_Request_QNAME, Request.class, null, value);
    }

}
