
package kz.tamur.gbdul.fl.person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.gbdul.fl.dictionaries.ExcludeReason;
import kz.tamur.gbdul.fl.dictionaries.Participant;


/**
 * Признак исключения ИИН
 * 
 * <p>Java class for PersonExcludeStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonExcludeStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="excludeReason" type="{http://dictionaries.persistence.interactive.nat}ExcludeReason"/>
 *         &lt;element name="excludeReasonDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="excludeDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="excludeParticipant" type="{http://dictionaries.persistence.interactive.nat}Participant" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonExcludeStatus", propOrder = {
    "excludeReason",
    "excludeReasonDate",
    "excludeDate",
    "excludeParticipant"
})
public class PersonExcludeStatus {

    @XmlElement(required = true)
    protected ExcludeReason excludeReason;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar excludeReasonDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar excludeDate;
    protected Participant excludeParticipant;

    /**
     * Gets the value of the excludeReason property.
     * 
     * @return
     *     possible object is
     *     {@link ExcludeReason }
     *     
     */
    public ExcludeReason getExcludeReason() {
        return excludeReason;
    }

    /**
     * Sets the value of the excludeReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcludeReason }
     *     
     */
    public void setExcludeReason(ExcludeReason value) {
        this.excludeReason = value;
    }

    /**
     * Gets the value of the excludeReasonDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExcludeReasonDate() {
        return excludeReasonDate;
    }

    /**
     * Sets the value of the excludeReasonDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExcludeReasonDate(XMLGregorianCalendar value) {
        this.excludeReasonDate = value;
    }

    /**
     * Gets the value of the excludeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExcludeDate() {
        return excludeDate;
    }

    /**
     * Sets the value of the excludeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExcludeDate(XMLGregorianCalendar value) {
        this.excludeDate = value;
    }

    /**
     * Gets the value of the excludeParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link Participant }
     *     
     */
    public Participant getExcludeParticipant() {
        return excludeParticipant;
    }

    /**
     * Sets the value of the excludeParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Participant }
     *     
     */
    public void setExcludeParticipant(Participant value) {
        this.excludeParticipant = value;
    }

}
