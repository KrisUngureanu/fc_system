
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06590Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06590Response">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponse">
 *       &lt;sequence>
 *         &lt;element name="responseData" type="{http://services.sync.mtszn/}p06590ResponseData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06590Response", propOrder = {
    "responseData"
})
public class P06590Response
    extends BaseResponse
{

    protected P06590ResponseData responseData;

    /**
     * Gets the value of the responseData property.
     * 
     * @return
     *     possible object is
     *     {@link P06590ResponseData }
     *     
     */
    public P06590ResponseData getResponseData() {
        return responseData;
    }

    /**
     * Sets the value of the responseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06590ResponseData }
     *     
     */
    public void setResponseData(P06590ResponseData value) {
        this.responseData = value;
    }

}
