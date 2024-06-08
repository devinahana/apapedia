package com.apapedia.catalog.dto;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    Catalog createCatalogRequestDTOToCatalog(CreateCatalogRequestDTO createCatalogRequestDTO);

    @AfterMapping
    default void setImage(@MappingTarget Catalog catalog, CreateCatalogRequestDTO createCatalogRequestDTO) {
        try {
            catalog.setImage(createCatalogRequestDTO.getImageFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
