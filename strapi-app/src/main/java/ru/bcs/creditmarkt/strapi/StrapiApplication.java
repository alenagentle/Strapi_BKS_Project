package ru.bcs.creditmarkt.strapi;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.bcs.creditmarkt.strapi.config.LogConfig;
import ru.bcs.creditmarkt.strapi.config.EncoderConfig;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        EncoderConfig.class, JacksonDecoder.class, Encoder.class, Decoder.class, LogConfig.class})})
public class StrapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrapiApplication.class, args);
    }
}
