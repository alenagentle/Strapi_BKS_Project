package ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@ToString
@Setter
@XmlRootElement(name = "InternalCodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class InternalCodesRequest {

    @XmlElement(name = "double")
    private String intCode;

}
