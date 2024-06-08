package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CategoryDb extends JpaRepository<Category, UUID> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c")
    boolean existsAny();
}
