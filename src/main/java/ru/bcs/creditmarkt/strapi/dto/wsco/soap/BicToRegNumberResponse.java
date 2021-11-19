package ru.bcs.creditmarkt.strapi.dto.wsco.soap;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BicToRegNumberResponse", propOrder = {
        "bicToRegNumberResult"
})
public class BicToRegNumberResponse {

    @XmlElement(name = "BicToRegNumberResult")
    protected String bicToRegNumberResult;

    public String getBicToRegNumberResult() {
        return bicToRegNumberResult;
    }
    public void setBicToRegNumberResult(String value) {
        this.bicToRegNumberResult = value;
    }
}
