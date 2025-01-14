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
 * <p>Java class for IdentityDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="IdentityDocumentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PASSPORT"/>
 *     &lt;enumeration value="IDENTITY_CARD"/>
 *     &lt;enumeration value="TEMPORARY_CERTIFICATE"/>
 *     &lt;enumeration value="RESIDENCE_PERMIT"/>
 *     &lt;enumeration value="FOREIGN_STATE_DOCUMENT"/>
 *     &lt;enumeration value="REFUGEE_CARD"/>
 *     &lt;enumeration value="STATELESS_PERSON"/>
 *     &lt;enumeration value="REGISTRATION_CERTIFICATE"/>
 *     &lt;enumeration value="BIRTH_CERTIFICATE"/>
 *     &lt;enumeration value="ORALMAN_IDENTITY_CARD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "IdentityDocumentType")
@XmlEnum
public enum IdentityDocumentType {


    /**
     * Паспорт
     * 
     */
    PASSPORT,

    /**
     * Удостоверение личности гражданина РК
     * 
     */
    IDENTITY_CARD,

    /**
     * Временное удостоверение личности
     * 
     */
    TEMPORARY_CERTIFICATE,

    /**
     * Вид на жительство
     * 
     */
    RESIDENCE_PERMIT,

    /**
     * Документ иностранного государства
     * 
     */
    FOREIGN_STATE_DOCUMENT,

    /**
     * Удостоверение беженца
     * 
     */
    REFUGEE_CARD,

    /**
     * Удостоверение лица без гражданства РК
     * 
     */
    STATELESS_PERSON,

    /**
     * Регистрационное свидетельство
     * 
     */
    REGISTRATION_CERTIFICATE,

    /**
     * Свидетельство о рождении
     * 
     */
    BIRTH_CERTIFICATE,

    /**
     * Удостоверение орлмана
     * 
     */
    ORALMAN_IDENTITY_CARD;

    public String value() {
        return name();
    }

    public static IdentityDocumentType fromValue(String v) {
        return valueOf(v);
    }

}
