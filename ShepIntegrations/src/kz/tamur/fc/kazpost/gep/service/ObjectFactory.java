
package kz.tamur.fc.kazpost.gep.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fc.kazpost.gep.service package. 
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

    private final static QName _SendMessageFault1SendMessageFault_QNAME = new QName("http://bip.bee.kz/SyncChannel/v10/Types", "SendMessageFault1_SendMessageFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fc.kazpost.gep.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SyncSendMessageResponse }
     * 
     */
    public SyncSendMessageResponse createSyncSendMessageResponse() {
        return new SyncSendMessageResponse();
    }

    /**
     * Create an instance of {@link ResponseData }
     * 
     */
    public ResponseData createResponseData() {
        return new ResponseData();
    }

    /**
     * Create an instance of {@link ErrorInfo }
     * 
     */
    public ErrorInfo createErrorInfo() {
        return new ErrorInfo();
    }

    /**
     * Create an instance of {@link SenderInfo }
     * 
     */
    public SenderInfo createSenderInfo() {
        return new SenderInfo();
    }

    /**
     * Create an instance of {@link StatusInfo }
     * 
     */
    public StatusInfo createStatusInfo() {
        return new StatusInfo();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link SendMessage }
     * 
     */
    public SendMessage createSendMessage() {
        return new SendMessage();
    }

    /**
     * Create an instance of {@link SyncSendMessageRequest }
     * 
     */
    public SyncSendMessageRequest createSyncSendMessageRequest() {
        return new SyncSendMessageRequest();
    }

    /**
     * Create an instance of {@link SendMessageResponse }
     * 
     */
    public SendMessageResponse createSendMessageResponse() {
        return new SendMessageResponse();
    }

    /**
     * Create an instance of {@link SyncMessageInfo }
     * 
     */
    public SyncMessageInfo createSyncMessageInfo() {
        return new SyncMessageInfo();
    }

    /**
     * Create an instance of {@link SyncMessageInfoResponse }
     * 
     */
    public SyncMessageInfoResponse createSyncMessageInfoResponse() {
        return new SyncMessageInfoResponse();
    }

    /**
     * Create an instance of {@link RequestData }
     * 
     */
    public RequestData createRequestData() {
        return new RequestData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/SyncChannel/v10/Types", name = "SendMessageFault1_SendMessageFault")
    public JAXBElement<ErrorInfo> createSendMessageFault1SendMessageFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_SendMessageFault1SendMessageFault_QNAME, ErrorInfo.class, null, value);
    }

}
