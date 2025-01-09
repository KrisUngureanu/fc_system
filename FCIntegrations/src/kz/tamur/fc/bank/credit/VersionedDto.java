
package kz.tamur.fc.bank.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for versionedDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionedDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="optimisticLockVersion" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionedDto", propOrder = {
    "optimisticLockVersion"
})
@XmlSeeAlso({
    SaveResult.class,
    DataSourceDto.class
})
public abstract class VersionedDto {

    protected int optimisticLockVersion;

    /**
     * Gets the value of the optimisticLockVersion property.
     * 
     */
    public int getOptimisticLockVersion() {
        return optimisticLockVersion;
    }

    /**
     * Sets the value of the optimisticLockVersion property.
     * 
     */
    public void setOptimisticLockVersion(int value) {
        this.optimisticLockVersion = value;
    }

}
