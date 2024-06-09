package com.apapedia.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCatalogRequestDTO {

    private UUID seller;

    private String productName;

    private BigDecimal price;

    private String productDescription;

    private Integer stock;

    private Long categoryId;

    private MultipartFile imageFile;
}
