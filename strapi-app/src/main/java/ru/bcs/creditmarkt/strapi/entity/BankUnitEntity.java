package ru.bcs.creditmarkt.strapi.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bank_units")
@Getter
@Setter
@ToString
public class BankUnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "\"longId\"")
    private String longId;

    private String name;

    private String slug;

    private String h1;

    private String address;

    private Double longitude;

    private Double latitude;

    private String type;

    @Column(name = "\"workingHours\"")
    private String workingHours;

    @Column(name = "\"workHours\"")
    private String workHours;

    private String telephones;

    private Long city;

    @Column(name = "\"bankBranch\"")
    private Long bankBranch;

    private boolean refill;

    @Column(name = "\"cashReceipt\"")
    private boolean cashReceipt;

    @Column(name = "published_at")
    private Date publishedAt;

    @Column(name = "updated_at")
    private Date updatedAt;

}
