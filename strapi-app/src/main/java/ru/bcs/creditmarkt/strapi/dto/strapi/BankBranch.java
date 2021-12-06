package ru.bcs.creditmarkt.strapi.dto.strapi;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("published_at")
    Date publishedAt;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("updated_at")
    Date updatedAt;
}
