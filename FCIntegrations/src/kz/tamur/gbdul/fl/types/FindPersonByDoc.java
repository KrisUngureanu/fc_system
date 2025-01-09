
package kz.tamur.gbdul.fl.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.gbdul.fl.message.RequestDoc;


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
 *         &lt;element name="requestDoc" type="{http://message.persistence.interactive.nat}RequestDoc" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findPersonByDoc", propOrder = {
    "requestDoc"
})
public class FindPersonByDoc {

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
