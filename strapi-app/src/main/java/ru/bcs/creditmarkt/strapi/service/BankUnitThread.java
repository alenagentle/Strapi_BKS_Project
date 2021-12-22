package ru.bcs.creditmarkt.strapi.service;

import com.poiji.bind.Poiji;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.*;
import ru.bcs.creditmarkt.strapi.exception.FileFormatException;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class BankUnitThread implements Runnable {

    private final BankUnitMapper mapper;
    private final StrapiClient strapiClient;
    private final Validator validator;
    private final List<MultipartFile> multipartFileList;
    private final String filePath;

    private final ResourceBundle messageBundle = Localization.getMessageBundle();
    private final SimpleDateFormat dateFormatWithMin = new SimpleDateFormat(messageBundle.getString("time.format.min"));
    private final SimpleDateFormat dateFormatWithMs = new SimpleDateFormat(messageBundle.getString("time.format.ms"));
    private final String resolvedPathText = "resolvedPath - %s";

    private Set<String> notFoundBanks = new HashSet<>();
    private Set<String> notFoundCities = new HashSet<>();

    public BankUnitThread(List<MultipartFile> multipartFileList,
                          BankUnitMapper mapper,
                          StrapiClient strapiClient,
                          Validator validator,
                          String filePath) {
        this.mapper = mapper;
        this.strapiClient = strapiClient;
        this.validator = validator;
        this.multipartFileList = multipartFileList;
        this.filePath = filePath;
    }


    @Override
    public void run() {
        manageBankUnits(multipartFileList);
    }

    private void manageBankUnits(List<MultipartFile> multipartFileList) {
        List<BankUnit> bankUnits = new ArrayList<>();
        AtomicReference<List<BankDictionary>> bankDictionaries = new AtomicReference<>();
        Set<BankUnit> updatedBankUnits = new HashSet<>();
        List<Path> pathList = loadXlsFileList(multipartFileList);

        readXlsFileList(pathList, bankUnits, bankDictionaries);
        System.out.println("bankUnits.size()1 = " + bankUnits.size());

        updateBankUnit(bankUnits, updatedBankUnits);
        System.out.println("updatedBankUnits.size() = " + updatedBankUnits.size());

        bankUnits.removeAll(updatedBankUnits);
        System.out.println("bankUnits.size()2 = " + bankUnits.size());

        System.out.println();
        bankUnits.forEach(strapiClient::createBankUnit);

        System.out.println("task is done!");

    }


    private List<Path> loadXlsFileList(List<MultipartFile> multipartFileList) {
        List<Path> pathList = new ArrayList<>();
        for (MultipartFile file : multipartFileList) {
            String originalFileName = file.getOriginalFilename();
            String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf("."));
            if (!extension.equals(".zip"))
                throw new FileFormatException(messageBundle.getString("text.formatRequired"));
            try (ZipInputStream inputStream = new ZipInputStream(file.getInputStream(), Charset.forName("CP866"))) {
                Path rootLocation = Paths.get(filePath);
                for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
                    StringBuilder fileName = new StringBuilder(dateFormatWithMs.format(new Timestamp(System.currentTimeMillis())));
                    fileName.append(entry.getName());
                    Path resolvedPath = rootLocation.resolve(fileName.toString()).normalize().toAbsolutePath();
                    System.out.println("resolvedPath = " + resolvedPath);
                    log.info(String.format(resolvedPathText, resolvedPath));
                    if (!entry.isDirectory()) {
                        Files.copy(inputStream, resolvedPath,
                                StandardCopyOption.REPLACE_EXISTING);
                        pathList.add(resolvedPath);
                    }
                }
                inputStream.closeEntry();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return pathList;
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
            System.out.println("bankUnitUpdatePaginationList.size() = " + bankUnitUpdatePaginationList.size());
            System.out.println("updatedBankUnits.size() = " + updatedBankUnits.size());
            startPosition += limit;
            bankUnitUpdatePaginationList = strapiClient.getPaginationBankUnits(limit, startPosition, SortConstants.SORT_ID);
        }
    }

    private void readXlsFileList(List<Path> pathList, List<BankUnit> bankUnits, AtomicReference<List<BankDictionary>> bankDictionaries) {
        pathList.forEach(path -> {
            bankDictionaries.set(readXlsFile(path));
            filterBankBranches(bankDictionaries.get(), bankUnits);
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }

    private List<BankDictionary> readXlsFile(Path path) {
        List<BankDictionary> bankDictionaries = new ArrayList<>();
        if (Files.exists(path)) {
            File file = new File(path.toString());
            bankDictionaries = Poiji.fromExcel(file, BankDictionary.class);
            validateBankDictionaries(bankDictionaries);
            System.out.println("bankDictionaries.size() = " + bankDictionaries.size());
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
//        System.out.println("filterBankBranches");
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
