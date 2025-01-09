//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 09:45:18 AM ALMT 
//


package kz.tamur.shep.sed.tempstorage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ��������� ���������� ������� �� ���������� ���������
 * 
 * <p>Java class for TempStorageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TempStorageResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://egov.bee.kz/eds/tempstorage/v2/}TempStorageRequestType"/>
 *         &lt;element name="uploadResponse" type="{http://egov.bee.kz/eds/tempstorage/v2/}UploadResponse" minOccurs="0"/>
 *         &lt;element name="downloadResponse" type="{http://egov.bee.kz/eds/tempstorage/v2/}DownloadResponse" minOccurs="0"/>
 *         &lt;element name="confirmResponse" type="{http://egov.bee.kz/eds/tempstorage/v2/}ConfirmResponse" minOccurs="0"/>
 *         &lt;element name="getFileInfoResponse" type="{http://egov.bee.kz/eds/tempstorage/v2/}GetFileInfoResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TempStorageResponse", propOrder = {
    "type",
    "uploadResponse",
    "downloadResponse",
    "confirmResponse",
    "getFileInfoResponse"
})
@XmlRootElement
public class TempStorageResponse {

    @XmlElement(required = true)
    protected TempStorageRequestType type;
    protected UploadResponse uploadResponse;
    protected DownloadResponse downloadResponse;
    protected ConfirmResponse confirmResponse;
    protected GetFileInfoResponse getFileInfoResponse;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TempStorageRequestType }
     *     
     */
    public TempStorageRequestType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TempStorageRequestType }
     *     
     */
    public void setType(TempStorageRequestType value) {
        this.type = value;
    }

    /**
     * Gets the value of the uploadResponse property.
     * 
     * @return
     *     possible object is
     *     {@link UploadResponse }
     *     
     */
    public UploadResponse getUploadResponse() {
        return uploadResponse;
    }

    /**
     * Sets the value of the uploadResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link UploadResponse }
     *     
     */
    public void setUploadResponse(UploadResponse value) {
        this.uploadResponse = value;
    }

    /**
     * Gets the value of the downloadResponse property.
     * 
     * @return
     *     possible object is
     *     {@link DownloadResponse }
     *     
     */
    public DownloadResponse getDownloadResponse() {
        return downloadResponse;
    }

    /**
     * Sets the value of the downloadResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link DownloadResponse }
     *     
     */
    public void setDownloadResponse(DownloadResponse value) {
        this.downloadResponse = value;
    }

    /**
     * Gets the value of the confirmResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ConfirmResponse }
     *     
     */
    public ConfirmResponse getConfirmResponse() {
        return confirmResponse;
    }

    /**
     * Sets the value of the confirmResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfirmResponse }
     *     
     */
    public void setConfirmResponse(ConfirmResponse value) {
        this.confirmResponse = value;
    }

    /**
     * Gets the value of the getFileInfoResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetFileInfoResponse }
     *     
     */
    public GetFileInfoResponse getGetFileInfoResponse() {
        return getFileInfoResponse;
    }

    /**
     * Sets the value of the getFileInfoResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetFileInfoResponse }
     *     
     */
    public void setGetFileInfoResponse(GetFileInfoResponse value) {
        this.getFileInfoResponse = value;
    }

}