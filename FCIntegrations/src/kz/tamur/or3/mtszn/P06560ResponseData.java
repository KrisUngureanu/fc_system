
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06560ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06560ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="disablements" type="{http://services.sync.mtszn/}disablement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06560ResponseData", propOrder = {
    "disablements"
})
public class P06560ResponseData
    extends BaseResponseData
{

    @XmlElement(nillable = true)
    protected List<Disablement> disablements;

    /**
     * Gets the value of the disablements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the disablements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisablements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Disablement }
     * 
     * 
     */
    public List<Disablement> getDisablements() {
        if (disablements == null) {
            disablements = new ArrayList<Disablement>();
        }
        return this.disablements;
    }

}
