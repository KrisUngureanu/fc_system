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
import javax.xml.bind.annotation.XmlType;


/**
 * ���, ����������� �������� � ���������� ���������� ����
 * 
 * <p>Java class for PersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MiddleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType"/>
 *         &lt;element name="CitizenShipType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Citizenship" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType" minOccurs="0"/>
 *         &lt;element name="TaxNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonType", propOrder = {
    "iin",
    "lastName",
    "firstName",
    "middleName",
    "country",
    "citizenShipType",
    "citizenship",
    "taxNumber"
})
public class PersonType {

    @XmlElement(name = "IIN")
    protected String iin;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "MiddleName")
    protected String middleName;
    @XmlElement(name = "Country", required = true)
    protected DictionaryType country;
    @XmlElement(name = "CitizenShipType", required = true)
    protected String citizenShipType;
    @XmlElement(name = "Citizenship")
    protected DictionaryType citizenship;
    @XmlElement(name = "TaxNumber")
    protected String taxNumber;

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIIN() {
        return iin;
    }

    /**
     * Sets the value of the iin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIIN(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
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
     * Gets the value of the citizenShipType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenShipType() {
        return citizenShipType;
    }

    /**
     * Sets the value of the citizenShipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenShipType(String value) {
        this.citizenShipType = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setCitizenship(DictionaryType value) {
        this.citizenship = value;
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

}
