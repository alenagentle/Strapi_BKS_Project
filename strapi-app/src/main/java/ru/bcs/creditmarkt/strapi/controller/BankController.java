package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bcs.creditmarkt.strapi.client.api.BankServiceApi;
import ru.bcs.creditmarkt.strapi.service.BankService;

@RestController
@RequiredArgsConstructor
public class BankController implements BankServiceApi {
    private final BankService bankService;

    @Override
    public ResponseEntity<String> syncBanks() {
        bankService.sync();
        return new ResponseEntity<>("update started", HttpStatus.OK);
    }
}
