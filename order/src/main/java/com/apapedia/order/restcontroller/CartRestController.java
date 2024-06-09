package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.DeleteCartItemRequestDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.dto.response.BaseResponse;
import com.apapedia.order.model.Cart;
import com.apapedia.order.restservice.CartRestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/cart")
public class CartRestController {

    CartRestService cartService;

    // POST : Create new cart
    @PostMapping("")
    public ResponseEntity<?> createCart(@Valid @RequestBody CreateCartRequestDTO createCartRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, getBindingErrorMessage(bindingResult)));
            }

            Cart cart = cartService.createCart(createCartRequestDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(true, "Cart created successfully", cart));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating cart : " + e.getMessage()));
        }
    }

    // POST : Add cart item
    @PostMapping("/add-item")
    public ResponseEntity<?> addCartItem(@Valid @RequestBody CreateCartItemRequestDTO createCartItemRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            Cart cart = cartService.addCartItem(createCartItemRequestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "Cart item created successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating cart item : " + e.getMessage()));
        }
    }

    // PUT : Update cart item quantity
    @PutMapping("/update-item")
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            Cart cart = cartService.updateCardItem(updateCartItemRequestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "Cart item updated successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed updating cart item : " + e.getMessage()));
        }
    }

    // GET Cart by User ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable String userId) {
        try {
            UUID userIdUUID = UUID.fromString(userId);
            Cart cart = cartService.getCartByUserId(userIdUUID);
            if (cart == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse<>(false, "Cart does not exist"));
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "Cart fetched successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid customer ID format. It should be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching cart : " + e.getMessage()));
        }
    }

    // DELETE :  Delete Cart Item
    @DeleteMapping("/delete-item")
    public ResponseEntity<?> deleteCartItem(@Valid @RequestBody DeleteCartItemRequestDTO deleteCartItemRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, getBindingErrorMessage(bindingResult)));
            }

            Cart cart = cartService.deleteCartItem(deleteCartItemRequestDTO);
            String message = "Cart item with ID " + deleteCartItemRequestDTO.getCartItemId() + " has been successfully deleted";
            return ResponseEntity.ok(new BaseResponse<>(true, message, cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        }
    }

    private String getBindingErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessages = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors ) {
            errorMessages
                    .append(error.getDefaultMessage())
                    .append("\n");
        }
        return errorMessages.toString();
    }
}
