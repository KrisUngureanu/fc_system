//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.23 at 04:01:18 PM ALMT 
//


package kz.tamur.fc.el.licensesearch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfEgovCabinetLicense complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfEgovCabinetLicense">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EgovCabinetLicense" type="{http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService}EgovCabinetLicense" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfEgovCabinetLicense", propOrder = {
    "egovCabinetLicense"
})
public class ArrayOfEgovCabinetLicense {

    @XmlElement(name = "EgovCabinetLicense", nillable = true)
    protected List<EgovCabinetLicense> egovCabinetLicense;

    /**
     * Gets the value of the egovCabinetLicense property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the egovCabinetLicense property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEgovCabinetLicense().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EgovCabinetLicense }
     * 
     * 
     */
    public List<EgovCabinetLicense> getEgovCabinetLicense() {
        if (egovCabinetLicense == null) {
            egovCabinetLicense = new ArrayList<EgovCabinetLicense>();
        }
        return this.egovCabinetLicense;
    }

}