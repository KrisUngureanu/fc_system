
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for batchComplexParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="batchComplexParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://data.chdb.scb.kz}paginationSupportSearchParam">
 *       &lt;sequence>
 *         &lt;element name="fromPackageId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="toPackageId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fromPackageAddTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="toPackageAddTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fromFileStartProcessingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="toFileStartProcessingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fromFileFinishProcessingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="toFileFinishProcessingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="creditorId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fileStatus" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="employeeId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfErrors" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="numberOfErrorsArgs" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchComplexParams", propOrder = {
    "fromPackageId",
    "toPackageId",
    "fromPackageAddTime",
    "toPackageAddTime",
    "fromFileStartProcessingTime",
    "toFileStartProcessingTime",
    "fromFileFinishProcessingTime",
    "toFileFinishProcessingTime",
    "creditorId",
    "fileStatus",
    "employeeId",
    "fileName",
    "fileType",
    "numberOfErrors",
    "numberOfErrorsArgs",
    "language"
})
public class BatchComplexParams
    extends PaginationSupportSearchParam
{

    protected Long fromPackageId;
    protected Long toPackageId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fromPackageAddTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar toPackageAddTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fromFileStartProcessingTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar toFileStartProcessingTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fromFileFinishProcessingTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar toFileFinishProcessingTime;
    protected Long creditorId;
    protected Integer fileStatus;
    protected Long employeeId;
    protected String fileName;
    protected String fileType;
    protected Integer numberOfErrors;
    protected Integer numberOfErrorsArgs;
    protected String language;

    /**
     * Gets the value of the fromPackageId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFromPackageId() {
        return fromPackageId;
    }

    /**
     * Sets the value of the fromPackageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFromPackageId(Long value) {
        this.fromPackageId = value;
    }

    /**
     * Gets the value of the toPackageId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getToPackageId() {
        return toPackageId;
    }

    /**
     * Sets the value of the toPackageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setToPackageId(Long value) {
        this.toPackageId = value;
    }

    /**
     * Gets the value of the fromPackageAddTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromPackageAddTime() {
        return fromPackageAddTime;
    }

    /**
     * Sets the value of the fromPackageAddTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromPackageAddTime(XMLGregorianCalendar value) {
        this.fromPackageAddTime = value;
    }

    /**
     * Gets the value of the toPackageAddTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getToPackageAddTime() {
        return toPackageAddTime;
    }

    /**
     * Sets the value of the toPackageAddTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setToPackageAddTime(XMLGregorianCalendar value) {
        this.toPackageAddTime = value;
    }

    /**
     * Gets the value of the fromFileStartProcessingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromFileStartProcessingTime() {
        return fromFileStartProcessingTime;
    }

    /**
     * Sets the value of the fromFileStartProcessingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromFileStartProcessingTime(XMLGregorianCalendar value) {
        this.fromFileStartProcessingTime = value;
    }

    /**
     * Gets the value of the toFileStartProcessingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getToFileStartProcessingTime() {
        return toFileStartProcessingTime;
    }

    /**
     * Sets the value of the toFileStartProcessingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setToFileStartProcessingTime(XMLGregorianCalendar value) {
        this.toFileStartProcessingTime = value;
    }

    /**
     * Gets the value of the fromFileFinishProcessingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromFileFinishProcessingTime() {
        return fromFileFinishProcessingTime;
    }

    /**
     * Sets the value of the fromFileFinishProcessingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromFileFinishProcessingTime(XMLGregorianCalendar value) {
        this.fromFileFinishProcessingTime = value;
    }

    /**
     * Gets the value of the toFileFinishProcessingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getToFileFinishProcessingTime() {
        return toFileFinishProcessingTime;
    }

    /**
     * Sets the value of the toFileFinishProcessingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setToFileFinishProcessingTime(XMLGregorianCalendar value) {
        this.toFileFinishProcessingTime = value;
    }

    /**
     * Gets the value of the creditorId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCreditorId() {
        return creditorId;
    }

    /**
     * Sets the value of the creditorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCreditorId(Long value) {
        this.creditorId = value;
    }

    /**
     * Gets the value of the fileStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileStatus() {
        return fileStatus;
    }

    /**
     * Sets the value of the fileStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileStatus(Integer value) {
        this.fileStatus = value;
    }

    /**
     * Gets the value of the employeeId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the value of the employeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEmployeeId(Long value) {
        this.employeeId = value;
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
     * Gets the value of the fileType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets the value of the fileType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
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
     * Gets the value of the numberOfErrorsArgs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfErrorsArgs() {
        return numberOfErrorsArgs;
    }

    /**
     * Sets the value of the numberOfErrorsArgs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfErrorsArgs(Integer value) {
        this.numberOfErrorsArgs = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
