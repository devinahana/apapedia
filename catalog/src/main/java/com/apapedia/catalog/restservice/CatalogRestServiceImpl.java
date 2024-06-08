package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
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

    @Override
    public Catalog setCatalogcategory(Catalog catalog, UUID categoryId) {
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new NoSuchElementException("Category with ID " + categoryId + " not found.");
        }

        catalog.setCategory(category);
        return catalogDb.save(catalog);
    }

    @Override
    public List<Catalog> findBySeller(UUID sellerId) {
        return catalogDb.findBySeller(sellerId);
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
    public List<Catalog> findAll() {
        return catalogDb.findAllByIsDeletedFalse();
    }

    @Override
    public List<Catalog> findAllNameAsc() {
        return catalogDb.findAllByIsDeletedFalseOrderByProductNameAscIgnoreCase();
    }

    @Override
    public List<Catalog> findAllNameDesc() {
        return catalogDb.findAllByIsDeletedFalseOrderByProductNameDescIgnoreCase();
    }

    @Override
    public void deleteCatalog(UUID id) {
        Catalog catalog = catalogDb.findById(id).orElse(null);
        if (catalog == null) {
            throw new NoSuchElementException("Catalog with ID " + id + " not found");
        }

        catalogDb.deleteById(id);
    }
}
