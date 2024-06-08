package com.apapedia.catalog.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCatalogRequestDTO {

    @NotNull(message = "Seller ID cannot be empty")
    private UUID seller;

    @NotNull(message = "Product name cannot be empty")
    private String productName;

    @NotNull(message = "Product price cannot be empty")
    private BigInteger price;

    @NotNull(message = "Product description cannot be empty")
    private String productDescription;

    @NotNull(message = "Product stock cannot be empty")
    private Integer stock;

    @NotNull(message = "Category ID cannot be empty")
    private UUID categoryId;

    @NotNull(message = "Product image cannot be empty")
    private MultipartFile imageFile;
}
