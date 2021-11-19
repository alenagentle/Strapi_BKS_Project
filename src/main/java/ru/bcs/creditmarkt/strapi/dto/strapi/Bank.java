package ru.bcs.creditmarkt.strapi.dto.strapi;

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
    String authorizedCapital;
    String systemParticipation;
    String isLicenseActive;
    String leadership;
    Date published_at;
    Date created_at;
    Date updated_at;
}
