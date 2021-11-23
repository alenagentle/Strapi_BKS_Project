package ru.bcs.creditmarkt.strapi.dto.strapi;

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
    Date publishedAt;
    Date createdAt;
    Date updatedAt;
}
