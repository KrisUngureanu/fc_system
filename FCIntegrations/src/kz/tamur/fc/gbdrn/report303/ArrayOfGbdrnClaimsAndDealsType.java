//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.24 at 03:16:44 PM ALMT 
//


package kz.tamur.fc.gbdrn.report303;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения о притязаниях на объект недвижимости
 * 
 * <p>Java class for ArrayOfGbdrnClaimsAndDealsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGbdrnClaimsAndDealsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocList" type="{http://types.303.reports.egov.rn3.tamur.kz}GbdrnClaimsAndDealsType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGbdrnClaimsAndDealsType", propOrder = {
    "docList"
})
public class ArrayOfGbdrnClaimsAndDealsType {

    @XmlElement(name = "DocList")
    protected List<GbdrnClaimsAndDealsType> docList;

    /**
     * Gets the value of the docList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GbdrnClaimsAndDealsType }
     * 
     * 
     */
    public List<GbdrnClaimsAndDealsType> getDocList() {
        if (docList == null) {
            docList = new ArrayList<GbdrnClaimsAndDealsType>();
        }
        return this.docList;
    }

}
