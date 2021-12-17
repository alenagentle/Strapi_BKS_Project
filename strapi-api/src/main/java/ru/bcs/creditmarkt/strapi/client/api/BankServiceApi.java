package ru.bcs.creditmarkt.strapi.client.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface BankServiceApi {

    @GetMapping("/sync")
    ResponseEntity<String> syncBanks();
}
