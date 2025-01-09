
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for batchImportStatisticResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="batchImportStatisticResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="averageImportTimeMillis" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchImportStatisticResult", propOrder = {
    "averageImportTimeMillis"
})
public class BatchImportStatisticResult {

    protected double averageImportTimeMillis;

    /**
     * Gets the value of the averageImportTimeMillis property.
     * 
     */
    public double getAverageImportTimeMillis() {
        return averageImportTimeMillis;
    }

    /**
     * Sets the value of the averageImportTimeMillis property.
     * 
     */
    public void setAverageImportTimeMillis(double value) {
        this.averageImportTimeMillis = value;
    }

}
