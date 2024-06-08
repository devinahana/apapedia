package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;

import java.util.List;
import java.util.UUID;

public interface CatalogRestService {

    Catalog setCatalogcategory(Catalog catalog, UUID categoryId);

    List<Catalog> findBySeller(UUID sellerId);

    List<Catalog> findBySellerIdNameAsc(UUID sellerId);

    List<Catalog> findBySellerIdNameDesc(UUID sellerId);
    List<Catalog> findAll();

    List<Catalog> findAllNameAsc();

    List<Catalog> findAllNameDesc();

    void deleteCatalog(UUID id);
}
