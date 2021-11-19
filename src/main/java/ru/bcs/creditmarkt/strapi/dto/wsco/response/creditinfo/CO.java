package ru.bcs.creditmarkt.strapi.dto.wsco.response.creditinfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CO", propOrder = {
        "intCode"
})
public class CO {

    @XmlElement(name = "IntCode")
    private String intCode;


}
