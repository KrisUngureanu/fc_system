//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.06 at 05:57:02 PM GMT+05:00 
//


package kz.tamur.fc.egov.sms.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OperatorData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperatorData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operatorId" type="{http://sms.egov.inessoft.kz/common/v10/Types}OperatorType" minOccurs="0"/>
 *         &lt;element name="shortCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smsType" type="{http://sms.egov.inessoft.kz/common/v10/Types}SmsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperatorData", propOrder = {
    "operatorId",
    "shortCode",
    "phoneNumber",
    "requestId",
    "smsType"
})
public class OperatorData {

    @XmlSchemaType(name = "string")
    protected OperatorType operatorId;
    protected String shortCode;
    @XmlElement(required = true)
    protected String phoneNumber;
    protected String requestId;
    @XmlSchemaType(name = "string")
    protected SmsType smsType;

    /**
     * Gets the value of the operatorId property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorType }
     *     
     */
    public OperatorType getOperatorId() {
        return operatorId;
    }

    /**
     * Sets the value of the operatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorType }
     *     
     */
    public void setOperatorId(OperatorType value) {
        this.operatorId = value;
    }

    /**
     * Gets the value of the shortCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortCode() {
        return shortCode;
    }

    /**
     * Sets the value of the shortCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortCode(String value) {
        this.shortCode = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the smsType property.
     * 
     * @return
     *     possible object is
     *     {@link SmsType }
     *     
     */
    public SmsType getSmsType() {
        return smsType;
    }

    /**
     * Sets the value of the smsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SmsType }
     *     
     */
    public void setSmsType(SmsType value) {
        this.smsType = value;
    }

}