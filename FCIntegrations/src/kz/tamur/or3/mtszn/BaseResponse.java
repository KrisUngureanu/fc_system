
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.bas.P06563Response;


/**
 * <p>Java class for baseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="baseResponse">
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
@XmlType(name = "baseResponse", propOrder = {
    "systemInfo"
})
@XmlSeeAlso({
    P06551Response.class,
    P06591Response.class,
    P06555Response.class,
    P06580Response.class,
    P06553Response.class,
    P06557Response.class,
    P06552Response.class,
    P06558Response.class,
    P06562Response.class,
    P06556Response.class,
    P06559Response.class,
    P06581Response.class,
    P06560Response.class,
    P06554Response.class,
    P06582Response.class,
    P06561Response.class,
    P06590Response.class,
    P06563Response.class
})
public abstract class BaseResponse {

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
