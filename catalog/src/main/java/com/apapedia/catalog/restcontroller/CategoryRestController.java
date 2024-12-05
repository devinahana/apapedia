package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.response.BaseResponse;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.restservice.CategoryRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get All Category", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCategoryList.class))),
    })
    @GetMapping("")
    public ResponseEntity<?> getAllCategory() {
        List<Category> listCategory = categoryService.getAllCategory();
        return ResponseEntity.ok(new BaseResponse<>(true, "Category fetched successfully", listCategory));
    }

    @Operation(summary = "Get List Catalog by Category", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Category not found\"}")))
    })
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

class BaseResponseCategoryList extends BaseResponse<List<Category>> {
}
