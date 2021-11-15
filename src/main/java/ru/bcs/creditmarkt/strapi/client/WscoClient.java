package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bcs.creditmarkt.strapi.dto.Envelope;
import ru.bcs.creditmarkt.strapi.dto.empty.BicCode;

@FeignClient(value = "wsco-client", url = "http://cbr.ru")
public interface WscoClient {

    @GetMapping(value = "/scripts/XML_bic.asp",
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    BicCode getCreditOrganizations();

    @PostMapping(value = "/CreditInfoWebServ/CreditOrgInfo.asmx",
            consumes = MediaType.TEXT_XML_VALUE)
    @ResponseBody String getLicenseNo(@RequestBody Envelope envelope);

}
