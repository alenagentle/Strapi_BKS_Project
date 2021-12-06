package ru.bcs.creditmarkt.strapi.service;

import com.ibm.icu.text.Transliterator;
import com.poiji.bind.Poiji;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankDictionary;
import ru.bcs.creditmarkt.strapi.dto.strapi.BankUnit;
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
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
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

    public void treatXlcFileList(List<MultipartFile> multipartFileList) {
        List<Path> pathList = loadXlsFileList(multipartFileList);
        System.out.println("pathList = " +  pathList);
        List<BankUnit> bankUnits = new ArrayList<>();
        pathList.forEach(path -> {
            List<BankDictionary> bankDictionaries = readXlsFile(path);
            filterBankBranches(bankDictionaries, bankUnits);
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });
        bankUnits.forEach(strapiClient::createBankUnit);
    }


    private List<Path> loadXlsFileList(List<MultipartFile> multipartFileList) {
        System.out.println("loadXlsFileList");
        List<Path> pathList = new ArrayList<>();
        multipartFileList.forEach(file -> {
            try (ZipInputStream inputStream = new ZipInputStream(file.getInputStream())) {
                Path rootLocation = Paths.get(filePath);
                System.out.println("rootLocation=" + rootLocation);
                for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
                    StringBuilder fileName = new StringBuilder(dateFormatWithMs.format(new Timestamp(System.currentTimeMillis())));
                    fileName.append(entry.getName());
                    Path resolvedPath = rootLocation.resolve(fileName.toString()).normalize().toAbsolutePath();
                    System.out.println("resolvedPath="+ resolvedPath);
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
        System.out.println("strapiBankBranches=" + strapiBankBranches);
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
                .build();
    }

}
