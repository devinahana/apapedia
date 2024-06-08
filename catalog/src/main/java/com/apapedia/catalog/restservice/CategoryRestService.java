package com.apapedia.catalog.restservice;

import com.apapedia.catalog.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRestService {
    void createCategory();

    Category findById(UUID id);

    List<Category> getAllCategory();

}
