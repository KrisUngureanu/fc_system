
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getImportDetailInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getImportDetailInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="recordsImportInfo" type="{http://data.chdb.scb.kz}batchRecordsImportInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getImportDetailInfoResponse", propOrder = {
    "recordsImportInfo"
})
public class GetImportDetailInfoResponse {

    protected BatchRecordsImportInfo recordsImportInfo;

    /**
     * Gets the value of the recordsImportInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BatchRecordsImportInfo }
     *     
     */
    public BatchRecordsImportInfo getRecordsImportInfo() {
        return recordsImportInfo;
    }

    /**
     * Sets the value of the recordsImportInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchRecordsImportInfo }
     *     
     */
    public void setRecordsImportInfo(BatchRecordsImportInfo value) {
        this.recordsImportInfo = value;
    }

}
