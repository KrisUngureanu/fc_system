//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Информация о приостановке/возобновлении исполнения заявки
 * 
 * <p>Java class for SuspensionStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuspensionStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractStatus">
 *       &lt;sequence>
 *         &lt;element name="isSuspended" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="statusInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusInfoKz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suspendedDeadline" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuspensionStatus", propOrder = {
    "isSuspended",
    "statusInfo",
    "statusInfoKz",
    "suspendedDeadline"
})
public class SuspensionStatus
    extends AbstractStatus
{

    protected boolean isSuspended;
    protected String statusInfo;
    protected String statusInfoKz;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar suspendedDeadline;

    /**
     * Gets the value of the isSuspended property.
     * 
     */
    public boolean isIsSuspended() {
        return isSuspended;
    }

    /**
     * Sets the value of the isSuspended property.
     * 
     */
    public void setIsSuspended(boolean value) {
        this.isSuspended = value;
    }

    /**
     * Gets the value of the statusInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusInfo() {
        return statusInfo;
    }

    /**
     * Sets the value of the statusInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusInfo(String value) {
        this.statusInfo = value;
    }

    /**
     * Gets the value of the statusInfoKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusInfoKz() {
        return statusInfoKz;
    }

    /**
     * Sets the value of the statusInfoKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusInfoKz(String value) {
        this.statusInfoKz = value;
    }

    /**
     * Gets the value of the suspendedDeadline property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSuspendedDeadline() {
        return suspendedDeadline;
    }

    /**
     * Sets the value of the suspendedDeadline property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSuspendedDeadline(XMLGregorianCalendar value) {
        this.suspendedDeadline = value;
    }

}
