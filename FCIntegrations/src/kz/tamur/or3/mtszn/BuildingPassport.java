
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for buildingPassport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="buildingPassport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="buildingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="buildingProperties" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flourCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="livingSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="purpose" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="square" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unlivingSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="walls" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "buildingPassport", propOrder = {
    "buildingDate",
    "buildingProperties",
    "flourCount",
    "livingSquare",
    "purpose",
    "square",
    "totalSquare",
    "unlivingSquare",
    "walls"
})
public class BuildingPassport {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar buildingDate;
    protected String buildingProperties;
    protected String flourCount;
    protected String livingSquare;
    protected AdditionalName purpose;
    protected String square;
    protected String totalSquare;
    protected String unlivingSquare;
    protected String walls;

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
     * Gets the value of the buildingProperties property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingProperties() {
        return buildingProperties;
    }

    /**
     * Sets the value of the buildingProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingProperties(String value) {
        this.buildingProperties = value;
    }

    /**
     * Gets the value of the flourCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlourCount() {
        return flourCount;
    }

    /**
     * Sets the value of the flourCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlourCount(String value) {
        this.flourCount = value;
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
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setPurpose(AdditionalName value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the square property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSquare() {
        return square;
    }

    /**
     * Sets the value of the square property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSquare(String value) {
        this.square = value;
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
     * Gets the value of the walls property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWalls() {
        return walls;
    }

    /**
     * Sets the value of the walls property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWalls(String value) {
        this.walls = value;
    }

}
