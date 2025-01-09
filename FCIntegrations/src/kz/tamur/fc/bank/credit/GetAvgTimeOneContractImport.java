
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAvgTimeOneContractImport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAvgTimeOneContractImport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="params" type="{http://data.chdb.scb.kz}dateFromToParams" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvgTimeOneContractImport", propOrder = {
    "params"
})
public class GetAvgTimeOneContractImport {

    protected DateFromToParams params;

    /**
     * Gets the value of the params property.
     * 
     * @return
     *     possible object is
     *     {@link DateFromToParams }
     *     
     */
    public DateFromToParams getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateFromToParams }
     *     
     */
    public void setParams(DateFromToParams value) {
        this.params = value;
    }

}
