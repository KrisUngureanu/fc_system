//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.24 at 03:16:44 PM ALMT 
//


package kz.tamur.fc.gbdrn.report303;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Тип, описывающий бизнес данные запроса
 * 
 * <p>Java class for RegistredRightsOrganizationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegistredRightsOrganizationRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Owner" type="{http://types.303.reports.egov.rn3.tamur.kz}OrganizationRequisitesType"/>
 *         &lt;element name="OwnerShort" type="{http://types.303.reports.egov.rn3.tamur.kz}OwnerShort"/>
 *         &lt;element name="ObjectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ObjectLocation" type="{http://types.303.reports.egov.rn3.tamur.kz}RealEstateObjectAddressType"/>
 *         &lt;element name="CadastralNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Language" type="{http://types.303.reports.egov.rn3.tamur.kz}Language"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistredRightsOrganizationRequest", propOrder = {
    "owner",
    "ownerShort",
    "objectType",
    "objectLocation",
    "cadastralNumber",
    "language"
})
public class RegistredRightsOrganizationRequest {

    @XmlElement(name = "Owner", required = true, nillable = true)
    protected OrganizationRequisitesType owner;
    @XmlElement(name = "OwnerShort", required = true, nillable = true)
    protected OwnerShort ownerShort;
    @XmlElement(name = "ObjectType", required = true, nillable = true)
    protected String objectType;
    @XmlElement(name = "ObjectLocation", required = true, nillable = true)
    protected RealEstateObjectAddressType objectLocation;
    @XmlElement(name = "CadastralNumber", required = true, nillable = true)
    protected String cadastralNumber;
    @XmlElement(name = "Language", required = true)
    protected Language language;

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationRequisitesType }
     *     
     */
    public OrganizationRequisitesType getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationRequisitesType }
     *     
     */
    public void setOwner(OrganizationRequisitesType value) {
        this.owner = value;
    }

    /**
     * Gets the value of the ownerShort property.
     * 
     * @return
     *     possible object is
     *     {@link OwnerShort }
     *     
     */
    public OwnerShort getOwnerShort() {
        return ownerShort;
    }

    /**
     * Sets the value of the ownerShort property.
     * 
     * @param value
     *     allowed object is
     *     {@link OwnerShort }
     *     
     */
    public void setOwnerShort(OwnerShort value) {
        this.ownerShort = value;
    }

    /**
     * Gets the value of the objectType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the value of the objectType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectType(String value) {
        this.objectType = value;
    }

    /**
     * Gets the value of the objectLocation property.
     * 
     * @return
     *     possible object is
     *     {@link RealEstateObjectAddressType }
     *     
     */
    public RealEstateObjectAddressType getObjectLocation() {
        return objectLocation;
    }

    /**
     * Sets the value of the objectLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link RealEstateObjectAddressType }
     *     
     */
    public void setObjectLocation(RealEstateObjectAddressType value) {
        this.objectLocation = value;
    }

    /**
     * Gets the value of the cadastralNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCadastralNumber() {
        return cadastralNumber;
    }

    /**
     * Sets the value of the cadastralNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCadastralNumber(String value) {
        this.cadastralNumber = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link Language }
     *     
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link Language }
     *     
     */
    public void setLanguage(Language value) {
        this.language = value;
    }

}