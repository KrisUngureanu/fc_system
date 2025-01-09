
package kz.tamur.or3.mtszn.natperson;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.dict.Country;
import kz.tamur.or3.mtszn.dict.District;
import kz.tamur.or3.mtszn.dict.Region;


/**
 * <p>Java class for BirthPlace complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BirthPlace">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://dictionaries.persistence.interactive.nat}Country" minOccurs="0"/>
 *         &lt;element name="district" type="{http://dictionaries.persistence.interactive.nat}District" minOccurs="0"/>
 *         &lt;element name="region" type="{http://dictionaries.persistence.interactive.nat}Region" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="foreignData" type="{http://person.persistence.interactive.nat}ForeignData" minOccurs="0"/>
 *         &lt;element name="birthTeCodeAR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BirthPlace", propOrder = {
    "country",
    "district",
    "region",
    "city",
    "foreignData",
    "birthTeCodeAR"
})
public class BirthPlace {

    protected Country country;
    protected District district;
    protected Region region;
    protected String city;
    protected ForeignData foreignData;
    protected String birthTeCodeAR;

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
     * Gets the value of the birthTeCodeAR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthTeCodeAR() {
        return birthTeCodeAR;
    }

    /**
     * Sets the value of the birthTeCodeAR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthTeCodeAR(String value) {
        this.birthTeCodeAR = value;
    }

}
