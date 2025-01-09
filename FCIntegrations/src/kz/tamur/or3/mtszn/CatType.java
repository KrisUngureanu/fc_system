
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="occupType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="stateType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="unempType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="workCondType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catType", propOrder = {
    "accountType",
    "occupType",
    "stateType",
    "unempType",
    "workCondType"
})
public class CatType {

    protected AdditionalName accountType;
    protected AdditionalName occupType;
    protected AdditionalName stateType;
    protected AdditionalName unempType;
    protected AdditionalName workCondType;

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setAccountType(AdditionalName value) {
        this.accountType = value;
    }

    /**
     * Gets the value of the occupType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getOccupType() {
        return occupType;
    }

    /**
     * Sets the value of the occupType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setOccupType(AdditionalName value) {
        this.occupType = value;
    }

    /**
     * Gets the value of the stateType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getStateType() {
        return stateType;
    }

    /**
     * Sets the value of the stateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setStateType(AdditionalName value) {
        this.stateType = value;
    }

    /**
     * Gets the value of the unempType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getUnempType() {
        return unempType;
    }

    /**
     * Sets the value of the unempType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setUnempType(AdditionalName value) {
        this.unempType = value;
    }

    /**
     * Gets the value of the workCondType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getWorkCondType() {
        return workCondType;
    }

    /**
     * Sets the value of the workCondType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setWorkCondType(AdditionalName value) {
        this.workCondType = value;
    }

}
