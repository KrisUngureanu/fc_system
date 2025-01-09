//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.24 at 03:16:44 PM ALMT 
//


package kz.tamur.fc.gbdrn.report303;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Формат служебных данных
 * 
 * <p>Java class for BaseMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="declarantId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="requestSystemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://types.303.reports.egov.rn3.tamur.kz}SysDirectoryType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseMessage", propOrder = {
    "requestNumber",
    "declarantId",
    "requestDate",
    "requestSystemId",
    "status"
})
@XmlSeeAlso({
    ResponseMessage.class
})
public class BaseMessage {

    @XmlElement(required = true)
    protected String requestNumber;
    @XmlElement(required = true)
    protected String declarantId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDate;
    protected String requestSystemId;
    protected SysDirectoryType status;

    /**
     * Gets the value of the requestNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestNumber() {
        return requestNumber;
    }

    /**
     * Sets the value of the requestNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestNumber(String value) {
        this.requestNumber = value;
    }

    /**
     * Gets the value of the declarantId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclarantId() {
        return declarantId;
    }

    /**
     * Sets the value of the declarantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclarantId(String value) {
        this.declarantId = value;
    }

    /**
     * Gets the value of the requestDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

    /**
     * Gets the value of the requestSystemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestSystemId() {
        return requestSystemId;
    }

    /**
     * Sets the value of the requestSystemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestSystemId(String value) {
        this.requestSystemId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link SysDirectoryType }
     *     
     */
    public SysDirectoryType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link SysDirectoryType }
     *     
     */
    public void setStatus(SysDirectoryType value) {
        this.status = value;
    }

}
