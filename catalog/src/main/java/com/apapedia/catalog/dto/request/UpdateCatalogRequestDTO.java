package com.apapedia.catalog.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCatalogRequestDTO {
    String productName;
    String productDescription;
    BigDecimal price;
    int stock;
    UUID categoryId;
    MultipartFile imageFile;
}
