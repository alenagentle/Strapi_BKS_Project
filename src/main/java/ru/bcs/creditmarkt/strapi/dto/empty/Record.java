package ru.bcs.creditmarkt.strapi.dto.empty;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@ToString
@XmlRootElement(name = "Record")
@XmlAccessorType(XmlAccessType.FIELD)
public class Record implements Serializable {

//    @XmlAttribute(name = "ID")
//    private Integer id;
//
//    @XmlAttribute(name = "DU")
//    private String du;

    @XmlElement(name = "ShortName")
    private String shortName;
    @XmlElement(name = "Bic")
    private Long bic;
}
