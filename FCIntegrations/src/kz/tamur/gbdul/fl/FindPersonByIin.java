
package kz.tamur.gbdul.fl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findPersonByIin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findPersonByIin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestIin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findPersonByIin", propOrder = {
    "requestIin"
})
public class FindPersonByIin {

    protected String requestIin;

    /**
     * Gets the value of the requestIin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestIin() {
        return requestIin;
    }

    /**
     * Sets the value of the requestIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestIin(String value) {
        this.requestIin = value;
    }

}
