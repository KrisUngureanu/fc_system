
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FarmInfoAnimals complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarmInfoAnimals">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AnimalInfo" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoAnimal"/>
 *         &lt;element name="AnimalHistoryInfo" type="{http://types.efarm.service.akimat.shep.nit}ArrayOfFarmInfoAnimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarmInfoAnimals", propOrder = {
    "animalInfo",
    "animalHistoryInfo"
})
public class FarmInfoAnimals {

    @XmlElement(name = "AnimalInfo", required = true, nillable = true)
    protected FarmInfoAnimal animalInfo;
    @XmlElement(name = "AnimalHistoryInfo", required = true, nillable = true)
    protected ArrayOfFarmInfoAnimal animalHistoryInfo;

    /**
     * Gets the value of the animalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link FarmInfoAnimal }
     *     
     */
    public FarmInfoAnimal getAnimalInfo() {
        return animalInfo;
    }

    /**
     * Sets the value of the animalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FarmInfoAnimal }
     *     
     */
    public void setAnimalInfo(FarmInfoAnimal value) {
        this.animalInfo = value;
    }

    /**
     * Gets the value of the animalHistoryInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarmInfoAnimal }
     *     
     */
    public ArrayOfFarmInfoAnimal getAnimalHistoryInfo() {
        return animalHistoryInfo;
    }

    /**
     * Sets the value of the animalHistoryInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarmInfoAnimal }
     *     
     */
    public void setAnimalHistoryInfo(ArrayOfFarmInfoAnimal value) {
        this.animalHistoryInfo = value;
    }

}
