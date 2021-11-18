package ru.bcs.creditmarkt.strapi.dto.wsco.soap;


import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BicToIntCode complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BicToIntCode"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BicCode" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BicToIntCode", propOrder = {
        "bicCode"
})
public class BicToIntCode {

    @XmlElement(name = "BicCode")
    protected int bicCode;

    /**
     * Gets the value of the bicCode property.
     *
     */
    public int getBicCode() {
        return bicCode;
    }

    /**
     * Sets the value of the bicCode property.
     *
     */
    public void setBicCode(int value) {
        this.bicCode = value;
    }

}
