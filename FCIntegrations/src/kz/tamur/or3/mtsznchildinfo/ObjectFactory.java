
package kz.tamur.or3.mtsznchildinfo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.or3.mtsznchildinfo package. 
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

    private final static QName _GetChildInfo_QNAME = new QName("http://mon.services.gcvp.kz/", "getChildInfo");
    private final static QName _GetChildInfoResponse_QNAME = new QName("http://mon.services.gcvp.kz/", "getChildInfoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.or3.mtsznchildinfo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetChildInfo }
     * 
     */
    public GetChildInfo createGetChildInfo() {
        return new GetChildInfo();
    }

    /**
     * Create an instance of {@link Payment }
     * 
     */
    public Payment createPayment() {
        return new Payment();
    }

    /**
     * Create an instance of {@link RequestInfo }
     * 
     */
    public RequestInfo createRequestInfo() {
        return new RequestInfo();
    }

    /**
     * Create an instance of {@link ChildResponse }
     * 
     */
    public ChildResponse createChildResponse() {
        return new ChildResponse();
    }

    /**
     * Create an instance of {@link ReferenceValue }
     * 
     */
    public ReferenceValue createReferenceValue() {
        return new ReferenceValue();
    }

    /**
     * Create an instance of {@link ChildRequest }
     * 
     */
    public ChildRequest createChildRequest() {
        return new ChildRequest();
    }

    /**
     * Create an instance of {@link ResponseInfo }
     * 
     */
    public ResponseInfo createResponseInfo() {
        return new ResponseInfo();
    }

    /**
     * Create an instance of {@link SystemInfo }
     * 
     */
    public SystemInfo createSystemInfo() {
        return new SystemInfo();
    }

    /**
     * Create an instance of {@link GetChildInfoResponse }
     * 
     */
    public GetChildInfoResponse createGetChildInfoResponse() {
        return new GetChildInfoResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChildInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mon.services.gcvp.kz/", name = "getChildInfo")
    public JAXBElement<GetChildInfo> createGetChildInfo(GetChildInfo value) {
        return new JAXBElement<GetChildInfo>(_GetChildInfo_QNAME, GetChildInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChildInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mon.services.gcvp.kz/", name = "getChildInfoResponse")
    public JAXBElement<GetChildInfoResponse> createGetChildInfoResponse(GetChildInfoResponse value) {
        return new JAXBElement<GetChildInfoResponse>(_GetChildInfoResponse_QNAME, GetChildInfoResponse.class, null, value);
    }

}
