//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.10.22 at 09:45:18 AM ALMT 
//


package kz.tamur.shep.sed.tempstorage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ������ �� �������� ������
 * 
 * <p>Java class for UploadRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UploadRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileUploadRequests" type="{http://egov.bee.kz/eds/tempstorage/v2/}UploadFileRequest" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UploadRequest", propOrder = {
    "fileUploadRequests"
})
public class UploadRequest {

    @XmlElement(required = true)
    protected List<UploadFileRequest> fileUploadRequests;

    /**
     * Gets the value of the fileUploadRequests property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileUploadRequests property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileUploadRequests().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UploadFileRequest }
     * 
     * 
     */
    public List<UploadFileRequest> getFileUploadRequests() {
        if (fileUploadRequests == null) {
            fileUploadRequests = new ArrayList<UploadFileRequest>();
        }
        return this.fileUploadRequests;
    }

}