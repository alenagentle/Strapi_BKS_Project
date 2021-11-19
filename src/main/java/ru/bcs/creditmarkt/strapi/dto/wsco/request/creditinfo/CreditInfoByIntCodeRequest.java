package ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo;

import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@ToString
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CreditInfoByIntCodeEx")
public class CreditInfoByIntCodeRequest {

    @XmlAttribute(name = "xmlns")
    private String xmlns="http://web.cbr.ru/";

    @XmlElement(name = "InternalCodes")
    private InternalCodesRequest internalCodesRequest;

}
