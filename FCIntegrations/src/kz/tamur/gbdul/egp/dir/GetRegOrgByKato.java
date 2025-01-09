
package kz.tamur.gbdul.egp.dir;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRegOrgByKato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRegOrgByKato">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://GBD_UL_Native_Library/DataTypes/DirectoryTypes}RegOrgByKatoRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRegOrgByKato", namespace = "http://dir.egp.gbdul.tamur.kz/", propOrder = {
    "arg0"
})
public class GetRegOrgByKato {

    protected RegOrgByKatoRequest arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link RegOrgByKatoRequest }
     *     
     */
    public RegOrgByKatoRequest getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegOrgByKatoRequest }
     *     
     */
    public void setArg0(RegOrgByKatoRequest value) {
        this.arg0 = value;
    }

}
