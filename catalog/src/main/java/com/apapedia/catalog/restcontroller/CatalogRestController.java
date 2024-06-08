package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.Response.BaseResponse;
import com.apapedia.catalog.dto.Response.MessageResponseDTO;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restservice.CatalogRestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/catalog/")
public class CatalogRestController {
    CatalogRestService catalogService;

    @PostMapping("")
    public ResponseEntity<?> createCatalog(
            @RequestParam UUID seller,
            @RequestParam BigDecimal price,
            @RequestParam String productName,
            @RequestParam String productDescription,
            @RequestParam int stock,
            @RequestParam UUID categoryId,
            @RequestParam MultipartFile imageFile
    ) {
        try {
            CreateCatalogRequestDTO catalogDTO = new CreateCatalogRequestDTO(
                    seller,
                    productName,
                    price,
                    productDescription,
                    stock,
                    categoryId,
                    imageFile
            );

            Catalog catalog = catalogService.createCatalog(catalogDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(true, "Catalog created successfully", catalog));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating catalog : " + e.getMessage()));
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> getAllCatalog(@RequestParam(name = "sellerId", required = false) String sellerId) {
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

    @PutMapping("{catalogId}")
    public ResponseEntity<?> updateCatalogDetail(
            @PathVariable("catalogId") String catalogId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productDescription,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) int stock,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) MultipartFile imageFile
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
                    imageFile
            );
            Catalog updatedCatalog = catalogService.updateCatalog(catalog, catalogDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "Catalog detail updated successfully", updatedCatalog));
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
                    .body(new BaseResponse<>(false, "Failed updating catalog detail : " + e.getMessage()));
        }
    }

    @GetMapping("{catalogId}")
    public ResponseEntity<?> getCatalogDetail(@PathVariable("catalogId") String catalogId) {
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

    @DeleteMapping("{catalogId}")
    public ResponseEntity<?> deleteCatalogById(@PathVariable("catalogId") String catalogId) {
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
}
