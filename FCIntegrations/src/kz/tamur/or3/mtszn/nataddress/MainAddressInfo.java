
package kz.tamur.or3.mtszn.nataddress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.dict.Country;
import kz.tamur.or3.mtszn.dict.District;
import kz.tamur.or3.mtszn.dict.Region;


/**
 * <p>Java class for MainAddressInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MainAddressInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://dictionaries.persistence.interactive.nat}Country" minOccurs="0"/>
 *         &lt;element name="district" type="{http://dictionaries.persistence.interactive.nat}District" minOccurs="0"/>
 *         &lt;element name="region" type="{http://dictionaries.persistence.interactive.nat}Region" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="txtDistrict" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="txtRegion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "MainAddressInfo", propOrder = {
    "country",
    "district",
    "region",
    "city",
    "txtDistrict",
    "txtRegion",
    "arCode"
})
public class MainAddressInfo {

    protected Country country;
    protected District district;
    protected Region region;
    protected String city;
    protected String txtDistrict;
    protected String txtRegion;
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
     * Gets the value of the txtDistrict property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxtDistrict() {
        return txtDistrict;
    }

    /**
     * Sets the value of the txtDistrict property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxtDistrict(String value) {
        this.txtDistrict = value;
    }

    /**
     * Gets the value of the txtRegion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxtRegion() {
        return txtRegion;
    }

    /**
     * Sets the value of the txtRegion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxtRegion(String value) {
        this.txtRegion = value;
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
