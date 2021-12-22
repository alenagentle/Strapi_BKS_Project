package ru.bcs.creditmarkt.strapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.exception.FileFormatException;
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;
import ru.bcs.creditmarkt.strapi.thread.BankUnitThread;
import ru.bcs.creditmarkt.strapi.thread.BankUnitThreadExecutor;
import ru.bcs.creditmarkt.strapi.utils.Localization;
import ru.bcs.creditmarkt.strapi.utils.constants.Queue;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.charset.Charset;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${file-path}")
    private String filePath;
    private final ResourceBundle messageBundle = Localization.getMessageBundle();
    private final SimpleDateFormat dateFormatWithMs = new SimpleDateFormat(messageBundle.getString("time.format.ms"));
    private final String resolvedPathText = "resolvedPath - %s";


    public ResponseEntity<String> manageBankUnits(List<MultipartFile> multipartFileList) {
        List<Path> pathList = loadXlsFileList(multipartFileList);
        Queue.fileReferencesQueue.addAll(pathList);
        return new ResponseEntity<>("file uploaded", HttpStatus.OK);
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

}
