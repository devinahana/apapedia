package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;

import java.util.List;
import java.util.UUID;

public interface CatalogRestService {

    Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO);

    Catalog findById(UUID catalogID);

    List<Catalog> findBySeller(UUID sellerId);

    Catalog updateCatalog(Catalog catalog, UpdateCatalogRequestDTO updateCatalogRequestDTO);

    void deleteCatalogById(UUID catalogId);

    List<Catalog> findBySellerNameAsc(UUID seller);

    List<Catalog> findBySellerIdNameDesc(UUID sellerId);
    List<Catalog> findAll();

    List<Catalog> findAllNameAsc();

    List<Catalog> findAllNameDesc();

    void deleteCatalog(UUID id);
}
