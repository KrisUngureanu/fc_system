
package kz.tamur.gbdul.fl.person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.gbdul.fl.dictionaries.GpTerritorial;


/**
 * Статус персоны "Пропавший без вести"
 * 
 * <p>Java class for MissingStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MissingStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="missing" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="missingDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="missingEndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="missingNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gpTerritorial" type="{http://dictionaries.persistence.interactive.nat}GpTerritorial"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MissingStatus", propOrder = {
    "missing",
    "missingDate",
    "missingEndDate",
    "missingNumber",
    "gpTerritorial"
})
public class MissingStatus {

    protected boolean missing;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar missingDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar missingEndDate;
    @XmlElement(required = true)
    protected String missingNumber;
    @XmlElement(required = true)
    protected GpTerritorial gpTerritorial;

    /**
     * Gets the value of the missing property.
     * 
     */
    public boolean isMissing() {
        return missing;
    }

    /**
     * Sets the value of the missing property.
     * 
     */
    public void setMissing(boolean value) {
        this.missing = value;
    }

    /**
     * Gets the value of the missingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMissingDate() {
        return missingDate;
    }

    /**
     * Sets the value of the missingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMissingDate(XMLGregorianCalendar value) {
        this.missingDate = value;
    }

    /**
     * Gets the value of the missingEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMissingEndDate() {
        return missingEndDate;
    }

    /**
     * Sets the value of the missingEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMissingEndDate(XMLGregorianCalendar value) {
        this.missingEndDate = value;
    }

    /**
     * Gets the value of the missingNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMissingNumber() {
        return missingNumber;
    }

    /**
     * Sets the value of the missingNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMissingNumber(String value) {
        this.missingNumber = value;
    }

    /**
     * Gets the value of the gpTerritorial property.
     * 
     * @return
     *     possible object is
     *     {@link GpTerritorial }
     *     
     */
    public GpTerritorial getGpTerritorial() {
        return gpTerritorial;
    }

    /**
     * Sets the value of the gpTerritorial property.
     * 
     * @param value
     *     allowed object is
     *     {@link GpTerritorial }
     *     
     */
    public void setGpTerritorial(GpTerritorial value) {
        this.gpTerritorial = value;
    }

}
