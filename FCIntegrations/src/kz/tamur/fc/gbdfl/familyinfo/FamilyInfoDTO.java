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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for familyInfoDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="familyInfoDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthInfos" type="{}birthDTO" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="divorceInfos" type="{}divorceDTO" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="marriageInfos" type="{}marriageDTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "familyInfoDTO", namespace = "", propOrder = {
    "birthInfos",
    "divorceInfos",
    "marriageInfos"
})
public class FamilyInfoDTO {

    @XmlElement(nillable = true)
    protected List<BirthDTO> birthInfos;
    @XmlElement(nillable = true)
    protected List<DivorceDTO> divorceInfos;
    @XmlElement(nillable = true)
    protected List<MarriageDTO> marriageInfos;

    /**
     * Gets the value of the birthInfos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the birthInfos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBirthInfos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BirthDTO }
     * 
     * 
     */
    public List<BirthDTO> getBirthInfos() {
        if (birthInfos == null) {
            birthInfos = new ArrayList<BirthDTO>();
        }
        return this.birthInfos;
    }

    /**
     * Gets the value of the divorceInfos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the divorceInfos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDivorceInfos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DivorceDTO }
     * 
     * 
     */
    public List<DivorceDTO> getDivorceInfos() {
        if (divorceInfos == null) {
            divorceInfos = new ArrayList<DivorceDTO>();
        }
        return this.divorceInfos;
    }

    /**
     * Gets the value of the marriageInfos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the marriageInfos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMarriageInfos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MarriageDTO }
     * 
     * 
     */
    public List<MarriageDTO> getMarriageInfos() {
        if (marriageInfos == null) {
            marriageInfos = new ArrayList<MarriageDTO>();
        }
        return this.marriageInfos;
    }

}
