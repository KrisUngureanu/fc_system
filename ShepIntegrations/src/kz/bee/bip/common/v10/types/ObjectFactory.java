
package kz.bee.bip.common.v10.types;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.bee.bip.common.v10.types package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.bee.bip.common.v10.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SenderInfo }
     * 
     */
    public SenderInfo createSenderInfo() {
        return new SenderInfo();
    }

    /**
     * Create an instance of {@link ErrorInfo }
     * 
     */
    public ErrorInfo createErrorInfo() {
        return new ErrorInfo();
    }

    /**
     * Create an instance of {@link DeliveryStatusInfo }
     * 
     */
    public DeliveryStatusInfo createDeliveryStatusInfo() {
        return new DeliveryStatusInfo();
    }

    /**
     * Create an instance of {@link DeliveryNotification }
     * 
     */
    public DeliveryNotification createDeliveryNotification() {
        return new DeliveryNotification();
    }

    /**
     * Create an instance of {@link ChangeStatusNotification }
     * 
     */
    public ChangeStatusNotification createChangeStatusNotification() {
        return new ChangeStatusNotification();
    }

    /**
     * Create an instance of {@link MessageStatusInfo }
     * 
     */
    public MessageStatusInfo createMessageStatusInfo() {
        return new MessageStatusInfo();
    }

    /**
     * Create an instance of {@link MessageData }
     * 
     */
    public MessageData createMessageData() {
        return new MessageData();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

}
