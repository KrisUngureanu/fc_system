
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for flatPassport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="flatPassport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="balconyCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="buildingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="effectiveSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flour" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inventoryNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="livingSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfStoreys" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registerNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalBalconySquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unlivingSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wallsMaterial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "flatPassport", propOrder = {
    "balconyCount",
    "buildingDate",
    "effectiveSquare",
    "flour",
    "inventoryNumber",
    "livingSquare",
    "numberOfStoreys",
    "registerNumber",
    "roomCount",
    "serialNumber",
    "totalBalconySquare",
    "totalSquare",
    "unlivingSquare",
    "wallsMaterial"
})
public class FlatPassport {

    protected String balconyCount;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar buildingDate;
    protected String effectiveSquare;
    protected String flour;
    protected String inventoryNumber;
    protected String livingSquare;
    protected String numberOfStoreys;
    protected String registerNumber;
    protected String roomCount;
    protected String serialNumber;
    protected String totalBalconySquare;
    protected String totalSquare;
    protected String unlivingSquare;
    protected String wallsMaterial;

    /**
     * Gets the value of the balconyCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBalconyCount() {
        return balconyCount;
    }

    /**
     * Sets the value of the balconyCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBalconyCount(String value) {
        this.balconyCount = value;
    }

    /**
     * Gets the value of the buildingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBuildingDate() {
        return buildingDate;
    }

    /**
     * Sets the value of the buildingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBuildingDate(XMLGregorianCalendar value) {
        this.buildingDate = value;
    }

    /**
     * Gets the value of the effectiveSquare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEffectiveSquare() {
        return effectiveSquare;
    }

    /**
     * Sets the value of the effectiveSquare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEffectiveSquare(String value) {
        this.effectiveSquare = value;
    }

    /**
     * Gets the value of the flour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlour() {
        return flour;
    }

    /**
     * Sets the value of the flour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlour(String value) {
        this.flour = value;
    }

    /**
     * Gets the value of the inventoryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInventoryNumber() {
        return inventoryNumber;
    }

    /**
     * Sets the value of the inventoryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInventoryNumber(String value) {
        this.inventoryNumber = value;
    }

    /**
     * Gets the value of the livingSquare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLivingSquare() {
        return livingSquare;
    }

    /**
     * Sets the value of the livingSquare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLivingSquare(String value) {
        this.livingSquare = value;
    }

    /**
     * Gets the value of the numberOfStoreys property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfStoreys() {
        return numberOfStoreys;
    }

    /**
     * Sets the value of the numberOfStoreys property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfStoreys(String value) {
        this.numberOfStoreys = value;
    }

    /**
     * Gets the value of the registerNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegisterNumber() {
        return registerNumber;
    }

    /**
     * Sets the value of the registerNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegisterNumber(String value) {
        this.registerNumber = value;
    }

    /**
     * Gets the value of the roomCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomCount() {
        return roomCount;
    }

    /**
     * Sets the value of the roomCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomCount(String value) {
        this.roomCount = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the totalBalconySquare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalBalconySquare() {
        return totalBalconySquare;
    }

    /**
     * Sets the value of the totalBalconySquare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalBalconySquare(String value) {
        this.totalBalconySquare = value;
    }

    /**
     * Gets the value of the totalSquare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSquare() {
        return totalSquare;
    }

    /**
     * Sets the value of the totalSquare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSquare(String value) {
        this.totalSquare = value;
    }

    /**
     * Gets the value of the unlivingSquare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnlivingSquare() {
        return unlivingSquare;
    }

    /**
     * Sets the value of the unlivingSquare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnlivingSquare(String value) {
        this.unlivingSquare = value;
    }

    /**
     * Gets the value of the wallsMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWallsMaterial() {
        return wallsMaterial;
    }

    /**
     * Sets the value of the wallsMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWallsMaterial(String value) {
        this.wallsMaterial = value;
    }

}
