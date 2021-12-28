package ru.bcs.creditmarkt.strapi.thread;

import com.poiji.bind.Poiji;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankDictionary;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
import ru.bcs.creditmarkt.strapi.dto.strapi.City;
import ru.bcs.creditmarkt.strapi.entity.BankUnitEntity;
import ru.bcs.creditmarkt.strapi.exception.NotFoundException;
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;
import ru.bcs.creditmarkt.strapi.repository.BankUnitRepository;
import ru.bcs.creditmarkt.strapi.utils.Localization;
import ru.bcs.creditmarkt.strapi.utils.constants.SeparatorConstants;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BankUnitThread implements Runnable {

    private final EntityManager manager;
    private final BankUnitMapper mapper;
    private final StrapiClient strapiClient;
    private final BankUnitRepository bankUnitRepository;
    private final Validator validator;
    private BlockingQueue<Path> fileReferencesQueue;
    private final ResourceBundle messageBundle = Localization.getMessageBundle();
    private final SimpleDateFormat dateFormatWithMin = new SimpleDateFormat(messageBundle.getString("time.format.min"));

    private Set<String> notFoundBanks = new HashSet<>();
    private Set<String> notFoundCities = new HashSet<>();

    public BankUnitThread(BankUnitMapper mapper,
                          StrapiClient strapiClient,
                          Validator validator,
                          BlockingQueue<Path> fileReferencesQueue,
                          EntityManager manager,
                          BankUnitRepository bankUnitRepository) {
        this.mapper = mapper;
        this.strapiClient = strapiClient;
        this.validator = validator;
        this.fileReferencesQueue = fileReferencesQueue;
        this.manager = manager;
        this.bankUnitRepository = bankUnitRepository;
    }


    @Override
    public void run() {
        try {
            Path path;
            while (!((path = fileReferencesQueue.take()).toString().isEmpty())) {
                System.out.println("fileReferencesQueue не пустая");
                log.info(String.format(messageBundle.getString("queue.file"), path.getFileName()));
                manageBankUnits(path);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void manageBankUnits(Path path) {
        System.out.println("manageBankUnits ...");
        List<BankUnit> bankUnits = new ArrayList<>();
        List<BankDictionary> bankDictionaries = readXlsFile(path);
        List<BankUnit> updatedBankUnits = new ArrayList<>();
        filterBankBranches(bankDictionaries, bankUnits);
        System.out.println("bankUnits.size() = " + bankUnits.size() + ", time: "
                + new SimpleDateFormat("yyyy.MM.dd HH:mm:sss").format(new Date()));
        updateBankUnit(bankUnits, updatedBankUnits);
//        System.out.println("updatedBankUnits.size() = " + updatedBankUnits.size()+ ", time: "
//                + new SimpleDateFormat("yyyy.MM.dd HH:mm:sss").format(new Date()));
        bankUnits.removeAll(updatedBankUnits);
        bankUnits.forEach(bankUnit -> bankUnit.setPublishedAt(new Date()));
        bankUnitRepository.saveAll(mapper.formBankUnitsToBankUnitEntities(bankUnits));
        System.out.println("task is done!");
    }

    private void updateBankUnit(List<BankUnit> bankUnits, List<BankUnit> updatedBankUnits) {
        System.out.println("updateBankUnit");

        List<BankUnitEntity> strapiBankUnitList = bankUnitRepository.findAllBankUnits();
        strapiBankUnitList.forEach(strapiBankUnit -> {
            bankUnits.forEach(bankUnit -> {
                if (strapiBankUnit.getLongId().equals(bankUnit.getLongId())) {
                    bankUnit.setId(strapiBankUnit.getId());
                    bankUnit.setPublishedAt(strapiBankUnit.getPublishedAt());
                    bankUnit.setUpdatedAt(new Date());
                    updatedBankUnits.add(bankUnit);
                }
            });
        });
        System.out.println("updatedBankUnits.size() = " + updatedBankUnits.size() + ", time: "
                + new SimpleDateFormat("yyyy.MM.dd HH:mm:sss").format(new Date()));
        bankUnitRepository.saveAll(mapper.formBankUnitsToBankUnitEntities(updatedBankUnits));
    }

    private List<BankDictionary> readXlsFile(Path path) {
        List<BankDictionary> bankDictionaries = new ArrayList<>();
        if (Files.exists(path)) {
            File file = new File(path.toString());
            bankDictionaries = Poiji.fromExcel(file, BankDictionary.class);
            validateBankDictionaries(bankDictionaries);
            System.out.println("bankDictionaries.size() = " + bankDictionaries.size());
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return bankDictionaries;
    }

    private void validateBankDictionaries(List<BankDictionary> bankDictionaries) {
        bankDictionaries.forEach(bankDictionary -> {
            Set<ConstraintViolation<BankDictionary>> violations = validator.validate(bankDictionary);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<BankDictionary> constraintViolation : violations) {
                    sb.append(constraintViolation.getMessage())
                            .append(messageBundle.getString("for.bank"))
                            .append(bankDictionary.getName());
                }
                log.error(sb.toString());
                throw new ConstraintViolationException(sb.toString(), violations);
            }
        });
    }

    private void filterBankBranches(List<BankDictionary> bankDictionaries, List<BankUnit> bankUnits) {
        List<BankBranch> strapiBankBranches = strapiClient.getBankBranches();
        bankDictionaries.forEach(bankDictionary ->
                filterByBic(strapiBankBranches, bankDictionary, bankUnits));
//        notFoundBanks.forEach(log::warn);
//        notFoundCities.forEach(log::warn);
    }

    private void filterByBic(List<BankBranch> strapiBankBranches, BankDictionary bankDictionary, List<BankUnit> bankUnits) {
        AtomicInteger countNotFoundBank = new AtomicInteger();
        strapiBankBranches.forEach(bankBranch -> {
            if (bankBranch.getBic() != null && bankBranch.getBic().equals(bankDictionary.getBic())) {
                bankUnits.add(getBankUnit(bankBranch, bankDictionary));
            } else
                filterByBankName(bankBranch, bankDictionary, bankUnits, countNotFoundBank);
        });
        if (countNotFoundBank.intValue() == strapiBankBranches.size())
            notFoundBanks.add(String.format(messageBundle.getString("bank.notFoundWithName"), bankDictionary.getName()));
    }

    private void filterByBankName(BankBranch bankBranch, BankDictionary bankDictionary,
                                  List<BankUnit> bankUnits, AtomicInteger countNotFoundBank) {
        if (StringUtils.toRootLowerCase(bankDictionary.getName())
                .equals(StringUtils.toRootLowerCase(bankBranch.getBank().getName()))) {
            filterByCity(bankDictionary, bankBranch, bankUnits);
        } else
            countNotFoundBank.getAndIncrement();
    }

    private void filterByCity(BankDictionary bankDictionary, BankBranch bankBranch, List<BankUnit> bankUnits) {
        AtomicInteger countNotFoundCity = new AtomicInteger();
        bankBranch.getCities().forEach(city -> {
            if (StringUtils.toRootLowerCase(city.getName())
                    .equals(StringUtils.toRootLowerCase(bankDictionary.getCity()))) {
                bankUnits.add(getBankUnit(bankBranch, bankDictionary));
            } else
                countNotFoundCity.getAndIncrement();
        });
        if (countNotFoundCity.intValue() == bankBranch.getCities().size()) {
            notFoundCities.add(String.format(messageBundle.getString("bank.notFoundInTown"), bankDictionary.getName(), bankDictionary.getCity()));
        }
    }

    private void filterByDate(BankDictionary bankDictionary, BankBranch bankBranch, List<BankUnit> bankUnits) {
        if (dateFormatWithMin.format(bankBranch.getCreatedAt()).equals(dateFormatWithMin.format(bankBranch.getUpdatedAt()))) {
            bankUnits.add(getBankUnit(bankBranch, bankDictionary));
        }
    }

    private BankUnit getBankUnit(BankBranch bankBranch, BankDictionary bankDictionary) {
        String atmType = StringUtils.toRootLowerCase(messageBundle.getString("atm"));
        StringBuilder address = new StringBuilder(bankDictionary.getCity());
        address.append(", ");
        String[] addressList = bankDictionary.getAddress().split(",");
        if (SeparatorConstants.ADDRESS_STREET < addressList.length)
            address.append(addressList[SeparatorConstants.ADDRESS_STREET]);
        if (SeparatorConstants.ADDRESS_STREET_NUMBER < addressList.length)
            address.append(addressList[SeparatorConstants.ADDRESS_STREET_NUMBER]);

        City city =
                bankBranch.getCities().stream()
                        .filter(strapiCity -> StringUtils.toRootLowerCase(strapiCity.getName())
                                .equals(StringUtils.toRootLowerCase(bankDictionary.getCity())))
                        .findFirst().orElseThrow(() -> new NotFoundException(String.format(messageBundle
                                .getString("town.notFound"), bankDictionary.getCity())));

        StringBuilder workHours = new StringBuilder("");
        if (bankDictionary.getWorkingHours() != null) {
            String[] workHoursArray = bankDictionary.getWorkingHours().split(";");
            Arrays.stream(workHoursArray)
                    .forEach(time -> workHours.append(time).append("\n"));
        }

        return BankUnit.builder()
                .name(bankDictionary.getName())
                .h1(bankDictionary.getH1())
                .slug(bankDictionary.getSlug())
                .address(address.toString())
                .latitude(bankDictionary.getLatitude().split(" ")[SeparatorConstants.LATITUDE])
                .longitude(bankDictionary.getLongitude().split(" ")[SeparatorConstants.LONGITUDE])
                .type(StringUtils.toRootLowerCase(bankDictionary.getSubheading())
                        .contains(atmType) ? "atm" : "branch")
                .workingHours(bankDictionary.getWorkingHours())
                .workHours(workHours.toString())
                .telephones(bankDictionary.getTelephones())
                .bankBranch(bankBranch.getId())
                .city(city.getId())
                .longId(bankDictionary.getId())
                .refill(bankDictionary.isRefill())
                .cashReceipt(bankDictionary.isCashReceipt())
                .build();
    }

}
