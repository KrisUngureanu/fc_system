
package kz.tamur.gbdul.fl.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.gbdul.fl.message.RequestIin;


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
 *         &lt;element name="requestIin" type="{http://message.persistence.interactive.nat}RequestIin" minOccurs="0"/>
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

    protected RequestIin requestIin;

    /**
     * Gets the value of the requestIin property.
     * 
     * @return
     *     possible object is
     *     {@link RequestIin }
     *     
     */
    public RequestIin getRequestIin() {
        return requestIin;
    }

    /**
     * Sets the value of the requestIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestIin }
     *     
     */
    public void setRequestIin(RequestIin value) {
        this.requestIin = value;
    }

}
