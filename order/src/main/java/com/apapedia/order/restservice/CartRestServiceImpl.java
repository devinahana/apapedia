package com.apapedia.order.restservice;

import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartRestServiceImpl implements CartRestService {

    CartDb cartDb;

    CartItemDb cartItemDb;

    @Override
    public Cart saveCart(Cart cart) {
        return cartDb.save(cart);
    }


    @Override
    public Cart findCartById(UUID cartId) {
        return cartDb.findById(cartId).orElse(null);
    }

    @Override
    public CartItem findCartItemById(UUID cartItemId) {
        return cartItemDb.findById(cartItemId).orElse(null);
    }

    @Override
    public Cart updateCartItem(UUID cartId, CartItem cartItem) {
        Cart cart = this.findCartById(cartId);
        if (cart != null) {
            BigInteger totalPriceItem = cartItem.getProductPrice().multiply(
                    BigInteger.valueOf(cartItem.getQuantity())
            );

            // Cart item already exists before
            for (CartItem existingCartItem : cart.getListCartItem()) {
                if (existingCartItem.getProductId().equals(cartItem.getProductId())) {
                    BigInteger totalPriceExistingItem = existingCartItem.getProductPrice().multiply(
                            BigInteger.valueOf(existingCartItem.getQuantity())
                    );
                    existingCartItem.setQuantity(cartItem.getQuantity());
                    cart.setTotalPrice(
                            cart.getTotalPrice().subtract(totalPriceExistingItem).add(totalPriceItem)
                    );
                    cartItemDb.save(existingCartItem);
                    cart = this.saveCart(cart);
                    return cart;
                }
            }

            // Cart item have not existed before
            cartItem.setCart(cart);
            cartItemDb.save(cartItem);
            cart.getListCartItem().add(cartItem);
            cart.setTotalPrice(
                    cart.getTotalPrice().add(
                            totalPriceItem
                    ));
            cart = this.saveCart(cart);
        }
        return cart;
    }

    @Override
    public Cart getCartByUserId(UUID userId) {
        return cartDb.findByUserId(userId);
    }

    @Override
    public void deleteCartItem(UUID cartId, UUID cartItemId) {
        CartItem cartItem = this.findCartItemById(cartItemId);
        Cart cart = this.findCartById(cartId);
        if (cart == null) {
            throw new NoSuchElementException("Cart with ID " + cartId + " not found.");
        }
        if (cartItem == null || ! cart.getListCartItem().contains(cartItem)) {
            throw new NoSuchElementException("Cart Item with ID " + cartItemId + " not found.");
        }

        BigInteger totalPriceDeletedItem = cartItem.getProductPrice().multiply(
                BigInteger.valueOf(cartItem.getQuantity())
        );
        cartItemDb.deleteById(cartItemId);
        cart.setTotalPrice(cart.getTotalPrice().subtract(totalPriceDeletedItem));
        this.saveCart(cart);
    }

}

