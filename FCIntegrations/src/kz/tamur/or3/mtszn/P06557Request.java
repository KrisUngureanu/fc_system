
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06557Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06557Request">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="requestData" type="{http://services.sync.mtszn/}p06557RequestData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06557Request", propOrder = {
    "requestData"
})
public class P06557Request
    extends BaseRequest
{

    protected P06557RequestData requestData;

    /**
     * Gets the value of the requestData property.
     * 
     * @return
     *     possible object is
     *     {@link P06557RequestData }
     *     
     */
    public P06557RequestData getRequestData() {
        return requestData;
    }

    /**
     * Sets the value of the requestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06557RequestData }
     *     
     */
    public void setRequestData(P06557RequestData value) {
        this.requestData = value;
    }

}
