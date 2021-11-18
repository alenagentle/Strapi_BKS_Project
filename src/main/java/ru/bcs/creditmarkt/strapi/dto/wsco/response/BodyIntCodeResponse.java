package ru.bcs.creditmarkt.strapi.dto.wsco.response;

import lombok.*;

import javax.xml.bind.annotation.*;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BodyIntCodeResponse", propOrder = "bicToIntCodeResult")
//@XmlRootElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
public class BodyIntCodeResponse {
//    @XmlAttribute(name = "xmlns")
//    private String xmlns;

    @XmlElement(name = "BicToIntCodeResult")
    private BicToIntCodeResult bicToIntCodeResult;
}
