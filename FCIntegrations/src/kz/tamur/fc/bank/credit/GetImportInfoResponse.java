
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getImportInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getImportInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filesImportInfo" type="{http://data.chdb.scb.kz}batchFilesImportInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getImportInfoResponse", propOrder = {
    "filesImportInfo"
})
public class GetImportInfoResponse {

    protected BatchFilesImportInfo filesImportInfo;

    /**
     * Gets the value of the filesImportInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BatchFilesImportInfo }
     *     
     */
    public BatchFilesImportInfo getFilesImportInfo() {
        return filesImportInfo;
    }

    /**
     * Sets the value of the filesImportInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchFilesImportInfo }
     *     
     */
    public void setFilesImportInfo(BatchFilesImportInfo value) {
        this.filesImportInfo = value;
    }

}
