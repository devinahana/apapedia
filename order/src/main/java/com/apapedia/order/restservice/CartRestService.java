package com.apapedia.order.restservice;

import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;

import java.util.UUID;

public interface CartRestService {
    Cart saveCart(Cart cart);


    Cart findCartById(UUID cartId);
    CartItem findCartItemById(UUID cartItemId);

    Cart updateCartItem(UUID cartId, CartItem cartItem);

    Cart getCartByUserId(UUID userId);

    void deleteCartItem(UUID cartId, UUID cartItemId);
}
