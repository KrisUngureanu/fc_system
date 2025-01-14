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
 * Информация о статусе исполнения заявки
 * 
 * <p>Java class for ExecutionStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExecutionStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractStatus">
 *       &lt;sequence>
 *         &lt;element name="appState" type="{http://schemas.letograf.kz/iiscon/bus/v1}AppState"/>
 *         &lt;element name="isPositive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="refuseReason" type="{http://schemas.letograf.kz/iiscon/bus/v1}refuseReasonType" minOccurs="0"/>
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
@XmlType(name = "ExecutionStatus", propOrder = {
    "appState",
    "isPositive",
    "refuseReason",
    "statusInfo",
    "statusInfoKz"
})
public class ExecutionStatus
    extends AbstractStatus
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AppState appState;
    protected Boolean isPositive;
    @XmlSchemaType(name = "string")
    protected RefuseReasonType refuseReason;
    protected String statusInfo;
    protected String statusInfoKz;

    /**
     * Gets the value of the appState property.
     * 
     * @return
     *     possible object is
     *     {@link AppState }
     *     
     */
    public AppState getAppState() {
        return appState;
    }

    /**
     * Sets the value of the appState property.
     * 
     * @param value
     *     allowed object is
     *     {@link AppState }
     *     
     */
    public void setAppState(AppState value) {
        this.appState = value;
    }

    /**
     * Gets the value of the isPositive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsPositive() {
        return isPositive;
    }

    /**
     * Sets the value of the isPositive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPositive(Boolean value) {
        this.isPositive = value;
    }

    /**
     * Gets the value of the refuseReason property.
     * 
     * @return
     *     possible object is
     *     {@link RefuseReasonType }
     *     
     */
    public RefuseReasonType getRefuseReason() {
        return refuseReason;
    }

    /**
     * Sets the value of the refuseReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefuseReasonType }
     *     
     */
    public void setRefuseReason(RefuseReasonType value) {
        this.refuseReason = value;
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
