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
import javax.xml.bind.annotation.XmlType;


/**
 * Инициирование запроса к ШЭП
 * 
 * <p>Java class for QueryEGovGate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryEGovGate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="callOrigin" type="{http://schemas.letograf.kz/iiscon/bus/v1}CallOrigin"/>
 *         &lt;element name="localRequestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="endpointCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestBody" type="{http://schemas.letograf.kz/iiscon/bus/v1}Any"/>
 *         &lt;element name="requestDocuments" type="{http://schemas.letograf.kz/iiscon/bus/v1}Documents" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryEGovGate", propOrder = {
    "applicationId",
    "callOrigin",
    "localRequestId",
    "requestTypeCode",
    "endpointCode",
    "requestBody",
    "requestDocuments"
})
public class QueryEGovGate {

    @XmlElement(required = true)
    protected String applicationId;
    @XmlElement(required = true)
    protected CallOrigin callOrigin;
    @XmlElement(required = true)
    protected String localRequestId;
    @XmlElement(required = true)
    protected String requestTypeCode;
    protected String endpointCode;
    @XmlElement(required = true)
    protected Any requestBody;
    protected Documents requestDocuments;

    /**
     * Gets the value of the applicationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the value of the applicationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationId(String value) {
        this.applicationId = value;
    }

    /**
     * Gets the value of the callOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link CallOrigin }
     *     
     */
    public CallOrigin getCallOrigin() {
        return callOrigin;
    }

    /**
     * Sets the value of the callOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link CallOrigin }
     *     
     */
    public void setCallOrigin(CallOrigin value) {
        this.callOrigin = value;
    }

    /**
     * Gets the value of the localRequestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalRequestId() {
        return localRequestId;
    }

    /**
     * Sets the value of the localRequestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalRequestId(String value) {
        this.localRequestId = value;
    }

    /**
     * Gets the value of the requestTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestTypeCode() {
        return requestTypeCode;
    }

    /**
     * Sets the value of the requestTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestTypeCode(String value) {
        this.requestTypeCode = value;
    }

    /**
     * Gets the value of the endpointCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndpointCode() {
        return endpointCode;
    }

    /**
     * Sets the value of the endpointCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndpointCode(String value) {
        this.endpointCode = value;
    }

    /**
     * Gets the value of the requestBody property.
     * 
     * @return
     *     possible object is
     *     {@link Any }
     *     
     */
    public Any getRequestBody() {
        return requestBody;
    }

    /**
     * Sets the value of the requestBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link Any }
     *     
     */
    public void setRequestBody(Any value) {
        this.requestBody = value;
    }

    /**
     * Gets the value of the requestDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link Documents }
     *     
     */
    public Documents getRequestDocuments() {
        return requestDocuments;
    }

    /**
     * Sets the value of the requestDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Documents }
     *     
     */
    public void setRequestDocuments(Documents value) {
        this.requestDocuments = value;
    }

}
