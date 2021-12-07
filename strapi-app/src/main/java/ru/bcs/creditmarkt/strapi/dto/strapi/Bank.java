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
    private Long id;
    private String name;
    private String description;
    private String slug;
    private String bic;
    private String cbrId;
    private String licenseNumber;
    private String letterBank;
    private String legalAddress;
    private String telephones;
    private String registrationDate;
    private String headOffice;
    private String realAddress;
    private Integer authorizedCapital;
    private String systemParticipation;
    private Boolean isLicenseActive;
    private String leadership;
    @JsonProperty("published_at")
    private Date publishedAt;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
    private String name_ablt;
    private String name_gent;
    private String name_loct;
    private String name_datv;
    private String name_accs;
    private Meta meta;
}
