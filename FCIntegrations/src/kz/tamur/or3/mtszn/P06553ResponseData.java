
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06553ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06553ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="jobPlacementTypes" type="{http://services.sync.mtszn/}jobPlacementType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06553ResponseData", propOrder = {
    "jobPlacementTypes"
})
public class P06553ResponseData
    extends BaseResponseData
{

    @XmlElement(nillable = true)
    protected List<JobPlacementType> jobPlacementTypes;

    /**
     * Gets the value of the jobPlacementTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jobPlacementTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJobPlacementTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JobPlacementType }
     * 
     * 
     */
    public List<JobPlacementType> getJobPlacementTypes() {
        if (jobPlacementTypes == null) {
            jobPlacementTypes = new ArrayList<JobPlacementType>();
        }
        return this.jobPlacementTypes;
    }

}
