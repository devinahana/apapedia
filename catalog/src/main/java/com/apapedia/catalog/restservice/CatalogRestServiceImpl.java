package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CatalogDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO) {
        Catalog catalog = catalogMapper.createCatalogRequestDTOToCatalog(createCatalogRequestDTO);

        UUID categoryId = createCatalogRequestDTO.getCategoryId();
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
        } else {
            catalog.setCategory(category);
        }
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
        return catalogDb.findAllBySellerOrderByProductNameAscIgnoreCase(sellerId);
    }

    @Override
    public Catalog updateCatalog(Catalog catalog, UpdateCatalogRequestDTO updateCatalogRequestDTO) {
        if (updateCatalogRequestDTO.getProductName() != null) {
            catalog.setProductName(updateCatalogRequestDTO.getProductName());
        }
        if (updateCatalogRequestDTO.getProductDescription() != null) {
            catalog.setProductDescription(updateCatalogRequestDTO.getProductDescription());
        }
        if (updateCatalogRequestDTO.getPrice() != null) {
            catalog.setPrice(updateCatalogRequestDTO.getPrice());
        }
        if (updateCatalogRequestDTO.getStock() != 0) { // assuming stock cannot be negative and 0 means not updated
            catalog.setStock(updateCatalogRequestDTO.getStock());
        }
        if (updateCatalogRequestDTO.getCategoryId() != null) {
            catalog.setCategoryId(updateCatalogRequestDTO.getCategoryId());
        }
        if (updateCatalogRequestDTO.getImageFile() != null && !updateCatalogRequestDTO.getImageFile().isEmpty()) {
            // handle the image file processing and updating
            byte[] imageBytes = updateCatalogRequestDTO.getImageFile().getBytes();
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
    public List<Catalog> findBySellerIdNameAsc(UUID sellerId) {
        return catalogDb.findBySellerOrderByProductNameAscIgnoreCase(sellerId);
    }

    @Override
    public List<Catalog> findBySellerIdNameDesc(UUID sellerId) {
        return catalogDb.findBySellerOrderByProductNameDescIgnoreCase(sellerId);
    }


    @Override
    public List<Catalog> findAllNameAsc() {
        return catalogDb.findAllByIsDeletedFalseOrderByProductNameAscIgnoreCase();
    }

    @Override
    public List<Catalog> findAllNameDesc() {
        return catalogDb.findAllByIsDeletedFalseOrderByProductNameDescIgnoreCase();
    }

}
