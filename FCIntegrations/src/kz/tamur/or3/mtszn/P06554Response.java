
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06554Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06554Response">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponse">
 *       &lt;sequence>
 *         &lt;element name="responseData" type="{http://services.sync.mtszn/}p06554ResponseData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06554Response", propOrder = {
    "responseData"
})
public class P06554Response
    extends BaseResponse
{

    protected P06554ResponseData responseData;

    /**
     * Gets the value of the responseData property.
     * 
     * @return
     *     possible object is
     *     {@link P06554ResponseData }
     *     
     */
    public P06554ResponseData getResponseData() {
        return responseData;
    }

    /**
     * Sets the value of the responseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06554ResponseData }
     *     
     */
    public void setResponseData(P06554ResponseData value) {
        this.responseData = value;
    }

}
