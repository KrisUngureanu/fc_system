
package kz.tamur.or3.mtszn.bas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.BaseResponse;


/**
 * <p>Java class for p06563Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06563Response">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponse">
 *       &lt;sequence>
 *         &lt;element name="responseData" type="{http://kz/mtszn/gcvp/bas/SSIFService/Types}ssifPartDocResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06563Response", propOrder = {
    "responseData"
})
public class P06563Response
    extends BaseResponse
{

    protected SsifPartDocResponse responseData;

    /**
     * Gets the value of the responseData property.
     * 
     * @return
     *     possible object is
     *     {@link SsifPartDocResponse }
     *     
     */
    public SsifPartDocResponse getResponseData() {
        return responseData;
    }

    /**
     * Sets the value of the responseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SsifPartDocResponse }
     *     
     */
    public void setResponseData(SsifPartDocResponse value) {
        this.responseData = value;
    }

}
