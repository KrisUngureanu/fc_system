
package kz.tamur.or3.mtszn.epay;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountCreditingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccountCreditingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Card"/>
 *     &lt;enumeration value="Account"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccountCreditingType")
@XmlEnum
public enum AccountCreditingType {

    @XmlEnumValue("Card")
    CARD("Card"),
    @XmlEnumValue("Account")
    ACCOUNT("Account");
    private final String value;

    AccountCreditingType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountCreditingType fromValue(String v) {
        for (AccountCreditingType c: AccountCreditingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
