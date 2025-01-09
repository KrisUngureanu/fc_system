
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for languageName complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="languageName">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}additionalName">
 *       &lt;sequence>
 *         &lt;element name="level" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "languageName", propOrder = {
    "level"
})
public class LanguageName
    extends AdditionalName
{

    protected AdditionalName level;

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setLevel(AdditionalName value) {
        this.level = value;
    }

}
