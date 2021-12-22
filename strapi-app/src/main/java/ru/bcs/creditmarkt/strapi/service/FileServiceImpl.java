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
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;

import javax.validation.Validator;
import java.util.List;
import java.util.concurrent.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${file-path}")
    private String filePath;
    private final BankUnitMapper mapper;
    private final StrapiClient strapiClient;
//    private final BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1);
//    private final Executor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, blockingQueue);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    private Validator validator;

    public ResponseEntity<String> manageBankUnits(List<MultipartFile> multipartFileList) {
        try {
//            executor.execute(new BankUnitThread(multipartFileList, mapper, strapiClient, validator, filePath));
            executor.submit(new BankUnitThread(multipartFileList, mapper, strapiClient, validator, filePath));
        } catch (RejectedExecutionException e) {
            log.warn("Service is busy " + e.getMessage());
            return new ResponseEntity<>("Service is busy ", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>("file uploaded", HttpStatus.OK);
    }

}
