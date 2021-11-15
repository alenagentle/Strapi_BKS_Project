package ru.bcs.creditmarkt.strapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BankUnit {
    Long id;
    String name;
    String slug;
    String h1;
    String address;
    String longitude;
    String latitude;
    String type;
    String workingHours;
    String telephones;
    String bankBranch;
}
