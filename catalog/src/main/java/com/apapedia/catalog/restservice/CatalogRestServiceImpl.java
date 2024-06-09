package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CatalogDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CatalogRestServiceImpl implements CatalogRestService{

    CatalogDb catalogDb;

    CategoryRestService categoryService;

    CatalogMapper catalogMapper;

    private Catalog saveCatalog(Catalog catalog) {
        return catalogDb.save(catalog);
    }

    @Override
    public Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO)  throws IOException {
        if (createCatalogRequestDTO.getStock() < 1) {
            throw new IllegalArgumentException("Stock cannot be less than 1");
        }

        Long categoryId = createCatalogRequestDTO.getCategoryId();
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
        }

        Catalog catalog = catalogMapper.createCatalogRequestDTOToCatalog(createCatalogRequestDTO);
        catalog.setCategory(category);

        return this.saveCatalog(catalog);
    }

    @Override
    public Catalog findById(UUID catalogID) {
        Catalog catalog = catalogDb.findById(catalogID).orElse(null);
        if (catalog == null) {
            throw new NoSuchElementException("Catalog with ID " + catalogID + " not found");
        }
        return catalog;
    }

    @Override
    public List<Catalog> findAll() {
        return catalogDb.findAllByOrderByProductNameAscIgnoreCase();
    }

    @Override
    public List<Catalog> findBySeller(UUID sellerId) {
        return catalogDb.findBySellerOrderByProductNameAscIgnoreCase(sellerId);
    }

    @Override
    public Catalog updateCatalog(Catalog catalog, UpdateCatalogRequestDTO updateCatalogRequestDTO) throws IOException {
        if (updateCatalogRequestDTO.getProductName() != null) {
            catalog.setProductName(updateCatalogRequestDTO.getProductName());
        }

        if (updateCatalogRequestDTO.getProductDescription() != null) {
            catalog.setProductDescription(updateCatalogRequestDTO.getProductDescription());
        }

        if (updateCatalogRequestDTO.getPrice() != null) {
            catalog.setPrice(updateCatalogRequestDTO.getPrice());
        }

        if (updateCatalogRequestDTO.getStock() != null) { // assuming stock cannot be negative and 0 means not updated
            if (updateCatalogRequestDTO.getStock() < 0) {
                throw new IllegalArgumentException("Stock cannot be less than 0");
            }
            catalog.setStock(updateCatalogRequestDTO.getStock());
        }

        Long categoryId = updateCatalogRequestDTO.getCategoryId();
        if (categoryId != null) {
            Category category = categoryService.findById(categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
            }
            catalog.setCategory(category);
        }

        if (updateCatalogRequestDTO.getImage() != null && !updateCatalogRequestDTO.getImage().isEmpty()) {
            byte[] imageBytes = updateCatalogRequestDTO.getImage().getBytes();
            catalog.setImage(imageBytes);
        }

        return this.saveCatalog(catalog);
    }

    @Override
    public void deleteCatalogById(UUID catalogId) {
        Catalog catalog = this.findById(catalogId);
        catalogDb.delete(catalog);
    }

    @Override
    public List<Catalog> findByName(String productName) {
        return catalogDb.findByProductNameContainingIgnoreCaseOrderByProductNameAscIgnoreCase(productName);
    }

    @Override
    public List<Catalog> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return catalogDb.findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice);
    }

    @Override
    public List<Catalog> findByPriceMin(BigDecimal minPrice) {
        return catalogDb.findByPriceGreaterThanEqualOrderByPriceAsc(minPrice);
    }

    @Override
    public List<Catalog> findByPriceMax(BigDecimal maxPrice) {
        return catalogDb.findByPriceLessThanEqualOrderByPriceAsc(maxPrice);
    }

    @Override
    public List<Catalog> sortByPrice(Boolean isAscending) {
        if (isAscending) {
            return catalogDb.findAllByOrderByPriceAsc();
        } else {
            return catalogDb.findAllByOrderByPriceDesc();
        }
    }

    @Override
    public List<Catalog> sortByName(Boolean isAscending) {
        if (isAscending) {
            return catalogDb.findAllByOrderByProductNameAscIgnoreCase();
        } else {
            return catalogDb.findAllByOrderByProductNameDescIgnoreCase();
        }
    }
}
