package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public interface CatalogDb extends JpaRepository<Catalog, UUID> {

    @Query("SELECT c FROM Catalog c ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findAllByOrderByProductNameAscIgnoreCase();

    @Query("SELECT c FROM Catalog c ORDER BY LOWER(c.productName) DESC")
    List<Catalog> findAllByOrderByProductNameDescIgnoreCase();

    List<Catalog> findAllByOrderByPriceAsc();

    List<Catalog> findAllByOrderByPriceDesc();

    List<Catalog> findByPriceGreaterThanEqualOrderByPriceAsc(BigDecimal minPrice);

    List<Catalog> findByPriceLessThanEqualOrderByPriceAsc(BigDecimal maxPrice);

    List<Catalog> findByPriceBetweenOrderByPriceAsc(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT c FROM Catalog c WHERE c.seller = :sellerId ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findBySellerOrderByProductNameAscIgnoreCase(UUID sellerId);

    @Query("SELECT c FROM Catalog c WHERE LOWER(c.productName) LIKE LOWER(CONCAT('%', :productName, '%')) ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findByProductNameContainingIgnoreCaseOrderByProductNameAscIgnoreCase(String productName);


}
