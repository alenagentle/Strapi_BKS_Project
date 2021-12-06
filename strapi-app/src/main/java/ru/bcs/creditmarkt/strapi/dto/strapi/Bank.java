package ru.bcs.creditmarkt.strapi.dto.strapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bank {
    Long id;
    String name;
    String description;
    String slug;
    String bic;
    String cbrId;
    String licenseNumber;
    String letterBank;
    String legalAddress;
    String telephones;
    String registrationDate;
    String headOffice;
    String realAddress;
    Integer authorizedCapital;
    String systemParticipation;
    Boolean isLicenseActive;
    String leadership;
    @JsonProperty("published_at")
    Date publishedAt;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("updated_at")
    Date updatedAt;
    String name_ablt;
    String name_gent;
    String name_loct;
    String name_datv;
    String name_accs;
}
