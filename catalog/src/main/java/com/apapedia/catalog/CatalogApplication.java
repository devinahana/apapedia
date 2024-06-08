package com.apapedia.catalog;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.restservice.CategoryRestService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
public class CatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(CategoryRestService categoryRestService) {
		return args -> {

			// Create all these categories when there have not been any categories in the database
			if (categoryRestService.isCategoryTableEmpty()) {
				String[] listCategory = {
						"Aksesoris Fashion",
						"Buku & Alat Tulis",
						"Elektronik",
						"Fashion Bayi & Anak",
						"Fashion Muslim",
						"Fotografi",
						"Hobi & Koleksi",
						"Jam Tangan",
						"Perawatan & Kecantikan",
						"Makanan & Minuman",
						"Otomotif",
						"Perlengkapan Rumah",
						"Souvenir & Party Supplies"
				};
				for (String category : listCategory) {
					categoryRestService.saveCategory(new Category(category));
				}
			}
		};
	}

}
