
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Справочное значение
 * 
 * <p>Java class for DictionaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DictionaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="description_ru" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="description_kz" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DictionaryType", propOrder = {
    "code",
    "descriptionRu",
    "descriptionKz"
})
public class DictionaryType {

    @XmlElement(required = true)
    protected String code;
    @XmlElement(name = "description_ru", required = true)
    protected String descriptionRu;
    @XmlElement(name = "description_kz", required = true)
    protected String descriptionKz;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the descriptionRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptionRu() {
        return descriptionRu;
    }

    /**
     * Sets the value of the descriptionRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptionRu(String value) {
        this.descriptionRu = value;
    }

    /**
     * Gets the value of the descriptionKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptionKz() {
        return descriptionKz;
    }

    /**
     * Sets the value of the descriptionKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptionKz(String value) {
        this.descriptionKz = value;
    }

}
