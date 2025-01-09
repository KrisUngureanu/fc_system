
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06559Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06559Request">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="requestData" type="{http://services.sync.mtszn/}p06559RequestData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06559Request", propOrder = {
    "requestData"
})
public class P06559Request
    extends BaseRequest
{

    protected P06559RequestData requestData;

    /**
     * Gets the value of the requestData property.
     * 
     * @return
     *     possible object is
     *     {@link P06559RequestData }
     *     
     */
    public P06559RequestData getRequestData() {
        return requestData;
    }

    /**
     * Sets the value of the requestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06559RequestData }
     *     
     */
    public void setRequestData(P06559RequestData value) {
        this.requestData = value;
    }

}
