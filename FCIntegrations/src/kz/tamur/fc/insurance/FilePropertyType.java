//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.05.18 at 09:26:53 AM ALMT 
//


package kz.tamur.fc.insurance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Запрос  для передачи реквизитов загружаемого файла
 * 
 * <p>Java class for filePropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataInfo" type="{http://insurance.fc.tamur.kz}BaseDoc" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filePropertyType", propOrder = {
    "dataInfo"
})
public class FilePropertyType {

    @XmlElement(required = true)
    protected BaseDoc dataInfo;

    /**
     * Gets the value of the dataInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BaseDoc }
     *     
     */
    public BaseDoc getDataInfo() {
        return dataInfo;
    }

    /**
     * Sets the value of the dataInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseDoc }
     *     
     */
    public void setDataInfo(BaseDoc value) {
        this.dataInfo = value;
    }

}
