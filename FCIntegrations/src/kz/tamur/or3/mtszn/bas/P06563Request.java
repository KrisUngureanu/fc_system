
package kz.tamur.or3.mtszn.bas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.BaseRequest;


/**
 * <p>Java class for p06563Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06563Request">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="requestData" type="{http://kz/mtszn/gcvp/bas/SSIFService/Types}ssifPartDocRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06563Request", propOrder = {
    "requestData"
})
public class P06563Request
    extends BaseRequest
{

    protected SsifPartDocRequest requestData;

    /**
     * Gets the value of the requestData property.
     * 
     * @return
     *     possible object is
     *     {@link SsifPartDocRequest }
     *     
     */
    public SsifPartDocRequest getRequestData() {
        return requestData;
    }

    /**
     * Sets the value of the requestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SsifPartDocRequest }
     *     
     */
    public void setRequestData(SsifPartDocRequest value) {
        this.requestData = value;
    }

}
