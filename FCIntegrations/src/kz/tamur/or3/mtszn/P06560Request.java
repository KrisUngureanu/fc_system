
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06560Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06560Request">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="requestData" type="{http://services.sync.mtszn/}p06560RequestData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06560Request", propOrder = {
    "requestData"
})
public class P06560Request
    extends BaseRequest
{

    protected P06560RequestData requestData;

    /**
     * Gets the value of the requestData property.
     * 
     * @return
     *     possible object is
     *     {@link P06560RequestData }
     *     
     */
    public P06560RequestData getRequestData() {
        return requestData;
    }

    /**
     * Sets the value of the requestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06560RequestData }
     *     
     */
    public void setRequestData(P06560RequestData value) {
        this.requestData = value;
    }

}
