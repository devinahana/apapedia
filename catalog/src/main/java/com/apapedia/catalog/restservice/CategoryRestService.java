package com.apapedia.catalog.restservice;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;

import java.util.List;

public interface CategoryRestService {
    void createCategory();

    Category findById(Long id);

    List<Category> getAllCategory();

    List<Catalog> getListCatalog(Long id);

}
