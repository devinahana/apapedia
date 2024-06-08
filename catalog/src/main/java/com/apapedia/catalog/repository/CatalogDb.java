package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.List;

public interface CatalogDb extends JpaRepository<Catalog, UUID> {

    @Query("SELECT c FROM Catalog c ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findAllByOrderByProductNameAscIgnoreCase();

    @Query("SELECT c FROM Catalog c WHERE c.seller = :sellerId ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findAllBySellerOrderByProductNameAscIgnoreCase(UUID sellerId);

//    @Query("SELECT c FROM Catalog c WHERE c.seller = :sellerId ORDER BY LOWER(c.productName) ASC")
//    List<Catalog> findBySellerOrderByProductNameAscIgnoreCase(UUID sellerId);

    @Query("SELECT c FROM Catalog c WHERE c.seller = :sellerId ORDER BY LOWER(c.productName) DESC")
    List<Catalog> findBySellerOrderByProductNameDescIgnoreCase(UUID sellerId);

    List<Catalog> findAllByIsDeletedFalse();

    @Query("SELECT c FROM Catalog c WHERE c.isDeleted = false ORDER BY LOWER(c.productName) ASC")
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameAscIgnoreCase();

    @Query("SELECT c FROM Catalog c WHERE c.isDeleted = false ORDER BY LOWER(c.productName) DESC")
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameDescIgnoreCase();

}
