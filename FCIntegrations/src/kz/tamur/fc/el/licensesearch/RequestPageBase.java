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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestPageBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestPageBase">
 *   &lt;complexContent>
 *     &lt;extension base="{http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService}ARequestResponseBase">
 *       &lt;sequence>
 *         &lt;element name="PageIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PageSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestPageBase", propOrder = {
    "pageIndex",
    "pageSize"
})
@XmlSeeAlso({
    UniversalLicenseRequest.class,
    EgovCabinetLicenseRequest.class
})
public class RequestPageBase
    extends ARequestResponseBase
{

    @XmlElement(name = "PageIndex")
    protected int pageIndex;
    @XmlElement(name = "PageSize")
    protected int pageSize;

    /**
     * Gets the value of the pageIndex property.
     * 
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * Sets the value of the pageIndex property.
     * 
     */
    public void setPageIndex(int value) {
        this.pageIndex = value;
    }

    /**
     * Gets the value of the pageSize property.
     * 
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the value of the pageSize property.
     * 
     */
    public void setPageSize(int value) {
        this.pageSize = value;
    }

}
