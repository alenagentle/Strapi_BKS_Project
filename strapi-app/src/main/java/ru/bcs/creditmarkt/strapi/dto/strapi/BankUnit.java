package ru.bcs.creditmarkt.strapi.dto.strapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BankUnit{
    private Long id;
    private String name;
    private String slug;
    private String h1;
    private String address;
    private String longitude;
    private String latitude;
    private String type;
    private String workingHours;
    private String workHours;
    private String telephones;
    private String longId;
    private Long bankBranch;
    private Long city;
    private boolean refill;
    private boolean cashReceipt;
    private Date publishedAt;
    private Date updatedAt;
}