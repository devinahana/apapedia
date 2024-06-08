package com.apapedia.catalog.restservice;

import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CategoryDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryRestServiceImpl implements CategoryRestService {

    CategoryDb categoryDb;

    @Override
    public Category saveCategory(Category category) {
        return categoryDb.save(category);
    }

    @Override
    public Category findById(UUID id) {
        return categoryDb.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryDb.findAll();
    }

    @Override
    public boolean isCategoryTableEmpty() {
        return !categoryDb.existsAny();
    }
}
