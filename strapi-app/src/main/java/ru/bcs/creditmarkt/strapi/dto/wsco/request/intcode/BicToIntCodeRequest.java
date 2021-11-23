package ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@ToString
@Setter
@XmlRootElement(name = "BicToIntCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicToIntCodeRequest {

    @XmlAttribute(name = "xmlns")
    private String xmlns="http://web.cbr.ru/";

    @XmlElement(name = "BicCode")
    private String bicCode;
}
