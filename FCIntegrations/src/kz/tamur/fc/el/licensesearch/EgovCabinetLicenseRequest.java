//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.23 at 04:01:18 PM ALMT 
//


package kz.tamur.fc.el.licensesearch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EgovCabinetLicenseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EgovCabinetLicenseRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService}RequestPageBase">
 *       &lt;sequence>
 *         &lt;element name="IinBin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EgovCabinetLicenseRequest", propOrder = {
    "iinBin"
})
public class EgovCabinetLicenseRequest
    extends RequestPageBase
{

    @XmlElement(name = "IinBin")
    protected String iinBin;

    /**
     * Gets the value of the iinBin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIinBin() {
        return iinBin;
    }

    /**
     * Sets the value of the iinBin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIinBin(String value) {
        this.iinBin = value;
    }

}
