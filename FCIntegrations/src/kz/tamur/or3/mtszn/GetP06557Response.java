
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getP06557Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getP06557Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://services.sync.mtszn/}p06557Response" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getP06557Response", propOrder = {
    "_return"
})
public class GetP06557Response {

    @XmlElement(name = "return")
    protected P06557Response _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link P06557Response }
     *     
     */
    public P06557Response getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06557Response }
     *     
     */
    public void setReturn(P06557Response value) {
        this._return = value;
    }

}