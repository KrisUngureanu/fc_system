//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.07 at 12:27:01 PM ALMT 
//


package kz.tamur.shep.sed.info;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NEW"/>
 *     &lt;enumeration value="MATCHES"/>
 *     &lt;enumeration value="CONFIRMED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentStatus", namespace = "http://egov.bee.kz/eds/scanneddocuments/info/v1/")
@XmlEnum
public enum DocumentStatus {

    NEW,
    MATCHES,
    CONFIRMED;

    public String value() {
        return name();
    }

    public static DocumentStatus fromValue(String v) {
        return valueOf(v);
    }

}
