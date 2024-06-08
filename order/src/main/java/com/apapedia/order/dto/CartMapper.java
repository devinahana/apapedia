package com.apapedia.order.dto;

import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart createCartRequestDTOToCart(CreateCartRequestDTO createCartRequestDTO);

}
