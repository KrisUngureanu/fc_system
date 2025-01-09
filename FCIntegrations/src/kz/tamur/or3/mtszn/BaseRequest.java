
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.bas.P06563Request;


/**
 * <p>Java class for baseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="baseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="systemInfo" type="{http://services.sync.mtszn/}SystemInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseRequest", propOrder = {
    "systemInfo"
})
@XmlSeeAlso({
    P06582Request.class,
    P06556Request.class,
    P06552Request.class,
    P06581Request.class,
    P06561Request.class,
    P06560Request.class,
    P06558Request.class,
    P06557Request.class,
    P06555Request.class,
    P06554Request.class,
    P06591Request.class,
    P06590Request.class,
    P06559Request.class,
    P06562Request.class,
    P06551Request.class,
    P06580Request.class,
    P06553Request.class,
    P06563Request.class
})
public abstract class BaseRequest {

    protected SystemInfo systemInfo;

    /**
     * Gets the value of the systemInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SystemInfo }
     *     
     */
    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    /**
     * Sets the value of the systemInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemInfo }
     *     
     */
    public void setSystemInfo(SystemInfo value) {
        this.systemInfo = value;
    }

}
