package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bcs.creditmarkt.strapi.config.FeignConfig;
import ru.bcs.creditmarkt.strapi.dto.phrasy.Phrasy;

@FeignClient(name = "phrasyClient",
        url = "${partnerCardService.phrasyUrl}",
        configuration = FeignConfig.class)
public interface PhrasyClient {

    @GetMapping(value = "/inflect",
            produces = MediaType.APPLICATION_JSON_VALUE)
    Phrasy getInflectedPhrase(@RequestParam String phrase,
                              @RequestParam String forms);

}
