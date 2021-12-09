package ru.bcs.creditmarkt.strapi.dto.strapi;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class BankUnit extends BankUnitParent{

    private String bankBranch;

    @Builder
    public BankUnit(Long id, String name, String slug, String h1, String address, String longitude, String latitude, String type, String workingHours, String telephones, String longId, String bankBranch) {
        super(id, name, slug, h1, address, longitude, latitude, type, workingHours, telephones, longId);
        this.bankBranch = bankBranch;
    }
}
