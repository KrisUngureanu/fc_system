//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.09.16 at 12:15:58 AM ALMT 
//


package kz.tamur.fc.mgov.nitec.mbc.verifyphone;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fc.mgov.nitec.mbc.verifyphone package. 
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

    private final static QName _VerifyPhoneResponse_QNAME = new QName("http://kz.nitec.mgov/mbc/verifyphone", "verifyPhoneResponse");
    private final static QName _VerifyPhoneRequest_QNAME = new QName("http://kz.nitec.mgov/mbc/verifyphone", "verifyPhoneRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fc.mgov.nitec.mbc.verifyphone
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VerifyPhoneResponse }
     * 
     */
    public VerifyPhoneResponse createVerifyPhoneResponse() {
        return new VerifyPhoneResponse();
    }

    /**
     * Create an instance of {@link VerifyPhoneRequest }
     * 
     */
    public VerifyPhoneRequest createVerifyPhoneRequest() {
        return new VerifyPhoneRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyPhoneResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://kz.nitec.mgov/mbc/verifyphone", name = "verifyPhoneResponse")
    public JAXBElement<VerifyPhoneResponse> createVerifyPhoneResponse(VerifyPhoneResponse value) {
        return new JAXBElement<VerifyPhoneResponse>(_VerifyPhoneResponse_QNAME, VerifyPhoneResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyPhoneRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://kz.nitec.mgov/mbc/verifyphone", name = "verifyPhoneRequest")
    public JAXBElement<VerifyPhoneRequest> createVerifyPhoneRequest(VerifyPhoneRequest value) {
        return new JAXBElement<VerifyPhoneRequest>(_VerifyPhoneRequest_QNAME, VerifyPhoneRequest.class, null, value);
    }

}
