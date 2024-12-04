package com.apapedia.order.restservice;

import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.DeleteCartItemRequestDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.model.Cart;
import java.util.UUID;

public interface CartRestService {
    Cart createCart(CreateCartRequestDTO createCartRequestDTO);

    Cart addCartItem(CreateCartItemRequestDTO createCartItemRequestDTO, String tokenUserId);

    Cart updateCardItem(UpdateCartItemRequestDTO updateCartItemRequestDTO, String tokenUserId);

    Cart getCartByUserId(UUID userId);

    Cart deleteCartItem(DeleteCartItemRequestDTO deleteCartItemRequestDTO, String tokenUserId);
}
