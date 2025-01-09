
package kz.tamur.or3.mtszn.akimat;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFarmInfoGardens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFarmInfoGardens">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FarmInfoGardens" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoGardens" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFarmInfoGardens", propOrder = {
    "farmInfoGardens"
})
public class ArrayOfFarmInfoGardens {

    @XmlElement(name = "FarmInfoGardens", nillable = true)
    protected List<FarmInfoGardens> farmInfoGardens;

    /**
     * Gets the value of the farmInfoGardens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the farmInfoGardens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFarmInfoGardens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FarmInfoGardens }
     * 
     * 
     */
    public List<FarmInfoGardens> getFarmInfoGardens() {
        if (farmInfoGardens == null) {
            farmInfoGardens = new ArrayList<FarmInfoGardens>();
        }
        return this.farmInfoGardens;
    }

}
