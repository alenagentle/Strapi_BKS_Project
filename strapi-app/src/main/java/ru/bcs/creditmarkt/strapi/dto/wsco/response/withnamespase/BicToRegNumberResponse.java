package ru.bcs.creditmarkt.strapi.dto.wsco.response.withnamespase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BicToRegNumberResponse", propOrder = {
        "bicToRegNumberResult"
})
public class BicToRegNumberResponse {

    @XmlElement(name = "BicToRegNumberResult")
    protected String bicToRegNumberResult;

}
