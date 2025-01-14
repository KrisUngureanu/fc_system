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
 * Ответ ШЭП на вызов асинхронного сервиса
 * 
 * <p>Java class for OnQueryEGovGateAsyncReply complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OnQueryEGovGateAsyncReply">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="localRequestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="endpointCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responseBody" type="{http://schemas.letograf.kz/iiscon/bus/v1}Any" minOccurs="0"/>
 *         &lt;element name="responseDocuments" type="{http://schemas.letograf.kz/iiscon/bus/v1}Documents" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnQueryEGovGateAsyncReply", propOrder = {
    "applicationId",
    "localRequestId",
    "requestTypeCode",
    "endpointCode",
    "requestId",
    "responseDate",
    "errorMessage",
    "responseBody",
    "responseDocuments"
})
public class OnQueryEGovGateAsyncReply {

    @XmlElement(required = true)
    protected String applicationId;
    @XmlElement(required = true)
    protected String localRequestId;
    @XmlElement(required = true)
    protected String requestTypeCode;
    protected String endpointCode;
    @XmlElement(required = true)
    protected String requestId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar responseDate;
    protected String errorMessage;
    protected Any responseBody;
    protected Documents responseDocuments;

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
     * Gets the value of the responseDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getResponseDate() {
        return responseDate;
    }

    /**
     * Sets the value of the responseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setResponseDate(XMLGregorianCalendar value) {
        this.responseDate = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Gets the value of the responseBody property.
     * 
     * @return
     *     possible object is
     *     {@link Any }
     *     
     */
    public Any getResponseBody() {
        return responseBody;
    }

    /**
     * Sets the value of the responseBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link Any }
     *     
     */
    public void setResponseBody(Any value) {
        this.responseBody = value;
    }

    /**
     * Gets the value of the responseDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link Documents }
     *     
     */
    public Documents getResponseDocuments() {
        return responseDocuments;
    }

    /**
     * Sets the value of the responseDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Documents }
     *     
     */
    public void setResponseDocuments(Documents value) {
        this.responseDocuments = value;
    }

}
