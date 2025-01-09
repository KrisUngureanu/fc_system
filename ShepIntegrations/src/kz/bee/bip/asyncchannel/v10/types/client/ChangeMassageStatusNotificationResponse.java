
package kz.bee.bip.asyncchannel.v10.types.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncChangeStatusNotifyResponse;


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
 *         &lt;element name="response" type="{http://bip.bee.kz/AsyncChannel/v10/ITypes}AsyncChangeStatusNotifyResponse"/>
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
    "response"
})
@XmlRootElement(name = "changeMassageStatusNotificationResponse")
public class ChangeMassageStatusNotificationResponse {

    @XmlElement(required = true, nillable = true)
    protected AsyncChangeStatusNotifyResponse response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link AsyncChangeStatusNotifyResponse }
     *     
     */
    public AsyncChangeStatusNotifyResponse getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link AsyncChangeStatusNotifyResponse }
     *     
     */
    public void setResponse(AsyncChangeStatusNotifyResponse value) {
        this.response = value;
    }

}
