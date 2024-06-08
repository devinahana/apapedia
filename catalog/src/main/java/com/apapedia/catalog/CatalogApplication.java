package com.apapedia.catalog;

import com.apapedia.catalog.restservice.CategoryRestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

	@Bean
	CommandLineRunner run(CategoryRestService categoryRestService) {
		return args -> {
			categoryRestService.createCategory();
		};
	}

}
