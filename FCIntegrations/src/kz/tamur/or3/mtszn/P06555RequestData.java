
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06555RequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06555RequestData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequestData">
 *       &lt;sequence>
 *         &lt;element name="p_dir_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="p_profession_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06555RequestData", propOrder = {
    "pDirType",
    "pProfessionId"
})
public class P06555RequestData
    extends BaseRequestData
{

    @XmlElement(name = "p_dir_type")
    protected String pDirType;
    @XmlElement(name = "p_profession_id")
    protected int pProfessionId;

    /**
     * Gets the value of the pDirType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPDirType() {
        return pDirType;
    }

    /**
     * Sets the value of the pDirType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPDirType(String value) {
        this.pDirType = value;
    }

    /**
     * Gets the value of the pProfessionId property.
     * 
     */
    public int getPProfessionId() {
        return pProfessionId;
    }

    /**
     * Sets the value of the pProfessionId property.
     * 
     */
    public void setPProfessionId(int value) {
        this.pProfessionId = value;
    }

}
