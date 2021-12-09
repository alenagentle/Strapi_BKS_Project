package ru.bcs.creditmarkt.strapi.dto.strapi;

import lombok.*;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
public class BankUnit extends BankUnitParent{
//    private Long id;
//    private String name;
//    private String slug;
//    private String h1;
//    private String address;
//    private String longitude;
//    private String latitude;
//    private String type;
//    private String workingHours;
//    private String telephones;
    private String bankBranch;
//    private String longId;


    @Builder
    public BankUnit(Long id, String name, String slug, String h1, String address, String longitude, String latitude, String type, String workingHours, String telephones, String longId, String bankBranch) {
        super(id, name, slug, h1, address, longitude, latitude, type, workingHours, telephones, longId);
        this.bankBranch = bankBranch;
    }
}
