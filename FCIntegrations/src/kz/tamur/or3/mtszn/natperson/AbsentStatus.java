
package kz.tamur.or3.mtszn.natperson;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.or3.mtszn.dict.Court;


/**
 * <p>Java class for AbsentStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbsentStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="absentDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="absentEndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="absentNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "AbsentStatus", propOrder = {
    "absentDate",
    "absentEndDate",
    "absentNumber",
    "court"
})
public class AbsentStatus {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar absentDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar absentEndDate;
    @XmlElement(required = true)
    protected String absentNumber;
    @XmlElement(required = true)
    protected Court court;

    /**
     * Gets the value of the absentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAbsentDate() {
        return absentDate;
    }

    /**
     * Sets the value of the absentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAbsentDate(XMLGregorianCalendar value) {
        this.absentDate = value;
    }

    /**
     * Gets the value of the absentEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAbsentEndDate() {
        return absentEndDate;
    }

    /**
     * Sets the value of the absentEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAbsentEndDate(XMLGregorianCalendar value) {
        this.absentEndDate = value;
    }

    /**
     * Gets the value of the absentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbsentNumber() {
        return absentNumber;
    }

    /**
     * Sets the value of the absentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbsentNumber(String value) {
        this.absentNumber = value;
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
