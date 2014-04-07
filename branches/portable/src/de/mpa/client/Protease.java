
package de.mpa.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for protease.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="protease">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TRYPSIN"/>
 *     &lt;enumeration value="SEMI_TRYPTIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "protease")
@XmlEnum
public enum Protease {

    TRYPSIN,
    SEMI_TRYPTIC;

    public String value() {
        return name();
    }

    public static Protease fromValue(String v) {
        return valueOf(v);
    }

}
