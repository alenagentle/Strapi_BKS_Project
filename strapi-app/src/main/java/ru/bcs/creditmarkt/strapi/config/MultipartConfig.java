package ru.bcs.creditmarkt.strapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import ru.bcs.creditmarkt.strapi.utils.constants.FileSizeConstants;

@Configuration
public class MultipartConfig {
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(FileSizeConstants.MAX_UPLOAD_FILE_SIZE);
        return multipartResolver;
    }
}
