
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06580AdditionalData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06580AdditionalData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activityCode" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="addIin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assistanceType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="category" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="gcvp_dep_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="region" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="sikId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06580AdditionalData", propOrder = {
    "activityCode",
    "addIin",
    "assistanceType",
    "category",
    "gcvpDepId",
    "region",
    "sikId"
})
public class P06580AdditionalData {

    protected AdditionalName activityCode;
    protected String addIin;
    protected AdditionalName assistanceType;
    protected AdditionalName category;
    @XmlElement(name = "gcvp_dep_id")
    protected String gcvpDepId;
    protected AdditionalName region;
    protected String sikId;

    /**
     * Gets the value of the activityCode property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getActivityCode() {
        return activityCode;
    }

    /**
     * Sets the value of the activityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setActivityCode(AdditionalName value) {
        this.activityCode = value;
    }

    /**
     * Gets the value of the addIin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddIin() {
        return addIin;
    }

    /**
     * Sets the value of the addIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddIin(String value) {
        this.addIin = value;
    }

    /**
     * Gets the value of the assistanceType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getAssistanceType() {
        return assistanceType;
    }

    /**
     * Sets the value of the assistanceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setAssistanceType(AdditionalName value) {
        this.assistanceType = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setCategory(AdditionalName value) {
        this.category = value;
    }

    /**
     * Gets the value of the gcvpDepId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGcvpDepId() {
        return gcvpDepId;
    }

    /**
     * Sets the value of the gcvpDepId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGcvpDepId(String value) {
        this.gcvpDepId = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setRegion(AdditionalName value) {
        this.region = value;
    }

    /**
     * Gets the value of the sikId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSikId() {
        return sikId;
    }

    /**
     * Sets the value of the sikId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSikId(String value) {
        this.sikId = value;
    }

}
