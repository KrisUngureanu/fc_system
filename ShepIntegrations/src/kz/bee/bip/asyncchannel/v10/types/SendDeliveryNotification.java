
package kz.bee.bip.asyncchannel.v10.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncSendDeliveryNotificationRequest;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{http://bip.bee.kz/AsyncChannel/v10/ITypes}AsyncSendDeliveryNotificationRequest"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request"
})
@XmlRootElement(name = "sendDeliveryNotification")
public class SendDeliveryNotification {

    @XmlElement(required = true, nillable = true)
    protected AsyncSendDeliveryNotificationRequest request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link AsyncSendDeliveryNotificationRequest }
     *     
     */
    public AsyncSendDeliveryNotificationRequest getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link AsyncSendDeliveryNotificationRequest }
     *     
     */
    public void setRequest(AsyncSendDeliveryNotificationRequest value) {
        this.request = value;
    }

}