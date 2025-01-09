
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FarmInfoGardens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarmInfoGardens">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GardenInfo" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoGarden"/>
 *         &lt;element name="GardenHistoryInfo" type="{http://types.efarm.service.akimat.shep.nit}ArrayOfFarmInfoGarden"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarmInfoGardens", propOrder = {
    "gardenInfo",
    "gardenHistoryInfo"
})
public class FarmInfoGardens {

    @XmlElement(name = "GardenInfo", required = true, nillable = true)
    protected FarmInfoGarden gardenInfo;
    @XmlElement(name = "GardenHistoryInfo", required = true, nillable = true)
    protected ArrayOfFarmInfoGarden gardenHistoryInfo;

    /**
     * Gets the value of the gardenInfo property.
     * 
     * @return
     *     possible object is
     *     {@link FarmInfoGarden }
     *     
     */
    public FarmInfoGarden getGardenInfo() {
        return gardenInfo;
    }

    /**
     * Sets the value of the gardenInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FarmInfoGarden }
     *     
     */
    public void setGardenInfo(FarmInfoGarden value) {
        this.gardenInfo = value;
    }

    /**
     * Gets the value of the gardenHistoryInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarmInfoGarden }
     *     
     */
    public ArrayOfFarmInfoGarden getGardenHistoryInfo() {
        return gardenHistoryInfo;
    }

    /**
     * Sets the value of the gardenHistoryInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarmInfoGarden }
     *     
     */
    public void setGardenHistoryInfo(ArrayOfFarmInfoGarden value) {
        this.gardenHistoryInfo = value;
    }

}
