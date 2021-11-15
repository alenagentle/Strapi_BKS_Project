package ru.bcs.creditmarkt.strapi.dto;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@ToString
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
public class Envelope {
    @XmlElement(name = "Body", required = true, namespace = "http://www.w3.org/2003/05/soap-envelope")
    private Body body;
}
