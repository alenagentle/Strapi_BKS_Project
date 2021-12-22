package ru.bcs.creditmarkt.strapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bcs.creditmarkt.strapi.client.StrapiClient;
import ru.bcs.creditmarkt.strapi.mapper.BankUnitMapper;
import ru.bcs.creditmarkt.strapi.thread.BankUnitThreadExecutor;
import ru.bcs.creditmarkt.strapi.utils.constants.Queue;

import javax.validation.Validator;

@Configuration
public class ExecutorConfig {

    @Bean
    public BankUnitThreadExecutor getExecutor(BankUnitMapper mapper,
                                              StrapiClient strapiClient,
                                              Validator validator) {
        return new BankUnitThreadExecutor(mapper, strapiClient, validator, Queue.fileReferencesQueue);
    }
}
