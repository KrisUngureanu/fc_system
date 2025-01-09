
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getImportDetailInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getImportDetailInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="params" type="{http://data.chdb.scb.kz}batchFileParams" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getImportDetailInfo", propOrder = {
    "params"
})
public class GetImportDetailInfo {

    protected BatchFileParams params;

    /**
     * Gets the value of the params property.
     * 
     * @return
     *     possible object is
     *     {@link BatchFileParams }
     *     
     */
    public BatchFileParams getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchFileParams }
     *     
     */
    public void setParams(BatchFileParams value) {
        this.params = value;
    }

}
