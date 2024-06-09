package com.apapedia.catalog.restservice;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.model.enumerator.CategoryName;
import com.apapedia.catalog.repository.CategoryDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CategoryRestServiceImpl implements CategoryRestService {

    CategoryDb categoryDb;

    private Category saveCategory(Category category) {
        return categoryDb.save(category);
    }

    private boolean isCategoryTableEmpty() {
        return !categoryDb.existsAny();
    }

    @Override
    public void createCategory() {
        if (this.isCategoryTableEmpty()) {
            for (CategoryName categoryName : CategoryName.values()) {
                Category category = new Category();
                category.setName(categoryName);
                this.saveCategory(category);
            }
        }
    }

    @Override
    public Category findById(Long id) {
        return categoryDb.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryDb.findAll();
    }

    @Override
    public List<Catalog> getListCatalog(Long id) {
        Category category = this.findById(id);
        if (category == null) {
            throw new NoSuchElementException("Category with ID " + id + " not found");
        }

        return category.getListCatalog();
    }
}
