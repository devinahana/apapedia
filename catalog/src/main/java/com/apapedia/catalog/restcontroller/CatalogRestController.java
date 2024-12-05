package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.response.BaseResponse;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restservice.CatalogRestService;

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

    @Operation(summary = "Create Catalog", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the SELLER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalog.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Error uploading image\"}")))
    })
    @PostMapping("")
    public ResponseEntity<?> createCatalog(
            @RequestParam String productName,
            @RequestParam BigDecimal price,
            @RequestParam String productDescription,
            @RequestParam Integer stock,
            @RequestParam Long categoryId,
            @RequestParam MultipartFile image,
            @RequestAttribute("userId") String seller) {
        try {
            CreateCatalogRequestDTO catalogDTO = new CreateCatalogRequestDTO(
                    UUID.fromString(seller),
                    productName,
                    price,
                    productDescription,
                    stock,
                    categoryId,
                    image);

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

    @Operation(summary = "Get All Catalog", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid seller ID format. It should be a valid UUID.\"}")))
    })
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

    @Operation(summary = "Update Catalog", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the SELLER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalog.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Stock cannot be less than 0\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to update this catalog\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Catalog not found\"}")))
    })
    @PutMapping("/{catalogId}")
    public ResponseEntity<?> updateCatalogDetail(
            @PathVariable String catalogId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productDescription,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile image,
            @RequestAttribute("userId") String seller) {
        try {
            UUID catalogIdUUID = UUID.fromString(catalogId);
            Catalog catalog = catalogService.findById(catalogIdUUID);
            if (!catalog.getSeller().equals(UUID.fromString(seller))) {
                return getForbiddenResponse("update this catalog");
            }

            UpdateCatalogRequestDTO catalogDTO = new UpdateCatalogRequestDTO(
                    productName,
                    productDescription,
                    price,
                    stock,
                    categoryId,
                    image);
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

    @Operation(summary = "Get Catalog by ID", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalog.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid catalog ID format. It should be a valid UUID.\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Catalog not found\"}")))
    })
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

    @Operation(summary = "Delete Catalog by ID", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the SELLER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalog.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid catalog ID format. It should be a valid UUID.\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Catalog not found\"}")))
    })
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

    @Operation(summary = "Get Catalog by Name", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class)))
    })
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

    @Operation(summary = "Get Catalog by Price Range", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class)))
    })
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

    @Operation(summary = "Get Catalog Sort by Price", description = "Default sort ASC", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class)))
    })
    @GetMapping("/sort-price")
    public ResponseEntity<?> getSortedPriceCatalog(@RequestParam(required = false) Boolean isAscending) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();
            if (isAscending == null || isAscending) {
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

    @Operation(summary = "Get Catalog Sort by Name", description = "Default sort ASC", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCatalogList.class)))
    })
    @GetMapping("/sort-name")
    public ResponseEntity<?> getSortedNameCatalog(@RequestParam(required = false) Boolean isAscending) {
        try {
            List<Catalog> listCatalog = new ArrayList<>();
            if (isAscending == null || isAscending) {
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

    private ResponseEntity<?> getForbiddenResponse(String action) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse<>(false, "You are not allowed to " + action));
    }
}

class BaseResponseCatalog extends BaseResponse<Catalog> {
}

class BaseResponseCatalogList extends BaseResponse<List<Catalog>> {
}
