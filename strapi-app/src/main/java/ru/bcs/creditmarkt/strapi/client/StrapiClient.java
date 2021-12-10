package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.bcs.creditmarkt.strapi.config.LogConfig;
import ru.bcs.creditmarkt.strapi.dto.strapi.*;

import java.util.List;

@FeignClient(name = "${partnerCardService.strapiClient}",
        url = "${partnerCardService.strapiUrl}",
        configuration = LogConfig.class)
public interface StrapiClient {

    @GetMapping("/bank-branches")
    List<BankBranch> getBankBranches();

    @GetMapping("/banks")
    List<Bank> getBanks();

    @GetMapping("/cities/{id}")
    City getCityById(@PathVariable("id") Long id);

    @PutMapping("/bank-branches/{id}")
    BankBranch updateBankBranches(@PathVariable("id") Long id, @RequestBody BankBranch bankBranch);

    @GetMapping("/bank-units")
    List<BankUnitUpdate> getBankUnits();

    @PostMapping("/bank-units")
    BankBranch createBankUnit(@RequestBody BankUnit bankUnit);

    @PutMapping("/bank-units/{id}")
    BankUnitUpdate updateBankUnit(@PathVariable("id") Long id, @RequestBody BankUnitUpdate bankUnit);

    @PutMapping("banks/{id}")
    Bank updateBank(@PathVariable("id") Long id, @RequestBody Bank bank);
}
