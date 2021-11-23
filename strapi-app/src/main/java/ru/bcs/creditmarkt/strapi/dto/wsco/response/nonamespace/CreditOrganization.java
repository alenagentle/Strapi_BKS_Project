package ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@XmlRootElement(name = "Record")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditOrganization implements Serializable {
    @XmlElement(name = "ShortName")
    private String shortName;
    @XmlElement(name = "Bic")
    private String bic;
}
