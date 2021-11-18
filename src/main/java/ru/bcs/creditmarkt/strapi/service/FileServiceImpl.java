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
import ru.bcs.creditmarkt.strapi.dto.BankBranch;
import ru.bcs.creditmarkt.strapi.dto.BankDictionary;
import ru.bcs.creditmarkt.strapi.dto.BankUnit;
import ru.bcs.creditmarkt.strapi.utils.Localization;
import ru.bcs.creditmarkt.strapi.utils.constants.SeparatorConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
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
    public static final String RUSSIAN_TO_LATIN = "Russian-Latin/BGN";

    public void loadAndReadXlsFileList(List<MultipartFile> multipartFileList) {
        multipartFileList.forEach(file -> {
            try (ZipInputStream inputStream = new ZipInputStream(file.getInputStream())) {
                Path rootLocation = Paths.get(filePath);
                for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
                    Path resolvedPath = rootLocation.resolve(dateFormat.format(new Timestamp(System.currentTimeMillis()))
                            + "_" + entry.getName());
                    if (!entry.isDirectory()) {
                        Files.createDirectories(resolvedPath.getParent());
                        Files.copy(inputStream, resolvedPath);
                        readXlsFile(resolvedPath);
                        if (Files.exists(resolvedPath))
                            Files.delete(resolvedPath);
                    }
                }
                inputStream.closeEntry();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    private void readXlsFile(Path path) {
        if (Files.exists(path)) {
            File file = new File(path.toString());
            List<BankDictionary> bankDictionaries = Poiji.fromExcel(file, BankDictionary.class);
            filterBankBranches(bankDictionaries);
        }
    }

    private void filterBankBranches(List<BankDictionary> bankDictionaries) {
        List<BankBranch> strapiBankBranches = strapiClient.getBankBranches();
        strapiBankBranches
                .forEach(bankBranch -> bankDictionaries
                        .forEach(bankDictionary -> {
                            if (bankDictionary.getName().equals(bankBranch.getBank().getName())) {
                                bankBranch.getCities().forEach(city -> {
                                    if (city.getName().equals(bankDictionary.getCity())) {
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        if (formatter.format(bankBranch.getCreated_at()).equals(formatter.format(bankBranch.getUpdated_at()))) {
                                            strapiClient.createBankUnit(getBankUnit(bankBranch, bankDictionary));
                                        }
                                    } else
                                        log.info(String.format(messageBundle.getString("bank.notFoundInTown"), bankDictionary.getCity()));
                                });
                            } else
                                log.info(String.format(messageBundle.getString("bank.notFoundWithName"), bankDictionary.getName()));
                        }));
    }

    private BankUnit getBankUnit(BankBranch bankBranch, BankDictionary bankDictionary) {
        Transliterator russianToLatin = Transliterator.getInstance(RUSSIAN_TO_LATIN);
        String slug = StringUtils.toRootLowerCase(russianToLatin.transliterate(bankDictionary.getName()));
        return BankUnit.builder()
                .name(bankDictionary.getName())
                .h1(bankDictionary.getName())
                .slug(StringUtils.toRootLowerCase(slug).replaceAll("\\s+", ""))
                .address(bankDictionary.getCity() + ", "
                        + bankDictionary.getAddress().split(",")[SeparatorConstants.ADDRESS_STREET] + ", "
                        + bankDictionary.getAddress().split(",")[SeparatorConstants.ADDRESS_STREET_NUMBER])
                .latitude(bankDictionary.getLatitude().split(" ")[SeparatorConstants.LATITUDE])
                .longitude(bankDictionary.getLongitude().split(" ")[SeparatorConstants.LONGITUDE])
                .type(bankDictionary.getSubheading().contains(StringUtils.toRootLowerCase(messageBundle.getString("atm"))) ? "atm" : "branch")
                .workingHours(bankDictionary.getWorkingHours())
                .telephones(bankDictionary.getTelephones())
                .bankBranch(bankBranch.getId().toString())
                .build();
    }

}
