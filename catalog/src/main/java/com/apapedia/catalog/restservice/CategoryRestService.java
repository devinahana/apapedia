package com.apapedia.catalog.restservice;

import com.apapedia.catalog.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRestService {
    Category saveCategory(Category category);

    Category findById(UUID id);

    List<Category> getAllCategory();

    boolean isCategoryTableEmpty();

}
