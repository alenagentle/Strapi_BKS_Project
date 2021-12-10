package ru.bcs.creditmarkt.strapi.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnitUpdate;

@Mapper(componentModel = "spring")
public abstract class BankUnitMapper {

    @Autowired
    protected StrapiClient strapiClient;

    @Mapping(target = "city", ignore = true)
    public abstract BankUnitUpdate fromBankUnitToBankUnitForRead(BankUnit bankUnit);

    @AfterMapping
    protected void map(@MappingTarget BankUnitUpdate bankUnitUpdate, BankUnit bankUnit) {
        bankUnitUpdate.setCity(strapiClient.getCityById(bankUnit.getCity()));
    }

}
