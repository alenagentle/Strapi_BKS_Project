package ru.bcs.creditmarkt.strapi.service;

import com.ibm.icu.text.Transliterator;
import com.poiji.bind.Poiji;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankDictionary;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnitParent;
import ru.bcs.creditmarkt.strapi.exception.FileFormatException;
import ru.bcs.creditmarkt.strapi.utils.Localization;
import ru.bcs.creditmarkt.strapi.utils.constants.SeparatorConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${file-path}")
    private String filePath;

    private final StrapiClient strapiClient;
    private final ResourceBundle messageBundle = Localization.getMessageBundle();
    private static final String RUSSIAN_TO_LATIN = "Russian-Latin/BGN";
    private final SimpleDateFormat dateFormatWithMin = new SimpleDateFormat(messageBundle.getString("time.format.min"));
    private final SimpleDateFormat dateFormatWithMs = new SimpleDateFormat(messageBundle.getString("time.format.ms"));

    public ResponseEntity<String> manageBankUnits(List<MultipartFile> multipartFileList) {
        List<BankUnit> bankUnits = new ArrayList<>();
        AtomicReference<List<BankDictionary>> bankDictionaries = new AtomicReference<>();
        List<BankUnit> updatedBankUnits = new ArrayList<>();

        List<Path> pathList = loadXlsFileList(multipartFileList);
        readXlsFileList(pathList, bankUnits, bankDictionaries);
        updateBankUnit(bankUnits, updatedBankUnits);
        updatedBankUnits.forEach(bankUnits::remove);
        bankUnits.forEach(strapiClient::createBankUnit);

        return new ResponseEntity<>(
                "file uploaded",
                HttpStatus.OK);
    }

    private void updateBankUnit(List<BankUnit> bankUnits, List<BankUnit> updatedBankUnits) {
        List<BankUnitParent> strapiBankUnits = strapiClient.getBankUnits();
        strapiBankUnits.forEach(strapiBankUnit -> {
            bankUnits.forEach(bankUnit -> {
                if (Objects.equals(strapiBankUnit.getLongId(), bankUnit.getLongId())) {
                    strapiClient.updateBankUnit(strapiBankUnit.getId(), bankUnit);
                    updatedBankUnits.add(bankUnit);
                }
            });
        });
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

    private List<Path> loadXlsFileList(List<MultipartFile> multipartFileList) {
        List<Path> pathList = new ArrayList<>();
        multipartFileList.forEach(file -> {
            String originalFileName = file.getOriginalFilename();
            String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf("."));
            if (!extension.equals(".zip"))
                throw new FileFormatException(messageBundle.getString("text.formatRequired"));
            try (ZipInputStream inputStream = new ZipInputStream(file.getInputStream())) {
                Path rootLocation = Paths.get(filePath);
                for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
                    StringBuilder fileName = new StringBuilder(dateFormatWithMs.format(new Timestamp(System.currentTimeMillis())));
                    fileName.append(entry.getName());
                    Path resolvedPath = rootLocation.resolve(fileName.toString()).normalize().toAbsolutePath();
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
        });
        return pathList;
    }

    private List<BankDictionary> readXlsFile(Path path) {
        List<BankDictionary> bankDictionaries = new ArrayList<>();
        if (Files.exists(path)) {
            File file = new File(path.toString());
            bankDictionaries = Poiji.fromExcel(file, BankDictionary.class);
        }
        return bankDictionaries;
    }

    private void filterBankBranches(List<BankDictionary> bankDictionaries, List<BankUnit> bankUnits) {
        List<BankBranch> strapiBankBranches = strapiClient.getBankBranches();
        bankDictionaries.forEach(bankDictionary -> {
            filterByBankName(strapiBankBranches, bankDictionary, bankUnits);
        });
    }

    private void filterByBankName(List<BankBranch> strapiBankBranches, BankDictionary bankDictionary, List<BankUnit> bankUnits) {
        AtomicInteger countNotFoundBank = new AtomicInteger();
        strapiBankBranches.forEach(bankBranch -> {
            if (bankDictionary.getName().equals(bankBranch.getBank().getName())) {
                filterByCity(bankDictionary, bankBranch, bankUnits);
            } else
                countNotFoundBank.getAndIncrement();
        });
        if (countNotFoundBank.intValue() == strapiBankBranches.size())
            log.warn(String.format(messageBundle.getString("bank.notFoundWithName"), bankDictionary.getName()));
    }

    private void filterByCity(BankDictionary bankDictionary, BankBranch bankBranch, List<BankUnit> bankUnits) {
        AtomicInteger countNotFoundCity = new AtomicInteger();
        bankBranch.getCities().forEach(city -> {
            if (city.getName().equals(bankDictionary.getCity())) {
                filterByDate(bankDictionary, bankBranch, bankUnits);
            } else
                countNotFoundCity.getAndIncrement();
        });
        if (countNotFoundCity.intValue() == bankBranch.getCities().size())
            log.warn(String.format(messageBundle.getString("bank.notFoundInTown"), bankDictionary.getName(), bankDictionary.getCity()));
    }

    private void filterByDate(BankDictionary bankDictionary, BankBranch bankBranch, List<BankUnit> bankUnits) {
        if (dateFormatWithMin.format(bankBranch.getCreatedAt()).equals(dateFormatWithMin.format(bankBranch.getUpdatedAt()))) {
            bankUnits.add(getBankUnit(bankBranch, bankDictionary));
        }
    }

    private BankUnit getBankUnit(BankBranch bankBranch, BankDictionary bankDictionary) {
        Transliterator russianToLatin = Transliterator.getInstance(RUSSIAN_TO_LATIN);
        StringBuilder slug = new StringBuilder(StringUtils.toRootLowerCase(russianToLatin.transliterate(bankDictionary.getName())));
        Pattern pattern = Pattern.compile("[^A-za-z]");
        Matcher matcher = pattern.matcher(slug);
        String atmType = StringUtils.toRootLowerCase(messageBundle.getString("atm"));
        StringBuilder address = new StringBuilder(bankDictionary.getCity());
        address.append(", ");
        String[] addressList = bankDictionary.getAddress().split(",");
        if (SeparatorConstants.ADDRESS_STREET <= addressList.length)
            address.append(addressList[SeparatorConstants.ADDRESS_STREET]);
        if (SeparatorConstants.ADDRESS_STREET_NUMBER <= addressList.length)
            address.append(addressList[SeparatorConstants.ADDRESS_STREET_NUMBER]);

        return BankUnit.builder()
                .name(bankDictionary.getName())
                .h1(bankDictionary.getName())
                .slug(matcher.replaceAll(""))
                .address(address.toString())
                .latitude(bankDictionary.getLatitude().split(" ")[SeparatorConstants.LATITUDE])
                .longitude(bankDictionary.getLongitude().split(" ")[SeparatorConstants.LONGITUDE])
                .type(bankDictionary.getSubheading().contains(atmType) ? "atm" : "branch")
                .workingHours(bankDictionary.getWorkingHours())
                .telephones(bankDictionary.getTelephones())
                .bankBranch(bankBranch.getId().toString())
                .longId(bankDictionary.getId())
                .build();
    }

}
