//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.05.17 at 08:13:49 PM GMT+05:00 
//


package kz.tamur.fc.gbdfl.familyinfo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResponseFamilyInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseFamilyInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://message.persistence.interactive.nat}ResponseShepSyncDTO">
 *       &lt;sequence>
 *         &lt;element name="familyInfoList" type="{}familyInfoDTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseFamilyInfo", namespace = "http://message.persistence.interactive.nat", propOrder = {
    "familyInfoList"
})
@XmlRootElement(name = "responseFamilyInfo", namespace = "http://dictionaries.persistence.interactive.nat")
public class ResponseFamilyInfo
    extends ResponseShepSyncDTO
{

    protected List<FamilyInfoDTO> familyInfoList;

    /**
     * Gets the value of the familyInfoList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the familyInfoList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFamilyInfoList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FamilyInfoDTO }
     * 
     * 
     */
    public List<FamilyInfoDTO> getFamilyInfoList() {
        if (familyInfoList == null) {
            familyInfoList = new ArrayList<FamilyInfoDTO>();
        }
        return this.familyInfoList;
    }

}
