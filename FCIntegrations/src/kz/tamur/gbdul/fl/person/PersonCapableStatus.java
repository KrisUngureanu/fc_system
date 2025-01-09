
package kz.tamur.gbdul.fl.person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.gbdul.fl.dictionaries.CapableStatus;
import kz.tamur.gbdul.fl.dictionaries.Court;


/**
 * Статус дееспособности персоны
 * 
 * <p>Java class for PersonCapableStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonCapableStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="capableStatus" type="{http://dictionaries.persistence.interactive.nat}CapableStatus"/>
 *         &lt;element name="capableDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="capableEndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="capableNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="court" type="{http://dictionaries.persistence.interactive.nat}Court"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonCapableStatus", propOrder = {
    "capableStatus",
    "capableDate",
    "capableEndDate",
    "capableNumber",
    "court"
})
public class PersonCapableStatus {

    @XmlElement(required = true)
    protected CapableStatus capableStatus;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar capableDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar capableEndDate;
    @XmlElement(required = true)
    protected String capableNumber;
    @XmlElement(required = true)
    protected Court court;

    /**
     * Gets the value of the capableStatus property.
     * 
     * @return
     *     possible object is
     *     {@link CapableStatus }
     *     
     */
    public CapableStatus getCapableStatus() {
        return capableStatus;
    }

    /**
     * Sets the value of the capableStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapableStatus }
     *     
     */
    public void setCapableStatus(CapableStatus value) {
        this.capableStatus = value;
    }

    /**
     * Gets the value of the capableDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCapableDate() {
        return capableDate;
    }

    /**
     * Sets the value of the capableDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCapableDate(XMLGregorianCalendar value) {
        this.capableDate = value;
    }

    /**
     * Gets the value of the capableEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCapableEndDate() {
        return capableEndDate;
    }

    /**
     * Sets the value of the capableEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCapableEndDate(XMLGregorianCalendar value) {
        this.capableEndDate = value;
    }

    /**
     * Gets the value of the capableNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCapableNumber() {
        return capableNumber;
    }

    /**
     * Sets the value of the capableNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCapableNumber(String value) {
        this.capableNumber = value;
    }

    /**
     * Gets the value of the court property.
     * 
     * @return
     *     possible object is
     *     {@link Court }
     *     
     */
    public Court getCourt() {
        return court;
    }

    /**
     * Sets the value of the court property.
     * 
     * @param value
     *     allowed object is
     *     {@link Court }
     *     
     */
    public void setCourt(Court value) {
        this.court = value;
    }

}
