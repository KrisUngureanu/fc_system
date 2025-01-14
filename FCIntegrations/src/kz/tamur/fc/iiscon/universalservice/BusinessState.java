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
 * <p>Java class for BusinessState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BusinessState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WAIT_THEORY"/>
 *     &lt;enumeration value="THEORY"/>
 *     &lt;enumeration value="CITIZEN_CAME"/>
 *     &lt;enumeration value="EXECUTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BusinessState")
@XmlEnum
public enum BusinessState {


    /**
     * Ожидает сдачи экзамена ПДД
     * 
     */
    WAIT_THEORY,

    /**
     * Сдача экзамена ПДД
     * 
     */
    THEORY,

    /**
     * Обращение гражданина
     * 
     */
    CITIZEN_CAME,

    /**
     * Исполнение заявки
     * 
     */
    EXECUTE;

    public String value() {
        return name();
    }

    public static BusinessState fromValue(String v) {
        return valueOf(v);
    }

}
