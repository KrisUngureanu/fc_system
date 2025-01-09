//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Регистрационные данные заявки
 * 
 * <p>Java class for ApplicationRegistrationData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationRegistrationData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="registerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="registerOrgCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="registerDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="registerEmployee" type="{http://schemas.letograf.kz/iiscon/bus/v1}Employee" minOccurs="0"/>
 *         &lt;element name="smsCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registerMethod" type="{http://schemas.letograf.kz/iiscon/bus/v1}RegisterMethod" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationRegistrationData", propOrder = {
    "registerId",
    "registerOrgCode",
    "serviceTypeCode",
    "registerDate",
    "registerEmployee",
    "smsCode",
    "registerMethod"
})
public class ApplicationRegistrationData {

    @XmlElement(required = true)
    protected String registerId;
    @XmlElement(required = true)
    protected String registerOrgCode;
    @XmlElement(required = true)
    protected String serviceTypeCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registerDate;
    protected Employee registerEmployee;
    protected String smsCode;
    protected String registerMethod;

    /**
     * Gets the value of the registerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisterId() {
        return registerId;
    }

    /**
     * Sets the value of the registerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisterId(String value) {
        this.registerId = value;
    }

    /**
     * Gets the value of the registerOrgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisterOrgCode() {
        return registerOrgCode;
    }

    /**
     * Sets the value of the registerOrgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisterOrgCode(String value) {
        this.registerOrgCode = value;
    }

    /**
     * Gets the value of the serviceTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceTypeCode() {
        return serviceTypeCode;
    }

    /**
     * Sets the value of the serviceTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceTypeCode(String value) {
        this.serviceTypeCode = value;
    }

    /**
     * Gets the value of the registerDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegisterDate() {
        return registerDate;
    }

    /**
     * Sets the value of the registerDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegisterDate(XMLGregorianCalendar value) {
        this.registerDate = value;
    }

    /**
     * Gets the value of the registerEmployee property.
     * 
     * @return
     *     possible object is
     *     {@link Employee }
     *     
     */
    public Employee getRegisterEmployee() {
        return registerEmployee;
    }

    /**
     * Sets the value of the registerEmployee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Employee }
     *     
     */
    public void setRegisterEmployee(Employee value) {
        this.registerEmployee = value;
    }

    /**
     * Gets the value of the smsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsCode() {
        return smsCode;
    }

    /**
     * Sets the value of the smsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsCode(String value) {
        this.smsCode = value;
    }

    /**
     * Gets the value of the registerMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisterMethod() {
        return registerMethod;
    }

    /**
     * Sets the value of the registerMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisterMethod(String value) {
        this.registerMethod = value;
    }

}
