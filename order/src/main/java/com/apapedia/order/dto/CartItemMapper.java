package com.apapedia.order.dto;

import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem createCartItemRequestDTOToCartItem(CreateCartItemRequestDTO createCartItemRequestDTO);

}
