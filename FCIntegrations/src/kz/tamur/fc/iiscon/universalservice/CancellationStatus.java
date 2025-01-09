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


/**
 * Информация о нестандартном завершении оказания услуги
 * 
 * <p>Java class for CancellationStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CancellationStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractStatus">
 *       &lt;sequence>
 *         &lt;element name="cancelState" type="{http://schemas.letograf.kz/iiscon/bus/v1}CancelState"/>
 *         &lt;element name="cancelReason" type="{http://schemas.letograf.kz/iiscon/bus/v1}cancelReasonType" minOccurs="0"/>
 *         &lt;element name="statusInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusInfoKz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancellationStatus", propOrder = {
    "cancelState",
    "cancelReason",
    "statusInfo",
    "statusInfoKz"
})
public class CancellationStatus
    extends AbstractStatus
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CancelState cancelState;
    @XmlSchemaType(name = "string")
    protected CancelReasonType cancelReason;
    protected String statusInfo;
    protected String statusInfoKz;

    /**
     * Gets the value of the cancelState property.
     * 
     * @return
     *     possible object is
     *     {@link CancelState }
     *     
     */
    public CancelState getCancelState() {
        return cancelState;
    }

    /**
     * Sets the value of the cancelState property.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelState }
     *     
     */
    public void setCancelState(CancelState value) {
        this.cancelState = value;
    }

    /**
     * Gets the value of the cancelReason property.
     * 
     * @return
     *     possible object is
     *     {@link CancelReasonType }
     *     
     */
    public CancelReasonType getCancelReason() {
        return cancelReason;
    }

    /**
     * Sets the value of the cancelReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelReasonType }
     *     
     */
    public void setCancelReason(CancelReasonType value) {
        this.cancelReason = value;
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

}
