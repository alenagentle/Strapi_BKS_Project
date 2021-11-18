package ru.bcs.creditmarkt.strapi.dto.wsco.soap;


import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BicToIntCodeResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BicToIntCodeResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BicToIntCodeResult" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
@XmlType(name = "BicToIntCodeResponse", propOrder = {
        "bicToIntCodeResult"
})
public class BicToIntCodeResponse {

    @XmlElement(name = "BicToIntCodeResult")
    protected int bicToIntCodeResult;

    /**
     * Gets the value of the bicToIntCodeResult property.
     *
     */
    public int getBicToIntCodeResult() {
        return bicToIntCodeResult;
    }

    /**
     * Sets the value of the bicToIntCodeResult property.
     *
     */
    public void setBicToIntCodeResult(int value) {
        this.bicToIntCodeResult = value;
    }

}
