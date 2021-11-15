package ru.bcs.creditmarkt.strapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StrapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StrapiApplication.class, args);
	}
}
