package ru.bcs.creditmarkt.strapi.dto.wsco.response;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@Setter
@XmlType
public class Gees {
    @XmlElement(name = "Say", namespace = "http://www.w3.org/2003/05/soap-envelope")
    private String say;
}
