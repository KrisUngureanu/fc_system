
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paginationSupportSearchParam complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paginationSupportSearchParam">
 *   &lt;complexContent>
 *     &lt;extension base="{http://data.chdb.scb.kz}excelFilterObject">
 *       &lt;sequence>
 *         &lt;element name="paginationSupportDto" type="{http://data.chdb.scb.kz}paginationSupportDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paginationSupportSearchParam", propOrder = {
    "paginationSupportDto"
})
@XmlSeeAlso({
    BatchPackageParams.class,
    BatchFileParams.class,
    BatchComplexParams.class
})
public abstract class PaginationSupportSearchParam
    extends ExcelFilterObject
{

    protected PaginationSupportDto paginationSupportDto;

    /**
     * Gets the value of the paginationSupportDto property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationSupportDto }
     *     
     */
    public PaginationSupportDto getPaginationSupportDto() {
        return paginationSupportDto;
    }

    /**
     * Sets the value of the paginationSupportDto property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationSupportDto }
     *     
     */
    public void setPaginationSupportDto(PaginationSupportDto value) {
        this.paginationSupportDto = value;
    }

}
