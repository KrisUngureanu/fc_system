
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FarmInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarmInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Patronymic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Birthday" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="NatureZoneKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NatureZoneRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Animals" type="{http://types.efarm.service.akimat.shep.nit}ArrayOfFarmInfoAnimals"/>
 *         &lt;element name="Gardens" type="{http://types.efarm.service.akimat.shep.nit}ArrayOfFarmInfoGardens"/>
 *         &lt;element name="FamilyMembers" type="{http://types.efarm.service.akimat.shep.nit}ArrayOfFarmInfoFamilyMember"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarmInfoResponse", propOrder = {
    "resultCode",
    "iin",
    "surname",
    "name",
    "patronymic",
    "birthday",
    "natureZoneKz",
    "natureZoneRu",
    "animals",
    "gardens",
    "familyMembers"
})
public class FarmInfoResponse {

    @XmlElement(name = "ResultCode", required = true, nillable = true)
    protected String resultCode;
    @XmlElement(name = "Iin", required = true, nillable = true)
    protected String iin;
    @XmlElement(name = "Surname", required = true, nillable = true)
    protected String surname;
    @XmlElement(name = "Name", required = true, nillable = true)
    protected String name;
    @XmlElement(name = "Patronymic", required = true, nillable = true)
    protected String patronymic;
    @XmlElement(name = "Birthday", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthday;
    @XmlElement(name = "NatureZoneKz", required = true, nillable = true)
    protected String natureZoneKz;
    @XmlElement(name = "NatureZoneRu", required = true, nillable = true)
    protected String natureZoneRu;
    @XmlElement(name = "Animals", required = true, nillable = true)
    protected ArrayOfFarmInfoAnimals animals;
    @XmlElement(name = "Gardens", required = true, nillable = true)
    protected ArrayOfFarmInfoGardens gardens;
    @XmlElement(name = "FamilyMembers", required = true, nillable = true)
    protected ArrayOfFarmInfoFamilyMember familyMembers;

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIin() {
        return iin;
    }

    /**
     * Sets the value of the iin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIin(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the patronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatronymic() {
        return patronymic;
    }

    /**
     * Sets the value of the patronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatronymic(String value) {
        this.patronymic = value;
    }

    /**
     * Gets the value of the birthday property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthday() {
        return birthday;
    }

    /**
     * Sets the value of the birthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthday(XMLGregorianCalendar value) {
        this.birthday = value;
    }

    /**
     * Gets the value of the natureZoneKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNatureZoneKz() {
        return natureZoneKz;
    }

    /**
     * Sets the value of the natureZoneKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatureZoneKz(String value) {
        this.natureZoneKz = value;
    }

    /**
     * Gets the value of the natureZoneRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNatureZoneRu() {
        return natureZoneRu;
    }

    /**
     * Sets the value of the natureZoneRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatureZoneRu(String value) {
        this.natureZoneRu = value;
    }

    /**
     * Gets the value of the animals property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarmInfoAnimals }
     *     
     */
    public ArrayOfFarmInfoAnimals getAnimals() {
        return animals;
    }

    /**
     * Sets the value of the animals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarmInfoAnimals }
     *     
     */
    public void setAnimals(ArrayOfFarmInfoAnimals value) {
        this.animals = value;
    }

    /**
     * Gets the value of the gardens property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarmInfoGardens }
     *     
     */
    public ArrayOfFarmInfoGardens getGardens() {
        return gardens;
    }

    /**
     * Sets the value of the gardens property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarmInfoGardens }
     *     
     */
    public void setGardens(ArrayOfFarmInfoGardens value) {
        this.gardens = value;
    }

    /**
     * Gets the value of the familyMembers property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarmInfoFamilyMember }
     *     
     */
    public ArrayOfFarmInfoFamilyMember getFamilyMembers() {
        return familyMembers;
    }

    /**
     * Sets the value of the familyMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarmInfoFamilyMember }
     *     
     */
    public void setFamilyMembers(ArrayOfFarmInfoFamilyMember value) {
        this.familyMembers = value;
    }

}
