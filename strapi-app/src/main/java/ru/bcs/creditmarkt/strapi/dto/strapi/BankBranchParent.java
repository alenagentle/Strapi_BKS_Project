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
public class BankBranchParent {
    private Long id;
    private String name;
    private String slug;
    private List<City> cities;
    private List<BankUnit> bankUnits;
    private String bic;
    @JsonProperty("published_at")
    private Date publishedAt;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
}
