//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:34:25 PM ALMT 
//


package kz.tamur.fc.mon.nedb.universal.nci;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for allDataItemsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allDataItemsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="allDataItems" type="{http://ws.ddm.nedb.kz/}dataItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "allDataItemsResponse", propOrder = {
    "allDataItems"
})
public class AllDataItemsResponse {

    protected List<DataItem> allDataItems;

    /**
     * Gets the value of the allDataItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allDataItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllDataItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataItem }
     * 
     * 
     */
    public List<DataItem> getAllDataItems() {
        if (allDataItems == null) {
            allDataItems = new ArrayList<DataItem>();
        }
        return this.allDataItems;
    }

}