package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.Response.BaseResponse;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restservice.CatalogRestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/catalog")
public class CatalogRestController {
    CatalogRestService catalogService;

    @PostMapping("")
    public ResponseEntity<?> createCatalog(
            @RequestParam UUID seller,
            @RequestParam String productName,
            @RequestParam BigDecimal price,
            @RequestParam String productDescription,
            @RequestParam Integer stock,
            @RequestParam Long categoryId,
            @RequestParam MultipartFile image
    ) {
        try {
            CreateCatalogRequestDTO catalogDTO = new CreateCatalogRequestDTO(
                    seller,
                    productName,
                    price,
                    productDescription,
                    stock,
                    categoryId,
                    image
            );

            Catalog catalog = catalogService.createCatalog(catalogDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(true, "Catalog created successfully", catalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Error uploading image : " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating catalog : " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCatalog(@RequestParam(required = false) String sellerId) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();

            if (sellerId == null || sellerId.isBlank()) {
                listCatalog = catalogService.findAll();
            } else {
                UUID sellerIdUUID = UUID.fromString(sellerId);
                listCatalog = catalogService.findBySeller(sellerIdUUID);
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog fetched successfully", listCatalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid seller ID format. It should be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog : " + e.getMessage()));
        }
    }

    @PutMapping("/{catalogId}")
    public ResponseEntity<?> updateCatalogDetail(
            @PathVariable String catalogId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productDescription,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            UUID catalogIdUUID = UUID.fromString(catalogId);
            Catalog catalog = catalogService.findById(catalogIdUUID);

            UpdateCatalogRequestDTO catalogDTO = new UpdateCatalogRequestDTO(
                    productName,
                    productDescription,
                    price,
                    stock,
                    categoryId,
                    image
            );
            Catalog updatedCatalog = catalogService.updateCatalog(catalog, catalogDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog detail updated successfully", updatedCatalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Error uploading image : " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed updating catalog detail : " + e.getMessage()));
        }
    }

    @GetMapping("/{catalogId}")
    public ResponseEntity<?> getCatalogDetail(@PathVariable String catalogId) {
        try {
            UUID catalogIdUUID = UUID.fromString(catalogId);
            Catalog catalog = catalogService.findById(catalogIdUUID);
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog detail fetched successfully", catalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid catalog ID format. It should be a valid UUID."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog detail : " + e.getMessage()));
        }
    }

    @DeleteMapping("/{catalogId}")
    public ResponseEntity<?> deleteCatalogById(@PathVariable String catalogId) {
        try {
            UUID catalogIdUUID = UUID.fromString(catalogId);
            catalogService.deleteCatalogById(catalogIdUUID);
            String message = "Catalog with ID " + catalogId + " has been succesfully deleted";
            return ResponseEntity.ok(new BaseResponse<>(true, message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid catalog ID format. It should be a valid UUID."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed deleting catalog : " + e.getMessage()));
        }
    }

    @GetMapping("/name")
    public ResponseEntity<?> getCatalogByName(@RequestParam String q) {
        try {
            List<Catalog> listCatalog = catalogService.findByName(q);
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog fetched successfully", listCatalog));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog : " + e.getMessage()));
        }
    }

    @GetMapping("/price")
    public ResponseEntity<?> getCatalogByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();
            if (minPrice == null && maxPrice == null) {
                listCatalog = catalogService.findAll();
            } else if (minPrice != null && maxPrice == null) {
                listCatalog = catalogService.findByPriceMin(minPrice);
            } else if (minPrice == null && maxPrice != null) {
                listCatalog = catalogService.findByPriceMax(maxPrice);
            } else {
                if (minPrice.compareTo(maxPrice) > 0) {
                    throw new IllegalArgumentException("Min price must be less than max price");
                } else {
                    listCatalog = catalogService.findByPriceRange(minPrice, maxPrice);
                }
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog fetched successfully", listCatalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog : " + e.getMessage()));
        }
    }

    @GetMapping("/sort-price")
    public ResponseEntity<?> getSortedPriceCatalog(@RequestParam(required = false) Boolean isAscending) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();
            if (isAscending == null  || isAscending) {
                listCatalog = catalogService.sortByPrice(true);
            } else {
                listCatalog = catalogService.sortByPrice(false);
            }
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog fetched successfully", listCatalog));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog : " + e.getMessage()));
        }
    }

    @GetMapping("/sort-name")
    public ResponseEntity<?> getSortedNameCatalog(@RequestParam(required = false) Boolean isAscending) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();
            if (isAscending == null  || isAscending) {
                listCatalog = catalogService.sortByName(true);
            } else {
                listCatalog = catalogService.sortByName(false);
            }
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog fetched successfully", listCatalog));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching catalog : " + e.getMessage()));
        }
    }
}
