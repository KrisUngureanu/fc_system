
package kz.tamur.or3.mtszn.akimat;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFarmInfoFamilyMember complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFarmInfoFamilyMember">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FarmInfoFamilyMember" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoFamilyMember" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFarmInfoFamilyMember", propOrder = {
    "farmInfoFamilyMember"
})
public class ArrayOfFarmInfoFamilyMember {

    @XmlElement(name = "FarmInfoFamilyMember", nillable = true)
    protected List<FarmInfoFamilyMember> farmInfoFamilyMember;

    /**
     * Gets the value of the farmInfoFamilyMember property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the farmInfoFamilyMember property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFarmInfoFamilyMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FarmInfoFamilyMember }
     * 
     * 
     */
    public List<FarmInfoFamilyMember> getFarmInfoFamilyMember() {
        if (farmInfoFamilyMember == null) {
            farmInfoFamilyMember = new ArrayList<FarmInfoFamilyMember>();
        }
        return this.farmInfoFamilyMember;
    }

}
