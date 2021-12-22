package ru.bcs.creditmarkt.strapi.thread;

import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;

import javax.validation.Validator;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankUnitThreadExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BankUnitThreadExecutor(BankUnitMapper mapper,
                                  StrapiClient strapiClient,
                                  Validator validator,
                                  BlockingQueue<Path> fileReferencesQueue) {
        executor.submit(new BankUnitThread(mapper, strapiClient, validator, fileReferencesQueue));
    }
}
