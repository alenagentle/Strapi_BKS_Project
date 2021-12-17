package ru.bcs.creditmarkt.strapi;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import org.apache.commons.lang3.StringUtils;
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

        System.out.println("os name = " + System.getProperty("os.name"));
//        System.getProperties().list(System.out);


//        String originalName = "АЛЬТЕРНАТИВА";
//        StringBuilder inflectiveName = new StringBuilder("альтернативой");
//
//        int upperCaseCount = 0;
//        for (int i = 0; i < originalName.length(); i++) {
//            for (int j = i; j < inflectiveName.length(); j++) {
//                if (StringUtils.toRootLowerCase(String.valueOf(originalName.charAt(i)))
//                        .equals(StringUtils.toRootLowerCase(String.valueOf(inflectiveName.charAt(j))))) {
//                    System.out.println(originalName.charAt(i) + " equals " + inflectiveName.charAt(j) + " i = " + i + " j = " + j);
//
//                    if (Character.isUpperCase(originalName.charAt(i))) {
//                        inflectiveName.setCharAt(j, Character.toUpperCase(inflectiveName.charAt(j)));
//                        System.out.println(inflectiveName.charAt(j) + " after to Upper case");
//                        upperCaseCount++;
//                    }
//                    break;
//                }
//            }
//        }
//
//        System.out.println("upperCaseCount = " + upperCaseCount);
//        System.out.println("originalName.replaceAll = " + originalName.replaceAll("\\s+", ""));
//        System.out.println("originalName.trim().length() = " + originalName.replaceAll("\\s+", "").length());
//
//        int lengthWithoutSpaces = originalName.replaceAll("\\s+", "").length();
//        int lengthWithoutEndAndSpaces = lengthWithoutSpaces - 1;
//
//        if (upperCaseCount == lengthWithoutSpaces || upperCaseCount == lengthWithoutEndAndSpaces) {
//            for (int i = 0; i < inflectiveName.length(); i++) {
//                inflectiveName.setCharAt(i, Character.toUpperCase(inflectiveName.charAt(i)));
//            }
//        }
//
//        System.out.println("inflectiveName = " + inflectiveName);


    }
}
