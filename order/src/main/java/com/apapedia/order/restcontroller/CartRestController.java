package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.CartItemMapper;
import com.apapedia.order.dto.CartMapper;
import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.DeleteCartItemRequestDTO;
import com.apapedia.order.dto.response.DeleteCartItemResponseDTO;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.restservice.CartRestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class CartRestController {

    CartRestService cartService;

    CartMapper cartMapper;

    CartItemMapper cartItemMapper;

    // POST : Create new cart
    @PostMapping("/create-cart")
    public ResponseEntity<Cart> createCart(@Valid @RequestBody CreateCartRequestDTO createCartRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getDefaultMessage();
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        Cart cart = cartMapper.createCartRequestDTOToCart(createCartRequestDTO);
        cart = cartService.saveCart(cart);

        return ResponseEntity.ok(cart);
    }

    // POST : Edit cart item
    @PostMapping("/update-cart-item")
    public ResponseEntity<Cart> addCartItem(@Valid @RequestBody CreateCartItemRequestDTO createCartItemRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        CartItem cartItem = cartItemMapper.createCartItemRequestDTOToCartItem(createCartItemRequestDTO);
        Cart cart = cartService.updateCartItem(createCartItemRequestDTO.getCartId(), cartItem);
        if (cart == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cart does not exist"
            );
        }
        return ResponseEntity.ok(cart);
    }

    // GET Cart by User ID
    @GetMapping("/get-cart/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable("userId") String userId) {
        Cart cart = cartService.getCartByUserId(UUID.fromString(userId));
        if (cart == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cart does not exist"
            );
        }
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<DeleteCartItemResponseDTO> deleteCartItem(@Valid @RequestBody DeleteCartItemRequestDTO deleteCartItemRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        UUID cartItemId = deleteCartItemRequestDTO.getCartItemId();
        UUID cartId = deleteCartItemRequestDTO.getCartId();
        try {
            cartService.deleteCartItem(cartId, cartItemId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }

        String message = "Cart item with ID " + cartItemId + " has been successfully deleted";
        return ResponseEntity.ok(new DeleteCartItemResponseDTO(message));
    }
}
