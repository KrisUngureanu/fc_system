
package kz.tamur.or3.mtszn.birth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.or3.mtszn.dict.Zags;
import kz.tamur.or3.mtszn.natperson.Child;
import kz.tamur.or3.mtszn.natperson.Parent;


/**
 * <p>Java class for BirthAktRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BirthAktRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zags" type="{http://dictionaries.persistence.interactive.nat}Zags" minOccurs="0"/>
 *         &lt;element name="numberAkt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="child" type="{http://person.persistence.interactive.nat}Child" minOccurs="0"/>
 *         &lt;element name="mother" type="{http://person.persistence.interactive.nat}Parent" minOccurs="0"/>
 *         &lt;element name="father" type="{http://person.persistence.interactive.nat}Parent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BirthAktRecord", propOrder = {
    "zags",
    "numberAkt",
    "regDate",
    "child",
    "mother",
    "father"
})
public class BirthAktRecord {

    protected Zags zags;
    protected String numberAkt;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar regDate;
    protected Child child;
    protected Parent mother;
    protected Parent father;

    /**
     * Gets the value of the zags property.
     * 
     * @return
     *     possible object is
     *     {@link Zags }
     *     
     */
    public Zags getZags() {
        return zags;
    }

    /**
     * Sets the value of the zags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Zags }
     *     
     */
    public void setZags(Zags value) {
        this.zags = value;
    }

    /**
     * Gets the value of the numberAkt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberAkt() {
        return numberAkt;
    }

    /**
     * Sets the value of the numberAkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberAkt(String value) {
        this.numberAkt = value;
    }

    /**
     * Gets the value of the regDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegDate() {
        return regDate;
    }

    /**
     * Sets the value of the regDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegDate(XMLGregorianCalendar value) {
        this.regDate = value;
    }

    /**
     * Gets the value of the child property.
     * 
     * @return
     *     possible object is
     *     {@link Child }
     *     
     */
    public Child getChild() {
        return child;
    }

    /**
     * Sets the value of the child property.
     * 
     * @param value
     *     allowed object is
     *     {@link Child }
     *     
     */
    public void setChild(Child value) {
        this.child = value;
    }

    /**
     * Gets the value of the mother property.
     * 
     * @return
     *     possible object is
     *     {@link Parent }
     *     
     */
    public Parent getMother() {
        return mother;
    }

    /**
     * Sets the value of the mother property.
     * 
     * @param value
     *     allowed object is
     *     {@link Parent }
     *     
     */
    public void setMother(Parent value) {
        this.mother = value;
    }

    /**
     * Gets the value of the father property.
     * 
     * @return
     *     possible object is
     *     {@link Parent }
     *     
     */
    public Parent getFather() {
        return father;
    }

    /**
     * Sets the value of the father property.
     * 
     * @param value
     *     allowed object is
     *     {@link Parent }
     *     
     */
    public void setFather(Parent value) {
        this.father = value;
    }

}
