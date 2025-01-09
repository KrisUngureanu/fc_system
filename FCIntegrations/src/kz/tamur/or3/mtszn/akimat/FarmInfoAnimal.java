
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FarmInfoAnimal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarmInfoAnimal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AnimalNameKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AnimalNameRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AnimalCount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AnimalCountUnitKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AnimalCountUnitRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ModificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="reasonRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reasonKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="animalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarmInfoAnimal", propOrder = {
    "animalNameKz",
    "animalNameRu",
    "animalCount",
    "animalCountUnitKz",
    "animalCountUnitRu",
    "modificationDate",
    "reasonRu",
    "reasonKz",
    "animalCode"
})
public class FarmInfoAnimal {

    @XmlElement(name = "AnimalNameKz", required = true, nillable = true)
    protected String animalNameKz;
    @XmlElement(name = "AnimalNameRu", required = true, nillable = true)
    protected String animalNameRu;
    @XmlElement(name = "AnimalCount", required = true, nillable = true)
    protected String animalCount;
    @XmlElement(name = "AnimalCountUnitKz", required = true, nillable = true)
    protected String animalCountUnitKz;
    @XmlElement(name = "AnimalCountUnitRu", required = true, nillable = true)
    protected String animalCountUnitRu;
    @XmlElement(name = "ModificationDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modificationDate;
    @XmlElement(required = true, nillable = true)
    protected String reasonRu;
    @XmlElement(required = true, nillable = true)
    protected String reasonKz;
    @XmlElement(required = true, nillable = true)
    protected String animalCode;

    /**
     * Gets the value of the animalNameKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalNameKz() {
        return animalNameKz;
    }

    /**
     * Sets the value of the animalNameKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalNameKz(String value) {
        this.animalNameKz = value;
    }

    /**
     * Gets the value of the animalNameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalNameRu() {
        return animalNameRu;
    }

    /**
     * Sets the value of the animalNameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalNameRu(String value) {
        this.animalNameRu = value;
    }

    /**
     * Gets the value of the animalCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalCount() {
        return animalCount;
    }

    /**
     * Sets the value of the animalCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalCount(String value) {
        this.animalCount = value;
    }

    /**
     * Gets the value of the animalCountUnitKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalCountUnitKz() {
        return animalCountUnitKz;
    }

    /**
     * Sets the value of the animalCountUnitKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalCountUnitKz(String value) {
        this.animalCountUnitKz = value;
    }

    /**
     * Gets the value of the animalCountUnitRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalCountUnitRu() {
        return animalCountUnitRu;
    }

    /**
     * Sets the value of the animalCountUnitRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalCountUnitRu(String value) {
        this.animalCountUnitRu = value;
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
     * Gets the value of the animalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnimalCode() {
        return animalCode;
    }

    /**
     * Sets the value of the animalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnimalCode(String value) {
        this.animalCode = value;
    }

}
