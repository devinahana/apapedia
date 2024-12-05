package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.DeleteCartItemRequestDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.dto.response.BaseResponse;
import com.apapedia.order.model.Cart;
import com.apapedia.order.restservice.CartRestService;
import com.apapedia.order.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.security.core.AuthenticationException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/cart")
public class CartRestController {

    CartRestService cartService;

    ResponseUtils responseUtils;

    @Operation(summary = "Create Cart", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the CUSTOMER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCart.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"User ID cannot be empty\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to create cart for this user\"}"))),
    })
    @PostMapping("")
    public ResponseEntity<?> createCart(
            @Valid @RequestBody CreateCartRequestDTO createCartRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, responseUtils.getBindingErrorMessage(bindingResult)));
            }

            if (!createCartRequestDTO.getUserId().equals(UUID.fromString(tokenUserId))) {
                return responseUtils.getForbiddenResponse("create cart for this user");
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

    @Operation(summary = "Add Cart Item", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the CUSTOMER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCart.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Quantity cannot be less than 1\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to add cart item for this user\"}"))),
    })
    @PostMapping("/add-item")
    public ResponseEntity<?> addCartItem(@Valid @RequestBody CreateCartItemRequestDTO createCartItemRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            Cart cart = cartService.addCartItem(createCartItemRequestDTO, tokenUserId);

            return ResponseEntity.ok(new BaseResponse<>(true, "Cart item created successfully", cart));
        } catch (AuthenticationException e) {
            return responseUtils.getForbiddenResponse("add cart item for this user");
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

    @Operation(summary = "Update Cart Item Quantity", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the CUSTOMER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCart.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Quantity cannot be less than 1\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to update cart item for this user\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cart item not found\"}")))
    })
    @PutMapping("/update-item")
    public ResponseEntity<?> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            Cart cart = cartService.updateCardItem(updateCartItemRequestDTO, tokenUserId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Cart item updated successfully", cart));
        } catch (AuthenticationException e) {
            return responseUtils.getForbiddenResponse("update cart item for this user");
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
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

    @Operation(summary = "Get Cart by User ID", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the CUSTOMER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCart.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid customer ID format. It should be a valid UUID.\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to fetch cart for this user\"}"))),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(
            @PathVariable String userId,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID userIdUUID = UUID.fromString(userId);
            if (!userId.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("fetch cart for this user");
            }

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

    @Operation(summary = "Delete Cart Item", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication. Token is accepted only from the CUSTOMER role.", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseCart.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cart ID cannot be empty\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to delete cart item for this user\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cart item not found\"}")))
    })
    @DeleteMapping("/delete-item")
    public ResponseEntity<?> deleteCartItem(
            @Valid @RequestBody DeleteCartItemRequestDTO deleteCartItemRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, responseUtils.getBindingErrorMessage(bindingResult)));
            }

            Cart cart = cartService.deleteCartItem(deleteCartItemRequestDTO, tokenUserId);
            String message = "Cart item with ID " + deleteCartItemRequestDTO.getCartItemId()
                    + " has been successfully deleted";
            return ResponseEntity.ok(new BaseResponse<>(true, message, cart));
        } catch (AuthenticationException e) {
            return responseUtils.getForbiddenResponse("delete cart item for this user");
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed deleting cart item : " + e.getMessage()));
        }
    }
}

class BaseResponseCart extends BaseResponse<Cart> {
}