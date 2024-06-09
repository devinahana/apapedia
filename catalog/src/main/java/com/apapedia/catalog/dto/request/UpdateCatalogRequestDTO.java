package com.apapedia.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCatalogRequestDTO {
    String productName;
    String productDescription;
    BigDecimal price;
    Integer stock;
    Long categoryId;
    MultipartFile image;
}
