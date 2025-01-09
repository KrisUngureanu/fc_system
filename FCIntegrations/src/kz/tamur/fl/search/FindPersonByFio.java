
package kz.tamur.fl.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findPersonByFio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findPersonByFio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestFio" type="{http://message.persistence.interactive.nat}RequestSearchFio" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findPersonByFio", namespace = "http://webservice.request.universal.interactive.nat", propOrder = {
    "requestFio"
})
public class FindPersonByFio {

    protected RequestSearchFio requestFio;

    /**
     * Gets the value of the requestFio property.
     * 
     * @return
     *     possible object is
     *     {@link RequestSearchFio }
     *     
     */
    public RequestSearchFio getRequestFio() {
        return requestFio;
    }

    /**
     * Sets the value of the requestFio property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestSearchFio }
     *     
     */
    public void setRequestFio(RequestSearchFio value) {
        this.requestFio = value;
    }

}
