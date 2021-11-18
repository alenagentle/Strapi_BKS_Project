package ru.bcs.creditmarkt.strapi.dto;

import lombok.*;

import java.util.Date;

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
    Date published_at;
    Date created_at;
    Date updated_at;
}
