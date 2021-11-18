package ru.bcs.creditmarkt.strapi.dto.empty;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@XmlRootElement(name = "BicCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicCode implements Serializable {
//    @XmlAttribute
//    private String name;

    @XmlElement(name = "Record")
    private List<CreditOrganization> creditOrganizations;
}
