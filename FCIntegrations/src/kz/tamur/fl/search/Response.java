
package kz.tamur.fl.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inquiryGUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateMessage" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="messageResult" type="{http://dictionaries.persistence.interactive.nat}MessageResult"/>
 *         &lt;element name="sender" type="{http://dictionaries.persistence.interactive.nat}Participant"/>
 *         &lt;element name="receiver" type="{http://dictionaries.persistence.interactive.nat}Participant"/>
 *         &lt;element name="persons" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="person" type="{http://person.persistence.interactive.nat}Person" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response", namespace = "http://message.persistence.interactive.nat", propOrder = {
    "guid",
    "inquiryGUID",
    "dateMessage",
    "messageResult",
    "sender",
    "receiver",
    "persons",
    "version"
})
public class Response {

    @XmlElement(name = "GUID", required = true)
    protected String guid;
    @XmlElement(required = true)
    protected String inquiryGUID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateMessage;
    @XmlElement(required = true)
    protected MessageResult messageResult;
    @XmlElement(required = true)
    protected Participant sender;
    @XmlElement(required = true)
    protected Participant receiver;
    protected Response.Persons persons;
    protected Integer version;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGUID() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGUID(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the inquiryGUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryGUID() {
        return inquiryGUID;
    }

    /**
     * Sets the value of the inquiryGUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryGUID(String value) {
        this.inquiryGUID = value;
    }

    /**
     * Gets the value of the dateMessage property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateMessage() {
        return dateMessage;
    }

    /**
     * Sets the value of the dateMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateMessage(XMLGregorianCalendar value) {
        this.dateMessage = value;
    }

    /**
     * Gets the value of the messageResult property.
     * 
     * @return
     *     possible object is
     *     {@link MessageResult }
     *     
     */
    public MessageResult getMessageResult() {
        return messageResult;
    }

    /**
     * Sets the value of the messageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageResult }
     *     
     */
    public void setMessageResult(MessageResult value) {
        this.messageResult = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link Participant }
     *     
     */
    public Participant getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Participant }
     *     
     */
    public void setSender(Participant value) {
        this.sender = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link Participant }
     *     
     */
    public Participant getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Participant }
     *     
     */
    public void setReceiver(Participant value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the persons property.
     * 
     * @return
     *     possible object is
     *     {@link Response.Persons }
     *     
     */
    public Response.Persons getPersons() {
        return persons;
    }

    /**
     * Sets the value of the persons property.
     * 
     * @param value
     *     allowed object is
     *     {@link Response.Persons }
     *     
     */
    public void setPersons(Response.Persons value) {
        this.persons = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersion(Integer value) {
        this.version = value;
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
     *         &lt;element name="person" type="{http://person.persistence.interactive.nat}Person" maxOccurs="unbounded" minOccurs="0"/>
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
        "person"
    })
    public static class Persons {

        protected List<Person> person;

        /**
         * Gets the value of the person property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the person property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPerson().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Person }
         * 
         * 
         */
        public List<Person> getPerson() {
            if (person == null) {
                person = new ArrayList<Person>();
            }
            return this.person;
        }

    }

}
