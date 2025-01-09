package kz.tamur.or3.mtszn.akimat;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFarmInfoAnimal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFarmInfoAnimal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FarmInfoAnimal" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoAnimal" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFarmInfoAnimal", propOrder = {
    "farmInfoAnimal"
})
public class ArrayOfFarmInfoAnimal {

    @XmlElement(name = "FarmInfoAnimal", nillable = true)
    protected List<FarmInfoAnimal> farmInfoAnimal;

    /**
     * Gets the value of the farmInfoAnimal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the farmInfoAnimal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFarmInfoAnimal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FarmInfoAnimal }
     * 
     * 
     */
    public List<FarmInfoAnimal> getFarmInfoAnimal() {
        if (farmInfoAnimal == null) {
            farmInfoAnimal = new ArrayList<FarmInfoAnimal>();
        }
        return this.farmInfoAnimal;
    }

}
