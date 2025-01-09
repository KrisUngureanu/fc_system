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
 * Оповещение об изменении данных заявки
 * 
 * <p>Java class for UpdateApplicationNotification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateApplicationNotification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="callOrigin" type="{http://schemas.letograf.kz/iiscon/bus/v1}CallOrigin"/>
 *         &lt;element name="callDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="applicationData" type="{http://schemas.letograf.kz/iiscon/bus/v1}ApplicationData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateApplicationNotification", propOrder = {
    "applicationId",
    "callOrigin",
    "callDate",
    "applicationData"
})
public class UpdateApplicationNotification {

    @XmlElement(required = true)
    protected String applicationId;
    @XmlElement(required = true)
    protected CallOrigin callOrigin;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar callDate;
    @XmlElement(required = true)
    protected ApplicationData applicationData;

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
     * Gets the value of the callDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCallDate() {
        return callDate;
    }

    /**
     * Sets the value of the callDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCallDate(XMLGregorianCalendar value) {
        this.callDate = value;
    }

    /**
     * Gets the value of the applicationData property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationData }
     *     
     */
    public ApplicationData getApplicationData() {
        return applicationData;
    }

    /**
     * Sets the value of the applicationData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationData }
     *     
     */
    public void setApplicationData(ApplicationData value) {
        this.applicationData = value;
    }

}
