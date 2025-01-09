
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06555ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06555ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="lessons" type="{http://services.sync.mtszn/}lesson" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vacancies" type="{http://services.sync.mtszn/}vacancy" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06555ResponseData", propOrder = {
    "lessons",
    "vacancies"
})
public class P06555ResponseData
    extends BaseResponseData
{

    @XmlElement(nillable = true)
    protected List<Lesson> lessons;
    @XmlElement(nillable = true)
    protected List<Vacancy> vacancies;

    /**
     * Gets the value of the lessons property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lessons property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLessons().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Lesson }
     * 
     * 
     */
    public List<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<Lesson>();
        }
        return this.lessons;
    }

    /**
     * Gets the value of the vacancies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vacancies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVacancies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Vacancy }
     * 
     * 
     */
    public List<Vacancy> getVacancies() {
        if (vacancies == null) {
            vacancies = new ArrayList<Vacancy>();
        }
        return this.vacancies;
    }

}
