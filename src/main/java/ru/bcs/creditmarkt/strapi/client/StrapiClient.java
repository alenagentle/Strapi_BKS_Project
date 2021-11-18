package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.bcs.creditmarkt.strapi.dto.Bank;
import ru.bcs.creditmarkt.strapi.dto.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.BankUnit;

import java.util.List;

@FeignClient(name="${partnerCardService.name:partnerCardService}", url="${partnerCardService.strapiUrl}")
public interface StrapiClient {

    @GetMapping("/bank-branches")
    List<BankBranch> getBankBranches();

    @GetMapping("/banks")
    List<Bank> getBanks();

    @PutMapping("/bank-branches/{id}")
    BankBranch updateBankBranches(@PathVariable Long id, @RequestBody BankBranch bankBranch);

    @PostMapping("/bank-units")
    BankBranch createBankUnit(@RequestBody BankUnit bankUnit);
}
