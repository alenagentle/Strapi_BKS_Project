package ru.bcs.creditmarkt.strapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bank {
    Long id;
    String  name;
    String description;
    String slug;
    String letterBank;
    String legalAddress;
}
