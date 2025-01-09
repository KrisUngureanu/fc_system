//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:36:37 PM ALMT 
//


package kz.tamur.fc.mon.nedb.universal.response;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for schoolFlatData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="schoolFlatData">
 *   &lt;complexContent>
 *     &lt;extension base="{}questionnaireFlatData">
 *       &lt;sequence>
 *         &lt;element name="passportItem" type="{}questionnaireValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="bin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="typeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="areaCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kkName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruShortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kkShortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enShortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ruFullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kkFullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enFullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currentStudents" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="completedStudents" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="leftStudents" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="currentTeachers" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="leftTeachers" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "schoolFlatData", propOrder = {
    "passportItem",
    "bin",
    "typeCode",
    "areaCode",
    "regionCode",
    "locationCode",
    "ruName",
    "kkName",
    "enName",
    "ruShortName",
    "kkShortName",
    "enShortName",
    "ruFullName",
    "kkFullName",
    "enFullName",
    "currentStudents",
    "completedStudents",
    "leftStudents",
    "currentTeachers",
    "leftTeachers"
})
public class SchoolFlatData
    extends QuestionnaireFlatData
{

    protected List<QuestionnaireValue> passportItem;
    protected String bin;
    protected String typeCode;
    protected String areaCode;
    protected String regionCode;
    protected String locationCode;
    protected String ruName;
    protected String kkName;
    protected String enName;
    protected String ruShortName;
    protected String kkShortName;
    protected String enShortName;
    protected String ruFullName;
    protected String kkFullName;
    protected String enFullName;
    @XmlElement(nillable = true)
    protected List<String> currentStudents;
    @XmlElement(nillable = true)
    protected List<String> completedStudents;
    @XmlElement(nillable = true)
    protected List<String> leftStudents;
    @XmlElement(nillable = true)
    protected List<String> currentTeachers;
    @XmlElement(nillable = true)
    protected List<String> leftTeachers;

    /**
     * Gets the value of the passportItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passportItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassportItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuestionnaireValue }
     * 
     * 
     */
    public List<QuestionnaireValue> getPassportItem() {
        if (passportItem == null) {
            passportItem = new ArrayList<QuestionnaireValue>();
        }
        return this.passportItem;
    }

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBin() {
        return bin;
    }

    /**
     * Sets the value of the bin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBin(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the typeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the value of the typeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeCode(String value) {
        this.typeCode = value;
    }

    /**
     * Gets the value of the areaCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the value of the areaCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaCode(String value) {
        this.areaCode = value;
    }

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionCode(String value) {
        this.regionCode = value;
    }

    /**
     * Gets the value of the locationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * Sets the value of the locationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationCode(String value) {
        this.locationCode = value;
    }

    /**
     * Gets the value of the ruName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuName() {
        return ruName;
    }

    /**
     * Sets the value of the ruName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuName(String value) {
        this.ruName = value;
    }

    /**
     * Gets the value of the kkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKkName() {
        return kkName;
    }

    /**
     * Sets the value of the kkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKkName(String value) {
        this.kkName = value;
    }

    /**
     * Gets the value of the enName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnName() {
        return enName;
    }

    /**
     * Sets the value of the enName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnName(String value) {
        this.enName = value;
    }

    /**
     * Gets the value of the ruShortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuShortName() {
        return ruShortName;
    }

    /**
     * Sets the value of the ruShortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuShortName(String value) {
        this.ruShortName = value;
    }

    /**
     * Gets the value of the kkShortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKkShortName() {
        return kkShortName;
    }

    /**
     * Sets the value of the kkShortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKkShortName(String value) {
        this.kkShortName = value;
    }

    /**
     * Gets the value of the enShortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnShortName() {
        return enShortName;
    }

    /**
     * Sets the value of the enShortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnShortName(String value) {
        this.enShortName = value;
    }

    /**
     * Gets the value of the ruFullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuFullName() {
        return ruFullName;
    }

    /**
     * Sets the value of the ruFullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuFullName(String value) {
        this.ruFullName = value;
    }

    /**
     * Gets the value of the kkFullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKkFullName() {
        return kkFullName;
    }

    /**
     * Sets the value of the kkFullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKkFullName(String value) {
        this.kkFullName = value;
    }

    /**
     * Gets the value of the enFullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnFullName() {
        return enFullName;
    }

    /**
     * Sets the value of the enFullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnFullName(String value) {
        this.enFullName = value;
    }

    /**
     * Gets the value of the currentStudents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the currentStudents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCurrentStudents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCurrentStudents() {
        if (currentStudents == null) {
            currentStudents = new ArrayList<String>();
        }
        return this.currentStudents;
    }

    /**
     * Gets the value of the completedStudents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the completedStudents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompletedStudents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCompletedStudents() {
        if (completedStudents == null) {
            completedStudents = new ArrayList<String>();
        }
        return this.completedStudents;
    }

    /**
     * Gets the value of the leftStudents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the leftStudents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLeftStudents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLeftStudents() {
        if (leftStudents == null) {
            leftStudents = new ArrayList<String>();
        }
        return this.leftStudents;
    }

    /**
     * Gets the value of the currentTeachers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the currentTeachers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCurrentTeachers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCurrentTeachers() {
        if (currentTeachers == null) {
            currentTeachers = new ArrayList<String>();
        }
        return this.currentTeachers;
    }

    /**
     * Gets the value of the leftTeachers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the leftTeachers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLeftTeachers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLeftTeachers() {
        if (leftTeachers == null) {
            leftTeachers = new ArrayList<String>();
        }
        return this.leftTeachers;
    }

}