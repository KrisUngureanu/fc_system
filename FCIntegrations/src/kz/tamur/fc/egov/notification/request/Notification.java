//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.10.26 at 10:02:00 AM ALMT 
//


package kz.tamur.fc.egov.notification.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Notification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Notification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="notificationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="eventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="broadCastType" type="{http://notification-v2.egov.bee.kz/}BroadCastType"/>
 *         &lt;element name="managed" type="{http://notification-v2.egov.bee.kz/}Managed"/>
 *         &lt;element name="notificationType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="notificationVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iinbin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="needCallBack" type="{http://notification-v2.egov.bee.kz/}CallBack"/>
 *         &lt;element name="properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="property" type="{http://notification-v2.egov.bee.kz/}Property" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resources" type="{http://notification-v2.egov.bee.kz/}Resources" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Notification", propOrder = {
    "notificationId",
    "sentDate",
    "eventDate",
    "broadCastType",
    "managed",
    "notificationType",
    "notificationVersion",
    "iinbin",
    "needCallBack",
    "properties",
    "resources"
})
public class Notification {

    @XmlElement(required = true)
    protected String notificationId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sentDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected BroadCastType broadCastType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Managed managed;
    @XmlElement(required = true)
    protected String notificationType;
    @XmlElement(required = true)
    protected String notificationVersion;
    @XmlElement(required = true)
    protected String iinbin;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CallBack needCallBack;
    protected Notification.Properties properties;
    protected Resources resources;

    /**
     * Gets the value of the notificationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the value of the notificationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationId(String value) {
        this.notificationId = value;
    }

    /**
     * Gets the value of the sentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSentDate(XMLGregorianCalendar value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventDate(XMLGregorianCalendar value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the broadCastType property.
     * 
     * @return
     *     possible object is
     *     {@link BroadCastType }
     *     
     */
    public BroadCastType getBroadCastType() {
        return broadCastType;
    }

    /**
     * Sets the value of the broadCastType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BroadCastType }
     *     
     */
    public void setBroadCastType(BroadCastType value) {
        this.broadCastType = value;
    }

    /**
     * Gets the value of the managed property.
     * 
     * @return
     *     possible object is
     *     {@link Managed }
     *     
     */
    public Managed getManaged() {
        return managed;
    }

    /**
     * Sets the value of the managed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Managed }
     *     
     */
    public void setManaged(Managed value) {
        this.managed = value;
    }

    /**
     * Gets the value of the notificationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the value of the notificationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationType(String value) {
        this.notificationType = value;
    }

    /**
     * Gets the value of the notificationVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationVersion() {
        return notificationVersion;
    }

    /**
     * Sets the value of the notificationVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationVersion(String value) {
        this.notificationVersion = value;
    }

    /**
     * Gets the value of the iinbin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIinbin() {
        return iinbin;
    }

    /**
     * Sets the value of the iinbin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIinbin(String value) {
        this.iinbin = value;
    }

    /**
     * Gets the value of the needCallBack property.
     * 
     * @return
     *     possible object is
     *     {@link CallBack }
     *     
     */
    public CallBack getNeedCallBack() {
        return needCallBack;
    }

    /**
     * Sets the value of the needCallBack property.
     * 
     * @param value
     *     allowed object is
     *     {@link CallBack }
     *     
     */
    public void setNeedCallBack(CallBack value) {
        this.needCallBack = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Notification.Properties }
     *     
     */
    public Notification.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Notification.Properties }
     *     
     */
    public void setProperties(Notification.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the resources property.
     * 
     * @return
     *     possible object is
     *     {@link Resources }
     *     
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * Sets the value of the resources property.
     * 
     * @param value
     *     allowed object is
     *     {@link Resources }
     *     
     */
    public void setResources(Resources value) {
        this.resources = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="property" type="{http://notification-v2.egov.bee.kz/}Property" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "property"
    })
    public static class Properties {

        @XmlElement(nillable = true)
        protected List<Property> property;

        /**
         * Gets the value of the property property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the property property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Property }
         * 
         * 
         */
        public List<Property> getProperty() {
            if (property == null) {
                property = new ArrayList<Property>();
            }
            return this.property;
        }

    }

}
