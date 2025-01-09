
package kz.tamur.or3.mtszn.akimat;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFarmInfoGarden complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFarmInfoGarden">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FarmInfoGarden" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoGarden" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFarmInfoGarden", propOrder = {
    "farmInfoGarden"
})
public class ArrayOfFarmInfoGarden {

    @XmlElement(name = "FarmInfoGarden", nillable = true)
    protected List<FarmInfoGarden> farmInfoGarden;

    /**
     * Gets the value of the farmInfoGarden property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the farmInfoGarden property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFarmInfoGarden().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FarmInfoGarden }
     * 
     * 
     */
    public List<FarmInfoGarden> getFarmInfoGarden() {
        if (farmInfoGarden == null) {
            farmInfoGarden = new ArrayList<FarmInfoGarden>();
        }
        return this.farmInfoGarden;
    }

}
