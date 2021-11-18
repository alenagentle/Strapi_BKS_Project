package ru.bcs.creditmarkt.strapi.dto.wsco.response;

import lombok.*;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@XmlType(name = "BicToIntCodeResult", propOrder = "bicToIntCodeResult")
//@XmlRootElement(name = "BicToIntCodeResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicToIntCodeResult {

//    @XmlAttribute(name = "xmlns")
//    private String xmlns="http://web.cbr.ru/";

    @XmlElement(name = "BicToIntCodeResult", required = true)
    private String bicToIntCodeResult;
}
