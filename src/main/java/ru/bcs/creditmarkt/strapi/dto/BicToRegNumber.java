package ru.bcs.creditmarkt.strapi.dto;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@ToString
@Setter
@XmlRootElement(name = "BicToRegNumber")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicToRegNumber {

    @XmlAttribute(name = "xmlns")
    private String xmlns="http://web.cbr.ru/";

    @XmlElement(name = "BicCode")
    private String bicCode;
}
