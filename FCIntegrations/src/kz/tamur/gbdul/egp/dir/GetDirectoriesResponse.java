
package kz.tamur.gbdul.egp.dir;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDirectoriesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDirectoriesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://GBD_UL_Native_Library/DataTypes/DirectoryTypes}DirectoriesInfoResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDirectoriesResponse", namespace = "http://dir.egp.gbdul.tamur.kz/", propOrder = {
    "_return"
})
public class GetDirectoriesResponse {

    @XmlElement(name = "return")
    protected DirectoriesInfoResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link DirectoriesInfoResponse }
     *     
     */
    public DirectoriesInfoResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectoriesInfoResponse }
     *     
     */
    public void setReturn(DirectoriesInfoResponse value) {
        this._return = value;
    }

}
