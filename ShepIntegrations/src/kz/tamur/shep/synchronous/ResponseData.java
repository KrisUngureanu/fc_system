
package kz.tamur.shep.synchronous;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import kz.tamur.fc.kazpost.gep.SendRPOResponse;


/**
 * <p>Java class for ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseData", namespace = "http://bip.bee.kz/SyncChannel/v10/Types/Response", propOrder = {
    "data", "sendRPOResponse"
})
public class ResponseData {

    protected Object data;
    protected SendRPOResponse sendRPOResponse;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setData(Object value) {
        this.data = value;
    }

	/**
	 * @return the sendRPOResponse
	 */
	public SendRPOResponse getSendRPOResponse() {
		return sendRPOResponse;
	}

	/**
	 * @param sendRPOResponse the sendRPOResponse to set
	 */
	public void setSendRPOResponse(SendRPOResponse sendRPOResponse) {
		this.sendRPOResponse = sendRPOResponse;
	}

}
