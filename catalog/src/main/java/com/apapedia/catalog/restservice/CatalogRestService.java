package com.apapedia.catalog.restservice;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CatalogRestService {

    Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO)  throws IOException;

    Catalog findById(UUID catalogID);

    List<Catalog> findAll();

    List<Catalog> findBySeller(UUID sellerId);

    Catalog updateCatalog(Catalog catalog, UpdateCatalogRequestDTO updateCatalogRequestDTO) throws IOException;

    void deleteCatalogById(UUID catalogId);

    List<Catalog> findByName(String productName);

    List<Catalog> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Catalog> findByPriceMin(BigDecimal minPrice);

    List<Catalog> findByPriceMax(BigDecimal maxPrice);

    List<Catalog> sortByPrice(Boolean isAscending);

    List<Catalog> sortByName(Boolean isAscending);

}
