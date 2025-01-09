
package kz.tamur.gbdul.egp.dir;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getNodeByCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getNodeByCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://GBD_UL_Native_Library/DataTypes/DirectoryTypes}NodeByCodeRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getNodeByCode", namespace = "http://dir.egp.gbdul.tamur.kz/", propOrder = {
    "arg0"
})
public class GetNodeByCode {

    protected NodeByCodeRequest arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link NodeByCodeRequest }
     *     
     */
    public NodeByCodeRequest getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeByCodeRequest }
     *     
     */
    public void setArg0(NodeByCodeRequest value) {
        this.arg0 = value;
    }

}