package ru.bcs.creditmarkt.strapi.dto.wsco.response;

import lombok.*;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
//@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlType(name = "EnvelopBicToIntCodeResponse", propOrder = "body")
@XmlAccessorType(XmlAccessType.NONE)
public class EnvelopBicToIntCodeResponse {

    @XmlElement(name="Body")
    private BodyIntCodeResponse body;
}
