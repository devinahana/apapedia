package com.apapedia.order.dto;

import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart createCartRequestDTOToCart(CreateCartRequestDTO createCartRequestDTO);

    @AfterMapping
    default void setListCartItem(@MappingTarget Cart cart, CreateCartRequestDTO createCartRequestDTO) {
        cart.setListCartItem(new ArrayList<CartItem>());
    }
}
