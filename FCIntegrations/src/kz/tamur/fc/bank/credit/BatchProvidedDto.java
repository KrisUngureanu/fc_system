
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for batchProvidedDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="batchProvidedDto">
 *   &lt;complexContent>
 *     &lt;extension base="{http://data.chdb.scb.kz}dataSourceDto">
 *       &lt;sequence>
 *         &lt;element name="batchId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="batchRecordId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchProvidedDto", propOrder = {
    "batchId",
    "batchRecordId"
})
public abstract class BatchProvidedDto
    extends DataSourceDto
{

    protected Long batchId;
    protected Long batchRecordId;

    /**
     * Gets the value of the batchId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBatchId() {
        return batchId;
    }

    /**
     * Sets the value of the batchId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBatchId(Long value) {
        this.batchId = value;
    }

    /**
     * Gets the value of the batchRecordId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBatchRecordId() {
        return batchRecordId;
    }

    /**
     * Sets the value of the batchRecordId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBatchRecordId(Long value) {
        this.batchRecordId = value;
    }

}
