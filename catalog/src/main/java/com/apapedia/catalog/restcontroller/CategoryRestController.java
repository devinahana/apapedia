package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.Response.BaseResponse;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.restservice.CategoryRestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/category")
public class CategoryRestController {
    CategoryRestService categoryService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategory() {
        List<Category> listCategory = categoryService.getAllCategory();
        return ResponseEntity.ok(new BaseResponse<>(true, "Category fetched successfully", listCategory));
    }

    @GetMapping("/catalog/{categoryId}")
    public ResponseEntity<?> getListCatalog(@PathVariable Long categoryId) {
        try {
            List<Catalog> listCatalog = categoryService.getListCatalog(categoryId);
            return ResponseEntity.ok(new BaseResponse<>(true, "List catalog fetched successfully", listCatalog));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching list catalog : " + e.getMessage()));

        }
    }
}
