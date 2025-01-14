//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.06 at 05:57:02 PM GMT+05:00 
//


package kz.tamur.fc.egov.sms.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Запрос на отправку сообщения
 * 
 * <p>Java class for SmsSendRequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SmsSendRequestData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="properties" type="{http://sms.egov.inessoft.kz/common/v10/Types}SmsProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="authentication" type="{http://sms.egov.inessoft.kz/common/v10/Types}LoginCredentials"/>
 *         &lt;element name="operatorData" type="{http://sms.egov.inessoft.kz/common/v10/Types}OperatorData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SmsSendRequestData", propOrder = {
    "text",
    "serviceId",
    "properties",
    "authentication",
    "operatorData"
})
public class SmsSendRequestData {

    @XmlElement(required = true)
    protected String text;
    @XmlElement(required = true)
    protected String serviceId;
    protected List<SmsProperty> properties;
    @XmlElement(required = true)
    protected LoginCredentials authentication;
    @XmlElement(required = true)
    protected OperatorData operatorData;

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the serviceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Sets the value of the serviceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceId(String value) {
        this.serviceId = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the properties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmsProperty }
     * 
     * 
     */
    public List<SmsProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<SmsProperty>();
        }
        return this.properties;
    }

    /**
     * Gets the value of the authentication property.
     * 
     * @return
     *     possible object is
     *     {@link LoginCredentials }
     *     
     */
    public LoginCredentials getAuthentication() {
        return authentication;
    }

    /**
     * Sets the value of the authentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginCredentials }
     *     
     */
    public void setAuthentication(LoginCredentials value) {
        this.authentication = value;
    }

    /**
     * Gets the value of the operatorData property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorData }
     *     
     */
    public OperatorData getOperatorData() {
        return operatorData;
    }

    /**
     * Sets the value of the operatorData property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorData }
     *     
     */
    public void setOperatorData(OperatorData value) {
        this.operatorData = value;
    }

}
