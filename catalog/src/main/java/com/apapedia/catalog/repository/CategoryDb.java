package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryDb extends JpaRepository<Category, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Category c")
    boolean existsAny();
}
