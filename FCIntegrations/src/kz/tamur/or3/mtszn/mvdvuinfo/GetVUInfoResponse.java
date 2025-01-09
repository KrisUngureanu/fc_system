
package kz.tamur.or3.mtszn.mvdvuinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.mvd.TvuInfoResponse;


/**
 * <p>Java class for getVUInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getVUInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="response" type="{http://schemas.letograf.kz/iiscon/mvd/driver}tvuInfoResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getVUInfoResponse", propOrder = {
    "response"
})
public class GetVUInfoResponse {

    protected TvuInfoResponse response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link TvuInfoResponse }
     *     
     */
    public TvuInfoResponse getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link TvuInfoResponse }
     *     
     */
    public void setResponse(TvuInfoResponse value) {
        this.response = value;
    }

}
