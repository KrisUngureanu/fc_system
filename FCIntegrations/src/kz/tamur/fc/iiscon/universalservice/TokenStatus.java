//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TokenStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TokenStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WAITING"/>
 *     &lt;enumeration value="CALLED"/>
 *     &lt;enumeration value="STARTED"/>
 *     &lt;enumeration value="ENDED"/>
 *     &lt;enumeration value="MISSED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TokenStatus")
@XmlEnum
public enum TokenStatus {

    WAITING,
    CALLED,
    STARTED,
    ENDED,
    MISSED;

    public String value() {
        return name();
    }

    public static TokenStatus fromValue(String v) {
        return valueOf(v);
    }

}
