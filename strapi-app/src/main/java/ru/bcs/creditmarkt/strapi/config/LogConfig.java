package ru.bcs.creditmarkt.strapi.config;

import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfig {

    @Value("${feign.log.level}")
    private String logLevel;

    private final String NONE = "NONE";
    private final String BASIC = "BASIC";
    private final String HEADERS = "HEADERS";
    private final String FULL = "FULL";

    @Bean
    Logger.Level feignCustomLoggerLevel() {
        Logger.Level level;
        switch (logLevel) {
            case (NONE):
                level = Logger.Level.NONE;
                break;
            case (BASIC):
                level = Logger.Level.BASIC;
                break;
            case (HEADERS):
                level = Logger.Level.HEADERS;
                break;
            case (FULL):
                level = Logger.Level.FULL;
                break;
            default:
                level = Logger.Level.FULL;
        }
        return level;
    }
}
