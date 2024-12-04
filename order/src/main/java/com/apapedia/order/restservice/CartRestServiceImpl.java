package com.apapedia.order.restservice;

import com.apapedia.order.dto.CartItemMapper;
import com.apapedia.order.dto.CartMapper;
import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.DeleteCartItemRequestDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;

import org.springframework.security.core.AuthenticationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartRestServiceImpl implements CartRestService {

    CartDb cartDb;
    CartItemDb cartItemDb;
    CartMapper cartMapper;
    CartItemMapper cartItemMapper;

    private Cart saveCart(Cart cart) {
        return cartDb.save(cart);
    }

    private CartItem saveCartItem(CartItem cartItem) {
        return cartItemDb.save(cartItem);
    }

    private Cart findCartById(UUID cartId) {
        return cartDb.findById(cartId).orElse(null);
    }

    private CartItem findCartItemById(UUID cartItemId) {
        return cartItemDb.findById(cartItemId).orElse(null);
    }
    private void deleteCartItem(CartItem cartItem) {
        cartItemDb.delete(cartItem);
    }

    @Override
    public Cart createCart(CreateCartRequestDTO createCartRequestDTO) {
        Cart cart = cartMapper.createCartRequestDTOToCart(createCartRequestDTO);
        return this.saveCart(cart);
    }

    @Override
    public Cart addCartItem(CreateCartItemRequestDTO createCartItemRequestDTO, String tokenUserId) {
        UUID cartId = createCartItemRequestDTO.getCartId();
        Cart cart = this.findCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart with ID " + cartId + " not found.");
        } else if (!cart.getUserId().toString().equals(tokenUserId)){
            throw new AuthenticationException("User ID does not match") {};
        }

        CartItem cartItem = cartItemMapper.createCartItemRequestDTOToCartItem(createCartItemRequestDTO);
        BigDecimal totalPriceItem = cartItem.getProductPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity())
        );

        cartItem.setCart(cart);
        this.saveCartItem(cartItem);

        cart.getListCartItem().add(cartItem);
        cart.setTotalPrice(
                cart.getTotalPrice().add(
                        totalPriceItem
                ));
        return this.saveCart(cart);
    }

    @Override
    public Cart updateCardItem(UpdateCartItemRequestDTO updateCartItemRequestDTO, String tokenUserId) {
        UUID cartId = updateCartItemRequestDTO.getCartId();
        Cart cart = this.findCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart with ID " + cartId + " not found.");
        } else if (!cart.getUserId().toString().equals(tokenUserId)){
            throw new AuthenticationException("User ID does not match") {};
        }

        UUID cartItemId = updateCartItemRequestDTO.getCartItemId();
        CartItem cartItem = this.findCartItemById(cartItemId);
        if (cartItem == null || ! cart.getListCartItem().contains(cartItem)) {
            throw new IllegalArgumentException("Cart Item with ID " + cartItemId + " not found.");
        }

        BigDecimal totalPriceItem = cartItem.getProductPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity())
        );

        BigDecimal updatedTotalPriceItem = cartItem.getProductPrice().multiply(
                BigDecimal.valueOf(updateCartItemRequestDTO.getQuantity())
        );

        cartItem.setQuantity(updateCartItemRequestDTO.getQuantity());
        this.saveCartItem(cartItem);

        cart.setTotalPrice(
                cart.getTotalPrice().subtract(
                        totalPriceItem
                ).add(
                        updatedTotalPriceItem
                )
        );
        return this.saveCart(cart);
    }

    @Override
    public Cart getCartByUserId(UUID userId) {
        return cartDb.findByUserId(userId);
    }

    @Override
    public Cart deleteCartItem(DeleteCartItemRequestDTO deleteCartItemRequestDTO, String tokenUserId) {
        UUID cartId = deleteCartItemRequestDTO.getCartId();
        Cart cart = this.findCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart with ID " + cartId + " not found.");
        } else if (!cart.getUserId().toString().equals(tokenUserId)){
            throw new AuthenticationException("User ID does not match") {};
        }

        UUID cartItemId = deleteCartItemRequestDTO.getCartItemId();
        CartItem cartItem = this.findCartItemById(cartItemId);
        if (cartItem == null || ! cart.getListCartItem().contains(cartItem)) {
            throw new IllegalArgumentException("Cart Item with ID " + cartItemId + " not found.");
        }

        BigDecimal totalPriceDeletedItem = cartItem.getProductPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity())
        );

        cart.getListCartItem().remove(cartItem);
        this.deleteCartItem(cartItem);
        cart.setTotalPrice(cart.getTotalPrice().subtract(totalPriceDeletedItem));
        return this.saveCart(cart);
    }

}

