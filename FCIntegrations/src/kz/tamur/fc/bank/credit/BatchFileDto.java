
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for batchFileDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="batchFileDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fileNumber" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="fileScheme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchFile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="uploadedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="batchUploadStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfContracts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="numberOfSubjects" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="updatedContracts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="updatedSubjects" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="newContracts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="newSubjects" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mergedContracts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="skippedContracts" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="skippedSubjects" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mergedSubjects" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="numberOfErrors" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="batchPackage" type="{http://data.chdb.scb.kz}batchPackageDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchFileDto", propOrder = {
    "fileId",
    "fileNumber",
    "fileScheme",
    "fileName",
    "batchFile",
    "uploadedTime",
    "batchUploadStatus",
    "numberOfContracts",
    "numberOfSubjects",
    "updatedContracts",
    "updatedSubjects",
    "newContracts",
    "newSubjects",
    "mergedContracts",
    "skippedContracts",
    "skippedSubjects",
    "mergedSubjects",
    "numberOfErrors",
    "batchPackage"
})
public class BatchFileDto {

    protected Long fileId;
    protected Integer fileNumber;
    protected String fileScheme;
    protected String fileName;
    protected String batchFile;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar uploadedTime;
    protected String batchUploadStatus;
    protected Integer numberOfContracts;
    protected Integer numberOfSubjects;
    protected Integer updatedContracts;
    protected Integer updatedSubjects;
    protected Integer newContracts;
    protected Integer newSubjects;
    protected Integer mergedContracts;
    protected Integer skippedContracts;
    protected Integer skippedSubjects;
    protected Integer mergedSubjects;
    protected Integer numberOfErrors;
    protected BatchPackageDto batchPackage;

    /**
     * Gets the value of the fileId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * Sets the value of the fileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFileId(Long value) {
        this.fileId = value;
    }

    /**
     * Gets the value of the fileNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileNumber() {
        return fileNumber;
    }

    /**
     * Sets the value of the fileNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileNumber(Integer value) {
        this.fileNumber = value;
    }

    /**
     * Gets the value of the fileScheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileScheme() {
        return fileScheme;
    }

    /**
     * Sets the value of the fileScheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileScheme(String value) {
        this.fileScheme = value;
    }

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the batchFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchFile() {
        return batchFile;
    }

    /**
     * Sets the value of the batchFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchFile(String value) {
        this.batchFile = value;
    }

    /**
     * Gets the value of the uploadedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUploadedTime() {
        return uploadedTime;
    }

    /**
     * Sets the value of the uploadedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUploadedTime(XMLGregorianCalendar value) {
        this.uploadedTime = value;
    }

    /**
     * Gets the value of the batchUploadStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchUploadStatus() {
        return batchUploadStatus;
    }

    /**
     * Sets the value of the batchUploadStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchUploadStatus(String value) {
        this.batchUploadStatus = value;
    }

    /**
     * Gets the value of the numberOfContracts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfContracts() {
        return numberOfContracts;
    }

    /**
     * Sets the value of the numberOfContracts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfContracts(Integer value) {
        this.numberOfContracts = value;
    }

    /**
     * Gets the value of the numberOfSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfSubjects() {
        return numberOfSubjects;
    }

    /**
     * Sets the value of the numberOfSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfSubjects(Integer value) {
        this.numberOfSubjects = value;
    }

    /**
     * Gets the value of the updatedContracts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUpdatedContracts() {
        return updatedContracts;
    }

    /**
     * Sets the value of the updatedContracts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUpdatedContracts(Integer value) {
        this.updatedContracts = value;
    }

    /**
     * Gets the value of the updatedSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUpdatedSubjects() {
        return updatedSubjects;
    }

    /**
     * Sets the value of the updatedSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUpdatedSubjects(Integer value) {
        this.updatedSubjects = value;
    }

    /**
     * Gets the value of the newContracts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNewContracts() {
        return newContracts;
    }

    /**
     * Sets the value of the newContracts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNewContracts(Integer value) {
        this.newContracts = value;
    }

    /**
     * Gets the value of the newSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNewSubjects() {
        return newSubjects;
    }

    /**
     * Sets the value of the newSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNewSubjects(Integer value) {
        this.newSubjects = value;
    }

    /**
     * Gets the value of the mergedContracts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMergedContracts() {
        return mergedContracts;
    }

    /**
     * Sets the value of the mergedContracts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMergedContracts(Integer value) {
        this.mergedContracts = value;
    }

    /**
     * Gets the value of the skippedContracts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkippedContracts() {
        return skippedContracts;
    }

    /**
     * Sets the value of the skippedContracts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkippedContracts(Integer value) {
        this.skippedContracts = value;
    }

    /**
     * Gets the value of the skippedSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkippedSubjects() {
        return skippedSubjects;
    }

    /**
     * Sets the value of the skippedSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkippedSubjects(Integer value) {
        this.skippedSubjects = value;
    }

    /**
     * Gets the value of the mergedSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMergedSubjects() {
        return mergedSubjects;
    }

    /**
     * Sets the value of the mergedSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMergedSubjects(Integer value) {
        this.mergedSubjects = value;
    }

    /**
     * Gets the value of the numberOfErrors property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfErrors() {
        return numberOfErrors;
    }

    /**
     * Sets the value of the numberOfErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfErrors(Integer value) {
        this.numberOfErrors = value;
    }

    /**
     * Gets the value of the batchPackage property.
     * 
     * @return
     *     possible object is
     *     {@link BatchPackageDto }
     *     
     */
    public BatchPackageDto getBatchPackage() {
        return batchPackage;
    }

    /**
     * Sets the value of the batchPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchPackageDto }
     *     
     */
    public void setBatchPackage(BatchPackageDto value) {
        this.batchPackage = value;
    }

}
