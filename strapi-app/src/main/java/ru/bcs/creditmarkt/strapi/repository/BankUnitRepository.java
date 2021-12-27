package ru.bcs.creditmarkt.strapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bcs.creditmarkt.strapi.entity.BankUnitEntity;

import java.util.List;

@Repository
public interface BankUnitRepository extends JpaRepository<BankUnitEntity, Long> {

    @Query("SElECT b FROM BankUnitEntity b WHERE b.longId is not null")
    List<BankUnitEntity> findAllBankUnits();

}
