
package kz.tamur.fl.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findPersonByDoc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findPersonByDoc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://webservice.request.universal.interactive.nat}requestDoc" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findPersonByDoc", namespace = "http://webservice.request.universal.interactive.nat", propOrder = {
    "requestDoc"
})
public class FindPersonByDoc {

    @XmlElement(namespace = "http://webservice.request.universal.interactive.nat")
    protected RequestDoc requestDoc;

    /**
     * Gets the value of the requestDoc property.
     * 
     * @return
     *     possible object is
     *     {@link RequestDoc }
     *     
     */
    public RequestDoc getRequestDoc() {
        return requestDoc;
    }

    /**
     * Sets the value of the requestDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestDoc }
     *     
     */
    public void setRequestDoc(RequestDoc value) {
        this.requestDoc = value;
    }

}
