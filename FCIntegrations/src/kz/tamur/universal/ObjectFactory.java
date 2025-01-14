//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.10 at 07:45:01 PM ALMT 
//


package kz.tamur.universal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.universal package. 
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

    private final static QName _Response_QNAME = new QName("http://tamur.kz/schemes/rn/universal", "Response");
    private final static QName _Request_QNAME = new QName("http://tamur.kz/schemes/rn/universal", "Request");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.universal
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResponseMessage }
     * 
     */
    public ResponseMessage createResponseMessage() {
        return new ResponseMessage();
    }

    /**
     * Create an instance of {@link RequestMessage }
     * 
     */
    public RequestMessage createRequestMessage() {
        return new RequestMessage();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link AttrType }
     * 
     */
    public AttrType createAttrType() {
        return new AttrType();
    }

    /**
     * Create an instance of {@link EntityValueType }
     * 
     */
    public EntityValueType createEntityValueType() {
        return new EntityValueType();
    }

    /**
     * Create an instance of {@link ResponseDataType }
     * 
     */
    public ResponseDataType createResponseDataType() {
        return new ResponseDataType();
    }

    /**
     * Create an instance of {@link EntityType }
     * 
     */
    public EntityType createEntityType() {
        return new EntityType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link RequestDataType }
     * 
     */
    public RequestDataType createRequestDataType() {
        return new RequestDataType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tamur.kz/schemes/rn/universal", name = "Response")
    public JAXBElement<ResponseMessage> createResponse(ResponseMessage value) {
        return new JAXBElement<ResponseMessage>(_Response_QNAME, ResponseMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tamur.kz/schemes/rn/universal", name = "Request")
    public JAXBElement<RequestMessage> createRequest(RequestMessage value) {
        return new JAXBElement<RequestMessage>(_Request_QNAME, RequestMessage.class, null, value);
    }

}
