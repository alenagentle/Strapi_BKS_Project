package ru.bcs.creditmarkt.strapi.dto.wsco.soap;

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
@XmlType(name = "CO", propOrder = {
        "intCode",
        "regNumber"
})
public class CO {

    @XmlElement(name = "IntCode")
    protected String intCode;

    @XmlElement(name = "RegNumber")
    protected String regNumber;

}
