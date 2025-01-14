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
 * Формат служебных данных
 * 
 * <p>Java class for BussinesDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BussinesDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RegistredRightsOrganizationRequest" type="{http://types.303.reports.egov.rn3.tamur.kz}RegistredRightsOrganizationRequest" minOccurs="0" form="unqualified"/>
 *         &lt;element name="RegisteredRightsEvidenceOrganization" type="{http://types.303.reports.egov.rn3.tamur.kz}GbdrnRegisteredRightsEvidenceOrganization" minOccurs="0" form="unqualified"/>
 *         &lt;element name="RegisteredRightsEvidenceOrganizationKz" type="{http://types.303.reports.egov.rn3.tamur.kz}GbdrnRegisteredRightsEvidenceOrganization" minOccurs="0" form="unqualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BussinesDataType", propOrder = {
    "registredRightsOrganizationRequest",
    "registeredRightsEvidenceOrganization",
    "registeredRightsEvidenceOrganizationKz"
})
public class BussinesDataType {

    @XmlElement(name = "RegistredRightsOrganizationRequest", namespace = "")
    protected RegistredRightsOrganizationRequest registredRightsOrganizationRequest;
    @XmlElement(name = "RegisteredRightsEvidenceOrganization", namespace = "")
    protected GbdrnRegisteredRightsEvidenceOrganization registeredRightsEvidenceOrganization;
    @XmlElement(name = "RegisteredRightsEvidenceOrganizationKz", namespace = "")
    protected GbdrnRegisteredRightsEvidenceOrganization registeredRightsEvidenceOrganizationKz;

    /**
     * Gets the value of the registredRightsOrganizationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RegistredRightsOrganizationRequest }
     *     
     */
    public RegistredRightsOrganizationRequest getRegistredRightsOrganizationRequest() {
        return registredRightsOrganizationRequest;
    }

    /**
     * Sets the value of the registredRightsOrganizationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistredRightsOrganizationRequest }
     *     
     */
    public void setRegistredRightsOrganizationRequest(RegistredRightsOrganizationRequest value) {
        this.registredRightsOrganizationRequest = value;
    }

    /**
     * Gets the value of the registeredRightsEvidenceOrganization property.
     * 
     * @return
     *     possible object is
     *     {@link GbdrnRegisteredRightsEvidenceOrganization }
     *     
     */
    public GbdrnRegisteredRightsEvidenceOrganization getRegisteredRightsEvidenceOrganization() {
        return registeredRightsEvidenceOrganization;
    }

    /**
     * Sets the value of the registeredRightsEvidenceOrganization property.
     * 
     * @param value
     *     allowed object is
     *     {@link GbdrnRegisteredRightsEvidenceOrganization }
     *     
     */
    public void setRegisteredRightsEvidenceOrganization(GbdrnRegisteredRightsEvidenceOrganization value) {
        this.registeredRightsEvidenceOrganization = value;
    }

    /**
     * Gets the value of the registeredRightsEvidenceOrganizationKz property.
     * 
     * @return
     *     possible object is
     *     {@link GbdrnRegisteredRightsEvidenceOrganization }
     *     
     */
    public GbdrnRegisteredRightsEvidenceOrganization getRegisteredRightsEvidenceOrganizationKz() {
        return registeredRightsEvidenceOrganizationKz;
    }

    /**
     * Sets the value of the registeredRightsEvidenceOrganizationKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link GbdrnRegisteredRightsEvidenceOrganization }
     *     
     */
    public void setRegisteredRightsEvidenceOrganizationKz(GbdrnRegisteredRightsEvidenceOrganization value) {
        this.registeredRightsEvidenceOrganizationKz = value;
    }

}
