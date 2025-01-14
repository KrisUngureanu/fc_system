
package kz.tamur.fc.kazpost.gep.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ���������� � ��������� ������
 * 
 * <p>Java class for SyncSendMessageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SyncSendMessageResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="responseInfo" type="{http://bip.bee.kz/SyncChannel/v10/Types}SyncMessageInfoResponse"/>
 *         &lt;element name="responseData" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SyncSendMessageResponse", namespace = "http://bip.bee.kz/SyncChannel/v10/Types/Response", propOrder = {
    "responseInfo",
    "responseData"
})
public class SyncSendMessageResponse {

    @XmlElement(required = true)
    protected SyncMessageInfoResponse responseInfo;
    @XmlElement(required = true)
    protected Object responseData;

    /**
     * Gets the value of the responseInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SyncMessageInfoResponse }
     *     
     */
    public SyncMessageInfoResponse getResponseInfo() {
        return responseInfo;
    }

    /**
     * Sets the value of the responseInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncMessageInfoResponse }
     *     
     */
    public void setResponseInfo(SyncMessageInfoResponse value) {
        this.responseInfo = value;
    }

    /**
     * Gets the value of the responseData property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getResponseData() {
        return responseData;
    }

    /**
     * Sets the value of the responseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setResponseData(Object value) {
        this.responseData = value;
    }

}
