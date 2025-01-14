//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.10 at 12:17:42 PM ALMT 
//


package kz.tamur.fc.gbdfl.universal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ����� �������� (�����������)
 * 
 * <p>Java class for RegAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegAddress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://dictionaries.persistence.interactive.nat}Country" minOccurs="0"/>
 *         &lt;element name="district" type="{http://dictionaries.persistence.interactive.nat}District" minOccurs="0"/>
 *         &lt;element name="region" type="{http://dictionaries.persistence.interactive.nat}Region" minOccurs="0"/>
 *         &lt;element name="foreignData" type="{http://person.persistence.interactive.nat}ForeignData" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="building" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="corpus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="beginDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="status" type="{http://dictionaries.persistence.interactive.nat}AddressStatus" minOccurs="0"/>
 *         &lt;element name="invalidity" type="{http://dictionaries.persistence.interactive.nat}AddressInvalidity" minOccurs="0"/>
 *         &lt;element name="arCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegAddress", namespace = "http://person.persistence.interactive.nat", propOrder = {
    "country",
    "district",
    "region",
    "foreignData",
    "city",
    "street",
    "building",
    "corpus",
    "flat",
    "beginDate",
    "endDate",
    "status",
    "invalidity",
    "arCode"
})
public class RegAddress {

    protected Country country;
    protected District district;
    protected Region region;
    protected ForeignData foreignData;
    protected String city;
    protected String street;
    protected String building;
    protected String corpus;
    protected String flat;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar beginDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    protected AddressStatus status;
    protected AddressInvalidity invalidity;
    protected String arCode;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link Country }
     *     
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link Country }
     *     
     */
    public void setCountry(Country value) {
        this.country = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link District }
     *     
     */
    public District getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link District }
     *     
     */
    public void setDistrict(District value) {
        this.district = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link Region }
     *     
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link Region }
     *     
     */
    public void setRegion(Region value) {
        this.region = value;
    }

    /**
     * Gets the value of the foreignData property.
     * 
     * @return
     *     possible object is
     *     {@link ForeignData }
     *     
     */
    public ForeignData getForeignData() {
        return foreignData;
    }

    /**
     * Sets the value of the foreignData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForeignData }
     *     
     */
    public void setForeignData(ForeignData value) {
        this.foreignData = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the building property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Sets the value of the building property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuilding(String value) {
        this.building = value;
    }

    /**
     * Gets the value of the corpus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorpus() {
        return corpus;
    }

    /**
     * Sets the value of the corpus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorpus(String value) {
        this.corpus = value;
    }

    /**
     * Gets the value of the flat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlat() {
        return flat;
    }

    /**
     * Sets the value of the flat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlat(String value) {
        this.flat = value;
    }

    /**
     * Gets the value of the beginDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBeginDate() {
        return beginDate;
    }

    /**
     * Sets the value of the beginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBeginDate(XMLGregorianCalendar value) {
        this.beginDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link AddressStatus }
     *     
     */
    public AddressStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressStatus }
     *     
     */
    public void setStatus(AddressStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the invalidity property.
     * 
     * @return
     *     possible object is
     *     {@link AddressInvalidity }
     *     
     */
    public AddressInvalidity getInvalidity() {
        return invalidity;
    }

    /**
     * Sets the value of the invalidity property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressInvalidity }
     *     
     */
    public void setInvalidity(AddressInvalidity value) {
        this.invalidity = value;
    }

    /**
     * Gets the value of the arCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArCode() {
        return arCode;
    }

    /**
     * Sets the value of the arCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArCode(String value) {
        this.arCode = value;
    }

}
