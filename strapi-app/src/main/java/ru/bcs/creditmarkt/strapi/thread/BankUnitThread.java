package ru.bcs.creditmarkt.strapi.thread;

import com.poiji.bind.Poiji;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.*;
import ru.bcs.creditmarkt.strapi.exception.NotFoundException;
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;
import ru.bcs.creditmarkt.strapi.utils.Localization;
import ru.bcs.creditmarkt.strapi.utils.constants.SeparatorConstants;
import ru.bcs.creditmarkt.strapi.utils.constants.SortConstants;

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

    private final BankUnitMapper mapper;
    private final StrapiClient strapiClient;
    private final Validator validator;
    private BlockingQueue<Path> fileReferencesQueue;

    private final ResourceBundle messageBundle = Localization.getMessageBundle();
    private final SimpleDateFormat dateFormatWithMin = new SimpleDateFormat(messageBundle.getString("time.format.min"));

    private Set<String> notFoundBanks = new HashSet<>();
    private Set<String> notFoundCities = new HashSet<>();

    public BankUnitThread(BankUnitMapper mapper,
                          StrapiClient strapiClient,
                          Validator validator,
                          BlockingQueue<Path> fileReferencesQueue) {
        this.mapper = mapper;
        this.strapiClient = strapiClient;
        this.validator = validator;
        this.fileReferencesQueue = fileReferencesQueue;
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

    private void manageBankUnits(Path path) {
        System.out.println("manageBankUnits ...");
        List<BankUnit> bankUnits = new ArrayList<>();
        List<BankDictionary> bankDictionaries = readXlsFile(path);
        Set<BankUnit> updatedBankUnits = new HashSet<>();
        filterBankBranches(bankDictionaries, bankUnits);
        updateBankUnit(bankUnits, updatedBankUnits);
        bankUnits.removeAll(updatedBankUnits);
        bankUnits.forEach(strapiClient::createBankUnit);
        System.out.println("task is done!");
    }

    private void updateBankUnit(List<BankUnit> bankUnits, Set<BankUnit> updatedBankUnits) {
        int limit = 500;
        int startPosition = 0;
        List<BankUnitUpdate> bankUnitUpdatePaginationList = strapiClient
                .getPaginationBankUnits(limit, startPosition, SortConstants.SORT_ID);
        while (bankUnitUpdatePaginationList.size() > 0) {
//            System.out.println();
            System.out.println("startPosition = " + startPosition + ", time: "
                    + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

            bankUnitUpdatePaginationList.forEach(strapiBankUnit -> {
                bankUnits.forEach(bankUnit -> {
                    if (strapiBankUnit.getLongId() != null && strapiBankUnit.getLongId().equals(bankUnit.getLongId())) {
                        strapiClient.updateBankUnit(strapiBankUnit.getId(), mapper.fromBankUnitToBankUnitForRead(bankUnit));
                        updatedBankUnits.add(bankUnit);
//                        System.out.println("updated strapi id =" + strapiBankUnit.getId() + " "
//                                + strapiBankUnit.getLongId() + "=" + bankUnit.getLongId() + " bankUnit");
                    }
                });
            });
//            System.out.println("bankUnitUpdatePaginationList.size() = " + bankUnitUpdatePaginationList.size());
//            System.out.println("updatedBankUnits.size() = " + updatedBankUnits.size());
            startPosition += limit;
            bankUnitUpdatePaginationList = strapiClient.getPaginationBankUnits(limit, startPosition, SortConstants.SORT_ID);
        }
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
//            log.warn(String.format(messageBundle.getString("bank.notFoundWithName"), bankDictionary.getName()));
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
//            log.warn(String.format(messageBundle.getString("bank.notFoundInTown"), bankDictionary.getName(), bankDictionary.getCity()));
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
            String[] workHoursArray = workHoursArray = bankDictionary.getWorkingHours().split(";");
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
                .bankBranch(bankBranch.getId().toString())
                .city(city.getId())
                .longId(bankDictionary.getId())
                .refill(bankDictionary.isRefill())
                .cashReceipt(bankDictionary.isCashReceipt())
                .build();
    }

}
