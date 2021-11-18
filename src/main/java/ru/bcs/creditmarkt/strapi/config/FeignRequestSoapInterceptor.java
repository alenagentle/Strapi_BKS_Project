package ru.bcs.creditmarkt.strapi.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignRequestSoapInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Content-Type", "text/xml");
        requestTemplate.header("soapAction", "http://web.cbr.ru/");
    }
}