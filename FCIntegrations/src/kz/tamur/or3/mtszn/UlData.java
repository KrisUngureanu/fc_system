
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ulData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ulData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateClose" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateLicvidation" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="licvidationCause" type="{http://services.sync.mtszn/}name" minOccurs="0"/>
 *         &lt;element name="licvidationDocNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ulname" type="{http://services.sync.mtszn/}name" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ulData", propOrder = {
    "bin",
    "dateClose",
    "dateLicvidation",
    "licvidationCause",
    "licvidationDocNumber",
    "regDate",
    "ulname"
})
public class UlData {

    protected String bin;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateClose;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateLicvidation;
    protected Name licvidationCause;
    protected String licvidationDocNumber;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar regDate;
    protected Name ulname;

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBin() {
        return bin;
    }

    /**
     * Sets the value of the bin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBin(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the dateClose property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateClose() {
        return dateClose;
    }

    /**
     * Sets the value of the dateClose property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateClose(XMLGregorianCalendar value) {
        this.dateClose = value;
    }

    /**
     * Gets the value of the dateLicvidation property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLicvidation() {
        return dateLicvidation;
    }

    /**
     * Sets the value of the dateLicvidation property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLicvidation(XMLGregorianCalendar value) {
        this.dateLicvidation = value;
    }

    /**
     * Gets the value of the licvidationCause property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getLicvidationCause() {
        return licvidationCause;
    }

    /**
     * Sets the value of the licvidationCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setLicvidationCause(Name value) {
        this.licvidationCause = value;
    }

    /**
     * Gets the value of the licvidationDocNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicvidationDocNumber() {
        return licvidationDocNumber;
    }

    /**
     * Sets the value of the licvidationDocNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicvidationDocNumber(String value) {
        this.licvidationDocNumber = value;
    }

    /**
     * Gets the value of the regDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegDate() {
        return regDate;
    }

    /**
     * Sets the value of the regDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegDate(XMLGregorianCalendar value) {
        this.regDate = value;
    }

    /**
     * Gets the value of the ulname property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getUlname() {
        return ulname;
    }

    /**
     * Sets the value of the ulname property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setUlname(Name value) {
        this.ulname = value;
    }

}
