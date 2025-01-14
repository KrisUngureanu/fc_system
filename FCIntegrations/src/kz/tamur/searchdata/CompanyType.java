//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.18 at 10:13:58 AM ALMT 
//


package kz.tamur.searchdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * �������� ���, ����������� ����������-������
 * 
 * <p>Java class for CompanyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompanyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType"/>
 *         &lt;element name="RegistrationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TaxNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegistrationLastDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Activity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompanyType", propOrder = {
    "bin",
    "country",
    "registrationNumber",
    "taxNumber",
    "registrationLastDate",
    "fullName",
    "activity"
})
public class CompanyType {

    @XmlElement(name = "BIN")
    protected String bin;
    @XmlElement(name = "Country", required = true)
    protected DictionaryType country;
    @XmlElement(name = "RegistrationNumber")
    protected String registrationNumber;
    @XmlElement(name = "TaxNumber")
    protected String taxNumber;
    @XmlElement(name = "RegistrationLastDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar registrationLastDate;
    @XmlElement(name = "FullName", required = true)
    protected String fullName;
    @XmlElement(name = "Activity")
    protected String activity;

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIN() {
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
    public void setBIN(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setCountry(DictionaryType value) {
        this.country = value;
    }

    /**
     * Gets the value of the registrationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /**
     * Sets the value of the registrationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistrationNumber(String value) {
        this.registrationNumber = value;
    }

    /**
     * Gets the value of the taxNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * Sets the value of the taxNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxNumber(String value) {
        this.taxNumber = value;
    }

    /**
     * Gets the value of the registrationLastDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegistrationLastDate() {
        return registrationLastDate;
    }

    /**
     * Sets the value of the registrationLastDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegistrationLastDate(XMLGregorianCalendar value) {
        this.registrationLastDate = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the activity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivity() {
        return activity;
    }

    /**
     * Sets the value of the activity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivity(String value) {
        this.activity = value;
    }

}
