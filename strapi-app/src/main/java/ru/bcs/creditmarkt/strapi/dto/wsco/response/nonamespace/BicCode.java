package ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@XmlRootElement(name = "BicCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicCode implements Serializable {

    @XmlElement(name = "Record")
    private List<CreditOrganization> creditOrganizations;
}
