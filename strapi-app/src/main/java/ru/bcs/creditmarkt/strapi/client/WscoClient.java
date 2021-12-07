package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.creditinfo.EnvelopCreditInfoByIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.intcode.EnvelopBicToIntCodeRequest;
import ru.bcs.creditmarkt.strapi.dto.wsco.request.regnumber.EnvelopeBicToRegNumber;
import ru.bcs.creditmarkt.strapi.dto.wsco.response.nonamespace.BicCode;

@FeignClient(name = "${partnerCardService.wscoClient}",
        url = "${partnerCardService.wscoUrl}")
public interface WscoClient {

    //KB-10053
    //получение имени и Бик кредитных организаций из ЦБ
    @GetMapping(value = "/scripts/XML_bic.asp",
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    BicCode getCreditOrganizations();

    //KB-10053
    //получение регистрационного номера по бик-у из ЦБ
    @PostMapping(value = "/CreditInfoWebServ/CreditOrgInfo.asmx",
            consumes = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    String bicToRegNumber(@RequestBody EnvelopeBicToRegNumber envelopeBicToRegNumber);

    //KB-10053
    //получение внутреннего кода по бик-у из ЦБ
    @PostMapping(value = "/CreditInfoWebServ/CreditOrgInfo.asmx",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    String bicToIntCode(@RequestBody EnvelopBicToIntCodeRequest envelopBicToIntCodeRequest);

    //KB-10053
    //получение информации о кредитной организации по внутреннему коду
    @PostMapping(value = "/CreditInfoWebServ/CreditOrgInfo.asmx",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    String creditInfoByIntCode(EnvelopCreditInfoByIntCodeRequest envelop);
}
