package ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@ToString
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
public class BodyIntCodeRequest {
    @XmlElement(name = "BicToIntCode")
    private BicToIntCodeRequest bicToIntCodeRequest;
}
