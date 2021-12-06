package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.bcs.creditmarkt.strapi.client.PhrasyClient;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.phrasy.Phrasy;
import ru.bcs.creditmarkt.strapi.dto.strapi.Bank;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.service.BankService;
import ru.bcs.creditmarkt.strapi.utils.Localization;

import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign")
public class FeignController {

    private final StrapiClient strapiClient;
    private final BankService bankService;
    private final PhrasyClient phrasyClient;

    private final ResourceBundle messageBundle = Localization.getMessageBundle();

    //получение филиалов из страпи через strapi клиент
    @GetMapping("/bank-branches")
    public List<BankBranch> getBankBranches() {
        return strapiClient.getBankBranches();
    }

    //получение банков из страпи через strapi клиент
    @GetMapping("/banks")
    public List<Bank> getBanks() {
        return strapiClient.getBanks();
    }

    //обновление данных в страпи из ЦБРФ
    @GetMapping("/sync")
    public void sync() {
        bankService.sync();
    }

//    @GetMapping(value = "/inflect",
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String inflect(@RequestParam String phrase,
//                          @RequestParam String forms) {
//        String res = phrasyClient.getInflectedPhrase(phrase, forms);
//        System.out.println("res = " + res);
//
//        return res;
//
//    }

}

