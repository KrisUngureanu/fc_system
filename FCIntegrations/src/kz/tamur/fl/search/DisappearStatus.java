
package kz.tamur.fl.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DisappearStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DisappearStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="disappear" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="disappearDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="disappearEndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="disappearNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DisappearStatus", namespace = "http://person.persistence.interactive.nat", propOrder = {
    "disappear",
    "disappearDate",
    "disappearEndDate",
    "disappearNumber",
    "gpTerritorial"
})
public class DisappearStatus {

    protected boolean disappear;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar disappearDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar disappearEndDate;
    @XmlElement(required = true)
    protected String disappearNumber;
    @XmlElement(required = true)
    protected GpTerritorial gpTerritorial;

    /**
     * Gets the value of the disappear property.
     * 
     */
    public boolean isDisappear() {
        return disappear;
    }

    /**
     * Sets the value of the disappear property.
     * 
     */
    public void setDisappear(boolean value) {
        this.disappear = value;
    }

    /**
     * Gets the value of the disappearDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisappearDate() {
        return disappearDate;
    }

    /**
     * Sets the value of the disappearDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisappearDate(XMLGregorianCalendar value) {
        this.disappearDate = value;
    }

    /**
     * Gets the value of the disappearEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisappearEndDate() {
        return disappearEndDate;
    }

    /**
     * Sets the value of the disappearEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisappearEndDate(XMLGregorianCalendar value) {
        this.disappearEndDate = value;
    }

    /**
     * Gets the value of the disappearNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisappearNumber() {
        return disappearNumber;
    }

    /**
     * Sets the value of the disappearNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisappearNumber(String value) {
        this.disappearNumber = value;
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