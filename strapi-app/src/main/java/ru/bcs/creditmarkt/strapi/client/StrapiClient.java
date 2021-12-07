package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.bcs.creditmarkt.strapi.config.FeignConfig;
import ru.bcs.creditmarkt.strapi.dto.strapi.Bank;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;

import java.util.List;

@FeignClient(name = "${partnerCardService.strapiClient}",
        url = "${partnerCardService.strapiUrl}")
public interface StrapiClient {

    @GetMapping("/bank-branches")
    List<BankBranch> getBankBranches();

    @GetMapping("/banks")
    List<Bank> getBanks();

    @PutMapping("/bank-branches/{id}")
    BankBranch updateBankBranches(@PathVariable("id") Long id, @RequestBody BankBranch bankBranch);

    @PostMapping("/bank-units")
    BankBranch createBankUnit(@RequestBody BankUnit bankUnit);

    @PutMapping("banks/{id}")
    Bank updateBank(@PathVariable("id") Long id, @RequestBody Bank bank);
}
