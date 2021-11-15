package ru.bcs.creditmarkt.strapi.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankBranch {
    Long id;
    String name;
    String slug;
    Bank bank;
    List<City> cities;
    List<BankUnit> bankUnits;
    String bic;
    Date published_at;
    Date created_at;
    Date updated_at;
}
