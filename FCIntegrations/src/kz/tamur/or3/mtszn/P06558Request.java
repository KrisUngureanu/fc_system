
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06558Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06558Request">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="requestData" type="{http://services.sync.mtszn/}p06558RequestData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06558Request", propOrder = {
    "requestData"
})
public class P06558Request
    extends BaseRequest
{

    protected P06558RequestData requestData;

    /**
     * Gets the value of the requestData property.
     * 
     * @return
     *     possible object is
     *     {@link P06558RequestData }
     *     
     */
    public P06558RequestData getRequestData() {
        return requestData;
    }

    /**
     * Sets the value of the requestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06558RequestData }
     *     
     */
    public void setRequestData(P06558RequestData value) {
        this.requestData = value;
    }

}
