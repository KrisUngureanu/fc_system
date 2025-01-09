
package kz.tamur.fc.bank.fcredit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="schemaId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "schemaId"
})
@XmlRootElement(name = "GetSchema2")
public class GetSchema2 {

    protected int schemaId;

    /**
     * Gets the value of the schemaId property.
     * 
     */
    public int getSchemaId() {
        return schemaId;
    }

    /**
     * Sets the value of the schemaId property.
     * 
     */
    public void setSchemaId(int value) {
        this.schemaId = value;
    }

}
