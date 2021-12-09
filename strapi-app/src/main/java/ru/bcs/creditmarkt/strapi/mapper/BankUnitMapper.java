package ru.bcs.creditmarkt.strapi.mapper;

import org.mapstruct.Mapper;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnitUpdate;

@Mapper(componentModel = "spring")
public interface BankUnitMapper {

    BankUnitUpdate fromBankUnitToBankUnitForRead(BankUnit bankUnit);

}
