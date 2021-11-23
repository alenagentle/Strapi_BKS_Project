package ru.bcs.creditmarkt.strapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.client.WscoClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.Bank;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.service.BankService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign")
public class FeignController {

    private final StrapiClient strapiClient;
    private final BankService bankService;

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

}

