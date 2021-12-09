package ru.bcs.creditmarkt.strapi.dto.strapi;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Meta {
    private String title;
    private String ogTitle;
    private String ogDescription;
    private String description;
}
