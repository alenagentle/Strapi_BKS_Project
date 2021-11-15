package ru.bcs.creditmarkt.strapi.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BicToIntCodeResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class LicenseNoResponse {
    @XmlElement(name = "BicToIntCodeResponse")
    private String bicToIntCodeResponse;
    @XmlElement(name = "BicToIntCodeResult")
    private String bicToIntCodeResult;
}
