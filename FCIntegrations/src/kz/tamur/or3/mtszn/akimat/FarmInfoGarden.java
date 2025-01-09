
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FarmInfoGarden complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarmInfoGarden">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProductNameKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductNameRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductCount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductCountUnitKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductCountUnitRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ModificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="reasonRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reasonKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gardenCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarmInfoGarden", propOrder = {
    "productNameKz",
    "productNameRu",
    "productCount",
    "productCountUnitKz",
    "productCountUnitRu",
    "modificationDate",
    "reasonRu",
    "reasonKz",
    "gardenCode"
})
public class FarmInfoGarden {

    @XmlElement(name = "ProductNameKz", required = true, nillable = true)
    protected String productNameKz;
    @XmlElement(name = "ProductNameRu", required = true, nillable = true)
    protected String productNameRu;
    @XmlElement(name = "ProductCount", required = true, nillable = true)
    protected String productCount;
    @XmlElement(name = "ProductCountUnitKz", required = true, nillable = true)
    protected String productCountUnitKz;
    @XmlElement(name = "ProductCountUnitRu", required = true, nillable = true)
    protected String productCountUnitRu;
    @XmlElement(name = "ModificationDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modificationDate;
    @XmlElement(required = true, nillable = true)
    protected String reasonRu;
    @XmlElement(required = true, nillable = true)
    protected String reasonKz;
    @XmlElement(required = true, nillable = true)
    protected String gardenCode;

    /**
     * Gets the value of the productNameKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductNameKz() {
        return productNameKz;
    }

    /**
     * Sets the value of the productNameKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductNameKz(String value) {
        this.productNameKz = value;
    }

    /**
     * Gets the value of the productNameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductNameRu() {
        return productNameRu;
    }

    /**
     * Sets the value of the productNameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductNameRu(String value) {
        this.productNameRu = value;
    }

    /**
     * Gets the value of the productCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCount() {
        return productCount;
    }

    /**
     * Sets the value of the productCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCount(String value) {
        this.productCount = value;
    }

    /**
     * Gets the value of the productCountUnitKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCountUnitKz() {
        return productCountUnitKz;
    }

    /**
     * Sets the value of the productCountUnitKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCountUnitKz(String value) {
        this.productCountUnitKz = value;
    }

    /**
     * Gets the value of the productCountUnitRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCountUnitRu() {
        return productCountUnitRu;
    }

    /**
     * Sets the value of the productCountUnitRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCountUnitRu(String value) {
        this.productCountUnitRu = value;
    }

    /**
     * Gets the value of the modificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the value of the modificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModificationDate(XMLGregorianCalendar value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the reasonRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonRu() {
        return reasonRu;
    }

    /**
     * Sets the value of the reasonRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonRu(String value) {
        this.reasonRu = value;
    }

    /**
     * Gets the value of the reasonKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonKz() {
        return reasonKz;
    }

    /**
     * Sets the value of the reasonKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonKz(String value) {
        this.reasonKz = value;
    }

    /**
     * Gets the value of the gardenCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGardenCode() {
        return gardenCode;
    }

    /**
     * Sets the value of the gardenCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGardenCode(String value) {
        this.gardenCode = value;
    }

}
