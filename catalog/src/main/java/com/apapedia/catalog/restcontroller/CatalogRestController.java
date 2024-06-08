package com.apapedia.catalog.restcontroller;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.Response.MessageResponseDTO;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restservice.CatalogRestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class CatalogRestController {
    CatalogRestService catalogService;

    CatalogMapper catalogMapper;

    @PostMapping("/create-catalog")
    public ResponseEntity<Catalog> createCatalog(
            @RequestParam("seller") UUID seller,
            @RequestParam("price") BigInteger price,
            @RequestParam("productName") String productName,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("stock") int stock,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("imageFile") MultipartFile imageFile) {

        CreateCatalogRequestDTO catalogDTO = new CreateCatalogRequestDTO();
        catalogDTO.setSeller(seller);
        catalogDTO.setPrice(price);
        catalogDTO.setProductName(productName);
        catalogDTO.setProductDescription(productDescription);
        catalogDTO.setStock(stock);
        catalogDTO.setCategoryId(categoryId);
        catalogDTO.setImageFile(imageFile);


        try {
            Catalog catalog = catalogMapper.createCatalogRequestDTOToCatalog(catalogDTO);
            catalog = catalogService.setCatalogcategory(catalog, categoryId);
            return ResponseEntity.ok(catalog);
        } catch (NoSuchElementException ne) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ne.getMessage()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Please check your input!"
            );
        }
    }

    @GetMapping("/get-all-catalog")
    public ResponseEntity<List<Catalog>> getCatalogBySellerId(@RequestParam(name = "sellerId", required = false) String sellerId) {
        List<Catalog> listCatalog = new ArrayList<>();

        if (sellerId == null || sellerId.isBlank()) {
            listCatalog = catalogService.findAll();
        } else {
            listCatalog = catalogService.findBySeller(UUID.fromString(sellerId));
        }

        if (listCatalog.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "There is no catalog found"
            );
        }

        return ResponseEntity.ok(listCatalog);
    }

    @DeleteMapping("/delete-catalog/{catalogId}")
    public ResponseEntity<MessageResponseDTO> deleteCatalogById(@PathVariable("catalogId") String catalogId) {
        try {
            catalogService.deleteCatalog(UUID.fromString(catalogId));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Catalog with id " + catalogId + " not found"
            );
        }

        String message = "Catalog with ID " + catalogId + " has been succesfully deleted";
        return ResponseEntity.ok(new MessageResponseDTO(message));
    }
}
