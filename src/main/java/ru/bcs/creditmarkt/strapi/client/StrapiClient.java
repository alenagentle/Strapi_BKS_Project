package ru.bcs.creditmarkt.strapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.bcs.creditmarkt.strapi.dto.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.BankUnit;

import java.util.List;

@FeignClient(value = "strapi-client", url = "localhost:1337")
public interface StrapiClient {

    @GetMapping("/bank-branches")
    List<BankBranch> getBankBranches();

    @PutMapping("/bank-branches/{id}")
    BankBranch updateBankBranches(@PathVariable Long id, @RequestBody BankBranch bankBranch);

    @PostMapping("/bank-units")
    BankBranch createBankUnit(@RequestBody BankUnit bankUnit);
}
