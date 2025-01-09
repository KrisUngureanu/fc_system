
package kz.tamur.gbdul.egp.dir;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DirectoriesInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectoriesInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Directories" type="{http://GBD_UL_Native_Library/DataTypes/DirectoryTypes}Directory" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectoriesInfoResponse", propOrder = {
    "directories"
})
public class DirectoriesInfoResponse {

    @XmlElement(name = "Directories")
    protected List<Directory> directories;

    /**
     * Gets the value of the directories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the directories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDirectories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Directory }
     * 
     * 
     */
    public List<Directory> getDirectories() {
        if (directories == null) {
            directories = new ArrayList<Directory>();
        }
        return this.directories;
    }

}
