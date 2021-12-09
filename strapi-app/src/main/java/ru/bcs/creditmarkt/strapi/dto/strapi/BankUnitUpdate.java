package ru.bcs.creditmarkt.strapi.dto.strapi;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankUnitUpdate {
    private Long id;
    private String name;
    private String slug;
    private String h1;
    private String address;
    private String longitude;
    private String latitude;
    private String type;
    private String workingHours;
    private String telephones;
    private String longId;
}
