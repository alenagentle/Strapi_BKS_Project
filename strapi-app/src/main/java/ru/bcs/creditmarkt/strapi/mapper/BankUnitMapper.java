package ru.bcs.creditmarkt.strapi.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnitUpdate;
import ru.bcs.creditmarkt.strapi.entity.BankUnitEntity;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class BankUnitMapper {

    @Autowired
    protected StrapiClient strapiClient;

    @Mapping(target = "city", ignore = true)
    public abstract BankUnitUpdate fromBankUnitToBankUnitForRead(BankUnit bankUnit);

    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    public abstract BankUnitEntity fromBankUnitToBankUnitEntity(BankUnit bankUnit);

    public abstract List<BankUnitEntity> formBankUnitsToBankUnitEntities(List<BankUnit> bankUnits);

    @AfterMapping
    protected void map(@MappingTarget BankUnitUpdate bankUnitUpdate, BankUnit bankUnit) {
        bankUnitUpdate.setCity(strapiClient.getCityById(bankUnit.getCity()));
    }

    @AfterMapping
    protected  void map(@MappingTarget BankUnitEntity bankUnitEntity, BankUnit bankUnit) {
        bankUnitEntity.setLatitude(Double.parseDouble(bankUnit.getLatitude()));
        bankUnitEntity.setLongitude(Double.parseDouble(bankUnit.getLongitude()));
    }

}
