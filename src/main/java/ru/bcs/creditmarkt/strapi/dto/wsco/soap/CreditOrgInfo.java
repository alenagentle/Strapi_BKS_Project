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
@XmlType(name = "CreditOrgInfo", propOrder = {
        "co"
})
public class CreditOrgInfo {

    @XmlElement(name = "CO")
    protected CO co;
}
